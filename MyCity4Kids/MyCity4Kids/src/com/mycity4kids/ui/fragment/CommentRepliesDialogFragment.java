package com.mycity4kids.ui.fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.kelltontech.ui.IScreen;
import com.kelltontech.utils.ConnectivityUtils;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.controller.ArticleBlogDetailsController;
import com.mycity4kids.controller.CommentController;
import com.mycity4kids.dbtable.UserTable;
import com.mycity4kids.models.forgot.CommonResponse;
import com.mycity4kids.models.parentingdetails.CommentRequest;
import com.mycity4kids.models.parentingdetails.CommentsData;
import com.mycity4kids.models.user.UserModel;
import com.mycity4kids.models.user.UserResponse;
import com.mycity4kids.newmodels.AttendeeModel;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.ui.activity.ArticlesAndBlogsDetailsActivity;
import com.mycity4kids.ui.adapter.CommentsReplyAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by user on 08-06-2015.
 */
public class CommentRepliesDialogFragment extends DialogFragment implements OnClickListener, IScreen {

    ArrayList<AttendeeModel> data;
    ListView replyListView;
    CommentsReplyAdapter adapter;
    private CommentsData commentsData;
    private LinearLayout addCommentLayout;
    private EditText addReplyEditText;
    Toolbar mToolbar;
    ArrayList<CommentsData> completeReplies = new ArrayList<>();
    String articleId;
    private ProgressDialog mProgressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.comments_replies_dialog, container,
                false);
        mToolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
//        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

//        Window window = getDialog().getWindow();
//        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen);

//        window.setGravity(Gravity.BOTTOM | Gravity.CENTER_VERTICAL);

        mToolbar.setTitle("Replies");
        mToolbar.setTitleTextColor(ContextCompat.getColor(getActivity(), R.color.white_color));
        Drawable upArrow = ContextCompat.getDrawable(getActivity(), R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        upArrow.setColorFilter(ContextCompat.getColor(getActivity(), R.color.white_color), PorterDuff.Mode.SRC_ATOP);
        mToolbar.setNavigationIcon(upArrow);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
// handle back button naviagtion
                dismiss();
            }
        });

        replyListView = (ListView) rootView.findViewById(R.id.replyListView);
        addCommentLayout = (LinearLayout) rootView.findViewById(R.id.addCommentLinearLayout);
        addReplyEditText = (EditText) rootView.findViewById(R.id.addReplyEditText);
        addCommentLayout.setOnClickListener(this);
        Bundle extras = getArguments();
        if (extras != null) {
            commentsData = extras.getParcelable("commentData");
            articleId = extras.getString("articleId");
        }
        completeReplies.add(commentsData);
        prepareCompleteList(commentsData);
        adapter = new CommentsReplyAdapter(getActivity(), R.layout.custom_comment_cell, completeReplies);
        replyListView.setAdapter(adapter);
        return rootView;
    }

    private void prepareCompleteList(CommentsData cData) {

        for (int i = 0; i < cData.getReplies().size(); i++) {
            completeReplies.add(cData.getReplies().get(i));
            prepareCompleteList(cData.getReplies().get(i));
        }
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
//            dialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.blue_bg_rounded_corners));
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.addCommentLinearLayout:

                if (!ConnectivityUtils.isNetworkEnabled(getActivity())) {
                    ((BaseActivity) getActivity()).showSnackbar(getView().findViewById(R.id.root), getString(R.string.error_network));
                    return;
                }


                if (isValid()) {
                    UserTable _table = new UserTable((BaseApplication) getActivity().getApplication());
                    int count = _table.getCount();

                    if (count > 0) {
                        UserModel userData = _table.getAllUserData();
                        CommentRequest _commentRequest = new CommentRequest();
                        _commentRequest.setArticleId(articleId);
                        /**
                         * in case of comment parentId will be empty.
                         *
                         */
                        _commentRequest.setParentId(commentsData.getId());
                        _commentRequest.setContent(addReplyEditText.getText().toString());
                        _commentRequest.setUserId("" + userData.getUser().getId());
                        _commentRequest.setSessionId(userData.getUser().getSessionId());
                        CommentController _controller = new CommentController(getActivity(), this);
                        View viewa = getActivity().getCurrentFocus();
                        if (viewa != null) {
                            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        }

                        showProgressDialog("Adding a comment...");
                        _controller.getData(AppConstants.COMMENT_REPLY_REQUEST, _commentRequest);
                    }
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

        if (addReplyEditText.getText().toString().length() == 0) {
            Toast.makeText(getActivity(), "Please add a reply", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    @Override
    public void handleUiUpdate(Response response) {
        removeProgressDialog();
        if (response == null) {
            Toast.makeText(getActivity(), getResources().getString(R.string.server_error), Toast.LENGTH_SHORT).show();
            return;
        }

        switch (response.getDataType()) {
            case AppConstants.COMMENT_REPLY_REQUEST:
                CommonResponse commonData = (CommonResponse) response.getResponseObject();
                String messageComment = commonData.getResult().getMessage();
                Toast.makeText(getActivity(), messageComment, Toast.LENGTH_LONG).show();
                if (commonData.getResponseCode() == 200) {
                    ((ArticlesAndBlogsDetailsActivity) getActivity()).sendScrollUp();
                    updateReplyListToShowComment();
                } else if (commonData.getResponseCode() == 400) {
                    String message = commonData.getResult().getMessage();
                    if (!StringUtils.isNullOrEmpty(message)) {
                        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getActivity(), getString(R.string.went_wrong), Toast.LENGTH_LONG).show();
                    }
                }
//                    removeProgressDialog();
                break;
        }
    }

    private void updateReplyListToShowComment() {

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = df.format(cal.getTime());

        CommentsData cData = new CommentsData();
        cData.setBody(addReplyEditText.getText().toString());
        cData.setName(SharedPrefUtils.getUserDetailModel(getActivity()).getFirst_name());
        cData.setCreate(formattedDate);
        cData.setProfile_image(SharedPrefUtils.getProfileImgUrl(getActivity()));
        addReplyEditText.setText("");
        completeReplies.add(cData);
        adapter.notifyDataSetChanged();
    }
}