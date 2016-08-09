package com.mycity4kids.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.models.request.UpdateUserDetail;
import com.mycity4kids.models.response.UserDetailResponse;
import com.mycity4kids.retrofitAPIsInterfaces.UserAttributeUpdateAPI;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by anshul on 7/29/16.
 */
public class EditProfieActivity extends BaseActivity {
    @Override
    protected void updateUi(Response response) {
    }

    Toolbar mToolBar;
    String bio, firstName, lastName,phoneNumber;
    EditText editFirstName, editLastName, userBioEditText, phoneEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile);
        mToolBar = (Toolbar) findViewById(R.id.toolbar);
        editFirstName = (EditText) findViewById(R.id.editFirstName);
        editLastName = (EditText) findViewById(R.id.editLastName);
        userBioEditText = (EditText) findViewById(R.id.userBioEditText);
        phoneEditText = (EditText) findViewById(R.id.phoneEditText);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Edit Profile");
        bio = getIntent().getStringExtra("bio");
        firstName = getIntent().getStringExtra("firstName");
        lastName = getIntent().getStringExtra("lastName");
        phoneNumber=getIntent().getStringExtra("phoneNumber");
        editFirstName.setText(firstName);
        editLastName.setText(lastName);
        userBioEditText.setText(bio);
        phoneEditText.setText(phoneNumber);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.forgot_password, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.save:
                UpdateUserDetail updateUserDetail = new UpdateUserDetail();
                updateUserDetail.setFirstName((editFirstName.getText().toString()) + " ");
                updateUserDetail.setLastName(editLastName.getText().toString() + " ");
                updateUserDetail.setPhoneNumber(phoneEditText.getText().toString() + " ");
                updateUserDetail.setUserBio(userBioEditText.getText().toString() + " ");
                Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
                showProgressDialog(getResources().getString(R.string.please_wait));
                UserAttributeUpdateAPI userAttributeUpdateAPI = retrofit.create(UserAttributeUpdateAPI.class);
                Call<UserDetailResponse> call = userAttributeUpdateAPI.updateProfile(updateUserDetail);
                call.enqueue(new Callback<UserDetailResponse>() {
                    @Override
                    public void onResponse(Call<UserDetailResponse> call, retrofit2.Response<UserDetailResponse> response) {
                        removeProgressDialog();
                        if (!response.body().getStatus().equals("success")) {
                            showToast(getString(R.string.toast_response_error));
                        } else {
                            showToast("Successfully updated!");
                        }
                    }

                    @Override
                    public void onFailure(Call<UserDetailResponse> call, Throwable t) {
removeProgressDialog();
                    }
                });

                return true;
            case android.R.id.home:
                finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}