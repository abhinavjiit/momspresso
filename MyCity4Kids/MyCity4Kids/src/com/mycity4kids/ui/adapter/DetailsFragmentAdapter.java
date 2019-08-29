package com.mycity4kids.ui.adapter;
/**
 * deepanker-chaudhary
 */

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.TabWidget;

import com.kelltontech.utils.StringUtils;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.models.businesseventdetails.DetailMap;
import com.mycity4kids.models.businesseventdetails.DetailsGallery;
import com.mycity4kids.models.businesseventdetails.DetailsResponse;
import com.mycity4kids.models.businesseventdetails.DetailsReviews;
import com.mycity4kids.models.businesslist.BusinessDataListing;
import com.mycity4kids.ui.activity.BusinessDetailsActivity;
import com.mycity4kids.ui.fragment.GalleryFragment;
import com.mycity4kids.ui.fragment.InformationFragment;
import com.mycity4kids.ui.fragment.MapFragment;
import com.mycity4kids.ui.fragment.ReviewFragment;
import com.mycity4kids.utils.tabwidget.MyTabFactory;

import java.util.ArrayList;

public class DetailsFragmentAdapter extends FragmentPagerAdapter implements TabHost.OnTabChangeListener, ViewPager.OnPageChangeListener {
    private TabHost mTabHost;
    private ViewPager mPager;
    private Context mContext;
    private DetailsResponse mDetailsResponse;
    private int mEventOrBusiness;
    private int mCategoryId;
    private String mDistance;


    public DetailsFragmentAdapter(BusinessDetailsActivity pContext, TabHost pTabHost, ViewPager pViewPager, DetailsResponse responseData, int pEventOrBusines, int categoryId, String distance) {
        super(pContext.getSupportFragmentManager());
        mTabHost = pTabHost;
        mPager = pViewPager;
        mContext = pContext;
        mDetailsResponse = responseData;
        mEventOrBusiness = pEventOrBusines;
        mCategoryId = categoryId;
        mDistance = distance;
        //	mTabHost.setup();
        //mTabHost.setOnTabChangedListener(this);
        //	mPager.setAdapter(this);
        //mPager.setOnPageChangeListener(this);
        //	mPager.setOffscreenPageLimit(4);


    }


    public void addTab(TabHost.TabSpec tabSpec) {
        tabSpec.setContent(new MyTabFactory(mContext));
        //   String tag = tabSpec.getTag();

		/*  TabInfo info = new TabInfo(tag, clss, args);
           mTabHost.add(info);*/
        mTabHost.addTab(tabSpec);
        notifyDataSetChanged();
    }


    @Override
    public Fragment getItem(int position) {
        Bundle args = new Bundle();
        switch (position) {
            case 0:

                InformationFragment infoFragment = new InformationFragment();
                BusinessDataListing information = mDetailsResponse.getResult().getData().getInfo();
                args.putParcelable("BusinessInfo", information);
                args.putInt(Constants.PAGE_TYPE, mEventOrBusiness);
                args.putInt(Constants.CATEGORY_ID, mCategoryId);
                args.putString(Constants.DISTANCE, mDistance);
                infoFragment.setArguments(args);
                return infoFragment;
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

                if (!StringUtils.isNullOrEmpty(mDetailsResponse.getResult().getData().getInfo().getAddress()) && mDetailsResponse.getResult().getData().getInfo().getAddress().equalsIgnoreCase("null")) {
                    args.putString("addressData", mDetailsResponse.getResult().getData().getInfo().getAddress() + " " + mDetailsResponse.getResult().getData().getInfo().getSubaddress() + " " + mDetailsResponse.getResult().getData().getInfo().getLocality());
                } else {
                    args.putString("addressData", mDetailsResponse.getResult().getData().getInfo().getSubaddress() + " " + mDetailsResponse.getResult().getData().getInfo().getLocality());
                }
                mapFragment.setArguments(args);
                return mapFragment;

        }

    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return 4;
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
        int pos = this.mPager.getCurrentItem();
        this.mTabHost.setCurrentTab(pos);

    }

    @Override
    public void onPageSelected(int position) {
        TabWidget widget = mTabHost.getTabWidget();
        int oldFocusability = widget.getDescendantFocusability();
        widget.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
        mTabHost.setCurrentTab(position);
        widget.setDescendantFocusability(oldFocusability);
//      switch (position) {
//	case  0:
//		((InformationFragment)this.getItemPosition(0)).onUpdate();
//		break;
//
//	default:
//		break;
//	}

    }

    @Override
    public void onTabChanged(String tabId) {

        int pos = this.mTabHost.getCurrentTab();
        this.mPager.setCurrentItem(pos);

    }


    private InformationFragment infoDetails() {
        Bundle args = new Bundle();
        InformationFragment infoFragment = new InformationFragment();
        BusinessDataListing information = mDetailsResponse.getResult().getData().getInfo();
        args.putParcelable("BusinessInfo", information);
        args.putInt(Constants.PAGE_TYPE, mEventOrBusiness);
        args.putInt(Constants.CATEGORY_ID, mCategoryId);
        args.putString(Constants.DISTANCE, mDistance);
        infoFragment.setArguments(args);
        return infoFragment;
    }

/*
@Override
public void destroyItem(ViewGroup container, int position, Object object) {
	super.destroyItem(container, position, object);
}*/


}
