package com.mycity4kids.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mycity4kids.R;
import com.mycity4kids.editor.DraftListViewActivity;
import com.mycity4kids.models.response.ReviewListingResult;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by anshul on 3/16/16.
 */
public class PublishedArticleListAdapter extends BaseAdapter {
    Context context;
    ArrayList<ReviewListingResult> reviewList;
    private LayoutInflater mInflator;
    DraftListViewActivity draftListView;
    TimeZone tz = TimeZone.getDefault();

    public PublishedArticleListAdapter(Context context, ArrayList<ReviewListingResult> reviewList) {
        this.context = context;
        this.reviewList = reviewList;
        mInflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return reviewList == null ? 0 : reviewList.size();
    }

    @Override
    public Object getItem(int position) {
        return reviewList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    ViewHolder holder = null;

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        if (view == null) {

            view = mInflator.inflate(R.layout.review_list_item, null);
            holder = new ViewHolder();
            holder.txvReviewTitle = (TextView) view.findViewById(R.id.txvReviewTitle);
            holder.reviewAddressDate = (TextView) view.findViewById(R.id.reviewAddressDate);
            holder.txvReviewText = (TextView) view.findViewById(R.id.txvReviewText);
            holder.ratingNo = (TextView) view.findViewById(R.id.ratingNo);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }


        try {

            holder.txvReviewText.setText(reviewList.get(position).getDescription());
            holder.ratingNo.setText(reviewList.get(position).getRating() + "/5");
            if (reviewList.get(position).getType().equals("business")) {
                holder.txvReviewTitle.setText(reviewList.get(position).getBusinessName());
                Calendar calendar1 = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
                calendar1.setTime(sdf.parse(reviewList.get(position).getDate()));
                holder.reviewAddressDate.setText(reviewList.get(position).getBusinessAddress() + ", " + sdf.format(calendar1.getTime()));
            } else {
                holder.txvReviewTitle.setText(reviewList.get(position).getEventName());
                Calendar calendar1 = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
                calendar1.setTime(sdf.parse(reviewList.get(position).getDate()));
                holder.reviewAddressDate.setText(reviewList.get(position).getEventAddress() + ", " + sdf.format(calendar1.getTime()));
            }
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
        TextView txvReviewTitle;
        TextView reviewAddressDate;
        TextView txvReviewText;
        TextView ratingNo;


    }
}
