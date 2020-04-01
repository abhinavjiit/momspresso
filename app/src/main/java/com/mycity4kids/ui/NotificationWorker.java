package com.mycity4kids.ui;

import android.accounts.NetworkErrorException;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask.TaskSnapshot;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.models.request.UploadVideoRequest;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.receiver.CancelNotification;
import com.mycity4kids.retrofitAPIsInterfaces.VlogsListingAndDetailsAPI;
import com.mycity4kids.ui.activity.UserPublishedContentActivity;
import java.text.DecimalFormat;
import java.util.ArrayList;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class NotificationWorker extends Worker {

    private long suffixName;
    private String title;
    private Uri contentUri;
    private NotificationCompat.Builder notification;
    private String categoryId;
    private String challengeId;
    private String comingFrom;
    private String thumbnailTime;
    private String dataUri;
    private Context context;
    private NotificationManagerCompat manager;
    private com.google.firebase.storage.UploadTask uploadTask;
    private BroadcastReceiver reciver;
    private int notificationId = 0;

    public NotificationWorker(@NonNull Context context,
            @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
    }

    @NonNull
    @Override
    public Result doWork() {
        dataUri = getInputData().getString("ContentUrl");
        contentUri = Uri.parse(dataUri);

        categoryId = getInputData().getString("categoryId");
        challengeId = getInputData().getString("challengeId");
        comingFrom = getInputData().getString("comingFrom");
        thumbnailTime = getInputData().getString("thumbnailTime");
        title = getInputData().getString("title");
        uploadToFirebase(contentUri);

        return Result.success();

    }

    private void uploadToFirebase(Uri file2) {
        try {
            FirebaseStorage storage = FirebaseStorage.getInstance("gs://api-project-3577377239.appspot.com");
            final StorageReference storageRef = storage.getReference();
            suffixName = System.currentTimeMillis();
            final StorageReference riversRef = storageRef
                    .child("user/" + SharedPrefUtils.getUserDetailModel(context).getDynamoId() + "/path/to/"
                            + file2
                            .getLastPathSegment() + "_" + suffixName);
            uploadTask = riversRef.putFile(file2);
            uploadTask.addOnFailureListener(new OnFailureListener() {

                @Override
                public void onFailure(@NonNull Exception exception) {
                    createForegroundInfo(0, "failed");
                }
            }).addOnSuccessListener(new OnSuccessListener<TaskSnapshot>() {
                @Override
                public void onSuccess(com.google.firebase.storage.UploadTask.TaskSnapshot taskSnapshot) {
                    riversRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            if (uploadTask != null) {
                                publishVideo(uri);
                            }
                        }
                    });
                }
            });

            uploadTask.addOnProgressListener(new OnProgressListener<TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull com.google.firebase.storage.UploadTask.TaskSnapshot taskSnapshot) {
                    Log.e("video uplo to firebase=", "Bytes uploaded: " + taskSnapshot.getBytesTransferred());
                    double progress =
                            (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                    double mb = (double) taskSnapshot.getTotalByteCount() / (1024 * 1024);
                    DecimalFormat df = new DecimalFormat();
                    df.setMaximumFractionDigits(2);
                    int currentprogress = (int) progress;
                    Log.d("upload_Tak_progress", "running");
                    if (uploadTask != null) {
                        createForegroundInfo(currentprogress, "Uploading");
                    }
                    if (currentprogress == 0) {
                        reciver = new BroadcastReceiver() {
                            @Override
                            public void onReceive(Context context, Intent intent) {
                                if (uploadTask != null) {
                                    uploadTask.cancel();
                                    NotificationManagerCompat notificationManager =
                                            NotificationManagerCompat.from(getApplicationContext());
                                    notificationManager.cancel(0);
                                    onStopped();
                                    uploadTask = null;
                                    Log.d("upload_Tak_progress", "Stopped");
                                }
                            }
                        };
                        context.registerReceiver(reciver, new IntentFilter("Cancel_Video_Uploading_Notification"));
                    }
                }
            });
        } catch (Exception e) {
            Crashlytics.logException(e);
            Log.d("MC4kException", Log.getStackTraceString(e));
        }
    }


    private void publishVideo(Uri uri) {
        ArrayList<String> catList = new ArrayList<String>();
        catList.add(categoryId);
        if (comingFrom.equals("Challenge")) {
            catList.add(challengeId);
        }
        UploadVideoRequest uploadVideoRequest = new UploadVideoRequest();
        uploadVideoRequest.setTitle(title);
        uploadVideoRequest.setFilename(contentUri.getLastPathSegment() + "_" + suffixName);

        uploadVideoRequest.setCategory_id(catList);
        uploadVideoRequest
                .setFile_location("user/" + SharedPrefUtils.getUserDetailModel(context).getDynamoId()
                        + "/path/to/");
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
        public void onResponse(@NonNull Call<ResponseBody> call, Response<ResponseBody> response) {
            if (response.body() == null) {
                if (response.errorBody() != null) {
                    if (response.code() == 409) {
                        //This title already exists. Kindly write a new title.
                        createForegroundInfo(0, "Successfully Uploaded");
                    }
                } else if (response != null && response.raw() != null) {
                    NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                    Crashlytics.logException(nee);
                }
                return;
            }
            try {
                if (response.isSuccessful()) {
                    createForegroundInfo(100, "Successfully Uploaded");
                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    private void createForegroundInfo(int progress, String contentTitle) {
        PendingIntent intent = WorkManager.getInstance(context)
                .createCancelPendingIntent(getId());

        Intent cancelIntent = new Intent(context, CancelNotification.class);
        Bundle extras = new Bundle();
        if ("Successfully Uploaded".equals(contentTitle)) {
            extras.putInt("notification_id", notificationId);
            cancelIntent.putExtras(extras);
        }
        PendingIntent pendingCancelIntent =
                PendingIntent.getBroadcast(context, 100, cancelIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent clickVideoNotificationIntent = new Intent(getApplicationContext(), UserPublishedContentActivity.class);
        clickVideoNotificationIntent.putExtra("isPrivateProfile", true);
        clickVideoNotificationIntent.putExtra(Constants.AUTHOR_ID,
                SharedPrefUtils.getUserDetailModel(getApplicationContext()).getDynamoId());
        clickVideoNotificationIntent.putExtra("contentType", AppConstants.CONTENT_TYPE_VIDEO);
        PendingIntent contentIntent = PendingIntent
                .getActivity(getApplicationContext(), 0, clickVideoNotificationIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);

        manager = NotificationManagerCompat.from(context);
        String channelId = "video_uploading";
        String channelName = "BackgroundVideoUploading";

        if (VERSION.SDK_INT >= VERSION_CODES.O) {
            NotificationChannel channel = manager.getNotificationChannel(channelId);
            if (channel == null) {
                channel = new NotificationChannel(channelId, channelName,
                        NotificationManager.IMPORTANCE_HIGH);
            }
            manager.createNotificationChannel(channel);
        }

        if ("failed".equals(contentTitle)) {
            notification = new NotificationCompat.Builder(context, channelId)
                    .setContentTitle(contentTitle)
                    .setContentText(title)
                    .setAutoCancel(true)
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher))
                    .setSmallIcon(R.drawable.icon_notify) // notification icon
                    .setProgress(100, progress, false)
                    .setContentIntent(intent);


        } else if ("Successfully Uploaded".equals(contentTitle)) {
            notification = new NotificationCompat.Builder(context, channelId)
                    .setContentTitle(contentTitle)
                    .setContentText(title)
                    .setAutoCancel(true)
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher))
                    .setSmallIcon(R.drawable.icon_notify) // notification icon
                    .setProgress(100, progress, false)
                    .addAction(R.drawable.myicon, "Open", contentIntent)
                    .setColor(ContextCompat.getColor(context, R.color.app_red))
                    .setContentIntent(contentIntent);
        } else {
            if (notification == null) {
                notification = new NotificationCompat.Builder(context, channelId)
                        .setContentTitle(contentTitle)
                        .setContentText(title)
                        .setOngoing(true)
                        .setAutoCancel(false)
                        .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher))
                        .setSmallIcon(R.drawable.icon_notify) // notification icon
                        .setProgress(100, progress, false)
                        .setOnlyAlertOnce(true)
                        .setColor(ContextCompat.getColor(context, R.color.app_red))
                        .addAction(R.drawable.myicon, "Stop Uploading", pendingCancelIntent);
            } else {
                notification.setProgress(100, progress, false)
                        .setContentText(title);
            }
        }
        manager.notify(notificationId, notification.build());


    }

    @Override
    public void onStopped() {
        super.onStopped();
        context.unregisterReceiver(reciver);
        Log.d("onStopped", "uploadTask_Canceled");
    }
}


