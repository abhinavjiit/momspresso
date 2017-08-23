package com.mycity4kids.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.ArticleDetailsAPI;
import com.mycity4kids.retrofitAPIsInterfaces.BloggerDashboardAPI;
import com.mycity4kids.ui.activity.ArticleDetailsContainerActivity;
import com.mycity4kids.ui.activity.UserPublishedAndDraftsActivity;
import com.mycity4kids.ui.adapter.UserPublishedArticleAdapter;
import com.mycity4kids.utils.AppUtils;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by hemant.parmar on 21-04-2016.
 */
public class UserPublishedArticleTabFragment extends BaseFragment implements View.OnClickListener, UserPublishedArticleAdapter.RecyclerViewClickListener {

    private ArrayList<ArticleListingResult> articleDataModelsNew;
    private RecyclerView recyclerView;
    private RelativeLayout mLodingView;
    private TextView noBlogsTextView;

    private UserPublishedArticleAdapter adapter;

    private int nextPageNumber = 0;
    private boolean isReuqestRunning = false;
    private boolean isLastPageReached = true;
    private boolean isPrivateProfile;
    private String authorId;
    private int pastVisiblesItems, visibleItemCount, totalItemCount;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = null;
        view = getActivity().getLayoutInflater().inflate(R.layout.user_published_article_tab_fragment, container, false);

        Utils.pushOpenScreenEvent(getActivity(), "Search Articles Fragment Listing", SharedPrefUtils.getUserDetailModel(getActivity()).getDynamoId() + "");

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        mLodingView = (RelativeLayout) view.findViewById(R.id.relativeLoadingView);
        noBlogsTextView = (TextView) view.findViewById(R.id.noBlogsTextView);

        if (getArguments() != null) {
            authorId = getArguments().getString(Constants.AUTHOR_ID);
            isPrivateProfile = getArguments().getBoolean("isPrivateProfile", false);
        }

        adapter = new UserPublishedArticleAdapter(getActivity(), this, isPrivateProfile);
        final LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(adapter);

        articleDataModelsNew = new ArrayList<ArticleListingResult>();

        //only when first time fragment is created
        nextPageNumber = 0;
        getUserPublishedArticles();

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
                            getUserPublishedArticles();
                        }
                    }
                }
            }
        });
        return view;
    }

    private void getUserPublishedArticles() {
        if (!ConnectivityUtils.isNetworkEnabled(getActivity())) {
            ((UserPublishedAndDraftsActivity) getActivity()).showToast("No connectivity available");
            return;
        }

        Retrofit retro = BaseApplication.getInstance().getRetrofit();
        BloggerDashboardAPI userpublishedArticlesAPI = retro.create(BloggerDashboardAPI.class);
        int from = 15 * nextPageNumber + 1;
        final Call<ArticleListingResponse> call = userpublishedArticlesAPI.getAuthorsPublishedArticles(authorId, 0, from, from + 14);
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
                    processPublisedArticlesResponse(responseData);
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
                intent.putExtra(Constants.FROM_SCREEN, "User Profile");
                if (true) {
                    intent.putExtra(Constants.ARTICLE_OPENED_FROM, "Private Comments");
                    intent.putExtra(Constants.FROM_SCREEN, "Private User Profile");
                } else {
                    intent.putExtra(Constants.ARTICLE_OPENED_FROM, "Public Comments");
                    intent.putExtra(Constants.FROM_SCREEN, "Public User Profile");
                }
                intent.putExtra(Constants.ARTICLE_INDEX, "" + position);
                startActivity(intent);
                break;
            case R.id.editPublishedTextView:
                Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
                ArticleDetailsAPI articleDetailsAPI = retrofit.create(ArticleDetailsAPI.class);
                Call<ArticleDetailResult> call = articleDetailsAPI.getArticleDetailsFromS3(articleDataModelsNew.get(position).getId());
                call.enqueue(articleDetailResponseCallback);
                break;
            case R.id.shareArticleImageView:
                Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                String shareUrl = AppUtils.getShareUrl(articleDataModelsNew.get(position).getUserType(),
                        articleDataModelsNew.get(position).getBlogPageSlug(), articleDataModelsNew.get(position).getTitleSlug());
                String shareMessage;
                if (StringUtils.isNullOrEmpty(shareUrl)) {
                    shareMessage = "mycity4kids\n\nCheck out this interesting blog post " + "\"" +
                            articleDataModelsNew.get(position).getTitle() + "\" by " + articleDataModelsNew.get(position).getUserName() + ".";
                } else {
                    shareMessage = "mycity4kids\n\nCheck out this interesting blog post " + "\"" +
                            articleDataModelsNew.get(position).getTitle() + "\" by " + articleDataModelsNew.get(position).getUserName() + ".\nRead Here: " + shareUrl;
                }
                shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareMessage);
                startActivity(Intent.createChooser(shareIntent, "mycity4kids"));
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

        Intent intent = new Intent(getActivity(), EditorPostActivity.class);
        intent.putExtra("from", "publishedList");
        intent.putExtra("title", detailData.getTitle());
        intent.putExtra("content", content);
        intent.putExtra("thumbnailUrl", detailData.getImageUrl().getThumbMax());
        intent.putExtra("articleId", detailData.getId());
        intent.putExtra("tag", new Gson().toJson(detailData.getTags()));
        intent.putExtra("cities", new Gson().toJson(detailData.getCities()));
        startActivity(intent);
    }
}
