package com.mycity4kids.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.mycity4kids.R;
import com.mycity4kids.listener.OnExternalCalenderTapped;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by manish.soni on 31-07-2015.
 */
public class ImportCalDialogFragment extends android.app.DialogFragment implements View.OnClickListener {

    Calendar calendar;
    SimpleDateFormat mFormat, mFormat1;
    private OnExternalCalenderTapped onExternalCalenderTapped;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.aa_impot_calendar, container, false);

        view.findViewById(R.id.fb_events).setOnClickListener(this);
        view.findViewById(R.id.google_events).setOnClickListener(this);
        view.findViewById(R.id.not_now).setOnClickListener(this);

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        calendar = Calendar.getInstance();
        mFormat = new SimpleDateFormat("d");
        mFormat1 = new SimpleDateFormat("EEEE");

        ((TextView) view.findViewById(R.id.day)).setText(mFormat1.format(calendar.getTime()));
        ((TextView) view.findViewById(R.id.date)).setText(mFormat.format(calendar.getTime()));

        return view;
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.fb_events:
                if (onExternalCalenderTapped != null) {
                    onExternalCalenderTapped.onExternalCalenderButtonTapped(true);
                }
                getDialog().dismiss();
                break;
            case R.id.google_events:
                if (onExternalCalenderTapped != null) {
                    onExternalCalenderTapped.onExternalCalenderButtonTapped(false);
                }
                getDialog().dismiss();

                break;
            case R.id.not_now:

                getDialog().dismiss();

                break;
        }
    }

    public void setListener(OnExternalCalenderTapped onExternalCalenderTapped) {
        this.onExternalCalenderTapped = onExternalCalenderTapped;
    }
}
