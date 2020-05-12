package com.mycity4kids.ui.fragment;

import android.accounts.NetworkErrorException;
import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.crashlytics.android.Crashlytics;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.base.BaseActivity;
import com.mycity4kids.base.BaseFragment;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.request.AddEditCommentOrReplyRequest;
import com.mycity4kids.models.response.CommentListData;
import com.mycity4kids.models.response.CommentListResponse;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.ArticleDetailsAPI;
import com.mycity4kids.ui.adapter.ArticleCommentsRecyclerAdapter;
import com.mycity4kids.utils.AppUtils;
import com.mycity4kids.utils.StringUtils;
import com.mycity4kids.utils.ToastUtils;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by user on 08-06-2015.
 */
public class ArticleCommentsFragment extends BaseFragment implements OnClickListener,
        ArticleCommentsRecyclerAdapter.RecyclerViewClickListener,
        CommentOptionsDialogFragment.ICommentOptionAction, AddArticleCommentReplyDialogFragment.AddComments {

    private int pastVisiblesItems;
    private int visibleItemCount;
    private int totalItemCount;
    private String paginationCommentId = null;
    private boolean isReuqestRunning = true;
    private boolean isLastPageReached = false;
    private int totalCommentCount = 0;
    private int downloadedComment = 0;
    private ArrayList<CommentListData> commentsList;
    private ProgressDialog progressDialog;
    private int actionItemPosition;
    private String editContent;
    private String editReplyParentCommentId;
    private String editReplyId;
    private int deleteCommentPos;
    private int deleteReplyPos;

    private String userDynamoId;
    private String articleId;
    private String titleSlug;
    private String blogSlug;
    private String userType;
    private RelativeLayout addCommentFab;
    private RecyclerView commentsRecyclerView;
    private ArticleCommentsRecyclerAdapter articleCommentsRecyclerAdapter;
    private ArticleDetailsAPI articleDetailsApi;
    private ArticleCommentRepliesDialogFragment articleCommentRepliesDialogFragment;
    private TextView noCommentsTextView;
    private String sourceType;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.article_comment_replies_dialog, container,
                false);
        addCommentFab = (RelativeLayout) rootView.findViewById(R.id.addCommentFAB);
        commentsRecyclerView = (RecyclerView) rootView.findViewById(R.id.commentsRecyclerView);
        noCommentsTextView = (TextView) rootView.findViewById(R.id.noCommentsTextView);

        addCommentFab.setOnClickListener(this);

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
            titleSlug = extras.getString(Constants.TITLE_SLUG);
            blogSlug = extras.getString(Constants.BLOG_SLUG);
            userType = extras.getString("userType");
            sourceType = extras.getString("type");
        }

        Retrofit retro = BaseApplication.getInstance().getRetrofit();
        articleDetailsApi = retro.create(ArticleDetailsAPI.class);

        getArticleComments(articleId, null);

        commentsRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
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
        Call<CommentListResponse> call = articleDetailsApi.getArticleComments(id, commentType, paginationCommentId);
        call.enqueue(ssCommentsResponseCallback);
    }

    private Callback<CommentListResponse> ssCommentsResponseCallback = new Callback<CommentListResponse>() {
        @Override
        public void onResponse(Call<CommentListResponse> call, retrofit2.Response<CommentListResponse> response) {
            isReuqestRunning = false;
            if (response.body() == null) {
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
            if (isAdded()) {
                ((BaseActivity) getActivity()).apiExceptions(t);
            }
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
                Bundle args = new Bundle();
                AddArticleCommentReplyDialogFragment addArticleCommentReplyDialogFragment =
                        new AddArticleCommentReplyDialogFragment();
                addArticleCommentReplyDialogFragment.setArguments(args);
                addArticleCommentReplyDialogFragment.setCancelable(true);
                FragmentManager fm = getChildFragmentManager();
                addArticleCommentReplyDialogFragment.show(fm, "Add Comment");
                break;
            default:
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
        Call<CommentListResponse> call = articleDetailsApi.addCommentOrReply(addEditCommentOrReplyRequest);
        call.enqueue(addCommentResponseListener);
    }

    private Callback<CommentListResponse> addCommentResponseListener = new Callback<CommentListResponse>() {
        @Override
        public void onResponse(Call<CommentListResponse> call, retrofit2.Response<CommentListResponse> response) {
            removeProgressDialog();
            if (response.body() == null) {
                NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                Crashlytics.logException(nee);
                if (isAdded()) {
                    ToastUtils.showToast(getActivity(), "Failed to add comment. Please try again");
                }
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
                    if (!StringUtils.isNullOrEmpty(userType) && !StringUtils.isNullOrEmpty(titleSlug) && !StringUtils
                            .isNullOrEmpty(blogSlug)) {
                        String shareUrl = AppUtils.getShareUrl(userType, blogSlug, titleSlug);
                        if (ShareDialog.canShow(ShareLinkContent.class)) {
                            ShareLinkContent content = new ShareLinkContent.Builder()
                                    .setQuote(responseData.getData().get(0).getMessage())
                                    .setContentUrl(Uri.parse(shareUrl))
                                    .build();
                            if (isAdded()) {
                                new ShareDialog(getActivity()).show(content);
                            }
                        }
                    }
                    if (isAdded()) {
                        Utils.pushArticleCommentReplyChangeEvent(getActivity(), "DetailArticleScreen", userDynamoId,
                                articleId, "add", "comment");
                    }
                } else {
                    if (isAdded()) {
                        ToastUtils.showToast(getActivity(), "Failed to add comment. Please try again");
                    }
                }
            } catch (Exception e) {
                if (isAdded()) {
                    ToastUtils.showToast(getActivity(), "Failed to add comment. Please try again");
                }
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<CommentListResponse> call, Throwable t) {
            removeProgressDialog();
            if (isAdded()) {
                ToastUtils.showToast(getActivity(), "Failed to add comment. Please try again");
            }
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
        Call<CommentListResponse> call = articleDetailsApi.editCommentOrReply(responseId, addEditCommentOrReplyRequest);
        call.enqueue(editCommentResponseListener);
    }

    private Callback<CommentListResponse> editCommentResponseListener = new Callback<CommentListResponse>() {
        @Override
        public void onResponse(Call<CommentListResponse> call, retrofit2.Response<CommentListResponse> response) {
            removeProgressDialog();
            if (response.body() == null) {
                NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                Crashlytics.logException(nee);
                if (isAdded()) {
                    ToastUtils.showToast(getActivity(), "Failed to add comment. Please try again");
                }

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
                    if (isAdded()) {
                        Utils.pushArticleCommentReplyChangeEvent(getActivity(), "DetailArticleScreen", userDynamoId,
                                articleId, "edit", "comment");
                    }
                } else {
                    if (isAdded()) {
                        ToastUtils.showToast(getActivity(), "Failed to add comment. Please try again");
                    }
                }
            } catch (Exception e) {
                if (isAdded()) {
                    ToastUtils.showToast(getActivity(), "Failed to add comment. Please try again");
                }
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<CommentListResponse> call, Throwable t) {
            removeProgressDialog();
            if (isAdded()) {
                ToastUtils.showToast(getActivity(), "Failed to add comment. Please try again");
            }
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    @Override
    public void onResponseDelete(int position, String responseType) {
        Call<CommentListResponse> call = articleDetailsApi.deleteCommentOrReply(commentsList.get(position).get_id());
        call.enqueue(deleteCommentResponseListener);
        actionItemPosition = position;
    }

    @Override
    public void onResponseEdit(int position, String responseType) {
        Bundle args = new Bundle();
        args.putString("action", "EDIT_COMMENT");
        args.putParcelable("parentCommentData", commentsList.get(position));
        args.putInt("position", position);
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
        args.putString("postId", commentsList.get(position).get_id());
        args.putInt("type", AppConstants.REPORT_TYPE_COMMENT);
        ReportContentDialogFragment reportContentDialogFragment = new ReportContentDialogFragment();
        reportContentDialogFragment.setArguments(args);
        reportContentDialogFragment.setCancelable(true);
        FragmentManager fm = getChildFragmentManager();
        reportContentDialogFragment.show(fm, "Report Content");
    }

    private Callback<CommentListResponse> deleteCommentResponseListener = new Callback<CommentListResponse>() {
        @Override
        public void onResponse(Call<CommentListResponse> call, retrofit2.Response<CommentListResponse> response) {
            removeProgressDialog();
            if (response.body() == null) {
                NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                Crashlytics.logException(nee);
                if (isAdded()) {
                    ToastUtils.showToast(getActivity(), "Failed to add comment. Please try again");
                }
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
                    if (isAdded()) {
                        Utils.pushArticleCommentReplyChangeEvent(getActivity(), "DetailArticleScreen", userDynamoId,
                                articleId, "delete", "comment");
                    }
                } else {
                    if (isAdded()) {
                        ToastUtils.showToast(getActivity(), "Failed to add comment. Please try again");
                    }
                }
            } catch (Exception e) {
                if (isAdded()) {
                    ToastUtils.showToast(getActivity(), "Failed to add comment. Please try again");
                }
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<CommentListResponse> call, Throwable t) {
            removeProgressDialog();
            if (isAdded()) {
                ToastUtils.showToast(getActivity(), "Failed to add comment. Please try again");
            }
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    void addReply(String content, String parentCommentId) {
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
        Call<CommentListResponse> call = articleDetailsApi.addCommentOrReply(addEditCommentOrReplyRequest);
        call.enqueue(addReplyResponseListener);
    }

    private Callback<CommentListResponse> addReplyResponseListener = new Callback<CommentListResponse>() {
        @Override
        public void onResponse(Call<CommentListResponse> call, retrofit2.Response<CommentListResponse> response) {
            removeProgressDialog();
            if (response.body() == null) {
                NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                Crashlytics.logException(nee);
                if (isAdded()) {
                    ToastUtils.showToast(getActivity(), "Failed to add comment. Please try again");
                }
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
                    if (isAdded()) {
                        Utils.pushArticleCommentReplyChangeEvent(getActivity(), "DetailArticleScreen", userDynamoId,
                                articleId, "add", "reply");
                    }
                } else {
                    if (isAdded()) {
                        ToastUtils.showToast(getActivity(), "Failed to add comment. Please try again");
                    }
                }
            } catch (Exception e) {
                if (isAdded()) {
                    ToastUtils.showToast(getActivity(), "Failed to add comment. Please try again");
                }
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<CommentListResponse> call, Throwable t) {
            removeProgressDialog();
            if (isAdded()) {
                ToastUtils.showToast(getActivity(), "Failed to add comment. Please try again");
            }
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };


    void editReply(String content, String parentCommentId, String replyId) {
        showProgressDialog("Editing Reply");
        AddEditCommentOrReplyRequest addEditCommentOrReplyRequest = new AddEditCommentOrReplyRequest();
        addEditCommentOrReplyRequest.setPost_id(articleId);
        addEditCommentOrReplyRequest.setMessage(content);
        Call<CommentListResponse> call = articleDetailsApi.editCommentOrReply(replyId, addEditCommentOrReplyRequest);
        call.enqueue(editReplyResponseListener);
        editReplyId = replyId;
        editReplyParentCommentId = parentCommentId;
        editContent = content;
    }

    private Callback<CommentListResponse> editReplyResponseListener = new Callback<CommentListResponse>() {
        @Override
        public void onResponse(Call<CommentListResponse> call, retrofit2.Response<CommentListResponse> response) {
            removeProgressDialog();
            if (response.body() == null) {
                NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                Crashlytics.logException(nee);
                if (isAdded()) {
                    ToastUtils.showToast(getActivity(), "Failed to add comment. Please try again");
                }
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
                    if (isAdded()) {
                        Utils.pushArticleCommentReplyChangeEvent(getActivity(), "DetailArticleScreen", userDynamoId,
                                articleId, "edit", "reply");
                    }
                } else {
                    if (isAdded()) {
                        ToastUtils.showToast(getActivity(), "Failed to add comment. Please try again");
                    }
                }
            } catch (Exception e) {
                if (isAdded()) {
                    ToastUtils.showToast(getActivity(), "Failed to add comment. Please try again");
                }
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<CommentListResponse> call, Throwable t) {
            if (isAdded()) {
                ToastUtils.showToast(getActivity(), "Failed to add comment. Please try again");
            }
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    void deleteReply(int commentPos, int replyPos) {
        deleteCommentPos = commentPos;
        deleteReplyPos = replyPos;
        Call<CommentListResponse> call = articleDetailsApi
                .deleteCommentOrReply(commentsList.get(commentPos).getReplies().get(replyPos).get_id());
        call.enqueue(deleteReplyResponseListener);
    }

    private Callback<CommentListResponse> deleteReplyResponseListener = new Callback<CommentListResponse>() {
        @Override
        public void onResponse(Call<CommentListResponse> call, retrofit2.Response<CommentListResponse> response) {
            removeProgressDialog();
            if (response.body() == null) {
                NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                Crashlytics.logException(nee);
                if (isAdded()) {
                    ToastUtils.showToast(getActivity(), "Failed to add comment. Please try again");
                }
                return;
            }
            try {
                CommentListResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    commentsList.get(deleteCommentPos).getReplies().remove(deleteReplyPos);
                    commentsList.get(deleteCommentPos)
                            .setReplies_count(commentsList.get(deleteCommentPos).getReplies_count() - 1);
                    if (articleCommentRepliesDialogFragment != null) {
                        articleCommentRepliesDialogFragment.updateRepliesList(commentsList.get(deleteCommentPos));
                        if (commentsList.get(deleteCommentPos).getReplies_count() == 0) {
                            articleCommentRepliesDialogFragment.dismiss();
                        }
                    }
                    articleCommentsRecyclerAdapter.notifyDataSetChanged();
                    if (isAdded()) {
                        Utils.pushArticleCommentReplyChangeEvent(getActivity(), "DetailArticleScreen", userDynamoId,
                                articleId, "delete", "reply");
                    }
                } else {
                    if (isAdded()) {
                        ToastUtils.showToast(getActivity(), "Failed to add comment. Please try again");
                    }
                }
            } catch (Exception e) {
                if (isAdded()) {
                    ToastUtils.showToast(getActivity(), "Failed to add comment. Please try again");
                }
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<CommentListResponse> call, Throwable t) {
            removeProgressDialog();
            if (isAdded()) {
                ToastUtils.showToast(getActivity(), "Failed to add comment. Please try again");
            }
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

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
        switch (view.getId()) {
            case R.id.commentRootLayout: {
                Bundle args = new Bundle();
                args.putInt("position", position);
                args.putString("authorId", commentsList.get(position).getUserId());
                args.putString("responseType", "COMMENT");
                CommentOptionsDialogFragment commentOptionsDialogFragment = new CommentOptionsDialogFragment();
                commentOptionsDialogFragment.setArguments(args);
                commentOptionsDialogFragment.setCancelable(true);
                FragmentManager fm = getChildFragmentManager();
                commentOptionsDialogFragment.show(fm, "Comment Options");
            }
            break;
            case R.id.replyCommentTextView: {
                openAddCommentReplyDialog(commentsList.get(position));
            }
            break;
            case R.id.replyCountTextView: {
                Bundle args = new Bundle();
                args.putParcelable("commentReplies", commentsList.get(position));
                args.putInt("totalRepliesCount", commentsList.get(position).getReplies_count());
                args.putInt("position", position);
                articleCommentRepliesDialogFragment = new ArticleCommentRepliesDialogFragment();
                articleCommentRepliesDialogFragment.setArguments(args);
                articleCommentRepliesDialogFragment.setCancelable(true);
                FragmentManager fm = getChildFragmentManager();
                articleCommentRepliesDialogFragment.show(fm, "View Replies");
            }
            break;
            default:
                break;
        }
    }

    void openAddCommentReplyDialog(CommentListData commentData) {
        Bundle args = new Bundle();
        args.putParcelable("parentCommentData", commentData);
        AddArticleCommentReplyDialogFragment addArticleCommentReplyDialogFragment =
                new AddArticleCommentReplyDialogFragment();
        addArticleCommentReplyDialogFragment.setArguments(args);
        addArticleCommentReplyDialogFragment.setCancelable(true);
        FragmentManager fm = getChildFragmentManager();
        addArticleCommentReplyDialogFragment.show(fm, "Add Replies");
    }

    @Override
    public void addComments(String content) {
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
        Call<CommentListResponse> call = articleDetailsApi.addCommentOrReply(addEditCommentOrReplyRequest);
        call.enqueue(addCommentResponseListener);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
