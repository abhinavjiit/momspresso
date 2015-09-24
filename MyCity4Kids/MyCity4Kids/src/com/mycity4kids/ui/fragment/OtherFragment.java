package com.mycity4kids.ui.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.ui.activity.ActivityCreateAppointment;
import com.mycity4kids.ui.activity.ActivityCreateTask;
import com.mycity4kids.ui.activity.ActivityEditAppointment;
import com.mycity4kids.ui.activity.ActivityEditTask;

/**
 * Created by manish.soni on 30-06-2015.
 */
public class OtherFragment extends android.app.DialogFragment implements View.OnClickListener {

    NumberPicker stringDWM, valuesDays, valuesWeeks, valuesMonths;
    TextView cancel, ok;
    String[] dayList;
    String[] weekList;
    String[] monthList;
    Boolean edit = false;
    int max;
    String[] mainlist;
    String ifTask = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.aa_other, container, false);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        valuesDays = (NumberPicker) rootView.findViewById(R.id.values_days);
        valuesWeeks = (NumberPicker) rootView.findViewById(R.id.values_weeks);
        valuesMonths = (NumberPicker) rootView.findViewById(R.id.values_months);
        stringDWM = (NumberPicker) rootView.findViewById(R.id.stringDWM);
        cancel = (TextView) rootView.findViewById(R.id.cancel);
        ok = (TextView) rootView.findViewById(R.id.ok);

        cancel.setTextColor(Color.parseColor("#3C69F2"));
        ok.setTextColor(Color.parseColor("#3C69F2"));

        Bundle extras = getArguments();
        if (extras != null) {
            edit = extras.getBoolean("edit");
            ifTask = extras.getString("iftask");
        }

        cancel.setOnClickListener(this);
        ok.setOnClickListener(this);


        dayList = new String[31];
        for (int i = 0; i < dayList.length; i++) {
            dayList[i] = Integer.toString(i + 1);
        }

        weekList = new String[52];
        for (int i = 0; i < weekList.length; i++) {
            weekList[i] = Integer.toString(i + 1);
        }

        monthList = new String[12];
        for (int i = 0; i < monthList.length; i++) {
            monthList[i] = Integer.toString(i + 1);
        }

        valuesMonths.setVisibility(View.GONE);
        valuesDays.setVisibility(View.GONE);


        String[] dwm_values = new String[3];
        dwm_values[0] = "Days";
        dwm_values[1] = "Weeks";
        dwm_values[2] = "Months";
        stringDWM.setMaxValue(dwm_values.length - 1);
        stringDWM.setMinValue(0);
        stringDWM.setDisplayedValues(dwm_values);
        stringDWM.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        stringDWM.setValue(getMiddlePosition(dwm_values));
        stringDWM.setWrapSelectorWheel(false);


        valuesDays.setMaxValue(weekList.length - 1);
        valuesDays.setMinValue(0);
        valuesDays.setValue(getMiddlePosition(weekList));
        valuesDays.setDisplayedValues(weekList);
        valuesDays.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        valuesDays.setWrapSelectorWheel(false);


        valuesDays.setMaxValue(dayList.length - 1);
        valuesDays.setMinValue(0);
        valuesDays.setDisplayedValues(dayList);
        valuesDays.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        valuesDays.setValue(getMiddlePosition(dayList));
        valuesDays.setWrapSelectorWheel(false);

        valuesWeeks.setMaxValue(weekList.length - 1);
        valuesWeeks.setMinValue(0);
        valuesWeeks.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        valuesWeeks.setValue(getMiddlePosition(weekList));
        valuesWeeks.setDisplayedValues(weekList);
        valuesWeeks.setWrapSelectorWheel(false);

        valuesMonths.setMaxValue(monthList.length - 1);
        valuesMonths.setMinValue(0);
        valuesMonths.setDisplayedValues(monthList);
        valuesMonths.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        valuesMonths.setValue(getMiddlePosition(monthList));
        valuesMonths.setWrapSelectorWheel(false);

        stringDWM.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {

                String selectedValue = "" + i1;

                if (selectedValue.equals("0")) {

                    valuesDays.setVisibility(View.VISIBLE);
                    valuesWeeks.setVisibility(View.GONE);
                    valuesMonths.setVisibility(View.GONE);


                } else if (selectedValue.equals("1")) {

                    valuesDays.setVisibility(View.GONE);
                    valuesWeeks.setVisibility(View.VISIBLE);
                    valuesMonths.setVisibility(View.GONE);


                } else if (selectedValue.equals("2")) {

                    valuesDays.setVisibility(View.GONE);
                    valuesWeeks.setVisibility(View.GONE);
                    valuesMonths.setVisibility(View.VISIBLE);

                }
            }
        });

        return rootView;
    }


    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.cancel:

                getDialog().dismiss();
                break;

            case R.id.ok:
                if (edit) {

                    if (!StringUtils.isNullOrEmpty(ifTask) && ifTask.equalsIgnoreCase("task")) {
                        if ((stringDWM.getValue() + "").equalsIgnoreCase("0")) {
                            ((ActivityEditTask) getActivity()).setRepeat("Other", stringDWM.getValue() + "", valuesDays.getValue() + "");
                        } else if ((stringDWM.getValue() + "").equalsIgnoreCase("1")) {
                            ((ActivityEditTask) getActivity()).setRepeat("Other", stringDWM.getValue() + "", valuesWeeks.getValue() + "");
                        } else if ((stringDWM.getValue() + "").equalsIgnoreCase("2")) {
                            ((ActivityEditTask) getActivity()).setRepeat("Other", stringDWM.getValue() + "", valuesMonths.getValue() + "");
                        }
                    } else {
                        if ((stringDWM.getValue() + "").equalsIgnoreCase("0")) {
                            ((ActivityEditAppointment) getActivity()).setRepeat("Other", stringDWM.getValue() + "", valuesDays.getValue() + "");
                        } else if ((stringDWM.getValue() + "").equalsIgnoreCase("1")) {
                            ((ActivityEditAppointment) getActivity()).setRepeat("Other", stringDWM.getValue() + "", valuesWeeks.getValue() + "");
                        } else if ((stringDWM.getValue() + "").equalsIgnoreCase("2")) {
                            ((ActivityEditAppointment) getActivity()).setRepeat("Other", stringDWM.getValue() + "", valuesMonths.getValue() + "");
                        }
                    }


                } else {

                    if (!StringUtils.isNullOrEmpty(ifTask) && ifTask.equalsIgnoreCase("task")) {
                        if ((stringDWM.getValue() + "").equalsIgnoreCase("0")) {
                            ((ActivityCreateTask) getActivity()).setRepeat("Other", stringDWM.getValue() + "", valuesDays.getValue() + "");
                        } else if ((stringDWM.getValue() + "").equalsIgnoreCase("1")) {
                            ((ActivityCreateTask) getActivity()).setRepeat("Other", stringDWM.getValue() + "", valuesWeeks.getValue() + "");
                        } else if ((stringDWM.getValue() + "").equalsIgnoreCase("2")) {
                            ((ActivityCreateTask) getActivity()).setRepeat("Other", stringDWM.getValue() + "", valuesMonths.getValue() + "");
                        }
                    } else {
                        if ((stringDWM.getValue() + "").equalsIgnoreCase("0")) {
                            ((ActivityCreateAppointment) getActivity()).setRepeat("Other", stringDWM.getValue() + "", valuesDays.getValue() + "");
                        } else if ((stringDWM.getValue() + "").equalsIgnoreCase("1")) {
                            ((ActivityCreateAppointment) getActivity()).setRepeat("Other", stringDWM.getValue() + "", valuesWeeks.getValue() + "");
                        } else if ((stringDWM.getValue() + "").equalsIgnoreCase("2")) {
                            ((ActivityCreateAppointment) getActivity()).setRepeat("Other", stringDWM.getValue() + "", valuesMonths.getValue() + "");
                        }
                    }

                }
                getDialog().dismiss();
                break;
        }
    }


    public int getMiddlePosition(String[] list) {
        int position = list.length / 2;
        return position;
    }

}

