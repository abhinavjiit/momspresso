package com.mycity4kids.ui.activity;

import android.Manifest;
import android.accounts.NetworkErrorException;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.filechooser.com.ipaulpro.afilechooser.utils.FileUtils;
import com.mycity4kids.models.request.AddGroupPostRequest;
import com.mycity4kids.models.response.AddGroupPostResponse;
import com.mycity4kids.models.response.GroupDetailResponse;
import com.mycity4kids.models.response.GroupResult;
import com.mycity4kids.models.response.GroupsListingResponse;
import com.mycity4kids.models.response.ImageUploadResponse;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.GroupsAPI;
import com.mycity4kids.retrofitAPIsInterfaces.ImageUploadAPI;
import com.mycity4kids.ui.adapter.AddImagePollRecyclerGridAdapter;
import com.mycity4kids.ui.adapter.AddTextPollRecyclerGridAdapter;
import com.mycity4kids.utils.PermissionUtil;
import com.mycity4kids.widget.SpacesItemDecoration;
import com.squareup.picasso.Picasso;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by hemant on 24/4/18.
 */

public class AddPollGroupPostActivity extends BaseActivity implements View.OnClickListener, AddImagePollRecyclerGridAdapter.ImagePollRecyclerViewClickListener, AddTextPollRecyclerGridAdapter.TextPollRecyclerViewClickListener {

    private static String[] PERMISSIONS_INIT = {Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};

    private static final String SAMPLE_CROPPED_IMAGE_NAME = "SampleCropImage";

    private static final int REQUEST_INIT_PERMISSION = 1;

    public static final int ADD_MEDIA_ACTIVITY_REQUEST_CODE = 1111;

    private AddImagePollRecyclerGridAdapter imagePollAdapter;
    private AddTextPollRecyclerGridAdapter textPollAdapter;

    private Uri imageUri;
    private int currentImagePosition;
    private ArrayList<String> urlList;
    private ArrayList<String> textChoiceList;
    private GroupResult selectedGroup;
    private boolean isRequestRunning = false;

    private TextView addChoiceTextView;
    private LinearLayout choicesContainer;
    private ImageView imagePollOptionImageView;
    private RecyclerView recyclerGridView, recyclerView;
    private View mLayout;
    private ImageView currentImageView;
    private EditText pollQuestionEditText;
    private ImageView togglePollOptionImageView;
    private TextView publishTextView;
    private ImageView closeEditorImageView;
    private TextView togglePollOptionTextView;
    private CheckBox anonymousCheckbox;
    private ImageView addMediaTextView;
    private TextView anonymousTextView;
    private ImageView anonymousImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_poll_group_post_activity);
        ((BaseApplication) getApplication()).setActivity(this);

        mLayout = findViewById(R.id.rootView);
        ((BaseApplication) getApplication()).setView(mLayout);
        addChoiceTextView = (TextView) findViewById(R.id.addChoiceTextView);
        publishTextView = (TextView) findViewById(R.id.publishTextView);
        togglePollOptionImageView = (ImageView) findViewById(R.id.togglePollOptionImageView);
        togglePollOptionTextView = (TextView) findViewById(R.id.togglePollOptionTextView);
        recyclerGridView = (RecyclerView) findViewById(R.id.recyclerGridView);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        pollQuestionEditText = (EditText) findViewById(R.id.pollQuestionEditText);
        closeEditorImageView = (ImageView) findViewById(R.id.closeEditorImageView);
        anonymousTextView = (TextView) findViewById(R.id.anonymousTextView);
        anonymousCheckbox = (CheckBox) findViewById(R.id.anonymousCheckbox);
        anonymousImageView = (ImageView) findViewById(R.id.anonymousImageView);


        selectedGroup = (GroupResult) getIntent().getParcelableExtra("groupItem");

        togglePollOptionImageView.setOnClickListener(this);
        togglePollOptionTextView.setOnClickListener(this);
        addChoiceTextView.setOnClickListener(this);
        togglePollOptionTextView.setOnClickListener(this);
        publishTextView.setOnClickListener(this);
        closeEditorImageView.setOnClickListener(this);
        anonymousImageView.setOnClickListener(this);
        anonymousTextView.setOnClickListener(this);
        anonymousCheckbox.setOnClickListener(this);

        pollQuestionEditText.setText(SharedPrefUtils.getSavedPostData(this, selectedGroup.getId()));

        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.groups_column_spacing);
        recyclerGridView.addItemDecoration(new SpacesItemDecoration(spacingInPixels));

        urlList = new ArrayList<>();
        urlList.add("");
        urlList.add("");

        textChoiceList = new ArrayList<>();
        textChoiceList.add("");
        textChoiceList.add("");

        final LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);

        final GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerGridView.setLayoutManager(gridLayoutManager);

        textPollAdapter = new AddTextPollRecyclerGridAdapter(this, this);
        textPollAdapter.setNewListData(textChoiceList);
        recyclerView.setAdapter(textPollAdapter);

        imagePollAdapter = new AddImagePollRecyclerGridAdapter(this, this);
        imagePollAdapter.setNewListData(urlList);
        recyclerGridView.setAdapter(imagePollAdapter);

        if (SharedPrefUtils.isUserAnonymous(this)) {
            anonymousCheckbox.setChecked(true);
        } else {
            anonymousCheckbox.setChecked(false);
        }

    }

    @Override
    protected void updateUi(Response response) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            return;
        }
        imageUri = data.getData();
        switch (requestCode) {
            case ADD_MEDIA_ACTIVITY_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    try {
                        Log.e("inImagePick", "test");
                        startCropActivity(imageUri);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case UCrop.REQUEST_CROP: {
                if (resultCode == RESULT_OK) {
                    final Uri resultUri = UCrop.getOutput(data);
                    Log.e("resultUri", resultUri.toString());
                    File file2 = FileUtils.getFile(this, resultUri);
                    sendUploadProfileImageRequest(file2);
                } else if (resultCode == UCrop.RESULT_ERROR) {
                    final Throwable cropError = UCrop.getError(data);
                }
            }
        }
    }

    private void startCropActivity(@NonNull Uri uri) {
        String destinationFileName = SAMPLE_CROPPED_IMAGE_NAME + ".jpg";
        Log.e("instartCropActivity", "test");
        UCrop uCrop = UCrop.of(uri, Uri.fromFile(new File(getCacheDir(), destinationFileName)));
        uCrop.withAspectRatio(16, 10);
        uCrop.withMaxResultSize(720, 450);
        uCrop.start(AddPollGroupPostActivity.this);
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
                                 showToast(getString(R.string.server_went_wrong));
                                 return;
                             }
                             ImageUploadResponse responseModel = response.body();
                             if (responseModel.getCode() != 200) {
                                 showToast(getString(R.string.toast_response_error));
                                 return;
                             } else {
                                 if (!StringUtils.isNullOrEmpty(responseModel.getData().getResult().getUrl())) {
                                     Log.i("IMAGE_UPLOAD_REQUEST", responseModel.getData().getResult().getUrl());
                                 }

                                 Picasso.with(AddPollGroupPostActivity.this).load(responseModel.getData().getResult().getUrl()).error(R.drawable.default_article).into(currentImageView);
//                                 currentImageView.setVisibility(View.VISIBLE);
                                 urlList.set(currentImagePosition, responseModel.getData().getResult().getUrl());
                                 imagePollAdapter.notifyDataSetChanged();
                                 showToast(getString(R.string.image_upload_success));
                             }
                         }

                         @Override
                         public void onFailure(Call<ImageUploadResponse> call, Throwable t) {
                             Crashlytics.logException(t);
                             Log.d("MC4KException", Log.getStackTraceString(t));
                             showToast(getString(R.string.went_wrong));
                         }
                     }
        );
    }


    private void requestPermissions() {
        // BEGIN_INCLUDE(contacts_permission_request)
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                || ActivityCompat.shouldShowRequestPermissionRationale(this,
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
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(this,
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
            if (ActivityCompat.checkSelfPermission(this, PERMISSIONS_INIT[i]) != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(PERMISSIONS_INIT[i]);
            }
        }
        String[] requiredPermission = permissionList.toArray(new String[permissionList.size()]);
        ActivityCompat.requestPermissions(this, requiredPermission, REQUEST_INIT_PERMISSION);
    }

    /**
     * Callback received when a permissions request has been completed.
     */
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
                Intent intent1 = new Intent(Intent.ACTION_PICK);
                intent1.setType("image/*");
                startActivityForResult(intent1, ADD_MEDIA_ACTIVITY_REQUEST_CODE);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.anonymousImageView:
            case R.id.anonymousTextView:
            case R.id.anonymousCheckbox:
                if (anonymousCheckbox.isChecked()) {
                    SharedPrefUtils.setUserAnonymous(BaseApplication.getAppContext(), true);
                } else {
                    SharedPrefUtils.setUserAnonymous(BaseApplication.getAppContext(), false);
                }
                break;
            case R.id.addChoiceTextView:
                if (recyclerView.getVisibility() == View.VISIBLE) {
                    textChoiceList.add("");
                    textPollAdapter.notifyDataSetChanged();
                    if (textChoiceList.size() >= 4) {
                        addChoiceTextView.setVisibility(View.GONE);
                    }
                } else {
                    urlList.add("");
                    imagePollAdapter.notifyDataSetChanged();
                    if (urlList.size() >= 4) {
                        addChoiceTextView.setVisibility(View.GONE);
                    }
                }
                break;
            case R.id.closeEditorImageView:
                onBackPressed();
                break;
            case R.id.publishTextView:
                if (!isRequestRunning && validateParams()) {
//                    showToast("Valid Poll");
                    isRequestRunning = true;
                    publishPoll();
                }
                break;
            case R.id.togglePollOptionTextView:
            case R.id.togglePollOptionImageView:
                if (recyclerView.getVisibility() == View.VISIBLE) {
                    recyclerView.setVisibility(View.GONE);
                    recyclerGridView.setVisibility(View.VISIBLE);
                    togglePollOptionTextView.setText(getString(R.string.groups_text_poll));
                    togglePollOptionImageView.setImageDrawable(ContextCompat.getDrawable(AddPollGroupPostActivity.this, R.drawable.tab3));
                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                    recyclerGridView.setVisibility(View.GONE);
                    togglePollOptionTextView.setText(getString(R.string.groups_image_poll));
                    togglePollOptionImageView.setImageDrawable(ContextCompat.getDrawable(AddPollGroupPostActivity.this, R.drawable.ic_incognito));
                }
                break;
        }
    }

    private void publishPoll() {
        Retrofit retrofit = BaseApplication.getInstance().getGroupsRetrofit();
        GroupsAPI groupsAPI = retrofit.create(GroupsAPI.class);
        if (recyclerView.getVisibility() == View.VISIBLE) {
            AddGroupPostRequest addGroupPostRequest = new AddGroupPostRequest();
            addGroupPostRequest.setContent(pollQuestionEditText.getText().toString());
            addGroupPostRequest.setType("2");
            addGroupPostRequest.setPollType("0");
            addGroupPostRequest.setGroupId(selectedGroup.getId());
            addGroupPostRequest.setUserId(SharedPrefUtils.getUserDetailModel(this).getDynamoId());
            if (SharedPrefUtils.isUserAnonymous(this)) {
                addGroupPostRequest.setAnnon(1);
            }
            Map<String, String> pollOptionsMap = new HashMap<>();
            for (int i = 0; i < textChoiceList.size(); i++) {
                pollOptionsMap.put("option" + (i + 1), textChoiceList.get(i));
            }
            addGroupPostRequest.setPollOptions(pollOptionsMap);
            Call<AddGroupPostResponse> call = groupsAPI.createPost(addGroupPostRequest);
            call.enqueue(postAdditionResponseCallback);
        } else {
            AddGroupPostRequest addGroupPostRequest = new AddGroupPostRequest();
            addGroupPostRequest.setContent(pollQuestionEditText.getText().toString());
            addGroupPostRequest.setType("2");
            addGroupPostRequest.setPollType("1");
            addGroupPostRequest.setGroupId(selectedGroup.getId());
            addGroupPostRequest.setUserId(SharedPrefUtils.getUserDetailModel(this).getDynamoId());
            if (SharedPrefUtils.isUserAnonymous(this)) {
                addGroupPostRequest.setAnnon(1);
            }
            Map<String, String> pollOptionsMap = new HashMap<>();
            for (int i = 0; i < urlList.size(); i++) {
                pollOptionsMap.put("option" + (i + 1), urlList.get(i));
            }
            addGroupPostRequest.setPollOptions(pollOptionsMap);
            Call<AddGroupPostResponse> call = groupsAPI.createPost(addGroupPostRequest);
            call.enqueue(postAdditionResponseCallback);
        }
    }

    private Callback<AddGroupPostResponse> postAdditionResponseCallback = new Callback<AddGroupPostResponse>() {
        @Override
        public void onResponse(Call<AddGroupPostResponse> call, retrofit2.Response<AddGroupPostResponse> response) {
            isRequestRunning = false;
            if (response == null || response.body() == null) {
                if (response != null && response.raw() != null) {
                    NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                    Crashlytics.logException(nee);
                }
                return;
            }
            try {
                if (response.isSuccessful()) {
                    AddGroupPostResponse responseModel = response.body();
                    setResult(RESULT_OK);
                    pollQuestionEditText.setText("");
                    onBackPressed();
                } else {

                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
                showToast(getString(R.string.went_wrong));
            }
        }

        @Override
        public void onFailure(Call<AddGroupPostResponse> call, Throwable t) {
            isRequestRunning = false;
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
            showToast(getString(R.string.went_wrong));
        }
    };

    private boolean validateParams() {
        if (pollQuestionEditText.getText() == null || StringUtils.isNullOrEmpty(pollQuestionEditText.getText().toString())) {
            showToast("Please enter question to continue");
            return false;
        }
        if (recyclerView.getVisibility() == View.VISIBLE) {
            for (int i = 0; i < textChoiceList.size(); i++) {
                if (StringUtils.isNullOrEmpty(textChoiceList.get(i))) {
                    showToast("Please enter valid options for poll");
                    return false;
                }
            }
        } else {
            for (int i = 0; i < urlList.size(); i++) {
                if (StringUtils.isNullOrEmpty(urlList.get(i))) {
                    showToast("Please enter valid options for poll");
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onTextPollItemClick(View view, int position) {
        switch (view.getId()) {
            case R.id.removeItemImageView:
                if (textChoiceList.size() < 4) {
                    addChoiceTextView.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    @Override
    public void onImagePollItemClick(View view, int position) {
        switch (view.getId()) {
            case R.id.removeItemImageView:
                urlList.remove(position);
                imagePollAdapter.notifyDataSetChanged();
                if (urlList.size() < 4) {
                    addChoiceTextView.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.addImageOptionContainer:
                currentImageView = (ImageView) view.findViewById(R.id.addImageOptionImageView);
                currentImagePosition = position;
                Intent intent1 = new Intent(Intent.ACTION_PICK);
                intent1.setType("image/*");

                if (Build.VERSION.SDK_INT >= 23) {
                    if (ActivityCompat.checkSelfPermission(AddPollGroupPostActivity.this, Manifest.permission.CAMERA)
                            != PackageManager.PERMISSION_GRANTED
                            || ActivityCompat.checkSelfPermission(AddPollGroupPostActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED
                            || ActivityCompat.checkSelfPermission(AddPollGroupPostActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions();
                    } else {
                        startActivityForResult(intent1, ADD_MEDIA_ACTIVITY_REQUEST_CODE);
                    }
                } else {
                    startActivityForResult(intent1, ADD_MEDIA_ACTIVITY_REQUEST_CODE);
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (null != pollQuestionEditText.getText() && !StringUtils.isNullOrEmpty(pollQuestionEditText.getText().toString())) {
            SharedPrefUtils.setSavedPostData(AddPollGroupPostActivity.this, selectedGroup.getId(), pollQuestionEditText.getText().toString());
        }
    }
}
