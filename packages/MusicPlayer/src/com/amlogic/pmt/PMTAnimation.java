package com.amlogic.pmt;

import java.util.Iterator;
import java.util.Vector;

import android.view.animation.Interpolator;

/**
 * Base class for animations.
 * @see PMTTransformAnimation
 * @see A3DFrameAnimation
 * @see PMTPathAnimation
 */
public abstract class PMTAnimation {
    protected GLPose mNode;
    protected Interpolator mInterp;
    protected long mDuration;
    protected long mStartTime;
    protected long mEndTime;
    protected int mState;
    private Vector<PMTAnimationListener> mListeners;

    public final int STATE_WAITSTART = 0;
    public final int STATE_RUNNING   = 1;
    public final int STATE_STOPPED   = 2;
    public final int STATE_FINISHED  = 3;    
    
	private PMTAnimation nextAnim = null;
    void setNextAnim(PMTAnimation next){
    	nextAnim = next;
    	addListener(new PMTAnimationListener(){
			//Override
			public void onAnimationEvent(int eventType, PMTAnimation animation) {
				if(eventType==EVENT_FINISHED && nextAnim!=null)
					nextAnim.start();
				if(eventType==EVENT_STOPPED && nextAnim!=null)
					nextAnim.stop();
			}});
    }
    /**
     * Animation.
     * @param node      The object to animate.
     * @param interp    An android.view.animation.Interpolator.<br>
     *                  Example:  new LinearInterpolator();
     * @param duration  Time in ms for one loop.
     * @param start     Start time.
     * @param end       End time.
     */
    public PMTAnimation(GLPose node,
                        Interpolator interp,
                        long duration, 
                        long start, 
                        long end) {
        mNode = node;
        mInterp = interp;
        mDuration = duration;
        mStartTime = start;
        mEndTime = end;
        mState = STATE_WAITSTART;
        mListeners = new Vector<PMTAnimationListener>();
        mListeners.add(PMTAnimator.getInstance());
    }
    
    /**
     * Add a listener to be notified of an animation's start and stop.    
     * @param listener
     * @return
     */
    public boolean addListener(PMTAnimationListener listener) {
        if (mListeners.contains(listener) == false) {
            mListeners.add(listener);
            return true;
        }
        return false;
    }
    
    /**
     * Used by an animation to notify all listeners.
     * @param event_type
     */
    protected void notifyListeners(int event_type) {
        Iterator<PMTAnimationListener> it = mListeners.iterator();
        while (it.hasNext()) {
            PMTAnimationListener listener = it.next();
            listener.onAnimationEvent(event_type, this);
        }
    }
    
    /**
     * Start the animation.
     */
    public abstract void start();

    /**
     * Stop the animation.
     */
    public abstract void stop();

    /**
     * Check if animation is running.
     * 
     * @return
     */
    public boolean isRunning() {
        return mState == STATE_RUNNING;
    }
    
    /**
     * Get state.
     * @return STATE_WAITSTART, STATE_RUNNING, STATE_FINISHED
     */
    public int getState() {
        return mState;
    }
    
    /** 
     * Update.  Called by Animator.
     */
    protected abstract void update(long time);
}
