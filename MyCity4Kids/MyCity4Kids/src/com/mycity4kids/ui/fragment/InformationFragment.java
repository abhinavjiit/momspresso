package com.mycity4kids.ui.fragment;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseFragment;
import com.kelltontech.utils.DataUtils;
import com.kelltontech.utils.DateTimeUtils;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.dbtable.UserTable;
import com.mycity4kids.enums.AddReviewOrPhoto;
import com.mycity4kids.fragmentdialog.CameraFragmentDialog;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.interfaces.IOnSubmitGallery;
import com.mycity4kids.models.businesseventdetails.Batches;
import com.mycity4kids.models.businesseventdetails.EventDate;
import com.mycity4kids.models.businesseventdetails.Facalities;
import com.mycity4kids.models.businesseventdetails.Timings;
import com.mycity4kids.models.businesslist.BusinessDataListing;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.ui.activity.BookOrPayWebActivity;
import com.mycity4kids.ui.activity.BusinessDetailsActivity;
import com.mycity4kids.ui.activity.WriteReviewActivity;

import org.apache.commons.lang3.text.StrBuilder;

import java.io.File;
import java.text.DateFormatSymbols;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class InformationFragment extends BaseFragment implements OnClickListener {

    private BusinessDataListing mBusinessInfoModel;
    private int mEventOrBusiness;
    private int mCategoryId;
    private File photo;
    private String mDistance;
    View view;
    boolean isbusiness = false;
    LinearLayout ifBatchAvl;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Utils.pushOpenScreenEvent(getActivity(), "Info resource/events", SharedPrefUtils.getUserDetailModel(getActivity()).getDynamoId() + "");

        try {
            mBusinessInfoModel = getArguments().getParcelable("BusinessInfo");
            isbusiness = getArguments().getBoolean("isbusiness");
            mEventOrBusiness = getArguments().getInt(Constants.PAGE_TYPE);
            mCategoryId = getArguments().getInt(Constants.CATEGORY_ID);
            mDistance = getArguments().getString(Constants.DISTANCE);
        } catch (Exception e) {
            // nothing to do here
        }
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT);
      /*if(isbusiness)
        view = inflater.inflate(R.layout.fragment_info_business, null, false);
       else*/
        view = inflater.inflate(R.layout.fragment_info, null, false);

        ifBatchAvl = (LinearLayout) view.findViewById(R.id.ifBatch_avl);
        ifBatchAvl.setVisibility(View.GONE);
        try {

            Log.d("check", "mBusinessInfoModel " + mBusinessInfoModel);
            if (mBusinessInfoModel == null) {
                view.findViewById(R.id.scroll).setVisibility(View.GONE);
                return view;
            }
            if (StringUtils.isNullOrEmpty(mBusinessInfoModel.getPhone())) {
                params.weight = 0.50f;
                view.findViewById(R.id.txv_btn_call).setEnabled(false);
                view.findViewById(R.id.txv_btn_call).setVisibility(View.GONE);
                view.findViewById(R.id.calldivider).setVisibility(View.GONE);

            } else {
                params.weight = 0.33f;
                view.findViewById(R.id.calldivider).setVisibility(View.VISIBLE);
                view.findViewById(R.id.txv_btn_call).setVisibility(View.VISIBLE);
                view.findViewById(R.id.txv_btn_call).setLayoutParams(params);
                view.findViewById(R.id.txv_btn_call).setOnClickListener(this);

            }

            view.findViewById(R.id.book_now_btn).setOnClickListener(this);
            //    if(mBusinessInfoModel.getEcommerce()){
            if (mBusinessInfoModel.getEcommerce().equalsIgnoreCase("yes")) {
                //TODO
                view.findViewById(R.id.book_now_btn).setVisibility(View.VISIBLE);
                //       view.findViewById(R.id.book_now_btn).setVisibility(View.GONE);
            } else {
                view.findViewById(R.id.book_now_btn).setVisibility(View.GONE);
            }

            if (mEventOrBusiness == Constants.BUSINESS_PAGE_TYPE) {
                view.findViewById(R.id.txv_btn_add_photo).setVisibility(View.VISIBLE);
                view.findViewById(R.id.txv_btn_add_review).setVisibility(View.VISIBLE);
                view.findViewById(R.id.txv_btn_add_calender).setVisibility(View.GONE);
                view.findViewById(R.id.txv_btn_invite_frnd).setVisibility(View.GONE);
                view.findViewById(R.id.txv_btn_add_photo).setLayoutParams(params);
                view.findViewById(R.id.txv_btn_add_review).setLayoutParams(params);
                view.findViewById(R.id.txv_btn_add_photo).setOnClickListener(this);
                view.findViewById(R.id.txv_btn_add_review).setOnClickListener(this);
                ((TextView) view.findViewById(R.id.book_now_btn)).setText("Pay Fees");
            } else if (mEventOrBusiness == Constants.EVENT_PAGE_TYPE) {
                view.findViewById(R.id.txv_btn_add_photo).setVisibility(View.GONE);
                view.findViewById(R.id.txv_btn_add_review).setVisibility(View.GONE);
                view.findViewById(R.id.txv_btn_add_calender).setVisibility(View.VISIBLE);
                view.findViewById(R.id.txv_btn_invite_frnd).setVisibility(View.VISIBLE);
                view.findViewById(R.id.txv_btn_add_calender).setLayoutParams(params);
                view.findViewById(R.id.txv_btn_invite_frnd).setLayoutParams(params);
                view.findViewById(R.id.txv_btn_add_calender).setOnClickListener(this);
                view.findViewById(R.id.txv_btn_invite_frnd).setOnClickListener(this);

                ((TextView) view.findViewById(R.id.book_now_btn)).setText("Book Now");
            }


            RatingBar _ratingBar = (RatingBar) view.findViewById(R.id.ratingBar);

            _ratingBar.setRating(mBusinessInfoModel.getRating());

            TextView eventDateOrSubAddress = (TextView) view.findViewById(R.id.sub_address_txt);


            if (StringUtils.isNullOrEmpty(mBusinessInfoModel.getSubaddress())) {
                if (mEventOrBusiness == Constants.EVENT_PAGE_TYPE) {
                    eventDateOrSubAddress.setVisibility(View.VISIBLE);

                } else {
                    eventDateOrSubAddress.setVisibility(View.GONE);
                }

            } else {
                if (mEventOrBusiness == Constants.EVENT_PAGE_TYPE && mBusinessInfoModel.getEvent_date() != null) {

                    eventDateOrSubAddress.setVisibility(View.VISIBLE);
                    eventDateOrSubAddress.setTextColor(Color.parseColor("#316ed5"));
                    eventDateOrSubAddress.setTypeface(null, Typeface.BOLD);

                    String[] startDateValues = getDateValues(mBusinessInfoModel.getEvent_date().getStart_date());
                    String[] endDateValues = getDateValues(mBusinessInfoModel.getEvent_date().getEnd_date());

                    Calendar cal = Calendar.getInstance();
                    cal.setTime(DateTimeUtils.stringToDate(mBusinessInfoModel.getEvent_date().getStart_date()));
                    int startmonth = cal.get(Calendar.MONTH);
                    int startDay = cal.get(Calendar.DAY_OF_MONTH);
                    int startYear = cal.get(Calendar.YEAR);
                    cal.setTime(DateTimeUtils.stringToDate(mBusinessInfoModel.getEvent_date().getEnd_date()));
                    int endMonth = cal.get(Calendar.MONTH);
                    int endtDay = cal.get(Calendar.DAY_OF_MONTH);
                    int endYear = cal.get(Calendar.YEAR);
                    // String startDate=mBusinessInfoModel.getEvent_date().getStart_date();
                    // String endDate=mBusinessInfoModel.getEvent_date().getEnd_date();
                    // if(isDatesEqual(startDate, endDate))

                    if (!StringUtils.isNullOrEmpty(mBusinessInfoModel.getEvent_date().getStart_date())) {
                        String startDate = mBusinessInfoModel.getEvent_date().getStart_date();
                        String endDate = mBusinessInfoModel.getEvent_date().getEnd_date();
                        /**
                         *
                         * according to CR no need to check eqality for years.
                         */
                        if (startmonth == endMonth && startDay == endtDay) {
                            eventDateOrSubAddress.setText(startDateValues[0] + " " + startDateValues[1]);
                        } else if (startmonth == endMonth && startDay != endtDay) {
                            eventDateOrSubAddress.setText(startDateValues[0] + " - " + endDateValues[0] + " " + endDateValues[1]);
                        } else {
                            eventDateOrSubAddress.setText(startDateValues[0] + " " + startDateValues[1] + " - " + endDateValues[0] + " " + endDateValues[1]);
                        }
                        // String date=DateTimeUtils.changeDate(mBusinessInfoModel.getEvent_date().getStart_date());
                  /*  if(StringUtils.isNullOrEmpty(endDate)){
                        _subAddress.setText(startDate);
                     }else{
                        _subAddress.setText(startDate+"-"+endDate);
                     }*/

                    } else {
                        eventDateOrSubAddress.setVisibility(View.GONE);
                    }

                } else {
                    /**
                     * now according to CR no need to shown sub address in case of bussiness:
                     * so i am going to make just visibility gone
                     */

                    eventDateOrSubAddress.setVisibility(View.GONE);
                    eventDateOrSubAddress.setTextColor(getResources().getColor(R.color.common_grey_1));
                    eventDateOrSubAddress.setTypeface(null, Typeface.NORMAL);
                    eventDateOrSubAddress.setText(mBusinessInfoModel.getSubaddress());
                }

            }
            TextView _locality = (TextView) view.findViewById(R.id.locality_txt);
            TextView _address = (TextView) view.findViewById(R.id.address_txt);
            if (mEventOrBusiness == Constants.BUSINESS_PAGE_TYPE) {
                if (StringUtils.isNullOrEmpty(mBusinessInfoModel.getAddress())) {
                    _address.setVisibility(View.GONE);
                } else {
                    _address.setVisibility(View.VISIBLE);
                    _address.setPadding(0, 2, 0, 0);
                    _address.setLineSpacing(0f, 1.6f);
                    _address.setText(mBusinessInfoModel.getAddress());
                }
                if (StringUtils.isNullOrEmpty(mBusinessInfoModel.getLocality())) {
                    _locality.setVisibility(View.GONE);
                } else {
                    _locality.setPadding(0, 6, 0, 0);
                    _locality.setVisibility(View.VISIBLE);
                    _locality.setText(mBusinessInfoModel.getLocality());
                }
            } else if (mEventOrBusiness == Constants.EVENT_PAGE_TYPE) {
                /**
                 * First we were showing this age group from startagegroup & endagegroup field .
                 * but now according to CR :- we are showing age Group from agegroup field;
                 */
            /*if( ((StringUtils.isNullOrEmpty(mBusinessInfoModel.getStartagegroup())) && (StringUtils.isNullOrEmpty(mBusinessInfoModel.getEndagegroup())))) {
            _address.setVisibility(View.GONE);
         } else {
            _address.setVisibility(View.VISIBLE);

            if(mBusinessInfoModel.getEndagegroup().contains("+")){
               _address.setText(Math.round(Float.parseFloat(mBusinessInfoModel.getStartagegroup()))+" "+mBusinessInfoModel.getEndagegroup());

            }else if(!mBusinessInfoModel.getStartagegroup().contains("+") && !mBusinessInfoModel.getEndagegroup().contains("+")){
               _address.setText(Math.round(Float.parseFloat(mBusinessInfoModel.getStartagegroup()))+"-"+Math.round(Float.parseFloat(mBusinessInfoModel.getEndagegroup())));

            }

         }*/

            /*if (StringUtils.isNullOrEmpty(mBusinessInfoModel.getAge_group()))  {
            _address.setVisibility(View.GONE);
         } else {
            _address.setVisibility(View.VISIBLE);
            _address.setText(mBusinessInfoModel.getAge_group());

         }*/

                /**
                 * Again CR.
                 */
                if (StringUtils.isNullOrEmpty(mBusinessInfoModel.getAgegroup_text())) {
                    _address.setVisibility(View.GONE);
                } else {
                    _address.setVisibility(View.VISIBLE);
                    _address.setText(mBusinessInfoModel.getAgegroup_text());

                }


                if (StringUtils.isNullOrEmpty(mBusinessInfoModel.getLocality())) {
                    _locality.setVisibility(View.GONE);
                } else {
                    _locality.setPadding(0, 8, 0, 0);
                    _locality.setVisibility(View.VISIBLE);
                    _locality.setText(mBusinessInfoModel.getLocality());
                }
            }

            TextView _likeConunt = (TextView) view.findViewById(R.id.like_txt);
            if (StringUtils.isNullOrEmpty(mBusinessInfoModel.getFaviorite_count())) {
                _likeConunt.setVisibility(View.GONE);
            } else {
                _likeConunt.setVisibility(View.VISIBLE);
                _likeConunt.setText(mBusinessInfoModel.getFaviorite_count());
            }

            //Distance:-
            /**
             * again this is in working
             */

            TextView distance = (TextView) view.findViewById(R.id.locality_distance_txt);
            //    if(mDistance!=null)
            if (!StringUtils.isNullOrEmpty(mDistance) && !mDistance.equals("") && !mDistance.equals("0") && !mDistance.equals("0.0")) {
                double diff = Double.parseDouble(mDistance);
                DecimalFormat df = new DecimalFormat("####0.0");
                mDistance = df.format(diff);
                distance.setText(mDistance + " Km");
            } else {
                distance.setVisibility(View.GONE);
            }
            /**
             * CR; - first they are not giving from server side
             * but now they are giving so above is comment & this is currently working
             */
         /*if(!StringUtils.isNullOrEmpty(mBusinessInfoModel.getDistance()) && !mBusinessInfoModel.getDistance().equals("0"))
      {
            double diff=Double.parseDouble(mBusinessInfoModel.getDistance());
            DecimalFormat df = new DecimalFormat("####0.0");
            String distanceAccuracy=df.format(diff);
            distance.setText(distanceAccuracy+ " Km");
      }else{
         distance.setVisibility(View.GONE);
      }*/

            /**
             * according to ui -
             */
         /*Facalities facilitiesModel = mBusinessInfoModel.getFacilities();
      if( facilitiesModel == null ) {
         facilitiesModel = new Facalities();
      }*/

            //Organised By:-
            TextView organisedBy = (TextView) view.findViewById(R.id.organised_by_desc);
            if (StringUtils.isNullOrEmpty(mBusinessInfoModel.getOrganised_by())) {
                //       view.findViewById(R.id.view_divider_organised).setVisibility(View.GONE);
                view.findViewById(R.id.organised_by_txt).setVisibility(View.GONE);
                view.findViewById(R.id.img1).setVisibility(View.GONE);
                organisedBy.setVisibility(View.GONE);
            } else {
                organisedBy.setText(mBusinessInfoModel.getOrganised_by());
            }

            LinearLayout parentBatchLout = (LinearLayout) view.findViewById(R.id.batches_lout);

            ArrayList<Batches> batchList = mBusinessInfoModel.getBatches();

            //Batches:-
            if (batchList != null && !batchList.isEmpty()) {

                view.findViewById(R.id.orgview).setVisibility(View.GONE);
                view.findViewById(R.id.dateview).setVisibility(View.GONE);
                view.findViewById(R.id.desview).setVisibility(View.GONE);
                ifBatchAvl.setVisibility(View.VISIBLE);

                if (!StringUtils.isNullOrEmpty(mBusinessInfoModel.getOrganised_by())) {
                    ((TextView) view.findViewById(R.id.batch_org_value)).setText(mBusinessInfoModel.getOrganised_by());
                    view.findViewById(R.id.org_batch).setVisibility(View.VISIBLE);
                } else {
                    view.findViewById(R.id.separator_btwn_orgDesc).setVisibility(View.GONE);
                    view.findViewById(R.id.org_batch).setVisibility(View.GONE);
                }
                if (!StringUtils.isNullOrEmpty(mBusinessInfoModel.getContact().getEmail()) || !StringUtils.isNullOrEmpty(mBusinessInfoModel.getContact().getContact_no())) {

                    StrBuilder contact = new StrBuilder();
                    if (!StringUtils.isNullOrEmpty(mBusinessInfoModel.getContact().getContact_no())) {
                        contact.append(mBusinessInfoModel.getContact().getContact_no().toString()).append(" /n ");
                    }
                    if (!StringUtils.isNullOrEmpty(mBusinessInfoModel.getContact().getEmail())) {
                        contact.append(mBusinessInfoModel.getContact().getEmail().toString());
                    }
                    ((TextView) view.findViewById(R.id.batch_contact_value)).setText(contact.toString());
                    view.findViewById(R.id.contact_batch).setVisibility(View.VISIBLE);
                } else {
                    view.findViewById(R.id.separator_btwn_descCont).setVisibility(View.GONE);
                    view.findViewById(R.id.contact_batch).setVisibility(View.GONE);
                }
                if (!StringUtils.isNullOrEmpty(mBusinessInfoModel.getDescription())) {
                    ((TextView) view.findViewById(R.id.batch_desc_value)).setText(mBusinessInfoModel.getDescription());
                    view.findViewById(R.id.desc_batch).setVisibility(View.VISIBLE);
                } else {
                    view.findViewById(R.id.desc_batch).setVisibility(View.GONE);
                }


                for (int i = 0; i < batchList.size(); i++) {
                 /* view.findViewById(R.id.activitiesview).setVisibility(View.GONE);
         view.findViewById(R.id.img2).setVisibility(View.GONE);
         view.findViewById(R.id.ageview).setVisibility(View.GONE);
         view.findViewById(R.id.img3).setVisibility(View.GONE);
         view.findViewById(R.id.dateview).setVisibility(View.GONE);
         view.findViewById(R.id.img4).setVisibility(View.GONE);
         view.findViewById(R.id.timeview).setVisibility(View.GONE);
         view.findViewById(R.id.img5).setVisibility(View.GONE);
         view.findViewById(R.id.costview).setVisibility(View.GONE);
         view.findViewById(R.id.img6).setVisibility(View.GONE);*/
                    /**
                     * we are removing all divider line in case of batches;
                     */

                    // view.findViewById(R.id.view_divider_3).setVisibility(View.GONE);
                    // view.findViewById(R.id.view_divider_4).setVisibility(View.GONE);
                    // view.findViewById(R.id.view_divider_cost).setVisibility(View.GONE);
                    // view.findViewById(R.id.view_divider_description).setVisibility(View.GONE);
                    // view.findViewById(R.id.view_divider_contact).setVisibility(View.GONE);
                    // view.findViewById(R.id.view_divider_1).setVisibility(View.VISIBLE);
                    final ViewHolder holder = new ViewHolder();

                    RelativeLayout _batches = (RelativeLayout) inflater.inflate(R.layout.custom_batch_cell, null);
                    TextView batchName = (TextView) _batches.findViewById(R.id.batch_txt);
                    CheckedTextView plus_minus = (CheckedTextView) _batches.findViewById(R.id.plus_minus_img);
                    LinearLayout childLout = (LinearLayout) _batches.findViewById(R.id.childLout);
                    RelativeLayout groupLout = (RelativeLayout) _batches.findViewById(R.id.group_lout);
                    //          View view_divider=(View)_batches.findViewById(R.id.view_divider);
                    holder.parentLayout = groupLout;
                    holder.textView = plus_minus;
                    holder.childLayout = childLout;
                    //          holder.view=view_divider;
                    holder.mBatches = batchList.get(i);
                    groupLout.setTag(holder);
                    groupLout.setOnClickListener(this);
                    if (!StringUtils.isNullOrEmpty(batchList.get(i).getBatchname())) {
                        batchName.setText(batchList.get(i).getBatchname());
                    } else {
                        batchName.setText("Batch");
                    }
                    parentBatchLout.addView(_batches);
                }
            } else {
                //view.findViewById(R.id.view_divider_organised).setVisibility(View.GONE);
            }


            // Activities:-
            TextView activity = (TextView) view.findViewById(R.id.activitiest_desc);
            Log.d("check", "activity check " + mBusinessInfoModel.getActivities());
            if (StringUtils.isNullOrEmpty(mBusinessInfoModel.getActivities())) {
                Log.d("check", "activity check in if " + mBusinessInfoModel.getActivities());
                // view.findViewById(R.id.view_divider_1).setVisibility(View.GONE);
                view.findViewById(R.id.activities_txt).setVisibility(View.GONE);
                view.findViewById(R.id.activitiesview).setVisibility(View.GONE);
                view.findViewById(R.id.img2).setVisibility(View.GONE);
                activity.setVisibility(View.GONE);
            } else {
                Log.d("check", "activity check in else " + mBusinessInfoModel.getActivities());
                activity.setText(mBusinessInfoModel.getActivities());
            }

            // Age Group


            ArrayList<Batches> batchedList = mBusinessInfoModel.getBatches();
            if ((batchedList != null && batchedList.size() > 0 && mEventOrBusiness == Constants.EVENT_PAGE_TYPE) || mEventOrBusiness == Constants.BUSINESS_PAGE_TYPE) {
                TextView txvAgeGroupDesc = (TextView) view.findViewById(R.id.age_group_desc);
                if (StringUtils.isNullOrEmpty(mBusinessInfoModel.getAge_group())) {
                    // view.findViewById(R.id.view_divider_2).setVisibility(View.GONE);
                    view.findViewById(R.id.age_group_txt).setVisibility(View.GONE);
                    view.findViewById(R.id.ageview).setVisibility(View.GONE);
                    view.findViewById(R.id.img3).setVisibility(View.GONE);
                    txvAgeGroupDesc.setVisibility(View.GONE);
                } else {
                    view.findViewById(R.id.age_group_txt).setVisibility(View.VISIBLE);
                    view.findViewById(R.id.ageview).setVisibility(View.VISIBLE);
                    view.findViewById(R.id.img3).setVisibility(View.VISIBLE);
                    txvAgeGroupDesc.setVisibility(View.VISIBLE);
                    txvAgeGroupDesc.setText(mBusinessInfoModel.getAge_group());
                }
            } else {
                view.findViewById(R.id.ageview).setVisibility(View.GONE);
                view.findViewById(R.id.img3).setVisibility(View.GONE);
                view.findViewById(R.id.age_group_txt).setVisibility(View.GONE);
                view.findViewById(R.id.age_group_desc).setVisibility(View.GONE);
            }
//
            String orgduration1 = "";
            Calendar cal = Calendar.getInstance();
            cal.setTime(DateTimeUtils.stringToDate(mBusinessInfoModel.getEvent_date().getStart_date()));
            Calendar cal2 = Calendar.getInstance();
            cal2.setTime(DateTimeUtils.stringToDate(mBusinessInfoModel.getEvent_date().getEnd_date()));
            long diff = cal2.getTimeInMillis() - cal.getTimeInMillis();

            //
            long diffSeconds = diff / 1000 % 60;
            long diffMinutes = diff / (60 * 1000) % 60;
            long diffHours = diff / (60 * 60 * 1000) % 24;
            long diffDays = diff / (24 * 60 * 60 * 1000);

            if (diffDays == 0) {
                if (diffHours == 0) {
                    orgduration1 = String.valueOf(diffMinutes) + " Minutes";
                } else {
                    orgduration1 = String.valueOf(diffHours) + " Hour";
                }
            } else {
                if (diffDays > 30) {
                    long mn = diffDays / 30;
                    orgduration1 = String.valueOf(mn) + " Month";
                } else {
                    orgduration1 = String.valueOf(diffDays) + " Day";
                }
            }
            //
            int startmonth = cal.get(Calendar.MONTH);
            int startDay = cal.get(Calendar.DAY_OF_MONTH);
            int startYear = cal.get(Calendar.YEAR);
            int weekday = cal.get(Calendar.DAY_OF_WEEK);
         /*Log.d("check","startmonth "+startmonth);
         Log.d("check","startDay "+startDay);
         Log.d("check","startYear "+startYear);
         Log.d("check","weekday "+weekday);
         Log.d("check","string mnth "+formatMonth(startmonth, Locale.getDefault()).substring(0,3));
         Log.d("check","string mnth "+getDayOfWeekAsString(weekday));
      */
      /* tvdate.setText(startDay+" "+formatMonth(startmonth,Locale.getDefault()).substring(0,3)+" "+startYear+","+getDayOfWeekAsString(weekday));
         TextView tvtime = (TextView)view.findViewById(R.id.timings_desc1);
         tvtime.setText(mBusinessInfoModel.getTimings().getStart_time()+"-"+mBusinessInfoModel.getTimings().getEnd_time());*/

            //

            //Event Date:
            /**
             * no need to display it in bottom according to CR. SO for this time i am just make visibily gone:
             * may be in future it can be use according to next CR
             */
            EventDate eventDates = mBusinessInfoModel.getEvent_date();
            String eventDatesStr = "";
            if (eventDates != null) {
                if (!StringUtils.isNullOrEmpty(eventDates.getStart_date())) {
                    eventDatesStr += "Start Date: " + eventDates.getStart_date() + "\n";
                }
                if (!StringUtils.isNullOrEmpty(eventDates.getEnd_date())) {
                    eventDatesStr += "End Date: " + eventDates.getEnd_date();
                }
            }
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, EEE");

            TextView txvEventDatesDesc = (TextView) view.findViewById(R.id.event_dates_desc);
            if (StringUtils.isNullOrEmpty(eventDatesStr)) {
                //view.findViewById(R.id.view_divider_3).setVisibility(View.GONE);
                view.findViewById(R.id.event_dates_txt).setVisibility(View.GONE);
                view.findViewById(R.id.img4).setVisibility(View.GONE);
                view.findViewById(R.id.dateview).setVisibility(View.GONE);
                txvEventDatesDesc.setVisibility(View.GONE);
            } else {
                view.findViewById(R.id.event_dates_txt).setVisibility(View.VISIBLE);
                txvEventDatesDesc.setVisibility(View.VISIBLE);
                view.findViewById(R.id.img4).setVisibility(View.VISIBLE);
//                txvEventDatesDesc.setText(startDay + " " + formatMonth(startmonth, Locale.getDefault()).substring(0, 3) + " " + startYear + "," + getDayOfWeekAsString(weekday));
                txvEventDatesDesc.setText(sdf.format(cal.getTime()) + " To " + sdf.format(cal2.getTime()));
            }

            // Timings:
            Timings timings = mBusinessInfoModel.getTimings();
            String timingsStr = "";
            if (timings != null) {
                if (!StringUtils.isNullOrEmpty(timings.getStart_time())) {
                    timingsStr += "Start Time: " + timings.getStart_time() + "\n";
                }
                if (!StringUtils.isNullOrEmpty(timings.getEnd_time())) {
                    timingsStr += "End Time: " + timings.getEnd_time();
                }
            }
            TextView txvTimingsDesc = (TextView) view.findViewById(R.id.timings_desc);
            if (StringUtils.isNullOrEmpty(timingsStr)) {
                view.findViewById(R.id.timings_txt).setVisibility(View.GONE);
                view.findViewById(R.id.timeview).setVisibility(View.GONE);
                view.findViewById(R.id.img5).setVisibility(View.GONE);
                txvTimingsDesc.setVisibility(View.GONE);
            } else {
                txvTimingsDesc.setText(mBusinessInfoModel.getTimings().getStart_time() + " - " + mBusinessInfoModel.getTimings().getEnd_time());
            }

            // set value in businesslist
            ((BusinessDetailsActivity) getActivity()).setDate(txvEventDatesDesc.getText().toString(), txvTimingsDesc.getText().toString());
            //Cost:-


            TextView cost = (TextView) view.findViewById(R.id.cost_desc);

            if (StringUtils.isNullOrEmpty(mBusinessInfoModel.getCost())) {
                // view.findViewById(R.id.view_divider_cost).setVisibility(View.GONE);
                view.findViewById(R.id.cost_txt).setVisibility(View.GONE);
                view.findViewById(R.id.costview).setVisibility(View.GONE);
                view.findViewById(R.id.img6).setVisibility(View.GONE);
                cost.setVisibility(View.GONE);
            } else {
                cost.setText("Rs." + mBusinessInfoModel.getCost());
            }

            //Duration:-
            TextView duration = (TextView) view.findViewById(R.id.duration_value);

            if (StringUtils.isNullOrEmpty(mBusinessInfoModel.getDuration())) {
                view.findViewById(R.id.durationview).setVisibility(View.GONE);
                view.findViewById(R.id.img10).setVisibility(View.GONE);
                duration.setVisibility(View.GONE);
            } else {
                duration.setText(mBusinessInfoModel.getDuration());
            }


            //agegroup
            TextView txvAgeGroupDesc = (TextView) view.findViewById(R.id.age_group_desc);
            if (StringUtils.isNullOrEmpty(mBusinessInfoModel.getAge_group())) {
                view.findViewById(R.id.age_group_txt).setVisibility(View.GONE);
                view.findViewById(R.id.ageview).setVisibility(View.GONE);
                view.findViewById(R.id.img3).setVisibility(View.GONE);
                txvAgeGroupDesc.setVisibility(View.GONE);
            } else {
                view.findViewById(R.id.age_group_txt).setVisibility(View.VISIBLE);
                view.findViewById(R.id.ageview).setVisibility(View.VISIBLE);
                view.findViewById(R.id.img3).setVisibility(View.VISIBLE);
                txvAgeGroupDesc.setVisibility(View.VISIBLE);
                txvAgeGroupDesc.setText(mBusinessInfoModel.getAge_group());
            }
            //Description:-

            TextView description = (TextView) view.findViewById(R.id.description_desc);

            if (StringUtils.isNullOrEmpty(mBusinessInfoModel.getDescription())) {
                //view.findViewById(R.id.view_divider_description).setVisibility(View.GONE);
                view.findViewById(R.id.description_txt).setVisibility(View.GONE);
                view.findViewById(R.id.desview).setVisibility(View.GONE);
                view.findViewById(R.id.img7).setVisibility(View.GONE);
                description.setVisibility(View.GONE);
            } else {
                description.setText(mBusinessInfoModel.getDescription());
            }

            //ABOUT:----
            TextView about = (TextView) view.findViewById(R.id.about_desc);

            if (StringUtils.isNullOrEmpty(mBusinessInfoModel.getAbout())) {
                //view.findViewById(R.id.view_divider_about).setVisibility(View.GONE);
                view.findViewById(R.id.about_txt).setVisibility(View.GONE);
                view.findViewById(R.id.aboutview).setVisibility(View.GONE);
                view.findViewById(R.id.img8).setVisibility(View.GONE);
                about.setVisibility(View.GONE);
            } else {
                about.setText(mBusinessInfoModel.getAbout());
            }


            //Contact:-

            TextView contact = (TextView) view.findViewById(R.id.contact_desc);
            contact.setOnClickListener(this);

            if (StringUtils.isNullOrEmpty(mBusinessInfoModel.getContact().getContact_no())) {
                // view.findViewById(R.id.view_divider_contact).setVisibility(View.GONE);
                view.findViewById(R.id.contact_txt).setVisibility(View.GONE);
                view.findViewById(R.id.conview).setVisibility(View.GONE);
                view.findViewById(R.id.img9).setVisibility(View.GONE);
                contact.setVisibility(View.GONE);
            } else {
                contact.setText(mBusinessInfoModel.getContact().getContact_no() + " \n " + mBusinessInfoModel.getContact().getEmail());
            }

            //Contact Person:
            TextView contactPerson = (TextView) view.findViewById(R.id.contact_person_desc);
            if (StringUtils.isNullOrEmpty(mBusinessInfoModel.getContact().getContact_person())) {
                contactPerson.setVisibility(View.GONE);
            } else {
                contactPerson.setText(mBusinessInfoModel.getContact().getContact_person() + "\n" + mBusinessInfoModel.getContact().getEmail());
            }


            //Email:-

            TextView emailId = (TextView) view.findViewById(R.id.email_desc);

            if (StringUtils.isNullOrEmpty(mBusinessInfoModel.getContact().getEmail())) {
                emailId.setVisibility(View.GONE);
            } else {
                emailId.setVisibility(View.GONE);
                //emailId.setText(mBusinessInfoModel.getContact().getEmail());
            }


            //Website:-
            //Changes required as per clien feedback
            TextView website = (TextView) view.findViewById(R.id.website_desc);
            if (StringUtils.isNullOrEmpty(mBusinessInfoModel.getContact().getWebsite())) {
                website.setVisibility(View.GONE);
            } else {
                website.setVisibility(View.GONE);
                website.setText(mBusinessInfoModel.getContact().getWebsite());
            }


            LinearLayout additionalInfoLout = (LinearLayout) view.findViewById(R.id.additional_lout);

            // facilitiesModel - additional information
            List<Facalities> addInfosList = mBusinessInfoModel.getFacilities();
            /**
             * it's hide
             */
            // TextView txvAdditionalInfoDesc = (TextView)view.findViewById(R.id.additional_info_desc);
            if (addInfosList == null || addInfosList.isEmpty()) {
                view.findViewById(R.id.facility_txt).setVisibility(View.GONE);
            } else {

                view.findViewById(R.id.facility_txt).setVisibility(View.VISIBLE);
                for (Facalities info : addInfosList) {
                    /**
                     * CR:- this line hide
                     */
                    // additionalInfoDesc += info.getInfo_key() + ": " + info.getInfo_value() + "\n";
                    LinearLayout lout = (LinearLayout) inflater.inflate(R.layout.custom_info_fragment, null);


                    TextView _commonTxv = (TextView) lout.findViewById(R.id.common_txt);
                    TextView _commonTxvDesc = (TextView) lout.findViewById(R.id.commont_txt_desc);
                    if (!StringUtils.isNullOrEmpty(info.getInfo_key()) && !StringUtils.isNullOrEmpty(info.getInfo_value())) {
                        _commonTxv.setText(info.getInfo_key());
                        _commonTxvDesc.setText(info.getInfo_value().trim());
                        additionalInfoLout.addView(lout);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return view;
    }

    private void saveCalendarEvent(BusinessDataListing mBusinessInfoModel) {
        try {


            Uri EVENTS_URI = Uri.parse(DataUtils.getCalendarUriBase(getActivity()) + "events");
            ContentResolver cr = getActivity().getContentResolver();

            // event insert
            ContentValues values = new ContentValues();
            values.put("calendar_id", 1);
            values.put("title", mBusinessInfoModel.getName());
            values.put("allDay", 0);
            ArrayList<Batches> batchedList = mBusinessInfoModel.getBatches();
            if (!batchedList.isEmpty() && batchedList.size() > 0) {

                long startDate = DateTimeUtils.parseExtendedDate(batchedList.get(0).getStart_date_time());
                //String startDate=DateTimeUtils.changeDateInddMMyyyy(batchedList.get(0).getStart_date_time());
                if (!StringUtils.isNullOrEmpty("" + startDate)) {
                    values.put("dtstart", startDate);
                }
                long endDate = DateTimeUtils.parseExtendedDate(batchedList.get(0).getEnd_date_time());
                //String endDate=DateTimeUtils.changeDateInddMMyyyy(batchedList.get(0).getEnd_date_time());
                if (!StringUtils.isNullOrEmpty("" + endDate)) {
                    values.put("dtend", endDate);
                }
            } else {
                long startDate = DateTimeUtils.parseExtendedDate(mBusinessInfoModel.getEvent_date().getStart_date());
                long endDate = DateTimeUtils.parseExtendedDate(mBusinessInfoModel.getEvent_date().getEnd_date());
                //String startDate=DateTimeUtils.changeDate(mBusinessInfoModel.getEvent_date().getStart_date());
                //String endDate=DateTimeUtils.changeDate(mBusinessInfoModel.getEvent_date().getEnd_date());
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
                Toast.makeText(getActivity(), "Calendar event created successfully", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(getActivity(), "Something went wrong with calendar", Toast.LENGTH_LONG).show();
            return;
        }
    }


   /*@Override
   public void onActivityResult(int requestCode, int resultCode, Intent data) {
      super.onActivityResult(requestCode, resultCode, data);

      switch (requestCode) {
      case Constants.TAKE_PICTURE:
         if (resultCode == Activity.RESULT_OK) {
            try {
               int maxImageSize = BitmapUtils.getMaxSize(getActivity());
               Bitmap sourceBitmap = BitmapUtils.getScaledBitmap(photo, maxImageSize);

               ExifInterface exif = new ExifInterface(photo.getPath());
               int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                     ExifInterface.ORIENTATION_NORMAL);
               // Log.e(TAG, "oreination" + orientation);
               Matrix matrix = new Matrix();
               switch (orientation) {
               case ExifInterface.ORIENTATION_ROTATE_90:
                  matrix.postRotate(90);
                  break;
               case ExifInterface.ORIENTATION_ROTATE_180:
                  matrix.postRotate(180);
                  break;
               case ExifInterface.ORIENTATION_ROTATE_270:
                  matrix.postRotate(270);
                  break;
               }

               Bitmap originalImage = Bitmap.createBitmap(sourceBitmap, 0, 0, sourceBitmap.getWidth(),
                     sourceBitmap.getHeight(), matrix, true);
    *//**
     * add photo on server side-deepanker Chaudahry
     *//*
               ((BusinessDetailsActivity) getActivity()).sendUploadBusinessImageRequest(originalImage);
            } catch (Exception ex) {
               ex.printStackTrace();
            }
         }
         break;
      case Constants.OPEN_GALLERY:
         if (resultCode == Activity.RESULT_OK) {
            try {
               Uri selectedImage = data.getData();
               String[] filePathColumn = {
                     MediaStore.Images.Media.DATA
               };

               Cursor cursor = getActivity().getContentResolver().query(selectedImage, filePathColumn, null, null, null);
               cursor.moveToFirst();

               int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
               String filePath = cursor.getString(columnIndex);
               cursor.close();
               Log.e("File", "filePath: " + filePath);

               File file = new File(new URI("file://" + filePath));
               int maxImageSize = BitmapUtils.getMaxSize(getActivity());
               Bitmap sourceBitmap = BitmapUtils.getScaledBitmap(file, maxImageSize);


               ExifInterface exif = new ExifInterface(file.getPath());
               int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                     ExifInterface.ORIENTATION_NORMAL);
               // Log.e(TAG, "oreination" + orientation);
               Matrix matrix = new Matrix();
               switch (orientation) {
               case ExifInterface.ORIENTATION_ROTATE_90:
                  matrix.postRotate(90);
                  break;
               case ExifInterface.ORIENTATION_ROTATE_180:
                  matrix.postRotate(180);
                  break;
               case ExifInterface.ORIENTATION_ROTATE_270:
                  matrix.postRotate(270);
                  break;
               }

               Bitmap originalImage = Bitmap.createBitmap(sourceBitmap, 0, 0, sourceBitmap.getWidth(),
                     sourceBitmap.getHeight(), matrix, true);
               ((BusinessDetailsActivity) getActivity()).sendUploadBusinessImageRequest(originalImage);
            } catch (Exception e) {
               e.printStackTrace();
            }
         }
         break;
      }
   }*/

    /**
     * It's an expandable list View With Layouts - Deepanker Chaudhary
     */
    private void removeAndAddViews(View view) {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        RelativeLayout relativeView = (RelativeLayout) view;
        ViewHolder holder = (ViewHolder) relativeView.getTag();
        Batches batches = holder.mBatches;
        LinearLayout childLout = holder.childLayout;
        CheckedTextView checkTextView = holder.textView;
        //View viewDivider=holder.view;
        checkTextView.setChecked(true);
        LinearLayout internalChildLayout = null;
        TextView childTxt = null;
        TextView headerTxt = null;
        //		View viewDividerBottom=null;
        if (childLout.getChildCount() > 0) {
            childLout.removeAllViews();
            checkTextView.setChecked(false);
            //	viewDivider.setVisibility(View.VISIBLE);
            return;
        }
        int i = 0;
        if (batches != null) {
            String activity = batches.getActivitiesnames();
            if (!StringUtils.isNullOrEmpty(activity)) {
                i++;
                internalChildLayout = (LinearLayout) inflater.inflate(R.layout.custom_child_batch, null);
                childTxt = (TextView) internalChildLayout.findViewById(R.id.common_txt_desc);
                headerTxt = (TextView) internalChildLayout.findViewById(R.id.common_txt);
                headerTxt.setText("ACTIVITIES");
                if (i == 1) {
                    //					viewDivider.setVisibility(View.GONE);
                }/*else{
                    viewDivider.setVisibility(View.VISIBLE);
				}*/
                childTxt.setText(activity);

                headerTxt.setCompoundDrawablesWithIntrinsicBounds(R.drawable.activity_xxhdpi, 0, 0, 0);
                childLout.addView(internalChildLayout);
            }
            //	internalChildLayout.removeAllViews();

            String startAgeGroup = batches.getAgegroup();
            //String endAgeGroup=batches.getEndagegroup();
            if (!StringUtils.isNullOrEmpty(startAgeGroup)) {

                internalChildLayout = (LinearLayout) inflater.inflate(R.layout.custom_child_batch, null);
                childTxt = (TextView) internalChildLayout.findViewById(R.id.common_txt_desc);
                headerTxt = (TextView) internalChildLayout.findViewById(R.id.common_txt);
                headerTxt.setText("AGE GROUP");
                headerTxt.setCompoundDrawablesWithIntrinsicBounds(R.drawable.age_group_xxhdpi, 0, 0, 0);
                //childTxt.setText(Math.round(Float.parseFloat(batches.getStartagegroup()))+"-"+Math.round(Float.parseFloat(batches.getEndagegroup())));
                childTxt.setText(startAgeGroup);

                childLout.addView(internalChildLayout);
            }
            String date = batches.getDate();

            if (!StringUtils.isNullOrEmpty(date)) {

				/*String startDate =DateTimeUtils.getSeperateDate(startDateTime);
                String startTime =DateTimeUtils.getSeperateTime(startDateTime);
				String endDate =DateTimeUtils.getSeperateDate(endDateTime);
				String endTime =DateTimeUtils.getSeperateTime(endDateTime);*/


                internalChildLayout = (LinearLayout) inflater.inflate(R.layout.custom_child_batch, null);


                childTxt = (TextView) internalChildLayout.findViewById(R.id.common_txt_desc);
                headerTxt = (TextView) internalChildLayout.findViewById(R.id.common_txt);
                headerTxt.setText("DATES");
                headerTxt.setCompoundDrawablesWithIntrinsicBounds(R.drawable.date_xxhdpi, 0, 0, 0);
                childTxt.setText(date);
                childLout.addView(internalChildLayout);
            }

            String timings = batches.getTimings();
            if (!StringUtils.isNullOrEmpty(timings)) {


                internalChildLayout = (LinearLayout) inflater.inflate(R.layout.custom_child_batch, null);
                if (StringUtils.isNullOrEmpty(batches.getCost())) {
                    //						viewDividerBottom=(View)internalChildLayout.findViewById(R.id.view_divider_bottom);
                    //						viewDividerBottom.setVisibility(View.VISIBLE);
                }
                childTxt = (TextView) internalChildLayout.findViewById(R.id.common_txt_desc);
                headerTxt = (TextView) internalChildLayout.findViewById(R.id.common_txt);
                headerTxt.setText("TIME");
                headerTxt.setCompoundDrawablesWithIntrinsicBounds(R.drawable.time_xxhdpi, 0, 0, 0);
                childTxt.setText(timings);
                childLout.addView(internalChildLayout);
            }


            if (!StringUtils.isNullOrEmpty(batches.getCost())) {
                i++;
                if (i == 1) {
                    //					viewDivider.setVisibility(View.GONE);
                }/*else{
                    viewDivider.setVisibility(View.VISIBLE);
				}*/
                internalChildLayout = (LinearLayout) inflater.inflate(R.layout.custom_child_batch, null);
                childTxt = (TextView) internalChildLayout.findViewById(R.id.common_txt_desc);
                headerTxt = (TextView) internalChildLayout.findViewById(R.id.common_txt);
                headerTxt.setText("COST");
                headerTxt.setCompoundDrawablesWithIntrinsicBounds(R.drawable.cost_xxhdpi, 0, 0, 0);
                //				viewDividerBottom=(View)internalChildLayout.findViewById(R.id.view_divider_bottom);
                //				viewDividerBottom.setVisibility(View.VISIBLE);
                childTxt.setText(batches.getCost());
                childLout.addView(internalChildLayout);
            }


        }
    }

    public class ViewHolder {
        public RelativeLayout parentLayout;
        public CheckedTextView textView;
        public LinearLayout childLayout;
        public Batches mBatches;
        public View view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txv_btn_call: {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + mBusinessInfoModel.getPhone()));
                startActivity(intent);
                break;
            }
            case R.id.book_now_btn: {
                UserTable _table = new UserTable((BaseApplication) getActivity().getApplicationContext());
                int count = _table.getCount();
                if (count <= 0) {
                    ((BusinessDetailsActivity) getActivity()).mDetailsHeader.goToLoginDialog();
                    return;
                }

                startActivity(new Intent(getActivity(), BookOrPayWebActivity.class).putExtra(Constants.WEB_VIEW_ECOMMERECE, mBusinessInfoModel.getEcommerce_url()));
                break;
            }
            case R.id.txv_btn_add_photo: {
                UserTable _table = new UserTable((BaseApplication) getActivity().getApplicationContext());
                int count = _table.getCount();
                if (count <= 0) {
                    ((BusinessDetailsActivity) getActivity()).mDetailsHeader.goToLoginDialog();
                    return;
                }
                CameraFragmentDialog fragmentDialog = new CameraFragmentDialog();
                fragmentDialog.setSubmitListner((IOnSubmitGallery) getActivity());
                fragmentDialog.show(getActivity().getSupportFragmentManager(), "");
                ((BusinessDetailsActivity) getActivity()).writeReviewFromHeader(AddReviewOrPhoto.AddPhoto);
                break;
            }

            case R.id.txv_btn_add_review: {
                UserTable _table = new UserTable((BaseApplication) getActivity().getApplicationContext());
                int count = _table.getCount();
                if (count <= 0) {
                    ((BusinessDetailsActivity) getActivity()).mDetailsHeader.goToLoginDialog();
                    return;
                }

                Intent intent = new Intent(getActivity(), WriteReviewActivity.class);
                intent.putExtra(Constants.PAGE_TYPE, mEventOrBusiness);
                intent.putExtra(Constants.CATEGORY_ID, mCategoryId);
                intent.putExtra(Constants.BUSINESS_OR_EVENT_ID, mBusinessInfoModel.getId());
                startActivity(intent);
                break;
            }
            case R.id.txv_btn_add_calender: {
                saveCalendarEvent(mBusinessInfoModel);
                // Toast.makeText(getActivity(), "Under Implementation", Toast.LENGTH_LONG).show();
                break;
            }
            case R.id.txv_btn_invite_frnd: {
                Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                String titleName = mBusinessInfoModel.getName();
                if (StringUtils.isNullOrEmpty(titleName)) {
                    titleName = "";
                }

                String webUrl = mBusinessInfoModel.getWeb_url();
                if (StringUtils.isNullOrEmpty(webUrl)) {
                    webUrl = "";
                }


                shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, titleName);

                String shareMessage = "I have just discovered " + titleName + " in Momspresso app. Check it out " + webUrl;
                shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareMessage);
                getActivity().startActivity(Intent.createChooser(shareIntent, "Momspresso"));
                break;
            }
            case R.id.group_lout: {

                removeAndAddViews(v);

                // Toast.makeText(getActivity(), "Under Implementation", Toast.LENGTH_LONG).show();
                break;
            }
            case R.id.contact_desc: {
                String contactNo = mBusinessInfoModel.getContact().getContact_no().split("/")[0];
                if (!StringUtils.isNullOrEmpty(contactNo)) {
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + contactNo));
                    startActivity(intent);
                }
                // Toast.makeText(getActivity(), "Under Implementation", Toast.LENGTH_LONG).show();
                break;
            }

        }
    }

   /* @Override
   public void setOnSubmitListner(String type) {
      if(type.equals(Constants.ALBUM_TYPE)){
         openGallery();
      }else if(type.endsWith(Constants.GALLERY_TYPE)){
         takePhoto();
      }
   }*/
   /*private void openGallery() {
      Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
      photoPickerIntent.setType("image/*");
      startActivityForResult(photoPickerIntent, Constants.OPEN_GALLERY);
   }

   private void takePhoto() {
      Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
      photo = new File(Environment.getExternalStorageDirectory(), "Pic.jpg");
      intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo));
      startActivityForResult(intent, Constants.TAKE_PICTURE);
   }*/

    /**
     * ADD A PHOTO to Server:
     *
     * @param
     *//*
   private void sendUploadBusinessImageRequest(Bitmap originalImage) {
      ((BusinessDetailsActivity)getActivity()).showProgressDialog(getResources().getString(R.string.please_wait));
      ByteArrayOutputStream bao = new ByteArrayOutputStream();
      originalImage.compress(Bitmap.CompressFormat.PNG, 100, bao);
      byte [] ba = bao.toByteArray();
      String imageString=Base64.encodeToString(ba,Base64.DEFAULT);

      JSONObject jsonObject = new JSONObject();
      try {
         jsonObject.put("extension", "image/png");
         jsonObject.put("size", ba.length);
         jsonObject.put("byteCode", imageString);
      } catch (JSONException e) {
         e.printStackTrace();
      }

      JSONArray jsonArray = new JSONArray();
      jsonArray.put(jsonObject);

      BusinessImageUploadRequest requestData = new BusinessImageUploadRequest();
      requestData.setImage(jsonArray.toString());

      if(mEventOrBusiness==Constants.BUSINESS_PAGE_TYPE){
         requestData.setType("business");
      }else if(mEventOrBusiness==Constants.EVENT_PAGE_TYPE){
         requestData.setType("event");
      }
      requestData.setBusinessId(mBusinessInfoModel.getId());

      UserTable userTable = new UserTable((BaseApplication)getActivity().getApplication());
      int count=userTable.getCount();
      if(count<=0){
         ((BusinessDetailsActivity)getActivity()).removeProgressDialog();
         ((BusinessDetailsActivity)getActivity()).showToast(getResources().getString(R.string.user_login));
         return;
      }
      UserModel userModel = userTable.getAllUserData();
      requestData.setUserId(""+userModel.getUser().getId());
      requestData.setSessionId(userModel.getUser().getSessionId());

      ImageUploadController controller = new ImageUploadController(getActivity(), (IScreen)getActivity());
      controller.getData(AppConstants.UPLOAD_BUSINESS_IMAGE_REQUEST, requestData);
   }*/
    private String[] getDateValues(String date) {
        String[] datesValue = null;
        try {
            String formatedDate = DateTimeUtils.changeDate(date);

            datesValue = formatedDate.split(" ");
         /*DateFormat format = new SimpleDateFormat("dd MMM yyyy",Locale.US);
         cal.setTime(format.parse(formatedDate));*/
        } catch (Exception e) {
            return null;
        }

        return datesValue;

    }

    private boolean isDatesEqual(String dateOne, String dateTwo) {

        Calendar cal = Calendar.getInstance();
        cal.setTime(DateTimeUtils.stringToDate(mBusinessInfoModel.getEvent_date().getStart_date()));
        int startmonth = cal.get(Calendar.MONTH);
        int startDay = cal.get(Calendar.DAY_OF_MONTH);
        int startYear = cal.get(Calendar.YEAR);
        cal.setTime(DateTimeUtils.stringToDate(mBusinessInfoModel.getEvent_date().getEnd_date()));
        int endMonth = cal.get(Calendar.MONTH);
        int endtDay = cal.get(Calendar.DAY_OF_MONTH);
        int endYear = cal.get(Calendar.YEAR);
        return true;
    }

    @Override
    protected void updateUi(Response response) {
        // TODO Auto-generated method stub

    }
}