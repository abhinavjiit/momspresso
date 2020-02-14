package com.mycity4kids.models.response;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by hemant on 4/1/17.
 */
public class VlogsListingData {
    @SerializedName("chunks")
    private int chunks;
    @SerializedName("result")
    private ArrayList<VlogsListingAndDetailResult> result;

    public ArrayList<VlogsListingAndDetailResult> getResult() {
        return result;
    }

    public void setResult(ArrayList<VlogsListingAndDetailResult> result) {
        this.result = result;
    }

    public int getChunks() {
        return chunks;
    }

    public void setChunks(int chunks) {
        this.chunks = chunks;
    }
}
