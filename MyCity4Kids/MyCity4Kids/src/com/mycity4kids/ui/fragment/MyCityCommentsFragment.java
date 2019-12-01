package com.mycity4kids.ui.fragment;

import android.accounts.NetworkErrorException;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseFragment;
import com.kelltontech.utils.ConnectivityUtils;
import com.kelltontech.utils.DateTimeUtils;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.models.parentingdetails.CommentsData;
import com.mycity4kids.models.response.FBCommentResponse;
import com.mycity4kids.observablescrollview.ObservableScrollView;
import com.mycity4kids.observablescrollview.ObservableScrollViewCallbacks;
import com.mycity4kids.observablescrollview.ScrollState;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.profile.UserProfileActivity;
import com.mycity4kids.retrofitAPIsInterfaces.ArticleDetailsAPI;
import com.mycity4kids.ui.activity.ArticleDetailsContainerActivity;
import com.mycity4kids.utils.AppUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by user on 08-06-2015.
 */
public class MyCityCommentsFragment extends BaseFragment implements OnClickListener, ObservableScrollViewCallbacks, AddEditCommentReplyFragment.IAddCommentReply, AddEditCommentReplyDialogFragment.IAddCommentReply {

    private final static int REPLY_LEVEL_PARENT = 1;
    private final static int REPLY_LEVEL_CHILD = 2;

    private Toolbar mToolbar;
    private ProgressDialog mProgressDialog;
    private ImageView closeImageView;
    private TextView addCommentTextView, replyToTextView;
    private View separator;
    private EditText commentReplyEditText;
    private LinearLayout commentLayout;
    private ObservableScrollView mScrollView;
    private TextView commentHeading;
    //    private FloatingActionButton commentFloatingActionButton;
    private TextView noCommentsTextView;

    private boolean isLoading;
    private boolean isFbCommentHeadingAdded = false;
    private CommentsData commentsData;
    private String operation;
    private String author;
    private String commentURL;
    private String userDynamoId;
    private String pagination = "";
    private String commentType = "db";
    private String articleId;
    private String type;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.view_all_comment_fragment, container,
                false);
        mScrollView = (ObservableScrollView) rootView.findViewById(R.id.scroll_view);
        commentLayout = ((LinearLayout) rootView.findViewById(R.id.commnetLout));
        commentHeading = ((TextView) rootView.findViewById(R.id.commentsHeading));
        noCommentsTextView = ((TextView) rootView.findViewById(R.id.noCommentsTextView));
//        commentFloatingActionButton = (FloatingActionButton) rootView.findViewById(R.id.commentFloatingActionButton);
//        commentFloatingActionButton.setOnClickListener(this);

        userDynamoId = SharedPrefUtils.getUserDetailModel(getActivity()).getDynamoId();

        mScrollView.setScrollViewCallbacks(this);

        Bundle extras = getArguments();
        if (extras != null) {
            commentURL = extras.getString("commentURL");
            articleId = extras.getString(Constants.ARTICLE_ID);
            author = extras.getString(Constants.AUTHOR);
            type = extras.getString("type");
        }
        if (!StringUtils.isNullOrEmpty(commentURL) && commentURL.contains("http")) {
            getMoreComments();
        } else {
            commentType = "fb";
            commentURL = "http";
            getMoreComments();
        }
        return rootView;
    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        View view = mScrollView.getChildAt(mScrollView.getChildCount() - 1);
        Rect scrollBounds = new Rect();
        mScrollView.getHitRect(scrollBounds);
        int diff = (view.getBottom() - (mScrollView.getHeight() + mScrollView.getScrollY()));
        if (diff <= 10 && !isLoading && !StringUtils.isNullOrEmpty(commentURL) && commentURL.contains("http") && !AppConstants.PAGINATION_END_VALUE.equals(pagination)) {
            getMoreComments();
        }
    }

    @Override
    public void onDownMotionEvent() {

    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
//            case R.id.commentFloatingActionButton:
//                openCommentDialog(null, "ADD");
//                break;
//            case R.id.txvCommentCellReply:
//                openCommentDialog((CommentsData) ((View) v.getParent().getParent().getParent()).getTag(), "ADD");
//                break;
//            case R.id.txvReplyCellReply:
//                openCommentDialog((CommentsData) ((View) v.getParent().getParent()).getTag(), "ADD");
//                break;
            case R.id.txvCommentCellEdit: {
                CommentsData cData = (CommentsData) ((View) v.getParent().getParent().getParent()).getTag();
                openCommentDialog(cData, "EDIT");
            }
            break;
            case R.id.txvReplyCellEdit: {
                CommentsData cData = (CommentsData) ((View) v.getParent().getParent()).getTag();
                openCommentDialog(cData, "EDIT");
            }
            break;
            case R.id.txvCommentTitle:
            case R.id.commentorImageView: {
                CommentsData commentData = (CommentsData) ((View) v.getParent().getParent()).getTag();
                if (!"fb".equals(commentData.getComment_type())) {
                    Intent profileIntent = new Intent(getActivity(), UserProfileActivity.class);
                    profileIntent.putExtra(Constants.USER_ID, commentData.getUserId());
                    profileIntent.putExtra(AppConstants.AUTHOR_NAME, commentData.getName());
                    profileIntent.putExtra(Constants.FROM_SCREEN, "Article Detail Comments");
                    startActivity(profileIntent);
                }
            }
            break;
            case R.id.txvReplyTitle:
            case R.id.replierImageView: {
                CommentsData commentData = (CommentsData) ((View) v.getParent()).getTag();
                if (!"fb".equals(commentData.getComment_type())) {
                    Intent profileIntent = new Intent(getActivity(), UserProfileActivity.class);
                    profileIntent.putExtra(Constants.USER_ID, commentData.getUserId());
                    profileIntent.putExtra(AppConstants.AUTHOR_NAME, commentData.getName());
                    profileIntent.putExtra(Constants.FROM_SCREEN, "Article Detail Comments");
                    startActivity(profileIntent);
                }
            }
        }

    }

    private void openCommentDialog(CommentsData comData, String opType) {
        if ("article".equals(type)) {
            try {
                AddEditCommentReplyFragment commentFrag = new AddEditCommentReplyFragment();
                commentFrag.setTargetFragment(this, 0);
                Bundle _args = new Bundle();
                _args.putString(Constants.ARTICLE_ID, articleId);
                _args.putString(Constants.AUTHOR, author);
                _args.putString("opType", opType);
                if (comData != null) {
                    _args.putParcelable("commentData", comData);
                }
                commentFrag.setArguments(_args);
                ((ArticleDetailsContainerActivity) getActivity()).hideToolbarPerm();
                ((ArticleDetailsContainerActivity) getActivity()).addFragment(commentFrag, null, true, "topToBottom");
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        } else {
            try {
                AddEditCommentReplyDialogFragment commentFrag = new AddEditCommentReplyDialogFragment();
                commentFrag.setTargetFragment(this, 0);
                Bundle _args = new Bundle();
                _args.putString(Constants.ARTICLE_ID, articleId);
                _args.putString(Constants.AUTHOR, author);
                _args.putString("opType", opType);
                if (comData != null) {
                    _args.putParcelable("commentData", comData);
                    _args.putString("type", "video");
                }
                commentFrag.setArguments(_args);
                FragmentManager fm = getChildFragmentManager();
                commentFrag.show(fm, "Replies");
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
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
    protected void updateUi(Response response) {

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


    private void getMoreComments() {
        isLoading = true;
        if (isAdded() && !ConnectivityUtils.isNetworkEnabled(getActivity())) {
            ((ArticleDetailsContainerActivity) getActivity()).showToast(getString(R.string.error_network));
            return;
        }
//        mLodingView.setVisibility(View.VISIBLE);
        Retrofit retro = BaseApplication.getInstance().getRetrofit();
//        ArticleDetailsAPI articleDetailsAPI = retro.create(ArticleDetailsAPI.class);
//        Call<ResponseBody> call = articleDetailsAPI.getComments(commentURL);
//        call.enqueue(commentsCallback);
        if ("db".equals(commentType)) {
            ArticleDetailsAPI articleDetailsAPI = retro.create(ArticleDetailsAPI.class);
            Call<ResponseBody> call = articleDetailsAPI.getComments(commentURL);
            call.enqueue(commentsCallback);
        } else {
            ArticleDetailsAPI articleDetailsAPI = retro.create(ArticleDetailsAPI.class);
            Call<FBCommentResponse> call = articleDetailsAPI.getFBComments(articleId, pagination);
            call.enqueue(fbCommentsCallback);
        }
    }

    Callback<ResponseBody> commentsCallback = new Callback<ResponseBody>() {
        @Override
        public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
            removeProgressDialog();
            if (response == null || null == response.body()) {
                NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                Crashlytics.logException(nee);
                if (isAdded())
                    ((ArticleDetailsContainerActivity) getActivity()).showToast(getString(R.string.server_went_wrong));
                ;
                isLoading = false;
                commentType = "fb";
                commentURL = "http";
                return;
            }
            try {
                isLoading = false;
                String resData = new String(response.body().bytes());
                ArrayList<CommentsData> arrayList = new ArrayList<>();
                JSONArray commentsJson = new JSONArray(resData);
                commentURL = "";
                if (commentsJson.length() > 0) {
                    commentHeading.setVisibility(View.VISIBLE);
                }
                for (int i = 0; i < commentsJson.length(); i++) {
                    if (commentsJson.getJSONObject(i).has("next")) {
                        commentURL = commentsJson.getJSONObject(i).getString("next");
                    } else {
                        CommentsData cData = new Gson().fromJson(commentsJson.get(i).toString(), CommentsData.class);
                        arrayList.add(cData);
                    }
                }
                if (StringUtils.isNullOrEmpty(commentURL)) {
                    commentType = "fb";
                    commentURL = "http";
                }

                ViewHolder viewHolder = new ViewHolder();
                for (int i = 0; i < arrayList.size(); i++) {
                    displayComments(viewHolder, arrayList.get(i), false);
                }
                if (commentLayout.getHeight() < mScrollView.getHeight()) {
                    getMoreComments();
                }

            } catch (JSONException jsonexception) {
                Crashlytics.logException(jsonexception);
                Log.d("JSONException", Log.getStackTraceString(jsonexception));
                if (isAdded())
                    ((ArticleDetailsContainerActivity) getActivity()).showToast(getString(R.string.server_went_wrong));
                ;
            } catch (Exception ex) {
                Crashlytics.logException(ex);
                Log.d("MC4kException", Log.getStackTraceString(ex));
                if (isAdded()) {
                    ((ArticleDetailsContainerActivity) getActivity()).showToast(getString(R.string.server_went_wrong));
                    ;
                }
            }
        }

        @Override
        public void onFailure(Call<ResponseBody> call, Throwable t) {
//            mLodingView.setVisibility(View.GONE);
//            handleExceptions(t);
        }
    };

    Callback<FBCommentResponse> fbCommentsCallback = new Callback<FBCommentResponse>() {
        @Override
        public void onResponse(Call<FBCommentResponse> call, retrofit2.Response<FBCommentResponse> response) {
            removeProgressDialog();
            if (response == null || null == response.body()) {
                if (isAdded())
                    ((ArticleDetailsContainerActivity) getActivity()).showToast(getString(R.string.server_went_wrong));
                ;
                return;
            }

            try {
                isLoading = false;
                FBCommentResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    ArrayList<CommentsData> dataList = responseData.getData().getResult();
                    pagination = responseData.getData().getPagination();
                    if (dataList.size() == 0) {
                        pagination = AppConstants.PAGINATION_END_VALUE;
                        if (!isFbCommentHeadingAdded && commentHeading.getVisibility() != View.VISIBLE) {
                            noCommentsTextView.setVisibility(View.VISIBLE);
                        }
                    } else {
                        if (!isFbCommentHeadingAdded) {
                            TextView textView = new TextView(getActivity());
                            textView.setText(getString(R.string.ad_comments_fb_comment));
                            textView.setTextColor(ContextCompat.getColor(getActivity(), R.color.ad_comment_title));
                            Typeface face = Typeface.createFromAsset(getActivity().getAssets(), "fonts/oswald_regular.ttf");
                            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.ad_comments_heading));
                            textView.setTypeface(face);
                            int paddingVal = AppUtils.dpTopx(10);
                            textView.setPadding(paddingVal, paddingVal, paddingVal, paddingVal);
                            commentLayout.addView(textView);
                            isFbCommentHeadingAdded = true;
                        }

                        ViewHolder viewHolder = new ViewHolder();
                        for (int i = 0; i < dataList.size(); i++) {
                            CommentsData fbCommentData = dataList.get(i);
                            fbCommentData.setComment_type("fb");
                            displayComments(viewHolder, fbCommentData, false);
                        }
                    }
                } else {
                    if (isAdded())
                        ((ArticleDetailsContainerActivity) getActivity()).showToast(getString(R.string.server_went_wrong));
                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
                if (isAdded())
                    ((ArticleDetailsContainerActivity) getActivity()).showToast(getString(R.string.went_wrong));
            }
        }

        @Override
        public void onFailure(Call<FBCommentResponse> call, Throwable t) {
//            mLodingView.setVisibility(View.GONE);
//            handleExceptions(t);
        }
    };

    private void displayComments(ViewHolder holder, CommentsData commentList,
                                 boolean isNewComment) {
        if (holder != null) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(R.layout.custom_comment_cell, null);
            holder.commentorsImage = (ImageView) view.findViewById(R.id.commentorImageView);
            holder.commentName = (TextView) view.findViewById(R.id.txvCommentTitle);
            holder.commentDescription = (TextView) view.findViewById(R.id.txvCommentDescription);
            holder.dateTxt = (TextView) view.findViewById(R.id.txvDate);
//            holder.commentCellReplyTxt = (TextView) view.findViewById(R.id.txvCommentCellReply);
//            holder.separatorView = view.findViewById(R.id.separatorView);
            holder.commentCellEditTxt = (TextView) view.findViewById(R.id.txvCommentCellEdit);
            holder.replyCommentView = (LinearLayout) view.findViewById(R.id.replyRelativeLayout);

            holder.commentorsImage.setOnClickListener(this);
            holder.commentName.setOnClickListener(this);
            holder.replyCommentView.setOnClickListener(this);
            holder.replyCommentView.setTag(commentList);

//            holder.commentCellReplyTxt.setOnClickListener(this);
            holder.commentCellEditTxt.setOnClickListener(this);

            view.setTag(commentList);

            if (!"fb".equals(commentList.getComment_type()) && userDynamoId.equals(commentList.getUserId())) {
                holder.commentCellEditTxt.setVisibility(View.VISIBLE);
//                holder.commentCellReplyTxt.setVisibility(View.GONE);
            } else {
                holder.commentCellEditTxt.setVisibility(View.GONE);
                if ("fb".equals(commentList.getComment_type())) {
//                    holder.commentCellReplyTxt.setVisibility(View.GONE);
//                    holder.separatorView.setVisibility(View.GONE);
                } else {
//                    holder.commentCellReplyTxt.setVisibility(View.VISIBLE);
                }
            }

            if (!StringUtils.isNullOrEmpty(commentList.getName())) {
                holder.commentName.setText(commentList.getName());
            } else {
                holder.commentName.setText("User");
            }
            if (!StringUtils.isNullOrEmpty(commentList.getBody())) {
                holder.commentDescription.setText(commentList.getBody());
            }
            if (!StringUtils.isNullOrEmpty(commentList.getCreate())) {
                holder.dateTxt.setText(DateTimeUtils.getDateFromNanoMilliTimestamp(Long.parseLong(commentList.getCreate())));
            } else {
                holder.dateTxt.setText("NA");
            }

            if (commentList.getProfile_image() != null && !StringUtils.isNullOrEmpty(commentList.getProfile_image().getClientAppMin())) {
                try {
                    Picasso.with(getActivity()).load(commentList.getProfile_image().getClientAppMin()).placeholder(R.drawable.default_commentor_img).into(holder.commentorsImage);
                } catch (Exception e) {
                    Crashlytics.logException(e);
                    Log.d("MC4kException", Log.getStackTraceString(e));
                    Picasso.with(getActivity()).load(R.drawable.default_commentor_img).into(holder.commentorsImage);
                }
            } else {
                Picasso.with(getActivity()).load(R.drawable.default_commentor_img).into(holder.commentorsImage);
            }

            if (isNewComment) {
                commentLayout.addView(view, 0);
            } else {
                commentLayout.addView(view);
            }
            if (commentList.getReplies() != null && commentList.getReplies().size() > 0) {
                holder.replyCommentView.setVisibility(View.VISIBLE);
                ViewHolder replyViewholder = new ViewHolder();
                for (int j = 0; j < commentList.getReplies().size(); j++) {
                    if ("fb".equals(commentList.getComment_type())) {
                        commentList.getReplies().get(j).setComment_type("fb");
                    }
                    displayReplies(replyViewholder, commentList.getReplies().get(j), holder.replyCommentView, REPLY_LEVEL_PARENT, j);
                }
            } else {
                holder.replyCommentView.setVisibility(View.GONE);
            }
        }
    }

    private void displayReplies(ViewHolder replyViewholder, CommentsData replies, LinearLayout parentView, int replyLevel, int replyPos) {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View view = inflater.inflate(R.layout.custom_reply_cell, null);
        replyViewholder.replyIndicatorImageView = (ImageView) view.findViewById(R.id.replyIndicatorImageView);
        replyViewholder.commentorsImage = (ImageView) view.findViewById(R.id.replierImageView);
        replyViewholder.commentName = (TextView) view.findViewById(R.id.txvReplyTitle);
        replyViewholder.commentDescription = (TextView) view.findViewById(R.id.txvCommentDescription);
        replyViewholder.dateTxt = (TextView) view.findViewById(R.id.txvDate);
//        replyViewholder.replyCellReplyTxt = (TextView) view.findViewById(R.id.txvReplyCellReply);
//        replyViewholder.separatorView = view.findViewById(R.id.separatorView);
        replyViewholder.replyCellEditTxt = (TextView) view.findViewById(R.id.txvReplyCellEdit);
        replyViewholder.replyCommentView = (LinearLayout) view.findViewById(R.id.replyRelativeLayout);

        replyViewholder.commentorsImage.setOnClickListener(this);
        replyViewholder.commentName.setOnClickListener(this);

//        replyViewholder.replyCellReplyTxt.setOnClickListener(this);
        replyViewholder.replyCellEditTxt.setOnClickListener(this);

        if (!"fb".equals(replies.getComment_type()) && userDynamoId.equals(replies.getUserId())) {
            replyViewholder.replyCellEditTxt.setVisibility(View.VISIBLE);
//            replyViewholder.replyCellReplyTxt.setVisibility(View.GONE);
        } else {
            replyViewholder.replyCellEditTxt.setVisibility(View.GONE);
            if ("fb".equals(replies.getComment_type())) {
//                replyViewholder.replyCellReplyTxt.setVisibility(View.GONE);
//                replyViewholder.separatorView.setVisibility(View.GONE);
            } else {
                if (replyLevel == REPLY_LEVEL_CHILD) {
//                    replyViewholder.replyCellReplyTxt.setVisibility(View.GONE);
//                    replyViewholder.separatorView.setVisibility(View.GONE);
                } else {
//                    replyViewholder.replyCellReplyTxt.setVisibility(View.VISIBLE);
                }
            }
        }

        if (replyLevel == REPLY_LEVEL_PARENT && replyPos == 0) {
            replyViewholder.replyIndicatorImageView.setVisibility(View.VISIBLE);
        } else {
            replyViewholder.replyIndicatorImageView.setVisibility(View.INVISIBLE);
        }


        view.setTag(replies);

        if (!StringUtils.isNullOrEmpty(replies.getName())) {
            replyViewholder.commentName.setText(replies.getName());
        } else {
            replyViewholder.commentName.setText("User");
        }
        if (!StringUtils.isNullOrEmpty(replies.getBody())) {
            replyViewholder.commentDescription.setText(replies.getBody());
        }
        if (!StringUtils.isNullOrEmpty(replies.getCreate())) {
            replyViewholder.dateTxt.setText(DateTimeUtils.getDateFromNanoMilliTimestamp(Long.parseLong(replies.getCreate())));
        } else {
            replyViewholder.dateTxt.setText("NA");
        }

        if (replies.getProfile_image() != null && !StringUtils.isNullOrEmpty(replies.getProfile_image().getClientAppMin())) {
            try {
                Picasso.with(getActivity()).load(replies.getProfile_image().getClientAppMin()).placeholder(R.drawable.default_commentor_img).into(replyViewholder.commentorsImage);
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
                Picasso.with(getActivity()).load(R.drawable.default_commentor_img).into(replyViewholder.commentorsImage);
            }
        } else {
            Picasso.with(getActivity()).load(R.drawable.default_commentor_img).into(replyViewholder.commentorsImage);
        }
        parentView.addView(view);

        if (replies.getReplies() != null && replies.getReplies().size() > 0) {
            replyViewholder.replyCommentView.setVisibility(View.VISIBLE);
            ViewHolder replyReplyViewholder = new ViewHolder();
            for (int j = 0; j < replies.getReplies().size(); j++) {
                if ("fb".equals(replies.getComment_type())) {
                    replies.getReplies().get(j).setComment_type("fb");
                }
                displayReplies(replyReplyViewholder, replies.getReplies().get(j), parentView, REPLY_LEVEL_CHILD, j);
            }
        } else {
            replyViewholder.replyCommentView.setVisibility(View.GONE);
        }
    }

    private class ViewHolder {
        private ImageView commentorsImage;
        private TextView commentName;
        private TextView commentDescription;
        private TextView dateTxt;
        //        private TextView commentCellReplyTxt;
//        private View separatorView;
//        private TextView replyCellReplyTxt;
        private TextView commentCellEditTxt;
        private TextView replyCellEditTxt;
        private LinearLayout replyCommentView;
        public ImageView replyIndicatorImageView;
    }

    @Override
    public void onCommentAddition(CommentsData cd) {
        displayComments(new ViewHolder(), cd, false);
    }

    @Override
    public void onReplyAddition(CommentsData updatedComment) {
        for (int i = 0; i < commentLayout.getChildCount(); i++) {
            CommentsData cdata = (CommentsData) commentLayout.getChildAt(i).getTag();
            CommentsData searchedData = recursiveSearch(cdata, updatedComment);
            if (searchedData != null) {
                searchedData.getReplies().add(updatedComment);
                ViewHolder viewHolder = new ViewHolder();
                displayCommentsAtPosition(viewHolder, cdata, false, i);
                break;
            } else {
                Log.d("Nothing in comment ", cdata.getBody());
            }
        }
    }

    @Override
    public void onCommentReplyEditSuccess(CommentsData cd) {
        for (int i = 0; i < commentLayout.getChildCount(); i++) {
            CommentsData cdata = (CommentsData) commentLayout.getChildAt(i).getTag();
            CommentsData searchedData = recursiveSearch(cdata, cd);
            if (searchedData != null) {
                ViewHolder viewHolder = new ViewHolder();
                displayCommentsAtPosition(viewHolder, cdata, false, i);
                break;
            } else {
                Log.d("Nothing in comment ", cdata.getBody());
            }
        }
    }

    private CommentsData recursiveSearch(CommentsData cd1, CommentsData upComment) {
        if (cd1.getId().equals(upComment.getId()) || cd1.getId().equals(upComment.getParent_id())) {
            return cd1;
        }
        ArrayList<CommentsData> children = cd1.getReplies();
        CommentsData res = null;
        for (int i = 0; res == null && i < children.size(); i++) {
            res = recursiveSearch(children.get(i), upComment);
        }
        return res;
    }

    private void displayCommentsAtPosition(ViewHolder holder, CommentsData commentList,
                                           boolean isNewComment, int position) {
        if (holder != null) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(R.layout.custom_comment_cell, null);
            holder.commentorsImage = (ImageView) view.findViewById(R.id.commentorImageView);
            holder.commentName = (TextView) view.findViewById(R.id.txvCommentTitle);
            holder.commentDescription = (TextView) view.findViewById(R.id.txvCommentDescription);
            holder.dateTxt = (TextView) view.findViewById(R.id.txvDate);
//            holder.commentCellReplyTxt = (TextView) view.findViewById(R.id.txvCommentCellReply);
            holder.commentCellEditTxt = (TextView) view.findViewById(R.id.txvCommentCellEdit);
            holder.replyCommentView = (LinearLayout) view.findViewById(R.id.replyRelativeLayout);

            holder.commentorsImage.setOnClickListener(this);
            holder.commentName.setOnClickListener(this);
            holder.replyCommentView.setOnClickListener(this);
            holder.replyCommentView.setTag(commentList);

//            holder.commentCellReplyTxt.setOnClickListener(this);
            holder.commentCellEditTxt.setOnClickListener(this);

            view.setTag(commentList);

            if (!"fb".equals(commentList.getComment_type()) && userDynamoId.equals(commentList.getUserId())) {
                holder.commentCellEditTxt.setVisibility(View.VISIBLE);
            } else {
                holder.commentCellEditTxt.setVisibility(View.INVISIBLE);
            }

//            if ("fb".equals(commentList.getComment_type())) {
//                holder.commentCellReplyTxt.setVisibility(View.INVISIBLE);
//            } else {
//                holder.commentCellReplyTxt.setVisibility(View.VISIBLE);
//            }

//            if (holder.commentCellEditTxt.getVisibility() == View.VISIBLE) {
//                holder.commentCellReplyTxt.setVisibility(View.INVISIBLE);
//            } else {
//                holder.commentCellReplyTxt.setVisibility(View.VISIBLE);
//            }

            if (!StringUtils.isNullOrEmpty(commentList.getName())) {
                holder.commentName.setText(commentList.getName());
            } else {
                holder.commentName.setText("User");
            }
            if (!StringUtils.isNullOrEmpty(commentList.getBody())) {
                holder.commentDescription.setText(commentList.getBody());
            }
            if (!StringUtils.isNullOrEmpty(commentList.getCreate())) {
                holder.dateTxt.setText(DateTimeUtils.getDateFromNanoMilliTimestamp(Long.parseLong(commentList.getCreate())));
            } else {
                holder.dateTxt.setText("NA");
            }

            if (commentList.getProfile_image() != null && !StringUtils.isNullOrEmpty(commentList.getProfile_image().getClientAppMin())) {
                try {
                    Picasso.with(getActivity()).load(commentList.getProfile_image().getClientAppMin()).placeholder(R.drawable.default_commentor_img).into(holder.commentorsImage);
                } catch (Exception e) {
                    Crashlytics.logException(e);
                    Log.d("MC4kException", Log.getStackTraceString(e));
                    Picasso.with(getActivity()).load(R.drawable.default_commentor_img).into(holder.commentorsImage);
                }
            } else {
                Picasso.with(getActivity()).load(R.drawable.default_commentor_img).into(holder.commentorsImage);
            }

            commentLayout.removeViewAt(position);
            commentLayout.addView(view, position);
            if (commentList.getReplies() != null && commentList.getReplies().size() > 0) {
                holder.replyCommentView.setVisibility(View.VISIBLE);
                ViewHolder replyViewholder = new ViewHolder();
                for (int j = 0; j < commentList.getReplies().size(); j++) {
                    displayReplies(replyViewholder, commentList.getReplies().get(j), holder.replyCommentView, REPLY_LEVEL_PARENT, j);
                }
            } else {
                holder.replyCommentView.setVisibility(View.GONE);
            }
        }
    }
}