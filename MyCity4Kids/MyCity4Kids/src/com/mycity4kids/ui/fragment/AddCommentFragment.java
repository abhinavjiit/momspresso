package com.mycity4kids.ui.fragment;

import android.accounts.NetworkErrorException;
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
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.kelltontech.ui.BaseFragment;
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
import com.mycity4kids.models.response.ProfilePic;
import com.mycity4kids.newmodels.AttendeeModel;
import com.mycity4kids.preference.SharedPrefUtils;
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
public class AddCommentFragment extends BaseFragment implements OnClickListener {

    ArrayList<AttendeeModel> data;
    ListView replyListView;
    CommentsReplyAdapter adapter;
    private CommentsData commentsData;
    private LinearLayout addCommentLayout, commentLayout;
    private EditText addReplyEditText;
    Toolbar mToolbar;
    ArrayList<CommentsData> completeReplies = new ArrayList<>();
    String articleId;
    String parentId;
    private ProgressDialog mProgressDialog;
    int pos;
    private int replyLevelFlag = 1;
    private String type = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.add_comment_fragment, container,
                false);
        mToolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        mToolbar.setTitle("Replies");
//        mToolbar.setTitleTextColor(ContextCompat.getColor(getActivity(), R.color.colorControlNormal));
//        Drawable upArrow = ContextCompat.getDrawable(getActivity(), R.drawable.back_arroow);
//        upArrow.setColorFilter(ContextCompat.getColor(getActivity(), R.color.colorControlNormal), PorterDuff.Mode.SRC_ATOP);
//        mToolbar.setNavigationIcon(upArrow);
//        mToolbar.setNavigationOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // handle back button naviagtion
//                dismiss();
//            }
//        });

        replyListView = (ListView) rootView.findViewById(R.id.replyListView);
        addCommentLayout = (LinearLayout) rootView.findViewById(R.id.addCommentLinearLayout);
        addReplyEditText = (EditText) rootView.findViewById(R.id.addReplyEditText);
        commentLayout = (LinearLayout) rootView.findViewById(R.id.comment_layout);

        return rootView;
    }

    private void prepareCompleteList(CommentsData cData) {

        if (cData == null) {
            return;
        }
        for (int i = 0; i < cData.getReplies().size(); i++) {
            cData.getReplies().get(i).setCommentLevel(0);
            if (!"fb".equals(cData.getComment_type())) {
                completeReplies.add(cData.getReplies().get(i));
                for (int j = 0; j < cData.getReplies().get(i).getReplies().size(); j++) {
                    cData.getReplies().get(i).getReplies().get(j).setCommentLevel(1);
                    completeReplies.add(cData.getReplies().get(i).getReplies().get(j));
                }
            } else {
                cData.getReplies().get(i).setComment_type("fb");
                completeReplies.add(cData.getReplies().get(i));
            }
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
                    Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
                    ArticleDetailsAPI articleDetailsAPI = retrofit.create(ArticleDetailsAPI.class);
                    AddCommentRequest addCommentRequest = new AddCommentRequest();
                    addCommentRequest.setArticleId(articleId);
                    addCommentRequest.setUserComment(addReplyEditText.getText().toString());
                    addCommentRequest.setParentId(completeReplies.get(pos).getId());
                    parentId = completeReplies.get(pos).getId();
                    Call<AddCommentResponse> callBookmark = articleDetailsAPI.addComment(addCommentRequest);
                    callBookmark.enqueue(addCommentsResponseCallback);
                    showProgressDialog("Please wait ...");
                }
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
                    ((ArticlesAndBlogsDetailsActivity) getActivity()).showToast("Comment added successfully!");
                } else {
                    ((VlogsDetailActivity) getActivity()).showToast("Comment added successfully!");
                }

                if (replyLevelFlag == 1) {
                    replyLevelFlag = 1;
                    updateCommentReplyList(responseData.getData().getId());
                } else {
                    replyLevelFlag = 1;
                    updateReplyReplyList(responseData.getData().getId());
                }

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

    private void updateCommentReplyList(String commentId) {

        CommentsData cData = new CommentsData();
        cData.setId(commentId);
        cData.setCommentLevel(0);
        cData.setBody(addReplyEditText.getText().toString());
        cData.setName(SharedPrefUtils.getUserDetailModel(getActivity()).getFirst_name() + " " + SharedPrefUtils.getUserDetailModel(getActivity()).getLast_name());
        cData.setUserId(SharedPrefUtils.getUserDetailModel(getActivity()).getDynamoId());
        cData.setCreate("" + System.currentTimeMillis() / 1000);
        cData.setParent_id(parentId);
        ProfilePic profilePic = new ProfilePic();
        profilePic.setClientApp(SharedPrefUtils.getProfileImgUrl(getActivity()));
        profilePic.setClientAppMin(SharedPrefUtils.getProfileImgUrl(getActivity()));
        cData.setProfile_image(profilePic);
        ArrayList<CommentsData> replyList = new ArrayList<>();
        cData.setReplies(replyList);

        addReplyEditText.setText("");
        completeReplies.add(cData);
        adapter.notifyDataSetChanged();
        if ("article".equals(type)) {
            ((ArticlesAndBlogsDetailsActivity) getActivity()).onReplyOrNestedReplyAddition(cData, 1);
        } else {
            ((VlogsDetailActivity) getActivity()).onReplyOrNestedReplyAddition(cData, 1);
        }
    }

    private void updateReplyReplyList(String commentId) {

        int parentPosition = 0;

        CommentsData cData = new CommentsData();
        cData.setId(commentId);
        cData.setCommentLevel(1);
        cData.setBody(addReplyEditText.getText().toString());
        cData.setName(SharedPrefUtils.getUserDetailModel(getActivity()).getFirst_name() + " " + SharedPrefUtils.getUserDetailModel(getActivity()).getLast_name());
        cData.setCreate("" + System.currentTimeMillis() / 1000);
        cData.setParent_id(parentId);
        cData.setUserId(SharedPrefUtils.getUserDetailModel(getActivity()).getDynamoId());
        ProfilePic profilePic = new ProfilePic();
        profilePic.setClientApp(SharedPrefUtils.getProfileImgUrl(getActivity()));
        profilePic.setClientAppMin(SharedPrefUtils.getProfileImgUrl(getActivity()));
        cData.setProfile_image(profilePic);
        ArrayList<CommentsData> replyList = new ArrayList<>();
        cData.setReplies(replyList);

        addReplyEditText.setText("");
        for (int i = 0; i < completeReplies.size(); i++) {
            if (completeReplies.get(i).getId().equals(parentId)) {
                //adding size to add the reply at the end of all replies.
                completeReplies.add(i + 1 + completeReplies.get(i).getReplies().size(), cData);
                parentPosition = i;
                break;
            }
        }

        completeReplies.get(parentPosition).getReplies().add(cData);
        adapter.notifyDataSetChanged();
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

    @Override
    protected void updateUi(Response response) {

    }

    private boolean isValid() {

        if (StringUtils.isNullOrEmpty(addReplyEditText.getText().toString())) {
            Toast.makeText(getActivity(), "Please add a reply", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    public void updateAfterReplyEditing(CommentsData cData) {
        completeReplies.clear();
        completeReplies.add(commentsData);
        prepareCompleteList(commentsData);
        adapter.notifyDataSetChanged();
    }
}