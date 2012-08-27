package com.amlogic.View;


import com.amlogic.Listener.AmlogicMenuListener;
import com.amlogic.AmlogicMenu.SearchDrawable;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;

public class ProgressBarView  extends View {
	
	private Bitmap bg;
	private Bitmap bar;
	private Bitmap focus=null,newfocus = null;
	private Bitmap tail=null;
	private int progress=0;
	private AmlogicMenuListener ProgressListener=null;
	private String MenuItemName="";
	private SearchDrawable SearchID ;
	private Bitmap BgBtp = null;
	private Bitmap typebmp= null;
	private int leftOffset=480;
	private int topOffset=55;
	private int MType= MenuGroup.noplayer;
	private Paint pa = new Paint();
	public ProgressBarView(Context context, AttributeSet attrs, String MenuItemName,int MenuType) {
		super(context, attrs);
//		this.setFocusable(true);
		this.MenuItemName = MenuItemName;
		init();
		MType=MenuType;
		setBgBtp(MenuType);
		this.setFocusable(true);
		
		pa.setColor(Color.WHITE);
		pa.setTextSize(30);
		pa.setAntiAlias(true);
		
	}	

	public void init(){
		
		SearchID = new SearchDrawable(this.getContext());
		bg = SearchID.getBitmap("bar_sc_slide_bg");
		bar = SearchID.getBitmap("bar_sc_slide_element");
		BgBtp = SearchID.getBitmap("shortcut_bg_bar_info");
		typebmp= SearchID.getBitmap(MenuItemName+"unsel");
	 
		focus=Bitmap.createBitmap(bar, 0, 0, 10, 41);
		tail=Bitmap.createBitmap(bar, 10, 0, 10, 41);
	}
	
	public void setBgBtp(int Type){
		int BGhight = 0;
		String BGName ; 
		if (Type == MenuGroup.noplayer){
			BGhight = 152;
			BGName="shortcut_bg_bar";
		}
		else{
			BGhight = 238;
			BGName="shortcut_bg_bar_info";
		}
		BgBtp = SearchID.getBitmap(BGName);
	}
	
	public void setProgress(int num){
		progress=num;
	}
	public int getProgress(){
		return progress;
	}
	public void setProgressBarListener(AmlogicMenuListener SML)
	{
		ProgressListener = SML;
	}
	
	
	public void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);

		if((focus==null)||(tail==null)||(bg==null))
			return;
		 if(BgBtp != null)
			 canvas.drawBitmap(BgBtp, this.getPaddingLeft(), this.getPaddingTop(), null);
		 if(typebmp != null)
			 canvas.drawBitmap(typebmp, this.getPaddingLeft()+330, this.getPaddingTop(), null);
		
		 
		
		 canvas.drawBitmap(bg, getPaddingLeft()+leftOffset, getPaddingTop()+topOffset, null);
		if(newfocus != null)
			 newfocus.recycle();
		if(progress > 0)
		{
			newfocus= Bitmap.createScaledBitmap(focus, progress*94/10, 41, true); 
			canvas.drawBitmap(newfocus, getPaddingLeft()+leftOffset+10, getPaddingTop()+topOffset, null);
		}
			 
		
		canvas.drawBitmap(tail, getPaddingLeft()+(progress)*94/10+leftOffset, getPaddingTop()+topOffset, null);
		
		if(MenuItemName.equals("shortcut_setup_audio_balance_"))
			canvas.drawText(String.valueOf(progress-50), getPaddingLeft()+960+leftOffset, getPaddingTop()+30+topOffset, pa);
		else
		canvas.drawText(""+progress, getPaddingLeft()+960+leftOffset, getPaddingTop()+30+topOffset, pa);
		
		
	}	

//	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		super.onKeyDown(keyCode, event);
		switch (keyCode) {
		case 0xCF:
		case 0xCE:
		case 0xCD:
		case 0xCC:
		case 0xCB:
		case 0xCA:
		case 0xC9:
		case 0xC8:
		case 0xC7:
		case 0xC6:
		case 0xC5:
		case 0xC4:
		case 0xC3:
		case 0xC2:
		case 0xC1:
			KeyOp(KeyEvent.KEYCODE_DPAD_LEFT);
			break;
		case 0xFF:
		case 0xFE:
		case 0xFD:
		case 0xFC:
		case 0xFB:
		case 0xFA:
		case 0xF9:
		case 0xF8:
		case 0xF7:
		case 0xF6:
		case 0xF5:
		case 0xF4:
		case 0xF3:
		case 0xF2:
		case 0xF1:
			KeyOp(KeyEvent.KEYCODE_DPAD_RIGHT);
			break;
			
		case KeyEvent.KEYCODE_DPAD_UP:
		case KeyEvent.KEYCODE_DPAD_DOWN:
			break;

	    case 115:
			if(!MenuItemName.equals("shortcut_common_vol_"))
				break;
		case KeyEvent.KEYCODE_DPAD_LEFT:
			KeyOp(KeyEvent.KEYCODE_DPAD_LEFT);
			break;
		case 114:
			if(!MenuItemName.equals("shortcut_common_vol_"))
				break;
		case KeyEvent.KEYCODE_DPAD_RIGHT:
			KeyOp(KeyEvent.KEYCODE_DPAD_RIGHT);
			break;
		case KeyEvent.KEYCODE_BACK:	
			onKeyDown(KeyEvent.KEYCODE_ENTER,event);
			return true;
		case KeyEvent.KEYCODE_DPAD_CENTER:	
		case KeyEvent.KEYCODE_ENTER:
			if(ProgressListener!=null)
				ProgressListener.ProgressBarToMenuHandle(progress,false,MenuItemName);
			break;
		case KeyEvent.KEYCODE_MENU:
			if(ProgressListener!=null)
				ProgressListener.ProgressBarToMenuHandle(progress,false,"__Nothing__");
			break;
		
		}
		return super.onKeyDown(keyCode, event);
	}
	
	private  void KeyOp( int keyCode )
	{
		if(keyCode == KeyEvent.KEYCODE_DPAD_LEFT)
		{
			if(progress>0){
				progress--;
				if (MType == MenuGroup.noplayer){
					
					this.invalidate(leftOffset, topOffset, leftOffset+1010, topOffset+41);
				}
				else{
					
					this.invalidate(leftOffset, topOffset, leftOffset+1010, topOffset+41);
				}
				if(ProgressListener!=null)
					ProgressListener.ProgressBarToMenuHandle(progress,true,MenuItemName);
			}
		}
		else
		if(keyCode == KeyEvent.KEYCODE_DPAD_RIGHT)
		{
			if(progress<100){
				progress++;
				if (MType == MenuGroup.noplayer){
					
					this.invalidate(leftOffset, topOffset, leftOffset+1010, topOffset+41);
				}
				else{
					
					this.invalidate(leftOffset, topOffset, leftOffset+1010, topOffset+41);
				}
				if(ProgressListener!=null)
					ProgressListener.ProgressBarToMenuHandle(progress,true,MenuItemName);
			}
		}
		
	}
}
