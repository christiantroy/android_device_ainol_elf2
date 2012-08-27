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
import android.util.Log;
import java.io.FileOutputStream;

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
			btp = BitmapFactory.decodeStream(is);
			btp = zoomBitmap(btp,Resolution.getScaleX(),Resolution.getScaleY());
			Log.e("SearchDrawable","------------------new_width"+btp.getWidth()+" "+btp.getHeight());
			
			/*FileOutputStream fos = null; 
			try { 
			fos = new FileOutputStream( "/sdcard/" +str + ".jpg" ); 
			if ( fos != null ) 
					{ 
							btp.compress(Bitmap.CompressFormat.JPEG, 100, fos ); 
							fos.close(); 
					} 
			} catch( Exception e ) 
					{ 
					fos.close(); 
					Log.e("SearchDrawable","------------------create file fail");
					} */

			
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
