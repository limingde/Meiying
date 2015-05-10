package com.dd.meiying.wiget.cropimage;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

/**
 * Created by sam on 14-10-17.
 */
public class CropImageLayout extends RelativeLayout {

	private CropZoomableImageView mZoomImageView;
	private CropImageBorderView mClipImageView;

	public final static int MAX_WIDTH = 2048;

	// private int mHorizontalPadding = 20;
	private Bitmap mBitmap;

	public CropImageLayout(Context context) {
		this(context, null);
	}

	public CropImageLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public CropImageLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		mZoomImageView = new CropZoomableImageView(context);
		mClipImageView = new CropImageBorderView(context);

		ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT);
		addView(mZoomImageView, params);
		addView(mClipImageView, params);

		// mZoomImageView.setImageResource(R.drawable.splash);
	}

	public Bitmap clip() {
		if (mBitmap != null) {
			return mZoomImageView.clip();
		} else {
			return null;
		}
	}

	public void setWHRate(float rate){
		mClipImageView.setWHRate(rate);
		mZoomImageView.setWHRate(rate);
	}
	public void setImageBitmap(Bitmap bitmap) {
		this.mBitmap = bitmap;
		if (mBitmap != null) {
			mZoomImageView.setImageBitmap(bitmap);
			mZoomImageView.reLayout();
			mZoomImageView.invalidate();
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		return super.onTouchEvent(event);
	}

	public void setImagePath(String filePath) {
		Bitmap b = BitmapFactory.decodeFile(filePath);
		setImageBitmap(b);
	}

	public void setRotate(int angle) {
		Matrix matrix = new Matrix();
		matrix.postRotate(angle);

		if (mBitmap != null) {

			Bitmap bm = Bitmap.createBitmap(mBitmap, 0, 0, mBitmap.getWidth(),
					mBitmap.getHeight(), matrix, true);
			mZoomImageView.setImageBitmap(bm);
			mZoomImageView.reLayout();
			mZoomImageView.invalidate();
		}
	}

}
