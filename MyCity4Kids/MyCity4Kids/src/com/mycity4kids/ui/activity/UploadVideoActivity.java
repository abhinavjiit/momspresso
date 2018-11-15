package com.mycity4kids.ui.activity;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.mycity4kids.R;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.ui.fragment.ChooseVideoUploadOptionDialogFragment;

public class UploadVideoActivity extends BaseActivity implements View.OnClickListener {
    private TextView getStartedTextView;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_video);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        getStartedTextView = (TextView) findViewById(R.id.getStartedTextView);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getStartedTextView.setOnClickListener(this);
    }

    @Override
    protected void updateUi(Response response) {

    }

    //@Override
    // protected void updateUi(Response response) {

    //}

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.getStartedTextView:
                SharedPrefUtils.setFirstVideoUploadFlag(this, true);
                ChooseVideoUploadOptionDialogFragment chooseVideoUploadOptionDialogFragment = new ChooseVideoUploadOptionDialogFragment();
                FragmentManager fm = getSupportFragmentManager();
                Bundle _args = new Bundle();
                _args.putString("activity", "dashboard");
                chooseVideoUploadOptionDialogFragment.setArguments(_args);
                chooseVideoUploadOptionDialogFragment.setCancelable(true);
                chooseVideoUploadOptionDialogFragment.show(fm, "Choose video option");

        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
