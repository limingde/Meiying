package com.dd.meiying.bean;

import java.io.Serializable;

import android.widget.ImageView;

public class TagResource implements Serializable{

	/**
	 * 记录标签的所有状�?
	 */
	private static final long serialVersionUID = 1L;
	public long mId; // 用于编辑标签时控制数据更�?
	public String id;
	public int type; // 1：自定义 2：化妆品
	public String title;
	public String product_slug = "";
	public float xscale;
	public float yscale;	
}
