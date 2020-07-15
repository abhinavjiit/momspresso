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
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.ui.ContentCommentReplyNotificationActivity;
import com.mycity4kids.utils.AppUtils;

/**
 * Created by user on 08-06-2015.
 */
public class CommentOptionsDialogFragment extends DialogFragment implements OnClickListener {

    private int position;
    private String responseType;
    private String authorId;
    private String blogWriterId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.comment_options_dialog, container,
                false);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        position = getArguments().getInt("position");
        responseType = getArguments().getString("responseType");
        authorId = getArguments().getString("authorId");
        blogWriterId = getArguments().getString("blogWriterId");

        TextView deleteCommentTextView = (TextView) rootView.findViewById(R.id.deleteCommentTextView);
        TextView editCommentTextView = (TextView) rootView.findViewById(R.id.editCommentTextView);
        TextView reportCommentTextView = (TextView) rootView.findViewById(R.id.reportCommentTextView);

        deleteCommentTextView.setOnClickListener(this);
        editCommentTextView.setOnClickListener(this);
        reportCommentTextView.setOnClickListener(this);

        if (AppUtils.isPrivateProfile(blogWriterId)) {
            if (SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId().equals(authorId)) {
                deleteCommentTextView.setVisibility(View.VISIBLE);
                editCommentTextView.setVisibility(View.VISIBLE);
                reportCommentTextView.setVisibility(View.VISIBLE);
            } else {
                deleteCommentTextView.setVisibility(View.VISIBLE);
                editCommentTextView.setVisibility(View.GONE);
                reportCommentTextView.setVisibility(View.VISIBLE);
            }
        } else {
            if (SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId().equals(authorId)) {
                deleteCommentTextView.setVisibility(View.VISIBLE);
                editCommentTextView.setVisibility(View.VISIBLE);
                reportCommentTextView.setVisibility(View.VISIBLE);
            } else {
                deleteCommentTextView.setVisibility(View.GONE);
                editCommentTextView.setVisibility(View.GONE);
                reportCommentTextView.setVisibility(View.VISIBLE);
            }

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
            case R.id.deleteCommentTextView: {
                if (getActivity() != null && getActivity() instanceof ContentCommentReplyNotificationActivity) {
                    ICommentOptionAction commentOptionAction = (ICommentOptionAction) getActivity();
                    commentOptionAction.onResponseDelete(position, responseType);
                    dismiss();
                } else {
                    ICommentOptionAction commentOptionAction = (ICommentOptionAction) getParentFragment();
                    commentOptionAction.onResponseDelete(position, responseType);
                    dismiss();
                }
            }
            break;
            case R.id.editCommentTextView: {
                if (getActivity() != null && getActivity() instanceof ContentCommentReplyNotificationActivity) {
                    ICommentOptionAction commentOptionAction = (ICommentOptionAction) getActivity();
                    commentOptionAction.onResponseEdit(position, responseType);
                    dismiss();
                } else {
                    ICommentOptionAction commentOptionAction = (ICommentOptionAction) getParentFragment();
                    commentOptionAction.onResponseEdit(position, responseType);
                    dismiss();
                }
            }
            break;
            case R.id.reportCommentTextView: {
                if (getActivity() != null && getActivity() instanceof ContentCommentReplyNotificationActivity) {
                    ICommentOptionAction commentOptionAction = (ICommentOptionAction) getActivity();
                    commentOptionAction.onResponseReport(position, responseType);
                    dismiss();
                } else {
                    ICommentOptionAction commentOptionAction = (ICommentOptionAction) getParentFragment();
                    commentOptionAction.onResponseReport(position, responseType);
                    dismiss();
                }
            }
            break;
            default:
                break;
        }
    }

    public interface ICommentOptionAction {

        void onResponseDelete(int position, String responseType);

        void onResponseEdit(int position, String responseType);

        void onResponseReport(int position, String responseType);
    }
}