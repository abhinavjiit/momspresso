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
import com.mycity4kids.ui.activity.ActivityCreateAppointment;
import com.mycity4kids.ui.activity.ActivityCreateTask;
import com.mycity4kids.ui.activity.ActivityEditAppointment;
import com.mycity4kids.ui.activity.ActivityEditTask;
import com.mycity4kids.ui.activity.ArticlesAndBlogsDetailsActivity;
import com.mycity4kids.ui.adapter.AttendeeCustomAdapter;

import java.util.ArrayList;

/**
 * Created by user on 08-06-2015.
 */
public class AnshulFrag extends android.app.DialogFragment {

    ArrayList<AttendeeModel> data;
    ListView listView;
    TextView cancel, done;
    AttendeeCustomAdapter adapter;
    public ArrayList<Integer> chklist;
    private boolean all;
    private boolean edit;
    String iftask = "";
    String dialogTitle;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.anshul_fragment, container,
                false);
        return rootView;
    }

}
