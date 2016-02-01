package com.chatPlatform.Adapters;

/**
 * Created by anshul on 22/12/15.
 */
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.chatPlatform.models.Group;

import java.util.List;/**
 * Created by pushpendra on 18/12/15.
 */
public class GroupQueryAdapter extends BaseAdapter{
    private Context context;
    private List<Group> groupList;    public GroupQueryAdapter(Context context,List<Group> groupList ){
        this.context=context;
        this.groupList=groupList;
    }
    @Override
    public int getCount() {
        return groupList.size();
    }    @Override
         public Object getItem(int position) {
        return groupList.get(position);
    }    @Override
         public long getItemId(int position) {
        return position;
    }    @Override
         public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }
}
