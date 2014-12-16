package com.lisz.banktraining.util;

import com.lisz.banktraining.BankApplication;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SharedPreferencesUtil {
	
	private static SharedPreferences sharePreferences = PreferenceManager.getDefaultSharedPreferences(BankApplication.getGlobalContext());
	private static SharedPreferences.Editor editor = sharePreferences.edit();
	
	public static void saveBooleanPreference(String modeName,boolean modeValue){
		editor.putBoolean(modeName, modeValue);
		editor.commit();
	}
	
	public static boolean getBooleanPreference(String modeName){
		return sharePreferences.getBoolean(modeName, false);
	}
	
	public static int getIntPreference(String modeName){
		return sharePreferences.getInt(modeName, 1);
	}
	
	public static void saveIntPreference(String modeName, int modeValue){
		editor.putInt(modeName, modeValue);
		editor.commit();
	}
	
	public static boolean firstStartApp(){
		return sharePreferences.getBoolean("firstStartApp", true);
	}
	
	public static void  setFirstStartApp(boolean value){
		editor.putBoolean("firstStartApp", value);
		editor.commit();
	}
}
