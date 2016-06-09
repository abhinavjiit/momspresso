package com.mycity4kids.ui.activity;

import android.app.ExpandableListActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.ui.adapter.TopicsParentExpandableListAdapter;
import com.mycity4kids.models.Topics;
import com.mycity4kids.models.TopicsResponse;
import com.mycity4kids.models.parentingstop.CommonParentingResponse;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.TopicsCategoryAPI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

public class TopicsFilterActivity extends ExpandableListActivity {

    int pageNum;
    private TopicsParentExpandableListAdapter topicsParentExpandableListAdapter;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);


//        ArrayList<SubCategoryChild> companyHyundai = new ArrayList<>();
//        companyHyundai.add(new SubCategoryChild("all", false));
//        companyHyundai.add(new SubCategoryChild("verna", false));
//        companyHyundai.add(new SubCategoryChild("city", false));
//        companyHyundai.add(new SubCategoryChild("cruz", false));
//
//        ArrayList<SubCategoryChild> companyMaruti = new ArrayList<>();
//        companyMaruti.add(new SubCategoryChild("all", false));
//        companyMaruti.add(new SubCategoryChild("ciaz", false));
//        companyMaruti.add(new SubCategoryChild("baleno", false));
//        companyMaruti.add(new SubCategoryChild("breza", false));
//        companyMaruti.add(new SubCategoryChild("800", false));
//
//        ArrayList<SubCategoryChild> companyNissan = new ArrayList<>();
//        companyNissan.add(new SubCategoryChild("all", false));
//        companyNissan.add(new SubCategoryChild("sunny", false));
//        companyNissan.add(new SubCategoryChild("pulse", false));
//        companyNissan.add(new SubCategoryChild("terrano", false));
//
//        ArrayList<SubCategoryChild> companyBajaj = new ArrayList<>();
//        companyBajaj.add(new SubCategoryChild("all", false));
//        companyBajaj.add(new SubCategoryChild("discover", false));
//        companyBajaj.add(new SubCategoryChild("pulsar", false));
//        companyBajaj.add(new SubCategoryChild("platina", false));
//
//        ArrayList<SubCategoryChild> companyHero = new ArrayList<>();
//        companyHero.add(new SubCategoryChild("all", false));
//        companyHero.add(new SubCategoryChild("splendor", false));
//        companyHero.add(new SubCategoryChild("passion", false));
//        companyHero.add(new SubCategoryChild("glamour", false));
//
//        ArrayList<String> vehicles = new ArrayList<>();
//        vehicles.add("car");
//        vehicles.add("bike");
//
//        SubCategories marutiCar = new SubCategories();
//        marutiCar.setSubCategoryName("maruti");
//        marutiCar.setSubCategoryChildren(companyMaruti);
//
//        SubCategories hyundaiCar = new SubCategories();
//        hyundaiCar.setSubCategoryName("hyundai");
//        hyundaiCar.setSubCategoryChildren(companyHyundai);
//
//        SubCategories nissanCar = new SubCategories();
//        nissanCar.setSubCategoryName("nissan");
//        nissanCar.setSubCategoryChildren(companyNissan);
//
//        SubCategories heroBike = new SubCategories();
//        heroBike.setSubCategoryName("hero");
//        heroBike.setSubCategoryChildren(companyHero);
//
//        SubCategories bajajBike = new SubCategories();
//        bajajBike.setSubCategoryName("bajaj");
//        bajajBike.setSubCategoryChildren(companyBajaj);
//
//
//        ArrayList<SubCategories> typeCar = new ArrayList<>();
//        typeCar.add(marutiCar);
//        typeCar.add(hyundaiCar);
//        typeCar.add(nissanCar);
//
//
//        ArrayList<SubCategories> typeBike = new ArrayList<>();
//        typeBike.add(heroBike);
//        typeBike.add(bajajBike);
//
//        HashMap vehicleMap = new HashMap<String, List<SubCategories>>();
//        vehicleMap.put(vehicles.get(0), typeCar);
//        vehicleMap.put(vehicles.get(1), typeBike);

        setContentView(R.layout.topics_filter_activity);
        TextView empty = (TextView) findViewById(R.id.empty);
        Retrofit retro = BaseApplication.getInstance().getRetrofit();
        final TopicsCategoryAPI topicsAPI = retro.create(TopicsCategoryAPI.class);

        Call<TopicsResponse> call = topicsAPI.getTopicsCategory("" + SharedPrefUtils.getUserDetailModel(this).getId());
        call.enqueue(getAllTopicsResponseCallback);

        empty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Call<CommonParentingResponse> filterCall = topicsAPI.filterCategories(topicsParentExpandableListAdapter.getAllSelectedElements(), "category", pageNum);
                filterCall.enqueue(getArticlesForSelectedCategories);
            }
        });


    }

    Callback<TopicsResponse> getAllTopicsResponseCallback = new Callback<TopicsResponse>() {
        @Override
        public void onResponse(Call<TopicsResponse> call, retrofit2.Response<TopicsResponse> response) {
            if (response == null || response.body() == null) {
//                showToast("Something went wrong from server");
                return;
            }
            String commentMessage = "";
            TopicsResponse responseData = (TopicsResponse) response.body();
            ArrayList<String> topicsList = new ArrayList<>();
            HashMap<Topics, List<Topics>> topicsMap = new HashMap<Topics, List<Topics>>();
            ArrayList<Topics> topicList = new ArrayList<>();


            //Prepare structure for multi-expandable listview.
            for (int i = 0; i < responseData.getResult().getData().size(); i++) {
                ArrayList<Topics> tempUpList = new ArrayList<>();

                for (int j = 0; j < responseData.getResult().getData().get(i).getChild().size(); j++) {
                    ArrayList<Topics> tempList = new ArrayList<>();

                    //add All option to select all sub-categories-childrens only if there are more then 0 child in a subcategory.
                    if (responseData.getResult().getData().get(i).getChild().get(j).getChild().size() > 0)
                        tempList.add(new Topics(-1, "all", false, new ArrayList<Topics>(), responseData.getResult().getData().get(i).getId(),
                                responseData.getResult().getData().get(i).getTitle()));

                    //add All option to select all sub-categories only if there are more then 0 subcategories.
                    tempList.addAll(responseData.getResult().getData().get(i).getChild().get(j).getChild());
                    responseData.getResult().getData().get(i).getChild().get(j).setChild(tempList);
                }

                if (responseData.getResult().getData().get(i).getChild().size() > 0)
                    tempUpList.add(new Topics(-1, "all", false, new ArrayList<Topics>(), responseData.getResult().getData().get(i).getId(),
                            responseData.getResult().getData().get(i).getTitle()));

                tempUpList.addAll(responseData.getResult().getData().get(i).getChild());
                topicList.add(responseData.getResult().getData().get(i));
                topicsMap.put(responseData.getResult().getData().get(i),
                        tempUpList);
            }
            topicsParentExpandableListAdapter =
                    new TopicsParentExpandableListAdapter(
                            TopicsFilterActivity.this,
                            getExpandableListView(),
                            topicList, topicsMap
                    );
            setListAdapter(topicsParentExpandableListAdapter);
            if (responseData.getResponseCode() == 200) {

            } else if (responseData.getResponseCode() == 400) {

            }
        }

        @Override
        public void onFailure(Call<TopicsResponse> call, Throwable t) {
//            showToast(getString(R.string.went_wrong));
            Crashlytics.logException(t);
            Log.d("Exception", Log.getStackTraceString(t));
        }
    };

    Callback<CommonParentingResponse> getArticlesForSelectedCategories = new Callback<CommonParentingResponse>() {
        @Override
        public void onResponse(Call<CommonParentingResponse> call, retrofit2.Response<CommonParentingResponse> response) {
            if (response == null || response.body() == null) {
//                showToast("Something went wrong from server");
                return;
            }
            String commentMessage = "";
            CommonParentingResponse responseData = (CommonParentingResponse) response.body();

            if (responseData.getResponseCode() == 200) {

            } else if (responseData.getResponseCode() == 400) {

            }
        }

        @Override
        public void onFailure(Call<CommonParentingResponse> call, Throwable t) {
//            showToast(getString(R.string.went_wrong));
            Crashlytics.logException(t);
            Log.d("Exception", Log.getStackTraceString(t));
        }
    };

}
