package com.mycity4kids.ui.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toolbar;

import com.mycity4kids.R;

import com.mycity4kids.ui.fragment.About;
import com.mycity4kids.ui.fragment.Contactdetails;

public class Mainprofile extends AppCompatActivity {
    ViewPager viewPager;
    android.support.v7.widget.Toolbar toolbar;
    TabLayout tabLayout;
    ViewPagerAdapter viewPagerAdapter;
    NestedScrollView nestedScrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainprofile);
        viewPager = (ViewPager) findViewById(R.id.id_viewpager);
        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.id_toolbar);
        //toolbar.setTitle("profile");
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitleTextColor(0xFFFFFFFF);
        tabLayout = (TabLayout) findViewById(R.id.id_tabs);
        nestedScrollView = (NestedScrollView) findViewById(R.id.nested);
        nestedScrollView.setFillViewport(true);
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

    }

//    private class ViewPagerAdapter {
//        public ViewPagerAdapter(FragmentManager supportFragmentManager) {
//
//        }
//    }

    public class ViewPagerAdapter extends FragmentPagerAdapter {

        public ViewPagerAdapter(android.support.v4.app.FragmentManager fm) {
            super(fm);
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {

            switch (position) {
                case 0:
                    return new About();
                default:
                    return new Contactdetails();
            }


        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            String title = null;
            if (position == 0) {
                title = "About";
            } else if (position == 1) {
                title = "Contact Detalis";
            }

            return title;
        }
    }
}