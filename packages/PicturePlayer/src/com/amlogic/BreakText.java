package com.amlogic;

import android.graphics.Paint;

public  class BreakText {
	
	public static String breakText(String text, int fontsize, int width){
		if(text !=null)
		{	
			String t = new String(text);
			Paint paint = new Paint();
			paint.setTextSize(fontsize);
			int len = paint.breakText(t, true, width, null);
			if(len <= 0)
				return "";
			if(len >= text.length())
				return t;
			return t.substring(0, len-1) + '~';
		}
		else
			return null;
	}
}
