package com.mycity4kids.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.models.Topics;

import java.util.ArrayList;

public class ArticleChallengesRecyclerAdapter extends RecyclerView.Adapter<ArticleChallengesRecyclerAdapter.ArticleChallengesViewHolder> {

    private Context context;
    private ArrayList<Topics> articleChallengesList;

    public ArticleChallengesRecyclerAdapter(Context context, ArrayList<Topics> articleChallengesList) {
        this.context = context;
        this.articleChallengesList = articleChallengesList;
    }

    @NonNull
    @Override
    public ArticleChallengesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = ((LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.challenge_recyler_adapter, parent, false);
        return new ArticleChallengesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArticleChallengesViewHolder holder, int position) {
        holder.noChallengeAddedText.setVisibility(View.GONE);
        holder.useThePictureTextView.setVisibility(View.GONE);
        holder.useThePictureTextView.setText(R.string.use_picture_word_to_upload_one);
        holder.StorytextViewLayout.setVisibility(View.VISIBLE);
        holder.yourStoryTextView.setVisibility(View.GONE);
        holder.yourStoryTextView.setText(R.string.choose_challenge_label);

        // for (int i = 0; i < articleChallengesList.size(); i++) {
        if ("1".equals(articleChallengesList.get(position).getPublicVisibility())) {
            if (articleChallengesList.get(position).getExtraData() != null && articleChallengesList.get(position).getExtraData().size() != 0) {
                if ("1".equals(articleChallengesList.get(position).getExtraData().get(0).getChallenge().getActive())) {
                    holder.rootView.setVisibility(View.VISIBLE);
                    if (articleChallengesList.get(position).getExtraData().get(0).getChallenge().getIs_live() != null) {
                        if (articleChallengesList.get(position).getExtraData().get(0).getChallenge().getIs_live().equals("1")) {
                            articleChallengesList.get(position).setPrevKey(false);
                            holder.previousAndThisWeekTextView.setVisibility(View.VISIBLE);
                            if (position == 0) {
                                holder.previousAndThisWeekTextView.setText(R.string.this_week_challenge);
                                holder.liveTextViewVideoChallenge.setVisibility(View.VISIBLE);
                            } else {
                                holder.previousAndThisWeekTextView.setVisibility(View.GONE);
                                holder.liveTextViewVideoChallenge.setVisibility(View.VISIBLE);

                            }

                        } else {
                            if (position != 0) {

                                if (articleChallengesList.get(position - 1).getExtraData().get(0).getChallenge().getIs_live().equals("1")) {
                                    holder.previousAndThisWeekTextView.setVisibility(View.VISIBLE);
                                    holder.previousAndThisWeekTextView.setText(R.string.previous_week_challenge);

                                    holder.liveTextViewVideoChallenge.setVisibility(View.GONE);
                                } else {
                                    holder.previousAndThisWeekTextView.setVisibility(View.GONE);
                                    holder.liveTextViewVideoChallenge.setVisibility(View.GONE);
                                }
                            } else if (position == 0) {
                                holder.previousAndThisWeekTextView.setVisibility(View.VISIBLE);
                                holder.liveTextViewVideoChallenge.setVisibility(View.GONE);

                                holder.previousAndThisWeekTextView.setText(R.string.previous_week_challenge);

                            } else {
                                holder.previousAndThisWeekTextView.setVisibility(View.GONE);
                                holder.liveTextViewVideoChallenge.setVisibility(View.GONE);

                            }


                        }
                    } else {
                        holder.liveTextViewVideoChallenge.setVisibility(View.GONE);
                        holder.previousAndThisWeekTextView.setVisibility(View.GONE);


                    }
//                    challengeId.add(articleChallengesList.get(position).getId());
//                    if (articleChallengesList.get(position).getExtraData().get(0).getChallenge().getMapped_category() != null && !articleChallengesList.get(position).getExtraData().get(0).getChallenge().getMapped_category().trim().isEmpty()) {
//                        mappedCategory.add(articleChallengesList.get(position).getExtraData().get(0).getChallenge().getMapped_category());
//                    } else {
//                        mappedCategory.add("category-6dfcf8006c794d4e852343776302f588");
//
//
//                    }
//
//                    max_Duration.add(articleChallengesList.get(position).getExtraData().get(0).getChallenge().getMax_duration());
//                    // holder.storyTitleTextView.setText("Take This Week's 100 Word Story Challenge");
//                    Display_Name.add(articleChallengesList.get(position).getDisplay_name());
//                    holder.storyTitleTextView.setVisibility(View.GONE);
//                    holder.titleTextUnderLine.setVisibility(View.GONE);
//                    if (!StringUtils.isNullOrEmpty(articleChallengesList.get(position).getDisplay_name())) {
//                        holder.challengeNameTextMomVlog.setText(articleChallengesList.get(position).getDisplay_name());
//                    }
//                    holder.imageBody.setVisibility(View.VISIBLE);
//                    if (!StringUtils.isNullOrEmpty(articleChallengesList.get(position).getExtraData().get(0).getChallenge().getRules())) {
//                        rules.add(articleChallengesList.get(position).getExtraData().get(0).getChallenge().getRules());
//                    }
//                    try {
//                        Glide.with(context).load(articleChallengesList.get(position).getExtraData().get(0).getChallenge().getImageUrl()).into(holder.imageBody);
//                                       /* Picasso.with(mcontext).load(articleChallengesList.getChild().get(i).getExtraData().get(0).getChallenge().getImageUrl()).placeholder(R.drawable.default_article).error(R.drawable.default_article)
//                                                .fit().into(holder.imageBody);*/
//                        activeImageUrl.add(articleChallengesList.get(position).getExtraData().get(0).getChallenge().getImageUrl());
//                        activeStreamUrl.add(articleChallengesList.get(position).getExtraData().get(0).getChallenge().getVideoUrl());
//                    } catch (Exception e) {
//                        holder.imageBody.setImageDrawable(ContextCompat.getDrawable(mcontext, R.drawable.default_article));
//                    }

                }


            }
        }
    }

    @Override
    public int getItemCount() {
        return articleChallengesList.size();
    }

    public class ArticleChallengesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private RelativeLayout mainView;
        private RelativeLayout rootView;
        private RelativeLayout titleContainer;
        private TextView storyTitleTextView;
        private TextView storyBodyTextView;
        private TextView getStartedTextView;
        private ImageView imageBody;
        private TextView storytitle;
        private View titleTextUnderLine;
        private TextView previousAndThisWeekTextView;
        private TextView yourStoryTextView;
        private LinearLayout StorytextViewLayout;
        private TextView useThePictureTextView, noChallengeAddedText, liveTextViewVideoChallenge, challengeNameTextMomVlog;

        ArticleChallengesViewHolder(@NonNull View itemView) {
            super(itemView);
            rootView = (RelativeLayout) itemView.findViewById(R.id.rootView);
            noChallengeAddedText = (TextView) itemView.findViewById(R.id.noChallengeAddedText);
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
            liveTextViewVideoChallenge = (TextView) itemView.findViewById(R.id.liveTextViewVideoChallenge);
            challengeNameTextMomVlog = (TextView) itemView.findViewById(R.id.challengeNameTextMomVlog);
            getStartedTextView.setOnClickListener(this);
            mainView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

        }
    }
}
