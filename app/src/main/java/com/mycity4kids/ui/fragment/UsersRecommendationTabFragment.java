package com.mycity4kids.ui.fragment;

import android.accounts.NetworkErrorException;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.base.BaseFragment;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.response.ArticleListingResponse;
import com.mycity4kids.models.response.ArticleListingResult;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.BloggerDashboardAPI;
import com.mycity4kids.ui.activity.ArticleDetailsContainerActivity;
import com.mycity4kids.ui.activity.ShortStoryContainerActivity;
import com.mycity4kids.ui.activity.UserActivitiesActivity;
import com.mycity4kids.ui.adapter.UsersRecommendationsRecycleAdapter;
import com.mycity4kids.utils.AppUtils;
import com.mycity4kids.utils.ConnectivityUtils;
import com.mycity4kids.utils.StringUtils;
import java.util.ArrayList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by hemant on 3/8/17.
 */
public class UsersRecommendationTabFragment extends BaseFragment implements
        UsersRecommendationsRecycleAdapter.RecyclerViewClickListener {

    private ArrayList<ArticleListingResult> recommendationsList;
    private String authorId;
    private UsersRecommendationsRecycleAdapter adapter;

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView noBlogsTextView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = getActivity().getLayoutInflater()
                .inflate(R.layout.users_recommendation_tab_fragment, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        noBlogsTextView = view.findViewById(R.id.noBlogsTextView);
        progressBar = view.findViewById(R.id.progressBar);

        authorId = getArguments().getString(Constants.AUTHOR_ID);

        adapter = new UsersRecommendationsRecycleAdapter(getActivity(), this);
        final LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(adapter);
        recommendationsList = new ArrayList<>();
        progressBar.setVisibility(View.VISIBLE);
        getUsersRecommendations();
        return view;
    }

    private void getUsersRecommendations() {
        if (!ConnectivityUtils.isNetworkEnabled(getActivity())) {
            ((UserActivitiesActivity) getActivity()).showToast(getString(R.string.connectivity_unavailable));
            return;
        }

        Retrofit retro = BaseApplication.getInstance().getRetrofit();
        BloggerDashboardAPI bloggerDashboardApi = retro.create(BloggerDashboardAPI.class);
        final Call<ArticleListingResponse> call = bloggerDashboardApi.getUsersRecommendation(authorId);
        call.enqueue(usersRecommendationsResponseListener);
    }

    private Callback<ArticleListingResponse> usersRecommendationsResponseListener =
            new Callback<ArticleListingResponse>() {
                @Override
                public void onResponse(Call<ArticleListingResponse> call,
                        retrofit2.Response<ArticleListingResponse> response) {
                    progressBar.setVisibility(View.GONE);
                    if (null == response.body()) {
                        NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                        FirebaseCrashlytics.getInstance().recordException(nee);
                        return;
                    }
                    try {
                        ArticleListingResponse responseData = response.body();
                        if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                            processRecommendationsResponse(responseData);
                        }
                    } catch (Exception e) {
                        FirebaseCrashlytics.getInstance().recordException(e);
                        Log.d("MC4kException", Log.getStackTraceString(e));
                    }
                }

                @Override
                public void onFailure(Call<ArticleListingResponse> call, Throwable t) {
                    progressBar.setVisibility(View.GONE);
                    FirebaseCrashlytics.getInstance().recordException(t);
                    Log.d("MC4kException", Log.getStackTraceString(t));
                }
            };

    private void processRecommendationsResponse(ArticleListingResponse responseData) {
        ArrayList<ArticleListingResult> dataList = responseData.getData().get(0).getResult();
        if (dataList.size() == 0) {
            noBlogsTextView.setVisibility(View.VISIBLE);
        } else {
            recommendationsList.addAll(dataList);
            adapter.setListData(recommendationsList);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onClick(View view, int position) {
        switch (view.getId()) {
            case R.id.shareImageView:
                if ("1".equals(recommendationsList.get(position).getContentType())) {
                    Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");

                    String shareUrl = AppUtils.getShortStoryShareUrl(recommendationsList.get(position).getUserType(),
                            recommendationsList.get(position).getBlogPageSlug(),
                            recommendationsList.get(position).getTitleSlug());
                    String shareMessage;
                    if (StringUtils.isNullOrEmpty(shareUrl)) {
                        shareMessage =
                                getString(R.string.check_out_short_story) + "\"" + recommendationsList.get(position)
                                        .getTitle() + "\" by " + recommendationsList.get(position).getUserName() + ".";
                    } else {
                        shareMessage =
                                getString(R.string.check_out_short_story) + "\"" + recommendationsList.get(position)
                                        .getTitle() + "\" by " + recommendationsList.get(position).getUserName()
                                        + ".\nRead Here: " + shareUrl;
                    }
                    shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareMessage);
                    startActivity(Intent.createChooser(shareIntent, "Momspresso"));
                } else {
                    Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    String shareUrl = AppUtils.getShareUrl(recommendationsList.get(position).getUserType(),
                            recommendationsList.get(position).getBlogPageSlug(),
                            recommendationsList.get(position).getTitleSlug());
                    String shareMessage;
                    if (StringUtils.isNullOrEmpty(shareUrl)) {
                        shareMessage =
                                getString(R.string.check_out_blog) + "\"" + recommendationsList.get(position).getTitle()
                                        + "\" by " + recommendationsList.get(position).getUserName() + ".";
                    } else {
                        shareMessage =
                                getString(R.string.check_out_blog) + "\"" + recommendationsList.get(position).getTitle()
                                        + "\" by " + recommendationsList.get(position).getUserName() + ".\nRead Here: "
                                        + shareUrl;
                    }
                    shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareMessage);
                    startActivity(Intent.createChooser(shareIntent, "Momspresso"));
                    if (authorId.equals(SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext())
                            .getDynamoId())) {
                        Utils.pushShareArticleEvent(getActivity(), "PrivateLikedScreen",
                                SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId() + "",
                                recommendationsList.get(position).getId(),
                                recommendationsList.get(position).getUserId() + "~" + recommendationsList.get(position)
                                        .getUserName(), "-");
                    } else {
                        Utils.pushShareArticleEvent(getActivity(), "PublicLikedScreen",
                                SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId() + "",
                                recommendationsList.get(position).getId(),
                                recommendationsList.get(position).getUserId() + "~" + recommendationsList.get(position)
                                        .getUserName(), "-");
                    }
                }
                break;
            case R.id.rootView:
                if ("1".equals(recommendationsList.get(position).getContentType())) {
                    Intent intent = new Intent(getActivity(), ShortStoryContainerActivity.class);
                    intent.putExtra(Constants.ARTICLE_ID, recommendationsList.get(position).getId());
                    intent.putExtra(Constants.AUTHOR_ID, recommendationsList.get(position).getUserId());
                    intent.putExtra(Constants.BLOG_SLUG, recommendationsList.get(position).getBlogPageSlug());
                    intent.putExtra(Constants.TITLE_SLUG, recommendationsList.get(position).getTitleSlug());
                    if (authorId.equals(SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext())
                            .getDynamoId())) {
                        intent.putExtra(Constants.ARTICLE_OPENED_FROM, "PrivatePublishedArticles");
                        intent.putExtra(Constants.FROM_SCREEN, "PrivateUserArticlesScreen");
                    } else {
                        intent.putExtra(Constants.ARTICLE_OPENED_FROM, "PublicPublishedArticles");
                        intent.putExtra(Constants.FROM_SCREEN, "PublicUserArticlesScreen");
                    }
                    intent.putExtra(Constants.AUTHOR,
                            recommendationsList.get(position).getUserId() + "~" + recommendationsList.get(position)
                                    .getUserName());
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(getActivity(), ArticleDetailsContainerActivity.class);
                    intent.putExtra(Constants.ARTICLE_ID, recommendationsList.get(position).getId());
                    intent.putExtra(Constants.AUTHOR_ID, recommendationsList.get(position).getUserId());
                    intent.putExtra(Constants.BLOG_SLUG, recommendationsList.get(position).getBlogPageSlug());
                    intent.putExtra(Constants.TITLE_SLUG, recommendationsList.get(position).getTitleSlug());
                    intent.putExtra(Constants.AUTHOR,
                            recommendationsList.get(position).getUserId() + "~" + recommendationsList.get(position)
                                    .getUserName());
                    if (authorId.equals(SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext())
                            .getDynamoId())) {
                        intent.putExtra(Constants.ARTICLE_OPENED_FROM, "UserPrivateLikes");
                        intent.putExtra(Constants.FROM_SCREEN, "PrivateProfileScreen");
                    } else {
                        intent.putExtra(Constants.ARTICLE_OPENED_FROM, "UserPublicLikes");
                        intent.putExtra(Constants.FROM_SCREEN, "PublicProfileScreen");
                    }
                    startActivity(intent);
                }
                break;
            default:
                break;
        }
    }
}
