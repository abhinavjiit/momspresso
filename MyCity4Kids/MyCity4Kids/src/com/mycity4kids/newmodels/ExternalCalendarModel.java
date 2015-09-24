package com.mycity4kids.newmodels;

import com.kelltontech.model.BaseModel;

/**
 * Created by manish.soni on 30-07-2015.
 */
public class ExternalCalendarModel extends BaseModel {

    String id;
    String userId;
    String colorCode;

    public ExternalCalendarModel(String id, String name, String colorcode) {
        this.id = id;
        this.userId = name;
        this.colorCode = colorcode;

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getColorCode() {
        return colorCode;
    }

    public void setColorCode(String colorCode) {
        this.colorCode = colorCode;
    }
}
