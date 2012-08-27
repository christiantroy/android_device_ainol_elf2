/**
 * 
 */
package com.amlogic.pmt;

import javax.microedition.khronos.opengles.GL10;

/**
 * @author Owner
 *
 */
public class GLPose {
	private float[] position;
	private float[] scale;
	private float[] rotate;
	
	GLPose(){
		//x, y, z
		position = new float[]{0,0,0};
		//x, y, z
		scale = new float[]{1,1,1};
		//angle, x, y, z
		rotate = new float[]{0,0,1,0};
	}
	GLPose(GLPose src){
		position = src.getPosition().clone();
		scale = src.getScale().clone();
		rotate = src.getRotate().clone();
	}
	public void setPosition(float[] pos){
		position = pos.clone();
	}
	public void setPosition(float x, float y, float z){
		position[0] = x;
		position[1] = y;
		position[2] = z;
	}
	public float[] getPosition(){
		return position;
	}
	public void setScale(float[] s){
		scale = s.clone();
	}
	public void setScale(float x, float y, float z){
		scale[0] = x;
		scale[1] = y;
		scale[2] = z;
	}
	public float[] getScale(){
		return scale;
	}
	public void setRotation(float[] r){
		rotate = r.clone();
	}
	public void setRotation(float a, float x, float y, float z){
		rotate[0] = a;
		rotate[1] = x;
		rotate[2] = y;
		rotate[3] = z;
	}
	public float[] getRotate(){
		return rotate;
	}
	
	public void setPaintBoardColor(float boardColor[]){
		
	}

	public void glPoseDraw(GL10 gl){
		float[] PSR ;
		PSR = getPosition();		
		if ((PSR[0]!=0)||(PSR[1]!=0)||(PSR[2]!=0))
			gl.glTranslatef(PSR[0],PSR[1],PSR[2]);
		PSR = getScale();
		if ((PSR[0]!=1)||(PSR[1]!=1)||(PSR[2]!=1))
			gl.glScalef(PSR[0],PSR[1],PSR[2]);
		PSR = getRotate();
		if (PSR[0]!=0)
			gl.glRotatef(PSR[0], PSR[1], PSR[2], PSR[3]);
		
	}
}
