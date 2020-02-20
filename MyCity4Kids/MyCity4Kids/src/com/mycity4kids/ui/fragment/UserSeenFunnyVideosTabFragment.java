package com.mycity4kids.ui.fragment;

import android.accounts.NetworkErrorException;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.kelltontech.ui.BaseFragment;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.models.response.VlogsListingAndDetailResult;
import com.mycity4kids.models.response.VlogsListingResponse;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.VlogsListingAndDetailsAPI;
import com.mycity4kids.ui.activity.ParallelFeedActivity;
import com.mycity4kids.ui.activity.SearchAllActivity;
import com.mycity4kids.ui.activity.UserReadArticlesContentActivity;
import com.mycity4kids.ui.adapter.SeenVideosAdapter;

import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by hemant on 13/1/17.
 */
public class UserSeenFunnyVideosTabFragment extends BaseFragment implements View.OnClickListener, SeenVideosAdapter.RecyclerViewClickListener {

    private SeenVideosAdapter articlesListingAdapter;
    private ArrayList<VlogsListingAndDetailResult> articleDataModelsNew;
    private boolean isLastPageReached = true;
    private int pastVisiblesItems, visibleItemCount, totalItemCount;
    private int nextPageNumber = 1;
    private boolean isReuqestRunning = true;
    private int chunk = 0;
    private String authorId;
    private RecyclerView seenVideosRecyclerView;
    private RelativeLayout mLodingView;
    private TextView noBlogsTextView;
    private ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.user_seen_videos, container, false);
        mLodingView = (RelativeLayout) view.findViewById(R.id.relativeLoadingView);
        noBlogsTextView = (TextView) view.findViewById(R.id.noBlogsTextView);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        seenVideosRecyclerView = (RecyclerView) view.findViewById(R.id.seenVideos);
        authorId = getArguments().getString(Constants.AUTHOR_ID);
        seenVideosRecyclerView.setVisibility(View.VISIBLE);
        view.findViewById(R.id.relativeLoadingView).startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.rotate_indefinitely));

        if (StringUtils.isNullOrEmpty(authorId)) {
            authorId = SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId();
        }

        progressBar.setVisibility(View.VISIBLE);

        final LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(RecyclerView.VERTICAL);
        seenVideosRecyclerView.setLayoutManager(llm);
        articleDataModelsNew = new ArrayList<>();

        articlesListingAdapter = new SeenVideosAdapter(getActivity(), this);
        articlesListingAdapter.setNewListData(articleDataModelsNew);
        seenVideosRecyclerView.setAdapter(articlesListingAdapter);
        articlesListingAdapter.notifyDataSetChanged();
        hitArticleListingApi();
        seenVideosRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) //check for scroll down
                {
                    visibleItemCount = llm.getChildCount();
                    totalItemCount = llm.getItemCount();
                    pastVisiblesItems = llm.findFirstVisibleItemPosition();

                    if (!isReuqestRunning && !isLastPageReached) {
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                            isReuqestRunning = true;
                            mLodingView.setVisibility(View.VISIBLE);
                            hitArticleListingApi();
                        }
                    }
                }
            }
        });
        return view;
    }

    private void hitArticleListingApi() {
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        VlogsListingAndDetailsAPI vlogsListingAndDetailsAPI = retrofit.create(VlogsListingAndDetailsAPI.class);
        Call<VlogsListingResponse> callRecentVideoArticles = vlogsListingAndDetailsAPI.getAuthorsSeenVideos(authorId, 10, chunk, "videos");
        callRecentVideoArticles.enqueue(userVideosListResponseCallback);
        progressBar.setVisibility(View.VISIBLE);
    }

    private Callback<VlogsListingResponse> userVideosListResponseCallback = new Callback<VlogsListingResponse>() {
        @Override
        public void onResponse(Call<VlogsListingResponse> call, retrofit2.Response<VlogsListingResponse> response) {
            progressBar.setVisibility(View.GONE);
            mLodingView.setVisibility(View.GONE);
            isReuqestRunning = false;
            if (null == response.body()) {
                NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                Crashlytics.logException(nee);
                if (isAdded())
                    ((UserReadArticlesContentActivity) getActivity()).showToast(getString(R.string.server_went_wrong));
                return;
            }
            try {
                VlogsListingResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    chunk = responseData.getData().get(0).getChunks();
                    processResponse(responseData);
                } else {
                    if (isAdded())
                        ((UserReadArticlesContentActivity) getActivity()).showToast(responseData.getReason());
                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
                if (isAdded())
                    ((UserReadArticlesContentActivity) getActivity()).showToast(getString(R.string.went_wrong));
            }
        }

        @Override
        public void onFailure(Call<VlogsListingResponse> call, Throwable t) {
            if (mLodingView.getVisibility() == View.VISIBLE) {
                mLodingView.setVisibility(View.GONE);
            }
            progressBar.setVisibility(View.INVISIBLE);
            Crashlytics.logException(t);
            Log.d("MC4KException", Log.getStackTraceString(t));
        }
    };

    private void processResponse(VlogsListingResponse responseData) {
        ArrayList<VlogsListingAndDetailResult> dataList = responseData.getData().get(0).getResult();
        if (dataList == null || dataList.size() == 0) {
            isLastPageReached = false;
            if (null != articleDataModelsNew && !articleDataModelsNew.isEmpty()) {
                //No more next results for search from pagination
            } else {
                // No results for search
                articleDataModelsNew = dataList;
                articlesListingAdapter.setNewListData(articleDataModelsNew);
                articlesListingAdapter.notifyDataSetChanged();
                noBlogsTextView.setVisibility(View.VISIBLE);
            }
        } else {
            noBlogsTextView.setVisibility(View.GONE);
            if (nextPageNumber == 1) {
                articleDataModelsNew.addAll(dataList);
                articlesListingAdapter.setNewListData(articleDataModelsNew);
                articlesListingAdapter.notifyDataSetChanged();
            } else {
                articleDataModelsNew.addAll(dataList);
            }
            articlesListingAdapter.setNewListData(articleDataModelsNew);
            nextPageNumber = nextPageNumber + 1;
            articlesListingAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.searchAllImageView:
                Intent searchIntent = new Intent(getActivity(), SearchAllActivity.class);
                searchIntent.putExtra(Constants.FILTER_NAME, "");
                searchIntent.putExtra(Constants.TAB_POSITION, 0);
                startActivity(searchIntent);
                break;
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        chunk = 0;
    }

    @Override
    public void onClick(View view, int position) {
        if (view.getId() == R.id.shareImageView) {
            Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            String shareUrl = articleDataModelsNew.get(position).getSharing_url();
            String shareMessage;
            if (StringUtils.isNullOrEmpty(shareUrl)) {
                shareMessage = getString(R.string.check_out_blog) + "\"" +
                        articleDataModelsNew.get(position).getTitle() + "\" by " + articleDataModelsNew.get(position).getAuthor().getFirstName() + ".";
            } else {
                shareMessage = getString(R.string.check_out_blog) + "\"" +
                        articleDataModelsNew.get(position).getTitle() + "\" by " + articleDataModelsNew.get(position).getAuthor().getFirstName() + ".\nRead Here: " + articleDataModelsNew.get(position).getSharing_url();
            }
            shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareMessage);
            startActivity(Intent.createChooser(shareIntent, "Momspresso"));
        } else {
            Intent intent = new Intent(getActivity(), ParallelFeedActivity.class);
            intent.putExtra(Constants.VIDEO_ID, articleDataModelsNew.get(position).getId());
            intent.putExtra(Constants.FROM_SCREEN, "Search Screen");
            startActivity(intent);
        }
    }
}