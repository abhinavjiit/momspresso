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

import com.mycity4kids.R;
import com.mycity4kids.models.Topics;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;


public class ChallengeRecyclerAdapter extends RecyclerView.Adapter<ChallengeRecyclerAdapter.ChallengeViewHolder> {
    int count = 0;
    private RecyclerViewClickListener recyclerViewClickListener;
    private int n = 2;
    private Context mcontext;
    private final float density;
    private LayoutInflater mInflator;
    private Topics articleDataModelsNew;
    boolean current = true;
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
    }

    @Override
    public ChallengeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ChallengeViewHolder viewHolder = null;
        View view = mInflator.inflate(R.layout.challenge_recyler_adapter, parent, false);
        viewHolder = new ChallengeViewHolder(view, recyclerViewClickListener);
        return viewHolder;
    }


    public void setListData(Topics mParentingLists) {
        articleDataModelsNew = mParentingLists;
        for (int i = 0; i < articleDataModelsNew.getChild().size(); i++) {
            if ("1".equals(articleDataModelsNew.getChild().get(i).getPublicVisibility())) {
                if ("1".equals(articleDataModelsNew.getChild().get(i).getExtraData().get(0).getChallenge().getActive())) {
                    count++;

                }
            }
        }
    }

    @Override
    public void onBindViewHolder(ChallengeViewHolder holder, int position) {

        //  for (int i = articleDataModelsNew.getChild().size()-1; i >= 0; i--) {

        switch (position) {
            case 0:
                holder.useThePictureTextView.setVisibility(View.VISIBLE);
                holder.StorytextViewLayout.setVisibility(View.VISIBLE);
                holder.yourStoryTextView.setVisibility(View.VISIBLE);
                holder.previousAndThisWeekTextView.setVisibility(View.VISIBLE);
                holder.previousAndThisWeekTextView.setText(" This Week's Challenge");
                int i = articleDataModelsNew.getChild().size() - 1;
                if ("1".equals(articleDataModelsNew.getChild().get(i).getPublicVisibility())) {
                    if ("1".equals(articleDataModelsNew.getChild().get(i).getExtraData().get(0).getChallenge().getActive())) {
                        challengeId.add(articleDataModelsNew.getChild().get(i).getId());
                        holder.storyTitleTextView.setText("Take This Week's 100 Word Story Challenge");
                        Display_Name.add(articleDataModelsNew.getChild().get(i).getDisplay_name());
                        holder.storyTitleTextView.setVisibility(View.GONE);
                        holder.titleTextUnderLine.setVisibility(View.GONE);
                        if (2 == (articleDataModelsNew.getChild().get(i).getExtraData().get(0).getChallenge().getType())) {
                            holder.imageBody.setVisibility(View.VISIBLE);

                            try {
                                Picasso.with(mcontext).load(articleDataModelsNew.getChild().get(i).getExtraData().get(0).getChallenge().getImageUrl()).placeholder(R.drawable.default_article).error(R.drawable.default_article)
                                        .fit().into(holder.imageBody);
                                activeImageUrl.add(articleDataModelsNew.getChild().get(i).getExtraData().get(0).getChallenge().getImageUrl());
                            } catch (Exception e) {
                                holder.imageBody.setImageDrawable(ContextCompat.getDrawable(mcontext, R.drawable.default_article));
                            }
                        }
                    }
                }
                break;
        /*    case 1:
                holder.previousAndThisWeekTextView.setVisibility(View.GONE);
                for (int j = articleDataModelsNew.getChild().size() - n; j >= 0; j--) {
                    if ("1".equals(articleDataModelsNew.getChild().get(j).getPublicVisibility())) {
                        if ("1".equals(articleDataModelsNew.getChild().get(j).getExtraData().get(0).getChallenge().getActive())) {
                            challengeId.add(articleDataModelsNew.getChild().get(j).getId());
                            Display_Name.add(articleDataModelsNew.getChild().get(j).getDisplay_name());
                            holder.storyTitleTextView.setText("Previous Week's Challenges");
                            //   if (position != 1) {
                            holder.storyTitleTextView.setVisibility(View.GONE);
                            holder.titleTextUnderLine.setVisibility(View.GONE);
                            //}
                            holder.storytitle.setText(articleDataModelsNew.getChild().get(j).getDisplay_name());
                            if (2 == (articleDataModelsNew.getChild().get(j).getExtraData().get(0).getChallenge().getType())) {
                                holder.imageBody.setVisibility(View.VISIBLE);
                                try {
                                    Picasso.with(mcontext).load(articleDataModelsNew.getChild().get(j).getExtraData().get(0).getChallenge().getImageUrl()).placeholder(R.drawable.default_article).error(R.drawable.default_article)
                                            .fit().into(holder.imageBody);
                                    activeImageUrl.add(articleDataModelsNew.getChild().get(j).getExtraData().get(0).getChallenge().getImageUrl());

                                } catch (Exception e) {
                                    holder.imageBody.setImageDrawable(ContextCompat.getDrawable(mcontext, R.drawable.default_article));
                                }
                                n++;
                                break;
                            }
                        }
                    }

                }
                break;

            case 2:
                holder.previousAndThisWeekTextView.setVisibility(View.VISIBLE);
                holder.previousAndThisWeekTextView.setText("Previous Week's Challenges");*/

            default:
                for (int j = articleDataModelsNew.getChild().size() - n; j >= 0; j--) {
                    if ("1".equals(articleDataModelsNew.getChild().get(j).getPublicVisibility())) {
                        if ("1".equals(articleDataModelsNew.getChild().get(j).getExtraData().get(0).getChallenge().getActive())) {
                            if (position != 1) {
                                holder.previousAndThisWeekTextView.setVisibility(View.GONE);
                            } else {
                                holder.previousAndThisWeekTextView.setVisibility(View.VISIBLE);
                                holder.previousAndThisWeekTextView.setText(R.string.this_week_challenge);
                            }

                            challengeId.add(articleDataModelsNew.getChild().get(j).getId());
                            Display_Name.add(articleDataModelsNew.getChild().get(j).getDisplay_name());
                            holder.storyTitleTextView.setText(R.string.previous_week_challenge);
                            holder.storyTitleTextView.setVisibility(View.GONE);
                            holder.titleTextUnderLine.setVisibility(View.GONE);
                            holder.storytitle.setText(articleDataModelsNew.getChild().get(j).getDisplay_name());
                            if (2 == (articleDataModelsNew.getChild().get(j).getExtraData().get(0).getChallenge().getType())) {
                                holder.imageBody.setVisibility(View.VISIBLE);
                                try {
                                    Picasso.with(mcontext).load(articleDataModelsNew.getChild().get(j).getExtraData().get(0).getChallenge().getImageUrl()).placeholder(R.drawable.default_article).error(R.drawable.default_article)
                                            .fit().into(holder.imageBody);
                                    activeImageUrl.add(articleDataModelsNew.getChild().get(j).getExtraData().get(0).getChallenge().getImageUrl());

                                } catch (Exception e) {
                                    holder.imageBody.setImageDrawable(ContextCompat.getDrawable(mcontext, R.drawable.default_article));
                                }
                                n++;
                                break;
                            }
                        }
                    }

                }
                break;
        }

    }
    //}

    @Override
    public int getItemCount() {
        return count;
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
