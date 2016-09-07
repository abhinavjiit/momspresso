package com.mycity4kids.ui.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseFragment;
import com.mycity4kids.R;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.businesseventdetails.DetailsGallery;
import com.mycity4kids.models.businesseventdetails.DetailsReviews;
import com.mycity4kids.models.businesseventdetails.GalleryListtingData;
import com.mycity4kids.models.businesseventdetails.VideoDetails;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.ui.activity.WriteReviewActivity;
import com.mycity4kids.ui.activity.YouTubeVideoActivity;
import com.mycity4kids.ui.adapter.GalleryAdapter;
import com.mycity4kids.widget.BitmapLruCache;
import com.mycity4kids.widget.CustomGridView;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class ReviewFragment extends BaseFragment {
    private int mEventOrBusiness, mCategoryId;
    private String mBusinessOrEventId;
    LinearLayout _reviewLayout;

    ArrayList<DetailsReviews> mReviewList;
    private ImageLoader.ImageCache imageCache;
    private ImageLoader imageLoader;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_review, null, false);
        Utils.pushOpenScreenEvent(getActivity(), "Review resource/events", SharedPrefUtils.getUserDetailModel(getActivity()).getDynamoId() + "");

        try {
            _reviewLayout = (LinearLayout) view.findViewById(R.id.reviewLayout);

            TextView writeReview = (TextView) view.findViewById(R.id.write_review);
            TextView reviewcount = (TextView) view.findViewById(R.id.reviewcount);
            TextView tot_reviewrating = (TextView) view.findViewById(R.id.totalreviewrating);
            //		ReviewListAdapter _adapter=new ReviewListAdapter(getActivity());
            imageCache = new BitmapLruCache();
            imageLoader = new ImageLoader(Volley.newRequestQueue(getActivity()), imageCache);
            Bundle bundle = getArguments();
            if (bundle != null) {
                mReviewList = bundle.getParcelableArrayList("reviewDetails");
                mEventOrBusiness = getArguments().getInt(Constants.PAGE_TYPE);
                mCategoryId = getArguments().getInt(Constants.CATEGORY_ID);
                mBusinessOrEventId = getArguments().getString(Constants.BUSINESS_OR_EVENT_ID);
                DetailsGallery galleryDetails = getArguments().getParcelable("galleryData");
                ArrayList<VideoDetails> videoList = galleryDetails.getVideo();
                //ArrayList<VideoListingDetails> videoListing=galleryDetails.getListing_videos();
                reviewcount.setText("Reviews (" + mReviewList.size() + ")");
                float totrating = 0.0f;
                float avgrating = 0.0f;
                for (int i = 0; i < mReviewList.size(); i++) {
                    totrating = totrating + mReviewList.get(i).getRatingcount();
                }
                avgrating = totrating / mReviewList.size();
                DecimalFormat oneDForm = new DecimalFormat("#.#");

//                tot_reviewrating.setText("" + (float) avgrating);
                tot_reviewrating.setText(oneDForm.format(avgrating));

                // set value in business detail activity

                if (mReviewList != null && !mReviewList.isEmpty()) {
                    _reviewLayout.setVisibility(View.VISIBLE);
                    ((TextView) view.findViewById(R.id.txt_no_data)).setVisibility(View.GONE);
                    updateReviewList();
                }
                //reviewcount.setText(mReviewList.get());
                CustomGridView _gridVideoView = (CustomGridView) view.findViewById(R.id.video_view);
                _gridVideoView.setIsExpanded(true);
                ArrayList<GalleryListtingData> videosArrayList = new ArrayList<GalleryListtingData>();
            /*ArrayList<GalleryListtingData>	videosArrayList = new ArrayList<GalleryListtingData>();

			GalleryListtingData listData=new GalleryListtingData();
			listData.setImageUrl("http://i1.ytimg.com/vi/OeVkZA4Bv0M/1.jpg");
			videosArrayList.add(listData);

			GalleryListtingData listData1=new GalleryListtingData();
			listData1.setImageUrl("http://i1.ytimg.com/vi/OeVkZA4Bv0M/1.jpg");
			videosArrayList.add(listData1);

			GalleryListtingData listData2=new GalleryListtingData();
			listData2.setImageUrl("http://i1.ytimg.com/vi/OeVkZA4Bv0M/1.jpg");
			videosArrayList.add(listData2);

			GalleryListtingData listData3=new GalleryListtingData();
			listData3.setImageUrl("http://i1.ytimg.com/vi/OeVkZA4Bv0M/1.jpg");
			videosArrayList.add(listData3);*/
                if (videoList != null && videoList.size() > 0) {
                    for (VideoDetails videoDetail : videoList) {
                        GalleryListtingData videoListingData = new GalleryListtingData();
                        videoListingData.setImageUrl(videoDetail.getThumbnail());
                        videoListingData.setPlayImageUrl(videoDetail.getUrl());
                        videosArrayList.add(videoListingData);
                    }
                    /**
                     * CR:
                     */
                /*for( VideoListingDetails videoDetail : videoListing ) {
                    GalleryListtingData videoListingData = new GalleryListtingData();
					videoListingData.setImageUrl(videoDetail.getThumbnail());
					videoListingData.setPlayImageUrl(videoDetail.getUrl());
					videosArrayList.add(videoListingData);
				}*/
                    GalleryAdapter adapter1 = new GalleryAdapter(getActivity(), R.layout.custom_gallery_cell, Constants.SECOND_GALLERY);
                    adapter1.setData(videosArrayList);

                    _gridVideoView.setAdapter(adapter1);
                } else {
                    view.findViewById(R.id.video_txt).setVisibility(View.GONE);
                }

                if ((videosArrayList == null || videosArrayList.isEmpty())) {
                    view.findViewById(R.id.txt_no_data).setVisibility(View.VISIBLE);
                    view.findViewById(R.id.cardview).setVisibility(View.GONE);
                } else {
                    view.findViewById(R.id.txt_no_data).setVisibility(View.GONE);
                }
                if ((mReviewList == null || mReviewList.isEmpty())) {
                    view.findViewById(R.id.txt_no_data).setVisibility(View.VISIBLE);
                    view.findViewById(R.id.cardview).setVisibility(View.GONE);
                } else {
                    view.findViewById(R.id.cardview).setVisibility(View.VISIBLE);
                    view.findViewById(R.id.txt_no_data).setVisibility(View.GONE);
                }

//                if ((videosArrayList == null || videosArrayList.isEmpty()) && (mReviewList == null || mReviewList.isEmpty())) {
//                    view.findViewById(R.id.txt_no_data).setVisibility(View.VISIBLE);
//                    view.findViewById(R.id.cardview).setVisibility(View.GONE);
//                } else {
//                    view.findViewById(R.id.txt_no_data).setVisibility(View.GONE);
//                }

                //_reviewListView.invalidate();

                _gridVideoView.setOnItemClickListener(new OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                        if (parent.getAdapter() instanceof GalleryAdapter) {
                            GalleryListtingData galleryVideoData = (GalleryListtingData) ((GalleryAdapter) parent.getAdapter()).getItem(pos);
                            Intent intent = new Intent(getActivity(), YouTubeVideoActivity.class);
                            intent.putExtra("youTubeUrl", galleryVideoData.getPlayImageUrl());
                            startActivity(intent);
                        }

                    }
                });
            }
            writeReview.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), WriteReviewActivity.class);
                    intent.putExtra(Constants.PAGE_TYPE, mEventOrBusiness);
                    intent.putExtra(Constants.CATEGORY_ID, mCategoryId);
                    intent.putExtra(Constants.BUSINESS_OR_EVENT_ID, mBusinessOrEventId);
                    startActivity(intent);
                }
            });
        } catch (Exception e) {
            Log.i("ReviewFragmentInfo", e.getMessage());
            //e.printStackTrace();
        }
        return view;

    }

    private void updateReviewList() {
        LayoutInflater inflater = (LayoutInflater) getActivity().getLayoutInflater();

        int id = 0;
        for (int i = 0; i < mReviewList.size(); i++) {
            final int position = i;
            CardView convertView = (CardView) inflater.inflate(R.layout.custom_review_cell, null);
            final ViewHolder holder = new ViewHolder();
            holder.titleTxt = (TextView) convertView.findViewById(R.id.title_txt);
            holder.ratingtxt = (TextView) convertView.findViewById(R.id.ratingtxt);
            holder.reviewerTxt = (TextView) convertView.findViewById(R.id.reviewer_txt);
            holder.descriptionTxt = (TextView) convertView.findViewById(R.id.description_txt);
            holder.dateTxt = (TextView) convertView.findViewById(R.id.date_txt);
            holder.authorImg = (NetworkImageView) convertView.findViewById(R.id.author_img);
            holder.ratingBar = (RatingBar) convertView.findViewById(R.id.ratingBar_review_new);
            holder.descriptionFull = (TextView) convertView.findViewById(R.id.description_full_txt);
            holder.viewMoreLess = (LinearLayout) convertView.findViewById(R.id.viewLout);
            holder.viewtxt = (TextView) convertView.findViewById(R.id.view_less_more);
            holder.ratingtxt.setText("" + mReviewList.get(i).getRatingcount());
            holder.titleTxt.setText(mReviewList.get(position).getTitle());
            holder.reviewerTxt.setText(mReviewList.get(position).getReviewer());
            holder.descriptionTxt.setText(mReviewList.get(position).getDescription());
            holder.viewMoreLess.setTag(i);
            holder.ratingBar.setId(id++);


            //String desc = mReviewList.get(position).getDescription();
            /*if ( desc.length()<150) {    // Need to update
                holder.viewtxt.setVisibility(View.GONE);
			}*/
            /*if(holder.descriptionFull.getLineCount()<3){
                holder.viewtxt.setVisibility(View.GONE);
			}*/
            holder.viewMoreLess.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (holder.descriptionFull.getVisibility() == View.GONE) {
                        holder.viewtxt.setText("Less");
                        holder.descriptionTxt.setVisibility(View.GONE);
                        holder.descriptionFull.setVisibility(View.VISIBLE);
                        holder.descriptionFull.setText(mReviewList.get(position).getDescription());
                    } else {
                        holder.viewtxt.setText("More");
                        holder.descriptionTxt.setVisibility(View.VISIBLE);
                        holder.descriptionFull.setVisibility(View.GONE);
                        holder.descriptionTxt.setText(mReviewList.get(position).getDescription());
                    }

                }
            });
            //holder.dateTxt.setText("Review was posted: " + mReviewList.get(i).getCreated_date());
            holder.dateTxt.setText(mReviewList.get(position).getCreated_date());
            System.out.println("Review" + i + "---" + mReviewList.get(position).getRatingcount());
            holder.ratingBar.setRating(mReviewList.get(position).getRatingcount());
            //	holder.ratingBar.setRating(4);
            try {
                holder.authorImg.setDefaultImageResId(R.drawable.default_icon);
                holder.authorImg.setImageUrl(mReviewList.get(position).getUser_image(), imageLoader);
            } catch (Exception e) {
                holder.authorImg.setErrorImageResId(R.drawable.default_icon);
            }
            _reviewLayout.addView(convertView);

        }
    }

    private class ViewHolder {
        NetworkImageView authorImg;
        TextView reviewerTxt;
        TextView titleTxt;
        TextView descriptionTxt;
        RatingBar ratingBar;
        TextView dateTxt;
        TextView descriptionFull;
        LinearLayout viewMoreLess;
        TextView viewtxt;
        TextView ratingtxt;
    }

    @Override
    protected void updateUi(Response response) {
        // TODO Auto-generated method stub

    }

    public Bitmap getRoundedShape(Bitmap scaleBitmapImage) {
        // TODO Auto-generated method stub
        int targetWidth = 50;
        int targetHeight = 50;
        Bitmap targetBitmap = Bitmap.createBitmap(targetWidth,
                targetHeight, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(targetBitmap);
        Path path = new Path();
        path.addCircle(((float) targetWidth - 1) / 2,
                ((float) targetHeight - 1) / 2,
                (Math.min(((float) targetWidth),
                        ((float) targetHeight)) / 2),
                Path.Direction.CCW);

        canvas.clipPath(path);
        Bitmap sourceBitmap = scaleBitmapImage;
        canvas.drawBitmap(sourceBitmap,
                new Rect(0, 0, sourceBitmap.getWidth(),
                        sourceBitmap.getHeight()),
                new Rect(0, 0, targetWidth,
                        targetHeight), null);
        return targetBitmap;
    }

}
