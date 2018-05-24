package com.mycity4kids.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Switch;

import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.mycity4kids.R;
import com.mycity4kids.models.response.GroupResult;

/**
 * Created by hemant on 24/4/18.
 */

public class GroupSettingsActivity extends BaseActivity implements View.OnClickListener {

    GroupResult groupItem;

    private RelativeLayout leaveGroupContainer;
    private Switch disableNotificationSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_settings_activity);
        disableNotificationSwitch = (Switch) findViewById(R.id.disableNotificationSwitch);
        leaveGroupContainer = (RelativeLayout) findViewById(R.id.leaveGroupContainer);

        groupItem = (GroupResult) getIntent().getParcelableExtra("groupItem");

        disableNotificationSwitch.setOnClickListener(this);
        leaveGroupContainer.setOnClickListener(this);
    }

    @Override
    protected void updateUi(Response response) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.disableNotificationSwitch:

                break;
            case R.id.leaveGroupContainer:
                Intent intent = new Intent(GroupSettingsActivity.this, LeaveGroupActivity.class);
                intent.putExtra("groupItem", groupItem);
                startActivity(intent);
                break;
        }
    }
}
