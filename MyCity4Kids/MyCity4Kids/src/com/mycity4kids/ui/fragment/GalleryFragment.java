package com.mycity4kids.ui.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseFragment;
import com.kelltontech.utils.BitmapUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.dbtable.UserTable;
import com.mycity4kids.fragmentdialog.CameraFragmentDialog;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.interfaces.IOnSubmitGallery;
import com.mycity4kids.models.businesseventdetails.DetailsGallery;
import com.mycity4kids.models.businesseventdetails.GalleryListtingData;
import com.mycity4kids.models.businesseventdetails.VideoListingDetails;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.ui.activity.BusinessDetailsActivity;
import com.mycity4kids.ui.activity.PhotoSlideActivity;
import com.mycity4kids.ui.activity.YouTubeVideoActivity;
import com.mycity4kids.ui.adapter.GalleryAdapter;
import com.mycity4kids.utils.PermissionUtil;
import com.mycity4kids.widget.CustomGridView;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;

public class GalleryFragment extends BaseFragment implements IOnSubmitGallery {

    private static final int REQUEST_CAMERA_PERMISSION = 2;
    private static final int REQUEST_GALLERY_PERMISSION = 3;

    private static String[] PERMISSIONS_STORAGE_CAMERA = {Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};

    private int mEventOrBusiness;
    private String mBusinessOrEventId;
    private File photo;
    private GalleryAdapter adapter;
    private ArrayList<GalleryListtingData> photos;
    private View rootLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gallery, null, false);
        Utils.pushOpenScreenEvent(getActivity(), "Gallery resource/events", SharedPrefUtils.getUserDetailModel(getActivity()).getDynamoId() + "");

        try {
            rootLayout = view.findViewById(R.id.scroll);

            CustomGridView _gridView = (CustomGridView) view.findViewById(R.id.galary_view);
            _gridView.setIsExpanded(true);
            _gridView.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {

                    if (pos == 0) {
                        UserTable _table = new UserTable((BaseApplication) getActivity().getApplicationContext());
                        int count = _table.getCount();
                        //if(count<=0){
                        //((BusinessDetailsActivity) getActivity()).mDetailsHeader.goToLoginDialog();
                        //return;
                        //}
                        CameraFragmentDialog fragmentDialog = new CameraFragmentDialog();
                        fragmentDialog.setSubmitListner(GalleryFragment.this);
                        fragmentDialog.show(getActivity().getSupportFragmentManager(), "");
                    } else {
                    /*Intent intent=new Intent(getActivity(),PhotoActivity.class);
                    if(parent.getAdapter() instanceof GalleryAdapter ){
						GalleryListtingData galleryData=(GalleryListtingData) ((GalleryAdapter)parent.getAdapter()).getItem(pos);

						if(galleryData.getImageUrl()!=null){
							intent.putExtra("photoUrl", galleryData.getImageUrl());
						}

						ByteArrayOutputStream stream = new ByteArrayOutputStream();
						if(galleryData.getImageBitmap()!=null){
							galleryData.getImageBitmap().compress(Bitmap.CompressFormat.PNG, 100, stream);

							byte[] byteArray = stream.toByteArray();
							intent.putExtra("imageArray",byteArray );
						}
						startActivity(intent);
					}*/

                        ArrayList<GalleryListtingData> photoList = ((GalleryAdapter) parent.getAdapter()).galleryPhotoList();
                        if (photoList != null && (photoList.size() > 0)) {

                            //photoList.remove(0);
                            Intent intent1 = new Intent(getActivity(), PhotoSlideActivity.class);
                            intent1.putParcelableArrayListExtra("photoList", photoList);
                            intent1.putExtra("position", pos);
                            startActivity(intent1);


                        } else {
                            Toast.makeText(getActivity(), "No photos available!", Toast.LENGTH_SHORT).show();
                        }

                    }
                }

            });


            CustomGridView _gridVideoView = (CustomGridView) view.findViewById(R.id.video_view);
            _gridVideoView.setIsExpanded(true);
            Bundle bundle = getArguments();
            if (bundle != null) {


                mEventOrBusiness = getArguments().getInt(Constants.PAGE_TYPE);
                mBusinessOrEventId = getArguments().getString(Constants.BUSINESS_OR_EVENT_ID);
                DetailsGallery detailsGallery = bundle.getParcelable("BusinessGallery");
                photos = new ArrayList<GalleryListtingData>();
                ArrayList<String> photosList = new ArrayList<String>();
                ArrayList<String> photosListServer = detailsGallery.getPhoto().getBusiness_pics();
                ArrayList<String> userUploadedPhoto = detailsGallery.getPhoto().getUser_uploaded();
                if (photosListServer != null) {
                    photosList.addAll(photosListServer);
                }
                if (userUploadedPhoto != null) {
                    photosList.addAll(userUploadedPhoto);
                }


                ArrayList<String> eventList = detailsGallery.getPhoto().getEvent_pics();
                GalleryListtingData data = new GalleryListtingData();
                BitmapFactory.Options options = null;
                options = new BitmapFactory.Options();
                options.inSampleSize = 2;

                Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.cameraicon, options);
                // data.setResourceId(resourceId);
                data.setImageBitmap(largeIcon);
			/*if(largeIcon!=null && largeIcon.isRecycled()){
				largeIcon.recycle();
				largeIcon=null;
				
			}*/
                data.setImagePath("");
                photos.add(data);
                if (photosList != null && !photosList.isEmpty()) {

                    for (String lists : photosList) {
                        GalleryListtingData dataList = new GalleryListtingData();

                        dataList.setImageUrl(lists);
                        //	data.setResourceId(0);
                        photos.add(dataList);
                    }
                } else if (eventList != null && !eventList.isEmpty()) {
                    {
                        for (String lists : eventList) {
                            GalleryListtingData dataList = new GalleryListtingData();
                            dataList.setImageUrl(lists);
                            //	data.setResourceId(0);
                            photos.add(dataList);
                        }
                    }
                }

                adapter = new GalleryAdapter(getActivity(), R.layout.custom_gallery_cell, Constants.FIRST_GALLERY);
                adapter.setData(photos);
                _gridView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                if (detailsGallery.getListing_videos().size() == 0 || detailsGallery.getListing_videos().isEmpty()) {
                    view.findViewById(R.id.video_txt).setVisibility(View.GONE);
                }
                /**
                 * gallery video now replaced on review page
                 * it's a CR But now this is commented due to future perspective-1 april
                 *
                 * Now again according to CR it will show in gallery := 9 april
                 */


                ArrayList<GalleryListtingData> videosArrayList = new ArrayList<GalleryListtingData>();
                for (VideoListingDetails videoDetail : detailsGallery.getListing_videos()) {
                    GalleryListtingData videoListingData = new GalleryListtingData();
                    videoListingData.setImageUrl(videoDetail.getThumbnail());
                    videoListingData.setPlayImageUrl(videoDetail.getUrl());
                    videosArrayList.add(videoListingData);
                }

                //			GalleryListtingData _listingData=new GalleryListtingData();
                //			_listingData.setImageUrl(dummyVideoArray[0]);
                //			videosArrayList.add(_listingData);
                //			GalleryListtingData _listingData1=new GalleryListtingData();
                //			_listingData1.setImageUrl(dummyVideoArray[1]);
                //			videosArrayList.add(_listingData1);

                GalleryAdapter adapter1 = new GalleryAdapter(getActivity(), R.layout.custom_gallery_cell, Constants.SECOND_GALLERY);
                adapter1.setData(videosArrayList);

                _gridVideoView.setAdapter(adapter1);

                _gridVideoView.setOnItemClickListener(new OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                        if (parent.getAdapter() instanceof GalleryAdapter) {
                            GalleryListtingData galleryVideoData = (GalleryListtingData) ((GalleryAdapter) parent.getAdapter()).getItem(pos);
                            Intent intent = new Intent(getActivity(), YouTubeVideoActivity.class);
                            intent.putExtra("youTubeUrl", galleryVideoData.getPlayImageUrl());
                            startActivity(intent);
                        }

                    }
                });


            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Intent intent = new Intent(DashboardActivity.this, ImageCropActivity.class);
        switch (requestCode) {
            case Constants.TAKE_PICTURE:
                if (resultCode == Activity.RESULT_OK) {


                    try {
                        int maxImageSize = BitmapUtils.getMaxSize(getActivity());
                        Bitmap sourceBitmap = BitmapUtils.getScaledBitmap(photo, maxImageSize);


                        ExifInterface exif = new ExifInterface(photo.getPath());
                        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

                        //	Log.e(TAG, "oreination" + orientation);
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

                        Bitmap originalImage = Bitmap.createBitmap(sourceBitmap, 0, 0, sourceBitmap.getWidth(), sourceBitmap.getHeight(), matrix, true);
                        GalleryListtingData _data = new GalleryListtingData();
                        _data.setImageBitmap(originalImage);
                        _data.setImagePath(photo.getPath());
                        photos.add(_data);
					/*adapter=new GalleryAdapter(getActivity(), R.layout.custom_gallery_cell,photos,Constants.FIRST_GALLERY);

						_gridView.setAdapter(adapter);*/
                        adapter.setData(photos);

                        adapter.notifyDataSetChanged();

                        ((BusinessDetailsActivity) getActivity()).sendUploadBusinessImageRequest(originalImage);

                        //	startActivity(intent);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        //Toast.makeText(DashboardActivity.this, R.string.image_size_error, Toast.LENGTH_LONG).show();
                    }
                }
                break;
            case Constants.OPEN_GALLERY:
                if (resultCode == Activity.RESULT_OK) {
                    try {
                        Uri selectedImage = data.getData();
                        String[] filePathColumn = {
                                MediaStore.Images.Media.DATA
                        };

                        Cursor cursor = getActivity().getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                        cursor.moveToFirst();

                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        String filePath = cursor.getString(columnIndex);
                        cursor.close();
                        Log.e("File", "filePath: " + filePath);

                        File file = new File(new URI("file://" + filePath.replaceAll(" ", "%20")));
                        int maxImageSize = BitmapUtils.getMaxSize(getActivity());
                        Bitmap sourceBitmap = BitmapUtils.getScaledBitmap(file, maxImageSize);


                        ExifInterface exif = new ExifInterface(file.getPath());
                        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                                ExifInterface.ORIENTATION_NORMAL);
                        //	Log.e(TAG, "oreination" + orientation);
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

                        Bitmap originalImage = Bitmap.createBitmap(sourceBitmap, 0, 0, sourceBitmap.getWidth(),
                                sourceBitmap.getHeight(), matrix, true);
                        GalleryListtingData _data = new GalleryListtingData();
                        _data.setImageBitmap(originalImage);
                        _data.setImagePath(file.toString());
                        photos.add(_data);
                        adapter.setData(photos);

                        adapter.notifyDataSetChanged();
                        /**
                         * add photo on server side-deepanker Chaudahry
                         */
                        ((BusinessDetailsActivity) getActivity()).sendUploadBusinessImageRequest(originalImage);
                        //	startActivity(intent);
                    } catch (Exception e) {
                        //Toast.makeText(DashboardActivity.this, R.string.image_size_error, Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

	/*@Override
	public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {

		switch (view.getId()) {
		case R.id.galary_view:
			if(pos==0){
			CameraFragmentDialog	 fragmentDialog = new CameraFragmentDialog(); 
					fragmentDialog.setSubmitListner(this);
			fragmentDialog.show(getActivity().getSupportFragmentManager(), "");
			}


   Log.i("position", ""+pos);
			break;
		case R.id.video_view:

			break;
		default:
			break;
		}

	}*/

    @Override
    public void setOnSubmitListner(String type) {
        if (type.equals(Constants.ALBUM_TYPE)) {
            openGallery();
        } else if (type.endsWith(Constants.GALLERY_TYPE)) {
            takePhoto();
        }

    }


    private void openGallery() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions("gallery");
            } else {
                startActivityForResult(photoPickerIntent, Constants.OPEN_GALLERY);
            }
        } else {
            startActivityForResult(photoPickerIntent, Constants.OPEN_GALLERY);
        }
    }

    private void takePhoto() {
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        photo = new File(Environment.getExternalStorageDirectory(), "Pic.jpg");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo));

        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions("camera");
            } else {
                startActivityForResult(intent, Constants.TAKE_PICTURE);
            }
        } else {
            startActivityForResult(intent, Constants.TAKE_PICTURE);
        }
    }

    private void requestPermissions(final String imageFrom) {
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
            Snackbar.make(rootLayout, R.string.permission_storage_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            requestUngrantedPermissions(imageFrom);
                        }
                    })
                    .show();
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                Manifest.permission.CAMERA)) {

            // Display a SnackBar with an explanation and a button to trigger the request.
            Snackbar.make(rootLayout, R.string.permission_camera_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            requestUngrantedPermissions(imageFrom);
                        }
                    })
                    .show();
        } else {
            requestUngrantedPermissions(imageFrom);
        }
    }

    private void requestUngrantedPermissions(String imageFrom) {
        ArrayList<String> permissionList = new ArrayList<>();
        for (int i = 0; i < PERMISSIONS_STORAGE_CAMERA.length; i++) {
            if (ActivityCompat.checkSelfPermission(getActivity(), PERMISSIONS_STORAGE_CAMERA[i]) != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(PERMISSIONS_STORAGE_CAMERA[i]);
            }
        }
        String[] requiredPermission = permissionList.toArray(new String[permissionList.size()]);
        if ("gallery".equals(imageFrom)) {
            requestPermissions(requiredPermission, REQUEST_GALLERY_PERMISSION);
        } else if ("camera".equals(imageFrom)) {
            requestPermissions(requiredPermission, REQUEST_CAMERA_PERMISSION);
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
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                photo = new File(Environment.getExternalStorageDirectory(), "Pic.jpg");
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo));
                startActivityForResult(intent, Constants.TAKE_PICTURE);
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
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, Constants.OPEN_GALLERY);
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

    @Override
    protected void updateUi(Response response) {
        // TODO Auto-generated method stub

    }

	/*	private void sendUploadBusinessImageRequest(Bitmap originalImage) {
		//((BusinessDetailsActivity) getActivity()).showProgressDialog(getResources().getString(R.string.please_wait));
		ByteArrayOutputStream bao = new ByteArrayOutputStream();
		originalImage.compress(Bitmap.CompressFormat.PNG, 100, bao);
		byte [] ba = bao.toByteArray();
		String imageString=Base64.encodeToString(ba,Base64.DEFAULT);

		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("extension", "image/png");
			jsonObject.put("size", ba.length);
			jsonObject.put("byteCode", imageString);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		JSONArray jsonArray = new JSONArray();
		jsonArray.put(jsonObject);

		BusinessImageUploadRequest requestData = new BusinessImageUploadRequest();
		requestData.setImage(jsonArray.toString());

		if(mEventOrBusiness==Constants.BUSINESS_PAGE_TYPE){
			requestData.setType("business");
		}else if(mEventOrBusiness==Constants.EVENT_PAGE_TYPE){
			requestData.setType("event");
		}

		requestData.setBusinessId(mBusinessOrEventId);

		UserTable userTable = new UserTable((BaseApplication)getActivity().getApplication());
		int count=userTable.getCount();
		if(count<=0){
		//	((BusinessDetailsActivity) getActivity()).removeProgressDialog();
			((BusinessDetailsActivity) getActivity()).showToast(getResources().getString(R.string.user_login));
			return;
		}
		UserModel userModel = userTable.getAllUserData();
		requestData.setUserId(""+userModel.getUser().getId());
		requestData.setSessionId(userModel.getUser().getSessionId());

		ImageUploadController controller = new ImageUploadController(getActivity(), (IScreen)getActivity());
		controller.getData(AppConstants.UPLOAD_BUSINESS_IMAGE_REQUEST, requestData);
	}*/


}
