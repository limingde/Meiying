package com.dd.meiying;

import android.app.Application;
import android.content.Context;
import android.util.DisplayMetrics;

public class MeiyingAplication extends Application{
	public static MeiyingAplication CONTEXT;
	public static int mScreenWidth = 0;
	public static int mScreenHeight = 0;
	public void onCreate() {
		super.onCreate();
		CONTEXT = this;
		// DisplayMetrics mDisplayMetrics = new DisplayMetrics();
				DisplayMetrics mDisplayMetrics = getApplicationContext().getResources()
						.getDisplayMetrics();
				mScreenWidth = mDisplayMetrics.widthPixels;
				mScreenHeight = mDisplayMetrics.heightPixels;
	}
	
	public static Context getContext() {
		if (CONTEXT != null)
			return CONTEXT.getApplicationContext();
		else
			return null;
	}
}
