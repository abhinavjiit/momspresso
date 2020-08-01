package com.mycity4kids.base;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.view.KeyEvent;
import android.view.Window;

import androidx.fragment.app.Fragment;

import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.models.response.CommentListResponse;
import com.mycity4kids.tagging.Mentions;
import com.mycity4kids.utils.AppUtils;
import java.util.Map;

/**
 * @author deepanker.chaudhary
 */
public abstract class BaseFragment extends Fragment {

    private ProgressDialog mProgressDialog;

    /**
     *
     */
    public void showProgressDialog(String bodyText) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(getActivity(), R.style.MyAlertDialogStyle);
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

    public void removeProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing() && isAdded()) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        final Tracker tracker = (BaseApplication.getInstance()).getTracker(BaseApplication.TrackerName.APP_TRACKER);
        if (tracker != null) {
            tracker.setScreenName(getClass().getSimpleName());
            tracker.send(new HitBuilders.ScreenViewBuilder().build());
        }
    }

    protected void shareCommentOnFacebook(String shareUrl, String shareMessage, Map<String, Mentions> map) {
        if (ShareDialog.canShow(ShareLinkContent.class)) {
            ShareLinkContent content = new ShareLinkContent.Builder().setQuote(
                    AppUtils.replaceUserIdWithName(shareMessage, map))
                    .setContentUrl(Uri.parse(shareUrl)).build();
            if (isAdded()) {
                new ShareDialog(getActivity()).show(content);
            }
        }
    }

}
