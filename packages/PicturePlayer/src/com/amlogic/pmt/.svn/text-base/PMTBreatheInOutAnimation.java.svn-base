package com.amlogic.pmt;

import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

public class PMTBreatheInOutAnimation {
    private float inColor[] = {
    		1,1,1,1,
    		1,1,1,1,
    		1,1,1,1,
    		1,1,1,1,
    	};

    private float outColor[] = {
    		1,1,1,1,
    		1,1,1,1,
    		1,1,1,1,
    		1,1,1,1,
    	};
    
    private PMTBoardColorAnimation in = null;
    private PMTBoardColorAnimation out = null;
    
	private PMTAnimationListener inListener = new PMTAnimationListener(){
		public void onAnimationEvent(int eventType, PMTAnimation animation) {
			if(eventType==EVENT_FINISHED){
				out.start();
			}
		}
	};
	private PMTAnimationListener outListener = new PMTAnimationListener(){
		public void onAnimationEvent(int eventType, PMTAnimation animation) {
			if(eventType==EVENT_FINISHED){
				in.start();
			}
		}
	};
	
    public PMTBreatheInOutAnimation(GLPose node, long duration, float alpha){
    	for(int i=3; i<inColor.length; i+=4){
    		inColor[i] = alpha;
    	}
    	
    	in = new PMTBoardColorAnimation(node, new AccelerateInterpolator(), duration/2, 1);
    	in.setBoardColor(outColor, inColor);
    	in.addListener(inListener);
    	out = new PMTBoardColorAnimation(node, new DecelerateInterpolator(), duration/2, 1);
    	out.setBoardColor(inColor, outColor);
    	out.addListener(outListener);
    }
    
    public void start(){
    	if(in != null)
    		in.start();
    }
    
    public void stop(){
    	if(in != null)
    		in.stop();
    	if(out != null)
    		out.stop();
    }
}
