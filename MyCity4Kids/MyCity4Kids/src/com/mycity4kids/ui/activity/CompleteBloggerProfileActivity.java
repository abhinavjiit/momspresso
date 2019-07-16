package com.mycity4kids.ui.activity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.kelltontech.utils.DateTimeUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.models.request.AddRemoveKidsRequest;
import com.mycity4kids.models.request.UpdateUserDetailsRequest;
import com.mycity4kids.models.response.KidsModel;
import com.mycity4kids.models.response.UserDetailResponse;
import com.mycity4kids.models.user.KidsInfo;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.rangebar.RangeBar;
import com.mycity4kids.retrofitAPIsInterfaces.BloggerDashboardAPI;
import com.mycity4kids.retrofitAPIsInterfaces.UserAttributeUpdateAPI;
import com.mycity4kids.widget.KidsInfoCustomView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by hemant on 2/8/17.
 */
public class CompleteBloggerProfileActivity extends BaseActivity implements View.OnClickListener {

    private ArrayList<AddRemoveKidsRequest> kidsModelArrayList;

    private LinearLayout childInfoContainer;
    private RangeBar rangebar;
    private static TextView dobTextView;
    private TextView saveTextView;
    private ProgressBar progressBar;
    private RelativeLayout root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.complete_blogger_profile_activity);
        root = findViewById(R.id.rootLayout);
        ((BaseApplication) getApplication()).setView(root);
        ((BaseApplication) getApplication()).setActivity(this);

        childInfoContainer = (LinearLayout) findViewById(R.id.childInfoContainer);
        rangebar = (RangeBar) findViewById(R.id.rangebar);
        saveTextView = (TextView) findViewById(R.id.saveTextView);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        saveTextView.setOnClickListener(this);

        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        BloggerDashboardAPI bloggerDashboardAPI = retrofit.create(BloggerDashboardAPI.class);
        Call<UserDetailResponse> call = bloggerDashboardAPI.getBloggerData(SharedPrefUtils.getUserDetailModel(this).getDynamoId());
        call.enqueue(getUserDetailsResponseCallback);
    }

    private Callback<UserDetailResponse> getUserDetailsResponseCallback = new Callback<UserDetailResponse>() {
        @Override
        public void onResponse(Call<UserDetailResponse> call, retrofit2.Response<UserDetailResponse> response) {
            removeProgressDialog();
            if (response == null || response.body() == null) {
                return;
            }

            UserDetailResponse responseData = response.body();
            if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                if (responseData.getData().get(0).getResult().getKids() == null) {
                    rangebar.setRangePinsByValue(0, 0);
                } else {
                    rangebar.setRangePinsByValue(0, responseData.getData().get(0).getResult().getKids().size());
                    for (KidsModel km : responseData.getData().get(0).getResult().getKids()) {
                        addKidView(km);
                    }
                }
                rangebar.setOnRangeBarChangeListener(new RangeBar.OnRangeBarChangeListener() {
                    @Override
                    public void onRangeChangeListener(RangeBar rangeBar, int leftPinIndex, int rightPinIndex, String leftPinValue, String rightPinValue) {
                        if (childInfoContainer.getChildCount() < rightPinIndex) {
                            while (rightPinIndex - childInfoContainer.getChildCount() > 0) {
                                addKidView(null);
                            }
                        } else if (childInfoContainer.getChildCount() > rightPinIndex) {
                            childInfoContainer.removeViews(rightPinIndex, childInfoContainer.getChildCount() - rightPinIndex);
                        }
                    }
                });

            } else {
//                noDataFoundTextView.setVisibility(View.VISIBLE);
//                showToast(responseData.getReason());
            }
        }

        @Override
        public void onFailure(Call<UserDetailResponse> call, Throwable t) {
//            noDataFoundTextView.setVisibility(View.VISIBLE);
            removeProgressDialog();
//            showToast(getString(R.string.server_went_wrong));
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    private void addKidView(KidsModel km) {
        final KidsInfoCustomView kidsInfo1 = new KidsInfoCustomView(this);
        if (km == null) {
            kidsInfo1.setKids_bdy("Date of Birth");
        } else {
            kidsInfo1.setKids_bdy(DateTimeUtils.getKidsDOBNanoMilliTimestamp("" + km.getBirthDay()));
            if ("0".equals(km.getGender())) {
                kidsInfo1.setMaleRadioButton(true);
            } else {
                kidsInfo1.setFemaleRadioButton(true);
            }
        }
        kidsInfo1.getKidsDOBTextView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dobTextView = kidsInfo1.getKidsDOBTextView();
                showDatePickerDialog();
            }
        });
        childInfoContainer.addView(kidsInfo1);
    }

    public void showDatePickerDialog() {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.saveTextView:
                saveKids();
                break;
        }
    }

    public void saveKids() {
        progressBar.setVisibility(View.VISIBLE);
        ArrayList<KidsInfo> kidsList = getEnteredKidsInfo();

        kidsModelArrayList = new ArrayList<>();
        for (KidsInfo ki : kidsList) {
            AddRemoveKidsRequest kmodel = new AddRemoveKidsRequest();
            long bdaytimestamp = DateTimeUtils.convertStringToTimestamp(ki.getDate_of_birth());
            if (bdaytimestamp != 0) {
                kmodel.setBirthDay(bdaytimestamp * 1000);
            } else {
                showToast(getString(R.string.complete_blogger_profile_incorrect_date));
                progressBar.setVisibility(View.GONE);
                return;
            }
            kmodel.setGender(ki.getGender());
            kidsModelArrayList.add(kmodel);
        }

        addCityAndKidsDetails();
    }

    private ArrayList<KidsInfo> getEnteredKidsInfo() {
        ArrayList<KidsInfo> kidsInfoList = new ArrayList<KidsInfo>();

        for (int position = 0; position < childInfoContainer.getChildCount(); position++) {
            View innerLayout = childInfoContainer.getChildAt(position);

            final TextView dobOfKidSpn = (TextView) innerLayout.findViewById(R.id.kidsDOBTextView);
            RadioGroup genderRadioGroup = (RadioGroup) innerLayout.findViewById(R.id.genderRadioGroup);
            RadioButton maleRadio = (RadioButton) innerLayout.findViewById(R.id.maleRadioButton);
            RadioButton femaleRadio = (RadioButton) innerLayout.findViewById(R.id.femaleRadioButton);

            int radioButtonID = genderRadioGroup.getCheckedRadioButtonId();

            dobOfKidSpn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dobTextView = dobOfKidSpn;
                    showDatePickerDialog();
                }
            });

            if ((dobOfKidSpn.getText().toString().trim().equals(""))) {

            } else {
                KidsInfo kidsInformation = new KidsInfo();
                kidsInformation.setDate_of_birth((dobOfKidSpn).getText().toString().trim());
                if (radioButtonID == maleRadio.getId()) {
                    kidsInformation.setGender("0");
                } else {
                    kidsInformation.setGender("1");
                }
                kidsInfoList.add(kidsInformation);
            }
        }
        return kidsInfoList;
    }


    private void addCityAndKidsDetails() {
        UpdateUserDetailsRequest addCityAndKidsInformationRequest = new UpdateUserDetailsRequest();
        addCityAndKidsInformationRequest.setKids(kidsModelArrayList);

        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        UserAttributeUpdateAPI userAttributeUpdateAPI = retrofit.create(UserAttributeUpdateAPI.class);
        Call<UserDetailResponse> call = userAttributeUpdateAPI.updateCityAndKids(addCityAndKidsInformationRequest);
        call.enqueue(addCityAndKidsResponseReceived);
    }

    Callback<UserDetailResponse> addCityAndKidsResponseReceived = new Callback<UserDetailResponse>() {
        @Override
        public void onResponse(Call<UserDetailResponse> call, retrofit2.Response<UserDetailResponse> response) {
            Log.d("SUCCESS", "" + response);
            progressBar.setVisibility(View.GONE);
            if (response == null || response.body() == null) {
                showToast(getString(R.string.went_wrong));
                return;
            }

            try {
                UserDetailResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    Intent intent = new Intent(CompleteBloggerProfileActivity.this, DashboardActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    showToast("" + responseData.getReason());
                    Intent intent = new Intent(CompleteBloggerProfileActivity.this, DashboardActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4KException", Log.getStackTraceString(e));
                showToast(getString(R.string.went_wrong));
                Intent intent = new Intent(CompleteBloggerProfileActivity.this, DashboardActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        }

        @Override
        public void onFailure(Call<UserDetailResponse> call, Throwable t) {
            progressBar.setVisibility(View.GONE);
            Log.d("MC4kException", Log.getStackTraceString(t));
            Crashlytics.logException(t);
            showToast(getString(R.string.went_wrong));
            Intent intent = new Intent(CompleteBloggerProfileActivity.this, DashboardActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
    };


    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        boolean cancel;

        final Calendar c = Calendar.getInstance();
        int curent_year = c.get(Calendar.YEAR);
        int current_month = c.get(Calendar.MONTH);
        int current_day = c.get(Calendar.DAY_OF_MONTH);

        @SuppressLint("NewApi")
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            DatePickerDialog dlg = new DatePickerDialog(getActivity(), android.R.style.Theme_Holo_Light_Dialog_NoActionBar, this, curent_year, current_month, current_day);
            dlg.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dlg.getDatePicker().setMaxDate(c.getTimeInMillis());
            return dlg;
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            if (dobTextView != null) {
                String sel_date = "" + day + "-" + (month + 1) + "-" + year;
                if (chkTime(sel_date)) {
                    dobTextView.setText("" + day + "-" + (month + 1) + "-" + year);
                } else {
                    dobTextView.setText("" + current_day + "-" + (current_month + 1) + "-" + curent_year);
                }
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
            Date dateobj = formatter.parse(convertdate);
            timestamp = "" + (dateobj.getTime()) / 1000;
            return timestamp;
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return timestamp;
    }

    @Override
    protected void updateUi(Response response) {

    }
}
