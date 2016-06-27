package com.mycity4kids.newmodels;

import com.kelltontech.model.BaseModel;

/**
 * Created by manish.soni on 16-06-2015.
 */
public class AttendeeModel extends BaseModel {

    String name;
    String colorCode;
    String id;
    String type;
    Boolean check = false;
    int appoitmentId;
    int serverid;
    String externalId;

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public int getServerid() {
        return serverid;
    }

    public void setServerid(int serverid) {
        this.serverid = serverid;
    }

    public AttendeeModel() {


    }

    public int getAppoitmentId() {
        return appoitmentId;
    }

    public void setAppoitmentId(int appoitmentId) {
        this.appoitmentId = appoitmentId;
    }


    public AttendeeModel(String colorCode) {
        this.colorCode = colorCode;
    }

    public AttendeeModel(String id, String type, String name, String code) {
        this.id = id;
        this.type = type;
        this.colorCode = code;
        this.name = name;
    }

    public AttendeeModel(String id, String type, String name, String code, boolean selected) {
        this.id = id;
        this.type = type;
        this.colorCode = code;
        this.name = name;
        this.check = selected;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getCheck() {
        return check;
    }

    public void setCheck(Boolean check) {
        this.check = check;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColorCode() {
        return colorCode;
    }

    public void setColorCode(String colorCode) {
        this.colorCode = colorCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AttendeeModel that = (AttendeeModel) o;

        return id == that.id;

    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
