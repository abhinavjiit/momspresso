package com.mycity4kids.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.mycity4kids.R;

public class Contactdetails extends Fragment implements View.OnClickListener {
    TextView editmail;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.contact_details_layout, container, false);
        editmail = (TextView) view.findViewById(R.id.edit_mailid);
        editmail.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {

    }
}
