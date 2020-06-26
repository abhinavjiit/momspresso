package com.mycity4kids.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.base.BaseActivity;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.gtmutils.GTMEventType;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.response.ContributorListResponse;
import com.mycity4kids.models.response.ContributorListResult;
import com.mycity4kids.models.response.LanguageConfigModel;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.profile.UserProfileActivity;
import com.mycity4kids.retrofitAPIsInterfaces.ContributorListAPI;
import com.mycity4kids.ui.adapter.ContributorListAdapter;
import com.mycity4kids.ui.adapter.CustomSpinnerAdapter;
import com.mycity4kids.utils.AppUtils;
import com.mycity4kids.utils.ConnectivityUtils;
import com.mycity4kids.utils.StringUtils;
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
    private RelativeLayout loadingView;
    private int totalPageCount = 2;
    private int nextPageNumber = 0;
    Boolean isSortEnable = false;
    Boolean isReuqestRunning = false;
    private Boolean isLastPageReached = false;
    private int limit = 10;
    private String paginationValue = "";
    ArrayList<ContributorListResult> contributorArrayList;
    Toolbar toolbar;
    FloatingActionButton rankFab;
    FloatingActionButton nameFab;
    int sortType = 2;
    String type = AppConstants.USER_TYPE_BLOGGER;
    FloatingActionsMenu floatingActionsMenu;
    FrameLayout frameLayout;
    private TextView noBlogsTextView;
    private TextView contributorTitleTextView;
    private Spinner spinnerNav;
    ArrayList<String> list;
    ArrayList<LanguageConfigModel> languageConfigModelArrayList;
    private String langKey = "0";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parenting_blog_home);
        RelativeLayout root = findViewById(R.id.root);
        ((BaseApplication) getApplication()).setView(root);
        ((BaseApplication) getApplication()).setActivity(this);

        Utils.pushOpenScreenEvent(ContributorListActivity.this, "Contributor List",
                SharedPrefUtils.getUserDetailModel(this).getDynamoId() + "");
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setVisibility(View.VISIBLE);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        spinnerNav = (Spinner) findViewById(R.id.spinner_nav);
        blogListing = (ListView) findViewById(R.id.blog_listing);
        loadingView = (RelativeLayout) findViewById(R.id.relativeLoadingView);
        rankFab = (FloatingActionButton) findViewById(R.id.rankSortFAB);
        nameFab = (FloatingActionButton) findViewById(R.id.nameSortFAB);
        floatingActionsMenu = (FloatingActionsMenu) findViewById(R.id.fab_menu);
        frameLayout = (FrameLayout) findViewById(R.id.frame_layout);
        noBlogsTextView = (TextView) findViewById(R.id.noBlogsTextView);
        contributorTitleTextView = (TextView) findViewById(R.id.contributorTitleTextView);

        frameLayout.getBackground().setAlpha(0);
        findViewById(R.id.imgLoader).startAnimation(AnimationUtils.loadAnimation(this, R.anim.rotate_indefinitely));
        contributorArrayList = new ArrayList<>();
        rankFab.setOnClickListener(ContributorListActivity.this);
        nameFab.setOnClickListener(this);

        contributorListAdapter = new ContributorListAdapter(this, contributorArrayList);
        blogListing.setAdapter(contributorListAdapter);

        blogListing.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                boolean loadMore = firstVisibleItem + visibleItemCount >= totalItemCount;
                if (visibleItemCount != 0 && loadMore && firstVisibleItem != 0 && !isReuqestRunning
                        && nextPageNumber < totalPageCount && !isLastPageReached) {
                    isReuqestRunning = true;
                    loadingView.setVisibility(View.VISIBLE);
                    hitBloggerApiRequest(sortType, type);
                }
            }
        });

        blogListing.setOnItemClickListener((adapterView, view, position, l) -> {
            ContributorListResult itemSelected = (ContributorListResult) adapterView.getItemAtPosition(position);
            Intent profileIntent = new Intent(ContributorListActivity.this, UserProfileActivity.class);
            profileIntent.putExtra(Constants.USER_ID, itemSelected.getId());
            startActivity(profileIntent);
        });

        addItemsToSpinner();

        floatingActionsMenu
                .setOnFloatingActionsMenuUpdateListener(new FloatingActionsMenu.OnFloatingActionsMenuUpdateListener() {
                    @Override
                    public void onMenuExpanded() {
                        frameLayout.getBackground().setAlpha(240);
                        frameLayout.setOnTouchListener((v, event) -> {
                            floatingActionsMenu.collapse();
                            return true;
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
        list = new ArrayList<>();
        languageConfigModelArrayList = new ArrayList<>();
        list.add("English");
        LanguageConfigModel languageConfigModel = new LanguageConfigModel();
        languageConfigModel.setName("English");
        languageConfigModel.setLangKey("0");
        languageConfigModel.setDisplay_name("English");
        languageConfigModel.setId(AppConstants.LANG_KEY_ENGLISH);
        languageConfigModelArrayList.add(languageConfigModel);
        try {
            FileInputStream fileInputStream = openFileInput(AppConstants.LANGUAGES_JSON_FILE);
            String fileContent = AppUtils.convertStreamToString(fileInputStream);
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
            FirebaseCrashlytics.getInstance().recordException(ffe);
            Log.d("MC4kException", Log.getStackTraceString(ffe));
        }

        CustomSpinnerAdapter spinAdapter = new CustomSpinnerAdapter(
                getApplicationContext(), list);
        spinnerNav.setAdapter(spinAdapter);
        spinnerNav.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapter, View v,
                    int position, long id) {
                if (position == 0) {
                    floatingActionsMenu.setVisibility(View.VISIBLE);
                } else {
                    floatingActionsMenu.setVisibility(View.GONE);
                }
                contributorArrayList.clear();
                paginationValue = "";
                sortType = 2;
                langKey = languageConfigModelArrayList.get(position).getLangKey();
                contributorListAdapter.setLangKey(langKey);
                hitBloggerApiRequest(sortType, AppConstants.USER_TYPE_BLOGGER);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        new Handler().postDelayed(() -> {
            if (AppConstants.LOCALE_ENGLISH.equals(SharedPrefUtils.getAppLocale(BaseApplication.getAppContext()))) {
                spinnerNav.setSelection(0);
            } else if (AppConstants.LOCALE_HINDI
                    .equals(SharedPrefUtils.getAppLocale(BaseApplication.getAppContext()))) {
                spinnerNav.setSelection(1);
            } else if (AppConstants.LOCALE_MARATHI
                    .equals(SharedPrefUtils.getAppLocale(BaseApplication.getAppContext()))) {
                spinnerNav.setSelection(2);
            } else if (AppConstants.LOCALE_BENGALI
                    .equals(SharedPrefUtils.getAppLocale(BaseApplication.getAppContext()))) {
                spinnerNav.setSelection(3);
            } else {
                spinnerNav.setSelection(0);
            }
        }, 1000);
    }

    public void hitBloggerApiRequest(int sortType, String type) {
        showProgressDialog(getResources().getString(R.string.please_wait));
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        ContributorListAPI contributorListApi = retrofit.create(ContributorListAPI.class);
        if (!ConnectivityUtils.isNetworkEnabled(this)) {
            removeProgressDialog();
            showToast(getString(R.string.error_network));
            return;
        }
        Call<ContributorListResponse> call = contributorListApi
                .getContributorList(limit, sortType, type, langKey, paginationValue);
        call.enqueue(contributorListResponseCallback);
    }

    Callback<ContributorListResponse> contributorListResponseCallback = new Callback<ContributorListResponse>() {
        @Override
        public void onResponse(Call<ContributorListResponse> call,
                retrofit2.Response<ContributorListResponse> response) {
            try {
                removeProgressDialog();
                isReuqestRunning = false;
                loadingView.setVisibility(View.GONE);
                ContributorListResponse responseModel = response.body();
                if (responseModel != null) {
                    if (responseModel.getCode() == 200 && Constants.SUCCESS.equals(responseModel.getStatus())) {
                        processResponse(responseModel);
                    } else {
                        showToast(getString(R.string.toast_response_error));
                        loadingView.setVisibility(View.GONE);
                    }
                }
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<ContributorListResponse> call, Throwable t) {
            removeProgressDialog();
            FirebaseCrashlytics.getInstance().recordException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    public void processResponse(ContributorListResponse responseModel) {
        ArrayList<ContributorListResult> dataList = responseModel.getData().getResult();
        if (dataList == null || dataList.size() == 0) {
            isLastPageReached = true;
            if (null == contributorArrayList || contributorArrayList.isEmpty()) {
                // No results for search
                contributorArrayList = dataList;
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
            default:
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
                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rankSortFAB:
                Utils.pushSortListingEvent(ContributorListActivity.this, GTMEventType.SORT_LISTING_EVENT,
                        SharedPrefUtils.getUserDetailModel(ContributorListActivity.this).getDynamoId(),
                        "Contributor List", "rank");
                floatingActionsMenu.collapse();
                sortType = 2;
                paginationValue = "0";
                contributorArrayList.clear();
                contributorListAdapter.notifyDataSetChanged();
                hitBloggerApiRequest(sortType, type);
                break;
            case R.id.nameSortFAB:
                Utils.pushSortListingEvent(ContributorListActivity.this, GTMEventType.SORT_LISTING_EVENT,
                        SharedPrefUtils.getUserDetailModel(ContributorListActivity.this).getDynamoId(),
                        "Contributor List", "name");
                floatingActionsMenu.collapse();
                sortType = 1;
                paginationValue = "0";
                contributorArrayList.clear();
                contributorListAdapter.notifyDataSetChanged();
                hitBloggerApiRequest(sortType, type);
                break;
            default:
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
            floatingActionsMenu.setVisibility(View.GONE);
            spinnerNav.setVisibility(View.GONE);
            contributorTitleTextView.setText(getString(R.string.contributors));
            hitBloggerApiRequest(sortType, type);
        } else {
            floatingActionsMenu.setVisibility(View.VISIBLE);
            spinnerNav.setVisibility(View.VISIBLE);
            contributorTitleTextView.setText(getString(R.string.contributor_in));
            if (spinnerNav.getSelectedItemPosition() == 0) {
                sortType = 2;
                hitBloggerApiRequest(sortType, type);
            } else {
                spinnerNav.setSelection(0);
            }

        }

    }
}
