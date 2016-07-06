package com.mycity4kids.models.request;

import com.mycity4kids.models.response.BaseResponse;

/**
 * Created by hemant on 24/6/16.
 */
public class UpdateUserDetail extends BaseResponse {

    private String attributeName;
    private String attributeValue;
    private String attributeType;

    public String getAttributeName() {
        return attributeName;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    public String getAttributeValue() {
        return attributeValue;
    }

    public void setAttributeValue(String attributeValue) {
        this.attributeValue = attributeValue;
    }

    public String getAttributeType() {
        return attributeType;
    }

    public void setAttributeType(String attributeType) {
        this.attributeType = attributeType;
    }
}
