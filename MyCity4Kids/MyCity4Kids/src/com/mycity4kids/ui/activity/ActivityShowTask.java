package com.mycity4kids.ui.activity;

import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.kelltontech.utils.ConnectivityUtils;
import com.kelltontech.utils.StringUtils;
import com.kelltontech.utils.ToastUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.controller.AddTaskNotesController;
import com.mycity4kids.controller.DeleteTaskController;
import com.mycity4kids.dbtable.TableAdult;
import com.mycity4kids.dbtable.TableAppointmentData;
import com.mycity4kids.dbtable.TableKids;
import com.mycity4kids.dbtable.TableTaskData;
import com.mycity4kids.dbtable.TaskTableAttendee;
import com.mycity4kids.dbtable.TaskTableFile;
import com.mycity4kids.dbtable.TaskTableNotes;
import com.mycity4kids.dbtable.TaskTableWhoToRemind;
import com.mycity4kids.models.user.KidsInfo;
import com.mycity4kids.newmodels.AddTaskNoteResponse;
import com.mycity4kids.newmodels.AttendeeModel;
import com.mycity4kids.newmodels.DeleteTaskModel;
import com.mycity4kids.newmodels.NotesModel;
import com.mycity4kids.newmodels.TaskDataModel;
import com.mycity4kids.newmodels.TaskResponse;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.reminders.Reminder;
import com.mycity4kids.ui.adapter.AdapterNotes;
import com.mycity4kids.ui.adapter.AddDocumentAdapter;
import com.mycity4kids.ui.adapter.AddImagesAdapter;
import com.mycity4kids.ui.fragment.NotesDialogFragment;
import com.mycity4kids.widget.CustomListView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by manish.soni on 09-07-2015.
 */
public class ActivityShowTask extends BaseActivity implements View.OnClickListener {

    TextView title, startTime, startDate, repeats, remanider, attendees, listName, notes, until;
    TableTaskData tableTask;
    TableKids kids;
    TableAdult adult;
    // ImageView delete;
    LatLng location;
    private int taskId;
    private Toolbar mToolbar;
    private CustomListView notesListView;

    private CustomListView listviewAttachment;
    private CustomListView listviewImages;
    private ArrayList<TaskDataModel.Files> imageList;
    private ArrayList<TaskDataModel.Files> documentList;
    private AddImagesAdapter imgadapter;
    private AddDocumentAdapter documentadapter;
    private ArrayList<String> documentUrl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aa_show_task);

        NotificationManager nMgr = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        nMgr.cancel(getIntent().getIntExtra(AppConstants.NOTIFICATION_ID, 0));

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Task");

        title = (TextView) findViewById(R.id.task_title);
        startTime = (TextView) findViewById(R.id.start_time);
        startDate = (TextView) findViewById(R.id.start_date);
        repeats = (TextView) findViewById(R.id.repeats);
        remanider = (TextView) findViewById(R.id.reminder);
        notes = (TextView) findViewById(R.id.addnotes);
        attendees = (TextView) findViewById(R.id.attendees);
        listName = (TextView) findViewById(R.id.list_name);
//        until = (TextView) findViewById(R.id.until_task);

        notes.setOnClickListener(this);

        listviewAttachment = (CustomListView) findViewById(R.id.list_attachment);
        listviewImages = (CustomListView) findViewById(R.id.list_images);

        listviewImages.isExpanded();
        listviewAttachment.isExpanded();

        notesListView = (CustomListView) findViewById(R.id.noteslist);

//        findViewById(R.id.addnotes).setOnClickListener(this);

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        tableTask = new TableTaskData(BaseApplication.getInstance());
        kids = new TableKids(BaseApplication.getInstance());
        adult = new TableAdult(BaseApplication.getInstance());

    }

    @Override
    protected void onResume() {
        super.onResume();

        Intent intent = getIntent();
        taskId = intent.getIntExtra(AppConstants.EXTRA_TASK_ID, 1);

        // appointment table
        TableTaskData taskData = new TableTaskData(BaseApplication.getInstance());
        TaskDataModel.TaskDetail appDetail = taskData.getTaskbyId(taskId);

        // get from attendee table
        TaskTableAttendee attendeeTable = new TaskTableAttendee(BaseApplication.getInstance());
        ArrayList<AttendeeModel> attendeeList = attendeeTable.getDataBytask(taskId);

        // get from whotoRemond table
        TaskTableWhoToRemind whotoRemindTable = new TaskTableWhoToRemind(BaseApplication.getInstance());
        ArrayList<AttendeeModel> whoToRemindList = whotoRemindTable.getDataByTask(taskId);

        // get from FILES

        imageList = new ArrayList<>();
        documentList = new ArrayList<>();
        documentUrl = new ArrayList<>();

        TaskTableFile fileTable = new TaskTableFile(BaseApplication.getInstance());
        ArrayList<TaskDataModel.Files> fileList = fileTable.getDataByTask(taskId);

        if (!fileList.isEmpty()) {
            for (TaskDataModel.Files model : fileList) {
                if (model.getFile_type().equalsIgnoreCase("image")) {
                    imageList.add(model);
                } else {

                    documentList.add(model);
                    documentUrl.add(model.getUrl());
                }
            }
        }

        if (imageList.isEmpty()) {
            findViewById(R.id.image_text).setVisibility(View.GONE);
        } else {
            findViewById(R.id.image_text).setVisibility(View.VISIBLE);
        }


        if (documentList.isEmpty()) {
            findViewById(R.id.attachments_text).setVisibility(View.GONE);
        } else {
            findViewById(R.id.attachments_text).setVisibility(View.VISIBLE);
        }

        // adapetrs for attachements and images
        imgadapter = new AddImagesAdapter(this, null, imageList, this, false, false);
        listviewImages.setAdapter(imgadapter);

        documentadapter = new AddDocumentAdapter(this, null, documentList, this, false, false);
        listviewAttachment.setAdapter(documentadapter);

        // note

        TaskTableNotes notesTable = new TaskTableNotes(BaseApplication.getInstance());
        ArrayList<TaskDataModel.Notes> noteList = notesTable.getDataByTask(taskId);

        ArrayList<NotesModel> notemodellist = new ArrayList<>();

        for (int i = 0; i < noteList.size(); i++) {

            NotesModel model = new NotesModel();
            model.setMsg(noteList.get(i).getNote());
            String addedby = new TableAdult(BaseApplication.getInstance()).getAdults(noteList.get(i).getUser_id()).getFirst_name();
            model.setAddedby(addedby);

            notemodellist.add(model);
        }

        if (noteList.isEmpty()) {
            findViewById(R.id.notes_text).setVisibility(View.GONE);
        } else {
            findViewById(R.id.notes_text).setVisibility(View.VISIBLE);
        }


        AdapterNotes adapterNotsList = new AdapterNotes(this, notemodellist);
        notesListView.setAdapter(adapterNotsList);


//         setting values

        if (appDetail != null) {
            title.setText(appDetail.getTask_name());
            startTime.setText(getTime(appDetail.getDue_date()).toLowerCase());
            startDate.setText(getDate(appDetail.getDue_date()));
            listName.setText(appDetail.getListName());

            if (!StringUtils.isNullOrEmpty(appDetail.getReminder())) {
                if (appDetail.getReminder().equals("0")) {
                    remanider.setText(appDetail.getReminder() + " minutes before ");
                } else {

                    if (Integer.parseInt(appDetail.getReminder()) % 60 == 0) {
                        remanider.setText(Integer.parseInt(appDetail.getReminder()) / 60 + " hours before ");
                    } else
                        remanider.setText(appDetail.getReminder() + " minutes before ");
                }
            }
            // remanider.setText(appDetail.getReminder() + " minutes before ");

            if (appDetail.getIs_recurring().equals("yes")) {

                StringBuilder stringBuilder = new StringBuilder();

                // stringBuilder.append("Every ");

                if (appDetail.getRepeate().equals("Days")) {
                    stringBuilder.append(appDetail.getRepeate_frequency());
                    stringBuilder.append(" until " + appDetail.getRepeate_untill());
                    repeats.setText(stringBuilder.toString());

                } else if (appDetail.getRepeate().equals("Other")) {

                    if (appDetail.getRepeate_frequency().equals("Days")) {

                        stringBuilder.append(appDetail.getRepeate_num() + " - " + appDetail.getRepeate_frequency());
                        stringBuilder.append(" until " + appDetail.getRepeate_untill());
                        repeats.setText(stringBuilder.toString());

                    } else if (appDetail.getRepeate_frequency().equals("Weeks")) {
                        stringBuilder.append(appDetail.getRepeate_num() + " - " + appDetail.getRepeate_frequency());
                        stringBuilder.append(" until " + appDetail.getRepeate_untill());
                        repeats.setText(stringBuilder.toString());


                    } else if (appDetail.getRepeate_frequency().equals("Months")) {
                        stringBuilder.append(appDetail.getRepeate_num() + " - " + appDetail.getRepeate_frequency());
                        stringBuilder.append(" until " + appDetail.getRepeate_untill());
                        repeats.setText(stringBuilder.toString());
                    }

                } else {
                    repeats.setText(appDetail.getRepeate());
                }

            } else if (appDetail.getIs_recurring().equals("no")) {
                repeats.setText("No");
            } else {
                repeats.setText("");
            }
            attendees.setText(Html.fromHtml(attendeeString(attendeeList)));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // according to fragment change it
        getMenuInflater().inflate(R.menu.show_task, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            case R.id.share:

                if ((imageList != null && imageList.size() > 0)) {
                    Picasso.with(this).load(imageList.get(0).getUrl()).into(new Target() {
                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom loadedFrom) {
                            Uri bitmapUri = getLocalBitmapUri(bitmap);
                            shareIntent(bitmapUri);
                        }

                        @Override
                        public void onBitmapFailed(Drawable drawable) {
                            shareIntent(null);
                        }

                        @Override
                        public void onPrepareLoad(Drawable drawable) {
                        }
                    });
                } else {
                    shareIntent(null);
                }

                return true;

            case R.id.edit:

                Intent i = new Intent(this, ActivityEditTask.class);
                i.putExtra(AppConstants.EXTRA_TASK_ID, taskId);
                startActivityForResult(i, 1);

                return true;

            case R.id.delete:


                if (ConnectivityUtils.isNetworkEnabled(this)) {

                    AlertDialog.Builder dialog = new AlertDialog.Builder(this);

                    dialog.setMessage(getResources().getString(R.string.delete_task)).setNegativeButton(R.string.new_yes
                            , new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.cancel();

                            DeleteTaskModel taskModel = new DeleteTaskModel();

                            ArrayList<DeleteTaskModel.Tasks> tasksArrayList = new ArrayList<>();

                            DeleteTaskModel.Tasks model = new DeleteTaskModel().new Tasks();
                            model.setId(taskId);
                            tasksArrayList.add(model);
                            taskModel.setTasks(tasksArrayList);

                            showProgressDialog(getString(R.string.please_wait));

                            DeleteTaskController _controller = new DeleteTaskController(ActivityShowTask.this, ActivityShowTask.this);
                            _controller.getData(AppConstants.DELETE_TASK_REQUEST, taskModel);


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


                } else {
                    ToastUtils.showToast(this, getString(R.string.error_network));
                }


                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void shareIntent(Uri bitmapUri) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        String type = "text/plain";
        if (bitmapUri != null) {
            sendIntent.putExtra(Intent.EXTRA_STREAM, bitmapUri);
            type = "image/*";
        }
        sendIntent.setType(type);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Task: " + title.getText().toString() + "\nDue Date and Time: " + startDate.getText().toString() + ", " + startTime.getText().toString().toLowerCase());

        startActivity(Intent.createChooser(sendIntent, "Share Tasks"));
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

    @Override
    protected void updateUi(Response response) {
        if (response == null) {
            removeProgressDialog();
            showToast("Content not fetching from server side");
            return;
        }

        switch (response.getDataType()) {

            case AppConstants.ADD_TASK_NOTES_REQ:
                try {
                    AddTaskNoteResponse responseData = (AddTaskNoteResponse) response.getResponseObject();

                    if (responseData.getResponseCode() == 200) {

                        showToast(responseData.getResult().getMessage());

                        // save in db
                        TaskTableNotes NoteTable = new TaskTableNotes((BaseApplication) getApplicationContext());
                        NoteTable.AddNotes(responseData.getResult().getData().getTaskNote().getNote(), responseData.getResult().getData().getTaskNote().getTask_id(), responseData.getResult().getData().getTaskNote().getUser_id(), responseData.getResult().getData().getTaskNote().getId());

                        //   AppoitmentDataModel.Notes attendeemodel = response.get
                        //NoteTable.insertData(responseData);
                        //saveData(responseData.getResult().getData());

                        showToast(responseData.getResult().getMessage());

                        // update listview
                        TaskTableNotes notesTable = new TaskTableNotes(BaseApplication.getInstance());
                        ArrayList<TaskDataModel.Notes> noteList = notesTable.getDataByTask(taskId);

                        ArrayList<NotesModel> notemodellist = new ArrayList<>();

                        for (int i = 0; i < noteList.size(); i++) {

                            NotesModel model = new NotesModel();
                            model.setMsg(noteList.get(i).getNote());
                            String addedby = new TableAdult(BaseApplication.getInstance()).getAdults(noteList.get(i).getUser_id()).getFirst_name();
                            model.setAddedby(addedby);

                            notemodellist.add(model);
                        }


                        AdapterNotes adapterNotsList = new AdapterNotes(this, notemodellist);
                        notesListView.setAdapter(adapterNotsList);


                        if (findViewById(R.id.notes_text).getVisibility() != View.VISIBLE)
                            findViewById(R.id.notes_text).setVisibility(View.VISIBLE);


                    } else if (responseData.getResponseCode() == 400) {
                        showToast(responseData.getResult().getMessage());

                    }
                    removeProgressDialog();

                } catch (Exception e) {

                }
                break;

            default:
                break;


            case AppConstants.DELETE_TASK_REQUEST:

                try {
                    TaskResponse responseData = (TaskResponse) response.getResponseObject();
                    if (responseData.getResponseCode() == 200) {

                        // delete  in db
                        tableTask = new TableTaskData(BaseApplication.getInstance());
                        tableTask.deleteTask(taskId);

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

                        showToast(responseData.getResult().getMessage());

                        Reminder.with(ActivityShowTask.this).cancel(taskId);

                        // check here count
                        TableAppointmentData data = new TableAppointmentData(BaseApplication.getInstance());
                        TableTaskData taskData = new TableTaskData(BaseApplication.getInstance());
                        int count = data.getRowsCount() + taskData.getRowsCount();
                        if (count > 0) {
                            SharedPrefUtils.setHomeCheckFlag(this, true);
                        } else {
                            SharedPrefUtils.setHomeCheckFlag(this, false);
                        }

                        Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
                        setResult(1, intent);
                        finish();


                    } else if (responseData.getResponseCode() == 400) {
                        showToast(responseData.getResult().getMessage());

                    }
                    removeProgressDialog();

                } catch (Exception e) {
                    removeProgressDialog();
                }
        }
    }

    private String getTime(long timeStampStr) {

        try {
            DateFormat sdf = new SimpleDateFormat("h:mm a");
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


    public void setAttendee(ArrayList<AttendeeModel> models) {

        ArrayList<KidsInfo> kidsInformations = kids.getAllKids();


        for (int i = 0; i < models.size(); i++) {

            for (int j = 0; j < kidsInformations.size(); j++) {

                if (models.get(i).getName().equals(kidsInformations.get(j).getName())) {
                    models.get(i).setColorCode(kidsInformations.get(j).getColor_code());
                }
            }
        }

        if (kidsInformations.size() == models.size()) {
        }

    }

    public void setNotes(String note) {
        if (!StringUtils.isNullOrEmpty(note)) {
            //call webservice
            NotesModel model = new NotesModel();
            model.setMsg(note);
            model.setAppointmentid(taskId);


            if (ConnectivityUtils.isNetworkEnabled(this)) {
                showProgressDialog(getString(R.string.please_wait));

                AddTaskNotesController _controller = new AddTaskNotesController(this, this);
                _controller.getData(AppConstants.ADD_TASK_NOTES_REQ, model);
            } else {
                showToast(getString(R.string.error_network));
            }

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.addnotes:

                NotesDialogFragment dialogFragment = new NotesDialogFragment();
                dialogFragment.setTargetFragment(dialogFragment, 2);
                Bundle bundle = new Bundle();
                bundle.putString("ifTask", "task");
                dialogFragment.setArguments(bundle);
                dialogFragment.show(getFragmentManager(), "notes");

                break;

            case R.id.image:

                Object object = v.getTag();
                if (object != null && object instanceof Integer) {
                    Integer _position = (Integer) v.getTag();
                    Intent intent = new Intent(ActivityShowTask.this, ActivityViewPager.class);
                    intent.putExtra("imagelist", imageList);
                    intent.putExtra("position", _position);
                    intent.putExtra("isfrmAppointment", false);
                    startActivity(intent);

                }
                break;

            case R.id.txtfile:

                object = v.getTag();
                if (object != null && object instanceof Integer) {
                    Integer _position = (Integer) v.getTag();
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(documentUrl.get(_position)));
                    startActivity(i);


                }
                break;

        }
    }


    public String attendeeString(ArrayList<AttendeeModel> attendeeList) {

        int totalCountv = kids.getKidsCount() + adult.getAdultCount();

        StringBuilder attendeeS = new StringBuilder();
        Boolean flag = false;

        if (totalCountv == attendeeList.size()) {

            String name = "<font color=" + "#3949ab" + ">" + "All" + "</font>";
            attendeeS.append(name);
            attendeeS.append(" ( ");

            for (int i = 0; i < attendeeList.size(); i++) {

                if (flag == true) {
                    attendeeS.append(", ");
                }

                name = "<font color=" + attendeeList.get(i).getColorCode() + ">" + attendeeList.get(i).getName().toString().trim() + "</font>";
                attendeeS.append((name));
                flag = true;
            }

            attendeeS.append(" )");


        } else {

            if (attendeeList.size() == 1) {

                String name = "<font color=" + attendeeList.get(0).getColorCode() + ">" + attendeeList.get(0).getName().toString().trim() + "</font>";
                attendeeS.append((name));

            } else if (attendeeList.size() > 1) {


                for (int i = 0; i < attendeeList.size(); i++) {

                    if (flag == true) {
                        attendeeS.append(", ");
                    }
                    String name = "<font color=" + attendeeList.get(i).getColorCode() + ">" + attendeeList.get(i).getName().toString().trim() + "</font>";
                    attendeeS.append((name));
                    flag = true;
                }

            }

        }
//        int totalCountv = kids.getKidsCount() + adult.getAdultCount();
//
//        StringBuilder attendeeS = new StringBuilder();
//        Boolean flag = false;
//
//        if (totalCountv == attendeeList.size()) {
//
//            if (attendeeList.size() == 1) {
//
//                //  String colorcode = attendeeList.get(0).getColorCode();
//
//                // String name = attendeeList.get(0).getName();
//
//                // String text = "<font color=colorcode>name</font> <font color=#ffcc00>zweite Farbe</font>";
//
//                attendeeS.append(attendeeList.get(0).getName());
//
//            } else if (attendeeList.size() > 1) {
//
//                attendeeS.append("All ( ");
//
//                for (int i = 0; i < attendeeList.size(); i++) {
//
//                    if (flag == true) {
//                        attendeeS.append(", ");
//                    }
//                    attendeeS.append(attendeeList.get(i).getName());
//                    flag = true;
//                }
//
//                attendeeS.append(" )");
//            }
//
//        } else {
//
//            if (attendeeList.size() == 1) {
//
//                attendeeS.append(attendeeList.get(0).getName());
//
//            } else if (attendeeList.size() > 1) {
//
//
//                for (int i = 0; i < attendeeList.size(); i++) {
//
//                    if (flag == true) {
//                        attendeeS.append(", ");
//                    }
//                    attendeeS.append(attendeeList.get(i).getName());
//                    flag = true;
//                }
//
//            }
//
//        }

        return attendeeS.toString();

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
        setResult(1, intent);
        finish();

    }
}
