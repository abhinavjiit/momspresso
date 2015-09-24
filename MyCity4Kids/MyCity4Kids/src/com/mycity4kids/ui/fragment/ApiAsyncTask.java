package com.mycity4kids.ui.fragment;

import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.newmodels.ExternalEventModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * An asynchronous task that handles the Google Calendar API call.
 * Placing the API calls in their own task ensures the UI stays responsive.
 */
public class ApiAsyncTask extends AsyncTask<Void, Void, Void> {

    private ExternalCalFragment mFragment;

    /**
     * Constructor.
     *
     * @param fragment MainActivity that spawned this task.
     */
    ApiAsyncTask(Fragment fragment) {

        this.mFragment = (ExternalCalFragment) fragment;
    }

    /**
     * Background task to call Google Calendar API.
     *
     * @param params no parameters needed for this task.
     */
    @Override
    public Void doInBackground(Void... params) {
        try {

            mFragment.clearResultsText();
            mFragment.updateResultsText(getDataFromApi());

        } catch (final GooglePlayServicesAvailabilityIOException availabilityException) {
//            mFragment.showGooglePlayServicesAvailabilityErrorDialog(
//                    availabilityException.getConnectionStatusCode());
            availabilityException.printStackTrace();
            mFragment.updateResultsText(null);
            Log.e("01", availabilityException.toString());
        } catch (UserRecoverableAuthIOException userRecoverableException) {
        mFragment.startActivityForResult(
                userRecoverableException.getIntent(),
                AppConstants.REQUEST_AUTHORIZATION);
        userRecoverableException.printStackTrace();
        // mFragment.updateResultsText(null);
        //mFragment. startActivityForResult(userRecoverableException.getIntent(), mFragment.REQUEST_AUTHORIZATION);
        Log.e("02", userRecoverableException.toString());
    } catch (Exception e) {
//            mFragment.updateStatus("The following error occurred:\n" +
//                    e.getMessage());
            e.printStackTrace();
            mFragment.updateResultsText(null);
            Log.e("03", e.toString());
        }
        return null;
    }

    /**
     * @throws IOException
     */
    public ArrayList<ExternalEventModel> getDataFromApi() throws IOException {
        // List the next 100 events from the primary calendar.
        DateTime now = new DateTime(System.currentTimeMillis());
        ArrayList<ExternalEventModel> allEvents = new ArrayList<ExternalEventModel>();
        Events events = mFragment.mService.events().list("primary")
                .setMaxResults(100)
                .setTimeMin(now)
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .execute();

       // Events events = mFragment.mService.events().list("#contacts@group.v.calendar.google.com").execute();

       // Events events = mFragment.mService.events().list("en.indian#holiday@group.v.calendar.google.com").execute();

        List<Event> items = events.getItems();

        for (Event event : items) {

            System.out.println("google event " + event.toString());

            ExternalEventModel googleEventModel = new ExternalEventModel();

            googleEventModel.setId(event.getId());
            googleEventModel.setEvent_name(event.getSummary());

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

            googleEventModel.setIs_google(1);
            googleEventModel.setIs_bday(0);
            googleEventModel.setIs_holiday(0);


            allEvents.add(googleEventModel);
        }


        // holidays events

        // now we are adding holidays
        Events holidaysEvents =

                mFragment.mService.events().list("en.indian#holiday@group.v.calendar.google.com")
                        .setMaxResults(100)
                        .setTimeMin(now)
                        .setOrderBy("startTime")
                        .setSingleEvents(true)
                        .execute();
               // mFragment.mService.events().list("en.indian#holiday@group.v.calendar.google.com").execute();

        List<Event> holidaysItems = holidaysEvents.getItems();

        for (Event event : holidaysItems) {

            System.out.println("google holiday event " + event.toString());

            ExternalEventModel googleEventModel = new ExternalEventModel();

            googleEventModel.setId(event.getId());
            googleEventModel.setEvent_name(event.getSummary());

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

            googleEventModel.setIs_google(1);
            googleEventModel.setIs_bday(0);
            googleEventModel.setIs_holiday(1);

            allEvents.add(googleEventModel);
        }

        // now we are adding bdayss
        Events bdayEvents =
                mFragment.mService.events().list("#contacts@group.v.calendar.google.com")
                        .setMaxResults(100)
                        .setTimeMin(now)
                        .setOrderBy("startTime")
                        .setSingleEvents(true)
                        .execute();
        //mFragment.mService.events().list("#contacts@group.v.calendar.google.com").execute();

        List<Event> bdayItems = bdayEvents.getItems();

        for (Event event : bdayItems) {

            System.out.println("google bday event " + event.toString());

            ExternalEventModel googleEventModel = new ExternalEventModel();

            googleEventModel.setId(event.getId());
            googleEventModel.setEvent_name(event.getSummary());

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

            googleEventModel.setIs_bday(1);
            googleEventModel.setIs_holiday(0);
            googleEventModel.setIs_google(1);

            allEvents.add(googleEventModel);
        }

        return allEvents;
    }

}