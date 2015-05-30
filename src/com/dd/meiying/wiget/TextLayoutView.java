package com.dd.meiying.wiget;

import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

public class TextLayoutView extends FrameLayout{
	 private GestureDetector mGestureDetector;
	 private List<CostomView> mChildView;
	 private Context mContext;
	public TextLayoutView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	public TextLayoutView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public TextLayoutView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);

		// TODO Auto-generated constructor stub
	}

	public void init(Context context){
		mGestureDetector = new GestureDetector(context, new MyOnGestureListener());
;
	}
	
	private float downX = 0;
	private float downY = 0;
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			downX = event.getX();
			downY = event.getY();
			break;
		case MotionEvent.ACTION_MOVE:
			break;
		case MotionEvent.ACTION_CANCEL:
			break;
		case MotionEvent.ACTION_UP:
			break;
		default:
			break;
		}
		return super.onTouchEvent(event);
	}
	
	
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
	}

	
	
	public void addRotateTextView(RotateTextView rtv){
		CostomView item = new CostomView();
		item.m_creatX = downX;
		item.m_creatY = downY;
		item.m_rtv = rtv;
		invalidate();
	}
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		for(CostomView view : mChildView){
			if(view != null &&  view.m_rtv != null){
				float vth = view.m_rtv.getMeasuredHeight();
				float vty = view.m_rtv.getMeasuredWidth();
				
			}
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		
	}

	
	class MyOnGestureListener extends SimpleOnGestureListener {
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
          return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
          return false;
        }

        @Override
        public void onShowPress(MotionEvent e) {
        }

        @Override
        public boolean onDown(MotionEvent e) {
           return false;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            return false;
        }

        @Override
        public boolean onDoubleTapEvent(MotionEvent e) {
            return false;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            return false;
        }
    }
	
	
	class CostomView{
		float m_creatX;
		float m_creatY;
		RotateTextView m_rtv;
	}

}
