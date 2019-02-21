package com.mycity4kids.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseFragment;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.models.Topics;
import com.mycity4kids.models.response.ArticleListingResult;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.ui.activity.ChallnegeDetailListingActivity;
import com.mycity4kids.ui.adapter.ChallengeRecyclerAdapter;

import java.util.ArrayList;

public class TopicChallengeTabFragment extends BaseFragment implements View.OnClickListener, ChallengeRecyclerAdapter.RecyclerViewClickListener {
    private RecyclerView recyclerView;
    private LinearLayoutManager llm;
    private String userDynamoId;
    private Topics currentSubTopic;
    private ArrayList<ArticleListingResult> mDatalist;
    private Topics selectedTopic;
    private ArrayList<String> challengeId = new ArrayList<String>();
    private ArrayList<String> Display_Name = new ArrayList<String>();
    private ArrayList<String> activeImageUrl = new ArrayList<String>();
    private ChallengeRecyclerAdapter recyclerAdapter;
    private int pastVisiblesItems, visibleItemCount, totalItemCount;
    private boolean isReuqestRunning = false;
    private boolean isLastPageReached = false;
    // private ChallengeRecyclerAdapter.RecyclerViewClickListener recyclerViewClickListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.challenge_tab_fragment, container, false);
        if (getArguments() != null) {
            currentSubTopic = getArguments().getParcelable("currentSubTopic");
            selectedTopic = currentSubTopic;
        }

        userDynamoId = SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId();

      /*  JSONObject data = new JSONObject((Map) selectedTopic);
        JSONArray accounts = null;
        try {
            accounts = data.getJSONArray("data");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        List<Topics> accountList = new Gson().fromJson(accounts.toString(), new TypeToken<ArrayList<Topics>>() {}.getType());*/
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview_challenge);
        llm = new LinearLayoutManager(getActivity());
        recyclerAdapter = new ChallengeRecyclerAdapter(this, getActivity(), challengeId, Display_Name, activeImageUrl);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        recyclerAdapter.setListData(selectedTopic);
        recyclerView.setAdapter(recyclerAdapter);
        // ((LinearLayoutManager) recyclerView.getLayoutManager()).scrollToPositionWithOffset(recyclerAdapter.currentPosition(), 200);
       /* recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) //check for scroll down
                {
                    visibleItemCount = llm.getChildCount();
                    totalItemCount = llm.getItemCount();
                    pastVisiblesItems = llm.findFirstVisibleItemPosition();

                    if (!isReuqestRunning && !isLastPageReached) {
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                            isReuqestRunning = true;
                            recyclerAdapter.setListData(selectedTopic);
                            recyclerAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        });*/


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
                Intent intent = new Intent(getContext(), ChallnegeDetailListingActivity.class);
                intent.putExtra("Display_Name", Display_Name);
                intent.putExtra("challenge", challengeId);
                intent.putExtra("position", position);
                intent.putExtra("topics", articledatamodal.getParentName());
                intent.putExtra("parentId", articledatamodal.getParentId());
                intent.putExtra("StringUrl", activeImageUrl);
                //    intent.putExtra("Data",articledatamodal);
                startActivity(intent);


        }


    }
}