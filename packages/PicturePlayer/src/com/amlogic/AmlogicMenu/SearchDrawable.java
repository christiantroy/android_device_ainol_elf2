package com.amlogic.AmlogicMenu;

import java.io.IOException;
import java.io.InputStream;

import com.amlogic.pmt.Resolution;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Matrix;
import android.graphics.Rect;


public class SearchDrawable {
	private AssetManager assets;
	private Bitmap btp = null;
	Context friendContext;
	public SearchDrawable(Context context){
//		try {
			 friendContext =context;//.createPackageContext("com.amlogic.ui.res",Context.CONTEXT_IGNORE_SECURITY);
			 assets = friendContext.getAssets();
//		} catch (NameNotFoundException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
		
	}
	
	public Bitmap getBitmap(String str){
		try {
			InputStream is=assets.open(str+".png");
			Bitmap bmp = BitmapFactory.decodeStream(is);
			btp = zoomBitmap(bmp,Resolution.getScaleX(),Resolution.getScaleY());
			bmp.recycle();
			is.close();
			//return is;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return btp;
	}

    public Bitmap zoomBitmap(Bitmap bitmap, float scaleWidth, float scaleHeight) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap newBmp = Bitmap.createBitmap(bitmap, 0, 0, width, height,
                matrix, true);
        return newBmp;
    }
	
//	public InputStream DrawableID(String id,boolean focus){
//		if(focus  ==  true)
//    		return getBitmap(id+"sel");
//    	else
//    		return getBitmap(id+"unsel");
//	}

	
    
}
