package com.mycity4kids.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mycity4kids.R;
import com.mycity4kids.models.response.UserCommentsResult;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by anshul on 8/2/16.
 */
public class UserCommentsAdapter extends BaseAdapter {
    Context context;
    ArrayList<UserCommentsResult> commentsList;
    private LayoutInflater mInflator;
    TimeZone tz = TimeZone.getDefault();

    public UserCommentsAdapter(Context context, ArrayList<UserCommentsResult> commentsList) {
        this.context = context;
        this.commentsList = commentsList;
        mInflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return commentsList == null ? 0 : commentsList.size();
    }

    @Override
    public Object getItem(int position) {
        return commentsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    ViewHolder holder=null;
    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        if (view == null) {

            view = mInflator.inflate(R.layout.comments_list_item, null);
            holder = new ViewHolder();
            holder.txvCommmentsTitle = (TextView) view.findViewById(R.id.txvCommentsTitle);
            holder.commentNameDate = (TextView) view.findViewById(R.id.commentsNameDate);
            holder.txvCommentsText = (TextView) view.findViewById(R.id.txvCommentText);


            view.setTag(holder);}
        else {
            holder = (ViewHolder) view.getTag();
        }



        try {

            holder.txvCommentsText.setText(commentsList.get(position).getUserComment());


              holder.txvCommmentsTitle.setText(commentsList.get(position).getArticleTitle());
                Calendar calendar1 = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
            calendar1.setTimeInMillis(commentsList.get(position).getUpdatedTime() * 1000);
                holder.commentNameDate.setText(commentsList.get(position).getUserName()+", "+sdf.format(calendar1.getTime()));


        } catch (Exception e) {
            e.printStackTrace();
        }

        return view;
    }




    @Override
    public int getViewTypeCount() {
        return 2;
    }

    class ViewHolder {
        TextView txvCommmentsTitle;
        TextView commentNameDate;
        TextView txvCommentsText;



    }
}
