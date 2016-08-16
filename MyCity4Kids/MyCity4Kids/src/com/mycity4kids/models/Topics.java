package com.mycity4kids.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Topics implements Parcelable {

    private String id;
    private String title;
    private String display_name;

    @SerializedName("public")
    private String publicVisibility;

    private String showInMenu;
    private ArrayList<Topics> child;
    private String parentId;
    private String parentName;
    private boolean isSelected;

    public Topics(String id, String title, boolean isSelected, ArrayList<Topics> child, String parentId, String parentName) {
        this.id = id;
        this.title = title;
        this.isSelected = isSelected;
        this.child = child;
        this.parentId = parentId;
        this.parentName = parentName;
    }

    protected Topics(Parcel in) {
        id = in.readString();
        title = in.readString();
        child = in.createTypedArrayList(Topics.CREATOR);
        parentId = in.readString();
        parentName = in.readString();
        display_name = in.readString();
        publicVisibility = in.readString();
        showInMenu = in.readString();
        isSelected = in.readByte() != 0;
    }

    public static final Creator<Topics> CREATOR = new Creator<Topics>() {
        @Override
        public Topics createFromParcel(Parcel in) {
            return new Topics(in);
        }

        @Override
        public Topics[] newArray(int size) {
            return new Topics[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArrayList<Topics> getChild() {
        return child;
    }

    public void setChild(ArrayList<Topics> child) {
        this.child = child;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    public String getPublicVisibility() {
        return publicVisibility;
    }

    public void setPublicVisibility(String publicVisibility) {
        this.publicVisibility = publicVisibility;
    }

    public String getShowInMenu() {
        return showInMenu;
    }

    public void setShowInMenu(String showInMenu) {
        this.showInMenu = showInMenu;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeTypedList(child);
        dest.writeString(parentId);
        dest.writeString(parentName);
        dest.writeString(display_name);
        dest.writeString(publicVisibility);
        dest.writeString(showInMenu);
        dest.writeByte((byte) (isSelected ? 1 : 0));
    }
}