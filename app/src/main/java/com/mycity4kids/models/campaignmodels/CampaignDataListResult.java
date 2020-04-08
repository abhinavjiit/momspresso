package com.mycity4kids.models.campaignmodels;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.mycity4kids.models.campaignmodels.BrandDetails;

public class CampaignDataListResult {

    @SerializedName("brand_details")
    @Expose
    private BrandDetails brandDetails;
    @SerializedName("brand_id")
    @Expose
    private int brandId;
    @SerializedName("campaign_status")
    @Expose
    private int campaignStatus;
    @SerializedName("deliverable_types")
    @Expose
    private List<Integer> deliverableTypes = null;
    @SerializedName("end_time")
    @Expose
    private Long endTime;
    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("image_url")
    @Expose
    private String imageUrl;
    @SerializedName("is_active")
    @Expose
    private int isActive;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("name_slug")
    @Expose
    private String nameSlug;
    @SerializedName("recm_order")
    @Expose
    private Object recmOrder;
    @SerializedName("start_time")
    @Expose
    private Long startTime;
    @SerializedName("total_payout")
    @Expose
    private int totalPayout;
    @SerializedName("max_slots")
    @Expose
    private int maxSlots;
    @SerializedName("total_used_slots")
    @Expose
    private int totalUsedSlots;
    @SerializedName("slot_available")
    @Expose
    private int slotAvailable;

    public BrandDetails getBrandDetails() {
        return brandDetails;
    }

    public void setBrandDetails(BrandDetails brandDetails) {
        this.brandDetails = brandDetails;
    }

    public int getBrandId() {
        return brandId;
    }

    public void setBrandId(int brandId) {
        this.brandId = brandId;
    }

    public int getCampaignStatus() {
        return campaignStatus;
    }

    public void setCampaignStatus(int campaignStatus) {
        this.campaignStatus = campaignStatus;
    }

    public List<Integer> getDeliverableTypes() {
        return deliverableTypes;
    }

    public void setDeliverableTypes(List<Integer> deliverableTypes) {
        this.deliverableTypes = deliverableTypes;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getIsActive() {
        return isActive;
    }

    public void setIsActive(int isActive) {
        this.isActive = isActive;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameSlug() {
        return nameSlug;
    }

    public void setNameSlug(String nameSlug) {
        this.nameSlug = nameSlug;
    }

    public Object getRecmOrder() {
        return recmOrder;
    }

    public void setRecmOrder(Object recmOrder) {
        this.recmOrder = recmOrder;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public int getTotalPayout() {
        return totalPayout;
    }

    public void setTotalPayout(int totalPayout) {
        this.totalPayout = totalPayout;
    }

    public int getMaxSlots() {
        return maxSlots;
    }

    public void setMaxSlots(int maxSlots) {
        this.maxSlots = maxSlots;
    }

    public int getTotalUsedSlots() {
        return totalUsedSlots;
    }

    public void setTotalUsedSlots(int totalUsedSlots) {
        this.totalUsedSlots = totalUsedSlots;
    }

    public int getSlotAvailable() {
        return slotAvailable;
    }

    public void setSlotAvailable(int slotAvailable) {
        this.slotAvailable = slotAvailable;
    }

}