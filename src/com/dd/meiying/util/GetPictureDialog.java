package com.dd.meiying.util;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import com.dd.meiying.abul.AlbumHelper;
import com.dd.meiying.abul.view.ImageBucketActivity;
import com.dd.meiying.abul.view.ImageBucketGridActivity;
import com.dd.meiying.constent.IntentExtra;

/**
 * 选择图片的对话框，可以让用户选择从相册找还是拍照。
 * 用法
 * 1，new GetPictureDialog
 * 2，调用getPicture
 * 3，重写onActivityResult，在onActivityResult里面调用parseActivityResult，得到图片的本地路径
 * @author Administrator
 *
 */
public class GetPictureDialog {
	private static final String TAG = "GetPictureDialog";
	Activity mAct;
	public boolean chooseMore = true;
	public int maxImageNum = ImageBucketGridActivity.MAX_MAX_IMG_NUM;
	public int imgChoosedNum = 0;

	public GetPictureDialog(Activity act) {
		mAct = act;
	}

	public void getPicture() {
		try{
		AlertDialog.Builder ad = new AlertDialog.Builder(mAct);
		ad.setTitle("提示");
		ad.setPositiveButton("从相册选择", onSelectBeforeClickListener());
		ad.setNegativeButton("拍照", onCaptureBeforeClickListener());
		ad.show();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void getPictureFromGallery() {
//		if(chooseMore){
//			Intent intent = new Intent(mAct, ImageBucketActivity.class);
//			intent.putExtra(IntentExtra.EXTRA_IMAGE_MAX_NUM, maxImageNum);
//			intent.putExtra(IntentExtra.EXTRA_IMAGE_CHOOSED_NUM, imgChoosedNum);
//			mAct.startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGES);
//		}else{
//			Intent getAlbum = new Intent(Intent.ACTION_GET_CONTENT);
//			getAlbum.setType("image/*");
//			mAct.startActivityForResult(getAlbum, REQUEST_CODE_SELECT_IMAGE);
//		}
		if(chooseMore){
		}else{
			maxImageNum = 1;
		}
		Intent intent = new Intent(mAct, ImageBucketActivity.class);
		intent.putExtra(IntentExtra.EXTRA_IMAGE_MAX_NUM, maxImageNum);
		intent.putExtra(IntentExtra.EXTRA_IMAGE_CHOOSED_NUM, imgChoosedNum);
		mAct.startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGES);
		
		addImgPath = null;
	}
	public String getPictureFromCamera() {
		addImgPath = sendCapture(REQUEST_CODE_CAPTURE_IMAGE);
		return addImgPath;
	}
	
	/**
	 * 解析onActivityResult的结果，返回图片的本地路径
	 * @param requestCode
	 * @param resultCode
	 * @param data
	 * @return
	 */
	public List<String> parseActivityResult(int requestCode, int resultCode, Intent data){
		if(resultCode == Activity.RESULT_CANCELED){
			return null;
		}
		
		if (requestCode == REQUEST_CODE_SELECT_IMAGE) {
			addImgPath = getImagePathFromMedia(data);
		} else if (requestCode == REQUEST_CODE_SELECT_IMAGES) {
			Object obj = data.getSerializableExtra(ImageBucketGridActivity.EXTRA_IMAGE_CHOOSE_LIST);
			if(obj != null){
				addImgPathList = (List<String>) obj;
				return addImgPathList;
			}
		} else if (requestCode == REQUEST_CODE_CAPTURE_IMAGE) {
			// 判断图片是不是旋转的
			BitmapUtils.checkImageOrientation(addImgPath);
			//刷新相册
			AlbumHelper.getHelper().refreshSystemAlbum(addImgPath);
		}
		
		List<String> rs = new ArrayList<String>();
		rs.add(addImgPath);
		return rs;
	}
	
	String getImagePathFromMedia(Intent data) {
		Cursor cursor = null;
		try {
			Uri selectedImage = data.getData();
			String[] filePathColumn = { MediaStore.Images.Media.DATA };

			cursor = mAct.getContentResolver().query(selectedImage,
					filePathColumn, null, null, null);
			cursor.moveToFirst();

			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			String picturePath = cursor.getString(columnIndex);
			return picturePath;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}
		return "";
	}

	/**
	 * 从相册选择
	 */
	public static final int REQUEST_CODE_SELECT_IMAGE = 1002;
	public static final int REQUEST_CODE_SELECT_IMAGES = 1004;//自定义相册，可多选
	/**
	 * 从照相机选择
	 */
	public static final int REQUEST_CODE_CAPTURE_IMAGE = 1003;

	private DialogInterface.OnClickListener onSelectBeforeClickListener() {
		return new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				getPictureFromGallery();
			}
		};
	}

	String addImgPath;
	List<String> addImgPathList;

	private DialogInterface.OnClickListener onCaptureBeforeClickListener() {
		return new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				getPictureFromCamera();
			}
		};
	}

	private String sendCapture(int requestCode) {
		String photoPath;
		Date now = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");// 可以方便地修改日期格式
		String strDate = dateFormat.format(now);
		Log.v("befterPhotoName", strDate);

		String fileName = strDate;
		photoPath = Environment.getExternalStorageDirectory() + "/"
				+ MeiyingResource.PHOTO_FOLDER + "/" + strDate + ".jpg";
		Log.v("PhotoPath", photoPath);
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		Log.v("PhotoPath", photoPath);
//		intent.putExtra(MediaStore.Images.Media.ORIENTATION, 180);
//		intent.putExtra(MediaStore.EXTRA_SCREEN_ORIENTATION, Configuration.ORIENTATION_LANDSCAPE);
//		intent.putExtra("return-data", true);
		intent.putExtra(
				MediaStore.EXTRA_OUTPUT,
				Uri.fromFile(new File(Environment.getExternalStorageDirectory()
						+ "/" + MeiyingResource.PHOTO_FOLDER + "/", fileName
						+ ".jpg")));
		mAct.startActivityForResult(intent, requestCode);
		return photoPath;
	}
}
