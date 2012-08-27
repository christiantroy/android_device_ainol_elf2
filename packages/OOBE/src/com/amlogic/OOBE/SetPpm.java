package com.amlogic.OOBE;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.os.SystemProperties;

public class SetPpm extends Service{

	public String outputmode;
	static {
		System.loadLibrary("setppmjni");
	}
	public native static int setPpmDispJni(int width, int height);
	
	@Override
	public IBinder onBind(Intent intent){
		return null;
	}
	
	@Override
	public void onCreate(){
		super.onCreate();
  		Log.d("SetPpm","onCreate");
		outputmode = SystemProperties.get("ubootenv.var.outputmode");
		int m1080scale = SystemProperties.getInt("ro.platform.has.1080scale", 0);
		if((m1080scale == 2) 
			|| ((m1080scale == 1) && ((outputmode.equals("1080i")) || (outputmode.equals("1080p")) || (outputmode.equals("720p"))))){
			setPpmDispJni(1280,720);
		}
  		Log.d("SetPpm","stopSelf");
  		this.stopSelf();
	}
	
	@Override
	public void onDestroy(){
  			Log.d("SetPpm","onDestroy");
		super.onDestroy();
	}
}