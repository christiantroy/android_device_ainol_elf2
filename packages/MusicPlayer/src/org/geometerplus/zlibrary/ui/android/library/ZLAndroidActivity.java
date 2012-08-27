/*
 * Copyright (C) 2007-2010 Geometer Plus <contact@geometerplus.com>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 */

package org.geometerplus.zlibrary.ui.android.library;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.geometerplus.zlibrary.core.application.ZLApplication;
import org.geometerplus.zlibrary.core.library.ZLibrary;
import org.geometerplus.zlibrary.ui.android.R;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.AbsoluteLayout;

import com.amlogic.pmt.DevStatusDisplay;
import com.amlogic.pmt.RelevanceOp;
import com.amlogic.pmt.RenderView;
import com.amlogic.pmt.Resolution;
import com.amlogic.pmt.Waiting;
import com.amlogic.pmt.menu.MenuOp;
//import android.os.SystemProperties;//gyx comment

import com.amlogic.pmt.GLMusicService;
import android.content.ServiceConnection;
import android.content.ComponentName;
import android.os.IBinder;
import com.amlogic.pmt.MusicPlayer;

import com.amlogic.pmt.IGLMusicService;
import android.os.RemoteException;
import android.net.Uri;

public abstract class ZLAndroidActivity extends Activity {
	protected static final String TAG = "zlactivity";
	public  abstract ZLApplication createApplication(String fileName);

//	private static final String REQUESTED_ORIENTATION_KEY = "org.geometerplus.zlibrary.ui.android.library.androidActiviy.RequestedOrientation";
//	private static final String ORIENTATION_CHANGE_COUNTER_KEY = "org.geometerplus.zlibrary.ui.android.library.androidActiviy.ChangeCounter";
	public RenderView mGLRenderView = null;
	private String filetype =  null ;
	private RelevanceOp relevanceOp = null;
	private MenuOp menuOp = null;
	public AbsoluteLayout layout;
	protected DevStatusDisplay  devdisplay;
	private int MouseX=0;
	private int MouseY=0;
	public IGLMusicService mService=null;
//	public int[] resolution = new int[]/*{1920,1080};//*/{1280,720};

	private String mPlayMode = null;
	private String mPath= null;

	public static boolean exit_flag = false;
	//Override
	protected void onSaveInstanceState(Bundle state) {
		super.onSaveInstanceState(state);
//		state.putInt(REQUESTED_ORIENTATION_KEY, myOrientation);
//		state.putInt(ORIENTATION_CHANGE_COUNTER_KEY, myChangeCounter);
	}

	public abstract String fileNameForEmptyUri();

	/*private String fileNameFromUri(Uri uri) {
		if (uri.equals(Uri.parse("file:///"))) {
			return fileNameForEmptyUri();
		} else {
			return uri.getPath();
		}
	}*/

	//Override
	public void onCreate(Bundle state) {
		Log.v("Txtplayer", "Create");
		super.onCreate(state);
//		BitmapFactory.setDefaultConfig(Bitmap.Config.ARGB_8888);//gyx comment
//		Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler(this));

//		if (state != null) {
//			myOrientation = state.getInt(REQUESTED_ORIENTATION_KEY, ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
//			myChangeCounter = state.getInt(ORIENTATION_CHANGE_COUNTER_KEY);
//		}
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		exit_flag = false;
	        
		/*try {
			final WindowManager.LayoutParams attrs = getWindow().getAttributes();
			final Class<?> cls = attrs.getClass();
			final Field fld = cls.getField("buttonBrightness");
			if (fld != null && "float".equals(fld.getType().toString())) {
				fld.setFloat(attrs, 0);
			}
		} catch (NoSuchFieldException e) {
		} catch (IllegalAccessException e) {
		}*/
	      
		//Set the current resolution
		DisplayMetrics dm =new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        Resolution.setResolution(dm.widthPixels, dm.heightPixels) ;
        Log.d(TAG, "WIDTH "+Resolution.getWidth()+", HEIGHT"+Resolution.getHeight());
        
	    layout = new AbsoluteLayout(this);
	    LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    View view = inflater.inflate(R.layout.main,null);
//	    devdisplay = new DevStatusDisplay(this, DevStatusDisplay.STS_NET
//				| DevStatusDisplay.STS_USB);
//	    devdisplay.setVisibility(View.INVISIBLE);


//	    layout.addView(view, 1920, 1080);
	    LayoutInflater inflater1 = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    //
	    int resltion_width=Resolution.getWidth();
	  /*  int resource=0;
	    if(Resolution.getWidth()>1280)
	    {
	    	resource = R.layout.waiting1920x1080;
	    }
	    else
	    {
	    	resource = R.layout.waiting1280x720;
	    }
	    */
	    //if(Resolution.getWidth()==1280&&Resolution.getHeight()==720)resource = R.layout.waiting1280x720;
	    
	    //View view1 =inflater1.inflate(resource,null);	
	    layout.addView(view, Resolution.getWidth(), Resolution.getHeight());
	    //layout.addView(view1, Resolution.getWidth(), Resolution.getHeight());
//	    layout.addView(devdisplay);
	    
	    
		setContentView(layout);
//		setDefaultKeyMode(DEFAULT_KEYS_SEARCH_LOCAL);
		getLibrary().setActivity(this);
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			filetype = bundle.getString("file_type");
		}
		else{
			filetype = "Audio";
//			filetype = "Picture";Audio;Text
		}	
		mGLRenderView = (RenderView)view.findViewById(R.id.glsurfaceview);
		//gyx add
		mGLRenderView.setOnTouchListener(new OnTouchListener(){

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				Log.d(TAG,"ontouch action:"+event.getAction()+","+event.getRawX()+","+event.getRawY());
				MouseX=(int) event.getRawX();
				MouseY=(int) event.getRawY();
				return false;
			}
			
		});
		mGLRenderView.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.d(TAG,"onclick");
				if(mGLRenderView.GetGridLayoutInstance() !=null)
					{
					Log.d(TAG,"---------------call gridlayout click");
					mGLRenderView.GetGridLayoutInstance().MouseClick(v,MouseX,MouseY);
					}	
				else if(mGLRenderView.GetMusicLayoutInstance()!= null)
					{
					if(menuOp.getMenuInstance() !=null && menuOp.getMenuInstance().getVisibility() == View.VISIBLE)
						{
						Log.d(TAG,"---------------getMenuInstance is exist");
						if(menuOp.getMenuInstance().getMenuIns()!= null)
							{
							int focus =menuOp.getMenuInstance().getMenuIns().mouseClick(MouseX,MouseY);
							if(focus>=0)
								{
								menuOp.getMenuInstance().handleOnClick(focus);
								menuOp.getMenuInstance().requestFocus();
								if(relevanceOp.menuStopFlag){
									menuOp.DestoryMenu();
									relevanceOp.menuStopFlag = false;
									}
								}
							else
								{
								double process =menuOp.getMenuInstance().getMenuIns().getPlayProcess(MouseX,MouseY);
								if(process>0)
									{
									mGLRenderView.GetMusicLayoutInstance().setProcess(process);
									}
								}
							}
						}
					else
						{
						showmenu();
						}
					}
 			}
			
		});
		
		
	    menuOp = new MenuOp(this);
	    menuOp.setRenderView(mGLRenderView);	
	    
	    relevanceOp = new RelevanceOp(this);
		relevanceOp.setRenderView(mGLRenderView);	
		relevanceOp.setMenuOpInstance(menuOp);


		Intent intent = new Intent("com.amlogic.pmt.GLMusicService");

		startService(intent);		
		bindService(intent ,sc,0);
	}
	
	private ServiceConnection sc = new ServiceConnection() {
		@Override 
		public void onServiceDisconnected(ComponentName name)
		{	
		mService = null;  
		Log.d(TAG, "in onServiceDisconnected");	
		}  
		@Override 
		public void onServiceConnected(ComponentName name, IBinder service) {  
		//mService = ((GLMusicService.MyBinder)(service)).getService(); 
		mService = IGLMusicService.Stub.asInterface(service);
		MusicPlayer.setMediaPlayerService(mService);	
		Log.d(TAG, "in onServiceConnected");

		if((mPlayMode != null)&&(mPlayMode.equals("PlayAMusic")))
			{
			relevanceOp.PlayFile(filetype,mPath);
			mPlayMode = " ";
			}
		}  
   };  
	
	//Override
	public void onStart() {
		super.onStart();
//		if (ZLAndroidApplication.Instance().AutoOrientationOption.getValue()) {
//			setAutoRotationMode();
//		} else {
//			switch (myOrientation) {
//				case ActivityInfo.SCREEN_ORIENTATION_PORTRAIT:
//				case ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE:
//					if (getRequestedOrientation() != myOrientation) {
//						setRequestedOrientation(myOrientation);
//						myChangeCounter = 0;
//					}
//					break;
//				default:
//					setAutoRotationMode();
//					break;
//			}
//		}
		
	}

//	private PowerManager.WakeLock myWakeLock;
//	private boolean myWakeLockToCreate;
//	private boolean myStartTimer;

	/*public final void createWakeLock() {
		if (myWakeLockToCreate) {
			synchronized (this) {
				if (myWakeLockToCreate) {
					myWakeLockToCreate = false;
					myWakeLock =
						((PowerManager)getSystemService(POWER_SERVICE)).
							newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "FBReader");
					myWakeLock.acquire();
				}
			}
		}
		if (myStartTimer) {
			ZLApplication.Instance().startTimer();
			myStartTimer = false;
		}
	}*/

	/*private final void switchWakeLock(boolean on) {
		if (on) {
			if (myWakeLock == null) {
				myWakeLockToCreate = true;
			}
		} else {
			if (myWakeLock != null) {
				synchronized (this) {
					if (myWakeLock != null) {
						myWakeLock.release();
						myWakeLock = null;
					}
				}
			}
		}
	}*/

	//Override
	public void onResume() {
		super.onResume();
		if(mGLRenderView.GetMusicLayoutInstance()!= null||mGLRenderView.GetPictureLayoutInstance()!= null)
			{
			return;
			}
//		switchWakeLock(
//			ZLAndroidApplication.Instance().BatteryLevelToTurnScreenOffOption.getValue() <
//			ZLApplication.Instance().getBatteryLevel()
//		);
//		myStartTimer = true;
		getDevice();

		IntentFilter Filter =new IntentFilter("state_chage");
		registerReceiver(mReceiver, Filter);
		
		
		IntentFilter intentFilter = new IntentFilter(Intent.ACTION_MEDIA_MOUNTED);
		 intentFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
	        intentFilter.addAction(Intent.ACTION_MEDIA_REMOVED);
	        intentFilter.addAction(Intent.ACTION_MEDIA_SHARED);
	        intentFilter.addAction(Intent.ACTION_MEDIA_BAD_REMOVAL);
	        intentFilter.addDataScheme("file");
	        registerReceiver(broadcastReceiver , intentFilter);
	        register_ad_broadcast();


			Intent intent = getIntent();
			String action = intent.getAction();
			Log.e(TAG,"-------action is"+action);
			if (Intent.ACTION_VIEW.equalsIgnoreCase(action)){
				
				Uri uri = intent.getData();
				if(uri!=null)
					{
					mPlayMode = "PlayAMusic";
					mPath = uri.getPath();
					//relevanceOp.PlayFile(filetype,uri.getPath());
					return ;
					}
			}
			
	    	relevanceOp.PlayBrowser(filetype);
	}

	//Override
	public void onPause() {
		Log.v("TxtAndriodplayer", "onPause");
//		unregisterReceiver(myBatteryInfoReceiver);
//		ZLApplication.Instance().stopTimer();
//		switchWakeLock(false);
//		ZLApplication.Instance().onWindowClosing();
		super.onPause();
	}

	/*//Override
	public void onNewIntent(Intent intent) {
		super.onNewIntent(intent);

		String fileToOpen = null;
		if (Intent.ACTION_VIEW.equals(intent.getAction())) {
			final Uri uri = intent.getData();
			if (uri != null) {
				fileToOpen = fileNameFromUri(uri);
			}
			intent.setData(null);
		}

		if (fileToOpen != null) {
			ZLApplication.Instance().openFile(ZLFile.createFileByPath(fileToOpen));
		}
		ZLApplication.Instance().repaintView();
	}*/

	private static ZLAndroidLibrary getLibrary() {
		return (ZLAndroidLibrary)ZLibrary.Instance();
	}

//	//Override
//	public boolean onCreateOptionsMenu(final Menu menu) {
//		super.onCreateOptionsMenu(menu);
////		((ZLAndroidApplication)getApplication()).myMainWindow.buildMenu(menu);
//		return true;
//	}


	//Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		View view = findViewById(R.id.main_view);
//		return ((view != null) && view.onKeyDown(keyCode, event)) || super.onKeyDown(keyCode, event);

//		Log.e("mBook","back " + keyCode);
		Log.d(TAG,"down:"+keyCode);
		if(!exit_flag)
			return false;
		if(keyCode==113){
			if(mGLRenderView.GetMusicLayoutInstance()!= null){			
				mGLRenderView.GetMusicLayoutInstance().pauseSong();
			}
			finish();
			return super.onKeyDown(keyCode, event);
		}
		if(PMTSpecialKeyDown(keyCode,event) == true)
			return true;
		if(PMTKeyDown(keyCode,event) == true)
			return true;
		Log.d(TAG,"onKeyDown");
		return super.onKeyDown(keyCode, event);
	}
	private boolean PMTKeyDown(int keyCode, KeyEvent event)
	{
		switch (keyCode) {
		case 0xCF:
		case 0xCE:
		case 0xCD:
		case 0xCC:
		case 0xCB:
		case 0xCA:
		case 0xC9:
		case 0xC8:
		case 0xC7:
		case 0xC6:
		case 0xC5:
		case 0xC4:
		case 0xC3:
		case 0xC2:
		case 0xC1:
			PMTKeyDown(KeyEvent.KEYCODE_DPAD_LEFT,event);
			break;
		case 0xFF:
		case 0xFE:
		case 0xFD:
		case 0xFC:
		case 0xFB:
		case 0xFA:
		case 0xF9:
		case 0xF8:
		case 0xF7:
		case 0xF6:
		case 0xF5:
		case 0xF4:
		case 0xF3:
		case 0xF2:
		case 0xF1:
			PMTKeyDown(KeyEvent.KEYCODE_DPAD_RIGHT,event);
			break;
		}				
		// the menu  contain volume , so show it.
		Log.d(TAG,"-------------------------4");
 		if(menuOp.getMenuInstance() != null && 
				menuOp.getMenuInstance().getVisibility() == View.VISIBLE)
		{
 			if((keyCode==25)||(keyCode==24))
			{
				return false;
			}
			postClearOsdMessage();
 			//if(menuOp.getMenuInstance().getMenuIns().isFocused()){
 				if(relevanceOp.menuStopFlag){
 					menuOp.DestoryMenu();
 					relevanceOp.menuStopFlag = false;
// 					String type = relevanceOp.getMenuType();
// 					if(type.equals("Music"))
// 						menuOp.ShowMenu(type);
 				}else if(keyCode == KeyEvent.KEYCODE_MENU || keyCode == KeyEvent.KEYCODE_BACK){
// 					String type = relevanceOp.getMenuType();
// 					if(!type.equals("Music"))
 					menuOp.HideMenu();
 				}
			//}
 			
 			if(mGLRenderView.GetMusicLayoutInstance()!= null){
 				if(keyCode == 90 || keyCode == 89 ||keyCode == 92 || keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_DPAD_CENTER){
					Log.d(TAG,"-------------------------3");
 					if(mGLRenderView.GetMusicLayoutInstance().audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)==0){
 						handlerClearOsd.removeMessages(1); 
 					}
 				}
 			}
 			return true;	
		}
 		else
 		{
 			if(keyCode==KeyEvent.KEYCODE_DPAD_LEFT||keyCode==KeyEvent.KEYCODE_DPAD_RIGHT)
 				if( mGLRenderView.GetGridLayoutInstance() == null)
 				{
 					if(menuOp.getMenuInstance() == null)
 	 				{
 	 					showmenu();
 	 					if(menuOp.getMenuInstance() != null)
 	 						menuOp.getMenuInstance().setVisibility(View.INVISIBLE);
 	 				}
 					if(menuOp.getMenuInstance().getVisibility() != View.VISIBLE)
 					{
 						String data = menuOp.getMenuInstance().getPlayType() ;
 						if(data.equals("music"))
 						{
 							menuOp.getMenuInstance().ShowVolumeBarShortCut();
 							postClearOsdMessage();
 							return true;
 						}
 	 				}
 				}
 		}
			
		
		if(keyCode == KeyEvent.KEYCODE_MENU)
		{
			showmenu();
		}
    	if(mGLRenderView.GetGridLayoutInstance() != null)
    	{
    		Log.d(TAG,"-------------GetGridLayoutInstance");
    		if(mGLRenderView.GetGridLayoutInstance().onKeyDown(keyCode, event) == true){ 
    			return true;
    		}
    		else
    			if(keyCode == KeyEvent.KEYCODE_BACK){
    				Log.d(TAG,"-------------KEYCODE_BACK");
    				if(mGLRenderView.GetMusicLayoutInstance()!= null)
    				{ // gridlayout and musiclayout are both exist. in the case of choose music or txt when play music
    					Log.d(TAG,"---------------stopAutoPlay");
    					mGLRenderView.GetMusicLayoutInstance().stopAutoPlay();
    					mGLRenderView.uninitMusicLayout();
    				}
    				finish();
    				/*Intent intent = new Intent();
    				intent .setComponent(new ComponentName("com.android.launcher", "com.android.launcher2.Launcher"));
					startActivity(intent);*/
    				return true;
    			}
    	}
    	if(mGLRenderView.GetTxtLayoutInstance() != null)
    	{
    		if(mGLRenderView.GetTxtLayoutInstance().onKeyDown(keyCode, event) == true)
    			return true;
    	}
		if(mGLRenderView.GetPictureLayoutInstance() != null)
		{
			
			if(mGLRenderView.GetPictureLayoutInstance().onKeyDown(keyCode, event) == true)
    			return true;
		}
		if(mGLRenderView.GetMusicLayoutInstance()!= null)
		{
			Log.d(TAG,"-------------GetMusicLayoutInstance");
			if(mGLRenderView.GetMusicLayoutInstance().onKeyDown(keyCode, event) == true)
    			return true;		
		}
		if(keyCode == KeyEvent.KEYCODE_BACK){
			relevanceOp.ReturnRelevance();
			return true;
		}
		
		if(keyCode == 90 || keyCode == 92){
			if(mGLRenderView.GetMusicLayoutInstance()!= null){
	 			if(mGLRenderView.GetMusicLayoutInstance().audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)==0)
	 				handlerClearOsd.removeMessages(1);
 			}
		}
		return false;
	}
	
	private boolean PMTSpecialKeyDown(int keyCode, KeyEvent event)
	{
		Log.d(TAG,"-------------2");
		if(mGLRenderView.GetGridLayoutInstance() != null)
			return  false;
		if(keyCode==KeyEvent.KEYCODE_ENTER|| keyCode == KeyEvent.KEYCODE_DPAD_CENTER)
		{
			if(menuOp.getMenuInstance() == null)
			{
				showmenu();
				if(menuOp.getMenuInstance() != null)
					menuOp.getMenuInstance().setVisibility(View.INVISIBLE);
			}
			if(menuOp.getMenuInstance() !=null && menuOp.getMenuInstance().getVisibility() == View.INVISIBLE)
			{
				menuOp.getMenuInstance().handleSpecialKey(85); //be same as pause/play key
				return true;
			}
		}
			
		if(keyCode==94||keyCode==95||keyCode==96||keyCode==100 ||keyCode==92
				||keyCode==85||keyCode==86||keyCode==90||keyCode==89||keyCode==87||keyCode==88
				||keyCode==114||keyCode==115  )
		{
			if(menuOp.getMenuInstance() == null)
			{
				showmenu();
				Log.d(TAG,"-------------showmenu");
				if(menuOp.getMenuInstance() != null)
					menuOp.getMenuInstance().setVisibility(View.INVISIBLE);
			}
		}
		switch(keyCode){
		case 94://IMAGE_MODE 图像模式
		   menuOp.getMenuInstance().SelectFrameShortCut("shortcut_setup_video_picture_mode_");
	       return true;
	    case 95://VOICE_MODE 声音模式
	       menuOp.getMenuInstance().SelectFrameShortCut("shortcut_setup_audio_sound_mode_");
	       return true;
	    case 96://DISP_MODE 显示模式
	       menuOp.getMenuInstance().SelectFrameShortCut("shortcut_setup_video_display_mode_");
	       return true;
	    case 100://SOURCE 通道选择
	       menuOp.getMenuInstance().SelectFrameShortCut("shortcut_common_source_");
	       return true;
					
		case 92:
		case 85:
		case 86:  //play_STOP
		case 89:
		case 90:
		case 87:
		case 88:
			Log.d(TAG,"-------------handleSpecialKey:"+keyCode);
			menuOp.getMenuInstance().handleSpecialKey(keyCode);
			menuOp.getMenuInstance().requestFocus();
			break;
		case 114:
	    case 115:
	    	menuOp.getMenuInstance().ShowVolumeBarShortCut();
	    	postClearOsdMessage();
	    	return true;
	   
	    	
	
		}
		// you press stop option,destory menu 
		if(keyCode == 86)  
		{
			menuOp.DestoryMenu();
			relevanceOp.menuStopFlag = false;
			return true;
		}
		return false;
	}
	public void showmenu(){
		if(mGLRenderView.GetGridLayoutInstance() == null)
    	{
			String type = relevanceOp.getMenuType();
			Log.d(TAG,"-------------showmenu func :"+type);
			if((!(type.equals(""))))     //if((!(type.equals(""))) &&(!type.equals("Music")))
			{
				menuOp.ShowMenu(type);	
				postClearOsdMessage();
				if(type.indexOf("T")!= -1)
				{
					relevanceOp.CallbackName("txt",relevanceOp.getCurTextName());
					relevanceOp.CallbackPosScale("txt","Nothing");
				}
				if(type.indexOf("P")!= -1)
				{
					relevanceOp.CallbackName("picture",relevanceOp.getCurPictureName());
					relevanceOp.CallbackPosScale("picture","Nothing");
				}
				if(type.indexOf("M")!= -1)
				{
					relevanceOp.CallbackName("music",relevanceOp.getCurMusicName());
//					relevanceOp.CallbackPosScale("music","Nothing");
				}
			}
    	}
	}
	
	  //Override
    public void onStop() 
    { 
    	Log.v("TxtAndriodplayer", "Stop");
        super.onStop(); 
		
		/*if(mGLRenderView.GetMusicLayoutInstance()!= null)
		{
			mGLRenderView.GetMusicLayoutInstance().stopAutoPlay();
		}*/
		//finish();
        
//        ZLAndroidWidget widget = ((ZLAndroidLibrary)ZLibrary.Instance()).getWidget();
//        widget.RecycleBitmap();     
    }
	//Override
    public void onDestroy() 
    { 
//        SystemProperties.set("media.amplayer.enable-local", "false");//gyx comment
    	Log.v("TxtAndriodplayer", "Destroy");
 
        super.onDestroy();
		unbindService(sc);
		mService=null;
		
        unregisterReceiver(ad_receiver);
        unregisterReceiver(broadcastReceiver);
		unregisterReceiver(mReceiver);
        mGLRenderView.uninitTxtLayout();
        mGLRenderView.uninitMusicLayout();
        mGLRenderView.uninitPictureLayout();
        mGLRenderView.uninitGridLayout();
        mGLRenderView.onStop();
        //killProcess();
    }
    private int killProcess() {
        // TODO Auto-generated method stub
        Log.d("Amlogic3DNetmovieBrowser", "killProcess");
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
        return 0;
    }

	private int myChangeCounter;
//	private int myOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;
	private void setAutoRotationMode() {
		/*
		final ZLAndroidApplication application = ZLAndroidApplication.Instance();
		myOrientation = application.AutoOrientationOption.getValue() ?
			ActivityInfo.SCREEN_ORIENTATION_SENSOR : ActivityInfo.SCREEN_ORIENTATION_NOSENSOR;
		setRequestedOrientation(myOrientation);
		myChangeCounter = 0;
		*/
	}

	//Override
	public void onConfigurationChanged(Configuration config) {
		super.onConfigurationChanged(config);

		switch (getRequestedOrientation()) {
			default:
				break;
			case ActivityInfo.SCREEN_ORIENTATION_PORTRAIT:
				if (config.orientation != Configuration.ORIENTATION_PORTRAIT) {
					myChangeCounter = 0;
				} else if (myChangeCounter++ > 0) {
					setAutoRotationMode();
				}
				break;
			case ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE:
				if (config.orientation != Configuration.ORIENTATION_LANDSCAPE) {
					myChangeCounter = 0;
				} else if (myChangeCounter++ > 0) {
					setAutoRotationMode();
				}
				break;
		}
	}

	void rotate() {
		/*View view = findViewById(R.id.main_view);
		if (view != null) {
			switch (getRequestedOrientation()) {
				case ActivityInfo.SCREEN_ORIENTATION_PORTRAIT:
					myOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
					break;
				case ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE:
					myOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
					break;
				default:
					if (view.getWidth() > view.getHeight()) {
						myOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
					} else {
						myOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
					}
			}
			setRequestedOrientation(myOrientation);
			myChangeCounter = 0;
		}*/
	}
	
	
    

	/*BroadcastReceiver myBatteryInfoReceiver = new BroadcastReceiver() {
		//Override
		public void onReceive(Context context, Intent intent) {
			final int level = intent.getIntExtra("level", 100);
			((ZLAndroidApplication)getApplication()).myMainWindow.setBatteryLevel(level);
			switchWakeLock(
				ZLAndroidApplication.Instance().BatteryLevelToTurnScreenOffOption.getValue() < level
			);
		}
	};*/

	public boolean getIsMusicLayoutExist(){
		try {
			return mService.getIsMusicLayoutExist();
		} catch (RemoteException ex) {
			return false;
		}
	}

	 private final BroadcastReceiver mReceiver = new BroadcastReceiver()
		{
			@Override
	        public void onReceive(Context context, Intent intent) {
	        if(getIsMusicLayoutExist())
	        	{
			        String action = intent.getAction();
					String req=intent.getStringExtra("PlayListener");
					if(req.equals("OnCompletion"))
						{
						mGLRenderView.GetMusicLayoutInstance().onCompletion();
						}
					else if(req.equals("OnError"))
						{
						mGLRenderView.GetMusicLayoutInstance().onError();
						}
	        	}
	        }
		};
	
	 private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver(){
			//@Override
	    	public void onReceive(Context context, Intent intent){
				String ReceiveName = intent.getData().toString().substring(7);
				/*if(ReceiveName.equals("/mnt/sdcard"))
					return;*/
	    		if(intent.getAction().equals("android.intent.action.MEDIA_MOUNTED"))
	    		{
	    				PostDelayMountMessage(ReceiveName);
	    		
	    			
	    		}
	    		else if(intent.getAction().equals("android.intent.action.MEDIA_REMOVED")
	                    ||intent.getAction().equals("android.intent.action.MEDIA_UNMOUNTED")
	                    ||intent.getAction().equals("android.intent.action.MEDIA_BAD_REMOVAL"))
	            {
	    			for(int i = 0;i<DeviceList.size();i++ )
	    				if(ReceiveName.equals(DeviceList.get(i)))
	    					relevanceOp.UnmountDevice(ReceiveName);		
	            }
	       }
	   };
	   List<String >  DeviceList = new  ArrayList<String >();
	   public void getDevice() {
			File[] files = new File("/mnt").listFiles();
			if (files != null) {
				for (File file : files) {
					if (file.getPath().startsWith("/mnt/sd") || file.getPath().equals("/mnt/sata")) {
							if (file != null) {
									DeviceList.add(file.getPath());
							}
					}
				}
			}
		}
	   	   
	   
		private Handler handlerClearOsd = new Handler() {
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 1:
					menuOp.HideMenu();
					break;
				}
			}
		};
		
		public void postClearOsdMessage() {
			handlerClearOsd.removeMessages(1);
			Message mage = handlerClearOsd.obtainMessage(1);
			handlerClearOsd.sendMessageDelayed(mage, 10000);
		}
	
		private Handler handlerDelayMount = new Handler() {
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 1:
					
					Bundle data = msg.getData();
					String ReceiveName = data.getString("name");
					relevanceOp.MountDevice(ReceiveName);
					DeviceList.add(ReceiveName);
					break;
				}
			}
		};
	
		public void PostDelayMountMessage(String name) {
			Message mage = handlerDelayMount.obtainMessage(1);
			Bundle data = new Bundle();
			data.putString("name", new String(name));
			mage.setData(data);
			handlerDelayMount.sendMessageDelayed(mage, 2000);
		}
		 
		 
		 
		 /**************** for amlogic advertise ****************/
			private static final String AD_UPDATE_BROAD = "com.amlogic.adservice.updatead";
			private static final String UPDATE_AD_IMGE = "update_ad_imge" ;
			private static final String UPDATE_AD_TEXT = "update_ad_text";
			private ADUpdateReceiver ad_receiver = null ;
			
		    private void register_ad_broadcast()
		    {
		    	ad_receiver = new ADUpdateReceiver();
		        IntentFilter filter = new IntentFilter();
		        filter.addAction(AD_UPDATE_BROAD);
		        this.registerReceiver(ad_receiver, filter);
		       
		    }
		   
		    private Bitmap getLoacalBitmap(String url)
		    {
		    	
		    	{
					File BitmapFile = new File( url );		
		    		FileInputStream fis;
					try {
						fis = new FileInputStream(BitmapFile);
						Bitmap btp =  BitmapFactory.decodeStream(fis);
			    		try {
							fis.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
			    		return btp;
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						return null;
					}
		    		
		    		
		    		
		    	}
		    }
		    
		    class ADUpdateReceiver extends BroadcastReceiver
		    {
				public void onReceive(Context context, Intent intent)
				{
					if(mGLRenderView.GetGridLayoutInstance() != null)
					{
						String action = intent.getAction();
						if (action.equals( AD_UPDATE_BROAD ))
						{
							Bundle bundle = intent.getExtras();
							if(bundle!=null)
							{
								String ad_imge_url = bundle.getString(UPDATE_AD_IMGE);
								if( null != ad_imge_url )
								{
								Log.d( TAG ,"the ad imge update url : " + ad_imge_url );
									Bitmap ad_bitmap = null ;
									if( ad_imge_url.startsWith( "/") )
										ad_bitmap = getLoacalBitmap( ad_imge_url );
									if( null != ad_bitmap )
									{
//										Log.d(TAG , "update ad poster : " + ad_imge_url ) ;
//										advert.setImageBitmap(ad_bitmap);
										if(mGLRenderView.GetGridLayoutInstance() != null)
											mGLRenderView.GetGridLayoutInstance().getADBitmap(ad_bitmap);
									}
								}					
								String ad_text = bundle.getString( UPDATE_AD_TEXT );
								if( null != ad_text )
								{
									Log.d( TAG ,"the ad text update text : " + ad_text );
								}				
							}
						}
					}
			
				}
		    }  
		    /**************** end amlogic advertise ****************/

}
