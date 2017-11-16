package com.mycity4kids.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseFragment;
import com.mycity4kids.R;
import com.mycity4kids.editor.EditorPostActivity;
import com.mycity4kids.models.response.TrendingListingResult;
import com.mycity4kids.preference.SharedPrefUtils;

import java.util.ArrayList;

/**
 * Created by hemant on 2/8/17.
 */
public class UploadVideoInfoFragment extends BaseFragment implements View.OnClickListener {
    private TextView getStartedTextView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.upload_video_info_fragment, container, false);

        getStartedTextView = (TextView) view.findViewById(R.id.getStartedTextView);

        getStartedTextView.setOnClickListener(this);
        return view;
    }

    @Override
    protected void updateUi(Response response) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.getStartedTextView:
                SharedPrefUtils.setFirstVideoUploadFlag(getActivity(), true);
                ChooseVideoUploadOptionDialogFragment chooseVideoUploadOptionDialogFragment = new ChooseVideoUploadOptionDialogFragment();
                FragmentManager fm = getChildFragmentManager();
                Bundle _args = new Bundle();
                _args.putString("activity", "dashboard");
                chooseVideoUploadOptionDialogFragment.setArguments(_args);
                chooseVideoUploadOptionDialogFragment.setCancelable(true);
                chooseVideoUploadOptionDialogFragment.show(fm, "Choose video option");
                break;
        }
    }

}