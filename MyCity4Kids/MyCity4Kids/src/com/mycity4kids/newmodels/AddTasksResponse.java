package com.mycity4kids.newmodels;

import com.mycity4kids.models.CommonMessage;
import com.mycity4kids.models.basemodel.BaseDataModel;

/**
 * Created by khushboo.goyal on 25-06-2015.
 */
public class AddTasksResponse extends BaseDataModel {

    private int responseCode;
    private String response;
    private AppontmentResult result;

    public AppontmentResult getResult() {
        return result;
    }

    public void setResult(AppontmentResult result) {
        this.result = result;
    }

    private boolean isLoggedIn;

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public void setLoggedIn(boolean isLoggedIn) {
        this.isLoggedIn = isLoggedIn;
    }


    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public class AppontmentResult extends CommonMessage {

        private AppoitmentNotes data;


        public AppoitmentNotes getData() {
            return data;
        }

        public void setData(AppoitmentNotes data) {
            this.data = data;
        }
    }

    public void setIsLoggedIn(boolean isLoggedIn) {
        this.isLoggedIn = isLoggedIn;
    }

    public class AppoitmentNotes {
        public AppoitmentDataModel.Notes AppointmentNote;

        public AppoitmentDataModel.Notes getAppointmentNote() {
            return AppointmentNote;
        }

        public void setAppointmentNote(AppoitmentDataModel.Notes appointmentNote) {
            AppointmentNote = appointmentNote;
        }
    }

}


