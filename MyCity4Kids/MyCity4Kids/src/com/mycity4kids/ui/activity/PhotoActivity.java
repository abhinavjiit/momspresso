package com.mycity4kids.ui.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.mycity4kids.R;
import com.mycity4kids.widget.BitmapLruCache;
/**
 * 
 * @author deepanker.chaudhary
 *
 */
public class PhotoActivity extends BaseActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {
			setContentView(R.layout.photo_activity);

			Bundle bundle=getIntent().getExtras();
			if(bundle!=null){
				NetworkImageView photoImg=(NetworkImageView)findViewById(R.id.photo_img);
				ImageView bitmapImg=(ImageView)findViewById(R.id.image_bitmap);
				ImageLoader.ImageCache	imageCache = new BitmapLruCache();
				ImageLoader   imageLoader = new ImageLoader(Volley.newRequestQueue(this), imageCache);
				String imageUrl=bundle.getString("photoUrl");
				 byte[] imageArray = bundle.getByteArray("imageArray");
				if(imageArray!=null && imageUrl==null){
					bitmapImg.setVisibility(View.VISIBLE);
					photoImg.setVisibility(View.GONE);
					Bitmap _bitmap = BitmapFactory.decodeByteArray(imageArray, 0, imageArray.length);
					bitmapImg.setImageBitmap(_bitmap);
				}else if(imageArray==null && imageUrl!=null){
					bitmapImg.setVisibility(View.GONE);
					photoImg.setVisibility(View.VISIBLE);
				photoImg.setImageUrl(imageUrl, imageLoader);
				}
			}
			else{
				showToast(getResources().getString(R.string.went_wrong));
				finish();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}


	}

	@Override
	protected void updateUi(Response response) {
		// TODO Auto-generated method stub

	}

}
