package com.subtitleparser;

import android.graphics.Bitmap;

public abstract class SubtitleApi
{
//	protected SubtitleFile SubFile =null;
	protected int begingtime = 0 ,endtime = 0 ;
	abstract public SubData getdata(int ms );
	abstract public void closeSubtitle();
	abstract public Subtitle.SUBTYPE type();
}



