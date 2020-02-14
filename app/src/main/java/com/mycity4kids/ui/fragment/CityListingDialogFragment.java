package com.mycity4kids.ui.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.mycity4kids.utils.StringUtils;
import com.mycity4kids.utils.ToastUtils;
import com.mycity4kids.R;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.models.response.CityInfoItem;
import com.mycity4kids.ui.activity.BlogSetupActivity;
import com.mycity4kids.ui.adapter.ChangeCityAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.app.Activity.RESULT_CANCELED;
import static com.crashlytics.android.core.CrashlyticsCore.TAG;

/**
 * Created by user on 08-06-2015.
 */
public class CityListingDialogFragment extends DialogFragment implements ChangeCityAdapter.IOtherCity {


    private Toolbar mToolbar;
    private ListView cityListView;
    private TextView toolbarTitleTextView;
    private ProgressDialog mProgressDialog;

    private ChangeCityAdapter adapter;

    private ArrayList<CityInfoItem> data;
    private String fromScreen;
    private static final int REQUEST_SELECT_PLACE = 1000;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.city_list_dialog, container,
                false);
        mToolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        toolbarTitleTextView = (TextView) mToolbar.findViewById(R.id.toolbarTitle);

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
        if ("editProfile".equals(fromScreen) || "explore".equals(fromScreen)) {
            IChangeCity changeCity = (IChangeCity) getParentFragment();
            changeCity.onCitySelect(data.get(position));
        } else if ("rewards".equalsIgnoreCase(fromScreen)) {
            IChangeCity changeCity = (IChangeCity) getParentFragment();
            changeCity.onCitySelect(data.get(position));
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
        if ("editProfile".equals(fromScreen) || "explore".equals(fromScreen)) {
            IChangeCity changeCity = (IChangeCity) getParentFragment();
            changeCity.onOtherCitySelect(position, otherCityName);
        } else if ("rewards".equalsIgnoreCase(fromScreen)) {
            IChangeCity changeCity = (IChangeCity) getParentFragment();
            changeCity.onOtherCitySelect(position, otherCityName);
        }
    }

    private void showAddNewCityNameDialog(final int position) {
        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG);
        Intent intent = new Autocomplete.IntentBuilder(
                AutocompleteActivityMode.FULLSCREEN, fields)
                .setTypeFilter(TypeFilter.CITIES)
                .build(getActivity());
        startActivityForResult(intent, REQUEST_SELECT_PLACE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_SELECT_PLACE) {
            Place place = Autocomplete.getPlaceFromIntent(data);
            String cityNameVal = place.getName();
            if (StringUtils.isNullOrEmpty(cityNameVal)) {
                ToastUtils.showToast(getActivity(), "Please enter the city name");
            } else {
                updateOtherCity(9, cityNameVal);
            }
            dismiss();
        } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
            Status status = Autocomplete.getStatusFromIntent(data);
            Log.i(TAG, status.getStatusMessage());
        } else if (resultCode == RESULT_CANCELED) {
            // The user canceled the operation.
        }
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