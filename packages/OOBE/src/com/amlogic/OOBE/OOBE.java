package com.amlogic.OOBE;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.util.Log;
import android.view.View;
import android.content.Intent;

public class OOBE extends Activity {
    /** Called when the activity is first created. */
	
	public Button mNextBtn;
	static final String TAG = "OOBESettings";
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        mNextBtn = (Button) findViewById(R.id.btn_main_next);

        mNextBtn.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v) {
				ConfigOOBE config = new ConfigOOBE(OOBE.this);
				String str = config.getFirstActivityName();
				
				Intent intent = new Intent();
				if(str.equals("") == false){
					intent.setClass(OOBE.this,(Class<?>) config.getActivity(str));
				}
				else {
					intent.setClass(OOBE.this,Finish.class);
				}
				startActivity(intent);
				OOBE.this.finish();
           }
        });
    }


}
