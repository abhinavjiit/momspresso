package com.mycity4kids.ui.fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseFragment;
import com.mycity4kids.R;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.ui.activity.ActivityLogin;
import com.mycity4kids.utils.PermissionUtil;
import com.mycity4kids.widget.CustomFontTextView;

import java.util.ArrayList;

/**
 * Created by hemant on 5/6/17.
 */
public class SignInFragment extends BaseFragment implements View.OnClickListener {

    private static final int REQUEST_INIT_PERMISSION = 1;
    private static String[] PERMISSIONS_INIT = {Manifest.permission.GET_ACCOUNTS};

    private View view;
    private RelativeLayout lnrRoot;
    private LayoutInflater mInflator;
    private CustomFontTextView facebookLoginTextView;
    private CustomFontTextView googleLoginTextView;
    private CustomFontTextView loginEmailTextView;
    private CustomFontTextView signupTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.signin_fragment, container, false);
        Utils.pushOpenScreenEvent(getActivity(), "Dashboard Fragment", SharedPrefUtils.getUserDetailModel(getActivity()).getDynamoId() + "");
        mInflator = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        lnrRoot = (RelativeLayout) view.findViewById(R.id.rootLayout);
        facebookLoginTextView = (CustomFontTextView) view.findViewById(R.id.connect_facebook);
        googleLoginTextView = (CustomFontTextView) view.findViewById(R.id.connect_googleplus);
        loginEmailTextView = (CustomFontTextView) view.findViewById(R.id.loginEmailTextView);
        signupTextView = (CustomFontTextView) view.findViewById(R.id.signupTextView);

        facebookLoginTextView.setOnClickListener(this);
        googleLoginTextView.setOnClickListener(this);
        loginEmailTextView.setOnClickListener(this);
        signupTextView.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.connect_facebook:
                ((ActivityLogin) getActivity()).loginWithFacebook();
//                if (ConnectivityUtils.isNetworkEnabled(getActivity())) {
//                    showProgressDialog(getString(R.string.please_wait));
//                    FacebookUtils.facebookLogin(this, this);
//                } else {
//                    showToast(getString(R.string.error_network));
//                }
                break;

            case R.id.connect_googleplus:
                if (Build.VERSION.SDK_INT >= 23) {
                    if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.GET_ACCOUNTS)
                            != PackageManager.PERMISSION_GRANTED) {
                        Log.i("PERMISSIONS", "Get accounts permission has NOT been granted. Requesting permissions.");
                        requestGetAccountsPermissions();
                    } else {
                        ((ActivityLogin) getActivity()).loginWithGplus();
                    }
                } else {
                    ((ActivityLogin) getActivity()).loginWithGplus();
                }
                break;
            case R.id.loginEmailTextView:
                EmailLoginFragment emailLoginFragment = new EmailLoginFragment();
                Bundle mBundle = new Bundle();
                emailLoginFragment.setArguments(mBundle);
                ((ActivityLogin) getActivity()).replaceFragmentWithAnimation(emailLoginFragment, mBundle, true);

                break;
            case R.id.signupTextView:
                SignUpFragment signUpFragment = new SignUpFragment();
                Bundle bundle = new Bundle();
                signUpFragment.setArguments(bundle);
                ((ActivityLogin) getActivity()).replaceFragmentWithAnimation(signUpFragment, bundle, true);

                break;
            default:
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
                ((ActivityLogin) getActivity()).loginWithGplus();
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

    @Override
    protected void updateUi(Response response) {

    }
}
