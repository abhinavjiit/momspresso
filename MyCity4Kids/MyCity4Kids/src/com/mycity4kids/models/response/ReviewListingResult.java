package com.mycity4kids.models.response;

/**
 * Created by anshul on 7/29/16.
 */
public class ReviewListingResult {
    private String id;
    private String type;
    private String reviewId;
    private String rating;
    private String title;
    private String description;
    private String date;
    private String eventName;
    private String businessName;
    private String business_slug;
    private String buisnessType;
    private String eventAddress;
    private String businessAddress;
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getReviewId() {
        return reviewId;
    }

    public void setReviewId(String reviewId) {
        this.reviewId = reviewId;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getBusiness_slug() {
        return business_slug;
    }

    public void setBusiness_slug(String business_slug) {
        this.business_slug = business_slug;
    }

    public String getBuisnessType() {
        return buisnessType;
    }

    public void setBuisnessType(String buisnessType) {
        this.buisnessType = buisnessType;
    }

    public String getEventAddress() {
        return eventAddress;
    }

    public void setEventAddress(String eventAddress) {
        this.eventAddress = eventAddress;
    }

    public String getBusinessAddress() {
        return businessAddress;
    }

    public void setBusinessAddress(String businessAddress) {
        this.businessAddress = businessAddress;
    }
}
