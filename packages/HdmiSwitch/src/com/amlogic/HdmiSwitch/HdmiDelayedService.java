package com.amlogic.HdmiSwitch;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.os.SystemProperties;
import android.view.WindowManagerPolicy;

import android.util.Log;

public class HdmiDelayedService extends Service {
    private static final String TAG = "HdmiDelayedService";

    private static int DELAY = 3*1000;	

    private Handler mProgressHandler;
    private Context mContext;
    
    @Override
    public void onCreate() {
        mContext = this;
        mProgressHandler = new DelayedHandler();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {        
        /* start after DELAY */        
        if (mProgressHandler != null) 
            mProgressHandler.sendEmptyMessageDelayed(0, DELAY);
                
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
    }    

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private class DelayedHandler extends Handler {
    @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            onDelayedProcess();
        }
    }
	
    private void onDelayedProcess() {
        HdmiSwitch.setMode("panel");
        if (SystemProperties.getBoolean("ro.vout.dualdisplay2", false)) {                        
            int dualEnabled = Settings.System.getInt(mContext.getContentResolver(),
                                    Settings.System.HDMI_DUAL_DISP, 1);
            HdmiSwitch.setDualDisplayStatic(false, (dualEnabled == 1));
        }
        Intent it = new Intent(WindowManagerPolicy.ACTION_HDMI_PLUGGED);
        it.putExtra(WindowManagerPolicy.EXTRA_HDMI_PLUGGED_STATE, false);
        mContext.sendStickyBroadcast(it);
    }	
	
}
