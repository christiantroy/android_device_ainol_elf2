package com.subtitleview;

import android.util.Log;
import com.subtitleparser.*;

public class SubManager{
	private static final String TAG = SubManager.class.getSimpleName();
	private SubtitleApi subapi = null;
	private Subtitle.SUBTYPE type = Subtitle.SUBTYPE.SUB_INVALID;
	private Subtitle subtitle = null;
	private SubData data = null;
	private int display_width = 0;
	private int display_height = 0;
	private int video_width = 0;
	private int video_height = 0;
	private boolean hasopenInsubfile = false;
	private static SubManager submanager = null;

	public static SubManager getinstance()
	{
		if(submanager == null)
			submanager = new SubManager();
		return submanager;
	}
	
	public SubManager() {
		subtitle = new Subtitle();
		clear();
	}
	
	public void clear() {
		display_width = 0;
		display_height = 0;
		video_width = 0;
		video_height = 0;
	}
	
	public Subtitle.SUBTYPE setFile(SubID file, String enc) throws Exception {
		if(subapi != null) {
			if(subapi.type() == Subtitle.SUBTYPE.INSUB) {
				//don't release now,apk will call closeSubtitle when change file.
			}
			else {
				subapi.closeSubtitle();
				subapi = null;
			}
		}
		
		subtitle.setSystemCharset(enc);
		// load Input File
		try {
			subtitle.setSubID(file);
		    type = subtitle.getSubType();
		    if (type == Subtitle.SUBTYPE.SUB_INVALID) {
		    	subapi = null;
		    }
		    else {
		    	if(type == Subtitle.SUBTYPE.INSUB) {
		    		hasopenInsubfile = true;
		    	}
		    	subapi = subtitle.parse();
	    	}
		} 
		catch (Exception e) {
		    Log.e(TAG, "setFile, error is " + e.getMessage());
			throw e;
		}

		return type;
	}
	
	public int getDisplayWidth() {
		//Log.d(TAG, "display_width:"+display_width);
		return display_width;
	}
	
	public int getDisplayHeight() {
		//Log.d(TAG, "display_height:"+display_height);
		return display_height;
	}
	
	public int getVideoWidth() {
		//Log.d(TAG, "video_height:"+video_width);
		return video_width;
	}
	
	public int getVideoHeight() {
		//Log.d(TAG, "video_height:"+video_height);
		return video_height;
	}

	public void setDisplayResolution(int width, int height) {
		if((width <= 0) || (height <= 0)) {
			return;
		}
		
		this.display_width = width;
		this.display_height = height;
		//Log.d(TAG,"set display width:" + display_width + ", height:" + display_height);
	}
	
	public void setVideoResolution(int width, int height) {
		if((width <= 0) || (height <= 0)) {
			return;
		}
		
		this.video_width = width;
		this.video_height = height;
		//Log.d(TAG,"set video width:" + video_width + ", height:" + video_height);
	}
	
    public void closeSubtitle() {
		if(subapi != null) {
			subapi.closeSubtitle();
			subapi = null;				
		}   
		
		if(hasopenInsubfile == true) {
			hasopenInsubfile = false;
			subtitle.setSubname("INSUB"); 
			try {
				subapi = subtitle.parse();
				subapi.closeSubtitle();
				subapi = null;
			} 
			catch (Exception e) {
				e.printStackTrace();
			}
		}
    }
	public SubtitleApi getSubtitleFile() {
		return subapi;
	}
	
	public SubData getSubData(int ms) {
		if(subapi != null) {
			if(data != null) {
				if((ms < data.beginTime()) || (ms > data.endTime())) {
					data = subapi.getdata(ms);
				}
			}
			else {
				data = subapi.getdata(ms);
			}
			return data;
		}
		return null;
	}
}
