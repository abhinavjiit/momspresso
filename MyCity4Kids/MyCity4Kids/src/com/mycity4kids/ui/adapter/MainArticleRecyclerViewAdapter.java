package com.mycity4kids.ui.adapter;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.ads.AdChoicesView;
import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAd;
import com.google.gson.Gson;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.request.ArticleDetailRequest;
import com.mycity4kids.models.request.DeleteBookmarkRequest;
import com.mycity4kids.models.response.AddBookmarkResponse;
import com.mycity4kids.models.response.ArticleListingResult;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.ui.activity.ArticleListingActivity;
import com.mycity4kids.ui.fragment.ForYouInfoDialogFragment;
import com.mycity4kids.utils.AppUtils;
import com.mycity4kids.widget.FeedNativeAd;
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
import java.util.List;

/**
 * Created by hemant on 4/12/17.
 */

public class MainArticleRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static int ARTICLE = 0;
    private static int AD = 1;
    private static int HEADER = 2;
    private final Context mContext;
    private final LayoutInflater mInflator;
    private final FeedNativeAd feedNativeAd;
    private ArrayList<ArticleListingResult> articleDataModelsNew;
    private RecyclerViewClickListener mListener;
    private boolean isAdChoiceAdded = false;
    private boolean topicHeaderVisibilityFlag;
    private List<NativeAd> adList = new ArrayList<>(10);

    public MainArticleRecyclerViewAdapter(Context pContext, FeedNativeAd feedNativeAd, RecyclerViewClickListener listener, boolean topicHeaderVisibilityFlag) {
        mContext = pContext;
        mInflator = (LayoutInflater) pContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.feedNativeAd = feedNativeAd;
        mListener = listener;
        this.topicHeaderVisibilityFlag = topicHeaderVisibilityFlag;
        for (int i = 0; i < 10; i++) {
            adList.add(null);
        }
    }

    public void hideFollowTopicHeader() {
        topicHeaderVisibilityFlag = false;
    }

    public void setNewListData(ArrayList<ArticleListingResult> mParentingLists_new) {
        articleDataModelsNew = mParentingLists_new;
    }

    @Override
    public int getItemCount() {
        return articleDataModelsNew.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (topicHeaderVisibilityFlag && position == 0) {
            return HEADER;
        } else if (position != 0 && position % 8 == 0) {
            return AD;
        } else {
            return ARTICLE;
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
        } else {
            View v0 = mInflator.inflate(R.layout.article_listing_item, parent, false);
            return new FeedViewHolder(v0);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof AdViewHolder) {
            try {
                addArticleItem((AdViewHolder) holder, position);
                if (position <= 80 && adList.get((position / 8) - 1) == null) {
                    NativeAd fbAd = feedNativeAd.getAd();
                    adList.set((position / 8) - 1, fbAd);
                }
                NativeAd fbAd;
                if (position < 80) {
                    fbAd = adList.get(((position / 8) % 10) - 1);
                } else {
                    fbAd = adList.get(((position / 8) % 10));
                }

                if (fbAd == null) {
                    ((AdViewHolder) holder).adContainerView.setVisibility(View.GONE);
                    return;
                }
                ((AdViewHolder) holder).adContainerView.setVisibility(View.VISIBLE);
                ((AdViewHolder) holder).nativeAdTitle.setText(fbAd.getAdTitle());
                ((AdViewHolder) holder).nativeAdSocialContext.setText(fbAd.getAdSocialContext());
                ((AdViewHolder) holder).nativeAdBody.setText(fbAd.getAdBody());
                ((AdViewHolder) holder).nativeAdCallToAction.setText(fbAd.getAdCallToAction());

                // Download and display the ad icon.
                NativeAd.Image adIcon = fbAd.getAdIcon();
                NativeAd.downloadAndDisplayImage(adIcon, ((AdViewHolder) holder).nativeAdIcon);

                // Download and display the cover image.
                ((AdViewHolder) holder).nativeAdMedia.setNativeAd(fbAd);

                // Add the AdChoices icon
                if (!isAdChoiceAdded) {
                    AdChoicesView adChoicesView = new AdChoicesView(mContext, fbAd, true);
                    ((AdViewHolder) holder).adChoicesContainer.addView(adChoicesView);
                    isAdChoiceAdded = true;
                }

                // Register the Title and CTA button to listen for clicks.
                List<View> clickableViews = new ArrayList<>();
                clickableViews.add(((AdViewHolder) holder).nativeAdTitle);
                clickableViews.add(((AdViewHolder) holder).nativeAdCallToAction);
                fbAd.registerViewForInteraction(((AdViewHolder) holder).adContainerView);
            } catch (Exception e) {

            }
        } else if (holder instanceof HeaderViewHolder) {
            addArticleItem((HeaderViewHolder) holder, position);
        } else {
            addArticleItem((FeedViewHolder) holder, position);
        }
    }

    private void addArticleItem(final HeaderViewHolder holder, final int position) {
        holder.txvArticleTitle.setText(articleDataModelsNew.get(position).getTitle());

        if (StringUtils.isNullOrEmpty(articleDataModelsNew.get(position).getReason())) {
            holder.forYouInfoLL.setVisibility(View.GONE);
        } else {
            holder.forYouInfoLL.setVisibility(View.VISIBLE);
            holder.forYouInfoLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("For You", "for you article -- " + articleDataModelsNew.get(position).getTitle());
                    ForYouInfoDialogFragment forYouInfoDialogFragment = new ForYouInfoDialogFragment();
                    FragmentManager fm = ((ArticleListingActivity) mContext).getSupportFragmentManager();
                    Bundle _args = new Bundle();
                    _args.putString("reason", articleDataModelsNew.get(position).getReason());
                    _args.putString("articleId", articleDataModelsNew.get(position).getId());
                    _args.putInt("position", position);
                    forYouInfoDialogFragment.setArguments(_args);
                    forYouInfoDialogFragment.setCancelable(true);
                    forYouInfoDialogFragment.setListener((ForYouInfoDialogFragment.IForYourArticleRemove) mContext);
                    forYouInfoDialogFragment.show(fm, "For You");
                }
            });
        }

        if (null == articleDataModelsNew.get(position).getArticleCount() || "0".equals(articleDataModelsNew.get(position).getArticleCount())) {
            holder.viewCountTextView.setVisibility(View.GONE);
        } else {
            holder.viewCountTextView.setVisibility(View.VISIBLE);
            holder.viewCountTextView.setText(articleDataModelsNew.get(position).getArticleCount());
        }

        if (null == articleDataModelsNew.get(position).getCommentsCount() || "0".equals(articleDataModelsNew.get(position).getCommentsCount())) {
            holder.commentCountTextView.setVisibility(View.GONE);
        } else {
            holder.commentCountTextView.setVisibility(View.VISIBLE);
            holder.commentCountTextView.setText(articleDataModelsNew.get(position).getCommentsCount());
        }

        if (null == articleDataModelsNew.get(position).getLikesCount() || "0".equals(articleDataModelsNew.get(position).getLikesCount())) {
            holder.recommendCountTextView.setVisibility(View.GONE);
        } else {
            holder.recommendCountTextView.setVisibility(View.VISIBLE);
            holder.recommendCountTextView.setText(articleDataModelsNew.get(position).getLikesCount());
        }

        if (StringUtils.isNullOrEmpty(articleDataModelsNew.get(position).getUserName()) || articleDataModelsNew.get(position).getUserName().toString().trim().equalsIgnoreCase("")) {
            holder.txvAuthorName.setText("NA");
        } else {
            holder.txvAuthorName.setText(articleDataModelsNew.get(position).getUserName());
        }

        try {
            if (!StringUtils.isNullOrEmpty(articleDataModelsNew.get(position).getVideoUrl())
                    && (articleDataModelsNew.get(position).getImageUrl().getThumbMax() == null || articleDataModelsNew.get(position).getImageUrl().getThumbMax().contains("default.jp"))) {
                Picasso.with(mContext).load(AppUtils.getYoutubeThumbnailURLMomspresso(articleDataModelsNew.get(position).getVideoUrl())).placeholder(R.drawable.default_article).into(holder.articleImageView);
            } else {
                if (!StringUtils.isNullOrEmpty(articleDataModelsNew.get(position).getImageUrl().getThumbMax())) {
                    Picasso.with(mContext).load(articleDataModelsNew.get(position).getImageUrl().getThumbMax())
                            .placeholder(R.drawable.default_article).error(R.drawable.default_article).into(holder.articleImageView);
                } else {
                    holder.articleImageView.setBackgroundResource(R.drawable.default_article);
                }
            }
        } catch (Exception e) {
            holder.articleImageView.setBackgroundResource(R.drawable.default_article);
        }


        if (!StringUtils.isNullOrEmpty(articleDataModelsNew.get(position).getVideoUrl())) {
            holder.videoIndicatorImageView.setVisibility(View.VISIBLE);
        } else {
            holder.videoIndicatorImageView.setVisibility(View.INVISIBLE);
        }

        if ("1".equals(articleDataModelsNew.get(position).getIsMomspresso())) {
            holder.bookmarkArticleImageView.setVisibility(View.INVISIBLE);
            holder.watchLaterImageView.setVisibility(View.VISIBLE);

            if (articleDataModelsNew.get(position).getListingWatchLaterStatus() == 0) {
                holder.watchLaterImageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_watch));
            } else {
                holder.watchLaterImageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_watch_added));
            }
        } else {
            holder.bookmarkArticleImageView.setVisibility(View.VISIBLE);
            holder.watchLaterImageView.setVisibility(View.INVISIBLE);

            if (articleDataModelsNew.get(position).getListingBookmarkStatus() == 0) {
                holder.bookmarkArticleImageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_bookmark));
            } else {
                holder.bookmarkArticleImageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_bookmarked));
            }
        }

        holder.watchLaterImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addRemoveWatchLater(position, holder);
                Utils.pushWatchLaterArticleEvent(mContext, "ArticleListing", SharedPrefUtils.getUserDetailModel(mContext).getDynamoId() + "",
                        articleDataModelsNew.get(position).getId(), articleDataModelsNew.get(position).getUserId() + "~" + articleDataModelsNew.get(position).getUserName());
            }
        });

        holder.bookmarkArticleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addRemoveBookmark(position, holder);
                Utils.pushBookmarkArticleEvent(mContext, "ArticleListing", SharedPrefUtils.getUserDetailModel(mContext).getDynamoId() + "",
                        articleDataModelsNew.get(position).getId(), articleDataModelsNew.get(position).getUserId() + "~" + articleDataModelsNew.get(position).getUserName());
            }
        });
    }

    private void addArticleItem(final AdViewHolder holder, final int position) {
        holder.txvArticleTitle.setText(articleDataModelsNew.get(position).getTitle());

        if (StringUtils.isNullOrEmpty(articleDataModelsNew.get(position).getReason())) {
            holder.forYouInfoLL.setVisibility(View.GONE);
        } else {
            holder.forYouInfoLL.setVisibility(View.VISIBLE);
            holder.forYouInfoLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("For You", "for you article -- " + articleDataModelsNew.get(position).getTitle());
                    ForYouInfoDialogFragment forYouInfoDialogFragment = new ForYouInfoDialogFragment();
                    FragmentManager fm = ((ArticleListingActivity) mContext).getSupportFragmentManager();
                    Bundle _args = new Bundle();
                    _args.putString("reason", articleDataModelsNew.get(position).getReason());
                    _args.putString("articleId", articleDataModelsNew.get(position).getId());
                    _args.putInt("position", position);
                    forYouInfoDialogFragment.setArguments(_args);
                    forYouInfoDialogFragment.setCancelable(true);
                    forYouInfoDialogFragment.setListener((ForYouInfoDialogFragment.IForYourArticleRemove) mContext);
                    forYouInfoDialogFragment.show(fm, "For You");
                }
            });
        }

        if (null == articleDataModelsNew.get(position).getArticleCount() || "0".equals(articleDataModelsNew.get(position).getArticleCount())) {
            holder.viewCountTextView.setVisibility(View.GONE);
        } else {
            holder.viewCountTextView.setVisibility(View.VISIBLE);
            holder.viewCountTextView.setText(articleDataModelsNew.get(position).getArticleCount());
        }

        if (null == articleDataModelsNew.get(position).getCommentsCount() || "0".equals(articleDataModelsNew.get(position).getCommentsCount())) {
            holder.commentCountTextView.setVisibility(View.GONE);
        } else {
            holder.commentCountTextView.setVisibility(View.VISIBLE);
            holder.commentCountTextView.setText(articleDataModelsNew.get(position).getCommentsCount());
        }

        if (null == articleDataModelsNew.get(position).getLikesCount() || "0".equals(articleDataModelsNew.get(position).getLikesCount())) {
            holder.recommendCountTextView.setVisibility(View.GONE);
        } else {
            holder.recommendCountTextView.setVisibility(View.VISIBLE);
            holder.recommendCountTextView.setText(articleDataModelsNew.get(position).getLikesCount());
        }

        if (StringUtils.isNullOrEmpty(articleDataModelsNew.get(position).getUserName()) || articleDataModelsNew.get(position).getUserName().toString().trim().equalsIgnoreCase("")) {
            holder.txvAuthorName.setText("NA");
        } else {
            holder.txvAuthorName.setText(articleDataModelsNew.get(position).getUserName());
        }

        try {
            if (!StringUtils.isNullOrEmpty(articleDataModelsNew.get(position).getVideoUrl())
                    && (articleDataModelsNew.get(position).getImageUrl().getThumbMax() == null || articleDataModelsNew.get(position).getImageUrl().getThumbMax().contains("default.jp"))) {
                Picasso.with(mContext).load(AppUtils.getYoutubeThumbnailURLMomspresso(articleDataModelsNew.get(position).getVideoUrl())).placeholder(R.drawable.default_article).into(holder.articleImageView);
            } else {
                if (!StringUtils.isNullOrEmpty(articleDataModelsNew.get(position).getImageUrl().getThumbMax())) {
                    Picasso.with(mContext).load(articleDataModelsNew.get(position).getImageUrl().getThumbMax())
                            .placeholder(R.drawable.default_article).error(R.drawable.default_article).into(holder.articleImageView);
                } else {
                    holder.articleImageView.setBackgroundResource(R.drawable.default_article);
                }
            }
        } catch (Exception e) {
            holder.articleImageView.setBackgroundResource(R.drawable.default_article);
        }


        if (!StringUtils.isNullOrEmpty(articleDataModelsNew.get(position).getVideoUrl())) {
            holder.videoIndicatorImageView.setVisibility(View.VISIBLE);
        } else {
            holder.videoIndicatorImageView.setVisibility(View.INVISIBLE);
        }

        if ("1".equals(articleDataModelsNew.get(position).getIsMomspresso())) {
            holder.bookmarkArticleImageView.setVisibility(View.INVISIBLE);
            holder.watchLaterImageView.setVisibility(View.VISIBLE);

            if (articleDataModelsNew.get(position).getListingWatchLaterStatus() == 0) {
                holder.watchLaterImageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_watch));
            } else {
                holder.watchLaterImageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_watch_added));
            }
        } else {
            holder.bookmarkArticleImageView.setVisibility(View.VISIBLE);
            holder.watchLaterImageView.setVisibility(View.INVISIBLE);

            if (articleDataModelsNew.get(position).getListingBookmarkStatus() == 0) {
                holder.bookmarkArticleImageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_bookmark));
            } else {
                holder.bookmarkArticleImageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_bookmarked));
            }
        }

        holder.watchLaterImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addRemoveWatchLater(position, holder);
                Utils.pushWatchLaterArticleEvent(mContext, "ArticleListing", SharedPrefUtils.getUserDetailModel(mContext).getDynamoId() + "",
                        articleDataModelsNew.get(position).getId(), articleDataModelsNew.get(position).getUserId() + "~" + articleDataModelsNew.get(position).getUserName());
            }
        });

        holder.bookmarkArticleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addRemoveBookmark(position, holder);
                Utils.pushBookmarkArticleEvent(mContext, "ArticleListing", SharedPrefUtils.getUserDetailModel(mContext).getDynamoId() + "",
                        articleDataModelsNew.get(position).getId(), articleDataModelsNew.get(position).getUserId() + "~" + articleDataModelsNew.get(position).getUserName());
            }
        });
    }

    private void addArticleItem(final FeedViewHolder holder, final int position) {
        holder.txvArticleTitle.setText(articleDataModelsNew.get(position).getTitle());

        if (StringUtils.isNullOrEmpty(articleDataModelsNew.get(position).getReason())) {
            holder.forYouInfoLL.setVisibility(View.GONE);
        } else {
            holder.forYouInfoLL.setVisibility(View.VISIBLE);
            holder.forYouInfoLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("For You", "for you article -- " + articleDataModelsNew.get(position).getTitle());
                    ForYouInfoDialogFragment forYouInfoDialogFragment = new ForYouInfoDialogFragment();
                    FragmentManager fm = ((ArticleListingActivity) mContext).getSupportFragmentManager();
                    Bundle _args = new Bundle();
                    _args.putString("reason", articleDataModelsNew.get(position).getReason());
                    _args.putString("articleId", articleDataModelsNew.get(position).getId());
                    _args.putInt("position", position);
                    forYouInfoDialogFragment.setArguments(_args);
                    forYouInfoDialogFragment.setCancelable(true);
                    forYouInfoDialogFragment.setListener((ForYouInfoDialogFragment.IForYourArticleRemove) mContext);
                    forYouInfoDialogFragment.show(fm, "For You");
                }
            });
        }

        if (null == articleDataModelsNew.get(position).getArticleCount() || "0".equals(articleDataModelsNew.get(position).getArticleCount())) {
            holder.viewCountTextView.setVisibility(View.GONE);
        } else {
            holder.viewCountTextView.setVisibility(View.VISIBLE);
            holder.viewCountTextView.setText(articleDataModelsNew.get(position).getArticleCount());
        }

        if (null == articleDataModelsNew.get(position).getCommentsCount() || "0".equals(articleDataModelsNew.get(position).getCommentsCount())) {
            holder.commentCountTextView.setVisibility(View.GONE);
        } else {
            holder.commentCountTextView.setVisibility(View.VISIBLE);
            holder.commentCountTextView.setText(articleDataModelsNew.get(position).getCommentsCount());
        }

        if (null == articleDataModelsNew.get(position).getLikesCount() || "0".equals(articleDataModelsNew.get(position).getLikesCount())) {
            holder.recommendCountTextView.setVisibility(View.GONE);
        } else {
            holder.recommendCountTextView.setVisibility(View.VISIBLE);
            holder.recommendCountTextView.setText(articleDataModelsNew.get(position).getLikesCount());
        }

        if (StringUtils.isNullOrEmpty(articleDataModelsNew.get(position).getUserName()) || articleDataModelsNew.get(position).getUserName().toString().trim().equalsIgnoreCase("")) {
            holder.txvAuthorName.setText("NA");
        } else {
            holder.txvAuthorName.setText(articleDataModelsNew.get(position).getUserName());
        }

        try {
            if (!StringUtils.isNullOrEmpty(articleDataModelsNew.get(position).getVideoUrl())
                    && (articleDataModelsNew.get(position).getImageUrl().getThumbMax() == null || articleDataModelsNew.get(position).getImageUrl().getThumbMax().contains("default.jp"))) {
                Picasso.with(mContext).load(AppUtils.getYoutubeThumbnailURLMomspresso(articleDataModelsNew.get(position).getVideoUrl())).placeholder(R.drawable.default_article).into(holder.articleImageView);
            } else {
                if (!StringUtils.isNullOrEmpty(articleDataModelsNew.get(position).getImageUrl().getThumbMax())) {
                    Picasso.with(mContext).load(articleDataModelsNew.get(position).getImageUrl().getThumbMax())
                            .placeholder(R.drawable.default_article).error(R.drawable.default_article).into(holder.articleImageView);
                } else {
                    holder.articleImageView.setBackgroundResource(R.drawable.default_article);
                }
            }
        } catch (Exception e) {
            holder.articleImageView.setBackgroundResource(R.drawable.default_article);
        }


        if (!StringUtils.isNullOrEmpty(articleDataModelsNew.get(position).getVideoUrl())) {
            holder.videoIndicatorImageView.setVisibility(View.VISIBLE);
        } else {
            holder.videoIndicatorImageView.setVisibility(View.INVISIBLE);
        }

        if ("1".equals(articleDataModelsNew.get(position).getIsMomspresso())) {
            holder.bookmarkArticleImageView.setVisibility(View.INVISIBLE);
            holder.watchLaterImageView.setVisibility(View.VISIBLE);

            if (articleDataModelsNew.get(position).getListingWatchLaterStatus() == 0) {
                holder.watchLaterImageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_watch));
            } else {
                holder.watchLaterImageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_watch_added));
            }
        } else {
            holder.bookmarkArticleImageView.setVisibility(View.VISIBLE);
            holder.watchLaterImageView.setVisibility(View.INVISIBLE);

            if (articleDataModelsNew.get(position).getListingBookmarkStatus() == 0) {
                holder.bookmarkArticleImageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_bookmark));
            } else {
                holder.bookmarkArticleImageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_bookmarked));
            }
        }

        holder.watchLaterImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addRemoveWatchLater(position, holder);
                Utils.pushWatchLaterArticleEvent(mContext, "ArticleListing", SharedPrefUtils.getUserDetailModel(mContext).getDynamoId() + "",
                        articleDataModelsNew.get(position).getId(), articleDataModelsNew.get(position).getUserId() + "~" + articleDataModelsNew.get(position).getUserName());
            }
        });

        holder.bookmarkArticleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addRemoveBookmark(position, holder);
                Utils.pushBookmarkArticleEvent(mContext, "ArticleListing", SharedPrefUtils.getUserDetailModel(mContext).getDynamoId() + "",
                        articleDataModelsNew.get(position).getId(), articleDataModelsNew.get(position).getUserId() + "~" + articleDataModelsNew.get(position).getUserName());
            }
        });
    }

    public class AdViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        LinearLayout adContainerView;
        ImageView nativeAdIcon;
        TextView nativeAdTitle;
        MediaView nativeAdMedia;
        TextView nativeAdSocialContext;
        TextView nativeAdBody;
        Button nativeAdCallToAction;
        LinearLayout adChoicesContainer;

        FrameLayout fbAdArticleView;
        TextView txvArticleTitle;
        TextView txvAuthorName;
        ImageView articleImageView;
        ImageView videoIndicatorImageView;
        LinearLayout forYouInfoLL;
        TextView viewCountTextView;
        TextView commentCountTextView;
        TextView recommendCountTextView;
        TextView authorTypeTextView;
        ImageView bookmarkArticleImageView;
        ImageView watchLaterImageView;

        AdViewHolder(View adView) {
            super(adView);
            adContainerView = (LinearLayout) adView.findViewById(R.id.adContainerView);
            nativeAdIcon = (ImageView) adView.findViewById(R.id.native_ad_icon);
            nativeAdTitle = (TextView) adView.findViewById(R.id.native_ad_title);
            nativeAdMedia = (MediaView) adView.findViewById(R.id.native_ad_media);
            nativeAdSocialContext = (TextView) adView.findViewById(R.id.native_ad_social_context);
            nativeAdBody = (TextView) adView.findViewById(R.id.native_ad_body);
            nativeAdCallToAction = (Button) adView.findViewById(R.id.native_ad_call_to_action);
            adChoicesContainer = (LinearLayout) adView.findViewById(R.id.ad_choices_container);

            fbAdArticleView = (FrameLayout) adView.findViewById(R.id.fbAdArticleView);
            txvArticleTitle = (TextView) adView.findViewById(R.id.txvArticleTitle);
            txvAuthorName = (TextView) adView.findViewById(R.id.txvAuthorName);
            articleImageView = (ImageView) adView.findViewById(R.id.articleImageView);
            videoIndicatorImageView = (ImageView) adView.findViewById(R.id.videoIndicatorImageView);
            forYouInfoLL = (LinearLayout) adView.findViewById(R.id.forYouInfoLL);
            viewCountTextView = (TextView) adView.findViewById(R.id.viewCountTextView);
            commentCountTextView = (TextView) adView.findViewById(R.id.commentCountTextView);
            recommendCountTextView = (TextView) adView.findViewById(R.id.recommendCountTextView);
            authorTypeTextView = (TextView) adView.findViewById(R.id.authorTypeTextView);
            bookmarkArticleImageView = (ImageView) adView.findViewById(R.id.bookmarkArticleImageView);
            watchLaterImageView = (ImageView) adView.findViewById(R.id.watchLaterImageView);
            fbAdArticleView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mListener.onRecyclerItemClick(v, getAdapterPosition());
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
        TextView authorTypeTextView;
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
            authorTypeTextView = (TextView) view.findViewById(R.id.authorTypeTextView);
            bookmarkArticleImageView = (ImageView) view.findViewById(R.id.bookmarkArticleImageView);
            watchLaterImageView = (ImageView) view.findViewById(R.id.watchLaterImageView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mListener.onRecyclerItemClick(v, getAdapterPosition());
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
        TextView authorTypeTextView;
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
            authorTypeTextView = (TextView) view.findViewById(R.id.authorTypeTextView);
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
            mListener.onRecyclerItemClick(v, getAdapterPosition());
        }
    }

    public interface RecyclerViewClickListener {
        void onRecyclerItemClick(View view, int position);
    }

    private void addRemoveBookmark(int position, RecyclerView.ViewHolder holder) {
        if (articleDataModelsNew.get(position).getListingBookmarkStatus() == 0) {
            ArticleDetailRequest articleDetailRequest = new ArticleDetailRequest();
            articleDetailRequest.setArticleId(articleDataModelsNew.get(position).getId());
            String jsonString = new Gson().toJson(articleDetailRequest);
            new MainArticleRecyclerViewAdapter.AddRemoveBookmarkAsyncTask(holder, "bookmarkArticle", position).execute(jsonString, "bookmarkArticle");
        } else {
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

        // The variable is moved here, we only need it here while displaying the
        // progress dialog.
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
                // is output buffer writter
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Accept", "application/json");
                urlConnection.addRequestProperty("id", SharedPrefUtils.getUserDetailModel(mContext).getDynamoId());
                urlConnection.addRequestProperty("mc4kToken", SharedPrefUtils.getUserDetailModel(mContext).getMc4kToken());

                //set headers and method
                Writer writer = new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream(), "UTF-8"));
                writer.write(JsonDATA);
                // json data
                writer.close();
                InputStream inputStream = urlConnection.getInputStream();
                //input stream
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String inputLine;
                while ((inputLine = reader.readLine()) != null)
                    buffer.append(inputLine + "\n");
                if (buffer.length() == 0) {
                    // Stream was empty. No point in parsing.
                    return null;
                }
                JsonResponse = buffer.toString();

                Log.i("RESPONSE " + type, JsonResponse);
                //send to post execute
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
                resetFollowUnfollowStatus();
                return;
            }
            try {
                AddBookmarkResponse responseData = new Gson().fromJson(result, AddBookmarkResponse.class);
//                if ((responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) ||
//                        (responseData.getCode() == 200 && Constants.FAILURE.equals(responseData.getStatus()) && "already bookmarked".equals(responseData.getReason()))) {
                if (responseData.getCode() == 200) {
                    for (int i = 0; i < articleDataModelsNew.size(); i++) {
                        if (articleDataModelsNew.get(i).getId().equals(responseData.getData().getResult().getArticleId())) {
                            if ("bookmarkArticle".equals(type)) {
                                articleDataModelsNew.get(i).setListingBookmarkStatus(1);
                                articleDataModelsNew.get(i).setBookmarkId(responseData.getData().getResult().getBookmarkId());
                                if (viewHolder instanceof FeedViewHolder) {
                                    ((FeedViewHolder) viewHolder).bookmarkArticleImageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_bookmarked));
                                } else if (viewHolder instanceof HeaderViewHolder) {
                                    ((HeaderViewHolder) viewHolder).bookmarkArticleImageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_bookmarked));
                                } else {
                                    ((AdViewHolder) viewHolder).bookmarkArticleImageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_bookmarked));
                                }

                            } else if ("unbookmarkArticle".equals(type)) {
                                articleDataModelsNew.get(i).setListingBookmarkStatus(0);
                                articleDataModelsNew.get(i).setBookmarkId("");
                                if (viewHolder instanceof FeedViewHolder) {
                                    ((FeedViewHolder) viewHolder).bookmarkArticleImageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_bookmark));
                                } else if (viewHolder instanceof HeaderViewHolder) {
                                    ((HeaderViewHolder) viewHolder).bookmarkArticleImageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_bookmark));
                                } else {
                                    ((AdViewHolder) viewHolder).bookmarkArticleImageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_bookmark));
                                }
                            } else if ("bookmarkVideo".equals(type)) {
                                articleDataModelsNew.get(i).setListingWatchLaterStatus(1);
                                articleDataModelsNew.get(i).setBookmarkId(responseData.getData().getResult().getBookmarkId());
                                if (viewHolder instanceof FeedViewHolder) {
                                    ((FeedViewHolder) viewHolder).watchLaterImageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_watch_added));
                                } else if (viewHolder instanceof HeaderViewHolder) {
                                    ((HeaderViewHolder) viewHolder).watchLaterImageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_watch_added));
                                } else {
                                    ((AdViewHolder) viewHolder).watchLaterImageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_watch_added));
                                }
                            } else if ("unbookmarkVideo".equals(type)) {
                                articleDataModelsNew.get(i).setListingWatchLaterStatus(0);
                                articleDataModelsNew.get(i).setBookmarkId("");
                                if (viewHolder instanceof FeedViewHolder) {
                                    ((FeedViewHolder) viewHolder).watchLaterImageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_watch));
                                } else if (viewHolder instanceof HeaderViewHolder) {
                                    ((HeaderViewHolder) viewHolder).watchLaterImageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_watch));
                                } else {
                                    ((AdViewHolder) viewHolder).watchLaterImageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_watch));
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
            if (type.equals("bookmark")) {
                if (viewHolder instanceof FeedViewHolder) {
                    ((FeedViewHolder) viewHolder).bookmarkArticleImageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_bookmark));
                } else if (viewHolder instanceof HeaderViewHolder) {
                    ((HeaderViewHolder) viewHolder).bookmarkArticleImageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_bookmark));
                } else {
                    ((AdViewHolder) viewHolder).bookmarkArticleImageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_bookmark));
                }
            } else if ("unbookmarkArticle".equals(type)) {
                if (viewHolder instanceof FeedViewHolder) {
                    ((FeedViewHolder) viewHolder).bookmarkArticleImageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_bookmarked));
                } else if (viewHolder instanceof HeaderViewHolder) {
                    ((HeaderViewHolder) viewHolder).bookmarkArticleImageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_bookmarked));
                } else {
                    ((AdViewHolder) viewHolder).bookmarkArticleImageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_bookmarked));
                }
            } else if ("bookmarkVideo".equals(type)) {
                if (viewHolder instanceof FeedViewHolder) {
                    ((FeedViewHolder) viewHolder).watchLaterImageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_watch));
                } else if (viewHolder instanceof HeaderViewHolder) {
                    ((HeaderViewHolder) viewHolder).watchLaterImageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_watch));
                } else {
                    ((AdViewHolder) viewHolder).watchLaterImageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_watch));
                }
            } else if ("unbookmarkVideo".equals(type)) {
                if (viewHolder instanceof FeedViewHolder) {
                    ((FeedViewHolder) viewHolder).watchLaterImageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_watch_added));
                } else if (viewHolder instanceof HeaderViewHolder) {
                    ((HeaderViewHolder) viewHolder).watchLaterImageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_watch_added));
                } else {
                    ((AdViewHolder) viewHolder).watchLaterImageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_watch_added));
                }
            }
        }

    }
}
