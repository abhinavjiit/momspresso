package com.mycity4kids.ui.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.mycity4kids.R;
import com.mycity4kids.models.Topics;
import java.util.ArrayList;


public class ShortStoryChallengesRecyclerAdapter extends
        RecyclerView.Adapter<ShortStoryChallengesRecyclerAdapter.ChallengeViewHolder> {

    private RecyclerViewClickListener recyclerViewClickListener;
    private Topics articleDataModelsNew;
    private ArrayList<Topics> articleDataModels = new ArrayList<>();

    public ShortStoryChallengesRecyclerAdapter(RecyclerViewClickListener recyclerViewClickListener) {
        this.recyclerViewClickListener = recyclerViewClickListener;
        setHasStableIds(true);
    }

    @Override
    public ChallengeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.challenge_short_story_recycle_adapter, parent, false);
        return new ChallengeViewHolder(view);
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
        try {
            articleDataModels.clear();
            articleDataModelsNew = mParentingLists;
            if (articleDataModelsNew != null) {
                for (int i = articleDataModelsNew.getChild().size() - 1; i >= 0; i--) {
                    if ("1".equals(articleDataModelsNew.getChild().get(i).getPublicVisibility())) {
                        articleDataModels.add(articleDataModelsNew.getChild().get(i));
                    }
                }
            }
        } catch (Exception e) {
            articleDataModels = new ArrayList<>();
            FirebaseCrashlytics.getInstance().recordException(e);
            Log.d("MC4KException", Log.getStackTraceString(e));
        }
    }

    @Override
    public void onBindViewHolder(ChallengeViewHolder holder, int position) {
        switch (position) {
            case 0:
                holder.previousAndThisWeekTextView.setText(R.string.this_week_challenge);
                holder.imageBody.setVisibility(View.VISIBLE);
                holder.rootView.setVisibility(View.VISIBLE);
                holder.useThePictureTextView.setVisibility(View.VISIBLE);
                holder.StorytextViewLayout.setVisibility(View.VISIBLE);
                holder.yourStoryTextView.setVisibility(View.VISIBLE);
                holder.previousAndThisWeekTextView.setVisibility(View.VISIBLE);
                try {
                    Glide.with(holder.imageBody.getContext())
                            .load(articleDataModels.get(position).getExtraData().get(0).getChallenge().getImageUrl())
                            .into(holder.imageBody);
                } catch (Exception e) {
                    holder.imageBody.setImageDrawable(
                            ContextCompat.getDrawable(holder.imageBody.getContext(), R.drawable.default_article));
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
                holder.storyTitleTextView.setVisibility(View.GONE);
                holder.titleTextUnderLine.setVisibility(View.GONE);
                try {
                    holder.imageBody.setVisibility(View.VISIBLE);
                    Glide.with(holder.imageBody.getContext())
                            .load(articleDataModels.get(position).getExtraData().get(0).getChallenge().getImageUrl())
                            .into(holder.imageBody);
                } catch (Exception e) {
                    holder.imageBody.setVisibility(View.GONE);
                    holder.imageBody.setImageDrawable(
                            ContextCompat.getDrawable(holder.imageBody.getContext(), R.drawable.default_article));
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

        ChallengeViewHolder(View itemView) {
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
            try {
                recyclerViewClickListener
                        .onClick(view, getAdapterPosition(), articleDataModels.get(getAdapterPosition()).getId(),
                                articleDataModels.get(getAdapterPosition()).getDisplay_name(), articleDataModelsNew,
                                articleDataModels.get(getAdapterPosition()).getExtraData().get(0).getChallenge()
                                        .getImageUrl());
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                Log.d("MC4KException", Log.getStackTraceString(e));
            }
        }
    }

    public interface RecyclerViewClickListener {

        void onClick(View view, int position, String challengeId, String Display_Name, Topics articledatamodelsnew,
                String activeImageUrl);
    }
}
