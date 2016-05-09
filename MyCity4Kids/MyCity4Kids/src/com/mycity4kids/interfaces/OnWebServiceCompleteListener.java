package com.mycity4kids.interfaces;


import com.mycity4kids.newmodels.VolleyBaseResponse;

public interface OnWebServiceCompleteListener {
    void onWebServiceComplete(VolleyBaseResponse response, boolean isError);
}

