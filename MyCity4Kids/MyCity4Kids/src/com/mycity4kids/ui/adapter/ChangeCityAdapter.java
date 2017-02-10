package com.mycity4kids.ui.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TableRow;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.kelltontech.persistence.SharedPrefsUtils;
import com.kelltontech.utils.StringUtils;
import com.kelltontech.utils.ToastUtils;
import com.mycity4kids.R;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.models.response.CityInfoItem;
import com.mycity4kids.models.response.VlogsListingAndDetailResult;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.utils.AppUtils;
import com.squareup.picasso.Picasso;

import org.apmem.tools.layouts.FlowLayout;

import java.util.ArrayList;

/**
 * Created by hemant on 23/1/17.
 */
public class ChangeCityAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflator;
    ArrayList<CityInfoItem> cityInfoItemArrayList;
    private int currentCityId;

    private IOtherCity iOtherCity;


    public ChangeCityAdapter(Context pContext, ArrayList<CityInfoItem> cityInfoItemArrayList, IOtherCity iOtherCity) {
        mInflator = (LayoutInflater) pContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = pContext;
        this.cityInfoItemArrayList = cityInfoItemArrayList;
        this.iOtherCity = iOtherCity;
    }

    @Override
    public int getCount() {
        return cityInfoItemArrayList == null ? 0 : cityInfoItemArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return cityInfoItemArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {

        try {
            final ViewHolder holder;
            if (view == null) {
                view = mInflator.inflate(R.layout.change_city_listing_item, null);
                holder = new ViewHolder();
                holder.cityRadioButton = (RadioButton) view.findViewById(R.id.cityRadioButton);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            final int cId = Integer.parseInt(cityInfoItemArrayList.get(position).getId().replace("city-", ""));
            holder.cityRadioButton.setText(cityInfoItemArrayList.get(position).getCityName());
            if (cityInfoItemArrayList.get(position).isSelected()) {
                holder.cityRadioButton.setChecked(true);
            } else {
                holder.cityRadioButton.setChecked(false);
            }
            holder.cityRadioButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (int i = 0; i < cityInfoItemArrayList.size(); i++) {
                        if (i == position) {
                            cityInfoItemArrayList.get(i).setSelected(true);
                        } else {
                            cityInfoItemArrayList.get(i).setSelected(false);
                        }
                    }
                    notifyDataSetChanged();
                    if (cId == AppConstants.OTHERS_CITY_ID) {
                        showAddNewCityNameDialog();
                    }
                }
            });
        } catch (Exception ex) {
            Crashlytics.logException(ex);
            Log.d("MC4kException", Log.getStackTraceString(ex));
        }

        return view;
    }

    private void showAddNewCityNameDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(mContext, R.style.MyAlertDialogStyle);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View dialogLayout = inflater.inflate(R.layout.other_city_name_dialog, null);
        final EditText edittext = (EditText) dialogLayout.findViewById(R.id.cityNameEditText);
        alert.setMessage("Change City");
        alert.setTitle("Enter Your City Name");

        alert.setView(dialogLayout);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String cityNameVal = edittext.getText().toString();
                if (StringUtils.isNullOrEmpty(cityNameVal)) {
                    ToastUtils.showToast(mContext, "Please enter the city name");
                } else {
                    iOtherCity.onOtherCityAdd(cityNameVal);
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

    class ViewHolder {
        RadioButton cityRadioButton;
    }

    public interface IOtherCity {
        void onOtherCityAdd(String cityName);
    }
}
