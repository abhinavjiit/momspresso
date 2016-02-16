package com.mycity4kids.ui.activity;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.mycity4kids.R;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.preference.SharedPrefUtils;

/**
 * Created by khushboo.goyal on 19-06-2015.
 */
public class JoinFamilyActivity extends BaseActivity implements View.OnClickListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.pushOpenScreenEvent(JoinFamilyActivity.this, "Join Family Dashboard", SharedPrefUtils.getUserDetailModel(this).getId() + "");

        setContentView(R.layout.aa_joinfamily);

        TextView login = (TextView) findViewById(R.id.email_login_btn);
        login.setPaintFlags(login.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        findViewById(R.id.craete_family).setOnClickListener(this);
        findViewById(R.id.join_family).setOnClickListener(this);
        findViewById(R.id.email_login_btn).setOnClickListener(this);

    }

    @Override
    protected void updateUi(Response response) {

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.craete_family:
                startActivity(new Intent(this, LandingLoginActivity.class));
                break;


            case R.id.join_family:
                Intent i = new Intent(this, ActivityLogin.class);
                i.putExtra("frmJoinFamily", true);
                startActivity(i);
                break;

            case R.id.email_login_btn:
                startActivity(new Intent(this, ActivityLogin.class));
                break;
        }

    }
}
