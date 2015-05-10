package com.dd.meiying.wiget;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dd.meiying.R;
import com.dd.meiying.bean.TagResource;

/**
 * 自定义用于添加标签的RelativeLayout
 * 
 * @author Jasen
 * @version v4.1.0
 * 
 */
public class TagRelativeLayout extends RelativeLayout{

	private Context mContext;
	public List<TagResource> mTagResList;
	private Animation mAnimation;
	public static final int TYPE_CUSTOM = 1;
	public static final int TYPE_MAKEUP = 2;

	public TagRelativeLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		this.mContext = context;
	}

	public TagRelativeLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		this.mContext = context;
	}

	public TagRelativeLayout(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		this.mContext = context;
	}

	/**
	 * * 发话�?编辑图片添加标签
	 * @param tag 标签数据结构
	 * @param dx 横向坐标
	 * @param dy 纵向坐标
	 */
	public void addTag(TagResource tag, final int dx, final int dy) {
		if (mTagResList == null) {
			mTagResList = new ArrayList<TagResource>();
		}
		mAnimation = AnimationUtils.loadAnimation(mContext, R.anim.tag_out);
		final long mId = System.currentTimeMillis() + (long) (Math.random() * 100);
		final TagResource mTagRes = new TagResource();
		mTagRes.mId = mId;
		mTagRes.type = tag.type;
		mTagRes.title = tag.title;
		mTagRes.product_slug = tag.product_slug;
		LayoutInflater mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = mInflater.inflate(R.layout.image_tag_layout, null);
		final RelativeLayout layout = (RelativeLayout) view.findViewById(R.id.image_tag_layout);
		final FrameLayout m_flLeftGroup = (FrameLayout) view.findViewById(R.id.tag_iv_left_group);
		final ImageView m_ivLeft = (ImageView) view.findViewById(R.id.tag_iv_left);
		final ImageView m_ivLeftOut = (ImageView) view.findViewById(R.id.tag_iv_left_out);
		final FrameLayout m_flRightGroup = (FrameLayout) view.findViewById(R.id.tag_iv_right_group);
		final ImageView m_ivRight = (ImageView) view.findViewById(R.id.tag_iv_right);
		final ImageView m_ivRightOut = (ImageView) view.findViewById(R.id.tag_iv_right_out);

		AnimView item = new AnimView();
		item.mId = mId;
		item.m_ivLeftOut = m_ivLeftOut;
		item.m_ivRightOut = m_ivRightOut;
		mAnimView.add(item);
		m_ivLeft.setBackgroundResource(getTagType(tag.type));
		m_ivRight.setBackgroundResource(getTagType(tag.type));
		final TextView m_tvTag = (TextView) view.findViewById(R.id.tag_text);
		m_tvTag.setText(tag.title);

		layout.setOnTouchListener(new OnTouchListener() {
			int mCurrentInScreenX = 0;
			int mCurrentInScreenY = 0;
			int mDownInScreenX = 0;
			int mDownInScreenY = 0;
			int mUpInScreenX = 0;
			int mUpInScreenY = 0;

			@Override
			public boolean onTouch(final View v, MotionEvent event) {

				if (v.getId() == R.id.image_tag_layout) {
					switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						mCurrentInScreenX = (int) event.getRawX();
						mCurrentInScreenY = (int) event.getRawY();
						mDownInScreenX = (int) event.getRawX();
						mDownInScreenY = (int) event.getRawY();
						break;
					case MotionEvent.ACTION_MOVE:
						int x = (int) event.getRawX();
						int y = (int) event.getRawY();
						int mx = x - mDownInScreenX;
						int my = y - mDownInScreenY;

						moveTag(v, mx, my, mId);
						// 标签到达边界，再有往外移动的动作，才执行
						tagTextBgChange(v, m_tvTag, m_flLeftGroup, m_ivLeftOut, 
								m_flRightGroup, m_ivRightOut, Math.abs(mx) > 10);

						mDownInScreenX = (int) event.getRawX();
						mDownInScreenY = (int) event.getRawY();
						break;
					case MotionEvent.ACTION_UP:
						mUpInScreenX = (int) event.getRawX();
						mUpInScreenY = (int) event.getRawY();
						// 模拟监听点击事件
						if (Math.abs(mUpInScreenX - mCurrentInScreenX) < 5 && Math.abs(mUpInScreenY - mCurrentInScreenY) < 5) {
							AlertDialog.Builder ab = new AlertDialog.Builder(mContext);
							ab.setTitle("确认删除");
							ab.setPositiveButton("确认", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									deleteTag(v, mId);
								}
							});
							ab.setNegativeButton("取消", null);
							ab.show();
						}
						break;
					}
				}
				return true;
			}
		});

		this.addView(layout);
		layout.setVisibility(View.INVISIBLE);

		// 保证 setPosition 方法�?addView 方法后调�?
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				setPosition(layout, m_tvTag, m_flLeftGroup, m_ivLeftOut, 
						m_flRightGroup, m_ivRightOut, mAnimation, dx, dy);
			}
		}, 100);

		final float b[] = coverToPercent(dx, dy);
		mTagRes.xscale = b[0];
		mTagRes.yscale = b[1];
		mTagResList.add(mTagRes);
	}

	/**
	 * 话题详情�?添加单个标签
	 * @param content
	 * @param percentX 横向坐标占父view的百分比
	 * @param percentY 纵向坐标占父view的百分比
	 */
	public void addTag(int type, final String id, final String content, float percentX, float percentY) {
		LayoutInflater mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = mInflater.inflate(R.layout.image_tag_layout, null);
		mAnimation = AnimationUtils.loadAnimation(mContext, R.anim.tag_out);
		final RelativeLayout layout = (RelativeLayout) view.findViewById(R.id.image_tag_layout);
		final FrameLayout m_flLeftGroup = (FrameLayout) view.findViewById(R.id.tag_iv_left_group);
		final ImageView m_ivLeft = (ImageView) view.findViewById(R.id.tag_iv_left);
		final ImageView m_ivLeftOut = (ImageView) view.findViewById(R.id.tag_iv_left_out);
		final FrameLayout m_flRightGroup = (FrameLayout) view.findViewById(R.id.tag_iv_right_group);
		final ImageView m_ivRight = (ImageView) view.findViewById(R.id.tag_iv_right);
		final ImageView m_ivRightOut = (ImageView) view.findViewById(R.id.tag_iv_right_out);
		m_ivLeft.setBackgroundResource(getTagType(type));
		m_ivRight.setBackgroundResource(getTagType(type));

		final TextView m_tvTag = (TextView) view.findViewById(R.id.tag_text);
		m_tvTag.setText(content);

		m_tvTag.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 跳转
//				mContext.startActivity(TagVTalkListActivity.getStartActIntent(mContext, id));
			} 
		});

		this.addView(layout);
		layout.setVisibility(View.INVISIBLE);
		final int a[] = coverToCoordinates(percentX, percentY);

		// 保证 setPosition 方法�?addView 方法后调�?
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				setPosition(layout, m_tvTag, m_flLeftGroup, m_ivLeftOut, 
						m_flRightGroup, m_ivRightOut, mAnimation, a[0], a[1]);
			}
		}, 100);
	}

	/**
	 * 话题详情�?批量添加标签
	 * @param tagResList
	 */
	public void addAllTags(List<TagResource> tagResList) {
		if (tagResList == null) {
			return;
		} else {
			for (TagResource tagResource : tagResList) {
				if (!TextUtils.isEmpty(tagResource.title) && !TextUtils.isEmpty(tagResource.id)) {
					addTag(tagResource.type, tagResource.id, tagResource.title, 
							tagResource.xscale, tagResource.yscale);
				}
			}
		}
	}

	/**
	 * 删除标签
	 * @param v
	 * @param id 标签id
	 */
	private void deleteTag(View v, long id) {
		this.removeView(v);
		if (mTagResList != null) {
			for (int i = 0; i < mTagResList.size(); i++) {
				if (mTagResList.get(i).mId == id) {
					mTagResList.remove(i);

					if(i >= 0 && i< mAnimView.size()){
						if(mAnimView.get(i).mId == id){
							mAnimView.remove(i);
						}
					}
					break;
				}
			}
		}
	}

	/**
	 * 初始化，设置标签
	 * @param v
	 * @param tv
	 * @param dx
	 * @param dy
	 */
	private void setPosition(View v, TextView tv, FrameLayout m_flLeftGroup, final ImageView m_ivLeftOut, 
			FrameLayout m_flRightGroup, final ImageView m_ivRightOut, final Animation mAnimation, int dx, int dy) {
		if(mParentWidth == 0){
			mParentWidth =  this.getWidth();
		}
		if(mParentHeight == 0){
			mParentHeight = this.getHeight();
		}
		int l, t, r, b;
		if (dx > (mParentWidth - dx)) {
			// 左边区域大于右边区域，箭头指向右�?
			if (dx >= v.getWidth()) {
				r = dx;
				l = dx - v.getWidth();
				m_flLeftGroup.setVisibility(GONE);
				m_flRightGroup.setVisibility(VISIBLE);
				m_ivRightOut.startAnimation(mAnimation);
				tv.setBackgroundResource(R.drawable.tag_arrow_right);
			} else {
				l = 0;
				r = v.getWidth();
				m_flLeftGroup.setVisibility(GONE);
				m_flRightGroup.setVisibility(VISIBLE);
				m_ivRightOut.startAnimation(mAnimation);
				tv.setBackgroundResource(R.drawable.tag_arrow_right);
			}
		} else {
			// 右边区域大于左边区域，箭头指向左�?
			if ((mParentWidth - dx) >= v.getWidth()) {
				l = dx;
				r = dx + v.getWidth();
				m_flLeftGroup.setVisibility(VISIBLE);
				m_flRightGroup.setVisibility(GONE);
				m_ivLeftOut.startAnimation(mAnimation);
				tv.setBackgroundResource(R.drawable.tag_arrow_left);
			} else {
				r = mParentWidth;
				l = mParentWidth - v.getWidth();
				m_flLeftGroup.setVisibility(VISIBLE);
				m_flRightGroup.setVisibility(GONE);
				m_ivLeftOut.startAnimation(mAnimation);
				tv.setBackgroundResource(R.drawable.tag_arrow_left);
			}
		}

		if ((mParentHeight - dy) >= v.getHeight()) {
			t = dy;
			b = l + v.getHeight();
		} else {
			b = mParentHeight;
			t = mParentHeight - v.getHeight();
		}
		v.layout(l, t, r, b);
		RelativeLayout.LayoutParams params = (android.widget.RelativeLayout.LayoutParams) v.getLayoutParams();
		params.leftMargin = l;
		params.topMargin = t;
		v.setLayoutParams(params);
		v.setVisibility(View.VISIBLE);
	}

	/**
	 * 移动标签
	 * @param v
	 * @param dx 横向移动距离
	 * @param dy 纵向移动距离
	 * @param id 标签id
	 */
	private void moveTag(View v, int dx, int dy, long id) {
		int parentWidth = this.getWidth();
		int parentHeight = this.getHeight();
		int l = v.getLeft() + dx;
		int t = v.getTop() + dy;
		if (l < 0) {
			l = 0;
		} else if ((l + v.getWidth()) >= parentWidth) {
			l = parentWidth - v.getWidth();
		}
		if (t < 0) {
			t = 0;
		} else if ((t + v.getHeight()) >= parentHeight) {
			t = parentHeight - v.getHeight();
		}
		int r = l + v.getWidth();
		int b = t + v.getHeight();
		v.layout(l, t, r, b);
		RelativeLayout.LayoutParams params = (android.widget.RelativeLayout.LayoutParams) v.getLayoutParams();
		params.leftMargin = l;
		params.topMargin = t;
		v.setLayoutParams(params);
		v.setVisibility(View.VISIBLE);

		refreshTagRes(l, t, id);
	}

	/**
	 * 标签达到边界时，再往外滑动，标签反向
	 * @param v
	 * @param tv
	 * @param isNeedChange
	 */
	private void tagTextBgChange(View v, final TextView tv, final FrameLayout m_flLeftGroup, final ImageView m_ivLeftOut,
			final FrameLayout m_flRightGroup, final ImageView m_ivRightOut, boolean isNeedChange) {
		if (v.getRight() == this.getWidth() && isNeedChange) {
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					m_flLeftGroup.setVisibility(GONE);
					m_flRightGroup.setVisibility(VISIBLE);
					m_ivRightOut.startAnimation(mAnimation);
					tv.setBackgroundResource(R.drawable.tag_arrow_right);
				}
			}, 100);
		} else if (v.getLeft() == 0 && isNeedChange) {
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					m_flLeftGroup.setVisibility(VISIBLE);
					m_flRightGroup.setVisibility(GONE);
					m_ivLeftOut.startAnimation(mAnimation);
					tv.setBackgroundResource(R.drawable.tag_arrow_left);
				}
			}, 100);
		}
	}


	private int mParentWidth = 0;
	private int mParentHeight = 0;
	public void setTagLayoutWH(int w,int h){
		mParentWidth = w;
		mParentHeight = h;
	}

	/**
	 *
	 * 百分比换算成坐标
	 * @param percentX
	 * @param percentY
	 */
	private int[] coverToCoordinates(float percentX, float percentY) {
		int a[] = new int[2];
		if(mParentWidth == 0){
			mParentWidth =  this.getWidth();
		}
		if(mParentHeight == 0){
			mParentHeight = this.getHeight();
		}
		a[0] = (int) (mParentWidth * percentX);
		a[1] = (int) (mParentHeight * percentY);
		return a;
	}

	/**
	 *  坐标换算成百分比
	 * @param dx
	 * @param dy
	 */
	private float[] coverToPercent(int dx, int dy) {
		float b[] = new float[2];
		if(mParentWidth == 0){
			mParentWidth =  this.getWidth();
		}
		if(mParentHeight == 0){
			mParentHeight = this.getHeight();
		}
		
		b[0] = (float)dx / (float)mParentWidth;
		b[1] = (float)dy / (float)mParentHeight;
		return b;
	}

	/**
	 * 移动标签时，更新标签坐标
	 * @param dx
	 * @param dy
	 * @param id 标签id
	 */
	private void refreshTagRes(int dx, int dy, long id) {
		if (mTagResList != null) {
			for (int i = 0; i < mTagResList.size(); i++) {
				if (mTagResList.get(i).mId == id) {
					mTagResList.get(i).xscale = (float) (dx / this.getWidth());
					mTagResList.get(i).yscale = (float) (dy / this.getHeight());
					break;
				}
			}
		}
	}

	// 获取标签类型相对应的锚点图标
	private int getTagType(int type) {
		if (type == TYPE_CUSTOM) {
			// 自定�?
			return R.drawable.icon_label_custom_small;
		} else {
			// 化妆�?
			return R.drawable.icon_label_makeup_small;
		}
	}

	/**
	 * 截图前，先停止锚点动�?
	 */
	@SuppressLint("NewApi")
	public void clearAllAnim() {
		if (mTagResList != null) {
			for (int i = 0; i < mTagResList.size(); i++) {
				mAnimView.get(i).m_ivLeftOut.clearAnimation();
				mAnimView.get(i).m_ivRightOut.clearAnimation();
				mAnimView.get(i).m_ivLeftOut.setAlpha((float)0.3);
				mAnimView.get(i).m_ivRightOut.setAlpha((float)0.3);
			}
		}
	}

	//保存 有动画的 view   因为截屏�?�?��把动画去�? �?��要循环遍历该 数组 去除�?��动画
	private List<AnimView> mAnimView = new ArrayList<TagRelativeLayout.AnimView>();
	class AnimView{
		// 用于控制锚点动画
		public long mId;
		public ImageView m_ivLeftOut;
		public ImageView m_ivRightOut;
	}
}
