package com.amlogic.pmt;

import java.util.Iterator;
import java.util.Vector;

import android.util.Log;

/**
 * Allows storing Animations and starting them by name. <br>
 * <br>
 * Example:
 * <pre>{@code
 * loadFromScript() {
 *   A3DTransformAnimation t = new A3DTransformAnimation(...);
 *   to.setRotation(...);
 *   A3DAnimationManager.getInstance().addAnimation("magic anim", t, false);
 * }
 * 
 * onClick() { 
 *   A3DAnimationManager.getInstance().startAnimation("magic anim");
 * }
 * 
 * onQuit() {
 *   A3DAnimationManager.getInstance().removeAnimation("magic anim");
 * }
 * }</pre>
 */
public class PMTAnimationManager implements PMTAnimationListener {
    private static final String TAG = "PMTAnimationManager";
    private static PMTAnimationManager instance = new PMTAnimationManager();
    private class AnimationM {
        public PMTAnimation mAnimation;
        public String mName;
        public boolean mAutoRemove;
        public AnimationM(String name, PMTAnimation anim, boolean autoremove) {
            mName = name;
            mAnimation = anim;
            mAutoRemove = autoremove;
        }
    }
    private Vector<AnimationM> mAnimationMs;

    private PMTAnimationManager() {
        mAnimationMs = new Vector<AnimationM>();
    }

    static public PMTAnimationManager getInstance() {
        return instance;
    }
    
    /**
     * Add animation.
     * @param animation
     * @param autoremove  Remove animation after it finishes playing all loops.
     * @return true if successful
     */
    public boolean addAnimation(String name, PMTAnimation animation, boolean autoremove) {
        if (getAnimation(name) == null) {
            mAnimationMs.add(new AnimationM(name, animation, autoremove));
            if (autoremove)
                animation.addListener(this);
            return true;
        }
        return false;
    }

    /**
     * Get animation previously added with addAnimation().
     * @param name
     * @return null if not found
     */
    public PMTAnimation getAnimation(String name) {
        Iterator<AnimationM> it = mAnimationMs.iterator();
        while (it.hasNext()) {
            AnimationM a = it.next();
            if (a.mName.equals(name))
                return a.mAnimation;
        }
        //Log.i(TAG, "animation " + name + " not found");
        return null;
    }
    
    /**
     * Get name of an animation.
     * @param animation
     * @return name, null if not found
     */
    public String getName(PMTAnimation animation) {
        Iterator<AnimationM> it = mAnimationMs.iterator();
        while (it.hasNext()) {
            AnimationM a = it.next();
            if (a.mAnimation == animation)
                return a.mName;
        }
        //Log.i(TAG, "animation not found");
        return null;
    }
    
    /**
     * Remove an animation.
     * @param name
     */
    public void removeAnimation(String name) {
        Iterator<AnimationM> it = mAnimationMs.iterator();
        while (it.hasNext()) {
            AnimationM a = it.next();
            if (a.mName.equals(name)) {
                mAnimationMs.remove(a);
                return;
            }
        }
    }

    /**
     * Start an animation by name.
     * @param name
     * @return
     */
    public boolean startAnimation(String name) {
        PMTAnimation a = getAnimation(name);
        if (a != null) {
            a.start();
            return true;
        }
        Log.i(TAG, "animation " + name + " not found");
        return false;
    }
    
    protected void finishedNotification(PMTAnimation animation) {
        //TODO notification interface
        Iterator<AnimationM> it = mAnimationMs.iterator();
        while (it.hasNext()) {
            AnimationM a = it.next();
            if (a.mAnimation == animation) {
                if (a.mAutoRemove == true)
                    mAnimationMs.remove(a);
                return;
            }
        }
    }

    //Override
    public void onAnimationEvent(int eventType, PMTAnimation animation) {
        if (eventType == PMTAnimationListener.EVENT_FINISHED) {
            Iterator<AnimationM> it = mAnimationMs.iterator();
            while (it.hasNext()) {
                AnimationM a = it.next();
                if (a.mAnimation == animation) {
                    mAnimationMs.remove(a);
                    return;
                }
            }
        }
    }
}



