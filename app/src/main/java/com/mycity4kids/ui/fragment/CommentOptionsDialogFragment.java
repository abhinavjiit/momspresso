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
import com.mycity4kids.ui.ArticleShortStoryMomVlogCommentNotificationActivity;

/**
 * Created by user on 08-06-2015.
 */
public class CommentOptionsDialogFragment extends DialogFragment implements OnClickListener {

    //    private IConfirmationResult iConfirmationResult;
    private int position;
    private String responseType;
    private String authorId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.comment_options_dialog, container,
                false);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        position = getArguments().getInt("position");
        responseType = getArguments().getString("responseType");
        authorId = getArguments().getString("authorId");

        TextView deleteCommentTextView = (TextView) rootView.findViewById(R.id.deleteCommentTextView);
        TextView editCommentTextView = (TextView) rootView.findViewById(R.id.editCommentTextView);
        TextView reportCommentTextView = (TextView) rootView.findViewById(R.id.reportCommentTextView);

        deleteCommentTextView.setOnClickListener(this);
        editCommentTextView.setOnClickListener(this);
        reportCommentTextView.setOnClickListener(this);

        if (SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId().equals(authorId)) {
            deleteCommentTextView.setVisibility(View.VISIBLE);
            editCommentTextView.setVisibility(View.VISIBLE);
            reportCommentTextView.setVisibility(View.VISIBLE);
        } else {
            deleteCommentTextView.setVisibility(View.GONE);
            editCommentTextView.setVisibility(View.GONE);
            reportCommentTextView.setVisibility(View.VISIBLE);
        }
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        iConfirmationResult = (IConfirmationResult) context;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.deleteCommentTextView: {
                if (getActivity() != null
                        && getActivity() instanceof ArticleShortStoryMomVlogCommentNotificationActivity) {
                    ICommentOptionAction iCommentOptionAction = (ICommentOptionAction) getActivity();
                    iCommentOptionAction.onResponseDelete(position, responseType);
                    dismiss();
                } else {
                    ICommentOptionAction iCommentOptionAction = (ICommentOptionAction) getParentFragment();
                    iCommentOptionAction.onResponseDelete(position, responseType);
                    dismiss();
                }
            }
            break;
            case R.id.editCommentTextView: {
                if (getActivity() != null
                        && getActivity() instanceof ArticleShortStoryMomVlogCommentNotificationActivity) {
                    ICommentOptionAction iCommentOptionAction = (ICommentOptionAction) getActivity();
                    iCommentOptionAction.onResponseEdit(position, responseType);
                    dismiss();
                } else {
                    ICommentOptionAction iCommentOptionAction = (ICommentOptionAction) getParentFragment();
                    iCommentOptionAction.onResponseEdit(position, responseType);
                    dismiss();
                }
            }
            break;
            case R.id.reportCommentTextView: {
                if (getActivity() != null
                        && getActivity() instanceof ArticleShortStoryMomVlogCommentNotificationActivity) {
                    ICommentOptionAction iCommentOptionAction = (ICommentOptionAction) getActivity();
                    iCommentOptionAction.onResponseReport(position, responseType);
                    dismiss();
                } else {
                    ICommentOptionAction iCommentOptionAction = (ICommentOptionAction) getParentFragment();
                    iCommentOptionAction.onResponseReport(position, responseType);
                    dismiss();
                }
            }
            break;
        }
    }

    public interface ICommentOptionAction {

        void onResponseDelete(int position, String responseType);

        void onResponseEdit(int position, String responseType);

        void onResponseReport(int position, String responseType);
    }
}