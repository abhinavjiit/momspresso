package com.mycity4kids.newmodels;

/**
 * Created by khushboo.goyal on 22-06-2015.
 */
public class NotesModel {

    private String msg;
    private String addedby;
    private int id;
    private String userid;
    private int appointmentid;


    public int getAppointmentid() {
        return appointmentid;
    }

    public void setAppointmentid(int appointmentid) {
        this.appointmentid = appointmentid;
    }

    public String getMsg() {
        return msg;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getAddedby() {
        return addedby;
    }

    public void setAddedby(String addedby) {
        this.addedby = addedby;
    }
}




