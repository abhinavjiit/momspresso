package com.mycity4kids.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mycity4kids.R;
import com.mycity4kids.animation.MyCityAnimationsUtil;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.base.BaseFragment;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.models.response.ContributorListResponse;
import com.mycity4kids.models.response.ContributorListResult;
import com.mycity4kids.models.response.LanguageConfigModel;
import com.mycity4kids.models.response.LanguageRanksModel;
import com.mycity4kids.models.response.UserDetailResponse;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.profile.UserProfileActivity;
import com.mycity4kids.retrofitAPIsInterfaces.BloggerDashboardAPI;
import com.mycity4kids.retrofitAPIsInterfaces.ContributorListAPI;
import com.mycity4kids.ui.activity.RankingActivity;
import com.mycity4kids.ui.adapter.RankingTopBloggerAdapter;
import com.mycity4kids.utils.AppUtils;
import com.mycity4kids.utils.ConnectivityUtils;
import com.mycity4kids.utils.StringUtils;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by hemant on 28/7/17.
 */
public class RankingInfoTabFragment extends BaseFragment implements View.OnClickListener,
        RankingTopBloggerAdapter.RecyclerViewClickListener {

    private ArrayList<LanguageRanksModel> multipleRankList = new ArrayList<>();
    private ArrayList<String> list;
    private ArrayList<LanguageConfigModel> languageConfigModelArrayList;
    private ArrayList<ContributorListResult> contributorListResults;
    private int sortType = 2;
    private String type = AppConstants.USER_TYPE_BLOGGER;
    private String langKey = "0";
    private String userId;
    private String authorId;

    private RankingTopBloggerAdapter topBloggerAdapter;

    private View view;
    private RelativeLayout rankingContainer;
    private TextView topBloggerLabelTV;
    private TextView myRankTextView;
    private TextView languageTextView;
    private TextView improveRankTextView;
    private RecyclerView topBloggerRecyclerView;
    private PopupMenu popup;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.ranking_info_tab_fragment, container, false);

        userId = SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId();

        authorId = getArguments().getString("authorId");
        if (StringUtils.isNullOrEmpty(authorId)) {
            authorId = userId;
        }

        rankingContainer = (RelativeLayout) view.findViewById(R.id.rankingContainer);
        myRankTextView = (TextView) view.findViewById(R.id.myRankTextView);
        languageTextView = (TextView) view.findViewById(R.id.languageTextView);
        improveRankTextView = (TextView) view.findViewById(R.id.improveRankTextView);
        topBloggerLabelTV = (TextView) view.findViewById(R.id.topBloggerLabel);
        topBloggerRecyclerView = (RecyclerView) view.findViewById(R.id.topBloggerRecyclerView);

        topBloggerLabelTV.setOnClickListener(this);
        improveRankTextView.setOnClickListener(this);

        //  String text=String.format(getResources().getString(R.string.ranking_top_bloggers),);

        contributorListResults = new ArrayList<>();
        topBloggerAdapter = new RankingTopBloggerAdapter(getActivity(), contributorListResults, this);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        topBloggerRecyclerView.setLayoutManager(layoutManager);
        topBloggerRecyclerView.setAdapter(topBloggerAdapter);
        getUserDetails();
        getTopBloggers(sortType, type);
        getAllLanguages();
        return view;
    }

    private void getUserDetails() {
        if (!ConnectivityUtils.isNetworkEnabled(getActivity())) {
            removeProgressDialog();
            return;
        }
        showProgressDialog("please wait ...");
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        // prepare call in Retrofit 2.0
        BloggerDashboardAPI bloggerDashboardApi = retrofit.create(BloggerDashboardAPI.class);
        Call<UserDetailResponse> call = bloggerDashboardApi.getBloggerData(authorId);
        call.enqueue(userDetailsResponseListener);
    }

    private void getAllLanguages() {
        popup = new PopupMenu(getActivity(), topBloggerLabelTV);
        popup.getMenuInflater().inflate(R.menu.analytics_pageviews_custom_date_popup, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).equals(item.getTitle())) {
                        topBloggerLabelTV.setText(
                                String.format(getString(R.string.ranking_top_bloggers), list.get(i).toUpperCase()));
                        langKey = languageConfigModelArrayList.get(i).getLangKey();
                        getTopBloggers(2, AppConstants.USER_TYPE_BLOGGER);
                    }
                }
                return true;
            }

        });
        popup.getMenu().removeItem(R.id.fixDays);
        popup.getMenu().removeItem(R.id.customDates);

        list = new ArrayList<String>();
        languageConfigModelArrayList = new ArrayList<>();
        list.add("English");
        LanguageConfigModel languageConfigModel = new LanguageConfigModel();
        languageConfigModel.setName("English");
        languageConfigModel.setDisplay_name("English");
        languageConfigModel.setId(AppConstants.LANG_KEY_ENGLISH);
        topBloggerLabelTV.setText(String.format(getString(R.string.ranking_top_bloggers), "ENGLISH"));

        languageConfigModelArrayList.add(languageConfigModel);
        try {
            FileInputStream fileInputStream = BaseApplication.getAppContext()
                    .openFileInput(AppConstants.LANGUAGES_JSON_FILE);
            String fileContent = AppUtils.convertStreamToString(fileInputStream);
            LinkedHashMap<String, LanguageConfigModel> retMap = new Gson().fromJson(
                    fileContent, new TypeToken<LinkedHashMap<String, LanguageConfigModel>>() {
                    }.getType()
            );
            Log.d("Map", "" + retMap.toString());
            for (final Map.Entry<String, LanguageConfigModel> entry : retMap.entrySet()) {
                list.add(entry.getValue().getDisplay_name());
                entry.getValue().setLangKey(entry.getKey());
                languageConfigModelArrayList.add(entry.getValue());
            }
        } catch (FileNotFoundException ffe) {
            FirebaseCrashlytics.getInstance().recordException(ffe);
            Log.d("MC4kException", Log.getStackTraceString(ffe));
        }

        for (int i = 0; i < list.size(); i++) {
            popup.getMenu().add(Menu.NONE, i + 1, i + 1, list.get(i));
        }
    }

    public void getTopBloggers(int sortType, String type) {
        showProgressDialog(getResources().getString(R.string.please_wait));
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        // prepare call in Retrofit 2.0
        ContributorListAPI contributorlistApi = retrofit.create(ContributorListAPI.class);
        if (!ConnectivityUtils.isNetworkEnabled(getActivity())) {
            removeProgressDialog();
            return;
        }
        Call<ContributorListResponse> call = contributorlistApi.getContributorList(5, sortType, type, langKey, "");
        call.enqueue(contributorListResponseCallback);

    }

    private Callback<UserDetailResponse> userDetailsResponseListener = new Callback<UserDetailResponse>() {
        @Override
        public void onResponse(Call<UserDetailResponse> call, retrofit2.Response<UserDetailResponse> response) {
            removeProgressDialog();
            if (response == null || null == response.body()) {
                return;
            }
            UserDetailResponse responseData = response.body();
            if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                if (responseData.getData().get(0).getResult().getRanks() == null
                        || responseData.getData().get(0).getResult().getRanks().size() == 0) {
                    myRankTextView.setText("--");
                    languageTextView.setText(getString(R.string.analytics_lang_rank, ""));
                } else if (responseData.getData().get(0).getResult().getRanks().size() < 2) {
                    myRankTextView.setText("" + responseData.getData().get(0).getResult().getRanks().get(0).getRank());
                    if (isAdded()) {
                        if (AppConstants.LANG_KEY_ENGLISH
                                .equals(responseData.getData().get(0).getResult().getRanks().get(0).getLangKey())) {
                            languageTextView.setText(getString(R.string.analytics_lang_rank, "ENGLISH"));
                        } else {
                            languageTextView.setText(getString(R.string.analytics_lang_rank,
                                    responseData.getData().get(0).getResult().getRanks().get(0).getLangValue()
                                            .toUpperCase()));
                        }
                    }
                } else {
                    for (int i = 0; i < responseData.getData().get(0).getResult().getRanks().size(); i++) {
                        if (AppConstants.LANG_KEY_ENGLISH
                                .equals(responseData.getData().get(0).getResult().getRanks().get(i).getLangKey())) {
                            multipleRankList.add(responseData.getData().get(0).getResult().getRanks().get(i));
                            break;
                        }
                    }
                    Collections.sort(responseData.getData().get(0).getResult().getRanks());
                    for (int i = 0; i < responseData.getData().get(0).getResult().getRanks().size(); i++) {
                        if (!AppConstants.LANG_KEY_ENGLISH
                                .equals(responseData.getData().get(0).getResult().getRanks().get(i).getLangKey())) {
                            multipleRankList.add(responseData.getData().get(0).getResult().getRanks().get(i));
                        }
                    }
                    if (isAdded()) {
                        MyCityAnimationsUtil.animate(getActivity(), rankingContainer, multipleRankList, 0, true);
                    }
                }
            }
        }

        @Override
        public void onFailure(Call<UserDetailResponse> call, Throwable t) {

        }
    };

    private Callback<ContributorListResponse> contributorListResponseCallback =
            new Callback<ContributorListResponse>() {
                @Override
                public void onResponse(Call<ContributorListResponse> call,
                        retrofit2.Response<ContributorListResponse> response) {
                    try {
                        removeProgressDialog();
                        ContributorListResponse responseModel = response.body();
                        if (responseModel.getCode() == 200 && Constants.SUCCESS.equals(responseModel.getStatus())) {
                            processResponse(responseModel);
                        }
                    } catch (Exception e) {
                        FirebaseCrashlytics.getInstance().recordException(e);
                        Log.d("MC4kException", Log.getStackTraceString(e));
                    }
                }

                @Override
                public void onFailure(Call<ContributorListResponse> call, Throwable t) {
                    removeProgressDialog();
                    FirebaseCrashlytics.getInstance().recordException(t);
                    Log.d("MC4kException", Log.getStackTraceString(t));
                }
            };

    public void processResponse(ContributorListResponse responseModel) {
        ArrayList<ContributorListResult> dataList = responseModel.getData().getResult();
        contributorListResults.clear();
        contributorListResults.addAll(dataList);
        topBloggerAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.topBloggerLabel:
                popup.show();
                break;
            case R.id.improveRankTextView:
                ImproveRankPageViewsSocialFragment analyticsStatsDialogFragment =
                        new ImproveRankPageViewsSocialFragment();
                Bundle b = new Bundle();
                b.putString(AppConstants.ANALYTICS_INFO_TYPE, AppConstants.ANALYTICS_INFO_RANK_CALCULATION);
                analyticsStatsDialogFragment.setArguments(b);
                ((RankingActivity) getActivity()).addFragment(analyticsStatsDialogFragment, b);
                break;
            default:
                break;

        }
    }

    @Override
    public void onClick(View view, int position) {
        ContributorListResult itemSelected = contributorListResults.get(position);
        Intent intent = new Intent(getActivity(), UserProfileActivity.class);
        intent.putExtra(Constants.USER_ID, itemSelected.getId());
        intent.putExtra(AppConstants.AUTHOR_NAME, itemSelected.getFirstName() + " " + itemSelected.getLastName());
        intent.putExtra(Constants.FROM_SCREEN, "Ranking");
        startActivity(intent);
    }
}
