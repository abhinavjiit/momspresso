package com.mycity4kids.utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;

import com.kelltontech.utils.BitmapUtils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.URI;

public class UploadFileUtils {

	private Activity	mActivity;
	int					count	= 0;

	public UploadFileUtils(Activity pActivity) {

		mActivity = pActivity;

	}

	public String gettimeSatmp() {
		Long tsLong = System.currentTimeMillis() / 1000;
		String ts = tsLong.toString();
		return ts;
	}

	public String uploadImage(String picturePath, String name, String id) {

		try {
			MultipartEntity multipartEntity = new MultipartEntity();

			multipartEntity.addPart("type", new StringBody("text/plain"));
			multipartEntity.addPart("", new StringBody("image/png"));
			multipartEntity.addPart("", new StringBody("image/png"));
			multipartEntity.addPart("", new StringBody("image/png"));


			// convert path into bitmap
			Bitmap bitmap = null;
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inPreferredConfig = Bitmap.Config.ARGB_8888;

			if (!picturePath.isEmpty()) {

				HttpClient httpClient = new DefaultHttpClient();

				HttpPost httpPost = new HttpPost();
				httpPost.setURI(new URI(""));
//				// set headers
//				httpPost.setHeader(ApiConstants.PARAM_ID, SharedPrefrenceUtils.getSharedPrefString(SociabileApplication.getContext(), ApiConstants.PARAM_USERID, ""));
//				httpPost.setHeader(ApiConstants.PARAM_TOKEN, "" + SharedPrefrenceUtils.getSharedPrefString(SociabileApplication.getContext(), ApiConstants.PARAM_TOKEN, ""));
//				httpPost.setHeader(ApiConstants.PARAM_OS, ApiConstants.PARAM_OS_NAME);
//				httpPost.setHeader(ApiConstants.PARAM_VERSION, SociabileApplication.getInstance().getVersionName());

				// according to loop
				File file = null;


					file = new File(new URI("file://" + picturePath.replaceAll(" ", "%20")));
					int maxImageSize = BitmapUtils.getMaxSize(mActivity);
					Bitmap sourceBitmap = BitmapUtils.getScaledBitmap(file, maxImageSize);

					ExifInterface exif = new ExifInterface(file.getPath());
					int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

					Matrix matrix = new Matrix();
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

					bitmap = Bitmap.createBitmap(sourceBitmap, 0, 0, sourceBitmap.getWidth(), sourceBitmap.getHeight(), matrix, true);

					ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

					bitmap.compress(CompressFormat.PNG, 75, byteArrayOutputStream);
					byte[] byteData = byteArrayOutputStream.toByteArray();

					//ByteArrayBody byteArrayBody = new ByteArrayBody(byteData, Constants.getMY_NAME() + count++ + "_" + gettimeSatmp() + ".png");
					//multipartEntity.addPart("images[]", byteArrayBody);



				httpPost.setEntity(multipartEntity);

				HttpResponse httpresponse = httpClient.execute(httpPost);
				if (httpresponse != null) {

					if (httpresponse.getStatusLine().getStatusCode() != 200) {
						return "";
					} else {
						HttpEntity resEntity = httpresponse.getEntity();
						final String string = EntityUtils.toString(resEntity);
						System.out.println(string);
						return string;

					}

				}

			}
		} catch (Exception e) {

			System.out.println("eror " + e.getMessage());
		}
		return "";

	}
}