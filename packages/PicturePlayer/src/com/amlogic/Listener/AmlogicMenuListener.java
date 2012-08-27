package com.amlogic.Listener;

public interface AmlogicMenuListener {
	public void CheckedMenuHandle(int Key, int FocusID,String MenuItemName);
	public void SelectFrameToMenuHandle(String SelectID);
	public void ProgressBarToMenuHandle(int progress,boolean isChanged,String MenuItemName);
	public void CallMenucontrolunbindservice();
	public void BalancerKeyListener(boolean doit,int high, byte low, int[] a);
	public void BalancerKeyListener(int soundmode);
	public void DialogManage(boolean doit);
	public void BackMenuHandle(String showState);
	public String GetSelectFrameState(String ItemName);
}
