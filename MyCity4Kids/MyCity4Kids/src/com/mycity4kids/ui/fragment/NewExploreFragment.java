package com.mycity4kids.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseFragment;
import com.mycity4kids.R;
import com.mycity4kids.models.response.CityInfoItem;
import com.mycity4kids.ui.adapter.ExplorePagerAdapter;

/**
 * Created by hemant on 7/8/17.
 */
public class NewExploreFragment extends BaseFragment implements View.OnClickListener, CityListingDialogFragment.IChangeCity {

    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.new_explore_fragment, container, false);

        tabLayout = (TabLayout) view.findViewById(R.id.tab_layout);
        viewPager = (ViewPager) view.findViewById(R.id.pager);

        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.explore_section_events)));
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.explore_section_resources)));
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.explore_section_top_things)));

        final ExplorePagerAdapter adapter = new ExplorePagerAdapter(getChildFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        return view;
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    protected void updateUi(Response response) {

    }

    @Override
    public void onCitySelect(CityInfoItem cityItem) {

    }

    @Override
    public void onOtherCitySelect(int pos, String cityName) {

    }
}
