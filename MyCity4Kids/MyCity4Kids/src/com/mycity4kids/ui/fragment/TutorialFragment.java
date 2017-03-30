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
import com.mycity4kids.utils.PermissionUtil;

import java.util.ArrayList;


public class TutorialFragment extends Fragment implements View.OnClickListener {

    private static final int REQUEST_INIT_PERMISSION = 1;
    private static String[] PERMISSIONS_INIT = {Manifest.permission.GET_ACCOUNTS};

    private View view;
    private TextView loginTextView;
    private TextView txvTitle;
    private TextView txvDesc;
    private RelativeLayout lnrRoot;
    private ImageView one, two, three, four, five;
    private TextView facebookTextView, googlePlusTextView;

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
        googlePlusTextView = (TextView) view.findViewById(R.id.connect_gplus);
        facebookTextView = (TextView) view.findViewById(R.id.connect_facebook);
        loginTextView = (TextView) view.findViewById(R.id.txvLogin);

        googlePlusTextView.setOnClickListener(this);
        facebookTextView.setOnClickListener(this);
        loginTextView.setOnClickListener(this);

        return view;
    }

    private void initilaize() {

        titleList = new ArrayList<String>();
        descList = new ArrayList<String>();
        pagerImagesList = new ArrayList<Integer>();
        pagerColorList = new ArrayList<String>();

        titleList.add("Harnessing The Wisdom Of Mums");
        descList.add("");
        pagerImagesList.add(R.drawable.onboarding1);
        pagerColorList.add("#7388ff");

        titleList.add("Get Your Shot Of Mommy Wisdom");
        descList.add("Blogs, Videos & Experts");
        pagerImagesList.add(R.drawable.onboarding2);
        pagerColorList.add("#10ddd0");

        titleList.add("Discover Your City's Best");
        descList.add("Editorial Lists, Events & Resources");
        pagerImagesList.add(R.drawable.onboarding3);
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
//        four = (ImageView) view.findViewById(R.id.four);
//        five = (ImageView) view.findViewById(R.id.five);

        setImageInPager();
    }

    private void setImageInPager() {
        txvTitle.setText(titleList.get(mPosition));
        txvDesc.setText(descList.get(mPosition));
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
//            case 4:
//                four.setAlpha(0.8f);
//                break;
//            case 5:
//                five.setAlpha(0.8f);
//                break;

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.connect_facebook:
                ((TutorialActivity) getActivity()).loginWithFacebook();
                break;
            case R.id.connect_gplus:

                if (Build.VERSION.SDK_INT >= 23) {
                    if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.GET_ACCOUNTS)
                            != PackageManager.PERMISSION_GRANTED) {
                        Log.i("PERMISSIONS", "Get accounts permission has NOT been granted. Requesting permissions.");
                        requestGetAccountsPermissions();
                    } else {
                        ((TutorialActivity) getActivity()).loginWithGplus();
                    }
                } else {
                    ((TutorialActivity) getActivity()).loginWithGplus();
                }


                break;
            case R.id.txvLogin:
                Intent intent = new Intent(getActivity(), ActivityLogin.class);
                startActivity(intent);
                break;
        }
    }

    private void requestGetAccountsPermissions() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                Manifest.permission.GET_ACCOUNTS)) {
            Log.i("Permissions",
                    "Displaying get accounts permission rationale to provide additional context.");

            // Display a SnackBar with an explanation and a button to trigger the request.
            Snackbar.make(lnrRoot, R.string.permission_get_account_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            requestUngrantedPermissions();
                        }
                    })
                    .show();
        } else {
            requestUngrantedPermissions();
        }
    }

    private void requestUngrantedPermissions() {
        ArrayList<String> permissionList = new ArrayList<>();
        for (int i = 0; i < PERMISSIONS_INIT.length; i++) {
            if (ActivityCompat.checkSelfPermission(getActivity(), PERMISSIONS_INIT[i]) != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(PERMISSIONS_INIT[i]);
            }
        }
        String[] requiredPermission = permissionList.toArray(new String[permissionList.size()]);
        requestPermissions(requiredPermission, REQUEST_INIT_PERMISSION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (requestCode == REQUEST_INIT_PERMISSION) {
            Log.i("Permissions", "Received response for storage permissions request.");

            if (PermissionUtil.verifyPermissions(grantResults)) {
                Snackbar.make(lnrRoot, R.string.permision_available_init,
                        Snackbar.LENGTH_SHORT)
                        .show();
                ((TutorialActivity) getActivity()).loginWithGplus();
            } else {
                Log.i("Permissions", "storage permissions were NOT granted.");
                Snackbar.make(lnrRoot, R.string.permissions_not_granted,
                        Snackbar.LENGTH_SHORT)
                        .show();
            }

        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


}
