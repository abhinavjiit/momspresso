package com.mycity4kids.ui.fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.ui.activity.DashboardActivity;
import com.mycity4kids.ui.videochallengenewui.activity.NewVideoChallengeActivity;
import com.mycity4kids.utils.AppUtils;
import com.mycity4kids.utils.PermissionUtil;
import com.mycity4kids.utils.StringUtils;
import com.mycity4kids.vlogs.VideoCategoryAndChallengeSelectionActivity;

/**
 * Created by user on 08-06-2015.
 */
public class ChooseVideoUploadOptionDialogFragment extends DialogFragment implements OnClickListener {

    private static final int REQUEST_CAMERA_PERMISSION = 1;
    private static final int REQUEST_GALLERY_PERMISSION = 2;

    private String activity;
    private View rootLayout;
    private String duration;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.choose_video_option_dialog, container,
                false);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Utils.pushOpenScreenEvent(getActivity(), "PickVideoScreen",
                SharedPrefUtils.getUserDetailModel(getActivity()).getDynamoId() + "");
        rootLayout = rootView.findViewById(R.id.root);
        Bundle extras = getArguments();
        if (extras != null) {
            activity = extras.getString("activity");
            duration = extras.getString("duration");
        }

        TextView cameraTextView = (TextView) rootView.findViewById(R.id.optionCameraTextView);
        cameraTextView.setOnClickListener(this);
        TextView galleryTextView = (TextView) rootView.findViewById(R.id.optionGalleryTextView);
        galleryTextView.setOnClickListener(this);
        TextView cancelTextView = (TextView) rootView.findViewById(R.id.cancelTextView);
        cancelTextView.setOnClickListener(this);
        TextView timeLimitTextView = (TextView) rootView.findViewById(R.id.timeLimitTextView);

        if (!StringUtils.isNullOrEmpty(duration)) {
            if ("video_category_activity".equals(activity)) {
                timeLimitTextView.setVisibility(View.VISIBLE);
                timeLimitTextView.setText(getString(R.string.time_limit,
                        AppUtils.calculateFormattedTimeLimit(Integer.parseInt(duration)) + getString(
                                R.string.minutes_label)));
                timeLimitTextView
                        .setTextColor(ContextCompat.getColor(BaseApplication.getAppContext(), R.color.app_red));
            } else if ("challengeDetailFragment".equals(activity)) {
                timeLimitTextView.setVisibility(View.VISIBLE);
                timeLimitTextView.setText(getString(R.string.time_limit,
                        AppUtils.calculateFormattedTimeLimit(Integer.parseInt(duration)) + getString(
                                R.string.minutes_label)));
                timeLimitTextView
                        .setTextColor(ContextCompat.getColor(BaseApplication.getAppContext(), R.color.app_red));
            }
        }
        return rootView;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.optionCameraTextView:
                if (isAdded()) {
                    Utils.momVlogEvent(getActivity(), "Video Upload", "Take_video", "N/A", "android",
                            SharedPrefUtils.getAppLocale(BaseApplication.getAppContext()),
                            SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId(),
                            String.valueOf(System.currentTimeMillis()), "Show_camera", "", "");
                }
                if (Build.VERSION.SDK_INT >= 23) {
                    if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                            != PackageManager.PERMISSION_GRANTED
                            ||
                            ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
                                    != PackageManager.PERMISSION_GRANTED
                            || ActivityCompat
                            .checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                        if ("dashboard".equals(activity)) {
                            ((DashboardActivity) getActivity()).requestPermissions("camera");
                        } else if ("video_category_activity".equals(activity)) {
                            ((VideoCategoryAndChallengeSelectionActivity) getActivity()).requestPermissions("camera");
                        } else if ("challengeDetailFragment".equals(activity)) {
                            Utils.shareEventTracking(getActivity(), "Video Challenge", "Vlog_Challenges_Android",
                                    "H_VCD_FinalCTA_Challenge");
                            ((NewVideoChallengeActivity) getActivity()).requestPermissions("camera");
                        }
                    } else {
                        openVideoCapture();
                    }
                } else {
                    openVideoCapture();
                }
                dismiss();
                break;
            case R.id.optionGalleryTextView:
                if (isAdded()) {
                    Utils.momVlogEvent(getActivity(), "Video Upload", "Choose_from_gallery", "N/A", "android",
                            SharedPrefUtils.getAppLocale(BaseApplication.getAppContext()),
                            SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId(),
                            String.valueOf(System.currentTimeMillis()), "Show_gallery", "", "");
                }
                if (Build.VERSION.SDK_INT >= 23) {
                    if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                            != PackageManager.PERMISSION_GRANTED
                            ||
                            ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
                                    != PackageManager.PERMISSION_GRANTED
                            || ActivityCompat
                            .checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                        if ("dashboard".equals(activity)) {
                            ((DashboardActivity) getActivity()).requestPermissions("gallery");
                        } else if ("video_category_activity".equals(activity)) {
                            ((VideoCategoryAndChallengeSelectionActivity) getActivity()).requestPermissions("gallery");
                        } else if ("challengeDetailFragment".equals(activity)) {
                            Utils.shareEventTracking(getActivity(), "Video Challenge", "Vlog_Challenges_Android",
                                    "H_VCD_FinalCTA_Challenge");
                            ((NewVideoChallengeActivity) getActivity()).requestPermissions("gallery");
                        }
                    } else {
                        pickFromGallery();
                    }
                } else {
                    pickFromGallery();
                }
                dismiss();
                break;
            case R.id.cancelTextView:
                dismiss();
                break;
            default:
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
            @NonNull int[] grantResults) {

        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            Log.i("Permissions", "Received response for camera permissions request.");

            if (PermissionUtil.verifyPermissions(grantResults)) {
                Snackbar.make(rootLayout, R.string.permision_available_init,
                        Snackbar.LENGTH_SHORT)
                        .show();
                openVideoCapture();
            } else {
                Log.i("Permissions", "storage permissions were NOT granted.");
                Snackbar.make(rootLayout, R.string.permissions_not_granted,
                        Snackbar.LENGTH_SHORT)
                        .show();
            }

        } else if (requestCode == REQUEST_GALLERY_PERMISSION) {
            Log.i("Permissions", "Received response for storage permissions request.");

            if (PermissionUtil.verifyPermissions(grantResults)) {
                Snackbar.make(rootLayout, R.string.permision_available_init,
                        Snackbar.LENGTH_SHORT)
                        .show();
                pickFromGallery();
            } else {
                Log.i("Permissions", "storage permissions were NOT granted.");
                Snackbar.make(rootLayout, R.string.permissions_not_granted,
                        Snackbar.LENGTH_SHORT)
                        .show();
            }

        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void openVideoCapture() {
        try {
            Intent videoCapture = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            if ("dashboard".equals(activity)) {
                getActivity().startActivityForResult(videoCapture, AppConstants.REQUEST_VIDEO_TRIMMER);
            } else if ("video_category_activity".equals(activity)) {
                getActivity().startActivityForResult(videoCapture, AppConstants.REQUEST_VIDEO_TRIMMER);
            } else if ("vlogslisting".equals(activity)) {
                getActivity().startActivityForResult(videoCapture, AppConstants.REQUEST_VIDEO_TRIMMER);
            } else if ("challengeDetailFragment".equals(activity)) {
                (getActivity()).startActivityForResult(videoCapture, AppConstants.REQUEST_VIDEO_TRIMMER);
            }
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
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
                getActivity()
                        .startActivityForResult(Intent.createChooser(intent, getString(R.string.label_select_video)),
                                AppConstants.REQUEST_VIDEO_TRIMMER);
            } else if ("video_category_activity".equals(activity)) {
                getActivity()
                        .startActivityForResult(Intent.createChooser(intent, getString(R.string.label_select_video)),
                                AppConstants.REQUEST_VIDEO_TRIMMER);
            } else if ("vlogslisting".equals(activity)) {
                getActivity()
                        .startActivityForResult(Intent.createChooser(intent, getString(R.string.label_select_video)),
                                AppConstants.REQUEST_VIDEO_TRIMMER);
            } else if ("challengeDetailFragment".equals(activity)) {
                getActivity()
                        .startActivityForResult(Intent.createChooser(intent, getString(R.string.label_select_video)),
                                AppConstants.REQUEST_VIDEO_TRIMMER);

            }
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            Log.d("MC4kException", Log.getStackTraceString(e));
        }
    }

}
