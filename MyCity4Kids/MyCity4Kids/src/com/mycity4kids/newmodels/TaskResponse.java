package com.mycity4kids.newmodels;

import com.mycity4kids.models.CommonMessage;
import com.mycity4kids.models.basemodel.BaseDataModel;

/**
 * Created by khushboo.goyal on 25-06-2015.
 */
public class TaskResponse extends BaseDataModel {

    private int responseCode;
    private String response;
    private TaskResult result;

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

    public TaskResult getResult() {
        return result;
    }

    public void setResult(TaskResult result) {
        this.result = result;
    }

    public class TaskResult extends CommonMessage {

        private TaskDataModel data;

        public TaskDataModel getData() {
            return data;
        }

        public void setData(TaskDataModel data) {
            this.data = data;
        }
    }

}


