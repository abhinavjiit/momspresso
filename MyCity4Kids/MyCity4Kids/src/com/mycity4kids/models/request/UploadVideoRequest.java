package com.mycity4kids.models.request;

/**
 * Created by hemant on 12/1/17.
 */
public class UploadVideoRequest {
    private String video_id;
    private String title;

    public String getVideo_id() {
        return video_id;
    }

    public void setVideo_id(String video_id) {
        this.video_id = video_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
