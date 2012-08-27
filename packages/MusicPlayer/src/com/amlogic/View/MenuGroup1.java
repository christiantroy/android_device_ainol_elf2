package com.amlogic.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.amlogic.pmt.Resolution;
import com.amlogic.Listener.AmlogicMenuListener;
import com.amlogic.control.playerStatus;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

@SuppressWarnings("unchecked")
public class MenuGroup1 extends View{

	private	List <MenuItem>  	 itemlist						= new ArrayList();
	private MenuItem  		  	 leftArrow;
	private MenuItem  		  	 rightArrow;
	private MenuItem  		  	 background;
	private MenuProgress         progress                      = new MenuProgress();
	private List<String>  		 menu ;
	private List<String>  		 tvSetMenu ;
	private List<String>  		 programMenu;
	private List<String>  		 musicMenu ;
	private List<String>  		 picMenu ;
	private List<String>  	     textMenu ;
	private List<String>  		 setup3DMenu ;
	private List<String>  		 defaultMenu ;
	private  int  		  		 absoluteFoucsID 				= 0;
	private  int  		  		 relativeFoucsID			    = 0;
	private int 			     showTotal 						= 10;
	private int 				 itemTotal 						= 0;
	private String 				 showState 						= null;
	public int 				     leftoffset						= 0;//sunsikai add 20110216	
	private AmlogicMenuListener checkedMenu;
	private int 				 myType ;
	
	public MenuGroup1(Context context, AttributeSet attrs,int type)
	{
		super(context,attrs);
		this.setFocusable(true);
		menu 		= new ArrayList<String>();
		tvSetMenu   = new ArrayList<String>();
		programMenu = new ArrayList<String>();
		musicMenu   = new ArrayList<String>();
		picMenu	    = new ArrayList<String>();
		textMenu 	= new ArrayList<String>();
		setup3DMenu = new ArrayList<String>();
		defaultMenu = new ArrayList<String>(); 
		myType      = type;
		
		progress.setType(myType);
		
	}

	
	
	public  void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
		background.myDraw(canvas);
		progress.myDraw(canvas);
		for(int i=0;i<itemlist.size();i++)
		{
			itemlist.get(i).myDraw(canvas);
		}
		rightArrow.myDraw(canvas);
		leftArrow.myDraw(canvas);
	}
	
	public void update()
	{
		hideAllItem();
		background.setVisible(true);
		int LastShowTotal = 0;
		itemTotal = menu.size();
		if (itemTotal < showTotal) 
			LastShowTotal = itemTotal;
		else 
			LastShowTotal = showTotal;
		
		if( showTotal < itemTotal && (absoluteFoucsID > relativeFoucsID))
		{
			for(int i = 0 ;i < itemTotal - showTotal; i++ )
			{
				if(i == relativeFoucsID )
					itemlist.get(showTotal+i).setStatus(true);									
				else
					itemlist.get(showTotal+i).setStatus(false);
				itemlist.get(showTotal+i).setVisible(true);
			}
			leftArrow.setVisible(true);
		}
		else 
		{
			if( showTotal < itemTotal)
				rightArrow.setVisible(true);
			for(int i = 0 ;i < LastShowTotal; i++ )
			{	
				if(i == relativeFoucsID )
					itemlist.get(i).setStatus(true);
				else
				{
					filterMenuItem(i);
					itemlist.get(i).setStatus(false);
		        }		
				 itemlist.get(i).setVisible(true);
			}
		}
		if(myType != MenuGroup.noplayer)	
		{
			progress.update();
			progress.setVisible(true);
		}
			
	}

	public double getPlayProcess(int x ,int y)
		{
		int top=880+25;
		int buttom=880+90;
		x=(int)(x/Resolution.getScaleX());
		y=(int)(y/Resolution.getScaleY());
		if(y>top && y<buttom)
		{
			if(x>40 && x<1940)
				{
				double duration= (double)(x-40+1900);
				double process =(double) (duration/1900.0);
				process =(double) (process-1.0);
				return process;
				}
		}
		return -1;
		}

	public int mouseClick(int x, int y)
	{
		int top=itemlist.get(0).mytop+880;
		int buttom=itemlist.get(0).mybottom+880;
		x=(int)(x/Resolution.getScaleX());
		y=(int)(y/Resolution.getScaleY());
		if(y>top && y<buttom)
		{
			for(int i=0;i<itemlist.size();i++)
			{
				int left =itemlist.get(i).myleft;
				int right =itemlist.get(i).myright;
				if(x<right&&x>left)
				{
				return i;
				}
			}	
		}
		return -1;
	}
	
	protected void onLayout (boolean changed, int left, int top, int right, int bottom) 
	{
		
	}
	public boolean onKeyDown(int keyCode, KeyEvent event) {
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
		case KeyEvent.KEYCODE_DPAD_LEFT:
			if(itemTotal <= showTotal)
			{
				if(relativeFoucsID > 0 )
				{
					absoluteFoucsID --;
					relativeFoucsID --;
					if(relativeFoucsID>2&&absoluteFoucsID>2){
						if(menu.get((absoluteFoucsID)).equals("shortcut_setup_audio_increase_bass_")){
							
							String state=checkedMenu.GetSelectFrameState("shortcut_setup_audio_srs_");
							if(state!=null)
							if(state.equals("OFF")){						
								absoluteFoucsID -=2;
								relativeFoucsID -=2;	
								refreshUI(keyCode,4);
								checkedMenu.CheckedMenuHandle(KeyEvent.KEYCODE_DPAD_LEFT,relativeFoucsID,menu.get(absoluteFoucsID));
								return super.onKeyDown(keyCode, event); 
							}
						}
					}
					refreshUI(keyCode,2);
					checkedMenu.CheckedMenuHandle(KeyEvent.KEYCODE_DPAD_LEFT,relativeFoucsID,menu.get(absoluteFoucsID));
				}
			}
			else
			{
				if(relativeFoucsID == 0 && (absoluteFoucsID == showTotal))
				{
					relativeFoucsID	 = showTotal -1;
					absoluteFoucsID --;
					refreshUI(keyCode,2);
					checkedMenu.CheckedMenuHandle(KeyEvent.KEYCODE_DPAD_LEFT,relativeFoucsID,menu.get(absoluteFoucsID));
				}
				else
				if(relativeFoucsID > 0 )
				{						
					absoluteFoucsID --;
					relativeFoucsID --;
					if(relativeFoucsID>0&&absoluteFoucsID>0){
						if(menu.get((absoluteFoucsID)).equals("shortcut_setup_sys_woofer_vol_")){
							
							String state=checkedMenu.GetSelectFrameState("shortcut_setup_sys_woofer_switch_");
							if(state!=null)
							if(state.equals("OFF")){						
								absoluteFoucsID --;
								relativeFoucsID --;	
								refreshUI(keyCode,3);
								checkedMenu.CheckedMenuHandle(KeyEvent.KEYCODE_DPAD_LEFT,relativeFoucsID,menu.get(absoluteFoucsID));
								return super.onKeyDown(keyCode, event); 
							}
						}
					}
					refreshUI(keyCode,2);
					checkedMenu.CheckedMenuHandle(KeyEvent.KEYCODE_DPAD_LEFT,relativeFoucsID,menu.get(absoluteFoucsID));
				}
			}
			break;
		case KeyEvent.KEYCODE_DPAD_RIGHT:
			if(itemTotal <= showTotal)
			{
				if(relativeFoucsID < (itemTotal -1) )
				{
					absoluteFoucsID ++;
					relativeFoucsID ++;
					if(menu.get(absoluteFoucsID).equals("shortcut_setup_audio_voice_")){
						
						String state=checkedMenu.GetSelectFrameState("shortcut_setup_audio_srs_");
						if(state!=null)
						if(state.equals("OFF")){
							absoluteFoucsID +=2;
							relativeFoucsID +=2;	
							refreshUI(keyCode,4);
							checkedMenu.CheckedMenuHandle(KeyEvent.KEYCODE_DPAD_RIGHT,relativeFoucsID,menu.get(absoluteFoucsID));
							return super.onKeyDown(keyCode, event); 
						}
					}	
					refreshUI(keyCode,2);
					checkedMenu.CheckedMenuHandle(KeyEvent.KEYCODE_DPAD_RIGHT,relativeFoucsID,menu.get(absoluteFoucsID));
				}
			}
			else
			{
				if(absoluteFoucsID < (showTotal -1) )
				{
					absoluteFoucsID ++;
					relativeFoucsID ++;	
					if(menu.get(absoluteFoucsID).equals("shortcut_setup_sys_woofer_vol_")){
						
						String state=checkedMenu.GetSelectFrameState("shortcut_setup_sys_woofer_switch_");
						if(state!=null)
						if(state.equals("OFF")){
							absoluteFoucsID ++;
							relativeFoucsID ++;	
							refreshUI(keyCode,3);
							checkedMenu.CheckedMenuHandle(KeyEvent.KEYCODE_DPAD_RIGHT,relativeFoucsID,menu.get(absoluteFoucsID));
							return super.onKeyDown(keyCode, event); 
						}
					}										
					refreshUI(keyCode,2);
					checkedMenu.CheckedMenuHandle(KeyEvent.KEYCODE_DPAD_RIGHT,relativeFoucsID,menu.get(absoluteFoucsID));
				}
				else
				{
					if(absoluteFoucsID < (itemTotal -1))
					{
						absoluteFoucsID ++;
						relativeFoucsID = absoluteFoucsID - showTotal ;
						refreshUI(keyCode,2);
						checkedMenu.CheckedMenuHandle(KeyEvent.KEYCODE_DPAD_RIGHT,relativeFoucsID,menu.get(absoluteFoucsID));
					}
					
				}
			}	
			
			break;
			
		case KeyEvent.KEYCODE_DPAD_UP:
		case KeyEvent.KEYCODE_DPAD_DOWN:
			checkedMenu.CheckedMenuHandle(KeyEvent.KEYCODE_DPAD_UP,relativeFoucsID,menu.get(absoluteFoucsID));
			break;
		case KeyEvent.KEYCODE_DPAD_CENTER:
		case KeyEvent.KEYCODE_ENTER:
			checkedMenu.CheckedMenuHandle(KeyEvent.KEYCODE_ENTER,relativeFoucsID,menu.get(absoluteFoucsID));
			if(menu.get(absoluteFoucsID).equals("shortcut_video_3d_setup_")
					||menu.get(absoluteFoucsID).equals("shortcut_common_vol_"))
				return true;
			break;
		
		case KeyEvent.KEYCODE_BACK:
			if(showState.equals("Setup3D")){
				checkedMenu.BackMenuHandle(showState);
				return true;
			}
			else
				checkedMenu.CallMenucontrolunbindservice();
			break; 
		}
		return super.onKeyDown(keyCode, event);
	}
	
	public void  initFocusID() 
    {
		for(int i =0; i<itemlist.size();i++)
			if(relativeFoucsID == i)
				itemlist.get(i).setStatus(true);
			else
				itemlist.get(i).setStatus(false);
		update();
    }
	
	public void setFocusID(int FocusID) {    
		absoluteFoucsID = FocusID;
		relativeFoucsID = FocusID%showTotal;
	}
	
	
	public void  notifydataFinish()
	{
		
		Rect myrect = new Rect();
		int topSpace     = 0;
		if(myType == MenuGroup.nobar_player || myType == MenuGroup.bar_player)	
			topSpace  = 140 ;
		else
			topSpace  = 60 ;
		/**********left arrow**********/
		myrect.top =  topSpace;
		myrect.left = 8;
		myrect.right = myrect.left + 27;
		myrect.bottom = myrect.top + 40; 
		leftArrow =  new MenuItem(this.getContext(),"shortcut_bg_arrow_left",myrect,true); 
		/**********right arrow**********/
		myrect.top =  topSpace;
		myrect.left = 1885;
		myrect.right = myrect.left + 27;
		myrect.bottom = myrect.top + 40; 
		rightArrow =  new MenuItem(this.getContext(),"shortcut_bg_arrow_right",myrect,true); 
		/**********background arrow**********/
		myrect.top =  0;
		myrect.left = 0;
		myrect.right = 1920;
		myrect.bottom = 200;  //anything
		String bgid = null;
		if(myType == MenuGroup.bar_player)
			bgid = "shortcut_bg_bar_progress";	
		else
		if(myType == MenuGroup.nobar_player )
			bgid = "shortcut_bg_bar_info";	
		else
			bgid = "shortcut_bg_bar";
		background =  new MenuItem(this.getContext(),bgid,myrect,true); 
		
		
	}
	private Rect getItemSlot(int pos)
	{
		if(pos >= itemTotal)
			return null;
	
		if (itemTotal < showTotal)
			this.leftoffset = (10 - itemTotal) / 2 * 185 + (10 - itemTotal) % 2* 92;//sunsikai add 20110216
		else 
			this.leftoffset = 0;//sunsikai add 20110216
		
		int leftSpace    = 35;
		int topSpace     = 0;
		
		if(myType == MenuGroup.bar_player)	
			topSpace     = 90 ;
		else
		if(myType == MenuGroup.nobar_player)	
			topSpace     = 75 ;
		
		Rect mrect      = new Rect();
		mrect.left      = leftSpace + leftoffset + pos%showTotal * 185;
		mrect.top       = topSpace;
		mrect.right     = mrect.left + 185;  //185  is icon width
		mrect.bottom    = mrect.top  + 145;  //145  is icon hight
		//mrect = scaleRect(mrect);
		return mrect;	
	}
	
	public Rect scaleRect(Rect rect1){
		Rect mrect = rect1;
		mrect.left*=Resolution.getScaleX();
		mrect.top*=Resolution.getScaleY();
		mrect.right*=Resolution.getScaleX();
		mrect.bottom*=Resolution.getScaleY();

		return mrect;
	}
	
	public void  AddMenuItem(String MI,String MediaType)
	{
		if(MediaType.equals("tv_set"))
			tvSetMenu.add(MI);
		else
		if(MediaType.equals("program"))
			programMenu.add(MI);
	    else
		if(MediaType.equals("music"))
			musicMenu.add(MI);
		else
		if(MediaType.equals("picture"))
			picMenu.add(MI);
		else
		if(MediaType.equals("txt"))
			textMenu.add(MI);
		else
		if(MediaType.equals("Setup3D"))
			setup3DMenu.add(MI);
		else
			defaultMenu.add(MI);
	}
	
	public void SetShowState(String state)
	{
		
		showState = state;
//		unFocusBitmapSet.clear();
//		FocusBitmapSet.clear();
		if(showState.equals("music"))
		{
			if(musicMenu.size() != 0)
				menu = musicMenu;
		}else
		if(showState.equals("picture"))
		{
			if(picMenu.size() != 0)
				menu = picMenu;
		}else
		if(showState.equals("txt"))
		{
			if(textMenu.size() != 0)
				menu = textMenu;
		}else
		if(showState.equals("Setup3D"))
		{
			if(setup3DMenu.size() != 0)
				menu = setup3DMenu;
		}else
		if(showState.equals("tv_set"))
		{
			if(tvSetMenu.size() != 0)
				menu = tvSetMenu;
		}else
		if(showState.equals("program"))
		{
			if(programMenu.size() != 0)
				menu = programMenu;
		}else
		{
			if(defaultMenu.size() != 0)
				menu = defaultMenu;
		}
		itemTotal = menu.size();	
		initAllItem();
		
	}
	public void showFirstSelectFrame()    
	{
		checkedMenu.CheckedMenuHandle(KeyEvent.KEYCODE_DPAD_LEFT,relativeFoucsID,menu.get(absoluteFoucsID));
	}
	public void setMenuGroupListener(AmlogicMenuListener MGL)
	{
		checkedMenu = MGL;
	}
	
	
////////////////////////////subsidiary  get info//////////////////////////////////////
	public String getAbsoluteFoucsItem() {
		return menu.get(absoluteFoucsID);
	}
	
	public int getRelativeFoucsID() {    
		return relativeFoucsID;
	}
	
	public void getLeftOffSet(){
		itemTotal = menu.size();
		if (itemTotal < showTotal) {
			this.leftoffset = (10 - itemTotal) / 2 * 185 + (10 - itemTotal) % 2* 92;//sunsikai add 20110216
		} else {
			this.leftoffset = 0;//sunsikai add 20110216
		}
	}
	
	public String GetShowState() {
		return showState;
	}
	
	public int GetFocusID(String id) {
		 int  focus = -1;
		 int total = menu.size();
		 for(int i =0;i<total;i++)
			 if(menu.get(i).equals(id) )		 
			 {
			 	focus = i;
			 	break;
			 }
		return focus;
	}
////////////////////////////finish//////////////////////////////////////////////
	
//////////////////////progress      start///////////////////////////
	public void set_seek_bar_info( int i_total , int i_cur_pos )
	{		
		
		 progress.bartotal = i_total;
		 progress.bar_cur_pos = i_cur_pos;
//		 this.postInvalidate(40, 70+BeatHight,1880,90+BeatHight);
//		 this.postInvalidate(1620, 25+BeatHight,1900,60+BeatHight); 

		 this.myinvalidate(40, 25,1900,90);
		 
		 
	
	}
	public void set_play_name( String name )
	{
		progress.playername = name;
		if(myType == MenuGroup.bar_player)	
			this.myinvalidate(80, 20,980,60);
		else
		if(myType == MenuGroup.nobar_player)	
			this.myinvalidate(80, 20,980,70);
	}
	
	
	public void set_seek_bar_info(String type ,int i_total , int i_cur_pos )
	{		
		if(showState.equals(type))
		{
			progress.bartotal = i_total;
			progress.bar_cur_pos = i_cur_pos;
//			this.postInvalidate(40, 70+BeatHight,1880,90+BeatHight);
//		    this.postInvalidate(1620, 25+BeatHight,1900,60+BeatHight);

		    this.myinvalidate(40, 70,1900,90);
		}
	}
	public void set_play_name( String type ,String name )
	{
		if(showState.equals(type))
		{
			progress.playername = name;
			if(myType == MenuGroup.bar_player)	
				this.myinvalidate(80, 20,980,60);
			else if(myType == MenuGroup.nobar_player)	
				this.myinvalidate(80, 20,980,70);
		}
	}
	public void setplayerPosScale(String type ,String scale)
	{
		if(showState.equals(type))
		{
			progress.playerPosScale = scale;
			this.myinvalidate(1520, 25,1620,60);
		}
	}
	public void setplayerPosScale(String scale)
	{
		progress.playerPosScale = scale;
		this.myinvalidate(1520, 25,1620,60);
	}
	/////////////////////////progress       finish////////////////////////////////////
	
	public void UpdataStatus(String  ID)
	{
		menu.set(absoluteFoucsID, ID);
		clearItemBitmapAndsetID(absoluteFoucsID, ID);
		myinvalidate(itemlist.get(absoluteFoucsID).getRefreshRect());
	}	
	
	public int  HandleSpecialKeyID(int key)
	{	 
		 int  focus = -1;
		 if(key  ==  85)  //play_PAUSE
		 {
			 int total = menu.size();
			 for(int i =0;i<total;i++)
				 if(menu.get(i).equals("shortcut_common_play_") || menu.get(i).equals("shortcut_common_pause_") )		 
				 {
				 	focus = i;
				 	break;
				 }
		 } else 
		 if(key  ==  86) //play_STOP
		 {
			 int total = menu.size();
			 for(int i =0;i<total;i++)
				 if(menu.get(i).equals("shortcut_common_stop_")  )
				 {
				 	focus = i;
				 	break;
				 }
		 }else
		 if(key  ==  90)  //play_FF
		 {
			 int total = menu.size();
			 for(int i =0;i<total;i++)
				 if(menu.get(i).contains("shortcut_common_ff"))
				 {
				 	focus = i;
				 	break;
				 }
		 }else
		 if(key  ==  89)//play_FB
		 {
			 int total = menu.size();
			 for(int i =0;i<total;i++)
				 if(menu.get(i).contains("shortcut_common_fb"))
				 {
				 	focus = i;
				 	break;
				 }
		 }else
		 if(key  ==  92)//play_FB
		 {
			 int total = menu.size();
			 for(int i =0;i<total;i++)
				 if(menu.get(i).contains("shortcut_common_fb"))
				 {
				 	focus = i;
				 	break;
				 }
		 }else
		 if(key  ==  88)//play_PROE
		 {
			 int total = menu.size();
			 for(int i =0;i<total;i++)
				 if(menu.get(i).contains("prev"))
				 {
				 	focus = i;
				 	break;
				 }
		 }else
		 if(key  ==  87) //play_NEXT
		 {
			 int total = menu.size();
			 for(int i =0;i<total;i++)
				 if(menu.get(i).contains("next"))
				 {
				 	focus = i;
				 	break;
				 }
		 }else
		 if(key  ==  91) //mute
		 {
			 int total = menu.size();
			 for(int i =0;i<total;i++)
				 if(menu.get(i).contains("shortcut_common_mute_"))
				 {
				 	focus = i;
				 	break;
				 }
		 }
		return focus;
	}
	
	
	public void UpdataMenuData(Map<Integer,String> map)
	{
		for(int i = 0 ;i <10; i++ )
		{
			String data = map.get(i);
			if(data != null)
			{
				menu.set(i, data);
				clearItemBitmapAndsetID(i,data);
			}
		}
		if(myType == MenuGroup.bar_player)	
			this.myinvalidate(0,90 ,this.getWidth(),(90+this.getHeight()));
		else
		if(myType == MenuGroup.nobar_player)	
			this.myinvalidate(0,75,this.getWidth(),(75+this.getHeight()));
		else
			this.myinvalidate();
	}
	
	
	public void  recoverPlayerStatus(List<String> list)
	{		
		if(list == null)
			return;
		for(int i =0;i< list.size();i++)
		{
			int total = menu.size();
			for(int ii =0;ii<total;ii++)
			{
				if(list.get(i).equals(playerStatus.player_PAUSE)
						&&menu.get(ii).contains("shortcut_common_pause_"))
				{
					 menu.set(ii, "shortcut_common_play_");
					 clearItemBitmapAndsetID(ii, "shortcut_common_play_");
					 
					 break;
				}else
				if(list.get(i).equals(playerStatus.player_PLAY)
						&&menu.get(ii).contains("shortcut_common_play_"))
				{
					 menu.set(ii, "shortcut_common_pause_");
					 clearItemBitmapAndsetID(ii, "shortcut_common_pause_");
					 break;	 
				}else
				if(list.get(i).equals(playerStatus.player_FB)
						&&menu.get(ii).contains("shortcut_common_fb"))
				{
					 menu.set(ii, "shortcut_common_fb_");
					 clearItemBitmapAndsetID(ii, "shortcut_common_fb_");
					 break;
				}else
				if(list.get(i).equals(playerStatus.player_FF)
						&&menu.get(ii).contains("shortcut_common_ff"))
				{
					 menu.set(ii, "shortcut_common_ff_");
					 clearItemBitmapAndsetID(ii, "shortcut_common_ff_");
					 break;
				}
			}
			
		}
		if(myType == MenuGroup.bar_player)	
			this.myinvalidate(0, 90,this.getWidth(),(90+this.getHeight()));
		else
		if(myType == MenuGroup.nobar_player)	
			this.myinvalidate(0,75,this.getWidth(),(75+this.getHeight()));
		else
			this.myinvalidate();
	}
	
	
	public void  recoverPlayerStatus(String type,List<String> list)
	{		
		if(list == null)
			return;
		if(showState.equals(type))
			recoverPlayerStatus(list);
		else
		{
			List<String>  tempMenu = null;
			if(type.equals("music"))
			{
				if(musicMenu.size() != 0)
					tempMenu = musicMenu;
			}
			else
			if(type.equals("picture"))
			{
				if(picMenu.size() != 0)
					tempMenu = picMenu;
			}
			else
			if(type.equals("txt"))
			{
				if(textMenu.size() != 0)
					tempMenu = textMenu;
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
	
	private void clearItemBitmapAndsetID(int id,String itemID)
	{
		if(id <itemlist.size())
		{
			 if(itemlist.get(id) != null)
			 {
				 itemlist.get(id).recycleBitmap();
				 itemlist.get(id).setItemID(itemID);
			 }
		}
	}
	
	private void initAllItem()
	{
		for(int i =0;i < itemlist.size();i++)
		{
			clearItemBitmapAndsetID(i,null);
		}
		if(itemlist.size() > 0)
			itemlist.removeAll(itemlist )	;

		for(int i =0;i < menu.size();i++)
		itemlist.add( new MenuItem(this.getContext(),menu.get(i),getItemSlot(i)));
		for(int i =0; i<itemlist.size();i++)
			if(relativeFoucsID == i)
				itemlist.get(i).setStatus(true);
			else
				itemlist.get(i).setStatus(false);
		setFocusID(0);
		for(int i =0; i<itemlist.size();i++)
			if(relativeFoucsID == i)
				itemlist.get(i).setStatus(true);
			else
				itemlist.get(i).setStatus(false);
		
	}
/////////////////////////////private function////////////////////////////////////
	private void hideAllItem()
	{
		for(int i=0;i<itemlist.size();i++)
		{
			itemlist.get(i).setVisible(false);
			itemlist.get(i).setStatus(false);
		}
		rightArrow.setVisible(false);
		leftArrow.setVisible(false);
		progress.setVisible(false);
	}
	private void refreshUI(int keyCode,int total)
	{
		Rect myRect  = new Rect();
		switch (keyCode) 
		{
			case KeyEvent.KEYCODE_DPAD_LEFT:
				if(relativeFoucsID< showTotal -1)
				{
					myRect.left  =  itemlist.get(absoluteFoucsID).getRefreshRect().left;
					myRect.top   =  itemlist.get(absoluteFoucsID).getRefreshRect().top;
					myRect.right =  myRect.left  +185*total;
					myRect.bottom =  myRect.top  +145;
				}
				else
				{
					myRect.left  =  leftArrow.getRefreshRect().left;
					myRect.top   =  itemlist.get(0).getRefreshRect().top;
					myRect.right =  1920;
					myRect.bottom =  myRect.top  +145;
				}
			break;
			case KeyEvent.KEYCODE_DPAD_RIGHT:
				
				if(relativeFoucsID == 0 && absoluteFoucsID == showTotal)
				{
					myRect.left  =  8;
					myRect.top   =  itemlist.get(0).getRefreshRect().top;
					myRect.right =  1920;
					myRect.bottom =  myRect.top  +145;
				}
				else
				{
					myRect.left  =  itemlist.get(absoluteFoucsID-(total -1)).getRefreshRect().left;
					myRect.top   =  itemlist.get(absoluteFoucsID-(total -1)).getRefreshRect().top;
					myRect.right =  myRect.left  +185*total;
					myRect.bottom =  myRect.top  +145;
				}
			break;
			
		}
		myinvalidate(myRect);
	}
	public void myinvalidate(Rect myRect)
	{
		update();
		Rect rec = scaleRect(myRect);
		this.invalidate(rec);
	}
	public void myinvalidate(int i,int j,int k ,int l )
	{
		update();
		this.invalidate((int)(i*Resolution.getScaleX()),(int)(j*Resolution.getScaleY()),(int)(k*Resolution.getScaleX()),(int)(l*Resolution.getScaleY()));
	}
	public void myinvalidate()
	{
		update();
		this.invalidate();
	}
	private void filterMenuItem(int id)
	{
		if(menu.get(id).equals("shortcut_setup_sys_woofer_vol_")){
			if(checkedMenu!=null){
				String state=checkedMenu.GetSelectFrameState("shortcut_setup_sys_woofer_switch_");
			if(state!=null)
			if(state.equals("OFF"))
				clearItemBitmapAndsetID(id,"shortcut_setup_sys_woofer_vol_disable_");
			else
				clearItemBitmapAndsetID(id,"shortcut_setup_sys_woofer_vol_");
			}
		}
		else if(menu.get(id).equals("shortcut_setup_audio_voice_")
				||menu.get(id).equals("shortcut_setup_audio_increase_bass_"))
		{
			if(checkedMenu!=null){
				String state=checkedMenu.GetSelectFrameState("shortcut_setup_audio_srs_");
			if(state!=null)
				if(state.equals("OFF")){
					if(menu.get(id).equals("shortcut_setup_audio_voice_"))
					clearItemBitmapAndsetID(id,"shortcut_setup_audio_voice_disable_");
					else
					clearItemBitmapAndsetID(id,"shortcut_setup_audio_increase_bass_disable_");
				}
				else
				{
					if(menu.get(id).equals("shortcut_setup_audio_voice_"))
					clearItemBitmapAndsetID(id,"shortcut_setup_audio_voice_");
					else
					clearItemBitmapAndsetID(id,"shortcut_setup_audio_increase_bass_");
				}
			}
		}
	}
}
