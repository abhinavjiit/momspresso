package com.mycity4kids.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import com.kelltontech.utils.ToastUtils;
import com.mycity4kids.R;
import com.mycity4kids.ui.activity.ActivityShowAppointment;
import com.mycity4kids.ui.activity.ActivityShowTask;

/**
 * Created by user on 08-06-2015.
 */
public class NotesDialogFragment extends android.app.DialogFragment {


    TextView cancel, done;
    EditText notes;
    String ifTask;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.aa_addnotes_fragment, container,
                false);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        cancel = (TextView) rootView.findViewById(R.id.cancel);
        done = (TextView) rootView.findViewById(R.id.done);
        notes = (EditText) rootView.findViewById(R.id.addnotes);

        Bundle extras = getArguments();
        if (extras != null) {
            ifTask = extras.getString("ifTask");
        }

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (notes.getText().toString().trim().equals("")) {
                    ToastUtils.showToast(getActivity(), "Please enter text");
                } else {

                    if (ifTask.equalsIgnoreCase("task")) {
                        ((ActivityShowTask) getActivity()).setNotes(notes.getText().toString().trim());
                        getDialog().dismiss();
                    } else {
                        ((ActivityShowAppointment) getActivity()).setNotes(notes.getText().toString().trim());
                        getDialog().dismiss();
                    }

                }

            }
        });

        return rootView;
    }
}
