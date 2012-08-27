package com.amlogic.View;
import java.util.ArrayList;
import java.util.List;

import com.amlogic.Listener.AmlogicMenuListener;
import com.amlogic.AmlogicMenu.SearchDrawable;
import com.amlogic.XmlParse.StringItem;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;


public class Balancer extends View
{
	private SearchDrawable SearchID ;
	private Bitmap bgBtp,unfocus,focus,flag,nameBG;
	private AmlogicMenuListener mylistener;
	private List<Line> mylineOne = new ArrayList<Line>();
	private List<Line> mylineTwo = new ArrayList<Line>();
	private List<Line> mylineThree = new ArrayList<Line>();
	private List<Line> mylineFour = new ArrayList<Line>();
	private List<Line> mylineFive = new ArrayList<Line>();
	private List<BalancerName> myBalancerSet = new ArrayList<BalancerName>();
	private int[] a =new int[7];
	private int focusid=0;
	private int soundmode=0;
	public Balancer(Context context, AttributeSet attrs)
	{
		super(context, attrs);
//		this.setFocusable(true); 
		SearchID = new SearchDrawable(this.getContext());
		
		addLine(mylineOne);
		addLine(mylineTwo);
		addLine(mylineThree);
		addLine(mylineFour);
		addLine(mylineFive);
		
		bgBtp   = SearchID.getBitmap("setup_eq_bg");
		flag    = SearchID.getBitmap("setup_eq_bar");
		focus   = Bitmap.createBitmap(flag, 0, 0, 6, 19);
		unfocus = Bitmap.createBitmap(flag, 6, 0, 6, 19);
		nameBG  = SearchID.getBitmap("setup_eq_sel"); 
		
		this.setFocusable(true);
	}

	public void addLine(List<Line> myline){
		myline.add(new Line(184,93+3,0,10,false));
		myline.add(new Line(184,149+3,0,10,false));
		myline.add(new Line(184,205+3,0,10,false));
		myline.add(new Line(184,261+3,0,10,false));
		myline.add(new Line(184,311+3,0,10,false));
	}	
	
	public void initBalancerRescource(List<StringItem> stringItems,List<String> data){
								
		for(StringItem si:stringItems){
			if(si.name.equals("SM_STD"))
				myBalancerSet.add(new BalancerName(si.value,BalancerName.BalancerName_unfocus));
			else if(si.name.equals("SM_MUSIC"))
				myBalancerSet.add(new BalancerName(si.value,BalancerName.BalancerName_unfocus));
			else if(si.name.equals("SM_NEWS"))
				myBalancerSet.add(new BalancerName(si.value,BalancerName.BalancerName_unfocus));
			else if(si.name.equals("SM_THEATER"))
				myBalancerSet.add(new BalancerName(si.value,BalancerName.BalancerName_unfocus));
			else if(si.name.equals("SM_USER"))
				myBalancerSet.add(new BalancerName(si.value,BalancerName.BalancerName_unfocus));
		}
		
		if(data==null)
			return ;
		InitSetData(mylineOne,data,0);
		InitSetData(mylineTwo,data,5);
		InitSetData(mylineThree,data,10);
		InitSetData(mylineFour,data,15);
		InitSetData(mylineFive,data,20);
		
		String ct = data.get(25);

		if (ct.equals("STD"))
			focusid=0;
		else if (ct.equals("MUSIC"))
			focusid=1;
		else if (ct.equals("NEWS"))
			focusid=2;
		else if (ct.equals("THEATER"))
			focusid=3;
		else if (ct.equals("USER"))
			focusid=4;
		myBalancerSet.get(focusid).setfocusmark(BalancerName.BalancerName_focus);		
	}
	
	public void InitSetData(List<Line> myline,List<String> data,int i){
		myline.get(0).setData(Integer.parseInt(data.get(0+i)));
		myline.get(1).setData(Integer.parseInt(data.get(1+i)));
		myline.get(2).setData(Integer.parseInt(data.get(2+i)));
		myline.get(3).setData(Integer.parseInt(data.get(3+i)));
		myline.get(4).setData(Integer.parseInt(data.get(4+i)));
	}
	
	
	public void drawmyline(Canvas canvas,List<Line> myline){
		for(int ii = 0;ii< myline.size();ii++)
        {
        	Bitmap temp;
        	if(myline.get(ii).mydata != 0)
        	{
        		if(myline.get(ii).Getfocusmark())
        			temp= Bitmap.createScaledBitmap(focus, (int) ((536.0-184.0)*myline.get(ii).GetData()/40.0), 19, true); 
	        	else
	        		temp= Bitmap.createScaledBitmap(unfocus, (int) ((536.0-184.0)*myline.get(ii).GetData()/40.0), 19, true); 
        		canvas.drawBitmap(temp, myline.get(ii).myleft,myline.get(ii).mytop, null);
        		temp.recycle();
        	}
        }
	}
	
	//@Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(bgBtp, 0,0, null);
        for(int i = 0;i< myBalancerSet.size();i++)
        {
        	if(myBalancerSet.get(i).Getfocusmark() == BalancerName.BalancerName_focus || 
        			myBalancerSet.get(i).Getfocusmark() == BalancerName.BalancerName_press)
        	{
        		Paint paint = new Paint();
        		paint.setColor(Color.WHITE);
        		paint.setTextSize(24);
        		paint.setTextAlign(Paint.Align.CENTER);
        		if(myBalancerSet.get(i).Getfocusmark() == BalancerName.BalancerName_focus )
        		{
        			canvas.drawBitmap(nameBG,268,0, null);
        		}	
        		canvas.drawText(myBalancerSet.get(i).GetData(), 397, 52, paint);
        		a[5]=i;
        		
				if (i == 0)
					drawmyline(canvas, mylineOne);
				else if (i == 1)
					drawmyline(canvas, mylineTwo);
				else if (i == 2)
					drawmyline(canvas, mylineThree);
				else if (i == 3)
					drawmyline(canvas, mylineFour);
				else if (i == 4)
					drawmyline(canvas, mylineFive);
        		break;
        	}
    		
        }
    }
    
    public void invalidateMyLineUp(int i,List<Line> myline){
    	for(int ii = 0;ii< myline.size();ii++)
        {
			if(myline.get(ii).Getfocusmark())
			{ 
				if(ii != 0)
			    {
					myline.get(ii-1).setfocusmark(true);
					myline.get(ii).setfocusmark(false);
				    this.invalidate();
			    }
				else
				{
					myline.get(ii).setfocusmark(false);
					myBalancerSet.get(i).setfocusmark(BalancerName.BalancerName_focus);
					this.invalidate();
				}
				break;			
			}		  
        }
    }
    public void invalidateMyLineDown(List<Line> myline){
		for(int ii = 0;ii< myline.size();ii++)
	    {
			int iii ;
			for(iii = 0;iii< myline.size();iii++)
	        {
				if(myline.get(iii).Getfocusmark())
					break;
	        }
			if(iii == myline.size())
			{
				myline.get(0).setfocusmark(true);
				this.invalidate();
				break;
			}		
			if(myline.get(ii).Getfocusmark())
			{
				 if(ii != myline.size()-1)
			     {
					 myline.get(ii+1).setfocusmark(true);
					 myline.get(ii).setfocusmark(false);
				     this.invalidate();
			     }
				 break;
			}
	    }
    }
    public void invalidateMyLineLeft(List<Line> myline){
		for(int ii = 0;ii< myline.size();ii++)
	    {
			if(myline.get(ii).Getfocusmark())
			{
				 if(0 != myline.get(ii).GetData())
			     {
					 myline.get(ii).setData(myline.get(ii).GetData()-1);
					 
					 if(mylistener != null) 
						 mylistener.BalancerKeyListener(true,ii,(byte)myline.get(ii).GetData(),a);
					 this.invalidate();
			     }
				 break;
			}
	    }
    }
    public void invalidateMyLineRight(List<Line> myline){
		for(int ii = 0;ii< myline.size();ii++)
        {
			if(myline.get(ii).Getfocusmark())
			{
				 if(40 != myline.get(ii).GetData())
			     {
					 myline.get(ii).setData(myline.get(ii).GetData()+1);
					 
					 if(mylistener != null) 
						 mylistener.BalancerKeyListener(true,ii,(byte)myline.get(ii).GetData(),a);
					 this.invalidate();
			     }
				 break;
			}
        }
    }
    
    
    
    
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		switch (keyCode) {
		case KeyEvent.KEYCODE_DPAD_UP:
		
			 for(int i = 0;i< myBalancerSet.size();i++)
		        {
		        	if(myBalancerSet.get(i).Getfocusmark()== BalancerName.BalancerName_focus || 
		        			myBalancerSet.get(i).Getfocusmark()== BalancerName.BalancerName_press)
		        	{	
						if (i == 0)
							invalidateMyLineUp(i, mylineOne);
						else if (i == 1)
							invalidateMyLineUp(i, mylineTwo);
						else if (i == 2)
							invalidateMyLineUp(i, mylineThree);
						else if (i == 3)
							invalidateMyLineUp(i, mylineFour);
						else if (i == 4)
							invalidateMyLineUp(i, mylineFive);
						break;
		        	}
		        }
		
		break;
		case KeyEvent.KEYCODE_DPAD_DOWN:
			 for(int i = 0;i< myBalancerSet.size();i++)
		        {
				 if(myBalancerSet.get(i).Getfocusmark() == BalancerName.BalancerName_focus)
				 {
					 myBalancerSet.get(i).setfocusmark(BalancerName.BalancerName_press);
				 }
		        	if(myBalancerSet.get(i).Getfocusmark()== BalancerName.BalancerName_focus || 
		        			myBalancerSet.get(i).Getfocusmark()== BalancerName.BalancerName_press)
		        	{	
						if (i == 0)
							invalidateMyLineDown(mylineOne);
						else if (i == 1)
							invalidateMyLineDown(mylineTwo);
						else if (i == 2)
							invalidateMyLineDown(mylineThree);
						else if (i == 3)
							invalidateMyLineDown(mylineFour);
						else if (i == 4)
							invalidateMyLineDown(mylineFive);
						break;
			        			
		        	}
		        }
			
		break;
		case KeyEvent.KEYCODE_DPAD_LEFT:
			for(int j = 0;j< myBalancerSet.size();j++)
	        {
				if(myBalancerSet.get(j).Getfocusmark() == BalancerName.BalancerName_focus)
				 {
					 if(j == 0)
					 {
						 myBalancerSet.get(j).setfocusmark(BalancerName.BalancerName_unfocus);
						 myBalancerSet.get(myBalancerSet.size()-1).setfocusmark(BalancerName.BalancerName_focus);
						 soundmode=myBalancerSet.size()-1;
					 }
					 else
					 {
						 myBalancerSet.get(j).setfocusmark(BalancerName.BalancerName_unfocus);
						 myBalancerSet.get(j-1).setfocusmark(BalancerName.BalancerName_focus);
						 soundmode=j-1;
					 }
					 this.invalidate();
					 
					 if(mylistener != null) 
						 mylistener.BalancerKeyListener(soundmode);
					 return super.onKeyDown(keyCode, event);
				 }
	        }
			for(int i = 0;i< myBalancerSet.size();i++)
	        {			
				if(	myBalancerSet.get(i).Getfocusmark()== BalancerName.BalancerName_press)
	        	{
					Log.d("Balancer", "........ KEYCODE_DPAD_LEFT BalancerName_press .........\n");
					if (i == 0){
						myBalancerSet.get(i).setfocusmark(BalancerName.BalancerName_unfocus);
						myBalancerSet.get(4).setfocusmark(BalancerName.BalancerName_press);
						changeUserValue(mylineOne);
						invalidateMyLineLeft(mylineFive);
					}
					else if (i == 1){
						myBalancerSet.get(i).setfocusmark(BalancerName.BalancerName_unfocus);
						myBalancerSet.get(4).setfocusmark(BalancerName.BalancerName_press);
						changeUserValue(mylineTwo);
						invalidateMyLineLeft(mylineFive);
					}
					else if (i == 2){
						myBalancerSet.get(i).setfocusmark(BalancerName.BalancerName_unfocus);
						myBalancerSet.get(4).setfocusmark(BalancerName.BalancerName_press);
						changeUserValue(mylineThree);
						invalidateMyLineLeft(mylineFive);
					}
					else if (i == 3){
						myBalancerSet.get(i).setfocusmark(BalancerName.BalancerName_unfocus);
						myBalancerSet.get(4).setfocusmark(BalancerName.BalancerName_press);
						changeUserValue(mylineFour);
						invalidateMyLineLeft(mylineFive);
					}
					else if (i == 4){
						invalidateMyLineLeft(mylineFive);
					}
					break;
	        	}		
				
		        	
	        }
			
		break;
		case KeyEvent.KEYCODE_DPAD_RIGHT:
			for(int j = 0;j< myBalancerSet.size();j++)
	        {
				if(myBalancerSet.get(j).Getfocusmark() == BalancerName.BalancerName_focus)
				 {
					 if(j == myBalancerSet.size()-1)
					 {
						 myBalancerSet.get(j).setfocusmark(BalancerName.BalancerName_unfocus);
						 myBalancerSet.get(0).setfocusmark(BalancerName.BalancerName_focus);
						 soundmode=0;
					 }
					 else
					 {
						 myBalancerSet.get(j).setfocusmark(BalancerName.BalancerName_unfocus);
						 myBalancerSet.get(j+1).setfocusmark(BalancerName.BalancerName_focus);
						 soundmode=j+1;
					 }
					 this.invalidate();
					 
					 if(mylistener != null) 
						 mylistener.BalancerKeyListener(soundmode);
					 return super.onKeyDown(keyCode, event);				 
				}
	        }
			for(int i = 0;i< myBalancerSet.size();i++)
	        {
				
				if(	myBalancerSet.get(i).Getfocusmark()== BalancerName.BalancerName_press)
	        	{	
					Log.d("Balancer", "........ KEYCODE_DPAD_RIGHT BalancerName_press .........\n");
					if (i == 0){
						myBalancerSet.get(i).setfocusmark(BalancerName.BalancerName_unfocus);
						myBalancerSet.get(4).setfocusmark(BalancerName.BalancerName_press);
						changeUserValue(mylineOne);			
						invalidateMyLineRight(mylineFive);								
					}
					else if (i == 1){
						myBalancerSet.get(i).setfocusmark(BalancerName.BalancerName_unfocus);
						myBalancerSet.get(4).setfocusmark(BalancerName.BalancerName_press);
						changeUserValue(mylineTwo);		
						invalidateMyLineRight(mylineFive);
					}
					else if (i == 2){
						myBalancerSet.get(i).setfocusmark(BalancerName.BalancerName_unfocus);
						myBalancerSet.get(4).setfocusmark(BalancerName.BalancerName_press);
						changeUserValue(mylineThree);	
						invalidateMyLineRight(mylineFive);
					}
					else if (i == 3){
						myBalancerSet.get(i).setfocusmark(BalancerName.BalancerName_unfocus);
						myBalancerSet.get(4).setfocusmark(BalancerName.BalancerName_press);
						changeUserValue(mylineFour);	
						invalidateMyLineRight(mylineFive);
					}
					else if (i == 4){
						invalidateMyLineRight(mylineFive);
					}
					break;
	        		
	        	}
				
	        }
						
		
		break;
		case KeyEvent.KEYCODE_BACK:	
			onKeyDown(KeyEvent.KEYCODE_ENTER,event);
		return true;
		case KeyEvent.KEYCODE_DPAD_CENTER:
		case KeyEvent.KEYCODE_ENTER:
			if (mylistener != null) {				
				for(int i=0;i<5;i++){
					a[i]=mylineFive.get(i).GetData();
				}
//				if(a[5]==focusid)
//					a[6]=0;
//				else
//					a[6]=1;
				mylistener.BalancerKeyListener(false,0,(byte)0,a);
			}
		break;
			 							
		}
		
		
		return super.onKeyDown(keyCode, event);
//		return true;	
	}
	
	private void changeUserValue(List<Line> myline) {
		
		for(int c=0;c<5;c++){
			mylineFive.get(c).setData(myline.get(c).GetData());
			mylineFive.get(c).setfocusmark((myline.get(c).Getfocusmark()));
		}		
		clearLineFocus(myline);	
	}	
	
    private void clearLineFocus(List<Line> myline) {
		
		for(Line templine:myline){
			if(templine.Getfocusmark())
				templine.setfocusmark(false);
		}
	}

	public void setBalancerKeyListener( AmlogicMenuListener listener)
    {
    	mylistener = listener;
    }
    
    class Line
    {
    	private boolean focusMark = false;
    	private int num = 0;
        private int mytop = 0;
        private int myleft = 0;
        private int mydata = 0;
        Line(int left,int top,int number,int data,boolean focusmark)
        {
        	mytop = top;
        	myleft = left;
        	num  = number;
        	focusMark = focusmark;
        	mydata = data;
        }
        
    	void setfocusmark(final boolean data)
    	{
    		focusMark = data;
    	}
    	void setNum(final int data)
    	{
    		num = data;
    	}
       
    	void setData(final int data)
    	{
    		mydata = data;
    	}
        boolean Getfocusmark()
    	{
    		return focusMark;
    	}
        int GetNum()
    	{
    		return num ;
    	}
        int GetData()
    	{
    		return mydata ;
    	}
    	
    }
    
    class BalancerName
    {
    	private int focusMark = BalancerName_focus;
    	private String mydata = "";
    	final static int BalancerName_focus = 1<<2;
		final static int BalancerName_unfocus = 1<<3;
	    final static int BalancerName_press = 1<<4;
        BalancerName(String data,int focusmark)
        {
        	mydata  = data;
        	focusMark = focusmark;
        }
    	void setfocusmark(final int data)
    	{
    		focusMark = data;
    	}
    	void setData(final String data)
    	{
    		mydata = data;
    	}

        int Getfocusmark()
    	{
    		return focusMark;
    	}
        String GetData()
    	{
    		return mydata ;
    	}

    }
    
    
}
	

