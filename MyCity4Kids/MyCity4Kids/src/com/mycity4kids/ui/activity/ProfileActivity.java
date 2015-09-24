package com.mycity4kids.ui.activity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.kelltontech.utils.BitmapUtils;
import com.kelltontech.utils.ConnectivityUtils;
import com.kelltontech.utils.DateTimeUtils;
import com.kelltontech.utils.StringUtils;
import com.kelltontech.utils.ToastUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.controller.ConfigurationController;
import com.mycity4kids.controller.ImageUploadController;
import com.mycity4kids.controller.ProfileController;
import com.mycity4kids.dbtable.CityTable;
import com.mycity4kids.dbtable.LocalityTable;
import com.mycity4kids.dbtable.UserTable;
import com.mycity4kids.fragmentdialog.CameraFragmentDialog;
import com.mycity4kids.fragmentdialog.StartDatePicker;
import com.mycity4kids.interfaces.IGetDate;
import com.mycity4kids.interfaces.IOnSubmitGallery;
import com.mycity4kids.models.VersionApiModel;
import com.mycity4kids.models.city.MetroCity;
import com.mycity4kids.models.configuration.ConfigurationApiModel;
import com.mycity4kids.models.forgot.CommonResponse;
import com.mycity4kids.models.locality.Localities;
import com.mycity4kids.models.locality.LocalityData;
import com.mycity4kids.models.profile.KidsInformation;
import com.mycity4kids.models.profile.SaveProfileRequest;
import com.mycity4kids.models.profile.ViewProfileRequest;
import com.mycity4kids.models.profile.ViewProfileResponse;
import com.mycity4kids.models.user.ImageUploadRequest;
import com.mycity4kids.models.user.UserModel;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.widget.BitmapLruCache;

public class ProfileActivity extends BaseActivity implements IOnSubmitGallery,OnClickListener, IGetDate {

	private EditText userNameEdt, emailEdt, contactNoEdt;
	private TextView profileTextView;
	private NetworkImageView profileImage;
	private LinearLayout container;
	private File photo;
	private Bitmap originalImage;
	private Spinner cityDropDown;
	private Spinner localityDropDown;
	private UserModel userModel;
	private ArrayList<MetroCity> cityList;
	private ArrayList<Localities> localityList;
	private ArrayAdapter<MetroCity> mCityAdapter;
	private ArrayAdapter<Localities> mLocalityAdapter;
	private int childCount = 0;
	private int currentDatePickerTAG = 0;
	private int TAG = 1000;
	private int localityId;
	private boolean isLocationUpdated = true;
	private boolean isNewUser = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {
			setContentView(R.layout.activity_view_edit_profile);
			profileImage=(NetworkImageView) findViewById(R.id.profile_img);
			userNameEdt = (EditText) findViewById(R.id.userName_edit_txt);
			emailEdt = (EditText) findViewById(R.id.email_edit_txt);
			contactNoEdt = (EditText) findViewById(R.id.contact_edit_txt);
			((ImageView)findViewById(R.id.cross_icon)).setOnClickListener(this);
			((TextView) findViewById(R.id.btnSubmit)).setOnClickListener(this);
			((Button) findViewById(R.id.btnAddChild)).setOnClickListener(this);
			container = (LinearLayout) findViewById(R.id.internal_kid_layout);
			profileImage.setOnClickListener(this);
			profileImage.setDefaultImageResId(R.drawable.default_img);
			profileTextView = (TextView) findViewById(R.id.profile_txv);
			cityDropDown = (Spinner) findViewById(R.id.spinnerCity);
			mCityAdapter = new ArrayAdapter<MetroCity>(ProfileActivity.this,R.layout.city_locality_dropdown_row, getCityList());
			cityDropDown.setAdapter(mCityAdapter);
			cityDropDown.setOnItemSelectedListener(new OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
					if(childCount > 0 && (!isNewUser) && (((MetroCity) parent.getAdapter().getItem(pos)).getId() > 0)){
						fetchLocationData(((MetroCity) parent.getAdapter().getItem(pos)).getId());
					}
					isNewUser = false;
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
				}
			});

			localityDropDown = (Spinner) findViewById(R.id.spinnerLocality);
			mLocalityAdapter = new ArrayAdapter<Localities>(ProfileActivity.this, R.layout.city_locality_dropdown_row,getLocalityList());
			localityDropDown.setAdapter(mLocalityAdapter);

			/**
			 * Hit for ViewProfile
			 */
			if(!ConnectivityUtils.isNetworkEnabled(this)){
				ToastUtils.showToast(this, getString(R.string.error_network));
				finish();
				return;

			}
			viewProfile();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public ArrayList<MetroCity> getCityList() {
		MetroCity currentSaveModel = SharedPrefUtils
				.getCurrentCityModel(ProfileActivity.this);
		CityTable cityTable = new CityTable(
				(BaseApplication) getApplicationContext());
		cityList = (ArrayList<MetroCity>) (ArrayList<?>) cityTable.getAllCityData(currentSaveModel);

		if (!StringUtils.isNullOrEmpty(currentSaveModel.getName())) {
			MetroCity metroCityCurrent = new MetroCity();
			if (currentSaveModel.getName().contains("Delhi")
					&& currentSaveModel.getName().contains("-")) {
				String[] headerCityName = currentSaveModel.getName().split("-");
				metroCityCurrent.setName(headerCityName[0] + " "
						+ headerCityName[1].toUpperCase());
			} else {
				metroCityCurrent.setName(currentSaveModel.getName());
			}
			metroCityCurrent.setId(currentSaveModel.getId());
			cityList.add(0, metroCityCurrent);
		}
		MetroCity selectCity = new MetroCity();
		selectCity.setId(0);
		selectCity.setName("Select City");
		cityList.add(0, selectCity);
		return cityList;
	}

	public ArrayList<Localities> getLocalityList() {
		LocalityTable _table = new LocalityTable((BaseApplication) getApplicationContext());
		localityList = new ArrayList<Localities>();
		localityList.addAll(_table.getLocalities());
		Localities selectLocality = new Localities();
		try {
			Collections.sort(localityList, new Comparator<Localities>() {
				public int compare(Localities result1, Localities result2) {
					return result1.getName().compareTo(result2.getName());
				}
			});
		} catch (ClassCastException e) {
			e.printStackTrace();
		}
		selectLocality.setId(0);
		selectLocality.setName("Select Locality");
		localityList.add(0, selectLocality);
		return localityList;
	}

	private void viewProfile() {
		ViewProfileRequest requestData = new ViewProfileRequest();
		UserTable userTable = new UserTable((BaseApplication) this.getApplication());
		userModel = userTable.getAllUserData();

		if(!StringUtils.isNullOrEmpty(SharedPrefUtils.getProfileImgUrl(ProfileActivity.this)) && 
				!(SharedPrefUtils.getProfileImgUrl(ProfileActivity.this).equals(userModel.getProfile().getProfile_image()))) {
			try {
				profileImage.setImageUrl(SharedPrefUtils.getProfileImgUrl(ProfileActivity.this), new ImageLoader(Volley.newRequestQueue(ProfileActivity.this),new BitmapLruCache()));
				profileTextView.setVisibility(View.GONE);
			} catch (Exception ex) {
				profileImage.setErrorImageResId(R.drawable.default_img);
			}
		} else	if (!StringUtils.isNullOrEmpty(userModel.getProfile().getProfile_image())) {
			profileImage.setDefaultImageResId(R.drawable.default_img);
			try {
				profileImage.setImageUrl(userModel.getProfile().getProfile_image(), new ImageLoader(Volley.newRequestQueue(this),new BitmapLruCache()));
				profileTextView.setVisibility(View.GONE);
			} catch (Exception ex) {
				profileImage.setErrorImageResId(R.drawable.default_img);
			}
		} else {
			profileImage.setDefaultImageResId(R.drawable.default_img);
		}

		requestData.setUserId("" + userModel.getUser().getId());
		requestData.setSessionId("" + userModel.getUser().getSessionId());
		ProfileController _viewProfileController = new ProfileController(this, this);
		showProgressDialog("Please Wait...");
		_viewProfileController.getData(AppConstants.VIEW_PROFILE_REQUEST,requestData);
				
	}

	private void fetchLocationData(int cityId) {
		if(cityId > 0) {
			VersionApiModel versionApiModel = SharedPrefUtils.getSharedPrefVersion(ProfileActivity.this);
			versionApiModel.setCityId(cityId);
			final ConfigurationController _controller = new ConfigurationController(ProfileActivity.this, this);
		//	showProgressDialog("Please Wait...");
			_controller.getData(AppConstants.LOCATION_MY_PROFILE_REQUEST,versionApiModel);
		} 
	}

	private void addNewChild(String kidsId) {
		++childCount;
		LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				
		final RelativeLayout convertView = (RelativeLayout) layoutInflater.inflate(R.layout.custom_profile_cell, null);
		if(StringUtils.isNullOrEmpty(kidsId)){
			kidsId="";
		}
		convertView.setTag(kidsId);
		final ViewHolder holder = new ViewHolder();
		holder.nameOfKidEdt = (EditText) convertView.findViewById(R.id.name_of_kid);
		holder.dobOfKidSpn = (TextView) convertView.findViewById(R.id.SpinnerDOB);
		holder.dobOfKidSpn.setTag(TAG++);
		holder.dobOfKidSpn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				currentDatePickerTAG = Integer.valueOf(v.getTag().toString());
				StartDatePicker _Picker = new StartDatePicker();
				_Picker.setDateAction(ProfileActivity.this);
				_Picker.show(getSupportFragmentManager(), "");
			}
		});

		holder.genderOfKidRGp = (RadioGroup) findViewById(R.id.gender_of_kid_RG);
	/*	holder.removeBtn = (Button) convertView.findViewById(R.id.btnRemove);
		if (childCount == 1) {
			holder.removeBtn.setVisibility(View.GONE);
		} else {
			holder.removeBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					((LinearLayout) convertView.getParent())
					.removeView(convertView);
					--childCount;
				}
			});
		}
*/
		container.addView(convertView);
	}

	class ViewHolder {
		EditText nameOfKidEdt;
		TextView dobOfKidSpn;
		RadioGroup genderOfKidRGp;
		RadioButton girlRBtn, boyRBtn;
	//	Button removeBtn;
	}

	@Override
	public void getDateValue(String date) {
		for (int i = 0; i < container.getChildCount(); i++) {
			TextView kidsDOB = (TextView) ((RelativeLayout) container.getChildAt(i)).findViewById(R.id.SpinnerDOB);
			if (Integer.valueOf(kidsDOB.getTag().toString()) == currentDatePickerTAG) {
				//String year = date.split("-")[0];
				String day = date.split("-")[0];
				String month = date.split("-")[1];
				if(month.length() == 1) {
					month = "0" + month;
				}
			//	String day = date.split("-")[2];
				String year = date.split("-")[2];
				String formattedDate = year + "-" + month + "-" + day;

				if(DateTimeUtils.parseExtendedDate(formattedDate) > System.currentTimeMillis()) {
					kidsDOB.setFocusableInTouchMode(true);
					kidsDOB.setError("Please enter a valid date.");
					kidsDOB.requestFocus();
					return;
				} else {
					kidsDOB.setError(null);
					kidsDOB.setText(date);
				}
			}
		}
	}

	@Override
	protected void updateUi(final Response response) {
		removeProgressDialog();
		if (response == null) {
			addNewChild("");
			showToast(getString(R.string.toast_response_error));
			return;
		}
		switch (response.getDataType()) {

		case AppConstants.VIEW_PROFILE_REQUEST: {
			if (response.getResponseObject() instanceof ViewProfileResponse) {
				ViewProfileResponse _viewProfileResponse = (ViewProfileResponse) response.getResponseObject();
						
				if (_viewProfileResponse.getResponseCode() != 200) {
					addNewChild("");
					showToast(getString(R.string.toast_response_error));
					return;
				} else {
					setData(_viewProfileResponse);
				}
			}
			break;
		}

		case AppConstants.IMAGE_UPLOAD_REQUEST: {
			if (response.getResponseObject() instanceof CommonResponse) {
				CommonResponse responseModel = (CommonResponse) response
						.getResponseObject();
				if (responseModel.getResponseCode() != 200) {
					showToast(getString(R.string.toast_response_error));
					return;
				} else {
					if (!StringUtils.isNullOrEmpty(responseModel.getResult().getMessage())) {
						SharedPrefUtils.setProfileImgUrl(ProfileActivity.this, responseModel.getResult().getMessage());
						Log.i("Uploaded Image URL", responseModel.getResult().getMessage());
					}					
					setProfileImage(originalImage);
					showToast("You have successfully uploaded image.");
				}
			}
			break;
		}

		case AppConstants.LOCATION_MY_PROFILE_REQUEST:
			Object responseObject = response.getResponseObject();
			if (responseObject instanceof ConfigurationApiModel) {
				updateLocalityList(responseObject);
				break;
			}

		case AppConstants.SAVE_PROFILE_REQUEST:
			if (response.getResponseObject() instanceof CommonResponse) {
				CommonResponse commonResponse = (CommonResponse) response.getResponseObject();
				if (commonResponse.getResponseCode() != 200) {
					if(StringUtils.isNullOrEmpty(commonResponse.getResult().getMessage()))
					{
					showToast(getString(R.string.toast_response_error));
					}else{
						showToast(commonResponse.getResult().getMessage());
					}
					return;
				} else {
					updateUserInfo(response);
					showMessageAndFinish("You have successfully saved your profile.");
				}
				break;
			}
		}
	}

	/**
	 * @param pMessage
	 */
	private void showMessageAndFinish(String pMessage) {
		showToast(pMessage);
		finish();
	}
	
	private void updateUserInfo(Response response){
		if(response.getRequestData() instanceof SaveProfileRequest){
			SaveProfileRequest requestData=(SaveProfileRequest)response.getRequestData();
			UserTable userTable=new UserTable((BaseApplication)getApplication());
			userTable.updateUserTable(requestData);
		}
	}

	private void setData(ViewProfileResponse response) {
		if(StringUtils.isNullOrEmpty(response.getResult().getData().getLocalityId()) 
				&& StringUtils.isNullOrEmpty(response.getResult().getData().getMobileNumber())) {
			isNewUser = true;
		}
		
		String name = response.getResult().getData().getName();
		if (!StringUtils.isNullOrEmpty(name)) {
			userNameEdt.setText("" + name);
		}

		String email = response.getResult().getData().getEmailId();
		if (!StringUtils.isNullOrEmpty(email)) {
			emailEdt.setText("" + email);
		}

		String contact = response.getResult().getData()
				.getMobileNumber();
		if (!StringUtils.isNullOrEmpty(contact)) {
			contactNoEdt.setText("" + contact);
		}

/*		String parentType = response.getResult().getData().getParentType();
		if (!StringUtils.isNullOrEmpty(parentType)) {
			if (parentType.equalsIgnoreCase("Mom")) {
				((RadioButton)findViewById(R.id.mom_radio_btn)).setChecked(true);
			} else if (parentType.equalsIgnoreCase("Dad")) {
				((RadioButton)findViewById(R.id.dad_radio_btn)).setChecked(true);
			} else if (parentType.equalsIgnoreCase("Just Love Kids")) {
				((RadioButton)findViewById(R.id.jlk_radio_btn)).setChecked(true);
			}
		}
*/
		ArrayList<KidsInformation> kidsInfoList = response.getResult().getData().getKidsInformation().getKidsInformation();
		if ((kidsInfoList != null) && !(kidsInfoList.isEmpty())) {
			for (int position = 0; position < kidsInfoList.size(); position++) {
				addNewChild(kidsInfoList.get(position).getId());
				RelativeLayout innerLayout = (RelativeLayout) container.getChildAt(position);
						
				String kidsName = kidsInfoList.get(position).getName();
				if (!StringUtils.isNullOrEmpty(kidsName)) {
					((EditText) innerLayout.findViewById(R.id.name_of_kid)).setText("" + kidsName);
					
				}
				String kidsDOB = kidsInfoList.get(position).getDob();
				if (!StringUtils.isNullOrEmpty(kidsDOB)) {
					((TextView) innerLayout.findViewById(R.id.SpinnerDOB)).setText("" + kidsDOB);
					
				}
				String kidsGender = kidsInfoList.get(position).getGender();
				if (!StringUtils.isNullOrEmpty(kidsGender)) {
					if (kidsGender.equalsIgnoreCase("Boy") || kidsGender.equalsIgnoreCase("Male")) {
						((RadioButton) innerLayout.findViewById(R.id.boy_radio_btn)).setChecked(true);
					} else if (kidsGender.equalsIgnoreCase("Girl") || kidsGender.equalsIgnoreCase("Female")) {
						((RadioButton) innerLayout.findViewById(R.id.girl_radio_btn)).setChecked(true);
					}
				}
			}
		} else {
			addNewChild("");
		}

		if (Integer.parseInt(response.getResult().getData().getCityId()) > 0) {
			for (int i = 0; i < mCityAdapter.getCount(); i++) {
				if (mCityAdapter.getItem(i).getId() == Integer.parseInt(response.getResult().getData().getCityId())) {
					cityDropDown.setSelection(i);
				}
			}
		} else {
			cityDropDown.setSelection(0);
			return;
		}
		
		if (!StringUtils.isNullOrEmpty(response.getResult().getData().getLocalityId())) {
			localityId = Integer.parseInt(response.getResult().getData().getLocalityId());
		}
		
		isLocationUpdated = false;
//		isNewUser = false;
	}

	private void updateLocalityList(Object responseObject) {
		ArrayList<Localities> localityList = new ArrayList<Localities>();

		ConfigurationApiModel _configurationResponse = (ConfigurationApiModel) responseObject;
		ArrayList<LocalityData> localityByZone = _configurationResponse
				.getResult().getData().getLocalityApi().getData();
		for (LocalityData localityData : localityByZone) {
			ArrayList<Localities> localities = localityData.getLocalities();
			for (Localities localities2 : localities) {
				if (localityData.getId() != localities2.getId()) {
					localityList.add(localities2);
				}
			}
		}
		try {
			Collections.sort(localityList, new Comparator<Localities>() {
				public int compare(Localities result1, Localities result2) {
					return result1.getName().compareTo(result2.getName());
				}
			});
		} catch (ClassCastException e) {
			e.printStackTrace();
		}

		Localities selectLocality = new Localities();
		selectLocality.setId(0);
		selectLocality.setName("Select Locality");
		localityList.add(0, selectLocality);

		mLocalityAdapter = new ArrayAdapter<Localities>(ProfileActivity.this,
				R.layout.city_locality_dropdown_row, localityList);
		localityDropDown.setAdapter(mLocalityAdapter);

		if(!isLocationUpdated) {
			if (localityId > 0) {
				for (int i = 0; i < mLocalityAdapter.getCount(); i++) {
					if (mLocalityAdapter.getItem(i).getId() == localityId) {
						localityDropDown.setSelection(i);
					}
				}
			} else {
				localityDropDown.setSelection(0);
				return;
			}
			isLocationUpdated = true;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.cross_icon:
			finish();
			break;

		case R.id.btnSubmit:
			saveProfile();
			break;

		case R.id.btnAddChild:
			addNewChild("");
			break;

		case R.id.profile_img:
			uploadProfileImage();
			break;
		}

	}

	private void saveProfile() {
		SaveProfileRequest profileData=getProfileData();
		if(isDataValid(profileData))
		{
			showProgressDialog(getString(R.string.please_wait));
			if(!ConnectivityUtils.isNetworkEnabled(ProfileActivity.this)){
				ToastUtils.showToast(ProfileActivity.this, getString(R.string.error_network));
				return;
			}

			ProfileController _profileController=new ProfileController(this, this);
			_profileController.getData(AppConstants.SAVE_PROFILE_REQUEST, profileData);
		}
	}

	private SaveProfileRequest getProfileData() {
		SaveProfileRequest profile = new SaveProfileRequest();
		profile.setUserId("" + userModel.getUser().getId());
		profile.setSessionId("" + userModel.getUser().getSessionId());
		profile.setProfileId("" + userModel.getUser().getProfileId());
		profile.setName("" + userNameEdt.getText().toString().trim());
		profile.setEmailId("" + emailEdt.getText().toString().trim());
		profile.setMobileNumber("" + contactNoEdt.getText().toString().trim());
//		profile.setParentType("" + getParentType());
		profile.setCityId("" + ((MetroCity)cityDropDown.getSelectedItem()).getId());
		profile.setLocalityId("" + ((Localities)localityDropDown.getSelectedItem()).getId());
		profile.setKidsInformation(getKidsInfo());

		return profile;
	}

/*	private String getParentType() {
		String parentType = "";
		if(((RadioButton)findViewById(R.id.mom_radio_btn)).isChecked()) {
			parentType = "mom";
		}else if(((RadioButton)findViewById(R.id.dad_radio_btn)).isChecked()) {
			parentType = "dad";
		}else if(((RadioButton)findViewById(R.id.jlk_radio_btn)).isChecked()) {
			parentType = "just love kids";
		}
		return parentType;
	}
*/
	private ArrayList<KidsInformation> getKidsInfo() {
		ArrayList<KidsInformation> kidsInfoList = new ArrayList<KidsInformation>(); 
		for (int position = 0; position < container.getChildCount(); position++)
		{ 
			RelativeLayout innerLayout = (RelativeLayout) container.getChildAt(position);
			if(!StringUtils.isNullOrEmpty(((EditText) innerLayout.findViewById(R.id.name_of_kid)).getText().toString().trim())) {
				KidsInformation kidsInformation = new KidsInformation();
				kidsInformation.setName(((EditText) innerLayout.findViewById(R.id.name_of_kid)).getText().toString().trim());
				kidsInformation.setDob(((TextView) innerLayout.findViewById(R.id.SpinnerDOB)).getText().toString().trim());
				kidsInformation.setGender(((RadioButton) innerLayout.findViewById(R.id.girl_radio_btn)).isChecked() ? "Girl" : "Boy");
				kidsInformation.setId((String)innerLayout.getTag());
				kidsInfoList.add(kidsInformation);
			}
		}
		return kidsInfoList;
	}

	private boolean isDataValid(SaveProfileRequest request){
		boolean isProfileOk=true;
		if(request.getName().trim().length() == 0 ){
			userNameEdt.setFocusableInTouchMode(true);
			userNameEdt.setError("Name cannot be empty.");
			userNameEdt.requestFocus();
			isProfileOk=false;
		} else if(request.getMobileNumber().trim().length() == 0 ){
			contactNoEdt.setFocusableInTouchMode(true);
			contactNoEdt.setError("Mobile number cannot be empty.");
			contactNoEdt.requestFocus();
			isProfileOk=false;
		} else if(Integer.valueOf(request.getCityId()) == 0 ){
			showToast("Select your city name.");
			isProfileOk=false;
		} else if(Integer.valueOf(request.getLocalityId()) == 0 ){
			showToast("Select your locality name.");
			isProfileOk=false;
		}
		return isProfileOk;
	}

	private void uploadProfileImage() {
		CameraFragmentDialog fragmentDialog = new CameraFragmentDialog();
		fragmentDialog.setSubmitListner(ProfileActivity.this);
		fragmentDialog.show(this.getSupportFragmentManager(), "");
	}

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
		startActivityForResult(photoPickerIntent, Constants.OPEN_GALLERY);
	}

	private void takePhoto() {
		Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
		photo = new File(Environment.getExternalStorageDirectory(), "Pic.jpg");
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo));
		startActivityForResult(intent, Constants.TAKE_PICTURE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
		case Constants.TAKE_PICTURE:
			if (resultCode == Activity.RESULT_OK) {
				try {
					int maxImageSize = BitmapUtils.getMaxSize(this);
					Bitmap sourceBitmap = BitmapUtils.getScaledBitmap(photo,
							maxImageSize);

					ExifInterface exif = new ExifInterface(photo.getPath());
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

				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
			break;
		case Constants.OPEN_GALLERY:
			if (resultCode == Activity.RESULT_OK) {
				try {
					Uri selectedImage = data.getData();
					String[] filePathColumn = { MediaStore.Images.Media.DATA };

					Cursor cursor = this.getContentResolver().query(
							selectedImage, filePathColumn, null, null, null);
					cursor.moveToFirst();

					int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
					String filePath = cursor.getString(columnIndex);
					cursor.close();
					Log.e("File", "filePath: " + filePath);

					File file = new File(new URI("file://"
							+ filePath.replaceAll(" ", "%20")));
					int maxImageSize = BitmapUtils.getMaxSize(this);
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

	public void sendUploadProfileImageRequest(Bitmap originalImage) {
		showProgressDialog(getResources().getString(R.string.please_wait));
		ByteArrayOutputStream bao = new ByteArrayOutputStream();
		originalImage.compress(Bitmap.CompressFormat.PNG, 100, bao);
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

		JSONArray jsonArray = new JSONArray();
		jsonArray.put(jsonObject);

		ImageUploadRequest requestData = new ImageUploadRequest();
		requestData.setImage(jsonArray.toString());
		requestData.setUser_id("" + userModel.getUser().getId());
		requestData.setSessionId("" + userModel.getUser().getSessionId());
		requestData.setProfileId("" + userModel.getUser().getProfileId());

		ImageUploadController controller = new ImageUploadController(this, this);
		controller.getData(AppConstants.IMAGE_UPLOAD_REQUEST, requestData);
	}

	public void setProfileImage(Bitmap bitmap) {
		profileImage.setImageBitmap(bitmap);
		profileTextView.setVisibility(View.GONE);
		if(!StringUtils.isNullOrEmpty(SharedPrefUtils.getProfileImgUrl(ProfileActivity.this))) {
			try {
				profileImage.setImageUrl(SharedPrefUtils.getProfileImgUrl(ProfileActivity.this), new ImageLoader(Volley.newRequestQueue(ProfileActivity.this),new BitmapLruCache()));
			} catch (Exception ex) {
				profileImage.setErrorImageResId(R.drawable.default_img);
			}
		}
	}
}
