package com.chatPlatform.ActivitiesFragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Base64InputStream;
import android.util.Base64OutputStream;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.chatPlatform.Adapters.ContactsAdapter;
import com.chatPlatform.ContactStore;
import com.chatPlatform.Controllers.ControllerCreateGroup;
import com.chatPlatform.models.GroupInfo;
import com.google.api.client.json.Json;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.mycity4kids.R;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.models.user.UserInfo;
import com.mycity4kids.preference.SharedPrefUtils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by anshul on 21/12/15.
 */
public class AccessContacts extends BaseActivity {
    static int i=0;
    ArrayList<ContactStore> ContactList;
    EditText editTextsearchContact;
    ContactsAdapter adapter;
    ProgressDialog progressDialog;
    ListView listView;
    ArrayList<String> checkedList;
    Button doneButton;
    String groupId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
          setContentView(R.layout.access_contacts);
        //   textDetail = (TextView) findViewById(R.id.textView1);
        listView =(ListView) findViewById(R.id.contactListView);
        editTextsearchContact =(EditText) findViewById(R.id.searchContacts);
        doneButton=(Button)findViewById(R.id.done);
        ContactList=new ArrayList<ContactStore>();
        functionShowingProgressDialog();
        Intent intent=getIntent();
        groupId=intent.getStringExtra("groupId");
        Log.e("groupId", groupId);
        new LoadContacts().execute();
//        readContacts();

//        progressDialog.cancel();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void updateUi(Response response) {

    }

    public void readContacts() {
//        functionShowingProgressDialog();
        StringBuffer sb = new StringBuffer();
        //ParseObject contact
        ContactStore contact;
        sb.append("......Contact Details.....");
        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        String phone,phone1,phone2,phone3 = null;
        String phoneNo=null;
        String emailContact = null;
        String emailType = null;


        if (cur.getCount() > 0) {
            while (cur.moveToNext()) {
                contact=new ContactStore();
                String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                //    image_uri = cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI));
                if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                    System.out.println("name : " + name + ", ID : " + id);
                   // contact.put("Name", name);
                    contact.setContactName(name);
                    Log.e("name", name);
                    sb.append("\n Contact Name:" + name);
                    Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);
                    i=0;
                    while (pCur.moveToNext()) {
                        if (i==0)
                        {   phone = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            sb.append("\n Phone number:" + phone);
                          //  contact.put("MobileNo", phone);
                             phoneNo=functionForFormattingNumber(phone);
                            contact.setPhoneNumber(phoneNo);
                            Log.e("mobile", phoneNo);
                            System.out.println("phone" + phone);}
                        if (i==1)
                        {   phone1 = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            sb.append("\n Phone number:" + phone1);
                            //contact.put("phone1", phone1);
                            String phoneNo1=functionForFormattingNumber(phone1);
                            if (phoneNo1!=null&&!phoneNo1.equals(phoneNo))
                            {  contact.setPhoneNumber1(phoneNo1);
                            Log.e("mobile", phoneNo1);}
                            System.out.println("phone" + phone1);}
                       /* if (i==2)
                        {   phone2 = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            sb.append("\n Phone number:" + phone2);
                            contact.put("phone2", phone2);
                            Log.e("mobile", phone2);
                            System.out.println("phone" + phone2);}
                        if (i==3)
                        {   phone3 = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            sb.append("\n Phone number:" + phone3);
                            contact.put("phone3", phone3);
                            Log.e("mobile", phone3);
                            System.out.println("phone" + phone3);}*/
                        i++;

                    }
                    pCur.close();
                /*    Cursor emailCur = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null, ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?", new String[]{id}, null);
                    while (emailCur.moveToNext()) {
                        emailContact = emailCur.getString(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                        emailType = emailCur.getString(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE));
                        contact.put("Email", emailContact);
                        sb.append("\nEmail:" + emailContact + "Email type:" + emailType);
                        Log.e("email", emailContact);
                        System.out.println("Email " + emailContact + " Email Type : " + emailType);
                    }
                    emailCur.close();
                }*/

                if (contact.getPhoneNumber()!=null)
                {ContactList.add(contact);}
                   // progressDialog.cancel();
            }}// Log.e("listsize",""+ContactList.size()+ContactList.get(7).getString("MobileNo")+ContactList.get(56).getString("MobileNo"));
        //ParseObject.saveAllInBackground(ContactList);
    }
        progressDialog.cancel();
}
    public void searchContact() {

        editTextsearchContact.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {


                String text = editTextsearchContact.getText().toString().toLowerCase(Locale.getDefault());
                adapter.functionImplementingSearch(text);



            }
        });

    }
    public void functionShowingProgressDialog() {
        progressDialog = new ProgressDialog(AccessContacts.this);
        Log.e("progress Dialogue","entered");
        progressDialog.setTitle("Loading Contacts...");
        progressDialog.setMessage("Please wait while contacts are being loaded");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }
    class LoadContacts extends AsyncTask<String,Void,String>
    {


        @Override
        protected String doInBackground(String... params) {
           readContacts();
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            adapter=new ContactsAdapter(ContactList);
            listView.setAdapter(adapter);
            searchContact();
            checkedList=new ArrayList<String>();
         doneButton.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 for (int i=0;i<ContactList.size();i++)
                 {
                     ContactStore contactStore=(ContactStore) adapter.getItem(i);
                     if (adapter.sba.get(i))
                     {
                         checkedList.add(contactStore.getPhoneNumber());
                         if (contactStore.getPhoneNumber1()!=null)
                         {
                             checkedList.add(contactStore.getPhoneNumber1());
                         }
                         Log.e(contactStore.getContactName(),"test");
                     }

                 }
                 Log.e("checkedListSize",checkedList.size()+"");
                 SharedPrefUtils.getUserDetailModel(AccessContacts.this).getId();
                 SharedPreferences _sharedPref = getSharedPreferences("my_city_prefs", Context.MODE_PRIVATE);
                 //UserInfo user = new UserInfo();
                 //user.setId(_sharedPref.getInt(USER_ID, 0));
                 int UserId=_sharedPref.getInt("userid", 0);
                // String groupId="123abc456";
                 Log.e("userId",UserId+"");
                 Log.e("sharedPref", SharedPrefUtils.getUserDetailModel(AccessContacts.this).getId() + "");
                 GroupInfo groupInfo=new GroupInfo();
                 groupInfo.setGroupId(groupId);
                 groupInfo.setInviteList(checkedList);
                 groupInfo.setUserId(UserId);
               //  new InviteApi().execute();
                 ControllerCreateGroup controllerCreateGroup=new ControllerCreateGroup(AccessContacts.this,AccessContacts.this);
                 controllerCreateGroup.getData(AppConstants.CREATE_GROUP_REQUEST, groupInfo);


             }
         });
        }
    }
    public String functionForFormattingNumber(String stringNumber) {
        String formattedNumber = "";
        char firstChars = stringNumber.charAt(0);
        if (firstChars == '+') {

            for (int i = 3; i < stringNumber.length(); i++) {
                char numberChar = stringNumber.charAt(i);
                if (numberChar == '0' || numberChar == '1' || numberChar == '2' || numberChar == '3' || numberChar == '4' || numberChar == '5' || numberChar == '6' || numberChar == '7' || numberChar == '8' || numberChar == '9') {
                    formattedNumber = formattedNumber + numberChar;
                }
            }

        } else if (firstChars == '0') {

            for (int i = 1; i < stringNumber.length(); i++) {
                char numberChar = stringNumber.charAt(i);
                if (numberChar == '0' || numberChar == '1' || numberChar == '2' || numberChar == '3' || numberChar == '4' || numberChar == '5' || numberChar == '6' || numberChar == '7' || numberChar == '8' || numberChar == '9') {
                    formattedNumber = formattedNumber + numberChar;
                }
            }
        } else {

            for (int i = 0; i < stringNumber.length(); i++) {
                char numberChar = stringNumber.charAt(i);
                if (numberChar == '0' || numberChar == '1' || numberChar == '2' || numberChar == '3' || numberChar == '4' || numberChar == '5' || numberChar == '6' || numberChar == '7' || numberChar == '8' || numberChar == '9') {
                    formattedNumber = formattedNumber + numberChar;
                }
            }
        }

        return formattedNumber;
    }
    private JSONObject getConvertedinJson(int userId, String groupId,ArrayList<String> list) {

        JSONObject object = new JSONObject();
        try {
            object.put("userId", userId);
            object.put("groupId", groupId);
            object.put("inviteList",list);
           Log.e("jsonString", object.toString());
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return object;
    }
   /* public JSONObject getJSONFromUrl(String url, JSONObject jObj) {
        InputStream is=null;
        String json=null;
        // Making HTTP request
        try {
            // Default Http Client
            DefaultHttpClient httpClient = new DefaultHttpClient();
            // Http Post Header
            HttpPost httpPost = new HttpPost(url);
            StringEntity se = new StringEntity(jObj.toString());
            httpPost.setEntity(se);
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");
            List<Cookie> cookies = loadSharedPreferencesCookie();
            if (cookies != null) {
                CookieStore cookieStore = new BasicCookieStore();
                for (int i = 0; i < cookies.size(); i++)
                    cookieStore.addCookie(cookies.get(i));
                ((DefaultHttpClient) httpClient).setCookieStore(cookieStore);
            }

           // httpResponse = httpClient.execute(getOrPost);
            cookies = ((DefaultHttpClient) httpClient).getCookieStore().getCookies();
            saveSharedPreferencesCookies(cookies);


            // Execute Http Post Request
            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            is = httpEntity.getContent();
            Log.e("inputstream",is.toString());

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    *//*
     * To convert the InputStream to String we use the
     * BufferedReader.readLine() method. We iterate until the BufferedReader
     * return null which means there's no more data to read. Each line will
     * appended to a StringBuilder and returned as String.
     *//*
*//*        try {
            // Getting Server Response
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            // Reading Server Response
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            json = sb.toString();
            Log.e("print",json);
        } catch (Exception e) {
            Log.e("Buffer Error", "Error converting result " + e.toString());
        }

        // try parse the string to a JSON object
        try {
            jObj = new JSONObject(json);
        } catch (JSONException e) {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        }

        // return JSON String
        Log.e("JSON Parser", jObj.toString());*//*
        return jObj;

    }*/
 class InviteApi extends AsyncTask<String,Void,String>
 {

     @Override
     protected String doInBackground(String... params) {
       /*  JSONObject jsonObject = getJSONFromUrl(
                 "http://10.42.0.1/apiusers/group_invites",
                 getConvertedinJson( SharedPrefUtils.getUserDetailModel(AccessContacts.this).getId(), "123abc456",checkedList));
         Log.e("response",jsonObject.toString());*/
         ControllerCreateGroup controllerCreateGroup=new ControllerCreateGroup(AccessContacts.this,AccessContacts.this);

         return null;
     }

     @Override
     protected void onPostExecute(String s) {
         super.onPostExecute(s);
         Log.e("testresponse", "response");
     }
 }

    /*private void saveSharedPreferencesCookies(List<Cookie> cookies) {
        SerializableCookie[] serializableCookies = new SerializableCookie[cookies.size()];
        for (int i = 0; i < cookies.size(); i++) {
            SerializableCookie serializableCookie = new SerializableCookie(cookies.get(i));
            serializableCookies[i] = serializableCookie;
        }
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        ObjectOutputStream objectOutput;
        ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
        try {
            objectOutput = new ObjectOutputStream(arrayOutputStream);


            objectOutput.writeObject(serializableCookies);
            byte[] data = arrayOutputStream.toByteArray();
            objectOutput.close();
            arrayOutputStream.close();

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Base64OutputStream b64 = new Base64OutputStream(out, Base64.DEFAULT);
            b64.write(data);
            b64.close();
            out.close();

            editor.putString("cookies", new String(out.toByteArray()));
            editor.apply();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/
/*    private List<Cookie> loadSharedPreferencesCookie() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        byte[] bytes = preferences.getString("cookies", "{}").getBytes();
        if (bytes.length == 0 || bytes.length == 2)
            return null;
        ByteArrayInputStream byteArray = new ByteArrayInputStream(bytes);
        Base64InputStream base64InputStream = new Base64InputStream(byteArray, Base64.DEFAULT);
        ObjectInputStream in;
        List<Cookie> cookies = new ArrayList<Cookie>();
        SerializableCookie[] serializableCookies;
        try {
            in = new ObjectInputStream(base64InputStream);
            serializableCookies = (SerializableCookie[]) in.readObject();
            for (int i = 0; i < serializableCookies.length; i++) {
                Cookie cookie = serializableCookies[i].getCookie();
                cookies.add(cookie);
            }
            return cookies;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }*/
}

