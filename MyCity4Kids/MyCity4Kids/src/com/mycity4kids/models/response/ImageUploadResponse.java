package com.mycity4kids.models.response;

import java.util.List;

/**
 * Created by anshul on 6/28/16.
 */
public class ImageUploadResponse extends BaseResponse {
    ImageUploadData data;

    public ImageUploadData getData() {
        return data;
    }

    public void setData(ImageUploadData data) {
        data = data;
    }
}
