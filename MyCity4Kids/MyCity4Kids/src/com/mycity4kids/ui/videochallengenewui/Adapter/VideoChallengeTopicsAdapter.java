package com.mycity4kids.ui.videochallengenewui.Adapter;

import android.content.Context;
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

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

public class VideoChallengeTopicsAdapter extends RecyclerView.Adapter<VideoChallengeTopicsAdapter.ViewHolder> {

    private RecyclerViewClickListener recyclerViewClickListener;
    private Context mContext;
    private LayoutInflater mInflator;
    private ArrayList<Topics> challengeTopics = new ArrayList<>();

    public VideoChallengeTopicsAdapter(Context mContext, RecyclerViewClickListener recyclerViewClickListener) {
        this.recyclerViewClickListener = recyclerViewClickListener;
        this.mContext = mContext;
        mInflator = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
        holder.challengeNameText.setText(challengeTopics.get(position).getDisplay_name());
        if (challengeTopics.get(position).getExtraData().get(0).getChallenge().getIs_live().equals("1")) {
            holder.liveTextViewVideoChallenge.setVisibility(View.VISIBLE);
        } else {
            holder.liveTextViewVideoChallenge.setVisibility(View.GONE);
        }
        try {
            Picasso.get().load(challengeTopics.get(position).getExtraData().get(0).getChallenge().getImageUrl()).placeholder(R.drawable.default_article).error(R.drawable.default_article)
                    .fit().into(holder.cureentChallengesImage);
        } catch (Exception e) {
            holder.cureentChallengesImage.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.default_article));
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
        for (int i = 0; i < challengeTopics.size(); i++) {
            if (AppConstants.PUBLIC_VISIBILITY.equals(challengeTopics.get(i).getPublicVisibility())) {
                if (challengeTopics.get(i).getExtraData() != null && challengeTopics.get(i).getExtraData().size() != 0 && "1".equals(challengeTopics.get(i).getExtraData().get(0).getChallenge().getActive())) {
                    this.challengeTopics.add(challengeTopics.get(i));
                }
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
            String mappedCategory;
            if (!StringUtils.isNullOrEmpty(challengeTopics.get(getAdapterPosition()).getExtraData().get(0).getChallenge().getMapped_category()))
                mappedCategory = challengeTopics.get(getAdapterPosition()).getExtraData().get(0).getChallenge().getMapped_category();
            else
                mappedCategory = "category-6dfcf8006c794d4e852343776302f588";
            recyclerViewClickListener.onClick(view, getAdapterPosition(), challengeTopics.get(getAdapterPosition()).getId(), challengeTopics.get(getAdapterPosition()).getDisplay_name(), challengeTopics.get(getAdapterPosition()), challengeTopics.get(getAdapterPosition()).getExtraData().get(0).getChallenge().getImageUrl(), challengeTopics.get(getAdapterPosition()).getExtraData().get(0).getChallenge().getVideoUrl(), challengeTopics.get(getAdapterPosition()).getExtraData().get(0).getChallenge().getRules(), mappedCategory, challengeTopics.get(getAdapterPosition()).getExtraData().get(0).getChallenge().getMax_duration());
        }
    }

    public interface RecyclerViewClickListener {
        void onClick(View view, int position, String challengeId, String Display_Name, Topics articledatamodelsnew, String imageUrl, String activeStreamUrl, String info, String mappedCategory, int max_Duration);
    }
}
