package com.amlogic.View;

import java.util.List;

import com.amlogic.Listener.AmlogicMenuListener;
import com.amlogic.AmlogicMenu.SearchDrawable;
import com.amlogic.XmlParse.StringItem;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;

public class DialogView extends View {
	private SearchDrawable SearchID ;
	private Bitmap bgbmp = null;
	private Bitmap focusbutton = null;
	private Bitmap unfocusbutton = null;
	private String info=null;
	private String yes=null;
	private String no=null;
	private boolean focusID=false;
	private AmlogicMenuListener mylistener;
	public DialogView(Context context, AttributeSet attrs) {
		super(context, attrs);	
//		this.setFocusable(true);
	}
	
	public void initDialogResource(List<StringItem> stringItems){
		SearchID = new SearchDrawable(this.getContext());
		bgbmp = SearchID.getBitmap("bg_info_content");
		focusbutton = SearchID.getBitmap("button_info_content_sel");
		unfocusbutton = SearchID.getBitmap("button_info_content_unsel");
		getString(stringItems);
	}
	
	private void getString(List<StringItem> stringItems){
		for(StringItem si:stringItems){
			if(si.name.equals("BUTTON_YES"))
				yes=si.value;
			else if(si.name.equals("BUTTON_NO"))
				no=si.value;
			else if(si.name.equals("SET_DEFAULT_INFO"))
				info=si.value;
		}		
	}
    public void setDialogListener( AmlogicMenuListener listener)
    {
    	mylistener = listener;
    }
	
	public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(bgbmp!=null)
        	canvas.drawBitmap(bgbmp, this.getPaddingLeft(), this.getPaddingTop(), null);
        if(focusbutton!=null&&unfocusbutton!=null){
        	if(focusID){
        		canvas.drawBitmap(focusbutton, this.getPaddingLeft()+40, this.getPaddingTop()+200, null);
        		canvas.drawBitmap(unfocusbutton, this.getPaddingLeft()+351, this.getPaddingTop()+200, null);
        	}
        	else{
        		canvas.drawBitmap(focusbutton, this.getPaddingLeft()+351, this.getPaddingTop()+200, null);   
        		canvas.drawBitmap(unfocusbutton, this.getPaddingLeft()+40, this.getPaddingTop()+200, null); 
        	}
        }
        Paint pa = new Paint();
        pa.setColor(Color.WHITE);
		pa.setTextSize(40);
		pa.setAntiAlias(true);
		pa.setTextAlign(Align.CENTER);
		if(info!=null)
			canvas.drawText(info, getPaddingLeft()+351, getPaddingTop()+106, pa);
		if(yes!=null)
			canvas.drawText(yes, getPaddingLeft()+190, getPaddingTop()+280, pa);
		if(no!=null)
			canvas.drawText(no, getPaddingLeft()+501, getPaddingTop()+280, pa);
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		
		switch (keyCode) {
			case KeyEvent.KEYCODE_DPAD_LEFT:
			case KeyEvent.KEYCODE_DPAD_RIGHT:
				if(focusID)
					focusID=false;
				else
					focusID=true;
				this.invalidate();
				break;
			case KeyEvent.KEYCODE_DPAD_CENTER:	
			case KeyEvent.KEYCODE_ENTER:
				if(mylistener != null)
				{
					if(focusID)
						mylistener.DialogManage(true);
					else
						mylistener.DialogManage(false);
				}
				break;
		}	
		
		return super.onKeyDown(keyCode, event);
//		return true;
	}
	

}
