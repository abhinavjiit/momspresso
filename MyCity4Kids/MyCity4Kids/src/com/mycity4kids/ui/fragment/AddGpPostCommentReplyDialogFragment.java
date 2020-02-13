package com.mycity4kids.ui.fragment;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.ExifInterface;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kelltontech.utils.DateTimeUtils;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.filechooser.com.ipaulpro.afilechooser.utils.FileUtils;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.response.GroupPostCommentResult;
import com.mycity4kids.models.response.ImageUploadResponse;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.ImageUploadAPI;
import com.mycity4kids.ui.activity.GroupDetailsActivity;
import com.mycity4kids.ui.activity.GroupPostDetailActivity;
import com.mycity4kids.ui.activity.NewsLetterWebviewActivity;
import com.mycity4kids.ui.activity.ViewGroupPostCommentsRepliesActivity;
import com.mycity4kids.utils.AppUtils;
import com.mycity4kids.utils.AudioRecordView;
import com.mycity4kids.utils.GenericFileProvider;
import com.mycity4kids.utils.PermissionUtil;
import com.squareup.picasso.Picasso;
import com.yalantis.ucrop.UCrop;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

import static android.app.Activity.RESULT_OK;
import static com.facebook.accountkit.internal.AccountKitController.getApplicationContext;

/**
 * Created by user on 08-06-2015.
 */
public class AddGpPostCommentReplyDialogFragment extends DialogFragment implements OnClickListener, ProcessBitmapTaskFragment.TaskCallbacks,
        AudioRecordView.RecordingListener, SeekBar.OnSeekBarChangeListener {

    private static final String TAG_TASK_FRAGMENT = "task_fragment";
    private ProgressDialog mProgressDialog;
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;

    private static String[] PERMISSIONS_INIT = {Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
    private static String[] PERMISSIONS_INIT_FOR_AUDIO = {Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO};
    static int TOOLTIP_SHOW_TIMES;
    private static final int REQUEST_INIT_PERMISSION = 1;
    private static final int REQUEST_INIT_PERMISSION_FOR_AUDIO = 2;

    private HashMap<ImageView, String> imageUrlHashMap = new HashMap<>();
    private HashMap<ImageView, String> audioUrlHashMap = new HashMap<>();
    public static final int ADD_IMAGE_GALLERY_ACTIVITY_REQUEST_CODE = 1111;
    private Map<String, String> mediaMap = new HashMap<>();
    public static final int ADD_IMAGE_CAMERA_ACTIVITY_REQUEST_CODE = 1112;
    private boolean isRequestRunning = false;
    private ImageView closeImageView;
    private TextView addCommentTextView;
    private EditText commentReplyEditText;
    ImageView media;
    TextView replyCountTextViewmedia;
    TextView commentdatetextviewmedia;
    private TextView replyToTextView;
    private Uri imageUri;
    private File photoFile;
    private View separator;
    private String mCurrentPhotoPath, absoluteImagePath;
    private ImageView postImageView;
    private LinearLayout mediaContainer;
    private GroupPostCommentResult commentOrReplyData;
    private TextView imageCameraTextView, imageGalleryTextView, cancelTextView;
    private String actionType;
    private RelativeLayout chooseMediaTypeContainer;
    private int position;
    private TextView headingTextView;
    private ImageView addMediaImageView, addAudioImageView;
    private RelativeLayout relativeMainContainer;
    private ImageView commentorImageView;
    private TextView commentorUsernameTextView;
    private TextView commentDataTextView;
    private TextView commentDateTextView;
    private ImageView anonymousImageView;
    private TextView anonymousTextView;
    private CheckBox anonymousCheckbox;
    private View bottombarTopline;
    private int groupId, postId;
    private TextView addMediaTextView, audioTimeElapsed, audioTimeElapsedComment;
    private View mLayout;
    private ProcessBitmapTaskFragment mProcessBitmapTaskFragment;
    private MediaRecorder mRecorder;
    private String mFileName;
    private Boolean myFeed = false;
    private FirebaseAuth mAuth;
    private Uri originalUri;
    private Uri contentURI;
    private long suffixName;
    private MediaPlayer mMediaplayer, mMediaPlayerComment;
    private Uri downloadUri;
    private AudioRecordView audioRecordView;
    private long time;
    private RelativeLayout mLinearBottomSheet;
    private ImageView mImgRecordButton, mImgRecordCross;
    private Animation slideDownAnim, slideAnim, fadeAnim;
    private Handler mHandler = new Handler();
    private SeekBar audioSeekBarUpdate, audioSeekBar;
    private long totalDuration, currentDuration;
    private ImageView playAudio, pauseAudio;
    private int pos;
    private boolean isPlayed = false;
    private boolean isPaused = false;
    private boolean isCommentPlay = false;
    private LinearLayout playAudioLayout, timerLayout, dateContainermedia;
    private ImageView playAudioImageView, pauseAudioImageView, micImg;
    private ArrayList<String> audioCommentList;
    private boolean isLocked = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.add_gp_post_comment_reply_fragment, container,
                false);
        media = (ImageView) rootView.findViewById(R.id.media);
        commentdatetextviewmedia = (TextView) rootView.findViewById(R.id.commentDateTextViewmedia);
        mLayout = rootView.findViewById(R.id.root);
        addMediaImageView = (ImageView) rootView.findViewById(R.id.addMediaImageView);
        addAudioImageView = (ImageView) rootView.findViewById(R.id.addAudioImageView);
        addMediaTextView = (TextView) rootView.findViewById(R.id.addMediaTextView);
        closeImageView = (ImageView) rootView.findViewById(R.id.closeImageView);
        addCommentTextView = (TextView) rootView.findViewById(R.id.postCommentReplyTextView);
        commentReplyEditText = (EditText) rootView.findViewById(R.id.commentReplyEditText);
        headingTextView = (TextView) rootView.findViewById(R.id.headingTextView);
        relativeMainContainer = (RelativeLayout) rootView.findViewById(R.id.relativeMainContainer);
        commentorImageView = (ImageView) rootView.findViewById(R.id.commentorImageView);
        commentorUsernameTextView = (TextView) rootView.findViewById(R.id.commentorUsernameTextView);
        commentDataTextView = (TextView) rootView.findViewById(R.id.commentDataTextView);
        commentDateTextView = (TextView) rootView.findViewById(R.id.commentDateTextView);
        anonymousImageView = (ImageView) rootView.findViewById(R.id.anonymousImageView);
        anonymousTextView = (TextView) rootView.findViewById(R.id.anonymousTextView);
        anonymousCheckbox = (CheckBox) rootView.findViewById(R.id.anonymousCheckbox);
        bottombarTopline = rootView.findViewById(R.id.bottombarTopline);
        chooseMediaTypeContainer = (RelativeLayout) rootView.findViewById(R.id.chooseMediaTypeContainer);
        mediaContainer = (LinearLayout) rootView.findViewById(R.id.mediaContainer);
        imageCameraTextView = (TextView) rootView.findViewById(R.id.imageCameraTextView);
        imageGalleryTextView = (TextView) rootView.findViewById(R.id.imageGalleryTextView);
        postImageView = (ImageView) rootView.findViewById(R.id.postImageView);
        cancelTextView = (TextView) rootView.findViewById(R.id.cancelTextView);
        audioRecordView = rootView.findViewById(R.id.recordingView);
        mImgRecordButton = rootView.findViewById(R.id.record_button_red);
        mImgRecordCross = rootView.findViewById(R.id.bottomSheetCross);
        mLinearBottomSheet = rootView.findViewById(R.id.bottomsheet);
        slideAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.appear_from_bottom);
        fadeAnim = AnimationUtils.loadAnimation(getContext(), R.anim.alpha_anim);
        slideDownAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_slide_down_from_top);
        playAudioLayout = rootView.findViewById(R.id.playAudioLayout);
        timerLayout = rootView.findViewById(R.id.timerLayout);
        playAudioImageView = rootView.findViewById(R.id.playAudioImageView);
        pauseAudioImageView = rootView.findViewById(R.id.pauseAudioImageView);
        audioSeekBar = rootView.findViewById(R.id.audioSeekBar);
        dateContainermedia = rootView.findViewById(R.id.dateContainermedia);
        audioTimeElapsedComment = rootView.findViewById(R.id.audioTimeElapsed);


        audioRecordView.setRecordingListener(this);
        setListener();

        mAuth = FirebaseAuth.getInstance();

        AppUtils.createDirIfNotExists("MyCity4Kids/videos");
        mFileName = Environment.getExternalStorageDirectory() + "/MyCity4Kids/videos/";
        mFileName += "/audiorecordtest.m4a";

        commentReplyEditText.setOnTouchListener((v, event) -> {
            if (commentReplyEditText.hasFocus()) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_SCROLL:
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        return true;
                }
            }
            return false;
        });

        commentReplyEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().length() > 0) {
                    audioRecordView.setVisibility(View.GONE);
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) addMediaImageView.getLayoutParams();
                    params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                    addMediaImageView.setLayoutParams(params);
                } else {
                    audioRecordView.setVisibility(View.VISIBLE);
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) addMediaImageView.getLayoutParams();
                    params.removeRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                    params.addRule(RelativeLayout.LEFT_OF, R.id.recordingView);
                    addMediaImageView.setLayoutParams(params);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        Bundle extras = getArguments();
        commentOrReplyData = (GroupPostCommentResult) extras.get("parentCommentData");
        actionType = (String) extras.get("action");
        position = extras.getInt("position");
        groupId = extras.getInt("groupId");
        postId = extras.getInt("postId");
        if (extras.containsKey("myfeed")) {
            myFeed = true;
        }

        addCommentTextView.setOnClickListener(this);
        closeImageView.setOnClickListener(this);
        anonymousImageView.setOnClickListener(this);
        anonymousTextView.setOnClickListener(this);
        anonymousCheckbox.setOnClickListener(this);
        addMediaImageView.setOnClickListener(this);
        addMediaTextView.setOnClickListener(this);
        imageCameraTextView.setOnClickListener(this);
        imageGalleryTextView.setOnClickListener(this);
        audioRecordView.setOnClickListener(this);
        playAudioImageView.setOnClickListener(this);
        pauseAudioImageView.setOnClickListener(this);
//        addAudioImageView.setOnClickListener(this);
        mImgRecordCross.setOnClickListener(this);
        mImgRecordButton.setOnClickListener(this);
//        videoCameraTextView.setOnClickListener(this);
//        videoGalleryTextView.setOnClickListener(this);
        cancelTextView.setOnClickListener(this);

        if (SharedPrefUtils.isUserAnonymous(BaseApplication.getAppContext())) {
            anonymousCheckbox.setChecked(true);
        } else {
            anonymousCheckbox.setChecked(false);
        }


        if (commentOrReplyData == null) {
            commentReplyEditText.setText(SharedPrefUtils.getSavedReplyData(BaseApplication.getAppContext(), groupId,
                    postId, 0));
            headingTextView.setText(BaseApplication.getAppContext().getString(R.string.short_s_add_comment));
            relativeMainContainer.setVisibility(View.GONE);
        } else {
            if ("EDIT_COMMENT".equals(actionType) || "EDIT_REPLY".equals(actionType)) {
                headingTextView.setText(BaseApplication.getAppContext().getString(R.string.ad_comments_edit_label));
                relativeMainContainer.setVisibility(View.GONE);
                commentReplyEditText.setText(commentOrReplyData.getContent());
            } else {
                headingTextView.setText(BaseApplication.getAppContext().getString(R.string.reply));
                relativeMainContainer.setVisibility(View.VISIBLE);
                commentReplyEditText.setText(SharedPrefUtils.getSavedReplyData(BaseApplication.getAppContext(), commentOrReplyData.getGroupId(),
                        commentOrReplyData.getPostId(), commentOrReplyData.getParentId()));
                if (commentOrReplyData.getIsAnnon() == 1) {
                    commentorUsernameTextView.setText(BaseApplication.getAppContext().getString(R.string.groups_anonymous));
                    commentorImageView.setImageDrawable(ContextCompat.getDrawable(BaseApplication.getAppContext(), R.drawable.ic_incognito));
                    if (commentOrReplyData.getCommentType() == AppConstants.COMMENT_TYPE_AUDIO) {
                        audioCommentList = new ArrayList<>();
                        Map<String, String> map = (Map<String, String>) commentOrReplyData.getMediaUrls();
                        if (map != null && !map.isEmpty()) {
                            for (String entry : map.values()) {
                                audioCommentList.add(entry);
                            }
                            commentDateTextView.setVisibility(View.GONE);
                            media.setVisibility(View.GONE);
                            playAudioLayout.setVisibility(View.VISIBLE);
                            timerLayout.setVisibility(View.VISIBLE);
                            commentdatetextviewmedia.setVisibility(View.VISIBLE);
                            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) dateContainermedia.getLayoutParams();
                            params.addRule(RelativeLayout.BELOW, R.id.timerLayout);
                            dateContainermedia.setLayoutParams(params);
                        } else {
                            commentDateTextView.setVisibility(View.VISIBLE);
                            media.setVisibility(View.GONE);
                            playAudioLayout.setVisibility(View.GONE);
                            timerLayout.setVisibility(View.GONE);
                            commentdatetextviewmedia.setVisibility(View.GONE);
                        }
                    } else {
                        ArrayList<String> mediaList = new ArrayList<>();
                        Map<String, String> map = (Map<String, String>) commentOrReplyData.getMediaUrls();
                        if (map != null && !map.isEmpty()) {
                            for (String entry : map.values()) {
                                mediaList.add(entry);
                            }
                            commentDateTextView.setVisibility(View.GONE);
                            media.setVisibility(View.VISIBLE);
                            playAudioLayout.setVisibility(View.GONE);
                            timerLayout.setVisibility(View.GONE);
                            commentdatetextviewmedia.setVisibility(View.VISIBLE);
                            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) dateContainermedia.getLayoutParams();
                            params.addRule(RelativeLayout.BELOW, R.id.media);
                            dateContainermedia.setLayoutParams(params);
                            Picasso.get().load(mediaList.get(0)).error(R.drawable.default_article).into(media);
                        } else {
                            commentDateTextView.setVisibility(View.VISIBLE);
                            media.setVisibility(View.GONE);
                            playAudioLayout.setVisibility(View.GONE);
                            timerLayout.setVisibility(View.GONE);
                            commentdatetextviewmedia.setVisibility(View.GONE);
                        }
                    }
                } else {
                    try {
                        Picasso.get().load(commentOrReplyData.getUserInfo().getProfilePicUrl().getClientApp())
                                .placeholder(R.drawable.default_commentor_img).into((commentorImageView));
                    } catch (Exception e) {
                        Crashlytics.logException(e);
                        Log.d("MC4kException", Log.getStackTraceString(e));
                        if (isAdded())
                            Picasso.get().load(R.drawable.default_commentor_img).into(commentorImageView);
                    }
                    commentorUsernameTextView.setText(commentOrReplyData.getUserInfo().getFirstName() + " " + commentOrReplyData.getUserInfo().getLastName());
                    if (commentOrReplyData.getCommentType() == AppConstants.COMMENT_TYPE_AUDIO) {
                        audioCommentList = new ArrayList<>();
                        Map<String, String> map = (Map<String, String>) commentOrReplyData.getMediaUrls();
                        if (map != null && !map.isEmpty()) {
                            for (String entry : map.values()) {
                                audioCommentList.add(entry);
                            }
                            commentDateTextView.setVisibility(View.GONE);
                            media.setVisibility(View.GONE);
                            playAudioLayout.setVisibility(View.VISIBLE);
                            timerLayout.setVisibility(View.VISIBLE);
                            commentdatetextviewmedia.setVisibility(View.VISIBLE);
                            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) dateContainermedia.getLayoutParams();
                            params.addRule(RelativeLayout.BELOW, R.id.timerLayout);
                            dateContainermedia.setLayoutParams(params);
                        } else {
                            commentDateTextView.setVisibility(View.VISIBLE);
                            media.setVisibility(View.GONE);
                            playAudioLayout.setVisibility(View.GONE);
                            timerLayout.setVisibility(View.GONE);
                            commentdatetextviewmedia.setVisibility(View.GONE);
                        }
                    } else {
                        ArrayList<String> mediaList = new ArrayList<>();
                        Map<String, String> map = (Map<String, String>) commentOrReplyData.getMediaUrls();
                        if (map != null && !map.isEmpty()) {
                            for (String entry : map.values()) {
                                mediaList.add(entry);
                            }
                            commentDateTextView.setVisibility(View.GONE);
                            media.setVisibility(View.VISIBLE);
                            commentdatetextviewmedia.setVisibility(View.VISIBLE);
                            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) dateContainermedia.getLayoutParams();
                            params.addRule(RelativeLayout.BELOW, R.id.media);
                            dateContainermedia.setLayoutParams(params);
                            Picasso.get().load(mediaList.get(0)).error(R.drawable.default_article).into(media);
                        } else {
                            commentDateTextView.setVisibility(View.VISIBLE);
                            media.setVisibility(View.GONE);
                            commentdatetextviewmedia.setVisibility(View.GONE);
                        }
                    }
                }

                commentDataTextView.setText(commentOrReplyData.getContent());
                Linkify.addLinks(commentDataTextView, Linkify.WEB_URLS);
                commentDataTextView.setMovementMethod(LinkMovementMethod.getInstance());
                commentDataTextView.setLinkTextColor(ContextCompat.getColor(BaseApplication.getAppContext(), R.color.groups_blue_color));
                addLinkHandler(commentDataTextView);
                commentDateTextView.setText(DateTimeUtils.getDateFromNanoMilliTimestamp(commentOrReplyData.getCreatedAt()));
                commentdatetextviewmedia.setText(DateTimeUtils.getDateFromNanoMilliTimestamp(commentOrReplyData.getCreatedAt()));
            }
        }
        return rootView;
    }

    private void setListener() {

    }

    @Override
    public void onRecordingStarted() {
        Utils.groupsEvent(getActivity(), "Add a comment", "Audio", "android", SharedPrefUtils.getAppLocale(BaseApplication.getAppContext()), SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId(), String.valueOf(System.currentTimeMillis()), "click", "", String.valueOf(postId));

        if (mMediaPlayerComment != null && isCommentPlay) {
            mMediaPlayerComment.release();
            mMediaPlayerComment = null;
            playAudioImageView.setVisibility(View.VISIBLE);
            pauseAudioImageView.setVisibility(View.GONE);
            audioSeekBar.setProgress(0);
            audioTimeElapsedComment.setVisibility(View.GONE);
        }
        startRecording();
    }

    @Override
    public void onRecordingLocked() {
        isLocked = true;
    }

    @Override
    public void onRecordingCompleted() {
        Log.d("RecordView", "onFinish");
        isLocked = false;
        int recordTime = (int) ((System.currentTimeMillis() / (1000)) - time);
        if (recordTime < 1) {
            resetIcons();
            Toast.makeText(getActivity(), R.string.hold_to_release, Toast.LENGTH_SHORT).show();
        } else if (recordTime >= 4) {
            stopRecording();
            originalUri = Uri.parse(mFileName);
            contentURI = AppUtils.exportAudioToGallery(originalUri.getPath(), BaseApplication.getAppContext().getContentResolver(), getActivity());
            contentURI = AppUtils.getAudioUriFromMediaProvider(originalUri.getPath(), BaseApplication.getAppContext().getContentResolver());
            uploadAudio(contentURI);
            mImgRecordCross.setVisibility(View.VISIBLE);
            Log.d("RecordTime", "" + recordTime);
        } else {
            audioRecordView.disableClick(false);
            resetIcons();
            Toast.makeText(getActivity(), R.string.please_hold_for_3_seconds, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRecordingCanceled() {
        addAudioImageView.setEnabled(false);
        addMediaImageView.setEnabled(false);
        addMediaTextView.setEnabled(false);
    }

    @Override
    public void onReset() {
        resetIcons();
    }

    @Override
    public void setPermission() {
        requestAudioPermissions();
    }

    private void resetIcons() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                commentReplyEditText.setVisibility(View.VISIBLE);
                addMediaImageView.setVisibility(View.VISIBLE);
                anonymousCheckbox.setVisibility(View.VISIBLE);
                anonymousImageView.setVisibility(View.VISIBLE);
                anonymousTextView.setVisibility(View.VISIBLE);
                ViewGroup.LayoutParams params = audioRecordView.getLayoutParams();
                params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                audioRecordView.setLayoutParams(params);
                audioRecordView.disableClick(true);
            }
        }, 500);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity(), getTheme()) {
            @Override
            public void onBackPressed() {
                super.onBackPressed();
                if ("EDIT_COMMENT".equals(actionType)) {
                } else if ("EDIT_REPLY".equals(actionType)) {
                } else {
                    if (null != commentReplyEditText.getText() && !StringUtils.isNullOrEmpty(commentReplyEditText.getText().toString())) {
                        if (commentOrReplyData == null) {
                            SharedPrefUtils.setSavedReplyData(BaseApplication.getAppContext(), groupId,
                                    postId, 0, commentReplyEditText.getText().toString());
                        } else {
                            SharedPrefUtils.setSavedReplyData(BaseApplication.getAppContext(), commentOrReplyData.getGroupId(),
                                    commentOrReplyData.getPostId(), commentOrReplyData.getParentId(), commentReplyEditText.getText().toString());
                        }
                    }
                }
            }
        };
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.postCommentReplyTextView:
                Utils.groupsEvent(getActivity(), "Add a comment", "save", "android", SharedPrefUtils.getAppLocale(BaseApplication.getAppContext()), SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId(), String.valueOf(System.currentTimeMillis()), "post page", "", "");
                Map<String, String> mediaMap = new HashMap<>();
                int i = 1;
                if (!imageUrlHashMap.isEmpty()) {
                    for (Map.Entry<ImageView, String> entry : imageUrlHashMap.entrySet()) {
                        mediaMap.put("image" + i, entry.getValue());
                        i++;
                    }
                } else if (!audioUrlHashMap.isEmpty()) {
                    mediaMap.put("audio", downloadUri.toString());
                }
                if (isValid(mediaMap)) {
                    if ("EDIT_COMMENT".equals(actionType)) {
                        if (getActivity() instanceof GroupPostDetailActivity)
                            ((GroupPostDetailActivity) getActivity()).editComment(commentOrReplyData.getId(), commentReplyEditText.getText().toString(), position);
                        else if (getActivity() instanceof ViewGroupPostCommentsRepliesActivity) {
                            ((ViewGroupPostCommentsRepliesActivity) getActivity()).editComment(commentOrReplyData.getId(), commentReplyEditText.getText().toString(), position);
                        }
                    } else if ("EDIT_REPLY".equals(actionType)) {
                        if (getActivity() instanceof GroupPostDetailActivity)
                            ((GroupPostDetailActivity) getActivity()).editReply(commentReplyEditText.getText().toString(), commentOrReplyData.getParentId(), commentOrReplyData.getId());
                        else if (getActivity() instanceof ViewGroupPostCommentsRepliesActivity) {
                            ((ViewGroupPostCommentsRepliesActivity) getActivity()).editReply(commentReplyEditText.getText().toString(), commentOrReplyData.getParentId(), commentOrReplyData.getId());
                        }
                    } else {
                        if (commentOrReplyData == null) {
                            if (getActivity() instanceof GroupPostDetailActivity) {
                                ((GroupPostDetailActivity) getActivity()).addComment(commentReplyEditText.getText().toString(), mediaMap);
                            }
                            if (getActivity() instanceof GroupDetailsActivity) {
                                ((GroupDetailsActivity) getActivity()).addComment(commentReplyEditText.getText().toString(), mediaMap, groupId, postId);
                            }
                        } else {
                            if (getActivity() instanceof GroupPostDetailActivity) {
                                ((GroupPostDetailActivity) getActivity()).addReply(commentOrReplyData.getId(), commentReplyEditText.getText().toString(), mediaMap);
                            } else if (getActivity() instanceof ViewGroupPostCommentsRepliesActivity) {
                                ((ViewGroupPostCommentsRepliesActivity) getActivity()).addReply(commentOrReplyData.getId(), commentReplyEditText.getText().toString(), mediaMap);
                            }
                        }
                    }
                    dismiss();
                    commentReplyEditText.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.closeImageView:
                Utils.groupsEvent(getActivity(), "Add a comment", "save", "android", SharedPrefUtils.getAppLocale(BaseApplication.getAppContext()), SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId(), String.valueOf(System.currentTimeMillis()), "post page", "", "");
                if ("EDIT_COMMENT".equals(actionType)) {
                } else if ("EDIT_REPLY".equals(actionType)) {
                } else {
                    if (null != commentReplyEditText.getText() && !StringUtils.isNullOrEmpty(commentReplyEditText.getText().toString())) {
                        if (commentOrReplyData == null) {
                            SharedPrefUtils.setSavedReplyData(BaseApplication.getAppContext(), groupId,
                                    postId, 0, commentReplyEditText.getText().toString());
                        } else {
                            SharedPrefUtils.setSavedReplyData(BaseApplication.getAppContext(), commentOrReplyData.getGroupId(),
                                    commentOrReplyData.getPostId(), commentOrReplyData.getParentId(), commentReplyEditText.getText().toString());
                        }
                    }
                }
                dismiss();
                break;
            case R.id.cancelTextView:
                Utils.groupsEvent(getActivity(), "Add a comment", "save", "android", SharedPrefUtils.getAppLocale(BaseApplication.getAppContext()), SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId(), String.valueOf(System.currentTimeMillis()), "post page", "", "");
                chooseMediaTypeContainer.setVisibility(View.GONE);
                break;
            case R.id.chooseMediaTypeContainer:
                chooseMediaTypeContainer.setVisibility(View.GONE);
                break;
            case R.id.addMediaTextView:
            case R.id.addMediaImageView:
                if (Build.VERSION.SDK_INT >= 23) {
                    if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                            != PackageManager.PERMISSION_GRANTED
                            || ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED
                            || ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions();
                    } else {
                        openMediaChooserDialog();
                    }
                } else {
                    openMediaChooserDialog();
                }
                break;
            case R.id.bottomSheetCross:
                mLinearBottomSheet.startAnimation(slideDownAnim);
                slideDownAnim.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mLinearBottomSheet.setVisibility(View.GONE);
                                commentReplyEditText.setVisibility(View.VISIBLE);
                                addMediaImageView.setVisibility(View.VISIBLE);
                                anonymousCheckbox.setVisibility(View.VISIBLE);
                                anonymousImageView.setVisibility(View.VISIBLE);
                                anonymousTextView.setVisibility(View.VISIBLE);
                                addAudioImageView.setEnabled(true);
                                addMediaImageView.setEnabled(true);
                                addMediaTextView.setEnabled(true);
                            }
                        }, 0);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });
                break;
            case R.id.record_button_red:
                audioRecordView.setVisibility(View.VISIBLE);
                mImgRecordButton.setVisibility(View.GONE);
                mImgRecordCross.setVisibility(View.GONE);
                break;
            case R.id.imageCameraTextView:
                Utils.groupsEvent(getActivity(), "choose image pop up ", "capture image from camera", "android", SharedPrefUtils.getAppLocale(BaseApplication.getAppContext()), SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId(), String.valueOf(System.currentTimeMillis()), "camera", "", String.valueOf(postId));
                loadImageFromCamera();
                chooseMediaTypeContainer.setVisibility(View.GONE);
                break;
            case R.id.imageGalleryTextView:
                Utils.groupsEvent(getActivity(), "choose image pop up ", "choose from gallery", "android", SharedPrefUtils.getAppLocale(BaseApplication.getAppContext()), SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId(), String.valueOf(System.currentTimeMillis()), "phone gallery", "", String.valueOf(postId));
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, ADD_IMAGE_GALLERY_ACTIVITY_REQUEST_CODE);
                chooseMediaTypeContainer.setVisibility(View.GONE);
                break;
            case R.id.anonymousImageView:
            case R.id.anonymousTextView:
            case R.id.anonymousCheckbox:
                Utils.groupsEvent(getActivity(), "Add a comment", "Anonymous", "android", SharedPrefUtils.getAppLocale(BaseApplication.getAppContext()), SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId(), String.valueOf(System.currentTimeMillis()), "click", "", String.valueOf(postId));
                if (anonymousCheckbox.isChecked()) {
                    SharedPrefUtils.setUserAnonymous(BaseApplication.getAppContext(), true);
                } else {
                    SharedPrefUtils.setUserAnonymous(BaseApplication.getAppContext(), false);
                }
                break;
            case R.id.playAudioImageView:
                playComment();
                break;
            case R.id.pauseAudioImageView:
                pauseComment();
                break;
        }
    }

    private void pauseComment() {
        playAudioImageView.setVisibility(View.VISIBLE);
        pauseAudioImageView.setVisibility(View.GONE);
        mMediaPlayerComment.pause();
        isCommentPlay = false;
        isPaused = true;
        audioSeekBar.setProgress(0);
    }

    private void playComment() {
        if (mMediaplayer != null && isPlayed) {
            mMediaplayer.release();
            mMediaplayer = null;
            playAudio.setVisibility(View.VISIBLE);
            pauseAudio.setVisibility(View.GONE);
            audioSeekBarUpdate.setProgress(0);
            audioTimeElapsed.setVisibility(View.GONE);
            micImg.setVisibility(View.GONE);
        }
        pauseAudioImageView.setVisibility(View.VISIBLE);
        playAudioImageView.setVisibility(View.GONE);
        audioTimeElapsedComment.setVisibility(View.VISIBLE);
        isCommentPlay = true;
        if (mMediaPlayerComment != null && isPaused) {
            mMediaPlayerComment.start();
            updateCommentProgressBar();
            isPlayed = true;
            isPaused = false;
        } else {
            mMediaPlayerComment = new MediaPlayer();
            mMediaPlayerComment.setAudioStreamType(AudioManager.STREAM_MUSIC);
            audioSeekBar.setProgress(0);
            audioSeekBar.setMax(100);
            try {
                mMediaPlayerComment.setDataSource(audioCommentList.get(0));

                // wait for media player to get prepare
                mMediaPlayerComment.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mediaPlayer) {
                        mMediaPlayerComment.start();
                        updateCommentProgressBar();
                        isPlayed = true;
                    }
                });
                mMediaPlayerComment.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        if (isPlayed) {
                            isPlayed = false;
                            isCommentPlay = false;
                            playAudioImageView.setVisibility(View.VISIBLE);
                            pauseAudioImageView.setVisibility(View.GONE);
                            mMediaPlayerComment.stop();
                            mMediaPlayerComment = null;
                            audioSeekBar.setProgress(0);
                        }
                    }
                });
                mMediaPlayerComment.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadImageFromCamera() {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
            photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.i("TAG", "IOException");
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                try {
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, GenericFileProvider.getUriForFile(getActivity(), getActivity().getApplicationContext().getPackageName() + ".my.package.name.provider", createImageFile()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                startActivityForResult(cameraIntent, ADD_IMAGE_CAMERA_ACTIVITY_REQUEST_CODE);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        File image = File.createTempFile(
                imageFileName,  // prefix
                ".jpg",         // suffix
                dir      // directory
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        absoluteImagePath = image.getAbsolutePath();
        return image;
    }


    public void showProgressDialog(String bodyText) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            mProgressDialog.setCancelable(false);
        }

        mProgressDialog.setMessage(bodyText);

        if (!mProgressDialog.isShowing()) {
            mProgressDialog.show();
        }
    }

    public void removeProgressDialog() {
        try {
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private boolean isValid(Map<String, String> image) {
        if (StringUtils.isNullOrEmpty(commentReplyEditText.getText().toString()) && image.isEmpty()) {
            if (isAdded()) {
                if (isLocked) {
                    Toast.makeText(getActivity(), R.string.stop_recording, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getActivity(), "Please add a reply", Toast.LENGTH_LONG).show();
                }
                return false;
            }
        }
        return true;
    }

    private void addLinkHandler(TextView textView) {
        CharSequence text = textView.getText();
        if (text instanceof Spannable) {
            int end = text.length();
            Spannable sp = (Spannable) textView.getText();
            URLSpan[] urls = sp.getSpans(0, end, URLSpan.class);
            SpannableStringBuilder style = new SpannableStringBuilder(text);
            style.clearSpans();//should clear old spans
            for (URLSpan url : urls) {
                CustomerTextClick click = new CustomerTextClick(url.getURL());
                style.setSpan(click, sp.getSpanStart(url), sp.getSpanEnd(url), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            textView.setText(style);
        }
    }

    @Override
    public void onPreExecute() {
        if (isAdded())
            showProgressDialog(getString(R.string.please_wait));
    }

    @Override
    public void onCancelled() {

    }

    @Override
    public void onPostExecute(Bitmap image) {
        if (isAdded()) {
            String path = MediaStore.Images.Media.insertImage(getActivity().getContentResolver(), image, "Title" + System.currentTimeMillis(), null);
            Uri imageUriTemp = Uri.parse(path);
            File file2 = FileUtils.getFile(getActivity(), imageUriTemp);
            sendUploadProfileImageRequest(file2);
        } else {
            Toast.makeText(BaseApplication.getAppContext(), R.string.went_wrong, Toast.LENGTH_SHORT).show();
        }
    }


    private class CustomerTextClick extends ClickableSpan {
        private String mUrl;

        CustomerTextClick(String url) {
            mUrl = url;
        }

        @Override
        public void onClick(View widget) {
            if (isAdded()) {
                Intent intent = new Intent(getActivity(), NewsLetterWebviewActivity.class);
                intent.putExtra(Constants.URL, mUrl);
                getActivity().startActivity(intent);
            }
        }

    }

    private void requestPermissions() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE)
                || ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Snackbar.make(mLayout, R.string.permission_storage_rationale,
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
            Snackbar.make(mLayout, R.string.permission_camera_rationale,
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

    private void requestAudioPermissions() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.RECORD_AUDIO)
                    != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissionsForAudio();
            }
        }
    }

    private void requestUngrantedPermissionsForAudio() {
        ArrayList<String> permissionList = new ArrayList<>();
        for (int i = 0; i < PERMISSIONS_INIT_FOR_AUDIO.length; i++) {
            if (ActivityCompat.checkSelfPermission(getActivity(), PERMISSIONS_INIT_FOR_AUDIO[i]) != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(PERMISSIONS_INIT_FOR_AUDIO[i]);
            }
        }
        String[] requiredPermission = permissionList.toArray(new String[permissionList.size()]);
        requestPermissions(requiredPermission, REQUEST_INIT_PERMISSION_FOR_AUDIO);
    }

    private void requestPermissionsForAudio() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                Manifest.permission.RECORD_AUDIO)) {
            Snackbar.make(mLayout, R.string.permission_audio_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            requestUngrantedPermissionsForAudio();
                        }
                    })
                    .show();
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE)
                || ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Snackbar.make(mLayout, R.string.permission_storage_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            requestUngrantedPermissionsForAudio();
                        }
                    })
                    .show();
        } else {
            requestUngrantedPermissionsForAudio();
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
        requestPermissions(requiredPermission, REQUEST_INIT_PERMISSION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_INIT_PERMISSION_FOR_AUDIO) {
            if (PermissionUtil.verifyPermissions(grantResults)) {
                // All required permissions have been granted, display contacts fragment.
                Snackbar.make(mLayout, R.string.permision_available_init,
                        Snackbar.LENGTH_SHORT)
                        .show();
                AppUtils.createDirIfNotExists("MyCity4Kids/videos");
                mFileName = Environment.getExternalStorageDirectory() + "/MyCity4Kids/videos/";
                mFileName += "/audiorecordtest.m4a";
            } else {
                Log.i("Permissions", "storage permissions were NOT granted.");
                Snackbar.make(mLayout, R.string.permissions_not_granted,
                        Snackbar.LENGTH_SHORT)
                        .show();
            }
        } else if (requestCode == REQUEST_INIT_PERMISSION) {
            if (PermissionUtil.verifyPermissions(grantResults)) {
                Snackbar.make(mLayout, R.string.permision_available_init,
                        Snackbar.LENGTH_SHORT)
                        .show();
                openMediaChooserDialog();
            } else {
                Snackbar.make(mLayout, R.string.permissions_not_granted,
                        Snackbar.LENGTH_SHORT)
                        .show();
            }
        } else if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            if (PermissionUtil.verifyPermissions(grantResults)) {
                Snackbar.make(mLayout, R.string.permision_available_init,
                        Snackbar.LENGTH_SHORT)
                        .show();
                AppUtils.createDirIfNotExists("MyCity4Kids/videos");
                mFileName = Environment.getExternalStorageDirectory() + "/MyCity4Kids/videos/";
                mFileName += "/audiorecordtest.m4a";
            } else {
                Snackbar.make(mLayout, R.string.permissions_not_granted,
                        Snackbar.LENGTH_SHORT)
                        .show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void openMediaChooserDialog() {
        chooseMediaTypeContainer.setVisibility(View.VISIBLE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case ADD_IMAGE_GALLERY_ACTIVITY_REQUEST_CODE:
                if (data == null) {
                    return;
                }
                imageUri = data.getData();
                if (resultCode == RESULT_OK) {
                    try {
                        if (getActivity() instanceof GroupPostDetailActivity) {
                            ((GroupPostDetailActivity) getActivity()).processImage(imageUri);
                        } else if (getActivity() instanceof GroupDetailsActivity) {
                            ((GroupDetailsActivity) getActivity()).processImage(imageUri);
                        } else if (getActivity() instanceof ViewGroupPostCommentsRepliesActivity) {
                        } else {
                            android.app.FragmentManager fm = getActivity().getFragmentManager();
                            mProcessBitmapTaskFragment = (ProcessBitmapTaskFragment) fm.findFragmentByTag(TAG_TASK_FRAGMENT);
                            if (mProcessBitmapTaskFragment == null) {
                                mProcessBitmapTaskFragment = new ProcessBitmapTaskFragment();
                                Bundle bundle = new Bundle();
                                bundle.putParcelable("uri", imageUri);
                                mProcessBitmapTaskFragment.setArguments(bundle);
                                fm.beginTransaction().add(mProcessBitmapTaskFragment, TAG_TASK_FRAGMENT).commit();
                            }
                        }
                    } catch (Exception e) {
                        Crashlytics.logException(e);
                        e.printStackTrace();
                    }
                }
                break;
            case ADD_IMAGE_CAMERA_ACTIVITY_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    try {
                        Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), Uri.parse(mCurrentPhotoPath));
                        ExifInterface ei = new ExifInterface(absoluteImagePath);
                        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                                ExifInterface.ORIENTATION_UNDEFINED);
                        switch (orientation) {
                            case ExifInterface.ORIENTATION_ROTATE_90:
                                imageBitmap = rotateImage(imageBitmap, 90);
                                break;
                            case ExifInterface.ORIENTATION_ROTATE_180:
                                imageBitmap = rotateImage(imageBitmap, 180);
                                break;
                            case ExifInterface.ORIENTATION_ROTATE_270:
                                imageBitmap = rotateImage(imageBitmap, 270);
                                break;
                            case ExifInterface.ORIENTATION_NORMAL:
                            default:
                                break;
                        }

                        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 75, bytes);
                        byte[] bitmapData = bytes.toByteArray();

                        //write the bytes in file
                        FileOutputStream fos = new FileOutputStream(photoFile);
                        fos.write(bitmapData);
                        fos.flush();
                        fos.close();
                        imageUri = Uri.fromFile(photoFile);
                        File file2 = FileUtils.getFile(getActivity(), imageUri);
                        sendUploadProfileImageRequest(file2);
                        // compressImage(filePath);
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
        }
    }

    private void uploadAudio(Uri file) {
        FirebaseUser currentUser = mAuth.getCurrentUser();

        mAuth.signInAnonymously()
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("VideoUpload", "signInAnonymously:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            uploadAudioToFirebase(contentURI);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("VideoUpload", "signInAnonymously:failure", task.getException());
                            Toast.makeText(getActivity(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void uploadAudioToFirebase(Uri file2) {
        FirebaseStorage storage = FirebaseStorage.getInstance("gs://api-project-3577377239.appspot.com");

        final StorageReference storageRef = storage.getReference();

        suffixName = System.currentTimeMillis();
        StorageMetadata metadata = new StorageMetadata.Builder()
                .setContentType("audio/m4a")
                .build();
        final StorageReference riversRef = storageRef.child("user/" + SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId()
                + "/audio/" + file2.getLastPathSegment() + "_" + suffixName + ".m4a");
        com.google.firebase.storage.UploadTask uploadTask = riversRef.putFile(file2, metadata);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(com.google.firebase.storage.UploadTask.TaskSnapshot taskSnapshot) {
//                MixPanelUtils.pushVideoUploadSuccessEvent(mixpanel, title);
                riversRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        downloadUri = uri;
                        removeProgressDialog();
                        audioRecordView.setVisibility(View.GONE);
                        addMediaImageView.setVisibility(View.GONE);
                        commentReplyEditText.setVisibility(View.GONE);
                        addAudioToContainer(contentURI.toString());
                    }
                });
            }
        });

        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(com.google.firebase.storage.UploadTask.TaskSnapshot taskSnapshot) {
                Log.e("video uploaded", "Bytes uploaded: " + taskSnapshot.getBytesTransferred());
            }
        });
    }


    public void sendUploadProfileImageRequest(File file) {
        showProgressDialog(getString(R.string.please_wait));
        MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
        RequestBody requestBodyFile = RequestBody.create(MEDIA_TYPE_PNG, file);
        Log.e("requestBodyFile", requestBodyFile.toString());
        RequestBody imageType = RequestBody.create(MediaType.parse("text/plain"), "2");

        Retrofit retro = BaseApplication.getInstance().getRetrofit();
        ImageUploadAPI imageUploadAPI = retro.create(ImageUploadAPI.class);
        Call<ImageUploadResponse> call = imageUploadAPI.uploadImage(//userId,
                //  imageType,
                imageType,
                requestBodyFile);
        //asynchronous call
        call.enqueue(new Callback<ImageUploadResponse>() {
                         @Override
                         public void onResponse(Call<ImageUploadResponse> call, retrofit2.Response<ImageUploadResponse> response) {
                             removeProgressDialog();
                             if (response.body() == null) {
                                 Toast.makeText(getActivity(), "server_went_wrong", Toast.LENGTH_SHORT).show();
                                 return;
                             }
                             ImageUploadResponse responseModel = response.body();
                             if (responseModel.getCode() != 200) {
                                 Toast.makeText(getActivity(), "toast_response_error", Toast.LENGTH_SHORT).show();
                                 return;
                             } else {
                                 if (!StringUtils.isNullOrEmpty(responseModel.getData().getResult().getUrl())) {
                                     Log.i("IMAGE_UPLOAD_REQUEST", responseModel.getData().getResult().getUrl());
                                     addImageToContainer(responseModel.getData().getResult().getUrl());
                                     Toast.makeText(getActivity(), "image_upload_success", Toast.LENGTH_SHORT).show();

                                 } else {
                                     Toast.makeText(getActivity(), "please try again later", Toast.LENGTH_SHORT).show();
                                 }
                             }
                         }

                         @Override
                         public void onFailure(Call<ImageUploadResponse> call, Throwable t) {
                             Crashlytics.logException(t);
                             Log.d("MC4KException", Log.getStackTraceString(t));
                             Toast.makeText(getActivity(), "went_wrong", Toast.LENGTH_SHORT).show();
                             //showToast(getString(R.string.went_wrong));
                         }
                     }
        );
    }

    private static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix,
                true);
    }

    private void addImageToContainer(String url) {
        try {
            RelativeLayout rl = (RelativeLayout) LayoutInflater.from(getActivity()).inflate(R.layout.image_post_upload_item, null, false);

            ImageView uploadedIV = (ImageView) rl.findViewById(R.id.addImageOptionImageView);
            final ImageView removeIV = (ImageView) rl.findViewById(R.id.removeItemImageView);
            mediaContainer.addView(rl);
            imageUrlHashMap.put(removeIV, url);
            audioRecordView.setVisibility(View.GONE);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) addMediaImageView.getLayoutParams();
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            addMediaImageView.setLayoutParams(params);
            Picasso.get().load(url).error(R.drawable.default_article).into(uploadedIV);
            removeIV.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
                @Override
                public void onClick(View v) {
                    imageUrlHashMap.remove(removeIV);
                    mediaContainer.removeView((View) removeIV.getParent());
                    audioRecordView.setVisibility(View.VISIBLE);
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) addMediaImageView.getLayoutParams();
                    params.removeRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                    params.addRule(RelativeLayout.LEFT_OF, R.id.recordingView);
                    addMediaImageView.setLayoutParams(params);
                    android.app.FragmentManager fm = getActivity().getFragmentManager();
                    mProcessBitmapTaskFragment = null;
                    mProcessBitmapTaskFragment = (ProcessBitmapTaskFragment) fm.findFragmentByTag(TAG_TASK_FRAGMENT);
                    if (mProcessBitmapTaskFragment != null)
                        fm.beginTransaction().remove(mProcessBitmapTaskFragment).commit();
                }
            });
        } catch (Exception e) {
            Log.e("Crash", e.toString());
            Toast.makeText(getContext(), "something went wrong", Toast.LENGTH_SHORT).show();
        }
    }


    private void addAudioToContainer(String url) {
        RelativeLayout rl = (RelativeLayout) LayoutInflater.from(getActivity()).inflate(R.layout.audio_post_upload_item, null);
        final ImageView removeIV = (ImageView) rl.findViewById(R.id.removeItemImageView);
        playAudio = rl.findViewById(R.id.playAudioImageView);
        pauseAudio = rl.findViewById(R.id.pauseAudioImageView);
        micImg = rl.findViewById(R.id.mic_img);
        audioSeekBarUpdate = rl.findViewById(R.id.audioSeekBar);
        audioTimeElapsed = rl.findViewById(R.id.audioTimeElapsed);
        mediaContainer.addView(rl);
        audioUrlHashMap.put(removeIV, url);

        playAudio.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mMediaPlayerComment != null && isCommentPlay) {
                    mMediaPlayerComment.release();
                    mMediaPlayerComment = null;
                    playAudioImageView.setVisibility(View.VISIBLE);
                    pauseAudioImageView.setVisibility(View.GONE);
                    audioSeekBar.setProgress(0);
                    audioTimeElapsedComment.setVisibility(View.GONE);
                }
                pauseAudio.setVisibility(View.VISIBLE);
                playAudio.setVisibility(View.GONE);
                audioTimeElapsed.setVisibility(View.VISIBLE);
                isCommentPlay = false;
                if (mMediaplayer != null && isPaused) {
                    mMediaplayer.start();
                    updateProgressBar();
                    micImg.setVisibility(View.VISIBLE);
                    isPlayed = true;
                    isPaused = false;
                } else {
                    mMediaplayer = new MediaPlayer();
                    mMediaplayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    audioSeekBarUpdate.setProgress(0);
                    audioSeekBarUpdate.setMax(100);
                    try {
                        mMediaplayer.setDataSource(mFileName);

                        // wait for media player to get prepare
                        mMediaplayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(MediaPlayer mediaPlayer) {
                                mMediaplayer.start();
                                updateProgressBar();
                                micImg.setVisibility(View.VISIBLE);
                                isPlayed = true;
                            }
                        });
                        mMediaplayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mediaPlayer) {
                                if (isPlayed) {
                                    isPlayed = false;
                                    playAudio.setVisibility(View.VISIBLE);
                                    pauseAudio.setVisibility(View.GONE);
                                    mMediaplayer.stop();
                                    mMediaplayer = null;
//                                    audioSeekBarUpdate.setProgress(0);
                                }
                            }
                        });
                        mMediaplayer.prepare();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        pauseAudio.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                playAudio.setVisibility(View.VISIBLE);
                pauseAudio.setVisibility(View.GONE);
                mMediaplayer.pause();
                isPaused = true;
                isCommentPlay = false;
                audioSeekBarUpdate.setProgress(0);
            }
        });
        removeIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMediaplayer != null) {
                    mMediaplayer.stop();
                    mMediaplayer.release();
                    isCommentPlay = false;
                    mMediaplayer = null;
                }
                audioUrlHashMap.remove(removeIV);
                mediaContainer.removeView((View) removeIV.getParent());
                commentReplyEditText.setVisibility(View.VISIBLE);
                addAudioImageView.setEnabled(true);
                addMediaImageView.setEnabled(true);
                addMediaTextView.setEnabled(true);
                audioRecordView.setVisibility(View.VISIBLE);
                addMediaImageView.setVisibility(View.VISIBLE);
            }
        });

    }

    private void startRecording() {
        if (mRecorder != null) {
            mRecorder.release();
        }
        ViewGroup.LayoutParams params = audioRecordView.getLayoutParams();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        audioRecordView.setLayoutParams(params);
        mRecorder = new MediaRecorder();
        commentReplyEditText.setVisibility(View.GONE);
        addAudioImageView.setVisibility(View.GONE);
        addMediaImageView.setVisibility(View.GONE);
        anonymousCheckbox.setVisibility(View.GONE);
        anonymousImageView.setVisibility(View.GONE);
        anonymousTextView.setVisibility(View.GONE);
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mRecorder.setOutputFile(mFileName);
        time = System.currentTimeMillis() / (1000);
        try {
            mRecorder.prepare();
            mRecorder.start();
        } catch (IOException e) {
            Log.e("LOG_TAG", "prepare() failed");
        }
    }

    private void stopRecording() {
        if (mRecorder != null) {
            try {
                mRecorder.stop();
            } catch (RuntimeException e) {

            } finally {
                mRecorder.release();
                showProgressDialog(getString(R.string.please_wait));
                mRecorder = null;
                mLinearBottomSheet.startAnimation(slideDownAnim);
                slideDownAnim.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mLinearBottomSheet.setVisibility(View.GONE);
                                addAudioImageView.setEnabled(false);
                                addMediaImageView.setEnabled(false);
                                addMediaTextView.setEnabled(false);
                                commentReplyEditText.setVisibility(View.VISIBLE);
                                addMediaImageView.setVisibility(View.VISIBLE);
                                anonymousCheckbox.setVisibility(View.VISIBLE);
                                anonymousImageView.setVisibility(View.VISIBLE);
                                anonymousTextView.setVisibility(View.VISIBLE);
                                ViewGroup.LayoutParams params = audioRecordView.getLayoutParams();
                                params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                                audioRecordView.setLayoutParams(params);
                            }
                        }, 0);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        playAudioImageView.setVisibility(View.VISIBLE);
        pauseAudioImageView.setVisibility(View.GONE);
        if (!audioUrlHashMap.isEmpty()) {
            playAudio.setVisibility(View.VISIBLE);
            pauseAudio.setVisibility(View.GONE);
        }
        if (mRecorder != null) {
            mRecorder.release();
            mRecorder = null;
        }
        if (mMediaplayer != null) {
            mMediaplayer.release();
            mMediaplayer = null;
        }
        if (mMediaPlayerComment != null) {
            mMediaPlayerComment.release();
            mMediaPlayerComment = null;
        }
    }


    public void updateProgressBar() {
        mHandler.postDelayed(mUpdateTimeTask, 100);
    }

    private void updateCommentProgressBar() {
        mHandler.postDelayed(mUpdateCommentTimeTask, 100);
    }


    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            if (mMediaplayer != null && !isCommentPlay) {
                totalDuration = mMediaplayer.getDuration();
                currentDuration = mMediaplayer.getCurrentPosition();

                audioTimeElapsed.setText(milliSecondsToTimer(currentDuration) + "/" + milliSecondsToTimer(totalDuration));

                // Updating progress bar
                int progress = (int) (getProgressPercentage(currentDuration, totalDuration));
                //Log.d("Progress", ""+progress);
                audioSeekBarUpdate.setProgress(progress);


                // Running this thread after 100 milliseconds
                mHandler.postDelayed(this, 100);
            }
        }
    };


    private Runnable mUpdateCommentTimeTask = new Runnable() {
        public void run() {
            if (mMediaPlayerComment != null && isCommentPlay) {
                totalDuration = mMediaPlayerComment.getDuration();
                currentDuration = mMediaPlayerComment.getCurrentPosition();
                audioTimeElapsedComment.setText(milliSecondsToTimer(currentDuration) + "/" + milliSecondsToTimer(totalDuration));
                int progress = (int) (getProgressPercentage(currentDuration, totalDuration));
                audioSeekBar.setProgress(progress);
                mHandler.postDelayed(this, 100);
            }
        }
    };

    /**
     *
     */
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {

    }

    /**
     * When user starts moving the progress handler
     */
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // remove message Handler from updating progress bar
        if (isCommentPlay) {
            mHandler.removeCallbacks(mUpdateCommentTimeTask);
        } else {
            mHandler.removeCallbacks(mUpdateTimeTask);
        }
    }

    /**
     * When user stops moving the progress hanlder
     */
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (isCommentPlay) {
            mHandler.removeCallbacks(mUpdateCommentTimeTask);
            int totalDuration = mMediaPlayerComment.getDuration();
            int currentPosition = progressToTimer(seekBar.getProgress(), totalDuration);
            mMediaPlayerComment.seekTo(currentPosition);
        } else {
            mHandler.removeCallbacks(mUpdateTimeTask);
            int totalDuration = mMediaplayer.getDuration();
            int currentPosition = progressToTimer(seekBar.getProgress(), totalDuration);
            mMediaplayer.seekTo(currentPosition);
        }
    }

    private int progressToTimer(int progress, int totalDuration) {
        int currentDuration = 0;
        totalDuration = (int) (totalDuration / 1000);
        currentDuration = (int) ((((double) progress) / 100) * totalDuration);
        return currentDuration * 1000;
    }

    public String milliSecondsToTimer(long milliseconds) {
        String finalTimerString = "";
        String secondsString = "";

        // Convert total duration into time
        int hours = (int) (milliseconds / (1000 * 60 * 60));
        int minutes = (int) (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);
        // Add hours if there
        if (hours > 0) {
            finalTimerString = hours + ":";
        }

        // Prepending 0 to seconds if it is one digit
        if (seconds < 10) {
            secondsString = "0" + seconds;
        } else {
            secondsString = "" + seconds;
        }

        finalTimerString = finalTimerString + minutes + ":" + secondsString;

        // return timer string
        return finalTimerString;
    }

    private int getProgressPercentage(long currentDuration, long totalDuration) {
        Double percentage = (double) 0;
        long currentSeconds = (int) (currentDuration / 1000);
        long totalSeconds = (int) (totalDuration / 1000);
        percentage = (((double) currentSeconds) / totalSeconds) * 100;
        return percentage.intValue();
    }

    @Override
    public void onPause() {
        super.onPause();
        playAudioImageView.setVisibility(View.VISIBLE);
        pauseAudioImageView.setVisibility(View.GONE);
        if (!audioUrlHashMap.isEmpty()) {
            playAudio.setVisibility(View.VISIBLE);
            pauseAudio.setVisibility(View.GONE);
        }
        if (mMediaplayer != null) {
//            mMediaplayer.stop();
            mMediaplayer.release();
            mMediaplayer = null;
        }
        if (mMediaPlayerComment != null) {
            mMediaPlayerComment.release();
            mMediaPlayerComment = null;
        }
        if (mRecorder != null) {
            mRecorder.release();
            mRecorder = null;
        }
    }
}