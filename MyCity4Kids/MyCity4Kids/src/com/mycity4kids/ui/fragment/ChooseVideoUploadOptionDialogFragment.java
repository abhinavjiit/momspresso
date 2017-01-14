package com.mycity4kids.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.mycity4kids.R;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.ui.activity.DashboardActivity;

/**
 * Created by user on 08-06-2015.
 */
public class ChooseVideoUploadOptionDialogFragment extends DialogFragment implements OnClickListener {

    //    private static final int REQUEST_VIDEO_TRIMMER = 0x01;
    static final String EXTRA_VIDEO_PATH = "EXTRA_VIDEO_PATH";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.choose_video_option_dialog, container,
                false);

        TextView cameraTextView = (TextView) rootView.findViewById(R.id.optionCameraTextView);
        TextView galleryTextView = (TextView) rootView.findViewById(R.id.optionGalleryTextView);
        TextView cancelTextView = (TextView) rootView.findViewById(R.id.cancelTextView);

        cameraTextView.setOnClickListener(this);
        galleryTextView.setOnClickListener(this);
        cancelTextView.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.optionCameraTextView:
                openVideoCapture();
                dismiss();
                break;
            case R.id.optionGalleryTextView:
                pickFromGallery();
                dismiss();
                break;
            case R.id.cancelTextView:
                dismiss();
                break;
        }

    }

    private void openVideoCapture() {
        try {
            Intent videoCapture = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            ((DashboardActivity) getActivity()).startActivityForResult(videoCapture, AppConstants.REQUEST_VIDEO_TRIMMER);
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
            ((DashboardActivity) getActivity()).startActivityForResult(Intent.createChooser(intent, getString(R.string.label_select_video)), AppConstants.REQUEST_VIDEO_TRIMMER);
        } catch (Exception e) {
            Crashlytics.logException(e);
            Log.d("MC4kException", Log.getStackTraceString(e));
        }
    }

//    private void startTrimActivity(@NonNull Uri uri) {
//        Intent intent = new Intent(getActivity(), VideoTrimmerActivity.class);
//        intent.putExtra(EXTRA_VIDEO_PATH, FileUtils.getPath((DashboardActivity) getActivity(), uri));
//        startActivity(intent);
//    }

}