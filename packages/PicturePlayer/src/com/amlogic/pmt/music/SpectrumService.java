package com.amlogic.pmt.music;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.IBinder;

public class SpectrumService extends Service
{
	public static final String UPDATESPECTRUM_DATA = "MusicPlayer.ACTION_UPDATESPECTRUMDATA";
	public static final String UPDATESPECTRUM_STATUS = "MusicPlayer.ACTION_UPDATESPECTRUM";
	public static final int inputBlockSize = 96*2;
	private CompleteReceiver completeUi = null;

	public void onCreate() 
	{
		completeUi = new CompleteReceiver();
		IntentFilter filter = new IntentFilter(UPDATESPECTRUM_STATUS);
		filter.addAction(UPDATESPECTRUM_STATUS);
		registerReceiver(completeUi, filter);
		
		
	}
	
	//@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	//@Override
	public void onStart(Intent i, int id) {
		super.onStart(i, id);
		sendSpectrumData();
	}
	
	public class CompleteReceiver extends BroadcastReceiver 
	{
	//	@Override
		public void onReceive(Context context, Intent intent) 
		{
			boolean  status = false;
			if(intent.getBooleanExtra("updatespecsts",status) == true)
			{
				sendSpectrumData();
			}
		}
	}
	

	public void onDestroy() {
		unregisterReceiver(completeUi);
	}

	public int[] getTransferData()
	{
		short [] data = new short[inputBlockSize];
//		int dataLen = MediaPlayer.snoop(data, 1);
		short [] dispdata = new short[inputBlockSize/2];
		for(int i=0;i< inputBlockSize/2;i++)
		{
			dispdata[i] =  data[i];
		}
		return getSpectrumIndex(dispdata);
	}
	
	
	private int[] getSpectrumIndex(short[] attitude)
	{
		int[] index = new int[inputBlockSize/2];
		int attLength = attitude.length;
		for(int i = 0;i<attLength/2;i++)
		{
			int absData ;
			if(attitude[i*2] < 0)
				absData = -attitude[i*2];
			else
				absData = attitude[i*2];
			if(absData > 3110)
				index[i] = 25 + (absData-3110)/400; //25~29
			else if(absData > 1610)
				index[i] = 20 + (absData-1610)/300; //20~25
			else if(absData > 610)
				index[i] = 15 + (absData-610)/200; //15~20
			else if(absData > 110)
				index[i] = 10 + (absData-110)/100; //10~15
			else if(absData > 10)
				index[i] = 5 + (absData-10)/20; //5~10
			else
				index[i] = absData/2; //0~5
			
			if(attitude[i] != 0 && index[i] == 0)
				index[i] = 1;
			
			if (index[i]> 29)
				index[i] = 29;
			else if(index[i] < 0)
				index[i] = 0;
//			
//			int absData = Math.abs(attitude[i]);
//			if(absData > 3110)
//				index[i] = 21 + (absData-3110)/400; // 21 ~24
//			else if(absData > 1610)
//				index[i] = 27 + (absData-1610)/300; // 17 ~20
//			else if(absData > 610)
//				index[i] = 13 + (absData-610)/200; // 13 ~16
//			else if(absData > 110)
//				index[i] = 9 + (absData-110)/100; // 9 ~12
//			else if(absData > 10)
//				index[i] = 5 + (absData-10)/20; //5 ~8
//			else
//				index[i] = absData/2; // 0 ~4
//			
//			if(attitude[i] != 0 && index[i] == 0)
//				index[i] = 1;
//			
//			if (index[i] > 24)
//	        	index[i] = 24;
//			else if(index[i] < 0)
//				index[i] = 0;	
		}
		int half = attLength/2;
		for(int i = 0;i<half;i++)
			index[attLength/2+i] = index[half-i-1];
		
		return index;
	}

	private void sendSpectrumData()
	{
		int[] data = getTransferData();
		Intent intent = new Intent(UPDATESPECTRUM_DATA);
		intent.putExtra("spectrumData", data);
		sendBroadcast(intent);
	}

	
}
