package com.mycity4kids.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mycity4kids.R;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.ui.activity.ActivityLogin;
import com.mycity4kids.ui.activity.TutorialActivity;

import java.util.ArrayList;


public class UploadVideoFLFragment extends Fragment implements View.OnClickListener {

    private View view;
    private ImageView uploadTutorialImageView;

    private int mPosition;
    ArrayList<Integer> pagerImagesList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        initilaize();
        view = inflater.inflate(R.layout.upload_video_fl_fragment, container, false);
        uploadTutorialImageView = (ImageView) view.findViewById(R.id.uploadTutorialImageView);
        return view;
    }

    private void initilaize() {

        pagerImagesList = new ArrayList<Integer>();
        pagerImagesList.add(R.drawable.video_intro1);
        pagerImagesList.add(R.drawable.video_intro2);
        pagerImagesList.add(R.drawable.video_intro3);
        pagerImagesList.add(R.drawable.video_intro4);
        pagerImagesList.add(R.drawable.video_intro5);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPosition = getArguments().getInt(AppConstants.SLIDER_POSITION);
        setImageInPager();
    }

    private void setImageInPager() {
        uploadTutorialImageView.setImageResource(pagerImagesList.get(mPosition));
    }

    @Override
    public void onClick(View v) {

    }

}
