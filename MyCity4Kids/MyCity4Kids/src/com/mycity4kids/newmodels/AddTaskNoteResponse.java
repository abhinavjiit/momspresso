package com.mycity4kids.newmodels;

import com.mycity4kids.models.CommonMessage;

/**
 * Created by manish.soni on 15-07-2015.
 */
public class AddTaskNoteResponse {

    private int responseCode;
    private String response;
    private TaskResult result;
    private boolean isLoggedIn;

    public TaskResult getResult() {
        return result;
    }

    public void setResult(TaskResult result) {
        this.result = result;
    }

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

    public class TaskResult extends CommonMessage {

        private TaskNotes data;

        public TaskNotes getData() {
            return data;
        }

        public void setData(TaskNotes data) {
            this.data = data;
        }
    }

    public void setIsLoggedIn(boolean isLoggedIn) {
        this.isLoggedIn = isLoggedIn;
    }

    public class TaskNotes {

        public TaskDataModel.Notes TaskNote;

        public TaskDataModel.Notes getTaskNote() {
            return TaskNote;
        }

        public void setTaskNote(TaskDataModel.Notes taskNote) {
            TaskNote = taskNote;
        }
    }

}