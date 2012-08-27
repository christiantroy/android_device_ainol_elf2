package com.amlogic.pmt;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.microedition.khronos.opengles.GL10;

import com.amlogic.pmt.ContentProvider.PMTContentProvider;

import android.content.Context;
import android.opengl.GLU;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

//1. Own a list of DisplaySlot;
//2. Event Process
//3. Animator 
//4. DataSource
//5. GLPose
//6. 
public abstract class GLBaseLayout extends GLPose{
	protected String name;
	protected Context mContext;
	protected ArrayList<DisplaySlot> slots;
	protected DataProvider dataProvider;
	final static float N = 0.00001f;	//near
	final static float S = 0.00001f;	//small
	final static float F = 2.0f;		//far
	final static float GX = 0.915f;		//general X scale
	final static float GY = 0.829f;		//general Y scale
	final static int pause = 1<<8;	
	final static int continueplayer = 1<<7;	
	private float[] cameraEye = new float[]{0,0,2};
	private float[] cameraCenter = new float[]{0,0,0};
	private float[] cameraUp = new float[]{0,1,0};
	protected  updataFilenameListener filenamelistener = null;
    public PMTContentProvider myConProvider = null;
	//
	GLBaseLayout(Context context, String n, String location){
		super();
		mContext = context;
		name = new String(n);
		dataProvider = new DataProvider(location);
		slots = new ArrayList<DisplaySlot>();
		myConProvider = new PMTContentProvider(context);
	}
	public abstract void setSlotsLayout();
    public abstract boolean onKeyDown(int keyCode, KeyEvent event);
    public abstract boolean onKeyUp(int keyCode, KeyEvent event);
    public abstract void drawFrame(GL10 gl);
    public abstract void startAutoPlay();
    public abstract void stopAutoPlay();
//    public abstract void setPlayerState(int state);
    public abstract void onstop();
//    public abstract void delLayoutTextures();
    //gyx add
//    public abstract boolean onTouch(MotionEvent event);
    
	protected void drawSlot(GL10 gl){
		synchronized(slots){
			if(slots!=null && slots.size() > 0){ 
				gl.glPushMatrix();
		
				glPoseDraw(gl);
				for(DisplaySlot slot : slots){
					slot.drawSlot(gl);
				}
		
				gl.glPopMatrix();
			}
		}
	}

	protected void drawEffects(GL10 gl){
		synchronized(slots){
			if(slots!=null && slots.size() > 0){ 
				gl.glPushMatrix();
				//gl.glEnable(GL10.GL_BLEND);
				
				glPoseDraw(gl);
				for(DisplaySlot slot : slots){
					slot.drawEffects(gl);
				}
		
				//gl.glDisable(GL10.GL_BLEND);
				gl.glPopMatrix();
			}
		}
	}
	
	public int getSize(){
		return slots.size();
	}
	public void addItem(DisplaySlot slot){
		synchronized(slots){
			slots.add(slot);
		}
	}
	
	public DisplaySlot getItem(int i){
		DisplaySlot slt = null;
		synchronized(slots){
			if(i < slots.size())
				slt = slots.get(i);
		}
		return slt;
	}
	
	public void removeItem(int i){
		synchronized(slots){
			if(i<slots.size()){
				DisplaySlot slot = slots.remove(i);
				slot.delSlotTextures();
			}
		}
	}
	
	public void clearItems(){
		synchronized(slots){
			for(DisplaySlot slot: slots){
				slot.delSlotTextures();
			}
			slots.clear();
		}
	}
	
	public void setCamera(GL10 gl
			){
		GLU.gluLookAt(gl, cameraEye[0], cameraEye[1], cameraEye[2], 
				cameraCenter[0], cameraCenter[1], cameraCenter[2], 
				cameraUp[0], cameraUp[1], cameraUp[2]);
	}
	
	public void delLayoutTextures() {
		// TODO Auto-generated method stub
		synchronized(slots){
			//Only remove textures from GPU memory...
			for(DisplaySlot slot: slots){
				slot.delSlotTextures();
			}
		}
	}
	/*
	protected void switchOrder(int idx){
		synchronized(slots){
			if(idx >= slots.size())
				return;
			DisplaySlot slt = slots.get(idx);
			slots.remove(idx);
			slots.add(slt);
		}
	}*/
	public void setFileList(List<String> filename)
	{	
		dataProvider.setFilelist(filename);
	}
	public void setFirstFileName(String name)
	{	
		dataProvider.setfirstname(name);
	}
	
	
	
	public void showFirstSlot()
	{
		String fName  = dataProvider.getFirstFile();
		
		DisplaySlot slot0 = new DisplaySlot(" ", "");
		DisplaySlot slot1 = new DisplaySlot(" ", fName);
		slot0.setResolution(3.2f, 2, 32, 20);
		slot1.setResolution(3.2f, 2, 32, 20);
		slot0.setScale(S,S,S);
		slot1.setScale(GX,GY,1);
		addItem(slot0);
		addItem(slot1);
		
		filenamelistener.CallbackName("Picture",fName);
	}
	
	public String  switchToNextFile()
	{
		String fName = dataProvider.getNextFile();
		if(fName == null)
			return null;
		DisplaySlot slot = new DisplaySlot("__NULL__", fName);
		slot.setResolution(3.2f, 2, 32, 20);
		slot.setScale(S,S,S);
		
		synchronized(slots){
			addItem(slot);
			removeItem(0);
		}
		return fName;
		
	}
	
	public  String  switchToPrevFile()
	{
		String fName = dataProvider.getPreFile();
		DisplaySlot slot = new DisplaySlot("UP", fName);
		slot.setResolution(3.2f, 2, 32, 20);
		slot.setScale(S,S,S);

		synchronized(slots){
			addItem(slot);
			removeItem(0);
		}
		return fName;
		
	}
	
	public void setFilenameListener(updataFilenameListener listener)
	{
		  filenamelistener = listener;
	}
	
	
	
}
