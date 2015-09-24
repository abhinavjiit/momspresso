package com.mycity4kids.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseFragment;
import com.kelltontech.utils.BitmapUtils;
import com.kelltontech.utils.ConnectivityUtils;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.controller.EditProfileController;
import com.mycity4kids.controller.ImageUploadController;
import com.mycity4kids.dbtable.TableFamily;
import com.mycity4kids.dbtable.UserTable;
import com.mycity4kids.models.forgot.CommonResponse;
import com.mycity4kids.models.profile.SignUpModel;
import com.mycity4kids.models.user.ImageUploadRequest;
import com.mycity4kids.models.user.UserModel;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.ui.activity.DashboardActivity;
import com.mycity4kids.utils.RoundedTransformation;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.URI;

/**
 * Created by manish.soni on 25-06-2015.
 */
public class FragmentFamilyProfile extends BaseFragment implements View.OnClickListener {

    View view;
    private EditText mFamilyname, mFamilysharepswd,mFamilyConfirmPswd;
    SignUpModel.Family _requestModel;
    private ImageView profile_image;
    private Bitmap originalImage = null;
    private String profileimgUrl;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.aa_family_profile, container, false);

        ((DashboardActivity) getActivity()).setTitle("Family Profile");

        //setHasOptionsMenu(true);

        profile_image = (ImageView) view.findViewById(R.id.profile_image);
        mFamilyname = (EditText) view.findViewById(R.id.family_name);
        mFamilysharepswd = (EditText) view.findViewById(R.id.password);
        mFamilyConfirmPswd = (EditText) view.findViewById(R.id.confirmpswd);

        profile_image.setOnClickListener(this);

        TableFamily familyTable = new TableFamily((BaseApplication) getActivity().getApplicationContext());
        UserModel.FamilyInfo family = familyTable.getFamily();

        if (family != null) {

            mFamilyname.setText(family.getFamily_name());
            //mFamilysharepswd.setText(family.getFamily_password());

        }

        profileimgUrl = SharedPrefUtils.getProfileImgUrl(getActivity());

        try{
            if (!StringUtils.isNullOrEmpty(profileimgUrl))
                Picasso.with(getActivity()).load(profileimgUrl).placeholder(R.drawable.family_xxhdpi).error(R.drawable.family_xxhdpi).transform(new RoundedTransformation()).into(profile_image);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        return view;

    }

    private boolean isDataValid() {
        boolean isLoginOk = true;
        String email_id = mFamilyname.getText().toString();

        if (mFamilyname.getText().toString().length() == 0) {
            mFamilyname.setFocusableInTouchMode(true);
            mFamilyname.requestFocus();
            mFamilyname.setError("Please enter family name");
            isLoginOk = false;
        } else if (mFamilysharepswd.getText().toString().length() == 0) {
            mFamilysharepswd.setFocusableInTouchMode(true);
            mFamilysharepswd.requestFocus();
            mFamilysharepswd.setError("Password can't be left blank");
            //mPassword.requestFocus();
            isLoginOk = false;
        } else if (mFamilysharepswd.getText().toString().length() < 6) {
            mFamilysharepswd.setFocusableInTouchMode(true);
            mFamilysharepswd.requestFocus();

            mFamilysharepswd.setError("Password should not less than 6 character.");
            //mPassword.requestFocus();
            isLoginOk = false;
        }
        else if (!(mFamilysharepswd.getText().toString().equals(mFamilyConfirmPswd.getText().toString()))) {

            mFamilyConfirmPswd.setFocusableInTouchMode(true);
            mFamilyConfirmPswd.requestFocus();
            mFamilyConfirmPswd.setError(Constants.PASSWORD_MISMATCH);
            //ToastUtils.showToast(this, Constants.PASSWORD_MISMATCH);
            isLoginOk = false;
        }
        return isLoginOk;
    }

    public void callService() {

        if (ConnectivityUtils.isNetworkEnabled(getActivity())) {

            if (isDataValid()) {

                showProgressDialog(getString(R.string.please_wait));
                _requestModel = new SignUpModel().new Family();
                _requestModel.setFamily_name(mFamilyname.getText().toString().trim());
                _requestModel.setFamily_password(mFamilysharepswd.getText().toString().trim());
                _requestModel.setFamily_image(profileimgUrl);

                EditProfileController _controller = new EditProfileController(getActivity(), this);
                _controller.getData(AppConstants.EDIT_FAMILY_REQUEST, _requestModel);
            }

        } else {
            Toast.makeText(getActivity(), getString(R.string.error_network), Toast.LENGTH_SHORT).show();
        }

    }

    protected void updateUi(Response response) {
        if (response == null) {
            removeProgressDialog();
            Toast.makeText(getActivity(), "Content not fetching from server side", Toast.LENGTH_SHORT).show();
            return;
        }
        switch (response.getDataType()) {
            case AppConstants.IMAGE_UPLOAD_REQUEST:
                removeProgressDialog();
                if (response.getResponseObject() instanceof CommonResponse) {
                    CommonResponse responseModel = (CommonResponse) response
                            .getResponseObject();
                    if (responseModel.getResponseCode() != 200) {
                        Toast.makeText(getActivity(), getString(R.string.toast_response_error), Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        if (!StringUtils.isNullOrEmpty(responseModel.getResult().getMessage())) {
                            SharedPrefUtils.setProfileImgUrl(getActivity(), responseModel.getResult().getMessage());
                            Log.i("Uploaded Image URL", responseModel.getResult().getMessage());
                        }
                        //setProfileImage(originalImage);
                        setProfileImage(responseModel.getResult().getMessage());

                        Toast.makeText(getActivity(), "You have successfully uploaded image.", Toast.LENGTH_SHORT).show();
                    }
                }
                break;


            case AppConstants.EDIT_FAMILY_REQUEST:

                CommonResponse responseData = (CommonResponse) response.getResponseObject();
                if (responseData.getResponseCode() == 200) {
                    Toast.makeText(getActivity(), responseData.getResult().getMessage(), Toast.LENGTH_SHORT).show();
                    // db update
                    TableFamily tableKids = new TableFamily(BaseApplication.getInstance());
                    tableKids.updateVal(_requestModel);

                    SharedPrefUtils.setProfileImgUrl(getActivity(),profileimgUrl);

                } else if (responseData.getResponseCode() == 400) {
                    Toast.makeText(getActivity(), responseData.getResult().getMessage(), Toast.LENGTH_SHORT).show();

                }
                removeProgressDialog();
                break;
        }


    }

    private void openGallery() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        getActivity().startActivityForResult(photoPickerIntent, Constants.OPEN_GALLERY);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case Constants.OPEN_GALLERY:
                if (resultCode == Activity.RESULT_OK) {
                    try {
                        Uri selectedImage = data.getData();
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};

                        Cursor cursor = getActivity().getContentResolver().query(
                                selectedImage, filePathColumn, null, null, null);
                        cursor.moveToFirst();

                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        String filePath = cursor.getString(columnIndex);
                        cursor.close();
                        Log.e("File", "filePath: " + filePath);

                        File file = new File(new URI("file://"
                                + filePath.replaceAll(" ", "%20")));
                        int maxImageSize = BitmapUtils.getMaxSize(getActivity());
                        Bitmap sourceBitmap = BitmapUtils.getScaledBitmap(file,
                                maxImageSize);

                        ExifInterface exif = new ExifInterface(file.getPath());
                        int orientation = exif.getAttributeInt(
                                ExifInterface.TAG_ORIENTATION,
                                ExifInterface.ORIENTATION_NORMAL);
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

                        originalImage = Bitmap.createBitmap(sourceBitmap, 0, 0,
                                sourceBitmap.getWidth(), sourceBitmap.getHeight(),
                                matrix, true);
                        sendUploadProfileImageRequest(originalImage);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.profile_image:

                openGallery();

                break;
        }
    }

    // for uploading image
    public void sendUploadProfileImageRequest(Bitmap originalImage) {
        showProgressDialog(getString(R.string.please_wait));
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        originalImage.compress(Bitmap.CompressFormat.PNG, 75, bao);
        byte[] ba = bao.toByteArray();
        String imageString = Base64.encodeToString(ba, Base64.DEFAULT);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("extension", "image/png");
            jsonObject.put("size", ba.length);
            jsonObject.put("byteCode", imageString);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        UserTable userTable = new UserTable((BaseApplication) getActivity().getApplication());
        UserModel userModel = userTable.getAllUserData();

        JSONArray jsonArray = new JSONArray();
        jsonArray.put(jsonObject);

        ImageUploadRequest requestData = new ImageUploadRequest();
        requestData.setImage(jsonArray.toString());
       // requestData.setUser_id("" + userModel.getUser().getId());
       // requestData.setSessionId("" + userModel.getUser().getSessionId());
       // requestData.setProfileId("" + userModel.getUser().getProfileId());

        ImageUploadController controller = new ImageUploadController(getActivity(), this);
        controller.getData(AppConstants.IMAGE_UPLOAD_REQUEST, requestData);
    }

    public void setProfileImage(String url) {
        //profile_image.setImageBitmap(bitmap);
        //url = "http://s9.postimg.org/n92phj9tr/image1.jpg";
        profileimgUrl = url;
        if (!StringUtils.isNullOrEmpty(url))
            Picasso.with(getActivity()).load(url).placeholder(R.drawable.family_xxhdpi).error(R.drawable.family_xxhdpi).transform(new RoundedTransformation()).into(profile_image);

    }
//
}
