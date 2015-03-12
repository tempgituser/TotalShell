package com.example.totalshell;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SettingState {

	private static SharedPreferences sSharedPreferences;

	public static void init(Context context) {
		sSharedPreferences = context.getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE);
	}

	public static boolean isIncludeSystem(Context t) {
		if (sSharedPreferences == null) {
			init(t);
		}
		boolean includeSystem = sSharedPreferences.getBoolean("isIncludeSystem", false);
		return includeSystem;
	}

	public static void setIsIncludeSystem(Context t, boolean isIncludeSystem) {
		if (sSharedPreferences == null) {
			init(t);
		}
		if (sSharedPreferences != null) {
			Editor editor = sSharedPreferences.edit();
			editor.putBoolean("isIncludeSystem", isIncludeSystem);
			editor.commit();
		}
	}
	
	public static boolean isIncludeService(Context t) {
		if (sSharedPreferences == null) {
			init(t);
		}
		boolean includeSystem = sSharedPreferences.getBoolean("isIncludeService", false);
		return includeSystem;
	}
	
	public static void setIsIncludeService(Context t, boolean isIncludeService) {
		if (sSharedPreferences == null) {
			init(t);
		}
		if (sSharedPreferences != null) {
			Editor editor = sSharedPreferences.edit();
			editor.putBoolean("isIncludeService", isIncludeService);
			editor.commit();
		}
	}
	
	public static boolean isUseRoot(Context t) {
		if (sSharedPreferences == null) {
			init(t);
		}
		boolean isUseRoot = sSharedPreferences.getBoolean("isUseRoot", true);
		return isUseRoot;
	}
	
	public static void setIsUseRoot(Context t, boolean isUseRoot) {
		if (sSharedPreferences == null) {
			init(t);
		}
		if (sSharedPreferences != null) {
			Editor editor = sSharedPreferences.edit();
			editor.putBoolean("isUseRoot", isUseRoot);
			editor.commit();
		}
	}

}
