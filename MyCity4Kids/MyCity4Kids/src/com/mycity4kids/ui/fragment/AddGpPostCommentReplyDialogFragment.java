package com.mycity4kids.ui.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.kelltontech.utils.DateTimeUtils;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.filechooser.com.ipaulpro.afilechooser.utils.FileUtils;
import com.mycity4kids.models.response.GroupPostCommentResult;
import com.mycity4kids.models.response.ImageUploadResponse;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.ImageUploadAPI;
import com.mycity4kids.ui.activity.AddTextOrMediaGroupPostActivity;
import com.mycity4kids.ui.activity.GroupPostDetailActivity;
import com.mycity4kids.ui.activity.NewsLetterWebviewActivity;
import com.mycity4kids.ui.adapter.GroupsGenericPostRecyclerAdapter;
import com.mycity4kids.utils.GenericFileProvider;
import com.mycity4kids.utils.PermissionUtil;
import com.squareup.picasso.Picasso;
import com.yalantis.ucrop.UCrop;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

import static android.app.Activity.RESULT_OK;
import static com.mycity4kids.ui.adapter.BusinessListingAdapterevent.PERMISSIONS_INIT;
import static com.mycity4kids.ui.adapter.BusinessListingAdapterevent.REQUEST_INIT_PERMISSION;
import static com.mycity4kids.ui.fragment.ChoosePostMediaDialogFragment.ADD_IMAGE_CAMERA_ACTIVITY_REQUEST_CODE;
import static com.mycity4kids.ui.fragment.ChoosePostMediaDialogFragment.ADD_IMAGE_GALLERY_ACTIVITY_REQUEST_CODE;

/**
 * Created by user on 08-06-2015.
 */
public class AddGpPostCommentReplyDialogFragment extends DialogFragment implements OnClickListener {

    private ProgressDialog mProgressDialog;
    private static String[] PERMISSIONS_INIT = {Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};

//    private static final String SAMPLE_CROPPED_IMAGE_NAME = "SampleCropImage";

    private static final int REQUEST_INIT_PERMISSION = 1;
    private HashMap<ImageView, String> imageUrlHashMap = new HashMap<>();
    public static final int ADD_IMAGE_GALLERY_ACTIVITY_REQUEST_CODE = 1111;
    private Map<String, String> mediaMap = new HashMap<>();
    public static final int ADD_IMAGE_CAMERA_ACTIVITY_REQUEST_CODE = 1112;
    private boolean isRequestRunning = false;
    private ImageView closeImageView;
    private TextView addCommentTextView;
    private EditText commentReplyEditText;
    ImageView media;
    TextView replyCountTextViewmedia;
    TextView commentdatetextviewmedia;
    private TextView replyToTextView;
    private Uri imageUri;
    private File photoFile;
    private View separator;
    private String mCurrentPhotoPath, absoluteImagePath;
    private ImageView postImageView;
    private LinearLayout mediaContainer;
    private GroupPostCommentResult commentOrReplyData;
    private TextView imageCameraTextView, imageGalleryTextView, cancelTextView;
    private String actionType;
    private RelativeLayout chooseMediaTypeContainer;
    private int position;
    private TextView headingTextView;
    private ImageView addMediaImageView;
    private RelativeLayout relativeMainContainer;
    private ImageView commentorImageView;
    private TextView commentorUsernameTextView;
    private TextView commentDataTextView;
    private TextView commentDateTextView;
    private ImageView anonymousImageView;
    private TextView anonymousTextView;
    private CheckBox anonymousCheckbox;
    private View bottombarTopline;
    private TextView addMediaTextView;
    private View mLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.add_gp_post_comment_reply_fragment, container,
                false);
        media = (ImageView) rootView.findViewById(R.id.media);
        commentdatetextviewmedia = (TextView) rootView.findViewById(R.id.commentDateTextViewmedia);
        mLayout = rootView.findViewById(R.id.rootLayout);
        addMediaImageView = (ImageView) rootView.findViewById(R.id.addMediaImageView);
        addMediaTextView = (TextView) rootView.findViewById(R.id.addMediaTextView);
        closeImageView = (ImageView) rootView.findViewById(R.id.closeImageView);
        addCommentTextView = (TextView) rootView.findViewById(R.id.postCommentReplyTextView);
        commentReplyEditText = (EditText) rootView.findViewById(R.id.commentReplyEditText);
        headingTextView = (TextView) rootView.findViewById(R.id.headingTextView);
        relativeMainContainer = (RelativeLayout) rootView.findViewById(R.id.relativeMainContainer);
        commentorImageView = (ImageView) rootView.findViewById(R.id.commentorImageView);
        commentorUsernameTextView = (TextView) rootView.findViewById(R.id.commentorUsernameTextView);
        commentDataTextView = (TextView) rootView.findViewById(R.id.commentDataTextView);
        commentDateTextView = (TextView) rootView.findViewById(R.id.commentDateTextView);
        anonymousImageView = (ImageView) rootView.findViewById(R.id.anonymousImageView);
        anonymousTextView = (TextView) rootView.findViewById(R.id.anonymousTextView);
        anonymousCheckbox = (CheckBox) rootView.findViewById(R.id.anonymousCheckbox);
        bottombarTopline = rootView.findViewById(R.id.bottombarTopline);
        chooseMediaTypeContainer = (RelativeLayout) rootView.findViewById(R.id.chooseMediaTypeContainer);
        mediaContainer = (LinearLayout) rootView.findViewById(R.id.mediaContainer);
        imageCameraTextView = (TextView) rootView.findViewById(R.id.imageCameraTextView);
        imageGalleryTextView = (TextView) rootView.findViewById(R.id.imageGalleryTextView);
        postImageView = (ImageView) rootView.findViewById(R.id.postImageView);
        cancelTextView = (TextView) rootView.findViewById(R.id.cancelTextView);
        commentReplyEditText.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                if (commentReplyEditText.hasFocus()) {
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    switch (event.getAction() & MotionEvent.ACTION_MASK) {
                        case MotionEvent.ACTION_SCROLL:
                            v.getParent().requestDisallowInterceptTouchEvent(false);
                            return true;
                    }
                }
                return false;
            }
        });

        Bundle extras = getArguments();
        commentOrReplyData = (GroupPostCommentResult) extras.get("parentCommentData");
        actionType = (String) extras.get("action");
        position = extras.getInt("position");

        addCommentTextView.setOnClickListener(this);
        closeImageView.setOnClickListener(this);
        anonymousImageView.setOnClickListener(this);
        anonymousTextView.setOnClickListener(this);
        anonymousCheckbox.setOnClickListener(this);
        addMediaImageView.setOnClickListener(this);
        addMediaTextView.setOnClickListener(this);
        imageCameraTextView.setOnClickListener(this);
        imageGalleryTextView.setOnClickListener(this);
//        videoCameraTextView.setOnClickListener(this);
//        videoGalleryTextView.setOnClickListener(this);
        cancelTextView.setOnClickListener(this);

        if (SharedPrefUtils.isUserAnonymous(BaseApplication.getAppContext())) {
            anonymousCheckbox.setChecked(true);
        } else {
            anonymousCheckbox.setChecked(false);
        }

        if (commentOrReplyData == null) {
            headingTextView.setText(BaseApplication.getAppContext().getString(R.string.short_s_add_comment));
            relativeMainContainer.setVisibility(View.GONE);
        } else {
            if ("EDIT_COMMENT".equals(actionType) || "EDIT_REPLY".equals(actionType)) {
                headingTextView.setText(BaseApplication.getAppContext().getString(R.string.ad_comments_edit_label));
                relativeMainContainer.setVisibility(View.GONE);
                commentReplyEditText.setText(commentOrReplyData.getContent());
                anonymousImageView.setVisibility(View.GONE);
                anonymousTextView.setVisibility(View.GONE);
                anonymousCheckbox.setVisibility(View.GONE);
                bottombarTopline.setVisibility(View.GONE);
                addMediaImageView.setVisibility(View.GONE);
                addCommentTextView.setVisibility(View.GONE);
            } else {
                headingTextView.setText(BaseApplication.getAppContext().getString(R.string.reply));
                relativeMainContainer.setVisibility(View.VISIBLE);

                if (commentOrReplyData.getIsAnnon() == 1) {
                    commentorUsernameTextView.setText(BaseApplication.getAppContext().getString(R.string.groups_anonymous));
                    commentorImageView.setImageDrawable(ContextCompat.getDrawable(BaseApplication.getAppContext(), R.drawable.ic_incognito));
                    ArrayList<String> mediaList = new ArrayList<>();
                    Map<String, String> map = (Map<String, String>) commentOrReplyData.getMediaUrls();
                    if (map != null && !map.isEmpty()) {
                        for (String entry : map.values()) {
                            mediaList.add(entry);
                        }
                        commentDateTextView.setVisibility(View.GONE);
                        media.setVisibility(View.VISIBLE);
                        commentdatetextviewmedia.setVisibility(View.VISIBLE);
                        Picasso.with(getActivity()).load(mediaList.get(0)).error(R.drawable.default_article).into(media);
                    } else {
                        commentDateTextView.setVisibility(View.VISIBLE);
                        media.setVisibility(View.GONE);
                        commentdatetextviewmedia.setVisibility(View.GONE);
                    }


                } else {
                    try {
                        Picasso.with(getActivity()).load(commentOrReplyData.getUserInfo().getProfilePicUrl().getClientApp())
                                .placeholder(R.drawable.default_commentor_img).into((commentorImageView));
                    } catch (Exception e) {
                        Crashlytics.logException(e);
                        Log.d("MC4kException", Log.getStackTraceString(e));
                        if (isAdded())
                            Picasso.with(getActivity()).load(R.drawable.default_commentor_img).into(commentorImageView);
                    }
                    commentorUsernameTextView.setText(commentOrReplyData.getUserInfo().getFirstName() + " " + commentOrReplyData.getUserInfo().getLastName());
                    ArrayList<String> mediaList = new ArrayList<>();
                    Map<String, String> map = (Map<String, String>) commentOrReplyData.getMediaUrls();
                    if (map != null && !map.isEmpty()) {
                        for (String entry : map.values()) {
                            mediaList.add(entry);
                        }
                        commentDateTextView.setVisibility(View.GONE);
                        media.setVisibility(View.VISIBLE);
                        commentdatetextviewmedia.setVisibility(View.VISIBLE);
                        Picasso.with(getActivity()).load(mediaList.get(0)).error(R.drawable.default_article).into(media);
                    } else {
                        commentDateTextView.setVisibility(View.VISIBLE);
                        media.setVisibility(View.GONE);
                        commentdatetextviewmedia.setVisibility(View.GONE);
                    }


                }

                commentDataTextView.setText(commentOrReplyData.getContent());
                Linkify.addLinks(commentDataTextView, Linkify.WEB_URLS);
                commentDataTextView.setMovementMethod(LinkMovementMethod.getInstance());
                commentDataTextView.setLinkTextColor(ContextCompat.getColor(BaseApplication.getAppContext(), R.color.groups_blue_color));
                addLinkHandler(commentDataTextView);
                commentDateTextView.setText(DateTimeUtils.getDateFromNanoMilliTimestamp(commentOrReplyData.getCreatedAt()));
                commentdatetextviewmedia.setText(DateTimeUtils.getDateFromNanoMilliTimestamp(commentOrReplyData.getCreatedAt()));
            }
        }


        return rootView;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.postCommentReplyTextView:
                Map<String, String> mediaMap = new HashMap<>();
                int i = 1;
                if (!imageUrlHashMap.isEmpty()) {
                    for (Map.Entry<ImageView, String> entry : imageUrlHashMap.entrySet()) {
                        mediaMap.put("image" + i, entry.getValue());
                        i++;
                    }
                }
                if (isValid(mediaMap)) {
                    if ("EDIT_COMMENT".equals(actionType)) {
                        ((GroupPostDetailActivity) getActivity()).editComment(commentOrReplyData.getId(), commentReplyEditText.getText().toString(), position);
                    } else if ("EDIT_REPLY".equals(actionType)) {
                        ((GroupPostDetailActivity) getActivity()).editReply(commentReplyEditText.getText().toString(), commentOrReplyData.getParentId(), commentOrReplyData.getId());
                    } else {
                        if (commentOrReplyData == null) {

                            ((GroupPostDetailActivity) getActivity()).addComment(commentReplyEditText.getText().toString(), mediaMap);
                        } else {


                            ((GroupPostDetailActivity) getActivity()).addReply(commentOrReplyData.getId(), commentReplyEditText.getText().toString(), mediaMap);
                        }
                    }
                    dismiss();
                }
                break;
            case R.id.closeImageView:
                dismiss();
                break;
            case R.id.cancelTextView:
                chooseMediaTypeContainer.setVisibility(View.GONE);
                break;
            case R.id.chooseMediaTypeContainer:
                chooseMediaTypeContainer.setVisibility(View.GONE);
                break;
            case R.id.addMediaTextView:
            case R.id.addMediaImageView:
                if (Build.VERSION.SDK_INT >= 23) {
                    if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                            != PackageManager.PERMISSION_GRANTED
                            || ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED
                            || ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions();
                    } else {
                        openMediaChooserDialog();
                    }
                } else {
                    openMediaChooserDialog();
                }
                break;
            case R.id.imageCameraTextView:
                loadImageFromCamera();
                chooseMediaTypeContainer.setVisibility(View.GONE);
                break;
            case R.id.imageGalleryTextView:
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, ADD_IMAGE_GALLERY_ACTIVITY_REQUEST_CODE);
                chooseMediaTypeContainer.setVisibility(View.GONE);
                break;
            case R.id.anonymousImageView:
            case R.id.anonymousTextView:
            case R.id.anonymousCheckbox:
                if (anonymousCheckbox.isChecked()) {
                    SharedPrefUtils.setUserAnonymous(BaseApplication.getAppContext(), true);
                } else {
                    SharedPrefUtils.setUserAnonymous(BaseApplication.getAppContext(), false);
                }
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
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, GenericFileProvider.getUriForFile(getActivity(), getActivity().getApplicationContext().getPackageName() + ".my.package.name.provider", createImageFile()));
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

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        absoluteImagePath = image.getAbsolutePath();
        return image;
    }


    public void showProgressDialog(String bodyText) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            mProgressDialog.setCancelable(false);
        }

        mProgressDialog.setMessage(bodyText);

        if (!mProgressDialog.isShowing()) {
            mProgressDialog.show();
        }
    }

    public void removeProgressDialog() {
        try {
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private boolean isValid(Map<String, String> image) {

        if (StringUtils.isNullOrEmpty(commentReplyEditText.getText().toString()) && image.isEmpty()) {
            if (isAdded())
                Toast.makeText(getActivity(), "Please add a reply", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private void addLinkHandler(TextView textView) {
        CharSequence text = textView.getText();
        if (text instanceof Spannable) {
            int end = text.length();
            Spannable sp = (Spannable) textView.getText();
            URLSpan[] urls = sp.getSpans(0, end, URLSpan.class);
            SpannableStringBuilder style = new SpannableStringBuilder(text);
            style.clearSpans();//should clear old spans
            for (URLSpan url : urls) {
                CustomerTextClick click = new CustomerTextClick(url.getURL());
                style.setSpan(click, sp.getSpanStart(url), sp.getSpanEnd(url), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            textView.setText(style);
        }
    }

    private class CustomerTextClick extends ClickableSpan {

        private String mUrl;

        CustomerTextClick(String url) {
            mUrl = url;
        }

        @Override
        public void onClick(View widget) {
            if (isAdded()) {
                Intent intent = new Intent(getActivity(), NewsLetterWebviewActivity.class);
                intent.putExtra(Constants.URL, mUrl);
                getActivity().startActivity(intent);
            }
        }
    }

    private void requestPermissions() {
        // BEGIN_INCLUDE(contacts_permission_request)
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE)
                || ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // For example, if the request has been denied previously.
            Log.i("Permissions",
                    "Displaying storage permission rationale to provide additional context.");

            // Display a SnackBar with an explanation and a button to trigger the request.
            Snackbar.make(mLayout, R.string.permission_storage_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            requestUngrantedPermissions();
                        }
                    })
                    .show();
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                Manifest.permission.CAMERA)) {

            // Display a SnackBar with an explanation and a button to trigger the request.
            Snackbar.make(mLayout, R.string.permission_camera_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            requestUngrantedPermissions();
                        }
                    })
                    .show();
        } else {
            requestUngrantedPermissions();
        }
    }

    private void requestUngrantedPermissions() {
        ArrayList<String> permissionList = new ArrayList<>();
        for (int i = 0; i < PERMISSIONS_INIT.length; i++) {
            if (ActivityCompat.checkSelfPermission(getActivity(), PERMISSIONS_INIT[i]) != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(PERMISSIONS_INIT[i]);
            }
        }
        String[] requiredPermission = permissionList.toArray(new String[permissionList.size()]);
        ActivityCompat.requestPermissions(getActivity(), requiredPermission, REQUEST_INIT_PERMISSION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (requestCode == REQUEST_INIT_PERMISSION) {
            Log.i("Permissions", "Received response for storage permissions request.");

            // We have requested multiple permissions for contacts, so all of them need to be
            // checked.
            if (PermissionUtil.verifyPermissions(grantResults)) {
                // All required permissions have been granted, display contacts fragment.
                Snackbar.make(mLayout, R.string.permision_available_init,
                        Snackbar.LENGTH_SHORT)
                        .show();
                openMediaChooserDialog();
            } else {
                Log.i("Permissions", "storage permissions were NOT granted.");
                Snackbar.make(mLayout, R.string.permissions_not_granted,
                        Snackbar.LENGTH_SHORT)
                        .show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void openMediaChooserDialog() {
        chooseMediaTypeContainer.setVisibility(View.VISIBLE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case ADD_IMAGE_GALLERY_ACTIVITY_REQUEST_CODE:
                if (data == null) {
                    return;
                }
                imageUri = data.getData();
                if (resultCode == RESULT_OK) {
                    try {
                        Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        imageBitmap.compress(Bitmap.CompressFormat.PNG, 75, stream);
                        String path = MediaStore.Images.Media.insertImage(getActivity().getContentResolver(), imageBitmap, "Title", null);
                        Uri imageUriTemp = Uri.parse(path);
                        File file2 = FileUtils.getFile(getActivity(), imageUriTemp);
                        sendUploadProfileImageRequest(file2);
                    } catch (Exception e) {
                        Crashlytics.logException(e);
                        e.printStackTrace();
                    }
                }
                break;
            case ADD_IMAGE_CAMERA_ACTIVITY_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    try {
                        Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), Uri.parse(mCurrentPhotoPath));
                        ExifInterface ei = new ExifInterface(absoluteImagePath);
                        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                                ExifInterface.ORIENTATION_UNDEFINED);

                        switch (orientation) {
                            case ExifInterface.ORIENTATION_ROTATE_90:
                                imageBitmap = rotateImage(imageBitmap, 90);
                                break;
                            case ExifInterface.ORIENTATION_ROTATE_180:
                                imageBitmap = rotateImage(imageBitmap, 180);
                                break;
                            case ExifInterface.ORIENTATION_ROTATE_270:
                                imageBitmap = rotateImage(imageBitmap, 270);
                                break;
                            case ExifInterface.ORIENTATION_NORMAL:
                            default:
                                break;
                        }

                        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 75, bytes);
                        byte[] bitmapData = bytes.toByteArray();

                        //write the bytes in file
                        FileOutputStream fos = new FileOutputStream(photoFile);
                        fos.write(bitmapData);
                        fos.flush();
                        fos.close();
                        imageUri = Uri.fromFile(photoFile);
                        File file2 = FileUtils.getFile(getActivity(), imageUri);
                        sendUploadProfileImageRequest(file2);
                        // compressImage(filePath);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case UCrop.REQUEST_CROP: {
                if (resultCode == RESULT_OK) {
                    final Uri resultUri = UCrop.getOutput(data);
                    Log.e("resultUri", resultUri.toString());
                    File file2 = FileUtils.getFile(getActivity(), resultUri);
                    sendUploadProfileImageRequest(file2);
                } else if (resultCode == UCrop.RESULT_ERROR) {
                    final Throwable cropError = UCrop.getError(data);
                }
            }
        }
    }

    public void sendUploadProfileImageRequest(File file) {
        showProgressDialog(getString(R.string.please_wait));
        MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
        RequestBody requestBodyFile = RequestBody.create(MEDIA_TYPE_PNG, file);
        Log.e("requestBodyFile", requestBodyFile.toString());
        //   RequestBody userId = RequestBody.create(MediaType.parse("text/plain"), "" + userModel.getUser().getId());
        RequestBody imageType = RequestBody.create(MediaType.parse("text/plain"), "2");
        // prepare call in Retrofit 2.0

        Retrofit retro = BaseApplication.getInstance().getRetrofit();
        ImageUploadAPI imageUploadAPI = retro.create(ImageUploadAPI.class);
        Call<ImageUploadResponse> call = imageUploadAPI.uploadImage(//userId,
                //  imageType,
                imageType,
                requestBodyFile);
        //asynchronous call
        call.enqueue(new Callback<ImageUploadResponse>() {
                         @Override
                         public void onResponse(Call<ImageUploadResponse> call, retrofit2.Response<ImageUploadResponse> response) {
                             removeProgressDialog();
                             if (response == null || response.body() == null) {
                                 //   showToast(getString(R.string.server_went_wrong));
                                 Toast.makeText(getActivity(), "server_went_wrong", Toast.LENGTH_SHORT).show();
                                 return;
                             }
                             ImageUploadResponse responseModel = response.body();
                             if (responseModel.getCode() != 200) {
                                 // showToast(getString(R.string.toast_response_error));
                                 Toast.makeText(getActivity(), "toast_response_error", Toast.LENGTH_SHORT).show();
                                 return;
                             } else {
                                 if (!StringUtils.isNullOrEmpty(responseModel.getData().getResult().getUrl())) {
                                     Log.i("IMAGE_UPLOAD_REQUEST", responseModel.getData().getResult().getUrl());
                                 }

                                 addImageToContainer(responseModel.getData().getResult().getUrl());
//                                 Picasso.with(AddTextOrMediaGroupPostActivity.this).load(responseModel.getData().getResult().getUrl()).error(R.drawable.default_article).into(postImageView);
//                                 postImageView.setVisibility(View.VISIBLE);
                                 //showToast(getString(R.string.image_upload_success));
                                 Toast.makeText(getActivity(), "image_upload_success", Toast.LENGTH_SHORT).show();
                             }
                         }

                         @Override
                         public void onFailure(Call<ImageUploadResponse> call, Throwable t) {
                             Crashlytics.logException(t);
                             Log.d("MC4KException", Log.getStackTraceString(t));
                             Toast.makeText(getActivity(), "went_wrong", Toast.LENGTH_SHORT).show();
                             //showToast(getString(R.string.went_wrong));
                         }
                     }
        );
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix,
                true);
    }

    private void addImageToContainer(String url) {
        RelativeLayout rl = (RelativeLayout) LayoutInflater.from(getActivity()).inflate(R.layout.image_post_upload_item, null);
        ImageView uploadedIV = (ImageView) rl.findViewById(R.id.addImageOptionImageView);
        final ImageView removeIV = (ImageView) rl.findViewById(R.id.removeItemImageView);
        mediaContainer.addView(rl);
        imageUrlHashMap.put(removeIV, url);
        Picasso.with(getActivity()).load(url).error(R.drawable.default_article).into(uploadedIV);
        removeIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageUrlHashMap.remove(removeIV);
                mediaContainer.removeView((View) removeIV.getParent());
            }
        });
    }
}
