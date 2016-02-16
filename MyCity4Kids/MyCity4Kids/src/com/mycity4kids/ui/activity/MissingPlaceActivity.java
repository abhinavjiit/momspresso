package com.mycity4kids.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.kelltontech.utils.ConnectivityUtils;
import com.kelltontech.utils.StringUtils;
import com.kelltontech.utils.ToastUtils;
import com.mycity4kids.R;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.controller.MissingPlaceController;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.parentingstop.ParentingRequest;
import com.mycity4kids.newmodels.TaskResponse;
import com.mycity4kids.preference.SharedPrefUtils;

/**
 * Created by manish.soni on 31-07-2015.
 */
public class MissingPlaceActivity extends BaseActivity {

    EditText businessEventName, contactNumber;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aa_missing_place);
        Utils.pushOpenScreenEvent(MissingPlaceActivity.this, "Add Missing Place", SharedPrefUtils.getUserDetailModel(this).getId() + "");

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Add a missing place");

        businessEventName = (EditText) findViewById(R.id.business_event_name);
        contactNumber = (EditText) findViewById(R.id.contact_number);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // according to fragment change it
        getMenuInflater().inflate(R.menu.forgot_password, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                finish();
                return true;

            case R.id.save:

                if (ConnectivityUtils.isNetworkEnabled(this)) {
                    if (!StringUtils.isNullOrEmpty(businessEventName.getText().toString().trim()) && !StringUtils.isNullOrEmpty(contactNumber.getText().toString().trim())) {
                        hitMissingPlaceAPI(businessEventName.getText().toString().trim(), contactNumber.getText().toString().trim());
                    } else {
                        ToastUtils.showToast(this, "Enter event or business name and contact number...");
                    }
                } else {
                    ToastUtils.showToast(this, "No network found..");
                }

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    protected void updateUi(Response response) {
        TaskResponse responseData;

        if (response == null) {
            ToastUtils.showToast(this, "Something went wrong from server");
            removeProgressDialog();
            return;
        }

        switch (response.getDataType()) {

            case AppConstants.MISSING_PLACE_REQUEST:
                responseData = (TaskResponse) response.getResponseObject();
                if (responseData.getResponseCode() == 200) {
                    removeProgressDialog();
                    String message = responseData.getResult().getMessage();
                    ToastUtils.showToast(this, message);

                    finish();


                } else if (responseData.getResponseCode() == 400) {
                    removeProgressDialog();
                    String message = responseData.getResult().getMessage();
                    if (!StringUtils.isNullOrEmpty(message)) {
                        ToastUtils.showToast(this, message);
                    } else {
                        ToastUtils.showToast(this, getString(R.string.went_wrong));
                    }
                }
                break;
        }
    }

    public void hitMissingPlaceAPI(String name, String number) {

        showProgressDialog(getString(R.string.please_wait));
        ParentingRequest _parentingModel = new ParentingRequest();
        _parentingModel.setEvent_name(name);
        _parentingModel.setContact_no(number);
        MissingPlaceController newParentingBlogController = new MissingPlaceController(this, this);
        newParentingBlogController.getData(AppConstants.MISSING_PLACE_REQUEST, _parentingModel);

    }
}
