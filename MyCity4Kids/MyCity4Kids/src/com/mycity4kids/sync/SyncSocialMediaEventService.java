package com.mycity4kids.sync;

import android.app.IntentService;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import com.google.gson.Gson;
import com.kelltontech.utils.ConnectivityUtils;
import com.kelltontech.utils.ToastUtils;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.dbtable.ExternalCalendarTable;
import com.mycity4kids.dbtable.TableAdult;
import com.mycity4kids.dbtable.TableKids;
import com.mycity4kids.models.user.KidsInfo;
import com.mycity4kids.models.user.UserModel;
import com.mycity4kids.models.user.UserResponse;
import com.mycity4kids.newmodels.ExternalAccountInfoModel;
import com.mycity4kids.newmodels.ExternalCalendarModel;
import com.mycity4kids.newmodels.ExternalEventModel;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.ui.fragment.ExternalCalFragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by manish.soni on 04-08-2015.
 */
public class SyncSocialMediaEventService extends IntentService {

    private final static String TAG = SyncSocialMediaEventService.class.getSimpleName();
    private ExternalCalendarTable externalTable;
    public com.google.api.services.calendar.Calendar mService;
    final HttpTransport transport = AndroidHttp.newCompatibleTransport();
    final JsonFactory jsonFactory = GsonFactory.getDefaultInstance();
    public GoogleAccountCredential credential;
    private static final String[] SCOPES = {CalendarScopes.CALENDAR_READONLY};

    public SyncSocialMediaEventService() {
        super(TAG);
        mService = new com.google.api.services.calendar.Calendar.Builder(
                transport, jsonFactory, credential)
                .setApplicationName("Google Calendar API Android Quickstart")
                .build();
    }

    public boolean checkSyncTime() {

        long syncTime = SharedPrefUtils.getSocialEventsTimeSatmp(this);

        Calendar calender = Calendar.getInstance();
        calender.setTime(new Date(syncTime));
        calender.add(Calendar.DAY_OF_MONTH, 7);

        syncTime = calender.getTimeInMillis();

        if(System.currentTimeMillis()>=syncTime)
            return  true;

        return false;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (ConnectivityUtils.isNetworkEnabled(this)) {
            // get account from db

            if (checkSyncTime()) {

                externalTable = new ExternalCalendarTable(BaseApplication.getInstance());
                ArrayList<ExternalAccountInfoModel> accountList = externalTable.getAllExternalUserData();

                for (ExternalAccountInfoModel model : accountList) {

                    if (model.getIsFacebook().equalsIgnoreCase("false")) {
                        // google
                        credential = GoogleAccountCredential.usingOAuth2(
                                BaseApplication.getInstance(), Arrays.asList(SCOPES))
                                .setBackOff(new ExponentialBackOff())
                                .setSelectedAccountName(model.getUserId());
                        new GoogleEventsAsyncTask().execute();


                    } else if (model.getIsFacebook().equalsIgnoreCase("true")) {
                        // facebook
                    }
                }
            }

        }
    }


    public void updateResultsText(final ArrayList<ExternalEventModel> eventsList) {

        new AddExternalEvents().execute(eventsList);
        // save timestamp in prefrences
        SharedPrefUtils.setSocialEventsTimeSatmp(this, System.currentTimeMillis());

    }


    // api fetching event class
    public class GoogleEventsAsyncTask extends AsyncTask<Void, Void, Void> {
        /**
         * Background task to call Google Calendar API.
         *
         * @param params no parameters needed for this task.
         */
        @Override
        public Void doInBackground(Void... params) {
            try {
                updateResultsText(getDataFromApi());

            } catch (final GooglePlayServicesAvailabilityIOException availabilityException) {


            } catch (UserRecoverableAuthIOException userRecoverableException) {


            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * Fetch a list of the next 10 events from the primary calendar.
         *
         * @return List of Strings describing returned events.
         * @throws IOException
         */
        public ArrayList<ExternalEventModel> getDataFromApi() throws IOException {
            // List the next 100 events from the primary calendar.
            DateTime now = new DateTime(System.currentTimeMillis());
            ArrayList<ExternalEventModel> allEvents = new ArrayList<ExternalEventModel>();
            Events events = mService.events().list("primary")
                    .setMaxResults(100)
                    .setTimeMin(now)
                    .setOrderBy("startTime")
                    .setSingleEvents(true)
                    .execute();

            List<Event> items = events.getItems();

            for (Event event : items) {

                // System.out.println("google event " + event.toString());

                ExternalEventModel googleEventModel = new ExternalEventModel();

                googleEventModel.setId(event.getId());
                googleEventModel.setEvent_name(event.getSummary());

                // System.out.println("event date " + event.getStart().getDate());

                //System.out.println("event datetime "+event.getStart().getDateTime());

                //System.out.println("event timezone "+event.getStart().getTimeZone());

                DateTime startDate = event.getStart().getDateTime();
                DateTime endDate = event.getEnd().getDateTime();
                if (startDate == null) {
                    startDate = event.getStart().getDate();
                    if (startDate == null)
                        googleEventModel.setStarttime(System.currentTimeMillis() + 86400);
                    else
                        googleEventModel.setStarttime(startDate.getValue());
                } else {
                    googleEventModel.setStarttime(startDate.getValue());
                }
                if (endDate == null) {
                    endDate = event.getEnd().getDate();
                    if (endDate == null)
                        googleEventModel.setEndtime(System.currentTimeMillis() + 86400);
                    else
                        googleEventModel.setEndtime(endDate.getValue());
                } else {
                    googleEventModel.setEndtime(endDate.getValue());
                }
                if (event.getLocation() == null) {
                    googleEventModel.setLocality("");
                } else {
                    googleEventModel.setLocality(event.getLocation());
                }
                if (event.getDescription() == null) {
                    googleEventModel.setEvent_description("");
                } else {
                    googleEventModel.setEvent_description(event.getDescription());
                }

                // log here for gooogle events
                //String data = new Gson().toJson(googleEventModel);
                //System.out.println("google data "+data);
                allEvents.add(googleEventModel);
            }
            return allEvents;
        }

    }

}
