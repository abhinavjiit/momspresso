package com.mycity4kids.ui.activity;

import android.os.Bundle;

import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.mycity4kids.R;

/**
 * Created by hemant on 26/4/18.
 */

public class LeaveGroupActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.leave_group_activity);
    }

    @Override
    protected void updateUi(Response response) {

    }
}
