package com.mycity4kids.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;

import com.mycity4kids.R;
import com.mycity4kids.newmodels.NotesModel;

import java.util.ArrayList;

/**
 * Created by manish.soni on 25-06-2015.
 */
public class AdapterEditNotes extends BaseAdapter implements TextWatcher {
    //
    Context context;
    ArrayList<NotesModel> datalist;
    Boolean iftask = false;

    public AdapterEditNotes(Context context, ArrayList<NotesModel> datalist, Boolean flag) {
        this.context = context;
        this.datalist = datalist;
        this.iftask = flag;
    }


    @Override
    public int getCount() {
        return datalist == null ? 0 : datalist.size();
    }

    @Override
    public Object getItem(int i) {
        return datalist.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup viewGroup) {

        View view = convertView;
        ViewHolder holder = null;
        if (view == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();

            if (iftask) {
                view = inflater.inflate(R.layout.aa_editnote_task, viewGroup, false);
            } else {
                view = inflater.inflate(R.layout.aa_editnote_item, viewGroup, false);
            }

            holder = new ViewHolder();
            holder.name = (EditText) view.findViewById(R.id.text);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        holder.name.setText(datalist.get(position).getMsg());
        holder.name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (datalist != null && datalist.size() > 0)
                    datalist.get(position).setMsg(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        holder.name.setTag(position);
        return view;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {

        System.out.println(("val chnge " + s.toString()));
        // datalist.get(po).setAddress(s.toString());
    }

    public static class ViewHolder {
        EditText name;
        // TextView addedby;

    }

    public ArrayList<NotesModel> getDatalist() {

        return datalist;
    }
}
