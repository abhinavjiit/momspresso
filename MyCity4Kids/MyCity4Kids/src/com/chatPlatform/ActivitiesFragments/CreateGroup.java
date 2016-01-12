package com.chatPlatform.ActivitiesFragments;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;


import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by anshul on 16/12/15.
 */
public class CreateGroup extends BaseActivity {
    EditText groupNameEditText, memberOne,memberTwo,memberThree,memberFour,groupType;
    Button addMemberButton;
    String membersArr[]=null;
    String owenrsArr[];
    String groupName;
    BaseApplication app;
    int counter;
    String groupTypeInput;
    Switch groupTypeSwitch;
    ArrayList<String> membersList;
    Toolbar mToolbar;
    TextView publicGroupTV,privateGroupTV;
    String documentId;
    String admin;
ActionBar actionBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.group_create);

        mToolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("CREATE GROUP");

      /*  ActionBar actionBar = getActionBar();
        if (actionBar!=null)
        {  actionBar.setTitle(" CREATE GROUP");}*/
        counter=0;
        app= (BaseApplication) getApplicationContext();
        SharedPreferences _sharedPref = getSharedPreferences("my_city_prefs", Context.MODE_PRIVATE);
        //UserInfo user = new UserInfo();
        //user.setId(_sharedPref.getInt(USER_ID, 0));
        int UserId=_sharedPref.getInt("userid", 0);
        admin="user-"+UserId;
      /*  membersArr=new String[2];
        membersArr[0]="user-156850";
        membersArr[1]="user-0987654321";*/
        ArrayList<String> membersList;
        owenrsArr=new String[2];
        owenrsArr[0]="user-"+UserId;
        groupNameEditText= (EditText) findViewById(R.id.groupName_editText);
        groupTypeSwitch=(Switch) findViewById(R.id.groupTypeSwitch);
        publicGroupTV=(TextView)findViewById(R.id.publicGroup);
        privateGroupTV=(TextView)findViewById(R.id.privateGroup);
        ColorStateList buttonStates = new ColorStateList(
                new int[][]{
                        new int[]{android.R.attr.state_checked},
                        new int[]{-android.R.attr.state_enabled},
                        new int[]{}
                },
                new int[]{
                        Color.RED,
                        Color.BLUE,
                        Color.GREEN
                }
        );
        groupTypeInput="private";
        groupTypeSwitch.getThumbDrawable().setColorFilter(Color.BLUE, PorterDuff.Mode.MULTIPLY);
        groupTypeSwitch.getTrackDrawable().setColorFilter(Color.BLUE, PorterDuff.Mode.MULTIPLY);
        groupTypeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                {
                    groupTypeInput="private";
                    privateGroupTV.setTextColor(Color.BLUE);
                    publicGroupTV.setTextColor(Color.BLACK);
                }
                else
                {
                    groupTypeInput="public";
                    publicGroupTV.setTextColor(Color.BLUE);
                    privateGroupTV.setTextColor(Color.BLACK);

                }
            }
        });
       /* memberOne= (EditText) findViewById(R.id.member_one_editText);
        memberTwo= (EditText) findViewById(R.id.member_two_editText);
        memberThree= (EditText) findViewById(R.id.member_three_editText);
        memberFour= (EditText) findViewById(R.id.member_four_edit_text);
        groupType=(EditText)findViewById(R.id.group_type);*/
       /* addMemberButton= (Button) findViewById(R.id.addMemberButton);
        addMemberButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                groupName=groupNameEditText.getText().toString();
              //  groupTypeInput=groupType.getText().toString();
              *//*  String firstMember=  memberOne.getText().toString();
                String secondMember = memberTwo.getText().toString();
                String thirdMember =  memberThree.getText().toString();
                String fourthMember =  memberFour.getText().toString();
                if(firstMember != null && !firstMember.isEmpty()){
                    membersArr[counter]=firstMember;
                    counter=counter+1;
                }
                if(secondMember != null && !secondMember.isEmpty()){
                    membersArr[counter]=secondMember;
                    counter=counter+1;
                }
                if(thirdMember != null && !thirdMember.isEmpty()){
                    membersArr[counter]=thirdMember;
                    counter=counter+1;
                }
                if(fourthMember != null && !fourthMember.isEmpty()){
                    membersArr[counter]=fourthMember;
                    counter=counter+1;
                }*//*

              documentId=createGroup();
                if(documentId.length()>0){
                   *//* Intent intent = new Intent(getApplicationContext(), ChatDashboard.class);
                    intent.putExtra("toOpen","publicFragment");
                    startActivity(intent);*//*

                }

            }
        });*/
        TextView next=(TextView)findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                groupName=groupNameEditText.getText().toString();
                documentId=createGroup();
                if (documentId!=null)
                {Intent intent=new Intent(CreateGroup.this,AccessContacts.class);
                intent.putExtra("groupId",documentId);
                startActivity(intent);}
            }
        });
    }

    @Override
    protected void updateUi(Response response) {

    }

    private  String createGroup() {
        Database database= app.getDatabase();
        Document document = database.createDocument();
        String documentId = document.getId();


        Map<String, Object> map = new HashMap<String, Object>();

        map.put("_id", documentId);
        map.put("createdBy",app.getUserNumber());
        map.put("createdAt", System.currentTimeMillis());
        map.put("type", "GROUP");
        map.put("channelId", documentId);
        map.put("admins", owenrsArr);
      //  map.put("members", membersArr);
        map.put("members", owenrsArr);
        map.put("blockedMembers", "");
        map.put("title", groupName);
        map.put("description", "This is new group");
        map.put("lastMessage", "");
       // map.put("membersList",membersList);

        if(groupTypeInput.equalsIgnoreCase("public")){
            map.put("subType","public");
        }else{
            map.put("subType","private");
        }
        try {
            // Save the properties to the document
            document.putProperties(map);
        } catch (CouchbaseLiteException e) {
            // Log.e(TAG, "Error putting", e);
            e.printStackTrace();
        }
        return documentId;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    /*    MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        final Menu m = menu;
        final MenuItem item = menu.findItem(R.id.next);
        return super.onCreateOptionsMenu(menu);*/
        getMenuInflater().inflate(R.menu.next, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.next:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

