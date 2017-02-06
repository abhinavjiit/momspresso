package com.mycity4kids.models.city;

import com.kelltontech.model.BaseModel;

public class MetroCity extends BaseModel {
    private int id;
    private String name;
    private String cityId;

    public MetroCity() {

    }

    public MetroCity(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNewCityId() {
        return cityId;
    }

    public void setNewCityId(String cityId) {
        this.cityId = cityId;
    }

    @Override
    public String toString() {
        return name;
    }

}
