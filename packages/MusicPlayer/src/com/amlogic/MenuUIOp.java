package com.amlogic;

import java.util.ArrayList;
import java.util.List;

import org.geometerplus.zlibrary.ui.android.library.ZLAndroidActivity;

import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AbsoluteLayout;

import com.amlogic.pmt.Resolution;
import com.amlogic.AmlogicMenu.Menucontrol;
import com.amlogic.View.Balancer;
import com.amlogic.View.DialogView;
import com.amlogic.View.MenuGroup;
import com.amlogic.View.MenuGroup1;
import com.amlogic.View.ProgressBarView;
import com.amlogic.View.SelectFrame;

public class MenuUIOp {

	private MenuGroup1 	     MG = null ;
	private SelectFrame  	 SF = null ;
	private ProgressBarView  pBV= null ;
	private Balancer         balancer= null ;
	private DialogView       dialog= null ;
	private Menucontrol      mControl= null ;
	private int              MGroupBGKind= MenuGroup.noplayer; 
	
	public MenuUIOp(Menucontrol menucontrol,String entrance)
	{
		mControl = menucontrol;
		getMGroupBGKind(entrance);
		MG = new MenuGroup1(mControl.getContext(),null,MGroupBGKind);
	    MG.setMenuGroupListener(mControl);
		SF = new SelectFrame(mControl.getContext(), null); 
		SF.setSelectFrameListener(mControl);
		SF.setFocusable(false);
		mControl.addView(SF);  
		
	}
	
	
	public void showMenu()
	{
		AbsoluteLayout.LayoutParams paramp ;
		if (MGroupBGKind == MenuGroup.nobar_player || MGroupBGKind == MenuGroup.bar_player)
			paramp = new AbsoluteLayout.LayoutParams( (int)(1920*Resolution.getScaleX()),
					(int)(238*Resolution.getScaleY()),0,(int)Math.ceil((1080-238)*Resolution.getScaleY()));	 
		else
			paramp = new AbsoluteLayout.LayoutParams( (int)(1920*Resolution.getScaleX()),
					(int)(152*Resolution.getScaleY()),0,(int)((1080-152)*Resolution.getScaleY()));	 
	    MG.setLayoutParams(paramp);   
	    MG.initFocusID();  
	    mControl.addView(MG);
	    MG.setFocusable(true);
	    MG.requestFocus();
	    
	}
	public void ShowL2Menu()    
	{
		MG.setFocusable(true);
		MG.requestFocus();
		MG.initFocusID();
		MG.invalidate();
		MG.showFirstSelectFrame();
	}
	
	public void BackToL1Menu(int FocusID)
	{
		MG.setFocusable(true);
		MG.requestFocus();
		MG.setFocusID(FocusID);
		MG.invalidate();
	}
	
	public void showBalancer(List<String> initBalancerPara)
	{
		MG.setFocusable(false);
		AbsoluteLayout.LayoutParams paramp = new AbsoluteLayout.LayoutParams(627, 376, (1920 - 627) / 2, 240);
		balancer = new Balancer(mControl.getContext(), null);
		balancer.setBalancerKeyListener(mControl);
		balancer.setLayoutParams(paramp);
		
		balancer.initBalancerRescource(mControl.xmlStringItem,initBalancerPara);
		mControl.addView(balancer,Resolution.getWidth(),Resolution.getHeight());	
		
		balancer.setFocusable(true);
		balancer.requestFocus();
	}
	
	public void showDialog()
	{
		MG.setFocusable(false);
		AbsoluteLayout.LayoutParams paramp = new AbsoluteLayout.LayoutParams(702, 480, (1920 - 702) / 2, 250);
		dialog = new DialogView(mControl.getContext(), null);
		dialog.setLayoutParams(paramp);
		dialog.initDialogResource(mControl.xmlStringItem);
		dialog.setDialogListener(mControl);
		
		mControl.addView(dialog);
		dialog.setFocusable(true);
		dialog.requestFocus();
	}
	
	public void showSelectFrame(int Focus,int initnum,boolean ifselect,ArrayList<String> SelectContext,ArrayList<String>   SelectContextID)
	{
		int offset = 0;// 20101202
		if (MGroupBGKind == MenuGroup.noplayer)
			offset = 152;//20101202 name or process bar except tv_set bar temp
		else
			offset = 238;
		SF.setSFData(SelectContext,SelectContextID,ifselect);
		int[] hightArr={80,135,190,245,300,355,410,465,528,583};
		int hightTotal = 0;
		if(SelectContext.size() >9)
			hightTotal = 9;
		else
			hightTotal = SelectContext.size();
		AbsoluteLayout.LayoutParams paramp = new AbsoluteLayout.LayoutParams( 
				(int)(256*Resolution.getScaleX()), 
				(int)(hightArr[hightTotal-1]*Resolution.getScaleY()),
				(int)((185*Focus+MG.leftoffset)*Resolution.getScaleX())/*//sunsikai add 20110216*/,
				(int)((1080- offset-hightArr[hightTotal-1])*Resolution.getScaleY() ));//20101202
		
		SF.setVisibility(View.VISIBLE);
		if(initnum != -1)
			SF.initSelectItem(initnum);
		SF.setLayoutParams(paramp);				  			  
		SF.setSFLayout(paramp.width,paramp.height,paramp.x,paramp.y);

		
	}
	
	public void HideSelectFrame()
	{
		  if(SF.getVisibility() != View.INVISIBLE)
		  {
			  SF.restoreSFData();
		  }	
	}
	
	public void showProgressBar(String MenuItemName,String initnum)
	{		
		if(pBV==null){
		  	  int offset = 0;
			  if (MGroupBGKind == MenuGroup.noplayer){
				  offset = 152;
			  }
			  else{
				  offset = 238;
			  }
			  MG.setFocusable(false);  
			  mControl.removeViewInLayout(MG);
			  AbsoluteLayout.LayoutParams paramp = new AbsoluteLayout.LayoutParams( 1920,offset,0,1080-offset);	
			  pBV = new ProgressBarView(mControl.getContext(),null,MenuItemName,MGroupBGKind);
			  pBV.setProgressBarListener(mControl);		  
			  if(initnum!=null&&!initnum.equals("")){
				  pBV.setProgress(Integer.parseInt(initnum));
			  }
			  mControl.addView(pBV, paramp);
	          pBV.requestFocus();		
		}
	}
	
	public void RpbvSmg()
	{
		if(pBV!=null){
			mControl.removeViewInLayout(pBV);
			pBV=null;
			mControl.addView(MG);
	    	MG.setFocusable(true);  
	    	MG.requestFocus();
	    	MG.myinvalidate();
		}

	}
	
	public void RpbvhideSmg()
	{
		if(pBV!=null){
			mControl.removeViewInLayout(pBV);
			pBV=null;
	    	MG.setFocusable(true);  
	    	MG.requestFocus();
		}
	}
	
	public void RsfSmg()
	{
		  if(SF.getVisibility() != View.INVISIBLE)
		  {
			  HideSelectFrame();
			  //MG.initFocusID();
			  MG.update();
			  MG.setFocusable(true);  
		      MG.requestFocus();
		  }
	}
	public void RsfSmg(int Key)
	{
		 HideSelectFrame();
	     MG.setFocusable(true);  
	     MG.requestFocus();
	     MG.onKeyDown(Key, new KeyEvent(Key, Key));
	}
	public void RbalancerSmg()
	{
		if(balancer!=null){
			mControl.removeViewInLayout(balancer);
			balancer= null;
			MG.setFocusable(true);
			MG.requestFocus();
		}

	}
	public void RdialogSmg()
	{
		if(dialog!=null){
			mControl.removeViewInLayout(dialog);
			dialog=null;
			MG.setFocusable(true);
			MG.requestFocus();
		}

	}
	
	
	
	public void setSFFocus()
	{
		 if(SF != null)
		 {
			 SF.setmyFocus();
			 MG.setFocusable(false);
			 SF.setFocusable(true);
			 SF.requestFocus();
			 
		 }
		 
	}
	
	public void setMGFocus( boolean focus )
	{
		if( null != MG )
		{
			MG.setFocusable(focus);
			if( focus )
			{
				MG.requestFocus();
			}	
		}
	}
	

	
	
	public MenuGroup1 getMGInstance()
	{
		return MG;
	}
	public SelectFrame getSFInstance()
	{
		return SF;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	//**************************************************************
	private  void getMGroupBGKind(String entrance)
	{
		if(entrance.equals("Local") ||entrance.equals("Music")||entrance.equals("Xunlei")||
				   entrance.equals("Voole")||entrance.equals("MusPT") || entrance.equals("MusPic")||
				   entrance.equals("MusT"))
		{
			MGroupBGKind = MenuGroup.bar_player;
		}
		else
		if(entrance.equals("Picture") ||entrance.equals("Txt") || entrance.equals("PicT") )
		{
			MGroupBGKind = MenuGroup.nobar_player;
		}
			
	}
	
	
}
