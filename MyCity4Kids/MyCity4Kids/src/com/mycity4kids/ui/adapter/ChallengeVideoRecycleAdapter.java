package com.mycity4kids.ui.adapter;

import android.content.Context;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
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

import java.util.ArrayList;

public class ChallengeVideoRecycleAdapter extends RecyclerView.Adapter<ChallengeVideoRecycleAdapter.ChallengeViewHolder> {
    int count = 0;
    private RecyclerViewClickListener recyclerViewClickListener;
    private Context mcontext;
    private int n = 1;
    private int m = 0;
    private LayoutInflater mInflator;
    private ArrayList<Topics> articleDataModelsNew = new ArrayList<>();
    boolean current = true;
    private ArrayList<String> challengeId = new ArrayList<>();
    private ArrayList<String> Display_Name = new ArrayList<>();
    private ArrayList<String> activeImageUrl = new ArrayList<>();
    private ArrayList<String> activeStreamUrl = new ArrayList<>();
    private ArrayList<String> rules = new ArrayList<>();
    private ArrayList<String> mappedCategory = new ArrayList<>();
    private ArrayList<Integer> max_Duration = new ArrayList<>();
    private boolean previousKey = false;


    public ChallengeVideoRecycleAdapter(RecyclerViewClickListener recyclerViewClickListener, Context mcontext, ArrayList<String> challengeId, ArrayList<String> Display_Name, ArrayList<String> activeImageUrl, ArrayList<String> activeStreamUrl, ArrayList<String> rules) {
        this.challengeId = challengeId;
        this.Display_Name = Display_Name;
        this.activeImageUrl = activeImageUrl;
        this.activeStreamUrl = activeStreamUrl;
        this.rules = rules;
        mInflator = (LayoutInflater) mcontext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.recyclerViewClickListener = recyclerViewClickListener;
        this.mcontext = mcontext;

    }


    @Override
    public ChallengeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ChallengeViewHolder viewHolder = null;
        View view = mInflator.inflate(R.layout.challenge_recyler_adapter, parent, false);
        viewHolder = new ChallengeViewHolder(view, recyclerViewClickListener);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(ChallengeViewHolder holder, int position) {
      /*  if (count == 0) {
            holder.rootView.setVisibility(View.GONE);
            holder.noChallengeAddedText.setVisibility(View.GONE);
            // holder.previousAndThisWeekTextView.setVisibility(View.GONE);
        } else {*/
        // switch (position) {
        //   case 0:
        //  holder.previousAndThisWeekTextView.setVisibility(View.VISIBLE
        //  );
        holder.noChallengeAddedText.setVisibility(View.GONE);
        holder.useThePictureTextView.setVisibility(View.GONE);
        holder.useThePictureTextView.setText(R.string.use_picture_word_to_upload_one);
        holder.StorytextViewLayout.setVisibility(View.VISIBLE);
        holder.yourStoryTextView.setVisibility(View.GONE);
        holder.yourStoryTextView.setText(R.string.choose_challenge_label);

        // for (int i = 0; i < articleDataModelsNew.size(); i++) {
        if ("1".equals(articleDataModelsNew.get(position).getPublicVisibility())) {
            if (articleDataModelsNew.get(position).getExtraData() != null && articleDataModelsNew.get(position).getExtraData().size() != 0) {
                if ("1".equals(articleDataModelsNew.get(position).getExtraData().get(0).getChallenge().getActive())) {
                    holder.rootView.setVisibility(View.VISIBLE);
                    if (articleDataModelsNew.get(position).getExtraData().get(0).getChallenge().getIs_live() != null) {
                        if (articleDataModelsNew.get(position).getExtraData().get(0).getChallenge().getIs_live().equals("1")) {
                            articleDataModelsNew.get(position).setPrevKey(false);
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

                                if (articleDataModelsNew.get(position - 1).getExtraData().get(0).getChallenge().getIs_live().equals("1")) {
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
                    challengeId.add(articleDataModelsNew.get(position).getId());
                    if (articleDataModelsNew.get(position).getExtraData().get(0).getChallenge().getMapped_category() != null && !articleDataModelsNew.get(position).getExtraData().get(0).getChallenge().getMapped_category().trim().isEmpty()) {
                        mappedCategory.add(articleDataModelsNew.get(position).getExtraData().get(0).getChallenge().getMapped_category());
                    } else {
                        mappedCategory.add("category-6dfcf8006c794d4e852343776302f588");


                    }

                    max_Duration.add(articleDataModelsNew.get(position).getExtraData().get(0).getChallenge().getMax_duration());
                    // holder.storyTitleTextView.setText("Take This Week's 100 Word Story Challenge");
                    Display_Name.add(articleDataModelsNew.get(position).getDisplay_name());
                    holder.storyTitleTextView.setVisibility(View.GONE);
                    holder.titleTextUnderLine.setVisibility(View.GONE);
                    if (!StringUtils.isNullOrEmpty(articleDataModelsNew.get(position).getDisplay_name())) {
                        holder.challengeNameTextMomVlog.setText(articleDataModelsNew.get(position).getDisplay_name());
                    }
                    //if (3 == (articleDataModelsNew.getChild().get(i).getExtraData().get(0).getChallenge().getType())) {
                    holder.imageBody.setVisibility(View.VISIBLE);
//                                    if (!StringUtils.isNullOrEmpty(articleDataModelsNew.getChild().get(i).getExtraData().get(0).getChallenge().getRules())) {
//                                        rules.add(articleDataModelsNew.getChild().get(i).getExtraData().get(0).getChallenge().getRules());
//                                    }
                    if (!StringUtils.isNullOrEmpty(articleDataModelsNew.get(position).getExtraData().get(0).getChallenge().getRules())) {
                        rules.add(articleDataModelsNew.get(position).getExtraData().get(0).getChallenge().getRules());
                    }
                    try {
                        Glide.with(mcontext).load(articleDataModelsNew.get(position).getExtraData().get(0).getChallenge().getImageUrl()).into(holder.imageBody);
                                       /* Picasso.with(mcontext).load(articleDataModelsNew.getChild().get(i).getExtraData().get(0).getChallenge().getImageUrl()).placeholder(R.drawable.default_article).error(R.drawable.default_article)
                                                .fit().into(holder.imageBody);*/
                        activeImageUrl.add(articleDataModelsNew.get(position).getExtraData().get(0).getChallenge().getImageUrl());
                        activeStreamUrl.add(articleDataModelsNew.get(position).getExtraData().get(0).getChallenge().getVideoUrl());
                    } catch (Exception e) {
                        holder.imageBody.setImageDrawable(ContextCompat.getDrawable(mcontext, R.drawable.default_article));
                    }

                }


            }
        }

    }

    @Override
    public int getItemCount() {
        return articleDataModelsNew.size();
    }

    public void setListData(ArrayList<Topics> mParentingLists) {
        articleDataModelsNew = mParentingLists;
        for (int i = 0; i < articleDataModelsNew.size(); i++) {
            if (AppConstants.PUBLIC_VISIBILITY.equals(articleDataModelsNew.get(i).getPublicVisibility())) {
                // if ("1".equals(articleDataModelsNew.getChild().get(i).getExtraData().get(0).getChallenge().getActive())) {
                if (articleDataModelsNew.get(i).getExtraData() != null && articleDataModelsNew.get(i).getExtraData().size() != 0)
                {
                    count++;
                }

                //}
            } else {
                articleDataModelsNew.remove(i);
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
        private TextView useThePictureTextView, noChallengeAddedText, liveTextViewVideoChallenge, challengeNameTextMomVlog;


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
            challengeNameTextMomVlog = (TextView) itemView.findViewById(R.id.challengeNameTextMomVlog);
            getStartedTextView.setOnClickListener(this);
            mainView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            recyclerViewClickListener.onClick(view, getAdapterPosition(), challengeId, Display_Name, articleDataModelsNew.get(getAdapterPosition()), activeImageUrl, activeStreamUrl, rules, mappedCategory, max_Duration.get(getAdapterPosition()));

        }
    }

    public interface RecyclerViewClickListener {
        void onClick(View view, int position, ArrayList<String> challengeId, ArrayList<String> Display_Name, Topics articledatamodelsnew, ArrayList<String> imageUrl, ArrayList<String> activeStreamUrl, ArrayList<String> rules, ArrayList<String> mappedCategory, int max_Duration);
    }

}
