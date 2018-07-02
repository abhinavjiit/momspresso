package com.mycity4kids.widget;

public class TrackingData {

    // Duration for which the view has been viewed.
    private long viewDuration;

    // ID for the view that was viewed (we'll use the position of the item here).
    private String viewId;

    // Percentage of the height visible
    private double percentageHeightVisible;

    public double getPercentageHeightVisible() {
        return percentageHeightVisible;
    }

    public void setPercentageHeightVisible(double percentageHeightVisible) {
        this.percentageHeightVisible = percentageHeightVisible;
    }

    public long getViewDuration() {
        return viewDuration;
    }

    public void setViewDuration(long viewDuration) {
        this.viewDuration = viewDuration;
    }

    public String getViewId() {
        return viewId;
    }

    public void setViewId(String viewId) {
        this.viewId = viewId;
    }
}