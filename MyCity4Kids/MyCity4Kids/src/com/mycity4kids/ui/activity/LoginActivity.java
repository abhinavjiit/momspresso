package com.mycity4kids.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.model.GraphUser;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.kelltontech.ui.BaseActivity;
import com.kelltontech.utils.ConnectivityUtils;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.controller.LoginController;
import com.mycity4kids.facebook.FacebookUtils;
import com.mycity4kids.google.GooglePlusUtils;
import com.mycity4kids.interfaces.IFacebookUser;
import com.mycity4kids.interfaces.IPlusClient;
import com.mycity4kids.models.user.UserRequest;
import com.mycity4kids.models.user.UserResponse;
/**
 * 
 * @author "Deepanker Chaudhary"
 *
 */

public class LoginActivity extends BaseActivity implements OnClickListener,IPlusClient,IFacebookUser{
	private GooglePlusUtils mGooglePlusUtils;
	private EditText mEmailId,mPassword;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_activity);
		
		mEmailId=(EditText)findViewById(R.id.email_edit_txt);
		mPassword=(EditText)findViewById(R.id.password_edit_txt);
		((Button)findViewById(R.id.facebook_logout)).setOnClickListener(this);
		((Button)findViewById(R.id.google_plus_btn)).setOnClickListener(this);
		((Button)findViewById(R.id.facebook_btn)).setOnClickListener(this);
		((Button)findViewById(R.id.skip_btn)).setOnClickListener(this);
		((Button)findViewById(R.id.email_login_btn)).setOnClickListener(this);
		((Button)findViewById(R.id.sign_up_btn)).setOnClickListener(this);
		((TextView)findViewById(R.id.forgot_txt)).setOnClickListener(this);
		/**
		 * this is a google plus utility class which will register from here for google plus login
		 */
	    mGooglePlusUtils=new GooglePlusUtils(this,this);
		
	//	ImagePoolLoader loader=ImagePoolLoader.getInstance(this, R.drawable.ic_launcher);
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		if(mGooglePlusUtils!=null)
		mGooglePlusUtils.onStart();
		
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		removeProgressDialog();
	}
	
	
	@Override
	protected void onStop() {
		super.onStop();
		//removeProgressDialog();
		if(mGooglePlusUtils!=null)
			mGooglePlusUtils.onStop();
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}
//	public void loginFacebook(View v){
//		/**
//		 * this function would be required for google plus  login
//		 */
//		mGooglePlusUtils.googlePlusLogin();
//	}
	
	@Override
	protected void onActivityResult(int _requestCode, int _resultCode, Intent _data) {
		super.onActivityResult(_requestCode, _resultCode, _data);//64206,0  -1
		if (_requestCode == GooglePlusUtils.REQUEST_CODE_SIGN_IN){
			mGooglePlusUtils.onActivityResult(this, _requestCode, _resultCode, _data);
			//removeProgressDialog();
		}else{
			if(_resultCode==0)
			{
			removeProgressDialog();
			}
			FacebookUtils.onActivityResult(this, _requestCode, _resultCode, _data);
		}
	
		
	}
	
	

	@Override
	public void onClick(View v) {
		Intent intent=null;
		switch (v.getId()) {
		case R.id.facebook_btn:
			if(ConnectivityUtils.isNetworkEnabled(this)){
			showProgressDialog(getString(R.string.please_wait));
	
			FacebookUtils.facebookLogin(this, this);
			}else{
				showToast(getString(R.string.error_network));
			}
		
			break;

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
			 intent=new Intent(this,RegistrationActivity.class);
			 intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			startActivity(intent);
			finish();
			break;
		case R.id.skip_btn:
			sendToHomeScreen();
			break;
		case R.id.facebook_logout:
			FacebookUtils.logout(this);
			break;
			
		case R.id.google_plus_btn:
			if(ConnectivityUtils.isNetworkEnabled(this)){
		   // showProgressDialog("Please Wait");
			mGooglePlusUtils.googlePlusLogin();
			}else{
				showToast(getString(R.string.error_network));
			}
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
	protected void updateUi(com.kelltontech.network.Response response) {
		if( response==null){
			
			showToast("Content not fetching from server side");
			return;
		}
		
		UserResponse responseData=(UserResponse)response.getResponseObject();
		if(responseData.getResponseCode()==200){
			Toast.makeText(LoginActivity.this,"Login has been successful.", Toast.LENGTH_SHORT).show();
			sendToHomeScreen();
		}else if(responseData.getResponseCode()==400){
			Toast.makeText(LoginActivity.this,R.string.login_failed_please_try_again_, Toast.LENGTH_SHORT).show();

		}
		removeProgressDialog();
		
		
	}
	/**
	 * this is a call back method which will give google plus details:
	 */
	@Override
	public void getGooglePlusInfo(GoogleApiClient plusClient) {
		showProgressDialog(getString(R.string.please_wait));
		final LoginController _controller=new LoginController(this, this);

		if (Plus.PeopleApi.getCurrentPerson(plusClient) != null) {
			Person currentPerson = Plus.PeopleApi.getCurrentPerson(plusClient);
			String currentPersonName = currentPerson.getDisplayName();
			String userId = currentPerson.getId();
			String googleEmailId = Plus.AccountApi.getAccountName(plusClient);
			if (StringUtils.isNullOrEmpty(googleEmailId)) {
				googleEmailId = "Email not fetch from google.";
			}
			if (StringUtils.isNullOrEmpty(currentPersonName)) {
				currentPersonName = getString(R.string.unknown_person);
			}
			if (StringUtils.isNullOrEmpty(userId)) {
				userId = getString(R.string.unknown_person);
			}
//            String personPhoto = currentPerson.getImage().getUrl();
//            String personGooglePlusProfile = currentPerson.getUrl();
			System.out.println(currentPersonName + "user ID" + userId);
			UserRequest _userModel = new UserRequest();
			_userModel.setEmailId(googleEmailId);
			_userModel.setProfileId(userId);
			_userModel.setNetworkName("google");
			//	showProgressDialog("PleaseWait");
			_controller.getData(AppConstants.LOGIN_REQUEST, _userModel);

			//removeProgressDialog();
		}
	}

	@Override
	public void onGooglePlusLoginFailed() {
		//removeProgressDialog();
	}
	
	/**
	 * this is a call back method which will give facebook user details
	 *  
	 */

	@Override
	public void getFacebookUser(GraphUser user) {
		//showProgressDialog(getString(R.string.please_wait));
		final LoginController _controller=new LoginController(this, this);
		//Toast.makeText(LoginActivity.this, user.asMap().get("email").toString(), Toast.LENGTH_LONG).show() ;
    	String fbEmailId=user.asMap().get("email").toString();
		UserRequest _userModel=new UserRequest();
		_userModel.setEmailId(fbEmailId);
		_userModel.setProfileId(user.getId());
		_userModel.setNetworkName("facebook");
		
		_controller.getData(AppConstants.LOGIN_REQUEST, _userModel);
		Log.i("fbUsernameUserId", user.getId()  +" "+ user.getUsername()+" "+user.asMap().get("email"));
		
	}
	private boolean isDataValid() {
		boolean isLoginOk=true;
		String email_id = mEmailId.getText().toString();

		if(email_id.trim().length() ==0 || (!StringUtils.isValidEmail(email_id))){
			mEmailId.setFocusableInTouchMode(true);
			mEmailId.setError("Please enter valid email id");
			mEmailId.requestFocus();
			isLoginOk=false;
		} else if(mPassword.getText().toString().length() == 0 ) {
			mPassword.setFocusableInTouchMode(true);
			mPassword.requestFocus();
			mPassword.setError("Password can't be left blank");
			//mPassword.requestFocus();
			isLoginOk=false;
		}
		else if(mPassword.getText().toString().length() <5 ) {
			mPassword.setFocusableInTouchMode(true);
			mPassword.requestFocus();
				
			mPassword.setError("Password should not less than 5 character.");
			//mPassword.requestFocus();
			isLoginOk=false;
		}
		return isLoginOk;
	}
	
	
}
