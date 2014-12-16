package com.lisz.banktraining.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.lisz.banktraining.BankApplication;

import android.content.Context;
import android.content.res.AssetManager;

public class BankUtility {

	private static final String TAG = "BankUtility";
	private static String databaseName = "bankDB.db";

	public static Context getContext() {
		return BankApplication.getGlobalContext();
	}

	public static AssetManager getAssetManager() {
		return getContext().getAssets();
	}

	public static void copyAssetsDBToData() {
		InputStream is = null;
		FileOutputStream fos = null;
		String databasePath = getContext().getFilesDir().getAbsolutePath();     
	     
	    databasePath = databasePath.substring(0, databasePath.lastIndexOf("/")) + "/databases";
	    String mDatabasePath = databasePath + "/" + databaseName;       
	     
	    File dir = new File(databasePath);
	    if(!dir.exists()){
	        dir.mkdir();
	    }
	     
	    File file = new File(mDatabasePath);
		try {
			is = getContext().getAssets().open(databaseName);
			fos = new FileOutputStream(file);
			byte[] buffer = new byte[1024];
			int count = 0;
			while ((count = is.read(buffer)) > 0) {
				fos.write(buffer, 0, count);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			L.v(TAG, "copyResourcesToDataFile", "copyResourcesToDataFile ");
			closeOutputStream(fos);
			closeInputStream(is);
		}
	}

	public static String configureGetDatabasePath() {
		String filePath = getContext().getFilesDir().getAbsolutePath();

		filePath = filePath.substring(0, filePath.lastIndexOf("/"))
				+ "/databases";
		String mDatabasePath = filePath + "/" + databaseName;
		L.v(TAG, "configureGetDatabasePath:", mDatabasePath);
		return mDatabasePath;
	}

	public static void closeInputStream(InputStream fis) {
		if (null != fis) {
			try {
				fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void closeOutputStream(OutputStream os) {
		if (null != os) {
			try {
				os.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
