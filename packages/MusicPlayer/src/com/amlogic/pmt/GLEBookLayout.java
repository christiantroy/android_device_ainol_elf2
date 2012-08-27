package com.amlogic.pmt;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;
import org.geometerplus.android.fbreader.BookmarksManager;
import org.geometerplus.fbreader.fbreader.ChangeFontSizeAction;
import org.geometerplus.fbreader.fbreader.FBReaderApp;
import org.geometerplus.fbreader.fbreader.TrackballScrollingAction;
import org.geometerplus.fbreader.fbreader.VolumeKeyScrollingAction;
import org.geometerplus.zlibrary.core.application.ZLApplication;
import org.geometerplus.zlibrary.core.library.ZLibrary;
import org.geometerplus.zlibrary.core.options.ZLIntegerOption;
import org.geometerplus.zlibrary.text.view.ZLTextView;
import org.geometerplus.zlibrary.ui.android.R;
import org.geometerplus.zlibrary.ui.android.library.ZLAndroidLibrary;
import org.geometerplus.zlibrary.ui.android.view.ZLAndroidWidget;

import com.amlogic.Listener.MenuCallbackListener;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;

public class GLEBookLayout extends GLBaseLayout implements MenuCallbackListener{
	private ZLAndroidWidget widget;
	protected  updataFilenameListener filenamelistener = null;
	 //位置、缩放定义
	//((ITEM_W/2+ITEM_X)-GL_0ES_X)*SCALE_MIN, (GL_0ES_Y-ITEM_Y-(ITEM_H/2))*SCALE_MIN, 0,
	//0,0,0,0,
	//ITEM_W/VERTEX,ITEM_H/VERTEX,1,
	final static float GL_0ES_X = 960;
	final static float GL_0ES_Y = 540;
	final static float VERTEX = 130;	//正方形的模型宽度(像素)
	final static float SCALE_MIN =  0.001534375f;

	final  int  IDX_TEX_BG				=	0;		//txt bg
	final  int  IDX_BG              	=	1;		
	final  int  IDX_SCROLL_BK           =	2;		//slot for scroll block
	final  int  IDX_SCROLL_BLOCK		=	3;		//slot for current/count
	

	final static float InitPositions[][] = new float[][]{
		{// TXT BG
			0,0,0,	//position
			0,0,0,0,	//Rotation
			192,108,1,		//Scale
		},
		{// BG
			((1920/2+0)-GL_0ES_X)*SCALE_MIN, (GL_0ES_Y-0-(1080/2))*SCALE_MIN, 0,
			0,0,0,0,
			1920/VERTEX,1080/VERTEX,1,
		},
		{// IDX_SCROLL_BK
			((70/2+1830)-GL_0ES_X)*SCALE_MIN, (GL_0ES_Y-80-(950/2))*SCALE_MIN, 0,
			0,0,0,0,
			70/VERTEX,950/VERTEX,1,
		},
		{// IDX_SCROLL_BLOCK
			((90/2+1820)-GL_0ES_X)*SCALE_MIN, (GL_0ES_Y-80-(50/2))*SCALE_MIN, 0,
			0,0,0,0,
			90/VERTEX,50/VERTEX,1,
		},
		
	};
	private void initPosition(){
		for(int idx = 0; idx <= IDX_SCROLL_BLOCK; idx++ ){
			DisplaySlot slt = new DisplaySlot("", ""); 
			slt.setPosition(InitPositions[idx][0], InitPositions[idx][1], InitPositions[idx][2]);
			slt.setRotation(InitPositions[idx][3], InitPositions[idx][4], InitPositions[idx][5], InitPositions[idx][6]);
			slt.setScale(InitPositions[idx][7], InitPositions[idx][8], InitPositions[idx][9]);
			addItem(slt);
		}
	}
	public GLEBookLayout(Context context, String n, String location) {
		super(context, n, location);
		initPosition();
		getItem(IDX_TEX_BG).SetBitmap(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.bg_txt));
		getItem(IDX_SCROLL_BK).SetBitmap(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.scroll_vert_long_bg));
		getItem(IDX_SCROLL_BLOCK).SetBitmap(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.scroll_vert_short_light));
		getItem(IDX_SCROLL_BK).setVisible(false);
		getItem(IDX_SCROLL_BLOCK).setVisible(false);
		// TODO Auto-generated constructor stub

	}
	
	private void UpdateScroll(int count,int total) {
		DisplaySlot sltBlock = getItem(IDX_SCROLL_BLOCK);
	
		if(count == 0)
			count = 1;
		if(total == 0)
			return;
		count-=1;
		float ITEM_H = 910.00f /total;
		float ITEM_Y = 0;
		if(ITEM_H < 20)
		{
			ITEM_H = 20;
			ITEM_Y = 890.00f *count/total + 100;
		}
		else
			ITEM_Y = ITEM_H *count + 100;
			
		float curscal =ITEM_H/VERTEX;
		if(ITEM_Y + ITEM_H > 1010f)
			ITEM_Y = ITEM_Y - ITEM_H;

		
		float pos = (GL_0ES_Y-ITEM_Y-(ITEM_H/2))*SCALE_MIN;
		sltBlock.setScale(InitPositions[IDX_SCROLL_BLOCK][7], curscal, InitPositions[IDX_SCROLL_BLOCK][9]);
		sltBlock.setPosition(InitPositions[IDX_SCROLL_BLOCK][0], pos, InitPositions[IDX_SCROLL_BLOCK][2]);
		sltBlock.setVisible(true);
		getItem(IDX_SCROLL_BK).setVisible(true);
	}

	int nextPageTexID = -1;
	int curPageTexID = -1;
	//Override
	public synchronized void drawFrame(GL10 gl) {
		// TODO Auto-generated method stub
		gl.glPushMatrix();
		gl.glDisable(GL11.GL_DEPTH_TEST);
		
		setCamera(gl);

		if(widget == null){
			widget = ((ZLAndroidLibrary)ZLibrary.Instance()).getWidget();
		}
		else if(widget.updatedPageAvailable && widget.drawFinish){
			Bitmap pageBmp = widget.getMainBitmap();
			if(pageBmp != null){
//				Log.i("GLEBookLayout", "loadTextureOES() -> nextPageTexID");
//				nextPageTexID = TextureManager.loadTextureOES(gl, pageBmp);
				synchronized(slots)
				{
					DisplaySlot sltBg = getItem(IDX_BG);
					sltBg.SetBitmap(pageBmp.copy(pageBmp.getConfig(), true), false);
					sltBg.setVisible(true);
				}
			}
			widget.updatedPageAvailable = false;
		    widget.drawFinish           = false;
			postUpdateMessage();
		}
		
		drawSlot(gl);
		drawEffects(gl);
		gl.glEnable(GL11.GL_DEPTH_TEST);
		gl.glPopMatrix();
	}
	
	//Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		switch (keyCode) {
		case KeyEvent.KEYCODE_DPAD_UP:
			postDelayKEYMessage(KeyEvent.KEYCODE_DPAD_UP)	;
		return true;
		case KeyEvent.KEYCODE_DPAD_DOWN:
			postDelayKEYMessage(KeyEvent.KEYCODE_DPAD_DOWN)	;
		return true;
		case KeyEvent.KEYCODE_DPAD_LEFT:
			postDelayKEYMessage(KeyEvent.KEYCODE_DPAD_LEFT)	;

		return true;
		case KeyEvent.KEYCODE_DPAD_RIGHT:
			postDelayKEYMessage(KeyEvent.KEYCODE_DPAD_RIGHT)	;
		return true;
		}
		return false;
	}

	
	
	public void CallbackMenuState(String... State)
    {
		if(State[0].equals("BackTo3D")){
			if(filenamelistener != null )
				filenamelistener.BackTo3D();
			return;
		}
		
		if(!State[0].equals("Text"))
			return;
		final FBReaderApp fbReader = (FBReaderApp)ZLApplication.Instance();
    	if(State[1].equals("shortcut_txt_fontsize_big"))
    	{
    		new ChangeFontSizeAction(fbReader,45).run();
    	}
    	else
		if(State[1].equals("shortcut_txt_fontsize_mid"))
    	{
    		 new ChangeFontSizeAction(fbReader,40).run();
    	}
		else
		if(State[1].equals("shortcut_txt_fontsize_small"))
    	{
    	    new ChangeFontSizeAction(fbReader,35).run();
    	}
		else
		if(State[1].equals("shortcut_common_stop_"))
    	{
			if(filenamelistener != null )
	    	{
	    		filenamelistener.stopplayer("Text");
	    	}
			
    	}
		else
		if(State[1].equals("shortcut_txt_turn_page_next_10p"))
    	{
			new VolumeKeyScrollingAction(fbReader,true,10).run1();
    	}
	
		else
		if(State[1].equals("shortcut_txt_turn_page_next_20p"))
    	{
			new VolumeKeyScrollingAction(fbReader,true,20).run1();
    	}
		else
		if(State[1].equals("shortcut_txt_turn_page_next_50p"))
    	{
			new VolumeKeyScrollingAction(fbReader,true,50).run1();
    	}
		else
		if(State[1].equals("shortcut_txt_turn_page_prve_10p"))
    	{
			new VolumeKeyScrollingAction(fbReader,false,-10).run1();
    	}
	
		else
		if(State[1].equals("shortcut_txt_turn_page_prve_20p"))
    	{
			new VolumeKeyScrollingAction(fbReader,false,-20).run1();
    	}
		else
		if(State[1].equals("shortcut_txt_turn_page_prve_50p"))
    	{
			new VolumeKeyScrollingAction(fbReader,false,-50).run1();
    	}
    	
		else
		if(State[1].equals("shortcut_txt_breakpoint_first"))
    	{
			new ZLIntegerOption("BookMark", "flag", 0).setValue(0);
    	}
		else  
		if(State[1].equals("shortcut_txt_breakpoint_breakpoint"))
    	{
			new ZLIntegerOption("BookMark", "flag", 0).setValue(1);
    	}
		else
		if(State[1].equals("shortcut_txt_prev_"))
    	{
//			 new FindNextAction(fbReader).run();
			new VolumeKeyScrollingAction(fbReader, false).run();
    	}
    	else
		if(State[1].equals("shortcut_txt_next_"))
    	{
			new VolumeKeyScrollingAction(fbReader,true ).run();
    	}
		else 
		if(State[1].equals("shortcut_common_sync_play_music")){
			if(filenamelistener != null )
	    	{
	    		filenamelistener.CallbackRelevance("Text","Audio");
	    	}
		}
		else 
		if(State[1].equals("shortcut_common_sync_play_picture")){
			if(filenamelistener != null )
	    	{
	    		filenamelistener.CallbackRelevance("Text","Picture");
	    	}   
		}
		else 
		if(State[1].equals("shortcut_common_sync_play_txt")){
			if(filenamelistener != null )
	    	{
	    		filenamelistener.CallbackRelevance("Text","Text");
	    	}
		}
		else if(State[1].equals("shortcut_common_sync_control_txt")){
			if(filenamelistener != null )
	    	{
	    		filenamelistener.CallbackName("txt", "Nothing");
	    		filenamelistener.CallbackPosScale("txt","Nothing");
	    	}
		}
			
    }
	public void setFilenameListener(updataFilenameListener listener)
	{
		  filenamelistener = listener;
	}
	
	//Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		return false;
	}

	//Override
	public void setSlotsLayout() {
		// TODO Auto-generated method stub

	}

	//Override
	public synchronized void onstop()
	{
	}

	//Override
	public void startAutoPlay() {
		// TODO Auto-generated method stub
		
	}

	//Override
	public void stopAutoPlay() {
		// TODO Auto-generated method stub
	}
	
	public synchronized void delLayoutTextures() {
		// TODO Auto-generated method stub
		super.delLayoutTextures();
		
		if(nextPageTexID > 0){
			TextureManager.delTexture(nextPageTexID);
			nextPageTexID = -1;
		}
		if(curPageTexID > 0){
			TextureManager.delTexture(curPageTexID);
			curPageTexID = -1;
		}
	
	}
	public void setBookmark()
	{
		int flag = new ZLIntegerOption("BookMark", "flag", 0).getValue();
		if(flag == 1)
			new BookmarksManager().addBookmark();
	}

	
	private Handler handlerDelayKEYText = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case 1:
					switch (msg.arg1) {
						case KeyEvent.KEYCODE_DPAD_UP:
							new TrackballScrollingAction((FBReaderApp)ZLApplication.Instance(), false).run();
							break;
						
						case KeyEvent.KEYCODE_DPAD_DOWN:
							new TrackballScrollingAction((FBReaderApp)ZLApplication.Instance(), true).run();
							break;
						
						case KeyEvent.KEYCODE_DPAD_LEFT:
							new VolumeKeyScrollingAction((FBReaderApp)ZLApplication.Instance(), false).run();
							break;
						case KeyEvent.KEYCODE_DPAD_RIGHT:
							new VolumeKeyScrollingAction((FBReaderApp)ZLApplication.Instance(), true).run();
							break;
					}	
				break;	
				case 2:
					final ZLTextView textView = (ZLTextView) ZLApplication.Instance().getCurrentView();
					final int page = textView.computeCurrentPage();
					final int pagesNumber = textView.computePageNumber();
					UpdateScroll(page,pagesNumber);
					
				break;	
		   }	
		}
	};
	
	private void postDelayKEYMessage(int key){
		handlerDelayKEYText.removeMessages(1);
		Message msg = handlerDelayKEYText.obtainMessage(1);
		msg.arg1 = key;
		handlerDelayKEYText.sendMessageDelayed(msg, 250);
	}
	private void postUpdateMessage(){
		handlerDelayKEYText.removeMessages(2);
		Message msg = handlerDelayKEYText.obtainMessage(2);
		handlerDelayKEYText.sendMessageDelayed(msg, 1300);
	}
	
	
	

}
