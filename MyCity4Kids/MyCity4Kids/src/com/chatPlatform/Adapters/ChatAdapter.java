package com.chatPlatform.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.couchbase.lite.Document;
import com.couchbase.lite.LiveQuery;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by anshul on 16/12/15.
 */
public class ChatAdapter extends LiveQueryAdapter  {
    Context mContext;
    String userNumber;
    public ChatAdapter(Context context, LiveQuery query)  {
        super(context, query);
        mContext=context;
     //   userNumber=UserNumber;

    }
    BaseApplication app;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Document task = (Document) getItem(position);
       app=(BaseApplication) mContext.getApplicationContext();
Log.e("userNo", ""+app.getUserNumber());
        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) parent.getContext().
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.custom_layout_left, null);
        }
        LinearLayout textlayout=(LinearLayout) convertView.findViewById(R.id.textlayout) ;
        ImageView imageView= (ImageView) convertView.findViewById(R.id.imageView);
        imageView.setImageResource(R.drawable.user);
        if (task!=null && task.getProperty("createdBy")!=null &&task.getProperty("createdBy").equals( app.getUserNumber()))
            {// convertView=inflater.inflate(R.layout.custom_layout_right,null);
                textlayout.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
            Log.e("right","called");
                imageView.setVisibility(View.GONE);}
            else
            { //convertView=inflater.inflate(R.layout.custom_layout_left,null);
                textlayout.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
imageView.setVisibility(View.VISIBLE);
            }


        if (task == null || task.getCurrentRevision() == null) {
            return convertView;
        }
        /*ImageView imageView= (ImageView) convertView.findViewById(R.id.imageView);
        imageView.setImageResource(R.drawable.user);*/
        TextView text = (TextView) convertView.findViewById(R.id.textView);
        TextView userName=(TextView) convertView.findViewById(R.id.userName);
        /*text.setText("dummy text");
        int totalCount=getCount();*/
        text.setText(task.getProperty("msg").toString());
        if (task.getProperty("userName")!=null)
        { Long yourmilliseconds=(Long) task.getProperty("createdAt");
           // Long l=Math.round(yourmilliseconds);
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm");
            Date resultdate = new Date(yourmilliseconds);
            System.out.println(sdf.format(resultdate));
            userName.setText(task.getProperty("userName").toString()+" ,"+sdf.format(resultdate));}
        return convertView;
    }
}
