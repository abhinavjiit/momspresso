package com.mycity4kids.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.kelltontech.ui.BaseFragment;
import com.mycity4kids.R;
import com.mycity4kids.models.Topics;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.ui.adapter.AddArticleTopicsTabAdapter;

import org.apmem.tools.layouts.FlowLayout;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by hemant on 17/7/17.
 */
public class AddArticleTopicsTabFragment extends BaseFragment implements AddArticleTopicsTabAdapter.RecyclerViewClickListener {

    private String userId;
    private ArrayList<Topics> selectTopic;
    private HashMap<String, Topics> selectedTopicsMap;
    private ArrayList<String> previouslyFollowedTopics;
    private int position;


    private View view;
    //    private RecyclerView popularTopicsRecyclerView;
//    private AddArticleTopicsTabAdapter searchTopicsSplashAdapter;
    private FlowLayout rootView;
    private LayoutInflater mInflator;
    private String jsonMyObject;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.add_article_topics_tab_fragment, container, false);
        rootView = (FlowLayout) view.findViewById(R.id.rootView);
        userId = SharedPrefUtils.getUserDetailModel(getActivity()).getDynamoId();
        selectedTopicsMap = new HashMap<>();
        selectTopic = getArguments().getParcelableArrayList("selectTopicList");
        position = getArguments().getInt("position");
        previouslyFollowedTopics = getArguments().getStringArrayList("previouslyFollowedTopics");
        processTopicsDataForList();
        createTopicsData();
        return view;
    }

    ArrayList<Topics> mDatalist = new ArrayList<>();

    private void processTopicsDataForList() {
        for (int i = 0; i < selectTopic.size(); i++) {
            if (selectTopic.get(i).getChild().size() == 0) {
                //terminal Topic. Eligible for tagging
                Topics tempTopic = new Topics();
                tempTopic.setId(selectTopic.get(i).getId());
                tempTopic.setDisplay_name(selectTopic.get(i).getDisplay_name());
                tempTopic.setIsSelected(selectTopic.get(i).isSelected());
                tempTopic.setTitle(selectTopic.get(i).getTitle());
                tempTopic.setPublicVisibility(selectTopic.get(i).getPublicVisibility());
                tempTopic.setShowInMenu(selectTopic.get(i).getShowInMenu());
                ArrayList<Topics> tempList = new ArrayList<>();
                tempList.add(tempTopic);
                selectTopic.get(i).setChild(tempList);
            }
            for (int j = 0; j < selectTopic.get(i).getChild().size(); j++) {
                if (selectTopic.get(i).getChild().get(j).isSelected()) {
                    selectedTopicsMap.put(selectTopic.get(i).getChild().get(j).getId(), selectTopic.get(i).getChild().get(j));
                }
            }

        }
        Log.d("dwa", "" + mDatalist);
    }

    private void createTopicsData() {
        try {
//            searchTopicsSplashAdapter = new AddArticleTopicsTabAdapter(getActivity(), selectTopic, selectedTopicsMap, this);
//            popularTopicsRecyclerView.setAdapter(searchTopicsSplashAdapter);

            mInflator = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            for (int j = 0; j < selectTopic.size(); j++) {
                for (int i = 0; i < selectTopic.get(j).getChild().size(); i++) {
                    LinearLayout ll = (LinearLayout) mInflator.inflate(R.layout.topic_follow_unfollow_item, null);
                    final TextView tv = ((TextView) ll.getChildAt(0));
                    tv.setText(selectTopic.get(j).getChild().get(i).getDisplay_name().toUpperCase());
                    tv.setTag(selectTopic.get(j).getChild().get(i));
                    if (null == selectedTopicsMap.get(((Topics) tv.getTag()).getId())) {
                        tv.setSelected(false);
                    } else {
                        tv.setSelected(true);
                    }
                    tv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (null == selectedTopicsMap.get(((Topics) tv.getTag()).getId())) {
//                        Utils.pushEventFollowUnfollowTopic(mContext, GTMEventType.TOPIC_FOLLOWED_UNFOLLOWED_CLICKED_EVENT, userId, "SearchOrDetailsTopicList", "follow", ((Topics) tv.getTag()).getDisplay_name() + ":" + ((Topics) tv.getTag()).getId());
//                        Utils.pushTopicFollowUnfollowEvent(mContext, GTMEventType.FOLLOW_TOPIC_CLICK_EVENT, userId, "SearchOrDetailsTopicList", ((Topics) tv.getTag()).getDisplay_name() + "~" + ((Topics) tv.getTag()).getId());
                                selectedTopicsMap.put(((Topics) tv.getTag()).getId(), (Topics) tv.getTag());
                                ((Topics) tv.getTag()).setIsSelected(true);
                                tv.setSelected(true);
                            } else {
//                        Utils.pushEventFollowUnfollowTopic(mContext, GTMEventType.TOPIC_FOLLOWED_UNFOLLOWED_CLICKED_EVENT, userId, "SearchOrDetailsTopicList", "unfollow", ((Topics) tv.getTag()).getDisplay_name() + ":" + ((Topics) tv.getTag()).getId());
//                        Utils.pushTopicFollowUnfollowEvent(mContext, GTMEventType.UNFOLLOW_TOPIC_CLICK_EVENT, userId, "SearchOrDetailsTopicList", ((Topics) tv.getTag()).getDisplay_name() + "~" + ((Topics) tv.getTag()).getId());
                                selectedTopicsMap.remove(((Topics) tv.getTag()).getId());
                                ((Topics) tv.getTag()).setIsSelected(false);
                                tv.setSelected(false);
                            }
                        }
                    });
                    rootView.addView(ll);
                }
            }
        } catch (Exception e) {
            Crashlytics.logException(e);
            Log.d("MC4kException", Log.getStackTraceString(e));
        }

    }

    public void clearTopicsSelection() {
        try {
            mInflator = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            for (int i = 0; i < rootView.getChildCount(); i++) {
                LinearLayout ll = (LinearLayout) rootView.getChildAt(i);
                final TextView tv = ((TextView) ll.getChildAt(0));
                tv.setSelected(false);

                if (null == selectedTopicsMap.get(((Topics) tv.getTag()).getId())) {

                } else {
                    selectedTopicsMap.remove(((Topics) tv.getTag()).getId());
                    ((Topics) tv.getTag()).setIsSelected(false);
                    tv.setSelected(false);
                }
            }
        } catch (Exception e) {
            Crashlytics.logException(e);
            Log.d("MC4kException", Log.getStackTraceString(e));
        }

    }

    @Override
    public void onClick(View view, int position) {

    }
}
