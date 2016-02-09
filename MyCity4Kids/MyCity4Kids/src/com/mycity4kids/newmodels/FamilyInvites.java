package com.mycity4kids.newmodels;

import android.os.Parcel;
import android.os.Parcelable;

import com.mycity4kids.models.basemodel.BaseDataModel;

public class FamilyInvites extends BaseDataModel implements Parcelable {

    private String familyId;
    private String invitationId;
    private String kidName;
    private String email;
    private String colorCode;
    private String profileImage;
    private String firstName;
    private String lastName;

    protected FamilyInvites(Parcel in) {
        familyId = in.readString();
        invitationId = in.readString();
        kidName = in.readString();
        email = in.readString();
        colorCode = in.readString();
        profileImage = in.readString();
        firstName = in.readString();
        lastName = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(familyId);
        dest.writeString(invitationId);
        dest.writeString(kidName);
        dest.writeString(email);
        dest.writeString(colorCode);
        dest.writeString(profileImage);
        dest.writeString(firstName);
        dest.writeString(lastName);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<FamilyInvites> CREATOR = new Creator<FamilyInvites>() {
        @Override
        public FamilyInvites createFromParcel(Parcel in) {
            return new FamilyInvites(in);
        }

        @Override
        public FamilyInvites[] newArray(int size) {
            return new FamilyInvites[size];
        }
    };

    public String getFamilyId() {
        return familyId;
    }

    public void setFamilyId(String familyId) {
        this.familyId = familyId;
    }

    public String getInvitationId() {
        return invitationId;
    }

    public void setInvitationId(String invitationId) {
        this.invitationId = invitationId;
    }

    public String getKidName() {
        return kidName;
    }

    public void setKidName(String kidName) {
        this.kidName = kidName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getColorCode() {
        return colorCode;
    }

    public void setColorCode(String colorCode) {
        this.colorCode = colorCode;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
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
}