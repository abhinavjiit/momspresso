package com.mycity4kids.ui.fragment;

import android.accounts.NetworkErrorException;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.crashlytics.android.Crashlytics;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.base.BaseFragment;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.Topics;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.VlogsListingAndDetailsAPI;
import com.mycity4kids.ui.videochallengenewui.activity.NewVideoChallengeActivity;
import com.mycity4kids.utils.ConnectivityUtils;
import com.mycity4kids.vlogs.VideoChallengeSelectionHorizontalAdapter;
import com.mycity4kids.vlogs.VideoChallengeSelectionVerticalAdapter;
import com.mycity4kids.vlogs.VlogsCategoryWiseChallengesResponse;
import java.util.ArrayList;
import org.jetbrains.annotations.NotNull;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

public class ChallengeCategoryVideoTabFragment extends BaseFragment implements OnClickListener,
        VideoChallengeSelectionHorizontalAdapter.RecyclerViewClickListener {

    RecyclerView categoryWiseChallengesRecyclerView;
    LinearLayoutManager llm;
    String userDynamoId;
    Topics currentSubTopic;
    Topics selectedTopic;
    private ArrayList<Topics> allChallenge = new ArrayList<>();
    private VideoChallengeSelectionVerticalAdapter recyclerAdapter;
    private SwipeRefreshLayout pullToRefresh;
    private ArrayList<Topics> categoryWiseChallengeList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.vlogs_challenge_list_tab_fragment, container, false);
        if (getArguments() != null) {
            currentSubTopic = getArguments().getParcelable("currentSubTopic");
            selectedTopic = currentSubTopic;
        }
        getChallenges();
        pullToRefresh = view.findViewById(R.id.pullToRefresh);
        categoryWiseChallengesRecyclerView = view.findViewById(R.id.challengesRecyclerView);
        llm = new LinearLayoutManager(getActivity());
        recyclerAdapter = new VideoChallengeSelectionVerticalAdapter(this);
        recyclerAdapter.setSource("vlogsListing");
        recyclerAdapter.setListData(categoryWiseChallengeList);
        llm.setOrientation(RecyclerView.VERTICAL);
        categoryWiseChallengesRecyclerView.setLayoutManager(llm);
        categoryWiseChallengesRecyclerView.setAdapter(recyclerAdapter);
        userDynamoId = SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId();

        pullToRefresh.setOnRefreshListener(() -> {
            allChallenge.clear();
            recyclerAdapter.notifyDataSetChanged();
            getChallenges();
            pullToRefresh.setRefreshing(false);
        });
        return view;
    }

    private void getChallenges() {
        if (!ConnectivityUtils.isNetworkEnabled(getActivity())) {
            removeProgressDialog();
            return;
        }
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        VlogsListingAndDetailsAPI vlogsListingAndDetailsApi = retrofit.create(VlogsListingAndDetailsAPI.class);
        Call<VlogsCategoryWiseChallengesResponse> callRecentVideoArticles = vlogsListingAndDetailsApi
                .getVlogsCategoryWiseChallenges();
        callRecentVideoArticles.enqueue(vlogChallengeResponseCallBack);
    }

    private Callback<VlogsCategoryWiseChallengesResponse> vlogChallengeResponseCallBack =
            new Callback<VlogsCategoryWiseChallengesResponse>() {
                @Override
                public void onResponse(Call<VlogsCategoryWiseChallengesResponse> call,
                        retrofit2.Response<VlogsCategoryWiseChallengesResponse> response) {
                    if (null == response.body()) {
                        NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                        Crashlytics.logException(nee);
                        return;
                    }
                    if (response.isSuccessful()) {
                        try {
                            VlogsCategoryWiseChallengesResponse responseData = response.body();
                            if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                                processChallengesData(responseData.getData().getResult());
                            }
                        } catch (Exception e) {
                            Crashlytics.logException(e);
                            Log.d("MC4kException", Log.getStackTraceString(e));
                        }
                    }
                }

                @Override
                public void onFailure(Call<VlogsCategoryWiseChallengesResponse> call, Throwable t) {

                }
            };

    private void processChallengesData(ArrayList<Topics> catWiseChallengeList) {
        if (categoryWiseChallengeList != null) {
            categoryWiseChallengeList.clear();
        }
        for (int i = 0; i < catWiseChallengeList.size(); i++) {
            ArrayList<Topics> originalChallengeList = new ArrayList<>();
            originalChallengeList.addAll(catWiseChallengeList.get(i).getChild());
            categoryWiseChallengeList.add(catWiseChallengeList.get(i));
            categoryWiseChallengeList.get(i).setChild(new ArrayList<>());
            for (int j = 0; j < originalChallengeList.size(); j++) {
                if ("1".equals(originalChallengeList.get(j).getPublicVisibility())) {
                    categoryWiseChallengeList.get(i).getChild().add(originalChallengeList.get(j));
                }
            }
        }
        recyclerAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View view) {
    }

    @Override
    public void onChallengeItemClick(@NotNull View view, @NotNull Topics topics) {
        try {
            switch (view.getId()) {
                case R.id.tagImageView:
                    Intent intent = new Intent(
                            getActivity(),
                            NewVideoChallengeActivity.class
                    );
                    intent.putExtra("challenge", topics.getId());
                    intent.putExtra("comingFrom", "vlog_listing");
                    startActivity(intent);
                    Utils.momVlogEvent(getActivity(), "Video Listing", "Challenge container", "", "android",
                            SharedPrefUtils.getAppLocale(BaseApplication.getAppContext()),
                            SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId(),
                            String.valueOf(System.currentTimeMillis()), "Show_video_creation_categories", "",
                            topics.getId());
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            Crashlytics.logException(e);
            Log.d("MC4kException", Log.getStackTraceString(e));
        }
    }
}
