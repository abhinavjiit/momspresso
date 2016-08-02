package com.mycity4kids.sync;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.text.Html;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.kelltontech.utils.ConnectivityUtils;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.dbtable.ExternalCalendarTable;
import com.mycity4kids.dbtable.TableAdult;
import com.mycity4kids.dbtable.TableApiEvents;
import com.mycity4kids.dbtable.TableAppointmentData;
import com.mycity4kids.dbtable.TableAttendee;
import com.mycity4kids.dbtable.TableFamily;
import com.mycity4kids.dbtable.TableFile;
import com.mycity4kids.dbtable.TableKids;
import com.mycity4kids.dbtable.TableNotes;
import com.mycity4kids.dbtable.TableTaskData;
import com.mycity4kids.dbtable.TableTaskList;
import com.mycity4kids.dbtable.TableWhoToRemind;
import com.mycity4kids.dbtable.TaskCompletedTable;
import com.mycity4kids.dbtable.TaskTableAttendee;
import com.mycity4kids.dbtable.TaskTableFile;
import com.mycity4kids.dbtable.TaskTableNotes;
import com.mycity4kids.dbtable.TaskTableWhoToRemind;
import com.mycity4kids.dbtable.UserTable;
import com.mycity4kids.newmodels.AppointmentResponse;
import com.mycity4kids.newmodels.AppoitmentDataModel;
import com.mycity4kids.newmodels.PushNotificationModel;
import com.mycity4kids.newmodels.TaskDataModel;
import com.mycity4kids.newmodels.TaskResponse;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.reminders.AppointmentManager;
import com.mycity4kids.reminders.Reminder;
import com.mycity4kids.reminders.ShareNotificationReceiver;
import com.mycity4kids.ui.activity.ActivityEditAppointment;
import com.mycity4kids.ui.activity.ActivityEditTask;
import com.mycity4kids.ui.activity.ActivityLogin;
import com.mycity4kids.ui.activity.ActivityShowAppointment;
import com.mycity4kids.ui.activity.ActivityShowTask;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

/**
 * Created by kapil.vij on 17-07-2015.
 */
public class SyncService extends IntentService implements UpdateListener {

    private final static String TAG = SyncService.class.getSimpleName();
    private boolean taskCallFromAppointment = true;
    private boolean isAppointment = true;
    private int id;
    private PushNotificationModel pushNotificationModel;
    // public static final int APPOINTMENT_NOTIFICATION_ID = 11231;
    // public static final int TASK_NOTIFICATION_ID = 11230;
    private Uri bitmapUri;
    Bitmap icon;


//    public SyncService(boolean callflag,boolean isAppointmentFlag) {
//        super(TAG);
//        TaskCallFromAppointment = callflag;
//        isAppointment  = isAppointmentFlag;
//    }


//    @Override
//    public void onStart(Intent intent, int startId) {
//        super.onStart(intent, startId);
//    }


    public SyncService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        try {
            Bundle b = intent.getExtras();
            if (b != null) {
                pushNotificationModel = b.getParcelable(Constants.PUSH_MODEL);
                isAppointment = b.getBoolean("isAppointmentFlag");
                taskCallFromAppointment = b.getBoolean("callTask");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        icon = BitmapFactory.decodeResource(getResources(),
                R.drawable.ic_launcher);

        if (ConnectivityUtils.isNetworkEnabled(this)) {

            if (isAppointment)
                hitApiRequest(AppConstants.GET_ALL_APPOINTMNET_REQ);
//            else
//                hitApiRequest(AppConstants.GET_ALL_TASK_REQ);

        }
//        _appointmentcontroller = new GetAppointmentController(SyncService.this, this);
//        _taskcontroller = new GetTaskController(SyncService.this, this);
//
//        if (ConnectivityUtils.isNetworkEnabled(this)) {
//            _appointmentcontroller.getData(AppConstants.GET_ALL_APPOINTMNET_REQ, null);
//        }
    }

    private void hitApiRequest(int requestType) {
        ApiHandler handler = new ApiHandler(this, this, requestType);
        handler.execute(getApiUrl(requestType));
    }

    public class SaveAppointmentDataInBackground extends AsyncTask<AppoitmentDataModel, Void, Void> {

        @Override
        protected Void doInBackground(AppoitmentDataModel... model) {
            saveData(model[0]);
            return null;
        }
    }

    public class SaveTaskDataInBackground extends AsyncTask<TaskDataModel, Void, Void> {

        @Override
        protected Void doInBackground(TaskDataModel... model) {
            saveTaskData(model[0]);
            return null;
        }
    }


    public void saveData(AppoitmentDataModel model) {

        TableAppointmentData apponntmentTable = new TableAppointmentData((BaseApplication) getApplicationContext());
        TableFile FileTable = new TableFile((BaseApplication) getApplicationContext());
        TableNotes NoteTable = new TableNotes((BaseApplication) getApplicationContext());
        TableWhoToRemind WhoToRemindTable = new TableWhoToRemind((BaseApplication) getApplicationContext());
        TableAttendee attendeeTable = new TableAttendee((BaseApplication) getApplicationContext());

        try {


            for (AppoitmentDataModel.AppointmentData data : model.getAppointment()) { // appoitment array loop

                SharedPrefUtils.setHomeCheckFlag(this, true);
                // first  delete
                int appointmentid = data.getAppointment().getId();
                apponntmentTable.deleteAppointment(appointmentid + "", false);
                FileTable.deleteAppointment(appointmentid);
                attendeeTable.deleteAppointment(appointmentid);
                NoteTable.deleteAppointment(appointmentid);
                WhoToRemindTable.deleteAppointment(appointmentid);

                // data is saving here
                try {
                    apponntmentTable.beginTransaction();
                    apponntmentTable.insertData(data.getAppointment());
                    apponntmentTable.setTransactionSuccessful();
                } finally {
                    apponntmentTable.endTransaction();
                }


                long startTimeMillis = data.getAppointment().getStarttime();
                String reminderBefore = data.getAppointment().getReminder();
                String recurring = data.getAppointment().getIs_recurring();
                String repeat = data.getAppointment().getRepeate();
                String repeatFrequency = data.getAppointment().getRepeate_frequency();
                String repeatNum = data.getAppointment().getRepeate_num();
                String repeatUntill = data.getAppointment().getRepeate_untill();
                int reminderId = data.getAppointment().getId();
                String appointmentName = data.getAppointment().getAppointment_name();

                for (AppoitmentDataModel.WhoToRemind dataModel : data.getAppointmentWhomRemind()) {
                    if (dataModel.getUser_id().equals(SharedPrefUtils.getUserDetailModel(this).getId())) {
                        Reminder.with(this).info(Constants.REMINDER_TYPE_APPOINTMENT, appointmentName).startTime(startTimeMillis).setRepeatBehavior(repeat, repeatUntill, repeatFrequency, repeatNum).remindBefore(reminderBefore).setRecurring(recurring).create(reminderId);
                        break;

                    }

                }


                for (AppoitmentDataModel.Files filesList : data.getAppointmentFile()) {


                    try {
                        FileTable.beginTransaction();
                        FileTable.insertData(filesList);
                        FileTable.setTransactionSuccessful();
                    } finally {
                        FileTable.endTransaction();
                    }


                }

                for (AppoitmentDataModel.Attendee attendeeList : data.getAppointmentAttendee()) {

                    try {
                        attendeeTable.beginTransaction();
                        attendeeTable.insertData(attendeeList);
                        attendeeTable.setTransactionSuccessful();
                    } finally {
                        attendeeTable.endTransaction();
                    }


                }
                for (AppoitmentDataModel.Notes notesList : data.getAppointmentNote()) {

                    try {
                        NoteTable.beginTransaction();
                        NoteTable.insertData(notesList);
                        NoteTable.setTransactionSuccessful();
                    } finally {
                        NoteTable.endTransaction();
                    }


                }
                for (AppoitmentDataModel.WhoToRemind whotoRemind : data.getAppointmentWhomRemind()) {

                    try {
                        WhoToRemindTable.beginTransaction();
                        WhoToRemindTable.insertData(whotoRemind);
                        WhoToRemindTable.setTransactionSuccessful();
                    } finally {
                        WhoToRemindTable.endTransaction();
                    }


                }
            }

            // for delete appoitmnets

            for (AppoitmentDataModel.AppointmentDelete list : model.getApppointmentDeleted()) {

                int appointmentid = list.getAppointment().getId();
                // delete from db
                apponntmentTable.deleteAppointment(appointmentid + "", false);
                attendeeTable.deleteAppointment(appointmentid);
                WhoToRemindTable.deleteAppointment(appointmentid);
                FileTable.deleteAppointment(appointmentid);
                NoteTable.deleteAppointment(appointmentid);

                Reminder.with(this).cancel(appointmentid);
            }


            // set apponitment map to null
            AppointmentManager.getInstance((BaseApplication) getApplicationContext()).clearList();

            // call pending intent here
            if (pushNotificationModel != null) {
                if (!pushNotificationModel.getAction().equalsIgnoreCase("deleted")) {
                    if (pushNotificationModel.getType().equalsIgnoreCase("Appointment")) {

                        int requestID = (int) System.currentTimeMillis();
                        // Intent shareIntent = shareIntent(pushNotificationModel.getId());

                        String message = pushNotificationModel.getMessage_id();
                        String title = pushNotificationModel.getTitle();
                        NotificationManager mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

                        Intent intent = new Intent(getApplicationContext(), ActivityShowAppointment.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra(AppConstants.NOTIFICATION_ID, requestID);
                        intent.putExtra(AppConstants.EXTRA_APPOINTMENT_ID, pushNotificationModel.getId());

                        PendingIntent contentIntent = PendingIntent.getActivity(this, getUniqueRequestCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
                        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this).setContentTitle(title).setLargeIcon(icon).setSmallIcon(R.drawable.iconnotify).setStyle(new NotificationCompat.BigTextStyle().bigText(message)).setContentText(Html.fromHtml(message));
                        mBuilder.setDefaults(NotificationCompat.DEFAULT_ALL);

                        Intent editActionIntent = new Intent((BaseApplication) getApplicationContext(), ActivityEditAppointment.class);
                        editActionIntent.putExtra(AppConstants.NOTIFICATION_ID, requestID);
                        editActionIntent.putExtra(AppConstants.EXTRA_APPOINTMENT_ID, pushNotificationModel.getId());
                        PendingIntent editIntent = PendingIntent.getActivity((BaseApplication) getApplicationContext(), getUniqueRequestCode(), editActionIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                        Intent shareIntent = new Intent(this, ShareNotificationReceiver.class);
                        shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        shareIntent.putExtra(AppConstants.NOTIFICATION_ID, requestID);
                        shareIntent.putExtra(AppConstants.EXTRA_ID, pushNotificationModel.getId());
                        shareIntent.putExtra(AppConstants.IS_APPOINTMENT, true);
                        PendingIntent sharePendingIntent = PendingIntent.getBroadcast((BaseApplication) getApplicationContext(), getUniqueRequestCode(), shareIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                        //PendingIntent sharePendingIntent = PendingIntent.getActivity((BaseApplication) getApplicationContext(), 0, shareIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                        mBuilder.addAction(R.drawable.edit_xxhdpi, "Edit", editIntent);
                        mBuilder.addAction(R.drawable.share, "Share", sharePendingIntent);

                        mBuilder.setAutoCancel(true);
                        mBuilder.setContentIntent(contentIntent);
                        mNotificationManager.notify(requestID, mBuilder.build());
                    }
                }

            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private String getTime(long timeStampStr) {

        try {
            DateFormat sdf = new SimpleDateFormat("hh:mm a");
            Date netDate = (new Date(timeStampStr));
            return sdf.format(netDate).toUpperCase();
        } catch (Exception ex) {
            return "xx";
        }
    }

    private String getDate(long timeStampStr) {

        try {
            DateFormat sdf = new SimpleDateFormat("dd MMM yyyy, EEEE");
            Date netDate = (new Date(timeStampStr));
            return sdf.format(netDate);
        } catch (Exception ex) {
            return "xx";
        }
    }

    public Intent shareIntent(int id) {
        TableAppointmentData appointmentTable = new TableAppointmentData(BaseApplication.getInstance());
        AppoitmentDataModel.AppointmentDetail appDetail = appointmentTable.getDataByAppointment(id, "", false);

//        TableFile fileTable = new TableFile(BaseApplication.getInstance());
//        ArrayList<AppoitmentDataModel.Files> fileList = fileTable.getDataByAppointment(id);
//
//
//        if (!fileList.isEmpty()) {
//            for (AppoitmentDataModel.Files model : fileList) {
//                if (model.getFile_type().equalsIgnoreCase("image")) {
//
//                    // got image url
//                    Picasso.with(this).load(model.getUrl()).into(new Target() {
//                        @Override
//                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom loadedFrom) {
//                             bitmapUri = getLocalBitmapUri(bitmap);
//                        }
//
//                        @Override
//                        public void onBitmapFailed(Drawable drawable) {
//
//                        }
//
//                        @Override
//                        public void onPrepareLoad(Drawable drawable) {
//                        }
//                    });
//
//                }
//            }
//        }


        if (appDetail != null) {

            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            String type = "text/plain";
//            if (bitmapUri != null) {
//                sendIntent.putExtra(Intent.EXTRA_STREAM, bitmapUri);
//                type = "image/*";
//            }
            sendIntent.setType(type);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "Appointment :" + appDetail.getAppointment_name().toString() + "\n AT " + appDetail.getLocality() + "\n Starting time " + getDate(appDetail.getStarttime()) + " " + getTime(appDetail.getStarttime()) + " \n End time " + getDate(appDetail.getEndtime()) + " " + getTime(appDetail.getEndtime()) + "");
            return Intent.createChooser(sendIntent, "Share Appointment");

        }
        return null;
    }

    public void saveTaskData(TaskDataModel model) {

        TableTaskData apponntmentTable = new TableTaskData((BaseApplication) getApplicationContext());
        TaskTableFile FileTable = new TaskTableFile((BaseApplication) getApplicationContext());
        TaskTableNotes NoteTable = new TaskTableNotes((BaseApplication) getApplicationContext());
        TaskTableWhoToRemind WhoToRemindTable = new TaskTableWhoToRemind((BaseApplication) getApplicationContext());
        TaskTableAttendee attendeeTable = new TaskTableAttendee((BaseApplication) getApplicationContext());
        TableTaskList tableTaskList = new TableTaskList(BaseApplication.getInstance());
        TaskCompletedTable taskCompleted = new TaskCompletedTable(BaseApplication.getInstance());
        try {

            for (TaskDataModel.TaskData data : model.getTask()) { // tasks array loop
                SharedPrefUtils.setHomeCheckFlag(this, true);
                // first  delete
                int taskid = data.getTask().getId();
                apponntmentTable.deleteTask(taskid);
                FileTable.deleteTask(taskid);
                attendeeTable.deleteTask(taskid);
                NoteTable.deleteTask(taskid);
                WhoToRemindTable.deleteTask(taskid);

                // data is saving here
                apponntmentTable.insertData(data.getTask());

                long startTimeMillis = data.getTask().getDue_date();
                String reminderBefore = data.getTask().getReminder();
                String recurring = data.getTask().getIs_recurring();
                String repeat = data.getTask().getRepeate();
                String repeatFrequency = data.getTask().getRepeate_frequency();
                String repeatNum = data.getTask().getRepeate_num();
                String repeatUntill = data.getTask().getRepeate_untill();
                int reminderId = data.getTask().getId();
                String taskName = data.getTask().getTask_name();

                for (TaskDataModel.WhoToRemind dataModel : data.getTaskWhomRemind()) {
                    if (dataModel.getUser_id().equals(SharedPrefUtils.getUserDetailModel(this).getId())) {
                        Reminder.with(this).info(Constants.REMINDER_TYPE_TASKS, taskName).startTime(startTimeMillis).setRepeatBehavior(repeat, repeatUntill, repeatFrequency, repeatNum).remindBefore(reminderBefore).setRecurring(recurring).create(reminderId);
                        break;

                    }

                }

                for (TaskDataModel.Files filesList : data.getTaskFile()) {
                    FileTable.insertData(filesList);
                }

                for (TaskDataModel.Attendee attendeeList : data.getTaskAttendee()) {
                    attendeeTable.insertData(attendeeList);
                }
                if (data.getTaskNote() != null && data.getTaskNote().size() > 0) {
                    for (TaskDataModel.Notes notesList : data.getTaskNote()) {
                        NoteTable.insertData(notesList);
                    }
                }
                if (data.getTaskWhomRemind() != null && data.getTaskWhomRemind().size() > 0) {
                    for (TaskDataModel.WhoToRemind whotoRemind : data.getTaskWhomRemind()) {
                        WhoToRemindTable.insertData(whotoRemind);
                    }
                }
                if (data.getTaskExcludedDate() != null && data.getTaskExcludedDate().size() > 0) {
                    for (TaskDataModel.TaskCompleted completedTask : data.getTaskExcludedDate()) {
                        taskCompleted.insertData(completedTask);
                    }
                }


            }

            // for delete tasks

            for (TaskDataModel.TaskDelete list : model.getTaskDeleted()) {

                int appointmentid = list.getTask().getId();
                // delete from db
                apponntmentTable.deleteTask(appointmentid);
                attendeeTable.deleteTask(appointmentid);
                WhoToRemindTable.deleteTask(appointmentid);
                FileTable.deleteTask(appointmentid);
                NoteTable.deleteTask(appointmentid);

                Reminder.with(this).cancel(appointmentid);
            }


            for (TaskDataModel.TaskList data : model.getTaskList()) {
                tableTaskList.insertData(data.getTaskList());
            }


            // notification handling
            // call pending intent here
            if (pushNotificationModel != null) {

                if (!pushNotificationModel.getAction().equalsIgnoreCase("deleted") && !pushNotificationModel.getAction().equalsIgnoreCase("Excluded")) {
                    if (pushNotificationModel.getType().equalsIgnoreCase("task")) {

                        int requestID = (int) System.currentTimeMillis();

                        String message = pushNotificationModel.getMessage_id();
                        String title = pushNotificationModel.getTitle();
                        NotificationManager mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

                        Intent intent = new Intent(getApplicationContext(), ActivityShowTask.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra(AppConstants.NOTIFICATION_ID, requestID);
                        intent.putExtra(AppConstants.EXTRA_TASK_ID, pushNotificationModel.getId());

                        PendingIntent contentIntent = PendingIntent.getActivity(this, getUniqueRequestCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
                        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this).setLargeIcon(icon).setSmallIcon(R.drawable.iconnotify).setContentTitle(title).setStyle(new NotificationCompat.BigTextStyle().bigText(message)).setContentText(message);
                        mBuilder.setDefaults(NotificationCompat.DEFAULT_ALL);

                        Intent editActionIntent = new Intent((BaseApplication) getApplicationContext(), ActivityEditTask.class);
                        editActionIntent.putExtra(AppConstants.EXTRA_TASK_ID, pushNotificationModel.getId());
                        editActionIntent.putExtra(AppConstants.NOTIFICATION_ID, requestID);
                        PendingIntent editIntent = PendingIntent.getActivity((BaseApplication) getApplicationContext(), getUniqueRequestCode(), editActionIntent, PendingIntent.FLAG_UPDATE_CURRENT);


                        Intent shareIntent = new Intent(this, ShareNotificationReceiver.class);
                        shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        shareIntent.putExtra(AppConstants.NOTIFICATION_ID, requestID);
                        shareIntent.putExtra(AppConstants.EXTRA_ID, pushNotificationModel.getId());
                        shareIntent.putExtra(AppConstants.IS_APPOINTMENT, false);
                        PendingIntent sharePendingIntent = PendingIntent.getBroadcast((BaseApplication) getApplicationContext(), getUniqueRequestCode(), shareIntent, PendingIntent.FLAG_UPDATE_CURRENT);

//                        Intent taskcompleteIntent = new Intent((BaseApplication) getApplicationContext(), DashboardActivity.class);
//                        taskcompleteIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                        taskcompleteIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        taskcompleteIntent.putExtra(AppConstants.NOTIFICATION_ID, TASK_NOTIFICATION_ID);
//                        taskcompleteIntent.putExtra("deleteTaskId", pushNotificationModel.getId());

                        //PendingIntent finishPendingIntent = PendingIntent.getActivity((BaseApplication) getApplicationContext(), 0, taskcompleteIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                        mBuilder.addAction(R.drawable.edit_xxhdpi, "Edit", editIntent);
                        mBuilder.addAction(R.drawable.share, "Share", sharePendingIntent);

                        mBuilder.setAutoCancel(true);
                        mBuilder.setContentIntent(contentIntent);
                        mNotificationManager.notify(requestID, mBuilder.build());
                    }
                }

            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    public void updateView(String jsonString, int requestType) {
        switch (requestType) {
            case AppConstants.GET_ALL_APPOINTMNET_REQ:
                try {

                    AppointmentResponse responseData = new Gson().fromJson(jsonString, AppointmentResponse.class);
                    if (responseData.getResponseCode() == 200) {
                        SharedPrefUtils.setAppointmentTimeSatmp(this, System.currentTimeMillis());
                        // save in db
                        new SaveAppointmentDataInBackground().execute(responseData.getResult().getData());
//
//                        if (taskCallFromAppointment)
//                            hitApiRequest(AppConstants.GET_ALL_TASK_REQ);
                        Log.i(TAG, "response success getAppointment");


                    } else if (responseData.getResponseCode() == 400) {
                        Log.e(TAG, "response failed getAppointment");
                    } else if (responseData.getResponseCode() == 401) {
                        String message = responseData.getResult().getMessage();
                        String pushToken = SharedPrefUtils.getDeviceToken((BaseApplication) getApplicationContext());
                        SharedPrefUtils.clearPrefrence((BaseApplication) getApplicationContext());
                        SharedPrefUtils.setDeviceToken((BaseApplication) getApplicationContext(), pushToken);
                        /**
                         * delete table from local also;
                         */
                        UserTable _tables = new UserTable((BaseApplication) getApplicationContext());
                        _tables.deleteAll();

                        TableFamily _familytables = new TableFamily((BaseApplication) getApplicationContext());
                        _familytables.deleteAll();

                        TableAdult _adulttables = new TableAdult((BaseApplication) getApplicationContext());
                        _adulttables.deleteAll();

                        TableKids _kidtables = new TableKids((BaseApplication) getApplicationContext());
                        _kidtables.deleteAll();

                        new TableAppointmentData(BaseApplication.getInstance()).deleteAll();
                        new TableNotes(BaseApplication.getInstance()).deleteAll();
                        new TableFile(BaseApplication.getInstance()).deleteAll();
                        new TableAttendee(BaseApplication.getInstance()).deleteAll();
                        new TableWhoToRemind(BaseApplication.getInstance()).deleteAll();


                        new TableTaskData(BaseApplication.getInstance()).deleteAll();
                        new TableTaskList(BaseApplication.getInstance()).deleteAll();
                        new TaskTableAttendee(BaseApplication.getInstance()).deleteAll();
                        new TaskTableWhoToRemind(BaseApplication.getInstance()).deleteAll();
                        new TaskTableFile(BaseApplication.getInstance()).deleteAll();
                        new TaskTableNotes(BaseApplication.getInstance()).deleteAll();
                        new TaskCompletedTable(BaseApplication.getInstance()).deleteAll();
                        new TableApiEvents(BaseApplication.getInstance()).deleteAll();

                        new ExternalCalendarTable(BaseApplication.getInstance()).deleteAll();

                        // clear cachee
                        AppointmentManager.getInstance((BaseApplication) getApplicationContext()).clearList();
                        BaseApplication.setBlogResponse(null);
                        BaseApplication.setBusinessREsponse(null);


                        // clear all sessions

                        if (StringUtils.isNullOrEmpty(message)) {
                            Toast.makeText((BaseApplication) getApplicationContext(), getString(R.string.went_wrong), Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText((BaseApplication) getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                        }

                        // set logout flag
                        SharedPrefUtils.setLogoutFlag((BaseApplication) getApplicationContext(), true);

                        Intent in = new Intent((BaseApplication) getApplicationContext(), ActivityLogin.class);
                        in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(in);
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case AppConstants.GET_ALL_TASK_REQ:
                try {
//                    TaskResponse responseData = (TaskResponse) response.getResponseObject();
                    TaskResponse responseData = new Gson().fromJson(jsonString, TaskResponse.class);

                    if (responseData.getResponseCode() == 200) {
                        SharedPrefUtils.setTaskTimeSatmp(this, System.currentTimeMillis());
                        // save in db

                        new SaveTaskDataInBackground().execute(responseData.getResult().getData());
                        //showToast(responseData.getResult().getMessage());
                        Log.i(TAG, "response Success getTask");

                    } else if (responseData.getResponseCode() == 400) {
                        Log.e(TAG, "response failed getAppointment");
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    private String getApiUrl(int requestType) {
        StringBuilder builder = new StringBuilder();
        switch (requestType) {
            case AppConstants.GET_ALL_APPOINTMNET_REQ:
                builder.append(AppConstants.GET_APPOITMENT_URL);
                Calendar c = Calendar.getInstance();
                long timestamp = SharedPrefUtils.getAppointmentTimeSatmp(this);
                if (timestamp > 0) {

                    builder.append("sessionId:").append(SharedPrefUtils.getUserDetailModel(this).getSessionId()).append("/user_id:").append(SharedPrefUtils.getUserDetailModel(this).getId()).append("/family_id:")
                            .append(SharedPrefUtils.getUserDetailModel(this).getFamily_id()).append("/timestamp:")
                            .append(timestamp);

                } else {
                    builder.append("user_id:").append(SharedPrefUtils.getUserDetailModel(this).getId()).append("/family_id:")
                            .append(SharedPrefUtils.getUserDetailModel(this).getFamily_id()).append("/sessionId:")
                            .append(SharedPrefUtils.getUserDetailModel(this).getSessionId());
                }
                Log.i("get appointment ", builder.toString());
                break;
            case AppConstants.GET_ALL_TASK_REQ:
                builder.append(AppConstants.GET_TASK_URL);
                long taskTimeSatmp = SharedPrefUtils.getTaskTimeSatmp(this);
                builder.append("sessionId:").append(SharedPrefUtils.getUserDetailModel(this).getSessionId()).append("/user_id:").append(SharedPrefUtils.getUserDetailModel(this).getId()).append("/family_id:")
                        .append(SharedPrefUtils.getUserDetailModel(this).getFamily_id()).append("/timestamp:")
                        .append(taskTimeSatmp);
                Log.i("get task ", builder.toString());
                break;
        }
        return builder.toString();
    }

    private int getUniqueRequestCode() {
        Random randomGenerator = new Random();
        return randomGenerator.nextInt(1000);
    }
}
