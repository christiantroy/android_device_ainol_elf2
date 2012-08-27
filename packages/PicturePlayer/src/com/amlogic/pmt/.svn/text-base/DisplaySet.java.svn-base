/**
 * 
 */
package com.amlogic.pmt;

import java.io.File;
import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.Bitmap;
import android.util.Log;

/**
 * @author Owner
 *1. Set should have the animator;
 *2. Own feature flag, which indicates the file type or dir
 *3. Own two type object group, opaque group and transparent group
 *4. For Slot, we have focus and unfocus status;
 *5. For Dir, we support multi inner item draw;
 */
public class DisplaySet extends GLPose{
	private String fullName;
	private DisplaySlot owner;
	private boolean isVisible = true;	
	private boolean isDir = false;
	private boolean isFocused = false;
	private float[] paintBoard;

	private static ArrayList transEffectTexIdList;
	private static ArrayList transEffectModelList;
	public final static int TRANS_EFFECT0 = 1<<0;
	public final static int TRANS_EFFECT1 = 1<<1;
	public final static int TRANS_EFFECT2 = 1<<2;
	public final static int TRANS_EFFECT3 = 1<<3;
	public final static int TRANS_EFFECT4 = 1<<4;
	public final static int TRANS_EFFECT5 = 1<<5;
	//Opaque from DisplayItem;
	private ArrayList<DisplayItem> items = new ArrayList<DisplayItem>();
	
	public DisplaySet(String name){
		super();
		init();
		
		if(name != null)
			setBitmapFile(name);
		else
			fullName = "";
	}
	
	public DisplaySet(DisplaySet src){
		super(src);
	
		fullName = src.fullName;
		isVisible = src.isVisible;	
		isDir = src.isDir;
		isFocused = src.isDir;
		paintBoard = src.paintBoard.clone();
		
		for(int i=0; i<src.items.size(); i++){
			DisplayItem itm;
			items.add(new DisplayItem(src.items.get(i)));
		}
	}
	
	public void SetBitmap(Bitmap bmp, boolean share){
		fullName = "";
		if(items.size() > 0){
			items.get(0).SetBitmap(bmp, share);
		}else{
//			clearItems();
			DisplayItem item = new DisplayItem((Bitmap)null);
			item.SetBitmap(bmp, share);
			items.add(item);
		}
	}
	
	public void setBitmapFile(String name){
		clearItems();
		fullName = new String(name);
		if(fullName != ""){
			File file = new File(fullName);
			if(file.exists()){
				if(isDir = file.isDirectory()){
					items.add(new DisplayItem(fullName, 0));
				}
				else{
					items.add(new DisplayItem(fullName, 0));
				}
			}
		}
	}
	
	void init(){
		paintBoard = new float[]{
				-0.1f,  0.1f, 0.0f,
				-0.1f, -0.1f, 0.0f,
				 0.1f,  0.1f, 0.0f,
				 0.1f, -0.1f, 0.0f};
	}
	
	public void setOwner(DisplaySlot ds){
		owner = ds;
	}
	
	public synchronized void drawSet(GL10 gl){
		if(isVisible && getSize() > 0){ 
			gl.glPushMatrix();

			glPoseDraw(gl);
			for(DisplayItem item : items){
				item.drawItem(gl);
			}
	
			gl.glPopMatrix();
		}
	}

	public void drawEffects(GL10 gl){
		if(getSize() > 0){ 
			gl.glPushMatrix();

			glPoseDraw(gl);

			gl.glPopMatrix();
		}
	}
	
	public int getSize(){
		return items.size();
	}
	public void addItem(DisplayItem item){
		item.setOwner(this);
		items.add(item);
	}
	
	public DisplayItem getItem(int i){
		if(i < items.size())
			return items.get(i);
		return null;
	}
	
	public void removeItem(int i){
		if(i<items.size()){
			DisplayItem item = items.remove(i);
//			item.recycleBitmap();
			item.delItemTextures();
		}
	}
	
	public void clearItems(){
		for(DisplayItem item : items){
//			item.recycleBitmap();
			item.delItemTextures();
		}
		items.clear();
	}
	public String getFullName(){
		return fullName;
	}
	public DisplaySlot getOwner(){
		return owner;
	}
	public void setVisible(boolean v){
		isVisible = v;
	}
	public boolean isVisible(){
		return isVisible;
	}
	public boolean isDir(){
		return isDir;
	}
	public void setFocused(boolean f){
		isFocused = f;
	}
	public boolean isFocused(){
		return isFocused;
	}	
	public void resizePaintBoard(float w, float h){
		for(int i=0; i<paintBoard.length; i+=3){
			paintBoard[0+i] = (paintBoard[0+i]>0)? w/2 : -w/2;
			paintBoard[1+i] = (paintBoard[0+i]>0)? h/2 : -h/2;
		}
	}
	public void setResolution(float w, float h, int collums, int rows){
		resizePaintBoard(w, h);
		for(DisplayItem item : items){
			item.setResolution(w, h, collums, rows);
		}
	}
	//Override
	public void setPaintBoardColor(float boardColor[]){
		for(DisplayItem item : items){
			item.setPaintBoardColor(boardColor);
		}
	}
	/*public static void loadTransEffectTexture(){
		transEffectTexIdList.add(1);
		transEffectModelList.add(new float[]{0,1,2});
	}*/
	
	public void delSetTextures(){
		//1. del textures for set;
		
		//2. del all items' texture;
		for(DisplayItem item : items){
//			item.recycleBitmap();
			item.delItemTextures();
		}
	}

	public void LoadTextures(GL10 gl) {
		for(DisplayItem item : items){
			item.LoadTexture(gl);
		}
	}
}
