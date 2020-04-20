package com.mycity4kids.ui.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.crashlytics.android.Crashlytics;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.models.Topics;
import com.mycity4kids.models.TopicsResponse;
import com.mycity4kids.models.response.VlogsListingAndDetailResult;
import com.mycity4kids.retrofitAPIsInterfaces.TopicsCategoryAPI;
import com.mycity4kids.utils.AppUtils;
import com.mycity4kids.utils.ArrayAdapterFactory;
import com.squareup.picasso.Picasso;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

public class VideoChallengeDetailListingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private LayoutInflater mInflator;
    ArrayList<VlogsListingAndDetailResult> articleDataModelsNew;
    public SimpleExoPlayer player;
    public String userAgent;
    public boolean isPaused = false;
    private Topics topics;
    private Topics videoChallengeTopics;
    private ArrayList<Topics> videoTopicList;
    private ArrayList<String> challengeId, videoChallengeId;
    private ArrayList<String> ImageUrl, videoImageUrl, videoStreamUrl;
    private ArrayList<String> Display_Name, videoDisplay_Name;
    private TopicsResponse res;
    private int num_of_categorys;
    private int screenWidth;
    private String imageUrl;
    RecyclerViewClickListner recyclerViewClickListner;


    public VideoChallengeDetailListingAdapter(RecyclerViewClickListner recyclerViewClickListner, Context pContext, String selectedId, Topics topics) {
        float density = pContext.getResources().getDisplayMetrics().density;
        mInflator = (LayoutInflater) pContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.recyclerViewClickListner = recyclerViewClickListner;
        mContext = pContext;
        this.topics = topics;
        String selectedId1 = selectedId;
        screenWidth = mContext.getResources().getDisplayMetrics().widthPixels;
        findActiveVideoChallenge();
    }


    public void setListData(ArrayList<VlogsListingAndDetailResult> mParentingLists) {
        ArrayList<VlogsListingAndDetailResult> mArticleListData = mParentingLists;
    }

    public void setNewListData(ArrayList<VlogsListingAndDetailResult> mParentingLists_new) {
        articleDataModelsNew = mParentingLists_new;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //  if (viewType == 1) {
        ViewHolderChallenge viewHolderChallenge = null;
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.mom_vlog_listing_adapter, parent, false);
        viewHolderChallenge = new ViewHolderChallenge(view, recyclerViewClickListner);
        return viewHolderChallenge;
//        } else {
//            AddVlogViewHolderChallenge addVlogViewHolderChallenge = null;
//            View view = LayoutInflater.from(parent.getContext())
//                    .inflate(R.layout.add_momvlog_list_item, parent, false);
//
//            addVlogViewHolderChallenge = new AddVlogViewHolderChallenge(view);
//            return addVlogViewHolderChallenge;
//        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolderChallenge) {
            Picasso.get().load(articleDataModelsNew.get(position).getThumbnail()).fit().into(((ViewHolderChallenge) holder).articleImageView);
            ((ViewHolderChallenge) holder).articleTitleTextView.setText(articleDataModelsNew.get(position).getTitle());
            ((ViewHolderChallenge) holder).author_name.setText(articleDataModelsNew.get(position).getAuthor().getFirstName().trim() + " " + articleDataModelsNew.get(position).getAuthor().getLastName().trim());
            ((ViewHolderChallenge) holder).viewCountTextView.setText(articleDataModelsNew.get(position).getView_count());
            ((ViewHolderChallenge) holder).recommendCountTextView1.setText(articleDataModelsNew.get(position).getLike_count());
            if (articleDataModelsNew.get(position).isIs_gold()) {
                ((ViewHolderChallenge) holder).imageWinner.setImageResource(R.drawable.ic_star_yellow);
            } else if (articleDataModelsNew.get(position).getWinner() == 1) {
                ((ViewHolderChallenge) holder).imageWinner.setImageResource(R.drawable.ic_trophy);

            } else {
                ((ViewHolderChallenge) holder).imageWinner.setVisibility(View.GONE);
            }


     /*       String userName = articleDataModelsNew.get(position).getAuthor().getFirstName() + " " + articleDataModelsNew.get(position).getAuthor().getLastName();
            if (StringUtils.isNullOrEmpty(userName) || userName.trim().equalsIgnoreCase("")) {
                holder.txvAuthorName.setText("NA");
            } else {
                holder.txvAuthorName.setText(userName);
            }
        } catch (Exception e) {
            holder.txvAuthorName.setText("NA");
        }
        try {
            imageUrl = articleDataModelsNew.get(position).getThumbnail() + "/tr:w-" + screenWidth + ",h-" + screenWidth / 2 + ",fo-auto";
            Picasso.get().load(imageUrl)
                    .placeholder(R.drawable.default_article).error(R.drawable.default_article).into(holder.articleImageView);
        } catch (Exception e) {
            holder.articleImageView.setImageResource(R.drawable.default_article);
        }
        if (articleDataModelsNew.get(position).isIs_gold()) {
            holder.goldLogo.setVisibility(View.VISIBLE);
        } else {
            holder.goldLogo.setVisibility(View.GONE);
        }
        if (article*/

        }


    }

    @Override
    public int getItemViewType(int position) {
        if (position != 0 && position % 5 == 0) {
            return 0;
        } else {
            return 1;
        }
    }

/*
    @Override
    public int getCount() {
        return articleDataModelsNew == null ? 0 : articleDataModelsNew.size();
    }

    @Override
    public Object getItem(int position) {
        return articleDataModelsNew.get(position);
    }*/

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public int getItemCount() {
        return articleDataModelsNew == null ? 0 : articleDataModelsNew.size();
    }

   /* @Override
    public int getViewTypeCount() {
        return 2;
    }*/


    //  @Override
/*
    public View getView(final int position, View view, ViewGroup parent) {
        if (getItemViewType(position) == 2) {
            VideoChallengeHeaderView videoChallengeHeaderView;
            if (view == null) {
                videoChallengeHeaderView = new VideoChallengeHeaderView();
                view = mInflator.inflate(R.layout.video_challenge_detail_listing_header, null);
                videoChallengeHeaderView.mExoPlayerView = (PlayerView) view.findViewById(R.id.exoplayerChallengeDetailListing);
                videoChallengeHeaderView.challengeNameText = (TextView) view.findViewById(R.id.ChallengeNameText);
                videoChallengeHeaderView.rootChallengeHeaderContainer = (RelativeLayout) view.findViewById(R.id.rootChallengeHeaderContainer);
                videoChallengeHeaderView.submitButtonVideoChallenge = (TextView) view.findViewById(R.id.submit_story_text);
                view.setTag(videoChallengeHeaderView);
            } else {
                videoChallengeHeaderView = (VideoChallengeHeaderView) view.getTag();
            }

            videoChallengeHeaderView.submitButtonVideoChallenge.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, ChooseVideoCategoryActivity.class);

                    mContext.startActivity(intent);
                }
            });

            return view;

        } else if (getItemViewType(position) == 0) {
            AddVlogViewHolderChallenge addVlogViewHolder;
            if (view == null) {
                addVlogViewHolder = new AddVlogViewHolderChallenge();
                view = mInflator.inflate(R.layout.add_momvlog_list_item, null);
                addVlogViewHolder.goldLogo = (TextView) view.findViewById(R.id.goldLogo);
                addVlogViewHolder.winnerLayout = (RelativeLayout) view.findViewById(R.id.winnerLayout);
                addVlogViewHolder.momVlogLayout = (RelativeLayout) view.findViewById(R.id.momVlogLayout);
                addVlogViewHolder.title = (TextView) view.findViewById(R.id.title);
                addVlogViewHolder.sub_title = (TextView) view.findViewById(R.id.sub_title);
                addVlogViewHolder.videoLogBanner = (TextView) view.findViewById(R.id.videoLogBanner);
                addVlogViewHolder.buttonsLayout = (LinearLayout) view.findViewById(R.id.buttonsLayout);
                addVlogViewHolder.txvArticleTitle = (TextView) view.findViewById(R.id.txvArticleTitle);
                addVlogViewHolder.txvAuthorName = (TextView) view.findViewById(R.id.txvAuthorName);
                addVlogViewHolder.articleImageView = (ImageView) view.findViewById(R.id.articleImageView);
                addVlogViewHolder.authorImageView = (ImageView) view.findViewById(R.id.authorImageView);
                addVlogViewHolder.viewCountTextView = (TextView) view.findViewById(R.id.viewCountTextView);
                addVlogViewHolder.commentCountTextView = (TextView) view.findViewById(R.id.commentCountTextView);
                addVlogViewHolder.recommendCountTextView = (TextView) view.findViewById(R.id.recommendCountTextView);
                addVlogViewHolder.addMomVlogImageView = (ImageView) view.findViewById(R.id.addMomVlogImageView);
                Drawable drawable = mContext.getResources().getDrawable(R.drawable.ic_star_gold_videos);
                drawable = DrawableCompat.wrap(drawable);
                DrawableCompat.setTint(drawable, mContext.getResources().getColor(R.color.gold_color_video_listing));
                DrawableCompat.setTintMode(drawable, PorterDuff.Mode.SRC_IN);
                addVlogViewHolder.goldLogo.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
                addVlogViewHolder.momVlogLayout.setVisibility(View.GONE);
                addVlogViewHolder.title.setVisibility(View.GONE);
                addVlogViewHolder.sub_title.setVisibility(View.GONE);
                addVlogViewHolder.videoLogBanner.setVisibility(View.GONE);
                addVlogViewHolder.buttonsLayout.setVisibility(View.GONE);
                if (AppConstants.LOCALE_HINDI.equals(SharedPrefUtils.getAppLocale(mContext))) {
                    addVlogViewHolder.addMomVlogImageView.setImageResource(R.drawable.add_mom_vlog_hi);
                } else {
                    addVlogViewHolder.addMomVlogImageView.setImageResource(R.drawable.add_mom_vlog_en);
                }
                view.setTag(addVlogViewHolder);
            } else {
                addVlogViewHolder = (AddVlogViewHolderChallenge) view.getTag();
            }

            addVlogViewHolder.txvArticleTitle.setText(articleDataModelsNew.get(position).getTitle());
            addVlogViewHolder.viewCountTextView.setVisibility(View.GONE);
            addVlogViewHolder.commentCountTextView.setVisibility(View.GONE);
            addVlogViewHolder.recommendCountTextView.setVisibility(View.GONE);

            try {
                String userName = articleDataModelsNew.get(position).getAuthor().getFirstName() + " " + articleDataModelsNew.get(position).getAuthor().getLastName();
                if (StringUtils.isNullOrEmpty(userName) || userName.trim().equalsIgnoreCase("")) {
                    addVlogViewHolder.txvAuthorName.setText("NA");
                } else {
                    addVlogViewHolder.txvAuthorName.setText(userName);
                }
            } catch (Exception e) {
                addVlogViewHolder.txvAuthorName.setText("NA");
            }
            try {
                imageUrl = articleDataModelsNew.get(position).getThumbnail() + "/tr:w-" + screenWidth + ",h-" + screenWidth / 2 + ",fo-auto";
                Picasso.get().load(imageUrl)
                        .placeholder(R.drawable.default_article).error(R.drawable.default_article).into(addVlogViewHolder.articleImageView);
            } catch (Exception e) {
                addVlogViewHolder.articleImageView.setImageResource(R.drawable.default_article);
            }
            if (articleDataModelsNew.get(position).isIs_gold()) {
                addVlogViewHolder.goldLogo.setVisibility(View.VISIBLE);
            } else {
                addVlogViewHolder.goldLogo.setVisibility(View.GONE);
            }
            if (articleDataModelsNew.get(position).getWinner() != 0) {
                addVlogViewHolder.winnerLayout.setVisibility(View.VISIBLE);
            } else {
                addVlogViewHolder.winnerLayout.setVisibility(View.GONE);

            }

            addVlogViewHolder.addMomVlogImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MixpanelAPI mixpanel = MixpanelAPI.getInstance(BaseApplication.getAppContext(), AppConstants.MIX_PANEL_TOKEN);
                    MixPanelUtils.pushAddMomVlogClickEvent(mixpanel, topics.getDisplay_name());
                    if (videoChallengeTopics == null) {
                        findActiveVideoChallenge();
                    } else {
                        Intent cityIntent = new Intent(mContext, ChooseVideoCategoryActivity.class);
                        cityIntent.putExtra("comingFrom", "notFromChallenge");
                        mContext.startActivity(cityIntent);
                    }
                }
            });
            return view;
        } else {
            ViewHolderChallenge holder;
            if (view == null) {
                holder = new ViewHolderChallenge();
                view = mInflator.inflate(R.layout.video_listing_item, null);
                holder.winnerLayout = (RelativeLayout) view.findViewById(R.id.winnerLayout);

                holder.goldLogo = (TextView) view.findViewById(R.id.goldLogo);
                holder.txvArticleTitle = (TextView) view.findViewById(R.id.txvArticleTitle);
                holder.txvAuthorName = (TextView) view.findViewById(R.id.txvAuthorName);
                holder.articleImageView = (ImageView) view.findViewById(R.id.articleImageView);
                holder.authorImageView = (ImageView) view.findViewById(R.id.authorImageView);
                holder.viewCountTextView = (TextView) view.findViewById(R.id.viewCountTextView);
                holder.commentCountTextView = (TextView) view.findViewById(R.id.commentCountTextView);
                holder.recommendCountTextView = (TextView) view.findViewById(R.id.recommendCountTextView);
                Drawable drawable = mContext.getResources().getDrawable(R.drawable.ic_star_gold_videos);
                drawable = DrawableCompat.wrap(drawable);
                DrawableCompat.setTint(drawable, mContext.getResources().getColor(R.color.gold_color_video_listing));
                DrawableCompat.setTintMode(drawable, PorterDuff.Mode.SRC_IN);
                holder.goldLogo.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
                Log.d("SetTag", "VLOGSetTag = " + position);
                view.setTag(holder);
            } else {
                holder = (ViewHolderChallenge) view.getTag();
            }
            holder.txvArticleTitle.setText(articleDataModelsNew.get(position).getTitle());
            holder.viewCountTextView.setVisibility(View.GONE);
            holder.commentCountTextView.setVisibility(View.GONE);
            holder.recommendCountTextView.setVisibility(View.GONE);

            try {
                String userName = articleDataModelsNew.get(position).getAuthor().getFirstName() + " " + articleDataModelsNew.get(position).getAuthor().getLastName();
                if (StringUtils.isNullOrEmpty(userName) || userName.trim().equalsIgnoreCase("")) {
                    holder.txvAuthorName.setText("NA");
                } else {
                    holder.txvAuthorName.setText(userName);
                }
            } catch (Exception e) {
                holder.txvAuthorName.setText("NA");
            }
            try {
                imageUrl = articleDataModelsNew.get(position).getThumbnail() + "/tr:w-" + screenWidth + ",h-" + screenWidth / 2 + ",fo-auto";
                Picasso.get().load(imageUrl)
                        .placeholder(R.drawable.default_article).error(R.drawable.default_article).into(holder.articleImageView);
            } catch (Exception e) {
                holder.articleImageView.setImageResource(R.drawable.default_article);
            }
            if (articleDataModelsNew.get(position).isIs_gold()) {
                holder.goldLogo.setVisibility(View.VISIBLE);
            } else {
                holder.goldLogo.setVisibility(View.GONE);
            }
            if (articleDataModelsNew.get(position).getWinner() != 0) {
                holder.winnerLayout.setVisibility(View.VISIBLE);
            } else {
                holder.winnerLayout.setVisibility(View.GONE);

            }

            return view;
        }


    }
*/

    private void findActiveVideoChallenge() {
        try {
            if (videoTopicList != null && videoTopicList.size() != 0) {
                videoChallengeId = new ArrayList<>();
                videoDisplay_Name = new ArrayList<>();
                videoImageUrl = new ArrayList<>();
                videoStreamUrl = new ArrayList<>();
                num_of_categorys = videoTopicList.get(0).getChild().size();
                if (num_of_categorys != 0) {
                    for (int j = 0; j < num_of_categorys; j++) {
                        if (videoTopicList.get(0).getChild().get(j).getId().equals("category-ee7ea82543bd4bc0a8dad288561f2beb")) {
                            videoChallengeTopics = videoTopicList.get(0).getChild().get(j);
                        }
                    }
                }
            }
            if (videoTopicList == null || videoTopicList.size() == 0) {
                FileInputStream fileInputStream = BaseApplication.getAppContext().openFileInput(AppConstants.CATEGORIES_JSON_FILE);
                String fileContent = AppUtils.convertStreamToString(fileInputStream);
                Gson gson = new GsonBuilder().registerTypeAdapterFactory(new ArrayAdapterFactory()).create();
                res = gson.fromJson(fileContent, TopicsResponse.class);
                videoTopicList = new ArrayList<Topics>();
                if (res != null) {
                    for (int i = 0; i < res.getData().size(); i++) {
                        if (AppConstants.HOME_VIDEOS_CATEGORYID.equals(res.getData().get(i).getId())) {
                            videoTopicList.add(res.getData().get(i));
                        }
                    }
                    videoChallengeId = new ArrayList<>();
                    videoDisplay_Name = new ArrayList<>();
                    videoImageUrl = new ArrayList<>();
                    videoStreamUrl = new ArrayList<>();
                    if (videoTopicList.get(0).getChild().size() != 0) {
                        num_of_categorys = videoTopicList.get(0).getChild().size();
                    }
                    if (num_of_categorys != 0) {
                        for (int j = 0; j < num_of_categorys; j++) {
                            if (videoTopicList.get(0).getChild().get(j).getId().equals("category-ee7ea82543bd4bc0a8dad288561f2beb")) {

                                videoChallengeTopics = videoTopicList.get(0).getChild().get(j);

                            }
                        }
                    }
                }
            }
        } catch (FileNotFoundException e) {
            Crashlytics.logException(e);
            Log.d("FileNotFoundException", Log.getStackTraceString(e));
            Retrofit retro = BaseApplication.getInstance().getRetrofit();
            final TopicsCategoryAPI topicsAPI = retro.create(TopicsCategoryAPI.class);
            Call<ResponseBody> caller = topicsAPI.downloadTopicsJSON();
            caller.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                    boolean writtenToDisk = AppUtils.writeResponseBodyToDisk(BaseApplication.getAppContext(), AppConstants.CATEGORIES_JSON_FILE, response.body());
                    Log.d("TopicsFilterActivity", "file download was a success? " + writtenToDisk);
                    try {
                        FileInputStream fileInputStream = BaseApplication.getAppContext().openFileInput(AppConstants.CATEGORIES_JSON_FILE);
                        String fileContent = AppUtils.convertStreamToString(fileInputStream);
                        Gson gson = new GsonBuilder().registerTypeAdapterFactory(new ArrayAdapterFactory()).create();
                        res = gson.fromJson(fileContent, TopicsResponse.class);
                        videoTopicList = new ArrayList<Topics>();
                        if (res != null) {
                            for (int i = 0; i < res.getData().size(); i++) {
                                if (AppConstants.HOME_VIDEOS_CATEGORYID.equals(res.getData().get(i).getId())) {
                                    videoTopicList.add(res.getData().get(i));
                                }
                            }
                            if (videoTopicList.size() != 0 && videoTopicList != null) {
                                videoChallengeId = new ArrayList<>();
                                videoDisplay_Name = new ArrayList<>();
                                videoImageUrl = new ArrayList<>();
                                videoStreamUrl = new ArrayList<>();
                                num_of_categorys = videoTopicList.get(0).getChild().size();
                                if (num_of_categorys != 0) {
                                    for (int j = 0; j < num_of_categorys; j++) {
                                        if (videoTopicList.get(0).getChild().get(j).getId().equals(AppConstants.VIDEO_CHALLENGE_ID)) {

                                            videoChallengeTopics = videoTopicList.get(0).getChild().get(j);

                                        }
                                    }
                                }
                            }
                        }
                    } catch (FileNotFoundException e) {
                        Crashlytics.logException(e);
                        Log.d("FileNotFoundException", Log.getStackTraceString(e));
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Crashlytics.logException(t);
                    Log.d("MC4KException", Log.getStackTraceString(t));
                }
            });
        }
    }


    private class ViewHolderChallenge extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView articleTitleTextView;
        TextView author_name;
        ImageView imageWinner;
        ImageView articleImageView;
        TextView viewCountTextView;
        TextView recommendCountTextView1;
        RelativeLayout container;

        ViewHolderChallenge(@NonNull View itemView, RecyclerViewClickListner recyclerViewClickListner) {
            super(itemView);

            articleTitleTextView = itemView.findViewById(R.id.articleTitleTextView);
            articleImageView = itemView.findViewById(R.id.articleImageView);
            author_name = itemView.findViewById(R.id.author_name);
            recommendCountTextView1 = itemView.findViewById(R.id.recommendCountTextView1);
            viewCountTextView = itemView.findViewById(R.id.viewCountTextView1);
            imageWinner = itemView.findViewById(R.id.imageWinner);
            container = itemView.findViewById(R.id.container);
            container.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            recyclerViewClickListner.onRecyclerClick(view, getAdapterPosition());
        }
    }


    class AddVlogViewHolderChallenge extends RecyclerView.ViewHolder {
        RelativeLayout winnerLayout, momVlogLayout;
        TextView goldLogo;
        ImageView addMomVlogImageView;
        TextView txvArticleTitle;
        TextView txvAuthorName;
        ImageView articleImageView;
        ImageView authorImageView;
        TextView viewCountTextView;
        TextView commentCountTextView;
        TextView recommendCountTextView;
        TextView title;
        TextView videoLogBanner;
        TextView sub_title;
        LinearLayout buttonsLayout;

        public AddVlogViewHolderChallenge(@NonNull View itemView) {
            super(itemView);
            goldLogo = (TextView) itemView.findViewById(R.id.goldLogo);
            winnerLayout = (RelativeLayout) itemView.findViewById(R.id.winnerLayout);
            momVlogLayout = (RelativeLayout) itemView.findViewById(R.id.momVlogLayout);
            title = (TextView) itemView.findViewById(R.id.title);

            sub_title = (TextView) itemView.findViewById(R.id.sub_title);
            videoLogBanner = (TextView) itemView.findViewById(R.id.videoLogBanner);
            buttonsLayout = (LinearLayout) itemView.findViewById(R.id.buttonsLayout);
            txvArticleTitle = (TextView) itemView.findViewById(R.id.txvArticleTitle);
            txvAuthorName = (TextView) itemView.findViewById(R.id.txvAuthorName);
            articleImageView = (ImageView) itemView.findViewById(R.id.articleImageView);
            authorImageView = (ImageView) itemView.findViewById(R.id.authorImageView);
            viewCountTextView = (TextView) itemView.findViewById(R.id.viewCountTextView);
            commentCountTextView = (TextView) itemView.findViewById(R.id.commentCountTextView);
            recommendCountTextView = (TextView) itemView.findViewById(R.id.recommendCountTextView);
            addMomVlogImageView = (ImageView) itemView.findViewById(R.id.addMomVlogImageView);
        }
    }

    class VideoChallengeHeaderView {
        RelativeLayout rootChallengeHeaderContainer;
        TextView submitButtonVideoChallenge;
        TextView challengeNameText;
        PlayerView mExoPlayerView;

    }

    public interface RecyclerViewClickListner {
        void onRecyclerClick(View v, int pos);
    }
}

