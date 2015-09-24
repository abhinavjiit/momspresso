package com.mycity4kids.models.deeplinking;

/**
 * Created by arsh.vardhan on 15-09-2015.
 */

    public class DeepLinkResult {

        private String message;
        private DeepLinkData data;

        public String getMessage() {
            return message;
        }
        public void setMessage(String message) {
            this.message = message;
        }
        public DeepLinkData getData() {
            return data;
        }
        public void setData(DeepLinkData data) {
            this.data = data;
        }
}
