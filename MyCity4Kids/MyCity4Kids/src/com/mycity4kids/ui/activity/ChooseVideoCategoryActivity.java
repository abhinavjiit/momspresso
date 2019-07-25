package com.mycity4kids.ui.activity;

import android.Manifest;
import android.accounts.NetworkErrorException;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.kelltontech.utils.ConnectivityUtils;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.ExploreTopicsModel;
import com.mycity4kids.models.ExploreTopicsResponse;
import com.mycity4kids.models.Topics;
import com.mycity4kids.models.TopicsResponse;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.TopicsCategoryAPI;
import com.mycity4kids.retrofitAPIsInterfaces.VlogsListingAndDetailsAPI;
import com.mycity4kids.ui.adapter.ParentTopicsGridAdapter;
import com.mycity4kids.ui.fragment.ChooseVideoUploadOptionDialogFragment;
import com.mycity4kids.ui.videochallengenewui.Adapter.VideoChallengeTopicsAdapter;
import com.mycity4kids.ui.videochallengenewui.ExpandableHeightGridView;
import com.mycity4kids.ui.videochallengenewui.activity.NewVideoChallengeActivity;
import com.mycity4kids.utils.AppUtils;
import com.mycity4kids.utils.ArrayAdapterFactory;
import com.mycity4kids.utils.PermissionUtil;
import com.mycity4kids.videotrimmer.utils.FileUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by hemant on 30/10/18.
 */

public class ChooseVideoCategoryActivity extends BaseActivity implements View.OnClickListener, VideoChallengeTopicsAdapter.RecyclerViewClickListener {

    private static final int REQUEST_CAMERA_PERMISSION = 1;
    private static final int REQUEST_GALLERY_PERMISSION = 2;
    private static String[] PERMISSIONS_STORAGE_CAMERA = {Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};

    private ArrayList<ExploreTopicsModel> mainTopicsList;
    private ParentTopicsGridAdapter adapter;
    private ExpandableHeightGridView gridview;
    private RecyclerView horizontalRecyclerViewForVideoChallenge;
    private Toolbar toolbar;
    private LinearLayout rootLayout;
    private String categoryId;
    private String duration;
    private String challengeId, challengeName, comingFrom;
    private Topics videoChallengeTopics;
    private Topics articledatamodelsnew;
    private TextView challengesTextView, categoriesTextView;
    private String jasonMyObject;
    VideoChallengeTopicsAdapter videoChallengeTopicsAdapter;
    private LinearLayoutManager linearLayoutManager;
    private ArrayList<String> challenge_Id = new ArrayList<String>();
    private ArrayList<String> Display_Name = new ArrayList<String>();
    private ArrayList<String> activeImageUrl = new ArrayList<String>();
    private ArrayList<String> activeStreamUrl = new ArrayList<String>();
    private ArrayList<String> info = new ArrayList<String>();
    private String challengeRulesInDialogBox;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_video_category_activity);
        ((BaseApplication) getApplication()).setActivity(this);

        rootLayout = (LinearLayout) findViewById(R.id.rootLayout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        gridview = (ExpandableHeightGridView) findViewById(R.id.gridview);
        horizontalRecyclerViewForVideoChallenge = (RecyclerView) findViewById(R.id.horizontalRecyclerViewForVideoChallenge);
        challengesTextView = (TextView) findViewById(R.id.challengesTextView);
        categoriesTextView = (TextView) findViewById(R.id.categoriesTextView);
        /*horizontalRecyclerViewForVideoChallenge.setExpanded(true);*/
        gridview.setExpanded(true);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Intent intent = getIntent();
        comingFrom = intent.getStringExtra("comingFrom");
        if (StringUtils.isNullOrEmpty(comingFrom)) {
            comingFrom = "notFromChallenge";
        }

        if (comingFrom.equals("Challenge")) {
            challengeId = intent.getStringExtra("selectedId");
            challengeName = intent.getStringExtra("selectedName");

        } else if (comingFrom.equals("createDashboardIcon")) {

            //        Bundle extras = getIntent().getExtras();
            if (intent != null) {
               // jasonMyObject = intent.getStringExtra("currentChallengesTopic");

            }

         //   videoChallengeTopics = new Gson().fromJson(jasonMyObject, Topics.class);
            categoriesTextView.setVisibility(View.VISIBLE);
            challengesTextView.setVisibility(View.VISIBLE);
            horizontalRecyclerViewForVideoChallenge.setVisibility(View.VISIBLE);
            linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
            horizontalRecyclerViewForVideoChallenge.setLayoutManager(linearLayoutManager);
            videoChallengeTopicsAdapter = new VideoChallengeTopicsAdapter(this, this, challenge_Id, Display_Name, activeImageUrl, activeStreamUrl, info);
            horizontalRecyclerViewForVideoChallenge.setAdapter(videoChallengeTopicsAdapter);
        } else {
            comingFrom = "notFromChallenge";
        }

        getChallengeData();
        try {
            FileInputStream fileInputStream = BaseApplication.getAppContext().openFileInput(AppConstants.CATEGORIES_JSON_FILE);
            String fileContent = AppUtils.convertStreamToString(fileInputStream);
            Gson gson = new GsonBuilder().registerTypeAdapterFactory(new ArrayAdapterFactory()).create();
            ExploreTopicsResponse res = gson.fromJson(fileContent, ExploreTopicsResponse.class);
            createTopicsData(res);

            adapter = new ParentTopicsGridAdapter(null);
            gridview.setAdapter(adapter);
            gridview.setExpanded(true);
            adapter.setDatalist(mainTopicsList);
//            adapter.setVideoFlag();
        } catch (FileNotFoundException e) {
            Retrofit retro = BaseApplication.getInstance().getRetrofit();
            final TopicsCategoryAPI topicsAPI = retro.create(TopicsCategoryAPI.class);

            Call<ResponseBody> caller = topicsAPI.downloadTopicsJSON();
            caller.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                    AppUtils.writeResponseBodyToDisk(BaseApplication.getAppContext(), AppConstants.CATEGORIES_JSON_FILE, response.body());

                    try {
                        FileInputStream fileInputStream = BaseApplication.getAppContext().openFileInput(AppConstants.CATEGORIES_JSON_FILE);
                        String fileContent = AppUtils.convertStreamToString(fileInputStream);
                        Gson gson = new GsonBuilder().registerTypeAdapterFactory(new ArrayAdapterFactory()).create();
                        ExploreTopicsResponse res = gson.fromJson(fileContent, ExploreTopicsResponse.class);
                        createTopicsData(res);
                        adapter = new ParentTopicsGridAdapter(null);
                        gridview.setAdapter(adapter);
                        adapter.setDatalist(mainTopicsList);
//                        adapter.setVideoFlag();
                    } catch (FileNotFoundException e) {
                        Crashlytics.logException(e);
                        Log.d("FileNotFoundException", Log.getStackTraceString(e));
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Crashlytics.logException(t);
                    Log.d("MC4KException", Log.getStackTraceString(t));
                }
            });
        }

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                view.setSelected(true);
                ExploreTopicsModel topic = (ExploreTopicsModel) adapterView.getAdapter().getItem(position);
                if (topic == null) {
                    return;
                }
                categoryId = topic.getId();
                if (topic.getExtraData() == null || topic.getExtraData().isEmpty()) {
                    duration = "60";
                } else if (StringUtils.isNullOrEmpty(topic.getExtraData().get(0).getMax_duration())) {
                    duration = "60";
                } else {
                    duration = topic.getExtraData().get(0).getMax_duration();
                }

                launchAddVideoOptions();
                Utils.momVlogEvent(ChooseVideoCategoryActivity.this, "Creation listing", "Category_Name", "", "android", SharedPrefUtils.getAppLocale(ChooseVideoCategoryActivity.this), SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId(), String.valueOf(System.currentTimeMillis()), "Show_upload_video", categoryId, "");

//                Intent intent = new Intent(ChooseVideoCategoryActivity.this, TopicsListingActivity.class);
//                intent.putExtra("parentTopicId", topic.getId());
//                startActivity(intent);
            }
        });
    }

    private void createTopicsData(ExploreTopicsResponse responseData) {
        try {
            mainTopicsList = new ArrayList<>();
            for (int i = 0; i < responseData.getData().size(); i++) {
                if (AppConstants.HOME_VIDEOS_CATEGORYID.equals(responseData.getData().get(i).getId())) {
                    for (int j = 0; j < responseData.getData().get(i).getChild().size(); j++) {
                        if ("1".equals(responseData.getData().get(i).getChild().get(j).getPublicVisibility())) {
                            mainTopicsList.add(responseData.getData().get(i).getChild().get(j));
                        }
                    }
                }
            }
        } catch (Exception e) {
            Crashlytics.logException(e);
            Log.d("MC4kException", Log.getStackTraceString(e));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        ((BaseApplication) getApplication()).setView(rootLayout);
    }

    public void launchAddVideoOptions() {
        ChooseVideoUploadOptionDialogFragment chooseVideoUploadOptionDialogFragment = new ChooseVideoUploadOptionDialogFragment();
        FragmentManager fm = getSupportFragmentManager();
        Bundle _args = new Bundle();
        _args.putString("activity", "video_category_activity");
        _args.putString("categoryId", categoryId);
        _args.putString("duration", duration);
        chooseVideoUploadOptionDialogFragment.setArguments(_args);
        chooseVideoUploadOptionDialogFragment.setCancelable(true);
        chooseVideoUploadOptionDialogFragment.show(fm, "Choose video option");
    }

    public void requestPermissions(final String imageFrom) {
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
            Snackbar.make(rootLayout, R.string.permission_storage_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            requestUngrantedPermissions(imageFrom);
                        }
                    })
                    .show();
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(this,
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
            if (ActivityCompat.checkSelfPermission(this, PERMISSIONS_STORAGE_CAMERA[i]) != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(PERMISSIONS_STORAGE_CAMERA[i]);
            }
        }
        String[] requiredPermission = permissionList.toArray(new String[permissionList.size()]);
        if ("gallery".equals(imageFrom)) {
            ActivityCompat.requestPermissions(this, requiredPermission, REQUEST_GALLERY_PERMISSION);
        } else if ("camera".equals(imageFrom)) {
            ActivityCompat.requestPermissions(this, requiredPermission, REQUEST_CAMERA_PERMISSION);
        }
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            Log.i("Permissions", "Received response for camera permissions request.");

            if (PermissionUtil.verifyPermissions(grantResults)) {
                Snackbar.make(rootLayout, R.string.permision_available_init,
                        Snackbar.LENGTH_SHORT)
                        .show();
                Intent videoCapture = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                startActivityForResult(videoCapture, AppConstants.REQUEST_VIDEO_TRIMMER);
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
                Intent intent = new Intent();
                intent.setType("video/mp4");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(Intent.createChooser(intent, getString(R.string.label_select_video)), AppConstants.REQUEST_VIDEO_TRIMMER);
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

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case AppConstants.REQUEST_VIDEO_TRIMMER:
                final Uri selectedUri = data.getData();
                if (selectedUri != null) {
                    startTrimActivity(selectedUri);
                } else {
                    Toast.makeText(this, R.string.toast_cannot_retrieve_selected_video, Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void startTrimActivity(@NonNull Uri uri) {
        Intent intent = new Intent(this, VideoTrimmerActivity.class);
        String filepath = FileUtils.getPath(this, uri);
        intent.putExtra("categoryId", categoryId);
        intent.putExtra("duration", duration);

        if (comingFrom.equals("Challenge")) {
            intent.putExtra("ChallengeId", challengeId);
            intent.putExtra("ChallengeName", challengeName);
            intent.putExtra("comingFrom", "Challenge");
        } else {
            intent.putExtra("comingFrom", "notFromChallenge");
        }

        // if (null != filepath && (filepath.endsWith(".mp4") || filepath.endsWith(".MP4"))) {
        intent.putExtra("EXTRA_VIDEO_PATH", FileUtils.getPath(this, uri));
        startActivity(intent);
       /* } else {
            showToast(getString(R.string.choose_mp4_file));
        }*/
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onClick(View view, int position, ArrayList<String> challengeId, ArrayList<String> Display_Name, Topics articledatamodelsnew, ArrayList<String> imageUrl, ArrayList<String> activeStreamUrl, ArrayList<String> info, ArrayList<String> mappedCategory, int max_Duration) {


        switch (view.getId()) {
            case R.id.tagImageView:
            case R.id.topicContainer:
                Intent intent = new Intent(this, NewVideoChallengeActivity.class);
                intent.putExtra("Display_Name", Display_Name);
                intent.putExtra("screenName", "creation");
                intent.putExtra("challenge", challengeId);
                intent.putExtra("position", position);
                intent.putExtra("maxDuration", max_Duration);
                intent.putExtra("StreamUrl", activeStreamUrl);
                intent.putExtra("topics", articledatamodelsnew.getParentName());
                intent.putExtra("parentId", articledatamodelsnew.getParentId());
                intent.putExtra("mappedCategory", mappedCategory);
                intent.putExtra("StringUrl", activeImageUrl);
                intent.putExtra("rules", info);
                intent.putExtra("comingFrom", "chooseVideoCategory");
                intent.putExtra("Topic", new Gson().toJson(articledatamodelsnew));
                startActivity(intent);
                Utils.momVlogEvent(ChooseVideoCategoryActivity.this, "Creation listing", "Listing_challenge_container", "", "android", SharedPrefUtils.getAppLocale(ChooseVideoCategoryActivity.this), SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId(), String.valueOf(System.currentTimeMillis()), "Show_Challenge_Detail", "", challengeId.toString());

                break;


            case R.id.info:
                if (info != null) {

                    if (info.size() > position) {
                        if (!StringUtils.isNullOrEmpty(info.get(position))) {
                            challengeRulesInDialogBox = info.get(position);
                            //  ToastUtils.showToast(this, String.valueOf(position) + " clicked");
                            final Dialog dialog = new Dialog(this);
                            dialog.setContentView(R.layout.challenge_rules_dialog);
                            dialog.setTitle("Title...");
                            ImageView imageView = (ImageView) dialog.findViewById(R.id.closeEditorImageView);
                            WebView webView = (WebView) dialog.findViewById(R.id.videoChallengeRulesWebView);
                            webView.loadData(challengeRulesInDialogBox, "text/html", "UTF-8");
                            imageView.setOnClickListener(view2 -> dialog.dismiss());

                            dialog.show();
                            Utils.momVlogEvent(ChooseVideoCategoryActivity.this, "Creation listing", "Challenge_info", "", "android", SharedPrefUtils.getAppLocale(ChooseVideoCategoryActivity.this), SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId(), String.valueOf(System.currentTimeMillis()), "Show_Challenge_Detail", "", challengeId.toString());

                        }
                    }
                }
                break;
        }


    }


    private void getChallengeData() {


        if (!ConnectivityUtils.isNetworkEnabled(this)) {
            removeProgressDialog();
            return;
        }

        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        VlogsListingAndDetailsAPI vlogsListingAndDetailsAPI = retrofit.create(VlogsListingAndDetailsAPI.class);
        Call<TopicsResponse> callRecentVideoArticles = vlogsListingAndDetailsAPI.getVlogChallenges();
        callRecentVideoArticles.enqueue(vlogChallengeResponseCallBack);

    }

    private Callback<TopicsResponse> vlogChallengeResponseCallBack = new Callback<TopicsResponse>() {
        @Override
        public void onResponse(Call<TopicsResponse> call, retrofit2.Response<TopicsResponse> response) {
            if (response == null || null == response.body()) {
                NetworkErrorException nee = new NetworkErrorException(response.raw().toString());
                Crashlytics.logException(nee);
                return;
            }
            if (response.isSuccessful()) {
                try {
                    TopicsResponse responseData = response.body();
                    if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {

                        videoChallengeTopicsAdapter.setData(responseData.getData());
                        videoChallengeTopicsAdapter.notifyDataSetChanged();


                    }
                } catch (Exception e) {
                    Crashlytics.logException(e);
                    Log.d("MC4kException", Log.getStackTraceString(e));
                }
            }


        }

        @Override
        public void onFailure(Call<TopicsResponse> call, Throwable t) {

        }
    };
}
