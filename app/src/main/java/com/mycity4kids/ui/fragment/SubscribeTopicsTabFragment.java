package com.mycity4kids.ui.fragment;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.crashlytics.android.Crashlytics;
import com.mycity4kids.base.BaseFragment;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.models.Topics;
import com.mycity4kids.newmodels.SelectTopic;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.ui.adapter.SubscribeTopicsTabAdapter;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by hemant on 17/7/17.
 */
public class SubscribeTopicsTabFragment extends BaseFragment {

    private String userId;
    private ArrayList<SelectTopic> selectTopic;
    private HashMap<String, Topics> selectedTopicsMap;
    private ArrayList<String> previouslyFollowedTopics;
    private int position;

    private View view;
    ListView popularTopicsListView;
    private SubscribeTopicsTabAdapter searchTopicsSplashAdapter;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.subscribe_topics_tab_fragment, container, false);
        popularTopicsListView = (ListView) view.findViewById(R.id.popularTopicsListView);
        userId = SharedPrefUtils.getUserDetailModel(getActivity()).getDynamoId();

        selectedTopicsMap = new HashMap<>();
        selectTopic = getArguments().getParcelableArrayList("selectTopicList");
        position = getArguments().getInt("position");
        previouslyFollowedTopics = getArguments().getStringArrayList("previouslyFollowedTopics");

        createTopicsData();
        return view;
    }

    private void createTopicsData() {
        try {
            searchTopicsSplashAdapter = new SubscribeTopicsTabAdapter(getActivity(), selectTopic, BaseApplication.getSelectedTopicsMap(), position);
            popularTopicsListView.setAdapter(searchTopicsSplashAdapter);
        } catch (Exception e) {
            Crashlytics.logException(e);
            Log.d("MC4kException", Log.getStackTraceString(e));
        }
    }
}
