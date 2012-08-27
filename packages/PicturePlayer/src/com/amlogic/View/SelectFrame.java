package com.amlogic.View;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import com.amlogic.pmt.Resolution;
import com.amlogic.BreakText;
import com.amlogic.Listener.AmlogicMenuListener;
import com.amlogic.AmlogicMenu.Menucontrol;
import com.amlogic.AmlogicMenu.SearchDrawable;
import android.view.View.OnTouchListener;
import android.view.MotionEvent;

//wk 110301 start
public class SelectFrame extends View implements View.OnClickListener {
	private ArrayList<String> selectContext;
	private ArrayList<String> selectContextID;

	private Bitmap allBtpBG;
	private int total_item_count = 0;
	private int cur_page_index = 0;
	private int cur_page_show_item_count = 0;
	private int cc_page_show_item_count = 0;

	private Bitmap btpFoucs = null;
	private Bitmap btpCheck = null;
	private int foucsID;
	private int checkedID;
	private boolean focusFlag = false;
	private AmlogicMenuListener selectMenuListener;
	private SearchDrawable searchID;
	private boolean bSelect;
	private int startfocusID = -1;
	private boolean bTVSelectFlag = false;
	private Paint paint;

	private int MouseX;
	private int MouseY;
	private int layout_w;
	private int layout_h;
	private int layout_x;
	private int layout_y;
	public SelectFrame(Context context, AttributeSet attrs) {
		super(context, attrs);
		// this.setFocusable(true);
		this.setVisibility(View.INVISIBLE);
		searchID = new SearchDrawable(this.getContext());

		paint = new Paint();
		paint.setColor(Color.WHITE);
		paint.setTextSize(25*Resolution.getScaleX());
		paint.setTextAlign(Paint.Align.CENTER);
		paint.setAntiAlias(true);

		OnTouchListener OnTouchListener = new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				Log.d("SelectFrame","ontouch action:"+event.getAction()+","+event.getRawX()+","+event.getRawY());
				MouseX=(int) event.getRawX();
				MouseY=(int) event.getRawY();
				return false;
			}
		};	
		setOnTouchListener(OnTouchListener);
		setOnClickListener(this);
	}

	public void onClick(View v) {
        Log.e("SelectFrame","--------MouseX:"+MouseX);
		int index=checkInItemID(MouseX,MouseY);
		if(index>=0)
			{			
			foucsID=index;
			this.invalidate();
			Log.d("my",checkedID+","+foucsID+","+startfocusID);
			if (checkedID != (cur_page_index * cc_page_show_item_count + foucsID)) {
				checkedID = cur_page_index * cc_page_show_item_count + foucsID;
			}
			if (bSelect) {
				if (!bTVSelectFlag)
					if (Menucontrol.initState != null) {
						if (startfocusID != -1) {
							if (startfocusID < Menucontrol.initState.size())
								Menucontrol.initState.remove(startfocusID);
						}
						Menucontrol.initState.add(selectContextID
								.get(checkedID));
						startfocusID = Menucontrol.initState.size() - 1;
					}
			}
			if (selectMenuListener != null) {
				selectMenuListener.SelectFrameToMenuHandle(selectContextID
						.get(checkedID));
			}
			}
    }
	public int checkInItemID(int x,int y)
		{
		/*int scale_x=(int) (x/Resolution.getScaleX())-layout_x;
		int scale_y=(int) (y/Resolution.getScaleY())-layout_y;*/
		int scale_x=(int) (x-layout_x);
		int scale_y=(int) (y-layout_y);
		int item_h=(int)(layout_h/cur_page_show_item_count);
		if(scale_x<layout_w && scale_x>0)
			{
			for(int i=0;i<cur_page_show_item_count;i++)
				{
				if(scale_y>i*item_h && scale_y<(i+1)*item_h)
					{
					return i;
					}
				}
			}
		return -1;
		}
	public void setSFLayout(int w,int h,int x,int y)
		{
		layout_w=w;
		layout_h=h;
		layout_x=x;
		layout_y=y;
		}
	public void setSFData(ArrayList<String> list, ArrayList<String> listID,
			boolean ifselect) {
		int checked_id = -1;
		selectContext = list;
		selectContextID = listID;
		total_item_count = selectContext.size();
		if (total_item_count > 9) {
			cc_page_show_item_count = 9;
		} else {
			cc_page_show_item_count = total_item_count;
		}

		bSelect = ifselect;
		if (bSelect == true) {

			int focus = filterIDGetFocus();
			if (focus >= 0) {	
				checkedID = focus;
				bTVSelectFlag = true; // initState is not save data of checkedID
			} else if (Menucontrol.initState != null) {
				for (int i = 0; i < Menucontrol.initState.size(); i++) {
					for (int j = 0; j < listID.size(); j++) {
						if (listID.get(j).equals(Menucontrol.initState.get(i))) {
							if (total_item_count > 9) {
								cur_page_index = j / cc_page_show_item_count;
								foucsID = j % cc_page_show_item_count;

							} else {
								cur_page_index = 0;
								foucsID = j;
							}
							checkedID = j;
							checked_id = checkedID;
							startfocusID = i;
							i = Menucontrol.initState.size();
							break;
						}
					}
				}
			}
		} else {
			foucsID = 0;
		}

		if (checked_id != -1) {
			if (checked_id >= total_item_count) {
				checked_id = 0;
			}
			if (total_item_count > 9) {
				cur_page_index = checked_id / cc_page_show_item_count;
				foucsID = checked_id % cc_page_show_item_count;
			} else {
				cur_page_index = 0;
				foucsID = checked_id;
			}
			checkedID = checked_id;
		}
		setCurPageShowItemCount();
		getBitmapBG();
	}

	public void initSelectItem(int focusid) {
		bTVSelectFlag = true;
		if (focusid >= total_item_count)
			focusid = total_item_count - 1;
		if (total_item_count > 9) {
			foucsID = focusid % cc_page_show_item_count;
			checkedID = focusid;
		} else {
			foucsID = focusid;
			checkedID = focusid;
		}
	}

	public void restoreSFData() {
		focusFlag = false;
		bTVSelectFlag = false;
		startfocusID = -1;
		checkedID = 0;
		foucsID = 0;
		total_item_count = 0;
		cur_page_index = 0;
		cur_page_show_item_count = 0;
		cc_page_show_item_count = 0;
		this.setVisibility(View.INVISIBLE);
	}

	public boolean getFocusFlag() {
		return focusFlag;
	}

	public void setmyFocus() {
		focusFlag = true;
		this.invalidate();
	}

	// @Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		if (null == allBtpBG)
			return;

		int show_start = cur_page_index * cc_page_show_item_count;
		if (null == allBtpBG)
			return;
		Log.d("my","onDraw top:"+this.getPaddingTop());
		canvas.drawBitmap(allBtpBG, this.getPaddingLeft(),
				this.getPaddingTop(), null);
		if (focusFlag)
			canvas.drawBitmap(btpFoucs, this.getPaddingLeft(),
					(int)((this.getPaddingTop() + foucsID * 55)*Resolution.getScaleY()), null);

		if (bSelect) {
			if (total_item_count > 9) {
				if (checkedID >= show_start
						&& checkedID < show_start + cur_page_show_item_count)
					canvas.drawBitmap(btpCheck, this.getPaddingLeft(),
							(int)((this.getPaddingTop() + checkedID
									% cc_page_show_item_count * 55)*Resolution.getScaleY()), null);
			} else {
				canvas.drawBitmap(btpCheck, this.getPaddingLeft(),
						(int)((this.getPaddingTop() + checkedID * 55)*Resolution.getScaleY()), null);// 20101108
			}
		}
		for (int i = 0; i < cur_page_show_item_count; i++) {
			String modifyText = BreakText.breakText(
					selectContext.get(show_start + i), (int)(25*Resolution.getScaleX()), (int)(160*Resolution.getScaleX()));
			if (modifyText != null)
				canvas.drawText(modifyText,(int)(( this.getPaddingLeft() + 128)*Resolution.getScaleX()),
						(int)((this.getPaddingTop() + 55 * i + 45)*Resolution.getScaleY()), paint);
		}

	}

	// @Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_DPAD_UP:
			if (foucsID > 0) {
				foucsID--;
			} else {
				changeToNextPage(true);
			}
			this.invalidate();
			break;
		case KeyEvent.KEYCODE_DPAD_DOWN:
			if (foucsID < cur_page_show_item_count - 1) {
				foucsID++;
			} else {
				changeToNextPage(false);
			}
			this.invalidate();
			break;
		case KeyEvent.KEYCODE_DPAD_LEFT:
			if (selectMenuListener != null) {
				// if(IfSelect)
				// {
				// if(Menucontrol.InitState != null)
				// Menucontrol.InitState.add(SelectContextID.get(CheckedID));
				// }
				// SelectMenuListener.SelectFrameToMenuHandle(CheckedID,"__LEFT__");
				// //wk 110302
			}
			break;
		case KeyEvent.KEYCODE_DPAD_RIGHT:
			if (selectMenuListener != null) {
				// if(IfSelect)
				// {
				// if(Menucontrol.InitState != null)
				// Menucontrol.InitState.add(SelectContextID.get(CheckedID));
				// }
				// SelectMenuListener.SelectFrameToMenuHandle(CheckedID,"__RIGHT__");
				// //wk 110302
			}
			break;
		case KeyEvent.KEYCODE_BACK:
			if (selectMenuListener != null) {
				selectMenuListener.SelectFrameToMenuHandle("__BACK__"); 
			}
			return true;
		case KeyEvent.KEYCODE_DPAD_CENTER:
		case KeyEvent.KEYCODE_ENTER:
			Log.d("my",checkedID+","+foucsID+","+startfocusID);
			if (checkedID != (cur_page_index * cc_page_show_item_count + foucsID)) {
				checkedID = cur_page_index * cc_page_show_item_count + foucsID;
			}
			if (bSelect) {
				if (!bTVSelectFlag)
					if (Menucontrol.initState != null) {
						if (startfocusID != -1) {
							if (startfocusID < Menucontrol.initState.size())
								Menucontrol.initState.remove(startfocusID);
						}
						Menucontrol.initState.add(selectContextID
								.get(checkedID));
						startfocusID = Menucontrol.initState.size() - 1;
					}
			}
			if (selectMenuListener != null) {
				selectMenuListener.SelectFrameToMenuHandle(selectContextID
						.get(checkedID));
			}

			break;
		}
		return super.onKeyDown(keyCode, event);

	}

	public void setSelectFrameListener(AmlogicMenuListener SFL) {
		selectMenuListener = SFL;
	}

	private void setCurPageShowItemCount() {
		if (total_item_count < cc_page_show_item_count)
			cur_page_show_item_count = total_item_count;
		else {
			if (cur_page_index < total_item_count / cc_page_show_item_count)
				cur_page_show_item_count = cc_page_show_item_count;
			else
				cur_page_show_item_count = total_item_count - cur_page_index
						* cc_page_show_item_count;
		}
	}

	private void changeToNextPage(boolean up_key) {
		int max_page_index = (total_item_count - 1) / cc_page_show_item_count;

		if (up_key) {
			if (max_page_index > 0) {
				if (cur_page_index > 0) {
					cur_page_index--;
				} else {
					cur_page_index = max_page_index;
				}
				setCurPageShowItemCount();
				foucsID = cur_page_show_item_count - 1;
			} else {
				foucsID = cur_page_show_item_count - 1;
			}
		} else {
			if (max_page_index > 0) {
				if (cur_page_index < max_page_index) {
					cur_page_index++;
				} else {
					cur_page_index = 0;
				}
				setCurPageShowItemCount();
				foucsID = 0;
			} else {
				foucsID = 0;
			}
		}
	}

	private Bitmap getBGBitmapBG(int item_count) {
		if (item_count< 0||item_count>10)
			return null;
		else{
			return searchID.getBitmap("shortcut_bg_box_"+item_count);
		} 
	}

	private void getBitmapBG() {
		allBtpBG = getBGBitmapBG(selectContext.size());
		if (btpFoucs == null)
			btpFoucs = searchID.getBitmap("shortcut_bg_sel");
		if (btpCheck == null)
			btpCheck = searchID.getBitmap("shortcut_bg_check");
	}

	private int filterIDGetFocus() {
		int focusID = -1;
		if (selectContextID.get(0).contains("shortcut_common_sync_control_")) {
			if (Menucontrol.mMediaType.equals("music")) {
				for (int i = 0; i < selectContextID.size(); i++)
					if (selectContextID.get(i).contains("music"))
						return i;

			} else if (Menucontrol.mMediaType.equals("picture")) {
				for (int i = 0; i < selectContextID.size(); i++)
					if (selectContextID.get(i).contains("picture"))
						return i;
			} else if (Menucontrol.mMediaType.equals("txt")) {
				for (int i = 0; i < selectContextID.size(); i++)
					if (selectContextID.get(i).contains("txt"))
						return i;
			}
		}
		return focusID;
	}
}
// wk 110301 end

