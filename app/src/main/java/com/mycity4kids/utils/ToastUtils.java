package com.mycity4kids.utils;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

/**
 * Class is designed for following purposes- 
 * 1. Immediate display of new toast, even if previous is being displayed.
 * 2. Changing position of all toasts through-out application.
 * 
 * It will work as intended, only if all toasts in application are shown using it.
 */
public class ToastUtils {

	/**
	 * This field will be used if any positive value is assigned to it.
	 */
	private static final int TOAST_MARGIN_BOTTOM_DP = 0;
	
	/**
	 * mSingletonToast will be Singleton in application.
	 */
	private static Toast mSingletonToast;

	/**
	 * Shows the pMessage as an immediate Toast for Toast.LENGTH_LONG duration.
	 * @param pContext
	 * @param pMessage
	 */
	public static void showToast(Context pContext, String pMessage) {
		showToast(pContext, pMessage, Toast.LENGTH_LONG);
	}
	
	/**
	 * Shows the pMessage as an immediate Toast for pDuration.
	 * @param pContext 
	 * @param pMessage
	 * @param pDuration
	 */
	public static void showToast(Context pContext, String pMessage, int pDuration) {
		/**
		 * If pMessage is null or blank, new toast will not be shown.
		 */
		if (pMessage == null || pMessage.trim().length() == 0) {
			return;
		}

		if (pDuration != Toast.LENGTH_LONG && pDuration != Toast.LENGTH_SHORT) {
			pDuration = Toast.LENGTH_LONG;
		}

		if (mSingletonToast == null) {
			mSingletonToast = Toast.makeText(pContext.getApplicationContext(), pMessage, pDuration);
			
			if( TOAST_MARGIN_BOTTOM_DP > 0 ) {
				float density = pContext.getResources().getDisplayMetrics().density;
				int yOffSet = (int) (density * TOAST_MARGIN_BOTTOM_DP);
				mSingletonToast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, 0, yOffSet);
			}
			
		}
		mSingletonToast.setDuration(pDuration);
		mSingletonToast.setText(pMessage);
		mSingletonToast.show();
	}
	
	/**
	 * Removes the currently displaying toast.
	 */
	public static void removeToast() {
		if( mSingletonToast == null) {
			mSingletonToast.cancel();
		}
	}
}
