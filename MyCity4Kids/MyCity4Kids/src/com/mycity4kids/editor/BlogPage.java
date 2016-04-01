package com.mycity4kids.editor;

import android.app.Activity;
import android.support.v7.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.kelltontech.utils.BitmapUtils;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.controller.ArticleDraftController;
import com.mycity4kids.controller.BlogSetupController;
import com.mycity4kids.dbtable.UserTable;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.editor.ArticleDraftRequest;
import com.mycity4kids.models.forgot.CommonResponse;
import com.mycity4kids.models.user.UserModel;
import com.mycity4kids.preference.SharedPrefUtils;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.wordpress.android.editor.EditorMediaUploadListener;
import org.wordpress.android.util.helpers.MediaFile;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by anshul on 3/18/16.
 */
public class BlogPage extends BaseActivity {
    Toolbar mToolbar;
    ImageView blogImage;
    public static final int ADD_MEDIA_ACTIVITY_REQUEST_CODE = 1111;
    Uri imageUri;
    String imageString;
    File file;
    String response;
    Bitmap finalBitmap;
    String url="blogger_list_default.jpg";
    EditText blogTitle,bloggerBio;
    Button createBlog;
    private UserModel userModel;
    @Override
    protected void updateUi(Response response) {
        switch (response.getDataType()) {

        case AppConstants.BLOG_SETUP_REQUEST: {
            if (response.getResponseObject() instanceof CommonResponse) {
                CommonResponse responseModel = (CommonResponse) response
                        .getResponseObject();
                removeProgressDialog();
                if (responseModel.getResponseCode() != 200) {
                    showToast(getString(R.string.toast_response_error));
                    return;
                } else {
                    if (!StringUtils.isNullOrEmpty(responseModel.getResult().getMessage())) {
                     //   SharedPrefUtils.setProfileImgUrl(BlogPage.this, responseModel.getResult().getMessage());
                        Log.i("Blog Setup Response", responseModel.getResult().getMessage());
                        if (responseModel.getResponse().equals("failure"))
                        {
                           // showToast(responseModel.getResult().getMessage());
                            alertDialog(responseModel.getResult().getMessage());
                        }
                        else
                        {
                            showToast(responseModel.getResult().getMessage());
                            finish();
                        }
                    }

                    //setProfileImage(originalImage);
                 //   showToast("You have successfully uploaded image.");
                }
              //  removeProgressDialog();
            }
            break;
    } }}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.blog_page);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        bloggerBio=(EditText) findViewById(R.id.bloggerBio);
        blogTitle=(EditText) findViewById(R.id.blogTitle);
        createBlog=(Button) findViewById(R.id.createBlog);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Setup your Blog");
        blogImage=(ImageView) findViewById(R.id.blogImage);
        Utils.pushOpenScreenEvent(BlogPage.this, "Article Image Upload", SharedPrefUtils.getUserDetailModel(this).getId() + "");
        UserTable userTable = new UserTable((BaseApplication) this.getApplication());
        userModel = userTable.getAllUserData();
        blogImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                        intent.setType("image/*");
                        startActivityForResult(intent, ADD_MEDIA_ACTIVITY_REQUEST_CODE);
            }
        });
        createBlog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (blogTitle.getText().toString().isEmpty())
                {
                   // showToast("Please fill the required fields");
                    blogTitle.setFocusableInTouchMode(true);
                    blogTitle.setError("Please enter Blog Title");
                    blogTitle.requestFocus();
                }
                else if (bloggerBio.getText().toString().isEmpty())
                {
                    bloggerBio.setFocusableInTouchMode(true);
                    bloggerBio.setError("Please enter your Bio");
                    bloggerBio.requestFocus();
                }

                else {
                showProgressDialog(getResources().getString(R.string.please_wait));
                ArticleDraftRequest articleDraftRequest = new ArticleDraftRequest();
                /**
                 * this case will case in pagination case: for sorting
                 */
                articleDraftRequest.setUser_id("" + userModel.getUser().getId());

                articleDraftRequest.setImageName(url);
                articleDraftRequest.setTitle(blogTitle.getText().toString());
                articleDraftRequest.setBody(bloggerBio.getText().toString());
/*
        articleDraftRequest.setCity_id(SharedPrefUtils.getCurrentCityModel(getActivity()).getId());
        _parentingModel.setPage("" + pPageCount);*/
                BlogSetupController _controller = new BlogSetupController(BlogPage.this, BlogPage.this);

                _controller.getData(AppConstants.BLOG_SETUP_REQUEST, articleDraftRequest);
            }}
        });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data == null) {
            return;
        }

        //   mediaFile.setVideo(imageUri.toString().contains("video"));

        switch (requestCode) {
            case ADD_MEDIA_ACTIVITY_REQUEST_CODE:
                imageUri = data.getData();

                if (resultCode == Activity.RESULT_OK) {
                    try {
                        Uri selectedImage = data.getData();
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};

                        Cursor cursor = this.getContentResolver().query(
                                selectedImage, filePathColumn, null, null, null);
                        cursor.moveToFirst();

                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        String filePath = cursor.getString(columnIndex);
                        cursor.close();
                        Log.e("File", "filePath: " + filePath);

                        file = new File(new URI("file://"
                                + filePath.replaceAll(" ", "%20")));
                        int maxImageSize = BitmapUtils.getMaxSize(this);
                        maxImageSize = 512;
                        Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(BlogPage.this.getContentResolver(), imageUri);
                        float actualHeight = imageBitmap.getHeight();
                        float actualWidth = imageBitmap.getWidth();
                        float maxHeight = 243;
                        float maxWidth = 423;
                        float imgRatio = actualWidth / actualHeight;
                        float maxRatio = maxWidth / maxHeight;


                        if (actualHeight > maxHeight || actualWidth > maxWidth) {
                            if (imgRatio < maxRatio) {
                                //adjust width according to maxHeight
                                imgRatio = maxHeight / actualHeight;
                                actualWidth = imgRatio * actualWidth;
                                actualHeight = maxHeight;
                            } else if (imgRatio > maxRatio) {
                                //adjust height according to maxWidth
                                imgRatio = maxWidth / actualWidth;
                                actualHeight = imgRatio * actualHeight;
                                actualWidth = maxWidth;
                            } else {
                                actualHeight = maxHeight;
                                actualWidth = maxWidth;
                            }
                        }
                        finalBitmap = Bitmap.createScaledBitmap(imageBitmap, (int) actualWidth, (int) actualHeight, true);
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        finalBitmap.compress(Bitmap.CompressFormat.PNG, 75, stream);
                        byte[] byteArrayFromGallery = stream.toByteArray();

                        imageString = Base64.encodeToString(byteArrayFromGallery, Base64.DEFAULT);
                        new FileUploadTask().execute();
                        // compressImage(filePath);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }
                break;}}
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // according to fragment change it

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
        }
        return true;
    }

    private class FileUploadTask extends AsyncTask<Object, Integer, String> {

        private ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
             dialog = new ProgressDialog(BlogPage.this);
            dialog.setMessage("Uploading...");
            dialog.setIndeterminate(false);
            dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            dialog.setProgress(0);
            dialog.setCancelable(false);
            dialog.show();

        }

        @Override
        protected String doInBackground(Object... arg0) {
            try {

                FileInputStream fileInputStream = new FileInputStream(file);
                byte[] bytes = new byte[(int) file.length()];
                fileInputStream.read(bytes);
                fileInputStream.close();

                URL url = new URL(AppConstants.IMAGE_EDITOR_UPLOAD_URL);
                HttpURLConnection connection =
                        (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setRequestProperty("Connection", "Keep-Alive");
                connection.setRequestProperty("Cache-Control", "no-cache");
                connection.setChunkedStreamingMode(32768);
               /* connection.setRequestProperty(
                        "Content-Type", "multipart/form-data;boundary=" + this.boundary);*/
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("imageType", "jpg"));
                params.add(new BasicNameValuePair("file", imageString));
                // params.add(new BasicNameValuePair("file", "jhsaiksa"));



//                HttpURLConnection.setFixedLengthStreamingMode(connection);
                OutputStream outputStream = connection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(outputStream, "UTF-8"));
                Log.e("param",getQuery(params));
                writer.write(getQuery(params));


                int bufferLength = 1024;
                for (int i = 0; i < bytes.length; i += bufferLength) {
                    int progress = (int) ((i / (float) bytes.length)*100);
                    publishProgress(progress);
                    if (bytes.length - i >= bufferLength) {
                        outputStream.write(bytes, i, bufferLength);
                    } else {
                        outputStream.write(bytes, i, bytes.length - i);
                    }
                }


                writer.flush();
                writer.close();
                outputStream.close();
                outputStream.flush();
                publishProgress(97);
                connection.getResponseCode();
                InputStream inputStream = connection.getInputStream();
                Reader in = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));

                StringBuilder sb = new StringBuilder();
                for (int c; (c = in.read()) >= 0; )
                    sb.append((char) c);
                response = sb.toString();
                Log.e("response", response);

                // read the response
                inputStream.close();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return response;
        }

        private String getQuery(List<NameValuePair> params) throws UnsupportedEncodingException {
            StringBuilder result = new StringBuilder();
            boolean first = true;

            for (NameValuePair pair : params) {
                if (first)
                    first = false;
                else
                    result.append("&");

                result.append(URLEncoder.encode(pair.getName(), "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
            }

            return result.toString();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
              dialog.setProgress(progress[0]);
            Log.e("progress", progress[0] + "");
          //  ((EditorMediaUploadListener) mEditorFragment).onMediaUploadProgress(mediaId, progress[0]/2);
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                if (response != null) {

                    CommonResponse responseModel = new Gson().fromJson(response, CommonResponse.class);
                    if (responseModel.getResponseCode() != 200) {
                        Log.e("responseCode",""+responseModel.getResponseCode());
                        showToast(getString(R.string.toast_response_error));

                    } else {
                        blogImage.setImageBitmap(finalBitmap);
                        if (!StringUtils.isNullOrEmpty(responseModel.getResult().getMessage())) {
                            //   SharedPrefUtils.setProfileImgUrl(EditorPostActivity.this, responseModel.getResult().getMessage());
                            Log.i("Uploaded Image URL", responseModel.getResult().getMessage());
                        }
                        url=responseModel.getResult().getMessage();
                        String[] seperated=url.split("/");
                        if (seperated.length!=0)
                        {  url=seperated[seperated.length-1];
                        Log.e("url",url);
                        }

                     //   ((EditorMediaUploadListener) mEditorFragment).onMediaUploadSucceeded(mediaId, mediaFile);


                        //setProfileImage(originalImage);
//                        showToast("You have successfully uploaded image.");
                    }
                } else {
                    showToast("Error uploading image, please try again");
                    Log.e("Response", "null");
                }
                dialog.dismiss();
            } catch (Exception e) {
            }

        }

    }
    private void alertDialog(String msg)
    {
        new AlertDialog.Builder(this)
                .setTitle("MyCity4Kids")
                .setMessage(msg)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                        dialog.dismiss();
                    }
                })
                .show();

    }
 }
