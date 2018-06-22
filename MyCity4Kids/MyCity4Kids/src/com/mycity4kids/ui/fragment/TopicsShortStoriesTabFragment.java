package com.mycity4kids.ui.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseFragment;
import com.kelltontech.utils.ConnectivityUtils;
import com.kelltontech.utils.StringUtils;
import com.kelltontech.utils.ToastUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.Topics;
import com.mycity4kids.models.request.RecommendUnrecommendArticleRequest;
import com.mycity4kids.models.response.ArticleListingResponse;
import com.mycity4kids.models.response.ArticleListingResult;
import com.mycity4kids.models.response.RecommendUnrecommendArticleResponse;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.ArticleDetailsAPI;
import com.mycity4kids.retrofitAPIsInterfaces.TopicsCategoryAPI;
import com.mycity4kids.ui.activity.AddShortStoryActivity;
import com.mycity4kids.ui.activity.BloggerProfileActivity;
import com.mycity4kids.ui.activity.DashboardActivity;
import com.mycity4kids.ui.activity.ShortStoryContainerActivity;
import com.mycity4kids.ui.activity.TopicsListingFragment;
import com.mycity4kids.ui.adapter.ShortStoriesRecyclerAdapter;
import com.mycity4kids.utils.AppUtils;
import com.mycity4kids.widget.FeedNativeAd;

import org.apmem.tools.layouts.FlowLayout;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by hemant on 29/5/17.
 */
public class TopicsShortStoriesTabFragment extends BaseFragment implements View.OnClickListener, ShortStoriesRecyclerAdapter.RecyclerViewClickListener {

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

    private ShortStoriesRecyclerAdapter recyclerAdapter;
//    private MainArticleListingAdapter adapter;

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
    private String userDynamoId;
    private View shareSSView;
    private TextView titleTextView, bodyTextView, authorTextView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.topics_short_stories_tab_fragment, container, false);

        userDynamoId = SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId();

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        noBlogsTextView = (TextView) view.findViewById(R.id.noBlogsTextView);
        mLodingView = (RelativeLayout) view.findViewById(R.id.relativeLoadingView);
        guideOverlay = (RelativeLayout) view.findViewById(R.id.guideOverlay);
        writeArticleCell = (RelativeLayout) view.findViewById(R.id.writeArticleCell);
        frameLayout = (FrameLayout) view.findViewById(R.id.frame_layout);
        shareSSView = view.findViewById(R.id.shareSSView);
        titleTextView = (TextView) view.findViewById(R.id.titleTextView);
        bodyTextView = (TextView) view.findViewById(R.id.bodyTextView);
        authorTextView = (TextView) view.findViewById(R.id.authorTextView);

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

        mDatalist = new ArrayList<>();
        recyclerAdapter = new ShortStoriesRecyclerAdapter(getActivity(), this);
        final LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        recyclerAdapter.setListData(mDatalist);
        recyclerView.setAdapter(recyclerAdapter);

        if (getArguments() != null) {
            currentSubTopic = getArguments().getParcelable("currentSubTopic");
            selectedTopic = currentSubTopic;
        }


        hitFilteredTopicsArticleListingApi(sortType);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
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

        //Just For First Time -- Since complete text is not rendered in the bitmap
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

        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        TopicsCategoryAPI topicsAPI = retrofit.create(TopicsCategoryAPI.class);

        int from = (nextPageNumber - 1) * limit + 1;
        Call<ArticleListingResponse> filterCall = topicsAPI.getArticlesForCategory(selectedTopic.getId(), sortType, from, from + limit - 1, "0");

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
//                noBlogsTextView.setVisibility(View.VISIBLE);
//                noBlogsTextView.setText(getString(R.string.no_articles_found));
                writeArticleCell.setVisibility(View.VISIBLE);
                mDatalist = dataList;
                recyclerAdapter.setListData(mDatalist);
                recyclerAdapter.notifyDataSetChanged();
            }
        } else {
//            noBlogsTextView.setVisibility(View.GONE);
            writeArticleCell.setVisibility(View.GONE);
            if (nextPageNumber == 1) {
                mDatalist = dataList;
            } else {
                mDatalist.addAll(dataList);
            }
            recyclerAdapter.setListData(mDatalist);
            nextPageNumber = nextPageNumber + 1;
            recyclerAdapter.notifyDataSetChanged();
        }

    }

    @Override
    protected void updateUi(Response response) {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.writeArticleCell:
                if (isAdded()) {
                    Intent intent = new Intent(getActivity(), AddShortStoryActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.guideOverlay:
                guideOverlay.setVisibility(View.GONE);
                TopicsListingFragment frag = ((TopicsListingFragment) this.getParentFragment());
                frag.hideTabLayer();
                if (isAdded()) {
                    ((DashboardActivity) getActivity()).hideToolbarAndNavigationLayer();
                    SharedPrefUtils.setCoachmarksShownFlag(getActivity(), "topics_article", true);
                }
                break;
            case R.id.recentSortFAB:
                fabMenu.collapse();
                mDatalist.clear();
                recyclerAdapter.notifyDataSetChanged();
                sortType = 0;
                nextPageNumber = 1;
                hitFilteredTopicsArticleListingApi(0);
                break;
            case R.id.popularSortFAB:
                fabMenu.collapse();
                mDatalist.clear();
                recyclerAdapter.notifyDataSetChanged();
                sortType = 1;
                nextPageNumber = 1;
                hitFilteredTopicsArticleListingApi(1);
                break;
        }
    }

    @Override
    public void onClick(View view, final int position) {
        switch (view.getId()) {
            case R.id.storyOptionImageView: {
                ReportStoryOrCommentDialogFragment reportStoryOrCommentDialogFragment = new ReportStoryOrCommentDialogFragment();
                FragmentManager fm = getChildFragmentManager();
                Bundle _args = new Bundle();
                _args.putString("postId", mDatalist.get(position).getId());
                _args.putInt("type", AppConstants.REPORT_TYPE_STORY);
                reportStoryOrCommentDialogFragment.setArguments(_args);
                reportStoryOrCommentDialogFragment.setCancelable(true);
                reportStoryOrCommentDialogFragment.setTargetFragment(this, 0);
                reportStoryOrCommentDialogFragment.show(fm, "Report Content");
            }
            break;
            case R.id.rootView:
                Intent intent = new Intent(getActivity(), ShortStoryContainerActivity.class);
                intent.putExtra(Constants.ARTICLE_ID, mDatalist.get(position).getId());
                intent.putExtra(Constants.AUTHOR_ID, mDatalist.get(position).getUserId());
                intent.putExtra(Constants.BLOG_SLUG, mDatalist.get(position).getBlogPageSlug());
                intent.putExtra(Constants.TITLE_SLUG, mDatalist.get(position).getTitleSlug());
                intent.putExtra(Constants.ARTICLE_OPENED_FROM, "" + currentSubTopic.getParentName());
                intent.putExtra(Constants.FROM_SCREEN, "TopicArticlesListingScreen");
                intent.putExtra(Constants.ARTICLE_INDEX, "" + position);
                intent.putParcelableArrayListExtra("pagerListData", mDatalist);
                intent.putExtra(Constants.AUTHOR, mDatalist.get(position).getUserId() + "~" + mDatalist.get(position).getUserName());
                startActivity(intent);
                break;
            case R.id.storyRecommendationContainer:
                recommendUnrecommentArticleAPI("1", mDatalist.get(position).getId(), mDatalist.get(position).getUserId(), mDatalist.get(position).getUserName());
                break;
            case R.id.facebookShareImageView: {
                try {
                    authorTextView.setText("By - " + mDatalist.get(position).getUserName());
                    titleTextView.setText(mDatalist.get(position).getTitle());
                    bodyTextView.setText(mDatalist.get(position).getBody());
                    shareSSView.setDrawingCacheEnabled(true);
                    AppUtils.createDirIfNotExists("MyCity4Kids/videos");
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Bitmap b = shareSSView.getDrawingCache();
                            try {
                                b.compress(Bitmap.CompressFormat.JPEG, 95, new FileOutputStream(Environment.getExternalStorageDirectory() + "/MyCity4Kids/videos/image.jpg"));
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                            SharePhoto photo = new SharePhoto.Builder()
                                    .setBitmap(b)
                                    .build();
                            SharePhotoContent content = new SharePhotoContent.Builder()
                                    .addPhoto(photo)
                                    .build();
                            ShareDialog shareDialog = new ShareDialog(TopicsShortStoriesTabFragment.this);
                            shareDialog.show(content);
                            shareSSView.setDrawingCacheEnabled(false);
                        }
                    }, 200);
                    Utils.pushShareStoryEvent(getActivity(), "ShortStoryListingScreen", userDynamoId + "", mDatalist.get(position).getId(),
                            mDatalist.get(position).getUserId() + "~" + mDatalist.get(position).getUserName(), "Facebook");
                } catch (Exception e) {
                    if (isAdded())
                        Toast.makeText(getActivity(), getString(R.string.moderation_or_share_facebook_fail), Toast.LENGTH_SHORT).show();
                }
            }
            break;
            case R.id.whatsappShareImageView: {
                try {
                    authorTextView.setText("By - " + mDatalist.get(position).getUserName());
                    titleTextView.setText(mDatalist.get(position).getTitle());
                    bodyTextView.setText(mDatalist.get(position).getBody());
                    shareSSView.setDrawingCacheEnabled(true);
                    AppUtils.createDirIfNotExists("MyCity4Kids/videos");
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Bitmap b = shareSSView.getDrawingCache();
                            try {
                                b.compress(Bitmap.CompressFormat.JPEG, 95, new FileOutputStream(Environment.getExternalStorageDirectory() + "/MyCity4Kids/videos/image.jpg"));
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                            shareSSView.setDrawingCacheEnabled(false);
                            String shareUrl = AppUtils.getShortStoryShareUrl(mDatalist.get(position).getUserType(),
                                    mDatalist.get(position).getBlogPageSlug(), mDatalist.get(position).getTitleSlug());

                            if (StringUtils.isNullOrEmpty(shareUrl)) {
                                Toast.makeText(getActivity(), getString(R.string.moderation_or_share_facebook_fail), Toast.LENGTH_SHORT).show();
                            } else {
                                Uri uri = Uri.parse("file://" + Environment.getExternalStorageDirectory() + "/MyCity4Kids/videos/image.jpg");
                                Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
                                whatsappIntent.putExtra(Intent.EXTRA_STREAM, uri);
                                whatsappIntent.putExtra(Intent.EXTRA_TEXT, shareUrl);
                                whatsappIntent.setPackage("com.whatsapp");
                                whatsappIntent.setType("image/*");
//                    whatsappIntent.putExtra(Intent.EXTRA_TEXT, mDatalist.get(position).getTitle() + "\n\n" + getString(R.string.ad_share_follow_author, mDatalist.get(position).getUserName()) + "\n" + shareUrl);
                                try {
                                    startActivity(Intent.createChooser(whatsappIntent, "Share image via:"));
                                } catch (android.content.ActivityNotFoundException ex) {
                                    if (isAdded())
                                        Toast.makeText(getActivity(), getString(R.string.moderation_or_share_whatsapp_not_installed), Toast.LENGTH_SHORT).show();
                                }
                                Utils.pushShareArticleEvent(getActivity(), "DetailArticleScreen", userDynamoId + "", mDatalist.get(position).getId(), mDatalist.get(position).getUserId() + "~" + mDatalist.get(position).getUserName(), "Whatsapp");
                            }
                        }
                    }, 200);
                    Utils.pushShareStoryEvent(getActivity(), "ShortStoryListingScreen", userDynamoId + "", mDatalist.get(position).getId(),
                            mDatalist.get(position).getUserId() + "~" + mDatalist.get(position).getUserName(), "Whatsapp");
                } catch (Exception e) {
                    if (isAdded())
                        Toast.makeText(getActivity(), getString(R.string.moderation_or_share_whatsapp_fail), Toast.LENGTH_SHORT).show();
                }

            }
            break;
            case R.id.genericShareImageView: {
                authorTextView.setText("By - " + mDatalist.get(position).getUserName());
                titleTextView.setText(mDatalist.get(position).getTitle());
                bodyTextView.setText(mDatalist.get(position).getBody());
                shareSSView.setDrawingCacheEnabled(true);
                AppUtils.createDirIfNotExists("MyCity4Kids/videos");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Bitmap b = shareSSView.getDrawingCache();
                        try {
                            b.compress(Bitmap.CompressFormat.JPEG, 95, new FileOutputStream(Environment.getExternalStorageDirectory() + "/MyCity4Kids/videos/image.jpg"));
                        } catch (Exception e) {
                            Crashlytics.logException(e);
                            Log.d("MC4kException", Log.getStackTraceString(e));
                        }
                        shareSSView.setDrawingCacheEnabled(false);
                        Uri uri = Uri.parse("file://" + Environment.getExternalStorageDirectory() + "/MyCity4Kids/videos/image.jpg");

                        Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
                        shareIntent.setType("text/plain");

                        String shareUrl = AppUtils.getShortStoryShareUrl(mDatalist.get(position).getUserType(),
                                mDatalist.get(position).getBlogPageSlug(), mDatalist.get(position).getTitleSlug());

                        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                        shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareUrl);
                        shareIntent.setType("image/*");
                        startActivity(Intent.createChooser(shareIntent, "ShortStory"));
                    }
                }, 200);
                Utils.pushShareStoryEvent(getActivity(), "ShortStoryListingScreen", userDynamoId + "", mDatalist.get(position).getId(),
                        mDatalist.get(position).getUserId() + "~" + mDatalist.get(position).getUserName(), "Generic");
            }
            break;
            case R.id.authorNameTextView:
                if (userDynamoId.equals(mDatalist.get(position).getUserId())) {
                    MyAccountProfileFragment fragment0 = new MyAccountProfileFragment();
                    Bundle mBundle0 = new Bundle();
                    fragment0.setArguments(mBundle0);
                    if (isAdded())
                        ((DashboardActivity) getActivity()).addFragment(fragment0, mBundle0, true);
                } else {
                    Intent intentnn = new Intent(getActivity(), BloggerProfileActivity.class);
                    intentnn.putExtra(AppConstants.PUBLIC_PROFILE_USER_ID, mDatalist.get(position).getUserId());
                    intentnn.putExtra(AppConstants.AUTHOR_NAME, mDatalist.get(position).getUserName());
                    intentnn.putExtra(Constants.FROM_SCREEN, "ShortStoryScreen");
                    startActivityForResult(intentnn, Constants.BLOG_FOLLOW_STATUS);
                }
                break;
        }
    }

    private void recommendUnrecommentArticleAPI(String status, String articleId, String authorId, String author) {
        Utils.pushLikeStoryEvent(getActivity(), "ShortStoryListingScreen", userDynamoId + "", articleId, authorId + "~" + author);
        Retrofit retro = BaseApplication.getInstance().getRetrofit();
        ArticleDetailsAPI articleDetailsAPI = retro.create(ArticleDetailsAPI.class);

        RecommendUnrecommendArticleRequest recommendUnrecommendArticleRequest = new RecommendUnrecommendArticleRequest();
        recommendUnrecommendArticleRequest.setArticleId(articleId);
        recommendUnrecommendArticleRequest.setStatus(status);
        Call<RecommendUnrecommendArticleResponse> recommendUnrecommendArticle = articleDetailsAPI.recommendUnrecommendArticle(recommendUnrecommendArticleRequest);
        recommendUnrecommendArticle.enqueue(recommendUnrecommendArticleResponseCallback);
    }

    private Callback<RecommendUnrecommendArticleResponse> recommendUnrecommendArticleResponseCallback = new Callback<RecommendUnrecommendArticleResponse>() {
        @Override
        public void onResponse(Call<RecommendUnrecommendArticleResponse> call, retrofit2.Response<RecommendUnrecommendArticleResponse> response) {
            if (response == null || null == response.body()) {
                if (!isAdded()) {
                    return;
                }
                ((DashboardActivity) getActivity()).showToast(getString(R.string.server_went_wrong));
                return;
            }

            try {
                RecommendUnrecommendArticleResponse responseData = response.body();
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                    if (!responseData.getData().isEmpty()) {
                        for (int i = 0; i < mDatalist.size(); i++) {
                            if (responseData.getData().get(0).equals(mDatalist.get(i).getId())) {
                                mDatalist.get(i).setLikesCount("" + (Integer.parseInt(mDatalist.get(i).getLikesCount()) + 1));
                                recyclerAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                    if (!isAdded()) {
                        return;
                    }
                    ((DashboardActivity) getActivity()).showToast("" + responseData.getReason());
                } else {
                    if (isAdded())
                        ((DashboardActivity) getActivity()).showToast(getString(R.string.server_went_wrong));
                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
                if (isAdded())
                    ((DashboardActivity) getActivity()).showToast(getString(R.string.went_wrong));
            }
        }

        @Override
        public void onFailure(Call<RecommendUnrecommendArticleResponse> call, Throwable t) {
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };
}
