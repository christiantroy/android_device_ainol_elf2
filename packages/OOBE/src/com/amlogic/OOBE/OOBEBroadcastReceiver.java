package com.amlogic.OOBE;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemProperties;

import android.util.Log;

import java.io.FileReader;
import java.io.FileNotFoundException;
import com.amlogic.OOBE.SelectFlag;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class OOBEBroadcastReceiver extends BroadcastReceiver {
	private static final String ACTION_BOOT_COMPLETED =
		"android.intent.action.BOOT_COMPLETED";
	
	private final static String launch_dispaly_mode = "oobeflag";
	public static final String filePath = "/data/data/com.amlogic.OOBE/files/outputmode_set";
	public static String defaultModeValues = "1 1 0 0 1 0 1" + "\n";
	public String outputmode;

	protected class MyException extends Exception {
        protected MyException(String msg){
            super(msg);
        }
    }
	
	@Override
	public void onReceive(Context context, Intent intent) {
		
		if (ACTION_BOOT_COMPLETED.equals(intent.getAction())) 
			{
			//context.startService(new Intent(context, UpdateService.class));

			//record the outputmode when power on 
			outputmode = SystemProperties.get("ubootenv.var.outputmode");
			isTVmodeCorrect(outputmode);
			SelectFlag.setSelect(outputmode);
			
			try
			{	
			int count, i = 0;
			char[] buf = new char[100];
			FileReader rd = new FileReader(filePath);
			count = rd.read(buf, 0, 99);
			buf[count] = '\n';
			String currentModeValues = new String(buf, 0, count);

			if ((currentModeValues.compareTo(defaultModeValues)) == 0)
				throw new MyException("Default OutPutMode Detected, exit");
			}
			catch (FileNotFoundException e1) 
				{              
                			//e1.printStackTrace();
				Intent starterIntent = new Intent(context, OOBE.class);
				starterIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(starterIntent);
            	}
			catch(Exception e2)
				{
				Log.e("OOBE Start Up Receiver: EXCEPTION ", e2.toString());
				};
			Intent serviceIntent = new Intent(context, SetPpm.class);          
      context.startService(serviceIntent);
			
			/*
			if(str.equals("setted") == false){
				Intent oobe = new Intent(context, OOBE.class);
				oobe.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(oobe);
			}*/
		}
	}

	private void isTVmodeCorrect(String tvmode)
		{
			// set outputmode in case it's not got correctly
			if((!tvmode.equals("480p"))&&(!tvmode.equals("480i"))
					&&(!tvmode.equals("576p"))&&(!tvmode.equals("576i"))&&(!tvmode.equals("720p"))
					&&(!tvmode.equals("1080p"))&&(!tvmode.equals("1080i")))
				{
				try
					{
						Log.e("OOBEBroadcastReceiver","Not get outputmode successfully,reset outputmode");
						FileReader mode =new FileReader("/sys/class/display/mode");
						char[] buf1 = new char[10];
						int len = mode.read(buf1, 0, 9);
						String mode_context = new String(buf1, 0, len);
						len=mode_context.indexOf('\n');
						String tv_mode= new String(buf1, 0, len);
						outputmode =tv_mode;
						SystemProperties.set("ubootenv.var.outputmode",outputmode);
					}
				catch (FileNotFoundException e1) 
					{  
					Log.e("OOBEBroadcastReceiver","Not get /sys/class/display/mode");
	            	}
				catch(Exception e2)
					{
					Log.e("OOBE Start Up Receiver: EXCEPTION ", e2.toString());
					};
				}
		}
}

