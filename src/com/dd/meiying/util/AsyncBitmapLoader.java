package com.dd.meiying.util;

import java.io.File;
import java.net.URL;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.dd.meiying.MeiyingAplication;

public class AsyncBitmapLoader {

	public final static String TAG = "AsyncBitmapLoader";

	private static final int SLEEP_TIME_FOR_GC_MS = 1000;

	public int maxW = 1024;
	public int maxH = 1024;
	public int mCriticalW = 720;
	private int qualityDefault = 80;
	final public int maxDownloadBmpFileSize = 0;// 从网上下载时，设置这个值，就对图片质量进行处理
	final long maxBmpSize = Runtime.getRuntime().maxMemory() / 10;// 加载到内存的单张图片所允许的最大占用内存，超过则通过设置quality来压缩
	public Config inPreferredConfig = Config.RGB_565;// 如果用RGB_565，某些图片的白色部分会有变色，不过换成ARGB_8888又比较占内存

	boolean isLocalBitmap = false;// 传进来的是url还是图片的本地path

	private float mRoundPx = 0;
	public static String ImgCacheDir = Environment
			.getExternalStorageDirectory().getAbsolutePath()
			+ "/meila/cache/image/";
	private boolean mIsCache = true;
	public boolean needSetNullIfNotHitCache = false;// 图片没在内存缓存中时，是否立即设置图片为null

	private String mCacheExtName = null;

	// private byte[] lock = new byte[0];
	// private boolean isLocked = false;

	// class CacheItem{
	// String path;
	// SoftReference<Bitmap> bmp;
	// int maxW;
	// int maxH;
	// }

	/**
	 * 创建一个定长线程池，可控制线程最大并发数，超出的线程会在队列中等待。
	 */
	// ExecutorService fixedThreadPool = Executors.newFixedThreadPool(8);
	int corePoolSize = 2, maximumPoolSize = 4, workQueueSize = 32;
	ThreadPoolExecutor fixedThreadPool;

	public AsyncBitmapLoader() {
		init();
	}

	public AsyncBitmapLoader(String cacheExtName) {
		mCacheExtName = cacheExtName;
		init();
	}

	public AsyncBitmapLoader(float roundPx, int width) {
		mRoundPx = roundPx;
		maxW = width;
		init();
	}

	public AsyncBitmapLoader(float roundPx, int width, boolean isCache) {
		mRoundPx = roundPx;
		maxW = width;
		mIsCache = isCache;
		init();
	}

	public AsyncBitmapLoader(int corePoolSize, int maximumPoolSize,
			int workQueueSize) {
		if (corePoolSize > 0) {
			this.corePoolSize = corePoolSize;
		}
		if (maximumPoolSize >= corePoolSize) {
			this.maximumPoolSize = maximumPoolSize;
		}
		if (workQueueSize >= 0) {
			this.workQueueSize = workQueueSize;
		}
		init();
	}

	void init() {
		getWH();
		initThreadPool();
	}

	void initThreadPool() {
		fixedThreadPool = new ThreadPoolExecutor(corePoolSize, maximumPoolSize,
				0, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(
						workQueueSize));
		fixedThreadPool
		.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardOldestPolicy());
	}

	public void setMaxH(int h) {
		maxH = h;
	}

	public void setMaxW(int w) {
		maxW = w;
	}

	void getWH() {
		// try{
		// maxW =
		// MeilaApplication.CONTEXT.getResources().getDisplayMetrics().widthPixels*2;
		// maxH =
		// MeilaApplication.CONTEXT.getResources().getDisplayMetrics().heightPixels*2;
		// }catch (Exception e) {
		// MeilaLog.e(TAG, "cannot get DisplayMetrics");
		// }
	}

	public void setLocalBitmapFlag(boolean isLocal) {
		this.isLocalBitmap = isLocal;
	}

	public boolean isBitmapExist(String imgLocalPath) {
		try {
			File temp = new File(imgLocalPath);
			if (temp.exists() && temp.isFile() && temp.length() > 100) {
				return true;
			}
		} catch (Exception e) {
		}
		return false;
	}

	public Bitmap loadBitmap(final View imageView, final String url,
			final ImageCallBackWithParams callbackWithParams,
			final Object... params) {
		return loadBitmap(imageView, url, callbackWithParams, null, params);
	}
	public void decodeLocalFileFromLocalPath(final View imageView,
			final String path,
			final ImageCallBackWithParams callbackWithParams,
			final Object... params) {

		if (imageView != null && path != null) {
			imageView.setTag(path);
		}

		final int maxFileSize = qualityDefault >= 100 ? 0
				: maxDownloadBmpFileSize;

		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				Bitmap bmp = (Bitmap) msg.obj;

				if (callbackWithParams != null) {
					callbackWithParams.imageLoad(imageView, bmp, params);
				}
			}
		};

		if (TextUtils.isEmpty(path)) {
			// MeilaLog.e(TAG, new Exception("invalid img url: "+url));
			Message msg = handler.obtainMessage(0, null);
			handler.sendMessage(msg);
			return;
		}

		fixedThreadPool.execute(new Runnable() {
			@Override
			public void run() {
				Bitmap bitmap = null;
				if(MeiyingAplication.mScreenWidth <= mCriticalW){
					bitmap = decodeLocalFile(path, maxW > 0 ? maxW : 0,
							maxH > 0 ? maxH : 0, maxFileSize);
				} else {
					bitmap = decodeLocalFile(path, 0,0, maxFileSize);
				}
				Message msg = handler.obtainMessage(0, bitmap);
				handler.sendMessage(msg);
			}
		});
	}
	/**
	 * 加载本地图片
	 * 
	 * @param path
	 * @param requiredSizeW
	 *            请求图片的最大宽，如果为0 ，则不限制
	 * @param requiredSizeH
	 *            请求图片的最大高，如果为0 ，则不限制
	 * @return
	 */
	public Bitmap decodeLocalFile(String path, int requiredSizeW,
			int requiredSizeH, int maxFileSize) {
		File f = new File(path);
		return decodeLocalFile(f, requiredSizeW, requiredSizeH, maxFileSize);
	}

	public String getBitmapName(String imageURL) {
		// final String bitmapName;
		// if (mCacheExtName != null) {
		// bitmapName = mCacheExtName
		// + imageURL.substring(imageURL.lastIndexOf("/") + 1);
		// } else {
		// bitmapName = imageURL.substring(imageURL.lastIndexOf("/") + 1);
		// }

		String tmp = parseImgUrlPrefix(imageURL);
		return TextUtils.isEmpty(tmp) ? null : Md5Util.strToMd5(tmp);
	}

	public String parseImgUrlPrefix(String url) {
		String parsedUrl = url;
		try {
			if (TextUtils.isEmpty(url)) {
				return url;
			}

			parsedUrl = url;


			URL temp_url = new URL(parsedUrl);
			parsedUrl = temp_url.toString();
		} catch (Exception e) {
		}
		return parsedUrl;
	}

	/**
	 * decodes image and scales it to reduce memory consumption
	 * 
	 * @param f
	 * @param requiredSizeW
	 *            请求图片的最大宽，如果为0 ，则不限制
	 * @param requiredSizeH
	 *            请求图片的最大高，如果为0 ，则不限制
	 * @return
	 */
	public Bitmap decodeLocalFile(File f, int requiredSizeW, int requiredSizeH,
			int maxFileSize) {
		try {
			String readPath = f.getAbsolutePath();

			// 对图片质量进行处理
			int bmpSize = getBitmapSize(readPath);
			if (bmpSize > maxBmpSize) {
				String savePath = getBitmapName(readPath);// 压缩图片放到图片缓存目录
				File saveFile = new File(savePath);

				if (saveFile.isFile() && saveFile.length() > 100) {
					readPath = savePath;
				} else {
					if (saveFile.isDirectory()) {
						deleteFileRecursive(saveFile);
					}
					if (saveFile.isFile()) {
						saveFile.delete();
					}

					int quality = (int) (((float) maxBmpSize) / bmpSize * 100f);
					// MeilaLog.d(TAG,
					// "compressed quality: "+quality+", bmpSize: "+bmpSize+", maxBmpSize: "+maxBmpSize);
					boolean isResizeOk = BitmapUtils.reduceBmpQuality(readPath,
							savePath, quality);
					if (isResizeOk && saveFile.isFile()
							&& saveFile.length() > 100) {
						readPath = savePath;
						// MeilaLog.d(TAG,
						// "compress ok, old: "+f.getAbsolutePath()+", new: "+saveFile.getAbsolutePath());
					} 
				}
			}

			// 对图片大小进行缩放处理
			Bitmap tmpBmp = BitmapUtils.resizeBmp(new File(readPath),
					requiredSizeW, requiredSizeH, inPreferredConfig);


			return tmpBmp;
		} catch (Throwable e) {
			if (e instanceof OutOfMemoryError) {
				System.gc();
				// try {
				// Thread.sleep(SLEEP_TIME_FOR_GC_MS);
				// } catch (InterruptedException e1) {
				// MeilaLog.e(TAG, e);
				// }
			}
		}

		return null;
	}

	/**
	 * 递归删除一个目录
	 * 
	 * @param dir
	 */
	protected void deleteFileRecursive(File dir) {
		if (dir == null || !dir.exists() || !dir.isDirectory()) {
			return;
		}
		File[] files = dir.listFiles();
		if (files == null) {
			return;
		}
		for (File f : files) {
			if (f.isFile()) {
				f.delete();
			} else {
				deleteFileRecursive(f);
			}
		}
		dir.delete();
	}

	/**
	 * 计算图片占用内存大小，单位：字节
	 * 
	 * @param bmp
	 *            要计算大小的图片
	 * @return 图片大小
	 */
	static int getBitmapSize(BitmapFactory.Options opts) {
		return opts == null ? 0 : opts.outWidth * opts.outHeight
				* getBytesPerPixel(opts.inPreferredConfig);
	}

	static int getBitmapSize(Bitmap bitmap) {
		return bitmap == null ? 0 : bitmap.getRowBytes() * bitmap.getHeight();
	}

	static int getBitmapSize(String path) {
		try {
			BitmapFactory.Options opts = new BitmapFactory.Options();
			opts.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(path, opts);
			return getBitmapSize(opts);
		} catch (Throwable e) {
			Log.e(TAG, "getBitmapSize failed", e);
		}
		return 0;
	}

	static int getBytesPerPixel(Config config) {
		if (config == null || config == Config.ARGB_8888) {
			return 4;
		} else if (config == Config.RGB_565) {
			return 2;
		} else if (config == Config.ARGB_4444) {
			return 2;
		} else if (config == Config.ALPHA_8) {
			return 1;
		}
		return 1;
	}

	/**
	 * 图片保存路径优化，按照首字母和 次字母创建文件夹
	 * 
	 * @param bitmapName
	 * @return
	 */
	public String createImgChildPath(String bitmapName) {
		String newPath = null;
		if (bitmapName == null || bitmapName.length() == 0) {
			return newPath = bitmapName;
		}
		if (bitmapName.length() == 1) {
			String newFirstPath = ImgCacheDir + bitmapName + "/";
			File f = new File(newFirstPath);
			if (f != null && !f.exists()) {
				f.mkdirs();
			}
			// MeilaLog.d(TAG, "decodeLocalFile after createImgChildPath:" +
			// newFirstPath);
			newPath = newFirstPath;
		} else if (bitmapName.length() == 2) {
			String newSecStringPath = ImgCacheDir + bitmapName.substring(0, 1)
					+ "/" + bitmapName.substring(0, 2) + "/";
			File f = new File(newSecStringPath);
			if (f != null && !f.exists()) {
				f.mkdirs();
			}
			// MeilaLog.d(TAG, "decodeLocalFile after createImgChildPath:" +
			// newSecStringPath);
			newPath = newSecStringPath;
		} else {
			String newThrStringPath = ImgCacheDir + bitmapName.substring(0, 1)
					+ "/" + bitmapName.substring(0, 2) + "/"
					+ bitmapName.substring(0, 3) + "/";
			File f = new File(newThrStringPath);
			if (f != null && !f.exists()) {
				f.mkdirs();
			}
			// MeilaLog.d(TAG, "decodeLocalFile after createImgChildPath:" +
			// newSecStringPath);
			newPath = newThrStringPath;
		}
		return newPath;
	}

	/**
	 * 回调接口
	 */

	public interface ImageCallBackWithParams {
		public void imageLoad(View imageView, Bitmap bitmap, Object... params);
	}

	public interface DownloadProgressListener {
		public void onStart();

		public void onProgress(int progress, int max);

		public void onDone();

		public void onFailed();
	}
}