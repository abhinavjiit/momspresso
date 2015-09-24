package com.mycity4kids.models.deeplinking;

import com.kelltontech.model.BaseModel;

/**
 * Created by arsh.vardhan on 15-09-2015.
 */

    public class DeepLinkApiModel extends BaseModel {
        private int responseCode;
        private String response;
        private DeepLinkResult result;

        public int getResponseCode() {
            return responseCode;
        }
        public void setResponseCode(int responseCode) {
            this.responseCode = responseCode;
        }
        public String getResponse() {
            return response;
        }
        public void setResponse(String response) {
            this.response = response;
        }
        public DeepLinkResult getResult() {
            return result;
        }
        public void setResult(DeepLinkResult result) {
            this.result = result;
        }

}
