package com.mycity4kids.utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Environment;

import com.facebook.share.model.ShareHashtag;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.kelltontech.ui.BaseFragment;
import com.mycity4kids.constants.AppConstants;

import java.util.ArrayList;

public class SharingUtils {

    public static Bitmap getRoundCornerBitmap(Bitmap bitmap, int radius) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        Bitmap output = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        final RectF rectF = new RectF(0, 0, w, h);
        canvas.drawRoundRect(rectF, radius, radius, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, null, rectF, paint);

        /**
         * here to define your corners, this is for left bottom and right bottom corners
         */
        final Rect clipRect = new Rect(0, radius, w, h);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
        canvas.drawRect(clipRect, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, null, rectF, paint);
        return output;
    }

    public static void shareViaFacebook(Activity activity) {
        Uri uri = Uri.parse("file://" + Environment.getExternalStorageDirectory() +
                "/MyCity4Kids/videos/" + AppConstants.STORY_SHARE_IMAGE_NAME + ".jpg");
        ShareHashtag shareHashTag = new ShareHashtag.Builder().setHashtag("#Momspressoshortstories").build();
        SharePhoto sharePhoto = new SharePhoto.Builder().setImageUrl(uri).build();
        ArrayList<SharePhoto> photoList = new ArrayList<>();
        photoList.add(sharePhoto);
        SharePhotoContent shareLinkContent = new SharePhotoContent.Builder()
                .setShareHashtag(shareHashTag)
                .setPhotos(photoList)
                .build();
        new ShareDialog(activity).show(shareLinkContent);
    }

    public static void shareViaFacebook(BaseFragment fragment) {
        Uri uri = Uri.parse("file://" + Environment.getExternalStorageDirectory() +
                "/MyCity4Kids/videos/" + AppConstants.STORY_SHARE_IMAGE_NAME + ".jpg");
        ShareHashtag shareHashTag = new ShareHashtag.Builder().setHashtag("#Momspressoshortstories").build();
        SharePhoto sharePhoto = new SharePhoto.Builder().setImageUrl(uri).build();
        ArrayList<SharePhoto> photoList = new ArrayList<>();
        photoList.add(sharePhoto);
        SharePhotoContent shareLinkContent = new SharePhotoContent.Builder()
                .setShareHashtag(shareHashTag)
                .setPhotos(photoList)
                .build();
        new ShareDialog(fragment).show(shareLinkContent);
    }
}
