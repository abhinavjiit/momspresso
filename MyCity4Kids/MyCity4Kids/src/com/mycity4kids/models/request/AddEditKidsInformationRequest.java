package com.mycity4kids.models.request;

import java.util.ArrayList;

/**
 * Created by hemant on 4/7/16.
 */
public class AddEditKidsInformationRequest {

    private ArrayList<AddRemoveKidsRequest> kids;

    public ArrayList<AddRemoveKidsRequest> getKids() {
        return kids;
    }

    public void setKids(ArrayList<AddRemoveKidsRequest> kids) {
        this.kids = kids;
    }
}
