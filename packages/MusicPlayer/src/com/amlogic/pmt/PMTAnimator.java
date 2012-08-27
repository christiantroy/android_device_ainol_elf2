package com.amlogic.pmt;

import java.util.Iterator;
import java.util.Vector;

import android.util.Log;

/**
 * Updates objects for their animation by calling every object's update().
 */
public class PMTAnimator implements PMTAnimationListener {
    private Vector<PMTAnimation> mActiveAnimations;
    private Vector<PMTAnimation> mStartingAnimations;
    private Vector<PMTAnimation> mFinishedAnimations;
    private static PMTAnimator instance = new PMTAnimator();
    private long mLastTime;
    private static final long FRAME_TIME_DELTA = 17;    //time between each update in milliseconds
    private static final int STATE_DEAD = 0;
    private static final int STATE_ALIVE = 1;
    private static final int STATE_SLEEPING = 2;
    private int mState = STATE_ALIVE;
    
    private PMTAnimator() {
        super();
        mActiveAnimations = new Vector<PMTAnimation>();
        mStartingAnimations = new Vector<PMTAnimation>();
        mFinishedAnimations = new Vector<PMTAnimation>();
        mLastTime = 0;
    }

    static public PMTAnimator getInstance() {
    	return instance;
    }

    /**
     * Update all animations
     * @return false if nothing updated, true otherwise
     */
    public boolean updateAnimations() {
        synchronized (instance) {
            mActiveAnimations.removeAll(mFinishedAnimations);
            mFinishedAnimations.clear();
            mActiveAnimations.addAll(mStartingAnimations);
            mStartingAnimations.clear();
            if (mState != STATE_ALIVE || 
                mActiveAnimations.isEmpty()) {
                return false;
            }
            long time = System.nanoTime() / 1000000L;
            long timedelta = time - mLastTime;
            if (timedelta < FRAME_TIME_DELTA) {
                return false;
            }
            mLastTime = time;
            
            Iterator<PMTAnimation> it = mActiveAnimations.iterator();
            while (it.hasNext()) {
                PMTAnimation a = it.next();
                a.update(time);
            }
            return true;
        }
    }

    public void reqResume() {
        synchronized (instance) {
            mState = STATE_ALIVE;
        }
    }
    
    public void reqPause() {
        synchronized (instance) {
            mState = STATE_SLEEPING;
        }
    }
    
    public void reqDie() {
        synchronized (instance) {
            mActiveAnimations.clear();
            mState = STATE_DEAD;
        }
    }
    
    public void reqStop(){
        synchronized (instance) {
            Iterator<PMTAnimation> it = mActiveAnimations.iterator();
            while (it.hasNext()) {
                PMTAnimation a = it.next();
                a.stop();
                mFinishedAnimations.add(a);
            }
            mActiveAnimations.clear();
        }
    }

    /**
     * All animations must notify the animator when they are starting,
     * otherwise the animations will not be updated.
     * <br><br>
     * Animation.start() will notify the Animator to add it to the active list, 
     * and then the renderer will update all animations by calling
     * Animator.update(). 
     */
    //Override
    public void onAnimationEvent(final int eventType, final PMTAnimation animation) {
        /* mStartingAnimations and mFinishedAnimations are used instead of
         * modifying mActiveAnimations directly because this method can be 
         * called from the loop using Iterator<A3DAnimation>.
         */
        synchronized (instance) {
            if (eventType == PMTAnimationListener.EVENT_STARTING) {
                if (mStartingAnimations.contains(animation) == false && 
                    mActiveAnimations.contains(animation) == false) {
                    mStartingAnimations.add(animation);
                }
            }
            else if (eventType == PMTAnimationListener.EVENT_FINISHED) {
                mFinishedAnimations.add(animation);
            }
        }
    }

    /**
     * Start all animations in a list at the same time.
     * @param animlist
     */
    public void startAnimations(Vector<PMTAnimation> animlist) {
        synchronized (instance) {
            Iterator<PMTAnimation> it = animlist.iterator();
            while (it.hasNext()) {
                PMTAnimation a = it.next();
                a.start();
            }
        }
    }
}
