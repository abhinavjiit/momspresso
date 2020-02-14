//
// Copyright (c) Microsoft. All rights reserved.
// Licensed under the MIT license.
//
// Microsoft Cognitive Services (formerly Project Oxford): https://www.microsoft.com/cognitive-services
//
// Microsoft Cognitive Services (formerly Project Oxford) GitHub:
// https://github.com/Microsoft/Cognitive-Speech-TTS
//
// Copyright (c) Microsoft Corporation
// All rights reserved.
//
// MIT License:
// Permission is hereby granted, free of charge, to any person obtaining
// a copy of this software and associated documentation files (the
// "Software"), to deal in the Software without restriction, including
// without limitation the rights to use, copy, modify, merge, publish,
// distribute, sublicense, and/or sell copies of the Software, and to
// permit persons to whom the Software is furnished to do so, subject to
// the following conditions:
//
// The above copyright notice and this permission notice shall be
// included in all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED ""AS IS"", WITHOUT WARRANTY OF ANY KIND,
// EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
// MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
// NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
// LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
// OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
// WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
//
package com.mycity4kids.azuretts;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.AsyncTask;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class Synthesizer {
    private static final String LOG_TAG = "SpeechSDKTTS";
    private static String s_contentType = "application/ssml+xml";
    private final String m_serviceUri = "https://westus.tts.speech.microsoft.com/cognitiveservices/v1?traffictype=AndroidTest";
    private String m_outputFormat = "raw-16khz-16bit-mono-pcm";
    private Authentication m_auth;
    private byte[] m_result;
    private Voice m_serviceVoice;
    private ServiceStrategy m_eServiceStrategy;
    private AudioTrack audioTrack;
    private boolean shouldStopAudio;

    private void playSound(final byte[] sound, final Runnable callback) {
        if (sound == null || sound.length == 0) {
            return;
        }

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                final int SAMPLE_RATE = 16000;
                if (!shouldStopAudio) {
                    audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, SAMPLE_RATE, AudioFormat.CHANNEL_CONFIGURATION_MONO,
                            AudioFormat.ENCODING_PCM_16BIT, AudioTrack.getMinBufferSize(SAMPLE_RATE, AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT), AudioTrack.MODE_STREAM);
                    if (audioTrack.getState() == AudioTrack.STATE_INITIALIZED) {
                        audioTrack.play();
                        audioTrack.write(sound, 0, sound.length);
                        audioTrack.stop();
                        audioTrack.release();
                    }
                    if (callback != null) {
                        callback.run();
                    }
                }
            }
        });
    }

    //stop playing audio data
    // if use STREAM mode, will wait for the end of the last write buffer data will stop.
    // if you stop immediately, call the pause() method and then call the flush() method to discard the data that has not yet been played
    public void stopSound() {
        shouldStopAudio = true;
        try {
            if (audioTrack != null && audioTrack.getState() == AudioTrack.STATE_INITIALIZED) {
                audioTrack.pause();
                audioTrack.flush();

            }
        } catch (Exception e) {
            Log.d("-----AZURE----", Log.getStackTraceString(e));
            e.printStackTrace();
        }
    }

    public enum ServiceStrategy {
        AlwaysService//, WiFiOnly, WiFi3G4GOnly, NoService
    }

    public Synthesizer(String apiKey) {
        m_serviceVoice = new Voice("en-US");
        m_eServiceStrategy = ServiceStrategy.AlwaysService;
        m_auth = new Authentication(apiKey);
    }

    public void SetVoice(Voice serviceVoice, Voice localVoice) {
        m_serviceVoice = serviceVoice;
    }

    public void SetServiceStrategy(ServiceStrategy eServiceStrategy) {
        m_eServiceStrategy = eServiceStrategy;
    }

    public void Speak(String text) {
        String ssml = "<speak version='1.0' xml:lang='" + m_serviceVoice.lang + "'><voice xml:lang='" + m_serviceVoice.lang + "' xml:gender='" + m_serviceVoice.gender + "'";
        if (m_eServiceStrategy == ServiceStrategy.AlwaysService) {
            if (m_serviceVoice.voiceName.length() > 0) {
                ssml += " name='" + m_serviceVoice.voiceName + "'>";
            } else {
                ssml += ">";
            }
            ssml += text + "</voice></speak>";
        }
        SpeakSSML(ssml);
    }

    public void SpeakToAudio(String text) {
        shouldStopAudio = false;
        Speak(text);
    }

    public byte[] SpeakSSML(String ssml) {
        byte[] result = null;
        if (m_eServiceStrategy == ServiceStrategy.AlwaysService) {
            new PlayAudioAsyncTask().execute(ssml);
        }
        return result;
    }

    private class PlayAudioAsyncTask extends AsyncTask<String, byte[], byte[]> {
        public PlayAudioAsyncTask() {
        }

        @Override
        protected byte[] doInBackground(String... strings) {

            int code;
            synchronized (m_auth) {
                String accessToken = m_auth.GetAccessToken();
                try {
                    URL url = new URL(m_serviceUri);
                    HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                    urlConnection.setDoInput(true);
                    urlConnection.setDoOutput(true);
                    urlConnection.setConnectTimeout(5000);
                    urlConnection.setReadTimeout(15000);
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setRequestProperty("Content-Type", s_contentType);
                    urlConnection.setRequestProperty("X-MICROSOFT-OutputFormat", m_outputFormat);
                    urlConnection.setRequestProperty("Authorization", "Bearer " + accessToken);
                    urlConnection.setRequestProperty("X-Search-AppId", "07D3234E49CE426DAA29772419F436CA");
                    urlConnection.setRequestProperty("X-Search-ClientID", "1ECFAE91408841A480F00935DC390960");
                    urlConnection.setRequestProperty("User-Agent", "TTSAndroid");
                    urlConnection.setRequestProperty("Accept", "*/*");
                    byte[] ssmlBytes = strings[0].getBytes();
                    urlConnection.setRequestProperty("content-length", String.valueOf(ssmlBytes.length));
                    urlConnection.connect();
                    urlConnection.getOutputStream().write(ssmlBytes);
                    code = urlConnection.getResponseCode();
                    if (code == 200) {
                        InputStream in = urlConnection.getInputStream();
                        ByteArrayOutputStream bout = new ByteArrayOutputStream();
                        byte[] bytes = new byte[1024];
                        int ret = in.read(bytes);
                        while (ret > 0) {
                            bout.write(bytes, 0, ret);
                            ret = in.read(bytes);
                        }
                        m_result = bout.toByteArray();
                    }
                    urlConnection.disconnect();
                } catch (Exception e) {
                    Log.e(LOG_TAG, "Exception error", e);
                }
            }
            return m_result;
        }

        @Override
        protected void onPostExecute(byte[] result) {
            if (!shouldStopAudio) {
                playSound(result, null);
            }

        }
    }
}
