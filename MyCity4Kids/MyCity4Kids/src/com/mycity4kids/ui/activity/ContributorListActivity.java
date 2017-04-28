package com.mycity4kids.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import com.mycity4kids.gtmutils.GTMEventType;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.response.ContributorListResponse;
import com.mycity4kids.models.response.ContributorListResult;
import com.mycity4kids.newmodels.bloggermodel.BlogItemModel;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.ContributorListAPI;
import com.mycity4kids.ui.adapter.ParentingBlogAdapter;

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
    int sortType = 2;
    String type = AppConstants.USER_TYPE_BLOGGER;
    FloatingActionsMenu fab_menu;
    FrameLayout frameLayout;
    private TextView noBlogsTextView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parenting_blog_home);
        Utils.pushOpenScreenEvent(ContributorListActivity.this, "Contributor List", SharedPrefUtils.getUserDetailModel(this).getDynamoId() + "");
        mToolBar = (Toolbar) findViewById(R.id.toolbar);
        mToolBar.setVisibility(View.VISIBLE);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("CONTRIBUTORS");

        blogListing = (ListView) findViewById(R.id.blog_listing);
        mLodingView = (RelativeLayout) findViewById(R.id.relativeLoadingView);
        rankFab = (FloatingActionButton) findViewById(R.id.rankSortFAB);
        nameFab = (FloatingActionButton) findViewById(R.id.nameSortFAB);
        fab_menu = (FloatingActionsMenu) findViewById(R.id.fab_menu);
        frameLayout = (FrameLayout) findViewById(R.id.frame_layout);
        noBlogsTextView = (TextView) findViewById(R.id.noBlogsTextView);
        frameLayout.getBackground().setAlpha(0);

        findViewById(R.id.imgLoader).startAnimation(AnimationUtils.loadAnimation(this, R.anim.rotate_indefinitely));
        contributorArrayList = new ArrayList<>();
        orignalListingData = new ArrayList<>();
        rankFab.setOnClickListener(ContributorListActivity.this);
        nameFab.setOnClickListener(this);
        listingData = new ArrayList<>();

        showProgressDialog(getString(R.string.please_wait));
        hitBloggerAPIrequest(sortType, type);

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
                        hitBloggerAPIrequest(sortType, type);
                    }
                }
            }
        });

        blogListing.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                ContributorListResult itemSelected = (ContributorListResult) adapterView.getItemAtPosition(position);
                Intent intent = new Intent(ContributorListActivity.this, BloggerDashboardActivity.class);
                intent.putExtra(AppConstants.PUBLIC_PROFILE_USER_ID, itemSelected.getId());
                intent.putExtra(AppConstants.AUTHOR_NAME, itemSelected.getFirstName() + " " + itemSelected.getLastName());
                intent.putExtra(Constants.FROM_SCREEN, "Contributor List");
                startActivity(intent);

            }
        });

        parentingBlogAdapter = new ParentingBlogAdapter(ContributorListActivity.this, contributorArrayList);
        blogListing.setAdapter(parentingBlogAdapter);

        fab_menu.setOnFloatingActionsMenuUpdateListener(new FloatingActionsMenu.OnFloatingActionsMenuUpdateListener() {
            @Override
            public void onMenuExpanded() {
                frameLayout.getBackground().setAlpha(240);
                frameLayout.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        fab_menu.collapse();
                        return true;
                    }
                });
            }

            @Override
            public void onMenuCollapsed() {
                frameLayout.getBackground().setAlpha(0);
                frameLayout.setOnTouchListener(null);
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        blogListing.invalidate();
    }

    @Override
    protected void updateUi(Response response) {
    }

    public void hitBloggerAPIrequest(int sortType, String type) {

        showProgressDialog(getResources().getString(R.string.please_wait));
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        // prepare call in Retrofit 2.0
        ContributorListAPI contributorListAPI = retrofit.create(ContributorListAPI.class);
        if (!ConnectivityUtils.isNetworkEnabled(this)) {
            removeProgressDialog();
            showToast(getString(R.string.error_network));
            return;
        }
        Call<ContributorListResponse> call = contributorListAPI.getContributorList(AppConstants.LIVE_URL + "v1/users/?limit=" + limit + "&sortType=" + sortType + "&type=" + type + "&pagination=" + paginationValue);
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

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
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
                parentingBlogAdapter.notifyDataSetChanged();
                noBlogsTextView.setVisibility(View.VISIBLE);
                noBlogsTextView.setText("No result found");
            }
        } else {
            noBlogsTextView.setVisibility(View.GONE);
            if (StringUtils.isNullOrEmpty(paginationValue)) {
                contributorArrayList.clear();
                contributorArrayList.addAll(dataList);
            } else {
                contributorArrayList.addAll(dataList);
            }
            paginationValue = responseModel.getData().getPagination();
            if (AppConstants.PAGINATION_END_VALUE.equals(paginationValue)) {
                isLastPageReached = true;
            }
            parentingBlogAdapter.notifyDataSetChanged();

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_filter, menu);
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
        if (null == data) {
            return;
        }

        switch (requestCode) {
            case Constants.FILTER_BLOG:
                sortParentingBlogListing(data.getStringExtra(Constants.FILTER_BLOG_SORT_TYPE));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rankSortFAB:
                Utils.pushSortListingEvent(ContributorListActivity.this, GTMEventType.SORT_LISTING_EVENT, SharedPrefUtils.getUserDetailModel(ContributorListActivity.this).getDynamoId(), "Contributor List", "rank");
                fab_menu.collapse();
                sortType = 2;
                paginationValue = "0";
                contributorArrayList.clear();
                parentingBlogAdapter.notifyDataSetChanged();
                hitBloggerAPIrequest(sortType, type);
                break;
            case R.id.nameSortFAB:
                Utils.pushSortListingEvent(ContributorListActivity.this, GTMEventType.SORT_LISTING_EVENT, SharedPrefUtils.getUserDetailModel(ContributorListActivity.this).getDynamoId(), "Contributor List", "name");
                fab_menu.collapse();
                sortType = 1;
                paginationValue = "0";
                contributorArrayList.clear();
                parentingBlogAdapter.notifyDataSetChanged();
                hitBloggerAPIrequest(sortType, type);
                break;
        }
    }

    public void sortParentingBlogListing(String type) {
        paginationValue = "";
        isSortEnable = true;
        this.type = type;
        contributorArrayList.clear();
        parentingBlogAdapter.notifyDataSetChanged();
        sortType = 1;
        isReuqestRunning = true;
        if (!type.equals(AppConstants.USER_TYPE_BLOGGER)) {
            fab_menu.setVisibility(View.GONE);
        } else {
            fab_menu.setVisibility(View.VISIBLE);
        }
        hitBloggerAPIrequest(sortType, type);
    }
}
