package com.dd.meiying.bean;

import java.io.Serializable;

import android.text.TextUtils;

public class ImageTask implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3058943933735418783L;

	public static final int STATE_READY = 1;
	public static final int STATE_DOING = 2;
	public static final int STATE_FAILED = 3;
	public static final int STATE_OK = 5;
	public static final int MAX_FAILED_COUNT = 2;
	public String path;
	public String url;
	public int state;
	public int failedCount = 0;
	public int rotateDegree = 0;


	public String mContent = null;
	public Object obj1;//存放其他附加信息

	public static ImageTask makeFromPath(String outPath) {
		ImageTask task = new ImageTask();
		task.url = null;
		task.path = outPath;
		task.state = STATE_READY;
		return task;
	}
	public static ImageTask makeFromUrl(String outUrl) {
		ImageTask task = new ImageTask();
		task.url = outUrl;
		task.path = null;
		task.state = STATE_OK;
		return task;
	}

	public static ImageTask makeFromUrl(String outUrl, String content) {
		ImageTask task = new ImageTask();
		task.url = outUrl;
		task.path = null;
		task.state = STATE_OK;
		if (!TextUtils.isEmpty(content)) {
			task.mContent = content;
		}
		return task;
	}

	public static ImageTask makeFromUrlForHuatiPinglun(String outUrl) {
		ImageTask task = new ImageTask();
		task.url = outUrl;
		task.path = null;
		task.state = STATE_OK;
		return task;
	}

	//	@Override
	//	public int describeContents() {
	//		return 0;
	//	}
	//
	//	@Override
	//	public void writeToParcel(Parcel dest, int flags) {
	//		dest.writeString(path);
	//		dest.writeString(url);
	//		dest.writeInt(state);
	//	}
	//	public static final Parcelable.Creator<ImageTask> CREATOR = new Creator<ImageTask>() {
	//		
	//		@Override
	//		public ImageTask[] newArray(int size) {
	//			return null;
	//		}
	//		
	//		@Override
	//		public ImageTask createFromParcel(Parcel source) {
	//			ImageTask task = new ImageTask();
	//			task.path = source.readString();
	//			task.url = source.readString();
	//			task.state = source.readInt();
	//			return task;
	//		}
	//	};
}
