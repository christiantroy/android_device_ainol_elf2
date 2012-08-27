package com.amlogic.pmt;

import android.view.animation.Interpolator;

public class PMTBoardColorAnimation extends PMTAnimation{
    private int mLoops, mLoopsLeft;
    private float startBoardColor[] = null;
	private float endBoardColor[] = null;
	private float curBoardColor[] = null;
	
    public PMTBoardColorAnimation(GLPose node,
            					  Interpolator interp,
            					  long duration,
            					  int loops) {
		super(node, interp, duration, 0, 0);
		mLoops = mLoopsLeft = loops;
    }

    public void setBoardColor(float start[], float end[]){
    	startBoardColor = start;
    	endBoardColor = end;
    	curBoardColor = startBoardColor.clone();
    }
    
	//Override
	public void start() {
        if (mState == STATE_RUNNING)
            return;
        synchronized (mNode) {
            mStartTime = System.nanoTime() / 1000000L;
            mEndTime = mStartTime + mDuration;
            moveToStart();
        }
        mLoopsLeft = mLoops;
        mState = STATE_RUNNING;
        notifyListeners(PMTAnimationListener.EVENT_STARTING);
	}
	
    private void moveToStart() {
    	mNode.setPaintBoardColor(startBoardColor);
    }
    
    private void moveToEnd() {
    	mNode.setPaintBoardColor(endBoardColor);
    }   
    
	//Override
	public void stop() {
        mState = STATE_STOPPED;
        synchronized (mNode) {
            moveToEnd(); //?
        }
        notifyListeners(PMTAnimationListener.EVENT_STOPPED);
	}

	//Override
	protected void update(long time) {
        if (mState != STATE_RUNNING)
            return;
        if (time >= mEndTime) {
            if (mLoopsLeft > 0)
                --mLoopsLeft;
            if (mLoopsLeft == 0) {
                mState = STATE_FINISHED;
                synchronized (mNode) {
                    moveToEnd();
                }
                notifyListeners(PMTAnimationListener.EVENT_FINISHED);
                return;
            }
            else {
                /* reset and start again */
                mStartTime = time;
                mEndTime = mStartTime + mDuration;
                synchronized (mNode) {
                    moveToStart();
                }
                return;
            }
        }
        synchronized (mNode) {
            float normTime = (float)(time - mStartTime) / (float)mDuration;
            float v = mInterp.getInterpolation(normTime);
            for(int i=0; i<curBoardColor.length; i++){
            	curBoardColor[i] = startBoardColor[i] + ((endBoardColor[i] - startBoardColor[i]) * v);
            }
            mNode.setPaintBoardColor(curBoardColor);
       }
	}

}
