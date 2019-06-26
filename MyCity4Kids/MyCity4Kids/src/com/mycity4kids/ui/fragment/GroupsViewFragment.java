package com.mycity4kids.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseFragment;
import com.mycity4kids.R;
import com.mycity4kids.ui.activity.GroupsListingActivity;
import com.mycity4kids.utils.AppUtils;

public class GroupsViewFragment extends BaseFragment {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private FloatingActionButton createFabButton;


    @Override
    protected void updateUi(Response response) {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_groupsview, container, false);
        viewPager = fragmentView.findViewById(R.id.viewpager);
        tabLayout = fragmentView.findViewById(R.id.tablayout);
        createFabButton = fragmentView.findViewById(R.id.createFabButton);
        createFabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), GroupsListingActivity.class);
                intent.putExtra("isMember", true);
                intent.putExtra("comingFrom", "myFeed");
                startActivity(intent);
            }
        });


        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.groups_groups_myfeed)));
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.groups_sections_myfeed)));
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.groups_sections_polls)));

        AppUtils.changeTabsFont(getActivity(), tabLayout);
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

        GroupsViewFragmentPagerAdapter adapter = new GroupsViewFragmentPagerAdapter(getChildFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(1);
        return fragmentView;
    }
}
