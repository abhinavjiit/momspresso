package com.mycity4kids.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kelltontech.ui.BaseActivity;
import com.kelltontech.utils.ConnectivityUtils;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.controller.LoginController;
import com.mycity4kids.models.user.UserRequest;
import com.mycity4kids.models.user.UserResponse;
/**
 * 
 * @author "Deepanker Chaudhary"
 *
 */

public class LoginEmailActivity extends BaseActivity implements OnClickListener{
	
	private EditText mEmailId,mPassword;
	private int mCategoryId;
	private int mBusinessOrEventType;
	private String mBusinessOrEventId;
	private String mDistance;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_with_email);
		
		mEmailId=(EditText)findViewById(R.id.email_edit_txt);
		mPassword=(EditText)findViewById(R.id.password_edit_txt);
		((Button)findViewById(R.id.email_login_btn)).setOnClickListener(this);
		((TextView)findViewById(R.id.sign_up_btn)).setOnClickListener(this);
		((TextView)findViewById(R.id.forgot_txt)).setOnClickListener(this);
		((ImageView)findViewById(R.id.cross_icon)).setOnClickListener(this);
		Bundle bundle=getIntent().getExtras();
		if(bundle!=null){

			Constants.IS_COMING_FROM_INSIDE=bundle.getBoolean(Constants.LOGIN_REQUIRED, false);
			mCategoryId=bundle.getInt(Constants.CATEGORY_ID,0);
			mBusinessOrEventId=bundle.getString(Constants.BUSINESS_OR_EVENT_ID);
			mBusinessOrEventType=bundle.getInt(Constants.PAGE_TYPE,0);
			mDistance=bundle.getString(Constants.DISTANCE);

		}else{
			Constants.IS_COMING_FROM_INSIDE=false;
		}
	}
	
	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		removeProgressDialog();
	}
	
	
	@Override
	public void onClick(View v) {
		Intent intent=null;
		switch (v.getId()) {
		

		case R.id.email_login_btn:
			if(isDataValid()) {
			if(ConnectivityUtils.isNetworkEnabled(this)){
			showProgressDialog(getString(R.string.please_wait));
				//mProgressDialog=ProgressDialog.show(this, "", "Please Wait...",true,false);
				
				String emailId=mEmailId.getText().toString().trim();
				String password=mPassword.getText().toString().trim();
				UserRequest _requestModel=new UserRequest();
				_requestModel.setEmailId(emailId);
				_requestModel.setPassword(password);
				_requestModel.setNetworkName("throughMail");
				LoginController _controller=new LoginController(this, this);
				_controller.getData(AppConstants.LOGIN_REQUEST, _requestModel);	
			}else{
				showToast(getString(R.string.error_network));
			}
			}
			break;
		case R.id.sign_up_btn:
			if(!Constants.IS_COMING_FROM_INSIDE){
				goToRegisterFromFirstTime();
			}else{
				goToRegisterForDetails();
			}
			break;
		case R.id.cross_icon:
			goToLanding();
			break;
		case R.id.forgot_txt:
			 intent =new Intent(this,ForgotPasswordActivity.class);
			startActivity(intent);
			break;
			default:
			break;
		}
			}
	
	@Override
	public void onBackPressed() {
		goToLanding();
	}
	
	
	@Override
	protected void updateUi(com.kelltontech.network.Response response) {
		if( response==null){
			showToast("Something went wrong from server");
			return;
		}
		
		UserResponse responseData=(UserResponse)response.getResponseObject();
		String message = responseData.getResult().getMessage();
		if(responseData.getResponseCode()==200){
			if(StringUtils.isNullOrEmpty(message)){
				Toast.makeText(LoginEmailActivity.this, getResources().getString(R.string.login_successful), Toast.LENGTH_SHORT).show();
			}else{
				Toast.makeText(LoginEmailActivity.this,message, Toast.LENGTH_SHORT).show();
			}
			if(!Constants.IS_COMING_FROM_INSIDE){
				sendToHomeScreen();
			}else{
				sendToDetailScreen();
			}
			
		}else if(responseData.getResponseCode()==400){
			if(StringUtils.isNullOrEmpty(message)){
				Toast.makeText(LoginEmailActivity.this, getResources().getString(R.string.email_or_password_not_matched), Toast.LENGTH_SHORT).show();
			}else{
				Toast.makeText(LoginEmailActivity.this,message, Toast.LENGTH_SHORT).show();
			}

		}
		removeProgressDialog();
		
		
	}
	

	
	private boolean isDataValid() {
		boolean isLoginOk=true;
		String email_id = mEmailId.getText().toString();

		if(email_id.trim().length() ==0 || (!StringUtils.isValidEmail(email_id))){
			mEmailId.setFocusableInTouchMode(true);
			mEmailId.setError("Please enter a valid email address");
			mEmailId.requestFocus();
			isLoginOk=false;
		} else if(mPassword.getText().toString().length() == 0 ) {
			mPassword.setFocusableInTouchMode(true);
			mPassword.requestFocus();
			mPassword.setError("Please enter a password");
			//mPassword.requestFocus();
			isLoginOk=false;
		}
		else if(mPassword.getText().toString().length() <5 ) {
			mPassword.setFocusableInTouchMode(true);
			mPassword.requestFocus();
				
			mPassword.setError("The password must have a minimum of 5 characters.");
			//mPassword.requestFocus();
			isLoginOk=false;
		}
		return isLoginOk;
	}
	
	
	private void sendToDetailScreen(){
		Intent intent =new Intent(this,BusinessDetailsActivity.class);
		intent.putExtra(Constants.CATEGORY_ID, mCategoryId);
		intent.putExtra(Constants.BUSINESS_OR_EVENT_ID, mBusinessOrEventId);
		intent.putExtra(Constants.PAGE_TYPE, mBusinessOrEventType);
		intent.putExtra(Constants.DISTANCE,mDistance);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
		finish();
	}
	private void goToRegisterFromFirstTime(){
		Intent intent=new Intent(this,RegistrationActivity.class);
		 intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		startActivity(intent);
		finish();
	}
	private void goToRegisterForDetails(){
		Intent intent=new Intent(this,RegistrationActivity.class);
		intent.putExtra(Constants.LOGIN_REQUIRED, Constants.IS_COMING_FROM_INSIDE);
		intent.putExtra(Constants.CATEGORY_ID, mCategoryId);
		intent.putExtra(Constants.BUSINESS_OR_EVENT_ID, mBusinessOrEventId);
		intent.putExtra(Constants.PAGE_TYPE, mBusinessOrEventType);
		intent.putExtra(Constants.DISTANCE,mDistance);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		startActivity(intent);
		finish();
	}
}
