package com.mycity4kids.models.response;

/**
 * Created by anshul on 6/30/16.
 */
public class ImageUploadData extends BaseData {
    public ImageResult getResult() {
        return result;
    }

    public void setResult(ImageResult result) {
        this.result = result;
    }

    ImageResult result;

}
