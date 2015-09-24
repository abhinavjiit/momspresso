package com.mycity4kids.reminders;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.content.WakefulBroadcastReceiver;

import com.kelltontech.utils.StringUtils;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.dbtable.TableAppointmentData;
import com.mycity4kids.dbtable.TableFile;
import com.mycity4kids.dbtable.TableTaskData;
import com.mycity4kids.dbtable.TaskTableFile;
import com.mycity4kids.newmodels.AppoitmentDataModel;
import com.mycity4kids.newmodels.TaskDataModel;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class ShareNotificationReceiver extends WakefulBroadcastReceiver {

    private Uri bitmapUri;
    private int id;
    private boolean isAppoitment;
    AppoitmentDataModel.AppointmentDetail appDetail;
    TaskDataModel.TaskDetail taskDetail;

    @Override
    public void onReceive(final Context context, Intent intent) {


        NotificationManager nMgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nMgr.cancel(intent.getIntExtra(AppConstants.NOTIFICATION_ID, 0));
        closeStatusBar(context);

        id = intent.getIntExtra(AppConstants.EXTRA_ID, 0);

        if (intent.getBooleanExtra(AppConstants.IS_APPOINTMENT, false)) {
            // appoitmnet
            TableAppointmentData appointmentTable = new TableAppointmentData(BaseApplication.getInstance());
            appDetail = appointmentTable.getDataByAppointment(id, "", false);

            TableFile fileTable = new TableFile(BaseApplication.getInstance());
            ArrayList<AppoitmentDataModel.Files> fileList = fileTable.getDataByAppointment(id);

            if (!fileList.isEmpty()) {
                for (AppoitmentDataModel.Files model : fileList) {
                    if (model.getFile_type().equalsIgnoreCase("image")) {

                        Picasso.with(context).load(model.getUrl()).into(new Target() {
                            @Override
                            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom loadedFrom) {
                                bitmapUri = getLocalBitmapUri(bitmap);
                                shareAppoitmnet(context, bitmapUri);
                            }

                            @Override
                            public void onBitmapFailed(Drawable drawable) {
                                shareAppoitmnet(context, null);
                            }

                            @Override
                            public void onPrepareLoad(Drawable drawable) {
                            }
                        });


                    }
                }

            } else
                shareAppoitmnet(context, null);


        } else {
            //task
            TableTaskData taskData = new TableTaskData(BaseApplication.getInstance());
            taskDetail = taskData.getTaskbyId(id);

            TaskTableFile fileTable = new TaskTableFile(BaseApplication.getInstance());
            ArrayList<TaskDataModel.Files> fileList = fileTable.getDataByTask(id);

            if (!fileList.isEmpty()) {
                for (TaskDataModel.Files model : fileList) {
                    if (model.getFile_type().equalsIgnoreCase("image")) {

                        Picasso.with(context).load(model.getUrl()).into(new Target() {
                            @Override
                            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom loadedFrom) {
                                bitmapUri = getLocalBitmapUri(bitmap);
                                shareTask(context, bitmapUri);
                            }

                            @Override
                            public void onBitmapFailed(Drawable drawable) {
                                shareTask(context, null);
                            }

                            @Override
                            public void onPrepareLoad(Drawable drawable) {
                            }
                        });

                    }
                }
            } else
                shareTask(context, null);


        }


    }

    private void closeStatusBar(Context context) {
        Intent it = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        context.sendBroadcast(it);
    }

    public void shareTask(Context context, Uri bitmapUri) {

        if (taskDetail != null) {

            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            String type = "text/plain";
            if (bitmapUri != null) {
                sendIntent.putExtra(Intent.EXTRA_STREAM, bitmapUri);
                type = "image/*";
            }
            sendIntent.setType(type);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "Task:" + taskDetail.getTask_name().toString() + "\nDue Date and Time: " + getDate(taskDetail.getDue_date()) + ", " + getTime(taskDetail.getDue_date()));
            Intent byintent = Intent.createChooser(sendIntent, "Share Task");
            byintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(byintent);

        }
    }

    public void shareAppoitmnet(Context context, Uri bitmapUri) {

        if (appDetail != null) {

            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            String type = "text/plain";
            if (bitmapUri != null) {
                sendIntent.putExtra(Intent.EXTRA_STREAM, bitmapUri);
                type = "image/*";
            }
            sendIntent.setType(type);

            if (StringUtils.isNullOrEmpty(appDetail.getLocality().toString()))
                sendIntent.putExtra(Intent.EXTRA_TEXT, "Appointment: " + appDetail.getAppointment_name().toString() + "\nStart Time: " + getDate(appDetail.getStarttime()) + ", " + getTime(appDetail.getStarttime()) + " \nEnd Time: " + getDate(appDetail.getEndtime()) + ", " + getTime(appDetail.getEndtime()));
            else
                sendIntent.putExtra(Intent.EXTRA_TEXT, "Appointment: " + appDetail.getAppointment_name().toString() + "\nStart Time: " + getDate(appDetail.getStarttime()) + ", " + getTime(appDetail.getStarttime()) + " \nEnd Time: " + getDate(appDetail.getEndtime()) + ", " + getTime(appDetail.getEndtime()) + "\nLocation: " + appDetail.getLocality().toString());
            Intent byintent = Intent.createChooser(sendIntent, "Share Appointment");
            byintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(byintent);

        }
    }

    public Uri getLocalBitmapUri(Bitmap bmp) {
        // Extract Bitmap from ImageView drawable

        // Store image to default external storage directory
        Uri bmpUri = null;
        try {
            File file = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS), "share_image_" + System.currentTimeMillis() + ".png");
            file.getParentFile().mkdirs();
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
            bmpUri = Uri.fromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }


    private String getTime(long timeStampStr) {

        try {
            DateFormat sdf = new SimpleDateFormat("hh:mm a");
            Date netDate = (new Date(timeStampStr));
            return sdf.format(netDate).toLowerCase();
        } catch (Exception ex) {
            return "xx";
        }
    }

    private String getDate(long timeStampStr) {

        try {
            DateFormat sdf = new SimpleDateFormat("dd MMMM, yyyy");
            Date netDate = (new Date(timeStampStr));
            return sdf.format(netDate);
        } catch (Exception ex) {
            return "xx";
        }
    }


}
