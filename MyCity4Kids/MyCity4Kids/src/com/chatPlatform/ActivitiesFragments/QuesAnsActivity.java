package com.chatPlatform.ActivitiesFragments;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.chatPlatform.Adapters.QARecyclerViewAdapter;
import com.chatPlatform.models.HomeworkModel;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.mycity4kids.R;

import java.util.ArrayList;

/**
 * Created by hemant on 14/1/16.
 */
public class QuesAnsActivity extends BaseActivity {

    RecyclerView mRecyclerView;
    private GridLayoutManager mLayoutManager;
    private RecyclerView.Adapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qa_list_activity);
        mRecyclerView = (RecyclerView) findViewById(R.id.qaRecyclerView);
        ArrayList<HomeworkModel> list = new ArrayList<HomeworkModel>();
        for (int i = 1; i < 10; i++) {
            list.add(new HomeworkModel("" + i, "item_" + i));
        }
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new QARecyclerViewAdapter(list);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new SpacesItemDecoration(2));
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ((QARecyclerViewAdapter) mAdapter).setOnItemClickListener(
                new QARecyclerViewAdapter.MyClickListener() {
                    @Override
                    public void onItemClick(int position, View v) {
                        Log.i("LOG_TAG", " Clicked on Item " + position);
                    }
                });
    }

    public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private int space;

        private final int mVerticalSpaceHeight;

        public SpacesItemDecoration(int mVerticalSpaceHeight) {
            this.mVerticalSpaceHeight = mVerticalSpaceHeight;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                                   RecyclerView.State state) {
            outRect.bottom = mVerticalSpaceHeight;
        }


    }


    @Override
    protected void updateUi(Response response) {

    }
}
