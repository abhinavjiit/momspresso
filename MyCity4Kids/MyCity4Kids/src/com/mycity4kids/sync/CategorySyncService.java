package com.mycity4kids.sync;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.kelltontech.utils.ConnectivityUtils;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.models.response.ConfigResponse;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.ConfigAPIs;
import com.mycity4kids.retrofitAPIsInterfaces.TopicsCategoryAPI;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by anshul on 7/12/16.
 */
public class CategorySyncService extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public int version;
    public String location;
    public CategorySyncService(String name) {
        super(name);
    }
public CategorySyncService()
    {
        super("CategorySyncService");
    }
    @Override
    protected void onHandleIntent(Intent intent) {

        final Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        // prepare call in Retrofit 2.0
        ConfigAPIs configAPIs = retrofit.create(ConfigAPIs.class);
        if (!ConnectivityUtils.isNetworkEnabled(this)) {
            //showToast(getString(R.string.error_network));
            return;
        }

            Call<ConfigResponse> call = configAPIs.getConfig();


            //asynchronous call
            call.enqueue(new Callback<ConfigResponse>() {
                             @Override
                             public void onResponse(Call<ConfigResponse> call, retrofit2.Response<ConfigResponse> response) {
                                 int statusCode = response.code();

                                 ConfigResponse responseModel = (ConfigResponse) response.body();
                                 // Result<ArticleDraftResult> result=responseModel.getData().getResult();
                             //    removeProgressDialog();
try {


                                 if (responseModel.getCode() != 200) {
                                   //  showToast(getString(R.string.toast_response_error));
                                     return;
                                 } else {
                                     if (!StringUtils.isNullOrEmpty(responseModel.getData().getMsg())) {
                                         //  SharedPrefUtils.setProfileImgUrl(EditorPostActivity.this, responseModel.getResult().getMessage());
                                     //    Log.i("Draft message", responseModel.getData().getMsg());
                                       version=  SharedPrefUtils.getConfigCategoryVersion(CategorySyncService.this);
                                         if (version==0||version!=responseModel.getData().getResult().getCategory().getVersion())
                                         {
                                             location=responseModel.getData().getResult().getCategory().getLocation();
                                             TopicsCategoryAPI categoryAPI=retrofit.create(TopicsCategoryAPI.class);
                                             if (!ConnectivityUtils.isNetworkEnabled(CategorySyncService.this)) {
                                                 //showToast(getString(R.string.error_network));
                                                 return;
                                             }

                                             Call<ResponseBody> caller = categoryAPI.downloadFileWithDynamicUrlSync(location);

                                             caller.enqueue(new Callback<ResponseBody>() {
                                                 @Override
                                                 public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                                                     Log.d("TAGA", "server contacted and has file");

                                                     boolean writtenToDisk = writeResponseBodyToDisk(response.body());

                                                     Log.d("TAGA", "file download was a success? " + writtenToDisk);

                                                 }

                                                 @Override
                                                 public void onFailure(Call<ResponseBody> call, Throwable t) {
                                                     Log.e("TAGA", "error");
                                                 }
                                             });
                                         }
                                     }

                                 }}
catch (Exception e)
{e.printStackTrace();}

                             }


                             @Override
                             public void onFailure(Call<ConfigResponse> call, Throwable t) {

                             }
                         }
            );

    }
    private boolean writeResponseBodyToDisk(ResponseBody body) {
        try {
            // todo change the file location/name according to your needs
            File futureStudioIconFile = new File(getExternalFilesDir(null) + File.separator + "Future Studio Icon.png");

            InputStream inputStream = null;
            OutputStream outputStream = null;
            String filename = "categories.json";
            try {
                byte[] fileReader = new byte[4096];

                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;

                inputStream = body.byteStream();
                outputStream = openFileOutput(filename, Context.MODE_PRIVATE);

                while (true) {
                    int read = inputStream.read(fileReader);

                    if (read == -1) {
                        break;
                    }

                    outputStream.write(fileReader, 0, read);

                    fileSizeDownloaded += read;

                    Log.d("dAWDdawwdawd", "file download: " + fileSizeDownloaded + " of " + fileSize);
                }

                outputStream.flush();

                return true;
            } catch (IOException e) {
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            return false;
        }
    }
}
