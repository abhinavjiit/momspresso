package com.mycity4kids.ui.activity;

import android.accounts.NetworkErrorException;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.request.NotificationReadRequest;
import com.mycity4kids.models.response.NotificationCenterListResponse;
import com.mycity4kids.models.response.NotificationCenterResult;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.NotificationsAPI;
import com.mycity4kids.ui.adapter.NotificationCenterListAdapter;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by hemant on 21/12/16.
 */
public class NotificationCenterListActivity extends BaseActivity {

    private ListView notificationListView;
    private Toolbar toolbar;
    private ProgressBar progressBar;
    private TextView noBlogsTextView;
    private RelativeLayout mLodingView;

    private NotificationCenterListAdapter notificationCenterListAdapter;
    private ArrayList<NotificationCenterResult> notificationCenterResultArrayList;

    private String paginationValue = "";
    private boolean isLastPageReached = false;
    private boolean isReuqestRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification_center_activity);
        Utils.pushOpenScreenEvent(NotificationCenterListActivity.this, "Notification Center List", SharedPrefUtils.getUserDetailModel(this).getDynamoId() + "");
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.icon_close);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("NOTIFICATION");
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        mLodingView = (RelativeLayout) findViewById(R.id.relativeLoadingView);
        noBlogsTextView = (TextView) findViewById(R.id.noBlogsTextView);

        notificationCenterResultArrayList = new ArrayList<>();
        notificationListView = (ListView) findViewById(R.id.notificationListView);
        notificationCenterListAdapter = new NotificationCenterListAdapter(this, notificationCenterResultArrayList);
        notificationListView.setAdapter(notificationCenterListAdapter);

        getNotificationFromAPI();
        notificationListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                boolean loadMore = firstVisibleItem + visibleItemCount >= totalItemCount;
                if (visibleItemCount != 0 && loadMore && firstVisibleItem != 0 && !isReuqestRunning && !isLastPageReached) {
                    mLodingView.setVisibility(View.VISIBLE);
                    getNotificationFromAPI();
                    isReuqestRunning = true;
                }
            }
        });

        notificationListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
    }

    private void getNotificationFromAPI() {
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        NotificationsAPI notificationsAPI = retrofit.create(NotificationsAPI.class);
        Call<NotificationCenterListResponse> call = notificationsAPI.getNotificationCenterList(SharedPrefUtils.getUserDetailModel(this).getDynamoId(), 10, paginationValue);
        progressBar.setVisibility(View.VISIBLE);
        call.enqueue(notificationCenterResponseCallback);

    }

    private Callback<NotificationCenterListResponse> notificationCenterResponseCallback = new Callback<NotificationCenterListResponse>() {
        @Override
        public void onResponse(Call<NotificationCenterListResponse> call, retrofit2.Response<NotificationCenterListResponse> response) {
            progressBar.setVisibility(View.GONE);
            mLodingView.setVisibility(View.GONE);
            isReuqestRunning = false;
            if (response == null || null == response.body()) {
                NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                Crashlytics.logException(nee);
                showToast("Something went wrong from server");
                return;
            }
            try {
                NotificationCenterListResponse responseData = (NotificationCenterListResponse) response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    processResponse(responseData);
//                    notificationCenterResultArrayList.addAll(responseData.getData().getResult());
//                    notificationCenterListAdapter.notifyDataSetChanged();
                } else {
                    showToast(responseData.getReason());
                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
                showToast(getString(R.string.went_wrong));
            }
        }

        @Override
        public void onFailure(Call<NotificationCenterListResponse> call, Throwable t) {
            progressBar.setVisibility(View.GONE);
            mLodingView.setVisibility(View.GONE);
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
            showToast(getString(R.string.went_wrong));
        }
    };


    private void processResponse(NotificationCenterListResponse responseData) {
        //	parentingResponse = responseData ;
        try {
            ArrayList<NotificationCenterResult> dataList = responseData.getData().getResult();

            if (dataList.size() == 0) {

                isLastPageReached = true;
                if (null != notificationCenterResultArrayList && !notificationCenterResultArrayList.isEmpty()) {
                    //No more next results for search from pagination

                } else {
                    // No results for search
                    notificationCenterResultArrayList.clear();
                    notificationCenterResultArrayList.addAll(dataList);
//                    notificationCenterListAdapter.setNewListData(dataList);
                    notificationCenterListAdapter.notifyDataSetChanged();
                    noBlogsTextView.setVisibility(View.VISIBLE);
                    noBlogsTextView.setText("No Notifications");
                }
            } else {
                noBlogsTextView.setVisibility(View.GONE);
                if ("".equals(paginationValue)) {
//                    notificationCenterResultArrayList = dataList;
                    notificationCenterResultArrayList.clear();
                    notificationCenterResultArrayList.addAll(dataList);
                } else {
                    notificationCenterResultArrayList.addAll(dataList);
                }

                if (null == responseData.getData().getPagination()) {
                    isLastPageReached = true;
                    paginationValue = "";
                } else {
                    paginationValue = responseData.getData().getPagination().getId() + "_" + responseData.getData().getPagination().getCreatedTime();
                }
//                notificationCenterListAdapter.setNewListData(notificationCenterResultArrayList);
                notificationCenterListAdapter.notifyDataSetChanged();
            }
        } catch (Exception ex) {
            Crashlytics.logException(ex);
            Log.d("MC4kException", Log.getStackTraceString(ex));
        }
    }

    @Override
    protected void updateUi(Response response) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_notification_center, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.markRead:
                Log.d("Notification Center", "Mark all as read");
                markAllNotificationAsRead();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }

    private void markAllNotificationAsRead() {
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        NotificationsAPI notificationsAPI = retrofit.create(NotificationsAPI.class);

        NotificationReadRequest notificationReadRequest = new NotificationReadRequest();
        notificationReadRequest.setReadAll("1");

        Call<NotificationCenterListResponse> call = notificationsAPI.markNotificationAsRead(notificationReadRequest);
//        progressBar.setVisibility(View.VISIBLE);
        call.enqueue(allNotificationReadResponseListener);
    }

    private Callback<NotificationCenterListResponse> allNotificationReadResponseListener = new Callback<NotificationCenterListResponse>() {
        @Override
        public void onResponse(Call<NotificationCenterListResponse> call, retrofit2.Response<NotificationCenterListResponse> response) {
            if (response == null || null == response.body()) {
                NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                Crashlytics.logException(nee);
                showToast("Something went wrong from server");
                return;
            }
            try {
                NotificationCenterListResponse responseData = (NotificationCenterListResponse) response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    paginationValue = "";
                    getNotificationFromAPI();
                } else {
                    showToast(responseData.getReason());
                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
//                showToast(getString(R.string.went_wrong));
            }
        }

        @Override
        public void onFailure(Call<NotificationCenterListResponse> call, Throwable t) {
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
//            showToast(getString(R.string.went_wrong));
        }
    };
}
