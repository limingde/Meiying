package com.dd.meiying.wiget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Xfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.dd.meiying.R;

/**
 * 4个角某些角是圆角，用这个比较好
 * @author Administrator
 *
 */
public class RoundCornerImageView extends ImageView {

	private float mCornerRadius = 6;  
	RectF rectF = new RectF();
	boolean leftTopCorner = true;
	boolean leftBottomCorner = true;
	boolean rightTopCorner = true;
	boolean rightBottomCorner = true;
	RectF cornerRectF = new RectF();
	RectF harfCornerRectF = new RectF();
	int cornerDirection = 0xf;
    
	public RoundCornerImageView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		init(context, attrs);
	}

	public RoundCornerImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	public RoundCornerImageView(Context context) {
		super(context);
		init(context, null);
	}
	
	private void init(Context context, AttributeSet attrs) {  
        //get corner radius
        if(attrs != null) {     
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RoundCornerImageView);
            
            if (a.hasValue(R.styleable.RoundCornerImageView_radius)) {
            	mCornerRadius= a.getDimensionPixelSize(R.styleable.RoundCornerImageView_radius, (int)mCornerRadius);
            }
            
            if (a.hasValue(R.styleable.RoundCornerImageView_cornerDirection)) {
            	cornerDirection = a.getInteger(R.styleable.RoundCornerImageView_cornerDirection,
            			cornerDirection);
    		}
            
            a.recycle();
        }else {  
            float density = context.getResources().getDisplayMetrics().density;  
            mCornerRadius = mCornerRadius*density+0.5f;
        }   
        
        leftTopCorner = ((cornerDirection & (0x1 << 0)) != 0);
        leftBottomCorner = ((cornerDirection & (0x1 << 1)) != 0);
        rightTopCorner = ((cornerDirection & (0x1 << 2)) != 0);
        rightBottomCorner = ((cornerDirection & (0x1 << 3)) != 0);
    }

	
	
	@Override
	protected void onDraw(Canvas canvas) {
		// Round some corners betch!
		Drawable maiDrawable = getDrawable();
//		float mCornerRadius = 6 * getContext().getResources()
//				.getDisplayMetrics().density;
		
//		Log.d("test", "radius: "+mCornerRadius);
//		mCornerRadius = 0;
//		if (maiDrawable != null && maiDrawable instanceof BitmapDrawable && maiDrawable.getIntrinsicWidth()>0 && maiDrawable.getIntrinsicHeight()>0 && mCornerRadius > 0) {
		if (maiDrawable != null && maiDrawable instanceof BitmapDrawable && ((BitmapDrawable)maiDrawable).getBitmap() != null && mCornerRadius > 0) {
			Paint paint = ((BitmapDrawable) maiDrawable).getPaint();
			final int color = 0xffffffff;
//			final int color = 0xffff0000;

//			final RectF rectF = new RectF(0, 0, getWidth(), getHeight());
			rectF.left = 0;
			rectF.top = 0;
			rectF.right = getWidth();
			rectF.bottom = getHeight();
			
			// Create an off-screen bitmap to the PorterDuff alpha blending to
			// work right
			int saveCount = canvas.saveLayer(rectF, null,
					Canvas.MATRIX_SAVE_FLAG | Canvas.CLIP_SAVE_FLAG
							| Canvas.HAS_ALPHA_LAYER_SAVE_FLAG
							| Canvas.FULL_COLOR_LAYER_SAVE_FLAG
							| Canvas.CLIP_TO_LAYER_SAVE_FLAG);

			paint.setAntiAlias(true);
			canvas.drawARGB(0, 0, 0, 0);
			paint.setColor(color);
			Xfermode oldMode = paint.getXfermode();
			
			drawLeftTopCornerMask(canvas, paint);
			drawLeftBottomCornerMask(canvas, paint);
			drawRightTopCornerMask(canvas, paint);
			drawRightBottomCornerMask(canvas, paint);
			// This is the paint already associated with the BitmapDrawable that
			// super draws
			paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT));
			super.onDraw(canvas);
			
			paint.setXfermode(oldMode);
			canvas.restoreToCount(saveCount);
		} else {
			super.onDraw(canvas);
		}
	}
	
	
	void drawLeftTopCornerMask(Canvas canvas, Paint paint){
		if(leftTopCorner){
			calcMaskRect(CornerDirection.left_top);
			
			canvas.drawArc(cornerRectF, 180, 90, true, paint);//水平向上为0度，按顺时针方向算
			paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT));
			canvas.drawRect(harfCornerRectF, paint);
		}
	}
	
	//得到左下角要被去掉的遮罩图形
	void drawLeftBottomCornerMask(Canvas canvas, Paint paint){
		if(leftBottomCorner){
			calcMaskRect(CornerDirection.left_bottom);
			
			canvas.drawArc(cornerRectF, 90, 90, true, paint);
			paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT));
			canvas.drawRect(harfCornerRectF, paint);
		}
	}
	
	void drawRightTopCornerMask(Canvas canvas, Paint paint){
		if(rightTopCorner){
			calcMaskRect(CornerDirection.right_top);
			
			canvas.drawArc(cornerRectF, 270, 90, true, paint);
			paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT));
			canvas.drawRect(harfCornerRectF, paint);
		}
	}
	
	void drawRightBottomCornerMask(Canvas canvas, Paint paint){
		if(rightBottomCorner){
			calcMaskRect(CornerDirection.right_bottom);
			
			canvas.drawArc(cornerRectF, 0, 90, true, paint);
			paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT));
			canvas.drawRect(harfCornerRectF, paint);
		}
	}
	
	public enum CornerDirection{
		left_top, left_bottom, right_top, right_bottom;
	}
	void calcMaskRect(CornerDirection dir){
		cornerRectF.left = rectF.left;
		cornerRectF.top = rectF.top;
		cornerRectF.right = rectF.right;
		cornerRectF.bottom = rectF.bottom;
		
		harfCornerRectF.left = cornerRectF.left;
		harfCornerRectF.top = cornerRectF.top;
		harfCornerRectF.right = cornerRectF.right;
		harfCornerRectF.bottom = cornerRectF.bottom;
		
		switch (dir) {
		case left_top:
			cornerRectF.right = cornerRectF.left+ mCornerRadius*2;
			cornerRectF.bottom = cornerRectF.top+ mCornerRadius*2;
			harfCornerRectF.right = harfCornerRectF.left+ mCornerRadius;
			harfCornerRectF.bottom = harfCornerRectF.top+ mCornerRadius;
			break;
		case left_bottom:
			cornerRectF.right = cornerRectF.left+ mCornerRadius*2;
			cornerRectF.top = cornerRectF.bottom- mCornerRadius*2;
			harfCornerRectF.right = harfCornerRectF.left+ mCornerRadius;
			harfCornerRectF.top = harfCornerRectF.bottom- mCornerRadius;
			break;
		case right_top:
			cornerRectF.left = cornerRectF.right- mCornerRadius*2;
			cornerRectF.bottom = cornerRectF.top+ mCornerRadius*2;
			harfCornerRectF.left = harfCornerRectF.right- mCornerRadius;
			harfCornerRectF.bottom = harfCornerRectF.top+ mCornerRadius;
			break;
		case right_bottom:
			cornerRectF.left = cornerRectF.right- mCornerRadius*2;
			cornerRectF.top = cornerRectF.bottom- mCornerRadius*2;
			harfCornerRectF.left = harfCornerRectF.right- mCornerRadius;
			harfCornerRectF.top = harfCornerRectF.bottom- mCornerRadius;
			break;
		default:
			break;
		}
	}
	
	public void setCornerDirection(CornerDirection dir, boolean set){
		if(dir != null){
			switch (dir) {
			case left_top:
				leftTopCorner = set;
				break;
			case left_bottom:
				leftBottomCorner = set;
				break;
			case right_top:
				rightTopCorner = set;
				break;
			case right_bottom:
				rightBottomCorner = set;
				break;
			default:
				break;
			}
			invalidate();
		}
	}
}
