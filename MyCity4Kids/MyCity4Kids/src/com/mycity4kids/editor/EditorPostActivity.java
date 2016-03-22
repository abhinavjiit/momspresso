package com.mycity4kids.editor;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.google.gson.Gson;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.kelltontech.utils.BitmapUtils;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.controller.ArticleDraftController;
import com.mycity4kids.controller.ImageUploadController;
import com.mycity4kids.dbtable.UserTable;
import com.mycity4kids.models.editor.ArticleDraftList;
import com.mycity4kids.models.editor.ArticleDraftRequest;
import com.mycity4kids.models.forgot.CommonResponse;
import com.mycity4kids.models.parentingdetails.ParentingDetailResponse;
import com.mycity4kids.models.user.ImageUploadRequest;
import com.mycity4kids.models.user.UserModel;
import com.mycity4kids.preference.SharedPrefUtils;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.wordpress.android.editor.EditorFragmentAbstract;
import org.wordpress.android.editor.EditorMediaUploadListener;
import org.wordpress.android.editor.ImageSettingsDialogFragment;
import org.wordpress.android.util.AppLog;
import org.wordpress.android.util.ToastUtils;
import org.wordpress.android.util.helpers.MediaFile;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by anshul on 2/29/16.
 */
public class EditorPostActivity extends BaseActivity implements EditorFragmentAbstract.EditorFragmentListener {
    String picturePath;
    private Bitmap bitmap;
    byte[] byteArrayToSend;
    private UserModel userModel;
    Uri imageUri;
    private Bitmap originalImage;
    File file;
    String imageString;
    MediaFile mediaFile;
    String mediaId;
    String response;
    Boolean fromBackpress = false;
    String draftId="";
    public static final String EDITOR_PARAM = "EDITOR_PARAM";
    public static final String TITLE_PARAM = "TITLE_PARAM";
    public static final String CONTENT_PARAM = "CONTENT_PARAM";
    public static final String DRAFT_PARAM = "DRAFT_PARAM";
    public static final String TITLE_PLACEHOLDER_PARAM = "TITLE_PLACEHOLDER_PARAM";
    public static final String CONTENT_PLACEHOLDER_PARAM = "CONTENT_PLACEHOLDER_PARAM";
    public static final int USE_NEW_EDITOR = 1;
    public static final int USE_LEGACY_EDITOR = 2;

    public static final int ADD_MEDIA_ACTIVITY_REQUEST_CODE = 1111;
    public static final int ADD_MEDIA_FAIL_ACTIVITY_REQUEST_CODE = 1112;
    public static final int ADD_MEDIA_CAMERA_ACTIVITY_REQUEST_CODE = 1113;
    public static final String MEDIA_REMOTE_ID_SAMPLE = "123";

    private static final int SELECT_IMAGE_MENU_POSITION = 0;
    private static final int SELECT_IMAGE_FAIL_MENU_POSITION = 1;
    private static final int SELECT_VIDEO_MENU_POSITION = 2;
    private static final int SELECT_VIDEO_FAIL_MENU_POSITION = 3;
    private static final int SELECT_IMAGE_CAMERA_MENU_POSITION = 4;

    private EditorFragmentAbstract mEditorFragment;

    private Map<String, String> mFailedUploads;


    @Override
    protected void updateUi(Response response) {
        switch (response.getDataType()) {

            case AppConstants.ARTICLE_DRAFT_REQUEST: {
                if (response.getResponseObject() instanceof ParentingDetailResponse) {
                    ParentingDetailResponse responseModel = (ParentingDetailResponse) response
                            .getResponseObject();
                    if (responseModel.getResponseCode() != 200) {
                        showToast(getString(R.string.toast_response_error));
                        return;
                    } else {
                        if (!StringUtils.isNullOrEmpty(responseModel.getResult().getMessage())) {
                            //  SharedPrefUtils.setProfileImgUrl(EditorPostActivity.this, responseModel.getResult().getMessage());
                            Log.i("Draft message", responseModel.getResult().getMessage());
                        }
                        removeProgressDialog();
                        draftId=responseModel.getResult().getData().getId()+"";

                        //setProfileImage(originalImage);
                        showToast("Draft Successfully saved");
                        if (fromBackpress) {
                            super.onBackPressed();
                        }
                        //  finish();
                    }
                }
                break;
            }
            case AppConstants.IMAGE_EDITOR_UPLOAD_REQUEST: {
                if (response.getResponseObject() instanceof CommonResponse) {
                    CommonResponse responseModel = (CommonResponse) response
                            .getResponseObject();
                    if (responseModel.getResponseCode() != 200) {
                        showToast(getString(R.string.toast_response_error));
                        return;
                    } else {
                        if (!StringUtils.isNullOrEmpty(responseModel.getResult().getMessage())) {
                            SharedPrefUtils.setProfileImgUrl(EditorPostActivity.this, responseModel.getResult().getMessage());
                            Log.i("Uploaded Image URL", responseModel.getResult().getMessage());
                        }
                        mediaFile.setFileURL(responseModel.getResult().getMessage());

                        ((EditorMediaUploadListener) mEditorFragment).onMediaUploadSucceeded(mediaId, mediaFile);
                        //setProfileImage(originalImage);
                        showToast("You have successfully uploaded image.");
                    }
                }
                break;
            }
        }

    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent().getIntExtra(EDITOR_PARAM, USE_NEW_EDITOR) == USE_NEW_EDITOR) {
            ToastUtils.showToast(this, R.string.starting_new_editor);
            setContentView(R.layout.activity_new_editor);
            UserTable userTable = new UserTable((BaseApplication) this.getApplication());
            userModel = userTable.getAllUserData();
           /* if (getIntent().getStringExtra("from").equals("draftList"))
            {  ArticleDraftList draftObject=(ArticleDraftList) getIntent().getSerializableExtra("draftItem");
            mEditorFragment.setTitle(draftObject.getTitle());
            mEditorFragment.setContent(draftObject.getBody());}*/
        } /*else {
            ToastUtils.showToast(this, R.string.starting_legacy_editor);
            setContentView(R.layout.activity_legacy_editor);
        }*/

        mFailedUploads = new HashMap<>();
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
        if (fragment instanceof EditorFragmentAbstract) {
            mEditorFragment = (EditorFragmentAbstract) fragment;
        }
    }

    @Override
    public void onBackPressed() {
        Log.e("title", mEditorFragment.getTitle().toString());
        Fragment fragment = getFragmentManager()
                .findFragmentByTag(ImageSettingsDialogFragment.IMAGE_SETTINGS_DIALOG_TAG);
        if (fragment != null && fragment.isVisible()) {
            ((ImageSettingsDialogFragment) fragment).dismissFragment();
        } else {
            //Toast.makeText(this,"Draft Saved",Toast.LENGTH_LONG).show();
            if (mEditorFragment.getTitle().toString().isEmpty()&&(mEditorFragment.getContent().toString().isEmpty()))
            {super.onBackPressed();

            }
            else {
                saveDraftRequest(mEditorFragment.getTitle().toString(), mEditorFragment.getContent().toString(), draftId);
                fromBackpress = true;
            }
            // super.onBackPressed();

        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.add(0, SELECT_IMAGE_MENU_POSITION, 0, getString(R.string.select_image));
      /*  menu.add(0, SELECT_IMAGE_FAIL_MENU_POSITION, 0, getString(R.string.select_image_fail));
        menu.add(0, SELECT_VIDEO_MENU_POSITION, 0, getString(R.string.select_video));
        menu.add(0, SELECT_VIDEO_FAIL_MENU_POSITION, 0, getString(R.string.select_video_fail));*/
        menu.add(0, SELECT_IMAGE_CAMERA_MENU_POSITION, 0, getString(R.string.camera_pick));
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        Intent intent = new Intent(Intent.ACTION_PICK);

        switch (item.getItemId()) {
            case SELECT_IMAGE_MENU_POSITION:
                intent.setType("image/*");
              /*  intent.setAction(Intent.ACTION_GET_CONTENT);
                intent = Intent.createChooser(intent, getString(R.string.select_image));*/

                startActivityForResult(intent, ADD_MEDIA_ACTIVITY_REQUEST_CODE);

                return true;
            case SELECT_IMAGE_FAIL_MENU_POSITION:
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent = Intent.createChooser(intent, getString(R.string.select_image_fail));

                startActivityForResult(intent, ADD_MEDIA_FAIL_ACTIVITY_REQUEST_CODE);
                return true;
            case SELECT_VIDEO_MENU_POSITION:
                intent.setType("video/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent = Intent.createChooser(intent, getString(R.string.select_video));

                startActivityForResult(intent, ADD_MEDIA_ACTIVITY_REQUEST_CODE);
                return true;
            case SELECT_VIDEO_FAIL_MENU_POSITION:
                intent.setType("video/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent = Intent.createChooser(intent, getString(R.string.select_video_fail));

                startActivityForResult(intent, ADD_MEDIA_FAIL_ACTIVITY_REQUEST_CODE);
                return true;
            case SELECT_IMAGE_CAMERA_MENU_POSITION:
                Intent intent1 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                String filename = "tmp_avatar_" + String.valueOf(System.currentTimeMillis()) + ".jpg";
                file = new File(Environment.getExternalStorageDirectory(),
                        filename);
                imageUri = Uri.fromFile(file);


                intent1.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, imageUri);
                intent1.putExtra("return-data", true);

                startActivityForResult(intent1, ADD_MEDIA_CAMERA_ACTIVITY_REQUEST_CODE);
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data == null) {
            return;
        }


        mediaFile = new MediaFile();
        mediaId = String.valueOf(System.currentTimeMillis());
        mediaFile.setMediaId(mediaId);
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
                        Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(EditorPostActivity.this.getContentResolver(), imageUri);
                        float actualHeight = imageBitmap.getHeight();
                        float actualWidth = imageBitmap.getWidth();
                        float maxHeight = 1300;
                        float maxWidth = 700;
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
                        Bitmap finalBitmap = Bitmap.createScaledBitmap(imageBitmap, (int) actualWidth, (int) actualHeight, true);
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        finalBitmap.compress(Bitmap.CompressFormat.PNG, 75, stream);
                        byte[] byteArrayFromGallery = stream.toByteArray();
                        byteArrayToSend = byteArrayFromGallery;
                        imageString = Base64.encodeToString(byteArrayToSend, Base64.DEFAULT);
                        mEditorFragment.imageUploading=0;
                        new FileUploadTask().execute();
                        // compressImage(filePath);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                    mEditorFragment.appendMediaFile(mediaFile, imageUri.toString(), null);

                    if (mEditorFragment instanceof EditorMediaUploadListener) {
                        //  simulateFileUpload(mediaId, imageUri.toString());
                    }
                }
                break;
            case ADD_MEDIA_CAMERA_ACTIVITY_REQUEST_CODE:

                if (resultCode == Activity.RESULT_OK) {
                    try {

                        Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(EditorPostActivity.this.getContentResolver(), imageUri);
                        float actualHeight = imageBitmap.getHeight();
                        float actualWidth = imageBitmap.getWidth();
                        float maxHeight = 1300;
                        float maxWidth = 700;
                        float imgRatio = actualWidth / actualHeight;
                        float maxRatio = maxWidth / maxHeight;
                        // float compressionQuality = 0.5;//50 percent compression

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
                        Bitmap finalBitmap = Bitmap.createScaledBitmap(imageBitmap, (int) actualWidth, (int) actualHeight, true);

                        ByteArrayOutputStream stream = new ByteArrayOutputStream();


                        finalBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                        byte[] byteArrayFromGallery = stream.toByteArray();
                        byteArrayToSend = byteArrayFromGallery;
                        imageString = Base64.encodeToString(byteArrayToSend, Base64.DEFAULT);
                        // imageString = Base64.encodeToString(array, Base64.DEFAULT);
                        mEditorFragment.imageUploading=0;
                        new FileUploadTask().execute();
                        // compressImage(filePath);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //  decodeFile(picturePath);

                    mEditorFragment.appendMediaFile(mediaFile, imageUri.toString(), null);

                    if (mEditorFragment instanceof EditorMediaUploadListener) {
                        //  simulateFileUpload(mediaId, imageUri.toString());
                    }
                }
                break;

        }
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.draft) {
            Log.e("draftId",draftId+"");
            saveDraftRequest(mEditorFragment.getTitle().toString(), mEditorFragment.getContent().toString(),draftId);
            fromBackpress = false;
        }
        else if (item.getItemId() == R.id.publish)
        {
            if (mEditorFragment.getTitle().toString().isEmpty())
            {
                showToast("Title can't be empty");
            }
            else if (mEditorFragment.getContent().toString().isEmpty())
            {
                showToast("Body can't be empty");
            }
            else if (mEditorFragment.imageUploading==0)
            { Log.e("imageuploading",mEditorFragment.imageUploading+"");
                showToast("Please wait while image is being uploaded");
            }
            else
            {

            ArticleDraftList draftObject=new ArticleDraftList();

            draftObject.setBody(contentFormatting(mEditorFragment.getContent().toString()));
            draftObject.setTitle(mEditorFragment.getTitle().toString());
            Log.e("publish", "clicked");
            Intent intent=new Intent(EditorPostActivity.this,ArticleImageTagUpload.class);
            intent.putExtra("draftItem",  draftObject);
            startActivity(intent);
        }}
        return super.onOptionsItemSelected(item);

    }



    public void saveDraftRequest(String title, String body, String draftId) {
        showProgressDialog(getResources().getString(R.string.please_wait));
        ArticleDraftRequest requestData = new ArticleDraftRequest();

        requestData.setUser_id("" + userModel.getUser().getId());
        requestData.setBody("" + body);
        requestData.setTitle("" + title);
        requestData.setId("" + draftId);

        Log.e("userId", userModel.getUser().getId() + "");
        ArticleDraftController controller = new ArticleDraftController(this, this);
        controller.getData(AppConstants.ARTICLE_DRAFT_REQUEST, requestData);
    }

    @Override
    public void onSettingsClicked() {
        // TODO
    }

    @Override
    public void onAddMediaClicked() {
        // TODO
    }

    @Override
    public void onMediaRetryClicked(String mediaId) {
        if (mFailedUploads.containsKey(mediaId)) {
           // simulateFileUpload(mediaId, mFailedUploads.get(mediaId));
        }
    }

    @Override
    public void onMediaUploadCancelClicked(String mediaId, boolean delete) {

    }

    @Override
    public void onFeaturedImageChanged(int mediaId) {

    }

    @Override
    public void onVideoPressInfoRequested(String videoId) {

    }

    @Override
    public String onAuthHeaderRequested(String url) {
        return "";
    }

    @Override
    public void onEditorFragmentInitialized() {
        // arbitrary setup
        mEditorFragment.setFeaturedImageSupported(true);
        mEditorFragment.setBlogSettingMaxImageWidth("600");
        mEditorFragment.setDebugModeEnabled(true);

        // get title and content and draft switch
        String title = getIntent().getStringExtra(TITLE_PARAM);
        String content = getIntent().getStringExtra(CONTENT_PARAM);
        boolean isLocalDraft = getIntent().getBooleanExtra(DRAFT_PARAM, true);
        if (getIntent().getStringExtra("from")!=null&&getIntent().getStringExtra("from").equals("draftList"))
        {
            ArticleDraftList draftObject=(ArticleDraftList) getIntent().getSerializableExtra("draftItem");
            title=draftObject.getTitle();
            content=draftObject.getBody();
            draftId=draftObject.getId();
            mEditorFragment.setTitle(title);
            mEditorFragment.setContent(content);
        }
        else /*if (getIntent().getStringExtra("from").equals("dashboard"))*/
        {
            mEditorFragment.setTitle(title);
            mEditorFragment.setContent(content);
            Log.e("postContent", content);
            mEditorFragment.setTitlePlaceholder(getIntent().getStringExtra(TITLE_PLACEHOLDER_PARAM));
            mEditorFragment.setContentPlaceholder(getIntent().getStringExtra(CONTENT_PLACEHOLDER_PARAM));
            mEditorFragment.setLocalDraft(isLocalDraft);
        }
      /*  mEditorFragment.setTitle(title);
        mEditorFragment.setContent(content);
        Log.e("postContent", content);
        mEditorFragment.setTitlePlaceholder(getIntent().getStringExtra(TITLE_PLACEHOLDER_PARAM));
        mEditorFragment.setContentPlaceholder(getIntent().getStringExtra(CONTENT_PLACEHOLDER_PARAM));
        mEditorFragment.setLocalDraft(isLocalDraft);*/
    }

    @Override
    public void saveMediaFile(MediaFile mediaFile) {
        // TODO
    }

    @Override
    public void onTrackableEvent(EditorFragmentAbstract.TrackableEvent event) {
        AppLog.d(AppLog.T.EDITOR, "Trackable event: " + event);
    }



    private class FileUploadTask extends AsyncTask<Object, Integer, String> {

        private ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(EditorPostActivity.this);
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

                URL url = new URL("http://54.169.17.138/apiblogs/uploadImage");
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
                  //  int progress =  ((i /  bytes.length)*100);
                    int progress =   (int) ((i / (float) bytes.length)*100);
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

                connection.getResponseCode();
                InputStream inputStream = connection.getInputStream();
                Reader in = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                publishProgress(97);
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
        //    ((EditorMediaUploadListener) mEditorFragment).onMediaUploadProgress(mediaId, (float)(progress[0]*0.8));
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
                        if (!StringUtils.isNullOrEmpty(responseModel.getResult().getMessage())) {
                            //   SharedPrefUtils.setProfileImgUrl(EditorPostActivity.this, responseModel.getResult().getMessage());
                            Log.i("Uploaded Image URL", responseModel.getResult().getMessage());
                        }
                        mediaFile.setFileURL(responseModel.getResult().getMessage());

                        ((EditorMediaUploadListener) mEditorFragment).onMediaUploadSucceeded(mediaId, mediaFile);
                        Log.i("PPPPPPPPPPPPPPPPP", responseModel.getResult().getMessage());

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


    private void simulateFileUpload(final String mediaId, final String mediaUrl) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    float count = (float) 0.5;
                    while (count < 1.1) {
                        sleep(500);

                        ((EditorMediaUploadListener) mEditorFragment).onMediaUploadProgress(mediaId, count);

                        count += 0.1;
                    }



                    if (mFailedUploads.containsKey(mediaId)) {
                        mFailedUploads.remove(mediaId);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        thread.start();
    }
    public String contentFormatting( String content)
    {

        String pTag="<p>";
        String newString=pTag.concat(content);
        String formattedString=newString.replace("\n\n", "</p><p>");
        formattedString.concat("</p>");
        return formattedString;

    }

}
