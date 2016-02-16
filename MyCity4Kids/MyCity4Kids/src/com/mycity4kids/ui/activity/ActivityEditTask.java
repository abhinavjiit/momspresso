package com.mycity4kids.ui.activity;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Base64OutputStream;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.kelltontech.utils.BitmapUtils;
import com.kelltontech.utils.ConnectivityUtils;
import com.kelltontech.utils.StringUtils;
import com.kelltontech.utils.ToastUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.controller.DeleteImagesController;
import com.mycity4kids.controller.ImageUploadController;
import com.mycity4kids.controller.TaskController;
import com.mycity4kids.controller.TaskListController;
import com.mycity4kids.dbtable.TableTaskData;
import com.mycity4kids.dbtable.TableTaskList;
import com.mycity4kids.dbtable.TaskTableAttendee;
import com.mycity4kids.dbtable.TaskTableFile;
import com.mycity4kids.dbtable.TaskTableNotes;
import com.mycity4kids.dbtable.TaskTableWhoToRemind;
import com.mycity4kids.dbtable.UserTable;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.forgot.CommonResponse;
import com.mycity4kids.models.user.ImageUploadRequest;
import com.mycity4kids.models.user.UserModel;
import com.mycity4kids.newmodels.AppointmentMappingModel;
import com.mycity4kids.newmodels.AttendeeModel;
import com.mycity4kids.newmodels.DeleteImageModel;
import com.mycity4kids.newmodels.NotesModel;
import com.mycity4kids.newmodels.TaskDataModel;
import com.mycity4kids.newmodels.TaskListModel;
import com.mycity4kids.newmodels.TaskListResponse;
import com.mycity4kids.newmodels.TaskResponse;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.reminders.Reminder;
import com.mycity4kids.ui.adapter.AdapterEditNotes;
import com.mycity4kids.ui.adapter.AddDocumentAdapter;
import com.mycity4kids.ui.adapter.AddImagesAdapter;
import com.mycity4kids.ui.fragment.AttendeeDialogFragment;
import com.mycity4kids.ui.fragment.DueDateFagment;
import com.mycity4kids.ui.fragment.FragmentTaskList;
import com.mycity4kids.ui.fragment.RemainderDialogFragment;
import com.mycity4kids.ui.fragment.RepeatDialogFragment;
import com.mycity4kids.ui.fragment.RepeatUntilFragment;
import com.mycity4kids.ui.fragment.WhoToRemindDialogFragment;
import com.mycity4kids.utils.DocumentUtils;
import com.mycity4kids.widget.CustomListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by manish.soni on 09-07-2015.
 */
public class ActivityEditTask extends BaseActivity implements View.OnClickListener {

    TextView startDate, startTime, attendee, remainder, textRepeat, repeat, textUntil, until, whoToRemind, taskList;
    long startTimeStamp, endTimeStamp;
    //ImageView save;
    EditText taskName;
    AppointmentMappingModel appointment;
    //    Appointment
    private Toolbar mToolbar;
    CheckBox recurring;

    String finalDate;
    String finalTime;
    Boolean flag = false;

    String select = "";
    String mode = "";
    String value = "";

    public ArrayList<TaskDataModel.Attendee> attendeeDataList;
    public ArrayList<TaskDataModel.WhoToRemind> whoToRemindList;
    public ArrayList<TaskDataModel.Notes> NoteList;
    public ArrayList<TaskDataModel.Files> fileList;
    private String reminderTime;
    private boolean editFlag = false;
    private int taskId;
    private TaskDataModel.TaskDetail editappDetail;
    private ArrayList<AttendeeModel> editattendeeList;
    private ArrayList<AttendeeModel> editwhoToRemindList;
    private ArrayList<TaskDataModel.Files> editfileList;
    private ArrayList<TaskDataModel.Notes> editnoteList;
    private CustomListView notesListView;
    private AdapterEditNotes adapterNotsList;
    TaskDataModel.TaskDetail taskDetail;
    // images
    private CustomListView listviewAttachment;
    private CustomListView listviewImages;
    private ArrayList<TaskDataModel.Files> imageList;
    private ArrayList<TaskDataModel.Files> documentList;
    private AddImagesAdapter imgadapter;
    private AddDocumentAdapter documentadapter;
    private String file_name = "";
    // private ArrayList<String> documentUrl;
    int listId = 0;
    private int deleteImagePostion;
    private boolean isImage = false;
    private ArrayList<DeleteImageModel.DeleteImage> deleteImageList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aa_edit_task);
        Utils.pushOpenScreenEvent(ActivityEditTask.this, "Edit task", SharedPrefUtils.getUserDetailModel(this).getId() + "");
        NotificationManager nMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nMgr.cancel(getIntent().getIntExtra(AppConstants.NOTIFICATION_ID, 0));

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Edit Task");

        notesListView = (CustomListView) findViewById(R.id.noteslist);
        taskName = (EditText) findViewById(R.id.task_name);

        attendee = (TextView) findViewById(R.id.task_attendee);
        whoToRemind = (TextView) findViewById(R.id.task_whotoremind);
        remainder = (TextView) findViewById(R.id.task_remainder);
        recurring = (CheckBox) findViewById(R.id.recurring_event);
        textRepeat = (TextView) findViewById(R.id.repeat_text);
        textUntil = (TextView) findViewById(R.id.until_text);
        repeat = (TextView) findViewById(R.id.task_repeat);
        until = (TextView) findViewById(R.id.task_until);
        taskList = (TextView) findViewById(R.id.task_list);
        taskList.setKeyListener(null);

        listviewAttachment = (CustomListView) findViewById(R.id.list_attachment);
        listviewImages = (CustomListView) findViewById(R.id.list_images);

        listviewImages.isExpanded();
        listviewAttachment.isExpanded();

        findViewById(R.id.add_images).setOnClickListener(this);
        findViewById(R.id.add_attachments).setOnClickListener(this);

        attendee.setKeyListener(null);
        startDate = (TextView) findViewById(R.id.duedate);
        startDate.setKeyListener(null);
        startTime = (TextView) findViewById(R.id.due_time);
        startTime.setKeyListener(null);
        //save.setOnClickListener(this);
        attendee.setOnClickListener(this);
        whoToRemind.setOnClickListener(this);
        startDate.setOnClickListener(this);
        startTime.setOnClickListener(this);
        remainder.setOnClickListener(this);
        repeat.setOnClickListener(this);
        until.setOnClickListener(this);
        taskList.setOnClickListener(this);

        deleteImageList = new ArrayList<>();
        appointment = new AppointmentMappingModel();
        attendeeDataList = new ArrayList<>();
        whoToRemindList = new ArrayList<>();
        fileList = new ArrayList<>();
        imageList = new ArrayList<>();
        documentList = new ArrayList<>();

        //documentUrl = new ArrayList<>();
        appointment = new AppointmentMappingModel();
        attendeeDataList = new ArrayList<>();
        whoToRemindList = new ArrayList<>();


        Intent intent = getIntent();
        if (intent != null)
            taskId = intent.getIntExtra(AppConstants.EXTRA_TASK_ID, 1);

        taskDetail = new TaskDataModel().new TaskDetail();


        // appointment table
        TableTaskData tableTaskData = new TableTaskData(BaseApplication.getInstance());
        editappDetail = tableTaskData.getTaskbyId(taskId);


        if (editappDetail.getIs_recurring().equals("yes")) {

            recurring.setChecked(true);

            textRepeat.setVisibility(View.VISIBLE);
            repeat.setVisibility(View.VISIBLE);
            textUntil.setVisibility(View.VISIBLE);
            until.setVisibility(View.VISIBLE);
        } else {
            textRepeat.setVisibility(View.GONE);
            repeat.setVisibility(View.GONE);
            textUntil.setVisibility(View.GONE);
            until.setVisibility(View.GONE);
        }

        // get from attendee table
        TaskTableAttendee attendeeTable = new TaskTableAttendee(BaseApplication.getInstance());
        editattendeeList = attendeeTable.getDataBytask(taskId);

        for (int j = 0; j < editattendeeList.size(); j++) {

            TaskDataModel.Attendee attendeeModel = new TaskDataModel().new Attendee();
            attendeeModel.setUk_id(editattendeeList.get(j).getId());
            attendeeModel.setUk_type(editattendeeList.get(j).getType());
            //attendeeModel.setId(editattendeeList.get(j).getServerid());
            attendeeDataList.add(attendeeModel);

        }


        // get from whotoRemond table
        TaskTableWhoToRemind whotoRemindTable = new TaskTableWhoToRemind(BaseApplication.getInstance());
        editwhoToRemindList = whotoRemindTable.getDataByTask(taskId);


        for (int j = 0; j < editwhoToRemindList.size(); j++) {

            TaskDataModel.WhoToRemind whoToRemindModel = new TaskDataModel().new WhoToRemind();
            whoToRemindModel.setUser_id(editwhoToRemindList.get(j).getId());
            //whoToRemindModel.setId(editattendeeList.get(j).getServerid());
            whoToRemindList.add(whoToRemindModel);

        }


        // get from FILES
        TaskTableFile fileTable = new TaskTableFile(BaseApplication.getInstance());
        editfileList = fileTable.getDataByTask(taskId);
        fileList = editfileList;

        if (!editfileList.isEmpty()) {
            for (TaskDataModel.Files model : editfileList) {
                if (model.getFile_type().equalsIgnoreCase("image")) {
                    imageList.add(model);
                } else {

                    documentList.add(model);
                    //documentUrl.add(model.getUrl());
                }
            }


        }


        // adapetrs for attachements and images

        imgadapter = new AddImagesAdapter(this, null, imageList, this, false, true);
        listviewImages.setAdapter(imgadapter);

        documentadapter = new AddDocumentAdapter(this, null, documentList, this, false, true);
        listviewAttachment.setAdapter(documentadapter);


        // note

        TaskTableNotes notesTable = new TaskTableNotes(BaseApplication.getInstance());
        editnoteList = notesTable.getDataByUserId(taskId, SharedPrefUtils.getUserDetailModel(this).getId());


        if (editnoteList.isEmpty())
            findViewById(R.id.headernotes).setVisibility(View.GONE);


        ArrayList<NotesModel> notemodellist = new ArrayList<>();

        for (int i = 0; i < editnoteList.size(); i++) {

            NotesModel model = new NotesModel();
            model.setMsg(editnoteList.get(i).getNote());
            //   String addedby = new TableAdult(BaseApplication.getInstance()).getAdults(editnoteList.get(i).getUser_id()).getFirst_name();
            // model.setAddedby(addedby);
            model.setId(editnoteList.get(i).getId());
            model.setUserid(editnoteList.get(i).getUser_id());
            model.setAppointmentid(editnoteList.get(i).getTask_id());

            notemodellist.add(model);
        }
//

        adapterNotsList = new AdapterEditNotes(this, notemodellist, true);
        notesListView.setAdapter(adapterNotsList);

        if (editappDetail != null) {
            taskName.setText(editappDetail.getTask_name());
            startTime.setText(getTime(editappDetail.getDue_date()).toLowerCase());
            startDate.setText(getDate(editappDetail.getDue_date()));
            //remainder.setText(editappDetail.getReminder() + " minutes before ");

            if (!StringUtils.isNullOrEmpty(editappDetail.getReminder())) {
                if (editappDetail.getReminder().equals("0")) {
                    remainder.setText(editappDetail.getReminder() + " minutes before ");
                } else {

                    if (Integer.parseInt(editappDetail.getReminder()) % 60 == 0) {
                        remainder.setText(Integer.parseInt(editappDetail.getReminder()) / 60 + " hours before ");
                    } else
                        remainder.setText(editappDetail.getReminder() + " minutes before ");

                }
            }


            reminderTime = editappDetail.getReminder();
            taskList.setText(editappDetail.getListName());

            taskDetail.setTask_list_id(editappDetail.getTask_list_id());


            if (editappDetail.getIs_recurring().equals("yes")) {

                StringBuilder stringBuilder = new StringBuilder();

                //stringBuilder.append("Every ");

                if (editappDetail.getRepeate().equals("Days")) {
                    stringBuilder.append(editappDetail.getRepeate_frequency());
                    // stringBuilder.append(" Until" + editappDetail.getRepeate_untill());
                    repeat.setText(stringBuilder.toString());
                    until.setText(editappDetail.getRepeate_untill());

                } else if (editappDetail.getRepeate().equals("Other")) {

                    if (editappDetail.getRepeate_frequency().equals("Days")) {

                        stringBuilder.append(editappDetail.getRepeate_num() + " - " + editappDetail.getRepeate_frequency());
                        // stringBuilder.append(" Until" + editappDetail.getRepeate_untill());
                        repeat.setText(stringBuilder.toString());
                        until.setText(editappDetail.getRepeate_untill());

                    } else if (editappDetail.getRepeate_frequency().equals("Weeks")) {
                        stringBuilder.append(editappDetail.getRepeate_num() + " - " + editappDetail.getRepeate_frequency());
                        // stringBuilder.append(" Until" + editappDetail.getRepeate_untill());
                        repeat.setText(stringBuilder.toString());
                        until.setText(editappDetail.getRepeate_untill());


                    } else if (editappDetail.getRepeate_frequency().equals("Months")) {
                        stringBuilder.append(editappDetail.getRepeate_num() + " - " + editappDetail.getRepeate_frequency());
                        // stringBuilder.append(" Until" + editappDetail.getRepeate_untill());
                        repeat.setText(stringBuilder.toString());
                        until.setText(editappDetail.getRepeate_untill());
                    }

                } else {
                    repeat.setText(editappDetail.getRepeate());
                    until.setText(editappDetail.getRepeate_untill());
                }

            } else if (editappDetail.getIs_recurring().equals("no")) {
                repeat.setText("No");
                until.setText("");
            } else {
                repeat.setText("");
                until.setText("");
            }
        }

        attendee.setText(attendeeString(editattendeeList));
        whoToRemind.setText(whoToRemindString(editwhoToRemindList));

        // recurring.setChecked(true);

        recurring.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (recurring.isChecked()) {

                    repeat.setVisibility(View.VISIBLE);
                    textRepeat.setVisibility(View.VISIBLE);
                    until.setVisibility(View.VISIBLE);
                    textUntil.setVisibility(View.VISIBLE);


                    RepeatDialogFragment repeatDialogFragment = new RepeatDialogFragment();
                    Bundle args = new Bundle();
                    args.putBoolean("edit", true);
                    args.putString("task", "task");
                    repeatDialogFragment.setArguments(args);
                    repeatDialogFragment.show(getFragmentManager(), "repeat");

                } else {
                    repeat.setVisibility(View.GONE);
                    textRepeat.setVisibility(View.GONE);
                    until.setVisibility(View.GONE);
                    textUntil.setVisibility(View.GONE);
                }
            }
        });

        taskDetail.setIs_recurring(editappDetail.getIs_recurring());
        taskDetail.setRepeate(editappDetail.getRepeate());
        taskDetail.setRepeate_untill(editappDetail.getRepeate_untill());
        taskDetail.setRepeate_num(editappDetail.getRepeate_num());
        taskDetail.setRepeate_frequency(editappDetail.getRepeate_frequency());

    }


    public String attendeeString(ArrayList<AttendeeModel> attendeeList) {

        StringBuilder attendeeS = new StringBuilder();
        Boolean flag = false;

        if (attendeeList.size() == 1) {

            attendeeS.append(attendeeList.get(0).getName());

        } else if (attendeeList.size() > 1) {

            for (int i = 0; i < attendeeList.size(); i++) {

                if (flag == true) {
                    attendeeS.append(", ");
                }
                attendeeS.append(attendeeList.get(i).getName());
                flag = true;
            }

        }

        return attendeeS.toString();
    }

    public String whoToRemindString(ArrayList<AttendeeModel> remindList) {

        StringBuilder attendeeS = new StringBuilder();
        Boolean flag = false;

        if (remindList.size() == 1) {

            attendeeS.append(remindList.get(0).getName());

        } else if (remindList.size() > 1) {

            for (int i = 0; i < remindList.size(); i++) {

                if (flag == true) {
                    attendeeS.append(", ");
                }
                attendeeS.append(remindList.get(i).getName());
                flag = true;
            }


        }

        return attendeeS.toString();
    }

    private String getTime(long timeStampStr) {

        try {
            DateFormat sdf = new SimpleDateFormat("hh:mm a");
            Date netDate = (new Date(timeStampStr));
            return sdf.format(netDate);
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // according to fragment change it
        getMenuInflater().inflate(R.menu.forgot_password, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            case R.id.save:

                // calling webservce
                if (ConnectivityUtils.isNetworkEnabled(this)) {
                    if (checkValidation()) {

                        if (!deleteImageList.isEmpty()) {
                            showProgressDialog(getString(R.string.please_wait));
                            DeleteImageModel list = new DeleteImageModel();
                            list.setDelete_files(deleteImageList);

                            DeleteImagesController controler = new DeleteImagesController(ActivityEditTask.this, ActivityEditTask.this);
                            controler.getData(AppConstants.DELETE_TASKS_IMAGE_REQUEST, list);
                        } else {
                            showProgressDialog(getString(R.string.please_wait));
                            TaskDataModel appList = getAlldata();
                            TaskController _controller = new TaskController(this, this);
                            _controller.getData(AppConstants.EDIT_TASK_REQUEST, appList);
                        }
                    }
                } else {
                    showToast(getString(R.string.error_network));
                }

                return true;


            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public void saveData(TaskDataModel model) {

        TableTaskData apponntmentTable = new TableTaskData((BaseApplication) getApplicationContext());
        TaskTableFile FileTable = new TaskTableFile((BaseApplication) getApplicationContext());
        TaskTableNotes NoteTable = new TaskTableNotes((BaseApplication) getApplicationContext());
        TaskTableWhoToRemind WhoToRemindTable = new TaskTableWhoToRemind((BaseApplication) getApplicationContext());
        TaskTableAttendee attendeeTable = new TaskTableAttendee((BaseApplication) getApplicationContext());


        try {


            for (TaskDataModel.TaskData data : model.getTask()) { // appoitment array loop

                // data is saving here

                apponntmentTable.insertData(data.getTask());

                for (TaskDataModel.Files filesList : data.getTaskFile()) {

                    FileTable.insertData(filesList);

                }

                for (TaskDataModel.Attendee attendeeList : data.getTaskAttendee()) {

                    attendeeTable.insertData(attendeeList);

                }
                for (TaskDataModel.Notes notesList : data.getTaskNote()) {

                    NoteTable.insertData(notesList);

                }
                for (TaskDataModel.WhoToRemind whotoRemind : data.getTaskWhomRemind()) {

                    WhoToRemindTable.insertData(whotoRemind);

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    protected void updateUi(Response response) {
        if (response == null) {
            removeProgressDialog();
            showToast(getResources().getString(R.string.server_error));
            return;
        }

        switch (response.getDataType()) {


            case AppConstants.IMAGE_UPLOAD_REQUEST:
                removeProgressDialog();
                if (response.getResponseObject() instanceof CommonResponse) {
                    CommonResponse responseModel = (CommonResponse) response
                            .getResponseObject();
                    if (responseModel.getResponseCode() != 200) {
                        showToast(getString(R.string.toast_response_error));
                        return;
                    } else {


                        if (!StringUtils.isNullOrEmpty(responseModel.getResult().getMessage())) {

                            TaskDataModel.Files model = new TaskDataModel.Files();
                            model.setUrl(responseModel.getResult().getMessage());
                            model.setFile_type("image");
                            model.setTask_id(taskId);
                            model.setFile_name(responseModel.getResult().getMessage().substring(responseModel.getResult().getMessage().lastIndexOf("/") + 1));

                            fileList.add(model);

                            imageList.add(model);
                            imgadapter.notifyDataSetChanged();
                        }
                        //setProfileImage(originalImage);
                        showToast("You have successfully uploaded image.");


                    }
                }
                break;

            case AppConstants.FILE_UPLOAD_REQ:
                removeProgressDialog();
                if (response.getResponseObject() instanceof CommonResponse) {
                    CommonResponse responseModel = (CommonResponse) response
                            .getResponseObject();
                    if (responseModel.getResponseCode() != 200) {
                        showToast(getString(R.string.toast_response_error));
                        return;
                    } else {


                        if (!StringUtils.isNullOrEmpty(responseModel.getResult().getMessage())) {


                            TaskDataModel.Files model = new TaskDataModel.Files();
                            model.setUrl(responseModel.getResult().getMessage());
                            model.setFile_type("text");
                            model.setTask_id(taskId);
                            model.setFile_name(responseModel.getResult().getMessage().substring(responseModel.getResult().getMessage().lastIndexOf("/") + 1));

                            fileList.add(model);

                            documentList.add(model);
                            //documentUrl.add(responseModel.getResult().getMessage());
                            documentadapter.notifyDataSetChanged();
                        }
                        showToast(getResources().getString(R.string.upload_file_successfully));
                    }
                }
                break;

            case AppConstants.CREATE_TASKLIST_REQUEST:

                try {
                    TaskListResponse responseData = (TaskListResponse) response.getResponseObject();
                    if (responseData.getResponseCode() == 200) {
                        // save in db
//                        saveData(responseData.getResult().getData());
                        saveListData(responseData.getResult().getData());
                        showToast(responseData.getResult().getMessage());

//                        Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
//                        setResult(1, intent);
//                        finish();


                    } else if (responseData.getResponseCode() == 400) {
                        showToast(responseData.getResult().getMessage());

                    }
                    removeProgressDialog();

                } catch (Exception e) {
                    removeProgressDialog();
                }

                break;


            case AppConstants.EDIT_TASK_REQUEST:
                try {
                    TaskResponse responseData = (TaskResponse) response.getResponseObject();

                    if (responseData.getResponseCode() == 200) {

                        // delete  in db
                        TableTaskData tableAppointment = new TableTaskData(BaseApplication.getInstance());
                        tableAppointment.deleteTask(taskId);

                        // get from attendee table
                        TaskTableAttendee attendeeTable = new TaskTableAttendee(BaseApplication.getInstance());
                        attendeeTable.deleteTask(taskId);


                        // get from whotoRemond table
                        TaskTableWhoToRemind whotoRemindTable = new TaskTableWhoToRemind(BaseApplication.getInstance());
                        whotoRemindTable.deleteTask(taskId);

                        // get from FILES

                        TaskTableFile fileTable = new TaskTableFile(BaseApplication.getInstance());
                        fileTable.deleteTask(taskId);
                        // note

                        TaskTableNotes notesTable = new TaskTableNotes(BaseApplication.getInstance());
                        notesTable.deleteTask(taskId);

                        // save in db
                        saveData(responseData.getResult().getData());
                        showToast(responseData.getResult().getMessage());

                        long startTimeMillis = responseData.getResult().getData().getTask().get(0).getTask().getDue_date();
                        String reminderBefore = responseData.getResult().getData().getTask().get(0).getTask().getReminder();
                        String recurring = responseData.getResult().getData().getTask().get(0).getTask().getIs_recurring();
                        String repeat = responseData.getResult().getData().getTask().get(0).getTask().getRepeate();
                        String repeatFrequency = responseData.getResult().getData().getTask().get(0).getTask().getRepeate_frequency();
                        String repeatNum = responseData.getResult().getData().getTask().get(0).getTask().getRepeate_num();
                        String repeatUntill = responseData.getResult().getData().getTask().get(0).getTask().getRepeate_untill();
                        int reminderId = responseData.getResult().getData().getTask().get(0).getTask().getId();
                        String taskName = responseData.getResult().getData().getTask().get(0).getTask().getTask_name();

                        boolean isReminder = false;
                        // craete reminder
                        for (TaskDataModel.WhoToRemind model : responseData.getResult().getData().getTask().get(0).getTaskWhomRemind()) {
                            if (model.getUser_id() == SharedPrefUtils.getUserDetailModel(this).getId()) {
                                isReminder = true;
                                break;

                            }
                        }
                        if (isReminder)
                            Reminder.with(this).info(Constants.REMINDER_TYPE_TASKS, taskName).startTime(startTimeMillis).setRepeatBehavior(repeat, repeatUntill, repeatFrequency, repeatNum).remindBefore(reminderBefore).setRecurring(recurring).create(reminderId);
                        else
                            Reminder.with(this).cancel(reminderId);

                        //Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
                        //setResult(1, intent);
                        finish();


                    } else if (responseData.getResponseCode() == 400) {
                        showToast(responseData.getResult().getMessage());

                    }
                    removeProgressDialog();

                } catch (Exception e) {
                    removeProgressDialog();
                }
                break;

            case AppConstants.DELETE_TASKS_IMAGE_REQUEST:
                removeProgressDialog();
                if (response.getResponseObject() instanceof CommonResponse) {
                    CommonResponse responseModel = (CommonResponse) response
                            .getResponseObject();
                    if (responseModel.getResponseCode() == 200) {

                        TaskDataModel appList = getAlldata();
                        TaskController _controller = new TaskController(this, this);
                        _controller.getData(AppConstants.EDIT_TASK_REQUEST, appList);

                    } else {
                        showToast(responseModel.getResult().getMessage());
                        removeProgressDialog();

                    }
                }
                break;
        }
    }


    private void openGallery() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, Constants.OPEN_GALLERY);
    }

    public Intent createGetContentIntent() {
        // Implicitly allow the user to select a particular kind of data
        final Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        // The MIME data type filter
        intent.setType("text/*");
        // Only return URIs that can be opened with ContentResolver
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        return intent;
    }


    // .docx, .xlxs, .ppt, .txt, .pdf

    private void openDocuments() {
//        Intent getContentIntent = createGetContentIntent();
//        Intent intent = Intent.createChooser(getContentIntent,
//                "Select a file");
//        startActivityForResult(intent,  Constants.OPEN_DOCUMENTS);

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        //intent.setType("*/*");
        intent.setType("text/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(
                    Intent.createChooser(intent, "Select a File to Upload"),
                    Constants.OPEN_DOCUMENTS);
        } catch (android.content.ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
            Toast.makeText(this, "Please install a File Manager.",
                    Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case Constants.OPEN_GALLERY:
                if (resultCode == Activity.RESULT_OK) {
                    try {
                        Uri selectedImage = data.getData();
//                        String[] filePathColumn = {MediaStore.Images.Media.DATA};
//
//                        Cursor cursor = this.getContentResolver().query(
//                                selectedImage, filePathColumn, null, null, null);
//                        cursor.moveToFirst();
//
//                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
//                        String filePath = cursor.getString(columnIndex);
//                        cursor.close();
//                        Log.e("File", "filePath: " + filePath);

                        String filePath = DocumentUtils.getPath(this, selectedImage);
                        file_name = filePath.substring(filePath.lastIndexOf("/") + 1);

                        File file = new File(new URI("file://"
                                + filePath.replaceAll(" ", "%20")));
                        int maxImageSize = BitmapUtils.getMaxSize(this);
                        Bitmap sourceBitmap = BitmapUtils.getScaledBitmap(file,
                                maxImageSize);

                        ExifInterface exif = new ExifInterface(file.getPath());
                        int orientation = exif.getAttributeInt(
                                ExifInterface.TAG_ORIENTATION,
                                ExifInterface.ORIENTATION_NORMAL);
                        Matrix matrix = new Matrix();
                        switch (orientation) {
                            case ExifInterface.ORIENTATION_ROTATE_90:
                                matrix.postRotate(90);
                                break;
                            case ExifInterface.ORIENTATION_ROTATE_180:
                                matrix.postRotate(180);
                                break;
                            case ExifInterface.ORIENTATION_ROTATE_270:
                                matrix.postRotate(270);
                                break;
                        }

                        Bitmap originalImage = Bitmap.createBitmap(sourceBitmap, 0, 0,
                                sourceBitmap.getWidth(), sourceBitmap.getHeight(),
                                matrix, true);
                        sendUploadProfileImageRequest(originalImage);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;


            case Constants.OPEN_DOCUMENTS:
                if (resultCode == Activity.RESULT_OK) {
                    try {
                        Uri selectedImage = data.getData();
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};


                        String path = selectedImage.getPath().toString();

                        Log.e("File", "filePath: " + path);

                        file_name = path.substring(path.lastIndexOf("/") + 1);

                        String extension = file_name.substring(file_name.lastIndexOf(".") + 1);

                        //docx, .xlxs, .ppt, .txt, .pdf
                        if (extension.equalsIgnoreCase("txt") || extension.equalsIgnoreCase("docx") || extension.equalsIgnoreCase("doc") || extension.equalsIgnoreCase("pdf") || extension.equalsIgnoreCase("ppt") || extension.equalsIgnoreCase("xls") || extension.equalsIgnoreCase("xlsx")) {
                            InputStream inputStream = null;//You can get an inputStream using any IO API
                            inputStream = new FileInputStream(path);
                            byte[] buffer = new byte[8192];
                            int bytesRead;
                            ByteArrayOutputStream output = new ByteArrayOutputStream();
                            Base64OutputStream output64 = new Base64OutputStream(output, Base64.DEFAULT);
                            try {
                                while ((bytesRead = inputStream.read(buffer)) != -1) {
                                    output64.write(buffer, 0, bytesRead);
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            output64.close();


                            sendUploadFileeRequest(output);
                        } else {
                            ToastUtils.showToast(this, getResources().getString(R.string.file_format));
                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    // for uploading image
    public void sendUploadProfileImageRequest(Bitmap originalImage) {
        showProgressDialog(getResources().getString(R.string.please_wait));
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        originalImage.compress(Bitmap.CompressFormat.PNG, 100, bao);
        byte[] ba = bao.toByteArray();
        String imageString = Base64.encodeToString(ba, Base64.DEFAULT);
        file_name = file_name.substring(0, (file_name.lastIndexOf(".")));
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("extension", "image/png");
            jsonObject.put("name", file_name);
            jsonObject.put("size", ba.length);
            jsonObject.put("byteCode", imageString);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        UserTable userTable = new UserTable((BaseApplication) this.getApplication());
        UserModel userModel = userTable.getAllUserData();

        JSONArray jsonArray = new JSONArray();
        jsonArray.put(jsonObject);

        try {
            ImageUploadRequest requestData = new ImageUploadRequest();
            requestData.setImage(jsonArray.toString());
            requestData.setType(AppConstants.IMAGE_TYPE_TASK);
//        requestData.setUser_id("" + userModel.getUser().getId());
            //requestData.setSessionId("" + userModel.getUser().getSessionId());
            // requestData.setProfileId("" + userModel.getUser().getProfileId());

            ImageUploadController controller = new ImageUploadController(this, this);
            controller.getData(AppConstants.IMAGE_UPLOAD_REQUEST, requestData);
        } catch (Exception e) {
            removeProgressDialog();
            e.printStackTrace();
        }


    }


    // for uploading file
    public void sendUploadFileeRequest(ByteArrayOutputStream output) {
        showProgressDialog(getResources().getString(R.string.please_wait));

        JSONObject jsonObject = new JSONObject();

        String extension = file_name.substring(file_name.lastIndexOf("."));
        file_name = file_name.substring(0, (file_name.lastIndexOf(".")));
        try {
            jsonObject.put("extension", extension);
            jsonObject.put("name", file_name);
            jsonObject.put("size", output.toByteArray().length);
            jsonObject.put("byteCode", output.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        UserTable userTable = new UserTable((BaseApplication) this.getApplication());
        UserModel userModel = userTable.getAllUserData();

        JSONArray jsonArray = new JSONArray();
        jsonArray.put(jsonObject);

        try {
            ImageUploadRequest requestData = new ImageUploadRequest();
            requestData.setImage(jsonArray.toString());
//        requestData.setUser_id("" + userModel.getUser().getId());
            //requestData.setSessionId("" + userModel.getUser().getSessionId());
            // requestData.setProfileId("" + userModel.getUser().getProfileId());

            ImageUploadController controller = new ImageUploadController(this, this);
            controller.getData(AppConstants.FILE_UPLOAD_REQ, requestData);
        } catch (Exception e) {
            removeProgressDialog();
            e.printStackTrace();
        }


    }

    public boolean checkTodayDate() {

        try {
            Calendar c = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("dd MMMM yyyy");
            String currentDate = df.format(c.getTime());


            DateFormat formatter = new SimpleDateFormat("dd MMMM yyyy, EEEE");
            Date dateobj = (Date) formatter.parse(startDate.getText().toString());
            String startDate = df.format(dateobj);


            if (startDate.equalsIgnoreCase(currentDate))
                return true;

        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        return false;
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.task_attendee:

                boolean all = false;

                ArrayList<Integer> idlist = new ArrayList<>();

                for (TaskDataModel.Attendee model : attendeeDataList) {

                    idlist.add(model.getUk_id());

                }

                if (attendee.getText().toString().trim().equalsIgnoreCase("All")) {
                    all = true;
                }
                AttendeeDialogFragment dialogFragment = new AttendeeDialogFragment();

                Bundle args = new Bundle();
                args.putIntegerArrayList("chkValues", idlist);
                args.putBoolean("edit", true);
                args.putBoolean("All", all);
                args.putString("iftask", "iftask");
                dialogFragment.setArguments(args);


                dialogFragment.setTargetFragment(dialogFragment, 2);
                dialogFragment.show(getFragmentManager(), "attendee");

                break;

            case R.id.task_whotoremind:

                all = false;

                idlist = new ArrayList<>();

                for (TaskDataModel.WhoToRemind model : whoToRemindList) {

                    idlist.add(model.getUser_id());

                }
                if (whoToRemind.getText().toString().trim().equalsIgnoreCase("All")) {
                    all = true;
                }
                WhoToRemindDialogFragment dialogFragment1 = new WhoToRemindDialogFragment();

                args = new Bundle();
                args.putIntegerArrayList("chkValues", idlist);
                args.putBoolean("All", all);
                args.putBoolean("edit", true);
                args.putString("iftask", "iftask");
                args.putString("dialogTitle","Who to remind");
                dialogFragment1.setArguments(args);

                dialogFragment1.setTargetFragment(dialogFragment1, 2);
                dialogFragment1.show(getFragmentManager(), "whotoremind");

                break;

            case R.id.duedate:
//                flag = true;
//                datePicket((TextView) view);

                DueDateFagment dueDateFagment = new DueDateFagment();

                Bundle ards1 = new Bundle();
                ards1.putString("ifedit", "edit");
                dueDateFagment.setArguments(ards1);
                dueDateFagment.show(getFragmentManager(), "duedate");

                break;

            case R.id.due_time:

//                flag = true;
//
                timePicker((TextView) view);
                break;

            case R.id.task_remainder:

                try {
                    RemainderDialogFragment remainderDialog = new RemainderDialogFragment();

                    args = new Bundle();
                    args.putBoolean("edit", true);
                    args.putString("task", "task");
                    args.putBoolean("is_recurring", recurring.isChecked());
                    args.putBoolean("todayDate", checkTodayDate());
                    args.putLong("time", convertTimeStamp(startDate.getText(), startTime.getText()));
                    remainderDialog.setArguments(args);
                    remainderDialog.show(getFragmentManager(), "remainder");
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;

            case R.id.task_repeat:

                RepeatDialogFragment repeatDialogFragment = new RepeatDialogFragment();

                args = new Bundle();
                args.putBoolean("edit", true);
                args.putString("task", "task");
                repeatDialogFragment.setArguments(args);

                repeatDialogFragment.show(getFragmentManager(), "repeat");

                break;

            case R.id.task_until:

                RepeatUntilFragment repeatUntilFragment = new RepeatUntilFragment();

                args = new Bundle();
                args.putBoolean("edit", true);
                args.putString("task", "task");
                repeatUntilFragment.setArguments(args);

                repeatUntilFragment.show(getFragmentManager(), "repeat_until");

                break;
            case R.id.add_images:

                openGallery();
                break;

            case R.id.add_attachments:

                openDocuments();
                break;

            case R.id.image:

                Object object = view.getTag();
                if (object != null && object instanceof Integer) {
                    Integer _position = (Integer) view.getTag();
                    Intent intent = new Intent(ActivityEditTask.this, ActivityViewPager.class);
                    intent.putExtra("imagelist", imageList);
                    intent.putExtra("position", _position);
                    intent.putExtra("isfrmAppointment", false);
                    startActivity(intent);

                }
                break;
            case R.id.txtfile:

                object = view.getTag();
                if (object != null && object instanceof Integer) {
                    Integer _position = (Integer) view.getTag();
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(documentList.get(_position).getUrl()));
                    startActivity(i);

                }
                break;

            case R.id.task_list:

                FragmentTaskList fragmentTaskList = new FragmentTaskList();
                args = new Bundle();
                args.putString("ifedit", "edit");
                fragmentTaskList.setArguments(args);
                fragmentTaskList.show(getFragmentManager(), "tasklist");

                break;
            case R.id.delete_image:

                object = view.getTag();
                if (object != null && object instanceof Integer) {
                    Integer _position = (Integer) view.getTag();
                    deleteImagePostion = _position;
                    isImage = true;
                    // check whether existing image or new upload by id
                    if (imageList.get(_position).getId() > 0) {
                        // existing image
//                        TaskDataModel.Files files = new TaskDataModel.Files();
//                        files.setTask_id(taskId);
//                        files.setId(imageList.get(_position).getId());
                        DeleteImageModel.DeleteImage model = new DeleteImageModel().new DeleteImage();
                        model.setFile_id(imageList.get(_position).getId());
                        deleteImageList.add(model);

                        imageList.remove(deleteImagePostion);
                        imgadapter.notifyDataSetChanged();

                        // deleteImageList.add(files);


//                        showProgressDialog(getString(R.string.please_wait));
//                        DeleteImagesController controler = new DeleteImagesController(ActivityEditTask.this, ActivityEditTask.this);
//                        controler.getData(AppConstants.DELETE_TASKS_IMAGE_REQUEST, files);

                    } else {
                        // new upload image
                        imageList.remove(deleteImagePostion);
                        imgadapter.notifyDataSetChanged();
                    }
                }
                break;

            case R.id.delete_text:

                object = view.getTag();
                if (object != null && object instanceof Integer) {
                    Integer _position = (Integer) view.getTag();
                    deleteImagePostion = _position;
                    isImage = false;

                    // check whether existing image or new upload by id
                    if (documentList.get(_position).getId() > 0) {
                        // existing image
//                        TaskDataModel.Files files = new TaskDataModel.Files();
//                        files.setTask_id(taskId);
//                        files.setId(documentList.get(_position).getId());

                        DeleteImageModel.DeleteImage model = new DeleteImageModel().new DeleteImage();
                        model.setFile_id(documentList.get(_position).getId());
                        deleteImageList.add(model);

                        documentList.remove(deleteImagePostion);
                        documentadapter.notifyDataSetChanged();

                        //    deleteImageList.add(files);

//                        showProgressDialog(getString(R.string.please_wait));
//                        DeleteImagesController controler = new DeleteImagesController(ActivityEditTask.this, ActivityEditTask.this);
//                        controler.getData(AppConstants.DELETE_TASKS_IMAGE_REQUEST, files);

                    } else {
                        // new upload text
                        documentList.remove(deleteImagePostion);
                        documentadapter.notifyDataSetChanged();
                    }
                }
                break;
        }
    }


    public void timePicker(final TextView Time) {
        final Calendar c = Calendar.getInstance();
        int mHour = c.get(Calendar.HOUR_OF_DAY);
        int mMinute = c.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        TimePickerDialog tpd = new TimePickerDialog(ActivityEditTask.this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {
                        // Display Selected time in textbox

                        int hour = hourOfDay;
                        int minutes = minute;
                        String timeSet = "";
                        if (hour > 12) {
                            hour -= 12;
                            timeSet = "PM";
                        } else if (hour == 0) {
                            hour += 12;
                            timeSet = "AM";
                        } else if (hour == 12)
                            timeSet = "PM";
                        else
                            timeSet = "AM";

                        String min = "";
                        if (minutes < 10)
                            min = "0" + minutes;
                        else
                            min = String.valueOf(minutes);

                        // Append in a StringBuilder
                        String aTime = new StringBuilder().append(hour).append(':')
                                .append(min).append(" ").append(timeSet).toString();


//                        startTime.setText(hourOfDay + ":" + minute);
                        Time.setText(aTime.toLowerCase());

                        Log.d("time ", (String) Time.getText());

                    }
                }, mHour, mMinute, false);
        tpd.show();
    }


    public void setRemainder(String time, String min) {
        remainder.setText(time + " before");
        reminderTime = min;
    }


    public void setAttendee(ArrayList<AttendeeModel> attendeeList) {

        attendeeDataList = new ArrayList<>();
        boolean flag = false;
        boolean flag1 = true;

        for (int i = 0; i < attendeeList.size(); i++) {

            if (i == 0) {
                if (attendeeList.get(i).getCheck() == true) {
                    attendee.setText("All");
                    flag = true;
                }

            } else {
                if (attendeeList.get(i).getCheck() == true) {

                    TaskDataModel.Attendee attendeeModel = new TaskDataModel().new Attendee();
                    attendeeModel.setUk_id(attendeeList.get(i).getId());
                    attendeeModel.setUk_type(attendeeList.get(i).getType());
                    attendeeDataList.add(attendeeModel);

                    if (!flag) {

                        if (flag1) {
                            attendee.setText(attendeeList.get(i).getName().toString().trim());
                            flag1 = false;
                        } else {
                            String txt = attendee.getText().toString();
                            attendee.setText(txt + ", " + attendeeList.get(i).getName().toString().trim());
                        }
                    }


                }

            }
            setWhoToRemind(attendeeList);

        }


    }

    public void setWhoToRemind(ArrayList<AttendeeModel> attendeeList) {

        whoToRemindList = new ArrayList<>();
        boolean flag = false;
        boolean flag1 = true;

        for (int i = 0; i < attendeeList.size(); i++) {

            if (i == 0) {
                if (attendeeList.get(i).getCheck() == true) {
                    whoToRemind.setText("All");
                    flag = true;

                }


            } else {
                if (attendeeList.get(i).getCheck() == true) {

                    if (attendeeList.get(i).getType().equalsIgnoreCase("user")) {

                        TaskDataModel.WhoToRemind whoModel = new TaskDataModel().new WhoToRemind();
                        whoModel.setUser_id(attendeeList.get(i).getId());
                        whoToRemindList.add(whoModel);

                        if (!flag) {

                            if (flag1) {
                                whoToRemind.setText(attendeeList.get(i).getName());
                                flag1 = false;
                            } else {
                                String txt = whoToRemind.getText().toString();
                                whoToRemind.setText(txt + ", " + attendeeList.get(i).getName());
                            }
                        }


                    }
                }
            }


        }


    }


    public long convertTimeStamp(CharSequence date, CharSequence time) throws ParseException {


        SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM yyyy, EEEE hh:mm a");

        String temp = date + " " + time;
        Date tempDate = formatter.parse(temp);

        java.sql.Timestamp timestamp = new java.sql.Timestamp(tempDate.getTime());

        Log.d("TimeStamp", String.valueOf(timestamp.getTime()));

        long time_stamp = timestamp.getTime();
        return time_stamp;
    }

    public TaskDataModel getAlldata() {


        try {
            startTimeStamp = convertTimeStamp(startDate.getText(), startTime.getText());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        taskDetail.setId(taskId);
        taskDetail.setOffline_id(1);
        taskDetail.setFamily_id(SharedPrefUtils.getUserDetailModel(this).getFamily_id());
        taskDetail.setTask_name(String.valueOf(taskName.getText().toString().trim()));
        taskDetail.setReminder("" + reminderTime);
        taskDetail.setDue_date(startTimeStamp);
//        taskDetail.setEndtime(endTimeStamp);

        if (taskDetail.getRepeate().equals("No Repeat")) {
            taskDetail.setIs_recurring("no");
            taskDetail.setRepeate("");
            taskDetail.setRepeate_num("");
            taskDetail.setRepeate_untill("");
            taskDetail.setRepeate_frequency("");
        }

        if (recurring.isChecked()) {
            taskDetail.setIs_recurring("yes");
        } else {
            taskDetail.setIs_recurring("no");
            taskDetail.setRepeate("");
            taskDetail.setRepeate_num("");
            taskDetail.setRepeate_untill("");
            taskDetail.setRepeate_frequency("");
        }

//        //files
//        ArrayList<TaskDataModel.Files> fileDataList = new ArrayList<>();
//
//        if (!fileList.isEmpty()) {
//            for (TaskDataModel.Files filesmodel : fileList) {
//                TaskDataModel.Files files = new TaskDataModel().new Files();
//                files.setFile_name(filesmodel.getFile_name());
//                files.setFile_type(filesmodel.getFile_type());
//                files.setUrl(filesmodel.getUrl());
//                fileDataList.add(files);
//            }
//        }
        // file edit data
        imageList.addAll(documentList);


        // notes
        NoteList = new ArrayList<>();
        ArrayList<NotesModel> notemodel = adapterNotsList.getDatalist();

        for (NotesModel model : notemodel) {
            TaskDataModel.Notes notes = new TaskDataModel().new Notes();
            notes.setId(model.getId());
            notes.setTask_id(model.getAppointmentid());
            notes.setNote(model.getMsg());
            model.setUserid(model.getUserid());

            NoteList.add(notes);
        }

        ArrayList<TaskDataModel.TaskData> appointmentList = new ArrayList<>();
        TaskDataModel.TaskData taskData = new TaskDataModel().new TaskData();
        taskData.setTask(taskDetail);
        taskData.setTaskAttendee(attendeeDataList);
        taskData.setTaskFile(imageList);
        taskData.setTaskWhomRemind(whoToRemindList);
        taskData.setTaskNote(NoteList);

        appointmentList.add(taskData);

        TaskDataModel model = new TaskDataModel();
        model.setTask(appointmentList);

        return model;
    }

    public boolean checkValidDate() {
        boolean result = true;

        try {
            long selectedTimestamp = convertTimeStamp(startDate.getText().toString(), startTime.getText().toString());
            long currentime = (System.currentTimeMillis());
            if ((currentime) > selectedTimestamp)
                result = false;
        } catch (Exception e) {
            e.printStackTrace();
        }


        return result;
    }

    public boolean checkValidation() {
        boolean result = true;


        if (taskName.getText().toString().trim().equals("")) {
            ToastUtils.showToast(this, Constants.ENTER_NAME);
            result = false;
        } else if (attendeeDataList.size() == 0) {
            attendee.setFocusableInTouchMode(true);
            attendee.requestFocus();
            attendee.setError(Constants.VALID_ATTENDEE_WHO);
            result = false;
        } else if (!checkValidDate()) {
            ToastUtils.showToast(this, Constants.TASK_VALID_DATE);
            result = false;
        } else if (recurring.isChecked()) {
            if (repeat.getText().toString().trim().equalsIgnoreCase("")) {
                repeat.setFocusableInTouchMode(true);
                repeat.requestFocus();
                repeat.setError("Please select repeat");
                result = false;
            } else if (until.getText().toString().trim().equalsIgnoreCase("")) {
                until.setFocusableInTouchMode(true);
                until.requestFocus();
                until.setError("Please select until");
                result = false;
            }
        }

        return result;
    }


    public void setRepeat(String select, String repeat, String value) {

        this.repeat.setError(null);
        if (repeat.equals("0")) {

            this.repeat.setText("Days" + " - " + String.valueOf(Integer.parseInt(value) + 1));
            this.select = select;
            this.mode = "Days";
            this.value = String.valueOf(Integer.parseInt(value) + 1);

            taskDetail.setRepeate(this.select);
            taskDetail.setRepeate_frequency(this.mode);
            taskDetail.setRepeate_num(this.value);


        } else if (repeat.equals("1")) {

            this.repeat.setText("Weeks" + " - " + String.valueOf(Integer.parseInt(value) + 1));
            this.select = select;
            this.mode = "Weeks";
            this.value = String.valueOf(Integer.parseInt(value) + 1);

            taskDetail.setRepeate(this.select);
            taskDetail.setRepeate_frequency(this.mode);
            taskDetail.setRepeate_num(this.value);


        } else if (repeat.equals("2")) {

            this.repeat.setText("Months" + " - " + String.valueOf(Integer.parseInt(value) + 1));
            this.select = select;
            this.mode = "Months";
            this.value = String.valueOf(Integer.parseInt(value) + 1);

            taskDetail.setRepeate(this.select);
            taskDetail.setRepeate_frequency(this.mode);
            taskDetail.setRepeate_num(this.value);

        } else if (repeat.equals("Days")) {

            this.repeat.setText(repeat + " - " + value);
            this.mode = "Days";
            this.value = value;

            taskDetail.setRepeate(this.mode);
            taskDetail.setRepeate_frequency(this.value);
            taskDetail.setRepeate_num("");

        } else {
            this.repeat.setText(repeat);
            this.mode = repeat;
            this.value = "";

            taskDetail.setRepeate(repeat);
            taskDetail.setRepeate_frequency("");
            taskDetail.setRepeate_num("");

        }

    }

    public void setRepeatUntil(String until, String value) {

        this.until.setError(null);
        this.until.setText(until);
        taskDetail.setRepeate_untill(until);
    }


    public void setTaskTime(String date) {
        startDate.setText(date);

    }

    public void selectList(String name, int id) {

        taskDetail.setTask_list_id(id);
        taskList.setText(name);

    }

    public void addTaskList(String name) {

        TaskListModel taskListModel = new TaskListModel();

        if (name != "") {

            taskListModel.setList_name(name);

            if (ConnectivityUtils.isNetworkEnabled(this)) {
                showProgressDialog(getString(R.string.please_wait));

                TaskListController _controller = new TaskListController(this, this);
                _controller.getData(AppConstants.CREATE_TASKLIST_REQUEST, taskListModel);
            } else {
                showToast(getString(R.string.error_network));
            }
        }
    }

    public void saveListData(ArrayList<TaskListResponse.AllList> model) {

        TableTaskList taskTable = new TableTaskList((BaseApplication) getApplicationContext());
//        for (TaskListResponse.AllList data : model) { // appoitment array loop
//
//            // data is saving here
//
//            taskTable.insertData(data.getTaskList());
//        }
        taskTable.insertData((model.get(model.size() - 1).getTaskList()));

        this.listId = (model.get(model.size() - 1).getTaskList()).getId();
        taskList.setText((model.get(model.size() - 1).getTaskList()).getList_name());
        taskDetail.setTask_list_id(listId);
    }

}
