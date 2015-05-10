package com.dd.meiying.abul;

import java.io.File;
import java.io.FileInputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.dd.meiying.util.FileUtil;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.support.v4.util.LruCache;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

public class BitmapCache {
	final String TAG = getClass().getSimpleName();

	int maxW = 128;
	int maxH = 128;

	ExecutorService fixedThreadPool = Executors.newFixedThreadPool(2);
	//	ThreadPoolExecutor fixedThreadPool;
	int corePoolSize = 1, maximumPoolSize = 1, workQueueSize = 64;

	//	private HashMap<String, SoftReference<Bitmap>> imageCache = new HashMap<String, SoftReference<Bitmap>>();
	private LruCache<String, Bitmap> imageCache;
	{
		initMemoryCache();
		initThreadPool();
	}

	public void setMaxWidth(int width) {
		maxW = width;
	}
	
	public void setMaxHeight(int height) {
		maxH = height;
	}
	
	void initThreadPool(){
		//		fixedThreadPool = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, 0, TimeUnit.SECONDS,
		//	            new ArrayBlockingQueue<Runnable>(workQueueSize));
		//    	fixedThreadPool.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardOldestPolicy());
	}
	void initMemoryCache(){
		// Get max available VM memory, exceeding this amount will throw an
		// OutOfMemory exception. Stored in kilobytes as LruCache takes an
		// int in its constructor.
		final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

		// Use 1/8th of the available memory for this memory cache.
		final int cacheSize = maxMemory / 8;


		imageCache = new LruCache<String, Bitmap>(cacheSize){
			@Override
			protected int sizeOf(String key, Bitmap bitmap) {
				// The cache size will be measured in kilobytes rather than
				// number of items.
				//	            return bitmap.getByteCount() / 1024;
				if (bitmap == null) {

					return 0;
				}

				int size = bitmap.getRowBytes()*bitmap.getHeight() / 1024;

				return size;
			}
		};
		//	    imageCache = new LruCache<String, Bitmap>(cacheSize);
	}
	void put(String path, Bitmap bmp){
		if(!TextUtils.isEmpty(path) && bmp != null){
			//			imageCache.put(path, new SoftReference<Bitmap>(bmp));
			imageCache.put(path, bmp);
		}
	}
	Bitmap get(String key){
		try{
			//			if(imageCache.containsKey(path)){
			//				SoftReference<Bitmap> reference = imageCache.get(path);
			//				return reference.get();
			//			}
			return imageCache.get(key);
		}catch (Throwable e) {
			Log.e(TAG, "get: "+key, e);
		}
		return null;
	}

	public void displayBmp(final ImageView iv, final String thumbPath, final String sourcePath, final ImageCallback callback){
		if(TextUtils.isEmpty(thumbPath) && TextUtils.isEmpty(sourcePath)){
			
			return;
		}

		final String path;
		final boolean isThumbPath;
		if(!TextUtils.isEmpty(thumbPath) && isBitmapExist(thumbPath)){
			path = thumbPath;
			isThumbPath = true;

		}else if(!TextUtils.isEmpty(sourcePath) && isBitmapExist(sourcePath)){
			path = sourcePath;
			isThumbPath = false;

		}else{

			iv.setImageBitmap(null);
			return;
		}

		Bitmap bmp = get(path);
		if (bmp != null) {
			if (callback != null) {
				callback.imageLoad(iv, bmp, sourcePath);
			}
			return;
		}
		iv.setImageBitmap(null);

		//			new Thread(){
		fixedThreadPool.execute(new Runnable() {

			public void run() {
				final Bitmap thumb;
				if(isThumbPath){
					//如果缩略图太大，就再缩小一点
					BitmapFactory.Options o = null;
					FileInputStream fis = null;
					try{
						o = new BitmapFactory.Options();
						o.inJustDecodeBounds = true;
						fis = new FileInputStream(path);
						BitmapFactory.decodeStream(fis, null, o);
					} catch (Exception e) {
						e.printStackTrace();
						o = null;
					} finally {
						FileUtil.closeIO(fis);
					}
					if(o != null && (o.outWidth > maxW || o.outHeight > maxH)){
						BitmapFactory.Options opts = getResizeBmpOption(new File(path), maxW, maxH);
						thumb = BitmapFactory.decodeFile(path, opts);
					}else{
						thumb = BitmapFactory.decodeFile(path);
					}
				}else{
					BitmapFactory.Options opts = getResizeBmpOption(new File(path), maxW, maxH);
					thumb = BitmapFactory.decodeFile(path, opts);
				}
//				Log.d(TAG, isThumbPath+", decodeFile "+path+", "+thumb);
				put(path, thumb);

				if(callback != null){
					h.postDelayed(new Runnable() {
						@Override
						public void run() {
							callback.imageLoad(iv, thumb, sourcePath);
						}
					}, 20);
				}
			}
			//			}.start();
		});

	}
	Handler h = new Handler();

	public BitmapFactory.Options getResizeBmpOption(File f, int requiredSizeW, int requiredSizeH){
		try {
			BitmapFactory.Options o = new BitmapFactory.Options();			
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(new FileInputStream(f), null, o);

			// Find the correct scale value. It should be the power of 2.
			int scale = 1;

			if (requiredSizeW > 0 || requiredSizeH > 0) {
				while (true) {
					int scaleSizeW = o.outWidth / scale;
					int scaleSizeH = o.outHeight / scale;
					if ((!(requiredSizeW > 0) || (requiredSizeW > 0 && scaleSizeW <= requiredSizeW))
							&& (!(requiredSizeH >0) || (requiredSizeH >0 && scaleSizeH <= requiredSizeH))) {
						break;
					}
					scale += scale;
				}
			}

//			Log.d(TAG, "after calculate, w: " + o.outWidth + ", h: " + o.outHeight
//					+ ", scale: " + scale + ", requiredSizeW: " + requiredSizeW
//					+ ", requiredSizeH: " + requiredSizeH);

			// decode with inSampleSize
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			// o2.inDensity = 1;
			// o2.inTargetDensity = o2.inDensity;
			o2.inSampleSize = scale;
			o2.inPurgeable = true;
			o2.inPreferredConfig = Config.RGB_565;
			//		o2.inPreferredConfig = Config.ARGB_8888;

			//			Bitmap tmpBmp = BitmapFactory.decodeStream(new FileInputStream(f),
			//					null, o2);
			return o2;
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}
	public static boolean isBitmapExist(String imgLocalPath){
		File temp = new File(imgLocalPath);
		if(temp.exists() && temp.isFile() && temp.length()>100){
			return true;
		}
		return false;
	}
	public interface ImageCallback {
		public void imageLoad(ImageView imageView, Bitmap bitmap, Object... params);
	}
}
