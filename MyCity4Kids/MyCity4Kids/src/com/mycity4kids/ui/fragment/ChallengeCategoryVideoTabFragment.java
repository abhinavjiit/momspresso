package com.mycity4kids.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseFragment;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.models.Topics;
import com.mycity4kids.models.response.ArticleListingResult;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.ui.activity.VideoChallengeDetailListingActivity;
import com.mycity4kids.ui.adapter.ChallengeVideoRecycleAdapter;

import java.util.ArrayList;

public class ChallengeCategoryVideoTabFragment extends BaseFragment implements View.OnClickListener, ChallengeVideoRecycleAdapter.RecyclerViewClickListener {
    private RecyclerView recyclerView;
    private LinearLayoutManager llm;
    private String userDynamoId;
    private Topics currentSubTopic;
    private ArrayList<ArticleListingResult> mDatalist;
    private Topics selectedTopic;
    private ArrayList<String> challengeId = new ArrayList<String>();
    private ArrayList<String> Display_Name = new ArrayList<String>();
    private ArrayList<String> activeImageUrl = new ArrayList<String>();
    private ArrayList<String> activeStreamUrl = new ArrayList<String>();
    private ChallengeVideoRecycleAdapter recyclerAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.challenge_tab_fragment, container, false);
        if (getArguments() != null) {
            currentSubTopic = getArguments().getParcelable("currentSubTopic");
            selectedTopic = currentSubTopic;
        }
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview_challenge);
        llm = new LinearLayoutManager(getActivity());
        recyclerAdapter = new ChallengeVideoRecycleAdapter(this, getActivity(), challengeId, Display_Name, activeImageUrl, activeStreamUrl);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(recyclerAdapter);
        recyclerAdapter.setListData(selectedTopic);
        userDynamoId = SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId();


        return view;


    }

    @Override
    protected void updateUi(Response response) {

    }

    @Override
    public void onClick(View view) {
    }

    @Override
    public void onClick(View view, int position, ArrayList<String> challengeId, ArrayList<String> Display_Name, Topics articledatamodal, ArrayList<String> imageUrl, ArrayList<String> activeStreamUrl) {
        switch (view.getId()) {
            case R.id.mainView:
            case R.id.getStartedTextView:
               /* Bundle bundle = new Bundle();
                bundle.putParcelable("topic", articledatamodal);*/

                Intent intent = new Intent(getActivity(), VideoChallengeDetailListingActivity.class);
                intent.putExtra("Display_Name", Display_Name);
                intent.putExtra("challenge", challengeId);
                intent.putExtra("position", position);
                intent.putExtra("StreamUrl", activeStreamUrl);
                intent.putExtra("topics", articledatamodal.getParentName());
                intent.putExtra("parentId", articledatamodal.getParentId());
                intent.putExtra("StringUrl", activeImageUrl);
                intent.putExtra("Topic", new Gson().toJson(articledatamodal));

                startActivity(intent);
        }


    }
}
