package com.mycity4kids.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseFragment;
import com.kelltontech.utils.ConnectivityUtils;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.controller.BusinessListController;
import com.mycity4kids.controller.ParentingStopController;
import com.mycity4kids.dbtable.TableAppointmentData;
import com.mycity4kids.dbtable.TableKids;
import com.mycity4kids.dbtable.TableTaskData;
import com.mycity4kids.dbtable.TaskCompletedTable;
import com.mycity4kids.enums.ParentingFilterType;
import com.mycity4kids.models.businesslist.BusinessDataListing;
import com.mycity4kids.models.businesslist.BusinessListRequest;
import com.mycity4kids.models.businesslist.BusinessListResponse;
import com.mycity4kids.models.parentingstop.CommonParentingList;
import com.mycity4kids.models.parentingstop.CommonParentingResponse;
import com.mycity4kids.models.parentingstop.ParentingRequest;
import com.mycity4kids.models.user.KidsInfo;
import com.mycity4kids.newmodels.AppointmentMappingModel;
import com.mycity4kids.newmodels.AttendeeModel;
import com.mycity4kids.newmodels.TaskDataModel;
import com.mycity4kids.newmodels.TaskMappingModel;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.ui.activity.ActivityCreateAppointment;
import com.mycity4kids.ui.activity.ActivityCreateTask;
import com.mycity4kids.ui.activity.ActivityShowAppointment;
import com.mycity4kids.ui.activity.ActivityShowTask;
import com.mycity4kids.ui.activity.ArticlesAndBlogsDetailsActivity;
import com.mycity4kids.ui.activity.BusinessDetailsActivity;
import com.mycity4kids.ui.activity.DashboardActivity;
import com.mycity4kids.ui.adapter.AdapterHomeAppointment;
import com.mycity4kids.ui.adapter.AdapterHomeTask;
import com.mycity4kids.ui.adapter.ArticlesListingAdapter;
import com.mycity4kids.ui.adapter.BusinessListingAdapterevent;
import com.mycity4kids.widget.CustomListView;

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

/**
 * Created by manish.soni on 17-06-2015.
 */
public class FragmentMC4KHome extends BaseFragment implements View.OnClickListener {

    View view;
    CustomListView appointmentList, taskList;
    TableAppointmentData tableAppointment;
    ArrayList<AppointmentMappingModel> appointmentListData;
    AdapterHomeAppointment adapterHomeAppointment;
    TextView goToCal, current, goToTask, goToBlogs;
    ImageView imgGoToCal, imgGoToTodo, imgGoToEvents, imgGoToBlogs;
    ImageView addAppointment, addTask;
    java.sql.Timestamp firsttamp;
    AdapterHomeTask adapterHomeTask;
    ScrollView baseScroll;
    private ProgressBar progressBar, blogProgessBar;
    private BusinessListingAdapterevent businessAdapter;
    private ArticlesListingAdapter articlesListingAdapter;
    private int mBusinessListCount = 1;
    private int mTotalPageCount = 0;
    private int mPageCount = 1;
    private int businessOrEventType;
    private ArrayList<BusinessDataListing> mBusinessDataListings;
    private ArrayList<CommonParentingList> mArticleDataListing;
    private CustomListView eventListView, blogListView;
    private View rltLoadingView;
    private boolean mIsRequestRunning;
    private boolean mEventDataAvalble;
    TextView txtCal, txtTodo, txtEvents, txtBlogs;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.aa_mc4k_home, container, false);
        appointmentList = (CustomListView) view.findViewById(R.id.home_appointmentList);
        goToCal = (TextView) view.findViewById(R.id.go_to_cal);
        addAppointment = (ImageView) view.findViewById(R.id.add_appointment);
        current = (TextView) view.findViewById(R.id.current_date);
        goToTask = (TextView) view.findViewById(R.id.go_to_task);
        baseScroll = (ScrollView) view.findViewById(R.id.base_scroll);
        imgGoToCal = (ImageView) view.findViewById(R.id.img_go_to_cal);
        imgGoToTodo = (ImageView) view.findViewById(R.id.img_go_to_todo);
        imgGoToEvents = (ImageView) view.findViewById(R.id.img_go_to_events);
        imgGoToBlogs = (ImageView) view.findViewById(R.id.img_go_to_blogs);
        txtCal = (TextView) view.findViewById(R.id.txtCal);
        txtTodo = (TextView) view.findViewById(R.id.txtTodo);
        txtEvents = (TextView) view.findViewById(R.id.txtEvents);
        txtBlogs = (TextView) view.findViewById(R.id.txtBlogs);


        addTask = (ImageView) view.findViewById(R.id.add_task);
        progressBar = (ProgressBar) view.findViewById(R.id.eventprogressbar);
        blogProgessBar = (ProgressBar) view.findViewById(R.id.blogprogressbar);
        taskList = (CustomListView) view.findViewById(R.id.home_taskList);
        eventListView = (CustomListView) view.findViewById(R.id.eventList);
        blogListView = (CustomListView) view.findViewById(R.id.bloglist);
        rltLoadingView = (RelativeLayout) view.findViewById(R.id.rltLoadingView);
        goToTask.setOnClickListener(this);
        addTask.setOnClickListener(this);
        goToCal.setOnClickListener(this);
        addAppointment.setOnClickListener(this);
        imgGoToCal.setOnClickListener(this);
        imgGoToTodo.setOnClickListener(this);
        imgGoToEvents.setOnClickListener(this);
        imgGoToBlogs.setOnClickListener(this);
        txtCal.setOnClickListener(this);
        txtTodo.setOnClickListener(this);
        txtEvents.setOnClickListener(this);
        txtBlogs.setOnClickListener(this);

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
                    Intent appointmentIntent = new Intent(getActivity(), ActivityCreateAppointment.class);
                    startActivity(appointmentIntent);

                }
            });
        }

        TableTaskData tTask = new TableTaskData(BaseApplication.getInstance());
        List<TaskDataModel.TaskDetail> allTaskList = tTask.getAll();
        if (null != allTaskList && allTaskList.size() == 0) {
            goToTask.setText("ADD A TASK");
            goToTask.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent TaskIntent = new Intent(getActivity(), ActivityCreateTask.class);
                    startActivity(TaskIntent);

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

        taskList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                TaskMappingModel taskMappingModel = (TaskMappingModel) adapterHomeTask.getItem(i);

                if (taskMappingModel.getTaskName() == null) {

                } else {
                    ((DashboardActivity) getActivity()).UploadCompleteTasks();

                    Intent intent = new Intent(getActivity(), ActivityShowTask.class);
                    intent.putExtra(AppConstants.EXTRA_TASK_ID, taskMappingModel.getTask_id());
                    startActivityForResult(intent, 1);
                }

            }
        });

//        eventListView.setOnScrollListener(new AbsListView.OnScrollListener() {
//
//            @Override
//            public void onScrollStateChanged(AbsListView view, int scrollState) {
//                // TODO Auto-generated method stub
//
//            }
//
//            @Override
//            public void onScroll(AbsListView view, int firstVisibleItem,
//                                 int visibleItemCount, int totalItemCount) {
//
//                if (!mEventDataAvalble) {
//                    if (Constants.IS_PAGE_AVAILABLE) {
//                        if (view.getCount() < mBusinessListCount) {
//
//                            boolean loadMore = firstVisibleItem + visibleItemCount >= totalItemCount;
//                            if (visibleItemCount != 0 && loadMore && !mIsRequestRunning && firstVisibleItem != 0) {
//                                rltLoadingView.setVisibility(View.VISIBLE);
//
//                                int currentPageCount = ++mPageCount;
//                                if (mTotalPageCount >= currentPageCount) {
//                                    hitBusinessListingApi(categoryId, currentPageCount);
//                                } else {
//                                    rltLoadingView.setVisibility(View.GONE);
//                                }
//                            }
//                        }
//                    }
//
//
//                }
//
//            }
//        });


        eventListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                Intent intent = new Intent(getActivity(), BusinessDetailsActivity.class);
                String businessId = null;
                if (parent.getAdapter() instanceof BusinessListingAdapterevent) {
                    BusinessDataListing businessListData = (BusinessDataListing) ((BusinessListingAdapterevent) parent.getAdapter()).getItem(pos);
                    businessId = businessListData.getId();
                    intent.putExtra(Constants.CATEGORY_ID, SharedPrefUtils.getEventIdForCity(getActivity()));
                    intent.putExtra(Constants.BUSINESS_OR_EVENT_ID, businessId);
                    intent.putExtra(Constants.PAGE_TYPE, Constants.EVENT_PAGE_TYPE);
                    intent.putExtra(Constants.DISTANCE, businessListData.getDistance());
                    startActivity(intent);
                }

            }
        });
        blogListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent intent = new Intent(getActivity(), ArticlesAndBlogsDetailsActivity.class);
                if (adapterView.getAdapter() instanceof ArticlesListingAdapter) {
                    CommonParentingList parentingListData = (CommonParentingList) ((ArticlesListingAdapter) adapterView.getAdapter()).getItem(i);
                    intent.putExtra(Constants.ARTICLE_ID, parentingListData.getId());
                    intent.putExtra(Constants.ARTICLE_COVER_IMAGE, parentingListData.getThumbnail_image());
                    intent.putExtra(Constants.PARENTING_TYPE, ParentingFilterType.ARTICLES);
                    intent.putExtra(Constants.FILTER_TYPE, parentingListData.getAuthor_type());
                    intent.putExtra(Constants.BLOG_NAME, parentingListData.getBlog_name());
                    startActivity(intent);

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
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        formatter.setTimeZone(TimeZone.getTimeZone("GMT+0530"));

        tableAppointment = new TableAppointmentData(BaseApplication.getInstance());

        try {
            appointmentListData = getAppointmentByDay(currentDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        adapterHomeTask = new AdapterHomeTask(getActivity(), getCurrentDateTask());
        taskList.setAdapter(adapterHomeTask);

        appointmentListData = getSorted(formatter.format(calendar.getTime()), appointmentListData);

        adapterHomeAppointment = new AdapterHomeAppointment(getActivity(), appointmentListData);
        appointmentList.setAdapter(adapterHomeAppointment);

        // blog adapter
        mArticleDataListing = new ArrayList<>();
        articlesListingAdapter = new ArticlesListingAdapter(getActivity(), true);
        blogListView.setAdapter(articlesListingAdapter);

        if (BaseApplication.getBlogResponse() == null || BaseApplication.getBlogResponse().isEmpty()) {

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {

                    hitBlogListingApi();
                }
            });
            thread.start();
        } else {
            mArticleDataListing.addAll(BaseApplication.getBlogResponse());
            articlesListingAdapter.setNewListData(mArticleDataListing);
            articlesListingAdapter.notifyDataSetChanged();
        }

        if (!SharedPrefUtils.isCityFetched(getActivity())) {
            view.findViewById(R.id.eventsss).setVisibility(View.GONE);

            // view.findViewById(R.id.blogss).setVisibility(View.GONE);
        } else {
            view.findViewById(R.id.eventsss).setVisibility(View.VISIBLE);
            view.findViewById(R.id.blogss).setVisibility(View.VISIBLE);


            // hit business list api
            businessAdapter = new BusinessListingAdapterevent(getActivity());

            mBusinessDataListings = new ArrayList<>();

            eventListView.setAdapter(businessAdapter);

            if (BaseApplication.getBusinessREsponse() == null || BaseApplication.getBusinessREsponse().isEmpty()) {

                hitBusinessListingApi(SharedPrefUtils.getEventIdForCity(getActivity()), 1);
//                Thread thread = new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//
//                        hitBusinessListingApi(SharedPrefUtils.getEventIdForCity(getActivity()), 1);
//                    }
//                });
                // thread.start();
            } else {
                mEventDataAvalble = true;
                mBusinessDataListings.addAll(BaseApplication.getBusinessREsponse());
                businessAdapter.setListData(mBusinessDataListings, businessOrEventType);
                businessAdapter.notifyDataSetChanged();
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
            progressBar.setVisibility(View.VISIBLE);
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


                // previously age group logic commented
//                for (int j = 0; j < mAgeGroupList.size(); j++) {
//
//                    if (calcAgeGroup(age, mAgeGroupList.get(j).getValue())) {
//                        selectedageGroups.add(mAgeGroupList.get(j).getKey());
//                    }
//
//                }


            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        // new logic

//        String finalString = "&age[]=";
//
//        boolean flag = false;
//        for (int i = 0; i < ageArray.size(); i++) {
//
//            if (flag) {
//                finalString = finalString + "&age[]=";
//            }
//
//            finalString = finalString + ageArray.get(i).toString();
//            flag = true;
//
//        }


        // previously age group logic commented

//        Object[] myArr = selectedageGroups.toArray();

//        String finalString = "&age[]=";
//
//        boolean flag = false;
//        for (int i = 0; i < myArr.length; i++) {
//
//            if (flag) {
//                finalString = finalString + "&age[]=";
//            }
//
//            finalString = finalString + myArr[i].toString();
//            flag = true;
//
//        }
        BusinessListController businessListController = new BusinessListController(getActivity(), this);
        BusinessListRequest businessListRequest = new BusinessListRequest();
        businessListRequest.setCategory_id(categoryId + "");
        businessListRequest.setDate_by("onlytoday");
//        if (myArr.length > 0)
//            businessListRequest.setAge_group(finalString);
        businessListRequest.setCity_id((SharedPrefUtils.getCurrentCityModel(getActivity())).getId() + "");
        businessListRequest.setPage(page + "");
        businessListController.getData(AppConstants.BUSINESS_LIST_REQUEST, businessListRequest);

        mIsRequestRunning = true;

    }

    public void hitBlogListingApi() {
        if (!ConnectivityUtils.isNetworkEnabled(getActivity())) {
            return;
        }
        blogProgessBar.setVisibility(View.VISIBLE);
        ParentingRequest _parentingModel = new ParentingRequest();
        _parentingModel.setCity_id(SharedPrefUtils.getCurrentCityModel(getActivity()).getId());
        _parentingModel.setPage("1");

        ParentingStopController _controller = new ParentingStopController(getActivity(), this);
        _controller.getData(AppConstants.ARTICLES_TODAY_REQUEST, _parentingModel);


    }

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
                    mBusinessDataListings.addAll(responseData.getResult().getData().getData());

                    BaseApplication.setBusinessREsponse(mBusinessDataListings);

                    businessAdapter.setListData(mBusinessDataListings, businessOrEventType);

                    businessAdapter.notifyDataSetChanged();

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

                    mArticleDataListing.addAll(responseBlogData.getResult().getData().getData());
                    BaseApplication.setBlogResponse(mArticleDataListing);
                    articlesListingAdapter.setNewListData(mArticleDataListing);
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

            //case R.id.blogs:
//                ((DashboardActivity) getActivity()).replaceFragment(new ParentingBlogFragment(), null, true);

            // break;
            case R.id.go_to_cal:
            case R.id.img_go_to_cal:
            case R.id.txtCal:
                ((DashboardActivity) getActivity()).replaceFragment(new FragmentCalender(), null, true);

                break;

            case R.id.add_appointment:


                intent = new Intent(getActivity(), ActivityCreateAppointment.class);
                startActivity(intent);
//                getActivity().getFragmentManager().beginTransaction().remove(getActivity().getApplicationContext()).commit();

                break;

            case R.id.go_to_task:
            case R.id.img_go_to_todo:
            case R.id.txtTodo:

                ((DashboardActivity) getActivity()).setTitle("All Task");
                ((DashboardActivity) getActivity()).replaceFragment(new FragmentTaskHome(), null, true);

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
                ((DashboardActivity) getActivity()).replaceFragment(new ArticlesFragment(), null, true);


                break;
//            case R.id.no_events:
//                Constants.IS_SEARCH_LISTING = false;
//                fragment = new FragmentBusinesslistEvents();
//                bundle = new Bundle();
//                bundle.putInt(Constants.PAGE_TYPE, Constants.EVENT_PAGE_TYPE);
//                bundle.putInt(Constants.EXTRA_CATEGORY_ID, SharedPrefUtils.getEventIdForCity(getActivity()));
//                bundle.putString(Constants.CATEGOTY_NAME, "Events & workshop");
//                fragment.setArguments(bundle);
//                ((DashboardActivity) getActivity()).replaceFragment(fragment, bundle, true);
//                break;
//            case R.id.no_blog:
//                ((DashboardActivity) getActivity()).replaceFragment(new ArticlesFragment(), null, true);
//
//
//                break;


            case R.id.add_task:

                intent = new Intent(getActivity(), ActivityCreateTask.class);
                startActivity(intent);

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

        Calendar calendar = Calendar.getInstance();
        tableAppointment = new TableAppointmentData(BaseApplication.getInstance());
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        appointmentListData = getAppointmentByDay(formatter.format(calendar.getTime()));
        try {
            // first change according to time
            appointmentListData = getSorted(formatter.format(calendar.getTime()), appointmentListData);
            adapterHomeAppointment.notifyList(appointmentListData);
            // refresh tasks list here also
            adapterHomeTask = new AdapterHomeTask(getActivity(), getCurrentDateTask());
            taskList.setAdapter(adapterHomeTask);

            // refresh events also
            // checking whether event is added or not

            // BaseApplication.setBusinessREsponse(setEventAddedFlag(BaseApplication.getBusinessREsponse()));
            // notify adapter
            // businessAdapter.setListData(BaseApplication.getBusinessREsponse(), businessOrEventType);
            // businessAdapter.setListData(BaseApplication.getBusinessREsponse(), businessOrEventType);
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

//        formatter.setTimeZone(TimeZone.getDefault());

        String f11 = Date + " 12:01 AM";
        String f22 = Date + " 11:59 PM";

        long fff = convertTimeStamp_new(f11);
        long lll = convertTimeStamp_new(f22);

//        Date firstDate = formatter.parse(Date);
//        Date lastDate = formatter.parse(Date);
//
//        java.sql.Timestamp firsttamp = new java.sql.Timestamp(firstDate.getTime());
//        java.sql.Timestamp laststamp = new java.sql.Timestamp(lastDate.getTime());
//
//        long first = firsttamp.getTime();
//        long last = laststamp.getTime() + 86280000;

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
            taskList.setOnItemClickListener(null);
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
        adapterHomeTask.notifyTaskList(getCurrentDateTask());
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

//    old function
//    public ArrayList<TaskMappingModel> getDatafromDB(List<String> allDays, ArrayList<TaskMappingModel> taskModels) throws ParseException {
//
//        ArrayList<TaskMappingModel> finalTaskDataModel = new ArrayList<>();
//
//        ArrayList<TaskMappingModel> finalTaskDataModelList = new ArrayList<>();
//
//        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
//
//        for (int i = 0; i < allDays.size(); i++) {
//
//            try {
//
//                finalTaskDataModel = new ArrayList<>();
//
//                for (int j = 0; j < taskModels.size(); j++) {
//                    java.sql.Timestamp tempTimestamp = new java.sql.Timestamp(formatter.parse(allDays.get(i)).getTime());
//
//                    // validation of start date
//
//                    if (checkCurrentDateValid(allDays.get(i), taskModels.get(j).getTaskDate())) {
//
//
//                        if (taskModels.get(j).getTaskDate() >= tempTimestamp.getTime() && taskModels.get(j).getTaskDate() <= (tempTimestamp.getTime() + 86280000)) {
//                            taskModels.get(j).setShowDate(allDays.get(i));
//                            finalTaskDataModel.add(taskModels.get(j));
//
//                        } else {
//                            // condition until check pick date
//
//                            if (!StringUtils.isNullOrEmpty(taskModels.get(j).getRepeate_untill())) {
//                                if (taskModels.get(j).getRepeate_untill().equalsIgnoreCase("forever")) {
//                                    if (taskModels.get(j).getRepeat().equalsIgnoreCase("No Repeat")) {
//
//                                    } else if (taskModels.get(j).getRepeat().equalsIgnoreCase("Days")) {
//
//                                        String[] daysArray = taskModels.get(j).getRepeate_frequency().split(",");
//                                        for (String day : daysArray) {
//                                            boolean result = chkDays(day, allDays.get(i));
//                                            if (result) {
//                                                taskModels.get(j).setShowDate(allDays.get(i));
//                                                finalTaskDataModel.add(taskModels.get(j));
//                                            }
//
//                                        }
//
//
//                                    } else if (taskModels.get(j).getRepeat().equalsIgnoreCase("Daily")) {
//                                        taskModels.get(j).setShowDate(allDays.get(i));
//                                        finalTaskDataModel.add(taskModels.get(j));
//                                    } else if (taskModels.get(j).getRepeat().equalsIgnoreCase("Weekly")) {
//                                        // check start date
//                                        long starttime = taskModels.get(j).getTaskDate();
//                                        boolean result = getValues(starttime, "weekly", allDays.get(i));
//                                        if (result) {
//
//                                            taskModels.get(j).setShowDate(allDays.get(i));
//                                            finalTaskDataModel.add(taskModels.get(j));
//                                        }
//                                    } else if (taskModels.get(j).getRepeat().equalsIgnoreCase("Monthly")) {
//
//                                        long starttime = taskModels.get(j).getTaskDate();
//                                        boolean result = getValues(starttime, "monthly", allDays.get(i));
//                                        if (result) {
//                                            taskModels.get(j).setShowDate(allDays.get(i));
//                                            finalTaskDataModel.add(taskModels.get(j));
//                                        }
//                                    } else if (taskModels.get(j).getRepeat().equalsIgnoreCase("Yearly")) {
//
//                                        long starttime = taskModels.get(j).getTaskDate();
//                                        boolean result = getValues(starttime, "yearly", allDays.get(i));
//                                        if (result) {
//                                            taskModels.get(j).setShowDate(allDays.get(i));
//                                            finalTaskDataModel.add(taskModels.get(j));
//                                        }
//                                    } else if (taskModels.get(j).getRepeat().equalsIgnoreCase("Other")) {
//
//
//                                        long starttime = taskModels.get(j).getTaskDate();
//                                        boolean result = getOtherValues(starttime, taskModels.get(j).getRepeate_frequency(), allDays.get(i), Integer.parseInt(taskModels.get(j).getRepeate_num()));
//                                        if (result) {
//                                            taskModels.get(j).setShowDate(allDays.get(i));
//                                            finalTaskDataModel.add(taskModels.get(j));
//                                        }
//                                    }
//
//                                } else {
//                                    String pickdate = taskModels.get(j).getRepeate_untill();
//                                    String currentdate = allDays.get(i);
//
//                                    SimpleDateFormat f1 = new SimpleDateFormat("yyyy-MM-dd");
//                                    SimpleDateFormat f2 = new SimpleDateFormat("dd MMMM yyyy");
//
//                                    Date dateCurrent = new Date();
//                                    dateCurrent = (Date) f1.parse(currentdate);
//
//                                    Date dateUntil = (Date) f2.parse(pickdate);
//                                    String untilFinal = f1.format(dateUntil);
//
//                                    Calendar calendarCurrent = Calendar.getInstance(TimeZone.getTimeZone("GMT+0530"));
//                                    calendarCurrent.clear();
//                                    calendarCurrent.setTime(dateCurrent);
//                                    calendarCurrent.set(Calendar.HOUR_OF_DAY, 23);
//                                    calendarCurrent.set(Calendar.MINUTE, 58);
//
//                                    Calendar calendarUntil = Calendar.getInstance(TimeZone.getTimeZone("GMT+0530"));
//                                    calendarUntil.clear();
//                                    calendarUntil.setTime(dateUntil);
//                                    calendarUntil.set(Calendar.HOUR_OF_DAY, 23);
//                                    calendarUntil.set(Calendar.MINUTE, 58);
//
//                                    if (calendarCurrent.getTimeInMillis() <= calendarUntil.getTimeInMillis()) {
//
//                                        if (taskModels.get(j).getRepeat().equalsIgnoreCase("No Repeat")) {
//
//                                        } else if (taskModels.get(j).getRepeat().equalsIgnoreCase("Days")) {
//
//                                            String[] daysArray = taskModels.get(j).getRepeate_frequency().split(",");
//                                            for (String day : daysArray) {
//                                                boolean result = chkDays(day, allDays.get(i));
//                                                if (result) {
//                                                    taskModels.get(j).setShowDate(allDays.get(i));
//                                                    finalTaskDataModel.add(taskModels.get(j));
//                                                }
//                                            }
//
//                                        } else if (taskModels.get(j).getRepeat().equalsIgnoreCase("Daily")) {
//                                            taskModels.get(j).setShowDate(allDays.get(i));
//                                            finalTaskDataModel.add(taskModels.get(j));
//                                        } else if (taskModels.get(j).getRepeat().equalsIgnoreCase("Weekly")) {
//                                            // check start date
//                                            long starttime = taskModels.get(j).getTaskDate();
//                                            boolean result = getValues(starttime, "weekly", allDays.get(i));
//                                            if (result) {
//                                                taskModels.get(j).setShowDate(allDays.get(i));
//                                                finalTaskDataModel.add(taskModels.get(j));
//                                            }
//                                        } else if (taskModels.get(j).getRepeat().equalsIgnoreCase("Monthly")) {
//
//                                            long starttime = taskModels.get(j).getTaskDate();
//                                            boolean result = getValues(starttime, "monthly", allDays.get(i));
//                                            if (result) {
//                                                taskModels.get(j).setShowDate(allDays.get(i));
//                                                finalTaskDataModel.add(taskModels.get(j));
//                                            }
//                                        } else if (taskModels.get(j).getRepeat().equalsIgnoreCase("Yearly")) {
//
//                                            long starttime = taskModels.get(j).getTaskDate();
//                                            boolean result = getValues(starttime, "yearly", allDays.get(i));
//                                            if (result) {
//                                                taskModels.get(j).setShowDate(allDays.get(i));
//                                                finalTaskDataModel.add(taskModels.get(j));
//                                            }
//                                        } else if (taskModels.get(j).getRepeat().equalsIgnoreCase("Other")) {
//
//
//                                            long starttime = taskModels.get(j).getTaskDate();
//                                            boolean result = getOtherValues(starttime, taskModels.get(j).getRepeate_frequency(), allDays.get(i), Integer.parseInt(taskModels.get(j).getRepeate_num()));
//                                            if (result) {
//                                                taskModels.get(j).setShowDate(allDays.get(i));
//                                                finalTaskDataModel.add(taskModels.get(j));
//                                            }
//                                        }
//
//                                    }
//                                }
//                            }
//
//                        }
//
//
//                    }
//
//
//                }
//                if (finalTaskDataModel.size() == 0) {
//                } else {
//                    finalTaskDataModelList.addAll(finalTaskDataModel);
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//
//        }
//
//        return finalTaskDataModelList;
//    }

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

}