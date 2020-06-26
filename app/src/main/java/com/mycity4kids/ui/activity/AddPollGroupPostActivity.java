package com.mycity4kids.ui.activity;

import android.Manifest;
import android.accounts.NetworkErrorException;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.base.BaseActivity;
import com.mycity4kids.filechooser.com.ipaulpro.afilechooser.utils.FileUtils;
import com.mycity4kids.models.request.AddGroupPostRequest;
import com.mycity4kids.models.response.AddGroupPostResponse;
import com.mycity4kids.models.response.GroupResult;
import com.mycity4kids.models.response.ImageUploadResponse;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.GroupsAPI;
import com.mycity4kids.retrofitAPIsInterfaces.ImageUploadAPI;
import com.mycity4kids.ui.adapter.AddImagePollRecyclerGridAdapter;
import com.mycity4kids.ui.adapter.AddTextPollRecyclerGridAdapter;
import com.mycity4kids.utils.PermissionUtil;
import com.mycity4kids.utils.StringUtils;
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

public class AddPollGroupPostActivity extends BaseActivity implements View.OnClickListener,
        AddImagePollRecyclerGridAdapter.ImagePollRecyclerViewClickListener,
        AddTextPollRecyclerGridAdapter.TextPollRecyclerViewClickListener {

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
    private RecyclerView recyclerGridView;
    private RecyclerView recyclerView;
    private View mainLayout;
    private ImageView currentImageView;
    private EditText pollQuestionEditText;
    private ImageView togglePollOptionImageView;
    private TextView publishTextView;
    private ImageView closeEditorImageView;
    private TextView togglePollOptionTextView;
    private CheckBox anonymousCheckbox;
    private TextView anonymousTextView;
    private ImageView anonymousImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_poll_group_post_activity);
        ((BaseApplication) getApplication()).setActivity(this);

        mainLayout = findViewById(R.id.rootView);
        ((BaseApplication) getApplication()).setView(mainLayout);
        addChoiceTextView = findViewById(R.id.addChoiceTextView);
        publishTextView = findViewById(R.id.publishTextView);
        togglePollOptionImageView = findViewById(R.id.togglePollOptionImageView);
        recyclerGridView = findViewById(R.id.recyclerGridView);
        recyclerView = findViewById(R.id.recyclerView);
        pollQuestionEditText = findViewById(R.id.pollQuestionEditText);
        closeEditorImageView = findViewById(R.id.closeEditorImageView);
        togglePollOptionTextView = findViewById(R.id.togglePollOptionTextView);
        anonymousTextView = findViewById(R.id.anonymousTextView);
        anonymousCheckbox = findViewById(R.id.anonymousCheckbox);
        anonymousImageView = findViewById(R.id.anonymousImageView);

        togglePollOptionImageView.setOnClickListener(this);
        togglePollOptionTextView.setOnClickListener(this);
        addChoiceTextView.setOnClickListener(this);
        publishTextView.setOnClickListener(this);
        closeEditorImageView.setOnClickListener(this);
        anonymousImageView.setOnClickListener(this);
        anonymousTextView.setOnClickListener(this);
        anonymousCheckbox.setOnClickListener(this);

        selectedGroup = getIntent().getParcelableExtra("groupItem");

        if (selectedGroup != null && selectedGroup.getAnnonAllowed() == 0) {
            SharedPrefUtils.setUserAnonymous(BaseApplication.getAppContext(), false);
            anonymousCheckbox.setChecked(false);
            anonymousCheckbox.setVisibility(View.INVISIBLE);
            anonymousImageView.setVisibility(View.INVISIBLE);
            anonymousTextView.setVisibility(View.INVISIBLE);
        } else {
            if (SharedPrefUtils.isUserAnonymous(BaseApplication.getAppContext())) {
                anonymousCheckbox.setChecked(true);
            } else {
                anonymousCheckbox.setChecked(false);
            }
        }

        pollQuestionEditText
                .setText(SharedPrefUtils.getSavedPostData(BaseApplication.getAppContext(), selectedGroup.getId()));

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
                        startCropActivity(imageUri);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case UCrop.REQUEST_CROP: {
                if (resultCode == RESULT_OK) {
                    final Uri resultUri = UCrop.getOutput(data);
                    File file2 = FileUtils.getFile(this, resultUri);
                    sendUploadProfileImageRequest(file2);
                } else if (resultCode == UCrop.RESULT_ERROR) {
                    final Throwable cropError = UCrop.getError(data);
                }
            }
            break;
            default:
                break;
        }
    }

    private void startCropActivity(@NonNull Uri uri) {
        String destinationFileName = SAMPLE_CROPPED_IMAGE_NAME + ".jpg";
        Log.e("instartCropActivity", "test");
        UCrop ucrop = UCrop.of(uri, Uri.fromFile(new File(getCacheDir(), destinationFileName)));
        ucrop.withAspectRatio(16, 10);
        ucrop.withMaxResultSize(720, 450);
        ucrop.start(AddPollGroupPostActivity.this);
    }

    public void sendUploadProfileImageRequest(File file) {
        showProgressDialog(getString(R.string.please_wait));
        MediaType mediaType = MediaType.parse("image/png");
        RequestBody requestBodyFile = RequestBody.create(mediaType, file);
        Log.e("requestBodyFile", requestBodyFile.toString());
        RequestBody imageType = RequestBody.create(MediaType.parse("text/plain"), "2");

        Retrofit retro = BaseApplication.getInstance().getRetrofit();
        ImageUploadAPI imageUploadApi = retro.create(ImageUploadAPI.class);
        Call<ImageUploadResponse> call = imageUploadApi.uploadImage(imageType, requestBodyFile);
        call.enqueue(
                new Callback<ImageUploadResponse>() {
                    @Override
                    public void onResponse(Call<ImageUploadResponse> call,
                            retrofit2.Response<ImageUploadResponse> response) {
                        removeProgressDialog();
                        if (response.body() == null) {
                            showToast(getString(R.string.server_went_wrong));
                            return;
                        }
                        ImageUploadResponse responseModel = response.body();
                        if (responseModel.getCode() != 200) {
                            showToast(getString(R.string.toast_response_error));
                        } else {
                            if (!StringUtils.isNullOrEmpty(responseModel.getData().getResult().getUrl())) {
                                Log.i("IMAGE_UPLOAD_REQUEST", responseModel.getData().getResult().getUrl());
                            }

                            Picasso.get().load(responseModel.getData().getResult().getUrl())
                                    .error(R.drawable.default_article)
                                    .into(currentImageView);
                            urlList.set(currentImagePosition, responseModel.getData().getResult().getUrl());
                            imagePollAdapter.notifyDataSetChanged();
                            showToast(getString(R.string.image_upload_success));
                        }
                    }

                    @Override
                    public void onFailure(Call<ImageUploadResponse> call, Throwable t) {
                        FirebaseCrashlytics.getInstance().recordException(t);
                        Log.d("MC4KException", Log.getStackTraceString(t));
                        apiExceptions(t);
                    }
                }
        );
    }

    private void requestPermissions() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                || ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Snackbar.make(mainLayout, R.string.permission_storage_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, view -> requestUngrantedPermissions())
                    .show();
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            Snackbar.make(mainLayout, R.string.permission_camera_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, view -> requestUngrantedPermissions())
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        if (requestCode == REQUEST_INIT_PERMISSION) {
            if (PermissionUtil.verifyPermissions(grantResults)) {
                Snackbar.make(mainLayout, R.string.permision_available_init,
                        Snackbar.LENGTH_SHORT)
                        .show();
                Intent intent1 = new Intent(Intent.ACTION_PICK);
                intent1.setType("image/*");
                startActivityForResult(intent1, ADD_MEDIA_ACTIVITY_REQUEST_CODE);
            } else {
                Snackbar.make(mainLayout, R.string.permissions_not_granted,
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
                    togglePollOptionImageView.setImageDrawable(
                            ContextCompat.getDrawable(AddPollGroupPostActivity.this, R.drawable.tab3));
                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                    recyclerGridView.setVisibility(View.GONE);
                    togglePollOptionTextView.setText(getString(R.string.groups_image_poll));
                    togglePollOptionImageView.setImageDrawable(
                            ContextCompat.getDrawable(AddPollGroupPostActivity.this, R.drawable.ic_incognito));
                }
                break;
            default:
                break;
        }
    }

    private void publishPoll() {
        Retrofit retrofit = BaseApplication.getInstance().getGroupsRetrofit();
        GroupsAPI groupsApi = retrofit.create(GroupsAPI.class);
        if (recyclerView.getVisibility() == View.VISIBLE) {
            AddGroupPostRequest addGroupPostRequest = new AddGroupPostRequest();
            addGroupPostRequest.setContent(pollQuestionEditText.getText().toString());
            addGroupPostRequest.setType("2");
            addGroupPostRequest.setPollType("0");
            addGroupPostRequest.setGroupId(selectedGroup.getId());
            addGroupPostRequest.setUserId(SharedPrefUtils.getUserDetailModel(this).getDynamoId());
            if (SharedPrefUtils.isUserAnonymous(BaseApplication.getAppContext())) {
                addGroupPostRequest.setAnnon(1);
            }
            Map<String, String> pollOptionsMap = new HashMap<>();
            for (int i = 0; i < textChoiceList.size(); i++) {
                pollOptionsMap.put("option" + (i + 1), textChoiceList.get(i));
            }
            addGroupPostRequest.setPollOptions(pollOptionsMap);
            Call<AddGroupPostResponse> call = groupsApi.createPost(addGroupPostRequest);
            call.enqueue(postAdditionResponseCallback);
        } else {
            AddGroupPostRequest addGroupPostRequest = new AddGroupPostRequest();
            addGroupPostRequest.setContent(pollQuestionEditText.getText().toString());
            addGroupPostRequest.setType("2");
            addGroupPostRequest.setPollType("1");
            addGroupPostRequest.setGroupId(selectedGroup.getId());
            addGroupPostRequest.setUserId(SharedPrefUtils.getUserDetailModel(this).getDynamoId());
            if (SharedPrefUtils.isUserAnonymous(BaseApplication.getAppContext())) {
                addGroupPostRequest.setAnnon(1);
            }
            Map<String, String> pollOptionsMap = new HashMap<>();
            for (int i = 0; i < urlList.size(); i++) {
                pollOptionsMap.put("option" + (i + 1), urlList.get(i));
            }
            addGroupPostRequest.setPollOptions(pollOptionsMap);
            Call<AddGroupPostResponse> call = groupsApi.createPost(addGroupPostRequest);
            call.enqueue(postAdditionResponseCallback);
        }
    }

    private Callback<AddGroupPostResponse> postAdditionResponseCallback = new Callback<AddGroupPostResponse>() {
        @Override
        public void onResponse(Call<AddGroupPostResponse> call, retrofit2.Response<AddGroupPostResponse> response) {
            isRequestRunning = false;
            if (response.body() == null) {
                NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                FirebaseCrashlytics.getInstance().recordException(nee);
                return;
            }
            try {
                if (response.isSuccessful()) {
                    setResult(RESULT_OK);
                    pollQuestionEditText.setText("");
                    onBackPressed();
                }
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
                showToast(getString(R.string.went_wrong));
            }
        }

        @Override
        public void onFailure(Call<AddGroupPostResponse> call, Throwable t) {
            isRequestRunning = false;
            FirebaseCrashlytics.getInstance().recordException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
            showToast(getString(R.string.went_wrong));
        }
    };

    private boolean validateParams() {
        if (pollQuestionEditText.getText() == null || StringUtils
                .isNullOrEmpty(pollQuestionEditText.getText().toString())) {
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
            default:
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
                            || ActivityCompat.checkSelfPermission(AddPollGroupPostActivity.this,
                            Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED
                            || ActivityCompat.checkSelfPermission(AddPollGroupPostActivity.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions();
                    } else {
                        startActivityForResult(intent1, ADD_MEDIA_ACTIVITY_REQUEST_CODE);
                    }
                } else {
                    startActivityForResult(intent1, ADD_MEDIA_ACTIVITY_REQUEST_CODE);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (null != pollQuestionEditText.getText() && !StringUtils
                .isNullOrEmpty(pollQuestionEditText.getText().toString())) {
            SharedPrefUtils.setSavedPostData(BaseApplication.getAppContext(), selectedGroup.getId(),
                    pollQuestionEditText.getText().toString());
        }
    }
}
