package com.mycity4kids.ui.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.calendar.CalendarScopes;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseFragment;
import com.kelltontech.utils.StringUtils;
import com.kelltontech.utils.ToastUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.dbtable.ExternalCalendarTable;
import com.mycity4kids.dbtable.TableAppointmentData;
import com.mycity4kids.facebook.FacebookUtils;
import com.mycity4kids.interfaces.IFacebookEvent;
import com.mycity4kids.interfaces.IFacebookUser;
import com.mycity4kids.interfaces.OnListItemClick;
import com.mycity4kids.listener.OnExternalCalenderTapped;
import com.mycity4kids.newmodels.AppoitmentDataModel;
import com.mycity4kids.newmodels.ExternalAccountInfoModel;
import com.mycity4kids.newmodels.ExternalEventModel;
import com.mycity4kids.newmodels.FacebookEventModelNew;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.reminders.AppointmentManager;
import com.mycity4kids.ui.activity.DashboardActivity;
import com.mycity4kids.ui.adapter.ExternalCalendarAdapter;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by manish.soni on 30-07-2015.
 */
public class ExternalCalFragment extends BaseFragment implements OnListItemClick, IFacebookUser {

    ListView calendarListView;
    TextView addCal;
    ExternalCalendarAdapter calendarAdapter;

    public com.google.api.services.calendar.Calendar mService;

    //    com.google.api.services.calendar.Calendar mService;
    public GoogleAccountCredential credential;
    final HttpTransport transport = AndroidHttp.newCompatibleTransport();
    final JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

    private static final String[] SCOPES = {CalendarScopes.CALENDAR_READONLY};
    private ArrayList<String> arrayList;

    ExternalCalendarTable externalCalendarTable;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.aa_external_cal, container, false);
        ((DashboardActivity) getActivity()).setTitle("External Calendars");

        calendarListView = (ListView) view.findViewById(R.id.ext_cal_list);
        addCal = (TextView) view.findViewById(R.id.add_cal);
        externalCalendarTable = new ExternalCalendarTable(BaseApplication.getInstance());

        calendarAdapter = new ExternalCalendarAdapter(getActivity(), externalCalendarTable.getAllExternalUserData(), this);
        calendarListView.setAdapter(calendarAdapter);

        // Google events code Initialize credentials and service object.

        SharedPreferences settings = getActivity().getPreferences(Context.MODE_PRIVATE);
        credential = GoogleAccountCredential.usingOAuth2(
                getActivity(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff())
                .setSelectedAccountName(settings.getString(AppConstants.PREF_ACCOUNT_NAME, null));

        mService = new com.google.api.services.calendar.Calendar.Builder(
                transport, jsonFactory, credential)
                .setApplicationName("Google Calendar API Android Quickstart")
                .build();


        addCal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ImportCalDialogFragment calDialogFragment = new ImportCalDialogFragment();
                calDialogFragment.setListener(new OnExternalCalenderTapped() {
                    @Override
                    public void onExternalCalenderButtonTapped(boolean isFacebook) {
                        if (isFacebook) {
                            fetchFacebookEvents();
                        } else {
                            fetchGoogleEvents();
                        }
                    }
                });
                calDialogFragment.show(getActivity().getFragmentManager(), "Import Calendars");
            }
        });

//        facebook events code....


        return view;
    }

    private void fetchFacebookEvents() {

        showProgressDialog("Fetching events...");

        boolean isSessionActive = FacebookUtils.fetchFacebookEvents(new IFacebookEvent() {
            @Override
            public void onFacebookEventReceived(String response) {

                Session session = Session.getActiveSession();
                if (session != null) {
                    session.closeAndClearTokenInformation();
                }
                Log.e("Facebook events", response);

                if (!StringUtils.isNullOrEmpty(response)) {
                    Type typeOfObjectsList = new TypeToken<ArrayList<FacebookEventModelNew>>() {
                    }.getType();
                    ArrayList<FacebookEventModelNew> values = new Gson().fromJson(response, typeOfObjectsList);

                    new loadExternalEvents().execute(convertFacebookEvents(values));
                } else {

                    ToastUtils.showToast(getActivity(), "No events found.");
                }
            }
        });

        if (!isSessionActive) {
            FacebookUtils.facebookLogin(getActivity(), this);
        }

    }

    @Override
    public void getFacebookUser(GraphUser user) {
        //removeProgressDialog();
        Log.i("facebook", "getfacebookuser");
        try {
            if (user != null) {

                ExternalAccountInfoModel model = new ExternalAccountInfoModel();
                model.setUserId(user.asMap().get("email").toString());
                model.setSession("");
                model.setIsFacebook("true");

                if (!externalCalendarTable.checkAccountExists(model.getUserId(), true)) {
                    externalCalendarTable.insertData(model);
                    ArrayList<ExternalAccountInfoModel> list = calendarAdapter.getUserList();
                    list.add(model);
                    calendarAdapter.setUserList(list);
                }
                fetchFacebookEvents();
                Log.i("facebook", "sucess");
            }
        } catch (Exception e) {
            Log.i("facebook", e.getMessage());

        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    public void fetchGoogleEvents() {
        if (isGooglePlayServicesAvailable()) {
            refreshResults(true);
        } else {
            ToastUtils.showToast(getActivity(), "Google Play Services required: " +
                    "after installing, close and relaunch this app.");
        }
    }

    public void refreshResults(boolean isFirst) {

        if (isFirst) {
            chooseAccount();
        } else {
            if (isDeviceOnline()) {
                new ApiAsyncTask(this).execute();
            } else {
                ToastUtils.showToast(getActivity(), "No network connection available.");
            }
        }
    }

    public void clearResultsText() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
            }
        });
    }

    /**
     * Fill the data TextView with the given List of Strings; called from
     * background threads and async tasks that need to update the UI (in the
     * UI thread).
     *
     * @param eventsList a List of Strings to populate the main TextView with.
     */
    public void updateResultsText(final ArrayList<ExternalEventModel> eventsList) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (eventsList == null) {
                    removeProgressDialog();
                    ToastUtils.showToast(getActivity(), "Error retrieving data!");
                } else if (eventsList.size() == 0) {
                    removeProgressDialog();
                    ToastUtils.showToast(getActivity(), "No events found.");
                } else {
                    //ToastUtils.showToast(getActivity(), eventsList.get(0).getEvent_name().toString());
                    showProgressDialog(Constants.LOAD_EXTERNAL_EVENTS);
                    new loadExternalEvents().execute(eventsList);
                }
            }
        });
    }

    /**
     * Show a status message in the list header TextView; called from background
     * threads and async tasks that need to update the UI (in the UI thread).
     *
     * @param message a String to display in the UI header TextView.
     */
//    public void updateStatus(final String message) {
//        getActivity().runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                mStatusText.setText(message);
//            }
//        });
//    }

    /**
     * Starts an activity in Google Play Services so the user can pick an
     * account.
     */
    public void chooseAccount() {
        getActivity().startActivityForResult(credential.newChooseAccountIntent(), AppConstants.REQUEST_ACCOUNT_PICKER);
    }

    /**
     * Checks whether the device currently has a network connection.
     *
     * @return true if the device has a network connection, false otherwise.
     */
    private boolean isDeviceOnline() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    /**
     * Check that Google Play services APK is installed and up to date. Will
     * launch an error dialog for the user to update Google Play Services if
     * possible.
     *
     * @return true if Google Play Services is available and up to
     * date on this device; false otherwise.
     */
    private boolean isGooglePlayServicesAvailable() {
        final int connectionStatusCode =
                GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity());
        if (GooglePlayServicesUtil.isUserRecoverableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
            return false;
        } else if (connectionStatusCode != ConnectionResult.SUCCESS) {
            return false;
        }
        return true;
    }

    /**
     * Display an error dialog showing that Google Play Services is missing
     * or out of date.
     *
     * @param connectionStatusCode code describing the presence (or lack of)
     *                             Google Play Services on this device.
     */

    public void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Dialog dialog = GooglePlayServicesUtil.getErrorDialog(
                        connectionStatusCode,
                        getActivity(),
                        AppConstants.REQUEST_GOOGLE_PLAY_SERVICES);
                dialog.show();
            }
        });
    }

    @Override
    protected void updateUi(Response response) {

    }

    public void setAccountName(String accountName) {

        showProgressDialog("Fetching events...");

        if (accountName != null) {

            credential.setSelectedAccountName(accountName);

            // check id exists
            if (externalCalendarTable.checkAccountExists(accountName, false)) {
                // show toast
                ToastUtils.showToast(getActivity(), "Account syncing started");
                refreshResults(false);

            } else {
                // insert selected id in db
                ExternalAccountInfoModel model = new ExternalAccountInfoModel();
                model.setUserId(accountName);
                model.setSession("");
                model.setIsFacebook("false");
                externalCalendarTable.insertData(model);
                ArrayList<ExternalAccountInfoModel> list = calendarAdapter.getUserList();
                list.add(model);
                calendarAdapter.setUserList(list);
                calendarAdapter.notifyDataSetChanged();
                refreshResults(false);
            }
        }
    }

    @Override
    public void onItenClicked(View view, int id) {
        externalCalendarTable.deleteBy_Id(id);
        calendarAdapter.updateList(externalCalendarTable.getAllExternalUserData());
        calendarAdapter.notifyDataSetChanged();
    }

    public class loadExternalEvents extends AsyncTask<ArrayList<ExternalEventModel>, Void, Void> {

        @Override
        protected Void doInBackground(ArrayList<ExternalEventModel>... arrayLists) {

            ArrayList<ExternalEventModel> list = arrayLists[0];
            TableAppointmentData tableAppointmentData = new TableAppointmentData(BaseApplication.getInstance());

            for (int i = 0; i < list.size(); i++) {

                if (!StringUtils.isNullOrEmpty(list.get(i).getEvent_name())) {

                    AppoitmentDataModel.AppointmentDetail appointmentDetails = new AppoitmentDataModel().new AppointmentDetail();
                    appointmentDetails.setId(0);
                    appointmentDetails.setExternal_id(list.get(i).getId());
                    appointmentDetails.setAppointment_name(list.get(i).getEvent_name());
                    appointmentDetails.setStarttime(list.get(i).getStarttime());
                    appointmentDetails.setEndtime(list.get(i).getEndtime());
                    appointmentDetails.setLocality(list.get(i).getLocality());
                    appointmentDetails.setReminder("0");
                    appointmentDetails.setIs_recurring("no");
                    appointmentDetails.setRepeate("");
                    appointmentDetails.setRepeate_untill("");
                    appointmentDetails.setRepeate_num("");
                    appointmentDetails.setRepeate_frequency("");
                    appointmentDetails.setOffline_id(1);
                    appointmentDetails.setUser_id(SharedPrefUtils.getUserDetailModel(getActivity()).getId());
                    appointmentDetails.setIs_bday(list.get(i).getIs_bday());
                    appointmentDetails.setIs_holiday(list.get(i).getIs_holiday());
                    appointmentDetails.setIs_google(list.get(i).getIs_google());


                    // find out wether event id exists

                    ArrayList<String> eventIdlist = tableAppointmentData.getExternalEventIdList();

                    if (eventIdlist.contains(list.get(i).getId()))
                        tableAppointmentData.updateData(appointmentDetails);
                    else
                        tableAppointmentData.insertData(appointmentDetails);
                }
            }
            // clear list of appointment manager
            AppointmentManager.getInstance(getActivity()).clearList();

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            removeProgressDialog();
            calendarAdapter.notifyDataSetChanged();
            ToastUtils.showToast(getActivity(), "Event Succesfully Added");
        }
    }

    public ArrayList<ExternalEventModel> convertFacebookEvents(ArrayList<FacebookEventModelNew> values) {
        // List the next 100 events from the primary calendar.
        ArrayList<ExternalEventModel> allEvents = new ArrayList<ExternalEventModel>();
        ArrayList<FacebookEventModelNew> items = values;

        for (FacebookEventModelNew event : items) {

            System.out.println("Facebook event " + event.toString());
            ExternalEventModel googleEventModel = new ExternalEventModel();
            googleEventModel.setId("" + event.getId());
            googleEventModel.setEvent_name(event.getName());


            if (event.getLocation() == null) {
                googleEventModel.setLocality("");
            } else {
                googleEventModel.setLocality(event.getLocation());
            }

            if (event.getStart_time() == null) {
                googleEventModel.setStarttime(System.currentTimeMillis() + 300000);
                googleEventModel.setEndtime(googleEventModel.getStarttime() + 86400000);
            } else {
                try {
                    googleEventModel.setStarttime(convertTimeStamp_new(event.getStart_time()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                googleEventModel.setEndtime(googleEventModel.getStarttime() + 86400000);
            }
            googleEventModel.setEvent_description("");
            allEvents.add(googleEventModel);
        }
        return allEvents;
    }

    public long convertTimeStamp_new(CharSequence date) throws ParseException {

        String currentdate = date.toString();

        SimpleDateFormat formatter = null;
        java.sql.Timestamp timestamp = null;

        if (currentdate.contains("T")) {
            formatter = new SimpleDateFormat("yyy-MM-dd'T'hh:mm:ss");

            Date tempDate = formatter.parse((String) date);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(tempDate);

            if (calendar.get(Calendar.HOUR) == 12 || calendar.get(Calendar.HOUR) == 00) {
                calendar.add(Calendar.MILLISECOND, 300000);
            }
            timestamp = new java.sql.Timestamp(calendar.getTimeInMillis());
        } else {
            formatter = new SimpleDateFormat("yyy-MM-dd");
            Date tempDate = formatter.parse((String) date);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(tempDate);
            calendar.set(Calendar.HOUR_OF_DAY, 9);
            timestamp = new java.sql.Timestamp(calendar.getTimeInMillis());
        }

        Log.d("TimeStamp", String.valueOf(timestamp.getTime()));
        long time_stamp = timestamp.getTime();
        return time_stamp;
    }

}