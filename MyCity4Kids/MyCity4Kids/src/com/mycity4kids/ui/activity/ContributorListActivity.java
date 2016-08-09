package com.mycity4kids.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.kelltontech.utils.ConnectivityUtils;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.controller.NewParentingBlogController;
import com.mycity4kids.models.parentingstop.ParentingRequest;
import com.mycity4kids.models.response.ArticleListingResult;
import com.mycity4kids.models.response.ContributorListResponse;
import com.mycity4kids.models.response.ContributorListResult;
import com.mycity4kids.models.response.UserCommentsResponse;
import com.mycity4kids.newmodels.bloggermodel.BlogItemModel;
import com.mycity4kids.newmodels.bloggermodel.ParentingBlogResponse;
import com.mycity4kids.retrofitAPIsInterfaces.BloggerDashboardAPI;
import com.mycity4kids.retrofitAPIsInterfaces.ContributorListAPI;
import com.mycity4kids.ui.adapter.ParentingBlogAdapter;
import com.mycity4kids.ui.fragment.ParentingBlogFragment;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by anshul on 8/7/16.
 */
public class ContributorListActivity extends BaseActivity implements View.OnClickListener {
    ListView blogListing;
    ParentingBlogAdapter parentingBlogAdapter;
    private RelativeLayout mLodingView;
    private int totalPageCount = 2;
    private int nextPageNumber = 0;
    Boolean isSortEnable = false;
    ArrayList<BlogItemModel> listingData;
    ArrayList<BlogItemModel> orignalListingData;
    Boolean isReuqestRunning = false;
    private Boolean isLastPageReached = false;
    private int limit = 10;
    private String paginationValue = "";
    ArrayList<ContributorListResult> contributorArrayList;
    Toolbar mToolBar;
    FloatingActionButton rankFab, nameFab;
    int sortType = 1;
    String type=AppConstants.USER_TYPE_BLOGGER;
    FloatingActionsMenu fab_menu;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parenting_blog_home);
        mToolBar = (Toolbar) findViewById(R.id.toolbar);
        mToolBar.setVisibility(View.VISIBLE);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("CONTRIBUTORS");
        blogListing = (ListView) findViewById(R.id.blog_listing);
        mLodingView = (RelativeLayout) findViewById(R.id.relativeLoadingView);
        rankFab = (FloatingActionButton) findViewById(R.id.rankSortFAB);
        nameFab = (FloatingActionButton) findViewById(R.id.nameSortFAB);
        fab_menu=(FloatingActionsMenu) findViewById(R.id.fab_menu);
        final FloatingActionsMenu fabMenu = (FloatingActionsMenu) findViewById(R.id.fab_menu);
        findViewById(R.id.imgLoader).startAnimation(AnimationUtils.loadAnimation(this, R.anim.rotate_indefinitely));
        contributorArrayList = new ArrayList<>();
        orignalListingData = new ArrayList<>();
        rankFab.setOnClickListener(ContributorListActivity.this);
        nameFab.setOnClickListener(this);
        listingData = new ArrayList<>();

        showProgressDialog(getString(R.string.please_wait));
        hitBloggerAPIrequest(sortType,type);

        blogListing.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                boolean loadMore = firstVisibleItem + visibleItemCount >= totalItemCount;

                if (isOnline()) {

                    if (visibleItemCount != 0 && loadMore && firstVisibleItem != 0 && !isReuqestRunning && nextPageNumber < totalPageCount && !isLastPageReached) {


                        isReuqestRunning = true;
                        mLodingView.setVisibility(View.VISIBLE);
                        hitBloggerAPIrequest(sortType,type);

                    }

                }
            }
        });

        blogListing.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                ContributorListResult itemSelected = (ContributorListResult) adapterView.getItemAtPosition(position);

//                if (!StringUtils.isNullOrEmpty(itemSelected.getBlog_title())) {
                Intent intent = new Intent(ContributorListActivity.this, BloggerDashboardActivity.class);
                intent.putExtra(AppConstants.PUBLIC_PROFILE_USER_ID, itemSelected.getId());
                startActivity(intent);
//                } else {
//                    ToastUtils.showToast(getActivity(), "Blogger details not available at this moment, please try again later...");
//                }

            }
        });

        parentingBlogAdapter = new ParentingBlogAdapter(ContributorListActivity.this, contributorArrayList);
        blogListing.setAdapter(parentingBlogAdapter);

    }


    @Override
    public void onResume() {
        super.onResume();

        blogListing.invalidate();

    }

    @Override
    protected void updateUi(Response response) {

        ParentingBlogResponse responseData;
/*
        if (response == null) {
            showToast("Something went wrong from server");
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
                            showToast(message);
                        } else {
                            showToast(getString(R.string.went_wrong));
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
                    showToast(getString(R.string.went_wrong));
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
                            showToast(message);
                        } else {
                            showToast(getString(R.string.went_wrong));
                        }

                    }

                } catch (Exception e) {
                    showToast(getString(R.string.went_wrong));
                    e.printStackTrace();
                }

                removeProgressDialog();
                isReuqestRunning = false;

                break;
        }*/
    }


 /*   private void updatBloggerResponse(ParentingBlogResponse responseData) {

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
*/

    public void hitBloggerAPIrequest(int sortType,String type) {

        showProgressDialog(getResources().getString(R.string.please_wait));
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        // prepare call in Retrofit 2.0
        ContributorListAPI contributorListAPI = retrofit.create(ContributorListAPI.class);
        if (!ConnectivityUtils.isNetworkEnabled(this)) {
            removeProgressDialog();
            showToast(getString(R.string.error_network));
            return;
        }
        Call<ContributorListResponse> call = contributorListAPI.getContributorList(AppConstants.LIVE_URL + "v1/users/?limit=" + limit + "&sortType=" + sortType + "&type="+type + "&pagination=" + paginationValue);
//asynchronous call
        call.enqueue(new Callback<ContributorListResponse>() {
                         @Override
                         public void onResponse(Call<ContributorListResponse> call, retrofit2.Response<ContributorListResponse> response) {
                             int statusCode = response.code();
                             removeProgressDialog();

                             ContributorListResponse responseModel = response.body();
                             if (responseModel.getCode() != 200) {
                                 showToast(getString(R.string.toast_response_error));
                                 mLodingView.setVisibility(View.GONE);
                                 return;
                             } else {
                                 if (!StringUtils.isNullOrEmpty(responseModel.getData().getMsg())) {
                                     Log.i("Draft message", responseModel.getData().getMsg());
                                 }
                                 processResponse(responseModel);
                                 //     isReuqestCommentsRunning = false;
                             }
                             isReuqestRunning = false;
                             mLodingView.setVisibility(View.GONE);
                         }

                         @Override
                         public void onFailure(Call<ContributorListResponse> call, Throwable t) {
                             removeProgressDialog();

                         }
                     }
        );

    }

    /*    public void hitSortBloggerAPIrequest(int page, String sort) {

            ParentingRequest _parentingModel = new ParentingRequest();

            _parentingModel.setPage(String.valueOf(page));
            _parentingModel.setSoty_by(sort);

            if (nextPageNumber == 0) {
                showProgressDialog(getString(R.string.please_wait));
            } else {
                mLodingView.setVisibility(View.VISIBLE);
            }

            NewParentingBlogController newParentingBlogController = new NewParentingBlogController(ContributorListActivity.this, this);
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
    */
    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
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

    public void processResponse(ContributorListResponse responseModel) {
        ArrayList<ContributorListResult> dataList = responseModel.getData().getResult();
        if (dataList.size() == 0) {

            isLastPageReached = true;
            if (null != contributorArrayList && !contributorArrayList.isEmpty()) {
                //No more next results for search from pagination

            } else {
                // No results for search
                contributorArrayList.clear();
                contributorArrayList.addAll(dataList);
//                parentingBlogAdapter.setListData(contributorArrayList);

                parentingBlogAdapter.notifyDataSetChanged();
               /* noBlogsTextView.setVisibility(View.VISIBLE);
                noBlogsTextView.setText("No articles found");*/
            }

//            articleDataModelsNew = dataList;
//            articlesListingAdapter.setNewListData(articleDataModelsNew);
//            articlesListingAdapter.notifyDataSetChanged();
//            noBlogsTextView.setVisibility(View.VISIBLE);
//            noBlogsTextView.setText("No articles found");
        } else {
            //  noBlogsTextView.setVisibility(View.GONE);
//            totalPageCount = responseData.getResult().getData().getPage_count();


            if (StringUtils.isNullOrEmpty(paginationValue)) {
//                    contributorArrayList = dataList;
                contributorArrayList.clear();
                contributorArrayList.addAll(dataList);
            } else {
                contributorArrayList.addAll(dataList);
            }
//            parentingBlogAdapter.setListData(contributorArrayList);
            paginationValue = responseModel.getData().getPagination();
            if (AppConstants.PAGINATION_END_VALUE.equals(paginationValue)) {
                isLastPageReached = true;
            }
          /*  parentingBlogAdapter=new ParentingBlogAdapter(ContributorListActivity.this,contributorArrayList);
            blogListing.setAdapter(parentingBlogAdapter);*/

            // parentingBlogAdapter.setNewListData(articleDataModelsNew);
            parentingBlogAdapter.notifyDataSetChanged();

        }
    }

   /* public void updateList_followBtn(int blogListPosition) {

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

    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //  return super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_filter, menu);
        MenuItem item = menu.findItem(R.id.filter);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.filter:
                Intent intent = new Intent(getApplicationContext(), BlogFilterActivity.class);
                startActivityForResult(intent, Constants.FILTER_BLOG);
                break;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
        case Constants.FILTER_BLOG:
      sortParentingBlogListing(data.getStringExtra(Constants.FILTER_BLOG_SORT_TYPE));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rankSortFAB:
                sortType = 0;
                hitBloggerAPIrequest(sortType,type);
                break;
            case R.id.nameSortFAB:
                sortType = 1;
                hitBloggerAPIrequest(sortType,type);
                break;
        }

    }
    public void sortParentingBlogListing(String type) {

        isSortEnable = true;
        this.type = type;
        contributorArrayList.clear();
        parentingBlogAdapter.notifyDataSetChanged();

//        listingData.clear();
        isReuqestRunning = true;
        if (!type.equals(AppConstants.USER_TYPE_BLOGGER))
        {
            fab_menu.setVisibility(View.GONE);
        }
        else
        {
            fab_menu.setVisibility(View.VISIBLE);
        }
        hitBloggerAPIrequest(sortType,type);

    }
}
