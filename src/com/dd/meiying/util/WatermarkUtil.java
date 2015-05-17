package com.dd.meiying.util;
import com.dd.meiying.ImageFilterActivity;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Environment;

/**
 * 图片加水印或者蒙层滤镜工具
 * @author Jasen
 * v4.1.0
 * 
 */
public class WatermarkUtil {
	
	public static final int TYPE_WATERMARK = 0x001;
	public static final int TYPE_FILTER = 0x002;
	
	public static String ImgSaveDir = Environment.getExternalStorageDirectory()
			+ "/dd_photo/";

	/**
	 * 
	 * @param act
	 * @param filDirPath 要加水印或者蒙层滤镜的图片存放的绝对路径
	 * @param resId 水印或者蒙层滤镜素材的resId
	 * @param type 水印:0x001; 蒙层滤镜:0x002
	 * @return
	 */
	public static Bitmap initWatermark(Activity act, String filDirPath, int resId, int type) {
		return initWatermark(act, BitmapFactory.decodeFile(filDirPath), resId, type);
	}

	/**
	 * 
	 * @param act
	 * @param bm 要加水印或者蒙层滤镜的Bitmap对象
	 * @param resId 水印或者蒙层滤镜素材的resId
	 * @param type 水印:0x001; 蒙层滤镜:0x002
	 * @return
	 */
	public static Bitmap initWatermark(Activity act, Bitmap bm, int resId, int type) {
		Resources res = act.getResources();
		Bitmap m_watermarkBitmap = BitmapFactory.decodeResource(res, resId);
		int m_bmpWidth = bm.getWidth();
		int m_bmpHeight = bm.getWidth();
		int m_wmWidth = m_watermarkBitmap.getWidth();
		int m_wmHeight = m_watermarkBitmap.getHeight();
		
		// 绘制新的bitmap
		Bitmap m_newBitmap = Bitmap.createBitmap(m_bmpWidth, m_bmpHeight, bm.getConfig());
		Canvas m_newCanvas = new Canvas(m_newBitmap);
		m_newCanvas.drawBitmap(bm, 0, 0, null);
		if (type == TYPE_WATERMARK) {
			m_newCanvas.drawBitmap(m_watermarkBitmap, (m_bmpWidth - m_wmWidth), 0, null);
		} else {
			m_watermarkBitmap = Bitmap.createScaledBitmap(m_watermarkBitmap, m_bmpWidth, m_bmpHeight, true);
			m_newCanvas.drawBitmap(m_watermarkBitmap, 0, 0, null);
		}		
		m_newCanvas.save(Canvas.ALL_SAVE_FLAG);
		m_newCanvas.restore();
		return m_newBitmap;
	}
	
	// 保存到到指定路径
    public static void savePic(Bitmap b, String strFileName) {
        BitmapUtils.saveBitmap(b, strFileName, ImgSaveDir);
//        boolean saveOk = AlbumHelper.getHelper().saveImg(b,
//        		ImgSaveDir+strFileName);
    }
    
    /**
     * 程序入口    当要在Bitmap对象上加水印时,直接用该入口;若要在图片素材加水印则先调用initWatermark(),再savePic().
     * @param act
	 * @param bm 要加水印的Bitmap对象
	 * @param watermarkResId 水印素材的resId
	 * @param type 水印:0x001; 蒙层滤镜:0x002
     * @param strFileName 要保存的文件名
     */
    public static void addWatermark(Activity act, Bitmap bm, int watermarkResId, int type) {
    	Bitmap b = WatermarkUtil.initWatermark(act, bm, watermarkResId, type);
    	ImageFilterActivity.saveImage(b,ImageFilterActivity.ImgSaveDir);
    }
}
