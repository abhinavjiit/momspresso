package com.mycity4kids.ui.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mycity4kids.R;
import com.mycity4kids.constants.AppConstants;

import java.util.ArrayList;


public class TutorialFragment extends Fragment {

    private View view;
    private int mPosition;
    private ImageView img_image;
    private TextView txvTitle;
    ArrayList<String> titleList;
    ArrayList<Integer> pagerImagesList;
    private ArrayList<String> pagerColorList;
    private TextView txvDesc;
    private ArrayList<String> descList;
    private RelativeLayout lnrRoot;
    private LinearLayout indicatoreLayout;
    private ImageView one, two, three, four, five, six;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        initilaize();
        view = inflater.inflate(R.layout.fragment_tutorial, container, false);

        return view;
    }

    private void initilaize() {


        titleList = new ArrayList<String>();
        descList = new ArrayList<String>();
        pagerImagesList = new ArrayList<Integer>();
        pagerColorList = new ArrayList<String>();

        titleList.add("mycity4kids");
        descList.add("#ParentingFromTheSamePage");
        pagerImagesList.add(R.drawable.tutorial_1);
        pagerColorList.add("#7388ff");

        titleList.add("Organize");
        descList.add("Your kids daily schedule with a colour-coded Calendar and To-do list");
        pagerImagesList.add(R.drawable.tutorial_2);
        pagerColorList.add("#10ddd0");

        titleList.add("Discover");
        descList.add("Great Events and Resources for children in the city");
        pagerImagesList.add(R.drawable.tutorial_3);
        pagerColorList.add("#fd7c5f");

        titleList.add("Learn");
        descList.add("from other Mums & Experts with the best Parenting blogs");
        pagerImagesList.add(R.drawable.tutorial_4);
        pagerColorList.add("#ffad53");


        titleList.add("Involve");
        descList.add("your Spouse by creating a Shared Calendar and To-Do List that can be jointly accessed");
        pagerImagesList.add(R.drawable.tutorial_5);
        pagerColorList.add("#ff548e");


    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPosition = getArguments().getInt(AppConstants.SLIDER_POSITION);
        img_image = (ImageView) view.findViewById(R.id.img_image);
        txvTitle = (TextView) view.findViewById(R.id.txvTitle);
        txvDesc = (TextView) view.findViewById(R.id.txvDesc);
        lnrRoot = (RelativeLayout) view.findViewById(R.id.lnrRoot);
        indicatoreLayout = (LinearLayout) view.findViewById(R.id.indicator_layout);
        one = (ImageView) view.findViewById(R.id.one);
        two = (ImageView) view.findViewById(R.id.two);
        three = (ImageView) view.findViewById(R.id.three);
        four = (ImageView) view.findViewById(R.id.four);
        five = (ImageView) view.findViewById(R.id.five);
//        six = (ImageView) view.findViewById(R.id.six);


        setImageInPager();
    }

    private void setImageInPager() {
        txvTitle.setText(titleList.get(mPosition));
        txvDesc.setText(descList.get(mPosition));
        img_image.setImageResource(pagerImagesList.get(mPosition));
        lnrRoot.setBackgroundColor(Color.parseColor(pagerColorList.get(mPosition)));

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
            case 5:
                five.setAlpha(0.8f);
                break;

        }
    }

}
