package com.mycity4kids.ui.fragment;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.models.response.BloggerAnalyticsResponse;
import com.mycity4kids.models.response.BloggerAnalyticsViews;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.BloggerDashboardAPI;
import com.mycity4kids.ui.activity.BloggersAnalyticsActivity;

import org.apache.commons.lang.StringUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by hemant on 14/12/16.
 */
public class PageViewsDateRangeDialogFragment extends DialogFragment implements View.OnClickListener {

    static TextView fromDateTextView;
    static TextView toDateTextView;
    static TextView submitTextView;
    static TextView cancelTextView;

    static ArrayList<String> datesList;
    static String toDate;
    static String fromDate;
    static Calendar toCalen;
    static Calendar fromCalen;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.page_views_date_range_dialog, container,
                false);
        fromDateTextView = (TextView) rootView.findViewById(R.id.fromDateTextView);
        toDateTextView = (TextView) rootView.findViewById(R.id.toDateTextView);
        submitTextView = (TextView) rootView.findViewById(R.id.submitTextView);
        cancelTextView = (TextView) rootView.findViewById(R.id.cancelTextView);

        fromDateTextView.setOnClickListener(this);
        toDateTextView.setOnClickListener(this);
        cancelTextView.setOnClickListener(this);
        submitTextView.setOnClickListener(this);

        datesList = new ArrayList<>();

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

        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.fromDateTextView:
//                DialogFragment newFragment = new DatePickerFragment();
//                Bundle b = new Bundle();
//                b.putString("type", "from");
//                newFragment.setArguments(b);
//                newFragment.show(getSupportFragmentManager(), "datePicker");

                DialogFragment fromFragment = new DatePickerFragment();
                Bundle b1 = new Bundle();
                b1.putString("type", "from");
                fromFragment.setArguments(b1);
                fromFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
                break;
            case R.id.toDateTextView:
                DialogFragment toFragment = new DatePickerFragment();
                Bundle b2 = new Bundle();
                b2.putString("type", "to");
                toFragment.setArguments(b2);
                toFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
                break;
            case R.id.cancelTextView:
                dismiss();
                break;
            case R.id.submitTextView:
                if (isDataValid()) {
                    getPageViewData();
                }
//                calculateDaysBeteenDates();
                Log.d("", "");
                break;
        }
    }

    private void getPageViewData() {
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        BloggerDashboardAPI bloggerDashboardAPI = retrofit.create(BloggerDashboardAPI.class);

        Call<BloggerAnalyticsResponse> callAnalytics = bloggerDashboardAPI.getAnalyticsReport(SharedPrefUtils.getUserDetailModel(getActivity()).getDynamoId(),
                "" + fromCalen.getTimeInMillis() / 1000, "" + toCalen.getTimeInMillis() / 1000);
        callAnalytics.enqueue(analyticsResponseListener);
    }

    private Callback<BloggerAnalyticsResponse> analyticsResponseListener = new Callback<BloggerAnalyticsResponse>() {
        @Override
        public void onResponse(Call<BloggerAnalyticsResponse> call, retrofit2.Response<BloggerAnalyticsResponse> response) {
            if (response == null || null == response.body()) {
                if (getActivity() != null) {
                    ((BloggersAnalyticsActivity) getActivity()).showToast("Something went wrong from server");
                }
                return;
            }
            try {
                BloggerAnalyticsResponse responseData = (BloggerAnalyticsResponse) response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    ((BloggersAnalyticsActivity) getActivity()).changeDataset(responseData, getDatesL(responseData.getData().getViews()), "custom");
                    dismiss();
                } else {
                    if (StringUtils.isNotEmpty(responseData.getReason())) {
                        if (getActivity() != null) {
                            ((BloggersAnalyticsActivity) getActivity()).showToast(responseData.getReason());
                        }
                    } else {
                        if (getActivity() != null) {
                            ((BloggersAnalyticsActivity) getActivity()).showToast(getString(R.string.server_went_wrong));
                        }
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
        }

        if (eDate.compareTo(sDate) > 0) {
            return true;
        }
        return false;
    }

    public static List<String> getDatesL(ArrayList<BloggerAnalyticsViews> bloggerAnalyticsViewsList) {
        DateFormat df1 = new SimpleDateFormat("MMM dd");
        datesList.clear();

        for (int i = 0; i < bloggerAnalyticsViewsList.size(); i++) {
            Date date = new Date(Long.parseLong(bloggerAnalyticsViewsList.get(i).getDate()));
            String datee = df1.format(date);
            datesList.add(datee);
        }
        return datesList;
    }

    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        boolean cancel;
        String type = "";
        final Calendar c = Calendar.getInstance();
        int curent_year = c.get(Calendar.YEAR);
        int current_month = c.get(Calendar.MONTH);
        int current_day = c.get(Calendar.DAY_OF_MONTH);

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

        }

        @SuppressLint("NewApi")
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            DatePickerDialog dlg = new DatePickerDialog(getActivity(), android.R.style.Theme_Holo_Light_Dialog_NoActionBar, this, curent_year, current_month, current_day);
            dlg.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            type = getArguments().getString("type", "");
            dlg.getDatePicker().setMaxDate(c.getTimeInMillis());
            return dlg;

        }

        public void onDateSet(DatePicker view, int year, int month, int day) {

            Log.d("TTTTYYYPPEEE ==== ", "dekho is haivaan ko = " + type);
            String sel_date = "" + day + "-" + (month + 1) + "-" + year;
//            if (chkTime(sel_date)) {
////                kidBdy.setText("" + day + "-" + (month + 1) + "-" + year);
//                fromDateTextView.setText(sel_date);
//                toDateTextView.setText(type);
//            } else {
//                fromDateTextView.setText(type);
//                toDateTextView.setText(type);
////                kidBdy.setText("" + current_day + "-" + (current_month + 1) + "-" + curent_year);
//            }

            if (type.equals("to")) {
                toDateTextView.setText(sel_date);
                toDate = sel_date;
                toCalen.clear();
                toCalen = Calendar.getInstance();
                toCalen.set(Calendar.HOUR_OF_DAY, 0);
                toCalen.set(Calendar.MINUTE, 0);
                toCalen.set(Calendar.SECOND, 0);
                toCalen.set(Calendar.MILLISECOND, 0);
                toCalen.set(Calendar.YEAR, year);
                toCalen.set(Calendar.MONTH, month);
                toCalen.set(Calendar.DAY_OF_MONTH, day);
//                List<String> dateList = getDates(fromDate, toDate);
//                xAxis.setAxisMinimum(0.0f);
////        xAxis.set
//                xAxis.setAxisMaximum(datesList.size());
//                xAxis.setValueFormatter(new IAxisValueFormatter() {
//                    @Override
//                    public String getFormattedValue(float value, AxisBase axis) {
//                        return datesList.get((int) value);
//                    }
//                });
//                mChart.getData().notifyDataChanged();
//                mChart.notifyDataSetChanged();
//                mChart.invalidate();
            } else {
                fromDateTextView.setText(sel_date);
                fromDate = sel_date;
                fromCalen.clear();
                fromCalen = Calendar.getInstance();
                fromCalen.set(Calendar.HOUR_OF_DAY, 0);
                fromCalen.set(Calendar.MINUTE, 0);
                fromCalen.set(Calendar.SECOND, 0);
                fromCalen.set(Calendar.MILLISECOND, 0);
                fromCalen.set(Calendar.YEAR, year);
                fromCalen.set(Calendar.MONTH, month);
                fromCalen.set(Calendar.DAY_OF_MONTH, day);
            }

        }
    }

    public static boolean chkTime(String time) {
        boolean result = true;

        String currentime = "" + (System.currentTimeMillis() / 1000);
        if (Integer.parseInt(currentime) < Integer.parseInt(convertDate(time)))
            result = false;

        return result;
    }

    public static String convertDate(String convertdate) {
        String timestamp = "";
        try {
            DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
            Date dateobj = (Date) formatter.parse(convertdate);
            timestamp = "" + (dateobj.getTime()) / 1000;
            return timestamp;
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return timestamp;
    }
}
