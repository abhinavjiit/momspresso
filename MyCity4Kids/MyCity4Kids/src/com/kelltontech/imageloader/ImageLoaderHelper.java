package com.kelltontech.imageloader;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;


public class ImageLoaderHelper implements Runnable {

	private ILoader iLoader ; 
	private String imageURL = "" ; 
	private ImageView img_view ;
	
	public ImageLoaderHelper(String url , ImageView img , ILoader loader ) {
		imageURL = url  ;
		iLoader = loader;
		img_view = img ;
	}
	
	@Override
	public void run() {
		try {
	        URL url = new URL(imageURL);
	        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	        connection.setDoInput(true);
	        connection.connect();
	        InputStream input = connection.getInputStream();
	        Bitmap myBitmap = BitmapFactory.decodeStream(input);
	        iLoader.downloaded(imageURL, myBitmap , img_view ) ;
	        
	    } catch (IOException e) {
	        e.printStackTrace();
	        Log.e("getBmpFromUrl error: ", e.getMessage().toString());
	    }	
	}
}
