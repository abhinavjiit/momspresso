package com.mycity4kids.utils;

import static android.content.Context.CLIPBOARD_SERVICE;


import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.Signature;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayout.TabView;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.response.ArticleListingResult;
import com.mycity4kids.models.response.ContributorListResult;
import com.mycity4kids.models.response.MixFeedResult;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.tagging.Mentions;
import com.mycity4kids.tagging.mentions.MentionSpan;
import com.mycity4kids.ui.activity.DeeplinkActivity;
import com.mycity4kids.widget.Hashids;
import com.squareup.picasso.Picasso;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import okhttp3.ResponseBody;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

    public static void printHashKey(Context context) {
        try {
            PackageInfo info = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String hashKey = new String(Base64.encode(md.digest(), 0));
                Log.i("FB Hash", "printHashKey() Hash Key: " + hashKey);
            }
        } catch (Exception e) {
            Log.e("FB Hash", "printHashKey()", e);
        }
    }

    public static String getYoutubeThumbnailURLMomspresso(String youtubeUrl) {
        String youtubeId = extractYoutubeIdMomspresso(youtubeUrl);
        if (StringUtils.isNullOrEmpty(youtubeId)) {
            return "";
        }
        return "http://img.youtube.com/vi/" + youtubeId + "/0.jpg";
    }

    private static String extractYoutubeIdMomspresso(String youtubeUrl) {
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
            FirebaseCrashlytics.getInstance().recordException(e);
            Log.d("IOException", Log.getStackTraceString(e));
        }
        return sb.toString();
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


    public static String getFileNameFromUri(Context context, Uri uri) {
        String result = null;
        if (null != uri.getScheme() && uri.getScheme().equals("content")) {
            Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
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

    public static void deleteDirectoryContent() {
        File dir = BaseApplication.getAppContext().getExternalFilesDir(null);
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            if (children != null) {
                for (String child : children) {
                    new File(dir, child).delete();
                }
            }
        }
    }

    public static String getAppVersion(Context context) {
        String appVersion = "";
        PackageInfo packageInfo = null;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            appVersion = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return appVersion;
    }

    private static Map<String, Object> toMap(JSONObject object) throws JSONException {
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

    private static List<Object> toList(JSONArray array) throws JSONException {
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
            TelephonyManager telephonyManager = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);
            return telephonyManager.getDeviceId();
        } catch (Exception se) {
            return "";
        }
    }

    public static String getUniqueIdentifier(Context context) {
        return Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
    }

    public static boolean writeJsonStringToFile(Context context, String jsonString, String fileName) {
        try {
            OutputStream outputStream = null;
            try {
                outputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
                outputStream.write(jsonString.getBytes(Charset.forName("UTF-8")));
                outputStream.flush();
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

    public static boolean writeResponseBodyToDisk(Context context, String fileName,
            ResponseBody body) {
        if (body != null) {
            try {
                InputStream inputStream = null;
                OutputStream outputStream = null;
                try {
                    byte[] fileReader = new byte[4096];
                    inputStream = body.byteStream();
                    outputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
                    while (true) {
                        int read = inputStream.read(fileReader);
                        if (read == -1) {
                            break;
                        }
                        outputStream.write(fileReader, 0, read);
                    }
                    outputStream.flush();
                    return true;
                } catch (IOException e) {
                    FirebaseCrashlytics.getInstance().recordException(e);
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
                FirebaseCrashlytics.getInstance().recordException(e);
                Log.d("MC4KException", Log.getStackTraceString(e));
                return false;
            }
        }
        return false;
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
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                result = Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY);
            } else {
                result = Html.fromHtml(html);
            }
        } catch (Exception e) {
            result = new SpannableString("");
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

    public static String getShortStoryShareUrl(String userType, String blogSlug, String titleSlug) {
        String shareUrl = "";
        if (AppConstants.USER_TYPE_BLOGGER.equals(userType) || AppConstants.USER_TYPE_USER
                .equals(userType)) {
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

    public static Intent getArticleShareIntent(String userType, String blogSlug, String titleSlug,
            String shareMsg, String title, String userName, String utmSource, String utmMedium) {
        String shareUrl = getUtmParamsAppendedShareUrl(getShareUrl(userType, blogSlug, titleSlug), utmSource,
                utmMedium);
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        String shareData;
        if (StringUtils.isNullOrEmpty(shareUrl)) {
            shareData = shareMsg + "\"" + title + "\" by " + userName + ".";
        } else {
            shareData = shareMsg + "\"" + title + "\" by " + userName + ".\nRead Here: " + shareUrl;
        }
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareData);
        return shareIntent;
    }

    public static Intent getVlogsShareIntent(String userType, String blogSlug, String titleSlug,
            String shareMsg, String title, String userName, String utmSource, String utmMedium) {
        String shareUrl = getUtmParamsAppendedShareUrl(getVlogsShareUrl(userType, blogSlug, titleSlug), utmSource,
                utmMedium);
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        String shareData;
        if (StringUtils.isNullOrEmpty(shareUrl)) {
            shareData = shareMsg + "\"" + title + "\" by " + userName + ".";
        } else {
            shareData = shareMsg + "\"" + title + "\" by " + userName + ".\nWatch Here: " + shareUrl;
        }
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareData);
        return shareIntent;
    }

    public static String getVlogsShareUrl(String userType, String blogSlug, String titleSlug) {
        String shareUrl = null;
        switch (userType) {
            case AppConstants.USER_TYPE_BLOGGER: {
                if (StringUtils.isNullOrEmpty(blogSlug)) {
                    shareUrl =
                            AppConstants.VIDEO_ARTICLE_SHARE_URL + "video/" + titleSlug;
                } else {
                    shareUrl = AppConstants.VIDEO_ARTICLE_SHARE_URL + blogSlug + "/video/" + titleSlug;
                }
            }
            break;
            case AppConstants.USER_TYPE_EXPERT:
            case AppConstants.USER_TYPE_EDITOR:
            case AppConstants.USER_TYPE_EDITORIAL:
            case AppConstants.USER_TYPE_FEATURED:
                shareUrl = AppConstants.VIDEO_ARTICLE_SHARE_URL + "video/" + titleSlug;
                break;
            case AppConstants.USER_TYPE_USER:
                if (StringUtils.isNullOrEmpty(blogSlug)) {
                    shareUrl =
                            AppConstants.VIDEO_ARTICLE_SHARE_URL + "video/" + titleSlug;
                } else {
                    shareUrl = AppConstants.VIDEO_ARTICLE_SHARE_URL + blogSlug + "/video/" + titleSlug;
                }
                break;
            default:
                break;
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
                final ComponentName name = new ComponentName(activityInfo.applicationInfo.packageName,
                        activityInfo.name);
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
        Typeface myTypeface = Typeface
                .createFromAsset(tabLayout.getContext().getAssets(), "fonts/" + "oswald_regular.ttf");
        ViewGroup vg = (ViewGroup) tabLayout.getChildAt(0);
        int tabsCount = vg.getChildCount();
        for (int j = 0; j < tabsCount; j++) {
            ViewGroup vgTab = (ViewGroup) vg.getChildAt(j);
            int tabChildsCount = vgTab.getChildCount();
            for (int i = 0; i < tabChildsCount; i++) {
                View tabViewChild = vgTab.getChildAt(i);
                if (tabViewChild instanceof TextView) {
                    ((TextView) tabViewChild).setTypeface(myTypeface, Typeface.NORMAL);
                }
            }
        }
    }

    public static void changeTabsFontInMomVlog(TabLayout tabLayout) {
        try {
            Typeface myTypeface = Typeface
                    .createFromAsset(tabLayout.getContext().getAssets(), "fonts/" + "Roboto-Bold.ttf");
            ViewGroup vg = (ViewGroup) tabLayout.getChildAt(0);
            int tabsCount = vg.getChildCount();
            for (int j = 0; j < tabsCount; j++) {
                ViewGroup vgTab = (ViewGroup) vg.getChildAt(j);
                int tabChildsCount = vgTab.getChildCount();
                for (int i = 0; i < tabChildsCount; i++) {
                    View tabViewChild = vgTab.getChildAt(i);
                    if (tabViewChild instanceof TextView) {
                        ((TextView) tabViewChild).setTypeface(myTypeface, Typeface.NORMAL);
                        TabLayout.Tab tab = ((TabView) tabViewChild.getParent()).getTab();
                        if (tab != null && tab.getTag() != null && AppConstants.VIDEO_CHALLENGE_ID
                                .equals(tab.getTag().toString())) {
                            Drawable drawable = tabLayout.getContext().getResources()
                                    .getDrawable(R.drawable.ic_winner_tablayout_icon);
                            ((TextView) tabViewChild)
                                    .setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
                            ((TextView) tabViewChild).setCompoundDrawablePadding(15);
                        }
                    }
                }
            }
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            Log.d("MC4kException", Log.getStackTraceString(e));
        }
    }

    public static String withSuffix(long count) {
        if (count < 1000) {
            return "" + count;
        }
        int exp = (int) (Math.log(count) / Math.log(1000));
        return String.format("%.1f %c",
                count / Math.pow(1000, exp),
                "kMGTPE".charAt(exp - 1));
    }

    public static ArrayList<ArticleListingResult> getFilteredContentList(
            ArrayList<ArticleListingResult> originalList, String contentType) {
        ArrayList<ArticleListingResult> filteredList = new ArrayList<>();
        for (int i = 0; i < originalList.size(); i++) {
            if (contentType.equals(originalList.get(i).getContentType())) {
                filteredList.add(originalList.get(i));
            }
        }
        return filteredList;
    }

    public static ArrayList<MixFeedResult> getFilteredContentList1(
            ArrayList<MixFeedResult> originalList, String contentType) {
        ArrayList<MixFeedResult> filteredList = new ArrayList<>();
        for (int i = 0; i < originalList.size(); i++) {
            if (contentType.equals(originalList.get(i).getContentType())) {
                filteredList.add(originalList.get(i));
            }
        }
        return filteredList;
    }

    public static int getFilteredPosition(int position, ArrayList<ArticleListingResult> filteredList,
            String contentType) {
        int effectivePosition = 0;
        for (int i = 0; i < position; i++) {
            if (contentType.equals(filteredList.get(i).getContentType())) {
                effectivePosition++;
            }
        }
        return effectivePosition;
    }

    public static int getFilteredPosition1(int position, ArrayList<MixFeedResult> filteredList,
            String contentType) {
        int effectivePosition = 0;
        for (int i = 0; i < position; i++) {
            if (contentType.equals(filteredList.get(i).getContentType())) {
                effectivePosition++;
            }
        }
        return effectivePosition;
    }

    public static void shareStoryWithWhatsApp(Context context, String userType, String blogSlug,
            String titleSlug,
            String screenName, String userDynamoId, String articleId, String authorId,
            String authorName) {
        String shareUrl = AppUtils.getShortStoryShareUrl(userType, blogSlug, titleSlug);
        shareStoryWithWhatsApp(context, shareUrl, screenName, userDynamoId, articleId, authorId,
                authorName);
    }

    private static void shareStoryWithWhatsApp(Context context, String shareUrl, String screenName, String userDynamoId,
            String articleId, String authorId, String authorName) {
        Uri uri = Uri.parse("file://" + BaseApplication.getAppContext().getExternalFilesDir(null) + File.separator
                + "image.jpg");
        if (shareImageWithWhatsApp(context, uri, shareUrl)) {
            Utils.pushShareStoryEvent(context, screenName, userDynamoId + "", articleId,
                    authorId + "~" + authorName, "Whatsapp");
        }
    }

    public static boolean shareImageWithWhatsApp(Context context, Uri uri, String shareUrl) {
        if (StringUtils.isNullOrEmpty(shareUrl)) {
            Toast.makeText(context, context.getString(R.string.moderation_or_share_whatsapp_fail),
                    Toast.LENGTH_SHORT).show();
            return false;
        } else {
            Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
            whatsappIntent.putExtra(Intent.EXTRA_STREAM, uri);
            whatsappIntent.putExtra(Intent.EXTRA_TEXT, shareUrl);
            whatsappIntent.setPackage("com.whatsapp");
            whatsappIntent.setType("image/*");
            try {
                context.startActivity(Intent.createChooser(whatsappIntent, "Share image via:"));
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(context,
                        context.getString(R.string.moderation_or_share_whatsapp_not_installed),
                        Toast.LENGTH_SHORT).show();
                return false;
            }
            return true;
        }
    }

    public static void shareStoryWithInstagram(Context context, String screenName, String userDynamoId,
            String articleId, String authorId, String authorName) {
        Uri uri = Uri.parse("file://" + BaseApplication.getAppContext().getExternalFilesDir(null) + File.separator
                + "image.jpg");
        if (shareImageWithInstagram(context, uri)) {
            Utils.pushShareStoryEvent(context, screenName, userDynamoId + "", articleId,
                    authorId + "~" + authorName, "Instagram");
        }
    }

    public static boolean shareImageWithInstagram(Context context, Uri uri) {
        Intent instaIntent = new Intent(Intent.ACTION_SEND);
        instaIntent.putExtra(Intent.EXTRA_STREAM, uri);
        instaIntent.setType("image/*");
        instaIntent.setPackage("com.instagram.android");
        try {
            context.startActivity(Intent.createChooser(instaIntent, "Share image via:"));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(context, context.getString(R.string.moderation_or_share_insta_not_installed),
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public static boolean shareLinkWithSuccessStatusWhatsapp(Context context, String shareUrl) {
        Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.setPackage("com.whatsapp");
        if (StringUtils.isNullOrEmpty(shareUrl)) {
            return false;
        } else {
            shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareUrl);
            context.startActivity(Intent.createChooser(shareIntent, "Momspresso"));
            return true;
        }
    }

    public static boolean shareGenericLinkWithSuccessStatus(Context context, String shareUrl) {
        Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        if (StringUtils.isNullOrEmpty(shareUrl)) {
            return false;
        } else {
            shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareUrl);
            context.startActivity(Intent.createChooser(shareIntent, "Momspresso"));
            return true;
        }
    }

    public static boolean shareGenericImageAndOrLink(Context context, Uri uri,
            String shareTextAndLink) {
        try {
            Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
            shareIntent.setType("image/jpeg");
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareTextAndLink);
            context.startActivity(Intent.createChooser(shareIntent, "Momspresso"));
            return true;
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            Log.d("MC4kException", Log.getStackTraceString(e));
            return false;
        }
    }

    public static boolean shareGenericImageAndOrLinkViaWhatsapp(Context context, Uri uri,
            String shareTextAndLink) {
        try {
            Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
            shareIntent.setType("image/jpeg");
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareTextAndLink);
            shareIntent.setPackage("com.whatsapp");
            context.startActivity(Intent.createChooser(shareIntent, "Momspresso"));
            return true;
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(context,
                    context.getString(R.string.moderation_or_share_whatsapp_not_installed),
                    Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public static void shareCampaignWithWhatsApp(Context context, String shareUrl, String screenName,
            String userDynamoId, String articleId, String authorId,
            String authorName) {
        if (StringUtils.isNullOrEmpty(shareUrl)) {
            Toast.makeText(context, context.getString(R.string.moderation_or_share_whatsapp_fail),
                    Toast.LENGTH_SHORT).show();
        } else {
            Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
            whatsappIntent.putExtra(Intent.EXTRA_TEXT, shareUrl);
            whatsappIntent.setPackage("com.whatsapp");
            whatsappIntent.setType("text/plain");
            try {
                context.startActivity(Intent.createChooser(whatsappIntent, "Share Url:"));
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(context,
                        context.getString(R.string.moderation_or_share_whatsapp_not_installed),
                        Toast.LENGTH_SHORT).show();
                return;
            }
            Utils.pushShareStoryEvent(context, screenName, userDynamoId + "", articleId,
                    authorId + "~" + authorName, "Whatsapp");
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
    }

    public static String getString(Activity activity, int stringId) {
        if (activity == null) {
            return BaseApplication.getAppContext().getString(stringId);
        } else {
            return activity.getString(stringId);
        }
    }

    public static String convertTimestampToDate(Long timestamp) {
        if (timestamp != null) {
            return new SimpleDateFormat("dd-MM-yyyy").format(new Date(timestamp * 1000));
        } else {
            return "";
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

    public static boolean isPrivateProfile(String authorId) {
        return StringUtils.isNullOrEmpty(authorId)
                || SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId()
                .equals(authorId);
    }

    public static boolean isContentCreator(String authorId) {
        return SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId().equals(authorId);
    }

    public static Bitmap getBitmapFromView(View view, String filename) {
        File appDir = BaseApplication.getAppContext().getExternalFilesDir(null);
        if (appDir != null && appDir.isDirectory()) {
            String[] children = appDir.list();
            if (children != null) {
                for (String child : children) {
                    new File(appDir, child).delete();
                }
            }
        }
        Bitmap returnedBitmap = Bitmap
                .createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null) {
            bgDrawable.draw(canvas);
        } else {
            canvas.drawColor(Color.WHITE);
        }

        view.draw(canvas);
        try {
            returnedBitmap.compress(Bitmap.CompressFormat.JPEG, 95, new FileOutputStream(
                    BaseApplication.getAppContext().getExternalFilesDir(null) + File.separator + filename + ".jpg"));
        } catch (FileNotFoundException e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            Log.d("MC4kException", Log.getStackTraceString(e));
        }
        return returnedBitmap;
    }

    public static String getHasTagFromCategoryList(@NotNull ArrayList<String> storyCategoriesList) {
        if (storyCategoriesList.contains(AppConstants.STORY_CATEGORY_QUOTES)) {
            return "#Quotes#Momspresso#Motherhood#Parents#Parenting#Kids#Babies#Children#MomsOfInstagram#real#relatable"
                    + "#IgMoms#MomspressoQuotes#Designyourwords#personaliseyourstory";
        } else if (storyCategoriesList.contains(AppConstants.STORY_CATEGORY_ROMANCE)) {
            return "#Romance#Momspresso#100wordstories#shortstories#Love#Motherhood#Parents#Parenting#Kids#Babies"
                    + "#Children#MomsOfInstagram#real#relatable#IgMoms#MomspressoStories#Designyourwords"
                    + "#personaliseyourstory";
        } else if (storyCategoriesList.contains(AppConstants.STORY_CATEGORY_COMEDY)) {
            return "#Comedy#Momspresso#100wordstories#shortstories#Laugh#Motherhood#Parents#Parenting#Kids#Babies"
                    + "#Children#Laughs#Laughters#Lol#Comic#Funny#MomsOfInstagram#real#relatable#IgMoms#JustForLaughs"
                    + "#MomspressoStories#Designyourwords#personaliseyourstory";
        } else if (storyCategoriesList.contains(AppConstants.STORY_CATEGORY_THRILLER)) {
            return "#Thriller#Suspense#100wordstories#shortstories#Motherhood#Parents#Parenting#Kids#Babies#Children"
                    + "#MomsOfInstagram#real#relatable#IgMoms#MomspressoStories#Designyourwords#personaliseyourstory";
        } else if (storyCategoriesList.contains(AppConstants.STORY_CATEGORY_INSPIRATIONAL)) {
            return "#Inspirational#Momspresso#100wordstories#shortstories#InspirationalStories#Motherhood#Parents"
                    + "#Parenting#Kids#Babies#Children#MomsOfInstagram#real#IgMoms#MomspressoStories#Designyourwords"
                    + "#personaliseyourstory";
        } else if (storyCategoriesList.contains(AppConstants.STORY_CATEGORY_DARK)) {
            return "#Dark#Darkstories#Momspresso#100wordstories#shortstories#Motherhood#Parents#Parenting#Kids#Babies"
                    + "#Children#MomsOfInstagram#IgMoms#MomspressoStories#Designyourwords#personaliseyourstory";
        }
        return "#Momspresso#100wordstories#shortstories#MomsOfInstagram#real#IgMoms#MomspressoStories#Designyourwords"
                + "#personaliseyourstory";
    }

    public static void copyToClipboard(@Nullable String hashtags) {
        ClipboardManager clipboard = (ClipboardManager) BaseApplication.getAppContext()
                .getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("label", hashtags);
        clipboard.setPrimaryClip(clip);
    }

    public static void populateLogoImageLanguageWise(Context context, ImageView logoImageView, String lang) {
        if (StringUtils.isNullOrEmpty(lang) || "0".equals(lang) || "en".equals(lang)) {
            Picasso.get().load(R.drawable.app_logo).into(logoImageView);
        } else if ("1".equals(lang) || "hi".equals(lang)) {
            Picasso.get().load(R.drawable.app_logo_hi).into(logoImageView);
        } else if ("2".equals(lang) || "mr".equals(lang)) {
            Picasso.get().load(R.drawable.app_logo_mr).into(logoImageView);
        } else if ("3".equals(lang) || "bn".equals(lang)) {
            Picasso.get().load(R.drawable.app_logo_bn).into(logoImageView);
        } else if ("4".equals(lang) || "ta".equals(lang)) {
            Picasso.get().load(R.drawable.app_logo_ta).into(logoImageView);
        } else if ("5".equals(lang) || "te".equals(lang)) {
            Picasso.get().load(R.drawable.app_logo_te).into(logoImageView);
        } else if ("6".equals(lang) || "kn".equals(lang)) {
            Picasso.get().load(R.drawable.app_logo_kn).into(logoImageView);
        } else if ("7".equals(lang) || "ml".equals(lang)) {
            Picasso.get().load(R.drawable.app_logo_ml).into(logoImageView);
        } else if ("8".equals(lang) || "gu".equals(lang)) {
            Picasso.get().load(R.drawable.app_logo_gu).into(logoImageView);
        } else if ("9".equals(lang) || "pa".equals(lang)) {
            Picasso.get().load(R.drawable.app_logo_pa).into(logoImageView);
        }
    }

    public static String getAdSlotId(String screenName, String position) {
        if (!StringUtils.isNullOrEmpty(position)) {
            position = "_" + position;
        }
        if (AppConstants.LOCALE_ENGLISH
                .equals(SharedPrefUtils.getAppLocale(BaseApplication.getAppContext()))) {
            return "APP_" + screenName + position + "_ENG";
        } else if (AppConstants.LOCALE_HINDI
                .equals(SharedPrefUtils.getAppLocale(BaseApplication.getAppContext()))) {
            return "APP_" + screenName + position + "_HIN";
        } else if (AppConstants.LOCALE_MARATHI
                .equals(SharedPrefUtils.getAppLocale(BaseApplication.getAppContext()))) {
            return "APP_" + screenName + position + "_MAR";
        } else if (AppConstants.LOCALE_BENGALI
                .equals(SharedPrefUtils.getAppLocale(BaseApplication.getAppContext()))) {
            return "APP_" + screenName + position + "_BEN";
        } else if (AppConstants.LOCALE_TAMIL
                .equals(SharedPrefUtils.getAppLocale(BaseApplication.getAppContext()))) {
            return "APP_" + screenName + position + "_TAM";
        } else if (AppConstants.LOCALE_TELUGU
                .equals(SharedPrefUtils.getAppLocale(BaseApplication.getAppContext()))) {
            return "APP_" + screenName + position + "_TEL";
        } else if (AppConstants.LOCALE_KANNADA
                .equals(SharedPrefUtils.getAppLocale(BaseApplication.getAppContext()))) {
            return "APP_" + screenName + position + "_KAN";
        } else if (AppConstants.LOCALE_MALAYALAM
                .equals(SharedPrefUtils.getAppLocale(BaseApplication.getAppContext()))) {
            return "APP_" + screenName + position + "_MAL";
        } else if (AppConstants.LOCALE_GUJARATI
                .equals(SharedPrefUtils.getAppLocale(BaseApplication.getAppContext()))) {
            return "APP_" + screenName + position + "_GUJ";
        } else if (AppConstants.LOCALE_PUNJABI
                .equals(SharedPrefUtils.getAppLocale(BaseApplication.getAppContext()))) {
            return "APP_" + screenName + position + "_PUN";
        } else {
            return "APP_" + screenName + position + "_ENG";
        }
    }

    public static int getLangKey() {
        if (AppConstants.LOCALE_ENGLISH
                .equals(SharedPrefUtils.getAppLocale(BaseApplication.getAppContext()))) {
            return 0;
        } else if (AppConstants.LOCALE_HINDI
                .equals(SharedPrefUtils.getAppLocale(BaseApplication.getAppContext()))) {
            return 1;
        } else if (AppConstants.LOCALE_MARATHI
                .equals(SharedPrefUtils.getAppLocale(BaseApplication.getAppContext()))) {
            return 2;
        } else if (AppConstants.LOCALE_BENGALI
                .equals(SharedPrefUtils.getAppLocale(BaseApplication.getAppContext()))) {
            return 3;
        } else if (AppConstants.LOCALE_TAMIL
                .equals(SharedPrefUtils.getAppLocale(BaseApplication.getAppContext()))) {
            return 4;
        } else if (AppConstants.LOCALE_TELUGU
                .equals(SharedPrefUtils.getAppLocale(BaseApplication.getAppContext()))) {
            return 5;
        } else if (AppConstants.LOCALE_KANNADA
                .equals(SharedPrefUtils.getAppLocale(BaseApplication.getAppContext()))) {
            return 6;
        } else if (AppConstants.LOCALE_MALAYALAM
                .equals(SharedPrefUtils.getAppLocale(BaseApplication.getAppContext()))) {
            return 7;
        } else if (AppConstants.LOCALE_GUJARATI
                .equals(SharedPrefUtils.getAppLocale(BaseApplication.getAppContext()))) {
            return 8;
        } else if (AppConstants.LOCALE_PUNJABI
                .equals(SharedPrefUtils.getAppLocale(BaseApplication.getAppContext()))) {
            return 9;
        } else {
            return 0;
        }
    }

    public static String getIpAddress(boolean useIPv4) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String hostAddress = addr.getHostAddress();
                        //boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
                        boolean isIPv4 = hostAddress.indexOf(':') < 0;

                        if (useIPv4) {
                            if (isIPv4) {
                                return hostAddress;
                            }
                        } else {
                            if (!isIPv4) {
                                int delim = hostAddress.indexOf('%'); // drop ip6 zone suffix
                                return delim < 0 ? hostAddress.toUpperCase()
                                        : hostAddress.substring(0, delim).toUpperCase();
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            Log.d("MC4KException", Log.getStackTraceString(e));
        }
        return "";
    }

    public static boolean isUserBucketedInNewEditor(FirebaseRemoteConfig firebaseRemoteConfig) {
        try {
            String object = firebaseRemoteConfig.getString("editor_user_json");
            Gson gson = new GsonBuilder().create();
            List<String> userList = gson.fromJson(object, new TypeToken<List<String>>() {
            }.getType());
            if (userList.contains(SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId())) {
                return true;
            }
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            Log.d("MC4KException", Log.getStackTraceString(e));
        }
        return false;
    }

    private static Map<String, Typeface> cachedFontMap = new HashMap<String, Typeface>();

    public static int pxToSp(final Context context, final float px) {
        return Math.round(px / context.getResources().getDisplayMetrics().scaledDensity);
    }

    public static int spToPx(final Context context, final float sp) {
        return Math.round(sp * context.getResources().getDisplayMetrics().scaledDensity);
    }

    public static Typeface findFont(Context context, String fontPath, String defaultFontPath) {

        if (fontPath == null) {
            return Typeface.DEFAULT;
        }

        String fontName = new File(fontPath).getName();
        String defaultFontName = "";
        if (!TextUtils.isEmpty(defaultFontPath)) {
            defaultFontName = new File(defaultFontPath).getName();
        }

        if (cachedFontMap.containsKey(fontName)) {
            return cachedFontMap.get(fontName);
        } else {
            try {
                AssetManager assets = BaseApplication.getAppContext().getResources().getAssets();

                if (Arrays.asList(assets.list("")).contains(fontPath)) {
                    Typeface typeface = Typeface.createFromAsset(BaseApplication.getAppContext().getAssets(), fontName);
                    cachedFontMap.put(fontName, typeface);
                    return typeface;
                } else if (Arrays.asList(assets.list("fonts")).contains(fontName)) {
                    Typeface typeface = Typeface.createFromAsset(BaseApplication.getAppContext().getAssets(),
                            String.format("fonts/%s", fontName));
                    cachedFontMap.put(fontName, typeface);
                    return typeface;
                } else if (Arrays.asList(assets.list("iconfonts")).contains(fontName)) {
                    Typeface typeface = Typeface.createFromAsset(BaseApplication.getAppContext().getAssets(),
                            String.format("iconfonts/%s", fontName));
                    cachedFontMap.put(fontName, typeface);
                    return typeface;
                } else if (!TextUtils.isEmpty(defaultFontPath) && Arrays.asList(assets.list(""))
                        .contains(defaultFontPath)) {
                    Typeface typeface = Typeface
                            .createFromAsset(BaseApplication.getAppContext().getAssets(), defaultFontPath);
                    cachedFontMap.put(defaultFontName, typeface);
                    return typeface;
                } else {
                    throw new Exception("Font not Found");
                }

            } catch (Exception e) {
                Log.e("Font", String.format("Unable to find %s font. Using Typeface.DEFAULT instead.", fontName));
                cachedFontMap.put(fontName, Typeface.DEFAULT);
                return Typeface.DEFAULT;
            }
        }
    }

    public static String getUtmParamsAppendedShareUrl(String shareUrl, String utmSource, String utmMedium) {
        if (StringUtils.isNullOrEmpty(utmSource) || StringUtils.isNullOrEmpty(utmMedium)) {
            return shareUrl;
        }
        return shareUrl + "?utm_source=" + utmSource + "&utm_medium=" + utmMedium;
    }

    public static boolean isMomspressoDomain(String url) {
        Pattern pattern = Pattern.compile(AppConstants.MOMSPRESSO_DOMAIN_REGEX);
        Matcher matcher = pattern.matcher(url);
        return matcher.matches();
    }

    public static String getLanguageFromLocale(Context context, String appLocale) {
        if (AppConstants.LOCALE_ENGLISH.equals(appLocale)) {
            return context.getString(R.string.language_label_english);
        } else if (AppConstants.LOCALE_HINDI.equals(appLocale)) {
            return context.getString(R.string.language_label_hindi);
        } else if (AppConstants.LOCALE_MARATHI.equals(appLocale)) {
            return context.getString(R.string.language_label_marathi);
        } else if (AppConstants.LOCALE_BENGALI.equals(appLocale)) {
            return context.getString(R.string.language_label_bengali);
        } else if (AppConstants.LOCALE_TAMIL.equals(appLocale)) {
            return context.getString(R.string.language_label_tamil);
        } else if (AppConstants.LOCALE_TELUGU.equals(appLocale)) {
            return context.getString(R.string.language_label_telegu);
        } else if (AppConstants.LOCALE_KANNADA.equals(appLocale)) {
            return context.getString(R.string.language_label_kannada);
        } else if (AppConstants.LOCALE_MALAYALAM.equals(appLocale)) {
            return context.getString(R.string.language_label_malayalam);
        } else if (AppConstants.LOCALE_GUJARATI.equals(appLocale)) {
            return context.getString(R.string.language_label_gujarati);
        } else if (AppConstants.LOCALE_PUNJABI.equals(appLocale)) {
            return context.getString(R.string.language_label_punjabi);
        } else {
            return context.getString(R.string.language_label_english);
        }
    }

    public static String getLanguage(String appLocale) {
        if (AppConstants.LOCALE_HINDI.equals(appLocale)) {
            return "hindi";
        } else if (AppConstants.LOCALE_MARATHI.equals(appLocale)) {
            return "marathi";
        } else if (AppConstants.LOCALE_BENGALI.equals(appLocale)) {
            return "bengali";
        } else if (AppConstants.LOCALE_TAMIL.equals(appLocale)) {
            return "tamil";
        } else if (AppConstants.LOCALE_TELUGU.equals(appLocale)) {
            return "telegu";
        } else if (AppConstants.LOCALE_KANNADA.equals(appLocale)) {
            return "kannada";
        } else if (AppConstants.LOCALE_MALAYALAM.equals(appLocale)) {
            return "malayalam";
        } else if (AppConstants.LOCALE_GUJARATI.equals(appLocale)) {
            return "gujarati";
        } else if (AppConstants.LOCALE_PUNJABI.equals(appLocale)) {
            return "punjabi";
        } else {
            return "www";
        }
    }

    public static SpannableString createSpannableForMentionHandling(String commenterId, String userName, String message,
            Map<String, Mentions> mentions, int color, int tagColor) {
        try {
            StringBuilder consolidateMessage = new StringBuilder(userName + " " + message);
            ArrayList<SpanData> list = new ArrayList<>();
            int userNameStart = 0;
            int userNameEnd = userName.length();
            MySpannable commenterSpannable = new MySpannable(AppConstants.USER_PROFILE_SHARE_BASE_URL + commenterId);
            SpanData te = new SpanData(userNameStart, userNameEnd, commenterSpannable);
            list.add(te);

            if (mentions != null) {
                Pattern pattern = Pattern.compile("(\\[~userId:)([a-z0-9]+)(\\])");
                Matcher matcher = pattern.matcher(consolidateMessage);
                while (matcher.find()) {
                    if (mentions.get(matcher.group(2)) != null) {
                        consolidateMessage = consolidateMessage
                                .replace(matcher.start(), matcher.end(), mentions.get(matcher.group(2)).getName());
                        MySpannable mySpannable = new MySpannable(
                                AppConstants.USER_PROFILE_SHARE_BASE_URL + mentions.get(matcher.group(2)).getUserId());
                        int start = matcher.start();
                        int end = start + mentions.get(matcher.group(2)).getName().length();
                        SpanData spanData = new SpanData(start, end, mySpannable);
                        list.add(spanData);
                        matcher = pattern.matcher(consolidateMessage);
                    }
                }
            }
            SpannableString messageSpannable = new SpannableString(consolidateMessage.toString());
            for (int i = 0; i < list.size(); i++) {
                messageSpannable.setSpan(list.get(i).mySpannable, list.get(i).start, list.get(i).end,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                if (i == 0) {
                    messageSpannable.setSpan(new ForegroundColorSpan(color), list.get(i).start, list.get(i).end,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                } else {
                    messageSpannable.setSpan(new ForegroundColorSpan(tagColor), list.get(i).start, list.get(i).end,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                messageSpannable.setSpan(new StyleSpan(Typeface.BOLD), list.get(i).start, list.get(i).end,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            return messageSpannable;
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            Log.d("MC4kException", Log.getStackTraceString(e));
            return new SpannableString("");
        }
    }

    public static String replaceUserIdWithName(String message, Map<String, Mentions> mentions) {
        try {
            StringBuilder consolidateMessage = new StringBuilder(message);
            if (mentions != null) {
                Pattern pattern = Pattern.compile("(\\[~userId:)([a-z0-9]+)(\\])");
                Matcher matcher = pattern.matcher(consolidateMessage);
                while (matcher.find()) {
                    if (mentions.get(matcher.group(2)) != null) {
                        consolidateMessage = consolidateMessage
                                .replace(matcher.start(), matcher.end(), mentions.get(matcher.group(2)).getName());
                        matcher = pattern.matcher(consolidateMessage);
                    }
                }
            }

            return consolidateMessage.toString();
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            Log.d("MC4kException", Log.getStackTraceString(e));
            return "";
        }
    }

    public static void updateFollowingStatus(ArrayList<ArticleListingResult> dataList) {
        if (dataList != null) {
            Map map = SharedPrefUtils.getFollowingJson(BaseApplication.getAppContext());
            for (int i = 0; i < dataList.size(); i++) {
                if (map.containsKey(dataList.get(i).getUserId())) {
                    dataList.get(i).setIsfollowing("1");
                }
            }
        }
    }

    public static void updateFollowingStatusMixFeed(ArrayList<MixFeedResult> dataList) {
        if (dataList != null) {
            Map map = SharedPrefUtils.getFollowingJson(BaseApplication.getAppContext());
            for (int i = 0; i < dataList.size(); i++) {
                if (map.containsKey(dataList.get(i).getUserId())) {
                    dataList.get(i).setIsfollowing("1");
                }
            }
        }
    }

    public static void updateFollowingStatusContributorList(ArrayList<ContributorListResult> dataList) {
        if (dataList != null) {
            Map map = SharedPrefUtils.getFollowingJson(BaseApplication.getAppContext());
            for (int i = 0; i < dataList.size(); i++) {
                if (map.containsKey(dataList.get(i).getId())) {
                    dataList.get(i).setIsFollowed(1);
                }
            }
        }
    }

    public static class SpanData {

        private int start;
        private int end;
        private MySpannable mySpannable;
        private MentionSpan mentionSpan;

        SpanData(int start, int end, MySpannable mySpannable) {
            this.start = start;
            this.end = end;
            this.mySpannable = mySpannable;
        }

        SpanData(int start, int end, MentionSpan mentionSpan) {
            this.start = start;
            this.end = end;
            this.mentionSpan = mentionSpan;
        }
    }

    public static class MySpannable extends ClickableSpan {

        private String url;

        MySpannable(String url) {
            this.url = url;
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            super.updateDrawState(ds);
            ds.setUnderlineText(false);
        }

        @Override
        public void onClick(View widget) {
            Intent intent = new Intent(widget.getContext(), DeeplinkActivity.class);
            intent.putExtra(Constants.URL, url);
            widget.getContext().startActivity(intent);
        }
    }


    public static SpannableString createMentionSpanForEditing(String message, Map<String, Mentions> mentions) {
        try {
            StringBuilder consolidateMessage = new StringBuilder(message);
            ArrayList<SpanData> list = new ArrayList<>();
            if (mentions != null) {
                Pattern pattern = Pattern.compile("(\\[~userId:)([a-z0-9]+)(\\])");
                Matcher matcher = pattern.matcher(consolidateMessage);
                while (matcher.find()) {
                    if (mentions.get(matcher.group(2)) != null) {
                        consolidateMessage = consolidateMessage
                                .replace(matcher.start(), matcher.end(), mentions.get(matcher.group(2)).getName());
                        MentionSpan mySpannable = new MentionSpan(mentions.get(matcher.group(2)));
                        int start = matcher.start();
                        int end = start + mentions.get(matcher.group(2)).getName().length();
                        SpanData spanData = new SpanData(start, end, mySpannable);
                        list.add(spanData);
                        matcher = pattern.matcher(consolidateMessage);
                    }
                }
            }
            SpannableString messageSpannable = new SpannableString(consolidateMessage.toString());
            for (int i = 0; i < list.size(); i++) {
                messageSpannable.setSpan(list.get(i).mentionSpan, list.get(i).start, list.get(i).end,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            return messageSpannable;
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            Log.d("MC4kException", Log.getStackTraceString(e));
            return new SpannableString("");
        }
    }

    public static String getBloggerGoldImageUrl() {
        if (AppConstants.LOCALE_ENGLISH
                .equals(SharedPrefUtils.getAppLocale(BaseApplication.getAppContext()))) {
            return "https://static.momspresso.com/assets/birthdaybonanza/banner-for-app-without-cta-english.png";
        } else if (AppConstants.LOCALE_HINDI
                .equals(SharedPrefUtils.getAppLocale(BaseApplication.getAppContext()))) {
            return "https://static.momspresso.com/assets/birthdaybonanza/banner-for-app-without-cta-hindi.png";
        } else if (AppConstants.LOCALE_MARATHI
                .equals(SharedPrefUtils.getAppLocale(BaseApplication.getAppContext()))) {
            return "https://static.momspresso.com/assets/birthdaybonanza/banner-for-app-without-cta-marathi.png";
        } else if (AppConstants.LOCALE_BENGALI
                .equals(SharedPrefUtils.getAppLocale(BaseApplication.getAppContext()))) {
            return "https://static.momspresso.com/assets/birthdaybonanza/banner-for-app-without-cta-bengali.jpg";
        } else if (AppConstants.LOCALE_TAMIL
                .equals(SharedPrefUtils.getAppLocale(BaseApplication.getAppContext()))) {
            return "https://static.momspresso.com/assets/birthdaybonanza/banner-for-app-without-cta-tamil.png";
        } else if (AppConstants.LOCALE_TELUGU
                .equals(SharedPrefUtils.getAppLocale(BaseApplication.getAppContext()))) {
            return "https://static.momspresso.com/assets/birthdaybonanza/banner-for-app-without-cta-telugu.png";
        } else if (AppConstants.LOCALE_KANNADA
                .equals(SharedPrefUtils.getAppLocale(BaseApplication.getAppContext()))) {
            return "https://static.momspresso.com/assets/birthdaybonanza/banner-for-app-without-cta-kannada.png";
        } else if (AppConstants.LOCALE_MALAYALAM
                .equals(SharedPrefUtils.getAppLocale(BaseApplication.getAppContext()))) {
            return "https://static.momspresso.com/assets/birthdaybonanza/banner-for-app-without-cta-malayalam.png";
        } else if (AppConstants.LOCALE_GUJARATI
                .equals(SharedPrefUtils.getAppLocale(BaseApplication.getAppContext()))) {
            return "https://static.momspresso.com/assets/birthdaybonanza/banner-for-app-without-cta-gujrati.png";
        } else if (AppConstants.LOCALE_PUNJABI
                .equals(SharedPrefUtils.getAppLocale(BaseApplication.getAppContext()))) {
            return "https://static.momspresso.com/assets/birthdaybonanza/banner-for-app-without-cta-punjabi.jpg";
        } else {
            return "https://static.momspresso.com/assets/birthdaybonanza/banner-for-app-without-cta-english.png";
        }
    }

    public static String getBloggerGoldImageUrlCreateSection() {
        if (AppConstants.LOCALE_ENGLISH
                .equals(SharedPrefUtils.getAppLocale(BaseApplication.getAppContext()))) {
            return "https://static.momspresso.com/assets/birthdaybonanza/banner-with-CTA-english.png";
        } else if (AppConstants.LOCALE_HINDI
                .equals(SharedPrefUtils.getAppLocale(BaseApplication.getAppContext()))) {
            return "https://static.momspresso.com/assets/birthdaybonanza/banner-with-CTA-hindi.jpg";
        } else if (AppConstants.LOCALE_MARATHI
                .equals(SharedPrefUtils.getAppLocale(BaseApplication.getAppContext()))) {
            return "https://static.momspresso.com/assets/birthdaybonanza/banner-with-CTA-marathi.jpg";
        } else if (AppConstants.LOCALE_BENGALI
                .equals(SharedPrefUtils.getAppLocale(BaseApplication.getAppContext()))) {
            return "https://static.momspresso.com/assets/birthdaybonanza/banner-with-CTA-bengali.jpg";
        } else if (AppConstants.LOCALE_TAMIL
                .equals(SharedPrefUtils.getAppLocale(BaseApplication.getAppContext()))) {
            return "https://static.momspresso.com/assets/birthdaybonanza/banner-with-CTA-tamil.png";
        } else if (AppConstants.LOCALE_TELUGU
                .equals(SharedPrefUtils.getAppLocale(BaseApplication.getAppContext()))) {
            return "https://static.momspresso.com/assets/birthdaybonanza/banner-with-CTA-telugu.png";
        } else if (AppConstants.LOCALE_KANNADA
                .equals(SharedPrefUtils.getAppLocale(BaseApplication.getAppContext()))) {
            return "https://static.momspresso.com/assets/birthdaybonanza/banner-with-CTA-kannada.png";
        } else if (AppConstants.LOCALE_MALAYALAM
                .equals(SharedPrefUtils.getAppLocale(BaseApplication.getAppContext()))) {
            return "https://static.momspresso.com/assets/birthdaybonanza/banner-with-CTA-malayalam.png";
        } else if (AppConstants.LOCALE_GUJARATI
                .equals(SharedPrefUtils.getAppLocale(BaseApplication.getAppContext()))) {
            return "https://static.momspresso.com/assets/birthdaybonanza/banner-with-CTA-gujrati.png";
        } else if (AppConstants.LOCALE_PUNJABI
                .equals(SharedPrefUtils.getAppLocale(BaseApplication.getAppContext()))) {
            return "https://static.momspresso.com/assets/birthdaybonanza/banner-with-CTA-punjabi.jpg";
        } else {
            return "https://static.momspresso.com/assets/birthdaybonanza/banner-with-CTA-english.png";
        }
    }
}
