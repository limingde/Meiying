package com.dd.meiying.filter;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;

public class ImageFilter {
	private static float[][] mFilterParameters ={
		{0.1f,0.2f,0.3f},//参数1 ：  色相     参数2：饱和度       参数3:亮度       代表一种类型
		{0.3f,0.4f,0.5f},
		{0.5f,0.1f,0.6f},
		{0.5f,0.8f,0.6f}};

	/******各种颜色的设定*******/  
	//LOMO  
	static float colormatrix_lomo[] = {  
		1.7f,  0.1f, 0.1f, 0, -73.1f,  
		0,  1.7f, 0.1f, 0, -73.1f,  
		0,  0.1f, 1.6f, 0, -73.1f,  
		0,  0, 0, 1.0f, 0 };  

	//黑白  
	static float colormatrix_blackandwhite[] = {  
		0.8f,  1.6f, 0.2f, 0, -163.9f,  
		0.8f,  1.6f, 0.2f, 0, -163.9f,  
		0.8f,  1.6f, 0.2f, 0, -163.9f,  
		0,  0, 0, 1.0f, 0 };  
	//复古  
	static float colormatrix_retro[] = {  
		0.2f,0.5f, 0.1f, 0, 40.8f,  
		0.2f, 0.5f, 0.1f, 0, 40.8f,  
		0.2f,0.5f, 0.1f, 0, 40.8f,  
		0, 0, 0, 1, 0 };  

	//哥特  
	static float colormatrix_gothic[] = {  
		1.9f,-0.3f, -0.2f, 0,-87.0f,  
		-0.2f, 1.7f, -0.1f, 0, -87.0f,  
		-0.1f,-0.6f, 2.0f, 0, -87.0f,  
		0, 0, 0, 1.0f, 0 };  

	//锐化  
	static float colormatrix_sharp[] = {  
		4.8f,-1.0f, -0.1f, 0,-388.4f,  
		-0.5f,4.4f, -0.1f, 0,-388.4f,  
		-0.5f,-1.0f, 5.2f, 0,-388.4f,  
		0, 0, 0, 1.0f, 0 };  

	//淡雅  
	static float colormatrix_contribute[] = {  
		0.6f,0.3f, 0.1f, 0,73.3f,  
		0.2f,0.7f, 0.1f, 0,73.3f,  
		0.2f,0.3f, 0.4f, 0,73.3f,  
		0, 0, 0, 1.0f, 0 };  

	//酒红  
	static float colormatrix_claret_red[] = {  
		1.2f,0.0f, 0.0f, 0.0f,0.0f,  
		0.0f,0.9f, 0.0f, 0.0f,0.0f,  
		0.0f,0.0f, 0.8f, 0.0f,0.0f,  
		0, 0, 0, 1.0f, 0 };  

	//清宁  
	static float colormatrix_qingning[] = {  
		0.9f, 0, 0, 0, 0,  
		0, 1.1f,0, 0, 0,  
		0, 0, 0.9f, 0, 0,  
		0, 0, 0, 1.0f, 0 };  

	//浪漫  
	static float colormatrix_romantic[] = {  
		0.9f, 0, 0, 0, 63.0f,  
		0, 0.9f,0, 0, 63.0f,  
		0, 0, 0.9f, 0, 63.0f,  
		0, 0, 0, 1.0f, 0 };  

	//光晕  
	static float colormatrix_halo[] = {  
		0.9f, 0, 0,  0, 64.9f,  
		0, 0.9f,0,  0, 64.9f,  
		0, 0, 0.9f,  0, 64.9f,  
		0, 0, 0, 1.0f, 0 };  

	//蓝调  
	static float colormatrix_blues[] = {  
		2.1f, -1.4f, 0.6f, 0.0f, -31.0f,  
		-0.3f, 2.0f, -0.3f, 0.0f, -31.0f,  
		-1.1f, -0.2f, 2.6f, 0.0f, -31.0f,  
		0.0f, 0.0f, 0.0f, 1.0f, 0.0f  
	};  

	//梦幻  
	static float colormatrix_dream[] = {  
		0.8f, 0.3f, 0.1f, 0.0f, 46.5f,  
		0.1f, 0.9f, 0.0f, 0.0f, 46.5f,  
		0.1f, 0.3f, 0.7f, 0.0f, 46.5f,  
		0.0f, 0.0f, 0.0f, 1.0f, 0.0f  
	};  

	//夜色  
	static float colormatrix_night[] = {  
		1.0f, 0.0f, 0.0f, 0.0f, -66.6f,  
		0.0f, 1.1f, 0.0f, 0.0f, -66.6f,  
		0.0f, 0.0f, 1.0f, 0.0f, -66.6f,  
		0.0f, 0.0f, 0.0f, 1.0f, 0.0f  
	};
	/**
	 * 
	 * @param bm
	 * @param mHueValue  色相 
	 * @param mSaturationValue   饱和度  
	 * @param mLumValue 亮度 
	 * @return
	 */
	public static Bitmap getFilterImageByChangeLight(Bitmap bm, int type) {  
		if(type >= mFilterParameters.length || type < 0){
			return bm;
		}
		Bitmap bmp = Bitmap.createBitmap(bm.getWidth(), bm.getHeight(),  
				Bitmap.Config.ARGB_8888);  
		// 创建一个相同尺寸的可变的位图区,用于绘制调色后的图片  
		Canvas canvas = new Canvas(bmp); // 得到画笔对象  
		Paint paint = new Paint(); // 新建paint  
		paint.setAntiAlias(true); // 设置抗锯齿,也即是边缘做平滑处理   
		ColorMatrix allMatrix = new ColorMatrix();  
		ColorMatrix mLightnessMatrix = new ColorMatrix();
		ColorMatrix mHueMatrix = new ColorMatrix();  
		ColorMatrix mSaturationMatrix = new ColorMatrix(); 

		mHueMatrix.reset();  
		mHueMatrix.setScale(mFilterParameters[type][0], mFilterParameters[type][0], mFilterParameters[type][0], 1); // 红、绿、蓝三分量按相同的比例,最后一个参数1表示透明度不做变化，此函数详细说明参考  

		// saturation 饱和度值，最小可设为0，此时对应的是灰度图(也就是俗话的“黑白图”)，  
		// 为1表示饱和度不变，设置大于1，就显示过饱和  
		mSaturationMatrix.reset();  
		mSaturationMatrix.setSaturation(mFilterParameters[type][1]);  

		// hueColor就是色轮旋转的角度,正值表示顺时针旋转，负值表示逆时针旋转  
		mLightnessMatrix.reset(); // 设为默认值  
		mLightnessMatrix.setRotate(0, mFilterParameters[type][2]); // 控制让红色区在色轮上旋转的角度  
		mLightnessMatrix.setRotate(1, mFilterParameters[type][2]); // 控制让绿红色区在色轮上旋转的角度  
		mLightnessMatrix.setRotate(2, mFilterParameters[type][2]); // 控制让蓝色区在色轮上旋转的角度  

		// 这里相当于改变的是全图的色相  
		allMatrix.reset();  
		allMatrix.postConcat(mHueMatrix);  
		allMatrix.postConcat(mSaturationMatrix); // 效果叠加  
		allMatrix.postConcat(mLightnessMatrix); // 效果叠加  

		paint.setColorFilter(new ColorMatrixColorFilter(allMatrix));// 设置颜色变换效果  
		canvas.drawBitmap(bm, 0, 0, paint); // 将颜色变化后的图片输出到新创建的位图区  
		// 返回新的位图，也即调色处理后的图片  
		return bmp;  
	}  
	public final static int TYPE_origin = 0;
	public final static int TYPE_lomo = 1;
	public final static int TYPE_blackandwhite = 2;
	public final static int TYPE_retro = 3;
	public final static int TYPE_gothic = 4;
	public final static int TYPE_sharp = 5;
	public final static int TYPE_claret_red = 6;
	public final static int TYPE_blues = 7;
	public final static int TYPE_contribute = 8;
	public final static int TYPE_dream = 9;
	public final static int TYPE_halo = 10;
	public final static int TYPE_night = 11;
	public final static int TYPE_qingning = 12;
	public final static int TYPE_romantic = 13;


	public static float[] getCorlorArrary(int type){
		switch (type) {
		case TYPE_lomo:
			return colormatrix_lomo;
		case TYPE_blackandwhite:
			return colormatrix_blackandwhite;
		case TYPE_retro:
			return colormatrix_retro;
		case TYPE_gothic:
			return colormatrix_gothic;
		case TYPE_sharp:
			return colormatrix_sharp;
		case TYPE_claret_red:
			return colormatrix_claret_red;
		case TYPE_blues:
			return colormatrix_blues;
		case TYPE_contribute:
			return colormatrix_contribute;
		case TYPE_dream:
			return colormatrix_dream;
		case TYPE_halo:
			return colormatrix_halo;
		case TYPE_night:
			return colormatrix_night;
		case TYPE_qingning:
			return colormatrix_qingning;
		case TYPE_romantic:
			return colormatrix_romantic;
		}
		return null;
	}
	/** 
	 *
	 * @param bmp 
	 * @return 
	 */  
	public static Bitmap getFilterImage(Bitmap bmp,int type)  
	{  
		if(bmp == null){
			return null;
		}
		float[] colorArr = getCorlorArrary(type);
		if(colorArr == null || colorArr.length < 20){
			return bmp;
		}
		ColorMatrix cm = new ColorMatrix(); 
		//设置颜色矩阵 
		cm.set(colorArr); 

		Paint paint = new Paint();
		paint.setColorFilter(new ColorMatrixColorFilter(cm));
		Bitmap afterBitmap = Bitmap.createBitmap(bmp.getWidth(),bmp.getHeight(), bmp.getConfig());
		Canvas canvas = new Canvas(afterBitmap);
		canvas.drawBitmap(bmp, new Matrix(), paint);
		return afterBitmap;  
	}  
}
