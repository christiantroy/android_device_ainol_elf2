package com.amlogic.pmt;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.geometerplus.android.fbreader.BookmarksManager;
import org.geometerplus.android.fbreader.FBReader;
import org.geometerplus.zlibrary.core.options.ZLIntegerOption;
import org.geometerplus.zlibrary.ui.android.R;
import org.geometerplus.zlibrary.ui.android.library.ZLAndroidActivity;

import com.amlogic.pmt.menu.MenuOp;
import com.amlogic.pmt.music.spectrumOp;
import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import com.amlogic.pmt.MusicPlayer;
import android.os.RemoteException;

public class RelevanceOp  implements updataFilenameListener{
	private RenderView mGLRenderView = null;
	public final static int MSG_PMTPLAY_TYPE_PICTURE = 1;
	public final static int MSG_PMTPLAY_TYPE_MUSIC = 2;
	public final static int MSG_PMTPLAY_TYPE_TEXT = 3;
	public final static int MSG_PMTPLAY_TYPE_VIDEO = 4;
	public final static int MSG_PMTPLAY_TYPE_MUSIC_CONTINUE = 5;
	private  ArrayList<String> musicList = new ArrayList<String>();
	private  ArrayList<String> picList = new ArrayList<String>();
	private String TextName = null;
	private MenuOp MymenuOp = null ;
	private String backFileName = null;
	private String lastFilebrowserType = null;
	private RelevanceState relevanceState;
	public  boolean menuStopFlag = false;
	private spectrumOp  specOp = null;
	private Activity myactivity = null;
	private String picRelevanceID = null;
	private IGLMusicService musicService=null;
	public RelevanceOp(Activity activity )
	{
		myactivity = activity;
		relevanceState = new RelevanceState();		
		specOp = new spectrumOp();		
		
	}

	public void BackTo3D() 
	{	
		if(myactivity!=null)
			myactivity.onKeyDown(113, new KeyEvent(KeyEvent.ACTION_DOWN, 113));
	}
	
	public void CallDelayHideMenu() 
	{
		if(myactivity!=null){
			((ZLAndroidActivity)myactivity).postClearOsdMessage();
		}
	}
	
	public void setRenderView(RenderView glRenderView)
	{
		mGLRenderView = glRenderView;
	}
	public void setMenuOpInstance(MenuOp menuOp)
	{
		MymenuOp = menuOp;
	}
	
	public static int getPlayHandlerWhat(String filetype) {
		if(filetype.equals("Picture"))
			return MSG_PMTPLAY_TYPE_PICTURE;
		
		if(filetype.equals("Audio"))
			return MSG_PMTPLAY_TYPE_MUSIC;
		
		if(filetype.equals("Audio_Show"))
			return MSG_PMTPLAY_TYPE_MUSIC_CONTINUE;
	
		if(filetype.equals("Text"))
			return MSG_PMTPLAY_TYPE_TEXT;
			
		if(filetype.equals("Video"))
			return MSG_PMTPLAY_TYPE_VIDEO;	
		
		return 0;
	}
	
	private Handler handlerPMTPlay = new Handler() {
		// Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				picList = mGLRenderView.GetGridLayoutInstance().gridGetPlayList();
				String filename = mGLRenderView.GetGridLayoutInstance().gridGetPlayFileName();
				PlayPicture(picList,filename);
				mGLRenderView.uninitGridLayout();
				relevanceState.setPlayState("Picture", true);
				
				if(relevanceState.getPlayState("Text"))
				{
					PlayRelevanceTxt(TextName);//	PlayTxt(TextName);
				
				}
				break;

			case 2:

				musicList = mGLRenderView.GetGridLayoutInstance().gridGetPlayList();
			    filename = mGLRenderView.GetGridLayoutInstance().gridGetPlayFileName();
				mGLRenderView.uninitGridLayout();
				PlayMusic(musicList,filename);
				relevanceState.setPlayState("Audio", true);
			
				if(relevanceState.getPlayState("Text"))
				{
					PlayRelevanceTxt(TextName);//	PlayTxt(TextName);
					mGLRenderView.GetMusicLayoutInstance().HideBg();
				}
				if(relevanceState.getPlayState("Picture"))
				{
					if(picRelevanceID != null )
						PlayPicture(picList,picRelevanceID);
					else
						PlayPicture(picList,picList.get(0));
		    		mGLRenderView.GetMusicLayoutInstance().HideBg();
					
				}
				
				if(getMenuType().equals("Music"))
				{
					addSpectrum();
//					MymenuOp.ShowMenu(getMenuType());
					mGLRenderView.GetMusicLayoutInstance().ShowBg();
					RelevanceOp.this.CallbackName("music",RelevanceOp.this.getCurMusicName());
					RelevanceOp.this.CallbackPosScale("music","Nothing");
				}
				break;
			case 3:
			    filename = mGLRenderView.GetGridLayoutInstance().gridGetPlayFileName();
				mGLRenderView.uninitGridLayout();
				PlayTxt(filename);
				relevanceState.setPlayState("Text", true);
				
				TextName = filename;
				
				if(relevanceState.getPlayState("Picture"))
				{
					if(picRelevanceID != null )
						PlayPicture(picList,picRelevanceID);
					else
						PlayPicture(picList,picList.get(0));
				}
				break;
			case 4:
				break;
			case 5:
					mGLRenderView.uninitGridLayout();
					relevanceState.setPlayState("Audio", true);
					addSpectrum();
					mGLRenderView.GetMusicLayoutInstance().ShowBg();
					RelevanceOp.this.CallbackName("music",RelevanceOp.this.getCurMusicName());
					RelevanceOp.this.CallbackPosScale("music","Nothing");
				break;
			}
			super.handleMessage(msg);
		}
	};
	
	public void BackBrowser(String filetype)
	{
		removeSpectrum();
		if(mGLRenderView == null)
			return;
		if(mGLRenderView.GetGridLayoutInstance() == null)
			mGLRenderView.initGridLayout(false);
		
		mGLRenderView.GetGridLayoutInstance().setFileType(filetype);
		mGLRenderView.GetGridLayoutInstance().setBgResId(R.drawable.bg);
		mGLRenderView.GetGridLayoutInstance().setPlayHandler(handlerPMTPlay);
		String flName = null;
		File fl = null;
		if(filetype.equals("Text"))
		{
			if(TextName != null)
			 fl = new File(TextName);
			 flName = TextName;
		}else
		{
			if(backFileName != null){
				 fl = new File(backFileName);
				 flName = backFileName;
			}
		}
		if(fl !=  null){
			mGLRenderView.GetGridLayoutInstance().enterFileBrowser(fl.getParent(), flName);
//			mGLRenderView.GetGridLayoutInstance().focusItemByAName(flName);
		}else
			mGLRenderView.GetGridLayoutInstance().enterDeviceBrowser();	
	}	
	
	public void PlayBrowser(String filetype)
	{
		if(mGLRenderView == null)
			return;
		if(mGLRenderView.GetGridLayoutInstance() == null)
			mGLRenderView.initGridLayout(true);
		lastFilebrowserType = filetype;
		mGLRenderView.GetGridLayoutInstance().setFileType(filetype);
		mGLRenderView.GetGridLayoutInstance().setBgResId(R.drawable.bg);
		mGLRenderView.GetGridLayoutInstance().setPlayHandler(handlerPMTPlay);
		mGLRenderView.GetGridLayoutInstance().enterDeviceBrowser();
	}

	public void PlayFile(String filetype,String path)
	{

		if(filetype.equals("Picture"))
			{
				picList.add(path);
				String filename = path;
				lastFilebrowserType = filetype;
				PlayPicture(picList,filename);
				relevanceState.setPlayState("Picture", true);
				
				if(relevanceState.getPlayState("Text"))
				{
					PlayRelevanceTxt(TextName);//	PlayTxt(TextName);
				
				}
			}
		else if(filetype.equals("Audio"))
			{
				musicList.add(path);
			    String filename = path;
				lastFilebrowserType = filetype;
				mGLRenderView.uninitGridLayout();
				PlayMusic(musicList,filename);
				relevanceState.setPlayState("Audio", true);
			
				if(relevanceState.getPlayState("Text"))
				{
					PlayRelevanceTxt(TextName);//	PlayTxt(TextName);
					mGLRenderView.GetMusicLayoutInstance().HideBg();
				}
				if(relevanceState.getPlayState("Picture"))
				{
					if(picRelevanceID != null )
						PlayPicture(picList,picRelevanceID);
					else
						PlayPicture(picList,picList.get(0));
		    		mGLRenderView.GetMusicLayoutInstance().HideBg();
					
				}
				
				if(getMenuType().equals("Music"))
				{
					addSpectrum();
//					MymenuOp.ShowMenu(getMenuType());
					mGLRenderView.GetMusicLayoutInstance().ShowBg();
					RelevanceOp.this.CallbackName("music",RelevanceOp.this.getCurMusicName());
					RelevanceOp.this.CallbackPosScale("music","Nothing");
				}
			}
	}

		
	
	public void StopBrowser()
	{
		mGLRenderView.uninitGridLayout();
	}
	
	public void PlayPicture(ArrayList<String> picList,String filename)
	{	
		if(mGLRenderView.GetPictureLayoutInstance() == null)
			mGLRenderView.initPictureLayout();
		mGLRenderView.GetPictureLayoutInstance().setFileList(picList);
		mGLRenderView.GetPictureLayoutInstance().setFirstFileName(filename);
		mGLRenderView.GetPictureLayoutInstance().setFilenameListener(this);
		mGLRenderView.GetPictureLayoutInstance().showFirstSlot();
        mGLRenderView.GetPictureLayoutInstance().startAutoPlay();
	}
	
	public void PlayMusic(ArrayList<String> musicList,String filename)
	{
		if(mGLRenderView.GetMusicLayoutInstance() == null)
			mGLRenderView.initMusicLayout();
		else
			mGLRenderView.GetMusicLayoutInstance().resetSong();;
		mGLRenderView.GetMusicLayoutInstance().setFileList(musicList);
		mGLRenderView.GetMusicLayoutInstance().setFirstFileName(filename);
		if(musicService==null)
			{
			musicService=MusicPlayer.getMediaPlayerService();
			}
		setIsMusicLayoutExist(true);
		setFileList(musicList);
		setFirstFileName(filename);
		mGLRenderView.GetMusicLayoutInstance().setFilenameListener(this);
		mGLRenderView.GetMusicLayoutInstance().startAutoPlay();

	}
	public void StopPicture()
	{
		mGLRenderView.uninitPictureLayout();
	}
	
	public void StopMusic()
	{
		mGLRenderView.uninitMusicLayout();
		setIsMusicLayoutExist(false);
	}
	
	public void PlayTxt(String textName)
	{
		FBReader.Instance.playTxt(textName,false);
//		postDelayShowTextMessage();
		if(mGLRenderView.GetTxtLayoutInstance() == null)
			mGLRenderView.initTxtLayout();
		mGLRenderView.GetTxtLayoutInstance().setFilenameListener(RelevanceOp.this);
	}
	
	public void PlayRelevanceTxt(String textName)
	{
		FBReader.Instance.playTxt(textName,true);
//		postDelayShowTextMessage();
		if(mGLRenderView.GetTxtLayoutInstance() == null)
			mGLRenderView.initTxtLayout();
		mGLRenderView.GetTxtLayoutInstance().setFilenameListener(RelevanceOp.this);
	}
	
	public void StopTxt()
	{
		mGLRenderView.GetTxtLayoutInstance().setBookmark();
		mGLRenderView.uninitTxtLayout();
	}
	
	public void handleBack()
	{
		
	}

	public String getMenuType()
	{
		int playRelated = 0;
		String info = "";
	
		if(relevanceState.getPlayState("Text"))		
			playRelated += 1<<1;
		if(relevanceState.getPlayState("Picture"))	
			playRelated += 1<<2;
		if(relevanceState.getPlayState("Audio"))	
			playRelated += 1<<3;
		
		if(playRelated == ( (1<<1) +(1<<2) + (1<<3) ))
			info = "MusPT";
		else
		if(playRelated ==  ((1<<2) + (1<<3)))
			info = "MusPic";
		else
		if(playRelated == ((1<<1) + (1<<3)))
			info = "MusT";
		else
		if(playRelated == ((1<<1) + (1<<2))) 
			info = "PicT";
		else
		if(playRelated == (1<<1))
			info = "Txt";
		else
		if(playRelated == (1<<2))
			info = "Picture";
		else
		if(playRelated == (1<<3))
			info = "Music";
		return info;
	}
	
	private Handler handlerCallbackMusicState = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
		    	if(MymenuOp.getMenuInstance() != null ){
					int total = msg.arg1;
					int cur = msg.arg2;
		    		MymenuOp.getMenuInstance().set_seek_bar_info(total, cur);
		    	}
				break;
			default:
				break;
			}
		}
	};
	
	public void CallbackMusicState(final int total,final int cur)
     {
//    	if(MymenuOp.getMenuInstance() != null &&MymenuOp.getMenuInstance().getVisibility() == View.VISIBLE)
//    	{
//    		myActivity.runOnUiThread(new Runnable(){
//				public void run() {	
//					if(MymenuOp.getMenuInstance() != null)
//					MymenuOp.getMenuInstance().set_seek_bar_info(total, cur);
//				}
//			});
//    	}
		Message msg = new Message();
		msg.what = 1;
		msg.arg1 = total;
		msg.arg2 = cur;
		Log.i("RelevanceOp","-----------total"+total);
		handlerCallbackMusicState.sendMessage(msg);
    }
	    
    public void ReturnRelevance()
    {
    	Log.i("RelevanceOp", "--> ReturnRelevance");
    	String CurPlayType = MymenuOp.getPlayType();
    	MymenuOp.DestoryMenu();
    	
    	int count = relevanceState.getStateCount();
    	if(count == 1)
    	{
	    		if(!(lastFilebrowserType.equals("Picture")&&relevanceState.getPlayState("Picture")||
	    		   lastFilebrowserType.equals("Text")&&relevanceState.getPlayState("Text")||
				   lastFilebrowserType.equals("Audio")&&relevanceState.getPlayState("Audio")))
				   {
	    			if(relevanceState.getPlayState("Picture"))
	    				lastFilebrowserType = "Picture";
	    			else
					if(relevanceState.getPlayState("Text"))
	    				lastFilebrowserType = "Text";
	    			else
	        			lastFilebrowserType = "Audio";
	            			
				   }
    	}
    	else
		if(count == 2)
    	{
				if(CurPlayType  != null)
				{
					if(CurPlayType.equals("txt"))
					{
						if(relevanceState.getPlayState("Text"))
							lastFilebrowserType = "Text";
					}
					else
					if(CurPlayType.equals("picture"))
					{
						if(relevanceState.getPlayState("Picture"))
							lastFilebrowserType = "Picture";
					}
					else
					if(CurPlayType.equals("music"))
					{
						if(relevanceState.getPlayState("Audio"))
							lastFilebrowserType = "Audio";
					}		
				}
				else
				{
					if(!(lastFilebrowserType.equals("Picture")&&relevanceState.getPlayState("Picture")||
				    		   lastFilebrowserType.equals("Text")&&relevanceState.getPlayState("Text")||
							   lastFilebrowserType.equals("Audio")&&relevanceState.getPlayState("Audio")))
				    {
		    			 if(relevanceState.getPlayState("Picture"))
		    			 	lastFilebrowserType = "Picture";
		    			 else
						 if(relevanceState.getPlayState("Text"))
		    			 	lastFilebrowserType = "Text";
		    			 else
		        		 	lastFilebrowserType = "Audio";
					}
					else
					{
						if(relevanceState.getPlayState("Audio"))
							lastFilebrowserType = "Audio";
						else
						if(relevanceState.getPlayState("Picture"))
							lastFilebrowserType = "Picture";
						else
							lastFilebrowserType = "Text";
						
					}
				}
    	}else
		if(count == 3)
		{
			if(CurPlayType  != null)
			{
				if(CurPlayType.equals("txt"))
				{
					if(relevanceState.getPlayState("Text"))
						lastFilebrowserType = "Text";
				}
				else
				if(CurPlayType.equals("picture"))
				{
					if(relevanceState.getPlayState("Picture"))
						lastFilebrowserType = "Picture";
				}
				else
				if(CurPlayType.equals("music"))
				{
					if(relevanceState.getPlayState("Audio"))
						lastFilebrowserType = "Audio";
				}		
			}
		}
		if(lastFilebrowserType.equals("Picture"))
		{
			if(mGLRenderView.GetPictureLayoutInstance() != null){
				backFileName = mGLRenderView.GetPictureLayoutInstance().dataProvider.getCurFilePath();
	    		mGLRenderView.GetPictureLayoutInstance().stopAutoPlay();
	    		relevanceState.setPlayState("Picture", false);
			}
			
			if(mGLRenderView.GetTxtLayoutInstance() != null){
	    		mGLRenderView.GetTxtLayoutInstance().stopAutoPlay();
	    		relevanceState.setPlayState("Text", true);
			}
		}
		else	
		if(lastFilebrowserType.equals("Audio"))
		{		
			if(mGLRenderView.GetMusicLayoutInstance() != null){
				backFileName = mGLRenderView.GetMusicLayoutInstance().dataProvider.getCurFilePath();
				removeSpectrum();
	    		mGLRenderView.GetMusicLayoutInstance().stopAutoPlay();
				relevanceState.setPlayState("Audio", false);
				mGLRenderView.uninitMusicLayout();
				setIsMusicLayoutExist(false);
			}
			
			if(mGLRenderView.GetPictureLayoutInstance() != null){
	    		mGLRenderView.GetPictureLayoutInstance().stopAutoPlay();
	    		relevanceState.setPlayState("Picture", true);
			}
			
			if(mGLRenderView.GetTxtLayoutInstance() != null){
	    		mGLRenderView.GetTxtLayoutInstance().stopAutoPlay();
	    		relevanceState.setPlayState("Text", true);
			}
		}
		else
		if(lastFilebrowserType.equals("Text"))
		{		
			if(mGLRenderView.GetTxtLayoutInstance() != null){
	    		mGLRenderView.GetTxtLayoutInstance().stopAutoPlay();
	    		relevanceState.setPlayState("Text", false);
			}
			
			if(mGLRenderView.GetPictureLayoutInstance() != null){
	    		mGLRenderView.GetPictureLayoutInstance().stopAutoPlay();
	    		relevanceState.setPlayState("Picture", true);
			}
		}
		
//		mGLRenderView.uninitMusicLayout();
	    mGLRenderView.uninitPictureLayout();
	    mGLRenderView.uninitTxtLayout();
		BackBrowser(lastFilebrowserType);
    	Log.i("RelevanceOp", "<-- ReturnRelevance");
    }

    class DataCallbackUpdataMenu{
    	String type;
    	List<String> data;
    	DataCallbackUpdataMenu(String t, List<String>  d){
    		type = t;
    		data = new ArrayList();
    		data.addAll(d);
    	}
    }
	private Handler handlerCallbackUpdataMenu = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
		    	if(MymenuOp.getMenuInstance() != null){
		    		DataCallbackUpdataMenu dt = (DataCallbackUpdataMenu)msg.obj;
					MymenuOp.getMenuInstance().getMenuIns().recoverPlayerStatus(dt.type, dt.data);
		    	}
				break;
			default:
				break;
			}
		}
	};
	
    public void CallbackUpdataMenu(final String type,final List<String> data)
    {
//    	if(MymenuOp.getMenuInstance() != null &&MymenuOp.getMenuInstance().getVisibility() == View.VISIBLE)
//    	{
//    		
//    		myActivity.runOnUiThread(new Runnable(){
//				public void run() {			
//					MymenuOp.getMenuInstance().updatamenu(type,data);
//				}
//			});
//    	}
		Message msg = new Message();
		msg.what = 1;
		msg.obj = new DataCallbackUpdataMenu(type, data);
		handlerCallbackUpdataMenu.sendMessage(msg);

    }
   
    public void CallbackRelevance(String res,String target)
    {
    	int ldFromMusic=0;
    	if(res.equals("Audio"))
    	{	
    		if((target.equals("Picture"))||(target.equals("Text")))
    			{
				ldFromMusic=1;
    			}
    		GLMusicLayout musicLayout;
    		musicLayout = (GLMusicLayout)mGLRenderView.GetMusicLayoutInstance();
    		musicLayout.HideBg();
    	}
    	else
		if(res.equals("Picture"))
    	{
			relevanceState.setPlayState("Picture", true);
			picRelevanceID = mGLRenderView.GetPictureLayoutInstance().dataProvider.getCurFilePath();
			mGLRenderView.uninitPictureLayout();
    	}
		else
		if(res.equals("Text"))
    	{
			relevanceState.setPlayState("Text", true);
			mGLRenderView.uninitTxtLayout();
		    //***************record data when op exit text*******************// 
			int flag = new ZLIntegerOption("BookMark", "flag", 0).getValue();
			if(flag == 0)
				new BookmarksManager().addBookmark();
			//***************finish********************//
			
    	}	
   	
		if(mGLRenderView.GetPictureLayoutInstance() != null){
    		mGLRenderView.GetPictureLayoutInstance().stopAutoPlay();
    		mGLRenderView.uninitPictureLayout();
    		relevanceState.setPlayState("Picture", true);
		}
		
		if(mGLRenderView.GetTxtLayoutInstance() != null){
    		mGLRenderView.GetTxtLayoutInstance().stopAutoPlay();
    		mGLRenderView.uninitTxtLayout();
    		relevanceState.setPlayState("Text", true);
    		//***************record data when op exit text*******************// 
			int flag = new ZLIntegerOption("BookMark", "flag", 0).getValue();
			if(flag == 0)
				new BookmarksManager().addBookmark();
			//***************finish********************//
		}
		
    	
//    	MymenuOp.DestoryMenu();
		menuStopFlag = true;
    	this.PlayBrowser(target);
		if(ldFromMusic==1)
			{
			mGLRenderView.GetGridLayoutInstance().ldFromMusic(true);
			}
    }
    
    //stop op by menu
    public void  stopplayer(String type)
    {
//    	MymenuOp.HideMenu();
    	menuStopFlag = true;
		if(type.equals("Picture"))
		{
			if(mGLRenderView.GetPictureLayoutInstance() != null){
				backFileName = mGLRenderView.GetPictureLayoutInstance().dataProvider.getCurFilePath();
	    		mGLRenderView.GetPictureLayoutInstance().stopAutoPlay();
	    		mGLRenderView.uninitPictureLayout();
	    		relevanceState.setPlayState("Picture", false);
			}
		}
		else	
		if(type.equals("Audio"))
		{		
			if(mGLRenderView.GetMusicLayoutInstance() != null){
				backFileName = mGLRenderView.GetMusicLayoutInstance().dataProvider.getCurFilePath();
	    		mGLRenderView.GetMusicLayoutInstance().stopPlayer();
	    		mGLRenderView.GetMusicLayoutInstance().stopAutoPlay();
				mGLRenderView.uninitMusicLayout();
				setIsMusicLayoutExist(false);
				relevanceState.setPlayState("Audio", false);
	    		
			}
		}
		else
		if(type.equals("Text"))
		{		
			if(mGLRenderView.GetTxtLayoutInstance() != null){
	    		mGLRenderView.GetTxtLayoutInstance().stopAutoPlay();
	    		mGLRenderView.uninitTxtLayout();
	    		relevanceState.setPlayState("Text", false);
			}
		}
		String mtype  = getMenuType();
		if(mtype.equals(""))
				BackBrowser(type);
		else
		{
			if(mtype.indexOf("P")!= -1)
			{
				
			}
			if(mtype.indexOf("Music")!= -1)
			{
	    		mGLRenderView.GetMusicLayoutInstance().ShowBg();
				this.CallbackName("music",this.getCurMusicName());
				addSpectrum();
	    		
			}
			if(mtype.indexOf("T")!= -1)
			{
				
			}
		}		
    }

    class DataCallbackName{
    	String type;
    	String name;
    	DataCallbackName(String t, String n){
    		if(n !=null && t != null)
    		{
        		type = t;
        		name = n;
    		}
    	}
    }
	private Handler handlerCallbackName = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
		    	if(MymenuOp.getMenuInstance() != null &&MymenuOp.getMenuInstance().getVisibility() == View.VISIBLE){
		    		DataCallbackName dt = (DataCallbackName)msg.obj;
					MymenuOp.getMenuInstance().set_play_name(dt.type, dt.name);
		    	}
				break;
			default:
				break;
			}
		} 
	};
	//@SuppressWarnings("null")
	public  void CallbackName(final String type,final String filename)
    {
		
		if(filename != null)
		{
			String name = null;
			if(filename.equals("Nothing"))
			{
				if(type.equals("music"))
				{
					name=  this.getCurMusicName();
				}else
				if(type.equals("picture"))
				{
					name = this.getCurPictureName();
				}else
				if(type.equals("txt"))
				{
					name = this.getCurTextName();
				}
				if(name != null)
					backFileName = new String(name);
			}
			else
				backFileName = new String(filename);
				
			final String playername  = backFileName;
			if(playername !=  null && playername.length() != 0){	
//				if(MymenuOp.getMenuInstance() != null &&MymenuOp.getMenuInstance().getVisibility() == View.VISIBLE)
//		    	{
//		    		myActivity.runOnUiThread(new Runnable(){
//						public void run() {			
//							MymenuOp.getMenuInstance().set_play_name(type,ConvertFileName(playername));
//						}
//					});
//		    	}
				Message msg = new Message();
				msg.what = 1;
				msg.obj = new DataCallbackName(type, ConvertFileName(playername));
				handlerCallbackName.sendMessage(msg);
			}
		}
    	
    }
	
    class DataCallbackPosScale{
    	String type;
    	String scale;
    	DataCallbackPosScale(String t, String s){
    		type = t;
    		scale = s;
    	}
    }
	private Handler handlerCallbackPosScale = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
		    	if(MymenuOp.getMenuInstance() != null &&MymenuOp.getMenuInstance().getVisibility() == View.VISIBLE){
		    		DataCallbackPosScale dt = (DataCallbackPosScale)msg.obj;
					MymenuOp.getMenuInstance().setplayerPosScale(dt.type, dt.scale);
		    	}
				break;
			default:
				break;
			}
		} 
	};
	public void CallbackPosScale(final String type, String scale)
	{
		
		if(scale.equals("Nothing"))
		{
			if(type.equals("music"))
			{
				scale = this.getCurMusicPosScale();
			}else
			if(type.equals("picture"))
			{
				scale = this.getCurPicturePosScale();
			}else
			if(type.equals("txt"))
			{
				scale = " ";
			}
			
		}
		
		final String myscale = scale;
		
		
		if(myscale !=  null && myscale.length() != 0){	
//			if(MymenuOp.getMenuInstance() != null &&MymenuOp.getMenuInstance().getVisibility() == View.VISIBLE)
//	    	{
//	    		myActivity.runOnUiThread(new Runnable(){
//					public void run() {			
//						MymenuOp.getMenuInstance().setplayerPosScale(type, myscale);
//					}
//				});
//	    	}
			Message msg = new Message();
			msg.what = 1;
			msg.obj = new DataCallbackPosScale(type, myscale);
			handlerCallbackPosScale.sendMessage(msg);
		}
	}
	public String ConvertFileName(String filepath)
	{
		  if(filepath != null)
		  {
			  int i  = filepath.lastIndexOf("/");
			  String filename = filepath.substring(i+1,filepath.length());
		      return filename;
		  }
		  else
			  return null;
		  
		
	}
		
	private void addSpectrum()
	{
		if(this.mGLRenderView.GetMusicLayoutInstance() != null)
		{
			specOp.setMusicListener(this.mGLRenderView.GetMusicLayoutInstance());
			specOp.resume();
		}
			
	}
	private void removeSpectrum()
	{
		specOp.Destroy();
	}

	public void UnmountDevice(String deviceName) {
		int stateCount = relevanceState.getStateCount();
		 if(mGLRenderView.GetGridLayoutInstance()!=null)
		 {
			 if( stateCount > 0)
			 {
				 if(relevanceState.getPlayState("Text"))
				 {
					 if((TextName.startsWith(deviceName)))
					 {
						 relevanceState.setPlayState("Text", false);	
					 }
				 }
				 if(relevanceState.getPlayState("Audio"))
				 {
					 if((musicList.get(0).startsWith(deviceName)))
					 {
						 removeSpectrum();
						if(mGLRenderView.GetMusicLayoutInstance() != null){
				    		mGLRenderView.GetMusicLayoutInstance().stopAutoPlay();
							mGLRenderView.uninitMusicLayout();
							setIsMusicLayoutExist(false);
							relevanceState.setPlayState("Audio", false);
				    		
						}
					 }
				 }
					
				 if(relevanceState.getPlayState("Picture"))
				 {
					 if((picList.get(0).startsWith(deviceName)))
					 {
					 	relevanceState.setPlayState("Picture", false);
					 }
				 }
			 }
			   
				mGLRenderView.GetGridLayoutInstance().UnmountDevice(deviceName);
		 }
		 else
		 {
			 if((relevanceState.getPlayState("Text") &&TextName.startsWith(deviceName))||
			   (relevanceState.getPlayState("Audio")&&musicList.get(0).startsWith(deviceName))||
			   (relevanceState.getPlayState("Picture")&&picList.get(0).startsWith(deviceName)))
			  {
					MymenuOp.DestoryMenu();
				 if(relevanceState.getPlayState("Text"))
					{
						
					 if(mGLRenderView.GetTxtLayoutInstance() != null){
				    		mGLRenderView.GetTxtLayoutInstance().stopAutoPlay();
				    		mGLRenderView.uninitTxtLayout();
				    		relevanceState.setPlayState("Text", false);
						}
					}
					
					if(relevanceState.getPlayState("Audio"))
					{
					 	
							removeSpectrum();
							if(mGLRenderView.GetMusicLayoutInstance() != null){
					    		mGLRenderView.GetMusicLayoutInstance().stopAutoPlay();
								mGLRenderView.uninitMusicLayout();
								setIsMusicLayoutExist(false);
								relevanceState.setPlayState("Audio", false);
					    		
							}
					}
					
					if(relevanceState.getPlayState("Picture"))
					{
						if(mGLRenderView.GetPictureLayoutInstance() != null){
				    		mGLRenderView.GetPictureLayoutInstance().stopAutoPlay();
				    		mGLRenderView.uninitPictureLayout();
				    		relevanceState.setPlayState("Picture", false);
						}
					}
					this.PlayBrowser(lastFilebrowserType);
			  }
			
		 }
	}

	public void MountDevice(String deviceName) {
		if(mGLRenderView.GetGridLayoutInstance() != null){
			mGLRenderView.GetGridLayoutInstance().MountDevice(deviceName);
		}
	}
	public String getCurPictureName()
	{
		if(mGLRenderView.GetPictureLayoutInstance() != null)
			return mGLRenderView.GetPictureLayoutInstance().dataProvider.getCurFilePath();
		return "";
	}
	public String getCurMusicName()
	{
		if(mGLRenderView.GetMusicLayoutInstance() != null)
			return mGLRenderView.GetMusicLayoutInstance().getCurrentMusicPath();
		return "";
	}	
	public String getCurTextName()
	{
		return TextName;
	}
	
	public String getCurPicturePosScale()
	{
		if(mGLRenderView.GetPictureLayoutInstance() != null)
			return mGLRenderView.GetPictureLayoutInstance().dataProvider.getCurPosScale();
		else
			return null;
	}
	public String getCurMusicPosScale()
	{
		if(mGLRenderView.GetMusicLayoutInstance() != null )
			return mGLRenderView.GetMusicLayoutInstance().getCurrentMusicPath();
		else
			return null;
	}
	
	
	private Handler handlerDelayShowText = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				if(mGLRenderView.GetTxtLayoutInstance() == null)
					mGLRenderView.initTxtLayout();
				mGLRenderView.GetTxtLayoutInstance().setFilenameListener(RelevanceOp.this);
					
			break;
			default:
				break;
			}
		}
	};
	
	private void postDelayShowTextMessage(){
		Message msg = new Message();
		msg.what = 1;
		handlerDelayShowText.sendMessageDelayed(msg, 200);
	}

	public void setIsMusicLayoutExist(boolean b){
		try {
			musicService.setIsMusicLayoutExist(b);
		} catch (RemoteException ex) {
		}
	}
	public void setFirstFileName(String name){
		try {
			musicService.setFirstFileName(name);
		} catch (RemoteException ex) {
		}
	}
	public void setFileList(List<String> list){
		try {
			musicService.setFileList(list);
		} catch (RemoteException ex) {
		}
	}
}
