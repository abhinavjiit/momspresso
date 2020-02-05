package com.mycity4kids.models.campaignmodels;

import com.google.gson.annotations.SerializedName;
import com.mycity4kids.models.response.BaseResponse;

import java.util.List;


public class PreProofResponse extends BaseResponse {
    @SerializedName("data")
    private List<ParticipateData> data;

    public List<ParticipateData> getData() {
        return data;
    }

    public void setData(List<ParticipateData> data) {
        this.data = data;
    }

    public class ParticipateData {
        @SerializedName("msg")
        private String msg;
        @SerializedName("result")
        private List<ParticipateResult> result;

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public List<ParticipateResult> getResult() {
            return result;
        }

        public void setResult(List<ParticipateResult> result) {
            this.result = result;
        }
    }

    public class ParticipateResult {
        @SerializedName("created_time")
        private String created_time;
        @SerializedName("deliverable_type")
        private int deliverable_type;
        @SerializedName("id")
        private int id;
        @SerializedName("image_name")
        private String image_name;
        @SerializedName("is_deleted")
        private String is_deleted;
        @SerializedName("is_image_required")
        private int is_image_required;
        @SerializedName("is_text_required")
        private int is_text_required;
        @SerializedName("name")
        private String name;
        @SerializedName("text_name")
        private String text_name;
        @SerializedName("updated_time")
        private String updated_time;

        public String getCreated_time() {
            return created_time;
        }

        public void setCreated_time(String created_time) {
            this.created_time = created_time;
        }

        public int getDeliverable_type() {
            return deliverable_type;
        }

        public void setDeliverable_type(int deliverable_type) {
            this.deliverable_type = deliverable_type;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getImage_name() {
            return image_name;
        }

        public void setImage_name(String image_name) {
            this.image_name = image_name;
        }

        public String getIs_deleted() {
            return is_deleted;
        }

        public void setIs_deleted(String is_deleted) {
            this.is_deleted = is_deleted;
        }

        public int isIs_image_required() {
            return is_image_required;
        }

        public void setIs_image_required(int is_image_required) {
            this.is_image_required = is_image_required;
        }

        public int isIs_text_required() {
            return is_text_required;
        }

        public void setIs_text_required(int is_text_required) {
            this.is_text_required = is_text_required;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getText_name() {
            return text_name;
        }

        public void setText_name(String text_name) {
            this.text_name = text_name;
        }

        public String getUpdated_time() {
            return updated_time;
        }

        public void setUpdated_time(String updated_time) {
            this.updated_time = updated_time;
        }

    }
}
