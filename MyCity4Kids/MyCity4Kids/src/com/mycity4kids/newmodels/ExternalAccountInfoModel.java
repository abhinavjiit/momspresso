package com.mycity4kids.newmodels;

import com.kelltontech.model.BaseModel;

/**
 * Created by manish.soni on 06-08-2015.
 */
public class ExternalAccountInfoModel extends BaseModel {

    int id;
    String userId;
    String isFacebook;
    String session;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getIsFacebook() {
        return isFacebook;
    }

    public void setIsFacebook(String isFacebook) {
        this.isFacebook = isFacebook;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }
}
