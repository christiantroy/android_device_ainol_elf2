package com.amlogic.pmt;

import android.app.Service;
import android.media.MediaPlayer;
import android.util.Log;
import android.content.Intent;
import android.os.IBinder;
import android.os.Binder;
import java.util.Timer;
import java.util.TimerTask;

import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import java.util.List;
import java.io.IOException;

import java.lang.ref.WeakReference;
import android.media.AudioManager;
import android.content.Context;
import android.content.ComponentName;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;

public class GLMusicService extends Service {
	
	public MediaPlayer mediaPlayer;  
	private static final String TAG = "GLMusicService";
	protected DataProvider dataProvider=null;
	//public final IBinder binder = new MyBinder(); 
	private final IBinder binder = new ServiceStub(this);
	private boolean isMusicLayoutExist=false;
	private String currentMusicPath="none";
	private int mServiceStartId = -1;
	
	private boolean mIsInitialized = false;
	private AudioManager mAudioManager;
    public static final String SERVICECMD = "com.android.pmt.musicservicecommand";
    public static final String CMDNAME = "command";
    public static final String CMDTOGGLEPAUSE = "togglepause";
    public static final String CMDSTOP = "stop";
    public static final String CMDPAUSE = "pause";
    public static final String CMDPREVIOUS = "previous";
    public static final String CMDNEXT = "next";

	public static final String TOGGLEPAUSE_ACTION = "com.android.music.musicservicecommand.togglepause";
    public static final String PAUSE_ACTION = "com.android.music.musicservicecommand.pause";
    public static final String PREVIOUS_ACTION = "com.android.music.musicservicecommand.previous";
    public static final String NEXT_ACTION = "com.android.music.musicservicecommand.next";

	/*public class MyBinder extends Binder{
		public GLMusicService getService(){
			return GLMusicService.this;

		}
	}*/
	
	static class ServiceStub extends IGLMusicService.Stub {
		WeakReference<GLMusicService> mService;
        
        ServiceStub(GLMusicService service) {
            mService = new WeakReference<GLMusicService>(service);
        }
		
		public boolean isPlaying() {
            return mService.get().isPlaying();
        }
        public void stop() {
            mService.get().stop();
        }
        public void pause() {
            mService.get().pause();
        }
        public void play() {
            mService.get().play();
        }
        public void prev() {
            mService.get().prev(true);
        }
        public void next() {
            mService.get().next(true);
        }

		public void setSwitchMode(int repeatmode) {
            mService.get().setSwitchMode(repeatmode);
        }
		public int getCurrentPosition(){
			return mService.get().getCurrentPosition();
		}
		public void reset(){
			mService.get().reset();
		}
		public void setDataSource(String path){
			mService.get().setDataSource(path);
		}
		public void prepare(){
			mService.get().prepare();
		}
		public void start(){
			mService.get().start();
		}
		public int getDuration(){
			return mService.get().getDuration();
		}
		public void seekTo(int msec){
			mService.get().seekTo(msec);
		}
		public boolean isPlayer(){
			return mService.get().isPlayer();
		}
		public void setIsMusicLayoutExist(boolean b){
			mService.get().setIsMusicLayoutExist(b);
		}
		public boolean getIsMusicLayoutExist(){
			return mService.get().getIsMusicLayoutExist();
		}
		public void setFirstFileName(String name){
			mService.get().setFirstFileName(name);
		}
		public void setFileList(List<String> list){
			mService.get().setFileList(list);
		}

		public void createDataProvider(String location){
			mService.get().createDataProvider(location);
		}
	    public void setCurrentMusicPath(String path){
			mService.get().setCurrentMusicPath(path);
		}
	    public String getNextFile(){
			return mService.get().getNextFile();
		}
	    public String getPreFile(){
			return mService.get().getPreFile();
		}			
	    public String getFirstFile(){
			return mService.get().getFirstFile();
		}
	    public String getCurrentMusicPath(){
			return mService.get().getCurrentMusicPath();
		}
	    public String getCurFilePath(){
			return mService.get().getCurFilePath();
		}
	}
		
	@Override
	public IBinder onBind(Intent intent){
	return binder; 
	}  
	    
    @Override
    public boolean onUnbind(Intent intent) {
        // Take a snapshot of the current playlist
		if(!isPlaying())
			{
			stopSelf(mServiceStartId);
			}
        return true;
    }
    
	
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mServiceStartId = startId;
		
        if ((intent != null)&&(!isMusicLayoutExist)) {
            String action = intent.getAction();
            String cmd = intent.getStringExtra("command");
			
			if(dataProvider==null)
			{
			return START_STICKY;
			}

            if (CMDNEXT.equals(cmd) || NEXT_ACTION.equals(action)) {
                next(true);
            } else if (CMDPREVIOUS.equals(cmd) || PREVIOUS_ACTION.equals(action)) {
                if (position() < 2000) {
                    prev(true);
                } else {
                    seekTo(0);
                    play();
                }
            } else if (CMDTOGGLEPAUSE.equals(cmd) || TOGGLEPAUSE_ACTION.equals(action)) {
                if (isPlaying()) {
                    pause();

                } else {
                    play();
                }
            } else if (CMDPAUSE.equals(cmd) || PAUSE_ACTION.equals(action)) {
                pause();

            } else if (CMDSTOP.equals(cmd)) {
                pause();
                seekTo(0);
            }
        }
        
        // make sure the service will shut down on its own if it was
        // just started but not bound to and nothing is playing
       /* mDelayedStopHandler.removeCallbacksAndMessages(null);
        Message msg = mDelayedStopHandler.obtainMessage();
        mDelayedStopHandler.sendMessageDelayed(msg, IDLE_DELAY);*/
        return START_STICKY;
    }

	@Override
    public void onCreate() {
    super.onCreate();
		
	if (mediaPlayer == null) {  
	   mediaPlayer = new MediaPlayer(); 
	   } 
	mediaPlayer.setOnCompletionListener(compleListener);
	mediaPlayer.setOnErrorListener(Errorlistener);

    mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
    mAudioManager.registerMediaButtonEventReceiver(new ComponentName(getPackageName(),
            MediaButtonReceiver.class.getName()));
    IntentFilter commandFilter = new IntentFilter();
    commandFilter.addAction(SERVICECMD);
    commandFilter.addAction(TOGGLEPAUSE_ACTION);
    commandFilter.addAction(PAUSE_ACTION);
    commandFilter.addAction(NEXT_ACTION);
    commandFilter.addAction(PREVIOUS_ACTION);
    registerReceiver(mIntentReceiver, commandFilter);
		
    }
	
	@Override 
	public void onDestroy(){
	Log.v(TAG, "onDestroy"); 
	super.onDestroy();
	unregisterReceiver(mIntentReceiver);
	if(mediaPlayer != null){  
		mediaPlayer.stop();	
		mediaPlayer.release();  
		}  
	} 

	public long position() {
        if (mIsInitialized) {
            return mediaPlayer.getCurrentPosition();
        }
        return -1;
    }
		
	public void setCurrentMusicPath(String path)
	{
		currentMusicPath=path;
	}
	public String getCurrentMusicPath()
	{
		return currentMusicPath;
	}
	public void createDataProvider(String location)
		{
		if(dataProvider==null)
			{
			dataProvider = new DataProvider(location);
			}
		}

	public String getCurFilePath()
		{
		return dataProvider.getCurFilePath();
		}

	public void setSwitchMode(int mode)
		{
		dataProvider.setSwitchMode(mode);
		}
	public MediaPlayer getMediaPlayer()
		{
		return mediaPlayer;
		}
	OnCompletionListener compleListener = new OnCompletionListener() {

		// Override
		public void onCompletion(MediaPlayer mp) {
			// TODO Auto-generated method stub
			if(isMusicLayoutExist)
				sendBroadcast2Layout("OnCompletion");
			else
				PlayNext();
		}
	};
	OnErrorListener Errorlistener = new OnErrorListener() {
		public boolean onError(MediaPlayer mp, int what, int extra) {
			// TODO Auto-generated method stub
			if(isMusicLayoutExist)
					sendBroadcast2Layout("OnError");
			else
				{
				try {
					Log.v("Player", "Errorlistener");
					PlayNext();
					} catch (Exception e) {
					e.printStackTrace();
					}
				}
			return false;
		}
	};
    private BroadcastReceiver mIntentReceiver = new BroadcastReceiver()
		{
		    @Override
		    public void onReceive(Context context, Intent intent) {
		        String action = intent.getAction();
		        String cmd = intent.getStringExtra("command");
		        if (CMDNEXT.equals(cmd) || NEXT_ACTION.equals(action)) {
		            next(true);
		        } else if (CMDPREVIOUS.equals(cmd) || PREVIOUS_ACTION.equals(action)) {
		            prev(true);
		        } else if (CMDTOGGLEPAUSE.equals(cmd) || TOGGLEPAUSE_ACTION.equals(action)) {
		            if (isPlaying()) {
		                pause();
		            } else {
		                play();
		            }
		        }else if (CMDSTOP.equals(cmd) && PAUSE_ACTION.equals(action)){
		        	stop();
				stopSelf(mServiceStartId);
		        }
		        else if (CMDPAUSE.equals(cmd) || PAUSE_ACTION.equals(action)) {
		            pause();
		        } else if (CMDSTOP.equals(cmd)) {
		            pause();
					seekTo(0);
		        } 
		    }
		};
	
	public String getNextFile() {
		if(dataProvider!=null)
			{
			String filename = dataProvider.getNextFile();
			return filename;	
			}
		else
			return "null";
	}
	
	public String getPreFile() {
		String filename = dataProvider.getPreFile();
		return filename;
	}
	public String getFirstFile(){
		String filename =dataProvider.getFirstFile();
		return filename;
	}
	public void next(boolean b)
		{
		if(b)
			PlayNext();
		}
	
	public void prev(boolean b)
		{
		if(b)
			PlayPre();
		}
	public boolean isPlaying()
		{
		boolean isPlaying = mediaPlayer.isPlaying();
		return isPlaying;
		}

	public void PlayNext() {
		
		String filename =getNextFile();
		if(filename=="null")
			stop();
		else
			{
			mediaPlayer.reset();
			playSong(getCurFilePath());
			}
	}
	
	public void PlayPre() {
		
		String filename =getPreFile();
		mediaPlayer.reset();
		playSong(getCurFilePath());
	}
	public void playSong(String locaton) {

		if (mediaPlayer.isPlaying()) {
			mediaPlayer.reset();
		}

		try {
			mediaPlayer.setDataSource(locaton);
			setCurrentMusicPath(locaton);
		} catch (IllegalArgumentException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IllegalStateException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			mediaPlayer.prepare();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.v("Player", "error");
			// PlayNext();
			return;
		}
		mediaPlayer.start();
	}

	public void setFileList(List<String> filename)
	{	
		dataProvider.setFilelist(filename);
	}
	
	public void setFirstFileName(String name)
	{	
		dataProvider.setfirstname(name);
	}
	
	public void setIsMusicLayoutExist(boolean isexist)
		{
		isMusicLayoutExist=isexist;
		}
	
	public boolean getIsMusicLayoutExist()
		{
		return isMusicLayoutExist;
		}
	
	private void sendBroadcast2Layout(String action)
		{
		Intent intent = new Intent("state_chage");
		intent.putExtra("PlayListener", action);
		sendBroadcast(intent);
		}

	public void play() {  
		if (!mediaPlayer.isPlaying()) {	
			mAudioManager.registerMediaButtonEventReceiver(new ComponentName(this.getPackageName(),
						   MediaButtonReceiver.class.getName()));
			mediaPlayer.start();  
			}  
	}  

	public void pause() {  
		if (mediaPlayer != null && mediaPlayer.isPlaying()) {  
			mediaPlayer.pause();  
			}  
	}  
	
	public void stop() {
		if (mediaPlayer != null)
			{
			mediaPlayer.reset();	
			mediaPlayer.stop();
			setCurrentMusicPath("");
			mIsInitialized = false;
			dataProvider=null;
		}  
	}
	public int getCurrentPosition(){
		return mediaPlayer.getCurrentPosition();
	}
	public void reset(){
		mediaPlayer.reset();	
	}
	public void setDataSource(String path){
        try {
			mediaPlayer.reset();
        	mediaPlayer.setDataSource(path);
			mIsInitialized = true;
        } catch (IOException ex) {
            // TODO: notify the user why the file couldn't be opened
            mIsInitialized = false;
            return;
        } catch (IllegalArgumentException ex) {
            // TODO: notify the user why the file couldn't be opened
            mIsInitialized = false;
            return;
        }
	}
	
	public void prepare(){
        try {
        	mediaPlayer.prepare();
        } catch (IOException ex) {
            // TODO: notify the user why the file couldn't be opened
            return;
        } catch (IllegalArgumentException ex) {
            // TODO: notify the user why the file couldn't be opened
            return;
        }
	}
	public void start(){
		mediaPlayer.start();
	}
	public int getDuration(){
		return mediaPlayer.getDuration();
	}
	public void seekTo(int msec){
		mediaPlayer.seekTo(msec);
	}
	public boolean isPlayer(){
		if(mediaPlayer!=null)
			return true;
		else 
			return false;
	}
}
