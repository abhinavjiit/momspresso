package com.mycity4kids.models.response;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hemant on 18/1/17.
 */
public class ArticleTagsImagesResponse extends BaseResponse {
    @SerializedName("data")
    private List<ArticleTagsImagesData> data;

    public List<ArticleTagsImagesData> getData() {
        return data;
    }

    public void setData(List<ArticleTagsImagesData> data) {
        this.data = data;
    }

    public class ArticleTagsImagesData {
        @SerializedName("result")
        private ArrayList<ArticleTagsImagesResult> result;

        public ArrayList<ArticleTagsImagesResult> getResult() {
            return result;
        }

        public void setResult(ArrayList<ArticleTagsImagesResult> result) {
            this.result = result;
        }

        public class ArticleTagsImagesResult {
            @SerializedName("imageUrl")
            public ImageURL imageUrl;
            @SerializedName("isSelected")
            public boolean isSelected = false;

            public ImageURL getImageUrl() {
                return imageUrl;
            }

            public void setImageUrl(ImageURL imageUrl) {
                this.imageUrl = imageUrl;
            }

            public boolean isSelected() {
                return isSelected;
            }

            public void setSelected(boolean selected) {
                isSelected = selected;
            }
        }
    }
}
