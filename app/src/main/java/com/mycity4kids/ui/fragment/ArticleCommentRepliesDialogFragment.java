package com.mycity4kids.ui.fragment;

import android.accounts.NetworkErrorException;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.BlockUserModel;
import com.mycity4kids.models.TopCommentData;
import com.mycity4kids.models.response.CommentListData;
import com.mycity4kids.models.response.CommentListResponse;
import com.mycity4kids.models.response.LikeReactionModel;
import com.mycity4kids.profile.UserProfileActivity;
import com.mycity4kids.retrofitAPIsInterfaces.ArticleDetailsAPI;
import com.mycity4kids.ui.activity.ArticleDetailsContainerActivity;
import com.mycity4kids.ui.activity.ParallelFeedActivity;
import com.mycity4kids.ui.activity.ShortStoryContainerActivity;
import com.mycity4kids.ui.adapter.CommentRepliesRecyclerAdapter;
import com.mycity4kids.utils.ToastUtils;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import java.util.ArrayList;
import java.util.List;
import okhttp3.ResponseBody;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ArticleCommentRepliesDialogFragment extends DialogFragment implements View.OnClickListener,
        CommentRepliesRecyclerAdapter.RecyclerViewClickListener,
        CommentOptionsDialogFragment.ICommentOptionAction {

    private int pastVisiblesItems;
    private int visibleItemCount;
    private int totalItemCount;
    private boolean isReuqestRunning = false;
    private boolean isLastPageReached = false;
    private CommentListData data;
    private ArrayList<CommentListData> repliesList;

    private Toolbar toolbar;
    private RecyclerView repliesRecyclerView;
    private ProgressDialog progressDialog;

    private CommentRepliesRecyclerAdapter commentRepliesRecyclerAdapter;
    private int totalRepliesCount;
    private FloatingActionButton openAddReplyDialog;
    private String paginationReplyId;
    private int downloadedReplies = 0;
    private int commentPosition;
    private String authorId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.short_story_comment_replies_dialog, container,
                false);
        toolbar = rootView.findViewById(R.id.toolbar);
        repliesRecyclerView = rootView.findViewById(R.id.repliesRecyclerView);
        openAddReplyDialog = rootView.findViewById(R.id.openAddReplyDialog);

        Drawable upArrow = ContextCompat.getDrawable(getActivity(), R.drawable.back_arroow);
        upArrow.setColorFilter(ContextCompat.getColor(getActivity(), R.color.colorControlNormal),
                PorterDuff.Mode.SRC_ATOP);
        toolbar.setNavigationIcon(upArrow);
        toolbar.setNavigationOnClickListener(v -> dismiss());

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
            authorId = extras.getString("blogWriterId");
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
                paginationReplyId = data.getReplies().get(data.getReplies().size() - 1).getId();
            }
        } else {
            isLastPageReached = true;
        }

        commentRepliesRecyclerAdapter = new CommentRepliesRecyclerAdapter(getActivity(), this, authorId);
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
        ArticleDetailsAPI articleDetailsApi = retro.create(ArticleDetailsAPI.class);
        Call<CommentListResponse> call = articleDetailsApi
                .getArticleCommentReplies(data.getPostId(), "reply", data.getId(), paginationReplyId);
        call.enqueue(articleCommentRepliesCallback);
    }

    private Callback<CommentListResponse> articleCommentRepliesCallback = new Callback<CommentListResponse>() {
        @Override
        public void onResponse(Call<CommentListResponse> call, retrofit2.Response<CommentListResponse> response) {
            isReuqestRunning = false;
            if (response.body() == null) {
                NetworkErrorException nee = new NetworkErrorException("Trending Article API failure");
                FirebaseCrashlytics.getInstance().recordException(nee);
                return;
            }

            try {
                CommentListResponse commentListResponse = response.body();
                showReplies(commentListResponse.getData());
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<CommentListResponse> call, Throwable t) {
            isReuqestRunning = false;
            FirebaseCrashlytics.getInstance().recordException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    private void showReplies(List<CommentListData> replyList) {
        if (replyList.size() == 0) {
            isLastPageReached = false;
            if (null != repliesList && !repliesList.isEmpty()) {
                isLastPageReached = true;
            }
        } else {
            repliesList.addAll(replyList);
            commentRepliesRecyclerAdapter.setData(repliesList);
            paginationReplyId = replyList.get(replyList.size() - 1).getId();
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
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    public void showProgressDialog(String bodyText) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            progressDialog.setCancelable(false);
        }

        progressDialog.setMessage(bodyText);

        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
    }

    public void removeProgressDialog() {
        try {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onRecyclerItemClick(View view, int position) {
        try {
            if (view.getId() == R.id.topCommentMarkedTextView) {
                if (repliesList.get(position).isTopCommentMarked()) {
                    TopCommentData commentListData = new TopCommentData(repliesList.get(position).getPostId(),
                            repliesList.get(position).getId(), false);
                    markedUnMarkedTopComment(commentListData);
                    repliesList.get(position).setTopCommentMarked(false);
                } else {
                    TopCommentData commentListData = new TopCommentData(repliesList.get(position).getPostId(),
                            repliesList.get(position).getId(), true);
                    markedUnMarkedTopComment(commentListData);
                    repliesList.get(position).setTopCommentMarked(true);
                }
                commentRepliesRecyclerAdapter.notifyDataSetChanged();
            }
            if (view.getId() == R.id.likeTextView) {
                if (repliesList.get(position).getLiked()) {
                    repliesList.get(position).setLiked(false);
                    LikeReactionModel commentListData = new LikeReactionModel();
                    commentListData.setReaction("like");
                    commentListData.setStatus("0");
                    repliesList.get(position).setLikeCount(repliesList.get(position).getLikeCount() - 1);
                    Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
                    ArticleDetailsAPI articleDetailsApi = retrofit.create(ArticleDetailsAPI.class);
                    Call<ResponseBody> call = articleDetailsApi
                            .likeDislikeComment(repliesList.get(position).getId(), commentListData);
                    call.enqueue(likeDisLikeCommentCallback);
                } else {
                    pushEvent("CD_Like_Comment");
                    repliesList.get(position).setLiked(true);
                    LikeReactionModel commentListData = new LikeReactionModel();
                    commentListData.setReaction("like");
                    commentListData.setStatus("1");
                    repliesList.get(position).setLikeCount(repliesList.get(position).getLikeCount() + 1);
                    Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
                    ArticleDetailsAPI articleDetailsApi = retrofit.create(ArticleDetailsAPI.class);
                    Call<ResponseBody> call = articleDetailsApi
                            .likeDislikeComment(repliesList.get(position).getId(), commentListData);
                    call.enqueue(likeDisLikeCommentCallback);
                }
                commentRepliesRecyclerAdapter.notifyDataSetChanged();
            } else if (view.getId() == R.id.commentorImageView) {
                Intent intent = new Intent(getActivity(), UserProfileActivity.class);
                intent.putExtra(Constants.USER_ID, repliesList.get(position).getUserId());
                startActivity(intent);
            } else if (view.getId() == R.id.replyCommentTextView) {
                pushEvent("CD_Reply_Comment");
                Fragment parentFragment = getParentFragment();
                if (parentFragment instanceof ArticleCommentsFragment) {
                    ((ArticleCommentsFragment) getParentFragment())
                            .openAddCommentReplyDialog(data, repliesList.get(position));
                } else if (parentFragment instanceof ArticleDetailsFragment) {
                    ((ArticleDetailsFragment) getParentFragment())
                            .openAddCommentReplyDialog(data, repliesList.get(position));
                }
            }
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            Log.d("MC4kException", Log.getStackTraceString(e));
        }
    }

    private void pushEvent(String eventSuffix) {
        try {
            if (getActivity() instanceof ArticleDetailsContainerActivity) {
                Utils.shareEventTracking(getActivity(), "Article Detail", "Comment_Android",
                        "ArticleDetail_" + eventSuffix);
            } else if (getActivity() instanceof ShortStoryContainerActivity) {
                Utils.shareEventTracking(getActivity(), "100WS Detail", "Comment_Android",
                        "StoryDetail_" + eventSuffix);
            } else if (getActivity() instanceof ParallelFeedActivity) {
                Utils.shareEventTracking(getActivity(), "Video Detail", "Comment_Android",
                        "VlogDetail_" + eventSuffix);
            }
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            Log.d("MC4kException", Log.getStackTraceString(e));
        }
    }

    @Override
    public void onRecyclerItemLongClick(View view, int position) {
        switch (view.getId()) {
            case R.id.moreOptionImageView:
            case R.id.commentRootLayout: {
                Bundle args = new Bundle();
                args.putInt("position", position);
                args.putString("responseType", "COMMENT");
                args.putString("authorId", repliesList.get(position).getUserId());
                CommentOptionsDialogFragment commentOptionsDialogFragment = new CommentOptionsDialogFragment();
                commentOptionsDialogFragment.setArguments(args);
                commentOptionsDialogFragment.setCancelable(true);
                FragmentManager fm = getChildFragmentManager();
                commentOptionsDialogFragment.show(fm, "Comment Options");
            }
            break;
            case R.id.moreOptionRepliesImageView:
            case R.id.replyRootView: {
                Bundle args = new Bundle();
                args.putInt("position", position);
                args.putString("responseType", "REPLY");
                args.putString("authorId", repliesList.get(position).getUserId());
                CommentOptionsDialogFragment commentOptionsDialogFragment = new CommentOptionsDialogFragment();
                commentOptionsDialogFragment.setArguments(args);
                commentOptionsDialogFragment.setCancelable(true);
                FragmentManager fm = getChildFragmentManager();
                commentOptionsDialogFragment.show(fm, "Comment Options");
            }
            break;
            default:
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.openAddReplyDialog: {
                pushEvent("CD_Comment");
                Fragment parentFragment = getParentFragment();
                if (parentFragment instanceof ArticleCommentsFragment) {
                    ((ArticleCommentsFragment) getParentFragment()).openAddCommentReplyDialog(data, null);
                } else if (parentFragment instanceof ArticleDetailsFragment) {
                    ((ArticleDetailsFragment) getParentFragment()).openAddCommentReplyDialog(data, null);
                }
            }
            break;
            default:
                break;
        }
    }

    private void markedUnMarkedTopComment(TopCommentData commentListData) {
        BaseApplication.getInstance().getRetrofit().create(ArticleDetailsAPI.class).markedTopComment(commentListData)
                .subscribeOn(
                        Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                new Observer<ResponseBody>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ResponseBody responseBody) {
                        Log.d("MARKED--UNMARKED", responseBody.toString());
                    }

                    @Override
                    public void onError(Throwable e) {
                        FirebaseCrashlytics.getInstance().recordException(e);
                        Log.d("MC4kException", Log.getStackTraceString(e));

                    }

                    @Override
                    public void onComplete() {

                    }
                });


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
            if (getParentFragment() instanceof ArticleCommentsFragment) {
                ((ArticleCommentsFragment) getParentFragment()).deleteReply(commentPosition, position - 1);
            } else if (getParentFragment() instanceof ArticleDetailsFragment) {
                ((ArticleDetailsFragment) getParentFragment()).deleteReply(commentPosition, position - 1);
            }
        } else {
            ((ArticleCommentsFragment) getParentFragment()).onResponseDelete(commentPosition, "COMMENT");
        }

    }

    @Override
    public void onResponseEdit(int position, String responseType) {
        Bundle args = new Bundle();
        if (position == 0) {
            args.putString("action", "EDIT_COMMENT");
            args.putInt("position", commentPosition);
        } else {
            args.putString("action", "EDIT_REPLY");
            args.putInt("position", position);
        }
        args.putParcelable("parentCommentData", repliesList.get(position));
        AddArticleCommentReplyDialogFragment addArticleCommentReplyDialogFragment =
                new AddArticleCommentReplyDialogFragment();
        addArticleCommentReplyDialogFragment.setArguments(args);
        addArticleCommentReplyDialogFragment.setCancelable(true);
        FragmentManager fm = getChildFragmentManager();
        addArticleCommentReplyDialogFragment.show(fm, "Add Comment");
    }

    @Override
    public void onResponseReport(int position, String responseType) {
        Bundle args = new Bundle();
        args.putString("postId", repliesList.get(position).getId());
        args.putInt("type", AppConstants.REPORT_TYPE_COMMENT);
        ReportContentDialogFragment reportContentDialogFragment = new ReportContentDialogFragment();
        reportContentDialogFragment.setArguments(args);
        reportContentDialogFragment.setCancelable(true);
        FragmentManager fm = getChildFragmentManager();
        reportContentDialogFragment.show(fm, "Report Content");
    }


    @Override
    public void onBlockUser(int position, String responseType) {
        showProgressDialog("please wait");
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        ArticleDetailsAPI articleDetailsAPI = retrofit.create(ArticleDetailsAPI.class);
        BlockUserModel blockUserModel = new BlockUserModel();
        blockUserModel.setBlocked_user_id(repliesList.get(position).getUserId());
        Call<ResponseBody> call = articleDetailsAPI.blockUserApi(blockUserModel);
        call.enqueue(blockUserCallBack);
    }

    private Callback<ResponseBody> blockUserCallBack = new Callback<ResponseBody>() {
        @Override
        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
            removeProgressDialog();
            if (response.body() == null) {
                NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                FirebaseCrashlytics.getInstance().recordException(nee);
                if (isAdded()) {
                    ToastUtils.showToast(getActivity(), "Please try again");
                }
                return;
            }

            try {
                String resData = new String(response.body().bytes());
                JSONObject jsonObject = new JSONObject(resData);
                if (jsonObject.getInt("code") == 200 && jsonObject.getString("status").equals(Constants.SUCCESS)) {
                    ToastUtils.showToast(getActivity(), jsonObject.getJSONObject("data").getString("msg").toString());
                }


            } catch (Exception t) {
                removeProgressDialog();
                if (isAdded()) {
                    ToastUtils.showToast(getActivity(), "Please try again");
                }
                FirebaseCrashlytics.getInstance().recordException(t);
                Log.d("MC4kException", Log.getStackTraceString(t));
            }


        }

        @Override
        public void onFailure(Call<ResponseBody> call, Throwable t) {
            removeProgressDialog();
            if (isAdded()) {
                ToastUtils.showToast(getActivity(), "Please try again");
            }
            FirebaseCrashlytics.getInstance().recordException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    private Callback<ResponseBody> likeDisLikeCommentCallback = new Callback<ResponseBody>() {
        @Override
        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
        }

        @Override
        public void onFailure(Call<ResponseBody> call, Throwable e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            Log.d("MC4kException", Log.getStackTraceString(e));
        }
    };

}