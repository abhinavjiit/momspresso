package com.mycity4kids.ui.adapter;

import android.content.Context;
import android.os.AsyncTask;
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
import com.google.gson.Gson;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.models.request.ArticleDetailRequest;
import com.mycity4kids.models.request.DeleteBookmarkRequest;
import com.mycity4kids.models.response.AddBookmarkResponse;
import com.mycity4kids.models.response.ArticleListingResult;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.ArticleDetailsAPI;
import com.mycity4kids.ui.activity.ArticleListingActivity;
import com.mycity4kids.ui.fragment.ForYouInfoDialogFragment;
import com.mycity4kids.utils.AppUtils;
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
                holder.watchLaterImageView = (ImageView) view.findViewById(R.id.watchLaterImageView);
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
//                    articleDataModelsNew.get(position).setListingWatchLaterStatus(1);
//                    holder.watchLaterImageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_watch_added));
                }
            });

            holder.bookmarkArticleImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addRemoveBookmark(position, holder);
//                    articleDataModelsNew.get(position).setListingBookmarkStatus(1);
//                    holder.bookmarkArticleImageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_bookmarked));
                }
            });

        } catch (Exception ex) {
            Crashlytics.logException(ex);
            Log.d("MC4kException", Log.getStackTraceString(ex));
        }

        return view;
    }

    private void addRemoveBookmark(int position, ViewHolder holder) {
        if (articleDataModelsNew.get(position).getListingBookmarkStatus() == 0) {
            ArticleDetailRequest articleDetailRequest = new ArticleDetailRequest();
            articleDetailRequest.setArticleId(articleDataModelsNew.get(position).getId());
            String jsonString = new Gson().toJson(articleDetailRequest);
            new AddRemoveBookmarkAsyncTask(holder, "bookmarkArticle", position).execute(jsonString, "bookmarkArticle");
        } else {
            DeleteBookmarkRequest deleteBookmarkRequest = new DeleteBookmarkRequest();
            deleteBookmarkRequest.setId(articleDataModelsNew.get(position).getBookmarkId());
            String jsonString = new Gson().toJson(deleteBookmarkRequest);
            new AddRemoveBookmarkAsyncTask(holder, "unbookmarkArticle", position).execute(jsonString, "unbookmarkArticle");
        }
    }

    private void addRemoveWatchLater(int position, ViewHolder holder) {
        if (articleDataModelsNew.get(position).getListingWatchLaterStatus() == 0) {
            ArticleDetailRequest articleDetailRequest = new ArticleDetailRequest();
            articleDetailRequest.setArticleId(articleDataModelsNew.get(position).getId());
            String jsonString = new Gson().toJson(articleDetailRequest);
            new AddRemoveBookmarkAsyncTask(holder, "bookmarkVideo", position).execute(jsonString, "bookmarkVideo");
        } else {
            DeleteBookmarkRequest deleteBookmarkRequest = new DeleteBookmarkRequest();
            deleteBookmarkRequest.setId(articleDataModelsNew.get(position).getBookmarkId());
            String jsonString = new Gson().toJson(deleteBookmarkRequest);
            new AddRemoveBookmarkAsyncTask(holder, "unbookmarkVideo", position).execute(jsonString, "unbookmarkVideo");
        }
//        if (bookmarkStatus == 0) {
//            ArticleDetailRequest articleDetailRequest = new ArticleDetailRequest();
//            articleDetailRequest.setArticleId(articleId);
//            Retrofit retro = BaseApplication.getInstance().getRetrofit();
//            articleDetailsAPI = retro.create(ArticleDetailsAPI.class);
//            Call<AddBookmarkResponse> call = articleDetailsAPI.addVideoWatchLater(articleDetailRequest);
//            call.enqueue(addBookmarkResponseCallback);
//        } else {
//            DeleteBookmarkRequest deleteBookmarkRequest = new DeleteBookmarkRequest();
//            deleteBookmarkRequest.setId(bookmarkId);
//        }

    }

    private class AddRemoveBookmarkAsyncTask extends AsyncTask<String, String, String> {

        // The variable is moved here, we only need it here while displaying the
        // progress dialog.
        ViewHolder viewHolder;
        String type;
        int pos;

        public AddRemoveBookmarkAsyncTask(ViewHolder viewHolder, String type, int position) {
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
                                viewHolder.bookmarkArticleImageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_bookmarked));
                            } else if ("unbookmarkArticle".equals(type)) {
                                articleDataModelsNew.get(i).setListingBookmarkStatus(0);
                                articleDataModelsNew.get(i).setBookmarkId("");
                                viewHolder.bookmarkArticleImageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_bookmark));
                            } else if ("bookmarkVideo".equals(type)) {
                                articleDataModelsNew.get(i).setListingWatchLaterStatus(1);
                                articleDataModelsNew.get(i).setBookmarkId(responseData.getData().getResult().getBookmarkId());
                                viewHolder.watchLaterImageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_watch_added));
                            } else if ("unbookmarkVideo".equals(type)) {
                                articleDataModelsNew.get(i).setListingWatchLaterStatus(0);
                                articleDataModelsNew.get(i).setBookmarkId("");
                                viewHolder.watchLaterImageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_watch));
                            }
                        }
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
                viewHolder.bookmarkArticleImageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_bookmark));
            } else if ("unbookmarkArticle".equals(type)) {
                viewHolder.bookmarkArticleImageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_bookmarked));
            } else if ("bookmarkVideo".equals(type)) {
                viewHolder.watchLaterImageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_watch));
            } else if ("unbookmarkVideo".equals(type)) {
                viewHolder.watchLaterImageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_watch_added));
            }
        }

    }

    class ViewHolder {
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
    }

}
