package com.mycity4kids.ui.fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.kelltontech.ui.IScreen;
import com.kelltontech.utils.ConnectivityUtils;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.models.parentingdetails.CommentsData;
import com.mycity4kids.models.request.AddCommentRequest;
import com.mycity4kids.models.response.AddCommentResponse;
import com.mycity4kids.newmodels.AttendeeModel;
import com.mycity4kids.retrofitAPIsInterfaces.ArticleDetailsAPI;
import com.mycity4kids.ui.activity.ArticlesAndBlogsDetailsActivity;
import com.mycity4kids.ui.activity.VlogsDetailActivity;
import com.mycity4kids.ui.adapter.CommentsReplyAdapter;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by user on 08-06-2015.
 */
public class EditCommentsRepliesFragment extends DialogFragment implements OnClickListener, IScreen {

    ArrayList<AttendeeModel> data;
    CommentsReplyAdapter adapter;
    private CommentsData commentsData, nestedReplyData;
    private EditText commentReplyEditText;
    private TextView updateCommentReplyTextView;
    private TextView cancelTextView;

    Toolbar mToolbar;
    ArrayList<CommentsData> completeReplies = new ArrayList<>();
    String articleId;
    int editType, position;
    private ProgressDialog mProgressDialog;
    private String type;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.edit_comments_replies_fragment, container,
                false);

        updateCommentReplyTextView = (TextView) rootView.findViewById(R.id.txvUpdate);
        cancelTextView = (TextView) rootView.findViewById(R.id.txvCancel);

        mToolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        mToolbar.setTitle("Edit Comment");
        mToolbar.setTitleTextColor(ContextCompat.getColor(getActivity(), R.color.colorControlNormal));
        Drawable upArrow = ContextCompat.getDrawable(getActivity(), R.drawable.back_arroow);
        upArrow.setColorFilter(ContextCompat.getColor(getActivity(), R.color.colorControlNormal), PorterDuff.Mode.SRC_ATOP);
        mToolbar.setNavigationIcon(upArrow);
        mToolbar.setNavigationOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // handle back button naviagtion
                dismiss();
            }
        });
        updateCommentReplyTextView.setOnClickListener(this);
        cancelTextView.setOnClickListener(this);

        commentReplyEditText = (EditText) rootView.findViewById(R.id.commentReplyEditText);

        Bundle extras = getArguments();
        if (extras != null) {
            commentsData = extras.getParcelable("commentData");
            articleId = extras.getString("articleId");
            type = extras.getString("type");
            editType = extras.getInt(AppConstants.COMMENT_OR_REPLY_OR_NESTED_REPLY);
            position = extras.getInt("position", 1);
            nestedReplyData = extras.getParcelable("replyData");
        }
        if (editType == AppConstants.EDIT_NESTED_REPLY) {
            commentReplyEditText.setText(nestedReplyData.getBody());
        } else {
            commentReplyEditText.setText(commentsData.getBody());
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
            case R.id.txvUpdate:
                if (!ConnectivityUtils.isNetworkEnabled(getActivity())) {
                    ((BaseActivity) getActivity()).showSnackbar(getView().findViewById(R.id.root), getString(R.string.error_network));
                    return;
                }

                if (isValid()) {
                    String commentId;
                    Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
                    ArticleDetailsAPI articleDetailsAPI = retrofit.create(ArticleDetailsAPI.class);
                    AddCommentRequest addCommentRequest = new AddCommentRequest();
                    addCommentRequest.setUserComment(commentReplyEditText.getText().toString());
                    if (editType == AppConstants.EDIT_NESTED_REPLY) {
                        commentId = nestedReplyData.getId();
                    } else {
                        commentId = commentsData.getId();
                    }

                    Call<AddCommentResponse> callBookmark = articleDetailsAPI.editComment(commentId, addCommentRequest);
                    callBookmark.enqueue(editCommentsResponseCallback);
                    showProgressDialog("Please wait ...");

                }
                break;
            case R.id.txvCancel:
                dismiss();
                break;
        }
    }

    private Callback<AddCommentResponse> editCommentsResponseCallback = new Callback<AddCommentResponse>() {
        @Override
        public void onResponse(Call<AddCommentResponse> call, retrofit2.Response<AddCommentResponse> response) {
            removeProgressDialog();
            if (response == null || null == response.body()) {
                if ("article".equals(type)) {
                    ((ArticlesAndBlogsDetailsActivity) getActivity()).showToast("Something went wrong from server");
                } else {
                    ((VlogsDetailActivity) getActivity()).showToast("Something went wrong from server");
                }
                return;
            }

            AddCommentResponse responseData = (AddCommentResponse) response.body();
            if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                if ("article".equals(type)) {
                    ((ArticlesAndBlogsDetailsActivity) getActivity()).showToast("Comment edited successfully!");
                } else {
                    ((VlogsDetailActivity) getActivity()).showToast("Comment edited successfully!");
                }

                if (editType == AppConstants.EDIT_NESTED_REPLY) {
                    nestedReplyData.setBody(commentReplyEditText.getText().toString());

                } else {
                    commentsData.setBody(commentReplyEditText.getText().toString());
                }
                commentReplyEditText.setText("");
                if ("article".equals(type)) {
                    ((ArticlesAndBlogsDetailsActivity) getActivity()).updateCommentReplyNestedReply(commentsData, editType);
                } else {
                    ((VlogsDetailActivity) getActivity()).updateCommentReplyNestedReply(commentsData, editType);
                }

                dismiss();
            } else {
                if ("article".equals(type)) {
                    ((ArticlesAndBlogsDetailsActivity) getActivity()).showToast(responseData.getReason());
                } else {
                    ((VlogsDetailActivity) getActivity()).showToast(responseData.getReason());
                }

            }
        }

        @Override
        public void onFailure(Call<AddCommentResponse> call, Throwable t) {
            removeProgressDialog();
            if ("article".equals(type)) {
                ((ArticlesAndBlogsDetailsActivity) getActivity()).handleExceptions(t);
            } else {
                ((VlogsDetailActivity) getActivity()).handleExceptions(t);
            }
        }
    };

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
            Toast.makeText(getActivity(), "The text you have entered is invalid", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    public void handleUiUpdate(Response response) {

    }

    public interface UpdateEditedComment {
        void onCommentUpdated(int position);
    }
}