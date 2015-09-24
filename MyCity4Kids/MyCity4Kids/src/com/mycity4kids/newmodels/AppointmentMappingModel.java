package com.mycity4kids.newmodels;

import com.kelltontech.model.BaseModel;
import com.kelltontech.utils.StringUtils;

import java.util.ArrayList;

/**
 * Created by manish.soni on 16-06-2015.
 */
public class AppointmentMappingModel extends BaseModel {

    int pId;
    String externalId;
    int eventId;
    int offline_id;
    String appointment_name;
    String locality;
    long starttime;
    long endtime;
    ArrayList<AttendeeModel> Attendee;
    String reminder;
    String is_recurring;



    String repeat;
    String repeate_untill;
    String repeate_num;
    String repeate_frequency;
    boolean hasNotes;
    long temptime;

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

    public int getIs_google() {
        return is_google;
    }

    public void setIs_google(int is_google) {
        this.is_google = is_google;
    }

    private int is_holiday;
    private int is_bday;
    private int is_google;

    public long getTemptime() {
        return temptime;
    }

    public void setTemptime(long temptime) {
        this.temptime = temptime;
    }
    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public String getRepeat() {
        return repeat;
    }

    public void setRepeat(String repeat) {
        this.repeat = repeat;
    }

    public String getRepeate_untill() {
        return repeate_untill;
    }

    public void setRepeate_untill(String repeate_untill) {
        this.repeate_untill = repeate_untill;
    }

    public String getRepeate_num() {
        return repeate_num;
    }

    public void setRepeate_num(String repeate_num) {
        this.repeate_num = repeate_num;
    }

    public String getRepeate_frequency() {
        return repeate_frequency;
    }

    public void setRepeate_frequency(String repeate_frequency) {
        this.repeate_frequency = repeate_frequency;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public AppointmentMappingModel() {
    }

    public AppointmentMappingModel(int eventId) {
        this.eventId = eventId;
    }

    public AppointmentMappingModel(String eventId) {
        this.externalId = eventId;
    }

    public AppointmentMappingModel(int offline_id, String appointment_name, String locality) {

        this.offline_id = offline_id;
        this.appointment_name = appointment_name;
        this.locality = locality;
//        this.Attendee = attendee;
    }

    public int getpId() {
        return pId;
    }

    public void setpId(int pId) {
        this.pId = pId;
    }

    public int getOffline_id() {
        return offline_id;
    }

    public void setOffline_id(int offline_id) {
        this.offline_id = offline_id;
    }

    public String getAppointment_name() {
        return appointment_name;
    }

    public void setAppointment_name(String appointment_name) {
        this.appointment_name = appointment_name;
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

    public ArrayList<AttendeeModel> getAttendee() {
        return Attendee;
    }

    public void setAttendee(ArrayList<AttendeeModel> attendee) {
        Attendee = attendee;
    }

    public String getReminder() {
        return reminder;
    }

    public void setReminder(String reminder) {
        this.reminder = reminder;
    }

    public String getIs_recurring() {
        return is_recurring;
    }

    public void setIs_recurring(String is_recurring) {
        this.is_recurring = is_recurring;
    }

    public boolean isHasNotes() {
        return hasNotes;
    }

    public void setHasNotes(boolean hasNotes) {
        this.hasNotes = hasNotes;
    }
//
//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//
//        AppointmentMappingModel that = (AppointmentMappingModel) o;
//
//        return eventId == that.eventId;
//
//    }
//
//    @Override
//    public int hashCode() {
//        return eventId;
//    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AppointmentMappingModel that = (AppointmentMappingModel) o;

        if (eventId != that.eventId) return false;
        return !(!StringUtils.isNullOrEmptyOrZero(externalId) ? !externalId.equals(that.externalId) : !StringUtils.isNullOrEmptyOrZero(that.externalId));

    }

    @Override
    public int hashCode() {
        int result = externalId != null ? externalId.hashCode() : 0;
        result = 31 * result + eventId;
        return result;
    }
}
