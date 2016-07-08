package com.mycity4kids.ui.fragment;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseFragment;
import com.kelltontech.utils.ConnectivityUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.ColorCode;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.controller.EditProfileController;
import com.mycity4kids.dbtable.TableKids;
import com.mycity4kids.models.forgot.CommonResponse;
import com.mycity4kids.models.profile.KidsInformation;
import com.mycity4kids.models.request.AddEditKidsInformationRequest;
import com.mycity4kids.models.response.KidsModel;
import com.mycity4kids.models.response.UserDetailResponse;
import com.mycity4kids.models.user.KidsInfo;
import com.mycity4kids.reminders.AppointmentManager;
import com.mycity4kids.retrofitAPIsInterfaces.LoginRegistrationAPI;
import com.mycity4kids.ui.activity.DashboardActivity;

import java.text.DateFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by manish.soni on 25-06-2015.
 */
public class FragmentKidProfile extends BaseFragment implements View.OnClickListener {

    View view;
    int id;
    String kids_name, kids_dob, kids_gender;
    KidsInfo selectedKidsInfo;
    ArrayList<KidsInfo> allKidsInfo;
    EditText name;
    static TextView kidBdy;
    private TextView mColorfrKid;
    private Dialog mColorPickerDialog;
    private String color_selected = "";
    private HashMap<String, String> used_colors = new HashMap<>();
    KidsInformation _requestModel;
    private ArrayList<KidsModel> kidsModelArrayList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.aa_kid_profile, container, false);

        if (getArguments() != null) {
            id = getArguments().getInt("KID_ID");
            kids_name = getArguments().getString("KID_NAME");
            kids_dob = getArguments().getString("KID_DOB");
            selectedKidsInfo = getArguments().getParcelable("KID_INFO");
            used_colors = (HashMap) getArguments().getSerializable("used_colors");
        }

        TableKids tableKids = new TableKids(BaseApplication.getInstance());
        allKidsInfo = tableKids.getAllKids();
        ((DashboardActivity) getActivity()).setTitle(selectedKidsInfo.getName());

        name = (EditText) view.findViewById(R.id.kids_name);
        kidBdy = (TextView) view.findViewById(R.id.kids_bdy);
        mColorfrKid = (TextView) view.findViewById(R.id.kidcolor);

        String key = new ColorCode().getKey(selectedKidsInfo.getColor_code());
        Drawable drawable = getResources().getDrawable(getResources()
                .getIdentifier("color_" + key + "xxhdpi", "drawable", getActivity().getPackageName()));
        mColorfrKid.setCompoundDrawablesWithIntrinsicBounds(null, null, null, drawable);
        mColorfrKid.setTag("" + key);

        mColorfrKid.setOnClickListener(this);

        kidBdy.setOnClickListener(this);

        name.setText(selectedKidsInfo.getName());
        kidBdy.setText(selectedKidsInfo.getDate_of_birth());

        return view;
    }


    public void showColorPickerDialog(final String name, final TextView textview) {

        // custom dialog
        mColorPickerDialog = new Dialog(getActivity());
        mColorPickerDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = mColorPickerDialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);


        LayoutInflater layoutInflater = (LayoutInflater) getActivity().getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view = (View) layoutInflater.inflate(R.layout.aa_colorpicker, null);

        mColorPickerDialog.setContentView(view);
        mColorPickerDialog.setCancelable(true);

        mColorPickerDialog.findViewById(R.id.color1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                color_selected = "1";
                setColor(name, textview);
            }
        });

        mColorPickerDialog.findViewById(R.id.color2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                color_selected = "2";
                setColor(name, textview);

            }
        });

        mColorPickerDialog.findViewById(R.id.color3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                color_selected = "3";
                setColor(name, textview);

            }
        });
        mColorPickerDialog.findViewById(R.id.color4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                color_selected = "4";
                setColor(name, textview);

            }
        });
        mColorPickerDialog.findViewById(R.id.color5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                color_selected = "5";
                setColor(name, textview);
            }
        });
        mColorPickerDialog.findViewById(R.id.color6).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                color_selected = "6";
                setColor(name, textview);

            }
        });
        mColorPickerDialog.findViewById(R.id.color7).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                color_selected = "7";
                setColor(name, textview);
            }
        });
        mColorPickerDialog.findViewById(R.id.color8).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                color_selected = "8";
                setColor(name, textview);
            }
        });
        mColorPickerDialog.findViewById(R.id.color9).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                color_selected = "9";
                setColor(name, textview);

            }
        });
        mColorPickerDialog.findViewById(R.id.color10).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                color_selected = "10";
                setColor(name, textview);
            }
        });


        mColorPickerDialog.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mColorPickerDialog.dismiss();
            }
        });


        Iterator myVeryOwnIterator = used_colors.keySet().iterator();
        while (myVeryOwnIterator.hasNext()) {
            String key = (String) myVeryOwnIterator.next();
            String value = (String) used_colors.get(key);
            view.findViewWithTag(value).setEnabled(false);
            view.findViewWithTag(value).setAlpha(0.2f);
        }

        mColorPickerDialog.show();


    }

    private boolean isDataValid() {
        boolean isLoginOk = true;
        String email_id = kidBdy.getText().toString();

        if (email_id.trim().length() == 0) {
            kidBdy.setFocusableInTouchMode(true);
            kidBdy.setError("Please enter dob");
            kidBdy.requestFocus();
            isLoginOk = false;
        } else if (name.getText().toString().length() == 0) {
            name.setFocusableInTouchMode(true);
            name.requestFocus();
            name.setError("Please enter name");
            isLoginOk = false;
        }
        return isLoginOk;
    }


    public void showDatePickerDialog() {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
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

    public String convertDateFormat(String _date) {
        DateFormat df = new SimpleDateFormat("yyyy-mm-dd");
        DateFormat df1 = new SimpleDateFormat("dd-mm-yyyy");

        Date date = new Date();
        try {
            date = df.parse(_date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return df1.format(date.getTime());
    }


    public static boolean chkTime(String time) {
        boolean result = true;

        String currentime = "" + (System.currentTimeMillis() / 1000);
        if (Integer.parseInt(currentime) < Integer.parseInt(convertDate(time)))
            result = false;

        return result;
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

            String sel_date = "" + day + "-" + (month + 1) + "-" + year;
            if (chkTime(sel_date)) {
                kidBdy.setText("" + day + "-" + (month + 1) + "-" + year);
            } else {
                kidBdy.setText("" + current_day + "-" + (current_month + 1) + "-" + curent_year);
            }

        }
    }

    public void callService() {


        if (ConnectivityUtils.isNetworkEnabled(getActivity())) {
            if (isDataValid()) {

                showProgressDialog(getString(R.string.please_wait));
                AddEditKidsInformationRequest addEditKidsInformationRequest = new AddEditKidsInformationRequest();
//                ArrayList<KidsInfo> kidsList = getEnteredKidsInfo();
                TableKids tableKids = new TableKids(BaseApplication.getInstance());
                ArrayList<KidsInfo> kidsInformations = (ArrayList<KidsInfo>) tableKids.getAllKids();

//                kidsList.addAll(kidsInformations);
                kidsModelArrayList = new ArrayList<>();

                for (int i = 0; i < allKidsInfo.size(); i++) {
                    KidsModel kmodel = new KidsModel();
                    if (id == i) {
                        kmodel.setName(name.getText().toString().trim());
                        kmodel.setBirthDay("" + convertStringToTimestamp(kidBdy.getText().toString().trim()));
                        kmodel.setColorCode(new ColorCode().getValue("" + mColorfrKid.getTag()));
//                        kmodel.setGender();
                    } else {
                        kmodel.setName(allKidsInfo.get(i).getName());
                        kmodel.setBirthDay("" + convertStringToTimestamp(allKidsInfo.get(i).getDate_of_birth()));
                        kmodel.setColorCode(allKidsInfo.get(i).getColor_code());
                    }
                    kidsModelArrayList.add(kmodel);
                }


                addEditKidsInformationRequest.setKids(kidsModelArrayList);

                Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
                LoginRegistrationAPI loginRegistrationAPI = retrofit.create(LoginRegistrationAPI.class);
                Call<UserDetailResponse> call = loginRegistrationAPI.addEditKidsInformation(addEditKidsInformationRequest);
                call.enqueue(onAddEditKidsResponseReceived);
//                _requestModel = new KidsInformation();
//                _requestModel.setDob(kidBdy.getText().toString().trim());
//                _requestModel.setName(name.getText().toString().trim());
//                _requestModel.setColor_code(new ColorCode().getValue("" + mColorfrKid.getTag()));
//                _requestModel.setKidid("" + id);
//
//                EditProfileController _controller = new EditProfileController(getActivity(), this);
//                _controller.getData(AppConstants.EDIT_KIDPROFILE_REQUEST, _requestModel);
            }


        } else {

            Toast.makeText(getActivity(), getString(R.string.error_network), Toast.LENGTH_SHORT).show();
            // showToast(getActivity().getString(R.string.error_network));
        }

    }

    Callback<UserDetailResponse> onAddEditKidsResponseReceived = new Callback<UserDetailResponse>() {
        @Override
        public void onResponse(Call<UserDetailResponse> call, retrofit2.Response<UserDetailResponse> response) {
            Log.d("SUCCESS", "" + response);
            removeProgressDialog();
            if (response == null || response.body() == null) {
                Toast.makeText(getActivity(), getString(R.string.went_wrong), Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                UserDetailResponse responseData = (UserDetailResponse) response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    saveDatainDB();
                    Toast.makeText(getActivity(), "dwadawdawdadawdawdawdawdawdawdawdawd", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), responseData.getReason(), Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("Exception", Log.getStackTraceString(e));
                Toast.makeText(getActivity(), getString(R.string.went_wrong), Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onFailure(Call<UserDetailResponse> call, Throwable t) {
            Log.d("MC4kException", Log.getStackTraceString(t));
            Crashlytics.logException(t);
            Toast.makeText(getActivity(), getString(R.string.went_wrong), Toast.LENGTH_SHORT).show();
        }
    };

    public void saveDatainDB() {

//        TableAdult adultTable = new TableAdult((BaseApplication) getActivity().getApplicationContext());
//        adultTable.deleteAll();
//        try {
//
//            adultTable.beginTransaction();
//            for (UserModel.AdultsInfo user : model.getResult().getData().getAdult()) {
//
//                adultTable.insertData(user.getUser());
//            }
//            adultTable.setTransactionSuccessful();
//        } finally {
//            adultTable.endTransaction();
//        }

        // saving child data
        TableKids kidsTable = new TableKids((BaseApplication) getActivity().getApplicationContext());
        kidsTable.deleteAll();
        try {
            kidsTable.beginTransaction();

            ArrayList<KidsInfo> kidsInfoArrayList = new ArrayList<>();

            for (KidsModel kid : kidsModelArrayList) {
                KidsInfo kidsInfo = new KidsInfo();
                kidsInfo.setName(kid.getName());
                kidsInfo.setDate_of_birth(convertTime(kid.getBirthDay()));
                kidsInfo.setColor_code(kid.getColorCode());
                kidsInfo.setGender(kid.getGender());

                kidsInfoArrayList.add(kidsInfo);
            }
            for (KidsInfo kids : kidsInfoArrayList) {

                kidsTable.insertData(kids);

            }
            kidsTable.setTransactionSuccessful();
        } finally {
            kidsTable.endTransaction();
        }

        ((DashboardActivity) getActivity()).replaceFragment(new FragmentFamilyDetail(), null, true);

        // saving family

//        TableFamily familyTable = new TableFamily((BaseApplication) getActivity().getApplicationContext());
//        familyTable.deleteAll();
//        try {
//
//            familyTable.insertData(model.getResult().getData().getFamily());
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        // update listview

//        mAdultContainer.removeAllViews();
//        mChildContainer.removeAllViews();
//        setList();

    }

    public long convertStringToTimestamp(String str_date) {
        try {
            DateFormat formatter;
            formatter = new SimpleDateFormat("dd-MM-yyyy");
            // you can change format of date
            Date date = formatter.parse(str_date);

            return date.getTime();
        } catch (ParseException e) {
            System.out.println("Exception :" + e);
            return 0;
        }
    }

    public String convertTime(String time) {
        Date date = new Date(Long.parseLong(time));
        Format format = new SimpleDateFormat("dd-MM-yyyy");
        return format.format(date);
    }

    protected void updateUi(Response response) {
        if (response == null) {
            removeProgressDialog();
            Toast.makeText(getActivity(), "Content not fetching from server side", Toast.LENGTH_SHORT).show();
            return;
        }

        CommonResponse responseData = (CommonResponse) response.getResponseObject();
        if (responseData.getResponseCode() == 200) {
            Toast.makeText(getActivity(), responseData.getResult().getMessage(), Toast.LENGTH_SHORT).show();
            // db update

            DateFormat df = new SimpleDateFormat("yyyy-mm-dd");
            DateFormat df1 = new SimpleDateFormat("dd-mm-yyyy");

            Date date = new Date();
            try {
                date = df1.parse(_requestModel.getDob());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            _requestModel.setDob(df.format(date.getTime()));

            TableKids tableKids = new TableKids(BaseApplication.getInstance());
            tableKids.updateKIDS(_requestModel);

            AppointmentManager.getInstance(getActivity()).clearList();

            ((DashboardActivity) getActivity()).replaceFragment(new FragmentFamilyDetail(), null, true);

        } else if (responseData.getResponseCode() == 400) {
            Toast.makeText(getActivity(), responseData.getResult().getMessage(), Toast.LENGTH_SHORT).show();

        }
        removeProgressDialog();


    }


    public void setColor(String id, TextView v) {

        used_colors.put(id, color_selected);
        // set on the custom view

        //ToastUtils.showToast(this,"color set");
        Drawable drawable = getResources().getDrawable(getResources()
                .getIdentifier("color_" + color_selected + "xxhdpi", "drawable", getActivity().getPackageName()));
        mColorfrKid.setCompoundDrawablesWithIntrinsicBounds(null, null, null, drawable);
        mColorfrKid.setTag("" + color_selected);


        mColorPickerDialog.dismiss();

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.kids_bdy:
                showDatePickerDialog();
                //datePicket((TextView) view);
                break;

            case R.id.kidcolor:

                showColorPickerDialog("kid", null);
                break;

        }


    }


//    public void datePicket(final TextView startDate) {
//
//        final Calendar c = Calendar.getInstance();
//        int mYear = c.get(Calendar.YEAR);
//        int mMonth = c.get(Calendar.MONTH);
//        int mDay = c.get(Calendar.DAY_OF_MONTH);
//
//        long maxdate = Long.parseLong(convertDate(mDay + "-" + (mMonth + 1) + "-" + mYear)) * 1000;
//
//
//        // Launch Date Picker Dialog
//        DatePickerDialog dpd = new DatePickerDialog(getActivity(), android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
//                new DatePickerDialog.OnDateSetListener() {
//
//                    @Override
//                    public void onDateSet(DatePicker view, int year,
//                                          int monthOfYear, int dayOfMonth) {
//                        // Display Selected date in textbox
//
//                        Calendar caltemp = Calendar.getInstance();
//                        caltemp.set(Calendar.DAY_OF_MONTH, dayOfMonth);
//                        caltemp.set(Calendar.MONTH, monthOfYear);
//                        caltemp.set(Calendar.YEAR, year);
//
//                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
//
////
////                        startDate.setText(dayOfMonth + "-"
////                                + (monthOfYear + 1) + "-" + year);
//                        startDate.setText(format.format(caltemp.getTime()));
//
//                        Log.d("Date ", (String) startDate.getText());
//                    }
//                }, mYear, mMonth, mDay);
//        dpd.getDatePicker().setMaxDate(maxdate);
//        dpd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        dpd.show();
//
//    }

}
