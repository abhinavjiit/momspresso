package com.mycity4kids.ui.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.kelltontech.utils.ConnectivityUtils;
import com.kelltontech.utils.StringUtils;
import com.kelltontech.utils.ToastUtils;
import com.mycity4kids.R;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.ColorCode;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.controller.ControllerSignUp;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.forgot.CommonResponse;
import com.mycity4kids.models.user.UserResponse;
import com.mycity4kids.newmodels.NewSignUpModel;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.ui.fragment.NotificationFragment;
import com.mycity4kids.ui.fragment.SyncSettingFragment;
import com.mycity4kids.utils.RoundedTransformation;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by hemant on 27/1/16.
 */
public class SocialSignUpActivity extends BaseActivity implements View.OnClickListener {

    private Toolbar mToolbar;
    private ImageView profile_image;
    private EditText mSpousename;
    private EditText mSpouseMobile;
    private TextView mColorfrSpouse;
    private Dialog mColorPickerDialog;
    private LinearLayout rootLayout;
    private HashMap<String, String> used_colors = new HashMap<>();

    private String color_selected = "";
    private String email = "";
    private String name = "";
    private String access_token = "";
    private String mode = "";
    private String profileImgUrl = "";
    private String social_id = "";

    private NewSignUpModel newSignupModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.social_signup_layout);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        Utils.pushOpenScreenEvent(SocialSignUpActivity.this, "Social Media Signup", SharedPrefUtils.getUserDetailModel(this).getId() + "");

        setSupportActionBar(mToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Sign Up");

        Bundle bundle = getIntent().getBundleExtra("userbundle");
        mode = bundle.getString(Constants.MODE);

        social_id = bundle.getString(Constants.USER_ID);
        name = bundle.getString(Constants.USER_NAME);
        email = bundle.getString(Constants.USER_EMAIL);
        access_token = bundle.getString(Constants.ACCESS_TOKEN);
        profileImgUrl = bundle.getString(Constants.PROFILE_IMAGE);

        rootLayout = (LinearLayout) findViewById(R.id.root);
        profile_image = (ImageView) findViewById(R.id.profile_image);
        mSpousename = (EditText) findViewById(R.id.spouse_name);
        mSpouseMobile = (EditText) findViewById(R.id.spouse_mobile);
        mColorfrSpouse = (TextView) findViewById(R.id.color_spouse);

        mSpousename.setText(name);
        mColorfrSpouse.setOnClickListener(this);
        mColorfrSpouse.setTag("1");
        used_colors.put("spouse1", "1");
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
                Log.d("Social Login", "Go Back");
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

                return true;

            case R.id.save:
                if (checkValidation()) {
                    newSignupModel = getNewSignUpRequestModel();

                    if (!ConnectivityUtils.isNetworkEnabled(SocialSignUpActivity.this)) {
                        ToastUtils.showToast(SocialSignUpActivity.this, getString(R.string.error_network));
                        return true;
                    }

                    showProgressDialog(getString(R.string.please_wait));
                    ControllerSignUp _controller = new ControllerSignUp(SocialSignUpActivity.this, SocialSignUpActivity.this);
                    _controller.getData(AppConstants.NEW_SIGNUP_REQUEST, newSignupModel);
                    return true;
                }
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    public boolean checkValidation() {
        boolean result = true;

        if (mSpousename.getText().toString().equals("")) {
            mSpousename.setFocusableInTouchMode(true);
            mSpousename.requestFocus();
            mSpousename.setError(Constants.ENTER_SPOUSENAME);
            result = false;
        } else if (!StringUtils.checkMobileNumber(mSpouseMobile.getText().toString())) {
            mSpouseMobile.setFocusableInTouchMode(true);
            mSpouseMobile.requestFocus();
            mSpouseMobile.setError(Constants.ENTER_VALID_MOBILE);
            result = false;
        }

        return result;
    }

    private NewSignUpModel getNewSignUpRequestModel() {
        NewSignUpModel nsuModel = new NewSignUpModel();
        nsuModel.setUsername(mSpousename.getText().toString().trim());
        nsuModel.setMobileNumber(mSpouseMobile.getText().toString().trim());
        nsuModel.setEmail(email);
//        nsuModel.setPassword(mFamilysharepswd.getText().toString().trim());
        nsuModel.setProfileImgUrl(profileImgUrl);
        nsuModel.setColor_code(new ColorCode().getValue("" + mColorfrSpouse.getTag()));
        nsuModel.setSocialMode(mode);
        nsuModel.setSocialToken(access_token);

        return nsuModel;
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


            case AppConstants.NEW_SIGNUP_REQUEST:

                UserResponse responseData = (UserResponse) response.getResponseObject();
                String message = responseData.getResult().getMessage();
                if (responseData.getResponseCode() == 200) {

                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

                    removeProgressDialog();
                    Intent intent = new Intent(SocialSignUpActivity.this, ActivityVerifyOTP.class);
                    intent.putExtra("email", newSignupModel.getEmail());
                    intent.putExtra("mobile", newSignupModel.getMobileNumber());
                    startActivity(intent);

                } else if (responseData.getResponseCode() == 400) {

                    removeProgressDialog();
                    Toast.makeText(this, message, Toast.LENGTH_LONG).show();
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
                            android.util.Log.i("Uploaded Image URL", responseModel.getResult().getMessage());

                            profileImgUrl = responseModel.getResult().getMessage();
                        }
                        setProfileImage(responseModel.getResult().getMessage());
                        showSnackbar(rootLayout, getResources().getString(R.string.upload_iamge_successfully));
                    }
                }
                break;
        }
    }

    public void setProfileImage(String url) {
        if (!StringUtils.isNullOrEmpty(url)) {
            Picasso.with(this).load(url).placeholder(R.drawable.family_xxhdpi).error(R.drawable.family_xxhdpi).transform(new RoundedTransformation()).into(profile_image);
            SharedPrefUtils.setProfileImgUrl(this, url);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.color_spouse:
                showColorPickerDialog("", null);
                break;
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
            view.findViewWithTag(value).setAlpha(0.2f);
        }

        mColorPickerDialog.show();

    }

    public void showSelectedcolorMessage(View v, final String name, final TextView textview, String colorsSelected) {
        if (v.getAlpha() == 0.2f) {
            showSnackbar(rootLayout, getResources().getString(R.string.color_selected));
        } else {
            color_selected = colorsSelected;
            setColor(name, textview);
        }
    }

    public void setColor(String id, TextView v) {

        mColorfrSpouse.setTag(color_selected);
        used_colors.clear();
        used_colors.put(id, color_selected);
        Drawable drawable = getResources().getDrawable(getResources()
                .getIdentifier("color_" + color_selected + "xxhdpi", "drawable", getPackageName()));
        mColorfrSpouse.setCompoundDrawablesWithIntrinsicBounds(null, null, null, drawable);
        mColorPickerDialog.dismiss();

    }
}
