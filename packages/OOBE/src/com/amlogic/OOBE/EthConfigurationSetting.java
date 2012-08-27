package com.amlogic.OOBE;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class EthConfigurationSetting extends Activity {
    
	private Button methsetBtn;
	private Button mbackBtn;
	private Button mnextBtn;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ethnetconfig);
		
		methsetBtn = (Button)findViewById(R.id.btn_ethnet_configuration);
		mbackBtn = (Button)findViewById(R.id.btn_ethnet_back);
		mnextBtn = (Button)findViewById(R.id.btn_ethnet_next);
		
		methsetBtn.setOnClickListener(new methsetBtnOnClickListener());
		mbackBtn.setOnClickListener(new mbackBtnOnClickListener());
		mnextBtn.setOnClickListener(new mnextBtnOnClickListener());
		
	}
	
	class methsetBtnOnClickListener implements OnClickListener{

		//@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent = new Intent();
			intent.setClass(EthConfigurationSetting.this, EthernetSettings.class);
			startActivity(intent);
			EthConfigurationSetting.this.finish();
		}
		
	}
	
	class mbackBtnOnClickListener implements OnClickListener {

		//@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			ConfigOOBE config = new ConfigOOBE(EthConfigurationSetting.this);
			String str = config.getPrevActivityName("EthConfigurationSetting");
			
			Intent intent = new Intent();
			if(str.equals("") == false){
				intent.setClass(EthConfigurationSetting.this,(Class<?>) config.getActivity(str));
			}
			else {
				intent.setClass(EthConfigurationSetting.this,OOBE.class);
			}
			startActivity(intent);
			EthConfigurationSetting.this.finish();
		}
		
	}
	
	class mnextBtnOnClickListener implements OnClickListener {

		//@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			ConfigOOBE config = new ConfigOOBE(EthConfigurationSetting.this);
			String str = config.getNextActivityName("EthConfigurationSetting");
			
			Intent intent = new Intent();
			if(str.equals("") == false){
				intent.setClass(EthConfigurationSetting.this,(Class<?>) config.getActivity(str));
			}
			else {
				intent.setClass(EthConfigurationSetting.this,Finish.class);
			}
			startActivity(intent);
			EthConfigurationSetting.this.finish();
		}
		
	}
}
