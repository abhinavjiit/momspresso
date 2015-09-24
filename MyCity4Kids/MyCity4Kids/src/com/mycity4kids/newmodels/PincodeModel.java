package com.mycity4kids.newmodels;

import java.util.ArrayList;

/**
 * Created by khushboo.goyal on 22-06-2015.
 */
public class PincodeModel {

    public ArrayList<ResultArray> results;

    public ArrayList<ResultArray> getResults() {
        return results;
    }

    public void setResults(ArrayList<ResultArray> results) {
        this.results = results;
    }


   public  class ResultArray {
        public Geometry geometry;


        public Geometry getGeometry() {
            return geometry;
        }

        public void setGeometry(Geometry geometry) {
            this.geometry = geometry;
        }


    }

    public class Geometry {
        public LocationCode location;

        public LocationCode getLocation() {
            return location;
        }

        public void setLocation(LocationCode location) {
            this.location = location;
        }


    }

    public class LocationCode {

        public Double lat;
        public Double lng;

        public Double getLat() {
            return lat;
        }

        public void setLat(Double lat) {
            this.lat = lat;
        }

        public Double getLng() {
            return lng;
        }

        public void setLng(Double lng) {
            this.lng = lng;
        }
    }

}




