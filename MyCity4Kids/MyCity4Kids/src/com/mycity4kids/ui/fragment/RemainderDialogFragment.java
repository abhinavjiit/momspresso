package com.mycity4kids.ui.fragment;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.kelltontech.ui.BaseActivity;
import com.mycity4kids.R;
import com.mycity4kids.newmodels.AttendeeModel;
import com.mycity4kids.ui.activity.ActivityCreateAppointment;
import com.mycity4kids.ui.activity.ActivityCreateTask;
import com.mycity4kids.ui.activity.ActivityEditAppointment;
import com.mycity4kids.ui.activity.ActivityEditTask;
import com.mycity4kids.ui.adapter.AttendeeCustomAdapter;

import java.util.ArrayList;

/**
 * Created by user on 08-06-2015.
 */
public class RemainderDialogFragment extends android.app.DialogFragment implements OnClickListener {

    ArrayList<AttendeeModel> data;
    ListView listView;
    AttendeeCustomAdapter adapter;
    TextView zeroMinute, fiveMinute, tenMinute, fifteenMinute, thirtyMinute, fortyfiveMinute, oneHr, twoHr, twntyfour, fortyEight;
    private boolean edit;
    String ifTask = "";
    private boolean is_recurring, todayDate;
    private long startTime;
    private long differenceMin = 0;
    private LinearLayout rootLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.remainder_fragment, container,
                false);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        Window window = getDialog().getWindow();
        window.setGravity(Gravity.BOTTOM | Gravity.CENTER_VERTICAL);

        Bundle extras = getArguments();
        if (extras != null) {
            edit = extras.getBoolean("edit");
            ifTask = extras.getString("task");
            is_recurring = extras.getBoolean("is_recurring");
            todayDate = extras.getBoolean("todayDate");
            startTime = extras.getLong("time");
        }

        rootLayout = (LinearLayout) rootView.findViewById(R.id.root);
        zeroMinute = (TextView) rootView.findViewById(R.id.zero_min);
        zeroMinute.setOnClickListener(this);
        fiveMinute = (TextView) rootView.findViewById(R.id.five_min);
        fiveMinute.setOnClickListener(this);
        tenMinute = (TextView) rootView.findViewById(R.id.ten_min);
        tenMinute.setOnClickListener(this);
        fifteenMinute = (TextView) rootView.findViewById(R.id.fifteen_min);
        fifteenMinute.setOnClickListener(this);
        thirtyMinute = (TextView) rootView.findViewById(R.id.thirty_min);
        thirtyMinute.setOnClickListener(this);
        fortyfiveMinute = (TextView) rootView.findViewById(R.id.fortyfive_min);
        fortyfiveMinute.setOnClickListener(this);
        oneHr = (TextView) rootView.findViewById(R.id.one_hr);
        oneHr.setOnClickListener(this);
       // twoHr = (TextView) rootView.findViewById(R.id.two_hr);
        //twoHr.setOnClickListener(this);

        twntyfour = (TextView) rootView.findViewById(R.id.twentyfour_hr);
        twntyfour.setOnClickListener(this);

        fortyEight = (TextView) rootView.findViewById(R.id.fortyeight_hr);
        fortyEight.setOnClickListener(this);


        if (!is_recurring && todayDate) {
            long currenttimeSatmp = System.currentTimeMillis();

            differenceMin = ((startTime - currenttimeSatmp) / (1000 * 60)) + 1;

            if (differenceMin < 0)
                differenceMin = -differenceMin;

            System.out.println("Minu diff " + differenceMin);

        }

        return rootView;
    }


    public boolean chkMinutesValidation(int minute) {
        boolean result = true;
        if (differenceMin != 0) {
            if (minute <= differenceMin) {
                result = true;
            } else {
                result = false;
            }

        }


        return result;
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.zero_min:

                if (chkMinutesValidation(0)) {
                    if (edit)

                        if (ifTask.equalsIgnoreCase("task")) {
                            ((ActivityEditTask) getActivity()).setRemainder("0 Minute", "0");
                        } else {
                            ((ActivityEditAppointment) getActivity()).setRemainder("0 Minute", "0");
                        }

                    else {

                        if (ifTask.equalsIgnoreCase("task")) {
                            ((ActivityCreateTask) getActivity()).setRemainder("0 Minute", "0");
                        } else {
                            ((ActivityCreateAppointment) getActivity()).setRemainder("0 Minute", "0");
                        }
                    }

                    getDialog().dismiss();
                } else {
                    ((BaseActivity) getActivity()).showSnackbar(rootLayout, getString(R.string.reminder_selected));
                }

                break;

            case R.id.five_min:

                if (chkMinutesValidation(5)) {
                    if (edit)

                        if (ifTask.equalsIgnoreCase("task")) {
                            ((ActivityEditTask) getActivity()).setRemainder("5 Minutes", "5");
                        } else {
                            ((ActivityEditAppointment) getActivity()).setRemainder("5 Minutes", "5");
                        }

                    else {

                        if (ifTask.equalsIgnoreCase("task")) {
                            ((ActivityCreateTask) getActivity()).setRemainder("5 Minutes", "5");
                        } else {
                            ((ActivityCreateAppointment) getActivity()).setRemainder("5 Minutes", "5");
                        }
                    }


                    getDialog().dismiss();
                } else {
                    ((BaseActivity) getActivity()).showSnackbar(rootLayout, getString(R.string.reminder_selected));
                }


                break;

            case R.id.ten_min:

                if (chkMinutesValidation(10)) {
                    if (edit)

                        if (ifTask.equalsIgnoreCase("task")) {
                            ((ActivityEditTask) getActivity()).setRemainder("10 Minutes", "10");
                        } else {
                            ((ActivityEditAppointment) getActivity()).setRemainder("10 Minutes", "10");
                        }

                    else {

                        if (ifTask.equalsIgnoreCase("task")) {
                            ((ActivityCreateTask) getActivity()).setRemainder("10 Minutes", "10");
                        } else {
                            ((ActivityCreateAppointment) getActivity()).setRemainder("10 Minutes", "10");
                        }
                    }

                    getDialog().dismiss();
                } else {
                    ((BaseActivity) getActivity()).showSnackbar(rootLayout, getString(R.string.reminder_selected));
                }


                break;

            case R.id.fifteen_min:

                if (chkMinutesValidation(15)) {
                    if (edit)

                        if (ifTask.equalsIgnoreCase("task")) {
                            ((ActivityEditTask) getActivity()).setRemainder("15 Minutes", "15");
                        } else {
                            ((ActivityEditAppointment) getActivity()).setRemainder("15 Minutes", "15");
                        }

                    else {

                        if (ifTask.equalsIgnoreCase("task")) {
                            ((ActivityCreateTask) getActivity()).setRemainder("15 Minutes", "15");
                        } else {
                            ((ActivityCreateAppointment) getActivity()).setRemainder("15 Minutes", "15");
                        }
                    }

                    getDialog().dismiss();
                } else {
                    ((BaseActivity) getActivity()).showSnackbar(rootLayout, getString(R.string.reminder_selected));
                }


                break;

            case R.id.thirty_min:

                if (chkMinutesValidation(30)) {
                    if (edit)

                        if (ifTask.equalsIgnoreCase("task")) {
                            ((ActivityEditTask) getActivity()).setRemainder("30 Minutes", "30");
                        } else {
                            ((ActivityEditAppointment) getActivity()).setRemainder("30 Minutes", "30");
                        }

                    else {

                        if (ifTask.equalsIgnoreCase("task")) {
                            ((ActivityCreateTask) getActivity()).setRemainder("30 Minutes", "30");
                        } else {
                            ((ActivityCreateAppointment) getActivity()).setRemainder("30 Minutes", "30");
                        }
                    }
                    getDialog().dismiss();
                } else {
                    ((BaseActivity) getActivity()).showSnackbar(rootLayout, getString(R.string.reminder_selected));
                }


                break;

            case R.id.fortyfive_min:

                if (chkMinutesValidation(45)) {

                    if (edit)

                        if (ifTask.equalsIgnoreCase("task")) {
                            ((ActivityEditTask) getActivity()).setRemainder("45 Minutes", "45");
                        } else {
                            ((ActivityEditAppointment) getActivity()).setRemainder("45 Minutes", "45");
                        }

                    else {

                        if (ifTask.equalsIgnoreCase("task")) {
                            ((ActivityCreateTask) getActivity()).setRemainder("45 Minutes", "45");
                        } else {
                            ((ActivityCreateAppointment) getActivity()).setRemainder("45 Minutes", "45");
                        }
                    }

                    getDialog().dismiss();
                } else {
                    ((BaseActivity) getActivity()).showSnackbar(rootLayout, getString(R.string.reminder_selected));
                }


                break;

            case R.id.one_hr:

                if (chkMinutesValidation(60)) {

                    if (edit)

                        if (ifTask.equalsIgnoreCase("task")) {
                            ((ActivityEditTask) getActivity()).setRemainder("1 Hour", "60");
                        } else {
                            ((ActivityEditAppointment) getActivity()).setRemainder("1 Hour", "60");
                        }

                    else {

                        if (ifTask.equalsIgnoreCase("task")) {
                            ((ActivityCreateTask) getActivity()).setRemainder("1 Hour", "60");
                        } else {
                            ((ActivityCreateAppointment) getActivity()).setRemainder("1 Hour", "60");
                        }
                    }

                    getDialog().dismiss();
                } else {
                    ((BaseActivity) getActivity()).showSnackbar(rootLayout, getString(R.string.reminder_selected));
                }


                break;

            case R.id.twentyfour_hr:
                if (chkMinutesValidation(1440)) {
                    if (edit)

                        if (ifTask.equalsIgnoreCase("task")) {
                            ((ActivityEditTask) getActivity()).setRemainder("24 Hours", "1440");
                        } else {
                            ((ActivityEditAppointment) getActivity()).setRemainder("24 Hours", "1440");
                        }

                    else {

                        if (ifTask.equalsIgnoreCase("task")) {
                            ((ActivityCreateTask) getActivity()).setRemainder("24 Hours", "1440");
                        } else {
                            ((ActivityCreateAppointment) getActivity()).setRemainder("24 Hours", "1440");
                        }
                    }

                    getDialog().dismiss();
                } else {
                    ((BaseActivity) getActivity()).showSnackbar(rootLayout, getString(R.string.reminder_selected));
                }


                break;

            case R.id.fortyeight_hr:
                if (chkMinutesValidation(2880)) {
                    if (edit)

                        if (ifTask.equalsIgnoreCase("task")) {
                            ((ActivityEditTask) getActivity()).setRemainder("48 Hours", "2880");
                        } else {
                            ((ActivityEditAppointment) getActivity()).setRemainder("48 Hours", "2880");
                        }

                    else {

                        if (ifTask.equalsIgnoreCase("task")) {
                            ((ActivityCreateTask) getActivity()).setRemainder("48 Hours", "2880");
                        } else {
                            ((ActivityCreateAppointment) getActivity()).setRemainder("48 Hours", "2880");
                        }
                    }

                    getDialog().dismiss();
                } else {
                    ((BaseActivity) getActivity()).showSnackbar(rootLayout, getString(R.string.reminder_selected));
                }


                break;

        }

    }
}