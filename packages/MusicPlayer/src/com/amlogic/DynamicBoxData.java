package com.amlogic;

import java.util.ArrayList;

public class DynamicBoxData {
private ArrayList<String> subtitle = null;
private ArrayList<String> soundtrack = null;

	public DynamicBoxData()
	{
	}
	public void setsubtitle(ArrayList<String> data)
	{
		subtitle = data;
	}
	public void setsoundtrack(ArrayList<String> data)
	{
		soundtrack = data;
	}
	public ArrayList<String> getsubtitle()
	{
		return subtitle;
	}
	public ArrayList<String> getsoundtrack()
	{
		return soundtrack;
	}
}
