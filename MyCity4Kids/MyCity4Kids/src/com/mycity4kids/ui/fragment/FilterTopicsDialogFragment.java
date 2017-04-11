package com.mycity4kids.ui.fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.kelltontech.network.Response;
import com.kelltontech.ui.IScreen;
import com.mycity4kids.R;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.models.Topics;
import com.mycity4kids.models.parentingdetails.CommentsData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 08-06-2015.
 */
public class FilterTopicsDialogFragment extends DialogFragment {

    private ArrayList<Topics> subTopicsArrayList;
    private List<String> selectedTopics;
    private String topicsLevel;

    private Toolbar mToolbar;
    private ProgressDialog mProgressDialog;
    private ListView subTopicsListView;
    private ListView subSubTopicsListView;
    private ImageView confirmImageView;
    private RelativeLayout subTopicsContainer;

    private SubTopicsListAdapter subTopicsListAdapter;
    private SubSubTopicsListAdapter subSubTopicsListAdapter;

    private OnTopicsSelectionComplete onTopicsSelectionComplete;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.filter_topics_dialog, container,
                false);
        mToolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        mToolbar.setTitle("Filter");
        mToolbar.setTitleTextColor(ContextCompat.getColor(getActivity(), R.color.colorControlNormal));
        Drawable upArrow = ContextCompat.getDrawable(getActivity(), R.drawable.back_arroow);
        upArrow.setColorFilter(ContextCompat.getColor(getActivity(), R.color.colorControlNormal), PorterDuff.Mode.SRC_ATOP);
        mToolbar.setNavigationIcon(upArrow);
        mToolbar.setNavigationOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // handle back button naviagtion
                dismiss();
            }
        });
        confirmImageView = (ImageView) rootView.findViewById(R.id.confirmImageView);
        subTopicsListView = (ListView) rootView.findViewById(R.id.subTopicsListView);
        subSubTopicsListView = (ListView) rootView.findViewById(R.id.subSubTopicsListView);
        subTopicsContainer = (RelativeLayout) rootView.findViewById(R.id.subTopicsContainer);

        Bundle extras = getArguments();
        if (extras != null) {
            subTopicsArrayList = extras.getParcelableArrayList("topicsList");
            topicsLevel = extras.getString("topicsLevel");
        }

        if (AppConstants.TOPIC_LEVEL_MAIN_CATEGORY.equals(topicsLevel)) {
            subTopicsListAdapter = new SubTopicsListAdapter(getActivity(), R.layout.sub_topics_filter_item, subTopicsArrayList);
            subSubTopicsListAdapter = new SubSubTopicsListAdapter(getActivity(), R.layout.sub_topics_filter_item, subTopicsArrayList.get(0).getChild());
            subTopicsListView.setAdapter(subTopicsListAdapter);
            subSubTopicsListView.setAdapter(subSubTopicsListAdapter);
        } else if (AppConstants.TOPIC_LEVEL_SUB_CATEGORY.equals(topicsLevel)) {
            subTopicsListView.setVisibility(View.GONE);
            subTopicsContainer.setVisibility(View.GONE);
            subSubTopicsListAdapter = new SubSubTopicsListAdapter(getActivity(), R.layout.sub_topics_filter_item, subTopicsArrayList);
            subSubTopicsListView.setAdapter(subSubTopicsListAdapter);
        }


        confirmImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppConstants.TOPIC_LEVEL_MAIN_CATEGORY.equals(topicsLevel)) {
                    for (int i = 0; i < subTopicsArrayList.size(); i++) {
                        subSubTopicsListAdapter.setSubSubTopicsData(subTopicsArrayList.get(i).getChild());
                        subSubTopicsListAdapter.notifyDataSetChanged();
                        selectedTopics = subSubTopicsListAdapter.getSelectedTopics();
                    }
                } else if (AppConstants.TOPIC_LEVEL_SUB_CATEGORY.equals(topicsLevel)) {
                    selectedTopics = subSubTopicsListAdapter.getSelectedTopics();
                }

                onTopicsSelectionComplete.onsSelectionComplete(selectedTopics);
                Log.d("selected tops = ", selectedTopics.toString());
                dismiss();
            }
        });


        subTopicsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                subTopicsListAdapter.setSelectedRow(position);
                subSubTopicsListAdapter.setSubSubTopicsData(subTopicsArrayList.get(position).getChild());
                subSubTopicsListAdapter.notifyDataSetChanged();
            }
        });

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            this.onTopicsSelectionComplete = (OnTopicsSelectionComplete) context;
        } catch (final ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnCompleteListener");
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

    public interface OnTopicsSelectionComplete {
        void onsSelectionComplete(List<String> topics);
    }

}