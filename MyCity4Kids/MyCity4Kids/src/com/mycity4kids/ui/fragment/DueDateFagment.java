package com.mycity4kids.ui.fragment;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.TextView;

import com.mycity4kids.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by manish.soni on 08-07-2015.
 */
public class DueDateFagment extends android.app.DialogFragment implements View.OnClickListener {


    TextView next7, next30, pickDate, none;
    String date = "";
    String dateTemp = "";
    String edit = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.aa_due_date, container,
                false);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        Window window = getDialog().getWindow();
        window.setGravity(Gravity.BOTTOM | Gravity.CENTER_VERTICAL);

        Bundle extras = getArguments();
        if (extras != null) {
            edit = extras.getString("ifedit");
        }

        next7 = (TextView) rootView.findViewById(R.id.next_7);
        next30 = (TextView) rootView.findViewById(R.id.next_30);
        pickDate = (TextView) rootView.findViewById(R.id.pick_date);
        none = (TextView) rootView.findViewById(R.id.none);

        next7.setOnClickListener(this);
        next30.setOnClickListener(this);
        pickDate.setOnClickListener(this);
        none.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(View view) {

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("dd MMMM yyyy, EEEE");

        switch (view.getId()) {

            case R.id.next_7:

                calendar.add(Calendar.DAY_OF_YEAR, 7);

                if (edit.equalsIgnoreCase("edit")) {
//                    ((ActivityEditTask) getActivity()).setTaskTime(format.format(calendar.getTime()));
                } else {
//                    ((ActivityCreateTask) getActivity()).setTaskTime(format.format(calendar.getTime()));
                }

                getDialog().dismiss();

                break;

            case R.id.next_30:

                calendar.add(Calendar.DAY_OF_YEAR, 30);

                if (edit.equalsIgnoreCase("edit")) {
//                    ((ActivityEditTask) getActivity()).setTaskTime(format.format(calendar.getTime()));
                } else {
//                    ((ActivityCreateTask) getActivity()).setTaskTime(format.format(calendar.getTime()));
                }

                getDialog().dismiss();
                break;

            case R.id.pick_date:
                datePicket();
                break;

            case R.id.none:
                if (edit.equalsIgnoreCase("edit")) {
//                    ((ActivityEditTask) getActivity()).setTaskTime(format.format(calendar.getTime()));
                } else {
//                    ((ActivityCreateTask) getActivity()).setTaskTime(format.format(calendar.getTime()));
                }

                getDialog().dismiss();
                break;
        }
    }

    public void datePicket() {

        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

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

                        SimpleDateFormat format = new SimpleDateFormat("dd MMMM yyyy, EEEE");
                        Calendar tempCal = Calendar.getInstance();
//                        Date.setText(format.format(caltemp.getTime()));

                        dateTemp = format.format(tempCal.getTime());
                        date = format.format(caltemp.getTime());

                        long currentTs = 0;
                        long selectedTs = 0;

                        try {
                            currentTs = convertTimeStamp((String.valueOf(tempCal.get(Calendar.DAY_OF_MONTH) + " " + tempCal.get(Calendar.MONTH) + " " + tempCal.get(Calendar.YEAR)) + " 12:01 AM"));

                            selectedTs = convertTimeStamp((String.valueOf(caltemp.get(Calendar.DAY_OF_MONTH) + " " + caltemp.get(Calendar.MONTH) + " " + caltemp.get(Calendar.YEAR)) + " 09:00 AM"));

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        if (currentTs < selectedTs) {

                            if (edit.equalsIgnoreCase("edit")) {
//                                ((ActivityEditTask) getActivity()).setTaskTime(date);
                            } else {
//                                ((ActivityCreateTask) getActivity()).setTaskTime(date);
                            }
                        } else {
                            if (edit.equalsIgnoreCase("edit")) {
//                                ((ActivityEditTask) getActivity()).setTaskTime(dateTemp);
                            } else {
//                                ((ActivityCreateTask) getActivity()).setTaskTime(dateTemp);
                            }
                        }

//                        if (edit.equalsIgnoreCase("edit")) {
//                            ((ActivityEditTask) getActivity()).setTaskTime(date);
//                        } else {
//                            ((ActivityCreateTask) getActivity()).setTaskTime(date);
//                        }
                        getDialog().dismiss();

                    }
                }, mYear, mMonth, mDay);
        dpd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dpd.show();

    }

    public long convertTimeStamp(CharSequence date) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("dd MM yyyy hh:mm a");

        Date tempDate = formatter.parse((String) date);

        java.sql.Timestamp timestamp = new java.sql.Timestamp(tempDate.getTime());

        Log.d("TimeStamp", String.valueOf(timestamp.getTime()));

        long time_stamp = timestamp.getTime();
        return time_stamp;
    }

}