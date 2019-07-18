package com.mycity4kids.ui.activity;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.models.businesseventdetails.GalleryListtingData;
import com.mycity4kids.ui.adapter.ImageAdapter;

public class PhotoSlideActivity extends BaseActivity{

	private LinearLayout root;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gallery_pager);
		root = findViewById(R.id.root);
		((BaseApplication) getApplication()).setView(root);
		((BaseApplication) getApplication()).setActivity(this);
		ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
		
		Bundle bundle=getIntent().getExtras();
		if(bundle!=null){
			ArrayList<GalleryListtingData> listingData=getIntent().getExtras().getParcelableArrayList("photoList");
			if(listingData!=null && listingData.size()>0){
				listingData.remove(0);
				int position =getIntent().getExtras().getInt("position");
				  ImageAdapter adapter = new ImageAdapter(this,listingData);
			      viewPager.setAdapter(adapter);
			      viewPager.setCurrentItem(position-1);
			}
			
				
		}
		
		findViewById(R.id.cross_icon).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
				
			}
		});
		
	}

	@Override
	protected void updateUi(Response response) {
		// TODO Auto-generated method stub
		
	}

}
