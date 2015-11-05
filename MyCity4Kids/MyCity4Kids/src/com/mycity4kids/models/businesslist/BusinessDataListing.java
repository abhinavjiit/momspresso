package com.mycity4kids.models.businesslist;

import android.os.Parcel;
import android.os.Parcelable;

import com.mycity4kids.models.businesseventdetails.Batches;
import com.mycity4kids.models.businesseventdetails.ContactDetail;
import com.mycity4kids.models.businesseventdetails.EventDate;
import com.mycity4kids.models.businesseventdetails.Facalities;
import com.mycity4kids.models.businesseventdetails.Timings;

import java.util.ArrayList;

/**
 * @author Deepanker Chaudharay:-
 *         this model will be use for Business & Event Listing & also use for business & event details (for info part).
 */
public class BusinessDataListing implements Parcelable {


    private String id;
    private String listing_id;
    private String type;
    private String name;

    public String getListing_id() {
        return listing_id;
    }

    public void setListing_id(String listing_id) {
        this.listing_id = listing_id;
    }

    //private String description;
    private String address;
    private String city_id;
    private String email;
    private String landmark;
    private String locality;
    private String merto_station;
    private String phone;
    private String subaddress;
    private String user_id;
    private String website;
    private String startagegroup;
    private String endagegroup;
    private float rating;
    private String locality_id;
    private String reviewscount;
    private String lattitude;
    private String longitude;
    private String distance;
    private String city_name;
    private String pincode;
    private String faviorite_count;
    private String city;
    private String mcity_id;
    //private Facalities facilities;
    private String activities;
    private String start_date;
    private String end_date;
    private String ecommerce;
    private String ecommerce_url;
    private String organised_by;
    private ArrayList<Batches> batches;
    private Timings timings;
    private ArrayList<Facalities> facilities;
    private String duration;
    private String is_bookmark;

    private EventDate event_date;
    private String age_group;
    private String cost;
    private String contacts;
    private String about;
    private String web_url;
    private ContactDetail contact;
    private String agegroup_text;
    //anupama

    public boolean isEventAdded() {
        return isEventAdded;
    }

    public void setIsEventAdded(boolean isEventAdded) {
        this.isEventAdded = isEventAdded;
    }

    private String thumbnail_url;
    private String description;

    private boolean isEventAdded;

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getThumbnail_url() {
        return thumbnail_url;
    }

    public void setThumbnail_url(String thumbnail_url) {
        this.thumbnail_url = thumbnail_url;
    }

    public String getThumbnail() {
        return thumbnail_url;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail_url = thumbnail;
    }

    public String getAgegroup_text() {
        return agegroup_text;
    }

    public void setAgegroup_text(String agegroup_text) {
        this.agegroup_text = agegroup_text;
    }

    public ContactDetail getContact() {
        return contact;
    }

    public void setContact(ContactDetail contact) {
        this.contact = contact;
    }

    public String getWeb_url() {
        return web_url;
    }

    public void setWeb_url(String web_url) {
        this.web_url = web_url;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getContacts() {
        return contacts;
    }

    public void setContacts(String contacts) {
        this.contacts = contacts;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getAge_group() {
        return age_group;
    }

    public void setAge_group(String age_group) {
        this.age_group = age_group;
    }

    public EventDate getEvent_date() {
        return event_date;
    }

    public void setEvent_date(EventDate event_date) {
        this.event_date = event_date;
    }

    public Timings getTimings() {
        return timings;
    }

    public void setTimings(Timings timings) {
        this.timings = timings;
    }

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public ArrayList<Batches> getBatches() {
        return batches;
    }

    public void setBatches(ArrayList<Batches> batches) {
        this.batches = batches;
    }

    public String getOrganised_by() {
        return organised_by;
    }

    public void setOrganised_by(String organised_by) {
        this.organised_by = organised_by;
    }

    public String getLattitude() {
        return lattitude;
    }

    public void setLattitude(String lattitude) {
        this.lattitude = lattitude;
    }

    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }

    public String getActivities() {
        return activities;
    }

    public void setActivities(String activities) {
        this.activities = activities;
    }

    /*public Facalities getFacilities() {
        return facilities;
    }
    public void setFacilities(Facalities facilities) {
        this.facilities = facilities;
    }*/
    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getFaviorite_count() {
        return faviorite_count;
    }

    public void setFaviorite_count(String faviorite_count) {
        this.faviorite_count = faviorite_count;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getMcity_id() {
        return mcity_id;
    }

    public void setMcity_id(String mcity_id) {
        this.mcity_id = mcity_id;
    }


    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the address
     */
    public String getAddress() {
        return address;
    }

    /**
     * @param address the address to set
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * @return the city_id
     */
    public String getCity_id() {
        return city_id;
    }

    /**
     * @param city_id the city_id to set
     */
    public void setCity_id(String city_id) {
        this.city_id = city_id;
    }

    /**
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return the landmark
     */
    public String getLandmark() {
        return landmark;
    }

    /**
     * @param landmark the landmark to set
     */
    public void setLandmark(String landmark) {
        this.landmark = landmark;
    }

    /**
     * @return the locality
     */
    public String getLocality() {
        return locality;
    }

    /**
     * @param locality the locality to set
     */
    public void setLocality(String locality) {
        this.locality = locality;
    }

    /**
     * @return the merto_station
     */
    public String getMerto_station() {
        return merto_station;
    }

    /**
     * @param merto_station the merto_station to set
     */
    public void setMerto_station(String merto_station) {
        this.merto_station = merto_station;
    }

    /**
     * @return the phone
     */
    public String getPhone() {
        return phone;
    }

    /**
     * @param phone the phone to set
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * @return the subaddress
     */
    public String getSubaddress() {
        return subaddress;
    }

    /**
     * @param subaddress the subaddress to set
     */
    public void setSubaddress(String subaddress) {
        this.subaddress = subaddress;
    }

    /**
     * @return the user_id
     */
    public String getUser_id() {
        return user_id;
    }

    /**
     * @param user_id the user_id to set
     */
    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    /**
     * @return the website
     */
    public String getWebsite() {
        return website;
    }

    /**
     * @param website the website to set
     */
    public void setWebsite(String website) {
        this.website = website;
    }

    /**
     * @return the startagegroup
     */
    public String getStartagegroup() {
        return startagegroup;
    }

    /**
     * @param startagegroup the startagegroup to set
     */
    public void setStartagegroup(String startagegroup) {
        this.startagegroup = startagegroup;
    }

    /**
     * @return the endagegroup
     */
    public String getEndagegroup() {
        return endagegroup;
    }

    /**
     * @param endagegroup the endagegroup to set
     */
    public void setEndagegroup(String endagegroup) {
        this.endagegroup = endagegroup;
    }

    /**
     * @return the rating
     */
    public float getRating() {
        return rating;
    }

    /**
     * @param rating the rating to set
     */
    public void setRating(float rating) {
        this.rating = rating;
    }

    /**
     * @return the locality_id
     */
    public String getLocality_id() {
        return locality_id;
    }

    /**
     * @param locality_id the locality_id to set
     */
    public void setLocality_id(String locality_id) {
        this.locality_id = locality_id;
    }

    /**
     * @return the reviewscount
     */
    public String getReviewscount() {
        return reviewscount;
    }

    /**
     * @param reviewscount the reviewscount to set
     */
    public void setReviewscount(String reviewscount) {
        this.reviewscount = reviewscount;
    }

    /**
     * @return the lat
     */
    public String getLat() {
        return lattitude;
    }

    /**
     * @param lat the lat to set
     */
    public void setLat(String lat) {
        this.lattitude = lat;
    }

    /**
     * @return the distance
     */
    public String getDistance() {
        return distance;
    }

    /**
     * @param distance the distance to set
     */
    public void setDistance(String distance) {
        this.distance = distance;
    }

    /**
     * @return the city_name
     */
    public String getCity_name() {
        return city_name;
    }

    /**
     * @param city_name the city_name to set
     */
    public void setCity_name(String city_name) {
        this.city_name = city_name;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    /**
     * @return the ecommerce
     */
    public String getEcommerce() {
        return ecommerce;
    }

    /**
     * @param ecommerce the ecommerce to set
     */
    public void setEcommerce(String ecommerce) {
        this.ecommerce = ecommerce;
    }

    /**
     * @return the ecommerce_url
     */
    public String getEcommerce_url() {
        return ecommerce_url;
    }

    /**
     * @param ecommerce_url the ecommerce_url to set
     */
    public void setEcommerce_url(String ecommerce_url) {
        this.ecommerce_url = ecommerce_url;
    }

    public ArrayList<Facalities> getFacilities() {
        return facilities;
    }

    public void setFacilities(ArrayList<Facalities> facilities) {
        this.facilities = facilities;
    }

    public String getBookmarkStatus() {
        return is_bookmark;
    }

    public void setBookmarkStatus(String is_bookmark) {
        this.is_bookmark = is_bookmark;
    }

    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        //anupama
        //	dest.writeString(slug);
        dest.writeString(thumbnail_url);
        //
        dest.writeString(id);
        dest.writeString(type);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeString(address);
        dest.writeString(city_id);
        dest.writeString(email);
        dest.writeString(landmark);
        dest.writeString(locality);
        dest.writeString(merto_station);
        dest.writeString(phone);
        dest.writeString(subaddress);
        dest.writeString(user_id);
        dest.writeString(website);
        dest.writeString(startagegroup);
        dest.writeString(endagegroup);
        dest.writeFloat(rating);
        dest.writeString(locality_id);
        dest.writeString(reviewscount);
        dest.writeString(lattitude);
        dest.writeString(longitude);
        dest.writeString(distance);
        dest.writeString(city_name);
        dest.writeString(pincode);
        dest.writeString(faviorite_count);
        dest.writeString(city);
        dest.writeString(mcity_id);
        //dest.writeParcelable(facilities, flags);
        dest.writeString(start_date);
        dest.writeString(end_date);
        dest.writeString(ecommerce);
//		dest.writeByte((byte) (ecommerce ? 1 : 0));   
        dest.writeString(ecommerce_url);
        dest.writeString(organised_by);
        dest.writeTypedList(batches);
        if (timings == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeInt((byte) 1);
            timings.writeToParcel(dest, flags);
        }
        dest.writeTypedList(facilities);
        if (event_date == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeInt((byte) 1);
            event_date.writeToParcel(dest, flags);
        }
        dest.writeString(age_group);
        dest.writeString(cost);
        dest.writeString(contacts);
        dest.writeString(about);
        if (contact == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeInt((byte) 1);
            contact.writeToParcel(dest, flags);
        }
        dest.writeString(is_bookmark);
    }

    private BusinessDataListing() {
        batches = new ArrayList<Batches>();
        facilities = new ArrayList<Facalities>();
    }


    public BusinessDataListing(Parcel in) {
        this();
        //anupama
        //this.slug=in.readString();
        this.thumbnail_url = in.readString();

        //
        this.id = in.readString();
        this.type = in.readString();
        this.name = in.readString();
        this.description = in.readString();
        this.address = in.readString();
        this.city_id = in.readString();
        this.email = in.readString();
        this.landmark = in.readString();
        this.locality = in.readString();
        this.merto_station = in.readString();
        this.phone = in.readString();
        this.subaddress = in.readString();
        this.user_id = in.readString();
        this.website = in.readString();
        this.startagegroup = in.readString();
        this.endagegroup = in.readString();
        this.rating = in.readFloat();
        this.locality_id = in.readString();
        this.reviewscount = in.readString();
        this.lattitude = in.readString();
        this.longitude = in.readString();
        this.distance = in.readString();
        this.city_name = in.readString();
        this.city_name = in.readString();
        this.city_name = in.readString();
        this.city_name = in.readString();
        this.city_name = in.readString();
        //this.facilities=in.readParcelable(Facalities.class.getClassLoader());
        this.start_date = in.readString();
        this.end_date = in.readString();
//		this.ecommerce = in.readByte() != 0;
        this.ecommerce = in.readString();
        this.ecommerce_url = in.readString();
        this.organised_by = in.readString();
        in.readTypedList(batches, Batches.CREATOR);
        int timings_flag = in.readByte();
        if (timings_flag == 1) {
            timings = new Timings(in);
        }
        in.readTypedList(facilities, Facalities.CREATOR);
        int event_flag = in.readByte();
        if (event_flag == 1) {
            event_date = new EventDate(in);
        }
        this.age_group = in.readString();
        this.cost = in.readString();
        this.contacts = in.readString();
        this.about = in.readString();
        int contact_flag = in.readByte();
        if (contact_flag == 1) {
            contact = new ContactDetail(in);
        }
        this.is_bookmark = in.readString();
    }


    public static final Parcelable.Creator<BusinessDataListing> CREATOR = new Parcelable.Creator<BusinessDataListing>() {

        public BusinessDataListing createFromParcel(Parcel in) {
            return new BusinessDataListing(in);
        }

        public BusinessDataListing[] newArray(int size) {
            return new BusinessDataListing[size];
        }
    };


}
