package com.mycity4kids.ui.videochallengenewui.Adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mycity4kids.R;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.models.Topics;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class VideoChallengeTopicsAdapter extends RecyclerView.Adapter<VideoChallengeTopicsAdapter.ViewHolder> {

    private Context mContext;
    private int n = 1;
    private int m;
    int count = 0;
    private LayoutInflater mInflator;
    private Topics challengeTopics;

    public VideoChallengeTopicsAdapter(Context mContext) {
        this.mContext = mContext;
        mInflator = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        setHasStableIds(true);

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder viewHolder = null;
        View view = (View) mInflator.inflate(R.layout.horizontal_recycler_view_video_challenge, parent, false);
        viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {


        //  Picasso.with(mContext).load(challengeTopics.get(position).getExtraData().)


        switch (position) {
            case 0:
                for (int i = challengeTopics.getChild().size() - 1; i >= 0; i--) {
                    if ("1".equals(challengeTopics.getChild().get(i).getPublicVisibility())) {
                        if (challengeTopics.getChild().get(i).getExtraData() != null) {
                            if ("1".equals(challengeTopics.getChild().get(i).getExtraData().get(0).getChallenge().getActive())) {

                                holder.liveTextViewVideoChallenge.setVisibility(View.VISIBLE);
                                //    challengeId.add(articleDataModelsNew.getChild().get(i).getId());
                                // holder.storyTitleTextView.setText("Take This Week's 100 Word Story Challenge");
                                //    Display_Name.add(articleDataModelsNew.getChild().get(i).getDisplay_name());
                                //   holder.storyTitleTextView.setVisibility(View.GONE);
                                //    holder.titleTextUnderLine.setVisibility(View.GONE);
                                //if (3 == (articleDataModelsNew.getChild().get(i).getExtraData().get(0).getChallenge().getType())) {
                                //    holder.imageBody.setVisibility(View.VISIBLE);
                                try {
                                    //    Glide.with(mContext).load(challengeTopics.getChild().get(i).getExtraData().get(0).getChallenge().getImageUrl()).into(holder.cureentChallengesImage);
                                    Picasso.with(mContext).load(challengeTopics.getChild().get(i).getExtraData().get(0).getChallenge().getImageUrl()).placeholder(R.drawable.default_article).error(R.drawable.default_article)
                                            .fit().into(holder.cureentChallengesImage);
                                    // activeImageUrl.add(articleDataModelsNew.getChild().get(i).getExtraData().get(0).getChallenge().getImageUrl());
                                    //activeStreamUrl.add(articleDataModelsNew.getChild().get(i).getExtraData().get(0).getChallenge().getVideoUrl());
                                } catch (Exception e) {
                                    holder.cureentChallengesImage.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.default_article));
                                }
                                m = i;
                                //  }
                                break;
                            }
                        }
                    }


                }
                break;
            default:
                for (int j = m - n; j >= 0; j--) {
                    if ("1".equals(challengeTopics.getChild().get(j).getPublicVisibility())) {
                        if (challengeTopics.getChild().get(j).getExtraData() != null) {
                            //  holder.rootView.setVisibility(View.VISIBLE);
                            //holder.liveTextViewVideoChallenge.setVisibility(View.GONE);

                            //if ("1".equals(articleDataModelsNew.getChild().get(j).getExtraData().get(0).getChallenge().getActive())) {
                           /* if (position != 1) {
                                holder.previousAndThisWeekTextView.setVisibility(View.GONE);
                            } else {
                                holder.previousAndThisWeekTextView.setVisibility(View.VISIBLE);
                                holder.previousAndThisWeekTextView.setText(R.string.previous_week_challenge);
                            }*/
                            //challengeId.add(articleDataModelsNew.getChild().get(j).getId());
                            //Display_Name.add(articleDataModelsNew.getChild().get(j).getDisplay_name());
                            // holder.storyTitleTextView.setText(R.string.previous_week_challenge);
                          /*  holder.storyTitleTextView.setVisibility(View.GONE);
                            holder.titleTextUnderLine.setVisibility(View.GONE);*/
                            //  holder.storytitle.setText(articleDataModelsNew.getChild().get(j).getDisplay_name());
                            //  if (3 == (articleDataModelsNew.getChild().get(j).getExtraData().get(0).getChallenge().getType())) {
                            //   holder.imageBody.setVisibility(View.VISIBLE);
                            try {
                                //  Glide.with(mContext).load(challengeTopics.getChild().get(j).getExtraData().get(0).getChallenge().getImageUrl()).into(holder.cureentChallengesImage);
                                Picasso.with(mContext).load(challengeTopics.getChild().get(j).getExtraData().get(0).getChallenge().getImageUrl()).placeholder(R.drawable.default_article).error(R.drawable.default_article)
                                        .fit().into(holder.cureentChallengesImage);
                                //    activeImageUrl.add(articleDataModelsNew.getChild().get(j).getExtraData().get(0).getChallenge().getImageUrl());
                                //   activeStreamUrl.add(articleDataModelsNew.getChild().get(j).getExtraData().get(0).getChallenge().getVideoUrl());
                            } catch (Exception e) {
                                holder.cureentChallengesImage.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.default_article));
                            }
                            n++;
                            break;
                            //  }
                        } else if (challengeTopics.getChild().get(j).getExtraData() == null) {
                            n++;
                        } else if (challengeTopics.getChild().get(j).getPublicVisibility().equals("0")) {
                            n++;
                            break;
                        }
                    }
                }
                break;


        }
    }

    @Override
    public int getItemCount() {
        return count;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setData(Topics challengeTopics) {
        this.challengeTopics = challengeTopics;
        for (int i = 0; i < challengeTopics.getChild().size(); i++) {
            if (AppConstants.PUBLIC_VISIBILITY.equals(challengeTopics.getChild().get(i).getPublicVisibility())) {
                // if ("1".equals(articleDataModelsNew.getChild().get(i).getExtraData().get(0).getChallenge().getActive())) {
                count++;

                //}
            }
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView cureentChallengesImage;
        TextView liveTextViewVideoChallenge;

        public ViewHolder(View itemView) {
            super(itemView);
            cureentChallengesImage = (ImageView) itemView.findViewById(R.id.tagImageView);
            liveTextViewVideoChallenge = (TextView) itemView.findViewById(R.id.liveTextViewVideoChallenge);
        }
    }
}
