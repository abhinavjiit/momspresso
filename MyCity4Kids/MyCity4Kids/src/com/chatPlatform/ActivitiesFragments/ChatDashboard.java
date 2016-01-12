package com.chatPlatform.ActivitiesFragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.chatPlatform.Adapters.JoinGroupAdapter;
import com.mycity4kids.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by anshul on 14/12/15.
 */
public class ChatDashboard extends AppCompatActivity implements JoinGroupAdapter.OnGroupJoinInterface {

        private Toolbar toolbar;
        private TabLayout tabLayout;
        private ViewPager viewPager;
      //  private TextView textView;
        private FloatingActionButton floatingActionButton;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.dashboard_chat);
            //    textView =(TextView)findViewById(R.id.button1);
                toolbar = (Toolbar) findViewById(R.id.toolbar);
            floatingActionButton=(FloatingActionButton) findViewById((R.id.createGroup));
                setSupportActionBar(toolbar);

                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setTitle("GROUPS");
                viewPager = (ViewPager) findViewById(R.id.viewpager);
                setupViewPager(viewPager);

                tabLayout = (TabLayout) findViewById(R.id.tabs);
                tabLayout.setupWithViewPager(viewPager);
       /*     Bundle extras = getIntent().getExtras();
          if (extras!=null)
          { String toOpen = extras.getString("toOpen");
              if (toOpen!=null)
              { if (toOpen.equals("publicFragment"))
            {
                FragmentManager fm = ChatDashboard.this.getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                PublicGroupListView fragment = new PublicGroupListView();
                if (fragment != null) {
                    // Replace current fragment by this new one
                    ft.replace(R.id.viewpager, fragment);
                       ft.commit();}
            }}}*/
            floatingActionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i=new Intent(ChatDashboard.this,CreateGroup.class);
                    startActivity(i);
                }
            });
         /*   textView=(TextView)findViewById(R.id.button1);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i=new Intent(ChatDashboard.this,AccessContacts.class);
                    startActivity(i);
                }
            });*/
        }
    ViewPagerAdapter adapter;
        private void setupViewPager(ViewPager viewPager) {
            adapter = new ViewPagerAdapter(getSupportFragmentManager());
                adapter.addFragment(new PrivateGroupListView(), "Private Group");
                adapter.addFragment(new PublicGroupListView(), "Public Group");
                adapter.addFragment(new JoinGroupListView(), "Join Group");
                viewPager.setAdapter(adapter);
        }

    @Override
    public void OnGroupJoined() {
        viewPager.setCurrentItem(1);
    }

   /* @Override
    public void OnGroupJoined() {

    }*/

    class ViewPagerAdapter extends FragmentPagerAdapter {
                private final List<Fragment> mFragmentList = new ArrayList<>();
                private final List<String> mFragmentTitleList = new ArrayList<>();

                public ViewPagerAdapter(FragmentManager manager) {
                        super(manager);
                }

                @Override
                public Fragment getItem(int position) {
                        return mFragmentList.get(position);
                }

                @Override
                public int getCount() {
                        return mFragmentList.size();
                }

                public void addFragment(Fragment fragment, String title) {
                        mFragmentList.add(fragment);
                        mFragmentTitleList.add(title);
                }

                @Override
                public CharSequence getPageTitle(int position) {
                        return mFragmentTitleList.get(position);
                }

        }
}
 /*extends AppCompatActivity{
        *//**
         * The {@link android.support.v4.view.PagerAdapter} that will provide
         * fragments for each of the sections. We use a
         * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
         * will keep every loaded fragment in memory. If this becomes too memory
         * intensive, it may be best to switch to a
         * {@link android.support.v4.app.FragmentStatePagerAdapter}.
         *//*
        SectionsPagerAdapter mSectionsPagerAdapter;

        *//**
         * The {@link ViewPager} that will host the section contents.
         *//*
        ViewPager mViewPager;

@Override
protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_chat);
    ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
    viewPager.setAdapter(new SectionsPagerAdapter(ChatDashboard.this,getSupportFragmentManager()));

    // Give the TabLayout the ViewPager
    TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
    tabLayout.setupWithViewPager(viewPager);
*//*final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the app.
        mSectionsPagerAdapter = new SectionsPagerAdapter(this,
        getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#004889")));
        *//**//*PagerTabStrip pagerTabStrip = (PagerTabStrip) findViewById(R.id.pager_header);
        pagerTabStrip.setDrawFullUnderline(true);
        pagerTabStrip.setTabIndicatorColor(Color.RED);*//**//*
        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager
        .setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
@Override
public void onPageSelected(int position) {
        actionBar.setSelectedNavigationItem(position);
        }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
        // Create a tab with text corresponding to the page title defined by
        // the adapter. Also specify this Activity object, which implements
        // the TabListener interface, as the callback (listener) for when
        // this tab is selected.
        actionBar.addTab(actionBar.newTab()
        .setText(mSectionsPagerAdapter.getPageTitle(i))
        .setTabListener(this));
        }*//*
        }

*//*@Override
public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
// When the given tab is selected, switch to the corresponding page in
        // the ViewPager.]
        mViewPager.setCurrentItem(tab.getPosition());
        }

@Override
public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

        }

@Override
public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

        }*//*
        }*/

