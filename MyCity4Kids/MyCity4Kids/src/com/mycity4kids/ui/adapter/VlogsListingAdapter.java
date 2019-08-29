package com.mycity4kids.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import androidx.core.graphics.drawable.DrawableCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.Topics;
import com.mycity4kids.models.TopicsResponse;
import com.mycity4kids.models.response.VlogsListingAndDetailResult;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.TopicsCategoryAPI;
import com.mycity4kids.ui.videochallengenewui.activity.NewVideoChallengeActivity;
import com.mycity4kids.utils.AppUtils;
import com.mycity4kids.utils.ArrayAdapterFactory;
import com.mycity4kids.widget.CustomFontTextView;
import com.squareup.picasso.Picasso;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * @author deepanker.chaudhary
 */
public class VlogsListingAdapter extends BaseAdapter {

    private ArrayList<VlogsListingAndDetailResult> mArticleListData;
    private Context mContext;
    private Topics topic;
    private LayoutInflater mInflator;
    ArrayList<VlogsListingAndDetailResult> articleDataModelsNew;
    Topics videoChallengeTopics;
    private final float density;
    private ArrayList<String> challengeId, videoChallengeId;
    private ArrayList<String> ImageUrl, videoImageUrl, videoStreamUrl;
    private ArrayList<String> deepLinkchallengeId;
    private ArrayList<String> deepLinkDisplayName;
    private ArrayList<String> deepLinkImageUrl;
    private ArrayList<Topics> shortStoriesTopicList;
    private ArrayList<Topics> videoTopicList;
    ArrayList<String> Display_Name, videoDisplay_Name;
    private int num_of_categorys;
    private TopicsResponse res;
    private Topics videoAd;

    public VlogsListingAdapter(Context pContext, Topics topic) {
        density = pContext.getResources().getDisplayMetrics().density;
        mInflator = (LayoutInflater) pContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = pContext;
        this.topic = topic;
        findActiveVideoChallenge();
    }

    public void setListData(ArrayList<VlogsListingAndDetailResult> mParentingLists) {
        mArticleListData = mParentingLists;
    }

    public void setNewListData(ArrayList<VlogsListingAndDetailResult> mParentingLists_new) {
        articleDataModelsNew = mParentingLists_new;
    }

    public void setRecommendedVideoAd(Topics videoAd) {
        this.videoAd = videoAd;
    }

    @Override
    public int getCount() {
        return articleDataModelsNew == null ? 0 : articleDataModelsNew.size();
    }

    @Override
    public Object getItem(int position) {
        return articleDataModelsNew.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        if (position != 0 && position % 9 == 0 && videoAd != null) {
            return 0;
        } else {
            return 1;
        }
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        if (getItemViewType(position) == 0) {
            AddVlogViewHolder addVlogViewHolder;
            if (view == null) {
                addVlogViewHolder = new AddVlogViewHolder();
                view = mInflator.inflate(R.layout.add_momvlog_list_item, null);
                addVlogViewHolder.videoLogBanner = (CustomFontTextView) view.findViewById(R.id.videoLogBanner);
                addVlogViewHolder.winnerLayout = (RelativeLayout) view.findViewById(R.id.winnerLayout);
                addVlogViewHolder.goldLogo = (TextView) view.findViewById(R.id.goldLogo);
                addVlogViewHolder.txvArticleTitle = (TextView) view.findViewById(R.id.txvArticleTitle);
                addVlogViewHolder.txvAuthorName = (TextView) view.findViewById(R.id.txvAuthorName);
                addVlogViewHolder.articleImageView = (ImageView) view.findViewById(R.id.articleImageView);
                addVlogViewHolder.authorImageView = (ImageView) view.findViewById(R.id.authorImageView);
                addVlogViewHolder.viewCountTextView = (TextView) view.findViewById(R.id.viewCountTextView);
                addVlogViewHolder.commentCountTextView = (TextView) view.findViewById(R.id.commentCountTextView);
                addVlogViewHolder.recommendCountTextView = (TextView) view.findViewById(R.id.recommendCountTextView);
                addVlogViewHolder.addMomVlogImageView = (ImageView) view.findViewById(R.id.addMomVlogImageView);
                /*addVlogViewHolder.goldLogo.setCompoundDrawablesWithIntrinsicBounds(mContext.getResources().getDrawable(R.drawable.ic_star_gold_videos), null, null, null);
                DrawableCompat.setTint();*/

                Drawable drawable = mContext.getResources().getDrawable(R.drawable.ic_star_gold_videos);
                drawable = DrawableCompat.wrap(drawable);
                DrawableCompat.setTint(drawable, mContext.getResources().getColor(R.color.gold_color_video_listing));
                DrawableCompat.setTintMode(drawable, PorterDuff.Mode.SRC_IN);
                addVlogViewHolder.goldLogo.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
                view.setTag(addVlogViewHolder);
            } else {
                addVlogViewHolder = (AddVlogViewHolder) view.getTag();
            }

            addVlogViewHolder.videoLogBanner.setText(videoAd.getDisplay_name());
            addVlogViewHolder.txvArticleTitle.setText(articleDataModelsNew.get(position).getTitle());
            addVlogViewHolder.viewCountTextView.setText(articleDataModelsNew.get(position).getView_count());
            addVlogViewHolder.commentCountTextView.setText(articleDataModelsNew.get(position).getComment_count());
            addVlogViewHolder.recommendCountTextView.setText(articleDataModelsNew.get(position).getLike_count());

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
                Picasso.with(mContext).load(articleDataModelsNew.get(position).getThumbnail())
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
                    if (videoChallengeTopics == null) {
                        findActiveVideoChallenge();
                    } else {
                        Utils.momVlogEvent(mContext, "Video Listing", "Live_challenge_banner", "", "android",
                                SharedPrefUtils.getAppLocale(mContext), SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId(),
                                String.valueOf(System.currentTimeMillis()), "Show_challenge_detail", "", videoAd.getId());
                        Intent intent = new Intent(mContext, NewVideoChallengeActivity.class);
                        if (videoAd.getExtraData() != null && videoAd.getExtraData().size() != 0) {
                            Topics.ExtraData extraData = videoAd.getExtraData().get(0);
                            intent.putExtra("Display_Name", new ArrayList<>(Arrays.asList(videoAd.getDisplay_name())));
                            intent.putExtra("screenName", "MomVlogs");
                            intent.putExtra("challenge", new ArrayList<>(Arrays.asList(videoAd.getId())));
                            intent.putExtra("position", 0);
                            intent.putExtra("StreamUrl", new ArrayList<>(Arrays.asList(extraData.getChallenge().getVideoUrl())));
                            intent.putExtra("rules", new ArrayList<>(Arrays.asList(extraData.getChallenge().getRules())));
                            intent.putExtra("maxDuration", extraData.getChallenge().getMax_duration());
                            intent.putExtra("mappedCategory", new ArrayList<>(Arrays.asList(extraData.getChallenge().getMapped_category())));
                            intent.putExtra("topics", videoAd.getParentName());
                            intent.putExtra("parentId", videoAd.getParentId());
                            intent.putExtra("StringUrl", new ArrayList<>(Arrays.asList(extraData.getChallenge().getImageUrl())));
                            intent.putExtra("Topic", new Gson().toJson(videoAd));
                            mContext.startActivity(intent);
                        }
                    }
                }
            });
            return view;
        } else {
            ViewHolder holder;
            if (view == null) {
                holder = new ViewHolder();
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
                holder = (ViewHolder) view.getTag();
            }
            holder.txvArticleTitle.setText(articleDataModelsNew.get(position).getTitle());
            holder.viewCountTextView.setText(articleDataModelsNew.get(position).getView_count());
            holder.commentCountTextView.setText(articleDataModelsNew.get(position).getComment_count());
            holder.recommendCountTextView.setText(articleDataModelsNew.get(position).getLike_count());

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
                Picasso.with(mContext).load(articleDataModelsNew.get(position).getThumbnail())
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

    class ViewHolder {
        RelativeLayout winnerLayout;
        TextView txvArticleTitle;
        TextView txvAuthorName;
        ImageView articleImageView;
        ImageView authorImageView;
        TextView viewCountTextView;
        TextView commentCountTextView;
        TextView recommendCountTextView;
        TextView goldLogo;
    }

    class AddVlogViewHolder {
        RelativeLayout winnerLayout;
        ImageView addMomVlogImageView;
        TextView goldLogo;
        TextView txvArticleTitle;
        TextView txvAuthorName;
        ImageView articleImageView;
        ImageView authorImageView;
        TextView viewCountTextView;
        TextView commentCountTextView;
        TextView recommendCountTextView;
        CustomFontTextView videoLogBanner;
    }

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
}
