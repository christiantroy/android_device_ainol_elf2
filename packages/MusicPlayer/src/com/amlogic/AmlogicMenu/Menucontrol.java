package com.amlogic.AmlogicMenu;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AbsoluteLayout;

import com.amlogic.pmt.MiscUtil;
import com.amlogic.serialport.Iuartservice;
import com.amlogic.DynamicBoxData;
import com.amlogic.InitMenuState;
import com.amlogic.MenuDataBase;
import com.amlogic.MenuUIOp;
import com.amlogic.Listener.MenuCallbackListener;
import com.amlogic.Listener.AmlogicMenuListener;
import com.amlogic.View.MenuGroup1;
import com.amlogic.XmlParse.SCMenuItem;
import com.amlogic.XmlParse.SCMenuManager;
import com.amlogic.XmlParse.StringItem;
import com.amlogic.XmlParse.StringManager;
import com.amlogic.XmlParse.TreeNode;

@SuppressWarnings("deprecation")
public class Menucontrol extends AbsoluteLayout implements AmlogicMenuListener {
	private static final String TAG = "Menucontrol";
	private String entrance;
	private ArrayList<String> selectContext, selectContextID;
	private SourceData sourceData;
	private InitMenuState initmenustate;
	private List<MenuCallbackListener> menulistenerList;
	public static ArrayList<String> initState;
	public List<StringItem> xmlStringItem;
	private DynamicBoxData dynamicData = null;
	public static String mMediaType = null;
	private MenuUIOp menuUIOp = null;
	private int menuL1FocusIndex = 0;
	private int menuFocusId = 0;
	private MenuDataBase mdb = null;

	public Menucontrol(Context context, AttributeSet attrs, String enter,
			ArrayList<String> initstate) {
		super(context, attrs);
		entrance = enter;
		initState = initstate;
		mdb = new MenuDataBase(context);

		InitData();
		setMenuData();
		showMenu();
	}

	// #ifdef multak ..........................
	public Menucontrol(Context context, AttributeSet attrs) {
		super(context, attrs);

		initmenustate = new InitMenuState();
		this.getContext().startService(mIntent);
		this.getContext().bindService(mIntent, mConnection,
				Context.BIND_AUTO_CREATE);
	}

	// Menu[0] xml name
	// Menu[1] if have value
	public void uartsend(String... Menu) {
		initmenustate.UartSendAndSaveParam(null, Menu);
	}

	public void uartSendVolumn(String state) {
		if (mIuartservice != null)
			try {
				mIuartservice.UartSend("volumn", state);
				mIuartservice.SaveParam("Volumn", state);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
	}

	public int getSFValue(String MenuItemName) {
		return initmenustate.InitSelectFrameValue(MenuItemName, null);
	}

	public String getBarValue(String MenuItemName) {
		return initmenustate.InitParamValue(MenuItemName);
	}

	// rembemer to unbindservice when you exit your application
	// #endif .................................

	// ////////////////////////////////////////////////////////////////////////
	// PicSoundDisplayModeSource
	public void SelectFrameShortCut(String item) {
		if (Menucontrol.this.getVisibility() == View.VISIBLE
				&& menuUIOp.getMGInstance().getAbsoluteFoucsItem().equals(item)
				&& menuUIOp.getSFInstance().getFocusFlag()) {
			menuUIOp.getSFInstance().onKeyDown(
					KeyEvent.KEYCODE_DPAD_DOWN,
					new KeyEvent(KeyEvent.ACTION_DOWN,
							KeyEvent.KEYCODE_DPAD_DOWN));
		} else {
			menuUIOp.HideSelectFrame();

			postUpdateShortCutMassage(item);
		}
	}

	String initVolumn = null;

	public void setInitVolumn(String value) {
		initVolumn = value;
	}

	public void ShowVolumeBarShortCut() {
		// int
		// absoluteID=SourceData.GetAbsoluteIDFromXML("shortcut_common_vol_");
		int absoluteID = menuUIOp.getMGInstance().GetFocusID(
				"shortcut_common_vol_");
		if (absoluteID < 0)
			return;
		menuUIOp.RsfSmg();
		if (Menucontrol.this.getVisibility() == View.INVISIBLE)
			Menucontrol.this.setVisibility(View.VISIBLE);
		menuUIOp.getMGInstance().setFocusID(absoluteID);
		ShowProgressBar("shortcut_common_vol_");
	}

	public void handleSpecialKey(int key) {
		int focus = menuUIOp.getMGInstance().HandleSpecialKeyID(key);
		if (focus != -1) {
			menuUIOp.RpbvSmg();
			menuUIOp.RsfSmg();
			menuUIOp.getMGInstance().setFocusID(focus);
			menuUIOp.getMGInstance().myinvalidate();
			menuUIOp.getMGInstance().requestFocus();
			menuUIOp.getMGInstance().onKeyDown(KeyEvent.KEYCODE_ENTER,
					new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER));
			Log.d("Menucontrol", ">>>>>>KEYCODE_ENTER 1");
			/*if (Menucontrol.this.getVisibility() == View.INVISIBLE)*/ {
				Log.d("Menucontrol", ">>>>>>KEYCODE_ENTER 2, set VISIBLE");
				Menucontrol.this.setVisibility(View.VISIBLE);
			}
		}
	}
	
	public void handleOnClick(int focus) {
		if (focus != -1) {
			menuUIOp.RpbvSmg();
			menuUIOp.RsfSmg();
			menuUIOp.getMGInstance().setFocusID(focus);
			menuUIOp.getMGInstance().myinvalidate();
			menuUIOp.getMGInstance().requestFocus();
			menuUIOp.getMGInstance().onKeyDown(KeyEvent.KEYCODE_ENTER,
					new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER));
			Log.d("Menucontrol", ">>>>>>KEYCODE_ENTER 1");
			/*if (Menucontrol.this.getVisibility() == View.INVISIBLE)*/ {
				Log.d("Menucontrol", ">>>>>>KEYCODE_ENTER 2, set VISIBLE");
				Menucontrol.this.setVisibility(View.VISIBLE);
			}
		}
	}

	// ////////////////////////////////////////////////////////////////////////
	public void setMenuCallbackListener(MenuCallbackListener listener) {
		// Menulistener = listener;
		addListener(listener);
	}

	public void cleanListener() {
		menulistenerList = null;
	}

	public void addListener(MenuCallbackListener listener) {
		if (menulistenerList != null)
			menulistenerList.add(listener);
		else {
			menulistenerList = new ArrayList<MenuCallbackListener>();
			menulistenerList.add(listener);
		}
	}

	public String getPlayType() {
		return mMediaType;
	}

	public void CheckedMenuHandle(int keyCode, int focusId, String menuItemName) {
		Log.d(TAG, "key:" + keyCode);
		String UIType = sourceData.GetUITypeFromXML(menuItemName);
		if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
			if (UIType.equals("SelectBox"))
				keyCode = KeyEvent.KEYCODE_ENTER;
			else
				return;
		}

		menuFocusId = focusId;
		if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT
				|| keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
			menuUIOp.HideSelectFrame();
			if (UIType.equals("SelectBox") || UIType.equals("SelectBoxCommand")
					|| UIType.equals("DynamicBox"))
				postUpdateSFMessage(focusId, menuItemName, UIType, 300);

		} else {
			if (keyCode == KeyEvent.KEYCODE_ENTER
					|| keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
				if (menuItemName.equals("shortcut_setup_audio_equalizer_")) {
					menuUIOp.showBalancer(mdb.InitBalancer());
					return;
				} else if (UIType.equals("StatusCommand")) {
					HandleMenuCommand(menuItemName);
				} else if (UIType.equals("SelectBox")
						|| UIType.equals("SelectBoxCommand")
						|| UIType.equals("DynamicBox")) {
					if (menuUIOp.getSFInstance().getVisibility() == View.VISIBLE)
						menuUIOp.setSFFocus();
					else {
						updateSF(menuItemName, UIType);
						if (menuUIOp.getSFInstance().getVisibility() == View.VISIBLE)
							menuUIOp.setSFFocus();
					}
				} else if (UIType.equals("Command")) {
					String Id = sourceData.GetAnotherStatusID(menuItemName);
					if (Id != null) {
						menuUIOp.getMGInstance().UpdataStatus(Id);
						HandleMenuCommand(Id);
					} else {
						HandleMenuCommand(menuItemName);
					}
				} else if (UIType.equals("SliderBar")) {
					if (entrance.equals("Xunlei") || entrance.equals("Voole")
							|| entrance.equals("Local")) {
						Menucontrol.this.setVisibility(View.INVISIBLE);
						Menucontrol.this.set_focus(false);
						UartSendOsdOnOff("0");
						initmenustate.UartSendVolumeOsd("2");
					} else
						ShowProgressBar(menuItemName);
				} else if (UIType.equals("BalanceBar")) {
					// ɫ�� ��������
					if (menuItemName.equals("shortcut_setup_video_hue_")) {
						// do ɫ��
					} else if (menuItemName
							.equals("shortcut_program_vol_correct_")) {
						// do ��������
					}
				} else if (UIType.equals("Dialog")) {
					// �ָ���������
					if (menuItemName.equals("shortcut_setup_sys_recovery_")) {
						menuUIOp.showDialog();
						return;
					}

					// ������
				} else if (UIType.equals("TunerDialog")) {
					// �ֶ���̨ ΢����̨ �Զ���̨
					if (menuItemName.equals("shortcut_program_manual_search_")) {
						// do manual search
					} else if (menuItemName.equals("shortcut_program_fine_")) {
						// do ΢����̨
					} else if (menuItemName
							.equals("shortcut_program_auto_search_")) {
						// do autosearch
					}

				} else if (UIType.equals("ProgramEditDialog")) {
					// do Ƶ���༭
				} else if (UIType.equals("DynamicBox")) {
					// Ƶ����
				} else if (UIType.equals("Switch")) {
					if (menuItemName.equals("shortcut_video_3d_setup_")) {

						// MenuL1FocusIndex =
						// menuUIOp.getMGInstance().getAbsoluteFoucsID();
						// SetMenuShowState("Setup3D");
						// menuUIOp.ShowL2Menu();
						try {
							Menucontrol.this.setVisibility(View.INVISIBLE);
							Menucontrol.this.set_focus(false);
							// 2d osd off
							UartSendOsdOnOff("0");
							// 3d osd on
							UartSendOsdOnOff(String
									.valueOf((0x88 << 8) & 0xFFff));
						} catch (Exception e) {
							e.printStackTrace();
						}
					} else
						SetMenuShowState("program");
				}
			}
		}
	}

	public void BackMenuHandle(String showState) {
		if (showState.equals("Setup3D")) {
			menuUIOp.HideSelectFrame();
			SetMenuShowState(initmenustate.GetInitType(entrance));
			menuUIOp.BackToL1Menu(menuL1FocusIndex);
		}
	}

	public void SelectFrameToMenuHandle(String ID) {Log.d("my","id "+ID);
		if (ID.equals("__RIGHT__")) {
			menuUIOp.RsfSmg(KeyEvent.KEYCODE_DPAD_RIGHT);
		} else if (ID.equals("__LEFT__")) {
			menuUIOp.RsfSmg(KeyEvent.KEYCODE_DPAD_LEFT);
		} else if (ID.equals("__BACK__")) {
			menuUIOp.HideSelectFrame();
			menuUIOp.getMGInstance().setFocusable(true);
			menuUIOp.getMGInstance().requestFocus();
		} else if (ID.equals("shortcut_common_sync_control_music")
				|| ID.equals("shortcut_common_sync_control_picture")
				|| ID.equals("shortcut_common_sync_control_txt")) {
			String mediaType = null;
			String Svalue = sourceData.GetValueFromXML(ID);
			mediaType = getChinaString(xmlStringItem, "MUSIC");
			if (Svalue.equals(mediaType)) {
				SetMenuShowState("music");
				mMediaType = "music";
			} else {
				mediaType = getChinaString(xmlStringItem, "PICTURE");
				if (Svalue.equals(mediaType)) {
					SetMenuShowState("picture");
					mMediaType = "picture";
				} else {
					mediaType = getChinaString(xmlStringItem, "TEXT");
					if (Svalue.equals(mediaType)) {
						SetMenuShowState("txt");
						mMediaType = "txt";
					}
				}
			}
			menuUIOp.getMGInstance().initFocusID();
			menuUIOp.RsfSmg();
			HandleMenuCommand(ID);
		} else {
			HandleMenuCommand(ID);
			// menuUIOp.HideSelectFrame();
			// menuUIOp.getMGInstance().setFocusable(true);
			// menuUIOp.getMGInstance().requestFocus();
			//Do not hide the select frame while zooming
			if (!ID.contains("shortcut_common_zoom"))
				menuUIOp.RsfSmg();

		}

	}

	// ������slidebar���淵�ز˵������������
	public void ProgressBarToMenuHandle(int progress, boolean isChanged,
			String MenuItemName) {
		// ���ֵ����ı�(+,-)
		if (isChanged) {
			HandleMenuCommand(MenuItemName, progress + "");
		} else {
			menuUIOp.RpbvSmg();
		}

	}

	// ��ʾ�����˵�
	private void ShowSelectFrame(int Focus, int initnum, boolean ifselect) {
		Log.d(TAG, ">>>ShowSelectFrame: initnum "+initnum+", ifselect "+ifselect);
		menuUIOp.showSelectFrame(Focus, initnum, ifselect, selectContext,
				selectContextID);
	}

	private void ShowProgressBar(String MenuItemName) {
		String initnum = mdb.InitParamValue(MenuItemName);
		if (initnum.equals("") && MenuItemName.equals("shortcut_common_vol_"))
			initnum = initVolumn;
		menuUIOp.showProgressBar(MenuItemName, initnum);
	}

	private void HandleMenuCommand(String... Menu) {
		for (int i = 0; i < menulistenerList.size(); i++) {
			if (mMediaType.equals("music"))
				menulistenerList.get(i).CallbackMenuState("Audio", Menu[0]);
			else if (mMediaType.equals("picture")){
				
				menulistenerList.get(i).CallbackMenuState("Picture", Menu[0]);
			}
			else if (mMediaType.equals("txt"))
				menulistenerList.get(i).CallbackMenuState("Text", Menu[0]);
			else
				menulistenerList.get(i).CallbackMenuState(Menu);

			if (Menu[0].contains("shortcut_common_source")) {
				menulistenerList.get(i).CallbackMenuState("BackTo3D");
			}
		}

		initmenustate.UartSendAndSaveParam(mMediaType, Menu);
	}

	// .....................start uartservice process.........................
	private Intent mIntent = new Intent("com.amlogic.serialport.UartService");
	private Iuartservice mIuartservice = null;
	private ServiceConnection mConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder service) {
			Log.d("uart", "......Menucontrol onServiceConnected start......\n");
			mIuartservice = Iuartservice.Stub.asInterface(service);
			initmenustate.setIuartService(mIuartservice);
			Log.d("uart", "......Menucontrol onServiceConnected end......\n");
		}

		public void onServiceDisconnected(ComponentName className) {
			Log.d("uart", "......Menucontrol onServiceDisconnected .........\n");
			mIuartservice = null;
		}
	};

	public String GetSelectFrameState(String ItemName) {

		if (mdb.GetUcpInstance() != null) {
			if (ItemName.equals("shortcut_setup_sys_woofer_switch_"))
				return mdb.GetUcpInstance().getParams("SubwooferSwitch");
			else if (ItemName.equals("shortcut_setup_audio_srs_"))
				return mdb.GetUcpInstance().getParams("SRS");
		}
		return null;
	}

	// SerialPort.UartSendJ(0x9a, 1);
	public String GetMac() {
		if (mdb.GetUcpInstance() != null)
			return mdb.GetUcpInstance().getParams("MAC");
		else
			return null;
	}

	public void UartSendVideoPlayState(String state) {
		if (mIuartservice != null)
			try {
				mIuartservice.UartSend("playerstate", state);
			} catch (RemoteException e) {

				e.printStackTrace();
			}// "0","1"
	}

	public void UartSendOsdOnOff(String state) {
		if (mIuartservice != null)
			try {
				mIuartservice.UartSend("osdonoff", state);
			} catch (RemoteException e) {

				e.printStackTrace();
			}// "0","1"
	}

	public String GetVideoPlaymode() {
		return mdb.Getvideoplaymode();
	}

	public Iuartservice GetIuartService() {
		return mIuartservice;
	}

	boolean unbindFlag = true;

	public void CallMenucontrolunbindservice() {
		if (unbindFlag) {
			unbindFlag = false;
			this.getContext().unbindService(mConnection);
			Log.d("uart",
					"........ call Menucontrol unbindService(mConnection) .........\n");
		}
	}

	// �����
	public void BalancerKeyListener(boolean doit, int high, byte low, int[] a) {

		if (doit) {
			initmenustate.BalancerKeyProcess(doit, high, low, a);
		} else {
			initmenustate.BalancerKeyProcess(doit, high, low, a);
			menuUIOp.RbalancerSmg();
		}

	}

	public void BalancerKeyListener(int soundmode) {

		initmenustate.BalancerKeyProcess(soundmode);
	}

	public void DialogManage(boolean doit) {
		if (doit) {
			new Thread(new Runnable() {
				public void run() {
					initmenustate.DialogSetDefault();
				}
			}).start();

		}
		menuUIOp.RdialogSmg();
	}

	// ........................end uartservice
	// process.............................

	// 设置菜单显示的类型，如果是关联状态，也要设置当前到底显示在哪个播放状态
	private void SetMenuShowState(String state) {
		menuUIOp.getMGInstance().SetShowState(state);
	}

	private String GetMenuShowState() {
		return menuUIOp.getMGInstance().GetShowState();
	}

	public void updatamenu(Map<Integer, String> map) {
		// MG.UpdataMenuData(map);
		menuUIOp.getMGInstance().UpdataMenuData(map);

	}

	// public void updatamenu(String type,Map<Integer,String> map)
	// {
	// // MG.UpdataMenuData(map);
	// menuUIOp.getMGInstance().UpdataMenuData(type,map);
	//
	// }

	public void setVisibility(int visibility) {
		if (visibility == View.INVISIBLE)
			hideAllSubMenu();
		super.setVisibility(visibility);
	}

	public void hideAllSubMenu() {
		menuUIOp.RbalancerSmg();
		menuUIOp.RdialogSmg();
		menuUIOp.RpbvSmg();
		menuUIOp.RsfSmg();
	}

	private void InitData() {

		menulistenerList = new ArrayList<MenuCallbackListener>();
		selectContext = new ArrayList<String>();
		selectContextID = new ArrayList<String>();

		sourceData = new SourceData();
		initmenustate = new InitMenuState();
		// this.getContext().startService(mIntent);
		this.getContext().bindService(mIntent, mConnection,
				Context.BIND_AUTO_CREATE);

		sourceData.InitXMLData();
		sourceData.getXmlString();
		dynamicData = new DynamicBoxData();
		menuUIOp = new MenuUIOp(this, entrance);

	}

	public void set_focus(Boolean b_focus) {
		if (null != menuUIOp)
			menuUIOp.setMGFocus(b_focus);
		this.setFocusable(b_focus);
	}

	// ***********************************************************************
	public void set_play_name(String name) {
		// MG.set_play_name(name);
		menuUIOp.getMGInstance().set_play_name(name);
	}

	public void set_seek_bar_info(int i_total, int i_cur_pos) {
		// MG.set_seek_bar_info(i_total, i_cur_pos);
		menuUIOp.getMGInstance().set_seek_bar_info(i_total, i_cur_pos);
	}

	public void set_play_name(String type, String name) {
		// MG.set_play_name(type,name);
		menuUIOp.getMGInstance().set_play_name(type, name);
	}

	public void set_seek_bar_info(String type, int i_total, int i_cur_pos) {
		// MG.set_seek_bar_info(type,i_total, i_cur_pos);
		menuUIOp.getMGInstance().set_seek_bar_info(type, i_total, i_cur_pos);
	}

	public void setplayerPosScale(String type, String myscale) {
		// MG.setplayerPosScale(type, myscale);
		menuUIOp.getMGInstance().setplayerPosScale(type, myscale);
	}

	public void setplayerPosScale(String myscale) {
		// MG.setplayerPosScale(type, myscale);
		menuUIOp.getMGInstance().setplayerPosScale(myscale);
	}

	public void setDynamicsubtitle(ArrayList<String> data) {
		dynamicData.setsubtitle(data);
	}

	public void setDynamicsoundtrack(ArrayList<String> data) {
		dynamicData.setsoundtrack(data);
	}

	private String getChinaString(List<StringItem> stringItems, String name) {
		String value = null;
		for (StringItem si : stringItems) {
			if (si.name.equals(name))
				value = si.value;
		}
		return value;
	}

	// ************************************************************

	// *****************************Menu
	// Handle*************************************

	private void setMenuData() {
		sourceData.GetBarData();
		SetMenuShowState(initmenustate.GetInitType(entrance));
		menuUIOp.getMGInstance().notifydataFinish();
	}

	private void showMenu() {
		menuUIOp.showMenu();
	}

	public MenuGroup1 getMenuIns() {
		return menuUIOp.getMGInstance();
	}

	// *****************************Menu Handle
	// finish*****************************

	class SourceData {
		private static final String TAG = "mc";
		private TreeNode<SCMenuItem> root;

		// ��XML�н���������Ҫ�����
		public void InitXMLData() {
			Log.d(TAG, "InitXMLData");
			Context friendContext;
			try {
				friendContext = Menucontrol.this.getContext();// .createPackageContext("com.amlogic.ui.res",Context.CONTEXT_IGNORE_SECURITY);
				AssetManager assets = friendContext.getAssets();

				InputStream im = assets.open("shortcutmenutree.xml");
				Log.d(TAG, "im" + im);
				SCMenuManager manager = new SCMenuManager(im, entrance);
				root = manager.getMenuRoot();
				im.close();

			} catch (IOException e) {
				Log.d(TAG, "e" + e);
				// } catch (NameNotFoundException e1) {
				// Log.d(TAG,"e1"+e1);
			}
		}

		public void getXmlString() {
			try {
				Context friendContext = Menucontrol.this.getContext()
						.createPackageContext("com.amlogic.ui.res",
								Context.CONTEXT_IGNORE_SECURITY);
				AssetManager assets = friendContext.getAssets();
				try {
					InputStream is = assets.open("strings.xml");
					StringManager smanager = new StringManager(is);
					xmlStringItem = smanager.getStringItems();
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
		}

		private void GetBarData() {

			Log.d(TAG, "root" + root);
			for (int i = 0; i < root.getChildren().size(); i++) {
				TreeNode<SCMenuItem> node = root.getChildren().get(i);
				if (node.getData().id.equals("TxtPlayControl"))
					mMediaType = "txt";
				else if (node.getData().id.equals("PicturePlayControl"))
					mMediaType = "picture";
				else if (node.getData().id.equals("MusicPlayControl"))
					mMediaType = "music";

				else if (node.getData().id.equals("TVControl"))
					mMediaType = "tv_set";
				else if (node.getData().id.equals("ProgramControl"))
					mMediaType = "program";
				else if (node.getData().id.equals("Setup3DControl"))
					mMediaType = "Setup3D";
				else
					mMediaType = "defult";

				for (int j = 0; j < node.getChildren().size(); j++) {

					String UIType = node.getChildren().get(j).getData().uiType;

					if (!(UIType.equals("StatusCommand"))) {
						menuUIOp.getMGInstance()
								.AddMenuItem(
										new String(node.getChildren().get(j)
												.getData().id), mMediaType);
					} else if (node.getChildren().get(j).getChildren().size() != 0) {
						if (Menucontrol.initState != null)
							for (int ii = 0; ii < Menucontrol.initState.size(); ii++) {
								for (int jj = 0; jj < node.getChildren().get(j)
										.getChildren().size(); jj++) {
									if (node.getChildren().get(j).getChildren()
											.get(jj).getData().id
											.equals(Menucontrol.initState
													.get(ii))) {
										menuUIOp.getMGInstance().AddMenuItem(
												new String(node.getChildren()
														.get(j).getChildren()
														.get(jj).getData().id),
												mMediaType);
										ii = Menucontrol.initState.size();
										break;
									}
								}
							}
					}
				}
			}
			if (entrance.equals("MusPT") || entrance.equals("MusPic")
					|| entrance.equals("MusT"))
				mMediaType = "music";
			else if (entrance.equals("PicT"))
				mMediaType = "picture";

		}

		public String GetValueFromXML(String ID) {
			String Svalue = null;
			for (int i = 0; i < root.getChildren().size(); i++) {
				TreeNode<SCMenuItem> node = root.getChildren().get(i);
				for (int j = 0; j < node.getChildren().size(); j++) {

					for (int jj = 0; jj < node.getChildren().get(j)
							.getChildren().size(); jj++) {
						if (node.getChildren().get(j).getChildren().get(jj)
								.getData().id.equals(ID)) {

							Svalue = node.getChildren().get(j).getChildren()
									.get(jj).getData().value;
							j = node.getChildren().size();
							i = root.getChildren().size();
							break;
						}
					}
				}
			}
			return Svalue;
		}

		public String GetUITypeFromXML(String ID) {
			String UIType = null;
			for (int i = 0; i < root.getChildren().size(); i++) {
				TreeNode<SCMenuItem> node = root.getChildren().get(i);
				for (int j = 0; j < node.getChildren().size(); j++) {

					if (node.getChildren().get(j).getData().id.equals("status")) {
						for (int jj = 0; jj < node.getChildren().get(j)
								.getChildren().size(); jj++) {
							if (node.getChildren().get(j).getChildren().get(jj)
									.getData().id.equals(ID)) {
								UIType = node.getChildren().get(j)
										.getChildren().get(jj).getData().uiType;
								j = node.getChildren().size();
								i = root.getChildren().size();
								break;
							}
						}

					} else if (node.getChildren().get(j).getData().id
							.equals(ID)) {
						UIType = node.getChildren().get(j).getData().uiType;
						i = root.getChildren().size();
						break;
					}
				}
			}
			return UIType;
		}

		public int GetAbsoluteIDFromXML(String ID) {
			int AbsoluteID = -1;
			for (int i = 0; i < root.getChildren().size(); i++) {
				TreeNode<SCMenuItem> node = root.getChildren().get(i);
				for (int j = 0; j < node.getChildren().size(); j++) {
					if (node.getChildren().get(j).getData().id.equals(ID)) {
						AbsoluteID = j;
						i = root.getChildren().size();
						break;
					}
				}
			}
			return AbsoluteID;
		}

		public void SetSelectBox(String ID) {
			String str;
			for (int i = 0; i < root.getChildren().size(); i++) {
				TreeNode<SCMenuItem> node = root.getChildren().get(i);

				if (mMediaType.equals("txt") || mMediaType.equals("picture")
						|| mMediaType.equals("music")) {
					if (!((node.getData().id.equals("TxtPlayControl") && mMediaType
							.equals("txt"))
							|| (node.getData().id.equals("PicturePlayControl") && mMediaType
									.equals("picture")) || (node.getData().id
							.equals("MusicPlayControl") && mMediaType
							.equals("music")))) {
						continue;
					}
				}

				for (int j = 0; j < node.getChildren().size(); j++) {

					if (node.getChildren().get(j).getData().id.equals(ID)) {
						selectContext.clear();
						selectContextID.clear();
						for (int jj = 0; jj < node.getChildren().get(j)
								.getChildren().size(); jj++) {
							selectContextID.add(node.getChildren().get(j)
									.getChildren().get(jj).getData().id);
							str = MiscUtil.getStringByID(Menucontrol.this.getContext(), node.getChildren().get(j)
									.getChildren().get(jj).getData().id);
							if (str == null)
								selectContext.add(node.getChildren().get(j)
										.getChildren().get(jj).getData().value);
							else
								selectContext.add(str);
						}
						i = root.getChildren().size();
						break;
					}
				}
			}
		}

		public String GetAnotherStatusID(String ID) {
			String Svalue = null;
			for (int i = 0; i < root.getChildren().size(); i++) {
				TreeNode<SCMenuItem> node = root.getChildren().get(i);
				for (int j = 0; j < node.getChildren().size(); j++) {
					if (node.getChildren().get(j).getData().id.equals("status"))
						for (int jj = 0; jj < node.getChildren().get(j)
								.getChildren().size(); jj++) {
							if (node.getChildren().get(j).getChildren().get(jj)
									.getData().id.equals(ID)) {

								if (jj == (node.getChildren().get(j)
										.getChildren().size() - 1))
									Svalue = node.getChildren().get(j)
											.getChildren().get(0).getData().id;
								else
									Svalue = node.getChildren().get(j)
											.getChildren().get(jj + 1)
											.getData().id;
								j = node.getChildren().size();
								i = root.getChildren().size();
								break;
							}
						}
				}
			}
			return Svalue;
		}
	}

	private void postUpdateSFMessage(int FocusID, String MenuItemName,
			String UIType, int timer) {
		handlerUpdateSF.removeMessages(1);
		Message msg = handlerUpdateSF.obtainMessage(1);
		msg.arg1 = FocusID;
		Bundle bundle = new Bundle();
		bundle.putString("MenuItemName", MenuItemName);
		bundle.putString("UIType", UIType);
		msg.setData(bundle);
		handlerUpdateSF.sendMessageDelayed(msg, timer);
	}

	private Handler handlerUpdateSF = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				if (menuFocusId == msg.arg1) {
					Bundle bundle = msg.getData();
					String MenuItemName = bundle.getString("MenuItemName");
					String UIType = bundle.getString("UIType");
					updateSF(MenuItemName, UIType);

				}
				break;
			case 2:
				String item = (String) msg.obj;
				sourceData.SetSelectBox(item);
				int absoluteID = menuUIOp.getMGInstance().GetFocusID(item);
				if (absoluteID < 0)
					return;
				// for progressbar exist case
				menuUIOp.RpbvSmg();
				menuUIOp.getMGInstance().setFocusID(absoluteID);
				int initnum = mdb.InitSelectFrameValue(item, mMediaType);
				menuUIOp.getMGInstance().getLeftOffSet();
				ShowSelectFrame(menuUIOp.getMGInstance().getRelativeFoucsID(),
						initnum, true);
				if (Menucontrol.this.getVisibility() == View.INVISIBLE) {
					Menucontrol.this.setVisibility(View.VISIBLE);
					if (entrance.equals("Xunlei") || entrance.equals("Voole")
							|| entrance.equals("Local"))
						UartSendOsdOnOff(String.valueOf((0x80 << 8) & 0xFFff));
				}
				menuUIOp.getMGInstance().myinvalidate();
				menuUIOp.setSFFocus();
				break;
			default:
				break;
			}
		}
	};

	private void updateSF(String MenuItemName, String UIType) {
		if (UIType.equals("SelectBox")) {
			sourceData.SetSelectBox(MenuItemName);
			int initnum = mdb.InitSelectFrameValue(MenuItemName, mMediaType);
			ShowSelectFrame(menuFocusId, initnum, true);
		} else if (UIType.equals("SelectBoxCommand")) {
			sourceData.SetSelectBox(MenuItemName);
			ShowSelectFrame(menuFocusId, -1, false);
		} else if (UIType.equals("DynamicBox")) {
			ArrayList<String> Data = null;
			if (MenuItemName.equals("shortcut_video_subtitle_")) {
				Data = dynamicData.getsubtitle();
			} else if (MenuItemName.equals("shortcut_video_soundtrack_")) {
				Data = dynamicData.getsoundtrack();
			}

			if (Data != null && Data.size() != 0) {
				selectContext.clear();
				selectContextID.clear();
				selectContext = new ArrayList<String>(Data);
				selectContextID = new ArrayList<String>(Data);
				ShowSelectFrame(menuFocusId, -1, true);
			} else
				menuUIOp.HideSelectFrame();
		} else
			menuUIOp.HideSelectFrame();
	}

	private void postUpdateShortCutMassage(String item) {
		handlerUpdateSF.removeMessages(1);
		Message msg = handlerUpdateSF.obtainMessage(2);
		msg.obj = item;
		handlerUpdateSF.sendMessageDelayed(msg, 250);
	}

}