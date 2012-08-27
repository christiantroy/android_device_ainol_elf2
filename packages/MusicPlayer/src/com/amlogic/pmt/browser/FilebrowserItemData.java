package com.amlogic.pmt.browser;


public class FilebrowserItemData {

	private int  UnFocusID;
	private int  FocusID;
	private String AbsoluteName; 
	
	public FilebrowserItemData(String name,int unfocusid,int focusid)
	{
		UnFocusID = unfocusid;
		FocusID = focusid;
		AbsoluteName = name;
	
	}
	
	public int GetUnfocusID()
	{
		return UnFocusID;
	}
	
	public int GetfocusID()
	{
		return FocusID;
	}
	public String getAbsoluteFilePath() {
		return AbsoluteName;
	}

	public String getFileName() {
		return ConvertFileName(AbsoluteName);
	}
	
	private String ConvertFileName(String filepath)
	{
		  int i  = filepath.lastIndexOf("/");
		  String filename = filepath.substring(i+1,filepath.length());
	      return filename;
		
	}
	

}


