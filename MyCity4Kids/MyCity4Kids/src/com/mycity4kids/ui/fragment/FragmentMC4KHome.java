package com.mycity4kids.ui.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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
import com.mycity4kids.controller.BusinessListController;
import com.mycity4kids.dbtable.TableAppointmentData;
import com.mycity4kids.dbtable.TableKids;
import com.mycity4kids.dbtable.TableTaskData;
import com.mycity4kids.dbtable.TaskCompletedTable;
import com.mycity4kids.enums.ParentingFilterType;
import com.mycity4kids.gtmutils.GTMEventType;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.interfaces.OnWebServiceCompleteListener;
import com.mycity4kids.models.businesslist.BusinessDataListing;
import com.mycity4kids.models.businesslist.BusinessListRequest;
import com.mycity4kids.models.businesslist.BusinessListResponse;
import com.mycity4kids.models.parentingstop.CommonParentingList;
import com.mycity4kids.models.parentingstop.CommonParentingResponse;
import com.mycity4kids.models.response.ArticleListingResponse;
import com.mycity4kids.models.response.ArticleListingResult;
import com.mycity4kids.models.user.KidsInfo;
import com.mycity4kids.newmodels.AppointmentMappingModel;
import com.mycity4kids.newmodels.AttendeeModel;
import com.mycity4kids.newmodels.TaskMappingModel;
import com.mycity4kids.newmodels.VolleyBaseResponse;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.TopicsCategoryAPI;
import com.mycity4kids.ui.activity.ActivityCreateAppointment;
import com.mycity4kids.ui.activity.ActivityShowAppointment;
import com.mycity4kids.ui.activity.ArticleListingActivity;
import com.mycity4kids.ui.activity.ArticlesAndBlogsDetailsActivity;
import com.mycity4kids.ui.activity.BusinessDetailsActivity;
import com.mycity4kids.ui.activity.CityBestArticleListingActivity;
import com.mycity4kids.ui.activity.CreateFamilyActivity;
import com.mycity4kids.ui.activity.DashboardActivity;
import com.mycity4kids.ui.adapter.AdapterHomeAppointment;
import com.mycity4kids.ui.adapter.ArticlesListingAdapter;
import com.mycity4kids.ui.adapter.BusinessListingAdapterevent;
import com.mycity4kids.utils.ArrayAdapterFactory;
import com.mycity4kids.volley.HttpVolleyRequest;
import com.mycity4kids.widget.CustomListView;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
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

    View view;
    CustomListView appointmentList;
    TableAppointmentData tableAppointment;
    ArrayList<AppointmentMappingModel> appointmentListData;
    AdapterHomeAppointment adapterHomeAppointment;
    TextView goToCal, current, goToBlogs, txtBlogs1;
    ImageView imgGoToCal, imgGoToEvents, imgGoToBlogs;
    ImageView addAppointment;
    ScrollView baseScroll;
    private ProgressBar progressBar, blogProgessBar, blogProgessBar1;
    private BusinessListingAdapterevent businessAdapter;
    private ArticlesListingAdapter articlesListingAdapter;
    private int mBusinessListCount = 1;
    private int mTotalPageCount = 0;
    private int mPageCount = 1;
    private int businessOrEventType;
    private int from = 1;
    private int to = 10;
    private ArrayList<BusinessDataListing> mBusinessDataListings;
    private ArrayList<CommonParentingList> mArticleDataListing1;
    private ArrayList<ArticleListingResult> mArticleDataListing;
    private ArrayList<ArticleListingResult> mArticleBestCityListing;
    private CustomListView eventListView;
    private HorizontalScrollView blogListView;
    private View rltLoadingView;
    private boolean mIsRequestRunning;
    private boolean mEventDataAvalble;
    TextView txtCal, txtEvents, txtBlogs;
    private LayoutInflater mInflator;
    private LinearLayout hzScrollLinearLayout, hzScrollLinearLayoutEvent, hzScrollLinearLayout1, blogHeader1;
    private float density;
    CardView cardView;
    int sortType = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.aa_mc4k_home, container, false);
        Utils.pushOpenScreenEvent(getActivity(), "Dashboard Fragment", SharedPrefUtils.getUserDetailModel(getActivity()).getId() + "");
        mInflator = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        hzScrollLinearLayout = (LinearLayout) view.findViewById(R.id.hzScrollLinearLayout);
        hzScrollLinearLayout1 = (LinearLayout) view.findViewById(R.id.hzScrollLinearLayout1);
        hzScrollLinearLayoutEvent = (LinearLayout) view.findViewById(R.id.hzScrollLinearLayoutEvent);
        density = getActivity().getResources().getDisplayMetrics().density;
        appointmentList = (CustomListView) view.findViewById(R.id.home_appointmentList);
        goToCal = (TextView) view.findViewById(R.id.go_to_cal);
        addAppointment = (ImageView) view.findViewById(R.id.add_appointment);
        current = (TextView) view.findViewById(R.id.current_date);
        baseScroll = (ScrollView) view.findViewById(R.id.base_scroll);
        imgGoToCal = (ImageView) view.findViewById(R.id.img_go_to_cal);
        imgGoToEvents = (ImageView) view.findViewById(R.id.img_go_to_events);
        imgGoToBlogs = (ImageView) view.findViewById(R.id.img_go_to_blogs);
        txtCal = (TextView) view.findViewById(R.id.txtCal);
        txtEvents = (TextView) view.findViewById(R.id.txtEvents);
        txtBlogs = (TextView) view.findViewById(R.id.txtBlogs);
        txtBlogs1 = (TextView) view.findViewById(R.id.txtBlogs1);
        progressBar = (ProgressBar) view.findViewById(R.id.eventprogressbar);
        blogProgessBar = (ProgressBar) view.findViewById(R.id.blogprogressbar);
        blogProgessBar1 = (ProgressBar) view.findViewById(R.id.blogprogressbar1);
        eventListView = (CustomListView) view.findViewById(R.id.eventList);
        blogListView = (HorizontalScrollView) view.findViewById(R.id.bloglist);
        rltLoadingView = (RelativeLayout) view.findViewById(R.id.rltLoadingView);
        blogHeader1 = (LinearLayout) view.findViewById(R.id.blogHeader1);
        if (SharedPrefUtils.getCurrentCityModel(getActivity()).getName().isEmpty()) {
            switch (SharedPrefUtils.getCurrentCityModel(getActivity()).getId()) {
                case 1:
                    txtBlogs1.setText("Best of " + "Delhi-NCR");
                    break;
                case 2:
                    txtBlogs1.setText("Best of " + "Bangalore");
                    break;
                case 3:
                    txtBlogs1.setText("Best of " + "Mumbai");
                    break;
                case 4:
                    txtBlogs1.setText("Best of " + "Pune");
                    break;
                case 5:
                    txtBlogs1.setText("Best of " + "Hyderabad");
                    break;
                case 6:
                    txtBlogs1.setText("Best of " + "Chennai");
                    break;
                case 7:
                    txtBlogs1.setText("Best of " + "Kolkata");
                    break;
                case 8:
                    txtBlogs1.setText("Best of " + "Jaipur");
                    break;
                case 9:
                    txtBlogs1.setText("Best of " + "Ahmedabad");
                    break;
                default:
                    txtBlogs1.setText("Best of " + "Delhi-NCR");
                    break;
            }

        } else {
            txtBlogs1.setText("Best of " + SharedPrefUtils.getCurrentCityModel(getActivity()).getName());
        }
        goToCal.setOnClickListener(this);
        addAppointment.setOnClickListener(this);
        imgGoToCal.setOnClickListener(this);
        imgGoToEvents.setOnClickListener(this);
        imgGoToBlogs.setOnClickListener(this);
        txtCal.setOnClickListener(this);
        txtEvents.setOnClickListener(this);
        txtBlogs.setOnClickListener(this);
        blogHeader1.setOnClickListener(this);

        view.findViewById(R.id.go_to_blog).setOnClickListener(this);
        view.findViewById(R.id.go_to_events).setOnClickListener(this);

        view.findViewById(R.id.no_blog).setOnClickListener(this);
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
        appointmentListData = new ArrayList<>();
        mArticleBestCityListing = new ArrayList<>();
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

        // blog adapter
        mArticleDataListing = new ArrayList<>();
        mArticleDataListing1 = new ArrayList<>();
        articlesListingAdapter = new ArticlesListingAdapter(getActivity(), true);

        hitBlogListingApi();
        hitEditorPicksApi();

        if (!SharedPrefUtils.isCityFetched(getActivity())) {
            view.findViewById(R.id.eventsss).setVisibility(View.GONE);
        } else {
            view.findViewById(R.id.eventsss).setVisibility(View.VISIBLE);
            view.findViewById(R.id.blogss).setVisibility(View.VISIBLE);

            // hit business list api
            businessAdapter = new BusinessListingAdapterevent(getActivity());
            mBusinessDataListings = new ArrayList<>();
            eventListView.setAdapter(businessAdapter);

            if (BaseApplication.getBusinessREsponse() == null || BaseApplication.getBusinessREsponse().isEmpty()) {
                hitBusinessListingApi(SharedPrefUtils.getEventIdForCity(getActivity()), 1);
            } else {
                mEventDataAvalble = true;
                mBusinessDataListings.addAll(BaseApplication.getBusinessREsponse());
                inflateEventCardsScroll();
            }
        }
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


    public void hitBusinessListingApi(int categoryId, int page) {
        if (!ConnectivityUtils.isNetworkEnabled(getActivity())) {
            return;
        }

        if (page == 1) {
            //        progressBar.setVisibility(View.VISIBLE);
        }

        // child ages
        TableKids tableKids = new TableKids(BaseApplication.getInstance());
        ArrayList<KidsInfo> kidsInformations = (ArrayList<KidsInfo>) tableKids.getAllKids();
        HashSet<String> selectedageGroups = new HashSet<>();

        // find ages

        for (int i = 0; i < kidsInformations.size(); i++) {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            Date startDate;
            try {
                startDate = df.parse(kidsInformations.get(i).getDate_of_birth());
                int age = getAge(startDate);

                selectedageGroups.add("" + age);

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        BusinessListController businessListController = new BusinessListController(getActivity(), this);
        BusinessListRequest businessListRequest = new BusinessListRequest();
        businessListRequest.setCategory_id(categoryId + "");
        businessListRequest.setDate_by("onlytoday");
        businessListRequest.setCity_id((SharedPrefUtils.getCurrentCityModel(getActivity())).getId() + "");
        businessListRequest.setPage(page + "");
        businessListController.getData(AppConstants.BUSINESS_LIST_REQUEST, businessListRequest);

        mIsRequestRunning = true;

    }

    public void hitBlogListingApi() {
        blogProgessBar.setVisibility(View.VISIBLE);
        String url;
        url = AppConstants.LIVE_URL + "v1/articles/trending/" + from + "/" + to;
        HttpVolleyRequest.getStringResponse(getActivity(), url, null, mGetArticleListingListener, Request.Method.GET, true);

    }

    private OnWebServiceCompleteListener mGetArticleListingListener = new OnWebServiceCompleteListener() {
        @Override
        public void onWebServiceComplete(VolleyBaseResponse response, boolean isError) {
            blogProgessBar.setVisibility(View.GONE);
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

                    mArticleDataListing.addAll(responseBlogData.getData().getResult());
                    hzScrollLinearLayout.removeAllViews();
                    for (int i = 0; i < mArticleDataListing.size(); i++) {
                        final View view = mInflator.inflate(R.layout.card_item_article_dashboard, null);
                        view.setTag(i);
                        ImageView articleImage = (ImageView) view.findViewById(R.id.imvAuthorThumb);
                        TextView title = (TextView) view.findViewById(R.id.txvArticleTitle);
                        cardView = (CardView) view.findViewById(R.id.cardViewWidget);
                        Picasso.with(getActivity()).load(mArticleDataListing.get(i).getImageUrl().getMobileWebThumbnail()).placeholder(R.drawable.default_article).into(articleImage);
                        title.setText(mArticleDataListing.get(i).getTitle());
                        view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getActivity(), ArticlesAndBlogsDetailsActivity.class);

                                ArticleListingResult parentingListData = (ArticleListingResult) (mArticleDataListing.get((int) view.getTag()));
                                intent.putExtra(Constants.ARTICLE_ID, parentingListData.getId());
                                intent.putExtra(Constants.AUTHOR_ID, parentingListData.getUserId());
                                startActivity(intent);
                                Log.e("Tag", "" + view.getTag());
                            }
                        });
                        hzScrollLinearLayout.addView(view);
                    }
                    View customViewMore = mInflator.inflate(R.layout.custom_view_more_dashboard, null);
                    DisplayMetrics metrics = new DisplayMetrics();
                    if (getActivity() != null) {
                        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
                        int widthPixels = metrics.widthPixels;
                        float width = (float) (widthPixels * 0.45);
                        customViewMore.setMinimumWidth((int) width);
                    }
                    if (mArticleDataListing.size() != 0) {
                        hzScrollLinearLayout.addView(customViewMore);
                    }
                    customViewMore.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getActivity(), ArticleListingActivity.class);
                            intent.putExtra(Constants.SORT_TYPE, Constants.KEY_TRENDING);
                            startActivity(intent);
                        }
                    });
                    if (mArticleDataListing.isEmpty()) {
                        ((TextView) view.findViewById(R.id.go_to_blog)).setVisibility(View.VISIBLE);
                        ((TextView) view.findViewById(R.id.no_blog)).setVisibility(View.VISIBLE);
                    }
                    baseScroll.smoothScrollTo(0, 0);

                } else {
                    ((TextView) view.findViewById(R.id.go_to_blog)).setVisibility(View.VISIBLE);
                    ((TextView) view.findViewById(R.id.no_blog)).setVisibility(View.VISIBLE);

                }

            }

        }
    };

    public void hitEditorPicksApi() {
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        TopicsCategoryAPI topicsAPI = retrofit.create(TopicsCategoryAPI.class);

        Call<ArticleListingResponse> filterCall = topicsAPI.getBestArticlesForCity("" + SharedPrefUtils.getCurrentCityModel(getActivity()).getId(), sortType, 1, 15);
        filterCall.enqueue(articleListingResponseCallback);
    }

    private Callback<ArticleListingResponse> articleListingResponseCallback = new Callback<ArticleListingResponse>() {
        @Override
        public void onResponse(Call<ArticleListingResponse> call, retrofit2.Response<ArticleListingResponse> response) {
            if (response == null || response.body() == null) {
                ((DashboardActivity) getActivity()).showToast("Something went wrong from server");
                return;
            }

            try {
                ArticleListingResponse responseData = (ArticleListingResponse) response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    processArticleListingResponse(responseData);
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
            Crashlytics.logException(t);
            Log.d("MC4KException", Log.getStackTraceString(t));
            ((DashboardActivity) getActivity()).showToast(getString(R.string.went_wrong));
        }
    };

    private void processArticleListingResponse(ArticleListingResponse responseData) {

        ArrayList<ArticleListingResult> dataList = responseData.getData().getResult();

        if (dataList.size() == 0) {
            ((TextView) view.findViewById(R.id.no_blog1)).setVisibility(View.VISIBLE);
        } else {
            ((TextView) view.findViewById(R.id.no_blog1)).setVisibility(View.GONE);
            mArticleBestCityListing.clear();
            mArticleBestCityListing.addAll(responseData.getData().getResult());
            hzScrollLinearLayout1.removeAllViews();
            BaseApplication.setBestCityResponse(mArticleBestCityListing);
            for (int i = 0; i < mArticleBestCityListing.size(); i++) {
                final View view1 = mInflator.inflate(R.layout.card_item_article_dashboard, null);
                view1.setTag(i);
                ImageView articleImage = (ImageView) view1.findViewById(R.id.imvAuthorThumb);
                TextView title = (TextView) view1.findViewById(R.id.txvArticleTitle);
                cardView = (CardView) view1.findViewById(R.id.cardViewWidget);
                Picasso.with(getActivity()).load(mArticleBestCityListing.get(i).getImageUrl().getMobileWebThumbnail()).placeholder(R.drawable.default_article).into(articleImage);
                title.setText(mArticleBestCityListing.get(i).getTitle());

                view1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), ArticlesAndBlogsDetailsActivity.class);

                        ArticleListingResult parentingListData1 = (ArticleListingResult) (mArticleBestCityListing.get((int) view1.getTag()));
                        intent.putExtra(Constants.ARTICLE_ID, parentingListData1.getId());
                        intent.putExtra(Constants.AUTHOR_ID, parentingListData1.getUserId());
                        startActivity(intent);
                        Log.e("Tag", "" + view1.getTag());
                    }
                });
                hzScrollLinearLayout1.addView(view1);
            }
            View customViewMore = mInflator.inflate(R.layout.custom_view_more_dashboard, null);
            DisplayMetrics metrics = new DisplayMetrics();
            if (getActivity() != null) {
                getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
                int widthPixels = metrics.widthPixels;
                float width = (float) (widthPixels * 0.45);
                customViewMore.setMinimumWidth((int) width);
            }
            hzScrollLinearLayout1.addView(customViewMore);
            customViewMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent1 = new Intent(getActivity(), CityBestArticleListingActivity.class);
                    startActivity(intent1);
                }
            });
            if (mArticleBestCityListing.isEmpty()) {
                ((TextView) view.findViewById(R.id.go_to_blog)).setVisibility(View.VISIBLE);
                ((TextView) view.findViewById(R.id.no_blog1)).setVisibility(View.VISIBLE);
            }
            baseScroll.smoothScrollTo(0, 0);
        }
    }

    private OnWebServiceCompleteListener mGetArticleListingListener1 = new OnWebServiceCompleteListener() {
        @Override
        public void onWebServiceComplete(VolleyBaseResponse response, boolean isError) {
            blogProgessBar1.setVisibility(View.GONE);
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

                CommonParentingResponse responseBlogData;
                try {
                    responseBlogData = new Gson().fromJson(response.getResponseBody(), CommonParentingResponse.class);
                } catch (JsonSyntaxException jse) {
                    Crashlytics.logException(jse);
                    Log.d("JsonSyntaxException", Log.getStackTraceString(jse));
                    ((DashboardActivity) getActivity()).showToast("Something went wrong from server");
                    removeProgressDialog();
                    return;
                }

                if (responseBlogData.getResponseCode() == Constants.HTTP_RESPONSE_SUCCESS) {
                    //clear list to avoid duplicates due to volley caching
                    mArticleDataListing1.clear();

                    mArticleDataListing1.addAll(responseBlogData.getResult().getData().getData());
                    hzScrollLinearLayout1.removeAllViews();
                    BaseApplication.setBlogResponse(mArticleDataListing1);
                    for (int i = 0; i < mArticleDataListing1.size(); i++) {
                        final View view1 = mInflator.inflate(R.layout.card_item_article_dashboard, null);
                        view1.setTag(i);
                        ImageView articleImage = (ImageView) view1.findViewById(R.id.imvAuthorThumb);
                        TextView title = (TextView) view1.findViewById(R.id.txvArticleTitle);
                        cardView = (CardView) view1.findViewById(R.id.cardViewWidget);
                        Picasso.with(getActivity()).load(mArticleDataListing1.get(i).getThumbnail_image()).placeholder(R.drawable.default_article).into(articleImage);
                        title.setText(mArticleDataListing1.get(i).getTitle());

                        view1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getActivity(), ArticlesAndBlogsDetailsActivity.class);

                                CommonParentingList parentingListData1 = (CommonParentingList) (mArticleDataListing1.get((int) view1.getTag()));
                                intent.putExtra(Constants.ARTICLE_ID, parentingListData1.getId());
                                intent.putExtra(Constants.ARTICLE_COVER_IMAGE, parentingListData1.getThumbnail_image());
                                intent.putExtra(Constants.PARENTING_TYPE, ParentingFilterType.ARTICLES);
                                intent.putExtra(Constants.FILTER_TYPE, parentingListData1.getAuthor_type());
                                intent.putExtra(Constants.BLOG_NAME, parentingListData1.getBlog_name());
                                startActivity(intent);
                                Log.e("Tag", "" + view1.getTag());
                            }
                        });
                        hzScrollLinearLayout1.addView(view1);
                    }
                    View customViewMore = mInflator.inflate(R.layout.custom_view_more_dashboard, null);
                    DisplayMetrics metrics = new DisplayMetrics();
                    if (getActivity() != null) {
                        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
                        int widthPixels = metrics.widthPixels;
                        float width = (float) (widthPixels * 0.45);
                        customViewMore.setMinimumWidth((int) width);
                    }
                    hzScrollLinearLayout1.addView(customViewMore);
                    customViewMore.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            FragmentEditorsPick editorsfragment = new FragmentEditorsPick();
                            ((DashboardActivity) getActivity()).replaceFragment(editorsfragment, null, true);
                        }
                    });
                    if (mArticleDataListing1.isEmpty()) {
                        ((TextView) view.findViewById(R.id.go_to_blog)).setVisibility(View.VISIBLE);
                        ((TextView) view.findViewById(R.id.no_blog)).setVisibility(View.VISIBLE);
                    }
                    baseScroll.smoothScrollTo(0, 0);

                } else if (responseBlogData.getResponseCode() == 400) {
                    ((TextView) view.findViewById(R.id.go_to_blog)).setVisibility(View.VISIBLE);
                    ((TextView) view.findViewById(R.id.no_blog)).setVisibility(View.VISIBLE);

                }

            }
        }
    };

    public boolean calcAgeGroup(int kidAge, String ageGroup) {
        boolean result = false;

        try {
            int position = ageGroup.indexOf('(') + 1;
            int yearPosition = ageGroup.indexOf("year");
            ageGroup = ageGroup.substring(position, yearPosition);

            String[] diff = ageGroup.split("-");
            if (diff.length >= 2) {
                int startAge = Integer.parseInt(diff[0].trim());
                int endAge = Integer.parseInt(diff[1].trim());

                if (kidAge >= startAge && kidAge <= endAge) {
                    result = true;
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        return result;
    }


    @Override
    protected void updateUi(Response response) {

        mIsRequestRunning = false;
        if (response == null) {
            progressBar.setVisibility(View.GONE);
            blogProgessBar.setVisibility(View.GONE);
            return;
        }


        switch (response.getDataType()) {
            case AppConstants.BUSINESS_LIST_REQUEST:
                progressBar.setVisibility(View.GONE);
                BusinessListResponse responseData = (BusinessListResponse) response.getResponseObject();
                if (responseData.getResponseCode() == Constants.HTTP_RESPONSE_SUCCESS) {

                    mBusinessListCount = responseData.getResult().getData().getTotal();
                    mTotalPageCount = responseData.getResult().getData().getPage_count();
                    //to add in already created list
                    // we neew to clear this list in case of sort by and filter
                    //  mBusinessDataListings.clear();
                    mBusinessDataListings.addAll(responseData.getResult().getData().getData());

                    BaseApplication.setBusinessREsponse(mBusinessDataListings);

                    businessAdapter.setListData(mBusinessDataListings, businessOrEventType);

                    businessAdapter.notifyDataSetChanged();
                    inflateEventCardsScroll();

                    if (mBusinessDataListings.isEmpty()) {

                        ((TextView) view.findViewById(R.id.go_to_events)).setVisibility(View.VISIBLE);
                        ((TextView) view.findViewById(R.id.no_events)).setVisibility(View.VISIBLE);
                        // eventListView.setVisibility(View.GONE);
                    }
                    baseScroll.smoothScrollTo(0, 0);

                } else if (responseData.getResponseCode() == 400) {

                    ((TextView) view.findViewById(R.id.go_to_events)).setVisibility(View.VISIBLE);
                    ((TextView) view.findViewById(R.id.no_events)).setVisibility(View.VISIBLE);
                    //eventListView.setVisibility(View.GONE);
                    //((LinearLayout) view.findViewById(R.id.eventHeader)).setVisibility(View.GONE);

                }
                break;
            case AppConstants.ARTICLES_TODAY_REQUEST:
                blogProgessBar.setVisibility(View.GONE);
                CommonParentingResponse responseBlogData = (CommonParentingResponse) response.getResponseObject();
                if (responseBlogData.getResponseCode() == Constants.HTTP_RESPONSE_SUCCESS) {

//                    mArticleDataListing.addAll(responseBlogData.getResult().getData().getData());
//                    BaseApplication.setBlogResponse(mArticleDataListing);
//                    articlesListingAdapter.setNewListData(mArticleDataListing);
                    articlesListingAdapter.notifyDataSetChanged();

                    if (mArticleDataListing.isEmpty()) {
                        ((TextView) view.findViewById(R.id.go_to_blog)).setVisibility(View.VISIBLE);
                        ((TextView) view.findViewById(R.id.no_blog)).setVisibility(View.VISIBLE);
                        //  ((LinearLayout) view.findViewById(R.id.blogHeader)).setVisibility(View.GONE);
                        //  eventListView.setVisibility(View.GONE);
                    }
                    baseScroll.smoothScrollTo(0, 0);

                } else if (responseBlogData.getResponseCode() == 400) {
                    ((TextView) view.findViewById(R.id.go_to_blog)).setVisibility(View.VISIBLE);
                    ((TextView) view.findViewById(R.id.no_blog)).setVisibility(View.VISIBLE);
                    //blogListView.setVisibility(View.GONE);
                    // ((LinearLayout) view.findViewById(R.id.blogHeader)).setVisibility(View.GONE);

                }
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
            case R.id.go_to_blog:
            case R.id.img_go_to_blogs:
            case R.id.txtBlogs:
                Intent intent3 = new Intent(getActivity(), ArticleListingActivity.class);
                intent3.putExtra(Constants.SORT_TYPE, Constants.KEY_TRENDING);
                startActivity(intent3);
                break;
            case R.id.blogHeader1:
                Intent intent1 = new Intent(getActivity(), CityBestArticleListingActivity.class);
                startActivity(intent1);
                break;
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
        if (SharedPrefUtils.isChangeCity(getActivity())) {
            hitEditorPicksApi();
            txtBlogs1.setText("Best of " + SharedPrefUtils.getCurrentCityModel(getActivity()).getName());
            SharedPrefUtils.setChangeCityFlag(getActivity(), false);
            mBusinessDataListings.clear();
            BaseApplication.setBusinessREsponse(mBusinessDataListings);
            businessAdapter.setListData(mBusinessDataListings, businessOrEventType);
            businessAdapter.notifyDataSetChanged();
            hzScrollLinearLayoutEvent.removeAllViews();
            hitBusinessListingApi(SharedPrefUtils.getEventIdForCity(getActivity()), 1);
        }


        Calendar calendar = Calendar.getInstance();
        tableAppointment = new TableAppointmentData(BaseApplication.getInstance());
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        appointmentListData = getAppointmentByDay(formatter.format(calendar.getTime()));
        try {
            // first change according to time
            appointmentListData = getSorted(formatter.format(calendar.getTime()), appointmentListData);
            adapterHomeAppointment.notifyList(appointmentListData);
            businessAdapter.refreshEventIdList();
            businessAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public ArrayList<AppointmentMappingModel> getAppointmentByDay(String Date) throws ParseException {

        ArrayList<AppointmentMappingModel> appointmentModels;
        ArrayList<AppointmentMappingModel> tempAppointmentModels;
        LinkedHashMap<String, ArrayList<AppointmentMappingModel>> finaldata = new LinkedHashMap<String, ArrayList<AppointmentMappingModel>>();

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        String f11 = Date + " 12:01 AM";
        String f22 = Date + " 11:59 PM";

        long fff = convertTimeStamp_new(f11);
        long lll = convertTimeStamp_new(f22);

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

//            currentTS = System.currentTimeMillis() - 1000;
//            currentTS_end = (convertTimeStamp(String.valueOf(baseCalendar.get(Calendar.DAY_OF_MONTH)) + " " + String.valueOf((baseCalendar.get(Calendar.MONTH) + 1)) + " " + String.valueOf(baseCalendar.get(Calendar.YEAR)) + " 11:59 PM"));

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
            ImageView call = (ImageView) view.findViewById(R.id.call);
            ImageView addEvent = (ImageView) view.findViewById(R.id.addEvent);
            cardView = (CardView) view.findViewById(R.id.cardViewWidget);
            LinearLayout middleContainer = (LinearLayout) view.findViewById(R.id.middleContainer);
            LinearLayout lowerContainer = (LinearLayout) view.findViewById(R.id.lowerContainer);
            Picasso.with(getActivity()).load(mBusinessDataListings.get(i).getThumbnail()).placeholder(R.drawable.thumbnail_eventsxxhdpi).into(articleImage);
            title.setText(mBusinessDataListings.get(i).getName());
            ageGroup.setText(mBusinessDataListings.get(i).getAgegroup_text() + " years");
            address.setText(mBusinessDataListings.get(i).getLocality());
            Calendar cal = Calendar.getInstance();
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
                    String businessId = null;

                    businessId = mBusinessDataListings.get(finalI).getId();
                    intent5.putExtra(Constants.CATEGORY_ID, SharedPrefUtils.getEventIdForCity(getActivity()));
                    intent5.putExtra(Constants.BUSINESS_OR_EVENT_ID, businessId);
                    intent5.putExtra(Constants.PAGE_TYPE, Constants.EVENT_PAGE_TYPE);
                    intent5.putExtra(Constants.DISTANCE, mBusinessDataListings.get(finalI).getDistance());
                    startActivity(intent5);

                }
            });
            final int finalI1 = i;
            addEvent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mBusinessDataListings.get(finalI1).isEventAdded()) {

                        ToastUtils.showToast(getActivity(), getActivity().getResources().getString(R.string.event_added));
                    } else {
                        Intent i = new Intent(getActivity(), ActivityCreateAppointment.class);
                        i.putExtra(Constants.BUSINESS_OR_EVENT_ID, mBusinessDataListings.get(finalI1).getId());
                        i.putExtra(Constants.EVENT_NAME, mBusinessDataListings.get(finalI1).getName());
                        i.putExtra(Constants.EVENT_DES, mBusinessDataListings.get(finalI1).getDescription());
                        i.putExtra(Constants.EVENT_LOCATION, mBusinessDataListings.get(finalI1).getLocality());
                        i.putExtra(Constants.EVENT_START_DATE, mBusinessDataListings.get(finalI1).getStart_date());
                        i.putExtra(Constants.EVENT_END_DATE, mBusinessDataListings.get(finalI1).getEnd_date());
                        Utils.pushEvent(getActivity(), GTMEventType.EVENTLIST_PLUS_CLICKED_EVENT, SharedPrefUtils.getUserDetailModel(getActivity()).getId() + "", "Upcoming Events");
                        getActivity().startActivity(i);
                    }
                }
            });
            final int finalI2 = i;
            call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + mBusinessDataListings.get(finalI2).getPhone()));
                    Utils.pushEvent(getActivity(), GTMEventType.CALL_RESOURCES_CLICKED_EVENT, SharedPrefUtils.getUserDetailModel(getActivity()).getId() + "", "Dashboard");

                    startActivity(intent);
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
}