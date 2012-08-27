package com.farcore.videoplayer;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

public class MyListAdapter<T> extends ArrayAdapter{

	private int mItemIndex = -1;
	private Boolean DBG_LA = false;
	private static String TAG = "MyListAdapter";
	
	public MyListAdapter(Context context, int textViewResourceId,
			Object[] objects) {
		super(context, textViewResourceId, objects);
		// TODO Auto-generated constructor stub
	}
	
	public MyListAdapter(Context context, int textViewResourceId, List objects) {
		super(context, textViewResourceId, objects);
		// TODO Auto-generated constructor stub
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View v = super.getView(position, convertView, parent);
		if(DBG_LA) Log.w(TAG, "position:" + position + " convertView:" + convertView);
		if(position == mItemIndex){
			if (v != null) {
				if(DBG_LA) Log.w(TAG, "setBackgroundColor ok");
				v.setSelected(true);
				v.setPressed(true);
				v.setBackgroundColor(Color.GREEN);
			}
		}
		else {
			v.setSelected(false);
			v.setPressed(false);
			v.setBackgroundColor(Color.WHITE);
		}
		return v;
	}

	public void setSelectItem(int index){
		mItemIndex = index;
	}

}
