package com.kelltontech.imageloader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v4.util.LruCache;
import android.widget.ImageView;

import com.kelltontech.imageloader.disc.DiskLruCache;
import com.kelltontech.imageloader.disc.Utils;

/**
 * This Class is used to provide image loading possibility
 * 
 * @author shashank.agarwal
 * 
 */
public class ImagePoolLoader implements ILoader {

	private static ImagePoolLoader mPoolLoader;
	private LruCache<String, Bitmap> mCache;
	private List<String> mRejectedList;
	private ImagePool mImagePool;
	private Context mContext;
	private static int DEFAULT_FILE_ID;

	// file write to disk
	private DiskLruCache mDiskCache;
	private CompressFormat mCompressFormat = CompressFormat.JPEG;
	private static final long DISK_CACHE_SIZE = 1024 * 1024 * 10; // 10MB
	
	private int quality = 70 ; 

	private ImagePoolLoader(Context context, int file_id) {
		mRejectedList = new ArrayList<String>();
		mContext = context;
		DEFAULT_FILE_ID = file_id;

		// using cache memory of device
		if (mCache == null) {
			int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
			int cacheSize = maxMemory / 8;
			mCache = new LruCache<String, Bitmap>(cacheSize);
		}
		mImagePool = new ImagePool(5, 10, 10, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(5), new RejectedExecution());
	}
	public static ImagePoolLoader getInstance(Context context, int file_id) {
		if (mPoolLoader == null) {
			mPoolLoader = new ImagePoolLoader(context, file_id);
		}
		return mPoolLoader;
	}

	public synchronized Bitmap getImage(final String URL, final ImageView img) {
		final Bitmap bmp = mCache.get(URL);
		if (bmp == null) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					final Bitmap bmp ;
					String[] arr = URL.split("/") ;
					if(arr.length != 0 ) {
						try {
							File diskCacheDir = getDiskCacheDir(mContext, arr[arr.length - 1].toString());
							mDiskCache = DiskLruCache.open(diskCacheDir, 1 , 1, DISK_CACHE_SIZE );	
							bmp = mDiskCache.getBitmap(arr[arr.length - 1].toString()) ; 
							if(bmp != null ) {
								mCache.put(URL, bmp);
								((Activity)img.getContext()).runOnUiThread(new Runnable() {
									@Override
									public void run() {
										img.setImageBitmap(bmp) ; 
									}
								});
							} else {
								mImagePool.execute(new ImageLoaderHelper(URL, img, ImagePoolLoader.this)) ; 
							}
						} catch (Exception e) {
							
						}
					}
				}
			}).start() ;
			return BitmapFactory.decodeResource(mContext.getResources(), DEFAULT_FILE_ID);
		} else {
			return bmp ; 	
		}
	}

	/**
	 * This function returns all those elements which were failed while loading
	 * data.
	 * 
	 * @return
	 */
	public List<String> getRejectedList() {
		return mRejectedList;
	}

	private class RejectedExecution implements RejectedExecutionHandler {

		@Override
		public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
			mRejectedList.add(r.toString());
		}
	}

	@Override
	public void downloaded(final String url, final Bitmap bmp, final ImageView img) {
		if (bmp != null) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					// saving bitmap to file ....
					try {
						String[] arr = url.split("/") ;
						if(arr.length != 0 ) {
							if(arr[arr.length -1 ].toString().toLowerCase(Locale.getDefault()).contains("png"))
								mCompressFormat = CompressFormat.PNG ; 
							else 
								mCompressFormat = CompressFormat.JPEG ;	
							final File diskCacheDir = getDiskCacheDir(mContext, arr[arr.length - 1].toString());
							mDiskCache = DiskLruCache.open(diskCacheDir, 1 , 1, DISK_CACHE_SIZE );
							mDiskCache.put(arr[arr.length -1 ].toString() , bmp , mCompressFormat , quality );
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}).start() ; 
			// saving bitmap to memory in lru cache ....
			mCache.put(url, bmp);
			((Activity)img.getContext()).runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if(img.getTag().toString().equals(url)){
						img.setImageBitmap(bmp);	
					}
				}
			})  ;
		}
	}

	private File getDiskCacheDir(Context context, String uniqueName) {
		final String cachePath = Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) || !Utils.isExternalStorageRemovable() ? Utils.getExternalCacheDir(context).getPath() : context.getCacheDir().getPath();
		return new File(cachePath + File.separator + uniqueName);
	}
	/**
	 * @return the quality
	 */
	public int getQuality() {
		return quality;
	}
	/**
	 * @param quality the quality to set
	 */
	public void setQuality(int quality) {
		this.quality = quality;
	}
}
