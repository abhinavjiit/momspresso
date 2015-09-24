package com.mycity4kids.ui.fragment;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.mycity4kids.R;
import com.mycity4kids.ui.activity.ActivityCreateAppointment;
import com.mycity4kids.ui.activity.ActivityCreateTask;
import com.mycity4kids.ui.activity.ActivityEditAppointment;
import com.mycity4kids.ui.activity.ActivityEditTask;

/**
 * Created by manish.soni on 30-06-2015.
 */
public class RepeatDialogFragment extends android.app.DialogFragment implements View.OnClickListener {


    TextView noRepeat, selectDay, daily, weekly, monthly, yearly, other;
    Boolean edit = false;
    String ifTask = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.aa_repeat_dialog, container,
                false);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        Window window = getDialog().getWindow();
        window.setGravity(Gravity.BOTTOM | Gravity.CENTER_VERTICAL);


        Bundle extras = getArguments();
        if (extras != null) {
            edit = extras.getBoolean("edit");
            ifTask = extras.getString("task");
        }

        noRepeat = (TextView) rootView.findViewById(R.id.no_repeat);
        noRepeat.setOnClickListener(this);
        selectDay = (TextView) rootView.findViewById(R.id.select_day);
        selectDay.setOnClickListener(this);
        daily = (TextView) rootView.findViewById(R.id.daily);
        daily.setOnClickListener(this);
        weekly = (TextView) rootView.findViewById(R.id.weekly);
        weekly.setOnClickListener(this);
        monthly = (TextView) rootView.findViewById(R.id.monthly);
        monthly.setOnClickListener(this);
        yearly = (TextView) rootView.findViewById(R.id.yearly);
        yearly.setOnClickListener(this);
        other = (TextView) rootView.findViewById(R.id.other);
        other.setOnClickListener(this);

        return rootView;
    }


    @Override
    public void onClick(View view) {

        Bundle args = new Bundle();
        args.putBoolean("edit", edit);
        args.putString("iftask", ifTask);

        switch (view.getId()) {

            case R.id.no_repeat:

                if (edit) {

                    if (ifTask.equalsIgnoreCase("task")) {
                        ((ActivityEditTask) getActivity()).setRepeat("", "No Repeat", "0");
                    } else {
                        ((ActivityEditAppointment) getActivity()).setRepeat("", "No Repeat", "0");
                    }


                } else {

                    if (ifTask.equalsIgnoreCase("task")) {
                        ((ActivityCreateTask) getActivity()).setRepeat("", "No Repeat", "0");
                    } else {
                        ((ActivityCreateAppointment) getActivity()).setRepeat("", "No Repeat", "0");
                    }

                }

                getDialog().dismiss();
                break;

            case R.id.select_day:

                DaysFragment daysFragment = new DaysFragment();


                daysFragment.setArguments(args);
                daysFragment.show(getFragmentManager(), "days");

                getDialog().dismiss();
                break;

            case R.id.daily:


                if (edit) {

                    if (ifTask.equalsIgnoreCase("task")) {
                        ((ActivityEditTask) getActivity()).setRepeat("", "Daily", "0");
                    } else {
                        ((ActivityEditAppointment) getActivity()).setRepeat("", "Daily", "0");
                    }


                } else {

                    if (ifTask.equalsIgnoreCase("task")) {
                        ((ActivityCreateTask) getActivity()).setRepeat("", "Daily", "0");
                    } else {
                        ((ActivityCreateAppointment) getActivity()).setRepeat("", "Daily", "0");
                    }

                }

                getDialog().dismiss();
                break;

            case R.id.weekly:

                if (edit) {

                    if (ifTask.equalsIgnoreCase("task")) {
                        ((ActivityEditTask) getActivity()).setRepeat("", "Weekly", "0");
                    } else {
                        ((ActivityEditAppointment) getActivity()).setRepeat("", "Weekly", "0");
                    }


                } else {

                    if (ifTask.equalsIgnoreCase("task")) {
                        ((ActivityCreateTask) getActivity()).setRepeat("", "Weekly", "0");
                    } else {
                        ((ActivityCreateAppointment) getActivity()).setRepeat("", "Weekly", "0");
                    }

                }


                getDialog().dismiss();
                break;

            case R.id.monthly:

                if (edit) {

                    if (ifTask.equalsIgnoreCase("task")) {
                        ((ActivityEditTask) getActivity()).setRepeat("", "Monthly", "0");
                    } else {
                        ((ActivityEditAppointment) getActivity()).setRepeat("", "Monthly", "0");
                    }


                } else {

                    if (ifTask.equalsIgnoreCase("task")) {
                        ((ActivityCreateTask) getActivity()).setRepeat("", "Monthly", "0");
                    } else {
                        ((ActivityCreateAppointment) getActivity()).setRepeat("", "Monthly", "0");
                    }

                }

                getDialog().dismiss();
                break;

            case R.id.yearly:

                if (edit) {

                    if (ifTask.equalsIgnoreCase("task")) {
                        ((ActivityEditTask) getActivity()).setRepeat("", "Yearly", "0");
                    } else {
                        ((ActivityEditAppointment) getActivity()).setRepeat("", "Yearly", "0");
                    }


                } else {

                    if (ifTask.equalsIgnoreCase("task")) {
                        ((ActivityCreateTask) getActivity()).setRepeat("", "Yearly", "0");
                    } else {
                        ((ActivityCreateAppointment) getActivity()).setRepeat("", "Yearly", "0");
                    }

                }

                getDialog().dismiss();
                break;

            case R.id.other:

                OtherFragment otherFragment = new OtherFragment();

                otherFragment.setArguments(args);
                otherFragment.show(getFragmentManager(), "other");

                getDialog().dismiss();
                break;

        }
    }
}
