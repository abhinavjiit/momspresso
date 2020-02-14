package com.mycity4kids.models.parentingdetails;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class DetailsBody {
    @SerializedName("text")
    private String text;
    @SerializedName("image")
    private ArrayList<ImageData> image;
    @SerializedName("video")
    private ArrayList<VideoData> video;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public ArrayList<ImageData> getImage() {
        return image;
    }

    public void setImage(ArrayList<ImageData> image) {
        this.image = image;
    }

    public ArrayList<VideoData> getVideo() {
        return video;
    }

    public void setVideo(ArrayList<VideoData> video) {
        this.video = video;
    }
}
