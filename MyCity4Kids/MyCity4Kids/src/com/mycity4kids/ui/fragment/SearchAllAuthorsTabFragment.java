package com.mycity4kids.ui.fragment;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.crashlytics.android.Crashlytics;
import com.kelltontech.ui.BaseFragment;
import com.kelltontech.utils.ConnectivityUtils;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.models.response.SearchAuthorResult;
import com.mycity4kids.models.response.SearchResponse;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.profile.UserProfileActivity;
import com.mycity4kids.retrofitAPIsInterfaces.SearchArticlesAuthorsAPI;
import com.mycity4kids.ui.activity.SearchAllActivity;
import com.mycity4kids.ui.adapter.SearchAuthorsListingAdapter;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by hemant.parmar on 21-04-2016.
 */
public class SearchAllAuthorsTabFragment extends BaseFragment {

    private String dynamoUserId;
    private SearchAuthorsListingAdapter authorsListingAdapter;
    private ListView listView;
    private TextView noAuthorsTextView;
    private String searchName = "";
    private RelativeLayout mLodingView;
    private int nextPageNumber = 0;
    private ArrayList<SearchAuthorResult> listingData;
    private boolean isReuqestRunning = false;
    private boolean fragmentResume = false;
    private boolean fragmentVisible = false;
    boolean loadMore = true;
    private ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = null;
        view = getActivity().getLayoutInflater().inflate(R.layout.fragment_author_listing, container, false);

        dynamoUserId = SharedPrefUtils.getUserDetailModel(getActivity()).getDynamoId();

        listView = (ListView) view.findViewById(R.id.authorListView);
        listView.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.white_color));

        ColorDrawable sage = new ColorDrawable(ContextCompat.getColor(getActivity(), R.color.gray2));
        listView.setDivider(sage);
        listView.setDividerHeight(1);

        mLodingView = (RelativeLayout) view.findViewById(R.id.relativeLoadingView);
        noAuthorsTextView = (TextView) view.findViewById(R.id.noAuthorsTextView);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        view.findViewById(R.id.imgLoader).startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.rotate_indefinitely));

        listingData = new ArrayList<SearchAuthorResult>();
        authorsListingAdapter = new SearchAuthorsListingAdapter(getActivity());
        listView.setAdapter(authorsListingAdapter);
        if (getArguments() != null) {
            searchName = getArguments().getString(Constants.SEARCH_PARAM);
        }
        if (StringUtils.isNullOrEmpty(searchName)) {

        } else if (!fragmentResume && fragmentVisible) {   //only when first time fragment is created
            nextPageNumber = 1;
            hitBloggerAPIrequest(nextPageNumber);
        }

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                boolean isPageEndReached = firstVisibleItem + visibleItemCount >= totalItemCount;
                if (visibleItemCount != 0 && loadMore && isPageEndReached && firstVisibleItem != 0 && !isReuqestRunning) {
                    isReuqestRunning = true;
                    mLodingView.setVisibility(View.VISIBLE);
                    hitBloggerAPIrequest(nextPageNumber);
                }
            }
        });


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                SearchAuthorResult itemSelected = (SearchAuthorResult) adapterView.getItemAtPosition(position);
                Intent profileIntent = new Intent(getActivity(), UserProfileActivity.class);
                profileIntent.putExtra(Constants.USER_ID, itemSelected.getUserId());
                startActivity(profileIntent);
            }
        });

        return view;
    }

    private void updateBloggerResponse(SearchResponse responseData) {

        ArrayList<SearchAuthorResult> dataList = responseData.getData().getResult().getAuthor();

        if (dataList.size() == 0) {
            loadMore = false;
            if (null != listingData && !listingData.isEmpty()) {
                //No more next results for search from pagination

            } else {
                // No results for search
                listingData = dataList;
                authorsListingAdapter.setNewListData(dataList);
                authorsListingAdapter.notifyDataSetChanged();
                noAuthorsTextView.setVisibility(View.VISIBLE);
            }

            //((SearchAllActivity) getActivity()).showToast(responseData.getResult().getMessage());
        } else {
            noAuthorsTextView.setVisibility(View.GONE);
            if (nextPageNumber == 1) {
                listingData = dataList;
            } else {
                listingData.addAll(dataList);
            }
            authorsListingAdapter.setNewListData(listingData);
            nextPageNumber = nextPageNumber + 1;
            authorsListingAdapter.notifyDataSetChanged();
        }
    }

    public void hitBloggerAPIrequest(int page) {

        if (nextPageNumber == 1 && null != progressBar) {
            progressBar.setVisibility(View.VISIBLE);
        }

        if (!ConnectivityUtils.isNetworkEnabled(getActivity())) {
            if (isAdded())
                ((SearchAllActivity) getActivity()).showToast(getString(R.string.connectivity_unavailable));
            return;
        }

        Retrofit retro = BaseApplication.getInstance().getRetrofit();
        SearchArticlesAuthorsAPI searchArticlesAuthorsAPI = retro.create(SearchArticlesAuthorsAPI.class);
        int from = (nextPageNumber - 1) * 15 + 1;
        Call<SearchResponse> call = searchArticlesAuthorsAPI.getSearchAuthorsResult(searchName,
                "author", from, from + 15);

        call.enqueue(searchAuthorsResponseCallback);
    }

    public void refreshAllAuthors(String searchTxt) {
        if (null != listingData) {
            listingData.clear();
        }
        nextPageNumber = 1;
        loadMore = true;
        searchName = searchTxt;
        hitBloggerAPIrequest(nextPageNumber);
    }

    public void resetOnceLoadedFlag(String searchTxt) {
        if (null != listingData) {
            listingData.clear();
        }
        loadMore = true;
        searchName = searchTxt;
    }


    Callback<SearchResponse> searchAuthorsResponseCallback = new Callback<SearchResponse>() {
        @Override
        public void onResponse(Call<SearchResponse> call, retrofit2.Response<SearchResponse> response) {
            isReuqestRunning = false;
            progressBar.setVisibility(View.INVISIBLE);
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
                    updateBloggerResponse(responseData);
                } else {
                    ((SearchAllActivity) getActivity()).showToast(responseData.getReason());
                }
            } catch (Exception e) {
                ((SearchAllActivity) getActivity()).showToast(getString(R.string.server_went_wrong));
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }

        }

        @Override
        public void onFailure(Call<SearchResponse> call, Throwable t) {
            progressBar.setVisibility(View.GONE);
            if (mLodingView.getVisibility() == View.VISIBLE) {
                mLodingView.setVisibility(View.GONE);
            }
            if (getActivity() != null) {
                ((SearchAllActivity) getActivity()).showToast(getString(R.string.server_went_wrong));
            }
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };
}
