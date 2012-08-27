package com.amlogic.OOBE;

import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.View;
import android.widget.Button;
import android.os.SystemProperties;
import android.app.ActivityManagerNative;
import android.app.IActivityManager;
import android.app.backup.BackupManager;
import android.util.Log;

public class Language extends Activity {
    /** Called when the activity is first created. */

	private String prefix = SystemProperties.get("ro.ubootenv.prefix");
	private static String LANGUAGE_SAV = "language";
	public Button mLanguageBtn;
	public Button mBackBtn;
	public Button mNextBtn;
	public int focus_index=0;
	private String []language_list;
	private String []country_list;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.language);
        
        mLanguageBtn = (Button) findViewById(R.id.btn_language_set);
        mBackBtn = (Button) findViewById(R.id.btn_language_back);
        mNextBtn = (Button) findViewById(R.id.btn_language_next);
        
        mLanguageBtn.setOnClickListener(myShowLanguageDialog);

		Configuration conf = getResources().getConfiguration();
        String locale = conf.locale.getDisplayName(conf.locale);
		String language =conf.locale.getDefault().getLanguage();
		String country =conf.locale.getDefault().getCountry();
		language_list = getResources().getStringArray(R.array.language);
		country_list = getResources().getStringArray(R.array.country);
		for (String count : country_list) 
			{
			if(country.equals(count))
				{
				break;
				}
			focus_index++;
			}
		
        if (locale != null && locale.length() > 1)
			{
                locale = Character.toUpperCase(locale.charAt(0)) + locale.substring(1);
                mLanguageBtn.setText(locale);
            }
			
        mBackBtn.setOnClickListener(new Button.OnClickListener(){

			//@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				ConfigOOBE config = new ConfigOOBE(Language.this);
				String str = config.getPrevActivityName("Language");
				
				Intent intent = new Intent();
				if(str.equals("") == false){
					intent.setClass(Language.this,(Class<?>) config.getActivity(str));
				}
				else {
					intent.setClass(Language.this,OOBE.class);
				}
				startActivity(intent);
				Language.this.finish();
			}
        	
        });
        
        mNextBtn.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v) {
				ConfigOOBE config = new ConfigOOBE(Language.this);
				String str = config.getNextActivityName("Language");
				
				Intent intent = new Intent();
				if(str.equals("") == false){
					intent.setClass(Language.this,(Class<?>) config.getActivity(str));
				}
				else {
					intent.setClass(Language.this,Finish.class);
				}
				startActivity(intent);
				Language.this.finish();
            }
        });
    }

		Button.OnClickListener myShowLanguageDialog =
			new Button.OnClickListener()
		{
			public void onClick(View arg0)
			{
				new AlertDialog.Builder(Language.this)
				.setTitle(R.string.language_set_title)
				.setSingleChoiceItems(R.array.language_choose_dialog, focus_index,
				new DialogInterface.OnClickListener()
				{
				public void onClick(DialogInterface dialog, int item) 
					{
					
	        		String [] language_array = getResources().getStringArray(R.array.language_choose_dialog);
					String format = language_array[item];
	
					SystemProperties.set(prefix + "." + LANGUAGE_SAV, format); 
					mLanguageBtn.setText(format);
					
					String language =language_list[item];
					String country=country_list[item];
					//Log.e("Language","--------------------onClick"+item);
					//Log.e("Language","--------------------language"+language);
					//Log.e("Language","--------------------country"+country);

					try {
						Locale l = new Locale(language, country);
						IActivityManager am = ActivityManagerNative.getDefault();
						Configuration config = am.getConfiguration();
						
						config.locale = l;

		
						// indicate this isn't some passing default - the user wants this remembered
						config.userSetLocale = true;
		
						am.updateConfiguration(config);
						// Trigger the dirty bit for the Settings Provider.
						BackupManager.dataChanged("com.android.providers.settings");
						dialog.dismiss();
		        	}
					catch (RemoteException e) {
						// Intentionally left blank
					}
					}
				})
				.show();
			}
			
		};
		

}
