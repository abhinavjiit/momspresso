package com.mycity4kids.ui.fragment;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mycity4kids.R;
import com.mycity4kids.ui.activity.ActivityLogin;
import com.mycity4kids.ui.activity.CreateFamilyActivity;
import com.mycity4kids.ui.activity.LandingLoginActivity;

import java.util.ArrayList;


public class JoinFamilyFragment extends Fragment implements View.OnClickListener {

    private View view;
    private int mPosition;
    private ImageView img_image;
    private TextView txvTitle;
    ArrayList<String> titleList;
    ArrayList<Integer> pagerImagesList;
    private ArrayList<String> pagerColorList;
    private TextView txvDesc;
    private ArrayList<String> descList;
    private LinearLayout lnrRoot;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        view = inflater.inflate(R.layout.aa_joinfamily, container, false);

        TextView login = (TextView) view.findViewById(R.id.email_login_btn);
        login.setPaintFlags(login.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        view.findViewById(R.id.craete_family).setOnClickListener(this);
        view.findViewById(R.id.join_family).setOnClickListener(this);
        view.findViewById(R.id.email_login_btn).setOnClickListener(this);

        return view;
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.craete_family:
                startActivity(new Intent(getActivity(), LandingLoginActivity.class));
//                startActivity(new Intent(getActivity(), CreateFamilyActivity.class));

                break;

            case R.id.join_family:
                Intent i = new Intent(getActivity(), ActivityLogin.class);
                i.putExtra("frmJoinFamily", true);
                startActivity(i);
                break;

            case R.id.email_login_btn:
                startActivity(new Intent(getActivity(), ActivityLogin.class));
                break;
        }

    }
}
