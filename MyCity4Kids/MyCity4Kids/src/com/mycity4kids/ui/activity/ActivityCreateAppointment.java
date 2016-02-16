package com.mycity4kids.ui.activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
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
import com.mycity4kids.controller.AppointmentController;
import com.mycity4kids.controller.ImageUploadController;
import com.mycity4kids.dbtable.TableApiEvents;
import com.mycity4kids.dbtable.TableAppointmentData;
import com.mycity4kids.dbtable.TableAttendee;
import com.mycity4kids.dbtable.TableFile;
import com.mycity4kids.dbtable.TableNotes;
import com.mycity4kids.dbtable.TableWhoToRemind;
import com.mycity4kids.dbtable.UserTable;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.forgot.CommonResponse;
import com.mycity4kids.models.user.ImageUploadRequest;
import com.mycity4kids.models.user.UserModel;
import com.mycity4kids.newmodels.AppointmentMappingModel;
import com.mycity4kids.newmodels.AppointmentResponse;
import com.mycity4kids.newmodels.AppoitmentDataModel;
import com.mycity4kids.newmodels.AttendeeModel;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.reminders.AppointmentManager;
import com.mycity4kids.reminders.Reminder;
import com.mycity4kids.ui.adapter.AddDocumentAdapter;
import com.mycity4kids.ui.adapter.AddImagesAdapter;
import com.mycity4kids.ui.fragment.AttendeeDialogFragment;
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


public class ActivityCreateAppointment extends BaseActivity implements View.OnClickListener {

    TextView startDate, startTime, endDate, endTime, attendee, remainder, textRepeat, repeat, textUntil, until, whoToRemind;
    long startTimeStamp, endTimeStamp;
    //ImageView save;
    EditText appointmentName, appointmentLocation, notes;
    AppointmentMappingModel appointment;
    //    Appointmen
    private Toolbar mToolbar;
    CheckBox recurring;

    String finalDate;
    String finalTime;
    String tempTime;
    Boolean flag = false;
    String value = "";
    String mode = "";
    String select = "";

    public ArrayList<AppoitmentDataModel.Attendee> attendeeDataList;
    public ArrayList<AppoitmentDataModel.WhoToRemind> whoToRemindList;
    //public ArrayList<AppoitmentDataModel.Files> fileList;
    private String reminderTime;
    private String repeat_num = "";
    private String repeat_frequency = "";
    private CustomListView listviewAttachment;
    private CustomListView listviewImages;
    private ArrayList<AppoitmentDataModel.Files> imageList;
    private ArrayList<AppoitmentDataModel.Files> documentList;
    private AddImagesAdapter imgadapter;
    private AddDocumentAdapter documentadapter;
    private String file_name = "";
    private ArrayList<String> documentUrl;
    private boolean imageUploading = false;
    private float density;
    private String apiEventId;
    private int deleteImagePostion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aa_create_appointment);
        density = getResources().getDisplayMetrics().density;
        Utils.pushOpenScreenEvent(ActivityCreateAppointment.this, "Create Appointment", SharedPrefUtils.getUserDetailModel(this).getId() + "");
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Create Appointment");

        //save = (ImageView) findViewById(R.id.save);
        appointmentName = (EditText) findViewById(R.id.appointment_name);
        appointmentLocation = (EditText) findViewById(R.id.appointment_location);
        notes = (EditText) findViewById(R.id.appointment_notes);
        attendee = (TextView) findViewById(R.id.appointment_attendee);
        whoToRemind = (TextView) findViewById(R.id.appointment_whotoremind);
        remainder = (TextView) findViewById(R.id.appointment_remainder);
        recurring = (CheckBox) findViewById(R.id.recurring_event);
        textRepeat = (TextView) findViewById(R.id.repeat_text);
        textUntil = (TextView) findViewById(R.id.until_text);
        repeat = (TextView) findViewById(R.id.appointment_repeat);
        until = (TextView) findViewById(R.id.appointment_until);

        listviewAttachment = (CustomListView) findViewById(R.id.list_attachment);
        listviewImages = (CustomListView) findViewById(R.id.list_images);

        listviewImages.isExpanded();
        listviewAttachment.isExpanded();

        attendee.setKeyListener(null);
        startDate = (TextView) findViewById(R.id.start_date);
        startDate.setKeyListener(null);
        startTime = (TextView) findViewById(R.id.start_time);
        startTime.setKeyListener(null);
        endDate = (TextView) findViewById(R.id.end_date);
        endDate.setKeyListener(null);
        endTime = (TextView) findViewById(R.id.end_time);
        endTime.setKeyListener(null);
        //save.setOnClickListener(this);
        attendee.setOnClickListener(this);
        whoToRemind.setOnClickListener(this);
        startDate.setOnClickListener(this);
        startTime.setOnClickListener(this);
        endDate.setOnClickListener(this);
        endTime.setOnClickListener(this);
        remainder.setOnClickListener(this);

        findViewById(R.id.add_images).setOnClickListener(this);
        findViewById(R.id.add_attachments).setOnClickListener(this);


        appointment = new AppointmentMappingModel();
        attendeeDataList = new ArrayList<>();
        whoToRemindList = new ArrayList<>();
        //fileList = new ArrayList<>();
        imageList = new ArrayList<>();
        documentList = new ArrayList<>();
        documentUrl = new ArrayList<>();
        // adapetrs for attachements and images

        imgadapter = new AddImagesAdapter(this, imageList, null, this, true, true);
        listviewImages.setAdapter(imgadapter);

        documentadapter = new AddDocumentAdapter(this, documentList, null, this, true, true);
        listviewAttachment.setAdapter(documentadapter);

        // get values

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            String name = extras.getString(Constants.EVENT_NAME);
            String location = extras.getString(Constants.EVENT_LOCATION);
            String startdate = extras.getString(Constants.EVENT_START_DATE);
            String enddate = extras.getString(Constants.EVENT_END_DATE);
            apiEventId = extras.getString(Constants.BUSINESS_OR_EVENT_ID);
            String description = extras.getString(Constants.EVENT_DES);

            Calendar cal1 = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            try {
                cal1.setTime(sdf.parse(startdate));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            SimpleDateFormat format = new SimpleDateFormat("dd MMMM yyyy, EEEE");
            SimpleDateFormat format1 = new SimpleDateFormat("hh:mm a");

//            startDate.setText(format.format(cal1.getTime()));
//            startTime.setText(format1.format(cal1.getTime()).toLowerCase());
            String a = format.format(cal1.getTime());
            String ab = format1.format(cal1.getTime()).toLowerCase();

            startDate.setText(format.format(cal1.getTime()));
            startTime.setText(format1.format(cal1.getTime()).toLowerCase());

            try {
                cal1.setTime(sdf.parse(enddate));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            endDate.setText(format.format(cal1.getTime()));
            endTime.setText(format1.format(cal1.getTime()).toLowerCase());

            appointmentName.setText(name);
            appointmentLocation.setText(location);
            notes.setText(description);

        } else {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MINUTE, 30);

            SimpleDateFormat format = new SimpleDateFormat("dd MMMM yyyy, EEEE");
            SimpleDateFormat format1 = new SimpleDateFormat("hh:mm a");

            startDate.setText(format.format(cal.getTime()));
            startTime.setText(format1.format(cal.getTime()).toLowerCase());

            cal.add(Calendar.HOUR_OF_DAY, 1);

            endDate.setText(format.format(cal.getTime()));
            endTime.setText(format1.format(cal.getTime()).toLowerCase());
        }


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
                    args.putString("task", "");
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

                dialog.setMessage(getResources().getString(R.string.exit_appointment)).setNegativeButton(getResources().getString(R.string.yes)
                        , new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.cancel();
                        finish();


                    }
                }).setPositiveButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
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
                AppoitmentDataModel appList = getAlldata();

                if (checkValidation()) {

                    if (ConnectivityUtils.isNetworkEnabled(this)) {
                        showProgressDialog(getString(R.string.please_wait));

                        AppointmentController _controller = new AppointmentController(this, this);
                        _controller.getData(AppConstants.CREATE_APPOINTEMT_REQUEST, appList);
                    } else {
                        showToast(getString(R.string.error_network));
                    }

                }

                return true;
            default:
                return super.onOptionsItemSelected(item);
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

                // data is saving here
                data.getAppointment().setExternal_id("0");
                data.getAppointment().setApi_event_id(apiEventId);

                apponntmentTable.insertData(data.getAppointment());

                for (AppoitmentDataModel.Files filesList : data.getAppointmentFile()) {

                    FileTable.insertData(filesList);

                }

                for (AppoitmentDataModel.Attendee attendeeList : data.getAppointmentAttendee()) {

                    attendeeTable.insertData(attendeeList);

                }
                for (AppoitmentDataModel.Notes notesList : data.getAppointmentNote()) {

                    NoteTable.insertData(notesList);

                }
                for (AppoitmentDataModel.WhoToRemind whotoRemind : data.getAppointmentWhomRemind()) {

                    WhoToRemindTable.insertData(whotoRemind);

                }

                // insert in api event table
                TableApiEvents apiEventTable = new TableApiEvents((BaseApplication) getApplicationContext());
                apiEventTable.insertData(data.getAppointment());
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
                imageUploading = false;
                if (response.getResponseObject() instanceof CommonResponse) {
                    CommonResponse responseModel = (CommonResponse) response
                            .getResponseObject();
                    if (responseModel.getResponseCode() != 200) {
                        showToast(getString(R.string.toast_response_error));
                        return;
                    } else {


                        if (!StringUtils.isNullOrEmpty(responseModel.getResult().getMessage())) {

                            AppoitmentDataModel.Files model = new AppoitmentDataModel.Files();
                            model.setUrl(responseModel.getResult().getMessage());
                            model.setFile_type("image");
                            model.setFile_name(responseModel.getResult().getMessage().substring(responseModel.getResult().getMessage().lastIndexOf("/") + 1));

                            //fileList.add(model);

                            imageList.add(model);
                            imgadapter.notifyDataSetChanged();
                        }
                        //setProfileImage(originalImage);
                        showToast("You have successfully uploaded an image.");

                    }
                }
                break;

            case AppConstants.FILE_UPLOAD_REQ:
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


                            AppoitmentDataModel.Files model = new AppoitmentDataModel.Files();
                            model.setUrl(responseModel.getResult().getMessage());
                            model.setFile_type("text");
                            model.setFile_name(responseModel.getResult().getMessage().substring(responseModel.getResult().getMessage().lastIndexOf("/") + 1));

                            //fileList.add(model);

                            documentList.add(model);
                            documentUrl.add(responseModel.getResult().getMessage());
                            documentadapter.notifyDataSetChanged();
                        }

                        showToast(getResources().getString(R.string.upload_file_successfully));

                    }
                }
                break;


            case AppConstants.CREATE_APPOINTEMT_REQUEST:

                try {
                    AppointmentResponse responseData = (AppointmentResponse) response.getResponseObject();
                    if (responseData.getResponseCode() == 200) {
                        // save in db
                        saveData(responseData.getResult().getData());
                        showToast(responseData.getResult().getMessage());

                        long startTimeMillis = responseData.getResult().getData().getAppointment().get(0).getAppointment().getStarttime();
                        String reminderBefore = responseData.getResult().getData().getAppointment().get(0).getAppointment().getReminder();
                        String recurring = responseData.getResult().getData().getAppointment().get(0).getAppointment().getIs_recurring();
                        String repeat = responseData.getResult().getData().getAppointment().get(0).getAppointment().getRepeate();
                        String repeatFrequency = responseData.getResult().getData().getAppointment().get(0).getAppointment().getRepeate_frequency();
                        String repeatNum = responseData.getResult().getData().getAppointment().get(0).getAppointment().getRepeate_num();
                        String repeatUntill = responseData.getResult().getData().getAppointment().get(0).getAppointment().getRepeate_untill();
                        int reminderId = responseData.getResult().getData().getAppointment().get(0).getAppointment().getId();
                        String appointmentName = responseData.getResult().getData().getAppointment().get(0).getAppointment().getAppointment_name();

                        for (AppoitmentDataModel.WhoToRemind model : responseData.getResult().getData().getAppointment().get(0).getAppointmentWhomRemind()) {
                            if (model.getUser_id() == SharedPrefUtils.getUserDetailModel(this).getId()) {
                                Reminder.with(this).info(Constants.REMINDER_TYPE_APPOINTMENT, appointmentName).startTime(startTimeMillis).setRepeatBehavior(repeat, repeatUntill, repeatFrequency, repeatNum).remindBefore(reminderBefore).setRecurring(recurring).create(reminderId);
                                break;

                            }

                        }


                        SharedPrefUtils.setHomeCheckFlag(this, true);
                        if (AppointmentManager.getInstance(this).getAppointmentMap() != null) {
                            AppoitmentDataModel.AppointmentData appointmentData = responseData.getResult().getData().getAppointment().get(0);
                            AppointmentManager.getInstance(this).createAppointmentMappingModelAndUpdate(appointmentData, false);
                        }

                        finish();


                    } else if (responseData.getResponseCode() == 400) {
                        showToast(responseData.getResult().getMessage());

                    }
                    removeProgressDialog();

                } catch (Exception e) {
                    e.printStackTrace();
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

            case R.id.delete_image:

                Object object = view.getTag();
                if (object != null && object instanceof Integer) {
                    Integer _position = (Integer) view.getTag();
                    deleteImagePostion = _position;

                    // new upload image
                    imageList.remove(deleteImagePostion);
                    imgadapter.notifyDataSetChanged();


                }
                break;

            case R.id.delete_text:

                object = view.getTag();
                if (object != null && object instanceof Integer) {
                    Integer _position = (Integer) view.getTag();
                    deleteImagePostion = _position;

                    documentList.remove(deleteImagePostion);
                    documentadapter.notifyDataSetChanged();

                }
                break;

            case R.id.appointment_attendee:

                boolean all = false;

                ArrayList<Integer> idlist = new ArrayList<>();

                for (AppoitmentDataModel.Attendee model : attendeeDataList) {

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
                args.putString("iftask", "");
                dialogFragment.setArguments(args);


                dialogFragment.setTargetFragment(dialogFragment, 2);
                dialogFragment.show(getFragmentManager(), "attendee");

                break;

            case R.id.appointment_whotoremind:

                all = false;

                idlist = new ArrayList<>();

                for (AppoitmentDataModel.WhoToRemind model : whoToRemindList) {

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
                args.putString("iftask", "");
                args.putString("dialogTitle","Who to remind");
                dialogFragment1.setArguments(args);

                dialogFragment1.setTargetFragment(dialogFragment1, 2);
                dialogFragment1.show(getFragmentManager(), "whotoremind");

                break;

            case R.id.start_date:
                flag = true;
                datePicket((TextView) view);

                break;

            case R.id.start_time:

                flag = true;
                timePicker((TextView) view);

                break;

            case R.id.end_date:
                flag = false;
                datePicket((TextView) view);
                break;

            case R.id.end_time:

                flag = false;
                // showRangerPicker();
                timePicker((TextView) view);
                break;

            case R.id.appointment_remainder:

                try {
                    RemainderDialogFragment remainderDialog = new RemainderDialogFragment();
                    args = new Bundle();
                    args.putBoolean("edit", false);
                    args.putString("task", "");
                    args.putBoolean("is_recurring", recurring.isChecked());
                    args.putBoolean("todayDate", checkTodayDate());
                    args.putLong("time", convertTimeStamp(startDate.getText(), startTime.getText()));

                    remainderDialog.setArguments(args);
                    remainderDialog.show(getFragmentManager(), "remainder");
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;


            case R.id.appointment_repeat:

                RepeatDialogFragment repeatDialogFragment = new RepeatDialogFragment();
                args = new Bundle();
                args.putBoolean("edit", false);
                args.putString("task", "");
                repeatDialogFragment.setArguments(args);
                repeatDialogFragment.show(getFragmentManager(), "repeat");

                break;

            case R.id.appointment_until:

                RepeatUntilFragment repeatUntilFragment = new RepeatUntilFragment();
                args = new Bundle();
                args.putBoolean("edit", false);
                args.putString("task", "");
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

                object = view.getTag();
                if (object != null && object instanceof Integer) {
                    Integer _position = (Integer) view.getTag();

//                    FragmentViewPager pager = new FragmentViewPager();
//                    args = new Bundle();
//                    args.putStringArrayList("imagelist", imageList);
//                    args.putInt("position", _position);
//                    pager.setArguments(args);
//                    replaceFragment(pager,args,true);

                    Intent intent = new Intent(ActivityCreateAppointment.this, ActivityViewPager.class);
                    intent.putExtra("imagelist", imageList);
                    intent.putExtra("position", _position);
                    intent.putExtra("isfrmAppointment", true);
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
        }
    }

    public static String convertDate(String convertdate) {
        String timestamp = "";
        try {
            DateFormat formatter = new SimpleDateFormat("dd MMMM yyyy, EEEE");
            Date dateobj = (Date) formatter.parse(convertdate);
            timestamp = "" + (dateobj.getTime()) / 1000;
            return timestamp;
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return timestamp;
    }

    public static boolean chkTime(String time) {
        boolean result = true;

        String currentime = "" + (System.currentTimeMillis() / 1000);
        if (Integer.parseInt(currentime) < Integer.parseInt(convertDate(time)))
            result = false;

        return result;
    }


    public void datePicket(final TextView Date) {

        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        // Launch Date Picker Dialog
        DatePickerDialog dpd = new DatePickerDialog(ActivityCreateAppointment.this,android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
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


//                        startDate.setText(dayOfMonth + "-"
//                                + (monthOfYear + 1) + "-" + year);


                        if (flag == true) {
                            // this is start time
                            Date.setText(format.format(caltemp.getTime()));
                            if (chkTime(startDate.getText().toString())) { // wrong date

                                Calendar cal = Calendar.getInstance();
                                Date.setText(format.format(cal.getTime()));
                            }


//                            try {
//                                setDateFrequently(Date.getText().toString().trim(), endDate);
//                            } catch (ParseException e) {
//                                e.printStackTrace();
//                            }

                            // check timestamp


                            try {
                                startTimeStamp = convertTimeStamp(startDate.getText(), startTime.getText());
                                endTimeStamp = convertTimeStamp(endDate.getText(), endTime.getText());

                                Calendar calendarStart = Calendar.getInstance();
                                calendarStart.setTimeInMillis(startTimeStamp);

                                Calendar calendarEnd = Calendar.getInstance();
                                calendarEnd.setTimeInMillis(endTimeStamp);

                                if (calendarStart.getTimeInMillis() > calendarEnd.getTimeInMillis()) {
                                    setDateFrequently(startDate.getText().toString().trim(), endDate, false);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }


                        } else {
                            // setting end date
                            //if enddate is less than start date
                            Date.setText(format.format(caltemp.getTime()));

                            try {
                                startTimeStamp = convertTimeStamp(startDate.getText(), startTime.getText());
                                endTimeStamp = convertTimeStamp(endDate.getText(), endTime.getText());

                                Calendar calendarStart = Calendar.getInstance();
                                calendarStart.setTimeInMillis(startTimeStamp);

                                Calendar calendarEnd = Calendar.getInstance();
                                calendarEnd.setTimeInMillis(endTimeStamp);

                                if (calendarStart.getTimeInMillis() > calendarEnd.getTimeInMillis()) {
                                    setDateFrequently(startDate.getText().toString().trim(), endDate, true);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            // }
                        }

                        // Log.d("Date ", (String) startDate.getText());
                    }
                }, mYear, mMonth, mDay);
        dpd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dpd.show();

    }


    public void setDateFrequently(String time, TextView view, boolean changeDate) throws ParseException {
        // setting end date via start date
        SimpleDateFormat format1 = new SimpleDateFormat("dd MMMM yyyy, EEEE");

        Calendar calendar = Calendar.getInstance();
        Date date = null;

        try {
            date = (Date) format1.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        calendar.setTime(date);
        if (changeDate)
            calendar.add(Calendar.DAY_OF_MONTH, 1);

        view.setText(format1.format(calendar.getTime()));
    }

    public void timePicker(final TextView Time) {
        final Calendar c = Calendar.getInstance();
        int mHour = c.get(Calendar.HOUR_OF_DAY);
        int mMinute = c.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        TimePickerDialog tpd = new TimePickerDialog(ActivityCreateAppointment.this,android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
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

                        if (flag == true) {
                            // this is start time

                            try {
                                setTimeFrequently(aTime, endTime);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                        } else {
                            //setting end time
                            try {
                                startTimeStamp = convertTimeStamp(startDate.getText(), startTime.getText());
                                endTimeStamp = convertTimeStamp(endDate.getText(), endTime.getText());

                                Calendar calendarStart = Calendar.getInstance();
                                calendarStart.setTimeInMillis(startTimeStamp);

                                Calendar calendarEnd = Calendar.getInstance();
                                calendarEnd.setTimeInMillis(endTimeStamp);

                                if (calendarStart.getTimeInMillis() > calendarEnd.getTimeInMillis()) {
                                    setDateFrequently(startDate.getText().toString().trim(), endDate, true);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }


                        }

                    }
                }, mHour, mMinute, false);
        tpd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        tpd.show();
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

                    AppoitmentDataModel.Attendee attendeeModel = new AppoitmentDataModel().new Attendee();
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

            // set who to remind
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

                AppoitmentDataModel.Attendee attendeeModel = new AppoitmentDataModel().new Attendee();
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

                        AppoitmentDataModel.WhoToRemind whoModel = new AppoitmentDataModel().new WhoToRemind();
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

    public AppoitmentDataModel getAlldata() {

        AppoitmentDataModel.AppointmentDetail appointmentDetail = new AppoitmentDataModel().new AppointmentDetail();

        try {
            startTimeStamp = convertTimeStamp(startDate.getText(), startTime.getText());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        try {
            endTimeStamp = convertTimeStamp(endDate.getText(), endTime.getText());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        appointmentDetail.setOffline_id(1);
        appointmentDetail.setAppointment_name(String.valueOf(appointmentName.getText().toString().trim()));
        appointmentDetail.setLocality(String.valueOf(appointmentLocation.getText().toString().trim()));
        appointmentDetail.setReminder("" + reminderTime);
        appointmentDetail.setStarttime(startTimeStamp);
        appointmentDetail.setEndtime(endTimeStamp);

        // api event id
        //  appointmentDetail.setApi_event_id(apiEventId);


        if (recurring.isChecked()) {

            appointmentDetail.setIs_recurring("yes");

            if (select.equals("Other")) {
                appointmentDetail.setRepeate(this.select);
                appointmentDetail.setRepeate_frequency(this.mode);
                appointmentDetail.setRepeate_num(this.value);
            } else {
                appointmentDetail.setRepeate(this.mode);
                appointmentDetail.setRepeate_frequency(this.value);
                appointmentDetail.setRepeate_num("");
            }

            appointmentDetail.setRepeate_untill(until.getText().toString().trim());

        } else {
            appointmentDetail.setIs_recurring("no");
            appointmentDetail.setRepeate("");
            appointmentDetail.setRepeate_frequency("");
            appointmentDetail.setRepeate_num("");
            appointmentDetail.setRepeate_untill("");
        }


        // notes

        ArrayList<AppoitmentDataModel.Notes> notesList = new ArrayList<>();

        if (notes.getText().toString().trim().equals("")) {

        } else {
            AppoitmentDataModel.Notes addnote = new AppoitmentDataModel().new Notes();
            addnote.setNote(notes.getText().toString());
            addnote.setUser_id(SharedPrefUtils.getUserDetailModel(this).getId());
            notesList.add(addnote);
        }


      /*  //files
        ArrayList<AppoitmentDataModel.Files> fileDataList = new ArrayList<>();

        if (!fileList.isEmpty()) {
            for (AppoitmentDataModel.Files filesmodel : fileList) {
                AppoitmentDataModel.Files files = new AppoitmentDataModel.Files();
                files.setFile_name(filesmodel.getFile_name());
                files.setFile_type(filesmodel.getFile_type());
                files.setUrl(filesmodel.getUrl());
                fileDataList.add(files);
            }


        }*/
        // for images

        imageList.addAll(documentList);
        // add all

        ArrayList<AppoitmentDataModel.AppointmentData> appointmentList = new ArrayList<>();

        AppoitmentDataModel.AppointmentData appointmentData = new AppoitmentDataModel().new AppointmentData();
        appointmentData.setAppointment(appointmentDetail);
        appointmentData.setAppointmentAttendee(attendeeDataList);
        appointmentData.setAppointmentFile(imageList);
        appointmentData.setAppointmentWhomRemind(whoToRemindList);
        appointmentData.setAppointmentNote(notesList);

        appointmentList.add(appointmentData);

        AppoitmentDataModel model = new AppoitmentDataModel();
        model.setAppointment(appointmentList);

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

        if (appointmentName.getText().toString().trim().equals("")) {
            appointmentName.setFocusableInTouchMode(true);
            appointmentName.requestFocus();
            appointmentName.setError(Constants.VALID_APPOINTMENT_NAME);
//            showSnackbar(findViewById(R.id.lnrRoot), Constants.ENTER_DETAIL);
//            ToastUtils.showToast(this, Constants.ENTER_DETAIL);
            result = false;
        } else if (attendeeDataList.size() == 0) {
            attendee.setFocusableInTouchMode(true);
            attendee.requestFocus();
            attendee.setError(Constants.VALID_ATTENDEE_WHO);
            result = false;
        } else if (!checkValidDate()) {
            ToastUtils.showToast(this, Constants.VALID_DATE);
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


    public void setTimeFrequently(String time, TextView view) throws ParseException {

        // setting end time via start time
        SimpleDateFormat format1 = new SimpleDateFormat("hh:mm a");

        Calendar calendar = Calendar.getInstance();
        Date date = null;

        try {
            date = (Date) format1.parse(time);
            calendar.setTime(date);
            calendar.add(Calendar.HOUR_OF_DAY, 1);
            view.setText(format1.format(calendar.getTime()).toLowerCase());
        } catch (ParseException e) {
            e.printStackTrace();
        }


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
            requestData.setType(AppConstants.IMAGE_TYPE_APPOINTMENT);
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
            controller.getData(AppConstants.FILE_UPLOAD_REQ, requestData);
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

                        //docx, .xlsx, .ppt, .txt, .pdf
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

                            // String imageString = output.toString();


                            //  String ba1 = Base64.encodeBytes(ba);

                            //System.out.println("image " + imageString);

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


    @Override
    public void onBackPressed() {


        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setMessage(getResources().getString(R.string.exit_appointment)).setNegativeButton(getResources().getString(R.string.yes)
                , new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                finish();

            }
        }).setPositiveButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // do nothing
                dialog.cancel();

            }
        }).setIcon(android.R.drawable.ic_dialog_alert);

        AlertDialog alert11 = dialog.create();
        alert11.show();

        alert11.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.home_light_blue));
        alert11.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.canceltxt_color));


        //.show();


    }

}
//
