package com.kelltontech.imageloader;

import android.graphics.Bitmap;
import android.widget.ImageView;

public interface ILoader {
	void downloaded(String url, Bitmap bmp, ImageView img_view) ;
}
