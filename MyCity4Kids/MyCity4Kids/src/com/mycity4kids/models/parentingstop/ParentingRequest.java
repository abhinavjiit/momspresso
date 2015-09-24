package com.mycity4kids.models.parentingstop;

/**
 * @author deepanker.chaudhary
 */
public class ParentingRequest {
    private int city_id;
    private String soty_by;
    private String page;
    private String authorId;
    private String searchName;
    private String contact_no;
    private String event_name;


    public String getContact_no() {
        return contact_no;
    }

    public void setContact_no(String contact_no) {
        this.contact_no = contact_no;
    }

    public String getEvent_name() {
        return event_name;
    }

    public void setEvent_name(String event_name) {
        this.event_name = event_name;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public int getCity_id() {
        return city_id;
    }

    public void setCity_id(int city_id) {
        this.city_id = city_id;
    }

    public String getSoty_by() {
        return soty_by;
    }

    public void setSoty_by(String soty_by) {
        this.soty_by = soty_by;
    }

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public String getSearchName() {
        return searchName;
    }

    public void setSearchName(String searchName) {
        this.searchName = searchName;
    }
}
