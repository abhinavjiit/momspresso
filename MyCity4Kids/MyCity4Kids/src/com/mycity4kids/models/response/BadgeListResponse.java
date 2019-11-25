package com.mycity4kids.models.response;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hemant on 18/1/17.
 */
public class BadgeListResponse extends BaseResponse {

    private List<BadgeListData> data;

    public List<BadgeListData> getData() {
        return data;
    }

    public void setData(List<BadgeListData> data) {
        this.data = data;
    }

    public class BadgeListData {

        private ArrayList<BadgeListResult> result;

        public ArrayList<BadgeListResult> getResult() {
            return result;
        }

        public void setResult(ArrayList<BadgeListResult> result) {
            this.result = result;
        }

        public class BadgeListResult {
            private BadgeId _id;
            private String badge_desc;
            private String badge_id;
            private String badge_metaclass;
            private String badge_title;
            private int count;
            private BadgeCreated created_at;
            private boolean deleted;
            private boolean enabled;
            private BadgeUpdated updated_at;
            private String user_id;
            private String badge_image_url = "";
            private String badge_sharing_url = "";
            private String item_type = "";
            private String content_id = "";

            public BadgeId get_id() {
                return _id;
            }

            public void set_id(BadgeId _id) {
                this._id = _id;
            }

            public String getBadge_desc() {
                return badge_desc;
            }

            public void setBadge_desc(String badge_desc) {
                this.badge_desc = badge_desc;
            }

            public String getBadge_id() {
                return badge_id;
            }

            public void setBadge_id(String badge_id) {
                this.badge_id = badge_id;
            }

            public String getBadge_metaclass() {
                return badge_metaclass;
            }

            public void setBadge_metaclass(String badge_metaclass) {
                this.badge_metaclass = badge_metaclass;
            }

            public String getBadge_title() {
                return badge_title;
            }

            public void setBadge_title(String badge_title) {
                this.badge_title = badge_title;
            }

            public int getCount() {
                return count;
            }

            public void setCount(int count) {
                this.count = count;
            }

            public BadgeCreated getCreated_at() {
                return created_at;
            }

            public void setCreated_at(BadgeCreated created_at) {
                this.created_at = created_at;
            }

            public boolean isDeleted() {
                return deleted;
            }

            public void setDeleted(boolean deleted) {
                this.deleted = deleted;
            }

            public boolean isEnabled() {
                return enabled;
            }

            public void setEnabled(boolean enabled) {
                this.enabled = enabled;
            }

            public BadgeUpdated getUpdated_at() {
                return updated_at;
            }

            public void setUpdated_at(BadgeUpdated updated_at) {
                this.updated_at = updated_at;
            }

            public String getUser_id() {
                return user_id;
            }

            public void setUser_id(String user_id) {
                this.user_id = user_id;
            }

            public String getBadge_image_url() {
                return badge_image_url;
            }

            public void setBadge_image_url(String badge_image_url) {
                this.badge_image_url = badge_image_url;
            }

            public String getBadge_sharing_url() {
                return badge_sharing_url;
            }

            public void setBadge_sharing_url(String badge_sharing_url) {
                this.badge_sharing_url = badge_sharing_url;
            }

            public String getItem_type() {
                return item_type;
            }

            public void setItem_type(String item_type) {
                this.item_type = item_type;
            }

            public String getContent_id() {
                return content_id;
            }

            public void setContent_id(String content_id) {
                this.content_id = content_id;
            }
        }
    }

    public class BadgeId {
        private String $oid;

        public String get$oid() {
            return $oid;
        }

        public void set$oid(String $oid) {
            this.$oid = $oid;
        }

    }

    public class BadgeCreated {
        private long $date;

        public long get$date() {
            return $date;
        }

        public void set$date(long $date) {
            this.$date = $date;
        }

    }

    public class BadgeUpdated {
        private long $date;

        public long get$date() {
            return $date;
        }

        public void set$date(long $date) {
            this.$date = $date;
        }

    }
}
