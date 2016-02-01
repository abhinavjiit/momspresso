package com.chatPlatform.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mycity4kids.R;

import java.util.ArrayList;

/**
 * Created by anshul on 1/14/16.
 */
public class GroupMembersAdapter extends BaseAdapter {
  Context context;
    ArrayList<String> members;
    public GroupMembersAdapter(Context context, ArrayList<String> members)
    {
        this.context=context;
        this.members=members;
    }
    @Override
    public int getCount() {
        return members.size();
    }

    @Override
    public Object getItem(int position) {
        return members.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) parent.getContext().
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView=inflater.inflate(R.layout.members_list_item,null);
        }
        TextView userId=(TextView)convertView.findViewById(R.id.userId);

        userId.setText(members.get(position).toString());
        return convertView;
    }
}
