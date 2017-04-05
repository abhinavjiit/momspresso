package com.mycity4kids.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseFragment;
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
import com.mycity4kids.dbtable.TableKids;
import com.mycity4kids.enums.MapTypeFilter;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.interfaces.IFilter;
import com.mycity4kids.interfaces.ISort;
import com.mycity4kids.models.autosuggest.AutoSuggestResponse;
import com.mycity4kids.models.businesslist.BusinessDataListing;
import com.mycity4kids.models.businesslist.BusinessListRequest;
import com.mycity4kids.models.businesslist.BusinessListResponse;
import com.mycity4kids.models.category.AdvancedSearch;
import com.mycity4kids.models.category.AgeGroup;
import com.mycity4kids.models.category.SortBy;
import com.mycity4kids.models.user.KidsInfo;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.ui.activity.AutoSuggestTransparentDialogActivity;
import com.mycity4kids.ui.activity.BusinessDetailsActivity;
import com.mycity4kids.ui.activity.BusinessListActivity;
import com.mycity4kids.ui.adapter.BusinessListingAdapter;
import com.mycity4kids.ui.adapter.BusinessListingAdapterevent;
import com.mycity4kids.ui.adapter.SubLocalityAdapter;
import com.mycity4kids.utils.PermissionUtil;
import com.mycity4kids.utils.tabwidget.MyTabFactory;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Created by khushboo.goyal on 09-06-2015.
 */
public class FragmentBusinesslistEvents extends BaseFragment implements View.OnClickListener, IFilter, TabHost.OnTabChangeListener, ViewPager.OnPageChangeListener, ISort {


    private ListView businessListView;
    private ListView eventListView;
    private BusinessListingAdapterevent businessAdapter;
    private int mBusinessListCount = 1;
    private int categoryId;
    private int businessOrEventType;
    EditText searchName;
    private ArrayList<BusinessDataListing> mBusinessDataListings;
    private ArrayList<BusinessDataListing> mBusinessDataListingschild;
    private int mPageCount = 1;
    private boolean mIsRequestRunning;
    private boolean mIsComingFromFilter;
    private View rltLoadingView;
    private String querySearch;
    private String localitySearch;
    TabHost tab_host;
    ViewPager pager_view;
    RelativeLayout search_bar;
    LinearLayout localityLayout;
    AutoSuggestController mAutoSuggestController;
    boolean isContainCommaQuery = false;
    private int selected_index_filter = 0;
    //    private int selected_index_sort = 0;
    private GetFilterAdapter getFilterAdapter;

    public enum FilterType {SubCategory, Locality, AgeGroup, More, Activities, DateValue}

    ;
    //    TabType chosen_tab;
    ArrayList<AdvancedSearch> advancedListFromDb;
    ArrayList<AdvancedSearch> advancedListFromSearch;
    private ArrayList<SortBy> mSortbyArrays;
    private BusinessListRequest businessListRequest;
    ArrayList<AgeGroup> mAgeGroupListFromSearch;
    public static HashMap<MapTypeFilter, String> mFilterMap = new HashMap<>();
    private int mTotalPageCount = 0;
    private View view;
    ImageView searchBtn;
    static boolean isagefilter = false;
    EditText mLocalitySearchEtxt;
    ListView mSearchList;
    String deepLinkURL;
    private GoogleApiClient mClient;
    private String TAG;
    private String screenTitle = "Events List";
    private View businessRoot;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Utils.pushOpenScreenEvent(getActivity(), "Upcoming Events", SharedPrefUtils.getUserDetailModel(getActivity()).getDynamoId() + "");

        view = inflater.inflate(R.layout.business_list_activity, null);

        TAG = getActivity().getClass().getSimpleName();
        mClient = new GoogleApiClient.Builder(getActivity()).addApi(AppIndex.APP_INDEX_API).build();
        deepLinkURL = getActivity().getIntent().getStringExtra(Constants.DEEPLINK_URL);

        Constants.IS_RESET = false;
        try {

            LinearLayout _RootLayout = (LinearLayout) view.findViewById(R.id.lnrRootFilterSort);
            mSearchList = (ListView) view.findViewById(R.id.searchList);
            mFilterMap = new HashMap<MapTypeFilter, String>();
            Constants.IS_PAGE_AVAILABLE = true;
            pager_view = (ViewPager) view.findViewById(R.id.viewpager);
            businessRoot = view.findViewById(R.id.businessRoot);
            //	pager_view.setVisibility(View.GONE);
            pager_view.setOnPageChangeListener(this);
            getFilterAdapter = new GetFilterAdapter(getActivity().getSupportFragmentManager());
            pager_view.setAdapter(getFilterAdapter);
            pager_view.setOffscreenPageLimit(4);

            tab_host = (TabHost) view.findViewById(android.R.id.tabhost);
            tab_host.setup();
            tab_host.setOnTabChangedListener(this);
            tab_host.setVisibility(View.GONE);
            // chosen_tab = TabType.Filter;
            businessAdapter = new BusinessListingAdapterevent(getActivity(), FragmentBusinesslistEvents.this);
            businessOrEventType = getActivity().getIntent().getIntExtra(Constants.PAGE_TYPE, 0);
            categoryId = getActivity().getIntent().getIntExtra(Constants.EXTRA_CATEGORY_ID, 0);
            mAutoSuggestController = new AutoSuggestController(getActivity(), this);
            // added by khushboo

            if (getArguments() != null) {
                businessOrEventType = getArguments().getInt(Constants.PAGE_TYPE, 0);
                categoryId = getArguments().getInt(Constants.EXTRA_CATEGORY_ID, 0);
            }


            //	mSortbyArrays = getSorts() ;
            manageView();
            mBusinessDataListings = new ArrayList<BusinessDataListing>();

            if (getActivity().getIntent().getExtras() != null) {
                // added by khushboo
            }

            String categoryName = getActivity().getIntent().getStringExtra(Constants.CATEGOTY_NAME);

            if (getArguments() != null) {
                categoryName = getArguments().getString(Constants.CATEGOTY_NAME);
            }

            TableKids tableKids = new TableKids(BaseApplication.getInstance());
            ArrayList<KidsInfo> kidsInformations = new ArrayList<>();
            kidsInformations = tableKids.getAllKids();
            ArrayList<Integer> agelist = new ArrayList<>();
            HashSet<String> ageArryList = new HashSet<>();
            for (int i = 0; i < kidsInformations.size(); i++) {
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                Date startDate;
                try {
                    startDate = df.parse(kidsInformations.get(i).getDate_of_birth());
                    int age = getAge(startDate);
                    agelist.add(age);
                    ageArryList.add("" + age);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            HashSet<String> selectedageGroups = new HashSet<>();

            for (int i = 0; i < agelist.size(); i++) {
                if ((agelist.get(i) >= 0) && (agelist.get(i) < 2)) {

                    selectedageGroups.add("infants");
                } else if ((agelist.get(i) >= 2) && (agelist.get(i) < 4)) {

                    selectedageGroups.add("toddlers");
                } else if ((agelist.get(i) >= 4) && (agelist.get(i) < 6)) {

                    selectedageGroups.add("kindergarten");
                } else if ((agelist.get(i) >= 6) && (agelist.get(i) < 10)) {

                    selectedageGroups.add("junior_school");
                } else if ((agelist.get(i) >= 10) && (agelist.get(i) < 14)) {

                    selectedageGroups.add("middle_school");
                }
            }

            Object[] myArr = selectedageGroups.toArray();
            String finalString = "&age_group[]=";

            boolean flag = false;
            for (int i = 0; i < myArr.length; i++) {

                if (flag) {
                    finalString = finalString + "&age_group[]=";
                }

                finalString = finalString + myArr[i].toString();
                flag = true;

            }


            // now add age also
            Object[] ageObj = ageArryList.toArray();


            mFilterMap.put(MapTypeFilter.AgeGroup, finalString);
            //
            hitBusinessListingApiSorted(categoryId, mPageCount, mFilterMap);
            if (businessOrEventType == Constants.EVENT_PAGE_TYPE) {
                tab_host.setVisibility(View.VISIBLE);
                view.findViewById(R.id.linearLayout1).setVisibility(View.GONE);
                view.findViewById(R.id.tabline).setVisibility(View.GONE);
                _RootLayout.setVisibility(View.GONE);
                manageTabForFilter();

            } else if (businessOrEventType == Constants.BUSINESS_PAGE_TYPE) {
                mSortbyArrays = getSorts();
                _RootLayout.setVisibility(View.VISIBLE);
            }
            //  }
        } catch (Exception e) {
            e.printStackTrace();
        }


        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
        if (!StringUtils.isNullOrEmpty(deepLinkURL) && AppConstants.BASE_URL.equalsIgnoreCase("https://api.mycity4kids.com/")) {
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
                        Log.d(TAG, APP_URI.toString() + " App Indexing API: The screen view started" +
                                " successfully.");
                    } else {
                        Log.e(TAG, APP_URI.toString() + " App Indexing API: There was an error " +
                                "recording the screen ." + status.toString());
                    }
                }
            });
        }
    }

    @Override
    public void onStop() {
        if (!StringUtils.isNullOrEmpty(deepLinkURL) && AppConstants.BASE_URL.equalsIgnoreCase("https://api.mycity4kids.com/")) {
            final String TITLE = screenTitle;
            final Uri APP_URI = AppConstants.APP_BASE_URI.buildUpon().appendPath(deepLinkURL).build();
            final Uri WEB_URL = AppConstants.WEB_BASE_URL.buildUpon().appendPath(deepLinkURL).build();
            Action viewAction = Action.newAction(Action.TYPE_VIEW, TITLE, WEB_URL, APP_URI);
            PendingResult<Status> result = AppIndex.AppIndexApi.end(mClient, viewAction);

            result.setResultCallback(new ResultCallback<Status>() {
                @Override
                public void onResult(Status status) {
                    if (status.isSuccess()) {
                        Log.d(TAG, APP_URI.toString() + " App Indexing API:  The screen view end " +
                                "successfully.");
                    } else {
                        Log.e(TAG, APP_URI.toString() + " App Indexing API: There was an error " +
                                "recording the screen." + status.toString());
                    }
                }
            });
            // Disconnecting the client
            mClient.disconnect();
        }
        super.onStop();
    }

    public void refreshList() {
        businessAdapter.refreshEventIdList();
        businessAdapter.notifyDataSetChanged();
    }

    @Override
    public void doFilter(BusinessListActivity.FilterType type, Object Content, int businessOrEvent) {

    }

    public void toggleFilter() {
//        Intent intent = new Intent(getActivity(), EventsFilterActivity.class);
//        startActivity(intent);
        View linearLayout1 = getView().findViewById(R.id.linearLayout1);
        if (linearLayout1.getVisibility() == View.VISIBLE) {
            search_bar.setVisibility(View.GONE);
            linearLayout1.setVisibility(View.GONE);
            view.findViewById(R.id.tabline).setVisibility(View.GONE);
            pager_view.setVisibility(View.GONE);
        } else {
            search_bar.setVisibility(View.GONE);
            linearLayout1.setVisibility(View.VISIBLE);
            view.findViewById(R.id.tabline).setVisibility(View.VISIBLE);
            pager_view.setVisibility(View.VISIBLE);
            ((TextView) view.findViewById(R.id.txt_no_data_business)).setVisibility(View.GONE);

        }
    }

    private void manageView() {

        rltLoadingView = (RelativeLayout) view.findViewById(R.id.rltLoadingView);
        view.findViewById(R.id.imgLoader).startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.rotate_indefinitely));
        eventListView = (ListView) view.findViewById(R.id.eventListView);
        businessListView = (ListView) view.findViewById(R.id.searchResultListView);
        searchBtn = (ImageView) view.findViewById(R.id.search_btn);
        searchName = (EditText) view.findViewById(R.id.search);
        mLocalitySearchEtxt = (EditText) view.findViewById(R.id.locality_search);
        searchName.addTextChangedListener(textWatcher);
        mLocalitySearchEtxt.addTextChangedListener(textWatcher);
        search_bar = (RelativeLayout) view.findViewById(R.id.search_bar);
        localityLayout = (LinearLayout) view.findViewById(R.id.localityLayout);
        //	ImageView imgView=(ImageView)findViewById(R.id.filter_icon);
        if (businessOrEventType == Constants.EVENT_PAGE_TYPE) {
            businessAdapter = new BusinessListingAdapterevent(getActivity(), FragmentBusinesslistEvents.this);
            businessListView.setVisibility(View.GONE);
            eventListView.setVisibility(View.VISIBLE);

            eventListView.setAdapter(businessAdapter);
        } else if (businessOrEventType == Constants.BUSINESS_PAGE_TYPE) {
            businessListView.setVisibility(View.VISIBLE);
            eventListView.setVisibility(View.GONE);

            businessListView.setAdapter(businessAdapter);
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
                    searchName.removeTextChangedListener(textWatcher);
                    if (!listItem.contains(",")) {
                        isContainCommaQuery = false;
                        searchName.setText(listItem);
                        mSearchList.setVisibility(View.GONE);
                        eventListView.setVisibility(View.VISIBLE);
                    } else {
                        isContainCommaQuery = true;
                        StringTokenizer tokens = new StringTokenizer(listItem, ",");
                        String first = tokens.nextToken();
                        String second = tokens.nextToken();
                        if (!StringUtils.isNullOrEmpty(first) && !StringUtils.isNullOrEmpty(second)) {
                            searchName.setText(first.trim());
                            mLocalitySearchEtxt.setText(second.trim());
                        }
                        mSearchList.setVisibility(View.GONE);
                        eventListView.setVisibility(View.VISIBLE);
                    }
                    searchName.addTextChangedListener(textWatcher);

                } else if (parent.getAdapter() instanceof SubLocalityAdapter) {
                    String listItem = (String) parent.getAdapter().getItem(pos).toString();
                    String queryData = searchName.getText().toString();
                    String locality = mLocalitySearchEtxt.getText().toString();
                    String localitysearch = "";

                    if (!queryData.equals("")) {

                        if (locality.equals("") || !isContainCommaQuery) {
                            String localityData = (String) parent.getAdapter().getItem(pos);
                            mLocalitySearchEtxt.setText(listItem);
                            if (!StringUtils.isNullOrEmpty(localityData)) {

                                if (pos != 0) {
                                    localitysearch = localityData;

                                }

                            }
                        } else {
                            localitysearch = locality;

                        }
                        mIsComingFromFilter = false;
                        mSearchList.setVisibility(View.GONE);
                        eventListView.setVisibility(View.VISIBLE);
                        hitBusinessSearchListingApi(queryData, localitysearch, mPageCount);
                        //  args.putBoolean("isSearchListing", true);
                        // infoFragment.setArguments(args);
                    } else {
                        ToastUtils.showToast(getActivity(), getString(R.string.no_query));
                    }
                }
            }
        });
        searchBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String queryData = searchName.getText().toString();
                String localityData = mLocalitySearchEtxt.getText().toString();
                if (StringUtils.isNullOrEmpty(queryData)) {
                    ToastUtils.showToast(getActivity(), getString(R.string.no_query));
                    return;
                }
                if (!StringUtils.isNullOrEmpty(queryData) || !StringUtils.isNullOrEmpty(localityData)) {
                    mIsComingFromFilter = false;
                    hitBusinessSearchListingApi(queryData, "", mPageCount);
                }
                hideSearchList();
            }
        });
        businessListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

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

        eventListView.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                String searchText = searchName.getText().toString().trim();
                if (searchText.equals("")) {
                    if (firstVisibleItem == 0) {
                        search_bar.setVisibility(View.GONE);
                        localityLayout.setVisibility(View.GONE);
                    } else {
                        search_bar.setVisibility(View.VISIBLE);
                    }
                }
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


        searchName.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (localityLayout.getVisibility() != View.VISIBLE)
                    localityLayout.setVisibility(View.VISIBLE);
                return false;
            }
        });


        businessListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                Intent intent = new Intent(getActivity(), BusinessDetailsActivity.class);
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

                    intent.putExtra(Constants.DISTANCE, businessListData.getDistance());
                    startActivity(intent);
                }
            }
        });
        eventListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                Intent intent = new Intent(getActivity(), BusinessDetailsActivity.class);
                String businessId = null;
                if (parent.getAdapter() instanceof BusinessListingAdapter) {
                    BusinessDataListing businessListData = (BusinessDataListing) ((BusinessListingAdapter) parent.getAdapter()).getItem(pos);
                    businessId = businessListData.getId();
                    intent.putExtra(Constants.CATEGORY_ID, categoryId);
                    intent.putExtra(Constants.BUSINESS_OR_EVENT_ID, businessId);
                    intent.putExtra(Constants.PAGE_TYPE, Constants.EVENT_PAGE_TYPE);
                    intent.putExtra(Constants.DISTANCE, businessListData.getDistance());
                    startActivity(intent);
                } else if (parent.getAdapter() instanceof BusinessListingAdapterevent) {
                    BusinessDataListing businessListData = (BusinessDataListing) ((BusinessListingAdapterevent) parent.getAdapter()).getItem(pos);
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

    public int getAge(Date dateOfBirth) {
        int age = 0;

        Calendar born = Calendar.getInstance();
        Calendar now = Calendar.getInstance();

        if (dateOfBirth != null) {
            now.setTime(new Date());
            born.setTime(dateOfBirth);
            if (now.get(Calendar.YEAR) == born.get(Calendar.YEAR)) {
                age = 0;
            } else if (now.get(Calendar.YEAR) > born.get(Calendar.YEAR)) {
                if (born.get(Calendar.MONTH) <= now.get(Calendar.MONTH) && born.get(Calendar.DAY_OF_MONTH) <= now.get(Calendar.DAY_OF_MONTH)) {
                    age = now.get(Calendar.YEAR) - born.get(Calendar.YEAR);
                } else {
                    age = now.get(Calendar.YEAR) - born.get(Calendar.YEAR);
                    age = age - 1;
                }
            }
        }

        return age;
    }

    public void filter(String query) {

        query = query.toLowerCase();

        ArrayList<BusinessDataListing> newList = new ArrayList<>();
        if (query.isEmpty()) {
            //childList.clear();
            mBusinessDataListingschild.addAll(mBusinessDataListings);
        } else {

            for (int i = 0; i < mBusinessDataListings.size(); i++) {

                BusinessDataListing b = mBusinessDataListings.get(i);
                if (!StringUtils.isNullOrEmpty(b.getName())) {
                    if (b.getName().toLowerCase().contains(query))

                        newList.add(b);
                }
            }
        }

        // businessAdapter.setListData();
        businessAdapter.setListData(newList, businessOrEventType);
        eventListView.setAdapter(businessAdapter);
        businessAdapter.notifyDataSetChanged();
    }

    public void hitBusinessListingApiSorted(int categoryId, int page, HashMap<MapTypeFilter, String> mFilterMap) {
        if (!ConnectivityUtils.isNetworkEnabled(getActivity())) {
            ToastUtils.showToast(getActivity(), getString(R.string.toast_network_error));
            return;
        }

        if (page == 1) {
            showProgressDialog(getString(R.string.fetching_data));
        }
        HashMap<MapTypeFilter, String> filterValues = mFilterMap;
        String finalValuesForFilter = "";
        for (Map.Entry<MapTypeFilter, String> entry : filterValues.entrySet()) {
            MapTypeFilter key = entry.getKey();
            String value = entry.getValue();
            finalValuesForFilter = finalValuesForFilter + value;

        }

        mBusinessDataListings = new ArrayList<BusinessDataListing>();
        mPageCount = 1;
        businessListRequest = new BusinessListRequest();
        businessListRequest.setCategory_id(categoryId + "");
        if (Constants.IS_SEARCH_LISTING) {

            if (!StringUtils.isNullOrEmpty(querySearch))
                businessListRequest.setQuerySearch(querySearch);
            businessListRequest.setLocalitySearch(localitySearch);
        }

        businessListRequest.setCity_id((SharedPrefUtils.getCurrentCityModel(getActivity())).getId() + "");
        businessListRequest.setPage(mPageCount + "");
        //System.out.println(finalValuesForFilter);
        businessListRequest.setTotalFilterValues(finalValuesForFilter);
        BusinessListController businessListController = new BusinessListController(getActivity(), this);
        if (mIsComingFromFilter) {
            businessListRequest.setPage(page + "");
            businessListController.getData(AppConstants.BUSINESS_LIST_REQUEST, businessListRequest);
        } else {
            if (Constants.IS_SEARCH_LISTING) {
                businessListController.getData(AppConstants.BUSINESS_SEARCH_LISTING_REQUEST, businessListRequest);
            } else {
                businessListController.getData(AppConstants.BUSINESS_LIST_REQUEST, businessListRequest);
            }
        }

        mIsRequestRunning = true;
        mIsComingFromFilter = true;
    }

    public void hitBusinessListingApi(int categoryId, int page) {
        if (!ConnectivityUtils.isNetworkEnabled(getActivity())) {
            ToastUtils.showToast(getActivity(), getString(R.string.toast_network_error));
            return;
        }

        if (page == 1) {
            showProgressDialog(getString(R.string.fetching_data));
        }
        BusinessListController businessListController = new BusinessListController(getActivity(), this);
        if (mIsComingFromFilter) {
            businessListRequest.setPage(page + "");
            businessListController.getData(AppConstants.BUSINESS_LIST_REQUEST, businessListRequest);
        } else {
            BusinessListRequest businessListRequest = new BusinessListRequest();
            businessListRequest.setCategory_id(categoryId + "");
            businessListRequest.setCity_id((SharedPrefUtils.getCurrentCityModel(getActivity())).getId() + "");
            businessListRequest.setPage(page + "");
            businessListController.getData(AppConstants.BUSINESS_LIST_REQUEST, businessListRequest);
        }
        mIsRequestRunning = true;
        //	mIsComingFromFilter=false;
    }

    private void hitBusinessSearchListingApi(String query, String locality, int page) {
//        Log.d("check","query "+query);
        if (!ConnectivityUtils.isNetworkEnabled(getActivity())) {
            ToastUtils.showToast(getActivity(), getString(R.string.toast_network_error));
            return;
        }

        if (page == 1) {
            showProgressDialog(getString(R.string.fetching_data));
        }
        BusinessListController businessListController = new BusinessListController(getActivity(), this);
        if (mIsComingFromFilter) {
//            Log.d("check","mIsComingFromFilter ");
            businessListRequest.setPage(page + "");
            businessListController.getData(AppConstants.BUSINESS_SEARCH_LISTING_REQUESTEVENTNEW, businessListRequest);
        } else {
//            Log.d("check","not mIsComingFromFilter ");
            BusinessListRequest businessListRequest = new BusinessListRequest();
            businessListRequest.setCity_id((SharedPrefUtils.getCurrentCityModel(getActivity())).getId() + "");
            businessListRequest.setPage(page + "");
            businessListRequest.setQuerySearch(query);
            if (!StringUtils.isNullOrEmpty(locality))
                businessListRequest.setLocalitySearch(locality);

            businessListController.getData(AppConstants.BUSINESS_SEARCH_LISTING_REQUESTEVENTNEW, businessListRequest);
        }
        mIsRequestRunning = true;
    }

    @Override
    protected void updateUi(Response response) {
        mIsRequestRunning = false;
        removeProgressDialog();
        if (response.getResponseObject() instanceof AutoSuggestResponse) {
            if (searchName.getText().toString().trim().equals("")) {
                eventListView.setVisibility(View.VISIBLE);
                mSearchList.setVisibility(View.GONE);
                return;
            }
            AutoSuggestResponse responseData1 = (AutoSuggestResponse) response.getResponseObject();
//            Log.d("check","onTextChanged updateUi "+responseData1);
            String message1 = responseData1.getResult().getMessage();
            if (responseData1.getResponseCode() == 200) {
                ArrayList<String> queryList = responseData1.getResult().getData().getSuggest();
//                Log.d("check","onTextChanged updateUi queryList "+queryList.size());
                if (!queryList.isEmpty()) {
                    eventListView.setVisibility(View.GONE);
                    mSearchList.setVisibility(View.VISIBLE);
                    ((TextView) view.findViewById(R.id.txt_no_data_business)).setVisibility(View.GONE);
                    //mLoutProgress.setVisibility(View.GONE);
                    ArrayAdapter<String> mQueryAdapter = new ArrayAdapter<String>(getActivity(), R.layout.text_for_locality, queryList);
                    mSearchList.setAdapter(mQueryAdapter);
                    mQueryAdapter.notifyDataSetChanged();
                } else {
                    //  mLoutProgress.setVisibility(View.GONE);
                    eventListView.setVisibility(View.VISIBLE);
                    mSearchList.setVisibility(View.GONE);
                }

            } else if (responseData1.getResponseCode() == 400) {

                mSearchList.setVisibility(View.GONE);
                eventListView.setVisibility(View.VISIBLE);
            }
            if (response == null) {
                ToastUtils.showToast(getActivity(), getString(R.string.toast_server_error));
                return;
            }
        } else if (isagefilter) {
//                Log.d("check", "update ui isagefilter " + isagefilter);
            BusinessListResponse responseData = (BusinessListResponse) response.getResponseObject();
            BusinessListingAdapterevent adapter = new BusinessListingAdapterevent(getActivity(), FragmentBusinesslistEvents.this);
            mBusinessDataListings.addAll(responseData.getResult().getData().getData());
            businessAdapter.setListData(mBusinessDataListings, businessOrEventType);
            businessAdapter.notifyDataSetChanged();
            isagefilter = false;
        } else {
//                Log.d("check", "update ui");
            if (response == null) {
                ToastUtils.showToast(getActivity(), getString(R.string.toast_server_error));
                return;
            }
            BusinessListResponse responseData = (BusinessListResponse) response.getResponseObject();
            if (rltLoadingView.getVisibility() == View.VISIBLE) {
                rltLoadingView.setVisibility(View.GONE);
            }
//                Log.d("check", "update ui responseData " + responseData);
//                Log.d("check", "update ui response.getDataType() " + response.getDataType());
            switch (response.getDataType()) {
                case AppConstants.BUSINESS_LIST_REQUEST:
                    search_bar.setVisibility(View.GONE);
                    if (responseData.getResponseCode() == Constants.HTTP_RESPONSE_SUCCESS) {
//                            Log.d("check", "update ui responseData.getResponseCode() " + responseData.getResponseCode());
                        mBusinessListCount = responseData.getResult().getData().getTotal();
                        mTotalPageCount = responseData.getResult().getData().getPage_count();
                        //to add in already created list
                        // we neew to clear this list in case of sort by and filter
                        mBusinessDataListings.addAll(responseData.getResult().getData().getData());
                        businessAdapter.setListData(mBusinessDataListings, businessOrEventType);
                        businessAdapter.notifyDataSetChanged();
                        if (mBusinessDataListings.isEmpty()) {
                            ((TextView) view.findViewById(R.id.txt_no_data_business)).setVisibility(View.VISIBLE);
                            businessListView.setVisibility(View.GONE);
                        } else {
                            ((TextView) view.findViewById(R.id.txt_no_data_business)).setVisibility(View.GONE);
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
                                ((TextView) view.findViewById(R.id.txt_no_data_business)).setVisibility(View.VISIBLE);
                                ((TextView) view.findViewById(R.id.txt_no_data_business)).setText(getString(R.string.event_list));
                            } else {
                                ((TextView) view.findViewById(R.id.txt_no_data_business)).setVisibility(View.VISIBLE);
                            }

                            //	businessListView.setVisibility(View.GONE);
                        } else {
                            ((TextView) view.findViewById(R.id.txt_no_data_business)).setVisibility(View.GONE);
                            if (businessOrEventType == Constants.EVENT_PAGE_TYPE) {
                                eventListView.setVisibility(View.VISIBLE);
                            } else {
                                businessListView.setVisibility(View.VISIBLE);
                            }

                        }

                        break;
                    } else {
                        ToastUtils.showToast(getActivity(), getString(R.string.toast_response_error));
                        getActivity().finish();
                    }
                    break;
                case AppConstants.BUSINESS_SEARCH_LISTING_REQUEST:
                    search_bar.setVisibility(View.GONE);
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
                            ((TextView) view.findViewById(R.id.txt_no_data_business)).setVisibility(View.GONE);
                        } else {
                            ((TextView) view.findViewById(R.id.txt_no_data_business)).setVisibility(View.VISIBLE);
                            businessListView.setVisibility(View.GONE);
                        }
                    } else if (responseData.getResponseCode() == 400) {
                        Constants.IS_PAGE_AVAILABLE = false;
                        if (mBusinessDataListings.isEmpty()) {
                            ((TextView) view.findViewById(R.id.txt_no_data_business)).setVisibility(View.VISIBLE);
                            businessListView.setVisibility(View.GONE);
                            eventListView.setVisibility(View.GONE);
                        } else {
                            ((TextView) view.findViewById(R.id.txt_no_data_business)).setVisibility(View.GONE);
                            if (businessOrEventType == Constants.EVENT_PAGE_TYPE) {
                                eventListView.setVisibility(View.VISIBLE);
                            } else {
                                businessListView.setVisibility(View.VISIBLE);
                            }


                        }
                        break;
                    } else {

                        ToastUtils.showToast(getActivity(), getString(R.string.toast_response_error));
                        getActivity().finish();
                    }
                    break;
                case AppConstants.BUSINESS_SEARCH_LISTING_REQUESTEVENTNEW:
                    search_bar.setVisibility(View.GONE);
                    ArrayList<BusinessDataListing> mBusinessDataListings1 = new ArrayList<>();
                    mSearchList.setVisibility(View.GONE);
                    if (responseData.getResponseCode() == Constants.HTTP_RESPONSE_SUCCESS) {
                        mSortbyArrays = responseData.getResult().getData().getSortBy();
                        advancedListFromSearch = responseData.getResult().getData().getAdvancedSearch();
                        mAgeGroupListFromSearch = responseData.getResult().getData().getAgeGroup();
                        mBusinessListCount = responseData.getResult().getData().getTotal();
                        mTotalPageCount = responseData.getResult().getData().getPage_count();
                        //to add in already created list
                        // we neew to clear this list in case of sort by and filter
                        mBusinessDataListings1.addAll(responseData.getResult().getData().getData());
                        businessAdapter.setListData(mBusinessDataListings1, businessOrEventType);

                        businessAdapter.notifyDataSetChanged();
                        if (!mBusinessDataListings1.isEmpty()) {
                            businessListView.setVisibility(View.VISIBLE);
                            ((TextView) view.findViewById(R.id.txt_no_data_business)).setVisibility(View.GONE);
                        } else {
                            ((TextView) view.findViewById(R.id.txt_no_data_business)).setVisibility(View.VISIBLE);
                            businessListView.setVisibility(View.GONE);
                        }
                    } else if (responseData.getResponseCode() == 400) {
                        Constants.IS_PAGE_AVAILABLE = false;
                        if (mBusinessDataListings1.isEmpty()) {
//                                Log.d("check", "mBusinessDataListings size 400 if" + mBusinessDataListings1.size());
                            ((TextView) view.findViewById(R.id.txt_no_data_business)).setVisibility(View.VISIBLE);
                            businessListView.setVisibility(View.GONE);
                            eventListView.setVisibility(View.GONE);
                        } else {
//                                Log.d("check", "mBusinessDataListings size 400 else " + mBusinessDataListings1.size());
                            ((TextView) view.findViewById(R.id.txt_no_data_business)).setVisibility(View.GONE);
                            if (businessOrEventType == Constants.EVENT_PAGE_TYPE) {
                                eventListView.setVisibility(View.VISIBLE);
                            } else {
                                businessListView.setVisibility(View.VISIBLE);
                            }


                        }
                        break;
                    } else {

                        ToastUtils.showToast(getActivity(), getString(R.string.toast_response_error));
                        getActivity().finish();
                    }
                    hideSearchList();

                    break;
            }

        }

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txvFilter:


                //	if(!Constants.IS_SEARCH_LISTING){

			/*if(businessOrEventType==Constants.BUSINESS_PAGE_TYPE)
            {*/
//                if (chosen_tab == TabType.Filter) {
//                    ((TextView) view.findViewById(R.id.txt_no_data_business)).setVisibility(View.GONE);
//                    //	tab_host.setVisibility(View.GONE) ;
//
//                    if (tab_host.getVisibility() == View.GONE) {
//
//                        manageTabForFilter();
//                        if (advancedListFromDb == null || advancedListFromDb.isEmpty()) {
//                            ToastUtils.showToast(getActivity(), "There is no filters available");
//                            return;
//                        }
//                        pager_view.setVisibility(View.VISIBLE);
//                        Animation bottomUp = AnimationUtils.loadAnimation(getActivity(), R.anim.bottom_down);
//                        tab_host.startAnimation(bottomUp);
//                        tab_host.setVisibility(View.VISIBLE);
//                        ((TextView) view.findViewById(R.id.txvSortBy)).setBackgroundColor(getResources().getColor(R.color.tab_unchecked));
//                        ((TextView) view.findViewById(R.id.txvFilter)).setBackgroundColor(getResources().getColor(R.color.tab_checked));
//                    } else {
//                        Animation bottomUp = AnimationUtils.loadAnimation(getActivity(), R.anim.bottom_up);
//                        tab_host.startAnimation(bottomUp);
//                        tab_host.setVisibility(View.GONE);
//                        ///	((TextView)findViewById(R.id.txvSortBy)).setBackgroundColor(getResources().getColor(R.color.tab_unchecked));
//                        ((TextView) view.findViewById(R.id.txvFilter)).setBackgroundColor(getResources().getColor(R.color.tab_unchecked));
//
//                    }
//                } else {
//                    manageTabForFilter();
//                    if (advancedListFromDb == null || advancedListFromDb.isEmpty()) {
//                        ToastUtils.showToast(getActivity(), "There is no filters available");
//                        return;
//                    }
//                    Animation bottomUp = AnimationUtils.loadAnimation(getActivity(), R.anim.bottom_down);
//                    tab_host.startAnimation(bottomUp);
//                    tab_host.setVisibility(View.VISIBLE);
//                    pager_view.setVisibility(View.VISIBLE);
//                    ((TextView) view.findViewById(R.id.txvSortBy)).setBackgroundColor(getResources().getColor(R.color.tab_unchecked));
//                    ((TextView) view.findViewById(R.id.txvFilter)).setBackgroundColor(getResources().getColor(R.color.tab_checked));
//
//                }


                //	}else if(businessOrEventType==Constants.EVENT_PAGE_TYPE){



			/*if(chosen_tab == TabType.Filter ) {
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



				}	*/
                //	}
            /*}else{
                showToast(getResources().getString(R.string.no_data));
			}*/
                break;
            case R.id.imgBack:
            /*if(Constants.IS_SEARCH_LISTING){
                startActivity(new Intent(this,HomeCategoryActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
			}else{*/
                getActivity().finish();
                //}
                break;
            case R.id.imgSearch: {
                Intent intent = new Intent(getActivity(), AutoSuggestTransparentDialogActivity.class);
                //	intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                //startActivityForResult(intent, 1);
                startActivity(intent);
                //finish();
            }
            break;

//            case R.id.txvSortBy:
//                if (mSortbyArrays != null && mSortbyArrays.size() > 0) {
//                    if (chosen_tab == TabType.Sort) {
//                        ((TextView) view.findViewById(R.id.txt_no_data_business)).setVisibility(View.GONE);
//                        if (tab_host.getVisibility() == View.GONE) {
//                            manageTabForSort();
//                            Animation bottomDown = AnimationUtils.loadAnimation(getActivity(), R.anim.bottom_down);
//                            tab_host.startAnimation(bottomDown);
//                            tab_host.setVisibility(View.VISIBLE);
//                            pager_view.setVisibility(View.GONE);
//                            ((TextView) view.findViewById(R.id.txvSortBy)).setBackgroundColor(getResources().getColor(R.color.tab_checked));
//                            ((TextView) view.findViewById(R.id.txvFilter)).setBackgroundColor(getResources().getColor(R.color.tab_unchecked));
//                        } else {
//                            Animation bottomUp = AnimationUtils.loadAnimation(getActivity(), R.anim.bottom_up);
//                            tab_host.startAnimation(bottomUp);
//                            tab_host.setVisibility(View.GONE);
//
//                            pager_view.setVisibility(View.GONE);
//
//                            ((TextView) view.findViewById(R.id.txvSortBy)).setBackgroundColor(getResources().getColor(R.color.tab_unchecked));
//                        }
//                    } else {
//                        manageTabForSort();
//                        Animation bottomUp = AnimationUtils.loadAnimation(getActivity(), R.anim.bottom_down);
//                        tab_host.startAnimation(bottomUp);
//                        tab_host.setVisibility(View.VISIBLE);
//                        pager_view.setVisibility(View.GONE);
//                        ((TextView) view.findViewById(R.id.txvSortBy)).setBackgroundColor(getResources().getColor(R.color.tab_checked));
//                        ((TextView) view.findViewById(R.id.txvFilter)).setBackgroundColor(getResources().getColor(R.color.tab_unchecked));
//                    }
//                }
//                break;
//            case android.R.id.tabhost:
//                if (chosen_tab == TabType.Sort) {
//                    LinearLayout layout = new LinearLayout(getActivity());
//                    layout.setLayoutParams(new ViewGroup.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
//                }
//                break;
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
            Bundle args = new Bundle();
            if (!advancedListFromDb.isEmpty() && advancedListFromDb != null) {
                if (advancedListFromDb.get(i).getKey().equalsIgnoreCase("locality") && advancedListFromDb.get(i).getValue().equalsIgnoreCase("Locality")) {
                    LocalitiesFragment localitiesFragment = new LocalitiesFragment();
                    args.putInt(Constants.PAGE_TYPE, businessOrEventType);
                    localitiesFragment.setAction(FragmentBusinesslistEvents.this);
                    localitiesFragment.setArguments(args);
                    return localitiesFragment;
                } else if (advancedListFromDb.get(i).getKey().equalsIgnoreCase("age_group") || advancedListFromDb.get(i).getValue().equalsIgnoreCase("Age Group")) {
                    AgeGroupFragment ageGroupFragment = new AgeGroupFragment();
                    ageGroupFragment.setAction(FragmentBusinesslistEvents.this);
                    args.putInt(Constants.CATEGORY_KEY, categoryId);
                    args.putInt(Constants.PAGE_TYPE, businessOrEventType);
                    args.putBoolean("isComeFromSearch", Constants.IS_SEARCH_LISTING);
                    args.putParcelableArrayList("AgeGroupArray", mAgeGroupListFromSearch);
                    ageGroupFragment.setArguments(args);
                    return ageGroupFragment;
                } else if (advancedListFromDb.get(i).getKey().equalsIgnoreCase("sub_category") || advancedListFromDb.get(i).getValue().equalsIgnoreCase("Sub Category")) {
                    SubCategoryFragment categoryFragment = new SubCategoryFragment();
                    categoryFragment.setAction(FragmentBusinesslistEvents.this);
                    args.putInt(Constants.CATEGORY_KEY, categoryId);
                    args.putInt(Constants.PAGE_TYPE, businessOrEventType);
                    categoryFragment.setArguments(args);
                    return categoryFragment;
                } else if (advancedListFromDb.get(i).getKey().equalsIgnoreCase("more")) {
                    MoreFragment fragment = new MoreFragment();
                    fragment.setAction(FragmentBusinesslistEvents.this);
                    args.putInt(Constants.CATEGORY_KEY, categoryId);
                    args.putInt(Constants.PAGE_TYPE, businessOrEventType);
                    fragment.setArguments(args);
                    return fragment;
                } else if (advancedListFromDb.get(i).getKey().equalsIgnoreCase("activities")) {
                    ActivitiesFragment fragment = new ActivitiesFragment();
                    fragment.setAction(FragmentBusinesslistEvents.this);
                    args.putInt(Constants.CATEGORY_KEY, categoryId);
                    args.putInt(Constants.PAGE_TYPE, businessOrEventType);
                    fragment.setArguments(args);
                    return fragment;
                } else if (advancedListFromDb.get(i).getKey().equalsIgnoreCase("date")) {
                    DateFragment fragment = new DateFragment();
                    fragment.setAction(FragmentBusinesslistEvents.this);
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

    private void addTab(Context myTabActivity, TabHost tabHost, TabHost.TabSpec tabSpec) {
        tabSpec.setContent(new MyTabFactory(myTabActivity));
        tabHost.addTab(tabSpec);
        getFilterAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        int pos = this.pager_view.getCurrentItem();
        this.tab_host.setCurrentTab(pos);
        View tabView = tab_host.getTabWidget().getChildAt(position);
        HorizontalScrollView _list = (HorizontalScrollView) view.findViewById(R.id.horizontalList);
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
        //  this.tab_host.getTabWidget().setCurrentTab(pos);
        // pager_view.setCurrentItem(pos);
    }

    @Override
    public void onTabChanged(String arg0) {

        ((TextView) view.findViewById(R.id.txt_no_data_business)).setVisibility(View.GONE);

        //	Toast.makeText(BusinessListActivity.this, "call "+arg0, Toast.LENGTH_SHORT).show();

        if (!(tab_host.getCurrentTabTag().equals("Tab0"))) {
            if (pager_view.getVisibility() == View.GONE) {
                Animation bottomUp = AnimationUtils.loadAnimation(getActivity(), R.anim.bottom_down);
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

        //tabcolor change
       /* TextView tv = (TextView) tab_host.getTabWidget().getChildAt(pos).findViewById(android.R.id.tab_title);
        tv.setTextColor(Color.parseColor("#000000"));*/
        //textView.setTextColor(getResources().getColorStateList(R.color.selectortabtext));
//        if (chosen_tab == TabType.Filter)
//            selected_index_filter = pos;
//        else
//            selected_index_sort = pos;

        manageIndicator(pos);

        View tabView = tab_host.getTabWidget().getChildAt(pos);
        HorizontalScrollView _list = (HorizontalScrollView) view.findViewById(R.id.horizontalList);
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
//        LayoutInflater inflater = getActivity().getLayoutInflater();
//        if (mSortbyArrays != null) {
//            for (int i = 0; i < mSortbyArrays.size(); i++) {
//                SortBy by = mSortbyArrays.get(i);
//                LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.tab_design, null);
//                ((TextView) layout.findViewById(R.id.tab_title)).setText(by.getValue().toUpperCase());
//                //	layout.setBackgroundResource(R.drawable.detail_tab_selector);
//
//                this.tab_host.getTabWidget().setDividerDrawable(R.drawable.tab_border);
//                addTab(getActivity(), this.tab_host, this.tab_host.newTabSpec("SortTab" + i).setIndicator(layout));
//            }
//        }
//        tab_host.getTabWidget().getChildAt(0).setSelected(false);
//        manageIndicator(selected_index_sort);
//
//        for (int i = 0; i < tab_host.getTabWidget().getTabCount(); i++) {
//            final int j = i;
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
//
//            final BusinessListController businessListController = new BusinessListController(getActivity(), this);
//            mPageCount = 1;
//            businessListRequest = new BusinessListRequest();
//            businessListRequest.setCategory_id(categoryId + "");
//            if (Constants.IS_SEARCH_LISTING) {
//                businessListRequest.setQuerySearch(querySearch);
//                businessListRequest.setLocalitySearch(localitySearch);
//            }
//            businessListRequest.setCity_id((SharedPrefUtils.getCurrentCityModel(getActivity())).getId() + "");
//            businessListRequest.setPage(mPageCount + "");
//            mBusinessDataListings = new ArrayList<BusinessDataListing>();
//            tab_host.getTabWidget().getChildAt(j).setOnClickListener(new View.OnClickListener() {
//
//                @Override
//                public void onClick(View v) {
//                    showProgressDialog(getString(R.string.fetching_data));
//                    SortBy sortBy = mSortbyArrays.get(j);
//                    rejectLocally(Constants.BUSINESS_PAGE_TYPE);
//                    businessListRequest.setSort_by(sortBy.getKey());
//
//                    HashMap<MapTypeFilter, String> filterValues = mFilterMap;
//                    String finalValuesForFilter = "";
//                    for (Map.Entry<MapTypeFilter, String> entry : filterValues.entrySet()) {
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
            AdvancedSearchTable _table = new AdvancedSearchTable((BaseApplication) getActivity().getApplicationContext());
            advancedListFromDb = _table.getAllAdvancedSearch(categoryId);

        } else if (Constants.IS_SEARCH_LISTING || categoryId == 0) {
            advancedListFromDb = new ArrayList<AdvancedSearch>();
            advancedListFromDb = advancedListFromSearch;
        }
        /*if( advancedListFromDb==null||advancedListFromDb.isEmpty()){
            showToast("There is no filters available");
			return;
		}*/

//        chosen_tab = TabType.Filter;

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
        LayoutInflater inflater = getActivity().getLayoutInflater();
        LinearLayout layout = null;
        if (advancedListFromDb != null) {
            for (int i = 0; i < advancedListFromDb.size(); i++) {

                layout = (LinearLayout) inflater.inflate(R.layout.tab_design, null);
                // layout.setBackgroundColor(Color.WHITE);
                String tabTitle = advancedListFromDb.get(i).getValue().toUpperCase();
                if (tabTitle.equalsIgnoreCase("AGE GROUP")) {
                    tabTitle = "AGE";
                }
                ((TextView) layout.findViewById(R.id.tab_title)).setText(tabTitle);

                //	}

				/*	if(businessOrEventType==Constants.EVENT_PAGE_TYPE){
                layout.setBackgroundResource(R.drawable.tab_selector);
			}else if(businessOrEventType==Constants.BUSINESS_PAGE_TYPE){*/
                //  layout.setBackgroundResource(R.drawable.detail_tab_selector);
                //	}
                /*	if(this.tab_host.getTabWidget().getTabCount()==1){
                layout.setBackgroundResource(R.color.tab_color);*/

				/*	}else{

			layout.setBackgroundResource(R.drawable.detail_tab_selector);
			}*/
                //	tab_host.getTabWidget().getChildAt(0).setLayoutParams(new LinearLayout.LayoutParams(50,40));

                //	layout.setBackgroundResource(R.drawable.detail_tab_selector);

                this.tab_host.getTabWidget().setDividerDrawable(R.drawable.tab_border);
                addTab(getActivity(), this.tab_host, this.tab_host.newTabSpec("Tab" + i).setIndicator(layout));
                //this.tab_host.setSelected(false);

            }
        }
        if (businessOrEventType == Constants.EVENT_PAGE_TYPE) {
            this.tab_host.getTabWidget().getChildAt(0).setSelected(true);
        }


        manageIndicator(selected_index_filter);
    }


    private ArrayList<SortBy> getSorts() {
        SortByTable table = new SortByTable((BaseApplication) getActivity().getApplication());
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


    /**
     * Change according to client requirement:
     */
    @Override
    public void doNewFilter(int businessOrEventType) {
//       Log.d("check","check filter");
        try {

            search_bar.setVisibility(View.GONE);

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

            businessListRequest.setCity_id((SharedPrefUtils.getCurrentCityModel(getActivity())).getId() + "");
            businessListRequest.setPage(mPageCount + "");
            rejectLocally(businessOrEventType);
            if (ConnectivityUtils.isNetworkEnabled(getActivity())) {
                showProgressDialog(getString(R.string.fetching_data));


                HashMap<MapTypeFilter, String> filterValues = mFilterMap;
                String finalValuesForFilter = "";
                for (Map.Entry<MapTypeFilter, String> entry : filterValues.entrySet()) {
                    MapTypeFilter key = entry.getKey();
                    String value = entry.getValue();
                    finalValuesForFilter = finalValuesForFilter + value;

                    //	System.out.println(key + " " + value);
                }

                //System.out.println(finalValuesForFilter);
                businessListRequest.setTotalFilterValues(finalValuesForFilter);

                // mFilterMap=new HashMap<MapTypeFilter, String>();


                BusinessListController businessListController = new BusinessListController(getActivity(), this);
                if (Constants.IS_SEARCH_LISTING) {
                    businessListController.getData(AppConstants.BUSINESS_SEARCH_LISTING_REQUEST, businessListRequest);
                } else {
                    businessListController.getData(AppConstants.BUSINESS_LIST_REQUEST, businessListRequest);
                }
                mIsRequestRunning = true;
                mIsComingFromFilter = true;
            } else {
                ToastUtils.showToast(getActivity(), getString(R.string.netwrok_error));
            }

        } catch (Exception e) {
            Log.i("filters", e.getMessage());
        }
    }


    /**
     * previous work:
     */


    final Animation.AnimationListener makeTopGone = new Animation.AnimationListener() {

        @Override
        public void onAnimationStart(Animation animation) {
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            try {
                view.findViewById(R.id.linearLayout1).setVisibility(View.GONE);
                view.findViewById(R.id.tabline).setVisibility(View.GONE);
                pager_view.setVisibility(View.GONE);
//                pager_view.setAdapter(getFilterAdapter);
//                if (tab_host != null && tab_host.getTabWidget() != null && tab_host.getTabWidget().getTabCount() > 0) {
//
//                    tab_host.setCurrentTab(0);
//                    tab_host.getTabWidget().getChildAt(0).setSelected(false);
//                }
            } catch (Exception e) {
                e.printStackTrace();


            }
        }
    };

//    final Animation.AnimationListener makeTopGoneLocally = new Animation.AnimationListener() {
//
//        @Override
//        public void onAnimationStart(Animation animation) {
//        }
//
//        @Override
//        public void onAnimationRepeat(Animation animation) {
//        }
//
//        @Override
//        public void onAnimationEnd(Animation animation) {
//            try {
//                pager_view.setVisibility(View.GONE);
//                if (tab_host != null && tab_host.getTabWidget() != null && tab_host.getTabWidget().getTabCount() > 0) {
//
//                    tab_host.setCurrentTab(0);
//                    tab_host.getTabWidget().getChildAt(0).setSelected(false);
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//
//
//            }
//        }
//    };
//    final Animation.AnimationListener makeTopGoneTabHost = new Animation.AnimationListener() {
//
//        @Override
//        public void onAnimationStart(Animation animation) {
//        }
//
//        @Override
//        public void onAnimationRepeat(Animation animation) {
//        }
//
//        @Override
//        public void onAnimationEnd(Animation animation) {
//            Log.d("View Pager", "onAnimationEnd - makeTopGone");
//            tab_host.setVisibility(View.GONE);
//            pager_view.setAdapter(getFilterAdapter);
//        }
//    };


    public void rejectLocally(int type) {
        int size = this.tab_host.getTabWidget().getTabCount();

        for (int i = 0; i < size; i++) {
            this.tab_host.getTabWidget().getChildAt(i).setSelected(false);
        }

        if (type == Constants.BUSINESS_PAGE_TYPE) {
            Animation bottomUp = AnimationUtils.loadAnimation(getActivity(), R.anim.bottom_up);
            tab_host.startAnimation(bottomUp);
            tab_host.setVisibility(View.GONE);
        } else if (type == Constants.EVENT_PAGE_TYPE) {
            Animation bottomUp = AnimationUtils.loadAnimation(getActivity(), R.anim.bottom_up);
            bottomUp.setAnimationListener(makeTopGone);
            pager_view.startAnimation(bottomUp);
            //	pager_view.setVisibility(View.INVISIBLE) ;
            //	pager_view.setVisibility(View.GONE) ;
        }
    }

    @Override
    public void cancel(int type) {
        ((TextView) view.findViewById(R.id.txvSortBy)).setBackgroundColor(getResources().getColor(R.color.tab_unchecked));
        ((TextView) view.findViewById(R.id.txvFilter)).setBackgroundColor(getResources().getColor(R.color.tab_unchecked));

        Animation bottomUp = AnimationUtils.loadAnimation(getActivity(), R.anim.bottom_up);
        bottomUp.setAnimationListener(makeTopGone);
        pager_view.startAnimation(bottomUp);

    }

    @Override
    public void reject(int type) {
        ((TextView) view.findViewById(R.id.txvSortBy)).setBackgroundColor(getResources().getColor(R.color.tab_unchecked));
        ((TextView) view.findViewById(R.id.txvFilter)).setBackgroundColor(getResources().getColor(R.color.tab_unchecked));
        mFilterMap = new HashMap<MapTypeFilter, String>();
        mBusinessDataListings = new ArrayList<BusinessDataListing>();

        mPageCount = 1;
        businessListRequest = new BusinessListRequest();
        businessListRequest.setCategory_id(categoryId + "");
        businessListRequest.setCity_id((SharedPrefUtils.getCurrentCityModel(getActivity())).getId() + "");
        businessListRequest.setPage(mPageCount + "");

        Constants.IS_RESET = true;

        getFilterAdapter = new GetFilterAdapter(getActivity().getSupportFragmentManager());
        pager_view.setAdapter(getFilterAdapter);
        //getFilterAdapter.notifyDataSetChanged();
        if (ConnectivityUtils.isNetworkEnabled(getActivity())) {
            showProgressDialog(getString(R.string.fetching_data));

			/*if(tab_host.getTabWidget()!=null){
            tab_host.getTabWidget().removeAllViews();
		}*/
            if (type == Constants.BUSINESS_PAGE_TYPE) {

                Animation bottomUp = AnimationUtils.loadAnimation(getActivity(), R.anim.bottom_up);
                bottomUp.setAnimationListener(makeTopGone);
                //tab_host.setVisibility(View.GONE) ;
                tab_host.startAnimation(bottomUp);

            } else if (type == Constants.EVENT_PAGE_TYPE) {

                Animation bottomUp = AnimationUtils.loadAnimation(getActivity(), R.anim.bottom_up);
                bottomUp.setAnimationListener(makeTopGone);
                pager_view.startAnimation(bottomUp);
            }

            if (Constants.IS_SEARCH_LISTING) {
                hitBusinessSearchListingApi(querySearch, localitySearch, mPageCount);
            } else {
                hitBusinessListingApiSorted(categoryId, mPageCount, mFilterMap);
               /* BusinessListController businessListController = new BusinessListController(getActivity(), this);
                businessListController.getData(AppConstants.BUSINESS_LIST_REQUEST, businessListRequest);
                mIsComingFromFilter = true;*/
            }
            mIsRequestRunning = true;

        } else {
            ToastUtils.showToast(getActivity(), getString(R.string.netwrok_error));
        }
    }

    TextWatcher textWatcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

//            .d("check","onTextChanged");
            /**
             * this will call a query listing from api.
             */
            if (searchName.getText().hashCode() == s.hashCode()) {
                if (ConnectivityUtils.isNetworkEnabled(getActivity())) {
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
//                        Log.d("check","mAutoSuggestController "+mAutoSuggestController);
                        if (mAutoSuggestController != null)
                            mAutoSuggestController.setCanceled(true);
                        mAutoSuggestController.getData(AppConstants.AUTO_SUGGEST_REQUEST, s.toString());
                        //	}

                    } else {
                        mSearchList.setVisibility(View.GONE);
                        eventListView.setVisibility(View.VISIBLE);
                    }
                } else {
                    //showToast(getString(R.string.error_network));
                    return;
                }

                /**
                 * it will call locality list from local db.
                 */
            } else if (mLocalitySearchEtxt.getText().hashCode() == s.hashCode()) {
//                Log.d("check","locality");
                SubLocalityAdapter adapter = null;
                LocalityTable _localitiesTable = new LocalityTable((BaseApplication) getActivity().getApplicationContext());
                ArrayList<String> localitiesName = new ArrayList<String>();
                localitiesName.add("Near Me");
                ArrayList<String> localitiesNameDb = _localitiesTable.getLocalitiesName(s.toString().trim());
                if (localitiesNameDb != null && localitiesName.size() != 0) {
                    localitiesName.addAll(localitiesNameDb);
                }
                if (localitiesName.size() == 1) {
                    adapter = new SubLocalityAdapter(getActivity(), localitiesName);
                    mSearchList.setAdapter(adapter);
                }

                if (s != null && !(s.toString().equals(""))) {

                    if (!localitiesName.isEmpty()) {
                        if (adapter == null) {
                            adapter = new SubLocalityAdapter(getActivity(), localitiesName);
                        }
                        adapter.notifyDataSetChanged();
                        mSearchList.setVisibility(View.VISIBLE);
                        eventListView.setVisibility(View.GONE);
                        ((TextView) view.findViewById(R.id.txt_no_data_business)).setVisibility(View.GONE);
                        mSearchList.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                        mSearchList.setTag(Constants.LOCALITY_LIST_TAG);

                    } else {
                        mSearchList.setVisibility(View.GONE);
                        eventListView.setVisibility(View.VISIBLE);
                        // mLoutProgress.setVisibility(View.GONE);

                    }


                } else {
                    mSearchList.setVisibility(View.GONE);
                    eventListView.setVisibility(View.VISIBLE);
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

    public void hideSearchList() {
        mSearchList.setVisibility(View.GONE);
        searchName.setText("");
        mLocalitySearchEtxt.setText("");
        localityLayout.setVisibility(View.GONE);
        search_bar.setVisibility(View.GONE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (requestCode == businessAdapter.REQUEST_INIT_PERMISSION) {
            Log.i("Permissions", "Received response for storage permissions request.");

            if (PermissionUtil.verifyPermissions(grantResults)) {
                Snackbar.make(businessRoot, R.string.permision_available_init,
                        Snackbar.LENGTH_SHORT)
                        .show();
                businessAdapter.addCalendarEvent(businessAdapter.addCalendarPos);
            } else {
                Log.i("Permissions", "storage permissions were NOT granted.");
                Snackbar.make(businessRoot, R.string.permissions_not_granted,
                        Snackbar.LENGTH_SHORT)
                        .show();
            }

        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

}
