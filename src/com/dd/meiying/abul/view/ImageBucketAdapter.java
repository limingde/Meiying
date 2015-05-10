package com.dd.meiying.abul.view;

import java.util.List;

import android.app.Activity;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dd.meiying.R;
import com.dd.meiying.abul.BitmapCache;
import com.dd.meiying.abul.BitmapCache.ImageCallback;
import com.dd.meiying.abul.ImageBucket;

public class ImageBucketAdapter  extends BaseAdapter{
	final String TAG = getClass().getSimpleName();
	
	Activity act;
	List<ImageBucket> dataList;
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
				Log.e(TAG, "callback, bmp null");
			}
		}
	};
	
	public ImageBucketAdapter(Activity act, List<ImageBucket> list){
		this.act = act;
		dataList = list;
		cache = new BitmapCache();
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
	public View getView(int position, View convertView, ViewGroup parent) {
		Holder holder;
		if(convertView == null){
			holder = new Holder();
			convertView = View.inflate(act, R.layout.item_bucket, null);
			holder.iv = (ImageView)convertView.findViewById(R.id.image);
			holder.name = (TextView) convertView.findViewById(R.id.name);
			holder.count = (TextView) convertView.findViewById(R.id.count);
			convertView.setTag(holder);
		}else{
			holder = (Holder) convertView.getTag();
		}
		
		ImageBucket item = dataList.get(position);
		
		holder.name.setText(item.bucketName);
		holder.count.setText("("+item.count+")");
		
//		if(item.imageList != null && item.imageList.size()>0){
//			Bitmap thumb = BitmapFactory.decodeFile(item.imageList.get(0).thumbnailPath);
//			if(thumb != null){
//				holder.iv.setImageBitmap(thumb);
//			}else{
//				Log.e(TAG, "decode bmp null, imageId: "+item.imageList.get(0).imageId+", thumbPath: "+item.imageList.get(0).thumbnailPath);
//				Bitmap source = BitmapFactory.decodeFile(item.imageList.get(0).imagePath);
//				thumb = ThumbnailUtils.extractThumbnail(source, 200, 200);
//				holder.iv.setImageBitmap(thumb);
//			}
//		}else{
//			holder.iv.setBackgroundResource(R.drawable.ic_launcher);
//			Log.e(TAG, "no images in bucket "+item.bucketName);
//		}
		if(item.imageList != null && item.imageList.size()>0){
			String thumbPath = item.imageList.get(0).thumbnailPath;
			String sourcePath = item.imageList.get(0).imagePath;
			holder.iv.setTag(sourcePath);
			cache.displayBmp(holder.iv, thumbPath, sourcePath, callback);
		}else{
			holder.iv.setImageBitmap(null);
			Log.e(TAG, "no images in bucket "+item.bucketName);
		}
		
		return convertView;
	}
	
	class Holder{
		private ImageView iv;
		private TextView name;
		private TextView count;
	}
}
