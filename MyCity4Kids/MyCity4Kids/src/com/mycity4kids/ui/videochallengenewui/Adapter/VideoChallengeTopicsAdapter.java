package com.mycity4kids.ui.videochallengenewui.Adapter;

import android.content.Context;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
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
    private ArrayList<Topics> challengeTopics = new ArrayList<>();
    private ArrayList<String> challengeId;
    private ArrayList<String> Display_Name;
    private ArrayList<String> activeImageUrl;
    private ArrayList<String> activeStreamUrl;
    private ArrayList<String> info;
    private ArrayList<String> mappedCategory = new ArrayList<>();
    private ArrayList<Integer> max_Duration = new ArrayList<>();

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

       /* switch (position) {
            case 0:
                for (int i = challengeTopics.getChild().size() - 1; i >= 0; i--) {*/
        if ("1".equals(challengeTopics.get(position).getPublicVisibility())) {
            if (challengeTopics.get(position).getExtraData() != null) {
                if ("1".equals(challengeTopics.get(position).getExtraData().get(0).getChallenge().getActive())) {
                    holder.challengeNameText.setText(challengeTopics.get(position).getDisplay_name());
                    if (challengeTopics.get(position).getExtraData().get(0).getChallenge().getIs_live().equals("1")) {
                        holder.liveTextViewVideoChallenge.setVisibility(View.VISIBLE);
                    } else {
                        holder.liveTextViewVideoChallenge.setVisibility(View.GONE);


                    }
                    challengeId.add(challengeTopics.get(position).getId());
                    if (challengeTopics.get(position).getExtraData().get(0).getChallenge().getMapped_category() != null && !challengeTopics.get(position).getExtraData().get(0).getChallenge().getMapped_category().trim().isEmpty()) {
                        mappedCategory.add(challengeTopics.get(position).getExtraData().get(0).getChallenge().getMapped_category());
                    } else {
                        mappedCategory.add("category-6dfcf8006c794d4e852343776302f588");


                    }
                    max_Duration.add(challengeTopics.get(position).getExtraData().get(0).getChallenge().getMax_duration());

                    Display_Name.add(challengeTopics.get(position).getDisplay_name());
                    if (!StringUtils.isNullOrEmpty(challengeTopics.get(position).getExtraData().get(0).getChallenge().getRules())) {
                        info.add(challengeTopics.get(position).getExtraData().get(0).getChallenge().getRules());
                    }
                    try {
                        Picasso.with(mContext).load(challengeTopics.get(position).getExtraData().get(0).getChallenge().getImageUrl()).placeholder(R.drawable.default_article).error(R.drawable.default_article)
                                .fit().into(holder.cureentChallengesImage);
                        activeImageUrl.add(challengeTopics.get(position).getExtraData().get(0).getChallenge().getImageUrl());
                        activeStreamUrl.add(challengeTopics.get(position).getExtraData().get(0).getChallenge().getVideoUrl());
                    } catch (Exception e) {
                        holder.cureentChallengesImage.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.default_article));
                    }
                }
            }
        }


    }


    @Override
    public int getItemCount() {

        return challengeTopics.size();

    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setData(ArrayList<Topics> challengeTopics) {
        this.challengeTopics = challengeTopics;
        for (int i = 0; i < challengeTopics.size(); i++) {
            if (AppConstants.PUBLIC_VISIBILITY.equals(challengeTopics.get(i).getPublicVisibility())) {
                count++;
            } else {
                challengeTopics.remove(i);

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
            recyclerViewClickListener.onClick(view, getAdapterPosition(), challengeId, Display_Name, challengeTopics.get(getAdapterPosition()), activeImageUrl, activeStreamUrl, info, mappedCategory,max_Duration.get(getAdapterPosition()));

        }
    }


    public interface RecyclerViewClickListener {
        void onClick(View view, int position, ArrayList<String> challengeId, ArrayList<String> Display_Name, Topics articledatamodelsnew, ArrayList<String> imageUrl, ArrayList<String> activeStreamUrl, ArrayList<String> info, ArrayList<String> mappedCategory,int max_Duration);

    }
}
