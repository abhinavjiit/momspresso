package com.mycity4kids.ui.adapter;

import java.io.File;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import androidx.viewpager.widget.PagerAdapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.kelltontech.utils.BitmapUtils;
import com.mycity4kids.models.businesseventdetails.GalleryListtingData;
import com.mycity4kids.widget.BitmapLruCache;

public class ImageAdapter extends PagerAdapter {
	private Context mContext;
	private ArrayList<GalleryListtingData> mPhotoList;
	ImageLoader.ImageCache imageCache;
	ImageLoader imageLoader;


	public ImageAdapter(Context pContext,ArrayList<GalleryListtingData> photoList){
		mContext=pContext;
		mPhotoList=photoList;
		imageCache = new BitmapLruCache();
		imageLoader = new ImageLoader(Volley.newRequestQueue(pContext), imageCache);

	}

	@Override
	public int getCount() {
		return mPhotoList.size();
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == object;
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {

		NetworkImageView networkImageView;
		ImageView imageView ;
		if(!mPhotoList.get(position).getImagePath().equals("")){
			imageView = new ImageView(mContext);
			imageView.setImageBitmap(getImageBitmap(mPhotoList.get(position).getImagePath()));
			container.addView(imageView, 0);
			return imageView;
		}else{
			networkImageView =new NetworkImageView(mContext);
			networkImageView.setImageUrl(mPhotoList.get(position).getImageUrl(), imageLoader);
			container.addView(networkImageView, 0);
			return networkImageView;
		}
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((ImageView) object);
	}
	private Bitmap getImageBitmap(String path){
		Bitmap sourceBitmap = null;
		Matrix matrix = null;
		try {
			File file = new File(path);
			int maxImageSize = BitmapUtils.getMaxSize(mContext);
			 sourceBitmap = BitmapUtils.getScaledBitmap(file, maxImageSize);

			ExifInterface exif = new ExifInterface(path);
			int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,ExifInterface.ORIENTATION_NORMAL);

			//	Log.e(TAG, "oreination" + orientation);
			 matrix = new Matrix();
			switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				matrix.postRotate(90);
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				matrix.postRotate(180);
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				matrix.postRotate(270);
				break;
			}
		} catch (Exception e) {
			// TODO: handle exception
		}


		Bitmap originalImage = Bitmap.createBitmap(sourceBitmap, 0, 0, sourceBitmap.getWidth(),
				sourceBitmap.getHeight(), matrix, true);
		return originalImage;
	}
}
