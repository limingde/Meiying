package com.dd.meiying.bean;

import java.io.Serializable;

public class HechengData implements Serializable{

	public final static int TYPE_L1_R2 = 1001;//左一右二
	public final static int TYPE_L2_R2 = 1002;//左二右二
	public final static int TYPE_L2_R1= 1003;//左二右一
	public final static int TYPE_L1_R1 = 1004;//左一右一

	public final static int TYPE_U1_B2 = 2001;//上一 下 二
	public final static int TYPE_U2_B2 = 2002;//上二下二
	public final static int TYPE_U2_B1= 2003;//上二下一
	public final static int TYPE_U1_B1 = 2004;//上一下一
	
	public int curPosition;
	public int curType;//文本或图片
	public int type = TYPE_L1_R2;//样式的
	public String img1;
	public String img2;
	public String img3;
	public String img4;

	public String text1;
	public String text2;
	public String text3;
}
