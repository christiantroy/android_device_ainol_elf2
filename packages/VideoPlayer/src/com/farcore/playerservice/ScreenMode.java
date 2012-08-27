package com.farcore.playerservice;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import android.util.Log;

public class ScreenMode {
	private static String TAG = "ScreenMode";
	private static String ScreenModeFile = "/sys/class/video/screen_mode";
	
	public static final int NORMAL = 0;
	public static final int FULLSTRETCH = 1;
	public static final int RATIO4_3 = 2;
	public static final int RATIO16_9 = 3;
	public static final int NORMAL_NOSCALEUP = 4;
	
	public static int getScreenMode()
	{
		File file = new File(ScreenModeFile);
		if (!file.exists()) {        	
        	return 0;
        }
		
		String mode = null;
		int ret = 0;
		try
		{
			BufferedReader in = new BufferedReader(new FileReader(ScreenModeFile), 32);
			try
			{
				mode = in.readLine();
				Log.d(TAG, "The current Screen Mode is :"+mode);
				mode = mode.substring(0, 1);
				Log.d(TAG, "after substring is :"+mode);
				ret = Integer.parseInt(mode);
				Log.d(TAG, "after parseInt is :"+ret);
			} finally {
    			in.close();
    		}
			 return ret;
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			Log.e(TAG, "IOException when setScreenMode ");
			return 0;
		}
	}
	
	public static int setScreenMode(String mode)
	{

		File file = new File(ScreenModeFile);
		if (!file.exists()) {        	
        	return 0;
        }
		
		try
		{
			BufferedWriter out = new BufferedWriter(new FileWriter(ScreenModeFile), 32);
    		try
    		{
    			out.write(mode);    
    			Log.d(TAG, "set Screen Mode to:"+mode);
    		} finally {
				out.close();
			}
			 return 1;
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			Log.e(TAG, "IOException when setScreenMode ");
			return 0;
		}
	}
}
