package com.mycity4kids.models.user;

import com.kelltontech.model.BaseModel;
import com.mycity4kids.models.basemodel.BaseDataModel;
import com.mycity4kids.newmodels.FamilyInvites;

import java.util.ArrayList;


public class UserModel extends BaseModel{
    private Profile Profile;
    private ArrayList<KidsInfo> KidsInformation;
    private UserInfo User;
    private FamilyInfo Family;
    private ArrayList<AdultsInfo> Adult;
    public String exist;
    private String error;
    private String message;
    private ArrayList<FamilyInvites> familyInvites;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getExist() {
        return exist;
    }

    public void setExist(String exist) {
        this.exist = exist;
    }

    public ArrayList<KidsInfo> getKidsInformation() {
        return KidsInformation;
    }

    public void setKidsInformation(ArrayList<KidsInfo> kidsInformation) {
        KidsInformation = kidsInformation;
    }

    public FamilyInfo getFamily() {
        return Family;
    }

    public void setFamily(FamilyInfo family) {
        Family = family;
    }

    public ArrayList<AdultsInfo> getAdult() {
        return Adult;
    }

    public void setAdult(ArrayList<AdultsInfo> adult) {
        Adult = adult;
    }

    /**
     * @return the profile
     */
    public Profile getProfile() {
        return Profile;
    }

    /**
     * @param profile the profile to set
     */
    public void setProfile(Profile profile) {
        Profile = profile;
    }

    /**
     * @return the kidsinformation
     */

    public UserInfo getUser() {
        return User;
    }

    /**
     * @param user the user to set
     */
    public void setUser(UserInfo user) {
        User = user;
    }

    public ArrayList<FamilyInvites> getFamilyInvites() {
        return familyInvites;
    }

    public void setFamilyInvites(ArrayList<FamilyInvites> familyInvites) {
        this.familyInvites = familyInvites;
    }


    public class FamilyInfo extends BaseDataModel {
        public int id;
        public String family_name;
        public String family_password;
        public String family_pic;
        public String pincode;
        public String city;

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getFamily_name() {
            return family_name;


        }

        public void setFamily_name(String family_name) {
            this.family_name = family_name;
        }


        public String getFamily_password() {
            return family_password;
        }

        public void setFamily_password(String family_password) {
            this.family_password = family_password;
        }

        public String getPincode() {
            return pincode;
        }

        public void setPincode(String pincode) {
            this.pincode = pincode;
        }

        public String getFamily_pic() {
            return family_pic;
        }

        public void setFamily_pic(String family_pic) {
            this.family_pic = family_pic;
        }


    }

    public class AdultsInfo extends BaseDataModel {

        private UserInfo User;


        public UserInfo getUser() {
            return User;
        }

        public void setUser(UserInfo user) {
            User = user;
        }


    }


}
