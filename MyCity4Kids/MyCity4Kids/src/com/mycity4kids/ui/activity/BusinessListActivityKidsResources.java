package com.mycity4kids.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListPopupWindow;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.kelltontech.utils.ConnectivityUtils;
import com.kelltontech.utils.StringUtils;
import com.kelltontech.utils.ToastUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.controller.AutoSuggestController;
import com.mycity4kids.controller.BusinessListController;
import com.mycity4kids.dbtable.AdvancedSearchTable;
import com.mycity4kids.dbtable.LocalityTable;
import com.mycity4kids.dbtable.SortByTable;
import com.mycity4kids.enums.MapTypeFilter;
import com.mycity4kids.interfaces.IFilter;
import com.mycity4kids.interfaces.ISort;
import com.mycity4kids.models.autosuggest.AutoSuggestResponse;
import com.mycity4kids.models.businesslist.BusinessDataListing;
import com.mycity4kids.models.businesslist.BusinessListRequest;
import com.mycity4kids.models.businesslist.BusinessListResponse;
import com.mycity4kids.models.category.AdvancedSearch;
import com.mycity4kids.models.category.AgeGroup;
import com.mycity4kids.models.category.SortBy;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.ui.adapter.BusinessListingAdapter;
import com.mycity4kids.ui.adapter.SubLocalityAdapter;
import com.mycity4kids.ui.fragment.ActivitiesFragment;
import com.mycity4kids.ui.fragment.AgeGroupFragment;
import com.mycity4kids.ui.fragment.DateFragment;
import com.mycity4kids.ui.fragment.LocalitiesFragment;
import com.mycity4kids.ui.fragment.MoreFragment;
import com.mycity4kids.ui.fragment.SubCategoryFragment;
import com.mycity4kids.utils.tabwidget.MyTabFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.StringTokenizer;

/**
 * To be used as search result screen and category search to show tha business listing of this app
 */
public class BusinessListActivityKidsResources extends BaseActivity implements OnClickListener, OnTabChangeListener, OnPageChangeListener, IFilter, ISort {

    private ListView businessListView;
    private ListView eventListView;
    private BusinessListingAdapter businessAdapter;
    private int mBusinessListCount = 1;
    private int categoryId;
    private int businessOrEventType;
    private boolean sortisChecked = false;
    private ArrayList<BusinessDataListing> mBusinessDataListings;
    private int mPageCount = 1;
    private boolean mIsRequestRunning;
    private boolean mIsComingFromFilter;
    private View rltLoadingView;
    private String querySearch;
    private String localitySearch;
    TabHost tab_host;
    ViewPager pager_view;
    private int selected_index_filter = 0;
    private int selected_index_sort = 0;
    private GetFilterAdapter filterAdapter;
    PopupWindow pwindo;

    enum TabType {Filter, Sort}
    ;

    public enum FilterType {SubCategory, Locality, AgeGroup, More, Activities, DateValue}

    ;
    TabType chosen_tab;
    ArrayList<AdvancedSearch> advancedListFromDb;
    ArrayList<AdvancedSearch> advancedListFromSearch;
    private ArrayList<SortBy> mSortbyArrays;
    private BusinessListRequest businessListRequest;
    ArrayList<AgeGroup> mAgeGroupListFromSearch;
    public HashMap<MapTypeFilter, String> mFilterMap;
    private int mTotalPageCount = 0;
    Toolbar mToolbar;
    EditText mQuerySearchEtxt;
    ImageView searchBtn;
    RelativeLayout search_bar;
    ListView mSearchList;
    AutoSuggestController mAutoSuggestController;
    boolean isContainCommaQuery = false;
    private EditText mLocalitySearchEtxt;
    LinearLayout localityLayout;
    private float  density;
    private boolean isFromDeepLink;
    private String cityIdFrmDeepLink;
    private String deepLinkURL;
    private GoogleApiClient mClient;
    private String TAG;
    private String screenTitle = "Resources List";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TAG = BusinessListActivityKidsResources.this.getClass().getSimpleName();
        mClient = new GoogleApiClient.Builder(this).addApi(AppIndex.APP_INDEX_API).build();

        Constants.IS_RESET = true;
        try {
            setContentView(R.layout.business_list_activitykidsres);
            mLocalitySearchEtxt = (EditText) findViewById(R.id.locality_search);
            searchBtn = (ImageView) findViewById(R.id.search_btn);
            mQuerySearchEtxt = (EditText) findViewById(R.id.search);
            mToolbar = (Toolbar) findViewById(R.id.rltHeader);
            setSupportActionBar(mToolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            mSearchList = (ListView) findViewById(R.id.searchList);

            density = getResources().getDisplayMetrics().density;
            // getSupportActionBar().setTitle("Write A Review");
            // TextView _headerTxt = (TextView) findViewById(R.id.txvHeaderText);
            //  LinearLayout _RootLayout = (LinearLayout) findViewById(R.id.lnrRootFilterSort);
            mQuerySearchEtxt.addTextChangedListener(textWatcher);
            mLocalitySearchEtxt.addTextChangedListener(textWatcher);
            filterAdapter = new GetFilterAdapter(getSupportFragmentManager());
            mAutoSuggestController = new AutoSuggestController(BusinessListActivityKidsResources.this, this);
            mFilterMap = new HashMap<MapTypeFilter, String>();
            Constants.IS_PAGE_AVAILABLE = true;
            pager_view = (ViewPager) findViewById(R.id.viewpager);
            //	pager_view.setVisibility(View.GONE);
            pager_view.setOnPageChangeListener(this);
            pager_view.setAdapter(filterAdapter);
            pager_view.setOffscreenPageLimit(4);
            tab_host = (TabHost) findViewById(android.R.id.tabhost);
            tab_host.setup();
            tab_host.setOnTabChangedListener(this);
            tab_host.setVisibility(View.GONE);
            chosen_tab = TabType.Filter;
            businessOrEventType = getIntent().getIntExtra(Constants.PAGE_TYPE, 0);
            categoryId = getIntent().getIntExtra(Constants.EXTRA_CATEGORY_ID, 0);
            isFromDeepLink = getIntent().getBooleanExtra(Constants.IS_FROM_DEEPLINK, false);
            cityIdFrmDeepLink = getIntent().getStringExtra(Constants.CITY_ID_DEEPLINK);
            deepLinkURL = getIntent().getStringExtra(Constants.DEEPLINK_URL);
            //	mSortbyArrays = getSorts() ;
            manageView();
            mBusinessDataListings = new ArrayList<BusinessDataListing>();
            Constants.IS_SEARCH_LISTING = getIntent().getExtras().getBoolean("isSearchListing", false);
            if (Constants.IS_SEARCH_LISTING) {

                querySearch = getIntent().getExtras().getString("query");
                localitySearch = getIntent().getExtras().getString("locality");
                Log.d("check", "check querySearch " + querySearch);
                hitBusinessSearchListingApi(querySearch, localitySearch, mPageCount);
                if (!StringUtils.isNullOrEmpty(localitySearch) && !StringUtils.isNullOrEmpty(querySearch)) {
                    // _headerTxt.setText(querySearch + " in " + localitySearch);
                    getSupportActionBar().setTitle(querySearch + " in " + localitySearch);
                } else if (!StringUtils.isNullOrEmpty(localitySearch) && StringUtils.isNullOrEmpty(querySearch)) {
                    // _headerTxt.setText(localitySearch);
                    getSupportActionBar().setTitle(localitySearch);
                } else if (StringUtils.isNullOrEmpty(localitySearch) && !StringUtils.isNullOrEmpty(querySearch)) {
                    // _headerTxt.setText(querySearch);
                    getSupportActionBar().setTitle(querySearch);
                }


            } else {

                String categoryName = !StringUtils.isNullOrEmpty(getIntent().getStringExtra
                        (Constants.CATEGOTY_NAME))? getIntent().getStringExtra(Constants.CATEGOTY_NAME):"";
                //_headerTxt.setText(categoryName);
                getSupportActionBar().setTitle(categoryName+"");
                hitBusinessListingApi(categoryId, mPageCount);
                if (businessOrEventType == Constants.EVENT_PAGE_TYPE) {
                    tab_host.setVisibility(View.VISIBLE);
                    // _RootLayout.setVisibility(View.GONE);
                    manageTabForFilter();
/*
                    tab_host.getTabWidget().getChildAt(0).setOnTouchListener(new OnTouchListener() {

                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            if (event.getAction() == MotionEvent.ACTION_UP) {
                                ((TextView) findViewById(R.id.txt_no_data_business)).setVisibility(View.GONE);
                                if (tab_host.getCurrentTabTag().equals("Tab0")) {
                                    tab_host.getTabWidget().getChildAt(0).setSelected(true);
                                    if (pager_view.getVisibility() == View.GONE) {
                                        Animation bottomUp = AnimationUtils.loadAnimation(BusinessListActivityKidsResources.this, R.anim.bottom_down);
                                        pager_view.startAnimation(bottomUp);
                                        pager_view.setVisibility(View.VISIBLE);
                                    } else {
                                        Animation bottomUp = AnimationUtils.loadAnimation(BusinessListActivityKidsResources.this, R.anim.bottom_up);
                                        bottomUp.setAnimationListener(makeTopGone);
                                        pager_view.startAnimation(bottomUp);
                                        //	pager_view.setVisibility(View.INVISIBLE) ;
                                        //	pager_view.setVisibility(View.GONE) ;
                                    }

                                } else if (tab_host.getCurrentTabTag().equals("SortTab0")) {
                                    tab_host.getTabWidget().getChildAt(0).setSelected(true);
                                    if (mSortbyArrays != null && !mSortbyArrays.isEmpty()) {
                                        SortBy sortBy = mSortbyArrays.get(0);
                                    }
                                }

                            }
                            return false;
                        }
                    });
*/

                } else if (businessOrEventType == Constants.BUSINESS_PAGE_TYPE) {
                    mSortbyArrays = getSorts();
                    //_RootLayout.setVisibility(View.VISIBLE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        mSearchList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int pos, long id) {


                if (parent.getAdapter() instanceof ArrayAdapter<?>) {

                    /**
                     * its related to search query adapter:
                     */
                    String whichAdapterDataIsLoaded = (String) parent.getTag();
                    String listItem = (String) parent.getAdapter().getItem(pos).toString();

                    mQuerySearchEtxt.removeTextChangedListener(textWatcher);
                    if (!listItem.contains(",")) {
                        isContainCommaQuery = false;
                        mQuerySearchEtxt.setText(listItem);
                        mSearchList.setVisibility(View.GONE);
                        businessListView.setVisibility(View.VISIBLE);
                    } else {
                        isContainCommaQuery = true;
                        StringTokenizer tokens = new StringTokenizer(listItem, ",");
                        String first = tokens.nextToken();
                        String second = tokens.nextToken();

                        if (!StringUtils.isNullOrEmpty(first) && !StringUtils.isNullOrEmpty(second)) {
                            mQuerySearchEtxt.setText(first.trim());
                            mLocalitySearchEtxt.setText(second.trim());
                        }
                        mSearchList.setVisibility(View.GONE);
                        businessListView.setVisibility(View.VISIBLE);
                    }
                    mQuerySearchEtxt.addTextChangedListener(textWatcher);


                } else if (parent.getAdapter() instanceof SubLocalityAdapter) {
                    String listItem = (String)parent.getAdapter().getItem(pos).toString();
                    ///if(pos == 0 && ((SubLocalityAdapter)parent.getAdapter()).getSublocalities().get(0).equals("Current Location")) {
                    String queryData = mQuerySearchEtxt.getText().toString();
                    String locality = mLocalitySearchEtxt.getText().toString();

                    if (!queryData.equals("")) {
                        mLocalitySearchEtxt.setText(listItem);
                        Intent intent = new Intent(BusinessListActivityKidsResources.this, BusinessListActivityKidsResources.class);
                        intent.putExtra("query", queryData);
                        if (locality.equals("") || !isContainCommaQuery) {
                            String localityData = (String) parent.getAdapter().getItem(pos);
                            if (!StringUtils.isNullOrEmpty(localityData)) {

                                if (pos != 0) {
                                    intent.putExtra("locality", localityData);
                                }

                            }
                        } else {
                            intent.putExtra("locality", locality);
                        }

                        intent.putExtra("isSearchListing", true);
                        startActivity(intent);
                    } else {
                        showToast(getString(R.string.no_query));
                    }
                    ///	}else{
                        /*	mLocalitySearchEtxt.removeTextChangedListener(textWatcher);
							String listItem = ((SubLocalityAdapter)parent.getAdapter()).getSublocalities().get(pos);
							mLocalitySearchEtxt.setText(listItem);
							mLocalitySearchEtxt.addTextChangedListener(textWatcher);
						}*/
                }
            }
        });
        searchBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String queryData = mQuerySearchEtxt.getText().toString();
                String localityData = mLocalitySearchEtxt.getText().toString();
                if (StringUtils.isNullOrEmpty(queryData)) {
                    showToast(getString(R.string.no_query));
                    return;
                }
                if (!StringUtils.isNullOrEmpty(queryData) || !StringUtils.isNullOrEmpty(localityData)) {
                    //  if (!StringUtils.isNullOrEmpty(queryData)) {

                    //if(isContainCommaQuery || localityData.equals(""))
                    //	{
                    Intent intent = new Intent(BusinessListActivityKidsResources.this, BusinessListActivityKidsResources.class);
                    intent.putExtra("query", queryData);
                    intent.putExtra("locality", "");
                    intent.putExtra("isSearchListing", true);
                    startActivity(intent);
                    //	}else{
                    //		showToast("Please give correct data!");
                    //		return;
                    //	}
                    //	}
                }
                   /* Intent intent = new Intent(getActivity(), AutoSuggestTransparentDialogActivity.class);
                    startActivity(intent);*/
            }
        });
    }

    @Override
	protected void onStart() {
		super.onStart();
        if (!StringUtils.isNullOrEmpty(deepLinkURL) && AppConstants.BASE_URL.equalsIgnoreCase("http://webserve.mycity4kids.com/")) {
		// Connect client
		mClient.connect();
		final String TITLE = screenTitle;
		final Uri APP_URI = AppConstants.APP_BASE_URI.buildUpon().appendPath(deepLinkURL).build();
        final Uri WEB_URL = AppConstants.WEB_BASE_URL.buildUpon().appendPath(deepLinkURL).build();
		Action viewAction = Action.newAction(Action.TYPE_VIEW, TITLE, WEB_URL, APP_URI);
		PendingResult<Status> result = AppIndex.AppIndexApi.start(mClient, viewAction);

		result.setResultCallback(new ResultCallback<Status>() {
			@Override
			public void onResult(Status status) {
				if (status.isSuccess()) {
					Log.d(TAG, APP_URI.toString()+" App Indexing API: The screen view started " +
                            "successfully.");
				} else {
					Log.e(TAG, APP_URI.toString()+" App Indexing API: There was an error " +
                            "recording the screen ." + status.toString());
				}
			}
		});
        }
	}

    @Override
	protected void onStop() {
        if (!StringUtils.isNullOrEmpty(deepLinkURL) && AppConstants.BASE_URL.equalsIgnoreCase("http://webserve.mycity4kids.com/")) {
            final String TITLE = screenTitle;
            final Uri APP_URI = AppConstants.APP_BASE_URI.buildUpon().appendPath(deepLinkURL).build();
            final Uri WEB_URL = AppConstants.WEB_BASE_URL.buildUpon().appendPath(deepLinkURL).build();
            Action viewAction = Action.newAction(Action.TYPE_VIEW, TITLE, WEB_URL, APP_URI);
            PendingResult<Status> result = AppIndex.AppIndexApi.end(mClient, viewAction);

            result.setResultCallback(new ResultCallback<Status>() {
                @Override
                public void onResult(Status status) {
                    if (status.isSuccess()) {
                        Log.d(TAG, APP_URI.toString()+" App Indexing API:  The screen view end " +
                                "successfully.");
                    } else {
                        Log.e(TAG, APP_URI.toString()+" App Indexing API: There was an error " +
                                "recording the screen." + status.toString());
                    }
                }
            });
            // Disconnecting the client
            mClient.disconnect();
        }
        super.onStop();
	}


    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        //	Constants.BACK_PRESS_CONST = 0;
    }

    private void manageView() {
        //findViewById(R.id.imgBack).setOnClickListener(this);
        // findViewById(R.id.imgSearch).setOnClickListener(this);
        findViewById(R.id.txvFilter).setOnClickListener(this);
        findViewById(R.id.txvSortBy).setOnClickListener(this);
        // findViewById(R.id.imgSearch).setOnClickListener(this);
        localityLayout = (LinearLayout) findViewById(R.id.localityLayout);
        rltLoadingView = (RelativeLayout) findViewById(R.id.rltLoadingView);
        findViewById(R.id.imgLoader).startAnimation(AnimationUtils.loadAnimation(this, R.anim.rotate_indefinitely));
        eventListView = (ListView) findViewById(R.id.eventListView);
        businessListView = (ListView) findViewById(R.id.searchResultListView);
        search_bar = (RelativeLayout) findViewById(R.id.search_bar);
        businessAdapter = new BusinessListingAdapter(BusinessListActivityKidsResources.this);
        //	ImageView imgView=(ImageView)findViewById(R.id.filter_icon);
        LinearLayout filterIconLout = (LinearLayout) findViewById(R.id.filterLout);
        if (businessOrEventType == Constants.EVENT_PAGE_TYPE) {
            businessAdapter = new BusinessListingAdapter(BusinessListActivityKidsResources.this);
            businessListView.setVisibility(View.GONE);
            eventListView.setVisibility(View.VISIBLE);
            filterIconLout.setVisibility(View.VISIBLE);

            eventListView.setAdapter(businessAdapter);
        } else if (businessOrEventType == Constants.BUSINESS_PAGE_TYPE) {
            businessListView.setVisibility(View.VISIBLE);
            eventListView.setVisibility(View.GONE);
            filterIconLout.setVisibility(View.GONE);

            businessListView.setAdapter(businessAdapter);
        }


        businessListView.setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                String searchText = mQuerySearchEtxt.getText().toString().trim();
                if (searchText.equals("")) {
                    if (firstVisibleItem == 0) {
                        search_bar.setVisibility(View.GONE);
                        localityLayout.setVisibility(View.GONE);
                    } else {
                        search_bar.setVisibility(View.VISIBLE);
                        //localityLayout.setVisibility(View.VISIBLE);

                    }
                }

                if (Constants.IS_PAGE_AVAILABLE) {
                    if (!Constants.IS_SEARCH_LISTING) {
                        if (view.getCount() < mBusinessListCount) {

                            boolean loadMore = firstVisibleItem + visibleItemCount >= totalItemCount;
                            if (visibleItemCount != 0 && loadMore && !mIsRequestRunning && firstVisibleItem != 0) {
                                rltLoadingView.setVisibility(View.VISIBLE);
                                int currentPageCount = ++mPageCount;
                                if (mTotalPageCount >= currentPageCount) {
                                    hitBusinessListingApi(categoryId, currentPageCount);
                                } else {
                                    rltLoadingView.setVisibility(View.GONE);
                                }

                            }
                        }
                    } else {
                        if (view.getCount() <= mBusinessListCount) {

                            boolean loadMore = firstVisibleItem + visibleItemCount >= totalItemCount;
                            if (visibleItemCount != 0 && loadMore && !mIsRequestRunning && firstVisibleItem != 0) {
                                rltLoadingView.setVisibility(View.VISIBLE);
                                int currentPageCount = ++mPageCount;
                                if (mTotalPageCount >= currentPageCount) {
                                    hitBusinessSearchListingApi(querySearch, localitySearch, currentPageCount);
                                } else {
                                    rltLoadingView.setVisibility(View.GONE);
                                }

                            }
                        }
                    }
                }
            }
        });

        mQuerySearchEtxt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if(localityLayout.getVisibility() !=View.VISIBLE)
                    localityLayout.setVisibility(View.VISIBLE);
                return false;
            }
        });
        eventListView.setOnScrollListener(new OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                if (Constants.IS_PAGE_AVAILABLE) {
                    if (view.getCount() < mBusinessListCount) {

                        boolean loadMore = firstVisibleItem + visibleItemCount >= totalItemCount;
                        if (visibleItemCount != 0 && loadMore && !mIsRequestRunning && firstVisibleItem != 0) {
                            rltLoadingView.setVisibility(View.VISIBLE);

                            int currentPageCount = ++mPageCount;
                            if (mTotalPageCount >= currentPageCount) {
                                hitBusinessListingApi(categoryId, currentPageCount);
                            } else {
                                rltLoadingView.setVisibility(View.GONE);
                            }
                        }
                    }


                }

            }
        });
        businessListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                Intent intent = new Intent(BusinessListActivityKidsResources.this, BusinessDetailsActivity.class);
                String businessId = null;
                if (parent.getAdapter() instanceof BusinessListingAdapter) {
                    BusinessDataListing businessListData = (BusinessDataListing) ((BusinessListingAdapter) parent.getAdapter()).getItem(pos);
                    businessId = businessListData.getId();
                    intent.putExtra(Constants.CATEGORY_ID, categoryId);
                    intent.putExtra(Constants.BUSINESS_OR_EVENT_ID, businessId);
                    if (Constants.IS_SEARCH_LISTING) {
                        if (businessListData.getType().equalsIgnoreCase("event")) {
                            intent.putExtra(Constants.PAGE_TYPE, Constants.EVENT_PAGE_TYPE);
                        } else if (businessListData.getType().equalsIgnoreCase("business")) {
                            intent.putExtra(Constants.PAGE_TYPE, Constants.BUSINESS_PAGE_TYPE);
                        }
                    } else {
                        intent.putExtra(Constants.PAGE_TYPE, Constants.BUSINESS_PAGE_TYPE);
                    }
                    intent.putExtra("isbusiness", true);
                    intent.putExtra(Constants.DISTANCE, businessListData.getDistance());
                    startActivity(intent);
                }
            }
        });
        eventListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                Intent intent = new Intent(BusinessListActivityKidsResources.this, BusinessDetailsActivity.class);
                String businessId = null;
                if (parent.getAdapter() instanceof BusinessListingAdapter) {
                    BusinessDataListing businessListData = (BusinessDataListing) ((BusinessListingAdapter) parent.getAdapter()).getItem(pos);
                    businessId = businessListData.getId();
                    intent.putExtra(Constants.CATEGORY_ID, categoryId);
                    intent.putExtra(Constants.BUSINESS_OR_EVENT_ID, businessId);
                    intent.putExtra(Constants.PAGE_TYPE, Constants.EVENT_PAGE_TYPE);
                    intent.putExtra(Constants.DISTANCE, businessListData.getDistance());
                    startActivity(intent);
                }

            }
        });
    }


    @Override
    public void onBackPressed() {
        if (tab_host.getVisibility() == View.VISIBLE && businessOrEventType == Constants.BUSINESS_PAGE_TYPE) {
            Animation bottomUp = AnimationUtils.loadAnimation(this, R.anim.bottom_up);
            tab_host.startAnimation(bottomUp);
            tab_host.setVisibility(View.GONE);
            ((TextView) findViewById(R.id.txvFilter)).setBackgroundColor(getResources().getColor(R.color.tab_unchecked));


        } else if (pager_view.getVisibility() == View.VISIBLE && businessOrEventType == Constants.EVENT_PAGE_TYPE) {
            Animation bottomUp = AnimationUtils.loadAnimation(this, R.anim.bottom_up);
            bottomUp.setAnimationListener(makeTopGone);
            pager_view.startAnimation(bottomUp);
            ((TextView) findViewById(R.id.txvFilter)).setBackgroundColor(getResources().getColor(R.color.tab_unchecked));
        } else {
            finish();
        }
    }


    public void hitBusinessListingApi(int categoryId, int page) {
        if (!ConnectivityUtils.isNetworkEnabled(BusinessListActivityKidsResources.this)) {
            ToastUtils.showToast(BusinessListActivityKidsResources.this, getString(R.string.toast_network_error));
            return;
        }

        if (page == 1) {
            showProgressDialog(getString(R.string.fetching_data));
        }
        BusinessListController businessListController = new BusinessListController(this, this);
        if (mIsComingFromFilter) {
            businessListRequest.setPage(page + "");
            businessListController.getData(AppConstants.BUSINESS_LIST_REQUEST, businessListRequest);
        } else if (isFromDeepLink) {
            BusinessListRequest businessListRequest = new BusinessListRequest();
            businessListRequest.setCategory_id(categoryId + "");
            businessListRequest.setCity_id(!StringUtils.isNullOrEmpty(cityIdFrmDeepLink)?cityIdFrmDeepLink :/*(SharedPrefUtils.getCurrentCityModel(BusinessListActivityKidsResources.this)).getId() +*/ "");
            businessListRequest.setPage(page + "");
            businessListController.getData(AppConstants.BUSINESS_LIST_REQUEST, businessListRequest);
            isFromDeepLink = false;
        } else {
            BusinessListRequest businessListRequest = new BusinessListRequest();
            businessListRequest.setCategory_id(categoryId + "");
            businessListRequest.setCity_id((SharedPrefUtils.getCurrentCityModel(BusinessListActivityKidsResources.this)).getId() + "");
            businessListRequest.setPage(page + "");
            businessListController.getData(AppConstants.BUSINESS_LIST_REQUEST, businessListRequest);
        }
        mIsRequestRunning = true;
        //	mIsComingFromFilter=false;
    }

    private void hitBusinessSearchListingApi(String query, String locality, int page) {
        if (!ConnectivityUtils.isNetworkEnabled(BusinessListActivityKidsResources.this)) {
            ToastUtils.showToast(BusinessListActivityKidsResources.this, getString(R.string.toast_network_error));
            return;
        }

        if (page == 1) {
            showProgressDialog(getString(R.string.fetching_data));
        }
        BusinessListController businessListController = new BusinessListController(this, this);
        if (mIsComingFromFilter) {
            businessListRequest.setPage(page + "");
            businessListController.getData(AppConstants.BUSINESS_SEARCH_LISTING_REQUEST, businessListRequest);
        } else {
            BusinessListRequest businessListRequest = new BusinessListRequest();
            businessListRequest.setCity_id((SharedPrefUtils.getCurrentCityModel(BusinessListActivityKidsResources.this)).getId() + "");
            businessListRequest.setPage(page + "");
            businessListRequest.setQuerySearch(query);
            if(!StringUtils.isNullOrEmpty(locality))
            businessListRequest.setLocalitySearch(locality);

            businessListController.getData(AppConstants.BUSINESS_SEARCH_LISTING_REQUESTNEW, businessListRequest);
        }
        mIsRequestRunning = true;
    }

    @Override
    protected void updateUi(Response response) {
        mIsRequestRunning = false;
        Log.d("check", "checking in updateUi after sort ");
        removeProgressDialog();
        if (response.getResponseObject() instanceof AutoSuggestResponse) {
            AutoSuggestResponse responseData1 = (AutoSuggestResponse) response.getResponseObject();
            Log.d("check", "onTextChanged updateUi " + responseData1);
            String message1 = responseData1.getResult().getMessage();
            if (responseData1.getResponseCode() == 200) {
                ArrayList<String> queryList = responseData1.getResult().getData().getSuggest();
                Log.d("check", "onTextChanged updateUi queryList " + queryList.size());
                if (!queryList.isEmpty()) {
                    businessListView.setVisibility(View.GONE);
                    mSearchList.setVisibility(View.VISIBLE);
                    ((TextView)findViewById(R.id.txt_no_data_business)).setVisibility(View.GONE);
                    //mLoutProgress.setVisibility(View.GONE);
                    ArrayAdapter<String> mQueryAdapter = new ArrayAdapter<String>(BusinessListActivityKidsResources.this, R.layout.text_for_locality, queryList);
                    mSearchList.setAdapter(mQueryAdapter);
                    mQueryAdapter.notifyDataSetChanged();
                } else {
                    //  mLoutProgress.setVisibility(View.GONE);
                    businessListView.setVisibility(View.VISIBLE);
                    mSearchList.setVisibility(View.GONE);
                }

            } else if (responseData1.getResponseCode() == 400) {

                mSearchList.setVisibility(View.GONE);
                businessListView.setVisibility(View.VISIBLE);
            }
            if (response == null) {
                ToastUtils.showToast(BusinessListActivityKidsResources.this, getString(R.string.toast_server_error));
                return;
            }
        }


        if (rltLoadingView.getVisibility() == View.VISIBLE) {
            rltLoadingView.setVisibility(View.GONE);
        }
        Log.d("check", "response.getDataType() " + response.getDataType());
        switch (response.getDataType()) {

            case AppConstants.BUSINESS_SEARCH_LISTING_REQUESTNEW:
                BusinessListResponse responseData = (BusinessListResponse) response.getResponseObject();
                mSearchList.setVisibility(View.GONE);
                Log.d("check", "in AppConstants.BUSINESS_SEARCH_LISTING_REQUEST " + AppConstants.BUSINESS_SEARCH_LISTING_REQUEST);
                if (responseData.getResponseCode() == Constants.HTTP_RESPONSE_SUCCESS) {
                    mSortbyArrays = responseData.getResult().getData().getSortBy();
                    advancedListFromSearch = responseData.getResult().getData().getAdvancedSearch();
                    mAgeGroupListFromSearch = responseData.getResult().getData().getAgeGroup();
                    mBusinessListCount = responseData.getResult().getData().getTotal();
                    mTotalPageCount = responseData.getResult().getData().getPage_count();
                    //to add in already created list
                    // we neew to clear this list in case of sort by and filter
                    mBusinessDataListings.addAll(responseData.getResult().getData().getData());
                    businessAdapter.setListData(mBusinessDataListings, businessOrEventType);
                    businessAdapter.notifyDataSetChanged();
                    if (!mBusinessDataListings.isEmpty()) {
                        businessListView.setVisibility(View.VISIBLE);
                        ((TextView) findViewById(R.id.txt_no_data_business)).setVisibility(View.GONE);
                    } else {
                        ((TextView) findViewById(R.id.txt_no_data_business)).setVisibility(View.VISIBLE);
                        businessListView.setVisibility(View.GONE);
                    }
                } else if (responseData.getResponseCode() == 400) {
                    Constants.IS_PAGE_AVAILABLE = false;
                    if (mBusinessDataListings.isEmpty()) {
                        ((TextView) findViewById(R.id.txt_no_data_business)).setVisibility(View.VISIBLE);
                        businessListView.setVisibility(View.GONE);
                        eventListView.setVisibility(View.GONE);
                    } else {
                        ((TextView) findViewById(R.id.txt_no_data_business)).setVisibility(View.GONE);
                        if (businessOrEventType == Constants.EVENT_PAGE_TYPE) {
                            eventListView.setVisibility(View.VISIBLE);
                        } else {
                            businessListView.setVisibility(View.VISIBLE);
                        }


                    }
                    break;


                } else {

                    ToastUtils.showToast(BusinessListActivityKidsResources.this, getString(R.string.toast_response_error));
                    finish();
                }
                break;

            case AppConstants.BUSINESS_LIST_REQUEST:
                responseData = (BusinessListResponse) response.getResponseObject();
                if (responseData.getResponseCode() == Constants.HTTP_RESPONSE_SUCCESS) {

                    mBusinessListCount = responseData.getResult().getData().getTotal();
                    mTotalPageCount = responseData.getResult().getData().getPage_count();
                    //to add in already created list
                    // we neew to clear this list in case of sort by and filter
                    mBusinessDataListings.addAll(responseData.getResult().getData().getData());

                    businessAdapter.setListData(mBusinessDataListings, businessOrEventType);

                    businessAdapter.notifyDataSetChanged();
                    if (mBusinessDataListings.isEmpty()) {
                        ((TextView) findViewById(R.id.txt_no_data_business)).setVisibility(View.VISIBLE);
                        businessListView.setVisibility(View.GONE);
                    } else {
                        ((TextView) findViewById(R.id.txt_no_data_business)).setVisibility(View.GONE);
                        if (businessOrEventType == Constants.EVENT_PAGE_TYPE) {
                            eventListView.setVisibility(View.VISIBLE);
                        } else {
                            businessListView.setVisibility(View.VISIBLE);
                        }

                    }

                    //				ToastUtils.showToast(BusinessListActivity.this, "Success");
                } else if (responseData.getResponseCode() == 400) {
                    Constants.IS_PAGE_AVAILABLE = false;
                    if (mBusinessDataListings == null || mBusinessDataListings.isEmpty()) {
                        businessListView.setVisibility(View.GONE);
                        eventListView.setVisibility(View.GONE);
                        if (businessOrEventType == Constants.EVENT_PAGE_TYPE) {
                            ((TextView) findViewById(R.id.txt_no_data_business)).setVisibility(View.VISIBLE);
                            ((TextView) findViewById(R.id.txt_no_data_business)).setText(getString(R.string.event_list));
                        } else {
                            ((TextView) findViewById(R.id.txt_no_data_business)).setVisibility(View.VISIBLE);
                        }

                        //	businessListView.setVisibility(View.GONE);
                    } else {
                        ((TextView) findViewById(R.id.txt_no_data_business)).setVisibility(View.GONE);
                        if (businessOrEventType == Constants.EVENT_PAGE_TYPE) {
                            eventListView.setVisibility(View.VISIBLE);
                        } else {
                            businessListView.setVisibility(View.VISIBLE);
                        }

                    }
                    break;
                } else {
                    ToastUtils.showToast(BusinessListActivityKidsResources.this, getString(R.string.toast_response_error));
                    finish();
                }
                break;
            case AppConstants.BUSINESS_SEARCH_LISTING_REQUEST:
                responseData = (BusinessListResponse) response.getResponseObject();
                mSearchList.setVisibility(View.GONE);
                if (responseData.getResponseCode() == Constants.HTTP_RESPONSE_SUCCESS) {
                    mSortbyArrays = responseData.getResult().getData().getSortBy();
                    advancedListFromSearch = responseData.getResult().getData().getAdvancedSearch();
                    mAgeGroupListFromSearch = responseData.getResult().getData().getAgeGroup();
                    mBusinessListCount = responseData.getResult().getData().getTotal();
                    mTotalPageCount = responseData.getResult().getData().getPage_count();
                    //to add in already created list
                    // we neew to clear this list in case of sort by and filter
                    mBusinessDataListings.addAll(responseData.getResult().getData().getData());
                    businessAdapter.setListData(mBusinessDataListings, businessOrEventType);
                    businessAdapter.notifyDataSetChanged();
                    if (!mBusinessDataListings.isEmpty()) {
                        businessListView.setVisibility(View.VISIBLE);
                        ((TextView) findViewById(R.id.txt_no_data_business)).setVisibility(View.GONE);
                    } else {
                        ((TextView) findViewById(R.id.txt_no_data_business)).setVisibility(View.VISIBLE);
                        businessListView.setVisibility(View.GONE);
                    }
                } else if (responseData.getResponseCode() == 400) {
                    Constants.IS_PAGE_AVAILABLE = false;
                    if (mBusinessDataListings.isEmpty()) {
                        ((TextView) findViewById(R.id.txt_no_data_business)).setVisibility(View.VISIBLE);
                        businessListView.setVisibility(View.GONE);
                        eventListView.setVisibility(View.GONE);
                    } else {
                        ((TextView) findViewById(R.id.txt_no_data_business)).setVisibility(View.GONE);
                        if (businessOrEventType == Constants.EVENT_PAGE_TYPE) {
                            eventListView.setVisibility(View.VISIBLE);
                        } else {
                            businessListView.setVisibility(View.VISIBLE);
                        }


                    }
                    break;


                } else {

                    ToastUtils.showToast(BusinessListActivityKidsResources.this, getString(R.string.toast_response_error));
                    finish();
                }
                break;


        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        /*    case R.id.txvFilter:


                //	if(!Constants.IS_SEARCH_LISTING){

			*//*if(businessOrEventType==Constants.BUSINESS_PAGE_TYPE)
            {*//*
                if (chosen_tab == TabType.Filter) {
                    ((TextView) findViewById(R.id.txt_no_data_business)).setVisibility(View.GONE);
                    //	tab_host.setVisibility(View.GONE) ;

                    if (tab_host.getVisibility() == View.GONE) {

                        manageTabForFilter();
                        if (advancedListFromDb == null || advancedListFromDb.isEmpty()) {
                            showToast("There is no filters available");
                            return;
                        }
                        pager_view.setVisibility(View.VISIBLE);
                        Animation bottomUp = AnimationUtils.loadAnimation(this, R.anim.bottom_down);
                        tab_host.startAnimation(bottomUp);
                        tab_host.setVisibility(View.VISIBLE);
                        ((TextView) findViewById(R.id.txvSortBy)).setBackgroundColor(getResources().getColor(R.color.tab_unchecked));
                        ((TextView) findViewById(R.id.txvFilter)).setBackgroundColor(getResources().getColor(R.color.tab_checked));
                    } else {
                        Animation bottomUp = AnimationUtils.loadAnimation(this, R.anim.bottom_up);
                        tab_host.startAnimation(bottomUp);
                        tab_host.setVisibility(View.GONE);
                        ///	((TextView)findViewById(R.id.txvSortBy)).setBackgroundColor(getResources().getColor(R.color.tab_unchecked));
                        ((TextView) findViewById(R.id.txvFilter)).setBackgroundColor(getResources().getColor(R.color.tab_unchecked));

                    }
                } else {
                    manageTabForFilter();
                    if (advancedListFromDb == null || advancedListFromDb.isEmpty()) {
                        showToast("There is no filters available");
                        return;
                    }
                    Animation bottomUp = AnimationUtils.loadAnimation(this, R.anim.bottom_down);
                    tab_host.startAnimation(bottomUp);
                    tab_host.setVisibility(View.VISIBLE);
                    pager_view.setVisibility(View.VISIBLE);
                    ((TextView) findViewById(R.id.txvSortBy)).setBackgroundColor(getResources().getColor(R.color.tab_unchecked));
                    ((TextView) findViewById(R.id.txvFilter)).setBackgroundColor(getResources().getColor(R.color.tab_checked));

                }


                //	}else if(businessOrEventType==Constants.EVENT_PAGE_TYPE){



			*//*if(chosen_tab == TabType.Filter ) {
					if( pager_view.getVisibility() == View.GONE ) {
						manageTabForFilter() ; 
						Animation bottomUp = AnimationUtils.loadAnimation(this, R.anim.bottom_down);
						pager_view.startAnimation(bottomUp);
						pager_view.setVisibility(View.VISIBLE) ;
						((TextView)findViewById(R.id.txvFilter)).setBackgroundColor(getResources().getColor(R.color.tab_checked));
					} else {
						Animation bottomUp = AnimationUtils.loadAnimation(this, R.anim.bottom_up);
						pager_view.startAnimation(bottomUp);
						//pager_view.setVisibility(View.INVISIBLE) ;
						pager_view.setVisibility(View.GONE) ;
						((TextView)findViewById(R.id.txvFilter)).setBackgroundColor(getResources().getColor(R.color.tab_unchecked));
					}
				} else {
					manageTabForFilter() ;
					Animation bottomUp = AnimationUtils.loadAnimation(this, R.anim.bottom_down);
					pager_view.startAnimation(bottomUp);
					pager_view.setVisibility(View.VISIBLE) ;
					((TextView)findViewById(R.id.txvFilter)).setBackgroundColor(getResources().getColor(R.color.tab_checked));



				}	*//*
                //	}
			*//*}else{
				showToast(getResources().getString(R.string.no_data));
			}*//*
                break;
            case R.id.imgBack:
			*//*if(Constants.IS_SEARCH_LISTING){
				startActivity(new Intent(this,HomeCategoryActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
			}else{*//*
                finish();
                //}
                break;
           *//* case R.id.imgSearch: {
                Intent intent = new Intent(BusinessListActivity.this, AutoSuggestTransparentDialogActivity.class);
                //	intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                //startActivityForResult(intent, 1);
                startActivity(intent);
                //finish();
            }
            break;*//*

            case R.id.txvSortBy:
                if (mSortbyArrays != null && mSortbyArrays.size() > 0) {
                    if (chosen_tab == TabType.Sort) {
                        ((TextView) findViewById(R.id.txt_no_data_business)).setVisibility(View.GONE);
                        if (tab_host.getVisibility() == View.GONE) {
                            manageTabForSort();
                            Animation bottomDown = AnimationUtils.loadAnimation(this, R.anim.bottom_down);
                            tab_host.startAnimation(bottomDown);
                            tab_host.setVisibility(View.VISIBLE);
                            pager_view.setVisibility(View.GONE);
                            ((TextView) findViewById(R.id.txvSortBy)).setBackgroundColor(getResources().getColor(R.color.tab_checked));
                            ((TextView) findViewById(R.id.txvFilter)).setBackgroundColor(getResources().getColor(R.color.tab_unchecked));
                        } else {
                            Animation bottomUp = AnimationUtils.loadAnimation(this, R.anim.bottom_up);
                            tab_host.startAnimation(bottomUp);
                            tab_host.setVisibility(View.GONE);

                            pager_view.setVisibility(View.GONE);

                            ((TextView) findViewById(R.id.txvSortBy)).setBackgroundColor(getResources().getColor(R.color.tab_unchecked));
                        }
                    } else {
                        manageTabForSort();
                        Animation bottomUp = AnimationUtils.loadAnimation(this, R.anim.bottom_down);
                        tab_host.startAnimation(bottomUp);
                        tab_host.setVisibility(View.VISIBLE);
                        pager_view.setVisibility(View.GONE);
                        ((TextView) findViewById(R.id.txvSortBy)).setBackgroundColor(getResources().getColor(R.color.tab_checked));
                        ((TextView) findViewById(R.id.txvFilter)).setBackgroundColor(getResources().getColor(R.color.tab_unchecked));
                    }
                }*/
            //   break;
            case android.R.id.tabhost:
                if (chosen_tab == TabType.Sort) {
                    LinearLayout layout = new LinearLayout(this);
                    layout.setLayoutParams(new LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                }
                break;
            default:
                break;
        }

    }

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        manageTabForFilter();
        manageTabForSort();
    }*/
    private class GetFilterAdapter extends FragmentStatePagerAdapter {
        public GetFilterAdapter(
                android.support.v4.app.FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public android.support.v4.app.Fragment getItem(int i) {
            Log.d("check", "checking getItem i " + i);
            Bundle args = new Bundle();
            if (!advancedListFromDb.isEmpty() && advancedListFromDb != null) {
                if (advancedListFromDb.get(i).getKey().equalsIgnoreCase("locality") && advancedListFromDb.get(i).getValue().equalsIgnoreCase("Locality")) {
                    LocalitiesFragment localitiesFragment = new LocalitiesFragment();
                    args.putInt(Constants.PAGE_TYPE, businessOrEventType);
                    args.putBoolean("flag", true);
                    localitiesFragment.setAction(BusinessListActivityKidsResources.this);
                    localitiesFragment.setArguments(args);
                    return localitiesFragment;
                } else if (advancedListFromDb.get(i).getKey().equalsIgnoreCase("age_group") || advancedListFromDb.get(i).getValue().equalsIgnoreCase("Age Group")) {
                    AgeGroupFragment ageGroupFragment = new AgeGroupFragment();
                    ageGroupFragment.setAction(BusinessListActivityKidsResources.this);
                    args.putBoolean("flag", true);
                    args.putInt(Constants.CATEGORY_KEY, categoryId);
                    args.putInt(Constants.PAGE_TYPE, businessOrEventType);
                    args.putBoolean("isComeFromSearch", Constants.IS_SEARCH_LISTING);
                    args.putParcelableArrayList("AgeGroupArray", mAgeGroupListFromSearch);
                    ageGroupFragment.setArguments(args);
                    return ageGroupFragment;
                } else if (advancedListFromDb.get(i).getKey().equalsIgnoreCase("sub_category") || advancedListFromDb.get(i).getValue().equalsIgnoreCase("Sub Category")) {
                    SubCategoryFragment categoryFragment = new SubCategoryFragment();
                    categoryFragment.setAction(BusinessListActivityKidsResources.this);
                    args.putInt(Constants.CATEGORY_KEY, categoryId);
                    args.putInt(Constants.PAGE_TYPE, businessOrEventType);
                    args.putBoolean("flag", true);
                    categoryFragment.setArguments(args);
                    return categoryFragment;
                } else if (advancedListFromDb.get(i).getKey().equalsIgnoreCase("more")) {
                    MoreFragment fragment = new MoreFragment();
                    fragment.setAction(BusinessListActivityKidsResources.this);
                    args.putBoolean("flag", true);
                    args.putInt(Constants.CATEGORY_KEY, categoryId);
                    args.putInt(Constants.PAGE_TYPE, businessOrEventType);
                    fragment.setArguments(args);
                    return fragment;
                } else if (advancedListFromDb.get(i).getKey().equalsIgnoreCase("activities")) {
                    ActivitiesFragment fragment = new ActivitiesFragment();
                    fragment.setAction(BusinessListActivityKidsResources.this);
                    args.putBoolean("flag", true);
                    args.putInt(Constants.CATEGORY_KEY, categoryId);
                    args.putInt(Constants.PAGE_TYPE, businessOrEventType);
                    fragment.setArguments(args);
                    return fragment;
                } else if (advancedListFromDb.get(i).getKey().equalsIgnoreCase("date")) {
                    DateFragment fragment = new DateFragment();
                    fragment.setAction(BusinessListActivityKidsResources.this);
                    args.putBoolean("flag", true);
                    args.putInt(Constants.CATEGORY_KEY, categoryId);
                    args.putInt(Constants.PAGE_TYPE, businessOrEventType);
                    fragment.setArguments(args);
                    return fragment;
                }
            }/*else{
				SortFragment sortFragment=new SortFragment();
				sortFragment.setAction(BusinessListActivity.this) ;
				args.putInt(Constants.CATEGORY_KEY,categoryId ) ; 
				args.putInt(Constants.PAGE_TYPE, businessOrEventType ) ;
				args.putParcelableArrayList("SortArray", mSortbyArrays);
				sortFragment.setArguments(args);
				return sortFragment;
			}*/
            return null;
        }

        @Override
        public int getCount() {
            return advancedListFromDb == null ? 0 : advancedListFromDb.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "OBJECT " + (position + 1);
        }
    }

    private void addTab(BusinessListActivityKidsResources myTabActivity, TabHost tabHost, TabHost.TabSpec tabSpec) {
        tabSpec.setContent(new MyTabFactory(myTabActivity));
        tabHost.addTab(tabSpec);
        filterAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        int pos = this.pager_view.getCurrentItem();
        this.tab_host.setCurrentTab(pos);
        View tabView = tab_host.getTabWidget().getChildAt(position);
        HorizontalScrollView _list = (HorizontalScrollView) findViewById(R.id.horizontalList);
        if (tabView != null) {
            final int width = _list.getWidth();
            final int scrollPos = tabView.getLeft() - (width - tabView.getWidth()) / 2;
            _list.scrollTo(scrollPos, 0);
        } else {
            _list.scrollBy(positionOffsetPixels, 0);
        }


    }

    @Override
    public void onPageSelected(int pos) {

    }

    @Override
    public void onTabChanged(String arg0) {

        ((TextView) findViewById(R.id.txt_no_data_business)).setVisibility(View.GONE);

        //	Toast.makeText(BusinessListActivity.this, "call "+arg0, Toast.LENGTH_SHORT).show();

        if (!(tab_host.getCurrentTabTag().equals("Tab0"))) {
            if (pager_view.getVisibility() == View.GONE) {
                Animation bottomUp = AnimationUtils.loadAnimation(BusinessListActivityKidsResources.this, R.anim.bottom_down);
                pager_view.startAnimation(bottomUp);
                pager_view.setVisibility(View.VISIBLE);
            }
        }/* else {
			Animation bottomUp = AnimationUtils.loadAnimation(BusinessListActivity.this, R.anim.bottom_up);
			pager_view.startAnimation(bottomUp);
			pager_view.setVisibility(View.INVISIBLE) ;
		}*/

        int pos = this.tab_host.getCurrentTab();
        this.pager_view.setCurrentItem(pos);
        if (chosen_tab == TabType.Filter)
            selected_index_filter = pos;
        else
            selected_index_sort = pos;

        manageIndicator(pos);

        View tabView = tab_host.getTabWidget().getChildAt(pos);
        HorizontalScrollView _list = (HorizontalScrollView) findViewById(R.id.horizontalList);
        if (tabView != null) {
            final int width = _list.getWidth();
            final int scrollPos = tabView.getLeft() - (width - tabView.getWidth()) / 2;
            _list.scrollTo(scrollPos, 0);
        } else {
            _list.scrollBy(pos, 0);
        }
    }

    private void manageIndicator(int pos) {
		/*for(int i = 0 ; i < this.tab_host.getTabWidget().getTabCount() ; i++ ) {
			((ImageView)((LinearLayout)tab_host.getTabWidget().getChildTabViewAt(i)).findViewById(R.id.tab_arrow_indicator)).setVisibility(View.GONE) ;
		}


		//	if(tab_host.getTabWidget().getTabCount() > 1 ) {
				//((ImageView)((LinearLayout)tab_host.getTabWidget().getChildTabViewAt(pos)).findViewById(R.id.tab_arrow_indicator)).setVisibility(View.VISIBLE) ;
		if(businessOrEventType==Constants.EVENT_PAGE_TYPE && (isFirstTime) ){
			isFirstTime=false;
		//	this.tab_host.getTabWidget().getChildAt(0).setSelected(false);
		//	this.tab_host.getTabWidget().getChildAt(0).setBackgroundResource(R.drawable.tab_selector);
		//	this.tab_host.getTabWidget().setCurrentTab(pos) ; 
		//	pager_view.setCurrentItem(pos) ;	

		}else{*/

        this.tab_host.getTabWidget().setCurrentTab(pos);
        pager_view.setCurrentItem(pos);
        //	}

        //	}

    }

//    private void manageTabForSort() {
//        //	((TextView)findViewById(R.id.txvSortBy)).setBackgroundColor(getResources().getColor(R.color.tab_unchecked));
//        //	((TextView)findViewById(R.id.txvFilter)).setBackgroundColor(getResources().getColor(R.color.tab_unchecked));
//        chosen_tab = TabType.Sort;
//        if (tab_host.getTabWidget() != null)
//            tab_host.getTabWidget().removeAllViews();
//        tab_host.setup();
//        pager_view.setVisibility(View.GONE);
//        // TODO Put here your Tabs
//        LayoutInflater inflater = getLayoutInflater();
//        if (mSortbyArrays != null) {
//            for (int i = 0; i < mSortbyArrays.size(); i++) {
//                SortBy by = mSortbyArrays.get(i);
//                Log.d("check", "checking mSortbyArrays.get(i) " + mSortbyArrays.get(i));
//                LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.tab_design, null);
//                ((TextView) layout.findViewById(R.id.tab_title)).setText(by.getValue().toUpperCase());
//                //	layout.setBackgroundResource(R.drawable.detail_tab_selector);
//
//                // this.tab_host.getTabWidget().setDividerDrawable(R.drawable.tab_border);
//                addTab(this, this.tab_host, this.tab_host.newTabSpec("SortTab" + i).setIndicator(layout));
//            }
//        }
//        tab_host.getTabWidget().getChildAt(0).setSelected(false);
//        manageIndicator(selected_index_sort);
//
//        for (int i = 0; i < tab_host.getTabWidget().getTabCount(); i++) {
//            final int j = i;
//            Log.d("check", "checking j " + j);
//			/*tab_host.getTabWidget().getChildAt(j).setOnTouchListener(new OnTouchListener() {
//
//				@Override
//				public boolean onTouch(View v, MotionEvent event) {
//					if(event.getAction()==MotionEvent.ACTION_UP){
//						SortBy sortBy=null;
//						int i=tab_host.getCurrentTab();
//						if(mSortbyArrays!=null && !mSortbyArrays.isEmpty()){
//						switch (i) {
//						case 0:
//								 sortBy = mSortbyArrays.get(j);
//
//							break;
//						case 1:
//							 sortBy = mSortbyArrays.get(j);
//							break;
//						case 2:
//							 sortBy = mSortbyArrays.get(j);
//							break;
//						case 3:
//							 sortBy = mSortbyArrays.get(j);
//							break;
//						case 4:
//							 sortBy = mSortbyArrays.get(j);
//							break;
//						default:
//							break;
//						}
//						}
//					}
//					return false;
//				}
//			});*/
////kjjk
//            final BusinessListController businessListController = new BusinessListController(this, this);
//            mPageCount = 1;
//            businessListRequest = new BusinessListRequest();
//            businessListRequest.setCategory_id(categoryId + "");
//            if (Constants.IS_SEARCH_LISTING) {
//                businessListRequest.setQuerySearch(querySearch);
//                businessListRequest.setLocalitySearch(localitySearch);
//            }
//            businessListRequest.setCity_id((SharedPrefUtils.getCurrentCityModel(BusinessListActivityKidsResources.this)).getId() + "");
//            businessListRequest.setPage(mPageCount + "");
//            mBusinessDataListings = new ArrayList<BusinessDataListing>();
//
//            tab_host.getTabWidget().getChildAt(j).setOnClickListener(new OnClickListener() {
//
//                @Override
//                public void onClick(View v) {
//                    showProgressDialog(getString(R.string.fetching_data));
//                    SortBy sortBy = mSortbyArrays.get(j);
//                    Log.d("check", "checking sortBy " + sortBy);
//                    rejectLocally(Constants.BUSINESS_PAGE_TYPE);
//                    businessListRequest.setSort_by(sortBy.getKey());
//
//                    HashMap<MapTypeFilter, String> filterValues = mFilterMap;
//                    String finalValuesForFilter = "";
//                    for (Entry<MapTypeFilter, String> entry : filterValues.entrySet()) {
//                        MapTypeFilter key = entry.getKey();
//                        String value = entry.getValue();
//                        finalValuesForFilter = finalValuesForFilter + value;
//
//                    }
//
//                    businessListRequest.setTotalFilterValues(finalValuesForFilter);
//
//                    if (Constants.IS_SEARCH_LISTING) {
//                        businessListController.getData(AppConstants.BUSINESS_SEARCH_LISTING_REQUEST, businessListRequest);
//                    } else {
//                        businessListController.getData(AppConstants.BUSINESS_LIST_REQUEST, businessListRequest);
//                    }
//
//                    mIsRequestRunning = true;
//                    mIsComingFromFilter = true;
//
//                }
//            });
//
//        }
//
//    }

    private void manageTabForFilter() {

        if (!Constants.IS_SEARCH_LISTING) {
            AdvancedSearchTable _table = new AdvancedSearchTable((BaseApplication) getApplicationContext());
            advancedListFromDb = _table.getAllAdvancedSearch(categoryId);
        } else if (Constants.IS_SEARCH_LISTING || categoryId == 0) {
            advancedListFromDb = new ArrayList<AdvancedSearch>();
            advancedListFromDb = advancedListFromSearch;
        }
		/*if( advancedListFromDb==null||advancedListFromDb.isEmpty()){
			showToast("There is no filters available");
			return;
		}*/

        chosen_tab = TabType.Filter;

        if (tab_host.getTabWidget() != null) {
            tab_host.getTabWidget().removeAllViews();
        }
		/*  if(tab_host.getTabWidget() != null )
		  {
			tab_host.getTabWidget().removeAllViews() ;
		  }*/
        // 	tab_host.setup();
        //	pager_view.setVisibility(View.VISIBLE);
        //	pager_view.setAdapter(new GetFilterAdapter(getSupportFragmentManager()));


        // TODO Put here your Tabs
        LayoutInflater inflater = getLayoutInflater();
        LinearLayout layout = null;
        if (advancedListFromDb != null) {
            for (int i = 0; i < advancedListFromDb.size(); i++) {

                layout = (LinearLayout) inflater.inflate(R.layout.tab_design, null);
                String tabTitle = advancedListFromDb.get(i).getValue().toUpperCase();

                ((TextView) layout.findViewById(R.id.tab_title)).setText(tabTitle);

                //	}

				/*	if(businessOrEventType==Constants.EVENT_PAGE_TYPE){
				layout.setBackgroundResource(R.drawable.tab_selector);
			}else if(businessOrEventType==Constants.BUSINESS_PAGE_TYPE){*/
                //layout.setBackgroundResource(R.drawable.detail_tab_selector);
                //	}
				/*	if(this.tab_host.getTabWidget().getTabCount()==1){
				layout.setBackgroundResource(R.color.tab_color);*/

				/*	}else{

			layout.setBackgroundResource(R.drawable.detail_tab_selector);
			}*/
                //	tab_host.getTabWidget().getChildAt(0).setLayoutParams(new LinearLayout.LayoutParams(50,40));

                //	layout.setBackgroundResource(R.drawable.detail_tab_selector);

                this.tab_host.getTabWidget().setDividerDrawable(R.drawable.tab_border);
                addTab(this, this.tab_host, this.tab_host.newTabSpec("Tab" + i).setIndicator(layout));
                //this.tab_host.setSelected(false);

            }
        }
        if (businessOrEventType == Constants.EVENT_PAGE_TYPE) {
            this.tab_host.getTabWidget().getChildAt(0).setSelected(false);
        }


        manageIndicator(selected_index_filter);
    }


    private ArrayList<SortBy> getSorts() {
        SortByTable table = new SortByTable((BaseApplication) getApplication());
        return table.getSortByOptionsFromCategoryId(categoryId);
    }

    @Override
    public void doSort(int id, String type, Object Content) {
        int size = this.tab_host.getTabWidget().getTabCount();
        //tab_host.setCurrentTab(0);
        for (int i = 0; i < size; i++) {
            this.tab_host.getTabWidget().getChildAt(i).setSelected(false);
        }

    }


    @Override
    public void doFilter(BusinessListActivity.FilterType type, Object Content, int businessOrEvent) {

    }

    /**
     * Change according to client requirement:
     */
    @Override
    public void doNewFilter(int businessOrEventType) {

        try {


            int size = this.tab_host.getTabWidget().getTabCount();
            //tab_host.setCurrentTab(0);
            for (int i = 0; i < size; i++) {
                this.tab_host.getTabWidget().getChildAt(i).setSelected(false);
            }

            mBusinessDataListings = new ArrayList<BusinessDataListing>();
            mPageCount = 1;
            businessListRequest = new BusinessListRequest();
            businessListRequest.setCategory_id(categoryId + "");
            if (Constants.IS_SEARCH_LISTING) {
                businessListRequest.setQuerySearch(querySearch);
                businessListRequest.setLocalitySearch(localitySearch);
            }

            businessListRequest.setCity_id((SharedPrefUtils.getCurrentCityModel(BusinessListActivityKidsResources.this)).getId() + "");
            businessListRequest.setPage(mPageCount + "");
            rejectLocally(businessOrEventType);
            if (ConnectivityUtils.isNetworkEnabled(this)) {
                showProgressDialog(getString(R.string.fetching_data));


                HashMap<MapTypeFilter, String> filterValues = mFilterMap;
                String finalValuesForFilter = "";
                for (Entry<MapTypeFilter, String> entry : filterValues.entrySet()) {
                    MapTypeFilter key = entry.getKey();
                    String value = entry.getValue();
                    finalValuesForFilter = finalValuesForFilter + value;

                    //	System.out.println(key + " " + value);
                }

                //System.out.println(finalValuesForFilter);
                businessListRequest.setTotalFilterValues(finalValuesForFilter);

                // mFilterMap=new HashMap<MapTypeFilter, String>();


                BusinessListController businessListController = new BusinessListController(this, this);
                if (Constants.IS_SEARCH_LISTING) {
                    businessListController.getData(AppConstants.BUSINESS_SEARCH_LISTING_REQUEST, businessListRequest);
                } else {
                    businessListController.getData(AppConstants.BUSINESS_LIST_REQUEST, businessListRequest);
                }
                mIsRequestRunning = true;
                mIsComingFromFilter = true;
            } else {
                showToast(getString(R.string.netwrok_error));
            }

        } catch (Exception e) {
            Log.i("filters", e.getMessage());
        }
    }


    /**
     * previous work:
     */



    /*@Override
    public void doFilter(FilterType type, Object Content,int businessOrEventType) {
        int size=this.tab_host.getTabWidget().getTabCount();
        //tab_host.setCurrentTab(0);
        for(int i=0;i<size;i++)
        {
            this.tab_host.getTabWidget().getChildAt(i).setSelected(false);
        }

        mBusinessDataListings=new ArrayList<BusinessDataListing>();
        mPageCount=1;
        businessListRequest = new BusinessListRequest();
        businessListRequest.setCategory_id(categoryId + "");
        businessListRequest.setCity_id((SharedPrefUtils.getCurrentCityModel(BusinessListActivity.this)).getId() + "");
        businessListRequest.setPage(mPageCount + "");
        reject(businessOrEventType) ;
        if(ConnectivityUtils.isNetworkEnabled(this)) {
            showProgressDialog(getString(R.string.fetching_data)) ;


                 HashMap<String, String> filterValues=mFilterMap;
                 String finalValuesForFilter="";
                 for(Entry<String, String> entry : filterValues.entrySet()) {
                        String key = entry.getKey();
                        String value = entry.getValue();
                        finalValuesForFilter=finalValuesForFilter+value;

                        System.out.println(key + " " + value);
                    }

                   System.out.println(finalValuesForFilter);
                   businessListRequest.setTotalFilterValues(finalValuesForFilter);

                   mFilterMap=new HashMap<String, String>();

            if(type == FilterType.SubCategory ) {
                ArrayList<SubCategory> categories = (ArrayList<SubCategory>) Content ;
                if(categories != null ) {
                    String subCategory = "" ;
                    for(int i = 0 ; i < categories.size() ; i++) {
                        subCategory += categories.get(i).getId() + "," ;
                    }
                    businessListRequest.setSub_category_id(subCategory.substring(0 , subCategory.length() - 1 )) ;
                }
            } else if(type == FilterType.Locality ) {
                ArrayList<LocalityModel> localities = (ArrayList<LocalityModel>) Content ;
                if(localities != null ) {
                    String locality = "" ;
                    int zoneId=0;
                      zoneId=localities.get(0).getZoneId();
                    for(int i = 0 ; i < localities.size() ; i++) {

                        locality += localities.get(i).getLocalityId() + "," ;
                    }
                    businessListRequest.setZone_id(""+zoneId);
                //	businessListRequest.setZone_id(zoneId.substring(0 , zoneId.length() - 1 ));
                    businessListRequest.setLocality_id(locality.substring(0 , locality.length() - 1 )) ;
                }
            } else if(type == FilterType.AgeGroup ) {
                ArrayList<AgeGroup> ageGroups = (ArrayList<AgeGroup>) Content ;
                if(ageGroups != null ) {
                    String ageGr = "" ;
                    for(int i = 0 ; i < ageGroups.size() ; i++) {
                        ageGr += ageGroups.get(i).getKey() + "," ;
                    }
                    businessListRequest.setAge_group(ageGr.substring(0 , ageGr.length() - 1 )) ;
                }
            } else if(type == FilterType.More ) {
                ArrayList<Filters> moreFilers = (ArrayList<Filters>) Content ;
                if(moreFilers != null ) {
                    String moreFltr = "" ;
                    for(int i = 0 ; i < moreFilers.size() ; i++) {
                        moreFltr += moreFilers.get(i).getValue() + "," ;
                    }
                    businessListRequest.setMore(moreFltr.substring(0 , moreFltr.length() - 1 )) ;
                }
            }
            else if(type==FilterType.Activities){
                ArrayList<Activities> activitiesList = (ArrayList<Activities>) Content ;
                if(activitiesList != null ) {
                    String activities = "" ;
                    for(int i = 0 ; i < activitiesList.size() ; i++) {
                        activities += activitiesList.get(i).getId() + "," ;
                    }
                    businessListRequest.setActivities(activities.substring(0 , activities.length() - 1 )) ;
                }
            }
            else if(type==FilterType.DateValue){
                ArrayList<DateValue> dateList = null;
                String date=null;
                if(Content instanceof String){
                    date =(String)Content;
                }else if(Content instanceof ArrayList<?>)
                {
                    dateList = (ArrayList<DateValue>) Content ;
                }
                if(date!=null){
                    businessListRequest.setDate_by(date);
                }
                else if(dateList != null ) {
                    String dateValue = "" ;
                    for(int i = 0 ; i < dateList.size() ; i++) {
                        dateValue += dateList.get(i).getValue() + "," ;
                    }
                    businessListRequest.setDate_by(dateValue.substring(0 , dateValue.length() - 1 )) ;
                }
            }
            BusinessListController businessListController = new BusinessListController(this, this);
            businessListController.getData(AppConstants.BUSINESS_LIST_REQUEST, businessListRequest);
            mIsRequestRunning = true;
            mIsComingFromFilter=true;
        } else {
            showToast(getString(R.string.netwrok_error));
        }
    }*/
    final AnimationListener makeTopGone = new AnimationListener() {

        @Override
        public void onAnimationStart(Animation animation) {
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            try {
                Log.d("View Pager", "onAnimationEnd - makeTopGone");
                pager_view.setVisibility(View.GONE);
                pager_view.setAdapter(filterAdapter);
                if (tab_host != null && tab_host.getTabWidget() != null && tab_host.getTabWidget().getTabCount() > 0) {

                    tab_host.setCurrentTab(0);
                    tab_host.getTabWidget().getChildAt(0).setSelected(false);
                }
            } catch (Exception e) {
                e.printStackTrace();


            }
        }
    };

    final AnimationListener makeTopGoneLocally = new AnimationListener() {

        @Override
        public void onAnimationStart(Animation animation) {
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            try {
                pager_view.setVisibility(View.GONE);
                if (tab_host != null && tab_host.getTabWidget() != null && tab_host.getTabWidget().getTabCount() > 0) {

                    tab_host.setCurrentTab(0);
                    tab_host.getTabWidget().getChildAt(0).setSelected(false);
                }
            } catch (Exception e) {
                e.printStackTrace();


            }
        }
    };
    final AnimationListener makeTopGoneTabHost = new AnimationListener() {

        @Override
        public void onAnimationStart(Animation animation) {
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            Log.d("View Pager", "onAnimationEnd - makeTopGone");
            tab_host.setVisibility(View.GONE);
            pager_view.setAdapter(filterAdapter);
        }
    };


    public void rejectLocally(int type) {
        int size = this.tab_host.getTabWidget().getTabCount();

        for (int i = 0; i < size; i++) {
            this.tab_host.getTabWidget().getChildAt(i).setSelected(false);
        }

        if (type == Constants.BUSINESS_PAGE_TYPE) {
            Animation bottomUp = AnimationUtils.loadAnimation(this, R.anim.bottom_up);
            tab_host.startAnimation(bottomUp);
            tab_host.setVisibility(View.GONE);
        } else if (type == Constants.EVENT_PAGE_TYPE) {
            Animation bottomUp = AnimationUtils.loadAnimation(this, R.anim.bottom_up);
            bottomUp.setAnimationListener(makeTopGoneLocally);
            pager_view.startAnimation(bottomUp);
            //	pager_view.setVisibility(View.INVISIBLE) ;
            //	pager_view.setVisibility(View.GONE) ;
        }
    }
    @Override
    public void cancel(int type) {
        ((TextView) findViewById(R.id.txvSortBy)).setBackgroundColor(getResources().getColor(R.color.tab_unchecked));
        ((TextView) findViewById(R.id.txvFilter)).setBackgroundColor(getResources().getColor(R.color.tab_unchecked));

        Animation bottomUp = AnimationUtils.loadAnimation(this, R.anim.bottom_up);
        bottomUp.setAnimationListener(makeTopGoneTabHost);
        tab_host.startAnimation(bottomUp);

    }
    @Override
    public void reject(int type) {
        ((TextView) findViewById(R.id.txvSortBy)).setBackgroundColor(getResources().getColor(R.color.tab_unchecked));
        ((TextView) findViewById(R.id.txvFilter)).setBackgroundColor(getResources().getColor(R.color.tab_unchecked));
        mFilterMap = new HashMap<MapTypeFilter, String>();
        mBusinessDataListings = new ArrayList<BusinessDataListing>();
        mPageCount = 1;
        businessListRequest = new BusinessListRequest();
        businessListRequest.setCategory_id(categoryId + "");
        businessListRequest.setCity_id((SharedPrefUtils.getCurrentCityModel(BusinessListActivityKidsResources.this)).getId() + "");
        businessListRequest.setPage(mPageCount + "");

        Constants.IS_RESET = true;
        filterAdapter = new GetFilterAdapter(getSupportFragmentManager());
        pager_view.setAdapter(filterAdapter);

        if (ConnectivityUtils.isNetworkEnabled(this)) {
            showProgressDialog(getString(R.string.fetching_data));

			/*if(tab_host.getTabWidget()!=null){
			tab_host.getTabWidget().removeAllViews();
		}*/
            if (type == Constants.BUSINESS_PAGE_TYPE) {

                Animation bottomUp = AnimationUtils.loadAnimation(this, R.anim.bottom_up);
                bottomUp.setAnimationListener(makeTopGoneTabHost);
                //tab_host.setVisibility(View.GONE) ;
                tab_host.startAnimation(bottomUp);

            } else if (type == Constants.EVENT_PAGE_TYPE) {

                Animation bottomUp = AnimationUtils.loadAnimation(this, R.anim.bottom_up);
                bottomUp.setAnimationListener(makeTopGone);
                pager_view.startAnimation(bottomUp);
            }

            if (Constants.IS_SEARCH_LISTING) {
                mIsComingFromFilter = false;
                hitBusinessSearchListingApi(querySearch, localitySearch, mPageCount);
            } else {
                BusinessListController businessListController = new BusinessListController(this, this);
                businessListController.getData(AppConstants.BUSINESS_LIST_REQUEST, businessListRequest);
                mIsComingFromFilter = true;
            }
            mIsRequestRunning = true;

        } else {
            showToast(getString(R.string.netwrok_error));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // according to fragment change it
        getMenuInflater().inflate(R.menu.menu_res, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.filterres:
                if (chosen_tab == TabType.Filter) {
                    ((TextView) findViewById(R.id.txt_no_data_business)).setVisibility(View.GONE);
                    //	tab_host.setVisibility(View.GONE) ;

                    if (tab_host.getVisibility() == View.GONE) {

                        manageTabForFilter();
                        if (advancedListFromDb == null || advancedListFromDb.isEmpty()) {
                            showToast("There is no filters available");
                            // return;
                        }
                        pager_view.setVisibility(View.VISIBLE);
                        Animation bottomUp = AnimationUtils.loadAnimation(this, R.anim.bottom_down);
                        tab_host.startAnimation(bottomUp);
                        tab_host.setVisibility(View.VISIBLE);
                        ((TextView) findViewById(R.id.txvSortBy)).setBackgroundColor(getResources().getColor(R.color.tab_unchecked));
                        ((TextView) findViewById(R.id.txvFilter)).setBackgroundColor(getResources().getColor(R.color.tab_checked));
                    } else {
                        Animation bottomUp = AnimationUtils.loadAnimation(this, R.anim.bottom_up);
                        tab_host.startAnimation(bottomUp);
                        tab_host.setVisibility(View.GONE);
                        ///	((TextView)findViewById(R.id.txvSortBy)).setBackgroundColor(getResources().getColor(R.color.tab_unchecked));
                        ((TextView) findViewById(R.id.txvFilter)).setBackgroundColor(getResources().getColor(R.color.tab_unchecked));

                    }
                } else {
                    manageTabForFilter();
                    if (advancedListFromDb == null || advancedListFromDb.isEmpty()) {
                        showToast("There is no filters available");
                        //   return;
                    }
                    Animation bottomUp = AnimationUtils.loadAnimation(this, R.anim.bottom_down);
                    tab_host.startAnimation(bottomUp);
                    tab_host.setVisibility(View.VISIBLE);
                    pager_view.setVisibility(View.VISIBLE);
                    ((TextView) findViewById(R.id.txvSortBy)).setBackgroundColor(getResources().getColor(R.color.tab_unchecked));
                    ((TextView) findViewById(R.id.txvFilter)).setBackgroundColor(getResources().getColor(R.color.tab_checked));

                }
                break;

            case R.id.sortres:
                LayoutInflater inflater = (LayoutInflater) BusinessListActivityKidsResources.this
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View layout = inflater.inflate(R.layout.screen_popup, (ViewGroup) findViewById(R.id.popup_element));
                int popUpWidth = (int)(120 * density);
                pwindo = new PopupWindow(layout, popUpWidth, ViewGroup.LayoutParams.WRAP_CONTENT, true);
                pwindo.setOutsideTouchable(true);
                pwindo.setFocusable(true);
                pwindo.setBackgroundDrawable(new BitmapDrawable());
                pwindo.setFocusable(true);

                TextView tvdis = (TextView) layout.findViewById(R.id.distance);
                TextView tvrating = (TextView) layout.findViewById(R.id.rating);
                TextView tvrev = (TextView) layout.findViewById(R.id.reviews);
                TextView tvpayfees = (TextView) layout.findViewById(R.id.payfees);
                int toolbarheight = getActionBarHeight() + getStatusBarHeight();
                sortisChecked = item.isChecked();
                Display display = getWindowManager().getDefaultDisplay();
                int widthPixels = getResources().getDisplayMetrics().widthPixels;
                if (!sortisChecked) {
                    pwindo.showAtLocation(layout, Gravity.NO_GRAVITY, (widthPixels - popUpWidth - (int)(50 * density)), toolbarheight);
                } else {
                    pwindo.dismiss();
                }
                if (mSortbyArrays != null) {
                    for (int i = 0; i < mSortbyArrays.size(); i++) {
                        SortBy by = mSortbyArrays.get(i);
                        if (i == 0) {
                            tvdis.setText(by.getValue());
                        } else if (i == 1) {
                            tvrating.setText(by.getValue());
                        } else if (i == 2) {
                            tvrev.setText(by.getValue());
                        } else if (i == 3) {
                            tvpayfees.setText(by.getValue());
                        }
                        // addTab(this, this.tab_host, this.tab_host.newTabSpec("SortTab" + i).setIndicator(layout));
                    }
                    final BusinessListController businessListController = new BusinessListController(this, this);
                    mPageCount = 1;
                    businessListRequest = new BusinessListRequest();
                    businessListRequest.setCategory_id(categoryId + "");
                    if (Constants.IS_SEARCH_LISTING) {
                        businessListRequest.setQuerySearch(querySearch);
                        businessListRequest.setLocalitySearch(localitySearch);
                    }
                    businessListRequest.setCity_id((SharedPrefUtils.getCurrentCityModel(BusinessListActivityKidsResources.this)).getId() + "");
                    businessListRequest.setPage(mPageCount + "");
                    mBusinessDataListings = new ArrayList<BusinessDataListing>();
                    tvdis.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            pwindo.dismiss();
                            showProgressDialog(getString(R.string.fetching_data));
                            SortBy sortBy = mSortbyArrays.get(0);
                            rejectLocally(Constants.BUSINESS_PAGE_TYPE);
                            businessListRequest.setSort_by(sortBy.getKey());

                            HashMap<MapTypeFilter, String> filterValues = mFilterMap;
                            String finalValuesForFilter = "";
                            for (Entry<MapTypeFilter, String> entry : filterValues.entrySet()) {
                                MapTypeFilter key = entry.getKey();
                                String value = entry.getValue();
                                finalValuesForFilter = finalValuesForFilter + value;

                            }

                            businessListRequest.setTotalFilterValues(finalValuesForFilter);

                            if (Constants.IS_SEARCH_LISTING) {
                                businessListController.getData(AppConstants.BUSINESS_SEARCH_LISTING_REQUEST, businessListRequest);
                            } else {
                                businessListController.getData(AppConstants.BUSINESS_LIST_REQUEST, businessListRequest);
                            }

                            mIsRequestRunning = true;
                            mIsComingFromFilter = true;
                            filterAdapter.notifyDataSetChanged();
                        }
                    });
                    tvrating.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            pwindo.dismiss();
                            showProgressDialog(getString(R.string.fetching_data));
                            SortBy sortBy = mSortbyArrays.get(1);
                            rejectLocally(Constants.BUSINESS_PAGE_TYPE);
                            businessListRequest.setSort_by(sortBy.getKey());

                            HashMap<MapTypeFilter, String> filterValues = mFilterMap;
                            String finalValuesForFilter = "";
                            for (Entry<MapTypeFilter, String> entry : filterValues.entrySet()) {
                                MapTypeFilter key = entry.getKey();
                                String value = entry.getValue();
                                finalValuesForFilter = finalValuesForFilter + value;

                            }

                            businessListRequest.setTotalFilterValues(finalValuesForFilter);

                            if (Constants.IS_SEARCH_LISTING) {
                                businessListController.getData(AppConstants.BUSINESS_SEARCH_LISTING_REQUEST, businessListRequest);
                            } else {
                                businessListController.getData(AppConstants.BUSINESS_LIST_REQUEST, businessListRequest);
                            }

                            mIsRequestRunning = true;
                            mIsComingFromFilter = true;
                            filterAdapter.notifyDataSetChanged();
                        }
                    });
                    tvrev.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            pwindo.dismiss();
                            showProgressDialog(getString(R.string.fetching_data));
                            SortBy sortBy = mSortbyArrays.get(2);
                            rejectLocally(Constants.BUSINESS_PAGE_TYPE);
                            businessListRequest.setSort_by(sortBy.getKey());

                            HashMap<MapTypeFilter, String> filterValues = mFilterMap;
                            String finalValuesForFilter = "";
                            for (Entry<MapTypeFilter, String> entry : filterValues.entrySet()) {
                                MapTypeFilter key = entry.getKey();
                                String value = entry.getValue();
                                finalValuesForFilter = finalValuesForFilter + value;

                            }

                            businessListRequest.setTotalFilterValues(finalValuesForFilter);

                            if (Constants.IS_SEARCH_LISTING) {
                                businessListController.getData(AppConstants.BUSINESS_SEARCH_LISTING_REQUEST, businessListRequest);
                            } else {
                                businessListController.getData(AppConstants.BUSINESS_LIST_REQUEST, businessListRequest);
                            }

                            mIsRequestRunning = true;
                            mIsComingFromFilter = true;
                            filterAdapter.notifyDataSetChanged();
                        }
                    });
                    tvpayfees.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            pwindo.dismiss();
                            showProgressDialog(getString(R.string.fetching_data));
                            SortBy sortBy = mSortbyArrays.get(3);
                            rejectLocally(Constants.BUSINESS_PAGE_TYPE);
                            businessListRequest.setSort_by(sortBy.getKey());

                            HashMap<MapTypeFilter, String> filterValues = mFilterMap;
                            String finalValuesForFilter = "";
                            for (Entry<MapTypeFilter, String> entry : filterValues.entrySet()) {
                                MapTypeFilter key = entry.getKey();
                                String value = entry.getValue();
                                finalValuesForFilter = finalValuesForFilter + value;

                            }

                            businessListRequest.setTotalFilterValues(finalValuesForFilter);

                            if (Constants.IS_SEARCH_LISTING) {
                                businessListController.getData(AppConstants.BUSINESS_SEARCH_LISTING_REQUEST, businessListRequest);
                            } else {
                                businessListController.getData(AppConstants.BUSINESS_LIST_REQUEST, businessListRequest);
                            }

                            mIsRequestRunning = true;
                            mIsComingFromFilter = true;
                            filterAdapter.notifyDataSetChanged();
                        }
                    });
                }

                break;


            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private int getActionBarHeight() {
        int actionBarHeight = getSupportActionBar().getHeight();
        if (actionBarHeight != 0)
            return actionBarHeight;
        final TypedValue tv = new TypedValue();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
                actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
        }
        Log.d("check", "actionBarHeight before adding " + actionBarHeight);
        /*else if (getTheme().resolveAttribute(com.actionbarsherlock.R.attr.actionBarSize, tv, true))
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());*/
        return actionBarHeight;
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    private void showListMenu(View anchor) {
        ListPopupWindow popupWindow = new ListPopupWindow(this);

        ArrayList<HashMap<String, String>> mapList = new ArrayList<HashMap<String, String>>();
        String[] tiles = {"Distance", "Rating", "Reviews", "Pay Fess"};
        for (int i = 0; i < tiles.length; i++) {
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("sort", tiles[i]);
            mapList.add(map);
        }


        ListAdapter adapter = new SimpleAdapter(
                this,
                mapList,
                android.R.layout.activity_list_item, // You may want to use your own cool layout
                new String[]{"sort"}, // These are just the keys that the data uses
                new int[]{android.R.id.text1}); // The view ids to map the data to


//        popupWindow.setAnchorView(anchor);
        popupWindow.setAdapter(adapter);
        popupWindow.setWidth(400); // note: don't use pixels, use a dimen resource
        popupWindow.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        }); // the callback for when a list item is selected
        popupWindow.show();
    }

    TextWatcher textWatcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            Log.d("check", "onTextChanged");
            /**
             * this will call a query listing from api.
             */
            if (mQuerySearchEtxt.getText().hashCode() == s.hashCode()) {
                if (ConnectivityUtils.isNetworkEnabled(BusinessListActivityKidsResources.this)) {
                    if (!StringUtils.isNullOrEmpty(s.toString())) {
                        //	String queryData=s.toString();
						/*if(queryData.contains(",")){
							StringTokenizer tokens = new StringTokenizer(queryData, ",");
							String first = tokens.nextToken();
							String second = tokens.nextToken();
							if(!StringUtils.isNullOrEmpty(first) && !StringUtils.isNullOrEmpty(second)){
								mQuerySearchEtxt.setText(first);
								mLocalitySearchEtxt.setText(second);
							}

						}else{*/
                        // mLoutProgress.setVisibility(View.VISIBLE);
                        Log.d("check", "mAutoSuggestController " + mAutoSuggestController);
                        if (mAutoSuggestController != null)
                            mAutoSuggestController.setCanceled(true);
                        mAutoSuggestController.getData(AppConstants.BUSINESS_AUTO_SUGGEST_REQUEST, s.toString());
                        //	}

                    } else {
                        mSearchList.setVisibility(View.GONE);
                        businessListView.setVisibility(View.VISIBLE);
                    }
                } else {
                    //showToast(getString(R.string.error_network));
                    return;
                }

                /**
                 * it will call locality list from local db.
                 */
            } else if (mLocalitySearchEtxt.getText().hashCode() == s.hashCode()) {
                Log.d("check", "locality");
                SubLocalityAdapter adapter = null;
                LocalityTable _localitiesTable = new LocalityTable((BaseApplication) BusinessListActivityKidsResources.this.getApplicationContext());
                ArrayList<String> localitiesName = new ArrayList<String>();
                localitiesName.add("Near Me");
                ArrayList<String> localitiesNameDb = _localitiesTable.getLocalitiesName(s.toString().trim());
                if (localitiesNameDb != null && localitiesName.size() != 0) {
                    localitiesName.addAll(localitiesNameDb);
                }
                if (localitiesName.size() == 1) {
                    adapter = new SubLocalityAdapter(BusinessListActivityKidsResources.this, localitiesName);
                    mSearchList.setAdapter(adapter);
                }

                if (s != null && !(s.toString().equals(""))) {

                    if (!localitiesName.isEmpty()) {
                        if (adapter == null) {
                            adapter = new SubLocalityAdapter(BusinessListActivityKidsResources.this, localitiesName);
                        }
                        adapter.notifyDataSetChanged();
                        mSearchList.setVisibility(View.VISIBLE);
                        businessListView.setVisibility(View.GONE);
                        ((TextView)findViewById(R.id.txt_no_data_business)).setVisibility(View.GONE);
                        mSearchList.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                        mSearchList.setTag(Constants.LOCALITY_LIST_TAG);

                    } else {
                        mSearchList.setVisibility(View.GONE);
                        businessListView.setVisibility(View.VISIBLE);
                        // mLoutProgress.setVisibility(View.GONE);

                    }


                } else {
                    mSearchList.setVisibility(View.GONE);
                    businessListView.setVisibility(View.VISIBLE);
                }

            }

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
            // TODO Auto-generated method stub

        }

        @Override
        public void afterTextChanged(Editable s) {
            // TODO Auto-generated method stub

        }
    };
}
