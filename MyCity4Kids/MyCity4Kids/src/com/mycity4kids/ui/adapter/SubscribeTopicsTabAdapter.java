package com.mycity4kids.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mycity4kids.R;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.Topics;
import com.mycity4kids.newmodels.SelectTopic;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.ui.activity.SubscribeTopicsActivity;
import com.mycity4kids.widget.MyBounceInterpolator;

import org.apmem.tools.layouts.FlowLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SubscribeTopicsTabAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflator;

    private HashMap<String, Topics> selectedTopicsMap;
    //    private ITopicSelectionEvent iTopicSelectionEvent;
    private ArrayList<SelectTopic> selectTopicArrayList;
    String userId;
    int tabPosition;

    /*
    * Adapter for Filtering through all items of all Topics(sub-sub-topics/sub-topics)
     * and also all the items of a Main Topic(sub-sub-topics/sub-topics)
    * */
    public SubscribeTopicsTabAdapter(Context pContext, ArrayList<SelectTopic> selectTopicArrayList, HashMap<String, Topics> selectedTopicsMap, int tabPosition) {

        mInflator = (LayoutInflater) pContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = pContext;
//        iTopicSelectionEvent = (ITopicSelectionEvent) pContext;
        this.selectedTopicsMap = selectedTopicsMap;
        userId = SharedPrefUtils.getUserDetailModel(mContext).getDynamoId();
        this.selectTopicArrayList = selectTopicArrayList;
        this.tabPosition = tabPosition;
    }


    class ViewHolder {
        RelativeLayout rootLayout;
        TextView parentCategoryTextView;
        FlowLayout rootView;
        Animation anim;
        MyBounceInterpolator interpolator;
    }

    @Override
    public int getCount() {
        return 1;
    }

    @Override
    public Object getItem(int position) {
        return selectTopicArrayList.get(tabPosition);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {

        final ViewHolder holder;
        if (view == null) {
            view = mInflator.inflate(R.layout.search_topics_splash_item, null);
            holder = new ViewHolder();

            holder.rootLayout = (RelativeLayout) view.findViewById(R.id.rootLayout);
            holder.rootView = (FlowLayout) view.findViewById(R.id.rootView);
            holder.parentCategoryTextView = (TextView) view.findViewById(R.id.parentCategoryTextView);
            holder.anim = AnimationUtils.loadAnimation(mContext, R.anim.bounce);
            holder.interpolator = new MyBounceInterpolator(0.05, 10);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        holder.parentCategoryTextView.setText(selectTopicArrayList.get(tabPosition).getDisplayName().toUpperCase());

        final List<Topics> top3Cat = selectTopicArrayList.get(tabPosition).getChildTopics();
        holder.rootView.removeAllViews();

        for (int i = 0; i < top3Cat.size(); i++) {
            LinearLayout ll = (LinearLayout) mInflator.inflate(R.layout.topic_follow_unfollow_item, null);
            final TextView tv = (TextView) ll.getChildAt(0);
            tv.setText(top3Cat.get(i).getDisplay_name().toUpperCase());
            tv.setTag(top3Cat.get(i));
            if (null == selectedTopicsMap.get(((Topics) tv.getTag()).getId())) {
                tv.setSelected(false);
            } else {
                tv.setSelected(true);
            }

            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null == selectedTopicsMap.get(((Topics) tv.getTag()).getId())) {
                        selectedTopicsMap.put(((Topics) tv.getTag()).getId(), (Topics) tv.getTag());
                        ((Topics) tv.getTag()).setIsSelected(true);
                        tv.setSelected(true);
                        Utils.pushFollowTopicEvent(mContext, "FollowTopicScreen", SharedPrefUtils.getUserDetailModel(mContext).getDynamoId(), ((Topics) tv.getTag()).getId() + "~" + ((Topics) tv.getTag()).getDisplay_name());
                        if (mContext != null)
                            ((SubscribeTopicsActivity) mContext).topicSelectionChanged();
                    } else {
                        selectedTopicsMap.remove(((Topics) tv.getTag()).getId());
                        ((Topics) tv.getTag()).setIsSelected(false);
                        tv.setSelected(false);
                        Utils.pushUnfollowTopicEvent(mContext, "FollowTopicScreen", SharedPrefUtils.getUserDetailModel(mContext).getDynamoId(), ((Topics) tv.getTag()).getId() + "~" + ((Topics) tv.getTag()).getDisplay_name());
                        if (mContext != null)
                            ((SubscribeTopicsActivity) mContext).topicSelectionChanged();
                    }
                }
            });

            holder.rootView.addView(ll);
        }

        return view;
    }

}
