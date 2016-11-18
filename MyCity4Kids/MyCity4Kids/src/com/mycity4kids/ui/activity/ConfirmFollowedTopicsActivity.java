package com.mycity4kids.ui.activity;

import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.models.Topics;
import com.mycity4kids.models.response.FollowUnfollowCategoriesResponse;
import com.mycity4kids.newmodels.FollowUnfollowCategoriesRequest;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.TopicsCategoryAPI;

import org.apmem.tools.layouts.FlowLayout;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by hemant on 10/11/16.
 */
public class ConfirmFollowedTopicsActivity extends BaseActivity {

    private FlowLayout rootView;
    private LayoutInflater mInflator;
    private TextView doneTextView, countTextView;
    private Toolbar mToolbar;
    private ArrayList<String> previouslyFollowedTopics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.confirm_followed_topics_activity);

        doneTextView = (TextView) findViewById(R.id.doneTextView);
        countTextView = (TextView) findViewById(R.id.countTextView);
        rootView = (FlowLayout) findViewById(R.id.rootView);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Confirm Topics");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        List<Topics> subTopicsList = getIntent().getParcelableArrayListExtra("topicsList");
        previouslyFollowedTopics = getIntent().getStringArrayListExtra("previouslyFollowedTopics");

        countTextView.setText(subTopicsList.size() + " topics selected ");
        mInflator = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final ArrayList<String> updateTopicList;
        Set<String> updateSet = new HashSet<>();

        //create datalist for updating the topics at backend
        //need to remove the previously selected topics (if they are not unselected)
        //as the API is toggle functionality and if the previously selected items are unselected add them to the dataset.
        for (int i = 0; i < subTopicsList.size(); i++) {
            updateSet.add(subTopicsList.get(i).getId());
        }
        for (int i = 0; i < previouslyFollowedTopics.size(); i++) {
            if (!updateSet.contains(previouslyFollowedTopics.get(i))) {
                updateSet.add(previouslyFollowedTopics.get(i));
            } else {
                updateSet.remove(previouslyFollowedTopics.get(i));
            }
        }
        updateTopicList = new ArrayList<>(updateSet);

        for (int i = 0; i < subTopicsList.size(); i++) {
            LinearLayout ll = (LinearLayout) mInflator.inflate(R.layout.search_topic_item, null);
            final TextView tv = (TextView) ((LinearLayout) ll.getChildAt(0)).getChildAt(0);
            tv.setText(subTopicsList.get(i).getDisplay_name());
            tv.setTag(subTopicsList.get(i));
            final LinearLayout ll_main = (LinearLayout) ll.getChildAt(0);
            ll_main.setBackgroundResource(R.drawable.topics_filled_bg);
            tv.setTextColor(ContextCompat.getColor(this, R.color.red_drawer_selected));
            rootView.addView(ll);
        }

        doneTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgressDialog(getResources().getString(R.string.please_wait));
                Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
                TopicsCategoryAPI topicsCategoryAPI = retrofit.create(TopicsCategoryAPI.class);
                FollowUnfollowCategoriesRequest followUnfollowCategoriesRequest = new FollowUnfollowCategoriesRequest();

                followUnfollowCategoriesRequest.setCategories(updateTopicList);
                Call<FollowUnfollowCategoriesResponse> categoriesResponseCall =
                        topicsCategoryAPI.followCategories(SharedPrefUtils.getUserDetailModel(ConfirmFollowedTopicsActivity.this).getDynamoId(), followUnfollowCategoriesRequest);
                categoriesResponseCall.enqueue(followUnfollowCategoriesResponseCallback);
            }
        });
    }

    private Callback<FollowUnfollowCategoriesResponse> followUnfollowCategoriesResponseCallback = new Callback<FollowUnfollowCategoriesResponse>() {
        @Override
        public void onResponse(Call<FollowUnfollowCategoriesResponse> call, retrofit2.Response<FollowUnfollowCategoriesResponse> response) {
            removeProgressDialog();
            if (response == null || null == response.body()) {
                NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                Crashlytics.logException(nee);
                showToast("Something went wrong from server");
                return;
            }
            try {
                FollowUnfollowCategoriesResponse responseData = (FollowUnfollowCategoriesResponse) response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    Intent intent = new Intent(ConfirmFollowedTopicsActivity.this, DashboardActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    showToast(responseData.getReason());
                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
                showToast(getString(R.string.went_wrong));
            }
        }

        @Override
        public void onFailure(Call<FollowUnfollowCategoriesResponse> call, Throwable t) {
            removeProgressDialog();
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
            showToast(getString(R.string.went_wrong));
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    protected void updateUi(Response response) {

    }
}
