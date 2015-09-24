package com.mycity4kids.ui.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.mycity4kids.R;
import com.mycity4kids.ui.activity.ActivityCreateAppointment;
import com.mycity4kids.ui.activity.ActivityCreateTask;
import com.mycity4kids.ui.activity.ActivityEditAppointment;
import com.mycity4kids.ui.activity.ActivityEditTask;

import java.util.ArrayList;

/**
 * Created by manish.soni on 30-06-2015.
 */
public class DaysFragment extends android.app.DialogFragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {


    CheckBox monday_check, tuesday_check, wednesday_check, thursday_check, friday_check, saturday_check, sunday_check;
    TextView monday, tuesday, wednesday, thursday, friday, saturday, sunday;
    Boolean edit = false;
    String ifTask = "";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.aa_days, container,
                false);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        Window window = getDialog().getWindow();
        window.setGravity(Gravity.BOTTOM | Gravity.CENTER_VERTICAL);

        Bundle extras = getArguments();
        if (extras != null) {
            edit = extras.getBoolean("edit");
            ifTask = extras.getString("iftask");
        }

        monday = (TextView) rootView.findViewById(R.id.monday);
        monday.setOnClickListener(this);
        tuesday = (TextView) rootView.findViewById(R.id.tuesday);
        tuesday.setOnClickListener(this);
        wednesday = (TextView) rootView.findViewById(R.id.wednesday);
        wednesday.setOnClickListener(this);
        thursday = (TextView) rootView.findViewById(R.id.thursday);
        thursday.setOnClickListener(this);
        friday = (TextView) rootView.findViewById(R.id.friday);
        friday.setOnClickListener(this);
        saturday = (TextView) rootView.findViewById(R.id.saturday);
        saturday.setOnClickListener(this);
        sunday = (TextView) rootView.findViewById(R.id.sunday);
        sunday.setOnClickListener(this);

        monday_check = (CheckBox) rootView.findViewById(R.id.monday_check);
        monday_check.setOnCheckedChangeListener(this);
        tuesday_check = (CheckBox) rootView.findViewById(R.id.tuesday_check);
        tuesday_check.setOnCheckedChangeListener(this);
        wednesday_check = (CheckBox) rootView.findViewById(R.id.wednesday_check);
        wednesday_check.setOnCheckedChangeListener(this);
        thursday_check = (CheckBox) rootView.findViewById(R.id.thursday_check);
        thursday_check.setOnCheckedChangeListener(this);
        friday_check = (CheckBox) rootView.findViewById(R.id.friday_check);
        friday_check.setOnCheckedChangeListener(this);
        saturday_check = (CheckBox) rootView.findViewById(R.id.saturday_check);
        saturday_check.setOnCheckedChangeListener(this);
        sunday_check = (CheckBox) rootView.findViewById(R.id.sunday_check);
        sunday_check.setOnCheckedChangeListener(this);


        rootView.findViewById(R.id.ok).setOnClickListener(this);
        rootView.findViewById(R.id.cancel).setOnClickListener(this);


        return rootView;
    }


    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.ok:

                if (edit) {

                    if (ifTask.equalsIgnoreCase("task")) {
                        ((ActivityEditTask) getActivity()).setRepeat("", "Days", getDays());
                    } else {
                        ((ActivityEditAppointment) getActivity()).setRepeat("", "Days", getDays());
                    }

                } else {

                    if (ifTask.equalsIgnoreCase("task")) {
                        ((ActivityCreateTask) getActivity()).setRepeat("", "Days", getDays());
                    } else {
                        ((ActivityCreateAppointment) getActivity()).setRepeat("", "Days", getDays());
                    }
                }

                getDialog().dismiss();
                break;

            case R.id.cancel:

                getDialog().dismiss();
                break;

        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

        switch (compoundButton.getId()) {

            case R.id.monday_check:

                if (monday_check.isChecked() == true) {
                    monday_check.setChecked(true);
                } else {
                    monday_check.setChecked(false);
                }

                break;

            case R.id.tuesday_check:

                if (tuesday_check.isChecked() == true) {
                    tuesday_check.setChecked(true);
                } else {
                    tuesday_check.setChecked(false);
                }

                break;

            case R.id.wednesday_check:

                if (wednesday_check.isChecked() == true) {
                    wednesday_check.setChecked(true);
                } else {
                    wednesday_check.setChecked(false);
                }

                break;

            case R.id.thursday_check:

                if (thursday_check.isChecked() == true) {
                    thursday_check.setChecked(true);
                } else {
                    thursday_check.setChecked(false);
                }

                break;

            case R.id.friday_check:

                if (friday_check.isChecked() == true) {
                    friday_check.setChecked(true);
                } else {
                    friday_check.setChecked(false);
                }

                break;

            case R.id.saturday_check:

                if (saturday_check.isChecked() == true) {
                    saturday_check.setChecked(true);
                } else {
                    saturday_check.setChecked(false);
                }

                break;

            case R.id.sunday_check:

                if (sunday_check.isChecked() == true) {
                    sunday_check.setChecked(true);
                } else {
                    sunday_check.setChecked(false);
                }

                break;

        }
    }

    public String getDays() {

        StringBuilder stringBuilder = new StringBuilder();

        Boolean flag = false;

        ArrayList<CheckBox> arrayList = new ArrayList<>();
        arrayList.add(monday_check);
        arrayList.add(tuesday_check);
        arrayList.add(wednesday_check);
        arrayList.add(thursday_check);
        arrayList.add(friday_check);
        arrayList.add(saturday_check);
        arrayList.add(sunday_check);

        for (int i = 0; i < arrayList.size(); i++) {


            if (arrayList.get(i).isChecked()) {

                if (flag == true) {
                    stringBuilder.append(", ");
                }

                stringBuilder.append(arrayList.get(i).getText().toString());
                flag = true;
            }
        }

        String asd = stringBuilder.toString();
        Log.d("", asd);

        return stringBuilder.toString();

    }

}