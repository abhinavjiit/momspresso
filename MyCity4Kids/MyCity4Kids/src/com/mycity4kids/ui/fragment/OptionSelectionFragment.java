package com.mycity4kids.ui.fragment;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.widget.AppCompatRadioButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import com.mycity4kids.R;

import java.util.Objects;

public class OptionSelectionFragment extends DialogFragment implements View.OnClickListener {
    private LinearLayout linearLayout;

    @Override
    public void onClick(View view) {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_category_challenge_layout, container, false);
        Objects.requireNonNull(getDialog().getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        RadioGroup reportReasonRadioGroup = (RadioGroup) view.findViewById(R.id.reportReasonRadioGroup);
        final AppCompatRadioButton reason1RadioButton = (AppCompatRadioButton) view.findViewById(R.id.reason1RadioButton);
        final AppCompatRadioButton reason2RadioButton = (AppCompatRadioButton) view.findViewById(R.id.reason2RadioButton);
        final AppCompatRadioButton reason3RadioButton = (AppCompatRadioButton) view.findViewById(R.id.reason3RadioButton);
        final AppCompatRadioButton reason4RadioButton = (AppCompatRadioButton) view.findViewById(R.id.reason4RadioButton);
        final AppCompatRadioButton reason5RadioButton = (AppCompatRadioButton) view.findViewById(R.id.reason5RadioButton);
        return view;

    }
}
