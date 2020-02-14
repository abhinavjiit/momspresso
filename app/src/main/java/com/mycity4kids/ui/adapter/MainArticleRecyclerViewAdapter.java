package com.mycity4kids.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.mycity4kids.utils.StringUtils;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.mycity4kids.BuildConfig;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.campaignmodels.CampaignDataListResult;
import com.mycity4kids.models.request.ArticleDetailRequest;
import com.mycity4kids.models.request.DeleteBookmarkRequest;
import com.mycity4kids.models.response.AddBookmarkResponse;
import com.mycity4kids.models.response.ArticleListingResult;
import com.mycity4kids.models.response.GroupsMembershipResponse;
import com.mycity4kids.models.response.VlogsListingAndDetailResult;
import com.mycity4kids.models.response.VlogsListingResponse;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.ui.GroupMembershipStatus;
import com.mycity4kids.ui.activity.DashboardActivity;
import com.mycity4kids.ui.activity.FilteredTopicsArticleListingActivity;
import com.mycity4kids.ui.activity.GroupDetailsActivity;
import com.mycity4kids.ui.activity.GroupsSummaryActivity;
import com.mycity4kids.ui.fragment.GroupsViewFragment;
import com.mycity4kids.utils.AppUtils;
import com.mycity4kids.utils.ArrayAdapterFactory;
import com.mycity4kids.widget.StoryShareCardWidget;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

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

public class MainArticleRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements GroupMembershipStatus.IMembershipStatus {

    private MixpanelAPI mixpanel;
    private static int ARTICLE = 0;
    private static int AD = 1;
    private static int HEADER = 2;
    private static int STORY = 3;
    private static int GROUPS = 4;
    private static int VIDEOS = 5;
    private static int CAROUSEL = 6;
    private final Context mContext;
    private final LayoutInflater mInflator;
    private ArrayList<ArticleListingResult> articleDataModelsNew;
    private ArrayList<CampaignDataListResult> campaignListDataModels;
    private RecyclerViewClickListener mListener;
    private boolean topicHeaderVisibilityFlag;
    private boolean isRequestRunning;
    private String heading, subHeading, gpImageUrl;
    private int groupId;
    private String screenName;
    private Gson gson;
    private boolean showVideoFlag;
    private String htmlContent = "";
    private String dataType = "";


    public MainArticleRecyclerViewAdapter(Context pContext, RecyclerViewClickListener listener, boolean topicHeaderVisibilityFlag, String screenName, boolean showVideoFlag) {
        mContext = pContext;
        mInflator = (LayoutInflater) pContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        gson = new GsonBuilder().registerTypeAdapterFactory(new ArrayAdapterFactory()).create();
        mListener = listener;
        this.topicHeaderVisibilityFlag = topicHeaderVisibilityFlag;
        mixpanel = MixpanelAPI.getInstance(BaseApplication.getAppContext(), AppConstants.MIX_PANEL_TOKEN);
        this.screenName = screenName;
        this.showVideoFlag = showVideoFlag;
    }

    public void hideFollowTopicHeader() {
        topicHeaderVisibilityFlag = false;
    }

    public void setNewListData(ArrayList<ArticleListingResult> mParentingLists_new) {
        articleDataModelsNew = mParentingLists_new;
    }

    public void setCampaignOrAdSlotData(String dataType, ArrayList<CampaignDataListResult> mCampaignList, String adSlotHtml) {
        this.dataType = dataType;
        campaignListDataModels = mCampaignList;
        this.htmlContent = adSlotHtml;
    }

    public void setGroupInfo(int groupId, String heading, String subHeading, String gpImageUrl) {
        if (StringUtils.isNullOrEmpty(heading)) {
            this.heading = BaseApplication.getAppContext().getString(R.string.groups_join_support_gp);
        } else {
            this.heading = heading;
        }
        if (StringUtils.isNullOrEmpty(subHeading)) {
            this.subHeading = BaseApplication.getAppContext().getString(R.string.groups_not_alone);
        } else {
            this.subHeading = subHeading;
        }
        this.gpImageUrl = gpImageUrl;
        this.groupId = groupId;
    }

    @Override
    public int getItemCount() {
        return articleDataModelsNew == null ? 0 : articleDataModelsNew.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (topicHeaderVisibilityFlag && position == 0) {
            return HEADER;
        } else if (position != 0 && position % 3 == 0) {
            if (showVideoFlag) {
                if (position == 3) {
                    return CAROUSEL;
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
            View v0 = mInflator.inflate(R.layout.facebook_ad_list_item, parent, false);
            return new AdViewHolder(v0);
        } else if (viewType == HEADER) {
            View v0 = mInflator.inflate(R.layout.trending_list_header_item, parent, false);
            return new HeaderViewHolder(v0);
        } else if (viewType == GROUPS) {
            View v0 = mInflator.inflate(R.layout.join_group_article_item, parent, false);
            return new JoinGroupViewHolder(v0);
        } else if (viewType == CAROUSEL) {
            View v0 = mInflator.inflate(R.layout.campaign_carousel_container, parent, false);
            return new CampaignCarouselViewHolder(v0);
        } else if (viewType == VIDEOS) {
            View v0 = mInflator.inflate(R.layout.video_carousel_container, parent, false);
            return new VideoCarouselViewHolder(v0);
        } else if (viewType == ARTICLE) {
            View v0 = mInflator.inflate(R.layout.article_listing_item, parent, false);
            return new FeedViewHolder(v0);
        } else {
            View v0 = mInflator.inflate(R.layout.short_story_listing_item, parent, false);
            return new ShortStoriesViewHolder(v0);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof AdViewHolder) {
        } else if (holder instanceof HeaderViewHolder) {
            HeaderViewHolder viewHolder = (HeaderViewHolder) holder;
            addArticleItem(viewHolder.txvArticleTitle, viewHolder.forYouInfoLL, viewHolder.viewCountTextView, viewHolder.commentCountTextView,
                    viewHolder.recommendCountTextView, viewHolder.txvAuthorName, viewHolder.articleImageView, viewHolder.videoIndicatorImageView
                    , viewHolder.bookmarkArticleImageView, viewHolder.watchLaterImageView, articleDataModelsNew.get(position), position, viewHolder);
        } else if (holder instanceof FeedViewHolder) {
            FeedViewHolder viewHolder = (FeedViewHolder) holder;
            addArticleItem(viewHolder.txvArticleTitle, viewHolder.forYouInfoLL, viewHolder.viewCountTextView, viewHolder.commentCountTextView,
                    viewHolder.recommendCountTextView, viewHolder.txvAuthorName, viewHolder.articleImageView, viewHolder.videoIndicatorImageView
                    , viewHolder.bookmarkArticleImageView, viewHolder.watchLaterImageView, articleDataModelsNew.get(position), position, viewHolder);
        } else if (holder instanceof JoinGroupViewHolder) {
            if (StringUtils.isNullOrEmpty(heading) || StringUtils.isNullOrEmpty(subHeading)) {
                ((JoinGroupViewHolder) holder).groupHeadingTextView.setText("");
                ((JoinGroupViewHolder) holder).groupSubHeadingTextView.setText("");
                ((JoinGroupViewHolder) holder).joinGroupTextView.setVisibility(View.GONE);
            } else {
                ((JoinGroupViewHolder) holder).groupHeadingTextView.setText(heading);
                ((JoinGroupViewHolder) holder).groupSubHeadingTextView.setText(subHeading);
                ((JoinGroupViewHolder) holder).joinGroupTextView.setVisibility(View.VISIBLE);
            }
            try {
                Picasso.get().load(gpImageUrl).placeholder(R.drawable.groups_generic)
                        .error(R.drawable.groups_generic).into(((JoinGroupViewHolder) holder).groupHeaderImageView);
            } catch (Exception e) {
                ((JoinGroupViewHolder) holder).groupHeaderImageView.setImageResource(R.drawable.groups_generic);
            }
            JoinGroupViewHolder viewHolder = (JoinGroupViewHolder) holder;
            if (AppConstants.CONTENT_TYPE_ARTICLE.equals(articleDataModelsNew.get(position).getContentType())) {
                viewHolder.headerArticleView.setVisibility(View.VISIBLE);
                viewHolder.storyHeaderView.setVisibility(View.GONE);
                addArticleItem(viewHolder.txvArticleTitle, viewHolder.forYouInfoLL, viewHolder.viewCountTextView, viewHolder.commentCountTextView,
                        viewHolder.recommendCountTextView, viewHolder.txvAuthorName, viewHolder.articleImageView, viewHolder.videoIndicatorImageView
                        , viewHolder.bookmarkArticleImageView, viewHolder.watchLaterImageView, articleDataModelsNew.get(position), position, viewHolder);
            } else {
                viewHolder.headerArticleView.setVisibility(View.GONE);
                viewHolder.storyHeaderView.setVisibility(View.VISIBLE);
                addShortStoryItem(viewHolder, viewHolder.storyImage, viewHolder.authorNameTextView,
                        viewHolder.storyCommentCountTextView, viewHolder.storyRecommendationCountTextView, viewHolder.likeImageView,
                        articleDataModelsNew.get(position), viewHolder.followAuthorTextView, viewHolder.storyAuthorTextView,
                        viewHolder.shareStoryImageView, viewHolder.logoImageView);
            }
        } else if (holder instanceof VideoCarouselViewHolder) {
            if (!articleDataModelsNew.get(position).isCarouselRequestRunning() && !articleDataModelsNew.get(position).isResponseReceived()) {
                articleDataModelsNew.get(position).setCarouselRequestRunning(true);
                Log.d("VideoCarouselViewHolder", "BEFORE API = " + position);
                new LoadVideoCarouselAsyncTask((VideoCarouselViewHolder) holder, position).execute();
            } else if (articleDataModelsNew.get(position).isCarouselRequestRunning() && !articleDataModelsNew.get(position).isResponseReceived()) {
            } else {
                Log.d("VideoCarouselViewHolder", "RECYCLED = " + position
                        + "request = " + articleDataModelsNew.get(position).isCarouselRequestRunning() + " response = " + articleDataModelsNew.get(position).isResponseReceived());
                populateCarouselVideos((VideoCarouselViewHolder) holder, articleDataModelsNew.get(position).getCarouselVideoList());
            }
            VideoCarouselViewHolder viewHolder = (VideoCarouselViewHolder) holder;
            if (AppConstants.CONTENT_TYPE_ARTICLE.equals(articleDataModelsNew.get(position).getContentType())) {
                viewHolder.headerArticleView.setVisibility(View.VISIBLE);
                viewHolder.storyHeaderView.setVisibility(View.GONE);

                addArticleItem(viewHolder.txvArticleTitle, viewHolder.forYouInfoLL, viewHolder.viewCountTextView, viewHolder.commentCountTextView,
                        viewHolder.recommendCountTextView, viewHolder.txvAuthorName, viewHolder.articleImageView, viewHolder.videoIndicatorImageView
                        , viewHolder.bookmarkArticleImageView, viewHolder.watchLaterImageView, articleDataModelsNew.get(position), position, viewHolder);
            } else {
                viewHolder.headerArticleView.setVisibility(View.GONE);
                viewHolder.storyHeaderView.setVisibility(View.VISIBLE);
                addShortStoryItem(viewHolder, viewHolder.storyImage, viewHolder.authorNameTextView,
                        viewHolder.storyCommentCountTextView, viewHolder.storyRecommendationCountTextView, viewHolder.likeImageView,
                        articleDataModelsNew.get(position), viewHolder.followAuthorTextView, viewHolder.storyAuthorTextView,
                        viewHolder.shareStoryImageView, viewHolder.logoImageView);
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
                    if (campaignListDataModels != null && campaignListDataModels.size() > 4) {
                        addCampaignCard(viewHolder.campaignHeader, viewHolder.brandImg, viewHolder.brandName, viewHolder.campaignName, viewHolder.campaignStatus, campaignListDataModels.get(0), position, viewHolder);
                        addCampaignCard(viewHolder.campaignHeader2, viewHolder.brandImg2, viewHolder.brandName2, viewHolder.campaignName2, viewHolder.campaignStatus2, campaignListDataModels.get(1), position, viewHolder);
                        addCampaignCard(viewHolder.campaignHeader3, viewHolder.brandImg3, viewHolder.brandName3, viewHolder.campaignName3, viewHolder.campaignStatus3, campaignListDataModels.get(2), position, viewHolder);
                        addCampaignCard(viewHolder.campaignHeader4, viewHolder.brandImg4, viewHolder.brandName4, viewHolder.campaignName4, viewHolder.campaignStatus4, campaignListDataModels.get(3), position, viewHolder);
                        addCampaignCard(viewHolder.campaignHeader5, viewHolder.brandImg5, viewHolder.brandName5, viewHolder.campaignName5, viewHolder.campaignStatus5, campaignListDataModels.get(4), position, viewHolder);
                    }
                } catch (Exception e) {
                    Crashlytics.logException(e);
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

                addArticleItem(viewHolder.txvArticleTitle, viewHolder.forYouInfoLL, viewHolder.viewCountTextView, viewHolder.commentCountTextView,
                        viewHolder.recommendCountTextView, viewHolder.txvAuthorName, viewHolder.articleImageView, viewHolder.videoIndicatorImageView
                        , viewHolder.bookmarkArticleImageView, viewHolder.watchLaterImageView, articleDataModelsNew.get(position), position, viewHolder);
            } else {
                viewHolder.headerArticleView.setVisibility(View.GONE);
                viewHolder.storyHeaderView.setVisibility(View.VISIBLE);
                addShortStoryItem(viewHolder, viewHolder.storyImage, viewHolder.authorNameTextView,
                        viewHolder.storyCommentCountTextView, viewHolder.storyRecommendationCountTextView, viewHolder.likeImageView,
                        articleDataModelsNew.get(position), viewHolder.followAuthorTextView, viewHolder.storyAuthorTextView,
                        viewHolder.shareStoryImageView, viewHolder.logoImageView);
            }
        } else {
            ShortStoriesViewHolder viewHolder = (ShortStoriesViewHolder) holder;
            addShortStoryItem(viewHolder, viewHolder.storyImage, viewHolder.authorNameTextView,
                    viewHolder.storyCommentCountTextView, viewHolder.storyRecommendationCountTextView, viewHolder.likeImageView,
                    articleDataModelsNew.get(position), viewHolder.followAuthorTextView, viewHolder.storyAuthorTextView,
                    viewHolder.shareStoryImageView, viewHolder.logoImageView);
        }
    }

    private void addCampaignCard(ImageView campaignHeader, CircularImageView brandImg, TextView brandName, TextView campaignName, TextView campaignStatus, final CampaignDataListResult data, final int position, final RecyclerView.ViewHolder holder) {
        Picasso.get().load(data.getImageUrl()).placeholder(R.drawable.default_article).error(R.drawable.default_article).into(campaignHeader);
        Picasso.get().load(data.getBrandDetails().getImageUrl()).placeholder(R.drawable.default_article).error(R.drawable.default_article).into(brandImg);
        brandName.setText(data.getBrandDetails().getName());
        campaignName.setText(data.getName());
        setTextAndColor(data.getCampaignStatus(), campaignStatus);
    }

    private void setTextAndColor(int status, TextView campaignStatus) {
        if (status == 0) {
            campaignStatus.setText(mContext.getResources().getString(R.string.campaign_details_expired));
            campaignStatus.setBackgroundResource(R.drawable.campaign_expired);
        } else if (status == 1 || status == 18) {
            campaignStatus.setText(mContext.getResources().getString(R.string.campaign_details_apply_now));
            campaignStatus.setBackgroundResource(R.drawable.subscribe_now);
        } else if (status == 2) {
            campaignStatus.setText(mContext.getResources().getString(R.string.campaign_details_submission_open));
            campaignStatus.setBackgroundResource(R.drawable.campaign_subscription_open);
        } else if (status == 22) {
            campaignStatus.setText(mContext.getResources().getString(R.string.campaign_details_submission_open));
            campaignStatus.setBackgroundResource(R.drawable.campaign_subscription_open);
        } else if (status == 3) {
            campaignStatus.setText(mContext.getResources().getString(R.string.campaign_details_applied));
            campaignStatus.setBackgroundResource(R.drawable.campaign_applied);
        } else if (status == 21) {
            campaignStatus.setText(mContext.getResources().getString(R.string.campaign_details_approved));
            campaignStatus.setBackgroundResource(R.drawable.campaign_subscribed);
        } else if (status == 4) {
            campaignStatus.setText(mContext.getResources().getString(R.string.campaign_details_application_full));
            campaignStatus.setBackgroundResource(R.drawable.campaign_submission_full);
        } else if (status == 5) {
            campaignStatus.setText(mContext.getResources().getString(R.string.campaign_details_apply_now));
            campaignStatus.setBackgroundResource(R.drawable.subscribe_now);
           /* if (forYouStatus == 0) {
                campaignStatus.setText(mContext.getResources().getString(R.string.campaign_details_apply_now));
                campaignStatus.setBackgroundResource(R.drawable.subscribe_now);
            } else {
                campaignStatus.setText(mContext.getResources().getString(R.string.campaign_details_ineligible));
                campaignStatus.setBackgroundResource(R.drawable.campaign_expired);
            }*/
        } else if (status == 6) {
            campaignStatus.setText(mContext.getResources().getString(R.string.campaign_list_proof_reject));
            campaignStatus.setBackgroundResource(R.drawable.campaign_rejected);
        } else if (status == 17) {
            campaignStatus.setText(mContext.getResources().getString(R.string.campaign_list_proof_reject));
            campaignStatus.setBackgroundResource(R.drawable.campaign_rejected);
        } else if (status == 7) {
            campaignStatus.setText(mContext.getResources().getString(R.string.campaign_details_completed));
            campaignStatus.setBackgroundResource(R.drawable.campaign_completed);
        } else if (status == 8) {
            campaignStatus.setText(mContext.getResources().getString(R.string.campaign_details_invite_only));
            campaignStatus.setBackgroundResource(R.drawable.campaign_invite_only);
        } else if (status == 9) {
            campaignStatus.setText(mContext.getResources().getString(R.string.campaign_list_proof_moderation));
            campaignStatus.setBackgroundResource(R.drawable.campaign_subscription_open);
        } else if (status == 16) {
            campaignStatus.setText(mContext.getResources().getString(R.string.campaign_list_proof_moderation));
            campaignStatus.setBackgroundResource(R.drawable.campaign_subscription_open);
        } else if (status == 10) {
            campaignStatus.setText(mContext.getResources().getString(R.string.campaign_list_proof_reject));
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
            viewCountTV.setText(data.getArticleCount());
        }
        if (null == data.getCommentsCount() || "0".equals(data.getCommentsCount())) {
            commentCountTV.setVisibility(View.GONE);
        } else {
            commentCountTV.setVisibility(View.VISIBLE);
            commentCountTV.setText(data.getCommentsCount());
        }
        if (null == data.getLikesCount() || "0".equals(data.getLikesCount())) {
            recommendCountTV.setVisibility(View.GONE);
        } else {
            recommendCountTV.setVisibility(View.VISIBLE);
            recommendCountTV.setText(data.getLikesCount());
        }
        if (StringUtils.isNullOrEmpty(data.getUserName()) || data.getUserName().trim().equalsIgnoreCase("")) {
            authorNameTV.setText("NA");
        } else {
            authorNameTV.setText(data.getUserName());
        }
        try {
            if (!StringUtils.isNullOrEmpty(data.getVideoUrl())
                    && (data.getImageUrl().getThumbMax() == null || data.getImageUrl().getThumbMax().contains("default.jp"))) {
                Picasso.get().load(AppUtils.getYoutubeThumbnailURLMomspresso(data.getVideoUrl())).placeholder(R.drawable.default_article).into(articleIV);
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
                bookmarkArticleIV.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_bookmark));
            } else {
                bookmarkArticleIV.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_bookmarked));
            }
        } else {
            bookmarkArticleIV.setVisibility(View.VISIBLE);
            watchLaterIV.setVisibility(View.GONE);
            if ("0".equals(data.getIs_bookmark())) {
                bookmarkArticleIV.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_bookmark));
            } else {
                bookmarkArticleIV.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_bookmarked));
            }
        }
        watchLaterIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addRemoveWatchLater(position, holder);
                Utils.pushWatchLaterArticleEvent(mContext, "ArticleListing", SharedPrefUtils.getUserDetailModel(mContext).getDynamoId() + "",
                        data.getId(), data.getUserId() + "~" + data.getUserName());
            }
        });
        bookmarkArticleIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isRequestRunning) {
                    addRemoveBookmark(position, holder);
                }
                Utils.pushBookmarkArticleEvent(mContext, "ArticleListing", SharedPrefUtils.getUserDetailModel(mContext).getDynamoId() + "",
                        data.getId(), data.getUserId() + "~" + data.getUserName());
            }
        });
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
            likeIV.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_recommended));
        } else {
            likeIV.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_ss_like));
        }

        try {
            Picasso.get().load(data.getStoryImage().trim()).into(storyImage);
        } catch (Exception e) {
            storyImage.setImageResource(R.drawable.default_article);
        }

        if (data.getIsfollowing().equals("1")) {
            followAuthorTextView.setText(mContext.getResources().getString(R.string.ad_following_author));
        } else {
            followAuthorTextView.setText(mContext.getResources().getString(R.string.ad_follow_author));
        }
        try {
            Picasso.get().load(data.getStoryImage()).into(shareStoryImageView);
            storyAuthorTextView.setText(data.getUserName());
        } catch (Exception e) {
            Crashlytics.logException(e);
            Log.d("MC4kException", Log.getStackTraceString(e));
        }
    }

    @Override
    public void onMembershipStatusFetchSuccess(GroupsMembershipResponse body, int groupId) {
        String userType = null;
        if (body.getData().getResult() == null || body.getData().getResult().isEmpty()) {

        } else {
            if (body.getData().getResult().get(0).getIsAdmin() == 1) {
                userType = AppConstants.GROUP_MEMBER_TYPE_ADMIN;
            } else if (body.getData().getResult().get(0).getIsModerator() == 1) {
                userType = AppConstants.GROUP_MEMBER_TYPE_MODERATOR;
            }
        }
        if (!AppConstants.GROUP_MEMBER_TYPE_MODERATOR.equals(userType) && !AppConstants.GROUP_MEMBER_TYPE_ADMIN.equals(userType)) {
            if ("male".equalsIgnoreCase(SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getGender()) ||
                    "m".equalsIgnoreCase(SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getGender())) {
                Toast.makeText(mContext, mContext.getString(R.string.women_only), Toast.LENGTH_SHORT).show();
                if (BuildConfig.DEBUG || AppConstants.DEBUGGING_USER_ID.contains(SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId())) {

                } else {
                    return;
                }
            } else {

            }
        }
        if (body.getData().getResult() == null || body.getData().getResult().isEmpty()) {
            Intent intent = new Intent(mContext, GroupsSummaryActivity.class);
            intent.putExtra("groupId", groupId);
            intent.putExtra(AppConstants.GROUP_MEMBER_TYPE, userType);
            mContext.startActivity(intent);
        } else if (AppConstants.GROUP_MEMBERSHIP_STATUS_BLOCKED.equals(body.getData().getResult().get(0).getStatus())) {
            Toast.makeText(mContext, mContext.getString(R.string.groups_user_blocked_msg), Toast.LENGTH_SHORT).show();
        } else if (AppConstants.GROUP_MEMBERSHIP_STATUS_MEMBER.equals(body.getData().getResult().get(0).getStatus())) {
            Intent intent = new Intent(mContext, GroupDetailsActivity.class);
            intent.putExtra("groupId", groupId);
            intent.putExtra(AppConstants.GROUP_MEMBER_TYPE, userType);
            mContext.startActivity(intent);
        } else if (AppConstants.GROUP_MEMBERSHIP_STATUS_PENDING_MODERATION.equals(body.getData().getResult().get(0).getStatus())) {
            Intent intent = new Intent(mContext, GroupsSummaryActivity.class);
            intent.putExtra("groupId", groupId);
            intent.putExtra("pendingMembershipFlag", true);
            intent.putExtra(AppConstants.GROUP_MEMBER_TYPE, userType);
            mContext.startActivity(intent);
        } else {
            Intent intent = new Intent(mContext, GroupsSummaryActivity.class);
            intent.putExtra("groupId", groupId);
            intent.putExtra(AppConstants.GROUP_MEMBER_TYPE, userType);
            mContext.startActivity(intent);
        }
    }

    @Override
    public void onMembershipStatusFetchFail() {

    }

    public class AdViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        AdViewHolder(View adView) {
            super(adView);
        }

        @Override
        public void onClick(View v) {
            if (getAdapterPosition() != RecyclerView.NO_POSITION) {
                mListener.onRecyclerItemClick(v, getAdapterPosition());
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
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (getAdapterPosition() != RecyclerView.NO_POSITION) {
                mListener.onRecyclerItemClick(v, getAdapterPosition());
            }
        }
    }

    public class ShortStoriesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView authorNameTextView;
        TextView storyCommentCountTextView;
        LinearLayout storyRecommendationContainer, storyCommentContainer;
        TextView storyRecommendationCountTextView, followAuthorTextView;
        ImageView storyImage, likeImageView, menuItem;
        ImageView facebookShareImageView, whatsappShareImageView, instagramShareImageView, genericShareImageView;
        StoryShareCardWidget storyShareCardWidget;
        ImageView shareStoryImageView, logoImageView;
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
                mListener.onRecyclerItemClick(v, getAdapterPosition());
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
                mListener.onRecyclerItemClick(v, getAdapterPosition());
            }
        }
    }


    public class CampaignCarouselViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        RelativeLayout adSlotContainer;
        WebView adSlotWebView;
        RelativeLayout relativeLayoutContainer;
        HorizontalScrollView videoCarouselContainer;
        ImageView campaignHeader, campaignHeader2, campaignHeader3, campaignHeader4, campaignHeader5;
        CircularImageView brandImg, brandImg2, brandImg3, brandImg4, brandImg5;
        TextView brandName, campaignName, campaignStatus, brandName2, campaignName2, campaignStatus2, brandName3, campaignName3, campaignStatus3, brandName4, campaignName4, campaignStatus4, brandName5, campaignName5, campaignStatus5;
        CardView cardView1, cardView2, cardView3, cardView4, cardView5;

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
        TextView storyCommentCountTextView, followAuthorTextView;
        LinearLayout storyRecommendationContainer, storyCommentContainer;
        TextView storyRecommendationCountTextView;
        ImageView storyImage, likeImageView, menuItem;
        ImageView facebookShareImageView, whatsappShareImageView, instagramShareImageView, genericShareImageView;
        StoryShareCardWidget storyShareCardWidget;
        ImageView shareStoryImageView, logoImageView;
        TextView storyAuthorTextView;

        CampaignCarouselViewHolder(View view) {
            super(view);
            adSlotContainer = view.findViewById(R.id.adSlotContainer);
            adSlotWebView = view.findViewById(R.id.adSlotWebView);
            relativeLayoutContainer = view.findViewById(R.id.relativeLayoutContainer);
            videoCarouselContainer = view.findViewById(R.id.videoCarouselContainer);
            campaignHeader = view.findViewById(R.id.campaign_header);
            campaignHeader2 = view.findViewById(R.id.campaign_header2);
            campaignHeader3 = view.findViewById(R.id.campaign_header3);
            campaignHeader4 = view.findViewById(R.id.campaign_header4);
            campaignHeader5 = view.findViewById(R.id.campaign_header5);
            brandImg = view.findViewById(R.id.brand_img);
            brandImg2 = view.findViewById(R.id.brand_img2);
            brandImg3 = view.findViewById(R.id.brand_img3);
            brandImg4 = view.findViewById(R.id.brand_img4);
            brandImg5 = view.findViewById(R.id.brand_img5);
            brandName = view.findViewById(R.id.brand_name);
            brandName2 = view.findViewById(R.id.brand_name2);
            brandName3 = view.findViewById(R.id.brand_name3);
            brandName4 = view.findViewById(R.id.brand_name4);
            brandName5 = view.findViewById(R.id.brand_name5);
            campaignName = view.findViewById(R.id.campaign_name);
            campaignName2 = view.findViewById(R.id.campaign_name2);
            campaignName3 = view.findViewById(R.id.campaign_name3);
            campaignName4 = view.findViewById(R.id.campaign_name4);
            campaignName5 = view.findViewById(R.id.campaign_name5);
            campaignStatus = view.findViewById(R.id.submission_status);
            campaignStatus2 = view.findViewById(R.id.submission_status2);
            campaignStatus3 = view.findViewById(R.id.submission_status3);
            campaignStatus4 = view.findViewById(R.id.submission_status4);
            campaignStatus5 = view.findViewById(R.id.submission_status5);
            menuItem = view.findViewById(R.id.menuItem);
            followAuthorTextView = view.findViewById(R.id.followAuthorTextView);
            cardView1 = view.findViewById(R.id.cardView1);
            cardView2 = view.findViewById(R.id.cardView2);
            cardView3 = view.findViewById(R.id.cardView3);
            cardView4 = view.findViewById(R.id.cardView4);
            cardView5 = view.findViewById(R.id.cardView5);
            cardView1.setOnClickListener(this);
            cardView2.setOnClickListener(this);
            cardView3.setOnClickListener(this);
            cardView4.setOnClickListener(this);
            cardView5.setOnClickListener(this);
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
                mListener.onRecyclerItemClick(v, getAdapterPosition());
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
        TextView authorNameTextView, followAuthorTextView;
        TextView storyCommentCountTextView;
        LinearLayout storyRecommendationContainer, storyCommentContainer;
        TextView storyRecommendationCountTextView;
        ImageView storyImage, likeImageView, menuItem;
        ImageView facebookShareImageView, whatsappShareImageView, instagramShareImageView, genericShareImageView;
        StoryShareCardWidget storyShareCardWidget;
        ImageView shareStoryImageView, logoImageView;
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
                mListener.onRecyclerItemClick(v, getAdapterPosition());
            }
        }
    }

    public class JoinGroupViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        FrameLayout groupHeaderView;
        TextView groupHeadingTextView;
        TextView groupSubHeadingTextView;
        TextView joinGroupTextView;
        ImageView groupHeaderImageView;

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
        ImageView watchLaterImageView, menuItem;

        RelativeLayout storyHeaderView;

        TextView authorNameTextView, followAuthorTextView;
        TextView storyCommentCountTextView;
        LinearLayout storyRecommendationContainer, storyCommentContainer;
        TextView storyRecommendationCountTextView;
        ImageView storyImage, likeImageView;
        ImageView facebookShareImageView, whatsappShareImageView, instagramShareImageView, genericShareImageView;
        RelativeLayout mainView;
        StoryShareCardWidget storyShareCardWidget;
        ImageView shareStoryImageView, logoImageView;
        TextView storyAuthorTextView;

        JoinGroupViewHolder(View view) {
            super(view);
            groupHeaderView = (FrameLayout) view.findViewById(R.id.groupHeaderView);
            groupHeadingTextView = (TextView) view.findViewById(R.id.groupHeadingTextView);
            groupSubHeadingTextView = (TextView) view.findViewById(R.id.groupSubHeadingTextView);
            joinGroupTextView = (TextView) view.findViewById(R.id.joinGroupTextView);
            groupHeaderImageView = (ImageView) view.findViewById(R.id.groupHeaderImageView);

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
            menuItem = (ImageView) view.findViewById(R.id.menuItem);
            followAuthorTextView = (TextView) view.findViewById(R.id.followAuthorTextView);
            storyShareCardWidget = (StoryShareCardWidget) itemView.findViewById(R.id.storyShareCardWidget);
            shareStoryImageView = (ImageView) storyShareCardWidget.findViewById(R.id.storyImageView);
            logoImageView = (ImageView) storyShareCardWidget.findViewById(R.id.logoImageView);
            storyAuthorTextView = (TextView) storyShareCardWidget.findViewById(R.id.storyAuthorTextView);
            whatsappShareImageView.setTag(view);

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

            groupHeaderView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("groupHeaderView", "GROUPID-" + groupId);
                    if (groupId == 0) {
                        if (mContext instanceof DashboardActivity) {
                            GroupsViewFragment groupsFragment = new GroupsViewFragment();
                            Bundle bundle = new Bundle();
                            groupsFragment.setArguments(bundle);
                            ((DashboardActivity) mContext).addFragment(groupsFragment, bundle, true);
                        } else {
                            Intent groupIntent = new Intent(mContext, DashboardActivity.class);
                            groupIntent.putExtra("TabType", "group");
                            mContext.startActivity(groupIntent);
                        }
                    } else {
                        GroupMembershipStatus groupMembershipStatus = new GroupMembershipStatus(MainArticleRecyclerViewAdapter.this);
                        groupMembershipStatus.checkMembershipStatus(groupId, SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId());
                    }

                    try {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("userId", SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId());
                        jsonObject.put("screenName", "" + screenName);
                        jsonObject.put("Topic", "" + screenName);
                        mixpanel.track("JoinSupportGroupBannerClick", jsonObject);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        @Override
        public void onClick(View v) {
            if (getAdapterPosition() != RecyclerView.NO_POSITION) {
                mListener.onRecyclerItemClick(v, getAdapterPosition());
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
            new MainArticleRecyclerViewAdapter.AddRemoveBookmarkAsyncTask(holder, "bookmarkArticle", position).execute(jsonString, "bookmarkArticle");
        } else {
            articleDataModelsNew.get(position).setIs_bookmark("0");
            notifyDataSetChanged();
            DeleteBookmarkRequest deleteBookmarkRequest = new DeleteBookmarkRequest();
            deleteBookmarkRequest.setId(articleDataModelsNew.get(position).getBookmarkId());
            String jsonString = new Gson().toJson(deleteBookmarkRequest);
            new MainArticleRecyclerViewAdapter.AddRemoveBookmarkAsyncTask(holder, "unbookmarkArticle", position).execute(jsonString, "unbookmarkArticle");
        }
    }

    private void addRemoveWatchLater(int position, RecyclerView.ViewHolder holder) {
        if (articleDataModelsNew.get(position).getListingWatchLaterStatus() == 0) {
            ArticleDetailRequest articleDetailRequest = new ArticleDetailRequest();
            articleDetailRequest.setArticleId(articleDataModelsNew.get(position).getId());
            String jsonString = new Gson().toJson(articleDetailRequest);
            new MainArticleRecyclerViewAdapter.AddRemoveBookmarkAsyncTask(holder, "bookmarkVideo", position).execute(jsonString, "bookmarkVideo");
        } else {
            DeleteBookmarkRequest deleteBookmarkRequest = new DeleteBookmarkRequest();
            deleteBookmarkRequest.setId(articleDataModelsNew.get(position).getBookmarkId());
            String jsonString = new Gson().toJson(deleteBookmarkRequest);
            new MainArticleRecyclerViewAdapter.AddRemoveBookmarkAsyncTask(holder, "unbookmarkVideo", position).execute(jsonString, "unbookmarkVideo");
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
            String JsonResponse;
            String JsonDATA = strings[0];

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
                urlConnection.addRequestProperty("id", SharedPrefUtils.getUserDetailModel(mContext).getDynamoId());
                urlConnection.addRequestProperty("mc4kToken", SharedPrefUtils.getUserDetailModel(mContext).getMc4kToken());

                Writer writer = new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream(), "UTF-8"));
                writer.write(JsonDATA);
                writer.close();
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String inputLine;
                while ((inputLine = reader.readLine()) != null)
                    buffer.append(inputLine + "\n");
                if (buffer.length() == 0) {
                    return null;
                }
                JsonResponse = buffer.toString();
                Log.i("RESPONSE " + type, JsonResponse);
                return JsonResponse;
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
                        if (articleDataModelsNew.get(i).getId().equals(responseData.getData().getResult().getArticleId())) {
                            if ("bookmarkArticle".equals(type)) {
                                articleDataModelsNew.get(i).setIs_bookmark("1");
                                articleDataModelsNew.get(i).setBookmarkId(responseData.getData().getResult().getBookmarkId());
                                if (viewHolder instanceof FeedViewHolder) {
                                    ((FeedViewHolder) viewHolder).bookmarkArticleImageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_bookmarked));
                                } else if (viewHolder instanceof HeaderViewHolder) {
                                    ((HeaderViewHolder) viewHolder).bookmarkArticleImageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_bookmarked));
                                } else if (viewHolder instanceof JoinGroupViewHolder) {
                                    ((JoinGroupViewHolder) viewHolder).bookmarkArticleImageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_bookmarked));
                                }
                                if (mContext instanceof DashboardActivity) {
                                    ((DashboardActivity) mContext).showBookmarkConfirmationTooltip();
                                } else if (mContext instanceof FilteredTopicsArticleListingActivity) {
                                    ((FilteredTopicsArticleListingActivity) mContext).showBookmarkConfirmationTooltip();
                                }
                            } else if ("unbookmarkArticle".equals(type)) {
                                articleDataModelsNew.get(i).setIs_bookmark("0");
                                articleDataModelsNew.get(i).setBookmarkId(responseData.getData().getResult().getBookmarkId());
                                if (viewHolder instanceof FeedViewHolder) {
                                    ((FeedViewHolder) viewHolder).bookmarkArticleImageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_bookmark));
                                } else if (viewHolder instanceof HeaderViewHolder) {
                                    ((HeaderViewHolder) viewHolder).bookmarkArticleImageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_bookmark));
                                } else if (viewHolder instanceof JoinGroupViewHolder) {
                                    ((JoinGroupViewHolder) viewHolder).bookmarkArticleImageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_bookmarked));
                                }
                            } else if ("bookmarkVideo".equals(type)) {
                                articleDataModelsNew.get(i).setListingWatchLaterStatus(1);
                                articleDataModelsNew.get(i).setBookmarkId(responseData.getData().getResult().getBookmarkId());
                                if (viewHolder instanceof FeedViewHolder) {
                                    ((FeedViewHolder) viewHolder).watchLaterImageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_watch_added));
                                } else if (viewHolder instanceof HeaderViewHolder) {
                                    ((HeaderViewHolder) viewHolder).watchLaterImageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_watch_added));
                                } else if (viewHolder instanceof JoinGroupViewHolder) {
                                    ((JoinGroupViewHolder) viewHolder).bookmarkArticleImageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_bookmarked));
                                }
                            } else if ("unbookmarkVideo".equals(type)) {
                                articleDataModelsNew.get(i).setListingWatchLaterStatus(0);
                                articleDataModelsNew.get(i).setBookmarkId(responseData.getData().getResult().getBookmarkId());
                                if (viewHolder instanceof FeedViewHolder) {
                                    ((FeedViewHolder) viewHolder).watchLaterImageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_watch));
                                } else if (viewHolder instanceof HeaderViewHolder) {
                                    ((HeaderViewHolder) viewHolder).watchLaterImageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_watch));
                                } else if (viewHolder instanceof JoinGroupViewHolder) {
                                    ((JoinGroupViewHolder) viewHolder).bookmarkArticleImageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_bookmarked));
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
                        ((FeedViewHolder) viewHolder).bookmarkArticleImageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_bookmark));
                    } else if (viewHolder instanceof HeaderViewHolder) {
                        ((HeaderViewHolder) viewHolder).bookmarkArticleImageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_bookmark));
                    } else if (viewHolder instanceof JoinGroupViewHolder) {
                        ((JoinGroupViewHolder) viewHolder).bookmarkArticleImageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_bookmark));
                    } else if (viewHolder instanceof VideoCarouselViewHolder) {
                        ((VideoCarouselViewHolder) viewHolder).bookmarkArticleImageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_bookmark));
                    }
                    break;
                case "unbookmarkArticle":
                    if (viewHolder instanceof FeedViewHolder) {
                        ((FeedViewHolder) viewHolder).bookmarkArticleImageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_bookmarked));
                    } else if (viewHolder instanceof HeaderViewHolder) {
                        ((HeaderViewHolder) viewHolder).bookmarkArticleImageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_bookmarked));
                    } else if (viewHolder instanceof JoinGroupViewHolder) {
                        ((JoinGroupViewHolder) viewHolder).bookmarkArticleImageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_bookmark));
                    } else if (viewHolder instanceof VideoCarouselViewHolder) {
                        ((VideoCarouselViewHolder) viewHolder).bookmarkArticleImageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_bookmark));
                    }
                    break;
                case "bookmarkVideo":
                    if (viewHolder instanceof FeedViewHolder) {
                        ((FeedViewHolder) viewHolder).watchLaterImageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_watch));
                    } else if (viewHolder instanceof HeaderViewHolder) {
                        ((HeaderViewHolder) viewHolder).watchLaterImageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_watch));
                    } else if (viewHolder instanceof JoinGroupViewHolder) {
                        ((JoinGroupViewHolder) viewHolder).bookmarkArticleImageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_bookmark));
                    } else if (viewHolder instanceof VideoCarouselViewHolder) {
                        ((VideoCarouselViewHolder) viewHolder).bookmarkArticleImageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_bookmark));
                    }
                    break;
                case "unbookmarkVideo":
                    if (viewHolder instanceof FeedViewHolder) {
                        ((FeedViewHolder) viewHolder).watchLaterImageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_watch_added));
                    } else if (viewHolder instanceof HeaderViewHolder) {
                        ((HeaderViewHolder) viewHolder).watchLaterImageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_watch_added));
                    } else if (viewHolder instanceof JoinGroupViewHolder) {
                        ((JoinGroupViewHolder) viewHolder).bookmarkArticleImageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_bookmark));
                    } else if (viewHolder instanceof VideoCarouselViewHolder) {
                        ((VideoCarouselViewHolder) viewHolder).bookmarkArticleImageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_bookmark));
                    }
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

            String JsonResponse;

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            try {
                int start = (((pos + 3) / 6) * 5) - 5;
                int end = start + 5;
                URL url = new URL("http://api.momspresso.com/" + "v2/videos/?" + "start=" + start + "&end=" + end + "&sort=0&type=3");
                Log.d("VideoCarouselViewHolder", AppConstants.BASE_URL + "v1/videos/?" + "start=" + start + "&end=" + end + "&sort=0&type=3");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Accept", "application/json");
                urlConnection.setRequestProperty("Accept-Language", Locale.getDefault().getLanguage());
                urlConnection.addRequestProperty("id", SharedPrefUtils.getUserDetailModel(mContext).getDynamoId());
                urlConnection.addRequestProperty("mc4kToken", SharedPrefUtils.getUserDetailModel(mContext).getMc4kToken());
                InputStream inputStream = urlConnection.getInputStream();
                InputStreamReader isReader = new InputStreamReader(inputStream);
                reader = new BufferedReader(isReader);
                String line = reader.readLine();
                StringBuffer readTextBuf = new StringBuffer();
                while (line != null) {
                    readTextBuf.append(line);
                    line = reader.readLine();
                }
                JsonResponse = readTextBuf.toString();
                Log.d("VideoCarouselViewHolder", "backgroud finish = " + JsonResponse);
                return JsonResponse;
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
                        Log.d("VideoCarouselViewHolder", "pos=" + pos + "  SIZE=" + responseData.getData().get(0).getResult().size());
                        articleDataModelsNew.get(pos).setCarouselVideoList(responseData.getData().get(0).getResult());
                        Log.d("VideoCarouselViewHolder", "ASYNC");
                        populateCarouselVideos(viewHolder, responseData.getData().get(0).getResult());
                        articleDataModelsNew.get(pos).setCarouselRequestRunning(false);
                        articleDataModelsNew.get(pos).setResponseReceived(true);
                    } else {
                        VlogsListingResponse responseData1 = gson.fromJson(result, VlogsListingResponse.class);
                        if (responseData != null) {
                            Log.d("VideoCarouselViewHolder", "pos=" + pos + "  SIZE=" + responseData1.getData().get(0).getResult().size());
                            articleDataModelsNew.get(pos).setCarouselVideoList(responseData1.getData().get(0).getResult());
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

    private void populateCarouselVideos(VideoCarouselViewHolder viewHolder, ArrayList<VlogsListingAndDetailResult> result) {
        ArrayList<VlogsListingAndDetailResult> videoList = result;
        if (videoList.isEmpty()) {
            viewHolder.videoContainerFL1.setVisibility(View.GONE);
            return;
        } else {
            viewHolder.videoContainerFL1.setVisibility(View.VISIBLE);
        }
        if (videoList.size() > 0) {
            updateCarouselView(viewHolder.txvArticleTitle1, viewHolder.articleImageView1, viewHolder.txvAuthorName1,
                    viewHolder.viewCountTextView1, viewHolder.commentCountTextView1, viewHolder.recommendCountTextView1, videoList.get(0));
        }
    }

    private void updateCarouselView(TextView textView, ImageView imageView, TextView authorTextView, TextView viewCountTextView1,
                                    TextView commentCountTextView1, TextView recommendCountTextView1, VlogsListingAndDetailResult data) {
        textView.setText(data.getTitle());
        if (null == data.getView_count() || "0".equals(data.getView_count())) {
            viewCountTextView1.setVisibility(View.GONE);
        } else {
            viewCountTextView1.setVisibility(View.VISIBLE);
            viewCountTextView1.setText(data.getView_count());
        }
        if (null == data.getComment_count() || "0".equals(data.getComment_count())) {
            commentCountTextView1.setVisibility(View.GONE);
        } else {
            commentCountTextView1.setVisibility(View.VISIBLE);
            commentCountTextView1.setText(data.getComment_count());
        }
        if (null == data.getLike_count() || "0".equals(data.getLike_count())) {
            recommendCountTextView1.setVisibility(View.GONE);
        } else {
            recommendCountTextView1.setVisibility(View.VISIBLE);
            recommendCountTextView1.setText(data.getLike_count());
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