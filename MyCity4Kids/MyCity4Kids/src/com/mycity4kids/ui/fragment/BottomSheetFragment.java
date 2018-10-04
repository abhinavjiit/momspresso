package com.mycity4kids.ui.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.mycity4kids.R;

public class BottomSheetFragment extends BottomSheetDialogFragment {
    RelativeLayout relativeLayout;
    public BottomSheetFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_bottom_sheet_dialog, container, false);
        relativeLayout=(RelativeLayout)v.findViewById(R.id.bottomsheet);
    return v;
    }

    @Override
    public void onResume() {
        super.onResume();
   relativeLayout.setVisibility(View.INVISIBLE);



    }
}