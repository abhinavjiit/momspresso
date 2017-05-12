package com.mycity4kids.ui.fragment;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.Request;
import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseFragment;
import com.kelltontech.utils.ConnectivityUtils;
import com.kelltontech.utils.DateTimeUtils;
import com.kelltontech.utils.StringUtils;
import com.kelltontech.utils.ToastUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.dbtable.TableAppointmentData;
import com.mycity4kids.dbtable.TableTaskData;
import com.mycity4kids.dbtable.TaskCompletedTable;
import com.mycity4kids.gtmutils.GTMEventType;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.interfaces.OnWebServiceCompleteListener;
import com.mycity4kids.models.Topics;
import com.mycity4kids.models.TopicsResponse;
import com.mycity4kids.models.businesslist.BusinessDataListing;
import com.mycity4kids.models.businesslist.BusinessListResponse;
import com.mycity4kids.models.response.ArticleListingResponse;
import com.mycity4kids.models.response.ArticleListingResult;
import com.mycity4kids.models.response.LanguageConfigModel;
import com.mycity4kids.models.response.NotificationCenterListResponse;
import com.mycity4kids.models.response.VlogsListingAndDetailResult;
import com.mycity4kids.models.response.VlogsListingResponse;
import com.mycity4kids.newmodels.AppointmentMappingModel;
import com.mycity4kids.newmodels.AttendeeModel;
import com.mycity4kids.newmodels.TaskMappingModel;
import com.mycity4kids.newmodels.VolleyBaseResponse;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.EventsAPI;
import com.mycity4kids.retrofitAPIsInterfaces.NotificationsAPI;
import com.mycity4kids.retrofitAPIsInterfaces.RecommendationAPI;
import com.mycity4kids.retrofitAPIsInterfaces.TopicsCategoryAPI;
import com.mycity4kids.retrofitAPIsInterfaces.VlogsListingAndDetailsAPI;
import com.mycity4kids.ui.activity.ActivityCreateAppointment;
import com.mycity4kids.ui.activity.ActivityShowAppointment;
import com.mycity4kids.ui.activity.BusinessDetailsActivity;
import com.mycity4kids.ui.activity.CreateFamilyActivity;
import com.mycity4kids.ui.activity.DashboardActivity;
import com.mycity4kids.ui.adapter.AdapterHomeAppointment;
import com.mycity4kids.ui.adapter.BusinessListingAdapterevent;
import com.mycity4kids.utils.AppUtils;
import com.mycity4kids.utils.ArrayAdapterFactory;
import com.mycity4kids.utils.PermissionUtil;
import com.mycity4kids.utils.location.GPSTracker;
import com.mycity4kids.volley.HttpVolleyRequest;
import com.mycity4kids.widget.CustomListView;
import com.mycity4kids.widget.HorizontalScrollCustomView;
import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by manish.soni on 17-06-2015.
 */
public class FragmentMC4KHome extends BaseFragment implements View.OnClickListener {

    private static final int REQUEST_INIT_PERMISSION = 1;
    private static String[] PERMISSIONS_INIT = {Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR};

    View view;
    CustomListView appointmentList;
    TableAppointmentData tableAppointment;
    ArrayList<AppointmentMappingModel> appointmentListData;
    AdapterHomeAppointment adapterHomeAppointment;
    TextView goToCal, current;
    ImageView imgGoToCal, imgGoToEvents;
    ImageView addAppointment;
    ScrollView baseScroll;
    private ProgressBar progressBar;
    private BusinessListingAdapterevent businessAdapter;
    private int mBusinessListCount = 1;
    private int mTotalPageCount = 0;
    private int mPageCount = 1;
    private int businessOrEventType;
    private int from = 1;
    private int to = 10;
    private String userId;
    private int eventPosition;

    private ArrayList<BusinessDataListing> mBusinessDataListings;
    private ArrayList<ArticleListingResult> mArticleDataListing;
    private ArrayList<ArticleListingResult> mArticleBestCityListing;
    private ArrayList<ArticleListingResult> mArticleForYouListing;
    private ArrayList<ArticleListingResult> mArticleEditorPicksListing;
    private ArrayList<ArticleListingResult> mMomspressoArticleListing;
    private ArrayList<VlogsListingAndDetailResult> funnyVideosListing;
    private ArrayList<ArticleListingResult> hindiArticlesListing;

    private CustomListView eventListView;
    private boolean mEventDataAvalble;
    TextView txtCal, txtEvents;
    private LayoutInflater mInflator;
    private LinearLayout hzScrollLinearLayoutEvent, momspressoHZScrollLinearLayout;
    CardView cardView;
    int sortType = 0;
    private LinearLayout momspressoHeader;
    private View mCustomView;
    private FrameLayout mCustomViewContainer;
    private WebChromeClient.CustomViewCallback mCustomViewCallback;
    private DrawerLayout mContentView;
    private MyWebChromeClient mWebChromeClient = null;
    ArrayList<WebView> videoIframe = new ArrayList<>();
    private ProgressBar momspressoProgressbar;
    private HorizontalScrollCustomView forYourSection, trendingSection, editorPicksSection, inYourCitySection, momspressoSection, funnyVideosSection, languageSection;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.aa_mc4k_home, container, false);
        Utils.pushOpenScreenEvent(getActivity(), "Dashboard Fragment", SharedPrefUtils.getUserDetailModel(getActivity()).getDynamoId() + "");
        mInflator = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        hzScrollLinearLayoutEvent = (LinearLayout) view.findViewById(R.id.hzScrollLinearLayoutEvent);
//        momspressoHZScrollLinearLayout = (LinearLayout) view.findViewById(R.id.momspressoHZScrollLinearLayout);
        forYourSection = (HorizontalScrollCustomView) view.findViewById(R.id.forYouSection);
        trendingSection = (HorizontalScrollCustomView) view.findViewById(R.id.trendingSection);
        editorPicksSection = (HorizontalScrollCustomView) view.findViewById(R.id.editorPicksSection);
        inYourCitySection = (HorizontalScrollCustomView) view.findViewById(R.id.inYourCitySection);
        momspressoSection = (HorizontalScrollCustomView) view.findViewById(R.id.momspressoSection);
        funnyVideosSection = (HorizontalScrollCustomView) view.findViewById(R.id.funnyVideosSection);
        languageSection = (HorizontalScrollCustomView) view.findViewById(R.id.languageSection);

        forYourSection.setSectionTitle(getString(R.string.home_sections_title_for_you));
        trendingSection.setSectionTitle(getString(R.string.home_sections_title_trending));
        editorPicksSection.setSectionTitle(getString(R.string.home_sections_title_editors_pick));
        momspressoSection.setSectionTitle(getString(R.string.home_sections_title_momspresso));
        funnyVideosSection.setSectionTitle(getString(R.string.home_sections_title_funny_videos));
        languageSection.setMultipleSectionsTitle();

        appointmentList = (CustomListView) view.findViewById(R.id.home_appointmentList);
        goToCal = (TextView) view.findViewById(R.id.go_to_cal);
        addAppointment = (ImageView) view.findViewById(R.id.add_appointment);
        current = (TextView) view.findViewById(R.id.current_date);
        baseScroll = (ScrollView) view.findViewById(R.id.base_scroll);
        imgGoToCal = (ImageView) view.findViewById(R.id.img_go_to_cal);
        imgGoToEvents = (ImageView) view.findViewById(R.id.img_go_to_events);

        txtCal = (TextView) view.findViewById(R.id.txtCal);
        txtEvents = (TextView) view.findViewById(R.id.txtEvents);
        progressBar = (ProgressBar) view.findViewById(R.id.eventprogressbar);
        eventListView = (CustomListView) view.findViewById(R.id.eventList);

        userId = SharedPrefUtils.getUserDetailModel(getActivity()).getDynamoId();

        if (SharedPrefUtils.getCurrentCityModel(getActivity()).getName().isEmpty()) {
            inYourCitySection.setCityNameFromCityId(SharedPrefUtils.getCurrentCityModel(getActivity()).getId());
        } else {
            inYourCitySection.setCityName(SharedPrefUtils.getCurrentCityModel(getActivity()).getName().toUpperCase());
        }
        goToCal.setOnClickListener(this);
        addAppointment.setOnClickListener(this);
        imgGoToCal.setOnClickListener(this);
        imgGoToEvents.setOnClickListener(this);
        txtCal.setOnClickListener(this);
        txtEvents.setOnClickListener(this);
//        momspressoHeader.setOnClickListener(this);

        view.findViewById(R.id.go_to_events).setOnClickListener(this);
        view.findViewById(R.id.no_events).setOnClickListener(this);

        TableAppointmentData tAppointment = new TableAppointmentData(BaseApplication.getInstance());
        List<AppointmentMappingModel> apptList = tAppointment.getAll();
        if (null != apptList && apptList.size() == 0) {
            goToCal.setText("ADD AN APPOINTMENT");
            goToCal.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (StringUtils.isNullOrEmpty("" + SharedPrefUtils.getUserDetailModel(getActivity()).getFamily_id()) ||
                            SharedPrefUtils.getUserDetailModel(getActivity()).getFamily_id() == 0) {
                        showCreateFamilyAlert();
                    } else {
                        Intent appointmentIntent = new Intent(getActivity(), ActivityCreateAppointment.class);
                        startActivity(appointmentIntent);
                    }
                }
            });
        }

        appointmentList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                AppointmentMappingModel appointmentSelected = (AppointmentMappingModel) adapterHomeAppointment.getItem(i);
                if (appointmentSelected.getAppointment_name() != null) {
                    Intent intent = new Intent(getActivity(), ActivityShowAppointment.class);
                    if (appointmentSelected.getEventId() == 0) {
                        intent.putExtra(AppConstants.EXTRA_APPOINTMENT_ID, 0);
                        intent.putExtra(AppConstants.EXTERNAL_APPOINTMENT_ID, appointmentSelected.getExternalId());
                    } else {
                        intent.putExtra(AppConstants.EXTRA_APPOINTMENT_ID, appointmentSelected.getEventId());
                        intent.putExtra(AppConstants.EXTERNAL_APPOINTMENT_ID, "");
                    }
                    startActivityForResult(intent, 1);
                }
            }
        });
        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat form = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        form.setTimeZone(TimeZone.getTimeZone("GMT+0530"));
        SimpleDateFormat currentForm = new SimpleDateFormat("dd", Locale.US);
        currentForm.setTimeZone(TimeZone.getTimeZone("GMT+0530"));
        String currentDate = form.format(calendar.getTime());
        current.setText(currentForm.format(calendar.getTime()).toString());
        mArticleDataListing = new ArrayList<>();
        appointmentListData = new ArrayList<>();
        mArticleBestCityListing = new ArrayList<>();
        mMomspressoArticleListing = new ArrayList<>();
        mArticleForYouListing = new ArrayList<>();
        mArticleEditorPicksListing = new ArrayList<>();
        funnyVideosListing = new ArrayList<>();
        hindiArticlesListing = new ArrayList<>();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        formatter.setTimeZone(TimeZone.getTimeZone("GMT+0530"));
        tableAppointment = new TableAppointmentData(BaseApplication.getInstance());

        try {
            appointmentListData = getAppointmentByDay(currentDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        appointmentListData = getSorted(formatter.format(calendar.getTime()), appointmentListData);
        adapterHomeAppointment = new AdapterHomeAppointment(getActivity(), appointmentListData);
        appointmentList.setAdapter(adapterHomeAppointment);

//        String momspressoCategoryId = getMomspressoCategory();

        hitForYouListingApi();
        hitBlogListingApi();
        hitMomspressoListingApi(AppConstants.MOMSPRESSO_CATEGORYID);
        hitEditorPicksListingApi();
        hitHindiArticlesListing();
        hitInYourCityListingApi();
        hitFunnyVideosListingApi();
        updateUnreadNotificationCount();
        businessAdapter = new BusinessListingAdapterevent(getActivity());
        mBusinessDataListings = new ArrayList<>();
        eventListView.setAdapter(businessAdapter);
        if (!SharedPrefUtils.isCityFetched(getActivity()) || SharedPrefUtils.getCurrentCityModel(getActivity()).getId() == AppConstants.OTHERS_CITY_ID) {
            view.findViewById(R.id.eventsss).setVisibility(View.GONE);
            inYourCitySection.setVisibility(View.GONE);
        } else {
            inYourCitySection.setVisibility(View.VISIBLE);
            view.findViewById(R.id.eventsss).setVisibility(View.VISIBLE);
            if (BaseApplication.getBusinessREsponse() == null || BaseApplication.getBusinessREsponse().isEmpty()) {
                hitBusinessListingApiRetro(SharedPrefUtils.getEventIdForCity(getActivity()), 1);
            } else {
                mEventDataAvalble = true;
                mBusinessDataListings.addAll(BaseApplication.getBusinessREsponse());
                inflateEventCardsScroll();
            }
        }
    }

    private void hitForYouListingApi() {
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        RecommendationAPI foryouAPI = retrofit.create(RecommendationAPI.class);

        Call<ArticleListingResponse> filterCall = foryouAPI.getRecommendedArticlesList("" + userId, 10, "", SharedPrefUtils.getLanguageFilters(getActivity()));
        filterCall.enqueue(forYouResponseCallback);
    }

    public void hitBlogListingApi() {
        trendingSection.setProgressBarVisibility(View.VISIBLE);
        String url;
        url = AppConstants.LIVE_URL + "v1/articles/trending/" + from + "/" + to + "?lang=" + SharedPrefUtils.getLanguageFilters(getActivity());
        HttpVolleyRequest.getStringResponse(getActivity(), url, null, mGetArticleListingListener, Request.Method.GET, false);

    }

    private void hitMomspressoListingApi(String momspressoCategoryId) {
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        TopicsCategoryAPI topicsAPI = retrofit.create(TopicsCategoryAPI.class);
        if (momspressoCategoryId == null) {
            momspressoCategoryId = AppConstants.MOMSPRESSO_CATEGORYID;
        }
        Call<ArticleListingResponse> filterCall = topicsAPI.getArticlesForCategory(momspressoCategoryId, 0, 1, 10, SharedPrefUtils.getLanguageFilters(getActivity()));
        filterCall.enqueue(momspressoListingResponseCallback);
    }

    private void hitFunnyVideosListingApi() {
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        VlogsListingAndDetailsAPI vlogsListingAndDetailsAPI = retrofit.create(VlogsListingAndDetailsAPI.class);
        Call<VlogsListingResponse> callRecentVideoArticles = vlogsListingAndDetailsAPI.getVlogsList(0, 9, 0, 3);
        callRecentVideoArticles.enqueue(funnyVideosResponseCallback);
    }

    private void hitEditorPicksListingApi() {
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        TopicsCategoryAPI topicsAPI = retrofit.create(TopicsCategoryAPI.class);
        int end = AppUtils.randInt(AppConstants.EDITOR_PICKS_MIN_ARTICLES + 1, AppConstants.EDITOR_PICKS_ARTICLE_COUNT + 1);
        int start = end - AppConstants.EDITOR_PICKS_MIN_ARTICLES;
        Call<ArticleListingResponse> filterCall = topicsAPI.getArticlesForCategory(AppConstants.EDITOR_PICKS_CATEGORY_ID, 0, start, end, SharedPrefUtils.getLanguageFilters(getActivity()));
        filterCall.enqueue(editorPicksResponseCallback);
    }

    private void hitHindiArticlesListing() {
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        TopicsCategoryAPI topicsAPI = retrofit.create(TopicsCategoryAPI.class);
        LanguageConfigModel hindiLangModel = AppUtils.getLangModelForLanguage(getActivity(), AppConstants.LANG_KEY_HINDI);
        if (null != hindiLangModel && !StringUtils.isNullOrEmpty(hindiLangModel.getId())) {
            Call<ArticleListingResponse> filterCall = topicsAPI.getArticlesForCategory(hindiLangModel.getId(), 0, 1, 10, "");
            filterCall.enqueue(hindiArticlesListingResponseCallback);
        } else {
            languageSection.setVisibility(View.GONE);
        }
    }

    public void hitInYourCityListingApi() {
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        TopicsCategoryAPI topicsAPI = retrofit.create(TopicsCategoryAPI.class);

        Call<ArticleListingResponse> filterCall = topicsAPI.getBestArticlesForCity("" + SharedPrefUtils.getCurrentCityModel(getActivity()).getId(), sortType, 1, 10, SharedPrefUtils.getLanguageFilters(getActivity()));
        filterCall.enqueue(inYourCityListingResponseCallback);
    }

    private Callback<ArticleListingResponse> forYouResponseCallback = new Callback<ArticleListingResponse>() {
        @Override
        public void onResponse(Call<ArticleListingResponse> call, retrofit2.Response<ArticleListingResponse> response) {
            forYourSection.setProgressBarVisibility(View.GONE);
            if (response == null || response.body() == null) {
                ((DashboardActivity) getActivity()).showToast("Something went wrong from server");
                return;
            }

            try {
                ArticleListingResponse responseData = (ArticleListingResponse) response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    processForYouResponse(responseData);
                } else {
                    ((DashboardActivity) getActivity()).showToast(getString(R.string.went_wrong));
                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4KException", Log.getStackTraceString(e));
                ((DashboardActivity) getActivity()).showToast(getString(R.string.went_wrong));
            }
        }

        @Override
        public void onFailure(Call<ArticleListingResponse> call, Throwable t) {
            forYourSection.setProgressBarVisibility(View.GONE);
            Crashlytics.logException(t);
            Log.d("MC4KException", Log.getStackTraceString(t));
            if (null != getActivity()) {
                ((DashboardActivity) getActivity()).showToast(getString(R.string.went_wrong));
            }
        }
    };

    private OnWebServiceCompleteListener mGetArticleListingListener = new OnWebServiceCompleteListener() {
        @Override
        public void onWebServiceComplete(VolleyBaseResponse response, boolean isError) {
//            blogProgessBar.setVisibility(View.GONE);
            trendingSection.setProgressBarVisibility(View.GONE);
            Log.d("Response back =", " " + response.getResponseBody());
            if (isError) {
                if (null != getActivity() && response.getResponseCode() != 999)
                    ((DashboardActivity) getActivity()).showToast("Something went wrong from server");
            } else {
                Log.d("Response = ", response.getResponseBody());
                String temp = "";
                if (response == null) {
                    ((DashboardActivity) getActivity()).showToast("Something went wrong from server");
                    removeProgressDialog();
                    return;
                }

                ArticleListingResponse responseBlogData;
                try {
                    Gson gson = new GsonBuilder().registerTypeAdapterFactory(new ArrayAdapterFactory()).create();
                    responseBlogData = gson.fromJson(response.getResponseBody(), ArticleListingResponse.class);
                } catch (JsonSyntaxException jse) {
                    Crashlytics.logException(jse);
                    Log.d("JsonSyntaxException", Log.getStackTraceString(jse));
                    ((DashboardActivity) getActivity()).showToast("Something went wrong from server");
                    removeProgressDialog();
                    return;
                }

                if (responseBlogData.getCode() == Constants.HTTP_RESPONSE_SUCCESS) {
                    //clear list to avoid duplicates due to volley caching
                    mArticleDataListing.clear();
                    mArticleDataListing.addAll(responseBlogData.getData().get(0).getResult());
                    trendingSection.setmDatalist(mArticleDataListing, Constants.KEY_TRENDING, "Home Screen");
                } else {
                    trendingSection.setEmptyListLabelVisibility(View.VISIBLE);
                }
            }
        }
    };

    private Callback<ArticleListingResponse> momspressoListingResponseCallback = new Callback<ArticleListingResponse>() {
        @Override
        public void onResponse(Call<ArticleListingResponse> call, retrofit2.Response<ArticleListingResponse> response) {
//            momspressoProgressbar.setVisibility(View.GONE);
            if (response == null || response.body() == null) {
                ((DashboardActivity) getActivity()).showToast("Something went wrong from server");
                return;
            }

            try {
                ArticleListingResponse responseData = (ArticleListingResponse) response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    processMomspressoListingResponse(responseData);
                } else {
                    ((DashboardActivity) getActivity()).showToast(getString(R.string.went_wrong));
                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4KException", Log.getStackTraceString(e));
                ((DashboardActivity) getActivity()).showToast(getString(R.string.went_wrong));
            }
        }

        @Override
        public void onFailure(Call<ArticleListingResponse> call, Throwable t) {
//            momspressoProgressbar.setVisibility(View.GONE);
            Crashlytics.logException(t);
            Log.d("MC4KException", Log.getStackTraceString(t));
            if (null != getActivity()) {
                ((DashboardActivity) getActivity()).showToast(getString(R.string.went_wrong));
            }
        }
    };

    private Callback<ArticleListingResponse> hindiArticlesListingResponseCallback = new Callback<ArticleListingResponse>() {
        @Override
        public void onResponse(Call<ArticleListingResponse> call, retrofit2.Response<ArticleListingResponse> response) {
//            momspressoProgressbar.setVisibility(View.GONE);
            if (response == null || response.body() == null) {
                ((DashboardActivity) getActivity()).showToast("Something went wrong from server");
                return;
            }

            try {
                ArticleListingResponse responseData = (ArticleListingResponse) response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    processHindiArticlesListingResponse(responseData);
                } else {
                    ((DashboardActivity) getActivity()).showToast(getString(R.string.went_wrong));
                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4KException", Log.getStackTraceString(e));
                ((DashboardActivity) getActivity()).showToast(getString(R.string.went_wrong));
            }
        }

        @Override
        public void onFailure(Call<ArticleListingResponse> call, Throwable t) {
//            momspressoProgressbar.setVisibility(View.GONE);
            Crashlytics.logException(t);
            Log.d("MC4KException", Log.getStackTraceString(t));
            if (null != getActivity()) {
                ((DashboardActivity) getActivity()).showToast(getString(R.string.went_wrong));
            }
        }
    };

    private Callback<VlogsListingResponse> funnyVideosResponseCallback = new Callback<VlogsListingResponse>() {
        @Override
        public void onResponse(Call<VlogsListingResponse> call, retrofit2.Response<VlogsListingResponse> response) {
            if (response == null || response.body() == null) {
                ((DashboardActivity) getActivity()).showToast("Something went wrong from server");
                return;
            }

            try {
                VlogsListingResponse responseData = (VlogsListingResponse) response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    processFunnyVideosResponse(responseData);
                } else {
                    ((DashboardActivity) getActivity()).showToast(getString(R.string.went_wrong));
                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4KException", Log.getStackTraceString(e));
                ((DashboardActivity) getActivity()).showToast(getString(R.string.went_wrong));
            }
        }

        @Override
        public void onFailure(Call<VlogsListingResponse> call, Throwable t) {
            Crashlytics.logException(t);
            Log.d("MC4KException", Log.getStackTraceString(t));
            if (null != getActivity()) {
                ((DashboardActivity) getActivity()).showToast(getString(R.string.went_wrong));
            }
        }
    };

    private Callback<ArticleListingResponse> editorPicksResponseCallback = new Callback<ArticleListingResponse>() {
        @Override
        public void onResponse(Call<ArticleListingResponse> call, retrofit2.Response<ArticleListingResponse> response) {
            editorPicksSection.setProgressBarVisibility(View.GONE);
            if (response == null || response.body() == null) {
                ((DashboardActivity) getActivity()).showToast("Something went wrong from server");
                return;
            }

            try {
                ArticleListingResponse responseData = (ArticleListingResponse) response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    processEditorPicksResponse(responseData);
                } else {
                    ((DashboardActivity) getActivity()).showToast(getString(R.string.went_wrong));
                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4KException", Log.getStackTraceString(e));
                ((DashboardActivity) getActivity()).showToast(getString(R.string.went_wrong));
            }
        }

        @Override
        public void onFailure(Call<ArticleListingResponse> call, Throwable t) {
//            forYouProgressbar.setVisibility(View.GONE);
            editorPicksSection.setProgressBarVisibility(View.GONE);
            Crashlytics.logException(t);
            Log.d("MC4KException", Log.getStackTraceString(t));
            if (null != getActivity()) {
                ((DashboardActivity) getActivity()).showToast(getString(R.string.went_wrong));
            }
        }
    };

    private Callback<ArticleListingResponse> inYourCityListingResponseCallback = new Callback<ArticleListingResponse>() {
        @Override
        public void onResponse(Call<ArticleListingResponse> call, retrofit2.Response<ArticleListingResponse> response) {
            inYourCitySection.setProgressBarVisibility(View.GONE);
            if (response == null || response.body() == null) {
                ((DashboardActivity) getActivity()).showToast("Something went wrong from server");
                return;
            }

            try {
                ArticleListingResponse responseData = (ArticleListingResponse) response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    processInYourCityListingResponse(responseData);
                } else {
                    ((DashboardActivity) getActivity()).showToast(getString(R.string.went_wrong));
                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4KException", Log.getStackTraceString(e));
                if (getActivity() != null && isAdded()) {
                    ((DashboardActivity) getActivity()).showToast(getString(R.string.went_wrong));
                }
            }
        }

        @Override
        public void onFailure(Call<ArticleListingResponse> call, Throwable t) {
            inYourCitySection.setProgressBarVisibility(View.GONE);
            Crashlytics.logException(t);
            Log.d("MC4KException", Log.getStackTraceString(t));
            if (null != getActivity()) {
                ((DashboardActivity) getActivity()).showToast(getString(R.string.went_wrong));
            }
        }
    };

    private void processForYouResponse(ArticleListingResponse responseData) {
        ArrayList<ArticleListingResult> dataList = responseData.getData().get(0).getResult();
        if (dataList.size() == 0) {
            forYourSection.setEmptyListLabelVisibility(View.VISIBLE);
        } else {
            mArticleForYouListing.clear();
            for (int i = 0; i < responseData.getData().get(0).getResult().size(); i++) {
                if (!StringUtils.isNullOrEmpty(responseData.getData().get(0).getResult().get(i).getId())) {
                    mArticleForYouListing.add(responseData.getData().get(0).getResult().get(i));
                }
            }
//            mArticleForYouListing.addAll(responseData.getData().get(0).getResult());
            forYourSection.setmDatalist(mArticleForYouListing, Constants.KEY_FOR_YOU, "Home Screen");
        }
    }

    private void processMomspressoListingResponse(ArticleListingResponse responseData) {
        ArrayList<ArticleListingResult> dataList = responseData.getData().get(0).getResult();
        if (dataList.size() == 0) {
            momspressoSection.setEmptyListLabelVisibility(View.VISIBLE);
        } else {
            mMomspressoArticleListing.clear();
            mMomspressoArticleListing.addAll(responseData.getData().get(0).getResult());
            momspressoSection.setmDatalist(mMomspressoArticleListing, Constants.KEY_MOMSPRESSO, "Home Screen");
        }
    }

    private void processHindiArticlesListingResponse(ArticleListingResponse responseData) {
        ArrayList<ArticleListingResult> dataList = responseData.getData().get(0).getResult();
        if (dataList.size() == 0) {
            languageSection.setEmptyListLabelVisibility(View.VISIBLE);
        } else {
            hindiArticlesListing.clear();
            hindiArticlesListing.addAll(responseData.getData().get(0).getResult());
            languageSection.setmDatalist(hindiArticlesListing, Constants.KEY_HINDI, "Home Screen");
        }
    }

    private void processFunnyVideosResponse(VlogsListingResponse responseData) {
        ArrayList<VlogsListingAndDetailResult> dataList = responseData.getData().get(0).getResult();
        if (dataList.size() == 0) {
            funnyVideosSection.setEmptyListLabelVisibility(View.VISIBLE);
        } else {
            funnyVideosListing.clear();
            funnyVideosListing.addAll(responseData.getData().get(0).getResult());
            funnyVideosSection.setVlogslist(funnyVideosListing, "dashboard", "Home Screen");
        }
    }

    private void processEditorPicksResponse(ArticleListingResponse responseData) {
        ArrayList<ArticleListingResult> dataList = responseData.getData().get(0).getResult();
        if (dataList.size() == 0) {
            editorPicksSection.setEmptyListLabelVisibility(View.VISIBLE);
        } else {
            mArticleEditorPicksListing.clear();
            mArticleEditorPicksListing.addAll(responseData.getData().get(0).getResult());
            editorPicksSection.setmDatalist(mArticleEditorPicksListing, Constants.KEY_EDITOR_PICKS, "Home Screen");
        }
    }

    private void processInYourCityListingResponse(ArticleListingResponse responseData) {
        ArrayList<ArticleListingResult> dataList = responseData.getData().get(0).getResult();
        if (dataList.size() == 0) {
            inYourCitySection.setEmptyListLabelVisibility(View.VISIBLE);

        } else {
            inYourCitySection.setEmptyListLabelVisibility(View.GONE);
            mArticleBestCityListing.clear();
            mArticleBestCityListing.addAll(responseData.getData().get(0).getResult());
            inYourCitySection.setmDatalist(mArticleBestCityListing, Constants.KEY_IN_YOUR_CITY + "~" + SharedPrefUtils.getCurrentCityModel(getActivity()).getName(), "Home Screen");
        }
    }

    public static String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            reader.close();
        } catch (IOException e) {
            Crashlytics.logException(e);
            Log.d("IOException", Log.getStackTraceString(e));
        }
        return sb.toString();
    }

    public int getAge(Date dateOfBirth) {
        float age = 0;

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

        return (int) age;
    }

    private void hitBusinessListingApiRetro(int categoryId, int page) {
        if (!ConnectivityUtils.isNetworkEnabled(getActivity())) {
            return;
        }

        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        EventsAPI topicsAPI = retrofit.create(EventsAPI.class);
        GPSTracker getCurrentLocation = new GPSTracker(getActivity());
        double _latitude = getCurrentLocation.getLatitude();
        double _longitude = getCurrentLocation.getLongitude();

        Call<BusinessListResponse> filterCall = topicsAPI.getEventList("" + (SharedPrefUtils.getCurrentCityModel(getActivity())).getId(), "" + categoryId,
                "" + _latitude, "" + _longitude, "", SharedPrefUtils.getUserDetailModel(getActivity()).getId(), 1);
        filterCall.enqueue(eventListingResponseCallback);
    }

    private Callback<BusinessListResponse> eventListingResponseCallback = new Callback<BusinessListResponse>() {
        @Override
        public void onResponse(Call<BusinessListResponse> call, retrofit2.Response<BusinessListResponse> response) {
//            momspressoProgressbar.setVisibility(View.GONE);
            if (response == null || response.body() == null) {
                ((DashboardActivity) getActivity()).showToast("Something went wrong from server");
                return;
            }

            try {
                progressBar.setVisibility(View.GONE);
                BusinessListResponse responseData = (BusinessListResponse) response.body();
                if (responseData.getResponseCode() == Constants.HTTP_RESPONSE_SUCCESS) {

                    mBusinessListCount = responseData.getResult().getData().getTotal();
                    mTotalPageCount = responseData.getResult().getData().getPage_count();
                    //to add in already created list
                    // we neew to clear this list in case of sort by and filter
                    mBusinessDataListings.addAll(responseData.getResult().getData().getData());

                    BaseApplication.setBusinessREsponse(mBusinessDataListings);

                    businessAdapter.setListData(mBusinessDataListings, businessOrEventType);

                    businessAdapter.notifyDataSetChanged();
                    inflateEventCardsScroll();

                    if (mBusinessDataListings.isEmpty()) {
                        ((TextView) view.findViewById(R.id.no_events)).setVisibility(View.VISIBLE);
                    }
                    baseScroll.smoothScrollTo(0, 0);

                } else if (responseData.getResponseCode() == 400) {
                    ((TextView) view.findViewById(R.id.no_events)).setVisibility(View.VISIBLE);
                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4KException", Log.getStackTraceString(e));
                ((DashboardActivity) getActivity()).showToast(getString(R.string.went_wrong));
            }
        }

        @Override
        public void onFailure(Call<BusinessListResponse> call, Throwable t) {
//            momspressoProgressbar.setVisibility(View.GONE);
            Crashlytics.logException(t);
            Log.d("MC4KException", Log.getStackTraceString(t));
            if (null != getActivity()) {
                ((DashboardActivity) getActivity()).showToast(getString(R.string.went_wrong));
            }
        }
    };

    @Override
    protected void updateUi(Response response) {

        if (response == null) {
            progressBar.setVisibility(View.GONE);
            return;
        }

        switch (response.getDataType()) {
            case AppConstants.BUSINESS_LIST_REQUEST:
                break;
            case AppConstants.ARTICLES_TODAY_REQUEST:
                break;
        }
    }

    @Override
    public void onClick(View view) {

        Intent intent;

        switch (view.getId()) {
            case R.id.go_to_cal:
            case R.id.img_go_to_cal:
            case R.id.txtCal:
                if (StringUtils.isNullOrEmpty("" + SharedPrefUtils.getUserDetailModel(getActivity()).getFamily_id()) ||
                        SharedPrefUtils.getUserDetailModel(getActivity()).getFamily_id() == 0) {
                    showCreateFamilyAlert();
                } else {
                    ((DashboardActivity) getActivity()).replaceFragment(new FragmentCalender(), null, true);
                }
                break;

            case R.id.add_appointment:
                if (StringUtils.isNullOrEmpty("" + SharedPrefUtils.getUserDetailModel(getActivity()).getFamily_id()) ||
                        SharedPrefUtils.getUserDetailModel(getActivity()).getFamily_id() == 0) {
                    showCreateFamilyAlert();
                } else {
                    intent = new Intent(getActivity(), ActivityCreateAppointment.class);
                    startActivity(intent);
                }
                break;

            case R.id.go_to_events:
            case R.id.img_go_to_events:
            case R.id.txtEvents:

                Constants.IS_SEARCH_LISTING = false;
                FragmentBusinesslistEvents fragment = new FragmentBusinesslistEvents();
                Bundle bundle = new Bundle();
                bundle.putInt(Constants.PAGE_TYPE, Constants.EVENT_PAGE_TYPE);
                bundle.putInt(Constants.EXTRA_CATEGORY_ID, SharedPrefUtils.getEventIdForCity(getActivity()));
                bundle.putString(Constants.CATEGOTY_NAME, "Events & workshop");
                fragment.setArguments(bundle);
                ((DashboardActivity) getActivity()).replaceFragment(fragment, bundle, true);
                break;
//            case R.id.momspressoHeader:
//                Intent momspressoIntent = new Intent(getActivity(), FilteredTopicsArticleListingActivity.class);
//                momspressoIntent.putExtra("selectedTopics", SharedPrefUtils.getMomspressoCategory(getActivity()).getId());
//                momspressoIntent.putExtra("displayName", SharedPrefUtils.getMomspressoCategory(getActivity()).getDisplay_name());
//                startActivity(momspressoIntent);
//                break;
        }

    }

    private String getTime(long timeStampStr) {

        try {
            DateFormat sdf = new SimpleDateFormat("h:mma");
            Date netDate = (new Date(timeStampStr));
            return sdf.format(netDate).toUpperCase();
        } catch (Exception ex) {
            return "xx";
        }
    }

    public long convertTimeStamp(CharSequence date, CharSequence time) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd h:mma");

        String temp = date + " " + time;
        Date tempDate = formatter.parse(temp);

        java.sql.Timestamp timestamp = new java.sql.Timestamp(tempDate.getTime());

        Log.d("TimeStamp", String.valueOf(timestamp.getTime()));

        long time_stamp = timestamp.getTime();
        return time_stamp;
    }


    public ArrayList<AppointmentMappingModel> getSorted(String date, ArrayList<AppointmentMappingModel> dataList) {
        for (int i = 0; i < dataList.size(); i++) {
            try {
                dataList.get(i).setTemptime(convertTimeStamp(date, getTime(dataList.get(i).getStarttime())));
            } catch (Exception e) {
                e.getMessage();
            }
        }

        // now sorted by timeastamp
        AppointmentMappingModel swapModel;
        for (int i = 0; i < dataList.size(); i++) {
            for (int j = i + 1; j < dataList.size(); j++) {
                if (dataList.get(i).getTemptime() > dataList.get(j).getTemptime()) {
                    swapModel = dataList.get(i);
                    dataList.set(i, dataList.get(j));
                    dataList.set(j, swapModel);
                }
            }
        }

        return dataList;

    }

    public void refreshList() throws ParseException {
        if (SharedPrefUtils.isChangeCity(getActivity()) && SharedPrefUtils.getCurrentCityModel(getActivity()).getId() != AppConstants.OTHERS_CITY_ID) {
            inYourCitySection.setVisibility(View.VISIBLE);
            view.findViewById(R.id.eventsss).setVisibility(View.VISIBLE);
            hitInYourCityListingApi();
            inYourCitySection.setCityName(SharedPrefUtils.getCurrentCityModel(getActivity()).getName().toUpperCase());
            SharedPrefUtils.setChangeCityFlag(getActivity(), false);
            if (mBusinessDataListings == null) {
                mBusinessDataListings = new ArrayList<>();
            }
            mBusinessDataListings.clear();
            BaseApplication.setBusinessREsponse(mBusinessDataListings);
            businessAdapter.setListData(mBusinessDataListings, businessOrEventType);
            businessAdapter.notifyDataSetChanged();
            hzScrollLinearLayoutEvent.removeAllViews();
            hitBusinessListingApiRetro(SharedPrefUtils.getEventIdForCity(getActivity()), 1);
        } else if (SharedPrefUtils.getCurrentCityModel(getActivity()).getId() == AppConstants.OTHERS_CITY_ID) {
            inYourCitySection.setVisibility(View.GONE);
            view.findViewById(R.id.eventsss).setVisibility(View.GONE);
        }

        if (BaseApplication.isHasLanguagePreferrenceChanged()) {
            hitForYouListingApi();
            hitBlogListingApi();
            hitEditorPicksListingApi();
            hitInYourCityListingApi();
            BaseApplication.setHasLanguagePreferrenceChanged(false);
        }

        updateUnreadNotificationCount();
        Calendar calendar = Calendar.getInstance();
        tableAppointment = new TableAppointmentData(BaseApplication.getInstance());
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        try {
            appointmentListData = getAppointmentByDay(formatter.format(calendar.getTime()));
            // first change according to time
            appointmentListData = getSorted(formatter.format(calendar.getTime()), appointmentListData);
            adapterHomeAppointment.notifyList(appointmentListData);
            businessAdapter.refreshEventIdList();
            businessAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateUnreadNotificationCount() {
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        NotificationsAPI notificationsAPI = retrofit.create(NotificationsAPI.class);

        Call<NotificationCenterListResponse> filterCall = notificationsAPI.getUnreadNotificationCount(userId);
        filterCall.enqueue(unreadNotificationCountResponseCallback);
    }


    public ArrayList<AppointmentMappingModel> getAppointmentByDay(String Date) throws ParseException {

        ArrayList<AppointmentMappingModel> appointmentModels;
        ArrayList<AppointmentMappingModel> tempAppointmentModels;
        LinkedHashMap<String, ArrayList<AppointmentMappingModel>> finaldata = new LinkedHashMap<String, ArrayList<AppointmentMappingModel>>();

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        String f11 = Date + " 12:01 AM";
        String f22 = Date + " 11:59 PM";

        TableAppointmentData tableAppointment = new TableAppointmentData(BaseApplication.getInstance());
        appointmentModels = tableAppointment.allDataBTWNdaysHome(convertTimeStamp_new(f11), convertTimeStamp_new(f22));

//        ?

        ArrayList<AttendeeModel> nullList = new ArrayList<AttendeeModel>();
        tempAppointmentModels = new ArrayList<>();


        try {

            for (int j = 0; j < appointmentModels.size(); j++) {

                if (checkCurrentDateValid(Date, appointmentModels.get(j).getStarttime())) {


                    java.sql.Timestamp tempTimestamp = new java.sql.Timestamp(formatter.parse(Date).getTime());
                    if (appointmentModels.get(j).getStarttime() >= tempTimestamp.getTime() && appointmentModels.get(j).getStarttime() <= (tempTimestamp.getTime() + 86280000)) {
                        tempAppointmentModels.add(appointmentModels.get(j));

                    } else {
                        // condition until check pick date

                        if (!StringUtils.isNullOrEmpty(appointmentModels.get(j).getRepeate_untill())) {
                            if (appointmentModels.get(j).getRepeate_untill().equalsIgnoreCase("forever")) {

                                if (appointmentModels.get(j).getRepeat().equalsIgnoreCase("No Repeat")) {

                                } else if (appointmentModels.get(j).getRepeat().equalsIgnoreCase("Days")) {

                                    String[] daysArray = appointmentModels.get(j).getRepeate_frequency().split(",");
                                    for (String day : daysArray) {
                                        boolean result = chkDays(day, Date);
                                        if (result)
                                            tempAppointmentModels.add(appointmentModels.get(j));
                                    }

                                } else if (appointmentModels.get(j).getRepeat().equalsIgnoreCase("Daily")) {
                                    tempAppointmentModels.add(appointmentModels.get(j));
                                } else if (appointmentModels.get(j).getRepeat().equalsIgnoreCase("Weekly")) {
                                    // check start date
                                    long starttime = appointmentModels.get(j).getStarttime();
                                    boolean result = getValues(starttime, "weekly", Date);
                                    if (result)
                                        tempAppointmentModels.add(appointmentModels.get(j));

                                } else if (appointmentModels.get(j).getRepeat().equalsIgnoreCase("Monthly")) {

                                    long starttime = appointmentModels.get(j).getStarttime();
                                    boolean result = getValues(starttime, "monthly", Date);
                                    if (result)
                                        tempAppointmentModels.add(appointmentModels.get(j));

                                } else if (appointmentModels.get(j).getRepeat().equalsIgnoreCase("Yearly")) {

                                    long starttime = appointmentModels.get(j).getStarttime();
                                    boolean result = getValues(starttime, "yearly", Date);
                                    if (result)
                                        tempAppointmentModels.add(appointmentModels.get(j));

                                } else if (appointmentModels.get(j).getRepeat().equalsIgnoreCase("Other")) {

                                    long starttime = appointmentModels.get(j).getStarttime();
                                    boolean result = getOtherValues(starttime, appointmentModels.get(j).getRepeate_frequency(), Date, Integer.parseInt(appointmentModels.get(j).getRepeate_num()));
                                    if (result)
                                        tempAppointmentModels.add(appointmentModels.get(j));
                                }


                            } else {
                                String pickdate = appointmentModels.get(j).getRepeate_untill();
                                String currentdate = Date;

                                SimpleDateFormat f1 = new SimpleDateFormat("yyyy-MM-dd");
                                SimpleDateFormat f2 = new SimpleDateFormat("dd MMMM yyyy");

                                Date dateCurrent = new Date();
                                dateCurrent = (Date) f1.parse(currentdate);

                                Date dateUntil = (Date) f2.parse(pickdate);
                                String untilFinal = f1.format(dateUntil);

                                Calendar calendarCurrent = Calendar.getInstance(TimeZone.getTimeZone("GMT+0530"));
                                calendarCurrent.clear();
                                calendarCurrent.setTime(dateCurrent);
                                calendarCurrent.set(Calendar.HOUR_OF_DAY, 23);
                                calendarCurrent.set(Calendar.MINUTE, 58);

                                Calendar calendarUntil = Calendar.getInstance(TimeZone.getTimeZone("GMT+0530"));
                                calendarUntil.clear();
                                calendarUntil.setTime(dateUntil);
                                calendarUntil.set(Calendar.HOUR_OF_DAY, 23);
                                calendarUntil.set(Calendar.MINUTE, 58);

                                if (calendarCurrent.getTimeInMillis() <= calendarUntil.getTimeInMillis()) {

                                    if (appointmentModels.get(j).getRepeat().equalsIgnoreCase("No Repeat")) {

                                    } else if (appointmentModels.get(j).getRepeat().equalsIgnoreCase("Days")) {

                                        String[] daysArray = appointmentModels.get(j).getRepeate_frequency().split(",");
                                        for (String day : daysArray) {
                                            boolean result = chkDays(day, Date);
                                            if (result)
                                                tempAppointmentModels.add(appointmentModels.get(j));
                                        }

                                    } else if (appointmentModels.get(j).getRepeat().equalsIgnoreCase("Daily")) {
                                        tempAppointmentModels.add(appointmentModels.get(j));
                                    } else if (appointmentModels.get(j).getRepeat().equalsIgnoreCase("Weekly")) {
                                        // check start date
                                        long starttime = appointmentModels.get(j).getStarttime();
                                        boolean result = getValues(starttime, "weekly", Date);
                                        if (result)
                                            tempAppointmentModels.add(appointmentModels.get(j));

                                    } else if (appointmentModels.get(j).getRepeat().equalsIgnoreCase("Monthly")) {

                                        long starttime = appointmentModels.get(j).getStarttime();
                                        boolean result = getValues(starttime, "monthly", Date);
                                        if (result)
                                            tempAppointmentModels.add(appointmentModels.get(j));

                                    } else if (appointmentModels.get(j).getRepeat().equalsIgnoreCase("Yearly")) {

                                        long starttime = appointmentModels.get(j).getStarttime();
                                        boolean result = getValues(starttime, "yearly", Date);
                                        if (result)
                                            tempAppointmentModels.add(appointmentModels.get(j));

                                    } else if (appointmentModels.get(j).getRepeat().equalsIgnoreCase("Other")) {


                                        long starttime = appointmentModels.get(j).getStarttime();
                                        boolean result = getOtherValues(starttime, appointmentModels.get(j).getRepeate_frequency(), Date, Integer.parseInt(appointmentModels.get(j).getRepeate_num()));
                                        if (result)
                                            tempAppointmentModels.add(appointmentModels.get(j));
                                    }

                                }
                            }
                        }

                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        if (tempAppointmentModels.size() == 0) {
            tempAppointmentModels.add(new AppointmentMappingModel(0, null, null));
        }


        return tempAppointmentModels;
    }


    public boolean chkDays(String appointmentday, String cureentdate) throws ParseException {
        boolean result = false;

        appointmentday = appointmentday.replace(" ", "");

        try {
            SimpleDateFormat f1 = new SimpleDateFormat("yyyy-MM-dd");

            Date date = new Date();
            date = (Date) f1.parse(cureentdate);
            SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
            String dayOfTheWeek = sdf.format(date);

            if (dayOfTheWeek.equalsIgnoreCase(appointmentday))
                result = true;

        } catch (Exception e) {
            e.printStackTrace();
        }


        return result;
    }


    public boolean getValues(long apointmenttime, String repeat, String cureentdate) throws ParseException {

        boolean result = false;

        SimpleDateFormat f1 = new SimpleDateFormat("yyyy-MM-dd");

        String appointmentDate = f1.format(apointmenttime);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(apointmenttime);

        Date date = new Date();
        date = (Date) f1.parse(cureentdate);

        Calendar calendar1 = Calendar.getInstance(TimeZone.getTimeZone("GMT+0530"));
        calendar1.clear();
        calendar1.setTime(date);
        calendar1.set(Calendar.HOUR_OF_DAY, 23);
        calendar1.set(Calendar.MINUTE, 58);


        if (repeat.equalsIgnoreCase("monthly")) {
            while (calendar.getTimeInMillis() <= calendar1.getTimeInMillis()) {
                if (f1.format(calendar.getTime()).equals(cureentdate)) {
                    result = true;
                    break;
                }
                calendar.add(Calendar.MONTH, 1);
            }

        } else if (repeat.equalsIgnoreCase("yearly")) {
            while (calendar.getTimeInMillis() <= calendar1.getTimeInMillis()) {
                if (f1.format(calendar.getTime()).equals(cureentdate)) {
                    result = true;
                    break;
                }
                calendar.add(Calendar.YEAR, 1);
            }

        } else if (repeat.equalsIgnoreCase("weekly")) {
            while (calendar.getTimeInMillis() <= calendar1.getTimeInMillis()) {
                if (f1.format(calendar.getTime()).equals(cureentdate)) {
                    result = true;
                    break;
                }
                calendar.add(Calendar.DAY_OF_MONTH, 7);
            }
        }

        return result;
    }

    public boolean getOtherValues(long apointmenttime, String repeat, String cureentdate, int count) throws ParseException {

        boolean result = false;
        SimpleDateFormat f1 = new SimpleDateFormat("yyyy-MM-dd");
        String appointmentDate = f1.format(apointmenttime);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(apointmenttime);

        Date date = new Date();
        date = (Date) f1.parse(cureentdate);

        Calendar calendar1 = Calendar.getInstance(TimeZone.getTimeZone("GMT+0530"));
        calendar1.clear();
        calendar1.setTime(date);
        calendar1.set(Calendar.HOUR_OF_DAY, 23);
        calendar1.set(Calendar.MINUTE, 58);

        if (repeat.equalsIgnoreCase("months")) {
            while (calendar.getTimeInMillis() <= calendar1.getTimeInMillis()) {
                if (f1.format(calendar.getTime()).equals(cureentdate)) {
                    result = true;
                    break;
                }
                calendar.add(Calendar.MONTH, count);
            }

        } else if (repeat.equalsIgnoreCase("weeks")) {
            while (calendar.getTimeInMillis() <= calendar1.getTimeInMillis()) {
                if (f1.format(calendar.getTime()).equals(cureentdate)) {
                    result = true;
                    break;
                }
                calendar.add(Calendar.WEEK_OF_YEAR, count);
            }

        } else if (repeat.equalsIgnoreCase("days")) {
            while (calendar.getTimeInMillis() <= calendar1.getTimeInMillis()) {
                if (f1.format(calendar.getTime()).equals(cureentdate)) {
                    result = true;
                    break;
                }
                calendar.add(Calendar.DAY_OF_MONTH, count);
            }
        }
        return result;
    }

    public long convertTimeStamp_new(CharSequence date) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm a");

        Date tempDate = formatter.parse((String) date);

        java.sql.Timestamp timestamp = new java.sql.Timestamp(tempDate.getTime());

        Log.d("TimeStamp", String.valueOf(timestamp.getTime()));

        long time_stamp = timestamp.getTime();
        return time_stamp;
    }

    public ArrayList<TaskMappingModel> getDaysRecurring(Calendar baseCalendar, Calendar dueCalender, ArrayList<TaskMappingModel> datalist) {

        ArrayList<TaskMappingModel> recurringList = null;
        // set values according to recurring
        ArrayList<String> allDays = new ArrayList<>();
        SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        mFormat.setTimeZone(TimeZone.getTimeZone("GMT+0530"));
        int countNew = (int) calculateBTWNDays(baseCalendar, dueCalender);

        // if (countNew != 1)
        countNew++;

        for (int i = 0; i < countNew; i++) {
            // Add day to list
            allDays.add(i, mFormat.format(baseCalendar.getTime()));
            // Move next day
            baseCalendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        try {
            recurringList = getDatafromDB(allDays, datalist);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return recurringList;

    }

    public ArrayList<TaskMappingModel> getCurrentDateTask() {

        TableTaskData tableTaskData = new TableTaskData(BaseApplication.getInstance());
        Calendar baseCalendar = Calendar.getInstance();

        long currentTS = 0, currentTS_end = 0;
        SimpleDateFormat formatter = new SimpleDateFormat("dd MM yyyy hh:mm a");
        try {

            currentTS = (convertTimeStamp(String.valueOf(baseCalendar.get(Calendar.DAY_OF_MONTH)) + " " + String.valueOf((baseCalendar.get(Calendar.MONTH) + 1)) + " " + String.valueOf(baseCalendar.get(Calendar.YEAR)) + " 12:01 AM"));
            currentTS_end = (convertTimeStamp(String.valueOf(baseCalendar.get(Calendar.DAY_OF_MONTH)) + " " + String.valueOf((baseCalendar.get(Calendar.MONTH) + 1)) + " " + String.valueOf(baseCalendar.get(Calendar.YEAR)) + " 11:59 PM"));

        } catch (ParseException e) {
            e.printStackTrace();
        }

        ArrayList<TaskMappingModel> tempData1 = (ArrayList<TaskMappingModel>) tableTaskData.allDataBTWNdays(currentTS, currentTS_end, false, 0, SharedPrefUtils.getUserDetailModel(getActivity()).getId());

        ArrayList<TaskMappingModel> new_List = (ArrayList<TaskMappingModel>) getDaysRecurring_New(baseCalendar, tempData1);

        // now add overdue list
        long backDateTS = currentTS - 120000;
        tempData1 = tableTaskData.getBackDaysData(backDateTS, false, 0, SharedPrefUtils.getUserDetailModel(getActivity()).getId());

        long minimumTime = tableTaskData.getMininumTimeStamp();
        ArrayList<TaskMappingModel> new_tempData1 = new ArrayList<>();
        try {

            Calendar minTime = Calendar.getInstance();
            Date netDate = (new Date(minimumTime));
            minTime.setTime(netDate);

            Calendar basecal = Calendar.getInstance();
            basecal.add(Calendar.DAY_OF_MONTH, -1);

            if (tempData1.size() > 0) {
                new_tempData1 = (ArrayList<TaskMappingModel>) getDaysRecurring(minTime, basecal, tempData1);
            }


        } catch (Exception ex) {
            ex.printStackTrace();
        }

        new_List.addAll(new_tempData1);

        if (new_List.size() == 0) {
            new_List.add(new TaskMappingModel(null, 0, null));
        }

        return new_List;

    }

    public long convertTimeStamp(CharSequence date) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("dd MM yyyy hh:mm a");

        Date tempDate = formatter.parse((String) date);

        java.sql.Timestamp timestamp = new java.sql.Timestamp(tempDate.getTime());

        Log.d("TimeStamp", String.valueOf(timestamp.getTime()));

        long time_stamp = timestamp.getTime();
        return time_stamp;
    }

    public void notifyTaskList() {

    }

    public ArrayList<TaskMappingModel> getDaysRecurring_New(Calendar date, ArrayList<TaskMappingModel> datalist) {

        ArrayList<TaskMappingModel> recurringList = null;
        // set values according to recurring
        ArrayList<String> allDays = new ArrayList<>();
        SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        mFormat.setTimeZone(TimeZone.getTimeZone("GMT+0530"));

        allDays.add(mFormat.format(date.getTime()));

        try {
            recurringList = getDatafromDB(allDays, datalist);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return recurringList;

    }

    public long calculateBTWNDays(Calendar a, Calendar b) {

        long MILLISECS_PER_DAY = 24 * 60 * 60 * 1000;
        // Optional: avoid cloning objects if it is the same day
        if (a.get(Calendar.ERA) == b.get(Calendar.ERA)
                && a.get(Calendar.YEAR) == b.get(Calendar.YEAR)
                && a.get(Calendar.DAY_OF_YEAR) == b.get(Calendar.DAY_OF_YEAR)) {
            return 0;
        }
        Calendar a2 = (Calendar) a.clone();
        Calendar b2 = (Calendar) b.clone();
        a2.set(Calendar.HOUR_OF_DAY, 0);
        a2.set(Calendar.MINUTE, 0);
        a2.set(Calendar.SECOND, 0);
        a2.set(Calendar.MILLISECOND, 0);
        b2.set(Calendar.HOUR_OF_DAY, 0);
        b2.set(Calendar.MINUTE, 0);
        b2.set(Calendar.SECOND, 0);
        b2.set(Calendar.MILLISECOND, 0);
        long diff = a2.getTimeInMillis() - b2.getTimeInMillis();
        long days = diff / MILLISECS_PER_DAY;
        return Math.abs(days);
    }

    public ArrayList<TaskMappingModel> getDatafromDB(List<String> allDays, ArrayList<TaskMappingModel> taskModels) throws ParseException {

        ArrayList<TaskMappingModel> finalTaskDataModel;
        ArrayList<TaskMappingModel> finalTaskDataModelList;
        finalTaskDataModelList = new ArrayList<>();

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        TaskCompletedTable completedTable = new TaskCompletedTable(BaseApplication.getInstance());

        for (int i = 0; i < allDays.size(); i++) {

            try {

                finalTaskDataModel = new ArrayList<>();

                for (int j = 0; j < taskModels.size(); j++) {
                    java.sql.Timestamp tempTimestamp = new java.sql.Timestamp(formatter.parse(allDays.get(i)).getTime());
                    TaskMappingModel model = (TaskMappingModel) taskModels.get(j).clone();
                    // validation of start date

                    if (checkCurrentDateValid(allDays.get(i), model.getTaskDate())) {


                        // check completed dates
                        ArrayList<String> completedDatesList = completedTable.getCompletedDatesById(taskModels.get(j).getTask_id());

                        if (!completedDatesList.contains(allDays.get(i))) {

                            if (model.getTaskDate() >= tempTimestamp.getTime() && model.getTaskDate() <= (tempTimestamp.getTime() + 86280000)) {
                                model.setShowDate(allDays.get(i));
                                finalTaskDataModel.add(model);

                            } else {
                                // condition until check pick date

                                if (!StringUtils.isNullOrEmpty(model.getRepeate_untill())) {
                                    if (model.getRepeate_untill().equalsIgnoreCase("forever")) {
                                        if (model.getRepeat().equalsIgnoreCase("No Repeat")) {

                                        } else if (model.getRepeat().equalsIgnoreCase("Days")) {

                                            String[] daysArray = model.getRepeate_frequency().split(",");
                                            for (String day : daysArray) {
                                                boolean result = chkDays(day, allDays.get(i));
                                                if (result) {
                                                    model.setShowDate(allDays.get(i));
                                                    finalTaskDataModel.add(model);
                                                }

                                            }


                                        } else if (model.getRepeat().equalsIgnoreCase("Daily")) {
                                            model.setShowDate(allDays.get(i));
                                            finalTaskDataModel.add(model);
                                        } else if (model.getRepeat().equalsIgnoreCase("Weekly")) {
                                            // check start date
                                            long starttime = model.getTaskDate();
                                            boolean result = getValues(starttime, "weekly", allDays.get(i));
                                            if (result) {

                                                model.setShowDate(allDays.get(i));
                                                finalTaskDataModel.add(model);
                                            }
                                        } else if (model.getRepeat().equalsIgnoreCase("Monthly")) {

                                            long starttime = model.getTaskDate();
                                            boolean result = getValues(starttime, "monthly", allDays.get(i));
                                            if (result) {
                                                model.setShowDate(allDays.get(i));
                                                finalTaskDataModel.add(model);
                                            }
                                        } else if (model.getRepeat().equalsIgnoreCase("Yearly")) {

                                            long starttime = model.getTaskDate();
                                            boolean result = getValues(starttime, "yearly", allDays.get(i));
                                            if (result) {
                                                model.setShowDate(allDays.get(i));
                                                finalTaskDataModel.add(model);
                                            }
                                        } else if (model.getRepeat().equalsIgnoreCase("Other")) {


                                            long starttime = model.getTaskDate();
                                            boolean result = getOtherValues(starttime, model.getRepeate_frequency(), allDays.get(i), Integer.parseInt(model.getRepeate_num()));
                                            if (result) {
                                                model.setShowDate(allDays.get(i));
                                                finalTaskDataModel.add(model);
                                            }
                                        }

                                    } else {
                                        String pickdate = model.getRepeate_untill();
                                        String currentdate = allDays.get(i);

                                        SimpleDateFormat f1 = new SimpleDateFormat("yyyy-MM-dd");
                                        SimpleDateFormat f2 = new SimpleDateFormat("dd MMMM yyyy");

                                        Date dateCurrent = new Date();
                                        dateCurrent = (Date) f1.parse(currentdate);

                                        Date dateUntil = (Date) f2.parse(pickdate);
                                        String untilFinal = f1.format(dateUntil);

                                        Calendar calendarCurrent = Calendar.getInstance(TimeZone.getTimeZone("GMT+0530"));
                                        calendarCurrent.clear();
                                        calendarCurrent.setTime(dateCurrent);
                                        calendarCurrent.set(Calendar.HOUR_OF_DAY, 23);
                                        calendarCurrent.set(Calendar.MINUTE, 58);

                                        Calendar calendarUntil = Calendar.getInstance(TimeZone.getTimeZone("GMT+0530"));
                                        calendarUntil.clear();
                                        calendarUntil.setTime(dateUntil);
                                        calendarUntil.set(Calendar.HOUR_OF_DAY, 23);
                                        calendarUntil.set(Calendar.MINUTE, 58);

                                        if (calendarCurrent.getTimeInMillis() <= calendarUntil.getTimeInMillis()) {

                                            if (model.getRepeat().equalsIgnoreCase("No Repeat")) {

                                            } else if (model.getRepeat().equalsIgnoreCase("Days")) {

                                                String[] daysArray = model.getRepeate_frequency().split(",");
                                                for (String day : daysArray) {
                                                    boolean result = chkDays(day, allDays.get(i));
                                                    if (result) {
                                                        model.setShowDate(allDays.get(i));
                                                        finalTaskDataModel.add(model);
                                                    }
                                                }

                                            } else if (model.getRepeat().equalsIgnoreCase("Daily")) {
                                                model.setShowDate(allDays.get(i));
                                                finalTaskDataModel.add(model);
                                            } else if (model.getRepeat().equalsIgnoreCase("Weekly")) {
                                                // check start date
                                                long starttime = model.getTaskDate();
                                                boolean result = getValues(starttime, "weekly", allDays.get(i));
                                                if (result) {
                                                    model.setShowDate(allDays.get(i));
                                                    finalTaskDataModel.add(model);
                                                }
                                            } else if (model.getRepeat().equalsIgnoreCase("Monthly")) {

                                                long starttime = model.getTaskDate();
                                                boolean result = getValues(starttime, "monthly", allDays.get(i));
                                                if (result) {
                                                    model.setShowDate(allDays.get(i));
                                                    finalTaskDataModel.add(model);
                                                }
                                            } else if (model.getRepeat().equalsIgnoreCase("Yearly")) {

                                                long starttime = model.getTaskDate();
                                                boolean result = getValues(starttime, "yearly", allDays.get(i));
                                                if (result) {
                                                    model.setShowDate(allDays.get(i));
                                                    finalTaskDataModel.add(model);
                                                }
                                            } else if (model.getRepeat().equalsIgnoreCase("Other")) {


                                                long starttime = model.getTaskDate();
                                                boolean result = getOtherValues(starttime, model.getRepeate_frequency(), allDays.get(i), Integer.parseInt(model.getRepeate_num()));
                                                if (result) {
                                                    model.setShowDate(allDays.get(i));
                                                    finalTaskDataModel.add(model);
                                                }
                                            }

                                        }
                                    }
                                }

                            }

                        }
                    }


                }
                if (finalTaskDataModel.size() == 0) {
                } else {
                    finalTaskDataModelList.addAll(finalTaskDataModel);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return finalTaskDataModelList;
    }

    public boolean checkCurrentDateValid(String currentdate, long startdate) {
        boolean result = false;

        SimpleDateFormat f1 = new SimpleDateFormat("yyyy-MM-dd");

        String taskstartdate = f1.format(startdate);

        Date dateCurrent = new Date();
        Date dateStart = new Date();
        try {

            dateCurrent = (Date) f1.parse(currentdate);
            dateStart = (Date) f1.parse(taskstartdate);

        } catch (Exception e) {
            e.printStackTrace();
        }

        Calendar calendarCurrent = Calendar.getInstance(TimeZone.getTimeZone("GMT+0530"));
        calendarCurrent.clear();
        calendarCurrent.setTime(dateCurrent);
        calendarCurrent.set(Calendar.HOUR_OF_DAY, 23);
        calendarCurrent.set(Calendar.MINUTE, 58);

        Calendar calendarStart = Calendar.getInstance(TimeZone.getTimeZone("GMT+0530"));
        calendarStart.clear();
        calendarStart.setTime(dateStart);
        calendarStart.set(Calendar.HOUR_OF_DAY, 23);
        calendarStart.set(Calendar.MINUTE, 58);

        if (calendarCurrent.getTimeInMillis() >= calendarStart.getTimeInMillis()) {
            return true;
        }

        return result;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // call service here for completed tasks
        ((DashboardActivity) getActivity()).UploadCompleteTasks();

    }

    private void showCreateFamilyAlert() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());

        dialog.setMessage(getResources().getString(R.string.create_family)).setNegativeButton(getResources().getString(R.string.yes)
                , new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent createFamilyIntent = new Intent(getActivity(), CreateFamilyActivity.class);
                        startActivity(createFamilyIntent);
                        dialog.cancel();
                    }
                }).setPositiveButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // do nothing
                dialog.cancel();

            }
        }).setIcon(android.R.drawable.ic_dialog_alert);

        AlertDialog alert11 = dialog.create();
        alert11.show();

        alert11.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.home_light_blue));
        alert11.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.canceltxt_color));

    }

    public String getMonth(int month) {
        SimpleDateFormat sdf_n = new SimpleDateFormat("MMM");

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, month);
//        return new DateFormatSymbols().getMonths()[month];
        return (sdf_n.format(calendar.getTime()));
    }

    public void inflateEventCardsScroll() {
        for (int i = 0; i < Math.min(10, mBusinessDataListings.size()); i++) {
            final View view = mInflator.inflate(R.layout.card_item_event_dashboard, null);
            view.setTag(i);
            ImageView articleImage = (ImageView) view.findViewById(R.id.eventThumbnail);
            TextView title = (TextView) view.findViewById(R.id.title);
            TextView ageGroup = (TextView) view.findViewById(R.id.ageGroup);
            TextView address = (TextView) view.findViewById(R.id.addresstxt);
            TextView durationtxt = (TextView) view.findViewById(R.id.durationtxt);
            TextView category = (TextView) view.findViewById(R.id.category);
            final ImageView call = (ImageView) view.findViewById(R.id.call);
            ImageView addEvent = (ImageView) view.findViewById(R.id.addEvent);
            cardView = (CardView) view.findViewById(R.id.cardViewWidget);
            LinearLayout middleContainer = (LinearLayout) view.findViewById(R.id.middleContainer);
            LinearLayout lowerContainer = (LinearLayout) view.findViewById(R.id.lowerContainer);
            Picasso.with(getActivity()).load(mBusinessDataListings.get(i).getThumbnail()).placeholder(R.drawable.thumbnail_eventsxxhdpi).into(articleImage);
            title.setText(mBusinessDataListings.get(i).getName());
            ageGroup.setText(mBusinessDataListings.get(i).getAgegroup_text() + " years");
            address.setText(mBusinessDataListings.get(i).getLocality());
            final Calendar cal = Calendar.getInstance();
            cal.setTime(DateTimeUtils.stringToDate(mBusinessDataListings.get(i).getStart_date()));
            int startmonth = cal.get(Calendar.MONTH);
            int startDay = cal.get(Calendar.DAY_OF_MONTH);
            String orgmnth = getMonth(startmonth);
            String startDaystr = String.valueOf(startDay);
            String orgmnthstr = String.valueOf(orgmnth);
            Calendar cal1 = Calendar.getInstance();
            cal1.setTime(DateTimeUtils.stringToDate(mBusinessDataListings.get(i).getEnd_date()));
            int startmonth1 = cal1.get(Calendar.MONTH);
            int startDay1 = cal1.get(Calendar.DAY_OF_MONTH);
            String orgmnth1 = getMonth(startmonth1);
            String startDaystr1 = String.valueOf(startDay1);
            String orgmnthstr1 = String.valueOf(orgmnth1);
            durationtxt.setText(startDaystr + " " + orgmnthstr + "-" + startDaystr1 + " " + orgmnthstr1);
            category.setText(mBusinessDataListings.get(i).getActivities());
            final int finalI = i;
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent5 = new Intent(getActivity(), BusinessDetailsActivity.class);
                    String businessId = mBusinessDataListings.get(finalI).getId();
                    intent5.putExtra(Constants.CATEGORY_ID, SharedPrefUtils.getEventIdForCity(getActivity()));
                    intent5.putExtra(Constants.BUSINESS_OR_EVENT_ID, businessId);
                    intent5.putExtra(Constants.PAGE_TYPE, Constants.EVENT_PAGE_TYPE);
                    intent5.putExtra(Constants.DISTANCE, mBusinessDataListings.get(finalI).getDistance());
                    startActivity(intent5);

                }
            });
            eventPosition = i;
            addEvent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Build.VERSION.SDK_INT >= 23) {
                        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CALENDAR)
                                != PackageManager.PERMISSION_GRANTED
                                || ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_CALENDAR)
                                != PackageManager.PERMISSION_GRANTED) {
                            Log.i("PERMISSIONS", "Calendar permissions has NOT been granted. Requesting permissions.");
                            requestCalendarPermissions();
                        } else {
                            addCalendarEvent(eventPosition);
                        }
                    } else {
                        addCalendarEvent(eventPosition);
                    }
                }
            });
            final int finalI2 = i;
            call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String[] telList = null;
                    if (null != mBusinessDataListings.get(finalI2).getPhone()) {
                        telList = mBusinessDataListings.get(finalI2).getPhone().split("/");
                    }
                    if (telList == null || telList.length == 0) {
                        call.setVisibility(View.GONE);
                    } else {
                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + telList[0]));
                        Utils.pushEvent(getActivity(), GTMEventType.CALL_RESOURCES_CLICKED_EVENT, userId + "", "Dashboard");
                        startActivity(intent);
                    }

                }
            });

            hzScrollLinearLayoutEvent.addView(view);
        }
        View customViewMore = mInflator.inflate(R.layout.custom_view_more_dashboard, null);
        DisplayMetrics metrics = new DisplayMetrics();
        if (getActivity() != null) {
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
            int widthPixels = metrics.widthPixels;
            float width = (float) (widthPixels * 0.45);
            customViewMore.setMinimumWidth((int) width);
        }
        hzScrollLinearLayoutEvent.addView(customViewMore);
        customViewMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Constants.IS_SEARCH_LISTING = false;
                FragmentBusinesslistEvents fragment = new FragmentBusinesslistEvents();
                Bundle bundle = new Bundle();
                bundle.putInt(Constants.PAGE_TYPE, Constants.EVENT_PAGE_TYPE);
                bundle.putInt(Constants.EXTRA_CATEGORY_ID, SharedPrefUtils.getEventIdForCity(getActivity()));
                bundle.putString(Constants.CATEGOTY_NAME, "Events & workshop");
                fragment.setArguments(bundle);
                ((DashboardActivity) getActivity()).replaceFragment(fragment, bundle, true);
            }
        });
    }

    private void requestCalendarPermissions() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                Manifest.permission.READ_CALENDAR) || ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                Manifest.permission.WRITE_CALENDAR)) {
            Log.i("Permissions",
                    "Displaying get accounts permission rationale to provide additional context.");

            // Display a SnackBar with an explanation and a button to trigger the request.
            Snackbar.make(baseScroll, R.string.permission_calendar_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            requestUngrantedPermissions();
                        }
                    })
                    .show();
        } else {
            requestUngrantedPermissions();
        }
    }

    private void requestUngrantedPermissions() {
        ArrayList<String> permissionList = new ArrayList<>();
        for (int i = 0; i < PERMISSIONS_INIT.length; i++) {
            if (ActivityCompat.checkSelfPermission(getActivity(), PERMISSIONS_INIT[i]) != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(PERMISSIONS_INIT[i]);
            }
        }
        String[] requiredPermission = permissionList.toArray(new String[permissionList.size()]);
        requestPermissions(requiredPermission, REQUEST_INIT_PERMISSION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (requestCode == REQUEST_INIT_PERMISSION) {
            Log.i("Permissions", "Received response for storage permissions request.");

            if (PermissionUtil.verifyPermissions(grantResults)) {
                Snackbar.make(baseScroll, R.string.permision_available_init,
                        Snackbar.LENGTH_SHORT)
                        .show();
                addCalendarEvent(eventPosition);
            } else {
                Log.i("Permissions", "storage permissions were NOT granted.");
                Snackbar.make(baseScroll, R.string.permissions_not_granted,
                        Snackbar.LENGTH_SHORT)
                        .show();
            }

        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void addCalendarEvent(int finalI1) {
        if (mBusinessDataListings.get(finalI1).isEventAdded()) {

            ToastUtils.showToast(getActivity(), getActivity().getResources().getString(R.string.event_added));
        } else {
            final BusinessDataListing information = mBusinessDataListings.get(finalI1);
            new AlertDialog.Builder(getActivity(), R.style.MyAlertDialogStyle)
                    .setTitle("Add Event to calendar")
                    .setMessage("Do you want add this event to you personal calendar?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // continue with delete
                            dialog.dismiss();
                            //  onButtonClicked.onButtonCLick(0);
                            saveCalendar(information.getName(), information.getDescription(), information.getStart_date(), information.getEnd_date(), information.getLocality());
                            ToastUtils.showToast(getActivity(), "Successfully added to Calendar");
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                            dialog.dismiss();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
    }

    private void saveCalendar(String title, String desc, String sDate, String eDate, String location) {

        ContentResolver cr = (getActivity()).getContentResolver();
        ContentValues values = new ContentValues();

        values.put(CalendarContract.Events.DTSTART, "" + DateTimeUtils.getTimestampFromStringDate(sDate));
        values.put(CalendarContract.Events.DTEND, "" + DateTimeUtils.getTimestampFromStringDate(eDate));
        values.put(CalendarContract.Events.TITLE, title);
        values.put(CalendarContract.Events.DESCRIPTION, desc);
        values.put(CalendarContract.Events.EVENT_LOCATION, location);

        TimeZone timeZone = TimeZone.getDefault();
        values.put(CalendarContract.Events.EVENT_TIMEZONE, timeZone.getID());

        // default calendar
        values.put(CalendarContract.Events.CALENDAR_ID, 3);
        values.put(CalendarContract.Events.HAS_ALARM, 1);

        // insert event to calendar
        Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);
    }

    private class MyWebChromeClient extends WebChromeClient {
        FrameLayout.LayoutParams LayoutParameters = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT);

        @Override
        public void onShowCustomView(View customView, CustomViewCallback callback) {
            // if a view already exists then immediately terminate the new one
            if (mCustomView != null) {
                callback.onCustomViewHidden();
                return;
            }
            mContentView = (DrawerLayout) ((DashboardActivity) getActivity()).findViewById(R.id.drawer_layout);
            mContentView.setVisibility(View.GONE);
            mCustomViewContainer = new FrameLayout(getActivity());
            mCustomViewContainer.setLayoutParams(LayoutParameters);
            mCustomViewContainer.setBackgroundResource(android.R.color.black);
            view.setLayoutParams(LayoutParameters);
            mCustomViewContainer.addView(customView);
            mCustomView = customView;
            mCustomViewCallback = callback;
            mCustomViewContainer.setVisibility(View.VISIBLE);
            getActivity().setContentView(mCustomViewContainer);
        }

        @Override
        public void onHideCustomView() {
            if (mCustomView == null) {
                return;
            } else {
                // Hide the custom view.
                mCustomView.setVisibility(View.GONE);
                // Remove the custom view from its container.
                mCustomViewContainer.removeView(mCustomView);
                mCustomView = null;
                mCustomViewContainer.setVisibility(View.GONE);
                mCustomViewCallback.onCustomViewHidden();
                // Show the content view.
                mContentView.setVisibility(View.VISIBLE);
                ((DashboardActivity) getActivity()).setContentView(mContentView);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (videoIframe == null) {
            return;
        } else {
            for (int i = 0; i < videoIframe.size(); i++) {
                videoIframe.get(i).onResume();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (videoIframe == null) {
            return;
        } else {
            for (int i = 0; i < videoIframe.size(); i++) {
                videoIframe.get(i).onPause();
            }
        }
    }

    private Callback<NotificationCenterListResponse> unreadNotificationCountResponseCallback = new Callback<NotificationCenterListResponse>() {
        @Override
        public void onResponse(Call<NotificationCenterListResponse> call, retrofit2.Response<NotificationCenterListResponse> response) {
            if (response == null || response.body() == null) {
                ((DashboardActivity) getActivity()).showToast("Something went wrong from server");
                return;
            }

            try {
                NotificationCenterListResponse responseData = (NotificationCenterListResponse) response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    ((DashboardActivity) getActivity()).updateUnreadNotificationCount(responseData.getData().getTotal());
                } else {
                    ((DashboardActivity) getActivity()).showToast(getString(R.string.went_wrong));
                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4KException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<NotificationCenterListResponse> call, Throwable t) {
            Crashlytics.logException(t);
            Log.d("MC4KException", Log.getStackTraceString(t));
        }
    };
}