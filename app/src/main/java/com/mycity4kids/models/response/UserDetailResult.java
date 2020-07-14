package com.mycity4kids.models.response;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by hemant on 22/6/16.
 */
public class UserDetailResult implements Parcelable {

    @SerializedName("id")
    private String id;
    @SerializedName("sqlId")
    private String sqlId;
    @SerializedName("mc4kToken")
    private String mc4kToken;
    @SerializedName("firstName")
    private String firstName;
    @SerializedName("lastName")
    private String lastName;
    @SerializedName("email")
    private String email = "";
    @SerializedName("cityId")
    private String cityId;
    @SerializedName("cityName")
    private String cityName;
    @SerializedName("userType")
    private String userType;
    @SerializedName("isValidated")
    private String isValidated;
    @SerializedName("emailValidated")
    private String emailValidated;
    @SerializedName("profilePicUrl")
    private ProfilePic profilePicUrl;
    @SerializedName("kids")
    private ArrayList<KidsModel> kids;
    @SerializedName("blogTitle")
    private String blogTitle = "";
    @SerializedName("followersCount")
    private String followersCount;
    @SerializedName("followingCount")
    private String followingCount;
    @SerializedName("rank")
    private String rank;
    @SerializedName("ranks")
    private ArrayList<LanguageRanksModel> ranks;
    @SerializedName("userBio")
    private String userBio = "";
    @SerializedName("sessionId")
    private String sessionId;
    @SerializedName("phoneNumber")
    private String phoneNumber;
    @SerializedName("phone")
    private PhoneDetails phone;
    @SerializedName("socialTokens")
    private SocialTokens socialTokens;
    @SerializedName("isLangSelection")
    private String isLangSelection = "0";
    @SerializedName("subscriptionEmail")
    private String subscriptionEmail;
    @SerializedName("crownData")
    private Object crownData;
    @SerializedName("langSubscription")
    private Map<String, String> langSubscription;
    @SerializedName("totalArticles")
    private String totalArticles;
    @SerializedName("totalArticlesViews")
    private String totalArticlesViews;
    @SerializedName("userTag")
    private ArrayList<String> userTag;
    @SerializedName("createrLangs")
    private ArrayList<String> createrLangs = new ArrayList<>();
    @SerializedName("rewardsAdded")
    private String rewardsAdded;
    @SerializedName("latitude")
    private double latitude;
    @SerializedName("longitude")
    private double longitude;
    @SerializedName("preferredLanguages")
    private ArrayList<String> preferredLanguages;
    @SerializedName("interests")
    private ArrayList<String> interests;
    @SerializedName("isMother")
    private String isMother;
    @SerializedName("workStatus")
    private String workStatus;
    @SerializedName("dob")
    private String dob;
    @SerializedName("isExpected")
    private String isExpected;
    @SerializedName("expectedDate")
    private String expectedDate;
    @SerializedName("mobileAuthToken")
    private String mobileAuthToken;
    @SerializedName("mobile")
    private String mobile;
    @SerializedName("gender")
    private String gender;
    @SerializedName("blogTitleSlug")
    private String blogTitleSlug;
    @SerializedName("isNewUser")
    private String isNewUser;
    @SerializedName("following")
    private Boolean following;
    @SerializedName("userHandle")
    private String userHandle;
    @SerializedName("isUserHandleUpdated")
    private String isUserHandleUpdated;

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
        emailValidated = in.readString();
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
        userTag = in.createStringArrayList();
        preferredLanguages = in.createStringArrayList();
        interests = in.createStringArrayList();
        cityName = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        isMother = in.readString();
        workStatus = in.readString();
        dob = in.readString();
        isExpected = in.readString();
        expectedDate = in.readString();
        mobileAuthToken = in.readString();
        mobile = in.readString();
        isNewUser = in.readString();
        userHandle = in.readString();
        isUserHandleUpdated = in.readString();
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

    public UserDetailResult() {

    }

    public String getRewardsAdded() {
        return rewardsAdded;
    }

    public void setRewardsAdded(String rewardsAdded) {
        this.rewardsAdded = rewardsAdded;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
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

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getIsMother() {
        return isMother;
    }

    public void setIsMother(String isMother) {
        this.isMother = isMother;
    }

    public String getWorkStatus() {
        return workStatus;
    }

    public void setWorkStatus(String workStatus) {
        this.workStatus = workStatus;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getExpectedDate() {
        return expectedDate;
    }

    public void setExpectedDate(String expectedDate) {
        this.expectedDate = expectedDate;
    }

    public String getMobileToken() {
        return mobileAuthToken;
    }

    public void setMobileToken(String mobileToken) {
        this.mobileAuthToken = mobileToken;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getIsExpected() {
        return isExpected;
    }

    public void setIsExpected(String isExpected) {
        this.isExpected = isExpected;
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


    public String getEmailValidated() {
        return emailValidated;
    }

    public void setEmailValidated(String emailValidated) {
        this.emailValidated = emailValidated;
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
        this.kids = kidsList;
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

    public ArrayList<String> getUserTag() {
        return userTag;
    }

    public void setUserTag(ArrayList<String> userTag) {
        this.userTag = userTag;
    }


    public ArrayList<String> getPreferredLanguages() {
        return preferredLanguages;
    }

    public void setPreferredLanguages(ArrayList<String> preferredLanguages) {
        this.preferredLanguages = preferredLanguages;
    }


    public ArrayList<String> getInterests() {
        return interests;
    }

    public void setInterests(ArrayList<String> interests) {
        this.interests = interests;
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

    public Boolean getFollowing() {
        return following;
    }

    public void setFollowing(Boolean following) {
        this.following = following;
    }

    public ArrayList<String> getCreaterLangs() {
        return createrLangs;
    }

    public void setCreaterLangs(ArrayList<String> createrLangs) {
        this.createrLangs = createrLangs;
    }

    public Object getCrownData() {
        return crownData;
    }

    public void setCrownData(Object crownData) {
        this.crownData = crownData;
    }

    public String getIsNewUser() {
        return isNewUser;
    }

    public void setIsNewUser(String isNewUser) {
        this.isNewUser = isNewUser;
    }

    public String getUserHandle() {
        return userHandle;
    }

    public void setUserHandle(String userHandle) {
        this.userHandle = userHandle;
    }

    public String getIsUserHandleUpdated() {
        return isUserHandleUpdated;
    }

    public void setIsUserHandleUpdated(String isUserHandleUpdated) {
        this.isUserHandleUpdated = isUserHandleUpdated;
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
        parcel.writeString(emailValidated);
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
        parcel.writeStringList(userTag);
        parcel.writeStringList(preferredLanguages);
        parcel.writeStringList(interests);
        parcel.writeString(cityName);
        parcel.writeDouble(latitude);
        parcel.writeDouble(longitude);
        parcel.writeString(isMother);
        parcel.writeString(workStatus);
        parcel.writeString(dob);
        parcel.writeString(isExpected);
        parcel.writeString(expectedDate);
        parcel.writeString(mobileAuthToken);
        parcel.writeString(mobile);
        parcel.writeString(isNewUser);
        parcel.writeString(userHandle);
        parcel.writeString(isUserHandleUpdated);
    }

    public class SocialTokens {
        @SerializedName("fb")
        private SocialTokenDesc fb;
        @SerializedName("twitter")
        private SocialTokenDesc twitter;
        @SerializedName("google")
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
            @SerializedName("isExpired")
            private String isExpired;
            @SerializedName("token")
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
