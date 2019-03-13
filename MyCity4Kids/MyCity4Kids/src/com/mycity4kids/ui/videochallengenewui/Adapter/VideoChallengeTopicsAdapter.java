package com.mycity4kids.ui.videochallengenewui.Adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.models.Topics;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class VideoChallengeTopicsAdapter extends RecyclerView.Adapter<VideoChallengeTopicsAdapter.ViewHolder> {

    private RecyclerViewClickListener recyclerViewClickListener;
    private Context mContext;
    private int n = 1;
    private int m;
    private int count = 0;
    private LayoutInflater mInflator;
    private Topics challengeTopics;
    private ArrayList<String> challengeId;
    private ArrayList<String> Display_Name;
    private ArrayList<String> activeImageUrl;
    private ArrayList<String> activeStreamUrl;
    private ArrayList<String> info;

    public VideoChallengeTopicsAdapter(Context mContext, RecyclerViewClickListener recyclerViewClickListener, ArrayList<String> challengeId, ArrayList<String> Display_Name, ArrayList<String> activeImageUrl, ArrayList<String> activeStreamUrl, ArrayList<String> info) {
        this.recyclerViewClickListener = recyclerViewClickListener;
        this.mContext = mContext;
        mInflator = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.challengeId = challengeId;
        this.activeImageUrl = activeImageUrl;
        this.activeStreamUrl = activeStreamUrl;
        this.Display_Name = Display_Name;
        this.info = info;
        setHasStableIds(true);

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder viewHolder = null;
        View view = (View) mInflator.inflate(R.layout.horizontal_recycler_view_video_challenge, parent, false);
        viewHolder = new ViewHolder(view, recyclerViewClickListener);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        switch (position) {
            case 0:
                for (int i = challengeTopics.getChild().size() - 1; i >= 0; i--) {
                    if ("1".equals(challengeTopics.getChild().get(i).getPublicVisibility())) {
                        if (challengeTopics.getChild().get(i).getExtraData() != null) {
                            if ("1".equals(challengeTopics.getChild().get(i).getExtraData().get(0).getChallenge().getActive())) {
                                holder.challengeNameText.setText(challengeTopics.getChild().get(i).getDisplay_name());
                                holder.liveTextViewVideoChallenge.setVisibility(View.VISIBLE);
                                challengeId.add(challengeTopics.getChild().get(i).getId());
                                Display_Name.add(challengeTopics.getChild().get(i).getDisplay_name());
                                if (!StringUtils.isNullOrEmpty(challengeTopics.getChild().get(i).getExtraData().get(0).getChallenge().getRules())) {
                                    info.add(challengeTopics.getChild().get(i).getExtraData().get(0).getChallenge().getRules());
                                } else {
                                    info.add("<ol>\n<li><b>Rules</b></li>\n<li><b>Rules</b></li>\n<li><b>Rules</b></li>\n<li><b>Rules</b></li>\n<li><b>Rules</b></li>\n</ol>");
                                }
                                try {
                                    Picasso.with(mContext).load(challengeTopics.getChild().get(i).getExtraData().get(0).getChallenge().getImageUrl()).placeholder(R.drawable.default_article).error(R.drawable.default_article)
                                            .fit().into(holder.cureentChallengesImage);
                                    activeImageUrl.add(challengeTopics.getChild().get(i).getExtraData().get(0).getChallenge().getImageUrl());
                                    activeStreamUrl.add(challengeTopics.getChild().get(i).getExtraData().get(0).getChallenge().getVideoUrl());
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
                            challengeId.add(challengeTopics.getChild().get(j).getId());
                            Display_Name.add(challengeTopics.getChild().get(j).getDisplay_name());
                            holder.challengeNameText.setText(challengeTopics.getChild().get(j).getDisplay_name());
                            if (!StringUtils.isNullOrEmpty(challengeTopics.getChild().get(j).getExtraData().get(0).getChallenge().getRules())) {
                                info.add(challengeTopics.getChild().get(j).getExtraData().get(0).getChallenge().getRules());
                            } else {
                                info.add("");
                            }
                            try {
                                Picasso.with(mContext).load(challengeTopics.getChild().get(j).getExtraData().get(0).getChallenge().getImageUrl()).placeholder(R.drawable.default_article).error(R.drawable.default_article)
                                        .fit().into(holder.cureentChallengesImage);
                                activeImageUrl.add(challengeTopics.getChild().get(j).getExtraData().get(0).getChallenge().getImageUrl());
                                activeStreamUrl.add(challengeTopics.getChild().get(j).getExtraData().get(0).getChallenge().getVideoUrl());
                            } catch (Exception e) {
                                holder.cureentChallengesImage.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.default_article));
                            }
                            n++;
                            break;
                            //  }
                        } else if (challengeTopics.getChild().get(j).getExtraData() == null) {
                            n++;
                        } /*else if (challengeTopics.getChild().get(j).getPublicVisibility().equals("0")) {
                            n++;
                            break;*/
                        //}
                    } else {
                        m--;
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
                count++;
            }
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView cureentChallengesImage, infoImage;

        TextView liveTextViewVideoChallenge, challengeNameText;

        public ViewHolder(View itemView, RecyclerViewClickListener recyclerViewClickListener) {
            super(itemView);
            cureentChallengesImage = (ImageView) itemView.findViewById(R.id.tagImageView);
            liveTextViewVideoChallenge = (TextView) itemView.findViewById(R.id.liveTextViewVideoChallenge);
            infoImage = (ImageView) itemView.findViewById(R.id.info);
            challengeNameText = (TextView) itemView.findViewById(R.id.challengeNameText);
            cureentChallengesImage.setOnClickListener(this);
            infoImage.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            recyclerViewClickListener.onClick(view, getAdapterPosition(), challengeId, Display_Name, challengeTopics, activeImageUrl, activeStreamUrl, info);

        }
    }


    public interface RecyclerViewClickListener {
        void onClick(View view, int position, ArrayList<String> challengeId, ArrayList<String> Display_Name, Topics articledatamodelsnew, ArrayList<String> imageUrl, ArrayList<String> activeStreamUrl, ArrayList<String> info);

    }
}
