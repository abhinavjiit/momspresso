package com.mycity4kids.widget;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kelltontech.ui.IScreen;
import com.kelltontech.utils.DataUtils;
import com.kelltontech.utils.DateTimeUtils;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.controller.FavoriteAndBeenThereController;
import com.mycity4kids.dbtable.UserTable;
import com.mycity4kids.enums.AddReviewOrPhoto;
import com.mycity4kids.fragmentdialog.CameraFragmentDialog;
import com.mycity4kids.fragmentdialog.LoginFragmentDialog;
import com.mycity4kids.gtmutils.GTMEventType;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.interfaces.IOnSubmitGallery;
import com.mycity4kids.models.businesseventdetails.Batches;
import com.mycity4kids.models.businesslist.BusinessDataListing;
import com.mycity4kids.models.favorite.FavoriteRequest;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.ui.activity.BusinessDetailsActivity;
import com.mycity4kids.ui.activity.RecentlyViewedActivity;

import java.util.ArrayList;
import java.util.TimeZone;

public class DetailHeader extends RelativeLayout implements OnClickListener {
    private LinearLayout mDetailsLayout;
    private View view;
    private FavoriteRequest requestData;
    private BusinessDataListing detailsResponse;
    private int mCategoryId;
    private int mEventOrBusiness;
    private String mDistance;
    private boolean fromEvents;

    public DetailHeader(Context context) {
        super(context);
    }

    public DetailHeader(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public DetailHeader(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void inflateHeader(int mEventOrBusiness) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (mEventOrBusiness == Constants.BUSINESS_PAGE_TYPE) {
            fromEvents = false;
            view = inflater.inflate(R.layout.details_animated_viewbusiness, this);
        } else if (mEventOrBusiness == Constants.EVENT_PAGE_TYPE) {
            fromEvents = true;
            view = inflater.inflate(R.layout.details_animated_viewnew, this);
        }

        // view.setOnTouchListener(this);
        //anupama
    /*	RelativeLayout bottomLineLout = (RelativeLayout) view.findViewById(R.id.bottomLineLout);
        bottomLineLout.setOnClickListener(this);*/
        View transparent = view.findViewById(R.id.transparentView);
        transparent.setOnClickListener(this);
        //anupama
        /*((TextView) view.findViewById(R.id.favorite)).setOnClickListener(this);
        ((TextView) view.findViewById(R.id.been_there)).setOnClickListener(this);
		((TextView) view.findViewById(R.id.share_with_frnd)).setOnClickListener(this);*/
        view.findViewById(R.id.write_a_review).setOnClickListener(this);
        view.findViewById(R.id.add_a_photo).setOnClickListener(this);
        view.findViewById(R.id.direction).setOnClickListener(this);
        view.findViewById(R.id.recentlyviewed).setOnClickListener(this);


        if (mEventOrBusiness == Constants.EVENT_PAGE_TYPE) {
            //anupama
            /*((TextView) view.findViewById(R.id.add_to_calendar)).setVisibility(View.VISIBLE);
			((TextView) view.findViewById(R.id.add_to_calendar)).setOnClickListener(this);*/

        }
        mDetailsLayout = (LinearLayout) view.findViewById(R.id.topBottomLout);

    }

    public void setRequestData(FavoriteRequest pRequestData) {
        requestData = pRequestData;
    }

    public void setBusinessDetails(BusinessDataListing businessDataListing, int categoryId, int pEventOrBusiness, String distance) {
        detailsResponse = businessDataListing;
        mCategoryId = categoryId;
        mEventOrBusiness = pEventOrBusiness;
        mDistance = distance;
    }

    public void bottomToTop() {

        Animation bottomUp = AnimationUtils.loadAnimation(getContext(),
                R.anim.bottom_up);
        mDetailsLayout.startAnimation(bottomUp);
        mDetailsLayout.setVisibility(View.GONE);
    }

    public void TopToBottom() {

        Animation topToBottom = AnimationUtils.loadAnimation(getContext(), R.anim.bottom_down);

        mDetailsLayout.startAnimation(topToBottom);
        mDetailsLayout.setVisibility(View.VISIBLE);
    }

    public void collepseView() {
        if (mDetailsLayout.getVisibility() == View.GONE) {
            Animation topToBottom = AnimationUtils.loadAnimation(getContext(), R.anim.bottom_down);
            mDetailsLayout.startAnimation(topToBottom);
            mDetailsLayout.setVisibility(View.VISIBLE);
        } else {
            Animation bottomUp = AnimationUtils.loadAnimation(getContext(), R.anim.bottom_up);
            bottomUp.setAnimationListener(makeTopGone);
            mDetailsLayout.startAnimation(bottomUp);
        }
    }

    public void hideView() {
        Animation bottomUp = AnimationUtils.loadAnimation(getContext(), R.anim.bottom_up);
        bottomUp.setAnimationListener(makeTopGone);
        mDetailsLayout.startAnimation(bottomUp);

    }

    public int getVisivility() {
        return mDetailsLayout.getVisibility();

    }

    @Override
    public void onClick(View v) {
        UserTable _table = new UserTable((BaseApplication) getContext().getApplicationContext());
        int count = _table.getCount();

        FavoriteAndBeenThereController _controller = new FavoriteAndBeenThereController((Activity) getContext(), (IScreen) getContext());
        BusinessDetailsActivity _activity = (BusinessDetailsActivity) getContext();
        if (mEventOrBusiness == Constants.BUSINESS_PAGE_TYPE) {
            switch (v.getId()) {

                case R.id.write_a_review:
                    Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    Utils.pushEvent(getContext(), GTMEventType.SHARE_RESOURCES_CLICKED_EVENT, SharedPrefUtils.getUserDetailModel(getContext()).getDynamoId() + "", "Resource/Event Details");


                    String titleName = detailsResponse.getName();
                    if (StringUtils.isNullOrEmpty(titleName)) {
                        titleName = "";
                    }

                    String webUrl = detailsResponse.getWeb_url();
                    if (StringUtils.isNullOrEmpty(webUrl)) {
                        webUrl = "";
                    }


                    String shareMessage = "Momspresso\n\nCheck out this interesting place I came across - " + titleName + ".\n" + webUrl;
                    shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareMessage);

                    getContext().startActivity(Intent.createChooser(shareIntent, "Momspresso"));


                    break;
                case R.id.recentlyviewed:

                    Intent i = new Intent(_activity, RecentlyViewedActivity.class);
                    i.putExtra(Constants.FROM_EVENTS, fromEvents);
                    _activity.startActivity(i);
                    break;
                case R.id.direction:

                    _activity.moveToMap();

                    break;

                default:
                    break;
            }
        } else if (mEventOrBusiness == Constants.EVENT_PAGE_TYPE) {
            switch (v.getId()) {
                case R.id.write_a_review:

                    ((BusinessDetailsActivity) getContext()).writeReviewFromHeader(AddReviewOrPhoto.WriteAReview);

                    // _activity.showToast("Under Implementation");
                    break;
                case R.id.add_a_photo:

                    if (count <= 0) {
                        goToLoginDialog();
                        return;
                    }
                    CameraFragmentDialog fragmentDialog = new CameraFragmentDialog();
                    fragmentDialog.setSubmitListner((IOnSubmitGallery) getContext());
                    fragmentDialog.show(((FragmentActivity) getContext()).getSupportFragmentManager(), "");
                    ((BusinessDetailsActivity) getContext()).writeReviewFromHeader(AddReviewOrPhoto.AddPhoto);
                    Utils.pushEvent(getContext(), GTMEventType.ADDPHOTOS_EVENT_CLICKED_EVENT, SharedPrefUtils.getUserDetailModel(getContext()).getDynamoId() + "", "");


                    break;
                case R.id.recentlyviewed:
                    Intent i = new Intent(_activity, RecentlyViewedActivity.class);
                    i.putExtra(Constants.FROM_EVENTS, fromEvents);
                    _activity.startActivity(i);
                    break;
                case R.id.direction:
                    _activity.moveToMap();
                    break;
                case R.id.transparentView:
                    Animation bottomUp1 = AnimationUtils.loadAnimation(getContext(),
                            R.anim.bottom_up);
                    bottomUp1.setAnimationListener(makeTopGone);
                    mDetailsLayout.startAnimation(bottomUp1);
                    // mDetailsLayout.setVisibility(View.GONE);
                    break;
                default:
                    break;
            }
        }
    }

    private void saveCalendarEvent(BusinessDataListing mBusinessInfoModel) {
        try {

            Uri EVENTS_URI = Uri.parse(DataUtils
                    .getCalendarUriBase((Activity) getContext()) + "events");
            ContentResolver cr = getContext().getContentResolver();

            // event insert
            ContentValues values = new ContentValues();
            values.put("calendar_id", 1);
            values.put("title", mBusinessInfoModel.getName());
            values.put("allDay", 0);
            ArrayList<Batches> batchedList = mBusinessInfoModel.getBatches();
            if (!batchedList.isEmpty() && batchedList.size() > 0) {

                long startDate = DateTimeUtils.parseExtendedDate(batchedList
                        .get(0).getStart_date_time());
                // String
                // startDate=DateTimeUtils.changeDateInddMMyyyy(batchedList.get(0).getStart_date_time());
                if (!StringUtils.isNullOrEmpty("" + startDate)) {
                    values.put("dtstart", startDate);
                }
                long endDate = DateTimeUtils.parseExtendedDate(batchedList.get(
                        0).getEnd_date_time());
                // String
                // endDate=DateTimeUtils.changeDateInddMMyyyy(batchedList.get(0).getEnd_date_time());
                if (!StringUtils.isNullOrEmpty("" + endDate)) {
                    values.put("dtend", endDate);
                }
            } else {
                long startDate = DateTimeUtils
                        .parseExtendedDate(mBusinessInfoModel.getEvent_date()
                                .getStart_date());
                long endDate = DateTimeUtils
                        .parseExtendedDate(mBusinessInfoModel.getEvent_date()
                                .getEnd_date());
                // String
                // startDate=DateTimeUtils.changeDate(mBusinessInfoModel.getEvent_date().getStart_date());
                // String
                // endDate=DateTimeUtils.changeDate(mBusinessInfoModel.getEvent_date().getEnd_date());
                if (!StringUtils.isNullOrEmpty("" + startDate)) {
                    values.put("dtstart", startDate);

                }
                if (!StringUtils.isNullOrEmpty("" + endDate)) {
                    values.put("dtend", endDate);

                }
            }

            // values.put("description", "Reminder description");
            // values.put("visibility", 0);
            values.put("hasAlarm", 1);
            values.put("eventTimezone", TimeZone.getDefault().getID());
            Uri event = cr.insert(EVENTS_URI, values);
            int id = Integer.parseInt(event.getLastPathSegment());
            if (id > 0) {
                Toast.makeText(getContext(),
                        "Calendar event created successfully",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(getContext(), "Something went wrong with calendar",
                    Toast.LENGTH_LONG).show();
            return;
        }
    }

    final AnimationListener makeTopGone = new AnimationListener() {

        @Override
        public void onAnimationStart(Animation animation) {
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            Log.d("View Pager", "onAnimationEnd - makeTopGone");
            mDetailsLayout.setVisibility(View.GONE);

        }
    };

    public void goToLoginDialog() {
        Bundle args = new Bundle();
        args.putInt(Constants.CATEGORY_ID, mCategoryId);
        args.putInt(Constants.PAGE_TYPE, mEventOrBusiness);
        args.putString(Constants.BUSINESS_OR_EVENT_ID, detailsResponse.getId());
        args.putString(Constants.DISTANCE, mDistance);
        LoginFragmentDialog fragmentDialog = new LoginFragmentDialog();
        fragmentDialog.setArguments(args);
        fragmentDialog.show(((FragmentActivity) getContext()).getSupportFragmentManager(), "");
    }

}
