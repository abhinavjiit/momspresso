package com.mycity4kids.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.base.BaseFragment;
import com.mycity4kids.models.Topics;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.ui.activity.ShortStoryChallengeDetailActivity;
import com.mycity4kids.ui.adapter.ShortStoryChallengesRecyclerAdapter;

public class ShortStoryChallengeListingTabFragment extends BaseFragment implements View.OnClickListener,
        ShortStoryChallengesRecyclerAdapter.RecyclerViewClickListener {

    RecyclerView recyclerView;
    LinearLayoutManager llm;
    String userDynamoId;
    Topics currentSubTopic;
    private Topics selectedTopic;
    private ShortStoryChallengesRecyclerAdapter recyclerAdapter;
    private SwipeRefreshLayout pullToRefresh;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.challenge_tab_fragment, container, false);
        if (getArguments() != null) {
            currentSubTopic = getArguments().getParcelable("currentSubTopic");
            selectedTopic = currentSubTopic;
        }
        pullToRefresh = view.findViewById(R.id.pullToRefresh);
        userDynamoId = SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId();
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview_challenge);
        llm = new LinearLayoutManager(getActivity());
        recyclerAdapter = new ShortStoryChallengesRecyclerAdapter(this);
        llm.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(llm);
        recyclerAdapter.setListData(selectedTopic);
        recyclerView.setAdapter(recyclerAdapter);
        pullToRefresh.setOnRefreshListener(() -> {
            pullToRefresh.setRefreshing(false);
            recyclerAdapter.notifyDataSetChanged();
        });

        return view;
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onClick(View view, int position, String challengeId, String displayName, Topics articledatamodal,
            String activeImageUrl) {
        try {
            switch (view.getId()) {
                case R.id.mainView:
                case R.id.getStartedTextView:
                    Intent intent = new Intent(getContext(), ShortStoryChallengeDetailActivity.class);
                    intent.putExtra("Display_Name", displayName);
                    intent.putExtra("challenge", challengeId);
                    intent.putExtra("position", position);
                    intent.putExtra("topics", articledatamodal.getParentName());
                    intent.putExtra("parentId", articledatamodal.getParentId());
                    intent.putExtra("StringUrl", activeImageUrl);
                    startActivity(intent);
                default:
                    break;
            }
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            Log.d("MC4KException", Log.getStackTraceString(e));
        }
    }
}
