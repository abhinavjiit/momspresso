package com.mycity4kids.ui.fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
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
import com.mycity4kids.utils.AppUtils;
import com.mycity4kids.utils.PermissionUtil;

import java.util.ArrayList;


public class TutorialFragment extends Fragment implements View.OnClickListener {

    private static final int REQUEST_INIT_PERMISSION = 1;
    private static String[] PERMISSIONS_INIT = {Manifest.permission.GET_ACCOUNTS};

    private View view;
    private TextView signinTextView;
    private TextView getStartedTextView;
    private TextView txvTitle;
    private TextView txvDesc;
    private RelativeLayout lnrRoot;
    private ImageView one, two, three, four, five;
//    private TextView facebookTextView, googlePlusTextView;

    private int mPosition;
    ArrayList<String> titleList;
    ArrayList<Integer> pagerImagesList;
    private ArrayList<String> pagerColorList;
    private ArrayList<String> descList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        initilaize();
        view = inflater.inflate(R.layout.fragment_tutorial, container, false);
//        googlePlusTextView = (TextView) view.findViewById(R.id.connect_gplus);
//        facebookTextView = (TextView) view.findViewById(R.id.connect_facebook);
        signinTextView = (TextView) view.findViewById(R.id.signinTextView);
        getStartedTextView = (TextView) view.findViewById(R.id.getStartedTextView);

//        googlePlusTextView.setOnClickListener(this);
//        facebookTextView.setOnClickListener(this);
        signinTextView.setOnClickListener(this);
        getStartedTextView.setOnClickListener(this);

        return view;
    }

    private void initilaize() {

        titleList = new ArrayList<String>();
        descList = new ArrayList<String>();
        pagerImagesList = new ArrayList<Integer>();
        pagerColorList = new ArrayList<String>();
        String[] titleArray = getResources().getStringArray(R.array.onboarding_title_array);
        String[] descArray = getResources().getStringArray(R.array.onboarding_desc_array);
        titleList.add(titleArray[0]);
        descList.add(descArray[0]);
        pagerImagesList.add(R.drawable.onboarding_1);
        pagerColorList.add("#7388ff");

        titleList.add(titleArray[1]);
        descList.add(descArray[1]);
        pagerImagesList.add(R.drawable.onboarding_2);
        pagerColorList.add("#10ddd0");

        titleList.add(titleArray[2]);
        descList.add(descArray[2]);
        pagerImagesList.add(R.drawable.onboarding_3);
        pagerColorList.add("#fd7c5f");


        titleList.add(titleArray[3]);
        descList.add(descArray[3]);
        pagerImagesList.add(R.drawable.onboarding_4);
        pagerColorList.add("#fd7c5f");
//        titleList.add("Learn");
//        descList.add("from other Mums & Experts with the best Parenting blogs");
//        pagerImagesList.add(R.drawable.tutorial_4);
//        pagerColorList.add("#ffad53");
//
//
//        titleList.add("Involve");
//        descList.add("your Spouse by creating a Shared Calendar which can be jointly accessed");
//        pagerImagesList.add(R.drawable.tutorial_5);
//        pagerColorList.add("#ff548e");


    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPosition = getArguments().getInt(AppConstants.SLIDER_POSITION);
//        img_image = (ImageView) view.findViewById(R.id.img_image);
        txvTitle = (TextView) view.findViewById(R.id.txvTitle);
        txvDesc = (TextView) view.findViewById(R.id.txvDesc);
        lnrRoot = (RelativeLayout) view.findViewById(R.id.lnrRoot);
        one = (ImageView) view.findViewById(R.id.one);
        two = (ImageView) view.findViewById(R.id.two);
        three = (ImageView) view.findViewById(R.id.three);
        four = (ImageView) view.findViewById(R.id.four);
//        four = (ImageView) view.findViewById(R.id.four);
//        five = (ImageView) view.findViewById(R.id.five);

        setImageInPager();
    }

    private void setImageInPager() {
        txvTitle.setText(AppUtils.fromHtml(titleList.get(mPosition)));
        txvDesc.setText(AppUtils.fromHtml(descList.get(mPosition)));
//        img_image.setImageResource(pagerImagesList.get(mPosition));
//        lnrRoot.setBackgroundColor(Color.parseColor(pagerColorList.get(mPosition)));
        lnrRoot.setBackgroundResource(pagerImagesList.get(mPosition));
        switch (mPosition + 1) {
            case 1:
                one.setAlpha(0.8f);
                break;

            case 2:
                two.setAlpha(0.8f);
                break;
            case 3:
                three.setAlpha(0.8f);
                break;
            case 4:
                four.setAlpha(0.8f);
                break;
//            case 5:
//                five.setAlpha(0.8f);
//                break;

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.signinTextView: {
                Intent intent = new Intent(getActivity(), ActivityLogin.class);
                intent.putExtra(AppConstants.LAUNCH_FRAGMENT, AppConstants.FRAGMENT_SIGNIN);
                startActivity(intent);
            }
            break;
            case R.id.getStartedTextView: {
                Intent intent = new Intent(getActivity(), ActivityLogin.class);
                intent.putExtra(AppConstants.LAUNCH_FRAGMENT, AppConstants.FRAGMENT_SIGNUP);
                startActivity(intent);
            }
            break;
        }
    }

}
