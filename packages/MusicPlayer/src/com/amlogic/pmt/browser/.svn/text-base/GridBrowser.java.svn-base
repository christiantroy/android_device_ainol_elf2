package com.amlogic.pmt.browser;

import java.util.ArrayList;

import android.graphics.Bitmap;

public interface GridBrowser {
	public int getCount();
	public int getNameWidthPixel();
	
	public Bitmap getItemBkBitmap();
	public Bitmap getItemIconBitmap(int idx);
	public Bitmap getItemNameBitmap(int idx);
	public Bitmap getItemScrollNameBitmap(int idx);

//	public Bitmap getItemBitmap(int idx);
	public String getItemAbsoluteName(int idx);
	public boolean IFIsFolder(int position);
	public void EnterDir(int position);
	public boolean upOneLevel();
	
	public Bitmap getTitleBitmap();
	public Bitmap getInfoTitleBitmap();
	public Bitmap getSelectedBitmap();
	
//	public Bitmap getInfoBitmap(int idx);
	
	public ArrayList<String> getPlayList();
	
	public boolean MountDevice(String deviceName);
	public boolean UnmountDevice(String deviceName);
	public int findItem(String AbsoluteName);
	public String getCurDirPath();
	public void requestThumbImage(int pos,int total);
	public void cancelThumbImage();
	
	public void requestInfoImage(int focusIndex);
	public void cancelInfoImage();
	public Bitmap getWarningBitmap();
}
