package com.mycity4kids.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.mycity4kids.R;
import com.mycity4kids.models.Topics;
import com.mycity4kids.widget.TopicView;

import org.apmem.tools.layouts.FlowLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by hemant on 10/6/16.
 */
public class EditSelectedTopicsActivity extends BaseActivity {

    FlowLayout rootView;
    TextView emptyTopicsTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_selected_topics_activity);

        ArrayList<Topics> selectedTopics = getIntent().getParcelableArrayListExtra("selectedTopics");
        rootView = (FlowLayout) findViewById(R.id.rootView);
        emptyTopicsTextView = (TextView) findViewById(R.id.emptyTopicsTextView);

        for (int i = 0; i < selectedTopics.size(); i++) {
            final TopicView topicView = new TopicView(this);
            topicView.setCategory(selectedTopics.get(i).getParentName());
            topicView.setSubcategory(selectedTopics.get(i).getTitle());
            ((ImageView) topicView.findViewById(R.id.removeTopicImageView)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    topicView.removeTopic();
                    if (rootView.getChildCount() == 0) {
//                        showToast("dadadadawdadawd");
                        rootView.setVisibility(View.GONE);
                        emptyTopicsTextView.setVisibility(View.VISIBLE);
                    }
                }
            });
            rootView.addView(topicView);
        }
    }

    @Override
    protected void updateUi(Response response) {

    }
}
