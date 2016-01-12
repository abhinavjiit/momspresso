package com.chatPlatform;

/**
 * Created by anshul on 21/12/15.
 */
public class ContactStore {
    private String contactName;
    private String phoneNumber;
    private String phoneNumber1;
    public boolean isChecked=false;
    public String getContactName() {
        return contactName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneNumber1() {
        return phoneNumber1;
    }

    public void setPhoneNumber1(String phoneNumber1) {
        this.phoneNumber1 = phoneNumber1;
    }
    public void isChecked(boolean isChecked)
    {this.isChecked=isChecked;}
}
