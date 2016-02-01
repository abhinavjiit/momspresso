package com.chatPlatform.models;

import com.mycity4kids.models.basemodel.BaseDataModel;

/**
 * Created by hemant on 13/1/16.
 */
public class HomeworkModel extends BaseDataModel {

    private String id;
    private String name;

    public HomeworkModel(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
