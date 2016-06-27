package com.mycity4kids.ui.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseFragment;
import com.kelltontech.utils.ConnectivityUtils;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.ColorCode;
import com.mycity4kids.controller.EditProfileController;
import com.mycity4kids.dbtable.TableAdult;
import com.mycity4kids.models.forgot.CommonResponse;
import com.mycity4kids.models.profile.SignUpModel;
import com.mycity4kids.models.user.UserInfo;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.reminders.AppointmentManager;
import com.mycity4kids.ui.activity.DashboardActivity;
import com.mycity4kids.ui.activity.LoadingActivity;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by manish.soni on 25-06-2015.
 */
public class FragmentAdultProfile extends BaseFragment implements View.OnClickListener {

    View view;
    EditText name, email, pincode;
    String id;
    private Dialog mColorPickerDialog;
    private String color_selected = "";
    private HashMap<String, String> used_colors = new HashMap<>();
    private TextView mColorfrAdult;
    SignUpModel.User _requestModel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.aa_adult_profile, container, false);

        if (getArguments() != null) {
            id = (String) getArguments().get("ADULT_ID");
            used_colors = (HashMap) getArguments().getSerializable("used_colors");
        }

        TableAdult tableAdult = new TableAdult(BaseApplication.getInstance());
        UserInfo userInfo = (UserInfo) tableAdult.getAdults(id);

        name = (EditText) view.findViewById(R.id.adult_name);
        email = (EditText) view.findViewById(R.id.adult_email);
//        pincode = (EditText) view.findViewById(R.id.pincode);

        mColorfrAdult = (TextView) view.findViewById(R.id.adultcolor);

        String key = new ColorCode().getKey(userInfo.getColor_code());
        Drawable drawable = getResources().getDrawable(getResources()
                .getIdentifier("color_" + key + "xxhdpi", "drawable", getActivity().getPackageName()));
        mColorfrAdult.setCompoundDrawablesWithIntrinsicBounds(null, null, null, drawable);
        mColorfrAdult.setTag(key);

        mColorfrAdult.setOnClickListener(this);


        ((DashboardActivity) getActivity()).setTitle(userInfo.getFirst_name());

        name.setText(userInfo.getFirst_name());
        email.setText(userInfo.getEmail());
//        pincode.setText(userInfo.getPincode());

//        TableFamily familyTable = new TableFamily((BaseApplication) getActivity().getApplicationContext());
//        UserModel.FamilyInfo family = familyTable.getFamily();
//
//        if (family != null) {
//
//            pincode.setText(family.getPincode());
//
//        }

        return view;

    }

    private boolean isDataValid() {
        boolean isLoginOk = true;
        String email_id = email.getText().toString();

        if (email_id.trim().length() == 0 || (!StringUtils.isValidEmail(email_id))) {
            email.setFocusableInTouchMode(true);
            email.setError("Please enter valid email id");
            email.requestFocus();
            isLoginOk = false;
        } else if (name.getText().toString().length() == 0) {
            name.setFocusableInTouchMode(true);
            name.requestFocus();
            name.setError("Please enter name");
            isLoginOk = false;
        }
//        else if (pincode.getText().toString().length() == 0) {
//            pincode.setFocusableInTouchMode(true);
//            pincode.requestFocus();
//            pincode.setError("Please enter pincode");
//            isLoginOk = false;
//        }
        return isLoginOk;
    }

    public void callService() {

        if (ConnectivityUtils.isNetworkEnabled(getActivity())) {

            if (isDataValid()) {

                showProgressDialog(getString(R.string.please_wait));

                _requestModel = new SignUpModel().new User();
                _requestModel.setUsername(name.getText().toString().trim());
                _requestModel.setEmail(email.getText().toString().trim());
                _requestModel.setColor_code(new ColorCode().getValue(""+mColorfrAdult.getTag()));
                _requestModel.setId("" + id);
//                _requestModel.setPincode(pincode.getText().toString());

                EditProfileController _controller = new EditProfileController(getActivity(), this);
                _controller.getData(AppConstants.EDIT_ADULTPROFILE_REQUEST, _requestModel);

            }


        } else {
            Toast.makeText(getActivity(), getString(R.string.error_network), Toast.LENGTH_SHORT).show();
        }

    }

    protected void updateUi(Response response) {
        if (response == null) {
            removeProgressDialog();
            Toast.makeText(getActivity(), getResources().getString(R.string.server_error), Toast.LENGTH_SHORT).show();
            return;
        }

        CommonResponse responseData = (CommonResponse) response.getResponseObject();
        if (responseData.getResponseCode() == 200) {
            Toast.makeText(getActivity(), responseData.getResult().getMessage(), Toast.LENGTH_SHORT).show();
            // db update
            TableAdult tableKids = new TableAdult(BaseApplication.getInstance());
            tableKids.updateVal(_requestModel);
            boolean isUserPinCodeChanged = false;
            if(_requestModel.getId().equalsIgnoreCase("" + SharedPrefUtils.getUserDetailModel(getActivity()).getId()))
            {
                if (!SharedPrefUtils.getpinCode(getActivity()).equals("" + _requestModel.getPincode())){
                    isUserPinCodeChanged = true;
                }
                SharedPrefUtils.setpinCode(getActivity(),_requestModel.getPincode());
            }

            AppointmentManager.getInstance(getActivity()).clearList();
           // TableFamily family = new TableFamily(BaseApplication.getInstance());
           // family.updatePincode(_requestModel.getPincode());

            if (isUserPinCodeChanged){
                Intent intent = new Intent(getActivity(), LoadingActivity.class);
               // intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            } else {
                ((DashboardActivity) getActivity()).replaceFragment(new FragmentFamilyDetail(), null, true);
            }


        } else if (responseData.getResponseCode() == 400) {
            Toast.makeText(getActivity(), responseData.getResult().getMessage(), Toast.LENGTH_SHORT).show();

        }
        removeProgressDialog();

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

    @Override
    public void onClick(View view) {

        switch (view.getId()) {


            case R.id.adultcolor:

                showColorPickerDialog("adult", null);
                break;

        }


    }

    public void setColor(String id, TextView v) {

        used_colors.put(id, color_selected);
        // set on the custom view

        //ToastUtils.showToast(this,"color set");
        Drawable drawable = getResources().getDrawable(getResources()
                .getIdentifier("color_" + color_selected + "xxhdpi", "drawable", getActivity().getPackageName()));
        mColorfrAdult.setCompoundDrawablesWithIntrinsicBounds(null, null, null, drawable);
        mColorfrAdult.setTag("" + color_selected);
        mColorPickerDialog.dismiss();

    }


}
