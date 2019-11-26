package com.mycity4kids.ui.activity;

import android.accounts.NetworkErrorException;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;

import com.crashlytics.android.Crashlytics;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.models.response.BadgeListResponse;
import com.mycity4kids.profile.BadgesDialogFragment;
import com.mycity4kids.retrofitAPIsInterfaces.BadgeAPI;
import com.mycity4kids.ui.adapter.BadgeListGridAdapter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

public class BadgeActivity extends BaseActivity implements View.OnClickListener, BadgeListGridAdapter.BadgeSelect {
    private GridView gridview;
    private BadgeListGridAdapter adapter;
    private ArrayList<BadgeListResponse.BadgeListData.BadgeListResult> badgeList;
    private int limit = 10;
    private int pageNumber = 1;
    private boolean isLastPageReached = false;
    private boolean isReuqestRunning = false;
    private RelativeLayout mLodingView;
    private ProgressBar progressBar;
    private ImageView backImageView;
    private String userId;

    @Override
    protected void updateUi(Response response) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_badge);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        gridview = (GridView) findViewById(R.id.gridview);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        backImageView = findViewById(R.id.backImageView);
        mLodingView = (RelativeLayout) findViewById(R.id.relativeLoadingView);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        userId = getIntent().getStringExtra(Constants.USER_ID);
        if (StringUtils.isNullOrEmpty(userId)) {
            showToast(getString(R.string.empty_screen));
            return;
        }

        adapter = new BadgeListGridAdapter(this);
        adapter.setDatalist(badgeList);
        gridview.setAdapter(adapter);
        getBadgeList();
        showProgressDialog(getString(R.string.please_wait));

        gridview.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                boolean loadMore = firstVisibleItem + visibleItemCount >= totalItemCount;
                if (visibleItemCount != 0 && loadMore && firstVisibleItem != 0 && !isReuqestRunning && !isLastPageReached) {
                    mLodingView.setVisibility(View.VISIBLE);
                    //caching enabled only for page 1. so disabling it here for all other pages by passing false.

                    isReuqestRunning = true;
                }
            }
        });
    }

    private void getBadgeList() {
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        BadgeAPI badgeAPI = retrofit.create(BadgeAPI.class);
        Call<BadgeListResponse> badgeListResponseCall = badgeAPI.getBadgeList(userId);
        badgeListResponseCall.enqueue(badgeListCall);
    }

    private Callback<BadgeListResponse> badgeListCall = new Callback<BadgeListResponse>() {
        @Override
        public void onResponse(Call<BadgeListResponse> call, retrofit2.Response<BadgeListResponse> response) {
            progressBar.setVisibility(View.GONE);
            mLodingView.setVisibility(View.GONE);
            removeProgressDialog();
            isReuqestRunning = false;
            if (response.body() == null) {
                if (response.raw() != null) {
                    NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                    Crashlytics.logException(nee);
                }
                return;
            }
            BadgeListResponse responseModel = response.body();
            if (responseModel.getCode() == 200 && Constants.SUCCESS.equals(responseModel.getStatus())) {
                if (responseModel.getData() != null && !responseModel.getData().isEmpty() && responseModel.getData().get(0) != null) {
                    processResponse(responseModel.getData());
                } else {
                    showToast(responseModel.getReason());
                }
            }
        }

        @Override
        public void onFailure(Call<BadgeListResponse> call, Throwable t) {
            progressBar.setVisibility(View.GONE);
            mLodingView.setVisibility(View.GONE);
            removeProgressDialog();
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    private void processResponse(List<BadgeListResponse.BadgeListData> data) {
        ArrayList<BadgeListResponse.BadgeListData.BadgeListResult> datalist = data.get(0).getResult();
        if (datalist == null || datalist.size() == 0) {
            isLastPageReached = true;
            if (null != badgeList && !badgeList.isEmpty()) {
                // empty arraylist in subsequent api calls while pagination
            } else {
                badgeList = datalist;
                adapter.setDatalist(datalist);
                adapter.notifyDataSetChanged();
            }
        } else {
            if (pageNumber == 1) {
                badgeList = datalist;
            } else {
                badgeList.addAll(datalist);
            }
            adapter.setDatalist(badgeList);
            pageNumber = pageNumber + 1;
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backImageView:
                onBackPressed();
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBadgeSelected(String image_url, String share_url, int position) {
        BadgesDialogFragment badgesDialogFragment = new BadgesDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.USER_ID, userId);
        bundle.putString("id", badgeList.get(position).getId());
        badgesDialogFragment.setArguments(bundle);
        FragmentManager fm = getSupportFragmentManager();
        badgesDialogFragment.show(fm, "BadgeDetailDialog");
//        final Dialog dialog = new Dialog(BadgeActivity.this);
//        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
//        dialog.setContentView(R.layout.dialog_badge_share);
//        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        dialog.show();
//
//        ImageView badgeImg = dialog.findViewById(R.id.badgeImageView);
//        TextView badgeName = dialog.findViewById(R.id.badgeName);
//        TextView badgeDesc = dialog.findViewById(R.id.badgeDesc);
//
//        Picasso.with(this).load(badgeList.get(position).getBadge_image_url()).placeholder(R.drawable.default_article).error(R.drawable.default_article)
//                .fit().into(badgeImg);
//
//        badgeName.setText(badgeList.get(position).getBadge_title().getOther());
//        badgeDesc.setText(badgeList.get(position).getBadge_desc().getOther());
//
//
//        dialog.findViewById(R.id.shareBtn).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent shareIntent = new Intent(Intent.ACTION_SEND);
//                shareIntent.setType("text/plain");
//                shareIntent.putExtra(Intent.EXTRA_TEXT, share_url);
//                startActivity(Intent.createChooser(shareIntent, "Share URL"));
//                dialog.cancel();
//            }
//        });
//
//        dialog.show();
    }
}
