package com.subtitleparser;

import android.graphics.Bitmap;

public class SubData
{
	public SubData(String s,int start ,int end)
	{
		substr=s;starttime=start;endtime=end;
		
	}
	public SubData(Bitmap bit,int start,int end )
	{
		bitmap=bit;starttime=start;endtime=end;
	}
	public int gettype()
	{
		if(bitmap!=null)
		{
			return 1;
		}else
		{
			return 0;
		}
	}
	public String getSubString(){
		return substr;
	}
	public Bitmap getSubBitmap(){
		return bitmap;
	}
	public int beginTime()
	{
		return starttime;
	}
	public int endTime()
	{
		return endtime;
	}	
		
	private String substr=null;
	private Bitmap bitmap=null;
	private int starttime=0;
	private int endtime=0;
	int type=0;        //0-string,1-bitmap
}