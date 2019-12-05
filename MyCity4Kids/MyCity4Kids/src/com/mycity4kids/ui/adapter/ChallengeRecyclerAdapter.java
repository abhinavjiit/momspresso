package com.mycity4kids.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mycity4kids.R;
import com.mycity4kids.models.Topics;

import java.util.ArrayList;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;


public class ChallengeRecyclerAdapter extends RecyclerView.Adapter<ChallengeRecyclerAdapter.ChallengeViewHolder> {
    int count = 0;
    private RecyclerViewClickListener recyclerViewClickListener;
    private Context mcontext;
    final float density;
    private LayoutInflater mInflator;
    private Topics articleDataModelsNew;
    private ArrayList<Topics> articleDataModels = new ArrayList<>();
    private ArrayList<String> challengeId;
    private ArrayList<String> Display_Name;
    private ArrayList<String> activeImageUrl;

    public ChallengeRecyclerAdapter(RecyclerViewClickListener recyclerViewClickListener, Context mcontext, ArrayList<String> challengeId, ArrayList<String> Display_Name, ArrayList<String> activeImageUrl) {
        this.challengeId = challengeId;
        this.Display_Name = Display_Name;
        this.activeImageUrl = activeImageUrl;
        density = mcontext.getResources().getDisplayMetrics().density;
        mInflator = (LayoutInflater) mcontext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.recyclerViewClickListener = recyclerViewClickListener;
        this.mcontext = mcontext;
        setHasStableIds(true);
    }

    @Override
    public ChallengeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflator.inflate(R.layout.challenge_short_story_recycle_adapter, parent, false);
        ChallengeViewHolder viewHolder = new ChallengeViewHolder(view, recyclerViewClickListener);
        return viewHolder;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setListData(Topics mParentingLists) {
        articleDataModelsNew = mParentingLists;
        if (articleDataModelsNew != null) {
            for (int i = articleDataModelsNew.getChild().size() - 1; i >= 0; i--) {
                if ("1".equals(articleDataModelsNew.getChild().get(i).getPublicVisibility())) {
                    if (articleDataModelsNew.getChild().get(i).getExtraData() != null && articleDataModelsNew.getChild().get(i).getExtraData().size() != 0) {
                        if ("1".equals(articleDataModelsNew.getChild().get(i).getExtraData().get(0).getChallenge().getActive())) {
                            articleDataModels.add(articleDataModelsNew.getChild().get(i));
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onBindViewHolder(ChallengeViewHolder holder, int position) {
        switch (position) {
            case 0:
                holder.previousAndThisWeekTextView.setText(R.string.this_week_challenge);
                challengeId.add(articleDataModels.get(position).getId());
                Display_Name.add(articleDataModels.get(position).getDisplay_name());
                holder.imageBody.setVisibility(View.VISIBLE);
                holder.rootView.setVisibility(View.VISIBLE);
                holder.useThePictureTextView.setVisibility(View.VISIBLE);
                holder.StorytextViewLayout.setVisibility(View.VISIBLE);
                holder.yourStoryTextView.setVisibility(View.VISIBLE);
                holder.previousAndThisWeekTextView.setVisibility(View.VISIBLE);
                try {
                    Glide.with(mcontext).load(articleDataModels.get(position).getExtraData().get(0).getChallenge().getImageUrl()).into(holder.imageBody);
                    activeImageUrl.add(articleDataModels.get(position).getExtraData().get(0).getChallenge().getImageUrl());
                } catch (Exception e) {
                    holder.imageBody.setImageDrawable(ContextCompat.getDrawable(mcontext, R.drawable.default_article));
                }
                break;
            default:
                holder.rootView.setVisibility(View.VISIBLE);
                if (position == 1) {
                    holder.previousAndThisWeekTextView.setVisibility(View.VISIBLE);
                    holder.previousAndThisWeekTextView.setText(R.string.previous_week_challenge);
                } else {
                    holder.previousAndThisWeekTextView.setVisibility(View.GONE);
                }
                challengeId.add(articleDataModels.get(position).getId());
                Display_Name.add(articleDataModels.get(position).getDisplay_name());
                holder.storyTitleTextView.setVisibility(View.GONE);
                holder.titleTextUnderLine.setVisibility(View.GONE);
                try {
                    holder.imageBody.setVisibility(View.VISIBLE);
                    Glide.with(mcontext).load(articleDataModels.get(position).getExtraData().get(0).getChallenge().getImageUrl()).into(holder.imageBody);
                    activeImageUrl.add(articleDataModels.get(position).getExtraData().get(0).getChallenge().getImageUrl());
                } catch (Exception e) {
                    holder.imageBody.setVisibility(View.GONE);
                    holder.imageBody.setImageDrawable(ContextCompat.getDrawable(mcontext, R.drawable.default_article));
                }
        }
    }

    @Override
    public int getItemCount() {
        return articleDataModels.size();
    }

    public class ChallengeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private RelativeLayout mainView;
        private RelativeLayout rootView;
        RelativeLayout titleContainer;
        private TextView storyTitleTextView;
        TextView storyBodyTextView;
        private TextView getStartedTextView;
        private ImageView imageBody;
        TextView storytitle;
        private View titleTextUnderLine;
        private TextView previousAndThisWeekTextView;
        private TextView yourStoryTextView;
        private LinearLayout StorytextViewLayout;
        private TextView useThePictureTextView;


        public ChallengeViewHolder(View itemView, RecyclerViewClickListener recyclerViewClickListener) {
            super(itemView);
            rootView = (RelativeLayout) itemView.findViewById(R.id.rootView);
            mainView = (RelativeLayout) itemView.findViewById(R.id.mainView);
            titleContainer = (RelativeLayout) itemView.findViewById(R.id.titleContainer);
            storyBodyTextView = (TextView) itemView.findViewById(R.id.storyBodyTextView);
            storyTitleTextView = (TextView) itemView.findViewById(R.id.storyTitleTextView);
            imageBody = (ImageView) itemView.findViewById(R.id.imageBody);
            getStartedTextView = (TextView) itemView.findViewById(R.id.getStartedTextView);
            storytitle = (TextView) itemView.findViewById(R.id.storytitle);
            titleTextUnderLine = (View) itemView.findViewById(R.id.TittleText_Line);
            previousAndThisWeekTextView = (TextView) itemView.findViewById(R.id.this_week_previous_week_textView);
            yourStoryTextView = (TextView) itemView.findViewById(R.id.your_100_word_story_text);
            StorytextViewLayout = (LinearLayout) itemView.findViewById(R.id.whats_your_story_text_linear_layout);
            useThePictureTextView = (TextView) itemView.findViewById(R.id.use_the_picture_textView);

            getStartedTextView.setOnClickListener(this);
            mainView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            recyclerViewClickListener.onClick(view, getAdapterPosition(), challengeId, Display_Name, articleDataModelsNew, activeImageUrl);

        }
    }

    public interface RecyclerViewClickListener {
        void onClick(View view, int position, ArrayList<String> challengeId, ArrayList<String> Display_Name, Topics articledatamodelsnew, ArrayList<String> activeImageUrl);
    }
}
