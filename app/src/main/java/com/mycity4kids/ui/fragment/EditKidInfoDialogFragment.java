package com.mycity4kids.ui.fragment;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.mycity4kids.utils.DateTimeUtils;
import com.mycity4kids.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.models.response.KidsModel;
import com.mycity4kids.models.user.KidsInfo;
import com.mycity4kids.preference.SharedPrefUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by user on 08-06-2015.
 */
public class EditKidInfoDialogFragment extends DialogFragment implements OnClickListener {

    private String selectedLang = "";
    private String userId = "";
    private TextView kidNameEditText, kidsDOBTextView, deleteKidInfoTextView;
    private RadioButton maleRadioButton, femaleRadioButton;
    private RadioGroup genderRadioGroup;
    private static TextView dobTextView;
    private ImageView closeDialogImageView;
    private TextView saveKidInfoTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.edit_kid_info_dialog_fragment, container,
                false);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        userId = SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId();

        KidsModel kidsModel = (KidsModel) getArguments().get("editKidInfo");

        closeDialogImageView = (ImageView) rootView.findViewById(R.id.closeDialogImageView);
        kidNameEditText = (TextView) rootView.findViewById(R.id.kidNameEditText);
        kidsDOBTextView = (TextView) rootView.findViewById(R.id.kidsDOBTextView);
        deleteKidInfoTextView = (TextView) rootView.findViewById(R.id.deleteKidInfoTextView);
        saveKidInfoTextView = (TextView) rootView.findViewById(R.id.saveKidInfoTextView);
        maleRadioButton = (RadioButton) rootView.findViewById(R.id.maleRadioButton);
        femaleRadioButton = (RadioButton) rootView.findViewById(R.id.femaleRadioButton);
        genderRadioGroup = (RadioGroup) rootView.findViewById(R.id.genderRadioGroup);

        closeDialogImageView.setOnClickListener(this);
        deleteKidInfoTextView.setOnClickListener(this);
        saveKidInfoTextView.setOnClickListener(this);

        kidNameEditText.setText(kidsModel.getName());
        kidsDOBTextView.setText(DateTimeUtils.getKidsDOBNanoMilliTimestamp("" + kidsModel.getBirthDay()));
        if ("0".equals(kidsModel.getGender())) {
            maleRadioButton.setChecked(true);
            femaleRadioButton.setChecked(false);
        } else {
            maleRadioButton.setChecked(false);
            femaleRadioButton.setChecked(true);
        }

        kidNameEditText.setText(kidsModel.getName());

        kidsDOBTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dobTextView = kidsDOBTextView;
                showDatePickerDialog();
            }
        });
        return rootView;
    }

    public void showDatePickerDialog() {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
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
            e.printStackTrace();
        }
        return timestamp;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.saveKidInfoTextView:
                if (validateKidsInfo()) {
                    KidsInfo kidsinfo = new KidsInfo();
                    kidsinfo.setName(kidNameEditText.getText().toString());
                    kidsinfo.setDate_of_birth(kidsDOBTextView.getText().toString());
                    int radioButtonID = genderRadioGroup.getCheckedRadioButtonId();
                    if (radioButtonID == maleRadioButton.getId()) {
                        kidsinfo.setGender("0");
                    } else {
                        kidsinfo.setGender("1");
                    }
                    ((About) getParentFragment()).saveEditKidInfo(kidsinfo);
                }
                break;
            case R.id.deleteKidInfoTextView:
                ((About) getParentFragment()).deleteKid();
                break;
            case R.id.closeDialogImageView:
                dismiss();
                break;
        }
    }

    private boolean validateKidsInfo() {
        if (kidNameEditText.getText() == null || kidNameEditText.getText().toString().isEmpty()) {
            Toast.makeText(getActivity(), getString(R.string.app_settings_edit_profile_toast_empty_name_kid), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (StringUtils.isNullOrEmpty(kidsDOBTextView.getText().toString()) || !DateTimeUtils.isValidDate(kidsDOBTextView.getText().toString())) {
            Toast.makeText(getActivity(), getString(R.string.app_settings_edit_profile_toast_incorrect_date), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!maleRadioButton.isChecked() && !femaleRadioButton.isChecked()) {
            Toast.makeText(getActivity(), getString(R.string.app_settings_edit_profile_toast_choose_gender), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }


}
