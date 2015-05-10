package com.dd.meiying.adapter;

import java.util.List;

import android.app.Activity;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.dd.meiying.R;
import com.dd.meiying.abul.BitmapCache;
import com.dd.meiying.abul.BitmapCache.ImageCallback;
import com.dd.meiying.abul.ImageItem;
import com.dd.meiying.wiget.SquareWrapHImageView;

public class ImageAlbumGridAdapter extends BaseAdapter{
	final String TAG = getClass().getSimpleName();
	
	Activity act;
	List<ImageItem> dataList;
	BitmapCache cache;
	
	ImageCallback callback = new ImageCallback() {
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
				((ImageView) imageView).setImageDrawable(act.getResources().getDrawable(R.drawable.topic_icon_photo_default));
				((ImageView) imageView).setScaleType(ScaleType.CENTER);
				Log.e(TAG, "callback, bmp null");
			}
		}
	};
	
	public ImageAlbumGridAdapter(Activity act, List<ImageItem> list){
		this.act = act;
		dataList = list;
		cache = new BitmapCache();
		cache.setMaxWidth(256);
		cache.setMaxHeight(256);
	}
	
	@Override
	public int getCount() {
		int count = 0;
		if(dataList != null){
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
	public View getView(int position, View convertView, ViewGroup parent) {
		if (position == 0) {
			if (convertView == null || convertView.getId() != R.layout.item_get_camera_pic_view) {
				convertView = View.inflate(act, R.layout.item_get_camera_pic_view, null);
			}
//			int tem = convertView.getMeasuredWidth();
			int mScreenWidth = act.getWindowManager().getDefaultDisplay().getWidth();
			int tmpVertivalSpace = (int)act.getResources().getDimension(R.dimen.px_18);
			int tmpWidth = (mScreenWidth / 3) - tmpVertivalSpace;
			AbsListView.LayoutParams picParams = new AbsListView.LayoutParams(tmpWidth, tmpWidth);
			convertView.setLayoutParams(picParams);
			return convertView;
		} else {		
			return getPicView(position, convertView, parent);
		}
	}
	
	private View getPicView(int position, View convertView, ViewGroup parent) {

		Holder holder;
		if (convertView == null || convertView.getId() != R.layout.item_album_grid_view) {
			holder = new Holder();
			convertView = View.inflate(act, R.layout.item_album_grid_view, null);
			holder.iv = (SquareWrapHImageView) convertView.findViewById(R.id.image);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}

		ImageItem item = dataList.get(position);

		if (item != null) {
			String thumbPath = item.thumbnailPath;
			String sourcePath = item.imagePath;
			holder.iv.setTag(sourcePath);
			cache.displayBmp(holder.iv, thumbPath, sourcePath, callback);
		} else {
			holder.iv.setImageDrawable(act.getResources().getDrawable(R.drawable.topic_icon_photo_default));
			holder.iv.setScaleType(ScaleType.CENTER);
		}

		return convertView;
	}
	
	class Holder{
		private SquareWrapHImageView iv;
	}
}
