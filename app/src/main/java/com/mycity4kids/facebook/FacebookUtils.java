package com.mycity4kids.facebook;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.mycity4kids.interfaces.IFacebookUser;
import java.util.Arrays;

public final class FacebookUtils {

    private static CallbackManager callbackManager;

    public static void facebookLogin(Activity context, final IFacebookUser facebookUser) {
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {

                    @Override
                    public void onSuccess(final LoginResult loginResult) {

                        final AccessToken accessToken = AccessToken
                                .getCurrentAccessToken();

                        GraphRequest request = GraphRequest.newMeRequest(
                                loginResult.getAccessToken(),
                                (object, response) -> {
                                    try {
                                        Log.e("FB Object", "+++" + object.toString());
                                        facebookUser.getFacebookUser(object, accessToken.getToken());
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                });
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id,name,email,gender,friends");
                        request.setParameters(parameters);
                        request.executeAsync();
                    }

                    @Override
                    public void onCancel() {
                        AccessToken.setCurrentAccessToken(null);
                        Log.e("faceboook on cancel", "cancel");
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        AccessToken.setCurrentAccessToken(null);
                        Log.e("faceboook on error", "onError " + exception.toString());
                    }
                });

        LoginManager.getInstance().logInWithReadPermissions(context,
                Arrays.asList("public_profile", "email", "user_gender", "user_friends"));
    }

    public static void logout() {
        LoginManager.getInstance().logOut();

    }

    /**
     * Don't forget to added this function to onActivityResult() of your activities ever, otherwise you can not finish
     * your facebook successfully.
     */
    public static void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
        if (callbackManager != null) {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }
}
