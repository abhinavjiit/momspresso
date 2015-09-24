package com.mycity4kids.ui.activity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.mycity4kids.R;
import com.mycity4kids.newmodels.AppoitmentDataModel;
import com.mycity4kids.newmodels.TaskDataModel;
import com.mycity4kids.ui.adapter.ImagesViewPagerAdapter;

import java.util.ArrayList;

/**
 * Created by khushboo.goyal on 01-07-2015.
 */
public class ActivityViewPager extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.view_pager);

        ViewPager gridGallery = (ViewPager) findViewById(R.id.myviewpager);

        Bundle bundle = getIntent().getExtras();
        boolean isFromAppointment = bundle.getBoolean("isfrmAppointment");
        int position = bundle.getInt("position");
        ArrayList<AppoitmentDataModel.Files> _ImagesAraryList;
        ArrayList<TaskDataModel.Files> _TaskImagesAraryList;
        ImagesViewPagerAdapter mPhotosAdapter;

        if (isFromAppointment) {
            _ImagesAraryList = bundle.getParcelableArrayList("imagelist");
          // _ImagesAraryList = (ArrayList<AppoitmentDataModel.Files>) bundle.getSerializable("imagelist");
            mPhotosAdapter = new ImagesViewPagerAdapter(this, _ImagesAraryList, null, true);
        } else {
            _TaskImagesAraryList = bundle.getParcelableArrayList("imagelist");
            //_TaskImagesAraryList = (ArrayList<TaskDataModel.Files>) bundle.getSerializable("imagelist");
            mPhotosAdapter = new ImagesViewPagerAdapter(this, null, _TaskImagesAraryList, false);
        }


        gridGallery.setAdapter(mPhotosAdapter);
        gridGallery.setCurrentItem(position);
    }

    @Override
    protected void updateUi(Response response) {

    }
}
