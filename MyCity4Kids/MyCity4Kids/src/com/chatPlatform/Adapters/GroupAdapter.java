package com.chatPlatform.Adapters;

/*import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.couchbase.lite.Document;
import com.couchbase.lite.LiveQuery;
import com.mycity4kids.R;

*//**
 * Created by anshul on 16/12/15.
 *//*
public class GroupAdapter extends LiveQueryAdapter {
    public GroupAdapter(Context context, LiveQuery query)  {
        super(context, query);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) parent.getContext().
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView=inflater.inflate(R.layout.custom_layout_left,null);
        }

        final Document task = (Document) getItem(position);
        if (task == null || task.getCurrentRevision() == null) {
            return convertView;
        }
   *//*     ImageView imageView= (ImageView) convertView.findViewById(R.id.imageView);
        imageView.setImageResource(R.drawable.user);*//*
        TextView text = (TextView) convertView.findViewById(R.id.textView);
        text.setText(task.getProperty("title").toString());
        return convertView;
    }
}*/

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.chatPlatform.models.Group;
import com.couchbase.lite.Document;
import com.mycity4kids.R;

import java.util.List;

public class GroupAdapter extends GroupQueryAdapter {
    public GroupAdapter(Context context, List<Group> groupList) {
        super(context, groupList);
    }    @Override
         public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) parent.getContext().
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView=inflater.inflate(R.layout.group_list_item,null);
        }
        Group group= (Group) getItem(position);
        if(group==null){
            return convertView;
        }
        Document groupDoc=group.getGroupDoc();
        Document chatDoc=group.getChatDoc();
        int unreadMsgCount=group.getUnreadMsgCount();
        ImageView imageView= (ImageView) convertView.findViewById(R.id.imageView);
        imageView.setImageResource(R.drawable.user);
        TextView groupTitle = (TextView) convertView.findViewById(R.id.groupTitle);
        String title=groupDoc.getProperty("title").toString();
        groupTitle.setText(title);
        TextView lastMsg = (TextView) convertView.findViewById(R.id.lastMsg);
        if(chatDoc!=null) {
            lastMsg.setText(chatDoc.getProperty("msg").toString());
        }
        TextView unreadCount = (TextView) convertView.findViewById(R.id.unreadCount);
        unreadCount.setText(unreadMsgCount+"");
        return convertView;
    }
}
