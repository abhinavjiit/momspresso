package com.mycity4kids.facebook;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.mycity4kids.interfaces.IFacebookUser;

import org.json.JSONObject;

import java.util.Arrays;

/**
 * @author "Deepanker Chaudhary : this is a utility class for facebook activities"
 */

public final class FacebookUtils {

    private static CallbackManager callbackManager;

//    public static void postToFacebook(final Activity _context, final String _name, final String _caption,
//                                      final String _description, final String _link, final String _imageUrl) {
//        Session s = Session.getActiveSession();
//        if (s != null && s.isOpened()) {
//            post(_context, _name, _caption, _description, _link, _imageUrl);
//        } else {
//            Session.openActiveSession(_context, true, new Session.StatusCallback() {
//
//                @Override
//                public void call(Session _session, SessionState _state, Exception _exception) {
//                    if (_session.isOpened()) {
//                        post(_context, _name, _caption, _description, _link, _imageUrl);
//                    }
//                }
//            });
//        }
//    }


//    private static void post(Context _context, String _name, String _caption, String _description, String _link,
//                             String _imageUrl) {
//        Bundle params = new Bundle();
//        params.putString("name", _name);
//        params.putString("caption", _caption);
//        params.putString("description", _description);
//        params.putString("link", _link);
//        if (_imageUrl != null) {
//            params.putString("picture", _imageUrl);
//        }
//        new WebDialog.FeedDialogBuilder(_context, Session.getActiveSession(), params).build().show();
//    }

    public static void facebookLogin(Activity context, final IFacebookUser iFacebookUser) {
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {

                    @Override
                    public void onSuccess(final LoginResult loginResult) {

                        final AccessToken accessToken = AccessToken
                                .getCurrentAccessToken();

                        GraphRequest request = GraphRequest.newMeRequest(
                                loginResult.getAccessToken(),
                                new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(final JSONObject object, GraphResponse response) {
                                        try {
//                                            LoginRegistrationRequest lr = new LoginRegistrationRequest();
//                                            lr.setCityId("" + SharedPrefUtils.getCurrentCityModel(ActivityLogin.this).getId());
//                                            lr.setRequestMedium("fb");
//                                            lr.setSocialToken(accessToken.getToken());
//
//                                            Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
//                                            LoginRegistrationAPI loginRegistrationAPI = retrofit.create(LoginRegistrationAPI.class);
//                                            Call<UserDetailResponse> call = loginRegistrationAPI.login(lr);
//                                            call.enqueue(onLoginResponseReceivedListener);
                                            iFacebookUser.getFacebookUser(accessToken.getToken());

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id,name,email,gender");
                        request.setParameters(parameters);
                        request.executeAsync();

                    }

                    @Override
                    public void onCancel() {
                        AccessToken.setCurrentAccessToken(null);
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        AccessToken.setCurrentAccessToken(null);
                        // Toast.makeText(SignUpOption.this, exception.getMessage(), Toast.LENGTH_LONG).show();
//                        showToast(exception.getMessage());
                    }
                });

        LoginManager.getInstance().logInWithReadPermissions(context, Arrays.asList("public_profile", "email"));


//        Settings.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);
//        Session.OpenRequest openRequest = new Session.OpenRequest(context);
//        openRequest.setPermissions(Arrays.asList("email", "user_friends"));
//        openRequest.setCallback(new Session.StatusCallback() {
//
//            @Override
//            public void call(Session session, SessionState state,
//                             Exception exception) {
//                if (session.isOpened()) {
//                    // Log.e("exception ","in session");
//                    // make request to the /me API
//                    Request request = Request.newMeRequest(session, new Request.GraphUserCallback() {
//                        @Override
//                        public void onCompleted(GraphUser user, Response response) {
//                            if (user != null) {
//                                iFacebookUser.getFacebookUser(user);
//                            }
//                        }
//                    });
//                    request.executeAsync();
//                } else {
////                    Log.e("exception ", "in catch block" + exception);
////                    if(session!=null)
////                        Log.e("exception ",session+"in session catch block");
////                    if(exception!=null)
////                    Log.e("exception ",exception.getMessage());
////                    if(state!=null)
////                        Log.e("state ", state.toString());
//                }
//            }
//        });
//        Session session = new Session(context);
//        Session.setActiveSession(session);
//        session.openForRead(openRequest);
    }


//    public static boolean fetchFacebookEvents(final IFacebookEvent iFacebookEvent) {
//        Session s = Session.getActiveSession();
//        if (s != null && s.isOpened()) {
//            Request request = Request.newGraphPathRequest(s, "/me/events", new Request.Callback() {
//                @Override
//                public void onCompleted(Response response) {
//                    if (response != null) {
////                        Log.e("Facebook response", response.toString());
//                        String eventData = response.getGraphObject().asMap().get("data").toString();
////                        Log.e("Facebook events", eventData);
//                        iFacebookEvent.onFacebookEventReceived
//                                (eventData);
//                    } else {
//                        iFacebookEvent.onFacebookEventReceived(null);
//                    }
//
//
//                }
//            });
//
//            request.executeAsync();
//            return true;
//        } else {
//            return false;
//        }
//    }

    public static void logout(Activity context) {
        LoginManager.getInstance().logOut();
//        Session session = Session.getActiveSession();
//        if (session != null) {
//            session.closeAndClearTokenInformation();
//            session.close();
//            Session.setActiveSession(null);
//        } else {
//            session = new Session(context);
//            Session.getActiveSession();
//
//            session.closeAndClearTokenInformation();
//            session.close();
//        }

    }


//    public static boolean isLoggedIn() {
//        Session session = Session.getActiveSession();
//        return (session != null && session.getAccessToken() != null && session.getAccessToken().length() > 1);
//    }
//
//
//    public static void askMe(Request.GraphUserCallback _callback) {
//        Session session = Session.getActiveSession();
//        if (session != null) {
//            Request.executeMeRequestAsync(session, _callback);
//        }
//    }


    /**
     * Don't forget to added this function to onActivityResult() of your
     * activities ever, otherwise you can not finish your facebook successfully.
     */
    public static void onActivityResult(Activity _activity, int _requestCode, int _resultCode, Intent _data) {
//        Session session = Session.getActiveSession();
//        if (session != null) {
//            Session.getActiveSession().onActivityResult(_activity, _requestCode, _resultCode, _data);
//        }
        callbackManager.onActivityResult(_requestCode, _resultCode, _data);
    }

}
