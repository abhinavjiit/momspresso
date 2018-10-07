package com.mycity4kids.ui.fragment;

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

import java.util.ArrayList;

public class Contactdetails extends Fragment implements View.OnClickListener, CityListingDialogFragment.IChangeCity {
    TextView editmail;
    private TextView emailTextView;
    private EditText handleNameEditText, phoneEditText, cityEditText, fullNameEditText;
    private UserDetailResult userDetail;
    private ArrayList<CityInfoItem> cityList;
    private CityListingDialogFragment cityFragment;
    private String currentCityName;
    private String newSelectedCityId;
    private int selectedCityId;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.contact_details_layout, container, false);
        emailTextView = (TextView) view.findViewById(R.id.emailTextView);
        handleNameEditText = (EditText) view.findViewById(R.id.handleNameEditText);
        phoneEditText = (EditText) view.findViewById(R.id.phoneEditText);
        cityEditText = (EditText) view.findViewById(R.id.cityEditText);
        fullNameEditText = (EditText) view.findViewById(R.id.fullNameEditText);

        userDetail = getArguments().getParcelable("userDetail");
        cityList = getArguments().getParcelableArrayList("cityList");

        emailTextView.setText("" + userDetail.getEmail());
        handleNameEditText.setText("" + userDetail.getBlogTitle());
        fullNameEditText.setText("" + userDetail.getFirstName() + " " + userDetail.getLastName());
        phoneEditText.setText("" + userDetail.getPhoneNumber());

        MetroCity currentCity = SharedPrefUtils.getCurrentCityModel(getActivity());
        for (int i = 0; i < cityList.size(); i++) {
            int cId = Integer.parseInt(cityList.get(i).getId().replace("city-", ""));
            if (currentCity.getId() == cId) {
                cityList.get(i).setSelected(true);
                cityEditText.setText(cityList.get(i).getCityName());
            } else {
                cityList.get(i).setSelected(false);
            }
        }

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cityNameTextView:
                cityFragment = new CityListingDialogFragment();
                cityFragment.setTargetFragment(this, 0);
                Bundle _args = new Bundle();
                _args.putParcelableArrayList("cityList", cityList);
                _args.putString("fromScreen", "editProfile");
                cityFragment.setArguments(_args);
                FragmentManager fm = getChildFragmentManager();
                cityFragment.show(fm, "Replies");
                break;
        }
    }

    @Override
    public void onCitySelect(CityInfoItem cityItem) {
        cityEditText.setText(cityItem.getCityName());
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
        cityEditText.setText(cityList.get(pos).getCityName());
    }

    public EditText getHandleNameEditText() {
        return handleNameEditText;
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
}
