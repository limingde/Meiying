package com.dd.meiying;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.youmi.android.AdManager;
import net.youmi.android.spot.SpotManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dd.meiying.abul.view.ImageAlbumGridActivity;
import com.dd.meiying.adapter.MineImgListAdapter;
import com.dd.meiying.bean.ImageTask;
import com.dd.meiying.constent.IntentExtra;
import com.dd.meiying.pichecheng.HechengPicActivity;
import com.dd.meiying.util.AsyncBitmapLoader;
import com.dd.meiying.util.FileUtil;
import com.dd.meiying.wiget.HorizontalListView;
import com.dd.meiying.wiget.pagecur.CurlPage;
import com.dd.meiying.wiget.pagecur.CurlView;

public class MainActivity extends Activity {
	String appId = "48caf3f639947b88";
	String appSecret = "fdd0b82f5cdb6a76";
	OnClickListener mOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.left_tv:
				startActivity(HechengPicActivity.getStartActIntent(MainActivity.this));
				break;
			case R.id.right_btn:
				startActivityForResult(ImageAlbumGridActivity.getStartActIntent(MainActivity.this,true), 
						ImageAlbumGridActivity.REQUEST_CODE_SELECT_IMAGE_FROM_ImageAlbumGridActivity);
				break;
			case R.id.tv_last_pic:
				if(mData != null && mData.size() > 0){
					if(mCurPosition <= 0){
						mCurPosition = mData.size()-1;
					}else {
						mCurPosition --;
					}
					m_lvImgList.setSelection(mCurPosition);
					fillData();
				}
				break;
			case R.id.tv_next_pic:
				if(mData != null && mData.size() > 0){
					if(mCurPosition >= mData.size()-1){
						mCurPosition = 0;
					}else {
						mCurPosition ++;
					}
					m_lvImgList.setSelection(mCurPosition);
					fillData();
				}
				break;
			default:
				break;
			}
		}
	};
	BroadcastReceiver mMyReceiver = new BroadcastReceiver() {		
		@Override
		public void onReceive(Context arg0, Intent data) {
			if(data != null){
				String path = data.getStringExtra(IntentExtra.EXTRA_DATA);
				if(!TextUtils.isEmpty(path)){
					if(mData == null){
						mData = new ArrayList<ImageTask>();
					}
					ImageTask item = new ImageTask();
					item.path = path;
					mData.add(0,item);
				}
			}
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mAsyncBitmapLoader = new AsyncBitmapLoader();
		initData();
		mAdapter =  new MineImgListAdapter(this, mData);
		findView();
		initAd();
		registerReceiver(mMyReceiver, new IntentFilter(IntentExtra.EXTRA_ACTION_HECHENGTUPIAN_SUCCESS));
	}

	public void initAd(){
		AdManager.getInstance(this).init(appId,appSecret, true);
		SpotManager.getInstance(this).loadSpotAds();
		SpotManager.getInstance(this).setSpotOrientation(
				SpotManager.ORIENTATION_PORTRAIT);
		SpotManager.getInstance(this).setAnimationType(SpotManager.ANIM_ADVANCE);
		SpotManager.getInstance(this).showSpotAds(this);
	}
	public void initData(){
		File dir = new File(ImageFilterActivity.ImgSaveDir);
		mData = new ArrayList<ImageTask>();
		if (!dir.exists()) {
			dir.mkdirs();
		} else if (!dir.isDirectory()) {
			if (dir.delete()) {
				dir.mkdirs();
			}
		}

		File[] files = dir.listFiles();
		for (File f : files) {
			if (f.isFile()) {
				ImageTask item = new ImageTask();
				item.path = f.getAbsolutePath();
				mData.add(item);
			} else {
				FileUtil.deleteDirRecursive(f);
			}
		}
	}
	private AsyncBitmapLoader mAsyncBitmapLoader;
	private MineImgListAdapter mAdapter;
	private List<ImageTask> mData;
	private RelativeLayout m_rlImgParrent;
	private CurlView m_ivImg;

	private TextView m_tvLastPic;
	private TextView m_tvNextPic;
	private TextView m_tvCurPic;

	private HorizontalListView m_lvImgList;
	private int mScreenWidth;
	public void findView(){
		initTitle();
		m_rlImgParrent = (RelativeLayout)findViewById(R.id.rl_img_parrent);
		m_ivImg = (CurlView) findViewById(R.id.iv_img);		
		m_ivImg.setPageProvider(new PageProvider());
		m_ivImg.setSizeChangedObserver(new SizeChangedObserver());
		m_ivImg.setCurrentIndex(0);
		m_ivImg.setBackgroundColor(getResources().getColor(R.color.black_10));
		//		mScreenWidth = getWindowManager().getDefaultDisplay().getWidth();
		//		LayoutParams p = new LayoutParams(mScreenWidth,mScreenWidth);
		//		p.setMargins(0, getResources().getDimensionPixelSize(R.dimen.px_100), 0, 0);
		//		m_rlImgParrent.setLayoutParams(p);

		m_tvLastPic = (TextView) findViewById(R.id.tv_last_pic);
		m_tvNextPic = (TextView) findViewById(R.id.tv_next_pic);
		m_tvLastPic.setOnClickListener(mOnClickListener);
		m_tvNextPic.setOnClickListener(mOnClickListener);
		m_tvCurPic = (TextView) findViewById(R.id.tv_cur_pic); 

		m_lvImgList = (HorizontalListView) findViewById(R.id.lv_filter_img);
		m_lvImgList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {

				mCurPosition = position;
				fillData();
			}
		});
		m_lvImgList.setAdapter(mAdapter);

		fillData();
	}

	private int mCurPosition = 0;
	public void fillData(){
		if(mData != null && mData.size() > 0 && mData.size() > mCurPosition){
			ImageTask item = mData.get(mCurPosition);
			mAdapter.setCurrentPosition(mCurPosition);
			int fp = m_lvImgList.getFirstVisiblePosition();
			int lp = m_lvImgList.getLastVisiblePosition();
			if(mCurPosition <=lp || mCurPosition >= fp){
			} else{
				m_lvImgList.setSelection(mCurPosition);

			}
			mAdapter.notifyDataSetChanged();
			m_tvCurPic.setText(mCurPosition + 1  + "/" + mData.size());
			//			if(item !=  null && !TextUtils.isEmpty(item.path)){
			//				Bitmap mbmp = mAsyncBitmapLoader.decodeLocalFile(item.path, 0, 0, 0);
			//				if(mbmp != null){
			//					m_ivImg.setImageBitmap(mbmp);
			//				}
			//			}


		}else {
			try {
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle("提示");
				builder.setMessage("您暂时还没有魅影照片哦，快去制作吧！");
				builder.setNeutralButton("去制作", new DialogInterface.OnClickListener() {						
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						startActivity(ImageAlbumGridActivity.getStartActIntent(MainActivity.this,true));
					}
				});
				builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub

					}					

				});
				builder.show();	
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void initTitle(){
		View titleView = findViewById(R.id.header);
		ImageView left = (ImageView) titleView.findViewById(R.id.left_iv);
		left.setVisibility(View.GONE);
		TextView leftTv = (TextView)titleView.findViewById(R.id.left_tv);
		leftTv.setText("图集");
		leftTv.setOnClickListener(mOnClickListener);
		leftTv.setVisibility(View.VISIBLE);
		Button rightBtn = (Button) titleView.findViewById(R.id.right_btn);
		rightBtn.setVisibility(View.VISIBLE);
		rightBtn.setText("制作");
		rightBtn.setOnClickListener(mOnClickListener);
		TextView title = (TextView)titleView.findViewById(R.id.title_tv);
		title.setText("我的魅影");
	}









	/**
	 * Bitmap provider.
	 */
	private class PageProvider implements CurlView.PageProvider {
		@Override
		public int getPageCount() {
			return mData == null?0:mData.size();
		}

		private Bitmap loadBitmap(int width, int height, int index) {
			Bitmap b = Bitmap.createBitmap(width, height,
					Bitmap.Config.ARGB_8888);
			b.eraseColor(0xFFFFFFFF);
			Canvas c = new Canvas(b);
			if(mData == null || 0>index || mData.size() <= index){
				return null;
			}
			ImageTask item = mData.get(index);
			if(item !=  null && !TextUtils.isEmpty(item.path)){
				Bitmap mbmp = mAsyncBitmapLoader.decodeLocalFile(item.path, 0, 0, 0);
				if(mbmp != null){
					int margin = 7;
					int border = 3;
					Drawable d = new BitmapDrawable(mbmp ); 
					Rect r = new Rect(margin, margin, width - margin, height - margin);

					int imageWidth = r.width() - (border * 2);
					int imageHeight = imageWidth * mbmp.getHeight()
							/ mbmp.getWidth();
					if (imageHeight > r.height() - (border * 2)) {
						imageHeight = r.height() - (border * 2);
						imageWidth = imageHeight * mbmp.getWidth()
								/ mbmp.getHeight();
					}

					r.left += ((r.width() - imageWidth) / 2) - border;
					r.right = r.left + imageWidth + border + border;
					r.top += ((r.height() - imageHeight) / 2) - border;
					r.bottom = r.top + imageHeight + border + border;

					Paint p = new Paint();
					p.setColor(0xFFC0C0C0);
					c.drawRect(r, p);
					r.left += border;
					r.right -= border;
					r.top += border;
					r.bottom -= border;

					d.setBounds(r);
					d.draw(c);
				}
			}


			return b;
		}

		@Override
		public void updatePage(CurlPage page, int width, int height, int index) {

			//			switch (index) {
			//			// First case is image on front side, solid colored back.
			//			case 0: {
			//				Bitmap front = loadBitmap(width, height, 0);
			//				page.setTexture(front, CurlPage.SIDE_FRONT);
			//				page.setColor(Color.rgb(180, 180, 180), CurlPage.SIDE_BACK);
			//				break;
			//			}
			//			// Second case is image on back side, solid colored front.
			//			case 1: {
			//				Bitmap back = loadBitmap(width, height, 2);
			//				page.setTexture(back, CurlPage.SIDE_BACK);
			//				page.setColor(Color.rgb(127, 140, 180), CurlPage.SIDE_FRONT);
			//				break;
			//			}
			//			// Third case is images on both sides.
			//			case 2: {
			//				Bitmap front = loadBitmap(width, height, 1);
			//				Bitmap back = loadBitmap(width, height, 3);
			//				page.setTexture(front, CurlPage.SIDE_FRONT);
			//				page.setTexture(back, CurlPage.SIDE_BACK);
			//				break;
			//			}
			//			// Fourth case is images on both sides - plus they are blend against
			//			// separate colors.
			//			case 3: {
			Bitmap front = loadBitmap(width, height, index);
			Bitmap back = loadBitmap(width, height, index +1 );
			page.setTexture(front, CurlPage.SIDE_FRONT);
			page.setTexture(back, CurlPage.SIDE_BACK);
			page.setColor(Color.rgb(255, 190, 150),
					CurlPage.SIDE_FRONT);
			page.setColor(Color.rgb(255, 190, 150), CurlPage.SIDE_BACK);
			//				break;
			//			}
			//			// Fifth case is same image is assigned to front and back. In this
			//			// scenario only one texture is used and shared for both sides.
			//			case 4:
			//				Bitmap front = loadBitmap(width, height, index);
			//				page.setTexture(front, CurlPage.SIDE_BOTH);
			//				page.setColor(Color.argb(127, 255, 255, 255),
			//						CurlPage.SIDE_BACK);
			//				break;
			//			}
		}

	}

	/**
	 * CurlView size changed observer.
	 */
	private class SizeChangedObserver implements CurlView.SizeChangedObserver {
		@Override
		public void onSizeChanged(int w, int h) {
			if (w > h) {
				m_ivImg.setViewMode(CurlView.SHOW_TWO_PAGES);
				m_ivImg.setMargins(.1f, .05f, .1f, .05f);
			} else {
				m_ivImg.setViewMode(CurlView.SHOW_ONE_PAGE);
				m_ivImg.setMargins(.1f, .1f, .1f, .1f);
			}
		}
	}


	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mMyReceiver);
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == ImageAlbumGridActivity.REQUEST_CODE_SELECT_IMAGE_FROM_ImageAlbumGridActivity){
			if (data != null) {
				String path = data.getStringExtra(IntentExtra.EXTRA_DATA);
				if(!TextUtils.isEmpty(path)){
					if(mData == null){
						mData = new ArrayList<ImageTask>();
					}
					ImageTask item = new ImageTask();
					item.path = path;
					mData.add(0,item);
				}
			}

		}
	}	

}
