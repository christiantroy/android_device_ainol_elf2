package com.amlogic.pmt;

/**
 * Implement this interface and call anim.addListener() to be notified of when
 * certain animation events occur.
 * 
 * <pre>{@code 
 * A3DTransformAnimation t = new A3DTransformAnimation(...);
 * t.addListener(your_class);
 * }</pre>
 * Then implements this interface.
 * <pre>{@code 
 * public class your_class implements A3DAnimationListener {
 *     public void onAnimationEvent(int event_type, A3DAnmation animation) {
 *          //...
 *     }
 * }
 * }</pre>
 * A3DAnimationManager is an example.<br>
 * For simpler listeners, this is also possible:
 * <pre>{@code
 * t.addListener(new A3DAnimationListener() { 
 *                   public void onAnimationEvent(...){..} });
 * }</pre>
 * 
 */
public interface PMTAnimationListener {
    /** When animation is starting. */
    public static final int EVENT_STARTING = (1 << 0);
    /** When animation has been stopped. */
    public static final int EVENT_STOPPED = (1 << 1);
    /** When all loops are completed. */
    public static final int EVENT_FINISHED = (1 << 2);

    /**
     * Called when certain animation events occur.
     * @param event_type
     *    Will be one of:<pre>
     *        A3DAnimationListener.EVENT_STARTING
     *        A3DAnimationListener.EVENT_FINISHED</pre>                 
     * @param animation
     */
    public void onAnimationEvent(int event_type, PMTAnimation animation);
}
