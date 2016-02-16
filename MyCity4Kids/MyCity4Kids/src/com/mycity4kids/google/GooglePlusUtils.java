package com.mycity4kids.google;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.mycity4kids.interfaces.IPlusClient;

/**
 * @author Deepanker Chaudhary
 */
public class GooglePlusUtils implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener /*,GoogleApiClient.OnAccessRevokedListener*/ {
    //	private PlusClient mPlusClient;
    private Activity mActivity;
    ConnectionResult mConnectionResult;
    private IPlusClient mPlusClientInterface;
    public static final int REQUEST_CODE_SIGN_IN = 5;
    private GoogleApiClient mPlusClient;
    //  private static final int REQUEST_CODE_GET_GOOGLE_PLAY_SERVICES = 2;

    public static final String SCOPES = "https://www.googleapis.com/auth/plus.login " + " email "
            +"https://www.googleapis.com/auth/drive.file";
    private boolean isGooglePlusConnecting = false;


    public GooglePlusUtils(Activity pActivity, IPlusClient plusClientInterface) {
        mPlusClientInterface = plusClientInterface;
        mActivity = pActivity;
        //	mConnectionResult=new ConnectionResult(0, mActivity.getApplicationContext());
        mPlusClient = new GoogleApiClient.Builder(mActivity, this, this)
                .addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .addScope(Plus.SCOPE_PLUS_PROFILE)
//        .addScope(Scopes.PLUS_ME)
//	    .setActions(MomentUtil.ACTIONS)
                .build();
    }

    public void onStart() {
        mPlusClient.connect();
    }

    /**
     * this method clear goolge session : it's mandatory because if we will not clear it
     * onConnected() will be call & our login hit will go as we come on login page
     */
    public void onStop() {
        //	clearGoogleSession();
        googlePlusSignOut();
    }

    public void googlePlusLogin() {
        int available = GooglePlayServicesUtil.isGooglePlayServicesAvailable(mActivity);
        if (available != ConnectionResult.SUCCESS) {

            return;
        }

        try {
            if (mConnectionResult != null) {
                mConnectionResult.startResolutionForResult(mActivity, REQUEST_CODE_SIGN_IN);
            }
        } catch (IntentSender.SendIntentException e) {
            // Fetch a new result to start.
            mPlusClient.connect();
        }
    }

    private void googlePlusSignOut() {
        if (mPlusClient.isConnected()) {
            mPlusClient.clearDefaultAccountAndReconnect();
//             mPlusClient.disconnect();
//             mPlusClient.connect();

        }
    }

    private void clearGoogleSession() {
        if (mPlusClient.isConnected()) {
            mPlusClient.disconnect();
//             mPlusClient.revokeAccessAndDisconnect(this);
        }
    }

    public void onActivityResult(Activity _activity, int _requestCode, int _resultCode, Intent _data) {
        if (_resultCode == Activity.RESULT_OK && !mPlusClient.isConnected()
                && !mPlusClient.isConnecting()) {
            // This time, connect should succeed.
            isGooglePlusConnecting = true;
            mPlusClient.connect();
        }
    }

    @Override
    public void onConnected(Bundle arg0) {
        isGooglePlusConnecting = false;
        mPlusClientInterface.getGooglePlusInfo(mPlusClient);
    }

    @Override
    public void onConnectionSuspended(int i) {
        mPlusClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        mConnectionResult = result;
        if (isGooglePlusConnecting) {
            isGooglePlusConnecting = false;
            googlePlusLogin();
        } else {
            mPlusClientInterface.onGooglePlusLoginFailed();
        }
    }

//	@Override
//	public void onAccessRevoked(ConnectionResult status) {
//		 if (status.isSuccess()) {
//	        //    mSignInStatus.setText(R.string.revoke_access_status);
//	        } else {
//	         //   mSignInStatus.setText(R.string.revoke_access_error_status);
//	            mPlusClient.disconnect();
//	        }
//	        mPlusClient.connect();
//
//	}

}
