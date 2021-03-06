package com.mycity4kids.ui.fragment;

import static android.app.Activity.RESULT_OK;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.viewpager.widget.ViewPager;
import com.google.android.material.tabs.TabLayout;
import com.mycity4kids.R;
import com.mycity4kids.base.BaseFragment;
import com.mycity4kids.ui.activity.DashboardActivity;
import com.mycity4kids.ui.activity.GroupsListingActivity;
import com.mycity4kids.utils.AppUtils;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class GroupsViewFragment extends BaseFragment {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private com.getbase.floatingactionbutton.FloatingActionButton createFabButton;
    private String selectedTab = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_groupsview, container, false);
        viewPager = fragmentView.findViewById(R.id.viewpager);
        tabLayout = fragmentView.findViewById(R.id.tablayout);
        createFabButton = fragmentView.findViewById(R.id.createFabButton);
        createFabButton.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), GroupsListingActivity.class);
            intent.putExtra("isMember", true);
            intent.putExtra("comingFrom", "myFeed");
            startActivityForResult(intent, 2000);
        });

        if (getArguments() != null) {
            selectedTab = getArguments().getString("selectedTab");
        }
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.groups_groups_myfeed)));
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.groups_sections_myfeed)));
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.groups_sections_polls)));
        AppUtils.changeTabsFont(tabLayout);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                if (tab.getPosition() == 0 || tab.getPosition() == 2) {
                    createFabButton.setVisibility(View.GONE);
                } else {
                    createFabButton.setVisibility(View.VISIBLE);

                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0 || tab.getPosition() == 2) {
                    createFabButton.setVisibility(View.GONE);
                } else {
                    createFabButton.setVisibility(View.VISIBLE);

                }
            }
        });

        GroupsViewFragmentPagerAdapter adapter = new GroupsViewFragmentPagerAdapter(getChildFragmentManager(),
                tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        if ("group_list".equals(selectedTab)) {
            if (getActivity() != null) {
                ((DashboardActivity) getActivity()).showGroupsCoachmark();
            }
            viewPager.setCurrentItem(0);
        } else {
            viewPager.setCurrentItem(1);
        }

        return fragmentView;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 2000) {

                GroupsViewFragmentPagerAdapter adapter = new GroupsViewFragmentPagerAdapter(getChildFragmentManager(),
                        tabLayout.getTabCount());
                viewPager.setAdapter(adapter);
                viewPager.setCurrentItem(1);

            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        EventBus.getDefault().unregister(this);

    }

    @Override
    public void onStart() {
        super.onStart();

        EventBus.getDefault().register(this);

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(String click) {
        if (click.equals("clicked")) {
            createFabButton.setVisibility(View.GONE);
        } else {
            createFabButton.setVisibility(View.VISIBLE);
        }
    }
}
