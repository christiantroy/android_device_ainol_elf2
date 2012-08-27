package com.amlogic.pmt;

import android.graphics.Bitmap;

public class GridSlot extends DisplaySlot {

	public GridSlot() {
		super();
		displaySet = new GridSet();
	}
	
	public GridSlot(GridSlot src){
		super(src);
		displaySet = new GridSet((GridSet)src.displaySet);
	}
	
	public void SetBkBitmap(Bitmap bmp){
		((GridSet)displaySet).SetBkBitmap(bmp);
	}
	
	public void SetIconBitmap(Bitmap bmp){
		SetIconBitmap(bmp, true);
	}
	public void SetIconBitmap(Bitmap bmp, boolean share){
		((GridSet)displaySet).SetIconBitmap(bmp, share);
	}
	
	public void SetIconPosition(float x, float y, float z){
		((GridSet)displaySet).SetIconPosition(x, y, z);
	}
	
	public void SetIconScale(float x, float y, float z){
		((GridSet)displaySet).SetIconScale(x, y, z);
	}
	
	public void SetNameBitmap(Bitmap bmp){
		((GridSet)displaySet).SetNameBitmap(bmp);
	}
	
	public void SetNameScrollBitmap(Bitmap bmp){
		((GridSet)displaySet).SetNameScrollBitmap(bmp);
	}	
	
	public void SetNamePosition(float x, float y, float z){
		((GridSet)displaySet).SetNamePosition(x, y, z);
	}
	
	public void SetNameScale(float x, float y, float z){
		((GridSet)displaySet).SetNameScale(x, y, z);
	}
	
	public void SetNameWidthPixel(int wp){
		((GridSet)displaySet).SetNameWidthPixel(wp);
	}
	
	public void SetNameScroll(boolean scroll){
		((GridSet)displaySet).SetNameScroll(scroll);
	}
}
