package com.mycity4kids.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kelltontech.ui.BaseFragment;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.editor.EditorPostActivity;
import com.mycity4kids.models.response.TrendingListingResult;
import com.mycity4kids.preference.SharedPrefUtils;

import java.util.ArrayList;

/**
 * Created by hemant on 2/8/17.
 */
public class BecomeBloggerFragment extends BaseFragment {
    String[] titleArray;
    String[] descArray;
    private TextView getStartedTextView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.become_blogger_fragment, container, false);

        titleArray = getResources().getStringArray(R.array.become_blogger_title_array);
        descArray = getResources().getStringArray(R.array.become_blogger_desc_array);

        getStartedTextView = (TextView) view.findViewById(R.id.getStartedTextView);

        ViewPager viewPager = (ViewPager) view.findViewById(R.id.viewPager);
        final BecomeBloggerPagerAdapter adapter = new BecomeBloggerPagerAdapter
                (getChildFragmentManager(), 4);
        viewPager.setClipToPadding(false);
        viewPager.setPadding(80, 20, 80, 20);
        viewPager.setPageMargin(30);
        viewPager.setAdapter(adapter);

        getStartedTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPrefUtils.setBecomeBloggerFlag(BaseApplication.getAppContext(), true);
                Intent intent = new Intent(getActivity(), EditorPostActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString(EditorPostActivity.TITLE_PARAM, "");
                bundle.putString(EditorPostActivity.CONTENT_PARAM, "");
                bundle.putString(EditorPostActivity.TITLE_PLACEHOLDER_PARAM,
                        getString(R.string.example_post_title_placeholder));
                bundle.putString(EditorPostActivity.CONTENT_PLACEHOLDER_PARAM,
                        getString(R.string.example_post_content_placeholder));
                bundle.putInt(EditorPostActivity.EDITOR_PARAM, EditorPostActivity.USE_NEW_EDITOR);
                bundle.putString("from", "DraftListViewActivity");
                intent.putExtras(bundle);
                FragmentManager fm = getActivity().getSupportFragmentManager();
                fm.popBackStack();
                fm.popBackStack();
                startActivity(intent);
            }
        });
        return view;
    }

    private class BecomeBloggerPagerAdapter extends FragmentStatePagerAdapter {
        private int mNumOfTabs;
        private ArrayList<TrendingListingResult> trendingListingResults;

        public BecomeBloggerPagerAdapter(FragmentManager fm, int NumOfTabs) {
            super(fm);
            this.mNumOfTabs = NumOfTabs;
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
            return mNumOfTabs;
        }
    }
}
