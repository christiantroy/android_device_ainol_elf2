package com.amlogic.pmt;

import java.io.File;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import com.amlogic.PicturePlayer.R;


public class DevStatusDisplay extends View {
	public static final int STS_NET = 0x0001;
	public static final int STS_USB = 0x0002;
	public static final int STS_CAMERA = 0x0004;
	public static final int STS_BLANKET = 0x0008;

	private Context mcontext;
	private BroadcastReceiver USB_Receiver, Net_Receiver;
	private ConnectivityManager connectivityManager;
	private Bitmap status_line;
	private Bitmap net_status_true, net_status_false;
	private Bitmap usb_status_true, usb_status_false;
	private Bitmap camera_status_true, camera_status_false;
	private Bitmap blanket_status_true, blanket_status_false;
	private boolean dis_net, dis_usb, dis_camera, dis_blanket;
	private boolean sts_net, sts_usb, sts_camera, sts_blanket;
	private boolean sts_camera_last, sts_blanket_last;
	
	protected static final String TAG = "DevStatus";

	public DevStatusDisplay(Context context, int sts_dev) {
		super(context);
		mcontext = this.getContext();
		
		connectivityManager = (ConnectivityManager) mcontext.getSystemService(Context.CONNECTIVITY_SERVICE);

		IntentFilter intentFilter_USB = new IntentFilter();
		intentFilter_USB.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
		intentFilter_USB.addAction(Intent.ACTION_MEDIA_MOUNTED);
		intentFilter_USB.addAction(Intent.ACTION_MEDIA_REMOVED);
		intentFilter_USB.addAction(Intent.ACTION_MEDIA_BAD_REMOVAL);
		intentFilter_USB.addDataScheme("file");
		USB_Receiver = new USBStatusReceiver();
		mcontext.registerReceiver(USB_Receiver, intentFilter_USB);

		IntentFilter intentFilter_Net = new IntentFilter();
		intentFilter_Net.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		Net_Receiver = new NetStatusReceiver();
		mcontext.registerReceiver(Net_Receiver, intentFilter_Net);

		status_line = BitmapFactory.decodeResource(getResources(), R.drawable.status_line);
		net_status_true = BitmapFactory.decodeResource(getResources(), R.drawable.status_net_connected);
		net_status_false = BitmapFactory.decodeResource(getResources(), R.drawable.status_net_disconnected);
		usb_status_true = BitmapFactory.decodeResource(getResources(), R.drawable.status_usb_connected);
		usb_status_false = BitmapFactory.decodeResource(getResources(), R.drawable.status_usb_disconnected);
		camera_status_true = BitmapFactory.decodeResource(getResources(), R.drawable.status_camera_connected);
		camera_status_false = BitmapFactory.decodeResource(getResources(), R.drawable.status_camera_disconnected);
		blanket_status_true = BitmapFactory.decodeResource(getResources(), R.drawable.status_blanket_connected);
		blanket_status_false = BitmapFactory.decodeResource(getResources(), R.drawable.status_blanket_disconnected);

		dis_net = (sts_dev & STS_NET) != 0;
		dis_usb = (sts_dev & STS_USB) != 0;
		dis_camera = (sts_dev & STS_CAMERA) != 0;
		dis_blanket = (sts_dev & STS_BLANKET) != 0;
		
		if(dis_net) sts_net = checkNetConnect();
		if(dis_usb) sts_usb = checkUsbExist();
		if(dis_camera) sts_camera = checkCameraExist();
		if(dis_blanket) sts_blanket = checkBlanketExist();
		
		if (dis_camera || dis_blanket) {
			resetTimer();
		}
	}

	class NetStatusReceiver extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {
			Log.d(TAG, "CONNECTIVITY_ACTION MSG is comming");
			boolean ifEtherConnected = false, ifWifiConnected = false;
			ifEtherConnected = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET).isConnected();
			ifWifiConnected = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected();
			String action = intent.getAction();
			if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
				Log.d(TAG, "ifWifiConnected=" + ifWifiConnected);
				Log.d(TAG, "ifEtherConnected=" + ifEtherConnected);
				sts_net = ifEtherConnected || ifWifiConnected;
				Log.d(TAG, "got CONNECTIVITY_ACTION: noConnectivity = " + sts_net);
				postInvalidate();
			}
		}
	}

	class USBStatusReceiver extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals("android.intent.action.MEDIA_REMOVED")
					|| action.equals("android.intent.action.MEDIA_BAD_REMOVAL")) {
				if (!checkUsbExist()) {
					sts_usb = false;
					postInvalidate();
				}
			}
			if (action.equals("android.intent.action.MEDIA_MOUNTED")) {
				sts_usb = true;
				postInvalidate();
			}
			Log.d(TAG, "USBStatus " + sts_usb + " - " + action);
		}
	}
	
	public void unregisterReceiver(){
		mcontext.unregisterReceiver(USB_Receiver);
		mcontext.unregisterReceiver(Net_Receiver);
	}
	
	public boolean checkNetConnect(){
		NetworkInfo EtherNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET);
		NetworkInfo WifiInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		return EtherNetInfo.isConnected() || WifiInfo.isConnected();
	}
	
	public boolean checkUsbExist(){
		File[] files = new File("/mnt").listFiles();
		if (files != null) {
			for (File file : files) {
				if (file.getPath().startsWith("/mnt/sd")
						&& !file.getPath().startsWith("/mnt/sdcard")) {
					File[] files1 = new File(file.getPath()).listFiles();
					if (files1 != null) {
						for (File file1 : files1) {
							if(file1.canRead()){
								return true;
							}
						}
					}
				}
			}
		}
		return false;
	}

	public boolean checkCameraExist() {
		File[] files = new File("/dev").listFiles();
		if (files != null) {
			for (File file : files) {
				if (file.getPath().startsWith("/dev/video0"))
					return true;
			}
		}
		return false;
	}

	public boolean checkBlanketExist() {
		File[] files = new File("/dev/input").listFiles();
		if (files != null) {
			for (File file : files) {
				if (file.getPath().startsWith("/dev/input/js0"))
					return true;
			}
		}
		return false;

	}

	protected void onDraw(Canvas canvas) {
		int i = 0 , space = 60;
		int xPos = 1410, yPos = 623;
		canvas.drawBitmap(status_line, 1389, 600, null);
		if (dis_net) {
			if (sts_net) {
				canvas.drawBitmap(net_status_true, xPos + i * space, yPos, null);
			} else {
				canvas.drawBitmap(net_status_false, xPos + i * space, yPos, null);
			}
			i++;
		}
		if (dis_usb) {
			if (sts_usb) {
				canvas.drawBitmap(usb_status_true, xPos + i * space, yPos, null);
			} else {
				canvas.drawBitmap(usb_status_false, xPos + i * space, yPos, null);
			}
			i++;
		}
		if (dis_camera) {
			if (sts_camera) {
				canvas.drawBitmap(camera_status_true, xPos + i * space, yPos, null);
			} else {
				canvas.drawBitmap(camera_status_false, xPos + i * space, yPos, null);
			}
			i++;
		}
		if (dis_blanket) {
			if (sts_blanket) {
				canvas.drawBitmap(blanket_status_true, xPos + i * space, yPos, null);
			} else {
				canvas.drawBitmap(blanket_status_false, xPos + i * space, yPos, null);
			}
		}
	}

	private Handler handlerTimer = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				sts_camera = checkCameraExist();
				sts_blanket = checkBlanketExist();
				if (sts_camera_last != sts_camera || sts_blanket_last != sts_blanket) {
					sts_camera_last = sts_camera;
					sts_blanket_last = sts_blanket;
					postInvalidate();
				}
				resetTimer();
				break;
			}
		}
	};

	private void resetTimer() {
		handlerTimer.removeMessages(1);
		Message mage = handlerTimer.obtainMessage(1);
		handlerTimer.sendMessageDelayed(mage, 1000);
	}
}
