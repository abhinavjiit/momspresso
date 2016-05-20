package com.mycity4kids.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseFragment;
import com.kelltontech.utils.StringUtils;
import com.kelltontech.utils.ToastUtils;
import com.mycity4kids.R;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.controller.NewParentingBlogController;
import com.mycity4kids.models.parentingstop.ParentingRequest;
import com.mycity4kids.newmodels.bloggermodel.BlogItemModel;
import com.mycity4kids.newmodels.bloggermodel.ParentingBlogResponse;
import com.mycity4kids.ui.activity.BlogDetailActivity;
import com.mycity4kids.ui.activity.DashboardActivity;
import com.mycity4kids.ui.adapter.ParentingBlogAdapter;

import java.util.ArrayList;

/**
 * Created by manish.soni on 27-07-2015.
 */
public class ParentingBlogFragment extends BaseFragment implements View.OnClickListener {

    ListView blogListing;
    ParentingBlogAdapter parentingBlogAdapter;
    private RelativeLayout mLodingView;
    private int totalPageCount = 2;
    private int nextPageNumber = 0;
    private String sortType = "";
    Boolean isSortEnable = false;
    ArrayList<BlogItemModel> listingData;
    ArrayList<BlogItemModel> orignalListingData;
    Boolean isReuqestRunning = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.parenting_blog_home, container, false);
        blogListing = (ListView) view.findViewById(R.id.blog_listing);
        mLodingView = (RelativeLayout) view.findViewById(R.id.relativeLoadingView);
        view.findViewById(R.id.imgLoader).startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.rotate_indefinitely));

        orignalListingData = new ArrayList<>();

        parentingBlogAdapter = new ParentingBlogAdapter(getActivity(), null);
        blogListing.setAdapter(parentingBlogAdapter);
        listingData = new ArrayList<>();

        showProgressDialog(getString(R.string.please_wait));
        hitBloggerAPIrequest(nextPageNumber);

        blogListing.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                boolean loadMore = firstVisibleItem + visibleItemCount >= totalItemCount;

                if (isOnline()) {

                    if (visibleItemCount != 0 && loadMore && firstVisibleItem != 0 && !isReuqestRunning && nextPageNumber < totalPageCount) {

                        if (isSortEnable == false) {
                            isReuqestRunning = true;
                            mLodingView.setVisibility(View.VISIBLE);
                            hitBloggerAPIrequest(nextPageNumber);

                        } else if (isSortEnable) {
                            isReuqestRunning = true;
                            mLodingView.setVisibility(View.VISIBLE);
                            hitSortBloggerAPIrequest(nextPageNumber, sortType);
                        }
                    }

                }
            }
        });

        blogListing.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                BlogItemModel itemSelected = (BlogItemModel) adapterView.getItemAtPosition(position);

//                if (!StringUtils.isNullOrEmpty(itemSelected.getBlog_title())) {
                    Intent intent = new Intent(getActivity(), BlogDetailActivity.class);
                    Bundle bundle = new Bundle();
                    intent.putExtra(Constants.IS_COMMING_FROM_LISTING, true);
                    intent.putExtra(Constants.AUTHOR_ID, ""+itemSelected.getId());
                    intent.putExtra(Constants.BLOG_DETAILS, itemSelected);
                    intent.putExtra(Constants.BLOG_LIST_POSITION, position);
                    getActivity().startActivityForResult(intent, Constants.BLOG_FOLLOW_STATUS);
//                } else {
//                    ToastUtils.showToast(getActivity(), "Blogger details not available at this moment, please try again later...");
//                }

            }
        });

        return view;
    }


    @Override
    public void onResume() {
        super.onResume();

        blogListing.invalidate();

    }

    @Override
    protected void updateUi(Response response) {

        ParentingBlogResponse responseData;

        if (response == null) {
            ((DashboardActivity) getActivity()).showToast("Something went wrong from server");
            removeProgressDialog();
            isReuqestRunning = false;
            mLodingView.setVisibility(View.GONE);
            return;
        }

        switch (response.getDataType()) {

            case AppConstants.PARRENTING_BLOG_DATA:
                responseData = (ParentingBlogResponse) response.getResponseObject();

                try {
                    if (responseData.getResponseCode() == 200) {
                        if (mLodingView.getVisibility() == View.VISIBLE) {
                            mLodingView.setVisibility(View.GONE);
                        }
                        isReuqestRunning = false;
                        totalPageCount = Integer.parseInt(responseData.getResult().getData().getPage_count());
                        updatBloggerResponse(responseData);

                    } else if (responseData.getResponseCode() == 400) {
                        if (mLodingView.getVisibility() == View.VISIBLE) {
                            mLodingView.setVisibility(View.GONE);
                        }
                        isReuqestRunning = false;
                        String message = responseData.getResult().getMessage();
                        if (!StringUtils.isNullOrEmpty(message)) {
                            ((DashboardActivity) getActivity()).showToast(message);
                        } else {
                            ((DashboardActivity) getActivity()).showToast(getString(R.string.went_wrong));
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
                    ((DashboardActivity) getActivity()).showToast(getString(R.string.went_wrong));
                    break;
                }

            case AppConstants.PARRENTING_BLOG_SORT_DATA:
                responseData = (ParentingBlogResponse) response.getResponseObject();
                try {

                    if (responseData.getResponseCode() == 200) {
                        if (mLodingView.getVisibility() == View.VISIBLE) {
                            mLodingView.setVisibility(View.GONE);
                        }

                        if (nextPageNumber == 0) {
                            listingData.clear();
                        }

                        totalPageCount = Integer.parseInt(responseData.getResult().getData().getPage_count());
                        updatBloggerResponse(responseData);


                    } else if (responseData.getResponseCode() == 400) {
                        if (mLodingView.getVisibility() == View.VISIBLE) {
                            mLodingView.setVisibility(View.GONE);
                        }
                        isReuqestRunning = false;
                        String message = responseData.getResult().getMessage();
                        if (!StringUtils.isNullOrEmpty(message)) {
                            ((DashboardActivity) getActivity()).showToast(message);
                        } else {
                            ((DashboardActivity) getActivity()).showToast(getString(R.string.went_wrong));
                        }

                    }

                } catch (Exception e) {
                    ((DashboardActivity) getActivity()).showToast(getString(R.string.went_wrong));
                    e.printStackTrace();
                }

                removeProgressDialog();
                isReuqestRunning = false;

                break;
        }
    }


    private void updatBloggerResponse(ParentingBlogResponse responseData) {

        if (isSortEnable) {
            if (nextPageNumber == 0) {
                parentingBlogAdapter.setListData(responseData.getResult().getData().getData());
            } else {
               // listingData = parentingBlogAdapter.getListData();
                listingData.addAll(responseData.getResult().getData().getData());
                parentingBlogAdapter.setListData(listingData);
            }
        } else {
            if (nextPageNumber == 0) {
                parentingBlogAdapter.setListData(responseData.getResult().getData().getData());
                listingData = responseData.getResult().getData().getData();
            } else {
                listingData = parentingBlogAdapter.getListData();
                listingData.addAll(responseData.getResult().getData().getData());
                parentingBlogAdapter.setListData(listingData);
            }

        }
        parentingBlogAdapter.notifyDataSetChanged();

        if (nextPageNumber == 0) {
            blogListing.smoothScrollToPosition(0);
        }

        isReuqestRunning = false;
        nextPageNumber = nextPageNumber + 1;

    }

    @Override
    public void onClick(View view) {

    }

    public void hitBloggerAPIrequest(int page) {

//        showProgressDialog(getString(R.string.please_wait));
        ParentingRequest _parentingModel = new ParentingRequest();
        if (!String.valueOf(page).equalsIgnoreCase("0"))
            _parentingModel.setPage(String.valueOf(page));

        NewParentingBlogController newParentingBlogController = new NewParentingBlogController(getActivity(), this);
        newParentingBlogController.getData(AppConstants.PARRENTING_BLOG_DATA, _parentingModel);

    }

    public void hitSortBloggerAPIrequest(int page, String sort) {

        ParentingRequest _parentingModel = new ParentingRequest();

        _parentingModel.setPage(String.valueOf(page));
        _parentingModel.setSoty_by(sort);

        if (nextPageNumber == 0) {
            showProgressDialog(getString(R.string.please_wait));
        } else {
            mLodingView.setVisibility(View.VISIBLE);
        }

        NewParentingBlogController newParentingBlogController = new NewParentingBlogController(getActivity(), this);
        newParentingBlogController.getData(AppConstants.PARRENTING_BLOG_SORT_DATA, _parentingModel);

    }

    public void sortParentingBlogListing(String sotyType) {

        isSortEnable = true;
        this.sortType = sotyType;
        nextPageNumber = 0;
        totalPageCount = 1;
        orignalListingData = listingData;
//        listingData.clear();
        isReuqestRunning = true;
        hitSortBloggerAPIrequest(nextPageNumber, sotyType);

    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }


    private void sendScrollTop() {
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
//                        blogListing.scrollBy(0, 0);
//                        blogListing.setSelectionAfterHeaderView();
                        blogListing.setSelection(0);
                    }
                });
            }
        }).start();
    }

    public void updateList_followBtn(int blogListPosition) {

        ArrayList<BlogItemModel> datalist = parentingBlogAdapter.getListData();
        BlogItemModel model = datalist.get(blogListPosition);

        if (datalist.get(blogListPosition).getUser_following_status().equals("0")) {
            datalist.get(blogListPosition).setUser_following_status("1");
        } else {
            datalist.get(blogListPosition).setUser_following_status("0");
        }

        datalist.set(blogListPosition, model);
        parentingBlogAdapter.setListData(datalist);
        parentingBlogAdapter.notifyDataSetChanged();

    }
}
