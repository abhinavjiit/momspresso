package com.mycity4kids.ui.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewConfiguration;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.OverScroller;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.kelltontech.ui.IScreen;
import com.kelltontech.utils.BitmapUtils;
import com.kelltontech.utils.ConnectivityUtils;
import com.kelltontech.utils.DataUtils;
import com.kelltontech.utils.DateTimeUtils;
import com.kelltontech.utils.StringUtils;
import com.kelltontech.utils.ToastUtils;
import com.mycity4kids.BuildConfig;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.controller.BookmarkController;
import com.mycity4kids.controller.BusinessAndEventDetailsController;
import com.mycity4kids.controller.FavoriteAndBeenThereController;
import com.mycity4kids.controller.ImageUploadController;
import com.mycity4kids.dbtable.UserTable;
import com.mycity4kids.enums.AddReviewOrPhoto;
import com.mycity4kids.fragmentdialog.CameraFragmentDialog;
import com.mycity4kids.fragmentdialog.LoginFragmentDialog;
import com.mycity4kids.gtmutils.GTMEventType;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.interfaces.IOnSubmitGallery;
import com.mycity4kids.models.bookmark.BookmarkModel;
import com.mycity4kids.models.businesseventdetails.Batches;
import com.mycity4kids.models.businesseventdetails.DetailMap;
import com.mycity4kids.models.businesseventdetails.DetailsRequest;
import com.mycity4kids.models.businesseventdetails.DetailsResponse;
import com.mycity4kids.models.businesseventdetails.DetailsReviews;
import com.mycity4kids.models.businesslist.BusinessDataListing;
import com.mycity4kids.models.favorite.FavoriteRequest;
import com.mycity4kids.models.forgot.CommonResponse;
import com.mycity4kids.models.parentingdetails.ParentingDetailResponse;
import com.mycity4kids.models.user.BusinessImageUploadRequest;
import com.mycity4kids.models.user.UserInfo;
import com.mycity4kids.models.user.UserModel;
import com.mycity4kids.observablescrollview.ObservableScrollViewCallbacks;
import com.mycity4kids.observablescrollview.ScrollState;
import com.mycity4kids.observablescrollview.ScrollUtils;
import com.mycity4kids.observablescrollview.Scrollable;
import com.mycity4kids.observablescrollview.TouchInterceptionFrameLayout;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.ArticlePublishAPI;
import com.mycity4kids.retrofitAPIsInterfaces.ResourcesAPI;
import com.mycity4kids.slidingtab.SlidingTabLayout;
import com.mycity4kids.ui.adapter.ViewPagerAdapterEventdetail;
import com.mycity4kids.ui.fragment.MapFragment;
import com.mycity4kids.widget.DetailHeader;
import com.nineoldandroids.view.ViewHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.URI;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * @author Deepanker Chaudhary
 */
public class BusinessDetailsActivity extends BaseActivity implements OnClickListener, IOnSubmitGallery, ObservableScrollViewCallbacks {

    boolean flag = true;
    SlidingTabLayout tabs;
    CharSequence Titles[] = {"INFO", "GALLERY", "REVIEWS", "VENUE"};
    int Numboftabs = 4;
    //	private TabHost mTabHost;
    private ViewPager mViewPager;
    private ViewPagerAdapterEventdetail mDetailsFragmentAdapter;
    public DetailHeader mDetailsHeader;
    private String[] TAB_ARRAY = {"INFO", "GALLERY", "REVIEWS", "VENUE"};
    private String[] TAB_ARRAY_BUSINESS = {"INFO", "GALLERY", "REVIEWS", "MAP"};
    private int mEventOrBusiness;
    private int categoryId;
    private File photo;
    private AddReviewOrPhoto writeAReviewFromHeader;
    private String businessOrEventId;
    private String distance;
    Toolbar toolbar;
    boolean isbusiness = false;
    // Observable Scrollview constants
    private static final float MAX_TEXT_SCALE_DELTA = 0.3f;
    private static final int INVALID_POINTER = -1;

    private View mImageView;
    private View mOverlayView;
    private TextView mTitleView;
    private TouchInterceptionFrameLayout mInterceptionLayout;
    //    private ViewPager mPager;
//    private NavigationAdapter mPagerAdapter;
    private VelocityTracker mVelocityTracker;
    private OverScroller mScroller;
    private float mBaseTranslationY;
    private int mMaximumVelocity;
    private int mActivePointerId = INVALID_POINTER;
    private int mSlop;
    private int mFlexibleSpaceHeight;
    private int mTabHeight;
    private boolean mScrolled;
    private int mTopHeaderHeight;
    private int bookmarkStatus;
    private View mBelowTitleView;
    private TextView titleMain;
    private float density;
    TextView txvcall, txvshare, txvbooknow, txvaddtocal, txvaddress, txvdistance;
    ImageView locationIcon;
    BusinessDataListing information;
    private String businessId;
    private TextView ratingHeader;
    private float ratingVal = 0;
    private String shareDate = "";
    private String shareTime = "";
    private String deepLinkURL;
    private GoogleApiClient mClient;
    private String TAG;
    private ImageView imgBookmark;
//    private String screenTitle = "Business Detail";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.pushOpenScreenEvent(BusinessDetailsActivity.this, "Resource/Event Details", SharedPrefUtils.getUserDetailModel(this).getId() + "");

        TAG = BusinessDetailsActivity.this.getClass().getSimpleName();
        mClient = new GoogleApiClient.Builder(this).addApi(AppIndex.APP_INDEX_API).build();

        Bundle bundle = getIntent().getExtras();
        deepLinkURL = getIntent().getStringExtra(Constants.DEEPLINK_URL);
        mEventOrBusiness = bundle.getInt(Constants.PAGE_TYPE);
        if (mEventOrBusiness == Constants.BUSINESS_PAGE_TYPE) {
            setContentView(R.layout.business_details_activity);
            imgBookmark = (ImageView) findViewById(R.id.img_favourites);
            imgBookmark.setOnClickListener(this);
        } else if (mEventOrBusiness == Constants.EVENT_PAGE_TYPE) {
            setContentView(R.layout.event_details_activity);
        }

        density = getResources().getDisplayMetrics().density;
        ((ImageView) findViewById(R.id.img_round)).setOnClickListener(this);
        txvaddress = (TextView) findViewById(R.id.description);
        ratingHeader = (TextView) findViewById(R.id.ratingHeader);
        txvdistance = (TextView) findViewById(R.id.distance);
        txvcall = (TextView) findViewById(R.id.txvCall);
        txvshare = (TextView) findViewById(R.id.txvShare);
        txvbooknow = (TextView) findViewById(R.id.txvBookNow);
        txvaddtocal = (TextView) findViewById(R.id.txvAddToCal);
        locationIcon = (ImageView) findViewById(R.id.img);
        txvcall.setOnClickListener(this);
        txvshare.setOnClickListener(this);
        txvbooknow.setOnClickListener(this);
        txvaddtocal.setOnClickListener(this);

        //mTabHost=(TabHost)findViewById(android.R.id.tabhost);

        mViewPager = (ViewPager) findViewById(R.id.pager);

        setUpObservableScroll();

        ((ImageView) findViewById(R.id.imgBack)).setOnClickListener(this);
        //((ImageView)findViewById(R.id.imgSearch)).setOnClickListener(this);


        if (bundle != null) {
            UserTable _table = new UserTable((BaseApplication) getApplicationContext());
            categoryId = bundle.getInt(Constants.CATEGORY_ID);
            isbusiness = bundle.getBoolean("isbusiness");
            businessId = bundle.getString(Constants.BUSINESS_OR_EVENT_ID);
            mEventOrBusiness = bundle.getInt(Constants.PAGE_TYPE);
            distance = bundle.getString(Constants.DISTANCE);
            BusinessAndEventDetailsController _controller = new BusinessAndEventDetailsController(this, this);
            DetailsRequest _requestModel = new DetailsRequest();
            _requestModel.setBusinessOrEventId(businessId);
            _requestModel.setCategoryId("" + categoryId);
            if (mEventOrBusiness == Constants.BUSINESS_PAGE_TYPE) {
                _requestModel.setType("business");
//                bookmarkStatus = getIntent().getIntExtra(Constants.RESOURCE_BOOKMARK_STATUS, 0);
//                if (bookmarkStatus == 0)
//                    imgBookmark.setImageResource(R.drawable.ic_favorite_border_white_48dp);
//                else
//                    imgBookmark.setImageResource(R.drawable.ic_favorite_border_white_48dp_fill);

            } else if (mEventOrBusiness == Constants.EVENT_PAGE_TYPE) {
                _requestModel.setType("event");
            }
            if (_table.getUserId() > 0) {
                _requestModel.setUser_id("" + _table.getUserId());
            }
            showProgressDialog(getString(R.string.fetching_data));
            _controller.getData(AppConstants.BUSINESS_AND_EVENT_DETAILS_REQUEST, _requestModel);
        }
        mDetailsHeader = (DetailHeader) findViewById(R.id.detailsHeader);
        mDetailsHeader.inflateHeader(mEventOrBusiness);
        ((TextView) mDetailsHeader.findViewById(R.id.report_an_error)).setOnClickListener(this);
        mDetailsHeader.bringToFront();
        mDetailsHeader.requestFocus();

        // view adapter chnge listener
        findViewById(R.id.reviewBtn).setOnClickListener(this);
        findViewById(R.id.transitBtn).setOnClickListener(this);
        findViewById(R.id.directionBtn).setOnClickListener(this);


    }


    public void setDate(String date, String time) {
        shareDate = date;
        shareTime = time;


    }

    public void setRating(ArrayList<DetailsReviews> list) {
        DecimalFormat oneDForm = new DecimalFormat("#.#");
        try {
            float totrating = 0.0f;
            float avgrating = 0.0f;
            for (int i = 0; i < list.size(); i++) {
                totrating = totrating + list.get(i).getRatingcount();
            }
            avgrating = totrating / list.size();
            ratingVal = avgrating;
            if (mEventOrBusiness == Constants.BUSINESS_PAGE_TYPE) {
                if (avgrating > 0) {
                    findViewById(R.id.ratingLayout).setVisibility(View.VISIBLE);
                    ratingHeader.setText("" + oneDForm.format(avgrating));
                } else
                    findViewById(R.id.ratingLayout).setVisibility(View.INVISIBLE);
            }
        } catch (Exception e)

        {
            e.printStackTrace();
        }

    }

    public void showBottomIcons(boolean isReview, boolean map) {

        if (isReview) {
            findViewById(R.id.reviewBtn).setVisibility(View.VISIBLE);
            findViewById(R.id.filterlayout).setVisibility(View.GONE);
        } else if (map) {
            findViewById(R.id.filterlayout).setVisibility(View.VISIBLE);
            findViewById(R.id.reviewBtn).setVisibility(View.GONE);
        } else {
            findViewById(R.id.reviewBtn).setVisibility(View.GONE);
            findViewById(R.id.filterlayout).setVisibility(View.GONE);
        }
    }

    private void setUpObservableScroll() {
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        ViewCompat.setElevation(findViewById(R.id.header), getResources().getDimension(R.dimen.toolbar_elevation));
//        mPagerAdapter = new NavigationAdapter(getSupportFragmentManager());
//        mPager = (ViewPager) findViewById(R.id.pager);
//        mPager.setAdapter(mPagerAdapter);
        mImageView = findViewById(R.id.image);
        mOverlayView = findViewById(R.id.overlay);
        RelativeLayout rltRootHeader = (RelativeLayout) findViewById(R.id.rltRootHeader);
        rltRootHeader.bringToFront();
        titleMain = (TextView) findViewById(R.id.titleMain);
        // Padding for ViewPager must be set outside the ViewPager itself
        // because with padding, EdgeEffect of ViewPager become strange.
        mFlexibleSpaceHeight = getResources().getDimensionPixelSize(R.dimen.flexible_space_relativelayout_height);
        mTopHeaderHeight = getResources().getDimensionPixelSize(R.dimen.flexible_space_image_height);
        mTabHeight = getResources().getDimensionPixelSize(R.dimen.tab_height);
        findViewById(R.id.pager_wrapper).setPadding(0, mTopHeaderHeight, 0, 0);
        mTitleView = (TextView) findViewById(R.id.title);
//        mTitleView.setText("This is very long lng text, which atleast comes in two or three lines.. depending on header title received from API response");
        mBelowTitleView = findViewById(R.id.description);
        setTitle(null);

        // set rating
        if (mEventOrBusiness == Constants.BUSINESS_PAGE_TYPE) {
            if (ratingVal > 0)
                ratingHeader.setText("" + ratingVal);
        }
        SlidingTabLayout slidingTabLayout = (SlidingTabLayout) findViewById(R.id.tabs);
//        slidingTabLayout.setCustomTabView(R.layout.tab_indicator, android.R.id.text1);
//        slidingTabLayout.setSelectedIndicatorColors(getResources().getColor(R.color.accent));
//        slidingTabLayout.setDistributeEvenly(true);
//        slidingTabLayout.setViewPager(mPager);
        ((FrameLayout.LayoutParams) slidingTabLayout.getLayoutParams()).topMargin = mTopHeaderHeight - mTabHeight;

        ViewConfiguration vc = ViewConfiguration.get(this);
        mSlop = vc.getScaledTouchSlop();
        mMaximumVelocity = vc.getScaledMaximumFlingVelocity();
        mInterceptionLayout = (TouchInterceptionFrameLayout) findViewById(R.id.container);
        mInterceptionLayout.setScrollInterceptionListener(mInterceptionListener);
        mScroller = new OverScroller(getApplicationContext());
        ScrollUtils.addOnGlobalLayoutListener(mInterceptionLayout, new Runnable() {
            @Override
            public void run() {
                // Extra space is required to move mInterceptionLayout when it's scrolled.
                // It's better to adjust its height when it's laid out
                // than to adjust the height when scroll events (onMoveMotionEvent) occur
                // because it causes lagging.
                // See #87: https://github.com/ksoichiro/Android-ObservableScrollView/issues/87
                FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mInterceptionLayout.getLayoutParams();
                lp.height = getScreenHeight() + mFlexibleSpaceHeight;
                mInterceptionLayout.requestLayout();

                updateFlexibleSpace();
            }
        });

        slidingTabLayout.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                switch (position) {
                    case 0:
                        showBottomIcons(false, false);
                        break;
                    case 1:
                        showBottomIcons(false, false);
                        break;
                    case 2:
                        // review
                        showBottomIcons(true, false);
                        break;
                    case 3:
                        showBottomIcons(false, true);
                        break;

                }

            }

            @Override
            public void onPageSelected(int position) {

                switch (position) {
                    case 0:
                        showBottomIcons(false, false);
                        break;
                    case 1:
                        showBottomIcons(false, false);
                        break;
                    case 2:
                        // review
                        showBottomIcons(true, false);
                        break;
                    case 3:
                        showBottomIcons(false, true);
                        break;


                }


            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!StringUtils.isNullOrEmpty(deepLinkURL) && AppConstants.BASE_URL.equalsIgnoreCase("http://webserve.mycity4kids.com/")) {
            // Connect client
            mClient.connect();
            final String TITLE = mEventOrBusiness == Constants.BUSINESS_PAGE_TYPE ? "Resource Detail" : "Event Detail";
            final Uri APP_URI = AppConstants.APP_BASE_URI.buildUpon().appendPath(deepLinkURL).build();
            final Uri WEB_URL = AppConstants.WEB_BASE_URL.buildUpon().appendPath(deepLinkURL).build();
            Action viewAction = Action.newAction(Action.TYPE_VIEW, TITLE, WEB_URL, APP_URI);
            PendingResult<Status> result = AppIndex.AppIndexApi.start(mClient, viewAction);

            result.setResultCallback(new ResultCallback<Status>() {
                @Override
                public void onResult(Status status) {
                    if (status.isSuccess()) {
                        Log.d(TAG, APP_URI.toString() + " App Indexing API: The screen view started" +
                                " successfully.");
                    } else {
                        Log.e(TAG, APP_URI.toString() + " App Indexing API: There was an error " +
                                "recording the screen ." + status.toString());
                    }
                }
            });
        }
    }

    @Override
    protected void onStop() {
        if (!StringUtils.isNullOrEmpty(deepLinkURL) && AppConstants.BASE_URL.equalsIgnoreCase("http://webserve.mycity4kids.com/")) {
            final String TITLE = mEventOrBusiness == Constants.BUSINESS_PAGE_TYPE ? "Resource Detail" : "Event Detail";
            final Uri APP_URI = AppConstants.APP_BASE_URI.buildUpon().appendPath(deepLinkURL).build();
            final Uri WEB_URL = AppConstants.WEB_BASE_URL.buildUpon().appendPath(deepLinkURL).build();
            Action viewAction = Action.newAction(Action.TYPE_VIEW, TITLE, WEB_URL, APP_URI);
            PendingResult<Status> result = AppIndex.AppIndexApi.end(mClient, viewAction);

            result.setResultCallback(new ResultCallback<Status>() {
                @Override
                public void onResult(Status status) {
                    if (status.isSuccess()) {
                        Log.d(TAG, APP_URI.toString() + " App Indexing API:  The screen view end " +
                                "successfully.");
                    } else {
                        Log.e(TAG, APP_URI.toString() + " App Indexing API: There was an error " +
                                "recording the screen." + status.toString());
                    }
                }
            });
            // Disconnecting the client
            mClient.disconnect();
        }
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        if (mDetailsHeader.getVisivility() == View.VISIBLE) {
            mDetailsHeader.hideView();
        } else {
            finish();
        }
    }


    public void writeReviewFromHeader(AddReviewOrPhoto writeareview) {
        writeAReviewFromHeader = writeareview;
        if (AddReviewOrPhoto.WriteAReview == writeareview) { //1 for write a review : its very urgent task so i will define name later for 1 & 0 : 1-writereview,0-addphoto
            Intent intent = new Intent(BusinessDetailsActivity.this, WriteReviewActivity.class);
            intent.putExtra(Constants.PAGE_TYPE, mEventOrBusiness);
            intent.putExtra(Constants.CATEGORY_ID, categoryId);
            intent.putExtra(Constants.BUSINESS_OR_EVENT_ID, businessOrEventId);
            startActivity(intent);
        }
    }

    @Override
    protected void updateUi(final Response response) {
        Log.d("check", "detail res in updateUi " + response);
        try {

            if (response == null) {
                showMessageAndFinish(getString(R.string.toast_response_error));
                return;
            }
            switch (response.getDataType()) {
                case AppConstants.BUSINESS_AND_EVENT_DETAILS_REQUEST: {
                    if (response.getResponseObject() instanceof DetailsResponse) {
                        Log.d("check", "in if ");
                        updateUiWithDetailsResponse((DetailsResponse) response.getResponseObject());
                    } else {
                        Log.d("check", "in else ");
                        showMessageAndFinish(getString(R.string.toast_response_error));
                    }
                    break;
                }
                case AppConstants.FAVORITE_REQUEST: {

                    if (response.getResponseObject() instanceof CommonResponse) {
                        CommonResponse responseModel = (CommonResponse) response.getResponseObject();
                        String message = responseModel.getResult().getMessage();
                        if (responseModel.getResponseCode() != 200) {
                            if (StringUtils.isNullOrEmpty(message)) {
                                showMessageAndFinish(getString(R.string.toast_response_error));
                            } else {
                                Toast.makeText(BusinessDetailsActivity.this, message, Toast.LENGTH_SHORT).show();
                            }

                            return;
                        } else {

                            if (StringUtils.isNullOrEmpty(message)) {
                                showToast("Details are successfully saved as favorite.");
                            } else {
                                Toast.makeText(BusinessDetailsActivity.this, message, Toast.LENGTH_SHORT).show();
                            }


                        }
                    }


                }
                break;
                case AppConstants.BEEN_THERE_REQUEST: {

                    if (response.getResponseObject() instanceof CommonResponse) {
                        CommonResponse responseModel = (CommonResponse) response.getResponseObject();
                        String message = responseModel.getResult().getMessage();
                        if (responseModel.getResponseCode() != 200) {
                            if (StringUtils.isNullOrEmpty(message)) {
                                showMessageAndFinish(getString(R.string.toast_response_error));
                            } else {
                                Toast.makeText(BusinessDetailsActivity.this, message, Toast.LENGTH_SHORT).show();
                            }
                            return;
                        } else {
                            if (StringUtils.isNullOrEmpty(message)) {
                                showToast("You have successfully been there.");
                            } else {
                                Toast.makeText(BusinessDetailsActivity.this, message, Toast.LENGTH_SHORT).show();
                            }

                        }
                    }


                }
                break;
                case AppConstants.UPLOAD_BUSINESS_IMAGE_REQUEST: {
                    if (response.getResponseObject() instanceof CommonResponse) {
                        CommonResponse responseModel = (CommonResponse) response.getResponseObject();
                        if (responseModel.getResponseCode() != 200) {
                            showMessageAndFinish(getString(R.string.toast_response_error));
                            return;
                        } else {
                            showToast("You have successfully uploaded image.");
                        }
                    }
                    break;
                }
            }
            removeProgressDialog();

        } catch (Exception e) {
            Log.d("check", "in e ");
            e.printStackTrace();
            showMessageAndFinish(getString(R.string.toast_response_error));
            removeProgressDialog();
        }
    }

    /**
     * @param pDetailsResponse
     */
    private void updateUiWithDetailsResponse(DetailsResponse pDetailsResponse) {
        if (pDetailsResponse.getResponseCode() != 200) {
            showMessageAndFinish(getString(R.string.toast_response_error));
            return;
        }
        try {
            if (mEventOrBusiness == Constants.BUSINESS_PAGE_TYPE) {
                if ("0".equals(pDetailsResponse.getResult().getData().getInfo().getBookmarkStatus())) {
                    imgBookmark.setImageResource(R.drawable.ic_favorite_border_white_48dp);
                    bookmarkStatus = 0;
                } else {
                    imgBookmark.setImageResource(R.drawable.ic_favorite_border_white_48dp_fill);
                    bookmarkStatus = 1;
                }
            }
            String name = pDetailsResponse.getResult().getData().getInfo().getName().trim();
            ((TextView) findViewById(R.id.titleMain)).setText(name.trim());
            ((TextView) findViewById(R.id.title)).setText(name.trim());
            locationIcon.setVisibility(View.VISIBLE);
            setRating(pDetailsResponse.getResult().getData().getReviews());
            if (!StringUtils.isNullOrEmpty(pDetailsResponse.getResult().getData().getInfo().getEcommerce())) {
                if (!pDetailsResponse.getResult().getData().getInfo().getEcommerce().trim().equalsIgnoreCase("no")) {
                    txvbooknow.setVisibility(View.VISIBLE);
                }
            }

            updateFlexibleSpace();
        } catch (Exception e) {
            // ignore
        }

        /**
         * passing data into details page : because it's required in some api :We can put up details click event in this activity
         * also but i am puting seperatly with details header. Deepanker
         */
        information = pDetailsResponse.getResult().getData().getInfo();
       /* if( StringUtils.isNullOrEmpty(information.getSubaddress()) ) {
            txvaddress.setVisibility(View.VISIBLE);*/


      /*  }else{
            txvaddress.setVisibility(View.GONE);
            }*/

/*if(information.getAddress()!=null)
{
    txvaddress.setText(information.getCity_name() + "(");
    txvdistance.setText(distance+"km)");
}*/
// if(information.getAddress()!=null)
//{
//    txvaddress.setText(information.getAddress() + "(");
//    txvdistance.setText(distance+"km)");
//}
//else if(information.getSubaddress()!=null)
//{
//    txvaddress.setText(information.getSubaddress() + "(");
//    txvdistance.setText(distance+"km)");
//}
//else
//{
//    txvaddress.setText("(");
//    txvdistance.setText(distance+"km)");
//}

        txvaddress.setText(information.getLocality() + " (");
        txvdistance.setText(distance + "km)");
        mDetailsHeader.setBusinessDetails(pDetailsResponse.getResult().getData().getInfo(), categoryId, mEventOrBusiness, distance);

        businessOrEventId = pDetailsResponse.getResult().getData().getInfo().getId();


        /**
         * this method use for favorite & been there api:
         */
        getFavoriteAndBeenThere(pDetailsResponse.getResult().getData().getInfo().getId());

        mDetailsFragmentAdapter = new ViewPagerAdapterEventdetail(BusinessDetailsActivity.this, pDetailsResponse, mEventOrBusiness, categoryId, distance, getSupportFragmentManager(), Titles, Numboftabs, isbusiness);
        //mTabHost.setup();
        //mTabHost.setOnTabChangedListener(mDetailsFragmentAdapter);
        mViewPager.setAdapter(mDetailsFragmentAdapter);
        // Assiging the Sliding Tab Layout View
        tabs = (SlidingTabLayout) findViewById(R.id.tabs);
        //tabs.setdis
        //tabs.setDistributeEvenly(true); // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width

        // Setting Custom Color for the Scroll bar indicator of the Tab View
        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.tabsScrollColor);
            }

            @Override
            public int getDividerColor(int position) {
                return 0;
            }
        });
        /*tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
			public int getIndicatorColor(int position) {
				return getResources().getColor(R.color.tabsScrollColor);
			}
		});*/

        // Setting the ViewPager For the SlidingTabsLayout
        tabs.setViewPager(mViewPager);
        //mViewPager.setOnPageChangeListener(mDetailsFragmentAdapter);
        //mViewPager.setOffscreenPageLimit(4);


        ArrayList<DetailsReviews> reviewList = pDetailsResponse.getResult().getData().getReviews();
        int countOfReviews = 0;
        if (reviewList != null) {
            countOfReviews = reviewList.size();
        }


        LayoutInflater inflater = LayoutInflater.from(BusinessDetailsActivity.this);
        for (int i = 0; i < TAB_ARRAY.length; i++) {
            LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.tab_design_with_circle, null);
            TextView circleTxtView = (TextView) layout.findViewById(R.id.tab_title_circle);
            if (countOfReviews > 0 && TAB_ARRAY[i].equalsIgnoreCase("reviews")) {
                //circleTxtView.setVisibility(View.VISIBLE);
                circleTxtView.setText("" + countOfReviews);
            } else {
                //circleTxtView.setVisibility(View.GONE);
            }
            if (mEventOrBusiness == Constants.BUSINESS_PAGE_TYPE) {
                ((TextView) layout.findViewById(R.id.tab_title)).setText(TAB_ARRAY_BUSINESS[i]);


            } else if (mEventOrBusiness == Constants.EVENT_PAGE_TYPE) {
                ((TextView) layout.findViewById(R.id.tab_title)).setText(TAB_ARRAY[i]);
            }

            //layout.setBackgroundResource(R.drawable.detail_tab_selector);
            //this.mTabHost.getTabWidget().setDividerDrawable(R.drawable.tab_border);
            //mDetailsFragmentAdapter.addTab(BusinessDetailsActivity.this.mTabHost.newTabSpec("Tab1").setIndicator(layout));
        }

        mViewPager.setAdapter(mDetailsFragmentAdapter);
    }


    private void getFavoriteAndBeenThere(String businessId) {
        UserTable _table = new UserTable((BaseApplication) this.getApplicationContext());
        /*if (_table.getCount() < 0) {
            return;
		}*/
        UserModel userData = _table.getAllUserData();
        UserInfo userInfo = userData != null ? userData.getUser() : null;
        /*if (userInfo == null || userInfo.getId() <= 0  || StringUtils.isNullOrEmpty(userInfo.getSessionId()) ) {

			return;
		}*/

        FavoriteRequest _request = new FavoriteRequest();
        if (userInfo != null) {
            _request.setUser_id("" + userInfo.getId());
            _request.setSessionId(userInfo.getSessionId());
        } else {
            _request.setUser_id("");
            _request.setSessionId("");
        }
        if (mEventOrBusiness == Constants.EVENT_PAGE_TYPE) {
            _request.setType("event");
        } else if (mEventOrBusiness == Constants.BUSINESS_PAGE_TYPE) {
            _request.setType("business");
        }
        _request.setId(businessId);

        mDetailsHeader.setRequestData(_request);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case Constants.TAKE_PICTURE:
                if (resultCode == Activity.RESULT_OK) {
                    try {
                        int maxImageSize = BitmapUtils.getMaxSize(this);
                        Bitmap sourceBitmap = BitmapUtils.getScaledBitmap(photo, maxImageSize);

                        ExifInterface exif = new ExifInterface(photo.getPath());
                        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                                ExifInterface.ORIENTATION_NORMAL);
                        //	Log.e(TAG, "oreination" + orientation);
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
                        if (writeAReviewFromHeader == AddReviewOrPhoto.AddPhoto) {
                            sendUploadBusinessImageRequest(originalImage);
                        } else if (writeAReviewFromHeader == AddReviewOrPhoto.WriteAReview) {
                            Intent intent = new Intent(this, WriteReviewActivity.class);
                            intent.putExtra(Constants.PAGE_TYPE, mEventOrBusiness);
                            intent.putExtra(Constants.CATEGORY_ID, categoryId);
                            intent.putExtra(Constants.BUSINESS_OR_EVENT_ID, businessOrEventId);
                            startActivity(intent);
                        }


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

                        Cursor cursor = this.getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                        cursor.moveToFirst();

                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        String filePath = cursor.getString(columnIndex);
                        cursor.close();
                        Log.e("File", "filePath: " + filePath);

                        File file = new File(new URI("file://" + filePath.replaceAll(" ", "%20")));
                        int maxImageSize = BitmapUtils.getMaxSize(this);
                        Bitmap sourceBitmap = BitmapUtils.getScaledBitmap(file, maxImageSize);


                        ExifInterface exif = new ExifInterface(file.getPath());
                        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                                ExifInterface.ORIENTATION_NORMAL);
                        //	Log.e(TAG, "oreination" + orientation);
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
                        if (writeAReviewFromHeader == AddReviewOrPhoto.AddPhoto) {
                            sendUploadBusinessImageRequest(originalImage);
                        } else if (writeAReviewFromHeader == AddReviewOrPhoto.WriteAReview) {
                            Intent intent = new Intent(this, WriteReviewActivity.class);
                            intent.putExtra(Constants.PAGE_TYPE, mEventOrBusiness);
                            intent.putExtra(Constants.CATEGORY_ID, categoryId);
                            intent.putExtra(Constants.BUSINESS_OR_EVENT_ID, businessOrEventId);
                            startActivity(intent);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    public void sendUploadBusinessImageRequest(Bitmap originalImage) {
        showProgressDialog(getResources().getString(R.string.please_wait));
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        originalImage.compress(Bitmap.CompressFormat.PNG, 100, bao);
        byte[] ba = bao.toByteArray();
        String imageString = Base64.encodeToString(ba, Base64.DEFAULT);

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

        if (mEventOrBusiness == Constants.BUSINESS_PAGE_TYPE) {
            requestData.setType("business");
        } else if (mEventOrBusiness == Constants.EVENT_PAGE_TYPE) {
            requestData.setType("event");
        }
        String businessId = getIntent().getExtras().getString(Constants.BUSINESS_OR_EVENT_ID);
        requestData.setBusinessId(businessId);

        UserTable userTable = new UserTable((BaseApplication) this.getApplication());
        int count = userTable.getCount();
        if (count <= 0) {
            removeProgressDialog();
            showToast(getResources().getString(R.string.user_login));
            return;
        }
        UserModel userModel = userTable.getAllUserData();
        requestData.setUserId("" + userModel.getUser().getId());
        requestData.setSessionId(userModel.getUser().getSessionId());

        ImageUploadController controller = new ImageUploadController(this, this);
        controller.getData(AppConstants.UPLOAD_BUSINESS_IMAGE_REQUEST, requestData);
    }

    private void saveCalendarEvent(BusinessDataListing mBusinessInfoModel) {
        try {


            Uri EVENTS_URI = Uri.parse(DataUtils.getCalendarUriBase(BusinessDetailsActivity.this) + "events");
            ContentResolver cr = BusinessDetailsActivity.this.getContentResolver();

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

            //	values.put("description", "Reminder description");
            //	values.put("visibility", 0);
            values.put("hasAlarm", 1);
            values.put("eventTimezone", TimeZone.getDefault().getID());
            Uri event = cr.insert(EVENTS_URI, values);
            int id = Integer.parseInt(event.getLastPathSegment());
            if (id > 0) {
                Toast.makeText(BusinessDetailsActivity.this, "Calendar event created successfully", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(BusinessDetailsActivity.this, "Something went wrong with calendar", Toast.LENGTH_LONG).show();
            return;
        }
    }


    @Override
    public void onClick(View v) {
        UserTable _table = new UserTable((BaseApplication) getApplicationContext());
        int count = _table.getCount();

        FavoriteAndBeenThereController _controller = new FavoriteAndBeenThereController(BusinessDetailsActivity.this, (IScreen) BusinessDetailsActivity.this);

        if (mEventOrBusiness == Constants.BUSINESS_PAGE_TYPE) {
            switch (v.getId()) {
                case R.id.txvCall: {
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + information.getPhone()));
                    Utils.pushEvent(BusinessDetailsActivity.this, GTMEventType.CALL_RESOURCES_CLICKED_EVENT, SharedPrefUtils.getUserDetailModel(this).getId() + "", "Resource/Event Details");

                    startActivity(intent);
                }
                break;
                case R.id.txvShare: {
                    if (count <= 0) {
                        goToLoginDialog();
                        return;
                    }
                    Utils.pushEvent(BusinessDetailsActivity.this, GTMEventType.WRITEREVIEW_RESOURCES_CLICKED_EVENT, SharedPrefUtils.getUserDetailModel(this).getId() + "", "Resource/Event Details");
                    writeReviewFromHeader(AddReviewOrPhoto.WriteAReview);
                }
                break;
                case R.id.txvBookNow: {
                    if (count <= 0) {
                        (BusinessDetailsActivity.this).mDetailsHeader.goToLoginDialog();
                        return;
                    }

                    startActivity(new Intent(BusinessDetailsActivity.this, BookOrPayWebActivity.class).putExtra(Constants.WEB_VIEW_ECOMMERECE, information.getEcommerce_url()));
                }
                break;
                case R.id.txvAddToCal: {
                    if (count <= 0) {
                        goToLoginDialog();
                        return;
                    }
                    Utils.pushEvent(BusinessDetailsActivity.this, GTMEventType.ADDPHOTOS_RESOURCES_CLICKED_EVENT, SharedPrefUtils.getUserDetailModel(this).getId() + "", "Resource/Event Details");

                    CameraFragmentDialog fragmentDialog = new CameraFragmentDialog();
                    fragmentDialog.setSubmitListner((IOnSubmitGallery) BusinessDetailsActivity.this);
                    fragmentDialog.show(getSupportFragmentManager(), "");
                    writeReviewFromHeader(AddReviewOrPhoto.AddPhoto);
                }
                break;
                case R.id.img_round: {
                    if (flag) {
                        Log.d("check", " if popup" + flag);
                        mDetailsHeader.TopToBottom();
            /*	mDetailsHeader.setVisibility(View.VISIBLE);
                mDetailsHeader.collepseView();*/
                        flag = false;
                    } else {
                        Log.d("check", "else popup" + flag);
                        mDetailsHeader.bottomToTop();
            /*	Animation bottomUp = AnimationUtils.loadAnimation(BusinessDetailsActivity.this, R.anim.bottom_up);
                bottomUp.setAnimationListener(makeTopGone);
				mDetailsHeader.startAnimation(bottomUp);*/
                        flag = true;
                    }

			/*if(isOpen){
                _detailsHeader.TopToBottom();
			}else{
				_detailsHeader.bottomToTop();
			}
			isOpen=!isOpen;*/
                }
                break;
                case R.id.imgBack: {
                    finish();
                }
                break;
                case R.id.report_an_error: {
                    Intent intent = new Intent(this, ReportAnErrorActivity.class);
                    intent.putExtra(Constants.PAGE_TYPE, mEventOrBusiness);
                    intent.putExtra(Constants.BUSINESS_OR_EVENT_ID, businessOrEventId);
                    startActivity(intent);
                }
                break;
                case R.id.reviewBtn: {
                    Intent intent = new Intent(this, WriteReviewActivity.class);
                    intent.putExtra(Constants.PAGE_TYPE, mEventOrBusiness);
                    intent.putExtra(Constants.CATEGORY_ID, categoryId);
                    intent.putExtra(Constants.BUSINESS_OR_EVENT_ID, businessId);
                    startActivity(intent);
                }
                break;

                case R.id.transitBtn: {

                    Fragment currentFragment = getCurrentFragment();
                    if (currentFragment instanceof MapFragment) {
                        ((MapFragment) currentFragment).transitBtnClick();
                    }
                }
                break;
                case R.id.directionBtn: {
                    Fragment currentFragment = getCurrentFragment();
                    if (currentFragment instanceof MapFragment) {
                        ((MapFragment) currentFragment).directionBtnClick();
                    }
                }
                break;
                case R.id.img_favourites: {
                    Utils.pushEvent(BusinessDetailsActivity.this, GTMEventType.FAVOURITE_RESOURCES_CLICKED_EVENT, SharedPrefUtils.getUserDetailModel(this).getId() + "", "");
                    addRemoveBookmark();
                }
                break;


            }
        } else if (mEventOrBusiness == Constants.EVENT_PAGE_TYPE) {

            switch (v.getId()) {
                case R.id.txvCall: {
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + information.getPhone()));
                    Utils.pushEvent(BusinessDetailsActivity.this, GTMEventType.CALL_EVENT_CLICKED_EVENT, SharedPrefUtils.getUserDetailModel(this).getId()+"", "Resource/Event Details");
                    startActivity(intent);
                }
                break;
                case R.id.txvShare: {
                    Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    String titleName = information.getName();
                    if (StringUtils.isNullOrEmpty(titleName)) {
                        titleName = "";
                    }

                    String webUrl = information.getWeb_url();
                    if (StringUtils.isNullOrEmpty(webUrl)) {
                        webUrl = "";
                    }

                    String shareMessage = "mycity4kids\n\nCheck out this interesting event " + "\"" + titleName + "\" on " + shareDate + " at " + shareTime + ".\n" + webUrl;
                    shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareMessage);
                    Utils.pushEvent(BusinessDetailsActivity.this, GTMEventType.SHARE_EVENT_CLICKED_EVENT, SharedPrefUtils.getUserDetailModel(this).getId() + "", "Resource/Event Details");
                    startActivity(Intent.createChooser(shareIntent, "mycity4kids"));
                }
                break;
                case R.id.txvBookNow: {

                    if (count <= 0) {
                        mDetailsHeader.goToLoginDialog();
                        return;
                    }
                    startActivity(new Intent(BusinessDetailsActivity.this, BookOrPayWebActivity.class).putExtra(Constants.WEB_VIEW_ECOMMERECE, information.getEcommerce_url()));
                    break;
                }

                case R.id.txvAddToCal: {
                    Intent i = new Intent(BusinessDetailsActivity.this, ActivityCreateAppointment.class);
                    i.putExtra(Constants.EVENT_NAME, information.getName());
                    i.putExtra(Constants.EVENT_LOCATION, information.getLocality());
                    i.putExtra(Constants.EVENT_START_DATE, information.getEvent_date().getStart_date());
                    i.putExtra(Constants.EVENT_END_DATE, information.getEvent_date().getEnd_date());
                    Utils.pushEvent(BusinessDetailsActivity.this, GTMEventType.ADDCALENDAR_EVENT_CLICKED_EVENT, SharedPrefUtils.getUserDetailModel(this).getId() + "", "Resource/Event Details");
                    startActivity(i);
                    //saveCalendarEvent(information);
                }
                break;
                case R.id.img_round: {
                    if (flag) {
                        Log.d("check", " if popup" + flag);
                        mDetailsHeader.TopToBottom();
            /*	mDetailsHeader.setVisibility(View.VISIBLE);
                mDetailsHeader.collepseView();*/
                        flag = false;
                    } else {
                        Log.d("check", "else popup" + flag);
                        mDetailsHeader.bottomToTop();
            /*	Animation bottomUp = AnimationUtils.loadAnimation(BusinessDetailsActivity.this, R.anim.bottom_up);
                bottomUp.setAnimationListener(makeTopGone);
				mDetailsHeader.startAnimation(bottomUp);*/
                        flag = true;
                    }

			/*if(isOpen){
                _detailsHeader.TopToBottom();
			}else{
				_detailsHeader.bottomToTop();
			}
			isOpen=!isOpen;*/
                }
                break;
                case R.id.imgBack: {
                    finish();
                }
                break;
                case R.id.report_an_error: {
                    Intent intent = new Intent(this, ReportAnErrorActivity.class);
                    intent.putExtra(Constants.PAGE_TYPE, mEventOrBusiness);
                    intent.putExtra(Constants.BUSINESS_OR_EVENT_ID, businessOrEventId);
                    startActivity(intent);
                }
                break;
                case R.id.reviewBtn: {
                    Intent intent = new Intent(this, WriteReviewActivity.class);
                    intent.putExtra(Constants.PAGE_TYPE, mEventOrBusiness);
                    intent.putExtra(Constants.CATEGORY_ID, categoryId);
                    intent.putExtra(Constants.BUSINESS_OR_EVENT_ID, businessId);
                    startActivity(intent);
                }
                break;
                case R.id.transitBtn: {

                    Fragment currentFragment = getCurrentFragment();
                    if (currentFragment instanceof MapFragment) {
                        ((MapFragment) currentFragment).transitBtnClick();
                    }
                }
                break;
                case R.id.directionBtn: {
                    Fragment currentFragment = getCurrentFragment();
                    if (currentFragment instanceof MapFragment) {
                        ((MapFragment) currentFragment).directionBtnClick();
                    }
                }
                break;
            }
        }
    }

    /**
     * use it later.
     */

	/*private class LongOperation extends AsyncTask<Response, Void, Response> {

		@Override
		    protected void onPreExecute() {
		    // Things to be done before execution of long running operation. For example showing ProgessDialog
		    }

		    @Override
		    protected Response doInBackground(Response... params) {
		    	Response response=params[0];
		        return response;
		    }

		    @Override
		    protected void onPostExecute(Response result) {
		        // execution of result of Long time consuming operation
		    }

		    @Override
		    protected void onProgressUpdate(Void... values) {
		      // Things to be done while execution of long running operation is in progress. For example updating ProgessDialog
		     }
		}*/
    public void goToLoginDialog() {
        Bundle args = new Bundle();
        args.putInt(Constants.CATEGORY_ID, categoryId);
        args.putInt(Constants.PAGE_TYPE, mEventOrBusiness);
        args.putString(Constants.BUSINESS_OR_EVENT_ID, information.getId());
        args.putString(Constants.DISTANCE, distance);
        LoginFragmentDialog fragmentDialog = new LoginFragmentDialog();
        fragmentDialog.setArguments(args);
        fragmentDialog.show(getSupportFragmentManager(), "");
    }


    private void addRemoveBookmark() {
        BookmarkModel bookmarkRequest = new BookmarkModel();
        bookmarkRequest.setId(businessId);
        if (bookmarkStatus == 0) {
            bookmarkStatus = 1;
            imgBookmark.setImageResource(R.drawable.ic_favorite_border_white_48dp_fill);
            bookmarkRequest.setAction("add");
        } else {
            bookmarkStatus = 0;
            imgBookmark.setImageResource(R.drawable.ic_favorite_border_white_48dp);
            bookmarkRequest.setAction("remove");
        }
//        followAPICall(followAuthorId);
       /* BookmarkController bookmarkController = new BookmarkController(this, this);
        bookmarkController.getData(AppConstants.BOOKMARK_RESOURCE_REQUEST, bookmarkRequest);*/

        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        // prepare call in Retrofit 2.0
        ResourcesAPI bookmarkAPI = retrofit.create(ResourcesAPI.class);
        if (!ConnectivityUtils.isNetworkEnabled(this)) {
            removeProgressDialog();
            showToast(getString(R.string.error_network));
            return;
        }
        Call<CommonResponse> call = bookmarkAPI.addRemoveBookmark(SharedPrefUtils.getUserDetailModel(this).getId()+"",
                bookmarkRequest.getAction(),
                bookmarkRequest.getId()
               );


        //asynchronous call
        call.enqueue(new Callback<CommonResponse>() {
                         @Override
                         public void onResponse(Call<CommonResponse> call, retrofit2.Response<CommonResponse> response) {
                             int statusCode = response.code();

                             CommonResponse bookmarkResponse = (CommonResponse) response.body();

                             removeProgressDialog();
                             if (bookmarkResponse.getResponseCode() == 200) {
                                 ToastUtils.showToast(getApplicationContext(), bookmarkResponse.getResult().getMessage(), Toast.LENGTH_SHORT);
                                 if (BuildConfig.DEBUG) {
                                     Log.e("Bookmark response", bookmarkResponse.getResult().getMessage());
                                 }
                             } else if (bookmarkResponse.getResponseCode() == 400) {
                                 String message = bookmarkResponse.getResult().getMessage();
                                 if (BuildConfig.DEBUG) {
                                     Log.e("Bookmark response", bookmarkResponse.getResult().getMessage());
                                 }
                                 if (!StringUtils.isNullOrEmpty(message)) {
                                     showToast(message);
                                 } else {
                                     showToast(getString(R.string.went_wrong));
                                 }

                                 if (bookmarkStatus == 0) {
                                     bookmarkStatus = 1;
                                     imgBookmark.setImageResource(R.drawable.ic_favorite_border_white_48dp_fill);
                                 } else {
                                     bookmarkStatus = 0;
                                     imgBookmark.setImageResource(R.drawable.ic_favorite_border_white_48dp);
                                 }
                             }

                         }


                         @Override
                         public void onFailure(Call<CommonResponse> call, Throwable t) {

                         }
                     }
        );
    }

    /**
     * @param pMessage
     */
    private void showMessageAndFinish(String pMessage) {
        showToast(pMessage);
        finish();
    }

    final Animation.AnimationListener makeTopGone = new Animation.AnimationListener() {

        @Override
        public void onAnimationStart(Animation animation) {
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            Log.d("View Pager", "onAnimationEnd - makeTopGone");
            mDetailsHeader.setVisibility(View.GONE);

        }
    };

    @Override
    public void setOnSubmitListner(String type) {
        if (type.equals(Constants.ALBUM_TYPE)) {
            openGallery();
        } else if (type.endsWith(Constants.GALLERY_TYPE)) {
            takePhoto();
        }
    }

    private void openGallery() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, Constants.OPEN_GALLERY);
    }

    private void takePhoto() {
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        photo = new File(Environment.getExternalStorageDirectory(), "Pic.jpg");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo));
        startActivityForResult(intent, Constants.TAKE_PICTURE);
    }

    // methods for observable scrollview

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
    }

    @Override
    public void onDownMotionEvent() {
    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
    }

    private TouchInterceptionFrameLayout.TouchInterceptionListener mInterceptionListener = new TouchInterceptionFrameLayout.TouchInterceptionListener() {
        @Override
        public boolean shouldInterceptTouchEvent(MotionEvent ev, boolean moving, float diffX, float diffY) {
            if (!mScrolled && mSlop < Math.abs(diffX) && Math.abs(diffY) < Math.abs(diffX)) {
                // Horizontal scroll is maybe handled by ViewPager
                return false;
            }

            Scrollable scrollable = getCurrentScrollable();
            if (scrollable == null) {
                mScrolled = false;
                return false;
            }

            // If interceptionLayout can move, it should intercept.
            // And once it begins to move, horizontal scroll shouldn't work any longer.
            int flexibleSpace = mFlexibleSpaceHeight - mTabHeight;
            int translationY = (int) ViewHelper.getTranslationY(mInterceptionLayout);
            boolean scrollingUp = 0 < diffY;
            boolean scrollingDown = diffY < 0;
            if (scrollingUp) {
                if (translationY < 0) {
                    mScrolled = true;
                    return true;
                }
            } else if (scrollingDown) {
                if (-flexibleSpace < translationY) {
                    mScrolled = true;
                    return true;
                }
            }
            mScrolled = false;
            return false;
        }

        @Override
        public void onDownMotionEvent(MotionEvent ev) {
            mActivePointerId = ev.getPointerId(0);
            mScroller.forceFinished(true);
            if (mVelocityTracker == null) {
                mVelocityTracker = VelocityTracker.obtain();
            } else {
                mVelocityTracker.clear();
            }
            mBaseTranslationY = ViewHelper.getTranslationY(mInterceptionLayout);
            mVelocityTracker.addMovement(ev);
        }

        @Override
        public void onMoveMotionEvent(MotionEvent ev, float diffX, float diffY) {
            int flexibleSpace = mFlexibleSpaceHeight - mTabHeight;
            float translationY = ScrollUtils.getFloat(ViewHelper.getTranslationY(mInterceptionLayout) + diffY, -flexibleSpace, 0);
            MotionEvent e = MotionEvent.obtainNoHistory(ev);
            e.offsetLocation(0, translationY - mBaseTranslationY);
            mVelocityTracker.addMovement(e);
            updateFlexibleSpace(translationY);
        }

        @Override
        public void onUpOrCancelMotionEvent(MotionEvent ev) {
            if (mVelocityTracker == null) {
                return;
            }
            mScrolled = false;
            mVelocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
            int velocityY = (int) mVelocityTracker.getYVelocity(mActivePointerId);

            mActivePointerId = INVALID_POINTER;
            mScroller.forceFinished(true);
            int baseTranslationY = (int) ViewHelper.getTranslationY(mInterceptionLayout);

            int minY = -(mTopHeaderHeight - mTabHeight);
            int maxY = 0;

            if (velocityY < -1500) {
                velocityY = -500;
            }

            mScroller.fling(0, baseTranslationY, 0, velocityY, 0, 0, minY, maxY);
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    updateLayout();
                }
            });
        }
    };

    private void updateLayout() {
        boolean needsUpdate = false;
        float translationY = 0;
        if (mScroller.computeScrollOffset()) {
            translationY = mScroller.getCurrY();
            int flexibleSpace = mTopHeaderHeight - mTabHeight;
            if (-flexibleSpace <= translationY && translationY <= 0) {
                needsUpdate = true;
            } else if (translationY < -flexibleSpace) {
                translationY = -flexibleSpace;
                needsUpdate = true;
            } else if (0 < translationY) {
                translationY = 0;
                needsUpdate = true;
            }
        }

        if (needsUpdate) {
            updateFlexibleSpace(translationY);

            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    updateLayout();
                }
            });
        }
    }

    private Scrollable getCurrentScrollable() {
        Fragment fragment = getCurrentFragment();
        if (fragment == null) {
            return null;
        }
        View view = fragment.getView();
        if (view == null) {
            return null;
        }
        return (Scrollable) view.findViewById(R.id.scroll);
    }

    private void updateFlexibleSpace() {
        updateFlexibleSpace(ViewHelper.getTranslationY(mInterceptionLayout));
    }

    private void updateFlexibleSpace(float translationY) {
        ViewHelper.setTranslationY(mInterceptionLayout, translationY);
        int minOverlayTransitionY = getActionBarSize() - mOverlayView.getHeight();
        ViewHelper.setTranslationY(mImageView, ScrollUtils.getFloat(-translationY / 2, minOverlayTransitionY, 0));

        // Change alpha of overlay
        float flexibleRange = mFlexibleSpaceHeight - getActionBarSize();
        ViewHelper.setAlpha(mOverlayView, ScrollUtils.getFloat(-translationY / flexibleRange, 0, 1));
        float valu = ScrollUtils.getFloat(-translationY / flexibleRange, 0, 1);


        ViewHelper.setAlpha(titleMain, valu);
        ViewHelper.setAlpha(mTitleView, (1 - (valu * 2)));
        ViewHelper.setAlpha(mBelowTitleView, 1 - valu);
//        if (valu >=0.3){
//            mBelowTitleView.setVisibility(View.GONE);
//        } else {
//            ViewHelper.setAlpha(mBelowTitleView, valu);
//            mBelowTitleView.setVisibility(View.VISIBLE);
//        }

        // Scale title text
        float scale = 1 + ScrollUtils.getFloat((flexibleRange + translationY - mTabHeight) / flexibleRange, 0, MAX_TEXT_SCALE_DELTA);
        // mTitleView.setTextSize((int) (8 * scale * density));
//        setPivotXToTitle();
//        ViewHelper.setPivotY(mTitleView, 0);
//        ViewHelper.setScaleX(mTitleView, scale);
//        ViewHelper.setScaleY(mTitleView, scale);
    }

    public void moveToMap() {
        Fragment currentFragment = getCurrentFragment();
        mDetailsHeader.bottomToTop();
        if (currentFragment instanceof MapFragment) {
            ((MapFragment) currentFragment).directionBtnClick();
        } else {
            mViewPager.setCurrentItem(3);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    ((MapFragment) mDetailsFragmentAdapter.getItemAt(3)).directionBtnClick();
                }
            }, 1000);


            // move to map fragment
        }
    }

    public void callTransitBtn(DetailMap model) {
        Fragment currentFragment = getCurrentFragment();

        if (currentFragment instanceof MapFragment) {
            ((MapFragment) currentFragment).TransitPopUpClick(model);
        }
    }

    protected int getActionBarSize() {
        TypedValue typedValue = new TypedValue();
        int[] textSizeAttr = new int[]{R.attr.actionBarSize};
        int indexOfAttrTextSize = 0;
        TypedArray a = obtainStyledAttributes(typedValue.data, textSizeAttr);
        int actionBarSize = a.getDimensionPixelSize(indexOfAttrTextSize, -1);
        a.recycle();
        return actionBarSize;
    }

    protected int getScreenHeight() {
        return findViewById(android.R.id.content).getHeight();
    }

    public Fragment getCurrentFragment() {
        return mDetailsFragmentAdapter.getItemAt(mViewPager.getCurrentItem());
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void setPivotXToTitle() {
        Configuration config = getResources().getConfiguration();
        if (Build.VERSION_CODES.JELLY_BEAN_MR1 <= Build.VERSION.SDK_INT
                && config.getLayoutDirection() == View.LAYOUT_DIRECTION_RTL) {
            ViewHelper.setPivotX(mTitleView, findViewById(android.R.id.content).getWidth());
        } else {
            ViewHelper.setPivotX(mTitleView, 0);
        }

    }

}
