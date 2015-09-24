package com.mycity4kids.ui.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.kelltontech.utils.BitmapUtils;
import com.kelltontech.utils.ConnectivityUtils;
import com.kelltontech.utils.StringUtils;
import com.kelltontech.utils.ToastUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.asynctask.HeavyDbTask;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.ColorCode;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.controller.ControllerSignUp;
import com.mycity4kids.controller.ImageUploadController;
import com.mycity4kids.cropimage.CropImage;
import com.mycity4kids.dbtable.TableAdult;
import com.mycity4kids.dbtable.TableFamily;
import com.mycity4kids.dbtable.TableKids;
import com.mycity4kids.dbtable.UserTable;
import com.mycity4kids.interfaces.OnUIView;
import com.mycity4kids.listener.OnButtonClicked;
import com.mycity4kids.models.configuration.ConfigurationApiModel;
import com.mycity4kids.models.forgot.CommonResponse;
import com.mycity4kids.models.profile.KidsInformation;
import com.mycity4kids.models.profile.SignUpModel;
import com.mycity4kids.models.user.ImageUploadRequest;
import com.mycity4kids.models.user.KidsInfo;
import com.mycity4kids.models.user.UserInfo;
import com.mycity4kids.models.user.UserModel;
import com.mycity4kids.models.user.UserResponse;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.ui.dialog.PhotoOptionsDialog;
import com.mycity4kids.ui.fragment.NotificationFragment;
import com.mycity4kids.ui.fragment.SyncSettingFragment;
import com.mycity4kids.utils.RoundedTransformation;
import com.mycity4kids.utils.location.LocationByPincode;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
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
 * Created by khushboo.goyal on 08-06-2015.
 */
public class ActivitySignUp extends BaseActivity implements View.OnClickListener, LocationByPincode.ICallBackForCity {
    private EditText mFamilyname;
    private EditText mSpousename;
    private EditText mSpouseemail;
    private TextView mColorfrSpouse;
    private EditText mFamilysharepswd;
    private EditText mConfirmPswd;
    private EditText mAddpincode;
    private EditText mKidsName;
    static private TextView mKidsbdy;
    private TextView mColorfrKid;
    private Dialog mColorPickerDialog;
    private int childCount = 0;
    private int adultCount = 0;
    private TextView mAdditionalChild;
    private TextView mAdditionalAdult;
    private LinearLayout mAdultContainer;
    private LinearLayout mChildContainer;
    //private Arraylist<Colors>
    private HashMap<String, String> used_colors = new HashMap<>();
    private String color_selected = "";
    private String spouse_color = "";
    private String kid0_color = "";
    boolean isSpouseColor;
    boolean isKIDColor;
    static boolean isKIDBdy;
    private TextView colorView, notificationBtn;
    private static TextView BdayView;
    private String g_id = "";
    private String facebook_id = "";
    private String Fb_access_token = "";
    private String google_secret = "";
    private SignUpModel signupModel;
    private ImageView profile_image;
    private Bitmap originalImage = null;
    private Toolbar mToolbar;
    private int cityid;
    private String profileImageUrl;
    private File photo;
    private File mFileTemp;
    private LinearLayout rootLayout;
    ScrollView scrollView;
    SignUpModel.User userInformation;
    Boolean signUpFlag = false;
    // color codess

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aa_signup);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Family Details");

        rootLayout = (LinearLayout) findViewById(R.id.root);
        scrollView = (ScrollView) findViewById(R.id.mainScroll);

        mFamilyname = (EditText) findViewById(R.id.family_name);
        profile_image = (ImageView) findViewById(R.id.profile_image);
        mSpousename = (EditText) findViewById(R.id.spouse_name);
        mSpouseemail = (EditText) findViewById(R.id.spouse_email);
        mFamilysharepswd = (EditText) findViewById(R.id.family_password);
        mConfirmPswd = (EditText) findViewById(R.id.confirm_password);
        mAddpincode = (EditText) findViewById(R.id.add_pincode);
        mColorfrSpouse = (TextView) findViewById(R.id.color_spouse);
        mAdultContainer = (LinearLayout) findViewById(R.id.internal_adult_layout);
        mKidsName = (EditText) findViewById(R.id.kids_name);
        mKidsbdy = (TextView) findViewById(R.id.kids_bdy);
        mChildContainer = (LinearLayout) findViewById(R.id.internal_kid_layout);
        mColorfrKid = (TextView) findViewById(R.id.kidcolor);
        mAdditionalChild = (TextView) findViewById(R.id.additional_child);
        mAdditionalAdult = (TextView) findViewById(R.id.additional_adult);
        notificationBtn = (TextView) findViewById(R.id.notification);
        ((TextView) findViewById(R.id.device_setting)).setOnClickListener(this);
        notificationBtn.setOnClickListener(this);
        //findViewById(R.id.save).setOnClickListener(this);
        findViewById(R.id.profile_image).setOnClickListener(this);
        mKidsbdy.setOnClickListener(this);

        mColorfrKid.setOnClickListener(this);
        mColorfrSpouse.setOnClickListener(this);
        mAdditionalChild.setOnClickListener(this);
        mAdditionalAdult.setOnClickListener(this);
        mKidsbdy.setOnClickListener(this);
        profile_image.setOnClickListener(this);

        // mFamilyname.setKeyListener(DigitsKeyListener.getInstance(" abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"));

        // getting credentials from Fb

        SharedPrefUtils.setNotificationPrefrence(this, "3", true);
        SharedPrefUtils.setNotificationPrefrence(this, "3", false);

        userInformation = new SignUpModel().new User();

        Bundle bundle = getIntent().getBundleExtra("userbundle");
        if (bundle != null) {
            String access_token = bundle.getString(Constants.ACCESS_TOKEN);
            String mode = bundle.getString(Constants.MODE);
            String user_id = bundle.getString(Constants.USER_ID);
            String username = bundle.getString(Constants.USER_NAME);
            String user_email = bundle.getString(Constants.USER_EMAIL);
            profileImageUrl = bundle.getString(Constants.PROFILE_IMAGE);

            mSpousename.setText(username);
            mSpouseemail.setText(user_email);

            if (mode.equalsIgnoreCase("google")) {
                g_id = user_id;
                google_secret = access_token;
            } else {
                facebook_id = user_id;
                Fb_access_token = access_token;
            }

            if (!StringUtils.isNullOrEmpty(profileImageUrl))
                Picasso.with(this).load(profileImageUrl).placeholder(R.drawable.family_xxhdpi).error(R.drawable.family_xxhdpi).transform(new RoundedTransformation()).into(profile_image);


        } else {
            // getting server response existing user in bundle
            Bundle bundleParams = getIntent().getExtras();
            if (bundleParams != null) {

                String data = bundleParams.getString(Constants.SIGNUP_DATA);
                signUpFlag = bundleParams.getBoolean(Constants.SIGNUP_FLAG);

                if (!StringUtils.isNullOrEmpty(data)) {
                    UserResponse response = new Gson().fromJson(data, UserResponse.class);

                    if (response != null) {


                        if (response.getResult().getData().getUser() != null) {
                            mSpousename.setText(response.getResult().getData().getUser().getName());
                            mSpouseemail.setText(response.getResult().getData().getUser().getEmail());

                            // entering kids detail
                            ArrayList<KidsInfo> kidList = response.getResult().getData().getKidsInformation();

                            for (int i = 0; i < kidList.size(); i++) {
                                if (i == 0) {
                                    mKidsName.setText(kidList.get(i).getName());
                                    mKidsbdy.setText(kidList.get(i).getDate_of_birth());
                                    // set color
                                }

                            }

                            int childKey = 3;

                            int length = kidList.size();
                            if (length > 1)
                                length = length - 1;

                            int count = 1;
                            while (length > 0) {
                                // method calling of additional adult
                                if (kidList.size() > count) {
                                    addNewKidFromDB(kidList.get(count).getName(), kidList.get(count).getDate_of_birth(), "" + childKey);
                                    childKey++;
                                    count++;
                                }

                                length--;

                            }

                        }
                    }
                }
            }

        }


        // checking usertable

        UserTable userTable = new UserTable(BaseApplication.getInstance());
        if (userTable.getRowsCount() > 0) {
            String lastName = userTable.getAllUserData().getUser().getLast_name();
            mSpousename.setText(userTable.getAllUserData().getUser().getFirst_name() + " " + lastName != null ? lastName : "");
            mSpouseemail.setText(userTable.getAllUserData().getUser().getEmail());
        }

        mColorfrSpouse.setTag("1");
        used_colors.put("spouse1", "1");

        mColorfrKid.setTag("2");
        used_colors.put("kid0", "2");

        // checkDBExists();
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

        Fragment currentFrag = getSupportFragmentManager().findFragmentById(R.id.setting_frame);

        switch (item.getItemId()) {
            case android.R.id.home:

                if (((FrameLayout) findViewById(R.id.setting_frame)).getVisibility() == View.GONE) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                    dialog.setMessage(getResources().getString(R.string.exit_signup)).setNegativeButton(R.string.new_yes
                            , new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.cancel();
                            finish();
                        }
                    }).setPositiveButton(R.string.new_cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                            dialog.cancel();


                        }
                    }).setIcon(android.R.drawable.ic_dialog_alert);

                    AlertDialog alert11 = dialog.create();
                    alert11.show();

                    alert11.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.home_light_blue));
                    alert11.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.canceltxt_color));
                }

                if (currentFrag instanceof NotificationFragment) {
                    ((NotificationFragment) currentFrag).saveNotificationSetting();
                    ((NotificationFragment) currentFrag).getFragmentManager().popBackStack();
                    ((FrameLayout) findViewById(R.id.setting_frame)).setVisibility(View.GONE);
                    scrollView.setVisibility(View.VISIBLE);
                    getSupportActionBar().setTitle("Notifications");

                }
                if (currentFrag instanceof SyncSettingFragment) {
//                    ((SyncSettingFragment) currentFrag).saveNotificationSetting();
                    ((SyncSettingFragment) currentFrag).getFragmentManager().popBackStack();
                    ((FrameLayout) findViewById(R.id.setting_frame)).setVisibility(View.GONE);
                    scrollView.setVisibility(View.VISIBLE);
                    getSupportActionBar().setTitle("Sync Settings");
                }

                return true;

            case R.id.save:

                if (((FrameLayout) findViewById(R.id.setting_frame)).getVisibility() == View.GONE) {
                    if (checkValidation()) {
                        // if (checkCustomLayoutValidations()) {

                        signupModel = getSignUpRequestModel();

                        if (!ConnectivityUtils.isNetworkEnabled(ActivitySignUp.this)) {
                            ToastUtils.showToast(ActivitySignUp.this, getString(R.string.error_network));
                            return true;
                        }

                        showProgressDialog(getString(R.string.please_wait));
                        ControllerSignUp _controller = new ControllerSignUp(ActivitySignUp.this, ActivitySignUp.this);
                        _controller.getData(AppConstants.SIGNUP_REQUEST, signupModel);
//                        if (!ConnectivityUtils.isNetworkEnabled(ActivitySignUp.this)) {
//                            ToastUtils.showToast(ActivitySignUp.this, getString(R.string.error_network));
//                            return true;
//                        }
//                        showProgressDialog(getString(R.string.please_wait));
                        // get cityid from pincode
//                        new ControllerCityByPincode(this, this).getData(AppConstants.CITY_BY_PINCODE_REQUEST, "" + mAddpincode.getText().toString());
//                        new LocationByPincode(ActivitySignUp.this, "" + mAddpincode.getText().toString(), ActivitySignUp.this);
                    }
                }

                if (currentFrag instanceof NotificationFragment) {

                    ((NotificationFragment) currentFrag).saveNotificationSetting();
                    ((FrameLayout) findViewById(R.id.setting_frame)).setVisibility(View.GONE);
                    scrollView.setVisibility(View.VISIBLE);
                    userInformation.setNotification_app(SharedPrefUtils.getNotificationPrefrence(this, true));
                    userInformation.setNotification_task(SharedPrefUtils.getNotificationPrefrence(this, false));

                }

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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


            case AppConstants.SIGNUP_REQUEST:

                // save in
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
                    model.setFamily_id(responseData.getResult().getData().getUser().getFamily_id());
                    model.setColor_code(responseData.getResult().getData().getUser().getColor_code());
                    model.setSessionId(responseData.getResult().getData().getUser().getSessionId());
                    model.setFirst_name(responseData.getResult().getData().getUser().getFirst_name() + " " + responseData.getResult().getData().getUser().getLast_name());

                    SharedPrefUtils.setUserDetailModel(ActivitySignUp.this, model);


                    // shift to my city for kids

                    removeProgressDialog();
                    Intent intent = new Intent(ActivitySignUp.this, LoadingActivity.class);
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
                    if (responseData.getResult().getData().getExist().equalsIgnoreCase("exist")) {
                        showLoginDialog(message);
                    } else {
                        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
                        // showSnackbar(rootLayout, message);
                    }


                }
                break;
            case AppConstants.IMAGE_UPLOAD_REQUEST:
                removeProgressDialog();
                if (response.getResponseObject() instanceof CommonResponse) {
                    CommonResponse responseModel = (CommonResponse) response
                            .getResponseObject();
                    if (responseModel.getResponseCode() != 200) {
                        showSnackbar(rootLayout, getResources().getString(R.string.server_error));
                        return;
                    } else {
                        if (!StringUtils.isNullOrEmpty(responseModel.getResult().getMessage())) {
                            // SharedPrefUtils.setProfileImgUrl(ActivitySignUp.this, responseModel.getResult().getMessage());
                            Log.i("Uploaded Image URL", responseModel.getResult().getMessage());

                            profileImageUrl = responseModel.getResult().getMessage();
                        }
                        setProfileImage(responseModel.getResult().getMessage());
                        showSnackbar(rootLayout, getResources().getString(R.string.upload_iamge_successfully));


                    }
                }
                break;
            case AppConstants.LOCATION_SEARCH_REQUEST:
                removeProgressDialog();
                Object responseObject = response.getResponseObject();
                if (responseObject instanceof ConfigurationApiModel) {
                    ConfigurationApiModel _configurationResponse = (ConfigurationApiModel) responseObject;

                    /**
                     * Save data into tables :-
                     */
                    HeavyDbTask _heavyDbTask = new HeavyDbTask(this, _configurationResponse, new OnUIView() {

                        @Override
                        public void comeBackOnUI() {
                            removeProgressDialog();

                            Intent intent = new Intent(ActivitySignUp.this, DashboardActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            startActivity(intent);
                            finish();


                        }
                    });
                    _heavyDbTask.execute();


                }
                break;
//            case AppConstants.CITY_BY_PINCODE_REQUEST:
//                CityByPinCodeModel cityByPinCodeModel = (CityByPinCodeModel) response.getResponseObject();
//                if (cityByPinCodeModel.getResponseCode() == 200) {
//                    MetroCity model = new MetroCity();
//                    model.setId(cityByPinCodeModel.getResult().getData().getCity_id());
//                    model.setName("");
//
//                    int lastCityIdUsed = SharedPrefUtils.getCurrentCityModel(this).getId();
//                    if (lastCityIdUsed != model.getId()) {
//                        SharedPrefUtils.setConfigurationDataRequired(this, true);
//                    } else {
//                        SharedPrefUtils.setConfigurationDataRequired(this, false);
//                    }
//                    SharedPrefUtils.setCurrentCityModel(this, model);
//                } else {
//                    SharedPrefUtils.setConfigurationDataRequired(this, false);
//                }
//                break;
            default:
                break;
        }


    }


    public void showLoginDialog(String message) {

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setMessage(message).setNegativeButton(R.string.new_yes
                , new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                dialog.cancel();

                startActivity(new Intent(ActivitySignUp.this, ActivityLogin.class));
                finish();


            }
        }).setPositiveButton(R.string.new_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // do nothing
                dialog.cancel();


            }
        }).setIcon(android.R.drawable.ic_dialog_alert);

        AlertDialog alert11 = dialog.create();
        alert11.show();

        alert11.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.home_light_blue));
        alert11.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.canceltxt_color));

    }


    private void addNewKidFromDB(String name, String bday, String key) {
        ++childCount;
        LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View convertView = (View) layoutInflater.inflate(R.layout.aa_addchild, null);
        convertView.setTag("kid" + childCount);
        convertView.setId(childCount);

        EditText nameOfKidEdt = (EditText) convertView.findViewById(R.id.kids_name);
        final TextView dobOfKidSpn = (TextView) convertView.findViewById(R.id.kids_bdy);
        final TextView kidcolor = (TextView) convertView.findViewById(R.id.kidcolor);


        nameOfKidEdt.setText(name);
        dobOfKidSpn.setText(bday);

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


        Drawable drawable = getResources().getDrawable(getResources()
                .getIdentifier("color_" + key + "xxhdpi", "drawable", getPackageName()));
        kidcolor.setCompoundDrawablesWithIntrinsicBounds(null, null, null, drawable);
        used_colors.put("kid" + convertView.getId(), "" + key);
        kidcolor.setTag("" + key);

        mChildContainer.addView(convertView);


    }

    private void addNewAdultFromDB(String name, String email, String colorcode) {
        ++adultCount;
        LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View convertView = (View) layoutInflater.inflate(R.layout.aa_addadult, null);
        convertView.setTag("adult" + adultCount);
        convertView.setId(adultCount);

        EditText adultname = (EditText) convertView.findViewById(R.id.spouse_name);
        EditText adultemail = (EditText) convertView.findViewById(R.id.spouse_email);
        final TextView adultColor = (TextView) convertView.findViewById(R.id.color_spouse);

        adultColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                colorView = adultColor;
                showColorPickerDialog("adult" + convertView.getId(), adultColor);
            }
        });

        adultname.setText(name);
        adultemail.setText(email);


        // set color

        String key = new ColorCode().getKey(colorcode);
        Drawable drawable = getResources().getDrawable(getResources()
                .getIdentifier("color_" + key + "xxhdpi", "drawable", getPackageName()));
        adultColor.setCompoundDrawablesWithIntrinsicBounds(null, null, null, drawable);
        used_colors.put("adult" + convertView.getId(), "" + key);
        adultColor.setTag("" + key);


        mAdultContainer.addView(convertView);
    }

//    public void checkDBExists() {
//        TableAdult adultTable = new TableAdult((BaseApplication) getApplicationContext());
//        ArrayList<UserInfo> adultList = adultTable.getAllAdults();
//
//        if (!adultList.isEmpty()) {
//
//            UserTable usertable = new UserTable((BaseApplication) getApplicationContext());
//            UserModel usermodel = usertable.getAllUserData();
//
//            String userEmail = "";
//            if (usermodel != null) {
//                userEmail = usermodel.getUser().getEmail();
//            }
//
//            int pos = 0;
//
//            for (int i = 0; i < adultList.size(); i++) {
//
//                if (adultList.get(i).getEmail().equals(userEmail)) {
//                    pos = i;
//                    break;
//                }
//            }
//
//
//            for (int i = 0; i < adultList.size(); i++) {
//                if (i == pos) {
//                    mSpousename.setText(adultList.get(i).getFirst_name());
//                    mSpouseemail.setText(adultList.get(i).getEmail());
//
//                    // set color
//
//                    String key = new ColorCode().getKey(adultList.get(i).getColor_code());
//                    Drawable drawable = getResources().getDrawable(getResources()
//                            .getIdentifier("color_" + key + "xxhdpi", "drawable", getPackageName()));
//                    mColorfrSpouse.setCompoundDrawablesWithIntrinsicBounds(null, null, null, drawable);
//                    spouse_color = key;
//                    mColorfrSpouse.setTag(key);
//                    used_colors.put("spouse1", key);
//
//                }
//
//            }
//
//            adultList.remove(pos);
//
//            for (int i = 0; i < adultList.size(); i++) {
//                addNewAdultFromDB(adultList.get(i).getFirst_name(), adultList.get(i).getEmail(), adultList.get(i).getColor_code());
//            }
//
////        int length = adultList.size();
////        if (length > 1)
////            length = length - 1;
////
////        int count = 1;
////        while (length > 0) {
////            // method calling of additional adult
////            if (adultList.size() > count) {
////                addNewAdultFromDB(adultList.get(count).getFirst_name(), adultList.get(count).getEmail(), adultList.get(count).getColor_code());
////                count++;
////            }
////
////            length--;
////
////        }
//
//
//            TableKids kidsTable = new TableKids((BaseApplication) getApplicationContext());
//            ArrayList<KidsInfo> kidList = kidsTable.getAllKids();
//
//            for (int i = 0; i < kidList.size(); i++) {
//                if (i == 0) {
//                    mKidsName.setText(kidList.get(i).getName());
//                    mKidsbdy.setText(kidList.get(i).getDate_of_birth());
//
//                    // set color
//
//                    String key = new ColorCode().getKey(kidList.get(i).getColor_code());
//                    Drawable drawable = getResources().getDrawable(getResources()
//                            .getIdentifier("color_" + key + "xxhdpi", "drawable", getPackageName()));
//                    mColorfrKid.setCompoundDrawablesWithIntrinsicBounds(null, null, null, drawable);
//                    kid0_color = key;
//                    mColorfrKid.setTag(key);
//                    used_colors.put("kid0", key);
//                }
//
//            }
//
//            int length = kidList.size();
//            if (length > 1)
//                length = length - 1;
//
//            int count = 1;
//            while (length > 0) {
//                // method calling of additional adult
//                if (kidList.size() > count) {
//                    addNewKidFromDB(kidList.get(count).getName(), kidList.get(count).getDate_of_birth(), kidList.get(count).getColor_code());
//                    count++;
//                }
//
//                length--;
//
//            }
//
//            TableFamily familyTable = new TableFamily((BaseApplication) getApplicationContext());
//            UserModel.FamilyInfo family = familyTable.getFamily();
//
//            if (family != null) {
//
//                mFamilyname.setText(family.getFamily_name());
//                mFamilysharepswd.setText(family.getFamily_password());
//                mConfirmPswd.setText(family.getFamily_password());
//                mAddpincode.setText(family.getPincode());
//
//            }
//
//
//        }
//
//
//    }
//

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
            SharedPrefUtils.setpinCode(ActivitySignUp.this, model.getResult().getData().getUser().getPincode());
            familyTable.insertData(model.getResult().getData().getFamily());

        } catch (Exception e) {
            e.getMessage();
        }


    }


    public void showSelectedcolorMessage(View v, final String name, final TextView textview, String colorsSelected) {
        if (v.getAlpha() == 0.2f) {
            showSnackbar(rootLayout, getResources().getString(R.string.color_selected));
        } else {
            color_selected = colorsSelected;
            setColor(name, textview);
        }
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
//        int digit = (int) Math.floor(Math.random() * (max - min + 1)) + min;
//
//        if (numbers.size() <= 8) {
//            while (numbers.contains("" + digit)) {
//                digit = (int) Math.floor(Math.random() * (max - min + 1)) + min;
//
//            }
//        }


        return digit;
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


//        final TextView txtKidname = (TextView) convertView.findViewById(R.id.txtkidname);
//        final TextView txtKidbdy = (TextView) convertView.findViewById(R.id.txtkidbdy);
//
//        txtKidbdy.setText("KID " + (childCount + 1) + "'S BIRTHDAY");
//        txtKidname.setText("KID " + (childCount + 1) + "'S NAME");


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

    private void addNewAdult() {
        boolean addAdult = false;
        if ((mSpousename.getText().toString().trim().equals("")) || (mSpouseemail.getText().toString().trim().equals(""))) {

            ToastUtils.showToast(this, getResources().getString(R.string.enter_adult));
        } else {

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

        }
    }

//    private String getStringResourceByName(String aString) {
//        aString = "color" + aString;
//        String packageName = getPackageName();
//        int resId = getResources()
//                .getIdentifier(aString, "string", packageName);
//        if (resId == 0) {
//            return aString;
//        } else {
//            return getString(resId);
//        }
//
//
//    }
//
//    private String getColorCodes(String aString) {
//        new ColorCode().getKey("");
//
//
//        return "";
//
//
//    }


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


    private ArrayList<SignUpModel.User> getAdultInfo() {
        ArrayList<SignUpModel.User> userInfoList = new ArrayList<>();


        userInformation.setUsername((mSpousename.getText().toString().trim()));
        userInformation.setEmail((mSpouseemail.getText().toString().trim()));
        userInformation.setColor_code(new ColorCode().getValue("" + mColorfrSpouse.getTag()));
        userInformation.setFacebook_id(facebook_id);
        userInformation.setOuth_token(Fb_access_token);
        userInformation.setToken_secret(google_secret);
        userInformation.setProfile_image(profileImageUrl);
        userInformation.setG_id(g_id);
        userInformation.setNotification_app(SharedPrefUtils.getNotificationPrefrence(this, true));
        userInformation.setNotification_task(SharedPrefUtils.getNotificationPrefrence(this, false));
        userInfoList.add(userInformation);

        for (int position = 0; position < mAdultContainer.getChildCount(); position++) {
            View innerLayout = (View) mAdultContainer.getChildAt(position);

            EditText adultname = (EditText) innerLayout.findViewById(R.id.spouse_name);
            EditText adultemail = (EditText) innerLayout.findViewById(R.id.spouse_email);
            final TextView adultColor = (TextView) innerLayout.findViewById(R.id.color_spouse);

            if ((adultname.getText().toString().trim().equals("")) || (adultemail.getText().toString().trim().equals(""))) {
            } else {
                SignUpModel.User usersInformation = new SignUpModel().new User();
                usersInformation.setUsername((adultname.getText().toString().trim()));
                usersInformation.setEmail((adultemail.getText().toString().trim()));
                usersInformation.setColor_code(new ColorCode().getValue("" + adultColor.getTag()));
//                userInformation.setNotification_app("3");
//                userInformation.setNotification_task("3");
                userInfoList.add(usersInformation);
            }
        }
        return userInfoList;
    }


    private SignUpModel getSignUpRequestModel() {
        SignUpModel.Family family = new SignUpModel().new Family();

        family.setFamily_password(mFamilysharepswd.getText().toString().trim());
        family.setConfirm_password(mConfirmPswd.getText().toString().trim());
        family.setPincode(mAddpincode.getText().toString().trim());
        family.setFamily_image("");
        family.setFamily_image(profileImageUrl);
        family.setFamily_city("" + SharedPrefUtils.getCurrentCityModel(this).getId());
        family.setFamily_name(mFamilyname.getText().toString().trim());

        if (signUpFlag)
            SharedPrefUtils.setSignupFlag(this, 0);
        else
            SharedPrefUtils.setSignupFlag(this, 1);

        ArrayList<KidsInformation> kidsArray = new ArrayList<KidsInformation>();
        kidsArray = getKidsInfo();

        ArrayList<SignUpModel.User> userArray = new ArrayList<>();

        userArray = getAdultInfo();

        SignUpModel _requestModel = new SignUpModel();
        _requestModel.setFamily(family);
        _requestModel.setKidInformation(kidsArray);
        _requestModel.setUser(userArray);

        return _requestModel;
    }

    public void showDatePickerDialog() {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    private void openGallery() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, Constants.OPEN_GALLERY);
    }

    private void startCropImage() {

        Intent intent = new Intent(this, CropImage.class);
        intent.putExtra(CropImage.IMAGE_PATH, mFileTemp.getPath());
        intent.putExtra(CropImage.SCALE, true);

        intent.putExtra(CropImage.ASPECT_X, 1);
        intent.putExtra(CropImage.ASPECT_Y, 1);

        startActivityForResult(intent, Constants.CROP_IMAGE);

    }

    public static void copyStream(InputStream input, OutputStream output) throws IOException {

        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = input.read(buffer)) != -1) {
            output.write(buffer, 0, bytesRead);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case Constants.CROP_IMAGE:
                if (requestCode == Constants.CROP_IMAGE && data != null) {
                    try {
                        String filePath = data.getStringExtra(CropImage.IMAGE_PATH);
                        if (filePath == null) {
                            return;
                        }
                        Log.e("File", "filePath: " + filePath);

                        File file = new File(new URI("file://"
                                + filePath.replaceAll(" ", "%20")));
                        int maxImageSize = BitmapUtils.getMaxSize(this);
                        Bitmap sourceBitmap = BitmapUtils.getScaledBitmap(file,
                                maxImageSize);

                        ExifInterface exif = new ExifInterface(file.getPath());
                        int orientation = exif.getAttributeInt(
                                ExifInterface.TAG_ORIENTATION,
                                ExifInterface.ORIENTATION_NORMAL);
                        Matrix matrix = new Matrix();
                        switch (orientation) {
                            case ExifInterface.ORIENTATION_ROTATE_90:
                                matrix.postRotate(90);
                                break;
                            case ExifInterface.ORIENTATION_ROTATE_180:
                                matrix.postRotate(180);
                                break;
                            case ExifInterface.ORIENTATION_ROTATE_270:
                                matrix.postRotate(270);
                                break;
                        }

                        originalImage = Bitmap.createBitmap(sourceBitmap, 0, 0,
                                sourceBitmap.getWidth(), sourceBitmap.getHeight(),
                                matrix, true);
                        sendUploadProfileImageRequest(originalImage);


                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
                break;

            case Constants.OPEN_GALLERY:
                if (resultCode == Activity.RESULT_OK) {
                    try {


                        InputStream inputStream = getContentResolver().openInputStream(data.getData());
                        FileOutputStream fileOutputStream = new FileOutputStream(mFileTemp);
                        copyStream(inputStream, fileOutputStream);
                        fileOutputStream.close();
                        inputStream.close();

                        startCropImage();

//                        Uri selectedImage = data.getData();
//                        String[] filePathColumn = {MediaStore.Images.Media.DATA};
//
//                        Cursor cursor = this.getContentResolver().query(
//                                selectedImage, filePathColumn, null, null, null);
//                        cursor.moveToFirst();
//
//                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
//                        String filePath = cursor.getString(columnIndex);
//                        cursor.close();
//                        Log.e("File", "filePath: " + filePath);
//
//                        File file = new File(new URI("file://"
//                                + filePath.replaceAll(" ", "%20")));
//                        int maxImageSize = BitmapUtils.getMaxSize(this);
//                        Bitmap sourceBitmap = BitmapUtils.getScaledBitmap(file,
//                                maxImageSize);
//
//                        ExifInterface exif = new ExifInterface(file.getPath());
//                        int orientation = exif.getAttributeInt(
//                                ExifInterface.TAG_ORIENTATION,
//                                ExifInterface.ORIENTATION_NORMAL);
//                        Matrix matrix = new Matrix();
//                        switch (orientation) {
//                            case ExifInterface.ORIENTATION_ROTATE_90:
//                                matrix.postRotate(90);
//                                break;
//                            case ExifInterface.ORIENTATION_ROTATE_180:
//                                matrix.postRotate(180);
//                                break;
//                            case ExifInterface.ORIENTATION_ROTATE_270:
//                                matrix.postRotate(270);
//                                break;
//                        }
//
//                        originalImage = Bitmap.createBitmap(sourceBitmap, 0, 0,
//                                sourceBitmap.getWidth(), sourceBitmap.getHeight(),
//                                matrix, true);
//                        sendUploadProfileImageRequest(originalImage);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case Constants.TAKE_PICTURE:
                try {

                    startCropImage();
//                    int maxImageSize = BitmapUtils.getMaxSize(this);
//                    Bitmap sourceBitmap = BitmapUtils.getScaledBitmap(photo,
//                            maxImageSize);
//
//                    ExifInterface exif = new ExifInterface(photo.getPath());
//                    int orientation = exif.getAttributeInt(
//                            ExifInterface.TAG_ORIENTATION,
//                            ExifInterface.ORIENTATION_NORMAL);
//                    Matrix matrix = new Matrix();
//                    switch (orientation) {
//                        case ExifInterface.ORIENTATION_ROTATE_90:
//                            matrix.postRotate(90);
//                            break;
//                        case ExifInterface.ORIENTATION_ROTATE_180:
//                            matrix.postRotate(180);
//                            break;
//                        case ExifInterface.ORIENTATION_ROTATE_270:
//                            matrix.postRotate(270);
//                            break;
//                    }
//
//                    originalImage = Bitmap.createBitmap(sourceBitmap, 0, 0,
//                            sourceBitmap.getWidth(), sourceBitmap.getHeight(),
//                            matrix, true);
//                    sendUploadProfileImageRequest(originalImage);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                break;

        }
    }

    public boolean checkDuplicateEmailIds() {

        ArrayList<String> emailIdlist = new ArrayList<String>();
        emailIdlist.add(mSpouseemail.getText().toString().trim());

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


        return false;
    }

    public boolean checkValidation() {
        boolean result = true;

        if (mSpousename.getText().toString().equals("")) {
            mSpousename.setFocusableInTouchMode(true);
            mSpousename.requestFocus();
            mSpousename.setError(Constants.ENTER_SPOUSENAME);
            result = false;
        } else if (mSpouseemail.getText().toString().trim().equals("")) {

            mSpouseemail.setFocusableInTouchMode(true);
            mSpouseemail.requestFocus();
            mSpouseemail.setError(Constants.ENTER_EMAIL);
            result = false;
        } else if (mFamilysharepswd.getText().toString().equals("")) {

            mFamilysharepswd.setFocusableInTouchMode(true);
            mFamilysharepswd.requestFocus();
            mFamilysharepswd.setError(Constants.ENTER_FAMILY_PSWD);
            result = false;
        } else if (mConfirmPswd.getText().toString().equals("")) {

            mConfirmPswd.setFocusableInTouchMode(true);
            mConfirmPswd.requestFocus();
            mConfirmPswd.setError(Constants.ENTER_CONFIRM_PSWD);
            result = false;
        } else if (mAddpincode.getText().toString().equals("")) {

            mAddpincode.setFocusableInTouchMode(true);
            mAddpincode.requestFocus();
            mAddpincode.setError(Constants.ENTER_PINCODE);
            result = false;
        } else if (mKidsName.getText().toString().trim().equals("")) {

            mKidsName.setFocusableInTouchMode(true);
            mKidsName.requestFocus();
            mKidsName.setError(Constants.ENTER_KIDNAME);
            result = false;
        } else if (mKidsbdy.getText().toString().equals("")) {

            mKidsbdy.setFocusableInTouchMode(true);
            mKidsbdy.requestFocus();
            mKidsbdy.setError(Constants.ENTER_KIDBDY);
            result = false;
        } else if (!StringUtils.isValidEmail(mSpouseemail.getText().toString().trim())) {

            mSpouseemail.setFocusableInTouchMode(true);
            mSpouseemail.requestFocus();
            mSpouseemail.setError(Constants.VALID_EMAIL);
            result = false;
        } else if (mFamilysharepswd.getText().toString().length() < 5) {

            mFamilysharepswd.setFocusableInTouchMode(true);
            mFamilysharepswd.requestFocus();
            mFamilysharepswd.setError(Constants.PASSWORD_LENGTH);
            result = false;
        } else if (!(mFamilysharepswd.getText().toString().equals(mConfirmPswd.getText().toString()))) {

            mConfirmPswd.setFocusableInTouchMode(true);
            mConfirmPswd.requestFocus();
            mConfirmPswd.setError(Constants.PASSWORD_MISMATCH);
            result = false;
        } else if (!checkDuplicateEmailIds()) {
            // check same email id
            ToastUtils.showToast(this, Constants.DUPLICATE_EMAIL);
            result = false;
        }


        return result;
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

                    return false;
                }
            }

        }
        for (int position = 0; position < mAdultContainer.getChildCount(); position++) {
            View innerLayout = (View) mAdultContainer.getChildAt(position);

            EditText adultname = (EditText) innerLayout.findViewById(R.id.spouse_name);
            EditText adultemail = (EditText) innerLayout.findViewById(R.id.spouse_email);


            if (!adultname.getText().toString().trim().equals("")) {
                if (adultemail.getText().toString().trim().equals("")) {

                    adultemail.setError(getResources().getString(R.string.please_enter_valid_email));

                    return false;
                } else {
                    if (!StringUtils.isValidEmail(adultemail.getText().toString())) {
                        adultemail.setError(getResources().getString(R.string.please_enter_valid_email));

                        return false;
                    }

                }
            }

        }
        return result;
    }

    @Override
    public void onClick(View v) {

        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Bundle bundle;

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


            case R.id.profile_image:

                showPhotoOptionsDialog();
                //openGallery();

                break;

            case R.id.notification:

                scrollView.setVisibility(View.GONE);
                ((FrameLayout) findViewById(R.id.setting_frame)).setVisibility(View.VISIBLE);
//               open notification settting fragment
                bundle = new Bundle();
                bundle.putBoolean(Constants.IS_COMMING_FROM_SETTING, false);
                NotificationFragment notificationFragment = new NotificationFragment();
                notificationFragment.setArguments(bundle);
                fragmentTransaction.replace(R.id.setting_frame, notificationFragment);
                fragmentTransaction.commit();

                break;

            case R.id.device_setting:

                scrollView.setVisibility(View.GONE);
                ((FrameLayout) findViewById(R.id.setting_frame)).setVisibility(View.VISIBLE);
//               open sync settting fragment
                bundle = new Bundle();
                bundle.putBoolean(Constants.IS_COMMING_FROM_SETTING, false);
                SyncSettingFragment syncSettingFragment = new SyncSettingFragment();
                syncSettingFragment.setArguments(bundle);
                fragmentTransaction.replace(R.id.setting_frame, syncSettingFragment);
                fragmentTransaction.commit();

                break;

        }

    }

    public void startCamera() {
//        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
//        photo = new File(Environment.getExternalStorageDirectory(), "profile.png");
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo));
//        startActivityForResult(intent, Constants.TAKE_PICTURE);

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
            startActivityForResult(intent, Constants.TAKE_PICTURE);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
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
                            mFileTemp = new File(getFilesDir(), Constants.TEMP_PHOTO_FILE_NAME);
                        }

                        startCamera();
                        break;
                    case R.id.btnGallery:
                        state = Environment.getExternalStorageState();
                        if (Environment.MEDIA_MOUNTED.equals(state)) {
                            mFileTemp = new File(Environment.getExternalStorageDirectory(), Constants.TEMP_PHOTO_FILE_NAME);
                        } else {
                            mFileTemp = new File(getFilesDir(), Constants.TEMP_PHOTO_FILE_NAME);
                        }

                        openGallery();
                        break;
                }
            }
        });
        photoOptionsDialog.show(getSupportFragmentManager(), photoOptionsDialog.getClass().getSimpleName());
    }

    @Override
    public void call() {

        signupModel = getSignUpRequestModel();

        if (!ConnectivityUtils.isNetworkEnabled(ActivitySignUp.this)) {
            ToastUtils.showToast(ActivitySignUp.this, getString(R.string.error_network));
            removeProgressDialog();
            return;
        }


        ControllerSignUp _controller = new ControllerSignUp(ActivitySignUp.this, ActivitySignUp.this);
        _controller.getData(AppConstants.SIGNUP_REQUEST, signupModel);
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
            DatePickerDialog dlg = new DatePickerDialog(getActivity(), this, curent_year, current_month, current_day);
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


    // for uploading image
    public void sendUploadProfileImageRequest(Bitmap originalImage) {
        showProgressDialog(getResources().getString(R.string.please_wait));
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        originalImage.compress(Bitmap.CompressFormat.PNG, 75, bao);
        //originalImage.compress(Bitmap.CompressFormat.PNG, 100, bao);
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

        UserTable userTable = new UserTable((BaseApplication) this.getApplication());
        UserModel userModel = userTable.getAllUserData();

        JSONArray jsonArray = new JSONArray();
        jsonArray.put(jsonObject);

        try {
            ImageUploadRequest requestData = new ImageUploadRequest();
            requestData.setImage(jsonArray.toString());
            requestData.setType(AppConstants.IMAGE_TYPE_USER_PROFILE);
//        requestData.setUser_id("" + userModel.getUser().getId());
            //requestData.setSessionId("" + userModel.getUser().getSessionId());
            // requestData.setProfileId("" + userModel.getUser().getProfileId());

            ImageUploadController controller = new ImageUploadController(this, this);
            controller.getData(AppConstants.IMAGE_UPLOAD_REQUEST, requestData);
        } catch (Exception e) {
            removeProgressDialog();
            e.printStackTrace();
        }


    }

    public void setProfileImage(String url) {
        //profile_image.setImageBitmap(bitmap);
        //url = "http://s9.postimg.org/n92phj9tr/image1.jpg";
        if (!StringUtils.isNullOrEmpty(url)) {
            Picasso.with(this).load(url).placeholder(R.drawable.family_xxhdpi).error(R.drawable.family_xxhdpi).transform(new RoundedTransformation()).into(profile_image);
            SharedPrefUtils.setProfileImgUrl(this, url);
        }
    }

    @Override
    public void onBackPressed() {
        Fragment currentFrag = getSupportFragmentManager().findFragmentById(R.id.setting_frame);
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        if (((FrameLayout) findViewById(R.id.setting_frame)).getVisibility() == View.GONE) {
            dialog.setMessage(getResources().getString(R.string.exit_signup)).setNegativeButton(R.string.new_yes
                    , new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                    dialog.cancel();
                    finish();


                }
            }).setPositiveButton(R.string.new_cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // do nothing
                    dialog.cancel();


                }
            }).setIcon(android.R.drawable.ic_dialog_alert);

            AlertDialog alert11 = dialog.create();
            alert11.show();

            alert11.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.home_light_blue));
            alert11.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.canceltxt_color));

        }

        if (currentFrag instanceof NotificationFragment) {
            ((NotificationFragment) currentFrag).getFragmentManager().popBackStack();
            ((FrameLayout) findViewById(R.id.setting_frame)).setVisibility(View.GONE);
            getSupportActionBar().setTitle("Family Details");
            scrollView.setVisibility(View.VISIBLE);

        }
        if (currentFrag instanceof SyncSettingFragment) {
            ((SyncSettingFragment) currentFrag).getFragmentManager().popBackStack();
            ((FrameLayout) findViewById(R.id.setting_frame)).setVisibility(View.GONE);
            getSupportActionBar().setTitle("Family Details");
            scrollView.setVisibility(View.VISIBLE);

        }
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
                        scrollView.fullScroll(View.FOCUS_DOWN);
                    }
                });
            }
        }).start();
    }
}
