package com.amlogic.AmlogicMenu;


public class AmlogicMenuItem  {
//	enum FOCUSSTATE
//	{
//		FOUCS,
//		UNFOUCS
//	};
//	private int MyUnfocusName;
//	private int MyFocusName;
	private String  ItemName ;
//	public AmlogicMenuItem(String name,int focusName,int unfocusName) {
//		
//		// TODO Auto-generated constructor stub
//		MyUnfocusName = unfocusName;
//		MyFocusName = focusName;
//		ItemName = name;
//	}
	
	private String UnfocusName;
	private String FocusName;
	public AmlogicMenuItem(String name,String focusName,String unfocusName) {
		
		// TODO Auto-generated constructor stub
		UnfocusName = unfocusName;
		FocusName = focusName;
		ItemName = name;
	}
	
	public String GetFocusName()
	{
		return FocusName;
	}
	public String GetUnFocusName()
	{
		return UnfocusName;
	}

	public String GetItemName()
	{
		return ItemName;
	}
	
	public void SetItemName(String name)
	{
		ItemName = name;
	}
	
	public void SetFocusName(String focusname)
	{
		FocusName = focusname;
	}
	public void SetunFocusName(String unfocusname)
	{
		UnfocusName = unfocusname;
	}

}
