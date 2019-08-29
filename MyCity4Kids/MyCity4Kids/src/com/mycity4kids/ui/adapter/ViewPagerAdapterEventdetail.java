package com.mycity4kids.ui.adapter;


import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import android.util.Log;

import com.mycity4kids.constants.Constants;
import com.mycity4kids.models.businesseventdetails.DetailMap;
import com.mycity4kids.models.businesseventdetails.DetailsGallery;
import com.mycity4kids.models.businesseventdetails.DetailsResponse;
import com.mycity4kids.models.businesseventdetails.DetailsReviews;
import com.mycity4kids.models.businesslist.BusinessDataListing;
import com.mycity4kids.observablescrollview.CacheFragmentStatePagerAdapter;
import com.mycity4kids.ui.activity.BusinessDetailsActivity;
import com.mycity4kids.ui.fragment.GalleryFragment;
import com.mycity4kids.ui.fragment.InformationFragment;
import com.mycity4kids.ui.fragment.InformationFragmentBusiness;
import com.mycity4kids.ui.fragment.MapFragment;
import com.mycity4kids.ui.fragment.ReviewFragment;

import java.util.ArrayList;


public class ViewPagerAdapterEventdetail extends CacheFragmentStatePagerAdapter {

    CharSequence Titles[]; // This will Store the Titles of the Tabs which are Going to be passed when UserProfilePagerAdapter is created
    int NumbOfTabs; // Store the number of tabs, this will also be passed when the UserProfilePagerAdapter is created
    private Context mContext;
    private DetailsResponse mDetailsResponse;
    private int mEventOrBusiness;
    private int mCategoryId;
    private String mDistance;
    boolean isbusiness = false;

    // Build a Constructor and assign the passed Values to appropriate values in the class
    public ViewPagerAdapterEventdetail(BusinessDetailsActivity pContext, DetailsResponse responseData, int pEventOrBusines, int categoryId, String distance, FragmentManager fm, CharSequence mTitles[], int mNumbOfTabsumb, boolean isbusiness) {
        super(fm);
        Log.d("check", "in ViewPagerAdapterEventdetail ");
        mContext = pContext;
        mDetailsResponse = responseData;
        mEventOrBusiness = pEventOrBusines;
        mCategoryId = categoryId;
        mDistance = distance;
        this.Titles = mTitles;
        this.NumbOfTabs = mNumbOfTabsumb;
        this.isbusiness = isbusiness;
        Log.d("check", "isbusiness adapter " + isbusiness);
    }

    //This method return the fragment for the every position in the View Pager
    @Override
    public Fragment createItem(int position) {
        Bundle args = new Bundle();
        Log.d("check", "in args " + args);
        switch (position) {
            case 0:
                Log.d("check", "in position " + position);
                if (isbusiness) {
                    InformationFragmentBusiness infoFragment = new InformationFragmentBusiness();
                    BusinessDataListing information = mDetailsResponse.getResult().getData().getInfo();
                    args.putParcelable("BusinessInfo", information);
                    args.putInt(Constants.PAGE_TYPE, mEventOrBusiness);
                    args.putInt(Constants.CATEGORY_ID, mCategoryId);
                    args.putString(Constants.DISTANCE, mDistance);
                    args.putBoolean("isbusiness", isbusiness);
                    infoFragment.setArguments(args);
                    return infoFragment;
                } else {
                    InformationFragment infoFragment = new InformationFragment();
                    BusinessDataListing information = mDetailsResponse.getResult().getData().getInfo();
                    args.putParcelable("BusinessInfo", information);
                    args.putInt(Constants.PAGE_TYPE, mEventOrBusiness);
                    args.putInt(Constants.CATEGORY_ID, mCategoryId);
                    args.putString(Constants.DISTANCE, mDistance);
                    args.putBoolean("isbusiness", isbusiness);
                    infoFragment.setArguments(args);
                    return infoFragment;
                }
            case 1:
                GalleryFragment galleryFragment = new GalleryFragment();
                DetailsGallery gallery = mDetailsResponse.getResult().getData().getGallery();
                args.putParcelable("BusinessGallery", gallery);
                args.putInt(Constants.PAGE_TYPE, mEventOrBusiness);
                args.putString(Constants.BUSINESS_OR_EVENT_ID, mDetailsResponse.getResult().getData().getInfo().getId());
                galleryFragment.setArguments(args);

                return galleryFragment;
            case 2:

                ReviewFragment raviewFragment = new ReviewFragment();
                ArrayList<DetailsReviews> reviewDetails = mDetailsResponse.getResult().getData().getReviews();
                args.putParcelableArrayList("reviewDetails", reviewDetails);
                args.putInt(Constants.PAGE_TYPE, mEventOrBusiness);
                args.putParcelable("galleryData", mDetailsResponse.getResult().getData().getGallery());
                args.putInt(Constants.CATEGORY_ID, mCategoryId);
                args.putString(Constants.BUSINESS_OR_EVENT_ID, mDetailsResponse.getResult().getData().getInfo().getId());
                raviewFragment.setArguments(args);
                return raviewFragment;
            default:

                MapFragment mapFragment = new MapFragment();
                DetailMap mapDetails = mDetailsResponse.getResult().getData().getMap();
                args.putParcelable("mapDetails", mapDetails);
                args.putString("addressData", mDetailsResponse.getResult().getData().getInfo().getAddress() + " " + mDetailsResponse.getResult().getData().getInfo().getSubaddress() + " " + mDetailsResponse.getResult().getData().getInfo().getLocality());
                mapFragment.setArguments(args);
                return mapFragment;

        }
       /* if(position == 0) // if the position is 0 we are returning the First tab
        {
            Tab1 tab1 = new Tab1();
            return tab1;
        }
        else             // As we are having 2 tabs if the position is now 0 it must be 1 so we are returning second tab
        {
            Tab2 tab2 = new Tab2();
            return tab2;*/
    }


    // This method return the titles for the Tabs in the Tab Strip

    @Override
    public CharSequence getPageTitle(int position) {
        return Titles[position];
    }

    // This method return the Number of tabs for the tabs Strip

    @Override
    public int getCount() {
        return NumbOfTabs;
    }


}