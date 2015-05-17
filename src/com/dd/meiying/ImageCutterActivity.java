package com.dd.meiying;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dd.meiying.abul.view.ImageAlbumGridActivity;
import com.dd.meiying.constent.IntentExtra;
import com.dd.meiying.wiget.cropimage.CropImageLayout;

/**
 * 裁剪图片
 * */
public class ImageCutterActivity extends Activity implements
OnClickListener {
	private Bitmap mBitmap;
	private CropImageLayout m_ivClipView;
	private TextView m_tvNext;
	private TextView m_tvBack;
	private LinearLayout m_rvRotare;
	private int rotate;
	private String mPicPath;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image_cutter);
		init();
		registerReceiver(mCloseActivity, new IntentFilter(
				ImageFilterActivity.ACTION_CLOSE_ACTIVITY));
	}

	private boolean mNeedToNext = false;
	private void init() {
		setUpView();
		if (getIntent() != null) {
			mPicPath = getIntent().getStringExtra(IntentExtra.EXTRA_IMG_URL);

			mNeedToNext = getIntent().getBooleanExtra(IntentExtra.EXTRA_DATA_BOOL, false);
			m_ivClipView.setImagePath(mPicPath);
			if (!TextUtils.isEmpty(mPicPath)) {
				m_ivClipView.setImageBitmap(createCaptureBitmap(mPicPath));
			}
		}
	}

	private TextView m_tvRate1;
	private TextView m_tvRate2;
	private TextView m_tvRate3;
	private TextView m_tvRate4;
	private TextView m_tvRate5;
	private void setUpView() {
		m_ivClipView = (CropImageLayout) findViewById(R.id.image_cutter_clip);
		m_tvNext = (TextView) findViewById(R.id.image_cutter_next);
		m_tvBack = (TextView) findViewById(R.id.image_cutter_back);
		m_rvRotare = (LinearLayout) findViewById(R.id.cutter_bottom_rotate);
		
		
		m_tvRate1 = (TextView) findViewById(R.id.tv_rate_1);
		m_tvRate2 = (TextView) findViewById(R.id.tv_rate_2);
		m_tvRate3 = (TextView) findViewById(R.id.tv_rate_3);
		m_tvRate4 = (TextView) findViewById(R.id.tv_rate_4);
		m_tvRate5 = (TextView) findViewById(R.id.tv_rate_5);
		m_tvRate1.setOnClickListener(this);
		m_tvRate2.setOnClickListener(this);
		m_tvRate3.setOnClickListener(this);
		m_tvRate4.setOnClickListener(this);
		m_tvRate5.setOnClickListener(this);
		
		registerListener();
	}

	private void registerListener() {
		m_tvNext.setOnClickListener(this);
		m_tvBack.setOnClickListener(this);
		m_rvRotare.setOnClickListener(this);
	}

	public static Intent getStartActIntent(Activity from, String picPath,boolean isNeedToNext){
		Intent intent = new Intent(from, ImageCutterActivity.class);
		intent.putExtra(IntentExtra.EXTRA_IMG_URL, picPath);
		intent.putExtra(IntentExtra.EXTRA_DATA_BOOL, isNeedToNext);
		return intent;
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		unregisterReceiver(mCloseActivity);
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.image_cutter_next:
			mBitmap = m_ivClipView.clip();
			if (mBitmap != null) {
				String path = ImageFilterActivity.saveImage(mBitmap,ImageFilterActivity.ImgCacheDir);
				if (path != null) {
					if(mNeedToNext){
						startActivityForResult(ImageFilterActivity.getStartActIntent(this, path,true),
								ImageAlbumGridActivity.REQUEST_CODE_SELECT_IMAGE_FROM_ImageAlbumGridActivity);
					} else {
						startActivity(ImageFilterActivity.getStartActIntent(this, path,false));					
						
					}
				}
			} 
			break;
		case R.id.image_cutter_back:
			this.finish();
			break;

		case R.id.cutter_bottom_rotate:
			rotate += 90;
			m_ivClipView.setRotate(rotate);
			break;
		case R.id.tv_rate_1:
			m_ivClipView.setWHRate(1f/1f);
			break;
		case R.id.tv_rate_2:
			m_ivClipView.setWHRate(2f/3f);
			break;
		case R.id.tv_rate_3:
			m_ivClipView.setWHRate(3f/4f);
			break;
		case R.id.tv_rate_4:
			m_ivClipView.setWHRate(9f/19f);
			break;
		case R.id.tv_rate_5:
			m_ivClipView.setWHRate(16f/9f);
			break;
		}
	}
	BroadcastReceiver mCloseActivity = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			finish();
		}
	};

	public static Bitmap createCaptureBitmap(String filepath) {
		int scale = 1;
		try {
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(filepath, options);
			int IMAGE_MAX_SIZE = 1024;
			if (options.outWidth > IMAGE_MAX_SIZE
					|| options.outHeight > IMAGE_MAX_SIZE) {
				scale = (int) Math.pow(
						2,
						(int) Math.round(Math.log(IMAGE_MAX_SIZE
								/ (double) Math.max(options.outHeight,
										options.outWidth))
										/ Math.log(0.5)));
			}
			BitmapFactory.Options opt = new BitmapFactory.Options();
			opt.inSampleSize = scale;
			return BitmapFactory.decodeFile(filepath, opt);
		} catch (OutOfMemoryError e) {
			scale = scale * 2;
			try {
				BitmapFactory.Options opt = new BitmapFactory.Options();
				opt.inSampleSize = scale;
				return BitmapFactory.decodeFile(filepath, opt);
			} catch (OutOfMemoryError oe) {
				return null;
			}
		}
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == ImageAlbumGridActivity.REQUEST_CODE_SELECT_IMAGE_FROM_ImageAlbumGridActivity){
			if (data != null) {
				setResult(ImageAlbumGridActivity.RESULT_CODE_SELECT_IMAGE_FROM_ImageAlbumGridActivity, data);				
			}	
			finish();
		}
	}
}
