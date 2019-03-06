package com.mycity4kids.models.response;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by hemant on 22/6/16.
 */
public class UserDetailResult implements Parcelable {
    private String id;
    private String sqlId;
    private String mc4kToken;
    private String firstName;
    private String lastName;
    private String email = "";
    private String cityId;
    private String userType;
    private String isValidated;
    private ProfilePic profilePicUrl;
    private ArrayList<KidsModel> kids;
    private String blogTitle = "";
    private String followersCount;
    private String followingCount;
    private String rank;
    private ArrayList<LanguageRanksModel> ranks;
    private String userBio = "";
    private String sessionId;
    private String phoneNumber;
    private PhoneDetails phone;
    private SocialTokens socialTokens;
    private String isLangSelection = "0";
    private String subscriptionEmail;
    private Map<String, String> langSubscription;
    private String totalArticles;
    private String totalArticlesViews;

    public String getRewardsAdded() {
        return rewardsAdded;
    }

    public void setRewardsAdded(String rewardsAdded) {
        this.rewardsAdded = rewardsAdded;
    }

    private String rewardsAdded;


    protected UserDetailResult(Parcel in) {
        id = in.readString();
        sqlId = in.readString();
        mc4kToken = in.readString();
        firstName = in.readString();
        lastName = in.readString();
        email = in.readString();
        cityId = in.readString();
        userType = in.readString();
        isValidated = in.readString();
        profilePicUrl = in.readParcelable(ProfilePic.class.getClassLoader());
        blogTitle = in.readString();
        followersCount = in.readString();
        followingCount = in.readString();
        rank = in.readString();
        userBio = in.readString();
        sessionId = in.readString();
        phoneNumber = in.readString();
        isLangSelection = in.readString();
        subscriptionEmail = in.readString();
        totalArticles = in.readString();
        totalArticlesViews = in.readString();
        gender = in.readString();
        blogTitleSlug = in.readString();
        rewardsAdded = in.readString();
    }

    public static final Creator<UserDetailResult> CREATOR = new Creator<UserDetailResult>() {
        @Override
        public UserDetailResult createFromParcel(Parcel in) {
            return new UserDetailResult(in);
        }

        @Override
        public UserDetailResult[] newArray(int size) {
            return new UserDetailResult[size];
        }
    };

    public String getBlogTitleSlug() {
        return blogTitleSlug;
    }

    public void setBlogTitleSlug(String blogTitleSlug) {
        this.blogTitleSlug = blogTitleSlug;
    }

    private String gender;
    private String blogTitleSlug;

    public UserDetailResult() {

    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getId() {
        return sqlId;
    }

    public void setId(String id) {
        this.sqlId = sqlId;
    }

    public String getDynamoId() {
        return id;
    }

    public void setDynamoId(String dynamoId) {
        this.id = id;
    }

    public String getMc4kToken() {
        return mc4kToken;
    }

    public void setMc4kToken(String mc4kToken) {
        this.mc4kToken = mc4kToken;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getIsValidated() {
        return isValidated;
    }

    public void setIsValidated(String isValidated) {
        this.isValidated = isValidated;
    }

    public ProfilePic getProfilePicUrl() {
        return profilePicUrl;
    }

    public void setProfilePicUrl(ProfilePic profilePicUrl) {
        this.profilePicUrl = profilePicUrl;
    }

    public ArrayList<KidsModel> getKids() {
        return kids;
    }

    public void setKids(ArrayList<KidsModel> kidsList) {
        this.kids = kids;
    }

    public String getBlogTitle() {
        return blogTitle;
    }

    public void setBlogTitle(String blogTitle) {
        this.blogTitle = blogTitle;
    }

    public String getFollowersCount() {
        return followersCount;
    }

    public void setFollowersCount(String followersCount) {
        this.followersCount = followersCount;
    }

    public String getFollowingCount() {
        return followingCount;
    }

    public void setFollowingCount(String followingCount) {
        this.followingCount = followingCount;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public ArrayList<LanguageRanksModel> getRanks() {
        return ranks;
    }

    public void setRanks(ArrayList<LanguageRanksModel> ranks) {
        this.ranks = ranks;
    }

    public String getUserBio() {
        return userBio;
    }

    public void setUserBio(String userBio) {
        this.userBio = userBio;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getIsLangSelection() {
        return isLangSelection;
    }

    public void setIsLangSelection(String isLangSelection) {
        this.isLangSelection = isLangSelection;
    }

    public PhoneDetails getPhone() {
        return phone;
    }

    public void setPhone(PhoneDetails phone) {
        this.phone = phone;
    }

    public SocialTokens getSocialTokens() {
        return socialTokens;
    }

    public void setSocialTokens(SocialTokens socialTokens) {
        this.socialTokens = socialTokens;
    }

    public Map<String, String> getLangSubscription() {
        return langSubscription;
    }

    public void setLangSubscription(Map<String, String> langSubscription) {
        this.langSubscription = langSubscription;
    }

    public String getSubscriptionEmail() {
        return subscriptionEmail;
    }

    public void setSubscriptionEmail(String subscriptionEmail) {
        this.subscriptionEmail = subscriptionEmail;
    }

    public String getTotalArticles() {
        return totalArticles;
    }

    public void setTotalArticles(String totalArticles) {
        this.totalArticles = totalArticles;
    }

    public String getTotalArticlesViews() {
        return totalArticlesViews;
    }

    public void setTotalArticlesViews(String totalArticlesViews) {
        this.totalArticlesViews = totalArticlesViews;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(sqlId);
        parcel.writeString(mc4kToken);
        parcel.writeString(firstName);
        parcel.writeString(lastName);
        parcel.writeString(email);
        parcel.writeString(cityId);
        parcel.writeString(userType);
        parcel.writeString(isValidated);
        parcel.writeParcelable(profilePicUrl, i);
        parcel.writeString(blogTitle);
        parcel.writeString(followersCount);
        parcel.writeString(followingCount);
        parcel.writeString(rank);
        parcel.writeString(userBio);
        parcel.writeString(sessionId);
        parcel.writeString(phoneNumber);
        parcel.writeString(isLangSelection);
        parcel.writeString(subscriptionEmail);
        parcel.writeString(totalArticles);
        parcel.writeString(totalArticlesViews);
        parcel.writeString(gender);
        parcel.writeString(blogTitleSlug);
        parcel.writeString(rewardsAdded);
    }

    public class SocialTokens {
        private SocialTokenDesc fb;
        private SocialTokenDesc twitter;
        private SocialTokenDesc google;

        public SocialTokenDesc getFb() {
            return fb;
        }

        public void setFb(SocialTokenDesc fb) {
            this.fb = fb;
        }

        public SocialTokenDesc getTwitter() {
            return twitter;
        }

        public void setTwitter(SocialTokenDesc twitter) {
            this.twitter = twitter;
        }

        public SocialTokenDesc getGoogle() {
            return google;
        }

        public void setGoogle(SocialTokenDesc google) {
            this.google = google;
        }

        public class SocialTokenDesc {
            private String isExpired;
            private String token;

            public String getIsExpired() {
                return isExpired;
            }

            public void setIsExpired(String isExpired) {
                this.isExpired = isExpired;
            }

            public String getToken() {
                return token;
            }

            public void setToken(String token) {
                this.token = token;
            }
        }
    }
}
