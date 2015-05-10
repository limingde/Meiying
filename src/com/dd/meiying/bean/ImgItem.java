package com.dd.meiying.bean;

import java.io.Serializable;

import com.dd.meiying.util.NetUtil;

public class ImgItem implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5903004703634486997L;
	/**
	 * 
	 */
	public String img;//原图，后面的img2到img4，尺寸依次减小
	public int img_width;
	public int img_height;
	
	public String img2;
	public int img2_width;
	public int img2_height;
	
	public String img3;
	public int img3_width;
	public int img3_height;
	
	public String img4;
	public int img4_width;
	public int img4_height;
	
	public boolean is_long;//是否是长图
	
	public boolean is_video;//是不是视频
	
	
	public String thumb(){
		if(NetUtil.isWifiAvailable()){
			return img2;
		}else{
			return img3;
		}
	}
	public int thumbW(){
		if(NetUtil.isWifiAvailable()){
			return img2_width;
		}else{
			return img3_width;
		}
	}
	public int thumbH(){
		if(NetUtil.isWifiAvailable()){
			return img2_height;
		}else{
			return img3_height;
		}
	}
}
