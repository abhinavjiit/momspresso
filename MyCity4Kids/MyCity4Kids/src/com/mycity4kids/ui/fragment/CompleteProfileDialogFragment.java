package com.mycity4kids.ui.fragment;

import android.accounts.NetworkErrorException;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.kelltontech.network.Response;
import com.kelltontech.ui.IScreen;
import com.kelltontech.utils.ConnectivityUtils;
import com.kelltontech.utils.StringUtils;
import com.kelltontech.utils.ToastUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.asynctask.HeavyDbTask;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.controller.ConfigurationController;
import com.mycity4kids.dbtable.TableKids;
import com.mycity4kids.interfaces.OnUIView;
import com.mycity4kids.models.VersionApiModel;
import com.mycity4kids.models.city.City;
import com.mycity4kids.models.city.MetroCity;
import com.mycity4kids.models.configuration.ConfigurationApiModel;
import com.mycity4kids.models.request.AddRemoveKidsRequest;
import com.mycity4kids.models.request.UpdateUserDetailsRequest;
import com.mycity4kids.models.response.CityConfigResponse;
import com.mycity4kids.models.response.CityInfoItem;
import com.mycity4kids.models.response.UserDetailResponse;
import com.mycity4kids.models.user.KidsInfo;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.ConfigAPIs;
import com.mycity4kids.retrofitAPIsInterfaces.UserAttributeUpdateAPI;
import com.mycity4kids.ui.activity.BloggerDashboardActivity;
import com.mycity4kids.ui.adapter.CitySpinnerAdapter;
import com.mycity4kids.utils.AppUtils;

import java.text.DateFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by Hemant.Parmar on 08-06-2015.
 */
public class CompleteProfileDialogFragment extends DialogFragment implements View.OnClickListener, IScreen {

    private TextView addChildTextView;
    private LinearLayout mChildContainer;
    private static TextView BdayView;
    private ScrollView scrollview;
    private Spinner citySelectorSpinner;
    private TextView cityNameEditText;
    private TextView cancel, done;
    private ProgressBar progressBar;

    private ArrayList<AddRemoveKidsRequest> kidsModelArrayList;

    private int childCount = 0;
    private int selectedCityId;
    private City cityModel;

    FirebaseAnalytics mFirebaseAnalytics;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.complete_profile_dialog_fragment, container,
                false);
        scrollview = (ScrollView) rootView.findViewById(R.id.scroll_view);
        addChildTextView = (TextView) rootView.findViewById(R.id.additional_child);
        cityNameEditText = (TextView) rootView.findViewById(R.id.cityNameEditText);
        mChildContainer = (LinearLayout) rootView.findViewById(R.id.internal_kid_layout);
        citySelectorSpinner = (Spinner) rootView.findViewById(R.id.citySpinner);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());
//        addChildAdult = (CustomListView) rootView.findViewById(R.id.add_kid_adult);
        cancel = (TextView) rootView.findViewById(R.id.cancel);
        done = (TextView) rootView.findViewById(R.id.done);

        addChildTextView.setVisibility(View.VISIBLE);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        addChildTextView.setOnClickListener(this);
        cancel.setOnClickListener(this);
        done.setOnClickListener(this);

        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        ConfigAPIs cityConfigAPI = retrofit.create(ConfigAPIs.class);
        Call<CityConfigResponse> call = cityConfigAPI.getCityConfig();
        call.enqueue(cityConfigResponseCallback);

        citySelectorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // An item was selected. You can retrieve the selected item using
                Log.d("Selected City = ", "city is " + parent.getItemAtPosition(position));
                if ("Others".equals(((CityInfoItem) parent.getItemAtPosition(position)).getCityName())) {
                    cityNameEditText.setVisibility(View.VISIBLE);
                    cityNameEditText.requestFocus();
                    selectedCityId = AppConstants.OTHERS_CITY_ID;
                } else {
                    selectedCityId = Integer.parseInt(((CityInfoItem) parent.getItemAtPosition(position)).getId().replace("city-", ""));
                    cityNameEditText.setVisibility(View.GONE);
                }
                CityInfoItem cii = (CityInfoItem) parent.getItemAtPosition(position);
                cityModel = new City(cii.getCityName(), cii.getLat(), cii.getLon(), selectedCityId, cii.getId());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        addNewChild();
        return rootView;
    }

    private Callback<CityConfigResponse> cityConfigResponseCallback = new Callback<CityConfigResponse>() {
        public ArrayList<CityInfoItem> mDatalist;

        @Override
        public void onResponse(Call<CityConfigResponse> call, retrofit2.Response<CityConfigResponse> response) {
            if (response == null || null == response.body()) {
                NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                Crashlytics.logException(nee);
                gotToProfile();
                return;
            }
            try {
                CityConfigResponse responseData = (CityConfigResponse) response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
//                    mDatalist = responseData.getData().getResult().getCityData();
                    mDatalist = new ArrayList<>();
                    if (mDatalist == null) {
                        gotToProfile();
                        return;
                    }
                    for (int i = 0; i < responseData.getData().getResult().getCityData().size(); i++) {
                        if (!AppConstants.ALL_CITY_NEW_ID.equals(responseData.getData().getResult().getCityData().get(i).getId())) {
                            mDatalist.add(responseData.getData().getResult().getCityData().get(i));
                        }
                    }
                    CitySpinnerAdapter citySpinnerAdapter = new CitySpinnerAdapter(getActivity(), R.layout.text_current_locality, mDatalist);
                    citySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    // Apply the adapter to the spinner
                    citySelectorSpinner.setAdapter(citySpinnerAdapter);
                    int currentCityId = SharedPrefUtils.getCurrentCityModel(getActivity()).getId();
                    for (int i = 0; i < mDatalist.size(); i++) {
                        int cId = Integer.parseInt(mDatalist.get(i).getId().replace("city-", ""));
                        if (currentCityId == cId) {
                            citySelectorSpinner.setSelection(i, true);
                        }
                    }
                } else {
                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
                gotToProfile();
            }
        }

        @Override
        public void onFailure(Call<CityConfigResponse> call, Throwable t) {
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
            gotToProfile();
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.additional_child:
                addNewChild();
                sendScroll();
                break;
            case R.id.cancel:
                gotToProfile();
                break;
            case R.id.done:
                if (isDataValid())
                    saveKidsAndCity();
                break;
        }
    }

    private boolean isDataValid() {
        if (cityNameEditText.getVisibility() == View.VISIBLE && StringUtils.isNullOrEmpty(cityNameEditText.getText().toString())) {
            Toast.makeText(getActivity(), "Please enter city name", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            for (int position = 0; position < mChildContainer.getChildCount(); position++) {
                View innerLayout = (View) mChildContainer.getChildAt(position);

                EditText nameOfKidEdt = (EditText) innerLayout.findViewById(R.id.kids_name);
                EditText dobOfKidSpn = (EditText) innerLayout.findViewById(R.id.kids_bdy);
                if (StringUtils.isNullOrEmpty(nameOfKidEdt.getText().toString())) {
                    Toast.makeText(getActivity(), "Please enter kids name", Toast.LENGTH_SHORT).show();
                    return false;
                } else if (StringUtils.isNullOrEmpty(dobOfKidSpn.getText().toString())) {
                    Toast.makeText(getActivity(), "Please enter kids bday", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
        }
        return true;
    }

    public void saveKidsAndCity() {
        progressBar.setVisibility(View.VISIBLE);
        ArrayList<KidsInfo> kidsList = getEnteredKidsInfo();

        kidsModelArrayList = new ArrayList<>();
        for (KidsInfo ki : kidsList) {
            AddRemoveKidsRequest kmodel = new AddRemoveKidsRequest();
            kmodel.setName(ki.getName());
            if (StringUtils.isNullOrEmpty(ki.getName())) {
                Toast.makeText(getActivity(), "Please enter kids name to continue", Toast.LENGTH_SHORT).show();
                return;
            }
            long bdaytimestamp = convertStringToTimestamp(ki.getDate_of_birth());
            if (bdaytimestamp != 0) {
                kmodel.setBirthDay(bdaytimestamp);
            } else {
                Toast.makeText(getActivity(), "incorrect kids bday", Toast.LENGTH_SHORT).show();
                return;
            }
            kidsModelArrayList.add(kmodel);
        }

        addCityAndKidsDetails();
    }

    private void addCityAndKidsDetails() {
        UpdateUserDetailsRequest addCityAndKidsInformationRequest = new UpdateUserDetailsRequest();
        addCityAndKidsInformationRequest.setKids(kidsModelArrayList);

        if (cityNameEditText.getVisibility() == View.VISIBLE) {
            addCityAndKidsInformationRequest.setCityId("" + selectedCityId);
            addCityAndKidsInformationRequest.setCityName(cityNameEditText.getText().toString());
        } else {
            addCityAndKidsInformationRequest.setCityId("" + selectedCityId);
        }
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        UserAttributeUpdateAPI userAttributeUpdateAPI = retrofit.create(UserAttributeUpdateAPI.class);
        Call<UserDetailResponse> call = userAttributeUpdateAPI.updateCityAndKids(addCityAndKidsInformationRequest);
        call.enqueue(addCityAndKidsResponseReceived);
    }

    Callback<UserDetailResponse> addCityAndKidsResponseReceived = new Callback<UserDetailResponse>() {
        @Override
        public void onResponse(Call<UserDetailResponse> call, retrofit2.Response<UserDetailResponse> response) {
            Log.d("SUCCESS", "" + response);
            if (response == null || response.body() == null) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getActivity(), getString(R.string.went_wrong), Toast.LENGTH_SHORT).show();
                gotToProfile();
                return;
            }

            try {
                UserDetailResponse responseData = (UserDetailResponse) response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    updateEventsResourcesConfigForCity();
                    saveDatainDB();
                } else {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getActivity(), responseData.getReason(), Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                progressBar.setVisibility(View.GONE);
                Crashlytics.logException(e);
                Log.d("MC4KException", Log.getStackTraceString(e));
                if (null != getActivity()) {
                    Toast.makeText(getActivity(), getString(R.string.went_wrong), Toast.LENGTH_SHORT).show();
                }
                gotToProfile();
            }
        }

        @Override
        public void onFailure(Call<UserDetailResponse> call, Throwable t) {
            progressBar.setVisibility(View.GONE);
            Log.d("MC4kException", Log.getStackTraceString(t));
            Crashlytics.logException(t);
            Toast.makeText(getActivity(), getString(R.string.went_wrong), Toast.LENGTH_SHORT).show();
            gotToProfile();
        }
    };

    private void updateEventsResourcesConfigForCity() {
        final VersionApiModel versionApiModel = SharedPrefUtils.getSharedPrefVersion(getActivity());
        final ConfigurationController _controller = new ConfigurationController(getActivity(), this);

        MetroCity model = new MetroCity();

        model.setId(cityModel.getCityId());
        model.setName(cityModel.getCityName());
        model.setNewCityId(cityModel.getNewCityId());

        SharedPrefUtils.setCurrentCityModel(getActivity(), model);
        SharedPrefUtils.setChangeCityFlag(getActivity(), true);

        if (cityModel.getCityId() > 0) {
            versionApiModel.setCityId(cityModel.getCityId());
            mFirebaseAnalytics.setUserProperty("CityId", cityModel.getCityId() + "");

            String version = AppUtils.getAppVersion(getActivity());
            if (!StringUtils.isNullOrEmpty(version)) {
                versionApiModel.setAppUpdateVersion(version);
            }

            if (!ConnectivityUtils.isNetworkEnabled(getActivity())) {
                ToastUtils.showToast(getActivity(), getString(R.string.error_network));
                return;

            }
            _controller.getData(AppConstants.CONFIGURATION_REQUEST, versionApiModel);
        }
    }

    @Override
    public void handleUiUpdate(Response response) {
        if (response == null) {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(getActivity(), getResources().getString(R.string.server_error), Toast.LENGTH_SHORT).show();
            gotToProfile();
            return;
        }
        switch (response.getDataType()) {
            case AppConstants.CONFIGURATION_REQUEST:
                Object responseObject = response.getResponseObject();
                if (responseObject instanceof ConfigurationApiModel) {
                    ConfigurationApiModel _configurationResponse = (ConfigurationApiModel) responseObject;
                    BaseApplication.setBusinessREsponse(null);
                    HeavyDbTask _heavyDbTask = new HeavyDbTask(getActivity(),
                            _configurationResponse, new OnUIView() {
                        @Override
                        public void comeBackOnUI() {
                            progressBar.setVisibility(View.GONE);
                            gotToProfile();
                        }
                    });
                    _heavyDbTask.execute();
                }
                break;
            default:
                break;
        }
    }

    private void gotToProfile() {
        Intent intent = new Intent(getActivity(), BloggerDashboardActivity.class);
        intent.putExtra(AppConstants.STACK_CLEAR_REQUIRED, true);
        startActivity(intent);
        getActivity().finish();
    }

    private void addNewChild() {
        boolean addChild = false;

        if (mChildContainer.getChildCount() > 0) {
            for (int position = 0; position < mChildContainer.getChildCount(); position++) {
                View innerLayout = (View) mChildContainer.getChildAt(position);

                EditText nameOfKidEdt = (EditText) innerLayout.findViewById(R.id.kids_name);
                EditText dobOfKidSpn = (EditText) innerLayout.findViewById(R.id.kids_bdy);

                if ((nameOfKidEdt.getText().toString().trim().equals("")) || (dobOfKidSpn.getText().toString().trim().equals(""))) {
                    addChild = false;
                    ToastUtils.showToast(getActivity(), getResources().getString(R.string.enter_kid));
                    break;
                } else {
                    addChild = true;
                }
            }

            if (addChild)
                addDynamicChild();

        } else {
            addDynamicChild();
        }
    }

    private void addDynamicChild() {
        ++childCount;
        LayoutInflater layoutInflater = (LayoutInflater) getActivity().getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View convertView = (View) layoutInflater.inflate(R.layout.child_info_item, null);
        convertView.setTag("kid" + childCount);
        convertView.setId(childCount);

        final EditText dobOfKidSpn = (EditText) convertView.findViewById(R.id.kids_bdy);
//        final TextView txtKidname = (TextView) convertView.findViewById(R.id.txtkidname);
//        final TextView txtKidbdy = (TextView) convertView.findViewById(R.id.txtkidbdy);

        final TextView deleteView = (TextView) convertView.findViewById(R.id.cross);

        if (childCount == 1) {
            deleteView.setVisibility(View.INVISIBLE);
        }
        deleteView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mChildContainer.removeView(convertView);
            }
        });

        final EditText nameOfKidEdt = (EditText) convertView.findViewById(R.id.kids_name);
        nameOfKidEdt.setOnKeyListener(new View.OnKeyListener() {
            @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press
                    dobOfKidSpn.requestFocus();
                    dobOfKidSpn.callOnClick();
                    return true;
                }
                return false;
            }
        });

//        txtKidbdy.setText("KID'S BIRTHDAY");
//        txtKidname.setText("KID'S NAME");

        dobOfKidSpn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BdayView = dobOfKidSpn;
                showDatePickerDialog();
            }
        });
        mChildContainer.addView(convertView);
    }

    public void showDatePickerDialog() {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
    }

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
            if (BdayView != null) {
                String sel_date = "" + day + "-" + (month + 1) + "-" + year;
                if (chkTime(sel_date)) {
                    BdayView.setText("" + day + "-" + (month + 1) + "-" + year);
                } else {
                    BdayView.setText("" + current_day + "-" + (current_month + 1) + "-" + curent_year);
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
            Date dateobj = (Date) formatter.parse(convertdate);
            timestamp = "" + (dateobj.getTime()) / 1000;
            return timestamp;
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return timestamp;
    }

    private void sendScroll() {
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        scrollview.fullScroll(View.FOCUS_DOWN);
                    }
                });
            }
        }).start();
    }

    private ArrayList<KidsInfo> getEnteredKidsInfo() {
        ArrayList<KidsInfo> kidsInfoList = new ArrayList<KidsInfo>();

        for (int position = 0; position < mChildContainer.getChildCount(); position++) {
            View innerLayout = (View) mChildContainer.getChildAt(position);

            EditText nameOfKidEdt = (EditText) innerLayout.findViewById(R.id.kids_name);
            EditText dobOfKidSpn = (EditText) innerLayout.findViewById(R.id.kids_bdy);

            if ((nameOfKidEdt.getText().toString().trim().equals("")) || (dobOfKidSpn.getText().toString().trim().equals(""))) {
            } else {
                KidsInfo kidsInformation = new KidsInfo();
                kidsInformation.setName((nameOfKidEdt).getText().toString().trim());
                kidsInformation.setDate_of_birth((dobOfKidSpn).getText().toString().trim());
                kidsInfoList.add(kidsInformation);
            }
        }
        return kidsInfoList;
    }

    private void saveDatainDB() {
        // saving child data
        TableKids kidsTable = new TableKids((BaseApplication) getActivity().getApplicationContext());
        kidsTable.deleteAll();
        try {
            kidsTable.beginTransaction();
            ArrayList<KidsInfo> kidsInfoArrayList = new ArrayList<>();
            for (AddRemoveKidsRequest kid : kidsModelArrayList) {
                KidsInfo kidsInfo = new KidsInfo();
                kidsInfo.setName(kid.getName());
                kidsInfo.setDate_of_birth(convertTime("" + kid.getBirthDay()));
                kidsInfoArrayList.add(kidsInfo);
            }
            for (KidsInfo kids : kidsInfoArrayList) {
                kidsTable.insertData(kids);
            }
            kidsTable.setTransactionSuccessful();
        } finally {
            kidsTable.endTransaction();
        }

        mChildContainer.removeAllViews();
    }

    public long convertStringToTimestamp(String str_date) {
        try {
            DateFormat formatter;
            formatter = new SimpleDateFormat("dd-MM-yyyy");
            // you can change format of date
            Date date = formatter.parse(str_date);
            return date.getTime() / 1000;
        } catch (ParseException e) {
            System.out.println("Exception :" + e);
            return 0;
        }
    }

    public String convertTime(String time) {
        Date date = new Date(Long.parseLong(time) * 1000);
        Format format = new SimpleDateFormat("dd-MM-yyyy");
        return format.format(date);
    }
}
