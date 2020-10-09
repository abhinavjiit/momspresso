package com.mycity4kids.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import com.afollestad.easyvideoplayer.EasyVideoCallback;
import com.afollestad.easyvideoplayer.EasyVideoPlayer;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.base.BaseActivity;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.Topics;
import com.mycity4kids.models.response.UserDetailResponse;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.LoginRegistrationAPI;
import com.mycity4kids.retrofitAPIsInterfaces.TopicsCategoryAPI;
import com.mycity4kids.utils.ConnectivityUtils;
import com.mycity4kids.utils.MixPanelUtils;
import com.mycity4kids.utils.StringUtils;
import com.mycity4kids.widget.ShareButtonWidget;
import java.io.File;
import org.apmem.tools.layouts.FlowLayout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by hemant on 10/1/17.
 */
public class AddVideoDetailsActivity extends BaseActivity implements View.OnClickListener, EasyVideoCallback {

    private EditText videoTitleEditText;
    private Toolbar toolbar;
    private TextView saveUploadTextView;

    private Uri originalUri;

    private String originalPath;
    private Uri contentUri;
    private EasyVideoPlayer player;
    private String subcategoryId;
    private String mappedCategoryId;
    private String duration;
    private String thumbnailTime;
    private SharedPreferences pref;
    private String comingFrom;
    private String challengeId;
    private RelativeLayout root;
    private FirebaseAuth auth;
    private WorkManager workManager;
    private Boolean signIn = false;
    private OneTimeWorkRequest request;
    private RelativeLayout popup;
    private TextView okay;
    private FlowLayout subCategoriesContainer;
    private Topics selectedCategory;
    MixpanelAPI mixpanel;
    private RelativeLayout toolTipContainer;
    private View tooltip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_video_details_activity);
        mixpanel = MixpanelAPI.getInstance(BaseApplication.getAppContext(), AppConstants.MIX_PANEL_TOKEN);
        root = findViewById(R.id.root);
        ((BaseApplication) getApplication()).setView(root);
        ((BaseApplication) getApplication()).setActivity(this);

        Utils.pushOpenScreenEvent(this, "CreateVideoScreen",
                SharedPrefUtils.getUserDetailModel(this).getDynamoId() + "");
        popup = (RelativeLayout) findViewById(R.id.popup);
        okay = (TextView) findViewById(R.id.okay);
        videoTitleEditText = (EditText) findViewById(R.id.videoTitleEditText);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        player = (EasyVideoPlayer) findViewById(R.id.player);
        saveUploadTextView = (TextView) findViewById(R.id.saveUploadTextView);
        subCategoriesContainer = (FlowLayout) findViewById(R.id.subCategoriesContainer);
        toolTipContainer = findViewById(R.id.toolTipContainer);
        tooltip = findViewById(R.id.tooltip);
        TextView next = tooltip.findViewById(R.id.okgot);
        auth = FirebaseAuth.getInstance();
        workManager = WorkManager.getInstance(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Upload Video");
        mappedCategoryId = getIntent().getStringExtra("categoryId");
        duration = getIntent().getStringExtra("duration");
        thumbnailTime = getIntent().getStringExtra("thumbnailTime");
        comingFrom = getIntent().getStringExtra("comingFrom");
        selectedCategory = getIntent().getParcelableExtra("selectedCategory");
        saveUploadTextView.setOnClickListener(this);
        next.setOnClickListener(this);
        if ("Challenge".equals(comingFrom)) {
            challengeId = getIntent().getStringExtra("ChallengeId");
            saveUploadTextView.setEnabled(false);
            getSubcategorySiblings();
        } else {
            populateSubcategories();
            saveUploadTextView.setEnabled(true);
        }
        if (getIntent().hasExtra("uriPath")) {
            originalPath = getIntent().getStringExtra("uriPath");
        } else if (getIntent().hasExtra("originalPath")) {
            originalPath = getIntent().getStringExtra("originalPath");
        }
        player.setCallback(this);
        player.setAutoPlay(true);
        player.setSource(Uri.fromFile(new File(originalPath)));
        originalUri = Uri.parse(originalPath);
        player.start();
        okay.setOnClickListener(this);
        toolTipContainer.setOnClickListener(this);
        tooltip.setOnClickListener(this);
        showToolTip();
    }

    private void showToolTip() {
        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (toolTipContainer.getVisibility() == View.VISIBLE) {
                    toolTipContainer.setVisibility(View.GONE);
                }
            }
        }, 3000);
    }

    private void getSubcategorySiblings() {
        showProgressDialog(getResources().getString(R.string.please_wait));
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        TopicsCategoryAPI topicsCategoryApi = retrofit.create(TopicsCategoryAPI.class);
        Call<Topics> call = topicsCategoryApi.getCategorySiblings(mappedCategoryId);
        call.enqueue(categorySiblingsResponseCallback);
    }

    private Callback<Topics> categorySiblingsResponseCallback = new Callback<Topics>() {
        @Override
        public void onResponse(Call<Topics> call, Response<Topics> response) {
            removeProgressDialog();
            if (response.body() == null) {
                showToast(getString(R.string.went_wrong));
                return;
            }
            try {
                if (response.isSuccessful()) {
                    Topics responseData = response.body();
                    selectedCategory = responseData;
                    populateSubcategories();
                    saveUploadTextView.setEnabled(true);
                }
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<Topics> call, Throwable t) {
            removeProgressDialog();
            FirebaseCrashlytics.getInstance().recordException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };


    private void populateSubcategories() {
        if (selectedCategory == null) {
            return;
        }
        for (int i = 0; i < selectedCategory.getChild().size(); i++) {
            if ("1".equals(selectedCategory.getChild().get(i).getPublicVisibility())) {
                ShareButtonWidget shareButtonWidget = new ShareButtonWidget(this);
                TextView shareTextView = shareButtonWidget.findViewById(R.id.shareTextView);
                ViewGroup.LayoutParams layoutParams = shareTextView.getLayoutParams();
                layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                shareTextView.setLayoutParams(layoutParams);
                shareButtonWidget.setTag(selectedCategory.getChild().get(i).getId());
                shareButtonWidget.setText(selectedCategory.getChild().get(i).getDisplay_name());
                shareButtonWidget.setButtonStartImage(null);
                shareButtonWidget.setTextSizeInSP(14);
                shareButtonWidget.setTextGravity(Gravity.CENTER);
                shareButtonWidget.setTextColor(ContextCompat.getColor(this, R.color.app_grey));
                shareButtonWidget.setButtonRadiusInDP(20);
                shareButtonWidget.setBorderColor(ContextCompat.getColor(this, R.color.app_grey));
                shareButtonWidget.setBorderThicknessInDP(1);
                shareButtonWidget.setElevation(0.0f);
                shareButtonWidget.setButtonBackgroundColor(
                        ContextCompat.getColor(
                                this,
                                R.color.white_color
                        )
                );
                FlowLayout.LayoutParams params = new FlowLayout.LayoutParams(
                        FlowLayout.LayoutParams.WRAP_CONTENT,
                        FlowLayout.LayoutParams.WRAP_CONTENT
                );
                params.setMargins(10, 10, 10, 10);
                shareButtonWidget.setLayoutParams(params);
                shareButtonWidget.setOnClickListener(view -> {
                    deselectAllSubcategories();
                    subcategoryId = (String) view.getTag();
                    view.setSelected(true);
                    ((ShareButtonWidget) view).setTextColor(
                            ContextCompat.getColor(
                                    AddVideoDetailsActivity.this,
                                    R.color.app_red
                            )
                    );
                    ((ShareButtonWidget) view).setBorderColor(
                            ContextCompat.getColor(
                                    AddVideoDetailsActivity.this,
                                    R.color.app_red
                            )
                    );
                });
                subCategoriesContainer.addView(shareButtonWidget);
            }
        }
    }

    private void deselectAllSubcategories() {
        for (int i = 0; i < subCategoriesContainer.getChildCount(); i++) {
            subCategoriesContainer.getChildAt(i).setSelected(false);
            ShareButtonWidget subCategoryButton = (ShareButtonWidget) subCategoriesContainer.getChildAt(i);
            subCategoryButton.setTextColor(
                    ContextCompat.getColor(
                            this,
                            R.color.app_grey
                    )
            );
            subCategoryButton.setBorderColor(
                    ContextCompat.getColor(
                            this,
                            R.color.app_grey
                    )
            );
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Make sure the player stops playing if the user presses the home button.
        player.pause();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tooltip:
            case R.id.toolTipContainer:
            case R.id.okgot: {
                if (toolTipContainer.getVisibility() == View.VISIBLE) {
                    toolTipContainer.setVisibility(View.GONE);
                }
                break;
            }

            case R.id.saveUploadTextView:
                if (StringUtils.isNullOrEmpty(videoTitleEditText.getText().toString())) {
                    videoTitleEditText.setFocusableInTouchMode(true);
                    videoTitleEditText.setError(getString(R.string.add_video_details_error_empty_title));
                    videoTitleEditText.requestFocus();
                } else if (videoTitleEditText.getText().toString().length() > 150) {
                    videoTitleEditText.setFocusableInTouchMode(true);
                    videoTitleEditText.setError(getString(R.string.add_video_details_title_length_error));
                    videoTitleEditText.requestFocus();
                } else if (StringUtils.isNullOrEmpty(subcategoryId)) {
                    showToast("Please select a category for your video");
                } else {
                    uploadVideo();
                }
                break;
            case R.id.okay:
                popup.setVisibility(View.GONE);
                Intent intent = new Intent(AddVideoDetailsActivity.this, DashboardActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
                break;
            default:
                break;

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void uploadVideo() {
        showProgressDialog("Please wait ...");
        contentUri = FileProvider.getUriForFile(this, "com.momspresso.fileprovider", new File(originalPath));
        Log.e("dwadad", "dwadad" + contentUri);
        if (contentUri != null) {
            removeProgressDialog();
            resumeUpload();
        } else {
            removeProgressDialog();
            MixPanelUtils.pushVideoUploadFailureEvent(mixpanel, "NULL URI FAILURE",
                    SharedPrefUtils.getUserDetailModel(this).getDynamoId());
        }
    }

    public void resumeUpload() {
        getBlogPage();
    }

    private void launchUploadInBackground() {
        removeProgressDialog();
        MixPanelUtils.pushVideoUploadCTAClick(mixpanel, videoTitleEditText.getText().toString() + "~" + signIn);
        Intent intt = new Intent(this, VideoUploadProgressActivity.class);
        intt.putExtra("uri", contentUri);
        intt.putExtra("title", videoTitleEditText.getText().toString());
        intt.putExtra("categoryId", subcategoryId);
        intt.putExtra("duration", duration);
        intt.putExtra("thumbnailTime", thumbnailTime);
        intt.putExtra("extension", originalUri.getPath().substring(originalUri.getPath().lastIndexOf(".")));
        if (comingFrom.equals("Challenge")) {
            intt.putExtra("ChallengeId", challengeId);
            intt.putExtra("comingFrom", "Challenge");
        } else {
            intt.putExtra("comingFrom", "notFromChallenge");
        }
        startActivity(intt);
    }

    private void getBlogPage() {
        showProgressDialog(getResources().getString(R.string.please_wait));
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        LoginRegistrationAPI loginRegistrationApi = retrofit.create(LoginRegistrationAPI.class);
        Call<UserDetailResponse> call = loginRegistrationApi
                .getUserDetails(SharedPrefUtils.getUserDetailModel(this).getDynamoId());
        call.enqueue(onLoginResponseReceivedListener);
        if (!ConnectivityUtils.isNetworkEnabled(this)) {
            removeProgressDialog();
            showToast(getString(R.string.error_network));
        }
    }

    Callback<UserDetailResponse> onLoginResponseReceivedListener = new Callback<UserDetailResponse>() {
        @Override
        public void onResponse(Call<UserDetailResponse> call, retrofit2.Response<UserDetailResponse> response) {
            removeProgressDialog();
            if (response.body() == null) {
                showToast(getString(R.string.went_wrong));
                return;
            }
            UserDetailResponse responseData = response.body();
            if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                if (responseData.getData().get(0).getResult().getBlogTitleSlug() == null || responseData.getData()
                        .get(0).getResult().getBlogTitleSlug().isEmpty()) {
                    Intent intent = new Intent(AddVideoDetailsActivity.this, BlogSetupActivity.class);
                    intent.putExtra("BlogTitle", responseData.getData().get(0).getResult().getBlogTitle());
                    intent.putExtra("email", responseData.getData().get(0).getResult().getEmail());
                    intent.putExtra("comingFrom", "Videos");
                    startActivity(intent);
                } else if (responseData.getData().get(0).getResult().getBlogTitleSlug() != null || !responseData
                        .getData().get(0).getResult().getBlogTitleSlug().isEmpty()) {
                    showProgressDialog(getResources().getString(R.string.please_wait));
                    launchUploadInBackground();
                }
            }
        }

        @Override
        public void onFailure(Call<UserDetailResponse> call, Throwable t) {
            removeProgressDialog();
            FirebaseCrashlytics.getInstance().recordException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));

        }
    };

    @Override
    public void onStarted(EasyVideoPlayer player) {

    }

    @Override
    public void onPaused(EasyVideoPlayer player) {

    }

    @Override
    public void onPreparing(EasyVideoPlayer player) {

    }

    @Override
    public void onPrepared(EasyVideoPlayer player) {

    }

    @Override
    public void onBuffering(int percent) {

    }

    @Override
    public void onError(EasyVideoPlayer player, Exception e) {

    }

    @Override
    public void onCompletion(EasyVideoPlayer player) {

    }

    @Override
    public void onRetry(EasyVideoPlayer player, Uri source) {

    }

    @Override
    public void onSubmit(EasyVideoPlayer player, Uri source) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = auth.getCurrentUser();
        auth.signInAnonymously()
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("VideoUpload", "signInAnonymously:success");
                        signIn = true;
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("VideoUpload", "signInAnonymously:failure", task.getException());
                        Toast.makeText(AddVideoDetailsActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
