/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mycity4kids.google;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.mycity4kids.R;

public class SignInActivity extends Activity implements OnClickListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener/*, ResultCallback<People.LoadPeopleResult>*/ {

    private static final int DIALOG_GET_GOOGLE_PLAY_SERVICES = 1;

    private static final int REQUEST_CODE_SIGN_IN = 1;
    private static final int REQUEST_CODE_GET_GOOGLE_PLAY_SERVICES = 2;

    private TextView mSignInStatus;
    private GoogleApiClient mPlusClient;
    private SignInButton mSignInButton;
    private View mSignOutButton;
    private View mRevokeAccessButton;
    private ConnectionResult mConnectionResult;
    private boolean mIntentInProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.google_sign_in);
        mPlusClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .addScope(Plus.SCOPE_PLUS_PROFILE)
                .build();

//        mPlusClient = new GoogleApiClient.Builder(this, this, this)
//        .setActions(MomentUtil.ACTIONS)
//      //  .setScopes(Scopes.PLUS_LOGIN, Scopes.PLUS_PROFILE)
//        .build();
       // .setActions(MomentUtil.ACTIONS)
        //.setActions("http://schemas.google.com/AddActivity", "http://schemas.google.com/BuyActivity")
       // .setScopes(Scopes.PLUS_LOGIN)
     //   .build();

        mSignInStatus = (TextView) findViewById(R.id.sign_in_status);
        mSignInButton = (SignInButton) findViewById(R.id.sign_in_button);
        mSignInButton.setOnClickListener(this);
        mSignOutButton = findViewById(R.id.sign_out_button);
        mSignOutButton.setOnClickListener(this);
        mRevokeAccessButton = findViewById(R.id.revoke_access_button);
        mRevokeAccessButton.setOnClickListener(this);
    }

    protected void onStart() {
        super.onStart();
        mPlusClient.connect();
    }

    protected void onStop() {
        super.onStop();

        if (mPlusClient.isConnected()) {
            mPlusClient.disconnect();
        }
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.sign_in_button:
                int available = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
                if (available != ConnectionResult.SUCCESS) {
                    showDialog(DIALOG_GET_GOOGLE_PLAY_SERVICES);
                    return;
                }

                try {
                    mSignInStatus.setText(getString(R.string.signing_in_status));
                    mConnectionResult.startResolutionForResult(this, REQUEST_CODE_SIGN_IN);
                } catch (IntentSender.SendIntentException e) {
                    // Fetch a new result to start.
                    mPlusClient.connect();
                }
                break;
            case R.id.sign_out_button:
                if (mPlusClient.isConnected()) {
                    mPlusClient.clearDefaultAccountAndReconnect();
//                    mPlusClient.disconnect();
//                    mPlusClient.connect();
                }
                break;
            case R.id.revoke_access_button:
                if (mPlusClient.isConnected()) {
                    mPlusClient.clearDefaultAccountAndReconnect();
                    updateButtons(false /* isSignedIn */);
                }
                break;
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if (id != DIALOG_GET_GOOGLE_PLAY_SERVICES) {
            return super.onCreateDialog(id);
        }

        int available = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (available == ConnectionResult.SUCCESS) {
            return null;
        }
        if (GooglePlayServicesUtil.isUserRecoverableError(available)) {
            return GooglePlayServicesUtil.getErrorDialog(
                    available, this, REQUEST_CODE_GET_GOOGLE_PLAY_SERVICES);
        }
        return new AlertDialog.Builder(this)
                .setMessage(R.string.plus_generic_error)
                .setCancelable(true)
                .create();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_SIGN_IN
                || requestCode == REQUEST_CODE_GET_GOOGLE_PLAY_SERVICES) {
            if (resultCode == RESULT_OK && !mPlusClient.isConnected()
                    && !mPlusClient.isConnecting()) {
                // This time, connect should succeed.
                mPlusClient.connect();
            }
        }
    }

//    @Override
//    public void onAccessRevoked(ConnectionResult status) {
//        if (status.isSuccess()) {
//            mSignInStatus.setText(R.string.revoke_access_status);
//        } else {
//            mSignInStatus.setText(R.string.revoke_access_error_status);
//            mPlusClient.disconnect();
//        }
//        mPlusClient.connect();
//    }

    @Override
    public void onConnected(Bundle connectionHint) {
        if (Plus.PeopleApi.getCurrentPerson(mPlusClient) != null) {
            Person currentPerson = Plus.PeopleApi.getCurrentPerson(mPlusClient);
            String personName = currentPerson.getDisplayName();
            String personId = currentPerson.getId();
//            String personPhoto = currentPerson.getImage().getUrl();
//            String personGooglePlusProfile = currentPerson.getUrl();


        mSignInStatus.setText(getString(R.string.signed_in_status, personName)+"user ID"+personId);
        }
        updateButtons(true /* isSignedIn */);
    }


    @Override
    public void onConnectionSuspended(int i) {
        mPlusClient.connect();
    }

//    @Override
//    public void onDisconnected() {
//        mSignInStatus.setText(R.string.loading_status);
//        mPlusClient.connect();
//        updateButtons(false /* isSignedIn */);
//    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (!mIntentInProgress && result.hasResolution()) {
            try {
                mIntentInProgress = true;
                startIntentSenderForResult(result.getResolution().getIntentSender(),
                        REQUEST_CODE_SIGN_IN, null, 0, 0, 0);
            } catch (IntentSender.SendIntentException e) {
                // The intent was canceled before it was sent.  Return to the default
                // state and attempt to connect to get an updated ConnectionResult.
                mIntentInProgress = false;
                mPlusClient.connect();
            }
        }
        mConnectionResult = result;
        updateButtons(false /* isSignedIn */);
    }

    private void updateButtons(boolean isSignedIn) {
        if (isSignedIn) {
            mSignInButton.setVisibility(View.INVISIBLE);
            mSignOutButton.setEnabled(true);
            mRevokeAccessButton.setEnabled(true);
        } else {
            if (mConnectionResult == null) {
                // Disable the sign-in button until onConnectionFailed is called with result.
                mSignInButton.setVisibility(View.INVISIBLE);
                mSignInStatus.setText(getString(R.string.loading_status));
            } else {
                // Enable the sign-in button since a connection result is available.
                mSignInButton.setVisibility(View.VISIBLE);
                mSignInStatus.setText(getString(R.string.signed_out_status));
            }

            mSignOutButton.setEnabled(false);
            mRevokeAccessButton.setEnabled(false);
        }
    }

//    @Override
//    public void onResult(People.LoadPeopleResult peopleData) {
//        if (peopleData.getStatus().getStatusCode() == CommonStatusCodes.SUCCESS) {
//            PersonBuffer personBuffer = peopleData.getPersonBuffer();
//            try {
//                int count = personBuffer.getCount();
//                for (int i = 0; i < count; i++) {
//                    Log.d(TAG, "Display name: " + personBuffer.get(i).getDisplayName());
//                }
//            } finally {
//                personBuffer.close();
//            }
//        } else {
//            Log.e(TAG, "Error requesting visible circles: " + peopleData.getStatus());
//        }
//    }
}
