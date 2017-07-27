package com.mycity4kids.ui.fragment;

import android.accounts.NetworkErrorException;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseFragment;
import com.kelltontech.utils.ConnectivityUtils;
import com.kelltontech.utils.DateTimeUtils;
import com.kelltontech.utils.StringUtils;
import com.kelltontech.utils.ToastUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.asynctask.HeavyDbTask;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.controller.ConfigurationController;
import com.mycity4kids.interfaces.OnUIView;
import com.mycity4kids.models.VersionApiModel;
import com.mycity4kids.models.city.City;
import com.mycity4kids.models.city.MetroCity;
import com.mycity4kids.models.configuration.ConfigurationApiModel;
import com.mycity4kids.models.request.AddRemoveKidsRequest;
import com.mycity4kids.models.request.UpdateUserDetailsRequest;
import com.mycity4kids.models.response.CityConfigResponse;
import com.mycity4kids.models.response.CityInfoItem;
import com.mycity4kids.models.response.KidsModel;
import com.mycity4kids.models.response.UserDetailResponse;
import com.mycity4kids.models.user.KidsInfo;
import com.mycity4kids.models.user.UserInfo;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.rangebar.RangeBar;
import com.mycity4kids.retrofitAPIsInterfaces.BloggerDashboardAPI;
import com.mycity4kids.retrofitAPIsInterfaces.ConfigAPIs;
import com.mycity4kids.retrofitAPIsInterfaces.UserAttributeUpdateAPI;
import com.mycity4kids.utils.AppUtils;
import com.mycity4kids.widget.KidsInfoCustomView;

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
 * Created by hemant on 19/7/17.
 */
public class EditProfileTabFragment extends BaseFragment implements View.OnClickListener, CityListingDialogFragment.IChangeCity {

    private static final int MAX_WORDS = 200;
    private static final int MAX_CHARACTER = 200;

    private CityListingDialogFragment cityFragment;
    private InputFilter filter;
    private View view;
    private ScrollView scrollView;
    private LinearLayout childInfoContainer;
    private EditText firstNameEditText, lastNameEditText, phoneEditText, describeSelfEditText, blogTitleEditText;
    private TextView emailTextView;
    private TextView cityNameTextView;
    private RangeBar rangebar;
    private ProgressBar progressBar;
    private TextView saveTextView;
    private static TextView dobTextView;

    public ArrayList<CityInfoItem> mDatalist;
    private int selectedCityId;
    private City cityModel;
    private ArrayList<AddRemoveKidsRequest> kidsModelArrayList;
    private String currentCityName;
    private String newSelectedCityId;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.edit_profile_tab_fragment, container, false);

        scrollView = (ScrollView) view.findViewById(R.id.scrollView);
        childInfoContainer = (LinearLayout) view.findViewById(R.id.childInfoContainer);
        firstNameEditText = (EditText) view.findViewById(R.id.firstNameEditText);
        lastNameEditText = (EditText) view.findViewById(R.id.lastNameEditText);
        emailTextView = (TextView) view.findViewById(R.id.emailTextView);
        phoneEditText = (EditText) view.findViewById(R.id.phoneEditText);
        describeSelfEditText = (EditText) view.findViewById(R.id.describeSelfEditText);
        blogTitleEditText = (EditText) view.findViewById(R.id.blogTitleEditText);
        cityNameTextView = (TextView) view.findViewById(R.id.cityNameTextView);
        rangebar = (RangeBar) view.findViewById(R.id.rangebar);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        saveTextView = (TextView) view.findViewById(R.id.saveTextView);

        saveTextView.setOnClickListener(this);
        cityNameTextView.setOnClickListener(this);

        describeSelfEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int wordsLength = countWords(s.toString());// words.length;
                Log.d("onTextChanged", "" + wordsLength);
                if (wordsLength > MAX_WORDS) {
                    describeSelfEditText.setText(s.toString().trim().replaceAll(" [^ ]+$", ""));
                    describeSelfEditText.setSelection(describeSelfEditText.length());
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                int wordsLength = countWords(s.toString());// words.length;
                // count == 0 means a new word is going to start
                Log.d("beforeTextChanged", "" + wordsLength);
                if (count == 0 && wordsLength >= MAX_WORDS) {
                } else {
                    removeFilter(describeSelfEditText);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                int wordsLength = countWords(s.toString());// words.length;
                Log.d("afterTextChanged", "" + wordsLength);

            }
        });

        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        BloggerDashboardAPI bloggerDashboardAPI = retrofit.create(BloggerDashboardAPI.class);
        Call<UserDetailResponse> call = bloggerDashboardAPI.getBloggerData(SharedPrefUtils.getUserDetailModel(getActivity()).getDynamoId());
        call.enqueue(getUserDetailsResponseCallback);

        ConfigAPIs cityConfigAPI = retrofit.create(ConfigAPIs.class);
        Call<CityConfigResponse> cityCall = cityConfigAPI.getCityConfig();
        cityCall.enqueue(cityConfigResponseCallback);

//        citySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                // An item was selected. You can retrieve the selected item using
//                Log.d("Selected City = ", "city is " + parent.getItemAtPosition(position));
//                if ("Others".equals(((CityInfoItem) parent.getItemAtPosition(position)).getCityName())) {
//                    selectedCityId = AppConstants.OTHERS_CITY_ID;
//                } else {
//                    selectedCityId = Integer.parseInt(((CityInfoItem) parent.getItemAtPosition(position)).getId().replace("city-", ""));
//                }
//                CityInfoItem cii = (CityInfoItem) parent.getItemAtPosition(position);
//                cityModel = new City(cii.getCityName(), cii.getLat(), cii.getLon(), selectedCityId, cii.getId());
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });

        return view;
    }

    private int countWords(String s) {
        String trim = s.trim();
        if (trim.isEmpty())
            return 0;
        return trim.split("\\s+").length; // separate string around spaces
    }

    private void removeFilter(EditText et) {
        if (filter != null) {
            et.setFilters(new InputFilter[0]);
            filter = null;
        }
    }

    private Callback<UserDetailResponse> getUserDetailsResponseCallback = new Callback<UserDetailResponse>() {
        @Override
        public void onResponse(Call<UserDetailResponse> call, retrofit2.Response<UserDetailResponse> response) {
            removeProgressDialog();
            if (response == null || response.body() == null) {
//                noDataFoundTextView.setVisibility(View.VISIBLE);
//                showToast(getString(R.string.went_wrong));
                return;
            }

            UserDetailResponse responseData = (UserDetailResponse) response.body();
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

                firstNameEditText.setText(responseData.getData().get(0).getResult().getFirstName());
                emailTextView.setText(responseData.getData().get(0).getResult().getEmail());
                lastNameEditText.setText(responseData.getData().get(0).getResult().getLastName());
                blogTitleEditText.setText(responseData.getData().get(0).getResult().getBlogTitle());
                describeSelfEditText.setText(responseData.getData().get(0).getResult().getUserBio());

                if (null == responseData.getData().get(0).getResult().getPhone()) {
                    phoneEditText.setText(" ");
                } else {
                    phoneEditText.setText(responseData.getData().get(0).getResult().getPhone().getMobile());
                }
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

    private Callback<CityConfigResponse> cityConfigResponseCallback = new Callback<CityConfigResponse>() {

        @Override
        public void onResponse(Call<CityConfigResponse> call, retrofit2.Response<CityConfigResponse> response) {
            if (response == null || null == response.body()) {
                NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                Crashlytics.logException(nee);
//                gotToProfile();
                return;
            }
            try {
                CityConfigResponse responseData = (CityConfigResponse) response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
//                    mDatalist = responseData.getData().getResult().getCityData();
                    mDatalist = new ArrayList<>();
                    if (mDatalist == null) {
//                        gotToProfile();
                        return;
                    }
                    MetroCity currentCity = SharedPrefUtils.getCurrentCityModel(getActivity());
                    for (int i = 0; i < responseData.getData().getResult().getCityData().size(); i++) {
                        if (!AppConstants.ALL_CITY_NEW_ID.equals(responseData.getData().getResult().getCityData().get(i).getId())) {
                            mDatalist.add(responseData.getData().getResult().getCityData().get(i));
                        }
                        if (AppConstants.OTHERS_NEW_CITY_ID.equals(responseData.getData().getResult().getCityData().get(i).getId())) {
                            if (currentCity.getName() != null && !"Others".equals(currentCity.getName()) && currentCity.getId() == AppConstants.OTHERS_CITY_ID) {
                                mDatalist.get(mDatalist.size() - 1).setCityName("Others(" + currentCity.getName() + ")");
                            }
                        }
                    }

                    for (int i = 0; i < mDatalist.size(); i++) {
                        int cId = Integer.parseInt(mDatalist.get(i).getId().replace("city-", ""));
                        if (currentCity.getId() == cId) {
                            mDatalist.get(i).setSelected(true);
                            cityNameTextView.setText(mDatalist.get(i).getCityName());
                        } else {
                            mDatalist.get(i).setSelected(false);
                        }
                    }
//                    CitySpinnerAdapter citySpinnerAdapter = new CitySpinnerAdapter(getActivity(), R.layout.text_current_locality, mDatalist);
//                    citySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                    // Apply the adapter to the spinner
//                    citySpinner.setAdapter(citySpinnerAdapter);
//                    int currentCityId = SharedPrefUtils.getCurrentCityModel(getActivity()).getId();
//                    for (int i = 0; i < mDatalist.size(); i++) {
//                        int cId = Integer.parseInt(mDatalist.get(i).getId().replace("city-", ""));
//                        if (currentCityId == cId) {
//                            citySpinner.setSelection(i, true);
//                        }
//                    }
                } else {
                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
//                gotToProfile();
            }
        }

        @Override
        public void onFailure(Call<CityConfigResponse> call, Throwable t) {
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
//            gotToProfile();
        }
    };

    private void addKidView(KidsModel km) {
        final KidsInfoCustomView kidsInfo1 = new KidsInfoCustomView(getActivity());
        if (km == null) {
            kidsInfo1.setKids_bdy("Date of Birth");
        } else {
            kidsInfo1.setKids_bdy(DateTimeUtils.getKidsDOBNanoMilliTimestamp("" + km.getBirthDay()));
            if ("0".equals(km.getGender())) {
                kidsInfo1.setFemaleRadioButton(true);
            } else {
                kidsInfo1.setMaleRadioButton(true);
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

    public void saveKidsAndCity() {
        progressBar.setVisibility(View.VISIBLE);
        ArrayList<KidsInfo> kidsList = getEnteredKidsInfo();

        kidsModelArrayList = new ArrayList<>();
        for (KidsInfo ki : kidsList) {
            AddRemoveKidsRequest kmodel = new AddRemoveKidsRequest();
            long bdaytimestamp = convertStringToTimestamp(ki.getDate_of_birth());
            if (bdaytimestamp != 0) {
                kmodel.setBirthDay(bdaytimestamp * 1000);
            } else {
                Toast.makeText(getActivity(), "incorrect kids bday", Toast.LENGTH_SHORT).show();
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
            View innerLayout = (View) childInfoContainer.getChildAt(position);

            final TextView dobOfKidSpn = (TextView) innerLayout.findViewById(R.id.kidsDOBTextView);
            RadioGroup genderRadioGroup = (RadioGroup) innerLayout.findViewById(R.id.genderRadioGroup);
            RadioButton maleRadio = (RadioButton) innerLayout.findViewById(R.id.maleRadioButton);
            RadioButton femaleRadio = (RadioButton) innerLayout.findViewById(R.id.femaleRadioButton);

            int radioButtonID = genderRadioGroup.getCheckedRadioButtonId();
            View radioButton = genderRadioGroup.findViewById(radioButtonID);
//            int idx = genderRadioGroup.indexOfChild(radioButton);
//            RadioButton r = (RadioButton) genderRadioGroup.getChildAt(idx);
//            String selectedGender = r.getText().toString();

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

        addCityAndKidsInformationRequest.setCityId("" + selectedCityId);
        addCityAndKidsInformationRequest.setCityName("" + currentCityName);
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
                Toast.makeText(getActivity(), getString(R.string.went_wrong), Toast.LENGTH_SHORT).show();
//                gotToProfile();
                return;
            }

            try {
                UserDetailResponse responseData = (UserDetailResponse) response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    updateEventsResourcesConfigForCity();
//                    saveDatainDB();
                } else {
                    Toast.makeText(getActivity(), responseData.getReason(), Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4KException", Log.getStackTraceString(e));
                if (null != getActivity()) {
                    Toast.makeText(getActivity(), getString(R.string.went_wrong), Toast.LENGTH_SHORT).show();
                }
//                gotToProfile();
            }
        }

        @Override
        public void onFailure(Call<UserDetailResponse> call, Throwable t) {
            progressBar.setVisibility(View.GONE);
            Log.d("MC4kException", Log.getStackTraceString(t));
            Crashlytics.logException(t);
            Toast.makeText(getActivity(), getString(R.string.went_wrong), Toast.LENGTH_SHORT).show();
//            gotToProfile();
        }
    };

    private void updateEventsResourcesConfigForCity() {
        final VersionApiModel versionApiModel = SharedPrefUtils.getSharedPrefVersion(getActivity());
        final ConfigurationController _controller = new ConfigurationController(getActivity(), this);

        MetroCity model = new MetroCity();

        model.setId(selectedCityId);
        model.setName(currentCityName);
        model.setNewCityId(newSelectedCityId);

        SharedPrefUtils.setCurrentCityModel(getActivity(), model);
        SharedPrefUtils.setChangeCityFlag(getActivity(), true);

        if (selectedCityId > 0) {
            versionApiModel.setCityId(selectedCityId);
//            mFirebaseAnalytics.setUserProperty("CityId", cityModel.getCityId() + "");

            String version = AppUtils.getAppVersion(getActivity());
            if (!StringUtils.isNullOrEmpty(version)) {
                versionApiModel.setAppUpdateVersion(version);
            }

            if (!ConnectivityUtils.isNetworkEnabled(getActivity())) {
                ToastUtils.showToast(getActivity(), getString(R.string.error_network));
                return;

            }
            if (null != cityFragment) {
                cityFragment.dismiss();
            }
            _controller.getData(AppConstants.CONFIGURATION_REQUEST, versionApiModel);
        }
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

    @Override
    protected void updateUi(Response response) {
        if (response == null) {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(getActivity(), getResources().getString(R.string.server_error), Toast.LENGTH_SHORT).show();
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
                        }
                    });
                    _heavyDbTask.execute();
                }
                break;
            default:
                break;
        }
    }

    public void showDatePickerDialog() {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
    }

    @Override
    public void onCitySelect(CityInfoItem cityItem) {
        cityNameTextView.setText(cityItem.getCityName());
        currentCityName = cityItem.getCityName();
        selectedCityId = Integer.parseInt(cityItem.getId().replace("city-", ""));
        newSelectedCityId = cityItem.getId();
    }

    @Override
    public void onOtherCitySelect(int pos, String cityName) {
        currentCityName = cityName;
        selectedCityId = Integer.parseInt(mDatalist.get(pos).getId().replace("city-", ""));
        newSelectedCityId = mDatalist.get(pos).getId();
        mDatalist.get(pos).setCityName("Others(" + cityName + ")");
        cityNameTextView.setText(mDatalist.get(pos).getCityName());
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
            Date dateobj = (Date) formatter.parse(convertdate);
            timestamp = "" + (dateobj.getTime()) / 1000;
            return timestamp;
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return timestamp;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.saveTextView:
                if (validateFields()) {
                    saveUserDetails();
                    saveKidsAndCity();
                }
                break;
            case R.id.cityNameTextView:
                cityFragment = new CityListingDialogFragment();
                cityFragment.setTargetFragment(this, 0);
                Bundle _args = new Bundle();
                _args.putParcelableArrayList("cityList", mDatalist);
                _args.putString("fromScreen", "editProfile");
                cityFragment.setArguments(_args);
                FragmentManager fm = getChildFragmentManager();
                cityFragment.show(fm, "Replies");

                break;
        }
    }

    private boolean validateFields() {
        if (StringUtils.isNullOrEmpty(firstNameEditText.getText().toString().trim())) {
            Toast.makeText(getActivity(), getString(R.string.app_settings_edit_profile_toast_fn_empty), Toast.LENGTH_SHORT).show();
            return false;
        } else if (StringUtils.isNullOrEmpty(describeSelfEditText.getText().toString().trim())) {
            Toast.makeText(getActivity(), getString(R.string.app_settings_edit_profile_toast_user_bio_empty), Toast.LENGTH_SHORT).show();
            return false;
        } else if (StringUtils.isNullOrEmpty(blogTitleEditText.getText().toString().trim())) {
            Toast.makeText(getActivity(), getString(R.string.app_settings_edit_profile_toast_blog_title_empty), Toast.LENGTH_SHORT).show();
            return false;
        } else if (countWords(describeSelfEditText.getText().toString()) > MAX_WORDS) {
            Toast.makeText(getActivity(), getString(R.string.app_settings_edit_profile_toast_user_bio_max)
                    + " " + MAX_WORDS + " " + getString(R.string.app_settings_edit_profile_toast_user_bio_words), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void saveUserDetails() {
        UpdateUserDetailsRequest updateUserDetail = new UpdateUserDetailsRequest();
        updateUserDetail.setFirstName((firstNameEditText.getText().toString()).trim() + "");
        updateUserDetail.setUserBio(describeSelfEditText.getText().toString().trim() + "");
        updateUserDetail.setBlogTitle(blogTitleEditText.getText().toString().trim() + "");

        if (StringUtils.isNullOrEmpty(lastNameEditText.getText().toString().trim())) {
            updateUserDetail.setLastName(" ");
        } else {
            updateUserDetail.setLastName(lastNameEditText.getText().toString().trim() + "");
        }

        if (StringUtils.isNullOrEmpty(phoneEditText.getText().toString().trim())) {
            updateUserDetail.setMobile(" ");
        } else {
            updateUserDetail.setMobile(phoneEditText.getText().toString().trim() + "");
        }

        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        showProgressDialog(getResources().getString(R.string.please_wait));
        UserAttributeUpdateAPI userAttributeUpdateAPI = retrofit.create(UserAttributeUpdateAPI.class);
        Call<UserDetailResponse> call = userAttributeUpdateAPI.updateProfile(updateUserDetail);
        call.enqueue(userDetailsUpdateResponseListener);
    }

    private Callback<UserDetailResponse> userDetailsUpdateResponseListener = new Callback<UserDetailResponse>() {
        @Override
        public void onResponse(Call<UserDetailResponse> call, retrofit2.Response<UserDetailResponse> response) {
            removeProgressDialog();
            if (response == null || response.body() == null) {
//                showToast(getString(R.string.went_wrong));
                return;
            }
            UserDetailResponse responseData = (UserDetailResponse) response.body();
            if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
//                showToast("Successfully updated!");
                UserInfo model = SharedPrefUtils.getUserDetailModel(getActivity());
                model.setFirst_name(firstNameEditText.getText().toString());
                model.setLast_name(lastNameEditText.getText().toString());
                SharedPrefUtils.setUserDetailModel(getActivity(), model);
            } else {
//                showToast(responseData.getReason());
            }
        }

        @Override
        public void onFailure(Call<UserDetailResponse> call, Throwable t) {
            removeProgressDialog();
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };
}
