package com.mycity4kids.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseFragment;
import com.kelltontech.utils.ConnectivityUtils;
import com.kelltontech.utils.ToastUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.editor.EditorPostActivity;
import com.mycity4kids.models.Topics;
import com.mycity4kids.models.response.ArticleListingResponse;
import com.mycity4kids.models.response.ArticleListingResult;
import com.mycity4kids.models.response.GroupsMembershipResponse;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.TopicsCategoryAPI;
import com.mycity4kids.ui.GroupMembershipStatus;
import com.mycity4kids.ui.activity.ArticleDetailsContainerActivity;
import com.mycity4kids.ui.activity.GroupDetailsActivity;
import com.mycity4kids.ui.activity.GroupsSummaryActivity;
import com.mycity4kids.ui.activity.LeafNodeTopicArticlesActivity;
import com.mycity4kids.ui.activity.ShortStoryContainerActivity;
import com.mycity4kids.ui.activity.TopicsListingActivity;
import com.mycity4kids.ui.adapter.MainArticleRecyclerViewAdapter;
import com.mycity4kids.utils.AppUtils;
import com.mycity4kids.utils.GroupIdCategoryMap;
import com.mycity4kids.widget.FeedNativeAd;

import org.apmem.tools.layouts.FlowLayout;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by hemant on 29/5/17.
 */
public class TopicsArticlesTabFragment extends BaseFragment implements View.OnClickListener, FeedNativeAd.AdLoadingListener, MainArticleRecyclerViewAdapter.RecyclerViewClickListener, GroupIdCategoryMap.GroupCategoryInterface, GroupMembershipStatus.IMembershipStatus {

    private int groupId;
    public String gpsubHeading, gpHeading, gpImageUrl;
    private int nextPageNumber = 1;
    private int limit = 15;
    private boolean isReuqestRunning = false;
    private boolean isLastPageReached = false;
    private int sortType = 0;
    private ArrayList<ArticleListingResult> mDatalist;
    private Topics currentSubTopic;
    private Topics selectedTopic;
    private boolean isHeaderVisible = false;
    private int pastVisiblesItems, visibleItemCount, totalItemCount;

    private MainArticleRecyclerViewAdapter recyclerAdapter;

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
    private RecyclerView recyclerView;
    private FeedNativeAd feedNativeAd;
    private RelativeLayout guideOverlay;
    private RelativeLayout writeArticleCell;
    private boolean showGuide = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.topics_articles_tab_fragment, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        noBlogsTextView = (TextView) view.findViewById(R.id.noBlogsTextView);
        mLodingView = (RelativeLayout) view.findViewById(R.id.relativeLoadingView);
        guideOverlay = (RelativeLayout) view.findViewById(R.id.guideOverlay);
        writeArticleCell = (RelativeLayout) view.findViewById(R.id.writeArticleCell);

        frameLayout = (FrameLayout) view.findViewById(R.id.frame_layout);
        frameLayout.getBackground().setAlpha(0);
        fabMenu = (FloatingActionsMenu) view.findViewById(R.id.fab_menu);
        popularSortFAB = (FloatingActionButton) view.findViewById(R.id.popularSortFAB);
        recentSortFAB = (FloatingActionButton) view.findViewById(R.id.recentSortFAB);
        fabSort = (FloatingActionButton) view.findViewById(R.id.fabSort);

        guideOverlay.setOnClickListener(this);
        writeArticleCell.setOnClickListener(this);
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

        if (getArguments() != null) {
            currentSubTopic = getArguments().getParcelable("currentSubTopic");
            selectedTopic = currentSubTopic;
        }

        mDatalist = new ArrayList<>();
        feedNativeAd = new FeedNativeAd(getActivity(), this, AppConstants.FB_AD_PLACEMENT_ARTICLE_LISTING);
        feedNativeAd.loadAds();
        recyclerAdapter = new MainArticleRecyclerViewAdapter(getActivity(), feedNativeAd, this, false, selectedTopic.getId() + "~" + selectedTopic.getDisplay_name());
        final LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        recyclerAdapter.setNewListData(mDatalist);
        recyclerView.setAdapter(recyclerAdapter);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int lineCount = 0;
        int width = displayMetrics.widthPixels;
        Log.d("\nsearchName", "*********" + currentSubTopic.getDisplay_name() + " measured width = " + width);

        headerRL = (RelativeLayout) view.findViewById(R.id.headerRL);
        if (currentSubTopic.getChild().size() == 1 && currentSubTopic.getChild().get(0).getId().equals(currentSubTopic.getId())) {
            //The child is same as the parent(this child is added for filters or follow unfollow logic)
            //this duplicate child might not be required here but leaving it unchanged for now.
            isHeaderVisible = false;
            headerRL.setVisibility(View.GONE);
        } else {
            headerRL.setVisibility(View.VISIBLE);
            flowLayout = (FlowLayout) headerRL.findViewById(R.id.flowLayout);
            expandImageView = (ImageView) headerRL.findViewById(R.id.expandImageView);

            final LinearLayout allSubsubLL = (LinearLayout) inflater.inflate(R.layout.sub_sub_topic_item, null);
            TextView allCatTextView = ((TextView) allSubsubLL.getChildAt(0));

            String allCategoryLabel = "";
            if (isAdded()) {
                allCategoryLabel = getString(R.string.all_categories_label);
            } else {
                allCategoryLabel = "ALL";
            }

            allCatTextView.setText(allCategoryLabel);
            allCatTextView.measure(0, 0);
            allSubsubLL.setTag(currentSubTopic);
            width = width - allCatTextView.getMeasuredWidth() - allSubsubLL.getPaddingLeft() - allSubsubLL.getPaddingRight();
            if (width < 0) {
                lineCount++;
                width = displayMetrics.widthPixels - allCatTextView.getMeasuredWidth() - allSubsubLL.getPaddingLeft() - allSubsubLL.getPaddingRight();
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
                allSubsubLL.setLayoutParams(layoutParams);
                expandImageView.setVisibility(View.VISIBLE);
                if (showGuide) {
                    guideOverlay.setVisibility(View.VISIBLE);
                    ((TopicsListingActivity) getActivity()).showGuideTopLayer();
                }
            } else {
                FlowLayout.LayoutParams layoutParams
                        = new FlowLayout.LayoutParams
                        (FlowLayout.LayoutParams.WRAP_CONTENT, FlowLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.setNewLine(false);
                allSubsubLL.setLayoutParams(layoutParams);
            }

            flowLayout.addView(allSubsubLL);
            allSubsubLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedTopic = (Topics) allSubsubLL.getTag();
                    openFilteredTopicArticles();

                }
            });

            for (int i = 0; i < currentSubTopic.getChild().size(); i++) {
                final LinearLayout subsubLL = (LinearLayout) inflater.inflate(R.layout.sub_sub_topic_item, null);
                TextView catTextView = ((TextView) subsubLL.getChildAt(0));
                catTextView.setText(currentSubTopic.getChild().get(i).getDisplay_name().toUpperCase());
                catTextView.measure(0, 0);
                subsubLL.setTag(currentSubTopic.getChild().get(i));
                width = width - catTextView.getMeasuredWidth() - subsubLL.getPaddingLeft() - subsubLL.getPaddingRight();
                if (width < 0) {
                    lineCount++;
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
                    if (showGuide) {
                        guideOverlay.setVisibility(View.VISIBLE);
                        ((TopicsListingActivity) getActivity()).showGuideTopLayer();
                    }
                } else {
                    FlowLayout.LayoutParams layoutParams
                            = new FlowLayout.LayoutParams
                            (FlowLayout.LayoutParams.WRAP_CONTENT, FlowLayout.LayoutParams.WRAP_CONTENT);
                    layoutParams.setNewLine(false);
                    subsubLL.setLayoutParams(layoutParams);
                }

                flowLayout.addView(subsubLL);
                subsubLL.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectedTopic = (Topics) subsubLL.getTag();
                        openFilteredTopicArticles();
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
                isHeaderVisible = true;
            } catch (Exception e) {

            }
        }
        getGroupIdForCurrentCategory();

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int pos = llm.findFirstVisibleItemPosition();
                if (llm.findViewByPosition(pos) != null) {
                    if (llm.findViewByPosition(pos).getTop() == 0 && pos == 0) {
                        if (isHeaderVisible)
                            headerRL.setVisibility(View.VISIBLE);
                    } else {
                        headerRL.setVisibility(View.GONE);
                    }
                }

                if (dy > 0) //check for scroll down
                {
                    visibleItemCount = llm.getChildCount();
                    totalItemCount = llm.getItemCount();
                    pastVisiblesItems = llm.findFirstVisibleItemPosition();

                    if (!isReuqestRunning && !isLastPageReached) {
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                            isReuqestRunning = true;
                            mLodingView.setVisibility(View.VISIBLE);
                            hitFilteredTopicsArticleListingApi(sortType);
                        }
                    }
                }
            }
        });

        return view;
    }


    private void openFilteredTopicArticles() {
        Intent intent = new Intent(getActivity(), LeafNodeTopicArticlesActivity.class);
        intent.putExtra("leafTopicParent", currentSubTopic);
        intent.putExtra("leafTopic", selectedTopic);
        startActivity(intent);
    }


    private void getGroupIdForCurrentCategory() {
        GroupIdCategoryMap groupIdCategoryMap = new GroupIdCategoryMap(selectedTopic.getId(), this);
        groupIdCategoryMap.getGroupIdForCurrentCategory();
    }

    @Override
    public void onGroupMappingResult(int groupId, String gpHeading, String gpsubHeading, String gpImageUrl) {
        this.groupId = groupId;
        this.gpHeading = gpHeading;
        this.gpsubHeading = gpsubHeading;
        this.gpImageUrl = gpImageUrl;
        recyclerAdapter.setGroupInfo(groupId, gpHeading, gpsubHeading, gpImageUrl);
        recyclerAdapter.notifyDataSetChanged();
        hitFilteredTopicsArticleListingApi(sortType);
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

        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        TopicsCategoryAPI topicsAPI = retrofit.create(TopicsCategoryAPI.class);

        int from = (nextPageNumber - 1) * limit + 1;
        Call<ArticleListingResponse> filterCall = topicsAPI.getArticlesForCategory(selectedTopic.getId(), sortType, from, from + limit - 1, SharedPrefUtils.getLanguageFilters(getActivity()));
        filterCall.enqueue(articleListingResponseCallback);
    }

    private Callback<ArticleListingResponse> articleListingResponseCallback = new Callback<ArticleListingResponse>() {
        @Override
        public void onResponse(Call<ArticleListingResponse> call, retrofit2.Response<ArticleListingResponse> response) {
            isReuqestRunning = false;
            if (mLodingView.getVisibility() == View.VISIBLE) {
                mLodingView.setVisibility(View.GONE);
            }
            if (response == null || response.body() == null) {
                return;
            }
            try {
                ArticleListingResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    processArticleListingResponse(responseData);
                } else {
                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4KException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<ArticleListingResponse> call, Throwable t) {
            if (mLodingView.getVisibility() == View.VISIBLE) {
                mLodingView.setVisibility(View.GONE);
            }
            Crashlytics.logException(t);
            Log.d("MC4KException", Log.getStackTraceString(t));
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
                writeArticleCell.setVisibility(View.VISIBLE);
                mDatalist = dataList;
                recyclerAdapter.setNewListData(mDatalist);
                recyclerAdapter.notifyDataSetChanged();
            }
        } else {
            writeArticleCell.setVisibility(View.GONE);
            if (nextPageNumber == 1) {
                mDatalist = dataList;
            } else {
                mDatalist.addAll(dataList);
            }
            recyclerAdapter.setNewListData(mDatalist);
            nextPageNumber = nextPageNumber + 1;
            recyclerAdapter.notifyDataSetChanged();
        }

    }

    @Override
    protected void updateUi(Response response) {

    }

    public void showGuideView() {
        showGuide = true;
        if (expandImageView != null && expandImageView.getVisibility() == View.VISIBLE) {
            guideOverlay.setVisibility(View.VISIBLE);
            ((TopicsListingActivity) getActivity()).showGuideTopLayer();
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.writeArticleCell:
                if (isAdded()) {
                    Intent intent1 = new Intent(getActivity(), EditorPostActivity.class);
                    Bundle bundle5 = new Bundle();
                    bundle5.putString(EditorPostActivity.TITLE_PARAM, "");
                    bundle5.putString(EditorPostActivity.CONTENT_PARAM, "");
                    bundle5.putString(EditorPostActivity.TITLE_PLACEHOLDER_PARAM,
                            getString(R.string.example_post_title_placeholder));
                    bundle5.putString(EditorPostActivity.CONTENT_PLACEHOLDER_PARAM,
                            getString(R.string.example_post_content_placeholder));
                    bundle5.putInt(EditorPostActivity.EDITOR_PARAM, EditorPostActivity.USE_NEW_EDITOR);
                    bundle5.putString("from", "TopicArticlesListingScreen");
                    intent1.putExtras(bundle5);
                    startActivity(intent1);
                }
                break;
            case R.id.guideOverlay:
                guideOverlay.setVisibility(View.GONE);
                ((TopicsListingActivity) getActivity()).hideGuideTopLayer();
                SharedPrefUtils.setCoachmarksShownFlag(getActivity(), "topics_article", true);
                break;
            case R.id.recentSortFAB:
                fabMenu.collapse();
                mDatalist.clear();
                recyclerAdapter.notifyDataSetChanged();
                sortType = 0;
                nextPageNumber = 1;
                getGroupIdForCurrentCategory();
//                hitFilteredTopicsArticleListingApi(0);
                break;
            case R.id.popularSortFAB:
                fabMenu.collapse();
                mDatalist.clear();
                recyclerAdapter.notifyDataSetChanged();
                sortType = 1;
                nextPageNumber = 1;
                getGroupIdForCurrentCategory();
//                hitFilteredTopicsArticleListingApi(1);
                break;
        }
    }

    @Override
    public void onFinishToLoadAds() {

    }

    @Override
    public void onErrorToLoadAd() {

    }

    @Override
    public void onRecyclerItemClick(View view, int position) {
        switch (view.getId()) {
//            case R.id.groupHeaderView:
//                if (groupId == 0) {
//                    Intent groupIntent = new Intent(getActivity(), DashboardActivity.class);
//                    groupIntent.putExtra("TabType", "group");
//                    startActivity(groupIntent);
//                    if (isAdded())
//                        getActivity().finish();
//                } else {
//                    GroupMembershipStatus groupMembershipStatus = new GroupMembershipStatus(this);
//                    groupMembershipStatus.checkMembershipStatus(groupId, SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId());
//                }
//                break;
            default:
                if ("1".equals(mDatalist.get(position).getContentType())) {
                    Intent intent = new Intent(getActivity(), ShortStoryContainerActivity.class);
                    intent.putExtra(Constants.ARTICLE_ID, mDatalist.get(position).getId());
                    intent.putExtra(Constants.AUTHOR_ID, mDatalist.get(position).getUserId());
                    intent.putExtra(Constants.BLOG_SLUG, mDatalist.get(position).getBlogPageSlug());
                    intent.putExtra(Constants.TITLE_SLUG, mDatalist.get(position).getTitleSlug());
                    intent.putExtra(Constants.ARTICLE_OPENED_FROM, "" + currentSubTopic.getParentName());
                    intent.putExtra(Constants.FROM_SCREEN, "TopicArticlesListingScreen");
                    intent.putExtra(Constants.AUTHOR, mDatalist.get(position).getUserId() + "~" + mDatalist.get(position).getUserName());

                    ArrayList<ArticleListingResult> filteredResult = AppUtils.getFilteredContentList(mDatalist, AppConstants.CONTENT_TYPE_SHORT_STORY);
                    intent.putParcelableArrayListExtra("pagerListData", filteredResult);
                    intent.putExtra(Constants.ARTICLE_INDEX, "" + AppUtils.getFilteredPosition(position, mDatalist, AppConstants.CONTENT_TYPE_SHORT_STORY));
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(getActivity(), ArticleDetailsContainerActivity.class);
                    intent.putExtra(Constants.ARTICLE_ID, mDatalist.get(position).getId());
                    intent.putExtra(Constants.AUTHOR_ID, mDatalist.get(position).getUserId());
                    intent.putExtra(Constants.BLOG_SLUG, mDatalist.get(position).getBlogPageSlug());
                    intent.putExtra(Constants.TITLE_SLUG, mDatalist.get(position).getTitleSlug());
                    intent.putExtra(Constants.ARTICLE_OPENED_FROM, "" + currentSubTopic.getParentName());
                    intent.putExtra(Constants.FROM_SCREEN, "TopicArticlesListingScreen");
                    intent.putExtra(Constants.AUTHOR, mDatalist.get(position).getUserId() + "~" + mDatalist.get(position).getUserName());

                    ArrayList<ArticleListingResult> filteredResult = AppUtils.getFilteredContentList(mDatalist, AppConstants.CONTENT_TYPE_ARTICLE);
                    intent.putParcelableArrayListExtra("pagerListData", filteredResult);
                    intent.putExtra(Constants.ARTICLE_INDEX, "" + AppUtils.getFilteredPosition(position, mDatalist, AppConstants.CONTENT_TYPE_ARTICLE));
                    startActivity(intent);
                }
                break;
        }

    }

    @Override
    public void onMembershipStatusFetchSuccess(GroupsMembershipResponse body, int groupId) {
        String userType = null;
        if (isAdded()) {
            if (body.getData().getResult() == null || body.getData().getResult().isEmpty()) {

            } else {
                if (body.getData().getResult().get(0).getIsAdmin() == 1) {
                    userType = AppConstants.GROUP_MEMBER_TYPE_ADMIN;
                } else if (body.getData().getResult().get(0).getIsModerator() == 1) {
                    userType = AppConstants.GROUP_MEMBER_TYPE_MODERATOR;
                }
            }

            if (body.getData().getResult() == null || body.getData().getResult().isEmpty()) {
                Intent intent = new Intent(getActivity(), GroupsSummaryActivity.class);
                intent.putExtra("groupId", groupId);
                intent.putExtra(AppConstants.GROUP_MEMBER_TYPE, userType);
                startActivity(intent);
            } else if (AppConstants.GROUP_MEMBERSHIP_STATUS_BLOCKED.equals(body.getData().getResult().get(0).getStatus())) {
                Toast.makeText(getActivity(), getString(R.string.groups_user_blocked_msg), Toast.LENGTH_SHORT).show();
            } else if (AppConstants.GROUP_MEMBERSHIP_STATUS_MEMBER.equals(body.getData().getResult().get(0).getStatus())) {
                Intent intent = new Intent(getActivity(), GroupDetailsActivity.class);
                intent.putExtra("groupId", groupId);
                intent.putExtra(AppConstants.GROUP_MEMBER_TYPE, userType);
                startActivity(intent);
            } else if (AppConstants.GROUP_MEMBERSHIP_STATUS_PENDING_MODERATION.equals(body.getData().getResult().get(0).getStatus())) {
                Intent intent = new Intent(getActivity(), GroupsSummaryActivity.class);
                intent.putExtra("groupId", groupId);
                intent.putExtra("pendingMembershipFlag", true);
                intent.putExtra(AppConstants.GROUP_MEMBER_TYPE, userType);
                startActivity(intent);
            } else {
                Intent intent = new Intent(getActivity(), GroupsSummaryActivity.class);
                intent.putExtra("groupId", groupId);
                intent.putExtra(AppConstants.GROUP_MEMBER_TYPE, userType);
                startActivity(intent);
            }
        }
    }

    @Override
    public void onMembershipStatusFetchFail() {

    }
}
