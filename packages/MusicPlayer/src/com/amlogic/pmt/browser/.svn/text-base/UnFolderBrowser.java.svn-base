package com.amlogic.pmt.browser;

import java.io.File;

import android.content.Context;
import android.os.AsyncTask;

public class UnFolderBrowser {
	public final static int skywbrowser = 1 << 2; // left
	public final static int skywprebrowser = 1 << 3; // right

	private SearchDrawableID SearchDid;
	private String FileType;
	private Context MyContext;
	private String[] PlayFormat;
	private OnUnFolderBrowserListener OnUFBListener;
	private String DeviceName;
	private int browserflag;

	// private LoadDataFromSDCard Loaddata = null;
	public UnFolderBrowser(Context context, String devicename) {
		// FileType = filetype;
		MyContext = context;
		SearchDid = new SearchDrawableID();
		DeviceName = devicename;

	}

	public void SetFileType(String type) {
		FileType = type;
		PlayFormat = MyContext.getResources().getStringArray((SearchDid.PlayFormatID(FileType)));
	}

	public void SetOnUnFolderBrowserListener(OnUnFolderBrowserListener listener) {
		OnUFBListener = listener;
	}

	private boolean checkEndsWithInStrings(String checkItsEnd,
			String[] fileEndings) {

		for (String aEnd : fileEndings) {
			if (checkItsEnd.toLowerCase().endsWith(aEnd))
				return true;
		}
		return false;

	}

	public void StartLoadData(int flag) {
		if (flag == this.skywbrowser || flag == this.skywprebrowser) {
			new LoadDataFromUSB().execute();
			browserflag = flag;
		}

	}

	private class LoadDataFromUSB extends
			AsyncTask<Object, FilebrowserItemData, Object> {

		// @Override
		protected Object doInBackground(Object... params) {
			// String sdcardDirectory =
			// android.os.Environment.getExternalStorageDirectory().toString();

			int filetotal = 0;
			File pathfile = new File(DeviceName);

			if (pathfile.isDirectory()) {
				File filesone[] = pathfile.listFiles(); // one
				if (filesone != null)
				for (File fileone : filesone) {
					if (!fileone.isDirectory()) {
						if (checkEndsWithInStrings(fileone.getName(),
									PlayFormat)) {
							publishProgress(new FilebrowserItemData(fileone.getPath(), SearchDid.SearchID(FileType, true), SearchDid.SearchID(FileType, false)));
							filetotal++;
							if (browserflag == UnFolderBrowser.this.skywprebrowser) {
								if (filetotal == 11)
								return null;
							}

						}

					} else {

						File FilesTwo[] = fileone.listFiles();
						if (FilesTwo != null)
						for (File FileTwo : FilesTwo) {
							if (!FileTwo.isDirectory()) {
								if (checkEndsWithInStrings(FileTwo.getName(), PlayFormat)) {
									filetotal++;
									publishProgress(new FilebrowserItemData(FileTwo.getPath(),SearchDid.SearchID(FileType, true),SearchDid.SearchID(FileType, false)));
									if (browserflag == UnFolderBrowser.this.skywprebrowser) {
										if (filetotal == 11)
										return null;
									}

								} 
							}else {

								File FilesThree[] = FileTwo.listFiles();
								if (FilesThree != null)
								for (File FileThree : FilesThree) {
									if (!FileThree.isDirectory()) {
										if (checkEndsWithInStrings(FileThree.getName(),PlayFormat)) {
											filetotal++;
											publishProgress(new FilebrowserItemData(FileThree.getPath(),SearchDid.SearchID(FileType,true),SearchDid.SearchID(FileType,false)));
											if (browserflag == UnFolderBrowser.this.skywprebrowser) {
												if (filetotal == 11)
												return null;
											}
										}

									} else {

										File FilesFour[] = FileThree.listFiles();
										if (FilesFour != null)
										for (File FileFour : FilesFour) {
											if (!FileFour.isDirectory()) {
												if (checkEndsWithInStrings(FileFour.getName(),PlayFormat)) {filetotal++;
													publishProgress(new FilebrowserItemData(FileFour.getPath(),SearchDid.SearchID(FileType,true),SearchDid.SearchID(FileType,false)));
													if (browserflag == UnFolderBrowser.this.skywprebrowser) {
														if (filetotal == 11)
														return null;
													}
												}

											} else {

												File FilesFive[] = FileFour.listFiles();
												if (FilesFive != null)
												for (File FileFive : FilesFive) {
													if (!FileFive.isDirectory()) {
														if (checkEndsWithInStrings(FileFive.getName(),PlayFormat)) {
															filetotal++;
															publishProgress(new FilebrowserItemData(FileFive.getPath(),SearchDid.SearchID(FileType,true),SearchDid.SearchID(FileType,false)));
															if (browserflag == UnFolderBrowser.this.skywprebrowser) {
																if (filetotal == 11)
																return null;
															}
														}
													}
												}
											}
										}
									}

								}
							}
						}
					}
				}
			}

		

			if (filetotal == 0)
				publishProgress(null);
			return null;

		}

		// @Override
		public void onProgressUpdate(FilebrowserItemData... value) {
			OnUFBListener.OnUnFolderBrowser(value);
		}

		// @Override
		protected void onPostExecute(Object result) {
			OnUFBListener.OnUnFolderBrowserFinish();
		}
	};
}
