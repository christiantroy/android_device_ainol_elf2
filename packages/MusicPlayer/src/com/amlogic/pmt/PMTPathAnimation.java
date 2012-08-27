package com.amlogic.pmt;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

//import android.util.Log;
import android.view.animation.Interpolator;

/**
 * Move an object to different points.
 * <br><br>
 * The path types are: <br>
 * PATH_STRAIGHT will move the object in straight lines to each of the points.
 * <br><br>
 * PATH_QUAD_CURVE will move the object in quadratic Bezier curves defined by
 * the points.  The point order is start point, guide point, end point.<br>  
 * If additional points are added, the last end point also becomes a start point 
 * for the next: <br>
 * start, guide, end/start, guide, end/start, guide, end...
 * <br><br>
 * Example:
 * <pre>{@code
 * A3DObject o = world.getObject(null, "laalaa");
 * if (o == null)
 *   return;
 * A3DPathAnimation a = new A3DPathAnimation(o, new LinearInterpolator(),
 *                                   3000, A3DPathAnimation.PATH_QUAD_CURVE, 1);
 * a.addPoint(-4, 0, -5);
 * a.addPoint(0, 4, -5);
 * a.addPoint(4, 0, -5);
 * a.start();
 * }</pre>
 */
public class PMTPathAnimation extends PMTAnimation {
    /** Move the object in straight lines to each of the points. */
    public static final int PATH_STRAIGHT = 1;
    /** Move the object in quadratic Bezier curves. */
    public static final int PATH_QUAD_CURVE = 2;

    private int mPathType;
    private float mTotalDistance;
    private int mLoops, mLoopsLeft;
    
    private class Point {
        float mX, mY, mZ;
        /** 
         * In PATH_STRAIGHT, this is the distance to the next point.<br>
         * In PATH_QUAD_CURVE, if the current point is a start point, this
         * is the distance (length of the arc) to the end point.
         *  */
        float mDist;
        public Point(float x, float y, float z) {
            mX = x; mY = y; mZ = z;
            mDist = 0;
        }
    }
    private ArrayList<Point> mControlPoints;

    /**
     * An animation where object follows a path.
     * @param node
     * @param interp    An android.view.animation.Interpolator.
     *                  Example:  new LinearInterpolator();
     * @param duration  Time in ms for one loop.
     * @param path_type See notes for class. 
     *                  A3DPathAnimation.PATH_STRAIGHT or PATH_QUAD_CURVE
     * @param loops     Number of times to play animation.  -1 for infinite.
     */
    public PMTPathAnimation(GLPose node,
                            Interpolator interp,
                            long duration,
                            int path_type,
                            int loops) {
        super(node, interp, duration, 0, 0);
        mPathType = path_type;
        mTotalDistance = 0;
        mControlPoints = new ArrayList<Point>();
        mLoops = mLoopsLeft = loops;
    }
    
    /** Change the node so an animation path can be reused for different nodes. */
    public boolean setNode(GLPose node) {
        if (mState == STATE_RUNNING)
            return false;
        mNode = node;
        return true;
    }
    
    public GLPose getNode() {
        return mNode;
    }
    
    private float getDistance(Point a, Point b) {
        return (float) (Math.sqrt(  Math.pow((b.mX - a.mX), 2)
                                  + Math.pow((b.mY - a.mY), 2)
                                  + Math.pow((b.mZ - a.mZ), 2)));
    }
    
    /**
     * Add a point.  See class notes.
     * @param x
     * @param y
     * @param z
     * @return
     */
    public boolean addPoint(float x, float y, float z) {
        if (mState == STATE_RUNNING)
            return false;
        if (mPathType == PATH_STRAIGHT) {
            int size = mControlPoints.size(); 
            if (size > 0) {
                Point prev = mControlPoints.get(size - 1);
                Point curr = new Point(x, y, z);
                mControlPoints.add(curr);
                prev.mDist = getDistance(prev, curr);
                mTotalDistance += prev.mDist;
            }
            else
                mControlPoints.add(new Point(x, y, z));
            return true;
        }
        else if (mPathType == PATH_QUAD_CURVE) {
            Point curr = new Point(x, y, z);
            mControlPoints.add(curr);
            int size = mControlPoints.size();
            if (size > 2 && (((size-1) % 2) == 0)) {
                Point start = mControlPoints.get(size - 3);
                Point guide = mControlPoints.get(size - 2);
                Point end   = mControlPoints.get(size - 1);
                if (start != null && guide != null && end != null) {
                    start.mDist = getQuadCurveLength(start, guide, end);
                    mTotalDistance += start.mDist;
                    //Log.d("PathAnim", "mTotalDistance+=" + start.mDist + "==" + mTotalDistance + " size=" + size);
                }
            }
            return true;
        }
        return false;
    }

    //Override
    public void start() {
        if (mState == STATE_RUNNING)
            return;
        if (mPathType == PATH_STRAIGHT ||
            (mPathType == PATH_QUAD_CURVE && mControlPoints.size() > 2 && mTotalDistance > 0)) {
            synchronized (mNode) {
                mStartTime = System.nanoTime() / 1000000L;
                mEndTime = mStartTime + mDuration;
                moveToStart();
            }
            mLoopsLeft = mLoops;
            mState = STATE_RUNNING;
            notifyListeners(PMTAnimationListener.EVENT_STARTING);
        }
    }

    //Override
    public void stop() {
        mState = STATE_STOPPED;
        synchronized (mNode) {
            moveToEnd(); //?
        }
        notifyListeners(PMTAnimationListener.EVENT_STOPPED);
    }

    private void moveToStart() {
        if (mPathType == PATH_STRAIGHT || mPathType == PATH_QUAD_CURVE) {
            if (mControlPoints.size() > 0)
                mNode.setPosition(mControlPoints.get(0).mX,
                                  mControlPoints.get(0).mY,
                                  mControlPoints.get(0).mZ);
        }
    }
    
    private void moveToEnd() {
        if (mPathType == PATH_STRAIGHT || mPathType == PATH_QUAD_CURVE) {
            int size = mControlPoints.size();
            if (size > 0)
                mNode.setPosition(mControlPoints.get(size-1).mX,
                                  mControlPoints.get(size-1).mY,
                                  mControlPoints.get(size-1).mZ);
        }
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
        /* move */
        synchronized (mNode) {
            float normTime = (float)(time - mStartTime) / (float)mDuration;
            float v = mInterp.getInterpolation(normTime);
    
            if (mPathType == PATH_STRAIGHT) {
                float interpDist = mTotalDistance * v;
                float dist = 0;
                for (int i=0; i < mControlPoints.size(); ++i) {
                    Point c = mControlPoints.get(i);
                    if (dist + c.mDist >= interpDist) {
                        // desired point is between c and c's next (Point n).
                        float subInterp = (interpDist - dist) / c.mDist;
                        if ((i + 1) < mControlPoints.size()) {
                            Point n = mControlPoints.get(i + 1);
                            float x, y, z;
                            x = (c.mX + ((n.mX - c.mX) * subInterp));
                            y = (c.mY + ((n.mY - c.mY) * subInterp));
                            z = (c.mZ + ((n.mZ - c.mZ) * subInterp));
                            mNode.setPosition(x, y, z);
                        }
                        return;
                    }
                    dist += c.mDist;
                }
            }
            else if (mPathType == PATH_QUAD_CURVE) {
                float interpDist = mTotalDistance * v;
                float dist = 0;
                for (int i=0; i <= (mControlPoints.size() - 3); i+=2) {
                    Point c = mControlPoints.get(i);
                    if (dist + c.mDist >= interpDist) {
                        // desired point is on the curve starting at c
                        float subInterp = (interpDist - dist) / c.mDist;
                        Point g = mControlPoints.get(i + 1);
                        Point e = mControlPoints.get(i + 2); 
                        Point pos = getQuadCurvePoint(c, g, e, subInterp);
                        mNode.setPosition(pos.mX, pos.mY, pos.mZ);
                        return;
                    }
                    dist += c.mDist;
                }
            }
        }
    }

    private Point getQuadCurvePoint(Point start, Point guide, Point end, float interp) {
        float x, y, z;
        x = (1 - interp) * (1 - interp) * start.mX +
            2 * (1 - interp) * interp * guide.mX +
            interp * interp * end.mX;
        y = (1 - interp) * (1 - interp) * start.mY +
            2 * (1 - interp) * interp * guide.mY +
            interp * interp * end.mY;
        z = (1 - interp) * (1 - interp) * start.mZ +
            2 * (1 - interp) * interp * guide.mZ +
            interp * interp * end.mZ;
        return new Point(x, y, z);
    }
    
    private float getQuadCurveLength(Point start, Point guide, Point end) {
        final float SAMPLESTEP = (1.0f / 128);
        float len = 0;
        Point prev, curr;
        prev = start;
        for (float i=SAMPLESTEP; i<1.0; i+=SAMPLESTEP) {
            curr = getQuadCurvePoint(start, guide, end, i);
            len += getDistance(prev, curr);
            prev = curr;
        }
        return len;
    }
    
}

