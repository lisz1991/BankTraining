package com.lisz.banktraining;

import com.lisz.banktraining.util.BankUtility;
import com.lisz.banktraining.util.Constent;
import com.lisz.banktraining.util.SharedPreferencesUtil;

import android.app.Application;
import android.content.Context;

public class BankApplication extends Application {
	
	private static Context mContext = null;
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		mContext = this;
		if(SharedPreferencesUtil.firstStartApp()){
			BankUtility.copyAssetsDBToData();
			initSetting();
			SharedPreferencesUtil.setFirstStartApp(false);
		}
	}
	
	public static Context getGlobalContext() {
		return mContext;
	}
	
	private static void initSetting(){
		SharedPreferencesUtil.saveBooleanPreference(
				Constent.SINGLECHOICE, true);
		SharedPreferencesUtil.saveBooleanPreference(
				Constent.MUTIPLECHOICE, true);
		SharedPreferencesUtil.saveBooleanPreference(Constent.JUDGE,
				true);
		SharedPreferencesUtil.saveBooleanPreference(
				Constent.AUTO_SAVEEROR_QUES, true);
	}
	
}
