package com.mycity4kids.models.CollectionsModels;

import com.mycity4kids.models.response.BaseResponse;
import com.mycity4kids.models.response.ProfilePic;
import com.mycity4kids.profile.FeaturedItem;

import java.util.ArrayList;
import java.util.List;

public class FeaturedOnModel extends BaseResponse {

    private FeaturedData data;

    public FeaturedData getData() {
        return data;
    }

    public void setData(FeaturedData data) {
        this.data = data;
    }

    public class FeaturedData {
        private FeaturedResult result;
        private String msg;

        public FeaturedResult getResult() {
            return result;
        }

        public void setResult(FeaturedResult result) {
            this.result = result;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }
    }

    public class FeaturedResult {
        private List<FeaturedItem> item_list;
        private int total_items;

        public List<FeaturedItem> getItem_list() {
            return item_list;
        }

        public void setItem_list(List<FeaturedItem> item_list) {
            this.item_list = item_list;
        }

        public int getTotal_items() {
            return total_items;
        }

        public void setTotal_items(int total_items) {
            this.total_items = total_items;
        }
    }
}

