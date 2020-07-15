package com.mycity4kids.ui.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.base.BaseFragment;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.editor.NewEditor;
import com.mycity4kids.models.Topics;
import com.mycity4kids.models.response.ArticleListingResponse;
import com.mycity4kids.models.response.ArticleListingResult;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.TopicsCategoryAPI;
import com.mycity4kids.ui.activity.ArticleDetailsContainerActivity;
import com.mycity4kids.ui.activity.LeafNodeTopicArticlesActivity;
import com.mycity4kids.ui.activity.ShortStoryContainerActivity;
import com.mycity4kids.ui.activity.TopicsListingActivity;
import com.mycity4kids.ui.adapter.MainArticleRecyclerViewAdapter;
import com.mycity4kids.utils.AppUtils;
import com.mycity4kids.utils.ConnectivityUtils;
import com.mycity4kids.utils.ToastUtils;
import java.util.ArrayList;
import org.apmem.tools.layouts.FlowLayout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by hemant on 29/5/17.
 */
public class TopicsArticlesTabFragment extends BaseFragment implements View.OnClickListener,
        MainArticleRecyclerViewAdapter.RecyclerViewClickListener {

    private int nextPageNumber = 1;
    private int limit = 15;
    private boolean isReuqestRunning = false;
    private boolean isLastPageReached = false;
    private int sortType = 0;
    private ArrayList<ArticleListingResult> articleListingResults;
    private Topics currentSubTopic;
    private Topics selectedTopic;
    private boolean isHeaderVisible = false;
    private int pastVisiblesItems;
    private int visibleItemCount;
    private int totalItemCount;

    private MainArticleRecyclerViewAdapter recyclerAdapter;
    SwipeRefreshLayout swipeRefreshLayout;
    private RelativeLayout loadingView;
    private FlowLayout flowLayout;
    private RelativeLayout headerRL;
    private ImageView expandImageView;
    private FrameLayout frameLayout;
    private FloatingActionsMenu fabMenu;
    private FloatingActionButton popularSortFab;
    private FloatingActionButton recentSortFab;
    private FloatingActionButton fabSort;
    private RecyclerView recyclerView;
    private RelativeLayout guideOverlay;
    private RelativeLayout writeArticleCell;
    private boolean showGuide = false;
    ShimmerFrameLayout shimmerFrameLayout;
    private String jsonMyObject;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.topics_articles_tab_fragment, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefresh);
        loadingView = view.findViewById(R.id.relativeLoadingView);
        guideOverlay = view.findViewById(R.id.guideOverlay);
        writeArticleCell = view.findViewById(R.id.writeArticleCell);
        shimmerFrameLayout = view.findViewById(R.id.shimmer1);

        frameLayout = view.findViewById(R.id.frame_layout);
        frameLayout.getBackground().setAlpha(0);
        fabMenu = view.findViewById(R.id.fab_menu);
        popularSortFab = view.findViewById(R.id.popularSortFAB);
        recentSortFab = view.findViewById(R.id.recentSortFAB);
        fabSort = view.findViewById(R.id.fabSort);

        guideOverlay.setOnClickListener(this);
        writeArticleCell.setOnClickListener(this);
        frameLayout.setVisibility(View.GONE);
        fabSort.setVisibility(View.GONE);
        popularSortFab.setOnClickListener(this);
        recentSortFab.setOnClickListener(this);
        fabSort.setOnClickListener(v -> {
            if (fabMenu.isExpanded()) {
                fabMenu.collapse();
            } else {
                fabMenu.expand();
            }
        });

        fabMenu.setOnFloatingActionsMenuUpdateListener(new FloatingActionsMenu.OnFloatingActionsMenuUpdateListener() {
            @Override
            public void onMenuExpanded() {
                frameLayout.getBackground().setAlpha(240);
                frameLayout.setOnTouchListener((v, event) -> {
                    fabMenu.collapse();
                    return true;
                });
            }

            @Override
            public void onMenuCollapsed() {
                frameLayout.getBackground().setAlpha(0);
                frameLayout.setOnTouchListener(null);
            }
        });
        swipeRefreshLayout.setOnRefreshListener(() -> {
            articleListingResults.clear();
            recyclerAdapter.notifyDataSetChanged();
            nextPageNumber = 1;
            hitFilteredTopicsArticleListingApi(sortType);
            swipeRefreshLayout.setRefreshing(false);
        });

        Bundle extras = getArguments();
        if (extras != null) {
            jsonMyObject = extras.getString("currentSubTopic");
        }
        currentSubTopic = new Gson().fromJson(jsonMyObject, Topics.class);
        selectedTopic = currentSubTopic;

        articleListingResults = new ArrayList<>();
        recyclerAdapter = new MainArticleRecyclerViewAdapter(getActivity(), this, false,
                selectedTopic.getId() + "~" + selectedTopic.getDisplay_name(), false);
        final LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(llm);
        recyclerAdapter.setNewListData(articleListingResults);
        recyclerView.setAdapter(recyclerAdapter);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int lineCount = 0;
        int width = displayMetrics.widthPixels;

        headerRL = (RelativeLayout) view.findViewById(R.id.headerRL);
        if (currentSubTopic.getChild().size() == 1 && currentSubTopic.getChild().get(0).getId()
                .equals(currentSubTopic.getId())) {
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
            width = width - allCatTextView.getMeasuredWidth() - allSubsubLL.getPaddingStart() - allSubsubLL
                    .getPaddingEnd();
            if (width < 0) {
                lineCount++;
                width = displayMetrics.widthPixels - allCatTextView.getMeasuredWidth() - allSubsubLL.getPaddingStart()
                        - allSubsubLL.getPaddingEnd();
                if (lineCount == 1) {
                    width = width - AppUtils.dpTopx(50) - expandImageView.getPaddingStart() - expandImageView
                            .getPaddingEnd();
                }
            }

            if (lineCount == 2) {
                lineCount++;
                FlowLayout.LayoutParams layoutParams = new FlowLayout.LayoutParams(FlowLayout.LayoutParams.WRAP_CONTENT,
                        FlowLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.setNewLine(true);
                allSubsubLL.setLayoutParams(layoutParams);
                expandImageView.setVisibility(View.VISIBLE);
                if (showGuide) {
                    guideOverlay.setVisibility(View.VISIBLE);
                    ((TopicsListingActivity) getActivity()).showGuideTopLayer();
                }
            } else {
                FlowLayout.LayoutParams layoutParams = new FlowLayout.LayoutParams(FlowLayout.LayoutParams.WRAP_CONTENT,
                        FlowLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.setNewLine(false);
                allSubsubLL.setLayoutParams(layoutParams);
            }

            flowLayout.addView(allSubsubLL);
            allSubsubLL.setOnClickListener(v -> {
                selectedTopic = (Topics) allSubsubLL.getTag();
                openFilteredTopicArticles();

            });

            for (int i = 0; i < currentSubTopic.getChild().size(); i++) {
                final LinearLayout subsubLL = (LinearLayout) inflater.inflate(R.layout.sub_sub_topic_item, null);
                TextView catTextView = ((TextView) subsubLL.getChildAt(0));
                catTextView.setText(currentSubTopic.getChild().get(i).getDisplay_name().toUpperCase());
                catTextView.measure(0, 0);
                subsubLL.setTag(currentSubTopic.getChild().get(i));
                width = width - catTextView.getMeasuredWidth() - subsubLL.getPaddingStart() - subsubLL.getPaddingEnd();
                if (width < 0) {
                    lineCount++;
                    width = displayMetrics.widthPixels - catTextView.getMeasuredWidth() - subsubLL.getPaddingStart()
                            - subsubLL.getPaddingEnd();
                    if (lineCount == 1) {
                        width = width - AppUtils.dpTopx(50) - expandImageView.getPaddingStart() - expandImageView
                                .getPaddingEnd();
                    }
                }

                if (lineCount == 2) {
                    lineCount++;
                    FlowLayout.LayoutParams layoutParams = new FlowLayout.LayoutParams(
                            FlowLayout.LayoutParams.WRAP_CONTENT, FlowLayout.LayoutParams.WRAP_CONTENT);
                    layoutParams.setNewLine(true);
                    subsubLL.setLayoutParams(layoutParams);
                    expandImageView.setVisibility(View.VISIBLE);
                    if (showGuide) {
                        guideOverlay.setVisibility(View.VISIBLE);
                        ((TopicsListingActivity) getActivity()).showGuideTopLayer();
                    }
                } else {
                    FlowLayout.LayoutParams layoutParams = new FlowLayout.LayoutParams(
                            FlowLayout.LayoutParams.WRAP_CONTENT, FlowLayout.LayoutParams.WRAP_CONTENT);
                    layoutParams.setNewLine(false);
                    subsubLL.setLayoutParams(layoutParams);
                }

                flowLayout.addView(subsubLL);
                subsubLL.setOnClickListener(v -> {
                    selectedTopic = (Topics) subsubLL.getTag();
                    openFilteredTopicArticles();
                });
            }

            if (lineCount == 0) {
                ViewGroup.LayoutParams layoutParams = flowLayout.getLayoutParams();
                layoutParams.height = AppUtils.dpTopx(50);
                flowLayout.setLayoutParams(layoutParams);
            }
            expandImageView.setOnClickListener(v -> {
                flowLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));
                expandImageView.setVisibility(View.INVISIBLE);
            });
            try {
                isHeaderVisible = true;
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                Log.d("MC4KException", Log.getStackTraceString(e));
            }
        }
        hitFilteredTopicsArticleListingApi(sortType);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int pos = llm.findFirstVisibleItemPosition();
                if (llm.findViewByPosition(pos) != null) {
                    if (llm.findViewByPosition(pos).getTop() == 0 && pos == 0) {
                        if (isHeaderVisible) {
                            headerRL.setVisibility(View.VISIBLE);
                        }
                    } else {
                        headerRL.setVisibility(View.GONE);
                    }
                }
                if (dy > 0) {
                    visibleItemCount = llm.getChildCount();
                    totalItemCount = llm.getItemCount();
                    pastVisiblesItems = llm.findFirstVisibleItemPosition();

                    if (!isReuqestRunning && !isLastPageReached) {
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                            isReuqestRunning = true;
                            loadingView.setVisibility(View.VISIBLE);
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
        intent.putExtra("leafTopicParent", new Gson().toJson(currentSubTopic));
        intent.putExtra("leafTopic", new Gson().toJson(selectedTopic));
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        shimmerFrameLayout.startShimmerAnimation();
    }

    private void hitFilteredTopicsArticleListingApi(int sortType) {
        if (isAdded()) {
            if (!ConnectivityUtils.isNetworkEnabled(BaseApplication.getAppContext())) {
                ToastUtils.showToast(BaseApplication.getAppContext(), getString(R.string.error_network));
                return;
            }

            Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
            TopicsCategoryAPI topicsApi = retrofit.create(TopicsCategoryAPI.class);

            int from = (nextPageNumber - 1) * limit + 1;
            Call<ArticleListingResponse> filterCall = topicsApi
                    .getArticlesForCategory(selectedTopic.getId(), sortType, from, from + limit - 1,
                            SharedPrefUtils.getLanguageFilters(BaseApplication.getAppContext()));
            filterCall.enqueue(articleListingResponseCallback);
        }
    }

    private Callback<ArticleListingResponse> articleListingResponseCallback = new Callback<ArticleListingResponse>() {
        @Override
        public void onResponse(Call<ArticleListingResponse> call, retrofit2.Response<ArticleListingResponse> response) {
            isReuqestRunning = false;
            if (loadingView.getVisibility() == View.VISIBLE) {
                loadingView.setVisibility(View.GONE);
            }
            if (response.body() == null) {
                return;
            }
            try {
                ArticleListingResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    processArticleListingResponse(responseData);
                    shimmerFrameLayout.stopShimmerAnimation();
                    shimmerFrameLayout.setVisibility(View.GONE);
                }
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                Log.d("MC4KException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<ArticleListingResponse> call, Throwable t) {
            if (loadingView.getVisibility() == View.VISIBLE) {
                loadingView.setVisibility(View.GONE);
            }
            FirebaseCrashlytics.getInstance().recordException(t);
            Log.d("MC4KException", Log.getStackTraceString(t));
        }
    };

    private void processArticleListingResponse(ArticleListingResponse responseData) {

        ArrayList<ArticleListingResult> dataList = responseData.getData().get(0).getResult();

        if (dataList.size() == 0) {
            isLastPageReached = false;
            if (null != articleListingResults && !articleListingResults.isEmpty()) {
                //No more next results for search from pagination
                isLastPageReached = true;
            } else {
                // No results for search
                writeArticleCell.setVisibility(View.VISIBLE);
                articleListingResults = dataList;
                recyclerAdapter.setNewListData(articleListingResults);
                recyclerAdapter.notifyDataSetChanged();
            }
        } else {
            writeArticleCell.setVisibility(View.GONE);
            if (nextPageNumber == 1) {
                articleListingResults = dataList;
            } else {
                articleListingResults.addAll(dataList);
            }
            recyclerAdapter.setNewListData(articleListingResults);
            nextPageNumber = nextPageNumber + 1;
            recyclerAdapter.notifyDataSetChanged();
        }

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
                    Intent intent = new Intent(getActivity(), NewEditor.class);
                    startActivity(intent);
                }
                break;
            case R.id.guideOverlay:
                guideOverlay.setVisibility(View.GONE);
                ((TopicsListingActivity) getActivity()).hideGuideTopLayer();
                SharedPrefUtils.setCoachmarksShownFlag(BaseApplication.getAppContext(), "topics_article", true);
                break;
            case R.id.recentSortFAB:
                shimmerFrameLayout.startShimmerAnimation();
                shimmerFrameLayout.setVisibility(View.VISIBLE);
                fabMenu.collapse();
                articleListingResults.clear();
                recyclerAdapter.notifyDataSetChanged();
                sortType = 0;
                nextPageNumber = 1;
                hitFilteredTopicsArticleListingApi(sortType);
                break;
            case R.id.popularSortFAB:
                shimmerFrameLayout.startShimmerAnimation();
                shimmerFrameLayout.setVisibility(View.VISIBLE);
                fabMenu.collapse();
                articleListingResults.clear();
                recyclerAdapter.notifyDataSetChanged();
                sortType = 1;
                nextPageNumber = 1;
                hitFilteredTopicsArticleListingApi(sortType);
                break;
            default:
                break;
        }
    }

    private void hitArticleListingSortByRecent() {
        shimmerFrameLayout.startShimmerAnimation();
        shimmerFrameLayout.setVisibility(View.VISIBLE);
        fabMenu.collapse();
        articleListingResults.clear();
        recyclerAdapter.notifyDataSetChanged();
        sortType = 0;
        nextPageNumber = 1;
        hitFilteredTopicsArticleListingApi(sortType);
    }

    private void hitArticleListingSortByPopular() {
        shimmerFrameLayout.startShimmerAnimation();
        shimmerFrameLayout.setVisibility(View.VISIBLE);
        fabMenu.collapse();
        articleListingResults.clear();
        recyclerAdapter.notifyDataSetChanged();
        sortType = 1;
        nextPageNumber = 1;
        hitFilteredTopicsArticleListingApi(sortType);
    }

    @Override
    public void onRecyclerItemClick(View view, int position) {
        switch (view.getId()) {
            default:
                if ("1".equals(articleListingResults.get(position).getContentType())) {
                    Intent intent = new Intent(getActivity(), ShortStoryContainerActivity.class);
                    intent.putExtra(Constants.ARTICLE_ID, articleListingResults.get(position).getId());
                    intent.putExtra(Constants.AUTHOR_ID, articleListingResults.get(position).getUserId());
                    intent.putExtra(Constants.BLOG_SLUG, articleListingResults.get(position).getBlogPageSlug());
                    intent.putExtra(Constants.TITLE_SLUG, articleListingResults.get(position).getTitleSlug());
                    intent.putExtra(Constants.ARTICLE_OPENED_FROM, "" + currentSubTopic.getParentName());
                    intent.putExtra(Constants.FROM_SCREEN, "TopicArticlesListingScreen");
                    intent.putExtra(Constants.AUTHOR,
                            articleListingResults.get(position).getUserId() + "~" + articleListingResults.get(position)
                                    .getUserName());
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(getActivity(), ArticleDetailsContainerActivity.class);
                    intent.putExtra(Constants.ARTICLE_ID, articleListingResults.get(position).getId());
                    intent.putExtra(Constants.AUTHOR_ID, articleListingResults.get(position).getUserId());
                    intent.putExtra(Constants.BLOG_SLUG, articleListingResults.get(position).getBlogPageSlug());
                    intent.putExtra(Constants.TITLE_SLUG, articleListingResults.get(position).getTitleSlug());
                    intent.putExtra(Constants.ARTICLE_OPENED_FROM, "" + currentSubTopic.getParentName());
                    intent.putExtra("id", selectedTopic.getId());
                    intent.putExtra(Constants.FROM_SCREEN, "TopicArticlesListingScreen");
                    intent.putExtra(Constants.AUTHOR,
                            articleListingResults.get(position).getUserId() + "~" + articleListingResults.get(position)
                                    .getUserName());
                    startActivity(intent);
                }
                break;
        }
    }


    public void showSortedByDialog() {
        if (getActivity() != null) {
            final Dialog dialog = new Dialog(getActivity());
            dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_sort_by);
            dialog.setCancelable(true);
            dialog.findViewById(R.id.linearSortByPopular).setOnClickListener(view -> {
                hitArticleListingSortByPopular();
                dialog.dismiss();
            });

            dialog.findViewById(R.id.linearSortByRecent).setOnClickListener(view -> {
                hitArticleListingSortByRecent();
                dialog.dismiss();
            });

            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.findViewById(R.id.textUpdate).setOnClickListener(view -> dialog.dismiss());
            dialog.show();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        shimmerFrameLayout.stopShimmerAnimation();
    }
}
