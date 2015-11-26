package com.mycity4kids.models.configuration;

import com.mycity4kids.models.category.CategoryResponse;
import com.mycity4kids.models.city.CityResponse;
import com.mycity4kids.models.locality.LocalityResponse;

/**
 * this model contains response model category,Locality,City.
 * These model are located in different different packages for better understanding :)
 *
 * @author Deepanker Chaudhary
 */

public class ConfigurationData {
    private CategoryResponse categoryApi;
    private LocalityResponse localityApi;
    private CityResponse cityApi;
    private AppUpdateVersion appVersionApi;
    private int isAppUpdateRequired;

    public AppUpdateVersion getAppVersionApi() {
        return appVersionApi;
    }

    public void setAppVersionApi(AppUpdateVersion appVersionApi) {
        this.appVersionApi = appVersionApi;
    }

    public LocalityResponse getLocalityApi() {
        return localityApi;
    }

    public void setLocalityApi(LocalityResponse localityApi) {
        this.localityApi = localityApi;
    }

    public CityResponse getCityApi() {
        return cityApi;
    }

    public void setCityApi(CityResponse cityApi) {
        this.cityApi = cityApi;
    }

    public CategoryResponse getCategoryApi() {
        return categoryApi;
    }

    public void setCategoryApi(CategoryResponse categoryApi) {
        this.categoryApi = categoryApi;
    }

    public int getIsAppUpdateRequired() {
        return isAppUpdateRequired;
    }

    public void setIsAppUpdateRequired(int isAppUpdateRequired) {
        this.isAppUpdateRequired = isAppUpdateRequired;
    }
}
