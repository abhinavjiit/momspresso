package com.mycity4kids.ui.fragment;

import android.accounts.NetworkErrorException;
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

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseFragment;
import com.kelltontech.utils.ConnectivityUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.Topics;
import com.mycity4kids.models.TopicsResponse;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.VlogsListingAndDetailsAPI;
import com.mycity4kids.ui.adapter.ChallengeVideoRecycleAdapter;
import com.mycity4kids.ui.videochallengenewui.activity.NewVideoChallengeActivity;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

public class ChallengeCategoryVideoTabFragment extends BaseFragment implements View.OnClickListener, ChallengeVideoRecycleAdapter.RecyclerViewClickListener {
    RecyclerView recyclerView;
    LinearLayoutManager llm;
    String userDynamoId;
    Topics currentSubTopic;
    Topics selectedTopic;
    private ArrayList<String> challengeId = new ArrayList<String>();
    private ArrayList<String> Display_Name = new ArrayList<String>();
    private ArrayList<String> activeImageUrl = new ArrayList<String>();
    private ArrayList<String> activeStreamUrl = new ArrayList<String>();
    private ArrayList<String> rules = new ArrayList<>();
    private ArrayList<Topics> allChallenge = new ArrayList<>();
    private ChallengeVideoRecycleAdapter recyclerAdapter;
    private SwipeRefreshLayout pullToRefresh;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.challenge_tab_fragment, container, false);
        if (getArguments() != null) {
            currentSubTopic = getArguments().getParcelable("currentSubTopic");
            selectedTopic = currentSubTopic;
        }
        getChallenges();
        pullToRefresh = view.findViewById(R.id.pullToRefresh);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview_challenge);
        llm = new LinearLayoutManager(getActivity());
        recyclerAdapter = new ChallengeVideoRecycleAdapter(this, getActivity(), challengeId, Display_Name, activeImageUrl, activeStreamUrl, rules);
        llm.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(recyclerAdapter);
        userDynamoId = SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId();

        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                allChallenge.clear();
                recyclerAdapter.notifyDataSetChanged();
                getChallenges();
                pullToRefresh.setRefreshing(false);
            }
        });
        return view;
    }

    private void getChallenges() {
        if (!ConnectivityUtils.isNetworkEnabled(getActivity())) {
            removeProgressDialog();
            return;
        }
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        VlogsListingAndDetailsAPI vlogsListingAndDetailsAPI = retrofit.create(VlogsListingAndDetailsAPI.class);
        Call<TopicsResponse> callRecentVideoArticles = vlogsListingAndDetailsAPI.getVlogChallenges();
        callRecentVideoArticles.enqueue(vlogChallengeResponseCallBack);
    }

    private Callback<TopicsResponse> vlogChallengeResponseCallBack = new Callback<TopicsResponse>() {
        @Override
        public void onResponse(Call<TopicsResponse> call, retrofit2.Response<TopicsResponse> response) {
            if (null == response.body()) {
                NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                Crashlytics.logException(nee);
                return;
            }
            if (response.isSuccessful()) {
                try {
                    TopicsResponse responseData = response.body();
                    if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                        allChallenge = responseData.getData();
                        recyclerAdapter.setListData(allChallenge);
                        recyclerAdapter.notifyDataSetChanged();
                    }
                } catch (Exception e) {
                    Crashlytics.logException(e);
                    Log.d("MC4kException", Log.getStackTraceString(e));
                }
            }
        }

        @Override
        public void onFailure(Call<TopicsResponse> call, Throwable t) {

        }
    };

    @Override
    protected void updateUi(Response response) {

    }

    @Override
    public void onClick(View view) {
    }

    @Override
    public void onClick(View view, int position, ArrayList<String> challengeId, ArrayList<String> Display_Name, Topics articledatamodal, ArrayList<String> imageUrl, ArrayList<String> activeStreamUrl, ArrayList<String> rules, ArrayList<String> mappedCategory, int max_Duration) {
        switch (view.getId()) {
            case R.id.mainView:
            case R.id.getStartedTextView:
                Intent intent = new Intent(getActivity(), NewVideoChallengeActivity.class);
                Utils.momVlogEvent(getActivity(), "Video Listing", "Challenge container", "", "android", SharedPrefUtils.getAppLocale(BaseApplication.getAppContext()), SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId(), String.valueOf(System.currentTimeMillis()), "Show_video_creation_categories", "", challengeId.toString());
                intent.putExtra("challenge", challengeId.get(position));
                intent.putExtra("comingFrom", "vlog_listing");
                intent.putExtra("mappedId", mappedCategory.get(position));
                startActivity(intent);
        }
    }
}
