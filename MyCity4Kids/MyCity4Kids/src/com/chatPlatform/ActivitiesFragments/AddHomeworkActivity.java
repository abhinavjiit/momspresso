package com.chatPlatform.ActivitiesFragments;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;
import android.widget.TextView;

import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.mycity4kids.R;
import com.mycity4kids.widget.CustomListView;

/**
 * Created by hemant on 19/1/16.
 */
public class AddHomeworkActivity extends BaseActivity {

    private Toolbar mToolbar;
    private TextView dueDateTextView;
    private EditText homeworkTitleEditText, homeworkNotesEditText;
    private CustomListView homeworkAttachment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_homework);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        dueDateTextView = (TextView) findViewById(R.id.due_date);
        homeworkTitleEditText = (EditText) findViewById(R.id.homework_title);
        homeworkNotesEditText = (EditText) findViewById(R.id.homework_notes);

        homeworkAttachment = (CustomListView) findViewById(R.id.homework_attachment);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Add Homework");
    }

    @Override
    protected void updateUi(Response response) {

    }
}
