package com.chatPlatform.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.chatPlatform.ActivitiesFragments.CreateChat;
import com.chatPlatform.models.Group;
import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Document;
import com.couchbase.lite.UnsavedRevision;
import com.mycity4kids.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by anshul on 23/12/15.
 */
public class JoinGroupAdapter extends RecyclerView.Adapter<JoinGroupAdapter.ViewHolder> {
    List<Group> groupList;
    Context context;
    static int UserId;
    public static OnGroupJoinInterface onGroupJoinInterface;
    public JoinGroupAdapter(Context context,List<Group> groupList,int UserId)
    {
        this.context=context;;
        this.groupList=groupList;
        this.UserId=UserId;
        onGroupJoinInterface=(OnGroupJoinInterface)context;
    }



    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.join_group_list_item, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.groupImage.setImageResource(R.drawable.user);
        holder.groupName.setText(groupList.get(position).getGroupName());
        holder.description.setText(groupList.get(position).getGroupDescription());
        holder.memberNumber.setText( groupList.get(position).getGroupSize()+"");
        holder.currentGroup=groupList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return groupList.size();
    }

 /*   @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) parent.getContext().
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView=inflater.inflate(R.layout.join_group_list_item,null);
        }
        TextView groupName=(TextView)convertView.findViewById(R.id.groupName);
        TextView description=(TextView)convertView.findViewById(R.id.description);
        ImageView groupImage=(ImageView) convertView.findViewById(R.id.groupImage);
        TextView memberNumber=(TextView) convertView.findViewById(R.id.memberNumber);
        groupImage.setImageResource(R.drawable.user);
        groupName.setText(groupList.get(position).getGroupName());
        description.setText(groupList.get(position).getGroupDescription());
        memberNumber.setText( groupList.get(position).getGroupSize()+"");
        return convertView;
    }*/
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView groupName, description,memberNumber;
        public ImageView groupImage;
        Group currentGroup;

        public ViewHolder(View v) {
            super(v);
            groupName = (TextView) v.findViewById(R.id.groupName);
            description = (TextView) v.findViewById(R.id.description);
            memberNumber = (TextView) v.findViewById(R.id.memberNumber);
            groupImage = (ImageView) v.findViewById(R.id.groupImage);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   // Document document = currentGroup.getGroupDoc();
                    Document groupDoc=currentGroup.getGroupDoc();
                    // String[] membersArray=(String[]) groupDoc.getProperty("members");
                    ArrayList<String> members=(ArrayList<String>)groupDoc.getProperty("members");
                    if(members!=null)
                    {members.add("user-"+UserId);}
                    else
                    {members=new ArrayList<String>();
                        members.add("user-"+UserId);

                    }
                    try {


                        final ArrayList<String> finalMembers = members;
                        groupDoc.update(new Document.DocumentUpdater() {
                            @Override
                            public boolean update(UnsavedRevision newRevision) {
                                Map<String,Object> properties=newRevision.getProperties();
                                properties.put("members", finalMembers);
                                newRevision.setProperties(properties);
                                return true;
                            }
                        });
                        onGroupJoinInterface.OnGroupJoined();
                    /*PublicGroupListView fragment = new PublicGroupListView();
                 //   onGroupJoinInterface.OnGroupJoined();
                    android.support.v4.app.FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.tabs, fragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();*/

                    } catch (CouchbaseLiteException e)
                    {e.printStackTrace();}
                }

            });
                }
            }
    public interface OnGroupJoinInterface{
        public void OnGroupJoined();
    }
        }

