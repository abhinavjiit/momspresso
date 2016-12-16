package com.mycity4kids.ui.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mycity4kids.R;
import com.mycity4kids.gtmutils.GTMEventType;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.interfaces.ITopicSelectionEvent;
import com.mycity4kids.models.Topics;
import com.mycity4kids.newmodels.SelectTopic;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.widget.MyBounceInterpolator;

import org.apmem.tools.layouts.FlowLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SearchTopicsSplashAdapter extends BaseAdapter implements Filterable {

    private Context mContext;
    private LayoutInflater mInflator;

    private HashMap<String, Topics> selectedTopicsMap;
    private ITopicSelectionEvent iTopicSelectionEvent;
    ContactsFilter mContactsFilter;
    private ArrayList<SelectTopic> selectTopicArrayList;
    private ArrayList<SelectTopic> filteredSelectTopicArrayList;
    String userId;

    /*
    * Adapter for Filtering through all items of all Topics(sub-sub-topics/sub-topics)
     * and also all the items of a Main Topic(sub-sub-topics/sub-topics)
    * */
    public SearchTopicsSplashAdapter(Context pContext, HashMap<String, Topics> selectedTopicsMap, ArrayList<SelectTopic> selectTopicArrayList) {

        mInflator = (LayoutInflater) pContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = pContext;
        this.selectedTopicsMap = selectedTopicsMap;
        iTopicSelectionEvent = (ITopicSelectionEvent) pContext;
        this.selectTopicArrayList = selectTopicArrayList;
        this.filteredSelectTopicArrayList = selectTopicArrayList;
        userId = SharedPrefUtils.getUserDetailModel(mContext).getDynamoId();
    }

    public void selectAllItems(View childAt) {
        List<Topics> allItems = filteredSelectTopicArrayList.get(0).getChildTopics();
        boolean areAllItemsSelected = true;
//        for (int i = 0; i < allItems.size(); i++) {
//            LinearLayout lll = (LinearLayout) ((FlowLayout) ((RelativeLayout) childAt).findViewById(R.id.rootView)).getChildAt(i);
//            final TextView tv = (TextView) ((LinearLayout) lll.getChildAt(0)).getChildAt(0);
//            if(((Topics) tv.getTag()).isSelected()){
//
//            }
//        }
        for (int i = 0; i < allItems.size(); i++) {
            LinearLayout lll = (LinearLayout) ((FlowLayout) ((RelativeLayout) childAt).findViewById(R.id.rootView)).getChildAt(i);
            final TextView tv = (TextView) ((LinearLayout) lll.getChildAt(0)).getChildAt(0);
//            tv.setText(allItems.get(i).getDisplay_name().toUpperCase());
//            tv.setTag(allItems.get(i));
            final LinearLayout ll_main = (LinearLayout) lll.getChildAt(0);
            selectedTopicsMap.put(((Topics) tv.getTag()).getId(), (Topics) tv.getTag());
            ((Topics) tv.getTag()).setIsSelected(true);
            ll_main.setBackgroundResource(R.drawable.search_topics_filled_bg);
            tv.setTextColor(ContextCompat.getColor(mContext, R.color.white_color));
        }
        iTopicSelectionEvent.onTopicSelectionChanged(selectedTopicsMap.size(), 1);
    }

    public void deselectAllItems(View childAt) {
        List<Topics> allItems = filteredSelectTopicArrayList.get(0).getChildTopics();
        for (int i = 0; i < allItems.size(); i++) {
            LinearLayout lll = (LinearLayout) ((FlowLayout) ((RelativeLayout) childAt).findViewById(R.id.rootView)).getChildAt(i);
            final TextView tv = (TextView) ((LinearLayout) lll.getChildAt(0)).getChildAt(0);
            final LinearLayout ll_main = (LinearLayout) lll.getChildAt(0);
            selectedTopicsMap.remove(((Topics) tv.getTag()).getId());
            ((Topics) tv.getTag()).setIsSelected(false);
            ll_main.setBackgroundResource(R.drawable.search_topics_transparent_bg);
            tv.setTextColor(ContextCompat.getColor(mContext, R.color.splashtopics_search_topic_item_text));
        }
        iTopicSelectionEvent.onTopicSelectionChanged(selectedTopicsMap.size(), 0);
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
        return filteredSelectTopicArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return filteredSelectTopicArrayList.get(position);
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
//        holder.anim.setAnimationListener(animationListener);
        holder.parentCategoryTextView.setText(filteredSelectTopicArrayList.get(position).getDisplayName().toUpperCase());

        final List<Topics> top3Cat = filteredSelectTopicArrayList.get(position).getChildTopics();
        holder.rootView.removeAllViews();

        for (int i = 0; i < top3Cat.size(); i++) {
            LinearLayout ll = (LinearLayout) mInflator.inflate(R.layout.search_topic_item, null);
            final TextView tv = (TextView) ((LinearLayout) ll.getChildAt(0)).getChildAt(0);
            tv.setText(top3Cat.get(i).getDisplay_name().toUpperCase());
            tv.setTag(top3Cat.get(i));
            final LinearLayout ll_main = (LinearLayout) ll.getChildAt(0);
            if (null == selectedTopicsMap.get(((Topics) tv.getTag()).getId())) {
                ll_main.setBackgroundResource(R.drawable.search_topics_transparent_bg);
                tv.setTextColor(ContextCompat.getColor(mContext, R.color.splashtopics_search_topic_item_text));
            } else {
                ll_main.setBackgroundResource(R.drawable.search_topics_filled_bg);
                tv.setTextColor(ContextCompat.getColor(mContext, R.color.white_color));
            }

            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int action = 0;
                    if (null == selectedTopicsMap.get(((Topics) tv.getTag()).getId())) {
                        Utils.pushEventFollowUnfollowTopic(mContext, GTMEventType.TOPIC_FOLLOWED_UNFOLLOWED_CLICKED_EVENT, userId, "SearchOrDetailsTopicList", "follow", ((Topics) tv.getTag()).getDisplay_name() + ":" + ((Topics) tv.getTag()).getId());
                        selectedTopicsMap.put(((Topics) tv.getTag()).getId(), (Topics) tv.getTag());
                        ((Topics) tv.getTag()).setIsSelected(true);
                        ll_main.setBackgroundResource(R.drawable.search_topics_filled_bg);
                        tv.setTextColor(ContextCompat.getColor(mContext, R.color.white_color));
                        action = 1;
                    } else {
                        Utils.pushEventFollowUnfollowTopic(mContext, GTMEventType.TOPIC_FOLLOWED_UNFOLLOWED_CLICKED_EVENT, userId, "SearchOrDetailsTopicList", "unfollow", ((Topics) tv.getTag()).getDisplay_name() + ":" + ((Topics) tv.getTag()).getId());
                        selectedTopicsMap.remove(((Topics) tv.getTag()).getId());
                        ((Topics) tv.getTag()).setIsSelected(false);
                        ll_main.setBackgroundResource(R.drawable.search_topics_transparent_bg);
                        tv.setTextColor(ContextCompat.getColor(mContext, R.color.splashtopics_search_topic_item_text));
                        action = 0;
                    }
                    iTopicSelectionEvent.onTopicSelectionChanged(selectedTopicsMap.size(), action);
                    ll_main.startAnimation(holder.anim);
                }
            });

            holder.rootView.addView(ll);
        }

        return view;
    }

    @Override
    public Filter getFilter() {
        if (mContactsFilter == null)
            mContactsFilter = new ContactsFilter();

        return mContactsFilter;
    }

    private class ContactsFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            // Create a FilterResults object
            FilterResults results = new FilterResults();

            // If the constraint (search string/pattern) is null
            // or its length is 0, i.e., its empty then
            // we just set the `values` property to the
            // original contacts list which contains all of them
            if (constraint == null || constraint.length() == 0) {
                results.values = selectTopicArrayList;
                results.count = selectTopicArrayList.size();
            } else {
                // Some search copnstraint has been passed
                // so let's filter accordingly
                ArrayList<SelectTopic> filteredContacts = new ArrayList<SelectTopic>();

                // We'll go through all the contacts and see
                // if they contain the supplied string
                for (SelectTopic st : selectTopicArrayList) {
                    SelectTopic std = new SelectTopic();
                    ArrayList<Topics> tt = new ArrayList<>();
                    for (Topics c : st.getChildTopics()) {
                        if (c.getDisplay_name().toUpperCase().contains(constraint.toString().toUpperCase())) {
                            // if `contains` == true then add it
                            // to our filtered list
                            tt.add(c);
                        }
                    }
                    if (!tt.isEmpty()) {
                        std.setChildTopics(tt);
                        std.setId(st.getId());
                        std.setDisplayName(st.getDisplayName());
                        filteredContacts.add(std);
                    }
                }

                // Finally set the filtered values and size/count
                results.values = filteredContacts;
                results.count = filteredContacts.size();
            }

            // Return our FilterResults object
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredSelectTopicArrayList = (ArrayList<SelectTopic>) results.values;
            notifyDataSetChanged();
        }
    }

}
