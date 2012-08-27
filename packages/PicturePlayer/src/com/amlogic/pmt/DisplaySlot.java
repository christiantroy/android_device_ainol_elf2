/**
 * 
 */
package com.amlogic.pmt;

import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.Bitmap;

/**
 * @author Owner
 * DisplaySlot is the basic unit when browsering files.
 * Internal
 * 1. Own one DisplaySet, which containing several DisplayItems
 * 2. Own Position, Scale, Rotation
 *
 * External
 * 1. DisplaySlot support animation
 * 2. All kinds of Effect
 * 3. Support multi-layout
 */
public class DisplaySlot extends GLPose{
	private String name;
	protected DisplaySet displaySet;
	private boolean isVisible = true;	
	private boolean isFocused = false;
	
	public DisplaySlot(String n, String location) {
		// TODO Auto-generated constructor stub
		super();
		name = new String(n);
		displaySet = new DisplaySet(location);
	}
	
	protected DisplaySlot() {
		super();
	}
	
	protected DisplaySlot(DisplaySlot src) {
		super(src);
		name = src.name;
		//displaySet;
		isVisible = src.isVisible;	
		isFocused = src.isFocused;
	}
	
	public void SetBitmap(Bitmap bmp) {
		displaySet.SetBitmap(bmp, true);
	}
	
	public void SetBitmap(Bitmap bmp, boolean share) {
		displaySet.SetBitmap(bmp, share);
	}	
	public void SetBitmapFile(String location){
		displaySet.setBitmapFile(location);
	}
	
	//Called from outside, should be layout
	public synchronized void drawSlot(GL10 gl){
		if(isVisible() && displaySet!=null && displaySet.getSize() > 0){ 
			gl.glPushMatrix();
	
			glPoseDraw(gl);
			displaySet.drawSet(gl);
	
			gl.glPopMatrix();
		}
	}
	//Called by outside, should be layout
	public void drawEffects(GL10 gl){
		if(isVisible() && displaySet!=null && displaySet.getSize() > 0){ 
			gl.glPushMatrix();
			
			glPoseDraw(gl);
			displaySet.drawEffects(gl);
	
			gl.glPopMatrix();
		}
	}
	
	public void setDisplaySet(DisplaySet ds){
		displaySet = ds;
		displaySet.setOwner(this);
	}
	public DisplaySet getDisplaySet(){
		return displaySet;
	}
	public void setVisible(boolean v){
		isVisible = v;
	}
	public boolean isVisible(){
		return isVisible;
	}
	public void setFocused(boolean f){
		isFocused = f;
	}
	public boolean isFocused(){
		return isFocused;
	}
	
	public void setResolution(float w, float h, int collums, int rows){
		displaySet.setResolution(w,h,collums,rows);
	}
	//Override
	public void setPaintBoardColor(float boardColor[]){
		displaySet.setPaintBoardColor(boardColor);
	}
	
	public void delSlotTextures(){
		displaySet.delSetTextures();
	}

	public void LoadTextures(GL10 gl) {
		displaySet.LoadTextures(gl);
	}
	
	public void setName(String mname)
	{
		name = mname;
	}
	
	public String getName()
	{
		return name;
	}
}
