package com.mycity4kids.ui.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.kelltontech.persistence.SharedPrefsUtils;
import com.kelltontech.utils.StringUtils;
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


    public ChangeCityAdapter(Context pContext, ArrayList<CityInfoItem> cityInfoItemArrayList) {
        mInflator = (LayoutInflater) pContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = pContext;
        this.cityInfoItemArrayList = cityInfoItemArrayList;

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
            int cId = Integer.parseInt(cityInfoItemArrayList.get(position).getId().replace("city-", ""));
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
                }
            });
        } catch (Exception ex) {
            Crashlytics.logException(ex);
            Log.d("MC4kException", Log.getStackTraceString(ex));
        }

        return view;
    }

    class ViewHolder {
        RadioButton cityRadioButton;
    }

}
