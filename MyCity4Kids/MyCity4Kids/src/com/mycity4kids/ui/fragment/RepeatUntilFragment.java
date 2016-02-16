package com.mycity4kids.ui.fragment;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.TextView;

import com.mycity4kids.R;
import com.mycity4kids.ui.activity.ActivityCreateAppointment;
import com.mycity4kids.ui.activity.ActivityCreateTask;
import com.mycity4kids.ui.activity.ActivityEditAppointment;
import com.mycity4kids.ui.activity.ActivityEditTask;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by manish.soni on 30-06-2015.
 */
public class RepeatUntilFragment extends android.app.DialogFragment implements View.OnClickListener {

    String tempDate = "";
    TextView forever, pickDate;
    static Boolean edit = true;
    String iftask = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.aa_repeat_until, container,
                false);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        Window window = getDialog().getWindow();
        window.setGravity(Gravity.BOTTOM | Gravity.CENTER_VERTICAL);

        Bundle extras = getArguments();
        if (extras != null) {
            edit = extras.getBoolean("edit");
            iftask = extras.getString("task");
        }

        forever = (TextView) rootView.findViewById(R.id.forever);
        forever.setOnClickListener(this);
        pickDate = (TextView) rootView.findViewById(R.id.pick_date);
        pickDate.setOnClickListener(this);

        return rootView;
    }


    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.forever:

                if (edit) {

                    if (iftask.equalsIgnoreCase("task")) {
                        ((ActivityEditTask) getActivity()).setRepeatUntil("Forever", "");
                    } else {
                        ((ActivityEditAppointment) getActivity()).setRepeatUntil("Forever", "");
                    }


                } else {

                    if (iftask.equalsIgnoreCase("task")) {
                        ((ActivityCreateTask) getActivity()).setRepeatUntil("Forever", "");
                    } else {
                        ((ActivityCreateAppointment) getActivity()).setRepeatUntil("Forever", "");
                    }


                }

                getDialog().dismiss();
                break;

            case R.id.pick_date:

                datePicket();
                //showDatePickerDialog();

                break;

        }
    }

    /// date picker management
//
//    public void showDatePickerDialog() {
//        DialogFragment newFragment = new DatePickerFragment();
//        if (edit)
//            newFragment.show(((ActivityEditAppointment) getActivity()).getSupportFragmentManager(), "datePicker");
//        else
//
//            newFragment.show(((ActivityCreateAppointment) getActivity()).getSupportFragmentManager(), "datePicker");
//    }

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
        if (Integer.parseInt(currentime) > Integer.parseInt(convertDate(time)))
            result = false;

        return result;
    }


//    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
//
//
//        boolean cancel;
//
//        final Calendar c = Calendar.getInstance();
//        int curent_year = c.get(Calendar.YEAR);
//        int current_month = c.get(Calendar.MONTH);
//        int current_day = c.get(Calendar.DAY_OF_MONTH);
//
//        @SuppressLint("NewApi")
//        @Override
//        public Dialog onCreateDialog(Bundle savedInstanceState) {
//            // Use the current date as the default date in the picker
//
//            DatePickerDialog dlg = new DatePickerDialog(getActivity(), this, curent_year, current_month, current_day);
//            dlg.getDatePicker().setMaxDate(c.getTimeInMillis());
//            return dlg;
//
//        }
//
//        public void onDateSet(DatePicker view, int year, int month, int day) {
//
//            String sel_date = "" + day + "-" + (month + 1) + "-" + year;
//            if (chkTime(sel_date)) {
//                if (edit) {
//                    ((ActivityEditAppointment) getActivity()).setRepeatUntil(sel_date, "");
//
//                } else {
//                    ((ActivityCreateAppointment) getActivity()).setRepeatUntil(sel_date, "");
//
//                }
//            } else {
//
//                sel_date = "" + current_day + "-" + (current_month + 1) + "-" + curent_year;
//
//                if (edit) {
//                    ((ActivityEditAppointment) getActivity()).setRepeatUntil(sel_date, "");
//
//                } else {
//                    ((ActivityCreateAppointment) getActivity()).setRepeatUntil(sel_date, "");
//
//                }
//
//            }
//            getDialog().dismiss();
//
//        }
//    }

    public void datePicket() {

        final Calendar c = Calendar.getInstance();
        final int mYear = c.get(Calendar.YEAR);
        final int mMonth = c.get(Calendar.MONTH);
        final int mDay = c.get(Calendar.DAY_OF_MONTH);


        // Launch Date Picker Dialog
        DatePickerDialog dpd = new DatePickerDialog(getActivity(),android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        // Display Selected date in textbox

                        Calendar caltemp = Calendar.getInstance();
                        caltemp.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        caltemp.set(Calendar.MONTH, monthOfYear);
                        caltemp.set(Calendar.YEAR, year);

                        SimpleDateFormat format = new SimpleDateFormat("dd MMMM yyyy");

                        String sel_date = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;

                        Calendar calendar1 = Calendar.getInstance();
                        calendar1.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        calendar1.set(Calendar.MONTH, monthOfYear);
                        calendar1.set(Calendar.YEAR, year);

                        // String sel_date = "" + day + "-" + (month + 1) + "-" + year;
                        if (chkTime(sel_date)) {
                            if (edit) {
//                                ((ActivityEditAppointment) getActivity()).setRepeatUntil(sel_date, "");\
                                if (iftask.equalsIgnoreCase("task")) {
                                    ((ActivityEditTask) getActivity()).setRepeatUntil(format.format(calendar1.getTime()), "");
                                } else {
                                    ((ActivityEditAppointment) getActivity()).setRepeatUntil(format.format(calendar1.getTime()), "");
                                }


                            } else {
//                                ((ActivityCreateAppointment) getActivity()).setRepeatUntil(sel_date, "");

                                if (iftask.equalsIgnoreCase("task")) {
                                    ((ActivityCreateTask) getActivity()).setRepeatUntil(format.format(calendar1.getTime()), "");

                                } else {
                                    ((ActivityCreateAppointment) getActivity()).setRepeatUntil(format.format(calendar1.getTime()), "");

                                }

                            }
                        } else {

                            sel_date = "" + mDay + "-" + (mMonth + 1) + "-" + mYear;

                            Calendar calendar = Calendar.getInstance();
                            calendar.set(Calendar.DAY_OF_MONTH, mDay);
                            calendar.set(Calendar.MONTH, mMonth);
                            calendar.set(Calendar.YEAR, mYear);


                            if (edit) {
//                                ((ActivityEditAppointment) getActivity()).setRepeatUntil(sel_date, "");\
                                if (iftask.equalsIgnoreCase("task")) {
                                    ((ActivityEditTask) getActivity()).setRepeatUntil(format.format(calendar1.getTime()), "");
                                } else {
                                    ((ActivityEditAppointment) getActivity()).setRepeatUntil(format.format(calendar1.getTime()), "");
                                }


                            } else {
//                                ((ActivityCreateAppointment) getActivity()).setRepeatUntil(sel_date, "");

                                if (iftask.equalsIgnoreCase("task")) {
                                    ((ActivityCreateTask) getActivity()).setRepeatUntil(format.format(calendar1.getTime()), "");

                                } else {
                                    ((ActivityCreateAppointment) getActivity()).setRepeatUntil(format.format(calendar1.getTime()), "");

                                }
                            }
                        }
                        getDialog().dismiss();

//                        if (edit) {
//                            ((ActivityEditAppointment) getActivity()).setRepeatUntil(t, "");
//                            getDialog().dismiss();
//                        } else {
//                            ((ActivityCreateAppointment) getActivity()).setRepeatUntil(t, "");
//                            getDialog().dismiss();
//                        }

                    }
                }, mYear, mMonth, mDay);

        dpd.getDatePicker().setMinDate(c.getTimeInMillis());
        dpd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dpd.show();

    }
}
