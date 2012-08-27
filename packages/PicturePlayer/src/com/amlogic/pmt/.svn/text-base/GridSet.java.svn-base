package com.amlogic.pmt;

import android.graphics.Bitmap;

public class GridSet extends DisplaySet {
	final static int IDX_ITM_BK				=	0;
	final static int IDX_ITM_ICON			=	1;
	final static int IDX_ITM_NAME			=	2;
	final static int IDX_ITM_NAME_SCROLL	=	3;
	private boolean nameScroll = false;

	public GridSet() {
		super("");
		
		DisplayItem itm;
		itm = new DisplayItem((Bitmap)null);
		addItem(itm);
		itm = new DisplayItem((Bitmap)null);
		addItem(itm);
		itm = new DisplayItem((Bitmap)null);
		addItem(itm);
		itm = new DisplayItem((Bitmap)null);
		itm.setVisible(false);
		addItem(itm);
	}
	
	public GridSet(GridSet src) {
		super(src);
		
		DisplayItem itm;
		itm = new DisplayItem(src.getItem(0));
		addItem(itm);
		itm = new DisplayItem(src.getItem(1));
		addItem(itm);
		itm = new DisplayItem(src.getItem(2));
		addItem(itm);
		itm = new DisplayItem(src.getItem(3));
		addItem(itm);
	}
	
	public void SetBkBitmap(Bitmap bmp){
		DisplayItem itm = getItem(IDX_ITM_BK);
//		itm.recycleBitmap();
		itm.delItemTextures();
		itm.SetBitmap(bmp, true);
	}
	
	public void SetIconBitmap(Bitmap bmp, boolean share){
		DisplayItem itm = getItem(IDX_ITM_ICON);
//		itm.recycleBitmap();
		itm.delItemTextures();
		itm.SetBitmap(bmp, share);
	}
	
	public void SetIconPosition(float x, float y, float z){
		DisplayItem itm = getItem(IDX_ITM_ICON);
		itm.setPosition(x, y, z);
	}
	
	public void SetIconScale(float x, float y, float z){
		DisplayItem itm = getItem(IDX_ITM_ICON);
		itm.setScale(x, y, z);
	}
	
	public void SetNameBitmap(Bitmap bmp){
		DisplayItem itm = getItem(IDX_ITM_NAME);
//		itm.recycleBitmap();
		itm.delItemTextures();
		itm.SetBitmap(bmp, true);
	}
	
	public void SetNameScrollBitmap(Bitmap bmp){
		DisplayItem itm = getItem(IDX_ITM_NAME_SCROLL);
//		itm.recycleBitmap();
		itm.delItemTextures();
		itm.SetBitmap(bmp, true);
	}
	
	public void SetNamePosition(float x, float y, float z){
		DisplayItem itm;
		itm = getItem(IDX_ITM_NAME);
		itm.setPosition(x, y, z);
		itm = getItem(IDX_ITM_NAME_SCROLL);
		itm.setPosition(x, y, z);
	}
	
	public void SetNameScale(float x, float y, float z){
		DisplayItem itm;
		itm = getItem(IDX_ITM_NAME);
		itm.setScale(x, y, z);
		itm = getItem(IDX_ITM_NAME_SCROLL);
		itm.setScale(x, y, z);
	}
	
	public void SetNameWidthPixel(int wp){
		DisplayItem itm = getItem(IDX_ITM_NAME_SCROLL);
		itm.SetNameWidthPixel(wp);
	}
	
	public void SetNameScroll(boolean scroll){
		if(scroll != nameScroll){
			DisplayItem itm;
			itm = getItem(IDX_ITM_NAME);
			itm.setVisible(!scroll);
			itm = getItem(IDX_ITM_NAME_SCROLL);
			itm.setVisible(scroll);
			itm.setNameScroll(scroll);
			nameScroll = scroll;
		}
	}
}
