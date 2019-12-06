package com.mycity4kids.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseFragment;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.models.Topics;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.ui.activity.ChallengeDetailListingActivity;
import com.mycity4kids.ui.adapter.ChallengeRecyclerAdapter;
import java.util.ArrayList;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class TopicChallengeTabFragment extends BaseFragment implements View.OnClickListener, ChallengeRecyclerAdapter.RecyclerViewClickListener {
    RecyclerView recyclerView;
    LinearLayoutManager llm;
    String userDynamoId;
    Topics currentSubTopic;
    private Topics selectedTopic;
    private ArrayList<String> challengeId = new ArrayList<String>();
    private ArrayList<String> Display_Name = new ArrayList<String>();
    private ArrayList<String> activeImageUrl = new ArrayList<String>();
    private ChallengeRecyclerAdapter recyclerAdapter;
    private SwipeRefreshLayout pullToRefresh;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.challenge_tab_fragment, container, false);
        if (getArguments() != null) {
            currentSubTopic = getArguments().getParcelable("currentSubTopic");
            selectedTopic = currentSubTopic;
        }
        pullToRefresh = view.findViewById(R.id.pullToRefresh);
        userDynamoId = SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId();
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview_challenge);
        llm = new LinearLayoutManager(getActivity());
        recyclerAdapter = new ChallengeRecyclerAdapter(this, getActivity(), challengeId, Display_Name, activeImageUrl);
        llm.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(llm);
        recyclerAdapter.setListData(selectedTopic);
        recyclerView.setAdapter(recyclerAdapter);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pullToRefresh.setRefreshing(false);
                recyclerAdapter.notifyDataSetChanged();
            }
        });


        return view;
    }

    @Override
    protected void updateUi(Response response) {

    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onClick(View view, int position, ArrayList<String> challengeId, ArrayList<String> Display_Name, Topics articledatamodal, ArrayList<String> activeImageUrl) {
        switch (view.getId()) {
            case R.id.mainView:
            case R.id.getStartedTextView:
                Intent intent = new Intent(getContext(), ChallengeDetailListingActivity.class);
                intent.putExtra("Display_Name", Display_Name);
                intent.putExtra("challenge", challengeId);
                intent.putExtra("position", position);
                intent.putExtra("topics", articledatamodal.getParentName());
                intent.putExtra("parentId", articledatamodal.getParentId());
                intent.putExtra("StringUrl", activeImageUrl);
                startActivity(intent);
        }
    }
}
