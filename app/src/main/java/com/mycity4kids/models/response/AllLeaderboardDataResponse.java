package com.mycity4kids.models.response;

import android.os.Parcel;
import android.os.Parcelable;
import com.facebook.all.All;
import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;

public class AllLeaderboardDataResponse extends BaseResponse {

    @SerializedName("data")
    private AllLeaderboardData data;

    public AllLeaderboardData getData() {
        return data;
    }

    public void setData(AllLeaderboardData data) {
        this.data = data;
    }


    public static class AllLeaderboardData {

        @SerializedName("result")
        public ArrayList<AllLeaderboardRankHolder> result;

        public ArrayList<AllLeaderboardRankHolder> getResult() {
            return result;
        }

        public void setResult(
                ArrayList<AllLeaderboardRankHolder> result) {
            this.result = result;
        }

        public static class AllLeaderboardRankHolder implements Parcelable {

            @SerializedName("name")
            private String name;
            @SerializedName("profilePic")
            private ProfilePic profilePic;
            @SerializedName("rank")
            private int rank;
            @SerializedName("score")
            private int score;
            @SerializedName("user_handle")
            private String user_handle;
            @SerializedName("user_id")
            private String user_id;

            protected AllLeaderboardRankHolder(Parcel in) {
                name = in.readString();
                profilePic = in.readParcelable(ProfilePic.class.getClassLoader());
                rank = in.readInt();
                score = in.readInt();
                user_handle = in.readString();
                user_id = in.readString();
            }

            public static final Creator<AllLeaderboardRankHolder> CREATOR = new Creator<AllLeaderboardRankHolder>() {
                @Override
                public AllLeaderboardRankHolder createFromParcel(Parcel in) {
                    return new AllLeaderboardRankHolder(in);
                }

                @Override
                public AllLeaderboardRankHolder[] newArray(int size) {
                    return new AllLeaderboardRankHolder[size];
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
            }
        }
    }

}
