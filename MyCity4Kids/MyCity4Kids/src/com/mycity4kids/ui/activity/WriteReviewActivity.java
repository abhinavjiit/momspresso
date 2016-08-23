package com.mycity4kids.ui.activity;

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
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.kelltontech.utils.BitmapUtils;
import com.kelltontech.utils.ConnectivityUtils;
import com.kelltontech.utils.StringUtils;
import com.kelltontech.utils.ToastUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.controller.AutoSuggestController;
import com.mycity4kids.controller.WriteReviewController;
import com.mycity4kids.dbtable.CategoryListTable;
import com.mycity4kids.dbtable.UserTable;
import com.mycity4kids.fragmentdialog.CameraFragmentDialog;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.interfaces.IOnSubmitGallery;
import com.mycity4kids.models.WriteReviewModel;
import com.mycity4kids.models.autosuggest.AutoSuggestModelData;
import com.mycity4kids.models.autosuggest.AutoSuggestReviewResponse;
import com.mycity4kids.models.category.CategoryModel;
import com.mycity4kids.models.forgot.CommonResponse;
import com.mycity4kids.models.user.UserModel;
import com.mycity4kids.preference.SharedPrefUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.URI;
import java.util.ArrayList;

public class WriteReviewActivity extends BaseActivity implements IOnSubmitGallery, OnClickListener {


    private EditText mwhatReviewEtxt;
    private ListView mSearchList;
    private LinearLayout innerLayout;
    private Button submit;
    private WriteReviewController _controller;
    private File photo;
    private Spinner writeReviewSpinner;
    private int categoryId;
    private String businessOrEventId;
    private int businessOrEvent;
    private String imageString;
    private int photosCount = 0;
    JSONArray markArray = null;
    //	private TextView textHeader;
    private TextView buttonUpLoadImage;
    private CategoryModel currentCategoryModel;
    private AutoSuggestModelData currentAutoSuggestModel;
    private EditText mTitleReviewEtxt;
    private EditText mDescriptionReviewEtxt;
    private RatingBar mRatingReviewEtxt;
    private String ratingValue = "";
    private AutoSuggestController mAutoSuggestController;
    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            Utils.pushOpenScreenEvent(WriteReviewActivity.this, "Write Review", SharedPrefUtils.getUserDetailModel(this).getId() + "");

            setContentView(R.layout.activity_write_a_review);
            mToolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(mToolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Write Review");
            buttonUpLoadImage = (TextView) findViewById(R.id.add_photo);
            buttonUpLoadImage.setOnClickListener(this);
            mSearchList = (ListView) findViewById(R.id.searchList);
            innerLayout = (LinearLayout) findViewById(R.id.internal_layout);
            mwhatReviewEtxt = (EditText) findViewById(R.id.name_of_event);
            mTitleReviewEtxt = (EditText) findViewById(R.id.title_for_review);
            mDescriptionReviewEtxt = (EditText) findViewById(R.id.description_for_review);
            mRatingReviewEtxt = (RatingBar) findViewById(R.id.review_ratingBar);
            //	mwhatReviewEtxt.setThreshold(1);
            mwhatReviewEtxt.addTextChangedListener(textWatcher);
            writeReviewSpinner = (Spinner) findViewById(R.id.SpinnerWhatReview);
//			textHeader=(TextView)findViewById(R.id.txvHeaderText);
//			textHeader.setText("Write A Review");
            ((TextView) findViewById(R.id.backbtn)).setOnClickListener(this);

            //	mwhatReviewEtxt.setOnFocusChangeListener(this);
            mAutoSuggestController = new AutoSuggestController(this, this);
            ((TextView) findViewById(R.id.save)).setOnClickListener(this);
            markArray = new JSONArray();
            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                categoryId = bundle.getInt(Constants.CATEGORY_ID, 0);
                businessOrEvent = bundle.getInt(Constants.PAGE_TYPE, 0);
                businessOrEventId = bundle.getString(Constants.BUSINESS_OR_EVENT_ID);
                writeReviewSpinner.setVisibility(View.GONE);
                mwhatReviewEtxt.setVisibility(View.GONE);
                ((TableRow) findViewById(R.id.spinner_row)).setVisibility(View.GONE);
                ((TableRow) findViewById(R.id.auto_suggest_row)).setVisibility(View.GONE);
            } else {

                CategoryListTable _categoryTable = new CategoryListTable((BaseApplication) getApplication());
                ArrayList<CategoryModel> categoryData = _categoryTable.getCategoriesNameId();
                CategoryModel model = new CategoryModel();
                model.setCategoryId(0);
                model.setCategoryName("What would you like to review?");
                categoryData.add(0, model);
                ArrayAdapter<CategoryModel> adapter = new ArrayAdapter<CategoryModel>(this, R.layout.text_for_write_review, categoryData);
                writeReviewSpinner.setAdapter(adapter);
            }

            writeReviewSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                    if (parent.getAdapter() instanceof ArrayAdapter<?>) {
                        currentCategoryModel = (CategoryModel) parent.getAdapter().getItem(pos);
                        categoryId = currentCategoryModel.getCategoryId();
                        //currentCategoryModel
                        //	mwhatReviewEtxt.setAdapter(new SuggestionAdapter(getApplicationContext(), R.layout.text_for_locality, currentCategoryModel.getCategoryId()));
                    }

                }

                @Override
                public void onNothingSelected(AdapterView<?> arg0) {
                    // TODO Auto-generated method stub

                }
            });
            mSearchList.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int pos, long id) {
                    if (parent.getAdapter() instanceof ArrayAdapter<?>) {
                        currentAutoSuggestModel = (AutoSuggestModelData) parent.getAdapter().getItem(pos);
                        mwhatReviewEtxt.removeTextChangedListener(textWatcher);
                        mwhatReviewEtxt.setText(currentAutoSuggestModel.getName());
                        mSearchList.setVisibility(View.GONE);
                        innerLayout.setVisibility(View.VISIBLE);
                        mwhatReviewEtxt.addTextChangedListener(textWatcher);
                    }

                }
            });
            /*	mwhatReviewEtxt.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View arg1,
						int pos, long arg3) {
					hideSoftKeyboard();
				//	mwhatReviewEtxt.addTextChangedListener(textWatcher);
					if (parent.getAdapter() instanceof SuggestionAdapter) {
						currentAutoSuggestModel=(AutoSuggestModelData)parent.getAdapter().getItem(pos);

					} 

				}
			});*/


			/*mwhatReviewEtxt.setOnTouchListener(new OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {
				hideSoftKeyboard();
					return false;
				}
			});

			mwhatReviewEtxt.setOnFocusChangeListener(new OnFocusChangeListener() {

				@Override
				public void onFocusChange(View v, boolean hasFocus) {
				hideSoftKeyboard();

				}
			});
			 */
            /*mwhatReviewEtxt.setOnKeyListener(new OnKeyListener() {

				@Override
				public boolean onKey(View v, int keyCode, KeyEvent event) {
					hideSoftKeyboard();
					return false;
				}
			});*/

            //	mwhatReviewEtxt.seton

            mRatingReviewEtxt.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {

                @Override
                public void onRatingChanged(RatingBar ratingBar, float rating,
                                            boolean fromUser) {
                    ratingValue = String.valueOf(rating);
                    Log.d("check", "rating" + ratingValue);
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /*@Override
    public boolean dispatchKeyEvent(KeyEvent event) {
         int keyaction = event.getAction();

            if(keyaction == KeyEvent.ACTION_DOWN)
            {
                int keycode = event.getKeyCode();
                int keyunicode = event.getUnicodeChar(event.getMetaState() );
                char character = (char) keyunicode;

                System.out.println("DEBUG MESSAGE KEY=" + character + " KEYCODE=" +  keycode);
            }

        return super.dispatchKeyEvent(event);
    }*/
    /*@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
        case KeyEvent.KEYCODE_A:
        {
          System.out.println("ys thizs on e");  //your Action code
            return true;
        }
    }
		return super.onKeyDown(keyCode, event);
	}*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        photosCount++;
        switch (requestCode) {
            case Constants.TAKE_PICTURE:
                if (resultCode == Activity.RESULT_OK) {
                    try {
                        int maxImageSize = BitmapUtils.getMaxSize(this);
                        Bitmap sourceBitmap = BitmapUtils.getScaledBitmap(photo, maxImageSize);
                        ExifInterface exif = new ExifInterface(photo.getPath());
                        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
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
                        ByteArrayOutputStream bao = new ByteArrayOutputStream();
                        originalImage.compress(Bitmap.CompressFormat.PNG, 100, bao);
                        byte[] ba = bao.toByteArray();
                        imageString = Base64.encodeToString(ba, Base64.DEFAULT);
                        try {
                            JSONObject json_params = new JSONObject();
                            json_params.put("extension", "image/png");
                            json_params.put("byteCode", imageString);
                            json_params.put("size", "" + imageString.length());
                            markArray.put(json_params);
                            Log.i("markArray: ", "" + markArray);

                        } catch (JSONException e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        }
                        buttonUpLoadImage.setText("Add Photo " + photosCount);
                    } catch (Exception ex) {
                        photosCount = 0;
                        ex.printStackTrace();
                    }
                }
                break;
            case Constants.OPEN_GALLERY:
                if (resultCode == Activity.RESULT_OK) {
                    try {
                        Uri selectedImage = data.getData();
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};
                        Cursor cursor = this.getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                        cursor.moveToFirst();
                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        String filePath = cursor.getString(columnIndex);
                        //String newPath=URLEncoder.encode(filePath,"UTF-8");
                        cursor.close();
                        Log.e("File", "filePath: " + filePath);

                        File file = new File(new URI("file://" + filePath.replaceAll(" ", "%20")));
                        int maxImageSize = BitmapUtils.getMaxSize(this);
                        Bitmap sourceBitmap = BitmapUtils.getScaledBitmap(file, maxImageSize);
                        ExifInterface exif = new ExifInterface(file.getPath());
                        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
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
                        ByteArrayOutputStream bao = new ByteArrayOutputStream();
                        originalImage.compress(Bitmap.CompressFormat.PNG, 100, bao);
                        byte[] ba = bao.toByteArray();
                        imageString = Base64.encodeToString(ba, Base64.DEFAULT);
                        try {
                            JSONObject json_params = new JSONObject();
                            json_params.put("extension", "image/png");
                            json_params.put("byteCode", imageString);
                            json_params.put("size", "" + imageString.length());
                            markArray.put(json_params);
                            Log.i("markArray: ", "" + markArray);

                        } catch (JSONException e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        }
                        buttonUpLoadImage.setText("Add Photo " + photosCount);
                    } catch (Exception e) {
                        photosCount = 0;
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    TextWatcher textWatcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
            if (currentCategoryModel == null || currentCategoryModel.getCategoryId() == 0) {
                showToast("Please select a review");
                return;
            }

            if (!ConnectivityUtils.isNetworkEnabled(WriteReviewActivity.this)) {
                ToastUtils.showToast(WriteReviewActivity.this, getString(R.string.error_network));
                return;

            }
            if (s.toString().equals("")) {
                innerLayout.setVisibility(View.VISIBLE);
                mSearchList.setVisibility(View.GONE);
            } else {
                if (mAutoSuggestController != null)
                    mAutoSuggestController.setCanceled(true);
                mAutoSuggestController.getData(AppConstants.WRITE_A_REVIEW_AUTO_SUGGEST_REQUEST, s.toString() + "," + categoryId);
            }

			/* hideSoftKeyboard();
               mwhatReviewEtxt.removeTextChangedListener(textWatcher);*/

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
            // TODO Auto-generated method stub

        }

        @Override
        public void afterTextChanged(Editable s) {


        }
    };

    @Override
    protected void updateUi(Response response) {
        removeProgressDialog();
        if (response == null) {

            showToast(getString(R.string.went_wrong));
            innerLayout.setVisibility(View.VISIBLE);
            mSearchList.setVisibility(View.GONE);
            return;
        }

        switch (response.getDataType()) {
            case AppConstants.WRITE_A_REVIEW_AUTO_SUGGEST_REQUEST:
                AutoSuggestReviewResponse responseData = (AutoSuggestReviewResponse) response.getResponseObject();
                if (responseData.getResponseCode() == 200) {
                    ArrayList<AutoSuggestModelData> queryList = responseData.getResult().getData().getSuggest();

                    if (!queryList.isEmpty()) {
                        innerLayout.setVisibility(View.GONE);
                        mSearchList.setVisibility(View.VISIBLE);
                        ArrayAdapter<AutoSuggestModelData> mQueryAdapter = new ArrayAdapter<AutoSuggestModelData>(this, R.layout.text_for_locality, queryList);

                        // set the height of Listview
                        //
                        int totalHeight = 0;
                        int adapterCount = mQueryAdapter.getCount();
                        for (int size = 0; size < adapterCount; size++) {
                            View listItem = mQueryAdapter.getView(size, null, mSearchList);
                            listItem.measure(0, 0);
                            totalHeight += listItem.getMeasuredHeight();
                        }

                        ViewGroup.LayoutParams params = mSearchList.getLayoutParams();
                        params.height = totalHeight + (mSearchList.getDividerHeight() * (adapterCount - 1));
                        mSearchList.setLayoutParams(params);
                        mSearchList.setAdapter(mQueryAdapter);
                        mQueryAdapter.notifyDataSetChanged();
                    } else {
                        innerLayout.setVisibility(View.VISIBLE);
                        mSearchList.setVisibility(View.GONE);
                    }

                } else if (responseData.getResponseCode() == 400) {
                    innerLayout.setVisibility(View.VISIBLE);
                    mSearchList.setVisibility(View.GONE);
                    showToast(getString(R.string.no_data));
                    //	showToast("Please give complete review");

                }
                break;

            case AppConstants.WRITE_A_REVIEW_REQUEST:
                CommonResponse reviewResponse = (CommonResponse) response.getResponseObject();
                String message = reviewResponse.getResult().getMessage();
                if (reviewResponse.getResponseCode() == 200) {
//                    if (StringUtils.isNullOrEmpty(message)) {
//                        Toast.makeText(WriteReviewActivity.this, getResources().getString(R.string.write_review_success_new), Toast.LENGTH_SHORT).show();
//                    } else {
//                        Toast.makeText(WriteReviewActivity.this, message, Toast.LENGTH_SHORT).show();
//                    }
//                    Toast.makeText(WriteReviewActivity.this, getResources().getString(R.string.write_review_success_new), Toast.LENGTH_LONG).show();
                    Toast.makeText(WriteReviewActivity.this, message, Toast.LENGTH_LONG).show();
                    final Handler handler = new Handler();
                    Runnable r = new Runnable() {
                        public void run() {
                            finish();
                            handler.postDelayed(this, 1000);
                        }
                    };
                    handler.postDelayed(r, 1000);

                } else if (reviewResponse.getResponseCode() == 400) {

                    if (StringUtils.isNullOrEmpty(message)) {
                        Toast.makeText(WriteReviewActivity.this, getResources().getString(R.string.no_data), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(WriteReviewActivity.this, message, Toast.LENGTH_LONG).show();
                    }
                }
                break;
            default:
                break;
        }
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

    public void hideSoftKeyboard() {
        if (getCurrentFocus() != null && getCurrentFocus() instanceof AutoCompleteTextView) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(), 0);
        }

    }

    private void doStuffForImage() {
        _controller = new WriteReviewController(WriteReviewActivity.this, WriteReviewActivity.this);
        WriteReviewModel wr_model = new WriteReviewModel();
        wr_model.setTitle(mTitleReviewEtxt.getText().toString());
        if (categoryId != 0) {
            wr_model.setReviewType("" + categoryId);
        } else if (currentCategoryModel != null) {
            wr_model.setReviewType("" + currentCategoryModel.getCategoryId());
        }


        //wr_model.setRating(ratingValue);
        wr_model.setDescription(mDescriptionReviewEtxt.getText().toString());
        if (markArray != null) {
            wr_model.setImage(markArray);
        }
//        UserTable userTable = new UserTable((BaseApplication) getApplication());
//        int count = userTable.getCount();
//        if (count <= 0) {
//            removeProgressDialog();
//            showToast(getResources().getString(R.string.user_login));
//            return;
//        }
//        UserModel userModel = userTable.getAllUserData();
        wr_model.setUserId("" + SharedPrefUtils.getUserDetailModel(this).getId());
//        wr_model.setSessionId(userModel.getUser().getSessionId());
        if (currentAutoSuggestModel != null && !StringUtils.isNullOrEmpty(currentAutoSuggestModel.getType())) {
            wr_model.setType(currentAutoSuggestModel.getType());
        } else if (businessOrEvent == Constants.BUSINESS_PAGE_TYPE) {
            wr_model.setType("business");
        } else if (businessOrEvent == Constants.EVENT_PAGE_TYPE) {
            wr_model.setType("event");
        }


        if (!StringUtils.isNullOrEmpty(businessOrEventId)) {
            wr_model.setBusinessId(businessOrEventId);
        } else if (currentAutoSuggestModel != null && !StringUtils.isNullOrEmpty(currentAutoSuggestModel.getType())) {
            wr_model.setBusinessId(currentAutoSuggestModel.getId());
        }

        if (ratingValue.equalsIgnoreCase("") || ratingValue.equalsIgnoreCase("0.0")) {

            ToastUtils.showToast(this, "Please give a star rating", Toast.LENGTH_SHORT);

        } else {
            wr_model.setRating(ratingValue);

            showProgressDialog(getString(R.string.please_wait));
            _controller.getData(AppConstants.WRITE_A_REVIEW_REQUEST, wr_model);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
    /*	case R.id.backbtn:
            finish();
			break;
		case R.id.save:
			Log.d("check","write review");
			UserTable userTable = new UserTable((BaseApplication)getApplication());
			int count=userTable.getCount();
			Log.d("check","write review count "+count);
			if(count<=0){
				removeProgressDialog();
				showToast(getResources().getString(R.string.user_login));
				return;
			}
			
			if(currentCategoryModel!=null && currentCategoryModel.getCategoryId()==0){
				showToast("What would you like to review?");
				return;
			}
			if(mwhatReviewEtxt.getVisibility()==View.VISIBLE){
				if(mwhatReviewEtxt.getText().toString().equals("") && currentAutoSuggestModel==null){
					showToast("Please enter the name of a business or event.");
					return;
				}
			}
			*//**
             * this edit text visibility will be gone when we come from business details page. so i am using this for validation
             *//*
            if(mwhatReviewEtxt.getVisibility()==View.GONE){
				if(mTitleReviewEtxt.getText().toString().equals("") && mRatingReviewEtxt.getRating()==0.0f && photosCount==0 ){
				//	if(mTitleReviewEtxt.getText().toString().equals("")){
						showToast("Please enter rating for the Review.");
						return;
				//	}
				}
				
			}
		//	if(mwhatReviewEtxt.getVisibility()==View.GONE){
                 if(photosCount==0)
                 {
				if(mRatingReviewEtxt.getRating()!=0.0f  ) {
					
					if(mTitleReviewEtxt.getText().toString().equals("") && !mDescriptionReviewEtxt.getText().toString().equals("") ) {
						showToast("Please enter title for the Review.");
						return;
					}*//*else{
                        doStuffForImage();
						
					}*//*

				} else {
					if(mTitleReviewEtxt.getText().toString().equals("")){
						showToast("Please enter a rating to submit your review.");
						return;
					}
					
				}
*//*				if((mRatingReviewEtxt.getRating()!=0.0f) && !(mTitleReviewEtxt.getText().toString().equals("")) ) {
                //	doStuffForImage();
				} else {
					showToast("Please enter title for the Review.");
					return;
				}*//*
                if(mRatingReviewEtxt.getRating()!=0.0f || !mTitleReviewEtxt.getText().toString().equals("")||!mDescriptionReviewEtxt.getText().toString().equals("") ) {
				//	doStuffForImage();
				} else {
					showToast("Please give description for the Review.");
					return;
				}
				doStuffForImage();
				
                 }else{
                	 doStuffForImage();
                	 return;
                 }
				*//*if(mRatingReviewEtxt.getRating()!=0.0f || !mTitleReviewEtxt.getText().toString().equals("")||!mDescriptionReviewEtxt.getText().toString().equals("")||photosCount!=0  ) {
                    doStuffForImage();
				} else {
					showToast("Please upload atleast one photo");
					return;
				}*//*
            *//*}else{
                if(!mDescriptionReviewEtxt.getText().toString().equals("")||photosCount!=0 || mRatingReviewEtxt.getRating()!=0.0f || !mTitleReviewEtxt.getText().toString().equals("") ) {
					doStuffForImage();
				} else {
					showToast("Please enter the description");
					return;
				}
			}*//*
            break;*/
            case R.id.add_photo:
                CameraFragmentDialog fragmentDialog = new CameraFragmentDialog();
                fragmentDialog.setSubmitListner(WriteReviewActivity.this);
                fragmentDialog.show(getSupportFragmentManager(), "");
                break;
            default:
                break;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // according to fragment change it
        getMenuInflater().inflate(R.menu.forgot_password, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            case R.id.save:

                if (mTitleReviewEtxt.getText().toString().equals("")) {
                    ToastUtils.showToast(WriteReviewActivity.this, "Title can't be blank");
                    return true;
                }
                if (mDescriptionReviewEtxt.getText().toString().equals("")) {
                    ToastUtils.showToast(WriteReviewActivity.this, "Description can't be blank");
                    return true;
                }

//                UserTable userTable = new UserTable((BaseApplication) getApplication());
//                int count = userTable.getCount();
//                if (count <= 0) {
//                    removeProgressDialog();
//                    showToast(getResources().getString(R.string.user_login));
//                    return true;
//                }

                if (currentCategoryModel != null && currentCategoryModel.getCategoryId() == 0) {
                    showToast("What would you like to review?");
                    return true;
                }
                if (mwhatReviewEtxt.getVisibility() == View.VISIBLE) {
                    if (mwhatReviewEtxt.getText().toString().equals("") && currentAutoSuggestModel == null) {
                        showToast("Please enter the name of a business or event.");
                        return true;
                    }
                }
                /**
                 * this edit text visibility will be gone when we come from business details page. so i am using this for validation
                 */
                if (mwhatReviewEtxt.getVisibility() == View.GONE) {
                    if (mTitleReviewEtxt.getText().toString().equals("") && mRatingReviewEtxt.getRating() == 0.0f && photosCount == 0) {
                        //	if(mTitleReviewEtxt.getText().toString().equals("")){
                        showToast("Please enter rating for the Review.");
                        return true;
                        //	}
                    }

                }
                //	if(mwhatReviewEtxt.getVisibility()==View.GONE){
                if (photosCount == 0) {
                    if (mRatingReviewEtxt.getRating() != 0.0f) {

                        if (mTitleReviewEtxt.getText().toString().equals("") && !mDescriptionReviewEtxt.getText().toString().equals("")) {
                            showToast("Please enter title for the Review.");
                            return true;
                        }/*else{
                        doStuffForImage();

					}*/

                    } else {
                        if (mTitleReviewEtxt.getText().toString().equals("")) {
                            showToast("Please enter a rating to submit your review.");
                            return true;
                        }

                    }
/*				if((mRatingReviewEtxt.getRating()!=0.0f) && !(mTitleReviewEtxt.getText().toString().equals("")) ) {
                //	doStuffForImage();
				} else {
					showToast("Please enter title for the Review.");
					return;
				}*/
                    if (mRatingReviewEtxt.getRating() != 0.0f || !mTitleReviewEtxt.getText().toString().equals("") || !mDescriptionReviewEtxt.getText().toString().equals("")) {
                        //	doStuffForImage();
                    } else {
                        showToast("Please give description for the Review.");
                        return true;
                    }
                    doStuffForImage();

                } else {
                    doStuffForImage();
                    return true;
                }
                /*if(mRatingReviewEtxt.getRating()!=0.0f || !mTitleReviewEtxt.getText().toString().equals("")||!mDescriptionReviewEtxt.getText().toString().equals("")||photosCount!=0  ) {
                    doStuffForImage();
				} else {
					showToast("Please upload atleast one photo");
					return;
				}*/
            /*}else{
                if(!mDescriptionReviewEtxt.getText().toString().equals("")||photosCount!=0 || mRatingReviewEtxt.getRating()!=0.0f || !mTitleReviewEtxt.getText().toString().equals("") ) {
					doStuffForImage();
				} else {
					showToast("Please enter the description");
					return;
				}
			}*/

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
