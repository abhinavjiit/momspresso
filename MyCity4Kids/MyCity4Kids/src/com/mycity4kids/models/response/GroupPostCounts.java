package com.mycity4kids.models.response;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by hemant on 28/5/18.
 */

public class GroupPostCounts implements Parcelable {
    private String name;
    private int count;
    private ArrayList<GroupPostCounts> counts;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public ArrayList<GroupPostCounts> getCounts() {
        return counts;
    }

    public void setCounts(ArrayList<GroupPostCounts> counts) {
        this.counts = counts;
    }

    protected GroupPostCounts(Parcel in) {
        name = in.readString();
        count = in.readInt();
        counts = new ArrayList<>();
        in.readTypedList(counts, GroupPostCounts.CREATOR);
    }

    public static final Creator<GroupPostCounts> CREATOR = new Creator<GroupPostCounts>() {
        @Override
        public GroupPostCounts createFromParcel(Parcel in) {
            return new GroupPostCounts(in);
        }

        @Override
        public GroupPostCounts[] newArray(int size) {
            return new GroupPostCounts[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeInt(count);
        dest.writeTypedList(counts);
    }
}
