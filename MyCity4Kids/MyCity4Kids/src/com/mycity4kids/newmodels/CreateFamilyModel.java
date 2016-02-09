package com.mycity4kids.newmodels;

import com.mycity4kids.models.basemodel.BaseDataModel;
import com.mycity4kids.models.profile.KidsInformation;

import java.util.ArrayList;

/**
 * Created by hemant on 5/2/16.
 */
public class CreateFamilyModel extends BaseDataModel {

    private String userId;
    private String userColorCode;
    private String profileImageUrl;
    private String pushToken;
    private String deviceId;
    private String familyName;
    private ArrayList<KidsInformation> kidsInformationArrayList;
    private ArrayList<InvitedUserModel> inviteUserList;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserColorCode() {
        return userColorCode;
    }

    public void setUserColorCode(String userColorCode) {
        this.userColorCode = userColorCode;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public String getPushToken() {
        return pushToken;
    }

    public void setPushToken(String pushToken) {
        this.pushToken = pushToken;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public ArrayList<KidsInformation> getKidsInformationArrayList() {
        return kidsInformationArrayList;
    }

    public void setKidsInformationArrayList(ArrayList<KidsInformation> kidsInformationArrayList) {
        this.kidsInformationArrayList = kidsInformationArrayList;
    }

    public ArrayList<InvitedUserModel> getInviteUserList() {
        return inviteUserList;
    }

    public void setInviteUserList(ArrayList<InvitedUserModel> inviteUserList) {
        this.inviteUserList = inviteUserList;
    }

    public class InvitedUserModel extends BaseDataModel {
        private String name;
        private String email;
        private String mobile;
        private String color_code;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public String getColor_code() {
            return color_code;
        }

        public void setColor_code(String color_code) {
            this.color_code = color_code;
        }


    }
}
