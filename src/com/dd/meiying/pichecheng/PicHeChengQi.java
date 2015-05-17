package com.dd.meiying.pichecheng;

import com.dd.meiying.R;
import com.dd.meiying.bean.HechengData;
import com.dd.meiying.bean.HechengIndexData;
import com.dd.meiying.util.BitmapUtils;
import com.dd.meiying.util.ScreenShot;
import com.dd.meiying.util.WatermarkUtil;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Rect;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

public class PicHeChengQi {

	public final static int TYPE_IMG = 0;
	public final static int TYPE_TEXT = 1;

	private Activity act;
	private int mScreenWidth;
	PicHechengQiCallBack mCallBack;
	public PicHeChengQi(Activity context,PicHechengQiCallBack callBack){
		this.act = context;
		this.mCallBack = callBack;
		mScreenWidth = context.getWindowManager().getDefaultDisplay().getWidth();
	}

	public View GetView(final HechengData data){
		View v = View.inflate(act, R.layout.item_pic_hecheng, null);	
		m_rlParrent = (RelativeLayout) v.findViewById(R.id.parrent);
		LayoutParams p = new LayoutParams(LayoutParams.MATCH_PARENT,mScreenWidth);
		m_rlParrent.setLayoutParams(p);

		m_ivBg = (ImageView) v.findViewById(R.id.iv_bg);
		m_llLRParrent = (LinearLayout) v.findViewById(R.id.ll_lr_parrent);
		m_llUBParrent = (LinearLayout) v.findViewById(R.id.ll_ub_parrent);
		

		m_ivLU = (ImageView) v.findViewById(R.id.iv_left_up);
		m_ivLB = (ImageView) v.findViewById(R.id.iv_left_bottom);
		m_ivRU = (ImageView) v.findViewById(R.id.iv_right_up);
		m_ivRB = (ImageView) v.findViewById(R.id.iv_right_bottom);

		m_ivUL = (ImageView) v.findViewById(R.id.iv_up_left);
		m_ivUR = (ImageView) v.findViewById(R.id.iv_up_right);
		m_ivBL = (ImageView) v.findViewById(R.id.iv_bottom_left);
		m_ivBR = (ImageView) v.findViewById(R.id.iv_bottom_right);

		OnClickListener mOnClickListener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.iv_left_up:	
					doCallBack(TYPE_IMG, 1,data);
					break;

				case R.id.iv_left_bottom:
					doCallBack(TYPE_IMG, 2,data);
					break;
				case R.id.iv_right_up:
					doCallBack(TYPE_IMG, 3,data);
					break;
				case R.id.iv_right_bottom:	
					doCallBack(TYPE_IMG, 4,data);
					break;

				case R.id.iv_up_left:	
					doCallBack(TYPE_IMG, 1,data);
					break;
				case R.id.iv_up_right:	
					doCallBack(TYPE_IMG, 3,data);
					break;
				case R.id.iv_bottom_left:
					doCallBack(TYPE_IMG, 2,data);				
					break;
				case R.id.iv_bottom_right:	
					doCallBack(TYPE_IMG, 4,data);
					break;
				default:
					break;
				}

			}
		};
		m_ivLU.setOnClickListener(mOnClickListener);
		m_ivLB.setOnClickListener(mOnClickListener);
		m_ivRU.setOnClickListener(mOnClickListener);
		m_ivRB.setOnClickListener(mOnClickListener);

		m_ivUL.setOnClickListener(mOnClickListener);
		m_ivUR.setOnClickListener(mOnClickListener);
		m_ivBL.setOnClickListener(mOnClickListener);
		m_ivBR.setOnClickListener(mOnClickListener);
		initView(data);

		return v;
	}
	public View GetView(final HechengIndexData data){
		View v = View.inflate(act, R.layout.item_pic_hecheng_index, null);	
		m_rlParrent = (RelativeLayout) v.findViewById(R.id.parrent);
		LayoutParams p = new LayoutParams(
				act.getResources().getDimensionPixelSize(R.dimen.px_169),
				act.getResources().getDimensionPixelSize(R.dimen.px_169));
		m_rlParrent.setLayoutParams(p);

		m_ivBg = (ImageView) v.findViewById(R.id.iv_bg);
		m_llLRParrent = (LinearLayout) v.findViewById(R.id.ll_lr_parrent);
		m_llUBParrent = (LinearLayout) v.findViewById(R.id.ll_ub_parrent);

		m_ivLU = (ImageView) v.findViewById(R.id.iv_left_up);
		m_ivLB = (ImageView) v.findViewById(R.id.iv_left_bottom);
		m_ivRU = (ImageView) v.findViewById(R.id.iv_right_up);
		m_ivRB = (ImageView) v.findViewById(R.id.iv_right_bottom);

		m_ivUL = (ImageView) v.findViewById(R.id.iv_up_left);
		m_ivUR = (ImageView) v.findViewById(R.id.iv_up_right);
		m_ivBL = (ImageView) v.findViewById(R.id.iv_bottom_left);
		m_ivBR = (ImageView) v.findViewById(R.id.iv_bottom_right);

		
		initView(data);

		return v;
	}
	
	private void initView(HechengData data) {

		if(data == null){
			return;
		}
		m_llLRParrent.setVisibility(View.GONE);
		m_llUBParrent.setVisibility(View.GONE);
		m_ivLU.setVisibility(View.GONE);
		m_ivRU.setVisibility(View.GONE);
		m_ivLB.setVisibility(View.GONE);
		m_ivRB.setVisibility(View.GONE);

		m_ivUL.setVisibility(View.GONE);
		m_ivUR.setVisibility(View.GONE);
		m_ivBL.setVisibility(View.GONE);
		m_ivBR.setVisibility(View.GONE);
		LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(mScreenWidth/2,mScreenWidth);
		LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(mScreenWidth,mScreenWidth/2);
		LinearLayout.LayoutParams lp3 = new LinearLayout.LayoutParams(mScreenWidth/2,mScreenWidth/2);
		switch (data.type) {
		case HechengData.TYPE_L1_R1:
			m_llLRParrent.setVisibility(View.VISIBLE);	
			m_ivLU.setLayoutParams(lp1);
			m_ivRU.setLayoutParams(lp1);

			m_ivLU.setVisibility(View.VISIBLE);
			m_ivRU.setVisibility(View.VISIBLE);
			initLRImgView(data);
			break;
		case HechengData.TYPE_L1_R2:
			m_llLRParrent.setVisibility(View.VISIBLE);
			m_ivLU.setLayoutParams(lp1);
			m_ivRU.setLayoutParams(lp3);
			m_ivRB.setLayoutParams(lp3);
			
			m_ivLU.setVisibility(View.VISIBLE);
			m_ivRU.setVisibility(View.VISIBLE);
			m_ivRB.setVisibility(View.VISIBLE);
			initLRImgView(data);
			break;
		case HechengData.TYPE_L2_R1:
			m_llLRParrent.setVisibility(View.VISIBLE);
			m_ivLU.setLayoutParams(lp3);
			m_ivRU.setLayoutParams(lp1);
			m_ivLB.setLayoutParams(lp3);
			
			m_ivLU.setVisibility(View.VISIBLE);
			m_ivRU.setVisibility(View.VISIBLE);
			m_ivLB.setVisibility(View.VISIBLE);
			initLRImgView(data);
			break;
		case HechengData.TYPE_L2_R2:
			m_llLRParrent.setVisibility(View.VISIBLE);
			m_ivLU.setLayoutParams(lp3);
			m_ivRU.setLayoutParams(lp3);
			m_ivLB.setLayoutParams(lp3);
			m_ivRB.setLayoutParams(lp3);
			
			m_ivLU.setVisibility(View.VISIBLE);
			m_ivRU.setVisibility(View.VISIBLE);
			m_ivLB.setVisibility(View.VISIBLE);
			m_ivRB.setVisibility(View.VISIBLE);
			initLRImgView(data);
			break;

		case HechengData.TYPE_U1_B1:
			m_llUBParrent.setVisibility(View.VISIBLE);
			m_ivUL.setLayoutParams(lp2);
			m_ivBL.setLayoutParams(lp2);
			
			m_ivUL.setVisibility(View.VISIBLE);
			m_ivBL.setVisibility(View.VISIBLE);
			initUBImgView(data);
			break;
		case HechengData.TYPE_U1_B2:
			m_llUBParrent.setVisibility(View.VISIBLE);
			m_ivUL.setLayoutParams(lp2);
			m_ivBL.setLayoutParams(lp3);
			m_ivBR.setLayoutParams(lp3);
			
			m_ivUL.setVisibility(View.VISIBLE);
			m_ivBL.setVisibility(View.VISIBLE);
			m_ivBR.setVisibility(View.VISIBLE);
			initUBImgView(data);
			break;
		case HechengData.TYPE_U2_B1:
			m_llUBParrent.setVisibility(View.VISIBLE);
			m_ivUL.setLayoutParams(lp3);
			m_ivUR.setLayoutParams(lp3);
			m_ivBL.setLayoutParams(lp2);
			
			m_ivUL.setVisibility(View.VISIBLE);
			m_ivUR.setVisibility(View.VISIBLE);
			m_ivBL.setVisibility(View.VISIBLE);
			initUBImgView(data);
			break;
		case HechengData.TYPE_U2_B2:
			m_llUBParrent.setVisibility(View.VISIBLE);
			m_ivUL.setLayoutParams(lp3);
			m_ivUR.setLayoutParams(lp3);
			m_ivBL.setLayoutParams(lp3);
			m_ivBR.setLayoutParams(lp3);
			
			m_ivUL.setVisibility(View.VISIBLE);
			m_ivUR.setVisibility(View.VISIBLE);
			m_ivBL.setVisibility(View.VISIBLE);
			m_ivBR.setVisibility(View.VISIBLE);
			initUBImgView(data);
			break;


		default:
			break;
		}
	}
	private void initView(HechengIndexData data) {

		if(data == null){
			return;
		}
		m_llLRParrent.setVisibility(View.GONE);
		m_llUBParrent.setVisibility(View.GONE);
		m_ivLU.setVisibility(View.GONE);
		m_ivRU.setVisibility(View.GONE);
		m_ivLB.setVisibility(View.GONE);
		m_ivRB.setVisibility(View.GONE);

		m_ivUL.setVisibility(View.GONE);
		m_ivUR.setVisibility(View.GONE);
		m_ivBL.setVisibility(View.GONE);
		m_ivBR.setVisibility(View.GONE);
		switch (data.type) {
		case HechengData.TYPE_L1_R1:
			m_llLRParrent.setVisibility(View.VISIBLE);			
			m_ivLU.setVisibility(View.VISIBLE);
			m_ivRU.setVisibility(View.VISIBLE);
			initLRImgView(data);
			break;
		case HechengData.TYPE_L1_R2:
			m_llLRParrent.setVisibility(View.VISIBLE);
			m_ivLU.setVisibility(View.VISIBLE);
			m_ivRU.setVisibility(View.VISIBLE);
			m_ivRB.setVisibility(View.VISIBLE);
			initLRImgView(data);
			break;
		case HechengData.TYPE_L2_R1:
			m_llLRParrent.setVisibility(View.VISIBLE);
			m_ivLU.setVisibility(View.VISIBLE);
			m_ivRU.setVisibility(View.VISIBLE);
			m_ivLB.setVisibility(View.VISIBLE);
			initLRImgView(data);
			break;
		case HechengData.TYPE_L2_R2:
			m_llLRParrent.setVisibility(View.VISIBLE);
			m_ivLU.setVisibility(View.VISIBLE);
			m_ivRU.setVisibility(View.VISIBLE);
			m_ivLB.setVisibility(View.VISIBLE);
			m_ivRB.setVisibility(View.VISIBLE);
			initLRImgView(data);
			break;

		case HechengData.TYPE_U1_B1:
			m_llUBParrent.setVisibility(View.VISIBLE);
			m_ivUL.setVisibility(View.VISIBLE);
			m_ivBL.setVisibility(View.VISIBLE);
			initUBImgView(data);
			break;
		case HechengData.TYPE_U1_B2:
			m_llUBParrent.setVisibility(View.VISIBLE);
			m_ivUL.setVisibility(View.VISIBLE);
			m_ivBL.setVisibility(View.VISIBLE);
			m_ivBR.setVisibility(View.VISIBLE);
			initUBImgView(data);
			break;
		case HechengData.TYPE_U2_B1:
			m_llUBParrent.setVisibility(View.VISIBLE);
			m_ivUL.setVisibility(View.VISIBLE);
			m_ivUR.setVisibility(View.VISIBLE);
			m_ivBL.setVisibility(View.VISIBLE);
			initUBImgView(data);
			break;
		case HechengData.TYPE_U2_B2:
			m_llUBParrent.setVisibility(View.VISIBLE);
			m_ivUL.setVisibility(View.VISIBLE);
			m_ivUR.setVisibility(View.VISIBLE);
			m_ivBL.setVisibility(View.VISIBLE);
			m_ivBR.setVisibility(View.VISIBLE);
			initUBImgView(data);
			break;


		default:
			break;
		}
	}
	public void initLRImgView(HechengData data){
		if( !TextUtils.isEmpty(data.img1)){
			Bitmap bmp = BitmapUtils.getLoacalBitmap(data.img1);
			m_ivLU.setImageBitmap(bmp);
		}
		if( !TextUtils.isEmpty(data.img2)){
			Bitmap bmp = BitmapUtils.getLoacalBitmap(data.img2);
			m_ivLB.setImageBitmap(bmp);
		}
		if( !TextUtils.isEmpty(data.img3)){
			Bitmap bmp = BitmapUtils.getLoacalBitmap(data.img3);
			m_ivRU.setImageBitmap(bmp);
		}
		if( !TextUtils.isEmpty(data.img4)){
			Bitmap bmp = BitmapUtils.getLoacalBitmap(data.img4);
			m_ivRB.setImageBitmap(bmp);
		}
	}
	
	public void initLRImgView(HechengIndexData data){
		if( data.img1 != null){
			m_ivLU.setImageBitmap(data.img1);
		}
		if( data.img2 != null){
			m_ivLB.setImageBitmap(data.img2);
		}
		if( data.img3 != null){
			m_ivRU.setImageBitmap(data.img3);
		}
		if( data.img4 != null){
			m_ivRB.setImageBitmap(data.img4);
		}
	}
	public void initUBImgView(HechengIndexData data){
		if( data.img1 != null){
			m_ivUL.setImageBitmap(data.img1);
		}
		if( data.img2 != null){
			m_ivBL.setImageBitmap(data.img2);
		}
		if( data.img3 != null){
			m_ivUR.setImageBitmap(data.img3);
		}
		if( data.img4 != null){
			m_ivBR.setImageBitmap(data.img4);
		}
	}
	public void initUBImgView(HechengData data){
		if( !TextUtils.isEmpty(data.img1)){
			Bitmap bmp = BitmapUtils.getLoacalBitmap(data.img1);
			m_ivUL.setImageBitmap(bmp);
		}
		if( !TextUtils.isEmpty(data.img2)){
			Bitmap bmp = BitmapUtils.getLoacalBitmap(data.img2);
			m_ivBL.setImageBitmap(bmp);
		}
		if( !TextUtils.isEmpty(data.img3)){
			Bitmap bmp = BitmapUtils.getLoacalBitmap(data.img3);
			m_ivUR.setImageBitmap(bmp);
		}
		if( !TextUtils.isEmpty(data.img4)){
			Bitmap bmp = BitmapUtils.getLoacalBitmap(data.img4);
			m_ivBR.setImageBitmap(bmp);
		}
	}
	public void doCallBack(int type,int pisition,HechengData data){
		if(mCallBack != null && data != null){
			data.curPosition = pisition;
			data.curType = type;
			mCallBack.onclickItem(data);
		}
	}

	private RelativeLayout m_rlParrent;

	private LinearLayout m_llLRParrent;
	private LinearLayout m_llUBParrent;
	private ImageView m_ivBg;

	private ImageView m_ivLU;
	private ImageView m_ivLB;
	private ImageView m_ivRU;
	private ImageView m_ivRB;

	private ImageView m_ivUL;
	private ImageView m_ivUR;
	private ImageView m_ivBL;
	private ImageView m_ivBR;

	private TextView m_tvLU;
	private TextView m_tvLB;
	private TextView m_tvRU;
	private TextView m_tvRB;

	private TextView m_tvUL;
	private TextView m_tvUR;
	private TextView m_tvBL;
	private TextView m_tvBR;

	public String  saveImage(int headerHeight){
		// 获取状�?栏高�?
		Rect frame = new Rect();
		act.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
		int statusBarHeight = frame.top;

		// 获取图片的宽�?
		int picWidth = m_rlParrent.getWidth();
		int picHeight = m_rlParrent.getHeight();
		Bitmap mBitmap = ScreenShot.takeScreenShot(act, 0, (statusBarHeight + headerHeight), picWidth, picHeight);
		// 保存图片（不带水印）
		return ScreenShot.savePic(mBitmap);
	}
	public interface PicHechengQiCallBack{
		/** 
		 * @param type 0:图片 1：文字
		 * @param pisition 点击的位置
		 */
		public void onclickItem(HechengData data);
	}
}
