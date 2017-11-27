package com.mycity4kids.tts;

import android.annotation.TargetApi;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.widget.Toast;

import com.mycity4kids.constants.AppConstants;

import java.util.HashMap;
import java.util.Locale;

/**
 * Created by hemant on 28/6/17.
 */
public class ReadArticleService extends Service implements TextToSpeech.OnInitListener {
    private TextToSpeech mTTS;
    private int ready = 999;
    private String content;
    private String langCode;


    @Override
    public void onCreate() {
        mTTS = new TextToSpeech(getApplicationContext(), this);
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("", "TTSService Created!");
        if (null != intent) {
            content = intent.getStringExtra("content");
            langCode = intent.getStringExtra("langCategoryId");
        } else {

        }

        return super.onStartCommand(intent, flags, startId);

    }

    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public void onDestroy() {
        mTTS.shutdown();
        super.onDestroy();
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
    @Override
    public void onInit(int status) {
        Log.d("", "TTSService onInit: " + String.valueOf(status));
        HashMap<String, String> params = new HashMap<String, String>();
        params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "utteranceId");
        if (status == TextToSpeech.SUCCESS) {
            ready = 1;
            if (AppConstants.HINDI_CATEGORYID.equals(langCode)) {
                if (mTTS.isLanguageAvailable(new Locale("hi")) != TextToSpeech.LANG_AVAILABLE) {
                    Toast.makeText(this, "Language not supported for speech", Toast.LENGTH_SHORT).show();
                    stopSelf();
                    return;
                }
                Locale locale = new Locale("hi", "IN");
                mTTS.setLanguage(locale);
            } else if (AppConstants.BANGLA_CATEGORYID.equals(langCode)) {
                if (mTTS.isLanguageAvailable(new Locale("bn")) != TextToSpeech.LANG_AVAILABLE) {
                    Toast.makeText(this, "Language not supported for speech", Toast.LENGTH_SHORT).show();
                    stopSelf();
                    return;
                }
                Locale locale = new Locale("bn", "IN");
                mTTS.setLanguage(locale);
            } else {
                Locale locale = new Locale("en", "IN");
                mTTS.setLanguage(locale);
            }

            mTTS.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                @Override
                public void onStart(String utteranceId) {
                    Log.d("", "TTSService onStart!");
                }

                @Override
                public void onDone(String utteranceId) {
                    Log.d("", "TTSService onDone!");
                    stopSelf();
                }

                @Override
                public void onError(String utteranceId) {
                    Log.d("", "TTSService onError!");
                }
            });
            startReading(params);
        } else {
            ready = 0;
            Log.d("", "failed to initialize");
        }

    }

    private void startReading(final HashMap<String, String> params) {
//        Toast.makeText(ReadArticleService.this, content, Toast.LENGTH_SHORT).show();
        Log.d("Read Language", "" + mTTS.getLanguage());
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (ready == 1) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        mTTS.speak(content, TextToSpeech.QUEUE_FLUSH, null, this.hashCode() + "");
                    } else {
                        mTTS.speak(content, TextToSpeech.QUEUE_FLUSH, params);
                    }
                } else {
                    Log.d("", "not ready");
                }
            }

        }).start();
    }
}