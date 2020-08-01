package com.mycity4kids.ui.fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.response.CommentListData;
import com.mycity4kids.retrofitAPIsInterfaces.SearchArticlesAuthorsAPI;
import com.mycity4kids.tagging.Mentions;
import com.mycity4kids.tagging.MentionsResponse;
import com.mycity4kids.tagging.mentions.MentionSpan;
import com.mycity4kids.tagging.mentions.MentionsEditable;
import com.mycity4kids.tagging.suggestions.SuggestionsResult;
import com.mycity4kids.tagging.tokenization.QueryToken;
import com.mycity4kids.tagging.tokenization.interfaces.QueryTokenReceiver;
import com.mycity4kids.tagging.ui.RichEditorView;
import com.mycity4kids.ui.fragment.AddArticleCommentReplyDialogFragment.MentionIndex;
import com.mycity4kids.utils.AppUtils;
import com.mycity4kids.utils.DateTimeUtils;
import com.mycity4kids.utils.StringUtils;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by hemant on 6/6/18.
 */

public class AddShortStoryCommentReplyDialogFragment extends DialogFragment implements OnClickListener,
        QueryTokenReceiver {

    private CommentListData commentOrReplyData;
    private ProgressDialog progressDialog;
    private ImageView closeImageView;
    private TextView postCommentReplyTextView;
    private RichEditorView commentReplyEditText;
    private RelativeLayout relativeMainContainer;
    private ImageView commentorImageView;
    private TextView commentorUsernameTextView;
    private TextView commentDataTextView;
    private TextView commentDateTextView;
    private TextView headingTextView;
    private String actionType;
    private int position;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.add_ss_comment_reply_fragment, container,
                false);

        closeImageView = rootView.findViewById(R.id.closeImageView);
        postCommentReplyTextView = rootView.findViewById(R.id.postCommentReplyTextView);
        commentReplyEditText = rootView.findViewById(R.id.commentReplyEditText);
        relativeMainContainer = rootView.findViewById(R.id.relativeMainContainer);
        commentorImageView = rootView.findViewById(R.id.commentorImageView);
        commentorUsernameTextView = rootView.findViewById(R.id.commentorUsernameTextView);
        commentDataTextView = rootView.findViewById(R.id.commentDataTextView);
        commentDateTextView = rootView.findViewById(R.id.commentDateTextView);
        headingTextView = rootView.findViewById(R.id.headingTextView);
        commentReplyEditText.setHint(getString(R.string.comment_placeholder));
        Bundle extras = getArguments();
        commentOrReplyData = (CommentListData) extras.get("parentCommentData");
        actionType = (String) extras.get("action");
        position = extras.getInt("position");
        commentReplyEditText.setQueryTokenReceiver(this);
        if (commentOrReplyData == null) {
            headingTextView.setText(BaseApplication.getAppContext().getString(R.string.short_s_add_comment));
            relativeMainContainer.setVisibility(View.GONE);
        } else {
            if ("EDIT_COMMENT".equals(actionType) || "EDIT_REPLY".equals(actionType)) {
                headingTextView.setText(BaseApplication.getAppContext().getString(R.string.ad_comments_edit_label));
                relativeMainContainer.setVisibility(View.GONE);
                commentReplyEditText.setText(AppUtils.createMentionSpanForEditing(commentOrReplyData.getMessage(),
                        commentOrReplyData.getMentions()));
            } else {
                headingTextView.setText(BaseApplication.getAppContext().getString(R.string.reply));
                relativeMainContainer.setVisibility(View.VISIBLE);
                setMentionForReplyingTo(extras);
                try {
                    Picasso.get().load(commentOrReplyData.getUserPic().getClientAppMin())
                            .placeholder(R.drawable.default_commentor_img).into((commentorImageView));
                } catch (Exception e) {
                    FirebaseCrashlytics.getInstance().recordException(e);
                    Log.d("MC4kException", Log.getStackTraceString(e));
                    if (isAdded()) {
                        Picasso.get().load(R.drawable.default_commentor_img).into(commentorImageView);
                    }
                }
                commentorUsernameTextView.setText(commentOrReplyData.getUserName());
                commentDataTextView.setText(AppUtils.createMentionSpanForEditing(commentOrReplyData.getMessage(),
                        commentOrReplyData.getMentions()));
                commentDateTextView.setText(DateTimeUtils
                        .getDateFromNanoMilliTimestamp(Long.parseLong(commentOrReplyData.getCreatedTime())));
            }
        }

        commentReplyEditText.requestFocus();
        if (getContext() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getContext()
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        }

        postCommentReplyTextView.setOnClickListener(this);
        closeImageView.setOnClickListener(this);
        return rootView;
    }

    private void setMentionForReplyingTo(Bundle extras) {
        try {
            if (extras.get("currentReplyData") != null) {
                CommentListData currentReplyData = (CommentListData) extras.get("currentReplyData");
                String message = "[~userId:" + currentReplyData.getUserId() + "] ";
                Map<String, Mentions> mentionsMap = new HashMap<>();
                Mentions mentions = new Mentions(currentReplyData.getUserId(),
                        currentReplyData.getUserName(),
                        "", "");
                mentionsMap.put(currentReplyData.getUserId(), mentions);
                commentReplyEditText.setText(AppUtils.createMentionSpanForEditing(message, mentionsMap));
            }
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            Log.d("MC4kException", Log.getStackTraceString(e));
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
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.postCommentReplyTextView:
                if (isValid()) {
                    InputMethodManager imm = (InputMethodManager) getContext()
                            .getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(commentReplyEditText.getWindowToken(), 0);
                    formatMentionDataForApiRequest(commentOrReplyData);
                    dismiss();
                }
                break;
            case R.id.closeImageView:
                InputMethodManager imm = (InputMethodManager) getContext()
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(commentReplyEditText.getWindowToken(), 0);
                dismiss();
                break;
            default:
                break;
        }
    }

    private void formatMentionDataForApiRequest(CommentListData commentOrReplyData) {
        Map<String, Mentions> mentionsMap = new HashMap<>();
        StringBuilder commentBody = new StringBuilder();
        try {
            MentionsEditable mentionsEditable = commentReplyEditText.getText();
            List<MentionIndex> marker = new ArrayList<>();
            marker.add(new MentionIndex(0, null));
            List<MentionSpan> mentionsList = commentReplyEditText.getMentionSpans();
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

        if ("EDIT_COMMENT".equals(actionType)) {
            Utils.shareEventTracking(getActivity(), "100WS Detail", "Comment_Android", "StoryDetail_Publish_Comment");
            ((ShortStoryFragment) getParentFragment())
                    .editComment(String.valueOf(commentBody), commentOrReplyData.getId(),
                            position, commentOrReplyData.getMentions());
        } else if ("EDIT_REPLY".equals(actionType)) {
            Utils.shareEventTracking(getActivity(), "100WS Detail", "Comment_Android",
                    "StoryDetail_PublishReply_Comment");
            Fragment parentFragment = getParentFragment();
            if (parentFragment != null) {
                if (parentFragment instanceof ShortStoryFragment) {
                    ((ShortStoryFragment) getParentFragment())
                            .editReply(String.valueOf(commentBody),
                                    commentOrReplyData.getParentCommentId(), commentOrReplyData.getId(),
                                    commentOrReplyData.getMentions());
                } else if (parentFragment instanceof ShortStoryCommentRepliesDialogFragment) {
                    Fragment parentOfParentFragment = parentFragment.getParentFragment();
                    if (parentOfParentFragment != null
                            && parentOfParentFragment instanceof ShortStoryFragment) {
                        ((ShortStoryFragment) parentOfParentFragment)
                                .editReply(String.valueOf(commentBody),
                                        commentOrReplyData.getParentCommentId(), commentOrReplyData.getId(),
                                        commentOrReplyData.getMentions());
                    }
                }
            }
        } else {
            if (commentOrReplyData == null) {
                Utils.shareEventTracking(getActivity(), "100WS Detail", "Comment_Android",
                        "StoryDetail_Publish_Comment");
                ((ShortStoryFragment) getParentFragment()).addComment(String.valueOf(commentBody), mentionsMap);
            } else {
                Utils.shareEventTracking(getActivity(), "100WS Detail", "Comment_Android",
                        "StoryDetail_PublishReply_Comment");
                ((ShortStoryFragment) getParentFragment())
                        .addReply(String.valueOf(commentBody), commentOrReplyData.getId(), mentionsMap);
            }
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

    private boolean isValid() {
        if (StringUtils.isNullOrEmpty(commentReplyEditText.getText().toString())) {
            if (isAdded()) {
                Toast.makeText(getActivity(), getString(R.string.ad_comments_toast_empty_comment), Toast.LENGTH_LONG)
                        .show();
            }
            return false;
        }
        return true;
    }

    @NonNull
    @Override
    public List<String> onQueryReceived(@NonNull QueryToken queryToken) {
        final QueryTokenReceiver receiver = commentReplyEditText;
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
                        commentReplyEditText.onReceiveSuggestionsResult(result, "dddd");
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
}
