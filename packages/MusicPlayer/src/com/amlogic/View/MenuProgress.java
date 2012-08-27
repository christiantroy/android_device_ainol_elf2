package com.amlogic.View;

import java.sql.Date;
import java.text.SimpleDateFormat;
import  java.util.TimeZone;

import com.amlogic.pmt.Resolution;
import com.amlogic.BreakText;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;


public class MenuProgress {
	private SimpleDateFormat 	 sdf ;
	private Paint   			 Rectpaint 						= new Paint();	
	private Paint   			 Fontpaint 						= new Paint();	
	private Paint   			 PosScalepaint 						= new Paint();	
	private boolean     	     myVisible   					= false;
	public int				     bartotal		 			    = 0;
	public int 				     bar_cur_pos 					= 0;
	public String 			     playername 					= null;
	private int 		     	 myType ;
	public String 				 playerPosScale 				= null;
	private int 				 dst_width                      = 0;
	private String               mytime                         = null;
	MenuProgress()
	{
		TimeZone tz=TimeZone.getTimeZone("GMT + 0");
		sdf = new SimpleDateFormat("HH:mm:ss");
		sdf.setTimeZone(tz); 
		Fontpaint.setColor(Color.WHITE);
		Fontpaint.setTextSize(28*Resolution.getScaleX());
		Fontpaint.setAntiAlias(true);
		
		Rectpaint.setARGB(0xaf, 0x6c, 0xc0, 0xff);
		
		PosScalepaint.setColor(Color.WHITE);
		PosScalepaint.setTextSize(28*Resolution.getScaleX());
		PosScalepaint.setAntiAlias(true);
		PosScalepaint.setTextAlign(Paint.Align.RIGHT);
	}
	
	public void setVisible(boolean visible)
	{
		myVisible = visible;
	}
	public void update()
	{
		if(bartotal != 0 && bar_cur_pos != 0)
		{
			dst_width = 1840*bar_cur_pos/bartotal ;
			if( dst_width < 1 )
				dst_width = 1; 
			mytime = formatSeconds2HHmmss(bar_cur_pos)+'/' +formatSeconds2HHmmss(bartotal);
		}
	}
	public void myDraw(Canvas canvas)
	{
		if(myVisible)
		{
			if(bartotal != 0 && bar_cur_pos != 0)
			{
				canvas.drawRect( (int)(44*Resolution.getScaleX()), (int)((70+5)*Resolution.getScaleY()), (int)((44+dst_width)*Resolution.getScaleX()), (int)((90-5)*Resolution.getScaleY()), Rectpaint);
				canvas.drawText(mytime,(int)(1600*Resolution.getScaleX()), (int)(45*Resolution.getScaleY()), Fontpaint);
			}
			if(playername != null)
			{
				playername = BreakText.breakText(playername, (int)(28*Resolution.getScaleX()), (int)(900*Resolution.getScaleX()));
				if(playername != null )
				{
					if(myType == MenuGroup.bar_player)	
						canvas.drawText(playername,(int)(80*Resolution.getScaleX()), (int)(45*Resolution.getScaleY()), Fontpaint);
					else
					if(myType == MenuGroup.nobar_player)	
						canvas.drawText(playername,(int)(80*Resolution.getScaleX()), (int)(50*Resolution.getScaleY()), Fontpaint);
				}
			}
			if(playerPosScale != null)
					canvas.drawText(playerPosScale,(int)(1520*Resolution.getScaleX()), (int)(45*Resolution.getScaleY()), PosScalepaint);
			
		}
	}
	public void setType(int type)
	{
		myType = type;
	}
	private String formatSeconds2HHmmss(int seconds) {
		long ms = seconds * 1000;
		Date date = new Date(ms);
		return sdf.format(date);
	}
}
