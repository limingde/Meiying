package com.dd.meiying.wiget;

import com.dd.meiying.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.widget.TextView;

/**
 * text旋转的textview
 * @author Darren
 *
 */
public class RotateTextView extends TextView {
	private static final   String NAME_SPACE = "http://schemas.android.com/apk/res/android";

	private static final String ATTR_ROTATE = "rotate";
	private static final int DEFAULT_VALUE_ROTATE = 0;


	private static final String ATTR_TRANSLATE_X= "translateX";
	private static final String ATTR_TRANSLATE_Y = "translateY";
	private static final float DEFAULT_VALUE_TRANSLATE_X = 0f;
	private static final float DEFAULT_VALUE_TRANSLATE_Y = 0f;

	private int rotate = DEFAULT_VALUE_ROTATE;

	private float translateX = DEFAULT_VALUE_TRANSLATE_X;
	private float translateY = DEFAULT_VALUE_TRANSLATE_Y;

	
    float x_down = 0;  
    float y_down = 0;  
    PointF start = new PointF();  
    PointF mid = new PointF();  
    float oldDist = 1f;  
    float oldRotation = 0;   
  
    private static final int NONE = 0;  
    private static final int DRAG = 1;  
    private static final int ZOOM = 2;  
    int mode = NONE; 
    public RotateTextView(Context context){
    	super(context);
    }
	public RotateTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray a = context.obtainStyledAttributes(attrs,  
				R.styleable.RotateTextView); 
		rotate = a.getInteger(R.styleable.RotateTextView_rotate, DEFAULT_VALUE_ROTATE);
		translateX = a.getDimension(R.styleable.RotateTextView_translateX, DEFAULT_VALUE_TRANSLATE_X);
		translateY = a.getDimension(R.styleable.RotateTextView_translateY, DEFAULT_VALUE_TRANSLATE_Y);
	}

	public void setRotate(int rot){
		rotate = rot;
		invalidate();
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction() & MotionEvent.ACTION_MASK) {  
        case MotionEvent.ACTION_DOWN:  
            mode = DRAG;  
            x_down = event.getX();  
            y_down = event.getY();   
            break;  
        case MotionEvent.ACTION_POINTER_DOWN:  
            mode = ZOOM;  
            oldDist = spacing(event);  
            oldRotation = rotation(event); 
            midPoint(mid, event);  
            break;  
        case MotionEvent.ACTION_MOVE:  
            if (mode == ZOOM) {   
            	int rotate = (int)(rotation(event) - oldRotation);  
                setRotate(rotate);  
            } else if (mode == DRAG) {  
                    
            }  
            break;  
        case MotionEvent.ACTION_UP:  
        case MotionEvent.ACTION_POINTER_UP:  
            mode = NONE;  
            break;  
        }  
        return true;
	}
	
	public void translate(MotionEvent event){
		translateX =  event.getX() - x_down;
		translateY = event.getY() - y_down;
		invalidate();
	}
	
	// 触碰两点间距离  
    private float spacing(MotionEvent event) {  
        float x = event.getX(0) - event.getX(1);  
        float y = event.getY(0) - event.getY(1);  
        return FloatMath.sqrt(x * x + y * y);  
    }  
      
    // 取手势中心点  
    private void midPoint(PointF point, MotionEvent event) {  
        float x = event.getX(0) + event.getX(1);  
        float y = event.getY(0) + event.getY(1);  
        point.set(x / 2, y / 2);  
    }  
    
 // 取旋转角度  
    private float rotation(MotionEvent event) {  
        double delta_x = (event.getX(0) - event.getX(1));  
        double delta_y = (event.getY(0) - event.getY(1));  
        double radians = Math.atan2(delta_y, delta_x);  
        return (float) Math.toDegrees(radians);  
    }  
   
	public void setTranslateX(float tx){
		translateX = tx;
		invalidate();
	}
	public void setTranslateY(float ty){
		translateX = ty;
		invalidate();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.translate(getMeasuredWidth()*translateX, getMeasuredHeight()*translateY);
		//首先偏移在旋转，是因为，如果先旋转，本身xy坐标系也会跟着旋转，之后在偏移会不方便我们的控制，也不直观
		canvas.rotate(rotate);
		super.onDraw(canvas);
	}


}
