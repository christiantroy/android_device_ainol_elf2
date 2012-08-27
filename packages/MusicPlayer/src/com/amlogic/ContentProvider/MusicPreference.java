package com.amlogic.ContentProvider;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class MusicPreference {
	private Context mycontext = null;
	private Editor editor=null;
	private SharedPreferences p=null;
	private String def_name="MusRepeatMode";
	private String def_value="FOLDER";
	public MusicPreference(Context context)
	{
		mycontext = context;
		p = PreferenceManager.getDefaultSharedPreferences(mycontext);
		String param = p.getString(def_name,"");
		editor=p.edit();
		if(param.equals(""))
		{
			editor.putString(def_name,def_value);
			editor.commit();	
		}

	}
	public void setMyParam(String name,String value)
	{
	editor.putString(name,value);
	editor.commit();
	}
	
	public String getMyParam(String name,String def)
	{
	String param = p.getString(name,def);
	return param;
	}
}


