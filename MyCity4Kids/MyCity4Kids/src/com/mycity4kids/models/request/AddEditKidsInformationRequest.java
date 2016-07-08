package com.mycity4kids.models.request;

import com.mycity4kids.models.response.KidsModel;

import java.util.ArrayList;

/**
 * Created by hemant on 4/7/16.
 */
public class AddEditKidsInformationRequest {

    private ArrayList<KidsModel> kids;

    public ArrayList<KidsModel> getKids() {
        return kids;
    }

    public void setKids(ArrayList<KidsModel> kids) {
        this.kids = kids;
    }
}
