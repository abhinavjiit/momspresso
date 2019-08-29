package com.mycity4kids.ui.fragment;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.fragment.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.utils.GenericFileProvider;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by user on 08-06-2015.
 */
public class ChoosePostMediaDialogFragment extends DialogFragment implements OnClickListener {

    private static final int REQUEST_CAMERA_PERMISSION = 1;
    private static final int REQUEST_GALLERY_PERMISSION = 2;

    public static final int ADD_IMAGE_GALLERY_ACTIVITY_REQUEST_CODE = 1111;
    public static final int ADD_IMAGE_CAMERA_ACTIVITY_REQUEST_CODE = 1113;

    private static String[] PERMISSIONS_STORAGE_CAMERA = {Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};

    private File photoFile;
    private String activity;
    private View rootLayout;
    private String mCurrentPhotoPath, absoluteImagePath;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.choose_post_media_option_dialog, container,
                false);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Utils.pushOpenScreenEvent(getActivity(), "PickMediaScreen", SharedPrefUtils.getUserDetailModel(getActivity()).getDynamoId() + "");
        Bundle extras = getArguments();
        if (extras != null) {
            activity = extras.getString("activity");
        }

        TextView imageCameraTextView = (TextView) rootView.findViewById(R.id.imageCameraTextView);
        TextView imageGalleryTextView = (TextView) rootView.findViewById(R.id.imageGalleryTextView);
        TextView videoCameraTextView = (TextView) rootView.findViewById(R.id.videoCameraTextView);
        TextView videoGalleryTextView = (TextView) rootView.findViewById(R.id.videoGalleryTextView);

        TextView cancelTextView = (TextView) rootView.findViewById(R.id.cancelTextView);
        rootLayout = rootView.findViewById(R.id.root);

        imageCameraTextView.setOnClickListener(this);
        imageGalleryTextView.setOnClickListener(this);
        videoCameraTextView.setOnClickListener(this);
        videoGalleryTextView.setOnClickListener(this);
        cancelTextView.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imageCameraTextView:
                loadImageFromCamera();
                break;
            case R.id.imageGalleryTextView:
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, ADD_IMAGE_GALLERY_ACTIVITY_REQUEST_CODE);
                break;
            case R.id.videoCameraTextView:
                openVideoCapture();
                dismiss();
                break;
            case R.id.videoGalleryTextView:
                pickFromGallery();
                dismiss();
                break;
            case R.id.cancelTextView:
                dismiss();
                break;
        }
    }

    private void loadImageFromCamera() {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
            photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.i("TAG", "IOException");
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                try {
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, GenericFileProvider.getUriForFile(getActivity(), BaseApplication.getAppContext().getPackageName() + ".my.package.name.provider", createImageFile()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                startActivityForResult(cameraIntent, ADD_IMAGE_CAMERA_ACTIVITY_REQUEST_CODE);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        File image = File.createTempFile(
                imageFileName,  // prefix
                ".jpg",         // suffix
                dir      // directory
        );

        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        absoluteImagePath = image.getAbsolutePath();
        return image;
    }

    private void openVideoCapture() {
        try {
            Intent videoCapture = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            if ("dashboard".equals(activity)) {
                getActivity().startActivityForResult(videoCapture, AppConstants.REQUEST_VIDEO_TRIMMER);
            } else if ("myfunnyvideos".equals(activity)) {
                getActivity().startActivityForResult(videoCapture, AppConstants.REQUEST_VIDEO_TRIMMER);
            } else if ("vlogslisting".equals(activity)) {
                getActivity().startActivityForResult(videoCapture, AppConstants.REQUEST_VIDEO_TRIMMER);
            } else if ("allvideosection".equals(activity)) {
            }
        } catch (Exception e) {
            Crashlytics.logException(e);
            Log.d("MC4kException", Log.getStackTraceString(e));
        }
    }

    private void pickFromGallery() {
        try {
            Intent intent = new Intent();
            intent.setType("video/mp4");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            if ("dashboard".equals(activity)) {
                getActivity().startActivityForResult(Intent.createChooser(intent, getString(R.string.label_select_video)), AppConstants.REQUEST_VIDEO_TRIMMER);
            } else if ("myfunnyvideos".equals(activity)) {
                getActivity().startActivityForResult(Intent.createChooser(intent, getString(R.string.label_select_video)), AppConstants.REQUEST_VIDEO_TRIMMER);
            } else if ("vlogslisting".equals(activity)) {
                getActivity().startActivityForResult(Intent.createChooser(intent, getString(R.string.label_select_video)), AppConstants.REQUEST_VIDEO_TRIMMER);
            } else if ("allvideosection".equals(activity)) {
            }
        } catch (Exception e) {
            Crashlytics.logException(e);
            Log.d("MC4kException", Log.getStackTraceString(e));
        }
    }

}