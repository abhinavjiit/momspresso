package com.mycity4kids.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseFragment;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.FollowTopics;
import com.mycity4kids.models.Topics;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.TopicsCategoryAPI;
import com.mycity4kids.ui.adapter.AddArticleTopicsTabAdapter;
import com.mycity4kids.utils.AppUtils;

import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by hemant on 17/7/17.
 */
public class AddArticleTopicsTabFragment extends BaseFragment implements AddArticleTopicsTabAdapter.RecyclerViewClickListener {

    private String userId;
    private ArrayList<Topics> selectTopic;
    private HashMap<String, Topics> selectedTopicsMap;
    private ArrayList<String> previouslyFollowedTopics;
    private int position;


    private View view;
    RecyclerView popularTopicsRecyclerView;
    private AddArticleTopicsTabAdapter searchTopicsSplashAdapter;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.add_article_topics_tab_fragment, container, false);
        Utils.pushOpenScreenEvent(getActivity(), "Dashboard Fragment", SharedPrefUtils.getUserDetailModel(getActivity()).getDynamoId() + "");
        popularTopicsRecyclerView = (RecyclerView) view.findViewById(R.id.popularTopicsRecyclerView);

        final LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        popularTopicsRecyclerView.setLayoutManager(llm);

        userId = SharedPrefUtils.getUserDetailModel(getActivity()).getDynamoId();

        selectedTopicsMap = new HashMap<>();
        selectTopic = getArguments().getParcelableArrayList("selectTopicList");
        position = getArguments().getInt("position");
        previouslyFollowedTopics = (ArrayList<String>) getArguments().getStringArrayList("previouslyFollowedTopics");
        processTopicsDataForList();
        createTopicsData(null);
//        trendingArraylist = new ArrayList<>();
//        hitTrendingDataAPI();
        return view;
    }

    ArrayList<Topics> mDatalist = new ArrayList<>();

    private void processTopicsDataForList() {
        for (int i = 0; i < selectTopic.size(); i++) {
            if (selectTopic.get(i).getChild().size() == 0) {
                //terminal Topic. Eligible for tagging
                Topics tempTopic = new Topics();
                tempTopic.setId(selectTopic.get(i).getId());
                tempTopic.setDisplay_name(selectTopic.get(i).getDisplay_name());
                tempTopic.setIsSelected(selectTopic.get(i).isSelected());
                tempTopic.setTitle(selectTopic.get(i).getTitle());
                tempTopic.setPublicVisibility(selectTopic.get(i).getPublicVisibility());
                tempTopic.setShowInMenu(selectTopic.get(i).getShowInMenu());
                ArrayList<Topics> tempList = new ArrayList<>();
                tempList.add(tempTopic);
                selectTopic.get(i).setChild(tempList);
            }
            for (int j = 0; j < selectTopic.get(i).getChild().size(); j++) {
                if (selectTopic.get(i).getChild().get(j).isSelected()) {
                    selectedTopicsMap.put(selectTopic.get(i).getChild().get(j).getId(), selectTopic.get(i).getChild().get(j));
                }
            }

        }
        Log.d("dwa", "" + mDatalist);
    }

    private void populateTopicsList() {
        try {
            FileInputStream fileInputStream = getActivity().openFileInput(AppConstants.FOLLOW_UNFOLLOW_TOPICS_JSON_FILE);
            String fileContent = AppUtils.convertStreamToString(fileInputStream);
            FollowTopics[] res = new Gson().fromJson(fileContent, FollowTopics[].class);
            createTopicsData(res);
        } catch (FileNotFoundException e) {
            Crashlytics.logException(e);
            Log.d("FileNotFoundException", Log.getStackTraceString(e));

            showProgressDialog("Please wait");
            Retrofit retro = BaseApplication.getInstance().getRetrofit();
            final TopicsCategoryAPI topicsAPI = retro.create(TopicsCategoryAPI.class);

            Call<ResponseBody> call = topicsAPI.downloadCategoriesJSON();
            call.enqueue(downloadCategoriesJSONCallback);
        }
    }

    Callback<ResponseBody> downloadCategoriesJSONCallback = new Callback<ResponseBody>() {
        @Override
        public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
            if (response == null || response.body() == null) {
//                showToast("Something went wrong from server");
                return;
            }
            try {
                String resData = new String(response.body().bytes());
                JSONObject jsonObject = new JSONObject(resData);

                Retrofit retro = BaseApplication.getInstance().getConfigurableTimeoutRetrofit(3);
                final TopicsCategoryAPI topicsAPI = retro.create(TopicsCategoryAPI.class);
                String popularURL = jsonObject.getJSONObject("data").getJSONObject("result").getJSONObject("category").getString("popularLocation");
                Call<ResponseBody> caller = topicsAPI.downloadTopicsListForFollowUnfollow(popularURL);

                caller.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                        removeProgressDialog();
                        boolean writtenToDisk = AppUtils.writeResponseBodyToDisk(getActivity(), AppConstants.FOLLOW_UNFOLLOW_TOPICS_JSON_FILE, response.body());
                        Log.d("TopicsSplashActivity", "file download was a success? " + writtenToDisk);

                        try {
                            FileInputStream fileInputStream = getActivity().openFileInput(AppConstants.FOLLOW_UNFOLLOW_TOPICS_JSON_FILE);
                            String fileContent = AppUtils.convertStreamToString(fileInputStream);
                            FollowTopics[] res = new Gson().fromJson(fileContent, FollowTopics[].class);
                            createTopicsData(res);
                        } catch (FileNotFoundException e) {
                            Crashlytics.logException(e);
                            Log.d("FileNotFoundException", Log.getStackTraceString(e));
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        removeProgressDialog();
                        Crashlytics.logException(t);
                        Log.d("MC4KException", Log.getStackTraceString(t));
//                        showToast("Something went wrong while downloading topics");

//                        Intent intent = new Intent(TopicsSplashActivity.this, DashboardActivity.class);
//                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                        startActivity(intent);
//                        finish();
                    }
                });
            } catch (Exception e) {
                removeProgressDialog();
                Crashlytics.logException(e);
                Log.d("MC4KException", Log.getStackTraceString(e));
//                showToast(getString(R.string.went_wrong));
            }
        }

        @Override
        public void onFailure(Call<ResponseBody> call, Throwable t) {
            removeProgressDialog();
//            showToast(getString(R.string.went_wrong));
            Crashlytics.logException(t);
            Log.d("MC4KException", Log.getStackTraceString(t));
        }
    };

    private void createTopicsData(FollowTopics[] responseData) {
        try {
            searchTopicsSplashAdapter = new AddArticleTopicsTabAdapter(getActivity(), selectTopic, selectedTopicsMap, this);
            popularTopicsRecyclerView.setAdapter(searchTopicsSplashAdapter);
        } catch (Exception e) {
            Crashlytics.logException(e);
            Log.d("MC4kException", Log.getStackTraceString(e));
//            showToast(getString(R.string.went_wrong));
        }
    }

    @Override
    protected void updateUi(Response response) {

    }

    @Override
    public void onClick(View view, int position) {

    }
}
