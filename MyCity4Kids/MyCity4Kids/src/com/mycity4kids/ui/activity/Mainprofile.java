package com.mycity4kids.ui.activity;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
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
    CoordinatorLayout mainprofileparentlayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainprofile);
        viewPager = (ViewPager) findViewById(R.id.id_viewpager);
        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.id_toolbar);
        mainprofileparentlayout = (CoordinatorLayout) findViewById(R.id.mainprofile_parent_layout);
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

    @Override
    protected void onResume() {
        super.onResume();
        setupUI(mainprofileparentlayout);
    }

    public void setupUI(View view) {

        // Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard(Mainprofile.this);
                    return false;
                }
            });
        }

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView);
            }
        }
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }

}