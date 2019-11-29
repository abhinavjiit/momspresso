package com.mycity4kids.ui.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseFragment;
import com.kelltontech.utils.ConnectivityUtils;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.editor.EditorPostActivity;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.parentingdetails.ImageData;
import com.mycity4kids.models.response.ArticleDetailResult;
import com.mycity4kids.models.response.ArticleListingResponse;
import com.mycity4kids.models.response.ArticleListingResult;
import com.mycity4kids.models.response.ShortStoryDetailResult;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.ArticleDetailsAPI;
import com.mycity4kids.retrofitAPIsInterfaces.BloggerDashboardAPI;
import com.mycity4kids.retrofitAPIsInterfaces.ShortStoryAPI;
import com.mycity4kids.ui.activity.AddShortStoryActivity;
import com.mycity4kids.ui.activity.ArticleDetailsContainerActivity;
import com.mycity4kids.ui.activity.ShortStoryContainerActivity;
import com.mycity4kids.ui.activity.UserPublishedContentActivity;
import com.mycity4kids.ui.adapter.UserReadArticleAdapter;
import com.mycity4kids.ui.adapter.UserReadShortStoriesAdapter;
import com.mycity4kids.utils.AppUtils;
import com.mycity4kids.widget.FeedNativeAd;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by hemant.parmar on 21-04-2016.
 */
public class UserReadArticleTabFragment extends BaseFragment implements View.OnClickListener, UserReadArticleAdapter.RecyclerViewClickListener, /*FeedNativeAd.AdLoadingListener,*/
        UserReadShortStoriesAdapter.SSRecyclerViewClickListener {

    private ArrayList<ArticleListingResult> articleDataModelsNew;
    private RecyclerView recyclerView;
    private RelativeLayout mLodingView;
    private TextView noBlogsTextView, noBlogsTextViewshortstory;

    private UserReadArticleAdapter adapter;
    private UserReadShortStoriesAdapter shortStoriesAdapter;
    int chunk = 0;
    int chunk1 = 0;

    private int nextPageNumber = 0;
    private boolean isReuqestRunning = false;
    private boolean isLastPageReached = true;
    private boolean isPrivateProfile;
    private String authorId;
    private int pastVisiblesItems, visibleItemCount, totalItemCount;

    private FeedNativeAd feedNativeAd;
    private String contentType;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.user_read_article_tab_fragment, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        mLodingView = (RelativeLayout) view.findViewById(R.id.relativeLoadingView);
        noBlogsTextView = (TextView) view.findViewById(R.id.noBlogsTextView);
        noBlogsTextViewshortstory = view.findViewById(R.id.noBlogsTextViewshortstory);
        view.findViewById(R.id.relativeLoadingView).startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.rotate_indefinitely));
        if (getArguments() != null) {
            authorId = getArguments().getString(Constants.AUTHOR_ID);
            isPrivateProfile = getArguments().getBoolean("isPrivateProfile", false);
            contentType = getArguments().getString("contentType");
        }

        final LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        articleDataModelsNew = new ArrayList<ArticleListingResult>();

//        feedNativeAd = new FeedNativeAd(getActivity(), this, AppConstants.FB_AD_PLACEMENT_USER_ARTICLE);
//        feedNativeAd.loadAds();

        nextPageNumber = 0;
        if ("shortStory".equals(contentType)) {
            shortStoriesAdapter = new UserReadShortStoriesAdapter(getActivity(), this, isPrivateProfile, feedNativeAd);
            recyclerView.setAdapter(shortStoriesAdapter);
            getUserPublishedShortStories();
        } else {
            adapter = new UserReadArticleAdapter(getActivity(), this, isPrivateProfile, feedNativeAd);
            recyclerView.setAdapter(adapter);
            getUserPublishedArticles();
        }


        //only when first time fragment is created


        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) //check for scroll down
                {
                    visibleItemCount = llm.getChildCount();
                    totalItemCount = llm.getItemCount();
                    pastVisiblesItems = llm.findFirstVisibleItemPosition();

                    if (!isReuqestRunning) {
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                            isReuqestRunning = true;
                            mLodingView.setVisibility(View.VISIBLE);
                            if ("shortStory".equals(contentType)) {
                                getUserPublishedShortStories();
                            } else {
                                getUserPublishedArticles();
                            }

                        }
                    }
                }
            }
        });
        return view;
    }

    private void getUserPublishedShortStories() {
        if (!ConnectivityUtils.isNetworkEnabled(getActivity())) {
            ((UserPublishedContentActivity) getActivity()).showToast(getString(R.string.connectivity_unavailable));
            return;
        }

        Retrofit retro = BaseApplication.getInstance().getRetrofit();
        BloggerDashboardAPI userpublishedArticlesAPI = retro.create(BloggerDashboardAPI.class);

        final Call<ArticleListingResponse> call = userpublishedArticlesAPI.getAuthorsReadArticles(authorId, 10, chunk1, "stories");
        call.enqueue(userPublishedArticleResponseListener);
    }

    private void getUserPublishedArticles() {
        if (isAdded()) {
            if (!ConnectivityUtils.isNetworkEnabled(getActivity())) {
                ((UserPublishedContentActivity) getActivity()).showToast(getString(R.string.connectivity_unavailable));
                return;
            }
        }

        Retrofit retro = BaseApplication.getInstance().getRetrofit();
        BloggerDashboardAPI userpublishedArticlesAPI = retro.create(BloggerDashboardAPI.class);
        final Call<ArticleListingResponse> call = userpublishedArticlesAPI.getAuthorsReadArticles(authorId, 10, chunk, "articles");
        call.enqueue(userPublishedArticleResponseListener);
    }

    @Override
    protected void updateUi(Response response) {

    }

    private Callback<ArticleListingResponse> userPublishedArticleResponseListener = new Callback<ArticleListingResponse>() {
        @Override
        public void onResponse(Call<ArticleListingResponse> call, retrofit2.Response<ArticleListingResponse> response) {
            removeProgressDialog();
            isReuqestRunning = false;
            mLodingView.setVisibility(View.GONE);
            if (response == null || response.body() == null) {
                return;
            }
            try {
                ArticleListingResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {

                    if ("shortStory".equals(contentType)) {
                        chunk1 = Integer.parseInt(responseData.getData().get(0).getChunks());

                        processPublishedStoriesResponse(responseData);
                    } else {
                        chunk = Integer.parseInt(responseData.getData().get(0).getChunks());
                        processPublisedArticlesResponse(responseData);
                    }
                } else {

                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<ArticleListingResponse> call, Throwable t) {
            mLodingView.setVisibility(View.GONE);
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    private void processPublishedStoriesResponse(ArticleListingResponse responseData) {
        ArrayList<ArticleListingResult> dataList = responseData.getData().get(0).getResult();

        if (dataList.size() == 0) {

            isLastPageReached = false;
            if (null != articleDataModelsNew && !articleDataModelsNew.isEmpty()) {
                //No more next results for search from pagination
            } else {
                // No results
                articleDataModelsNew.addAll(dataList);
                shortStoriesAdapter.setListData(articleDataModelsNew);
                shortStoriesAdapter.notifyDataSetChanged();
                if (isAdded())
                    noBlogsTextView.setText(getString(R.string.short_s_no_published));
                if ("shortStory".equals(contentType)) {
                    noBlogsTextViewshortstory.setVisibility(View.VISIBLE);
                } else {
                    noBlogsTextView.setVisibility(View.VISIBLE);
                }
            }
        } else {
            if (nextPageNumber == 1) {
                articleDataModelsNew.addAll(dataList);
                shortStoriesAdapter.setListData(articleDataModelsNew);
                shortStoriesAdapter.notifyDataSetChanged();
            } else {
                articleDataModelsNew.addAll(dataList);
            }
            shortStoriesAdapter.setListData(articleDataModelsNew);
            nextPageNumber = nextPageNumber + 1;
            shortStoriesAdapter.notifyDataSetChanged();
        }
    }

    private void processPublisedArticlesResponse(ArticleListingResponse responseData) {
        ArrayList<ArticleListingResult> dataList = responseData.getData().get(0).getResult();

        if (dataList.size() == 0) {

            isLastPageReached = false;
            if (null != articleDataModelsNew && !articleDataModelsNew.isEmpty()) {
                //No more next results for search from pagination
            } else {
                // No results
                articleDataModelsNew.addAll(dataList);
                adapter.setListData(articleDataModelsNew);
                adapter.notifyDataSetChanged();
                noBlogsTextView.setVisibility(View.VISIBLE);
            }
        } else {
            if (nextPageNumber == 1) {
                articleDataModelsNew.addAll(dataList);
                adapter.setListData(articleDataModelsNew);
                adapter.notifyDataSetChanged();
            } else {
                articleDataModelsNew.addAll(dataList);
            }
            adapter.setListData(articleDataModelsNew);
            nextPageNumber = nextPageNumber + 1;
            adapter.notifyDataSetChanged();
        }

    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onClick(View view, int position) {
        switch (view.getId()) {
            case R.id.rootLayout:
                Intent intent = new Intent(getActivity(), ArticleDetailsContainerActivity.class);
                intent.putExtra(Constants.ARTICLE_ID, articleDataModelsNew.get(position).getId());
                intent.putExtra(Constants.AUTHOR_ID, articleDataModelsNew.get(position).getUserId());
                intent.putExtra(Constants.BLOG_SLUG, articleDataModelsNew.get(position).getBlogPageSlug());
                intent.putExtra(Constants.TITLE_SLUG, articleDataModelsNew.get(position).getTitleSlug());
                if (authorId.equals(SharedPrefUtils.getUserDetailModel(getActivity()).getDynamoId())) {
                    intent.putExtra(Constants.ARTICLE_OPENED_FROM, "PrivatePublishedArticles");
                    intent.putExtra(Constants.FROM_SCREEN, "PrivateUserArticlesScreen");
                } else {
                    intent.putExtra(Constants.ARTICLE_OPENED_FROM, "PublicPublishedArticles");
                    intent.putExtra(Constants.FROM_SCREEN, "PublicUserArticlesScreen");
                }
                intent.putExtra(Constants.ARTICLE_INDEX, "" + position);
                intent.putParcelableArrayListExtra("pagerListData", articleDataModelsNew);
                intent.putExtra(Constants.AUTHOR, articleDataModelsNew.get(position).getUserId() + "~" + articleDataModelsNew.get(position).getUserName());
                startActivity(intent);
                break;
            case R.id.editPublishedTextView:
                Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
                ArticleDetailsAPI articleDetailsAPI = retrofit.create(ArticleDetailsAPI.class);
                Call<ArticleDetailResult> call = articleDetailsAPI.getArticleDetailsFromRedis(articleDataModelsNew.get(position).getId(), "articleId");
                call.enqueue(articleDetailResponseCallback);
                break;
            case R.id.shareArticleImageView:
                Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
                shareIntent.setType("text/plain");

                String shareUrl = AppUtils.getShareUrl(articleDataModelsNew.get(position).getUserType(),
                        articleDataModelsNew.get(position).getBlogPageSlug(), articleDataModelsNew.get(position).getTitleSlug());
                String shareMessage;
                if (StringUtils.isNullOrEmpty(shareUrl)) {
                    shareMessage = getString(R.string.check_out_blog) + "\"" +
                            articleDataModelsNew.get(position).getTitle() + "\" by " + articleDataModelsNew.get(position).getUserName() + ".";
                } else {
                    shareMessage = getString(R.string.check_out_blog) + "\"" +
                            articleDataModelsNew.get(position).getTitle() + "\" by " + articleDataModelsNew.get(position).getUserName() + ".\nRead Here: " + shareUrl;
                }
                shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareMessage);
                startActivity(Intent.createChooser(shareIntent, "Momspresso"));
                if (isPrivateProfile) {
                    Utils.pushShareArticleEvent(getActivity(), "PrivateUserArticlesScreen", SharedPrefUtils.getUserDetailModel(getActivity()).getDynamoId() + "", articleDataModelsNew.get(position).getId(),
                            authorId + "~" + articleDataModelsNew.get(position).getUserName(), "-");
                } else {
                    Utils.pushShareArticleEvent(getActivity(), "PublicUserArticlesScreen", SharedPrefUtils.getUserDetailModel(getActivity()).getDynamoId() + "", articleDataModelsNew.get(position).getId(),
                            authorId + "~" + articleDataModelsNew.get(position).getUserName(), "-");
                }
                break;
        }
    }

    Callback<ArticleDetailResult> articleDetailResponseCallback = new Callback<ArticleDetailResult>() {
        @Override
        public void onResponse(Call<ArticleDetailResult> call, retrofit2.Response<ArticleDetailResult> response) {
            removeProgressDialog();
            if (response == null || response.body() == null) {
//                showToast("Something went wrong from server");
                return;
            }
            try {
                ArticleDetailResult responseData = response.body();
                getResponseUpdateUi(responseData);
            } catch (Exception e) {
//                showToast(getString(R.string.server_went_wrong));
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<ArticleDetailResult> call, Throwable t) {
            removeProgressDialog();
            if (t instanceof UnknownHostException) {
//                showToast(getString(R.string.error_network));
            } else if (t instanceof SocketTimeoutException) {
//                showToast("connection timed out");
            } else {
//                showToast(getString(R.string.server_went_wrong));
                Crashlytics.logException(t);
                Log.d("MC4kException", Log.getStackTraceString(t));
            }
        }
    };

    private void getResponseUpdateUi(ArticleDetailResult detailsResponse) {
        ArticleDetailResult detailData = detailsResponse;
        ArrayList<ImageData> imageList = detailData.getBody().getImage();

        String bodyDescription = detailData.getBody().getText();
        String bodyDesc = bodyDescription;
        String content;
        if (imageList.size() > 0) {
            for (ImageData images : imageList) {
                if (bodyDescription.contains(images.getKey())) {
                    bodyDesc = bodyDesc.replace(images.getKey(), "<p style='text-align:center'><img src=" + images.getValue() + " style=\"width: 100%;\"+></p>");
                }
            }

            String bodyImgTxt = "<html><head></head><body>" + bodyDesc + "</body></html>";
            content = bodyImgTxt;

        } else {
            String bodyImgTxt = "<html><head></head><body>" + bodyDesc + "</body></html>";
            content = bodyImgTxt;
        }

//        if (AppConstants.LOCALE_TAMIL.equals(SharedPrefUtils.getAppLocale(BaseApplication.getAppContext())) && AppConstants.ANDROID_NEW_EDITOR.equals(detailData.getUserAgent())) {
//            Intent intent = new Intent(getActivity(), NewEditorPostActivity.class);
//            intent.putExtra("from", "publishedList");
//            intent.putExtra("title", detailData.getTitle());
//            intent.putExtra("content", content);
//            intent.putExtra("thumbnailUrl", detailData.getImageUrl().getThumbMax());
//            intent.putExtra("articleId", detailData.getId());
//            intent.putExtra("tag", new Gson().toJson(detailData.getTags()));
//            intent.putExtra("cities", new Gson().toJson(detailData.getCities()));
//            startActivity(intent);
//        } else {
        Intent intent = new Intent(getActivity(), EditorPostActivity.class);
        intent.putExtra("from", "publishedList");
        intent.putExtra("title", detailData.getTitle());
        intent.putExtra("content", content);
        intent.putExtra("thumbnailUrl", detailData.getImageUrl().getThumbMax());
        intent.putExtra("articleId", detailData.getId());
        intent.putExtra("tag", new Gson().toJson(detailData.getTags()));
        intent.putExtra("cities", new Gson().toJson(detailData.getCities()));
        startActivity(intent);
//        }
    }

//    @Override
//    public void onFinishToLoadAds() {
//
//    }
//
//    @Override
//    public void onErrorToLoadAd() {
//
//    }

    @Override
    public void onShortStoryClick(View view, int position) {
        switch (view.getId()) {
            case R.id.rootLayout:
                Intent intent = new Intent(getActivity(), ShortStoryContainerActivity.class);
                intent.putExtra(Constants.ARTICLE_ID, articleDataModelsNew.get(position).getId());
                intent.putExtra(Constants.AUTHOR_ID, articleDataModelsNew.get(position).getUserId());
                intent.putExtra(Constants.BLOG_SLUG, articleDataModelsNew.get(position).getBlogPageSlug());
                intent.putExtra(Constants.TITLE_SLUG, articleDataModelsNew.get(position).getTitleSlug());
                if (authorId.equals(SharedPrefUtils.getUserDetailModel(getActivity()).getDynamoId())) {
                    intent.putExtra(Constants.ARTICLE_OPENED_FROM, "PrivatePublishedArticles");
                    intent.putExtra(Constants.FROM_SCREEN, "PrivateUserArticlesScreen");
                } else {
                    intent.putExtra(Constants.ARTICLE_OPENED_FROM, "PublicPublishedArticles");
                    intent.putExtra(Constants.FROM_SCREEN, "PublicUserArticlesScreen");
                }
                intent.putExtra(Constants.ARTICLE_INDEX, "" + position);
                intent.putParcelableArrayListExtra("pagerListData", articleDataModelsNew);
                intent.putExtra(Constants.AUTHOR, articleDataModelsNew.get(position).getUserId() + "~" + articleDataModelsNew.get(position).getUserName());
                startActivity(intent);
                break;
            case R.id.editPublishedTextView:
                Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
                ShortStoryAPI shortStoryAPI = retrofit.create(ShortStoryAPI.class);
                Call<ShortStoryDetailResult> call = shortStoryAPI.getShortStoryDetails(articleDataModelsNew.get(position).getId(), "articleId");
                call.enqueue(ssDetailResponseCallbackRedis);
                break;
            case R.id.shareArticleImageView:
                Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
                shareIntent.setType("text/plain");

                String shareUrl = AppUtils.getShortStoryShareUrl(articleDataModelsNew.get(position).getUserType(),
                        articleDataModelsNew.get(position).getBlogPageSlug(), articleDataModelsNew.get(position).getTitleSlug());
                String shareMessage;
                if (StringUtils.isNullOrEmpty(shareUrl)) {
                    shareMessage = getString(R.string.check_out_short_story) + "\"" +
                            articleDataModelsNew.get(position).getTitle() + "\" by " + articleDataModelsNew.get(position).getUserName() + ".";
                } else {
                    shareMessage = getString(R.string.check_out_short_story) + "\"" +
                            articleDataModelsNew.get(position).getTitle() + "\" by " + articleDataModelsNew.get(position).getUserName() + ".\nRead Here: " + shareUrl;
                }
                shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareMessage);
                startActivity(Intent.createChooser(shareIntent, "Momspresso"));
                if (isPrivateProfile) {
                    Utils.pushShareArticleEvent(getActivity(), "PrivateUserArticlesScreen", SharedPrefUtils.getUserDetailModel(getActivity()).getDynamoId() + "", articleDataModelsNew.get(position).getId(),
                            authorId + "~" + articleDataModelsNew.get(position).getUserName(), "-");
                } else {
                    Utils.pushShareArticleEvent(getActivity(), "PublicUserArticlesScreen", SharedPrefUtils.getUserDetailModel(getActivity()).getDynamoId() + "", articleDataModelsNew.get(position).getId(),
                            authorId + "~" + articleDataModelsNew.get(position).getUserName(), "-");
                }
                break;
        }
    }

    Callback<ShortStoryDetailResult> ssDetailResponseCallbackRedis = new Callback<ShortStoryDetailResult>() {
        @Override
        public void onResponse(Call<ShortStoryDetailResult> call, retrofit2.Response<ShortStoryDetailResult> response) {
            removeProgressDialog();
            if (response == null || response.body() == null) {
                return;
            }
            try {
                ShortStoryDetailResult responseData = response.body();
                Intent intent = new Intent(getActivity(), AddShortStoryActivity.class);
                intent.putExtra("from", "publishedList");
                intent.putExtra("title", responseData.getTitle());
                intent.putExtra("body", responseData.getBody());
                intent.putExtra("articleId", responseData.getId());
                intent.putExtra("tag", new Gson().toJson(responseData.getTags()));
                intent.putExtra("cities", new Gson().toJson(responseData.getCities()));
                startActivity(intent);
            } catch (Exception e) {
                removeProgressDialog();
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }

        }

        @Override
        public void onFailure(Call<ShortStoryDetailResult> call, Throwable t) {
            removeProgressDialog();
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    @Override
    public void onPause() {
        super.onPause();
        chunk = 0;

    }
}
