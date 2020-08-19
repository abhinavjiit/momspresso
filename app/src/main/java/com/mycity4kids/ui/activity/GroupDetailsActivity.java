package com.mycity4kids.ui.activity;

import android.accounts.NetworkErrorException;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.LayoutManager;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.internal.LinkedTreeMap;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.base.BaseActivity;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.filechooser.com.ipaulpro.afilechooser.utils.FileUtils;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.collectionsModels.UserCollectionsListModel;
import com.mycity4kids.models.collectionsModels.UserCollectionsModel;
import com.mycity4kids.models.request.AddGpPostCommentOrReplyRequest;
import com.mycity4kids.models.request.AddGroupPostRequest;
import com.mycity4kids.models.request.GroupActionsPatchRequest;
import com.mycity4kids.models.request.GroupActionsRequest;
import com.mycity4kids.models.request.UpdateGroupMembershipRequest;
import com.mycity4kids.models.request.UpdateGroupPostRequest;
import com.mycity4kids.models.request.UpdatePostSettingsRequest;
import com.mycity4kids.models.request.UpdateUserPostSettingsRequest;
import com.mycity4kids.models.response.AddGpPostCommentReplyResponse;
import com.mycity4kids.models.response.AddGroupPostResponse;
import com.mycity4kids.models.response.ArticleListingResponse;
import com.mycity4kids.models.response.ArticleListingResult;
import com.mycity4kids.models.response.BaseResponseGeneric;
import com.mycity4kids.models.response.GroupDetailResponse;
import com.mycity4kids.models.response.GroupPostCommentResult;
import com.mycity4kids.models.response.GroupPostResponse;
import com.mycity4kids.models.response.GroupPostResult;
import com.mycity4kids.models.response.GroupResult;
import com.mycity4kids.models.response.GroupResult.VirtualClinicSettings.VirtualClinicCollectionSettings;
import com.mycity4kids.models.response.GroupsActionResponse;
import com.mycity4kids.models.response.GroupsCategoryMappingResponse;
import com.mycity4kids.models.response.GroupsCategoryMappingResult;
import com.mycity4kids.models.response.GroupsMembershipResponse;
import com.mycity4kids.models.response.KidsModel;
import com.mycity4kids.models.response.UserDetailResponse;
import com.mycity4kids.models.response.UserDetailResult;
import com.mycity4kids.models.response.UserPostSettingResponse;
import com.mycity4kids.models.response.UserPostSettingResult;
import com.mycity4kids.models.rewardsmodels.RewardsPersonalResponse;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.profile.UserProfileActivity;
import com.mycity4kids.retrofitAPIsInterfaces.BloggerDashboardAPI;
import com.mycity4kids.retrofitAPIsInterfaces.CollectionsAPI;
import com.mycity4kids.retrofitAPIsInterfaces.GroupsAPI;
import com.mycity4kids.retrofitAPIsInterfaces.LoginRegistrationAPI;
import com.mycity4kids.retrofitAPIsInterfaces.TopicsCategoryAPI;
import com.mycity4kids.ui.activity.collection.UserCollectionItemListActivity;
import com.mycity4kids.ui.adapter.CollectionItemsListAdapter;
import com.mycity4kids.ui.adapter.CollectionItemsListAdapter.RecyclerViewClick;
import com.mycity4kids.ui.adapter.CustomSpinnerAdapter;
import com.mycity4kids.ui.adapter.GroupAboutRecyclerAdapter;
import com.mycity4kids.ui.adapter.GroupAboutRecyclerAdapter.RecyclerViewClickListener;
import com.mycity4kids.ui.adapter.GroupBlogsRecyclerAdapter;
import com.mycity4kids.ui.adapter.GroupCollectionRecyclerAdapter;
import com.mycity4kids.ui.adapter.GroupsGenericPostRecyclerAdapter;
import com.mycity4kids.ui.fragment.AddGpPostCommentReplyDialogFragment;
import com.mycity4kids.ui.fragment.GroupPostReportDialogFragment;
import com.mycity4kids.ui.fragment.ProcessBitmapTaskFragment;
import com.mycity4kids.ui.fragment.ProcessBitmapTaskFragment.TaskCallbacks;
import com.mycity4kids.ui.fragment.ShareBlogInDiscussionDialogFragment;
import com.mycity4kids.ui.fragment.ShareBlogInDiscussionDialogFragment.IForYourArticleRemove;
import com.mycity4kids.utils.AppUtils;
import com.mycity4kids.utils.ConnectivityUtils;
import com.mycity4kids.utils.DateTimeUtils;
import com.mycity4kids.utils.StringUtils;
import com.mycity4kids.utils.ToastUtils;
import com.mycity4kids.widget.MomspressoButtonWidget;
import com.mycity4kids.widget.SpacesItemDecoration;
import com.squareup.picasso.Picasso;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.TimerTask;
import okhttp3.ResponseBody;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.threeten.bp.LocalDate;
import org.threeten.bp.Period;
import org.threeten.bp.format.DateTimeFormatter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by hemant on 10/4/18.
 */

public class GroupDetailsActivity extends BaseActivity implements OnClickListener,
        RecyclerViewClickListener, GroupBlogsRecyclerAdapter.RecyclerViewClickListener,
        GroupsGenericPostRecyclerAdapter.RecyclerViewClickListener,
        IForYourArticleRemove, TaskCallbacks, RecyclerViewClick,
        GroupCollectionRecyclerAdapter.RecyclerViewClickListener {

    private static final int EDIT_POST_REQUEST_CODE = 1010;
    private static final String[] sectionsKey = {"ABOUT", "DISCUSSION", "BLOGS", "POLLS", "ASK AN EXPERT"};
    private static final String[] sectionsKeyResources = {"ABOUT", "DISCUSSION", "RESOURCES", "POLLS", "ASK AN EXPERT"};
    private static final String[] sectionsKeyVirtualClinic = {"ABOUT", "DISCUSSION", "VIRTUAL", "POLLS",
            "ASK AN EXPERT"};
    private static final int TUTORIAL = 1;
    private ArrayList<GroupsCategoryMappingResult> groupMappedCategories;
    private int categoryIndex = 0;
    private int nextPageNumber = 1;
    private String type;
    private int totalPostCount;
    private static final String TAG_TASK_FRAGMENT = "task_fragment";
    private ProcessBitmapTaskFragment processBitmapTaskFragment;
    private int skip = 0;
    private int postId;
    private int limit = 10;
    private LinearLayout shareGroupImageViewLinearLayoutContainer;
    private String postType;
    private boolean isRequestRunning = false;
    private boolean isLastPageReached = false;
    private ArrayList<ArticleListingResult> articleDataModelsNew;
    private ArrayList<UserCollectionsModel> collectionItemsList;
    private ArrayList<UserCollectionsModel> collectionList;
    private ArrayList<GroupPostResult> postList;
    private int pastVisiblesItems;
    private int visibleItemCount;
    private int totalItemCount;
    private UserPostSettingResult currentPostPrefsForUser;
    private GroupResult selectedGroup;
    private GroupPostResult selectedPost;
    private GroupPostResult editedPost;
    private String memberType;
    private int groupId;
    private String commaSepCategoryList = "";
    private String source;
    private Handler handler = new Handler();
    private boolean justJoined = false;

    private Animation slideAnim;
    private Animation fadeAnim;

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private GroupAboutRecyclerAdapter groupAboutRecyclerAdapter;
    private GroupBlogsRecyclerAdapter groupBlogsRecyclerAdapter;
    private CollectionItemsListAdapter collectionItemsListAdapter;
    private GroupsGenericPostRecyclerAdapter groupsGenericPostRecyclerAdapter;
    private GroupCollectionRecyclerAdapter groupCollectionRecyclerAdapter;
    private TabLayout groupPostTabLayout;
    private RelativeLayout addPostContainer;
    private FloatingActionButton addPostFab;
    private LinearLayout postContainer;
    private LinearLayout postAudioContainer;
    private LinearLayout pollContainer;
    private ImageView closeImageView;
    private TextView noPostsTextView;
    private TextView groupNameTextView;
    private TextView toolbarTitle;
    private LinearLayout postSettingsContainer;
    private RelativeLayout postSettingsContainerMain;
    private View overlayView;
    private TextView savePostTextView;
    private TextView notificationToggleTextView;
    private TextView commentToggleTextView;
    private TextView reportPostTextView;
    private TextView editPostTextView;
    private TextView deletePostTextView;
    private TextView blockUserTextView;
    private TextView pinPostTextView;
    private ProgressBar progressBar;
    private ImageView groupSettingsImageView;
    private TextView memberCountTextView;
    private ImageView groupImageView;
    private ImageView clearSearchImageView;
    private ImageView shareGroupImageView;
    private CoordinatorLayout root;
    private View hideBottomDrawer;
    private LinearLayout bottomSheet;
    private BottomSheetBehavior bottomSheetBehavior;
    private RelativeLayout announcementContainerR;
    private RelativeLayout pollContainerR;
    private RelativeLayout postContainerR;
    private TextView justJoinedPostTextView;
    private CheckBox anonymousCheckbox;
    private Dialog dialog;
    private int start = 0;
    private ConstraintLayout filterContentContainer;
    private MomspressoButtonWidget forYouFilterWidget;
    private MomspressoButtonWidget monthFilterWidget;
    private MomspressoButtonWidget categoryFilterWidget;
    private Map<String, String> questionnaireResponse;
    private int currentUserMembershipId;
    private String parentCollectionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getUserType().equals("1")) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        }
        setContentView(R.layout.group_details_activity);
        root = findViewById(R.id.root);
        ((BaseApplication) getApplication()).setView(root);
        ((BaseApplication) getApplication()).setActivity(this);

        Utils.pushOpenScreenEvent(this, "GroupDetailsScreen",
                SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId());
        toolbar = findViewById(R.id.toolbar);
        postContainerR = findViewById(R.id.postContainerR);
        pollContainerR = findViewById(R.id.pollContainerR);
        announcementContainerR = findViewById(R.id.announcementContainerR);
        hideBottomDrawer = findViewById(R.id.hideBottomDrawer);
        bottomSheet = findViewById(R.id.bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        recyclerView = findViewById(R.id.recyclerView);
        groupPostTabLayout = findViewById(R.id.groupPostTabLayout);
        addPostContainer = findViewById(R.id.addPostContainer);
        addPostFab = findViewById(R.id.addPostFAB);
        postContainer = findViewById(R.id.postContainer);
        postAudioContainer = findViewById(R.id.postAudioContainer);
        pollContainer = findViewById(R.id.pollContainer);
        closeImageView = findViewById(R.id.closeImageView);
        noPostsTextView = findViewById(R.id.noPostsTextView);
        groupNameTextView = findViewById(R.id.groupNameTextView);
        shareGroupImageViewLinearLayoutContainer = findViewById(
                R.id.shareGroupImageViewLinearLayoutContainer);
        toolbarTitle = findViewById(R.id.toolbarTitle);
        clearSearchImageView = findViewById(R.id.clearSearchImageView);
        groupSettingsImageView = findViewById(R.id.groupSettingsImageView);
        groupImageView = findViewById(R.id.groupImageView);
        shareGroupImageView = findViewById(R.id.shareGroupImageView);
        progressBar = findViewById(R.id.progressBar);
        postSettingsContainer = findViewById(R.id.postSettingsContainer);
        postSettingsContainerMain = findViewById(R.id.postSettingsContainerMain);
        overlayView = findViewById(R.id.overlayView);
        savePostTextView = findViewById(R.id.savePostTextView);
        deletePostTextView = findViewById(R.id.deletePostTextView);
        editPostTextView = findViewById(R.id.editPostTextView);
        blockUserTextView = findViewById(R.id.blockUserTextView);
        pinPostTextView = findViewById(R.id.pinPostTextView);
        memberCountTextView = findViewById(R.id.memberCountTextView);
        notificationToggleTextView = findViewById(R.id.notificationToggleTextView);
        commentToggleTextView = findViewById(R.id.commentToggleTextView);
        reportPostTextView = findViewById(R.id.reportPostTextView);
        filterContentContainer = findViewById(R.id.filterContentContainer);
        forYouFilterWidget = findViewById(R.id.forYouFilterWidget);
        monthFilterWidget = findViewById(R.id.monthFilterWidget);
        categoryFilterWidget = findViewById(R.id.categoryFilterWidget);

        selectedGroup = getIntent().getParcelableExtra("groupItem");
        groupId = getIntent().getIntExtra("groupId", 0);
        justJoined = getIntent().getBooleanExtra("justJoined", false);
        memberType = getIntent().getStringExtra(AppConstants.GROUP_MEMBER_TYPE);
        source = getIntent().getStringExtra("source");
        questionnaireResponse = (Map<String, String>) getIntent().getSerializableExtra("questionnaireResponse");
        currentUserMembershipId = getIntent().getIntExtra("membershipId", 0);
        MixpanelAPI mixpanel = MixpanelAPI.getInstance(BaseApplication.getAppContext(), AppConstants.MIX_PANEL_TOKEN);

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("userId", SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId());
            jsonObject.put("groupId", "" + groupId);
            mixpanel.track("GroupDetail", jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        slideAnim = AnimationUtils.loadAnimation(this, R.anim.appear_from_bottom);
        fadeAnim = AnimationUtils.loadAnimation(this, R.anim.alpha_anim);

        postContainerR.setOnClickListener(this);
        pollContainerR.setOnClickListener(this);
        announcementContainerR.setOnClickListener(this);
        hideBottomDrawer.setOnClickListener(this);
        addPostFab.setOnClickListener(this);
        pollContainer.setOnClickListener(this);
        postContainer.setOnClickListener(this);
        postAudioContainer.setOnClickListener(this);
        closeImageView.setOnClickListener(this);
        savePostTextView.setOnClickListener(this);
        notificationToggleTextView.setOnClickListener(this);
        commentToggleTextView.setOnClickListener(this);
        reportPostTextView.setOnClickListener(this);
        overlayView.setOnClickListener(this);
        groupSettingsImageView.setOnClickListener(this);
        editPostTextView.setOnClickListener(this);
        deletePostTextView.setOnClickListener(this);
        blockUserTextView.setOnClickListener(this);
        pinPostTextView.setOnClickListener(this);
        clearSearchImageView.setOnClickListener(this);
        shareGroupImageView.setOnClickListener(this);
        shareGroupImageViewLinearLayoutContainer.setOnClickListener(this);
        forYouFilterWidget.setOnClickListener(this);
        monthFilterWidget.setOnClickListener(this);
        categoryFilterWidget.setOnClickListener(this);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        bottomSheetStateChange();

        final LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(llm);

        collectionList = new ArrayList<>();
        groupCollectionRecyclerAdapter = new GroupCollectionRecyclerAdapter(this);
        groupCollectionRecyclerAdapter.setData(collectionList);

        articleDataModelsNew = new ArrayList<>();
        groupBlogsRecyclerAdapter = new GroupBlogsRecyclerAdapter(this, this);
        groupBlogsRecyclerAdapter.setData(articleDataModelsNew);

        collectionItemsList = new ArrayList<>();
        collectionItemsListAdapter = new CollectionItemsListAdapter(this, this);
        collectionItemsListAdapter.setListType(TUTORIAL);
        collectionItemsListAdapter.setListData(collectionItemsList);

        postList = new ArrayList<>();
        groupsGenericPostRecyclerAdapter = new GroupsGenericPostRecyclerAdapter(this, this, selectedGroup, memberType);
        groupsGenericPostRecyclerAdapter.setData(postList);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    LayoutManager llm = recyclerView.getLayoutManager();
                    if (llm instanceof LinearLayoutManager) {
                        visibleItemCount = llm.getChildCount();
                        totalItemCount = llm.getItemCount();
                        pastVisiblesItems = ((LinearLayoutManager) llm).findFirstVisibleItemPosition();
                    } else {
                        visibleItemCount = llm.getChildCount();
                        totalItemCount = llm.getItemCount();
                        pastVisiblesItems = ((GridLayoutManager) llm).findFirstVisibleItemPosition();
                    }

                    if (!isRequestRunning && !isLastPageReached) {
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                            isRequestRunning = true;
                            if (recyclerView.getAdapter() instanceof GroupsGenericPostRecyclerAdapter) {
                                getGroupPosts();
                            } else if (recyclerView.getAdapter() instanceof CollectionItemsListAdapter) {
                                getUserCollectionItems(selectedGroup.getCollectionId(), start);
                            } else if (recyclerView.getAdapter() instanceof GroupCollectionRecyclerAdapter) {
                                getCollectionsList(parentCollectionId);
                            } else {
                                if (groupMappedCategories != null && groupMappedCategories.size() != 0) {
                                    hitFilteredTopicsArticleListingApi(
                                            groupMappedCategories.get(categoryIndex).getCategoryId());
                                }
                            }
                        }
                    }
                }
            }
        });

        toolbarTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (TextUtils.isEmpty(editable)) {
                    clearSearchImageView.setVisibility(View.GONE);
                    skip = 0;
                    limit = 10;
                    isRequestRunning = false;
                    isLastPageReached = false;
                    postList.clear();
                    groupsGenericPostRecyclerAdapter.notifyDataSetChanged();
                    getGroupPosts();
                } else {
                    clearSearchImageView.setVisibility(View.VISIBLE);
                    handler.postDelayed(() -> runOnUiThread(new TimerTask() {
                        @Override
                        public void run() {
                            skip = 0;
                            limit = 10;
                            isRequestRunning = false;
                            isLastPageReached = false;
                            postList.clear();
                            groupsGenericPostRecyclerAdapter.notifyDataSetChanged();
                            requestSearch();
                        }
                    }), 1000);
                }
            }
        });
        getGroupDetails();
    }

    private boolean validateParams() {
        if (justJoinedPostTextView.getText() == null || StringUtils
                .isNullOrEmpty(justJoinedPostTextView.getText().toString())) {
            showToast("Please enter some content to continue");
            return false;
        }
        return true;
    }

    private void publishPost() {
        AddGroupPostRequest addGroupPostRequest = new AddGroupPostRequest();
        addGroupPostRequest.setContent(justJoinedPostTextView.getText().toString());
        addGroupPostRequest.setType("5");
        addGroupPostRequest.setGroupId(selectedGroup.getId());
        if (SharedPrefUtils.isUserAnonymous(BaseApplication.getAppContext())) {
            addGroupPostRequest.setAnnon(1);
        }
        addGroupPostRequest.setUserId(SharedPrefUtils.getUserDetailModel(this).getDynamoId());
        Retrofit retrofit = BaseApplication.getInstance().getGroupsRetrofit();
        GroupsAPI groupsApi = retrofit.create(GroupsAPI.class);
        Call<AddGroupPostResponse> call = groupsApi.createPost(addGroupPostRequest);
        call.enqueue(postAdditionResponseCallback);
    }

    private Callback<AddGroupPostResponse> postAdditionResponseCallback = new Callback<AddGroupPostResponse>() {
        @Override
        public void onResponse(Call<AddGroupPostResponse> call, retrofit2.Response<AddGroupPostResponse> response) {
            isRequestRunning = false;
            if (response.body() == null) {
                NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                FirebaseCrashlytics.getInstance().recordException(nee);
                return;
            }
            try {
                if (response.isSuccessful()) {
                    SharedPrefUtils.clearSavedPostData(BaseApplication.getAppContext(), selectedGroup.getId());
                    setResult(RESULT_OK);
                    justJoinedPostTextView.setText("");
                    postList.clear();
                    skip = 0;
                    limit = 10;
                    getGroupPosts();
                    dialog.dismiss();
                }
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
                showToast(getString(R.string.went_wrong));
            }
        }

        @Override
        public void onFailure(Call<AddGroupPostResponse> call, Throwable t) {
            isRequestRunning = false;
            showToast(getString(R.string.went_wrong));
            FirebaseCrashlytics.getInstance().recordException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    private void requestSearch() {
        if (!StringUtils.isNullOrEmpty(toolbarTitle.getText().toString())) {
            Retrofit retrofit = BaseApplication.getInstance().getGroupsRetrofit();
            GroupsAPI groupsApi = retrofit.create(GroupsAPI.class);
            Call<GroupPostResponse> call = groupsApi
                    .searchWithinGroup(toolbarTitle.getText().toString(), "post", 1, groupId, skip, limit);
            call.enqueue(searchResultResponseCallback);
        }
    }

    private Callback<GroupPostResponse> searchResultResponseCallback = new Callback<GroupPostResponse>() {
        @Override
        public void onResponse(Call<GroupPostResponse> call, retrofit2.Response<GroupPostResponse> response) {
            if (response.body() == null) {
                NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                FirebaseCrashlytics.getInstance().recordException(nee);
                return;
            }
            try {
                if (response.isSuccessful()) {
                    GroupPostResponse groupPostResponse = response.body();
                    processSearchResultListing(groupPostResponse);
                }
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<GroupPostResponse> call, Throwable t) {
            FirebaseCrashlytics.getInstance().recordException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    private void getGroupDetails() {
        Retrofit retrofit = BaseApplication.getInstance().getGroupsRetrofit();
        GroupsAPI groupsApi = retrofit.create(GroupsAPI.class);

        Call<GroupDetailResponse> call = groupsApi.getGroupById(groupId);
        call.enqueue(groupDetailsResponseCallback);
    }

    private Callback<GroupDetailResponse> groupDetailsResponseCallback = new Callback<GroupDetailResponse>() {
        @Override
        public void onResponse(Call<GroupDetailResponse> call, retrofit2.Response<GroupDetailResponse> response) {
            if (response.body() == null) {
                NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                FirebaseCrashlytics.getInstance().recordException(nee);
                return;
            }
            try {
                if (response.isSuccessful()) {
                    GroupDetailResponse groupPostResponse = response.body();
                    selectedGroup = groupPostResponse.getData().getResult();
                    toolbarTitle.setHint(getString(R.string.groups_search_in));
                    if ("datetime".equals(selectedGroup.getQuestionnaire().get("type"))) {
                        String[] sections = {
                                getString(R.string.groups_sections_about),
                                getString(R.string.groups_sections_discussions),
                                getString(R.string.all_resources),
                                getString(R.string.groups_sections_polls), getString(R.string.groups_sections_ask)
                        };
                        setUpTabLayout(sections, 2);
                    } else if (StringUtils.isNullOrEmpty(selectedGroup.getCollectionId())) {
                        String[] sections = {
                                getString(R.string.groups_sections_about),
                                getString(R.string.groups_sections_discussions),
                                getString(R.string.onboarding_desc_array_tutorial_1_blogs),
                                getString(R.string.groups_sections_polls), getString(R.string.groups_sections_ask)
                        };
                        setUpTabLayout(sections, 0);
                    } else {
                        String[] sections = {
                                getString(R.string.groups_sections_about),
                                getString(R.string.groups_sections_discussions),
                                getString(R.string.all_resources),
                                getString(R.string.groups_sections_polls), getString(R.string.groups_sections_ask)
                        };
                        setUpTabLayout(sections, 1);
                    }
                    memberCountTextView
                            .setText(selectedGroup.getMemberCount() + " " + getString(R.string.groups_member_label));
                    groupNameTextView.setText(selectedGroup.getTitle());

                    Picasso.get().load(selectedGroup.getHeaderImage())
                            .placeholder(R.drawable.default_article).error(R.drawable.default_article)
                            .into(groupImageView);

                    groupAboutRecyclerAdapter = new GroupAboutRecyclerAdapter(GroupDetailsActivity.this,
                            GroupDetailsActivity.this);
                    groupAboutRecyclerAdapter.setData(selectedGroup);
                    TabLayout.Tab tab = groupPostTabLayout.getTabAt(1);
                    tab.select();
                    try {
                        if (questionnaireResponse.isEmpty() && selectedGroup.getQuestionnaire() != null
                                && !selectedGroup.getQuestionnaire().isEmpty()) {
                            if ("expectedDate".equals(selectedGroup.getQuestionnaire().get("name"))) {
                                getUserInfo("ExpectingMom");
                            } else if ("dob".equals(selectedGroup.getQuestionnaire().get("name"))) {
                                getUserInfo("NewMom");
                            }
                        } else if (justJoined) {
                            showFirstTimeJoinerDialog();
                        }
                        if (selectedGroup.getAnnonAllowed() != 0) {
                            Toast toast = Toast.makeText(GroupDetailsActivity.this,
                                    getResources().getString(R.string.group_detail_activity_toast_text),
                                    Toast.LENGTH_LONG);
                            LinearLayout toastLayout = (LinearLayout) toast.getView();
                            TextView toastTV = (TextView) toastLayout.getChildAt(0);
                            toastTV.setTextSize(16);
                            toast.show();
                        }
                    } catch (Exception e) {
                        FirebaseCrashlytics.getInstance().recordException(e);
                        Log.d("MC4kException", Log.getStackTraceString(e));
                    }
                }
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<GroupDetailResponse> call, Throwable t) {
            FirebaseCrashlytics.getInstance().recordException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    private void getUserInfo(String groupType) {
        showProgressDialog(getString(R.string.please_wait));
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        BloggerDashboardAPI bloggerDashboardApi = retrofit.create(BloggerDashboardAPI.class);
        Call<UserDetailResponse> call = bloggerDashboardApi
                .getBloggerData(SharedPrefUtils.getUserDetailModel(this).getDynamoId());
        call.enqueue(new Callback<UserDetailResponse>() {
            @Override
            public void onResponse(Call<UserDetailResponse> call, Response<UserDetailResponse> response) {
                removeProgressDialog();
                if (response.body() == null) {
                    NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                    FirebaseCrashlytics.getInstance().recordException(nee);
                    return;
                }
                try {
                    if (response.isSuccessful()) {
                        UserDetailResponse userDetailResponse = response.body();
                        if (userDetailResponse.getCode() == 200 && Constants.SUCCESS
                                .equals(userDetailResponse.getStatus())) {
                            if ("ExpectingMom".equals(groupType)) {
                                if (StringUtils
                                        .isNullOrEmpty(
                                                userDetailResponse.getData().get(0).getResult().getExpectedDate())) {
                                    showExpectedDateDialog();
                                } else {
                                    //Save Expecting Date to MemberShip Level
                                    saveExpectedDateOrYoungestChildDobToMembership(
                                            userDetailResponse.getData().get(0).getResult().getExpectedDate(),
                                            "expectedDate");
                                }
                            } else if ("NewMom".equals(groupType)) {
                                if ("0".equals(userDetailResponse.getData().get(0).getResult().getIsMother())) {
                                    showNewMomDialog();
                                } else if (
                                        getDobYoungestChild(userDetailResponse.getData().get(0).getResult().getKids())
                                                == 0) {
                                    showNewMomDialog();
                                } else {
                                    //Save Youngest Dob to MemberShip Level
                                    saveExpectedDateOrYoungestChildDobToMembership("" + getDobYoungestChild(
                                            userDetailResponse.getData().get(0).getResult().getKids()), "dob");
                                }
                            }
                        } else {
                            showToast(userDetailResponse.getReason());
                        }
                    }
                } catch (Exception e) {
                    FirebaseCrashlytics.getInstance().recordException(e);
                    Log.d("MC4kException", Log.getStackTraceString(e));
                }
            }

            @Override
            public void onFailure(Call<UserDetailResponse> call, Throwable t) {
                removeProgressDialog();
                FirebaseCrashlytics.getInstance().recordException(t);
                Log.d("MC4kException", Log.getStackTraceString(t));
            }
        });
    }

    private long getDobYoungestChild(ArrayList<KidsModel> kids) {
        try {
            if (kids == null || kids.isEmpty()) {
                return 0;
            } else {
                long maximumDate = Long.parseLong(kids.get(0).getBirthDay());
                for (int i = 0; i < kids.size(); i++) {
                    if (maximumDate < Long.parseLong(kids.get(i).getBirthDay())) {
                        maximumDate = Long.parseLong(kids.get(i).getBirthDay());
                    }
                }
                return maximumDate;
            }
        } catch (Exception e) {
            return 0;
        }
    }

    private void showExpectedDateDialog() {
        dialog = new Dialog(this);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_expecting_mom);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        ImageView headerImageView = dialog.findViewById(R.id.headerImageView);
        Picasso.get().load(selectedGroup.getQuestionnaire().get("imageUrl")).into(headerImageView);
        MomspressoButtonWidget continueButtonWidget = dialog.findViewById(R.id.continueButtonWidget);
        continueButtonWidget
                .setText(org.apache.commons.lang3.StringUtils
                        .capitalize(getString(R.string.dialog_continue).toLowerCase()));
        MomspressoButtonWidget dateButtonWidget = dialog.findViewById(R.id.dateButtonWidget);
        dateButtonWidget.setGravity(Gravity.START);
        dateButtonWidget.setOnClickListener(v -> {
            final Calendar cldr = Calendar.getInstance();
            int day = cldr.get(Calendar.DAY_OF_MONTH);
            int month = cldr.get(Calendar.MONTH);
            int year = cldr.get(Calendar.YEAR);
            DatePickerDialog picker = new DatePickerDialog(GroupDetailsActivity.this,
                    android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
                    (view, year1, monthOfYear, dayOfMonth) -> dateButtonWidget
                            .setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year1), year, month, day);
            Date referenceDate = new Date();
            Calendar c = Calendar.getInstance();
            c.setTime(referenceDate);
            picker.getDatePicker().setMinDate(c.getTimeInMillis() - 10000);
            c.add(Calendar.MONTH, 9);
            picker.getDatePicker().setMaxDate(c.getTimeInMillis());
            picker.show();
        });

        continueButtonWidget.setOnClickListener(view -> {
            if (getString(R.string.rewards_expected_date).equals(dateButtonWidget.getText().toString())) {
                showToast("Please select a valid date");
            } else {
                String date = dateButtonWidget.getText().toString();
                long timeInMillis = DateTimeUtils.convertStringToMilliTimestamp(date);
                if (timeInMillis == 0) {
                    showToast("Please select a valid date");
                } else {
                    UserDetailResult userDetailResult = new UserDetailResult();
                    userDetailResult.setIsExpected("1");
                    userDetailResult.setExpectedDate("" + timeInMillis);
                    saveExpectedDateOrKidsToUserDashboard(userDetailResult);
                }
            }
        });
        ImageView cancelDialog = dialog.findViewById(R.id.cancel);
        cancelDialog.setOnClickListener(view -> dialog.dismiss());
        dialog.show();
    }

    private void showNewMomDialog() {
        dialog = new Dialog(this);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_new_mom);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        ImageView headerImageView = dialog.findViewById(R.id.headerImageView);
        Picasso.get().load(selectedGroup.getQuestionnaire().get("imageUrl")).into(headerImageView);
        MomspressoButtonWidget continueButtonWidget = dialog.findViewById(R.id.continueButtonWidget);
        continueButtonWidget
                .setText(org.apache.commons.lang3.StringUtils
                        .capitalize(getString(R.string.dialog_continue).toLowerCase()));
        ArrayList<String> genderList = new ArrayList<>();
        genderList.add("Male");
        genderList.add("Female");
        CustomSpinnerAdapter spinAdapter = new CustomSpinnerAdapter(this, genderList);
        Spinner spinnerGender = dialog.findViewById(R.id.spinnerGender);
        spinnerGender.setAdapter(spinAdapter);
        spinnerGender.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                spinnerGender.setSelection(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        TextView deleteTextView = dialog.findViewById(R.id.textDeleteChild);
        deleteTextView.setVisibility(View.GONE);
        TextView kidsDobTextView = dialog.findViewById(R.id.textKidsDOB);
        kidsDobTextView.setOnClickListener(v -> {
            final Calendar cldr = Calendar.getInstance();
            int day = cldr.get(Calendar.DAY_OF_MONTH);
            int month = cldr.get(Calendar.MONTH);
            int year = cldr.get(Calendar.YEAR);
            DatePickerDialog picker = new DatePickerDialog(GroupDetailsActivity.this,
                    android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
                    (view, year1, monthOfYear, dayOfMonth) -> kidsDobTextView
                            .setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year1), year, month, day);
            Date referenceDate = new Date();
            Calendar c = Calendar.getInstance();
            c.setTime(referenceDate);
            picker.getDatePicker().setMaxDate(c.getTimeInMillis());
            picker.show();
        });

        EditText kidsNameEditText = dialog.findViewById(R.id.editKidsName);
        continueButtonWidget.setOnClickListener(view -> {
            if (kidsDobTextView.getText() == null || StringUtils.isNullOrEmpty(kidsDobTextView.getText().toString())) {
                showToast("Please select a valid date");
            } else {
                String date = kidsDobTextView.getText().toString();
                long timeInMillis = DateTimeUtils.convertStringToMilliTimestamp(date);
                if (timeInMillis == 0) {
                    showToast("Please select a valid date");
                } else {
                    KidsModel kidsModel = new KidsModel();
                    if (spinnerGender.getSelectedItemPosition() == 0) {
                        kidsModel.setGender("Male");
                    } else {
                        kidsModel.setGender("Female");
                    }
                    kidsModel.setName(kidsNameEditText.getText().toString());
                    kidsModel.setBirthDay("" + timeInMillis);
                    ArrayList<KidsModel> kidsList = new ArrayList<>();
                    kidsList.add(kidsModel);
                    UserDetailResult userDetailResult = new UserDetailResult();
                    userDetailResult.setIsMother("1");
                    userDetailResult.setKids(kidsList);
                    saveExpectedDateOrKidsToUserDashboard(userDetailResult);
                }
            }
        });
        ImageView cancelDialog = dialog.findViewById(R.id.cancel);
        cancelDialog.setOnClickListener(view -> dialog.dismiss());
        dialog.show();
    }

    private void saveExpectedDateOrKidsToUserDashboard(UserDetailResult userDetailResult) {
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        LoginRegistrationAPI updateUserDetailApi = retrofit.create(LoginRegistrationAPI.class);
        Call<RewardsPersonalResponse> call = updateUserDetailApi
                .updateUserDetails(SharedPrefUtils.getUserDetailModel(this).getDynamoId(),
                        userDetailResult);
        call.enqueue(new Callback<RewardsPersonalResponse>() {
            @Override
            public void onResponse(Call<RewardsPersonalResponse> call,
                    Response<RewardsPersonalResponse> response) {
                if (response.body() == null) {
                    NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                    FirebaseCrashlytics.getInstance().recordException(nee);
                    return;
                }
                try {
                    if (response.isSuccessful()) {
                        RewardsPersonalResponse updateUserResponse = response.body();
                        if (updateUserResponse.getCode() == 200 && Constants.SUCCESS
                                .equals(updateUserResponse.getStatus())) {
                            if ("expectedDate".equals(selectedGroup.getQuestionnaire().get("name"))) {
                                saveExpectedDateOrYoungestChildDobToMembership(userDetailResult.getExpectedDate(),
                                        "expectedDate");
                            } else if ("dob".equals(selectedGroup.getQuestionnaire().get("name"))) {
                                saveExpectedDateOrYoungestChildDobToMembership(
                                        userDetailResult.getKids().get(0).getBirthDay(), "dob");
                            }
                        } else {
                            showToast(updateUserResponse.getReason());
                        }
                    }
                } catch (Exception e) {
                    FirebaseCrashlytics.getInstance().recordException(e);
                    Log.d("MC4kException", Log.getStackTraceString(e));
                }
            }

            @Override
            public void onFailure(Call<RewardsPersonalResponse> call, Throwable t) {
                FirebaseCrashlytics.getInstance().recordException(t);
                Log.d("MC4kException", Log.getStackTraceString(t));
            }
        });
    }

    private void saveExpectedDateOrYoungestChildDobToMembership(String timeInMillis, String responseQuestionnaireKey) {
        UpdateGroupMembershipRequest updateRequest = new UpdateGroupMembershipRequest();
        updateRequest.setUserId(SharedPrefUtils.getUserDetailModel(this).getDynamoId());
        Map<String, String> map = new LinkedTreeMap<>();
        map.put(responseQuestionnaireKey, timeInMillis);
        updateRequest.setQuestionnaireResponse(map);
        Retrofit retrofit = BaseApplication.getInstance().getGroupsRetrofit();
        GroupsAPI groupsApi = retrofit.create(GroupsAPI.class);
        Call<GroupsMembershipResponse> groupsCall = groupsApi
                .updateMember(currentUserMembershipId, updateRequest);
        groupsCall.enqueue(updateGroupMembershipResponseCallback);
    }

    private void showFirstTimeJoinerDialog() {
        dialog = new Dialog(this);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_group_yourself);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        justJoinedPostTextView = dialog.findViewById(R.id.post_edit);
        anonymousCheckbox = dialog.findViewById(R.id.anonymousCheckbox);
        ImageView anonymousImageView = dialog.findViewById(R.id.anonymousImageView);
        TextView anonymousTextView = dialog.findViewById(R.id.anonymousTextView);

        if (selectedGroup.getAnnonAllowed() != 0) {
            SharedPrefUtils.setUserAnonymous(BaseApplication.getAppContext(), false);
            anonymousCheckbox.setChecked(false);
            anonymousCheckbox.setVisibility(View.INVISIBLE);
            anonymousImageView.setVisibility(View.INVISIBLE);
            anonymousTextView.setVisibility(View.INVISIBLE);
        } else {
            if (SharedPrefUtils.isUserAnonymous(BaseApplication.getAppContext())) {
                anonymousCheckbox.setChecked(true);
            } else {
                anonymousCheckbox.setChecked(false);
            }
        }

        anonymousCheckbox.setOnClickListener(view -> {
            if (anonymousCheckbox.isChecked()) {
                SharedPrefUtils.setUserAnonymous(BaseApplication.getAppContext(), true);
            } else {
                SharedPrefUtils.setUserAnonymous(BaseApplication.getAppContext(), false);
            }

        });
        dialog.findViewById(R.id.cross).setOnClickListener(view -> dialog.dismiss());
        dialog.findViewById(R.id.postTextView).setOnClickListener(view -> {
            if (!isRequestRunning && validateParams()) {
                isRequestRunning = true;
                publishPost();
            }
        });

        dialog.show();
    }

    private void getGroupPosts() {
        Retrofit retrofit = BaseApplication.getInstance().getGroupsRetrofit();
        GroupsAPI groupsApi = retrofit.create(GroupsAPI.class);
        if (selectedGroup != null) {
            Call<GroupPostResponse> call = groupsApi.getAllPostsForAGroup(selectedGroup.getId(), skip, limit);
            call.enqueue(groupPostResponseCallback);
        }
    }

    private void getFilteredGroupPosts() {
        Retrofit retrofit = BaseApplication.getInstance().getGroupsRetrofit();
        GroupsAPI groupsApi = retrofit.create(GroupsAPI.class);
        if (selectedGroup != null) {
            Call<GroupPostResponse> call = groupsApi
                    .getAllFilteredPostsForAGroup(selectedGroup.getId(), skip, limit, postType);
            call.enqueue(groupPostResponseCallback);
        }
    }

    private Callback<GroupPostResponse> groupPostResponseCallback = new Callback<GroupPostResponse>() {
        @Override
        public void onResponse(Call<GroupPostResponse> call, retrofit2.Response<GroupPostResponse> response) {
            isRequestRunning = false;
            if (response.body() == null) {
                NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                FirebaseCrashlytics.getInstance().recordException(nee);
                return;
            }
            try {
                if (response.isSuccessful()) {
                    GroupPostResponse groupPostResponse = response.body();
                    processPostListingResponse(groupPostResponse);
                }
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<GroupPostResponse> call, Throwable t) {
            isRequestRunning = false;
            FirebaseCrashlytics.getInstance().recordException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    private void processPostListingResponse(GroupPostResponse response) {
        totalPostCount = response.getTotal();
        ArrayList<GroupPostResult> dataList = (ArrayList<GroupPostResult>) response.getData().get(0).getResult();
        if (dataList.size() == 0) {
            //No more next results for search from pagination
            isLastPageReached = null != postList && !postList.isEmpty();
        } else {
            formatPostData(dataList);
            noPostsTextView.setVisibility(View.GONE);
            postList.addAll(dataList);
            groupsGenericPostRecyclerAdapter.setData(postList);
            skip = skip + limit;
            if (skip >= totalPostCount) {
                isLastPageReached = true;
            }
            groupsGenericPostRecyclerAdapter.notifyDataSetChanged();
        }
    }

    private void processSearchResultListing(GroupPostResponse postSearchResponse) {
        totalPostCount = postSearchResponse.getTotal();
        if (totalPostCount == 0) {
            return;
        }
        ArrayList<GroupPostResult> dataList = (ArrayList<GroupPostResult>) postSearchResponse.getData().get(0)
                .getResult();
        if (dataList.size() == 0) {
            isLastPageReached = false;
            if (null != postList && !postList.isEmpty()) {
                //No more next results for search from pagination
                isLastPageReached = true;
            }
        } else {
            formatPostData(dataList);
            noPostsTextView.setVisibility(View.GONE);
            postList.addAll(dataList);
            groupsGenericPostRecyclerAdapter.setData(postList);
            skip = skip + limit;
            if (skip >= totalPostCount) {
                isLastPageReached = true;
            }
            groupsGenericPostRecyclerAdapter.notifyDataSetChanged();
        }
    }

    private void formatPostData(ArrayList<GroupPostResult> dataList) {
        for (int j = 0; j < dataList.size(); j++) {
            if (dataList.get(j).getMediaUrls() != null && !((Map<String, String>) dataList.get(j).getMediaUrls())
                    .isEmpty()) {
                if (((Map<String, String>) dataList.get(j).getMediaUrls()).get("audio") != null) {
                    dataList.get(j).setCommentType(AppConstants.COMMENT_TYPE_AUDIO);
                }
            }
            if (dataList.get(j).getCounts() != null) {
                for (int i = 0; i < dataList.get(j).getCounts().size(); i++) {
                    switch (dataList.get(j).getCounts().get(i).getName()) {
                        case "helpfullCount":
                            dataList.get(j).setHelpfullCount(dataList.get(j).getCounts().get(i).getCount());
                            break;
                        case "notHelpfullCount":
                            dataList.get(j).setNotHelpfullCount(dataList.get(j).getCounts().get(i).getCount());
                            break;
                        case "responseCount":
                            dataList.get(j).setResponseCount(dataList.get(j).getCounts().get(i).getCount());
                            break;
                        case "votesCount": {
                            for (int k = 0; k < dataList.get(j).getCounts().get(i).getCounts().size(); k++) {
                                dataList.get(j).setTotalVotesCount(
                                        dataList.get(j).getTotalVotesCount() + dataList.get(j).getCounts().get(i)
                                                .getCounts().get(k).getCount());
                                switch (dataList.get(j).getCounts().get(i).getCounts().get(k).getName()) {
                                    case "option1":
                                        dataList.get(j).setOption1VoteCount(
                                                dataList.get(j).getCounts().get(i).getCounts().get(k).getCount());
                                        break;
                                    case "option2":
                                        dataList.get(j).setOption2VoteCount(
                                                dataList.get(j).getCounts().get(i).getCounts().get(k).getCount());
                                        break;
                                    case "option3":
                                        dataList.get(j).setOption3VoteCount(
                                                dataList.get(j).getCounts().get(i).getCounts().get(k).getCount());
                                        break;
                                    case "option4":
                                        dataList.get(j).setOption4VoteCount(
                                                dataList.get(j).getCounts().get(i).getCounts().get(k).getCount());
                                        break;
                                    default:
                                        break;
                                }
                            }
                        }
                        break;
                        default:
                            break;
                    }
                }
            }
        }
    }

    public void addComment(String content, Map<String, String> image, int groupId, int postId) {
        this.postId = postId;
        MixpanelAPI mixpanel = MixpanelAPI.getInstance(BaseApplication.getAppContext(), AppConstants.MIX_PANEL_TOKEN);
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("userId", SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId());
            jsonObject.put("groupId", "" + groupId);
            jsonObject.put("postId", "" + postId);
            mixpanel.track("CreateGroupComment", jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
        AddGpPostCommentOrReplyRequest addGpPostCommentOrReplyRequest = new AddGpPostCommentOrReplyRequest();
        addGpPostCommentOrReplyRequest.setGroupId(groupId);
        addGpPostCommentOrReplyRequest.setPostId(postId);
        if (SharedPrefUtils.isUserAnonymous(BaseApplication.getAppContext())) {
            addGpPostCommentOrReplyRequest.setIsAnnon(1);
        }
        addGpPostCommentOrReplyRequest.setUserId(SharedPrefUtils.getUserDetailModel(this).getDynamoId());
        addGpPostCommentOrReplyRequest.setContent(content);
        addGpPostCommentOrReplyRequest.setMediaUrls(image);
        Retrofit retrofit = BaseApplication.getInstance().getGroupsRetrofit();
        GroupsAPI groupsApi = retrofit.create(GroupsAPI.class);
        Call<AddGpPostCommentReplyResponse> call = groupsApi.addPostCommentOrReply(addGpPostCommentOrReplyRequest);
        call.enqueue(addCommentResponseListener);
    }

    private Callback<AddGpPostCommentReplyResponse> addCommentResponseListener =
            new Callback<AddGpPostCommentReplyResponse>() {
                @Override
                public void onResponse(Call<AddGpPostCommentReplyResponse> call,
                        retrofit2.Response<AddGpPostCommentReplyResponse> response) {
                    if (response.body() == null) {
                        NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                        FirebaseCrashlytics.getInstance().recordException(nee);
                        showToast("Failed to add comment. Please try again");
                        return;
                    }
                    try {
                        if (response.isSuccessful()) {
                            for (int i = 0; i < postList.size(); i++) {
                                if (postList.get(i).getId() == postId) {
                                    postList.get(i).setResponseCount(1);
                                    groupsGenericPostRecyclerAdapter.notifyDataSetChanged();
                                    break;
                                }
                            }
                        } else {
                            showToast("Failed to add comment. Please try again");
                        }
                    } catch (Exception e) {
                        showToast("Failed to add comment. Please try again");
                        FirebaseCrashlytics.getInstance().recordException(e);
                        Log.d("MC4kException", Log.getStackTraceString(e));
                    }
                }

                @Override
                public void onFailure(Call<AddGpPostCommentReplyResponse> call, Throwable t) {
                    showToast("Failed to add comment. Please try again");
                    FirebaseCrashlytics.getInstance().recordException(t);
                    Log.d("MC4kException", Log.getStackTraceString(t));
                }
            };


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.shareGroupImageViewLinearLayoutContainer:
            case R.id.shareGroupImageView:
                MixpanelAPI mixpanel = MixpanelAPI
                        .getInstance(BaseApplication.getAppContext(), AppConstants.MIX_PANEL_TOKEN);
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("userId",
                            SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId());
                    jsonObject.put("groupId", "" + groupId);
                    mixpanel.track("GroupInvite", jsonObject);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
                shareIntent.setType("text/plain");

                String shareUrl = AppConstants.WEB_URL + selectedGroup.getUrl();
                shareIntent.putExtra(android.content.Intent.EXTRA_TEXT,
                        selectedGroup.getDescription() + "\n\n" + "Join " + selectedGroup.getTitle()
                                + " support group\n" + shareUrl);
                startActivity(Intent.createChooser(shareIntent, "Momspresso"));
                break;
            case R.id.clearSearchImageView: {
                toolbarTitle.setText("");
            }
            break;
            case R.id.groupSettingsImageView: {
                Utils.groupsEvent(GroupDetailsActivity.this, "Groups_Discussion", "Setting", "android",
                        SharedPrefUtils.getAppLocale(BaseApplication.getAppContext()),
                        SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId(),
                        String.valueOf(System.currentTimeMillis()), "Settings page", "", "");
                Intent intent = new Intent(GroupDetailsActivity.this, GroupSettingsActivity.class);
                intent.putExtra("groupItem", selectedGroup);
                intent.putExtra("currentUserMembershipId", currentUserMembershipId);
                intent.putExtra("expectedDate", questionnaireResponse.get("expectedDate"));
                intent.putExtra("memberType", memberType);
                startActivity(intent);
            }
            break;
            case R.id.addPostFAB:
                hideBottomDrawer.setVisibility(View.VISIBLE);
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                break;
            case R.id.hideBottomDrawer:
                hideBottomSheet();
                break;
            case R.id.postContainerR:
            case R.id.postContainer:
                Utils.groupsEvent(GroupDetailsActivity.this, "Create post page", "Post", "android",
                        SharedPrefUtils.getAppLocale(BaseApplication.getAppContext()),
                        SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId(),
                        String.valueOf(System.currentTimeMillis()), "Create post screen", "", String.valueOf(groupId));
                Intent intent = new Intent(GroupDetailsActivity.this, AddTextOrMediaGroupPostActivity.class);
                intent.putExtra("groupItem", selectedGroup);
                startActivityForResult(intent, 1111);
                hideBottomSheet();
                break;
            case R.id.announcementContainerR:
            case R.id.postAudioContainer:
                Utils.groupsEvent(GroupDetailsActivity.this, "Create post page", "Audio", "android",
                        SharedPrefUtils.getAppLocale(BaseApplication.getAppContext()),
                        SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId(),
                        String.valueOf(System.currentTimeMillis()), "Audio post screen", "", String.valueOf(groupId));
                Intent audioIntent = new Intent(GroupDetailsActivity.this, AddAudioGroupPostActivity.class);
                audioIntent.putExtra("groupItem", selectedGroup);
                startActivityForResult(audioIntent, 1111);
                hideBottomSheet();
                break;
            case R.id.pollContainerR:
            case R.id.pollContainer:
                Utils.groupsEvent(GroupDetailsActivity.this, "Create post page", "polls", "android",
                        SharedPrefUtils.getAppLocale(BaseApplication.getAppContext()),
                        SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId(),
                        String.valueOf(System.currentTimeMillis()), "Create poll screen", "", String.valueOf(groupId));
                Intent pollIntent = new Intent(GroupDetailsActivity.this, AddPollGroupPostActivity.class);
                pollIntent.putExtra("groupItem", selectedGroup);
                startActivityForResult(pollIntent, 1111);
                hideBottomSheet();
                break;
            case R.id.closeImageView:
                Utils.groupsEvent(GroupDetailsActivity.this, "Create post page", "Cancel X sign", "android",
                        SharedPrefUtils.getAppLocale(BaseApplication.getAppContext()),
                        SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId(),
                        String.valueOf(System.currentTimeMillis()), "click", "", String.valueOf(groupId));
                if (addPostContainer.getVisibility() == View.VISIBLE) {
                    addPostContainer.setVisibility(View.GONE);
                }
                break;
            case R.id.savePostTextView:
                if (savePostTextView.getText().toString().equals(getString(R.string.groups_save_post))) {
                    updateUserPostPreferences("savePost");
                } else {
                    updateUserPostPreferences("deletePost");
                }
                break;
            case R.id.commentToggleTextView:
                if (commentToggleTextView.getText().toString().equals(getString(R.string.groups_disable_comment))) {
                    Utils.groupsEvent(GroupDetailsActivity.this, "Group_discussion_Post ActionView (...)",
                            "enable comment", "android", SharedPrefUtils.getAppLocale(BaseApplication.getAppContext()),
                            SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId(),
                            String.valueOf(System.currentTimeMillis()), "click", "", String.valueOf(postId));
                    updatePostCommentSettings(1);
                } else {
                    Utils.groupsEvent(GroupDetailsActivity.this, "Group_discussion_Post ActionView (...)",
                            "disable comment", "android", SharedPrefUtils.getAppLocale(BaseApplication.getAppContext()),
                            SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId(),
                            String.valueOf(System.currentTimeMillis()), "click", "", String.valueOf(postId));
                    updatePostCommentSettings(0);
                }
                break;
            case R.id.notificationToggleTextView:
                if (notificationToggleTextView.getText().toString().equals("DISABLE NOTIFICATION")) {
                    Utils.groupsEvent(GroupDetailsActivity.this, "Group_discussion_Post ActionView (...)",
                            "enable notification ", "android",
                            SharedPrefUtils.getAppLocale(BaseApplication.getAppContext()),
                            SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId(),
                            String.valueOf(System.currentTimeMillis()), "click", "", String.valueOf(postId));
                    updateUserPostPreferences("enableNotif");
                } else {
                    Utils.groupsEvent(GroupDetailsActivity.this, "Group_discussion_Post ActionView (...)",
                            "disable notification ", "android",
                            SharedPrefUtils.getAppLocale(BaseApplication.getAppContext()),
                            SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId(),
                            String.valueOf(System.currentTimeMillis()), "click", "", String.valueOf(postId));
                    updateUserPostPreferences("disableNotif");
                }
                break;
            case R.id.editPostTextView:
                openEditPostOption();
                break;
            case R.id.deletePostTextView:
                Utils.groupsEvent(GroupDetailsActivity.this, "Group_discussion_Post ActionView (...)", "Delete post",
                        "android", SharedPrefUtils.getAppLocale(BaseApplication.getAppContext()),
                        SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId(),
                        String.valueOf(System.currentTimeMillis()), "click", "", String.valueOf(postId));

                Log.d("deletePostTextView", "" + selectedPost.getId());
                updateAdminLevelPostPrefs("markInactive");
                break;
            case R.id.blockUserTextView:
                Utils.groupsEvent(GroupDetailsActivity.this, "Group_discussion_Post ActionView (...)",
                        "Block this user", "android", SharedPrefUtils.getAppLocale(BaseApplication.getAppContext()),
                        SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId(),
                        String.valueOf(System.currentTimeMillis()), "click", "", String.valueOf(postId));
                blockUserWithPostId(selectedPost.getId());
                break;
            case R.id.pinPostTextView:
                Utils.groupsEvent(GroupDetailsActivity.this, "Group_discussion_Post ActionView (...)",
                        "pin this post to the top", "android",
                        SharedPrefUtils.getAppLocale(BaseApplication.getAppContext()),
                        SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId(),
                        String.valueOf(System.currentTimeMillis()), "click", "", String.valueOf(postId));
                if (pinPostTextView.getText().toString().equals(getString(R.string.groups_pin_post))) {
                    updateAdminLevelPostPrefs("pinPost");
                } else {
                    updateAdminLevelPostPrefs("unpinPost");
                }
                break;
            case R.id.reportPostTextView:
                Utils.groupsEvent(GroupDetailsActivity.this, "Group_discussion_Post ActionView (...)",
                        "report this post", "android", SharedPrefUtils.getAppLocale(BaseApplication.getAppContext()),
                        SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId(),
                        String.valueOf(System.currentTimeMillis()), "click", "", String.valueOf(postId));
                Bundle args = new Bundle();
                args.putInt("groupId", groupId);
                args.putInt("postId", selectedPost.getId());
                args.putString("type", AppConstants.GROUP_REPORT_TYPE_POST);
                GroupPostReportDialogFragment groupPostReportDialogFragment = new GroupPostReportDialogFragment();
                groupPostReportDialogFragment.setArguments(args);
                groupPostReportDialogFragment.setCancelable(true);
                FragmentManager fm = getSupportFragmentManager();
                groupPostReportDialogFragment.show(fm, "Choose video report option");
            case R.id.overlayView:
                postSettingsContainerMain.setVisibility(View.GONE);
                overlayView.setVisibility(View.GONE);
                postSettingsContainer.setVisibility(View.GONE);
                break;
            case R.id.forYouFilterWidget:
                removeRecyclerViewDecorator();
                filterContentContainer.setVisibility(View.VISIBLE);
                forYouFilterWidget.setSelected(true);
                monthFilterWidget.setSelected(false);
                categoryFilterWidget.setSelected(false);
                initializeCollectionsItemsList(getCollectionIdForMonth());
                break;
            case R.id.monthFilterWidget:
                removeRecyclerViewDecorator();
                forYouFilterWidget.setSelected(false);
                monthFilterWidget.setSelected(true);
                categoryFilterWidget.setSelected(false);
                GridLayoutManager llm1 = new GridLayoutManager(GroupDetailsActivity.this, 3);
                parentCollectionId = selectedGroup.getCollectionByMonth();
                initializeVirtualClinicLayout(llm1);
                break;
            case R.id.categoryFilterWidget:
                removeRecyclerViewDecorator();
                forYouFilterWidget.setSelected(false);
                monthFilterWidget.setSelected(false);
                categoryFilterWidget.setSelected(true);
                GridLayoutManager llm2 = new GridLayoutManager(GroupDetailsActivity.this, 2);
                parentCollectionId = selectedGroup.getCollectionByCategory();
                initializeVirtualClinicLayout(llm2);
                break;
            default:
                break;
        }
    }

    private String getCollectionIdForMonth() {
        ArrayList<VirtualClinicCollectionSettings> collectionList = selectedGroup.getSettings().getCollectionSettings();
        try {
            String eventDate = "";
            if ("expectedDate".equals(selectedGroup.getQuestionnaire().get("name"))) {
                eventDate = DateTimeUtils.getDOBMilliTimestamp(questionnaireResponse.get("expectedDate"));
            } else {
                eventDate = DateTimeUtils.getDOBMilliTimestamp(questionnaireResponse.get("dob"));
            }
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            LocalDate dt = LocalDate.parse(eventDate, formatter);
            LocalDate currentdate = LocalDate.now();

            Period period = Period.between(currentdate, dt);
            int months;
            if (period.getYears() < 1) {
                months = period.getMonths();
            } else {
                months = period.getYears() * 12 + period.getMonths();
            }
            for (int i = 0; i < collectionList.size(); i++) {
                if (collectionList.get(i).getMin() <= months && months < collectionList.get(i).getMax()) {
                    Log.e("MIN", "MIN === " + collectionList.get(i).getMin());
                    return collectionList.get(i).getCollectionId();
                }
            }
            return collectionList.get(0).getCollectionId();
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            Log.d("MC4kException", Log.getStackTraceString(e));
            return collectionList.get(0).getCollectionId();
        }
    }

    private void openEditPostOption() {
        Intent intent = new Intent(this, GroupsEditPostActivity.class);
        intent.putExtra("postData", selectedPost);
        startActivityForResult(intent, EDIT_POST_REQUEST_CODE);
    }

    private void updateAdminLevelPostPrefs(String actionType) {
        Retrofit retrofit = BaseApplication.getInstance().getGroupsRetrofit();
        GroupsAPI groupsApi = retrofit.create(GroupsAPI.class);
        UpdatePostSettingsRequest request = new UpdatePostSettingsRequest();
        if ("pinPost".equals(actionType)) {
            request.setIsPinned(1);
            request.setIsActive(1);
            request.setPinnedBy(SharedPrefUtils.getUserDetailModel(this).getDynamoId());
        } else if ("unpinPost".equals(actionType)) {
            request.setIsPinned(0);
            request.setIsActive(1);
            request.setPinnedBy(SharedPrefUtils.getUserDetailModel(this).getDynamoId());
        } else if ("blockUser".equals(actionType)) {
            getPostingUsersMembershipDetails(selectedPost.getGroupId(), selectedPost.getUserId());
            return;
        } else if ("markInactive".equals(actionType)) {
            request.setIsActive(0);
            request.setIsPinned(0);
        }
        Call<GroupPostResponse> call = groupsApi.updatePost(selectedPost.getId(), request);
        call.enqueue(updateAdminLvlPostSettingResponseCallback);
    }

    private void hideBottomSheet() {
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        hideBottomDrawer.setVisibility(View.GONE);
    }

    private void blockUserWithPostId(int postId) {
        Retrofit retrofit = BaseApplication.getInstance().getGroupsRetrofit();
        GroupsAPI groupsApi = retrofit.create(GroupsAPI.class);
        Call<GroupsMembershipResponse> call = groupsApi.blockUserWithPostId(postId);
        call.enqueue(blockUserResponseCallback);
    }

    private Callback<GroupsMembershipResponse> blockUserResponseCallback = new Callback<GroupsMembershipResponse>() {
        @Override
        public void onResponse(Call<GroupsMembershipResponse> call, Response<GroupsMembershipResponse> response) {
            if (response.body() == null) {
                NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                FirebaseCrashlytics.getInstance().recordException(nee);
                return;
            }
            try {
                if (response.isSuccessful()) {
                    showToast(getString(R.string.groups_user_block_success));
                }
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<GroupsMembershipResponse> call, Throwable t) {
            FirebaseCrashlytics.getInstance().recordException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    private void getPostingUsersMembershipDetails(int groupId, String postsUserId) {
        Retrofit retrofit = BaseApplication.getInstance().getGroupsRetrofit();
        GroupsAPI groupsApi = retrofit.create(GroupsAPI.class);

        Call<GroupsMembershipResponse> call = groupsApi.getUsersMembershipDetailsForGroup(groupId, postsUserId);
        call.enqueue(getMembershipDetailsReponseCallback);
    }

    private Callback<GroupsMembershipResponse> getMembershipDetailsReponseCallback =
            new Callback<GroupsMembershipResponse>() {
                @Override
                public void onResponse(Call<GroupsMembershipResponse> call,
                        retrofit2.Response<GroupsMembershipResponse> response) {
                    if (response.body() == null) {
                        NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                        FirebaseCrashlytics.getInstance().recordException(nee);
                        return;
                    }
                    try {
                        if (response.isSuccessful()) {
                            GroupsMembershipResponse membershipResponse = response.body();
                            Retrofit retrofit = BaseApplication.getInstance().getGroupsRetrofit();
                            GroupsAPI groupsApi = retrofit.create(GroupsAPI.class);

                            UpdateGroupMembershipRequest updateGroupMembershipRequest =
                                    new UpdateGroupMembershipRequest();
                            updateGroupMembershipRequest.setUserId(selectedPost.getUserId());
                            updateGroupMembershipRequest.setStatus(AppConstants.GROUP_MEMBERSHIP_STATUS_BLOCKED);
                            Call<GroupsMembershipResponse> call1 = groupsApi
                                    .updateMember(membershipResponse.getData().getResult().get(0).getId(),
                                            updateGroupMembershipRequest);
                            call1.enqueue(updateGroupMembershipResponseCallback);
                        }
                    } catch (Exception e) {
                        FirebaseCrashlytics.getInstance().recordException(e);
                        Log.d("MC4kException", Log.getStackTraceString(e));
                    }
                }

                @Override
                public void onFailure(Call<GroupsMembershipResponse> call, Throwable t) {
                    FirebaseCrashlytics.getInstance().recordException(t);
                    Log.d("MC4kException", Log.getStackTraceString(t));
                }
            };

    private Callback<GroupsMembershipResponse> updateGroupMembershipResponseCallback =
            new Callback<GroupsMembershipResponse>() {
                @Override
                public void onResponse(Call<GroupsMembershipResponse> call,
                        retrofit2.Response<GroupsMembershipResponse> response) {
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                    if (response.body() == null) {
                        NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                        FirebaseCrashlytics.getInstance().recordException(nee);
                        return;
                    }
                    try {
                        if (response.isSuccessful()) {
                            GroupsMembershipResponse groupsMembershipResponse = response.body();
                            questionnaireResponse = groupsMembershipResponse.getData().getResult().get(0)
                                    .getQuestionnaireResponse();
                            postSettingsContainerMain.setVisibility(View.GONE);
                        }
                    } catch (Exception e) {
                        FirebaseCrashlytics.getInstance().recordException(e);
                        Log.d("MC4kException", Log.getStackTraceString(e));
                    }
                }

                @Override
                public void onFailure(Call<GroupsMembershipResponse> call, Throwable t) {
                    FirebaseCrashlytics.getInstance().recordException(t);
                    Log.d("MC4kException", Log.getStackTraceString(t));
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                }
            };

    private Callback<GroupPostResponse> updateAdminLvlPostSettingResponseCallback = new Callback<GroupPostResponse>() {
        @Override
        public void onResponse(Call<GroupPostResponse> call, retrofit2.Response<GroupPostResponse> response) {
            if (response.body() == null) {
                NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                FirebaseCrashlytics.getInstance().recordException(nee);
                return;
            }
            try {
                if (response.isSuccessful()) {
                    GroupPostResponse updatePostResponse = response.body();
                    postSettingsContainerMain.setVisibility(View.GONE);
                    overlayView.setVisibility(View.GONE);
                    postSettingsContainer.setVisibility(View.GONE);
                    clearSearchImageView.setVisibility(View.GONE);
                    skip = 0;
                    limit = 10;
                    isRequestRunning = false;
                    isLastPageReached = false;
                    postList.clear();
                    groupsGenericPostRecyclerAdapter.notifyDataSetChanged();
                    TabLayout.Tab tab = groupPostTabLayout.getTabAt(1);
                    tab.select();
                }
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<GroupPostResponse> call, Throwable t) {
            FirebaseCrashlytics.getInstance().recordException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    private void updatePostCommentSettings(int status) {
        Retrofit retrofit = BaseApplication.getInstance().getGroupsRetrofit();
        GroupsAPI groupsApi = retrofit.create(GroupsAPI.class);

        UpdateGroupPostRequest updateGroupPostRequest = new UpdateGroupPostRequest();
        updateGroupPostRequest.setGroupId(selectedGroup.getId());
        updateGroupPostRequest.setDisableComments(status);

        Call<GroupPostResponse> call = groupsApi.disablePostComment(selectedPost.getId(), updateGroupPostRequest);
        call.enqueue(postUpdateResponseListener);
    }

    private Callback<GroupPostResponse> postUpdateResponseListener = new Callback<GroupPostResponse>() {
        @Override
        public void onResponse(Call<GroupPostResponse> call, retrofit2.Response<GroupPostResponse> response) {
            if (response.body() == null) {
                NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                FirebaseCrashlytics.getInstance().recordException(nee);
                return;
            }
            try {
                if (response.isSuccessful()) {
                    GroupPostResponse groupPostResponse = response.body();
                    if (groupPostResponse.getData().get(0).getResult().get(0).getDisableComments() == 1) {
                        commentToggleTextView.setText(getString(R.string.groups_enable_comment));
                        selectedPost.setDisableComments(1);
                    } else {
                        commentToggleTextView.setText(getString(R.string.groups_disable_comment));
                        selectedPost.setDisableComments(0);
                    }
                }
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<GroupPostResponse> call, Throwable t) {
            FirebaseCrashlytics.getInstance().recordException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    private void updateUserPostPreferences(String action) {
        Retrofit retrofit = BaseApplication.getInstance().getGroupsRetrofit();
        GroupsAPI groupsApi = retrofit.create(GroupsAPI.class);
        UpdateUserPostSettingsRequest request = new UpdateUserPostSettingsRequest();
        request.setPostId(selectedPost.getId());
        request.setIsAnno(selectedPost.getIsAnnon());
        request.setUserId(SharedPrefUtils.getUserDetailModel(this).getDynamoId());
        if (currentPostPrefsForUser == null) {
            if ("savePost".equals(action)) {
                request.setIsBookmarked(1);
                request.setNotificationOff(1);
            } else if ("deletePost".equals(action)) {
                request.setIsBookmarked(0);
                request.setNotificationOff(1);
            } else if ("enableNotif".equals(action)) {
                request.setIsBookmarked(0);
                request.setNotificationOff(1);
            } else if ("disableNotif".equals(action)) {
                request.setIsBookmarked(0);
                request.setNotificationOff(0);
            }
            Call<ResponseBody> call = groupsApi.createNewPostSettingsForUser(request);
            call.enqueue(createPostSettingForUserResponseCallback);
        } else {
            if ("savePost".equals(action)) {
                request.setIsBookmarked(1);
                request.setNotificationOff(currentPostPrefsForUser.getNotificationOff());
            } else if ("deletePost".equals(action)) {
                request.setIsBookmarked(0);
                request.setNotificationOff(currentPostPrefsForUser.getNotificationOff());
            } else if ("enableNotif".equals(action)) {
                request.setIsBookmarked(currentPostPrefsForUser.getIsBookmarked());
                request.setNotificationOff(1);
            } else if ("disableNotif".equals(action)) {
                request.setIsBookmarked(currentPostPrefsForUser.getIsBookmarked());
                request.setNotificationOff(0);
            }
            Call<UserPostSettingResponse> call = groupsApi
                    .updatePostSettingsForUser(currentPostPrefsForUser.getId(), request);
            call.enqueue(updatePostSettingForUserResponseCallback);
        }
    }

    private Callback<ResponseBody> createPostSettingForUserResponseCallback = new Callback<ResponseBody>() {
        @Override
        public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
            if (response.body() == null) {
                NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                FirebaseCrashlytics.getInstance().recordException(nee);
                return;
            }
            try {
                if (response.isSuccessful()) {
                    String resData = new String(response.body().bytes());
                    JSONObject jsonObject = new JSONObject(resData);
                    currentPostPrefsForUser = new UserPostSettingResult();
                    currentPostPrefsForUser.setId(jsonObject.getJSONObject("data").getJSONObject("result")
                            .getInt("id"));
                    if (jsonObject.getJSONObject("data").getJSONObject("result").getBoolean("notificationOff")) {
                        notificationToggleTextView.setText(getString(R.string.groups_enable_notification));
                    } else {
                        notificationToggleTextView.setText("DISABLE NOTIFICATION");
                    }
                    if (jsonObject.getJSONObject("data").getJSONObject("result").getBoolean("isBookmarked")) {
                        savePostTextView.setText(getString(R.string.groups_remove_post));
                    } else {
                        savePostTextView.setText(getString(R.string.groups_save_post));
                    }
                }
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<ResponseBody> call, Throwable t) {
            FirebaseCrashlytics.getInstance().recordException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    private Callback<UserPostSettingResponse> updatePostSettingForUserResponseCallback =
            new Callback<UserPostSettingResponse>() {
                @Override
                public void onResponse(Call<UserPostSettingResponse> call,
                        retrofit2.Response<UserPostSettingResponse> response) {
                    if (response.body() == null) {
                        NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                        FirebaseCrashlytics.getInstance().recordException(nee);
                        return;
                    }
                    try {
                        if (response.isSuccessful()) {
                            UserPostSettingResponse userPostSettingResponse = response.body();
                            currentPostPrefsForUser = userPostSettingResponse.getData().get(0).getResult().get(0);
                            if (userPostSettingResponse.getData().get(0).getResult().get(0).getNotificationOff() == 1) {
                                notificationToggleTextView.setText(getString(R.string.groups_enable_notification));
                            } else {
                                notificationToggleTextView.setText("DISABLE NOTIFICATION");
                            }
                            if (userPostSettingResponse.getData().get(0).getResult().get(0).getIsBookmarked() == 1) {
                                savePostTextView.setText(getString(R.string.groups_remove_post));
                            } else {
                                savePostTextView.setText(getString(R.string.groups_save_post));
                            }
                        }
                    } catch (Exception e) {
                        FirebaseCrashlytics.getInstance().recordException(e);
                        Log.d("MC4kException", Log.getStackTraceString(e));
                    }
                }

                @Override
                public void onFailure(Call<UserPostSettingResponse> call, Throwable t) {
                    FirebaseCrashlytics.getInstance().recordException(t);
                    Log.d("MC4kException", Log.getStackTraceString(t));
                }
            };

    @Override
    public void onBackPressed() {
        Utils.groupsEvent(GroupDetailsActivity.this, "Groups_Discussion", "Back arrow", "android",
                SharedPrefUtils.getAppLocale(BaseApplication.getAppContext()),
                SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId(),
                String.valueOf(System.currentTimeMillis()), "Groups listing ", "", "");

        if (addPostContainer.getVisibility() == View.VISIBLE) {
            addPostContainer.setVisibility(View.GONE);
        } else {
            if ("questionnaire".equals(source)) {
                Intent intent = new Intent(this, DashboardActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            } else {
                super.onBackPressed();
            }
        }
        if (groupsGenericPostRecyclerAdapter != null) {
            groupsGenericPostRecyclerAdapter.releasePlayer();
        }
    }

    private void setUpTabLayout(String[] sections, int tabType) {
        if (tabType == 0) {
            for (int i = 0; i < sections.length; i++) {
                TabLayout.Tab tab = groupPostTabLayout.newTab();
                tab.setTag(sectionsKey[i]);
                groupPostTabLayout.addTab(tab.setText(sections[i]));
            }
        } else if (tabType == 1) {
            for (int i = 0; i < sections.length; i++) {
                TabLayout.Tab tab = groupPostTabLayout.newTab();
                tab.setTag(sectionsKeyResources[i]);
                groupPostTabLayout.addTab(tab.setText(sections[i]));
            }
        } else {
            for (int i = 0; i < sections.length; i++) {
                TabLayout.Tab tab = groupPostTabLayout.newTab();
                tab.setTag(sectionsKeyVirtualClinic[i]);
                groupPostTabLayout.addTab(tab.setText(sections[i]));
            }
        }

        groupPostTabLayout.setTabGravity(TabLayout.GRAVITY_CENTER);

        AppUtils.changeTabsFont(groupPostTabLayout);

        groupPostTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (AppConstants.GROUP_SECTION_ABOUT.equalsIgnoreCase(tab.getTag().toString())) {
                    Utils.groupsEvent(GroupDetailsActivity.this, "Groups_Discussion", "About", "android",
                            SharedPrefUtils.getAppLocale(BaseApplication.getAppContext()),
                            SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId(),
                            String.valueOf(System.currentTimeMillis()), "About Page", "", "");
                    filterContentContainer.setVisibility(View.GONE);
                    removeRecyclerViewDecorator();
                    final LinearLayoutManager llm = new LinearLayoutManager(GroupDetailsActivity.this);
                    llm.setOrientation(RecyclerView.VERTICAL);
                    recyclerView.setLayoutManager(llm);
                    recyclerView.setAdapter(groupAboutRecyclerAdapter);
                } else if (AppConstants.GROUP_SECTION_DISCUSSION.equalsIgnoreCase(tab.getTag().toString())) {
                    Utils.groupsEvent(GroupDetailsActivity.this, "Groups_Discussion", "discussion", "android",
                            SharedPrefUtils.getAppLocale(BaseApplication.getAppContext()),
                            SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId(),
                            String.valueOf(System.currentTimeMillis()), "discussion page", "", "");
                    filterContentContainer.setVisibility(View.GONE);
                    removeRecyclerViewDecorator();
                    isRequestRunning = false;
                    isLastPageReached = false;
                    final LinearLayoutManager llm = new LinearLayoutManager(GroupDetailsActivity.this);
                    llm.setOrientation(RecyclerView.VERTICAL);
                    recyclerView.setLayoutManager(llm);
                    recyclerView.setAdapter(groupsGenericPostRecyclerAdapter);
                    postList.clear();
                    skip = 0;
                    limit = 10;
                    getGroupPosts();
                } else if (AppConstants.GROUP_SECTION_BLOGS.equalsIgnoreCase(tab.getTag().toString())) {
                    Utils.groupsEvent(GroupDetailsActivity.this, "Groups_Discussion", "Blogs", "android",
                            SharedPrefUtils.getAppLocale(BaseApplication.getAppContext()),
                            SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId(),
                            String.valueOf(System.currentTimeMillis()), "Blogs page", "", "");
                    filterContentContainer.setVisibility(View.GONE);
                    removeRecyclerViewDecorator();
                    isRequestRunning = false;
                    isLastPageReached = false;
                    nextPageNumber = 1;
                    final LinearLayoutManager llm = new LinearLayoutManager(GroupDetailsActivity.this);
                    llm.setOrientation(RecyclerView.VERTICAL);
                    recyclerView.setLayoutManager(llm);
                    recyclerView.setAdapter(groupBlogsRecyclerAdapter);
                    if (StringUtils.isNullOrEmpty(commaSepCategoryList)) {
                        getCategoriesTaggedWithGroups();
                    } else {
                        hitFilteredTopicsArticleListingApi(groupMappedCategories.get(categoryIndex).getCategoryId());
                    }
                } else if (AppConstants.GROUP_SECTION_POLLS.equalsIgnoreCase(tab.getTag().toString())) {
                    Utils.groupsEvent(GroupDetailsActivity.this, "Groups_Discussion", "photos", "android",
                            SharedPrefUtils.getAppLocale(BaseApplication.getAppContext()),
                            SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId(),
                            String.valueOf(System.currentTimeMillis()), "photos page", "", "");
                    filterContentContainer.setVisibility(View.GONE);
                    removeRecyclerViewDecorator();
                    final LinearLayoutManager llm = new LinearLayoutManager(GroupDetailsActivity.this);
                    llm.setOrientation(RecyclerView.VERTICAL);
                    recyclerView.setLayoutManager(llm);
                    recyclerView.setAdapter(groupsGenericPostRecyclerAdapter);
                    isRequestRunning = false;
                    isLastPageReached = false;
                    postList.clear();
                    skip = 0;
                    limit = 10;
                    postType = AppConstants.POST_TYPE_POLL_KEY;
                    getFilteredGroupPosts();
                } else if (AppConstants.GROUP_SECTION_ASK_AN_EXPERT.equalsIgnoreCase(tab.getTag().toString())) {
                    Utils.groupsEvent(GroupDetailsActivity.this, "Groups_Discussion", "polls", "android",
                            SharedPrefUtils.getAppLocale(BaseApplication.getAppContext()),
                            SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId(),
                            String.valueOf(System.currentTimeMillis()), "polls page", "", "");
                    filterContentContainer.setVisibility(View.GONE);
                    removeRecyclerViewDecorator();
                    isRequestRunning = false;
                    isLastPageReached = false;
                    final LinearLayoutManager llm = new LinearLayoutManager(GroupDetailsActivity.this);
                    llm.setOrientation(RecyclerView.VERTICAL);
                    recyclerView.setLayoutManager(llm);
                    recyclerView.setAdapter(groupsGenericPostRecyclerAdapter);
                    postList.clear();
                    skip = 0;
                    limit = 10;
                    postType = AppConstants.ASK_AN_EXPERT_KEY;
                    getFilteredGroupPosts();
                } else if (AppConstants.GROUP_SECTION_RESOURCES.equalsIgnoreCase(tab.getTag().toString())) {
                    Utils.groupsEvent(GroupDetailsActivity.this, "Groups_Discussion", "resources", "android",
                            SharedPrefUtils.getAppLocale(BaseApplication.getAppContext()),
                            SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId(),
                            String.valueOf(System.currentTimeMillis()), "resources page", "", "");
                    if (collectionItemsList != null) {
                        collectionItemsList.clear();
                    }
                    filterContentContainer.setVisibility(View.GONE);
                    removeRecyclerViewDecorator();
                    initializeCollectionsItemsList(selectedGroup.getCollectionId());
                } else if (AppConstants.GROUP_SECTION_VIRTUAL_CLINIC.equalsIgnoreCase(tab.getTag().toString())) {
                    Utils.groupsEvent(GroupDetailsActivity.this, "Groups_Discussion", "resources", "android",
                            SharedPrefUtils.getAppLocale(BaseApplication.getAppContext()),
                            SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId(),
                            String.valueOf(System.currentTimeMillis()), "resources page", "", "");
                    filterContentContainer.setVisibility(View.VISIBLE);
                    if (StringUtils.isNullOrEmpty(questionnaireResponse.get("expectedDate")) && StringUtils
                            .isNullOrEmpty(questionnaireResponse.get("dob"))) {
                        forYouFilterWidget.setVisibility(View.GONE);
                        monthFilterWidget.setSelected(true);
                        categoryFilterWidget.setSelected(false);
                        GridLayoutManager llm1 = new GridLayoutManager(GroupDetailsActivity.this, 3);
                        parentCollectionId = selectedGroup.getCollectionByMonth();
                        initializeVirtualClinicLayout(llm1);
                    } else {
                        forYouFilterWidget.setVisibility(View.VISIBLE);
                        forYouFilterWidget.setSelected(true);
                        monthFilterWidget.setSelected(false);
                        categoryFilterWidget.setSelected(false);
                        removeRecyclerViewDecorator();
                        initializeCollectionsItemsList(getCollectionIdForMonth());
                    }
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                if (AppConstants.GROUP_SECTION_ABOUT.equalsIgnoreCase(tab.getTag().toString())) {
                    recyclerView.setAdapter(groupAboutRecyclerAdapter);
                } else if (AppConstants.GROUP_SECTION_DISCUSSION.equalsIgnoreCase(tab.getTag().toString())) {
                    isRequestRunning = false;
                    isLastPageReached = false;
                    recyclerView.setAdapter(groupsGenericPostRecyclerAdapter);
                    postList.clear();
                    skip = 0;
                    limit = 10;
                    getGroupPosts();
                } else if (AppConstants.GROUP_SECTION_BLOGS.equalsIgnoreCase(tab.getTag().toString())) {
                    isRequestRunning = false;
                    isLastPageReached = false;
                    nextPageNumber = 1;
                    recyclerView.setAdapter(groupBlogsRecyclerAdapter);
                    if (StringUtils.isNullOrEmpty(commaSepCategoryList)) {
                        getCategoriesTaggedWithGroups();
                    } else {
                        hitFilteredTopicsArticleListingApi(groupMappedCategories.get(categoryIndex).getCategoryId());
                    }
                } else if (AppConstants.GROUP_SECTION_POLLS.equalsIgnoreCase(tab.getTag().toString())) {
                    isRequestRunning = false;
                    isLastPageReached = false;
                    recyclerView.setAdapter(groupsGenericPostRecyclerAdapter);
                    postList.clear();
                    skip = 0;
                    limit = 10;
                    postType = AppConstants.POST_TYPE_POLL_KEY;
                    getFilteredGroupPosts();
                } else if (AppConstants.GROUP_SECTION_ASK_AN_EXPERT.equalsIgnoreCase(tab.getTag().toString())) {
                    isRequestRunning = false;
                    isLastPageReached = false;
                    recyclerView.setAdapter(groupsGenericPostRecyclerAdapter);
                    postList.clear();
                    skip = 0;
                    limit = 10;
                    postType = AppConstants.POST_TYPE_POLL_KEY;
                    getFilteredGroupPosts();
                }
            }
        });
    }

    private void initializeCollectionsItemsList(String collectionId) {
        isRequestRunning = false;
        isLastPageReached = false;
        start = 0;
        final LinearLayoutManager llm = new LinearLayoutManager(GroupDetailsActivity.this);
        llm.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(collectionItemsListAdapter);
        getUserCollectionItems(collectionId, start);
    }

    private void initializeVirtualClinicLayout(GridLayoutManager llm1) {
        recyclerView.setLayoutManager(llm1);
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.groups_column_spacing);
        recyclerView.addItemDecoration(new SpacesItemDecoration(spacingInPixels));
        filterContentContainer.setVisibility(View.VISIBLE);
        isRequestRunning = false;
        isLastPageReached = false;
        start = 0;
        if (collectionList != null) {
            collectionList.clear();
        }
        recyclerView.setAdapter(groupCollectionRecyclerAdapter);
        getCollectionsList(parentCollectionId);
    }

    private void getCollectionsList(String collectionId) {
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        CollectionsAPI collectionsApi = retrofit.create(CollectionsAPI.class);
        Call<BaseResponseGeneric<UserCollectionsListModel>> call = collectionsApi
                .getUserCollectionItem(collectionId, start, 10);
        call.enqueue(collectionOfCollectionsResponseCallback);
    }

    private Callback<BaseResponseGeneric<UserCollectionsListModel>> collectionOfCollectionsResponseCallback =
            new Callback<BaseResponseGeneric<UserCollectionsListModel>>() {
                @Override
                public void onResponse(Call<BaseResponseGeneric<UserCollectionsListModel>> call,
                        Response<BaseResponseGeneric<UserCollectionsListModel>> response) {
                    isRequestRunning = false;
                    if (response.body() == null) {
                        NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                        FirebaseCrashlytics.getInstance().recordException(nee);
                        return;
                    }
                    try {
                        if (response.isSuccessful()) {
                            BaseResponseGeneric<UserCollectionsListModel> userCollectionsListModel = response.body();
                            processCollectionList(userCollectionsListModel);
                        }
                    } catch (Exception e) {
                        FirebaseCrashlytics.getInstance().recordException(e);
                        Log.d("MC4kException", Log.getStackTraceString(e));
                    }
                }

                @Override
                public void onFailure(Call<BaseResponseGeneric<UserCollectionsListModel>> call, Throwable t) {

                }
            };

    private void processCollectionList(BaseResponseGeneric<UserCollectionsListModel> userCollectionsListModel) {
        ArrayList<UserCollectionsModel> dataList = userCollectionsListModel.getData().getResult().getCollectionItems();
        if (dataList.size() == 0) {
            isLastPageReached = false;
            if (null != collectionList && !collectionList.isEmpty()) {
                isLastPageReached = true;
            } else {
                collectionList = dataList;
                groupCollectionRecyclerAdapter.setData(collectionList);
                groupCollectionRecyclerAdapter.notifyDataSetChanged();
            }
        } else {
            collectionList.addAll(dataList);
            groupCollectionRecyclerAdapter.setData(collectionList);
            start = start + 10;
            groupCollectionRecyclerAdapter.notifyDataSetChanged();
        }
    }

    private void getUserCollectionItems(String collectionId, int start) {
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        CollectionsAPI collectionsApi = retrofit.create(CollectionsAPI.class);
        Call<BaseResponseGeneric<UserCollectionsListModel>> call = collectionsApi
                .getUserCollectionItem(collectionId, start, 10);
        call.enqueue(userCollectionItemsResponseCallback);
    }

    private Callback<BaseResponseGeneric<UserCollectionsListModel>> userCollectionItemsResponseCallback =
            new Callback<BaseResponseGeneric<UserCollectionsListModel>>() {
                @Override
                public void onResponse(Call<BaseResponseGeneric<UserCollectionsListModel>> call,
                        Response<BaseResponseGeneric<UserCollectionsListModel>> response) {
                    if (response.body() == null) {
                        NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                        FirebaseCrashlytics.getInstance().recordException(nee);
                        return;
                    }
                    try {
                        if (response.isSuccessful()) {
                            BaseResponseGeneric<UserCollectionsListModel> userCollectionsListModel = response.body();
                            processCollectionItems(userCollectionsListModel);
                        }
                    } catch (Exception e) {
                        FirebaseCrashlytics.getInstance().recordException(e);
                        Log.d("MC4kException", Log.getStackTraceString(e));
                    }
                }

                @Override
                public void onFailure(Call<BaseResponseGeneric<UserCollectionsListModel>> call, Throwable t) {
                    FirebaseCrashlytics.getInstance().recordException(t);
                    Log.d("MC4kException", Log.getStackTraceString(t));
                }
            };

    private void processCollectionItems(BaseResponseGeneric<UserCollectionsListModel> userCollectionsListModel) {
        ArrayList<UserCollectionsModel> dataList = userCollectionsListModel.getData().getResult().getCollectionItems();
        if (dataList.size() == 0) {
            isLastPageReached = false;
            if (null != collectionItemsList && !collectionItemsList.isEmpty()) {
                isLastPageReached = true;
            } else {
                collectionItemsList = dataList;
                collectionItemsListAdapter.setListData(collectionItemsList);
                collectionItemsListAdapter.notifyDataSetChanged();
            }
        } else {
            collectionItemsList.addAll(dataList);
            collectionItemsListAdapter.setListData(collectionItemsList);
            start = start + 10;
            collectionItemsListAdapter.notifyDataSetChanged();
        }
    }


    private void getCategoriesTaggedWithGroups() {
        Retrofit retrofit = BaseApplication.getInstance().getGroupsRetrofit();
        GroupsAPI groupsApi = retrofit.create(GroupsAPI.class);
        Call<GroupsCategoryMappingResponse> call = groupsApi.getGroupCategories(groupId);
        call.enqueue(groupsCategoryResponseCallback);
    }

    private Callback<GroupsCategoryMappingResponse> groupsCategoryResponseCallback =
            new Callback<GroupsCategoryMappingResponse>() {
                @Override
                public void onResponse(Call<GroupsCategoryMappingResponse> call,
                        retrofit2.Response<GroupsCategoryMappingResponse> response) {
                    if (response.body() == null) {
                        NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                        FirebaseCrashlytics.getInstance().recordException(nee);
                        return;
                    }
                    try {
                        if (response.isSuccessful()) {
                            GroupsCategoryMappingResponse groupsCategoryMappingResponse = response.body();
                            groupMappedCategories =
                                    (ArrayList<GroupsCategoryMappingResult>) groupsCategoryMappingResponse
                                            .getData().getResult();
                            if (groupMappedCategories != null && !groupMappedCategories.isEmpty()) {
                                hitFilteredTopicsArticleListingApi(
                                        groupMappedCategories.get(categoryIndex).getCategoryId());
                            }
                        }
                    } catch (Exception e) {
                        FirebaseCrashlytics.getInstance().recordException(e);
                        Log.d("MC4kException", Log.getStackTraceString(e));
                    }
                }

                @Override
                public void onFailure(Call<GroupsCategoryMappingResponse> call, Throwable t) {
                    FirebaseCrashlytics.getInstance().recordException(t);
                    Log.d("MC4kException", Log.getStackTraceString(t));
                }
            };

    private void hitFilteredTopicsArticleListingApi(String categoryId) {
        if (!ConnectivityUtils.isNetworkEnabled(this)) {
            ToastUtils.showToast(this, getString(R.string.error_network));
            return;
        }

        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        TopicsCategoryAPI topicsApi = retrofit.create(TopicsCategoryAPI.class);

        int from = (nextPageNumber - 1) * limit + 1;
        Call<ArticleListingResponse> filterCall = topicsApi
                .getArticlesForCategory(categoryId, 1, from, from + limit - 1,
                        SharedPrefUtils.getLanguageFilters(BaseApplication.getAppContext()));
        filterCall.enqueue(articleListingResponseCallback);
    }

    private Callback<ArticleListingResponse> articleListingResponseCallback = new Callback<ArticleListingResponse>() {
        @Override
        public void onResponse(Call<ArticleListingResponse> call, retrofit2.Response<ArticleListingResponse> response) {
            isRequestRunning = false;
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
            isRequestRunning = false;
            FirebaseCrashlytics.getInstance().recordException(t);
            Log.d("MC4KException", Log.getStackTraceString(t));
        }
    };

    private void processArticleListingResponse(ArticleListingResponse responseData) {
        ArrayList<ArticleListingResult> dataList = responseData.getData().get(0).getResult();
        if (dataList.size() == 0) {
            if (null != articleDataModelsNew && !articleDataModelsNew.isEmpty()) {
                //No more next results for search from pagination
                if (categoryIndex < groupMappedCategories.size() - 1) {
                    nextPageNumber = 1;
                    categoryIndex++;
                } else {
                    isLastPageReached = true;
                }
            } else {
                if (categoryIndex < groupMappedCategories.size() - 1) {
                    nextPageNumber = 1;
                    categoryIndex++;
                    hitFilteredTopicsArticleListingApi(groupMappedCategories.get(categoryIndex).getCategoryId());
                } else {
                    articleDataModelsNew.addAll(dataList);
                    groupBlogsRecyclerAdapter.setData(articleDataModelsNew);
                    groupBlogsRecyclerAdapter.notifyDataSetChanged();
                }
            }
        } else {
            articleDataModelsNew.addAll(dataList);
            groupBlogsRecyclerAdapter.setData(articleDataModelsNew);
            groupBlogsRecyclerAdapter.notifyDataSetChanged();
            nextPageNumber = nextPageNumber + 1;
        }
    }


    @Override
    public void onRecyclerItemClick(View view, int position) {
        switch (view.getId()) {
            case R.id.forYouInfoLL:
                Bundle args = new Bundle();
                args.putInt("position", position);
                args.putInt("groupId", groupId);
                args.putString("articleUrl",
                        AppConstants.BLOG_SHARE_BASE_URL + articleDataModelsNew.get(position).getUrl());
                ShareBlogInDiscussionDialogFragment shareBlogInDiscussionDialogFragment =
                        new ShareBlogInDiscussionDialogFragment();
                shareBlogInDiscussionDialogFragment.setArguments(args);
                shareBlogInDiscussionDialogFragment.setCancelable(true);
                shareBlogInDiscussionDialogFragment.setListener(this);
                FragmentManager fm = getSupportFragmentManager();
                shareBlogInDiscussionDialogFragment.show(fm, "For You");
                break;
            default:
                Intent intent = new Intent(this, ArticleDetailsContainerActivity.class);
                intent.putExtra(Constants.ARTICLE_ID, articleDataModelsNew.get(position).getId());
                intent.putExtra(Constants.AUTHOR_ID, articleDataModelsNew.get(position).getUserId());
                intent.putExtra(Constants.BLOG_SLUG, articleDataModelsNew.get(position).getBlogPageSlug());
                intent.putExtra(Constants.TITLE_SLUG, articleDataModelsNew.get(position).getTitleSlug());
                intent.putExtra(Constants.ARTICLE_OPENED_FROM, "GroupDetailsArticleListing");
                intent.putExtra(Constants.FROM_SCREEN, "GroupDetailActivity");
                intent.putExtra(Constants.AUTHOR,
                        articleDataModelsNew.get(position).getUserId() + "~" + articleDataModelsNew.get(position)
                                .getUserName());
                startActivity(intent);
                break;
        }

    }

    @Override
    public void onGroupPostRecyclerItemClick(View view, int position) {
        switch (view.getId()) {
            case R.id.userImageView:
            case R.id.usernameTextView: {
                if (postList.get(position).getIsAnnon() == 0) {
                    Intent profileIntent = new Intent(this, UserProfileActivity.class);
                    profileIntent.putExtra(Constants.USER_ID, postList.get(position).getUserId());
                    startActivity(profileIntent);
                }
                break;
            }
            case R.id.postSettingImageView:
                selectedPost = postList.get(position);
                type = selectedPost.getType();
                getCurrentUserPostSettingsStatus(selectedPost);
                if (AppConstants.GROUP_MEMBER_TYPE_ADMIN.equals(memberType)
                        || AppConstants.GROUP_MEMBER_TYPE_MODERATOR.equals(memberType)) {
                    getAdminPostSettingsStatus(selectedPost);
                }
                if (selectedPost.getDisableComments() == 1) {
                    commentToggleTextView.setText(getString(R.string.groups_enable_comment));
                } else {
                    commentToggleTextView.setText(getString(R.string.groups_disable_comment));
                }
                break;
            case R.id.postDataTextView:
            case R.id.postDateTextView: {
                break;
            }
            case R.id.upvoteCommentContainer:
            case R.id.upvoteContainer:
                Utils.groupsEvent(GroupDetailsActivity.this, "Groups_Discussion", "Helpful", "android",
                        SharedPrefUtils.getAppLocale(BaseApplication.getAppContext()),
                        SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId(),
                        String.valueOf(System.currentTimeMillis()), "click", "", String.valueOf(groupId));
                if (postList.get(position).getMarkedHelpful() == 0) {
                    markAsHelpfulOrUnhelpful(AppConstants.GROUP_ACTION_TYPE_HELPFUL_KEY, position);
                }
                if (postList.get(position).getMarkedHelpful() == 1) {
                    markAsHelpfulOrUnhelpful(AppConstants.GROUP_ACTION_TYPE_UNHELPFUL_KEY, position);
                }
                break;
            case R.id.downvoteContainer:
                Utils.groupsEvent(GroupDetailsActivity.this, "Groups_Discussion", "not helpful", "android",
                        SharedPrefUtils.getAppLocale(BaseApplication.getAppContext()),
                        SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId(),
                        String.valueOf(System.currentTimeMillis()), "click", "", String.valueOf(groupId));
                markAsHelpfulOrUnhelpful(AppConstants.GROUP_ACTION_TYPE_UNHELPFUL_KEY, position);
                break;
            case R.id.shareTextView:
                Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                String shareUrl = AppConstants.WEB_URL + postList.get(position).getUrl();
                shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareUrl);
                startActivity(Intent.createChooser(shareIntent, "Momspresso"));
                break;
            case R.id.whatsappShare:
                String shareUrlWhatsapp = AppConstants.WEB_URL + postList.get(position).getUrl();
                AppUtils.shareCampaignWithWhatsApp(GroupDetailsActivity.this, shareUrlWhatsapp, "", "", "", "", "");
                break;
            default:
                break;
        }
    }

    private void markAsHelpfulOrUnhelpful(String markType, int position) {
        Retrofit retrofit = BaseApplication.getInstance().getGroupsRetrofit();
        GroupsAPI groupsApi = retrofit.create(GroupsAPI.class);
        GroupActionsRequest groupActionsRequest = new GroupActionsRequest();
        groupActionsRequest.setGroupId(postList.get(position).getGroupId());
        groupActionsRequest.setPostId(postList.get(position).getId());
        groupActionsRequest.setUserId(SharedPrefUtils.getUserDetailModel(this).getDynamoId());
        groupActionsRequest.setType(markType);//AppConstants.GROUP_ACTION_TYPE_HELPFUL_KEY
        Call<GroupsActionResponse> call = groupsApi.addAction(groupActionsRequest);
        call.enqueue(groupActionResponseCallback);
    }

    private Callback<GroupsActionResponse> groupActionResponseCallback = new Callback<GroupsActionResponse>() {
        @Override
        public void onResponse(Call<GroupsActionResponse> call, retrofit2.Response<GroupsActionResponse> response) {
            if (response.body() == null) {
                if (response.code() == 400) {
                    try {
                        int patchActionId = 0;
                        String patchActionType = null;

                        String errorBody = new String(response.errorBody().bytes());
                        JSONObject jsonObject = new JSONObject(errorBody);
                        JSONArray dataArray = jsonObject.optJSONArray("data");
                        if (dataArray.getJSONObject(0).get("type").equals(dataArray.getJSONObject(1).get("type"))) {
                            //Same Action Event
                            if ("0".equals(dataArray.getJSONObject(0).get("type"))) {
                                showToast("already marked unhelpful");
                            } else {
                                showToast("already marked helpful");
                            }
                        } else {
                            if (dataArray.getJSONObject(0).has("id") && !dataArray.getJSONObject(0).isNull("id")) {
                                patchActionId = dataArray.getJSONObject(0).getInt("id");
                                patchActionType = dataArray.getJSONObject(1).getString("type");
                            } else {
                                patchActionType = dataArray.getJSONObject(0).getString("type");
                                patchActionId = dataArray.getJSONObject(1).getInt("id");
                            }
                            sendUpvoteDownvotePatchRequest(patchActionId, patchActionType);
                        }
                    } catch (IOException | JSONException e) {
                        FirebaseCrashlytics.getInstance().recordException(e);
                        Log.d("MC4kException", Log.getStackTraceString(e));
                    }
                }
                return;
            }
            try {
                if (response.isSuccessful()) {
                    GroupsActionResponse groupsActionResponse = response.body();
                    if (groupsActionResponse.getData().getResult().size() == 1) {
                        for (int i = 0; i < postList.size(); i++) {
                            if (postList.get(i).getId() == groupsActionResponse.getData().getResult().get(0)
                                    .getPostId()) {
                                if ("1".equals(groupsActionResponse.getData().getResult().get(0).getType())) {
                                    postList.get(i).setHelpfullCount(postList.get(i).getHelpfullCount() + 1);
                                    postList.get(i).setMarkedHelpful(1);
                                } else {
                                    postList.get(i).setNotHelpfullCount(postList.get(i).getNotHelpfullCount() + 1);
                                    postList.get(i).setMarkedHelpful(0);
                                }
                            }
                        }
                    }
                    groupsGenericPostRecyclerAdapter.notifyDataSetChanged();
                }
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<GroupsActionResponse> call, Throwable t) {
            FirebaseCrashlytics.getInstance().recordException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    private void sendUpvoteDownvotePatchRequest(int patchActionId, String patchActionType) {
        Retrofit retrofit = BaseApplication.getInstance().getGroupsRetrofit();
        GroupsAPI groupsApi = retrofit.create(GroupsAPI.class);
        GroupActionsPatchRequest groupActionsRequest = new GroupActionsPatchRequest();
        groupActionsRequest.setType(patchActionType);
        Call<GroupsActionResponse> call = groupsApi.patchAction(patchActionId, groupActionsRequest);
        call.enqueue(patchActionResponseCallback);
    }

    private Callback<GroupsActionResponse> patchActionResponseCallback = new Callback<GroupsActionResponse>() {
        @Override
        public void onResponse(Call<GroupsActionResponse> call, retrofit2.Response<GroupsActionResponse> response) {
            if (response.body() == null) {
                NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                FirebaseCrashlytics.getInstance().recordException(nee);
                return;
            }
            try {
                if (response.isSuccessful()) {
                    GroupsActionResponse groupsActionResponse = response.body();
                    if (groupsActionResponse.getData().getResult().size() == 1) {
                        for (int i = 0; i < postList.size(); i++) {
                            if (postList.get(i).getId() == groupsActionResponse.getData().getResult().get(0)
                                    .getPostId()) {
                                if ("1".equals(groupsActionResponse.getData().getResult().get(0).getType())) {
                                    postList.get(i).setHelpfullCount(postList.get(i).getHelpfullCount() + 1);
                                    postList.get(i).setNotHelpfullCount(postList.get(i).getNotHelpfullCount() - 1);
                                    postList.get(i).setMarkedHelpful(1);
                                } else {
                                    postList.get(i).setNotHelpfullCount(postList.get(i).getNotHelpfullCount() + 1);
                                    postList.get(i).setHelpfullCount(postList.get(i).getHelpfullCount() - 1);
                                    postList.get(i).setMarkedHelpful(0);
                                }
                            }
                        }
                    }
                    groupsGenericPostRecyclerAdapter.notifyDataSetChanged();
                }
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<GroupsActionResponse> call, Throwable t) {

        }
    };

    private void getCurrentUserPostSettingsStatus(GroupPostResult selectedPost) {
        progressBar.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        Retrofit retrofit = BaseApplication.getInstance().getGroupsRetrofit();
        GroupsAPI groupsApi = retrofit.create(GroupsAPI.class);

        Call<UserPostSettingResponse> call = groupsApi.getPostSettingForUser(selectedPost.getId());
        call.enqueue(userPostSettingResponseCallback);
    }

    private Callback<UserPostSettingResponse> userPostSettingResponseCallback =
            new Callback<UserPostSettingResponse>() {
                @Override
                public void onResponse(Call<UserPostSettingResponse> call,
                        retrofit2.Response<UserPostSettingResponse> response) {
                    progressBar.setVisibility(View.GONE);
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    if (response.body() == null) {
                        NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                        FirebaseCrashlytics.getInstance().recordException(nee);
                        return;
                    }
                    try {
                        if (response.isSuccessful()) {
                            UserPostSettingResponse userPostSettingResponse = response.body();
                            setPostCurrentPreferences(userPostSettingResponse);
                            postSettingsContainer.startAnimation(slideAnim);
                            overlayView.startAnimation(fadeAnim);
                            postSettingsContainerMain.setVisibility(View.VISIBLE);
                            if (type.equals("3")) {
                                editPostTextView.setVisibility(View.GONE);
                            }
                            postSettingsContainer.setVisibility(View.VISIBLE);
                            overlayView.setVisibility(View.VISIBLE);
                        }
                    } catch (Exception e) {
                        FirebaseCrashlytics.getInstance().recordException(e);
                        Log.d("MC4kException", Log.getStackTraceString(e));
                    }
                }

                @Override
                public void onFailure(Call<UserPostSettingResponse> call, Throwable t) {
                    progressBar.setVisibility(View.GONE);
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    FirebaseCrashlytics.getInstance().recordException(t);
                    Log.d("MC4kException", Log.getStackTraceString(t));
                }
            };

    private void getAdminPostSettingsStatus(GroupPostResult selectedPost) {
        pinPostTextView.setVisibility(View.GONE);
        blockUserTextView.setVisibility(View.GONE);
        deletePostTextView.setVisibility(View.GONE);
        if (selectedPost.getType().equals("3")) {
            editPostTextView.setVisibility(View.GONE);
        }
        Retrofit retrofit = BaseApplication.getInstance().getGroupsRetrofit();
        GroupsAPI groupsApi = retrofit.create(GroupsAPI.class);
        Call<GroupPostResponse> call = groupsApi.getSinglePost(selectedPost.getId());
        call.enqueue(postDetailsResponseCallback);
    }

    private Callback<GroupPostResponse> postDetailsResponseCallback = new Callback<GroupPostResponse>() {
        @Override
        public void onResponse(Call<GroupPostResponse> call, retrofit2.Response<GroupPostResponse> response) {
            if (response.body() == null) {
                NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                FirebaseCrashlytics.getInstance().recordException(nee);
                return;
            }
            try {
                if (response.isSuccessful()) {
                    GroupPostResponse groupPostResponse = response.body();
                    setAdminPostPreferences(groupPostResponse);
                }
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<GroupPostResponse> call, Throwable t) {
            FirebaseCrashlytics.getInstance().recordException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    private void setPostCurrentPreferences(UserPostSettingResponse userPostSettingResponse) {
        if (selectedPost.getUserId().equals(SharedPrefUtils.getUserDetailModel(GroupDetailsActivity.this)
                .getDynamoId()) || AppConstants.GROUP_MEMBER_TYPE_ADMIN.equals(memberType)
                || AppConstants.GROUP_MEMBER_TYPE_MODERATOR.equals(memberType)) {
            commentToggleTextView.setVisibility(View.VISIBLE);
        } else {
            commentToggleTextView.setVisibility(View.GONE);
        }

        if (selectedPost.getUserId()
                .equals(SharedPrefUtils.getUserDetailModel(GroupDetailsActivity.this).getDynamoId())) {
            editPostTextView.setVisibility(View.VISIBLE);
            deletePostTextView.setVisibility(View.VISIBLE);
        } else {
            editPostTextView.setVisibility(View.GONE);
            deletePostTextView.setVisibility(View.GONE);
        }

        //No existing settings for this post for this user
        if (userPostSettingResponse.getData().get(0).getResult() == null
                || userPostSettingResponse.getData().get(0).getResult().size() == 0) {
            savePostTextView.setText(getString(R.string.groups_save_post));
            notificationToggleTextView.setText(getString(R.string.groups_enable_notification));
            currentPostPrefsForUser = null;
            return;
        }
        currentPostPrefsForUser = userPostSettingResponse.getData().get(0).getResult().get(0);
        if (currentPostPrefsForUser.getIsBookmarked() == 1) {
            savePostTextView.setText(getString(R.string.groups_remove_post));
        } else {
            savePostTextView.setText(getString(R.string.groups_save_post));
        }

        if (currentPostPrefsForUser.getNotificationOff() == 1) {
            notificationToggleTextView.setText(getString(R.string.groups_enable_notification));
        } else {
            notificationToggleTextView.setText("DISABLE NOTIFICATION");
        }
    }

    private void setAdminPostPreferences(GroupPostResponse groupPostResponse) {
        pinPostTextView.setVisibility(View.VISIBLE);
        blockUserTextView.setVisibility(View.VISIBLE);
        deletePostTextView.setVisibility(View.VISIBLE);
        if (groupPostResponse.getData().get(0).getResult().get(0).getIsPinned() == 1) {
            pinPostTextView.setText(getString(R.string.groups_unpin_post));
        } else {
            pinPostTextView.setText(getString(R.string.groups_pin_post));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1111) {
                if (data != null && data.getParcelableExtra("postDatas") != null) {
                    GroupPostResult currentPost = data.getParcelableExtra("postDatas");
                    for (int i = 0; i < postList.size(); i++) {
                        if (postList.get(i).getId() == currentPost.getId()) {
                            postList.get(i).setHelpfullCount(currentPost.getHelpfullCount());
                            postList.get(i).setNotHelpfullCount(currentPost.getNotHelpfullCount());
                            groupsGenericPostRecyclerAdapter.notifyDataSetChanged();
                            break;
                        }
                    }
                }
                if (data != null && data.getParcelableArrayListExtra("completeResponseList") != null
                        && data.getIntExtra("postId", -1) != -1) {
                    ArrayList<GroupPostCommentResult> completeCommentResponseList = data
                            .getParcelableArrayListExtra("completeResponseList");
                    int postId = data.getIntExtra("postId", -1);

                    for (int i = 0; i < postList.size(); i++) {

                        if (postList.get(i).getId() == postId) {
                            postList.get(i).setResponseCount(completeCommentResponseList.size() - 1);
                            groupsGenericPostRecyclerAdapter.notifyDataSetChanged();
                            break;
                        }
                    }
                } else {
                    MixpanelAPI mixpanel = MixpanelAPI
                            .getInstance(BaseApplication.getAppContext(), AppConstants.MIX_PANEL_TOKEN);
                    try {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("userId",
                                SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId());
                        jsonObject.put("groupId", "" + groupId);
                        mixpanel.track("GroupPostCreation", jsonObject);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (addPostContainer.getVisibility() == View.VISIBLE) {
                        addPostContainer.setVisibility(View.GONE);
                    }
                    isLastPageReached = false;
                    skip = 0;
                    limit = 10;
                    postList.clear();
                    groupsGenericPostRecyclerAdapter.notifyDataSetChanged();
                    TabLayout.Tab tab = groupPostTabLayout.getTabAt(1);
                    tab.select();
                }
            } else if (requestCode == EDIT_POST_REQUEST_CODE) {
                if (postSettingsContainerMain.getVisibility() == View.VISIBLE) {
                    postSettingsContainerMain.setVisibility(View.GONE);
                }
                editedPost = data.getParcelableExtra("editedPost");
                selectedPost.setMediaUrls(editedPost.getMediaUrls());
                selectedPost.setContent(editedPost.getContent());
                selectedPost.setType(editedPost.getType());
                groupsGenericPostRecyclerAdapter.notifyDataSetChanged();
            } else if (requestCode == 2222) {
                if (data != null && data.getParcelableArrayListExtra("completeResponseList") != null
                        && data.getIntExtra("postId", -1) != -1 && data.getIntExtra("replyCount", -1) != -1) {
                    ArrayList<GroupPostCommentResult> completeCommentResponseList = data
                            .getParcelableArrayListExtra("completeResponseList");
                    int postId = data.getIntExtra("postId", -1);
                    int responseCount = data.getIntExtra("responseCount", -1);
                    if (responseCount != -1) {
                        for (int i = 0; i < postList.size(); i++) {
                            if (postList.get(i).getId() == postId) {
                                postList.get(i).setResponseCount(responseCount);
                                groupsGenericPostRecyclerAdapter.notifyDataSetChanged();
                                break;
                            }
                        }
                    }
                }
            }
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
    public void onForYouArticleRemoved(int position) {

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (groupsGenericPostRecyclerAdapter != null) {
            groupsGenericPostRecyclerAdapter.releasePlayer();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (groupsGenericPostRecyclerAdapter != null) {
            groupsGenericPostRecyclerAdapter.releasePlayer();
        }
    }

    public void processImage(Uri imageUri) {
        android.app.FragmentManager fm = getFragmentManager();
        processBitmapTaskFragment = null;
        processBitmapTaskFragment = (ProcessBitmapTaskFragment) fm.findFragmentByTag(TAG_TASK_FRAGMENT);
        if (processBitmapTaskFragment == null) {
            processBitmapTaskFragment = new ProcessBitmapTaskFragment();
            Bundle bundle = new Bundle();
            bundle.putParcelable("uri", imageUri);
            processBitmapTaskFragment.setArguments(bundle);
            fm.beginTransaction().add(processBitmapTaskFragment, TAG_TASK_FRAGMENT).commit();
        } else {
            showToast("You can add only 1 image in comments");
        }
    }

    @Override
    public void onPreExecute() {
        showProgressDialog(getString(R.string.please_wait));
    }

    @Override
    public void onCancelled() {
        removeProgressDialog();
    }

    @Override
    public void onPostExecute(Bitmap image) {
        Fragment prev = getSupportFragmentManager().findFragmentByTag("Add Comment");
        if (prev != null) {
            String path = MediaStore.Images.Media
                    .insertImage(getContentResolver(), image, "Title" + System.currentTimeMillis(), null);
            Uri imageUriTemp = Uri.parse(path);
            File file2 = FileUtils.getFile(this, imageUriTemp);
            removeProgressDialog();
            ((AddGpPostCommentReplyDialogFragment) prev).sendUploadProfileImageRequest(file2);
        }

    }

    private void bottomSheetStateChange() {
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int i) {
                if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                    hideBottomDrawer.setVisibility(View.GONE);
                }
            }

            @Override
            public void onSlide(@NonNull View view, float v) {
            }
        });
    }

    @Override
    public void onRecyclerViewclick(int position) {
        if (AppConstants.CONTENT_TYPE_ARTICLE.equals(collectionItemsList.get(position).getItemType())) {
            Intent intent = new Intent(this, ArticleDetailsContainerActivity.class);
            intent.putExtra(Constants.ARTICLE_ID, collectionItemsList.get(position).getItem());
            startActivity(intent);
        } else if (AppConstants.CONTENT_TYPE_SHORT_STORY.equals(collectionItemsList.get(position).getItemType())) {
            Intent intent = new Intent(this, ShortStoryContainerActivity.class);
            intent.putExtra(Constants.ARTICLE_ID, collectionItemsList.get(position).getItem());
        } else if (AppConstants.CONTENT_TYPE_VIDEO.equals(collectionItemsList.get(position).getItemType())) {
            Intent intent = new Intent(this, ParallelFeedActivity.class);
            intent.putExtra(Constants.STREAM_URL, collectionItemsList.get(position).getItem_info().getStreamUrl());
            intent.putExtra(Constants.VIDEO_ID, collectionItemsList.get(position).getItem());
            intent.putExtra(AppConstants.COLLECTION_ID, AppConstants.MOM_VLOG_TUTORIAL_COLLECTION);
            intent.putExtra(Constants.AUTHOR_ID, collectionItemsList.get(position).getItem_info().getAuthor().getId());
            startActivity(intent);
        }
    }

    private void removeRecyclerViewDecorator() {
        while (recyclerView.getItemDecorationCount() > 0) {
            recyclerView.removeItemDecorationAt(0);
        }
    }

    @Override
    public void onCollectionClickListener(View view, int position) {
        Intent intent = new Intent(this, UserCollectionItemListActivity.class);
        intent.putExtra("id", collectionList.get(position).getItem());
        startActivity(intent);
    }
}
