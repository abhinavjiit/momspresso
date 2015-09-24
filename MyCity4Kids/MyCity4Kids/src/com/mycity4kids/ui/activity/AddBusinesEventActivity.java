package com.mycity4kids.ui.activity;

import android.os.Bundle;
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
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.controller.ForgotPasswordController;
import com.mycity4kids.models.forgot.AddAListingRequest;
import com.mycity4kids.models.forgot.CommonResponse;

/**
 * @author ArshVardhan
 * @email ArshVardhan.Atreya@kelltontech.com
 * @createdDate 28-03-2014
 * @modifiedDate 29-03-2014
 * @description The AddBusinesEventActivity helps to add a new business
 *              event/listing
 */

public class AddBusinesEventActivity extends BaseActivity implements
OnClickListener {
	private EditText addBusinessOrEventEtxt;
	private EditText contactEtxt;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {

			setContentView(R.layout.add_busines_event);
			((TextView) findViewById(R.id.txvHeaderText))
			.setText(getResources().getString(R.string.add_business));
			((TextView) findViewById(R.id.txtSentBtn)).setOnClickListener(this);
			((ImageView) findViewById(R.id.cross_icon))
			.setOnClickListener(this);
			addBusinessOrEventEtxt = (EditText) findViewById(R.id.add_businees_or_event_etxt);
			contactEtxt = (EditText) findViewById(R.id.contact_edit_txt);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private AddAListingRequest getRequestData() {
		AddAListingRequest requestData = new AddAListingRequest();
		
		String businessName = addBusinessOrEventEtxt.getText().toString()
				.trim();
		if (StringUtils.isNullOrEmpty(businessName)) {
			Toast.makeText(AddBusinesEventActivity.this,
					"Please enter the name of a business or event.",
					Toast.LENGTH_SHORT).show();
			return null;
		} else if (businessName.length() > 30) {
			Toast.makeText(AddBusinesEventActivity.this,"Please restrict the business or event name to 30 characters.",Toast.LENGTH_SHORT).show();
					
			return null;
		} 
		else {
			requestData.setBusinessName(addBusinessOrEventEtxt.getText().toString().trim());
					
		}
		String contactNumber = contactEtxt.getText().toString().trim();
		if (!StringUtils.isNullOrEmpty(contactNumber)&& contactNumber.length() > 9) {
				
			requestData.setContactNumber(contactEtxt.getText().toString().trim());
					
		} else {
			Toast.makeText(AddBusinesEventActivity.this,
					"Please enter a valid 10 digit contact number",
					Toast.LENGTH_SHORT).show();
			return null;
		}
		
		return requestData;
	}

	@Override
	protected void updateUi(Response response) {
		if (response == null) {
			removeProgressDialog();
			showToast("Something went wrong from server");
			return;
		}
		switch (response.getDataType()) {
		case AppConstants.ADD_A_LISTING_REQUEST:
			removeProgressDialog();
			CommonResponse responseData = (CommonResponse) response
					.getResponseObject();
			String message = responseData.getResult().getMessage();
			if (responseData.getResponseCode() == Constants.HTTP_RESPONSE_SUCCESS) {
				if(StringUtils.isNullOrEmpty(message)){
					Toast.makeText(AddBusinesEventActivity.this,getResources().getString(R.string.add_listing_success), Toast.LENGTH_SHORT).show();
				}else{
					Toast.makeText(AddBusinesEventActivity.this,message,Toast.LENGTH_SHORT).show();
				}
				finish();
			} else if (responseData.getResponseCode() == 400) {
				if(StringUtils.isNullOrEmpty(message)){
					Toast.makeText(AddBusinesEventActivity.this,getResources().getString(R.string.went_wrong), Toast.LENGTH_SHORT).show();
				}else{
					Toast.makeText(AddBusinesEventActivity.this,message,Toast.LENGTH_SHORT).show();
				}
			}
			break;

		default:
			break;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.txtSentBtn:
			ForgotPasswordController _controller = new ForgotPasswordController(
					this, this);
			if (null != getRequestData()) {
				if (!ConnectivityUtils
						.isNetworkEnabled(AddBusinesEventActivity.this)) {
					ToastUtils.showToast(AddBusinesEventActivity.this,getString(R.string.error_network));

					return;
				}
				showProgressDialog(getString(R.string.please_wait));
				_controller.getData(AppConstants.ADD_A_LISTING_REQUEST,getRequestData());
			}
			break;
		case R.id.cross_icon:
			finish();
			break;

		default:
			break;
		}
	}
}
