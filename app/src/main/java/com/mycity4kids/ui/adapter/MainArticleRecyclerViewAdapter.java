package com.mycity4kids.ui.adapter;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.campaignmodels.CampaignDataListResult;
import com.mycity4kids.models.request.ArticleDetailRequest;
import com.mycity4kids.models.request.DeleteBookmarkRequest;
import com.mycity4kids.models.response.AddBookmarkResponse;
import com.mycity4kids.models.response.ArticleListingResult;
import com.mycity4kids.models.response.VlogsListingAndDetailResult;
import com.mycity4kids.models.response.VlogsListingResponse;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.utils.AppUtils;
import com.mycity4kids.utils.ArrayAdapterFactory;
import com.mycity4kids.utils.StringUtils;
import com.mycity4kids.widget.StoryShareCardWidget;
import com.squareup.picasso.Picasso;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by hemant on 4/12/17.
 */

public class MainArticleRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private MixpanelAPI mixpanel;
    private static int ARTICLE = 0;
    private static int AD = 1;
    private static int STORY = 3;
    private static int VIDEOS = 5;
    private static int MM_CAMPAIGN = 6;
    private static int TORCAI_AD = 7;
    private final Context mainContext;
    private final LayoutInflater layoutInflater;
    private ArrayList<ArticleListingResult> articleDataModelsNew;
    private ArrayList<CampaignDataListResult> campaignListDataModels;
    private RecyclerViewClickListener recyclerViewClickListener;
    private boolean isRequestRunning;
    private String screenName;
    private Gson gson;
    private boolean showVideoFlag;
    private String htmlContent = "";
    private String dataType = "";
    private boolean showAds = false;

    public MainArticleRecyclerViewAdapter(Context context, RecyclerViewClickListener listener,
            boolean topicHeaderVisibilityFlag, String screenName, boolean showVideoFlag) {
        mainContext = context;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        gson = new GsonBuilder().registerTypeAdapterFactory(new ArrayAdapterFactory()).create();
        recyclerViewClickListener = listener;
        mixpanel = MixpanelAPI.getInstance(BaseApplication.getAppContext(), AppConstants.MIX_PANEL_TOKEN);
        this.screenName = screenName;
        this.showVideoFlag = showVideoFlag;
    }

    public void setNewListData(ArrayList<ArticleListingResult> articleDataModelsNew) {
        this.articleDataModelsNew = articleDataModelsNew;
    }

    @Override
    public int getItemCount() {
        return articleDataModelsNew == null ? 0 : articleDataModelsNew.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 3) {
            return TORCAI_AD;
        } else if (position > 3 && position % 3 == 0) {
            if (showVideoFlag) {
                if (position == 6) {
                    return MM_CAMPAIGN;
                } else {
                    return VIDEOS;
                }
            } else {
                if (AppConstants.CONTENT_TYPE_ARTICLE.equals(articleDataModelsNew.get(position).getContentType())) {
                    return ARTICLE;
                } else {
                    return STORY;
                }
            }
        } else {
            if (AppConstants.CONTENT_TYPE_ARTICLE.equals(articleDataModelsNew.get(position).getContentType())) {
                return ARTICLE;
            } else {
                return STORY;
            }
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == AD) {
            View v0 = layoutInflater.inflate(R.layout.facebook_ad_list_item, parent, false);
            return new AdViewHolder(v0);
        } else if (viewType == TORCAI_AD) {
            View v0 = layoutInflater.inflate(R.layout.campaign_carousel_container, parent, false);
            return new TorcaiAdsViewHolder(v0);
        } else if (viewType == MM_CAMPAIGN) {
            View v0 = layoutInflater.inflate(R.layout.campaign_carousel_container, parent, false);
            return new CampaignCarouselViewHolder(v0);
        } else if (viewType == VIDEOS) {
            View v0 = layoutInflater.inflate(R.layout.video_carousel_container, parent, false);
            return new VideoCarouselViewHolder(v0);
        } else if (viewType == ARTICLE) {
            View v0 = layoutInflater.inflate(R.layout.article_listing_item, parent, false);
            return new FeedViewHolder(v0);
        } else {
            View v0 = layoutInflater.inflate(R.layout.short_story_listing_item, parent, false);
            return new ShortStoriesViewHolder(v0);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof HeaderViewHolder) {
            HeaderViewHolder viewHolder = (HeaderViewHolder) holder;
            addArticleItem(viewHolder.txvArticleTitle, viewHolder.forYouInfoLL, viewHolder.viewCountTextView,
                    viewHolder.commentCountTextView, viewHolder.recommendCountTextView, viewHolder.txvAuthorName,
                    viewHolder.articleImageView, viewHolder.videoIndicatorImageView,
                    viewHolder.bookmarkArticleImageView, viewHolder.watchLaterImageView,
                    articleDataModelsNew.get(position), position, viewHolder);
        } else if (holder instanceof FeedViewHolder) {
            FeedViewHolder viewHolder = (FeedViewHolder) holder;
            addArticleItem(viewHolder.txvArticleTitle, viewHolder.forYouInfoLL, viewHolder.viewCountTextView,
                    viewHolder.commentCountTextView, viewHolder.recommendCountTextView, viewHolder.txvAuthorName,
                    viewHolder.articleImageView, viewHolder.videoIndicatorImageView,
                    viewHolder.bookmarkArticleImageView, viewHolder.watchLaterImageView,
                    articleDataModelsNew.get(position), position, viewHolder);
        } else if (holder instanceof VideoCarouselViewHolder) {
            if (!articleDataModelsNew.get(position).isCarouselRequestRunning() && !articleDataModelsNew.get(position)
                    .isResponseReceived()) {
                articleDataModelsNew.get(position).setCarouselRequestRunning(true);
                Log.d("VideoCarouselViewHolder", "BEFORE API = " + position);
                new LoadVideoCarouselAsyncTask((VideoCarouselViewHolder) holder, position).execute();
            } else {
                Log.d("VideoCarouselViewHolder", "RECYCLED = " + position
                        + "request = " + articleDataModelsNew.get(position).isCarouselRequestRunning() + " response = "
                        + articleDataModelsNew.get(position).isResponseReceived());
                populateCarouselVideos((VideoCarouselViewHolder) holder,
                        articleDataModelsNew.get(position).getCarouselVideoList());
            }
            VideoCarouselViewHolder viewHolder = (VideoCarouselViewHolder) holder;
            if (AppConstants.CONTENT_TYPE_ARTICLE.equals(articleDataModelsNew.get(position).getContentType())) {
                viewHolder.headerArticleView.setVisibility(View.VISIBLE);
                viewHolder.storyHeaderView.setVisibility(View.GONE);

                addArticleItem(viewHolder.txvArticleTitle, viewHolder.forYouInfoLL, viewHolder.viewCountTextView,
                        viewHolder.commentCountTextView, viewHolder.recommendCountTextView, viewHolder.txvAuthorName,
                        viewHolder.articleImageView, viewHolder.videoIndicatorImageView,
                        viewHolder.bookmarkArticleImageView, viewHolder.watchLaterImageView,
                        articleDataModelsNew.get(position), position, viewHolder);
            } else {
                viewHolder.headerArticleView.setVisibility(View.GONE);
                viewHolder.storyHeaderView.setVisibility(View.VISIBLE);
                addShortStoryItem(viewHolder, viewHolder.storyImage, viewHolder.authorNameTextView,
                        viewHolder.storyCommentCountTextView, viewHolder.storyRecommendationCountTextView,
                        viewHolder.likeImageView, articleDataModelsNew.get(position), viewHolder.followAuthorTextView,
                        viewHolder.storyAuthorTextView, viewHolder.shareStoryImageView, viewHolder.logoImageView);
            }
        } else if (holder instanceof CampaignCarouselViewHolder) {
            CampaignCarouselViewHolder viewHolder = (CampaignCarouselViewHolder) holder;
            if ("adslot".equals(dataType)) {
                viewHolder.adSlotContainer.setVisibility(View.VISIBLE);
                viewHolder.adSlotWebView.setVisibility(View.VISIBLE);
                viewHolder.relativeLayoutContainer.setVisibility(View.GONE);
                viewHolder.videoCarouselContainer.setVisibility(View.GONE);
                viewHolder.adSlotWebView.loadDataWithBaseURL("", htmlContent, "text/html", "utf-8", "");
            } else if ("campaign".equals(dataType)) {
                viewHolder.adSlotContainer.setVisibility(View.GONE);
                viewHolder.adSlotWebView.setVisibility(View.GONE);
                viewHolder.relativeLayoutContainer.setVisibility(View.VISIBLE);
                viewHolder.videoCarouselContainer.setVisibility(View.VISIBLE);
                try {
                    if (campaignListDataModels != null && campaignListDataModels.size() > 0) {
                        addCampaignCard(viewHolder.campaignHeader, viewHolder.brandImg, viewHolder.brandName,
                                viewHolder.campaignName, viewHolder.campaignStatus, campaignListDataModels.get(0),
                                position, viewHolder);
                    }
                } catch (Exception e) {
                    FirebaseCrashlytics.getInstance().recordException(e);
                    Log.d("MC4kException", Log.getStackTraceString(e));
                }
            } else {
                viewHolder.adSlotContainer.setVisibility(View.GONE);
                viewHolder.adSlotWebView.setVisibility(View.GONE);
                viewHolder.relativeLayoutContainer.setVisibility(View.GONE);
                viewHolder.videoCarouselContainer.setVisibility(View.GONE);
            }
            if (AppConstants.CONTENT_TYPE_ARTICLE.equals(articleDataModelsNew.get(position).getContentType())) {
                viewHolder.headerArticleView.setVisibility(View.VISIBLE);
                viewHolder.storyHeaderView.setVisibility(View.GONE);

                addArticleItem(viewHolder.txvArticleTitle, viewHolder.forYouInfoLL, viewHolder.viewCountTextView,
                        viewHolder.commentCountTextView, viewHolder.recommendCountTextView, viewHolder.txvAuthorName,
                        viewHolder.articleImageView, viewHolder.videoIndicatorImageView,
                        viewHolder.bookmarkArticleImageView, viewHolder.watchLaterImageView,
                        articleDataModelsNew.get(position), position, viewHolder);
            } else {
                viewHolder.headerArticleView.setVisibility(View.GONE);
                viewHolder.storyHeaderView.setVisibility(View.VISIBLE);
                addShortStoryItem(viewHolder, viewHolder.storyImage, viewHolder.authorNameTextView,
                        viewHolder.storyCommentCountTextView, viewHolder.storyRecommendationCountTextView,
                        viewHolder.likeImageView, articleDataModelsNew.get(position), viewHolder.followAuthorTextView,
                        viewHolder.storyAuthorTextView, viewHolder.shareStoryImageView, viewHolder.logoImageView);
            }
        } else if (holder instanceof TorcaiAdsViewHolder) {
            TorcaiAdsViewHolder viewHolder = (TorcaiAdsViewHolder) holder;
            if (showAds) {
                viewHolder.adSlotContainer.setVisibility(View.VISIBLE);
                viewHolder.adSlotWebView.setVisibility(View.VISIBLE);
                viewHolder.adSlotWebView.loadDataWithBaseURL("", htmlContent, "text/html", "utf-8", "");
            } else {
                viewHolder.adSlotContainer.setVisibility(View.GONE);
                viewHolder.adSlotWebView.setVisibility(View.GONE);
            }

            if (AppConstants.CONTENT_TYPE_ARTICLE.equals(articleDataModelsNew.get(position).getContentType())) {
                viewHolder.headerArticleView.setVisibility(View.VISIBLE);
                viewHolder.storyHeaderView.setVisibility(View.GONE);

                addArticleItem(viewHolder.txvArticleTitle, viewHolder.forYouInfoLL, viewHolder.viewCountTextView,
                        viewHolder.commentCountTextView, viewHolder.recommendCountTextView, viewHolder.txvAuthorName,
                        viewHolder.articleImageView, viewHolder.videoIndicatorImageView,
                        viewHolder.bookmarkArticleImageView, viewHolder.watchLaterImageView,
                        articleDataModelsNew.get(position), position, viewHolder);
            } else {
                viewHolder.headerArticleView.setVisibility(View.GONE);
                viewHolder.storyHeaderView.setVisibility(View.VISIBLE);
                addShortStoryItem(viewHolder, viewHolder.storyImage, viewHolder.authorNameTextView,
                        viewHolder.storyCommentCountTextView, viewHolder.storyRecommendationCountTextView,
                        viewHolder.likeImageView, articleDataModelsNew.get(position), viewHolder.followAuthorTextView,
                        viewHolder.storyAuthorTextView, viewHolder.shareStoryImageView, viewHolder.logoImageView);
            }
        } else {
            ShortStoriesViewHolder viewHolder = (ShortStoriesViewHolder) holder;
            addShortStoryItem(viewHolder, viewHolder.storyImage, viewHolder.authorNameTextView,
                    viewHolder.storyCommentCountTextView, viewHolder.storyRecommendationCountTextView,
                    viewHolder.likeImageView, articleDataModelsNew.get(position), viewHolder.followAuthorTextView,
                    viewHolder.storyAuthorTextView, viewHolder.shareStoryImageView, viewHolder.logoImageView);
        }
    }

    private void addCampaignCard(ImageView campaignHeader, CircularImageView brandImg, TextView brandName,
            TextView campaignName, TextView campaignStatus, final CampaignDataListResult data, final int position,
            final RecyclerView.ViewHolder holder) {
        Picasso.get().load(data.getImageUrl()).placeholder(R.drawable.default_article).error(R.drawable.default_article)
                .into(campaignHeader);
        Picasso.get().load(data.getBrandDetails().getImageUrl()).placeholder(R.drawable.default_article)
                .error(R.drawable.default_article).into(brandImg);
        brandName.setText(data.getBrandDetails().getName());
        campaignName.setText(data.getName());
        setTextAndColor(data.getCampaignStatus(), campaignStatus);
    }

    private void setTextAndColor(int status, TextView campaignStatus) {
        if (status == 0) {
            campaignStatus.setText(mainContext.getResources().getString(R.string.campaign_details_expired));
            campaignStatus.setBackgroundResource(R.drawable.campaign_expired);
        } else if (status == 1 || status == 18) {
            campaignStatus.setText(mainContext.getResources().getString(R.string.campaign_details_apply_now));
            campaignStatus.setBackgroundResource(R.drawable.subscribe_now);
        } else if (status == 2) {
            campaignStatus.setText(mainContext.getResources().getString(R.string.campaign_details_submission_open));
            campaignStatus.setBackgroundResource(R.drawable.campaign_subscription_open);
        } else if (status == 22) {
            campaignStatus.setText(mainContext.getResources().getString(R.string.campaign_details_submission_open));
            campaignStatus.setBackgroundResource(R.drawable.campaign_subscription_open);
        } else if (status == 3) {
            campaignStatus.setText(mainContext.getResources().getString(R.string.campaign_details_applied));
            campaignStatus.setBackgroundResource(R.drawable.campaign_applied);
        } else if (status == 21) {
            campaignStatus.setText(mainContext.getResources().getString(R.string.campaign_details_approved));
            campaignStatus.setBackgroundResource(R.drawable.campaign_subscribed);
        } else if (status == 4) {
            campaignStatus.setText(mainContext.getResources().getString(R.string.campaign_details_application_full));
            campaignStatus.setBackgroundResource(R.drawable.campaign_submission_full);
        } else if (status == 5) {
            campaignStatus.setText(mainContext.getResources().getString(R.string.campaign_details_apply_now));
            campaignStatus.setBackgroundResource(R.drawable.subscribe_now);
        } else if (status == 6) {
            campaignStatus.setText(mainContext.getResources().getString(R.string.campaign_list_proof_reject));
            campaignStatus.setBackgroundResource(R.drawable.campaign_rejected);
        } else if (status == 17) {
            campaignStatus.setText(mainContext.getResources().getString(R.string.campaign_list_proof_reject));
            campaignStatus.setBackgroundResource(R.drawable.campaign_rejected);
        } else if (status == 7) {
            campaignStatus.setText(mainContext.getResources().getString(R.string.campaign_details_completed));
            campaignStatus.setBackgroundResource(R.drawable.campaign_completed);
        } else if (status == 8) {
            campaignStatus.setText(mainContext.getResources().getString(R.string.campaign_details_invite_only));
            campaignStatus.setBackgroundResource(R.drawable.campaign_invite_only);
        } else if (status == 9) {
            campaignStatus.setText(mainContext.getResources().getString(R.string.campaign_list_proof_moderation));
            campaignStatus.setBackgroundResource(R.drawable.campaign_subscription_open);
        } else if (status == 16) {
            campaignStatus.setText(mainContext.getResources().getString(R.string.campaign_list_proof_moderation));
            campaignStatus.setBackgroundResource(R.drawable.campaign_subscription_open);
        } else if (status == 10) {
            campaignStatus.setText(mainContext.getResources().getString(R.string.campaign_list_proof_reject));
            campaignStatus.setBackgroundResource(R.drawable.campaign_proof_rejected_bg);
        }
    }

    private void addArticleItem(TextView articleTitleTV, LinearLayout forYouInfoLL, TextView viewCountTV,
            TextView commentCountTV, TextView recommendCountTV, TextView authorNameTV,
            ImageView articleIV, ImageView videoIndicatorIV, ImageView bookmarkArticleIV, ImageView watchLaterIV,
            final ArticleListingResult data, final int position, final RecyclerView.ViewHolder holder) {
        articleTitleTV.setText(data.getTitle());
        forYouInfoLL.setVisibility(View.GONE);
        if (null == data.getArticleCount() || "0".equals(data.getArticleCount())) {
            viewCountTV.setVisibility(View.GONE);
        } else {
            viewCountTV.setVisibility(View.VISIBLE);
            try {
                viewCountTV.setText(AppUtils.withSuffix(Long.parseLong(data.getArticleCount())));
            } catch (Exception e) {
                viewCountTV.setText(data.getArticleCount());
            }
        }
        if (null == data.getCommentsCount() || "0".equals(data.getCommentsCount())) {
            commentCountTV.setVisibility(View.GONE);
        } else {
            commentCountTV.setVisibility(View.VISIBLE);
            try {
                commentCountTV.setText(AppUtils.withSuffix(Long.parseLong(data.getCommentsCount())));
            } catch (Exception e) {
                commentCountTV.setText(data.getCommentsCount());
            }
        }
        if (null == data.getLikesCount() || "0".equals(data.getLikesCount())) {
            recommendCountTV.setVisibility(View.GONE);
        } else {
            recommendCountTV.setVisibility(View.VISIBLE);
            try {
                recommendCountTV.setText(AppUtils.withSuffix(Long.parseLong(data.getLikesCount())));
            } catch (Exception e) {
                recommendCountTV.setText(data.getLikesCount());
            }
        }
        if (StringUtils.isNullOrEmpty(data.getUserName()) || data.getUserName().trim().equalsIgnoreCase("")) {
            authorNameTV.setText("NA");
        } else {
            authorNameTV.setText(data.getUserName());
        }
        try {
            if (!StringUtils.isNullOrEmpty(data.getVideoUrl())
                    && (data.getImageUrl().getThumbMax() == null || data.getImageUrl().getThumbMax()
                    .contains("default.jp"))) {
                Picasso.get().load(AppUtils.getYoutubeThumbnailURLMomspresso(data.getVideoUrl()))
                        .placeholder(R.drawable.default_article).into(articleIV);
            } else {
                if (!StringUtils.isNullOrEmpty(data.getImageUrl().getThumbMax())) {
                    Picasso.get().load(data.getImageUrl().getThumbMax())
                            .placeholder(R.drawable.default_article).error(R.drawable.default_article).into(articleIV);
                } else {
                    articleIV.setBackgroundResource(R.drawable.default_article);
                }
            }
        } catch (Exception e) {
            articleIV.setBackgroundResource(R.drawable.default_article);
        }
        if (!StringUtils.isNullOrEmpty(data.getVideoUrl())) {
            videoIndicatorIV.setVisibility(View.VISIBLE);
        } else {
            videoIndicatorIV.setVisibility(View.INVISIBLE);
        }
        if ("1".equals(data.getIsMomspresso())) {
            bookmarkArticleIV.setVisibility(View.VISIBLE);
            watchLaterIV.setVisibility(View.GONE);
            if ("0".equals(data.getIs_bookmark())) {
                bookmarkArticleIV.setImageDrawable(ContextCompat.getDrawable(mainContext, R.drawable.ic_bookmark));
            } else {
                bookmarkArticleIV.setImageDrawable(ContextCompat.getDrawable(mainContext, R.drawable.ic_bookmarked));
            }
        } else {
            bookmarkArticleIV.setVisibility(View.VISIBLE);
            watchLaterIV.setVisibility(View.GONE);
            if ("0".equals(data.getIs_bookmark())) {
                bookmarkArticleIV.setImageDrawable(ContextCompat.getDrawable(mainContext, R.drawable.ic_bookmark));
            } else {
                bookmarkArticleIV.setImageDrawable(ContextCompat.getDrawable(mainContext, R.drawable.ic_bookmarked));
            }
        }
        watchLaterIV.setOnClickListener(v -> {
            addRemoveWatchLater(position, holder);
            Utils.pushWatchLaterArticleEvent(mainContext, "ArticleListing",
                    SharedPrefUtils.getUserDetailModel(mainContext).getDynamoId() + "",
                    data.getId(), data.getUserId() + "~" + data.getUserName());
        });
        bookmarkArticleIV.setOnClickListener(v -> {
            if (!isRequestRunning) {
                addRemoveBookmark(position, holder);
            }
            Utils.pushBookmarkArticleEvent(mainContext, "ArticleListing",
                    SharedPrefUtils.getUserDetailModel(mainContext).getDynamoId() + "",
                    data.getId(), data.getUserId() + "~" + data.getUserName());
        });
        if (holder instanceof FeedViewHolder) {
            setWinnerOrGoldFlag(((FeedViewHolder) holder).trophyImageView, articleDataModelsNew.get(position));
        }
    }

    private void setWinnerOrGoldFlag(ImageView winnerGoldImageView, ArticleListingResult articleListingResult) {
        try {
            if ("1".equals(articleListingResult.getWinner()) || "true".equals(articleListingResult.getWinner())) {
                winnerGoldImageView.setImageResource(R.drawable.ic_trophy);
                winnerGoldImageView.setVisibility(View.VISIBLE);
            } else if ("1".equals(articleListingResult.getIsGold()) || "true"
                    .equals(articleListingResult.getIsGold())) {
                winnerGoldImageView.setImageResource(R.drawable.ic_star_yellow);
                winnerGoldImageView.setVisibility(View.VISIBLE);
            } else {
                winnerGoldImageView.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            winnerGoldImageView.setVisibility(View.GONE);
            FirebaseCrashlytics.getInstance().recordException(e);
            Log.d("MC4kException", Log.getStackTraceString(e));
        }
    }

    private void addShortStoryItem(final RecyclerView.ViewHolder holder, ImageView storyImage, TextView authorNameTV,
            TextView storyCommentCountTV, TextView storyRecommendationCountTV, ImageView likeIV,
            ArticleListingResult data, TextView followAuthorTextView, TextView storyAuthorTextView,
            ImageView shareStoryImageView, ImageView logoImageView) {

        authorNameTV.setText(data.getUserName());
        if (null == data.getCommentsCount()) {
            storyCommentCountTV.setText("0");
        } else {
            storyCommentCountTV.setText(data.getCommentsCount());
        }
        if (null == data.getLikesCount()) {
            storyRecommendationCountTV.setText("0");
        } else {
            storyRecommendationCountTV.setText(data.getLikesCount());
        }
        if (data.isLiked()) {
            likeIV.setImageDrawable(ContextCompat.getDrawable(mainContext, R.drawable.ic_recommended));
        } else {
            likeIV.setImageDrawable(ContextCompat.getDrawable(mainContext, R.drawable.ic_ss_like));
        }

        try {
            Picasso.get().load(data.getStoryImage().trim()).into(storyImage);
        } catch (Exception e) {
            storyImage.setImageResource(R.drawable.default_article);
        }

        if (data.getIsfollowing().equals("1")) {
            followAuthorTextView.setText(mainContext.getResources().getString(R.string.ad_following_author));
        } else {
            followAuthorTextView.setText(mainContext.getResources().getString(R.string.ad_follow_author));
        }
        try {
            Picasso.get().load(data.getStoryImage()).into(shareStoryImageView);
            storyAuthorTextView.setText(data.getUserName());
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            Log.d("MC4kException", Log.getStackTraceString(e));
        }
    }

    public class AdViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        AdViewHolder(View adView) {
            super(adView);
        }

        @Override
        public void onClick(View v) {
            if (getAdapterPosition() != RecyclerView.NO_POSITION) {
                recyclerViewClickListener.onRecyclerItemClick(v, getAdapterPosition());
            }
        }
    }

    public class FeedViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView txvArticleTitle;
        TextView txvAuthorName;
        ImageView articleImageView;
        ImageView videoIndicatorImageView;
        LinearLayout forYouInfoLL;
        TextView viewCountTextView;
        TextView commentCountTextView;
        TextView recommendCountTextView;
        ImageView bookmarkArticleImageView;
        ImageView watchLaterImageView;
        ImageView trophyImageView;

        FeedViewHolder(View view) {
            super(view);
            txvArticleTitle = (TextView) view.findViewById(R.id.txvArticleTitle);
            txvAuthorName = (TextView) view.findViewById(R.id.txvAuthorName);
            articleImageView = (ImageView) view.findViewById(R.id.articleImageView);
            videoIndicatorImageView = (ImageView) view.findViewById(R.id.videoIndicatorImageView);
            forYouInfoLL = (LinearLayout) view.findViewById(R.id.forYouInfoLL);
            viewCountTextView = (TextView) view.findViewById(R.id.viewCountTextView);
            commentCountTextView = (TextView) view.findViewById(R.id.commentCountTextView);
            recommendCountTextView = (TextView) view.findViewById(R.id.recommendCountTextView);
            bookmarkArticleImageView = (ImageView) view.findViewById(R.id.bookmarkArticleImageView);
            watchLaterImageView = (ImageView) view.findViewById(R.id.watchLaterImageView);
            trophyImageView = (ImageView) view.findViewById(R.id.trophyImageView);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (getAdapterPosition() != RecyclerView.NO_POSITION) {
                recyclerViewClickListener.onRecyclerItemClick(v, getAdapterPosition());
            }
        }
    }

    public class ShortStoriesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView authorNameTextView;
        TextView storyCommentCountTextView;
        LinearLayout storyRecommendationContainer;
        LinearLayout storyCommentContainer;
        TextView storyRecommendationCountTextView;
        TextView followAuthorTextView;
        ImageView storyImage;
        ImageView likeImageView;
        ImageView menuItem;
        ImageView facebookShareImageView;
        ImageView whatsappShareImageView;
        ImageView instagramShareImageView;
        ImageView genericShareImageView;
        StoryShareCardWidget storyShareCardWidget;
        ImageView shareStoryImageView;
        ImageView logoImageView;
        TextView storyAuthorTextView;

        private ShortStoriesViewHolder(View itemView) {
            super(itemView);
            authorNameTextView = (TextView) itemView.findViewById(R.id.authorNameTextView);
            storyRecommendationContainer = (LinearLayout) itemView.findViewById(R.id.storyRecommendationContainer);
            storyCommentContainer = (LinearLayout) itemView.findViewById(R.id.storyCommentContainer);
            storyCommentCountTextView = (TextView) itemView.findViewById(R.id.storyCommentCountTextView);
            storyRecommendationCountTextView = (TextView) itemView.findViewById(R.id.storyRecommendationCountTextView);
            storyImage = (ImageView) itemView.findViewById(R.id.storyImageView1);
            likeImageView = (ImageView) itemView.findViewById(R.id.likeImageView);
            facebookShareImageView = (ImageView) itemView.findViewById(R.id.facebookShareImageView);
            whatsappShareImageView = (ImageView) itemView.findViewById(R.id.whatsappShareImageView);
            instagramShareImageView = (ImageView) itemView.findViewById(R.id.instagramShareImageView);
            genericShareImageView = (ImageView) itemView.findViewById(R.id.genericShareImageView);
            menuItem = (ImageView) itemView.findViewById(R.id.menuItem);
            followAuthorTextView = itemView.findViewById(R.id.followAuthorTextView);
            storyShareCardWidget = (StoryShareCardWidget) itemView.findViewById(R.id.storyShareCardWidget);
            shareStoryImageView = (ImageView) storyShareCardWidget.findViewById(R.id.storyImageView);
            logoImageView = (ImageView) storyShareCardWidget.findViewById(R.id.logoImageView);
            storyAuthorTextView = (TextView) storyShareCardWidget.findViewById(R.id.storyAuthorTextView);

            whatsappShareImageView.setTag(itemView);
            storyRecommendationContainer.setOnClickListener(this);
            facebookShareImageView.setOnClickListener(this);
            whatsappShareImageView.setOnClickListener(this);
            instagramShareImageView.setOnClickListener(this);
            genericShareImageView.setOnClickListener(this);
            authorNameTextView.setOnClickListener(this);
            storyImage.setOnClickListener(this);
            itemView.setOnClickListener(this);
            menuItem.setOnClickListener(this);
            followAuthorTextView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (getAdapterPosition() != RecyclerView.NO_POSITION) {
                recyclerViewClickListener.onRecyclerItemClick(v, getAdapterPosition());
            }
        }
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        FrameLayout headerView;
        FrameLayout headerArticleView;
        TextView txvArticleTitle;
        TextView txvAuthorName;
        ImageView articleImageView;
        ImageView closeImageView;
        ImageView videoIndicatorImageView;
        LinearLayout forYouInfoLL;
        TextView viewCountTextView;
        TextView commentCountTextView;
        TextView recommendCountTextView;
        ImageView bookmarkArticleImageView;
        ImageView watchLaterImageView;

        HeaderViewHolder(View view) {
            super(view);
            headerView = (FrameLayout) view.findViewById(R.id.headerView);
            headerArticleView = (FrameLayout) view.findViewById(R.id.headerArticleView);

            txvArticleTitle = (TextView) view.findViewById(R.id.txvArticleTitle);
            txvAuthorName = (TextView) view.findViewById(R.id.txvAuthorName);
            articleImageView = (ImageView) view.findViewById(R.id.articleImageView);
            closeImageView = (ImageView) view.findViewById(R.id.closeImageView);
            videoIndicatorImageView = (ImageView) view.findViewById(R.id.videoIndicatorImageView);
            forYouInfoLL = (LinearLayout) view.findViewById(R.id.forYouInfoLL);
            viewCountTextView = (TextView) view.findViewById(R.id.viewCountTextView);
            commentCountTextView = (TextView) view.findViewById(R.id.commentCountTextView);
            recommendCountTextView = (TextView) view.findViewById(R.id.recommendCountTextView);
            bookmarkArticleImageView = (ImageView) view.findViewById(R.id.bookmarkArticleImageView);
            watchLaterImageView = (ImageView) view.findViewById(R.id.watchLaterImageView);

            if (SharedPrefUtils.getFollowTopicApproachChangeFlag(BaseApplication.getAppContext())) {
                closeImageView.setVisibility(View.VISIBLE);
            } else {
                closeImageView.setVisibility(View.GONE);
            }

            closeImageView.setOnClickListener(this);
            headerView.setOnClickListener(this);
            headerArticleView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (getAdapterPosition() != RecyclerView.NO_POSITION) {
                recyclerViewClickListener.onRecyclerItemClick(v, getAdapterPosition());
            }
        }
    }

    public class TorcaiAdsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        RelativeLayout adSlotContainer;
        WebView adSlotWebView;
        CardView cardView1;

        FrameLayout headerArticleView;
        TextView txvArticleTitle;
        TextView txvAuthorName;
        ImageView articleImageView;
        ImageView videoIndicatorImageView;
        LinearLayout forYouInfoLL;
        TextView viewCountTextView;
        TextView commentCountTextView;
        TextView recommendCountTextView;
        ImageView bookmarkArticleImageView;
        ImageView watchLaterImageView;
        TextView viewCountTextView1;
        TextView commentCountTextView1;
        TextView recommendCountTextView1;

        RelativeLayout storyHeaderView;
        TextView authorNameTextView;
        TextView storyCommentCountTextView;
        TextView followAuthorTextView;
        LinearLayout storyRecommendationContainer;
        LinearLayout storyCommentContainer;
        TextView storyRecommendationCountTextView;
        ImageView storyImage;
        ImageView likeImageView;
        ImageView menuItem;
        ImageView facebookShareImageView;
        ImageView whatsappShareImageView;
        ImageView instagramShareImageView;
        ImageView genericShareImageView;
        StoryShareCardWidget storyShareCardWidget;
        ImageView shareStoryImageView;
        ImageView logoImageView;
        TextView storyAuthorTextView;

        TorcaiAdsViewHolder(View view) {
            super(view);
            adSlotContainer = view.findViewById(R.id.adSlotContainer);
            adSlotWebView = view.findViewById(R.id.adSlotWebView);
            menuItem = view.findViewById(R.id.menuItem);
            followAuthorTextView = view.findViewById(R.id.followAuthorTextView);
            cardView1 = view.findViewById(R.id.cardView1);
            cardView1.setOnClickListener(this);
            menuItem.setOnClickListener(this);
            followAuthorTextView.setOnClickListener(this);

            viewCountTextView1 = (TextView) view.findViewById(R.id.viewCountTextView1);
            commentCountTextView1 = (TextView) view.findViewById(R.id.commentCountTextView1);
            recommendCountTextView1 = (TextView) view.findViewById(R.id.recommendCountTextView1);

            headerArticleView = (FrameLayout) view.findViewById(R.id.headerArticleView);
            txvArticleTitle = (TextView) view.findViewById(R.id.txvArticleTitle);
            txvAuthorName = (TextView) view.findViewById(R.id.txvAuthorName);
            articleImageView = (ImageView) view.findViewById(R.id.articleImageView);
            videoIndicatorImageView = (ImageView) view.findViewById(R.id.videoIndicatorImageView);
            forYouInfoLL = (LinearLayout) view.findViewById(R.id.forYouInfoLL);
            viewCountTextView = (TextView) view.findViewById(R.id.viewCountTextView);
            commentCountTextView = (TextView) view.findViewById(R.id.commentCountTextView);
            recommendCountTextView = (TextView) view.findViewById(R.id.recommendCountTextView);
            bookmarkArticleImageView = (ImageView) view.findViewById(R.id.bookmarkArticleImageView);
            watchLaterImageView = (ImageView) view.findViewById(R.id.watchLaterImageView);

            storyHeaderView = (RelativeLayout) view.findViewById(R.id.storyHeaderView);
            authorNameTextView = (TextView) view.findViewById(R.id.authorNameTextView);
            storyRecommendationContainer = (LinearLayout) view.findViewById(R.id.storyRecommendationContainer);
            storyCommentContainer = (LinearLayout) view.findViewById(R.id.storyCommentContainer);
            storyCommentCountTextView = (TextView) view.findViewById(R.id.storyCommentCountTextView);
            storyRecommendationCountTextView = (TextView) view.findViewById(R.id.storyRecommendationCountTextView);
            storyImage = (ImageView) view.findViewById(R.id.storyImageView1);
            likeImageView = (ImageView) view.findViewById(R.id.likeImageView);
            facebookShareImageView = (ImageView) view.findViewById(R.id.facebookShareImageView);
            whatsappShareImageView = (ImageView) view.findViewById(R.id.whatsappShareImageView);
            instagramShareImageView = (ImageView) view.findViewById(R.id.instagramShareImageView);
            genericShareImageView = (ImageView) view.findViewById(R.id.genericShareImageView);
            adSlotWebView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
            storyShareCardWidget = (StoryShareCardWidget) itemView.findViewById(R.id.storyShareCardWidget);
            shareStoryImageView = (ImageView) storyShareCardWidget.findViewById(R.id.storyImageView);
            logoImageView = (ImageView) storyShareCardWidget.findViewById(R.id.logoImageView);
            storyAuthorTextView = (TextView) storyShareCardWidget.findViewById(R.id.storyAuthorTextView);

            view.setOnClickListener(this);
            headerArticleView.setOnClickListener(this);
            whatsappShareImageView.setTag(view);
            storyHeaderView.setOnClickListener(this);
            storyRecommendationContainer.setOnClickListener(this);
            facebookShareImageView.setOnClickListener(this);
            whatsappShareImageView.setOnClickListener(this);
            instagramShareImageView.setOnClickListener(this);
            genericShareImageView.setOnClickListener(this);
            authorNameTextView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (getAdapterPosition() != RecyclerView.NO_POSITION) {
                recyclerViewClickListener.onRecyclerItemClick(v, getAdapterPosition());
            }
        }
    }

    public class CampaignCarouselViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        RelativeLayout adSlotContainer;
        WebView adSlotWebView;
        RelativeLayout relativeLayoutContainer;
        LinearLayout videoCarouselContainer;
        ImageView campaignHeader;
        CircularImageView brandImg;
        TextView brandName;
        TextView campaignName;
        TextView campaignStatus;
        CardView cardView1;

        FrameLayout headerArticleView;
        TextView txvArticleTitle;
        TextView txvAuthorName;
        ImageView articleImageView;
        ImageView videoIndicatorImageView;
        LinearLayout forYouInfoLL;
        TextView viewCountTextView;
        TextView commentCountTextView;
        TextView recommendCountTextView;
        ImageView bookmarkArticleImageView;
        ImageView watchLaterImageView;
        TextView viewCountTextView1;
        TextView commentCountTextView1;
        TextView recommendCountTextView1;

        RelativeLayout storyHeaderView;
        TextView authorNameTextView;
        TextView storyCommentCountTextView;
        TextView followAuthorTextView;
        LinearLayout storyRecommendationContainer;
        LinearLayout storyCommentContainer;
        TextView storyRecommendationCountTextView;
        ImageView storyImage;
        ImageView likeImageView;
        ImageView menuItem;
        ImageView facebookShareImageView;
        ImageView whatsappShareImageView;
        ImageView instagramShareImageView;
        ImageView genericShareImageView;
        StoryShareCardWidget storyShareCardWidget;
        ImageView shareStoryImageView;
        ImageView logoImageView;
        TextView storyAuthorTextView;

        CampaignCarouselViewHolder(View view) {
            super(view);
            adSlotContainer = view.findViewById(R.id.adSlotContainer);
            adSlotWebView = view.findViewById(R.id.adSlotWebView);
            relativeLayoutContainer = view.findViewById(R.id.relativeLayoutContainer);
            videoCarouselContainer = view.findViewById(R.id.linearLayoutVideoCrousalConatiner);
            campaignHeader = view.findViewById(R.id.campaign_header);
            brandImg = view.findViewById(R.id.brand_img);
            brandName = view.findViewById(R.id.brand_name);
            campaignName = view.findViewById(R.id.campaign_name);
            campaignStatus = view.findViewById(R.id.submission_status);
            menuItem = view.findViewById(R.id.menuItem);
            followAuthorTextView = view.findViewById(R.id.followAuthorTextView);
            cardView1 = view.findViewById(R.id.cardView1);
            cardView1.setOnClickListener(this);
            menuItem.setOnClickListener(this);
            followAuthorTextView.setOnClickListener(this);

            viewCountTextView1 = (TextView) view.findViewById(R.id.viewCountTextView1);
            commentCountTextView1 = (TextView) view.findViewById(R.id.commentCountTextView1);
            recommendCountTextView1 = (TextView) view.findViewById(R.id.recommendCountTextView1);

            headerArticleView = (FrameLayout) view.findViewById(R.id.headerArticleView);
            txvArticleTitle = (TextView) view.findViewById(R.id.txvArticleTitle);
            txvAuthorName = (TextView) view.findViewById(R.id.txvAuthorName);
            articleImageView = (ImageView) view.findViewById(R.id.articleImageView);
            videoIndicatorImageView = (ImageView) view.findViewById(R.id.videoIndicatorImageView);
            forYouInfoLL = (LinearLayout) view.findViewById(R.id.forYouInfoLL);
            viewCountTextView = (TextView) view.findViewById(R.id.viewCountTextView);
            commentCountTextView = (TextView) view.findViewById(R.id.commentCountTextView);
            recommendCountTextView = (TextView) view.findViewById(R.id.recommendCountTextView);
            bookmarkArticleImageView = (ImageView) view.findViewById(R.id.bookmarkArticleImageView);
            watchLaterImageView = (ImageView) view.findViewById(R.id.watchLaterImageView);

            storyHeaderView = (RelativeLayout) view.findViewById(R.id.storyHeaderView);
            authorNameTextView = (TextView) view.findViewById(R.id.authorNameTextView);
            storyRecommendationContainer = (LinearLayout) view.findViewById(R.id.storyRecommendationContainer);
            storyCommentContainer = (LinearLayout) view.findViewById(R.id.storyCommentContainer);
            storyCommentCountTextView = (TextView) view.findViewById(R.id.storyCommentCountTextView);
            storyRecommendationCountTextView = (TextView) view.findViewById(R.id.storyRecommendationCountTextView);
            storyImage = (ImageView) view.findViewById(R.id.storyImageView1);
            likeImageView = (ImageView) view.findViewById(R.id.likeImageView);
            facebookShareImageView = (ImageView) view.findViewById(R.id.facebookShareImageView);
            whatsappShareImageView = (ImageView) view.findViewById(R.id.whatsappShareImageView);
            instagramShareImageView = (ImageView) view.findViewById(R.id.instagramShareImageView);
            genericShareImageView = (ImageView) view.findViewById(R.id.genericShareImageView);
            adSlotWebView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
            storyShareCardWidget = (StoryShareCardWidget) itemView.findViewById(R.id.storyShareCardWidget);
            shareStoryImageView = (ImageView) storyShareCardWidget.findViewById(R.id.storyImageView);
            logoImageView = (ImageView) storyShareCardWidget.findViewById(R.id.logoImageView);
            storyAuthorTextView = (TextView) storyShareCardWidget.findViewById(R.id.storyAuthorTextView);

            view.setOnClickListener(this);
            headerArticleView.setOnClickListener(this);
            whatsappShareImageView.setTag(view);
            storyHeaderView.setOnClickListener(this);
            storyRecommendationContainer.setOnClickListener(this);
            facebookShareImageView.setOnClickListener(this);
            whatsappShareImageView.setOnClickListener(this);
            instagramShareImageView.setOnClickListener(this);
            genericShareImageView.setOnClickListener(this);
            authorNameTextView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (getAdapterPosition() != RecyclerView.NO_POSITION) {
                recyclerViewClickListener.onRecyclerItemClick(v, getAdapterPosition());
            }
        }
    }


    public class VideoCarouselViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        FrameLayout videoContainerFL1;
        TextView txvArticleTitle1;
        TextView txvAuthorName1;
        ImageView articleImageView1;

        FrameLayout headerArticleView;
        TextView txvArticleTitle;
        TextView txvAuthorName;
        ImageView articleImageView;
        ImageView videoIndicatorImageView;
        LinearLayout forYouInfoLL;
        TextView viewCountTextView;
        TextView commentCountTextView;
        TextView recommendCountTextView;
        ImageView bookmarkArticleImageView;
        ImageView watchLaterImageView;
        TextView viewCountTextView1;
        TextView commentCountTextView1;
        TextView recommendCountTextView1;

        RelativeLayout storyHeaderView;
        TextView authorNameTextView;
        TextView followAuthorTextView;
        TextView storyCommentCountTextView;
        LinearLayout storyRecommendationContainer;
        LinearLayout storyCommentContainer;
        TextView storyRecommendationCountTextView;
        ImageView storyImage;
        ImageView likeImageView;
        ImageView menuItem;
        ImageView facebookShareImageView;
        ImageView whatsappShareImageView;
        ImageView instagramShareImageView;
        ImageView genericShareImageView;
        StoryShareCardWidget storyShareCardWidget;
        ImageView shareStoryImageView;
        ImageView logoImageView;
        TextView storyAuthorTextView;

        VideoCarouselViewHolder(View view) {
            super(view);
            videoContainerFL1 = (FrameLayout) view.findViewById(R.id.videoContainerFL1);
            txvArticleTitle1 = (TextView) view.findViewById(R.id.txvArticleTitle1);
            txvAuthorName1 = (TextView) view.findViewById(R.id.txvAuthorName1);
            articleImageView1 = (ImageView) view.findViewById(R.id.articleImageView1);
            viewCountTextView1 = (TextView) view.findViewById(R.id.viewCountTextView1);
            commentCountTextView1 = (TextView) view.findViewById(R.id.commentCountTextView1);
            recommendCountTextView1 = (TextView) view.findViewById(R.id.recommendCountTextView1);

            headerArticleView = (FrameLayout) view.findViewById(R.id.headerArticleView);
            txvArticleTitle = (TextView) view.findViewById(R.id.txvArticleTitle);
            txvAuthorName = (TextView) view.findViewById(R.id.txvAuthorName);
            articleImageView = (ImageView) view.findViewById(R.id.articleImageView);
            videoIndicatorImageView = (ImageView) view.findViewById(R.id.videoIndicatorImageView);
            forYouInfoLL = (LinearLayout) view.findViewById(R.id.forYouInfoLL);
            viewCountTextView = (TextView) view.findViewById(R.id.viewCountTextView);
            commentCountTextView = (TextView) view.findViewById(R.id.commentCountTextView);
            recommendCountTextView = (TextView) view.findViewById(R.id.recommendCountTextView);
            bookmarkArticleImageView = (ImageView) view.findViewById(R.id.bookmarkArticleImageView);
            watchLaterImageView = (ImageView) view.findViewById(R.id.watchLaterImageView);

            storyHeaderView = (RelativeLayout) view.findViewById(R.id.storyHeaderView);
            authorNameTextView = (TextView) view.findViewById(R.id.authorNameTextView);
            storyRecommendationContainer = (LinearLayout) view.findViewById(R.id.storyRecommendationContainer);
            storyCommentContainer = (LinearLayout) view.findViewById(R.id.storyCommentContainer);
            storyCommentCountTextView = (TextView) view.findViewById(R.id.storyCommentCountTextView);
            storyRecommendationCountTextView = (TextView) view.findViewById(R.id.storyRecommendationCountTextView);
            storyImage = (ImageView) view.findViewById(R.id.storyImageView1);
            likeImageView = (ImageView) view.findViewById(R.id.likeImageView);
            facebookShareImageView = (ImageView) view.findViewById(R.id.facebookShareImageView);
            whatsappShareImageView = (ImageView) view.findViewById(R.id.whatsappShareImageView);
            instagramShareImageView = (ImageView) view.findViewById(R.id.instagramShareImageView);
            genericShareImageView = (ImageView) view.findViewById(R.id.genericShareImageView);
            menuItem = view.findViewById(R.id.menuItem);
            followAuthorTextView = view.findViewById(R.id.followAuthorTextView);
            storyShareCardWidget = (StoryShareCardWidget) itemView.findViewById(R.id.storyShareCardWidget);
            shareStoryImageView = (ImageView) storyShareCardWidget.findViewById(R.id.storyImageView);
            logoImageView = (ImageView) storyShareCardWidget.findViewById(R.id.logoImageView);
            storyAuthorTextView = (TextView) storyShareCardWidget.findViewById(R.id.storyAuthorTextView);

            whatsappShareImageView.setTag(view);
            videoContainerFL1.setOnClickListener(this);
            headerArticleView.setOnClickListener(this);
            storyHeaderView.setOnClickListener(this);
            storyRecommendationContainer.setOnClickListener(this);
            facebookShareImageView.setOnClickListener(this);
            whatsappShareImageView.setOnClickListener(this);
            instagramShareImageView.setOnClickListener(this);
            genericShareImageView.setOnClickListener(this);
            authorNameTextView.setOnClickListener(this);
            storyImage.setOnClickListener(this);
            menuItem.setOnClickListener(this);
            followAuthorTextView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            if (getAdapterPosition() != RecyclerView.NO_POSITION) {
                recyclerViewClickListener.onRecyclerItemClick(v, getAdapterPosition());
            }
        }
    }

    public interface RecyclerViewClickListener {

        void onRecyclerItemClick(View view, int position);
    }

    private void addRemoveBookmark(int position, RecyclerView.ViewHolder holder) {
        isRequestRunning = true;
        if ("0".equals(articleDataModelsNew.get(position).getIs_bookmark())) {
            articleDataModelsNew.get(position).setIs_bookmark("1");
            notifyDataSetChanged();
            ArticleDetailRequest articleDetailRequest = new ArticleDetailRequest();
            articleDetailRequest.setArticleId(articleDataModelsNew.get(position).getId());
            String jsonString = new Gson().toJson(articleDetailRequest);
            new MainArticleRecyclerViewAdapter.AddRemoveBookmarkAsyncTask(holder, "bookmarkArticle", position)
                    .execute(jsonString, "bookmarkArticle");
        } else {
            articleDataModelsNew.get(position).setIs_bookmark("0");
            notifyDataSetChanged();
            DeleteBookmarkRequest deleteBookmarkRequest = new DeleteBookmarkRequest();
            deleteBookmarkRequest.setId(articleDataModelsNew.get(position).getBookmarkId());
            String jsonString = new Gson().toJson(deleteBookmarkRequest);
            new MainArticleRecyclerViewAdapter.AddRemoveBookmarkAsyncTask(holder, "unbookmarkArticle", position)
                    .execute(jsonString, "unbookmarkArticle");
        }
    }

    private void addRemoveWatchLater(int position, RecyclerView.ViewHolder holder) {
        if (articleDataModelsNew.get(position).getListingWatchLaterStatus() == 0) {
            ArticleDetailRequest articleDetailRequest = new ArticleDetailRequest();
            articleDetailRequest.setArticleId(articleDataModelsNew.get(position).getId());
            String jsonString = new Gson().toJson(articleDetailRequest);
            new MainArticleRecyclerViewAdapter.AddRemoveBookmarkAsyncTask(holder, "bookmarkVideo", position)
                    .execute(jsonString, "bookmarkVideo");
        } else {
            DeleteBookmarkRequest deleteBookmarkRequest = new DeleteBookmarkRequest();
            deleteBookmarkRequest.setId(articleDataModelsNew.get(position).getBookmarkId());
            String jsonString = new Gson().toJson(deleteBookmarkRequest);
            new MainArticleRecyclerViewAdapter.AddRemoveBookmarkAsyncTask(holder, "unbookmarkVideo", position)
                    .execute(jsonString, "unbookmarkVideo");
        }
    }

    private class AddRemoveBookmarkAsyncTask extends AsyncTask<String, String, String> {

        RecyclerView.ViewHolder viewHolder;
        String type;
        int pos;

        public AddRemoveBookmarkAsyncTask(RecyclerView.ViewHolder viewHolder, String type, int position) {
            this.viewHolder = viewHolder;
            this.type = type;
            pos = position;
        }

        @Override
        protected String doInBackground(String... strings) {
            String jsonResponse;
            String jsonData = strings[0];

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            try {
                URL url;
                if ("bookmarkArticle".equals(strings[1])) {
                    url = new URL(AppConstants.BASE_URL + "v1/users/bookmark/");
                } else if ("unbookmarkArticle".equals(strings[1])) {
                    url = new URL(AppConstants.BASE_URL + "v1/users/deleteBookmark/");
                } else if ("bookmarkVideo".equals(strings[1])) {
                    url = new URL(AppConstants.BASE_URL + "v1/users/bookmarkVideo/");
                } else {
                    url = new URL(AppConstants.BASE_URL + "v1/users/deleteBookmarkVideo/");
                }

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Accept", "application/json");
                urlConnection.addRequestProperty("id", SharedPrefUtils.getUserDetailModel(mainContext).getDynamoId());
                urlConnection
                        .addRequestProperty("mc4kToken",
                                SharedPrefUtils.getUserDetailModel(mainContext).getMc4kToken());

                Writer writer = new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream(), "UTF-8"));
                writer.write(jsonData);
                writer.close();
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String inputLine;
                while ((inputLine = reader.readLine()) != null) {
                    buffer.append(inputLine + "\n");
                }
                if (buffer.length() == 0) {
                    return null;
                }
                jsonResponse = buffer.toString();
                Log.i("RESPONSE " + type, jsonResponse);
                return jsonResponse;
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("TAAGG", "Error closing stream", e);
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            isRequestRunning = false;
            if (result == null) {
                resetFollowUnfollowStatus();
                return;
            }
            try {
                AddBookmarkResponse responseData = new Gson().fromJson(result, AddBookmarkResponse.class);
                if (responseData.getCode() == 200) {
                    for (int i = 0; i < articleDataModelsNew.size(); i++) {
                        if (articleDataModelsNew.get(i).getId()
                                .equals(responseData.getData().getResult().getArticleId())) {
                            if ("bookmarkArticle".equals(type)) {
                                articleDataModelsNew.get(i).setIs_bookmark("1");
                                articleDataModelsNew.get(i)
                                        .setBookmarkId(responseData.getData().getResult().getBookmarkId());
                                if (viewHolder instanceof FeedViewHolder) {
                                    ((FeedViewHolder) viewHolder).bookmarkArticleImageView.setImageDrawable(
                                            ContextCompat.getDrawable(mainContext, R.drawable.ic_bookmarked));
                                } else if (viewHolder instanceof HeaderViewHolder) {
                                    ((HeaderViewHolder) viewHolder).bookmarkArticleImageView.setImageDrawable(
                                            ContextCompat.getDrawable(mainContext, R.drawable.ic_bookmarked));
                                }
                            } else if ("unbookmarkArticle".equals(type)) {
                                articleDataModelsNew.get(i).setIs_bookmark("0");
                                articleDataModelsNew.get(i)
                                        .setBookmarkId(responseData.getData().getResult().getBookmarkId());
                                if (viewHolder instanceof FeedViewHolder) {
                                    ((FeedViewHolder) viewHolder).bookmarkArticleImageView.setImageDrawable(
                                            ContextCompat.getDrawable(mainContext, R.drawable.ic_bookmark));
                                } else if (viewHolder instanceof HeaderViewHolder) {
                                    ((HeaderViewHolder) viewHolder).bookmarkArticleImageView.setImageDrawable(
                                            ContextCompat.getDrawable(mainContext, R.drawable.ic_bookmark));
                                }
                            } else if ("bookmarkVideo".equals(type)) {
                                articleDataModelsNew.get(i).setListingWatchLaterStatus(1);
                                articleDataModelsNew.get(i)
                                        .setBookmarkId(responseData.getData().getResult().getBookmarkId());
                                if (viewHolder instanceof FeedViewHolder) {
                                    ((FeedViewHolder) viewHolder).watchLaterImageView.setImageDrawable(
                                            ContextCompat.getDrawable(mainContext, R.drawable.ic_watch_added));
                                } else if (viewHolder instanceof HeaderViewHolder) {
                                    ((HeaderViewHolder) viewHolder).watchLaterImageView.setImageDrawable(
                                            ContextCompat.getDrawable(mainContext, R.drawable.ic_watch_added));
                                }
                            } else if ("unbookmarkVideo".equals(type)) {
                                articleDataModelsNew.get(i).setListingWatchLaterStatus(0);
                                articleDataModelsNew.get(i)
                                        .setBookmarkId(responseData.getData().getResult().getBookmarkId());
                                if (viewHolder instanceof FeedViewHolder) {
                                    ((FeedViewHolder) viewHolder).watchLaterImageView
                                            .setImageDrawable(
                                                    ContextCompat.getDrawable(mainContext, R.drawable.ic_watch));
                                } else if (viewHolder instanceof HeaderViewHolder) {
                                    ((HeaderViewHolder) viewHolder).watchLaterImageView
                                            .setImageDrawable(
                                                    ContextCompat.getDrawable(mainContext, R.drawable.ic_watch));
                                }
                            }
                        }
                        notifyDataSetChanged();
                    }
                } else {
                    resetFollowUnfollowStatus();
                }
            } catch (Exception e) {
                resetFollowUnfollowStatus();
            }
        }

        void resetFollowUnfollowStatus() {
            switch (type) {
                case "bookmark":
                    if (viewHolder instanceof FeedViewHolder) {
                        ((FeedViewHolder) viewHolder).bookmarkArticleImageView
                                .setImageDrawable(ContextCompat.getDrawable(mainContext, R.drawable.ic_bookmark));
                    } else if (viewHolder instanceof HeaderViewHolder) {
                        ((HeaderViewHolder) viewHolder).bookmarkArticleImageView
                                .setImageDrawable(ContextCompat.getDrawable(mainContext, R.drawable.ic_bookmark));
                    } else if (viewHolder instanceof VideoCarouselViewHolder) {
                        ((VideoCarouselViewHolder) viewHolder).bookmarkArticleImageView
                                .setImageDrawable(ContextCompat.getDrawable(mainContext, R.drawable.ic_bookmark));
                    }
                    break;
                case "unbookmarkArticle":
                    if (viewHolder instanceof FeedViewHolder) {
                        ((FeedViewHolder) viewHolder).bookmarkArticleImageView
                                .setImageDrawable(ContextCompat.getDrawable(mainContext, R.drawable.ic_bookmarked));
                    } else if (viewHolder instanceof HeaderViewHolder) {
                        ((HeaderViewHolder) viewHolder).bookmarkArticleImageView
                                .setImageDrawable(ContextCompat.getDrawable(mainContext, R.drawable.ic_bookmarked));
                    } else if (viewHolder instanceof VideoCarouselViewHolder) {
                        ((VideoCarouselViewHolder) viewHolder).bookmarkArticleImageView
                                .setImageDrawable(ContextCompat.getDrawable(mainContext, R.drawable.ic_bookmark));
                    }
                    break;
                case "bookmarkVideo":
                    if (viewHolder instanceof FeedViewHolder) {
                        ((FeedViewHolder) viewHolder).watchLaterImageView
                                .setImageDrawable(ContextCompat.getDrawable(mainContext, R.drawable.ic_watch));
                    } else if (viewHolder instanceof HeaderViewHolder) {
                        ((HeaderViewHolder) viewHolder).watchLaterImageView
                                .setImageDrawable(ContextCompat.getDrawable(mainContext, R.drawable.ic_watch));
                    } else if (viewHolder instanceof VideoCarouselViewHolder) {
                        ((VideoCarouselViewHolder) viewHolder).bookmarkArticleImageView
                                .setImageDrawable(ContextCompat.getDrawable(mainContext, R.drawable.ic_bookmark));
                    }
                    break;
                case "unbookmarkVideo":
                    if (viewHolder instanceof FeedViewHolder) {
                        ((FeedViewHolder) viewHolder).watchLaterImageView
                                .setImageDrawable(ContextCompat.getDrawable(mainContext, R.drawable.ic_watch_added));
                    } else if (viewHolder instanceof HeaderViewHolder) {
                        ((HeaderViewHolder) viewHolder).watchLaterImageView
                                .setImageDrawable(ContextCompat.getDrawable(mainContext, R.drawable.ic_watch_added));
                    } else if (viewHolder instanceof VideoCarouselViewHolder) {
                        ((VideoCarouselViewHolder) viewHolder).bookmarkArticleImageView
                                .setImageDrawable(ContextCompat.getDrawable(mainContext, R.drawable.ic_bookmark));
                    }
                    break;
                default:
                    break;
            }
        }
    }


    private class LoadVideoCarouselAsyncTask extends AsyncTask<String, String, String> {

        VideoCarouselViewHolder viewHolder;
        int pos;

        public LoadVideoCarouselAsyncTask(VideoCarouselViewHolder viewHolder, int position) {
            this.viewHolder = viewHolder;
            pos = position;
        }

        @Override
        protected String doInBackground(String... strings) {

            String jsonResponse;

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            try {
                int start = (((pos + 3) / 6) * 5) - 5;
                int end = start + 5;
                URL url = new URL("http://api.momspresso.com/" + "v2/videos/?" + "start=" + start + "&end=" + end
                        + "&sort=0&type=3");
                Log.d("VideoCarouselViewHolder",
                        AppConstants.BASE_URL + "v1/videos/?" + "start=" + start + "&end=" + end + "&sort=0&type=3");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Accept", "application/json");
                urlConnection.setRequestProperty("Accept-Language", Locale.getDefault().getLanguage());
                urlConnection.addRequestProperty("id", SharedPrefUtils.getUserDetailModel(mainContext).getDynamoId());
                urlConnection
                        .addRequestProperty("mc4kToken",
                                SharedPrefUtils.getUserDetailModel(mainContext).getMc4kToken());
                InputStream inputStream = urlConnection.getInputStream();
                InputStreamReader isReader = new InputStreamReader(inputStream);
                reader = new BufferedReader(isReader);
                String line = reader.readLine();
                StringBuffer readTextBuf = new StringBuffer();
                while (line != null) {
                    readTextBuf.append(line);
                    line = reader.readLine();
                }
                jsonResponse = readTextBuf.toString();
                Log.d("VideoCarouselViewHolder", "backgroud finish = " + jsonResponse);
                return jsonResponse;
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("TAAGG", "Error closing stream", e);
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result == null) {
                return;
            }
            Log.d("VideoCarouselViewHolder", "Response = " + result);
            try {
                VlogsListingResponse responseData = gson.fromJson(result, VlogsListingResponse.class);
                if (articleDataModelsNew.size() > 0) {
                    if (responseData != null) {
                        Log.d("VideoCarouselViewHolder",
                                "pos=" + pos + "  SIZE=" + responseData.getData().get(0).getResult().size());
                        articleDataModelsNew.get(pos).setCarouselVideoList(responseData.getData().get(0).getResult());
                        Log.d("VideoCarouselViewHolder", "ASYNC");
                        populateCarouselVideos(viewHolder, responseData.getData().get(0).getResult());
                        articleDataModelsNew.get(pos).setCarouselRequestRunning(false);
                        articleDataModelsNew.get(pos).setResponseReceived(true);
                    } else {
                        VlogsListingResponse responseData1 = gson.fromJson(result, VlogsListingResponse.class);
                        if (responseData != null) {
                            Log.d("VideoCarouselViewHolder",
                                    "pos=" + pos + "  SIZE=" + responseData1.getData().get(0).getResult().size());
                            articleDataModelsNew.get(pos)
                                    .setCarouselVideoList(responseData1.getData().get(0).getResult());
                            Log.d("VideoCarouselViewHolder", "ASYNC");
                            populateCarouselVideos(viewHolder, responseData1.getData().get(0).getResult());
                            articleDataModelsNew.get(pos).setCarouselRequestRunning(false);
                            articleDataModelsNew.get(pos).setResponseReceived(true);
                        }
                    }
                }
            } catch (JsonSyntaxException jse) {
                articleDataModelsNew.get(pos).setCarouselVideoList(new ArrayList<>());
                populateCarouselVideos(viewHolder, new ArrayList<>());
                articleDataModelsNew.get(pos).setCarouselRequestRunning(false);
                articleDataModelsNew.get(pos).setResponseReceived(true);
            }
        }
    }

    private void populateCarouselVideos(VideoCarouselViewHolder viewHolder,
            ArrayList<VlogsListingAndDetailResult> result) {
        ArrayList<VlogsListingAndDetailResult> videoList = result;
        if (videoList == null || videoList.isEmpty()) {
            viewHolder.videoContainerFL1.setVisibility(View.GONE);
            return;
        } else {
            viewHolder.videoContainerFL1.setVisibility(View.VISIBLE);
        }
        if (videoList.size() > 0) {
            updateCarouselView(viewHolder.txvArticleTitle1, viewHolder.articleImageView1, viewHolder.txvAuthorName1,
                    viewHolder.viewCountTextView1, viewHolder.commentCountTextView1, viewHolder.recommendCountTextView1,
                    videoList.get(0));
        }
    }

    private void updateCarouselView(TextView textView, ImageView imageView, TextView authorTextView,
            TextView viewCountTextView1,
            TextView commentCountTextView1, TextView recommendCountTextView1, VlogsListingAndDetailResult data) {
        textView.setText(data.getTitle());
        if (null == data.getView_count() || "0".equals(data.getView_count())) {
            viewCountTextView1.setVisibility(View.GONE);
        } else {
            viewCountTextView1.setVisibility(View.VISIBLE);
            try {
                viewCountTextView1.setText(AppUtils.withSuffix(Long.parseLong(data.getView_count())));
            } catch (Exception e) {
                viewCountTextView1.setText(data.getView_count());
            }
        }
        if (null == data.getComment_count() || "0".equals(data.getComment_count())) {
            commentCountTextView1.setVisibility(View.GONE);
        } else {
            commentCountTextView1.setVisibility(View.VISIBLE);
            try {
                commentCountTextView1.setText(AppUtils.withSuffix(Long.parseLong(data.getComment_count())));
            } catch (Exception e) {
                commentCountTextView1.setText(data.getComment_count());
            }
        }
        if (null == data.getLike_count() || "0".equals(data.getLike_count())) {
            recommendCountTextView1.setVisibility(View.GONE);
        } else {
            recommendCountTextView1.setVisibility(View.VISIBLE);
            try {
                recommendCountTextView1.setText(AppUtils.withSuffix(Long.parseLong(data.getLike_count())));
            } catch (Exception e) {
                recommendCountTextView1.setText(data.getLike_count());
            }
        }
        try {
            String userName = data.getAuthor().getFirstName() + " " + data.getAuthor().getLastName();
            if (StringUtils.isNullOrEmpty(userName) || userName.trim().equalsIgnoreCase("")) {
                authorTextView.setText("NA");
            } else {
                authorTextView.setText(userName);
            }
        } catch (Exception e) {
            authorTextView.setText("NA");
        }
        try {
            Picasso.get().load(data.getThumbnail())
                    .placeholder(R.drawable.default_article).error(R.drawable.default_article).into(imageView);
        } catch (Exception e) {
            imageView.setImageResource(R.drawable.default_article);
        }
    }
}
