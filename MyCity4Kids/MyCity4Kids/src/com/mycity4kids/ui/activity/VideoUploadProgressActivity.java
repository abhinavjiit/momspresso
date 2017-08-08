package com.mycity4kids.ui.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.kelltontech.utils.StringUtils;
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
import com.mycity4kids.ui.TusAndroidUpload;
import com.mycity4kids.ui.TusClient;
import com.mycity4kids.ui.TusUpload;
import com.mycity4kids.ui.TusUploader;

import java.net.URL;

import io.tus.android.client.TusPreferencesURLStore;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by hemant on 11/1/17.
 */
public class VideoUploadProgressActivity extends BaseActivity implements View.OnClickListener {

    private Toolbar mToolbar;
    //    private CircleProgressBar mCircleView;
    RelativeLayout uploadFinishContainer, uploadingContainer;
    private TusClient client;
    private TextView status, okayTextView;
//    private TextView cancelTextView;

    private UploadTask uploadTask;
    private Uri contentURI;
    private String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_upload_progress_activity);
        Utils.pushOpenScreenEvent(this, "VideoUploadProgressActivity", SharedPrefUtils.getUserDetailModel(this).getDynamoId() + "");
        contentURI = getIntent().getParcelableExtra("uri");
        title = getIntent().getStringExtra("title");

        uploadingContainer = (RelativeLayout) findViewById(R.id.uploadingContainer);
        uploadFinishContainer = (RelativeLayout) findViewById(R.id.uploadFinishContainer);
//        mCircleView = (CircleProgressBar) findViewById(R.id.circleView);
//        cancelTextView = (TextView) findViewById(R.id.cancelTextView);
        status = (TextView) findViewById(R.id.status);
        okayTextView = (TextView) findViewById(R.id.okayTextView);

        okayTextView.setOnClickListener(this);

        uploadingContainer.setVisibility(View.VISIBLE);
        uploadFinishContainer.setVisibility(View.GONE);

//        mCircleView.setStartPositionInDegrees(ProgressStartPoint.DEFAULT);
//        mCircleView.setLinearGradientProgress(false);

        try {
            SharedPreferences pref = getSharedPreferences("tus", 0);
            client = new TusClient();
            client.setUploadCreationURL(new URL(AppConstants.VIDEO_UPLOAD_URL));
            client.enableResuming(new TusPreferencesURLStore(pref));
        } catch (Exception e) {
//            showError(e);
        }

        try {
            TusUpload upload = new TusAndroidUpload(contentURI, this);
            uploadTask = new UploadTask(this, client, upload);
            uploadTask.execute(new Void[0]);
        } catch (Exception e) {
//            showError(e);
        }

    }

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
        showAlertDialog("mycity4kids", "Your upload progress will be lost. Are you sure you want to exit?", new OnButtonClicked() {
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
                showToast("Something went wrong from server");
                return;
            }
            try {
                UpdateVideoDetailsResponse responseData = (UpdateVideoDetailsResponse) response.body();
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
        if (null != uploadTask && uploadTask.getStatus() == AsyncTask.Status.RUNNING) {
            showAlertDialog("mycity4kids", getString(R.string.video_progress_progress_lost_msg), new OnButtonClicked() {
                @Override
                public void onButtonCLick(int buttonId) {
                    uploadTask.cancel(true);
                    finish();
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
        }
    }
}
