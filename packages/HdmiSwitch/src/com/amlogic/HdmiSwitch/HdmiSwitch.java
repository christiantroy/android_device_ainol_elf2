package com.amlogic.HdmiSwitch;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.os.SystemProperties;
import android.view.WindowManagerPolicy;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;

public class HdmiSwitch extends Activity {
	
	private static final String TAG = "HdmiSwitch";
	
	//private static PowerManager.WakeLock mWakeLock;
	
    static {
    	System.loadLibrary("hdmiswitchjni");
    }
	//public native static int scaleFrameBufferJni(int flag);	
	public native static int freeScaleSetModeJni(int mode);
	public native static int DisableFreeScaleJni(int mode);
	public native static int EnableFreeScaleJni(int mode);
	
	public static final String DISP_CAP_PATH = "/sys/class/amhdmitx/amhdmitx0/disp_cap";
	public static final String MODE_PATH = "/sys/class/display/mode";
	public static final String MODE_PATH_VOUT2 = "/sys/class/display2/mode";	
	public static final String AXIS_PATH = "/sys/class/display/axis";
	
	public static final String CODEC_REG = "/sys/devices/platform/soc-audio/codec_reg";
	public static final String SPK_MUTE = "12 b063";
	public static final String SPK_UNMUTE = "12 b073";
	
	public static final String DISP_MODE_PATH = "/sys/class/amhdmitx/amhdmitx0/disp_mode";
	public static final String HDMI_OFF = "aaa";
	
	public static final String BRIGHTNESS_PATH = "/sys/class/backlight/aml-bl/brightness";
	public static final String FB0_BLANK_PATH = "/sys/class/graphics/fb0/blank";	
	public static final String FB1_BLANK_PATH = "/sys/class/graphics/fb1/blank";
	
	public static final String DISABLE_VIDEO_PATH = "/sys/class/video/disable_video";
	public static final String REQUEST2XSCALE_PATH = "/sys/class/graphics/fb0/request2XScale";
	
	//public static final String SCALE_FB0_PATH = "/sys/class/graphics/fb0/scale";
	//public static final String SCALE_FB1_PATH = "/sys/class/graphics/fb1/scale";
	
	private static final int CONFIRM_DIALOG_ID = 0;
	private static final int MAX_PROGRESS = 15;
	private static final int STOP_PROGRESS = -1;
	private int mProgress;
	private int mProgress2;
	private Handler mProgressHandler;
	
	private static final boolean HDMI_CONNECTED = true;
    private static final boolean HDMI_DISCONNECTED = false;
    private static boolean hdmi_stat = HDMI_DISCONNECTED;
    private static boolean hdmi_stat_old = HDMI_DISCONNECTED;
	
	private AlertDialog confirm_dialog;	
	private static String old_mode = "panel";

	private ListView lv;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.main);
//        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title_layout); 
        
		//PowerManager pm = (PowerManager)getSystemService(Context.POWER_SERVICE);
		//mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
        /* set window size */
        WindowManager wm = getWindowManager();
        Display display = wm.getDefaultDisplay();
        LayoutParams lp = getWindow().getAttributes();
        if (display.getHeight() > display.getWidth()) {
            //lp.height = (int) (display.getHeight() * 0.5);
            lp.width = (int) (display.getWidth() * 1.0);       	
        } else {
        	//lp.height = (int) (display.getHeight() * 0.75);
            lp.width = (int) (display.getWidth() * 0.5);            	
        }
        getWindow().setAttributes(lp);        
        
//        /* close button listener */
//        Button btn_close = (Button) findViewById(R.id.title_btn_right);  
//        btn_close.setOnClickListener(new OnClickListener() {
//			public void onClick(View v) {				
//				finish();
//			}        	
//        }); 
        
        /* check driver interface */        
        TextView tv = (TextView) findViewById(R.id.hdmi_state_str);        
        File file = new File(DISP_CAP_PATH);
        if (!file.exists()) {
        	tv.setText(getText(R.string.driver_api_err) + "[001]");
        	return;
        }
        file = new File(MODE_PATH);
        if (!file.exists()) {
        	tv.setText(getText(R.string.driver_api_err) + "[010]");
        	return;
        }
        file = new File(AXIS_PATH);
        if (!file.exists()) {
        	tv.setText(getText(R.string.driver_api_err) + "[100]");
        	return;
        }
        
        
        /* update hdmi_state_str*/
        if (isHdmiConnected())
        	tv.setText(getText(R.string.hdmi_state_str1));
        else
        	tv.setText(getText(R.string.hdmi_state_str2));

//        /* update hdmi_info_str*/
//        TextView tv2 = (TextView) findViewById(R.id.hdmi_info_str); 
//        if (getCurMode().equals("panel"))
//        	tv2.setVisibility(View.GONE);
//        else {
//        	tv2.setVisibility(View.VISIBLE);
//        	tv2.setText(getText(R.string.hdmi_info_str1));
//        }
        
        /* setup video mode list */
        lv = (ListView) findViewById(R.id.listview); 
        SimpleAdapter adapter = new SimpleAdapter(this,getListData(),R.layout.list_item,        		
        		                new String[]{"item_text","item_img"},        		
        		                new int[]{R.id.item_text,R.id.item_img});        		
        lv.setAdapter(adapter);
        
        /* mode select listener */
        lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int pos,
					long id) {
				Map<String, Object> item = (Map<String, Object>)parent.getItemAtPosition(pos);
				if (item.get("item_img").equals(R.drawable.item_img_unsel)) {					
					old_mode = getCurMode();
//					setMode((String)item.get("mode"));							
//					notifyModeChanged();
//					updateListDisplay();					
//					
//					if (!getCurMode().equals("panel"))
//						showDialog(CONFIRM_DIALOG_ID);
//					else
//						finish();

                    if (SystemProperties.getBoolean("ro.vout.dualdisplay2", false)) {
                        String isCameraBusy = SystemProperties.get("camera.busy", "0");
                        if (!isCameraBusy.equals("0")) {
                            Log.w(TAG, "setDualDisplay, camera is busy");
                            Toast.makeText(HdmiSwitch.this,
        					    getText(R.string.Toast_msg_camera_busy),
        					    Toast.LENGTH_LONG).show(); 
                            return;
                        }                                                         
                    }
			        final String mode = (String)item.get("mode");
			        new Thread("setMode") {
			            @Override
			            public void run() {
		            		setMode(mode);
		                    mProgressHandler.sendEmptyMessage(2);
		                }

			        }.start();  						

				}
				
			}        	
        });    
        
        /* progress handler*/
        mProgressHandler = new HdmiSwitchProgressHandler(); 
        
    }
    
    /** onResume() */
    @Override
    public void onResume() {
    	super.onResume();    
    	
    	/* check driver interface */        
        File file = new File(HdmiSwitch.DISP_CAP_PATH);
        if (!file.exists()) {        	
        	return;
        }
        file = new File(HdmiSwitch.MODE_PATH);
        if (!file.exists()) {        	
        	return;
        }
        file = new File(HdmiSwitch.AXIS_PATH);
        if (!file.exists()) {        	
        	return;
        }
        
    	hdmi_stat_old = isHdmiConnected(); 
    	mProgress2 = 0;
    	mProgressHandler.sendEmptyMessageDelayed(1, 1000); 
    }
    
    /** onPause() */
    @Override
    public void onPause() {
    	super.onPause();
    	
    	mProgress = STOP_PROGRESS;  
    	mProgress2 = STOP_PROGRESS;
    }
   
    
    /** Confirm Dialog */
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
        case CONFIRM_DIALOG_ID:
        	confirm_dialog =  new AlertDialog.Builder(HdmiSwitch.this)
                //.setIcon(R.drawable.dialog_icon)
                .setTitle(R.string.dialog_title)
                .setPositiveButton(R.string.dialog_str_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {   
                    	mProgress = STOP_PROGRESS;   
                    	finish();
                        /* User clicked OK so do some stuff */
                    }
                })
                .setNegativeButton(R.string.dialog_str_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    	mProgress = STOP_PROGRESS;                    	
//                    	setMode(old_mode);
//                    	updateListDisplay();                     	
//                    	notifyModeChanged();
				        final String mode = old_mode;
				        new Thread("setMode") {
				            @Override
				            public void run() {
			            		setMode(mode);
			                    mProgressHandler.sendEmptyMessage(3);
			                }
	
				        }.start(); 
                    	/* User clicked Cancel so do some stuff */                    	
                    }
                })
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
					
					public void onCancel(DialogInterface dialog) {						
						mProgress = STOP_PROGRESS;  										
					}
				})				
                .create();  
        	
            return confirm_dialog;
        }
        
		return null;    	
    }
    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {
    	switch (id) {
    	case CONFIRM_DIALOG_ID: 
            WindowManager wm = getWindowManager();
            Display display = wm.getDefaultDisplay();
            LayoutParams lp = dialog.getWindow().getAttributes();
            if (display.getHeight() > display.getWidth()) {            	
            	lp.width = (int) (display.getWidth() * 1.0);       	
        	} else {        		
        		lp.width = (int) (display.getWidth() * 0.5);            	
        	}
            dialog.getWindow().setAttributes(lp);
        
            ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE)
    		.setText(getText(R.string.dialog_str_cancel) 
    				+ " (" + MAX_PROGRESS + ")");            
            
            mProgress = 0;	                
            mProgressHandler.sendEmptyMessageDelayed(0, 1000);
            break;
    	}
    }  
	
    /** getListData */
    private List<Map<String, Object>> getListData() {    	
    	List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();	 
    	
    	for (String modeStr : getAllMode()) {
        	Map<String, Object> map = new HashMap<String, Object>();  
        	map.put("mode", modeStr);
        	map.put("item_text", getText((Integer)MODE_STR_TABLE.get(modeStr)));
        	if (modeStr.equals(getCurMode()))
        		map.put("item_img", R.drawable.item_img_sel);
        	else
        		map.put("item_img", R.drawable.item_img_unsel);
        	list.add(map);    		
    	}
    	
    	return list;
 	} 
    /** updateListDisplay */
    private void updateListDisplay() {
//        /* update hdmi_info_str*/
//        TextView tv2 = (TextView) findViewById(R.id.hdmi_info_str); 
//        if (getCurMode().equals("panel"))
//        	tv2.setVisibility(View.GONE);
//        else {
//        	tv2.setVisibility(View.VISIBLE);
//        	tv2.setText(getText(R.string.hdmi_info_str1));
//        } 
            	
    	Map<String, Object> list_item;
    	for (int i = 0; i < lv.getAdapter().getCount(); i++) {						
			list_item = (Map<String, Object>)lv.getAdapter().getItem(i);    						
			if (list_item.get("mode").equals(getCurMode()))
				list_item.put("item_img", R.drawable.item_img_sel);
			else
				list_item.put("item_img", R.drawable.item_img_unsel);
		}  
    	((BaseAdapter) lv.getAdapter()).notifyDataSetChanged();  
    }
    
    /** updateActivityDisplay */
    private void updateActivityDisplay() {
    	/* update hdmi_state_str*/
        TextView tv = (TextView) findViewById(R.id.hdmi_state_str);
        if (isHdmiConnected())
        	tv.setText(getText(R.string.hdmi_state_str1));
        else
        	tv.setText(getText(R.string.hdmi_state_str2));
        
//        /* update hdmi_info_str*/
//        TextView tv2 = (TextView) findViewById(R.id.hdmi_info_str); 
//        if (getCurMode().equals("panel"))
//        	tv2.setVisibility(View.GONE);
//        else {
//        	tv2.setVisibility(View.VISIBLE);
//        	tv2.setText(getText(R.string.hdmi_info_str1));
//        }        
        
        /* update video mode list */
        lv = (ListView) findViewById(R.id.listview);        
        SimpleAdapter adapter = new SimpleAdapter(this,getListData(),R.layout.list_item,        		
        		                new String[]{"item_text","item_img"},        		
        		                new int[]{R.id.item_text,R.id.item_img});        		
        lv.setAdapter(adapter);    	
        
        ((BaseAdapter) lv.getAdapter()).notifyDataSetChanged();  
    }
    
    /** check hdmi connection*/
    public static boolean isHdmiConnected() {    
    	try {
    		BufferedReader reader = new BufferedReader(new FileReader(DISP_CAP_PATH), 256);
    		try {
    			return (reader.readLine() == null)? false : true;     			
    		} finally {
    			reader.close();
    		}   
    		
    	} catch (IOException e) { 
    		Log.e(TAG, "IO Exception when read: " + DISP_CAP_PATH, e);   
    		return false;
    	}  
    	
    }
    /** get all support mode*/
    private List<String> getAllMode() {
    	List<String> list = new ArrayList<String>();
    	String modeStr;
    	if (SystemProperties.getBoolean("ro.vout.dualdisplay", false)) {
    	   list.add("480p");
    	   list.add("720p");
    	   list.add("1080p"); 
    	   return list;
    	}
    	
    	list.add("panel");     	
    	
    	//list.add("480i");
    	if(SystemProperties.getBoolean("ro.hdmi480p.enable", true)){
    		list.add("480p");
    	}
    	list.add("720p");
    	//list.add("1080i");
    	list.add("1080p");    
    	/* 	
    	try {
    		BufferedReader reader = new BufferedReader(new FileReader(DISP_CAP_PATH), 256);
    		try {
    			while ((modeStr = reader.readLine()) != null) {
    				modeStr = modeStr.split("\\*")[0]; //720p* to 720p
    				
    				if (MODE_STR_TABLE.containsKey(modeStr))
    					list.add(modeStr);	
    			}
    		} finally {
    			reader.close();
    		}   
    		
    	} catch (IOException e) { 
    		Log.e(TAG, "IO Exception when read: " + DISP_CAP_PATH, e);    		
    	}    	
    	*/
    	return list;
    }

	/** get current mode*/
    public static String getCurMode() {
    	String modeStr;
    	if (SystemProperties.getBoolean("ro.vout.dualdisplay", false)) {
        	try {
        		BufferedReader reader2 = new BufferedReader(new FileReader(MODE_PATH_VOUT2), 32);
        		try {
        			modeStr = reader2.readLine();  
        		} finally {
        			reader2.close();
        		}    		
        		return (modeStr == null)? "720p" : modeStr;   	
        		
        	} catch (IOException e) { 
        		Log.e(TAG, "IO Exception when read: " + MODE_PATH_VOUT2, e);
        		return "720p";
        	}    	    
    	}
    	
    	try {
    		BufferedReader reader = new BufferedReader(new FileReader(MODE_PATH), 32);
    		try {
    			modeStr = reader.readLine();  
    		} finally {
    			reader.close();
    		}    		
    		return (modeStr == null)? "panel" : modeStr;   	
    		
    	} catch (IOException e) { 
    		Log.e(TAG, "IO Exception when read: " + MODE_PATH, e);
    		return "panel";
    	}    	
    }
    
	/** sendTvOutIntent **/
	private void sendTvOutIntent( boolean plugged ) {

        Intent intent = new Intent(WindowManagerPolicy.ACTION_HDMI_PLUGGED);
        intent.putExtra(WindowManagerPolicy.EXTRA_HDMI_PLUGGED_STATE, plugged);
        sendStickyBroadcast(intent);
	}
	private void notifyModeChanged() {
	    if (SystemProperties.getBoolean("ro.vout.dualdisplay", false))
	        return;
	        
		if (getCurMode().equals("panel")) 
			sendTvOutIntent(false);
		else
			sendTvOutIntent(true);
	}
	
    /** set mode */
    public static int setMode(String modeStr) {   
    	//Log.i(TAG, "Set mode = " + modeStr);	
    	if (!modeStr.equals("panel")) {
    		if (!isHdmiConnected())
    			return 0;
    	}
    	if (modeStr.equals(getCurMode()))
    		return 0; 
    	
	    if (SystemProperties.getBoolean("ro.vout.dualdisplay", false)) {    		
    		try {
    		    BufferedWriter writer2 = new BufferedWriter(new FileWriter(MODE_PATH_VOUT2), 32);
        		try {
        			writer2.write(modeStr + "\r\n");    			
        		} finally {
        			writer2.close();
        		}     		
    		} catch (IOException e) { 
        		Log.e(TAG, "IO Exception when write: " + MODE_PATH_VOUT2, e);    		
        		return 1;
    	    }    	        
	        return 0;
	    }
    	
    	try {
    		String briStr = "128";
    		if (modeStr.equals("panel")) {
    			disableHdmi();
    			briStr = getBrightness();
    			setBrightness("0");
    			setFb0Blank("1");
    			disableVideo(true);
    		}
    		
    		BufferedWriter writer = new BufferedWriter(new FileWriter(MODE_PATH), 32);
    		try {
    			writer.write(modeStr + "\r\n");    			
    		} finally {
    			writer.close();
    		} 
    		
    		//do free_scale    		
    		if (getCurMode().equals("panel")) { 
    			setFb0Blank("1");			
    			freeScaleSetModeJni(0);
    			setFb0Blank("0");
    			disableVideo(false);
    			setBrightness(briStr);
    			writeSysfs(REQUEST2XSCALE_PATH, "2");
    		}
    		else if (getCurMode().equals("480p"))
    			freeScaleSetModeJni(1);  
    		else if (getCurMode().equals("720p"))
    			freeScaleSetModeJni(2);  
    		else if (getCurMode().equals("1080i"))
    			freeScaleSetModeJni(3);  
    		else if (getCurMode().equals("1080p"))
    			freeScaleSetModeJni(4);  
 		
    		
//    		//do spk_mute/unmute
//    		if (getCurMode().equals("panel"))
//    			setAudio(SPK_UNMUTE);
//    		else
//    			setAudio(SPK_MUTE);    		
    		
//    		//do 2x scale only for 1080p
//    		if (modeStr.equals("1080p")) {
//    			if (setScale("0x10001") == 0)
//    				scaleFrameBufferJni(1);
//    		}
//    		else {
//    			setScale("0x00000");
//    			scaleFrameBufferJni(0);    			
//    		}
//    		
//    		setAxis(MODE_AXIS_TABLE.get(modeStr));
//    		
//    		//set WakeLock
//    		if (getCurMode().equals("panel")) {
//    			if (mWakeLock.isHeld())
//    				mWakeLock.release();    				
//    		}
//    		else {
//    			if (!mWakeLock.isHeld())
//    				mWakeLock.acquire();    				
//    		}
    		
    		return 0;
    		
    	} catch (IOException e) { 
    		Log.e(TAG, "IO Exception when write: " + MODE_PATH, e);    		
    		return 1;
    	}
    	
    }
    
    /** disable Hdmi*/
    public static int disableHdmi() {
    	try {
        	BufferedWriter writer = new BufferedWriter(new FileWriter(DISP_MODE_PATH), 32);
        		try {
        			writer.write(HDMI_OFF + "\r\n");
        		} finally {
        			writer.close();
        		}    		
        		return 0;
        		
        	} catch (IOException e) { 
        		Log.e(TAG, "IO Exception when write: " + DISP_MODE_PATH, e);
        		return 1;
        	}    	
    }     
    
    /** set axis*/
    public static int setAxis(String axisStr) {
    	try {
        	BufferedWriter writer = new BufferedWriter(new FileWriter(AXIS_PATH), 32);
        		try {
        			writer.write(axisStr + "\r\n");
        		} finally {
        			writer.close();
        		}    		
        		return 0;
        		
        	} catch (IOException e) { 
        		Log.e(TAG, "IO Exception when write: " + AXIS_PATH, e);
        		return 1;
        	}    	
    }    

    private static final String VIDEO2_CTRL_PATH = "/sys/class/video2/clone";
    private static final String VFM_CTRL_PATH = "/sys/class/vfm/map";
    private static final String VIDEO2_FRAME_RATE_PATH = "/sys/module/amvideo2/parameters/clone_frame_rate";
    private static final String VIDEO2_FRAME_WIDTH_PATH = "/sys/module/amvideo2/parameters/clone_frame_scale_width";
    private static final String VIDEO2_SCREEN_MODE_PATH = "/sys/class/video2/screen_mode";
    private static final String VIDEO2_ZOOM_PATH = "/sys/class/video2/zoom";
    
    private static int writeSysfs(String path, String val) {
        if (!new File(path).exists()) {
            Log.e(TAG, "File not found: " + path);
            return 1; 
        }
        
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(path), 64);
            try {
                writer.write(val);
            } finally {
                writer.close();
            }    		
            return 0;
        		
        } catch (IOException e) { 
            Log.e(TAG, "IO Exception when write: " + path, e);
            return 1;
        }                 
    }
    private void setDualDisplay(boolean hdmiPlugged) {
        String isCameraBusy = SystemProperties.get("camera.busy", "0");
 
        if (!isCameraBusy.equals("0")) {
            Log.w(TAG, "setDualDisplay, camera is busy");
            return;
        }    
        
        if (hdmiPlugged) {
            writeSysfs(VIDEO2_CTRL_PATH, "0");
            writeSysfs(VFM_CTRL_PATH, "rm default_ext");
            writeSysfs(VFM_CTRL_PATH, "add default_ext vdin amvideo2");
            writeSysfs(VIDEO2_CTRL_PATH, "1");

            if (getCurMode().equals("720p")) {
                writeSysfs(VIDEO2_FRAME_WIDTH_PATH, "640");
            } else if (getCurMode().equals("1080p")) {
                writeSysfs(VIDEO2_FRAME_WIDTH_PATH, "800");
            } else {
                writeSysfs(VIDEO2_FRAME_WIDTH_PATH, "0");
            }
            writeSysfs(VIDEO2_ZOOM_PATH, "105");
            
            if (getDualDisplayState() == 1) {
                writeSysfs(VIDEO2_SCREEN_MODE_PATH, "1");
                writeSysfs(MODE_PATH_VOUT2, "null");
                writeSysfs(MODE_PATH_VOUT2, "panel");
            }
                         
            
        } else {
            writeSysfs(VIDEO2_CTRL_PATH, "0");
            writeSysfs(VFM_CTRL_PATH, "rm default_ext");
            writeSysfs(VFM_CTRL_PATH, "add default_ext vdin vm amvideo");
            writeSysfs(MODE_PATH_VOUT2, "null");
        }    	
    }
    
    private int getDualDisplayState() {
        return Settings.System.getInt(getContentResolver(),
                    Settings.System.HDMI_DUAL_DISP, 1);
    }    
    
    public static void setDualDisplayStatic(boolean hdmiPlugged, boolean dualEnabled) {
        String isCameraBusy = SystemProperties.get("camera.busy", "0");
 
        if (!isCameraBusy.equals("0")) {
            Log.w(TAG, "setDualDisplay, camera is busy");
            return;
        }    
        
        if (hdmiPlugged) {
            writeSysfs(VIDEO2_CTRL_PATH, "0");
            writeSysfs(VFM_CTRL_PATH, "rm default_ext");
            writeSysfs(VFM_CTRL_PATH, "add default_ext vdin amvideo2");
            writeSysfs(VIDEO2_CTRL_PATH, "1");

            if (getCurMode().equals("720p")) {
                writeSysfs(VIDEO2_FRAME_WIDTH_PATH, "640");
            } else if (getCurMode().equals("1080p")) {
                writeSysfs(VIDEO2_FRAME_WIDTH_PATH, "800");
            } else {
                writeSysfs(VIDEO2_FRAME_WIDTH_PATH, "0");
            }
            
            writeSysfs(VIDEO2_ZOOM_PATH, "105");
            
            if (dualEnabled) {
                writeSysfs(VIDEO2_SCREEN_MODE_PATH, "1");
                writeSysfs(MODE_PATH_VOUT2, "null");
                writeSysfs(MODE_PATH_VOUT2, "panel");
            }
                         
            
        } else {
            writeSysfs(VIDEO2_CTRL_PATH, "0");
            writeSysfs(VFM_CTRL_PATH, "rm default_ext");
            writeSysfs(VFM_CTRL_PATH, "add default_ext vdin vm amvideo");
            writeSysfs(MODE_PATH_VOUT2, "null");
        }    	
    }    
    
    /** video layer control */
    private static int disableVideo(boolean disable) {
    	//Log.i(TAG, "disableVideo: " + disable);
        File file = new File(DISABLE_VIDEO_PATH);
        if (!file.exists()) {        	
        	return 0;
        }    	
    	try {
        	BufferedWriter writer = new BufferedWriter(new FileWriter(DISABLE_VIDEO_PATH), 32);
        		try {
        			if (disable)
        				writer.write("1");
        			else
        				writer.write("2");
        		} finally {
        			writer.close();
        		}    		
        		return 0;
        		
        	} catch (IOException e) { 
        		Log.e(TAG, "IO Exception when write: " + DISABLE_VIDEO_PATH, e);
        		return 1;
        	}    	
    }    
    
    /** set osd blank*/    
    public static int setFb0Blank(String blankStr) {
    	//Log.i(TAG, "setFb0Blank: " + blankStr);
        File file = new File(FB0_BLANK_PATH);
        if (!file.exists()) {        	
        	return 0;
        }    	
    	try {
        	BufferedWriter writer = new BufferedWriter(new FileWriter(FB0_BLANK_PATH), 32);
        		try {
        			writer.write(blankStr);
        		} finally {
        			writer.close();
        		}    		
        		return 0;
        		
        	} catch (IOException e) { 
        		Log.e(TAG, "IO Exception when write: " + FB0_BLANK_PATH, e);
        		return 1;
        	}    	
    }
    private static int setFb1Blank(String blankStr) {
    	//Log.i(TAG, "setFb1Blank: " + blankStr);
        File file = new File(FB1_BLANK_PATH);
        if (!file.exists()) {        	
        	return 0;
        }    	
    	try {
        	BufferedWriter writer = new BufferedWriter(new FileWriter(FB1_BLANK_PATH), 32);
        		try {
        			writer.write(blankStr);
        		} finally {
        			writer.close();
        		}    		
        		return 0;
        		
        	} catch (IOException e) { 
        		Log.e(TAG, "IO Exception when write: " + FB1_BLANK_PATH, e);
        		return 1;
        	}    	
    }    
        
    /** set brightness*/
    public static int setBrightness(String briStr) {
    	//Log.i(TAG, "setBrightness: " + briStr);
        File file = new File(BRIGHTNESS_PATH);
        if (!file.exists()) {        	
        	return 0;
        }    	
    	try {
        	BufferedWriter writer = new BufferedWriter(new FileWriter(BRIGHTNESS_PATH), 32);
        		try {
        			writer.write(briStr);
        		} finally {
        			writer.close();
        		}    		
        		return 0;
        		
        	} catch (IOException e) { 
        		Log.e(TAG, "IO Exception when write: " + BRIGHTNESS_PATH, e);
        		return 1;
        	}    	
    }
	/** get brightness*/
    public static String getBrightness() {
    	String briStr = "128";
        File file = new File(BRIGHTNESS_PATH);
        if (!file.exists()) {        	
        	return briStr;
        }     	
    	try {
    		BufferedReader reader = new BufferedReader(new FileReader(BRIGHTNESS_PATH), 32);
    		try {
    			briStr = reader.readLine();  
    		} finally {
    			reader.close();
    		}    		
    		return (briStr == null)? "128" : briStr;   	
    		
    	} catch (IOException e) { 
    		Log.e(TAG, "IO Exception when read: " + BRIGHTNESS_PATH, e);
    		return "128";
    	}    	
    }
    /** set Audio*/
    public static int setAudio(String audioStr) {
        File file = new File(CODEC_REG);
        if (!file.exists()) {        
        	//Log.w(TAG, "File does not exist: " + CODEC_REG);
        	return 0;  
        }
        
    	try {
        	BufferedWriter writer = new BufferedWriter(new FileWriter(CODEC_REG), 64);
        		try {
        			writer.write(audioStr);        			
        		} finally {
        			writer.close();
        		}    		
        		return 0;
        		
        	} catch (IOException e) { 
        		Log.e(TAG, "IO Exception when write: " + CODEC_REG, e);
        		return 1;
        	}    	
    }    
    
//    /** set scale*/
//    public static int setScale(String scaleStr) {
//        File file = new File(SCALE_FB0_PATH);
//        if (!file.exists()) {        	
//        	return 1;
//        }   
//        file = new File(SCALE_FB1_PATH);
//        if (!file.exists()) {        	
//        	return 1;
//        }
//    	
//    	try {
//        	BufferedWriter writer = new BufferedWriter(new FileWriter(SCALE_FB0_PATH), 32);
//        		try {
//        			writer.write(scaleStr + "\r\n");
//        		} finally {
//        			writer.close();
//        		}   
//
//            	try {
//                	writer = new BufferedWriter(new FileWriter(SCALE_FB1_PATH), 32);
//                		try {
//                			writer.write(scaleStr + "\r\n");
//                		} finally {
//                			writer.close();
//                		}    		
//                		return 0;
//                		
//                	} catch (IOException e) { 
//                		Log.e(TAG, "IO Exception when write: " + SCALE_FB1_PATH, e);
//                		return 1;
//                	} 
//        		
//        	} catch (IOException e) { 
//        		Log.e(TAG, "IO Exception when write: " + SCALE_FB0_PATH, e);
//        		return 1;
//        	}         	
//         	
//    }
    
    /** process handler */
    private class HdmiSwitchProgressHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
            case 0:		// confirm dialog 
                if (mProgress == STOP_PROGRESS) 
                	return;                     
                
                if (mProgress >= MAX_PROGRESS) {  
//                	setMode(old_mode);
//                	notifyModeChanged();
//                	updateListDisplay();                 	
			        final String mode = old_mode;
			        new Thread("setMode") {
			            @Override
			            public void run() {
		            		setMode(mode);
		                    mProgressHandler.sendEmptyMessage(3);
		                }    

			        }.start();                 	

                	confirm_dialog.dismiss();
                } else {
                    mProgress++;                    
                    confirm_dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                    	.setText(getText(R.string.dialog_str_cancel) 
                    			+ " (" + (MAX_PROGRESS - mProgress) + ")");
                    
                    mProgressHandler.sendEmptyMessageDelayed(0, 1000);                    
                }
                break;   
                
            case 1:		// hdmi check
            	if (mProgress2 == STOP_PROGRESS) 
                	return;  
            	
            	hdmi_stat = HdmiSwitch.isHdmiConnected(); 
                if (hdmi_stat_old == HDMI_DISCONNECTED) {            	 
                	if (hdmi_stat == HDMI_CONNECTED) {
                		hdmi_stat_old = hdmi_stat;
                		
                		if (confirm_dialog != null) {
                			mProgress = STOP_PROGRESS;
                			confirm_dialog.dismiss();
                		}
                		
                		updateActivityDisplay();
                	}
                } else {            	
                	if (hdmi_stat == HDMI_DISCONNECTED) {
                		hdmi_stat_old = hdmi_stat;  
                		
                		if (confirm_dialog != null) {
                			mProgress = STOP_PROGRESS;
                			confirm_dialog.dismiss();
                		}
                		
//                		if (!HdmiSwitch.getCurMode().equals("panel")) {
//                     		HdmiSwitch.setMode("panel");
//                     		notifyModeChanged();
//                		}
                		
                		updateActivityDisplay();
                	}
                }  
            	mProgressHandler.sendEmptyMessageDelayed(1, 3000); 
            	break;
            	
            case 2:		// setMode finish, show confirm dialog
                if (SystemProperties.getBoolean("ro.vout.dualdisplay2", false)) {
                    boolean hdmiPlugged = !getCurMode().equals("panel");
                    if (hdmiPlugged) setFb0Blank("1");
                    setDualDisplay(hdmiPlugged);
                    if (hdmiPlugged) mProgressHandler.sendEmptyMessageDelayed(4, 1000); 
                }                           
				notifyModeChanged();
				updateListDisplay();					
				if (!SystemProperties.getBoolean("ro.vout.dualdisplay", false)) {
    				if (!getCurMode().equals("panel"))
    					showDialog(CONFIRM_DIALOG_ID);
    				else
    					finish();
				}   
					         	
            	break;	
            	
            case 3:		// setMode finish
                if (SystemProperties.getBoolean("ro.vout.dualdisplay2", false)) {
                    boolean hdmiPlugged = !getCurMode().equals("panel");
                    if (hdmiPlugged) setFb0Blank("1");
                    setDualDisplay(hdmiPlugged);
                    if (hdmiPlugged) mProgressHandler.sendEmptyMessageDelayed(4, 1000); 
                }                       
				notifyModeChanged();
				updateListDisplay();
          	
            	break;
            	
            case 4:     // delayed panel on
                if (SystemProperties.getBoolean("ro.vout.dualdisplay2", false)) {
                    setFb0Blank("0");
                }
            	break;            	
            }
        }
    }
    
    /** mode <-> mode_str/axis */
	private static final Map<String, Object> MODE_STR_TABLE = new HashMap<String, Object>();
	private static final Map<String, String> MODE_AXIS_TABLE = new HashMap<String, String>();
	static {
		MODE_STR_TABLE.put("panel", R.string.mode_str_panel);
		//MODE_STR_TABLE.put("480i", R.string.mode_str_480i);
		MODE_STR_TABLE.put("480p", R.string.mode_str_480p);
		//MODE_STR_TABLE.put("576i", R.string.mode_str_576i);
		//MODE_STR_TABLE.put("576p", R.string.mode_str_576p);
		MODE_STR_TABLE.put("720p", R.string.mode_str_720p);
		//MODE_STR_TABLE.put("1080i", R.string.mode_str_1080i);
		MODE_STR_TABLE.put("1080p", R.string.mode_str_1080p);		
		
		MODE_AXIS_TABLE.put("panel", "0 0 800 480 0 0 18 18");
		MODE_AXIS_TABLE.put("480i", "0 0 800 480 0 0 18 18");
		MODE_AXIS_TABLE.put("480p", "0 0 800 480 0 0 18 18");
		MODE_AXIS_TABLE.put("576i", "0 48 800 480 0 48 18 18");
		MODE_AXIS_TABLE.put("576p", "0 48 800 480 0 48 18 18");
		MODE_AXIS_TABLE.put("720p", "240 120 800 480 240 120 18 18");
		MODE_AXIS_TABLE.put("1080i", "560 300 800 480 560 300 18 18");
		//MODE_AXIS_TABLE.put("1080p", "560 300 800 480 560 300 18 18");
		MODE_AXIS_TABLE.put("1080p", "160 60 1600 960 160 60 36 36");	//2x scale	
	}
	
	/** fastSwitch func for amlplayer*/
	public static int fastSwitch() {		
        /* check driver interface */        
        File file = new File(HdmiSwitch.DISP_CAP_PATH);
        if (!file.exists()) {        	
        	return 0;
        }
        file = new File(HdmiSwitch.MODE_PATH);
        if (!file.exists()) {        	
        	return 0;
        }
        file = new File(HdmiSwitch.AXIS_PATH);
        if (!file.exists()) {        	
        	return 0;
        }	
        
        /* panel <-> TV*/
        if (getCurMode().equals("panel")) {  
        	String mode = getBestMode();
        	if (mode != null)
        		setMode(mode);
        	return 1;
        } else {
        	setMode("panel");
        	return 1;
        }   
	}
	/** get the best mode */
    private static String getBestMode() {
    	List<String> list = new ArrayList<String>();    	
    	String modeStr;
   	
    	try {
    		BufferedReader reader = new BufferedReader(new FileReader(DISP_CAP_PATH), 256);
    		try {
    			while ((modeStr = reader.readLine()) != null) {
    				modeStr = modeStr.split("\\*")[0]; //720p* to 720p
    				
    				if (MODE_STR_TABLE.containsKey(modeStr))
    					list.add(modeStr);	
    			}
    		} finally {
    			reader.close();
    		}   
    		
    	} catch (IOException e) { 
    		Log.e(TAG, "IO Exception when read: " + DISP_CAP_PATH, e);    		
    	}    	
    	
    	if (list.size() > 0) {    		
    		return list.get(list.size() - 1);
    	} else
    		return null;
    }
    
    //option menu    
    public boolean onCreateOptionsMenu(Menu menu)
    {
   	 String ver_str = null;
   	 try {
			ver_str = getPackageManager().getPackageInfo("com.amlogic.HdmiSwitch", 0).versionName;			
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        menu.add(0, 0, 0, getText(R.string.app_name) + " v" + ver_str);
        return true;
    }    
    
    //fix free_scale for video 
    public static int doBeforePlayVideo() {
		if (!isHdmiConnected())
			return 0;
    	
    	if (!getCurMode().equals("panel")) {
    		if (getCurMode().equals("480p"))
    			DisableFreeScaleJni(1);  
    		else if (getCurMode().equals("720p"))
    			DisableFreeScaleJni(2);  
    		else if (getCurMode().equals("1080i"))
    			DisableFreeScaleJni(3);  
    		else if (getCurMode().equals("1080p"))
    			DisableFreeScaleJni(4);  
    	}    	
		return 0;    	
    }
    public static int doAfterPlayVideo() {
		if (!isHdmiConnected())
			return 0;
    	
    	if (!getCurMode().equals("panel")) {
    		if (getCurMode().equals("480p"))
    			EnableFreeScaleJni(1);  
    		else if (getCurMode().equals("720p"))
    			EnableFreeScaleJni(2);  
    		else if (getCurMode().equals("1080i"))
    			EnableFreeScaleJni(3);  
    		else if (getCurMode().equals("1080p"))
    			EnableFreeScaleJni(4);  
    	}    	
		return 0;   	
    }    
    
}
