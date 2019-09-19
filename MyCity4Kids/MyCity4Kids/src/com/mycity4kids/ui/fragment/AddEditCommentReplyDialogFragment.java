package com.mycity4kids.ui.fragment;

import android.accounts.NetworkErrorException;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.kelltontech.ui.BaseActivity;
import com.kelltontech.utils.ConnectivityUtils;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.parentingdetails.CommentsData;
import com.mycity4kids.models.request.AddCommentRequest;
import com.mycity4kids.models.response.AddCommentResponse;
import com.mycity4kids.models.response.ProfilePic;
import com.mycity4kids.models.user.UserInfo;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.ArticleDetailsAPI;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by user on 08-06-2015.
 */
public class AddEditCommentReplyDialogFragment extends DialogFragment implements OnClickListener {

    private ProgressDialog mProgressDialog;
    private ImageView closeImageView;
    private TextView addCommentTextView;
    private EditText commentReplyEditText;
    private TextView replyToTextView;
    private View separator;

    private CommentsData commentsData;
    private String articleId;
    private String operation;
    private String openFrom;
    private String author;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.add_comment_fragment, container,
                false);

        closeImageView = (ImageView) rootView.findViewById(R.id.closeImageView);
        addCommentTextView = (TextView) rootView.findViewById(R.id.addCommentTextView);
        commentReplyEditText = (EditText) rootView.findViewById(R.id.commentReplyEditText);
        replyToTextView = (TextView) rootView.findViewById(R.id.replyToTextView);
        separator = rootView.findViewById(R.id.separator);

        Bundle extras = getArguments();
        if (extras != null) {
            articleId = extras.getString(Constants.ARTICLE_ID);
            author = extras.getString(Constants.AUTHOR);
            commentsData = extras.getParcelable("commentData");
            operation = extras.getString("opType");
            openFrom = extras.getString("type");
        }
        if (commentsData != null && "EDIT".equals(operation)) {
            commentReplyEditText.setText("" + commentsData.getBody());
            replyToTextView.setVisibility(View.GONE);
            separator.setVisibility(View.GONE);
        }
        if ("ADD".equals(operation) && commentsData != null) {
            replyToTextView.setVisibility(View.VISIBLE);
            separator.setVisibility(View.VISIBLE);
            replyToTextView.setText(getString(R.string.ad_comments_replying_to, commentsData.getName()));
        }

//        if (commentsData != null) {
//            commentReplyEditText.setText("" + commentsData.getBody());
//        }
        addCommentTextView.setOnClickListener(this);
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
//            dialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.blue_bg_rounded_corners));
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.addCommentTextView:

                if (!ConnectivityUtils.isNetworkEnabled(getActivity())) {
                    ((BaseActivity) getActivity()).showSnackbar(getView().findViewById(R.id.root), getString(R.string.error_network));
                    return;
                }

                if (isValid()) {
                    if ("ADD".equals(operation)) {
                        String contentData = commentReplyEditText.getText().toString();
                        Retrofit retro = BaseApplication.getInstance().getRetrofit();
                        ArticleDetailsAPI articleDetailsAPI = retro.create(ArticleDetailsAPI.class);
                        AddCommentRequest addCommentRequest = new AddCommentRequest();
                        addCommentRequest.setArticleId(articleId);
                        addCommentRequest.setUserComment(contentData);
                        if (commentsData != null) {
                            addCommentRequest.setParentId(commentsData.getId());
                            Utils.pushReplyCommentArticleEvent(getActivity(), "DetailArticleScreen", SharedPrefUtils.getUserDetailModel(getActivity()).getDynamoId() + "",
                                    articleId, author);
                        } else {
                            Utils.pushCommentArticleEvent(getActivity(), "DetailArticleScreen", SharedPrefUtils.getUserDetailModel(getActivity()).getDynamoId() + "",
                                    articleId, author);
                        }
                        Call<AddCommentResponse> callBookmark = articleDetailsAPI.addComment(addCommentRequest);
                        callBookmark.enqueue(addCommentsResponseCallback);
                        showProgressDialog("Please wait ...");
                    } else {
                        String commentId;
                        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
                        ArticleDetailsAPI articleDetailsAPI = retrofit.create(ArticleDetailsAPI.class);
                        AddCommentRequest addCommentRequest = new AddCommentRequest();
                        addCommentRequest.setUserComment(commentReplyEditText.getText().toString());
                        Call<AddCommentResponse> callBookmark = articleDetailsAPI.editComment(commentsData.getId(), addCommentRequest);
                        callBookmark.enqueue(editCommentsResponseCallback);
                        showProgressDialog("Please wait ...");
                    }
                }
                break;
            case R.id.closeImageView:
                dismiss();
                break;

        }

    }

    private Callback<AddCommentResponse> addCommentsResponseCallback = new Callback<AddCommentResponse>() {
        @Override
        public void onResponse(Call<AddCommentResponse> call, retrofit2.Response<AddCommentResponse> response) {
            removeProgressDialog();
            if (response == null || null == response.body()) {
                NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                Crashlytics.logException(nee);
                return;
            }

            AddCommentResponse responseData = response.body();
            if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                UserInfo userInfo = SharedPrefUtils.getUserDetailModel(getActivity());
                CommentsData cd = new CommentsData();
                cd.setId(responseData.getData().getId());
                cd.setBody(commentReplyEditText.getText().toString().trim());
                cd.setUserId(userInfo.getDynamoId());
                cd.setName(userInfo.getFirst_name() + " " + userInfo.getLast_name());
                if (commentsData != null) {
                    cd.setParent_id(commentsData.getId());
                }
                cd.setReplies(new ArrayList<CommentsData>());

                ProfilePic profilePic = new ProfilePic();
                profilePic.setClientApp(SharedPrefUtils.getProfileImgUrl(BaseApplication.getAppContext()));
                profilePic.setClientAppMin(SharedPrefUtils.getProfileImgUrl(BaseApplication.getAppContext()));
                cd.setProfile_image(profilePic);
                cd.setCreate("" + System.currentTimeMillis() / 1000);
                try {
                    IAddCommentReply addCommentReply = (IAddCommentReply) getTargetFragment();
                    if (commentsData == null) {
                        addCommentReply.onCommentAddition(cd);
                    } else {
                        addCommentReply.onReplyAddition(cd);
                    }
                } catch (Exception e) {

                }
                dismiss();
            } else {
//                showToast(responseData.getReason());
            }
        }

        @Override
        public void onFailure(Call<AddCommentResponse> call, Throwable t) {
            removeProgressDialog();
//            handleExceptions(t);
        }
    };

    private Callback<AddCommentResponse> editCommentsResponseCallback = new Callback<AddCommentResponse>() {
        @Override
        public void onResponse(Call<AddCommentResponse> call, retrofit2.Response<AddCommentResponse> response) {
            removeProgressDialog();
            if (response == null || null == response.body()) {
                return;
            }

            AddCommentResponse responseData = response.body();
            if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
//                if ("article".equals(type)) {
//                    ((ArticlesAndBlogsDetailsActivity) getActivity()).showToast("Comment edited successfully!");
//                } else {
//                    ((VlogsDetailActivity) getActivity()).showToast("Comment edited successfully!");
//                }

//                if (editType == AppConstants.EDIT_NESTED_REPLY) {
//                    nestedReplyData.setBody(commentReplyEditText.getText().toString());
//                } else {
                commentsData.setBody(commentReplyEditText.getText().toString());
//                }
                commentReplyEditText.setText("");
                try {
                    IAddCommentReply addCommentReply = (IAddCommentReply) getTargetFragment();
                    addCommentReply.onCommentReplyEditSuccess(commentsData);
                } catch (Exception e) {

                }
                dismiss();
//                if ("article".equals(type)) {
//                    ((ArticlesAndBlogsDetailsActivity) getActivity()).updateCommentReplyNestedReply(commentsData, editType);
//                } else {
//                    ((VlogsDetailActivity) getActivity()).updateCommentReplyNestedReply(commentsData, editType);
//                }
            } else {
//                if ("article".equals(type)) {
//                    ((ArticlesAndBlogsDetailsActivity) getActivity()).showToast(responseData.getReason());
//                } else {
//                    ((VlogsDetailActivity) getActivity()).showToast(responseData.getReason());
//                }

            }
        }

        @Override
        public void onFailure(Call<AddCommentResponse> call, Throwable t) {
            removeProgressDialog();
//            if ("article".equals(type)) {
//                ((ArticlesAndBlogsDetailsActivity) getActivity()).handleExceptions(t);
//            } else {
//                ((VlogsDetailActivity) getActivity()).handleExceptions(t);
//            }
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

        if (StringUtils.isNullOrEmpty(addCommentTextView.getText().toString())) {
            Toast.makeText(getActivity(), "Please add a reply", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    public interface IAddCommentReply {
        void onCommentAddition(CommentsData cd);

        void onCommentReplyEditSuccess(CommentsData cd);

        void onReplyAddition(CommentsData cd);
    }

}