package com.mycity4kids.ui.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.kelltontech.utils.ConnectivityUtils;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.controller.UpdateMobileController;
import com.mycity4kids.models.user.UserRequest;
import com.mycity4kids.models.user.UserResponse;
import com.mycity4kids.preference.SharedPrefUtils;

/**
 * Created by hemant on 15/2/16.
 */
public class UpdateMobileNumberActivity extends BaseActivity {

    private Toolbar mToolbar;
    private EditText mobileNumberEditText, nameEditText;
    private TextView colorTextView;
    private ImageView profileImageView;

    private boolean dataValid;

    private String mobileNumber;
    private String isExistingUser = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.social_signup_layout);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Sign Up");

        String fromActivity = getIntent().getStringExtra("activity");
        String name = getIntent().getStringExtra("name");
        String colorCode = getIntent().getStringExtra("colorCode");
        isExistingUser = getIntent().getStringExtra("isExistingUser");

        mobileNumberEditText = (EditText) findViewById(R.id.spouse_mobile);
        nameEditText = (EditText) findViewById(R.id.spouse_name);
        colorTextView = (TextView) findViewById(R.id.color_spouse);
        profileImageView = (ImageView) findViewById(R.id.profile_image);

        nameEditText.setText(name);

        String cc = "";
        switch (colorCode) {
            case "#ff8a65":
                cc = "1";
                break;
            case "#ef5350":
                cc = "2";
                break;
            case "#ff1744":
                cc = "3";
                break;
            case "#d81b60":
                cc = "4";
                break;
            case "#ab47bc":
                cc = "5";
                break;
            case "#7e57c2":
                cc = "6";
                break;
            case "#3949ab":
                cc = "7";
                break;
            case "#42a5f5":
                cc = "8";
                break;
            case "#00acc1":
                cc = "9";
                break;
            case "#26a69a":
                cc = "10";
                break;
            default:
                cc = "1";
                break;

        }

        Drawable drawable = getResources().getDrawable(getResources()
                .getIdentifier("color_" + cc + "xxhdpi", "drawable", getPackageName()));
        colorTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, null, drawable);
        if ("1".equals(isExistingUser)) {
            getSupportActionBar().setTitle("Add Mobile");
            nameEditText.setEnabled(false);
            colorTextView.setEnabled(false);
            profileImageView.setEnabled(false);
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
                if (isDataValid()) {
                    if (ConnectivityUtils.isNetworkEnabled(this)) {
                        showProgressDialog(getString(R.string.please_wait));
                        //mProgressDialog=ProgressDialog.show(this, "", "Please Wait...",true,false);

                        showProgressDialog(getString(R.string.please_wait));
                        UserRequest _requestModel = new UserRequest();
                        _requestModel.setUserId("" + SharedPrefUtils.getUserDetailModel(this).getId());
                        _requestModel.setMobileNumber(mobileNumber);
                        UpdateMobileController _controller = new UpdateMobileController(this, this);
                        _controller.getData(AppConstants.UPDATE_MOBILE_FOR_EXISTING_USER_REQUEST, _requestModel);
                    } else {
                        showToast(getString(R.string.error_network));
                    }
                }

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void updateUi(Response response) {
        if (response == null) {
            removeProgressDialog();
            showToast(getResources().getString(R.string.server_error));
            return;
        }
        switch (response.getDataType()) {
            case AppConstants.UPDATE_MOBILE_FOR_EXISTING_USER_REQUEST:
                UserResponse responseData = (UserResponse) response.getResponseObject();
                String message = responseData.getResult().getMessage();
                removeProgressDialog();
                if (responseData.getResponseCode() == 200) {
                    Intent intent = new Intent(this, ActivityVerifyOTP.class);
                    intent.putExtra("email", SharedPrefUtils.getUserDetailModel(this).getEmail());
                    intent.putExtra("mobile", mobileNumber);
                    intent.putExtra("isExistingUser", isExistingUser);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } else if (responseData.getResponseCode() == 400) {
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    public boolean isDataValid() {

        mobileNumber = mobileNumberEditText.getText().toString();
        if (StringUtils.isNullOrEmpty(mobileNumber)) {
            mobileNumberEditText.setFocusableInTouchMode(true);
            mobileNumberEditText.setError("Mobile number cannot be empty");
            mobileNumberEditText.requestFocus();
            return false;
        } else if (!StringUtils.checkMobileNumber(mobileNumberEditText.getText().toString())) {
            mobileNumberEditText.setFocusableInTouchMode(true);
            mobileNumberEditText.setError("Please enter a valid mobile number");
            mobileNumberEditText.requestFocus();
            return false;
        }
        return true;
    }
}
