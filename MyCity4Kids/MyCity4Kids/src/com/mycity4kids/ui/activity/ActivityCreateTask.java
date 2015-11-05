package com.mycity4kids.ui.activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Base64OutputStream;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
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
import com.mycity4kids.models.forgot.CommonResponse;
import com.mycity4kids.models.user.ImageUploadRequest;
import com.mycity4kids.models.user.UserModel;
import com.mycity4kids.newmodels.AttendeeModel;
import com.mycity4kids.newmodels.DeleteImageModel;
import com.mycity4kids.newmodels.TaskDataModel;
import com.mycity4kids.newmodels.TaskListModel;
import com.mycity4kids.newmodels.TaskListResponse;
import com.mycity4kids.newmodels.TaskResponse;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.reminders.Reminder;
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


public class ActivityCreateTask extends BaseActivity implements View.OnClickListener {

    TextView attendee, remainder, textRepeat, repeat, textUntil, until, whoToRemind, taskList, dueDate, dueTime;
    long startTimeStamp;
    //ImageView save;
    EditText taskName, notes;
    private Toolbar mToolbar;
    CheckBox recurring;

    int listId = 0;
    Boolean flag = false;
    String value = "";
    String mode = "";
    String select = "";
    private int deleteImagePostion;
    public ArrayList<TaskDataModel.Attendee> attendeeDataList;
    public ArrayList<TaskDataModel.WhoToRemind> whoToRemindList;
    public ArrayList<TaskDataModel.Files> fileList;
    private String reminderTime;
    private String repeat_num = "";
    private String repeat_frequency = "";
    private CustomListView listviewAttachment;
    private CustomListView listviewImages;
    private ArrayList<TaskDataModel.Files> imageList;
    private ArrayList<TaskDataModel.Files> documentList;
    private AddImagesAdapter imgadapter;
    private AddDocumentAdapter documentadapter;
    private String file_name = "";
    private ArrayList<String> documentUrl;
    private boolean imageUploading = false;
    private float density;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aa_create_task);
        density = getResources().getDisplayMetrics().density;

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Add Task");

        //save = (ImageView) findViewById(R.id.save);
        taskName = (EditText) findViewById(R.id.task_name);
        notes = (EditText) findViewById(R.id.task_notes);
        attendee = (TextView) findViewById(R.id.task_attendee);
        whoToRemind = (TextView) findViewById(R.id.task_whotoremind);
        remainder = (TextView) findViewById(R.id.task_remainder);
        recurring = (CheckBox) findViewById(R.id.recurring_task);
        textRepeat = (TextView) findViewById(R.id.repeat_text);
        textUntil = (TextView) findViewById(R.id.until_text);
        repeat = (TextView) findViewById(R.id.task_repeat);
        until = (TextView) findViewById(R.id.task_until);
        taskList = (TextView) findViewById(R.id.task_list);
        dueDate = (TextView) findViewById(R.id.due_date);
        dueTime = (TextView) findViewById(R.id.due_time);
        dueTime.setOnClickListener(this);

        listviewAttachment = (CustomListView) findViewById(R.id.list_attachment);
        listviewImages = (CustomListView) findViewById(R.id.list_images);

        listviewImages.isExpanded();
        listviewAttachment.isExpanded();

        attendee.setKeyListener(null);
        dueDate.setKeyListener(null);
        //save.setOnClickListener(this);
        attendee.setOnClickListener(this);
        whoToRemind.setOnClickListener(this);
        remainder.setOnClickListener(this);
        dueDate.setOnClickListener(this);

        findViewById(R.id.add_images).setOnClickListener(this);
        findViewById(R.id.add_attachments).setOnClickListener(this);


        attendeeDataList = new ArrayList<>();
        whoToRemindList = new ArrayList<>();
        fileList = new ArrayList<>();
        imageList = new ArrayList<>();
        documentList = new ArrayList<>();
        documentUrl = new ArrayList<>();
        // adapetrs for attachements and images

        imgadapter = new AddImagesAdapter(this, null, imageList, this, false, true);
        listviewImages.setAdapter(imgadapter);

        documentadapter = new AddDocumentAdapter(this, null, documentList, this, false, true);
        listviewAttachment.setAdapter(documentadapter);

        Calendar cal = Calendar.getInstance();

        SimpleDateFormat format = new SimpleDateFormat("dd MMMM yyyy, EEEE");

        dueDate.setText(format.format(cal.getTime()));

        SimpleDateFormat format1 = new SimpleDateFormat("hh:mm a");
        cal.add(Calendar.MINUTE, 30);
        dueTime.setText(format1.format(cal.getTime()).toLowerCase());

        //dueTime.setText("09:00 AM");

        repeat.setVisibility(View.GONE);
        textRepeat.setVisibility(View.GONE);
        until.setVisibility(View.GONE);
        textUntil.setVisibility(View.GONE);

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
                    args.putBoolean("edit", false);
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

        repeat.setOnClickListener(this);
        until.setOnClickListener(this);
        taskList.setOnClickListener(this);

//        // set attendee by default
//
//        TableAdult tableAdult = new TableAdult(BaseApplication.getInstance());
//        ArrayList<UserInfo> userInfos = (ArrayList<UserInfo>) tableAdult.getAllAdults();
//
//        ArrayList<AttendeeModel> attendeeList = new ArrayList<AttendeeModel>();
//
//        for (int i = 0; i < userInfos.size(); i++) {
//            attendeeList.add(new AttendeeModel(userInfos.get(i).getId(), "user", userInfos.get(i).getFirst_name(), userInfos.get(i).getColor_code(), true));
//        }
//        setDefaultAttendee(attendeeList);
//        setWhoToRemind(attendeeList);

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


                AlertDialog.Builder dialog = new AlertDialog.Builder(this);

                dialog.setMessage(getResources().getString(R.string.exit_task)).setNegativeButton(R.string.new_yes
                        , new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.cancel();
                        finish();


                    }
                }).setPositiveButton(R.string.new_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                        dialog.cancel();


                    }
                }).setIcon(android.R.drawable.ic_dialog_alert);

                AlertDialog alert11 = dialog.create();
                alert11.show();

                alert11.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.home_light_blue));
                alert11.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.canceltxt_color));

                return true;

            case R.id.save:

                // calling webservce

                TaskDataModel appList = getAlldata();

                if (checkValidation()) {

                    if (ConnectivityUtils.isNetworkEnabled(this)) {
                        showProgressDialog(getString(R.string.please_wait));

                        TaskController _controller = new TaskController(this, this);
                        _controller.getData(AppConstants.CREATE_TASK_REQUEST, appList);
                    } else {
                        showToast(getString(R.string.error_network));
                    }
                }

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public void saveData(TaskDataModel model) {

        TableTaskData taskTable = new TableTaskData((BaseApplication) getApplicationContext());
        TaskTableFile FileTable = new TaskTableFile((BaseApplication) getApplicationContext());
        TaskTableNotes NoteTable = new TaskTableNotes((BaseApplication) getApplicationContext());
        TaskTableWhoToRemind WhoToRemindTable = new TaskTableWhoToRemind((BaseApplication) getApplicationContext());
        TaskTableAttendee attendeeTable = new TaskTableAttendee((BaseApplication) getApplicationContext());

        try {

            for (TaskDataModel.TaskData data : model.getTask()) { // appoitment array loop

                // data is saving here

                taskTable.insertData(data.getTask());

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
            showToast(getString(R.string.server_error));
            return;
        }

        switch (response.getDataType()) {

            case AppConstants.IMAGE_UPLOAD_REQUEST:
                removeProgressDialog();
                imageUploading = false;
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
                            model.setFile_name(responseModel.getResult().getMessage().substring(responseModel.getResult().getMessage().lastIndexOf("/") + 1));

                            fileList.add(model);

                            imageList.add(model);
                            imgadapter.notifyDataSetChanged();
                        }
                        //setProfileImage(originalImage);
                        showToast("You have successfully uploaded an image.");


                    }
                }
                break;

            case AppConstants.FILE_UPLOAD_REQ_TASK:
                removeProgressDialog();
                imageUploading = false;
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
                            model.setFile_name(responseModel.getResult().getMessage().substring(responseModel.getResult().getMessage().lastIndexOf("/") + 1));

                            fileList.add(model);

                            documentList.add(model);
                            documentUrl.add(responseModel.getResult().getMessage());
                            documentadapter.notifyDataSetChanged();
                        }

                        showToast(getResources().getString(R.string.upload_file_successfully));

                    }
                }
                break;


            case AppConstants.CREATE_TASK_REQUEST:

                try {
                    TaskResponse responseData = (TaskResponse) response.getResponseObject();
                    if (responseData.getResponseCode() == 200) {
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


                        for (TaskDataModel.WhoToRemind model : responseData.getResult().getData().getTask().get(0).getTaskWhomRemind()) {
                            if (model.getUser_id() == SharedPrefUtils.getUserDetailModel(this).getId()) {
                                Reminder.with(this).info(Constants.REMINDER_TYPE_TASKS, taskName).startTime(startTimeMillis).setRepeatBehavior(repeat, repeatUntill, repeatFrequency, repeatNum).remindBefore(reminderBefore).setRecurring(recurring).create(reminderId);
                                break;

                            }

                        }


                        SharedPrefUtils.setHomeCheckFlag(this, true);

                        Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
                        setResult(RESULT_OK, intent);
                        finish();


                    } else if (responseData.getResponseCode() == 400) {
                        showToast(responseData.getResult().getMessage());

                    }
                    removeProgressDialog();

                } catch (Exception e) {
                    removeProgressDialog();
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

        }
    }

//    @Override
//    public void onBackPressed() {
//
//        if (imageUploading) {
//            AlertDialog.Builder dialog1 = new AlertDialog.Builder(this);
//
//            dialog1.setTitle("").setMessage("Are you sure want to cancel the image processing").setPositiveButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
//                public void onClick(DialogInterface dialog, int which) {
//
//                    dialog.cancel();
//
//                }
//            }).setNegativeButton("Yes", new DialogInterface.OnClickListener() {
//                public void onClick(DialogInterface dialog, int which) {
//                    // perform tasks
//
//                    dialog.cancel();
//                    removeProgressDialog();
//                }
//            }).setIcon(android.R.drawable.ic_dialog_alert).show();
//
//        }
//
//        else
//        {
//            super.onBackPressed();
//        }
//    }

    public boolean checkTodayDate() {

        try {
            Calendar c = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("dd MMMM yyyy");
            String currentDate = df.format(c.getTime());


            DateFormat formatter = new SimpleDateFormat("dd MMMM yyyy, EEEE");
            Date dateobj = (Date) formatter.parse(dueDate.getText().toString());
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
                args.putBoolean("All", all);
                args.putBoolean("edit", false);
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
                args.putBoolean("edit", false);
                args.putString("iftask", "iftask");
                args.putString("dialogTitle","Who to remind");
                dialogFragment1.setArguments(args);

                dialogFragment1.setTargetFragment(dialogFragment1, 2);
                dialogFragment1.show(getFragmentManager(), "whotoremind");

                break;


            case R.id.task_remainder:

                try {
                    RemainderDialogFragment remainderDialog = new RemainderDialogFragment();
                    args = new Bundle();
                    args.putBoolean("edit", false);
                    args.putString("task", "task");
                    args.putBoolean("is_recurring", recurring.isChecked());
                    args.putBoolean("todayDate", checkTodayDate());
                    args.putLong("time", convertTimeStamp(dueDate.getText(), dueTime.getText()));

                    remainderDialog.setArguments(args);
                    remainderDialog.show(getFragmentManager(), "remainder");
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;


            case R.id.task_repeat:

                RepeatDialogFragment repeatDialogFragment = new RepeatDialogFragment();
                args = new Bundle();
                args.putBoolean("edit", false);
                args.putString("task", "task");
                repeatDialogFragment.setArguments(args);
                repeatDialogFragment.show(getFragmentManager(), "repeat");

                break;

            case R.id.task_until:

                RepeatUntilFragment repeatUntilFragment = new RepeatUntilFragment();
                args = new Bundle();
                args.putBoolean("edit", false);
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

//                    FragmentViewPager pager = new FragmentViewPager();
//                    args = new Bundle();
//                    args.putStringArrayList("imagelist", imageList);
//                    args.putInt("position", _position);
//                    pager.setArguments(args);
//                    replaceFragment(pager,args,true);

                    Intent intent = new Intent(ActivityCreateTask.this, ActivityViewPager.class);
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
                    i.setData(Uri.parse(documentUrl.get(_position)));
                    startActivity(i);

                }
                break;

            case R.id.task_list:

                FragmentTaskList fragmentTaskList = new FragmentTaskList();

                Bundle ards1 = new Bundle();
                ards1.putString("ifedit", "");
                fragmentTaskList.setArguments(ards1);

                fragmentTaskList.show(getFragmentManager(), "tasklist");

                break;

            case R.id.due_date:

                DueDateFagment dueDateFagment = new DueDateFagment();

                Bundle ards2 = new Bundle();
                ards2.putString("ifedit", "");
                dueDateFagment.setArguments(ards2);

                dueDateFagment.show(getFragmentManager(), "duedate");

                break;

            case R.id.due_time:

                timePicker((TextView) view);

                break;
            case R.id.delete_image:

                object = view.getTag();
                if (object != null && object instanceof Integer) {
                    Integer _position = (Integer) view.getTag();
                    deleteImagePostion = _position;
                    imageList.remove(deleteImagePostion);
                    imgadapter.notifyDataSetChanged();

                }
                break;

            case R.id.delete_text:

                object = view.getTag();
                if (object != null && object instanceof Integer) {
                    Integer _position = (Integer) view.getTag();
                    deleteImagePostion = _position;
                    // new upload text
                    documentList.remove(deleteImagePostion);
                    documentadapter.notifyDataSetChanged();

                }
                break;
        }
    }


    public void datePicket(final TextView Date) {

        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        // Launch Date Picker Dialog
        DatePickerDialog dpd = new DatePickerDialog(ActivityCreateTask.this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        // Display Selected date in textbox

                        Calendar caltemp = Calendar.getInstance();
                        caltemp.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        caltemp.set(Calendar.MONTH, monthOfYear);
                        caltemp.set(Calendar.YEAR, year);

                        SimpleDateFormat format = new SimpleDateFormat("dd MMMM yyyy, EEEE");

                        Date.setText(format.format(caltemp.getTime()));

//                        Log.d("Date ", (String) startDate.getText());
                    }
                }, mYear, mMonth, mDay);
        dpd.show();

    }


    public void setRemainder(String time, String min) {
        remainder.setText(time + " before");
        reminderTime = min;
    }


    public void setRepeat(String select, String repeat, String value) {

        this.repeat.setError(null);

        if (repeat.equals("0")) {

            this.repeat.setText("Days" + " - " + String.valueOf(Integer.parseInt(value) + 1));
            this.select = select;
            this.mode = "Days";
            this.value = String.valueOf(Integer.parseInt(value) + 1);


        } else if (repeat.equals("1")) {

            this.repeat.setText("Weeks" + " - " + String.valueOf(Integer.parseInt(value) + 1));
            this.select = select;
            this.mode = "Weeks";
            this.value = String.valueOf(Integer.parseInt(value) + 1);


        } else if (repeat.equals("2")) {

            this.repeat.setText("Months" + " - " + String.valueOf(Integer.parseInt(value) + 1));
            this.select = select;
            this.mode = "Months";
            this.value = String.valueOf(Integer.parseInt(value) + 1);
        } else if (repeat.equals("Days")) {

            this.repeat.setText(repeat + " - " + value);
            this.mode = "Days";
            this.value = value;
        } else {
            this.repeat.setText(repeat);
            this.mode = repeat;
            this.value = "";

        }

    }

    public void setRepeatUntil(String until, String value) {

        this.until.setError(null);
        this.until.setText(until);
    }


    public void setAttendee(ArrayList<AttendeeModel> attendeeList) {

        this.attendee.setError(null);
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


    public void setDefaultAttendee(ArrayList<AttendeeModel> attendeeList) {

        this.attendee.setError(null);
        attendeeDataList = new ArrayList<>();
        boolean flag = false;
        boolean flag1 = true;

        for (int i = 0; i < attendeeList.size(); i++) {

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
                                whoToRemind.setText(attendeeList.get(i).getName().toString().trim());
                                flag1 = false;
                            } else {
                                String txt = whoToRemind.getText().toString();
                                whoToRemind.setText(txt + ", " + attendeeList.get(i).getName().toString().trim());
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

        TaskDataModel.TaskDetail taskDetails = new TaskDataModel().new TaskDetail();

        taskDetails.setOffline_id(1);
        taskDetails.setTask_name(String.valueOf(taskName.getText().toString().trim()));
        taskDetails.setReminder("" + reminderTime);

        if (listId == 0) {
            taskDetails.setTask_list_id(SharedPrefUtils.getTaskListID(this));
        } else
            taskDetails.setTask_list_id(listId);

        try {
            taskDetails.setDue_date(convertTimeStamp(dueDate.getText(), dueTime.getText()));
        } catch (Exception e) {
            e.printStackTrace();
        }

//        taskDetails.setEndtime(endTimeStamp);

        if (recurring.isChecked()) {

            taskDetails.setIs_recurring("yes");

            if (select.equals("Other")) {
                taskDetails.setRepeate(this.select);
                taskDetails.setRepeate_frequency(this.mode);
                taskDetails.setRepeate_num(this.value);
            } else {
                taskDetails.setRepeate(this.mode);
                taskDetails.setRepeate_frequency(this.value);
                taskDetails.setRepeate_num("");
            }

            taskDetails.setRepeate_untill(until.getText().toString().trim());

        } else {
            taskDetails.setIs_recurring("no");
            taskDetails.setRepeate("");
            taskDetails.setRepeate_frequency("");
            taskDetails.setRepeate_num("");
            taskDetails.setRepeate_untill("");
        }

        // notes

        ArrayList<TaskDataModel.Notes> notesList = new ArrayList<>();

        if (notes.getText().toString().trim().equals("")) {

        } else {
            TaskDataModel.Notes addnote = new TaskDataModel().new Notes();
            addnote.setNote(notes.getText().toString());
            addnote.setUser_id(SharedPrefUtils.getUserDetailModel(this).getId());
            notesList.add(addnote);
        }


        /*//files
        ArrayList<TaskDataModel.Files> fileDataList = new ArrayList<>();

        if (!fileList.isEmpty()) {
            for (TaskDataModel.Files filesmodel : fileList) {
                TaskDataModel.Files files = new TaskDataModel.Files();
                files.setFile_name(filesmodel.getFile_name());
                files.setFile_type(filesmodel.getFile_type());
                files.setUrl(filesmodel.getUrl());
                fileDataList.add(files);
            }
        }*/

        imageList.addAll(documentList);
        // add all

        ArrayList<TaskDataModel.TaskData> taskList = new ArrayList<>();

        TaskDataModel.TaskData taskData = new TaskDataModel().new TaskData();
        taskData.setTask(taskDetails);
        taskData.setTaskAttendee(attendeeDataList);
        taskData.setTaskFile(imageList);
        taskData.setTaskWhomRemind(whoToRemindList);
        taskData.setTaskNote(notesList);

        taskList.add(taskData);

        TaskDataModel model = new TaskDataModel();
        model.setTask(taskList);

        return model;

    }

    public boolean checkValidDate() {
        boolean result = true;

        try {
            long selectedTimestamp = convertTimeStamp(dueDate.getText().toString(), dueTime.getText().toString());
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
            attendee.setError(Constants.VALID_ATTENDEE_WHO_N);
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
// else if (taskList.getText().toString().trim().equals("")) {
//            ToastUtils.showToast(this, Constants.ENTER_DETAIL);
//            result = false;
//        }


        return result;
    }


    // for uploading image
    public void sendUploadProfileImageRequest(Bitmap originalImage) {
        showProgressDialog(getResources().getString(R.string.please_wait));
        imageUploading = true;
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

        System.out.println("image size" + ba.length);
        Log.e("image size", "" + ba.length);
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

        imageUploading = true;
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
            controller.getData(AppConstants.FILE_UPLOAD_REQ_TASK, requestData);
        } catch (Exception e) {
            removeProgressDialog();
            e.printStackTrace();
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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


//    public void uploadFiles(final String filepath) {
//
//String uploadedData ="";
//        if (ConnectivityUtils.isNetworkEnabled(this)) {
//
//            showProgressDialog("Please wait..");
//
//            try {
//                final Handler handler = new Handler() {
//                    public void handleMessage(Message msg) {
//                        // dialog dismiss
//                        removeProgressDialog();
//                        if (!StringUtils.isNullOrEmpty(uploadedData)) {
//
//                            try {
//                                JSONObject obj = new JSONObject(uploadedData);
//                                Intent i = new Intent();
//                                i.putExtra(AppConstants.DATA, uploadedData);
//                                setResult(RESULT_OK, i);
//                                finish();
//                            } catch (Exception e) {
//                                // TODO: handle exception
//                                finish();
//                            }
//
//                        } else
//                            ToastUtils.showToast(CameraRollActivity.this, AppConstants.SERVER_ERROR);
//                    }
//                };
//                Thread checkUpdate = new Thread() {
//                    public void run() {
//
//                        UploadFileUtils multipleImages = new UploadFileUtils(ActivityCreateAppointment.this);
//                        uploadedData = multipleImages.uploadImage(filepath, "", "");
//
//                        handler.sendEmptyMessage(0);
//                    }
//                };
//                checkUpdate.start();
//            } catch (Exception e) {
//                // TODO: handle exception
//                removeProgressDialog();
//                e.printStackTrace();
//            }
//        } else {
//            ToastUtils.showToast(this, "NO_NETWORK");
//        }
//    }

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
                        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());


                        //int maxImageSize = BitmapUtils.getMaxSize(this);
                        // Bitmap sourceBitmap = BitmapUtils.getScaledBitmap(bitmap,(int)(400 * density),(int)(400 * density),false);

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


    public void setTaskTime(String date) {
        dueDate.setText(date);

    }

    public void timePicker(final TextView Time) {
        final Calendar c = Calendar.getInstance();
        int mHour = c.get(Calendar.HOUR_OF_DAY);
        int mMinute = c.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        TimePickerDialog tpd = new TimePickerDialog(ActivityCreateTask.this,
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

                        Time.setText(aTime.toUpperCase());

                        Log.d("time ", (String) Time.getText());

                    }
                }, mHour, mMinute, false);
        tpd.show();
    }


    public void selectList(String name, int id) {

        this.listId = id;
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

    }

    @Override
    public void onBackPressed() {

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setMessage(getResources().getString(R.string.exit_task)).setNegativeButton(R.string.new_yes
                , new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                dialog.cancel();
                finish();


            }
        }).setPositiveButton(R.string.new_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // do nothing
                dialog.cancel();


            }
        }).setIcon(android.R.drawable.ic_dialog_alert);

        AlertDialog alert11 = dialog.create();
        alert11.show();

        alert11.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.home_light_blue));
        alert11.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.canceltxt_color));

    }
}

