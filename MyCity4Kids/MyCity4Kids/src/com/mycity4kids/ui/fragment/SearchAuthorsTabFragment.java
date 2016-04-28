package com.mycity4kids.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
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

import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseFragment;
import com.kelltontech.utils.StringUtils;
import com.kelltontech.utils.ToastUtils;
import com.mycity4kids.R;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.controller.NewParentingBlogController;
import com.mycity4kids.models.parentingstop.CommonParentingList;
import com.mycity4kids.models.parentingstop.ParentingRequest;
import com.mycity4kids.newmodels.bloggermodel.BlogItemModel;
import com.mycity4kids.newmodels.bloggermodel.ParentingBlogResponse;
import com.mycity4kids.ui.activity.BlogDetailActivity;
import com.mycity4kids.ui.activity.SearchArticlesAndAuthorsActivity;
import com.mycity4kids.ui.adapter.AuthorsListingAdapter;

import java.util.ArrayList;

/**
 * Created by hemant.parmar on 21-04-2016.
 */
public class SearchAuthorsTabFragment extends BaseFragment {

    AuthorsListingAdapter authorsListingAdapter;
    ListView listView;
    TextView noAuthorsTextView;
    String searchName = "";
    private RelativeLayout mLodingView;
    private int nextPageNumber = 0;
    ArrayList<BlogItemModel> listingData;
    private boolean isReuqestRunning = false;
    private boolean fragmentResume = false;
    private boolean fragmentVisible = false;
    private boolean fragmentOnCreated = false;
    private boolean isDataLoadedOnce = false;
    boolean loadMore = true;
    private ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = null;
        view = getActivity().getLayoutInflater().inflate(R.layout.fragment_author_listing, container, false);
        listView = (ListView) view.findViewById(R.id.authorListView);
        mLodingView = (RelativeLayout) view.findViewById(R.id.relativeLoadingView);
        noAuthorsTextView = (TextView) view.findViewById(R.id.noAuthorsTextView);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        view.findViewById(R.id.imgLoader).startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.rotate_indefinitely));

        listingData = new ArrayList<BlogItemModel>();
        authorsListingAdapter = new AuthorsListingAdapter(getActivity());
        listView.setAdapter(authorsListingAdapter);
        if (getArguments() != null) {
            searchName = getArguments().getString(Constants.SEARCH_PARAM);
        }
        if (StringUtils.isNullOrEmpty(searchName)) {

        } else if (!fragmentResume && fragmentVisible) {   //only when first time fragment is created
            nextPageNumber = 1;
            hitBloggerAPIrequest(nextPageNumber);
            isDataLoadedOnce = true;
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

                BlogItemModel itemSelected = (BlogItemModel) adapterView.getItemAtPosition(position);

                if (!StringUtils.isNullOrEmpty(itemSelected.getBlog_title())) {
                    Intent intent = new Intent(getActivity(), BlogDetailActivity.class);
                    Bundle bundle = new Bundle();
                    intent.putExtra(Constants.IS_COMMING_FROM_LISTING, false);
                    intent.putExtra(Constants.ARTICLE_NAME, listingData.get(position).getBlog_title());
                    intent.putExtra(Constants.FILTER_TYPE, "blogs");
                    getActivity().startActivityForResult(intent, Constants.BLOG_FOLLOW_STATUS);
                } else {
                    ToastUtils.showToast(getActivity(), "Blogger details not available at this moment, please try again later...");
                }
            }
        });

        return view;
    }

    @Override
    protected void updateUi(Response response) {

        ParentingBlogResponse responseData;
        progressBar.setVisibility(View.INVISIBLE);
        if (response == null) {
            ((SearchArticlesAndAuthorsActivity) getActivity()).showToast("Something went wrong from server");
            removeProgressDialog();
            isReuqestRunning = false;
            mLodingView.setVisibility(View.GONE);
            return;
        }

        switch (response.getDataType()) {

            case AppConstants.SEARCH_AUTHORS_REQUEST:
                responseData = (ParentingBlogResponse) response.getResponseObject();

                try {
                    if (responseData.getResponseCode() == 200) {
                        if (mLodingView.getVisibility() == View.VISIBLE) {
                            mLodingView.setVisibility(View.GONE);
                        }
                        isReuqestRunning = false;
//                        totalPageCount = Integer.parseInt(responseData.getResult().getData().getPage_count());
                        updateBloggerResponse(responseData);

                    } else if (responseData.getResponseCode() == 400) {
                        if (mLodingView.getVisibility() == View.VISIBLE) {
                            mLodingView.setVisibility(View.GONE);
                        }
                        isReuqestRunning = false;
                        String message = responseData.getResult().getMessage();
                        if (!StringUtils.isNullOrEmpty(message)) {
                            ((SearchArticlesAndAuthorsActivity) getActivity()).showToast(message);
                        } else {
                            ((SearchArticlesAndAuthorsActivity) getActivity()).showToast(getString(R.string.went_wrong));
                        }
                    }

                    removeProgressDialog();
                    break;
                } catch (Exception e) {
                    removeProgressDialog();
                    e.printStackTrace();
                    if (mLodingView.getVisibility() == View.VISIBLE) {
                        mLodingView.setVisibility(View.GONE);
                    }
                    isReuqestRunning = false;
                    ((SearchArticlesAndAuthorsActivity) getActivity()).showToast(getString(R.string.went_wrong));
                    break;
                }
        }
    }

    @Override
    public void setUserVisibleHint(boolean visible) {
        super.setUserVisibleHint(visible);
        if (visible && isResumed()) {   // only at fragment screen is resumed
            fragmentResume = true;
            fragmentVisible = false;
            fragmentOnCreated = true;
            nextPageNumber = 1;
            if (!isDataLoadedOnce && !StringUtils.isNullOrEmpty(searchName)) {
                hitBloggerAPIrequest(nextPageNumber);
                isDataLoadedOnce = true;
            }
        } else if (visible) {        // only at fragment onCreated
            fragmentResume = false;
            fragmentVisible = true;
            fragmentOnCreated = true;
        } else if (!visible && fragmentOnCreated) {// only when you go out of fragment screen
            fragmentVisible = false;
            fragmentResume = false;
        }
    }

    private void updateBloggerResponse(ParentingBlogResponse responseData) {

        ArrayList<BlogItemModel> dataList = responseData.getResult().getData().getData();

        if (dataList.size() == 0) {
            loadMore = false;
            if (null != listingData && !listingData.isEmpty()) {
                //No more next results for search

            } else {
                // No results for search
                listingData = dataList;
                authorsListingAdapter.setNewListData(dataList);
                authorsListingAdapter.notifyDataSetChanged();
                noAuthorsTextView.setVisibility(View.VISIBLE);
            }

            //((SearchArticlesAndAuthorsActivity) getActivity()).showToast(responseData.getResult().getMessage());
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

        if (nextPageNumber == 1) {
            progressBar.setVisibility(View.VISIBLE);
        }
//        showProgressDialog(getString(R.string.please_wait));
        ParentingRequest _parentingModel = new ParentingRequest();
        _parentingModel.setSearchName(searchName);
        _parentingModel.setPage("" + nextPageNumber);

        NewParentingBlogController newParentingBlogController = new NewParentingBlogController(getActivity(), this);
        newParentingBlogController.getData(AppConstants.SEARCH_AUTHORS_REQUEST, _parentingModel);
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
        isDataLoadedOnce = false;
        loadMore = true;
        searchName = searchTxt;
    }
}
