package com.amlogic.OOBE;


import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.app.AlertDialog;
import android.view.View;
import android.content.DialogInterface;

import android.widget.CheckBox;
import android.content.Intent;



public class UserLicenseAgree extends Activity {
    /** Called when the activity is first created. */
	
	public Button mNext;
	public Button mBack;
	public CheckBox mCheckBox;

	private static final String DATE_TIME_SETTING_PACKAGE = "com.android.settings";
    private static final String DATE_TIME_SETTING_CLASS ="com.android.settings.DateTimeSettings";
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.userlicenseagree);

        mNext = (Button) findViewById(R.id.btn_userlicense_next);
		mBack = (Button) findViewById(R.id.btn_userlicense_back);
		mCheckBox= (CheckBox) findViewById(R.id.cb_userlicense_accept);

		mCheckBox.setChecked(false);
		mNext.setEnabled(false);

		mCheckBox.setOnClickListener(new CheckBox.OnClickListener(){   
			public void onClick(View v)
				{
				if(mCheckBox.isChecked())
					{
					mNext.setEnabled(true);
					}
				else
					{
					mNext.setEnabled(false);
					}
				}
			}

		);

        mBack.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v) {
				ConfigOOBE config = new ConfigOOBE(UserLicenseAgree.this);
				String str = config.getPrevActivityName("UserLicenseAgree");
				
				Intent intent = new Intent();
				if(str.equals("") == false){
					intent.setClass(UserLicenseAgree.this,(Class<?>) config.getActivity(str));
				}
				else {
					intent.setClass(UserLicenseAgree.this,OOBE.class);
				}
				startActivity(intent);
				UserLicenseAgree.this.finish();		
            }
        });


		 mNext.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v) {
				ConfigOOBE config = new ConfigOOBE(UserLicenseAgree.this);
				String str = config.getNextActivityName("UserLicenseAgree");
				
				Intent intent = new Intent();
				if(str.equals("") == false){
					intent.setClass(UserLicenseAgree.this,(Class<?>) config.getActivity(str));
				}
				else {
					intent.setClass(UserLicenseAgree.this,Finish.class);
				}
				startActivity(intent);
				UserLicenseAgree.this.finish();			
            }
        });
    }
    
}
