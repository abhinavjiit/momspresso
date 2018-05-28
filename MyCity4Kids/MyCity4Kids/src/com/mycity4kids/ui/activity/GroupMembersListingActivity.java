package com.mycity4kids.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.mycity4kids.R;
import com.mycity4kids.ui.adapter.GroupMemberRecyclerAdapter;

/**
 * Created by hemant on 25/5/18.
 */

public class GroupMembersListingActivity extends BaseActivity {

    private GroupMemberRecyclerAdapter groupMemberRecyclerAdapter;
    private RecyclerView groupMembersRecyclerView;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_member_listing_activity);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        groupMembersRecyclerView = (RecyclerView) findViewById(R.id.groupMembersRecyclerView);

        final LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        groupMembersRecyclerView.setLayoutManager(llm);
    }

    @Override
    protected void updateUi(Response response) {

    }
}
