package com.mycity4kids.models.response;

import java.util.List;

/**
 * Created by hemant on 22/6/16.
 */
public class UserDetailResponse extends BaseResponse {
    private List<UserDetailData> data;

    public List<UserDetailData> getData() {
        return data;
    }

    public void setData(List<UserDetailData> data) {
        this.data = data;
    }

//    public static class MyModelDeserializer implements JsonDeserializer<ArrayList<UserDetailData>> {
//
//        @Override
//        public ArrayList<UserDetailData> deserialize(JsonElement json, Type typeOfT,
//                                     JsonDeserializationContext context) throws JsonParseException {
//
//            if (json instanceof JsonArray) {
//
//                return new Gson().fromJson(json, MyModel[].class);
//
//            }
//
//            MyModel child = context.deserialize(json, MyModel.class);
//
//            return new MyModel[] { child };
//        }
//
//        @Override
//        public ArrayList<UserDetailData> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
//            return null;
//        }
//    }
}
