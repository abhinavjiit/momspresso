package com.mycity4kids.ui.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.models.Topics;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ChallengeVideoRecycleAdapter extends RecyclerView.Adapter<ChallengeVideoRecycleAdapter.ChallengeViewHolder> {
    int count = 0;
    private RecyclerViewClickListener recyclerViewClickListener;
    private Context mcontext;
    private int n = 1;
    private int m;
    private LayoutInflater mInflator;
    private Topics articleDataModelsNew;
    boolean current = true;
    private ArrayList<String> challengeId;
    private ArrayList<String> Display_Name;
    private ArrayList<String> activeImageUrl;
    private ArrayList<String> activeStreamUrl;
    private ArrayList<String> rules;


    public ChallengeVideoRecycleAdapter(RecyclerViewClickListener recyclerViewClickListener, Context mcontext, ArrayList<String> challengeId, ArrayList<String> Display_Name, ArrayList<String> activeImageUrl, ArrayList<String> activeStreamUrl) {
        this.challengeId = challengeId;
        this.Display_Name = Display_Name;
        this.activeImageUrl = activeImageUrl;
        this.activeStreamUrl = activeStreamUrl;
        mInflator = (LayoutInflater) mcontext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.recyclerViewClickListener = recyclerViewClickListener;
        this.mcontext = mcontext;
        setHasStableIds(true);
    }


    @Override
    public ChallengeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ChallengeViewHolder viewHolder = null;
        View view = mInflator.inflate(R.layout.challenge_recyler_adapter, parent, false);
        viewHolder = new ChallengeViewHolder(view, recyclerViewClickListener);
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

    @Override
    public void onBindViewHolder(ChallengeViewHolder holder, int position) {
        if (count == 0) {
            holder.rootView.setVisibility(View.GONE);
            holder.noChallengeAddedText.setVisibility(View.GONE);
            // holder.previousAndThisWeekTextView.setVisibility(View.GONE);
        } else {
            switch (position) {
                case 0:
                    //  holder.previousAndThisWeekTextView.setVisibility(View.VISIBLE
                    //  );
                    holder.noChallengeAddedText.setVisibility(View.GONE);
                    holder.useThePictureTextView.setVisibility(View.VISIBLE);
                    holder.useThePictureTextView.setText(R.string.use_picture_word_to_upload_one);
                    holder.StorytextViewLayout.setVisibility(View.VISIBLE);
                    holder.yourStoryTextView.setVisibility(View.VISIBLE);
                    holder.yourStoryTextView.setText(R.string.choose_challenge_label);
                    holder.previousAndThisWeekTextView.setVisibility(View.VISIBLE);
                    holder.previousAndThisWeekTextView.setText(R.string.this_week_challenge);
                    for (int i = articleDataModelsNew.getChild().size() - 1; i >= 0; i--) {
                        if ("1".equals(articleDataModelsNew.getChild().get(i).getPublicVisibility())) {
                            if (articleDataModelsNew.getChild().get(i).getExtraData() != null) {
                                if ("1".equals(articleDataModelsNew.getChild().get(i).getExtraData().get(0).getChallenge().getActive())) {
                                    holder.rootView.setVisibility(View.VISIBLE);
                                    holder.liveTextViewVideoChallenge.setVisibility(View.VISIBLE);
                                    challengeId.add(articleDataModelsNew.getChild().get(i).getId());
                                    // holder.storyTitleTextView.setText("Take This Week's 100 Word Story Challenge");
                                    Display_Name.add(articleDataModelsNew.getChild().get(i).getDisplay_name());
                                    holder.storyTitleTextView.setVisibility(View.GONE);
                                    holder.titleTextUnderLine.setVisibility(View.GONE);
                                    //if (3 == (articleDataModelsNew.getChild().get(i).getExtraData().get(0).getChallenge().getType())) {
                                    holder.imageBody.setVisibility(View.VISIBLE);
                                    if (!StringUtils.isNullOrEmpty(articleDataModelsNew.getChild().get(i).getExtraData().get(0).getChallenge().getRules())) {
                                        rules.add(articleDataModelsNew.getChild().get(i).getExtraData().get(0).getChallenge().getRules());
                                    }
                                    try {
                                        Glide.with(mcontext).load(articleDataModelsNew.getChild().get(i).getExtraData().get(0).getChallenge().getImageUrl()).into(holder.imageBody);
                                       /* Picasso.with(mcontext).load(articleDataModelsNew.getChild().get(i).getExtraData().get(0).getChallenge().getImageUrl()).placeholder(R.drawable.default_article).error(R.drawable.default_article)
                                                .fit().into(holder.imageBody);*/
                                        activeImageUrl.add(articleDataModelsNew.getChild().get(i).getExtraData().get(0).getChallenge().getImageUrl());
                                        activeStreamUrl.add(articleDataModelsNew.getChild().get(i).getExtraData().get(0).getChallenge().getVideoUrl());
                                    } catch (Exception e) {
                                        holder.imageBody.setImageDrawable(ContextCompat.getDrawable(mcontext, R.drawable.default_article));
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
                        if ("1".equals(articleDataModelsNew.getChild().get(j).getPublicVisibility())) {
                            if (articleDataModelsNew.getChild().get(j).getExtraData() != null) {
                                holder.rootView.setVisibility(View.VISIBLE);
                                holder.liveTextViewVideoChallenge.setVisibility(View.GONE);

                                //if ("1".equals(articleDataModelsNew.getChild().get(j).getExtraData().get(0).getChallenge().getActive())) {
                                if (position != 1) {
                                    holder.previousAndThisWeekTextView.setVisibility(View.GONE);
                                } else {
                                    holder.previousAndThisWeekTextView.setVisibility(View.VISIBLE);
                                    holder.previousAndThisWeekTextView.setText(R.string.previous_week_challenge);
                                }
                                challengeId.add(articleDataModelsNew.getChild().get(j).getId());
                                Display_Name.add(articleDataModelsNew.getChild().get(j).getDisplay_name());
                                // holder.storyTitleTextView.setText(R.string.previous_week_challenge);
                                holder.storyTitleTextView.setVisibility(View.GONE);
                                holder.titleTextUnderLine.setVisibility(View.GONE);
                                holder.storytitle.setText(articleDataModelsNew.getChild().get(j).getDisplay_name());
                                //  if (3 == (articleDataModelsNew.getChild().get(j).getExtraData().get(0).getChallenge().getType())) {
                                holder.imageBody.setVisibility(View.VISIBLE);
                                try {
                                    Glide.with(mcontext).load(articleDataModelsNew.getChild().get(j).getExtraData().get(0).getChallenge().getImageUrl()).into(holder.imageBody);
                               /*     Picasso.with(mcontext).load(articleDataModelsNew.getChild().get(j).getExtraData().get(0).getChallenge().getImageUrl()).placeholder(R.drawable.default_article).error(R.drawable.default_article)
                                            .fit().into(holder.imageBody);*/
                                    if (!StringUtils.isNullOrEmpty(articleDataModelsNew.getChild().get(j).getExtraData().get(0).getChallenge().getRules())) {
                                        rules.add(articleDataModelsNew.getChild().get(j).getExtraData().get(0).getChallenge().getRules());
                                    }
                                    activeImageUrl.add(articleDataModelsNew.getChild().get(j).getExtraData().get(0).getChallenge().getImageUrl());
                                    activeStreamUrl.add(articleDataModelsNew.getChild().get(j).getExtraData().get(0).getChallenge().getVideoUrl());
                                } catch (Exception e) {
                                    holder.imageBody.setImageDrawable(ContextCompat.getDrawable(mcontext, R.drawable.default_article));
                                }
                                n++;
                                break;
                                //  }
                            } else if (articleDataModelsNew.getChild().get(j).getExtraData() == null) {
                                n++;
                            } else if (articleDataModelsNew.getChild().get(j).getPublicVisibility().equals("0")) {
                                n++;
                                break;
                            }
                        }
                    }
                    break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return count;
    }

    public void setListData(Topics mParentingLists) {
        articleDataModelsNew = mParentingLists;
        for (int i = 0; i < articleDataModelsNew.getChild().size(); i++) {
            if (AppConstants.PUBLIC_VISIBILITY.equals(articleDataModelsNew.getChild().get(i).getPublicVisibility())) {
                // if ("1".equals(articleDataModelsNew.getChild().get(i).getExtraData().get(0).getChallenge().getActive())) {
                count++;

                //}
            }
        }
    }

    public class ChallengeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
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
        private TextView useThePictureTextView, noChallengeAddedText, liveTextViewVideoChallenge;


        public ChallengeViewHolder(View itemView, ChallengeVideoRecycleAdapter.RecyclerViewClickListener recyclerViewClickListener) {
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
            getStartedTextView.setOnClickListener(this);
            mainView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            recyclerViewClickListener.onClick(view, getAdapterPosition(), challengeId, Display_Name, articleDataModelsNew, activeImageUrl, activeStreamUrl, rules);

        }
    }

    public interface RecyclerViewClickListener {
        void onClick(View view, int position, ArrayList<String> challengeId, ArrayList<String> Display_Name, Topics articledatamodelsnew, ArrayList<String> imageUrl, ArrayList<String> activeStreamUrl, ArrayList<String> rules);
    }

}
