package com.mycity4kids.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.kelltontech.utils.ConnectivityUtils;
import com.kelltontech.utils.StringUtils;
import com.kelltontech.utils.ToastUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.controller.ForgotPasswordController;
import com.mycity4kids.dbtable.UserTable;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.forgot.CommonResponse;
import com.mycity4kids.models.reportanerror.ErrorListModel;
import com.mycity4kids.models.reportanerror.ErrorRequest;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.ui.adapter.ReportAnErrorAdapter;
import com.mycity4kids.widget.CustomListView;

import java.util.ArrayList;


/**
 * @author ArshVardhan
 * @email ArshVardhan.Atreya@kelltontech.com
 * @createdDate 29-03-2014
 * @modifiedDate 29-03-2014
 * @description The ReportAnErrorActivity screen helps user to report errors
 */

public class ReportAnErrorActivity extends BaseActivity implements
        OnClickListener {

    private ArrayList<ErrorListModel> errorList;
    private CustomListView errorListView;
    //private TextView headerText;
    public EditText specifyErrorEditText;
    private int mEventOrBusinessType;
    private String mEventOrBusinesId;
    Toolbar mToolbar;
    ReportAnErrorAdapter _adapter;
    public ImageView separator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.pushOpenScreenEvent(ReportAnErrorActivity.this, "Report Error Info", SharedPrefUtils.getUserDetailModel(this).getId() + "");

        setContentView(R.layout.activity_report_an_error);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Report an Error");
        /*mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setTitle("Report an Error");*/
        errorListView = (CustomListView) findViewById(R.id.lvErrorList);
        errorListView.isExpanded();
        //headerText = (TextView) findViewById(R.id.txvHeaderText);
        specifyErrorEditText = (EditText) findViewById(R.id.edtSpecifyError);
        separator = (ImageView) findViewById(R.id.separator);
        specifyErrorEditText.setVisibility(View.GONE);
        separator.setVisibility(View.GONE);
        //	((TextView) findViewById(R.id.btnReportError)).setOnClickListener(this);
//		((ImageView) findViewById(R.id.cross_icon)).setOnClickListener(this);
        ((TextView) findViewById(R.id.backbtn)).setOnClickListener(this);
        ((TextView) findViewById(R.id.save)).setOnClickListener(this);
        //headerText.setText("Report an Error");


        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mEventOrBusinessType = bundle.getInt(Constants.PAGE_TYPE);
            mEventOrBusinesId = bundle.getString(Constants.BUSINESS_OR_EVENT_ID);
        }


        errorList = addErrorTypesToErrorList();
        if (errorList != null && errorList.size() != 0) {
            errorListView.setVisibility(View.VISIBLE);
            _adapter = new ReportAnErrorAdapter(this);
            _adapter.setData(errorList);
            errorListView.setAdapter(_adapter);
        } else {
            errorListView.setVisibility(View.GONE);
        }


    }

    private String getSelectedErrors() {
        String selectedErrors = "";
        for (int i = 1; i < errorList.size(); i++) {
            ErrorListModel errorListItem = errorList.get(i);
            if (errorListItem.isSelected()) {
                selectedErrors = selectedErrors + errorListItem.getErrorKey() + ",";
            }
        }
        if ((selectedErrors != null) && !(selectedErrors.equals(""))) {
            return selectedErrors.substring(0, selectedErrors.length() - 1);
        } else {
            Toast.makeText(this, getResources().getString(R.string.please_select_an_error), Toast.LENGTH_SHORT).show();


            return null;
        }
    }

    private ArrayList<ErrorListModel> addErrorTypesToErrorList() {

        ArrayList<ErrorListModel> errList = new ArrayList<ErrorListModel>();
        /*String[] errorTypes = { "What's Wrong?", "Phone Number", "Address",
                "Closed Down", "Information", "Others" };*/
        String[] errorTypes = {"", "Phone Number", "Address",
                "Closed Down", "Map", "Others"};
        String[] errorKey = {"", "phone", "address", "closed", "map", "others"};
        int i = 1;
        for (String string : errorTypes) {
            ErrorListModel model = new ErrorListModel();
            model.setErrorKey(errorKey[i - 1]);
            model.setErrorId(i++);
            model.setErrorType(string);
            errList.add(model);
        }
        return errList;
    }

    @Override
    protected void updateUi(Response response) {
        removeProgressDialog();
        if (response == null) {
            //showToast("Something went wrong from server");
            return;
        }
        switch (response.getDataType()) {
            case AppConstants.REPORT_AN_ERROR_REQUEST:
                removeProgressDialog();
                CommonResponse responseData = (CommonResponse) response
                        .getResponseObject();
                if (responseData.getResponseCode() == Constants.HTTP_RESPONSE_SUCCESS) {
                    Toast.makeText(ReportAnErrorActivity.this, responseData.getResult().getMessage(), Toast.LENGTH_SHORT).show();

                    finish();
                } else if (responseData.getResponseCode() == 400) {
                    Toast.makeText(ReportAnErrorActivity.this, responseData.getResult().getMessage(), Toast.LENGTH_SHORT).show();


                }
                break;

            default:
                break;
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        /*case R.id.btnReportError:
            sendReportErrorReq();
			break;*/
            case R.id.backbtn:
                finish();
                break;
            case R.id.save:
//                sendReportErrorReq();
                break;
            default:
                break;
        }
    }

    private void sendReportErrorReq(String selectedError) {
        ErrorRequest request = new ErrorRequest();
        if (!StringUtils.isNullOrEmpty(selectedError)) {
            request.setReportType(selectedError);
        } else {
            showToast("Please select What's Wrong!");
            return;
        }
        String reportContent = specifyErrorEditText.getText().toString();
        if (!StringUtils.isNullOrEmpty(reportContent)) {
            request.setReportContent(reportContent);
        } else {
            request.setReportContent("");
        }
        if (!StringUtils.isNullOrEmpty(mEventOrBusinesId)) {
            request.setListingId(mEventOrBusinesId);
            if (Constants.BUSINESS_PAGE_TYPE == mEventOrBusinessType) {
                request.setListingType("business");
            } else if (Constants.EVENT_PAGE_TYPE == mEventOrBusinessType) {
                request.setListingType("event");
            }
        }


        UserTable userTable = new UserTable((BaseApplication) this.getApplication());
        int userId = userTable.getUserId();
        int count = userTable.getCount();
        if (count <= 0) {
            request.setUserId("");
        } else {
            request.setUserId("" + userId);
        }



        ForgotPasswordController _controller = new ForgotPasswordController(this, this);

        if (null != request) {
            if (!ConnectivityUtils.isNetworkEnabled(ReportAnErrorActivity.this)) {
                ToastUtils.showToast(ReportAnErrorActivity.this, getString(R.string.error_network));

                return;
            }
            showProgressDialog(getString(R.string.please_wait));
            _controller.getData(AppConstants.REPORT_AN_ERROR_REQUEST, request);
        }
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
                String selectedError = getSelectedErrors();
                if (!StringUtils.isNullOrEmpty(selectedError)) {
                    if (selectedError.equals("others") && specifyErrorEditText.getText().toString().trim().equals("")) {
                        ToastUtils.showToast(this, "Reason is required field in case of \"Others\"");
                        return true;
                    }
                    sendReportErrorReq(selectedError);
                }

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
