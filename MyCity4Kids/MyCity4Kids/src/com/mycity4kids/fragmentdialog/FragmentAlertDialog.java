package com.mycity4kids.fragmentdialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.TextView;

import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.dbtable.CityTable;
import com.mycity4kids.models.city.MetroCity;
import com.mycity4kids.ui.activity.SelectLocationActivity;
import com.mycity4kids.widget.StaticCityList;

import java.util.ArrayList;

public class FragmentAlertDialog extends DialogFragment {
    private TextView mInputText;

    public FragmentAlertDialog newInstance(Context pContext, String str) {
        FragmentAlertDialog frag = new FragmentAlertDialog();

        return frag;

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        OnClickListener okClick = new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        getActivity().startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                       // doFirstTimeConfigWork();

                        getDialog().cancel();
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        showSelectCityLocation();
                        break;

                    default:
                        break;
                }


            }
        };


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        mInputText = new TextView(getActivity());
        mInputText.setTextColor(Color.BLACK);
        mInputText.setText(getString(R.string.gps_enabled_alert));
        mInputText.setPadding(30, 30, 30, 30);
        mInputText.setTextSize(20);
        builder.setPositiveButton("OK", okClick);
        // commented by khushboo
        //builder.setNegativeButton("Select City", okClick);
        //builder.setCancelable(false);

        builder.setView(mInputText);
        Dialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        return dialog;

    }

    private void showSelectCityLocation() {
        CityTable mCityTable = new CityTable((BaseApplication) getActivity().getApplicationContext());
        if (mCityTable.getTotalCount() == 0) {


            ArrayList<MetroCity> _city = StaticCityList._hardCodedCity;
            try {
                mCityTable.beginTransaction();
                for (MetroCity cityModel : _city) {

                    mCityTable.insertData(cityModel);
                }
                mCityTable.setTransactionSuccessful();
            } finally {
                mCityTable.endTransaction();
            }
        }

        Intent intent = new Intent(getActivity(), SelectLocationActivity.class);
        intent.putExtra("isFromSplash", true);
        getActivity().startActivity(intent);
        getActivity().finish();

    }




}
