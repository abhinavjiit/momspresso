package com.mycity4kids.models.response;

import android.os.Parcel;
import android.os.Parcelable;

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

    public static class BadgeListData {

        private ArrayList<BadgeListResult> result;

        public ArrayList<BadgeListResult> getResult() {
            return result;
        }

        public void setResult(ArrayList<BadgeListResult> result) {
            this.result = result;
        }

        public static class BadgeListResult implements Parcelable {
            private String id;
            private String badge_desc;
            private String badge_id;
            private String badge_metaclass;
            private String badge_title;
            private int count;
            private boolean deleted;
            private boolean enabled;
            private String user_id;
            private String badge_bg_url = "";
            private String badge_image_url = "";
            private String badge_sharing_url = "";
            private String item_type = "";
            private String content_id = "";

            protected BadgeListResult(Parcel in) {
                id = in.readString();
                badge_desc = in.readString();
                badge_id = in.readString();
                badge_metaclass = in.readString();
                badge_title = in.readString();
                count = in.readInt();
                deleted = in.readByte() != 0;
                enabled = in.readByte() != 0;
                user_id = in.readString();
                badge_bg_url = in.readString();
                badge_image_url = in.readString();
                badge_sharing_url = in.readString();
                item_type = in.readString();
                content_id = in.readString();
            }

            public static final Creator<BadgeListResult> CREATOR = new Creator<BadgeListResult>() {
                @Override
                public BadgeListResult createFromParcel(Parcel in) {
                    return new BadgeListResult(in);
                }

                @Override
                public BadgeListResult[] newArray(int size) {
                    return new BadgeListResult[size];
                }
            };

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getBadge_desc() {
                return badge_desc;
            }

            public void setBadge_desc(String badge_desc) {
                this.badge_desc = badge_desc;
            }

            public String getBadge_title() {
                return badge_title;
            }

            public void setBadge_title(String badge_title) {
                this.badge_title = badge_title;
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

            public int getCount() {
                return count;
            }

            public void setCount(int count) {
                this.count = count;
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

            public String getUser_id() {
                return user_id;
            }

            public void setUser_id(String user_id) {
                this.user_id = user_id;
            }

            public String getBadge_image_url() {
                return badge_image_url;
            }

            public String getBadge_bg_url() {
                return badge_bg_url;
            }

            public void setBadge_bg_url(String badge_bg_url) {
                this.badge_bg_url = badge_bg_url;
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

            @Override
            public int describeContents() {
                return 0;
            }

            @Override
            public void writeToParcel(Parcel parcel, int i) {
                parcel.writeString(id);
                parcel.writeString(badge_desc);
                parcel.writeString(badge_id);
                parcel.writeString(badge_metaclass);
                parcel.writeString(badge_title);
                parcel.writeInt(count);
                parcel.writeByte((byte) (deleted ? 1 : 0));
                parcel.writeByte((byte) (enabled ? 1 : 0));
                parcel.writeString(user_id);
                parcel.writeString(badge_bg_url);
                parcel.writeString(badge_image_url);
                parcel.writeString(badge_sharing_url);
                parcel.writeString(item_type);
                parcel.writeString(content_id);
            }
        }
    }
}
