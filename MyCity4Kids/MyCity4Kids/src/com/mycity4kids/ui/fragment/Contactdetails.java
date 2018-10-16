package com.mycity4kids.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.mycity4kids.R;
import com.mycity4kids.models.city.MetroCity;
import com.mycity4kids.models.response.CityInfoItem;
import com.mycity4kids.models.response.UserDetailResult;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.ui.activity.ChangePasswordActivity;

import java.util.ArrayList;

public class Contactdetails extends Fragment implements View.OnClickListener, CityListingDialogFragment.IChangeCity {
    TextView editmail;
    private TextView emailTextView, cityTextView, handleNameTextView;
    private EditText phoneEditText, fullNameEditText;
    private UserDetailResult userDetail;
    private ArrayList<CityInfoItem> cityList;
    private CityListingDialogFragment cityFragment;
    private String currentCityName;
    private String newSelectedCityId;
    private int selectedCityId;
    private TextView changePassTextView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.contact_details_layout, container, false);
        emailTextView = (TextView) view.findViewById(R.id.emailTextView);
        handleNameTextView = (TextView) view.findViewById(R.id.handleNameTextView);
        phoneEditText = (EditText) view.findViewById(R.id.phoneEditText);
        cityTextView = (TextView) view.findViewById(R.id.cityTextView);
        fullNameEditText = (EditText) view.findViewById(R.id.fullNameEditText);
        changePassTextView = (TextView) view.findViewById(R.id.changePasswordTextView);

        userDetail = getArguments().getParcelable("userDetail");
        cityList = getArguments().getParcelableArrayList("cityList");

        cityTextView.setOnClickListener(this);
        changePassTextView.setOnClickListener(this);

        emailTextView.setText("" + userDetail.getEmail());
        handleNameTextView.setText("" + userDetail.getBlogTitle());
        fullNameEditText.setText("" + userDetail.getFirstName() + " " + userDetail.getLastName());
        if (userDetail.getPhone() != null) {
            phoneEditText.setText(userDetail.getPhone().getMobile());
        }
        MetroCity currentCity = SharedPrefUtils.getCurrentCityModel(getActivity());
        for (int i = 0; i < cityList.size(); i++) {
            int cId = Integer.parseInt(cityList.get(i).getId().replace("city-", ""));
            if (currentCity.getId() == cId) {
                cityList.get(i).setSelected(true);
                cityTextView.setText(cityList.get(i).getCityName());
            } else {
                cityList.get(i).setSelected(false);
            }
        }

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cityTextView:
                cityFragment = new CityListingDialogFragment();
                cityFragment.setTargetFragment(this, 0);
                Bundle _args = new Bundle();
                _args.putParcelableArrayList("cityList", cityList);
                _args.putString("fromScreen", "editProfile");
                cityFragment.setArguments(_args);
                FragmentManager fm = getChildFragmentManager();
                cityFragment.show(fm, "Replies");
                break;
            case R.id.changePasswordTextView:
                Intent intent = new Intent(getActivity(), ChangePasswordActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onCitySelect(CityInfoItem cityItem) {
        cityTextView.setText(cityItem.getCityName());
        currentCityName = cityItem.getCityName();
        selectedCityId = Integer.parseInt(cityItem.getId().replace("city-", ""));
        newSelectedCityId = cityItem.getId();
    }

    @Override
    public void onOtherCitySelect(int pos, String cityName) {
        currentCityName = cityName;
        selectedCityId = Integer.parseInt(cityList.get(pos).getId().replace("city-", ""));
        newSelectedCityId = cityList.get(pos).getId();
        cityList.get(pos).setCityName("Others(" + cityName + ")");
        cityTextView.setText(cityList.get(pos).getCityName());
    }

    public TextView getHandleNameTextView() {
        return handleNameTextView;
    }

    public EditText getPhoneEditText() {
        return phoneEditText;
    }

    public EditText getFullNameEditText() {
        return fullNameEditText;
    }

    public String getCurrentCityName() {
        return currentCityName;
    }

    public int getSelectedCityId() {
        return selectedCityId;
    }

    public String getNewSelectedCityId() {
        return newSelectedCityId;
    }
}
