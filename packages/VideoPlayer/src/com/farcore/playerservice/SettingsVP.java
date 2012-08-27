package com.farcore.playerservice;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.os.SystemProperties;

public class SettingsVP {

	public static final String SETTING_INFOS = "SETTING_Infos";
	private static SharedPreferences setting = null;
	private static String video_rotate_path = "/sys/class/ppmgr/angle";
	private static String displaymode_path = "/sys/class/display/mode";
	private static String displayaxis_path = "/sys/class/display/axis";
	private static String video_axis_path = "/sys/class/video/axis";
	private static String video_layout_disable = "/sys/class/video/disable_video";
	private static String TAG = "SettingVideoPlayer";
	public static String display_mode = null; 
	public static String panel_resolution = null;
	public static int panel_width = 0;
	public static int panel_height = 0;
	public static final String RESUME_MODE = "ResumeMode";
	public static final String DISPLAY_MODE = "DisplayMode";
	
	public static void init(Activity act)
	{
		setting = act.getSharedPreferences(SettingsVP.SETTING_INFOS, 
												Activity.MODE_PRIVATE);
	}
	
	public static Boolean getParaBoolean(String name)
	{
		Boolean para = setting.getBoolean(name, false);
		return para;
	}
	
	public static int getParaInt(String name)
	{
		int para = setting.getInt(name, 0);
		return para;
	}
	
	public static String getParaStr(String name)
	{
		String para = setting.getString(name, "");
		//Log.d("SettingsVP", "getParaStr() name:"+name+" content:"+para);
		return para;
	}
	
	public static boolean putParaStr(String name, String para)
	{
		return setting.edit()
		  		 .putString(name, para)
		  		 .commit();
	}
	
	public static boolean putParaInt(String name, int para)
	{
		return setting.edit()
		  		 .putInt(name, para)
		  		 .commit();
	}
	
	public static boolean putParaBoolean(String name, Boolean para)
	{
		return setting.edit()
		  		 .putBoolean(name, para)
		  		 .commit();
	}
	
	public static boolean setVideoLayoutMode()
	{
    	String buf = null;
    	String dispMode = null;
		File file = new File(displaymode_path);
		if (!file.exists()) {        	
        	return false;
        }
		file = new File(video_axis_path);
		if (!file.exists()) {        	
        	return false;
        }
		file = new File(displayaxis_path);
		if (!file.exists()) {        	
        	return false;
        }
		
		//read
		try
		{
			BufferedReader in = new BufferedReader(new FileReader(displaymode_path), 32);
			BufferedReader in_axis = new BufferedReader(new FileReader(displayaxis_path), 32);
			try
			{
				dispMode = in.readLine();
				
				String dispaxis = in_axis.readLine();
				if(dispMode== null ||dispaxis== null){//not exist,default m2,lvds1080p
					dispMode = "lvds1080p";
					panel_width = 1919;
					panel_height = 1079;
					buf = "0 0 1919 1079";
					
				}else{
					String[] axisstr = dispaxis.split(" ", 5);
					
					panel_resolution = axisstr[2]+"x"+axisstr[3];
					panel_width = Integer.parseInt(axisstr[2]);
					panel_height = Integer.parseInt(axisstr[3]);
					Log.d(TAG, "Panel resolution: "+panel_resolution);

				
					if (dispMode.equals("panel"))
					{
						buf = "0,0,"+axisstr[2]+","+axisstr[3];
						Log.d(TAG, "Current display axis: "+buf);
					}
					else{
						buf = "0 0 0 0";
					}

				}	
				display_mode = dispMode;
				Log.d(TAG, "Current display mode: "+display_mode);
			} finally {
    			in.close();
    			in_axis.close();
    		} 
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e(TAG, "IOException when read "+displaymode_path);
		} 
		
		//write
		try
		{
			BufferedWriter out = new BufferedWriter(new FileWriter(video_axis_path), 32);
    		try
    		{
    			out.write(buf);    
    			Log.d(TAG, "set video window as:"+buf);
    		} finally {
				out.close();
			}
			 return true;
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e(TAG, "IOException when write "+video_axis_path);
			return false;
		}
	}
	
	public static boolean disableVideoLayout()
	{
    	String ifDisable = null;
		File file = new File(video_layout_disable);
		if (!file.exists()) {        	
        	return false;
        }
		
		//read
		try
		{
			BufferedReader in = new BufferedReader(new FileReader(video_layout_disable), 32);
			try
			{
				ifDisable = in.readLine();
				if (ifDisable.equals("1") || ifDisable.equals("2"))
				{
					Log.d(TAG, "video layout now is disable. ");
					return false;
				}
				
			} finally {
				in.close();
    		} 
		}
		catch (IOException e) {
			e.printStackTrace();
			// TODO Auto-generated catch block
			Log.e(TAG, "IOException when read "+video_layout_disable);
		} 
		
		//write
		try
		{
			BufferedWriter out = new BufferedWriter(new FileWriter(video_layout_disable), 32);
    		try
    		{
    			out.write("2");
    			Log.d(TAG, "disable video layout ok.");
    		} finally {
				out.close();
			}
			 return true;
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			Log.e(TAG, "IOException when write "+video_layout_disable);
			return false;
		}
	}
	
	public static boolean enableVideoLayout()
	{
		File file = new File(video_layout_disable);
		if (!file.exists()) {        	
        	return false;
        }
		
		try
		{
			BufferedWriter out = new BufferedWriter(new FileWriter(video_layout_disable), 32);
    		try
    		{
    			out.write("2");
    			Log.d(TAG, "enable video layout ok.");
    		} finally {
				out.close();
			}
			 return true;
		}
		catch (IOException e) {
			e.printStackTrace();
			// TODO Auto-generated catch block
			Log.e(TAG, "IOException when write "+video_layout_disable);
			return false;
		}
	}

	public static boolean setVideoRotateAngle(int angle)
	{
    	String buf = null;
    	String angle_str = null;
		File file = new File(video_rotate_path);
		if (!file.exists()) {        	
        	return false;
        }
		
		//read
		try
		{
			BufferedReader in = new BufferedReader(new FileReader(video_rotate_path), 32);
			try
			{
				angle_str = in.readLine();
				Log.d(TAG, angle_str);
				if(angle_str.startsWith("current angel is ")) {
	                String temp = angle_str.substring(17, 18);
	                Log.d(TAG, "read angle is " + temp);
					if((temp != null) && (angle != Integer.parseInt(temp))){
						buf = Integer.toString(angle);
						Log.d(TAG, buf);
					}
				}
			} finally {
    			in.close();
    		} 
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			Log.e(TAG, "IOException when read " + video_rotate_path);
		} 
		if(buf == null) {
			return false;
		}
		//write
		try
		{
			BufferedWriter out = new BufferedWriter(new FileWriter(video_rotate_path), 32);
    		try
    		{
    			Log.d(TAG, "write :"+buf);
    			out.write(buf);
    		} finally {
				out.close();
			}
			 return true;
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			Log.e(TAG, "IOException when write " + video_rotate_path);
			return false;
		}
	}

	public static boolean chkEnableOSD2XScale() {
		boolean enable = false;
		String temp_scale=SystemProperties.get("rw.fb.need2xscale");
		if(temp_scale.equals("ok")) {
			/*
			String tmp_output = SystemProperties.get("ubootenv.var.outputmode");
			if(tmp_output.equals("1080p"))
				enable = true;
			*/
			String dispMode = null;
			File file = new File(displaymode_path);
			if (!file.exists()) {        	
	        	return false;
	        }
			try
			{
				BufferedReader in = new BufferedReader(new FileReader(displaymode_path), 32);
				try
				{
					dispMode = in.readLine();
					if(dispMode == null)
						enable = false;
					else if (dispMode.equals("1080p") || dispMode.equals("lvds1080p") || dispMode.equals("lvds1080p50hz"))
						enable = true;
				}finally {
	    			in.close();
	    		} 
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.e(TAG, "IOException when read "+displaymode_path);
			} 
		}
		return enable;
	}

}
