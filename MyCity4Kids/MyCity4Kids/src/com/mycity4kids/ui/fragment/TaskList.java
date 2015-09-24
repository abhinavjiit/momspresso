package com.mycity4kids.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.mycity4kids.R;

/**
 * Created by manish.soni on 08-07-2015.
 */
public class TaskList extends android.app.DialogFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.aa_attendees_fragment, container,
                false);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);


        Bundle extras = getArguments();
        if (extras != null) {
//            chklist = extras.getIntegerArrayList("chkValues");
//            all = extras.getBoolean("All");
//            edit = extras.getBoolean("edit");
//            iftask = extras.getString("task");

        }

        return rootView;
    }

}