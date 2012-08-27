package com.amlogic.View;

import com.amlogic.AmlogicMenu.SearchDrawable;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;

import com.amlogic.pmt.Resolution;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import com.amlogic.pmt.MiscUtil;

public class MenuItem {
	int myleft,mytop,myright,mybottom;
	private		Bitmap 			unfocus     = null;
	private		Bitmap 			focus  		= null;
	private		Bitmap 			fixed  		= null;
	private     boolean     	myStatus    = false;
	private     boolean     	myVisible    = false;
	private     boolean     	myFixedID    = false;
	private     String          MenuID 		= null;
	private		SearchDrawable  SearchID    = null;
	private		Context			mycontext;
	MenuItem(Context context,String id,Rect rect)
	{
		myleft    = rect.left;
		mytop     = rect.top;
		myright   = rect.right;
		mybottom  = rect.bottom;
		MenuID    = id;
		mycontext = context;
		SearchID  = new SearchDrawable(mycontext);
		
	}
	
	MenuItem(Context context,String id,Rect rect,boolean fixid)
	{
		myleft    = rect.left;
		mytop     = rect.top;
		myright   = rect.right;
		mybottom  = rect.bottom;
		MenuID    = id;
		mycontext = context;
		SearchID  = new SearchDrawable(mycontext);
		myFixedID = fixid;
		
	}
	
	public void setStatus(boolean status)
	{
		myStatus = status;
	}
	
	public void setVisible(boolean visible)
	{
		myVisible = visible;
	}
	
	private Bitmap drawCenterAlignText(Bitmap bitmap, String str,int fontSize)
	{
		Bitmap bmp = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
		if (bmp == null)
			return null;
		Canvas canvas = new Canvas(bmp);
		Rect src = new Rect(0, 0, bitmap.getWidth(), (int)(85*Resolution.getScaleY()));
		Rect dst = new Rect(0, 0, bitmap.getWidth(), (int)(85*Resolution.getScaleY()));
		canvas.drawBitmap(bitmap, src, dst, null);
		
		Paint paint = new Paint();
		
		paint.setColor(Color.WHITE);
		paint.setTextSize(fontSize);
		paint.setTextAlign(Align.CENTER);
		paint.setAntiAlias(true);
		canvas.drawText(str, bitmap.getWidth()/2, bitmap.getHeight()-(int)(15*Resolution.getScaleY()), paint);
		
		return bmp;
	}
	
	public void myDraw(Canvas canvas)
	{
		//Scale the x, y
		int x, y;
		Rect src,dst;
		x = (int)(Resolution.getScaleX() * myleft);
		y = (int)(Resolution.getScaleY() * mytop);

		if(myVisible)
		{
			if(myFixedID)
			{
				if(fixed == null)
					fixed = SearchID.getBitmap(MenuID);	
				Matrix matrix = new Matrix();
				canvas.drawBitmap(fixed, x, y, null);
			}
			else
			{
				if(myStatus)
				{
					if(focus == null)
						focus = SearchID.getBitmap(MenuID + "sel");	
				
					Bitmap bmp = drawCenterAlignText(focus, MiscUtil.getStringByID(mycontext, 
							MenuID), (int)(30*Resolution.getScaleX()));
					if (bmp == null)
					{
						canvas.drawBitmap(focus, x, y, null);
					}
					else
					{
						canvas.drawBitmap(bmp, x, y, null);
						bmp.recycle();
					}
				}
				else
				{
					if(unfocus == null)
						unfocus = SearchID.getBitmap(MenuID + "unsel");	
					src = new Rect(0, 0, unfocus.getWidth(), unfocus.getHeight()/2);
					dst = new Rect(x, y, x+unfocus.getWidth(), y+unfocus.getHeight()/2);
					//canvas.drawBitmap(unfocus, src, dst, null);
					Bitmap bmp = drawCenterAlignText(unfocus, MiscUtil.getStringByID(mycontext, 
							MenuID), (int)(30*Resolution.getScaleX()));
					if (bmp == null)
					{
						canvas.drawBitmap(unfocus, x, y, null);
					}
					else
					{
						canvas.drawBitmap(bmp, x, y, null);
						bmp.recycle();
					}
				}
			}	
		}
	}
	
//	public void myDraw(Canvas canvas,int w,int h)
//	{
//		if(myVisible)
//		{
//			if(myFixedID)
//			{
//				if(fixed == null)
//					fixed = SearchID.getBitmap(MenuID,w,h);	
//				Matrix matrix = new Matrix();
//				canvas.drawBitmap(fixed, myleft, mytop, null);
//			}
//			else
//			{
//				if(myStatus)
//				{
//					if(focus == null)
//						focus = SearchID.getBitmap(MenuID + "sel",w,h);	
//					canvas.drawBitmap(focus, myleft, mytop, null);
//				}
//				else
//				{
//					if(unfocus == null)
//						unfocus = SearchID.getBitmap(MenuID + "unsel",w,h);	
//					canvas.drawBitmap(unfocus, myleft, mytop, null);
//				}
//			}	
//		}
//	}
	public Rect getRefreshRect()
	{
		return new Rect(myleft,mytop,myright,mybottom);
	}
	
	public void recycleBitmap()
	{
		if(focus != null)
		{
			focus.recycle();
			focus = null;
		}
		if(unfocus != null)
		{
			unfocus.recycle();
			unfocus = null;
		}
	}
	public void setItemID(String ID)
	{
		if(ID != null)
			MenuID = ID;
	}
	
}
