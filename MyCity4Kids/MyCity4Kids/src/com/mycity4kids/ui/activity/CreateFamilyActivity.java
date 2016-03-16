package com.mycity4kids.ui.activity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.kelltontech.utils.ConnectivityUtils;
import com.kelltontech.utils.StringUtils;
import com.kelltontech.utils.ToastUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.ColorCode;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.controller.CreateFamilyController;
import com.mycity4kids.dbtable.TableAdult;
import com.mycity4kids.dbtable.TableFamily;
import com.mycity4kids.dbtable.TableKids;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.profile.KidsInformation;
import com.mycity4kids.models.user.KidsInfo;
import com.mycity4kids.models.user.UserInfo;
import com.mycity4kids.models.user.UserModel;
import com.mycity4kids.models.user.UserResponse;
import com.mycity4kids.newmodels.CreateFamilyModel;
import com.mycity4kids.newmodels.UserInviteModel;
import com.mycity4kids.preference.SharedPrefUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Created by hemant on 22/1/16.
 */
public class CreateFamilyActivity extends BaseActivity implements View.OnClickListener {

    private Toolbar mToolbar;

    private EditText mFamilyName, mKidsName, mSpouseName, mSpouseEmail;
    private LinearLayout mAdultContainer, mChildContainer;
    private TextView mAdditionalChild, mAdditionalAdult, mColorfrKid, mSpouseColor, colorView, mColorfrSpouse;
    private static TextView mKidsbdy;
    private ScrollView scrollView;
    private LinearLayout rootLayout;

    private static TextView BdayView;

    private int childCount = 0;
    private int adultCount = 0;

    private String color_selected = "";
    private String spouse_color = "";
    private String kid0_color = "";

    boolean isSpouseColor;
    boolean isKIDColor;
    static boolean isKIDBdy;
    private Dialog mColorPickerDialog;
//    private UserInviteModel userInviteModel;

    private HashMap<String, String> used_colors = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_family_activity);
        Utils.pushOpenScreenEvent(CreateFamilyActivity.this, "Create Family", SharedPrefUtils.getUserDetailModel(this).getId() + "");

        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Family Details");

//        if (null != getIntent().getExtras())
//            userInviteModel = getIntent().getExtras().getParcelable("userInviteData");

        rootLayout = (LinearLayout) findViewById(R.id.root);
        mAdultContainer = (LinearLayout) findViewById(R.id.internal_adult_layout);
        mChildContainer = (LinearLayout) findViewById(R.id.internal_kid_layout);
        mFamilyName = (EditText) findViewById(R.id.family_name);
        mAdditionalChild = (TextView) findViewById(R.id.additional_child);
        mAdditionalAdult = (TextView) findViewById(R.id.additional_adult);
        scrollView = (ScrollView) findViewById(R.id.mainScroll);


//        mSpouseEmail = (EditText) findViewById(R.id.spouse_email);
//        mSpouseName = (EditText) findViewById(R.id.spouse_name);
//        mColorfrSpouse = (TextView) findViewById(R.id.color_spouse);

        mKidsName = (EditText) findViewById(R.id.kids_name);
        mKidsbdy = (TextView) findViewById(R.id.kids_bdy);

        mColorfrKid = (TextView) findViewById(R.id.kidcolor);

        mAdditionalChild.setOnClickListener(this);
        mAdditionalAdult.setOnClickListener(this);
        mKidsbdy.setOnClickListener(this);
        mColorfrKid.setOnClickListener(this);
//        mColorfrSpouse.setOnClickListener(this);
        mColorfrKid.setTag("3");
        used_colors.put("kid0", "3");

        used_colors.put("spouse1", "1");
        addDynamicAdult();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // according to fragment change it
        getMenuInflater().inflate(R.menu.forgot_password, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.save:
                Log.d("onOptionsItemSelected", " HOME");
                if (checkCustomLayoutValidations() && checkDuplicateEmailIds()) {
                    // if (checkCustomLayoutValidations()) {

                    CreateFamilyModel createFamilyModel = getCreateFamilyRequestModel();

                    if (!ConnectivityUtils.isNetworkEnabled(CreateFamilyActivity.this)) {
                        ToastUtils.showToast(CreateFamilyActivity.this, getString(R.string.error_network));
                        return true;
                    }

                    showProgressDialog(getString(R.string.please_wait));
                    CreateFamilyController _controller = new CreateFamilyController(CreateFamilyActivity.this, CreateFamilyActivity.this);
                    _controller.getData(AppConstants.CREATE_FAMILY_REQUEST, createFamilyModel);
//                        if (!ConnectivityUtils.isNetworkEnabled(ActivitySignUp.this)) {
//                            ToastUtils.showToast(ActivitySignUp.this, getString(R.string.error_network));
//                            return true;
//                        }
//                        showProgressDialog(getString(R.string.please_wait));
                    // get cityid from pincode
//                        new ControllerCityByPincode(this, this).getData(AppConstants.CITY_BY_PINCODE_REQUEST, "" + mAddpincode.getText().toString());
//                        new LocationByPincode(ActivitySignUp.this, "" + mAddpincode.getText().toString(), ActivitySignUp.this);
                }
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean checkCustomLayoutValidations() {
        boolean result = true;

        for (int position = 0; position < mAdultContainer.getChildCount(); position++) {
            View innerLayout = (View) mAdultContainer.getChildAt(position);

            EditText adultname = (EditText) innerLayout.findViewById(R.id.spouse_name);
            EditText adultemail = (EditText) innerLayout.findViewById(R.id.spouse_email);


            if (!adultname.getText().toString().trim().equals("")) {
                if (adultemail.getText().toString().trim().equals("")) {

                    adultemail.setError(getResources().getString(R.string.please_enter_valid_email_or_mobile));
                    adultemail.setFocusableInTouchMode(true);
                    adultemail.requestFocus();
                    return false;
                } else {
                    if (!StringUtils.isValidEmail(adultemail.getText().toString())
                            && (!StringUtils.checkMobileNumber(adultemail.getText().toString()))) {
                        adultemail.setError(getResources().getString(R.string.please_enter_valid_email_or_mobile));
                        adultemail.setFocusableInTouchMode(true);
                        adultemail.requestFocus();
                        return false;
                    }

                }
            } else {
                adultname.setError(getResources().getString(R.string.please_enter_name));
                adultname.setFocusableInTouchMode(true);
                adultname.requestFocus();
                return false;
            }

        }

        // checking kids validation

        if (mKidsName.getText().toString().trim().equals("")) {

            mKidsName.setFocusableInTouchMode(true);
            mKidsName.requestFocus();
            mKidsName.setError(Constants.ENTER_KIDNAME);
            result = false;
        } else if (mKidsbdy.getText().toString().equals("")) {

            mKidsbdy.setFocusableInTouchMode(true);
            mKidsbdy.requestFocus();
            mKidsbdy.setError(Constants.ENTER_KIDBDY);
            result = false;
        }

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

    public boolean checkDuplicateEmailIds() {

        ArrayList<String> emailIdlist = new ArrayList<String>();

        for (int position = 0; position < mAdultContainer.getChildCount(); position++) {
            View innerLayout = (View) mAdultContainer.getChildAt(position);
            EditText adultemail = (EditText) innerLayout.findViewById(R.id.spouse_email);
            emailIdlist.add((adultemail.getText().toString().trim()));
        }


        int listSize = emailIdlist.size();
        HashSet<String> hashset = new HashSet<>();
        hashset.addAll(emailIdlist);

        int actualSize = hashset.size();

        if (listSize == actualSize)
            return true;
        else
            ToastUtils.showToast(this, "Please remove duplicate Email or Mobile");


        return false;
    }

    @Override
    protected void updateUi(Response response) {
        if (response == null) {
            removeProgressDialog();
            Toast.makeText(this, getResources().getString(R.string.server_error), Toast.LENGTH_SHORT).show();
            // showSnackbar(rootLayout, getResources().getString(R.string.server_error));
            return;
        }
        switch (response.getDataType()) {


            case AppConstants.CREATE_FAMILY_REQUEST:
                //                // save in
                UserResponse responseData = (UserResponse) response.getResponseObject();
                String message = responseData.getResult().getMessage();
                if (responseData.getResponseCode() == 200) {

                    //  showSnackbar(rootLayout, message);

                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

                    // save in db
                    saveDatainDB(responseData);

                    // store userdetail in prefrences

                    UserInfo model = new UserInfo();
                    model.setId(responseData.getResult().getData().getUser().getId());
                    model.setMobile_number(responseData.getResult().getData().getUser().getMobile_number());
                    model.setFamily_id(responseData.getResult().getData().getUser().getFamily_id());
                    model.setColor_code(responseData.getResult().getData().getUser().getColor_code());
                    model.setSessionId(responseData.getResult().getData().getUser().getSessionId());
                    model.setFirst_name(responseData.getResult().getData().getUser().getFirst_name() + " " + responseData.getResult().getData().getUser().getLast_name());

                    SharedPrefUtils.setUserDetailModel(CreateFamilyActivity.this, model);


                    // shift to my city for kids

                    removeProgressDialog();
                    Intent intent = new Intent(CreateFamilyActivity.this, LoadingActivity.class);
                    //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    //finish();


                    // configuration api call
//                    VersionApiModel versionApiModel = SharedPrefUtils.getSharedPrefVersion(this);
//                    ConfigurationController _controller = new ConfigurationController(this, this);
//
//                    try {
//                        PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
//                        String version = pInfo.versionName;
//
//                        versionApiModel.setCityId(SharedPrefUtils.getCurrentCityModel(this).getId());
//                        versionApiModel.setAppUpdateVersion(version);
//                        if (ConnectivityUtils.isNetworkEnabled(ActivitySignUp.this)) {
//
//                            showProgressDialog(getResources().getString(R.string.please_wait));
//                            _controller.getData(AppConstants.LOCATION_SEARCH_REQUEST, versionApiModel);
//
//                        }
//                    } catch (Exception e) {
//
//                    }


                } else if (responseData.getResponseCode() == 400) {

                    removeProgressDialog();
                    Toast.makeText(this, message, Toast.LENGTH_LONG).show();

                }
                break;
        }
    }

    public void saveDatainDB(UserResponse model) {

        TableAdult adultTable = new TableAdult((BaseApplication) getApplicationContext());
        adultTable.deleteAll();
        try {

            adultTable.beginTransaction();
            for (UserModel.AdultsInfo user : model.getResult().getData().getAdult()) {

                adultTable.insertData(user.getUser());
            }
            adultTable.setTransactionSuccessful();
        } finally {
            adultTable.endTransaction();
        }

        // saving child data
        TableKids kidsTable = new TableKids((BaseApplication) getApplicationContext());
        kidsTable.deleteAll();
        try {
            kidsTable.beginTransaction();
            for (KidsInfo kids : model.getResult().getData().getKidsInformation()) {

                kidsTable.insertData(kids);

            }
            kidsTable.setTransactionSuccessful();
        } finally {
            kidsTable.endTransaction();
        }

        // saving family

        TableFamily familyTable = new TableFamily((BaseApplication) getApplicationContext());
        familyTable.deleteAll();
        try {
            SharedPrefUtils.setpinCode(CreateFamilyActivity.this, model.getResult().getData().getUser().getPincode());
            familyTable.insertData(model.getResult().getData().getFamily());

        } catch (Exception e) {
            e.getMessage();
        }


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.additional_adult:
                addNewAdult();
                break;

            case R.id.additional_child:
                addNewChild();
                break;
            case R.id.kidcolor:
                isKIDColor = true;
                showColorPickerDialog("", null);
                break;

            case R.id.color_spouse:
                isSpouseColor = true;
                showColorPickerDialog("", null);
                break;

            case R.id.kids_bdy:
                isKIDBdy = true;
                showDatePickerDialog();
                break;

        }
    }

    private CreateFamilyModel getCreateFamilyRequestModel() {
        CreateFamilyModel createFamilyModel = new CreateFamilyModel();
        createFamilyModel.setUserId(SharedPrefUtils.getUserDetailModel(this).getId() + "");
        createFamilyModel.setFamilyName(mFamilyName.getText().toString());
//        createFamilyModel.setProfileImageUrl(userInviteModel.getProfileImgUrl());
//        createFamilyModel.setUserColorCode(userInviteModel.getColorCode());

//        if (signUpFlag)
//            SharedPrefUtils.setSignupFlag(this, 0);
//        else
//            SharedPrefUtils.setSignupFlag(this, 1);

        ArrayList<KidsInformation> kidsArray = getKidsInfo();

        ArrayList<CreateFamilyModel.InvitedUserModel> userArray = getAdultInfo();

        createFamilyModel.setInviteUserList(userArray);
        createFamilyModel.setKidsInformationArrayList(kidsArray);
        return createFamilyModel;
    }

    private ArrayList<CreateFamilyModel.InvitedUserModel> getAdultInfo() {
        ArrayList<CreateFamilyModel.InvitedUserModel> userInfoList = new ArrayList<>();

        for (int position = 0; position < mAdultContainer.getChildCount(); position++) {
            View innerLayout = (View) mAdultContainer.getChildAt(position);

            EditText adultname = (EditText) innerLayout.findViewById(R.id.spouse_name);
            EditText adultemail = (EditText) innerLayout.findViewById(R.id.spouse_email);
            final TextView adultColor = (TextView) innerLayout.findViewById(R.id.color_spouse);

            if ((adultname.getText().toString().trim().equals("")) || (adultemail.getText().toString().trim().equals(""))) {
            } else {
                CreateFamilyModel.InvitedUserModel usersInformation = new CreateFamilyModel().new InvitedUserModel();
                usersInformation.setName(adultname.getText().toString().trim());
                if (StringUtils.isValidEmail(adultemail.getText().toString().trim())) {
                    usersInformation.setEmail(adultemail.getText().toString().trim());
                } else {
                    usersInformation.setMobile(adultemail.getText().toString().trim());
                }
                usersInformation.setColor_code(new ColorCode().getValue("" + adultColor.getTag()));
                userInfoList.add(usersInformation);
            }
        }
        return userInfoList;
    }

    private ArrayList<KidsInformation> getKidsInfo() {
        ArrayList<KidsInformation> kidsInfoList = new ArrayList<KidsInformation>();

        KidsInformation kidInformation = new KidsInformation();
        kidInformation.setName((mKidsName.getText().toString().trim()));
        kidInformation.setBirthday((mKidsbdy.getText().toString().trim()));
        kidInformation.setColor_code(new ColorCode().getValue("" + mColorfrKid.getTag()));
        kidsInfoList.add(kidInformation);

        for (int position = 0; position < mChildContainer.getChildCount(); position++) {
            View innerLayout = (View) mChildContainer.getChildAt(position);

            EditText nameOfKidEdt = (EditText) innerLayout.findViewById(R.id.kids_name);
            TextView dobOfKidSpn = (TextView) innerLayout.findViewById(R.id.kids_bdy);
            final TextView kidcolor = (TextView) innerLayout.findViewById(R.id.kidcolor);

            //if(!StringUtils.isNullOrEmpty((nameOfKidEdt.getText().toString().trim()) && (dobOfKidSpn.getText().toString().trim())))

            if ((nameOfKidEdt.getText().toString().trim().equals("")) || (dobOfKidSpn.getText().toString().trim().equals(""))) {
            } else {
                KidsInformation kidsInformation = new KidsInformation();
                kidsInformation.setName((nameOfKidEdt).getText().toString().trim());
                kidsInformation.setBirthday((dobOfKidSpn).getText().toString().trim());
                kidsInformation.setColor_code(new ColorCode().getValue("" + kidcolor.getTag()));
                // kidsInformation.setId((String) innerLayout.getTag());
                kidsInfoList.add(kidsInformation);
            }
        }
        return kidsInfoList;
    }

    private void addNewAdult() {
        boolean addAdult = false;
//        if ((mSpouseName.getText().toString().trim().equals("")) || (mSpouseEmail.getText().toString().trim().equals(""))) {
//
//            ToastUtils.showToast(this, getResources().getString(R.string.enter_adult));
//        } else {
        if (mAdultContainer.getChildCount() > 0) {


            for (int position = 0; position < mAdultContainer.getChildCount(); position++) {
                View innerLayout = (View) mAdultContainer.getChildAt(position);

                EditText adultname = (EditText) innerLayout.findViewById(R.id.spouse_name);
                EditText adultemail = (EditText) innerLayout.findViewById(R.id.spouse_email);

                if ((adultname.getText().toString().trim().equals("")) || (adultemail.getText().toString().trim().equals(""))) {

                    addAdult = false;
                    ToastUtils.showToast(this, getResources().getString(R.string.enter_adult));
                    break;

                } else {
                    addAdult = true;
                }
            }


            if (addAdult)
                addDynamicAdult();

        } else {
            addDynamicAdult();
        }
//        }
    }

    private void addNewChild() {

        boolean addChild = false;
        if ((mKidsName.getText().toString().trim().equals("")) || (mKidsbdy.getText().toString().trim().equals(""))) {

            ToastUtils.showToast(this, getResources().getString(R.string.enter_kid));
        } else {


            if (mChildContainer.getChildCount() > 0) {

                for (int position = 0; position < mChildContainer.getChildCount(); position++) {
                    View innerLayout = (View) mChildContainer.getChildAt(position);

                    EditText nameOfKidEdt = (EditText) innerLayout.findViewById(R.id.kids_name);
                    TextView dobOfKidSpn = (TextView) innerLayout.findViewById(R.id.kids_bdy);
                    final TextView kidcolor = (TextView) innerLayout.findViewById(R.id.kidcolor);

                    if ((nameOfKidEdt.getText().toString().trim().equals("")) || (dobOfKidSpn.getText().toString().trim().equals(""))) {
                        addChild = false;
                        ToastUtils.showToast(this, getResources().getString(R.string.enter_kid));
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
    }

    private void addDynamicAdult() {
        ++adultCount;
        LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View convertView = (View) layoutInflater.inflate(R.layout.aa_addadult, null);
        convertView.setTag("adult" + adultCount);
        convertView.setId(adultCount);

        EditText adultname = (EditText) convertView.findViewById(R.id.spouse_name);
        EditText adultemail = (EditText) convertView.findViewById(R.id.spouse_email);
        final TextView adultColor = (TextView) convertView.findViewById(R.id.color_spouse);

        final TextView deleteView = (TextView) convertView.findViewById(R.id.cross);

        deleteView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAdultContainer.removeView(convertView);
                used_colors.remove("adult" + convertView.getId());
            }
        });

        adultColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                colorView = adultColor;
                showColorPickerDialog("adult" + convertView.getId(), adultColor);
            }
        });

        int digit = getRandomNumber();
        Drawable drawable = getResources().getDrawable(getResources()
                .getIdentifier("color_" + digit + "xxhdpi", "drawable", getPackageName()));
        adultColor.setCompoundDrawablesWithIntrinsicBounds(null, null, null, drawable);
        used_colors.put("adult" + convertView.getId(), "" + digit);

        adultColor.setTag("" + digit);


//        if (kid0_color.equals("")) {
//
//            digit = getRandomNumber();
//            drawable = getResources().getDrawable(getResources()
//                    .getIdentifier("color_" + digit + "xxhdpi", "drawable", getPackageName()));
//            mColorfrKid.setCompoundDrawablesWithIntrinsicBounds(null, null, null, drawable);
//            mColorfrKid.setTag("" + digit);
//        }

        mAdultContainer.addView(convertView);
        sendScrollDown();
    }

    private void addDynamicChild() {
        ++childCount;
        LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View convertView = (View) layoutInflater.inflate(R.layout.aa_addchild, null);
        convertView.setTag("kid" + childCount);
        convertView.setId(childCount);

        final TextView dobOfKidSpn = (TextView) convertView.findViewById(R.id.kids_bdy);
        final TextView kidcolor = (TextView) convertView.findViewById(R.id.kidcolor);

        final TextView deleteView = (TextView) convertView.findViewById(R.id.cross);

        deleteView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mChildContainer.removeView(convertView);
            }
        });

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
                colorView = kidcolor;
                showColorPickerDialog("kid" + convertView.getId(), kidcolor);
            }
        });

        int digit;
        Drawable drawable;
//        if (kid0_color.equals("")) {
//
//            digit = getRandomNumber();
//            drawable = getResources().getDrawable(getResources()
//                    .getIdentifier("color_" + digit + "xxhdpi", "drawable", getPackageName()));
//            mColorfrKid.setCompoundDrawablesWithIntrinsicBounds(null, null, null, drawable);
//            mColorfrKid.setTag("" + digit);
//            used_colors.put("kid0" + convertView.getId(), "" + digit);
//            kid0_color = "" + digit;
//        }


        digit = getRandomNumber();
        drawable = getResources().getDrawable(getResources()
                .getIdentifier("color_" + digit + "xxhdpi", "drawable", getPackageName()));

        kidcolor.setCompoundDrawablesWithIntrinsicBounds(null, null, null, drawable);
        used_colors.put("kid" + convertView.getId(), "" + digit);
        kidcolor.setTag("" + digit);

        mChildContainer.addView(convertView);
        sendScrollDown();

    }

    private void sendScrollDown() {
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
                        if (null != scrollView)
                            scrollView.fullScroll(View.FOCUS_DOWN);
                    }
                });
            }
        }).start();
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

    public void showColorPickerDialog(final String name, final TextView textview) {

        // custom dialog
        mColorPickerDialog = new Dialog(this);
        mColorPickerDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = mColorPickerDialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);


        LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view = (View) layoutInflater.inflate(R.layout.aa_colorpicker, null);

        mColorPickerDialog.setContentView(view);
        mColorPickerDialog.setCancelable(true);

        mColorPickerDialog.findViewById(R.id.color1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //color_selected = "1";
                // setColor(name, textview);
                showSelectedcolorMessage(v, name, textview, "1");

            }
        });

        mColorPickerDialog.findViewById(R.id.color2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //color_selected = "2";
                // setColor(name, textview);

                showSelectedcolorMessage(v, name, textview, "2");

            }
        });

        mColorPickerDialog.findViewById(R.id.color3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //color_selected = "3";
                //setColor(name, textview);
                showSelectedcolorMessage(v, name, textview, "3");

            }
        });
        mColorPickerDialog.findViewById(R.id.color4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // color_selected = "4";
                //setColor(name, textview);
                showSelectedcolorMessage(v, name, textview, "4");
            }
        });
        mColorPickerDialog.findViewById(R.id.color5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // color_selected = "5";
                // setColor(name, textview);
                showSelectedcolorMessage(v, name, textview, "5");
            }
        });
        mColorPickerDialog.findViewById(R.id.color6).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // color_selected = "6";
                // setColor(name, textview);
                showSelectedcolorMessage(v, name, textview, "6");

            }
        });
        mColorPickerDialog.findViewById(R.id.color7).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //color_selected = "7";
                //setColor(name, textview);
                showSelectedcolorMessage(v, name, textview, "7");
            }
        });
        mColorPickerDialog.findViewById(R.id.color8).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // color_selected = "8";
                // setColor(name, textview);
                showSelectedcolorMessage(v, name, textview, "8");
            }
        });
        mColorPickerDialog.findViewById(R.id.color9).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // color_selected = "9";
                //setColor(name, textview);
                showSelectedcolorMessage(v, name, textview, "9");

            }
        });
        mColorPickerDialog.findViewById(R.id.color10).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

              /*  color_selected = "10";
                setColor(name, textview);*/
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
            //view.findViewWithTag(value).setEnabled(false);
            view.findViewWithTag(value).setAlpha(0.2f);
        }

        mColorPickerDialog.show();


    }

    public void setColor(String id, TextView v) {
        if (isSpouseColor) {

            used_colors.put("spouse1", color_selected);
            spouse_color = color_selected;
            isSpouseColor = false;

            mColorfrSpouse.setTag(color_selected);

            Drawable drawable = getResources().getDrawable(getResources()
                    .getIdentifier("color_" + color_selected + "xxhdpi", "drawable", getPackageName()));
            mColorfrSpouse.setCompoundDrawablesWithIntrinsicBounds(null, null, null, drawable);

//            if (kid0_color.equals("")) {
//                int digit = getRandomNumber();
//                drawable = getResources().getDrawable(getResources()
//                        .getIdentifier("color_" + digit + "xxhdpi", "drawable", getPackageName()));
//                mColorfrKid.setCompoundDrawablesWithIntrinsicBounds(null, null, null, drawable);
//                mColorfrKid.setTag("" + digit);
//            }


        } else if (isKIDColor) {

            used_colors.put("kid0", color_selected);
            kid0_color = color_selected;
            isKIDColor = false;
            mColorfrKid.setTag(color_selected);


            Drawable drawable = getResources().getDrawable(getResources()
                    .getIdentifier("color_" + color_selected + "xxhdpi", "drawable", getPackageName()));
            mColorfrKid.setCompoundDrawablesWithIntrinsicBounds(null, null, null, drawable);
            // remove this color from kid

        } else {

//            if (kid0_color.equals("")) {
//
//                int digit = getRandomNumber();
//                Drawable drawable = getResources().getDrawable(getResources()
//                        .getIdentifier("color_" + digit + "xxhdpi", "drawable", getPackageName()));
//                mColorfrKid.setCompoundDrawablesWithIntrinsicBounds(null, null, null, drawable);
//                mColorfrKid.setTag("" + digit);
//            }

            used_colors.put(id, color_selected);
            // set on the custom view
            if (v != null) {
                //ToastUtils.showToast(this,"color set");
                Drawable drawable = getResources().getDrawable(getResources()
                        .getIdentifier("color_" + color_selected + "xxhdpi", "drawable", getPackageName()));
                v.setCompoundDrawablesWithIntrinsicBounds(null, null, null, drawable);
                v.setTag("" + color_selected);

            }

        }
        mColorPickerDialog.dismiss();

    }

    public void showSelectedcolorMessage(View v, final String name, final TextView textview, String colorsSelected) {
        if (v.getAlpha() == 0.2f) {
            showSnackbar(rootLayout, getResources().getString(R.string.color_selected));
        } else {
            color_selected = colorsSelected;
            setColor(name, textview);
        }
    }

    public void showDatePickerDialog() {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
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


            //c.set(curent_year,current_month,current_day);

            long maxdate = Long.parseLong(convertDate(current_day + "-" + (current_month + 1) + "-" + curent_year)) * 1000;
            DatePickerDialog dlg = new DatePickerDialog(getActivity(), android.R.style.Theme_Holo_Light_Dialog_NoActionBar, this, curent_year, current_month, current_day);
            dlg.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dlg.getDatePicker().setMaxDate(c.getTimeInMillis());
            return dlg;

        }

        public void onDateSet(DatePicker view, int year, int month, int day) {

            String sel_date = "" + day + "-" + (month + 1) + "-" + year;
            if (isKIDBdy) {
                isKIDBdy = false;
                mKidsbdy.setError(null);

                if (chkTime(sel_date)) {
                    mKidsbdy.setText("" + day + "-" + (month + 1) + "-" + year);
                } else {
                    mKidsbdy.setText("" + current_day + "-" + (current_month + 1) + "-" + curent_year);
                }

            } else {
                if (BdayView != null)

                {
                    if (chkTime(sel_date)) {
                        BdayView.setText("" + day + "-" + (month + 1) + "-" + year);
                    } else {
                        BdayView.setText("" + current_day + "-" + (current_month + 1) + "-" + curent_year);
                    }

                }
            }


        }

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

}
