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
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.Topics;
import com.mycity4kids.models.response.ArticleListingResult;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.ui.adapter.ChallengeVideoRecycleAdapter;
import com.mycity4kids.ui.videochallengenewui.activity.NewVideoChallengeActivity;

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
    private ArrayList<String> rules = new ArrayList<>();

    private ChallengeVideoRecycleAdapter recyclerAdapter;
    private String jsonMyObject;

    @Nullable
    @Override

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.challenge_tab_fragment, container, false);
        if (getArguments() != null) {
           /* jsonMyObject = getArguments().getString("currentSubTopic");
            currentSubTopic = new Gson().fromJson(jsonMyObject, Topics.class);*/
            currentSubTopic = getArguments().getParcelable("currentSubTopic");
            selectedTopic = currentSubTopic;
        }


        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview_challenge);
        llm = new LinearLayoutManager(getActivity());
        recyclerAdapter = new ChallengeVideoRecycleAdapter(this, getActivity(), challengeId, Display_Name, activeImageUrl, activeStreamUrl, rules);
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
    public void onClick(View view, int position, ArrayList<String> challengeId, ArrayList<String> Display_Name, Topics articledatamodal, ArrayList<String> imageUrl, ArrayList<String> activeStreamUrl, ArrayList<String> rules) {
        switch (view.getId()) {
            case R.id.mainView:
            case R.id.getStartedTextView:
         /*       Bundle bundle = new Bundle();
                bundle.putParcelable("topic", articledatamodal);*/

                Intent intent = new Intent(getActivity(), NewVideoChallengeActivity.class);
                Utils.momVlogEvent(getActivity(), "Video Listing", "Challenge container", "", "android", SharedPrefUtils.getAppLocale(getActivity()), SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId(), String.valueOf(System.currentTimeMillis()), "Show_video_creation_categories", "", challengeId.toString());

                intent.putExtra("Display_Name", Display_Name);
                intent.putExtra("screenName", "MomVlogs");
                intent.putExtra("challenge", challengeId);
                intent.putExtra("position", position);
                intent.putExtra("StreamUrl", activeStreamUrl);
                intent.putExtra("rules", rules);
                intent.putExtra("topics", articledatamodal.getParentName());
                intent.putExtra("parentId", articledatamodal.getParentId());
                intent.putExtra("StringUrl", activeImageUrl);
                intent.putExtra("Topic", new Gson().toJson(articledatamodal));

                startActivity(intent);
        }


    }
}
