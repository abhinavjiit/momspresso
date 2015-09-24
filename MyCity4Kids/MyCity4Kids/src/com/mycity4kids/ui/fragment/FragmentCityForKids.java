package com.mycity4kids.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseFragment;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.dbtable.TableAdult;
import com.mycity4kids.dbtable.TableFamily;
import com.mycity4kids.dbtable.TableKids;
import com.mycity4kids.dbtable.UserTable;
import com.mycity4kids.models.user.KidsInfo;
import com.mycity4kids.models.user.UserModel;
import com.mycity4kids.models.user.UserResponse;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.ui.activity.DashboardActivity;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by khushboo.goyal on 08-06-2015.
 */
public class FragmentCityForKids extends BaseFragment implements View.OnClickListener {


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.aa_cityforkids, container, false);
        ((DashboardActivity) getActivity()).refreshMenu();

        SharedPrefUtils.setFirstTimeCheckFlag(getActivity(), true);

        TextView txtviewDay = (TextView) view.findViewById(R.id.day);
        TextView txtviewDate = (TextView) view.findViewById(R.id.date);

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd");
        String formattedDate = df.format(c.getTime());

        txtviewDate.setText("" + formattedDate);

        SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
        Date d = new Date();
        String dayOfTheWeek = sdf.format(d);
        txtviewDay.setText("" + dayOfTheWeek);

        if (SharedPrefUtils.isCityFetched(getActivity())){
            view.findViewById(R.id.lnrEventBlofg).setVisibility(View.VISIBLE);
        } else {
            view.findViewById(R.id.lnrEventBlofg).setVisibility(View.GONE);
        }

        view.findViewById(R.id.todos).setOnClickListener(this);
        view.findViewById(R.id.events).setOnClickListener(this);
        view.findViewById(R.id.calender).setOnClickListener(this);
        view.findViewById(R.id.blogs).setOnClickListener(this);

        view.findViewById(R.id.txttodo).setOnClickListener(this);
        view.findViewById(R.id.txtevents).setOnClickListener(this);
        view.findViewById(R.id.txtCalender).setOnClickListener(this);
        view.findViewById(R.id.txtblogs).setOnClickListener(this);

        // saveDatainDB(null);

        return view;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.calender:

                ((DashboardActivity) getActivity()).replaceFragment(new FragmentCalender(), null, true);

                break;
            case R.id.todos:
                ((DashboardActivity) getActivity()).replaceFragment(new FragmentTaskHome(), null, true);
                break;
            case R.id.events:
                Constants.IS_SEARCH_LISTING = false;
                FragmentBusinesslistEvents fragment = new FragmentBusinesslistEvents();
                Bundle bundle = new Bundle();
                bundle.putInt(Constants.PAGE_TYPE, Constants.EVENT_PAGE_TYPE);
                bundle.putInt(Constants.EXTRA_CATEGORY_ID, SharedPrefUtils.getEventIdForCity(getActivity()));
                bundle.putString(Constants.CATEGOTY_NAME, "Events & workshop");
                fragment.setArguments(bundle);
                ((DashboardActivity) getActivity()).replaceFragment(fragment, bundle, true);
                break;

            case R.id.txtCalender:

                ((DashboardActivity) getActivity()).replaceFragment(new FragmentCalender(), null, true);

                break;
            case R.id.txttodo:
                ((DashboardActivity) getActivity()).replaceFragment(new FragmentTaskHome(), null, true);
                break;
            case R.id.txtevents:
                Constants.IS_SEARCH_LISTING = false;
                fragment = new FragmentBusinesslistEvents();
                bundle = new Bundle();
                bundle.putInt(Constants.PAGE_TYPE, Constants.EVENT_PAGE_TYPE);
                bundle.putInt(Constants.EXTRA_CATEGORY_ID, 6);
                bundle.putString(Constants.CATEGOTY_NAME, "Events & workshop");
                fragment.setArguments(bundle);
                ((DashboardActivity) getActivity()).replaceFragment(fragment, bundle, true);
                break;

            case R.id.txtblogs:
                ((DashboardActivity) getActivity()).replaceFragment(new ArticlesFragment(), null, true);

                break;
            case R.id.blogs:
                ((DashboardActivity) getActivity()).replaceFragment(new ArticlesFragment(), null, true);
                break;


        }
    }

    @Override
    protected void updateUi(Response response) {

    }

    @Override
    public void onResume() {
        super.onResume();

    }

    public void saveDatainDB(UserResponse model) {

        String response = readFromFile(getActivity(), "countries");

        model = new Gson().fromJson(response, UserResponse.class);

        UserTable userTable = new UserTable((BaseApplication) getActivity().getApplicationContext());
        userTable.insertData(model);

        TableAdult adultTable = new TableAdult((BaseApplication) getActivity().getApplicationContext());
        adultTable.deleteAll();
        try {

            adultTable.beginTransaction();
            for (UserModel.AdultsInfo user : model.getResult().getData().getAdult()) {

                adultTable.insertData(user.getUser());
            }
            adultTable.setTransactionSuccessful();
        } finally {
            adultTable.endTransaction();
        }

        // saving child data
        TableKids kidsTable = new TableKids((BaseApplication) getActivity().getApplicationContext());
        kidsTable.deleteAll();
        try {
            kidsTable.beginTransaction();
            for (KidsInfo kids : model.getResult().getData().getKidsInformation()) {

                kidsTable.insertData(kids);

            }
            kidsTable.setTransactionSuccessful();
        } finally {
            kidsTable.endTransaction();
        }

        // saving family

        TableFamily familyTable = new TableFamily((BaseApplication) getActivity().getApplicationContext());
        familyTable.deleteAll();
        try {

            familyTable.insertData(model.getResult().getData().getFamily());

        } catch (Exception e) {
            e.getMessage();
        }

    }

    public static String readFromFile(Context context, String fileName) {

        String ret = "";

        try {
            InputStream inputStream = context.getAssets().open("countries.json");

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ((receiveString = bufferedReader.readLine()) != null) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("login activity", "File not found: " + e.toString());
        }

        return ret;
    }
}
