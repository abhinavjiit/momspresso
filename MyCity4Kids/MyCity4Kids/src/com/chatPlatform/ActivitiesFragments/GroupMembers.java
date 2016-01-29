package com.chatPlatform.ActivitiesFragments;

import android.os.Bundle;
import android.widget.ListView;

import com.chatPlatform.Adapters.GroupMembersAdapter;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by anshul on 1/14/16.
 */
public class GroupMembers extends BaseActivity {
   ListView membersList;
    BaseApplication app;
    String groupChannelId=null;
    ArrayList<String> members;
    GroupMembersAdapter groupMembersAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_members);
        membersList=(ListView)findViewById(R.id.membersList);
        app = (BaseApplication) getApplicationContext();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            groupChannelId = extras.getString("groupChannelId");
        }
        if (groupChannelId != null) {
            Database database = app.getDatabase();
            Document document = database.getDocument(groupChannelId);
            members  = (ArrayList < String > )document.getProperty("members");
           // descriptionContent = document.getProperty("description").toString();
        }
        groupMembersAdapter=new GroupMembersAdapter(GroupMembers.this, members);
        membersList.setAdapter(groupMembersAdapter);
    }

    @Override
    protected void updateUi(Response response) {

    }
}
