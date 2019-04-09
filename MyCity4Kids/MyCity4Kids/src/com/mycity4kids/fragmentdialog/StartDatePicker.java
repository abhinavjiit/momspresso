package com.mycity4kids.fragmentdialog;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import com.mycity4kids.interfaces.IGetDate;

import java.util.Calendar;

public class StartDatePicker extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    private IGetDate iGetDate;
    boolean fired = false;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog dlg = new DatePickerDialog(getActivity(), android.R.style.Theme_Holo_Light_Dialog_NoActionBar, this, year, month, day);
        dlg.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return dlg;
    }

    public void setDateAction(IGetDate iGetDate) {
        this.iGetDate = iGetDate;
    }


    public void onDateSet(DatePicker view, int year, int month, int day) {

        if (fired == true) {
            return;
        } else {

            fired = true;
            String date = String.valueOf(day) + "-" + String.valueOf(month + 1) + "-" + String.valueOf(year);
            iGetDate.getDateValue(date);
        }


    }
}
