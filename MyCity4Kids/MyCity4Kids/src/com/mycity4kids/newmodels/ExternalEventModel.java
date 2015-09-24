package com.mycity4kids.newmodels;

/**
 * Created by manish.soni on 05-08-2015.
 */
public class ExternalEventModel {

    String id;
    String event_name = "";
    String event_description = "";
    String locality = "";
    long starttime = 0;
    long endtime = 0;
    private boolean isfromEvents;



    private int is_holiday;
    private int is_bday;
    private int is_google;

    public int getIs_google() {
        return is_google;
    }

    public void setIs_google(int is_google) {
        this.is_google = is_google;
    }

    public int getIs_holiday() {
        return is_holiday;
    }

    public void setIs_holiday(int is_holiday) {
        this.is_holiday = is_holiday;
    }

    public int getIs_bday() {
        return is_bday;
    }

    public void setIs_bday(int is_bday) {
        this.is_bday = is_bday;
    }

    public boolean isfromEvents() {
        return isfromEvents;
    }

    public void setIsfromEvents(boolean isfromEvents) {
        this.isfromEvents = isfromEvents;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEvent_name() {
        return event_name;
    }

    public void setEvent_name(String event_name) {
        this.event_name = event_name;
    }

    public String getEvent_description() {
        return event_description;
    }

    public void setEvent_description(String event_description) {
        this.event_description = event_description;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public long getStarttime() {
        return starttime;
    }

    public void setStarttime(long starttime) {
        this.starttime = starttime;
    }

    public long getEndtime() {
        return endtime;
    }

    public void setEndtime(long endtime) {
        this.endtime = endtime;
    }

}
