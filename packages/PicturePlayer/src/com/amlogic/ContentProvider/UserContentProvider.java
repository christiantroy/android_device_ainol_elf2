package com.amlogic.ContentProvider;

import java.io.IOException;
import java.io.InputStream;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;
import android.util.Log;

public class UserContentProvider {
	public static final String AUTHORITY = "com.amlogic.contentprovider"; 
	public static final Uri SETTING_CONTENT_URI = Uri.parse("content://"+ AUTHORITY + "/setting"); 	
	public static final String PARAM_NAME = "PARAM_NAME";	
	public static final String PARAM_VALUE = "PARAM_VALUE";	
	
	private Context mcontext;
	public UserContentProvider(Context context){
		this.mcontext=context;
	}
	
	
	public void insertParams(String name,String value) {
		try{
			ContentValues values = new ContentValues();
			values.put(PARAM_NAME, name);
			values.put(PARAM_VALUE, value);
			mcontext.getContentResolver().insert(SETTING_CONTENT_URI, values);
		} catch (SQLException e) {
			Log.e("ERROR", "insert error\n"+e.toString());
		}
	}

	
	public boolean deleteParams(String name) {
		try {
			int row=mcontext.getContentResolver().delete(SETTING_CONTENT_URI, "PARAM_NAME=?", new String[] { name });
			if(row==0)
				return false;
		} catch (SQLException e) {
			Log.e("ERROR", "delete error\n"+e.toString());
		}
		return true;
	}

	
	public boolean setParams(String name,String value) {

		try {
			ContentValues values = new ContentValues();
			values.put(PARAM_NAME, name);
			values.put(PARAM_VALUE, value);
			int row=mcontext.getContentResolver().update(SETTING_CONTENT_URI, values, "PARAM_NAME=?", new String[] { name });
			if(row==0)
				return false;
		} catch (SQLException e) {
			Log.e("ERROR", "update error\n"+e.toString());
		}
		return true;
	}
	
	
	public synchronized String getParams(String name) {
		String data="";
		try {
			String[] column = { "PARAM_NAME", "PARAM_VALUE"};  
			String selection = "PARAM_NAME=?";   
			String[] selectionArgs = new String[]{name};       
			//String orderBy = "_id";  
			Cursor cursor = mcontext.getContentResolver().query(SETTING_CONTENT_URI, column, selection, selectionArgs, null);		
			if(cursor==null)
				return data;
			Integer num = cursor.getCount();//num=1;
			if(num>0){
				cursor.moveToFirst();
				int pvalue = cursor.getColumnIndex(PARAM_VALUE);
				data += String.valueOf(cursor.getString(pvalue));
			}		
			cursor.close();
		} catch (SQLException e) {
			Log.e("ERROR", "read error\n"+e.toString());
		}
		return data;
	}
	
	
	public void upgradeToDefault() throws IOException {
		try {
			int row=mcontext.getContentResolver().delete(SETTING_CONTENT_URI, null, null);
			Context xmlContext=mcontext.createPackageContext("com.amlogic.contentprovider", Context.CONTEXT_IGNORE_SECURITY);
			AssetManager assets = xmlContext.getAssets();
			InputStream is=assets.open("profile.xml");    
			ProfileHandler ph = new ProfileHandler(is);
	        for(int i=0;i<ph.Pitems.size();i++){
	            ContentValues values = new ContentValues();   
	            values.put(PARAM_NAME, ph.Pitems.get(i).name);   
	            values.put(PARAM_VALUE, ph.Pitems.get(i).value);    
	            mcontext.getContentResolver().insert(SETTING_CONTENT_URI, values);
	        }	
	        is.close();
		} catch (SQLException e) {
			Log.e("ERROR", "upgrade to default error\n"+e.toString());
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
