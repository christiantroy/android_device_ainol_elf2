package com.farcore.playerservice;

import android.util.Log;

public class ResumePlay {
	private static String TAG = "ResumePlay";
	private static final int RESUME_NUM_MAX = 10;
	
	public static int check(String filename)
	{
		for (int i=0; i<RESUME_NUM_MAX; i++)
		{
			if (filename.equals(SettingsVP.getParaStr("filename"+i)))
			{
				int position = SettingsVP.getParaInt("filetime"+i);
				return position;
			}
		}
		return 0;
	}
	
	public static int saveResumePara(String filename, int time)
	{
		String isNull = null;
		int i = -1;
		for (i=0; i<RESUME_NUM_MAX;)
		{
			isNull = SettingsVP.getParaStr("filename"+i);
			if (isNull == null 
					|| isNull.length() == 0
					|| isNull.equals(filename))
				break;
			i++;
		}
		Log.d(TAG, "saveResumePara() count is :"+ i);
		if (i<RESUME_NUM_MAX)
		{
			SettingsVP.putParaStr("filename"+i, filename);
			SettingsVP.putParaInt("filetime"+i, time);
		}
		else
		{
			for (int j=0; j<RESUME_NUM_MAX-1; j++)
			{
				SettingsVP.putParaStr("filename"+j, 
						SettingsVP.getParaStr("filename"+(j+1)));
				SettingsVP.putParaInt("filetime"+j, 
						SettingsVP.getParaInt("filetime"+(j+1)));
			}
			SettingsVP.putParaStr("filename"+(RESUME_NUM_MAX-1), filename);
			SettingsVP.putParaInt("filetime"+(RESUME_NUM_MAX-1), time);
		}
		return 0;
	}
}
