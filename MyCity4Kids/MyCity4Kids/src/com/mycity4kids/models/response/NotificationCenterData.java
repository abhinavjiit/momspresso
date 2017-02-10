package com.mycity4kids.models.response;

import java.util.ArrayList;

/**
 * Created by hemant on 21/12/16.
 */
public class NotificationCenterData {
    private ArrayList<NotificationCenterResult> result;
    private Pagination pagination;
    private String total;

    public ArrayList<NotificationCenterResult> getResult() {
        return result;
    }

    public void setResult(ArrayList<NotificationCenterResult> result) {
        this.result = result;
    }

    public Pagination getPagination() {
        return pagination;
    }

    public void setPagination(Pagination pagination) {
        this.pagination = pagination;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public class Pagination {
        private String id;
        private String userId;
        private String createdTime;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getCreatedTime() {
            return createdTime;
        }

        public void setCreatedTime(String createdTime) {
            this.createdTime = createdTime;
        }
    }
}
