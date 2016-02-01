package com.chatPlatform.ActivitiesFragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;

/**
 * Created by anshul on 16/12/15.
 */
public class GroupActivity extends Activity {
    Button createGroupButton,viewGroupButton;
    TextView welcomeTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_activity);
        createGroupButton= (Button) findViewById(R.id.createGroupButton);
        viewGroupButton= (Button) findViewById(R.id.viewGroupButton);
        welcomeTextView= (TextView) findViewById(R.id.welcomeTextView);
        // getting application object
        final BaseApplication app= (BaseApplication) getApplicationContext();
        welcomeTextView.setText("welcome " + app.getUserNumber());


        createGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), CreateGroup.class);
                startActivity(intent);

            }
        });

        viewGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), ChatDashboard.class);
                intent.putExtra("toOpen","publicFragment");
                startActivity(intent);

            }
        });


    }
}
