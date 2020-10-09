package com.mycity4kids.models.response;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;

public class LeaderboardDataResponse extends BaseResponse {

    @SerializedName("data")
    private LeaderboardData data;

    public LeaderboardData getData() {
        return data;
    }

    public void setData(LeaderboardData data) {
        this.data = data;
    }


    public static class LeaderboardData {

        @SerializedName("result")
        public ArrayList<LeaderBoradRank> result;

        public ArrayList<LeaderBoradRank> getResult() {
            return result;
        }

        public void setResult(
                ArrayList<LeaderBoradRank> result) {
            this.result = result;
        }

        public static class LeaderBoradRank implements Parcelable {

            @SerializedName("ranks")
            public ArrayList<LeaderboardRankHolder> ranks;
            @SerializedName("type")
            public String type;

            public LeaderBoradRank() {

            }

            protected LeaderBoradRank(Parcel in) {
                type = in.readString();
                ranks = in.createTypedArrayList(LeaderboardRankHolder.CREATOR);
            }

            public static final Creator<LeaderBoradRank> CREATOR = new Creator<LeaderBoradRank>() {
                @Override
                public LeaderBoradRank createFromParcel(Parcel in) {
                    return new LeaderBoradRank(in);
                }

                @Override
                public LeaderBoradRank[] newArray(int size) {
                    return new LeaderBoradRank[size];
                }
            };

            public ArrayList<LeaderboardRankHolder> getRanks() {
                return ranks;
            }

            public void setRanks(
                    ArrayList<LeaderboardRankHolder> ranks) {
                this.ranks = ranks;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }


            @Override
            public int describeContents() {
                return 0;
            }

            @Override
            public void writeToParcel(Parcel parcel, int i) {
                parcel.writeString(type);
                parcel.writeTypedList(ranks);
            }
        }

        public static class LeaderboardRankHolder implements Parcelable {

            @SerializedName("name")
            private String name;
            @SerializedName("profile_pic")
            private ProfilePic profilePic;
            @SerializedName("rank")
            private int rank;
            @SerializedName("score")
            private int score;
            @SerializedName("user_handle")
            private String user_handle;
            @SerializedName("user_id")
            private String user_id;
            @SerializedName("yesterday_rank")
            private int yesterday_rank;

            protected LeaderboardRankHolder(Parcel in) {
                name = in.readString();
                profilePic = in.readParcelable(ProfilePic.class.getClassLoader());
                rank = in.readInt();
                score = in.readInt();
                user_handle = in.readString();
                user_id = in.readString();
                yesterday_rank = in.readInt();
            }

            public static final Creator<LeaderboardRankHolder> CREATOR = new Creator<LeaderboardRankHolder>() {
                @Override
                public LeaderboardRankHolder createFromParcel(Parcel in) {
                    return new LeaderboardRankHolder(in);
                }

                @Override
                public LeaderboardRankHolder[] newArray(int size) {
                    return new LeaderboardRankHolder[size];
                }
            };

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public ProfilePic getProfilePic() {
                return profilePic;
            }

            public void setProfilePic(ProfilePic profilePic) {
                this.profilePic = profilePic;
            }

            public int getRank() {
                return rank;
            }

            public void setRank(int rank) {
                this.rank = rank;
            }

            public int getScore() {
                return score;
            }

            public void setScore(int score) {
                this.score = score;
            }

            public String getUser_handle() {
                return user_handle;
            }

            public void setUser_handle(String user_handle) {
                this.user_handle = user_handle;
            }

            public String getUser_id() {
                return user_id;
            }

            public void setUser_id(String user_id) {
                this.user_id = user_id;
            }

            public int getYesterday_rank() {
                return yesterday_rank;
            }

            public void setYesterday_rank(int yesterday_rank) {
                this.yesterday_rank = yesterday_rank;
            }

            @Override
            public int describeContents() {
                return 0;
            }

            @Override
            public void writeToParcel(Parcel parcel, int i) {
                parcel.writeString(name);
                parcel.writeParcelable(profilePic, i);
                parcel.writeInt(rank);
                parcel.writeInt(score);
                parcel.writeString(user_handle);
                parcel.writeString(user_id);
                parcel.writeInt(yesterday_rank);
            }
        }
    }

}
