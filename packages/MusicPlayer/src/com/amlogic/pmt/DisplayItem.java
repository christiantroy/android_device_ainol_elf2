/**
 * 
 */
package com.amlogic.pmt;

import java.io.File;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.HashMap;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

/**
 * @author Owner
 *
 */
public final class DisplayItem extends GLPose{
	private String pathName;
	private String fileName;
	private DisplaySet owner;
	
	private Bitmap bitmap = null;
	private boolean shareBitmap = true;
	private float nameScrollStep= 0f;
	private int nameWidthPixel = 1920;
	
	boolean isVisible = true;
	private boolean isThumbMode = true;
	private int thumbTexID = -1;
	private int imageTexID = -1;
	private float[] paintBoardVertex;
	private float[] paintBoardColor;
	private float[] paintBoardTexCoord;
	private short[] paintBoardIndex;
	private int paintBoardRow;
	private int paintBoardCollum;
	
	private int vertexVBO = -1;
	private int colorVBO = -1;
	private int texcoordVBO = -1;
	private int indexVBO = -1;
	
	private Buffer vertexBuff = null;
	private Buffer indexBuff = null;
	private Buffer colorBuff = null;
	private Buffer texcoordBuff = null;
	
	DisplayItem(String fullName, int defaultTexIndex){
		super();
		init();
		
		File file = new File(fullName);
		pathName = file.getParent();
		fileName = fullName.substring(pathName.length());
		thumbTexID = (defaultTexIndex <= 0)? MiscUtil.getFileDefaultTexture(fullName):defaultTexIndex;
	}
	
	DisplayItem(Bitmap bmp){
		super();
		init();
		
		bitmap = bmp;
	}
	
	DisplayItem(DisplayItem src){
		super(src);
		paintBoardVertex = src.paintBoardVertex.clone();
		paintBoardColor = src.paintBoardColor.clone();
		paintBoardTexCoord = src.paintBoardTexCoord.clone();
		paintBoardIndex = src.paintBoardIndex.clone();
		paintBoardRow = src.paintBoardRow;
		paintBoardCollum = src.paintBoardCollum;

		isVisible = src.isVisible;
		isThumbMode = src.isThumbMode;
		if(src.bitmap == null)
			bitmap = null;
		else
			bitmap = src.bitmap.copy(src.bitmap.getConfig(), true);
		pathName = src.pathName;
		fileName = src.fileName;
		//owner;
		nameScrollStep= src.nameScrollStep;
		nameWidthPixel = src.nameWidthPixel;
	}

	public synchronized void SetBitmap(Bitmap bmp, boolean sharebmp){
		delItemTextures();
		if(bitmap!=null && !shareBitmap){
			bitmap.recycle();
		}
		bitmap = bmp;
		shareBitmap = sharebmp;
	}
//	public void SetBitmap(Bitmap bmp){
//		SetBitmap(bmp, true);
//	}
	
	public Bitmap GetBitmap(){
		return bitmap;
	}

	void init(){
		paintBoardVertex = new float[]{
				-0.1f,  0.1f, 0.0f,
				-0.1f, -0.1f, 0.0f,
				 0.1f,  0.1f, 0.0f,
				 0.1f, -0.1f, 0.0f,
		};
		paintBoardColor = new float[]{
				1, 1, 1, 1,
				1, 1, 1, 1,
				1, 1, 1, 1,
				1, 1, 1, 1
		};
		paintBoardTexCoord = new float[]{
			0,0,
			0,1,
			1,0,
			1,1,
		};
		paintBoardIndex = new short[]{
				0, 1, 2, 3};
		paintBoardRow = 1;
		paintBoardCollum = 1;
	}
	
//	float x=0;
	public synchronized void drawItem(GL10 gl){
		if(isVisible){
			gl.glPushMatrix();
	
	//		x+= 0.25;
	//		gl.glRotatef(x, 0, 1, 0);
			glPoseDraw(gl);
			
			if(vertexBuff==null){
				vertexBuff = MiscUtil.makeFloatBuffer(paintBoardVertex);
				Log.i("DisplayItem", "------------------makeFloatBuffer");
			}
			if(indexBuff==null){
				indexBuff = MiscUtil.makeShortBuffer(paintBoardIndex);
			}
			synchronized(paintBoardColor){
				if(colorBuff==null){
					colorBuff = MiscUtil.makeFloatBuffer(paintBoardColor);
				}
			}
			synchronized(paintBoardTexCoord){
				if(texcoordBuff == null || nameScrollStep != 0f){
					texcoordBuff = MiscUtil.makeFloatBuffer(paintBoardTexCoord);
					NameScroll();
				}
			}
	
			gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuff);
			gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
	
			gl.glColorPointer(4, GL10.GL_FLOAT, 0, colorBuff);
			gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
		
			if(isThumbMode()){
				if(thumbTexID<0){
					if(bitmap != null){
						thumbTexID = TextureManager.loadTexture(gl,bitmap,shareBitmap);
						if(!shareBitmap){
							bitmap.recycle();
							bitmap = null;
						}
					}
					else if(fileName != null && fileName != "")
						thumbTexID = TextureManager.loadThumbTextureFile(gl,pathName+fileName);
				}
				if(thumbTexID>=0){
					gl.glClientActiveTexture(GL10.GL_TEXTURE0);
			        gl.glActiveTexture( GL10.GL_TEXTURE0 );
			        gl.glEnable( GL10.GL_TEXTURE_2D );
					gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
					gl.glBindTexture( GL10.GL_TEXTURE_2D, thumbTexID);
					gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, texcoordBuff);
				}
			}
			else{
				if(imageTexID<0){
					if(bitmap != null){
						imageTexID = TextureManager.loadTexture(gl,bitmap,shareBitmap);
						if(!shareBitmap){
							bitmap.recycle();
							bitmap = null;
						}
					}
					else if(fileName != null && fileName != "")
						imageTexID = TextureManager.loadTextureFile(gl,pathName+fileName);
				}
				if(imageTexID>=0){
					gl.glClientActiveTexture(GL10.GL_TEXTURE0);
			        gl.glActiveTexture( GL10.GL_TEXTURE0 );
			        gl.glEnable( GL10.GL_TEXTURE_2D );
					gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
					gl.glBindTexture( GL10.GL_TEXTURE_2D, imageTexID);
					gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, texcoordBuff);
				}
			}
			if(thumbTexID>=0 || imageTexID>=0){
				gl.glDrawElements(GL10.GL_TRIANGLE_STRIP, paintBoardIndex.length, GL10.GL_UNSIGNED_SHORT, indexBuff);
			}
			
			gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
			gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
			gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
				
			gl.glPopMatrix();
		}
	}
	
	public void setOwner(DisplaySet ds){
		owner = ds;
	}

	public String getPathName(){
		return pathName;
	}
	public String getFileName(){
		return fileName;
	}
	public DisplaySet getOwner(){
		return owner;
	}
	public void setVisible(boolean v){
		isVisible = v;
	}
	public boolean isVisible(){
		return isVisible;
	}	
	public int getThumbTexID(){
		return thumbTexID;
	}
	public int getImageTexID(){
		return imageTexID;
	}
	public void setThumbMode(boolean thumbFlag){
		isThumbMode = thumbFlag;
	}
	public boolean isThumbMode(){
		return isThumbMode;
	}
	public void setResolution(float w, float h, int collums, int rows){
		setThumbMode(false);

		paintBoardRow = (rows<=20)?((rows/2)*2): 20;
		paintBoardCollum = (collums<=32)?((collums/2)*2):32;
		paintBoardVertex = new float[(paintBoardRow+1)*(paintBoardCollum+1)*3];
		int i=0;
		Log.i("DisplayItem", "------------------rows" + rows + "collums"+collums);	
		for(int y=paintBoardRow/2; y>=-paintBoardRow/2;y--){
			for(int x=-paintBoardCollum/2; x<=paintBoardCollum/2; x++){
				paintBoardVertex[i+0] = w*x/paintBoardCollum;
				paintBoardVertex[i+1] = h*y/paintBoardRow;
				paintBoardVertex[i+2] = 0;
				i+=3;
			}
		}
		
		paintBoardColor = new float[(paintBoardRow+1)*(paintBoardCollum+1)*4];
		for(i=0; i<paintBoardColor.length; i++){
			paintBoardColor[i] = 1.0f;
		}
		/*
		for(i=0; i<paintBoardColor.length; i+=4){
			paintBoardColor[i+0] = 1;//1.0f*i/paintBoardColor.length;
			paintBoardColor[i+1] = 1;//1.0f*i/paintBoardColor.length;
			paintBoardColor[i+2] = 1;//1.0f*i/paintBoardColor.length;
			paintBoardColor[i+3] = 1f;//1;//1.0f*i/paintBoardColor.length;
		}*/
		
		paintBoardTexCoord = new float[((paintBoardRow+1)*(paintBoardCollum+1))*2];
		i=0;
		for(int y=0; y<paintBoardRow+1; y++){
			for(int x=0; x<paintBoardCollum+1; x++){
				paintBoardTexCoord[i++] =1.0f*x/paintBoardCollum;
				paintBoardTexCoord[i++] =1.0f*y/paintBoardRow;
			}
		}
		
		paintBoardIndex = new short[(2+paintBoardCollum*2)*paintBoardRow+3*(paintBoardRow-1)];
		i = 0;
		for(int y=0; y<paintBoardRow; y++){
			if(y>0){
				short t = paintBoardIndex[i-1];
				paintBoardIndex[i++] = t;
				paintBoardIndex[i++] = t;
				paintBoardIndex[i++]=(short)((y)*(paintBoardCollum+1));
			}
			for(int x=0; x<paintBoardCollum;x++){
				if(x==0){
					paintBoardIndex[i++]=(short)((y)*(paintBoardCollum+1)+(x));
					paintBoardIndex[i++]=(short)((y+1)*(paintBoardCollum+1)+(x));
				}
				paintBoardIndex[i++]=(short)((y)*(paintBoardCollum+1)+(x+1));
				paintBoardIndex[i++]=(short)((y+1)*(paintBoardCollum+1)+(x+1));
			}
		}	
	}
	//Override
	public void setPaintBoardColor(float boardColor[]){
		synchronized(paintBoardColor){
			for(int i=0; i<paintBoardColor.length; i++){
				if(boardColor != null && i < boardColor.length)
					paintBoardColor[i] = boardColor[i];
				else
					paintBoardColor[i] = 1.0f;
			}
			colorBuff = MiscUtil.makeFloatBuffer(paintBoardColor);
		}
	}
	public void delItemTextures(){
//		Log.i("DisplayItem", "delItemTextures() " + thumbTexID + "   "+imageTexID);		
		if(thumbTexID>0) TextureManager.delTexture(thumbTexID);
		thumbTexID = -1;
		if(imageTexID>0) TextureManager.delTexture(imageTexID);
		imageTexID = -1;
	}
//	public void recycleBitmap(){
//		if(bitmap != null){
//			bitmap.recycle();
//			bitmap = null;
//		}
//	}
	
	long timeNameScroll;
	public void setNameScroll(boolean scroll){
		synchronized (paintBoardTexCoord) {
			if(scroll && bitmap != null && bitmap.getWidth() > nameWidthPixel){
				float bmpWidth = bitmap.getWidth();
				float w = (float)nameWidthPixel / bmpWidth;
				nameScrollStep = 0.02f / bmpWidth;   //step per ms
				paintBoardTexCoord = new float[]{
						0,0,
						0,1,
						w,0,
						w,1,
					};
				timeNameScroll = System.currentTimeMillis();
			}else if(nameScrollStep !=0){
				paintBoardTexCoord = new float[]{
						0,0,
						0,1,
						1,0,
						1,1,
					};
				nameScrollStep = 0f;
				texcoordBuff = null;
			}
		}
	}
	
	public void SetNameWidthPixel(int wp) {
		nameWidthPixel = wp;
	}
	
	private void NameScroll() {
		synchronized (paintBoardTexCoord) {
			if (nameScrollStep != 0f) {
				float width = paintBoardTexCoord[4] - paintBoardTexCoord[0];
				long curtime = System.currentTimeMillis();
				float mov  = (curtime-timeNameScroll) * nameScrollStep;
				timeNameScroll = curtime;
				for (int i = 0; i < paintBoardTexCoord.length; i+=2) {
					if ((paintBoardTexCoord[i] += mov) >= 1.0f) {
						paintBoardTexCoord = new float[] {
								0,0,
								0,1,
								width,0,
								width,1,
							};
						break;
					}
				}
			}
		}
	}

	public void LoadTexture(GL10 gl) {
		if(isThumbMode()){
			if(thumbTexID<0){
				if(bitmap != null){
					thumbTexID = TextureManager.loadTexture(gl,bitmap,shareBitmap);
					if(!shareBitmap){
						bitmap.recycle();
						bitmap = null;
					}
				}
				else
					thumbTexID = TextureManager.loadThumbTextureFile(gl,pathName+fileName);
			}
		}
		else{
			if(imageTexID<0){
				if(bitmap != null){
					imageTexID = TextureManager.loadTexture(gl,bitmap,shareBitmap);
					if(!shareBitmap){
						bitmap.recycle();
						bitmap = null;
					}
				}
				else
					imageTexID = TextureManager.loadTextureFile(gl,pathName+fileName);
			}
		}
	}
}
