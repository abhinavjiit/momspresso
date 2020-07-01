package com.mycity4kids.ui.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.base.BaseFragment;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.editor.EditorPostActivity;
import com.mycity4kids.editor.NewEditor;
import com.mycity4kids.models.Topics;
import com.mycity4kids.models.response.ArticleListingResponse;
import com.mycity4kids.models.response.ArticleListingResult;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.TopicsCategoryAPI;
import com.mycity4kids.ui.activity.ArticleDetailsContainerActivity;
import com.mycity4kids.ui.activity.ShortStoryContainerActivity;
import com.mycity4kids.ui.activity.TopicsListingActivity;
import com.mycity4kids.ui.adapter.MainArticleRecyclerViewAdapter;
import com.mycity4kids.utils.AppUtils;
import com.mycity4kids.utils.StringUtils;
import java.util.ArrayList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by hemant on 29/5/17.
 */
public class LeafTopicArticlesTabFragment extends BaseFragment implements View.OnClickListener,
        MainArticleRecyclerViewAdapter.RecyclerViewClickListener {

    private static final String EDITOR_TYPE = "editor_type";
    private FirebaseRemoteConfig firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
    private int nextPageNumber = 1;
    private int limit = 15;
    private boolean isReuqestRunning = false;
    private boolean isLastPageReached = false;
    private int sortType = 0;
    private ArrayList<ArticleListingResult> mdatalist;
    private Topics currentSubTopic;
    private Topics selectedTopic;
    private int pastVisiblesItems;
    private int visibleItemCount;
    private int totalItemCount;

    private MainArticleRecyclerViewAdapter recyclerAdapter;
    private SwipeRefreshLayout swipeRefresh;
    private RelativeLayout loadingView;
    private FrameLayout frameLayout;
    private FloatingActionsMenu fabMenu;
    private FloatingActionButton popularSortFab;
    private FloatingActionButton recentSortFab;
    private FloatingActionButton fabSort;
    private RecyclerView recyclerView;
    private RelativeLayout guideOverlay;
    private RelativeLayout writeArticleCell;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.topics_articles_tab_fragment, container, false);
        swipeRefresh = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefresh);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        loadingView = (RelativeLayout) view.findViewById(R.id.relativeLoadingView);
        guideOverlay = (RelativeLayout) view.findViewById(R.id.guideOverlay);
        writeArticleCell = (RelativeLayout) view.findViewById(R.id.writeArticleCell);

        frameLayout = (FrameLayout) view.findViewById(R.id.frame_layout);
        frameLayout.getBackground().setAlpha(0);
        fabMenu = (FloatingActionsMenu) view.findViewById(R.id.fab_menu);
        popularSortFab = (FloatingActionButton) view.findViewById(R.id.popularSortFAB);
        recentSortFab = (FloatingActionButton) view.findViewById(R.id.recentSortFAB);
        fabSort = (FloatingActionButton) view.findViewById(R.id.fabSort);

        guideOverlay.setOnClickListener(this);
        writeArticleCell.setOnClickListener(this);
        frameLayout.setVisibility(View.VISIBLE);
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

        swipeRefresh.setOnRefreshListener(() -> {
            mdatalist.clear();
            recyclerAdapter.notifyDataSetChanged();
            nextPageNumber = 1;
            hitFilteredTopicsArticleListingApi(sortType);
            swipeRefresh.setRefreshing(false);
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

        if (getArguments() != null) {
            currentSubTopic = getArguments().getParcelable("currentSubTopic");
            selectedTopic = currentSubTopic;
        }

        mdatalist = new ArrayList<>();
        recyclerAdapter = new MainArticleRecyclerViewAdapter(getActivity(), this, false,
                selectedTopic.getId() + "~" + selectedTopic.getDisplay_name(), false);
        final LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(llm);
        recyclerAdapter.setNewListData(mdatalist);
        recyclerView.setAdapter(recyclerAdapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
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
        hitFilteredTopicsArticleListingApi(sortType);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void hitFilteredTopicsArticleListingApi(int sortType) {
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        TopicsCategoryAPI topicsApi = retrofit.create(TopicsCategoryAPI.class);

        int from = (nextPageNumber - 1) * limit + 1;
        Call<ArticleListingResponse> filterCall = topicsApi
                .getArticlesForCategory(selectedTopic.getId(), sortType, from, from + limit - 1,
                        SharedPrefUtils.getLanguageFilters(BaseApplication.getAppContext()));
        filterCall.enqueue(articleListingResponseCallback);
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
            if (null != mdatalist && !mdatalist.isEmpty()) {
                //No more next results for search from pagination
                isLastPageReached = true;
            } else {
                // No results for search
                writeArticleCell.setVisibility(View.VISIBLE);
                mdatalist = dataList;
                recyclerAdapter.setNewListData(mdatalist);
                recyclerAdapter.notifyDataSetChanged();
            }
        } else {
            writeArticleCell.setVisibility(View.GONE);
            if (nextPageNumber == 1) {
                mdatalist = dataList;
            } else {
                mdatalist.addAll(dataList);
            }
            recyclerAdapter.setNewListData(mdatalist);
            nextPageNumber = nextPageNumber + 1;
            recyclerAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.writeArticleCell:
                if (isAdded()) {
                    String editorType = firebaseRemoteConfig.getString(EDITOR_TYPE);
                    if ((!StringUtils.isNullOrEmpty(editorType) && "1".equals(editorType)) || AppUtils
                            .isUserBucketedInNewEditor(firebaseRemoteConfig)) {
                        Bundle bundle5 = new Bundle();
                        bundle5.putString("TITLE_PARAM", "");
                        bundle5.putString("CONTENT_PARAM", "");
                        bundle5.putString("TITLE_PLACEHOLDER_PARAM",
                                getString(R.string.example_post_title_placeholder));
                        bundle5.putString("CONTENT_PLACEHOLDER_PARAM",
                                getString(R.string.example_post_content_placeholder));
                        bundle5.putInt("EDITOR_PARAM", NewEditor.USE_NEW_EDITOR);
                        bundle5.putString("from", "TopicArticlesListingScreen");
                        Intent intent1 = new Intent(getActivity(), NewEditor.class);
                        intent1.putExtras(bundle5);
                        startActivity(intent1);
                    } else {
                        Bundle bundle5 = new Bundle();
                        bundle5.putString(EditorPostActivity.TITLE_PARAM, "");
                        bundle5.putString(EditorPostActivity.CONTENT_PARAM, "");
                        bundle5.putString(EditorPostActivity.TITLE_PLACEHOLDER_PARAM,
                                getString(R.string.example_post_title_placeholder));
                        bundle5.putString(EditorPostActivity.CONTENT_PLACEHOLDER_PARAM,
                                getString(R.string.example_post_content_placeholder));
                        bundle5.putInt(EditorPostActivity.EDITOR_PARAM, EditorPostActivity.USE_NEW_EDITOR);
                        bundle5.putString("from", "TopicArticlesListingScreen");
                        Intent intent1 = new Intent(getActivity(), EditorPostActivity.class);
                        intent1.putExtras(bundle5);
                        startActivity(intent1);
                    }
                }
                break;
            case R.id.guideOverlay:
                guideOverlay.setVisibility(View.GONE);
                ((TopicsListingActivity) getActivity()).hideGuideTopLayer();
                SharedPrefUtils.setCoachmarksShownFlag(BaseApplication.getAppContext(), "topics_article", true);
                break;
            case R.id.recentSortFAB:
                fabMenu.collapse();
                mdatalist.clear();
                recyclerAdapter.notifyDataSetChanged();
                sortType = 0;
                nextPageNumber = 1;
                break;
            case R.id.popularSortFAB:
                fabMenu.collapse();
                mdatalist.clear();
                recyclerAdapter.notifyDataSetChanged();
                sortType = 1;
                nextPageNumber = 1;
                break;
            default:
                break;
        }
    }

    @Override
    public void onRecyclerItemClick(View view, int position) {
        switch (view.getId()) {
            default:
                if ("1".equals(mdatalist.get(position).getContentType())) {
                    Intent intent = new Intent(getActivity(), ShortStoryContainerActivity.class);
                    intent.putExtra(Constants.ARTICLE_ID, mdatalist.get(position).getId());
                    intent.putExtra(Constants.AUTHOR_ID, mdatalist.get(position).getUserId());
                    intent.putExtra(Constants.BLOG_SLUG, mdatalist.get(position).getBlogPageSlug());
                    intent.putExtra(Constants.TITLE_SLUG, mdatalist.get(position).getTitleSlug());
                    intent.putExtra(Constants.ARTICLE_OPENED_FROM, "" + currentSubTopic.getParentName());
                    intent.putExtra(Constants.FROM_SCREEN, "TopicArticlesListingScreen");
                    intent.putExtra(Constants.AUTHOR,
                            mdatalist.get(position).getUserId() + "~" + mdatalist.get(position).getUserName());
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(getActivity(), ArticleDetailsContainerActivity.class);
                    intent.putExtra(Constants.ARTICLE_ID, mdatalist.get(position).getId());
                    intent.putExtra(Constants.AUTHOR_ID, mdatalist.get(position).getUserId());
                    intent.putExtra(Constants.BLOG_SLUG, mdatalist.get(position).getBlogPageSlug());
                    intent.putExtra(Constants.TITLE_SLUG, mdatalist.get(position).getTitleSlug());
                    intent.putExtra(Constants.ARTICLE_OPENED_FROM, "" + currentSubTopic.getParentName());
                    intent.putExtra(Constants.FROM_SCREEN, "TopicArticlesListingScreen");
                    intent.putExtra(Constants.AUTHOR,
                            mdatalist.get(position).getUserId() + "~" + mdatalist.get(position).getUserName());
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
                mdatalist.clear();
                recyclerAdapter.notifyDataSetChanged();
                sortType = 1;
                nextPageNumber = 1;
                hitFilteredTopicsArticleListingApi(sortType);
                dialog.dismiss();
            });

            dialog.findViewById(R.id.linearSortByRecent).setOnClickListener(view -> {
                mdatalist.clear();
                recyclerAdapter.notifyDataSetChanged();
                sortType = 0;
                nextPageNumber = 1;
                hitFilteredTopicsArticleListingApi(sortType);
                dialog.dismiss();
            });

            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.findViewById(R.id.textUpdate).setOnClickListener(view -> dialog.dismiss());

            dialog.show();
        }

    }
}
