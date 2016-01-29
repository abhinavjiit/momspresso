package com.chatPlatform.ActivitiesFragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;

/**
 * Created by anshul on 1/7/16.
 */
public class GroupDetails extends BaseActivity {
    String groupChannelId;
    TextView groupName, description,next;
    BaseApplication app;
    String title;
    String descriptionContent;
    LinearLayout notificationLayout, leaveGroupLayout, muteLayout;
    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_detail);
        mToolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Group Details");
        //  Intent intent=getIntent();
        app = (BaseApplication) getApplicationContext();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            groupChannelId = extras.getString("groupChannelId");
        }
        groupName = (TextView) findViewById(R.id.groupName);
        description = (TextView) findViewById(R.id.description);
        next = (TextView) findViewById(R.id.next);
        notificationLayout = (LinearLayout) findViewById(R.id.notificationLayout);
        leaveGroupLayout = (LinearLayout) findViewById(R.id.leaveGroupLayout);
        muteLayout = (LinearLayout) findViewById(R.id.muteLayout);
        if (groupChannelId != null) {
            Database database = app.getDatabase();
            Document document = database.getDocument(groupChannelId);
            title = document.getProperty("title").toString();
            descriptionContent = document.getProperty("description").toString();
        }
        leaveGroupLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(GroupDetails.this,"rook ja",Toast.LENGTH_LONG).show();
            }
        });

        notificationLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        muteLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        groupName.setText(title);
        description.setText(descriptionContent);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(GroupDetails.this,GroupMembers.class);
                intent.putExtra("groupChannelId",groupChannelId);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void updateUi(Response response) {

    }
}
