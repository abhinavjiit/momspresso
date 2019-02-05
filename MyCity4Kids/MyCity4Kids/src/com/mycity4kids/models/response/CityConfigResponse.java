package com.mycity4kids.models.response;

import java.util.ArrayList;

/**
 * Created by hemant on 23/1/17.
 */
public class CityConfigResponse extends BaseResponse {

    private CityConfigData data;

    public CityConfigData getData() {
        return data;
    }

    public void setData(CityConfigData data) {
        this.data = data;
    }

    public class CityConfigData {
        private String msg;
        private CityConfigResult result;

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public CityConfigResult getResult() {
            return result;
        }

        public void setResult(CityConfigResult result) {
            this.result = result;
        }

        public class CityConfigResult {
            private ArrayList<CityInfoItem> cityData;

            public ArrayList<CityInfoItem> getCityData() {
                return cityData;
            }

            public void setCityData(ArrayList<CityInfoItem> cityData) {
                this.cityData = cityData;
            }
        }
    }
}

