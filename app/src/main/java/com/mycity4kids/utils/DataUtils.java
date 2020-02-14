package com.mycity4kids.utils;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.telephony.TelephonyManager;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * This class is intended to have methods, which uses java IO APIs. If any
 * method uses other APIs, that should be added to any other more suitable
 * Utility class. Even android.util.Log is not used in this class.
 */
public class DataUtils {
	/**
	 * @param pInputStream
	 * @return
	 */
	public static byte[] convertStreamToBytes(InputStream pInputStream) {
		int read = 0;
		byte[] data = new byte[1024];
		/** data will be read in chunks */
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			while ((read = pInputStream.read(data)) != -1) {
				baos.write(data, 0, read);
			}
			return baos.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @param pFilePath
	 * @return
	 */
	public static FileInputStream getFileInputStream(String pFilePath) {
		try {
			return new FileInputStream(new File(pFilePath));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @param pFilePath
	 * @return
	 */
	public static byte[] getFileData(String pFilePath) {
		try {
			return convertStreamToBytes(getFileInputStream(pFilePath));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @param Object o
	 * @return byte[]
	 * @description Converting objects to byte arrays
	 */
	static public byte[] object2Bytes(Object o) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(o);
		return baos.toByteArray();
	}

	/**
	 * @param byte raw[]
	 * @return Object
	 * @description Converting byte arrays to objects
	 */
	static public Object bytes2Object(byte raw[]) throws IOException,ClassNotFoundException {
		ByteArrayInputStream bais = new ByteArrayInputStream(raw);
		ObjectInputStream ois = new ObjectInputStream(bais);
		Object o = ois.readObject();
		return o;
	}


	public static String getCalendarUriBase(Activity activity) {

		String calendarUriBase = null;
		Uri calendars = Uri.parse("content://calendar/calendars");
		Cursor managedCursor = null;
		try {
			managedCursor=	activity.getContentResolver().query(calendars, null, null, null, null);
		} catch (Exception e) {
		}
		if (managedCursor != null) {
			calendarUriBase = "content://calendar/";
		} else {
			calendars = Uri.parse("content://com.android.calendar/calendars");
			try {
				managedCursor=	activity.getContentResolver().query(calendars, null, null, null, null);
			} catch (Exception e) {
			}
			if (managedCursor != null) {
				calendarUriBase = "content://com.android.calendar/";
			}
		}
		return calendarUriBase;
	}

	public static String getDeviceId(Activity activity){
		try {
			TelephonyManager telephonyManager = (TelephonyManager)activity.getSystemService(Context.TELEPHONY_SERVICE);
			String deviceId = telephonyManager.getDeviceId();
			return deviceId;
		} catch (Exception e) {
			return null;
		}

	}



}
