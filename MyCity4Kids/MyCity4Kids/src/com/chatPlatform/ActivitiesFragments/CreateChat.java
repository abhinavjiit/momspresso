package com.chatPlatform.ActivitiesFragments;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;


import com.chatPlatform.Adapters.ChatAdapter;
import com.chatPlatform.Chats;
import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.LiveQuery;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryEnumerator;
import com.couchbase.lite.UnsavedRevision;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by anshul on 16/12/15.
 */
public class CreateChat extends BaseActivity {
    Button send;
    EditText editText;
    ListView chatListView;
    BaseApplication app;
    String groupChannelId = null;
    int intialQueryLimit = 10;
    public static String userNumber;
    public static String userName;
    ChatAdapter chatAdapter;
    Query query;
    LiveQuery liveQuery;
Toolbar mToolbar;
TextView groupName;
    String title;
    int rows;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_create_list);
       // actionBar= getActionBar();
        mToolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        groupName=(TextView)findViewById(R.id.groupName);
        app = (BaseApplication) getApplicationContext();
        send = (Button) findViewById(R.id.button);
        editText = (EditText) findViewById(R.id.editText);
        chatListView = (ListView) findViewById(R.id.chatListView);
        userNumber = app.getUserNumber();
        userName = app.getUserName();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            groupChannelId = extras.getString("groupChannelId");
        }
        if (groupChannelId!=null)
        { Database database = app.getDatabase();
        title=database.getDocument(groupChannelId).getProperty("title").toString();}
        groupName.setText(title);

        query = Chats.getQuery(app.getDatabase(), groupChannelId);
        liveQuery = query.toLiveQuery();
        chatAdapter = new ChatAdapter(this, liveQuery);
        chatListView.setAdapter(chatAdapter);
       // int rows=query.getView().getTotalRows();

        try {
            final QueryEnumerator chatEnumerator = query.run();
            rows=chatEnumerator.getCount();
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
        Log.e("rows",rows+"");
        if (rows>10)
        {
            Log.e("rows",rows+"");
            final Button btnAddMore = new Button(this);
            btnAddMore.setText("load previous messages");
            chatListView.addHeaderView(btnAddMore);
            btnAddMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    intialQueryLimit += 10;
                    increaseQuery(intialQueryLimit);
                }
            });
        }
groupName.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        Intent intent=new Intent(CreateChat.this,GroupDetails.class);
        intent.putExtra("groupChannelId",groupChannelId);
        startActivity(intent);
    }
});
        // chat send logic
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String chatText = editText.getText().toString();
                if (chatText != null && !chatText.isEmpty()) {
                    String documentId = createChat(chatText);
                    editText.setText("");
                }

            }
        });

    }

    @Override
    protected void updateUi(Response response) {

    }

    @Override
    protected void onPause() {
        super.onPause();
        updateCount();
    }

    private String createChat(String chatText) {
        Database database = app.getDatabase();
        Document document = database.createDocument();
        String documentId = document.getId();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("_id", documentId);
        map.put("createdBy", app.getUserNumber());
        map.put("createdAt", System.currentTimeMillis());
        map.put("favouritedBy", "");
        map.put("type", "CHAT");
        map.put("channelId", groupChannelId);
        map.put("msg", chatText);
        map.put("userName", userName);
        try {
            // Save the properties to the document
            document.putProperties(map);
        } catch (CouchbaseLiteException e) {
            // Log.e(TAG, "Error putting", e);
            e.printStackTrace();
        }
        return documentId;


    }

    private void updateCount() {
        if (groupChannelId != null) {
            Database database = app.getDatabase();
            Document countDoc = database.getExistingDocument(groupChannelId + "-count");
            if (countDoc == null) {
                countDoc = database.getDocument(groupChannelId + "-count");
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("sync", "false");
                map.put("count", chatAdapter.getCount());
                try {
                    countDoc.putProperties(map);
                } catch (CouchbaseLiteException e) {
                    e.printStackTrace();
                }
            } else {
                int previousCount = (int) countDoc.getProperty("count");
                Database db = app.getDatabase();
                try {
                    Query query = Chats.getQuery(db, groupChannelId);
                    final QueryEnumerator chatEnumerator = query.run();
                    countDoc.update(new Document.DocumentUpdater() {
                        @Override
                        public boolean update(UnsavedRevision newRevision) {
                            int currentCount = chatEnumerator.getCount();
                            Map<String, Object> map = newRevision.getUserProperties();
                            map.put("count", currentCount);
                            newRevision.setUserProperties(map);
                            return true;
                        }
                    });
                } catch (CouchbaseLiteException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void increaseQuery(int intialQueryLimit) {
        liveQuery.setLimit(intialQueryLimit);
        query.setLimit(intialQueryLimit);
        try {
            chatAdapter.query = liveQuery;
            chatAdapter.enumerator = query.run();
            chatAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
