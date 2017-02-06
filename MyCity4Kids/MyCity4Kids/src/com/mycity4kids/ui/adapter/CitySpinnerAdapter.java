package com.mycity4kids.ui.adapter;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mycity4kids.R;
import com.mycity4kids.models.response.CityInfoItem;

import java.util.List;

/**
 * Created by hemant on 24/1/17.
 */
public class CitySpinnerAdapter extends ArrayAdapter<CityInfoItem> {
    private Context context;
    private List<CityInfoItem> itemList;
    private LayoutInflater mInflator;

    public CitySpinnerAdapter(Context context, int textViewResourceId, List<CityInfoItem> itemList) {
        super(context, textViewResourceId, itemList);
        this.context = context;
        mInflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.itemList = itemList;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    // This funtion called for each row ( Called data.size() times )
    public View getCustomView(int position, View convertView, ViewGroup parent) {

        /********** Inflate spinner_rows.xml file for each row ( Defined below ) ************/
        View row = mInflator.inflate(R.layout.complete_profile_spinner_city_item, parent, false);
        TextView cityTextView = (TextView) row.findViewById(R.id.cityNameTextView);
        cityTextView.setText(itemList.get(position).getCityName());
        return row;
    }
}
