package com.mycity4kids.ui.activity;

import android.accounts.NetworkErrorException;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.kelltontech.utils.StringUtils;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.listener.OnButtonClicked;
import com.mycity4kids.models.request.UploadVideoRequest;
import com.mycity4kids.models.response.UpdateVideoDetailsResponse;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.UploadVideosAPI;
import com.mycity4kids.retrofitAPIsInterfaces.VlogsListingAndDetailsAPI;
import com.mycity4kids.ui.TusClient;
import com.mycity4kids.ui.TusUpload;
import com.mycity4kids.ui.TusUploader;
import com.mycity4kids.utils.MixPanelUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by hemant on 11/1/17.
 */
public class VideoUploadProgressActivity extends BaseActivity implements View.OnClickListener {

    private Toolbar mToolbar;
    RelativeLayout uploadFinishContainer, uploadingContainer;
    private TusClient client;
    private TextView status, okayTextView, mTxtPercentage, mTxtVideoSize, mTxtVideoName, mTxtVideoExtension;
    private FirebaseAuth mAuth;
    private UploadTask uploadTask;
    private boolean isUploading = false;
    private Uri contentURI;
    private String title;
    private String categoryId, challengeId, challengeName, comingFrom;
    private String duration;
    private String thumbnailTime;
    private MixpanelAPI mixpanel;
    private long suffixName;
    private String jsonMyObject;
    private ProgressBar mProgressBar;
    private ProgressDialog progressdialog;
    private Handler handler = new Handler();
    private int statusProgress = 0;
    private String extension;
    private ImageView mImgCancelUpload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_upload_progress_activity);
        Utils.pushOpenScreenEvent(this, "VideoUploadScreen", SharedPrefUtils.getUserDetailModel(this).getDynamoId() + "");

        mAuth = FirebaseAuth.getInstance();

        contentURI = getIntent().getParcelableExtra("uri");
        title = getIntent().getStringExtra("title");
        categoryId = getIntent().getStringExtra("categoryId");
        duration = getIntent().getStringExtra("duration");
        thumbnailTime = getIntent().getStringExtra("thumbnailTime");
        extension = getIntent().getStringExtra("extension");


        comingFrom = getIntent().getStringExtra("comingFrom");
        if (comingFrom.equals("Challenge")) {
            challengeId = getIntent().getStringExtra("ChallengeId");
            challengeName = getIntent().getStringExtra("ChallengeName");
        }

        uploadingContainer = (RelativeLayout) findViewById(R.id.uploadingContainer);
        uploadFinishContainer = (RelativeLayout) findViewById(R.id.uploadFinishContainer);
        status = (TextView) findViewById(R.id.status);
        okayTextView = (TextView) findViewById(R.id.okayTextView);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mTxtPercentage = (TextView) findViewById(R.id.percentage);
        mTxtVideoSize = (TextView) findViewById(R.id.video_size);
        mTxtVideoName = (TextView) findViewById(R.id.video_name);
        mTxtVideoExtension = (TextView) findViewById(R.id.video_extension);
        mImgCancelUpload = (ImageView)findViewById(R.id.cancel_upload);

        okayTextView.setOnClickListener(this);
        mImgCancelUpload.setOnClickListener(this);

        uploadingContainer.setVisibility(View.VISIBLE);
        uploadFinishContainer.setVisibility(View.GONE);

        mixpanel = MixpanelAPI.getInstance(BaseApplication.getAppContext(), AppConstants.MIX_PANEL_TOKEN);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        mAuth.signInAnonymously()
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("VideoUpload", "signInAnonymously:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (contentURI != null) {
                                uploadToFirebase(contentURI);
                            }

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("VideoUpload", "signInAnonymously:failure", task.getException());
                            Toast.makeText(VideoUploadProgressActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
//                            updateUI(null);
                        }

                        // ...
                    }
                });

    }

    private void uploadToFirebase(Uri file2) {
        FirebaseStorage storage = FirebaseStorage.getInstance("gs://api-project-3577377239.appspot.com");

        final StorageReference storageRef = storage.getReference();
//        extension = file2.substring(uri.lastIndexOf("."));
        suffixName = System.currentTimeMillis();
//        Uri file = Uri.fromFile(file2);
        final StorageReference riversRef = storageRef.child("user/" + SharedPrefUtils.getUserDetailModel(this).getDynamoId() + "/path/to/" + file2.getLastPathSegment() + "_" + suffixName);
        com.google.firebase.storage.UploadTask uploadTask = riversRef.putFile(file2);

// Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {

            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                isUploading = false;
                MixPanelUtils.pushVideoUploadFailureEvent(mixpanel, title);
                createRowForFailedAttempt(exception.getMessage());

            }
        }).addOnSuccessListener(new OnSuccessListener<com.google.firebase.storage.UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(com.google.firebase.storage.UploadTask.TaskSnapshot taskSnapshot) {
                MixPanelUtils.pushVideoUploadSuccessEvent(mixpanel, title);
                riversRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        isUploading = false;
                        Uri downloadUri = uri;
                        publishVideo(uri);
                    }
                });
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                // ...
            }
        });

        uploadTask.addOnProgressListener(new OnProgressListener<com.google.firebase.storage.UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(com.google.firebase.storage.UploadTask.TaskSnapshot taskSnapshot) {
                Log.e("video uplo to firebase=", "Bytes uploaded: " + taskSnapshot.getBytesTransferred());
                isUploading = true;
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                double mb = (double) taskSnapshot.getTotalByteCount() / (1024 * 1024);
                DecimalFormat df = new DecimalFormat();
                df.setMaximumFractionDigits(2);
                int currentprogress = (int) progress;
                mTxtVideoSize.setText("(" + df.format(mb) + "MB)");
                mTxtVideoName.setText("UPLOADING " + contentURI.getLastPathSegment() + "_" + suffixName + "." + extension);
//                mTxtVideoExtension.setText("." + extension);

                mTxtPercentage.setText(currentprogress + "%");
                mProgressBar.setProgress(currentprogress);
//                progressBar.setProgress(currentprogress);
            }
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
//        uploadVideoRequest.setThumbnail_milliseconds(thumbnailTime);

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
        uploadVideoRequest.setFilename(contentURI.getLastPathSegment() + "_" + suffixName);
        uploadVideoRequest.setCategory_id(catList);
        uploadVideoRequest.setFile_location("user/" + SharedPrefUtils.getUserDetailModel(this).getDynamoId() + "/path/to/");
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
            if (response == null || response.body() == null) {
                if (response.errorBody() != null) {
                    MixPanelUtils.pushVideoPublishSuccessEvent(mixpanel, title);
                    uploadingContainer.setVisibility(View.GONE);
                    uploadFinishContainer.setVisibility(View.VISIBLE);
                } else if (response != null && response.raw() != null) {
                    NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                    Crashlytics.logException(nee);
                }
                return;
            }
            try {
                if (response.isSuccessful()) {
                    MixPanelUtils.pushVideoPublishSuccessEvent(mixpanel, title);
                    uploadingContainer.setVisibility(View.GONE);
                    uploadFinishContainer.setVisibility(View.VISIBLE);
                } else {

                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
//                showToast(getString(R.string.went_wrong));
            }
        }

        @Override
        public void onFailure(Call<ResponseBody> call, Throwable t) {
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    private void setStatus(String text) {
        status.setText(text);
    }

    private void setUploadProgress(int progress) {
//        mCircleView.setProgress(progress);
//        mCircleView.setText("" + progress + "%", Color.DKGRAY);
    }

    private class UploadTask extends AsyncTask<Void, Long, String> {
        private VideoUploadProgressActivity activity;
        private TusClient client;
        private TusUpload upload;
        private Exception exception;

        public UploadTask(VideoUploadProgressActivity activity, TusClient client, TusUpload upload) {
            this.activity = activity;
            this.client = client;
            this.upload = upload;
        }

        @Override
        protected void onPreExecute() {
//            activity.setStatus("Upload selected...");
            activity.setPauseButtonEnabled(true);
        }

        @Override
        protected void onPostExecute(String videoId) {
            if (!StringUtils.isNullOrEmpty(videoId)) {
//                cancelTextView.setVisibility(View.GONE);

                UploadVideoRequest uploadVideoRequest = new UploadVideoRequest();
                uploadVideoRequest.setVideo_id(videoId);
                uploadVideoRequest.setTitle(title);

                Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
                UploadVideosAPI updateVideoUrlAPI = retrofit.create(UploadVideosAPI.class);
                Call<UpdateVideoDetailsResponse> updateVideoUrlCall = updateVideoUrlAPI.updateUploadedVideoURL(uploadVideoRequest);
                updateVideoUrlCall.enqueue(updateVideoUrlResponseCallback);
            } else {
                showToast(getString(R.string.video_progress_uploading_error));
                finish();
            }
//            activity.setStatus("Upload finished!\n");
//            activity.setPauseButtonEnabled(false);
        }

        @Override
        protected void onCancelled() {
            if (exception != null) {
//                activity.showError(exception);
            }

            activity.setPauseButtonEnabled(false);
        }

        @Override
        protected void onProgressUpdate(Long... updates) {
            long uploadedBytes = updates[0];
            long totalBytes = updates[1];
//            activity.setStatus("Upload in progress");
//            activity.setUploadProgress((int) ((double) uploadedBytes / totalBytes * 100));
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                TusUploader uploader = client.resumeOrCreateUpload(upload);
                long totalBytes = upload.getSize();
                long uploadedBytes = uploader.getOffset();

                // Upload file in 10KB chunks
                uploader.setChunkSize(10 * 1024);

                while (!isCancelled() && uploader.uploadChunk() > 0) {
                    uploadedBytes = uploader.getOffset();
                    publishProgress(uploadedBytes, totalBytes);
                }
                uploader.finish();
                String videoId = uploader.getVideoId();
                Log.d("VIDEOID=", "VVV " + videoId);
                return videoId;

            } catch (Exception e) {
                exception = e;
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
                cancel(true);
            }
            return null;
        }
    }

    private void showError(Exception e) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Internal error");
        builder.setMessage(e.getMessage());
        AlertDialog dialog = builder.create();
        dialog.show();
        e.printStackTrace();
    }

    @Override
    protected void updateUi(Response response) {

    }

    public void setPauseButtonEnabled(boolean enabled) {
//        pauseButton.setEnabled(enabled);
//        resumeButton.setEnabled(!enabled);
    }

    public void cancelUpload() {
        showAlertDialog("Momspresso", "Your upload progress will be lost. Are you sure you want to exit?", new OnButtonClicked() {
            @Override
            public void onButtonCLick(int buttonId) {
                if (uploadTask != null) {
                    uploadTask.cancel(true);
                }
                finish();
            }
        });
    }

    private Callback<UpdateVideoDetailsResponse> updateVideoUrlResponseCallback = new Callback<UpdateVideoDetailsResponse>() {
        @Override
        public void onResponse(Call<UpdateVideoDetailsResponse> call, retrofit2.Response<UpdateVideoDetailsResponse> response) {
            if (response == null || response.body() == null) {
                showToast(getString(R.string.server_went_wrong));
                return;
            }
            try {
                UpdateVideoDetailsResponse responseData = response.body();
                Log.d("Response Body = ", "" + new Gson().toJson(responseData));
                if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
//                    setStatus("Upload finished!");
                    setPauseButtonEnabled(false);
//                    showToast("Your video has been succesfully uploaded and sent for moderation. We will notify you once it is published.");
                    uploadingContainer.setVisibility(View.GONE);
                    uploadFinishContainer.setVisibility(View.VISIBLE);
//                    showOkDialog("Video Uploaded Successfully", "Video has been successfully uploaded and send for moderation. We will notifiy you once moderated",
//                            new OnButtonClicked() {
//                                @Override
//                                public void onButtonCLick(int buttonId) {
//                                    Intent intent = new Intent(VideoUploadProgressActivity.this, DashboardActivity.class);
//                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                                    startActivity(intent);
//                                    finish();
//                                }
//                            });

//                    Intent intent = new Intent(VideoUploadProgressActivity.this, DashboardActivity.class);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                    startActivity(intent);
//                    finish();
                } else {
                    setStatus(getString(R.string.video_progress_uploading_failed));
                    showToast(responseData.getReason());
                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                setStatus(getString(R.string.video_progress_uploading_failed));
                Log.d("MC4KException", Log.getStackTraceString(e));
                showToast(getString(R.string.went_wrong));
            }
        }

        @Override
        public void onFailure(Call<UpdateVideoDetailsResponse> call, Throwable t) {
            Crashlytics.logException(t);
            Log.d("MC4KException", Log.getStackTraceString(t));
            setStatus(getString(R.string.video_progress_uploading_failed));
            showToast(getString(R.string.went_wrong));
        }
    };

    @Override
    public void onBackPressed() {
        if (isUploading) {
            showAlertDialog("Momspresso", getString(R.string.video_progress_progress_lost_msg), new OnButtonClicked() {
                @Override
                public void onButtonCLick(int buttonId) {
                    if (isUploading) {
                        uploadTask.cancel(true);
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
            case R.id.okayTextView:
                Intent intent = new Intent(VideoUploadProgressActivity.this, DashboardActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
                break;

            case R.id.cancel_upload:
                if (isUploading) {
                    showAlertDialog("Momspresso", getString(R.string.video_progress_progress_lost_msg), new OnButtonClicked() {
                        @Override
                        public void onButtonCLick(int buttonId) {
                            if (isUploading) {
                                uploadTask.cancel(true);
                                finish();
                            }
                        }
                    });
                }
                break;
        }
    }
}
