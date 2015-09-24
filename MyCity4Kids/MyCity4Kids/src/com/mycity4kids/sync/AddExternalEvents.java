package com.mycity4kids.sync;

import android.os.AsyncTask;

import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.dbtable.TableAppointmentData;
import com.mycity4kids.newmodels.AppoitmentDataModel;
import com.mycity4kids.newmodels.ExternalEventModel;
import com.mycity4kids.preference.SharedPrefUtils;

import java.util.ArrayList;

/**
 * Created by user on 09-08-2015.
 */

public class AddExternalEvents extends AsyncTask<ArrayList<ExternalEventModel>, Void, Void> {

    @Override
    protected Void doInBackground(ArrayList<ExternalEventModel>... arrayLists) {

        ArrayList<ExternalEventModel> list = arrayLists[0];
        TableAppointmentData tableAppointmentData = new TableAppointmentData(BaseApplication.getInstance());

        for (int i = 0; i < list.size(); i++) {
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
            appointmentDetails.setUser_id(SharedPrefUtils.getUserDetailModel(BaseApplication.getInstance()).getId());

            // find out wether event id exists
            ArrayList<String> eventIdlist = tableAppointmentData.getExternalEventIdList();
            //System.out.println("existing ids "+eventIdlist.toString());
            // System.out.println("inserted ids "+list.get(i).getId());
            if (eventIdlist.contains(list.get(i).getId()))
                tableAppointmentData.updateData(appointmentDetails);
            else
                tableAppointmentData.insertData(appointmentDetails);
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        // TODO Auto-generated method stub
        super.onPostExecute(result);
    }
}