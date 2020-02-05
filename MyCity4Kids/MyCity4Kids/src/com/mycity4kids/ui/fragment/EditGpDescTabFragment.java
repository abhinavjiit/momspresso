package com.mycity4kids.ui.fragment;

import android.Manifest;
import android.accounts.NetworkErrorException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.kelltontech.ui.BaseFragment;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.filechooser.com.ipaulpro.afilechooser.utils.FileUtils;
import com.mycity4kids.models.Topics;
import com.mycity4kids.models.response.GroupResult;
import com.mycity4kids.models.response.GroupsCategoryMappingResponse;
import com.mycity4kids.models.response.GroupsCategoryMappingResult;
import com.mycity4kids.models.response.ImageUploadResponse;
import com.mycity4kids.retrofitAPIsInterfaces.GroupsAPI;
import com.mycity4kids.retrofitAPIsInterfaces.ImageUploadAPI;
import com.mycity4kids.ui.activity.EditGroupActivity;
import com.mycity4kids.ui.activity.GroupCategoriesSelectionActivity;
import com.mycity4kids.utils.PermissionUtil;
import com.squareup.picasso.Picasso;
import com.yalantis.ucrop.UCrop;

import org.apmem.tools.layouts.FlowLayout;

import java.io.File;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

import static android.app.Activity.RESULT_OK;

/**
 * Created by hemant on 6/7/18.
 */

public class EditGpDescTabFragment extends BaseFragment implements View.OnClickListener {

    private static final int ADD_GROUP_CATEGORIES_REQUEST_CODE = 1112;
    private static String[] PERMISSIONS_INIT = {Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};

    private static final int REQUEST_INIT_PERMISSION = 1;

    public static final int ADD_MEDIA_ACTIVITY_REQUEST_CODE = 1111;
    private static final String SAMPLE_CROPPED_IMAGE_NAME = "SampleCropImage";

    private ArrayList<GroupsCategoryMappingResult> groupMappedCategories;
    private GroupResult groupItem;
    private String uploadImageURL;

    private View view;
    private ImageView groupImageView, editGroupImageView;
    private EditText groupCategoriesEditText;
    private EditText groupDescEditText;
    private Uri imageUri;
    private FlowLayout flowLayout;
    private TextView addTopicsBtn;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.edit_gp_desc_tab_fragment, null);

        groupImageView = (ImageView) view.findViewById(R.id.groupImageView);
        editGroupImageView = (ImageView) view.findViewById(R.id.editGroupImageView);
        addTopicsBtn = (TextView) view.findViewById(R.id.addTopicsBtn);
        groupDescEditText = (EditText) view.findViewById(R.id.groupDescEditText);
        flowLayout = (FlowLayout) view.findViewById(R.id.flowLayout);

        groupItem = getArguments().getParcelable("groupItem");

        editGroupImageView.setOnClickListener(this);
        addTopicsBtn.setOnClickListener(this);

        groupDescEditText.setText(groupItem.getDescription());
        try {
            Picasso.with(getActivity()).load(groupItem.getHeaderImage()).placeholder(R.drawable.default_article).into(groupImageView);
        } catch (Exception e) {
            Crashlytics.logException(e);
            Log.d("MC4kException", Log.getStackTraceString(e));
            Picasso.with(getActivity()).load(R.drawable.default_article).into(groupImageView);
        }

        getGroupsCategories();
        return view;

    }

    private void getGroupsCategories() {
        Retrofit retrofit = BaseApplication.getInstance().getGroupsRetrofit();
        GroupsAPI groupsAPI = retrofit.create(GroupsAPI.class);
        Call<GroupsCategoryMappingResponse> call = groupsAPI.getGroupCategories(groupItem.getId());
        call.enqueue(groupsCategoryResponseCallback);
    }

    private Callback<GroupsCategoryMappingResponse> groupsCategoryResponseCallback = new Callback<GroupsCategoryMappingResponse>() {
        @Override
        public void onResponse(Call<GroupsCategoryMappingResponse> call, retrofit2.Response<GroupsCategoryMappingResponse> response) {
            if (response == null || response.body() == null) {
                if (response != null && response.raw() != null) {
                    NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                    Crashlytics.logException(nee);
                }
                return;
            }
            try {
                if (response.isSuccessful()) {
                    GroupsCategoryMappingResponse groupsCategoryMappingResponse = response.body();
                    groupMappedCategories = (ArrayList<GroupsCategoryMappingResult>) groupsCategoryMappingResponse.getData().getResult();
                    inflateFollowedTopics();
                } else {

                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
        }

        @Override
        public void onFailure(Call<GroupsCategoryMappingResponse> call, Throwable t) {
            Crashlytics.logException(t);
            Log.d("MC4kException", Log.getStackTraceString(t));
        }
    };

    private void inflateFollowedTopics() {
//        if (followedSubSubTopicList == null || followedSubSubTopicList.size() == 0) {
//            showMoreFollowedTopicsTextView.setVisibility(View.GONE);
//            return;
//        }
        flowLayout.removeAllViews();
        for (int i = 0; i < groupMappedCategories.size(); i++) {
            groupMappedCategories.get(i).setSelected(true);
            final LinearLayout subsubLL = (LinearLayout) LayoutInflater.from(getActivity()).inflate(R.layout.topic_follow_unfollow_item, null);
            final TextView catTextView = ((TextView) subsubLL.getChildAt(0));
            catTextView.setText(groupMappedCategories.get(i).getCategoryName());
            catTextView.setSelected(true);
            subsubLL.setTag(groupMappedCategories.get(i));
            flowLayout.addView(subsubLL);
            subsubLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GroupsCategoryMappingResult top = (GroupsCategoryMappingResult) subsubLL.getTag();
                    if (top.isSelected()) {
                        top.setSelected(false);
                        catTextView.setSelected(false);
//                        Utils.pushUnfollowTopicEvent(getActivity(), "DetailArticleScreen", SharedPrefUtils.getUserDetailModel(getActivity()).getDynamoId(),
//                                top.getId() + "~" + top.getCategoryName());
                    } else {
                        top.setSelected(true);
                        catTextView.setSelected(true);
//                        Utils.pushFollowTopicEvent(getActivity(), "DetailArticleScreen", SharedPrefUtils.getUserDetailModel(getActivity()).getDynamoId(),
//                                top.getId() + "~" + top.getCategoryName());
                    }
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addTopicsBtn:
                Intent subscribeTopicIntent = new Intent(getActivity(), GroupCategoriesSelectionActivity.class);
                subscribeTopicIntent.putParcelableArrayListExtra("groupTaggedCategories", groupMappedCategories);
                startActivityForResult(subscribeTopicIntent, ADD_GROUP_CATEGORIES_REQUEST_CODE);
                break;
            case R.id.editGroupImageView:
                Intent intent1 = new Intent(Intent.ACTION_PICK);
                intent1.setType("image/*");
                if (Build.VERSION.SDK_INT >= 23) {
                    if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                            != PackageManager.PERMISSION_GRANTED
                            || ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED
                            || ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
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
            Snackbar.make(view, R.string.permission_storage_rationale,
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
            Snackbar.make(view, R.string.permission_camera_rationale,
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
                Snackbar.make(view, R.string.permision_available_init,
                        Snackbar.LENGTH_SHORT)
                        .show();
                Intent intent1 = new Intent(Intent.ACTION_PICK);
                intent1.setType("image/*");
                startActivityForResult(intent1, ADD_MEDIA_ACTIVITY_REQUEST_CODE);
            } else {
                Log.i("Permissions", "storage permissions were NOT granted.");
                Snackbar.make(view, R.string.permissions_not_granted,
                        Snackbar.LENGTH_SHORT)
                        .show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
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
                if (resultCode == RESULT_OK) {
                    try {
                        Log.e("inImagePick", "test");
                        Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
                        float actualHeight = imageBitmap.getHeight();
                        float actualWidth = imageBitmap.getWidth();
                        if (actualHeight < 405 || actualWidth < 720) {
                            if (isAdded())
                                ((EditGroupActivity) getActivity()).showToast(getString(R.string.upload_bigger_image));
                            return;
                        }
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
                    File file2 = FileUtils.getFile(getActivity(), resultUri);
                    sendUploadProfileImageRequest(file2);
                } else if (resultCode == UCrop.RESULT_ERROR) {
                    final Throwable cropError = UCrop.getError(data);
                }
            }
            break;
            case ADD_GROUP_CATEGORIES_REQUEST_CODE: {
                ArrayList<Topics> topicList = data.getParcelableArrayListExtra("updatedTopicList");
                groupMappedCategories.clear();
                for (int i = 0; i < topicList.size(); i++) {
                    GroupsCategoryMappingResult result = new GroupsCategoryMappingResult();
                    result.setCategoryId(topicList.get(i).getId());
                    result.setCategoryName(topicList.get(i).getDisplay_name());
                    result.setSelected(true);
                    groupMappedCategories.add(result);
                }

                inflateFollowedTopics();
            }
        }
    }

    private void startCropActivity(@NonNull Uri uri) {
        String destinationFileName = SAMPLE_CROPPED_IMAGE_NAME + ".jpg";
        Log.e("instartCropActivity", "test");
        UCrop uCrop = UCrop.of(uri, Uri.fromFile(new File(BaseApplication.getAppContext().getCacheDir(), destinationFileName)));
        uCrop.withAspectRatio(16, 9);
        uCrop.withMaxResultSize(720, 405);
        uCrop.start(getActivity(), this);
    }

    public void sendUploadProfileImageRequest(File file) {
        showProgressDialog(getString(R.string.please_wait));
        MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
        RequestBody requestBodyFile = RequestBody.create(MEDIA_TYPE_PNG, file);
        RequestBody imageType = RequestBody.create(MediaType.parse("text/plain"), "1");

        Retrofit retro = BaseApplication.getInstance().getRetrofit();
        ImageUploadAPI imageUploadAPI = retro.create(ImageUploadAPI.class);
        Call<ImageUploadResponse> call = imageUploadAPI.uploadImage(//userId,topic
                imageType,
                requestBodyFile);
        call.enqueue(new Callback<ImageUploadResponse>() {
                         @Override
                         public void onResponse(Call<ImageUploadResponse> call, retrofit2.Response<ImageUploadResponse> response) {
                             removeProgressDialog();
                             if (response == null || response.body() == null) {
                                 if (isAdded())
                                     ((EditGroupActivity) getActivity()).showToast(getString(R.string.server_went_wrong));
                                 return;
                             }
                             ImageUploadResponse responseModel = response.body();
                             if (responseModel.getCode() == 200 && Constants.SUCCESS.equals(responseModel.getStatus())) {
                                 uploadImageURL = (responseModel.getData().getResult().getUrl());
                                 Picasso.with(getActivity()).load(responseModel.getData().getResult().getUrl()).error(R.drawable.default_article).into(groupImageView);
                                 if (isAdded())
                                     ((EditGroupActivity) getActivity()).showToast(getString(R.string.image_upload_success));
                             } else {
                                 if (isAdded())
                                     ((EditGroupActivity) getActivity()).showToast(getString(R.string.went_wrong));
                             }
                         }

                         @Override
                         public void onFailure(Call<ImageUploadResponse> call, Throwable t) {
                             Crashlytics.logException(t);
                             Log.d("MC4KException", Log.getStackTraceString(t));
                             if (isAdded())
                                 ((EditGroupActivity) getActivity()).showToast(getString(R.string.went_wrong));
                         }
                     }
        );
    }

    public GroupResult getUpdatedDetails() {
        if (StringUtils.isNullOrEmpty(groupDescEditText.getText().toString())) {
            if (isAdded())
                ((EditGroupActivity) getActivity()).showToast("Empty Description");
            return null;
        }
        groupItem.setHeaderImage(uploadImageURL);
        groupItem.setDescription(groupDescEditText.getText().toString());
        return groupItem;
    }

    public ArrayList<GroupsCategoryMappingResult> getUpdatedCategories() {
        return groupMappedCategories;
    }
}
