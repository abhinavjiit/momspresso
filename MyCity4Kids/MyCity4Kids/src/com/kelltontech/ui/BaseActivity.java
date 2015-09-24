package com.kelltontech.ui;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.kelltontech.network.Response;
import com.kelltontech.utils.ConnectivityUtils;
import com.kelltontech.utils.StringUtils;
import com.kelltontech.utils.facebook.listener.FacebookLoginListener;
import com.kelltontech.utils.facebook.listener.FacebookPostListener;
import com.kelltontech.utils.facebook.model.FacebookUtils;
import com.kelltontech.utils.facebook.model.UserInfo;
import com.mycity4kids.BuildConfig;
import com.mycity4kids.R;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.controller.ArticleBlogFollowController;
import com.mycity4kids.listener.OnButtonClicked;
import com.mycity4kids.models.parentingstop.ArticleBlogFollowRequest;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.sync.SyncService;
import com.mycity4kids.sync.SyncSocialMediaEventService;
import com.mycity4kids.sync.SyncUserInfoService;
import com.mycity4kids.ui.activity.DashboardActivity;
import com.mycity4kids.ui.activity.LandingLoginActivity;
import com.mycity4kids.utils.AnalyticsHelper;

import java.util.Timer;
import java.util.TimerTask;

/**
 * This class is used as base-class for application-base-activity.
 */
public abstract class BaseActivity extends AppCompatActivity implements IScreen {

    //    private int activitiesCount = 0;
    public static boolean isAppInFg = false;
    public static boolean isScrInFg = false;
    public static boolean isChangeScrFg = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(getClass().getSimpleName(), "onCreate()");

    }


    public void replaceFragment(final Fragment fragment, Bundle bundle, boolean isAddToBackStack) {
        if (bundle != null) {
            fragment.setArguments(bundle);
        }
        new Handler().post(new Runnable() {
            @Override
            public void run() {

                try {
                    String backStateName = fragment.getClass().getName();
                    boolean fragmentPopped = getSupportFragmentManager().popBackStackImmediate(backStateName, 0);

                    if (!fragmentPopped) { // fragment not in back stack, create
                        // it.
                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                        ft.replace(R.id.content_frame, fragment);
                        ft.addToBackStack(backStateName);
                        ft.commit();
                    }
                } catch (Exception e) {
                    // TODO: handle exception
                }

            }
        });


//        String tag = fragment.getClass().getSimpleName();
//        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//        Fragment fragmentLocal = getSupportFragmentManager().findFragmentById(R.id.content_frame);
//        if (fragmentLocal != null && fragmentLocal.getTag().equalsIgnoreCase(tag)) {
//            ((BaseFragment) fragmentLocal).refreshFragment(bundle);
//            return;
//        }
//
//        ft.replace(R.id.content_frame, fragment, tag);
//        fragment.setRetainInstance(true);
//        if (isAddToBackStack) {
//            ft.addToBackStack(tag);
//        }
//        try {
//            ft.commit();
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            try{
//                ft.commitAllowingStateLoss();
//            }catch(Exception e){
//            }
//        }
    }

    protected void startSyncing() {
        Intent intent = new Intent(this, SyncService.class);
        startService(intent);
    }

    protected void startSyncingUserInfo() {
        Intent intent = new Intent(this, SyncUserInfoService.class);
        startService(intent);
    }

    protected void startSyncingSocialEvents() {
        Intent intent = new Intent(this, SyncSocialMediaEventService.class);
        startService(intent);
    }


    @Override
    protected void onStart() {
        if (!isAppInFg) {
            isAppInFg = true;
            isChangeScrFg = false;
            onAppStart();
        } else {
            isChangeScrFg = true;
        }
        isScrInFg = true;
        super.onStart();
        AnalyticsHelper.onActivityStart(this);
        AnalyticsHelper.setLogEnabled(Constants.IS_GOOGLE_ANALYTICS_ENABLED);
    }

    @Override
    protected void onStop() {
        super.onStop();
        AnalyticsHelper.onActivityStop(this);
        if (!isScrInFg || !isChangeScrFg) {
            isAppInFg = false;
            onAppPause();
        }
        isScrInFg = false;
    }

    public void onAppStart() {
        if (ConnectivityUtils.isNetworkEnabled(this)) {
            if (SharedPrefUtils.getUserDetailModel(this).getId() > 0) {
                startSyncing();
                startSyncingUserInfo();
            }

        }
    }

    public void onAppPause() {
        // Code here if required any event, when app geoes to background
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(getClass().getSimpleName(), "onResume()");
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.i(getClass().getSimpleName(), "onNewIntent()");
    }


    /**
     * this method should be called only from UI thread.
     *
     * @param response
     */
    @Override
    public final void handleUiUpdate(final Response response) {
        if (isFinishing()) {
            return;
        }
        if (BuildConfig.DEBUG) {
            updateUi(response);
        } else {
            try {
                updateUi(response);
            } catch (Exception e) {
                Log.i(getClass().getSimpleName(), "updateUi()", e);
            }
        }

    }

    public void showAlertDialog(String title, String message, final OnButtonClicked onButtonClicked) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                        dialog.dismiss();
                        onButtonClicked.onButtonCLick(0);
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                        dialog.dismiss();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    /**
     * Subclass should over-ride this method to update the UI with response
     *
     * @param response
     */
    protected abstract void updateUi(Response response);

    // ////////////////////////////// show and hide ProgressDialog

    private ProgressDialog mProgressDialog;

    /**
     * @param bodyText
     */
    public void showProgressDialog(String bodyText) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(BaseActivity.this);
            mProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setOnKeyListener(new Dialog.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_CAMERA || keyCode == KeyEvent.KEYCODE_SEARCH) {
                        return true; //
                    }
                    return false;
                }
            });
        }

        mProgressDialog.setMessage(bodyText);

        if (!mProgressDialog.isShowing()) {
            mProgressDialog.show();
        }
    }

    /**
     *
     */
    public void removeProgressDialog() {
        try {
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // ////////////////////////////// show and hide key-board

    /**
     *
     */
    protected void showVirturalKeyboard() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                InputMethodManager m = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (m != null) {
                    m.toggleSoftInput(0, InputMethodManager.HIDE_IMPLICIT_ONLY);
                }
            }
        }, 100);
    }

    /**
     *
     */
    protected void hideVirturalKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(), 0);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        View v = getCurrentFocus();
        boolean ret = super.dispatchTouchEvent(event);

        if (v instanceof EditText) {
            View w = getCurrentFocus();
            int scrcoords[] = new int[2];
            w.getLocationOnScreen(scrcoords);
            float x = event.getRawX() + w.getLeft() - scrcoords[0];
            float y = event.getRawY() + w.getTop() - scrcoords[1];

            if (event.getAction() == MotionEvent.ACTION_UP && (x < w.getLeft() || x >= w.getRight() || y < w.getTop() || y > w.getBottom())) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(), 0);
            }
        }
        return ret;
    }

    /**
     * @param message
     */
    Toast toast;

    public void showToast(String message) {
        if (toast != null)
            toast.cancel();
        toast = Toast.makeText(BaseActivity.this, message, Toast.LENGTH_LONG);
        toast.show();
    }

    public void showSnackbar(View view, String message) {
        Snackbar
                .make(view, message, Snackbar.LENGTH_LONG)
//                .setAction(R.string.snackbar_action, myOnClickListener)
                .show(); // Don’t forget to show!
    }


    public void goToLanding() {
        Intent intent = new Intent(this, LandingLoginActivity.class);
        if (Constants.IS_COMING_FROM_INSIDE) {
            intent.putExtra(Constants.LOGIN_REQUIRED, true);
        } else {
            intent.putExtra(Constants.LOGIN_REQUIRED, false);
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }


    public void sendToHomeScreen() {
        Intent intent1 = new Intent(this, DashboardActivity.class);
        intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent1);
        finish();
    }

    public String getImeiNumber() {
        try {
            TelephonyManager mngr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
            return mngr.getDeviceId();
        } catch (Exception ex) {
            ex.printStackTrace();
            return "";
        }
    }

    public void followAPICall(String id) {

        ArticleBlogFollowRequest _followRequest = new ArticleBlogFollowRequest();
        _followRequest.setSessionId("" + SharedPrefUtils.getUserDetailModel(getApplicationContext()).getSessionId());
        _followRequest.setUserId("" + SharedPrefUtils.getUserDetailModel(getApplicationContext()).getId());
        _followRequest.setAuthorId("" + id);
        ArticleBlogFollowController _followController = new ArticleBlogFollowController(this, this);
        showProgressDialog(getString(R.string.please_wait));
        _followController.getData(AppConstants.ARTICLE_BLOG_FOLLOW_REQUEST, _followRequest);

    }

    public void facebookAPICall(final Activity activity, final String url, final String title) {
        showProgressDialog(getString(R.string.loading_));
        FacebookUtils.loginFacebook(activity, new FacebookLoginListener() {

            @Override
            public void doAfterLogin(UserInfo userInfo) {
                removeProgressDialog();
                //System.out.println("Test");
                Log.d("facebook", "Login Completed.");
                String shareUrl = "";
                if (StringUtils.isNullOrEmpty(url)) {
                    shareUrl = "";
                } else {
                    shareUrl = url;
                }
                FacebookUtils.postOnWall(activity,
                        new FacebookPostListener() {
                            @Override
                            public void doAfterPostOnWall(boolean status) {
                                removeProgressDialog();
                                showToast("Message has been successfully posted on your wall.");
                                Log.d("facebook", "Post On Wall  Completed.");
                                System.out.println(status);
                            }
                        }, shareUrl, title);
            }
        });
    }

//    public void twitterAPICall(String url, String title) {
//
//        Twitter.getInstance(Constants.TWITTER_OAUTH_KEY,
//                Constants.TWITTER_OAUTH_SECRET, Constants.CALLBACK_URL,
//                this).doLogin(new ITLogin() {
//            @Override
//            public void success(TwitterUser user) {
//                showToast("Tweet has been successfully posted.");
//            }
//        }, title + " on mycity4kids. Check it out here " + url);
//
//    }

    public void rssAPICall() {

    }


}