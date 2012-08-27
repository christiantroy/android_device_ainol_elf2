package com.amlogic.serialport;



import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.AttributeSet;
import android.util.Log;


import com.amlogic.serialport.Iuartservice;


public class UartControl {
	private Context mContext;

	public UartControl(Context context, AttributeSet attrs) {
		mContext = context;
		mContext.startService(mIntent);
		mContext.bindService(mIntent, mConnection, Context.BIND_AUTO_CREATE);
	}

	private Intent mIntent = new Intent("com.amlogic.serialport.UartService");
	private Iuartservice mIuartservice = null;
	private ServiceConnection mConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder service) {
			Log.d("uart", "......UartControl onServiceConnected start......\n");
			mIuartservice = Iuartservice.Stub.asInterface(service);
    	}
        public void onServiceDisconnected(ComponentName className) {
        	Log.d("uart", "......UartControl onServiceDisconnected .........\n");
        	mIuartservice = null;
        }
	};
	
	//state "0" disable tv vol+-
	//state	"1" enable tv vol+-
	//state	"2" show tv volumn menu and enable tv vol+-
	public void UartSendVolumeOsd(String state) {
		if(mIuartservice!=null)
			try {
				mIuartservice.UartSend("VolumeOsd", state);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
	}	
	public void UartSendVideoPlayState(String state) {
		if(mIuartservice!=null)
			try {
				mIuartservice.UartSend("playerstate", state);
			} catch (RemoteException e) {
				
				e.printStackTrace();
			}//"0","1"
	}
	public void UartSendOsdOnOff(String state) {
		if(mIuartservice!=null)
			try {
				mIuartservice.UartSend("osdonoff", state);
			} catch (RemoteException e) {
					
				e.printStackTrace();
			}//"0","1"
	}
	private boolean unbindFlag = true;
	public void CallUartcontrolunbindservice() {
		if (unbindFlag) {
			unbindFlag = false;
			mContext.unbindService(mConnection);
			Log.d("uart","........ call Uartcontrol unbindService(mConnection) .........\n");
		}
	}	 
	//rembemer to unbindservice when you exit your application
				
}