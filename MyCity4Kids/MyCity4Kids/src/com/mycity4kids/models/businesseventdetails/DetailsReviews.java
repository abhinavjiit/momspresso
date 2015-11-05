package com.mycity4kids.models.businesseventdetails;

import android.os.Parcel;
import android.os.Parcelable;

public class DetailsReviews implements Parcelable {

    private String title;
    private String description;
    private float ratingcount;
    private String created_date;
    private String user_image;
    private String reviewer;
    private boolean isExpend;

    public boolean isExpend() {
        return isExpend;
    }

    public void setExpend(boolean isExpend) {
        this.isExpend = isExpend;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the ratingcount
     */
    public float getRatingcount() {
        return ratingcount;
    }

    /**
     * @param ratingcount the ratingcount to set
     */
    public void setRatingcount(int ratingcount) {
        this.ratingcount = ratingcount;
    }

    /**
     * @return the created_date
     */
    public String getCreated_date() {
        return created_date;
    }

    /**
     * @param created_date the created_date to set
     */
    public void setCreated_date(String created_date) {
        this.created_date = created_date;
    }

    /**
     * @return the user_image
     */
    public String getUser_image() {
        return user_image;
    }

    /**
     * @param user_image the user_image to set
     */
    public void setUser_image(String user_image) {
        this.user_image = user_image;
    }

    /**
     * @return the reviewer
     */
    public String getReviewer() {
        return reviewer;
    }

    /**
     * @param reviewer the reviewer to set
     */
    public void setReviewer(String reviewer) {
        this.reviewer = reviewer;
    }

    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(description);
        dest.writeFloat(ratingcount);
        dest.writeString(created_date);
        dest.writeString(user_image);
        dest.writeString(reviewer);
    }

    public DetailsReviews(Parcel in) {
        this.title = in.readString();
        this.description = in.readString();
        this.ratingcount = in.readFloat();
        this.created_date = in.readString();
        this.user_image = in.readString();
        this.reviewer = in.readString();
    }

    public static final Parcelable.Creator<DetailsReviews> CREATOR = new Parcelable.Creator<DetailsReviews>() {

        public DetailsReviews createFromParcel(Parcel in) {
            return new DetailsReviews(in);
        }

        public DetailsReviews[] newArray(int size) {
            return new DetailsReviews[size];
        }
    };
}
