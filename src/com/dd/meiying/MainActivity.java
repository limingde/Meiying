package com.dd.meiying;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.youmi.android.AdManager;
import net.youmi.android.spot.SpotManager;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dd.meiying.abul.view.ImageAlbumGridActivity;
import com.dd.meiying.adapter.MineImgListAdapter;
import com.dd.meiying.bean.ImageTask;
import com.dd.meiying.pichecheng.HechengPicActivity;
import com.dd.meiying.util.AsyncBitmapLoader;
import com.dd.meiying.util.FileUtil;
import com.dd.meiying.wiget.HorizontalListView;

public class MainActivity extends Activity {
	String appId = "48caf3f639947b88";
	String appSecret = "fdd0b82f5cdb6a76";
	OnClickListener mOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.left_iv:

				break;
			case R.id.left_tv:
				startActivity(HechengPicActivity.getStartActIntent(MainActivity.this));
				break;
			case R.id.right_btn:
				startActivity(ImageAlbumGridActivity.getStartActIntent(MainActivity.this,true));
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
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mAsyncBitmapLoader = new AsyncBitmapLoader();
		initData();
		mAdapter =  new MineImgListAdapter(this, mData);
		findView();
		initAd();
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
		File dir = new File(ImageFilterActivity.ImgCacheDir);
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
	private ImageView m_ivImg;

	private TextView m_tvLastPic;
	private TextView m_tvNextPic;
	private TextView m_tvCurPic;

	private HorizontalListView m_lvImgList;
	private int mScreenWidth;
	public void findView(){
		initTitle();
		m_rlImgParrent = (RelativeLayout)findViewById(R.id.rl_img_parrent);
		m_ivImg = (ImageView) findViewById(R.id.iv_img);		
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
			if(item !=  null && !TextUtils.isEmpty(item.path)){
				Bitmap mbmp = mAsyncBitmapLoader.decodeLocalFile(item.path, 0, 0, 0);
				if(mbmp != null){
					m_ivImg.setImageBitmap(mbmp);
				}
			}
			

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
}
