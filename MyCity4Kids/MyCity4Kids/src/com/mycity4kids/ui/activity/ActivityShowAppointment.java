package com.mycity4kids.ui.activity;

import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.kelltontech.utils.ConnectivityUtils;
import com.kelltontech.utils.StringUtils;
import com.kelltontech.utils.ToastUtils;
import com.mycity4kids.HorizontalListView.HorizontalListView;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.controller.AddNotesController;
import com.mycity4kids.controller.AppointmentDeleteController;
import com.mycity4kids.dbtable.TableAdult;
import com.mycity4kids.dbtable.TableApiEvents;
import com.mycity4kids.dbtable.TableAppointmentData;
import com.mycity4kids.dbtable.TableAttendee;
import com.mycity4kids.dbtable.TableFile;
import com.mycity4kids.dbtable.TableKids;
import com.mycity4kids.dbtable.TableNotes;
import com.mycity4kids.dbtable.TableTaskData;
import com.mycity4kids.dbtable.TableWhoToRemind;
import com.mycity4kids.googlemap.GeocodingLocation;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.user.KidsInfo;
import com.mycity4kids.newmodels.AddNotesResponse;
import com.mycity4kids.newmodels.AppointmentMappingModel;
import com.mycity4kids.newmodels.AppointmentResponse;
import com.mycity4kids.newmodels.AppoitmentDataModel;
import com.mycity4kids.newmodels.AttendeeModel;
import com.mycity4kids.newmodels.NotesModel;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.reminders.AppointmentManager;
import com.mycity4kids.reminders.Reminder;
import com.mycity4kids.ui.adapter.AdapterHorizontalList;
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
import java.util.Arrays;
import java.util.Date;


public class ActivityShowAppointment extends BaseActivity implements View.OnClickListener {

    GoogleMap googleMap;
    AdapterHorizontalList horizontalAdapter;
    HorizontalListView imageView;
    TextView title, startTime, startDate, endTime, endDate, appointmentAddress, repeats, remanider, attendees;
    TableAppointmentData tableAppointment;
    TableAdult adult;
    TableKids kids;
    // ImageView delete;
    int pId;
    float lat;
    float lng;
    LatLng location;
    private int appointmentid;
    private String exteranlAppointmentid = "";
    private Toolbar mToolbar;
    private CustomListView notesListView;
    private CustomListView listviewAttachment;
    private CustomListView listviewImages;
    private ArrayList<AppoitmentDataModel.Files> imageList;
    private ArrayList<AppoitmentDataModel.Files> documentList;
    private AddImagesAdapter imgadapter;
    private AddDocumentAdapter documentadapter;
    private ArrayList<String> documentUrl;
    private ScrollView scrollview;
    private LinearLayout attachmentLayout;
    private AppoitmentDataModel.AppointmentDetail appDetail = null;
    private ArrayList<AttendeeModel> attendeeList;
    private CardView docLayout;


    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aa_show_appointment);
        Utils.pushOpenScreenEvent(ActivityShowAppointment.this, "Appointment Detail", SharedPrefUtils.getUserDetailModel(this).getId() + "");
        NotificationManager nMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nMgr.cancel(getIntent().getIntExtra(AppConstants.NOTIFICATION_ID, 0));


        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Appointment");


        docLayout = (CardView) findViewById(R.id.attach_layout);
        title = (TextView) findViewById(R.id.appointment_title);
        startTime = (TextView) findViewById(R.id.start_time);
        startDate = (TextView) findViewById(R.id.start_date);
        endTime = (TextView) findViewById(R.id.end_time);
        endDate = (TextView) findViewById(R.id.end_date);
        appointmentAddress = (TextView) findViewById(R.id.appointment_address);
        repeats = (TextView) findViewById(R.id.repeats);
        remanider = (TextView) findViewById(R.id.reminder);
        // notes = (TextView) findViewById(R.id.notes);
        attendees = (TextView) findViewById(R.id.attendees);
        attachmentLayout = (LinearLayout) findViewById(R.id.attachments_layout);

        scrollview = (ScrollView) findViewById(R.id.scroll_view);
        listviewAttachment = (CustomListView) findViewById(R.id.list_attachment);
        listviewImages = (CustomListView) findViewById(R.id.list_images);

        listviewImages.isExpanded();
        listviewAttachment.isExpanded();

        notesListView = (CustomListView) findViewById(R.id.noteslist);

        //scrollToTop();
        scrollview.smoothScrollTo(0, 0);

        findViewById(R.id.addnotes).setOnClickListener(this);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        tableAppointment = new TableAppointmentData(BaseApplication.getInstance());
        kids = new TableKids(BaseApplication.getInstance());
        adult = new TableAdult(BaseApplication.getInstance());


    }

    @Override
    protected void onResume() {
        super.onResume();


        Intent intent = getIntent();
        appointmentid = intent.getIntExtra(AppConstants.EXTRA_APPOINTMENT_ID, 0);
        exteranlAppointmentid = intent.getStringExtra(AppConstants.EXTERNAL_APPOINTMENT_ID);

        // isShare = intent.getBooleanExtra(AppConstants.EXTRA_SHARE, false);

        // appointment table
        TableAppointmentData appointmentTable = new TableAppointmentData(BaseApplication.getInstance());

        if (appointmentid == 0) {
            attachmentLayout.setVisibility(View.GONE);
            docLayout.setVisibility(View.GONE);
            attendees.setText(Html.fromHtml((SharedPrefUtils.getUserDetailModel(this).getFirst_name()).toString()));
            attendees.setTextColor(Color.parseColor(SharedPrefUtils.getUserDetailModel(this).getColor_code()));
            appDetail = appointmentTable.getDataByAppointment(appointmentid, exteranlAppointmentid, true);

        } else {
            attachmentLayout.setVisibility(View.VISIBLE);
            appDetail = appointmentTable.getDataByAppointment(appointmentid, exteranlAppointmentid, false);
        }

        // get from attendee table
        TableAttendee attendeeTable = new TableAttendee(BaseApplication.getInstance());
        attendeeList = attendeeTable.getDataByAppointment(appointmentid);


        // get from whotoRemond table
        TableWhoToRemind whotoRemindTable = new TableWhoToRemind(BaseApplication.getInstance());
        ArrayList<AttendeeModel> whoToRemindList = whotoRemindTable.getDataByAppointment(appointmentid);

        // get from FILES

        imageList = new ArrayList<>();
        documentList = new ArrayList<>();
        documentUrl = new ArrayList<>();

        TableFile fileTable = new TableFile(BaseApplication.getInstance());
        ArrayList<AppoitmentDataModel.Files> fileList = fileTable.getDataByAppointment(appointmentid);

        if (!fileList.isEmpty()) {
            for (AppoitmentDataModel.Files model : fileList) {
                if (model.getFile_type().equalsIgnoreCase("image")) {
                    imageList.add(model);
                } else {

                    documentList.add(model);
                    documentUrl.add(model.getUrl());
                }
            }
        }


        if (imageList.isEmpty())
            findViewById(R.id.image_text).setVisibility(View.GONE);
        else
            findViewById(R.id.image_text).setVisibility(View.VISIBLE);

        if (documentList.isEmpty())
            findViewById(R.id.attachments_text).setVisibility(View.GONE);
        else
            findViewById(R.id.attachments_text).setVisibility(View.VISIBLE);

        // adapetrs for attachements and images

        imgadapter = new AddImagesAdapter(this, imageList, null, this, true, false);
        listviewImages.setAdapter(imgadapter);

        documentadapter = new AddDocumentAdapter(this, documentList, null, this, true, false);
        listviewAttachment.setAdapter(documentadapter);

        // note

        TableNotes notesTable = new TableNotes(BaseApplication.getInstance());
        ArrayList<AppoitmentDataModel.Notes> noteList = notesTable.getDataByAppointment(appointmentid);

        ArrayList<NotesModel> notemodellist = new ArrayList<>();

        for (int i = 0; i < noteList.size(); i++) {

            NotesModel model = new NotesModel();
            model.setMsg(noteList.get(i).getNote());
            String addedby = new TableAdult(BaseApplication.getInstance()).getAdults(noteList.get(i).getUser_id()).getFirst_name();
            model.setAddedby(addedby);

            notemodellist.add(model);
        }

        if (noteList.isEmpty())
            findViewById(R.id.notes_text).setVisibility(View.GONE);

        AdapterNotes adapterNotsList = new AdapterNotes(this, notemodellist);
        notesListView.setAdapter(adapterNotsList);


        googleMap = ((MapFragment) getFragmentManager().findFragmentById(
                R.id.map)).getMap();


        // horizontalAdapter = new AdapterHorizontalList(getApplicationContext(), list);
        //imageView.setAdapter(horizontalAdapter);

        // setting values

        if (appDetail != null) {
            title.setText(appDetail.getAppointment_name());
            startTime.setText(getTime(appDetail.getStarttime()));
            startDate.setText(getDate(appDetail.getStarttime()));

            endTime.setText(getTime(appDetail.getEndtime()));
            endDate.setText(getDate(appDetail.getEndtime()));


            appointmentAddress.setText(appDetail.getLocality());

            String address = appDetail.getLocality();

            GeocodingLocation locationAddress = new GeocodingLocation();
            locationAddress.getAddressFromLocation(address,
                    getApplicationContext(), new GeocoderHandler());

            location = new LatLng(lat, lng);
            Log.d("location ", String.valueOf(location));

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
            if (appDetail.getIs_recurring().equals("yes")) {


                StringBuilder stringBuilder = new StringBuilder();

                //stringBuilder.append("Every ");

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
        getMenuInflater().inflate(R.menu.show_appointment, menu);
        return true;
    }

//    public void scrollToTop() {
//        scrollview.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                scrollview.post(new Runnable() {
//                    public void run() {
//                        scrollview.fullScroll(View.FOCUS_UP);
//                    }
//                });
//            }
//        });
//    }
//
//    public void scrollToBottom()
//    {
//        scrollview.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                scrollview.post(new Runnable() {
//                    public void run() {
//                        scrollview.fullScroll(View.FOCUS_DOWN);
//                    }
//                });
//            }
//        });
//    }

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

//                Uri uriToImage = null;
//
//                ArrayList<Uri> imageUris = new ArrayList<Uri>();
//                for (String url : imageList) {
//                    // imageUris.add(Uri.parse(url));
//                    uriToImage = Uri.parse(url);
//                    break;
//                }
//
//                Intent sendIntent = new Intent();
//                sendIntent.setAction(Intent.ACTION_SEND);
//                sendIntent.putExtra(Intent.EXTRA_TEXT, "Appointment :" + title.getText().toString() + "\n AT " + appointmentAddress.getText().toString() + "\n Starting time " + startDate.getText().toString() + " " + startTime.getText().toString() + " \n End time " + endDate.getText().toString() + " " + endTime.getText().toString());
//
//                String type = "text/plain";
////                if (uriToImage != null) {
////                    sendIntent.putExtra(Intent.EXTRA_STREAM, uriToImage);
////                    type = "image/*";
////                }
//                sendIntent.setType(type);
//                startActivity(Intent.createChooser(sendIntent, "Share Appointment"));

                return true;

            case R.id.edit:

                if (appointmentid != 0) {
                    Intent i = new Intent(this, ActivityEditAppointment.class);
                    i.putExtra(AppConstants.EXTRA_APPOINTMENT_ID, appointmentid);
                    startActivityForResult(i, 1);
                } else {
                    showToast(getResources().getString(R.string.social_media_edit));
                }
                return true;

            case R.id.delete:

                if (ConnectivityUtils.isNetworkEnabled(this)) {
                    if (appointmentid != 0) {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

                        dialog.setMessage(getResources().getString(R.string.delete_appointment)).setNegativeButton(R.string.new_yes
                                , new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                dialog.cancel();

                                AppoitmentDataModel.AppointmentDetail model = new AppoitmentDataModel().new AppointmentDetail();
                                model.setId(appointmentid);

                                showProgressDialog(getString(R.string.please_wait));

                                AppointmentDeleteController _controller = new AppointmentDeleteController(ActivityShowAppointment.this, ActivityShowAppointment.this);
                                _controller.getData(AppConstants.DELETE_APPOINTEMT_REQUEST, model);

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
                        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

                        dialog.setMessage(getResources().getString(R.string.delete_appointment)).setNegativeButton(R.string.new_yes
                                , new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                dialog.cancel();

                                tableAppointment.deleteAppointment("" + exteranlAppointmentid, true);


                                if (AppointmentManager.getInstance(ActivityShowAppointment.this).getAppointmentMap() != null) {
                                    AppointmentManager.getInstance(ActivityShowAppointment.this).removeAppointmentDataFromMap(new AppointmentMappingModel(exteranlAppointmentid), true);
                                }

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


                } else {
                    ToastUtils.showToast(this, getString(R.string.error_network));
                }


                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private String getShareTime(long timeStampStr) {

        try {
            DateFormat sdf = new SimpleDateFormat("hh:mm a");
            Date netDate = (new Date(timeStampStr));
            return sdf.format(netDate).toLowerCase();
        } catch (Exception ex) {
            return "xx";
        }
    }

    private String getShareDate(long timeStampStr) {

        try {
            DateFormat sdf = new SimpleDateFormat("dd MMMM, yyyy");
            Date netDate = (new Date(timeStampStr));
            return sdf.format(netDate);
        } catch (Exception ex) {
            return "xx";
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

        //sendIntent.putExtra(Intent.EXTRA_TEXT, "Appointment: " + appDetail.getAppointment_name().toString() + "\nStart Time: " + getDate(appDetail.getStarttime()) + ", " + getTime(appDetail.getStarttime()) + " \nEnd Time: " + getDate(appDetail.getEndtime()) + ", " + getTime(appDetail.getEndtime()));
        if (StringUtils.isNullOrEmpty(appDetail.getLocality().toString()))
            sendIntent.putExtra(Intent.EXTRA_TEXT, "Appointment: " + title.getText().toString() + "\nStart Time: " + getShareDate(appDetail.getStarttime()) + ", " + getShareTime(appDetail.getStarttime()) + " \nEnd Time: " + getShareDate(appDetail.getEndtime()) + ", " + getShareTime(appDetail.getEndtime()));
        else
            sendIntent.putExtra(Intent.EXTRA_TEXT, "Appointment: " + title.getText().toString() +"\nStart Time: " + getShareDate(appDetail.getStarttime()) + ", " + getShareTime(appDetail.getStarttime()) + " \nEnd Time: " + getShareDate(appDetail.getEndtime()) + ", " + getShareTime(appDetail.getEndtime()) + "\nLocation: " + appointmentAddress.getText().toString());
        startActivity(Intent.createChooser(sendIntent, "Share Appointment"));
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
            showToast(getString(R.string.server_error));
            return;
        }

        switch (response.getDataType()) {

            case AppConstants.ADD_NOTES_REQ:
                try {
                    AddNotesResponse responseData = (AddNotesResponse) response.getResponseObject();

                    if (responseData.getResponseCode() == 200) {

                        showToast(responseData.getResult().getMessage());

                        // save in db
                        TableNotes NoteTable = new TableNotes((BaseApplication) getApplicationContext());
                        NoteTable.AddNotes(responseData.getResult().getData().getAppointmentNote().getNote(), responseData.getResult().getData().getAppointmentNote().getAppointment_id(), responseData.getResult().getData().getAppointmentNote().getUser_id(), responseData.getResult().getData().getAppointmentNote().getId());

                        // update listview
                        TableNotes notesTable = new TableNotes(BaseApplication.getInstance());
                        ArrayList<AppoitmentDataModel.Notes> noteList = notesTable.getDataByAppointment(appointmentid);

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


                        // update in appoitment manager too
                        if (AppointmentManager.getInstance(this).getAppointmentMap() != null) {
                            AppoitmentDataModel.AppointmentData appointmentData = new AppoitmentDataModel().new AppointmentData();
                            appointmentData.setAppointmentAttendee(new TableAttendee(BaseApplication.getInstance()).getData(appointmentid));
                            appointmentData.setAppointment(appDetail);
                            AppointmentManager.getInstance(this).createAppointmentMappingModelAndUpdate(appointmentData, true);
                        }
                        removeProgressDialog();
                    } else if (responseData.getResponseCode() == 400) {
                        showToast(responseData.getResult().getMessage());
                        removeProgressDialog();
                    }


                } catch (Exception e) {

                }
                break;

            default:
                break;


            case AppConstants.DELETE_APPOINTEMT_REQUEST:

                try {
                    AppointmentResponse responseData = (AppointmentResponse) response.getResponseObject();
                    if (responseData.getResponseCode() == 200) {

                        // delete  in db
                        tableAppointment = new TableAppointmentData(BaseApplication.getInstance());
                        tableAppointment.deleteAppointment(appointmentid + "", false);


                        // delete from api event table
                        TableApiEvents apiEventsTable = new TableApiEvents(BaseApplication.getInstance());
                        apiEventsTable.deleteAppointment("" + appointmentid);

                        // get from attendee table
                        TableAttendee attendeeTable = new TableAttendee(BaseApplication.getInstance());
                        attendeeTable.deleteAppointment(appointmentid);


                        // get from whotoRemond table
                        TableWhoToRemind whotoRemindTable = new TableWhoToRemind(BaseApplication.getInstance());
                        whotoRemindTable.deleteAppointment(appointmentid);

                        // get from FILES

                        TableFile fileTable = new TableFile(BaseApplication.getInstance());
                        fileTable.deleteAppointment(appointmentid);
                        // note

                        TableNotes notesTable = new TableNotes(BaseApplication.getInstance());
                        notesTable.deleteAppointment(appointmentid);

                        showToast(responseData.getResult().getMessage());

                        try {
                            // check here count
                            TableAppointmentData data = new TableAppointmentData(BaseApplication.getInstance());
                            TableTaskData taskData = new TableTaskData(BaseApplication.getInstance());
                            int count = data.getRowsCount() + taskData.getRowsCount();
                            if (count > 0) {
                                SharedPrefUtils.setHomeCheckFlag(this, true);
                            } else {
                                SharedPrefUtils.setHomeCheckFlag(this, false);
                            }

                            Reminder.with(ActivityShowAppointment.this).cancel(appointmentid);
                            if (AppointmentManager.getInstance(this).getAppointmentMap() != null) {
                                AppointmentManager.getInstance(this).removeAppointmentDataFromMap(new AppointmentMappingModel(appointmentid), true);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
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
            model.setAppointmentid(appointmentid);


            if (ConnectivityUtils.isNetworkEnabled(this)) {
                showProgressDialog(getString(R.string.please_wait));

                AddNotesController _controller = new AddNotesController(this, this);
                _controller.getData(AppConstants.ADD_NOTES_REQ, model);
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
                bundle.putString("ifTask", "app");
                dialogFragment.setArguments(bundle);
                dialogFragment.show(getFragmentManager(), "notes");

                break;
            case R.id.image:

                Object object = v.getTag();
                if (object != null && object instanceof Integer) {
                    Integer _position = (Integer) v.getTag();
                    Intent intent = new Intent(ActivityShowAppointment.this, ActivityViewPager.class);
                    intent.putExtra("imagelist", imageList);
                    intent.putExtra("position", _position);
                    intent.putExtra("isfrmAppointment", true);
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

    private class GeocoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            String locationAddress;
            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    locationAddress = bundle.getString("address");
                    break;
                default:
                    locationAddress = null;
            }
//            latLongTV.setText(locationAddress);
            Log.d("map location", locationAddress);

            ArrayList<String> items =
                    new ArrayList<String>(Arrays.asList(locationAddress.split(",")));

            lat = Float.parseFloat(items.get(0));
            lng = Float.parseFloat(items.get(1));

            Log.d("lat long location", lat + " " + lng);

            try {
                MarkerOptions marker = new MarkerOptions().position(new LatLng(lat, lng)).title("");

                if (marker != null)
                    // blue color icon
                    marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));

                if (googleMap != null)
                    googleMap.addMarker(marker);


                CameraPosition cameraPosition = new CameraPosition.Builder().target(
                        new LatLng(lat, lng)).zoom(12).build();

                if (googleMap != null)
                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public String attendeeString(ArrayList<AttendeeModel> attendeeList) {

        //String text = "<font color=#cc0029>Erste Farbe</font> <font color=#ffcc00>zweite Farbe</font>";
//        yourtextview.setText(Html.fromHtml(text));

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
                    if (StringUtils.isNullOrEmpty(attendeeList.get(i).getName())) {
                        continue;
                    }
                    String name = "<font color=" + attendeeList.get(i).getColorCode() + ">" + attendeeList.get(i).getName().toString().trim() + "</font>";
                    attendeeS.append((name));
                    flag = true;
                }
            }
        }

        return attendeeS.toString();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
        setResult(RESULT_OK, intent);
        finish();

    }
}
