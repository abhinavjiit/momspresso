package com.mycity4kids.ui.fragment;

import android.accounts.NetworkErrorException;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.models.response.CommentListData;
import com.mycity4kids.models.response.CommentListResponse;
import com.mycity4kids.retrofitAPIsInterfaces.ArticleDetailsAPI;
import com.mycity4kids.ui.adapter.CommentRepliesRecyclerAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

public class ArticleCommentRepliesDialogFragment extends DialogFragment implements View.OnClickListener, CommentRepliesRecyclerAdapter.RecyclerViewClickListener,
        CommentOptionsDialogFragment.ICommentOptionAction {

    private int pastVisiblesItems, visibleItemCount, totalItemCount;
    private boolean isReuqestRunning = false;
    private boolean isLastPageReached = false;
    private CommentListData data;
    private ArrayList<CommentListData> repliesList;

    private Toolbar mToolbar;
    private RecyclerView repliesRecyclerView;
    private TextView toolbarTitleTextView;
    private ProgressDialog mProgressDialog;

    private CommentRepliesRecyclerAdapter commentRepliesRecyclerAdapter;
    private int totalRepliesCount;
    private FloatingActionButton openAddReplyDialog;
    private String paginationReplyId;
    private int downloadedReplies = 0;
    private int commentPosition;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.short_story_comment_replies_dialog, container,
                false);
        mToolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        repliesRecyclerView = (RecyclerView) rootView.findViewById(R.id.repliesRecyclerView);
        toolbarTitleTextView = (TextView) mToolbar.findViewById(R.id.toolbarTitle);
        openAddReplyDialog = (FloatingActionButton) rootView.findViewById(R.id.openAddReplyDialog);

        Drawable upArrow = ContextCompat.getDrawable(getActivity(), R.drawable.back_arroow);
        upArrow.setColorFilter(ContextCompat.getColor(getActivity(), R.color.colorControlNormal), PorterDuff.Mode.SRC_ATOP);
        mToolbar.setNavigationIcon(upArrow);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // handle back button naviagtion
                dismiss();
            }
        });

        openAddReplyDialog.setOnClickListener(this);

        final LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        repliesRecyclerView.setLayoutManager(llm);

        repliesList = new ArrayList<>();

        Bundle extras = getArguments();
        if (extras != null) {
            data = extras.getParcelable("commentReplies");
            totalRepliesCount = extras.getInt("totalRepliesCount");
            commentPosition = extras.getInt("position");
        }

        repliesList.add(data);
        if (data.getReplies() != null) {
            for (int i = 0; i < data.getReplies().size(); i++) {
                repliesList.add(data.getReplies().get(i));
            }
            if (totalRepliesCount <= data.getReplies().size()) {
                isLastPageReached = true;
            } else {
                downloadedReplies = data.getReplies().size();
                paginationReplyId = data.getReplies().get(data.getReplies().size() - 1).get_id();
            }
        } else {
            isLastPageReached = true;
        }

        commentRepliesRecyclerAdapter = new CommentRepliesRecyclerAdapter(getActivity(), this);
        commentRepliesRecyclerAdapter.setData(repliesList);
        repliesRecyclerView.setAdapter(commentRepliesRecyclerAdapter);

        repliesRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                visibleItemCount = llm.getChildCount();
                totalItemCount = llm.getItemCount();
                pastVisiblesItems = llm.findFirstVisibleItemPosition();

                if (!isReuqestRunning && !isLastPageReached) {
                    if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                        isReuqestRunning = true;
                        getCommentReplies();
                    }
                }
            }
        });

        return rootView;
    }

    private void getCommentReplies() {
        Retrofit retro = BaseApplication.getInstance().getRetrofit();
        ArticleDetailsAPI articleDetailsAPI = retro.create(ArticleDetailsAPI.class);
        Call<CommentListResponse> call = articleDetailsAPI.getArticleCommentReplies(data.getPostId(), "reply", data.get_id(), paginationReplyId);
        call.enqueue(articleCommentRepliesCallback);
    }

    private Callback<CommentListResponse> articleCommentRepliesCallback = new Callback<CommentListResponse>() {
        @Override
        public void onResponse(Call<CommentListResponse> call, retrofit2.Response<CommentListResponse> response) {
            isReuqestRunning = false;
            if (response == null || response.body() == null) {
                NetworkErrorException nee = new NetworkErrorException("Trending Article API failure");
                Crashlytics.logException(nee);
                return;
            }

            try {
                CommentListResponse commentListResponse = response.body();
                showReplies(commentListResponse.getData());
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<CommentListResponse> call, Throwable t) {
            isReuqestRunning = false;
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    private void showReplies(List<CommentListData> replyList) {
        if (replyList.size() == 0) {
            isLastPageReached = false;
            if (null != repliesList && !repliesList.isEmpty()) {
                //No more next results from pagination
                isLastPageReached = true;
            } else {
            }
        } else {
            repliesList.addAll(replyList);
            commentRepliesRecyclerAdapter.setData(repliesList);
            paginationReplyId = replyList.get(replyList.size() - 1).get_id();
            downloadedReplies = downloadedReplies + replyList.size();
            if (downloadedReplies >= totalRepliesCount) {
                isLastPageReached = true;
            }
        }
        commentRepliesRecyclerAdapter.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
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
    public void onRecyclerItemClick(View view, int position) {

    }

    @Override
    public void onRecyclerItemLongClick(View view, int position) {
        switch (view.getId()) {
            case R.id.commentRootLayout: {
                CommentOptionsDialogFragment commentOptionsDialogFragment = new CommentOptionsDialogFragment();
                FragmentManager fm = getChildFragmentManager();
              //  commentOptionsDialogFragment.setTargetFragment(this, 0);
                Bundle _args = new Bundle();
                _args.putInt("position", position);
                _args.putString("responseType", "COMMENT");
                _args.putString("authorId", repliesList.get(position).getUserId());
                commentOptionsDialogFragment.setArguments(_args);
                commentOptionsDialogFragment.setCancelable(true);
                commentOptionsDialogFragment.show(fm, "Comment Options");
            }
            break;
            case R.id.replyRootView: {
                CommentOptionsDialogFragment commentOptionsDialogFragment = new CommentOptionsDialogFragment();
                FragmentManager fm = getChildFragmentManager();
               // commentOptionsDialogFragment.setTargetFragment(this, 0);
                Bundle _args = new Bundle();
                _args.putInt("position", position);
                _args.putString("responseType", "REPLY");
                _args.putString("authorId", repliesList.get(position).getUserId());
                commentOptionsDialogFragment.setArguments(_args);
                commentOptionsDialogFragment.setCancelable(true);
                commentOptionsDialogFragment.show(fm, "Comment Options");
            }
            break;
        }
//        ((ArticleCommentsFragment) getTargetFragment()).openAddCommentReplyDialog(data);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.openAddReplyDialog: {
                ((ArticleCommentsFragment) getParentFragment()).openAddCommentReplyDialog(data);
            }
            break;
        }
    }

    public void updateRepliesList(CommentListData ssComment) {
        if (repliesList != null) {
            repliesList.clear();
            repliesList.add(ssComment);
            if (data.getReplies() != null) {
                for (int i = 0; i < ssComment.getReplies().size(); i++) {
                    repliesList.add(ssComment.getReplies().get(i));
                }
                if (totalRepliesCount <= ssComment.getReplies().size()) {
                    isLastPageReached = true;
                }
            } else {
                isLastPageReached = true;
            }
        }
        commentRepliesRecyclerAdapter.notifyDataSetChanged();
    }

    @Override
    public void onResponseDelete(int position, String responseType) {
        // position-1 to adjust for the comment added on the top of reply list
        if ("REPLY".equals(responseType)) {
            ((ArticleCommentsFragment) getParentFragment()).deleteReply(commentPosition, position - 1);
        } else {
            ((ArticleCommentsFragment) getParentFragment()).onResponseDelete(commentPosition, "COMMENT");
        }

    }

    @Override
    public void onResponseEdit(int position, String responseType) {
        AddArticleCommentReplyDialogFragment addArticleCommentReplyDialogFragment = new AddArticleCommentReplyDialogFragment();
        FragmentManager fm = getChildFragmentManager();
        Bundle _args = new Bundle();
        if (position == 0) {
            _args.putString("action", "EDIT_COMMENT");
            _args.putInt("position", commentPosition);
        } else {
            _args.putString("action", "EDIT_REPLY");
            _args.putInt("position", position);
        }
        _args.putParcelable("parentCommentData", repliesList.get(position));
        addArticleCommentReplyDialogFragment.setArguments(_args);
        addArticleCommentReplyDialogFragment.setCancelable(true);
       // addArticleCommentReplyDialogFragment.setTargetFragment(getParentFragment(), 0);
        addArticleCommentReplyDialogFragment.show(fm, "Add Comment");
    }

    @Override
    public void onResponseReport(int position, String responseType) {
        ReportContentDialogFragment reportContentDialogFragment = new ReportContentDialogFragment();
        FragmentManager fm = getChildFragmentManager();
        Bundle _args = new Bundle();
        _args.putString("postId", repliesList.get(position).get_id());
        _args.putInt("type", AppConstants.REPORT_TYPE_COMMENT);
        reportContentDialogFragment.setArguments(_args);
        reportContentDialogFragment.setCancelable(true);
        //reportContentDialogFragment.setTargetFragment(this, 0);
        reportContentDialogFragment.show(fm, "Report Content");
    }

}