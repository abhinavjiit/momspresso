package com.mycity4kids.newmodels;

import com.kelltontech.model.BaseModel;
import com.mycity4kids.models.CommonMessage;

import java.util.ArrayList;

/**
 * Created by manish.soni on 13-07-2015.
 */
public class TaskListResponse extends BaseModel {

    private int responseCode;
    private String response;
    private TaskListResult result;

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

    public TaskListResult getResult() {
        return result;
    }

    public void setResult(TaskListResult result) {
        this.result = result;
    }

    public class TaskListResult extends CommonMessage {

        private ArrayList<AllList> data;

        public ArrayList<AllList> getData() {
            return data;
        }

        public void setData(ArrayList<AllList> data) {
            this.data = data;
        }
    }

    public class AllList {

        TaskListModel TaskList;

        public TaskListModel getTaskList() {
            return TaskList;
        }

        public void setTaskList(TaskListModel taskList) {
            TaskList = taskList;
        }
    }

}
