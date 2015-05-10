package com.dd.meiying.util;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

public class BitmapUtils {
	public static final String TAG = "BitmapUtils";
	public static BitmapDrawable createRepeaterX(int width, int drawableId,
			Resources r) {
		InputStream is = r.openRawResource(drawableId);
		Bitmap bmp = BitmapFactory.decodeStream(is);
		try {
			if(is != null){
				is.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int count = (width + bmp.getWidth() - 1) / bmp.getWidth();
		Bitmap bitmap = Bitmap.createBitmap(width, bmp.getHeight(),
				Config.ARGB_8888);
		android.graphics.Canvas canvas = new android.graphics.Canvas(bitmap);
		for (int idx = 0; idx < count; ++idx) {
			canvas.drawBitmap(bmp, idx * bmp.getWidth(), 0, null);
		}
		return new BitmapDrawable(bitmap);
	}

	public static BitmapDrawable createRepeaterY(int height, int drawableId,
			Resources r) {
		InputStream is = r.openRawResource(drawableId);
		Bitmap bmp = BitmapFactory.decodeStream(is);

		int count = (height + bmp.getHeight() - 1) / bmp.getHeight();
		Bitmap bitmap = Bitmap.createBitmap(bmp.getWidth(), height,
				Config.ARGB_8888);
		android.graphics.Canvas canvas = new android.graphics.Canvas(bitmap);
		for (int idx = 0; idx < count; ++idx) {
			canvas.drawBitmap(bmp, 0, idx * bmp.getHeight(), null);
		}
		return new BitmapDrawable(bitmap);
	}

	public static Bitmap drawable2Bitmap(Drawable d) {
		// Log.d("getIntrinsicHeight", String.valueOf(d.getIntrinsicHeight()));
		// Log.d("getIntrinsicWidth", String.valueOf(d.getIntrinsicWidth()));
		BitmapDrawable bd = (BitmapDrawable) d;
		return bd.getBitmap();
	}

	public static Drawable bitmap2Drawable(Bitmap b) {
		BitmapDrawable bd = new BitmapDrawable(b);
		return (Drawable) bd;
	}

	public static boolean saveBitmap(Bitmap b, String bitName, String dirPath){
		String fileName = dirPath + bitName + ".jpg";
		return saveBitmap(b, fileName);
	}

	public static boolean saveBitmap(Bitmap b, String bitName, String dirPath,
			int quality){
		String fileName = dirPath + bitName + ".jpg";
		return saveBitmap(b, fileName, quality);
	}

	public static boolean saveBitmap(Bitmap b, String absolutePath){
		return saveBitmap(b, absolutePath, 100);
	}

	public static boolean saveBitmap(Bitmap b, String absolutePath, int quality){
		String fileName = absolutePath;
		File f = new File(fileName);
		try {
			f.createNewFile();		
			FileOutputStream fOut = new FileOutputStream(f);		
			b.compress(Bitmap.CompressFormat.JPEG, quality, fOut);		
			fOut.flush();
			fOut.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean saveBitmap(InputStream in, String absolutePath){
		return saveBitmap(in, absolutePath, null);
	}

	// 带进度的下载
	public static boolean saveBitmap(InputStream in, String absolutePath, int[] param){
		if (in == null) {
			return false;
		}
		File bitmapFile = new File(absolutePath);
		if (!bitmapFile.exists()) {
			try {
				bitmapFile.createNewFile();
			} catch (Exception e) {
				e.printStackTrace();


				try {
					// 如何创建文件失败，在某些4.0的机型上
					// 先创建文件夹在创建文件
					File dirPath = bitmapFile.getParentFile();
					if (dirPath.exists()) {
						if (dirPath.isFile()) {
							boolean ok = dirPath.delete();
							if (ok) {
								dirPath.mkdir();
							}
						}
					} else {
						dirPath.mkdirs();
					}

					bitmapFile.createNewFile();
				} catch (Exception ex) {
					e.printStackTrace();
					return false;
				}
			}
		}
		return false;
	}

	public static void getLoacalBitmap(String url, ImageView iv) {
		String release = android.os.Build.VERSION.RELEASE;
		if (release.contains("2.1")) {
			url = url.replace("/mnt", "");
		}
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeFile(url, options); // 此时返回bm为空
		options.inJustDecodeBounds = false;
		int be = (int) (options.outHeight / (float) 200);
		if (be <= 0)
			be = 1;
		options.inSampleSize = be;

		bitmap = BitmapFactory.decodeFile(url, options);
		if(bitmap != null){
			iv.setImageBitmap(bitmap);
		}
	}
	
	public static Bitmap getLoacalBitmap(String url) {
		String release = android.os.Build.VERSION.RELEASE;
		if (release.contains("2.1")) {
			url = url.replace("/mnt", "");
		}
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeFile(url, options); // 此时返回bm为空
		options.inJustDecodeBounds = false;
		int be = (int) (options.outHeight / (float) 200);
		if (be <= 0)
			be = 1;
		options.inSampleSize = be;

		bitmap = BitmapFactory.decodeFile(url, options);
		return bitmap;
	}

	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float roundPx) {
		Bitmap output = bitmap;
		if (bitmap != null) {
			int w = bitmap.getWidth();
			int h = bitmap.getHeight();
			output = Bitmap.createBitmap(w, h, Config.ARGB_8888);
			if (output != null) {
				Canvas canvas = new Canvas(output);

				final int color = 0xff424242;
				final Paint paint = new Paint();
				final Rect rect = new Rect(0, 0, w, h);
				final RectF rectF = new RectF(rect);

				paint.setAntiAlias(true);
				canvas.drawARGB(0, 0, 0, 0);
				paint.setColor(color);
				canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

				paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
				canvas.drawBitmap(bitmap, rect, rect, paint);

				//				bitmap.recycle();
				bitmap = null;
			}
		}
		return output;
	}

	public static Bitmap createBitmap(Bitmap b, float width, float angle) {
		// 计算缩放比例
		if (b != null) {
			if (b.getWidth() != width) {
				float scale = width / b.getWidth();

				Matrix matrix = new Matrix();
				matrix.postScale(scale, scale);
				matrix.postRotate(angle);

				Bitmap bNew = Bitmap.createBitmap(b, 0, 0, b.getWidth(),
						b.getHeight(), matrix, true);
				return bNew;
			} else {
				return b;
			}
		}
		return null;
	}

	public static byte[] Bitmap2Bytes(Bitmap bm) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
		return baos.toByteArray();
	}

	public static Bitmap Bytes2Bimap(byte[] b) {
		if (b != null) {
			if (b.length != 0) {
				return BitmapFactory.decodeByteArray(b, 0, b.length);
			}
		}
		return null;
	}


	/**
	 * 降低图片质量
	 * @param readPath
	 * @param savePath
	 * @param maxSizeInByte
	 * @return
	 */
	public static boolean reduceBmpQuality(String readPath, String savePath, int quality) {

		try {
			FileInputStream fin = new FileInputStream(readPath);
			//			Bitmap bmp = BitmapFactory.decodeFile(readPath);
			Bitmap bmp = BitmapFactory.decodeStream(fin);//内存开销较小
			return reduceBmpQuality(bmp, savePath, quality);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Throwable e) {
			e.printStackTrace();
			if (e instanceof OutOfMemoryError) {
				System.gc();
				try {
					Thread.sleep(100);
				} catch (Exception e1) {
					e.printStackTrace();
				}
			}
		}
		return false;
	}
	public static boolean reduceBmpQuality(Bitmap bmp, String savePath, int quality) {

		File saveFile = new File(savePath);
		try {
			if(quality>=100 || quality<=0){
				quality = 80;
			}
			FileOutputStream fout = new FileOutputStream(saveFile);
			bmp.compress(Bitmap.CompressFormat.JPEG, quality, fout);
			fout.close();
			bmp.recycle();
			bmp = null;

			if(saveFile.exists() && saveFile.length()>100){
				return true;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Throwable e) {
			e.printStackTrace();
			if (e instanceof OutOfMemoryError) {
				System.gc();
				try {
					Thread.sleep(100);
				} catch (Exception e1) {
					e.printStackTrace();
				}
			}
		}
		return false;
	}
	public static boolean resizeBmpWH(String readPath, String savePath, int requiredSizeW, int requiredSizeH){
		Bitmap bmp = BitmapUtils.resizeBmp(readPath, requiredSizeW, requiredSizeH);

		boolean isSaveOk = false;
		if(bmp != null){
			isSaveOk = BitmapUtils.saveBitmap(bmp, savePath);
			bmp.recycle();
			bmp = null;			
		}

		if(!isSaveOk){
			File saveFile = new File(savePath);
			if(saveFile.exists()){
				saveFile.delete();
			}
		}
		return isSaveOk;
	}

	public static Bitmap resizeBmp(String localPath, int requiredSizeW, int requiredSizeH){
		File f = new File(localPath);
		return resizeBmp(f, requiredSizeW, requiredSizeH);
	}
	public static Bitmap resizeBmp(File f, int requiredSizeW, int requiredSizeH){
		return resizeBmp(f, requiredSizeW, requiredSizeH, Config.RGB_565);
	}
	public static Bitmap resizeBmp(File f, int requiredSizeW, int requiredSizeH, Config inPreferredConfig){
		Bitmap tmpBmp = null; 
		try {
			BitmapFactory.Options o2 = getResizeBmpOption(f, requiredSizeW, requiredSizeH, inPreferredConfig);		
			if(o2 != null){
				tmpBmp = BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return tmpBmp;
	}

	public static BitmapFactory.Options getResizeBmpOption(File f, int requiredSizeW, int requiredSizeH){
		//某些图片的白色部分会有变色，不过换成ARGB_8888又比较占内存
		return getResizeBmpOption(f, requiredSizeW, requiredSizeH, Config.RGB_565);
	}
	public static BitmapFactory.Options getResizeBmpOption(File f, int requiredSizeW, int requiredSizeH, Config inPreferredConfig){
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
					if ((!(requiredSizeW >0) || (requiredSizeW >0 && scaleSizeW <= requiredSizeW))
							&& (!(requiredSizeH >0) || (requiredSizeH >0 && scaleSizeH <= requiredSizeH))) {
						break;
					}
					scale += scale;
				}
			}

			Log.d(TAG, "after calculate, w: " + o.outWidth + ", h: " + o.outHeight
					+ ", scale: " + scale + ", requiredSizeW: " + requiredSizeW
					+ ", requiredSizeH: " + requiredSizeH);

			// decode with inSampleSize
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			// o2.inDensity = 1;
			// o2.inTargetDensity = o2.inDensity;
			o2.inSampleSize = scale;
			o2.inPurgeable = true;
			o2.inPreferredConfig = inPreferredConfig;

			//			Bitmap tmpBmp = BitmapFactory.decodeStream(new FileInputStream(f),
			//					null, o2);
			return o2;
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}


	/**
	 * 圆角图片
	 * @param context
	 * @param resId 资源文件
	 * @param ratio 圆角占图片的百分比
	 * @return
	 */

	public static Bitmap getRoundCornerBitmapByRatio(Context context, int resId, float ratio) {
		Bitmap output = BitmapFactory.decodeResource(context.getResources(), resId);
		return getRoundCornerBitmapByRatio(output, ratio);
	}

	/**
	 * 圆角图片
	 * @param bm
	 * @param ratio 圆角占图片的百分比
	 * @return
	 */
	public static Bitmap getRoundCornerBitmapByRatio(Bitmap bm, float ratio) {
		try{
			if (bm != null) {
				float roundPx = bm.getWidth() * ratio;
				Bitmap output = Bitmap.createBitmap(bm.getWidth(),
						bm.getHeight(), Config.ARGB_8888);
				if(output == null){
					return null;
				}
				Canvas canvas = new Canvas(output);

				final int color = 0xff424242;
				final Paint paint = new Paint();
				final Rect rect = new Rect(0, 0, bm.getWidth(), bm.getHeight());
				final RectF rectF = new RectF(rect);

				paint.setAntiAlias(true);
				canvas.drawARGB(0, 0, 0, 0);
				paint.setColor(color);
				canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

				paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
				canvas.drawBitmap(bm, rect, rect, paint);

				return output;
			}
		}catch (Throwable e) {
		}

		return null;
	}

	public static Bitmap createCaptureBitmap(String filepath) {
		int scale = 1;
		try {
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(filepath, options);
			int IMAGE_MAX_SIZE = 800;
			if (options.outWidth > IMAGE_MAX_SIZE
					|| options.outHeight > IMAGE_MAX_SIZE) {
				scale = (int) Math.pow(2, (int) Math.round(Math
						.log(IMAGE_MAX_SIZE
								/ (double) Math.max(options.outHeight,
										options.outWidth))
										/ Math.log(0.5)));
			}
			BitmapFactory.Options opt = new BitmapFactory.Options();
			opt.inSampleSize = scale;
			return BitmapFactory.decodeFile(filepath, opt);
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
			scale = scale * 2;
			try {
				BitmapFactory.Options opt = new BitmapFactory.Options();
				opt.inSampleSize = scale;
				return BitmapFactory.decodeFile(filepath, opt);
			} catch (OutOfMemoryError oe) {
				e.printStackTrace();
				return null;
			}
		}
	}

//	public static String getImageLoadUrl(String url) {
//		if (!TextUtils.isEmpty(url) && !url.startsWith("http://") && !url.startsWith("https://")) {
//			return HttpUtils.concatUrl(MeilaConst.getConst().ImgHttpPrefix, url);
//		}
//		return url;
//	}

	/**
	 * 处理三星手机拍出来的图片会旋转的问题，同时刷新相册
	 * @param imgPath
	 */
	public static void checkImageOrientation(String imgPath) {
		ByteArrayOutputStream baos = null;
		FileOutputStream fos = null;
		BufferedOutputStream bos = null;

		int rotateDegress = 0;
		try {
			ExifInterface exif = new ExifInterface(imgPath);
			int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
			if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
				rotateDegress = 90;
			} else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
				rotateDegress = 180;
			} else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
				rotateDegress = 270;
			} else {
				return ;
			}

			Options opt = new Options();
			opt.inJustDecodeBounds = true;
			Bitmap bit = BitmapFactory.decodeFile(imgPath, opt);
			int width = opt.outWidth;
			int height = opt.outHeight;
			if (width > height && width > 1024) {
				opt.inSampleSize = width / 1024 + 1;
			} else if (height > 1024) {
				opt.inSampleSize = height / 1024 + 1;
			}
			opt.inJustDecodeBounds = false;
			bit = BitmapFactory.decodeFile(imgPath, opt).copy(Config.ARGB_8888, true);

			Matrix matrix = new Matrix();
			matrix.postScale(1.0f, 1.0f);
			matrix.postRotate(rotateDegress);
			Bitmap bitmap = Bitmap.createBitmap(bit, 0, 0, bit.getWidth(),
					bit.getHeight(), matrix, false);
			bit.recycle();
			bit = null;
			bit = bitmap;

			baos = new ByteArrayOutputStream();
			bit.compress(Bitmap.CompressFormat.JPEG, 100, baos);
			//			String newImagePath = imgPath + "1.jpg";
			File file = new File(imgPath);
			if (!file.exists()) {
				file.createNewFile();
			} else {
				file.delete();
				file.createNewFile();
			}
			bos = new BufferedOutputStream(new FileOutputStream(file));
			bos.write(baos.toByteArray());
			bos.flush();

			bit.recycle();
			bit = null;

			exif.setAttribute(ExifInterface.TAG_ORIENTATION, String.valueOf(ExifInterface.ORIENTATION_NORMAL));
		} catch (Throwable e) {
			e.printStackTrace();
		} finally {
			try {
				if (baos != null) {
					baos.close();
				}
				if (fos != null) {
					fos.close();
				}
				if (bos != null) {
					bos.close();
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * 旋转图片
	 * @param imgPath
	 * @param rotateDegress：旋转角度，顺时针
	 * @return 旋转后的图片路径
	 */
	public static String rotateImage(String imgPath, int rotateDegress) {
		ByteArrayOutputStream baos = null;
		FileOutputStream fos = null;
		BufferedOutputStream bos = null;
		if(rotateDegress % 360 == 0
				|| rotateDegress % 90 != 0
				){
			return imgPath;
		}

		if (TextUtils.isEmpty(imgPath)) {
			return imgPath;
		}

		int index =  imgPath.lastIndexOf(".");
		if(index <= 0){
			index = imgPath.length();
		}
		String outImgPath = imgPath.substring(0, index)+"_"+rotateDegress+".jpg";
		try {

			Options opt = new Options();
			opt.inJustDecodeBounds = true;

			Bitmap bit = BitmapFactory.decodeFile(imgPath, opt);
			int width = opt.outWidth;
			int height = opt.outHeight;
			if (width > height && width > 1024) {
				opt.inSampleSize = width / 1024 + 1;
			} else if (height > 1024) {
				opt.inSampleSize = height / 1024 + 1;
			}
			opt.inJustDecodeBounds = false;
			bit = BitmapFactory.decodeFile(imgPath, opt).copy(Config.ARGB_8888, true);

			Matrix matrix = new Matrix();
			matrix.postScale(1.0f, 1.0f);
			matrix.postRotate(rotateDegress);
			Bitmap bitmap = Bitmap.createBitmap(bit, 0, 0, bit.getWidth(),
					bit.getHeight(), matrix, false);
			bit.recycle();
			bit = null;
			bit = bitmap;

			baos = new ByteArrayOutputStream();
			bit.compress(Bitmap.CompressFormat.JPEG, 100, baos);
			//			String newImagePath = imgPath + "1.jpg";
			File file = new File(outImgPath);
			if (!file.exists()) {
				file.createNewFile();
			} else {
				file.delete();
				file.createNewFile();
			}
			bos = new BufferedOutputStream(new FileOutputStream(file));
			bos.write(baos.toByteArray());
			bos.flush();

			bit.recycle();
			bit = null;

			return outImgPath;
		} catch (Throwable e) {
			e.printStackTrace();
		} finally {
			try {
				if (baos != null) {
					baos.close();
				}
				if (fos != null) {
					fos.close();
				}
				if (bos != null) {
					bos.close();
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return imgPath;
	}
}