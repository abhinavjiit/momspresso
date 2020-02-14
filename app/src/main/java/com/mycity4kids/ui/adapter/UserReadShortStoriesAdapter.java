package com.mycity4kids.ui.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mycity4kids.utils.DateTimeUtils;
import com.mycity4kids.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.models.response.ArticleListingResult;
import com.mycity4kids.widget.FeedNativeAd;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by hemant on 19/7/17.
 */
public class UserReadShortStoriesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static int ARTICLE = 0;
    private static int AD = 1;
    private Context mContext;
    private LayoutInflater mInflator;
    private ArrayList<ArticleListingResult> articleDataModelsNew;
    private SSRecyclerViewClickListener mListener;
    private boolean isPrivateProfile;
    private final FeedNativeAd feedNativeAd;
    private boolean isAdChoiceAdded = false;
//    private List<NativeAd> adList = new ArrayList<>(10);

    public UserReadShortStoriesAdapter(Context pContext, SSRecyclerViewClickListener listener, boolean isPrivateProfile, FeedNativeAd feedNativeAd) {

        mInflator = (LayoutInflater) pContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = pContext;
        this.mListener = listener;
        this.isPrivateProfile = isPrivateProfile;
        this.feedNativeAd = feedNativeAd;
//        for (int i = 0; i < 10; i++) {
//            adList.add(null);
//        }
    }

    public void setListData(ArrayList<ArticleListingResult> mParentingLists) {
        articleDataModelsNew = mParentingLists;
    }

//    @Override
//    public int getItemViewType(int position) {
//        if (position != 0 && position % 8 == 0) {
//            return ARTICLE;
//        } else {
//            return ARTICLE;
//        }
//    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        if (viewType == AD) {
//            View v0 = mInflator.inflate(R.layout.user_published_fb_ad_list_item, parent, false);
//            return new AdViewHolder(v0);
//        } else {
        UserPublishedArticleViewHolder viewHolder = null;
        View v0 = mInflator.inflate(R.layout.user_published_short_stories_list_item, parent, false);
        viewHolder = new UserPublishedArticleViewHolder(v0, mListener);
        return viewHolder;
//        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
//        if (holder instanceof AdViewHolder) {
//            addArticleItem((AdViewHolder) holder, position);
//            if (position <= 80 && adList.get((position / 8) - 1) == null) {
//                NativeAd fbAd = feedNativeAd.getAd();
//                adList.set((position / 8) - 1, fbAd);
//            }
//            NativeAd fbAd;
//            if (position < 80) {
//                fbAd = adList.get(((position / 8) % 10) - 1);
//            } else {
//                fbAd = adList.get(((position / 8) % 10));
//            }
//            if (fbAd == null) {
//                ((AdViewHolder) holder).adContainerView.setVisibility(View.GONE);
//                return;
//            }
//            ((AdViewHolder) holder).adContainerView.setVisibility(View.VISIBLE);
//            ((AdViewHolder) holder).nativeAdTitle.setText(fbAd.getAdTitle());
//            ((AdViewHolder) holder).nativeAdSocialContext.setText(fbAd.getAdSocialContext());
//            ((AdViewHolder) holder).nativeAdBody.setText(fbAd.getAdBody());
//            ((AdViewHolder) holder).nativeAdCallToAction.setText(fbAd.getAdCallToAction());
//
//            // Download and display the ad icon.
//            NativeAd.Image adIcon = fbAd.getAdIcon();
//            NativeAd.downloadAndDisplayImage(adIcon, ((AdViewHolder) holder).nativeAdIcon);
//
//            // Download and display the cover image.
////            ((AdViewHolder) holder).nativeAdMedia.setNativeAd(feedNativeAd.getAd());
//
//            // Add the AdChoices icon
//            if (!isAdChoiceAdded) {
//                AdChoicesView adChoicesView = new AdChoicesView(mContext, fbAd, true);
//                ((AdViewHolder) holder).adChoicesContainer.addView(adChoicesView);
//                isAdChoiceAdded = true;
//            }
//
//            // Register the Title and CTA button to listen for clicks.
//            List<View> clickableViews = new ArrayList<>();
//            clickableViews.add(((AdViewHolder) holder).nativeAdTitle);
//            clickableViews.add(((AdViewHolder) holder).nativeAdCallToAction);
//            fbAd.registerViewForInteraction(((AdViewHolder) holder).adContainerView);

//        } else {

        ((UserPublishedArticleViewHolder) holder).txvArticleTitle.setText(articleDataModelsNew.get(position).getTitle());

        ((UserPublishedArticleViewHolder) holder).commentCountTextView.setText(articleDataModelsNew.get(position).getCommentsCount());
        if (StringUtils.isNullOrEmpty(articleDataModelsNew.get(position).getCommentsCount()) || "0".equals(articleDataModelsNew.get(position).getCommentsCount())) {
            ((UserPublishedArticleViewHolder) holder).commentCountTextView.setVisibility(View.GONE);
            ((UserPublishedArticleViewHolder) holder).separatorView2.setVisibility(View.GONE);
        } else {
            ((UserPublishedArticleViewHolder) holder).commentCountTextView.setVisibility(View.VISIBLE);
            ((UserPublishedArticleViewHolder) holder).separatorView2.setVisibility(View.VISIBLE);
        }
        ((UserPublishedArticleViewHolder) holder).recommendCountTextView.setText(articleDataModelsNew.get(position).getLikesCount());
        if (StringUtils.isNullOrEmpty(articleDataModelsNew.get(position).getLikesCount()) || "0".equals(articleDataModelsNew.get(position).getLikesCount())) {
            ((UserPublishedArticleViewHolder) holder).recommendCountTextView.setVisibility(View.INVISIBLE);
        } else {
            ((UserPublishedArticleViewHolder) holder).recommendCountTextView.setVisibility(View.VISIBLE);
        }
        if (isPrivateProfile) {
            ((UserPublishedArticleViewHolder) holder).editPublishedTextView.setVisibility(View.GONE);
        } else {
            ((UserPublishedArticleViewHolder) holder).editPublishedTextView.setVisibility(View.GONE);
        }

        try {
            Calendar calendar1 = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
            SimpleDateFormat sdf1 = new SimpleDateFormat("HH:mm", Locale.US);
            calendar1.setTimeInMillis(articleDataModelsNew.get(position).getCreatedTime() * 1000);

            Long diff = System.currentTimeMillis() - articleDataModelsNew.get(position).getCreatedTime() * 1000;
            if (diff / (1000 * 60 * 60) > 24 && !sdf.format(System.currentTimeMillis()).equals(sdf.format((articleDataModelsNew.get(position).getCreatedTime() * 1000)))) {
                ((UserPublishedArticleViewHolder) holder).txvPublishDate.setText(DateTimeUtils.getDateFromTimestamp(articleDataModelsNew.get(position).getCreatedTime()));
            } else {
                ((UserPublishedArticleViewHolder) holder).txvPublishDate.setText(sdf1.format(calendar1.getTime()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
//        }
    }

    private void addArticleItem(AdViewHolder holder, int position) {
        if (null != articleDataModelsNew.get(position).getImageUrl()) {
            Picasso.get().load(articleDataModelsNew.get(position).getImageUrl().getThumbMin()).
                    placeholder(R.drawable.default_article).error(R.drawable.default_article).into(holder.articleImageView);
        } else {
            holder.articleImageView.setBackgroundResource(R.drawable.article_default);
        }

        holder.txvArticleTitle.setText(articleDataModelsNew.get(position).getTitle());

        holder.commentCountTextView.setText(articleDataModelsNew.get(position).getCommentsCount());
        if (StringUtils.isNullOrEmpty(articleDataModelsNew.get(position).getCommentsCount()) || "0".equals(articleDataModelsNew.get(position).getCommentsCount())) {
            holder.commentCountTextView.setVisibility(View.GONE);
            holder.separatorView2.setVisibility(View.GONE);
        } else {
            holder.commentCountTextView.setVisibility(View.VISIBLE);
            holder.separatorView2.setVisibility(View.VISIBLE);
        }
        holder.recommendCountTextView.setText(articleDataModelsNew.get(position).getLikesCount());
        if (StringUtils.isNullOrEmpty(articleDataModelsNew.get(position).getLikesCount()) || "0".equals(articleDataModelsNew.get(position).getLikesCount())) {
            holder.recommendCountTextView.setVisibility(View.INVISIBLE);

        } else {
            holder.recommendCountTextView.setVisibility(View.VISIBLE);

        }
        if (isPrivateProfile) {
            holder.editPublishedTextView.setVisibility(View.GONE);
        } else {
            holder.editPublishedTextView.setVisibility(View.GONE);
        }

        try {
            Calendar calendar1 = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
            SimpleDateFormat sdf1 = new SimpleDateFormat("HH:mm", Locale.US);
            calendar1.setTimeInMillis(articleDataModelsNew.get(position).getCreatedTime() * 1000);

            Long diff = System.currentTimeMillis() - articleDataModelsNew.get(position).getCreatedTime() * 1000;
            if (diff / (1000 * 60 * 60) > 24 && !sdf.format(System.currentTimeMillis()).equals(sdf.format((articleDataModelsNew.get(position).getCreatedTime() * 1000)))) {
                holder.txvPublishDate.setText(DateTimeUtils.getDateFromTimestamp(articleDataModelsNew.get(position).getCreatedTime()));
            } else {
                holder.txvPublishDate.setText(sdf1.format(calendar1.getTime()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return articleDataModelsNew == null ? 0 : articleDataModelsNew.size();
    }

//    public void refreshArticleList(ArrayList<SearchArticleResult> newList) {
//        this.articleDataModelsNew = newList;
//    }

    public class AdViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        LinearLayout adContainerView;
        ImageView nativeAdIcon;
        TextView nativeAdTitle;
        TextView nativeAdSocialContext;
        TextView nativeAdBody;
        Button nativeAdCallToAction;
        LinearLayout adChoicesContainer;

        RelativeLayout rootLayout;
        ImageView articleImageView;
        TextView txvArticleTitle;
        TextView txvPublishDate;
        TextView commentCountTextView;
        TextView recommendCountTextView;
        ImageView shareArticleImageView;
        TextView editPublishedTextView;
        View separatorView2;

        AdViewHolder(View adView) {
            super(adView);
            adContainerView = (LinearLayout) adView.findViewById(R.id.adContainerView);
            nativeAdIcon = (ImageView) adView.findViewById(R.id.native_ad_icon);
            nativeAdTitle = (TextView) adView.findViewById(R.id.native_ad_title);
//            nativeAdMedia = (MediaView) adView.findViewById(R.id.native_ad_media);
            nativeAdSocialContext = (TextView) adView.findViewById(R.id.native_ad_social_context);
            nativeAdBody = (TextView) adView.findViewById(R.id.native_ad_body);
            nativeAdCallToAction = (Button) adView.findViewById(R.id.native_ad_call_to_action);
            adChoicesContainer = (LinearLayout) adView.findViewById(R.id.ad_choices_container);

            rootLayout = (RelativeLayout) itemView.findViewById(R.id.rootLayout);
            txvArticleTitle = (TextView) itemView.findViewById(R.id.txvArticleTitle);
            txvPublishDate = (TextView) itemView.findViewById(R.id.txvPublishDate);
            commentCountTextView = (TextView) itemView.findViewById(R.id.commentCountTextView);
            recommendCountTextView = (TextView) itemView.findViewById(R.id.recommendCountTextView);
            articleImageView = (ImageView) itemView.findViewById(R.id.articleImageView);
            shareArticleImageView = (ImageView) itemView.findViewById(R.id.shareArticleImageView);
            editPublishedTextView = (TextView) itemView.findViewById(R.id.editPublishedTextView);
            separatorView2 = itemView.findViewById(R.id.separatorView2);
            shareArticleImageView.setOnClickListener(this);
            editPublishedTextView.setOnClickListener(this);
            rootLayout.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mListener.onShortStoryClick(v, getAdapterPosition());
        }
    }

    public class UserPublishedArticleViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView txvArticleTitle;
        TextView txvPublishDate;
        TextView commentCountTextView;
        TextView recommendCountTextView;
        ImageView shareArticleImageView;
        TextView editPublishedTextView;
        View separatorView2;

        public UserPublishedArticleViewHolder(View itemView, SSRecyclerViewClickListener listener) {
            super(itemView);
            txvArticleTitle = (TextView) itemView.findViewById(R.id.txvArticleTitle);
            txvPublishDate = (TextView) itemView.findViewById(R.id.txvPublishDate);
            commentCountTextView = (TextView) itemView.findViewById(R.id.commentCountTextView);
            recommendCountTextView = (TextView) itemView.findViewById(R.id.recommendCountTextView);
            shareArticleImageView = (ImageView) itemView.findViewById(R.id.shareArticleImageView);
            editPublishedTextView = (TextView) itemView.findViewById(R.id.editPublishedTextView);
            separatorView2 = itemView.findViewById(R.id.separatorView2);
            shareArticleImageView.setOnClickListener(this);
            editPublishedTextView.setOnClickListener(this);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mListener.onShortStoryClick(v, getAdapterPosition());
        }
    }

    public interface SSRecyclerViewClickListener {
        void onShortStoryClick(View view, int position);
    }

}