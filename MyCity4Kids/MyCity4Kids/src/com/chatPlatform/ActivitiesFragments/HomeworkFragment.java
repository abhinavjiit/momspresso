package com.chatPlatform.ActivitiesFragments;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.chatPlatform.Adapters.HomeworkRecyclerViewAdapter;
import com.chatPlatform.models.HomeworkModel;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.mycity4kids.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hemant on 13/1/16.
 */
public class HomeworkFragment extends BaseActivity {

    RecyclerView mRecyclerView;
    private GridLayoutManager mLayoutManager;
    private RecyclerView.Adapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_homework);
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        ArrayList<HomeworkModel> list = new ArrayList<HomeworkModel>();
        for (int i = 1; i < 10; i++) {
            list.add(new HomeworkModel("" + i, "item_" + i));
        }
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new HomeworkRecyclerViewAdapter(list);
        GridLayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new SpacesItemDecoration(10));
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ((HomeworkRecyclerViewAdapter) mAdapter).setOnItemClickListener(
                new HomeworkRecyclerViewAdapter.MyClickListener() {
                    @Override
                    public void onItemClick(int position, View v) {
                        Log.i("LOG_TAG", " Clicked on Item " + position);
                        Intent qaIntent = new Intent(HomeworkFragment.this, QADetailsActivity.class);
                        startActivity(qaIntent);
                    }
                });
    }

    public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private int space;

        public SpacesItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view,
                                   RecyclerView parent, RecyclerView.State state) {
            outRect.left = space;
            outRect.right = space;
            outRect.bottom = space;

            // Add top margin only for the first item to avoid double space between items
//            if (parent.getChildLayoutPosition(view) == 0)
//                outRect.top = space;
        }
    }

    @Override
    protected void updateUi(Response response) {

    }
}
