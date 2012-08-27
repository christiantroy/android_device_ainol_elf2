package com.subtitleview;

import android.R.bool;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.FrameLayout;
import android.view.View;
import android.view.Gravity;
import com.subtitleparser.*;
import android.graphics.Matrix;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.widget.LinearLayout;

public class SubtitleView extends FrameLayout {
	private static final String TAG = SubtitleView.class.getSimpleName();
	private boolean needSubTitleShow = true;
	private int timeoffset = 400;
	private SubData data = null;
	private int graphicViewMode = 0;
	private float wscale = 1.000f;
	private float hscale = 1.000f;
	private ImageView mImageView = null;
	private TextView mTextView = null;
	
	public void setGraphicSubViewMode(int flag) {
		graphicViewMode = flag;
	}
	
	public SubtitleView(Context context) {
		super(context);
		init(context);
	}

	public SubtitleView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public SubtitleView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}
	
	private void init(Context context) {
		mImageView = new ImageView(context);
		
		mTextView = new TextView(context);
		if(mTextView != null) {
			mTextView.setTextColor(0);
			mTextView.setTextSize(12);
			mTextView.setTypeface(null, Typeface.BOLD);
			mTextView.setGravity(Gravity.CENTER);
		}
		wscale = 1.000f;
		hscale = 1.000f;
		
		SubManager.getinstance();
	}
	
	public void setTextColor(int color) {
		if(mTextView != null) {
			mTextView.setTextColor(color);
		}
	}
	
	public void setTextSize(int size) {
		if(mTextView != null) {
			mTextView.setTextSize(size);
		}
	}
	
	public void setTextStyle(int style) {
		if(mTextView != null) {
			mTextView.setTypeface(null, style);
		}
	}
	
	public void setGravity(int gravity) {
		if(mTextView != null) {
			mTextView.setGravity(gravity);
		}
	}
	
	public void setViewStatus(boolean flag) {
		needSubTitleShow = flag;
		if(flag == false) {
			setVisibility(INVISIBLE); 
		}
		else {
			setVisibility(VISIBLE); 
		}
	}
	
	public void clear() {
		data = null;
		SubManager.getinstance().clear();
		wscale = 1.000f;
		hscale = 1.000f;
		this.removeAllViews();
		this.requestLayout();
	}
	
	public void redraw() {
		this.removeAllViews();
		if(data != null) {
			if(data.gettype() == 1) {	
				evaluteScale(data.getSubBitmap());
				Bitmap inter_bitmap = creatBitmapByScale(data.getSubBitmap(), 
						wscale, wscale);
				if((inter_bitmap != null) && (mImageView != null)) {
					mImageView.setImageBitmap(inter_bitmap);
					this.addView(mImageView);
				}
			}
			else {
				String sttmp = data.getSubString();		
				sttmp=sttmp.replaceAll("\r","");
				byte sttmp_2[] = sttmp.getBytes();
				if( sttmp_2.length > 0 && 0 == sttmp_2[ sttmp_2.length-1] )
					sttmp_2[ sttmp_2.length-1] = 0x20;
				if(mTextView != null) {
					mTextView.setText( new String( sttmp_2 ) );
					this.addView(mTextView);
				}
		    }
		}
		this.requestLayout();
	}

	public void setDisplayResolution(int width, int height) {
		SubManager.getinstance().setDisplayResolution(width, height);
	}
	
	public void setVideoResolution(int width, int height) {
		SubManager.getinstance().setVideoResolution(width, height);
	}

	public void evaluteScale(Bitmap bitmap) {
		float w_scale = 1.000f;
		float h_scale = 1.000f;
		int display_width = 0;
		int display_height = 0;
		int video_width = 0;
		int video_height = 0;
		int bitmap_width = 0;
		int bitmap_height = 0;
		int max_width = 0;
		int max_height = 0;
		
		wscale = 1.000f;
		hscale = 1.000f;
		
		display_width = SubManager.getinstance().getDisplayWidth();
		display_height = SubManager.getinstance().getDisplayHeight();
		video_width = SubManager.getinstance().getVideoWidth();
		video_height = SubManager.getinstance().getVideoHeight();
		
		if(bitmap != null) {
			bitmap_width = bitmap.getWidth();
			bitmap_height = bitmap.getHeight(); 
		}
		//Log.d(TAG, "disply width: " + display_width + ", height: " + display_height);
		//Log.d(TAG, "video width: " + video_width + ", height: " + video_height);
		//Log.d(TAG, "bitmap width: " + bitmap_width + ", height: " + bitmap_height);
		
		max_width = (video_width > bitmap_width) ? video_width : bitmap_width;
		max_height = (video_height > max_height) ? video_height : max_height;
		
		if((display_width <= 0) || (display_height <= 0) 
				|| (max_width <= 0) || (max_height <= 0)) {
			return;
		}

		if((max_width <= display_width) && (max_height <= display_height)) {
			return; 
		}
		
		if(this.getWidth() == display_width) {
			w_scale = ((float)display_width)/max_width;
			h_scale = ((float)display_height)/max_height;
		}
		else if(this.getWidth() == display_height){
			w_scale = ((float)display_height)/max_width;
			h_scale = ((float)display_width)/max_height;
		}
		
		//Log.d(TAG, "w_scale: " + Float.toString(w_scale));
		//Log.d(TAG, "h_scale: " + Float.toString(h_scale));
		
		if((w_scale < 0.000f) || (h_scale < 0.000f)) {
			return;
		}
		
		wscale = w_scale;
		hscale = h_scale;
	}
	
	public static Bitmap creatBitmapByScale(Bitmap bitmap, float w_scale, float h_scale) {   
		if( bitmap == null ) {   
			return null;   
		}  
		
		int w = bitmap.getWidth();
		int h = bitmap.getHeight(); 
		/*
		Log.d(TAG, "bitmap width: " + w + ", height: " + h 
				+ ", w_scale: " + Float.toString(w_scale)
				+ ", h_scale: " + Float.toString(h_scale));
		*/
		Matrix matrix = new Matrix();
		matrix.postScale(w_scale, h_scale); 
		Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true );   
		return resizedBitmap;   
	}
	
	public void tick(int millisec) {
		if(needSubTitleShow == false) {
			return;
		}

		int modifytime = millisec + timeoffset;
		if(data != null) {
			if((modifytime >= data.beginTime()) && (modifytime <= data.endTime())) {
				if(getVisibility() == View.GONE)
					return ;
			}
			else {
				data = SubManager.getinstance().getSubData(modifytime);
			}
		}
		else {
			data = SubManager.getinstance().getSubData(modifytime);
		}
		
		redraw();
	}
	
	public void setDelay(int milsec) {
		timeoffset = milsec;
	}
	
	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas); 
    }
    
    public void closeSubtitle() {
    	SubManager.getinstance().closeSubtitle();
    }

	public Subtitle.SUBTYPE setFile(SubID file, String enc) throws Exception {
		return SubManager.getinstance().setFile(file, enc);
	}

	public SubtitleApi getSubtitleFile() {
		return SubManager.getinstance().getSubtitleFile();
	}
}
