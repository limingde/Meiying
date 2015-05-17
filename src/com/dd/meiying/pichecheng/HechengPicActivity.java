package com.dd.meiying.pichecheng;

import java.util.ArrayList;
import java.util.List;

import com.dd.meiying.MainActivity;
import com.dd.meiying.R;
import com.dd.meiying.abul.view.ImageAlbumGridActivity;
import com.dd.meiying.adapter.HechengImgIndexAdapter;
import com.dd.meiying.bean.HechengData;
import com.dd.meiying.bean.HechengIndexData;
import com.dd.meiying.constent.IntentExtra;
import com.dd.meiying.pichecheng.PicHeChengQi.PicHechengQiCallBack;
import com.dd.meiying.util.ScreenShot;
import com.dd.meiying.util.WatermarkUtil;
import com.dd.meiying.wiget.HorizontalListView;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.TextureView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class HechengPicActivity extends Activity{
	public static Intent getStartActIntent(Activity from){
		Intent in = new Intent(from,HechengPicActivity.class);
		return in;
	}
	private OnClickListener mOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.left_iv:
				finish();
				break;
			case R.id.right_btn:
				int headerHeight = titleView.getHeight();
				String path = mPicHeChengQi.saveImage(headerHeight);
				Intent in = new Intent(IntentExtra.EXTRA_ACTION_HECHENGTUPIAN_SUCCESS);
				in.putExtra(IntentExtra.EXTRA_DATA, path);
				sendBroadcast(in);
				finish();
				break;
			}
		}

	};

	private PicHechengQiCallBack mPicHechengQiCallBack = new PicHechengQiCallBack() {

		@Override
		public void onclickItem(HechengData data) {
			mHechengData = data;
			if(data != null ){
				if(data.curType == PicHeChengQi.TYPE_IMG){
					startActivityForResult(ImageAlbumGridActivity.getStartActIntent(HechengPicActivity.this,true), 
							ImageAlbumGridActivity.REQUEST_CODE_SELECT_IMAGE_FROM_ImageAlbumGridActivity);
				} else {

				}
			}
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hecheng_pic);
		mPicHeChengQi = new PicHeChengQi(this,mPicHechengQiCallBack);
		mHechengData = new HechengData();
		initData();
		mHechengImgIndexAdapter = new HechengImgIndexAdapter(this, mdata);
		findView();
		initPicParrent();
	}

	private PicHeChengQi mPicHeChengQi;
	private HechengData mHechengData;
	private LinearLayout m_llPicParrent;
	private HorizontalListView m_hlvType;
	private HechengImgIndexAdapter mHechengImgIndexAdapter;
	private List<HechengIndexData> mdata;
	public void findView(){
		initHeader();

		m_llPicParrent = (LinearLayout) findViewById(R.id.pic_parrent);

		m_hlvType  = (HorizontalListView) findViewById(R.id.lv_type);
		m_hlvType.setAdapter(mHechengImgIndexAdapter);
		m_hlvType.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int p,
					long arg3) {
				if(p >= 0 && p < mdata.size()){
					HechengIndexData item = mdata.get(p);
					mHechengData = new HechengData();
					mHechengData.type = item.type;
					initPicParrent();
				}
			}
		});
		mHechengImgIndexAdapter.notifyDataSetChanged();
	}

	public void initPicParrent(){
		m_llPicParrent.removeAllViews();
		m_llPicParrent.addView(mPicHeChengQi.GetView(mHechengData));
	}
	private View titleView;
	public void initHeader(){
		titleView = findViewById(R.id.header);
		ImageView left = (ImageView) titleView.findViewById(R.id.left_iv);
		left.setVisibility(View.VISIBLE);	
		left.setOnClickListener(mOnClickListener);
		Button rightBtn = (Button) titleView.findViewById(R.id.right_btn);
		rightBtn.setVisibility(View.VISIBLE);
		rightBtn.setText("完成");
		rightBtn.setOnClickListener(mOnClickListener);
		TextView title = (TextView)titleView.findViewById(R.id.title_tv);
		title.setText("魅影图集");
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_CANCELED) {
			return;
		}
		if (requestCode == ImageAlbumGridActivity.REQUEST_CODE_SELECT_IMAGE_FROM_ImageAlbumGridActivity) {	
			if(data != null){
				String path = data.getStringExtra(IntentExtra.EXTRA_DATA);
				if(mHechengData != null && !TextUtils.isEmpty(path)){
					switch (mHechengData.curPosition) {
					case 1:
						mHechengData.img1 = path;
						break;
					case 2:
						mHechengData.img2 = path;
						break;
					case 3:
						mHechengData.img3 = path;
						break;
					case 4:
						mHechengData.img4 = path;
						break;

					default:
						break;
					}
					initPicParrent();
				}
			}
		}

	}
	/**
	 * 保存编辑完成的图片，路径�? meila/photo/ "
	 */
	private void saveImgWithTag() {
		// 获取状�?栏高�?
		Rect frame = new Rect();
		getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
		int statusBarHeight = frame.top;

		// 获取状�?栏与图片之间控件的高�?
		int headerHeight = m_llPicParrent.getHeight();

		// 获取图片的宽�?
		int picWidth = m_llPicParrent.getWidth();
		int picHeight = m_llPicParrent.getHeight();
		Bitmap mBitmap = ScreenShot.takeScreenShot(this, 0, (statusBarHeight + headerHeight), picWidth, picHeight);
		// 保存图片（不带水印）
		ScreenShot.savePic(mBitmap);
		// 保存图片（带水印�?
		WatermarkUtil.addWatermark(this, mBitmap, R.drawable.topic_photo_save_icon,WatermarkUtil.TYPE_WATERMARK);
	}

	public void initData(){
		mdata = new ArrayList<HechengIndexData>();
		Resources res = getResources();
		 Bitmap bmp_1_1 = BitmapFactory.decodeResource(res, R.drawable.img_1_1);
		 Bitmap bmp_1_2 = BitmapFactory.decodeResource(res, R.drawable.img_1_2);
		 Bitmap bmp_2_1 = BitmapFactory.decodeResource(res, R.drawable.img_2_1);
		HechengIndexData item = new HechengIndexData();
		item.type = HechengData.TYPE_L1_R1;
		item.img1 = bmp_1_2;
		item.img2 = bmp_1_2;
		item.img3 = bmp_1_2;
		item.img4 = bmp_1_2;
		mdata.add(item);
		
		item = new HechengIndexData();
		item.type = HechengData.TYPE_L1_R2;
		item.img1 = bmp_1_2;
		item.img2 = bmp_1_2;
		item.img3 = bmp_1_1;
		item.img4 = bmp_1_1;
		mdata.add(item);
		
		item = new HechengIndexData();
		item.type = HechengData.TYPE_L2_R1;
		item.img1 = bmp_1_1;
		item.img2 = bmp_1_1;
		item.img3 = bmp_1_2;
		item.img4 = bmp_1_2;
		mdata.add(item);
		
		item = new HechengIndexData();
		item.type = HechengData.TYPE_L2_R2;
		item.img1 = bmp_1_1;
		item.img2 = bmp_1_1;
		item.img3 = bmp_1_1;
		item.img4 = bmp_1_1;
		mdata.add(item);
		
		item = new HechengIndexData();
		item.type = HechengData.TYPE_U1_B1;
		item.img1 = bmp_2_1;
		item.img2 = bmp_2_1;
		item.img3 = bmp_2_1;
		item.img4 = bmp_2_1;
		mdata.add(item);
		
		item = new HechengIndexData();
		item.type = HechengData.TYPE_U1_B2;
		item.img1 = bmp_2_1;
		item.img2 = bmp_1_1;
		item.img3 = bmp_2_1;
		item.img4 = bmp_1_1;
		mdata.add(item);
		
		item = new HechengIndexData();
		item.type = HechengData.TYPE_U2_B1;
		item.img1 = bmp_1_1;
		item.img2 = bmp_2_1;
		item.img3 = bmp_1_1;
		item.img4 = bmp_2_1;
		mdata.add(item);
		
		item = new HechengIndexData();
		item.type = HechengData.TYPE_U2_B2;
		item.img1 = bmp_1_1;
		item.img2 = bmp_1_1;
		item.img3 = bmp_1_1;
		item.img4 = bmp_1_1;
		mdata.add(item);
	}
}
