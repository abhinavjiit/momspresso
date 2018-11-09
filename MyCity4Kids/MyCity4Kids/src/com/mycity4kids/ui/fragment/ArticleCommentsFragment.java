package com.mycity4kids.ui.fragment;

import android.accounts.NetworkErrorException;
import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseFragment;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.request.AddEditCommentOrReplyRequest;
import com.mycity4kids.models.response.CommentListData;
import com.mycity4kids.models.response.CommentListResponse;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.ArticleDetailsAPI;
import com.mycity4kids.ui.activity.ArticleDetailsContainerActivity;
import com.mycity4kids.ui.adapter.ArticleCommentsRecyclerAdapter;
import com.mycity4kids.utils.AppUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by user on 08-06-2015.
 */
public class ArticleCommentsFragment extends BaseFragment implements OnClickListener, ArticleCommentsRecyclerAdapter.RecyclerViewClickListener,
        CommentOptionsDialogFragment.ICommentOptionAction {

    private int pastVisiblesItems, visibleItemCount, totalItemCount;
    private String paginationCommentId = null;
    private boolean isReuqestRunning = true;
    private boolean isLastPageReached = false;
    private int totalCommentCount = 0;
    private int downloadedComment = 0;
    private ArrayList<CommentListData> commentsList;
    private ProgressDialog mProgressDialog;
    private int actionItemPosition;
    private String editContent;
    private String editReplyParentCommentId;
    private String editReplyId;
    private int deleteCommentPos;
    private int deleteReplyPos;

    private String userDynamoId;
    private String articleId, author, titleSlug, blogSlug, userType;
    private FloatingActionButton addCommentFAB;
    private RecyclerView commentsRecyclerView;
    private ArticleCommentsRecyclerAdapter articleCommentsRecyclerAdapter;
    private ArticleDetailsAPI articleDetailsAPI;
    private ArticleCommentRepliesDialogFragment articleCommentRepliesDialogFragment;
    private TextView noCommentsTextView;
    private String sourceType;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.article_comment_replies_dialog, container,
                false);
        addCommentFAB = (FloatingActionButton) rootView.findViewById(R.id.addCommentFAB);
        commentsRecyclerView = (RecyclerView) rootView.findViewById(R.id.commentsRecyclerView);
        noCommentsTextView = (TextView) rootView.findViewById(R.id.noCommentsTextView);

        addCommentFAB.setOnClickListener(this);

        userDynamoId = SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId();

        final LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        commentsRecyclerView.setLayoutManager(llm);

        commentsList = new ArrayList<>();
        articleCommentsRecyclerAdapter = new ArticleCommentsRecyclerAdapter(getActivity(), this);
        articleCommentsRecyclerAdapter.setData(commentsList);
        commentsRecyclerView.setAdapter(articleCommentsRecyclerAdapter);

        Bundle extras = getArguments();
        if (extras != null) {
            articleId = extras.getString(Constants.ARTICLE_ID);
            author = extras.getString(Constants.AUTHOR);
            titleSlug = extras.getString(Constants.TITLE_SLUG);
            blogSlug = extras.getString(Constants.BLOG_SLUG);
            userType = extras.getString("userType");
            sourceType = extras.getString("type");
        }

        Retrofit retro = BaseApplication.getInstance().getRetrofit();
        articleDetailsAPI = retro.create(ArticleDetailsAPI.class);

        getArticleComments(articleId, null);

        commentsRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) //check for scroll down
                {
                    visibleItemCount = llm.getChildCount();
                    totalItemCount = llm.getItemCount();
                    pastVisiblesItems = llm.findFirstVisibleItemPosition();

                    if (!isReuqestRunning && !isLastPageReached) {
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                            isReuqestRunning = true;
                            getArticleComments(articleId, "comment");
                        }
                    }
                }
            }
        });
        return rootView;
    }

    private void getArticleComments(String id, String commentType) {
        Call<CommentListResponse> call = articleDetailsAPI.getArticleComments(id, commentType, paginationCommentId);
        call.enqueue(ssCommentsResponseCallback);
    }

    private Callback<CommentListResponse> ssCommentsResponseCallback = new Callback<CommentListResponse>() {
        @Override
        public void onResponse(Call<CommentListResponse> call, retrofit2.Response<CommentListResponse> response) {
            isReuqestRunning = false;
            if (response == null || response.body() == null) {
                NetworkErrorException nee = new NetworkErrorException("New comments API failure");
                Crashlytics.logException(nee);
                return;
            }

            try {
                CommentListResponse commentListResponse = response.body();
                if (commentListResponse.getCount() != 0) {
                    totalCommentCount = commentListResponse.getCount();
                }
                showComments(commentListResponse.getData());
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

    private void showComments(List<CommentListData> commentList) {
        if (commentList.size() == 0) {
            isLastPageReached = false;
            if (null != commentsList && !commentsList.isEmpty()) {
                //No more next results from pagination
                isLastPageReached = true;
            } else {
                noCommentsTextView.setVisibility(View.VISIBLE);
            }
        } else {
            for (int i = 0; i < commentList.size(); i++) {
                commentsList.add(commentList.get(i));
            }
            articleCommentsRecyclerAdapter.setData(commentsList);
            paginationCommentId = commentList.get(commentList.size() - 1).get_id();
            downloadedComment = downloadedComment + commentList.size();
            if (downloadedComment >= totalCommentCount) {
                isLastPageReached = true;
            }
        }
        articleCommentsRecyclerAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.addCommentFAB:
                AddArticleCommentReplyDialogFragment addArticleCommentReplyDialogFragment = new AddArticleCommentReplyDialogFragment();
                FragmentManager fm = getChildFragmentManager();
                Bundle _args = new Bundle();
                addArticleCommentReplyDialogFragment.setArguments(_args);
                addArticleCommentReplyDialogFragment.setCancelable(true);
                addArticleCommentReplyDialogFragment.setTargetFragment(this, 0);
                addArticleCommentReplyDialogFragment.show(fm, "Add Comment");
                break;
        }

    }

    public void addComment(String content) {
        showProgressDialog("Adding Comment");
        AddEditCommentOrReplyRequest addEditCommentOrReplyRequest = new AddEditCommentOrReplyRequest();
        addEditCommentOrReplyRequest.setPost_id(articleId);
        addEditCommentOrReplyRequest.setMessage(content);
        addEditCommentOrReplyRequest.setParent_id("0");
        if ("video".equals(sourceType)) {
            addEditCommentOrReplyRequest.setType("video");
        } else {
            addEditCommentOrReplyRequest.setType("article");
        }
        Call<CommentListResponse> call = articleDetailsAPI.addCommentOrReply(addEditCommentOrReplyRequest);
        call.enqueue(addCommentResponseListener);
    }

    private Callback<CommentListResponse> addCommentResponseListener = new Callback<CommentListResponse>() {
        @Override
        public void onResponse(Call<CommentListResponse> call, retrofit2.Response<CommentListResponse> response) {
            removeProgressDialog();
            if (response == null || response.body() == null) {
                if (response != null && response.raw() != null) {
                    NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                    Crashlytics.logException(nee);
                }
                if (isAdded())
                    ((ArticleDetailsContainerActivity) getActivity()).showToast("Failed to add comment. Please try again");

                return;
            }
            try {
                CommentListResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {

                    CommentListData commentModel = new CommentListData();
                    commentModel.set_id(responseData.getData().get(0).get_id());
                    commentModel.setMessage(responseData.getData().get(0).getMessage());
                    commentModel.setCreatedTime(responseData.getData().get(0).getCreatedTime());
                    commentModel.setPostId(responseData.getData().get(0).getPostId());
                    commentModel.setParentCommentId("0");
                    commentModel.setReplies(new ArrayList<CommentListData>());
                    commentModel.setReplies_count(0);
                    commentModel.setUserPic(responseData.getData().get(0).getUserPic());
                    commentModel.setUserName(responseData.getData().get(0).getUserName());
                    commentModel.setUserId(responseData.getData().get(0).getUserId());

                    commentsList.add(0, commentModel);
                    articleCommentsRecyclerAdapter.notifyDataSetChanged();
                    if (StringUtils.isNullOrEmpty(userType) || StringUtils.isNullOrEmpty(titleSlug) || StringUtils.isNullOrEmpty(blogSlug)) {

                    } else {
                        String shareUrl = AppUtils.getShareUrl(userType, blogSlug, titleSlug);
                        if (ShareDialog.canShow(ShareLinkContent.class)) {
                            ShareLinkContent content = new ShareLinkContent.Builder()
                                    .setQuote(responseData.getData().get(0).getMessage())
                                    .setContentUrl(Uri.parse(shareUrl))
                                    .build();
                            new ShareDialog(ArticleCommentsFragment.this).show(content);
                        }
                    }
                    if (isAdded())
                        Utils.pushArticleCommentReplyChangeEvent(getActivity(), "DetailArticleScreen", userDynamoId, articleId, "add", "comment");
                } else {
                    if (isAdded())
                        ((ArticleDetailsContainerActivity) getActivity()).showToast("Failed to add comment. Please try again");
                }
            } catch (Exception e) {
                if (isAdded())
                    ((ArticleDetailsContainerActivity) getActivity()).showToast("Failed to add comment. Please try again");
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<CommentListResponse> call, Throwable t) {
            removeProgressDialog();
            if (isAdded())
                ((ArticleDetailsContainerActivity) getActivity()).showToast("Failed to add comment. Please try again");
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    public void editComment(String content, String responseId, int position) {
        showProgressDialog("Editing your response");
        actionItemPosition = position;
        editContent = content;
        AddEditCommentOrReplyRequest addEditCommentOrReplyRequest = new AddEditCommentOrReplyRequest();
        addEditCommentOrReplyRequest.setPost_id(articleId);
        addEditCommentOrReplyRequest.setMessage(content);
        Call<CommentListResponse> call = articleDetailsAPI.editCommentOrReply(responseId, addEditCommentOrReplyRequest);
        call.enqueue(editCommentResponseListener);
    }

    private Callback<CommentListResponse> editCommentResponseListener = new Callback<CommentListResponse>() {
        @Override
        public void onResponse(Call<CommentListResponse> call, retrofit2.Response<CommentListResponse> response) {
            removeProgressDialog();
            if (response == null || response.body() == null) {
                if (response != null && response.raw() != null) {
                    NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                    Crashlytics.logException(nee);
                }
                if (isAdded())
                    ((ArticleDetailsContainerActivity) getActivity()).showToast("Failed to edit comment. Please try again");

                return;
            }
            try {
                CommentListResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    commentsList.get(actionItemPosition).setMessage(editContent);
                    if (articleCommentRepliesDialogFragment != null) {
                        articleCommentRepliesDialogFragment.updateRepliesList(commentsList.get(actionItemPosition));
                    }
                    articleCommentsRecyclerAdapter.notifyDataSetChanged();
                    if (isAdded())
                        Utils.pushArticleCommentReplyChangeEvent(getActivity(), "DetailArticleScreen", userDynamoId, articleId, "edit", "comment");
                } else {
                    if (isAdded())
                        ((ArticleDetailsContainerActivity) getActivity()).showToast("Failed to edit comment. Please try again");
                }
            } catch (Exception e) {
                if (isAdded())
                    ((ArticleDetailsContainerActivity) getActivity()).showToast("Failed to edit comment. Please try again");
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<CommentListResponse> call, Throwable t) {
            removeProgressDialog();
            if (isAdded())
                ((ArticleDetailsContainerActivity) getActivity()).showToast("Failed to edit comment. Please try again");
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    @Override
    public void onResponseDelete(int position, String responseType) {
        Call<CommentListResponse> call = articleDetailsAPI.deleteCommentOrReply(commentsList.get(position).get_id());
        call.enqueue(deleteCommentResponseListener);
        actionItemPosition = position;
    }

    @Override
    public void onResponseEdit(int position, String responseType) {
        AddArticleCommentReplyDialogFragment addArticleCommentReplyDialogFragment = new AddArticleCommentReplyDialogFragment();
        FragmentManager fm = getChildFragmentManager();
        Bundle _args = new Bundle();
        _args.putString("action", "EDIT_COMMENT");
        _args.putParcelable("parentCommentData", commentsList.get(position));
        _args.putInt("position", position);
        addArticleCommentReplyDialogFragment.setArguments(_args);
        addArticleCommentReplyDialogFragment.setCancelable(true);
        addArticleCommentReplyDialogFragment.setTargetFragment(this, 0);
        addArticleCommentReplyDialogFragment.show(fm, "Add Comment");
    }

    @Override
    public void onResponseReport(int position, String responseType) {
        ReportContentDialogFragment reportContentDialogFragment = new ReportContentDialogFragment();
        FragmentManager fm = getChildFragmentManager();
        Bundle _args = new Bundle();
        _args.putString("postId", commentsList.get(position).get_id());
        _args.putInt("type", AppConstants.REPORT_TYPE_COMMENT);
        reportContentDialogFragment.setArguments(_args);
        reportContentDialogFragment.setCancelable(true);
        reportContentDialogFragment.setTargetFragment(this, 0);
        reportContentDialogFragment.show(fm, "Report Content");
    }

    private Callback<CommentListResponse> deleteCommentResponseListener = new Callback<CommentListResponse>() {
        @Override
        public void onResponse(Call<CommentListResponse> call, retrofit2.Response<CommentListResponse> response) {
            removeProgressDialog();
            if (response == null || response.body() == null) {
                if (response != null && response.raw() != null) {
                    NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                    Crashlytics.logException(nee);
                }
                if (isAdded())
                    ((ArticleDetailsContainerActivity) getActivity()).showToast("Failed to delete comment. Please try again");

                return;
            }
            try {
                CommentListResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    commentsList.remove(actionItemPosition);
                    if (articleCommentRepliesDialogFragment != null) {
                        articleCommentRepliesDialogFragment.dismiss();
                    }
                    articleCommentsRecyclerAdapter.notifyDataSetChanged();
                    if (isAdded())
                        Utils.pushArticleCommentReplyChangeEvent(getActivity(), "DetailArticleScreen", userDynamoId, articleId, "delete", "comment");
                } else {
                    if (isAdded())
                        ((ArticleDetailsContainerActivity) getActivity()).showToast("Failed to delete comment. Please try again");
                }
            } catch (Exception e) {
                if (isAdded())
                    ((ArticleDetailsContainerActivity) getActivity()).showToast("Failed to delete comment. Please try again");
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<CommentListResponse> call, Throwable t) {
            removeProgressDialog();
            if (isAdded())
                ((ArticleDetailsContainerActivity) getActivity()).showToast("Failed to delete comment. Please try again");
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    public void addReply(String content, String parentCommentId) {
        showProgressDialog("Adding Reply");
        AddEditCommentOrReplyRequest addEditCommentOrReplyRequest = new AddEditCommentOrReplyRequest();
        addEditCommentOrReplyRequest.setPost_id(articleId);
        addEditCommentOrReplyRequest.setMessage(content);
        addEditCommentOrReplyRequest.setParent_id(parentCommentId);
        if ("video".equals(sourceType)) {
            addEditCommentOrReplyRequest.setType("video");
        } else {
            addEditCommentOrReplyRequest.setType("article");
        }
        Call<CommentListResponse> call = articleDetailsAPI.addCommentOrReply(addEditCommentOrReplyRequest);
        call.enqueue(addReplyResponseListener);
    }

    private Callback<CommentListResponse> addReplyResponseListener = new Callback<CommentListResponse>() {
        @Override
        public void onResponse(Call<CommentListResponse> call, retrofit2.Response<CommentListResponse> response) {
            removeProgressDialog();
            if (response == null || response.body() == null) {
                if (response != null && response.raw() != null) {
                    NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                    Crashlytics.logException(nee);
                }
                if (isAdded())
                    ((ArticleDetailsContainerActivity) getActivity()).showToast("Failed to add reply. Please try again");

                return;
            }
            try {
                CommentListResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {

                    CommentListData commentListData = new CommentListData();
                    commentListData.set_id(responseData.getData().get(0).get_id());
                    commentListData.setMessage(responseData.getData().get(0).getMessage());
                    commentListData.setCreatedTime(responseData.getData().get(0).getCreatedTime());
                    commentListData.setPostId(responseData.getData().get(0).getPostId());
                    commentListData.setParentCommentId(responseData.getData().get(0).getParentCommentId());
                    commentListData.setUserPic(responseData.getData().get(0).getUserPic());
                    commentListData.setUserName(responseData.getData().get(0).getUserName());
                    commentListData.setUserId(responseData.getData().get(0).getUserId());

                    for (int i = 0; i < commentsList.size(); i++) {
                        if (commentsList.get(i).get_id().equals(responseData.getData().get(0).getParentCommentId())) {
                            commentsList.get(i).getReplies().add(0, commentListData);
                            commentsList.get(i).setReplies_count(commentsList.get(i).getReplies_count() + 1);
                            if (articleCommentRepliesDialogFragment != null) {
                                articleCommentRepliesDialogFragment.updateRepliesList(commentsList.get(i));
                            }
                            break;
                        }
                    }
                    articleCommentsRecyclerAdapter.notifyDataSetChanged();
                    if (isAdded())
                        Utils.pushArticleCommentReplyChangeEvent(getActivity(), "DetailArticleScreen", userDynamoId, articleId, "add", "reply");
                } else {
                    if (isAdded())
                        ((ArticleDetailsContainerActivity) getActivity()).showToast("Failed to add reply. Please try again");
                }
            } catch (Exception e) {
                if (isAdded())
                    ((ArticleDetailsContainerActivity) getActivity()).showToast("Failed to add reply. Please try again");
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<CommentListResponse> call, Throwable t) {
            removeProgressDialog();
            if (isAdded())
                ((ArticleDetailsContainerActivity) getActivity()).showToast("Failed to add reply. Please try again");
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    public void editReply(String content, String parentCommentId, String replyId) {
        showProgressDialog("Editing Reply");
        AddEditCommentOrReplyRequest addEditCommentOrReplyRequest = new AddEditCommentOrReplyRequest();
        addEditCommentOrReplyRequest.setPost_id(articleId);
        addEditCommentOrReplyRequest.setMessage(content);
        Call<CommentListResponse> call = articleDetailsAPI.editCommentOrReply(replyId, addEditCommentOrReplyRequest);
        call.enqueue(editReplyResponseListener);
        editReplyId = replyId;
        editReplyParentCommentId = parentCommentId;
        editContent = content;
    }

    private Callback<CommentListResponse> editReplyResponseListener = new Callback<CommentListResponse>() {
        @Override
        public void onResponse(Call<CommentListResponse> call, retrofit2.Response<CommentListResponse> response) {
            removeProgressDialog();
            if (response == null || response.body() == null) {
                if (response != null && response.raw() != null) {
                    NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                    Crashlytics.logException(nee);
                }
                if (isAdded())
                    ((ArticleDetailsContainerActivity) getActivity()).showToast("Failed to edit reply. Please try again");

                return;
            }
            try {
                CommentListResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    boolean isReplyUpdated = false;
                    for (int i = 0; i < commentsList.size(); i++) {
                        if (commentsList.get(i).get_id().equals(editReplyParentCommentId)) {
                            for (int j = 0; j < commentsList.get(i).getReplies().size(); j++) {
                                if (commentsList.get(i).getReplies().get(j).get_id().equals(editReplyId)) {
                                    commentsList.get(i).getReplies().get(j).setMessage(editContent);
                                    if (articleCommentRepliesDialogFragment != null) {
                                        articleCommentRepliesDialogFragment.updateRepliesList(commentsList.get(i));
                                    }
                                    isReplyUpdated = true;
                                    break;
                                }
                            }
                        }
                        if (isReplyUpdated) {
                            break;
                        }
                    }
                    articleCommentsRecyclerAdapter.notifyDataSetChanged();
                    if (isAdded())
                        Utils.pushArticleCommentReplyChangeEvent(getActivity(), "DetailArticleScreen", userDynamoId, articleId, "edit", "reply");
                } else {
                    if (isAdded())
                        ((ArticleDetailsContainerActivity) getActivity()).showToast("Failed to edit reply. Please try again");
                }
            } catch (Exception e) {
                if (isAdded())
                    ((ArticleDetailsContainerActivity) getActivity()).showToast("Failed to edit reply. Please try again");
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<CommentListResponse> call, Throwable t) {
            if (isAdded())
                ((ArticleDetailsContainerActivity) getActivity()).showToast("Failed to edit reply. Please try again");
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    public void deleteReply(int commentPos, int replyPos) {
        deleteCommentPos = commentPos;
        deleteReplyPos = replyPos;
        Call<CommentListResponse> call = articleDetailsAPI.deleteCommentOrReply(commentsList.get(commentPos).getReplies().get(replyPos).get_id());
        call.enqueue(deleteReplyResponseListener);
    }

    private Callback<CommentListResponse> deleteReplyResponseListener = new Callback<CommentListResponse>() {
        @Override
        public void onResponse(Call<CommentListResponse> call, retrofit2.Response<CommentListResponse> response) {
            removeProgressDialog();
            if (response == null || response.body() == null) {
                if (response != null && response.raw() != null) {
                    NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                    Crashlytics.logException(nee);
                }
                if (isAdded())
                    ((ArticleDetailsContainerActivity) getActivity()).showToast("Failed to delete reply. Please try again");

                return;
            }
            try {
                CommentListResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    commentsList.get(deleteCommentPos).getReplies().remove(deleteReplyPos);
                    commentsList.get(deleteCommentPos).setReplies_count(commentsList.get(deleteCommentPos).getReplies_count() - 1);
                    if (articleCommentRepliesDialogFragment != null) {
                        articleCommentRepliesDialogFragment.updateRepliesList(commentsList.get(deleteCommentPos));
                        if (commentsList.get(deleteCommentPos).getReplies_count() == 0) {
                            articleCommentRepliesDialogFragment.dismiss();
                        }
                    }
                    articleCommentsRecyclerAdapter.notifyDataSetChanged();
                    if (isAdded())
                        Utils.pushArticleCommentReplyChangeEvent(getActivity(), "DetailArticleScreen", userDynamoId, articleId, "delete", "reply");
                } else {
                    if (isAdded())
                        ((ArticleDetailsContainerActivity) getActivity()).showToast("Failed to delete reply. Please try again");
                }
            } catch (Exception e) {
                if (isAdded())
                    ((ArticleDetailsContainerActivity) getActivity()).showToast("Failed to delete reply. Please try again");
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<CommentListResponse> call, Throwable t) {
            removeProgressDialog();
            if (isAdded())
                ((ArticleDetailsContainerActivity) getActivity()).showToast("Failed to delete reply. Please try again");
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
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

    @Override
    protected void updateUi(Response response) {

    }

    @Override
    public void onRecyclerItemClick(View view, int position) {
        switch (view.getId()) {
            case R.id.commentRootLayout: {
                CommentOptionsDialogFragment commentOptionsDialogFragment = new CommentOptionsDialogFragment();
                FragmentManager fm = getChildFragmentManager();
                commentOptionsDialogFragment.setTargetFragment(this, 0);
                Bundle _args = new Bundle();
                _args.putInt("position", position);
                _args.putString("authorId", commentsList.get(position).getUserId());
                _args.putString("responseType", "COMMENT");
                commentOptionsDialogFragment.setArguments(_args);
                commentOptionsDialogFragment.setCancelable(true);
                commentOptionsDialogFragment.show(fm, "Comment Options");
            }
            break;
            case R.id.replyCommentTextView: {
                openAddCommentReplyDialog(commentsList.get(position));
            }
            break;
            case R.id.replyCountTextView: {
                articleCommentRepliesDialogFragment = new ArticleCommentRepliesDialogFragment();
                FragmentManager fm = getChildFragmentManager();
                Bundle _args = new Bundle();
                _args.putParcelable("commentReplies", commentsList.get(position));
                _args.putInt("totalRepliesCount", commentsList.get(position).getReplies_count());
                _args.putInt("position", position);
                articleCommentRepliesDialogFragment.setArguments(_args);
                articleCommentRepliesDialogFragment.setCancelable(true);
                articleCommentRepliesDialogFragment.setTargetFragment(this, 0);
                articleCommentRepliesDialogFragment.show(fm, "View Replies");
            }
            break;
        }
    }

    public void openAddCommentReplyDialog(CommentListData cData) {
        AddArticleCommentReplyDialogFragment addArticleCommentReplyDialogFragment = new AddArticleCommentReplyDialogFragment();
        FragmentManager fm = getChildFragmentManager();
        Bundle _args = new Bundle();
        _args.putParcelable("parentCommentData", cData);
        addArticleCommentReplyDialogFragment.setArguments(_args);
        addArticleCommentReplyDialogFragment.setCancelable(true);
        addArticleCommentReplyDialogFragment.setTargetFragment(this, 0);
        addArticleCommentReplyDialogFragment.show(fm, "Add Replies");
    }

}