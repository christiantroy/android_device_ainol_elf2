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

package org.geometerplus.fbreader.fbreader;

public class VolumeKeyScrollingAction extends FBAction {
	private final boolean myForward ;
	private static int pageCount = 1;
	private int QuickPageCount = 0 ;

	public VolumeKeyScrollingAction(FBReaderApp fbreader, boolean forward) {
		super(fbreader);
		myForward = forward;
	}
	
	public VolumeKeyScrollingAction(FBReaderApp fbreader,boolean forward, int Quickpagecount) {
		super(fbreader);
		myForward = forward;
		QuickPageCount = Quickpagecount;
	}
	
		
	//Override
	public boolean isEnabled() {
		return ScrollingPreferences.Instance().VolumeKeysOption.getValue();
	}

	//Override
	public void run() {
		boolean isInverted = ScrollingPreferences.Instance().InvertVolumeKeysOption.getValue();
		Reader.getTextView().doScrollPage(isInverted ? !myForward : myForward, pageCount);
	}
	
	
	public void run1() {
	
		Reader.getTextView().doScrollPage(myForward, QuickPageCount);
	}
	
	
	
	public static void setPageCount(int count){
		pageCount = (count>0)?count:0;
	}
}
