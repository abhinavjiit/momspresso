package com.mycity4kids.ui.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ListView;
import android.widget.TextView;

import com.kelltontech.utils.StringUtils;
import com.kelltontech.utils.ToastUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.dbtable.TableAdult;
import com.mycity4kids.models.user.UserInfo;
import com.mycity4kids.newmodels.AttendeeModel;
import com.mycity4kids.ui.adapter.AttendeeCustomAdapter;

import java.util.ArrayList;

/**
 * Created by user on 08-06-2015.
 */
public class WhoToRemindDialogFragment extends android.app.DialogFragment {

    ArrayList<AttendeeModel> data;
    ListView listView;
    TextView cancel, done;
    AttendeeCustomAdapter adapter;
    public ArrayList<String> chklist;
    private boolean all;
    private boolean edit;
    String iftask = "";
    String dialogTitle;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.aa_attendees_fragment, container,
                false);
        TextView addAdultTextView = (TextView) rootView.findViewById(R.id.additional_adult);
        addAdultTextView.setVisibility(View.VISIBLE);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        TextView title = (TextView) rootView.findViewById(R.id.title);
        //title.setText("Who to remind");

        Bundle extras = getArguments();
        if (extras != null) {
            chklist = extras.getStringArrayList("chkValues");
            all = extras.getBoolean("All");
            edit = extras.getBoolean("edit");
            iftask = extras.getString("iftask");
            dialogTitle = extras.getString("dialogTitle");
        }
        title.setText(dialogTitle);
        addAdultTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAddAdultDialog();
            }
        });


        TableAdult tableAdult = new TableAdult(BaseApplication.getInstance());
        ArrayList<UserInfo> userInfos = tableAdult.getAllAdults();

        ArrayList<AttendeeModel> attendeeList = new ArrayList<AttendeeModel>();

        if (dialogTitle.equals("Share with")) {
            Log.d("User Size is ", String.valueOf(userInfos.size()));


            for (int i = 1; i < userInfos.size(); i++) {
                attendeeList.add(new AttendeeModel(userInfos.get(i).getId(), "user", userInfos.get(i).getFirst_name(), userInfos.get(i).getColor_code()));
            }


        }
        else {


            for (int i = 0; i < userInfos.size(); i++) {
                attendeeList.add(new AttendeeModel(userInfos.get(i).getId(), "user", userInfos.get(i).getFirst_name(), userInfos.get(i).getColor_code()));
            }
        }


        listView = (ListView) rootView.findViewById(R.id.attendee_list);
        cancel = (TextView) rootView.findViewById(R.id.cancel);
        done = (TextView) rootView.findViewById(R.id.done);

        AttendeeModel data1 = new AttendeeModel("0", "ALL", "All", "#3949ab");
        if(userInfos.size()<2) {
            System.out.println("Hello");
        }
        else if(userInfos.size()==2)
        {
            System.out.println("Do nothing");
        }
        else
        {
            attendeeList.add(0, data1);
        }

        // chking values
        if (all) {
            for (int i = 0; i < attendeeList.size(); i++) {
                attendeeList.get(i).setCheck(true);
            }


        } else {
            for (int i = 0; i < attendeeList.size(); i++) {
                for (int j = 0; j < chklist.size(); j++) {
                    if (attendeeList.get(i).getId().equals(chklist.get(j))) {
                        attendeeList.get(i).setCheck(true);
                    }
                }
            }


        }

        adapter = new AttendeeCustomAdapter(getActivity(), attendeeList);
        listView.setAdapter(adapter);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dialogTitle.equals("Who to remind")) {


                    if (chkCondition()) {

                        if (edit) {

                            if (!StringUtils.isNullOrEmpty(iftask) && iftask.equalsIgnoreCase("iftask")) {
//                                ((ActivityEditTask) getActivity()).setWhoToRemind(adapter.getAttendeeList());
                            } else {
//                                ((ActivityEditAppointment) getActivity()).setWhoToRemind(adapter.getAttendeeList());
                            }

                            getDialog().dismiss();
                        } else {

                            if (!StringUtils.isNullOrEmpty(iftask) && iftask.equalsIgnoreCase("iftask")) {
//                                ((ActivityCreateTask) getActivity()).setWhoToRemind(adapter.getAttendeeList());
                            } else {
//                                ((ActivityCreateAppointment) getActivity()).setWhoToRemind(adapter.getAttendeeList());
                            }

                            getDialog().dismiss();
                        }


                    } else {
                        ToastUtils.showToast(getActivity(), "Please select atleast one user");
                    }
                }
//                else if (dialogTitle.equals("Share with")) {
//
//
//                    if (chkCondition()) {
//                        ((ArticlesAndBlogsDetailsActivity) getActivity()).setShareWith(adapter.getAttendeeList());
//
//                        ToastUtils.showToast(getActivity(), "Thanks for Sharing");
//                        getDialog().dismiss();
//
//                    } else {
//                        ToastUtils.showToast(getActivity(), "Please select atleast one user");
//                    }
//                }


            }
        });
        return rootView;
    }

    public boolean chkCondition() {
        boolean result = false;
        for (int i = 0; i < adapter.getAttendeeList().size(); i++) {

            if (adapter.getAttendeeList().get(i).getCheck() == true) {

                result = true;
                break;

            }
        }
        return result;

    }

    private void openAddAdultDialog() {

    }
}
