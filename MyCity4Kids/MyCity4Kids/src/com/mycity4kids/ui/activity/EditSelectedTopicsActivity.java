package com.mycity4kids.ui.activity;

import android.os.Bundle;

import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.mycity4kids.R;
import com.mycity4kids.widget.TopicView;

import org.apmem.tools.layouts.FlowLayout;

/**
 * Created by hemant on 10/6/16.
 */
public class EditSelectedTopicsActivity extends BaseActivity {

    FlowLayout rootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_selected_topics_activity);

        rootView = (FlowLayout) findViewById(R.id.rootView);
        for (int i = 0; i < 10; i++) {
            TopicView topicView = new TopicView(this);
            topicView.setCategory("category_" + i);
            topicView.setSubcategory("subcategory_" + i);
            rootView.addView(topicView);
        }
    }

    @Override
    protected void updateUi(Response response) {

    }
}
