package com.mycity4kids.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import com.kelltontech.utils.StringUtils;
import com.kelltontech.utils.ToastUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.dbtable.TableTaskList;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.ui.activity.ActivityCreateTask;
import com.mycity4kids.ui.activity.ActivityEditTask;
import com.mycity4kids.ui.activity.DashboardActivity;

/**
 * Created by manish.soni on 09-07-2015.
 */
public class AddTaskListPopUp extends android.app.DialogFragment implements View.OnClickListener {

    TextView addList, cancel;
    EditText listName;
    String args = "";
    boolean editTaskList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.aa_add_list_popup, container,
                false);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        Bundle extras = getArguments();
        if (extras != null) {
            args = extras.getString("from");
            editTaskList = extras.getBoolean("editList");
        }


        addList = (TextView) rootView.findViewById(R.id.add);
        cancel = (TextView) rootView.findViewById(R.id.cancel);
        listName = (EditText) rootView.findViewById(R.id.add_list);
        addList.setOnClickListener(this);
        cancel.setOnClickListener(this);
        addList.setOnClickListener(this);

        if (editTaskList) {

            addList.setText("SAVE");
            listName.setText(new TableTaskList(BaseApplication.getInstance()).getListName(SharedPrefUtils.getTaskListID(getActivity())));
        }


        return rootView;
    }


    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.add:

                if (StringUtils.isNullOrEmpty(listName.getText().toString())) {

                    ToastUtils.showToast(getActivity(),"Please enter task list name");

                } else {

                    if (args.equalsIgnoreCase("dashboard")) {
                        ((DashboardActivity) getActivity()).addTaskList(listName.getText().toString(), editTaskList);
                    } else if (args.equalsIgnoreCase("createTask")) {
                        ((ActivityCreateTask) getActivity()).addTaskList(listName.getText().toString());
                    } else if (args.equalsIgnoreCase("editTask")) {
                        ((ActivityEditTask) getActivity()).addTaskList(listName.getText().toString());
                    }

                    getDialog().dismiss();
                }
                break;
            case R.id.cancel:

                getDialog().dismiss();

                break;

        }
    }

}