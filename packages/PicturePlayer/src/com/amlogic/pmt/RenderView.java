package com.amlogic.pmt;


import java.util.Timer;
import java.util.TimerTask;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

import android.content.Context;

import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.opengl.GLSurfaceView.Renderer;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.GestureDetector.SimpleOnGestureListener;

import org.geometerplus.zlibrary.ui.android.library.ZLAndroidActivity;
public class RenderView extends GLSurfaceView implements Renderer {
	public GLGridLayout currentGridLayout =  null ;
	public GLSlideShowLayout currentPicLayout =  null ;
	public GLMusicLayout currentMusicLayout =  null ;
	public GLEBookLayout currentTextLayout =  null ;
	GestureDetector gestureDetector=null;
	public RenderView(Context context, AttributeSet attrs) {

		super(context, attrs);
		//gestureDetector = new GestureDetector(new HahaGestureDetectorListener());
		// TODO Auto-generated constructor stub
		this.setFocusable(false);
		setEGLConfigChooser(new EGLConfigChooser() {
			//Override
			public EGLConfig chooseConfig(EGL10 egl, EGLDisplay display) {
				int[] attributes = new int[] { 
						EGL10.EGL_SAMPLES, 4,
						EGL10.EGL_RED_SIZE, 8, EGL10.EGL_GREEN_SIZE, 8,
						EGL10.EGL_BLUE_SIZE, 8, EGL10.EGL_ALPHA_SIZE, 8,
						EGL10.EGL_DEPTH_SIZE, 8, EGL10.EGL_STENCIL_SIZE, 8,
						EGL10.EGL_NONE };
				EGLConfig[] configs = new EGLConfig[1];
				int[] result = new int[1];
				if (egl
						.eglChooseConfig(display, attributes, configs, 1,
								result) == false
						|| result[0] <= 0) {
					// fallback for emulator
					attributes = new int[] { EGL10.EGL_NONE };
					egl
							.eglChooseConfig(display, attributes, configs, 1,
									result);
				}
				return configs[0];
			}
		});
		this.getHolder().setFormat(PixelFormat.RGBA_8888);
        setRenderer(this);
	}
	public RenderView(Context context) {

		super(context);
		//gestureDetector = new GestureDetector(new HahaGestureDetectorListener());
		// TODO Auto-generated constructor stub
		this.setFocusable(false);
		setEGLConfigChooser(new EGLConfigChooser() {
			//Override
			public EGLConfig chooseConfig(EGL10 egl, EGLDisplay display) {
				int[] attributes = new int[] { 
						EGL10.EGL_SAMPLES, 4,
						EGL10.EGL_RED_SIZE, 8, EGL10.EGL_GREEN_SIZE, 8,
						EGL10.EGL_BLUE_SIZE, 8, EGL10.EGL_ALPHA_SIZE, 8,
						EGL10.EGL_DEPTH_SIZE, 8, EGL10.EGL_STENCIL_SIZE, 8,
						EGL10.EGL_NONE };
				EGLConfig[] configs = new EGLConfig[1];
				int[] result = new int[1];
				if (egl
						.eglChooseConfig(display, attributes, configs, 1,
								result) == false
						|| result[0] <= 0) {
					// fallback for emulator
					attributes = new int[] { EGL10.EGL_NONE };
					egl
							.eglChooseConfig(display, attributes, configs, 1,
									result);
				}
				return configs[0];
			}
		});
		this.getHolder().setFormat(PixelFormat.RGBA_8888);
        setRenderer(this);
	}
//    //Override
//    public void onResume() {
//        super.onResume();
//    }
//
//    //Override
//    public void onPause() {
//        super.onPause();     
//        if(currentGridLayout != null)
//        	currentGridLayout.delLayoutTextures();
//        if(currentPicLayout != null)
//        	currentPicLayout.delLayoutTextures();
//        if(currentMusicLayout != null)
//        	currentMusicLayout.delLayoutTextures();
//        if(currentTextLayout != null)
//        	currentTextLayout.delLayoutTextures(); 
//        	
//    }
    
    public 	GLGridLayout GetGridLayoutInstance()
	{
    		return currentGridLayout;
	}
    public 	GLBaseLayout GetPictureLayoutInstance()
	{
		return currentPicLayout;
	}
    public 	GLMusicLayout GetMusicLayoutInstance()
	{
    	return currentMusicLayout;
	}
    public 	GLEBookLayout GetTxtLayoutInstance()
	{
    	return currentTextLayout;
	}
    
    public 	void  initGridLayout(boolean showLoading)
	{
    	Log.i("RenderView", "--> initGridLayout");
    	if(currentGridLayout == null)
    		currentGridLayout = new GLGridLayout(this.getContext(),"grid", showLoading);
   	
    	Log.i("RenderView", "<-- initGridLayout");
	}
    public 	void  uninitGridLayout()
	{
    	Log.i("RenderView", "--> uninitGridLayout");
    	if(currentGridLayout != null)
    	{
    		GLGridLayout ly = currentGridLayout;
	    	currentGridLayout = null;
    		ly.delLayoutTextures();
    	}
    	Log.i("RenderView", "<-- uninitGridLayout");
 	}
    
    public 	void  uninitPictureLayout()
	{
    	Log.i("RenderView", "--> uninitPictureLayout");
    	if(currentPicLayout != null){
    		GLSlideShowLayout ly = currentPicLayout;
	    	currentPicLayout = null;
    		ly.delLayoutTextures();
    	}
    	Log.i("RenderView", "<-- uninitPictureLayout");
 	}
    
    public 	void  uninitMusicLayout()
	{
    	Log.i("RenderView", "--> uninitMusicLayout");
    	if(currentMusicLayout != null){
    		GLMusicLayout ly = currentMusicLayout;
    		currentMusicLayout = null;
    		ly.delLayoutTextures();
    	}
    	Log.i("RenderView", "<-- uninitMusicLayout");
 	}
    
    public 	void  uninitTxtLayout()
	{
    	Log.i("RenderView", "--> uninitTxtLayout");
    	if(currentTextLayout != null)
    	{
    		GLEBookLayout ly = currentTextLayout;
	    	currentTextLayout = null;
	    	ly.setBookmark();
	    	ly.delLayoutTextures();
    	}
    	Log.i("RenderView", "<-- uninitTxtLayout");
 	}
    
    public 	void  initPictureLayout()
	{
    	Log.i("RenderView", "--> initPictureLayout");
    	if(currentPicLayout == null){
    		currentPicLayout = new GLSlideShowLayout(this.getContext(),"/sdcard", "");
    	}
    	Log.i("RenderView", "<-- initPictureLayout");
	}
    
    public 	void initMusicLayout()
	{
    	Log.i("RenderView", "--> initMusicLayout");
    	if(currentMusicLayout == null)
    		currentMusicLayout = new GLMusicLayout(this.getContext(),"/sdcard", "");
    	Log.i("RenderView", "<-- initMusicLayout");
	}
    
    public 	void initTxtLayout()
	{
    	Log.i("RenderView", "--> initTxtLayout");
    	if(currentTextLayout == null)
    	{
    		currentTextLayout = new GLEBookLayout(this.getContext(),"/sdcard", "");
    	}
    	Log.i("RenderView", "<-- initTxtLayout");
	}
    
  
    
    
    
 
    //Override
    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
    }


    
    //Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        super.surfaceDestroyed(holder);
    }

    //Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        timer.schedule(task, 0, 2000);  
    }

    //Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        timer.cancel();
    }
    
    long lastDrawFrameTime = 0;
	//Override
	public void onDrawFrame(GL10 gl) {
		TextureManager.freeDeledTextures();
		
		long curTime;
		do{
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}while((curTime = System.currentTimeMillis()) < lastDrawFrameTime+20);
		lastDrawFrameTime = curTime;
		
		if( PMTAnimator.getInstance().updateAnimations() == false){
//			return;
		}
		
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		gl.glClearColor(0, 0, 0, 0);
		gl.glLoadIdentity();
		if(currentGridLayout != null)
			currentGridLayout.drawFrame(gl);
		
		if(currentPicLayout != null)
			currentPicLayout.drawFrame(gl);
		
		if(currentMusicLayout != null)
			currentMusicLayout.drawFrame(gl);
	
		if(currentTextLayout != null)
			currentTextLayout.drawFrame(gl);	
		
		calFPS();
	}

	//Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		// TODO Auto-generated method stub

        // Set the viewport and projection matrix.
        final float zNear = 0.1f;
        final float zFar = 100.0f;
        gl.glViewport(0, 0, width, height);
        gl.glMatrixMode(GL11.GL_PROJECTION);
        gl.glLoadIdentity();
        GLU.gluPerspective(gl, 45.0f, (float) width / height, zNear, zFar);
        gl.glMatrixMode(GL11.GL_MODELVIEW);

	}

	//Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		// TODO Auto-generated method stub
        //setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
	ZLAndroidActivity.exit_flag = true;

        // Disable unused state.
        gl.glEnable(GL11.GL_DITHER);
        gl.glDisable(GL11.GL_LIGHTING);

        // Set global state.
        // gl.glHint(GL11.GL_PERSPECTIVE_CORRECTION_HINT, GL11.GL_NICEST);

        // Enable textures.
        gl.glEnable(GL11.GL_TEXTURE_2D);
        gl.glTexEnvf(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_COLOR, GL11.GL_REPLACE);

        // Set up state for multitexture operations. Since multitexture is
        // currently used
        // only for layered crossfades the needed state can be factored out into
        // one-time
        // initialization. This section may need to be folded into drawMixed2D()
        // if multitexture
        // is used for other effects.

        // Enable Vertex Arrays
        gl.glEnableClientState(GL11.GL_VERTEX_ARRAY);
        //gl.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
        //gl.glClientActiveTexture(GL11.GL_TEXTURE1);
        //gl.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
        //gl.glClientActiveTexture(GL11.GL_TEXTURE0);

        // Enable depth test.
        gl.glEnable(GL11.GL_DEPTH_TEST);
        gl.glDepthFunc(GL11.GL_LEQUAL);
        
//		gl.glDisable(GL10.GL_ALPHA_TEST);
//		gl.glEnable(GL10.GL_ALPHA_TEST);
//		gl.glAlphaFunc(GL10.GL_GREATER, 0.0f);
        
//		gl.glDisable(GL10.GL_BLEND);
        // Set the blend function for premultiplied alpha.
        gl.glEnable(GL10.GL_BLEND);
        //gl.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_ALPHA);
        //gl.glBlendFunc(GL10.GL_ONE_MINUS_DST_ALPHA, GL10.GL_DST_ALPHA);
        gl.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        
        // Set the background color.
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        gl.glClear(GL11.GL_COLOR_BUFFER_BIT);
	}
	
	
	private int FPS = 0;
	private int fpsCounter = 0;
	private long fpsStartTime = 0;
	
	private void calFPS(){
		//Here we simply calculate the FPS
		long curTime = System.currentTimeMillis();
		if(fpsStartTime==0){
			fpsStartTime = curTime;
			fpsCounter = 10;
		}
		else if(curTime > fpsStartTime + 1000){
			fpsStartTime = curTime;
			FPS += fpsCounter;
			FPS /= 2;
			fpsCounter = 0;
		}
		else{
			fpsCounter += 10;
		}
	}
	public int getFPS(){
		return FPS;
	}
    Timer timer = new Timer();  
    Handler handler = new Handler(){  
        public void handleMessage(Message msg) {  
        	int fps = getFPS();
            super.handleMessage(msg);  
            
            Log.i("FBReaderFPS", fps/10 + "." + fps%10);
        }            
    };  
    
    TimerTask task = new TimerTask(){  
        public void run() {  
            Message message = new Message();      
            message.what = 1;      
            handler.sendMessage(message);    
        }            
    };  
    
    public void onStop()
    {
    	if(currentGridLayout != null)
    		currentGridLayout.onstop();
    	
    	if(currentPicLayout != null)
    		currentPicLayout.onstop();
		
		if(currentMusicLayout != null)
			currentMusicLayout.onstop();
	
		if(currentTextLayout != null)
			currentTextLayout.onstop();	
		
    	TextureManager.clearAllTextLists();
    }
    class HahaGestureDetectorListener extends SimpleOnGestureListener{

		private static final String TAG = "renderv";

		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			// TODO Auto-generated method stub
			return super.onSingleTapUp(e);
		}

		@Override
		public void onLongPress(MotionEvent e) {
			// TODO Auto-generated method stub
			super.onLongPress(e);
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			// TODO Auto-generated method stub
			return super.onScroll(e1, e2, distanceX, distanceY);
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2,
				float velocityX, float velocityY) {
			// TODO Auto-generated method stub
			Log.d(TAG,"x,y:"+velocityX+","+ velocityY);
			return super.onFling(e1, e2, velocityX, velocityY);
		}

		@Override
		public void onShowPress(MotionEvent e) {
			// TODO Auto-generated method stub
			super.onShowPress(e);
		}

		@Override
		public boolean onDown(MotionEvent e) {
			// TODO Auto-generated method stub
			return super.onDown(e);
		}

		@Override
		public boolean onDoubleTap(MotionEvent e) {
			// TODO Auto-generated method stub
			return super.onDoubleTap(e);
		}

		@Override
		public boolean onDoubleTapEvent(MotionEvent e) {
			// TODO Auto-generated method stub
			return super.onDoubleTapEvent(e);
		}

		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			// TODO Auto-generated method stub
			return super.onSingleTapConfirmed(e);
		}
    	
    }
}
