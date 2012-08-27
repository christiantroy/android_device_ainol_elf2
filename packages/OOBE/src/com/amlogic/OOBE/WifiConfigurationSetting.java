package com.amlogic.OOBE;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class WifiConfigurationSetting extends Activity {
	
	private Button mwifisetBtn;
	private Button mbackBtn;
	private Button mnextBtn;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.wificonfig);
		
		mwifisetBtn = (Button)findViewById(R.id.btn_wifi_configuration);
		mbackBtn = (Button)findViewById(R.id.btn_wifi_back);
		mnextBtn = (Button)findViewById(R.id.btn_wifi_next);
		
		mwifisetBtn.setOnClickListener(new mwifisetBtnOnClickListener());
		mbackBtn.setOnClickListener(new mbackBtnOnClickListener());
		mnextBtn.setOnClickListener(new mnextBtnOnClickListener());
	}
	
	class mwifisetBtnOnClickListener implements OnClickListener{

		//@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent = new Intent();
			intent.setClass(WifiConfigurationSetting.this, WirelessSettings.class);
			startActivity(intent);
			WifiConfigurationSetting.this.finish();
		}
		
	}
	
	class mbackBtnOnClickListener implements OnClickListener {

		//@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			ConfigOOBE config = new ConfigOOBE(WifiConfigurationSetting.this);
			String str = config.getPrevActivityName("WifiConfigurationSetting");
			
			Intent intent = new Intent();
			if(str.equals("") == false){
				intent.setClass(WifiConfigurationSetting.this,(Class<?>) config.getActivity(str));
			}
			else {
				intent.setClass(WifiConfigurationSetting.this,Finish.class);
			}
			startActivity(intent);
			WifiConfigurationSetting.this.finish();
		}
		
	}
	
	class mnextBtnOnClickListener implements OnClickListener {

		//@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			ConfigOOBE config = new ConfigOOBE(WifiConfigurationSetting.this);
			String str = config.getNextActivityName("WifiConfigurationSetting");
			
			Intent intent = new Intent();
			if(str.equals("") == false){
				intent.setClass(WifiConfigurationSetting.this,(Class<?>) config.getActivity(str));
			}
			else {
				intent.setClass(WifiConfigurationSetting.this,Finish.class);
			}
			startActivity(intent);
			WifiConfigurationSetting.this.finish();
		}
		
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
	
	
}
