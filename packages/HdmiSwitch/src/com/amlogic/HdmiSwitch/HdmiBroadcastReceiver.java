package com.amlogic.HdmiSwitch;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.KeyguardManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemProperties;
import android.util.Log;
import android.view.WindowManagerPolicy;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.widget.Toast;

import android.view.KeyEvent;
import android.view.IWindowManager;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemClock;

import android.hardware.input.InputManager;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class HdmiBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "HdmiBroadcastReceiver";

    // Use a layout id for a unique identifier
    private static final int HDMI_NOTIFICATIONS = R.layout.main;
        
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {						
	        boolean plugged = false;
	        // watch for HDMI plug messages if the hdmi switch exists
	        if (new File("/sys/devices/virtual/switch/hdmi/state").exists()) {	
	            final String filename = "/sys/class/switch/hdmi/state";
	            FileReader reader = null;
	            try {
	                reader = new FileReader(filename);
	                char[] buf = new char[15];
	                int n = reader.read(buf);
	                if (n > 1) {
	                    plugged = 0 != Integer.parseInt(new String(buf, 0, n-1));
	                }
	            } catch (IOException ex) {
	                Log.w(TAG, "Couldn't read hdmi state from " + filename + ": " + ex);
	            } catch (NumberFormatException ex) {
	                Log.w(TAG, "Couldn't read hdmi state from " + filename + ": " + ex);
	            } finally {
	                if (reader != null) {
	                    try {
	                        reader.close();
	                    } catch (IOException ex) {
	                    }
	                }
	            }
	        }
	        
	        if (plugged) {
                NotificationManager nM = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
                
                CharSequence text = context.getText(R.string.hdmi_state_str1);     
                Notification notification = new Notification(R.drawable.stat_connected, text, System.currentTimeMillis());  

                Intent it = new Intent(context, HdmiSwitch.class);
                it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                PendingIntent contentIntent = PendingIntent.getActivity(context, 0, it, 0);        
                notification.setLatestEventInfo(context, context.getText(R.string.app_name), text, contentIntent);

                nM.notify(HDMI_NOTIFICATIONS, notification);	        
	        }
	        		
        }    	

        if (WindowManagerPolicy.ACTION_HDMI_HW_PLUGGED.equals(intent.getAction())) {
            //Log.d(TAG, "onReceive: " + intent.getAction());
            boolean plugged = intent.getBooleanExtra(WindowManagerPolicy.EXTRA_HDMI_PLUGGED_STATE, false); 
            if(plugged){
                NotificationManager nM = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
                
                CharSequence text = context.getText(R.string.hdmi_state_str1);     
                Notification notification = new Notification(R.drawable.stat_connected, text, System.currentTimeMillis());  

                Intent it = new Intent(context, HdmiSwitch.class);
                it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                PendingIntent contentIntent = PendingIntent.getActivity(context, 0, it, 0);        
                notification.setLatestEventInfo(context, context.getText(R.string.app_name), text, contentIntent);

                nM.notify(HDMI_NOTIFICATIONS, notification);
                onHdmiPlugged(context);
                
            }else{
                onHdmiUnplugged(context);
                 
                NotificationManager nM = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
                nM.cancel(HDMI_NOTIFICATIONS); 
            }
        }
    }
    
    
    private void onHdmiPlugged(Context context) {
        if (!SystemProperties.getBoolean("ro.vout.dualdisplay", false)) {
            if (HdmiSwitch.getCurMode().equals("panel")) {
                // camera in-use
                String isCameraBusy = SystemProperties.get("camera.busy", "0");
                if (!isCameraBusy.equals("0")) {
                    Log.w(TAG, "onHdmiPlugged, camera is busy");
                    Toast.makeText(context,
                        context.getText(R.string.Toast_msg_camera_busy),
                        Toast.LENGTH_LONG).show();                     
                    return;
                }
                // keyguard on
                KeyguardManager mKeyguardManager = (KeyguardManager) context.getSystemService(context.KEYGUARD_SERVICE); 
        		if (mKeyguardManager != null && mKeyguardManager.inKeyguardRestrictedInputMode()) {
        		    Log.w(TAG, "onHdmiPlugged, keyguard on");
        			return;
        		}
        		
                HdmiSwitch.setFb0Blank("1");
                
                /// send BACK key to stop other player
                sendKeyEvent(KeyEvent.KEYCODE_HOME);                
                
                HdmiSwitch.setMode("720p");
                Intent it = new Intent(WindowManagerPolicy.ACTION_HDMI_PLUGGED);
                it.putExtra(WindowManagerPolicy.EXTRA_HDMI_PLUGGED_STATE, true);
                context.sendStickyBroadcast(it);
                if (SystemProperties.getBoolean("ro.vout.dualdisplay2", false)) {                        
                    int dualEnabled = Settings.System.getInt(context.getContentResolver(),
                                            Settings.System.HDMI_DUAL_DISP, 1);
                    HdmiSwitch.setDualDisplayStatic(true, (dualEnabled == 1));
                } 
                HdmiSwitch.setFb0Blank("0");            
            }
        }
    }    
    
    private void onHdmiUnplugged(Context context) {
         if (!SystemProperties.getBoolean("ro.vout.dualdisplay", false)) {
             if (!HdmiSwitch.getCurMode().equals("panel")) {
                
                /// 1. send broadcast to stop player
//                Intent it = new Intent(WindowManagerPolicy.ACTION_HDMI_PLUGGED);
//                it.putExtra(WindowManagerPolicy.EXTRA_HDMI_PLUGGED_STATE, false);
//                context.sendStickyBroadcast(it);   
                
                /// 2. send BACK key to stop player
                sendKeyEvent(KeyEvent.KEYCODE_HOME);
                
                /// 3. kill player
           
//                        HdmiSwitch.setMode("panel");
//                        //Intent it = new Intent(WindowManagerPolicy.ACTION_HDMI_PLUGGED);
//                        //it.putExtra(WindowManagerPolicy.EXTRA_HDMI_PLUGGED_STATE, false);
//                        //context.sendStickyBroadcast(it);
//                        if (SystemProperties.getBoolean("ro.vout.dualdisplay2", false)) {                        
//                            int dualEnabled = Settings.System.getInt(context.getContentResolver(),
//                                                    Settings.System.HDMI_DUAL_DISP, 1);
//                            HdmiSwitch.setDualDisplayStatic(plugged, (dualEnabled == 1));
//                        }       
    			context.startService(new Intent(context, 
    				HdmiDelayedService.class));                 
             }
         }    
    }

    /**
     * Send a single key event.
     *
     * @param event is a string representing the keycode of the key event you
     * want to execute.
     */
    private void sendKeyEvent(int keyCode) {
        int eventCode = keyCode;
        long now = SystemClock.uptimeMillis();
        /*try {
            KeyEvent down = new KeyEvent(now, now, KeyEvent.ACTION_DOWN, eventCode, 0);
            KeyEvent up = new KeyEvent(now, now, KeyEvent.ACTION_UP, eventCode, 0);
            (IWindowManager.Stub
                .asInterface(ServiceManager.getService("window")))
                .injectInputEventNoWait(down);
            (IWindowManager.Stub
                .asInterface(ServiceManager.getService("window")))
                .injectInputEventNoWait(up);
        } catch (RemoteException e) {
            Log.i(TAG, "DeadOjbectException");
        }*/
	InputManager im = InputManager.getInstance();
	KeyEvent down = new KeyEvent(now, now, KeyEvent.ACTION_DOWN, eventCode, 0);
        KeyEvent up = new KeyEvent(now, now, KeyEvent.ACTION_UP, eventCode, 0);
	im.injectInputEvent(down, InputManager.INJECT_INPUT_EVENT_MODE_ASYNC);
	im.injectInputEvent(up, InputManager.INJECT_INPUT_EVENT_MODE_ASYNC);
    }

}