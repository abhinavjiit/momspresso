package com.mycity4kids.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.kelltontech.utils.ConnectivityUtils;
import com.kelltontech.utils.ToastUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.models.Topics;
import com.mycity4kids.models.response.ArticleListingResponse;
import com.mycity4kids.models.response.ArticleListingResult;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.ShortStoryAPI;
import com.mycity4kids.retrofitAPIsInterfaces.TopicsCategoryAPI;
import com.mycity4kids.ui.adapter.ChallengeListingRecycleAdapter;
import com.mycity4kids.ui.fragment.ReportContentDialogFragment;
import com.mycity4kids.utils.AppUtils;
import com.mycity4kids.widget.FeedNativeAd;

import org.apmem.tools.layouts.FlowLayout;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import okhttp3.internal.io.RealConnection;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

public class ChallnegeDetailListingActivity extends BaseActivity implements View.OnClickListener, ChallengeListingRecycleAdapter.RecyclerViewClickListener {
    private RecyclerView recyclerView;
    private LinearLayoutManager llm;
    private ChallengeListingRecycleAdapter challengeListingRecycleAdapter;
    private Toolbar mToolbar;

    private int pos;
    private String ActiveUrl;
    private ArrayList<String> challengeId;
    private ArrayList<String> activeUrl;
    private ArrayList<String> Display_Name;
    private String challenge = "challenge";
    private Topics articledatamodal;
    private String parentName, parentId;
    private static final long MIN_TIME_VIEW = 3;
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
    private RelativeLayout mLodingView;
    private TextView noBlogsTextView;
    private FlowLayout flowLayout;
    private String title = "#100WORDSTORY";
    private String selectedId;
    private String selected_Name;
    private String selectedActiveUrl;
    private RelativeLayout headerRL;
    private ImageView expandImageView;
    private FrameLayout frameLayout;
    private FloatingActionsMenu fabMenu;
    private FloatingActionButton popularSortFAB;
    private FloatingActionButton recentSortFAB;
    private FloatingActionButton fabSort;
    private FeedNativeAd feedNativeAd;
    private RelativeLayout guideOverlay;
    private RelativeLayout writeArticleCell;
    private boolean showGuide = false;
    private String userDynamoId;
    private View shareSSView;
    private TextView titleTextView, bodyTextView, authorTextView;
    private ShortStoryAPI shortStoryAPI;
    Set<Integer> viewedStoriesSet = new HashSet<>();
    private boolean isRecommendRequestRunning;
    private String likeStatus;
    private int currentShortStoryPosition;
    private RelativeLayout chooseLayout;
    int count = -1;
    private String[] array;
    private String[] url;
    private String[] array_Name;
    private TextView toolbartitle;
    private RelativeLayout relative_frame;
    private FrameLayout frameLayout_report;
    private View overlayLayout;
    private String ssTopicsText;
    private TextView startWriting;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challnege_detail_listing);

        userDynamoId = SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId();
        startWriting = (TextView) findViewById(R.id.start_writing);
        relative_frame = (RelativeLayout) findViewById(R.id.framelayout_relative);
        chooseLayout = (RelativeLayout) findViewById(R.id.choose_layout);
        frameLayout_report = (FrameLayout) findViewById(R.id.framelayout);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView_listing);
        noBlogsTextView = (TextView) findViewById(R.id.noBlogsTextView);
        toolbartitle = (TextView) findViewById(R.id.toolbarTitleTextView);
        mLodingView = (RelativeLayout) findViewById(R.id.relativeLoadingView);
        overlayLayout = (View) findViewById(R.id.overlayView_choose_story_challenge);
        guideOverlay = (RelativeLayout) findViewById(R.id.guideOverlay);
        writeArticleCell = (RelativeLayout) findViewById(R.id.writeArticleCell);
        frameLayout = (FrameLayout) findViewById(R.id.frame_layout);
        shareSSView = findViewById(R.id.shareSSView);
        titleTextView = (TextView) findViewById(R.id.titleTextView);
        bodyTextView = (TextView) findViewById(R.id.bodyTextView);
        authorTextView = (TextView) findViewById(R.id.authorTextView);

        frameLayout.getBackground().setAlpha(0);
        fabMenu = (FloatingActionsMenu) findViewById(R.id.fab_menu);
        popularSortFAB = (FloatingActionButton) findViewById(R.id.popularSortFAB);
        recentSortFAB = (FloatingActionButton) findViewById(R.id.recentSortFAB);
        fabSort = (FloatingActionButton) findViewById(R.id.fabSort);
        RadioGroup chooseoptionradioButton = (RadioGroup) findViewById(R.id.reportReasonRadioGroup);
        final AppCompatRadioButton option1RadioButton = (AppCompatRadioButton) findViewById(R.id.reason1RadioButton);
        final AppCompatRadioButton option2RadioButton = (AppCompatRadioButton) findViewById(R.id.reason2RadioButton);
        final AppCompatRadioButton option3RadioButton = (AppCompatRadioButton) findViewById(R.id.reason3RadioButton);
        final AppCompatRadioButton option4RadioButton = (AppCompatRadioButton) findViewById(R.id.reason4RadioButton);
        final AppCompatRadioButton option5RadioButton = (AppCompatRadioButton) findViewById(R.id.reason5RadioButton);
        guideOverlay.setOnClickListener(this);
        writeArticleCell.setOnClickListener(this);
        frameLayout.setVisibility(View.VISIBLE);
        fabSort.setVisibility(View.VISIBLE);
        popularSortFAB.setOnClickListener(this);
        recentSortFAB.setOnClickListener(this);
        overlayLayout.setOnClickListener(this);
        startWriting.setOnClickListener(this);

        chooseoptionradioButton.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (option1RadioButton.isChecked()) {
                    ssTopicsText = option1RadioButton.getText().toString();
                }
                if (option2RadioButton.isChecked()) {
                    ssTopicsText = option2RadioButton.getText().toString();
                }
                if (option3RadioButton.isChecked()) {
                    ssTopicsText = option3RadioButton.getText().toString();
                }
                if (option4RadioButton.isChecked()) {
                    ssTopicsText = option4RadioButton.getText().toString();
                }
                if (option5RadioButton.isChecked()) {
                    ssTopicsText = option5RadioButton.getText().toString();
                }

            }
        });
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

        Intent intent = getIntent();
        //      articledatamodal=intent.getParcelableExtra("Data");
        pos = intent.getIntExtra("position", 0);
        challengeId = intent.getStringArrayListExtra("challenge");
        Display_Name = intent.getStringArrayListExtra("Display_Name");
        activeUrl = intent.getStringArrayListExtra("StringUrl");
        //articledatamodal = intent.getParcelableExtra("topics");
        parentId = intent.getStringExtra("parentId");
        parentName = intent.getStringExtra("topics");
        array = challengeId.toArray(new String[challengeId.size()]);
        selectedId = array[pos];
        url = activeUrl.toArray(new String[activeUrl.size()]);
        selectedActiveUrl = url[pos];
        array_Name = Display_Name.toArray(new String[Display_Name.size()]);
        selected_Name = array_Name[pos];
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        // challengeListingRecycleAdapter = new ChallengeListingRecycleAdapter(this, this, pos);
        // recyclerView.setAdapter(challengeListingRecycleAdapter);
        setSupportActionBar(mToolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setDisplayShowHomeEnabled(true);
        toolbartitle.setText(getString(R.string.article_listing_type_short_story_label));
        Retrofit retro = BaseApplication.getInstance().getRetrofit();
        shortStoryAPI = retro.create(ShortStoryAPI.class);
        hitFilteredTopicsArticleListingApi(sortType);
        challengeListingRecycleAdapter = new ChallengeListingRecycleAdapter(this, this, pos, selected_Name, selectedActiveUrl);
        recyclerView.setAdapter(challengeListingRecycleAdapter);
        challengeListingRecycleAdapter.setListData(mDatalist);
        challengeListingRecycleAdapter.notifyDataSetChanged();
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
    }

    @Override
    protected void updateUi(Response response) {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.writeArticleCell:
                //if (isAdded()) {
                chooseLayout.setVisibility(View.VISIBLE);
               /* Intent intent = new Intent(this, AddShortStoryActivity.class);
                intent.putExtra("selectedrequest", challenge);
                intent.putExtra("challengeId", selectedId);
                intent.putExtra("challengeName", selected_Name);
                intent.putExtra("Url", selectedActiveUrl);
                intent.putExtra("selectedCategory", ssTopicsText);
                startActivity(intent);*/
                //}
                break;
            //  case R.id.guideOverlay:
            //    guideOverlay.setVisibility(View.GONE);
            //  TopicsListingFragment frag = ((TopicsListingFragment) this.getParentFragment());
            // frag.hideTabLayer();
            // if (isAdded()) {
//                    ((ShortStoriesListingContainerActivity) getActivity()).hideToolbarAndNavigationLayer();
            //SharedPrefUtils.setCoachmarksShownFlag(this, "topics_article", true);
            //}
            // break;
            case R.id.recentSortFAB:
                fabMenu.collapse();
                mDatalist.clear();
                challengeListingRecycleAdapter.notifyDataSetChanged();
                sortType = 0;
                nextPageNumber = 1;
                hitFilteredTopicsArticleListingApi(0);
                break;
            case R.id.popularSortFAB:
                fabMenu.collapse();
                mDatalist.clear();
                challengeListingRecycleAdapter.notifyDataSetChanged();
                sortType = 1;
                nextPageNumber = 1;
                hitFilteredTopicsArticleListingApi(1);
                break;

            case R.id.start_writing:
                if (ssTopicsText != null) {
                    Intent intentt = new Intent(this, AddShortStoryActivity.class);
                    intentt.putExtra("selectedrequest", challenge);
                    intentt.putExtra("challengeId", selectedId);
                    intentt.putExtra("challengeName", selected_Name);
                    intentt.putExtra("Url", selectedActiveUrl);
                    intentt.putExtra("selectedCategory", ssTopicsText);
                    startActivity(intentt);
                } else {
                    ToastUtils.showToast(this, "Choose Atleast One Category");
                }
                break;
            case R.id.overlayView_choose_story_challenge:
                chooseLayout.setVisibility(View.GONE);
        }
    }


    private void hitFilteredTopicsArticleListingApi(int sortType) {
        if (!ConnectivityUtils.isNetworkEnabled(this)) {
            ToastUtils.showToast(this, getString(R.string.error_network));
            return;
        }

        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        TopicsCategoryAPI topicsAPI = retrofit.create(TopicsCategoryAPI.class);

        int from = (nextPageNumber - 1) * limit + 1;
        Call<ArticleListingResponse> filterCall = topicsAPI.getArticlesForCategory(selectedId, sortType, from, from + limit - 1, "0");

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
                challengeListingRecycleAdapter.setListData(mDatalist);
                challengeListingRecycleAdapter.notifyDataSetChanged();
            }
        } else {
//            noBlogsTextView.setVisibility(View.GONE);
            writeArticleCell.setVisibility(View.GONE);
            if (nextPageNumber == 1) {
                mDatalist = dataList;

            } else {
                mDatalist.addAll(dataList);
            }
            challengeListingRecycleAdapter.setListData(mDatalist);
            nextPageNumber = nextPageNumber + 1;
            challengeListingRecycleAdapter.notifyDataSetChanged();
            if (nextPageNumber == 2) {
                //startTracking();
            }
        }

    }


    @Override
    public void onClick(View view, final int position, String ActiveUrl) {
        switch (view.getId()) {
            case R.id.storyOptionImageView: {
                relative_frame.setVisibility(View.VISIBLE);
                ReportContentDialogFragment reportContentDialogFragment = new ReportContentDialogFragment();
                FragmentManager fm = getSupportFragmentManager();
                Bundle _args = new Bundle();
                FragmentTransaction fragmentTransaction = fm.beginTransaction();
                _args.putString("postId", mDatalist.get(position).getId());
                _args.putInt("type", AppConstants.REPORT_TYPE_STORY);
                reportContentDialogFragment.setArguments(_args);
                reportContentDialogFragment.setCancelable(true);
                reportContentDialogFragment.show(fm, "report_dialog");
                // fragmentTransaction.replace(R.id.framelayout_relative, reportContentDialogFragment);
                fragmentTransaction.commit();

                //
//   android.app.FragmentManager fm = getFragmentManager();
//                Bundle _args1 = new Bundle();
//                _args.putString("postId", mDatalist.get(position).getId());
//                _args.putInt("type", AppConstants.REPORT_TYPE_STORY);
//                reportContentDialogFragment.setArguments(_args);
//                reportContentDialogFragment.setCancelable(true);
//                reportContentDialogFragment.setTargetFragment(this, 0);
                //reportContentDialogFragment.show(fm, "Report Content");
            }
            break;
            case R.id.mainView:
                Intent intent = new Intent(this, ShortStoryContainerActivity.class);
                intent.putExtra(Constants.ARTICLE_ID, mDatalist.get(position).getId());
                intent.putExtra(Constants.AUTHOR_ID, mDatalist.get(position).getUserId());
                intent.putExtra(Constants.BLOG_SLUG, mDatalist.get(position).getBlogPageSlug());
                intent.putExtra(Constants.TITLE_SLUG, mDatalist.get(position).getTitleSlug());
                intent.putExtra(Constants.ARTICLE_OPENED_FROM, "" + parentName);
                intent.putExtra(Constants.FROM_SCREEN, "TopicArticlesListingScreen");
                intent.putExtra(Constants.ARTICLE_INDEX, "" + position);
                intent.putParcelableArrayListExtra("pagerListData", mDatalist);
                intent.putExtra(Constants.AUTHOR, mDatalist.get(position).getUserId() + "~" + mDatalist.get(position).getUserName());
                startActivity(intent);
                break;
            case R.id.storyRecommendationContainer:
                if (!isRecommendRequestRunning) {
                    if (mDatalist.get(position).isLiked()) {
                        likeStatus = "0";
                        currentShortStoryPosition = position;
                        //  recommendUnrecommentArticleAPI("0", mDatalist.get(position).getId(), mDatalist.get(position).getUserId(), mDatalist.get(position).getUserName());
                    } else {
                        likeStatus = "1";
                        currentShortStoryPosition = position;
                        //   recommendUnrecommentArticleAPI("1", mDatalist.get(position).getId(), mDatalist.get(position).getUserId(), mDatalist.get(position).getUserName());
                    }
                }
                break;
            case R.id.facebookShareImageView: {
                AppUtils.shareStoryWithFB(this, this, mDatalist.get(position).getUserType(), mDatalist.get(position).getBlogPageSlug(), mDatalist.get(position).getTitleSlug(),
                        "ShortStoryListingScreen", userDynamoId, mDatalist.get(position).getId(), mDatalist.get(position).getUserId(), mDatalist.get(position).getUserName());

            }
            break;
            case R.id.whatsappShareImageView: {
                try {

                    AppUtils.drawMultilineTextToBitmap(mDatalist.get(position).getTitle().trim(), mDatalist.get(position).getBody().trim(), mDatalist.get(position).getUserName());
                } catch (Exception e) {
                    Crashlytics.logException(e);
                    Log.d("MC4kException", Log.getStackTraceString(e));
                    return;
                }
                // if (isAdded()) {
                AppUtils.shareStoryWithWhatsApp(this, mDatalist.get(position).getUserType(), mDatalist.get(position).getBlogPageSlug(), mDatalist.get(position).getTitleSlug(),
                        "ShortStoryListingScreen", userDynamoId, mDatalist.get(position).getId(), mDatalist.get(position).getUserId(), mDatalist.get(position).getUserName());
                //}
            }
            break;
            case R.id.instagramShareImageView: {
                try {
                    AppUtils.drawMultilineTextToBitmap(mDatalist.get(position).getTitle().trim(), mDatalist.get(position).getBody().trim(), mDatalist.get(position).getUserName());
                } catch (Exception e) {
                    Crashlytics.logException(e);
                    Log.d("MC4kException", Log.getStackTraceString(e));
                }

                //if (isAdded()) {
                AppUtils.shareStoryWithInstagram(this, "ShortStoryListingScreen", userDynamoId, mDatalist.get(position).getId(),
                        mDatalist.get(position).getUserId(), mDatalist.get(position).getUserName());
                // }
            }
            break;
            case R.id.genericShareImageView: {

                // if (isAdded()) {
                AppUtils.shareStoryGeneric(this, mDatalist.get(position).getUserType(), mDatalist.get(position).getBlogPageSlug(), mDatalist.get(position).getTitleSlug(),
                        "ShortStoryListingScreen", userDynamoId, mDatalist.get(position).getId(), mDatalist.get(position).getUserId(), mDatalist.get(position).getUserName());
                //}
            }
            break;
            case R.id.authorNameTextView:
                if (userDynamoId.equals(mDatalist.get(position).getUserId())) {
//                    MyAccountProfileFragment fragment0 = new MyAccountProfileFragment();
//                    Bundle mBundle0 = new Bundle();
//                    fragment0.setArguments(mBundle0);
//                    if (isAdded())
//                        ((ShortStoriesListingContainerActivity) getActivity()).addFragment(fragment0, mBundle0, true);
                    Intent pIntent = new Intent(this, PrivateProfileActivity.class);
                    startActivity(pIntent);
                } else {
                    Intent intentnn = new Intent(this, PublicProfileActivity.class);
                    intentnn.putExtra(AppConstants.PUBLIC_PROFILE_USER_ID, mDatalist.get(position).getUserId());
                    intentnn.putExtra(AppConstants.AUTHOR_NAME, mDatalist.get(position).getUserName());
                    intentnn.putExtra(Constants.FROM_SCREEN, "ShortStoryScreen");
                    startActivityForResult(intentnn, Constants.BLOG_FOLLOW_STATUS);
                }
                break;

            case R.id.submit_story_text:
                chooseLayout.setVisibility(View.VISIBLE);
             /*   Intent intent1 = new Intent(this, AddShortStoryActivity.class);
                intent1.putExtra("selectedrequest", challenge);
                intent1.putExtra("challengeId", selectedId);
                intent1.putExtra("challengeName", selected_Name);
                intent1.putExtra("Url", ActiveUrl);
                startActivity(intent1);*/
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        chooseLayout.setVisibility(View.INVISIBLE);
    }
}
