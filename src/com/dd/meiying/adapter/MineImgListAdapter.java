package com.dd.meiying.adapter;


import java.util.List;

import com.dd.meiying.R;
import com.dd.meiying.bean.FilterImgItem;
import com.dd.meiying.bean.ImageTask;
import com.dd.meiying.filter.ImageFilter;
import com.dd.meiying.util.AsyncBitmapLoader;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

public class MineImgListAdapter extends BaseAdapter {
	static final String TAG = "ClubAdapter";
	
	List<ImageTask> data;
	Activity act;
	private Bitmap mbmp;
	private int mPosition = 0;
	private boolean mIsShowTitle = true;
	private AsyncBitmapLoader mAsyncBitmapLoader;
	public MineImgListAdapter(Activity act,List<ImageTask> data){
		this.act = act;
		this.data = data;
		mAsyncBitmapLoader = new AsyncBitmapLoader();
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
			convertView = View.inflate(act, R.layout.item_img_filter, null);	
			holder.img = (ImageView) convertView.findViewById(R.id.iv_img);
			holder.tv = (TextView) convertView.findViewById(R.id.tv_name);
			holder.view = convertView.findViewById(R.id.view);
			convertView.setTag(holder);
		} else{
			holder = (ListHolder) convertView.getTag();
		}
		
		ImageTask item = null;
		if(data != null && data.size() > position && position >= 0){
			item = data.get(position);
			if(item != null){
				Bitmap bmp = mAsyncBitmapLoader.decodeLocalFile(item.path, 169, 169, 256);
				holder.img.setImageBitmap(bmp);
				holder.tv.setText(String.valueOf(position + 1));
				if(mIsShowTitle){
					holder.tv.setVisibility(View.VISIBLE);
				} else {
					holder.tv.setVisibility(View.GONE);
				}
				if(mPosition == position ){
					holder.tv.setTextColor(act.getResources().getColor(R.color.color_f15b82));
					holder.view.setVisibility(View.VISIBLE);
				} else{
					holder.view.setVisibility(View.GONE);
					holder.tv.setTextColor(act.getResources().getColor(R.color.white_50));
				}
			}
		}
		
		return convertView;
	}
	
	class ListHolder{
		ImageView img;
		TextView tv;
		View view;
	}
}
