package com.dd.meiying.abul.view;

import java.util.List;

import android.app.Activity;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.dd.meiying.R;
import com.dd.meiying.abul.BitmapCache;
import com.dd.meiying.abul.BitmapCache.ImageCallback;
import com.dd.meiying.abul.ImageItem;

public class ImageGridAdapter  extends BaseAdapter{
	final String TAG = getClass().getSimpleName();
	
	Activity act;
	List<ImageItem> dataList;
	BitmapCache cache;
	ItemCallback itemCallback = null;
	
	ImageCallback imgCallback = new ImageCallback() {
		@Override
		public void imageLoad(ImageView imageView, Bitmap bitmap, Object... params) {
			if (imageView != null && bitmap != null) {
				String url = (String) params[0];
				if (url != null && url.equals((String) imageView.getTag())) {
					((ImageView) imageView).setImageBitmap(bitmap);
				}else{
					Log.e(TAG, "callback, bmp not match");
				}
			}else{
				Log.e(TAG, "callback, bmp null");
			}
		}
	};
	
	public ImageGridAdapter(Activity act, List<ImageItem> list){
		this.act = act;
		dataList = list;
		cache = new BitmapCache();
	}
	public void setItemCallback(ItemCallback itemCallback){
		this.itemCallback = itemCallback;
	}
	
	@Override
	public int getCount() {
		int count = 0;
		if(dataList!=null){
			count = dataList.size();
		}
		return count;
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final Holder holder;
		if(convertView == null){
			holder = new Holder();
			convertView = View.inflate(act, R.layout.item_bucket_item, null);
			holder.iv = (ImageView)convertView.findViewById(R.id.image);
			holder.selected = (ImageView)convertView.findViewById(R.id.isselected);
			convertView.setTag(holder);
		}else{
			holder = (Holder) convertView.getTag();
		}
		
		final ImageItem item = dataList.get(position);
		
		holder.iv.setTag(item.imagePath);
		cache.displayBmp(holder.iv, item.thumbnailPath, item.imagePath, imgCallback);
		
		if(item.isSelected){
			holder.selected.setImageResource(R.drawable.data_select);
		}else{
			holder.selected.setImageResource(R.drawable.data_select_not);
		}
//		holder.selected.setOnClickListener(new OnClickListener() {
		holder.iv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				item.isSelected = !item.isSelected;
				if(item.isSelected){
					holder.selected.setImageResource(R.drawable.data_select);
				}else{
					holder.selected.setImageResource(R.drawable.data_select_not);
				}
				if(itemCallback != null){
					itemCallback.onChoose(position, item.isSelected);
				}
			}
		});
		
		return convertView;
	}
	
	class Holder{
		private ImageView iv;
		private ImageView selected;
	}
	
	public interface ItemCallback{
		public void onChoose(int position, boolean choosed);
	}
}
