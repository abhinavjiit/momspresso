package com.mycity4kids.googlemap.models;

import android.os.Parcel;
import android.os.Parcelable;

public class TransitModel implements Parcelable {
    private String startLatitude;
    private String endLatitude;
    private String startLongitude;
    private String endLongitude;
    private String instructions;
    private String travelMode;


    public String getStartLatitude() {
        return startLatitude;
    }

    public void setStartLatitude(String startLatitude) {
        this.startLatitude = startLatitude;
    }

    public String getEndLatitude() {
        return endLatitude;
    }

    public void setEndLatitude(String endLatitude) {
        this.endLatitude = endLatitude;
    }

    public String getStartLongitude() {
        return startLongitude;
    }

    public void setStartLongitude(String startLongitude) {
        this.startLongitude = startLongitude;
    }

    public String getEndLongitude() {
        return endLongitude;
    }

    public void setEndLongitude(String endLongitude) {
        this.endLongitude = endLongitude;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public String getTravelMode() {
        return travelMode;
    }

    public void setTravelMode(String travelMode) {
        this.travelMode = travelMode;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(startLatitude);
        dest.writeString(endLatitude);
        dest.writeString(startLongitude);
        dest.writeString(endLongitude);
        dest.writeString(instructions);
        dest.writeString(travelMode);


    }

    public TransitModel()
    {

    }

    public TransitModel(Parcel in) {
        // TODO Auto-generated constructor stub
        super();
        startLatitude = in.readString();
        endLatitude = in.readString();
        startLongitude = in.readString();
        endLongitude = in.readString();
        instructions = in.readString();
        travelMode = in.readString();

    }

    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    public static Parcelable.Creator<TransitModel> CREATOR = new Parcelable.Creator<TransitModel>() {
        @Override
        public TransitModel createFromParcel(Parcel source) {
            return new TransitModel(source);
        }

        @Override
        public TransitModel[] newArray(int size) {
            return new TransitModel[size];
        }
    };
}
