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
import com.kelltontech.ui.IScreen;
import com.kelltontech.utils.ConnectivityUtils;
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
import com.mycity4kids.ui.adapter.CommentsReplyAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by user on 08-06-2015.
 */
public class CommentRepliesDialogFragment extends DialogFragment implements OnClickListener, IScreen, CommentsReplyAdapter.EditReplyCommentInterface {

    ArrayList<AttendeeModel> data;
    ListView replyListView;
    CommentsReplyAdapter adapter;
    private CommentsData commentsData;
    private LinearLayout addCommentLayout;
    private EditText addReplyEditText;
    Toolbar mToolbar;
    ArrayList<CommentsData> completeReplies = new ArrayList<>();
    String articleId;
    String parentId;
    private ProgressDialog mProgressDialog;
    int pos;
    private int replyLevelFlag = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.comments_replies_dialog, container,
                false);
        mToolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        mToolbar.setTitle("Replies");
        mToolbar.setTitleTextColor(ContextCompat.getColor(getActivity(), R.color.white_color));
        Drawable upArrow = ContextCompat.getDrawable(getActivity(), R.drawable.back_arroow);
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
        adapter = new CommentsReplyAdapter(getActivity(), R.layout.custom_comment_cell, completeReplies, this);
        replyListView.setAdapter(adapter);
//        replyListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
//                final TextView textView = (TextView) view.findViewById(R.id.txvReply);
//                textView.setOnClickListener(new OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        pos = position;
//
//                    }
//                });
//            }
//        });


        return rootView;
    }

    private void prepareCompleteList(CommentsData cData) {

//        for (int i = 0; i < cData.getReplies().size(); i++) {
//            completeReplies.add(cData.getReplies().get(i));
//            prepareCompleteList(cData.getReplies().get(i));
//        }
        if (cData == null) {
            return;
        }
        for (int i = 0; i < cData.getReplies().size(); i++) {
            cData.getReplies().get(i).setCommentLevel(0);
            completeReplies.add(cData.getReplies().get(i));
            for (int j = 0; j < cData.getReplies().get(i).getReplies().size(); j++) {
                cData.getReplies().get(i).getReplies().get(j).setCommentLevel(1);
                completeReplies.add(cData.getReplies().get(i).getReplies().get(j));
            }
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
    public void onReplyButtonClicked(int posit) {
        pos = posit;
        if (posit == 0) {
            Log.d("First Item", "0");
            addReplyEditText.setText("");
            addReplyEditText.requestFocus();
            replyLevelFlag = 1;
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(addReplyEditText, InputMethodManager.SHOW_IMPLICIT);
        } else {
            Log.d("Other Item", "" + posit);
            addReplyEditText.setText("");
            addReplyEditText.requestFocus();
            replyLevelFlag = 2;
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(addReplyEditText, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    @Override
    public void onEditButtonClicked(int posit) {
        pos = posit;
        try {
            EditCommentsRepliesFragment editCommentsRepliesFragment = new EditCommentsRepliesFragment();
            Bundle _args = new Bundle();
            _args.putString("articleId", articleId);
            if (completeReplies.get(pos).getCommentLevel() == 0) {
                _args.putInt(AppConstants.COMMENT_OR_REPLY_OR_NESTED_REPLY, AppConstants.EDIT_REPLY);
                _args.putParcelable("commentData", completeReplies.get(pos));
            } else {
                _args.putInt(AppConstants.COMMENT_OR_REPLY_OR_NESTED_REPLY, AppConstants.EDIT_NESTED_REPLY);
                int temp = 0;
                for (int i = 0; i < completeReplies.size(); i++) {
                    if (completeReplies.get(i).getId().equals(completeReplies.get(pos).getParent_id())) {
                        temp = i;
                        break;
                    }
                }
                _args.putParcelable("commentData", completeReplies.get(temp));
                _args.putParcelable("replyData", completeReplies.get(pos));
            }
            editCommentsRepliesFragment.setArguments(_args);
            FragmentManager fm = getActivity().getSupportFragmentManager();
            editCommentsRepliesFragment.show(fm, "Replies");
        } catch (Exception e) {
            Crashlytics.logException(e);
            Log.d("MC4kException", Log.getStackTraceString(e));
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
//                    if (replyLevelFlag == 1) {
                    addCommentRequest.setParentId(completeReplies.get(pos).getId());
//                    } else {
//
//                    }
//                    addCommentRequest.setParentId(parentId);
                    parentId = completeReplies.get(pos).getId();
                    Call<AddCommentResponse> callBookmark = articleDetailsAPI.addComment(addCommentRequest);
                    callBookmark.enqueue(addCommentsResponseCallback);

                }
                break;
        }

    }

    private Callback<AddCommentResponse> addCommentsResponseCallback = new Callback<AddCommentResponse>() {
        @Override
        public void onResponse(Call<AddCommentResponse> call, retrofit2.Response<AddCommentResponse> response) {

            if (response == null || null == response.body()) {
                ((ArticlesAndBlogsDetailsActivity) getActivity()).showToast("Something went wrong from server");
                return;
            }

            AddCommentResponse responseData = (AddCommentResponse) response.body();
            if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
//            if (response.isSuccessful()) {
                ((ArticlesAndBlogsDetailsActivity) getActivity()).showToast("Comment added successfully!");
                if (replyLevelFlag == 1) {
                    replyLevelFlag = 1;
                    updateCommentReplyList(responseData.getData().getId());
                } else {
                    replyLevelFlag = 1;
                    updateReplyReplyList(responseData.getData().getId());
                }

            } else {
                ((ArticlesAndBlogsDetailsActivity) getActivity()).showToast(responseData.getReason());
            }
        }

        @Override
        public void onFailure(Call<AddCommentResponse> call, Throwable t) {
            ((ArticlesAndBlogsDetailsActivity) getActivity()).handleExceptions(t);
        }
    };

    private void updateCommentReplyList(String commentId) {

        CommentsData cData = new CommentsData();
        cData.setId(commentId);
        cData.setCommentLevel(0);
        cData.setBody(addReplyEditText.getText().toString());
        cData.setName(SharedPrefUtils.getUserDetailModel(getActivity()).getFirst_name());
        cData.setUserId(SharedPrefUtils.getUserDetailModel(getActivity()).getDynamoId());
        cData.setCreate("" + System.currentTimeMillis());
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
        ((ArticlesAndBlogsDetailsActivity) getActivity()).onReplyOrNestedReplyAddition(cData, 1);
    }

    private void updateReplyReplyList(String commentId) {

        int parentPosition = 0;

        CommentsData cData = new CommentsData();
        cData.setId(commentId);
        cData.setCommentLevel(1);
        cData.setBody(addReplyEditText.getText().toString());
        cData.setName(SharedPrefUtils.getUserDetailModel(getActivity()).getFirst_name());
        cData.setCreate("" + System.currentTimeMillis());
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
                completeReplies.add(i + 1, cData);
                parentPosition = i;
                break;
            }
        }
//        completeReplies.add(cData);
        completeReplies.get(parentPosition).getReplies().add(cData);
        adapter.notifyDataSetChanged();
        ((ArticlesAndBlogsDetailsActivity) getActivity()).onReplyOrNestedReplyAddition(completeReplies.get(parentPosition), 2);
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
    }

    public void updateAfterReplyEditing(CommentsData cData) {
        completeReplies.clear();
        completeReplies.add(commentsData);
        prepareCompleteList(commentsData);
        adapter.notifyDataSetChanged();
    }
}