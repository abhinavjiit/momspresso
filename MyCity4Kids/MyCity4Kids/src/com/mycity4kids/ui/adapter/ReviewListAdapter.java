package com.mycity4kids.ui.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.kelltontech.utils.DateTimeUtils;
import com.mycity4kids.R;
import com.mycity4kids.models.businesseventdetails.DetailsReviews;
import com.mycity4kids.widget.BitmapLruCache;
import com.mycity4kids.widget.CustomListView;

public class ReviewListAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<DetailsReviews> reviewList;
    private ImageLoader.ImageCache imageCache;
    private ImageLoader imageLoader;
    String ZERO_WIDTH_SPACE = "\u200b";
    int reviewPostion;
    boolean showReview;
    CustomListView mListView;

    public ReviewListAdapter(Context pContext) {
        mContext = pContext;
        imageCache = new BitmapLruCache();
        imageLoader = new ImageLoader(Volley.newRequestQueue(pContext), imageCache);
    }

    public void setReviewData(ArrayList<DetailsReviews> reviewDetailList, CustomListView _reviewListView) {
        reviewList = reviewDetailList;
        mListView = _reviewListView;
    }


    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return reviewList == null ? 0 : reviewList.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return reviewList.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        LayoutInflater mInflater = (LayoutInflater) mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.custom_review_cell, null);
            holder = new ViewHolder();
            holder.titleTxt = (TextView) convertView.findViewById(R.id.title_txt);
            holder.reviewerTxt = (TextView) convertView.findViewById(R.id.reviewer_txt);
            holder.descriptionTxt = (TextView) convertView.findViewById(R.id.description_txt);
            holder.dateTxt = (TextView) convertView.findViewById(R.id.date_txt);
            holder.authorImg = (NetworkImageView) convertView.findViewById(R.id.author_img);
            holder.ratingBar = (RatingBar) convertView.findViewById(R.id.ratingBar_review_new);
            holder.descriptionFull = (TextView) convertView.findViewById(R.id.description_full_txt);
            holder.viewMoreLess = (LinearLayout) convertView.findViewById(R.id.viewLout);
            holder.viewtxt = (TextView) convertView.findViewById(R.id.view_less_more);
            holder.boolValue = true;

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        //	holder.descriptionFull.setText(reviewList.get(position).getDescription());

        holder.titleTxt.setText(reviewList.get(position).getTitle());
        holder.reviewerTxt.setText(reviewList.get(position).getReviewer());
        holder.descriptionTxt.setText(reviewList.get(position).getDescription());
        //	holder.descriptionTxt.setTag(reviewList.get(position).getDescription());

        holder.viewMoreLess.setTag(position);
        holder.viewMoreLess.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {


                reviewPostion = Integer.valueOf(v.getTag().toString());
                showReview = true;
                //		mListView.refreshDrawableState();
                //	mListView.invalidate();

                //	mListView.invalidateViews();


                //	ListHeight.setListViewHeightBasedOnChildren(mListView);
                if (holder.descriptionFull.getVisibility() == View.GONE) {
                    holder.viewtxt.setText("View Less");
                    holder.descriptionTxt.setVisibility(View.GONE);
                    holder.descriptionFull.setVisibility(View.VISIBLE);
                    holder.descriptionFull.setText(reviewList.get(position).getDescription());
                    //       ListHeight.setListViewHeightBasedOnChildren(mListView,mContext);

                } else {
                    holder.viewtxt.setText(mContext.getString(R.string.view_more_profile));
                    holder.descriptionTxt.setVisibility(View.VISIBLE);
                    holder.descriptionFull.setVisibility(View.GONE);
                    holder.descriptionTxt.setText(reviewList.get(position).getDescription());
                    //	ListHeight.setListViewHeightBasedOnChildren(mListView,mContext);
                }
                notifyDataSetChanged();

            }

        });
		
		/*convertView.addOnLayoutChangeListener(new OnLayoutChangeListener() {
			
			@Override
			public void onLayoutChange(View v, int left, int top, int right,
					int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
				//  final ListView list_view = (ListView) getParent();
                 final ViewGroup.LayoutParams params = mListView.getLayoutParams();
                 params.height += bottom - oldBottom;
                 mListView.setLayoutParams(params);
                 mListView.requestLayout();
                 ListHeight.setListViewHeightBasedOnChildren(mListView);
                 notifyDataSetChanged();
                 
				
			}
		});*/
		
		/*if(showReview&&reviewPostion ==position&&holder.descriptionFull.getVisibility()==View.GONE){
			holder.viewtxt.setText("View Less");
			holder.descriptionTxt.setVisibility(View.GONE);
			holder.descriptionFull.setVisibility(View.VISIBLE);
			holder.descriptionFull.setText(reviewList.get(position).getDescription());
		}else{
			holder.viewtxt.setText("");
			holder.descriptionTxt.setVisibility(View.VISIBLE);
			holder.descriptionFull.setVisibility(View.GONE);
			holder.descriptionTxt.setText(reviewList.get(position).getDescription());
		}*/
		
		/*if(!reviewList.get(position).isExpend()){
			reviewList.get(position).setExpend(true);
		
		//	TextExpand.textDescription(reviewList.get(position).getDescription());
			
		}*/
        //	TextExpand.makeTextViewResizable(holder.descriptionTxt, 3, "View More",reviewList.get(position).isExpend());

        //TextExpand.textDescription(reviewList.get(position).getDescription());
        //	holder.descriptionTxt.setTag(reviewList.get(position).isExpend());
		/*if(holder.boolValue){
			holder.boolValue=false;

		//	holder.descriptionTxt.setText(reviewList.get(position).getDescription());
			TextExpand.makeTextViewResizable(holder.descriptionTxt, 3, "View More",false);

		}*/
        //	String changeDate=changedDate(reviewList.get(position).getCreated_date());
        holder.dateTxt.setText("Review was posted: " + reviewList.get(position).getCreated_date());
        holder.ratingBar.setRating(reviewList.get(position).getRatingcount());
        holder.authorImg.setImageUrl(reviewList.get(position).getUser_image(), imageLoader);
        return convertView;
    }

    class ViewHolder {
        NetworkImageView authorImg;
        TextView reviewerTxt;
        TextView titleTxt;
        TextView descriptionTxt;
        RatingBar ratingBar;
        TextView dateTxt;
        boolean boolValue;
        TextView descriptionFull;
        LinearLayout viewMoreLess;
        TextView viewtxt;
    }

    private String changedDate(String currentDate) {

        return DateTimeUtils.changeDateInddMMyyyy(currentDate);
    }

}
