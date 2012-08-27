package com.amlogic.OOBE;


import java.io.IOException;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.util.Log;
import android.view.View;
import android.content.Intent;
import android.os.SystemProperties;


import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FileNotFoundException;
import com.amlogic.OOBE.SelectFlag;


public class Finish extends Activity {
    /** Called when the activity is first created. */
	private final static String sel_dispaly_mode = "oobeflag";
	private static final String STR_OUTPUT_MODE = "ubootenv.var.outputmode";
	private final static String TAG = "Finish";
	public Button mDoneBtn;
	public Button mBackBtn;

	private FileOutputStream fos;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.finish);
        
		mDoneBtn = (Button) findViewById(R.id.btn_finish_done);
        mBackBtn = (Button) findViewById(R.id.btn_finish_back);
        
        mDoneBtn.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v) {
				//SystemProperties.set(prefix + "." + STR_OUTPUT_MODE, "setted");
				
				try
					{
					byte[] setflag = new byte[20];
	                String setflagValues = "1 1 0 0 1 0 1" + "\n";
	                setflag = setflagValues.getBytes();
	                fos = Finish.this.openFileOutput("outputmode_set", MODE_WORLD_READABLE);
	                fos.write(setflag);

					String sel_outputmode = SelectFlag.getSelect();
					if(sel_outputmode.equals(""))
						{
						sel_outputmode="720p";
						Log.e(TAG, "Get selected outpumode failed");
						}
					SystemProperties.set(STR_OUTPUT_MODE,sel_outputmode);
					int m1080scale = SystemProperties.getInt("ro.platform.has.1080scale", 0);
					if(m1080scale != 2){
					SystemProperties.set("ctl.start", "display_reset");
					String ret = SystemProperties.get("init.svc.display_reset", "");
					if(ret != null && ret.equals("stopped"))
					{
						  Log.i(TAG,"--------------------------------reboot android");
						  //return true;
						}
					}
					else{
						Finish.this.finish();
					}
					}
				catch (Exception e)
					{
	                Log.e(TAG, "Exception Occured: Trying to add set setflag : " +
	                        e.toString());
	                Log.e(TAG, "Finishing the Application");
	                Finish.this.finish();
					}
            }
        });

		mBackBtn.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v) {
				ConfigOOBE config = new ConfigOOBE(Finish.this);
				String str = config.getLastActivityName();

				Intent intent = new Intent();
				if(str.equals("") == false){
					intent.setClass(Finish.this,(Class<?>) config.getActivity(str));
				}
				else {
					intent.setClass(Finish.this,OOBE.class);
				}
				
				startActivity(intent);
				Finish.this.finish();
            }
        });
    }
}
