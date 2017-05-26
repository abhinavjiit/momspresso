package com.mycity4kids.ui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.Utils;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.models.response.BloggerAnalyticsResponse;
import com.mycity4kids.models.response.BloggerAnalyticsViews;
import com.mycity4kids.models.response.ContributorListResponse;
import com.mycity4kids.models.response.ContributorListResult;
import com.mycity4kids.models.response.LanguageRanksModel;
import com.mycity4kids.models.response.UserDetailResponse;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.BloggerDashboardAPI;
import com.mycity4kids.retrofitAPIsInterfaces.ContributorListAPI;
import com.mycity4kids.ui.fragment.AnalyticsStatsDialogFragment;
import com.mycity4kids.ui.fragment.PageViewsDateRangeDialogFragment;
import com.mycity4kids.utils.AppUtils;
import com.mycity4kids.widget.CustomViewFlipper;
import com.mycity4kids.widget.MyMarkerView;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by hemant on 9/12/16.
 */
public class BloggersAnalyticsActivity extends BaseActivity implements OnChartGestureListener, OnChartValueSelectedListener, View.OnClickListener {

    private LinearLayout topBloggersContainer;
    private Toolbar mToolbar;
    private TextView likesCountTextView, shareCountTextView, commentsCountTextView, rankTextView, followersTextView, pageViewCountTextView, daysCountTextView;
    private ImageView customDateMenuOption, pageViewsMenuOption, rankMenuOption, engagementMenuOption, followersMenuOption;
    private LayoutInflater mInflater;
    static LineChart mChart;
    static XAxis xAxis;
    private CustomViewFlipper rankViewFlipper;

    static String init_from_date, init_to_date;
    private String userId;
    static ArrayList<String> datesList;
    private static int colorCode;

    BloggerDashboardAPI bloggerDashboardAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bloggers_analytics_activity);
        com.mycity4kids.gtmutils.Utils.pushOpenScreenEvent(BloggersAnalyticsActivity.this, "Blog Analytics", SharedPrefUtils.getUserDetailModel(this).getDynamoId() + "");
        mInflater = LayoutInflater.from(this);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        daysCountTextView = (TextView) findViewById(R.id.daysCount);
        likesCountTextView = (TextView) findViewById(R.id.likesCount);
        shareCountTextView = (TextView) findViewById(R.id.sharesCount);
        commentsCountTextView = (TextView) findViewById(R.id.commentsCount);
        rankTextView = (TextView) findViewById(R.id.rankTextView);
        followersTextView = (TextView) findViewById(R.id.followersCount);
        pageViewCountTextView = (TextView) findViewById(R.id.pageViewCount);
        topBloggersContainer = (LinearLayout) findViewById(R.id.topBloggersContainer);

        customDateMenuOption = (ImageView) findViewById(R.id.customDateMenuOption);
        pageViewsMenuOption = (ImageView) findViewById(R.id.pageViewsMenuOption);
        rankMenuOption = (ImageView) findViewById(R.id.rankMenuOption);
        engagementMenuOption = (ImageView) findViewById(R.id.engagementMenuOption);
        followersMenuOption = (ImageView) findViewById(R.id.followersMenuOption);

        rankViewFlipper = (CustomViewFlipper) findViewById(R.id.rankViewFlipper);

        rankViewFlipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.in_from_top));
        rankViewFlipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.out_from_bottom));
        rankViewFlipper.setAutoStart(true);
        rankViewFlipper.setFlipInterval(3000);
        rankViewFlipper.startFlipping();

        customDateMenuOption.setOnClickListener(this);
        pageViewsMenuOption.setOnClickListener(this);
        rankMenuOption.setOnClickListener(this);
        engagementMenuOption.setOnClickListener(this);
        followersMenuOption.setOnClickListener(this);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.activity_analytics_toolbar_title));

        userId = SharedPrefUtils.getUserDetailModel(this).getDynamoId();
        colorCode = ContextCompat.getColor(BloggersAnalyticsActivity.this, R.color.analytics_engagement_graph);
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        bloggerDashboardAPI = retrofit.create(BloggerDashboardAPI.class);
        Call<UserDetailResponse> call = bloggerDashboardAPI.getBloggerData(userId);
        call.enqueue(bloggerDetailsResponseListener);

        datesList = new ArrayList<>();

        mChart = (LineChart) findViewById(R.id.pageViewsChart);

        mChart.getDescription().setEnabled(false);

        mChart.setOnChartGestureListener(this);
        mChart.setOnChartValueSelectedListener(this);
        mChart.setDrawGridBackground(false);

        // no description text
        mChart.getDescription().setEnabled(false);

        // enable touch gestures
        mChart.setTouchEnabled(true);

        // enable scaling and dragging
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);

        // if disabled, scaling can be done on x- and y-axis separately
        mChart.setPinchZoom(true);

        MyMarkerView mv = new MyMarkerView(this, R.layout.custom_marker_view);
        mv.setChartView(mChart); // For bounds control
        mChart.setMarker(mv);

        xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
        leftAxis.setDrawZeroLine(false);

        // limit lines are drawn behind data (and not on top)
        leftAxis.setDrawLimitLinesBehindData(true);

        mChart.getAxisRight().setEnabled(false);

        create30daysPageViewGraph();
        fetchTopBloggersList();
    }

    @Override
    protected void onPause() {
        super.onPause();
        rankViewFlipper.clearAnimation();
    }

    private void create30daysPageViewGraph() {

        daysCountTextView.setText(getString(R.string.analytics_popup_last30days));
        Calendar fromCal = Calendar.getInstance();
        fromCal.set(Calendar.HOUR_OF_DAY, 0);
        fromCal.set(Calendar.MINUTE, 0);
        fromCal.set(Calendar.SECOND, 0);
        fromCal.set(Calendar.MILLISECOND, 0);
        fromCal.add(Calendar.DATE, -31);
        fromCal.getTimeInMillis();
        init_from_date = new SimpleDateFormat("dd-MM-yyyy").format(fromCal.getTime());

        Calendar toCal = Calendar.getInstance();
        toCal.set(Calendar.HOUR_OF_DAY, 0);
        toCal.set(Calendar.MINUTE, 0);
        toCal.set(Calendar.SECOND, 0);
        toCal.set(Calendar.MILLISECOND, 0);
        toCal.add(Calendar.DATE, -1);
        toCal.getTimeInMillis();
        init_to_date = new SimpleDateFormat("dd-MM-yyyy").format(toCal.getTime());

        Call<BloggerAnalyticsResponse> callAnalytics = bloggerDashboardAPI.getAnalyticsReport(userId, "" + fromCal.getTimeInMillis() / 1000, "" + toCal.getTimeInMillis() / 1000);
        callAnalytics.enqueue(analyticsResponseListener);
    }

    private void fetchTopBloggersList() {
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        ContributorListAPI contributorListAPI = retrofit.create(ContributorListAPI.class);
        Call<ContributorListResponse> contributorListResponseCall = contributorListAPI.getContributorList(AppConstants.LIVE_URL + "v1/users/?limit=" + AppConstants.ANALYTICS_TOP_BLOGGERS_COUNT + "&sortType=" + AppConstants.CONTRIBUTOR_SORT_TYPE_RANK + "&type=" + AppConstants.USER_TYPE_BLOGGER + "&pagination=");
        contributorListResponseCall.enqueue(contributorListResponseCallback);
    }

    private static void setData(ArrayList<BloggerAnalyticsViews> viewsList) {

        ArrayList<Entry> values = new ArrayList<Entry>();

        for (int i = 0; i < viewsList.size(); i++) {
            values.add(new Entry(i, Float.parseFloat(viewsList.get(i).getViews())));
        }

        LineDataSet set1;

        if (mChart.getData() != null &&
                mChart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) mChart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            set1.setDrawValues(false);
            mChart.getData().notifyDataChanged();
            mChart.notifyDataSetChanged();
        } else {
            // create a dataset and give it a type
            set1 = new LineDataSet(values, "Page Views");

            set1.setColor(colorCode);
            set1.setCircleColor(colorCode);
            set1.setLineWidth(1f);
            set1.setCircleRadius(3f);
            set1.setDrawCircleHole(false);
            set1.setValueTextSize(9f);
            set1.setDrawValues(false);

            if (Utils.getSDKInt() >= 18) {
                // fill drawable only supported on api level 18 and above
//                Drawable drawable = ContextCompat.getDrawable(this, R.drawable.add_red);
                set1.setFillDrawable(null);
            } else {
                set1.setFillColor(Color.WHITE);
            }

            ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
            dataSets.add(set1); // add the datasets

            // create a data object with the datasets
            LineData data = new LineData(dataSets);

            // set data
            mChart.setData(data);
        }
    }

    private Callback<UserDetailResponse> bloggerDetailsResponseListener = new Callback<UserDetailResponse>() {
        @Override
        public void onResponse(Call<UserDetailResponse> call, retrofit2.Response<UserDetailResponse> response) {
            if (response == null || null == response.body()) {
                showToast("Something went wrong from server");
                return;
            }
            try {
                UserDetailResponse responseData = (UserDetailResponse) response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
//                    if (StringUtils.isNullOrEmpty(responseData.getData().get(0).getResult().getRank())) {
//                        rankTextView.setText("NA");
//                    } else {
//                        rankTextView.setText(responseData.getData().get(0).getResult().getRank());
//                    }
                    if (responseData.getData().get(0).getResult().getRanks() == null || responseData.getData().get(0).getResult().getRanks().size() == 0) {
                        LanguageRanksModel languageRanksModel = new LanguageRanksModel();
                        languageRanksModel.setRank(-1);
                        languageRanksModel.setLangKey("");
                        addRankView(languageRanksModel);
                        rankViewFlipper.setAutoStart(false);
                        rankViewFlipper.stopFlipping();
                    } else if (responseData.getData().get(0).getResult().getRanks().size() < 2) {
                        addRankView(responseData.getData().get(0).getResult().getRanks().get(0));
                        rankViewFlipper.setAutoStart(false);
                        rankViewFlipper.stopFlipping();
                    } else {
                        for (int i = 0; i < responseData.getData().get(0).getResult().getRanks().size(); i++) {
                            if (AppConstants.LANG_KEY_ENGLISH.equals(responseData.getData().get(0).getResult().getRanks().get(i).getLangKey())) {
                                addRankView(responseData.getData().get(0).getResult().getRanks().get(i));
                            }
                        }
                        Collections.sort(responseData.getData().get(0).getResult().getRanks());
                        for (int i = 0; i < responseData.getData().get(0).getResult().getRanks().size(); i++) {
                            if (!AppConstants.LANG_KEY_ENGLISH.equals(responseData.getData().get(0).getResult().getRanks().get(i).getLangKey())) {
                                addRankView(responseData.getData().get(0).getResult().getRanks().get(i));
                            }
                        }
                    }

                    int followerCount = Integer.parseInt(responseData.getData().get(0).getResult().getFollowersCount());
                    if (followerCount > 999) {
                        float singleFollowerCount = ((float) followerCount) / 1000;
                        followersTextView.setText("" + singleFollowerCount + "k");
                    } else {
                        followersTextView.setText("" + followerCount);
                    }

                } else {
                    if (!StringUtils.isNullOrEmpty(responseData.getReason())) {
                        showToast(responseData.getReason());
                    } else {
                        showToast(getString(R.string.server_went_wrong));
                    }
                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<UserDetailResponse> call, Throwable t) {
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    private Callback<BloggerAnalyticsResponse> analyticsResponseListener = new Callback<BloggerAnalyticsResponse>() {
        @Override
        public void onResponse(Call<BloggerAnalyticsResponse> call, retrofit2.Response<BloggerAnalyticsResponse> response) {
            if (response == null || null == response.body()) {
                showToast("Something went wrong from server");
                return;
            }
            try {
                BloggerAnalyticsResponse responseData = (BloggerAnalyticsResponse) response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    if (null == responseData.getData().getSocial().getLikes()) {
                        likesCountTextView.setText("0");
                    } else {
                        likesCountTextView.setText(responseData.getData().getSocial().getLikes());
                    }

                    if (null == responseData.getData().getSocial().getShare()) {
                        shareCountTextView.setText("0");
                    } else {
                        shareCountTextView.setText(responseData.getData().getSocial().getShare());
                    }

                    if (null == responseData.getData().getSocial().getComment()) {
                        commentsCountTextView.setText("0");
                    } else {
                        commentsCountTextView.setText(responseData.getData().getSocial().getComment());
                    }

                    int totalViews = 0;
                    for (int i = 0; i < responseData.getData().getViews().size(); i++) {
                        totalViews = totalViews + Integer.parseInt(responseData.getData().getViews().get(i).getViews());
                    }
                    pageViewCountTextView.setText("" + totalViews);
                    if (totalViews == 0) {
                        mChart.setVisibility(View.GONE);
                    } else {
                        changeDataset(responseData, getDatesL(responseData.getData().getViews()), "Last30Days");
//                        setData(responseData.getData().getViews());
                        mChart.animateX(2500);
                    }
                } else {
                    if (!StringUtils.isNullOrEmpty(responseData.getReason())) {
                        showToast(responseData.getReason());
                    } else {
                        showToast(getString(R.string.server_went_wrong));
                    }
                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<BloggerAnalyticsResponse> call, Throwable t) {
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    private ViewHolder holder;
    private Callback<ContributorListResponse> contributorListResponseCallback = new Callback<ContributorListResponse>() {
        @Override
        public void onResponse(Call<ContributorListResponse> call, retrofit2.Response<ContributorListResponse> response) {
            if (response == null || null == response.body()) {
                showToast("Something went wrong from server");
                return;
            }
            try {
                ContributorListResponse responseData = (ContributorListResponse) response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    ArrayList<ContributorListResult> dataList = responseData.getData().getResult();
                    holder = new ViewHolder();

                    for (int i = 0; i < dataList.size(); i++) {
                        View view = null;
                        if (holder != null) {
                            view = mInflater.inflate(R.layout.analytics_top_blogger_item, null);
                            holder.topContainer = (RelativeLayout) view.findViewById(R.id.topContainer);
                            holder.bloggerName = (TextView) view.findViewById(R.id.bloggerName);
                            holder.authorRank = (TextView) view.findViewById(R.id.rank);
                            holder.followersCount = (TextView) view.findViewById(R.id.followersCount);
                            holder.bloggerCover = (ImageView) view.findViewById(R.id.bloggerImageView);
                        }

                        view.setTag(dataList.get(i));
                        holder.bloggerName.setText(dataList.get(i).getFirstName() + " " + dataList.get(i).getLastName());
                        holder.followersCount.setText(dataList.get(i).getFollowersCount() + " followers");

                        Picasso.with(BloggersAnalyticsActivity.this).load(dataList.get(i).getProfilePic().getClientApp()).fit()
                                .placeholder(R.drawable.default_commentor_img).error(R.drawable.default_commentor_img).into(holder.bloggerCover);
                        if (!StringUtils.isNullOrEmpty(String.valueOf(dataList.get(i).getRank()))) {
                            holder.authorRank.setText(String.valueOf(dataList.get(i).getRank()));
                        } else {
                            holder.authorRank.setText("--");
                        }
                        holder.topContainer.setOnClickListener(BloggersAnalyticsActivity.this);

                        topBloggersContainer.addView(view);
                    }
                } else {
                    if (!StringUtils.isNullOrEmpty(responseData.getReason())) {
                        showToast(responseData.getReason());
                    } else {
                        showToast(getString(R.string.server_went_wrong));
                    }
                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<ContributorListResponse> call, Throwable t) {
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    private void addRankView(LanguageRanksModel languageRanksModel) {
        View rankItem = getLayoutInflater().inflate(R.layout.rank_flipping_item, null);
        TextView rankTextView = (TextView) rankItem.findViewById(R.id.rankingTextView);
        TextView rankLabelTextView = (TextView) rankItem.findViewById(R.id.rankingLabelTextView);

        rankTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
        rankLabelTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);

        if (languageRanksModel.getRank() == -1) {
            rankTextView.setText("--");
            rankLabelTextView.setText("Rank");
        } else {
            rankTextView.setText("" + languageRanksModel.getRank());
            if (AppConstants.LANG_KEY_ENGLISH.equals(languageRanksModel.getLangKey())) {
                rankLabelTextView.setText("Rank in English");
            } else {
                rankLabelTextView.setText("Rank in " + AppUtils.getLangModelForLanguage(BloggersAnalyticsActivity.this, languageRanksModel.getLangKey()).getDisplay_name());
            }
        }
        rankViewFlipper.addView(rankItem);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.customDateMenuOption:
                openCustomDatePickerMenu(customDateMenuOption);
                break;
            case R.id.pageViewsMenuOption:
                openPageViewOptionsMenu(pageViewsMenuOption);
                break;
            case R.id.rankMenuOption:
                openRankOptionsMenu(rankMenuOption);
                break;
            case R.id.engagementMenuOption:
                openEngagementOptionsMenu(engagementMenuOption);
                break;
            case R.id.followersMenuOption:
                openFollowersOptionsMenu(followersMenuOption);
                break;
            case R.id.topContainer:
                ContributorListResult contributorListResult = (ContributorListResult) v.getTag();
                Intent intent = new Intent(BloggersAnalyticsActivity.this, BloggerDashboardActivity.class);
                intent.putExtra(AppConstants.PUBLIC_PROFILE_USER_ID, contributorListResult.getId());
                intent.putExtra(AppConstants.AUTHOR_NAME, contributorListResult.getFirstName() + " " + contributorListResult.getLastName());
                intent.putExtra(Constants.FROM_SCREEN, "Blog Analytics");
                startActivity(intent);
                break;

        }
    }

    public void openCustomDatePickerMenu(ImageView profileImageView) {
        final PopupMenu popup = new PopupMenu(this, profileImageView);
        popup.getMenuInflater().inflate(R.menu.analytics_pageviews_custom_date_popup, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.fixDays) {
                    create30daysPageViewGraph();
                    return true;
                } else {
                    PageViewsDateRangeDialogFragment pageViewsDateRangeDialogFragment = new PageViewsDateRangeDialogFragment();
                    Bundle b = new Bundle();
                    b.putString("type", "from");
                    pageViewsDateRangeDialogFragment.setArguments(b);
                    pageViewsDateRangeDialogFragment.show(getSupportFragmentManager(), "datePicker");
                    return true;
                }
            }

        });
        popup.show();
    }

    private void openPageViewOptionsMenu(ImageView pageViewsMenuOption) {
        final PopupMenu popup = new PopupMenu(this, pageViewsMenuOption);
        popup.getMenuInflater().inflate(R.menu.analytics_pageviews_popup, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.improvePageViews) {
                    AnalyticsStatsDialogFragment analyticsStatsDialogFragment = new AnalyticsStatsDialogFragment();
                    Bundle b = new Bundle();
                    b.putString(AppConstants.ANALYTICS_INFO_TYPE, AppConstants.ANALYTICS_INFO_IMPROVE_PAGE_VIEWS);
                    analyticsStatsDialogFragment.setArguments(b);
                    analyticsStatsDialogFragment.show(getSupportFragmentManager(), "Analytics Info");
                    return true;
                }
                return true;
            }

        });
        popup.show();
    }

    private void openRankOptionsMenu(ImageView rankMenuOption) {
        final PopupMenu popup = new PopupMenu(this, rankMenuOption);
        popup.getMenuInflater().inflate(R.menu.analytics_ranks_popup, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.rankCalculation) {
                    AnalyticsStatsDialogFragment analyticsStatsDialogFragment = new AnalyticsStatsDialogFragment();
                    Bundle b = new Bundle();
                    b.putString(AppConstants.ANALYTICS_INFO_TYPE, AppConstants.ANALYTICS_INFO_RANK_CALCULATION);
                    analyticsStatsDialogFragment.setArguments(b);
                    analyticsStatsDialogFragment.show(getSupportFragmentManager(), "Analytics Info");
                    return true;
                } else {
                    AnalyticsStatsDialogFragment analyticsStatsDialogFragment = new AnalyticsStatsDialogFragment();
                    Bundle b = new Bundle();
                    b.putString(AppConstants.ANALYTICS_INFO_TYPE, AppConstants.ANALYTICS_INFO_IMPROVE_RANK);
                    analyticsStatsDialogFragment.setArguments(b);
                    analyticsStatsDialogFragment.show(getSupportFragmentManager(), "Analytics Info");
                    return true;
                }
            }

        });
        popup.show();
    }

    private void openEngagementOptionsMenu(ImageView engagementMenuOption) {
        final PopupMenu popup = new PopupMenu(this, engagementMenuOption);
        popup.getMenuInflater().inflate(R.menu.analytics_engagement_popup, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.improveEngagement) {
                    AnalyticsStatsDialogFragment analyticsStatsDialogFragment = new AnalyticsStatsDialogFragment();
                    Bundle b = new Bundle();
                    b.putString(AppConstants.ANALYTICS_INFO_TYPE, AppConstants.ANALYTICS_INFO_IMPROVE_SOCIAL_SHARE);
                    analyticsStatsDialogFragment.setArguments(b);
                    analyticsStatsDialogFragment.show(getSupportFragmentManager(), "Analytics Info");
                    return true;
                }
                return true;
            }

        });
        popup.show();
    }

    private void openFollowersOptionsMenu(ImageView followersMenuOption) {
        final PopupMenu popup = new PopupMenu(this, followersMenuOption);
        popup.getMenuInflater().inflate(R.menu.analytics_followers_popup, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.improveFollowers) {
                    AnalyticsStatsDialogFragment analyticsStatsDialogFragment = new AnalyticsStatsDialogFragment();
                    Bundle b = new Bundle();
                    b.putString(AppConstants.ANALYTICS_INFO_TYPE, AppConstants.ANALYTICS_INFO_INCREASE_FOLLOWERS);
                    analyticsStatsDialogFragment.setArguments(b);
                    analyticsStatsDialogFragment.show(getSupportFragmentManager(), "Analytics Info");
                    return true;
                }
                return true;
            }

        });
        popup.show();
    }

    public static List<String> getDatesL(ArrayList<BloggerAnalyticsViews> bloggerAnalyticsViewsList) {
        DateFormat df1 = new SimpleDateFormat("MMM dd");
        datesList.clear();

        for (int i = 0; i < bloggerAnalyticsViewsList.size(); i++) {
            Date date = new Date(Long.parseLong(bloggerAnalyticsViewsList.get(i).getDate()));
            String datee = df1.format(date);
            datesList.add(datee);
        }
        return datesList;
    }

    public void changeDataset(BloggerAnalyticsResponse bloggerAnalyticsResponse, final List<String> dList, String viewsDateType) {

        ArrayList<BloggerAnalyticsViews> list = bloggerAnalyticsResponse.getData().getViews();
        if (null == list || list.isEmpty()) {
            return;
        }
        Date date1 = new Date(Long.parseLong(list.get(0).getDate()));
        Date date2 = new Date(Long.parseLong(list.get(list.size() - 1).getDate()));

        if ("custom".equals(viewsDateType)) {
            daysCountTextView.setText(new SimpleDateFormat("dd-MMM-yyyy").format(date1) + " to " + new SimpleDateFormat("dd-MMM-yyyy").format(date2));
        } else {
            daysCountTextView.setText(getString(R.string.analytics_popup_last30days));
        }

        mChart.invalidate();

        int totalViews = 0;
        for (int i = 0; i < list.size(); i++) {
            totalViews = totalViews + Integer.parseInt(list.get(i).getViews());
        }
        pageViewCountTextView.setText("" + totalViews);

        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return dList.get((int) value);
            }
        });
        setData(list);
        mChart.animateX(2500);
    }

    public static class ViewHolder {
        RelativeLayout topContainer;
        TextView bloggerName;
        TextView authorRank;
        ImageView bloggerCover;
        TextView followersCount;
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
    protected void updateUi(Response response) {

    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {

    }

    @Override
    public void onNothingSelected() {

    }

    @Override
    public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

    }

    @Override
    public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

    }

    @Override
    public void onChartLongPressed(MotionEvent me) {

    }

    @Override
    public void onChartDoubleTapped(MotionEvent me) {

    }

    @Override
    public void onChartSingleTapped(MotionEvent me) {

    }

    @Override
    public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {

    }

    @Override
    public void onChartScale(MotionEvent me, float scaleX, float scaleY) {

    }

    @Override
    public void onChartTranslate(MotionEvent me, float dX, float dY) {

    }

}
