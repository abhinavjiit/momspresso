package com.mycity4kids.ui.adapter;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.kelltontech.utils.DateTimeUtils;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.models.response.ArticleListingResult;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.ui.activity.BloggerDashboardActivity;
import com.mycity4kids.ui.activity.SettingsActivity;
import com.mycity4kids.ui.activity.SplashActivity;
import com.mycity4kids.utils.AppUtils;
import com.squareup.picasso.Picasso;

import org.apmem.tools.layouts.FlowLayout;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

/**
 * @author deepanker.chaudhary
 */
public class NewArticlesListingAdapter extends BaseAdapter {

    private ArrayList<ArticleListingResult> mArticleListData;
    private Context mContext;
    private LayoutInflater mInflator;
    ArrayList<ArticleListingResult> articleDataModelsNew;

    private final float density;
    private String listingType = "";
    private int languageFeedChosen;

    public NewArticlesListingAdapter(Context pContext) {

        density = pContext.getResources().getDisplayMetrics().density;
        mInflator = (LayoutInflater) pContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = pContext;
        languageFeedChosen = Integer.parseInt(SharedPrefUtils.getUserDetailModel(pContext).getIsLangSelection());

    }

    public void setListData(ArrayList<ArticleListingResult> mParentingLists) {
        mArticleListData = mParentingLists;
    }

    public void setNewListData(ArrayList<ArticleListingResult> mParentingLists_new) {
        articleDataModelsNew = mParentingLists_new;
    }

    public void addNewListData(ArrayList<ArticleListingResult> mParentingLists_new) {
        articleDataModelsNew.addAll(mParentingLists_new);
    }

    public void setListingType(String listingType) {
        this.listingType = listingType;
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
    public int getItemViewType(int position) {
        if (1 == languageFeedChosen && position == 10) {
            Log.d("Manage", "Language");
            return 0;
        } else {
            if (1 != languageFeedChosen && position > 0 && position % 3 == 0) {
                Log.d("Manage", "Language");
                return 0;
            } else {
                return 1;
            }
        }
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {

        try {
            final ViewHolder holder;
            int type = getItemViewType(position);

            if (view == null) {
                holder = new ViewHolder();
                switch (type) {
                    case 0:
                        view = mInflator.inflate(R.layout.language_feed_article_listing_item, null);

                        holder.languageFeedTextView = (TextView) view.findViewById(R.id.languageFeedTextView);

                        holder.txvArticleTitle = (TextView) view.findViewById(R.id.txvArticleTitle);
                        holder.txvAuthorName = (TextView) view.findViewById(R.id.txvAuthorName);
                        holder.articleImageView = (ImageView) view.findViewById(R.id.articleImageView);
                        holder.authorImageView = (ImageView) view.findViewById(R.id.authorImageView);
                        holder.videoIndicatorImageView = (ImageView) view.findViewById(R.id.videoIndicatorImageView);
                        holder.forYouDescriptionTextView = (TextView) view.findViewById(R.id.forYouDescriptionTextView);

                        holder.viewCountTextView = (TextView) view.findViewById(R.id.viewCountTextView);
                        holder.commentCountTextView = (TextView) view.findViewById(R.id.commentCountTextView);
                        holder.recommendCountTextView = (TextView) view.findViewById(R.id.recommendCountTextView);

                        holder.authorTypeTextView = (TextView) view.findViewById(R.id.authorTypeTextView);
                        holder.rankTextView = (TextView) view.findViewById(R.id.rankTextView);

                        holder.flowLayout = (FlowLayout) view.findViewById(R.id.flowLayout);
                        holder.popularSubCatTextView1 = (TextView) view.findViewById(R.id.popularSubcatTextView_1);
                        holder.popularSubCatTextView2 = (TextView) view.findViewById(R.id.popularSubcatTextView_2);
                        holder.popularSubCatTextView3 = (TextView) view.findViewById(R.id.popularSubcatTextView_3);
                        holder.popularSubCatTextView4 = (TextView) view.findViewById(R.id.popularSubcatTextView_4);

                        holder.tvParentLL1 = (LinearLayout) view.findViewById(R.id.popularSubcatLL_1);
                        holder.tvParentLL2 = (LinearLayout) view.findViewById(R.id.popularSubcatLL_2);
                        holder.tvParentLL3 = (LinearLayout) view.findViewById(R.id.popularSubcatLL_3);
                        holder.tvParentLL4 = (LinearLayout) view.findViewById(R.id.popularSubcatLL_4);
                        break;
                    case 1:
                        view = mInflator.inflate(R.layout.new_article_listing_item, null);

                        holder.txvArticleTitle = (TextView) view.findViewById(R.id.txvArticleTitle);
                        holder.txvAuthorName = (TextView) view.findViewById(R.id.txvAuthorName);
                        holder.articleImageView = (ImageView) view.findViewById(R.id.articleImageView);
                        holder.authorImageView = (ImageView) view.findViewById(R.id.authorImageView);
                        holder.videoIndicatorImageView = (ImageView) view.findViewById(R.id.videoIndicatorImageView);
                        holder.forYouDescriptionTextView = (TextView) view.findViewById(R.id.forYouDescriptionTextView);

                        holder.viewCountTextView = (TextView) view.findViewById(R.id.viewCountTextView);
                        holder.commentCountTextView = (TextView) view.findViewById(R.id.commentCountTextView);
                        holder.recommendCountTextView = (TextView) view.findViewById(R.id.recommendCountTextView);

                        holder.authorTypeTextView = (TextView) view.findViewById(R.id.authorTypeTextView);
                        holder.rankTextView = (TextView) view.findViewById(R.id.rankTextView);

                        holder.flowLayout = (FlowLayout) view.findViewById(R.id.flowLayout);
                        holder.popularSubCatTextView1 = (TextView) view.findViewById(R.id.popularSubcatTextView_1);
                        holder.popularSubCatTextView2 = (TextView) view.findViewById(R.id.popularSubcatTextView_2);
                        holder.popularSubCatTextView3 = (TextView) view.findViewById(R.id.popularSubcatTextView_3);
                        holder.popularSubCatTextView4 = (TextView) view.findViewById(R.id.popularSubcatTextView_4);

                        holder.tvParentLL1 = (LinearLayout) view.findViewById(R.id.popularSubcatLL_1);
                        holder.tvParentLL2 = (LinearLayout) view.findViewById(R.id.popularSubcatLL_2);
                        holder.tvParentLL3 = (LinearLayout) view.findViewById(R.id.popularSubcatLL_3);
                        holder.tvParentLL4 = (LinearLayout) view.findViewById(R.id.popularSubcatLL_4);
                        break;
                }
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            ArrayList<Map<String, String>> topicsList;
            if (articleDataModelsNew.get(position).getTags() != null) {
                topicsList = articleDataModelsNew.get(position).getTags();
            } else {
                topicsList = new ArrayList<>();
            }

            if (topicsList.size() > 3) {
                holder.popularSubCatTextView1.setVisibility(View.VISIBLE);
                holder.popularSubCatTextView1.setText((String) topicsList.get(0).values().toArray()[0]);
                holder.popularSubCatTextView2.setVisibility(View.VISIBLE);
                holder.popularSubCatTextView2.setText((String) topicsList.get(1).values().toArray()[0]);
                holder.popularSubCatTextView3.setVisibility(View.VISIBLE);
                holder.popularSubCatTextView3.setText((String) topicsList.get(2).values().toArray()[0]);
                holder.popularSubCatTextView4.setVisibility(View.VISIBLE);
                holder.popularSubCatTextView4.setText((String) topicsList.get(3).values().toArray()[0]);
            } else if (topicsList.size() > 2) {
                holder.popularSubCatTextView1.setVisibility(View.VISIBLE);
                holder.popularSubCatTextView1.setText((String) topicsList.get(0).values().toArray()[0]);
                holder.popularSubCatTextView2.setVisibility(View.VISIBLE);
                holder.popularSubCatTextView2.setText((String) topicsList.get(1).values().toArray()[0]);
                holder.popularSubCatTextView3.setVisibility(View.VISIBLE);
                holder.popularSubCatTextView3.setText((String) topicsList.get(2).values().toArray()[0]);
                holder.tvParentLL4.setVisibility(View.GONE);
            } else if (topicsList.size() > 1) {
                holder.popularSubCatTextView1.setVisibility(View.VISIBLE);
                holder.popularSubCatTextView1.setText((String) topicsList.get(0).values().toArray()[0]);
                holder.popularSubCatTextView2.setVisibility(View.VISIBLE);
                holder.popularSubCatTextView2.setText((String) topicsList.get(1).values().toArray()[0]);
                holder.tvParentLL3.setVisibility(View.GONE);
                holder.tvParentLL4.setVisibility(View.GONE);
            } else if (topicsList.size() > 0) {
                holder.popularSubCatTextView1.setVisibility(View.VISIBLE);
                holder.popularSubCatTextView1.setText((String) topicsList.get(0).values().toArray()[0]);
                holder.tvParentLL2.setVisibility(View.GONE);
                holder.tvParentLL3.setVisibility(View.GONE);
                holder.tvParentLL4.setVisibility(View.GONE);
            } else {
                holder.tvParentLL1.setVisibility(View.GONE);
                holder.tvParentLL2.setVisibility(View.GONE);
                holder.tvParentLL3.setVisibility(View.GONE);
                holder.tvParentLL4.setVisibility(View.GONE);
            }

            holder.txvArticleTitle.setText(articleDataModelsNew.get(position).getTitle());

            if (StringUtils.isNullOrEmpty(articleDataModelsNew.get(position).getReason())) {
                holder.forYouDescriptionTextView.setVisibility(View.GONE);
            } else {
                holder.forYouDescriptionTextView.setVisibility(View.VISIBLE);
                holder.forYouDescriptionTextView.setText(Html.fromHtml(articleDataModelsNew.get(position).getReason()));
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

            if (null == articleDataModelsNew.get(position).getTrendingCount()) {
                holder.rankTextView.setVisibility(View.GONE);
            } else {
                holder.rankTextView.setVisibility(View.VISIBLE);
                holder.rankTextView.setText(articleDataModelsNew.get(position).getTrendingCount());
            }

            if (AppConstants.USER_TYPE_BLOGGER.equals(articleDataModelsNew.get(position).getUserType())) {
                holder.authorTypeTextView.setText(AppConstants.AUTHOR_TYPE_BLOGGER.toUpperCase());
                holder.authorTypeTextView.setTextColor(ContextCompat.getColor(mContext, R.color.authortype_colorcode_blogger));
            } else if (AppConstants.USER_TYPE_EDITOR.equals(articleDataModelsNew.get(position).getUserType())) {
                holder.authorTypeTextView.setText(AppConstants.AUTHOR_TYPE_EDITOR.toUpperCase());
                holder.authorTypeTextView.setTextColor(ContextCompat.getColor(mContext, R.color.authortype_colorcode_editor));
            } else if (AppConstants.USER_TYPE_EDITORIAL.equals(articleDataModelsNew.get(position).getUserType())) {
                holder.authorTypeTextView.setText(AppConstants.AUTHOR_TYPE_EDITORIAL.toUpperCase());
                holder.authorTypeTextView.setTextColor(ContextCompat.getColor(mContext, R.color.authortype_colorcode_editorial));
            } else if (AppConstants.USER_TYPE_EXPERT.equals(articleDataModelsNew.get(position).getUserType())) {
                holder.authorTypeTextView.setText(AppConstants.AUTHOR_TYPE_EXPERT.toUpperCase());
                holder.authorTypeTextView.setTextColor(ContextCompat.getColor(mContext, R.color.authortype_colorcode_expert));
            } else if (AppConstants.USER_TYPE_FEATURED.equals(articleDataModelsNew.get(position).getUserType())) {
                holder.authorTypeTextView.setText(AppConstants.AUTHOR_TYPE_FEATURED.toUpperCase());
                holder.authorTypeTextView.setTextColor(ContextCompat.getColor(mContext, R.color.authortype_colorcode_featured));
            }


            if (StringUtils.isNullOrEmpty(articleDataModelsNew.get(position).getUserName()) || articleDataModelsNew.get(position).getUserName().toString().trim().equalsIgnoreCase("")) {
                holder.txvAuthorName.setText("NA");
            } else {
                holder.txvAuthorName.setText(articleDataModelsNew.get(position).getUserName());
            }

            if (!StringUtils.isNullOrEmpty(articleDataModelsNew.get(position).getVideoUrl())
                    && (articleDataModelsNew.get(position).getImageUrl().getMobileWebThumbnail() == null || articleDataModelsNew.get(position).getImageUrl().getMobileWebThumbnail().endsWith("default.jpg"))) {
                Picasso.with(mContext).load(AppUtils.getYoutubeThumbnailURLMomspresso(articleDataModelsNew.get(position).getVideoUrl())).placeholder(R.drawable.default_article).into(holder.articleImageView);
            } else {
                if (!StringUtils.isNullOrEmpty(articleDataModelsNew.get(position).getImageUrl().getMobileWebThumbnail())) {
                    Picasso.with(mContext).load(articleDataModelsNew.get(position).getImageUrl().getMobileWebThumbnail())
                            .placeholder(R.drawable.default_article).error(R.drawable.default_article).into(holder.articleImageView);
                } else {
                    holder.articleImageView.setBackgroundResource(R.drawable.default_article);
                }
            }

            if (!StringUtils.isNullOrEmpty(articleDataModelsNew.get(position).getVideoUrl())) {
                holder.videoIndicatorImageView.setVisibility(View.VISIBLE);
            } else {
                holder.videoIndicatorImageView.setVisibility(View.INVISIBLE);
            }

            if (!StringUtils.isNullOrEmpty(articleDataModelsNew.get(position).getProfilePic().getClientAppMin())) {
                Picasso.with(mContext).load(articleDataModelsNew.get(position).getProfilePic().getClientAppMin())
                        .placeholder(R.drawable.default_commentor_img).error(R.drawable.default_commentor_img).into(holder.authorImageView);
            } else {
                holder.authorImageView.setBackgroundResource(R.drawable.default_commentor_img);
            }

            holder.txvAuthorName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, BloggerDashboardActivity.class);
                    intent.putExtra(AppConstants.PUBLIC_PROFILE_USER_ID, articleDataModelsNew.get(position).getUserId());
                    intent.putExtra(AppConstants.AUTHOR_NAME, "" + articleDataModelsNew.get(position).getUserName());
                    intent.putExtra(Constants.FROM_SCREEN, listingType);
                    mContext.startActivity(intent);
                }
            });
            holder.authorImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, BloggerDashboardActivity.class);
                    intent.putExtra(AppConstants.PUBLIC_PROFILE_USER_ID, articleDataModelsNew.get(position).getUserId());
                    intent.putExtra(AppConstants.AUTHOR_NAME, "" + articleDataModelsNew.get(position).getUserName());
                    intent.putExtra(Constants.FROM_SCREEN, listingType);
                    mContext.startActivity(intent);
                }
            });

            if (type == 0) {
                holder.languageFeedTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent resultIntent = new Intent(mContext, SettingsActivity.class);
                        resultIntent.putExtra("load_fragment", Constants.LANGUAGE_FRAGMENT);
                        mContext.startActivity(resultIntent);
                    }
                });
            }
        } catch (Exception ex) {
            Crashlytics.logException(ex);
            Log.d("MC4kException", Log.getStackTraceString(ex));
        }

        return view;
    }

    class ViewHolder {
        TextView languageFeedTextView;

        TextView txvArticleTitle;
        TextView txvAuthorName;
        ImageView articleImageView;
        ImageView authorImageView;
        ImageView videoIndicatorImageView;
        TextView forYouDescriptionTextView;
        TextView viewCountTextView;
        TextView commentCountTextView;
        TextView recommendCountTextView;
        TextView authorTypeTextView;
        TextView rankTextView;
        FlowLayout flowLayout;
        TextView popularSubCatTextView1;
        TextView popularSubCatTextView2;
        TextView popularSubCatTextView3;
        TextView popularSubCatTextView4;
        LinearLayout tvParentLL1;
        LinearLayout tvParentLL2;
        LinearLayout tvParentLL3;
        LinearLayout tvParentLL4;
    }

}
