package com.mycity4kids.ui.fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.kelltontech.network.Response;
import com.kelltontech.utils.StringUtils;
import com.kelltontech.utils.ToastUtils;
import com.mycity4kids.R;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.models.response.CityInfoItem;
import com.mycity4kids.newmodels.AttendeeModel;
import com.mycity4kids.ui.activity.BlogSetupActivity;
import com.mycity4kids.ui.adapter.ChangeCityAdapter;
import com.mycity4kids.ui.adapter.CommentsReplyAdapter;

import java.util.ArrayList;

/**
 * Created by user on 08-06-2015.
 */
public class CityListingDialogFragment extends DialogFragment implements ChangeCityAdapter.IOtherCity {

    ArrayList<CityInfoItem> data;
    ListView cityListView;
    ChangeCityAdapter adapter;
    Toolbar mToolbar;
    private ProgressDialog mProgressDialog;
    private String fromScreen;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.city_list_dialog, container,
                false);
        mToolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        mToolbar.setTitle("Choose City");
        mToolbar.setTitleTextColor(ContextCompat.getColor(getActivity(), R.color.colorControlNormal));
        Drawable upArrow = ContextCompat.getDrawable(getActivity(), R.drawable.back_arroow);
        upArrow.setColorFilter(ContextCompat.getColor(getActivity(), R.color.colorControlNormal), PorterDuff.Mode.SRC_ATOP);
        mToolbar.setNavigationIcon(upArrow);
        mToolbar.setNavigationOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // handle back button naviagtion
                dismiss();
            }
        });

        cityListView = (ListView) rootView.findViewById(R.id.cityListView);
        Bundle extras = getArguments();
        if (extras != null) {
            data = extras.getParcelableArrayList("cityList");
            fromScreen = extras.getString("fromScreen");
        }

        adapter = new ChangeCityAdapter(getActivity(), data, CityListingDialogFragment.this);
        cityListView.setAdapter(adapter);
        cityListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final int cId = Integer.parseInt(data.get(position).getId().replace("city-", ""));
                if (cId == AppConstants.OTHERS_CITY_ID) {
                    showAddNewCityNameDialog(position);
                    return;
                }
                updateDataset(position);
                dismiss();
            }
        });
        return rootView;
    }

    private void updateDataset(int position) {
        for (int i = 0; i < data.size(); i++) {
            if (i == position) {
                data.get(i).setSelected(true);
            } else {
                data.get(i).setSelected(false);
            }
        }
        adapter.notifyDataSetChanged();
        if ("editProfile".equals(fromScreen)) {
            IChangeCity changeCity = (IChangeCity) getTargetFragment();
            changeCity.onCitySelect(data.get(position));
        } else {
            ((BlogSetupActivity) getActivity()).changeCityText(data.get(position));
        }

    }

    private void updateOtherCity(int position, String otherCityName) {
        for (int i = 0; i < data.size(); i++) {
            if (i == position) {
                data.get(i).setSelected(true);
            } else {
                data.get(i).setSelected(false);
            }
        }
        adapter.notifyDataSetChanged();
        if ("editProfile".equals(fromScreen)) {
            IChangeCity changeCity = (IChangeCity) getTargetFragment();
            changeCity.onOtherCitySelect(position, otherCityName);
        } else {
            ((BlogSetupActivity) getActivity()).setOtherCityName(position, otherCityName);
        }
    }

    private void showAddNewCityNameDialog(final int position) {
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity(), R.style.MyAlertDialogStyle);
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View dialogLayout = inflater.inflate(R.layout.other_city_name_dialog, null);
        final EditText edittext = (EditText) dialogLayout.findViewById(R.id.cityNameEditText);
        alert.setMessage("Change City");
        alert.setTitle("Enter Your City Name");

        alert.setView(dialogLayout);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String cityNameVal = edittext.getText().toString();
                if (StringUtils.isNullOrEmpty(cityNameVal)) {
                    ToastUtils.showToast(getActivity(), "Please enter the city name");
                } else {
                    updateOtherCity(position, cityNameVal);
                    dismiss();
                }
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // what ever you want to do with No option.
            }
        });

        alert.show();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//            dialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.blue_bg_rounded_corners));
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    public void showProgressDialog(String bodyText) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            mProgressDialog.setCancelable(false);
        }

        mProgressDialog.setMessage(bodyText);

        if (!mProgressDialog.isShowing()) {
            mProgressDialog.show();
        }
    }

    public void removeProgressDialog() {
        try {
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onOtherCityAdd(String cityName) {

    }

    public interface IChangeCity {
        void onCitySelect(CityInfoItem cityItem);

        void onOtherCitySelect(int pos, String cityName);
    }
}