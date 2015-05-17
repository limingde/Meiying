package com.dd.meiying.abul.view;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dd.meiying.ImageCutterActivity;
import com.dd.meiying.ImageFilterActivity;
import com.dd.meiying.R;
import com.dd.meiying.abul.AlbumHelper;
import com.dd.meiying.abul.BitmapCache;
import com.dd.meiying.abul.BitmapCache.ImageCallback;
import com.dd.meiying.abul.ImageBucket;
import com.dd.meiying.abul.ImageItem;
import com.dd.meiying.adapter.ImageAlbumGridAdapter;
import com.dd.meiying.constent.IntentExtra;
import com.dd.meiying.util.GetPictureDialog;
import com.dd.meiying.util.MeiyingResource;
import com.dd.meiying.util.Utils;

public class ImageAlbumGridActivity extends Activity {
	public static final int REQUEST_CODE_SELECT_IMAGE_FROM_ImageAlbumGridActivity = 10001;
	public static final int RESULT_CODE_SELECT_IMAGE_FROM_ImageAlbumGridActivity = 2001;
	
	public static final int MSG_REFRESH_ALBUM_VIEW = 0;
	// ArrayList<Entity> mImageBucketList;//用来装载数据源的列表
	List<ImageBucket> mImageBucketList = new ArrayList<ImageBucket>();
	List<ImageItem> mImageList = new ArrayList<ImageItem>();
	List<ImageItem> mAdapterList = new ArrayList<ImageItem>();

	/** 一个空对象，放置与mAdapterList 的第一个位置，因为该位置显示的【选择照相】功能 */
	ImageItem mEmptyImageItem = new ImageItem();
	GridView gridView;
	ImageAlbumGridAdapter adapter;// 相片显示的grid适配器
	AlbumHelper helper;
	Activity act;
	String addCameraImgPath;
	TextView mHeaderTitle;
	private boolean mNeedToNext = false;

	GetPictureDialog getPictureDlg;

	private OrderAdapter orderAdapter;
	// private TextView mTitleTv;
	private View orderListLayout;
	private ListView orderListView;
	private Animation animOrderListSlideUpOut;
	private Animation animOrderListSlideDownIn;
	/** 相册选择title */
	private List<String> orderList = new ArrayList<String>();
	private int mSelectedOrder = 0;
	
	private BitmapCache cache;

	// int maxImageNum;
	// int imgChoosedNum;

	// int mCurAlbumSelectPos = 0;
	boolean mAlreadyQuerydate = false;

	BroadcastReceiver chooseImgReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			List<String> choosedList = (List<String>) intent
					.getSerializableExtra(ImageBucketGridActivity.EXTRA_IMAGE_CHOOSE_LIST);
			act.setResult(Activity.RESULT_OK, intent);
			finish();
		}
	};
	BroadcastReceiver mCloseActivity = new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			finish();;
		}
	};

	OnClickListener mClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.left_iv:
				finish();
				break;

			case R.id.title_tv:
			case R.id.title_select_layout:
				switchProductList();

			default:
				break;
			}
		}
	};
	
	Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MSG_REFRESH_ALBUM_VIEW:
				refreshAlbumData();
				break;

			default:
				break;
			}
		};
	};
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// MeilaLog.d(TAG, "onActivityResult, " + requestCode + ", " +
		// resultCode);

		if (resultCode == Activity.RESULT_CANCELED) {
			return;
		}

		if (requestCode == GetPictureDialog.REQUEST_CODE_CAPTURE_IMAGE) {
			List<String> pathList = getPictureDlg.parseActivityResult(requestCode, resultCode, data);

			/**
			 * TODO 跳转到图片剪裁界面
			 */
			if (pathList != null && pathList.size() > 0) {
				
				mHandler.sendEmptyMessage(MSG_REFRESH_ALBUM_VIEW);
				if(mNeedToNext){
					startActivityForResult(ImageCutterActivity.getStartActIntent(act, pathList.get(0),true), 
							ImageAlbumGridActivity.REQUEST_CODE_SELECT_IMAGE_FROM_ImageAlbumGridActivity);
				} else{
					startActivity(ImageCutterActivity.getStartActIntent(act, pathList.get(0),false));							
					
				}
			}


		}else if (requestCode == GetPictureDialog.REQUEST_CODE_SELECT_IMAGE
				|| requestCode == GetPictureDialog.REQUEST_CODE_SELECT_IMAGES) {

			List<String> pathList = getPictureDlg.parseActivityResult(
					requestCode, resultCode, data);

			// if (pathList != null && pathList.size() > 0) {
			// // for(String path:pathList){
			// // addCommentImg(path);
			// // }
			// addCommentImgPath(pathList, true);
			// } else {
			// MeilaLog.e(TAG, "not return image path");
			// }

		} else if(requestCode == ImageAlbumGridActivity.REQUEST_CODE_SELECT_IMAGE_FROM_ImageAlbumGridActivity){
			if (data != null) {
				setResult(ImageAlbumGridActivity.RESULT_CODE_SELECT_IMAGE_FROM_ImageAlbumGridActivity, data);				
			}	
			finish();
		}
	}

	private final static String EXTRA_Is_need_to_next = "is need to next";
	public static Intent getStartActIntent(Activity from,boolean needToNext){
		Intent intent = new Intent(from, ImageAlbumGridActivity.class);
		intent.putExtra(EXTRA_Is_need_to_next, needToNext);
		return intent;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_album_image_select_grid);
		act = this;
		helper = AlbumHelper.getHelper();
		helper.init(getApplicationContext());

		initView();
		initAnim();
		
		cache = new BitmapCache();
		mNeedToNext = getIntent().getBooleanExtra(EXTRA_Is_need_to_next, false);
		getPictureDlg = new GetPictureDialog(this);

		registerReceiver(chooseImgReceiver, new IntentFilter(
				ImageBucketGridActivity.ACTION_CHOOSED));
		registerReceiver(mCloseActivity, new IntentFilter(ImageFilterActivity.ACTION_CLOSE_ACTIVITY));
		
		mHandler.sendEmptyMessage(MSG_REFRESH_ALBUM_VIEW);
	}

	@Override
	public void onResume() {
		super.onResume();
	}
	
	private void refreshAlbumData() {
		
		mImageBucketList = helper.getImagesBucketList(true);
		mImageList = helper.getImagesList();

		if (mImageList != null) {
			mAdapterList.clear();
			mAdapterList.add(mEmptyImageItem);
			mAdapterList.addAll(mImageList);

		}
		mAlreadyQuerydate = true;

		orderList.clear();
		orderList.add(getResources().getString(R.string.all_album_picture));
		for (int i = 0; i < mImageBucketList.size(); i++) {
			orderList.add(mImageBucketList.get(i).bucketName);
		}
		
		adapter.notifyDataSetChanged();
	}

	public void switchProductList() {
		if (orderListLayout.getVisibility() == View.VISIBLE) {
			hideProductList();
		} else {
			showProductList();
		}
	}

	public void hideProductList() {
		orderListView.startAnimation(animOrderListSlideUpOut);
		Drawable d = getResources().getDrawable(R.drawable.arrow_down_ff);
		d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
		mHeaderTitle.setCompoundDrawables(null, null, d, null);
		mHeaderTitle.setCompoundDrawablePadding(Utils.dip2px(act, 6));
	}

	public void showProductList() {
		orderListLayout.setVisibility(View.VISIBLE);
		
		orderAdapter.notifyDataSetChanged();
		
		if (orderList != null && orderList.size() > 5) {
			int mScreenHeight = act.getWindowManager().getDefaultDisplay().getHeight();
			int mScreenWidth = act.getWindowManager().getDefaultDisplay().getWidth();
			RelativeLayout.LayoutParams picParams = new RelativeLayout.LayoutParams(mScreenWidth, (mScreenHeight / 3) * 2);
			picParams.addRule(RelativeLayout.BELOW, R.id.header);
			orderListView.setLayoutParams(picParams);
		}
		
		
		orderListView.setVisibility(View.VISIBLE);
	
		orderListView.startAnimation(animOrderListSlideDownIn);

		Drawable d = getResources().getDrawable(R.drawable.arrow_up);
		d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
		mHeaderTitle.setCompoundDrawables(null, null, d, null);
		mHeaderTitle.setCompoundDrawablePadding(Utils.dip2px(act, 6));
	}

	public void initAnim() {
		animOrderListSlideDownIn = AnimationUtils.loadAnimation(act, R.anim.slide_down_in_slow);
		animOrderListSlideUpOut = AnimationUtils.loadAnimation(act, R.anim.slide_up_out_slow);
		animOrderListSlideDownIn.setFillAfter(true);
		animOrderListSlideUpOut.setFillAfter(true);
		animOrderListSlideDownIn.setDuration(200);
		animOrderListSlideUpOut.setDuration(200);

		animOrderListSlideDownIn.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				// headerIv3.setImageResource(R.drawable.books_product_has);
			}
		});
		animOrderListSlideUpOut.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				// showProductListHeader();
				orderListView.clearAnimation();
				orderListView.setVisibility(View.GONE);
				orderListLayout.setVisibility(View.GONE);
				// headerIv3.setImageResource(R.drawable.books_product);
			}
		});
	}

	@Override
	protected void onDestroy() {
		unregisterReceiver(chooseImgReceiver);
		unregisterReceiver(mCloseActivity);
		super.onDestroy();
	}

	private void updateChildAlbumView(int pos) {

		mHeaderTitle.setText(orderList.get(mSelectedOrder));
		// getNailShowList(true);

		if (mSelectedOrder == 0) {
			/**
			 * 选择的全部相册
			 * */
			if (mImageList != null) {
				mAdapterList.clear();
				mAdapterList.add(mEmptyImageItem);
				mAdapterList.addAll(mImageList);

				adapter.notifyDataSetChanged();
			}
		} else {
			/**
			 * 选择的子相册
			 * */
			List<ImageItem> tmpList = mImageBucketList.get(mSelectedOrder - 1).imageList;
			if (tmpList != null) {
				mAdapterList.clear();
				mAdapterList.add(mEmptyImageItem);
				mAdapterList.addAll(tmpList);

				adapter.notifyDataSetChanged();
			}
		}

	}

	/**
	 * 初始化view视图
	 */
	private void initView() {

		// 标题栏
		View header = findViewById(R.id.header);
		View left = header.findViewById(R.id.left_iv);
		left.setOnClickListener(mClickListener);
		mHeaderTitle = (TextView) header.findViewById(R.id.title_tv);
		Drawable d = getResources().getDrawable(R.drawable.arrow_down_ff);
		d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
		mHeaderTitle.setCompoundDrawables(null, null, d, null);
		mHeaderTitle.setCompoundDrawablePadding(Utils.dip2px(act, 6));
		mHeaderTitle.setText(getResources().getString(R.string.all_album_picture));
		mHeaderTitle.setOnClickListener(mClickListener);
		View right = header.findViewById(R.id.right_btn);
		right.setVisibility(View.GONE);

		orderListLayout = (LinearLayout) findViewById(R.id.title_select_layout);
		orderListLayout.setOnClickListener(mClickListener);
		orderListLayout.setVisibility(View.GONE);
		orderListView = (ListView) findViewById(R.id.title_select_listview);
		orderAdapter = new OrderAdapter();
		orderListView.setAdapter(orderAdapter);
		orderListView.setVisibility(View.GONE);
		orderListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long id) {

				boolean changed = (mSelectedOrder != position);
				mSelectedOrder = position;
				hideProductList();

				if (!changed) {
					return;
				}
				updateChildAlbumView(position);
			}
		});

		gridView = (GridView) findViewById(R.id.gridview);
		adapter = new ImageAlbumGridAdapter(ImageAlbumGridActivity.this,
				mAdapterList);
		gridView.setAdapter(adapter);

		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (position == 0) {
					/** 拍摄的照片没有保存，需要解决 */
					addCameraImgPath = getPictureDlg.getPictureFromCamera();
				} else {
					/**
					 * TODO 跳转到图片剪裁界面
					 */
					String picPath = mAdapterList.get(position).imagePath;
					if (TextUtils.isEmpty(picPath)) {
						Utils.displayToast(ImageAlbumGridActivity.this,
								"你选择的图片有误");
					} else {
						if(mNeedToNext){
							startActivityForResult(ImageCutterActivity.getStartActIntent(act, picPath,true), 
									ImageAlbumGridActivity.REQUEST_CODE_SELECT_IMAGE_FROM_ImageAlbumGridActivity);
						} else{
							startActivity(ImageCutterActivity.getStartActIntent(act, picPath,false));							
							
						}
					}
					// Intent intent = new Intent(ImageAlbumGridActivity.this,
					// ImageBucketGridActivity.class);
					// intent.putExtra(ImageBucketGridActivity.EXTRA_IMAGE_LIST,
					// (Serializable)mImageBucketList.get(position).imageList);
					// intent.putExtra(ImageBucketGridActivity.EXTRA_BUCKET_NAME,
					// mImageBucketList.get(position).bucketName);
					// intent.putExtra(IntentExtra.EXTRA_IMAGE_MAX_NUM,
					// maxImageNum);
					// intent.putExtra(IntentExtra.EXTRA_IMAGE_CHOOSED_NUM,
					// imgChoosedNum);
					// startActivity(intent);
				}
			}

		});
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

		intent.putExtra(MediaStore.EXTRA_OUTPUT,
				Uri.fromFile(new File(photoPath)));
		startActivityForResult(intent, requestCode);
		return photoPath;
	}

	@Override
	public void onBackPressed() {
		setResult(RESULT_CANCELED);
		finish();
	}

//	public boolean back() {
//		finish();
//		// overridePendingTransition(R.anim.slide_right_in,
//		// R.anim.slide_right_out);
//		overridePendingTransition(0, R.anim.slide_right_out);
//		return true;
//	}

	/**
	 * 相册标题的选择适配器
	 * */
	class OrderAdapter extends BaseAdapter {

		int mItemCount = 0;
		
		ImageCallback callback = new ImageCallback() {
			@Override
			public void imageLoad(ImageView imageView, Bitmap bitmap, Object... params) {
				if (imageView != null && bitmap != null) {
					String url = (String) params[0];
					if (url != null && url.equals((String) imageView.getTag())) {
						((ImageView) imageView).setImageBitmap(bitmap);
					}else{
//						Log.e(TAG, "callback, bmp not match");
					}
				}else{
//					Log.e(TAG, "callback, bmp null");
				}
			}
		};
		
		@Override
		public int getCount() {
			mItemCount = orderList == null ? 0 : orderList.size();
			return mItemCount;
		}

		@Override
		public Object getItem(int arg0) {
			return arg0;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			Holder holder;
			if (convertView == null) {
				holder = new Holder();
				convertView = View.inflate(act, R.layout.item_album_order, null);
				holder.tv = (TextView) convertView.findViewById(R.id.sub_album_name);
				holder.mAlbumIcon = (ImageView) convertView.findViewById(R.id.album_thumb_icon);
				holder.mItemView = (View) convertView.findViewById(R.id.album_thumb_layout);
				convertView.setTag(holder);
			} else {
				holder = (Holder) convertView.getTag();
			}

			holder.mItemView.setBackgroundColor(
					act.getResources().getColor((mSelectedOrder == position) ? R.color.f2 : R.color.white));
			holder.tv.setTextColor(act.getResources().getColor(mSelectedOrder == position ? R.color.ff7 : R.color.font6));
			holder.tv.setText(orderList.get(position));
			
			if(mImageBucketList != null && mImageBucketList.size() > 0){
				int tmpIndex = 0;
				if (position == 0) {
					/**
					 * 初始位置为0时，选择第一个相册的缩略图作为【全部照片】的缩略图
					 */
					tmpIndex = 0;
				} else {
					tmpIndex = position - 1;
				}
				String thumbPath = mImageBucketList.get(tmpIndex).imageList.get(0).thumbnailPath;
				String sourcePath = mImageBucketList.get(tmpIndex).imageList.get(0).imagePath;		
				
				holder.mAlbumIcon.setTag(sourcePath);
				cache.displayBmp(holder.mAlbumIcon, thumbPath, sourcePath, callback);		
				
			} else {
				holder.mAlbumIcon.setImageBitmap(null);	
			}

			return convertView;
		}
		
		
		class Holder{
			private TextView tv;
			private ImageView mAlbumIcon;
			private View mItemView;
		}
	}
}
