package com.mycity4kids.ui.adapter;

import android.content.Context;
import androidx.core.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TopicsSplashAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflator;

    private HashMap<String, Topics> selectedTopicsMap;
    private ITopicSelectionEvent iTopicSelectionEvent;
    private ArrayList<SelectTopic> filteredSelectTopicArrayList;
    String userId;

    public TopicsSplashAdapter(Context pContext, HashMap<String, Topics> selectedTopicsMap, ArrayList<SelectTopic> selectTopicArrayList) {

        mInflator = (LayoutInflater) pContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = pContext;
        this.selectedTopicsMap = selectedTopicsMap;
        iTopicSelectionEvent = (ITopicSelectionEvent) pContext;
        this.filteredSelectTopicArrayList = selectTopicArrayList;
        userId = SharedPrefUtils.getUserDetailModel(mContext).getDynamoId();
    }

    class ViewHolder {
        RelativeLayout rootLayout;
        ImageView topicsBackgroundImageView;
        TextView parentCategoryTextView;
        LinearLayout popularSubcatLL_1;
        LinearLayout popularSubcatLL_2;
        LinearLayout popularSubcatLL_3;
        LinearLayout popularSubcatLL_4;
        TextView popularSubCatTextView1;
        TextView popularSubCatTextView2;
        TextView popularSubCatTextView3;
        TextView popularSubCatTextView4;
        Animation anim1;
        Animation anim2;
        Animation anim3;
        Animation anim4;
        MyBounceInterpolator interpolator1;
        MyBounceInterpolator interpolator2;
        MyBounceInterpolator interpolator3;
        MyBounceInterpolator interpolator4;
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
            view = mInflator.inflate(R.layout.topics_splash_item, null);
            holder = new ViewHolder();

            holder.rootLayout = (RelativeLayout) view.findViewById(R.id.rootLayout);
            holder.topicsBackgroundImageView = (ImageView) view.findViewById(R.id.topicsBackgroundImageView);
            holder.parentCategoryTextView = (TextView) view.findViewById(R.id.parentCategoryTextView);
            holder.popularSubcatLL_1 = (LinearLayout) view.findViewById(R.id.popularSubcatLL_1);
            holder.popularSubcatLL_2 = (LinearLayout) view.findViewById(R.id.popularSubcatLL_2);
            holder.popularSubcatLL_3 = (LinearLayout) view.findViewById(R.id.popularSubcatLL_3);
            holder.popularSubcatLL_4 = (LinearLayout) view.findViewById(R.id.popularSubcatLL_4);

            holder.popularSubCatTextView1 = (TextView) view.findViewById(R.id.popularSubcatTextView_1);
            holder.popularSubCatTextView2 = (TextView) view.findViewById(R.id.popularSubcatTextView_2);
            holder.popularSubCatTextView3 = (TextView) view.findViewById(R.id.popularSubcatTextView_3);
            holder.popularSubCatTextView4 = (TextView) view.findViewById(R.id.popularSubcatTextView_4);

            holder.anim1 = AnimationUtils.loadAnimation(mContext, R.anim.bounce);
            holder.anim2 = AnimationUtils.loadAnimation(mContext, R.anim.bounce);
            holder.anim3 = AnimationUtils.loadAnimation(mContext, R.anim.bounce);
            holder.anim4 = AnimationUtils.loadAnimation(mContext, R.anim.bounce);

            holder.interpolator1 = new MyBounceInterpolator(0.05, 10);
            holder.interpolator2 = new MyBounceInterpolator(0.05, 10);
            holder.interpolator3 = new MyBounceInterpolator(0.05, 10);
            holder.interpolator4 = new MyBounceInterpolator(0.05, 10);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.anim1.setInterpolator(holder.interpolator1);
        holder.anim2.setInterpolator(holder.interpolator2);
        holder.anim3.setInterpolator(holder.interpolator3);
        holder.anim4.setInterpolator(holder.interpolator4);

        holder.parentCategoryTextView.setText(filteredSelectTopicArrayList.get(position).getDisplayName().toUpperCase());

        final List<Topics> top3Cat = getDisplayedItem(filteredSelectTopicArrayList.get(position).getChildTopics());
        if (null != filteredSelectTopicArrayList.get(position).getBackgroundImageUrl()) {
            Picasso.get().load(filteredSelectTopicArrayList.get(position).getBackgroundImageUrl()).
                    placeholder(R.drawable.default_article).error(R.drawable.default_article).into(holder.topicsBackgroundImageView);
        } else {
            holder.topicsBackgroundImageView.setBackgroundResource(R.drawable.article_default);
        }

        if (top3Cat.size() > 3) {
            holder.popularSubCatTextView1.setVisibility(View.VISIBLE);
            holder.popularSubCatTextView1.setText(top3Cat.get(0).getDisplay_name().toUpperCase());
            holder.popularSubCatTextView2.setVisibility(View.VISIBLE);
            holder.popularSubCatTextView2.setText(top3Cat.get(1).getDisplay_name().toUpperCase());
            holder.popularSubCatTextView3.setVisibility(View.VISIBLE);
            holder.popularSubCatTextView3.setText(top3Cat.get(2).getDisplay_name().toUpperCase());
            holder.popularSubCatTextView4.setVisibility(View.VISIBLE);
            holder.popularSubCatTextView4.setText(top3Cat.get(3).getDisplay_name().toUpperCase());

            if (null == selectedTopicsMap.get(top3Cat.get(0).getId())) {
                holder.popularSubcatLL_1.setBackgroundResource(R.drawable.topics_transparent_bg);
                holder.popularSubCatTextView1.setTextColor(ContextCompat.getColor(mContext, R.color.white_color));
            } else {
                holder.popularSubcatLL_1.setBackgroundResource(R.drawable.topics_filled_bg);
                holder.popularSubCatTextView1.setTextColor(ContextCompat.getColor(mContext, R.color.red_drawer_selected));
            }

            if (null == selectedTopicsMap.get(top3Cat.get(1).getId())) {
                holder.popularSubcatLL_2.setBackgroundResource(R.drawable.topics_transparent_bg);
                holder.popularSubCatTextView2.setTextColor(ContextCompat.getColor(mContext, R.color.white_color));
            } else {
                holder.popularSubcatLL_2.setBackgroundResource(R.drawable.topics_filled_bg);
                holder.popularSubCatTextView2.setTextColor(ContextCompat.getColor(mContext, R.color.red_drawer_selected));
            }

            if (null == selectedTopicsMap.get(top3Cat.get(2).getId())) {
                holder.popularSubcatLL_3.setBackgroundResource(R.drawable.topics_transparent_bg);
                holder.popularSubCatTextView3.setTextColor(ContextCompat.getColor(mContext, R.color.white_color));
            } else {
                holder.popularSubcatLL_3.setBackgroundResource(R.drawable.topics_filled_bg);
                holder.popularSubCatTextView3.setTextColor(ContextCompat.getColor(mContext, R.color.red_drawer_selected));
            }

            if (null == selectedTopicsMap.get(top3Cat.get(3).getId())) {
                holder.popularSubcatLL_4.setBackgroundResource(R.drawable.topics_transparent_bg);
                holder.popularSubCatTextView4.setTextColor(ContextCompat.getColor(mContext, R.color.white_color));
            } else {
                holder.popularSubcatLL_4.setBackgroundResource(R.drawable.topics_filled_bg);
                holder.popularSubCatTextView4.setTextColor(ContextCompat.getColor(mContext, R.color.red_drawer_selected));
            }

        } else if (top3Cat.size() > 2) {
            holder.popularSubCatTextView1.setVisibility(View.VISIBLE);
            holder.popularSubCatTextView1.setText(top3Cat.get(0).getDisplay_name().toUpperCase());
            holder.popularSubCatTextView2.setVisibility(View.VISIBLE);
            holder.popularSubCatTextView2.setText(top3Cat.get(1).getDisplay_name().toUpperCase());
            holder.popularSubCatTextView3.setVisibility(View.VISIBLE);
            holder.popularSubCatTextView3.setText(top3Cat.get(2).getDisplay_name().toUpperCase());
            holder.popularSubCatTextView4.setVisibility(View.GONE);

            if (null == selectedTopicsMap.get(top3Cat.get(0).getId())) {
                holder.popularSubcatLL_1.setBackgroundResource(R.drawable.topics_transparent_bg);
                holder.popularSubCatTextView1.setTextColor(ContextCompat.getColor(mContext, R.color.white_color));
            } else {
                holder.popularSubcatLL_1.setBackgroundResource(R.drawable.topics_filled_bg);
                holder.popularSubCatTextView1.setTextColor(ContextCompat.getColor(mContext, R.color.red_drawer_selected));
            }

            if (null == selectedTopicsMap.get(top3Cat.get(1).getId())) {
                holder.popularSubcatLL_2.setBackgroundResource(R.drawable.topics_transparent_bg);
                holder.popularSubCatTextView2.setTextColor(ContextCompat.getColor(mContext, R.color.white_color));
            } else {
                holder.popularSubcatLL_2.setBackgroundResource(R.drawable.topics_filled_bg);
                holder.popularSubCatTextView2.setTextColor(ContextCompat.getColor(mContext, R.color.red_drawer_selected));
            }

            if (null == selectedTopicsMap.get(top3Cat.get(2).getId())) {
                holder.popularSubcatLL_3.setBackgroundResource(R.drawable.topics_transparent_bg);
                holder.popularSubCatTextView3.setTextColor(ContextCompat.getColor(mContext, R.color.white_color));
            } else {
                holder.popularSubcatLL_3.setBackgroundResource(R.drawable.topics_filled_bg);
                holder.popularSubCatTextView3.setTextColor(ContextCompat.getColor(mContext, R.color.red_drawer_selected));
            }

        } else if (top3Cat.size() == 2) {
            holder.popularSubCatTextView1.setVisibility(View.VISIBLE);
            holder.popularSubCatTextView1.setText(top3Cat.get(0).getDisplay_name().toUpperCase());
            holder.popularSubCatTextView2.setVisibility(View.VISIBLE);
            holder.popularSubCatTextView2.setText(top3Cat.get(1).getDisplay_name().toUpperCase());
            holder.popularSubCatTextView3.setVisibility(View.GONE);
            holder.popularSubCatTextView4.setVisibility(View.GONE);

            if (null == selectedTopicsMap.get(top3Cat.get(0).getId())) {
                holder.popularSubcatLL_1.setBackgroundResource(R.drawable.topics_transparent_bg);
                holder.popularSubCatTextView1.setTextColor(ContextCompat.getColor(mContext, R.color.white_color));
            } else {
                holder.popularSubcatLL_1.setBackgroundResource(R.drawable.topics_filled_bg);
                holder.popularSubCatTextView1.setTextColor(ContextCompat.getColor(mContext, R.color.red_drawer_selected));
            }

            if (null == selectedTopicsMap.get(top3Cat.get(1).getId())) {
                holder.popularSubcatLL_2.setBackgroundResource(R.drawable.topics_transparent_bg);
                holder.popularSubCatTextView2.setTextColor(ContextCompat.getColor(mContext, R.color.white_color));
            } else {
                holder.popularSubcatLL_2.setBackgroundResource(R.drawable.topics_filled_bg);
                holder.popularSubCatTextView2.setTextColor(ContextCompat.getColor(mContext, R.color.red_drawer_selected));
            }

        } else if (top3Cat.size() == 1) {
            holder.popularSubCatTextView1.setVisibility(View.VISIBLE);
            holder.popularSubCatTextView1.setText(top3Cat.get(0).getDisplay_name().toUpperCase());
            holder.popularSubCatTextView2.setVisibility(View.GONE);
            holder.popularSubCatTextView3.setVisibility(View.GONE);
            holder.popularSubCatTextView4.setVisibility(View.GONE);

            if (null == selectedTopicsMap.get(top3Cat.get(0).getId())) {
                holder.popularSubcatLL_1.setBackgroundResource(R.drawable.topics_transparent_bg);
                holder.popularSubCatTextView1.setTextColor(ContextCompat.getColor(mContext, R.color.white_color));
            } else {
                holder.popularSubcatLL_1.setBackgroundResource(R.drawable.topics_filled_bg);
                holder.popularSubCatTextView1.setTextColor(ContextCompat.getColor(mContext, R.color.red_drawer_selected));
            }

        } else {
            holder.popularSubCatTextView1.setVisibility(View.GONE);
            holder.popularSubCatTextView2.setVisibility(View.GONE);
            holder.popularSubCatTextView3.setVisibility(View.GONE);
            holder.popularSubCatTextView4.setVisibility(View.GONE);
        }

        holder.popularSubCatTextView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int action = 0;
                if (null == selectedTopicsMap.get(top3Cat.get(0).getId())) {
                    Log.d("FOLLOW 1", top3Cat.get(0).getDisplay_name() + ":" + top3Cat.get(0).getId());
//                    Utils.pushEventFollowUnfollowTopic(mContext, GTMEventType.TOPIC_FOLLOWED_UNFOLLOWED_CLICKED_EVENT, userId, "TopicsSplashList", "follow", top3Cat.get(0).getDisplay_name() + ":" + top3Cat.get(0).getId());
                    Utils.pushTopicFollowUnfollowEvent(mContext, GTMEventType.FOLLOW_TOPIC_CLICK_EVENT, userId, "TopicsSplashList", top3Cat.get(0).getDisplay_name() + "~" + top3Cat.get(0).getId());
                    selectedTopicsMap.put(top3Cat.get(0).getId(), top3Cat.get(0));
                    top3Cat.get(0).setIsSelected(true);
                    action = 1;
                    holder.popularSubcatLL_1.setBackgroundResource(R.drawable.topics_filled_bg);
                    holder.popularSubCatTextView1.setTextColor(ContextCompat.getColor(mContext, R.color.red_drawer_selected));
                } else {
                    Log.d("UNFOLLOW 1", top3Cat.get(0).getDisplay_name() + ":" + top3Cat.get(0).getId());
//                    Utils.pushEventFollowUnfollowTopic(mContext, GTMEventType.TOPIC_FOLLOWED_UNFOLLOWED_CLICKED_EVENT, userId, "TopicsSplashList", "unfollow", top3Cat.get(0).getDisplay_name() + ":" + top3Cat.get(0).getId());
                    Utils.pushTopicFollowUnfollowEvent(mContext, GTMEventType.UNFOLLOW_TOPIC_CLICK_EVENT, userId, "TopicsSplashList", top3Cat.get(0).getDisplay_name() + "~" + top3Cat.get(0).getId());
                    selectedTopicsMap.remove(top3Cat.get(0).getId());
                    top3Cat.get(0).setIsSelected(false);
                    action = 0;
                    holder.popularSubcatLL_1.setBackgroundResource(R.drawable.topics_transparent_bg);
                    holder.popularSubCatTextView1.setTextColor(ContextCompat.getColor(mContext, R.color.white_color));
                    holder.anim1.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            //Prevent shuffling data evertime a topic is selected or unselected
                            int selectedItem = 0;
                            for (int i = 0; i < top3Cat.size(); i++) {
                                if (top3Cat.get(i).isSelected()) {
                                    selectedItem++;
                                }
                            }
                            if (selectedItem > 3) {
                                notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {
                        }
                    });
                }
                iTopicSelectionEvent.onTopicSelectionChanged(selectedTopicsMap.size(), action);
                holder.popularSubcatLL_1.startAnimation(holder.anim1);
            }
        });

        holder.popularSubCatTextView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int action = 0;
                if (null == selectedTopicsMap.get(top3Cat.get(1).getId())) {
                    Log.d("FOLLOW 2", top3Cat.get(1).getDisplay_name() + ":" + top3Cat.get(1).getId());
//                    Utils.pushEventFollowUnfollowTopic(mContext, GTMEventType.TOPIC_FOLLOWED_UNFOLLOWED_CLICKED_EVENT, userId, "TopicsSplashList", "follow", top3Cat.get(1).getDisplay_name() + ":" + top3Cat.get(1).getId());
                    Utils.pushTopicFollowUnfollowEvent(mContext, GTMEventType.FOLLOW_TOPIC_CLICK_EVENT, userId, "TopicsSplashList", top3Cat.get(1).getDisplay_name() + "~" + top3Cat.get(1).getId());
                    selectedTopicsMap.put(top3Cat.get(1).getId(), top3Cat.get(1));
                    top3Cat.get(1).setIsSelected(true);
                    action = 1;
                    holder.popularSubcatLL_2.setBackgroundResource(R.drawable.topics_filled_bg);
                    holder.popularSubCatTextView2.setTextColor(ContextCompat.getColor(mContext, R.color.red_drawer_selected));
                } else {
                    Log.d("UNFOLLOW 2", top3Cat.get(1).getDisplay_name() + ":" + top3Cat.get(1).getId());
//                    Utils.pushEventFollowUnfollowTopic(mContext, GTMEventType.TOPIC_FOLLOWED_UNFOLLOWED_CLICKED_EVENT, userId, "TopicsSplashList", "unfollow", top3Cat.get(1).getDisplay_name() + ":" + top3Cat.get(1).getId());
                    Utils.pushTopicFollowUnfollowEvent(mContext, GTMEventType.UNFOLLOW_TOPIC_CLICK_EVENT, userId, "TopicsSplashList", top3Cat.get(1).getDisplay_name() + "~" + top3Cat.get(1).getId());
                    selectedTopicsMap.remove(top3Cat.get(1).getId());
                    top3Cat.get(1).setIsSelected(false);
                    action = 0;
                    holder.popularSubcatLL_2.setBackgroundResource(R.drawable.topics_transparent_bg);
                    holder.popularSubCatTextView2.setTextColor(ContextCompat.getColor(mContext, R.color.white_color));
                    holder.anim2.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            //Prevent shuffling data evertime a topic is selected or unselected
                            int selectedItem = 0;
                            for (int i = 0; i < top3Cat.size(); i++) {
                                if (top3Cat.get(i).isSelected()) {
                                    selectedItem++;
                                }
                            }
                            if (selectedItem > 3) {
                                notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {
                        }
                    });
                }
                iTopicSelectionEvent.onTopicSelectionChanged(selectedTopicsMap.size(), action);
                holder.popularSubcatLL_2.startAnimation(holder.anim2);
            }
        });

        holder.popularSubCatTextView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int action = 0;
                if (null == selectedTopicsMap.get(top3Cat.get(2).getId())) {
                    Log.d("FOLLOW 3", top3Cat.get(2).getDisplay_name() + ":" + top3Cat.get(2).getId());
//                    Utils.pushEventFollowUnfollowTopic(mContext, GTMEventType.TOPIC_FOLLOWED_UNFOLLOWED_CLICKED_EVENT, userId, "TopicsSplashList", "follow", top3Cat.get(2).getDisplay_name() + ":" + top3Cat.get(2).getId());
                    Utils.pushTopicFollowUnfollowEvent(mContext, GTMEventType.FOLLOW_TOPIC_CLICK_EVENT, userId, "TopicsSplashList", top3Cat.get(2).getDisplay_name() + "~" + top3Cat.get(2).getId());
                    top3Cat.get(2).setIsSelected(true);
                    action = 1;
                    selectedTopicsMap.put(top3Cat.get(2).getId(), top3Cat.get(2));
                    holder.popularSubcatLL_3.setBackgroundResource(R.drawable.topics_filled_bg);
                    holder.popularSubCatTextView3.setTextColor(ContextCompat.getColor(mContext, R.color.red_drawer_selected));
                } else {
                    Log.d("UNFOLLOW 3", top3Cat.get(2).getDisplay_name() + ":" + top3Cat.get(2).getId());
//                    Utils.pushEventFollowUnfollowTopic(mContext, GTMEventType.TOPIC_FOLLOWED_UNFOLLOWED_CLICKED_EVENT, userId, "TopicsSplashList", "unfollow", top3Cat.get(2).getDisplay_name() + ":" + top3Cat.get(2).getId());
                    Utils.pushTopicFollowUnfollowEvent(mContext, GTMEventType.UNFOLLOW_TOPIC_CLICK_EVENT, userId, "TopicsSplashList", top3Cat.get(2).getDisplay_name() + "~" + top3Cat.get(2).getId());
                    selectedTopicsMap.remove(top3Cat.get(2).getId());
                    top3Cat.get(2).setIsSelected(false);
                    action = 0;
                    holder.popularSubcatLL_3.setBackgroundResource(R.drawable.topics_transparent_bg);
                    holder.popularSubCatTextView3.setTextColor(ContextCompat.getColor(mContext, R.color.white_color));
                    holder.anim3.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            //Prevent shuffling data evertime a topic is selected or unselected
                            int selectedItem = 0;
                            for (int i = 0; i < top3Cat.size(); i++) {
                                if (top3Cat.get(i).isSelected()) {
                                    selectedItem++;
                                }
                            }
                            if (selectedItem > 3) {
                                notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {
                        }
                    });
                }
                iTopicSelectionEvent.onTopicSelectionChanged(selectedTopicsMap.size(), action);
                holder.popularSubcatLL_3.startAnimation(holder.anim3);
            }
        });

        holder.popularSubCatTextView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int action = 0;
                if (null == selectedTopicsMap.get(top3Cat.get(3).getId())) {
                    Log.d("FOLLOW 4", top3Cat.get(3).getDisplay_name() + ":" + top3Cat.get(3).getId());
//                    Utils.pushEventFollowUnfollowTopic(mContext, GTMEventType.TOPIC_FOLLOWED_UNFOLLOWED_CLICKED_EVENT, userId, "TopicsSplashList", "follow", top3Cat.get(3).getDisplay_name() + ":" + top3Cat.get(3).getId());
                    Utils.pushTopicFollowUnfollowEvent(mContext, GTMEventType.FOLLOW_TOPIC_CLICK_EVENT, userId, "TopicsSplashList", top3Cat.get(3).getDisplay_name() + "~" + top3Cat.get(3).getId());
                    top3Cat.get(3).setIsSelected(true);
                    action = 1;
                    selectedTopicsMap.put(top3Cat.get(3).getId(), top3Cat.get(3));
                    holder.popularSubcatLL_4.setBackgroundResource(R.drawable.topics_filled_bg);
                    holder.popularSubCatTextView4.setTextColor(ContextCompat.getColor(mContext, R.color.red_drawer_selected));
                } else {
                    Log.d("UNFOLLOW 4", top3Cat.get(3).getDisplay_name() + ":" + top3Cat.get(3).getId());
//                    Utils.pushEventFollowUnfollowTopic(mContext, GTMEventType.TOPIC_FOLLOWED_UNFOLLOWED_CLICKED_EVENT, userId, "TopicsSplashList", "unfollow", top3Cat.get(3).getDisplay_name() + ":" + top3Cat.get(3).getId());
                    Utils.pushTopicFollowUnfollowEvent(mContext, GTMEventType.UNFOLLOW_TOPIC_CLICK_EVENT, userId, "TopicsSplashList", top3Cat.get(3).getDisplay_name() + "~" + top3Cat.get(3).getId());
                    selectedTopicsMap.remove(top3Cat.get(3).getId());
                    top3Cat.get(3).setIsSelected(false);
                    action = 0;
                    holder.popularSubcatLL_4.setBackgroundResource(R.drawable.topics_transparent_bg);
                    holder.popularSubCatTextView4.setTextColor(ContextCompat.getColor(mContext, R.color.white_color));
                    holder.anim4.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            //Prevent shuffling data evertime a topic is selected or unselected
                            int selectedItem = 0;
                            for (int i = 0; i < top3Cat.size(); i++) {
                                if (top3Cat.get(i).isSelected()) {
                                    selectedItem++;
                                }
                            }
                            if (selectedItem > 3) {
                                notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {
                        }
                    });
                }
                iTopicSelectionEvent.onTopicSelectionChanged(selectedTopicsMap.size(), action);
                holder.popularSubcatLL_4.startAnimation(holder.anim4);
            }
        });
        return view;
    }

    private List<Topics> getDisplayedItem(ArrayList<Topics> childTopics) {
        List<Topics> displayList = new ArrayList<>();

        //Sorting items by selected unselected with selected at top.
        for (int i = 0; i < childTopics.size(); i++) {
            if (childTopics.get(i).isSelected()) {
                displayList.add(childTopics.get(i));
            }
        }
        for (int i = 0; i < childTopics.size(); i++) {
            if (!childTopics.get(i).isSelected()) {
                displayList.add(childTopics.get(i));
            }
        }
        return displayList;
    }
}
