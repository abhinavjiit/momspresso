package com.mycity4kids.ui.fragment;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.widget.AppCompatSpinner;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.kelltontech.utils.DateTimeUtils;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.models.city.MetroCity;
import com.mycity4kids.models.request.AddRemoveKidsRequest;
import com.mycity4kids.models.request.UpdateUserDetailsRequest;
import com.mycity4kids.models.response.CityInfoItem;
import com.mycity4kids.models.response.KidsModel;
import com.mycity4kids.models.response.UserDetailResponse;
import com.mycity4kids.models.response.UserDetailResult;
import com.mycity4kids.models.user.KidsInfo;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.UserAttributeUpdateAPI;
import com.mycity4kids.ui.activity.ChangePasswordActivity;
import com.mycity4kids.ui.adapter.CustomSpinnerAdapter;
import com.mycity4kids.widget.KidsInfoNewCustomView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

public class About extends Fragment implements AdapterView.OnItemSelectedListener, View.OnClickListener, CityListingDialogFragment.IChangeCity {

    private boolean isEditFlag = false;
    private int kidsViewPosition;
    private String kidsInfoActionType = "";
    private ArrayList<AddRemoveKidsRequest> kidsModelArrayList;
    private KidsInfoNewCustomView viewInEditMode;
    private EditKidInfoDialogFragment editKidInfoDialogFragment;
    private LinearLayout childInfoContainer;
    private static TextView dobTextView;
    EditText aboutEditText;
    TextView kidsNameTV;
    LinearLayout aboutprofilemaincontainer;
    private UserDetailResult userDetail;
    private ArrayList<CityInfoItem> cityList;
    private TextView addNewKidTextView, kidsDOBTextView;
    private EditText kidNameEditText;
    private RelativeLayout addKidContainer;
    private AppCompatSpinner genderSpinner;
    int position;
    TextView editmail;
    private TextView emailTextView, cityTextView, handleNameTextView;
    private EditText phoneEditText, fullNameEditText;
    //    private UserDetailResult userDetail;
//    private ArrayList<CityInfoItem> cityList;
    private CityListingDialogFragment cityFragment;
    private String currentCityName;
    private String newSelectedCityId;
    private int selectedCityId;
    private TextView changePassTextView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile_about_layout, container, false);
        aboutEditText = (EditText) view.findViewById(R.id.aboutEditText);
        aboutprofilemaincontainer = (LinearLayout) view.findViewById(R.id.main_profile_About_layout);
        childInfoContainer = (LinearLayout) view.findViewById(R.id.childInfoContainer);
        addNewKidTextView = (TextView) view.findViewById(R.id.addNewKidTextView);
        kidNameEditText = (EditText) view.findViewById(R.id.kidNameEditText);
        kidsDOBTextView = (TextView) view.findViewById(R.id.kidsDOBTextView);
        addKidContainer = (RelativeLayout) view.findViewById(R.id.addKidContainer);
        genderSpinner = (AppCompatSpinner) view.findViewById(R.id.genderSpinner);
        userDetail = getArguments().getParcelable("userDetail");
        cityList = getArguments().getParcelableArrayList("cityList");

        addNewKidTextView.setOnClickListener(this);

        aboutEditText.setText("" + userDetail.getUserBio());

        ArrayList<String> genderList = new ArrayList<>();
        genderList.add("Male");
        genderList.add("Female");

        CustomSpinnerAdapter spinAdapter = new CustomSpinnerAdapter(getActivity(), genderList);
        genderSpinner.setAdapter(spinAdapter);
        genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapter, View v,
                                       int position, long id) {
                String item = adapter.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });

        kidsDOBTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dobTextView = kidsDOBTextView;
                showDatePickerDialog();
            }
        });

        position = 0;
        if (userDetail.getKids() != null) {
            for (KidsModel km : userDetail.getKids()) {
                addKidView(km, position);
                position++;
            }
        }

        emailTextView = (TextView) view.findViewById(R.id.emailTextView);
        handleNameTextView = (TextView) view.findViewById(R.id.handleNameTextView);
        phoneEditText = (EditText) view.findViewById(R.id.phoneEditText);
        cityTextView = (TextView) view.findViewById(R.id.cityTextView);
        fullNameEditText = (EditText) view.findViewById(R.id.fullNameEditText);
        changePassTextView = (TextView) view.findViewById(R.id.changePasswordTextView);

        userDetail = getArguments().getParcelable("userDetail");
        cityList = getArguments().getParcelableArrayList("cityList");

        cityTextView.setOnClickListener(this);
        changePassTextView.setOnClickListener(this);

        emailTextView.setText("" + userDetail.getEmail());
        handleNameTextView.setText("" + userDetail.getBlogTitle());
        fullNameEditText.setText("" + userDetail.getFirstName() + " " + userDetail.getLastName());
        if (userDetail.getPhone() != null) {
            if (userDetail.getPhone().getMobile() != null && userDetail.getPhone().getMobile().contains("+91")) {
                phoneEditText.setText("" + userDetail.getPhone().getMobile().replace("+91", ""));
            } else {
                phoneEditText.setText("" + userDetail.getPhone().getMobile());
            }
        }
        MetroCity currentCity = SharedPrefUtils.getCurrentCityModel(BaseApplication.getAppContext());
        for (int i = 0; i < cityList.size(); i++) {
            int cId = Integer.parseInt(cityList.get(i).getId().replace("city-", ""));
            if (currentCity.getId() == cId) {
                cityList.get(i).setSelected(true);
                cityTextView.setText(cityList.get(i).getCityName());
            } else {
                cityList.get(i).setSelected(false);
            }
        }

        return view;
    }

    private void addKidView(final KidsModel km, final int position) {
        try {
            final KidsInfoNewCustomView kidsInfo1 = new KidsInfoNewCustomView(getActivity());
            if (km == null) {
                kidsInfo1.setKids_bdy(BaseApplication.getAppContext().getString(R.string.dob));
            } else {
                kidsInfo1.setKidName(km.getName());
                kidsInfo1.setKids_bdy(DateTimeUtils.getKidsDOBNanoMilliTimestamp("" + km.getBirthDay()));
                if ("0".equals(km.getGender())) {
                    kidsInfo1.setGenderAsMale(true);
                } else {
                    kidsInfo1.setGenderAsFemale(true);
                }
            }
            kidsInfo1.getEditKidInfoIV().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    isEditFlag = true;
                    kidsViewPosition = position;
                    viewInEditMode = kidsInfo1;
                    editKidInfoDialogFragment = new EditKidInfoDialogFragment();
                    FragmentManager fm = getChildFragmentManager();
                    Bundle _args = new Bundle();
                    _args.putParcelable("editKidInfo", km);
                    editKidInfoDialogFragment.setArguments(_args);
                    //  editKidInfoDialogFragment.setTargetFragment(About.this, 1111);
                    editKidInfoDialogFragment.setCancelable(true);
                    editKidInfoDialogFragment.show(fm, "Choose video option");
                }
            });

            childInfoContainer.addView(kidsInfo1);
        } catch (Exception ex) {
            Crashlytics.logException(ex);
            Log.d("MC4KException", Log.getStackTraceString(ex));
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // Toast.makeText(getApplicationContext(), country[position], Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addNewKidTextView:
                if (validateKidsInfo()) {
                    kidsInfoActionType = "ADD";
                    saveKidsInfo();
                }
                break;

            case R.id.cityTextView:
                cityFragment = new CityListingDialogFragment();
                // cityFragment.setTargetFragment(this, 0);
                Bundle _args = new Bundle();
                _args.putParcelableArrayList("cityList", cityList);
                _args.putString("fromScreen", "editProfile");
                cityFragment.setArguments(_args);
                FragmentManager fm = getChildFragmentManager();
                cityFragment.show(fm, "Replies");
                break;
            case R.id.changePasswordTextView:
                Intent intent = new Intent(getActivity(), ChangePasswordActivity.class);
                startActivity(intent);
                break;
        }
    }

    private boolean validateKidsInfo() {
        if (StringUtils.isNullOrEmpty(kidsDOBTextView.getText().toString()) || !DateTimeUtils.isValidDate(kidsDOBTextView.getText().toString())) {
            Toast.makeText(getActivity(), getString(R.string.app_settings_edit_profile_toast_incorrect_date), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private ArrayList<KidsInfo> getEnteredKidsInfo() {
        ArrayList<KidsInfo> kidsInfoList = new ArrayList<KidsInfo>();

        for (int position = 0; position < childInfoContainer.getChildCount(); position++) {
            View innerLayout = childInfoContainer.getChildAt(position);

            final TextView dobOfKidSpn = (TextView) innerLayout.findViewById(R.id.kidsDOBTextView);
            kidsNameTV = (TextView) innerLayout.findViewById(R.id.nameTextView);
            TextView genderTV = (TextView) innerLayout.findViewById(R.id.genderTextView);

            KidsInfo kidsInformation = new KidsInfo();
            kidsInformation.setName(kidsNameTV.getText().toString());
            kidsInformation.setDate_of_birth(dobOfKidSpn.getText().toString().trim());
            if ("Male".equalsIgnoreCase(genderTV.getText().toString())) {
                kidsInformation.setGender("0");
            } else {
                kidsInformation.setGender("1");
            }
            kidsInfoList.add(kidsInformation);
        }
        return kidsInfoList;
    }

    private void saveKidsInfo() {
        kidsModelArrayList = new ArrayList<>();
        ArrayList<KidsInfo> kidsList = getEnteredKidsInfo();

        kidsModelArrayList = new ArrayList<>();
        for (KidsInfo ki : kidsList) {
            AddRemoveKidsRequest kmodel = new AddRemoveKidsRequest();
            kmodel.setName(ki.getName());
            long bdaytimestamp = DateTimeUtils.convertStringToTimestamp(ki.getDate_of_birth());
            if (bdaytimestamp != 0) {
                kmodel.setBirthDay(bdaytimestamp * 1000);
            } else {
                if (isAdded())
                    Toast.makeText(getActivity(), getString(R.string.complete_blogger_profile_incorrect_date), Toast.LENGTH_SHORT).show();
                return;
            }
            kmodel.setGender(ki.getGender());
            kidsModelArrayList.add(kmodel);
        }

        if ("ADD".equals(kidsInfoActionType)) {
            AddRemoveKidsRequest kmodel = new AddRemoveKidsRequest();
            if (StringUtils.isNullOrEmpty(kidNameEditText.getText().toString())) {
                kmodel.setName(" ");
            } else {
                kmodel.setName(kidNameEditText.getText().toString());
            }

            long bdaytimestamp = DateTimeUtils.convertStringToTimestamp(kidsDOBTextView.getText().toString());
            kmodel.setBirthDay(bdaytimestamp * 1000);

            if ("Male".equals(genderSpinner.getSelectedItem().toString())) {
                kmodel.setGender("0");
            } else {
                kmodel.setGender("1");
            }
            kidsModelArrayList.add(kmodel);
        }

        UpdateUserDetailsRequest addCityAndKidsInformationRequest = new UpdateUserDetailsRequest();
        addCityAndKidsInformationRequest.setKids(kidsModelArrayList);

        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        UserAttributeUpdateAPI userAttributeUpdateAPI = retrofit.create(UserAttributeUpdateAPI.class);
        Call<UserDetailResponse> call = userAttributeUpdateAPI.updateProfile(addCityAndKidsInformationRequest);
        call.enqueue(updateKidsInfoResponseListener);
    }

    private void postKidsData() {
        kidsModelArrayList = new ArrayList<>();
        ArrayList<KidsInfo> kidsList = getEnteredKidsInfo();

        kidsModelArrayList = new ArrayList<>();
        for (KidsInfo ki : kidsList) {
            AddRemoveKidsRequest kmodel = new AddRemoveKidsRequest();
            kmodel.setName(ki.getName());
            long bdaytimestamp = DateTimeUtils.convertStringToTimestamp(ki.getDate_of_birth());
            if (bdaytimestamp != 0) {
                kmodel.setBirthDay(bdaytimestamp * 1000);
            } else {
                if (isAdded())
                    Toast.makeText(getActivity(), getString(R.string.complete_blogger_profile_incorrect_date), Toast.LENGTH_SHORT).show();
                return;
            }
            kmodel.setGender(ki.getGender());
            kidsModelArrayList.add(kmodel);
        }

        if ("ADD".equals(kidsInfoActionType)) {
            AddRemoveKidsRequest kmodel = new AddRemoveKidsRequest();
            if (StringUtils.isNullOrEmpty(kidNameEditText.getText().toString())) {
                kmodel.setName(" ");
            } else {
                kmodel.setName(kidNameEditText.getText().toString());
            }

            long bdaytimestamp = DateTimeUtils.convertStringToTimestamp(kidsDOBTextView.getText().toString());
            kmodel.setBirthDay(bdaytimestamp * 1000);

            if ("Male".equals(genderSpinner.getSelectedItem().toString())) {
                kmodel.setGender("0");
            } else {
                kmodel.setGender("1");
            }
            kidsModelArrayList.add(kmodel);
        }


    }

    public void saveEditKidInfo(KidsInfo kidsinfo) {
        viewInEditMode.setKidName(kidsinfo.getName());
        viewInEditMode.setKids_bdy(kidsinfo.getDate_of_birth());
        if ("0".equals(kidsinfo.getGender())) {
            viewInEditMode.setGenderAsMale(true);
        } else {
            viewInEditMode.setGenderAsFemale(true);
        }
        kidsInfoActionType = "EDIT";
        saveKidsInfo();
    }

    public void deleteKid() {
        kidsInfoActionType = "DELETE";
        kidsModelArrayList = new ArrayList<>();
        ArrayList<KidsInfo> kidsList = getEnteredKidsInfo();

        kidsModelArrayList = new ArrayList<>();
        for (KidsInfo ki : kidsList) {
            AddRemoveKidsRequest kmodel = new AddRemoveKidsRequest();
            kmodel.setName(ki.getName());
            long bdaytimestamp = DateTimeUtils.convertStringToTimestamp(ki.getDate_of_birth());
            if (bdaytimestamp != 0) {
                kmodel.setBirthDay(bdaytimestamp * 1000);
            } else {
                if (isAdded())
                    Toast.makeText(getActivity(), getString(R.string.complete_blogger_profile_incorrect_date), Toast.LENGTH_SHORT).show();
                return;
            }
            kmodel.setGender(ki.getGender());
            kidsModelArrayList.add(kmodel);
        }
        if (kidsModelArrayList.size() == kidsViewPosition) {
            kidsModelArrayList.remove(kidsViewPosition - 1);
        } else {
            kidsModelArrayList.remove(kidsViewPosition);
        }
        UpdateUserDetailsRequest addCityAndKidsInformationRequest = new UpdateUserDetailsRequest();
        addCityAndKidsInformationRequest.setKids(kidsModelArrayList);

        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        UserAttributeUpdateAPI userAttributeUpdateAPI = retrofit.create(UserAttributeUpdateAPI.class);
        Call<UserDetailResponse> call = userAttributeUpdateAPI.updateProfile(addCityAndKidsInformationRequest);
        call.enqueue(updateKidsInfoResponseListener);
    }

    private Callback<UserDetailResponse> updateKidsInfoResponseListener = new Callback<UserDetailResponse>() {
        @Override
        public void onResponse(Call<UserDetailResponse> call, retrofit2.Response<UserDetailResponse> response) {
            if (editKidInfoDialogFragment != null) {
                editKidInfoDialogFragment.dismiss();
            }
            Log.d("SUCCESS", "" + response);
            if (response == null || response.body() == null) {
                Toast.makeText(getActivity(), getString(R.string.went_wrong), Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                UserDetailResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    if ("DELETE".equals(kidsInfoActionType)) {
//                        childInfoContainer.removeViewAt(kidsViewPosition);
                        childInfoContainer.removeAllViews();
                        int pos = 0;
                        for (AddRemoveKidsRequest km : kidsModelArrayList) {
                            KidsModel km1 = new KidsModel();
                            km1.setName(km.getName());
                            km1.setBirthDay("" + km.getBirthDay());
                            km1.setGender(km.getGender());
                            addKidView(km1, pos);
                            pos++;
                        }
                    } else if ("EDIT".equals(kidsInfoActionType)) {

                    } else {
                        KidsModel km = new KidsModel();
                        km.setName(kidsModelArrayList.get(kidsModelArrayList.size() - 1).getName());
                        km.setBirthDay("" + kidsModelArrayList.get(kidsModelArrayList.size() - 1).getBirthDay());
                        km.setGender(kidsModelArrayList.get(kidsModelArrayList.size() - 1).getGender());
                        addKidView(km, kidsModelArrayList.size());
                    }
                    addNewKidTextView.setText(BaseApplication.getAppContext().getString(R.string.app_settings_edit_prefs_add));
                    kidNameEditText.setText("");
                    kidsDOBTextView.setText(BaseApplication.getAppContext().getString(R.string.app_settings_edit_profile_dob));
                } else {
                    Toast.makeText(getActivity(), responseData.getReason(), Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4KException", Log.getStackTraceString(e));
                if (isAdded()) {
                    Toast.makeText(getActivity(), getString(R.string.went_wrong), Toast.LENGTH_SHORT).show();
                }
            }
        }

        @Override
        public void onFailure(Call<UserDetailResponse> call, Throwable t) {
            if (editKidInfoDialogFragment != null) {
                editKidInfoDialogFragment.dismiss();
            }
            Crashlytics.logException(t);
            Log.d("MC4KException", Log.getStackTraceString(t));
        }
    };

    public void showDatePickerDialog() {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
    }

    @Override
    public void onCitySelect(CityInfoItem cityItem) {
        cityTextView.setText(cityItem.getCityName());
        currentCityName = cityItem.getCityName();
        selectedCityId = Integer.parseInt(cityItem.getId().replace("city-", ""));
        newSelectedCityId = cityItem.getId();
    }

    @Override
    public void onOtherCitySelect(int pos, String cityName) {
        currentCityName = cityName;
        selectedCityId = Integer.parseInt(cityList.get(pos).getId().replace("city-", ""));
        newSelectedCityId = cityList.get(pos).getId();
        cityList.get(pos).setCityName("Others(" + cityName + ")");
        cityTextView.setText(cityList.get(pos).getCityName());
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
            Date dateobj = formatter.parse(convertdate);
            timestamp = "" + (dateobj.getTime()) / 1000;
            return timestamp;
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return timestamp;
    }

    public EditText getAboutEditText() {
        return aboutEditText;
    }

    public TextView getHandleNameTextView() {
        return handleNameTextView;
    }

    public EditText getPhoneEditText() {
        return phoneEditText;
    }

    public EditText getFullNameEditText() {
        return fullNameEditText;
    }

    public String getCurrentCityName() {
        return currentCityName;
    }

    public int getSelectedCityId() {
        return selectedCityId;
    }

    public String getNewSelectedCityId() {
        return newSelectedCityId;
    }
}


