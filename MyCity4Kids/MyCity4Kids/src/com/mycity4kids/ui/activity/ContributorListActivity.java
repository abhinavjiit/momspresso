package com.mycity4kids.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.crashlytics.android.Crashlytics;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
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
import com.mycity4kids.models.response.LanguageConfigModel;
import com.mycity4kids.newmodels.bloggermodel.BlogItemModel;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.profile.UserProfileActivity;
import com.mycity4kids.retrofitAPIsInterfaces.ContributorListAPI;
import com.mycity4kids.ui.adapter.ContributorListAdapter;
import com.mycity4kids.ui.adapter.CustomSpinnerAdapter;
import com.mycity4kids.utils.AppUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by anshul on 8/7/16.
 */
public class ContributorListActivity extends BaseActivity implements View.OnClickListener {
    ListView blogListing;
    ContributorListAdapter contributorListAdapter;
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
    private TextView contributorTitleTextView;
    private Spinner spinner_nav;
    ArrayList<String> list;
    ArrayList<LanguageConfigModel> languageConfigModelArrayList;
    private String langKey = "0";
    private String dynamoUserId;
    private RelativeLayout root;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parenting_blog_home);
        root = findViewById(R.id.root);
        ((BaseApplication) getApplication()).setView(root);
        ((BaseApplication) getApplication()).setActivity(this);

        Utils.pushOpenScreenEvent(ContributorListActivity.this, "Contributor List", SharedPrefUtils.getUserDetailModel(this).getDynamoId() + "");
        mToolBar = (Toolbar) findViewById(R.id.toolbar);
        mToolBar.setVisibility(View.VISIBLE);
        dynamoUserId = SharedPrefUtils.getUserDetailModel(this).getDynamoId();

        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
//        getSupportActionBar().setTitle("CONTRIBUTORS");
        spinner_nav = (Spinner) findViewById(R.id.spinner_nav);
        blogListing = (ListView) findViewById(R.id.blog_listing);
        mLodingView = (RelativeLayout) findViewById(R.id.relativeLoadingView);
        rankFab = (FloatingActionButton) findViewById(R.id.rankSortFAB);
        nameFab = (FloatingActionButton) findViewById(R.id.nameSortFAB);
        fab_menu = (FloatingActionsMenu) findViewById(R.id.fab_menu);
        frameLayout = (FrameLayout) findViewById(R.id.frame_layout);
        noBlogsTextView = (TextView) findViewById(R.id.noBlogsTextView);
        contributorTitleTextView = (TextView) findViewById(R.id.contributorTitleTextView);

        addItemsToSpinner();

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
                Intent profileIntent = new Intent(ContributorListActivity.this, UserProfileActivity.class);
                profileIntent.putExtra(Constants.USER_ID, itemSelected.getId());
                startActivity(profileIntent);
            }
        });

        contributorListAdapter = new ContributorListAdapter(ContributorListActivity.this, contributorArrayList);
        blogListing.setAdapter(contributorListAdapter);

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

    public void addItemsToSpinner() {

        list = new ArrayList<String>();
        languageConfigModelArrayList = new ArrayList<>();
        list.add("English");
        LanguageConfigModel languageConfigModel = new LanguageConfigModel();
        languageConfigModel.setName("English");
        languageConfigModel.setDisplay_name("English");
        languageConfigModel.setId(AppConstants.LANG_KEY_ENGLISH);
        languageConfigModelArrayList.add(languageConfigModel);
        try {
            FileInputStream fileInputStream = openFileInput(AppConstants.LANGUAGES_JSON_FILE);
            String fileContent = AppUtils.convertStreamToString(fileInputStream);
//            ConfigResult res = new Gson().fromJson(fileContent, ConfigResult.class);
            LinkedHashMap<String, LanguageConfigModel> retMap = new Gson().fromJson(
                    fileContent, new TypeToken<LinkedHashMap<String, LanguageConfigModel>>() {
                    }.getType()
            );
            Log.d("Map", "" + retMap.toString());
            for (final Map.Entry<String, LanguageConfigModel> entry : retMap.entrySet()) {
                list.add(entry.getValue().getDisplay_name());
                entry.getValue().setLangKey(entry.getKey());
                languageConfigModelArrayList.add(entry.getValue());
            }
        } catch (FileNotFoundException ffe) {
            Crashlytics.logException(ffe);
            Log.d("MC4kException", Log.getStackTraceString(ffe));
        }

        CustomSpinnerAdapter spinAdapter = new CustomSpinnerAdapter(
                getApplicationContext(), list);
        spinner_nav.setAdapter(spinAdapter);
        spinner_nav.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapter, View v,
                                       int position, long id) {
                if (position == 0) {
                    fab_menu.setVisibility(View.VISIBLE);
                } else {
                    fab_menu.setVisibility(View.GONE);
                }
                // On selecting a spinner item
                String item = adapter.getItemAtPosition(position).toString();
                // Showing selected spinner item
                contributorArrayList.clear();
                paginationValue = "";
                langKey = languageConfigModelArrayList.get(position).getLangKey();
                hitBloggerAPIrequest(2, AppConstants.USER_TYPE_BLOGGER);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (AppConstants.LOCALE_ENGLISH.equals(SharedPrefUtils.getAppLocale(BaseApplication.getAppContext()))) {
                    spinner_nav.setSelection(0);
                } else if (AppConstants.LOCALE_HINDI.equals(SharedPrefUtils.getAppLocale(BaseApplication.getAppContext()))) {
                    spinner_nav.setSelection(1);
                } else if (AppConstants.LOCALE_MARATHI.equals(SharedPrefUtils.getAppLocale(BaseApplication.getAppContext()))) {
                    spinner_nav.setSelection(2);
                } else if (AppConstants.LOCALE_BENGALI.equals(SharedPrefUtils.getAppLocale(BaseApplication.getAppContext()))) {
                    spinner_nav.setSelection(3);
                } else {
                    spinner_nav.setSelection(0);
                }
            }
        }, 1000);

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
//        Call<ContributorListResponse> call = contributorListAPI.getContributorList(AppConstants.LIVE_URL + "v1/users/?limit=" + limit + "&sortType=" + sortType + "&type=" + type + "&pagination=" + paginationValue);
        Call<ContributorListResponse> call = contributorListAPI.getContributorList(limit, sortType, type, langKey, paginationValue);
//asynchronous call
        call.enqueue(contributorListResponseCallback);

    }


    Callback<ContributorListResponse> contributorListResponseCallback = new Callback<ContributorListResponse>() {
        @Override
        public void onResponse(Call<ContributorListResponse> call, retrofit2.Response<ContributorListResponse> response) {
            try {
                removeProgressDialog();
                isReuqestRunning = false;
                mLodingView.setVisibility(View.GONE);
                ContributorListResponse responseModel = response.body();
                if (responseModel.getCode() == 200 && Constants.SUCCESS.equals(responseModel.getStatus())) {
                    processResponse(responseModel);
                } else {
                    showToast(getString(R.string.toast_response_error));
                    mLodingView.setVisibility(View.GONE);
                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<ContributorListResponse> call, Throwable t) {
            removeProgressDialog();
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

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
                contributorListAdapter.notifyDataSetChanged();
                noBlogsTextView.setVisibility(View.VISIBLE);
                noBlogsTextView.setText(getString(R.string.no_result_txt));
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
            contributorListAdapter.notifyDataSetChanged();

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
                contributorListAdapter.notifyDataSetChanged();
                hitBloggerAPIrequest(sortType, type);
                break;
            case R.id.nameSortFAB:
                Utils.pushSortListingEvent(ContributorListActivity.this, GTMEventType.SORT_LISTING_EVENT, SharedPrefUtils.getUserDetailModel(ContributorListActivity.this).getDynamoId(), "Contributor List", "name");
                fab_menu.collapse();
                sortType = 1;
                paginationValue = "0";
                contributorArrayList.clear();
                contributorListAdapter.notifyDataSetChanged();
                hitBloggerAPIrequest(sortType, type);
                break;
        }
    }

    public void sortParentingBlogListing(String type) {
        langKey = "0";
        paginationValue = "";
        isSortEnable = true;
        this.type = type;
        contributorArrayList.clear();
        contributorListAdapter.notifyDataSetChanged();
        sortType = 1;
        isReuqestRunning = true;
        if (!type.equals(AppConstants.USER_TYPE_BLOGGER)) {
            fab_menu.setVisibility(View.GONE);
            spinner_nav.setVisibility(View.GONE);
            contributorTitleTextView.setText(getString(R.string.contributors));
            hitBloggerAPIrequest(sortType, type);
        } else {
            fab_menu.setVisibility(View.VISIBLE);
            spinner_nav.setVisibility(View.VISIBLE);
            contributorTitleTextView.setText(getString(R.string.contributor_in));
            if (spinner_nav.getSelectedItemPosition() == 0) {
                sortType = 2;
                hitBloggerAPIrequest(sortType, type);
            } else {
                spinner_nav.setSelection(0);
            }

        }

    }
}
