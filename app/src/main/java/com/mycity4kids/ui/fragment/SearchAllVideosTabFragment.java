package com.mycity4kids.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.base.BaseFragment;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.models.response.SearchResponse;
import com.mycity4kids.models.response.SearchVideoResult;
import com.mycity4kids.retrofitAPIsInterfaces.SearchArticlesAuthorsAPI;
import com.mycity4kids.ui.activity.ParallelFeedActivity;
import com.mycity4kids.ui.activity.SearchAllActivity;
import com.mycity4kids.ui.adapter.SearchVideosListingAdapter;
import com.mycity4kids.utils.ConnectivityUtils;
import com.mycity4kids.utils.StringUtils;
import java.util.ArrayList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

public class SearchAllVideosTabFragment extends BaseFragment implements
        SearchVideosListingAdapter.RecyclerViewClickListener {

    private boolean isLastPageReached = true;
    private int pastVisiblesItems, visibleItemCount, totalItemCount;
    private String searchName = "";
    private int nextPageNumber = 1;
    private boolean isReuqestRunning = true;

    private RelativeLayout mLodingView;
    SearchVideosListingAdapter searchVideosListingAdapter;
    ArrayList<SearchVideoResult> videoList;
    RecyclerView recyclerView;
    TextView noBlogsTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search_common_tab_fragment, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        noBlogsTextView = (TextView) view.findViewById(R.id.noBlogsTextView);
        mLodingView = (RelativeLayout) view.findViewById(R.id.relativeLoadingView);

        noBlogsTextView.setText(BaseApplication.getAppContext().getString(R.string.search_article_no_result));

        if (getArguments() != null) {
            searchName = getArguments().getString(Constants.SEARCH_PARAM);
        }

        final LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);

        videoList = new ArrayList<>();

        searchVideosListingAdapter = new SearchVideosListingAdapter(getActivity(), this);
        searchVideosListingAdapter.setListData(videoList);

        recyclerView.setAdapter(searchVideosListingAdapter);
        if (StringUtils.isNullOrEmpty(searchName)) {

        } else {
            searchVideoAPI();
        }

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                            searchVideoAPI();
                        }
                    }
                }
            }
        });

        return view;
    }

    private void updateTopicsListing(SearchResponse responseData) {

        ArrayList<SearchVideoResult> dataList = responseData.getData().getResult().getVideo();

        if (dataList.size() == 0) {
            isLastPageReached = false;
            if (null != videoList && !videoList.isEmpty()) {
                //No more next results for search from pagination
                isLastPageReached = true;
            } else {
                // No results for search
                videoList = dataList;
                searchVideosListingAdapter.setListData(dataList);
                searchVideosListingAdapter.notifyDataSetChanged();
                noBlogsTextView.setVisibility(View.VISIBLE);
            }

            //((SearchAllActivity) getActivity()).showToast(responseData.getResult().getMessage());
        } else {
            noBlogsTextView.setVisibility(View.GONE);
            if (nextPageNumber == 1) {
                videoList = dataList;
            } else {
                videoList.addAll(dataList);
            }
            searchVideosListingAdapter.setListData(videoList);
            nextPageNumber = nextPageNumber + 1;
            searchVideosListingAdapter.notifyDataSetChanged();
        }
    }

    public void searchVideoAPI() {

        if (!ConnectivityUtils.isNetworkEnabled(getActivity())) {
            if (isAdded()) {
                ((SearchAllActivity) getActivity()).showToast(getString(R.string.connectivity_unavailable));
            }
            return;
        }

        Retrofit retro = BaseApplication.getInstance().getRetrofit();
        SearchArticlesAuthorsAPI searchArticlesAuthorsAPI = retro.create(SearchArticlesAuthorsAPI.class);
        int from = (nextPageNumber - 1) * 15 + 1;
        Call<SearchResponse> call = searchArticlesAuthorsAPI.getSearchTopicsResult(searchName,
                "video", from, from + 15, 0);

        call.enqueue(searchTopicsResponseCallback);
    }

    public void refreshAllArticles(String searchTxt) {
        if (null != videoList) {
            videoList.clear();
        }
        nextPageNumber = 1;
        isLastPageReached = false;
        searchName = searchTxt;
        searchVideoAPI();
    }

    public void resetOnceLoadedFlag(String searchTxt) {
        if (null != videoList) {
            videoList.clear();
        }
        isLastPageReached = false;
        searchName = searchTxt;
    }


    Callback<SearchResponse> searchTopicsResponseCallback = new Callback<SearchResponse>() {
        @Override
        public void onResponse(Call<SearchResponse> call, retrofit2.Response<SearchResponse> response) {
            isReuqestRunning = false;
            if (mLodingView.getVisibility() == View.VISIBLE) {
                mLodingView.setVisibility(View.GONE);
            }
            if (response == null || response.body() == null) {
                ((SearchAllActivity) getActivity()).showToast(getString(R.string.server_went_wrong));
                return;
            }
            try {
                SearchResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    updateTopicsListing(responseData);
                } else {
                    ((SearchAllActivity) getActivity()).showToast(responseData.getReason());
                }
            } catch (Exception e) {
                ((SearchAllActivity) getActivity()).showToast(getString(R.string.server_went_wrong));
                FirebaseCrashlytics.getInstance().recordException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }

        }

        @Override
        public void onFailure(Call<SearchResponse> call, Throwable t) {
            if (mLodingView.getVisibility() == View.VISIBLE) {
                mLodingView.setVisibility(View.GONE);
            }
            if (getActivity() != null) {
                ((SearchAllActivity) getActivity()).showToast(getString(R.string.server_went_wrong));
            }
            FirebaseCrashlytics.getInstance().recordException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };


    @Override
    public void onClick(View view, int position) {
        Intent intent = new Intent(getActivity(), ParallelFeedActivity.class);
        SearchVideoResult topic = videoList.get(position);
        intent.putExtra(Constants.VIDEO_ID, topic.getId());
        intent.putExtra(Constants.FROM_SCREEN, "Search Screen");
        startActivity(intent);
    }
}
