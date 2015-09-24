package com.mycity4kids.newmodels;

import com.kelltontech.model.BaseModel;

/**
 * Created by manish.soni on 31-08-2015.
 */
public class FacebookEventModelNew extends BaseModel {

    String id;
    String name;
    String location;
    String start_time;
    String timezone;
    String rsvp_status;

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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public String getRsvp_status() {
        return rsvp_status;
    }

    public void setRsvp_status(String rsvp_status) {
        this.rsvp_status = rsvp_status;
    }

}
