package com.mycity4kids.models.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by anshul on 6/30/16.
 */
public class ImageUploadData extends BaseData {

  @SerializedName("result")
  private ImageResult result;

  public ImageResult getResult() {
    return result;
  }

  public void setResult(ImageResult result) {
    this.result = result;
  }
}
