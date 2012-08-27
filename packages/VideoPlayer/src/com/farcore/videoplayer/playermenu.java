package com.farcore.videoplayer;
 
import android.os.storage.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import android.os.SystemProperties;

import com.subtitleparser.*;
import com.subtitleview.SubtitleView;
import android.content.Context;
import com.farcore.playerservice.*;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.res.Configuration;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.*;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.provider.Settings.System;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.provider.MediaStore;
import android.util.Log;
import android.view.*;
import android.widget.*;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.storage.StorageVolume;
import android.net.Uri;
import android.content.SharedPreferences;  

import java.io.FileOutputStream;
import java.lang.Thread.UncaughtExceptionHandler;
public class playermenu extends Activity {
	private static String TAG = "playermenu";
	private static String codec_mips = null;
	private static String InputFile = "/sys/class/audiodsp/codec_mips";
	private static String OutputFile = "/sys/devices/system/cpu/cpu0/cpufreq/scaling_min_freq";
	private static String ScaleaxisFile= "/sys/class/graphics/fb0/scale_axis";
	private static String ScaleFile= "/sys/class/graphics/fb0/scale";
	private static String RequestScaleFile= "/sys/class/graphics/fb0/request2XScale";
	public static final String PREFS_NAME = "subtitlesetting"; 


	private static final int SET_OSD_ON= 1;
	private static final int SET_OSD_OFF= 2;

	private boolean mHdmiPlugged;
	private boolean mPaused;

  /** Called when the activity is first created. */
	private int totaltime = 0;
	private int curtime = 0;
	private int playPosition = 0;
	private int cur_audio_stream = 0;
	private int total_audio_num = 0;
	private int cur_audio_channel = 0;
	private int audio_flag = 0;
	
	private final int PLAY_RESUME = 0;
	private final int PLAY_MODE = 1;
	private final int AUDIOTRACK = 2;
	private final int DISPLAY = 3;
	private final int BRIGHTNESS = 4;
	private final int PLAY3D = 5;

	private boolean backToFileList = false;
	//private boolean progressSliding = false;
	private boolean INITOK = false;
	private boolean FF_FLAG = false;
	private boolean FB_FLAG = false;

    // The ffmpeg step is 2*step
	private int FF_LEVEL = 0;
	private int FB_LEVEL = 0;
    private static int FF_MAX = 5;
    private static int FB_MAX = 5;
    private static int FF_SPEED[] = {0, 2, 4, 8, 16, 32};
    private static int FB_SPEED[] = {0, 2, 4, 8, 16, 32};
    private static int FF_STEP[] =  {0, 1, 2, 4, 8, 16};
    private static int FB_STEP[] =  {0, 1, 2, 4, 8, 16};

	private boolean NOT_FIRSTTIME = false;
	private static final int MID_FREESCALE = 0x10001;
    private boolean fb32 = false;
    
    private int seek = 0;
    private int seek_cur_time = 0;
    //for repeat mode;
	private boolean playmode_switch = true;
    private static int m_playmode = 1;
    private static final int REPEATLIST = 1;
    private static final int REPEATONE = 2;
  
    private SeekBar myProgressBar = null;
    private ImageButton play = null;
    private ImageButton fastforword = null;
    private ImageButton fastreverse = null;
	private TextView cur_time = null;
	private TextView total_time = null;
	private LinearLayout infobar = null;
	private LinearLayout morbar = null;
	private LinearLayout subbar = null;
	private LinearLayout otherbar = null;
	private LinearLayout infodialog = null;
	private AlertDialog confirm_dialog = null;
	private BroadcastReceiver mReceiver = null;
	private int morebar_status = 0;
	private boolean backToOtherAPK = true;

	Timer timer = new Timer();
	Toast ff_fb = null;
	Toast toast = null;
	public MediaInfo bMediaInfo = null;
	private static int PRE_NEXT_FLAG = 0;
	private int resumeSecond = 8;
	private int player_status = VideoInfo.PLAYER_UNKNOWN;

	//if already set 2xscale
	private boolean bSet2XScale = false;

	// MBX freescale mode
	private int m1080scale = 0;
	private String outputmode = "720p";
	private static String VideoAxisFile= "/sys/class/video/axis";
	private static String RegFile= "/sys/class/display/wr_reg";
	private static String Fb0Blank= "/sys/class/graphics/fb0/blank";
	private static String Fb1Blank= "/sys/class/graphics/fb1/blank";
	private static final String STR_OUTPUT_MODE = "ubootenv.var.outputmode";
	
	private boolean intouch_flag = false;
	private int item_position_selected, item_position_first, fromtop_piexl, item_position_selected_init;
	private boolean item_init_flag = true;
	private ArrayList<Integer> fileDirectory_position_selected = new ArrayList<Integer>();
	private ArrayList<Integer> fileDirectory_position_piexl = new ArrayList<Integer>();
	
	//for subtitle
	private SubtitleUtils subtitleUtils = null;
	private SubtitleView subTitleView = null;

	//peter added it in 2011.1027,just for gadmei Glass-Less 3D pad,
	private SubtitleView subTitleView_sm = null; 
	private boolean is3DVideoDisplayFlag = false;
	private int sub_sm_offset_x=0;
	private int sub_sm_offset_y =0;
	private int view_mode = 0;
	private int VIEW_3D_MODE = 4;
	
	private subview_set sub_para = null;
	private int sub_switch_state = 0;
	private int sub_font_state = 0;
	private int sub_color_state = 0;
	private int sub_position_v_state = 0;
	private TextView t_subswitch =null ;
	private TextView t_subsfont=null ;
	private TextView t_subscolor=null ;
	private TextView t_subsposition_v=null ;
	private TextView morebar_tileText =null;
    private boolean touchVolFlag = false;
	private WindowManager mWindowManager;
	private int[] angle_table = {0, 1, 2, 3};
	private String[] m_brightness= {"1","2","3","4","5","6"};
	private int mode_3d = 0;
    private enum Mode_3D {
		MODE_3D_DISABLE,
		MODE_3D_AUTO,
		MODE_3D_LR,
		MODE_3D_BT,
		MODE_3D_TO_2D_L,
		MODE_3D_TO_2D_R,
		MODE_3D_TO_2D_T,
		MODE_3D_TO_2D_B,
		MODE_3D_TO_2D_AUTO_1,
		MODE_3D_TO_2D_AUTO_2,	
		MODE_2D_TO_3D,
		MODE_FIELD_DEPTH,	
		MODE_3D_AUTO_SWITCH,
		MODE_3D_LR_SWITCH,
		MODE_3D_BT_SWITCH, 
		MODE_3D_FULL_OFF,
		MODE_3D_LR_FULL,
		MODE_3D_BT_FULL,
		MODE_3D_GRATING_ON,
		MODE_3D_GRATING_OFF,
		
    }
	
	private int[] string_3d_id = {
		R.string.setting_3d_diable,
		R.string.setting_3d_auto,	
		R.string.setting_3d_lr,
		R.string.setting_3d_bt,
		R.string.setting_3d_2d_l,
		R.string.setting_3d_2d_r,
		R.string.setting_3d_2d_t,
		R.string.setting_3d_2d_b,
		R.string.setting_3d_2d_auto1,
		R.string.setting_3d_2d_auto2,	
		R.string.setting_2d_3d,
		R.string.setting_3d_field_depth,
		R.string.setting_3d_auto_switch,
		R.string.setting_3d_lr_switch,
		R.string.setting_3d_tb_switch,
		R.string.setting_3d_full_off,
		R.string.setting_3d_lr_full,
		R.string.setting_3d_tb_full,
		R.string.setting_3d_grating_open,
		R.string.setting_3d_grating_close,
	};
	
//	private static final String ACTION_HDMISWITCH_MODE_CHANGED =
//		"com.amlogic.HdmiSwitch.HDMISWITCH_MODE_CHANGED";
	
	private boolean mSuspendFlag = false;
	PowerManager.WakeLock mScreenLock = null;
	private Handler mDelayHandler;
	private final static long ScrnOff_delay = 2*1000;
    private final static int GETROTATION_TIMEOUT = 500;
	private final static int GETROTATION = 0x0001;
	private int mLastRotation;
	
	public void setAngleTable() {
		String hwrotation = SystemProperties.get("ro.sf.hwrotation");
		if(hwrotation == null) {
			angle_table[0] = 0;
			angle_table[1] = 1;
			angle_table[2] = 2;
			angle_table[3] = 3;
			Log.e(TAG, "setAngleTable, Can not get hw rotation!");
			return;
		}
		
		if(hwrotation.equals("90")) {
			angle_table[0] = 1;
			angle_table[1] = 2;
			angle_table[2] = 3;
			angle_table[3] = 0;
		}
		else if(hwrotation.equals("180")) {
			angle_table[0] = 2;
			angle_table[1] = 3;
			angle_table[2] = 0;
			angle_table[3] = 1;
		}
		else if(hwrotation.equals("270")) {
			angle_table[0] = 3;
			angle_table[1] = 0;
			angle_table[2] = 1;
			angle_table[3] = 2;
		}
		else {
			angle_table[0] = 0;
			angle_table[1] = 1;
			angle_table[2] = 2;
			angle_table[3] = 3;
		}
	}
	
    private SimpleAdapter getMorebarListAdapter(int id, int pos) {
        return new SimpleAdapter(this, getMorebarListData(id, pos),
            R.layout.list_row, new String[] {
                "item_name", "item_sel"
            }, new int[] {
                R.id.Text01, R.id.imageview,
            });
    }

    private List<? extends Map<String, ?>> getMorebarListData(int id, int pos) {
        // TODO Auto-generated method stub
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Map<String, Object> map;

        switch (id) {
            case PLAY_RESUME:
                map = new HashMap<String, Object>();
                map.put("item_name", getResources().getString(R.string.str_on));
                map.put("item_sel", R.drawable.item_img_unsel);
                list.add(map);
                map = new HashMap<String, Object>();
                map.put("item_name", getResources().getString(R.string.str_off));
                map.put("item_sel", R.drawable.item_img_unsel);
                list.add(map);

                list.get(pos).put("item_sel", R.drawable.item_img_sel);
                break;
				
            case PLAY_MODE:
                map = new HashMap<String, Object>();
                map.put("item_name", getResources().getString(R.string.setting_playmode_repeatall));
                map.put("item_sel", R.drawable.item_img_unsel);
                list.add(map);
                map = new HashMap<String, Object>();
                map.put("item_name", getResources().getString(R.string.setting_playmode_repeatone));
                map.put("item_sel", R.drawable.item_img_unsel);
                list.add(map);

                list.get(pos).put("item_sel", R.drawable.item_img_sel);
                break;

            case AUDIOTRACK:
                if (AudioTrackOperation.AudioStreamFormat.size() < bMediaInfo.getAudioTrackCount())
                    AudioTrackOperation.setAudioStream(bMediaInfo);
                int size_as = AudioTrackOperation.AudioStreamFormat.size();
				if (size_as > 0) 
				{
	                for (int i = 0; i < size_as; i++) {
	                	if(AudioTrackOperation.AudioStreamFormat.get(i) != "UNSUPPORT"){
		                    map = new HashMap<String, Object>();
		                    map.put("item_name", AudioTrackOperation.AudioStreamFormat.get(i));
		                    map.put("item_sel", R.drawable.item_img_unsel);
		                    list.add(map);
	                	}
	                }
	                list.get(pos).put("item_sel", R.drawable.item_img_sel);
				}
                break;

            case DISPLAY:
                map = new HashMap<String, Object>();
                map.put("item_name", getResources().getString(R.string.setting_displaymode_normal));
                map.put("item_sel", R.drawable.item_img_unsel);
                list.add(map);
                map = new HashMap<String, Object>();
                map.put("item_name", getString(R.string.setting_displaymode_fullscreen));
                map.put("item_sel", R.drawable.item_img_unsel);
                list.add(map);
                map = new HashMap<String, Object>();
                map.put("item_name", "4:3");
                map.put("item_sel", R.drawable.item_img_unsel);
                list.add(map);
                map = new HashMap<String, Object>();
                map.put("item_name", "16:9");
                map.put("item_sel", R.drawable.item_img_unsel);
                list.add(map);
                if(SystemProperties.getBoolean("3D_setting.enable", false)){ 
                	if(is3DVideoDisplayFlag){//judge is 3D                		
		                map = new HashMap<String, Object>();
		                map.put("item_name", getResources().getString(R.string.setting_displaymode_normal_noscaleup));
		                map.put("item_sel", R.drawable.item_img_unsel);
		                list.add(map);
                	}
                }
                list.get(pos).put("item_sel", R.drawable.item_img_sel);
                break;

            case BRIGHTNESS:
                int size_bgh = m_brightness.length;
                for (int i = 0; i < size_bgh; i++) {
                    map = new HashMap<String, Object>();
                    map.put("item_name", m_brightness[i].toString());
                    map.put("item_sel", R.drawable.item_img_unsel);
                    list.add(map);
                }

                list.get(pos).put("item_sel", R.drawable.item_img_sel);
                break;
			case PLAY3D:
			    int size_3d =  string_3d_id.length;
				for (int i = 0; i < size_3d; i++) {
                  map = new HashMap<String, Object>();
                    map.put("item_name", getResources().getString(string_3d_id[i]));
                    map.put("item_sel", R.drawable.item_img_unsel);
                    list.add(map);
				}
				
				list.get(pos).put("item_sel", R.drawable.item_img_sel);
			    break;

            default:
                break;
        }

        return list;
    }

	protected void waitForHideVideoBar(){	//videoBar auto hide
    	final Handler handler = new Handler(){   
            public void handleMessage(Message msg) {   
                switch (msg.what) {       
                case 0x40:       
                	hideVideoBar();
                    break;       
                }       
                super.handleMessage(msg);   
            }  
        };   
		
        TimerTask task = new TimerTask(){   
            public void run() {
				if(!touchVolFlag){
	                Message message = new Message();       
	                message.what = 0x40;       
	                handler.sendMessage(message);  
				}				
            }     
        };   
        
        timer.cancel();
        timer = new Timer();
    	timer.schedule(task, 5000);
    }

/// patch for hide OSD
    private static final String OSD_BLANK_PATH = "/sys/class/graphics/fb0/blank";
    private static final String OSD_BLOCK_MODE_PATH = "/sys/class/graphics/fb0/block_mode";
    
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
    private boolean isSubtitleOn() {
        if (sub_para != null && sub_para.totalnum > 0 && sub_para.sub_id != null)
        {
			AmPlayer.setSubOnFlag(true);//tony.wang
            return true;
        }
		else
		{
            AmPlayer.setSubOnFlag(false);//tony.wang    
       		return false;
		}
    }
    private int getOSDRotation() {
        Display display = getWindowManager().getDefaultDisplay();
        int orientation = display.getOrientation();
        int hw_rotation = SystemProperties.getInt("ro.sf.hwrotation", 0);
        return (orientation * 90 + hw_rotation) % 360;
    }
    void setOSDOnOff(boolean on) {
        if(confirm_dialog != null && confirm_dialog.isShowing())
            on = true;
		if(isSubtitleOn())
			on = true;
        if (!on && !mPaused) {
            if (isSubtitleOn()) {
                int ori = getOSDRotation();
                if (ori == 90)
                    writeSysfs(OSD_BLOCK_MODE_PATH, "0x20001"); //OSD ver blk0 enable
                else if (ori == 180)
                    writeSysfs(OSD_BLOCK_MODE_PATH, "0x10001"); //OSD hor blk0 enable
                else if (ori == 270)
                    writeSysfs(OSD_BLOCK_MODE_PATH, "0x20008"); //OSD ver blk3 enable
                else                
                    writeSysfs(OSD_BLOCK_MODE_PATH, "0x10008"); //OSD hor blk3 enable
                    
            } else {
                writeSysfs(OSD_BLANK_PATH, "1");
				AmPlayer.setOSDOnFlag(false);//tony.wang
            }
        } else {
            writeSysfs(OSD_BLANK_PATH, "0");
            writeSysfs(OSD_BLOCK_MODE_PATH, "0");
			AmPlayer.setOSDOnFlag(true);//tony.wang
        }
    }    
/// patch for hide OSD
	private void hideVideoBar(){
		if(null != morbar){
			morbar.setVisibility(View.GONE);
	    	getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,   
	    			WindowManager.LayoutParams.FLAG_FULLSCREEN);
		}
	}

	private void showVideoBar(){
		if(null != morbar){
			morbar.setVisibility(View.VISIBLE);
			if(AmPlayer.getProductType() == 1){
				getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
			}
			else{
				getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
			}
		}
    }

	private static SurfaceHolder.Callback mSHCallback = new SurfaceHolder.Callback() {
		public void surfaceChanged(SurfaceHolder holder, int format,
									int w, int h) {
			Log.d(TAG, "surfaceChanged");
			//initSurface(holder);
		}
		public void surfaceCreated(SurfaceHolder holder) {
			Log.d(TAG, "surfaceCreated");
			initSurface(holder);
		}
		public void surfaceDestroyed(SurfaceHolder holder) {
			Log.d(TAG, "surfaceDestroyed");
		}
		private void initSurface(SurfaceHolder h) {
			Canvas c = null;
			try {
				Log.d(TAG, "initSurface");
				c = h.lockCanvas();
			} finally {
				if (c != null)
					h.unlockCanvasAndPost(c);
			}
		}
	};

	private void initVideoView(int resourceId) {
		if (fb32) {
			Log.d(TAG, "initVideoView");
			VideoView v = (VideoView)findViewById(resourceId);
			if (v != null) {
			Log.d(TAG, "initVideoView 2");
				v.getHolder().addCallback(mSHCallback);
				v.getHolder().setFormat(PixelFormat.VIDEO_HOLE);
			}
		}
		else
			Log.d(TAG, "!initVideoView");
	}


    private void videobar() {
		FrameLayout baselayout2 = (FrameLayout)findViewById(R.id.BaseLayout2);
		if(AmPlayer.getProductType() == 1){
			if (SettingsVP.display_mode.equals("480p") && SettingsVP.panel_height > 480) {
				FrameLayout.LayoutParams frameParams = (FrameLayout.LayoutParams) baselayout2.getLayoutParams();
				if (SettingsVP.panel_width > 720)
				    frameParams.width = 720;
				frameParams.height = 480 - 50;
				frameParams.gravity = Gravity.TOP;
				baselayout2.setLayoutParams(frameParams);
			} else if (SettingsVP.display_mode.equals("720p") && SettingsVP.panel_height > 720) {
				FrameLayout.LayoutParams frameParams = (FrameLayout.LayoutParams) baselayout2.getLayoutParams();
				if (SettingsVP.panel_width > 1280)
				    frameParams.width = 1280;
				frameParams.height = 720 - 50;
				frameParams.gravity = Gravity.TOP;
				baselayout2.setLayoutParams(frameParams);
			}
		}

		if(SystemProperties.getBoolean("3D_setting.enable", false)){
		    	subTitleView_sm = (SubtitleView) findViewById(R.id.subTitle_more_sm);
		    	subTitleView_sm.setGravity(Gravity.CENTER);
		    	subTitleView_sm.setTextColor(android.graphics.Color.GRAY);
		    	subTitleView_sm.setTextSize(sub_para.font);
		    	subTitleView_sm.setTextStyle(Typeface.BOLD);		
		
		}
    
		
		subbar = (LinearLayout)findViewById(R.id.LinearLayout_sub);
		subbar.setVisibility(View.GONE);
		
		otherbar = (LinearLayout)findViewById(R.id.LinearLayout_other);
		morebar_tileText = (TextView)findViewById(R.id.more_title);
		otherbar.setVisibility(View.GONE);
		
		infodialog = (LinearLayout)findViewById(R.id.dialog_layout);
		infodialog.setVisibility(View.GONE);
    	morbar = (LinearLayout)findViewById(R.id.morebarLayout);
    	morbar.setVisibility(View.GONE);
    	
    	ImageButton resume = (ImageButton) findViewById(R.id.ResumeBtn);
		setOnTouchHoldShow(resume);
    	resume.setOnClickListener(new View.OnClickListener() {
    		public void onClick(View v) {
			    otherbar.setVisibility(View.VISIBLE);
			    subTitleView.setViewStatus(false);
				if(SystemProperties.getBoolean("3D_setting.enable", false)&&subTitleView_sm!=null){
				     subTitleView_sm.setViewStatus(false);
				}				
				morbar.setVisibility(View.GONE);
				morebar_tileText.setText(R.string.setting_resume);
				
				ListView listView = (ListView)findViewById(R.id.AudioListView);
                listView.setAdapter(getMorebarListAdapter(PLAY_RESUME, SettingsVP.getParaBoolean(SettingsVP.RESUME_MODE)
                    ? 0 : 1));
				listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					    if (position == 0)
						    SettingsVP.putParaBoolean(SettingsVP.RESUME_MODE, true);
						else if (position == 1)
						    SettingsVP.putParaBoolean(SettingsVP.RESUME_MODE, false);
						otherbar.setVisibility(View.GONE);
						subTitleView.setViewStatus(true);
						if(subTitleView_sm!=null&&SystemProperties.getBoolean("3D_setting.enable", false)){
						     subTitleView_sm.setViewStatus(true);
						}			
						morbar.setVisibility(View.VISIBLE);
				    ImageButton resume = (ImageButton) findViewById(R.id.ResumeBtn);
				    resume.requestFocus();

					waitForHideOsd();
					}
				});
				otherbar.requestFocus();
				morebar_status = R.string.setting_resume;

				timer.cancel();
			} 
		});
    	
    	ImageButton playmode = (ImageButton) findViewById(R.id.PlaymodeBtn);
    	if(playmode_switch) {
			setOnTouchHoldShow(playmode);
    		playmode.setOnClickListener(new View.OnClickListener() {
    			public void onClick(View v) {
    				otherbar.setVisibility(View.VISIBLE);
    				subTitleView.setViewStatus(false);
				if(subTitleView_sm!=null&&SystemProperties.getBoolean("3D_setting.enable", false)){
				     subTitleView_sm.setViewStatus(false);
				}					
    				morbar.setVisibility(View.GONE);
    				morebar_tileText.setText(R.string.setting_playmode);
    				ListView listView = (ListView)findViewById(R.id.AudioListView);
                    listView.setAdapter(getMorebarListAdapter(PLAY_MODE, m_playmode - 1));
    				listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {	
    				    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    				    	if (position == 0)
    				    		m_playmode = REPEATLIST;
    				    	else if (position == 1)
    				    		m_playmode = REPEATONE;
    				    	otherbar.setVisibility(View.GONE);
    				    	subTitleView.setViewStatus(true);
					if(subTitleView_sm!=null&&SystemProperties.getBoolean("3D_setting.enable", false)){
					     subTitleView_sm.setViewStatus(true);
					}							
    				    	morbar.setVisibility(View.VISIBLE);
							    ImageButton playmode = (ImageButton) findViewById(R.id.PlaymodeBtn);
							    playmode.requestFocus();

							waitForHideOsd();
    				    }
    				});
    				otherbar.requestFocus();
    				morebar_status = R.string.setting_playmode;

					timer.cancel();
    			}
    		});
    	}
    	else {
    		playmode.setImageDrawable(getResources().getDrawable(R.drawable.mode_disable));
    	}
    	
		if(SystemProperties.getBoolean("3D_setting.enable", false)) {
			ImageButton play3d = (ImageButton) findViewById(R.id.Play3DBtn);
			setOnTouchHoldShow(play3d);
			play3d.setVisibility(View.VISIBLE);
			play3d.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					otherbar.setVisibility(View.VISIBLE);
					subTitleView.setViewStatus(false);
					if(subTitleView_sm!=null&&SystemProperties.getBoolean("3D_setting.enable", false)){
					     subTitleView_sm.setViewStatus(false);
					}					
					morbar.setVisibility(View.GONE);
					
					morebar_tileText.setText(R.string.setting_3d_mode);
					ListView listView = (ListView)findViewById(R.id.AudioListView);
					listView.setAdapter(getMorebarListAdapter(PLAY3D, mode_3d));
					listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
						public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
							mode_3d = position;
							switch (position) {
							case 0:
							    try {
								    m_Amplayer.Set3Dmode(Mode_3D.MODE_3D_DISABLE.ordinal());
								    is3DVideoDisplayFlag = false;
								} 
								catch(RemoteException e) {
								    e.printStackTrace();
								}
                    			break;
                    		case 1:
							    try {
								    m_Amplayer.Set3Dmode(Mode_3D.MODE_3D_AUTO.ordinal());
								    is3DVideoDisplayFlag = true;
								} 
								catch(RemoteException e) {
								    e.printStackTrace();
								}
                    			break;
                    		case 2:
							    try {
								    m_Amplayer.Set3Dmode(Mode_3D.MODE_3D_LR.ordinal());
								    is3DVideoDisplayFlag = true;
								} 
								catch(RemoteException e) {
								    e.printStackTrace();
								}
                    			break;
                    		case 3:
							    try {
								    m_Amplayer.Set3Dmode(Mode_3D.MODE_3D_BT.ordinal());
								    is3DVideoDisplayFlag = true;
								} 
								catch(RemoteException e) {
								    e.printStackTrace();
								}
                    			break;
                    		case 4:
							    try {
								    m_Amplayer.Set3Dmode(Mode_3D.MODE_3D_TO_2D_L.ordinal());
								    is3DVideoDisplayFlag = false;
								} 
								catch(RemoteException e) {
								    e.printStackTrace();
								}
                    			break;
                    		case 5:
							    try {
								    m_Amplayer.Set3Dmode(Mode_3D.MODE_3D_TO_2D_R.ordinal());
								    is3DVideoDisplayFlag = false;
								} 
								catch(RemoteException e) {
								    e.printStackTrace();
								}
                    			break;
                    		case 6:
							    try {
								    m_Amplayer.Set3Dmode(Mode_3D.MODE_3D_TO_2D_T.ordinal());
								    is3DVideoDisplayFlag = false;
								} 
								catch(RemoteException e) {
								    e.printStackTrace();
								}
                    			break;
                    		case 7:
							    try {
								    m_Amplayer.Set3Dmode(Mode_3D.MODE_3D_TO_2D_B.ordinal());
								    is3DVideoDisplayFlag = false;
								} 
								catch(RemoteException e) {
								    e.printStackTrace();
								}
                    			break;
                    		case 8:
							    try {
								    m_Amplayer.Set3Dmode(Mode_3D.MODE_3D_TO_2D_AUTO_1.ordinal());
								    is3DVideoDisplayFlag =false;
								} 
								catch(RemoteException e) {
								    e.printStackTrace();
								}
                    			break;
                    		case 9:
							    try {
								    m_Amplayer.Set3Dmode(Mode_3D.MODE_3D_TO_2D_AUTO_2.ordinal());
								    is3DVideoDisplayFlag =false;
								} 
								catch(RemoteException e) {
								    e.printStackTrace();
								}
                    			break;
                    		case 10:
							    try {
								    m_Amplayer.Set3Dmode(Mode_3D.MODE_2D_TO_3D.ordinal());
								    is3DVideoDisplayFlag =true;
								} 
								catch(RemoteException e) {
								    e.printStackTrace();
								}
                    			break;
                    		case 11:
							    try {
								    m_Amplayer.Set3Dmode(Mode_3D.MODE_FIELD_DEPTH.ordinal());
								    is3DVideoDisplayFlag = true;
								} 
								catch(RemoteException e) {
								    e.printStackTrace();
								}
                    			break;
                    		case 12:
							    try {
								    m_Amplayer.Set3Dmode(Mode_3D.MODE_3D_AUTO_SWITCH.ordinal());
								    is3DVideoDisplayFlag = true;
								} 
								catch(RemoteException e) {
								    e.printStackTrace();
								}
                    			break;  
                    		case 13:
							    try {
								    m_Amplayer.Set3Dmode(Mode_3D.MODE_3D_LR_SWITCH.ordinal());
								    is3DVideoDisplayFlag = true;
								} 
								catch(RemoteException e) {
								    e.printStackTrace();
								}
                    			break;
                    		case 14:
							    try {
								    m_Amplayer.Set3Dmode(Mode_3D.MODE_3D_BT_SWITCH.ordinal());
								    is3DVideoDisplayFlag = true;
								} 
								catch(RemoteException e) {
								    e.printStackTrace();
								}
                    			break; 
                    		case 15:
							    try {
								    m_Amplayer.Set3Daspectfull(0);
								    is3DVideoDisplayFlag = true;
								} 
								catch(RemoteException e) {
								    e.printStackTrace();
								}                    			
                    			break;
                    		case 16:
							    try {
								    m_Amplayer.Set3Daspectfull(1);
								    is3DVideoDisplayFlag = true;
								} 
								catch(RemoteException e) {
								    e.printStackTrace();
								} 
								break;
                    		case 17:
							    try {
								    m_Amplayer.Set3Daspectfull(2);
								    is3DVideoDisplayFlag = true;
								} 
								catch(RemoteException e) {
								    e.printStackTrace();
								}                     			
                    			break;
                    		case 18: //open
							    try {
								    m_Amplayer.Set3Dgrating(1);								    
								} 
								catch(RemoteException e) {
								    e.printStackTrace();
								}                     			
                    			break;
                    		case 19: //close
							    try {
								    m_Amplayer.Set3Dgrating(0);								    
								} 
								catch(RemoteException e) {
								    e.printStackTrace();
								}                     			
                    			break;								
                    		default:
                    			break;
                    		}
							otherbar.setVisibility(View.GONE);
							subTitleView.setViewStatus(true);
							if(subTitleView_sm!=null&&SystemProperties.getBoolean("3D_setting.enable", false)){
							     subTitleView_sm.setViewStatus(true);
							}							
							morbar.setVisibility(View.VISIBLE);
							ImageButton play3d = (ImageButton) findViewById(R.id.Play3DBtn);
							play3d.requestFocus();

							waitForHideOsd();
						}
					});    
					otherbar.requestFocus();
					morebar_status = R.string.setting_3d_mode;

					timer.cancel();
				} 
			});
		}
    	
    	ImageButton audiotrack = (ImageButton) findViewById(R.id.ChangetrackBtn);
		setOnTouchHoldShow(audiotrack);
    	audiotrack.setOnClickListener(new View.OnClickListener() {
    		public void onClick(View v) {
				if (player_status < VideoInfo.PLAYER_INITOK) {
    				return;
				}
                SimpleAdapter audioarray = getMorebarListAdapter(AUDIOTRACK, cur_audio_stream);
                if((audio_flag == Errorno.PLAYER_NO_AUDIO) || (audioarray.getCount() <= 0) ) {
    				Toast toast =Toast.makeText(playermenu.this, R.string.file_have_no_audio,Toast.LENGTH_SHORT );
    				toast.setGravity(Gravity.BOTTOM,110,0);
    				toast.setDuration(0x00000001);
    				toast.show();
					waitForHideOsd();
    				return;
    			}
    			otherbar.setVisibility(View.VISIBLE);
    			subTitleView.setViewStatus(false);
			if(subTitleView_sm!=null&&SystemProperties.getBoolean("3D_setting.enable", false)){
			     subTitleView_sm.setViewStatus(false);
			}				
    			morbar.setVisibility(View.GONE);
    			morebar_tileText.setText(R.string.setting_audiotrack);
    			ListView listView = (ListView)findViewById(R.id.AudioListView);
                listView.setAdapter(audioarray);
    			listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {	
    			    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
    			    	if (bMediaInfo.getAudioTrackCount()>1) {
    			    		try {
    			    			m_Amplayer.SwitchAID(AudioTrackOperation.AudioStreamInfo.get(arg2).audio_id);
    			    			Log.d("audiostream","change audio stream to: " + arg2);
    			    			cur_audio_stream = arg2;
    			    		}
    			    		catch (RemoteException e) {
    			    			e.printStackTrace();
    			    		}
    			    		try {
    			    			m_Amplayer.GetMediaInfo();
    			    		} 
    			    		catch (RemoteException e) {
    			    			e.printStackTrace();
    			    		}
    			    	}
    			    	otherbar.setVisibility(View.GONE);
    			    	subTitleView.setViewStatus(true);
				if(subTitleView_sm!=null&&SystemProperties.getBoolean("3D_setting.enable", false)){
				     subTitleView_sm.setViewStatus(true);
				}						
    			    	morbar.setVisibility(View.VISIBLE);
						    ImageButton audiotrack = (ImageButton) findViewById(R.id.ChangetrackBtn);
						    audiotrack.requestFocus();

						waitForHideOsd();
    			    }	
    			});
    			otherbar.requestFocus();
				morebar_status = R.string.setting_audiotrack;

				timer.cancel();
    		} 
    	});
    	
    	ImageButton subtitle = (ImageButton) findViewById(R.id.SubtitleBtn);
		setOnTouchHoldShow(subtitle);
    	subtitle.setOnClickListener(new View.OnClickListener() {
    		public void onClick(View v) {
    			if(sub_para.totalnum<=0) {
    				Toast toast =Toast.makeText(playermenu.this, R.string.sub_no_subtitle,Toast.LENGTH_SHORT );
    				toast.setGravity(Gravity.BOTTOM,110,0);
    				toast.setDuration(0x00000001);
    				toast.show();
					waitForHideOsd();
    				return;
    			}
    			subbar.setVisibility(View.VISIBLE);
    			subTitleView.setViewStatus(false);
			if(subTitleView_sm!=null&&SystemProperties.getBoolean("3D_setting.enable", false)){
			     subTitleView_sm.setViewStatus(false);
			}				
    			morbar.setVisibility(View.GONE);
    			subtitle_control();
    			subbar.requestFocus();
    			morebar_status = R.string.setting_subtitle;

				timer.cancel();
    		}

			String color_text[]={ 
				playermenu.this.getResources().getString(R.string.color_white),
				playermenu.this.getResources().getString(R.string.color_yellow),
				playermenu.this.getResources().getString(R.string.color_blue)
			};
    		
    		private void subtitle_control() {
				Button ok = (Button) findViewById(R.id.button_ok);
				Button cancel = (Button) findViewById(R.id.button_canncel);
				ImageButton Bswitch_l = (ImageButton) findViewById(R.id.switch_l);	
				ImageButton Bswitch_r = (ImageButton) findViewById(R.id.switch_r);
				ImageButton Bfont_l = (ImageButton) findViewById(R.id.font_l);	
				ImageButton Bfont_r = (ImageButton) findViewById(R.id.font_r);
				ImageButton Bcolor_l = (ImageButton) findViewById(R.id.color_l);	
				ImageButton Bcolor_r = (ImageButton) findViewById(R.id.color_r);
				ImageButton Bposition_v_l = (ImageButton) findViewById(R.id.position_v_l);	
				ImageButton Bposition_v_r = (ImageButton) findViewById(R.id.position_v_r);
				TextView font =(TextView)findViewById(R.id.font_title);
				TextView color =(TextView)findViewById(R.id.color_title);
				TextView position_v =(TextView)findViewById(R.id.position_v_title);
				
				initSubSetOptions(color_text);
		
    			ok.setOnClickListener(new View.OnClickListener() {	
    			    public void onClick(View v) {
    			    	sub_para.curid = sub_switch_state;
    			    	sub_para.font = sub_font_state;
						sub_para.position_v = sub_position_v_state;
    			    	
    			    	if(sub_para.curid==sub_para.totalnum )
    			    	{
    			    		sub_para.sub_id =null;
    			    		sub_para.enable = false;
    			    	}
    			    	else
    			    	{
    			    		sub_para.enable = true;
    			    		sub_para.sub_id =subtitleUtils.getSubID(sub_para.curid);
    			    	}
    			    	if(sub_color_state==0)
    			    		sub_para.color =android.graphics.Color.WHITE;
    			    	else if(sub_color_state==1) 
    			    		sub_para.color =android.graphics.Color.YELLOW;
    			    	else
    			    		sub_para.color =android.graphics.Color.BLUE;
    			    	
    			    	//add by jeff.yang
    			    	SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0); 
    			    	SharedPreferences.Editor editor = settings.edit(); 
    			    	editor.putBoolean("enable", sub_para.enable); 
						editor.putInt("color", sub_para.color); 
    			    	editor.putInt("font", sub_para.font); 
    			    	editor.putInt("position_v", sub_para.position_v); 
    			    	// Don't forget to commit your edits!!! 
    			    	editor.commit();  
    			    	
    			    	
    			    	subbar.setVisibility(View.GONE);
    			    	subTitleView.setViewStatus(true);
						if(subTitleView_sm!=null&&SystemProperties.getBoolean("3D_setting.enable", false)){
						     subTitleView_sm.setViewStatus(true);
						}
    			    	showOsdView();
						setSubtitleView();
						openFile(sub_para.sub_id);
						if (sub_para.sub_id != null) {
			  				if(sub_para.sub_id.filename.equals("INSUB")||sub_para.sub_id.filename.endsWith(".idx")) {
								disableSubSetOptions();
			  				}
							else
							{
								initSubSetOptions(color_text);
							}
		  				}
						else
						{
							initSubSetOptions(color_text);
						}
						
    			    	ImageButton mSubtitle = (ImageButton) findViewById(R.id.SubtitleBtn);
    			    	mSubtitle.requestFocus();

						waitForHideOsd();
    			    } 
    			});
				
    			cancel.setOnClickListener(new View.OnClickListener() {
  		            public void onClick(View v) {
  		            	subbar.setVisibility(View.GONE);
  		            	subTitleView.setViewStatus(true);
				if(subTitleView_sm!=null&&SystemProperties.getBoolean("3D_setting.enable", false)){
				     subTitleView_sm.setViewStatus(true);
				}						
						showOsdView();
						setSubtitleView();
						openFile(sub_para.sub_id);
						if (sub_para.sub_id != null) {
			  				if(sub_para.sub_id.filename.equals("INSUB")||sub_para.sub_id.filename.endsWith(".idx")) {
								disableSubSetOptions();
			  				}
							else
							{
								initSubSetOptions(color_text);
							}
		  				}
						else
						{
							initSubSetOptions(color_text);
						}
						
    			    	ImageButton mSubtitle = (ImageButton) findViewById(R.id.SubtitleBtn);
    			    	mSubtitle.requestFocus();

						waitForHideOsd();
  		            } 
  		        });
				
    			Bswitch_l.setOnClickListener(new View.OnClickListener() {
  					public void onClick(View v) {
  						if(sub_switch_state <= 0)
  							sub_switch_state =sub_para.totalnum;
   						else
   							sub_switch_state --;
   							
  						if(sub_switch_state==sub_para.totalnum)
  							t_subswitch.setText(R.string.str_off);
  						else
  							t_subswitch.setText(String.valueOf(sub_switch_state+1)+"/"+String.valueOf(sub_para.totalnum));
   		            } 
  				});
  				Bswitch_r.setOnClickListener(new View.OnClickListener() {
  					public void onClick(View v) {
  						if(sub_switch_state >= sub_para.totalnum)
  							sub_switch_state =0;
   						else
   							sub_switch_state ++;
   							
  						if(sub_switch_state==sub_para.totalnum)
  							t_subswitch.setText(R.string.str_off);
  						else
  							t_subswitch.setText(String.valueOf(sub_switch_state+1)+"/"+String.valueOf(sub_para.totalnum));;
   		            } 
  				});

  				Bfont_l.setOnClickListener(new View.OnClickListener() {
  					public void onClick(View v) {
  						if(sub_font_state > 12)
  							sub_font_state =sub_font_state-2;
  						else
  							sub_font_state =30;
  							 
  						t_subsfont.setText(String.valueOf(sub_font_state));	 
   		            } 
  				});
  				Bfont_r.setOnClickListener(new View.OnClickListener() {
  					public void onClick(View v) {
  						if(sub_font_state < 30)
  							sub_font_state =sub_font_state +2;
  						else
  							sub_font_state =12;
  							
  						t_subsfont.setText(String.valueOf(sub_font_state));
   		            } 
  				});
  					
  				Bcolor_l.setOnClickListener(new View.OnClickListener() {
  					public void onClick(View v) {
  						if(sub_color_state<= 0)
  							sub_color_state=2;
   						else 
   							sub_color_state-- ;
   							 
   						t_subscolor.setText(color_text[sub_color_state]);
   		            } 
  				});
  				Bcolor_r.setOnClickListener(new View.OnClickListener() {
  					public void onClick(View v) {
  						if(sub_color_state>=2)
   							sub_color_state=0;
    					else 
    						sub_color_state++ ;
    							 
  						t_subscolor.setText(color_text[sub_color_state]);
   		            } 
  				});

				Bposition_v_l.setOnClickListener(new View.OnClickListener() {
  					public void onClick(View v) {
  						if(sub_position_v_state<= 0)
  							sub_position_v_state=15;
   						else 
   							sub_position_v_state-- ;
   							 
   						t_subsposition_v.setText(String.valueOf(sub_position_v_state));
   		            } 
  				});
  				Bposition_v_r.setOnClickListener(new View.OnClickListener() {
  					public void onClick(View v) {
  						if(sub_position_v_state>=15)
   							sub_position_v_state=0;
    					else 
    						sub_position_v_state++ ;
    							 
  						t_subsposition_v.setText(String.valueOf(sub_position_v_state));
   		            } 
  				});

				if (sub_para.sub_id != null) {
	  				if(sub_para.sub_id.filename.equals("INSUB")||sub_para.sub_id.filename.endsWith(".idx")) {
						disableSubSetOptions();
	  				}
  				}
			} 
    	});
    	
    	ImageButton display = (ImageButton) findViewById(R.id.DisplayBtn);
		setOnTouchHoldShow(display);
    	display.setOnClickListener(new View.OnClickListener() {
    		public void onClick(View v) {
				otherbar.setVisibility(View.VISIBLE);
				subTitleView.setViewStatus(false);
				if(subTitleView_sm!=null&&SystemProperties.getBoolean("3D_setting.enable", false)){
					subTitleView_sm.setViewStatus(false);
				}				
	        	morbar.setVisibility(View.GONE);
                	
                morebar_tileText.setText(R.string.setting_displaymode);
                ListView listView = (ListView)findViewById(R.id.AudioListView);
                if(is3DVideoDisplayFlag){
                	listView.setAdapter(getMorebarListAdapter(DISPLAY, view_mode));
                }
                else{
                	listView.setAdapter(getMorebarListAdapter(DISPLAY, ScreenMode.getScreenMode()));
                }
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        //if(SystemProperties.getBoolean("3D_setting.enable", false)){
                        if(is3DVideoDisplayFlag){  
                        	if(is3DVideoDisplayFlag){//judge is 3D 
                        		view_mode = position;
    	                    	switch (position) { //view mode
		                    		case 0:
		                    			if(m_Amplayer != null){
		                    				try{
		                    					
		                    					m_Amplayer.Set3Dviewmode(0);
		                    				}
	                    					catch(RemoteException e) {
	        								    e.printStackTrace();
	        								}
		                    			}
		                    				
		                    			break;
		                    		case 1:
		                    			if(m_Amplayer != null){
		                    				try{
		                    					
		                    					m_Amplayer.Set3Dviewmode(1);
		                    				}catch(RemoteException e) {
		    								    e.printStackTrace();
		    								}
		                    			}
		                    			break;
		                    		case 2:
		                    			if(m_Amplayer != null){
		                    				try{
		                    					
		                    					m_Amplayer.Set3Dviewmode(2);
		                    				}
		                    				catch(RemoteException e) {
		    								    e.printStackTrace();
		    								}
		                    			}
		                    			break;
		                    		case 3:
		                    			if(m_Amplayer != null){
		                    				try{
		                    					
		                    					m_Amplayer.Set3Dviewmode(3);
		                    				}
		                    				catch(RemoteException e) {
		    								    e.printStackTrace();
		    								}
		                    			}
		                    			break;
		                    		
		                            case 4:
		                    			if(m_Amplayer != null){
		                    				try{
		                    					
		                    					m_Amplayer.Set3Dviewmode(4);
		                    				}
		                    				catch(RemoteException e) {
		    								    e.printStackTrace();
		    								}
		                    			}
		                    			break;
		                    			
		                    		default:
		                    			break;
    	                    	}                        	
                        		
                        	}
                        }else{                  	
	                    	switch (position) {
	                    		case ScreenMode.NORMAL:
	                            	SettingsVP.putParaInt(SettingsVP.DISPLAY_MODE, ScreenMode.NORMAL);
	                            	ScreenMode.setScreenMode("0");                                
	                    			break;
	                    		case ScreenMode.FULLSTRETCH:
	                    			SettingsVP.putParaInt(SettingsVP.DISPLAY_MODE, ScreenMode.FULLSTRETCH);
	                    			ScreenMode.setScreenMode("1");
	                    			break;
	                    		case ScreenMode.RATIO4_3:
	                    			SettingsVP.putParaInt(SettingsVP.DISPLAY_MODE, ScreenMode.RATIO4_3);
	                    			ScreenMode.setScreenMode("2");
	                    			break;
	                    		case ScreenMode.RATIO16_9:
	                    			SettingsVP.putParaInt(SettingsVP.DISPLAY_MODE, ScreenMode.RATIO16_9);
	                    			ScreenMode.setScreenMode("3");
	                    			break;
	                    		/*	
	                            case ScreenMode.NORMAL_NOSCALEUP:
	                            	SettingsVP.putParaInt(SettingsVP.DISPLAY_MODE, ScreenMode.NORMAL_NOSCALEUP);
	                                ScreenMode.setScreenMode("4");
	                    			break;
	                    		*/	
	                    		default:
	                    			break;
	                    	}
                        }
                    	otherbar.setVisibility(View.GONE);
                    	subTitleView.setViewStatus(true);
						if(subTitleView_sm!=null&&SystemProperties.getBoolean("3D_setting.enable", false)){
						     subTitleView_sm.setViewStatus(true);
						}						
                    	morbar.setVisibility(View.VISIBLE);
						ImageButton display = (ImageButton) findViewById(R.id.DisplayBtn);
						display.requestFocus();

						waitForHideOsd();
                    }
                });      
                otherbar.requestFocus();
    			morebar_status = R.string.setting_displaymode;

				timer.cancel();
            } 
    	});
    	
    	/*this is setting is default*/
    	if(SystemProperties.getBoolean("ro.screen.has.brightness", true)) {
    		ImageButton brigtness = (ImageButton) findViewById(R.id.BrightnessBtn);
			setOnTouchHoldShow(brigtness);
    		brigtness.setOnClickListener(new View.OnClickListener() {
    			public void onClick(View v) {
    				otherbar.setVisibility(View.VISIBLE);
    				subTitleView.setViewStatus(false);
				if(subTitleView_sm!=null&&SystemProperties.getBoolean("3D_setting.enable", false)){
				     subTitleView_sm.setViewStatus(false);
				}					
    				morbar.setVisibility(View.GONE);
    				morebar_tileText.setText(R.string.setting_brightness);
    				ListView listView = (ListView)findViewById(R.id.AudioListView);
    				int mBrightness = 0;
    				try {
    					mBrightness = Settings.System.getInt(playermenu.this.getContentResolver(), 
						   Settings.System.SCREEN_BRIGHTNESS);
    				} 
    				catch (SettingNotFoundException e) {
    					e.printStackTrace();
    				}
    				int item;
    				if (mBrightness <= (android.os.Power.BRIGHTNESS_DIM + 10))
    					item = 0;
    				else if (mBrightness <= (android.os.Power.BRIGHTNESS_ON * 0.2f))
    					item = 1;
    				else if (mBrightness <= (android.os.Power.BRIGHTNESS_ON * 0.4f))
    					item = 2;
    				else if (mBrightness <= (android.os.Power.BRIGHTNESS_ON * 0.6f))
    					item = 3;
    				else if (mBrightness <= (android.os.Power.BRIGHTNESS_ON * 0.8f))
    					item = 4;
    				else
    					item = 5;
    				
    				listView.setAdapter(getMorebarListAdapter(BRIGHTNESS, item));
    				listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
    					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    						int brightness;
    						switch(position) {
                        	case 0:
                        	 	brightness = android.os.Power.BRIGHTNESS_DIM + 10;
                        		break;
                        	case 1:
                        		brightness = (int)(android.os.Power.BRIGHTNESS_ON * 0.2f);
                        		break;
                        	case 2:
                        		brightness = (int)(android.os.Power.BRIGHTNESS_ON * 0.4f);
                        	 	break;
                        	case 3:
                        		brightness = (int)(android.os.Power.BRIGHTNESS_ON * 0.6f);
                        	 	break;	 
							case 4:
                        		brightness = (int)(android.os.Power.BRIGHTNESS_ON * 0.8f);
                        	 	break;
							case 5:
                        		brightness = android.os.Power.BRIGHTNESS_ON;
                        	 	break;
                        	default:
								brightness = android.os.Power.BRIGHTNESS_DIM + 30;
                        		break;
                        	}
    						try {
    							IPowerManager power = IPowerManager.Stub.asInterface(ServiceManager.getService("power"));
    							if (power != null) {
    								power.setBacklightBrightness(brightness);
    								Settings.System.putInt(playermenu.this.getContentResolver(), 
				                    	Settings.System.SCREEN_BRIGHTNESS, brightness);
    							}
    						} 
    						catch (RemoteException doe) {
    							
    						}  
    						otherbar.setVisibility(View.GONE);
    						subTitleView.setViewStatus(true);
						if(subTitleView_sm!=null&&SystemProperties.getBoolean("3D_setting.enable", false)){
						     subTitleView_sm.setViewStatus(true);
						}							
    						morbar.setVisibility(View.VISIBLE);
    						ImageButton brigtness = (ImageButton) findViewById(R.id.BrightnessBtn);
    						brigtness.requestFocus();

							waitForHideOsd();
    					}
    				});
    				otherbar.requestFocus();
    				morebar_status = R.string.setting_brightness;

					timer.cancel();
    			} 
    		}); 
    	}
    	
    	ImageButton backtovidebar = (ImageButton) findViewById(R.id.BackBtn);
		setOnTouchHoldShow(backtovidebar);
    	backtovidebar.setOnClickListener(new View.OnClickListener() {
    		public void onClick(View v) {
				switchOsdView();
                
                ImageButton morebtn = (ImageButton) findViewById(R.id.moreBtn);
                morebtn.requestFocus();
    		} 
    	}); 
    	
    	ImageButton fileinformation = (ImageButton) findViewById(R.id.InfoBtn);
		setOnTouchHoldShow(fileinformation);
    	fileinformation.setOnClickListener(new View.OnClickListener() {
    		public void onClick(View v) {
				infodialog.setVisibility(View.VISIBLE);
				subTitleView.setViewStatus(false);
				if(subTitleView_sm!=null&&SystemProperties.getBoolean("3D_setting.enable", false)){
				     subTitleView_sm.setViewStatus(false);
				}				
				morbar.setVisibility(View.GONE);
				TextView title = (TextView)findViewById(R.id.info_title);
				title.setText(R.string.str_file_information);
					
				String fileinf = null;
				TextView filename = (TextView)findViewById(R.id.filename);
                fileinf = playermenu.this.getResources().getString(R.string.str_file_name)
        			+ "\t: " + bMediaInfo.getFileName(PlayList.getinstance().getcur());
				filename.setText(fileinf);

				TextView filetype = (TextView)findViewById(R.id.filetype);
                fileinf = playermenu.this.getResources().getString(R.string.str_file_format)
        			+ "\t: " + bMediaInfo.getFileType();
				filetype.setText(fileinf);
					
				TextView filesize = (TextView)findViewById(R.id.filesize);
                fileinf = playermenu.this.getResources().getString(R.string.str_file_size)
        			+ "\t: " + bMediaInfo.getFileSize();
				filesize.setText(fileinf);
					
				TextView resolution = (TextView)findViewById(R.id.resolution);
                fileinf = playermenu.this.getResources().getString(R.string.str_file_resolution)
        			+ "\t: " + bMediaInfo.getResolution();
                resolution.setText(fileinf);
					
				TextView duration = (TextView)findViewById(R.id.duration);
                fileinf = playermenu.this.getResources().getString(R.string.str_file_duration)
        			+ "\t: " + secToTime(bMediaInfo.duration, true);
				duration.setText(fileinf);
					
				Button ok = (Button)findViewById(R.id.info_ok);
				ok.setText("OK");
				ok.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
                        infodialog.setVisibility(View.GONE);
                        subTitleView.setViewStatus(true);
			if(subTitleView_sm!=null&&SystemProperties.getBoolean("3D_setting.enable", false)){
			     subTitleView_sm.setViewStatus(true);
			}					
                        morbar.setVisibility(View.VISIBLE);
						ImageButton fileinformation = (ImageButton) findViewById(R.id.InfoBtn);
						fileinformation.requestFocus();

						waitForHideOsd();
					}
				});
				infodialog.requestFocus();	
    			morebar_status = R.string.str_file_name;

				timer.cancel();
            } 
    	}); 
    }
    
    public boolean onKeyUp(int keyCode, KeyEvent msg) {
        if (keyCode == KeyEvent.KEYCODE_UNKNOWN) {
            touchVolFlag = false;
        }

		if((morbar.getVisibility() == View.VISIBLE)||(infobar.getVisibility() == View.VISIBLE))
		{
			isKeyOrTouchDown=false;
			waitForHideOsd();
		}
		
        return true;
    }

    public boolean onKeyDown(int keyCode, KeyEvent msg) {
		setOSDOnOff(true);
        if (keyCode != KeyEvent.KEYCODE_UNKNOWN) {
        	//if((morbar.getVisibility() == View.VISIBLE)||(infobar.getVisibility() == View.VISIBLE))
			//waitForHideOsd();
			isKeyOrTouchDown=true;
			
        	if(SystemProperties.getBoolean("ro.platform.has.mbxuimode", false)){
	        	if(intouch_flag){
	        		int flag = getCurOsdViewFlag();
					if(OSD_MORE_BAR==flag)
					{
	            		if(morbar.getVisibility() == View.VISIBLE){
	            			morbar.requestFocusFromTouch();
	            		}
	            	}
	            	else{
	            		if (infobar.getVisibility() == View.VISIBLE){
	            			infobar.requestFocusFromTouch();
	            		}
	            	}
	        		intouch_flag = false;
	        	}
        	}
        }

        if (keyCode == KeyEvent.KEYCODE_UNKNOWN) {
            touchVolFlag = true;
        } 
		else if (keyCode == KeyEvent.KEYCODE_POWER) {
    		if (player_status == VideoInfo.PLAYER_RUNNING) {
    			try {
    				m_Amplayer.Pause();
    			} 
    			catch(RemoteException e) {
    				e.printStackTrace();
    			}
    		}
    		mSuspendFlag = true;
    		openScreenOffTimeout();
    		return true;
    	}
    	else if (keyCode == KeyEvent.KEYCODE_BACK) {
			int flag = getCurOsdViewFlag();
			if(OSD_MORE_BAR==flag)
			{
				if((otherbar.getVisibility() == View.VISIBLE) 
				|| (infodialog.getVisibility() == View.VISIBLE)
				|| (subbar.getVisibility() == View.VISIBLE))
				{
					showOsdView();

					if(SystemProperties.getBoolean("ro.platform.has.mbxuimode", false)){
		    	        switch(morebar_status){
	    	        	case R.string.setting_resume:
	    	        		ImageButton resume = (ImageButton) findViewById(R.id.ResumeBtn);
	    	        		resume.requestFocusFromTouch();
	    				    resume.requestFocus();
	    	        		break;
	    	        	case R.string.setting_playmode:
	    	        		ImageButton playmode = (ImageButton) findViewById(R.id.PlaymodeBtn);
	    	        		playmode.requestFocusFromTouch();
						    playmode.requestFocus();
	    	        		break;
	    	        	case R.string.setting_3d_mode:
	    	        		ImageButton play3d = (ImageButton) findViewById(R.id.Play3DBtn);
	    	        		play3d.requestFocusFromTouch();
	                    	play3d.requestFocus();
	    	        		break;
	    	        	case R.string.setting_audiotrack:
	    	        		ImageButton audiotrack = (ImageButton) findViewById(R.id.ChangetrackBtn);
	    	        		audiotrack.requestFocusFromTouch();
						    audiotrack.requestFocus();
	    	        		break;
	    	        	case R.string.setting_subtitle:
	    	        		ImageButton subtitle = (ImageButton) findViewById(R.id.SubtitleBtn);
	    	        		subtitle.requestFocusFromTouch();
	    	        		subtitle.requestFocus();
	    	        		break;
	    	        	case R.string.setting_displaymode:
	    	        		ImageButton display = (ImageButton) findViewById(R.id.DisplayBtn);
	    	        		display.requestFocusFromTouch();
							display.requestFocus();
	    	        		break;
	    	        	case R.string.setting_brightness:
	    	        		ImageButton brigtness = (ImageButton) findViewById(R.id.BrightnessBtn);
	    	        		brigtness.requestFocusFromTouch();
	                        brigtness.requestFocus();
	    	        		break;
	    	        	case R.string.str_file_name:
	    	        		ImageButton fileinformation = (ImageButton) findViewById(R.id.InfoBtn);
	    	        		fileinformation.requestFocusFromTouch();
							fileinformation.requestFocus();	
	    	        		break;
		    	        default:
		    	        	morbar.requestFocus();
		    	        	break;
		    	        }
	            	}
					return(true);
				}
				else
				{
					switchOsdView();
					
					ImageButton morebtn = (ImageButton) findViewById(R.id.moreBtn);

		        	if(SystemProperties.getBoolean("ro.platform.has.mbxuimode", false)){
		                morebtn.requestFocusFromTouch();
		                morebtn.requestFocus();
		        	}
					return(true);
				}
			}
			else if(OSD_INFO_BAR==flag)
			{
    			if(m_Amplayer == null)
					return (true);
    			if(bMediaInfo == null)
					return (true);

                // close infobar  
				if(SettingsVP.chkEnableOSD2XScale() == true) {
					showNoOsdView();
				}
	  			item_position_selected = item_position_selected_init + PlayList.getinstance().getindex();
    			Intent selectFileIntent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putInt("item_position_selected", item_position_selected);
			    bundle.putInt("item_position_first", item_position_first);
			    bundle.putInt("fromtop_piexl", fromtop_piexl);
			    bundle.putIntegerArrayList("fileDirectory_position_selected", fileDirectory_position_selected);
			    bundle.putIntegerArrayList("fileDirectory_position_piexl", fileDirectory_position_piexl);
				selectFileIntent.setClass(playermenu.this, FileList.class);
				selectFileIntent.putExtras(bundle);
				//close sub;
				if(subTitleView!=null){
					subTitleView.closeSubtitle();	
    				subTitleView.clear();
				}
				if(subTitleView_sm!=null&&SystemProperties.getBoolean("3D_setting.enable", false)){
				     subTitleView_sm.closeSubtitle();
				     subTitleView_sm.clear();
				}				
                if (!fb32) {
                    // Hide the view with key color
                    FrameLayout layout = (FrameLayout) findViewById(R.id.BaseLayout1);
                    if (layout != null) {
                        layout.setVisibility(View.INVISIBLE);
                        layout.invalidate();
                    }
                }
				//stop play
				backToFileList = true;
				if(m_Amplayer != null)
					Amplayer_stop();
				//do disable2XScale in onPause()
				if(!backToOtherAPK){
					startActivity(selectFileIntent);
				}
				playermenu.this.finish();
				return true;
    		}
    	}
		else if (keyCode == KeyEvent.KEYCODE_MENU || keyCode == KeyEvent.KEYCODE_9) {
			if((morbar.getVisibility() == View.VISIBLE)||(infobar.getVisibility() == View.VISIBLE))
			{
				showNoOsdView();
			}
			else
			{
				showOsdView();
				
				int flag = getCurOsdViewFlag();
				if(OSD_INFO_BAR==flag)
				{
					if(SystemProperties.getBoolean("ro.platform.has.mbxuimode", false)){
			    		play.requestFocusFromTouch();
			    		play.requestFocus();
		        	}
				}
			}
			return (true);
		}
    	else if (keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE) {
            play.requestFocus();

			int flag = getCurOsdViewFlag();
			if(OSD_INFO_BAR==flag)
			{
				showOsdView();
			}
			else if(OSD_MORE_BAR==flag)
			{
				switchOsdView();
			}

			if (player_status == VideoInfo.PLAYER_RUNNING) {
				try	{
					m_Amplayer.Pause();
				} 
				catch(RemoteException e) {
					e.printStackTrace();
				}
			}
			else if (player_status == VideoInfo.PLAYER_PAUSE) {
				try	{
					m_Amplayer.Resume();
				} 
				catch(RemoteException e)	{
					e.printStackTrace();
				}
			}
			else if (player_status == VideoInfo.PLAYER_SEARCHING) {
				try	{
					ff_fb.cancel();
					if(FF_FLAG)
						m_Amplayer.FastForward(0);
					if(FB_FLAG)
						m_Amplayer.BackForward(0);
					FF_FLAG = false;
					FB_FLAG = false;
					FF_LEVEL = 0;
					FB_LEVEL = 0;
				} 
				catch(RemoteException e) {
					e.printStackTrace();
				}
			}
			return (true);
		}
    	else if (keyCode == KeyEvent.KEYCODE_MEDIA_PREVIOUS) {
			if (!INITOK)
				return false;
			try
			{
		        int flag = getCurOsdViewFlag();
				if(OSD_INFO_BAR==flag)
				{
					showOsdView();
				}
				else if(OSD_MORE_BAR==flag)
				{
					switchOsdView();
				}
				
	        	ImageButton preItem = (ImageButton) findViewById(R.id.PreBtn);
            	preItem.requestFocus();
            }
            catch(Exception ex )
            {
            }

			ResumePlay.saveResumePara(PlayList.getinstance().getcur(), curtime);
			String filename = PlayList.getinstance().moveprev();
			toast.cancel();
			toast.setText(filename);
			toast.show();
			playPosition = 0;
			if(m_Amplayer == null)
				return false; 
			else
				Amplayer_stop();
			PRE_NEXT_FLAG = 1;	 		
    	}
    	else if(keyCode == KeyEvent.KEYCODE_MEDIA_NEXT) {
			if (!INITOK)
				return false;
			try
			{
	        	int flag = getCurOsdViewFlag();
				if(OSD_INFO_BAR==flag)
				{
					showOsdView();
				}
				else if(OSD_MORE_BAR==flag)
				{
					switchOsdView();
				}
				
	        	ImageButton nextItem = (ImageButton) findViewById(R.id.NextBtn);
	        	nextItem.requestFocus();
        	}
        	catch(Exception ex)
        	{
        	}

			ResumePlay.saveResumePara(PlayList.getinstance().getcur(), curtime);
			String filename = PlayList.getinstance().movenext();
			toast.cancel();
			toast.setText(filename); 
			toast.show();
			playPosition = 0;
			if(m_Amplayer == null)
				return false;
			else
				Amplayer_stop();
			PRE_NEXT_FLAG = 1;		    		   		
    	}
    	else if(keyCode == KeyEvent.KEYCODE_MEDIA_FAST_FORWARD) {
			if (!INITOK)
				return false;

            fastforword.requestFocus();
            int flag = getCurOsdViewFlag();
			if(OSD_INFO_BAR==flag)
			{
				showOsdView();
			}
			else if(OSD_MORE_BAR==flag)
			{
				switchOsdView();
			}

			if (player_status == VideoInfo.PLAYER_SEARCHING) {
				if(FF_FLAG) {
					if(FF_LEVEL < FF_MAX) {
						FF_LEVEL = FF_LEVEL + 1;
					}
					else {
						FF_LEVEL = 0;
					}
					
					try	{
						m_Amplayer.FastForward(FF_STEP[FF_LEVEL]);
					} 
					catch(RemoteException e) {
						e.printStackTrace();
					}
					
					if(FF_LEVEL == 0) {
						ff_fb.cancel();
						FF_FLAG = false;
					}
					else {
						ff_fb.cancel();
						ff_fb.setText(new String("FF x" + Integer.toString(FF_SPEED[FF_LEVEL])));
						ff_fb.show();
					}
				}
				
				if(FB_FLAG) {
					if(FB_LEVEL > 0) {
						FB_LEVEL = FB_LEVEL - 1;
					}
					else {
						FB_LEVEL = 0;
					}
					
					try	{
						m_Amplayer.BackForward(FB_STEP[FB_LEVEL]);
					} 
					catch(RemoteException e) {
						e.printStackTrace();
					}
					
					if(FB_LEVEL == 0) {
						ff_fb.cancel();
						FB_FLAG = false;
					}
					else {
						ff_fb.cancel();
						ff_fb.setText(new String("FB x" + Integer.toString(FB_SPEED[FB_LEVEL])));
	    				ff_fb.show();
					}
				}
			}
			else {
				try	{
					m_Amplayer.FastForward(FF_STEP[1]);
				} 
				catch(RemoteException e) {
					e.printStackTrace();
				}
				FF_FLAG = true;
				FF_LEVEL = 1;
				ff_fb.cancel();
				ff_fb.setText(new String("FF x"+FF_SPEED[FF_LEVEL]));
				ff_fb.show();
			}		 
    	}
    	else if(keyCode == KeyEvent.KEYCODE_MEDIA_REWIND) {
			if (!INITOK)
				return false;

            fastreverse.requestFocus();
            int flag = getCurOsdViewFlag();
			if(OSD_INFO_BAR==flag)
			{
				showOsdView();
			}
			else if(OSD_MORE_BAR==flag)
			{
				switchOsdView();
			}

            if (player_status == VideoInfo.PLAYER_SEARCHING) {
				if(FB_FLAG) {
					if(FB_LEVEL < FF_MAX) {
						FB_LEVEL = FB_LEVEL + 1;
					}
					else {
						FB_LEVEL = 0;
					}
					
					try	{
						m_Amplayer.BackForward(FB_STEP[FB_LEVEL]);
					} 
					catch(RemoteException e) {
						e.printStackTrace();
					}
					
					if(FB_LEVEL == 0) {
						ff_fb.cancel();
						FB_FLAG = false;
					}
					else {
						ff_fb.cancel();
						ff_fb.setText(new String("FB x" + Integer.toString(FB_SPEED[FB_LEVEL])));
	    				ff_fb.show();
					}
				}
				
				if(FF_FLAG) {
					if(FF_LEVEL > 0) {
						FF_LEVEL = FF_LEVEL - 1;
					}
					else {
						FF_LEVEL = 0;
					}
					
					try	{
						m_Amplayer.FastForward(FF_STEP[FF_LEVEL]);
					} 
					catch(RemoteException e) {
						e.printStackTrace();
					}
					
					if(FF_LEVEL == 0) {
						ff_fb.cancel();
						FF_FLAG = false;
					}
					else {
						ff_fb.cancel();
						ff_fb.setText(new String("FF x" + Integer.toString(FF_SPEED[FF_LEVEL])));
						ff_fb.show();
					}
				}
            } 
			else {
                try {
                    m_Amplayer.BackForward(FB_STEP[1]);
                } 
				catch (RemoteException e) {
                    e.printStackTrace();
                }
				FB_FLAG = true;
				FB_LEVEL = 1;
				ff_fb.cancel();
				ff_fb.setText(new String("FB x"+FB_SPEED[FB_LEVEL]));
				ff_fb.show();
            }
        } 
    	else if (keyCode == KeyEvent.KEYCODE_MUTE) {
			int flag = getCurOsdViewFlag();
			if(OSD_INFO_BAR==flag)
			{
				showOsdView();
				play.requestFocus();
			}
			else if(OSD_MORE_BAR==flag)
			{
				if(!(otherbar.getVisibility() == View.VISIBLE) 
				&& !(infodialog.getVisibility() == View.VISIBLE)
				&& !(subbar.getVisibility() == View.VISIBLE))
				{
					showOsdView();
				}
			}
    	}
        /*
    	else if (keyCode == KeyEvent.KEYCODE_7) {
    		videobar();
    		ImageButton subtitle = (ImageButton) findViewById(R.id.SubtitleBtn);
    		subtitle.requestFocusFromTouch();
    		return (true);
    	}
      	else if (keyCode == KeyEvent.KEYCODE_MEDIA_AUDIO) {
    		videobar();
    		ImageButton subtitle = (ImageButton) findViewById(R.id.ChangetrackBtn);
    		subtitle.requestFocusFromTouch();
    		return (true);
    	}
    	*/
        else
        	return super.onKeyDown(keyCode, msg);
    	return (true);
    }
    
    public void onCreate(Bundle savedInstanceState) {
        fb32 = SystemProperties.get("sys.fb.bits", "16").equals("32");

        if(fb32) {
            setTheme(R.style.theme_trans);
        } else {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
        }

        super.onCreate(savedInstanceState);
        //uncaughtException execute
    	Thread.currentThread().setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
    		public void uncaughtException(Thread thread, Throwable ex) {    
    			
    			SystemProperties.set("vplayer.hideStatusBar.enable","false");
    			  
    			Intent selectFileIntent = new Intent();
				selectFileIntent.setClass(playermenu.this, FileList.class);
				if(SettingsVP.chkEnableOSD2XScale() == true) {
					if(infobar != null) {
						infobar.setVisibility(View.GONE);
						getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,   
								WindowManager.LayoutParams.FLAG_FULLSCREEN);
					}
				}

				//close sub;
				if(subTitleView!=null){
					subTitleView.closeSubtitle();	
    				subTitleView.clear();
				}
			  if(subTitleView_sm!=null){
					subTitleView_sm.closeSubtitle();	
    				subTitleView_sm.clear();
				}
                if (!fb32) {
                    // Hide the view with key color
                    FrameLayout layout = (FrameLayout) findViewById(R.id.BaseLayout1);
                    if (layout != null) {
                        layout.setVisibility(View.INVISIBLE);
                        layout.invalidate();
                    }
                }
				//stop play
				backToFileList = true;
				if(m_Amplayer != null)
					Amplayer_stop();
				PlayList.getinstance().rootPath =null;
				if(!backToOtherAPK)
					startActivity(selectFileIntent);
				finish();
    		  	onPause(); //for disable 2Xscale
    		  	onDestroy(); //set freescale when exception
    			Log.d(TAG,"----------------uncaughtException--------------------");
				
    		  	android.os.Process.killProcess(android.os.Process.myPid());
    		}
    	});

        m1080scale = SystemProperties.getInt("ro.platform.has.1080scale", 0);
        outputmode = SystemProperties.get(STR_OUTPUT_MODE);
        if(m1080scale == 2 || (m1080scale == 1 && (outputmode.equals("1080p") || outputmode.equals("1080i") || outputmode.equals("720p")))){
	 			 	writeFile(Fb0Blank,"1");
					writeFile(Fb1Blank,"1");
	 			 	AmPlayer.GL2XScale(1);	
				  	AmPlayer.disableFreescaleMBX();
				/*	Timer timer1 = new Timer();
	 			 	TimerTask task = new TimerTask(){   
            public void run() {   
				  		AmPlayer.disableFreescaleMBX();
            }
	        };
	        timer1.cancel();
	        timer1 = new Timer();
	    		timer1.schedule(task, 1000);	*/
        }

		
        if(AmPlayer.getProductType() == 1)
        	AmPlayer.disable_freescale(MID_FREESCALE);
        //fixed bug for green line
        FrameLayout foreground = (FrameLayout)findViewById(android.R.id.content);
        foreground.setForeground(null);
        
        if(fb32) {
            setContentView(R.layout.infobar32);
            setTitle(null);
        } 
        else {
            setContentView(R.layout.infobar);
        }
        toast = Toast.makeText(playermenu.this, "", Toast.LENGTH_SHORT);
        ff_fb =Toast.makeText(playermenu.this, "",Toast.LENGTH_SHORT );
        ff_fb.setGravity(Gravity.TOP | Gravity.RIGHT,10,10);
		ff_fb.setDuration(0x00000001);

        mScreenLock = ((PowerManager)this.getSystemService(Context.POWER_SERVICE)).newWakeLock(
        		PowerManager.SCREEN_BRIGHT_WAKE_LOCK,TAG);
        closeScreenOffTimeout();
		
        Intent it = this.getIntent();
        playmode_switch = true;
        if(it.getData() != null) {
        	if(it.getData().getScheme().equals("file")) {
        		List<String> paths = new ArrayList<String>();
                paths.add(it.getData().getPath());
                PlayList.getinstance().setlist(paths, 0);
                PlayList.getinstance().rootPath = null;
                backToOtherAPK = true;
        	}
        	else {
                Cursor cursor = managedQuery(it.getData(), null, null, null, null);
                cursor.moveToFirst();

                int index = cursor.getColumnIndex(MediaStore.Video.Media.DATA);
                if((index == -1) || (cursor.getCount() <= 0)) {
                    Log.e(TAG, "Cursor empty or failed\n"); 
                }
                else {
                    List<String> paths = new ArrayList<String>();
                    cursor.moveToFirst();

                    paths.add(cursor.getString(index));
                    PlayList.getinstance().setlist(paths, 0);
                    
                    playmode_switch = false;
                    Log.d(TAG, "index = " + index);
                    Log.d(TAG, "From content providor DATA:" + cursor.getString(index));
                    Log.d(TAG, " -- MIME_TYPE :" + 
                    		cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.MIME_TYPE)));
                }
        	}
        }
		mode_3d = 0;
        SettingsVP.init(this);
        SettingsVP.setVideoLayoutMode();
        SettingsVP.enableVideoLayout();
//        if(AmPlayer.getProductType() == 1){
//	        if(SettingsVP.display_mode.equals("480p")) {
//	        	getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,   
//	        			WindowManager.LayoutParams.FLAG_FULLSCREEN);
//	        }
//        }
		subinit();
		displayinit();
		firstInvokFlag=1;//tony.wang 20120601
		initOsdBar();
		
		IntentFilter intentFilter = new IntentFilter(WindowManagerPolicy.ACTION_HDMI_PLUGGED);
		mReceiver = new BroadcastReceiver() {
	        @Override
	        public void onReceive(Context context, Intent intent) {
	        	boolean plugged
	                = intent.getBooleanExtra(WindowManagerPolicy.EXTRA_HDMI_PLUGGED_STATE, false); 
	                
	            if(mHdmiPlugged != plugged && confirm_dialog != null && confirm_dialog.isShowing()) {
	                confirm_dialog.dismiss();
	            } 	                
	                
                if (!SystemProperties.getBoolean("ro.vout.dualdisplay", false)) {    
    	        	if (mHdmiPlugged != plugged) {
    	                mHdmiPlugged = plugged;
    	                Intent selectFileIntent = new Intent();
    	                selectFileIntent.setClass(playermenu.this, FileList.class);	
    	                backToFileList = true;
    	                PlayList.getinstance().rootPath=null;
    	                if(!backToOtherAPK)
    	                	startActivity(selectFileIntent);
    	                playermenu.this.finish();
    	        	}
                }
	            
	        }
		};
		Intent intent = registerReceiver(mReceiver, intentFilter);
		if (intent != null) {
	        // Retrieve current sticky dock event broadcast.
	        mHdmiPlugged = intent.getBooleanExtra(WindowManagerPolicy.EXTRA_HDMI_PLUGGED_STATE, false);
		} 		
		
		mWindowManager = getWindowManager();
        setAngleTable();
        seek = 0;
        seek_cur_time = 0;
		if(SettingsVP.getParaBoolean(SettingsVP.RESUME_MODE))
			resumePlay();
		else {
			if(!NOT_FIRSTTIME)
    			StartPlayerService();
        	else
        		Amplayer_play();
		}
		
		set2XScale();

        if(infobar != null) {
            infobar.setVisibility(View.VISIBLE);
            ImageButton browser = (ImageButton) findViewById(R.id.BrowserBtn);
            browser.requestFocus();
        }
        try{
	        Bundle bundle = new Bundle();
	        bundle = this.getIntent().getExtras();
	        
	        if (bundle != null) {
		        item_position_selected = bundle.getInt("item_position_selected");
		        item_position_first = bundle.getInt("item_position_first");
		        fromtop_piexl = bundle.getInt("fromtop_piexl");
		        fileDirectory_position_selected = bundle.getIntegerArrayList("fileDirectory_position_selected");
		        fileDirectory_position_piexl = bundle.getIntegerArrayList("fileDirectory_position_piexl");
		        backToOtherAPK = bundle.getBoolean("backToOtherAPK", true);			
		        if(item_init_flag){
		        	item_position_selected_init = item_position_selected - PlayList.getinstance().getindex();
		        	item_init_flag = false;
		        }
	        }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void displayinit() {
    	int mode = SettingsVP.getParaInt(SettingsVP.DISPLAY_MODE);
    	switch (mode) {
		case ScreenMode.NORMAL:
			ScreenMode.setScreenMode("0");
			break;
		case ScreenMode.FULLSTRETCH:
			ScreenMode.setScreenMode("1");
			break;
		case ScreenMode.RATIO4_3:
			ScreenMode.setScreenMode("2");
			break;
		case ScreenMode.RATIO16_9:
			ScreenMode.setScreenMode("3");
			break;
		/*
        case ScreenMode.NORMAL_NOSCALEUP:
            ScreenMode.setScreenMode("4");
			break;
		*/	
		default:
			Log.e(TAG, "load display mode para error!");
			break;
		}
    }
	
    protected void subinit() {
        subtitleUtils = new SubtitleUtils(PlayList.getinstance().getcur());
        sub_para = new subview_set();
         
        sub_para.totalnum = 0;
        sub_para.curid = 0;
        //add by jeff.yang
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);  
        sub_para.enable = settings.getBoolean("enable", true);  
        sub_para.color = settings.getInt("color", android.graphics.Color.WHITE);  //android.graphics.Color.WHITE;
    	sub_para.font=settings.getInt("font", 20);//20;
    	sub_para.position_v=settings.getInt("position_v", 0);//0;
    	
        sub_para.sub_id = null;
    }
    
    private static String do_exec(String[] cmd) {
        String s = "\n";
        try {
            java.lang.Process p = Runtime.getRuntime().exec(cmd);
            BufferedReader in = new BufferedReader(new InputStreamReader(
                p.getInputStream()));
            String line = null;
            while ((line = in.readLine()) != null) {
                s += line + "\n";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cmd.toString();
    }
    
    protected void initinfobar() {
		initVideoView(R.id.VideoView);
		LinearLayout.LayoutParams linearParams = null;
    	//set subtitle
		setSubtitleView();
		
	if(SystemProperties.getBoolean("3D_setting.enable", false)){
	    	subTitleView_sm= (SubtitleView) findViewById(R.id.subTitle_sm);
	    	subTitleView_sm.setGravity(Gravity.CENTER);
	    	subTitleView_sm.setTextColor(android.graphics.Color.GRAY);
	    	subTitleView_sm.setTextSize(sub_para.font);	    	
	    	subTitleView_sm.setTextStyle(Typeface.BOLD);
	}
    	if(AmPlayer.getProductType() == 1){
	        if(SettingsVP.display_mode.equals("480p") && SettingsVP.panel_height > 480) {
	        	linearParams = (LinearLayout.LayoutParams) subTitleView.getLayoutParams();
            	if (SettingsVP.panel_width > 720)
            	    linearParams.width = 720;
            	linearParams.bottomMargin = SettingsVP.panel_height - 480 + 10;	        	
	        	subTitleView.setLayoutParams(linearParams);
				if(subTitleView_sm!=null&&SystemProperties.getBoolean("3D_setting.enable", false)){
					subTitleView_sm.setLayoutParams(linearParams);
				}				
	        } else if (SettingsVP.display_mode.equals("720p") && SettingsVP.panel_height > 720) {
	        	linearParams = (LinearLayout.LayoutParams) subTitleView.getLayoutParams();
	        	if (SettingsVP.panel_width > 1280)
            	    linearParams.width = 1280;
            	linearParams.bottomMargin = SettingsVP.panel_height - 720 + 10;	        	
	        	subTitleView.setLayoutParams(linearParams);
				if(subTitleView_sm!=null&&SystemProperties.getBoolean("3D_setting.enable", false)){
					subTitleView_sm.setLayoutParams(linearParams);
				}				
	        } 
    	}
    	openFile(sub_para.sub_id);
	
        ImageButton browser = (ImageButton)findViewById(R.id.BrowserBtn);
        ImageButton more = (ImageButton)findViewById(R.id.moreBtn);
        ImageButton preItem = (ImageButton)findViewById(R.id.PreBtn);
        ImageButton nextItem = (ImageButton)findViewById(R.id.NextBtn);
        play = (ImageButton)findViewById(R.id.PlayBtn);
        fastforword = (ImageButton)findViewById(R.id.FastForward);
        fastreverse = (ImageButton)findViewById(R.id.FastReverse);
        infobar = (LinearLayout)findViewById(R.id.infobarLayout);
		//tony.wang 20120525
		if(null!=infobar)
			infobar.setVisibility(View.GONE);
		
    	if(AmPlayer.getProductType() == 1){
	        if(SettingsVP.display_mode.equals("480p") && SettingsVP.panel_height > 480) {
	        	linearParams = (LinearLayout.LayoutParams) infobar.getLayoutParams();
	        	if (SettingsVP.panel_width > 720)
            	    linearParams.width = 720;
            	linearParams.bottomMargin = SettingsVP.panel_height - 480 + 10;	        	
	        	subTitleView.setLayoutParams(linearParams);				
	        } else if (SettingsVP.display_mode.equals("720p") && SettingsVP.panel_height > 720) {
	        	linearParams = (LinearLayout.LayoutParams) infobar.getLayoutParams();
	        	if (SettingsVP.panel_width > 1280)
            	    linearParams.width = 1280;
            	linearParams.bottomMargin = SettingsVP.panel_height - 720 + 10;	        	
	        	subTitleView.setLayoutParams(linearParams);			
	        } 
    	}        
        myProgressBar = (SeekBar)findViewById(R.id.SeekBar02);
    	cur_time = (TextView)findViewById(R.id.TextView03);
    	total_time = (TextView)findViewById(R.id.TextView04);
    	cur_time.setText(secToTime(curtime, false));
    	total_time.setText(secToTime(totaltime, true));
    	if(bMediaInfo != null) {
	    	if(bMediaInfo.seekable == 0) {
				myProgressBar.setEnabled(false);
				fastforword.setEnabled(false);
				fastreverse.setEnabled(false);
				fastforword.setImageResource(R.drawable.ff_disable);
				fastreverse.setImageResource(R.drawable.rewind_disable);
			}
    	}

		setOnTouchHoldShow(browser);
		setOnTouchHoldShow(preItem);
		setOnTouchHoldShow(nextItem);
		setOnTouchHoldShow(play);
		setOnTouchHoldShow(fastforword);
		setOnTouchHoldShow(fastreverse);
		setOnTouchHoldShow(more);
		
        browser.setOnClickListener(new ImageButton.OnClickListener() {
			public void onClick(View v) {
			// TODO Auto-generated method stub
				/* hui.xu remove  
    			if(bMediaInfo == null)
					return;*/
				if(SettingsVP.chkEnableOSD2XScale() == true) {
					if(infobar != null) {
						infobar.setVisibility(View.GONE);
		    			getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,   
		    			WindowManager.LayoutParams.FLAG_FULLSCREEN);
					}
				}

	  			item_position_selected = item_position_selected_init + PlayList.getinstance().getindex();
				Intent selectFileIntent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putInt("item_position_selected", item_position_selected);
			    bundle.putInt("item_position_first", item_position_first);
			    bundle.putInt("fromtop_piexl", fromtop_piexl);
			    bundle.putIntegerArrayList("fileDirectory_position_selected", fileDirectory_position_selected);
			    bundle.putIntegerArrayList("fileDirectory_position_piexl", fileDirectory_position_piexl);
				selectFileIntent.setClass(playermenu.this, FileList.class);
				selectFileIntent.putExtras(bundle);
				//close sub;
				if(subTitleView!=null){
					subTitleView.closeSubtitle();	
					subTitleView.clear();	
				}
				if(subTitleView_sm!=null&&SystemProperties.getBoolean("3D_setting.enable", false)){
					subTitleView_sm.closeSubtitle();
				     	subTitleView_sm.clear();
				}			
				//stop play
				backToFileList = true;
				if(m_Amplayer != null)
					Amplayer_stop();
				if(!backToOtherAPK)
					startActivity(selectFileIntent);
				playermenu.this.finish();
			}
		});
        
        preItem.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(!INITOK)
					return;
				ResumePlay.saveResumePara(PlayList.getinstance().getcur(), curtime);
				String filename = PlayList.getinstance().moveprev();
				toast.cancel();
				toast.setText(catShowFilePath(filename));
				toast.show();
				playPosition = 0;
				if(m_Amplayer == null)
					return;
				//stop play
				else
					Amplayer_stop();
				PRE_NEXT_FLAG = 1;
				waitForHideOsd();
			}
        });
        
        nextItem.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(!INITOK)
					return;
				ResumePlay.saveResumePara(PlayList.getinstance().getcur(), curtime);
				String filename = PlayList.getinstance().movenext();
				toast.cancel();
				toast.setText(catShowFilePath(filename)); 
				toast.show();
				playPosition = 0;
				if(m_Amplayer == null)
					return;
				else
					Amplayer_stop();
				PRE_NEXT_FLAG = 1;
				waitForHideOsd();
			}
        });
        
        if(player_status == VideoInfo.PLAYER_RUNNING)
			play.setImageResource(R.drawable.pause);
        play.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(player_status == VideoInfo.PLAYER_RUNNING) {
					try	{
						m_Amplayer.Pause();
					} 
					catch(RemoteException e) {
						e.printStackTrace();
					}
				}
				else if(player_status == VideoInfo.PLAYER_PAUSE) {
					try	{
						m_Amplayer.Resume();
					} 
					catch(RemoteException e)	{
						e.printStackTrace();
					}
				}
				else if(player_status == VideoInfo.PLAYER_SEARCHING) {
					try	{
						ff_fb.cancel();
						if(FF_FLAG)
							m_Amplayer.FastForward(0);
						if(FB_FLAG)
							m_Amplayer.BackForward(0);
						FF_FLAG = false;
						FB_FLAG = false;
						FF_LEVEL = 0;
						FB_LEVEL = 0;
						
					} catch(RemoteException e) {
						e.printStackTrace();
					}
				}
				waitForHideOsd();
			}
        });
                
        fastforword.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v) {
				if(!INITOK)
					return;
				if(player_status == VideoInfo.PLAYER_SEARCHING) {
					if(FF_FLAG) {
						if(FF_LEVEL < FF_MAX) {
							FF_LEVEL = FF_LEVEL + 1;
						}
						else {
							FF_LEVEL = 0;
						}
						
						try	{
							m_Amplayer.FastForward(FF_STEP[FF_LEVEL]);
						} 
						catch(RemoteException e) {
							e.printStackTrace();
						}
						
						if(FF_LEVEL == 0) {
							ff_fb.cancel();
							FF_FLAG = false;
						}
						else {
							ff_fb.cancel();
							ff_fb.setText(new String("FF x" + Integer.toString(FF_SPEED[FF_LEVEL])));
		    				ff_fb.show();
						}
					}
					
					if(FB_FLAG) {
						if(FB_LEVEL > 0) {
							FB_LEVEL = FB_LEVEL - 1;
						}
						else {
							FB_LEVEL = 0;
						}
						
						try	{
							m_Amplayer.BackForward(FB_STEP[FB_LEVEL]);
						} 
						catch(RemoteException e) {
							e.printStackTrace();
						}
						
						if(FB_LEVEL == 0) {
							ff_fb.cancel();
							FB_FLAG = false;
						}
						else {
							ff_fb.cancel();
							ff_fb.setText(new String("FB x" + Integer.toString(FB_SPEED[FB_LEVEL])));
							ff_fb.show();
						}
					}
				}
				else {
					try	{
						m_Amplayer.FastForward(FF_STEP[1]);
					} 
					catch(RemoteException e) {
						e.printStackTrace();
					}
					FF_FLAG = true;
					FF_LEVEL = 1;
					ff_fb.cancel();
					ff_fb.setText(new String("FF x"+FF_SPEED[FF_LEVEL]));
    				ff_fb.show();
				}
				waitForHideOsd();
			}
        });
        
        fastreverse.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v) {
				if(!INITOK)
					return;
				if(player_status == VideoInfo.PLAYER_SEARCHING) {
					if(FB_FLAG) {
						if(FB_LEVEL < FB_MAX) {
							FB_LEVEL = FB_LEVEL + 1;
						}
						else {
							FB_LEVEL = 0;
						}
						
						try	{
							m_Amplayer.BackForward(FB_STEP[FB_LEVEL]);
						} 
						catch(RemoteException e) {
							e.printStackTrace();
						}
						
						if(FB_LEVEL == 0) {
							ff_fb.cancel();
							FB_FLAG = false;
						}
						else {
							ff_fb.cancel();
							ff_fb.setText(new String("FB x" + Integer.toString(FB_SPEED[FB_LEVEL])));
		    				ff_fb.show();
						}
					}
					
					if(FF_FLAG) {
						if(FF_LEVEL > 0) {
							FF_LEVEL = FF_LEVEL - 1;
						}
						else {
							FF_LEVEL = 0;
						}
						
						try	{
							m_Amplayer.FastForward(FF_STEP[FF_LEVEL]);
						} 
						catch(RemoteException e) {
							e.printStackTrace();
						}
						
						if(FF_LEVEL == 0) {
							ff_fb.cancel();
							FF_FLAG = false;
						}
						else {
							ff_fb.cancel();
							ff_fb.setText(new String("FF x" + Integer.toString(FF_SPEED[FF_LEVEL])));
							ff_fb.show();
						}
					}
				}
				else {
					try	{
						m_Amplayer.BackForward(FB_STEP[1]);
					} 
					catch(RemoteException e) {
						e.printStackTrace();
					}
					FB_FLAG = true;
					FB_LEVEL = 1;
					ff_fb.cancel();
					ff_fb.setText(new String("FB x"+FB_SPEED[FB_LEVEL]));
    				ff_fb.show();
				}
				waitForHideOsd();
			}
        });
        
        more.setOnClickListener(new ImageButton.OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				switchOsdView();
			}
		});
        if(curtime != 0)
        	myProgressBar.setProgress(curtime*100/totaltime);
        myProgressBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				isKeyOrTouchDown=false;
				waitForHideOsd();
			}
			
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				//timer.cancel();
				//progressSliding = true;
				isKeyOrTouchDown=true;
			}
			
			public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {
				// TODO Auto-generated method stub
				if(fromUser == true){
					timer.cancel();
					int dest = myProgressBar.getProgress();
					int pos = totaltime * (dest+1) / 100;
					
					try {
						if(m_Amplayer != null) {
					        seek_cur_time = curtime;
					        if(pos > curtime)
					        	seek = 2;
					        else
					        	seek = 1;
					        //Log.d(TAG, "seek curtime: " + seek_cur_time);
							m_Amplayer.Seek(pos);
						}
					}
					catch(RemoteException e) {
				        seek = 0;
				        seek_cur_time = 0;
						e.printStackTrace();
					}
					isKeyOrTouchDown=true;
					waitForHideOsd();
				}
			}
		});
    }
	
    private String catShowFilePath(String path) {
    	String text = null;
    	if(path.startsWith("/mnt/flash"))
    		text=path.replaceFirst("/mnt/flash","/mnt/flash");
    	else if(path.startsWith("/mnt/sda"))
    		text=path.replaceFirst("/mnt/sda","/mnt/sda");
    	else if(path.startsWith("/mnt/sdb"))
    		text=path.replaceFirst("/mnt/sdb","/mnt/sdb");
    	else if(path.startsWith("/mnt/sdcard"))
    		text=path.replaceFirst("/mnt/sdcard","/mnt/sdcard");
    	return text;
    }
    
    public static int setCodecMips() {
    	int tmp;
    	String buf = null;
		File file = new File(InputFile);
		if(!file.exists()) {        	
        	return 0;
        }
		file = new File(OutputFile);
		if(!file.exists()) {        	
        	return 0;
        }
		//read
		try {
			BufferedReader in = new BufferedReader(new FileReader(InputFile), 32);
			try {
				codec_mips = in.readLine();
				Log.d(TAG, "file content:"+codec_mips);
				tmp = Integer.parseInt(codec_mips)*2;
				buf = Integer.toString(tmp);
			} 
			finally {
    			in.close();
    		} 
		}
		catch(IOException e) {
			// TODO Auto-generated catch block
			Log.e(TAG, "IOException when read "+InputFile);
		} 
		
		//write
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(OutputFile), 32);
    		try {
    			out.write(buf);    
    			Log.d(TAG, "set codec mips ok:"+buf);
    		} 
			finally {
				out.close();
			}
			 return 1;
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			Log.e(TAG, "IOException when write "+OutputFile);
			return 0;
		}
	}
    
    public static int setDefCodecMips() {
    	File file = new File(OutputFile);
		if(!file.exists()) {        	
        	return 0;
        }
		if(codec_mips == null)
			return 0;
    	try {
			BufferedWriter out = new BufferedWriter(new FileWriter(OutputFile), 32);
    		try {
    			out.write(codec_mips);    
    			Log.d(TAG, "set codec mips ok:"+codec_mips);
    		} 
			finally {
				out.close();
			}
			 return 1;
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			Log.e(TAG, "IOException when write "+OutputFile);
			return 0;
		}
    }

	public int set2XScale() {
		if(SettingsVP.chkEnableOSD2XScale() == false)
			return 0;
		bSet2XScale = true;
    	File OutputFile = new File(RequestScaleFile);
		if(!OutputFile.exists()) {        	
        	return 0;
        }

    	try {
			BufferedWriter out = new BufferedWriter(new FileWriter(OutputFile), 32);
    		try {
				Log.d(TAG, "request  2XScale" );

    			out.write(" 1 ");    
    		} 
			finally {
				out.close();
			}
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			Log.e(TAG, "IOException when write "+OutputFile);
		}
		return 1;
    }
    
    
    public int disable2XScale() {
		if(bSet2XScale == false)
			return 0;
		bSet2XScale = false;
	//	ScreenOffForWhile(SET_OSD_OFF);
		Log.d(TAG, "request disable2XScale");

    	File OutputFile = new File(RequestScaleFile);
		if(!OutputFile.exists()) {        	
        	return 0;
        }

    	try {
			BufferedWriter out = new BufferedWriter(new FileWriter(OutputFile), 32);
    		try {
				Log.d(TAG, "request disable2XScale " );

    			out.write(" 2 ");    
    		} 
			finally {
				out.close();
			}
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			Log.e(TAG, "IOException when write "+OutputFile);
		}
		return 1;
    }
    
    protected void closeScreenOffTimeout() {
    	if(mScreenLock.isHeld() == false)
    		mScreenLock.acquire();
    }
    
    protected void openScreenOffTimeout() {
    	if(mScreenLock.isHeld() == true)
    		mScreenLock.release();
    }
    
    protected void waitForHide() {
    	final Handler handler = new Handler(){   
    		  
            public void handleMessage(Message msg) {   
                switch (msg.what) {       
                case 0x3c:       
                	hide_infobar();
                    break;       
                }       
                super.handleMessage(msg);   
            }
               
        };   
        TimerTask task = new TimerTask(){   
      
            public void run() {   
                if(!touchVolFlag) {
                    Message message = Message.obtain();
                    message.what = 0x3c;       
                    handler.sendMessage(message);     
                }   
            }
        };   
        
        timer.cancel();
        timer = new Timer();
    	timer.schedule(task, 5000);
    }
    
    protected void ResumeCountdown() {
    	final Handler handler = new Handler(){   	  
            public void handleMessage(Message msg) {   
                switch (msg.what) {       
                case 0x3d:
                	if(confirm_dialog.isShowing()) {
	                	if(resumeSecond > 0) {
	                		String cancel = playermenu.this.getResources().getString(R.string.str_cancel);
	                		confirm_dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
	                			.setText(cancel+" ( "+(--resumeSecond)+" )");
	                		ResumeCountdown();
	                	}
	                	else {
	                		playPosition = 0;
				        	confirm_dialog.dismiss();
				        	resumeSecond = 8;
	                	}
                	}
                    break;       
                }       
                super.handleMessage(msg);   
            }  
        };
		   
        TimerTask task = new TimerTask(){   
            public void run() {   
                Message message = Message.obtain();
                message.what = 0x3d;       
                handler.sendMessage(message);     
            }   
               
        };   
        Timer resumeTimer = new Timer();
        resumeTimer.schedule(task, 1000);
    }
    
    protected void hide_infobar() {
    	infobar.setVisibility(View.GONE);
		if(subTitleView!=null)
			subTitleView.redraw();
		if(subTitleView_sm!=null&&SystemProperties.getBoolean("3D_setting.enable", false)){
		     subTitleView_sm.redraw();
		}		
    	getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,   
    			WindowManager.LayoutParams.FLAG_FULLSCREEN);
    	setOSDOnOff(false);		
    }
    
    protected void show_menu() {
    	infobar.setVisibility(View.VISIBLE);
    	if(AmPlayer.getProductType() == 1){
	    	getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    	}
    	else{
    		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    	}
    	setOSDOnOff(true);
    }
    
	public boolean onTouchEvent (MotionEvent event) {
    	super.onTouchEvent(event);
    	if(event.getAction() == MotionEvent.ACTION_DOWN) {
			if((morbar.getVisibility() == View.VISIBLE)||(infobar.getVisibility() == View.VISIBLE))
			{
				showNoOsdView();
			}
			else if((View.VISIBLE==otherbar.getVisibility())
				||(View.VISIBLE==infodialog.getVisibility())
				||(View.VISIBLE==subbar.getVisibility()))
			{
				// do nothing
				showNoOsdView();
			}
			else
			{
				showOsdView();
			}

			int flag = getCurOsdViewFlag();
			if(OSD_INFO_BAR==flag)
			{
				//tony.wang
				AmPlayer.setOSDOnFlag(true);
				curtime=AmPlayer.getBackupCurrentTime()/1000;
				totaltime = AmPlayer.getBackupTotalTime();
				//Log.i("wxl","curtime:"+curtime+";totaltime:"+totaltime);
				cur_time.setText(secToTime(curtime, false));
		    	total_time.setText(secToTime(totaltime, true));
		    	myProgressBar.setProgress(curtime*100/totaltime);
			}
			
			intouch_flag = true;
    	}
    	return true;
    }
    
    private String secToTime(int i, Boolean isTotalTime) {
		String retStr = null;
		int hour = 0;
		int minute = 0;
		int second = 0;
		if(i <= 0) {
            return "00:00:00";
		}
		else {
			minute = i/60;
			if(minute < 60) {
				second = i%60;
				retStr = "00:" + unitFormat(minute) + ":" + unitFormat(second);
			}
			else {
				hour = minute/60;
				if (hour > 99)
					return "99:59:59";
				minute = minute%60;
				second = i%60;
				retStr = unitFormat(hour) + ":" + unitFormat(minute) + ":" + unitFormat(second);
			}
		}
		return retStr;
	}
	
	private String unitFormat(int i) {
		String retStr = null;
		if(i >= 0 && i < 10)
			retStr = "0" + Integer.toString(i);
		else
			retStr = Integer.toString(i);
		return retStr;
    }
	
	@Override
    public void onDestroy() {
        ResumePlay.saveResumePara(PlayList.getinstance().getcur(), curtime);
        //close sub;
        if(subTitleView!=null)
        	subTitleView.closeSubtitle();
	if(subTitleView_sm!=null&&SystemProperties.getBoolean("3D_setting.enable", false)){
	     subTitleView_sm.closeSubtitle();
	}		
        backToFileList = true;
        Amplayer_stop();
        if(m_Amplayer != null)
			try {
				if(SystemProperties.getBoolean("3D_setting.enable", false)){
					m_Amplayer.Set3Dgrating(0);
					m_Amplayer.Set3Dmode(0); //close 3D
					
				}
				if(!fb32){
					m_Amplayer.DisableColorKey();
				}
			} 
			catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}


			
        StopPlayerService();
        setDefCodecMips();
        openScreenOffTimeout();
        SettingsVP.disableVideoLayout();
        SettingsVP.setVideoRotateAngle(0);
        unregisterReceiver(mReceiver);

        if(AmPlayer.getProductType() == 1) //1:MID 0:other
        	AmPlayer.enable_freescale(MID_FREESCALE);
        if(m1080scale == 2 || (m1080scale == 1 && (outputmode.equals("1080p") || outputmode.equals("1080i") || outputmode.equals("720p")))){
			writeFile(Fb0Blank,"1");
			writeFile(Fb1Blank,"1");
			AmPlayer.GL2XScale(0);
			AmPlayer.enableFreescaleMBX();
        }
        super.onDestroy();
    }

	@Override
    public void onPause() {
		Log.d(TAG,"onPause");
        super.onPause();
        mPaused = true;
        
        if(confirm_dialog != null && confirm_dialog.isShowing()) {
            confirm_dialog.dismiss();
        }        
        
        setOSDOnOff(true);
//        StorageManager m_storagemgr = (StorageManager) getSystemService(Context.STORAGE_SERVICE);
//        m_storagemgr.unregisterListener(mListener);
        unregisterReceiver(mMountReceiver);

        SystemProperties.set("vplayer.hideStatusBar.enable","false");      
        
        if(mSuspendFlag){
            if(player_status == VideoInfo.PLAYER_RUNNING) {
                try{
                    m_Amplayer.Pause();
                } 
				catch(RemoteException e) {
                    e.printStackTrace();
                }
            }
            mSuspendFlag = false;
            closeScreenOffTimeout();
        }
        else {
            if(!backToFileList){
			    PlayList.getinstance().rootPath =null;
            }
            finish();
        }
        if(m1080scale == 2 || (m1080scale == 1 && (outputmode.equals("1080p") || outputmode.equals("1080i") || outputmode.equals("720p")))){
        	writeFile(Fb0Blank,"1");
			writeFile(Fb1Blank,"1");
        }
		disable2XScale();
        ScreenMode.setScreenMode("0");
    }

	public void onStop(){
		super.onStop();
		Log.d(TAG,"onStop");
		if(!backToFileList){
			PlayList.getinstance().rootPath =null;
		}
		finish();
	}
    
	//=========================================================
    private Messenger m_PlayerMsg = new Messenger(new Handler() {
		Toast tp = null;
    	public void handleMessage(Message msg) {
    		switch(msg.what) {
    			case VideoInfo.TIME_INFO_MSG:
    				//Log.d(TAG,"get time "+secToTime((msg.arg1)/1000,false));
                    //Log.d(TAG,"total time "+secToTime(msg.arg2,false));
    		    	cur_time.setText(secToTime((msg.arg1)/1000, false));
    		    	total_time.setText(secToTime(msg.arg2, true));
    		    	curtime = msg.arg1/1000;
    		    	totaltime = msg.arg2;
    		    	
                    boolean mVfdDisplay = SystemProperties.getBoolean("hw.vfd", false);
                    if(mVfdDisplay) {
                        String[] cmdtest = {
                            "/system/bin/sh",
                            "-c",
                            "echo" + " "
                                + cur_time.getText().toString().substring(1)
                                + " " + "> /sys/devices/platform/m1-vfd.0/led"
                        };
                        do_exec(cmdtest);
                    }

    		    	//for subtitle tick;
    		    	if(player_status == VideoInfo.PLAYER_RUNNING) {
    		    		if(subTitleView!=null&&sub_para.sub_id!=null)
    		    			subTitleView.tick(msg.arg1);
				if(SystemProperties.getBoolean("3D_setting.enable", false)){
					if(subTitleView_sm!=null&View.INVISIBLE ==subTitleView_sm.getVisibility()&&is3DVideoDisplayFlag){
						subTitleView_sm.setVisibility(View.VISIBLE);
					}
					if(subTitleView_sm!=null&&sub_para.sub_id!=null)
    		    				subTitleView_sm.tick(msg.arg1);
					
				}
    		    	}
    		    	if(totaltime == 0)
						myProgressBar.setProgress(0);
					else {
						if((seek == 1) && (curtime >= (seek_cur_time-2))) {
							//Log.d(TAG, "count curtime: " + curtime);
							//Log.d(TAG, "seek curtime: " + seek_cur_time);
							return;
						}
						else if((seek == 2) && (curtime <= (seek_cur_time+2))) {
							//Log.d(TAG, "count curtime: " + curtime);
							//Log.d(TAG, "seek curtime: " + seek_cur_time);
							return;
						}
						//Log.d(TAG, "curtime: " + curtime);
						
						seek = 0;
						seek_cur_time = 0;
						//if(!progressSliding)
						myProgressBar.setProgress(msg.arg1/1000*100/totaltime);
					}
    				break;
    			case VideoInfo.STATUS_CHANGED_INFO_MSG:
    				player_status = msg.arg1;
    				
    				switch(player_status) {
					case VideoInfo.PLAYER_RUNNING:
						play.setImageResource(R.drawable.pause);						
						break;
					case VideoInfo.PLAYER_PAUSE:
					case VideoInfo.PLAYER_SEARCHING:	
						play.setImageResource(R.drawable.play);
						break;
					case VideoInfo.PLAYER_EXIT:	
						audio_flag = 0;
						if(PRE_NEXT_FLAG == 1 && !backToFileList) {
    						Log.d(TAG,"to play another file!");
							//new PlayThread().start();
							if(SettingsVP.getParaBoolean(SettingsVP.RESUME_MODE)) {
								if (resumePlay() == 0)
									Amplayer_play();
							}
							else {
								playPosition = 0;
								Amplayer_play();
							}
    						PRE_NEXT_FLAG = 0;
							//progressSliding = false;
    					}
						if(subTitleView!=null)
						{
							subTitleView.closeSubtitle(); //need return focus.

							if(subTitleView_sm!=null&&SystemProperties.getBoolean("3D_setting.enable", false)){
								subTitleView_sm.closeSubtitle(); //need return focus.	
								
							}
							
				            int flag = getCurOsdViewFlag();
							if(OSD_MORE_BAR==flag)
				            {
				            	switchOsdView();
								ImageButton morebtn = (ImageButton) findViewById(R.id.moreBtn);
				            	morebtn.requestFocus();
				            }
						}

						
						sub_para.totalnum = 0;
						cur_audio_stream = 0;
						InternalSubtitleInfo.setInsubNum(0);
						
						boolean mVfdDisplay_exit = SystemProperties.getBoolean("hw.vfd", false);
						if(mVfdDisplay_exit) {
						    String[] cmdtest = {
                                    "/system/bin/sh",
                                    "-c",
                                    "echo"
                                        + " "
                                        + "0:00:00"
                                        + " "
                                        + "> /sys/devices/platform/m1-vfd.0/led"
							};
							do_exec(cmdtest);
                        }
						break;
					case VideoInfo.PLAYER_STOPED:
						break;
					case VideoInfo.PLAYER_PLAYEND:
						try	{
							m_Amplayer.Close();
						} 
						catch(RemoteException e) {
							e.printStackTrace();
						}
						ResumePlay.saveResumePara(PlayList.getinstance().getcur(), 0);
						playPosition = 0;
						if(m_playmode == REPEATLIST)
							PlayList.getinstance().movenext();
						AudioTrackOperation.AudioStreamFormat.clear();
						AudioTrackOperation.AudioStreamInfo.clear();
						INITOK = false;
						PRE_NEXT_FLAG = 1;
						break;
					case VideoInfo.PLAYER_ERROR:
						String InfoStr = null;
						InfoStr = Errorno.getErrorInfo(msg.arg2);
						if(tp == null){
							tp = Toast.makeText(playermenu.this, "Status Error:"+InfoStr, Toast.LENGTH_SHORT);															
						}else{
							tp.cancel();
							tp.setText("Status Error:"+InfoStr);
						}
						tp.show();						
						Log.d(TAG, "Player error, msg.arg2 = " + Integer.toString(msg.arg2));
						if (msg.arg2 < 0) {							
							try	{
							m_Amplayer.Close();
							} 
							catch(RemoteException e) {
								e.printStackTrace();
							}
							ResumePlay.saveResumePara(PlayList.getinstance().getcur(), 0);
							playPosition = 0;
							if(m_playmode == REPEATLIST)
								PlayList.getinstance().movenext();
							AudioTrackOperation.AudioStreamFormat.clear();
							AudioTrackOperation.AudioStreamInfo.clear();
							INITOK = false;
							PRE_NEXT_FLAG = 1;							
						}
						break;
					case VideoInfo.PLAYER_INITOK:
						INITOK = true;
						NOT_FIRSTTIME = true;
						try {
							bMediaInfo = m_Amplayer.GetMediaInfo();
						} 
						catch(RemoteException e) {
							e.printStackTrace();
						}
						
						if((bMediaInfo != null) && (subTitleView != null)) {
							subTitleView.setDisplayResolution(
									SettingsVP.panel_width, SettingsVP.panel_height);
							subTitleView.setVideoResolution(
									bMediaInfo.getWidth(), bMediaInfo.getHeight());
						}
						if(bMediaInfo != null&&subTitleView_sm!=null&&SystemProperties.getBoolean("3D_setting.enable", false)){
							subTitleView_sm.setDisplayResolution(
									SettingsVP.panel_width, SettingsVP.panel_height);
							subTitleView_sm.setVideoResolution(
									bMediaInfo.getWidth(), bMediaInfo.getHeight());							
						}
						if(SystemProperties.getBoolean("3D_setting.enable", false)&&bMediaInfo.getVideoFormat().compareToIgnoreCase("H264MVC")==0){//if 264mvc,set auto mode.
							try {
								m_Amplayer.Set3Dgrating(1); //open grating
								m_Amplayer.Set3Dmode(1);
								
				    			mode_3d = 1;				
							} 
							catch(RemoteException e) {
								e.printStackTrace();
							}			    					
						}					
						
						if(bMediaInfo.drm_check == 0) {
						    try {
							    m_Amplayer.Play();
                            } 
							catch (RemoteException e) {
                                e.printStackTrace();
                            }
                        }
						sub_para.totalnum =subtitleUtils.getExSubTotal()+InternalSubtitleInfo.getInsubNum();
						if(sub_para.totalnum >0){
        						
							sub_para.curid = subtitleUtils.getCurrentInSubtitleIndexByJni();
							//change by jeff.yang	
							if(sub_para.curid == 0xff||sub_para.enable==false)
							    sub_para.curid = sub_para.totalnum;
							if(sub_para.totalnum>0)
					    		sub_para.sub_id =subtitleUtils.getSubID(sub_para.curid);
							else
							    sub_para.sub_id = null;

							setSubtitleView();
							openFile(sub_para.sub_id);
						}else{
							sub_para.sub_id = null;
						}
						
						if(bMediaInfo.seekable == 0) {
							myProgressBar.setEnabled(false);
							fastforword.setEnabled(false);
							fastreverse.setEnabled(false);
							fastforword.setImageResource(R.drawable.ff_disable);
							fastreverse.setImageResource(R.drawable.rewind_disable);
						}
						else {
							myProgressBar.setEnabled(true);
							fastforword.setEnabled(true);
							fastreverse.setEnabled(true);
							fastforword.setImageResource(R.drawable.ff);
							fastreverse.setImageResource(R.drawable.rewind);
						}
						if(setCodecMips() == 0){
				        		Log.d(TAG, "setCodecMips Failed");

						}
                        if(SystemProperties.getBoolean("vplayer.hideStatusBar.enable",false) == false) {
                            Log.d(TAG, "hideStatusBar is false");
                            try {   
                                SystemProperties.set("vplayer.hideStatusBar.enable","true");
                                Log.d(TAG, "hideStatusBar is" + SystemProperties.getBoolean("vplayer.hideStatusBar.enable",false));
                            } catch (RuntimeException e) {
                                e.printStackTrace();
                            }
                        }
						break;
					case VideoInfo.PLAYER_SEARCHOK:
						//progressSliding = false;
						break;
					case VideoInfo.DIVX_AUTHOR_ERR:
					    Log.d(TAG, "Authorize Error");
						try {
						    DivxInfo divxInfo;
							divxInfo = m_Amplayer.GetDivxInfo();
							new AlertDialog.Builder(playermenu.this)
							.setTitle("Authorization Error")
							.setMessage("This player is not authorized to play this DivX protected video")
							.setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
							    public void onClick(DialogInterface dialog, int whichButton) {
								    Intent selectFileIntent = new Intent();
									selectFileIntent.setClass(playermenu.this, FileList.class);
									// close sub;
									if(subTitleView != null)
									    subTitleView.closeSubtitle();
									if(subTitleView_sm!=null&&SystemProperties.getBoolean("3D_setting.enable", false)){
									     subTitleView_sm.closeSubtitle();
									}									
									if(!fb32) {
									    // Hide the view with key color
										LinearLayout layout = (LinearLayout) findViewById(R.id.BaseLayout1);
										if(layout != null) {
                                            layout.setVisibility(View.INVISIBLE);
                                            layout.invalidate();
                                        }

										//tony.wang 20120525
										LinearLayout layout2 = (LinearLayout) findViewById(R.id.BaseLayout2);
										if(layout2 != null) {
                                            layout.setVisibility(View.INVISIBLE);
                                            layout2.invalidate();
                                        }
									}
									// stop play
									backToFileList = true;
									if(m_Amplayer != null)
									    Amplayer_stop();
									PlayList.getinstance().rootPath =null;
									if(!backToOtherAPK)
										startActivity(selectFileIntent);
									playermenu.this.finish();
								}
							}).show();
						} 
						catch (RemoteException e) {
						    e.printStackTrace();
                        }
                        break;
                    case VideoInfo.DIVX_EXPIRED:
                        Log.d(TAG, "Authorize Expired");
                        try {
                            DivxInfo divxInfo;
                            divxInfo = m_Amplayer.GetDivxInfo();
                            String s = "This rental has "
                                + msg.arg2
                                + " views left\nDo you want to use one of your "
                                + msg.arg2 + " views now";
                            new AlertDialog.Builder(playermenu.this)
							.setTitle("View DivX(R) VOD Rental")
							.setMessage(s)
							.setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    Intent selectFileIntent = new Intent();
                                    selectFileIntent.setClass(playermenu.this, FileList.class);
                                    // close sub;
                                    if(subTitleView != null)
                                        subTitleView.closeSubtitle();
                                        
					if(subTitleView_sm!=null&&SystemProperties.getBoolean("3D_setting.enable", false)){
					     subTitleView_sm.closeSubtitle();
					}
                                    if(!fb32) {
                                        // Hide the view with key color
                                        LinearLayout layout = (LinearLayout) findViewById(R.id.BaseLayout1);
                                        if (layout != null) {
                                            layout.setVisibility(View.INVISIBLE);
                                            layout.invalidate();
                                        }

										//tony.wang 20120525
										LinearLayout layout2 = (LinearLayout) findViewById(R.id.BaseLayout2);
										if(layout2 != null) {
                                            layout.setVisibility(View.INVISIBLE);
                                            layout2.invalidate();
                                        }
                                    }
                                    // stop play
                                    backToFileList = true;
                                    if(m_Amplayer != null)
                                        Amplayer_stop();
                                    PlayList.getinstance().rootPath =null;
                                    if(!backToOtherAPK)
                                    	startActivity(selectFileIntent);
                                    playermenu.this.finish();
                                }
                            }).show();
                        } 
						catch (RemoteException e) {
                            e.printStackTrace();
                        }
						break;
                    case VideoInfo.DIVX_RENTAL:
                        Log.d(TAG, "Authorize rental");
                        try {
                            DivxInfo divxInfo;
                            divxInfo = m_Amplayer.GetDivxInfo();
                            String s = "This rental has "
                                + msg.arg2
                                + " views left\nDo you want to use one of your "
                                + msg.arg2 + " views now?";
                            new AlertDialog.Builder(playermenu.this)
							.setTitle("View DivX(R) VOD Rental")
							.setMessage(s)
							.setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    // finish();
                                    try {
                                        m_Amplayer.Play();
                                    } 
									catch (RemoteException e) {
                                        e.printStackTrace();
                                    }
                                }
                            })
							.setNegativeButton(R.string.str_cancel, new DialogInterface.OnClickListener() {
							    public void onClick(DialogInterface dialog, int whichButton) {
                                    Intent selectFileIntent = new Intent();
                                    selectFileIntent.setClass(playermenu.this, FileList.class);
                                    // close sub;
                                    if(subTitleView != null)
                                        subTitleView.closeSubtitle();
					if(subTitleView_sm!=null&&SystemProperties.getBoolean("3D_setting.enable", false)){
						subTitleView_sm.closeSubtitle();
					}										
                                    if(!fb32) {
                                        // Hide the view with key color
                                        LinearLayout layout = (LinearLayout) findViewById(R.id.BaseLayout1);
										if(layout != null) {
                                            layout.setVisibility(View.INVISIBLE);
                                            layout.invalidate();
                                        }

										//tony.wang 20120525
										LinearLayout layout2 = (LinearLayout) findViewById(R.id.BaseLayout2);
										if(layout2 != null) {
                                            layout.setVisibility(View.INVISIBLE);
                                            layout2.invalidate();
                                        }
                                    }
                                    // stop play
                                    backToFileList = true;
                                    if(m_Amplayer != null)
                                        Amplayer_stop();
                                   	PlayList.getinstance().rootPath =null;
                                   	if(!backToOtherAPK)
                                    	startActivity(selectFileIntent);
                                    playermenu.this.finish();
                                }
                            }).show();
                        } 
						catch (RemoteException e) {
                            e.printStackTrace();
                        }
						break;
					default:
						break;
    				}
    				break;
    			case VideoInfo.AUDIO_CHANGED_INFO_MSG:
    				total_audio_num = msg.arg1;
    				cur_audio_stream = msg.arg2;
    				break;
    			case VideoInfo.HAS_ERROR_MSG:
					String errStr = null;
					errStr = Errorno.getErrorInfo(msg.arg2);
    				audio_flag = msg.arg2;
					if(tp == null){
						tp = Toast.makeText(playermenu.this, errStr, Toast.LENGTH_SHORT);						
					}else{
						tp.cancel();
						tp.setText(errStr);
					}					
					tp.show();
    				break;
    			default:
    				super.handleMessage(msg);
    				break;
    		}
    	}
    });   
	
    public Player m_Amplayer = null;
    private void Amplayer_play() {
        // stop music player
        Intent intent = new Intent();
        intent.setAction("com.android.music.musicservicecommand.pause");
        intent.putExtra("command", "stop");
        this.sendBroadcast(intent);
        seek = 0;
        seek_cur_time = 0;
		
		ff_fb.cancel();
		FF_FLAG = false;
		FB_FLAG = false;
		FF_LEVEL = 0;
		FB_LEVEL = 0;
    	try {
			int flag = getCurOsdViewFlag();
			if(OSD_MORE_BAR==flag)
			{
				showOsdView();
			}

			setOSDOnOff(true);//make sure subtitle show ok

			//reset sub;
			subTitleView.clear();
			subinit();
			setSubtitleView();
			
	    if(SystemProperties.getBoolean("3D_setting.enable", false)){
        	try {
    			m_Amplayer.Set3Dmode(0);
    			mode_3d = 0;
    			
    			m_Amplayer.Set3Dviewmode(0);
    			view_mode = 0;
    		} 
    		catch(RemoteException e) {
    			e.printStackTrace();
    		}
    		
    		if(PlayList.getinstance().getcur().indexOf("[3D]")!=-1&&PlayList.getinstance().getcur().indexOf("[HALF]")!=-1){
				m_Amplayer.Set3Dgrating(1);
    			m_Amplayer.Set3Dmode(1);				
    			mode_3d = 1;
    		    is3DVideoDisplayFlag = true;
    		}else if(PlayList.getinstance().getcur().indexOf("[3D]")!=-1&&PlayList.getinstance().getcur().indexOf("[FULL]")!=-1){
				m_Amplayer.Set3Dgrating(1);
    			m_Amplayer.Set3Dmode(2);
    			mode_3d = 2;  
    			m_Amplayer.Set3Daspectfull(1);
    			mode_3d = 16;    	
    		    is3DVideoDisplayFlag = true;
    		}else if(PlayList.getinstance().getcur().indexOf("[3D]")!=-1){
				m_Amplayer.Set3Dgrating(1);
    			m_Amplayer.Set3Dmode(2);
    			mode_3d = 2;     			
    		    is3DVideoDisplayFlag = true;   			
    		}
    	}
	    
		if(subTitleView_sm!=null&&SystemProperties.getBoolean("3D_setting.enable", false)){
			subTitleView_sm.clear();			
			subTitleView_sm.setTextColor(android.graphics.Color.GRAY);
	    	subTitleView_sm.setTextSize(sub_para.font);	    		

		}

		m_Amplayer.Open(PlayList.getinstance().getcur(), playPosition);								
            // openFile(sub_para.sub_id);
        mRotateHandler.sendEmptyMessageDelayed(GETROTATION, GETROTATION_TIMEOUT);    
		}
		catch(RemoteException e) {
			e.printStackTrace();
		}
    }
    
    private void Amplayer_stop() {
    	try {
			m_Amplayer.Stop();
		} 
		catch(RemoteException e) {
			e.printStackTrace();
		}
		
		try {
			m_Amplayer.Close();
		} 
		catch(RemoteException e) {
			e.printStackTrace();
		}
		AudioTrackOperation.AudioStreamFormat.clear();
		AudioTrackOperation.AudioStreamInfo.clear();
		INITOK = false;
		mRotateHandler.removeMessages(GETROTATION);
    }
    
    ServiceConnection m_PlayerConn = new ServiceConnection() {
		public void onServiceConnected(ComponentName name, IBinder service) {
			m_Amplayer = Player.Stub.asInterface(service);

			try {
				m_Amplayer.Init();
			} 
			catch(RemoteException e) {
				e.printStackTrace();
				Log.d(TAG,"init fail!");
			}
			
			try {
				m_Amplayer.RegisterClientMessager(m_PlayerMsg.getBinder());
			} 
			catch(RemoteException e) {
				e.printStackTrace();
				Log.e(TAG, "set client fail!");
			}
			
			//auto play
			Log.d(TAG,"to play files!");
			try {
				final short color = ((0x8 >> 3) << 11) 
									| ((0x30 >> 2) << 5) 
									| ((0x8 >> 3) << 0);
				m_Amplayer.SetColorKey(color);
				Log.d(TAG, "set colorkey() color=" + color);
			}
			catch(RemoteException e) {
				e.printStackTrace();
			}
			Amplayer_play();
		}

		public void onServiceDisconnected(ComponentName name) {
			try {
				m_Amplayer.Stop();
			} 
			catch(RemoteException e) {
				e.printStackTrace();
			}
			
			try {
				m_Amplayer.Close();
			} 
			catch(RemoteException e) {
				e.printStackTrace();
			}
			m_Amplayer = null;
		}
    };

    public void StartPlayerService() {
    	Intent intent = new Intent();
    	ComponentName hcomponet = new ComponentName("com.farcore.videoplayer","com.farcore.playerservice.AmPlayer");
    	intent.setComponent(hcomponet);
    	this.startService(intent);
    	this.bindService(intent, m_PlayerConn, BIND_AUTO_CREATE);
    }
    
    public void StopPlayerService() {
    	this.unbindService(m_PlayerConn);
    	Intent intent = new Intent();
    	ComponentName hcomponet = new ComponentName("com.farcore.videoplayer","com.farcore.playerservice.AmPlayer");
    	intent.setComponent(hcomponet);
    	this.stopService(intent);
    	m_Amplayer = null;
    }

    private String setSublanguage() {
    	String type=null;
    	String able=getResources().getConfiguration().locale.getCountry();
	
    	if(able.equals("TW"))  
    		 type ="BIG5";
    	else if(able.equals("JP"))
    		  type ="cp932";
    	else if(able.equals("KR"))
    		  type ="cp949";
    	else if(able.equals("IT")||able.equals("FR")||able.equals("DE"))
    		  type ="iso88591";
    	else
    		  type ="GBK";
    	
    	return type;
    }
    
	private void openFile(SubID filepath) {
		setSublanguage();	
		
		if(filepath==null)
			return;
		
		try {
			if(subTitleView.setFile(filepath,setSublanguage())==Subtitle.SUBTYPE.SUB_INVALID)
				return;
			if(subTitleView_sm!=null&&SystemProperties.getBoolean("3D_setting.enable", false)){
				if(subTitleView_sm.setFile(filepath,setSublanguage())==Subtitle.SUBTYPE.SUB_INVALID){
					return;
				}
			}
			
		} 
		catch(Exception e) {
			Log.d(TAG, "open:error");
			subTitleView = null;
			if(subTitleView_sm!=null&&SystemProperties.getBoolean("3D_setting.enable", false)){
			     subTitleView_sm= null;
			}
			e.printStackTrace();
		}
	
	}
	
	private int resumePlay() {
		final int pos = ResumePlay.check(PlayList.getinstance().getcur());
		Log.d(TAG, "resumePlay() pos is :"+pos);
		if(pos > 0) {
			confirm_dialog = new AlertDialog.Builder(this)
				.setTitle(R.string.setting_resume)  
				.setMessage(R.string.str_resume_play) 
				.setPositiveButton(R.string.str_ok,  
					new DialogInterface.OnClickListener() {  
			            public void onClick(DialogInterface dialog, int whichButton) {  
			                playPosition = pos;
			            }  
			        })  
			    .setNegativeButton(playermenu.this.getResources().getString(R.string.str_cancel) + " ( "+resumeSecond+" )",  
				    new DialogInterface.OnClickListener() {  
				        public void onClick(DialogInterface dialog, int whichButton) {  
				        	playPosition = 0;
				        }  
				    })  
			    .show(); 
			confirm_dialog.setOnDismissListener(new myAlertDialogDismiss());
			ResumeCountdown();
			return pos;
		}
		if(!NOT_FIRSTTIME)
			StartPlayerService();
		return pos;
	}
	
	private class myAlertDialogDismiss implements DialogInterface.OnDismissListener {
		public void onDismiss(DialogInterface arg0) {
			// TODO Auto-generated method stub
			if(!NOT_FIRSTTIME)
    			StartPlayerService();
        	else
        		Amplayer_play();
        	resumeSecond = 8;
		}
		
	}
	
//	private final StorageEventListener mListener = new StorageEventListener() {
//		public void onUsbMassStorageConnectionChanged(boolean connected) {
//			//this is the action when connect to pc
//			return ;
//		}
//		
//		public void onStorageStateChanged(String path, String oldState, String newState) {
//			if(newState == null || path == null) 
//				return;
//			
//			if(newState.compareTo("unmounted") == 0||newState.compareTo("removed") == 0) {
//				if(PlayList.getinstance().rootPath!=null) {
//					if(PlayList.getinstance().rootPath.startsWith(path)) {
//						Intent selectFileIntent = new Intent();
//						selectFileIntent.setClass(playermenu.this, FileList.class);
//						//close sub;
//						if(subTitleView!=null)
//							subTitleView.closeSubtitle();		
//						if(subTitleView_sm!=null&&SystemProperties.getBoolean("3D_setting.enable", false)){
//							subTitleView_sm.closeSubtitle();
//						}
//							
//						//stop play
//						backToFileList = true;
//						if(m_Amplayer != null)
//							Amplayer_stop();
//						PlayList.getinstance().rootPath=null;
//						startActivity(selectFileIntent);
//						playermenu.this.finish();
//					}
//				}
//			}
//		}
//	};
	
    private BroadcastReceiver mMountReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Uri uri = intent.getData();
            String path = uri.getPath();                   
            
            if (action == null || path == null)
            	return;
            
            if (action.equals(Intent.ACTION_MEDIA_EJECT)) {
				if(PlayList.getinstance().getcur()!=null) {
					if(PlayList.getinstance().getcur().startsWith(path)) {
						Intent selectFileIntent = new Intent();
						selectFileIntent.setClass(playermenu.this, FileList.class);
						//close sub;
						if(subTitleView!=null)
							subTitleView.closeSubtitle();		
						if(subTitleView_sm!=null&&SystemProperties.getBoolean("3D_setting.enable", false)){
							subTitleView_sm.closeSubtitle();
						}
							
						//stop play
						backToFileList = true;
						if(m_Amplayer != null)
							Amplayer_stop();
						PlayList.getinstance().rootPath=null;
						if(!backToOtherAPK)
							startActivity(selectFileIntent);
						playermenu.this.finish();
					}
				}				
            } else if (action.equals(Intent.ACTION_MEDIA_MOUNTED)) {          	
                // Nothing				
            } else if (action.equals(Intent.ACTION_MEDIA_UNMOUNTED)) {
                // SD card unavailable
                // handled in ACTION_MEDIA_EJECT
            } 
        }
    };	
	
	@Override
	public void onResume() {
		super.onResume();
		mPaused = false;
		
		int getRotation = mWindowManager.getDefaultDisplay().getRotation();
		//Log.d("sensor", "rotate angle: "+Integer.toString(getRotation));
		if((getRotation >= 0) && (getRotation <= 3)) {
		    SettingsVP.setVideoRotateAngle(angle_table[getRotation]);
			mLastRotation = getRotation;
		}
//        StorageManager m_storagemgr = (StorageManager) getSystemService(Context.STORAGE_SERVICE);
//        m_storagemgr.registerListener(mListener);

        // install an intent filter to receive SD card related events.
        IntentFilter intentFilter =
                new IntentFilter(Intent.ACTION_MEDIA_MOUNTED);
        intentFilter.addAction(Intent.ACTION_MEDIA_EJECT);
        intentFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
        intentFilter.addDataScheme("file");
        registerReceiver(mMountReceiver, intentFilter);
        
    }
	
	@Override
	public void onConfigurationChanged(Configuration config) {
		try {
			super.onConfigurationChanged(config);
			
			//int getRotation = mWindowManager.getDefaultDisplay().getRotation();
			//Log.d("sensor", "rotate angle: "+Integer.toString(getRotation));
			//if((getRotation < 0) || (getRotation > 3))
			//	return;
			//SettingsVP.setVideoRotateAngle(angle_table[getRotation]);
			
			if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
				//setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			} 
			else if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
				//setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			}
		} 
		catch (Exception ex) {
			
		}
	}

Handler mRotateHandler = new Handler() {
	public void handleMessage(Message msg) {	
		switch (msg.what) {
			case GETROTATION:
				int getRotation = mWindowManager.getDefaultDisplay().getRotation();
			//	Log.d("sensor", "rotate angle: "+Integer.toString(getRotation));
				if((getRotation >= 0) && (getRotation <= 3) && (getRotation != mLastRotation)) {
					SettingsVP.setVideoRotateAngle(angle_table[getRotation]);
					mLastRotation = getRotation;
				}
				mRotateHandler.sendEmptyMessageDelayed(GETROTATION, GETROTATION_TIMEOUT);
				break;
		} 
		super.handleMessage(msg); 
	}
};

	public void writeFile(String file, String value){
		File OutputFile = new File(file);
		if(!OutputFile.exists()) {        	
        	return;
        }

    	try {
			BufferedWriter out = new BufferedWriter(new FileWriter(OutputFile), 32);
    		try {
				Log.d(TAG, "---------------------------------------set" + file + ": " + value);
    			out.write(value);    
    		} 
			finally {
				out.close();
			}
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			Log.e(TAG, "IOException when write "+OutputFile);
		}
	}

	//tony.wang 20120525 add for flash while osd switching
	private static final int OSD_INFO_BAR=0;
	private static final int OSD_MORE_BAR=1;
	private int curOsdViewFlag=-1;
	private int firstInvokFlag=1;//tony.wang 20120601 add for box shield scale interface on start of playing
	private boolean isKeyOrTouchDown=false;

	private void setOnTouchHoldShow(ImageButton ib)
	{
		ib.setOnTouchListener(new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction() == MotionEvent.ACTION_DOWN)
				{
					isKeyOrTouchDown=true;
				}
				else if(event.getAction() == MotionEvent.ACTION_UP)
				{
					isKeyOrTouchDown=false;
					waitForHideOsd();
				}
				return false;
			}
		});
	}
	
	protected void waitForHideOsd() {
    	final Handler handler = new Handler(){   
    		  
            public void handleMessage(Message msg) {   
                switch (msg.what) {       
                case 0x4c: 
					if(false==isKeyOrTouchDown)
            		showNoOsdView();
                    break;       
                }       
                super.handleMessage(msg);   
            }
               
        };   
        TimerTask task = new TimerTask(){   
      
            public void run() {   
                if(!touchVolFlag) {
                    Message message = Message.obtain();
                    message.what = 0x4c;       
                    handler.sendMessage(message);     
                }   
            }
        };   
        
        timer.cancel();
        timer = new Timer();
    	timer.schedule(task, 5000);
    }

	private void initOsdBar()
	{
		initinfobar();
		videobar();
		showInfoBar();
	}

	private int getCurOsdViewFlag()
	{
		return curOsdViewFlag;
	}

	private void setCurOsdViewFlag(int osdView)
	{
		curOsdViewFlag = osdView;
	}

	private void showInfoBar()
	{
		if ((null!=infobar)&&(View.GONE==infobar.getVisibility()))
		{
			getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

			if ((null!=morbar)&&(View.VISIBLE==morbar.getVisibility()))
				morbar.setVisibility(View.GONE);
			if ((null!=subbar)&&(View.VISIBLE==subbar.getVisibility()))
				subbar.setVisibility(View.GONE);
			if ((null!=otherbar)&&(View.VISIBLE==otherbar.getVisibility()))
				otherbar.setVisibility(View.GONE);
			if ((null!=infodialog)&&(View.VISIBLE==infodialog.getVisibility()))
				infodialog.setVisibility(View.GONE);
			
			infobar.setVisibility(View.VISIBLE);
			infobar.requestFocus();
			setCurOsdViewFlag(OSD_INFO_BAR);
			Log.i("wxl","firstInvokFlag:"+firstInvokFlag);
			if(1==firstInvokFlag)
			{
				firstInvokFlag--;
			}
			else
			{
				setOSDOnOff(true);
			}
			//waitForHide();
			waitForHideOsd();
        }
	}

	private void showMorBar()
	{
		if ((null!=morbar)&&(View.GONE==morbar.getVisibility()))
		{
			getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
			
			if ((null!=infobar)&&(View.VISIBLE==infobar.getVisibility()))
				infobar.setVisibility(View.GONE);
			if ((null!=subbar)&&(View.VISIBLE==subbar.getVisibility()))
				subbar.setVisibility(View.GONE);
			if ((null!=otherbar)&&(View.VISIBLE==otherbar.getVisibility()))
				otherbar.setVisibility(View.GONE);
			if ((null!=infodialog)&&(View.VISIBLE==infodialog.getVisibility()))
				infodialog.setVisibility(View.GONE);
			
			morbar.setVisibility(View.VISIBLE);
	        morbar.requestFocus();
			setCurOsdViewFlag(OSD_MORE_BAR);
			setOSDOnOff(true);
			//waitForHideVideoBar();
			waitForHideOsd();
        }
	}

	private void showNoOsdView()
	{
		if(subTitleView!=null)
			subTitleView.redraw();
		if(subTitleView_sm!=null&&SystemProperties.getBoolean("3D_setting.enable", false)){
		     subTitleView_sm.redraw();
		}	

		if ((null!=infobar)&&(View.VISIBLE==infobar.getVisibility()))
			infobar.setVisibility(View.GONE);
		if ((null!=morbar)&&(View.VISIBLE==morbar.getVisibility()))
			morbar.setVisibility(View.GONE);
		if ((null!=subbar)&&(View.VISIBLE==subbar.getVisibility()))
			subbar.setVisibility(View.GONE);
		if ((null!=otherbar)&&(View.VISIBLE==otherbar.getVisibility()))
			otherbar.setVisibility(View.GONE);
		if ((null!=infodialog)&&(View.VISIBLE==infodialog.getVisibility()))
			infodialog.setVisibility(View.GONE);

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,   
    			WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setOSDOnOff(false);
	}

	private void showOsdView()
	{
		if(null==infobar)
			return;
		if(null==morbar)
			return;

		int flag = getCurOsdViewFlag();
		switch(flag)
		{
			case OSD_INFO_BAR:
			{
				showInfoBar();
			}
			break;

			case OSD_MORE_BAR:
			{
				showMorBar();
			}
			break;

			default:
			{
				Log.e(TAG,"[showOsdView]getCurOsdView error flag:"+flag+",set CurOsdView default");
				showInfoBar();
			}
			break;
		}
	}

	private void switchOsdView()
	{
		if(null==infobar)
			return;
		if(null==morbar)
			return;
		
		int flag = getCurOsdViewFlag();
		switch(flag)
		{
			case OSD_INFO_BAR:
			{
				showMorBar();
			}
			break;

			case OSD_MORE_BAR:
			{
				showInfoBar();
			}
			break;

			default:
			{
				Log.e(TAG,"[switchOsdView]getCurOsdView error flag:"+flag+",set CurOsdView default");
				showInfoBar();
			}
			break;
		}
	}

	private void setSubtitleView()
	{
		subTitleView = (SubtitleView) findViewById(R.id.subTitle);
		subTitleView.clear();
		subTitleView.setGravity(Gravity.CENTER);
		subTitleView.setTextColor(sub_para.color);
		subTitleView.setTextSize(sub_para.font);
		subTitleView.setTextStyle(Typeface.BOLD);
		subTitleView.setPadding(
			subTitleView.getPaddingLeft(),
			subTitleView.getPaddingTop(),
			subTitleView.getPaddingRight(),
			getWindowManager().getDefaultDisplay().getRawHeight()*sub_para.position_v/20+10);
	}

	private void initSubSetOptions(String color_text[])
	{
		t_subswitch =(TextView)findViewById(R.id.sub_swith111);
		t_subsfont =(TextView)findViewById(R.id.sub_font111);
		t_subscolor =(TextView)findViewById(R.id.sub_color111);
		t_subsposition_v =(TextView)findViewById(R.id.sub_position_v111);
		
		sub_switch_state = sub_para.curid;
		sub_font_state = sub_para.font;
		sub_position_v_state = sub_para.position_v;
		
		if(sub_para.color==android.graphics.Color.WHITE)
			sub_color_state =0;
		else if(sub_para.color==android.graphics.Color.YELLOW)
			sub_color_state =1;
		else
			sub_color_state =2;
		
		
		//modify by jeff.yang
		if(sub_para.curid==sub_para.totalnum||sub_para.enable==false)
		{
			sub_para.curid=sub_para.totalnum;
			t_subswitch.setText(R.string.str_off);
		}
		else
			t_subswitch.setText(String.valueOf(sub_para.curid+1)+"/"+String.valueOf(sub_para.totalnum));
		
		t_subsfont.setText(String.valueOf(sub_font_state));
		t_subscolor.setText(color_text[sub_color_state]);
		t_subsposition_v.setText(String.valueOf(sub_position_v_state));
		
	
		Button ok = (Button) findViewById(R.id.button_ok);
		Button cancel = (Button) findViewById(R.id.button_canncel);
		ImageButton Bswitch_l = (ImageButton) findViewById(R.id.switch_l);	
		ImageButton Bswitch_r = (ImageButton) findViewById(R.id.switch_r);
		ImageButton Bfont_l = (ImageButton) findViewById(R.id.font_l);	
		ImageButton Bfont_r = (ImageButton) findViewById(R.id.font_r);
		ImageButton Bcolor_l = (ImageButton) findViewById(R.id.color_l);	
		ImageButton Bcolor_r = (ImageButton) findViewById(R.id.color_r);
		ImageButton Bposition_v_l = (ImageButton) findViewById(R.id.position_v_l);	
		ImageButton Bposition_v_r = (ImageButton) findViewById(R.id.position_v_r);
		TextView font =(TextView)findViewById(R.id.font_title);
		TextView color =(TextView)findViewById(R.id.color_title);
		TextView position_v =(TextView)findViewById(R.id.position_v_title);

		font.setTextColor(android.graphics.Color.BLACK);
		color.setTextColor(android.graphics.Color.BLACK);
		position_v.setTextColor(android.graphics.Color.BLACK);
		
		t_subsfont.setTextColor(android.graphics.Color.BLACK);
		t_subscolor.setTextColor(android.graphics.Color.BLACK);
		t_subsposition_v.setTextColor(android.graphics.Color.BLACK);	
			
	    Bfont_l.setEnabled(true);
		Bfont_r.setEnabled(true);
		Bcolor_l.setEnabled(true);
		Bcolor_r.setEnabled(true);
		Bposition_v_l.setEnabled(true);
		Bposition_v_r.setEnabled(true);
		Bfont_l.setImageResource(R.drawable.fondsetup_larrow_unfocus);
		Bfont_r.setImageResource(R.drawable.fondsetup_rarrow_unfocus);
		Bcolor_l.setImageResource(R.drawable.fondsetup_larrow_unfocus);
		Bcolor_r.setImageResource(R.drawable.fondsetup_rarrow_unfocus);
		Bposition_v_l.setImageResource(R.drawable.fondsetup_larrow_unfocus);
		Bposition_v_r.setImageResource(R.drawable.fondsetup_rarrow_unfocus);

		Bswitch_l.setNextFocusUpId(R.id.switch_l);
		Bswitch_l.setNextFocusDownId(R.id.font_l);
		Bswitch_l.setNextFocusLeftId(R.id.switch_l);
		Bswitch_l.setNextFocusRightId(R.id.switch_r);
		
		Bswitch_r.setNextFocusUpId(R.id.switch_r);
		Bswitch_r.setNextFocusDownId(R.id.font_r);
		Bswitch_r.setNextFocusLeftId(R.id.switch_l);
		Bswitch_r.setNextFocusRightId(R.id.switch_r);

		Bfont_l.setNextFocusUpId(R.id.switch_l);
		Bfont_l.setNextFocusDownId(R.id.color_l);
		Bfont_l.setNextFocusLeftId(R.id.font_l);
		Bfont_l.setNextFocusRightId(R.id.font_r);

		Bfont_r.setNextFocusUpId(R.id.switch_r);
		Bfont_r.setNextFocusDownId(R.id.color_r);
		Bfont_r.setNextFocusLeftId(R.id.font_l);
		Bfont_r.setNextFocusRightId(R.id.font_r);

		Bcolor_l.setNextFocusUpId(R.id.font_l);
		Bcolor_l.setNextFocusDownId(R.id.position_v_l);
		Bcolor_l.setNextFocusLeftId(R.id.color_l);
		Bcolor_l.setNextFocusRightId(R.id.color_r);

		Bcolor_r.setNextFocusUpId(R.id.font_r);
		Bcolor_r.setNextFocusDownId(R.id.position_v_r);
		Bcolor_r.setNextFocusLeftId(R.id.color_l);
		Bcolor_r.setNextFocusRightId(R.id.color_r);
		
		Bposition_v_l.setNextFocusUpId(R.id.color_l);
		Bposition_v_l.setNextFocusDownId(R.id.button_ok);
		Bposition_v_l.setNextFocusLeftId(R.id.position_v_l);
		Bposition_v_l.setNextFocusRightId(R.id.position_v_r);

		Bposition_v_r.setNextFocusUpId(R.id.color_r);
		Bposition_v_r.setNextFocusDownId(R.id.button_canncel);
		Bposition_v_r.setNextFocusLeftId(R.id.position_v_l);
		Bposition_v_r.setNextFocusRightId(R.id.position_v_r);

		cancel.setNextFocusUpId(R.id.position_v_r);
		cancel.setNextFocusDownId(R.id.button_canncel);
		cancel.setNextFocusLeftId(R.id.button_ok);
		cancel.setNextFocusRightId(R.id.button_canncel);

		ok.setNextFocusUpId(R.id.position_v_l);
		ok.setNextFocusDownId(R.id.button_ok);
		ok.setNextFocusLeftId(R.id.button_ok);
		ok.setNextFocusRightId(R.id.button_canncel);
	}

	private void disableSubSetOptions()
	{
		Button ok = (Button) findViewById(R.id.button_ok);
		Button cancel = (Button) findViewById(R.id.button_canncel);
		ImageButton Bswitch_l = (ImageButton) findViewById(R.id.switch_l);	
		ImageButton Bswitch_r = (ImageButton) findViewById(R.id.switch_r);
		ImageButton Bfont_l = (ImageButton) findViewById(R.id.font_l);	
		ImageButton Bfont_r = (ImageButton) findViewById(R.id.font_r);
		ImageButton Bcolor_l = (ImageButton) findViewById(R.id.color_l);	
		ImageButton Bcolor_r = (ImageButton) findViewById(R.id.color_r);
		ImageButton Bposition_v_l = (ImageButton) findViewById(R.id.position_v_l);	
		ImageButton Bposition_v_r = (ImageButton) findViewById(R.id.position_v_r);
		TextView font =(TextView)findViewById(R.id.font_title);
		TextView color =(TextView)findViewById(R.id.color_title);
		TextView position_v =(TextView)findViewById(R.id.position_v_title);
			
		font.setTextColor(android.graphics.Color.LTGRAY);
		color.setTextColor(android.graphics.Color.LTGRAY);
		position_v.setTextColor(android.graphics.Color.LTGRAY);
		
		t_subsfont.setTextColor(android.graphics.Color.LTGRAY);
		t_subscolor.setTextColor(android.graphics.Color.LTGRAY);
		t_subsposition_v.setTextColor(android.graphics.Color.LTGRAY);	
			
	    Bfont_l.setEnabled(false);
		Bfont_r.setEnabled(false);
		Bcolor_l.setEnabled(false);
		Bcolor_r.setEnabled(false);
		Bposition_v_l.setEnabled(false);
		Bposition_v_r.setEnabled(false);
		Bfont_l.setImageResource(R.drawable.fondsetup_larrow_disable);
		Bfont_r.setImageResource(R.drawable.fondsetup_rarrow_disable);
		Bcolor_l.setImageResource(R.drawable.fondsetup_larrow_disable);
		Bcolor_r.setImageResource(R.drawable.fondsetup_rarrow_disable);
		Bposition_v_l.setImageResource(R.drawable.fondsetup_larrow_disable);
		Bposition_v_r.setImageResource(R.drawable.fondsetup_rarrow_disable);
		
		Bswitch_l.setNextFocusUpId(R.id.switch_l);
		Bswitch_l.setNextFocusDownId(R.id.button_ok);
		Bswitch_l.setNextFocusLeftId(R.id.switch_l);
		Bswitch_l.setNextFocusRightId(R.id.switch_r);
	
		Bswitch_r.setNextFocusUpId(R.id.switch_r);
		Bswitch_r.setNextFocusDownId(R.id.button_canncel);
		Bswitch_r.setNextFocusLeftId(R.id.switch_l);
		Bswitch_r.setNextFocusRightId(R.id.switch_r);
		
		ok.setNextFocusUpId(R.id.switch_l);
		ok.setNextFocusDownId(R.id.button_ok);
		ok.setNextFocusLeftId(R.id.button_ok);
		ok.setNextFocusRightId(R.id.button_canncel);

		cancel.setNextFocusUpId(R.id.switch_r);
		cancel.setNextFocusDownId(R.id.button_canncel);
		cancel.setNextFocusLeftId(R.id.button_ok);
		cancel.setNextFocusRightId(R.id.button_canncel);
	}
}

class subview_set{
	public int totalnum; 
	public int curid;
	public int color;
	public int font; 
	public SubID sub_id;
	public boolean enable;
	public int position_v;
}
