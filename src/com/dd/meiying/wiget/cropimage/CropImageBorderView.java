package com.dd.meiying.wiget.cropimage;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

/**
 * Created by sam on 14-10-16.
 */
public class CropImageBorderView extends View {

    private int mHorizontalPadding = 0;
    private int mVerticalPadding;
    private int mWidth;
    private int mHeight;
    private int mBorderColor = Color.parseColor("#FFFFFF");
    private int mBorderWidth = 1;
    private Paint mPaint;

    public CropImageBorderView(Context context) {
        this(context, null);
    }

    public CropImageBorderView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    private float rate = 1f;
    public void setWHRate(float rate){
    	this.rate = rate;
    	invalidate();
    }
    public CropImageBorderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mHorizontalPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                mHorizontalPadding, getResources().getDisplayMetrics());
        mBorderWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                mBorderWidth, getResources().getDisplayMetrics());
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mHorizontalPadding = 0;
        if(rate >= 1){
        	 mWidth = getWidth() - 2 * mHorizontalPadding;
        	 int height = (int)(mWidth/rate);
             mVerticalPadding = (getHeight() - height) / 2;
             mPaint.setColor(Color.parseColor("#AA000000"));
             mPaint.setStyle(Paint.Style.FILL);

             canvas.drawRect(0, 0, mHorizontalPadding, getHeight(), mPaint);
             canvas.drawRect(getWidth() - mHorizontalPadding, 0, getWidth(), getHeight(), mPaint);
             canvas.drawRect(mHorizontalPadding, 0, getWidth() - mHorizontalPadding, mVerticalPadding, mPaint);
             canvas.drawRect(mHorizontalPadding, getHeight() - mVerticalPadding, getWidth() - mHorizontalPadding,
                     getHeight(), mPaint);
             mPaint.setColor(mBorderColor);
             mPaint.setStrokeWidth(mBorderWidth);
             mPaint.setStyle(Paint.Style.STROKE);
             canvas.drawRect(mHorizontalPadding, mVerticalPadding, getWidth() - mHorizontalPadding,
                     getHeight() - mVerticalPadding, mPaint);
        } else {
        	 int height= getWidth();
        	 int mWidth = (int)(height * rate);
        	 mHorizontalPadding = (getWidth() - mWidth)/2;
             mVerticalPadding = (getHeight() - height) / 2;
             mPaint.setColor(Color.parseColor("#AA000000"));
             mPaint.setStyle(Paint.Style.FILL);

             canvas.drawRect(0, 0, mHorizontalPadding, getHeight(), mPaint);
             canvas.drawRect(getWidth() - mHorizontalPadding, 0, getWidth(), getHeight(), mPaint);
             canvas.drawRect(mHorizontalPadding, 0, getWidth() - mHorizontalPadding, mVerticalPadding, mPaint);
             canvas.drawRect(mHorizontalPadding, getHeight() - mVerticalPadding, getWidth() - mHorizontalPadding,
                     getHeight(), mPaint);
             mPaint.setColor(mBorderColor);
             mPaint.setStrokeWidth(mBorderWidth);
             mPaint.setStyle(Paint.Style.STROKE);
             canvas.drawRect(mHorizontalPadding, mVerticalPadding, getWidth() - mHorizontalPadding,
                     getHeight() - mVerticalPadding, mPaint);
        }
       
    }
}
