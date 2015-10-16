package com.mycity4kids.ui.fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.kelltontech.ui.IScreen;
import com.kelltontech.utils.ConnectivityUtils;
import com.kelltontech.utils.StringUtils;
import com.kelltontech.utils.ToastUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.ColorCode;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.controller.AddUserKidsController;
import com.mycity4kids.dbtable.TableAdult;
import com.mycity4kids.dbtable.TableFamily;
import com.mycity4kids.dbtable.TableKids;
import com.mycity4kids.models.profile.SignUpModel;
import com.mycity4kids.models.user.KidsInfo;
import com.mycity4kids.models.user.UserInfo;
import com.mycity4kids.models.user.UserModel;
import com.mycity4kids.models.user.UserResponse;
import com.mycity4kids.newmodels.AttendeeModel;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.ui.activity.ActivityCreateAppointment;
import com.mycity4kids.ui.activity.ActivityCreateTask;
import com.mycity4kids.ui.activity.ActivityEditAppointment;
import com.mycity4kids.ui.activity.ActivityEditTask;
import com.mycity4kids.ui.adapter.AdapterKidAdultList;
import com.mycity4kids.ui.adapter.AttendeeCustomAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Created by user on 08-06-2015.
 */
public class AddAdultDialogFragment extends android.app.DialogFragment implements IScreen {

    ArrayList<AttendeeModel> data;
    TextView cancel, done;
    EditText adultname, adultemail;
    TextView adultColor;
    AttendeeCustomAdapter adapter;
    private Dialog mColorPickerDialog;
    ArrayList<AttendeeModel> addAttendeeList;
    private HashMap<String, String> used_colors = new HashMap<>();
    private String color_selected = "";
    private boolean all;
    private boolean edit;
    String iftask = "";
    private SignUpModel signupModel;
    ArrayList<AttendeeModel> attendeeList;
    private ProgressDialog mProgressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.aa_addadult_fragment, container, false);
//        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        adultname = (EditText) rootView.findViewById(R.id.spouse_name);
        adultemail = (EditText) rootView.findViewById(R.id.spouse_email);
        adultColor = (TextView) rootView.findViewById(R.id.color_spouse);
        cancel = (TextView) rootView.findViewById(R.id.cancel);
        done = (TextView) rootView.findViewById(R.id.done);

        Bundle extras = getArguments();
        if (extras != null) {
            all = extras.getBoolean("All");
            edit = extras.getBoolean("edit");
            iftask = extras.getString("iftask");

        }
        adultColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showColorPickerDialog("adult", adultColor);
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkCustomLayoutValidations()) {
                    showProgressDialog(getString(R.string.please_wait));
                    hitApiRequest();
                }
            }
        });

        disableUsedColor();
        return rootView;
    }

    private void disableUsedColor() {
        TableKids tableKids = new TableKids(BaseApplication.getInstance());
        ArrayList<KidsInfo> kidsInformations = (ArrayList<KidsInfo>) tableKids.getAllKids();

        TableAdult tableAdult = new TableAdult(BaseApplication.getInstance());
        ArrayList<UserInfo> userInfos = (ArrayList<UserInfo>) tableAdult.getAllAdults();

        addAttendeeList = new ArrayList<AttendeeModel>();

        for (int i = 0; i < kidsInformations.size(); i++) {
            addAttendeeList.add(new AttendeeModel(kidsInformations.get(i).getId(), "KID", kidsInformations.get(i).getName(), kidsInformations.get(i).getColor_code()));
        }

        for (int i = 0; i < userInfos.size(); i++) {
            addAttendeeList.add(new AttendeeModel(userInfos.get(i).getId(), "ADULT", userInfos.get(i).getFirst_name(), userInfos.get(i).getColor_code()));
        }

        used_colors.clear();

        for (int i = 0; i < addAttendeeList.size(); i++) {

            String key = new ColorCode().getKey(addAttendeeList.get(i).getColorCode());
            used_colors.put("" + i, key);

        }

        int digit = getRandomNumber();
        Drawable drawable = getResources().getDrawable(getResources()
                .getIdentifier("color_" + digit + "xxhdpi", "drawable", getActivity().getPackageName()));
        adultColor.setCompoundDrawablesWithIntrinsicBounds(null, null, null, drawable);
        used_colors.put("adult", "" + digit);
        adultColor.setTag("" + digit);
    }

    public void showProgressDialog(String bodyText) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setOnKeyListener(new Dialog.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_CAMERA || keyCode == KeyEvent.KEYCODE_SEARCH) {
                        return true; //
                    }
                    return false;
                }
            });
        }

        mProgressDialog.setMessage(bodyText);

        if (!mProgressDialog.isShowing()) {
            mProgressDialog.show();
        }
    }

    public void removeProgressDialog() {
        try {
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void hitApiRequest() {
        if (!ConnectivityUtils.isNetworkEnabled(getActivity())) {
            ((BaseActivity) getActivity()).showSnackbar(getView().findViewById(R.id.root), getString(R.string.error_network));
            return;
        }

        signupModel = getSignUpRequestModel();
        AddUserKidsController _controller = new AddUserKidsController(getActivity(), this);
        _controller.getData(AppConstants.ADD_ADDITIONAL_USERKID_REQ, signupModel);

    }

    private SignUpModel getSignUpRequestModel() {

        ArrayList<SignUpModel.User> userArray = new ArrayList<>();
        userArray = getAdultInfo();

        SignUpModel _requestModel = new SignUpModel();
        _requestModel.setUser(userArray);

        return _requestModel;
    }

    private ArrayList<SignUpModel.User> getAdultInfo() {
        ArrayList<SignUpModel.User> userInfoList = new ArrayList<>();

        if ((adultname.getText().toString().trim().equals("")) || (adultemail.getText().toString().trim().equals(""))) {
        } else {
            SignUpModel.User usersInformation = new SignUpModel().new User();
            usersInformation.setUsername((adultname.getText().toString().trim()));
            usersInformation.setEmail((adultemail.getText().toString().trim()));
            usersInformation.setColor_code(new ColorCode().getValue("" + adultColor.getTag()));
            usersInformation.setPincode(SharedPrefUtils.getpinCode(getActivity()));
            userInfoList.add(usersInformation);
        }
        return userInfoList;
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
            //view.findViewWithTag(value).setEnabled(false);
            view.findViewWithTag(value).setAlpha(0.2f);
        }

        mColorPickerDialog.show();

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

    public boolean checkCustomLayoutValidations() {
        boolean result = true;

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
        return result;
    }

    @Override
    public void handleUiUpdate(Response response) {
        removeProgressDialog();
        if (response == null) {
            Toast.makeText(getActivity(), getResources().getString(R.string.server_error), Toast.LENGTH_SHORT).show();
            return;
        }

        switch (response.getDataType()) {

            case AppConstants.ADD_ADDITIONAL_USERKID_REQ:
//                mAddKidsApiComleted = true;
                UserResponse responseData = (UserResponse) response.getResponseObject();
                if (responseData.getResponseCode() == 200) {
                    // db update
                    saveDatainDB(responseData);
                    Toast.makeText(getActivity(), "Successfully added an adult account", Toast.LENGTH_SHORT).show();

                    WhoToRemindDialogFragment dialogFragment = new WhoToRemindDialogFragment();
                    ArrayList<Integer> idlist = new ArrayList<>();
                    Bundle args = new Bundle();
                    args.putIntegerArrayList("chkValues", idlist);
                    args.putBoolean("All", all);
                    args.putBoolean("edit", false);
                    args.putString("iftask", iftask);
                    dialogFragment.setArguments(args);


                    dialogFragment.setTargetFragment(dialogFragment, 2);
                    dialogFragment.show(getFragmentManager(), "whotoremind");

                    getDialog().dismiss();
//                    removeProgressDialog();
                } else if (responseData.getResponseCode() == 400) {
                    Toast.makeText(getActivity(), responseData.getResult().getMessage() + "", Toast.LENGTH_SHORT).show();
//                    removeProgressDialog();
                }
        }

        if (response == null) {
            //showToast(getResources().getString(R.string.server_error));

            return;
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

        // saving family

        TableFamily familyTable = new TableFamily((BaseApplication) getActivity().getApplicationContext());
        familyTable.deleteAll();
        try {

            familyTable.insertData(model.getResult().getData().getFamily());

        } catch (Exception e) {
            e.printStackTrace();
        }
        // update listview
        //        setList();

    }
}
