package com.mycity4kids.newmodels;

import com.mycity4kids.models.CommonMessage;

/**
 * Created by hemant on 1/2/16.
 */
public class UserInviteResult extends CommonMessage {
    private UserInviteModel data;

    public UserInviteModel getData() {
        return data;
    }

    public void setData(UserInviteModel data) {
        this.data = data;
    }
}
