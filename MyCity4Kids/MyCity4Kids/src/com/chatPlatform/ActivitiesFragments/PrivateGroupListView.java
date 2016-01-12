package com.chatPlatform.ActivitiesFragments;

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
import android.widget.ListView;
;import com.chatPlatform.Adapters.GroupAdapter;
import com.chatPlatform.Adapters.NewGroupAdapter;
import com.chatPlatform.Chats;
import com.chatPlatform.Groups;
import com.chatPlatform.models.Group;
import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.LiveQuery;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryEnumerator;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by anshul on 14/12/15.
 */
public class PrivateGroupListView extends Fragment {
    RecyclerView listView;
    BaseApplication app;
    GroupAdapter groupAdapter;
    Query query;
    int counter=0;
int UserId;
    List<Group> groupList;
    NewGroupAdapter newGroupAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.private_group_listview,container,false);

        listView= (RecyclerView) v.findViewById(R.id.listView);
        app= (BaseApplication) getActivity().getApplication();
        getActivity().getApplication();

        //  Application application = (Application) getActivity().getApplication();
        query = Groups.getQuery(app.getDatabase());
        groupList= generateData(query);
     //   groupAdapter=new GroupAdapter(getActivity(),groupList);
        newGroupAdapter=new NewGroupAdapter(getActivity(),groupList);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        listView.setLayoutManager(llm);
        listView.setAdapter(newGroupAdapter);
      //  listView.setAdapter(groupAdapter);
        LiveQuery liveQuery = query.toLiveQuery();
        // groupAdapter=new GroupAdapter(getContext(),liveQuery);
        //  listView.setAdapter(groupAdapter);
        SharedPreferences _sharedPref = getActivity().getSharedPreferences("my_city_prefs", Context.MODE_PRIVATE);
        UserId =_sharedPref.getInt("userid", 0);
/*

        listView.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //view.getTe
                Group group = (Group)groupAdapter.getItem(position);
                Document document=group.getGroupDoc();
                String title=document.getProperty("title").toString();
                // test only
                Intent intent = new Intent(getActivity(), CreateChat.class);
                intent.putExtra("groupChannelId",document.getProperty("channelId").toString());
                startActivity(intent);
            }
        });
*/



        return v;}
    public List<Group> generateData(Query groupQuery){
        List<Group> groupList=new ArrayList<Group>();
        Database db=app.getDatabase();
        try {
            QueryEnumerator enumerator=groupQuery.run();
            int count= enumerator.getCount();
            if(count>0){            for(int i=0;i<count;i++) {
                Group group=new Group();
                Document groupDoc = enumerator.getRow(i).getDocument();
                Map<String, Object> properties = groupDoc.getProperties();
                if (properties.containsKey("subType")) {
                    if (groupDoc != null && "private".equals(groupDoc.getProperty("subType").toString())) {
                        String title = groupDoc.getProperty("title").toString();
                        String groupId = groupDoc.getId();
                        ArrayList<String> members= (ArrayList < String > )groupDoc.getProperty("members");
                        int previousCount;
                        Document countDoc = db.getExistingDocument(groupId + "-count");// some count
                        if (countDoc == null) {
                            previousCount = 0;
                        } else {
                            previousCount = (int) countDoc.getProperty("count");
                        }
                        Query query = Chats.getQuery(db, groupId);
                        QueryEnumerator chatEnumerator = query.run();
                        int currentCount = chatEnumerator.getCount();
                        int unreadMsg = currentCount - previousCount;
                        if (currentCount > 0) {
                            Document lastChatDoc = chatEnumerator.getRow(currentCount-1).getDocument();
                            group.setChatDoc(lastChatDoc);
                        }
                        group.setGroupDoc(groupDoc);
                        if (unreadMsg >= 0) {
                            group.setUnreadMsgCount(unreadMsg);
                        }
                        boolean ifMember=false;
                        if (members!=null)
                        {  for (int j=0;j<members.size();j++)
                        {
                            if (members.get(j)!=null&&members.get(j).equals("user-"+UserId))
                            {
                                ifMember=true;
                            }
                        }
                            if (ifMember)
                            { groupList.add(group);}}
                       // groupList.add(group);
                    }
                }
            }

            }
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
        return groupList;
    }

    @Override
    public void onPause() {
        super.onPause();
        counter++;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(groupList!=null && counter>0) {
            groupList.clear();
            List<Group> grpList=  generateData(query);
            groupList.addAll(grpList);
            newGroupAdapter.notifyDataSetChanged();
        }
    }
}
