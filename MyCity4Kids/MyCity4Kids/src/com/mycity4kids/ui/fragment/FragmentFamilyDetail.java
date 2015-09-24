package com.mycity4kids.ui.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.kelltontech.ui.BaseFragment;
import com.kelltontech.utils.BitmapUtils;
import com.kelltontech.utils.ConnectivityUtils;
import com.kelltontech.utils.StringUtils;
import com.kelltontech.utils.ToastUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.ColorCode;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.controller.AddUserKidsController;
import com.mycity4kids.controller.EditProfileController;
import com.mycity4kids.controller.ImageUploadController;
import com.mycity4kids.cropimage.CropImage;
import com.mycity4kids.dbtable.TableAdult;
import com.mycity4kids.dbtable.TableFamily;
import com.mycity4kids.dbtable.TableKids;
import com.mycity4kids.dbtable.UserTable;
import com.mycity4kids.listener.OnButtonClicked;
import com.mycity4kids.models.forgot.CommonResponse;
import com.mycity4kids.models.profile.KidsInformation;
import com.mycity4kids.models.profile.SignUpModel;
import com.mycity4kids.models.user.ImageUploadRequest;
import com.mycity4kids.models.user.KidsInfo;
import com.mycity4kids.models.user.UserInfo;
import com.mycity4kids.models.user.UserModel;
import com.mycity4kids.models.user.UserResponse;
import com.mycity4kids.newmodels.AttendeeModel;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.ui.activity.DashboardActivity;
import com.mycity4kids.ui.adapter.AdapterKidAdultList;
import com.mycity4kids.ui.dialog.PhotoOptionsDialog;
import com.mycity4kids.utils.RoundedTransformation;
import com.mycity4kids.widget.CustomListView;
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
 * Created by manish.soni on 25-06-2015.
 */
public class FragmentFamilyDetail extends BaseFragment implements View.OnClickListener {


    View view;
    TextView additionalAdult, additionalChild;
    CustomListView addChildAdult;
    AdapterKidAdultList adapterKidAdultList;
    private LinearLayout mAdultContainer;
    private LinearLayout mChildContainer;
    private HashMap<String, String> used_colors = new HashMap<>();
    private int childCount = 0;
    int adultCount = 0;
    private static TextView BdayView;
    private Dialog mColorPickerDialog;
    private String color_selected = "";
    private SignUpModel signupModel;
    private ImageView imgProfile;
    private EditText edtFamilyName;
    private Bitmap originalImage;
    private SignUpModel.Family mFamilyModel;
    private File photo;
    private boolean mAddKidsApiComleted, mUpdateProfileRequestCompleted;
    private float density;
    private File mFileTemp;
    public String profileimgUrl = "";
    private ScrollView scrollview;
    private boolean ifAdultAdded = false;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.aa_family_detail, container, false);

        density = getActivity().getResources().getDisplayMetrics().density;

        imgProfile = (ImageView) view.findViewById(R.id.imgProfile);
        edtFamilyName = (EditText) view.findViewById(R.id.edtFamilyName);
//        familyProfile = (TextView) view.findViewById(R.id.family_profile);
        additionalAdult = (TextView) view.findViewById(R.id.additional_adult);
        additionalChild = (TextView) view.findViewById(R.id.additional_child);
        addChildAdult = (CustomListView) view.findViewById(R.id.add_kid_adult);
        scrollview = (ScrollView) view.findViewById(R.id.scroll_view);

        setHasOptionsMenu(true);

        setUserProfile();

        addChildAdult.isExpanded();

        mAdultContainer = (LinearLayout) view.findViewById(R.id.internal_adult_layout);
        mChildContainer = (LinearLayout) view.findViewById(R.id.internal_kid_layout);

        additionalChild.setOnClickListener(this);
        additionalAdult.setOnClickListener(this);
        ((DashboardActivity) getActivity()).setTitle("Family Details");

//        familyProfile.setOnClickListener(this);


        setList();

        addChildAdult.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {


                AttendeeModel attendeeModel = (AttendeeModel) adapterKidAdultList.getItem(i);

                if (attendeeModel.getType().equals("KID")) {

                    Bundle bundle = new Bundle();
                    bundle.putInt("KID_ID", attendeeModel.getId());
                    bundle.putSerializable("used_colors", used_colors);
                    ((DashboardActivity) getActivity()).replaceFragment(new FragmentKidProfile(), bundle, true);

                } else if (attendeeModel.getType().equals("ADULT")) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("ADULT_ID", attendeeModel.getId());
                    bundle.putSerializable("used_colors", used_colors);
                    ((DashboardActivity) getActivity()).replaceFragment(new FragmentAdultProfile(), bundle, true);
                }
            }
        });


        return view;
    }

    private void setUserProfile() {
        imgProfile.setOnClickListener(this);

        TableFamily familyTable = new TableFamily(BaseApplication.getInstance());
        UserModel.FamilyInfo family = familyTable.getFamily();

        if (family != null) {
            edtFamilyName.setText(family.getFamily_name());
        }

        profileimgUrl = SharedPrefUtils.getProfileImgUrl(getActivity());

        if (!StringUtils.isNullOrEmpty(profileimgUrl))
            Picasso.with(getActivity()).load(profileimgUrl).placeholder(R.drawable.family_xxhdpi).error(R.drawable.family_xxhdpi).resize((int) (150 * density), (int) (150 * density)).centerCrop().transform(new RoundedTransformation()).into(imgProfile);
    }

    public void setList() {

        TableKids tableKids = new TableKids(BaseApplication.getInstance());
        ArrayList<KidsInfo> kidsInformations = (ArrayList<KidsInfo>) tableKids.getAllKids();

        TableAdult tableAdult = new TableAdult(BaseApplication.getInstance());
        ArrayList<UserInfo> userInfos = (ArrayList<UserInfo>) tableAdult.getAllAdults();

        ArrayList<AttendeeModel> attendeeList = new ArrayList<AttendeeModel>();

        for (int i = 0; i < kidsInformations.size(); i++) {
            attendeeList.add(new AttendeeModel(kidsInformations.get(i).getId(), "KID", kidsInformations.get(i).getName(), kidsInformations.get(i).getColor_code()));
        }

        for (int i = 0; i < userInfos.size(); i++) {
            attendeeList.add(new AttendeeModel(userInfos.get(i).getId(), "ADULT", userInfos.get(i).getFirst_name(), userInfos.get(i).getColor_code()));
        }

        adapterKidAdultList = new AdapterKidAdultList(getActivity(), attendeeList);
        addChildAdult.setAdapter(adapterKidAdultList);

        // setting used colors

        used_colors.clear();
        for (int i = 0; i < attendeeList.size(); i++) {

            String key = new ColorCode().getKey(attendeeList.get(i).getColorCode());
            used_colors.put("" + i, key);

        }

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
//            case R.id.family_profile:
//                startActivity(new Intent(getActivity(), ActivityFamilyProfile.class));
//                // ((DashboardActivity) getActivity()).replaceFragment(new FragmentFamilyProfile(), null, true);
//                break;
            case R.id.additional_adult:
                ifAdultAdded = true;
                addNewAdult();
                sendScroll();
                break;
            case R.id.additional_child:
                ifAdultAdded = false;
                addNewChild();
                sendScroll();
                break;
            case R.id.imgProfile:
                showPhotoOptionsDialog();
//                openGallery();
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
            // mColorPickerDialog.dismiss();
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

        used_colors.put(id, color_selected);
        // set on the custom view
        if (v != null) {
            //ToastUtils.showToast(this,"color set");
            Drawable drawable = getResources().getDrawable(getResources()
                    .getIdentifier("color_" + color_selected + "xxhdpi", "drawable", getActivity().getPackageName()));
            v.setCompoundDrawablesWithIntrinsicBounds(null, null, null, drawable);
            v.setTag("" + color_selected);

        }
        mColorPickerDialog.dismiss();

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


    private ArrayList<SignUpModel.User> getAdultInfo() {
        ArrayList<SignUpModel.User> userInfoList = new ArrayList<>();

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
                usersInformation.setPincode(SharedPrefUtils.getpinCode(getActivity()));
                userInfoList.add(usersInformation);
            }
        }
        return userInfoList;
    }

    private SignUpModel getSignUpRequestModel() {

        ArrayList<KidsInformation> kidsArray = new ArrayList<KidsInformation>();
        kidsArray = getKidsInfo();

        ArrayList<SignUpModel.User> userArray = new ArrayList<>();

        userArray = getAdultInfo();

        SignUpModel _requestModel = new SignUpModel();
        _requestModel.setKidInformation(kidsArray);
        _requestModel.setUser(userArray);

        return _requestModel;
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
            ToastUtils.showToast(getActivity(), Constants.DUPLICATE_EMAIL);


        return false;
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
        for (int position = 0; position < mAdultContainer.getChildCount(); position++) {
            View innerLayout = (View) mAdultContainer.getChildAt(position);

            EditText adultname = (EditText) innerLayout.findViewById(R.id.spouse_name);
            EditText adultemail = (EditText) innerLayout.findViewById(R.id.spouse_email);


            if (!adultname.getText().toString().trim().equals("")) {
                if (adultemail.getText().toString().trim().equals("")) {

                    adultemail.setError(getResources().getString(R.string.please_enter_valid_email));
                    adultemail.setFocusableInTouchMode(true);
                    adultemail.requestFocus();
                    return false;
                } else {
                    if (!StringUtils.isValidEmail(adultemail.getText().toString())) {
                        adultemail.setError(getResources().getString(R.string.please_enter_valid_email));
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
                if (checkCustomLayoutValidations() && checkDuplicateEmailIds()) {
                    showProgressDialog(getString(R.string.please_wait));
                    mFamilyModel = new SignUpModel().new Family();
                    mFamilyModel.setFamily_name(edtFamilyName.getText().toString().trim());
//                    _requestModel.setFamily_password(mFamilysharepswd.getText().toString().trim());
                    mFamilyModel.setFamily_image(profileimgUrl);

                    EditProfileController _editcontroller = new EditProfileController(getActivity(), this);
                    _editcontroller.getData(AppConstants.EDIT_FAMILY_REQUEST, mFamilyModel);
                }
                break;
        }


    }

    public void saveDatainDB(UserResponse model) {

        TableAdult adultTable = new TableAdult((BaseApplication) getActivity().getApplicationContext());
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
        TableKids kidsTable = new TableKids((BaseApplication) getActivity().getApplicationContext());
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

        TableFamily familyTable = new TableFamily((BaseApplication) getActivity().getApplicationContext());
        familyTable.deleteAll();
        try {

            familyTable.insertData(model.getResult().getData().getFamily());

        } catch (Exception e) {
            e.printStackTrace();
        }
        // update listview

        mAdultContainer.removeAllViews();
        mChildContainer.removeAllViews();
        setList();

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
                    // db update
                    saveDatainDB(responseData);
                    //if (mUpdateProfileRequestCompleted) {
                    removeProgressDialog();
                    mAdultContainer.removeAllViews();
                    mChildContainer.removeAllViews();

                    ((BaseActivity) getActivity()).showSnackbar(getView().findViewById(R.id.root),responseData.getResult().getMessage());

//                    if (ifAdultAdded) {
//                        ((BaseActivity) getActivity()).showSnackbar(getView().findViewById(R.id.root), getResources().getString(R.string.add_adult) + "");
//                    } else {
//                        ((BaseActivity) getActivity()).showSnackbar(getView().findViewById(R.id.root), getResources().getString(R.string.add_child) + "");
//                    }

                } else if (responseData.getResponseCode() == 400) {
                    ((BaseActivity) getActivity()).showSnackbar(getView().findViewById(R.id.root), responseData.getResult().getMessage() + "");
                    removeProgressDialog();
                }
                break;

            case AppConstants.IMAGE_UPLOAD_REQUEST:
                removeProgressDialog();
                if (response.getResponseObject() instanceof CommonResponse) {
                    CommonResponse responseModel = (CommonResponse) response
                            .getResponseObject();
                    if (responseModel.getResponseCode() != 200) {
                        ((BaseActivity) getActivity()).showSnackbar(getView().findViewById(R.id.root), getString(R.string.toast_response_error));
                        return;
                    } else {
                        if (!StringUtils.isNullOrEmpty(responseModel.getResult().getMessage())) {
                            Log.i("IMAGE_UPLOAD_REQUEST", responseModel.getResult().getMessage());
                        }
                        setProfileImage(responseModel.getResult().getMessage());
                        ((BaseActivity) getActivity()).showSnackbar(getView().findViewById(R.id.root), "You have successfully uploaded an image.");
                    }
                }
                break;


            case AppConstants.EDIT_FAMILY_REQUEST:
                mUpdateProfileRequestCompleted = true;
                CommonResponse commonresponseData = (CommonResponse) response.getResponseObject();
                if (commonresponseData.getResponseCode() == 200) {
                    // db update
                    TableFamily tableKids = new TableFamily(BaseApplication.getInstance());
                    tableKids.updateVal(mFamilyModel);

                    SharedPrefUtils.setProfileImgUrl(getActivity(), profileimgUrl);
                    ((DashboardActivity) getActivity()).updateImageProfile();


                    if (mAdultContainer.getChildCount() > 0 || mChildContainer.getChildCount() > 0) {
                        hitApiRequest(AppConstants.ADD_ADDITIONAL_USERKID_REQ);
                    } else {
                        removeProgressDialog();
                        ((BaseActivity) getActivity()).showSnackbar(getView().findViewById(R.id.root), commonresponseData.getResult().getMessage() + "");
                    }


                    //if (mAddKidsApiComleted) {

                    //}
                } else if (commonresponseData.getResponseCode() == 400) {
                    ((BaseActivity) getActivity()).showSnackbar(getView().findViewById(R.id.root), commonresponseData.getResult().getMessage() + "");
                    removeProgressDialog();
                }
                break;
        }
    }

    private void addDynamicAdult() {
        ++adultCount;
        LayoutInflater layoutInflater = (LayoutInflater) getActivity().getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
                showColorPickerDialog("adult" + convertView.getId(), adultColor);
            }
        });

        int digit = getRandomNumber();
        Drawable drawable = getResources().getDrawable(getResources()
                .getIdentifier("color_" + digit + "xxhdpi", "drawable", getActivity().getPackageName()));
        adultColor.setCompoundDrawablesWithIntrinsicBounds(null, null, null, drawable);
        used_colors.put("adult" + convertView.getId(), "" + digit);

        adultColor.setTag("" + digit);

        mAdultContainer.addView(convertView);
        //scrollToBottom();

    }

    private void addNewAdult() {

        boolean addAdult = false;

        if (mAdultContainer.getChildCount() > 0) {


            for (int position = 0; position < mAdultContainer.getChildCount(); position++) {
                View innerLayout = (View) mAdultContainer.getChildAt(position);

                EditText adultname = (EditText) innerLayout.findViewById(R.id.spouse_name);
                EditText adultemail = (EditText) innerLayout.findViewById(R.id.spouse_email);

                if ((adultname.getText().toString().trim().equals("")) || (adultemail.getText().toString().trim().equals(""))) {

                    addAdult = false;
                    ToastUtils.showToast(getActivity(), getResources().getString(R.string.enter_adult));
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

        hitApiRequest(AppConstants.EDIT_FAMILY_REQUEST);
        //hitApiRequest(AppConstants.ADD_ADDITIONAL_USERKID_REQ);
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

            DatePickerDialog dlg = new DatePickerDialog(getActivity(), this, curent_year, current_month, current_day);
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

//    public void scrollToBottom()
//    {
//        scrollview.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                scrollview.post(new Runnable() {
//                    public void run() {
//                        scrollview.fullScroll(View.FOCUS_DOWN);
//                    }
//                });
//            }
//        });
//    }

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

        // scrollToBottom();


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
//        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
//        photo = new File(Environment.getExternalStorageDirectory(), "profile.png");
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo));
//        getActivity().startActivityForResult(intent, Constants.TAKE_PICTURE);

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


    private boolean isDataValid() {
        boolean isLoginOk = true;
        String email_id = edtFamilyName.getText().toString();

        if (edtFamilyName.getText().toString().length() == 0) {
            edtFamilyName.setFocusableInTouchMode(true);
            edtFamilyName.requestFocus();
            edtFamilyName.setError("Please enter family name");
            isLoginOk = false;
        }
        return isLoginOk;
    }

    private void startCropImage() {

        Intent intent = new Intent(getActivity(), CropImage.class);
        intent.putExtra(CropImage.IMAGE_PATH, mFileTemp.getPath());
        intent.putExtra(CropImage.SCALE, true);

        intent.putExtra(CropImage.ASPECT_X, 1);
        intent.putExtra(CropImage.ASPECT_Y, 1);

        getActivity().startActivityForResult(intent, Constants.CROP_IMAGE);

    }

    public static void copyStream(InputStream input, OutputStream output) throws IOException {

        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = input.read(buffer)) != -1) {
            output.write(buffer, 0, bytesRead);
        }
    }

    public void onActivityResultDelegate(int requestCode, int resultCode, Intent data) {

        if (getActivity() == null) {
            return;
        }
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
                        int maxImageSize = BitmapUtils.getMaxSize(getActivity());
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

                        InputStream inputStream = getActivity().getContentResolver().openInputStream(data.getData());
                        FileOutputStream fileOutputStream = new FileOutputStream(mFileTemp);
                        copyStream(inputStream, fileOutputStream);
                        fileOutputStream.close();
                        inputStream.close();

                        startCropImage();

//                        Uri selectedImage = data.getData();
//                        String[] filePathColumn = {MediaStore.Images.Media.DATA};
//
//                        Cursor cursor = getActivity().getContentResolver().query(
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
//                        int maxImageSize = BitmapUtils.getMaxSize(getActivity());
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
//                    int maxImageSize = BitmapUtils.getMaxSize(getActivity());
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

    public void setProfileImage(String url) {
        if (!StringUtils.isNullOrEmpty(url)) {
            profileimgUrl = url;
            // SharedPrefUtils.setProfileImgUrl(getActivity(), url);
            //((DashboardActivity) getActivity()).updateImageProfile();
            Picasso.with(getActivity()).load(url).placeholder(R.drawable.family_xxhdpi).error(R.drawable.family_xxhdpi).resize((int) (150 * density), (int) (150 * density)).centerCrop().transform(new RoundedTransformation()).into(imgProfile);
        }
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








