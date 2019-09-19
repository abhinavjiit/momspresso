package com.mycity4kids.ui.fragment;

import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.Utils;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseFragment;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.models.response.BloggerAnalyticsResponse;
import com.mycity4kids.models.response.BloggerAnalyticsViews;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.BloggerDashboardAPI;
import com.mycity4kids.ui.activity.RankingActivity;
import com.mycity4kids.widget.MyMarkerView;
import com.mycity4kids.widget.PageViewsDatePickerFragment;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by hemant on 28/7/17.
 */
public class RankingStatsTabFragment extends BaseFragment implements OnChartGestureListener, OnChartValueSelectedListener, View.OnClickListener, PageViewsDatePickerFragment.IDateSelection {

    private static String userId;
    private LineChart mChart;
    private XAxis xAxis;
    private String init_from_date, init_to_date;
    private ArrayList<String> datesList;
    private int colorCode;
    private String toDate;
    private String fromDate;
    private Calendar toCalen;
    private Calendar fromCalen;
    private String pageViewLabel;

    private BloggerDashboardAPI bloggerDashboardAPI;

    private View view;
    private TextView likesCountTextView;
    private TextView shareCountTextView;
    private TextView commentsCountTextView;
    private TextView pageViewCountTextView;
    private TextView fromDateTextView;
    private TextView toDateTextView;
    private TextView dateChooserTextView;
    private RelativeLayout customDatePickerView;
    private TextView improvePageViewTextView, improveSocialShareTextView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.ranking_stats_tab_fragment, container, false);

//        daysCountTextView = (TextView) view.findViewById(R.id.daysCount);
        likesCountTextView = (TextView) view.findViewById(R.id.likeCountTV);
        shareCountTextView = (TextView) view.findViewById(R.id.shareCountTV);
        commentsCountTextView = (TextView) view.findViewById(R.id.commentCountTV);
        improvePageViewTextView = (TextView) view.findViewById(R.id.improvePageViewTextView);
        improveSocialShareTextView = (TextView) view.findViewById(R.id.improveSocialShareTextView);
        pageViewCountTextView = (TextView) view.findViewById(R.id.pageViewCountTextView);
        fromDateTextView = (TextView) view.findViewById(R.id.fromDateTextView);
        toDateTextView = (TextView) view.findViewById(R.id.toDateTextView);
        dateChooserTextView = (TextView) view.findViewById(R.id.dateChooserTextView);
        customDatePickerView = (RelativeLayout) view.findViewById(R.id.customDatePickerView);

        dateChooserTextView.setOnClickListener(this);
        fromDateTextView.setOnClickListener(this);
        toDateTextView.setOnClickListener(this);
        improvePageViewTextView.setOnClickListener(this);
        improveSocialShareTextView.setOnClickListener(this);

        fromCalen = Calendar.getInstance();
        fromCalen.set(Calendar.HOUR_OF_DAY, 0);
        fromCalen.set(Calendar.MINUTE, 0);
        fromCalen.set(Calendar.SECOND, 0);
        fromCalen.set(Calendar.MILLISECOND, 0);
        fromCalen.add(Calendar.DATE, -31);
        fromCalen.getTimeInMillis();
        fromDateTextView.setText(new SimpleDateFormat("dd-MM-yyyy").format(fromCalen.getTime()));

        toCalen = Calendar.getInstance();
        toCalen.set(Calendar.HOUR_OF_DAY, 0);
        toCalen.set(Calendar.MINUTE, 0);
        toCalen.set(Calendar.SECOND, 0);
        toCalen.set(Calendar.MILLISECOND, 0);
        toCalen.add(Calendar.DATE, -1);
        toCalen.getTimeInMillis();
        toDateTextView.setText(new SimpleDateFormat("dd-MM-yyyy").format(toCalen.getTime()));

        pageViewLabel = getString(R.string.ranking_page_views_label);

        userId = SharedPrefUtils.getUserDetailModel(getActivity()).getDynamoId();
        String authorId = getArguments().getString("authorId");
        if (!StringUtils.isNullOrEmpty(authorId)) {
            userId = authorId;
        }

        colorCode = ContextCompat.getColor(getActivity(), R.color.analytics_engagement_graph);
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        bloggerDashboardAPI = retrofit.create(BloggerDashboardAPI.class);
//        Call<UserDetailResponse> call = bloggerDashboardAPI.getBloggerData(userId);
//        call.enqueue(bloggerDetailsResponseListener);

        datesList = new ArrayList<>();

        mChart = (LineChart) view.findViewById(R.id.pageViewsChart);

        mChart.getDescription().setEnabled(false);

        mChart.setOnChartGestureListener(this);
        mChart.setOnChartValueSelectedListener(this);
        mChart.setDrawGridBackground(false);

        // no description text
        mChart.getDescription().setEnabled(false);

        // enable touch gestures
        mChart.setTouchEnabled(true);

        // enable scaling and dragging
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);

        // if disabled, scaling can be done on x- and y-axis separately
        mChart.setPinchZoom(true);

        MyMarkerView mv = new MyMarkerView(getActivity(), R.layout.custom_marker_view);
        mv.setChartView(mChart); // For bounds control
        mChart.setMarker(mv);

        xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
        leftAxis.setDrawZeroLine(false);

        // limit lines are drawn behind data (and not on top)
        leftAxis.setDrawLimitLinesBehindData(true);

        mChart.getAxisRight().setEnabled(false);

        create30daysPageViewGraph();

        return view;

    }

    private void create30daysPageViewGraph() {

//        daysCountTextView.setText(getString(R.string.analytics_popup_last30days));
        Calendar fromCal = Calendar.getInstance();
        fromCal.set(Calendar.HOUR_OF_DAY, 0);
        fromCal.set(Calendar.MINUTE, 0);
        fromCal.set(Calendar.SECOND, 0);
        fromCal.set(Calendar.MILLISECOND, 0);
        fromCal.add(Calendar.DATE, -31);
        fromCal.getTimeInMillis();
        init_from_date = new SimpleDateFormat("dd-MM-yyyy").format(fromCal.getTime());

        Calendar toCal = Calendar.getInstance();
        toCal.set(Calendar.HOUR_OF_DAY, 0);
        toCal.set(Calendar.MINUTE, 0);
        toCal.set(Calendar.SECOND, 0);
        toCal.set(Calendar.MILLISECOND, 0);
        toCal.add(Calendar.DATE, -1);
        toCal.getTimeInMillis();
        init_to_date = new SimpleDateFormat("dd-MM-yyyy").format(toCal.getTime());

        Call<BloggerAnalyticsResponse> callAnalytics = bloggerDashboardAPI.getAnalyticsReport(userId, "" + fromCal.getTimeInMillis() / 1000, "" + toCal.getTimeInMillis() / 1000);
        callAnalytics.enqueue(analyticsResponseListener);
    }

    private void setData(ArrayList<BloggerAnalyticsViews> viewsList) {

        ArrayList<Entry> values = new ArrayList<Entry>();

        for (int i = 0; i < viewsList.size(); i++) {
            values.add(new Entry(i, Float.parseFloat(viewsList.get(i).getViews())));
        }

        LineDataSet set1;

        if (mChart.getData() != null &&
                mChart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) mChart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            set1.setDrawValues(false);
            mChart.getData().notifyDataChanged();
            mChart.notifyDataSetChanged();
        } else {
            // create a dataset and give it a type
            set1 = new LineDataSet(values, "Page Views");

            set1.setColor(colorCode);
            set1.setCircleColor(colorCode);
            set1.setLineWidth(1f);
            set1.setCircleRadius(3f);
            set1.setDrawCircleHole(false);
            set1.setValueTextSize(9f);
            set1.setDrawValues(false);

            if (Utils.getSDKInt() >= 18) {
                // fill drawable only supported on api level 18 and above
//                Drawable drawable = ContextCompat.getDrawable(this, R.drawable.add_red);
                set1.setFillDrawable(null);
            } else {
                set1.setFillColor(Color.WHITE);
            }

            ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
            dataSets.add(set1); // add the datasets

            // create a data object with the datasets
            LineData data = new LineData(dataSets);

            // set data
            mChart.setData(data);
        }
    }

    private Callback<BloggerAnalyticsResponse> analyticsResponseListener = new Callback<BloggerAnalyticsResponse>() {
        @Override
        public void onResponse(Call<BloggerAnalyticsResponse> call, retrofit2.Response<BloggerAnalyticsResponse> response) {
            if (response == null || null == response.body()) {
//                showToast("Something went wrong from server");
                return;
            }
            try {
                BloggerAnalyticsResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    if (null == responseData.getData().getSocial().getLikes()) {
                        likesCountTextView.setText("0" + " " + getString(R.string.ranking_stats_like));
                    } else {
                        likesCountTextView.setText(responseData.getData().getSocial().getLikes() + " " + getString(R.string.ranking_stats_like));
                    }

                    if (null == responseData.getData().getSocial().getShare()) {
                        shareCountTextView.setText("0" + " " + getString(R.string.ranking_stats_shares));
                    } else {
                        shareCountTextView.setText(responseData.getData().getSocial().getShare() + " " + getString(R.string.ranking_stats_shares));
                    }

                    if (null == responseData.getData().getSocial().getComment()) {
                        commentsCountTextView.setText("0" + " " + getString(R.string.ranking_stats_comments));
                    } else {
                        commentsCountTextView.setText(responseData.getData().getSocial().getComment() + " " + getString(R.string.ranking_stats_comments));
                    }

                    int totalViews = 0;
                    for (int i = 0; i < responseData.getData().getViews().size(); i++) {
                        totalViews = totalViews + Integer.parseInt(responseData.getData().getViews().get(i).getViews());
                    }
                    pageViewCountTextView.setText(pageViewLabel + " " + totalViews);
                    if (totalViews == 0) {
                        mChart.setVisibility(View.GONE);
                    } else {
                        changeDataset(responseData, getDatesL(responseData.getData().getViews()), "Last30Days");
//                        setData(responseData.getData().getViews());
                        mChart.animateX(2500);
                    }
                } else {
                    if (!StringUtils.isNullOrEmpty(responseData.getReason())) {
//                        showToast(responseData.getReason());
                    } else {
//                        showToast(getString(R.string.server_went_wrong));
                    }
                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<BloggerAnalyticsResponse> call, Throwable t) {
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    public List<String> getDatesL(ArrayList<BloggerAnalyticsViews> bloggerAnalyticsViewsList) {
        DateFormat df1 = new SimpleDateFormat("MMM dd");
        datesList.clear();

        for (int i = 0; i < bloggerAnalyticsViewsList.size(); i++) {
            Date date = new Date(Long.parseLong(bloggerAnalyticsViewsList.get(i).getDate()));
            String datee = df1.format(date);
            datesList.add(datee);
        }
        return datesList;
    }

    public void changeDataset(BloggerAnalyticsResponse bloggerAnalyticsResponse, final List<String> dList, String viewsDateType) {

        ArrayList<BloggerAnalyticsViews> list = bloggerAnalyticsResponse.getData().getViews();
        if (null == list || list.isEmpty()) {
            return;
        }
        Date date1 = new Date(Long.parseLong(list.get(0).getDate()));
        Date date2 = new Date(Long.parseLong(list.get(list.size() - 1).getDate()));

        if ("custom".equals(viewsDateType)) {
//            daysCountTextView.setText(new SimpleDateFormat("dd-MMM-yyyy").format(date1) + " to " + new SimpleDateFormat("dd-MMM-yyyy").format(date2));
        } else {
//            daysCountTextView.setText(getString(R.string.analytics_popup_last30days));
        }

        mChart.invalidate();

        int totalViews = 0;
        for (int i = 0; i < list.size(); i++) {
            totalViews = totalViews + Integer.parseInt(list.get(i).getViews());
        }
        pageViewCountTextView.setText(pageViewLabel + " " + totalViews);

        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                try {
                    return dList.get((int) value);
                } catch (Exception e) {
                    return "";
                }

            }
        });
        setData(list);
        mChart.animateX(2500);
    }

    public void openCustomDatePickerMenu(TextView dateTextView) {
        final PopupMenu popup = new PopupMenu(getActivity(), dateTextView);
        popup.getMenuInflater().inflate(R.menu.analytics_pageviews_custom_date_popup, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.fixDays) {
                    customDatePickerView.setVisibility(View.GONE);
                    create30daysPageViewGraph();
                    return true;
                } else {
                    customDatePickerView.setVisibility(View.VISIBLE);
                    dateChooserTextView.setText(getString(R.string.ranking_menu_custom_label));
                    return true;

                }
            }

        });
        popup.show();
    }

    @Override
    public void onDateSelection(String dateType, String selectedDate, int year, int month, int day) {
        if (dateType.equals("to")) {
            toDateTextView.setText(selectedDate);
            toDate = selectedDate;
            toCalen.clear();
            toCalen = Calendar.getInstance();
            toCalen.set(Calendar.HOUR_OF_DAY, 0);
            toCalen.set(Calendar.MINUTE, 0);
            toCalen.set(Calendar.SECOND, 0);
            toCalen.set(Calendar.MILLISECOND, 0);
            toCalen.set(Calendar.YEAR, year);
            toCalen.set(Calendar.MONTH, month);
            toCalen.set(Calendar.DAY_OF_MONTH, day);
            getPageViewData();
        } else {
            fromDateTextView.setText(selectedDate);
            fromDate = selectedDate;
            fromCalen.clear();
            fromCalen = Calendar.getInstance();
            fromCalen.set(Calendar.HOUR_OF_DAY, 0);
            fromCalen.set(Calendar.MINUTE, 0);
            fromCalen.set(Calendar.SECOND, 0);
            fromCalen.set(Calendar.MILLISECOND, 0);
            fromCalen.set(Calendar.YEAR, year);
            fromCalen.set(Calendar.MONTH, month);
            fromCalen.set(Calendar.DAY_OF_MONTH, day);
            getPageViewData();
        }
    }

    private void getPageViewData() {
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        BloggerDashboardAPI bloggerDashboardAPI = retrofit.create(BloggerDashboardAPI.class);
        if (fromCalen.getTimeInMillis() > toCalen.getTimeInMillis()) {
            Toast.makeText(getActivity(), getString(R.string.ranking_toast_start_end_date), Toast.LENGTH_SHORT).show();
        }
        Call<BloggerAnalyticsResponse> callAnalytics = bloggerDashboardAPI.getAnalyticsReport(userId,
                "" + fromCalen.getTimeInMillis() / 1000, "" + toCalen.getTimeInMillis() / 1000);
        callAnalytics.enqueue(analyticsResponseListener1);
    }

    private Callback<BloggerAnalyticsResponse> analyticsResponseListener1 = new Callback<BloggerAnalyticsResponse>() {
        @Override
        public void onResponse(Call<BloggerAnalyticsResponse> call, retrofit2.Response<BloggerAnalyticsResponse> response) {
            if (response == null || null == response.body()) {
                return;
            }
            try {
                BloggerAnalyticsResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    changeDataset(responseData, getDatesL(responseData.getData().getViews()), "custom");
                } else {
                    if (org.apache.commons.lang3.StringUtils.isNotEmpty(responseData.getReason())) {
                    } else {

                    }
                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<BloggerAnalyticsResponse> call, Throwable t) {
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    @Override
    protected void updateUi(Response response) {

    }

    @Override
    public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

    }

    @Override
    public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

    }

    @Override
    public void onChartLongPressed(MotionEvent me) {

    }

    @Override
    public void onChartDoubleTapped(MotionEvent me) {

    }

    @Override
    public void onChartSingleTapped(MotionEvent me) {

    }

    @Override
    public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {

    }

    @Override
    public void onChartScale(MotionEvent me, float scaleX, float scaleY) {

    }

    @Override
    public void onChartTranslate(MotionEvent me, float dX, float dY) {

    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {

    }

    @Override
    public void onNothingSelected() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dateChooserTextView:
                openCustomDatePickerMenu(dateChooserTextView);
                break;
            case R.id.fromDateTextView: {
                PageViewsDatePickerFragment fromFragment = new PageViewsDatePickerFragment();
     //          fromFragment.setTargetFragment(this, 0);
                Bundle b1 = new Bundle();
                b1.putString("type", "from");
                fromFragment.setArguments(b1);
                fromFragment.show(getChildFragmentManager(), "datePicker");
            }
            break;
            case R.id.toDateTextView: {
                PageViewsDatePickerFragment toFragment = new PageViewsDatePickerFragment();
    //           toFragment.setTargetFragment(this, 0);
                Bundle b1 = new Bundle();
                b1.putString("type", "to");
                toFragment.setArguments(b1);
                toFragment.show(getChildFragmentManager(), "datePicker");
            }
            break;
            case R.id.improvePageViewTextView: {
                ImproveRankPageViewsSocialFragment analyticsStatsDialogFragment = new ImproveRankPageViewsSocialFragment();
                Bundle b = new Bundle();
                b.putString(AppConstants.ANALYTICS_INFO_TYPE, AppConstants.ANALYTICS_INFO_IMPROVE_PAGE_VIEWS);
                analyticsStatsDialogFragment.setArguments(b);
                ((RankingActivity) getActivity()).addFragment(analyticsStatsDialogFragment, b, false);
            }
            break;
            case R.id.improveSocialShareTextView: {
                ImproveRankPageViewsSocialFragment analyticsStatsDialogFragment = new ImproveRankPageViewsSocialFragment();
                Bundle b = new Bundle();
                b.putString(AppConstants.ANALYTICS_INFO_TYPE, AppConstants.ANALYTICS_INFO_IMPROVE_SOCIAL_SHARE);
                analyticsStatsDialogFragment.setArguments(b);
                ((RankingActivity) getActivity()).addFragment(analyticsStatsDialogFragment, b, false);
            }
            break;
        }
    }

    private boolean isDataValid() {
        boolean isLoginOk = true;
        String stDate = fromDateTextView.getText().toString();
        String enDate = toDateTextView.getText().toString();

        if (stDate.trim().length() == 0) {
            Toast.makeText(getActivity(), "Please enter start date", Toast.LENGTH_SHORT).show();
            isLoginOk = false;
        } else if (enDate.toString().length() == 0) {
            Toast.makeText(getActivity(), "Please enter end date", Toast.LENGTH_SHORT).show();
            isLoginOk = false;
        } else if (!checkEndDateIsGreater(stDate, enDate)) {
            Toast.makeText(getActivity(), "End date should be greater", Toast.LENGTH_SHORT).show();
            isLoginOk = false;
        }
        return isLoginOk;
    }

    private boolean checkEndDateIsGreater(String dateString1, String dateString2) {
        DateFormat df1 = new SimpleDateFormat("dd-MM-yyyy");
//        datesList.clear();
        Date sDate = null;
        Date eDate = null;

        try {
            sDate = df1.parse(dateString1);
            eDate = df1.parse(dateString2);
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }

        return eDate.compareTo(sDate) > 0;
    }
}
