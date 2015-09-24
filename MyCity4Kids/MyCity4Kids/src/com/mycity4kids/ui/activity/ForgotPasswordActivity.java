package com.mycity4kids.ui.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
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
import com.mycity4kids.controller.ForgotPasswordController;
import com.mycity4kids.models.forgot.CommonResponse;

public class ForgotPasswordActivity extends BaseActivity {
    private EditText mEmailId;
    private ForgotPasswordController _controller;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aa_forgotpswd);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mEmailId = (EditText) findViewById(R.id.editEmail);
        _controller = new ForgotPasswordController(this, this);

        getSupportActionBar().setTitle("Forgot Family Password");

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
                    if (!ConnectivityUtils
                            .isNetworkEnabled(ForgotPasswordActivity.this)) {
                        ToastUtils.showToast(ForgotPasswordActivity.this,
                                getString(R.string.error_network));
                    } else {
                        showProgressDialog(getString(R.string.please_wait));
                        _controller.getData(AppConstants.FORGOT_REQUEST, mEmailId
                                .getText().toString().trim());
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
            showSnackbar(findViewById(R.id.root), getResources().getString(R.string.server_error));
            return;
        }
        switch (response.getDataType()) {
            case AppConstants.FORGOT_REQUEST:
                removeProgressDialog();
                CommonResponse responseData = (CommonResponse) response.getResponseObject();
                String message = responseData.getResult().getMessage();
                if (responseData.getResponseCode() == 200) {

                    //showSnackbar(findViewById(R.id.root), message);
                    AlertDialog.Builder dialog = new AlertDialog.Builder(this);

                    dialog.setMessage(message + "").setNegativeButton(android.R.string.yes
                            , new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.cancel();
                            Intent intent = new Intent(ForgotPasswordActivity.this, ActivityLogin.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            startActivity(intent);


                        }
                    }).setIcon(android.R.drawable.ic_dialog_alert).show();
//                    Toast.makeText(this,message,Toast.LENGTH_LONG).show();


                } else if (responseData.getResponseCode() == 400) {

                    showSnackbar(findViewById(R.id.root), message);


                }
                break;

            default:
                break;
        }

    }

    private boolean isDataValid() {
        boolean isForgotOk = true;
        String email_id = mEmailId.getText().toString().trim();

        if (email_id.trim().length() == 0
                || (!StringUtils.isValidEmail(email_id))) {
            mEmailId.setError("Please enter a valid email address");
            mEmailId.requestFocus();
            isForgotOk = false;
        }
        return isForgotOk;
    }


}
