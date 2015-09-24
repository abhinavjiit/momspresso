package com.kelltontech.persistence;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * @author sachin.gupta
 * Utility class to store/retrieve values in SharedPreferences.
 */
public class SharedPrefsUtils {

    /**
	 * @param pContext
	 * @param pFileName
	 * @param pKey
	 * @param pDefault
	 * @return
	 */
	public static boolean getSharedPrefBoolean(Context pContext, String pFileName, String pKey, boolean pDefault) {
		SharedPreferences _sharedPref = pContext.getSharedPreferences(pFileName, Context.MODE_PRIVATE);
		return _sharedPref.getBoolean(pKey, pDefault);
	}

	/**
	 * @param pContext
	 * @param pFileName
	 * @param pKey
	 * @param pValue
	 */
	public static void setSharedPrefBoolean(Context pContext, String pFileName, String pKey, boolean pValue) {
		SharedPreferences _sharedPref = pContext.getSharedPreferences(pFileName, Context.MODE_PRIVATE);
		Editor 	_editor = _sharedPref.edit();
		_editor.putBoolean(pKey, pValue);
		_editor.commit();
	}

	/**
	 * @param pContext
	 * @param pFileName
	 * @param pKey
	 * @param pDefault
	 * @return
	 */
	public static int getSharedPrefInt(Context pContext, String pFileName, String pKey, int pDefault) {
		SharedPreferences _sharedPref = pContext.getSharedPreferences(pFileName, Context.MODE_PRIVATE);
		return _sharedPref.getInt(pKey, pDefault);
	}

	/**
	 * @param pContext
	 * @param pFileName
	 * @param pKey
	 * @param pValue
	 */
	public static void setSharedPrefInt(Context pContext, String pFileName, String pKey, int pValue ) {
		SharedPreferences _sharedPref = pContext.getSharedPreferences(pFileName, Context.MODE_PRIVATE);
		Editor 	_editor = _sharedPref.edit();
		_editor.putInt(pKey, pValue);
		_editor.commit();
	}

	/**
	 * @param pContext
	 * @param pFileName
	 * @param pKey
	 * @param pDefault
	 * @return
	 */
	public static String getSharedPrefString(Context pContext, String pFileName, String pKey, String pDefault) {
		SharedPreferences _sharedPref = pContext.getSharedPreferences(pFileName, Context.MODE_PRIVATE);
		return _sharedPref.getString(pKey, pDefault);
	}

	/**
	 * @param pContext
	 * @param pFileName
	 * @param pKey
	 * @param pValue
	 */
	public static void setSharedPrefString(Context pContext, String pFileName, String pKey, String pValue ) {
		SharedPreferences _sharedPref = pContext.getSharedPreferences(pFileName, Context.MODE_PRIVATE);
		Editor 	_editor = _sharedPref.edit();
		_editor.putString(pKey, pValue);
		_editor.commit();
	}
}
