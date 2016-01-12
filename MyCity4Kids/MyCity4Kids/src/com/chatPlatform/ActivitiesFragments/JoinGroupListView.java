package com.chatPlatform.ActivitiesFragments;


import android.app.Activity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
;import com.chatPlatform.Adapters.GroupAdapter;
import com.chatPlatform.Adapters.JoinGroupAdapter;
import com.chatPlatform.Chats;
import com.chatPlatform.Groups;
import com.chatPlatform.models.Group;
import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryEnumerator;
import com.couchbase.lite.UnsavedRevision;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by anshul on 14/12/15.
 */
public class JoinGroupListView extends Fragment {
    RecyclerView groupListView;
    Query query;
    BaseApplication app;
    List<Group> groupList;
    JoinGroupAdapter joinGroupAdapter;
    ArrayList<String> members;
     int  UserId;

    public OnGroupJoinInterface onGroupJoinInterface;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)  {
      //  super.onCreate(savedInstanceState);
        View v= inflater.inflate(R.layout.join_group_listview,container,false);
        groupListView=(RecyclerView) v.findViewById(R.id.groupList);
        app= (BaseApplication) getActivity().getApplication();
        members=new ArrayList<>();
        SharedPreferences _sharedPref = getActivity().getSharedPreferences("my_city_prefs", Context.MODE_PRIVATE);
       UserId =_sharedPref.getInt("userid", 0);
        query = Groups.getQuery(app.getDatabase());
        groupList= generateData(query);
        joinGroupAdapter=new JoinGroupAdapter(getActivity(),groupList,UserId);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        groupListView.setLayoutManager(llm);
        groupListView.setAdapter(joinGroupAdapter);
        /*groupListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Group group = (Group)joinGroupAdapter.getItem(position);
                Document groupDoc=group.getGroupDoc();
                // String[] membersArray=(String[]) groupDoc.getProperty("members");
                members=( ArrayList<String>)groupDoc.getProperty("members");
                if(members!=null)
                {members.add("user-"+UserId);}
                else
                {members=new ArrayList<String>();
                members.add("user-"+UserId);

                }
                try {


                groupDoc.update(new Document.DocumentUpdater() {
                    @Override
                    public boolean update(UnsavedRevision newRevision) {
                        Map<String,Object> properties=newRevision.getProperties();
                        properties.put("members",members);
                        newRevision.setProperties(properties);
                        return true;
                    }
                });
                onGroupJoinInterface.OnGroupJoined();
                    *//*PublicGroupListView fragment = new PublicGroupListView();
                 //   onGroupJoinInterface.OnGroupJoined();
                    android.support.v4.app.FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.tabs, fragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();*//*

            } catch (CouchbaseLiteException e)
                {e.printStackTrace();}
            }

        });*/
return v;
    }
  /*  @Override
    public void onAttach(Activity context) {
        super.onAttach(context);
        onGroupJoinInterface=(OnGroupJoinInterface)context;
    }*/

    public List<Group> generateData(Query groupQuery){
        List<Group> groupList=new ArrayList<Group>();
        Database db=app.getDatabase();
        String description=null;
        try {            QueryEnumerator enumerator=groupQuery.run();
            int count= enumerator.getCount();
            if(count>0){            for(int i=0;i<count;i++) {
                Group group=new Group();
                Document groupDoc = enumerator.getRow(i).getDocument();
                String title=groupDoc.getProperty("title").toString();
                if (groupDoc.getProperty("description")!=null)
                {  description=groupDoc.getProperty("description").toString();}
                String groupId=groupDoc.getId();
                ArrayList<String> members= (ArrayList < String > )groupDoc.getProperty("members");
               if (members!=null)
               {  int groupSize=members.size();
               group.setGroupSize(groupSize);
          }
                group.setGroupName(title);
                group.setGroupDescription(description);
                group.setGroupDoc(groupDoc);
               /* int previousCount;
                Document countDoc=db.getExistingDocument(groupId+"-count");// some count
                if(countDoc==null){
                    previousCount=0;
                }else{
                    previousCount=(int)countDoc.getProperty("count");
                }
                Query query = Chats.getQuery(db, groupId);
                QueryEnumerator chatEnumerator = query.run();
                int currentCount = chatEnumerator.getCount();
                int unreadMsg = currentCount - previousCount;
                if(currentCount>0) {
                    Document lastChatDoc = chatEnumerator.getRow(0).getDocument();
                    group.setChatDoc(lastChatDoc);
                }
                group.setGroupDoc(groupDoc);
                if(unreadMsg>=0) {
                    group.setUnreadMsgCount(unreadMsg);
                }*/
                if (groupDoc.getProperty("subType").equals("public"))
                { boolean ifMember=false;
                    if (members!=null)
                    {  for (int j=0;j<members.size();j++)
                    {
                        if (members.get(j)!=null&&members.get(j).equals("user-"+UserId))
                        {
                            ifMember=true;
                        }
                    }
                    if (!ifMember) //anshul change
                    { groupList.add(group);}}
                   // groupList.add(group);
            }}

            }
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
        return groupList;
    }
    public interface OnGroupJoinInterface{
        public void OnGroupJoined();
    }

}
