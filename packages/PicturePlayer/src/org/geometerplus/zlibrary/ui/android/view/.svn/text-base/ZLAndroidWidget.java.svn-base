/*
 * Copyright (C) 2007-2010 Geometer Plus <contact@geometerplus.com>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 */

package org.geometerplus.zlibrary.ui.android.view;

import android.content.Context;
import android.graphics.*;
import android.view.*;
import android.util.AttributeSet;
import android.util.Log;

import org.geometerplus.zlibrary.core.view.ZLView;
import org.geometerplus.zlibrary.core.application.ZLApplication;

import org.geometerplus.zlibrary.ui.android.library.ZLAndroidActivity;

public class ZLAndroidWidget/* extends View*/ {
	private final Paint myPaint = new Paint();
	private Bitmap myMainBitmap;
	private Bitmap mySecondaryBitmap;
	private boolean mySecondaryBitmapIsUpToDate;
	private Bitmap myFooterBitmap;

	private boolean myScrollingInProgress;
	private int myScrollingShift;
	private float myScrollingSpeed;
	private int myScrollingBound;
	
	private boolean  renderThreadFlag = true;
	private Context mContext;
	private Context getContext(){
		return mContext;
	}
	private int getWidth() {
		// TODO Auto-generated method stub
		return 1280;
	}
	private boolean requestRender = false;
	public void setRequestRender(boolean b){
		requestRender = b;
	}
	public void postInvalidate() {
		// TODO Auto-generated method stub
		setRequestRender(true);
	}
	public Boolean updatedPageAvailable = false;
	public Boolean drawFinish           = false;
	private boolean PAUSE_RENDER_THREAD = false;
	private boolean STOP_RENDER_THREAD = false;
	public void pauseRender(){
		PAUSE_RENDER_THREAD = true;
	}
	public void stopRender(){
		STOP_RENDER_THREAD = true;
	}
	public void resumeRender(){
		PAUSE_RENDER_THREAD = false;
	}
	private int getVerticalScrollbarWidth() {
		// TODO Auto-generated method stub
		return 40;
	}
	
	Thread renderThread;
	
	public ZLAndroidWidget(Context context, AttributeSet attrs, int defStyle) {
		//super(context, attrs, defStyle);
		//setDrawingCacheEnabled(false);
		mContext = context;
	}

	public ZLAndroidWidget(Context context, AttributeSet attrs) {
		//super(context, attrs);
		//setDrawingCacheEnabled(false);
		mContext = context;
	}

	public ZLAndroidWidget(Context context) {
		//super(context);
		//setDrawingCacheEnabled(false);
		mContext = context;
		renderThread = new Thread(new Runnable() {
			public void run() {
				while (renderThreadFlag && !renderThread.isInterrupted() ) {
					synchronized (updatedPageAvailable) {
						try {
							if(STOP_RENDER_THREAD){
								break;
							}
							if(PAUSE_RENDER_THREAD){
								Thread.sleep(20);
							}else {
								if(requestRender){	
									onDraw();
									requestRender = false;
									updatedPageAvailable = true;
									drawFinish = true;

								}else{
									Thread.sleep(20);
								}
							}
						} catch (InterruptedException e) {
						}
					}
				}
			}
		});
		renderThread.start();
	}

	public void RecycleBitmap()
	{
//		renderThreadFlag = false;
		if(myMainBitmap != null)
		{
			myMainBitmap.recycle();
			myMainBitmap = null;
		}
			
		if(mySecondaryBitmap != null)
		{
			mySecondaryBitmap.recycle();
			mySecondaryBitmap = null;
		}
			
		if(myFooterBitmap != null)
		{
			myFooterBitmap.recycle();
			myFooterBitmap = null;
		}
	}
			
	////Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		/*super.onSizeChanged(w, h, oldw, oldh);
		if (myScreenIsTouched) {
			final ZLView view = ZLApplication.Instance().getCurrentView();
			myScrollingInProgress = false;
			myScrollingShift = 0;
			myScreenIsTouched = false;
			view.onScrollingFinished(ZLView.PAGE_CENTRAL);
			setPageToScroll(ZLView.PAGE_CENTRAL);
		}*/
	}

	////Override
	//protected void onDraw(final Canvas canvas) {
	private  void onDraw() {
		
		final Context context = getContext();
		if (context instanceof ZLAndroidActivity) {
//			((ZLAndroidActivity)context).createWakeLock();
		} else {
//			System.err.println("A surprise: view's context is not a ZLAndroidActivity");
		}
		//super.onDraw(canvas);

		final int w = getWidth();
		final int h = getMainAreaHeight();

		if ((myMainBitmap != null) && ((myMainBitmap.getWidth() != w) || (myMainBitmap.getHeight() != h))) {
			myMainBitmap.recycle();
			myMainBitmap = null;
			mySecondaryBitmap = null;
			System.gc();
			System.gc();
			System.gc();
		}
		if (myMainBitmap == null) {
			myMainBitmap = Bitmap.createBitmap(1280, 720, Bitmap.Config.ARGB_8888);
			mySecondaryBitmap = Bitmap.createBitmap(1280, 720, Bitmap.Config.ARGB_8888);
			mySecondaryBitmapIsUpToDate = false;
//			drawOnBitmap(myMainBitmap);
			Log.v("CREATEBITMAP","------BITMAP IS CREATED-----");
		}

		if (myScrollingInProgress || (myScrollingShift != 0)) {
			//onDrawInScrolling(canvas);
			onDrawInScrolling();
		} else {
			//onDrawStatic(canvas);
			onDrawStatic();
			ZLApplication.Instance().onRepaintFinished();
		}
	}

	//private void onDrawInScrolling(Canvas canvas) {
	private void onDrawInScrolling() {
		final int w = getWidth();
		final int h = getMainAreaHeight();

		boolean stopScrolling = false;
		if (myScrollingInProgress) {
			myScrollingShift += (int)myScrollingSpeed;
			if (myScrollingSpeed > 0) {
				if (myScrollingShift >= myScrollingBound) {
					myScrollingShift = myScrollingBound;
					stopScrolling = true;
				}
			} else {
				if (myScrollingShift <= myScrollingBound) {
					myScrollingShift = myScrollingBound;
					stopScrolling = true;
				}
			}
			myScrollingSpeed *= 1.5;
		}
		final boolean horizontal =
			(myViewPageToScroll == ZLView.PAGE_RIGHT) ||
			(myViewPageToScroll == ZLView.PAGE_LEFT);
		/*canvas.drawBitmap(
			myMainBitmap,
			horizontal ? myScrollingShift : 0,
			horizontal ? 0 : myScrollingShift,
			myPaint
		);*/
		final int size = horizontal ? w : h;
		int shift = (myScrollingShift < 0) ? (myScrollingShift + size) : (myScrollingShift - size);
		/*canvas.drawBitmap(
			mySecondaryBitmap,
			horizontal ? shift : 0,
			horizontal ? 0 : shift,
			myPaint
		);*/

		if (stopScrolling) {
			final ZLView view = ZLApplication.Instance().getCurrentView();
			if (myScrollingBound != 0) {
				Bitmap swap = myMainBitmap;
				myMainBitmap = mySecondaryBitmap;
				mySecondaryBitmap = swap;
				mySecondaryBitmapIsUpToDate = false;
				view.onScrollingFinished(myViewPageToScroll);
				ZLApplication.Instance().onRepaintFinished();
			} else {
				view.onScrollingFinished(ZLView.PAGE_CENTRAL);
			}
			setPageToScroll(ZLView.PAGE_CENTRAL);
			myScrollingInProgress = false;
			myScrollingShift = 0;
		} else {
			if (shift < 0) {
				shift += size;
			}
			// TODO: set color
			myPaint.setColor(Color.rgb(127, 127, 127));
			if (horizontal) {
				//canvas.drawLine(shift, 0, shift, h + 1, myPaint);
			} else {
				//canvas.drawLine(0, shift, w + 1, shift, myPaint);
			}
			if (myScrollingInProgress) {
				postInvalidate();
			}
		}

		//drawFooter(canvas);
//		drawFooter();
	}

	private int myViewPageToScroll = ZLView.PAGE_CENTRAL;
	private void setPageToScroll(int viewPage) {
		if (myViewPageToScroll != viewPage) {
			myViewPageToScroll = viewPage;
			mySecondaryBitmapIsUpToDate = false;
		}
	}

	void scrollToPage(int viewPage, int shift) {
		switch (viewPage) {
			case ZLView.PAGE_BOTTOM:
			case ZLView.PAGE_RIGHT:
				shift = -shift;
				break;
		}

		if (myMainBitmap == null) {
			return;
		}
		if (((shift > 0) && (myScrollingShift <= 0)) ||
			((shift < 0) && (myScrollingShift >= 0))) {
			mySecondaryBitmapIsUpToDate = false;
		}
		myScrollingShift = shift;
		setPageToScroll(viewPage);
		drawOnBitmap(mySecondaryBitmap);
		postInvalidate();
	}

	void startAutoScrolling(int viewPage) {
		if (myMainBitmap == null) {
			return;
		}
		myScrollingInProgress = true;
		switch (viewPage) {
			case ZLView.PAGE_CENTRAL:
				switch (myViewPageToScroll) {
					case ZLView.PAGE_CENTRAL:
						myScrollingSpeed = 0;
						break;
					case ZLView.PAGE_LEFT:
					case ZLView.PAGE_TOP:
						myScrollingSpeed = -3;
						break;
					case ZLView.PAGE_RIGHT:
					case ZLView.PAGE_BOTTOM:
						myScrollingSpeed = 3;
						break;
				}
				myScrollingBound = 0;
				break;
			case ZLView.PAGE_LEFT:
				myScrollingSpeed = 3;
				myScrollingBound = getWidth();
				break;
			case ZLView.PAGE_RIGHT:
				myScrollingSpeed = -3;
				myScrollingBound = -getWidth();
				break;
			case ZLView.PAGE_TOP:
				myScrollingSpeed = 3;
				myScrollingBound = getMainAreaHeight();
				break;
			case ZLView.PAGE_BOTTOM:
				myScrollingSpeed = -3;
				myScrollingBound = -getMainAreaHeight();
				break;
		}
		if (viewPage != ZLView.PAGE_CENTRAL) {
			setPageToScroll(viewPage);
		}
		drawOnBitmap(mySecondaryBitmap);
		postInvalidate();
	}

	private void drawOnBitmap(Bitmap bitmap) {
		final ZLView view = ZLApplication.Instance().getCurrentView();
		if (view == null) {
			return;
		}

		if (bitmap == myMainBitmap) {
			mySecondaryBitmapIsUpToDate = false;
		} else if (mySecondaryBitmapIsUpToDate) {
			return;
		} else {
			mySecondaryBitmapIsUpToDate = true;
		}

//		final ZLAndroidPaintContext context = new ZLAndroidPaintContext(
//			new Canvas(bitmap),
//			getWidth(),
//			getMainAreaHeight(),
//			//view.isScrollbarShown() ? getVerticalScrollbarWidth() : 0
//			0
//		);
		final ZLAndroidPaintContext context = new ZLAndroidPaintContext(
				new Canvas(bitmap),
				1280,
				720,
				//view.isScrollbarShown() ? getVerticalScrollbarWidth() : 0
				0
			);
		view.paint(context, (bitmap == myMainBitmap) ? ZLView.PAGE_CENTRAL : myViewPageToScroll);
	
	}

	//private void drawFooter(Canvas canvas) {
	private void drawFooter() {
		final ZLView view = ZLApplication.Instance().getCurrentView();
		final ZLView.FooterArea footer = view.getFooterArea();
		if (footer != null) {
			if (myFooterBitmap != null &&
				(myFooterBitmap.getWidth() != getWidth() ||
				 myFooterBitmap.getHeight() != footer.getHeight())) {
				myFooterBitmap = null;
			}
			if (myFooterBitmap == null) {
				myFooterBitmap = Bitmap.createBitmap(getWidth(), footer.getHeight(), Bitmap.Config.ARGB_4444);
			}
			final ZLAndroidPaintContext context = new ZLAndroidPaintContext(
				new Canvas(myFooterBitmap),
				getWidth(),
				footer.getHeight(),
				view.isScrollbarShown() ? getVerticalScrollbarWidth() : 0
			);
			footer.paint(context);
			//canvas.drawBitmap(myFooterBitmap, 0, getMainAreaHeight(), myPaint);
		} else {
			myFooterBitmap = null;
		}
	}

	//private void onDrawStatic(Canvas canvas) {
	private void onDrawStatic() {
		drawOnBitmap(myMainBitmap);
		//canvas.drawBitmap(myMainBitmap, 0, 0, myPaint);
		//drawFooter(canvas);
//		drawFooter();
//		postInvalidate();
	}

	


	/*
	private class LongClickRunnable implements Runnable {
		public void run() {
			if (performLongClick()) {
				myLongClickPerformed = true;
			}
		}
	}
	
	private LongClickRunnable myPendingLongClickRunnable;
	private boolean myLongClickPerformed;

	private void postLongClickRunnable() {
        myLongClickPerformed = false;
        if (myPendingLongClickRunnable == null) {
            myPendingLongClickRunnable = new LongClickRunnable();
        }
        postDelayed(myPendingLongClickRunnable, 2 * ViewConfiguration.getLongPressTimeout());
    }

	private boolean myPendingPress;
	private int myPressedX, myPressedY;
	private boolean myScreenIsTouched;
	//Override
	public boolean onTouchEvent(MotionEvent event) {
		int x = (int)event.getX();
		int y = (int)event.getY();

		final ZLView view = ZLApplication.Instance().getCurrentView();
		switch (event.getAction()) {
			case MotionEvent.ACTION_UP:
				if (!myLongClickPerformed) {
					if (myPendingLongClickRunnable != null) {
						removeCallbacks(myPendingLongClickRunnable);
					}
					if (myPendingPress) {
						view.onStylusPress(myPressedX, myPressedY);
					}
					view.onStylusRelease(x, y);
				}
				myPendingPress = false;
				myScreenIsTouched = false;
				break;
			case MotionEvent.ACTION_DOWN:
				postLongClickRunnable();
				myScreenIsTouched = true;
				myPendingPress = true;
				myPressedX = x;
				myPressedY = y;
				break;
			case MotionEvent.ACTION_MOVE:
				if (!myLongClickPerformed) {
					if (myPendingPress) {
						final int slop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
						if (Math.abs(myPressedX - x) > slop || Math.abs(myPressedY - y) > slop) {
							if (myPendingLongClickRunnable != null) {
								removeCallbacks(myPendingLongClickRunnable);
							}
							view.onStylusPress(myPressedX, myPressedY);
							myPendingPress = false;
						}
					}
					if (!myPendingPress) {
						view.onStylusMovePressed(x, y);
					}
				}
				break;
		}

		return true;
	}
	*/


	protected int computeVerticalScrollExtent() {
		final ZLView view = ZLApplication.Instance().getCurrentView();
		if (!view.isScrollbarShown()) {
			return 0;
		}
		if (myScrollingInProgress || (myScrollingShift != 0)) {
			final int from = view.getScrollbarThumbLength(ZLView.PAGE_CENTRAL);
			final int to = view.getScrollbarThumbLength(myViewPageToScroll);
			final boolean horizontal =
				(myViewPageToScroll == ZLView.PAGE_RIGHT) ||
				(myViewPageToScroll == ZLView.PAGE_LEFT);
			final int size = horizontal ? getWidth() : getMainAreaHeight();
			final int shift = Math.abs(myScrollingShift);
			return (from * (size - shift) + to * shift) / size;
		} else {
			return view.getScrollbarThumbLength(ZLView.PAGE_CENTRAL);
		}
	}

	protected int computeVerticalScrollOffset() {
		final ZLView view = ZLApplication.Instance().getCurrentView();
		if (!view.isScrollbarShown()) {
			return 0;
		}
		if (myScrollingInProgress || (myScrollingShift != 0)) {
			final int from = view.getScrollbarThumbPosition(ZLView.PAGE_CENTRAL);
			final int to = view.getScrollbarThumbPosition(myViewPageToScroll);
			final boolean horizontal =
				(myViewPageToScroll == ZLView.PAGE_RIGHT) ||
				(myViewPageToScroll == ZLView.PAGE_LEFT);
			final int size = horizontal ? getWidth() : getMainAreaHeight();
			final int shift = Math.abs(myScrollingShift);
			return (from * (size - shift) + to * shift) / size;
		} else {
			return view.getScrollbarThumbPosition(ZLView.PAGE_CENTRAL);
		}
	}

	protected int computeVerticalScrollRange() {
		final ZLView view = ZLApplication.Instance().getCurrentView();
		if (!view.isScrollbarShown()) {
			return 0;
		}
		return view.getScrollbarFullSize();
	}

	private int getMainAreaHeight() {
		//final ZLView.FooterArea footer = ZLApplication.Instance().getCurrentView().getFooterArea();
		//return footer != null ? getMainAreaHeight() - footer.getHeight() : getMainAreaHeight();
		return 720;
	}
	public Bitmap getMainBitmap(){
		return myMainBitmap;
	}
}
