package com.mycity4kids.ui.fragment;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mycity4kids.R;
import com.mycity4kids.googlemap.models.TransitModel;
import com.mycity4kids.models.businesseventdetails.DetailMap;
import com.mycity4kids.ui.activity.BusinessDetailsActivity;

import java.util.ArrayList;

/**
 * Created by manish.soni on 30-06-2015.
 */
public class TransitMapFragment extends android.app.DialogFragment {

    ArrayList<TransitModel> tarnsitList;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.aa_transit, container,
                false);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        Window window = getDialog().getWindow();
        window.setGravity(Gravity.BOTTOM | Gravity.CENTER_VERTICAL);

        Bundle bundle = getArguments();
        if (bundle != null) {
            tarnsitList = bundle.getParcelableArrayList("transitlist");
        }

        System.out.println("tran list  " + tarnsitList);


        LinearLayout layout = (LinearLayout) rootView.findViewById(R.id.transit_lout);
        LayoutInflater inflaterLout = LayoutInflater.from(getActivity());
        if (tarnsitList != null) {
            for (TransitModel transitModel : tarnsitList) {
                TextView textView = (TextView) inflaterLout.inflate(R.layout.custom_transit_cell, null);
                textView.setTag(transitModel);
                textView.setText(transitModel.getInstructions());
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (v instanceof TextView) {
                            TextView textView = (TextView) v;
                            TransitModel model = (TransitModel) textView.getTag();
                            DetailMap mapInfo = new DetailMap();
                            mapInfo.setLatitude(model.getEndLatitude());
                            mapInfo.setLongitude(model.getEndLongitude());
                            ((BusinessDetailsActivity) getActivity()).callTransitBtn(mapInfo);
                        }

                        getDialog().dismiss();
                    }
                });
                layout.addView(textView);
            }
        }
//        layout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                if (v instanceof TextView) {
//                    TextView textView = (TextView) v;
//                    TransitModel model = (TransitModel) textView.getTag();
//                    DetailMap mapInfo = new DetailMap();
//                    mapInfo.setLatitude(model.getEndLatitude());
//                    mapInfo.setLongitude(model.getEndLongitude());
//                    ((BusinessDetailsActivity)getActivity()).callTransitBtn(mapInfo);
//                }
//
//                getDialog().dismiss();
//            }
//        });


        return rootView;
    }


}