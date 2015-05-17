package com.dd.meiying.util;

import com.dd.meiying.ImageFilterActivity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Environment;
import android.view.View;


public class ScreenShot {
	/**
     * 获取指定Activity的截屏，保存到png文件
     * @param act
     * @param x 截屏横向起点坐标
     * @param y 截屏纵向起点坐标
     * @param width 截屏指定宽度
     * @param height 截屏指定高度
     */
    public static Bitmap takeScreenShot(Activity act, int x, int y, int width, int height) {
        // View是你需要截图的View
        View view = act.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap b1 = view.getDrawingCache();
//        Bitmap b1 = Bitmap.createBitmap(480*10, 800*10, Bitmap.Config.ARGB_8888);
//        view.draw(new Canvas(b1));

//        // 获取状态栏高度
//        Rect frame = new Rect();
//        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
//        int statusBarHeight = frame.top;
//        Log.i("TAG", "" + statusBarHeight);
//
//        // 获取屏幕长和高
//        int width = activity.getWindowManager().getDefaultDisplay().getWidth();
//        int height = activity.getWindowManager().getDefaultDisplay()
//                .getHeight();

        Bitmap b = Bitmap.createBitmap(b1, x, y, width, height);
        view.destroyDrawingCache();
        return b;
    }

    // 保存到到指定路径
    public static String savePic(Bitmap b) {
//        BitmapUtils.saveBitmap(b, strFileName, ImgSaveDir);
//        boolean saveOk = AlbumHelper.getHelper().saveImg(b,
//        		ImgSaveDir + strFileName);
    	return ImageFilterActivity.saveImage(b,ImageFilterActivity.ImgSaveDir);
    }

    /**
     * 功能类入口
     * @param act
     * @param x 截屏横向起点坐标
     * @param y 截屏纵向起点坐标
     * @param width 截屏指定宽度
     * @param height 截屏指定高度
     * @param strFileName 保存截图文件名字
     */
    public static void shoot(Activity act, int x, int y, int width, int height) {
        ScreenShot.savePic(ScreenShot.takeScreenShot(act, x, y, width, height));
    }
}
