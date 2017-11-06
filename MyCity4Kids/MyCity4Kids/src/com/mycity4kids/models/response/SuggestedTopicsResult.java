package com.mycity4kids.models.response;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by hemant on 1/11/17.
 */

public class SuggestedTopicsResult implements Parcelable {

    //    private String suggestedTopic;
    private Map<String, ArrayList<String>> suggestedTopic;

    protected SuggestedTopicsResult(Parcel in) {
    }

    public static final Creator<SuggestedTopicsResult> CREATOR = new Creator<SuggestedTopicsResult>() {
        @Override
        public SuggestedTopicsResult createFromParcel(Parcel in) {
            return new SuggestedTopicsResult(in);
        }

        @Override
        public SuggestedTopicsResult[] newArray(int size) {
            return new SuggestedTopicsResult[size];
        }
    };

    public Map<String, ArrayList<String>> getSuggestedTopic() {
        return suggestedTopic;
    }

    public void setSuggestedTopic(Map<String, ArrayList<String>> suggestedTopic) {
        this.suggestedTopic = suggestedTopic;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }
}
