package com.mycity4kids.ui.fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.mycity4kids.utils.DateTimeUtils;
import com.mycity4kids.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.models.response.CommentListData;
import com.squareup.picasso.Picasso;

/**
 * Created by hemant on 6/6/18.
 */

public class AddArticleCommentReplyDialogFragment extends DialogFragment implements View.OnClickListener {

    private CommentListData commentOrReplyData;

    private ProgressDialog mProgressDialog;
    private ImageView closeImageView;
    private TextView postCommentReplyTextView;
    private EditText commentReplyEditText;
    private TextView replyToTextView;
    private View separator;
    private RelativeLayout relativeMainContainer;
    private ImageView commentorImageView;
    private TextView commentorUsernameTextView;
    private TextView commentDataTextView;
    private TextView commentDateTextView;
    private TextView headingTextView;
    private String actionType;
    private int position;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.add_ss_comment_reply_fragment, container,
                false);

        closeImageView = (ImageView) rootView.findViewById(R.id.closeImageView);
        postCommentReplyTextView = (TextView) rootView.findViewById(R.id.postCommentReplyTextView);
        commentReplyEditText = (EditText) rootView.findViewById(R.id.commentReplyEditText);
        relativeMainContainer = (RelativeLayout) rootView.findViewById(R.id.relativeMainContainer);
        commentorImageView = (ImageView) rootView.findViewById(R.id.commentorImageView);
        commentorUsernameTextView = (TextView) rootView.findViewById(R.id.commentorUsernameTextView);
        commentDataTextView = (TextView) rootView.findViewById(R.id.commentDataTextView);
        commentDateTextView = (TextView) rootView.findViewById(R.id.commentDateTextView);
        headingTextView = (TextView) rootView.findViewById(R.id.headingTextView);

        Bundle extras = getArguments();
//        String responseType = extras.getString("responseType");
        commentOrReplyData = (CommentListData) extras.get("parentCommentData");
        actionType = (String) extras.get("action");
        position = extras.getInt("position");

        if (commentOrReplyData == null) {
            headingTextView.setText(BaseApplication.getAppContext().getString(R.string.short_s_add_comment));
            relativeMainContainer.setVisibility(View.GONE);
        } else {
            if ("EDIT_COMMENT".equals(actionType) || "EDIT_REPLY".equals(actionType)) {
                headingTextView.setText(BaseApplication.getAppContext().getString(R.string.ad_comments_edit_label));
                relativeMainContainer.setVisibility(View.GONE);
                commentReplyEditText.setText(commentOrReplyData.getMessage());
            } else {
                headingTextView.setText(BaseApplication.getAppContext().getString(R.string.reply));
                relativeMainContainer.setVisibility(View.VISIBLE);
                try {
                    Picasso.get().load(commentOrReplyData.getUserPic().getClientAppMin())
                            .placeholder(R.drawable.default_commentor_img).into((commentorImageView));
                } catch (Exception e) {
                    Crashlytics.logException(e);
                    Log.d("MC4kException", Log.getStackTraceString(e));
                    if (isAdded())
                        Picasso.get().load(R.drawable.default_commentor_img).into(commentorImageView);
                }
                commentorUsernameTextView.setText(commentOrReplyData.getUserName());
                commentDataTextView.setText(commentOrReplyData.getMessage());
                commentDateTextView.setText(DateTimeUtils.getDateFromNanoMilliTimestamp(Long.parseLong(commentOrReplyData.getCreatedTime())));
            }
        }

        postCommentReplyTextView.setOnClickListener(this);
        closeImageView.setOnClickListener(this);


        return rootView;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.postCommentReplyTextView:
                if (isValid()) {
                    if ("EDIT_COMMENT".equals(actionType)) {

                        ((ArticleCommentsFragment) getParentFragment()).editComment(commentReplyEditText.getText().toString(), commentOrReplyData.get_id(), position);
                    } else if ("EDIT_REPLY".equals(actionType)) {
                        Fragment fragment = getParentFragment();
                        if (fragment != null && fragment instanceof ArticleCommentsFragment) {
                            ((ArticleCommentsFragment) getParentFragment()).editReply(commentReplyEditText.getText().toString(), commentOrReplyData.getParentCommentId(), commentOrReplyData.get_id());
                        } else if (fragment != null && fragment instanceof ArticleCommentRepliesDialogFragment) {
                            Fragment parentOfParentFragment = fragment.getParentFragment();
                            if (parentOfParentFragment != null && parentOfParentFragment instanceof ArticleCommentsFragment) {
                                ((ArticleCommentsFragment) parentOfParentFragment).editReply(commentReplyEditText.getText().toString(), commentOrReplyData.getParentCommentId(), commentOrReplyData.get_id());
                            }
                        }

                    } else {
                        if (commentOrReplyData == null) {
                            ((AddComments) this.getParentFragment()).addComments(commentReplyEditText.getText().toString());

                        } else {
                            ((ArticleCommentsFragment) getParentFragment()).addReply(commentReplyEditText.getText().toString(), commentOrReplyData.get_id());
                        }
                    }
                    dismiss();
                }
                break;
            case R.id.closeImageView:
                dismiss();
                break;

        }

    }

    public void showProgressDialog(String bodyText) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            mProgressDialog.setCancelable(false);
        }

        mProgressDialog.setMessage(bodyText);

        if (!mProgressDialog.isShowing()) {
            mProgressDialog.show();
        }
    }

    public void removeProgressDialog() {
        try {
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private boolean isValid() {
        if (StringUtils.isNullOrEmpty(commentReplyEditText.getText().toString())) {
            if (isAdded())
                Toast.makeText(getActivity(), getString(R.string.ad_comments_toast_empty_comment), Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    public interface AddComments {

        void addComments(String comment);
    }


}