package com.mycity4kids.ui.fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.kelltontech.utils.DateTimeUtils;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.models.response.GroupPostCommentResult;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.ui.activity.GroupPostDetailActivity;
import com.squareup.picasso.Picasso;

/**
 * Created by user on 08-06-2015.
 */
public class AddGpPostCommentReplyDialogFragment extends DialogFragment implements OnClickListener {

    private ProgressDialog mProgressDialog;
    private ImageView closeImageView;
    private TextView addCommentTextView;
    private EditText commentReplyEditText;
    private TextView replyToTextView;
    private View separator;
    private GroupPostCommentResult commentOrReplyData;
    private String actionType;
    private int position;
    private TextView headingTextView;
    private RelativeLayout relativeMainContainer;
    private ImageView commentorImageView;
    private TextView commentorUsernameTextView;
    private TextView commentDataTextView;
    private TextView commentDateTextView;
    private ImageView anonymousImageView;
    private TextView anonymousTextView;
    private CheckBox anonymousCheckbox;
    private View bottombarTopline;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.add_gp_post_comment_reply_fragment, container,
                false);

        closeImageView = (ImageView) rootView.findViewById(R.id.closeImageView);
        addCommentTextView = (TextView) rootView.findViewById(R.id.postCommentReplyTextView);
        commentReplyEditText = (EditText) rootView.findViewById(R.id.commentReplyEditText);
        headingTextView = (TextView) rootView.findViewById(R.id.headingTextView);
        relativeMainContainer = (RelativeLayout) rootView.findViewById(R.id.relativeMainContainer);
        commentorImageView = (ImageView) rootView.findViewById(R.id.commentorImageView);
        commentorUsernameTextView = (TextView) rootView.findViewById(R.id.commentorUsernameTextView);
        commentDataTextView = (TextView) rootView.findViewById(R.id.commentDataTextView);
        commentDateTextView = (TextView) rootView.findViewById(R.id.commentDateTextView);
        anonymousImageView = (ImageView) rootView.findViewById(R.id.anonymousImageView);
        anonymousTextView = (TextView) rootView.findViewById(R.id.anonymousTextView);
        anonymousCheckbox = (CheckBox) rootView.findViewById(R.id.anonymousCheckbox);
        bottombarTopline = rootView.findViewById(R.id.bottombarTopline);

        Bundle extras = getArguments();
        commentOrReplyData = (GroupPostCommentResult) extras.get("parentCommentData");
        actionType = (String) extras.get("action");
        position = extras.getInt("position");

        addCommentTextView.setOnClickListener(this);
        closeImageView.setOnClickListener(this);
        anonymousImageView.setOnClickListener(this);
        anonymousTextView.setOnClickListener(this);
        anonymousCheckbox.setOnClickListener(this);

        if (SharedPrefUtils.isUserAnonymous(BaseApplication.getAppContext())) {
            anonymousCheckbox.setChecked(true);
        } else {
            anonymousCheckbox.setChecked(false);
        }

        if (commentOrReplyData == null) {
            headingTextView.setText(BaseApplication.getAppContext().getString(R.string.short_s_add_comment));
            relativeMainContainer.setVisibility(View.GONE);
        } else {
            if ("EDIT_COMMENT".equals(actionType) || "EDIT_REPLY".equals(actionType)) {
                headingTextView.setText(BaseApplication.getAppContext().getString(R.string.ad_comments_edit_label));
                relativeMainContainer.setVisibility(View.GONE);
                commentReplyEditText.setText(commentOrReplyData.getContent());
                anonymousImageView.setVisibility(View.GONE);
                anonymousTextView.setVisibility(View.GONE);
                anonymousCheckbox.setVisibility(View.GONE);
                bottombarTopline.setVisibility(View.GONE);
            } else {
                headingTextView.setText(BaseApplication.getAppContext().getString(R.string.reply));
                relativeMainContainer.setVisibility(View.VISIBLE);

                if (commentOrReplyData.getIsAnnon() == 1) {
                    commentorUsernameTextView.setText(BaseApplication.getAppContext().getString(R.string.groups_anonymous));
                    commentorImageView.setImageDrawable(ContextCompat.getDrawable(BaseApplication.getAppContext(), R.drawable.ic_incognito));
                } else {
                    try {
                        Picasso.with(getActivity()).load(commentOrReplyData.getUserInfo().getProfilePicUrl().getClientApp())
                                .placeholder(R.drawable.default_commentor_img).into((commentorImageView));
                    } catch (Exception e) {
                        Crashlytics.logException(e);
                        Log.d("MC4kException", Log.getStackTraceString(e));
                        if (isAdded())
                            Picasso.with(getActivity()).load(R.drawable.default_commentor_img).into(commentorImageView);
                    }
                    commentorUsernameTextView.setText(commentOrReplyData.getUserInfo().getFirstName() + " " + commentOrReplyData.getUserInfo().getLastName());
                }

                commentDataTextView.setText(commentOrReplyData.getContent());
                commentDateTextView.setText(DateTimeUtils.getDateFromNanoMilliTimestamp(commentOrReplyData.getCreatedAt()));
            }
        }

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
                        ((GroupPostDetailActivity) getActivity()).editComment(commentOrReplyData.getId(), commentReplyEditText.getText().toString(), position);
                    } else if ("EDIT_REPLY".equals(actionType)) {
                        ((GroupPostDetailActivity) getActivity()).editReply(commentReplyEditText.getText().toString(), commentOrReplyData.getParentId(), commentOrReplyData.getId());
                    } else {
                        if (commentOrReplyData == null) {
                            ((GroupPostDetailActivity) getActivity()).addComment(commentReplyEditText.getText().toString());
                        } else {
                            ((GroupPostDetailActivity) getActivity()).addReply(commentOrReplyData.getId(), commentReplyEditText.getText().toString());
                        }
                    }
                    dismiss();
                }
                break;
            case R.id.closeImageView:
                dismiss();
                break;
            case R.id.anonymousImageView:
            case R.id.anonymousTextView:
            case R.id.anonymousCheckbox:
                if (anonymousCheckbox.isChecked()) {
                    SharedPrefUtils.setUserAnonymous(BaseApplication.getAppContext(), true);
                } else {
                    SharedPrefUtils.setUserAnonymous(BaseApplication.getAppContext(), false);
                }
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
                Toast.makeText(getActivity(), "Please add a reply", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

}