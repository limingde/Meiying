package com.dd.meiying;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.dd.meiying.adapter.ImageFilterAdapter;
import com.dd.meiying.bean.FilterImgItem;
import com.dd.meiying.bean.TagResource;
import com.dd.meiying.constent.IntentExtra;
import com.dd.meiying.filter.ImageFilter;
import com.dd.meiying.util.AsyncBitmapLoader;
import com.dd.meiying.util.BitmapUtils;
import com.dd.meiying.util.ScreenShot;
import com.dd.meiying.util.Utils;
import com.dd.meiying.util.WatermarkUtil;
import com.dd.meiying.wiget.HorizontalListView;
import com.dd.meiying.wiget.TagRelativeLayout;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 图片滤镜&标签
 * @author Darren
 * 
 * v4.1.0
 *
 */

public class ImageFilterActivity extends Activity {

	public final static String ACTION_CLOSE_ACTIVITY = "action close activity";
	public static String ImgCacheDir = Environment
			.getExternalStorageDirectory().getAbsolutePath()
			+ "/meiying_photo";
	// 标签相关
	private RelativeLayout m_rlAllTag;
	private RelativeLayout m_rlMark; // 标记
	private List<RelativeLayout> markList;
	private LinearLayout m_llTypeGroup;
	private ImageView m_ivTagTypeMakeup; // 妆品
	private ImageView m_ivTagTypeMakeupOut;
	private ImageView m_ivTagTypeCustom; // 自定�?
	private ImageView m_ivTagTypeCustomOut;
	private TagRelativeLayout mTagRelativeLayout;
	private LinearLayout m_llAddTagTips;
	private Animation mAnimMakeup; // 添加妆品标签按钮的动�?
	private Animation mAnimCustom; // 添加自定义标签按钮的动画
	private int mDownInScreenX = 0;
	private int mDownInScreenY = 0;
	private int mMaxTagCount = 6;
	private boolean isTagLayout = false; // 用于判断当前是否可添加标签状�?
	private boolean isFristTag = true; // 用于判断是否第一次添加标�?
	private boolean isExist = false; // 用于判断标签添加按钮是否存在
	public final static int RESULT_CODE_FOR_TAG_MAKEUP = 10;
	public final static int RESULT_CODE_FOR_TAG_CUSTOM = 11;

	boolean isShowMass = false;

	private ImageFilterAdapter mAdapter;
	private AsyncBitmapLoader mBmpLoader;

	/**
	 * 
	 * @param from
	 * @param mass
	 * @param imgPath
	 * @param needToStartTopicPublish  完成后是否需要跳转到发话题页�?
	 * @return
	 */
	public static Intent getStartActIntent(Activity from,String imgPath){
		Intent intent = new Intent(from,ImageFilterActivity.class);
		intent.putExtra(IntentExtra.EXTRA_DATA, imgPath);
		return intent;
	}

	OnClickListener mClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.tv_back:
				finish();
				break;
			case R.id.tv_next:
				String picPath = saveImage(mFilterBmp == null ? mBmp : mFilterBmp);
				saveImgWithTag();				
				if( !TextUtils.isEmpty(picPath)){
					closeAllActivity();
					finish();
				} else {
					Utils.displayToast(ImageFilterActivity.this, "图片保存失败");
				}
				break;	
			case R.id.btn_filter:
				m_btnFilter.setTextColor(Color.parseColor("#f15b82"));
				m_btnTag.setTextColor(Color.parseColor("#88ffffff"));
				m_lvFilterImg.setVisibility(View.VISIBLE);
				m_llAddTagTips.setVisibility(View.GONE);
				if (isExist) {
					swichButtonState(isExist);
				}
				m_ivTagTypeMakeupOut.clearAnimation();
				m_ivTagTypeCustomOut.clearAnimation();
				isTagLayout = false;
				break;
			case R.id.btn_tag:
				m_btnFilter.setTextColor(Color.parseColor("#88ffffff"));
				m_btnTag.setTextColor(Color.parseColor("#f15b82"));
				m_lvFilterImg.setVisibility(View.GONE);
				m_llAddTagTips.setVisibility(View.VISIBLE);
				isTagLayout = true;
				break;
			case R.id.iv_type_makeup:
				if (isFristTag) {
					//					startActivityForResult(ProductTagActivity.getStartActIntent(act, ProductTagActivity.TYPE_PRODUCT_TAG), 
					//							RESULT_CODE_FOR_TAG_MAKEUP);
				} else if (!isFristTag && mTagRelativeLayout.mTagResList != null 
						&& mTagRelativeLayout.mTagResList.size() < mMaxTagCount) {
					//					startActivityForResult(ProductTagActivity.getStartActIntent(act, ProductTagActivity.TYPE_PRODUCT_TAG), 
					//							RESULT_CODE_FOR_TAG_MAKEUP);
				} else {
					//					Utils.displayToast(act, "标签�?��添加6个哦~");
				}
				swichButtonState(true);
				break;
			case R.id.iv_type_custom:
				if (isFristTag) {
					//					startActivityForResult(ProductTagActivity.getStartActIntent(act, ProductTagActivity.TYPE_CUSTOM_TAG), 
					//							RESULT_CODE_FOR_TAG_CUSTOM);
				} else if (!isFristTag && mTagRelativeLayout.mTagResList != null 
						&& mTagRelativeLayout.mTagResList.size() < mMaxTagCount) {
					//					startActivityForResult(ProductTagActivity.getStartActIntent(act, ProductTagActivity.TYPE_CUSTOM_TAG), 
					//							RESULT_CODE_FOR_TAG_CUSTOM);
				} else {
					//					Utils.displayToast(act, "标签�?��添加6个哦~");
				}
				swichButtonState(true);
				break;
			}
		}
	};

	public static String saveImage(Bitmap bmp){
		if (bmp != null) {

			File dir = new File(ImgCacheDir);
			if (!dir.exists()) {
				dir.mkdirs();
			} else if (!dir.isDirectory()) {
				if (dir.delete()) {
					dir.mkdirs();
				}
			}
			String path =  dir.getAbsolutePath() + "/edit_image"+ System.currentTimeMillis();				
			boolean saveOk = BitmapUtils.saveBitmap(bmp,path);
			if (saveOk) {
				// 进行下一步操�?
				// 将图片路径作为参�?
				return path;

			} 
		} 
		return null;
	}

	public void closeAllActivity(){
		sendBroadcast(new Intent(ACTION_CLOSE_ACTIVITY));
		this.finish();
	}
	private String mImgPsth;
	private boolean mNeedToStartTopicPublish = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image_filter);
		mTagRelativeLayout = new TagRelativeLayout(this);
		mBmpLoader = new AsyncBitmapLoader();
		if(getIntent() != null){
			mImgPsth = getIntent().getStringExtra(IntentExtra.EXTRA_DATA);
		}
		getData();
		mAdapter =  new ImageFilterAdapter(this, mData,mThuBmp);
		findView();	
		addCommentImgTask();
	}

	OnItemClickListener mOnItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view,
				int position, long id) {
			if(mData != null && mData.size() > position && position >= 0){
				FilterImgItem item = mData.get(position);
				mAdapter.setCurrentPosition(position);
				mAdapter.notifyDataSetChanged();
				if(item != null){
					filterImage(item.type);
				}
			}
		}
	};
	private Bitmap mFilterBmp;
	public void filterImage(int type){
		mFilterBmp = ImageFilter.getFilterImage(mBmp, type);
		if(mFilterBmp != null){
			m_mgvInsertResultImage.setImageBitmap(mFilterBmp);
		}
		resetImgView(mFilterBmp);
	}

	public void resetImgView(Bitmap bmp){
		if(bmp == null){
			return;
		}
		int w = bmp.getWidth();
		int h = bmp.getHeight();
		int totalH = (int) ( getResources().getDimensionPixelSize(R.dimen.px_114) 
				+ getResources().getDimensionPixelSize(R.dimen.px_248));
		mScreenWidth = getWindowManager().getDefaultDisplay().getWidth();
		if(mActivityHeight <= 0){
			return;
		}
		int h1 = mActivityHeight -totalH;
		int hh = 0;
		int ww = mScreenWidth;
		if(w != 0){
			hh = (int)((float)ww/(float)w * h);
		}
		if(h1 < hh){
			hh = h1;
			if(hh != 0){
				ww = (int)((float)h/(float)hh * w);
			}
		} else {
			if(w != 0){
				hh = (int)((float)ww/(float)w * h);
			}
		}
		Log.e(">>>>>>>>>", "====>>ww:" + ww + "====>>hh:" + hh +"===>>w:" + w + "===>>h:" + h + "===>>h1:" + h1);
		RelativeLayout rlImgParrent= (RelativeLayout) findViewById(R.id.rl_img_parrent);
		LayoutParams p = new LayoutParams(ww,hh);
		rlImgParrent.setLayoutParams(p);
	}
	private ImageView m_mgvInsertResultImage;
	private RelativeLayout m_rlHeader;
	private TextView m_tvLastStep;
	private TextView m_tvFinish;

	private Button m_btnFilter;
	private Button m_btnTag;
	LinearLayout llOperationParrent;

	private HorizontalListView m_lvFilterImg;
	private void findView() {
		m_rlHeader = (RelativeLayout) findViewById(R.id.rl_header);
		m_tvLastStep = (TextView) findViewById(R.id.tv_back);
		m_tvFinish = (TextView) findViewById(R.id.tv_next);
		m_mgvInsertResultImage = (ImageView) findViewById(R.id.iv_img);
		m_tvLastStep.setOnClickListener(mClickListener);
		m_tvFinish.setOnClickListener(mClickListener);

		//		mScreenWidth = getWindowManager().getDefaultDisplay().getWidth();
		//		RelativeLayout rlImgParrent= (RelativeLayout) findViewById(R.id.rl_img_parrent);
		//		LayoutParams p = new LayoutParams(mScreenWidth,mScreenWidth);
		//		rlImgParrent.setLayoutParams(p);

		llOperationParrent = (LinearLayout) findViewById(R.id.ll_btn_parrent);
		m_btnFilter = (Button) findViewById(R.id.btn_filter);
		m_btnTag = (Button) findViewById(R.id.btn_tag);
		m_btnFilter.setOnClickListener(mClickListener);
		m_btnTag.setOnClickListener(mClickListener);
		llOperationParrent.setVisibility(View.VISIBLE);

		m_lvFilterImg = (HorizontalListView) findViewById(R.id.lv_filter_img);
		m_lvFilterImg.setAdapter(mAdapter);
		m_lvFilterImg.setVisibility(View.VISIBLE);

		m_lvFilterImg.setOnItemClickListener(mOnItemClickListener);

		// 标签相关
		m_rlAllTag = (RelativeLayout) findViewById(R.id.all_tag_layout);
		m_llTypeGroup = (LinearLayout) m_rlAllTag.findViewById(R.id.ll_tag_group);
		m_ivTagTypeMakeupOut = (ImageView) m_rlAllTag.findViewById(R.id.iv_type_makeup_out);
		m_ivTagTypeMakeup = (ImageView) m_rlAllTag.findViewById(R.id.iv_type_makeup);
		m_ivTagTypeMakeup.setOnClickListener(mClickListener);
		m_ivTagTypeCustomOut = (ImageView) m_rlAllTag.findViewById(R.id.iv_type_custom_out);
		m_ivTagTypeCustom = (ImageView) m_rlAllTag.findViewById(R.id.iv_type_custom);
		m_ivTagTypeCustom.setOnClickListener(mClickListener);

		mTagRelativeLayout = (TagRelativeLayout) m_rlAllTag.findViewById(R.id.tag_layout);
		mTagRelativeLayout.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (isTagLayout) {
					switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						mDownInScreenX = (int) event.getX();
						mDownInScreenY = (int) event.getY();
						swichButtonState(isExist);
						break;
					default:
						break;
					}
				}				
				return true;
			}
		});

		mAnimMakeup = AnimationUtils.loadAnimation(this, R.anim.tag_add);
		mAnimCustom = AnimationUtils.loadAnimation(this, R.anim.tag_add);

		m_llAddTagTips = (LinearLayout) findViewById(R.id.ll_add_tag_tips);
	}

	private int mActivityHeight = 0;
	private int mScreenWidth = 0;
	public void onWindowFocusChanged(boolean hasFocus) {
		View v = findViewById(R.id.ll_img_outer);
		mActivityHeight = v.getHeight();
		resetView();
		resetImgView(mFilterBmp == null ? mBmp : mFilterBmp);
		mTagRelativeLayout.setTagLayoutWH(m_mgvInsertResultImage.getWidth(), m_mgvInsertResultImage.getHeight());
	};
	public void resetView(){

		int totalH = (int) ( mScreenWidth 
				+ getResources().getDimensionPixelSize(R.dimen.px_114) 
				+ getResources().getDimensionPixelSize(R.dimen.px_248));
		if(mActivityHeight < totalH){
			mAdapter.setIsShowTitle(false);

		} else{
			mAdapter.setIsShowTitle(true);	
		}
		m_lvFilterImg.setAdapter(mAdapter);
		mAdapter.notifyDataSetChanged();
	}
	private void swichButtonState(boolean isExist) {
		if (isExist) {
			this.isExist = false;
			m_llTypeGroup.setVisibility(View.INVISIBLE);
			m_ivTagTypeMakeupOut.clearAnimation();
			m_ivTagTypeCustomOut.clearAnimation();
			deleteMark();
		} else {
			this.isExist = true;
			m_llTypeGroup.setVisibility(View.VISIBLE);
			m_ivTagTypeMakeupOut.startAnimation(mAnimMakeup);
			// 两个按钮动画要有错落�?
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					m_ivTagTypeCustomOut.startAnimation(mAnimCustom);
				}
			}, 600);
			addMark();
		}

	}

	// 添加标记
	private void addMark() {
		if (markList == null) {
			markList = new ArrayList<RelativeLayout>();
		}
		m_rlMark = new RelativeLayout(this);
		m_rlMark = (RelativeLayout) View.inflate(this, R.layout.tag_mark_layout, null);
		mTagRelativeLayout.addView(m_rlMark);
		m_rlMark.setVisibility(View.INVISIBLE);
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				setPosition(m_rlMark, mDownInScreenX, mDownInScreenY);
			}
		}, 100);		
		markList.add(m_rlMark);
	}

	// 删除标记
	private void deleteMark() {
		if (markList!= null && markList.size() != 0) {
			mTagRelativeLayout.removeView(markList.get(markList.size() - 1));
		}
		markList.remove(markList.size() - 1);
	}

	/**
	 * 设置定位标记
	 * @param v
	 * @param dx
	 * @param dy
	 */
	private void setPosition(View v, int dx, int dy) {
		int parentWidth = mTagRelativeLayout.getWidth();
		int parentHeight = mTagRelativeLayout.getHeight();
		int l, t, r, b;
		if ((parentWidth - dx) >= v.getWidth()) {
			l = dx;
			r = l + v.getWidth();
		} else {
			r = parentWidth;
			l = parentWidth - v.getWidth();
		}
		if ((parentHeight - dy) >= v.getHeight()) {
			t = dy;
			b = l + v.getHeight();
		} else {
			b = parentHeight;
			t = parentHeight - v.getHeight();
		}
		v.layout(l, t, r, b);
		RelativeLayout.LayoutParams params = (android.widget.RelativeLayout.LayoutParams) v.getLayoutParams();
		params.leftMargin = l;
		params.topMargin = t;
		v.setLayoutParams(params);
		v.setVisibility(View.VISIBLE);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}


	@Override
	public void onResume() {

		super.onResume();
	}


	private Bitmap mThuBmp;

	private List<FilterImgItem> mData = new ArrayList<FilterImgItem>();
	public void getData(){
		mThuBmp = BitmapUtils.resizeBmp(mImgPsth, 
				getResources().getDimensionPixelSize(R.dimen.px_160), getResources().getDimensionPixelSize(R.dimen.px_160));
		FilterImgItem item = new FilterImgItem();
		item.title = "原图";
		item.type = ImageFilter.TYPE_origin;
		mData.add(item);

		FilterImgItem item1 = new FilterImgItem();		
		item1.title = "LOMO";
		item1.type = ImageFilter.TYPE_lomo;
		mData.add(item1);

		FilterImgItem item2 = new FilterImgItem();
		item2.title = "黑白  ";
		item2.type =ImageFilter.TYPE_blackandwhite;
		mData.add(item2);

		FilterImgItem item3 = new FilterImgItem();	
		item3.title = "复古  ";
		item3.type = ImageFilter.TYPE_retro;
		mData.add(item3);

		FilterImgItem item4 = new FilterImgItem();
		item4.title = "哥特  ";
		item4.type = ImageFilter.TYPE_gothic;
		mData.add(item4);

		FilterImgItem item5 = new FilterImgItem();
		item5.title = "锐化  ";
		item5.type = ImageFilter.TYPE_sharp;
		mData.add(item5);

		FilterImgItem item6 = new FilterImgItem();
		item6.title = "淡雅  ";
		item6.type = ImageFilter.TYPE_contribute;
		mData.add(item6);

		FilterImgItem item7 = new FilterImgItem();
		item7.title = "酒红  ";
		item7.type = ImageFilter.TYPE_claret_red;
		mData.add(item7);

		FilterImgItem item8 = new FilterImgItem();
		item8.title = "清宁 ";
		item8.type = ImageFilter.TYPE_qingning;
		mData.add(item8);

		FilterImgItem item9 = new FilterImgItem();
		item9.title = "浪漫  ";
		item9.type = ImageFilter.TYPE_romantic;
		mData.add(item9);

		FilterImgItem item10 = new FilterImgItem();
		item10.title = "光晕 ";
		item10.type = ImageFilter.TYPE_halo;
		mData.add(item10);

		FilterImgItem item11 = new FilterImgItem();
		item11.title = "蓝调 ";
		item11.type = ImageFilter.TYPE_blues;
		mData.add(item11);

		FilterImgItem item12 = new FilterImgItem();
		item12.title = "梦幻 ";
		item12.type = ImageFilter.TYPE_dream;
		mData.add(item12);

		FilterImgItem item13 = new FilterImgItem();
		item13.title = "夜色";
		item13.type = ImageFilter.TYPE_night;
		mData.add(item13);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == RESULT_CODE_FOR_TAG_MAKEUP && resultCode == RESULT_OK) {
			if (data.getSerializableExtra(IntentExtra.EXTRA_DATA) != null) {
				TagResource mTAg = (TagResource) data.getSerializableExtra(IntentExtra.EXTRA_DATA);
				mTagRelativeLayout.addTag(mTAg, mDownInScreenX, mDownInScreenY);
				if (isFristTag) {
					isFristTag = false;	
				}							
			}
		} 

		if (requestCode == RESULT_CODE_FOR_TAG_CUSTOM && resultCode == RESULT_OK) {
			if (data.getSerializableExtra(IntentExtra.EXTRA_DATA) != null) {
				TagResource mTAg = (TagResource) data.getSerializableExtra(IntentExtra.EXTRA_DATA);
				mTagRelativeLayout.addTag(mTAg, mDownInScreenX, mDownInScreenY);
				if (isFristTag) {
					isFristTag = false;	
				}				
			}
		}

	}	

	private Bitmap mBmp;
	void addCommentImgTask() {	
		mBmp = mBmpLoader.decodeLocalFile(mImgPsth, 0,0,0);
		if(mBmp != null){
			m_mgvInsertResultImage.setImageBitmap(mBmp);
		}
		resetImgView(mBmp);
	}

	/**
	 * 保存编辑完成的图片
	 */
	private void saveImgWithTag() {
		// 获取状�?栏高�?
		Rect frame = new Rect();
		getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
		int statusBarHeight = frame.top;

		// 获取状�?栏与图片之间控件的高�?
		int headerHeight = m_rlHeader.getHeight();

		// 获取图片的宽�?
		int picWidth = m_rlAllTag.getWidth();
		int picHeight = m_rlAllTag.getHeight();
		mTagRelativeLayout.clearAllAnim();
		Bitmap mBitmap = ScreenShot.takeScreenShot(this, 0, (statusBarHeight + headerHeight), picWidth, picHeight);
		// 保存图片（不带水印）
		ScreenShot.savePic(mBitmap);
		// 保存图片（带水印�?
		//		WatermarkUtil.addWatermark(this, mBitmap, R.drawable.topic_photo_save_icon,WatermarkUtil.TYPE_WATERMARK);
	}
}
