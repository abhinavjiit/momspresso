package com.kelltontech.ui;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.kelltontech.network.Response;
import com.mycity4kids.application.BaseApplication;

/**
 * @author deepanker.chaudhary
 */
public abstract class BaseFragment extends Fragment implements IScreen {


    @Override
    public final void handleUiUpdate(final Response response) {
        if (getActivity() != null) {
            if (getActivity().isFinishing()) {
                return;
            }
            try {
                updateUi(response);
            } catch (Exception e) {
                Log.i(getClass().getSimpleName(), "updateUi()", e);
            }

        }
    }

    private ProgressDialog mProgressDialog;

    /**
     * @param bodyText
     */
    public void showProgressDialog(String bodyText) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.setOnKeyListener(new Dialog.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    return keyCode == KeyEvent.KEYCODE_CAMERA || keyCode == KeyEvent.KEYCODE_SEARCH;
                }
            });
        }

        mProgressDialog.setMessage(bodyText);

        if (!mProgressDialog.isShowing() && isAdded()) {
            mProgressDialog.show();
        }
    }

    /**
     *
     */
    public void removeProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing() && isAdded()) {
            mProgressDialog.dismiss();
        }
    }

    protected abstract void updateUi(Response response);


    public void refreshFragment(Bundle bundle) {

    }

    @Override
    public void onResume() {
        super.onResume();
        final Tracker tracker = ((BaseApplication) getActivity().getApplication()).getTracker(BaseApplication.TrackerName.APP_TRACKER);
        if (tracker != null) {

            tracker.setScreenName(getClass().getSimpleName());
            tracker.send(new HitBuilders.ScreenViewBuilder().build());
        }
    }
}
