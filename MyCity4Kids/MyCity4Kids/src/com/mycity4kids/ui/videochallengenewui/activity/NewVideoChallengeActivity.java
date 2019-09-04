package com.mycity4kids.ui.videochallengenewui.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.kelltontech.utils.ToastUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.Topics;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.ui.activity.VideoTrimmerActivity;
import com.mycity4kids.ui.fragment.ChooseVideoUploadOptionDialogFragment;
import com.mycity4kids.ui.videochallengenewui.Adapter.VideoChallengePagerAdapter;
import com.mycity4kids.utils.AppUtils;
import com.mycity4kids.utils.PermissionUtil;
import com.mycity4kids.videotrimmer.utils.FileUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

public class NewVideoChallengeActivity extends BaseActivity implements View.OnClickListener {
    VideoChallengePagerAdapter videoChallengePagerAdapter;
    AppBarLayout appBarLayout;
    RelativeLayout challengeHeaderRelative;
    RelativeLayout mainMediaFrameLayout;
    SimpleExoPlayerView exoplayerChallengeDetailListing;
    LinearLayout submitButtonLinearLayout;
    TextView challengeNameText, submitStoryText, toolbarTitleTextView;
    FloatingActionButton saveTextView;
    TabLayout tabs;
    private ViewPager viewPager;
    private static final int REQUEST_CAMERA_PERMISSION = 1;
    private static final int REQUEST_GALLERY_PERMISSION = 2;
    private static String[] PERMISSIONS_STORAGE_CAMERA = {Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
    private Toolbar toolbar;
    private String jsonMyObject;
    private String selectedId, mappedId;
    String screen;
    private String selected_Name;
    private String selectedActiveUrl;
    private String selectedStreamUrl;
    String challengeRules = "";
    private int pos;
    private Topics topic;
    private CoordinatorLayout rootLayout;
    private ArrayList<String> challengeId = new ArrayList<>();
    private ArrayList<String> activeUrl = new ArrayList<>();
    private ArrayList<String> activeStreamUrl = new ArrayList<>();
    private ArrayList<String> Display_Name = new ArrayList<>();
    private ArrayList<String> rules = new ArrayList<>();
    private ArrayList<String> mappedCategory = new ArrayList<>();
    private int max_Duration;
    private String parentName, parentId;
    private CoordinatorLayout coordinatorLayout;
    private ImageView thumbNail;
    private int duration;
    private String comingFrom = "";
    private CoordinatorLayout momVlogCoachMark;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_video_listing_detail);
        //coordinatorLayout = (CoordinatorLayout) findViewById(R.id.mainprofile_parent_layout);
        saveTextView = (FloatingActionButton) findViewById(R.id.saveTextView);
        appBarLayout = (AppBarLayout) findViewById(R.id.id_appbar);
        rootLayout = (CoordinatorLayout) findViewById(R.id.mainprofile_parent_layout);
        challengeHeaderRelative = (RelativeLayout) findViewById(R.id.challengeHeaderRelative);
        mainMediaFrameLayout = (RelativeLayout) findViewById(R.id.main_media_frame);
        exoplayerChallengeDetailListing = (SimpleExoPlayerView) findViewById(R.id.exoplayerChallengeDetailListing);
        submitButtonLinearLayout = (LinearLayout) findViewById(R.id.submit_challenge_relative_Layout);
        challengeNameText = (TextView) findViewById(R.id.ChallengeNameText);
        submitStoryText = (TextView) findViewById(R.id.submit_story_text);
        tabs = (TabLayout) findViewById(R.id.id_tabs);
        thumbNail = (ImageView) findViewById(R.id.thumbNail);
        viewPager = (ViewPager) findViewById(R.id.id_viewpager);
        toolbar = (Toolbar) findViewById(R.id.id_toolbar);
        momVlogCoachMark = findViewById(R.id.momVlogCoachMark);
        toolbarTitleTextView = (TextView) findViewById(R.id.toolbarTitleTextView);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbarTitleTextView.setText(getString(R.string.myprofile_section_videos_label));


        Intent intent = getIntent();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            jsonMyObject = extras.getString("Topic");
        }
        topic = new Gson().fromJson(jsonMyObject, Topics.class);
        pos = intent.getIntExtra("position", 0);
        challengeId = intent.getStringArrayListExtra("challenge");
        Display_Name = intent.getStringArrayListExtra("Display_Name");
        activeUrl = intent.getStringArrayListExtra("StringUrl");
        parentId = intent.getStringExtra("parentId");
        parentName = intent.getStringExtra("topics");
        screen = intent.getStringExtra("screenName");
        max_Duration = intent.getIntExtra("maxDuration", 0);
        activeStreamUrl = intent.getStringArrayListExtra("StreamUrl");
        rules = intent.getStringArrayListExtra("rules");
        if (intent.hasExtra("comingFrom")) {
            comingFrom = intent.getStringExtra("comingFrom");
            if (comingFrom.equals("chooseVideoCategory")) {
                saveTextView.setVisibility(View.VISIBLE);
            }

        }

        mappedCategory = intent.getStringArrayListExtra("mappedCategory");
        if (mappedCategory != null && mappedCategory.size() != 0) {
            mappedId = mappedCategory.get(pos);
        }

        if (challengeId != null && challengeId.size() != 0) {
            selectedId = challengeId.get(pos);
        } else {
            ToastUtils.showToast(this, "server problem,please refresh your app");
        }
        if (activeUrl != null && activeUrl.size() != 0) {
            selectedActiveUrl = activeUrl.get(pos);
        } else {
            ToastUtils.showToast(this, "server problem,please refresh your app");
        }
        if (activeStreamUrl != null && activeStreamUrl.size() != 0) {
            selectedStreamUrl = activeStreamUrl.get(pos);
        } else {
            ToastUtils.showToast(this, "server problem,please refresh your app");
        }
        if (Display_Name != null && Display_Name.size() != 0) {
            selected_Name = Display_Name.get(pos);
        } else {
            ToastUtils.showToast(this, "server problem,please refresh your app");
        }
        if (rules != null && rules.size() != 0) {
            if (rules.size() > pos) {
                challengeRules = rules.get(pos);
            }
        }
        try {
            Picasso.with(this).load(selectedActiveUrl)
                    .placeholder(R.drawable.default_article).error(R.drawable.default_article).into(thumbNail);
        } catch (Exception e) {
            thumbNail.setImageResource(R.drawable.default_article);
        }
        tabs.addTab(tabs.newTab().setText(getResources().getString(R.string.about_video)));
        tabs.addTab(tabs.newTab().setText(getResources().getString(R.string.all_videos_toolbar_title)));
        AppUtils.changeTabsFont(NewVideoChallengeActivity.this, tabs);
        View root = tabs.getChildAt(0);
        if (root instanceof LinearLayout) {
            ((LinearLayout) root).setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
            GradientDrawable drawable = new GradientDrawable();
            drawable.setColor(getResources().getColor(R.color.app_red));
            drawable.setSize(5, 1);
            ((LinearLayout) root).setDividerPadding(20);
            ((LinearLayout) root).setDividerDrawable(drawable);
        }


        videoChallengePagerAdapter = new VideoChallengePagerAdapter(getSupportFragmentManager(), selected_Name, selectedActiveUrl, selectedId, topic, selectedStreamUrl, challengeRules);
        viewPager.setAdapter(videoChallengePagerAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabs));
        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {


                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

                viewPager.setCurrentItem(tab.getPosition());


            }
        });
        thumbNail.setOnClickListener(this);
        saveTextView.setOnClickListener(view -> {
            //   SharedPrefUtils.setToastMomVlog(this, "Challenge", true);

            ChooseVideoUploadOptionDialogFragment chooseVideoUploadOptionDialogFragment = new ChooseVideoUploadOptionDialogFragment();
            FragmentManager fm = getSupportFragmentManager();
            Bundle _args = new Bundle();
            _args.putString("activity", "newVideoChallengeActivity");
            if (max_Duration != 0) {
                _args.putString("duration", String.valueOf(max_Duration));
            }

            chooseVideoUploadOptionDialogFragment.setArguments(_args);
            chooseVideoUploadOptionDialogFragment.setCancelable(true);
            chooseVideoUploadOptionDialogFragment.show(fm, "Choose video option");

        });


    }

    @Override
    protected void updateUi(Response response) {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
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
    public void onClick(View view) {

        if (view.getId() == R.id.thumbNail) {
            Intent intent = new Intent(this, ExoplayerVideoChallengePlayViewActivity.class);
            intent.putExtra("StreamUrl", selectedStreamUrl);
            startActivity(intent);
            Utils.momVlogEvent(NewVideoChallengeActivity.this, "Challenge detail", "Prompt_video_play", "", "android", SharedPrefUtils.getAppLocale(NewVideoChallengeActivity.this), SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId(), String.valueOf(System.currentTimeMillis()), "Show_Video_Detail", "", "");

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
     /*   intent.putExtra("categoryId", categoryId);
        intent.putExtra("duration", duration);*/
        /**/
        if (max_Duration != 0) {
            intent.putExtra("duration", String.valueOf(max_Duration));
        }

        intent.putExtra("ChallengeId", selectedId);
        intent.putExtra("categoryId", mappedId);
        intent.putExtra("comingFrom", "Challenge");


        // if (null != filepath && (filepath.endsWith(".mp4") || filepath.endsWith(".MP4"))) {
        intent.putExtra("EXTRA_VIDEO_PATH", FileUtils.getPath(this, uri));
        startActivity(intent);

    }

    public void showDialogBox() {

        if (screen.equals("creation")) {
            if (comingFrom.equals("chooseVideoCategory")) {
                ChooseVideoUploadOptionDialogFragment chooseVideoUploadOptionDialogFragment = new ChooseVideoUploadOptionDialogFragment();
                FragmentManager fm = getSupportFragmentManager();
                Bundle _args = new Bundle();
                _args.putString("activity", "newVideoChallengeActivity");
                if (max_Duration != 0) {
                    _args.putString("duration", String.valueOf(max_Duration));
                }

                chooseVideoUploadOptionDialogFragment.setArguments(_args);
                chooseVideoUploadOptionDialogFragment.setCancelable(true);
                chooseVideoUploadOptionDialogFragment.show(fm, "Choose video option");

            }
            viewPager.setCurrentItem(0);
            saveTextView.setVisibility(View.VISIBLE);
        } else {

            viewPager.setCurrentItem(1);
            saveTextView.setVisibility(View.VISIBLE);
        }
    }

}


