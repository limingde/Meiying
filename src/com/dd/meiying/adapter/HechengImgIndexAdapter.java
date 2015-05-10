package com.dd.meiying.adapter;


import java.util.List;

import com.dd.meiying.R;
import com.dd.meiying.bean.FilterImgItem;
import com.dd.meiying.bean.HechengData;
import com.dd.meiying.bean.HechengIndexData;
import com.dd.meiying.bean.ImageTask;
import com.dd.meiying.filter.ImageFilter;
import com.dd.meiying.pichecheng.PicHeChengQi;
import com.dd.meiying.util.AsyncBitmapLoader;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

public class HechengImgIndexAdapter extends BaseAdapter {
	static final String TAG = "ClubAdapter";
	
	List<HechengIndexData> data;
	Activity act;
	private int mPosition = 0;
	private boolean mIsShowTitle = true;
	PicHeChengQi mPicHeChengQi;
	public HechengImgIndexAdapter(Activity act,List<HechengIndexData> data){
		this.act = act;
		this.data = data;
		mPicHeChengQi = new PicHeChengQi(act,null);
	}
	public void setIsShowTitle(boolean isShow){
		mIsShowTitle = isShow;
	}
	@Override
	public int getCount() {
		return data==null?0:data.size();
	}
	
	public void setCurrentPosition(int position){
		mPosition = position;
	}
	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ListHolder holder;
		if (convertView == null) {
			holder = new ListHolder();
			convertView = View.inflate(act, R.layout.item_hecheng_img_index, null);	
			holder.imgLayout = (LinearLayout) convertView.findViewById(R.id.ll_index);
			convertView.setTag(holder);
		} else{
			holder = (ListHolder) convertView.getTag();
		}
		
		HechengIndexData item = data.get(position);
		if(item != null){
			holder.imgLayout.removeAllViews();
			holder.imgLayout.addView(mPicHeChengQi.GetView(item));
		}
		
		return convertView;
	}
	
	class ListHolder{
		LinearLayout imgLayout;
		TextView tv;
		View view;
	}
}
