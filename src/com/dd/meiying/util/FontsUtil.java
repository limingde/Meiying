package com.dd.meiying.util;

import com.dd.meiying.MeiyingAplication;

import android.graphics.Typeface;
import android.widget.TextView;

public class FontsUtil {
	public static final int TYPE_STXINGKA = 1001;//行楷
	public Typeface getFontsTypeface(int type){
		Typeface fontFace = Typeface.createFromAsset(
				MeiyingAplication.CONTEXT.getAssets(),getTypeString(type)
	            );
		return fontFace;
	}
	
	public static void setTypeface(TextView tv,int type){
		if(tv != null){
			Typeface fontFace = Typeface.createFromAsset(
					MeiyingAplication.CONTEXT.getAssets(),getTypeString(type)
		            );
			tv.setTypeface(fontFace);
		}
	}
	public static String getTypeString(int type){
		switch (type) {
		case TYPE_STXINGKA:
			
			return "fonts/HDZB_1.TTF";

		default:
			break;
		}
		return null;
	}
}