package com.mycity4kids.ui.fragment;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.TextView;

import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.dbtable.TableTaskList;
import com.mycity4kids.newmodels.TaskListModel;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.ui.activity.ActivityCreateTask;
import com.mycity4kids.ui.activity.ActivityEditTask;
import com.mycity4kids.ui.adapter.UserTaskListAdapter;
import com.mycity4kids.widget.CustomListView;

import java.util.ArrayList;

/**
 * Created by manish.soni on 09-07-2015.
 */
public class FragmentTaskList extends android.app.DialogFragment implements View.OnClickListener {

    TextView addList;
    UserTaskListAdapter userTaskListAdapter;
    CustomListView taskList;
    String edit = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.aa_task_list, container,
                false);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        Window window = getDialog().getWindow();
        window.setGravity(Gravity.BOTTOM | Gravity.CENTER_VERTICAL);

        addList = (TextView) rootView.findViewById(R.id.dialog_addlist);
        taskList = (CustomListView) rootView.findViewById(R.id.dialog_tasklist);

        addList.setOnClickListener(this);

        TableTaskList tableTaskList = new TableTaskList(BaseApplication.getInstance());


        Bundle extras = getArguments();
        if (extras != null) {
            edit = extras.getString("ifedit");
        }

        ArrayList<TaskListModel> userTaskLists = new ArrayList<>();

//        userTaskLists.add(new TaskListModel(0, "List Name 1", 2));
//        userTaskLists.add(new TaskListModel(1, "List Name 2", 8));
//        userTaskLists.add(new TaskListModel(2, "List Name 3", 5));

        userTaskLists = tableTaskList.getAllList(SharedPrefUtils.getUserDetailModel(getActivity()).getId());

        userTaskListAdapter = new UserTaskListAdapter(getActivity(), userTaskLists, true);
        taskList.setAdapter(userTaskListAdapter);

        taskList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

//                userTaskListAdapter.getItem(i)

                if (edit.equalsIgnoreCase("edit")) {
                    ((ActivityEditTask) getActivity()).selectList(((TaskListModel) userTaskListAdapter.getItem(i)).getList_name(), ((TaskListModel) userTaskListAdapter.getItem(i)).getId());
                    getDialog().dismiss();
                } else {
                    ((ActivityCreateTask) getActivity()).selectList(((TaskListModel) userTaskListAdapter.getItem(i)).getList_name(), ((TaskListModel) userTaskListAdapter.getItem(i)).getId());
                    getDialog().dismiss();
                }


            }
        });

        return rootView;
    }


    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.dialog_addlist:

                AddTaskListPopUp addTaskListPopUp = new AddTaskListPopUp();

                Bundle bundle = new Bundle();


                if (edit.equalsIgnoreCase("edit")){
                    bundle.putString("from", "editTask");
                }else {
                    bundle.putString("from", "createTask");
                }

                addTaskListPopUp.setArguments(bundle);

                addTaskListPopUp.show(getFragmentManager(), "addList");
                getDialog().dismiss();

                break;

        }
    }
}