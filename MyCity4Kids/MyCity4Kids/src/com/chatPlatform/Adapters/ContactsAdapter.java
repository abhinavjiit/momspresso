package com.chatPlatform.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.chatPlatform.ContactStore;
import com.mycity4kids.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by anshul on 21/12/15.
 */
public class ContactsAdapter extends BaseAdapter implements Filterable {
    ArrayList<ContactStore> contactStoreArrayList;
    ArrayList<ContactStore> tempContactList;
    public SparseBooleanArray sba;

    public ContactsAdapter(ArrayList<ContactStore> contactStoreArrayList) {
        tempContactList = new ArrayList<ContactStore>();
        this.contactStoreArrayList = contactStoreArrayList;
        tempContactList.addAll(contactStoreArrayList);
        sba = new SparseBooleanArray();
    }

    @Override
    public int getCount() {
        return contactStoreArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return contactStoreArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        LayoutInflater inflater = (LayoutInflater) parent.getContext().
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.contact_list_item, null);
            viewHolder.contactName = (TextView) convertView.findViewById(R.id.contactName);
            viewHolder.contactNumber = (TextView) convertView.findViewById(R.id.phoneNumber);
            viewHolder.phone2 = (TextView) convertView.findViewById(R.id.phone2);
            viewHolder.checkBox = (CheckBox) convertView.findViewById(R.id.checkbox1);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.contactName.setText(contactStoreArrayList.get(position).getContactName());
        viewHolder.contactNumber.setText(contactStoreArrayList.get(position).getPhoneNumber());
        viewHolder.phone2.setText(contactStoreArrayList.get(position).getPhoneNumber1());
        //  viewHolder.checkBox.setChecked(sba.get(position, false));
        if (contactStoreArrayList.get(position).isChecked) {
            viewHolder.checkBox.setChecked(true);
        } else {
            viewHolder.checkBox.setChecked(false);
        }
        viewHolder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewHolder.checkBox.isChecked()) {
                    sba.put(position, true);
                    contactStoreArrayList.get(position).isChecked(true);
                } else {
                    sba.put(position, false);
                }
            }
        });


        return convertView;
    }


    public void functionImplementingSearch(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        // listContactDetailGetterSetter.clear();p
        contactStoreArrayList.clear();
        // Log.e("size of list", "hi" + listContactDetailGetterSetter.size() + listContactDetailGetterSetterFull.size());

        if (charText.length() == 0) {
            //  Log.e("search length zero", "" + listContactDetailGetterSetter.size() + listContactDetailGetterSetterFull.size());
            contactStoreArrayList.addAll(tempContactList);
        } else {
          /*  for (ContactStore contactStore : tempContactList) {


                if (contactStore.getContactName().toLowerCase(Locale.getDefault()).contains(charText)) {
                  //  Log.e("contact added", "" + listContactDetailGetterSetter.size() + listContactDetailGetterSetterFull.size());

                    contactStoreArrayList.add(contactStore);
                }


            }*/
            for (int i = 0; i < tempContactList.size(); i++) {

                if (tempContactList.get(i).getContactName().toLowerCase(Locale.getDefault()).contains(charText)) {
                    //  Log.e("contact added", "" + listContactDetailGetterSetter.size() + listContactDetailGetterSetterFull.size());

                    contactStoreArrayList.add(tempContactList.get(i));
                    if (sba.get(i) == true && tempContactList.get(i).isChecked) {
                    } else if (sba.get(i) == true && !tempContactList.get(i).isChecked) {
                        sba.put(i, false);

                    } else if (!sba.get(i) && tempContactList.get(i).isChecked) {
                        sba.put(i, true);
                    }
                }
            }

        }
        notifyDataSetChanged();

    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                constraint = constraint.toString().toLowerCase();
                for (int i = 0; i < tempContactList.size(); i++) {
                    String name = tempContactList.get(i).getContactName();
                }


                return null;
            }
        };

        return null;
    }


    public class ViewHolder {
        TextView contactName;
        TextView contactNumber;
        TextView phone2;
        CheckBox checkBox;
    }
}
