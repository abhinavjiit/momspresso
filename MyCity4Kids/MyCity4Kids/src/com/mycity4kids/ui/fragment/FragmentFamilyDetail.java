package com.mycity4kids.ui.fragment;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.kelltontech.ui.BaseFragment;
import com.kelltontech.utils.ConnectivityUtils;
import com.kelltontech.utils.ToastUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.ColorCode;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.controller.AddUserKidsController;
import com.mycity4kids.controller.EditProfileController;
import com.mycity4kids.controller.ImageUploadController;
import com.mycity4kids.dbtable.TableFamily;
import com.mycity4kids.dbtable.TableKids;
import com.mycity4kids.dbtable.UserTable;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.listener.OnButtonClicked;
import com.mycity4kids.models.forgot.CommonResponse;
import com.mycity4kids.models.profile.KidsInformation;
import com.mycity4kids.models.profile.SignUpModel;
import com.mycity4kids.models.request.AddEditKidsInformationRequest;
import com.mycity4kids.models.response.KidsModel;
import com.mycity4kids.models.response.UserDetailResponse;
import com.mycity4kids.models.user.ImageUploadRequest;
import com.mycity4kids.models.user.KidsInfo;
import com.mycity4kids.models.user.UserModel;
import com.mycity4kids.models.user.UserResponse;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.LoginRegistrationAPI;
import com.mycity4kids.ui.activity.SettingsActivity;
import com.mycity4kids.ui.adapter.AdapterKidAdultList;
import com.mycity4kids.ui.dialog.PhotoOptionsDialog;
import com.mycity4kids.widget.CustomListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
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
public class FragmentFamilyDetail extends BaseFragment implements View.OnClickListener {


    View view;
    TextView additionalChild;
    CustomListView addChildAdult;
    AdapterKidAdultList adapterKidAdultList;
    private LinearLayout mChildContainer;
    private HashMap<String, String> used_colors = new HashMap<>();
    private int childCount = 0;
    private static TextView BdayView;
    private Dialog mColorPickerDialog;
    private String color_selected = "";
    private SignUpModel signupModel;
    private SignUpModel.Family mFamilyModel;
    private File photo;
    private boolean mAddKidsApiComleted, mUpdateProfileRequestCompleted;
    private float density;
    private File mFileTemp;
    private ScrollView scrollview;
    private boolean ifAdultAdded = false;
    ArrayList<KidsModel> kidsModelArrayList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.aa_family_detail, container, false);
        Utils.pushOpenScreenEvent(getActivity(), "Family Details ", SharedPrefUtils.getUserDetailModel(getActivity()).getDynamoId() + "");

        density = getActivity().getResources().getDisplayMetrics().density;
        additionalChild = (TextView) view.findViewById(R.id.additional_child);
        addChildAdult = (CustomListView) view.findViewById(R.id.add_kid_adult);
        scrollview = (ScrollView) view.findViewById(R.id.scroll_view);

        setHasOptionsMenu(true);
        addChildAdult.isExpanded();
        mChildContainer = (LinearLayout) view.findViewById(R.id.internal_kid_layout);

        additionalChild.setOnClickListener(this);
        ((SettingsActivity) getActivity()).setTitle("Family Details");

        setList();

        addChildAdult.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                KidsInfo attendeeModel = (KidsInfo) adapterKidAdultList.getItem(i);
                Bundle bundle = new Bundle();
                bundle.putInt("KID_ID", i);
                bundle.putString("KID_NAME", attendeeModel.getName());
                bundle.putString("KID_DOB", attendeeModel.getDate_of_birth());
                bundle.putParcelable("KID_INFO", attendeeModel);
                bundle.putSerializable("used_colors", used_colors);
                ((SettingsActivity) getActivity()).replaceFragment(new FragmentKidProfile(), bundle, true);
            }
        });

        return view;
    }

    public void setList() {

        TableKids tableKids = new TableKids(BaseApplication.getInstance());
        ArrayList<KidsInfo> kidsInformations = (ArrayList<KidsInfo>) tableKids.getAllKids();

        adapterKidAdultList = new AdapterKidAdultList(getActivity(), kidsInformations);
        addChildAdult.setAdapter(adapterKidAdultList);

        // setting used colors
        used_colors.clear();
        for (int i = 0; i < kidsInformations.size(); i++) {
            String key = new ColorCode().getKey(kidsInformations.get(i).getColor_code());
            used_colors.put("" + i, key);
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.additional_adult:
                ifAdultAdded = true;
                sendScroll();
                break;
            case R.id.additional_child:
                ifAdultAdded = false;
                addNewChild();
                sendScroll();
                break;
            case R.id.imgProfile:
                showPhotoOptionsDialog();
                break;
        }
    }

    private void showPhotoOptionsDialog() {
        PhotoOptionsDialog photoOptionsDialog = new PhotoOptionsDialog();
        photoOptionsDialog.setonButtonClickListener(new OnButtonClicked() {
            @Override
            public void onButtonCLick(int buttonId) {
                switch (buttonId) {
                    case R.id.btnCamera:
                        String state = Environment.getExternalStorageState();
                        if (Environment.MEDIA_MOUNTED.equals(state)) {
                            mFileTemp = new File(Environment.getExternalStorageDirectory(), Constants.TEMP_PHOTO_FILE_NAME);
                        } else {
                            mFileTemp = new File(getActivity().getFilesDir(), Constants.TEMP_PHOTO_FILE_NAME);
                        }

                        startCamera();
                        break;
                    case R.id.btnGallery:
                        state = Environment.getExternalStorageState();
                        if (Environment.MEDIA_MOUNTED.equals(state)) {
                            mFileTemp = new File(Environment.getExternalStorageDirectory(), Constants.TEMP_PHOTO_FILE_NAME);
                        } else {
                            mFileTemp = new File(getActivity().getFilesDir(), Constants.TEMP_PHOTO_FILE_NAME);
                        }

                        openGallery();
                        break;
                }
            }
        });
        photoOptionsDialog.show(getActivity().getSupportFragmentManager(), photoOptionsDialog.getClass().getSimpleName());
    }

    public void showSelectedcolorMessage(View v, final String name, final TextView textview, String colorsSelected) {
        if (v.getAlpha() == 0.2f) {
            ((BaseActivity) getActivity()).showSnackbar(getView().findViewById(R.id.root), getActivity().getResources().getString(R.string.color_selected));
        } else {
            color_selected = colorsSelected;
            setColor(name, textview);
        }
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
                showSelectedcolorMessage(v, name, textview, "1");

            }
        });

        mColorPickerDialog.findViewById(R.id.color2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSelectedcolorMessage(v, name, textview, "2");
            }
        });

        mColorPickerDialog.findViewById(R.id.color3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSelectedcolorMessage(v, name, textview, "3");

            }
        });
        mColorPickerDialog.findViewById(R.id.color4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSelectedcolorMessage(v, name, textview, "4");
            }
        });
        mColorPickerDialog.findViewById(R.id.color5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSelectedcolorMessage(v, name, textview, "5");
            }
        });
        mColorPickerDialog.findViewById(R.id.color6).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSelectedcolorMessage(v, name, textview, "6");

            }
        });
        mColorPickerDialog.findViewById(R.id.color7).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSelectedcolorMessage(v, name, textview, "7");
            }
        });
        mColorPickerDialog.findViewById(R.id.color8).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSelectedcolorMessage(v, name, textview, "8");
            }
        });
        mColorPickerDialog.findViewById(R.id.color9).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSelectedcolorMessage(v, name, textview, "9");

            }
        });
        mColorPickerDialog.findViewById(R.id.color10).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSelectedcolorMessage(v, name, textview, "10");

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
            view.findViewWithTag(value).setAlpha(0.2f);
        }

        mColorPickerDialog.show();


    }

    public void setColor(String id, TextView v) {
        used_colors.put(id, color_selected);
        // set on the custom view
        if (v != null) {
            Drawable drawable = getResources().getDrawable(getResources()
                    .getIdentifier("color_" + color_selected + "xxhdpi", "drawable", getActivity().getPackageName()));
            v.setCompoundDrawablesWithIntrinsicBounds(null, null, null, drawable);
            v.setTag("" + color_selected);
        }
        mColorPickerDialog.dismiss();

    }

    private ArrayList<KidsInfo> getEnteredKidsInfo() {
        ArrayList<KidsInfo> kidsInfoList = new ArrayList<KidsInfo>();

        for (int position = 0; position < mChildContainer.getChildCount(); position++) {
            View innerLayout = (View) mChildContainer.getChildAt(position);

            EditText nameOfKidEdt = (EditText) innerLayout.findViewById(R.id.kids_name);
            TextView dobOfKidSpn = (TextView) innerLayout.findViewById(R.id.kids_bdy);
            final TextView kidcolor = (TextView) innerLayout.findViewById(R.id.kidcolor);

            if ((nameOfKidEdt.getText().toString().trim().equals("")) || (dobOfKidSpn.getText().toString().trim().equals(""))) {
            } else {
                KidsInfo kidsInformation = new KidsInfo();
                kidsInformation.setName((nameOfKidEdt).getText().toString().trim());
                kidsInformation.setDate_of_birth((dobOfKidSpn).getText().toString().trim());
                kidsInformation.setColor_code(new ColorCode().getValue("" + kidcolor.getTag()));
                kidsInfoList.add(kidsInformation);
            }
        }
        return kidsInfoList;
    }


    private ArrayList<KidsInformation> getKidsInfo() {
        ArrayList<KidsInformation> kidsInfoList = new ArrayList<KidsInformation>();


        for (int position = 0; position < mChildContainer.getChildCount(); position++) {
            View innerLayout = (View) mChildContainer.getChildAt(position);

            EditText nameOfKidEdt = (EditText) innerLayout.findViewById(R.id.kids_name);
            TextView dobOfKidSpn = (TextView) innerLayout.findViewById(R.id.kids_bdy);
            final TextView kidcolor = (TextView) innerLayout.findViewById(R.id.kidcolor);


            if ((nameOfKidEdt.getText().toString().trim().equals("")) || (dobOfKidSpn.getText().toString().trim().equals(""))) {
            } else {
                KidsInformation kidsInformation = new KidsInformation();
                kidsInformation.setName((nameOfKidEdt).getText().toString().trim());
                kidsInformation.setBirthday((dobOfKidSpn).getText().toString().trim());
                kidsInformation.setColor_code(new ColorCode().getValue("" + kidcolor.getTag()));
                kidsInfoList.add(kidsInformation);
            }
        }
        return kidsInfoList;
    }

    private SignUpModel getSignUpRequestModel() {

        ArrayList<KidsInformation> kidsArray = new ArrayList<KidsInformation>();
        kidsArray = getKidsInfo();

        SignUpModel _requestModel = new SignUpModel();
        _requestModel.setKidInformation(kidsArray);

        return _requestModel;
    }

    public boolean checkCustomLayoutValidations() {
        boolean result = true;

        // checking kids validation
        for (int position = 0; position < mChildContainer.getChildCount(); position++) {
            View innerLayout = (View) mChildContainer.getChildAt(position);

            EditText nameOfKidEdt = (EditText) innerLayout.findViewById(R.id.kids_name);
            TextView dobOfKidSpn = (TextView) innerLayout.findViewById(R.id.kids_bdy);

            if (!nameOfKidEdt.getText().toString().trim().equals("")) {

                if (dobOfKidSpn.getText().toString().trim().equals("")) {

                    dobOfKidSpn.setError(getResources().getString(R.string.please_enter_dob));
                    dobOfKidSpn.setFocusableInTouchMode(true);
                    dobOfKidSpn.requestFocus();

                    return false;
                }
            } else {
                nameOfKidEdt.setError(getResources().getString(R.string.please_enter_name));
                nameOfKidEdt.setFocusableInTouchMode(true);
                nameOfKidEdt.requestFocus();
                return false;
            }

        }
        return result;
    }


    public void hitApiRequest(int requestType) {

        if (!ConnectivityUtils.isNetworkEnabled(getActivity())) {
            ((BaseActivity) getActivity()).showSnackbar(getView().findViewById(R.id.root), getString(R.string.error_network));
            return;
        }

        switch (requestType) {
            case AppConstants.ADD_ADDITIONAL_USERKID_REQ:

                signupModel = getSignUpRequestModel();
                AddUserKidsController _controller = new AddUserKidsController(getActivity(), this);
                _controller.getData(AppConstants.ADD_ADDITIONAL_USERKID_REQ, signupModel);

                break;
            case AppConstants.EDIT_FAMILY_REQUEST:
                if (checkCustomLayoutValidations()
                        ) {
                    showProgressDialog(getString(R.string.please_wait));
                    mFamilyModel = new SignUpModel().new Family();

                    EditProfileController _editcontroller = new EditProfileController(getActivity(), this);
                    _editcontroller.getData(AppConstants.EDIT_FAMILY_REQUEST, mFamilyModel);
                }
                break;
        }


    }

    public void saveDatainDB() {

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

        mChildContainer.removeAllViews();
        setList();

    }

    public String convertTime(String time) {
        Date date = new Date(Long.parseLong(time) * 1000);
        Format format = new SimpleDateFormat("dd-MM-yyyy");
        return format.format(date);
    }

    protected void updateUi(Response response) {
        if (response == null) {
            removeProgressDialog();
            ((BaseActivity) getActivity()).showSnackbar(getView().findViewById(R.id.root), getResources().getString(R.string.server_error));
//            Toast.makeText(getActivity(), "Content not fetching from server side", Toast.LENGTH_SHORT).show();
            return;
        }

        switch (response.getDataType()) {

            case AppConstants.ADD_ADDITIONAL_USERKID_REQ:
                mAddKidsApiComleted = true;
                UserResponse responseData = (UserResponse) response.getResponseObject();
                if (responseData.getResponseCode() == 200) {
                    removeProgressDialog();
                    mChildContainer.removeAllViews();
                    ((BaseActivity) getActivity()).showSnackbar(getView().findViewById(R.id.root), responseData.getResult().getMessage());
                } else if (responseData.getResponseCode() == 400) {
                    ((BaseActivity) getActivity()).showSnackbar(getView().findViewById(R.id.root), responseData.getResult().getMessage() + "");
                    removeProgressDialog();
                }
                break;
            case AppConstants.EDIT_FAMILY_REQUEST:
                mUpdateProfileRequestCompleted = true;
                CommonResponse commonresponseData = (CommonResponse) response.getResponseObject();
                if (commonresponseData.getResponseCode() == 200) {
                    // db update
                    TableFamily tableKids = new TableFamily(BaseApplication.getInstance());
                    tableKids.updateVal(mFamilyModel);
                    if (mChildContainer.getChildCount() > 0) {
                        hitApiRequest(AppConstants.ADD_ADDITIONAL_USERKID_REQ);
                    } else {
                        removeProgressDialog();
                        ((BaseActivity) getActivity()).showSnackbar(getView().findViewById(R.id.root), commonresponseData.getResult().getMessage() + "");
                    }

                } else if (commonresponseData.getResponseCode() == 400) {
                    ((BaseActivity) getActivity()).showSnackbar(getView().findViewById(R.id.root), commonresponseData.getResult().getMessage() + "");
                    removeProgressDialog();
                }
                break;
        }
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

    public static boolean chkTime(String time) {
        boolean result = true;

        String currentime = "" + (System.currentTimeMillis() / 1000);
        if (Integer.parseInt(currentime) < Integer.parseInt(convertDate(time)))
            result = false;

        return result;
    }

    public void onHeaderButtonTapped() {

        ArrayList<KidsInfo> kidsList = getEnteredKidsInfo();
        TableKids tableKids = new TableKids(BaseApplication.getInstance());
        ArrayList<KidsInfo> kidsInformations = (ArrayList<KidsInfo>) tableKids.getAllKids();

        kidsModelArrayList = new ArrayList<>();
        for (KidsInfo ki : kidsList) {
            KidsModel kmodel = new KidsModel();
            kmodel.setName(ki.getName());

            long bdaytimestamp = convertStringToTimestamp(ki.getDate_of_birth());
            if (bdaytimestamp != 0) {
                kmodel.setBirthDay("" + bdaytimestamp);
            } else {
                Toast.makeText(getActivity(), "incorrect kids bday", Toast.LENGTH_SHORT).show();
            }

            kmodel.setColorCode(ki.getColor_code());
            kmodel.setGender(ki.getGender());

            kidsModelArrayList.add(kmodel);
        }

        for (KidsInfo ki : kidsInformations) {
            KidsModel kmodel = new KidsModel();
            kmodel.setName(ki.getName());
            long bdaytimestamp = convertStringToTimestamp(ki.getDate_of_birth());
            if (bdaytimestamp != 0) {
                kmodel.setBirthDay("" + bdaytimestamp);
            } else {
                Toast.makeText(getActivity(), "incorrect kids bday", Toast.LENGTH_SHORT).show();
            }

            kmodel.setColorCode(ki.getColor_code());
            kmodel.setGender(ki.getGender());

            kidsModelArrayList.add(kmodel);
        }

        addEditKidsDetails();
    }

    private void addEditKidsDetails() {
        AddEditKidsInformationRequest addEditKidsInformationRequest = new AddEditKidsInformationRequest();
        addEditKidsInformationRequest.setKids(kidsModelArrayList);

        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        LoginRegistrationAPI loginRegistrationAPI = retrofit.create(LoginRegistrationAPI.class);
        Call<UserDetailResponse> call = loginRegistrationAPI.addEditKidsInformation(addEditKidsInformationRequest);
        call.enqueue(onAddEditKidsResponseReceived);
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
                } else {
                    Toast.makeText(getActivity(), responseData.getReason(), Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4KException", Log.getStackTraceString(e));
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

    private void addDynamicChild() {
        ++childCount;
        LayoutInflater layoutInflater = (LayoutInflater) getActivity().getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View convertView = (View) layoutInflater.inflate(R.layout.aa_addchild, null);
        convertView.setTag("kid" + childCount);
        convertView.setId(childCount);

        EditText nameOfKidEdt = (EditText) convertView.findViewById(R.id.kids_name);
        final TextView dobOfKidSpn = (TextView) convertView.findViewById(R.id.kids_bdy);
        final TextView kidcolor = (TextView) convertView.findViewById(R.id.kidcolor);
        final TextView txtKidname = (TextView) convertView.findViewById(R.id.txtkidname);
        final TextView txtKidbdy = (TextView) convertView.findViewById(R.id.txtkidbdy);

        final TextView deleteView = (TextView) convertView.findViewById(R.id.cross);

        deleteView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mChildContainer.removeView(convertView);
            }
        });

        txtKidbdy.setText("KID'S BIRTHDAY");
        txtKidname.setText("KID'S NAME");


        dobOfKidSpn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BdayView = dobOfKidSpn;
                showDatePickerDialog();
            }
        });


        kidcolor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showColorPickerDialog("kid" + convertView.getId(), kidcolor);
            }
        });

        int digit;
        Drawable drawable;

        digit = getRandomNumber();
        drawable = getResources().getDrawable(getResources()
                .getIdentifier("color_" + digit + "xxhdpi", "drawable", getActivity().getPackageName()));

        kidcolor.setCompoundDrawablesWithIntrinsicBounds(null, null, null, drawable);
        used_colors.put("kid" + convertView.getId(), "" + digit);
        kidcolor.setTag("" + digit);

        mChildContainer.addView(convertView);

    }


    private void addNewChild() {
        boolean addChild = false;

        if (mChildContainer.getChildCount() > 0) {
            for (int position = 0; position < mChildContainer.getChildCount(); position++) {
                View innerLayout = (View) mChildContainer.getChildAt(position);

                EditText nameOfKidEdt = (EditText) innerLayout.findViewById(R.id.kids_name);
                TextView dobOfKidSpn = (TextView) innerLayout.findViewById(R.id.kids_bdy);
                final TextView kidcolor = (TextView) innerLayout.findViewById(R.id.kidcolor);

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

    public int getRandomNumber() {
        ArrayList<String> numbers = new ArrayList<>();
        numbers.clear();
        Iterator myVeryOwnIterator = used_colors.keySet().iterator();
        while (myVeryOwnIterator.hasNext()) {
            String key = (String) myVeryOwnIterator.next();
            String value = (String) used_colors.get(key);
            numbers.add(value);
        }
        int digit = 1;
        for (int i = 1; i <= 10; i++) {

            if (!numbers.contains("" + i)) {
                digit = i;
                break;
            }
        }
        return digit;
    }

    private void openGallery() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        getActivity().startActivityForResult(photoPickerIntent, Constants.OPEN_GALLERY);
    }

    public void startCamera() {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        try {
            Uri mImageCaptureUri = null;
            String state = Environment.getExternalStorageState();
            if (Environment.MEDIA_MOUNTED.equals(state)) {
                mImageCaptureUri = Uri.fromFile(mFileTemp);
            } else {
                mImageCaptureUri = Uri.fromFile(mFileTemp);

            }
            intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
            intent.putExtra("return-data", true);
            getActivity().startActivityForResult(intent, Constants.TAKE_PICTURE);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }

    }

    public void sendUploadProfileImageRequest(Bitmap originalImage) {
        showProgressDialog(getString(R.string.please_wait));
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        originalImage.compress(Bitmap.CompressFormat.PNG, 75, bao);
        byte[] ba = bao.toByteArray();
        String imageString = Base64.encodeToString(ba, Base64.DEFAULT);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("extension", "image/png");
            jsonObject.put("size", ba.length);
            jsonObject.put("byteCode", imageString);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        UserTable userTable = new UserTable((BaseApplication) getActivity().getApplication());
        UserModel userModel = userTable.getAllUserData();

        JSONArray jsonArray = new JSONArray();
        jsonArray.put(jsonObject);

        ImageUploadRequest requestData = new ImageUploadRequest();
        requestData.setImage(jsonArray.toString());
        requestData.setUser_id("" + userModel.getUser().getId());
        requestData.setSessionId("" + userModel.getUser().getSessionId());
        requestData.setProfileId("" + userModel.getUser().getProfileId());
        requestData.setType(AppConstants.IMAGE_TYPE_USER_PROFILE);

        ImageUploadController controller = new ImageUploadController(getActivity(), this);
        controller.getData(AppConstants.IMAGE_UPLOAD_REQUEST, requestData);
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

}








