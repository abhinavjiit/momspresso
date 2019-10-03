package com.mycity4kids.utils;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.text.Layout;
import android.text.Spanned;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.crashlytics.android.Crashlytics;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kelltontech.ui.BaseFragment;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.Topics;
import com.mycity4kids.models.TopicsResponse;
import com.mycity4kids.models.response.ArticleListingResult;
import com.mycity4kids.models.response.LanguageConfigModel;
import com.mycity4kids.widget.Hashids;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import okhttp3.ResponseBody;

/**
 * Created by hemant on 22/11/16.
 */
public class AppUtils {

    private static final int SS_FB_CARD_WIDTH = 1200;
    private static final int SS_FB_CARD_HEIGHT = 628;
    private static final int SS_FB_CARD_HEIGHT_WITHOUT_AUTHOR = 537;
    private static final int SS_WA_INSTA_CARD_WIDTH = 800;
    private static final int SS_WA_INSTA_CARD_HEIGHT = 800;

    private static String SALT = "iasdas1oi23ubnaoligueiug12311028313liuege";
    private static float singleContentHeight = 800f;

    public static int randInt(int min, int max) {

        // NOTE: This will (intentionally) not run as written so that folks
        // copy-pasting have to think about how to initialize their
        // Random instance.  Initialization of the Random instance is outside
        // the main scope of the question, but some decent options are to have
        // a field that is initialized once and then re-used as needed or to
        // use ThreadLocalRandom (if using at least Java 1.7).
        Random rand = new Random();

        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        int randomNum = rand.nextInt((max - min) + 1) + min;

        return randomNum;
    }

    public static String getYoutubeThumbnailURL(String youtubeUrl) {
        String youtubeId = extractYoutubeId(youtubeUrl);
        if (StringUtils.isNullOrEmpty(youtubeId)) {
            return "empty";
        }
        String youtubeThumbUrl = "http://img.youtube.com/vi/" + youtubeId + "/0.jpg";
        return youtubeThumbUrl;
    }

    public static String extractYoutubeId(String url) {
        String query = null;
        try {
            query = new URL(url).getQuery();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return "";
        }
        String[] param = query.split("&");
        String id = null;
        for (String row : param) {
            String[] param1 = row.split("=");
            if (param1[0].equals("v")) {
                id = param1[1];
            }
        }
        return id;
    }

    public static String getYoutubeThumbnailURLMomspresso(String youtubeUrl) {
        String youtubeId = extractYoutubeIdMomspresso(youtubeUrl);
        if (StringUtils.isNullOrEmpty(youtubeId)) {
            return "";
        }
        String youtubeThumbUrl = "http://img.youtube.com/vi/" + youtubeId + "/0.jpg";
        return youtubeThumbUrl;
    }

    public static String extractYoutubeIdMomspresso(String youtubeUrl) {
        String[] param = youtubeUrl.split("/");
        return param[param.length - 1];
    }

    public static String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            reader.close();
        } catch (IOException e) {
            Crashlytics.logException(e);
            Log.d("IOException", Log.getStackTraceString(e));
        }
        return sb.toString();
    }

    public static Topics getSpecificLanguageTopic(Context mContext, String topicId) {
        try {
            FileInputStream fileInputStream = mContext.openFileInput(AppConstants.CATEGORIES_JSON_FILE);
            String fileContent = convertStreamToString(fileInputStream);
            TopicsResponse responseData = new Gson().fromJson(fileContent, TopicsResponse.class);

            for (int i = 0; i < responseData.getData().size(); i++) {
                for (int j = 0; j < responseData.getData().get(i).getChild().size(); j++) {
                    if (topicId.equals(responseData.getData().get(i).getChild().get(j).getId())) {
                        return responseData.getData().get(i).getChild().get(j);
                    }
                }
            }
        } catch (FileNotFoundException fnfe) {

        }
        return null;
    }

    public static boolean createDirIfNotExists(String path) {
        boolean ret = true;

        File file = new File(Environment.getExternalStorageDirectory(), path);
        if (!file.exists()) {
            if (!file.mkdirs()) {
                Log.e("TravellerLog :: ", "Problem creating Image folder");
                ret = false;
            }
        }
        return ret;
    }


    public static String getFileNameFromUri(Context mContext, Uri uri) {
        String result = null;
        if (null != uri.getScheme() && uri.getScheme().equals("content")) {
            Cursor cursor = mContext.getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    /**
     * Converts a file to a content uri, by inserting it into the media store.
     * Requires this permission: <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
     */
    protected static Uri convertFileToContentUri(Context context, File file) throws Exception {

        //Uri localImageUri = Uri.fromFile(localImageFile); // Not suitable as it's not a content Uri

        ContentResolver cr = context.getContentResolver();
        String imagePath = file.getAbsolutePath();
        String imageName = null;
        String imageDescription = null;
        String uriString = MediaStore.Images.Media.insertImage(cr, imagePath, imageName, imageDescription);
        return Uri.parse(uriString);
    }

    public static Uri exportToGallery(String filename, ContentResolver contentResolver, Context mContext) {
        // Save the name and description of a video in a ContentValues map.
        final ContentValues values = new ContentValues(2);
        values.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4");
        values.put(MediaStore.Video.Media.DATA, filename);
        // Add a new record (identified by uri)
        final Uri uri = contentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                values);
        mContext.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                Uri.parse("file://" + filename)));
        return uri;
    }

    public static final Uri getVideoUriFromMediaProvider(String videoFile, ContentResolver contentResolver) {
        String selection = MediaStore.Video.VideoColumns.DATA + "=?";
        String[] selectArgs = {videoFile};
        String[] projection = {MediaStore.Video.VideoColumns._ID};
        Cursor c = null;
        try {
            c = contentResolver.query(android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                    projection, selection, selectArgs, null);
            if (c.getCount() > 0) {
                c.moveToFirst();
                String id = c.getString(c
                        .getColumnIndex(MediaStore.Video.VideoColumns._ID));

                return Uri
                        .withAppendedPath(
                                android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                                id);
            }
            return null;
        } finally {
            if (c != null) {
                c.close();
            }
        }
    }


    public static Uri exportAudioToGallery(String filename, ContentResolver contentResolver, Context mContext) {
        // Save the name and description of a video in a ContentValues map.
        final ContentValues values = new ContentValues(2);
        values.put(MediaStore.Video.Media.MIME_TYPE, "audio/3gp");
        values.put(MediaStore.Video.Media.DATA, filename);
        // Add a new record (identified by uri)
        final Uri uri = contentResolver.insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                values);
        mContext.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                Uri.parse("file://" + filename)));
        return uri;
    }

    public static final Uri getAudioUriFromMediaProvider(String videoFile, ContentResolver contentResolver) {
        String selection = MediaStore.Video.VideoColumns.DATA + "=?";
        String[] selectArgs = {videoFile};
        String[] projection = {MediaStore.Audio.AudioColumns._ID};
        Cursor c = null;
        try {
            c = contentResolver.query(android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    projection, selection, selectArgs, null);
            if (c.getCount() > 0) {
                c.moveToFirst();
                String id = c.getString(c
                        .getColumnIndex(MediaStore.Audio.AudioColumns._ID));

                return Uri
                        .withAppendedPath(
                                android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                                id);
            }
            return null;
        } finally {
            if (c != null) {
                c.close();
            }
        }
    }


    public static void deleteDirectoryContent(String dirName) {
        File dir = new File(Environment.getExternalStorageDirectory() + File.separator + dirName);
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                new File(dir, children[i]).delete();
            }
        }
    }

    public static void deleteFile(String filePath) {
        File dir = new File(Environment.getExternalStorageDirectory() + File.separator + filePath);
        if (dir.exists())
            dir.delete();
    }

    public static String getAppVersion(Context mContext) {
        PackageInfo pInfo = null;
        try {
            pInfo = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return pInfo.versionName;
    }

    public static Map<String, Object> toMap(JSONObject object) throws JSONException {
        Map<String, Object> map = new HashMap<String, Object>();

        Iterator<String> keysItr = object.keys();
        while (keysItr.hasNext()) {
            String key = keysItr.next();
            Object value = object.get(key);

            if (value instanceof JSONArray) {
                value = toList((JSONArray) value);
            } else if (value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            map.put(key, value);
        }
        return map;
    }

    public static List<Object> toList(JSONArray array) throws JSONException {
        List<Object> list = new ArrayList<Object>();
        for (int i = 0; i < array.length(); i++) {
            Object value = array.get(i);
            if (value instanceof JSONArray) {
                value = toList((JSONArray) value);
            } else if (value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            list.add(value);
        }
        return list;
    }

    public static String getDeviceId(Context context) {
        try {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            return telephonyManager.getDeviceId();
        } catch (Exception se) {
            return "";
        }

    }

    public static boolean writeJsonStringToFile(Context context, String jsonString, String fileName) {
        try {
            OutputStream outputStream = null;
            try {
                outputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
                outputStream.write(jsonString.getBytes(Charset.forName("UTF-8")));
                outputStream.flush();
//            output.close();
//            Toast.makeText(getApplicationContext(), "Composition saved", Toast.LENGTH_LONG).show();
                return true;
            } catch (IOException e) {
                return false;
            } finally {
                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException ioe) {
            return false;
        }
    }

    public static boolean writeResponseBodyToDisk(Context mContext, String fileName, ResponseBody body) {
        if (body != null) {
            try {
                InputStream inputStream = null;
                OutputStream outputStream = null;
                try {
                    byte[] fileReader = new byte[4096];
                    long fileSize = body.contentLength();
                    long fileSizeDownloaded = 0;

                    inputStream = body.byteStream();
                    outputStream = mContext.openFileOutput(fileName, Context.MODE_PRIVATE);

                    while (true) {
                        int read = inputStream.read(fileReader);
                        if (read == -1) {
                            break;
                        }
                        outputStream.write(fileReader, 0, read);
                        fileSizeDownloaded += read;
                        Log.d("AppUtils", "file download: " + fileSizeDownloaded + " of " + fileSize);
                    }

                    outputStream.flush();
                    return true;
                } catch (IOException e) {
                    Crashlytics.logException(e);
                    Log.d("MC4KException", Log.getStackTraceString(e));
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
                Crashlytics.logException(e);
                Log.d("MC4KException", Log.getStackTraceString(e));
                return false;
            }
        }
        return false;
    }

    public static LanguageConfigModel getLangModelForLanguage(Context mContext, String key) {
        try {
            FileInputStream fileInputStream = mContext.openFileInput(AppConstants.LANGUAGES_JSON_FILE);
            String fileContent = AppUtils.convertStreamToString(fileInputStream);
//            ConfigResult res = new Gson().fromJson(fileContent, ConfigResult.class);
            LinkedHashMap<String, LanguageConfigModel> retMap = new Gson().fromJson(
                    fileContent, new TypeToken<LinkedHashMap<String, LanguageConfigModel>>() {
                    }.getType()
            );
            return retMap.get(key);
//            return (new ArrayList<LanguageConfigModel>(retMap.values())).get(0).getId();
        } catch (FileNotFoundException ffe) {
            Crashlytics.logException(ffe);
            Log.d("MC4kException", Log.getStackTraceString(ffe));
            return null;
        }
    }

    public static LanguageConfigModel getLangModelFromLanguageKey(Context mContext, String key) {
        try {
            FileInputStream fileInputStream = mContext.openFileInput(AppConstants.LANGUAGES_JSON_FILE);
            String fileContent = AppUtils.convertStreamToString(fileInputStream);
//            ConfigResult res = new Gson().fromJson(fileContent, ConfigResult.class);
            LinkedHashMap<String, LanguageConfigModel> retMap = new Gson().fromJson(
                    fileContent, new TypeToken<LinkedHashMap<String, LanguageConfigModel>>() {
                    }.getType()
            );
            return retMap.get(key);
//            return (new ArrayList<LanguageConfigModel>(retMap.values())).get(0).getId();
        } catch (FileNotFoundException ffe) {
            Crashlytics.logException(ffe);
            Log.d("MC4kException", Log.getStackTraceString(ffe));
            return null;
        }
    }

    public static int pxToDp(int px) {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }

    public static int dpTopx(float dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    @SuppressWarnings("deprecation")
    public static Spanned fromHtml(String html) {
        Spanned result;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            result = Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY);
        } else {
            result = Html.fromHtml(html);
        }
        return result;
    }

    public static String stripHtml(String html) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            return Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY).toString();
        } else {
            return Html.fromHtml(html).toString();
        }
    }


    public static boolean testPrep() {
        Integer[] arr = {1, 2, 3, 4, 2, 6, 7};
        HashSet<Integer> set = new HashSet<>();
        int k = 3;
        for (int i = 0; i < arr.length; i++) {
            if (i - k > 0) {
                set.remove(arr[i - k - 1]);
                if (!set.add(arr[i])) {
                    System.out.println("FOund");
                    return true;
                }
            } else {
                if (!set.add(arr[i])) {
                    System.out.println("Found early");
                    return true;
                }
            }
        }
        System.out.println("Not Found");
        return false;
    }

    public static String getShortStoryShareUrl(String userType, String blogSlug, String titleSlug) {
        String shareUrl = "";
        if (AppConstants.USER_TYPE_BLOGGER.equals(userType) || AppConstants.USER_TYPE_USER.equals(userType)) {
            shareUrl = AppConstants.ARTICLE_SHARE_URL + blogSlug + "/story/" + titleSlug;
        } else if (AppConstants.USER_TYPE_EXPERT.equals(userType)) {
            shareUrl = AppConstants.ARTICLE_SHARE_URL + "story/" + titleSlug;
        } else if (AppConstants.USER_TYPE_EDITOR.equals(userType)) {
            shareUrl = AppConstants.ARTICLE_SHARE_URL + "story/" + titleSlug;
        } else if (AppConstants.USER_TYPE_EDITORIAL.equals(userType)) {
            shareUrl = AppConstants.ARTICLE_SHARE_URL + "story/" + titleSlug;
        } else if (AppConstants.USER_TYPE_FEATURED.equals(userType)) {
            shareUrl = AppConstants.ARTICLE_SHARE_URL + "story/" + titleSlug;
        }
        return shareUrl;
    }

    public static String getShareUrl(String userType, String blogSlug, String titleSlug) {
        String shareUrl = "";
        if (AppConstants.USER_TYPE_BLOGGER.equals(userType)) {
            shareUrl = AppConstants.ARTICLE_SHARE_URL + blogSlug + "/article/" + titleSlug;
        } else if (AppConstants.USER_TYPE_EXPERT.equals(userType)) {
            shareUrl = AppConstants.ARTICLE_SHARE_URL + "article/" + titleSlug;
        } else if (AppConstants.USER_TYPE_EDITOR.equals(userType)) {
            shareUrl = AppConstants.ARTICLE_SHARE_URL + "article/" + titleSlug;
        } else if (AppConstants.USER_TYPE_EDITORIAL.equals(userType)) {
            shareUrl = AppConstants.ARTICLE_SHARE_URL + "article/" + titleSlug;
        } else if (AppConstants.USER_TYPE_FEATURED.equals(userType)) {
            shareUrl = AppConstants.ARTICLE_SHARE_URL + "article/" + titleSlug;
        }
        return shareUrl;
    }

    public static void shareFacebook(Activity activity, String text, String url) {
        boolean facebookAppFound = false;
        Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, url);
        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(url));

        PackageManager pm = activity.getPackageManager();
        List<ResolveInfo> activityList = pm.queryIntentActivities(shareIntent, 0);
        for (final ResolveInfo app : activityList) {
            if ((app.activityInfo.packageName).contains("com.facebook.katana")) {
                final ActivityInfo activityInfo = app.activityInfo;
                final ComponentName name = new ComponentName(activityInfo.applicationInfo.packageName, activityInfo.name);
                shareIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                shareIntent.setComponent(name);
                facebookAppFound = true;
                break;
            }
        }
        if (!facebookAppFound) {
            String sharerUrl = "https://www.facebook.com/sharer/sharer.php?u=" + url;
            shareIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(sharerUrl));
        }
        activity.startActivity(shareIntent);
    }

    public static void changeTabsFont(TabLayout tabLayout) {
        Typeface myTypeface = Typeface.createFromAsset(tabLayout.getContext().getAssets(), "fonts/" + "oswald_regular.ttf");
        ViewGroup vg = (ViewGroup) tabLayout.getChildAt(0);
        int tabsCount = vg.getChildCount();
        for (int j = 0; j < tabsCount; j++) {
            ViewGroup vgTab = (ViewGroup) vg.getChildAt(j);
            int tabChildsCount = vgTab.getChildCount();
            for (int i = 0; i < tabChildsCount; i++) {
                View tabViewChild = vgTab.getChildAt(i);
                if (tabViewChild instanceof TextView) {
                    ((TextView) tabViewChild).setTypeface(myTypeface, Typeface.NORMAL);
                    if (((TextView) tabViewChild).getText().toString().equals("Challenges") || ((TextView) tabViewChild).getText().toString().equals("challenges") || ((TextView) tabViewChild).getText().toString().equals("चैलेंज") || ((TextView) tabViewChild).getText().toString().equals("চ্যালেঞ্জ") || ((TextView) tabViewChild).getText().toString().equals("ചാലഞ്ച്") || ((TextView) tabViewChild).getText().toString().equals("चॅलेंज") || ((TextView) tabViewChild).getText().toString().equals("சவால்கள்") || ((TextView) tabViewChild).getText().toString().equals("ఛాలెంజ్") || ((TextView) tabViewChild).getText().toString().equals("ಸವಾಲು") || ((TextView) tabViewChild).getText().toString().equals("પડકારો") || ((TextView) tabViewChild).getText().toString().equals("ਚੈਲੇੰਜਸ")) {
                        Drawable drawable = tabLayout.getContext().getResources().getDrawable(R.drawable.ic_winner_tablayout_icon);
                        drawable.setTint(ContextCompat.getColor(tabLayout.getContext(), R.color.topic_articles_tabbar_text));
                        ((TextView) tabViewChild).setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
                        ((TextView) tabViewChild).setCompoundDrawablePadding(15);
                    }
                }
            }
        }
    }

    public static String withSuffix(long count) {
        if (count < 1000) return "" + count;
        int exp = (int) (Math.log(count) / Math.log(1000));
        return String.format("%.1f %c",
                count / Math.pow(1000, exp),
                "kMGTPE".charAt(exp - 1));
    }

    public static ArrayList<ArticleListingResult> getFilteredContentList(ArrayList<ArticleListingResult> originalList, String contentType) {
        ArrayList<ArticleListingResult> filteredList = new ArrayList<>();
        for (int i = 0; i < originalList.size(); i++) {
            if (contentType.equals(originalList.get(i).getContentType())) {
                filteredList.add(originalList.get(i));
            }
        }
        return filteredList;
    }

    public static int getFilteredPosition(int position, ArrayList<ArticleListingResult> filteredList, String contentType) {
        int effectivePosition = 0;
        for (int i = 0; i < position; i++) {
            if (contentType.equals(filteredList.get(i).getContentType())) {
                effectivePosition++;
            }
        }
        return effectivePosition;
    }

    public static Bitmap drawMultilineTextToBitmap(String title, String body, String authorName, boolean isRequiredForUpload) {
        Random rand = new Random();
        switch (rand.nextInt(6)) {
            case 0:
                return drawMultilineTextToBitmap(R.color.short_story_card_bg_1, title, body, authorName, isRequiredForUpload);
            case 1:
                return drawMultilineTextToBitmap(R.color.short_story_card_bg_2, title, body, authorName, isRequiredForUpload);
            case 2:
                return drawMultilineTextToBitmap(R.color.short_story_card_bg_3, title, body, authorName, isRequiredForUpload);
            case 3:
                return drawMultilineTextToBitmap(R.color.short_story_card_bg_4, title, body, authorName, isRequiredForUpload);
            case 4:
                return drawMultilineTextToBitmap(R.color.short_story_card_bg_5, title, body, authorName, isRequiredForUpload);
            case 5:
                return drawMultilineTextToBitmap(R.color.short_story_card_bg_6, title, body, authorName, isRequiredForUpload);
            default:
                return drawMultilineTextToBitmap(R.color.short_story_card_bg_6, title, body, authorName, isRequiredForUpload);
        }
    }

    private static Bitmap drawMultilineTextToBitmap(int bgColor, String title, String body, String authorName, boolean isRequiredForUpload) {
        if (isRequiredForUpload) {
            return drawMultilineTextToBitmapForUpload(bgColor, title, body, authorName);
        } else {
            return drawMultilineTextToBitmap(bgColor, title, body, authorName);
        }
    }

    private static Bitmap drawMultilineTextToBitmapForUpload(int bgColor, String title, String body, String authorName) {
        Bitmap bitmap;
        String author = " - " + authorName;
        Resources resources = BaseApplication.getAppContext().getResources();
        float scale = resources.getDisplayMetrics().density;
        // set text width to canvas width minus 50dp padding
        int textWidth = SS_FB_CARD_WIDTH - (int) (50 * scale);

        int titleSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                14, resources.getDisplayMetrics());

        int bodyAndAuthorSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                12, resources.getDisplayMetrics());

        Typeface georgiaTypeface = Typeface.createFromAsset(BaseApplication.getAppContext().getAssets(), "fonts/georgia.ttf");
        Typeface geoBoldTypeface = Typeface.createFromAsset(BaseApplication.getAppContext().getAssets(), "fonts/georgia_bold.ttf");
        TextPaint titlePaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        titlePaint.setTypeface(geoBoldTypeface);
        titlePaint.setColor(Color.rgb(61, 61, 61));
        titlePaint.setTextSize(titleSize);

        TextPaint bodyPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        bodyPaint.setTypeface(georgiaTypeface);
        bodyPaint.setColor(Color.rgb(61, 61, 61));
        bodyPaint.setTextSize(bodyAndAuthorSize);
        CharSequence txt = TextUtils.ellipsize(body, bodyPaint, textWidth, TextUtils.TruncateAt.END);

        TextPaint authorPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        authorPaint.setTypeface(geoBoldTypeface);
        authorPaint.setColor(Color.rgb(61, 61, 61));
        authorPaint.setTextSize(bodyAndAuthorSize);

        StaticLayout bodyLayout, titleLayout, authorLayout;
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StaticLayout.Builder bodyStaticBuilder = StaticLayout.Builder.obtain(body, 0, body.length(), bodyPaint, textWidth)
                    .setAlignment(Layout.Alignment.ALIGN_CENTER)
                    .setIncludePad(false);
            StaticLayout.Builder titleStaticBuilder = StaticLayout.Builder.obtain(title, 0, title.length(), titlePaint, textWidth)
                    .setAlignment(Layout.Alignment.ALIGN_CENTER)
                    .setIncludePad(false);
            StaticLayout.Builder authorStaticBuilder = StaticLayout.Builder.obtain(author, 0, author.length(), authorPaint, textWidth)
                    .setAlignment(Layout.Alignment.ALIGN_CENTER)
                    .setIncludePad(false);
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                bodyStaticBuilder.setJustificationMode(Layout.JUSTIFICATION_MODE_NONE);
                titleStaticBuilder.setJustificationMode(Layout.JUSTIFICATION_MODE_NONE);
                authorStaticBuilder.setJustificationMode(Layout.JUSTIFICATION_MODE_NONE);
            }
            bodyLayout = bodyStaticBuilder.build();
            titleLayout = titleStaticBuilder.build();
            authorLayout = authorStaticBuilder.build();
        } else {
            bodyLayout = new StaticLayout(
                    body, bodyPaint, textWidth, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
            titleLayout = new StaticLayout(
                    title, titlePaint, textWidth, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
            authorLayout = new StaticLayout(
                    author, authorPaint, textWidth, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
        }
        // get height of multiline text
        int bodyHeight = bodyLayout.getHeight();
        int titleHeight = titleLayout.getHeight();

        float currentContentHeight = bodyHeight + titleHeight + authorLayout.getHeight()
                + (50 * scale) //50*scale for spacing between title, body, author and logo
                + 30; //For Padding at Top

        if (currentContentHeight >= SS_FB_CARD_HEIGHT) {
            bitmap = Bitmap.createBitmap(SS_FB_CARD_WIDTH, SS_FB_CARD_HEIGHT_WITHOUT_AUTHOR, Bitmap.Config.ARGB_8888);
        } else {
            bitmap = Bitmap.createBitmap(SS_FB_CARD_WIDTH, SS_FB_CARD_HEIGHT, Bitmap.Config.ARGB_8888);
        }

        Bitmap logoBitmap = BitmapFactory.decodeResource(resources, R.drawable.icon_notify);
        Bitmap watermark = BitmapFactory.decodeResource(resources, R.drawable.share_bg);
        android.graphics.Bitmap.Config bitmapConfig = bitmap.getConfig();
        // set default bitmap config if none
        if (bitmapConfig == null) {
            bitmapConfig = android.graphics.Bitmap.Config.ARGB_8888;
        }
        // resource bitmaps are immutable, so we need to convert it to mutable one
        bitmap = bitmap.copy(bitmapConfig, true);

        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(ContextCompat.getColor(BaseApplication.getAppContext(), bgColor));

        float xPosWatermark = (bitmap.getWidth() - watermark.getWidth()) / 2.0f;
        float yPosWatermark = (bitmap.getHeight() - watermark.getHeight()) / 2.0f;
        canvas.drawBitmap(watermark, xPosWatermark, yPosWatermark, null);

        // get X Coordinate of body's top left corner
        float xBodyInitial = (bitmap.getWidth() - textWidth) / 2.0f;

        Paint p = new Paint();
        p.setColor(ContextCompat.getColor(BaseApplication.getAppContext(), R.color.short_story_light_black_color));
        p.setStrokeWidth(2);

        if (currentContentHeight > SS_FB_CARD_HEIGHT) {
            Log.d("drawMultilineText", "LONG CARD === " + currentContentHeight);
            /*Text overflow .. starting drawing from Top instead of center
            get Y Coordinate of body's top left corner*/
            float yBodyInitial = titleHeight + 30 * scale;
            float ySeparator = yBodyInitial - 10 * scale;

            canvas.save();
            canvas.translate(xBodyInitial, yBodyInitial);
            bodyLayout.draw(canvas);
            canvas.restore();
            canvas.save();
            canvas.drawLine(bitmap.getWidth() / 2.0f - 20, ySeparator, bitmap.getWidth() / 2.0f + 20, ySeparator, p);
            canvas.translate(xBodyInitial, ySeparator - titleHeight - 10 * scale); //subtract 10*scale for spacing between title and body
            titleLayout.draw(canvas);
            canvas.restore();
            bitmap = authorSectionBitmap(bitmap, xBodyInitial, authorLayout, bgColor);
        } else {
            Log.d("drawMultilineText", "SHORT CARD === " + currentContentHeight);

            // get Y Coordinate of body's top left corner
            float yBodyInitial = (bitmap.getHeight() - bodyHeight) / 2.0f;
            float ySeparator = yBodyInitial - 10 * scale;
            float yAuthor = bitmap.getHeight() - yBodyInitial;

            // draw text to the Canvas center
            canvas.save();
            canvas.translate(xBodyInitial, yBodyInitial);
            bodyLayout.draw(canvas);
            canvas.restore();
            canvas.save();
            canvas.drawLine(bitmap.getWidth() / 2.0f - 20, ySeparator, bitmap.getWidth() / 2.0f + 20, ySeparator, p);
            canvas.translate(xBodyInitial, ySeparator - titleHeight - 10 * scale); //subtract 10*scale for spacing between title and body
            titleLayout.draw(canvas);
            canvas.restore();
            canvas.save();
            canvas.translate(xBodyInitial, yAuthor + 10 * scale);//Add 10*scale for spacing between author and body
            authorLayout.draw(canvas);
            canvas.restore();
            canvas.save();
            canvas.drawBitmap(logoBitmap, bitmap.getWidth() - 30 * scale, bitmap.getHeight() - 30 * scale, null);
        }
        AppUtils.createDirIfNotExists("MyCity4Kids/videos");
        try {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 95, new FileOutputStream(Environment.getExternalStorageDirectory() + "/MyCity4Kids/videos/image.jpg"));
        } catch (FileNotFoundException e) {
            Crashlytics.logException(e);
            Log.d("MC4kException", Log.getStackTraceString(e));
        }
        return bitmap;
    }

    private static Bitmap authorSectionBitmap(Bitmap bitmap, float xInitial, StaticLayout authorLayout, int bgColor) {
        Resources resources = BaseApplication.getAppContext().getResources();
        Bitmap logoBitmap = BitmapFactory.decodeResource(resources, R.drawable.icon_notify);
        float scale = resources.getDisplayMetrics().density;
        int height = authorLayout.getHeight() + logoBitmap.getHeight();
        Log.d("drawMultilineText", "Author Section = " + height + "  bitmap height = " + bitmap.getHeight());
        Bitmap cs = Bitmap.createBitmap(bitmap.getWidth(), height + bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas comboImage = new Canvas(cs);
        comboImage.drawColor(ContextCompat.getColor(BaseApplication.getAppContext(), bgColor));
        comboImage.drawBitmap(bitmap, 0, 0, null);
        comboImage.save();
        comboImage.translate(xInitial, bitmap.getHeight() + 10 * scale);
        authorLayout.draw(comboImage);
        comboImage.restore();
        comboImage.save();
        comboImage.drawBitmap(logoBitmap, comboImage.getWidth() - 30 * scale, comboImage.getHeight() - 30 * scale, null);
        return cs;
    }

    public static Bitmap drawMultilineTextToBitmap(int bgColor, String title, String body, String authorName) {
        Resources resources = BaseApplication.getAppContext().getResources();
        float scale = resources.getDisplayMetrics().density;
        Bitmap bitmap = Bitmap.createBitmap(800, 800, Bitmap.Config.ARGB_8888);
        Bitmap logoBitmap = BitmapFactory.decodeResource(resources, R.drawable.icon_notify);
        Bitmap watermark = BitmapFactory.decodeResource(resources, R.drawable.share_bg);
        android.graphics.Bitmap.Config bitmapConfig = bitmap.getConfig();
        // set default bitmap config if none
        if (bitmapConfig == null) {
            bitmapConfig = android.graphics.Bitmap.Config.ARGB_8888;
        }
        // resource bitmaps are immutable,
        // so we need to convert it to mutable one
        bitmap = bitmap.copy(bitmapConfig, true);

        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(ContextCompat.getColor(BaseApplication.getAppContext(), bgColor));

        int titleSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                14, resources.getDisplayMetrics());

        int bodyAndAuthorSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                12, resources.getDisplayMetrics());

        Typeface georgiaTypeface = Typeface.createFromAsset(BaseApplication.getAppContext().getAssets(), "fonts/georgia.ttf");
        Typeface geoBoldTypeface = Typeface.createFromAsset(BaseApplication.getAppContext().getAssets(), "fonts/georgia_bold.ttf");

        TextPaint titlePaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        titlePaint.setTypeface(geoBoldTypeface);
        titlePaint.setColor(Color.rgb(61, 61, 61));
        titlePaint.setTextSize(titleSize);

        TextPaint bodyPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        bodyPaint.setTypeface(georgiaTypeface);
        bodyPaint.setColor(Color.rgb(61, 61, 61));
        bodyPaint.setTextSize(bodyAndAuthorSize);

        TextPaint authorPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        authorPaint.setTypeface(geoBoldTypeface);
        authorPaint.setColor(Color.rgb(61, 61, 61));
        authorPaint.setTextSize(bodyAndAuthorSize);
        // set text width to canvas width minus 40dp padding
        int textWidth = canvas.getWidth() - (int) (36 * scale);

        String author = " - " + authorName;

        StaticLayout bodyLayout, titleLayout, authorLayout;
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StaticLayout.Builder bodyStaticBuilder = StaticLayout.Builder.obtain(body, 0, body.length(), bodyPaint, textWidth)
                    .setAlignment(Layout.Alignment.ALIGN_CENTER)
                    .setIncludePad(false);
            StaticLayout.Builder titleStaticBuilder = StaticLayout.Builder.obtain(title, 0, title.length(), titlePaint, textWidth)
                    .setAlignment(Layout.Alignment.ALIGN_CENTER)
                    .setIncludePad(false);
            StaticLayout.Builder authorStaticBuilder = StaticLayout.Builder.obtain(author, 0, author.length(), authorPaint, textWidth)
                    .setAlignment(Layout.Alignment.ALIGN_CENTER)
                    .setIncludePad(false);
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                bodyStaticBuilder.setJustificationMode(Layout.JUSTIFICATION_MODE_INTER_WORD);
                titleStaticBuilder.setJustificationMode(Layout.JUSTIFICATION_MODE_INTER_WORD);
                authorStaticBuilder.setJustificationMode(Layout.JUSTIFICATION_MODE_INTER_WORD);
            }
            bodyLayout = bodyStaticBuilder.build();
            titleLayout = titleStaticBuilder.build();
            authorLayout = authorStaticBuilder.build();
        } else {
            bodyLayout = new StaticLayout(
                    body, bodyPaint, textWidth, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
            titleLayout = new StaticLayout(
                    title, titlePaint, textWidth, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
            authorLayout = new StaticLayout(
                    author, authorPaint, textWidth, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
        }

        // get height of multiline text
        int bodyHeight = bodyLayout.getHeight();
        int titleHeight = titleLayout.getHeight();

        float currentContentHeight = bodyHeight + titleHeight + authorLayout.getHeight()
                + (50 * scale) //50*scale for spacing between title, body, author and logo
                + 30; //For Padding at Top
        if (currentContentHeight > singleContentHeight) {
            bitmap = extendBitmap(bitmap, currentContentHeight - singleContentHeight, bgColor);
            canvas = new Canvas(bitmap);
        }

        float xPosWatermark = (bitmap.getWidth() - watermark.getWidth()) / 2.0f;
        float yPosWatermark = (bitmap.getHeight() - watermark.getHeight()) / 2.0f;
        canvas.drawBitmap(watermark, xPosWatermark, yPosWatermark, null);

        // get position of text's top left corner
        float xBodyInitial = (bitmap.getWidth() - textWidth) / 2.0f;
        float yBodyInitial = (bitmap.getHeight() - bodyHeight) / 2.0f;

        float ySeparator = yBodyInitial - 10 * scale;
        float yAuthor = bitmap.getHeight() - yBodyInitial;

        Paint p = new Paint();
        p.setColor(ContextCompat.getColor(BaseApplication.getAppContext(), R.color.short_story_light_black_color));
        p.setStrokeWidth(2);
        // draw text to the Canvas center
        canvas.save();
        canvas.translate(xBodyInitial, yBodyInitial);
        bodyLayout.draw(canvas);
        canvas.restore();
        canvas.save();
        canvas.drawLine(bitmap.getWidth() / 2.0f - 20, ySeparator, bitmap.getWidth() / 2.0f + 20, ySeparator, p);
        canvas.translate(xBodyInitial, ySeparator - titleHeight - 10 * scale); //subtract 10*scale for spacing between title and body
        titleLayout.draw(canvas);
        canvas.restore();
        canvas.save();
        canvas.translate(xBodyInitial, yAuthor + 10 * scale);//Add 10*scale for spacing between author and body
        authorLayout.draw(canvas);
        canvas.restore();
        canvas.save();
        canvas.drawBitmap(logoBitmap, bitmap.getWidth() - 30 * scale, bitmap.getHeight() - 30 * scale, null);

        AppUtils.createDirIfNotExists("MyCity4Kids/videos");
        try {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 95, new FileOutputStream(Environment.getExternalStorageDirectory() + "/MyCity4Kids/videos/image.jpg"));
        } catch (FileNotFoundException e) {
            Crashlytics.logException(e);
            Log.d("MC4kException", Log.getStackTraceString(e));
        }
        return bitmap;
    }

    private static Bitmap extendBitmap(Bitmap bitmap, float v, int bgColor) {
        int height = (int) v;
        int width = bitmap.getWidth();
        Log.d("----WEDDINGCARD----", "extendBitmap=" + v);
        Bitmap cs = Bitmap.createBitmap(width, height + bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas comboImage = new Canvas(cs);
        comboImage.drawBitmap(bitmap, 0, bitmap.getHeight(), null);
        comboImage.drawColor(ContextCompat.getColor(BaseApplication.getAppContext(), bgColor));
        return cs;
    }

    public static Bitmap combineImages(Bitmap c, int s) { // can add a 3rd parameter 'String loc' if you want to save the new image - left some code to do that at the bottom
        Bitmap cs;
        int width, height;
        height = s * c.getHeight();
        width = c.getWidth();

        cs = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas comboImage = new Canvas(cs);

        for (int i = 0; i < s; i++) {
            Log.d("----WEDDINGCARD----", "combineImages=" + s);
            comboImage.drawBitmap(c, 0f, i * c.getHeight(), null);
        }
        return cs;
    }

    public static void shareStoryWithWhatsApp(Context mContext, String userType, String blogSlug, String titleSlug,
                                              String screenName, String userDynamoId, String articleId, String authorId, String authorName) {
        String shareUrl = AppUtils.getShortStoryShareUrl(userType, blogSlug, titleSlug);
        shareStoryWithWhatsApp(mContext, shareUrl, screenName, userDynamoId, articleId, authorId, authorName);
    }

    public static void shareStoryWithWhatsApp(Context mContext, String shareUrl, String screenName, String userDynamoId, String articleId, String authorId, String authorName) {
        if (StringUtils.isNullOrEmpty(shareUrl)) {
            Toast.makeText(mContext, mContext.getString(R.string.moderation_or_share_whatsapp_fail), Toast.LENGTH_SHORT).show();
        } else {
            Uri uri = Uri.parse("file://" + Environment.getExternalStorageDirectory() + "/MyCity4Kids/videos/image.jpg");
            Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
            whatsappIntent.putExtra(Intent.EXTRA_STREAM, uri);
            whatsappIntent.putExtra(Intent.EXTRA_TEXT, shareUrl);
            whatsappIntent.setPackage("com.whatsapp");
            whatsappIntent.setType("image/*");
            try {
                mContext.startActivity(Intent.createChooser(whatsappIntent, "Share image via:"));
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(mContext, mContext.getString(R.string.moderation_or_share_whatsapp_not_installed), Toast.LENGTH_SHORT).show();
                return;
            }
            Utils.pushShareStoryEvent(mContext, screenName, userDynamoId + "", articleId, authorId + "~" + authorName, "Whatsapp");
        }
    }

    public static void shareCampaignWithWhatsApp(Context mContext, String shareUrl, String screenName, String userDynamoId, String articleId, String authorId, String authorName) {
        if (StringUtils.isNullOrEmpty(shareUrl)) {
            Toast.makeText(mContext, mContext.getString(R.string.moderation_or_share_whatsapp_fail), Toast.LENGTH_SHORT).show();
        } else {
            Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
            whatsappIntent.putExtra(Intent.EXTRA_TEXT, shareUrl);
            whatsappIntent.setPackage("com.whatsapp");
            whatsappIntent.setType("text/plain");
            try {
                mContext.startActivity(Intent.createChooser(whatsappIntent, "Share Url:"));
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(mContext, mContext.getString(R.string.moderation_or_share_whatsapp_not_installed), Toast.LENGTH_SHORT).show();
                return;
            }
            Utils.pushShareStoryEvent(mContext, screenName, userDynamoId + "", articleId, authorId + "~" + authorName, "Whatsapp");
        }
    }

    public static void shareStoryWithInstagram(Context mContext, String screenName, String userDynamoId, String articleId, String authorId, String authorName) {
        Uri uri = Uri.parse("file://" + Environment.getExternalStorageDirectory() + "/MyCity4Kids/videos/image.jpg");
        Intent instaIntent = new Intent(Intent.ACTION_SEND);
        instaIntent.putExtra(Intent.EXTRA_STREAM, uri);
        instaIntent.setType("image/*");
        instaIntent.setPackage("com.instagram.android");
        try {
            mContext.startActivity(Intent.createChooser(instaIntent, "Share image via:"));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(mContext, mContext.getString(R.string.moderation_or_share_insta_not_installed), Toast.LENGTH_SHORT).show();
            return;
        }
        Utils.pushShareStoryEvent(mContext, screenName, userDynamoId + "", articleId, authorId + "~" + authorName, "Instagram");
    }


    public static void shareStoryWithFB(BaseFragment topicsShortStoriesTabFragment, String userType, String blogSlug, String titleSlug,
                                        String screenName, String userDynamoId, String articleId, String authorId, String authorName) {
        String shareUrl = AppUtils.getShortStoryShareUrl(userType, blogSlug, titleSlug);

        if (ShareDialog.canShow(ShareLinkContent.class)) {
            ShareLinkContent content = new ShareLinkContent.Builder()
                    .setContentUrl(Uri.parse(shareUrl))
                    .build();
            new ShareDialog(topicsShortStoriesTabFragment).show(content);
        }
        Utils.pushShareStoryEvent(topicsShortStoriesTabFragment.getContext(), screenName, userDynamoId + "", articleId, authorId + "~" + authorName, "Facebook");
    }

    public static void shareWithFBForCampaign(BaseFragment campaignCongratulationFragment, String shareUrl) {
        if (ShareDialog.canShow(ShareLinkContent.class)) {
            ShareLinkContent content = new ShareLinkContent.Builder()
                    .setContentUrl(Uri.parse(shareUrl))
                    .build();
            new ShareDialog(campaignCongratulationFragment).show(content);
        }
    }

    public static void shareStoryWithFBC(BaseFragment topicsChallengeTabFragment, String userType, String blogSlug, String titleSlug,
                                         String screenName, String userDynamoId, String articleId, String authorId, String authorName) {
        String shareUrl = AppUtils.getShortStoryShareUrl(userType, blogSlug, titleSlug);

        if (ShareDialog.canShow(ShareLinkContent.class)) {
            ShareLinkContent content = new ShareLinkContent.Builder()
                    .setContentUrl(Uri.parse(shareUrl))
                    .build();
            new ShareDialog(topicsChallengeTabFragment).show(content);
        }
        Utils.pushShareStoryEvent(topicsChallengeTabFragment.getContext(), screenName, userDynamoId + "", articleId, authorId + "~" + authorName, "Facebook");
    }

    public static void shareStoryWithFB(Activity ChallnegeDetailListingActivity, Context mContext, String userType, String blogSlug, String titleSlug,
                                        String screenName, String userDynamoId, String articleId, String authorId, String authorName) {
        String shareUrl = AppUtils.getShortStoryShareUrl(userType, blogSlug, titleSlug);

        if (ShareDialog.canShow(ShareLinkContent.class)) {
            ShareLinkContent content = new ShareLinkContent.Builder()
                    .setContentUrl(Uri.parse(shareUrl))
                    .build();
            new ShareDialog(ChallnegeDetailListingActivity).show(content);
        }
        Utils.pushShareStoryEvent(mContext, screenName, userDynamoId + "", articleId, authorId + "~" + authorName, "Facebook");
    }

    public static void shareStoryGeneric(Context mContext, String userType, String blogSlug, String titleSlug,
                                         String screenName, String userDynamoId, String articleId, String authorId, String authorName) {
        String shareUrl = AppUtils.getShortStoryShareUrl(userType, blogSlug, titleSlug);

        Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
        shareIntent.setType("text/plain");

        if (StringUtils.isNullOrEmpty(shareUrl)) {

        } else {
            shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareUrl);
            mContext.startActivity(Intent.createChooser(shareIntent, "Momspresso"));
            Utils.pushShareStoryEvent(mContext, screenName, userDynamoId + "", articleId,
                    authorId + "~" + authorName, "Generic");
        }
    }

    public static float round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
    }

    public static long getIdFromHash(String hash) {
        Hashids hashids = new Hashids(SALT);
        long[] id = hashids.decode(hash);
        if (id == null || id.length == 0) {
            return -1;
        }
        return id[0];
    }

    public static String extractYoutubeIdForMomspresso(String videoUrl) {
        String[] separated = videoUrl.split("/");
        return separated[separated.length - 1];
    }

    public static String calculateFormattedTimeLimit(long seconds) {
        long minute = TimeUnit.SECONDS.toMinutes(seconds) - (TimeUnit.SECONDS.toHours(seconds) * 60);
        return minute + " ";
//        long second = TimeUnit.SECONDS.toSeconds(seconds) - (TimeUnit.SECONDS.toMinutes(seconds) * 60);
//        if (minute == 0) {
//            return second + " " + BaseApplication.getAppContext().getString(R.string.seconds_label);
//        } else if (second == 0) {
//            return minute + " " + BaseApplication.getAppContext().getString(R.string.minutes_label);
//        } else {
//            return minute + " " + BaseApplication.getAppContext().getString(R.string.minutes_label) + " " + second + " " + BaseApplication.getAppContext().getString(R.string.seconds_label);
//        }
    }

    public static String getString(Activity activity, int stringId) {
        if (activity == null) {
            return BaseApplication.getAppContext().getString(stringId);
        } else {
            return activity.getString(stringId);
        }
    }

    public static String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }

    public static String convertTimestampToDate(Long timestamp) {
        if (timestamp != null) {
            return new SimpleDateFormat("dd-MM-yyyy").format(new Date(timestamp * 1000));
        } else {
            return "";
        }

    }

    public static void CopyStream(InputStream is, OutputStream os) {
        final int buffer_size = 1024;
        try {
            byte[] bytes = new byte[buffer_size];
            for (; ; ) {
                int count = is.read(bytes, 0, buffer_size);
                if (count == -1)
                    break;
                os.write(bytes, 0, count);
            }
        } catch (Exception ex) {
        }
    }

    public static String streamToString(InputStream is) throws IOException {
        String str = "";

        if (is != null) {
            StringBuilder sb = new StringBuilder();
            String line;

            try {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(is));

                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }

                reader.close();
            } finally {
                is.close();
            }

            str = sb.toString();
        }

        return str;
    }
}
