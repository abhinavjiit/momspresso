package com.mycity4kids.models.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by anshul on 6/28/16.
 */
public class ImageUploadResponse extends BaseResponse {

  @SerializedName("data")
  ImageUploadData data;

  public ImageUploadData getData() {
    return data;
  }

  public void setData(ImageUploadData data) {
    data = data;
  }
}
