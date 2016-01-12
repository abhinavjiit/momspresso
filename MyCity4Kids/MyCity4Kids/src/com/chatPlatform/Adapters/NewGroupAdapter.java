package com.chatPlatform.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.chatPlatform.ActivitiesFragments.CreateChat;
import com.chatPlatform.models.Group;
import com.couchbase.lite.Document;
import com.mycity4kids.R;

import java.util.List;

/**
 * Created by anshul on 1/11/16.
 */
public class NewGroupAdapter extends RecyclerView.Adapter<NewGroupAdapter.ViewHolder> {
    private  Context context;
    private List<Group> groupList;
    public NewGroupAdapter(Context context, List<Group> groupList) {
        this.context=context;
        this.groupList=groupList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_list_item, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Group group = (Group) groupList.get(position);
        Document groupDoc = group.getGroupDoc();
        Document chatDoc = group.getChatDoc();
        int unreadMsgCount = group.getUnreadMsgCount();
     //   ImageView imageView = (ImageView) convertView.findViewById(R.id.imageView);
        holder.imageView.setImageResource(R.drawable.user);
   //     TextView groupTitle = (TextView) convertView.findViewById(R.id.groupTitle);
        String title = groupDoc.getProperty("title").toString();
        holder.groupTitle.setText(title);
    //    TextView lastMsg = (TextView) convertView.findViewById(R.id.lastMsg);
        if (chatDoc != null) {
            holder.lastMsg.setText(chatDoc.getProperty("msg").toString());
        }
    //    TextView unreadCount = (TextView) convertView.findViewById(R.id.unreadCount);
        holder.unreadCount.setText(unreadMsgCount + "");
        holder.currentGroup=group;
    }

    @Override
    public int getItemCount() {
        return groupList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView groupTitle, lastMsg,unreadCount;
        public ImageView imageView;
        Group currentGroup;

        public ViewHolder(View v) {
            super(v);
            groupTitle = (TextView) v.findViewById(R.id.groupTitle);
            lastMsg = (TextView) v.findViewById(R.id.lastMsg);
            unreadCount = (TextView) v.findViewById(R.id.unreadCount);
            imageView = (ImageView) v.findViewById(R.id.imageView);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Document document = currentGroup.getGroupDoc();
                    String title = document.getProperty("title").toString();
                    // test only
                    Intent intent = new Intent(v.getContext(), CreateChat.class);
                    intent.putExtra("groupChannelId", document.getProperty("channelId").toString());
                    v.getContext().startActivity(intent);
                }
            });
        }
    }
}
