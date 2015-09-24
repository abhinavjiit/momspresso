package com.mycity4kids.newmodels;

import com.mycity4kids.models.basemodel.BaseDataModel;

/**
 * Created by khushboo.goyal on 22-06-2015.
 */
public class TasksCompletedModel extends BaseDataModel{

    private int taskid;
    private String date;

    public int getTaskid() {
        return taskid;
    }

    public void setTaskid(int taskid) {
        this.taskid = taskid;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}




