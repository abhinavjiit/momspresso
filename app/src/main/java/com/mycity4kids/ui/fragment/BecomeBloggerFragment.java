package com.mycity4kids.ui.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import com.mycity4kids.R;
import com.mycity4kids.base.BaseFragment;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.ui.activity.ArticleChallengeOrTopicSelectionActivity;

/**
 * Created by hemant on 2/8/17.
 */
public class BecomeBloggerFragment extends BaseFragment {

    String[] titleArray;
    String[] descArray;
    private TextView getStartedTextView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.become_blogger_fragment, container, false);

        titleArray = getResources().getStringArray(R.array.become_blogger_title_array);
        descArray = getResources().getStringArray(R.array.become_blogger_desc_array);

        getStartedTextView = view.findViewById(R.id.getStartedTextView);

        ViewPager viewPager = view.findViewById(R.id.viewPager);
        final BecomeBloggerPagerAdapter adapter = new BecomeBloggerPagerAdapter(getChildFragmentManager(), 4);
        viewPager.setClipToPadding(false);
        viewPager.setPadding(80, 20, 80, 20);
        viewPager.setPageMargin(30);
        viewPager.setAdapter(adapter);

        getStartedTextView.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ArticleChallengeOrTopicSelectionActivity.class);
            startActivity(intent);
        });
        if (!SharedPrefUtils.getOriginalContentBlogClick(getActivity())) {
            showOriginalContentDialog();
        }
        return view;
    }

    private class BecomeBloggerPagerAdapter extends FragmentStatePagerAdapter {

        private int numOfTabs;

        BecomeBloggerPagerAdapter(FragmentManager fm, int numOfTabs) {
            super(fm);
            this.numOfTabs = numOfTabs;
        }

        @Override
        public Fragment getItem(int position) {

            Bundle bundle = new Bundle();
            bundle.putString("title", titleArray[position]);
            bundle.putString("desc", descArray[position]);
            bundle.putInt("position", position);
            BecomeBloggerTabFragment tab1 = new BecomeBloggerTabFragment();
            tab1.setArguments(bundle);
            return tab1;
        }

        @Override
        public int getCount() {
            return numOfTabs;
        }
    }

    private void showOriginalContentDialog() {
        if (getActivity() != null) {
            final Dialog dialog = new Dialog(getActivity());
            dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_original_content);
            dialog.setCancelable(false);
            dialog.findViewById(R.id.okBtn).setOnClickListener(view -> {
                SharedPrefUtils.setOriginalContentBlogClick(getActivity(), true);
                dialog.dismiss();
            });

            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        }
    }
}
