/**
 Earth
 @copyright Gan Yu Xiong
 */
package com.amlogic.pmt;

import  android.util.Log;

/**
 * TODO
 * @date 2011-7-29 下午01:41:53
 * @author Administrator
 */
public class Resolution {
	static private int[] resolution = new int[]{1920,1080};
	
	static public void setResolution(int width, int height)
	{
		Log.d("Resolution", "Set resolution to "+width+"X"+height);
		resolution[0] = width;
		resolution[1] = height;
	}
	
	static public int getWidth()
	{
		return resolution[0];
	}
	
	static public int getHeight()
	{
		return resolution[1];
	}
	
	static public float getScaleX()
	{
		return (float)resolution[0]/1920;
	}
	
	static public float getScaleY()
	{
		return (float)resolution[1]/1080;
	}
}
