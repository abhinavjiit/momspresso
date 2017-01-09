package com.mycity4kids.utils;

import android.content.Context;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.models.Topics;
import com.mycity4kids.models.TopicsResponse;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;

/**
 * Created by hemant on 22/11/16.
 */
public class AppUtils {
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

    public static String extractYoutubeId(String url) {
        String query = null;
        try {
            query = new URL(url).getQuery();
        } catch (MalformedURLException e) {
            e.printStackTrace();
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

    public static String getYoutubeThumbnailURL(String youtubeUrl) {
        String youtubeId = extractYoutubeId(youtubeUrl);
        String youtubeThumbUrl = "http://img.youtube.com/vi/" + youtubeId + "/0.jpg";
        return youtubeThumbUrl;
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

    public static Topics getHindiTopic(Context mContext) {
        try {
            FileInputStream fileInputStream = mContext.openFileInput(AppConstants.CATEGORIES_JSON_FILE);
            String fileContent = convertStreamToString(fileInputStream);
            TopicsResponse responseData = new Gson().fromJson(fileContent, TopicsResponse.class);

            for (int i = 0; i < responseData.getData().size(); i++) {
                for (int j = 0; j < responseData.getData().get(i).getChild().size(); j++) {
                    if (AppConstants.HINDI_CATEGORYID.equals(responseData.getData().get(i).getChild().get(j).getId())) {
                        return responseData.getData().get(i).getChild().get(j);
                    }
                }
            }
        } catch (FileNotFoundException fnfe) {

        }
        return null;
    }
}
