package com.mycity4kids.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseFragment;
import com.kelltontech.utils.ConnectivityUtils;
import com.kelltontech.utils.ToastUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.Topics;
import com.mycity4kids.models.response.ArticleListingResponse;
import com.mycity4kids.models.response.ArticleListingResult;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.TopicsCategoryAPI;
import com.mycity4kids.ui.activity.ArticleDetailsContainerActivity;
import com.mycity4kids.ui.adapter.MainArticleListingAdapter;
import com.mycity4kids.utils.AppUtils;

import org.apmem.tools.layouts.FlowLayout;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by hemant on 29/5/17.
 */
public class TopicsArticlesTabFragment extends BaseFragment implements View.OnClickListener {

    private int nextPageNumber = 1;
    private int limit = 15;
    private boolean isReuqestRunning = false;
    private boolean isLastPageReached = false;
    private int sortType = 0;
    private ArrayList<ArticleListingResult> mDatalist;
    private Topics currentSubTopic;
    private Topics selectedTopic;
    private boolean isHeaderVisible = false;

    private MainArticleListingAdapter adapter;

    private RelativeLayout mLodingView;
    private TextView noBlogsTextView;
    private FlowLayout flowLayout;
    private RelativeLayout headerRL;
    private ImageView expandImageView;
    private FrameLayout frameLayout;
    private FloatingActionsMenu fabMenu;
    private FloatingActionButton popularSortFAB;
    private FloatingActionButton recentSortFAB;
    private FloatingActionButton fabSort;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.topics_articles_tab_fragment, container, false);

        final ListView listView = (ListView) view.findViewById(R.id.scroll);
        noBlogsTextView = (TextView) view.findViewById(R.id.noBlogsTextView);
        mLodingView = (RelativeLayout) view.findViewById(R.id.relativeLoadingView);

        frameLayout = (FrameLayout) view.findViewById(R.id.frame_layout);
        frameLayout.getBackground().setAlpha(0);
        fabMenu = (FloatingActionsMenu) view.findViewById(R.id.fab_menu);
        popularSortFAB = (FloatingActionButton) view.findViewById(R.id.popularSortFAB);
        recentSortFAB = (FloatingActionButton) view.findViewById(R.id.recentSortFAB);
        fabSort = (FloatingActionButton) view.findViewById(R.id.fabSort);
        frameLayout.setVisibility(View.VISIBLE);
        fabSort.setVisibility(View.VISIBLE);
        popularSortFAB.setOnClickListener(this);
        recentSortFAB.setOnClickListener(this);
        fabSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fabMenu.isExpanded()) {
                    fabMenu.collapse();
                } else {
                    fabMenu.expand();
                }
            }
        });

        fabMenu.setOnFloatingActionsMenuUpdateListener(new FloatingActionsMenu.OnFloatingActionsMenuUpdateListener() {
            @Override
            public void onMenuExpanded() {
                frameLayout.getBackground().setAlpha(240);
                frameLayout.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        fabMenu.collapse();
                        return true;
                    }
                });
            }

            @Override
            public void onMenuCollapsed() {
                frameLayout.getBackground().setAlpha(0);
                frameLayout.setOnTouchListener(null);
            }
        });

        mDatalist = new ArrayList<>();
        adapter = new MainArticleListingAdapter(getActivity());
        adapter.setNewListData(mDatalist);
        listView.setAdapter(adapter);


        if (getArguments() != null) {
            currentSubTopic = getArguments().getParcelable("currentSubTopic");
            selectedTopic = currentSubTopic;
        }

        DisplayMetrics displayMetrics = new DisplayMetrics();

        ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int lineCount = 0;
        int width = displayMetrics.widthPixels;
        Log.d("\nsearchName", "*********" + currentSubTopic.getDisplay_name() + " measured width = " + width);


        if (currentSubTopic.getChild().size() == 1 && currentSubTopic.getChild().get(0).getId().equals(currentSubTopic.getId())) {
            //The child is same as the parent(this child is added for filters or follow unfollow logic)
            //this duplicate child might not be required here but leaving it unchanged for now.
            isHeaderVisible = false;
        } else {
            headerRL = (RelativeLayout) inflater.inflate(R.layout.topics_articles_list_header, null);
            flowLayout = (FlowLayout) headerRL.findViewById(R.id.flowLayout);
            expandImageView = (ImageView) headerRL.findViewById(R.id.expandImageView);

            for (int i = 0; i < currentSubTopic.getChild().size(); i++) {
                final LinearLayout subsubLL = (LinearLayout) inflater.inflate(R.layout.sub_sub_topic_item, null);
                TextView catTextView = ((TextView) subsubLL.getChildAt(0));
                catTextView.setText(currentSubTopic.getChild().get(i).getDisplay_name());
                catTextView.measure(0, 0);
                subsubLL.setTag(currentSubTopic.getChild().get(i));
                Log.d("dimensions", " *************** " + width + " ##### " + (catTextView.getMeasuredWidth() + subsubLL.getPaddingLeft() + subsubLL.getPaddingRight()) + " ----- " + catTextView.getText());
                width = width - catTextView.getMeasuredWidth() - subsubLL.getPaddingLeft() - subsubLL.getPaddingRight();
                if (width < 0) {
                    lineCount++;
                    Log.d("Its a new line", " *************** linecount =  " + lineCount);
                    width = displayMetrics.widthPixels - catTextView.getMeasuredWidth() - subsubLL.getPaddingLeft() - subsubLL.getPaddingRight();
                    if (lineCount == 1) {
                        width = width - AppUtils.dpTopx(50) - expandImageView.getPaddingLeft() - expandImageView.getPaddingRight();
                    }
                }

                if (lineCount == 2) {
                    lineCount++;
                    FlowLayout.LayoutParams layoutParams
                            = new FlowLayout.LayoutParams
                            (FlowLayout.LayoutParams.WRAP_CONTENT, FlowLayout.LayoutParams.WRAP_CONTENT);
                    layoutParams.setNewLine(true);
                    subsubLL.setLayoutParams(layoutParams);
                    expandImageView.setVisibility(View.VISIBLE);
                } else {
                    FlowLayout.LayoutParams layoutParams
                            = new FlowLayout.LayoutParams
                            (FlowLayout.LayoutParams.WRAP_CONTENT, FlowLayout.LayoutParams.WRAP_CONTENT);
                    layoutParams.setNewLine(false);
                    subsubLL.setLayoutParams(layoutParams);
//                expandImageView.setVisibility(View.VISIBLE);
                }

                flowLayout.addView(subsubLL);
                subsubLL.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        for (int i = 0; i < flowLayout.getChildCount(); i++) {
                            flowLayout.getChildAt(i).setSelected(false);
                        }
                        subsubLL.setSelected(true);
                        selectedTopic = (Topics) subsubLL.getTag();
                        nextPageNumber = 1;
                        Utils.pushFilterTopicArticlesEvent(getActivity(), "TopicArticlesListingScreen", SharedPrefUtils.getUserDetailModel(getActivity()).getDynamoId() + "",
                                selectedTopic.getId() + "~" + selectedTopic.getDisplay_name(), currentSubTopic.getId() + "~" + currentSubTopic.getDisplay_name());
                        hitFilteredTopicsArticleListingApi(sortType);
                    }
                });
            }

            if (lineCount == 0) {
                ViewGroup.LayoutParams layoutParams = flowLayout.getLayoutParams();
                layoutParams.height = AppUtils.dpTopx(50);
                flowLayout.setLayoutParams(layoutParams);
            }
            expandImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    flowLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                    expandImageView.setVisibility(View.INVISIBLE);
                }
            });
            try {
                listView.addHeaderView(headerRL);
                isHeaderVisible = true;
            } catch (Exception e) {

            }
        }

        hitFilteredTopicsArticleListingApi(sortType);
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                boolean loadMore = firstVisibleItem + visibleItemCount >= totalItemCount;
                if (visibleItemCount != 0 && loadMore && firstVisibleItem != 0 && !isReuqestRunning && !isLastPageReached) {
                    mLodingView.setVisibility(View.VISIBLE);
                    hitFilteredTopicsArticleListingApi(sortType);
                    isReuqestRunning = true;
                }
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ArticleListingResult parentingListData = (ArticleListingResult) adapterView.getItemAtPosition(i);
                if (parentingListData == null) {
                    return;
                }
                Intent intent = new Intent(getActivity(), ArticleDetailsContainerActivity.class);
                intent.putExtra(Constants.ARTICLE_ID, parentingListData.getId());
                intent.putExtra(Constants.AUTHOR_ID, parentingListData.getUserId());
                intent.putExtra(Constants.BLOG_SLUG, parentingListData.getBlogPageSlug());
                intent.putExtra(Constants.TITLE_SLUG, parentingListData.getTitleSlug());
                intent.putExtra(Constants.ARTICLE_OPENED_FROM, "" + currentSubTopic.getParentName());
                intent.putExtra(Constants.FROM_SCREEN, "TopicArticlesListingScreen");
                if (isHeaderVisible == true) {
                    intent.putExtra(Constants.ARTICLE_INDEX, "" + (i - 1));
                } else {
                    intent.putExtra(Constants.ARTICLE_INDEX, "" + i);
                }
                intent.putParcelableArrayListExtra("pagerListData", mDatalist);
                intent.putExtra(Constants.AUTHOR, parentingListData.getUserId() + "~" + parentingListData.getUserName());
                startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void hitFilteredTopicsArticleListingApi(int sortType) {
        if (!ConnectivityUtils.isNetworkEnabled(getActivity())) {
            ToastUtils.showToast(getActivity(), getString(R.string.error_network));
            return;
        }
        if (nextPageNumber == 1) {
//            progressBar.setVisibility(View.VISIBLE);
        }

        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        TopicsCategoryAPI topicsAPI = retrofit.create(TopicsCategoryAPI.class);

        int from = (nextPageNumber - 1) * limit + 1;
//                Utils.pushOpenArticleListingEvent(this, GTMEventType.ARTICLE_LISTING_CLICK_EVENT, fromScreen, SharedPrefUtils.getUserDetailModel(this).getDynamoId(), displayName + "~" + selectedTopics, "" + nextPageNumber);

//        Utils.pushOpenArticleListingEvent(this, GTMEventType.ARTICLE_LISTING_CLICK_EVENT, "fromScreen", SharedPrefUtils.getUserDetailModel(getActivity()).getDynamoId(), displayName + "~" + selectedTopics, "" + nextPageNumber);
        Call<ArticleListingResponse> filterCall = topicsAPI.getFilteredArticlesForCategories(selectedTopic.getId(), sortType, from, from + limit - 1, SharedPrefUtils.getLanguageFilters(getActivity()));
        filterCall.enqueue(articleListingResponseCallback);
    }

    private Callback<ArticleListingResponse> articleListingResponseCallback = new Callback<ArticleListingResponse>() {
        @Override
        public void onResponse(Call<ArticleListingResponse> call, retrofit2.Response<ArticleListingResponse> response) {
            isReuqestRunning = false;
//            progressBar.setVisibility(View.INVISIBLE);
            if (mLodingView.getVisibility() == View.VISIBLE) {
                mLodingView.setVisibility(View.GONE);
            }
            if (response == null || response.body() == null) {
//                showToast("Something went wrong from server");
                return;
            }
//            swipeRefreshLayout.setRefreshing(false);
            try {
                ArticleListingResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    processArticleListingResponse(responseData);
                } else {
//                    showToast(getString(R.string.went_wrong));
                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4KException", Log.getStackTraceString(e));
//                showToast(getString(R.string.went_wrong));
            }
        }

        @Override
        public void onFailure(Call<ArticleListingResponse> call, Throwable t) {
            if (mLodingView.getVisibility() == View.VISIBLE) {
                mLodingView.setVisibility(View.GONE);
            }
//            progressBar.setVisibility(View.INVISIBLE);
            Crashlytics.logException(t);
            Log.d("MC4KException", Log.getStackTraceString(t));
//            showToast(getString(R.string.went_wrong));
        }
    };

    private void processArticleListingResponse(ArticleListingResponse responseData) {

        ArrayList<ArticleListingResult> dataList = responseData.getData().get(0).getResult();

        if (dataList.size() == 0) {
            isLastPageReached = false;
            if (null != mDatalist && !mDatalist.isEmpty()) {
                //No more next results for search from pagination
                isLastPageReached = true;
            } else {
                // No results for search
                noBlogsTextView.setVisibility(View.VISIBLE);
                noBlogsTextView.setText("No articles found");
                mDatalist = dataList;
//                trendingTopicData.setArticleList(dataList);
                adapter.setNewListData(mDatalist);
                adapter.notifyDataSetChanged();
            }
        } else {
            noBlogsTextView.setVisibility(View.GONE);
            if (nextPageNumber == 1) {
                mDatalist = dataList;
//                trendingTopicData.setArticleList(dataList);
            } else {
                mDatalist.addAll(dataList);
//                trendingTopicData.getArticleList().addAll(dataList);
            }
            adapter.setNewListData(mDatalist);
            nextPageNumber = nextPageNumber + 1;
            adapter.notifyDataSetChanged();
        }

    }

    @Override
    protected void updateUi(Response response) {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.recentSortFAB:
//                Utils.pushSortListingEvent(FilteredTopicsArticleListingActivity.this, GTMEventType.SORT_LISTING_EVENT, SharedPrefUtils.getUserDetailModel(FilteredTopicsArticleListingActivity.this).getDynamoId(),
//                        listingType, "recent");
                fabMenu.collapse();
                mDatalist.clear();
                adapter.notifyDataSetChanged();
                sortType = 0;
                nextPageNumber = 1;
                hitFilteredTopicsArticleListingApi(0);
                break;
            case R.id.popularSortFAB:
//                Utils.pushSortListingEvent(FilteredTopicsArticleListingActivity.this, GTMEventType.SORT_LISTING_EVENT, SharedPrefUtils.getUserDetailModel(FilteredTopicsArticleListingActivity.this).getDynamoId(),
//                        listingType, "popular");
                fabMenu.collapse();
                mDatalist.clear();
                adapter.notifyDataSetChanged();
                sortType = 1;
                nextPageNumber = 1;
                hitFilteredTopicsArticleListingApi(1);
                break;
        }
    }

}
