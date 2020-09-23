package com.mycity4kids.ui.fragment;

import android.accounts.NetworkErrorException;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Selection;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.base.BaseActivity;
import com.mycity4kids.base.BaseFragment;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.interfaces.CommentPostButtonColorChangeInterface;
import com.mycity4kids.models.BlockUserModel;
import com.mycity4kids.models.TopCommentData;
import com.mycity4kids.models.request.AddEditCommentOrReplyRequest;
import com.mycity4kids.models.response.CommentListData;
import com.mycity4kids.models.response.CommentListResponse;
import com.mycity4kids.models.response.LikeReactionModel;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.profile.UserProfileActivity;
import com.mycity4kids.retrofitAPIsInterfaces.ArticleDetailsAPI;
import com.mycity4kids.retrofitAPIsInterfaces.SearchArticlesAuthorsAPI;
import com.mycity4kids.tagging.Mentions;
import com.mycity4kids.tagging.MentionsResponse;
import com.mycity4kids.tagging.mentions.MentionSpan;
import com.mycity4kids.tagging.mentions.MentionsEditable;
import com.mycity4kids.tagging.suggestions.SuggestionsResult;
import com.mycity4kids.tagging.tokenization.QueryToken;
import com.mycity4kids.tagging.tokenization.interfaces.QueryTokenReceiver;
import com.mycity4kids.tagging.ui.RichEditorView;
import com.mycity4kids.ui.activity.ArticleDetailsContainerActivity;
import com.mycity4kids.ui.activity.ParallelFeedActivity;
import com.mycity4kids.ui.activity.ShortStoryContainerActivity;
import com.mycity4kids.ui.adapter.ArticleCommentsRecyclerAdapter;
import com.mycity4kids.ui.fragment.AddArticleCommentReplyDialogFragment.MentionIndex;
import com.mycity4kids.utils.AppUtils;
import com.mycity4kids.utils.StringUtils;
import com.mycity4kids.utils.ToastUtils;
import com.mycity4kids.widget.CustomFontTextView;
import com.squareup.picasso.Picasso;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import okhttp3.ResponseBody;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by user on 08-06-2015.
 */
public class ArticleCommentsFragment extends BaseFragment implements OnClickListener,
        ArticleCommentsRecyclerAdapter.RecyclerViewClickListener,
        CommentOptionsDialogFragment.ICommentOptionAction, AddArticleCommentReplyDialogFragment.AddComments,
        QueryTokenReceiver, CommentPostButtonColorChangeInterface {

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
    //   private RelativeLayout addCommentFab;
    private RecyclerView commentsRecyclerView;
    private ArticleCommentsRecyclerAdapter articleCommentsRecyclerAdapter;
    private ArticleDetailsAPI articleDetailsApi;
    private ArticleCommentRepliesDialogFragment articleCommentRepliesDialogFragment;
    private TextView noCommentsTextView;
    private String contentType;
    private ImageView userImageView;
    private String authorId;
    private int pos;
    private RichEditorView typeHere;
    private ImageView disableStatePostTextView;
    private LinearLayout suggestionContainer;
    private HorizontalScrollView horizontalCommentSuggestionsContainer;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.article_comment_replies_dialog, container,
                false);
        userImageView = rootView.findViewById(R.id.userImageView);
        //    addCommentFab = (RelativeLayout) rootView.findViewById(R.id.addCommentFAB);
        commentsRecyclerView = (RecyclerView) rootView.findViewById(R.id.commentsRecyclerView);
        noCommentsTextView = (TextView) rootView.findViewById(R.id.noCommentsTextView);
        disableStatePostTextView = rootView.findViewById(R.id.disableStatePostTextView);
        typeHere = rootView.findViewById(R.id.typeHere);
        suggestionContainer = rootView.findViewById(R.id.suggestionContainer);
        horizontalCommentSuggestionsContainer = rootView.findViewById(R.id.horizontalCommentSuggestionsContainer);
        typeHere.setMaxLines();
        typeHere.displayTextCounter(false);
        typeHere.setOnClickListener(this);
        //   addCommentFab.setOnClickListener(this);
        disableStatePostTextView.setOnClickListener(this);
        typeHere.requestFocus();
        typeHere.setQueryTokenReceiver(this);
        typeHere.changeButtonColorOnTextChanged(this);
        if (getContext() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getContext()
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        }
        userDynamoId = SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId();

        final LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        commentsRecyclerView.setLayoutManager(llm);

        Bundle extras = getArguments();
        if (extras != null) {
            articleId = extras.getString(Constants.ARTICLE_ID);
            titleSlug = extras.getString(Constants.TITLE_SLUG);
            blogSlug = extras.getString(Constants.BLOG_SLUG);
            userType = extras.getString("userType");
            contentType = extras.getString("contentType");
            authorId = extras.getString(Constants.AUTHOR_ID);
        }

        commentsList = new ArrayList<>();
        articleCommentsRecyclerAdapter = new ArticleCommentsRecyclerAdapter(this, authorId);
        articleCommentsRecyclerAdapter.setData(commentsList);
        commentsRecyclerView.setAdapter(articleCommentsRecyclerAdapter);

        Retrofit retro = BaseApplication.getInstance().getRetrofit();
        articleDetailsApi = retro.create(ArticleDetailsAPI.class);

        getArticleComments(articleId, null);

        if (SharedPrefUtils.getCommentSuggestionsVisibilityFlag(BaseApplication.getAppContext())) {
            getCommentSuggestions();
        } else {
            horizontalCommentSuggestionsContainer.setVisibility(View.GONE);
        }

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
        try {
            Picasso.get().load(SharedPrefUtils.getProfileImgUrl(BaseApplication.getAppContext()))
                    .error(R.drawable.default_commentor_img).into(userImageView);
        } catch (Exception e) {
            Picasso.get().load(R.drawable.default_commentor_img).into(userImageView);
        }
        return rootView;
    }

    private void getCommentSuggestions() {
        Call<ResponseBody> call = articleDetailsApi.getCommentSuggestions(articleId);
        call.enqueue(commentSuggestinsListCallback);
    }

    private Callback<ResponseBody> commentSuggestinsListCallback = new Callback<ResponseBody>() {
        @Override
        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
            if (response.body() == null) {
                NetworkErrorException nee = new NetworkErrorException("New comments API failure");
                FirebaseCrashlytics.getInstance().recordException(nee);
                return;
            }

            try {
                String resData = new String(response.body().bytes());
                JSONObject jsonObject = new JSONObject((resData));
                int code = jsonObject.getInt("code");
                String status = jsonObject.getString("status");
                if (code == 200 && Constants.SUCCESS.equals(status)) {

                    JSONObject data = jsonObject.getJSONObject("data");
                    JSONArray result = data.getJSONArray("result");
                    setHorizontalCommentSuggestions(result);
                }


            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<ResponseBody> call, Throwable t) {
            if (isAdded()) {
                ((BaseActivity) getActivity()).apiExceptions(t);
            }
            FirebaseCrashlytics.getInstance().recordException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };


    private void setHorizontalCommentSuggestions(JSONArray result) {//suggestionContainer
        for (int i = 0; i < result.length(); i++) {
            try {
                CustomFontTextView textView = new CustomFontTextView(getActivity());
                MarginLayoutParams params = new MarginLayoutParams(MarginLayoutParams.WRAP_CONTENT,
                        MarginLayoutParams.WRAP_CONTENT);
                params.leftMargin = 25;
                textView.setLayoutParams(params);
                textView.setText(result.getString(i));
                textView.setTag(result.getString(i));
                textView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        MentionsEditable commentText = typeHere.getText();
                        commentText.append(view.getTag().toString());
                        Selection.setSelection(commentText, commentText.length());
                        SharedPrefUtils.setCommentSuggestionsVisibilityFlag(BaseApplication.getAppContext(), false);


                    }
                });
                Typeface face = Typeface.createFromAsset(getResources().getAssets(), "fonts/Roboto-Regular.ttf");
                textView.setTypeface(face);
                textView.setTextSize(14f);
                textView.setTextColor(getResources().getColor(R.color.campaign_4A4A4A));
                textView.setPadding(10, 10, 10, 10);
                textView.setBackground(getResources().getDrawable(R.drawable.comment_suggestions_background_layout));

                suggestionContainer.addView(textView);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

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
                FirebaseCrashlytics.getInstance().recordException(nee);
                return;
            }

            try {
                CommentListResponse commentListResponse = response.body();
                if (commentListResponse.getCount() != 0) {
                    totalCommentCount = commentListResponse.getCount();
                }
                showComments(commentListResponse.getData());
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<CommentListResponse> call, Throwable t) {
            isReuqestRunning = false;
            if (isAdded()) {
                ((BaseActivity) getActivity()).apiExceptions(t);
            }
            FirebaseCrashlytics.getInstance().recordException(t);
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
            noCommentsTextView.setVisibility(View.GONE);
            for (int i = 0; i < commentList.size(); i++) {
                commentsList.add(commentList.get(i));
            }
            articleCommentsRecyclerAdapter.setData(commentsList);
            paginationCommentId = commentList.get(commentList.size() - 1).getId();
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
            //   case R.id.addCommentFAB:
            case R.id.typeHere:
                pushEvent("CD_Comment");
                Bundle args = new Bundle();
                AddArticleCommentReplyDialogFragment addArticleCommentReplyDialogFragment =
                        new AddArticleCommentReplyDialogFragment();
                addArticleCommentReplyDialogFragment.setArguments(args);
                addArticleCommentReplyDialogFragment.setCancelable(true);
                FragmentManager fm = getChildFragmentManager();
                addArticleCommentReplyDialogFragment.show(fm, "Add Comment");
                break;
            case R.id.disableStatePostTextView: {
                if (isValid()) {
                    formatMentionDataForApiRequest();
                    InputMethodManager imm = (InputMethodManager) getContext()
                            .getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(typeHere.getWindowToken(), 0);
                }
            }

            default:
                break;
        }
    }

    private boolean isValid() {
        if (StringUtils.isNullOrEmpty(typeHere.getText().toString())) {
            if (isAdded()) {
                Toast.makeText(getActivity(), getString(R.string.ad_comments_toast_empty_comment), Toast.LENGTH_LONG)
                        .show();
            }
            return false;
        }
        return true;
    }


    private void formatMentionDataForApiRequest() {
        Map<String, Mentions> mentionsMap = new HashMap<>();
        StringBuilder commentBody = new StringBuilder();
        try {
            MentionsEditable mentionsEditable = typeHere.getText();
            List<MentionIndex> marker = new ArrayList<>();
            marker.add(new MentionIndex(0, null));
            List<MentionSpan> mentionsList = typeHere.getMentionSpans();
            for (int i = 0; i < mentionsList.size(); i++) {
                Mentions mention = (Mentions) mentionsList.get(i).getMention();
                marker.add(new MentionIndex(mentionsEditable.getSpanStart(mentionsList.get(i)),
                        mention));
                mentionsMap.put(mention.getUserId(), mention);
            }
            marker.add(new MentionIndex(mentionsEditable.length(), null));
            Collections.sort(marker);
            ArrayList<MentionIndex> splittedComment = new ArrayList<>();
            for (int i = 0; i < marker.size() - 1; i++) {
                CharSequence value = mentionsEditable
                        .subSequence(marker.get(i).index, marker.get(i + 1).index);
                splittedComment.add(new MentionIndex(value, marker.get(i).mention));
            }
            for (int i = 0; i < splittedComment.size(); i++) {
                if (splittedComment.get(i).mention != null) {
                    commentBody.append(org.apache.commons.lang3.StringUtils
                            .replaceFirst(splittedComment.get(i).charSequence.toString(),
                                    splittedComment.get(i).mention.getName(),
                                    "[~userId:" + splittedComment.get(i).mention.getUserId() + "]"));
                } else {
                    commentBody.append(splittedComment.get(i).charSequence);
                }
            }
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            Log.d("MC4kException", Log.getStackTraceString(e));
        }
        addComment(commentBody.toString(), mentionsMap);
    }


    private Callback<CommentListResponse> addCommentResponseListener = new Callback<CommentListResponse>() {
        @Override
        public void onResponse(Call<CommentListResponse> call, retrofit2.Response<CommentListResponse> response) {
            removeProgressDialog();
            if (response.body() == null) {
                NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                FirebaseCrashlytics.getInstance().recordException(nee);
                if (isAdded()) {
                    ToastUtils.showToast(getActivity(), "Failed to add comment. Please try again");
                }
                return;
            }
            try {
                CommentListResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    typeHere.setText("");
                    CommentListData commentModel = new CommentListData();
                    commentModel.setId(responseData.getData().get(0).getId());
                    commentModel.setMessage(responseData.getData().get(0).getMessage());
                    commentModel.setCreatedTime(responseData.getData().get(0).getCreatedTime());
                    commentModel.setPostId(responseData.getData().get(0).getPostId());
                    commentModel.setMentions(responseData.getData().get(0).getMentions());
                    commentModel.setParentCommentId("0");
                    commentModel.setReplies(new ArrayList<>());
                    commentModel.setRepliesCount(0);
                    commentModel.setUserPic(responseData.getData().get(0).getUserPic());
                    commentModel.setUserName(responseData.getData().get(0).getUserName());
                    commentModel.setUserId(responseData.getData().get(0).getUserId());

                    commentsList.add(0, commentModel);
                    if (noCommentsTextView.getVisibility() == View.VISIBLE) {
                        noCommentsTextView.setVisibility(View.GONE);
                    }
                    articleCommentsRecyclerAdapter.notifyDataSetChanged();

                    if (!StringUtils.isNullOrEmpty(userType) && !StringUtils.isNullOrEmpty(titleSlug) && !StringUtils
                            .isNullOrEmpty(blogSlug)) {
                        String shareUrl = AppUtils.getShareUrl(userType, blogSlug, titleSlug);
                        shareCommentOnFacebook(shareUrl, responseData.getData().get(0).getMessage(),
                                responseData.getData().get(0).getMentions());
                    }
                    if (isAdded()) {
                        Utils.pushArticleCommentReplyChangeEvent(getActivity(), "DetailArticleScreen", userDynamoId,
                                articleId, "add", "comment");
                    }
                } else {
                    if (isAdded()) {
                        if (responseData.getCode() == 401) {
                            ToastUtils.showToast(getActivity(), responseData.getReason());
                        } else {
                            ToastUtils.showToast(getActivity(), "Failed to add comment. Please try again");
                        }
                    }
                }
            } catch (Exception e) {
                if (isAdded()) {
                    ToastUtils.showToast(getActivity(), "Failed to add comment. Please try again");
                }
                FirebaseCrashlytics.getInstance().recordException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<CommentListResponse> call, Throwable t) {
            removeProgressDialog();
            if (isAdded()) {
                ToastUtils.showToast(getActivity(), "Failed to add comment. Please try again");
            }
            FirebaseCrashlytics.getInstance().recordException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    public void editComment(String content, String responseId, int position,
            Map<String, Mentions> mentions) {
        showProgressDialog("Editing your response");
        actionItemPosition = position;
        editContent = content;
        AddEditCommentOrReplyRequest addEditCommentOrReplyRequest = new AddEditCommentOrReplyRequest();
        addEditCommentOrReplyRequest.setPost_id(articleId);
        addEditCommentOrReplyRequest.setMessage(content);
        addEditCommentOrReplyRequest.setMentions(mentions);
        Call<CommentListResponse> call = articleDetailsApi.editCommentOrReply(responseId, addEditCommentOrReplyRequest);
        call.enqueue(editCommentResponseListener);
    }

    private Callback<CommentListResponse> editCommentResponseListener = new Callback<CommentListResponse>() {
        @Override
        public void onResponse(Call<CommentListResponse> call, retrofit2.Response<CommentListResponse> response) {
            removeProgressDialog();
            if (response.body() == null) {
                NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                FirebaseCrashlytics.getInstance().recordException(nee);
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
                FirebaseCrashlytics.getInstance().recordException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<CommentListResponse> call, Throwable t) {
            removeProgressDialog();
            if (isAdded()) {
                ToastUtils.showToast(getActivity(), "Failed to add comment. Please try again");
            }
            FirebaseCrashlytics.getInstance().recordException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    @Override
    public void onResponseDelete(int position, String responseType) {
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        ArticleDetailsAPI articleDetailsApi = retrofit.create(ArticleDetailsAPI.class);
        Call<CommentListResponse> call = articleDetailsApi.deleteCommentOrReply(commentsList.get(position).getId());
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
        args.putString("postId", commentsList.get(position).getId());
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
        blockUserModel.setBlocked_user_id(commentsList.get(position).getUserId());
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


    private Callback<CommentListResponse> deleteCommentResponseListener = new Callback<CommentListResponse>() {
        @Override
        public void onResponse(Call<CommentListResponse> call, retrofit2.Response<CommentListResponse> response) {
            removeProgressDialog();
            if (response.body() == null) {
                NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                FirebaseCrashlytics.getInstance().recordException(nee);
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
                    if (commentsList.isEmpty()) {
                        noCommentsTextView.setVisibility(View.VISIBLE);
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
                FirebaseCrashlytics.getInstance().recordException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<CommentListResponse> call, Throwable t) {
            removeProgressDialog();
            if (isAdded()) {
                ToastUtils.showToast(getActivity(), "Failed to add comment. Please try again");
            }
            FirebaseCrashlytics.getInstance().recordException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    void addReply(String content, String parentCommentId, Map<String, Mentions> mentionsMap) {
        showProgressDialog("Adding Reply");
        AddEditCommentOrReplyRequest addEditCommentOrReplyRequest = new AddEditCommentOrReplyRequest();
        addEditCommentOrReplyRequest.setPost_id(articleId);
        addEditCommentOrReplyRequest.setMessage(content);
        addEditCommentOrReplyRequest.setMentions(mentionsMap);
        addEditCommentOrReplyRequest.setParent_id(parentCommentId);
        if (AppConstants.CONTENT_TYPE_VIDEO.equals(contentType)) {
            addEditCommentOrReplyRequest.setType("video");
        } else if (AppConstants.CONTENT_TYPE_SHORT_STORY.equals(contentType)) {
            addEditCommentOrReplyRequest.setType("story");
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
                FirebaseCrashlytics.getInstance().recordException(nee);
                if (isAdded()) {
                    ToastUtils.showToast(getActivity(), "Failed to add comment. Please try again");
                }
                return;
            }
            try {
                CommentListResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {

                    CommentListData commentListData = new CommentListData();
                    commentListData.setId(responseData.getData().get(0).getId());
                    commentListData.setMessage(responseData.getData().get(0).getMessage());
                    commentListData.setCreatedTime(responseData.getData().get(0).getCreatedTime());
                    commentListData.setPostId(responseData.getData().get(0).getPostId());
                    commentListData.setParentCommentId(responseData.getData().get(0).getParentCommentId());
                    commentListData.setUserPic(responseData.getData().get(0).getUserPic());
                    commentListData.setUserName(responseData.getData().get(0).getUserName());
                    commentListData.setUserId(responseData.getData().get(0).getUserId());
                    commentListData.setMentions(responseData.getData().get(0).getMentions());

                    for (int i = 0; i < commentsList.size(); i++) {
                        if (commentsList.get(i).getId().equals(responseData.getData().get(0).getParentCommentId())) {
                            commentsList.get(i).getReplies().add(0, commentListData);
                            commentsList.get(i).setRepliesCount(commentsList.get(i).getRepliesCount() + 1);
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
                        if (responseData.getCode() == 401) {
                            ToastUtils.showToast(getActivity(), responseData.getReason());
                        } else {
                            ToastUtils.showToast(getActivity(), "Failed to add comment. Please try again");
                        }
                    }
                }
            } catch (Exception e) {
                if (isAdded()) {
                    ToastUtils.showToast(getActivity(), "Failed to add comment. Please try again");
                }
                FirebaseCrashlytics.getInstance().recordException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<CommentListResponse> call, Throwable t) {
            removeProgressDialog();
            if (isAdded()) {
                ToastUtils.showToast(getActivity(), "Failed to add comment. Please try again");
            }
            FirebaseCrashlytics.getInstance().recordException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };


    void editReply(String content, String parentCommentId, String replyId, Map<String, Mentions> mentionsMap) {
        showProgressDialog("Editing Reply");
        AddEditCommentOrReplyRequest addEditCommentOrReplyRequest = new AddEditCommentOrReplyRequest();
        addEditCommentOrReplyRequest.setPost_id(articleId);
        addEditCommentOrReplyRequest.setMessage(content);
        addEditCommentOrReplyRequest.setMentions(mentionsMap);
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
                FirebaseCrashlytics.getInstance().recordException(nee);
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
                        if (commentsList.get(i).getId().equals(editReplyParentCommentId)) {
                            for (int j = 0; j < commentsList.get(i).getReplies().size(); j++) {
                                if (commentsList.get(i).getReplies().get(j).getId().equals(editReplyId)) {
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
                FirebaseCrashlytics.getInstance().recordException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<CommentListResponse> call, Throwable t) {
            if (isAdded()) {
                ToastUtils.showToast(getActivity(), "Failed to add comment. Please try again");
            }
            FirebaseCrashlytics.getInstance().recordException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    void deleteReply(int commentPos, int replyPos) {
        deleteCommentPos = commentPos;
        deleteReplyPos = replyPos;
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        ArticleDetailsAPI articleDetailsApi = retrofit.create(ArticleDetailsAPI.class);
        Call<CommentListResponse> call = articleDetailsApi
                .deleteCommentOrReply(commentsList.get(commentPos).getReplies().get(replyPos).getId());
        call.enqueue(deleteReplyResponseListener);
    }

    private Callback<CommentListResponse> deleteReplyResponseListener = new Callback<CommentListResponse>() {
        @Override
        public void onResponse(Call<CommentListResponse> call, retrofit2.Response<CommentListResponse> response) {
            removeProgressDialog();
            if (response.body() == null) {
                NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                FirebaseCrashlytics.getInstance().recordException(nee);
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
                            .setRepliesCount(commentsList.get(deleteCommentPos).getRepliesCount() - 1);
                    if (articleCommentRepliesDialogFragment != null) {
                        articleCommentRepliesDialogFragment.updateRepliesList(commentsList.get(deleteCommentPos));
                        if (commentsList.get(deleteCommentPos).getRepliesCount() == 0) {
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
                FirebaseCrashlytics.getInstance().recordException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<CommentListResponse> call, Throwable t) {
            removeProgressDialog();
            if (isAdded()) {
                ToastUtils.showToast(getActivity(), "Failed to add comment. Please try again");
            }
            FirebaseCrashlytics.getInstance().recordException(t);
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
            case R.id.topCommentMarkedTextView:
                if (!commentsList.get(position).isTopCommentMarked()) {
                    TopCommentData commentListData = new TopCommentData(commentsList.get(position).getPostId(),
                            commentsList.get(position).getId(), true);
                    markedUnMarkedTopComment(commentListData);
                    for (int i = 0; i < commentsList.size(); i++) {
                        if (i == position) {
                            commentsList.get(i).setTopCommentMarked(true);
                            commentsList.get(i).setIs_top_comment(false);
                        } else {
                            commentsList.get(i).setTopCommentMarked(false);
                            commentsList.get(i).setIs_top_comment(false);
                        }
                    }
                }
                articleCommentsRecyclerAdapter.notifyDataSetChanged();
                break;
            case R.id.commentorImageView:
                Intent intent = new Intent(getActivity(), UserProfileActivity.class);
                intent.putExtra(Constants.USER_ID, commentsList.get(position).getUserId());
                startActivity(intent);
                break;
            case R.id.likeTextView:
                pos = position;
                if (commentsList.get(position).getLiked()) {
                    LikeReactionModel commentListData = new LikeReactionModel();
                    commentListData.setReaction("like");
                    commentListData.setStatus("0");
                    commentsList.get(position).setLikeCount(commentsList.get(position).getLikeCount() - 1);
                    Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
                    ArticleDetailsAPI articleDetailsApi = retrofit.create(ArticleDetailsAPI.class);
                    Call<ResponseBody> call = articleDetailsApi
                            .likeDislikeComment(commentsList.get(position).getId(), commentListData);
                    call.enqueue(likeDisLikeCommentCallback);
                } else {
                    pushEvent("CD_Like_Comment");
                    LikeReactionModel commentListData = new LikeReactionModel();
                    commentListData.setReaction("like");
                    commentListData.setStatus("1");
                    commentsList.get(position).setLikeCount(commentsList.get(position).getLikeCount() + 1);
                    Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
                    ArticleDetailsAPI articleDetailsApi = retrofit.create(ArticleDetailsAPI.class);
                    Call<ResponseBody> call = articleDetailsApi
                            .likeDislikeComment(commentsList.get(position).getId(), commentListData);
                    call.enqueue(likeDisLikeCommentCallback);
                }
                break;
            case R.id.moreOptionImageView:
            case R.id.commentRootLayout: {
                Bundle args = new Bundle();
                args.putInt("position", position);
                args.putInt("position", position);
                args.putString("authorId", commentsList.get(position).getUserId());
                args.putString("responseType", "COMMENT");
                args.putString("blogWriterId", authorId);
                CommentOptionsDialogFragment commentOptionsDialogFragment = new CommentOptionsDialogFragment(this);
                commentOptionsDialogFragment.setArguments(args);
                commentOptionsDialogFragment.setCancelable(true);
                FragmentManager fm = getChildFragmentManager();
                commentOptionsDialogFragment.show(fm, "Comment Options");
            }
            break;
            case R.id.replyCommentTextView: {
                pushEvent("CD_Reply_Comment");
                if (commentsList.get(position).getRepliesCount() == 0) {
                    openAddCommentReplyDialog(commentsList.get(position), null);
                } else {
                    articleCommentRepliesDialogFragment = new ArticleCommentRepliesDialogFragment();
                    Bundle args = new Bundle();
                    args.putParcelable("commentReplies", commentsList.get(position));
                    args.putInt("totalRepliesCount", commentsList.get(position).getRepliesCount());
                    args.putInt("position", position);
                    args.putString("blogWriterId", authorId);
                    articleCommentRepliesDialogFragment.setArguments(args);
                    articleCommentRepliesDialogFragment.setCancelable(true);
                    FragmentManager fm = getChildFragmentManager();
                    articleCommentRepliesDialogFragment.show(fm, "View Replies");
                }
            }
            break;
            default:
                break;
        }
    }

    private void markedUnMarkedTopComment(TopCommentData commentListData) {
        pushTopCommentEvent();
        BaseApplication.getInstance().getRetrofit().create(ArticleDetailsAPI.class).markedTopComment(commentListData)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseBody>() {
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

    private void pushTopCommentEvent() {
        try {
            if (getActivity() instanceof ArticleDetailsContainerActivity) {
                Utils.shareEventTracking(getActivity(), "Article Detail", "TopComment_Android", "AD_TopComment");
            } else if (getActivity() instanceof ShortStoryContainerActivity) {
                Utils.shareEventTracking(getActivity(), "100WS Detail", "TopComment_Android", "SD_TopComment");
            } else if (getActivity() instanceof ParallelFeedActivity) {
                Utils.shareEventTracking(getActivity(), "Video Detail", "TopComment_Android", "VD_TopComment");
            }
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            Log.d("MC4kException", Log.getStackTraceString(e));
        }
    }

    private Callback<ResponseBody> likeDisLikeCommentCallback = new Callback<ResponseBody>() {
        @Override
        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
            if (null == response.body()) {
                NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                FirebaseCrashlytics.getInstance().recordException(nee);
                if (isAdded()) {
                    ToastUtils.showToast(getActivity(), getResources().getString(R.string.server_went_wrong));
                }
            }
            try {
                String res = new String(response.body().bytes());
                JSONObject responsee = new JSONObject(res);
                if (responsee.getInt("code") == 200 && responsee.get("status").equals("success")) {
                    if (commentsList.get(pos).getLiked()) {
                        commentsList.get(pos).setLiked(false);
                    } else {
                        commentsList.get(pos).setLiked(true);
                    }
                    articleCommentsRecyclerAdapter.notifyDataSetChanged();
                    JSONObject data = responsee.getJSONObject("data");
                    JSONObject result = data.getJSONObject("result");
                    String msg = result.getString("msg");
                    ToastUtils.showToast(getActivity(), msg);
                } else {
                    JSONObject data = responsee.getJSONObject("data");
                    JSONObject result = data.getJSONObject("result");
                    String msg = result.getString("msg");
                    ToastUtils.showToast(getActivity(), msg);
                }
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
                ToastUtils.showToast(getActivity(), e.getMessage());
            }
        }

        @Override
        public void onFailure(Call<ResponseBody> call, Throwable e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            Log.d("MC4kException", Log.getStackTraceString(e));
            ToastUtils.showToast(getActivity(), e.getMessage());


        }
    };

    void openAddCommentReplyDialog(CommentListData commentData, CommentListData currentReplyData) {
        Bundle args = new Bundle();
        args.putParcelable("currentReplyData", currentReplyData);
        args.putParcelable("parentCommentData", commentData);
        AddArticleCommentReplyDialogFragment addArticleCommentReplyDialogFragment =
                new AddArticleCommentReplyDialogFragment();
        addArticleCommentReplyDialogFragment.setArguments(args);
        addArticleCommentReplyDialogFragment.setCancelable(true);
        FragmentManager fm = getChildFragmentManager();
        addArticleCommentReplyDialogFragment.show(fm, "Add Replies");
    }

    @Override
    public void addComments(String content, Map<String, Mentions> mentionsMap) {

    }

    private void addComment(String content, Map<String, Mentions> mentionsMap) {
        showProgressDialog("Adding Comment");
        AddEditCommentOrReplyRequest addEditCommentOrReplyRequest = new AddEditCommentOrReplyRequest();
        addEditCommentOrReplyRequest.setPost_id(articleId);
        addEditCommentOrReplyRequest.setMessage(content);
        addEditCommentOrReplyRequest.setParent_id("0");
        if (contentType.equals("0")) {
            addEditCommentOrReplyRequest.setType("article");
        } else if ("1".equals(contentType)) {
            addEditCommentOrReplyRequest.setType("story");

        } else {
            addEditCommentOrReplyRequest.setType("video");

        }
        addEditCommentOrReplyRequest.setMentions(mentionsMap);
        Call<CommentListResponse> call = articleDetailsApi.addCommentOrReply(addEditCommentOrReplyRequest);
        call.enqueue(addCommentResponseListener);
    }

    @NonNull
    @Override
    public List<String> onQueryReceived(@NonNull QueryToken queryToken) {
        final QueryTokenReceiver receiver = typeHere;
        Retrofit retro = BaseApplication.getInstance().getRetrofit();
        SearchArticlesAuthorsAPI searchArticlesAuthorsApi = retro.create(SearchArticlesAuthorsAPI.class);
        Call<MentionsResponse> call = searchArticlesAuthorsApi.searchUserHandles(queryToken.getKeywords());
        call.enqueue(new Callback<MentionsResponse>() {
            @Override
            public void onResponse(Call<MentionsResponse> call, Response<MentionsResponse> response) {
                try {
                    if (response.isSuccessful()) {
                        MentionsResponse responseModel = response.body();
                        List<Mentions> suggestions = new ArrayList<>(responseModel.getData().getResult());
                        SuggestionsResult result = new SuggestionsResult(queryToken, suggestions);
                        typeHere.onReceiveSuggestionsResult(result, "dddd");
                    }
                } catch (Exception e) {
                    FirebaseCrashlytics.getInstance().recordException(e);
                    Log.d("MC4kException", Log.getStackTraceString(e));
                }
            }

            @Override
            public void onFailure(Call<MentionsResponse> call, Throwable t) {
                FirebaseCrashlytics.getInstance().recordException(t);
                Log.d("MC4kException", Log.getStackTraceString(t));
            }
        });

        return Arrays.asList("dddd");
    }

    @Override
    public void onStop() {
        super.onStop();
        try {
            InputMethodManager imm = (InputMethodManager) getContext()
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(typeHere.getWindowToken(), 0);
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            Log.d("MC4kException", Log.getStackTraceString(e));
        }
    }

    @Override
    public void onTextChanged(@NotNull String text) {
        if (text.isEmpty()) {
            disableStatePostTextView
                    .setImageDrawable(getResources().getDrawable(R.drawable.ic_post_comment_disabled_state));
        } else {
            horizontalCommentSuggestionsContainer.setVisibility(View.GONE);
            SharedPrefUtils.setCommentSuggestionsVisibilityFlag(BaseApplication.getAppContext(), false);
            disableStatePostTextView
                    .setImageDrawable(getResources().getDrawable(R.drawable.ic_post_comment_enabled_state));
        }
    }
}
