package com.mycity4kids.newmodels;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by kapil.vij on 20-07-2015.
 */
public class PushNotificationModel implements Parcelable {

    private int family_id;
    private String action;
    private String calendar_items = "";
    private String todo_items = "";
    private String title;
    private String share_content;
    private String message_id;
    private int user_id;
    private int appointment_id;
    private int id;
    private String type;
    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFamily_id() {
        return family_id;
    }

    public void setFamily_id(int family_id) {
        this.family_id = family_id;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getMessage_id() {
        return message_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setMessage_id(String message_id) {
        this.message_id = message_id;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setCalendar_items(String calendar_items) {
        this.calendar_items = calendar_items;
    }

    public String getCalendar_items() {
        return calendar_items;
    }

    public String getTodo_items() {
        return todo_items;
    }

    public void setTodo_items(String todo_items) {
        this.todo_items = todo_items;
    }

    public String getShare_content() {
        return share_content;
    }

    public void setShare_content(String share_content) {
        this.share_content = share_content;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public PushNotificationModel(Parcel in) {
        // TODO Auto-generated constructor stub
        super();
        action = in.readString();
        message_id = in.readString();
        type = in.readString();
        title = in.readString();
        share_content = in.readString();
        url = in.readString();
        family_id = in.readInt();
        user_id = in.readInt();
        appointment_id = in.readInt();
        id = in.readInt();
        calendar_items=in.readString();
        todo_items=in.readString();
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(action);
        dest.writeString(message_id);
        dest.writeString(type);
        dest.writeString(title);
        dest.writeString(share_content);
        dest.writeString(url);
        dest.writeInt(family_id);
        dest.writeInt(user_id);
        dest.writeInt(appointment_id);
        dest.writeInt(id);
        dest.writeString(calendar_items);
        dest.writeString(todo_items);
    }

    public static Parcelable.Creator<PushNotificationModel> CREATOR = new Parcelable.Creator<PushNotificationModel>() {
        @Override
        public PushNotificationModel createFromParcel(Parcel source) {
            return new PushNotificationModel(source);
        }

        @Override
        public PushNotificationModel[] newArray(int size) {
            return new PushNotificationModel[size];
        }
    };


}
