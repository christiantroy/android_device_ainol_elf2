/**
 * 
 */
package com.amlogic.pmt.menu;

import java.util.ArrayList;

import org.geometerplus.zlibrary.core.options.ZLIntegerOption;
import org.geometerplus.zlibrary.text.view.style.ZLTextStyleCollection;
import org.geometerplus.zlibrary.ui.android.library.ZLAndroidActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.View;
import android.widget.AbsoluteLayout;

import com.amlogic.pmt.DataProvider;
import com.amlogic.pmt.RenderView;
import com.amlogic.pmt.ContentProvider.PMTContentProvider;
import com.amlogic.pmt.music.musiclistener;
import com.amlogic.Listener.MenuCallbackListener;
import com.amlogic.AmlogicMenu.Menucontrol;

/**
 * @author Administrator
 *
 */
public class MenuOp {
	private static final String TAG = "MenuOp";
	private Menucontrol mControl =null ;
	private String myType = null;
	private RenderView mGLRenderView = null;
	private Activity myActivity;
	private PMTContentProvider myConProvider = null;

	
	public MenuOp(Activity activity )
	{
		myActivity = activity;
		myConProvider = new PMTContentProvider(myActivity);
	}
	
	public void setType(String type)
	{
		myType = type;
	}
	
	public void setRenderView(RenderView glRenderView)
	{
		mGLRenderView = glRenderView;
	}
	
	public Menucontrol getMenuInstance()
	{
		return mControl;
	}
	
	public void ShowMenu(String menutype)
	{
		myType = menutype;
		if(mControl == null)
		{
			ArrayList<String> State = new ArrayList<String>();
			InitData(State);
//			Log.d("SHowMenu", "" +System.currentTimeMillis());
			Log.d(TAG,"param:"+menutype+","+State);
			mControl = new Menucontrol(myActivity,null,menutype,State);
//			AbsoluteLayout.LayoutParams paramp = 
//							new AbsoluteLayout.LayoutParams( 1920,1080,0,0);	 
			mControl.setInitVolumn(myConProvider.getMyParam("Volumn"));
			if(myType.indexOf("T")!= -1)
			{
//				Mcontrol.set_play_name(ConvertFileName(textName));
				 mControl.setMenuCallbackListener((MenuCallbackListener) 
		    				    this.mGLRenderView.GetTxtLayoutInstance());
			}			
			if(myType.indexOf("P")!= -1)
			{
			    mControl.setMenuCallbackListener((MenuCallbackListener) 
			    				this.mGLRenderView.GetPictureLayoutInstance());
			}
			if(myType.indexOf("M")!= -1)
			{
				mControl.setMenuCallbackListener((MenuCallbackListener) 
								this.mGLRenderView.GetMusicLayoutInstance());	
			}
			ZLAndroidActivity tempActivity = (ZLAndroidActivity)myActivity;
			tempActivity.layout.addView(mControl);//tempActivity.layout.addView(Mcontrol,paramp);
//			Log.d("SHowMenu", "" +System.currentTimeMillis());
		}
		else
		{
			if(mControl.getVisibility() == View.VISIBLE){
				mControl.setVisibility(View.INVISIBLE);
			}
			else{
				mControl.setVisibility(View.VISIBLE);
				mControl.requestFocus();
			}
		}
	}
	
	public void HideMenu()
	{
		if(mControl != null)
		{
			mControl.setVisibility(View.INVISIBLE);
		}
		
	}
	public void DestoryMenu()
	{
		
		if(mControl!=null)
		{ 
			mControl.CallMenucontrolunbindservice();
		} 
		ZLAndroidActivity tempActivity = (ZLAndroidActivity)myActivity;
		tempActivity.layout.removeView(mControl);
		mControl = null;
//		Runtime.getRuntime().gc();
		
	}
	
	private void InitData(ArrayList<String> state)
	{
		String data = null;
		if(myType.indexOf("T")!= -1)
		{
			int fontsize = ZLTextStyleCollection.Instance().getBaseStyle().getFontSize();
			if(fontsize == 45)
				data = "shortcut_txt_fontsize_big";
			else
			if(fontsize == 40)
				data = "shortcut_txt_fontsize_mid";
			else
			if(fontsize == 35)
				data = "shortcut_txt_fontsize_small";
			else
				data = "shortcut_txt_fontsize_mid";
			state.add(data);
			
			
			int flag = new ZLIntegerOption("BookMark", "flag", 0).getValue();
			if(flag == 0)
				data = "shortcut_txt_breakpoint_first";
			else
		    if(flag == 1)
				data = "shortcut_txt_breakpoint_breakpoint";
			else
				data = "shortcut_txt_breakpoint_first";
			state.add(data);
		}
	
		if(myType.indexOf("P")!= -1)
		{
			data = "shortcut_common_pause_";
			state.add(data);
		}	

		if(myType.indexOf("M")!= -1)
		{
			data = "shortcut_common_pause_";
			state.add(data);	
			data = "shortcut_common_ff_";	
			state.add(data);	
			data = "shortcut_common_fb_";	
			state.add(data);		
		}
	}

	public String getPlayType()
	{
		if(mControl != null)
			return mControl.getPlayType();
		else
		   return null;
	}
	
	

	
		
}
