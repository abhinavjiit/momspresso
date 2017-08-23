package com.mycity4kids.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ListView;
import android.widget.TextView;

import com.kelltontech.utils.ToastUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.dbtable.TableAdult;
import com.mycity4kids.dbtable.TableKids;
import com.mycity4kids.models.user.KidsInfo;
import com.mycity4kids.models.user.UserInfo;
import com.mycity4kids.newmodels.AttendeeModel;
import com.mycity4kids.ui.adapter.AttendeeCustomAdapter;

import java.util.ArrayList;

/**
 * Created by user on 08-06-2015.
 */
public class AttendeeDialogFragment extends android.app.DialogFragment {

    ArrayList<AttendeeModel> data;
    ListView listView;
    TextView cancel, done;
    AttendeeCustomAdapter adapter;
    public ArrayList<String> chklist;
    private boolean all;
    private boolean edit;
    String iftask = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.aa_attendees_fragment, container,
                false);
        TextView addAdultTextView = (TextView) rootView.findViewById(R.id.additional_adult);
        addAdultTextView.setVisibility(View.GONE);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);


        Bundle extras = getArguments();
        if (extras != null) {
            chklist = extras.getStringArrayList("chkValues");
            all = extras.getBoolean("All");
            edit = extras.getBoolean("edit");
            iftask = extras.getString("iftask");

        }

        if (iftask.equalsIgnoreCase("iftask")) {
            TextView title = (TextView) rootView.findViewById(R.id.title);
            title.setText("ASSIGNEES");
        }

        TableKids tableKids = new TableKids(BaseApplication.getInstance());
        ArrayList<KidsInfo> kidsInformations = tableKids.getAllKids();

        TableAdult tableAdult = new TableAdult(BaseApplication.getInstance());
        ArrayList<UserInfo> userInfos = tableAdult.getAllAdults();

        ArrayList<AttendeeModel> attendeeList = new ArrayList<AttendeeModel>();

        for (int i = 0; i < kidsInformations.size(); i++) {
            attendeeList.add(new AttendeeModel(kidsInformations.get(i).getId(), "kid", kidsInformations.get(i).getName(), kidsInformations.get(i).getColor_code()));
        }


        for (int i = 0; i < userInfos.size(); i++) {
            attendeeList.add(new AttendeeModel(userInfos.get(i).getId(), "user", userInfos.get(i).getFirst_name(), userInfos.get(i).getColor_code()));
        }


        listView = (ListView) rootView.findViewById(R.id.attendee_list);
        cancel = (TextView) rootView.findViewById(R.id.cancel);
        done = (TextView) rootView.findViewById(R.id.done);


        AttendeeModel data1 = new AttendeeModel("0", "ALL", "All", "#3949ab");
        attendeeList.add(0, data1);
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

                if (chkCondition()) {

                    if (edit) {

                        if (iftask.equalsIgnoreCase("iftask")) {
//                            ((ActivityEditTask) getActivity()).setAttendee(adapter.getAttendeeList());
                        } else {
//                            ((ActivityEditAppointment) getActivity()).setAttendee(adapter.getAttendeeList());
                        }

                        getDialog().dismiss();
                    } else {

                        if (iftask.equalsIgnoreCase("iftask")) {
//                            ((ActivityCreateTask) getActivity()).setAttendee(adapter.getAttendeeList());
                        } else {
//                            ((ActivityCreateAppointment) getActivity()).setAttendee(adapter.getAttendeeList());
                        }

                        getDialog().dismiss();
                    }

                } else {
                    ToastUtils.showToast(getActivity(), "Please select atleast one attendee");
                }
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

}
