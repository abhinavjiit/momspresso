package com.mycity4kids.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.mycity4kids.R;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.preference.SharedPrefUtils;

/**
 * Created by hemant on 18/12/15.
 */
public class KidsBirthdayActivity extends BaseActivity {

    private TextView kidsBirthdayMsgTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.pushOpenScreenEvent(KidsBirthdayActivity.this, "Kid's Birthday", SharedPrefUtils.getUserDetailModel(this).getId() + "");

        setContentView(R.layout.kids_birthday_layout);
        kidsBirthdayMsgTextView = (TextView) findViewById(R.id.birthdayMsgTextView);
        ImageView closeButton = (ImageView) findViewById(R.id.closeButton);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        String kidsName = getIntent().getStringExtra(Constants.KIDS_NAME);

        kidsBirthdayMsgTextView.setText("Congratulations and Best wishes on " + kidsName + "'s special day");
    }

    @Override
    protected void updateUi(Response response) {

    }
}
