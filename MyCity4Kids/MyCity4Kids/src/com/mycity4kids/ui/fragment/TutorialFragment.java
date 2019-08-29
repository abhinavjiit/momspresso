package com.mycity4kids.ui.fragment;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mycity4kids.R;
import com.mycity4kids.constants.AppConstants;

import java.util.ArrayList;


public class TutorialFragment extends Fragment implements View.OnClickListener {

    private ArrayList<String> titleList;
    private View view;
    private RelativeLayout lnrRoot;
    private TextView tutorial_desc_1, tutorial_desc_2, tutorial_desc_3, tutorial_desc_4, tutorial_desc_5;
    private int mPosition;
    private ArrayList<Integer> pagerImagesList;
    private TextView txvTitle;
    private ImageView onboardingImageView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        initilaize();
        view = inflater.inflate(R.layout.fragment_tutorial, container, false);
        txvTitle = (TextView) view.findViewById(R.id.txvTitle);
        onboardingImageView = (ImageView) view.findViewById(R.id.onboardingImageView);

        tutorial_desc_1 = (TextView) view.findViewById(R.id.tutorial_desc_1);
        tutorial_desc_2 = (TextView) view.findViewById(R.id.tutorial_desc_2);
        tutorial_desc_3 = (TextView) view.findViewById(R.id.tutorial_desc_3);
        tutorial_desc_4 = (TextView) view.findViewById(R.id.tutorial_desc_4);
        tutorial_desc_5 = (TextView) view.findViewById(R.id.tutorial_desc_5);

        return view;
    }

    private void initilaize() {
        titleList = new ArrayList<String>();
        String[] titleArray = getResources().getStringArray(R.array.onboarding_title_array);

        titleList.add(titleArray[0]);
        titleList.add(titleArray[1]);
        titleList.add(titleArray[2]);
        titleList.add(titleArray[3]);
        titleList.add(titleArray[4]);

        pagerImagesList = new ArrayList<Integer>();

        pagerImagesList.add(R.drawable.onboarding_1);
        pagerImagesList.add(R.drawable.onboarding_2);
        pagerImagesList.add(R.drawable.onboarding_3);
        pagerImagesList.add(R.drawable.onboarding_4);
        pagerImagesList.add(R.drawable.onboarding_5);


    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPosition = getArguments().getInt(AppConstants.SLIDER_POSITION);
        lnrRoot = (RelativeLayout) view.findViewById(R.id.lnrRoot);

        setImageInPager();
    }

    private void setImageInPager() {
        lnrRoot.setBackgroundResource(pagerImagesList.get(mPosition));
        txvTitle.setText(titleList.get(mPosition));
        tutorial_desc_1.setVisibility(View.GONE);
        tutorial_desc_2.setVisibility(View.GONE);
        tutorial_desc_3.setVisibility(View.GONE);
        tutorial_desc_4.setVisibility(View.GONE);
        tutorial_desc_5.setVisibility(View.GONE);
        switch (mPosition + 1) {
            case 1:
                tutorial_desc_1.setVisibility(View.VISIBLE);
                onboardingImageView.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.onboarding_1));
                break;
            case 2:
                tutorial_desc_2.setVisibility(View.VISIBLE);
                onboardingImageView.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.onboarding_2));
                break;
            case 3:
                tutorial_desc_3.setVisibility(View.VISIBLE);
                onboardingImageView.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.onboarding_3));
                break;
            case 4:
                tutorial_desc_4.setVisibility(View.VISIBLE);
                onboardingImageView.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.onboarding_4));
                break;
            case 5:
                tutorial_desc_5.setVisibility(View.VISIBLE);
                onboardingImageView.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.onboarding_5));
                break;
        }
    }

    @Override
    public void onClick(View v) {
    }

}
