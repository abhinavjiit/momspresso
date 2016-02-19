package com.mycity4kids.models.profile;

import com.mycity4kids.models.basemodel.BaseDataModel;

import java.util.ArrayList;

public class SignUpModel extends BaseDataModel {


    private Family Family;
    private ArrayList<KidsInformation> KidInformation;
    private ArrayList<User> User;

    public ArrayList<KidsInformation> getKidInformation() {
        return KidInformation;
    }

    public void setKidInformation(ArrayList<KidsInformation> kidInformation) {
        KidInformation = kidInformation;
    }

    public ArrayList<SignUpModel.User> getUser() {
        return User;
    }

    public void setUser(ArrayList<SignUpModel.User> user) {
        User = user;
    }

    public Family getFamily() {
        return Family;
    }

    public void setFamily(Family family) {
        this.Family = family;
    }

    public class Family extends BaseDataModel {

        private String family_image;
        private String family_name;
        private String family_password;
        private String pincode;
        private String confirm_password;
        private String family_city;
        public String getFamily_name() {
            return family_name;
        }

        public void setFamily_name(String family_name) {
            this.family_name = family_name;
        }

        public String getConfirm_password() {
            return confirm_password;
        }

        public void setConfirm_password(String confirm_password) {
            this.confirm_password = confirm_password;
        }

        public String getFamily_city() {
            return family_city;
        }

        public void setFamily_city(String family_city) {
            this.family_city = family_city;
        }

        public String getFamily_image() {
            return family_image;
        }

        public void setFamily_image(String family_image) {
            this.family_image = family_image;
        }

        public String getFamily_password() {
            return family_password;
        }

        public void setFamily_password(String family_password) {
            this.family_password = family_password;
        }

        public String getPincode() {
            return pincode;
        }

        public void setPincode(String pincode) {
            this.pincode = pincode;
        }


    }

    public class User extends BaseDataModel {

        private String username;
        private String token_secret;
        private String outh_token;
        private String g_id;
        private String notification_app;
        private String notification_task;

        public String getProfile_image() {
            return profile_image;
        }

        public void setProfile_image(String profile_image) {
            this.profile_image = profile_image;
        }

        private String facebook_id;
        private String profile_image;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        private String id;

        public String getPincode() {
            return pincode;
        }

        public void setPincode(String pincode) {
            this.pincode = pincode;
        }

        private String email;
        private String mobile;
        private String color_code;
        private String pincode;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getToken_secret() {
            return token_secret;
        }

        public void setToken_secret(String token_secret) {
            this.token_secret = token_secret;
        }

        public String getOuth_token() {
            return outh_token;
        }

        public void setOuth_token(String outh_token) {
            this.outh_token = outh_token;
        }

        public String getG_id() {
            return g_id;
        }

        public void setG_id(String g_id) {
            this.g_id = g_id;
        }

        public String getFacebook_id() {
            return facebook_id;
        }

        public void setFacebook_id(String facebook_id) {
            this.facebook_id = facebook_id;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getColor_code() {
            return color_code;
        }

        public void setColor_code(String color_code) {
            this.color_code = color_code;
        }

        public String getNotification_app() {
            return notification_app;
        }

        public void setNotification_app(String notification_app) {
            this.notification_app = notification_app;
        }

        public String getNotification_task() {
            return notification_task;
        }

        public void setNotification_task(String notification_task) {
            this.notification_task = notification_task;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }
    }

}
