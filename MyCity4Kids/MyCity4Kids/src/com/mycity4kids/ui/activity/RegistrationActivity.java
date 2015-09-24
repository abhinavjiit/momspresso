package com.mycity4kids.ui.activity;

import android.content.Intent;
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
import com.mycity4kids.controller.RegistrationController;
import com.mycity4kids.models.user.UserRequest;
import com.mycity4kids.models.user.UserResponse;
import com.mycity4kids.preference.SharedPrefUtils;
/**
 * 
 * @author Deepanker Chaudhary
 *
 */

public class RegistrationActivity extends BaseActivity implements OnClickListener{
	private EditText editFirstName;
	private EditText editEmailId;
	private EditText editPassword;
	private EditText editConfirmEmail;
	private int mCategoryId;
	private int mBusinessOrEventType;
	private String mBusinessOrEventId;
	private String mDistance;




	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.registration_activity);
		editFirstName=(EditText)findViewById(R.id.editFirstName);
		editEmailId=(EditText)findViewById(R.id.editEmail);
		editPassword=(EditText)findViewById(R.id.editPassword);
		editConfirmEmail=(EditText)findViewById(R.id.confirm_email);
		((TextView)findViewById(R.id.txvSignUp)).setOnClickListener(this);
		((ImageView)findViewById(R.id.cross_icon)).setOnClickListener(this);
		Bundle bundle=getIntent().getExtras();
		if(bundle!=null){

			Constants.IS_COMING_FROM_INSIDE=bundle.getBoolean(Constants.LOGIN_REQUIRED, false);
			mCategoryId=bundle.getInt(Constants.CATEGORY_ID,0);
			mBusinessOrEventId=bundle.getString(Constants.BUSINESS_OR_EVENT_ID,"");
			mBusinessOrEventType=bundle.getInt(Constants.PAGE_TYPE,0);
			mDistance=bundle.getString(Constants.DISTANCE,"");
		}else{
			Constants.IS_COMING_FROM_INSIDE=false;
		}
	}

	@Override
	protected void updateUi(Response response) {
		removeProgressDialog();
		if( response==null){

			showToast("Something went wrong from server");
			return;
		}
		switch (response.getDataType()) {
		case AppConstants.REGISTRATION_REQUEST:
			UserResponse responseData=(UserResponse)response.getResponseObject();
			String message=responseData.getResult().getMessage();
			if(responseData.getResponseCode()==200){
				if(!Constants.IS_COMING_FROM_INSIDE){
					sendToHomeScreen();
				}else{
					sendToDetailScreen();
				}
				if(StringUtils.isNullOrEmpty(message)){
					Toast.makeText(RegistrationActivity.this, getResources().getString(R.string.registration_successful), Toast.LENGTH_SHORT).show();
				}else{
					Toast.makeText(RegistrationActivity.this,message, Toast.LENGTH_SHORT).show();
				}
				
			}else if(responseData.getResponseCode()==400){
				
				
				if(StringUtils.isNullOrEmpty(message)){
					Toast.makeText(RegistrationActivity.this,getResources().getString(R.string.email_already_registered), Toast.LENGTH_SHORT).show();
				}else{
					Toast.makeText(RegistrationActivity.this,message, Toast.LENGTH_SHORT).show();
				}
			

			}
			break;

		default:
			break;
		}
		


	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		removeProgressDialog();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.txvSignUp:

			UserRequest registraionModel=getRegistrationRequestModel();
			if(isDataValid(registraionModel))
			{
				showProgressDialog(getString(R.string.please_wait));
				if(!ConnectivityUtils.isNetworkEnabled(RegistrationActivity.this)){
					ToastUtils.showToast(RegistrationActivity.this, getString(R.string.error_network));
					return;
				}
				RegistrationController _controller=new RegistrationController(this, this);
				_controller.getData(AppConstants.REGISTRATION_REQUEST, registraionModel);

			}
			break;
		case R.id.cross_icon:
			goToLogin();
			break;
		default:
			break;
		}

	}
	@Override
	public void onBackPressed() {
		goToLogin();
	}

	/**
	 *  
	 * @return registration request model:
	 */
	private UserRequest getRegistrationRequestModel(){

		int currentCityId=SharedPrefUtils.getCurrentCityModel(this).getId();

		String emailId=editEmailId.getText().toString().trim();
		String password=editPassword.getText().toString().trim();
		String firstName=editFirstName.getText().toString().trim();
		String confirmEmail=editConfirmEmail.getText().toString().trim();


		UserRequest _requestModel=new UserRequest();
		_requestModel.setEmailId(emailId);
		_requestModel.setPassword(password);
		_requestModel.setCityId(currentCityId);
		_requestModel.setFirstName(firstName);
		_requestModel.setConfirmEmail(confirmEmail);

		return _requestModel;
	}

	private boolean isDataValid(UserRequest registraionModel){
		boolean isRegistrationOk=true;
		String email_id = registraionModel.getEmailId();
		String password =registraionModel.getPassword();
		String first_name = registraionModel.getFirstName();
		String confirm_email=registraionModel.getConfirmEmail();

		if(first_name.trim().length() == 0 ){
			editFirstName.setFocusableInTouchMode(true);
			editFirstName.setError("Please enter your name");
			editFirstName.requestFocus();
			isRegistrationOk=false;
		} else if(first_name.trim().length() > 30 ){
			editFirstName.setFocusableInTouchMode(true);
			editFirstName.setError("Please restrict it to 30 characters");
			editFirstName.requestFocus();
			isRegistrationOk=false;
		} /*else if(last_name.trim().length() == 0 ){
			editLastName.setFocusableInTouchMode(true);
			editLastName.setError("Please enter last name");
			editLastName.requestFocus();
			isRegistrationOk=false;
		}*/
		else if(email_id.length()==0) {
			//	editEmailId.setError(Html.fromHtml("<font color='green'>Please enter valid email id.</font>"));
			editEmailId.setFocusableInTouchMode(true);
			editEmailId.setError("Please enter a valid email id.");
			editEmailId.requestFocus();
			isRegistrationOk=false;
		} else if(!StringUtils.isValidEmail(email_id)){
			editEmailId.setFocusableInTouchMode(true);
			editEmailId.setError("Your email address is invalid.");
			editEmailId.requestFocus();
			isRegistrationOk=false;
		} 
		/*if(confirm_password.length()==0){
			editPassword.setFocusableInTouchMode(true);
			editPassword.setError("Please enter a confirm password.");
			isRegistrationOk=false;
		} if(confirm_password.length()<5){
			editPassword.setFocusableInTouchMode(true);
			editPassword.setError("Confirm password should not less than 5 character.");
			isRegistrationOk=false;
		}*/
		else if(confirm_email.length()==0){
			editConfirmEmail.setFocusableInTouchMode(true);
			editConfirmEmail.setError("Please enter your confirm email address.");
			editConfirmEmail.requestFocus();
			isRegistrationOk=false;
		}
		else if(!StringUtils.isValidEmail(confirm_email)){
			editConfirmEmail.setFocusableInTouchMode(true);
			editConfirmEmail.setError("Please enter valid confirm email address.");
			editConfirmEmail.requestFocus();
			isRegistrationOk=false;
		}
		else if(!(confirm_email.equalsIgnoreCase(email_id))){
			editConfirmEmail.setFocusableInTouchMode(true);
			editConfirmEmail.setError("Email id does not match.");
			editConfirmEmail.requestFocus();
			isRegistrationOk=false;
		}
		else if(password.length()==0){
			editPassword.setFocusableInTouchMode(true);
			editPassword.setError("Please enter a password.");
			editPassword.requestFocus();
			isRegistrationOk=false;
		} else if(password.length()<5){
			editPassword.setFocusableInTouchMode(true);
			editPassword.setError("The password must have a minimum of 5 characters.");
			editPassword.requestFocus();
			isRegistrationOk=false;
		}
		/*if(mobile_number.length()==0){
			editMobileNumber.setError("Please enter mobile number.");
			isRegistrationOk=false;
		} if(!StringUtils.checkMobileNumber(mobile_number)){
			editMobileNumber.setError("Invalid mobile number.");
			isRegistrationOk=false;
		}*/ 
		return isRegistrationOk;
	}
	private void goToLogin(){
		//Intent intent=new Intent(this,LoginActivity.class);
		Intent intent=new Intent(this,LandingLoginActivity.class);
		if(Constants.IS_COMING_FROM_INSIDE){
			intent.putExtra(Constants.LOGIN_REQUIRED, true);	
			intent.putExtra(Constants.LOGIN_REQUIRED, Constants.IS_COMING_FROM_INSIDE);
			intent.putExtra(Constants.CATEGORY_ID, mCategoryId);
			intent.putExtra(Constants.BUSINESS_OR_EVENT_ID, mBusinessOrEventId);
			intent.putExtra(Constants.PAGE_TYPE, mBusinessOrEventType);
			intent.putExtra(Constants.DISTANCE,mDistance);
		}else{
			intent.putExtra(Constants.LOGIN_REQUIRED, false);	
		}
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
		finish();
	}

	
	
	public void sendToDetailScreen(){
		Intent intent =new Intent(this,BusinessDetailsActivity.class);
		intent.putExtra(Constants.LOGIN_REQUIRED, Constants.IS_COMING_FROM_INSIDE);
		intent.putExtra(Constants.CATEGORY_ID, mCategoryId);
		intent.putExtra(Constants.BUSINESS_OR_EVENT_ID, mBusinessOrEventId);
		intent.putExtra(Constants.PAGE_TYPE, mBusinessOrEventType);
		intent.putExtra(Constants.DISTANCE,mDistance);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
		finish();
	}


}
