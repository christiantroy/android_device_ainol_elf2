package com.amlogic.View;


import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import com.amlogic.BreakText;
import com.amlogic.Listener.AmlogicMenuListener;
import com.amlogic.AmlogicMenu.SearchDrawable;
import com.amlogic.control.playerStatus;

public class MenuGroup  extends View{
	private  final String  TAG = "MenuGroup";
	private  final int     		 MenusTotal 				    = 10;
	private  int  		  		 AbsoluteFoucsID 				= 0;
	private  int  		  		 RelativeFoucsID			    = 0;
	private List<String>  		 Menu ;
	private List<String>  		 TvSetMenu ;
	private List<String>  		 ProgramMenu;
	private List<String>  		 MusicMenu ;
	private List<String>  		 PicMenu ;
	private List<String>  	     TextMenu ;
	private List<String>  		 Setup3DMenu ;
	private List<String>  		 DefaultMenu ;
	
	private List<Bitmap>  		 FocusBitmapSet;
	private List<Bitmap>  		 unFocusBitmapSet;
	private Bitmap        		 BgBtp 						    = null;
	private Bitmap 		  		 leftBtp						= null;
	private Bitmap 	      		 rightBtp						= null;
	private AmlogicMenuListener MyCheckedMenu;
	private int 			     ShowTotal 						= 10;
	private int 				 ItemTotal 						= 0;
	private String 				 ShowState 						= null;
	private SearchDrawable 		 SearchID ;
	private int				     bartotal		 			    = 0;
	private int 				 bar_cur_pos 					= 0;
	private String 			     playername 					= null;
	private int 				 Type ;
	private String 				 playerPosScale 				= null;
	private int 				 BeatHight 						= 0;
	private SimpleDateFormat 	 sdf ;
	Paint   					 Rectpaint 						= new Paint();	
	Paint   					 Fontpaint 						= new Paint();	
	public final static int   	 bar_player 					= 1<<1;
	public final static int      nobar_player  					= 1<<2;
	public final static int      noplayer 						= 1<<3;
	public int 				     leftoffset						= 0;//sunsikai add 20110216	


	
	public MenuGroup(Context context, AttributeSet attrs,int type)
	{
		super(context,attrs);
		Menu = new ArrayList<String>();
		TvSetMenu = new ArrayList<String>();
		ProgramMenu = new ArrayList<String>();
		MusicMenu = new ArrayList<String>();
		PicMenu = new ArrayList<String>();
		TextMenu = new ArrayList<String>();
		Setup3DMenu = new ArrayList<String>();
		DefaultMenu = new ArrayList<String>(); 
		FocusBitmapSet = new ArrayList<Bitmap>();
		unFocusBitmapSet = new ArrayList<Bitmap>();
		
		SearchID = new SearchDrawable(this.getContext());
		Type = type;
		if(Type == MenuGroup.bar_player)
			BgBtp = SearchID.getBitmap("shortcut_bg_bar_progress");	
		else
		if(Type == MenuGroup.nobar_player )
			BgBtp = SearchID.getBitmap("shortcut_bg_bar_info");	
		else
			BgBtp = SearchID.getBitmap("shortcut_bg_bar");
		leftBtp = SearchID.getBitmap("shortcut_bg_arrow_left");
		rightBtp = SearchID.getBitmap("shortcut_bg_arrow_right");
		
		
//		this.setFocusable(true);
		sdf = new SimpleDateFormat("HH:mm:ss");
		Fontpaint.setColor(Color.WHITE);
		Fontpaint.setTextSize(28);
		Fontpaint.setAntiAlias(true);
		Rectpaint.setARGB(0xaf, 0x6c, 0xc0, 0xff);
	
	}
	
	public void initFocusID(){
		AbsoluteFoucsID = 0;
		RelativeFoucsID = 0;
	}
	
	
	
	public void setFocusID(int FocusID) {    
		AbsoluteFoucsID = FocusID;
		RelativeFoucsID = FocusID%ShowTotal;
	}

	
	
	public void showFirstSelectFrame()    
	{
		MyCheckedMenu.CheckedMenuHandle(KeyEvent.KEYCODE_DPAD_LEFT,RelativeFoucsID,Menu.get(AbsoluteFoucsID));
	}    
	
	public void  AddMenuItem(String MI,String MediaType)
	{
		if(MediaType.equals("tv_set"))
		{
			TvSetMenu.add(MI);
			return;
		}
		else
		if(MediaType.equals("program"))
		{
			ProgramMenu.add(MI);
			return;
		}	
		
		if(MediaType.equals("music"))
		{
			MusicMenu.add(MI);
		}
		else
		if(MediaType.equals("picture"))
		{
			PicMenu.add(MI);
		}
		else
		if(MediaType.equals("txt"))
		{
			TextMenu.add(MI);
		}
		else
		if(MediaType.equals("Setup3D"))
		{
			Setup3DMenu.add(MI);
		}
		else
			DefaultMenu.add(MI);
	}
	
	public void SetShowState(String state)
	{
		
		ShowState = state;
		unFocusBitmapSet.clear();
		FocusBitmapSet.clear();
		if(ShowState.equals("music"))
		{
			if(MusicMenu.size() != 0)
				Menu = MusicMenu;
		}
		else
		if(ShowState.equals("picture"))
		{
			if(PicMenu.size() != 0)
				Menu = PicMenu;
		}
		else
		if(ShowState.equals("txt"))
		{
			if(TextMenu.size() != 0)
				Menu = TextMenu;
		}
		else
			if(ShowState.equals("Setup3D"))
			{
				if(Setup3DMenu.size() != 0)
					Menu = Setup3DMenu;
			}
		else
			if(ShowState.equals("tv_set"))
			{
				if(TvSetMenu.size() != 0)
					Menu = TvSetMenu;
			}
			else
			if(ShowState.equals("program"))
			{
				if(ProgramMenu.size() != 0)
					Menu = ProgramMenu;
			}
			else
			{
				if(DefaultMenu.size() != 0)
					Menu = DefaultMenu;
			}
		ItemTotal = Menu.size();
		
		
		while(ItemTotal - FocusBitmapSet.size() > 0)
		{
			FocusBitmapSet.add(null);
			unFocusBitmapSet.add(null);
		}
			
	}	
			
		
	
	public void setMenuGroupListener(AmlogicMenuListener MGL)
	{
		MyCheckedMenu = MGL;
	}
	
	public void UpdataStatus(String  ID)
	{
		Menu.set(RelativeFoucsID, ID);
		if(FocusBitmapSet.get(AbsoluteFoucsID) != null)
			FocusBitmapSet.set(RelativeFoucsID, null);
		if(unFocusBitmapSet.get(AbsoluteFoucsID) != null)
			unFocusBitmapSet.set(RelativeFoucsID, null);
		if(Type == MenuGroup.bar_player)	
			this.postInvalidate(0,90 +  BeatHight,this.getWidth(),this.getHeight());
		else
		if(Type == MenuGroup.nobar_player)	
			this.postInvalidate(0,75+ BeatHight,this.getWidth(),this.getHeight());
		else
			this.postInvalidate();
	}	
	
	public void UpdataMenuData(String type ,Map<Integer,String> map)
	{
		if(ShowState.equals(type))
		{
			for(int i = 0 ;i <10; i++ )
			{
				String data = map.get(i);
				if(data != null)
				{
					Menu.set(i, data);
					if(FocusBitmapSet.get(i) != null)
						FocusBitmapSet.set(i, null);
					if(unFocusBitmapSet.get(i) != null)
						unFocusBitmapSet.set(i, null);
				}
			}
			if(Type == MenuGroup.bar_player)	
				this.postInvalidate(0,90 +  BeatHight,this.getWidth(),this.getHeight());
			else
			if(Type == MenuGroup.nobar_player)	
				this.postInvalidate(0,75+ BeatHight,this.getWidth(),this.getHeight());
			else
				this.postInvalidate();
		}
		else
		{

			if(type.equals("music"))
			{
				if(MusicMenu.size() != 0)
				{
					for(int i = 0 ;i <10; i++ )
					{
						String data = map.get(i);
						if(data != null)
							MusicMenu.set(i, data);
					}
				}
			}
			else
			if(type.equals("picture"))
			{
		
				if(PicMenu.size() != 0)
				{
					for(int i = 0 ;i <10; i++ )
					{
						String data = map.get(i);
						if(data != null)
							PicMenu.set(i, data);
					}
				}
			}
			else
			if(type.equals("txt"))
			{
				if(TextMenu.size() != 0)
				{
					for(int i = 0 ;i <10; i++ )
					{
						String data = map.get(i);
						if(data != null)
							TextMenu.set(i, data);
					}
				}
			}
		}
	}
	
	public void UpdataMenuData(Map<Integer,String> map)
{
		for(int i = 0 ;i <10; i++ )
		{
			String data = map.get(i);
			if(data != null)
			{
				Menu.set(i, data);
				if(FocusBitmapSet.get(i) != null)
					FocusBitmapSet.get(i).recycle();
				if(unFocusBitmapSet.get(i) != null)
					unFocusBitmapSet.get(i).recycle();
				FocusBitmapSet.set(i, null);
				unFocusBitmapSet.set(i, null);
			}
		}
		if(Type == MenuGroup.bar_player)	
			this.postInvalidate(0,90 +  BeatHight,this.getWidth(),this.getHeight());
		else
		if(Type == MenuGroup.nobar_player)	
			this.postInvalidate(0,75+ BeatHight,this.getWidth(),this.getHeight());
		else
			this.postInvalidate();
	}
	//@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Log.d(TAG, " MenuGroup_onKey  start "  + AbsoluteFoucsID  + "   "+RelativeFoucsID);
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
			onKeyDown(KeyEvent.KEYCODE_DPAD_LEFT,event);
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
			onKeyDown(KeyEvent.KEYCODE_DPAD_RIGHT,event);
			break;
			
		case KeyEvent.KEYCODE_DPAD_UP:
			
		
		break;
		case KeyEvent.KEYCODE_DPAD_DOWN:


		break;
		case KeyEvent.KEYCODE_DPAD_LEFT:
		if(ItemTotal <= ShowTotal)
		{
			if(RelativeFoucsID > 0 )
			{
				AbsoluteFoucsID --;
				RelativeFoucsID --;
				
				refreshUI(keyCode,AbsoluteFoucsID,RelativeFoucsID);
				
				
				MyCheckedMenu.CheckedMenuHandle(KeyEvent.KEYCODE_DPAD_LEFT,RelativeFoucsID,Menu.get(AbsoluteFoucsID));
			}
		}
		else
		{
			if(RelativeFoucsID == 0 && (AbsoluteFoucsID == ShowTotal))
			{
				RelativeFoucsID	 = ShowTotal -1;
				AbsoluteFoucsID --;
	
				refreshUI(keyCode,AbsoluteFoucsID,RelativeFoucsID);
				
				MyCheckedMenu.CheckedMenuHandle(KeyEvent.KEYCODE_DPAD_LEFT,RelativeFoucsID,Menu.get(AbsoluteFoucsID));
			}
			else
			if(RelativeFoucsID > 0 )
			{						
				AbsoluteFoucsID --;
				RelativeFoucsID --;
				
				if(RelativeFoucsID>0&&AbsoluteFoucsID>0){
					if(Menu.get((AbsoluteFoucsID-1)).equals("shortcut_setup_sys_woofer_switch_")){
						
						String state=MyCheckedMenu.GetSelectFrameState("shortcut_setup_sys_woofer_switch_");
						if(state!=null)
						if(state.equals("OFF")){						
							AbsoluteFoucsID --;
							RelativeFoucsID --;		
						}
					}
				}
				
				refreshUI(keyCode,AbsoluteFoucsID,RelativeFoucsID);

				
				MyCheckedMenu.CheckedMenuHandle(KeyEvent.KEYCODE_DPAD_LEFT,RelativeFoucsID,Menu.get(AbsoluteFoucsID));
			}
		}
		Log.d(TAG, " MenuGroup_onKey  finish "  + AbsoluteFoucsID  + "   "+RelativeFoucsID);
		return super.onKeyDown(keyCode, event);

		case KeyEvent.KEYCODE_DPAD_RIGHT:

			if(ItemTotal <= ShowTotal)
			{
				if(RelativeFoucsID < (ItemTotal -1) )
				{
					AbsoluteFoucsID ++;
					RelativeFoucsID ++;

					refreshUI(keyCode,AbsoluteFoucsID,RelativeFoucsID);
					
					MyCheckedMenu.CheckedMenuHandle(KeyEvent.KEYCODE_DPAD_RIGHT,RelativeFoucsID,Menu.get(AbsoluteFoucsID));
				}
			}
			else
			{
				if(AbsoluteFoucsID < (ShowTotal -1) )
				{
					if(Menu.get(AbsoluteFoucsID).equals("shortcut_setup_sys_woofer_switch_")){
						
						String state=MyCheckedMenu.GetSelectFrameState("shortcut_setup_sys_woofer_switch_");
						if(state!=null)
						if(state.equals("OFF")){
							AbsoluteFoucsID ++;
							RelativeFoucsID ++;						
						}
					}					
					
					AbsoluteFoucsID ++;
					RelativeFoucsID ++;
					
					refreshUI(keyCode,AbsoluteFoucsID,RelativeFoucsID);

					
					MyCheckedMenu.CheckedMenuHandle(KeyEvent.KEYCODE_DPAD_RIGHT,RelativeFoucsID,Menu.get(AbsoluteFoucsID));
				}
				else
				{
					if(AbsoluteFoucsID < (ItemTotal -1))
					{
						AbsoluteFoucsID ++;
						RelativeFoucsID = AbsoluteFoucsID - ShowTotal ;
						refreshUI(keyCode,AbsoluteFoucsID,RelativeFoucsID);	
						MyCheckedMenu.CheckedMenuHandle(KeyEvent.KEYCODE_DPAD_RIGHT,RelativeFoucsID,Menu.get(AbsoluteFoucsID));
					}
					
				}
			}	
			Log.d(TAG, " MenuGroup_onKey  finish "  + AbsoluteFoucsID  + "   "+RelativeFoucsID);
		return super.onKeyDown(keyCode, event);

		case KeyEvent.KEYCODE_DPAD_CENTER:
		case KeyEvent.KEYCODE_ENTER:
			MyCheckedMenu.CheckedMenuHandle(KeyEvent.KEYCODE_ENTER,RelativeFoucsID,Menu.get(AbsoluteFoucsID));
			break;
		
		case KeyEvent.KEYCODE_BACK:
			if(ShowState.equals("Setup3D")){
				MyCheckedMenu.BackMenuHandle(ShowState);
				return true;
			}
			else
				MyCheckedMenu.CallMenucontrolunbindservice();
			break; 
		}
		return super.onKeyDown(keyCode, event);
		
	}
	
	//////////////////////progress      start///////////////////////////
	public void set_seek_bar_info( int i_total , int i_cur_pos )
	{		
		
	     bartotal = i_total;
		 bar_cur_pos = i_cur_pos;
//		 this.postInvalidate(40, 70+BeatHight,1880,90+BeatHight);
//		 this.postInvalidate(1620, 25+BeatHight,1900,60+BeatHight); 
		 this.postInvalidate(40, 25+BeatHight,1900,90+BeatHight);
		 
	
	}
	public void set_play_name( String name )
	{
		playername = name;
		if(Type == MenuGroup.bar_player)	
			this.postInvalidate(120, 10+BeatHight,600,50+BeatHight);
		else
		if(Type == MenuGroup.nobar_player)	
			this.postInvalidate(120, 10+BeatHight,600,60+BeatHight);
	}
	
	
	public void set_seek_bar_info(String type ,int i_total , int i_cur_pos )
	{		
		if(ShowState.equals(type))
		{
			bartotal = i_total;
			bar_cur_pos = i_cur_pos;
//			this.postInvalidate(40, 70+BeatHight,1880,90+BeatHight);
//		    this.postInvalidate(1620, 25+BeatHight,1900,60+BeatHight);
		    
		    this.postInvalidate(40, 25+BeatHight,1900,90+BeatHight);
		}
	}
	public void set_play_name( String type ,String name )
	{
		if(ShowState.equals(type))
		{
			playername = name;
			if(Type == MenuGroup.bar_player)	
				this.postInvalidate(120, 10+BeatHight,600,50+BeatHight);
			else
			if(Type == MenuGroup.nobar_player)	
				this.postInvalidate(120, 10+BeatHight,600,60+BeatHight);
		}
	}
	public void setplayerPosScale(String type ,String scale)
	{
		if(ShowState.equals(type))
		{
			playerPosScale = scale;
			this.postInvalidate(1520, 25+BeatHight,1620,60+BeatHight);
		}
	}
	/////////////////////////progress       finish////////////////////////////////////
	public  void myDraw(Canvas canvas,int hight)
	{	
//		Log.v("MenuGroup_myDraw", ""+RelativeFoucsID + AbsoluteFoucsID);

		Bitmap  bitmap;
		int LastShowTotal = 0;
//		if(FirstDrawFlag == 0)
//		{
			ItemTotal = Menu.size();
			while(ItemTotal - FocusBitmapSet.size() > 0)
			{
				FocusBitmapSet.add(null);
				unFocusBitmapSet.add(null);
			}
//			FirstDrawFlag = 1;
//		}
		if (ItemTotal < ShowTotal) {
			LastShowTotal = ItemTotal;
			this.leftoffset = (10 - ItemTotal) / 2 * 185 + (10 - ItemTotal) % 2* 92;//sunsikai add 20110216
		} else {
			LastShowTotal = ShowTotal;
			this.leftoffset = 0;//sunsikai add 20110216
		}
		 
		 if(BgBtp != null)
			 canvas.drawBitmap(BgBtp, this.getPaddingLeft(), this.getPaddingTop() + hight, null);

		 ///////////////////////progress////////////////////////////////////////
		
		 UpdataProgress(canvas,hight);
		 ///////////////////////progress////////////////////////////////////////	
		
		if( ShowTotal < ItemTotal && (AbsoluteFoucsID > RelativeFoucsID))
			{
				for(int i = 0 ;i < ItemTotal - ShowTotal; i++ )
				{
					if(i == RelativeFoucsID )
					{
		                if(FocusBitmapSet.get(ShowTotal + i) == null)	                	
		                	FocusBitmapSet.set(ShowTotal + i, SearchID.getBitmap(Menu.get(ShowTotal + i)+"sel")); 
		                bitmap = FocusBitmapSet.get(ShowTotal + i);
					}										
					else
					{
						 if(unFocusBitmapSet.get(ShowTotal + i) == null) 
							 unFocusBitmapSet.set(ShowTotal + i, SearchID.getBitmap(Menu.get(ShowTotal + i)+"unsel")); 
							 bitmap = unFocusBitmapSet.get(ShowTotal + i);
					}
					if(Type == MenuGroup.bar_player)	
						canvas.drawBitmap(bitmap, this.getLeft() + 185*i +35, 0 +90 + hight, null);
					else
					if(Type == MenuGroup.nobar_player)	
						canvas.drawBitmap(bitmap, this.getLeft() + 185*i +35, 0 +75 + hight, null);
					else
						canvas.drawBitmap(bitmap, this.getLeft() + 185*i +35, 0 + hight, null);
				}
				if(Type == MenuGroup.nobar_player || Type == MenuGroup.bar_player)	
					canvas.drawBitmap(leftBtp, this.getLeft() +8, 140 + hight, null);
				else
					canvas.drawBitmap(leftBtp, this.getLeft() +8, 60 + hight, null);
			}
			else 
			{
				if( ShowTotal < ItemTotal)
				{
					if(Type == MenuGroup.nobar_player || Type == MenuGroup.bar_player)	
						canvas.drawBitmap(rightBtp, this.getLeft() +1885, 140 + hight, null);
					else
						canvas.drawBitmap(rightBtp, this.getLeft() +1885, 60 + hight, null);
				}
				for(int i = 0 ;i < LastShowTotal; i++ )
				{
					
					if(i == RelativeFoucsID )
					{
		                if(FocusBitmapSet.get(i) == null)
		                	FocusBitmapSet.set(i, SearchID.getBitmap(Menu.get(i)+"sel")); 
		                	bitmap = FocusBitmapSet.get(i);
					}
					else
					{
						
						if(Menu.get(i).equals("shortcut_setup_sys_woofer_vol_")){
							if(MyCheckedMenu!=null){
								String state=MyCheckedMenu.GetSelectFrameState("shortcut_setup_sys_woofer_switch_");
							if(state!=null)
							if(state.equals("OFF"))
								unFocusBitmapSet.set(i, SearchID.getBitmap("shortcut_setup_sys_woofer_vol_disable"));
							else
								unFocusBitmapSet.set(i,  SearchID.getBitmap(Menu.get(i)+"unsel"));					
							}
						}
						
						
						////////////////////////////////////
						 if(unFocusBitmapSet.get(i) == null)
							 unFocusBitmapSet.set(i,  SearchID.getBitmap(Menu.get(i)+"unsel")); 
						 bitmap = unFocusBitmapSet.get(i);
					}
					if(Type == MenuGroup.bar_player)	
						canvas.drawBitmap(bitmap, this.getLeft() + 185*i+leftoffset+35, 0 +90 + hight, null);
					else
					if(Type == MenuGroup.nobar_player)	
						canvas.drawBitmap(bitmap, this.getLeft() + 185*i+leftoffset+35, 0 +75 + hight, null);
					else
						canvas.drawBitmap(bitmap, this.getLeft() + 185*i+leftoffset+35, 0 + hight, null);
			    }		
	         }
	   }
	
	private void UpdataProgress(Canvas canvas,int hight)
	{
		 if(Type != MenuGroup.noplayer)	
		 {			
			if(bartotal != 0 && bar_cur_pos != 0)
			{
				int dst_width = 1840*bar_cur_pos/bartotal ;
				if( dst_width < 1 )
					dst_width = 1; 
				Log.d(TAG, " onDraw progressbar cur_pos: "  + bar_cur_pos + " tota: " + bartotal + " dst_width : " + dst_width );			
				canvas.drawRect( 44, 70+hight, 44+dst_width, 90+hight, Rectpaint);
				canvas.drawText(formatSeconds2HHmmss(bar_cur_pos)+'/' +formatSeconds2HHmmss(bartotal),1600, 45+hight, Fontpaint);
//				Log.d(TAG," OnDraw : play time:" + formatSeconds2HHmmss(bar_cur_pos)+'/' +formatSeconds2HHmmss(bartotal) );
			}
			if(playername != null)
			{
				playername = BreakText.breakText(playername, 28, 360);
				if(playername != null )
				{
					if(Type == MenuGroup.bar_player)	
						canvas.drawText(playername,120, 40+hight, Fontpaint);
					else
					if(Type == MenuGroup.nobar_player)	
						canvas.drawText(playername,120, 50+hight, Fontpaint);
				}
				
			}
			if(playerPosScale != null)
					canvas.drawText(playerPosScale,1520, 45+hight, Fontpaint);
		}
	}
	private String formatSeconds2HHmmss(int seconds) {
	
		Date date = new Date(seconds * 1000);
		return sdf.format(date);
	}

	public void refreshUI(int keyCode,int RelativeFoucsID,int AbsoluteFoucsID)
	{	
		if(Type == MenuGroup.bar_player)	
			this.invalidate(0,90+BeatHight,this.getWidth(),this.getHeight());
		else
		if(Type == MenuGroup.nobar_player)	
			this.invalidate(0,75+BeatHight,this.getWidth(),this.getHeight());
		else
			this.invalidate();
		
//		Rect myRect = new Rect();
//		
//		switch (keyCode) {
//		case KeyEvent.KEYCODE_DPAD_LEFT:
//			if(RelativeFoucsID == 9)
//			{
//				if(Type == MenuGroup.bar_player)	
//					myRect.set(0,90+BeatHight,this.getWidth(),this.getHeight());
//				else
//				if(Type == MenuGroup.nobar_player)	
//					myRect.set(0,75+BeatHight,this.getWidth(),this.getHeight());
//			}
//			else
//			{
//				if(Type == MenuGroup.bar_player)	
//					myRect.set(0,90+BeatHight,this.getWidth(),this.getHeight());
//				else
//				if(Type == MenuGroup.nobar_player)	
//					myRect.set(0,75+BeatHight,this.getWidth(),this.getHeight());
//			}	
//			break;
//		case KeyEvent.KEYCODE_DPAD_RIGHT:
//			
//			break;
//		}
	}
	
	Bitmap myMainBitmap = null;//Bitmap.createBitmap(1920, 238, Bitmap.Config.ARGB_8888);
	private boolean first = false;
	private Matrix matrix = new Matrix();
	private boolean Myeffect = false;
	public  void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
		if(first)
		{
			first = false;
			startWorker();
			return;
		}
		
	    if(Myeffect)
	    {
	    	canvas.drawBitmap(myMainBitmap, matrix, null);
	    }
	    else
	    {
	    	myDraw(canvas, BeatHight);
	    }
	}

	protected void onLayout (boolean changed, int left, int top, int right, int bottom) 
	{
		if(changed)
		{
			BeatHight = bottom - top - BgBtp.getHeight();
		}
	}
	private void startWorker() {	
	  Thread mWorkerThread = new Thread() {
			
			public void run() {
			boolean myfisrt = true;	
			int path = MenuGroup.this.getHeight();;
				while(!isInterrupted() )
				{
					try {							
						if(Myeffect)
						{
							matrix.setTranslate(0, path);
							MenuGroup.this.postInvalidate(0, 0, 1920,  MenuGroup.this.getHeight());
							if( myfisrt == true )
								path = EffectSegmentingaCurve(path,1);
							if(path<= 0 )
							{
								myfisrt = false;
								path = 0;
							}			
							if(path>= 0 && myfisrt == false)
							{
								path = EffectSegmentingaCurve(path,2);
								if(path >= 100)
								{
									myMainBitmap.recycle();
									Myeffect = false;
									break;
								}	
							}
						}
						sleep(20);	
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} 
				}
			}
		};
		Canvas c =  new Canvas(myMainBitmap);
		myDraw(c,0);
		Myeffect = true;
		mWorkerThread.start();
	}	
	private int EffectSegmentingaCurve(int x,int dir )
	{
		int y = 0;  
		if(dir == 1) //up
		{
			 //338 ---100  gradient =1
			if(x >100 && x <= this.getHeight())
				y =x -15; 
			 //100 ---0  gradient =0.5
			else
			if(x >=0&& x <=100)
				y =5*x*10/10/10 -8; 
		}
		else
		if(dir == 2)
		{
			//100 ---0  gradient =1
			if(x >=0&& x <100)
			    y = x+12;
		}
		
		return y;
	}
	

////////////////////////////subsidiary  get info//////////////////////////////////////
	public String getAbsoluteFoucsItem() {
		return Menu.get(AbsoluteFoucsID);
	}
	
	public int getRelativeFoucsID() {    
		return RelativeFoucsID;
	}
	
	public void getLeftOffSet(){
		ItemTotal = Menu.size();
		if (ItemTotal < ShowTotal) {
			this.leftoffset = (10 - ItemTotal) / 2 * 185 + (10 - ItemTotal) % 2* 92;//sunsikai add 20110216
		} else {
			this.leftoffset = 0;//sunsikai add 20110216
		}
	}
	
	public String GetShowState() {
		return ShowState;
	}
	
	public int GetFocusID(String id) {
		 int  focus = -1;
		 int total = Menu.size();
		 for(int i =0;i<total;i++)
			 if(Menu.get(i).equals(id) )		 
			 {
			 	focus = i;
			 	break;
			 }
		
		
		return focus;
	}
////////////////////////////finish//////////////////////////////////////////////
	public void  HandleSpecialKeyID(int key)
	{	 
		 int  focus = -1;
		 if(key  ==  85)  //play_PAUSE
		 {
			 int total = Menu.size();
			 for(int i =0;i<total;i++)
				 if(Menu.get(i).equals("shortcut_common_play_") || Menu.get(i).equals("shortcut_common_pause_") )		 
				 {
				 	focus = i;
				 	break;
				 }
		 } else 
		 if(key  ==  86) //play_STOP
		 {
			 int total = Menu.size();
			 for(int i =0;i<total;i++)
				 if(Menu.get(i).equals("shortcut_common_stop_")  )
				 {
				 	focus = i;
				 	break;
				 }
		 }else
		 if(key  ==  90)  //play_FF
		 {
			 int total = Menu.size();
			 for(int i =0;i<total;i++)
				 if(Menu.get(i).contains("shortcut_common_ff"))
				 {
				 	focus = i;
				 	break;
				 }
		 }else
		 if(key  ==  92)//play_FB
		 {
			 int total = Menu.size();
			 for(int i =0;i<total;i++)
				 if(Menu.get(i).contains("shortcut_common_fb"))
				 {
				 	focus = i;
				 	break;
				 }
		 }else
		 if(key  ==  88)//play_PROE
		 {
			 int total = Menu.size();
			 for(int i =0;i<total;i++)
				 if(Menu.get(i).contains("prev"))
				 {
				 	focus = i;
				 	break;
				 }
		 }else
		 if(key  ==  87) //play_NEXT
		 {
			 int total = Menu.size();
			 for(int i =0;i<total;i++)
				 if(Menu.get(i).contains("next"))
				 {
				 	focus = i;
				 	break;
				 }
		 }else
		 if(key  ==  91) //mute
		 {
			 int total = Menu.size();
			 for(int i =0;i<total;i++)
				 if(Menu.get(i).contains("shortcut_common_mute_"))
				 {
				 	focus = i;
				 	break;
				 }
		 }
		if(focus != -1)
		{
			 this.setFocusID(focus);	
			 this.refreshUI(0,0,0);
			 this.onKeyDown(KeyEvent.KEYCODE_ENTER, new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER));
		}
	}
	public void  recoverPlayerStatus(List<String> list)
	{		
		if(list == null)
			return;
		for(int i =0;i< list.size();i++)
		{
			int total = Menu.size();
			for(int ii =0;ii<total;ii++)
			{
				if(list.get(i).equals(playerStatus.player_PAUSE))
				{
					 if(Menu.get(ii).contains("shortcut_common_pause_"))
					 {
						 Menu.set(ii, "shortcut_common_play_");
						 clearMenuIDBitmap(ii);
						 break;
					 }
				}else
				if(list.get(i).equals(playerStatus.player_PLAY))
				{
					
					 if(Menu.get(ii).contains("shortcut_common_play_"))
					 {
						 Menu.set(ii, "shortcut_common_pause_");
						 clearMenuIDBitmap(ii);
						 break;
					 }
				}else
				if(list.get(i).equals(playerStatus.player_FB))
				{
					
					 if(Menu.get(ii).contains("shortcut_common_fb"))
					 {
						 Menu.set(ii, "shortcut_common_fb_");
						 clearMenuIDBitmap(ii);
						 break;
					 }
				}else
				if(list.get(i).equals(playerStatus.player_FF))
				{
					 if(Menu.get(ii).contains("shortcut_common_ff"))
					 {
						 Menu.set(ii, "shortcut_common_ff_");
						 clearMenuIDBitmap(ii);
						 break;
					 }
				}
			}
			
		}
		if(Type == MenuGroup.bar_player)	
			this.postInvalidate(0,90 +  BeatHight,this.getWidth(),this.getHeight());
		else
		if(Type == MenuGroup.nobar_player)	
			this.postInvalidate(0,75+ BeatHight,this.getWidth(),this.getHeight());
		else
			this.postInvalidate();
	}
	
	
	public void  recoverPlayerStatus(String type,List<String> list)
	{		
		if(list == null)
			return;
		if(ShowState.equals(type))
			recoverPlayerStatus(list);
		else
		{
			List<String>  tempMenu = null;
			if(type.equals("music"))
			{
				if(MusicMenu.size() != 0)
					tempMenu = MusicMenu;
			}
			else
			if(type.equals("picture"))
			{
				if(PicMenu.size() != 0)
					tempMenu = PicMenu;
			}
			else
			if(type.equals("txt"))
			{
				if(TextMenu.size() != 0)
					tempMenu = TextMenu;
			}
			if(tempMenu == null)
				return;
			for(int i =0;i< list.size();i++)
			{
				int total = tempMenu.size();
				 for(int ii =0;ii<total;ii++)
				 {
					if(list.get(i).equals(playerStatus.player_PAUSE) && tempMenu.get(ii).contains("shortcut_common_pause_"))
					{
						 tempMenu.set(ii, "shortcut_common_play_");
						 break;
					
					}else
					if(list.get(i).equals(playerStatus.player_PLAY) && tempMenu.get(ii).contains("shortcut_common_play_"))
					{
						 tempMenu.set(ii, "shortcut_common_pause_");
						 break; 
					}else
					if(list.get(i).equals(playerStatus.player_FB) && tempMenu.get(ii).contains("shortcut_common_fb"))
					{
						tempMenu.set(ii, "shortcut_common_fb_");
						 break;
					}else
					if(list.get(i).equals(playerStatus.player_FF)&&tempMenu.get(ii).contains("shortcut_common_ff"))
					{
						 tempMenu.set(ii, "shortcut_common_ff_");
						 break;
					}
					
				 }		 
		    }
		}	
	}
	
	private void clearMenuIDBitmap(int id)
	{
		 if(FocusBitmapSet.get(id) != null)
		 {
			 FocusBitmapSet.get(id).recycle();
			 FocusBitmapSet.set(id, null);
		 }
		 if(unFocusBitmapSet.get(id) != null)
		 {
			 unFocusBitmapSet.get(id).recycle();
			 unFocusBitmapSet.set(id, null);
		 }
	}
	
}
