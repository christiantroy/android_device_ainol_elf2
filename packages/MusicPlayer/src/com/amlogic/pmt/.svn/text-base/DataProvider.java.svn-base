package com.amlogic.pmt;

import java.util.List;
import java.util.Random;

public class DataProvider {
//	private String root;
	private List<String> myfilelist = null;
	private String myfirstname = null;
	private int FocusID = 0;
	public final static int playmode_normal = 1<<1;
	public final static int playmode_folder = 1<<2;
	public final static int playmode_rand = 1<<3;
	public final static int playmode_singer= 1<<4;
	
	
	private int mymode ;

	public DataProvider(String name)
	{
		mymode = playmode_folder;
	}
	public void setFilelist(List<String> list)
	{
		myfilelist = list;
	}
	
	public void setfirstname(String name)
	{
		myfirstname = name;
	}
	

	public String getFirstFile()
	{
		    findFirstFileID();
			return myfirstname;

	}
	/*
	public String getSecondFile()
	{
		String name = null;
		if(mymode == playmode_folder)
		{
			if(FocusID >= (myfilelist.size() -1))
				name = myfilelist.get(0);
			else
				name = myfilelist.get(FocusID + 1);
		}
		return name;
		
	}*/

	public String getPreFile()
	{
		String name = null;
		if(mymode == playmode_folder)
		{
			if(FocusID == 0)
				FocusID = myfilelist.size() -1 ;
			else
				FocusID -= 1 ;	
			name = myfilelist.get(FocusID);

		}
		else
		if(mymode == playmode_rand)
		{
			Random rand = new Random();
		    FocusID = rand.nextInt(myfilelist.size());
			name = myfilelist.get(FocusID);
		}
		else
		if(mymode == playmode_normal)
		{
			if(FocusID == 0)
				FocusID = 0 ;
			else
				FocusID -= 1 ;	
			name = myfilelist.get(FocusID);
		}
		else
		if(mymode == playmode_singer)
		{	
			name = myfilelist.get(FocusID);
		}
		
		return name;
	}
	
	public String getNextFile()
	{
		String name = null;
		if(mymode == playmode_folder)
		{
			if(FocusID >= (myfilelist.size() -1))
				FocusID = 0;
			else
				FocusID += 1;
			name = myfilelist.get(FocusID);
		}
		else
		if(mymode == playmode_rand)
		{
			Random rand = new Random();
		    FocusID = rand.nextInt(myfilelist.size());
			name = myfilelist.get(FocusID);
		}
		else
		if(mymode == playmode_normal)
		{
			if(FocusID >= (myfilelist.size() -1))
				return null;
			else
				FocusID += 1;	
			name = myfilelist.get(FocusID);
		}
		else
		if(mymode == playmode_singer)
		{	
			name = myfilelist.get(FocusID);
		}
		return name;
		
	}
	
	
	

	public void setSwitchMode(int mode)
	{
		if(mode == playmode_normal || mode == playmode_folder 
				|| mode == playmode_rand || mode == playmode_singer)
			mymode = mode;
		else
			mymode = playmode_normal;
			
	}
	
	
	public String getCurFilePath()
	{
		return myfilelist.get(FocusID);
	}
	
	
	private int findFirstFileID()
	{
		int firstiD = 0;
		if( myfilelist != null &&  myfirstname != null)
			for(int i =0 ; i < myfilelist.size() ; i++)
				if(myfirstname.equals(myfilelist.get(i)))
				{
					firstiD = i ;
					break;
				}
		FocusID = firstiD;
		return firstiD ;
	}
	
	public String getCurPosScale()
	{
		int pos = FocusID+1;
		if(myfilelist != null)
			return "" + pos +"/"+myfilelist.size();
		else
			return null;
	}
	
}
