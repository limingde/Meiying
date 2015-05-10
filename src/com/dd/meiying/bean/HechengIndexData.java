package com.dd.meiying.bean;

import java.io.Serializable;

import android.graphics.Bitmap;

public class HechengIndexData implements Serializable{
	
	public int curPosition;
	public int curType;//文本或图片
	public int type = HechengData.TYPE_L1_R2;//样式的
	public Bitmap img1;
	public Bitmap img2;
	public Bitmap img3;
	public Bitmap img4;

	public String text1;
	public String text2;
	public String text3;
}
