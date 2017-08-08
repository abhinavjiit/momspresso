package com.mycity4kids.ui.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.models.request.ArticleDetailRequest;
import com.mycity4kids.models.response.AddBookmarkResponse;
import com.mycity4kids.models.response.ArticleListingResult;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.ArticleDetailsAPI;
import com.mycity4kids.ui.activity.ArticleListingActivity;
import com.mycity4kids.ui.fragment.ForYouInfoDialogFragment;
import com.mycity4kids.utils.AppUtils;
import com.squareup.picasso.Picasso;

import org.apmem.tools.layouts.FlowLayout;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by hemant on 25/5/17.
 */
public class MainArticleListingAdapter extends BaseAdapter {

    private ArrayList<ArticleListingResult> mArticleListData;
    private Context mContext;
    private LayoutInflater mInflator;
    ArrayList<ArticleListingResult> articleDataModelsNew;

    private final float density;
    private String listingType = "";
    private int languageFeedChosen;
    private ArticleDetailsAPI articleDetailsAPI;

    public MainArticleListingAdapter(Context pContext) {

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
//        if (1 == languageFeedChosen && position == 10) {
//            Log.d("Manage", "Language");
//            return 0;
//        } else {
//            if (1 != languageFeedChosen && position > 0 && position % 3 == 0) {
//                Log.d("Manage", "Language");
//                return 0;
//            } else {
//                return 1;
//            }
//        }
        return 1;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {

        try {
            final ViewHolder holder;
            int type = getItemViewType(position);

            if (view == null) {
                holder = new ViewHolder();
                view = mInflator.inflate(R.layout.article_listing_item, null);

                holder.txvArticleTitle = (TextView) view.findViewById(R.id.txvArticleTitle);
                holder.txvAuthorName = (TextView) view.findViewById(R.id.txvAuthorName);
                holder.articleImageView = (ImageView) view.findViewById(R.id.articleImageView);
                holder.videoIndicatorImageView = (ImageView) view.findViewById(R.id.videoIndicatorImageView);
                holder.forYouInfoLL = (LinearLayout) view.findViewById(R.id.forYouInfoLL);

                holder.viewCountTextView = (TextView) view.findViewById(R.id.viewCountTextView);
                holder.commentCountTextView = (TextView) view.findViewById(R.id.commentCountTextView);
                holder.recommendCountTextView = (TextView) view.findViewById(R.id.recommendCountTextView);

                holder.authorTypeTextView = (TextView) view.findViewById(R.id.authorTypeTextView);
                holder.bookmarkArticleImageView = (ImageView) view.findViewById(R.id.bookmarkArticleImageView);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }

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

//            if (AppConstants.USER_TYPE_BLOGGER.equals(articleDataModelsNew.get(position).getUserType())) {
//                holder.authorTypeTextView.setText(AppConstants.AUTHOR_TYPE_BLOGGER.toUpperCase());
//                holder.authorTypeTextView.setTextColor(ContextCompat.getColor(mContext, R.color.authortype_colorcode_blogger));
//            } else if (AppConstants.USER_TYPE_EDITOR.equals(articleDataModelsNew.get(position).getUserType())) {
//                holder.authorTypeTextView.setText(AppConstants.AUTHOR_TYPE_EDITOR.toUpperCase());
//                holder.authorTypeTextView.setTextColor(ContextCompat.getColor(mContext, R.color.authortype_colorcode_editor));
//            } else if (AppConstants.USER_TYPE_EDITORIAL.equals(articleDataModelsNew.get(position).getUserType())) {
//                holder.authorTypeTextView.setText(AppConstants.AUTHOR_TYPE_EDITORIAL.toUpperCase());
//                holder.authorTypeTextView.setTextColor(ContextCompat.getColor(mContext, R.color.authortype_colorcode_editorial));
//            } else if (AppConstants.USER_TYPE_EXPERT.equals(articleDataModelsNew.get(position).getUserType())) {
//                holder.authorTypeTextView.setText(AppConstants.AUTHOR_TYPE_EXPERT.toUpperCase());
//                holder.authorTypeTextView.setTextColor(ContextCompat.getColor(mContext, R.color.authortype_colorcode_expert));
//            } else if (AppConstants.USER_TYPE_FEATURED.equals(articleDataModelsNew.get(position).getUserType())) {
//                holder.authorTypeTextView.setText(AppConstants.AUTHOR_TYPE_FEATURED.toUpperCase());
//                holder.authorTypeTextView.setTextColor(ContextCompat.getColor(mContext, R.color.authortype_colorcode_featured));
//            }


            if (StringUtils.isNullOrEmpty(articleDataModelsNew.get(position).getUserName()) || articleDataModelsNew.get(position).getUserName().toString().trim().equalsIgnoreCase("")) {
                holder.txvAuthorName.setText("NA");
            } else {
                holder.txvAuthorName.setText(articleDataModelsNew.get(position).getUserName());
            }

            if (!StringUtils.isNullOrEmpty(articleDataModelsNew.get(position).getVideoUrl())
                    && (articleDataModelsNew.get(position).getImageUrl().getWebThumbnail() == null || articleDataModelsNew.get(position).getImageUrl().getWebThumbnail().endsWith("default.jpg"))) {
                Picasso.with(mContext).load(AppUtils.getYoutubeThumbnailURLMomspresso(articleDataModelsNew.get(position).getVideoUrl())).placeholder(R.drawable.default_article).into(holder.articleImageView);
            } else {
                if (!StringUtils.isNullOrEmpty(articleDataModelsNew.get(position).getImageUrl().getWebThumbnail())) {
                    Picasso.with(mContext).load(articleDataModelsNew.get(position).getImageUrl().getWebThumbnail())
                            .placeholder(R.drawable.default_article).error(R.drawable.default_article).into(holder.articleImageView);
                } else if (!StringUtils.isNullOrEmpty(articleDataModelsNew.get(position).getImageUrl().getMobileWebThumbnail())) {
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

            if (articleDataModelsNew.get(position).getListingBookmarkStatus() == 0) {
                holder.bookmarkArticleImageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_bookmark));
            } else {
                holder.bookmarkArticleImageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_bookmarked));
            }
            holder.bookmarkArticleImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addRemoveBookmark(articleDataModelsNew.get(position).getListingBookmarkStatus(), articleDataModelsNew.get(position).getId());
                    articleDataModelsNew.get(position).setListingBookmarkStatus(1);
                    holder.bookmarkArticleImageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_bookmarked));
                }
            });

//            if (type == 0) {
//                holder.languageFeedTextView.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//
//                        Intent resultIntent = new Intent(mContext, SettingsActivity.class);
//                        resultIntent.putExtra("load_fragment", Constants.LANGUAGE_FRAGMENT);
//                        mContext.startActivity(resultIntent);
//                    }
//                });
//            }
        } catch (Exception ex) {
            Crashlytics.logException(ex);
            Log.d("MC4kException", Log.getStackTraceString(ex));
        }

        return view;
    }

    private void addRemoveBookmark(int bookmarkStatus, String articleId) {

        if (bookmarkStatus == 0) {
            ArticleDetailRequest articleDetailRequest = new ArticleDetailRequest();
            articleDetailRequest.setArticleId(articleId);
            bookmarkStatus = 1;
//            Utils.pushArticleBookmarkUnbookmarkEvent(getActivity(), GTMEventType.BOOKMARK_ARTICLE_CLICK_EVENT, "Article Details", userDynamoId,
//                    articleId, author + "-" + authorId);
            Retrofit retro = BaseApplication.getInstance().getRetrofit();
            articleDetailsAPI = retro.create(ArticleDetailsAPI.class);
            Call<AddBookmarkResponse> call = articleDetailsAPI.addBookmark(articleDetailRequest);
            call.enqueue(addBookmarkResponseCallback);
        }

    }

    private Callback<AddBookmarkResponse> addBookmarkResponseCallback = new Callback<AddBookmarkResponse>() {
        @Override
        public void onResponse(Call<AddBookmarkResponse> call, retrofit2.Response<AddBookmarkResponse> response) {
            if (response == null || null == response.body()) {
                return;
            }
//            AddBookmarkResponse responseData = (AddBookmarkResponse) response.body();
//            if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
//
//            }
        }

        @Override
        public void onFailure(Call<AddBookmarkResponse> call, Throwable t) {

        }
    };

    class ViewHolder {
        TextView languageFeedTextView;

        TextView txvArticleTitle;
        TextView txvAuthorName;
        ImageView articleImageView;
        ImageView authorImageView;
        ImageView videoIndicatorImageView;
        LinearLayout forYouInfoLL;
        TextView viewCountTextView;
        TextView commentCountTextView;
        TextView recommendCountTextView;
        TextView authorTypeTextView;
        ImageView bookmarkArticleImageView;
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
