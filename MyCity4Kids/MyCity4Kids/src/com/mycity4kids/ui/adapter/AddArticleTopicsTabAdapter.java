package com.mycity4kids.ui.adapter;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mycity4kids.R;
import com.mycity4kids.models.Topics;

import org.apmem.tools.layouts.FlowLayout;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by hemant on 19/7/17.
 */
public class AddArticleTopicsTabAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private LayoutInflater mInflator;

    private HashMap<String, Topics> selectedTopicsMap;
    //    private ITopicSelectionEvent iTopicSelectionEvent;
    private ArrayList<Topics> selectTopicArrayList;
    String userId;
    int tabPosition;
    RecyclerViewClickListener mListener;

    public AddArticleTopicsTabAdapter(Context pContext, ArrayList<Topics> selectTopicArrayList, HashMap<String, Topics> selectedTopicsMap, RecyclerViewClickListener listener) {
        mInflator = (LayoutInflater) pContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = pContext;
        this.selectTopicArrayList = selectTopicArrayList;
        this.selectedTopicsMap = selectedTopicsMap;
        this.mListener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        View v0 = mInflator.inflate(R.layout.search_topics_splash_item, parent, false);
        viewHolder = new SubSubTopicViewHolder(v0, mListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        SubSubTopicViewHolder vh1 = (SubSubTopicViewHolder) holder;
        configureTopicsViewHolder(vh1, position);

    }

    private void configureTopicsViewHolder(SubSubTopicViewHolder vh1, int position) {
//        vh1.textView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.red_drawer_selected));
        vh1.parentCategoryTextView.setText(selectTopicArrayList.get(position).getTitle());
        vh1.rootView.removeAllViews();
        for (int i = 0; i < selectTopicArrayList.get(position).getChild().size(); i++) {
            LinearLayout ll = (LinearLayout) mInflator.inflate(R.layout.topic_follow_unfollow_item, null);
            final TextView tv = ((TextView) ll.getChildAt(0));
            tv.setText(selectTopicArrayList.get(position).getChild().get(i).getDisplay_name().toUpperCase());
            tv.setTag(selectTopicArrayList.get(position).getChild().get(i));
//            final LinearLayout ll_main = (LinearLayout) ll.getChildAt(0);
            if (null == selectedTopicsMap.get(((Topics) tv.getTag()).getId())) {
                tv.setSelected(false);
//                ll_main.setBackgroundResource(R.drawable.search_topics_transparent_bg);
//                tv.setTextColor(ContextCompat.getColor(mContext, R.color.splashtopics_search_topic_item_text));
            } else {
                tv.setSelected(true);
//                ll_main.setBackgroundResource(R.drawable.search_topics_filled_bg);
//                tv.setTextColor(ContextCompat.getColor(mContext, R.color.white_color));
            }

            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int action = 0;
                    if (null == selectedTopicsMap.get(((Topics) tv.getTag()).getId())) {
//                        Utils.pushEventFollowUnfollowTopic(mContext, GTMEventType.TOPIC_FOLLOWED_UNFOLLOWED_CLICKED_EVENT, userId, "SearchOrDetailsTopicList", "follow", ((Topics) tv.getTag()).getDisplay_name() + ":" + ((Topics) tv.getTag()).getId());
//                        Utils.pushTopicFollowUnfollowEvent(mContext, GTMEventType.FOLLOW_TOPIC_CLICK_EVENT, userId, "SearchOrDetailsTopicList", ((Topics) tv.getTag()).getDisplay_name() + "~" + ((Topics) tv.getTag()).getId());
                        selectedTopicsMap.put(((Topics) tv.getTag()).getId(), (Topics) tv.getTag());
                        ((Topics) tv.getTag()).setIsSelected(true);
                        tv.setSelected(true);
//                        ll_main.setBackgroundResource(R.drawable.search_topics_filled_bg);
//                        tv.setTextColor(ContextCompat.getColor(mContext, R.color.white_color));
                        action = 1;
                    } else {
//                        Utils.pushEventFollowUnfollowTopic(mContext, GTMEventType.TOPIC_FOLLOWED_UNFOLLOWED_CLICKED_EVENT, userId, "SearchOrDetailsTopicList", "unfollow", ((Topics) tv.getTag()).getDisplay_name() + ":" + ((Topics) tv.getTag()).getId());
//                        Utils.pushTopicFollowUnfollowEvent(mContext, GTMEventType.UNFOLLOW_TOPIC_CLICK_EVENT, userId, "SearchOrDetailsTopicList", ((Topics) tv.getTag()).getDisplay_name() + "~" + ((Topics) tv.getTag()).getId());
                        selectedTopicsMap.remove(((Topics) tv.getTag()).getId());
                        ((Topics) tv.getTag()).setIsSelected(false);
                        tv.setSelected(false);
//                        ll_main.setBackgroundResource(R.drawable.search_topics_transparent_bg);
//                        tv.setTextColor(ContextCompat.getColor(mContext, R.color.splashtopics_search_topic_item_text));
                        action = 0;
                    }
//                    iTopicSelectionEvent.onTopicSelectionChanged(selectedTopicsMap.size(), action);
                }
            });

            vh1.rootView.addView(ll);
        }
    }

    @Override
    public int getItemCount() {
        return selectTopicArrayList == null ? 0 : selectTopicArrayList.size();
    }

//    public void refreshArticleList(ArrayList<SearchArticleResult> newList) {
//        this.articleDataModelsNew = newList;
//    }

    public class SubSubTopicViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView parentCategoryTextView;
        FlowLayout rootView;

        public SubSubTopicViewHolder(View itemView, RecyclerViewClickListener listener) {
            super(itemView);
            rootView = (FlowLayout) itemView.findViewById(R.id.rootView);
            parentCategoryTextView = (TextView) itemView.findViewById(R.id.parentCategoryTextView);

        }

        @Override
        public void onClick(View v) {
            mListener.onClick(v, getAdapterPosition());
        }
    }

    public interface RecyclerViewClickListener {
        void onClick(View view, int position);
    }

}