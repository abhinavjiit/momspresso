package com.mycity4kids.ui.activity;

import android.accounts.NetworkErrorException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.base.BaseActivity;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.listener.OnButtonClicked;
import com.mycity4kids.models.request.UploadVideoRequest;
import com.mycity4kids.models.response.GroupsMembershipResponse;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.VlogsListingAndDetailsAPI;
import com.mycity4kids.ui.GroupMembershipStatus;
import com.mycity4kids.ui.GroupMembershipStatus.IMembershipStatus;
import com.mycity4kids.utils.AppUtils;
import com.mycity4kids.utils.MixPanelUtils;
import com.mycity4kids.vlogs.VideoCategoryAndChallengeSelectionActivity;
import com.mycity4kids.widget.MomspressoButtonWidget;
import java.text.DecimalFormat;
import java.util.ArrayList;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by hemant on 11/1/17.
 */
public class VideoUploadProgressActivity extends BaseActivity implements View.OnClickListener, IMembershipStatus {

    private RelativeLayout uploadFinishContainer;
    private RelativeLayout uploadingContainer;
    private TextView okayTextView;
    private TextView mtxtpercentage;
    private TextView mtxtvideosize;
    private TextView mtxtvideoname;
    private FirebaseAuth firebaseAuth;
    private boolean isUploading = false;
    private Uri contentUri;
    private String title;
    private String categoryId;
    private String challengeId;
    private String comingFrom;
    private String thumbnailTime;
    private MixpanelAPI mixpanel;
    private long suffixName;
    private ProgressBar progressBar;
    private String extension;
    private ImageView imgCancelUpload;
    private com.google.firebase.storage.UploadTask uploadTask;
    private RelativeLayout root;
    private String uploadStatus = AppConstants.VIDEO_UPLOAD_NOT_STARTED;
    private ImageView cancelImage;
    private MomspressoButtonWidget joinVloggersGroup;
    private TextView needOpinionTextView;
    private ConstraintLayout youAreDoneView;
    private int groupId;
    private ImageView back;
    private TextView modrationText;
    private RelativeLayout bottomLayout;
    private ArrayList<String> langs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_upload_progress_activity);
        root = findViewById(R.id.root);
        ((BaseApplication) getApplication()).setView(root);
        ((BaseApplication) getApplication()).setActivity(this);
        Utils.pushOpenScreenEvent(this, "VideoUploadScreen",
                SharedPrefUtils.getUserDetailModel(this).getDynamoId() + "");
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        firebaseAuth = FirebaseAuth.getInstance();
        contentUri = getIntent().getParcelableExtra("uri");
        title = getIntent().getStringExtra("title");
        categoryId = getIntent().getStringExtra("categoryId");
        thumbnailTime = getIntent().getStringExtra("thumbnailTime");
        extension = getIntent().getStringExtra("extension");
        comingFrom = getIntent().getStringExtra("comingFrom");
        if (getIntent().hasExtra("langs")) {
            langs = getIntent().getStringArrayListExtra("langs");
        }
        if ("Challenge".equals(comingFrom)) {
            challengeId = getIntent().getStringExtra("ChallengeId");
        }
        needOpinionTextView = findViewById(R.id.needOpinionTextView);
        youAreDoneView = findViewById(R.id.youAreDoneView);
        joinVloggersGroup = findViewById(R.id.joinVloggersGroup);
        cancelImage = findViewById(R.id.cancelImage);
        uploadingContainer = findViewById(R.id.uploadingContainer);
        uploadFinishContainer = findViewById(R.id.uploadFinishContainer);
        okayTextView = findViewById(R.id.okayTextView);
        progressBar = findViewById(R.id.progressBar);
        mtxtpercentage = findViewById(R.id.percentage);
        mtxtvideosize = findViewById(R.id.video_size);
        mtxtvideoname = findViewById(R.id.video_name);
        imgCancelUpload = findViewById(R.id.cancel_upload);
        bottomLayout = findViewById(R.id.bottomLayout);
        back = findViewById(R.id.back);
        modrationText = findViewById(R.id.modrationText);
        okayTextView.setOnClickListener(this);
        imgCancelUpload.setOnClickListener(this);
        modrationText.setOnClickListener(this);
        back.setOnClickListener(this);
        joinVloggersGroup.setOnClickListener(this);
        uploadingContainer.setVisibility(View.VISIBLE);
        uploadFinishContainer.setVisibility(View.GONE);
        mixpanel = MixpanelAPI.getInstance(BaseApplication.getAppContext(), AppConstants.MIX_PANEL_TOKEN);
    }

    private void checkCreatorGroupStatus() {
        GroupMembershipStatus groupMembershipStatus = new GroupMembershipStatus(this);
        groupId = AppUtils.getCreatorGroupIdForLanguage();
        groupMembershipStatus.checkMembershipStatus(groupId,
                SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId());
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        firebaseAuth.signInAnonymously()
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("VideoUpload", "signInAnonymously:success");
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        if (contentUri != null && AppConstants.VIDEO_UPLOAD_NOT_STARTED.equals(uploadStatus)) {
                            uploadToFirebase(contentUri);
                        }

                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("VideoUpload", "signInAnonymously:failure", task.getException());
                        Toast.makeText(VideoUploadProgressActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void uploadToFirebase(Uri file2) {
        FirebaseStorage storage = FirebaseStorage.getInstance("gs://api-project-3577377239.appspot.com");
        uploadStatus = AppConstants.VIDEO_UPLOAD_IN_PROGRESS;
        final StorageReference storageRef = storage.getReference();
        suffixName = System.currentTimeMillis();
        final StorageReference riversRef = storageRef
                .child("user/" + SharedPrefUtils.getUserDetailModel(this).getDynamoId() + "/path/to/" + file2
                        .getLastPathSegment() + "_" + suffixName);
        uploadTask = riversRef.putFile(file2);

        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(exception -> {
            // Handle unsuccessful uploads
            isUploading = false;
            uploadStatus = AppConstants.VIDEO_UPLOAD_FAILED;
            MixPanelUtils.pushVideoUploadFailureEvent(mixpanel, title, exception.getMessage());
            createRowForFailedAttempt(exception.getMessage());

        }).addOnSuccessListener(taskSnapshot -> {
            MixPanelUtils.pushVideoUploadSuccessEvent(mixpanel, title);
            riversRef.getDownloadUrl().addOnSuccessListener(uri -> {
                isUploading = false;
                uploadStatus = AppConstants.VIDEO_UPLOAD_SUCCESS;
                Uri downloadUri = uri;
                publishVideo(uri);
            });
        });

        uploadTask.addOnProgressListener(taskSnapshot -> {
            Log.e("video uplo to firebase=", "Bytes uploaded: " + taskSnapshot.getBytesTransferred());
            isUploading = true;
            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
            double mb = (double) taskSnapshot.getTotalByteCount() / (1024 * 1024);
            DecimalFormat df = new DecimalFormat();
            df.setMaximumFractionDigits(2);
            int currentprogress = (int) progress;
            mtxtvideosize.setText("(" + df.format(mb) + "MB)");
            mtxtvideoname
                    .setText("UPLOADING " + contentUri.getLastPathSegment() + "_" + suffixName + "." + extension);
            mtxtpercentage.setText(currentprogress + "%");
            progressBar.setProgress(currentprogress);
        });
    }

    private void createRowForFailedAttempt(String message) {
        ArrayList<String> catList = new ArrayList<String>();
        catList.add(categoryId);
        if (comingFrom.equals("Challenge")) {
            catList.add(challengeId);
        }
        UploadVideoRequest uploadVideoRequest = new UploadVideoRequest();
        uploadVideoRequest.setTitle(title);
        uploadVideoRequest.setCategory_id(catList);
        uploadVideoRequest.setReason("" + message);
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        VlogsListingAndDetailsAPI api = retrofit.create(VlogsListingAndDetailsAPI.class);
        Call<ResponseBody> call = api.publishHomeVideo(uploadVideoRequest);
        call.enqueue(publishVideoResponseCallback);
    }

    private void publishVideo(Uri uri) {
        ArrayList<String> catList = new ArrayList<String>();
        catList.add(categoryId);
        if (comingFrom.equals("Challenge")) {
            catList.add(challengeId);
        }
        UploadVideoRequest uploadVideoRequest = new UploadVideoRequest();
        uploadVideoRequest.setTitle(title);
        if (langs != null && !langs.isEmpty()) {
            uploadVideoRequest.setLang(langs.toArray(new String[langs.size()]));
        }
        uploadVideoRequest.setFilename(contentUri.getLastPathSegment() + "_" + suffixName);
        uploadVideoRequest.setCategory_id(catList);
        uploadVideoRequest
                .setFile_location("user/" + SharedPrefUtils.getUserDetailModel(this).getDynamoId() + "/path/to/");
        uploadVideoRequest.setUploaded_url(uri.toString());
        uploadVideoRequest.setThumbnail_milliseconds(thumbnailTime);
        uploadVideoRequest.setUser_agent("Android");

        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        VlogsListingAndDetailsAPI api = retrofit.create(VlogsListingAndDetailsAPI.class);
        Call<ResponseBody> call = api.publishHomeVideo(uploadVideoRequest);
        call.enqueue(publishVideoResponseCallback);
    }

    private Callback<ResponseBody> publishVideoResponseCallback = new Callback<ResponseBody>() {
        @Override
        public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
            if (response.body() == null) {
                if (response.errorBody() != null) {
                    if (response.code() == 409) {
                        showToast("This title already exists. Kindly write a new title.");
                        finish();
                    }
                    MixPanelUtils.pushVideoPublishSuccessEvent(mixpanel, title);
                    uploadingContainer.setVisibility(View.GONE);
                    uploadFinishContainer.setVisibility(View.VISIBLE);
                } else {
                    NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                    FirebaseCrashlytics.getInstance().recordException(nee);
                }
                return;
            }
            try {
                if (response.isSuccessful()) {
                    MixPanelUtils.pushVideoPublishSuccessEvent(mixpanel, title);
                    uploadingContainer.setVisibility(View.GONE);
                    uploadFinishContainer.setVisibility(View.VISIBLE);
                    checkCreatorGroupStatus();
                    youAreDoneView.setVisibility(View.VISIBLE);
                    cancelImage.setVisibility(View.VISIBLE);
                    final Handler handler = new Handler(Looper.getMainLooper());
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (youAreDoneView.getVisibility() == View.VISIBLE) {
                                youAreDoneView.setVisibility(View.INVISIBLE);
                                cancelImage.setVisibility(View.INVISIBLE);
                            }
                        }
                    }, 3000);
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

    @Override
    public void onBackPressed() {
        if (isUploading) {
            showAlertDialog("Momspresso", getString(R.string.video_progress_progress_lost_msg), new OnButtonClicked() {
                @Override
                public void onButtonCLick(int buttonId) {
                    if (uploadTask != null && isUploading) {
                        uploadTask.cancel();
                        finish();
                    }
                }
            });
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.modrationText: {
                Utils.shareEventTracking(this, "Post creation", "Create_Android", "PCV_Moderation_Guide");
                handleDeeplinks("https://www.momspresso.com/moderation-rules");
                break;
            }
            case R.id.back: {
                Intent intent = new Intent(this, DashboardActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
            }
            case R.id.cancelImage: {
                youAreDoneView.setVisibility(View.INVISIBLE);
                cancelImage.setVisibility(View.INVISIBLE);
                break;
            }
            case R.id.joinVloggersGroup: {
                if (joinVloggersGroup.getTag() == "already_join") {
                    Utils.shareEventTracking(this, "Post creation", "Create_Android", "PCV_Create_M");
                    Intent videoCreationIntent = new Intent(
                            this,
                            VideoCategoryAndChallengeSelectionActivity.class
                    );
                    videoCreationIntent.setFlags(
                            (Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    startActivity(videoCreationIntent);
                } else {
                    Utils.shareEventTracking(this, "Post creation", "Create_Android", "PCV_Suppport_Group_M");
                    Intent intent = new Intent(this, GroupsSummaryActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.putExtra("groupId", groupId);
                    intent.putExtra("comingFrom", "story");
                    startActivity(intent);
                }
                break;
            }
            case R.id.okayTextView:
                Intent intent = new Intent(VideoUploadProgressActivity.this, DashboardActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra("showInviteDialog", true);
                intent.putExtra("source", AppConstants.CONTENT_TYPE_VIDEO);
                startActivity(intent);
                finish();
                break;
            case R.id.cancel_upload:
                if (isUploading) {
                    showAlertDialog("Momspresso", getString(R.string.video_progress_progress_lost_msg),
                            buttonId -> {
                                if (uploadTask != null && isUploading) {
                                    uploadTask.cancel();
                                    finish();
                                }
                            });
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onMembershipStatusFetchSuccess(GroupsMembershipResponse body, int groupId) {
        bottomLayout.setVisibility(View.VISIBLE);
        if (body.getData().getResult() == null || body.getData().getResult().isEmpty() || body.getData().getResult()
                .get(0).getStatus().equals("2")) {
            joinVloggersGroup.setText(getString(R.string.join_creator_group));
            needOpinionTextView.setText(getString(R.string.get_tips_ideas));
            joinVloggersGroup.setTag("please_join");
        } else {
            joinVloggersGroup.setText(getString(R.string.create_more));
            needOpinionTextView.setText(getString(R.string.dont_stop_magic));
            joinVloggersGroup.setTag("already_join");
        }
    }

    @Override
    public void onMembershipStatusFetchFail() {
        bottomLayout.setVisibility(View.VISIBLE);
        joinVloggersGroup.setText("Join Creator's Hangout");
        needOpinionTextView.setText("Get tips or ideas from other creators");
        joinVloggersGroup.setTag("please_join");
    }
}
