package com.mycity4kids.models.response;

import java.util.ArrayList;

/**
 * Created by hemant on 4/1/17.
 */
public class HomeVideosListingData {
    private ArrayList<HomeVideosListingResult> result;

    public ArrayList<HomeVideosListingResult> getResult() {
        return result;
    }

    public void setResult(ArrayList<HomeVideosListingResult> result) {
        this.result = result;
    }
}
