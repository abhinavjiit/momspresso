package com.mycity4kids.widget;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.widget.DatePicker;

import java.util.Calendar;

public class PageViewsDatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    boolean cancel;
    String type = "";
    final Calendar c = Calendar.getInstance();
    int curent_year = c.get(Calendar.YEAR);
    int current_month = c.get(Calendar.MONTH);
    int current_day = c.get(Calendar.DAY_OF_MONTH);

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @SuppressLint("NewApi")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        DatePickerDialog dlg = new DatePickerDialog(getActivity(), android.R.style.Theme_Holo_Light_Dialog_NoActionBar, this, curent_year, current_month, current_day);
        dlg.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        type = getArguments().getString("type", "");
        dlg.getDatePicker().setMaxDate(c.getTimeInMillis());
        return dlg;

    }

    public void onDateSet(DatePicker view, int year, int month, int day) {

        String sel_date = "" + day + "-" + (month + 1) + "-" + year;

        IDateSelection iDateSelection = (IDateSelection) getParentFragment();
        iDateSelection.onDateSelection(type, sel_date, year, month, day);

    }

    public interface IDateSelection {
        void onDateSelection(String dateType, String selectedDate, int year, int month, int day);
    }
}