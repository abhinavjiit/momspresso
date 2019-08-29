package com.mycity4kids.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.kelltontech.utils.ConnectivityUtils;
import com.kelltontech.utils.ToastUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.controller.RecentlyViewController;
import com.mycity4kids.models.businesslist.BusinessDataListing;
import com.mycity4kids.models.businesslist.BusinessListResponse;
import com.mycity4kids.newmodels.ExternalEventModel;
import com.mycity4kids.ui.adapter.BusinessListingAdapter;
import com.mycity4kids.ui.adapter.BusinessListingAdapterevent;

import java.util.ArrayList;

public class RecentlyViewedActivity extends BaseActivity {

    private ListView mRecentlyListView;
    private TextView noData;
    private boolean fromEvents;
    private Toolbar mToolbar;
    private BusinessListingAdapterevent eventAdapeter;
    private BusinessListingAdapter kidsResourcesAdapeter;
    private ArrayList<BusinessDataListing> mBusinessDataListings = new ArrayList<>();
    private RelativeLayout root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
//            int SDK_INT = android.os.Build.VERSION.SDK_INT;
//            if (SDK_INT < 11) {
//                requestWindowFeature(Window.FEATURE_NO_TITLE);
//                getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//            }
            setContentView(R.layout.recently_view_activity);
            root = findViewById(R.id.businessRoot);
            ((BaseApplication) getApplication()).setView(root);
            ((BaseApplication) getApplication()).setActivity(this);

            mToolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(mToolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Recently Viewed");
            mToolbar.setClickable(true);

            mRecentlyListView = (ListView) findViewById(R.id.recentlyListView);
            noData = (TextView) findViewById(R.id.no_data_txt);


            // get intent
            Bundle extras = getIntent().getExtras();
            fromEvents = extras.getBoolean(Constants.FROM_EVENTS);

            if (fromEvents)
                eventAdapeter = new BusinessListingAdapterevent(this);
            else
                kidsResourcesAdapeter = new BusinessListingAdapter(this);

            ExternalEventModel model = new ExternalEventModel();
            model.setIsfromEvents(fromEvents);

            RecentlyViewController _controller = new RecentlyViewController(this, this);
            if (!ConnectivityUtils.isNetworkEnabled(this)) {
                ToastUtils.showToast(this, getString(R.string.error_network));
                finish();
                return;

            }
            showProgressDialog(getResources().getString(R.string.please_wait));
            _controller.getData(AppConstants.RECENTLY_VIEWED_REQUEST, model);

            mRecentlyListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {

                    Intent intent = new Intent(RecentlyViewedActivity.this, BusinessDetailsActivity.class);
                    intent.putExtra(Constants.CATEGORY_ID, fromEvents ? 6 : 7);
                    intent.putExtra(Constants.BUSINESS_OR_EVENT_ID, mBusinessDataListings.get(pos).getId());
                    intent.putExtra("isbusiness", !fromEvents);
                    intent.putExtra(Constants.PAGE_TYPE, fromEvents ? Constants.EVENT_PAGE_TYPE : Constants.BUSINESS_PAGE_TYPE);
                    intent.putExtra(Constants.DISTANCE, mBusinessDataListings.get(pos).getDistance());
                    startActivity(intent);
                }
            });

//            mRecentlyListView.setOnItemClickListener(new OnItemClickListener() {
//
//                @Override
//                public void onItemClick(AdapterView<?> parent, View view,
//                                        int pos, long id) {
//
//                    RecentlyViewedModel recentlyViewModel = ((RecentlyViewedModel) parent.getAdapter().getItem(pos));
//                    Intent intent = new Intent(RecentlyViewedActivity.this, BusinessDetailsActivity.class);
//                    intent.putExtra(Constants.CATEGORY_ID, recentlyViewModel.getCategory_id());
//                    intent.putExtra(Constants.BUSINESS_OR_EVENT_ID, recentlyViewModel.getListing_id());
//                    intent.putExtra("isbusiness", !fromEvents);
//                    intent.putExtra(Constants.PAGE_TYPE, fromEvents ? Constants.EVENT_PAGE_TYPE : Constants.BUSINESS_PAGE_TYPE);
//                    intent.putExtra(Constants.DISTANCE, recentlyViewModel.getDistance());
//                    //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    startActivity(intent);
//                    //	finish();
//
//
//                }
//            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void updateUi(Response response) {
        removeProgressDialog();
        if (response == null) {
            showToast(getResources().getString(R.string.server_error));
            return;
        }
        switch (response.getDataType()) {
            case AppConstants.RECENTLY_VIEWED_REQUEST:


                BusinessListResponse responseData = (BusinessListResponse) response.getResponseObject();
                if (responseData.getResponseCode() == 200) {
                    mBusinessDataListings.addAll(responseData.getResult().getData().getData());
                    if (fromEvents) {
                        mRecentlyListView.setAdapter(eventAdapeter);
                        eventAdapeter.setListData(mBusinessDataListings, Constants.EVENT_PAGE_TYPE);
                        eventAdapeter.notifyDataSetChanged();
                    } else {
                        mRecentlyListView.setAdapter(kidsResourcesAdapeter);
                        kidsResourcesAdapeter.setListData(mBusinessDataListings, Constants.BUSINESS_PAGE_TYPE);
                        kidsResourcesAdapeter.notifyDataSetChanged();
                    }


                } else if (responseData.getResponseCode() == 400) {
                    noData.setVisibility(View.VISIBLE);
                }
                break;
//            case AppConstants.LOGOUT_REQUEST:
////			if(mHeader!=null){
////				mHeader.closeDrawer();
////			}
//                removeProgressDialog();
//                LogoutResponse logoutResponse = (LogoutResponse) response.getResponseObject();
//                String message = logoutResponse.getResult().getMessage();
//                if (logoutResponse.getResponseCode() == 200) {
//                    /**
//                     * delete table from local also;
//                     */
//                    UserTable _tables = new UserTable((BaseApplication) getApplicationContext());
//                    _tables.deleteAll();
//                    if (StringUtils.isNullOrEmpty(message)) {
//                        Toast.makeText(this, getString(R.string.went_wrong), Toast.LENGTH_SHORT).show();
//
//                    } else {
//                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
//                    }
//
//                } else if (logoutResponse.getResponseCode() == 400) {
//                    if (StringUtils.isNullOrEmpty(message)) {
//                        Toast.makeText(this, getString(R.string.went_wrong), Toast.LENGTH_SHORT).show();
//                    } else {
//                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
//                    }
//                }
//                break;
            default:
                break;
        }


    }

}
