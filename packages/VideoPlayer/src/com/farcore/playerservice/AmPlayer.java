package com.farcore.playerservice;

import com.farcore.playerservice.Player;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import java.lang.*;

public class AmPlayer extends Service {
    static 
    {
    	System.loadLibrary("amplayerjni");
    };
    private static final String TAG = "amplayer";
    private static Messenger mClient = null;
    private static int player_status = 0;
    private static int last_cur_time = -1;
    
    private int mPid = -1;
    
  //TODO need api
	private native int setMedia(String url,int loop,int playMode,int pos);//playMode:0,all,just default;
	//play
	private native int playMedia(String url,int loop,int playMode,int pos);// pos refer to "start position"

	private native int start(int pid);
	private native int pause(int pid);
	private native int resume(int pid);
	private native int seek(int pid,int pos);//in second
	private native int stop(int pid);
	private native int close(int pid);
	private native int set3Dmode(int pid, int mode);//0 off, 1...14 mode

	private native int fastforward(int pid,int speed);
	private native int fastrewind(int pid,int speed);
	private native int setSubtitleOut(int pid, int sub_uid);
	private native int setAudioTrack(int pid,int track_uid);
	private native int setAudioChannel(int pid,int channel_id);
	private native int setRepeat(int pid, int isRepeat);
	private native Object getMetaInfo(int pid);
    private native Object getDivxInfo(int pid);

	private static native int setTone(int pid, int tone);
	private static native int setIVolume(int vol);
	private static native int mute();
	private static native int unmute();
	private static native int setVideoBlackOut(int isBlackOut);

	private static native int native_init();
	private static native int native_uninit();

    public static native int native_enablecolorkey(short key_rgb565);
    public static native int native_disablecolorkey();
    public static native int native_setglobalalpha(int alpha);
    public native int set3Dviewmode(int vmode);
	public native int set3Daspectfull(int aspect);
	public native int set3Dswitch(int isOn);
	
	public native int set3Dgrating(int isOn);
		
    public static native int native_getosdbpp();
    public static native int enable_freescale(int cfg);
    public static native int disable_freescale(int cfg);
    public static native int getProductType();
    public static native int disableFreescaleMBX();
    public static native int enable2XScale();
    public static native int enable2XYScale();
    public static native int enableFreescaleMBX();
    public static native int disable2X2XYScale();
    public static native int GL2XScale(int mSwitch);

    public int start() { return start(mPid);}
	public int pause() { return pause(mPid);}
	public int resume() { return resume(mPid);}
	public int seek(int pos) { return seek(mPid,pos);}
	public int set3Dmode(int mode) { return set3Dmode(mPid,mode);}
	public int stop() { return stop(mPid);}
	public int close() { return close(mPid);}
	public int fastforward(int speed) { return fastforward(mPid,speed);}
	public int fastrewind(int speed) { return fastrewind(mPid,speed);}
	public int setSubtitleOut( int sub_uid) { return setSubtitleOut(mPid,sub_uid);}
	public int setAudioTrack(int track_uid) { return setAudioTrack(mPid,track_uid);}
	public int setAudioChannel(int channel_id) { return setAudioChannel(mPid,channel_id);}
	public int setRepeat(int isRepeat) { return setRepeat(mPid,isRepeat);}
	public Object getMetaInfo() {return getMetaInfo(mPid);}
    public Object getDivxInfo() {return getDivxInfo(mPid);}
	public int setTone(int tone) {return setTone(mPid, tone);}
	
	
	public void onCreate()
	{
		super.onCreate();
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		mClient = null;
		return m_player;
	}
	public boolean onUnbind (Intent intent)
	{
		try {
			m_player.Stop();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			m_player.Close();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mClient = null;
		return true;
	}
	
	public Player.Stub m_player = new Player.Stub()
	{
		public int Init() throws RemoteException {
			native_init();
			return 0;
		}

		public int Open(String filepath, int position) throws RemoteException {
			mPid = setMedia( filepath, 0, 0, position);
			if (mPid < 0)
				Log.e(TAG, "get pid failed after setMedia");
			//else
				//start();
			return 0;
		}

		public int Play() throws RemoteException {
			//startGetStates();
			start();
			return 0;
		}

		public int Pause() throws RemoteException {
			pause();
			return 0;
		}

		public int Resume() throws RemoteException {
			resume();
			return 0;
		}

		public int Stop() throws RemoteException {
			//stopGetStates();
			if (mPid >= 0) {
				stop(mPid);
			}
			return 0;
		}

		public int Close() throws RemoteException {
			if (mPid >= 0)
			{
				close();
				mPid = -1;
			}
			return 0;
		}
		
		public MediaInfo GetMediaInfo() throws RemoteException {
			return (MediaInfo)getMetaInfo();
		}
		
        public DivxInfo GetDivxInfo() throws RemoteException{
          return (DivxInfo)getDivxInfo();
        }

		public int SwitchAID(int id) throws RemoteException {
			setAudioTrack(id);
			Log.d("audiostream","aid: " + id);
			return 0;
		}

		public int SwitchAudioChannel(int id) throws RemoteException {
			setAudioChannel(id);
			Log.d("audiochannel ","select channel: " + id);
			return 0;
		}

		public int FastForward(int speed) throws RemoteException {
			fastforward(speed);
			return 0;
		}

		public int BackForward(int speed) throws RemoteException {
			fastrewind(speed);
			return 0;
		}

		public int SetColorKey(int color) throws RemoteException {
			native_enablecolorkey((short) color);
			return 0;
		}
		
		public void DisableColorKey() throws RemoteException {
			native_disablecolorkey();
		}
		
		public int GetOsdBpp() throws RemoteException {
			int ret = native_getosdbpp();
			return ret;
		}

		public int Seek(int time) throws RemoteException {
			seek(time);
			return 0;
		}
		
		public int Set3Dmode(int mode) throws RemoteException {
			set3Dmode(mode);
			return 0;
		}
		public int Set3Dviewmode(int mode) throws RemoteException {
			set3Dviewmode(mode);
			return 0;
		}

		public int Set3Daspectfull(int aspect) throws RemoteException{
			set3Daspectfull(aspect);
			return 0;
		}
		public int Set3Dswitch(int isOn){
			set3Dswitch(isOn);
			return 0;
		}
		public int Set3Dgrating(int isOn){
			set3Dgrating(isOn);
			return 0;
		}
		public int RegisterClientMessager(IBinder hbinder)
				throws RemoteException {
			mClient = new Messenger(hbinder);
			return 0;
		}
	};
	
	//get info every 0.5s,and send it to client
	Handler mhandler = new Handler();
    private Runnable mGetState = new Runnable() {
        public void run() {
        	//AmPlayer.this.native_reqstate();
        	mhandler.postDelayed(mGetState, 500);
        }
    };
    private void startGetStates() {
    	mhandler.removeCallbacks(mGetState);
    	mhandler.postDelayed(mGetState, 500);
    }
    private void stopGetStates() {
    	mhandler.removeCallbacks(mGetState);
    }

	//tony.wang
	private static boolean isOSDOn = true;
	private static boolean isSubOn = false;
	private static int current_time_bac = 0;
	private static int full_time_bac = 0;
	public static void setOSDOnFlag(boolean flag)
	{
		isOSDOn = flag;
	}
	public static void setSubOnFlag(boolean flag)
	{
		isSubOn = flag;
	}
	public static int getBackupCurrentTime()
	{
		return current_time_bac;
	}
	public static int getBackupTotalTime()
	{
		return full_time_bac;
	}
	public static void onUpdateState(int pid, int status, int full_time,
			int current_time, int last_time, int error_no, int param)
	{
		if (null == mClient)
		{
			Log.i(TAG, "RegisterClientMessager has not be called. ");
			return;
		}
		if (0 != full_time)
		{
			Message message = Message.obtain();
			message.what = VideoInfo.TIME_INFO_MSG;
			message.arg1 = current_time;
			message.arg2 = full_time;

			current_time_bac = current_time;//tony.wang
			full_time_bac = full_time;

			if((isOSDOn)||(!isOSDOn&&isSubOn))
			{
				try {
					mClient.send(message);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
			last_cur_time = current_time;
		}
		
		//tony.wang
		if(current_time==0)
		{
			current_time_bac = 0;
		}
		if(full_time==0)
		{
			current_time_bac = 0;
			full_time_bac = 0;
		}
		
		//send message for status changed
		if(player_status != status)
		{
			player_status = status;
			Message s_message = Message.obtain();
			s_message.what = VideoInfo.STATUS_CHANGED_INFO_MSG;
			s_message.arg1 = player_status;
			s_message.arg2 = param;
			if (player_status == VideoInfo.PLAYER_ERROR)
			{
				s_message.arg2 = error_no;
				error_no = 0;
			}
            if(player_status == VideoInfo.DIVX_AUTHOR_ERR)
            {
              Log.d(TAG, "Divx author failed");
            }
            if(player_status == VideoInfo.DIVX_EXPIRED)
            {
              Log.d(TAG, "Divx expired");
            }

			Log.d(TAG,"player status changed to: " + Integer.toHexString(player_status));
			try {
				mClient.send(s_message);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		
		if (error_no != 0)
		{
			Message e_message = Message.obtain();
			e_message.what = VideoInfo.HAS_ERROR_MSG;
			e_message.arg2 = error_no;
			Log.d(TAG,"player has error: " + error_no);
			try {
				mClient.send(e_message);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}
	
}
