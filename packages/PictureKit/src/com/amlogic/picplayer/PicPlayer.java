/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.amlogic.picplayer;


import android.app.Activity;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.*;
import android.graphics.*;
import android.graphics.Paint.*;
import android.os.Bundle;
import com.amlogic.graphics.*;
import android.view.View;
import android.view.View.OnClickListener;

class MyImageView extends ImageView{
	public MyImageView(Bitmap bm,Context context) {
		super(context);
		fbm=bm;
	}
	
	Bitmap fbm;
	
	protected void onDraw(Canvas canvas) {
		Paint paint=new Paint(); 
		Style style=Style.FILL_AND_STROKE;
		paint.setStyle(style);
		paint.setColor(Color.BLUE);  
		canvas.drawBitmap(fbm, 0, 0, paint);
		super.onDraw(canvas);
	}
}

public class PicPlayer extends Activity
{
	int st=0;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        /* Create a TextView and set its content.
         * the text is retrieved by calling a native
         * function.
         */
        
	
		DecoderInfo di = new DecoderInfo();
		di.image3DPref=1;
		di.image3DParam1=2;
		di.image3DParam2=3;
		Bitmap ii=PictureKit.loadPicture("/sdcard/1.mpo",di);
		if(ii==null) {
			TextView  tv = new TextView(this);
			String strOpt = "decoderInfo:" + "	iw:"+di.imageWidth;
			strOpt += "	ih:"+ di.imageHeight;
			strOpt += "	dw:"+ di.widthToDecoder;
			strOpt += "	dh:"+ di.heightToDecoder;
			strOpt += "	bpp:"+ di.colorMode;
			strOpt += "	pt:"+ di.pictureType;
			strOpt += "\nbut decoding error\n";
			
			tv.setText( strOpt);
			setContentView(tv);
		} else {
			setContentView(new MyImageView(ii,this));
		}
    }

}
