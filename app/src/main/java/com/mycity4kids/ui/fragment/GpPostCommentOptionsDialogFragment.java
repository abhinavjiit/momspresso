package com.mycity4kids.ui.fragment;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.DialogFragment;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.ui.activity.GroupPostDetailActivity;
import com.mycity4kids.ui.activity.ViewGroupPostCommentsRepliesActivity;

/**
 * Created by user on 08-06-2015.
 */
public class GpPostCommentOptionsDialogFragment extends DialogFragment implements OnClickListener {

    private int position;
    private String responseType;
    private String authorId;
    private int commentPosition;
    private String memberType;
    private int commentType = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.comment_options_dialog, container,
                false);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        position = getArguments().getInt("position");
        commentPosition = getArguments().getInt("commentPosition");
        responseType = getArguments().getString("responseType");
        authorId = getArguments().getString("authorId");
        commentType = getArguments().getInt("commentType");
        memberType = getArguments().getString(AppConstants.GROUP_MEMBER_TYPE);

        TextView blockUserTextView = (TextView) rootView.findViewById(R.id.blockUserTextView);
        blockUserTextView.setVisibility(View.VISIBLE);
        blockUserTextView.setOnClickListener(this);
        TextView deleteCommentTextView = (TextView) rootView.findViewById(R.id.deleteCommentTextView);
        deleteCommentTextView.setOnClickListener(this);
        TextView editCommentTextView = (TextView) rootView.findViewById(R.id.editCommentTextView);
        editCommentTextView.setOnClickListener(this);
        TextView reportCommentTextView = (TextView) rootView.findViewById(R.id.reportCommentTextView);
        reportCommentTextView.setOnClickListener(this);

        if (SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId().equals(authorId)
                || AppConstants.GROUP_MEMBER_TYPE_MODERATOR.equals(memberType)) {
            deleteCommentTextView.setVisibility(View.VISIBLE);
            if (commentType == AppConstants.COMMENT_TYPE_AUDIO) {
                editCommentTextView.setVisibility(View.GONE);
            } else {
                editCommentTextView.setVisibility(View.VISIBLE);
            }
            reportCommentTextView.setVisibility(View.VISIBLE);
            blockUserTextView.setVisibility(View.GONE);
        } else if (AppConstants.GROUP_MEMBER_TYPE_ADMIN.equals(memberType)) {
            deleteCommentTextView.setVisibility(View.VISIBLE);
            blockUserTextView.setVisibility(View.VISIBLE);
            reportCommentTextView.setVisibility(View.VISIBLE);
            if (commentType == AppConstants.COMMENT_TYPE_AUDIO) {
                editCommentTextView.setVisibility(View.GONE);
            } else {
                editCommentTextView.setVisibility(View.VISIBLE);
            }
        } else {
            deleteCommentTextView.setVisibility(View.GONE);
            editCommentTextView.setVisibility(View.GONE);
            blockUserTextView.setVisibility(View.GONE);
            reportCommentTextView.setVisibility(View.VISIBLE);
        }
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.blockUserTextView: {
                if (getActivity() instanceof GroupPostDetailActivity) {
                    ((GroupPostDetailActivity) getActivity())
                            .blockUserWithResponseId(commentPosition, position, responseType);
                } else if (getActivity() instanceof ViewGroupPostCommentsRepliesActivity) {
                    ((ViewGroupPostCommentsRepliesActivity) getActivity())
                            .blockUserWithResponseId(commentPosition, position, responseType);
                }
                dismiss();
            }
            break;
            case R.id.deleteCommentTextView: {
                if (getActivity() instanceof GroupPostDetailActivity) {
                    ((GroupPostDetailActivity) getActivity()).onResponseDelete(commentPosition, position, responseType);
                } else if (getActivity() instanceof ViewGroupPostCommentsRepliesActivity) {
                    ((ViewGroupPostCommentsRepliesActivity) getActivity())
                            .onResponseDelete(commentPosition, position, responseType);
                }
                dismiss();
            }
            break;
            case R.id.editCommentTextView: {
                if (getActivity() instanceof GroupPostDetailActivity) {
                    ((GroupPostDetailActivity) getActivity()).onResponseEdit(commentPosition, position, responseType);
                } else if (getActivity() instanceof ViewGroupPostCommentsRepliesActivity) {
                    ((ViewGroupPostCommentsRepliesActivity) getActivity())
                            .onResponseEdit(commentPosition, position, responseType);
                }
                dismiss();
            }
            break;
            case R.id.reportCommentTextView: {
                if (getActivity() instanceof GroupPostDetailActivity) {
                    ((GroupPostDetailActivity) getActivity()).onResponseReport(commentPosition, position, responseType);
                } else if (getActivity() instanceof ViewGroupPostCommentsRepliesActivity) {
                    ((ViewGroupPostCommentsRepliesActivity) getActivity())
                            .onResponseReport(commentPosition, position, responseType);
                }
                dismiss();
            }
            break;
            default:
                break;
        }
    }
}