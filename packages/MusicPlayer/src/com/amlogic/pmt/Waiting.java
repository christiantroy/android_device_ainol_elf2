package com.amlogic.pmt;

import java.util.ArrayList;
import java.util.List;

import org.geometerplus.zlibrary.ui.android.R;
import org.geometerplus.zlibrary.ui.android.library.ZLAndroidActivity;
import java.io.IOException;
import java.io.InputStream;
import com.amlogic.View.MenuGroup;
import android.widget.AbsoluteLayout;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.util.Log;
import android.app.Activity;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.content.res.AssetManager;

public class Waiting  extends View implements Runnable{	 
	private Bitmap BgBtp = null;
	private int leftOffset=100;
	private int topOffset=100;
    private int src_id_offset = 0; 
    private List<Bitmap>  BitmapSet;
    private String infoString=null;
    private String ok_button_str ;
    private Bitmap pm;
    private Bitmap button_ok;
    private Bitmap bgBmp;
    public static final int NO_SEARCH_RESULT          = 0x0001 ;
    public static final int NET_GETTING               = 0x0002 ;
    public static final int GET_PROGRAM_LIST_FAILED   = 0x0003 ;
    public static final int NO_BUTTON		          = 0x0001 ;
    public static final int BUFFRING_BUTTON           = 0x0002 ;
    public static final int OK_BUTTON                 = 0x0003 ;
    public static final int OK__CANCEL_BUTTON         = 0x0004 ;
	Paint paint = new Paint();
    private int i_button_type = BUFFRING_BUTTON;
    private int dlgLeft = 609;
    private int dlgTop = 387;
    private int dlgWidth = 702;
    private int dlgHeight = 480;
    private Context mContext;
    private boolean hidden = true;
    
	public Waiting(Context context, AttributeSet attrs) {
		super(context, attrs);
		AbsoluteLayout.LayoutParams paramp ;
		mContext = context;
		
		paramp = new AbsoluteLayout.LayoutParams( (int)(dlgWidth*Resolution.getScaleX()),
					(int)(dlgHeight*Resolution.getScaleY()),(int)(dlgLeft*Resolution.getScaleX()),
					(int)(dlgTop*Resolution.getScaleY()));	 
	    this.setLayoutParams(paramp);   

	    hidden = true;
	    
        BitmapSet = new ArrayList<Bitmap>(); 
        setInfoString(context.getString(R.string.buffing_string) , BUFFRING_BUTTON );
        Options opts = new Options();
        opts.inScaled=true;
		/*pm = decodeBitmap("loading_ani.png");         
        button_ok = decodeBitmap("button_info_content_sel.png");       
        bgBmp = decodeBitmap("bg_waiting_content.png");*/
        pm = BitmapFactory.decodeResource(this.getResources(), R.drawable.loading_ani);         
        button_ok = BitmapFactory.decodeResource(this.getResources(), R.drawable.button_info_content_sel);       
        bgBmp = BitmapFactory.decodeResource(getResources(),R.drawable.bg_waiting_content);
        
        ok_button_str = context.getString(R.string.ok) ;
        BitmapSet.clear();
        while(BitmapSet.size()<10)	BitmapSet.add(null);     
       
		paint.setColor(Color.WHITE);
		paint.setAntiAlias(true);
		paint.setTextSize(40);
		paint.setTextAlign(Paint.Align.CENTER);
        new Thread(this).start();
	}	
	
	public void show()
	{
		Log.d("Waiting", ">>>>hidden is "+hidden);
		if (hidden)
		{
			Log.d("Waiting", ">>>>Waiting dialog show<<<<<");
		    ((ZLAndroidActivity)mContext).layout.addView(this);
		    this.setFocusable(true);
		    this.requestFocus();
		    this.setVisibility(View.VISIBLE);
		    hidden = false;
		}
	}
	
	public void hide()
	{
		if (!hidden)
		{
			Log.d("Waiting", ">>>>Waiting dialog hide<<<<<");
			this.setFocusable(false);
		    this.setVisibility(View.INVISIBLE);
			((ZLAndroidActivity)mContext).layout.removeView(this);
			hidden = true;
		}
	}
	
	public void setInfoString( String info , int button_type )
	{
		i_button_type = button_type ;
		infoString=info;	
	}
	
	public String getInfoString()
	{
		return infoString ;
	}
	
	public void onDraw(Canvas canvas)	
	{			
		
		//20101205 draw bg
		if (BitmapSet.get(9) == null)
			BitmapSet.set(9, zoomBitmap(bgBmp, Resolution.getScaleX(),Resolution.getScaleY()));
		BgBtp = BitmapSet.get(9);
		canvas.drawBitmap(BgBtp, 0, 0, null);
		
		//20101205 draw text info
		
		int leftPos, topPos;
		
		//20101205 draw animation
		if( BUFFRING_BUTTON == i_button_type )
		{
			paint.setTextSize((int)(40*Resolution.getScaleX()));
			canvas.drawText(infoString, 350*Resolution.getScaleX(), 170*Resolution.getScaleY(), paint);
			src_id_offset++;		
			src_id_offset = src_id_offset%9 ;
			if(BitmapSet.get(src_id_offset) == null)
			{
				Bitmap tmpBmp = Bitmap.createBitmap(pm, src_id_offset*152, 0, 152, 152); 
				BitmapSet.set(src_id_offset, zoomBitmap(tmpBmp, Resolution.getScaleX(),Resolution.getScaleY()));
				tmpBmp.recycle();
			}
			BgBtp = BitmapSet.get(src_id_offset);			
			canvas.drawBitmap(BgBtp, 50*Resolution.getScaleX(), 95*Resolution.getScaleY(), null);					
		}
		/*else if( OK_BUTTON == i_button_type )
		{
			canvas.drawText(infoString, 320, 150, paint);
			canvas.drawBitmap( button_ok , 260 , 200 , null  ) ;
			paint.setColor(Color.WHITE);
			paint.setAntiAlias(true);
			paint.setTextSize(40);
			paint.setTextAlign(Paint.Align.CENTER);			
			canvas.drawText( ok_button_str , 350, 270, paint);
		}*/
	}
	
	public void aniDraw()
	{
		this.invalidate((int)((this.getPaddingLeft()+leftOffset)*Resolution.getScaleX()), 
				(int)((this.getPaddingTop()+topOffset)*Resolution.getScaleY()), 
				(int)((this.getPaddingLeft()+leftOffset+152)*Resolution.getScaleX()), 
				(int)((this.getPaddingTop()+topOffset+228)*Resolution.getScaleY()));
	}
	//20101205  draw shader	
	

	
	public void run() {
		// TODO Auto-generated method stub
		while(!Thread.currentThread().isInterrupted())
		{
			
			try
			{
				Thread.sleep(200);
			}
			catch(InterruptedException e)
			{
				e.printStackTrace();
			}
			
		    if(this.getVisibility() == View.VISIBLE)
		    	postInvalidate();

		}
		
	}
	private Bitmap zoomBitmap(Bitmap bitmap, float scaleWidth, float scaleHeight) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap newBmp = Bitmap.createBitmap(bitmap, 0, 0, width, height,
                matrix, true);
        return newBmp;
    }
	
	private Bitmap decodeBitmap(String file) {
		try {
			InputStream is=mContext.getAssets().open(file);
			Bitmap bmp = BitmapFactory.decodeStream(is);
			is.close();
			return bmp;
			//return is;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
}
