package com.mycity4kids.newmodels;

import android.os.Parcel;
import android.os.Parcelable;

import com.mycity4kids.models.basemodel.BaseDataModel;

import java.util.ArrayList;

/**
 * Created by khushboo.goyal on 24-06-2015.
 */
public class AppoitmentDataModel extends BaseDataModel {


    private ArrayList<AppointmentData> Appointment;
    private ArrayList<AppointmentDelete> ApppointmentDeleted;


    public ArrayList<AppointmentData> getAppointment() {
        return Appointment;
    }

    public void setAppointment(ArrayList<AppointmentData> appointment) {
        Appointment = appointment;
    }

    public class AppointmentData extends BaseDataModel {
        private AppointmentDetail Appointment;
        ArrayList<Notes> AppointmentNote;
        ArrayList<WhoToRemind> AppointmentWhomRemind;
        ArrayList<Attendee> AppointmentAttendee;
        ArrayList<Files> AppointmentFile;

        public AppointmentDetail getAppointment() {
            return Appointment;
        }

        public void setAppointment(AppointmentDetail appointment) {
            Appointment = appointment;
        }

        public ArrayList<WhoToRemind> getAppointmentWhomRemind() {
            return AppointmentWhomRemind;
        }

        public void setAppointmentWhomRemind(ArrayList<WhoToRemind> appointmentWhomRemind) {
            AppointmentWhomRemind = appointmentWhomRemind;
        }

        public ArrayList<Notes> getAppointmentNote() {
            return AppointmentNote;
        }

        public void setAppointmentNote(ArrayList<Notes> appointmentNote) {
            AppointmentNote = appointmentNote;
        }

        public ArrayList<Attendee> getAppointmentAttendee() {
            return AppointmentAttendee;
        }

        public void setAppointmentAttendee(ArrayList<Attendee> appointmentAttendee) {
            AppointmentAttendee = appointmentAttendee;
        }

        public ArrayList<Files> getAppointmentFile() {
            return AppointmentFile;
        }

        public void setAppointmentFile(ArrayList<Files> appointmentFile) {
            AppointmentFile = appointmentFile;
        }
    }



    public class AppointmentDetail extends BaseDataModel {


        int offline_id;
        int id;
        String external_id;

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

        public String getApi_event_id() {
            return api_event_id;
        }

        public void setApi_event_id(String api_event_id) {
            this.api_event_id = api_event_id;
        }

        String api_event_id;
        String appointment_name = "";
        String locality = "";
        long starttime = 0;
        long endtime = 0;
        String reminder = "";
        String is_recurring = "";
        String repeate = "";
        String repeate_untill = "";
        String repeate_num = "";
        String repeate_frequency;
        private int user_id;
        private int family_id;

        public String getExternal_id() {
            return external_id;
        }

        public void setExternal_id(String external_id) {
            this.external_id = external_id;
        }

        public int getUser_id() {
            return user_id;
        }

        public void setUser_id(int user_id) {
            this.user_id = user_id;
        }

        public int getFamily_id() {
            return family_id;
        }

        public void setFamily_id(int family_id) {
            this.family_id = family_id;
        }


        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getReminder() {
            return reminder;
        }

        public void setReminder(String reminder) {
            this.reminder = reminder;
        }

        public String getLocality() {
            return locality;
        }

        public void setLocality(String locality) {
            this.locality = locality;
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

        public String getIs_recurring() {
            return is_recurring;
        }

        public void setIs_recurring(String is_recurring) {
            this.is_recurring = is_recurring;
        }


        public String getRepeate_num() {
            return repeate_num;
        }

        public String getRepeate() {
            return repeate;
        }

        public void setRepeate(String repeate) {
            this.repeate = repeate;
        }

        public void setRepeate_num(String repeate_num) {
            this.repeate_num = repeate_num;
        }

        public String getRepeate_untill() {
            return repeate_untill;
        }

        public void setRepeate_untill(String repeate_untill) {
            this.repeate_untill = repeate_untill;
        }

        public String getRepeate_frequency() {
            return repeate_frequency;
        }

        public void setRepeate_frequency(String repeate_frequency) {
            this.repeate_frequency = repeate_frequency;
        }
    }

    public class Attendee extends BaseDataModel {
        private int uk_id;
        private String uk_type;
        private int id;
        private int appointment_id;


        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }


        public String getUk_type() {
            return uk_type;
        }

        public void setUk_type(String uk_type) {
            this.uk_type = uk_type;
        }

        public int getAppointment_id() {
            return appointment_id;
        }

        public void setAppointment_id(int appointment_id) {
            this.appointment_id = appointment_id;
        }

        public int getUk_id() {
            return uk_id;
        }

        public void setUk_id(int uk_id) {
            this.uk_id = uk_id;
        }


    }

    public static class Files extends BaseDataModel implements Parcelable {

        private int appointment_id;
        private String file_name;
        private String file_type;
        private String url;
        private int id;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getAppointment_id() {
            return appointment_id;
        }

        public void setAppointment_id(int appointment_id) {
            this.appointment_id = appointment_id;
        }

        public String getFile_name() {
            return file_name;
        }

        public void setFile_name(String file_name) {
            this.file_name = file_name;
        }

        public String getFile_type() {
            return file_type;
        }

        public void setFile_type(String file_type) {
            this.file_type = file_type;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(file_name);
            dest.writeString(file_type);
            dest.writeString(url);
            dest.writeInt(appointment_id);
            dest.writeInt(id);

        }

        public Files()
        {

        }
        public Files(Parcel in) {
            // TODO Auto-generated constructor stub
            super();
            file_name = in.readString();
            file_type = in.readString();
            url = in.readString();
            appointment_id = in.readInt();
            id = in.readInt();
        }

        @Override
        public int describeContents() {
            // TODO Auto-generated method stub
            return 0;
        }

        public static Parcelable.Creator<Files> CREATOR = new Parcelable.Creator<Files>() {
            @Override
            public Files createFromParcel(Parcel source) {
                return new Files(source);
            }

            @Override
            public Files[] newArray(int size) {
                return new Files[size];
            }
        };


    }


    public class WhoToRemind extends BaseDataModel {

        int user_id;
        int appointment_id;
        private int id;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getUser_id() {
            return user_id;
        }

        public void setUser_id(int user_id) {
            this.user_id = user_id;
        }

        public int getAppointment_id() {
            return appointment_id;
        }

        public void setAppointment_id(int appointment_id) {
            this.appointment_id = appointment_id;
        }
    }

    public class Notes extends BaseDataModel {

        private int user_id;
        private int id;
        private String note;
        private int appointment_id;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }


        public int getAppointment_id() {
            return appointment_id;
        }

        public void setAppointment_id(int appointment_id) {
            this.appointment_id = appointment_id;
        }

        public int getUser_id() {
            return user_id;
        }

        public void setUser_id(int user_id) {
            this.user_id = user_id;
        }

        public String getNote() {
            return note;
        }

        public void setNote(String note) {
            this.note = note;
        }
    }


    public class AppointmentDelete
    {
        public AppoitmentDel Appointment;
        public AppoitmentDel getAppointment() {
            return Appointment;
        }

        public void setAppointment(AppoitmentDel appointment) {
            Appointment = appointment;
        }


    }

    public ArrayList<AppointmentDelete> getApppointmentDeleted() {
        return ApppointmentDeleted;
    }

    public void setApppointmentDeleted(ArrayList<AppointmentDelete> apppointmentDeleted) {
        ApppointmentDeleted = apppointmentDeleted;
    }

    public class AppoitmentDel
    {
        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int id;
    }
}
