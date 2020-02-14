package com.mycity4kids.ui.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioButton;

import com.crashlytics.android.Crashlytics;
import com.mycity4kids.R;
import com.mycity4kids.models.response.CityInfoItem;

import java.util.ArrayList;

/**
 * Created by hemant on 23/1/17.
 */
public class ChangeCityAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflator;
    private ArrayList<CityInfoItem> cityInfoItemArrayList;
    private int currentCityId;
    private Typeface font;
    private IOtherCity iOtherCity;


    public ChangeCityAdapter(Context pContext, ArrayList<CityInfoItem> cityInfoItemArrayList, IOtherCity iOtherCity) {
        mInflator = (LayoutInflater) pContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = pContext;
        this.cityInfoItemArrayList = cityInfoItemArrayList;
        this.iOtherCity = iOtherCity;
        font = Typeface.createFromAsset(pContext.getAssets(), "fonts/" + "oswald.ttf");
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
                holder.cityRadioButton.setTypeface(font);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }

            holder.cityRadioButton.setText(cityInfoItemArrayList.get(position).getCityName());
            if (cityInfoItemArrayList.get(position).isSelected()) {
                holder.cityRadioButton.setChecked(true);
            } else {
                holder.cityRadioButton.setChecked(false);
            }
        } catch (Exception ex) {
            Crashlytics.logException(ex);
            Log.d("MC4kException", Log.getStackTraceString(ex));
        }

        return view;
    }

    class ViewHolder {
        RadioButton cityRadioButton;
    }

    public interface IOtherCity {
        void onOtherCityAdd(String cityName);
    }
}
