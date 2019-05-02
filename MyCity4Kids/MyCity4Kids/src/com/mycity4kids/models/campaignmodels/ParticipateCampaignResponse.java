package com.mycity4kids.models.campaignmodels;

import com.mycity4kids.models.response.BaseResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hemant on 2/8/16.
 */
public class ParticipateCampaignResponse extends BaseResponse {
    private List<String> data = new ArrayList<String>();

    public List<String> getData() {
        return data;
    }

    public void setData(List<String> data) {
        this.data = data;
    }
}
