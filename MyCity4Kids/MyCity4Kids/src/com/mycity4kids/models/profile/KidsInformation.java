package com.mycity4kids.models.profile;

import com.mycity4kids.models.basemodel.BaseDataModel;

public class KidsInformation extends BaseDataModel {

    private String name;
    private String birthday;
    private String color_code;
    private String gender;
    private String dob;

    public String getKidid() {
        return kidid;
    }

    public void setKidid(String kidid) {
        this.kidid = kidid;
    }

    private String kidid;

    public String getColor_code() {
        return color_code;
    }

    public void setColor_code(String color_code) {
        this.color_code = color_code;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    private String id;

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

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

}
