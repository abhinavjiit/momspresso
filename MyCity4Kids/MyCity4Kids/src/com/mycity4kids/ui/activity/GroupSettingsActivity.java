package com.mycity4kids.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.mycity4kids.R;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.models.response.GroupResult;

/**
 * Created by hemant on 24/4/18.
 */

public class GroupSettingsActivity extends BaseActivity implements View.OnClickListener {

    GroupResult groupItem;

    private RelativeLayout leaveGroupContainer, reportedContentContainer;
    private Switch disableNotificationSwitch;
    private ImageView editGroupImageView;
    private TextView memberCountTextView;
    private Toolbar toolbar;
    private String memberType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_settings_activity);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        disableNotificationSwitch = (Switch) findViewById(R.id.disableNotificationSwitch);
        leaveGroupContainer = (RelativeLayout) findViewById(R.id.leaveGroupContainer);
        editGroupImageView = (ImageView) findViewById(R.id.editGroupImageView);
        reportedContentContainer = (RelativeLayout) findViewById(R.id.reportedContentContainer);
        memberCountTextView = (TextView) findViewById(R.id.memberCountTextView);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        groupItem = (GroupResult) getIntent().getParcelableExtra("groupItem");
        memberType = getIntent().getStringExtra(AppConstants.GROUP_MEMBER_TYPE);

        disableNotificationSwitch.setOnClickListener(this);
        leaveGroupContainer.setOnClickListener(this);
        editGroupImageView.setOnClickListener(this);
        reportedContentContainer.setOnClickListener(this);

        if (AppConstants.GROUP_MEMBER_TYPE_ADMIN.equals(memberType)) {
            reportedContentContainer.setVisibility(View.VISIBLE);
            editGroupImageView.setVisibility(View.VISIBLE);
            memberCountTextView.setOnClickListener(this);
        }

        memberCountTextView.setText("" + groupItem.getMemberCount());
    }

    @Override
    protected void updateUi(Response response) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.memberCountTextView: {
                Intent intent = new Intent(GroupSettingsActivity.this, GroupMembershipRequestActivity.class);
                intent.putExtra("groupId", groupItem.getId());
                startActivity(intent);
            }
            break;
            case R.id.editGroupImageView: {
                Intent intent = new Intent(GroupSettingsActivity.this, EditGroupActivity.class);
                intent.putExtra("groupId", groupItem.getId());
                startActivity(intent);
            }
            break;
            case R.id.disableNotificationSwitch:

                break;
            case R.id.reportedContentContainer: {
                Intent intent = new Intent(GroupSettingsActivity.this, GroupsReportedContentActivity.class);
                intent.putExtra("groupItem", groupItem);
                startActivity(intent);
            }
            break;
            case R.id.leaveGroupContainer: {
                Intent intent = new Intent(GroupSettingsActivity.this, LeaveGroupActivity.class);
                intent.putExtra("groupItem", groupItem);
                startActivity(intent);
            }
            break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
