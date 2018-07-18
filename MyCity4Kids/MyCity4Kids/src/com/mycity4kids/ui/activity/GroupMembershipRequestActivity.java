package com.mycity4kids.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.mycity4kids.R;
import com.mycity4kids.ui.adapter.GroupsMembershipRequestRecyclerAdapter;

/**
 * Created by hemant on 9/7/18.
 */

public class GroupMembershipRequestActivity extends BaseActivity implements GroupsMembershipRequestRecyclerAdapter.RecyclerViewClickListener {

    private GroupsMembershipRequestRecyclerAdapter adapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_membership_request_activity);
        recyclerView = (RecyclerView) findViewById(R.id.dotIndicatorView);

        final LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);

        adapter = new GroupsMembershipRequestRecyclerAdapter(this, this);
    }

    @Override
    protected void updateUi(Response response) {

    }

    @Override
    public void onRecyclerItemClick(View view, int position, boolean isMember) {

    }
}
