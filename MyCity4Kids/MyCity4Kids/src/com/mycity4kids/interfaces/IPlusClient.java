package com.mycity4kids.interfaces;

import com.google.android.gms.common.api.GoogleApiClient;

/**
 * this interface for getting info from google Plus after Login.
 * @author Deepanker Chaudhary
 *
 */
public interface IPlusClient {
	public void getGooglePlusInfo(GoogleApiClient plusClient);
	void onGooglePlusLoginFailed();
}
