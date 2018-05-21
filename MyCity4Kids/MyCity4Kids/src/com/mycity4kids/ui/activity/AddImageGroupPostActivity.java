package com.mycity4kids.ui.activity;

import android.os.Bundle;

import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.mycity4kids.R;

/**
 * Created by hemant on 24/4/18.
 */

public class AddImageGroupPostActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_text_group_post_activity);
    }

    @Override
    protected void updateUi(Response response) {

    }
}
