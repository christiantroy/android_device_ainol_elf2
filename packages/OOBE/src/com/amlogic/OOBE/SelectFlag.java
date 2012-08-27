package com.amlogic.OOBE;

public class SelectFlag {
	static private String selected_mode = "720p";
	
	static public void setSelect(String tv_mode)
	{
		//Log.d("SelectFlag", "Set SelectFlag to "+tv_mode);
		selected_mode=tv_mode;
	}
	
	static public String getSelect()
	{
		return selected_mode;
	}
}

