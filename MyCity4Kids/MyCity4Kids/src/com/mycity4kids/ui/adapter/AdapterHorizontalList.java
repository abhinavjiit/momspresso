package com.mycity4kids.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.mycity4kids.R;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by user on 08-06-2015.
 */
//
//
public class AdapterHorizontalList extends ArrayAdapter<String> {

    private LayoutInflater mInflater;
    Context context;
    List<String> values;


    public AdapterHorizontalList(Context context, List<String> values) {
        super(context, R.layout.aa_image_show, values);
        this.context = context;
        this.values = values;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.aa_image_show, parent, false);

            holder = new Holder();
            holder.image = (ImageView) convertView.findViewById(R.id.image);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }

        Picasso.with(context).load(values.get(position)).noFade().into(holder.image);

        return convertView;
    }

    private static class Holder {
        public ImageView image;
    }
}

