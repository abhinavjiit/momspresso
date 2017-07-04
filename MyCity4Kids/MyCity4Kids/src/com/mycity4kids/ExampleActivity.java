package com.mycity4kids;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.kelltontech.utils.DateTimeUtils;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.models.parentingdetails.ImageData;
import com.mycity4kids.models.parentingdetails.VideoData;
import com.mycity4kids.models.response.ArticleDetailResult;
import com.mycity4kids.models.response.ArticleDetailWebserviceResponse;
import com.mycity4kids.retrofitAPIsInterfaces.ArticleDetailsAPI;
import com.mycity4kids.ui.CircleTransformation;
import com.mycity4kids.utils.TrackArticleReadTime;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

public class ExampleActivity extends BaseActivity {


    private VideoEnabledWebView webView;
    private VideoEnabledWebChromeClient webChromeClient;
    private ArrayList<ImageData> imageList;
    private ArrayList<VideoData> videoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.temp_activity_example);

        // Save the web view
        webView = (VideoEnabledWebView) findViewById(R.id.webView);

        // Initialize the VideoEnabledWebChromeClient and set event handlers
        View nonVideoLayout = findViewById(R.id.nonVideoLayout); // Your own view, read class comments
        ViewGroup videoLayout = (ViewGroup) findViewById(R.id.videoLayout); // Your own view, read class comments
        //noinspection all
        View loadingView = getLayoutInflater().inflate(R.layout.temp_view_loading_video, null); // Your own view, read class comments
        webChromeClient = new VideoEnabledWebChromeClient(nonVideoLayout, videoLayout, loadingView, webView) // See all available constructors...
        {
            // Subscribe to standard events, such as onProgressChanged()...
            @Override
            public void onProgressChanged(WebView view, int progress) {
                // Your code...
            }
        };
        webChromeClient.setOnToggledFullscreen(new VideoEnabledWebChromeClient.ToggledFullscreenCallback() {
            @Override
            public void toggledFullscreen(boolean fullscreen) {
                // Your code to handle the full-screen change, for example showing and hiding the title bar. Example:
                if (fullscreen) {
                    WindowManager.LayoutParams attrs = getWindow().getAttributes();
                    attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
                    attrs.flags |= WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
                    getWindow().setAttributes(attrs);
                    if (android.os.Build.VERSION.SDK_INT >= 14) {
                        //noinspection all
                        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
                    }
                } else {
                    WindowManager.LayoutParams attrs = getWindow().getAttributes();
                    attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
                    attrs.flags &= ~WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
                    getWindow().setAttributes(attrs);
                    if (android.os.Build.VERSION.SDK_INT >= 14) {
                        //noinspection all
                        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
                    }
                }

            }
        });
        webView.setWebChromeClient(webChromeClient);
        // Call private class InsideWebViewClient
        webView.setWebViewClient(new InsideWebViewClient());
        hitArticleDetailsS3API();
//        String frameVideo = "<html><body>Video From YouTube<br><iframe width=\"420\" height=\"315\" src=\"https://www.youtube.com/embed/47yJ2XCRLZs\" frameborder=\"0\" webkitallowfullscreen mozallowfullscreen allowfullscreen></iframe></body></html>";
        // Navigate anywhere you want, but consider that this classes have only been tested on YouTube's mobile site
//        webView.loadUrl("https://www.youtube.com/watch?v=Ul4nxhEtaB4");
//        webView.loadDataWithBaseURL("", frameVideo, "text/html", "utf-8", "");

    }

    private void hitArticleDetailsS3API() {
        Retrofit retro = BaseApplication.getInstance().getRetrofit();
        ArticleDetailsAPI articleDetailsAPI = retro.create(ArticleDetailsAPI.class);
        Call<ArticleDetailResult> call = articleDetailsAPI.getArticleDetailsFromS3("article-259a9f63a01e44f39481a359abc34d7e");
        call.enqueue(articleDetailResponseCallbackS3);

//        For Local JSON Testing
//        Call<ArticleDetailResult> call = articleDetailsAPI.getArticleDetailsFromLocal();

//        For Direct fetch from webservice testing
//        getArticleDetailsWebserviceAPI();
    }

    Callback<ArticleDetailResult> articleDetailResponseCallbackS3 = new Callback<ArticleDetailResult>() {
        @Override
        public void onResponse(Call<ArticleDetailResult> call, retrofit2.Response<ArticleDetailResult> response) {
            removeProgressDialog();
            if (response == null || response.body() == null) {
                return;
            }
            try {
                ArticleDetailResult responseData = (ArticleDetailResult) response.body();
                getResponseUpdateUi(responseData);

            } catch (Exception e) {
                removeProgressDialog();
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }

        }

        @Override
        public void onFailure(Call<ArticleDetailResult> call, Throwable t) {
            removeProgressDialog();
        }
    };


    private void getResponseUpdateUi(ArticleDetailResult detailsResponse) {
        ArticleDetailResult detailData = detailsResponse;
        imageList = detailData.getBody().getImage();
        videoList = detailData.getBody().getVideo();
        String bodyDescription = detailData.getBody().getText();
        String bodyDesc = bodyDescription;
        int imageIndex = 0;
        int imageReadTime = 0;
        if (imageList.size() > 0) {
            for (ImageData images : imageList) {
                if (imageIndex <= AppConstants.MIN_ARTICLE_BODY_IMAGE_READ_TIME) {
                    imageReadTime = imageReadTime + AppConstants.MAX_ARTICLE_BODY_IMAGE_READ_TIME - imageIndex;
                } else {
                    imageReadTime = imageReadTime + AppConstants.MIN_ARTICLE_BODY_IMAGE_READ_TIME;
                }
                imageIndex++;
                if (bodyDescription.contains(images.getKey())) {
                    bodyDesc = bodyDesc.replace(images.getKey(), "<p style='text-align:center'><img src=" + images.getValue() + " style=\"width: 100%;\"+></p>");
                }
            }
            if (null != videoList && !videoList.isEmpty()) {
                for (VideoData video : videoList) {
                    String vURL = video.getVideoUrl().replace("http:", "").replace("https:", "");
                    bodyDesc = bodyDesc.replace(video.getKey(), "<p style='text-align:center'><iframe allowfullscreen src=http:" + vURL + "?modestbranding=1&amp;rel=0&amp;showinfo=0\" style=\"width: 100%;\"></iframe></p>");
                }
            }

//            <iframe allowfullscreen=\"true\" allowtransparency=\"true\" frameborder=\"0\" src=\"//www.youtube.com/embed/TqQ5xM2x1Gs\" width=\"640\" height=\"360\" class=\"note-video-clip\"></iframe>
            String bodyImgTxt = "<html><head>" +
                    "" +
                    "<style type=\"text/css\">\n" +
                    "@font-face {\n" +
                    "    font-family: MyFont;\n" +
                    "    src: url(\"file:///android_asset/fonts/georgia.ttf\")\n" +
                    "}\n" +
                    "body {\n" +
                    "    font-family: MyFont;\n" +
                    "    font-size: " + getResources().getDimension(R.dimen.article_details_text_size) + ";\n" +
                    "    line-height: " + getResources().getInteger(R.integer.article_details_line_height) + "%;\n" +
                    "    text-align: left;\n" +
                    "}\n" +
                    "</style>" +
                    "</head><body>" + bodyDesc + "</body></html>";

            webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
            webView.loadDataWithBaseURL("", bodyImgTxt, "text/html", "utf-8", "");
            webView.getSettings().setJavaScriptEnabled(true);
        } else {
            if (null != videoList && !videoList.isEmpty()) {
                for (VideoData video : videoList) {
                    String vURL = video.getVideoUrl().replace("http:", "").replace("https:", "");
                    bodyDesc = bodyDesc.replace(video.getKey(), "<p style='text-align:center'><iframe allowfullscreen src=http:" + vURL + "?modestbranding=1&amp;rel=0&amp;showinfo=0\" style=\"width: 100%;\" ></iframe></p>");
                }
            }
            String bodyImgTxt = "<html><head>" +
                    "" +
                    "<style type=\"text/css\">\n" +
                    "@font-face {\n" +
                    "    font-family: MyFont;\n" +
                    "    src: url(\"file:///android_asset/fonts/georgia.ttf\")\n" +
                    "}\n" +
                    "body {\n" +
                    "    font-family: MyFont;\n" +
                    "    font-size: " + getResources().getDimension(R.dimen.article_details_text_size) + ";\n" +
                    "    line-height: " + getResources().getInteger(R.integer.article_details_line_height) + "%;\n" +
                    "    text-align: left;\n" +
                    "}\n" +
                    "</style>" +
                    "</head><body>" + bodyDesc + "</body></html>";

            webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
            webView.loadDataWithBaseURL("", bodyImgTxt, "text/html", "utf-8", "");
            webView.getSettings().setJavaScriptEnabled(true);
        }

    }

    @Override
    protected void updateUi(Response response) {

    }

    private class InsideWebViewClient extends WebViewClient {
        @Override
        // Force links to be opened inside WebView and not in Default Browser
        // Thanks http://stackoverflow.com/a/33681975/1815624
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

    @Override
    public void onBackPressed() {
        // Notify the VideoEnabledWebChromeClient, and handle it ourselves if it doesn't handle it
        if (!webChromeClient.onBackPressed()) {
            if (webView.canGoBack()) {
                webView.goBack();
            } else {
                // Standard back button implementation (for example this could close the app)
                super.onBackPressed();
            }
        }
    }


//    private VideoEnabledWebView webView;
//    private VideoEnabledWebChromeClient webChromeClient;
//    public static final String javascriptcode = "<script type=\"text/javascript\" src=\"jquery.js\"></script> <script type=\"text/javascript\" src=\"jquerylazyload.js\"></script> <script type=\"text/javascript\" language=\"javascript\"> /*source: http://www.appelsiini.net/projects/lazyload*/ function initials() { loadVideo(); convertImages(); loadImages(); } function loadImages() { $(\"img.lazy\").lazyload({ effect: \"fadeIn\" }); $(\"img.lazy\").click(function() { image.openImageActivity($(this).attr(\"data-original\")); }); } function loadVideo() { var n, v = document.getElementsByClassName(\"youtube\"); var l = v.length; for (n = 0; n < l; n++) { var iframe = document.createElement(\"iframe\"); iframe.setAttribute(\"src\", \"https://www.youtube.com/embed/\" + v[n].dataset.id + \"?rel=0&fs=1\"); iframe.setAttribute(\"frameborder\", \"0\"); iframe.setAttribute(\"width\", \"100%\"); iframe.setAttribute(\"id\", \"player\"); iframe.setAttribute(\"height\", \"240\"); iframe.setAttribute(\"allowfullscreen\", \"1\"); while (v[n].firstChild) { v[n].removeChild(v[n].firstChild); } v[n].appendChild(iframe); } } function convertImages() { var n, v = document.getElementsByTagName(\"img\"); var l = v.length; for (n = 0; n < l; n++) { v[n].setAttribute(\"data-original\", v[n].src); v[n].setAttribute(\"src\", \"placeholder.png\"); v[n].setAttribute(\"class\", \"lazy\"); } } </script>";
//    public static final String webstyle = "@font-face { font-family: MyFont; src: url(\"fonts/RobotoSlab-Regular.ttf\") } body { font-family: MyFont !important; background-color: #FFFFFF; font-size: 0.987em; font-weight: 120; color: #4D4D4D; line-height: 160%; } strong { font-family: MyFont !important; font-size: 0.987em; } span { font-family: MyFont !important; font-size: 0.987em !important; } img { display: inline; height: auto; max-width: 100%; } .youtube { position: relative; padding-bottom: 56.23%; height: 0; overflow: hidden; max-width: 100%; background: #000; margin: 5px; } .youtube iframe, .youtube object, .youtube embed { position: absolute; top: 0; left: 0; width: 100%; height: 100%; z-index: 100; background: transparent; } .youtube img { bottom: 0; display: block; left: 0; margin: auto; max-width: 100%; width: 100%; position: absolute; right: 0; top: 0; border: none; height: auto; cursor: pointer; -webkit-transition: .4s all; -moz-transition: .4s all; transition: .4s all; } .youtube img:hover { -webkit-filter: brightness(75%); } .youtube .play { height: 72px; width: 72px; left: 50%; top: 50%; margin-left: -36px; margin-top: -36px; position: absolute; background: url(\"http://i.imgur.com/TxzC70f.png\") no-repeat; cursor: pointer; }";
//    private static final String RELATIVE_PATH_ASSETS = "file:///android_asset/";
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.temp_activity_example);
//
//        // Save the web view
//        webView = (VideoEnabledWebView) findViewById(R.id.webView);
//
//        // Initialize the VideoEnabledWebChromeClient and set event handlers
//        View nonVideoLayout = findViewById(R.id.nonVideoLayout); // Your own view, read class comments
//        ViewGroup videoLayout = (ViewGroup) findViewById(R.id.videoLayout); // Your own view, read class comments
//        //noinspection all
//        View loadingView = getLayoutInflater().inflate(R.layout.temp_view_loading_video, null); // Your own view, read class comments
//        webChromeClient = new VideoEnabledWebChromeClient(nonVideoLayout, videoLayout, loadingView, webView) // See all available constructors...
//        {
//            // Subscribe to standard events, such as onProgressChanged()...
//            @Override
//            public void onProgressChanged(WebView view, int progress) {
//                // Your code...
//            }
//        };
//        this.webChromeClient.setOnToggledFullscreen(new C20477());
//        webChromeClient.setOnToggledFullscreen(new VideoEnabledWebChromeClient.ToggledFullscreenCallback() {
//            @Override
//            public void toggledFullscreen(boolean fullscreen) {
//                // Your code to handle the full-screen change, for example showing and hiding the title bar. Example:
//                if (fullscreen) {
//                    WindowManager.LayoutParams attrs = getWindow().getAttributes();
//                    attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
//                    attrs.flags |= WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
//                    getWindow().setAttributes(attrs);
//                    if (android.os.Build.VERSION.SDK_INT >= 14) {
//                        //noinspection all
//                        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
//                    }
//                } else {
//                    WindowManager.LayoutParams attrs = getWindow().getAttributes();
//                    attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
//                    attrs.flags &= ~WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
//                    getWindow().setAttributes(attrs);
//                    if (android.os.Build.VERSION.SDK_INT >= 14) {
//                        //noinspection all
//                        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
//                    }
//                }
//
//            }
//        });
//        webView.setWebViewClient(new C20488());
//        WebSettings webSettings = webView.getSettings();
//        webSettings.setJavaScriptEnabled(true);
//        webView.setWebChromeClient(webChromeClient);
//        // Call private class InsideWebViewClient
//        webView.setWebViewClient(new InsideWebViewClient());
//        // Navigate anywhere you want, but consider that this classes have only been tested on YouTube's mobile site
////        String frameVideo = "<p>Newborns often cry because of colic pain. Today we are sharing 5 simple methods to soothe colic pain. Watch this video to know about these methods.<br /><br /></p>\n<div class=\"youtube\" style=\"width: 100%; height: 240px;\" data-id=\"Ul4nxhEtaB4\"><a href=\"https://www.youtube.com/watch?v=Ul4nxhEtaB4\">https://www.youtube.com/watch?v=Ul4nxhEtaB4<br /><br /></a></div>\n<p><span style=\"font-size: 15.52px;\">Keep following Babygogo to know more about baby care. <br />Happy Parenting!<br /><br /></span></p>";
//        String frameVideo = "<html><body>Video From YouTube<br><iframe width=\"420\" height=\"315\" src=\"https://www.youtube.com/embed/47yJ2XCRLZs\" frameborder=\"0\" webkitallowfullscreen mozallowfullscreen allowfullscreen></iframe></body></html>";
////        String str2 = "<style>" + webstyle + " </style> <body> " + javascriptcode + (frameVideo == null ? "" : frameVideo) + " </body>";
////        webView.loadUrl("http://m.youtube.com");
////        String str2 = "<style>" + getStyleFromConfig() + " </style> <body> " + getJavaScriptFromConfig() + (article.body == null ? "" : article.body) + " </body>";
//        webView.getSettings().setJavaScriptEnabled(true);
//        webView.setWebChromeClient(this.webChromeClient);
//        webView.setVerticalScrollBarEnabled(false);
////        webView.setWebViewClient(new C20488());
//        webView.setFocusable(false);
//        WebSettings settings = webView.getSettings();
//        settings.setJavaScriptEnabled(true);
//        settings.setDomStorageEnabled(true);
//
//        webView.loadDataWithBaseURL("", frameVideo, "text/html", "utf-8", null);
////        webView.loadDataWithBaseURL("", frameVideo, "text/html", "utf-8", "");
//    }
//
//    class C20488 extends WebViewClient {
//        C20488() {
//        }
//
//        public void onPageFinished(WebView webView, String str) {
//            ExampleActivity.this.webView.loadUrl("javascript:initials()");
////            ExampleActivity.this.showShareCardView();
//        }
//
//        public boolean shouldOverrideUrlLoading(WebView webView, String str) {
//            if (str == null) {
//                return false;
//            }
//            webView.getContext().startActivity(new Intent("android.intent.action.VIEW", Uri.parse(str)));
//            return true;
//        }
//    }
//
//    @Override
//    protected void updateUi(Response response) {
//
//    }
//
//    private class InsideWebViewClient extends WebViewClient {
//        @Override
//        // Force links to be opened inside WebView and not in Default Browser
//        // Thanks http://stackoverflow.com/a/33681975/1815624
//        public boolean shouldOverrideUrlLoading(WebView view, String url) {
//            view.loadUrl(url);
//            return true;
//        }
//    }
//
//    class C20477 implements VideoEnabledWebChromeClient.ToggledFullscreenCallback {
//        C20477() {
//        }
//
//        public void toggledFullscreen(boolean z) {
//            if (z) {
//                ExampleActivity.this.setRequestedOrientation(0);
//                WindowManager.LayoutParams attributes = ExampleActivity.this.getWindow().getAttributes();
//                attributes.flags |= 1024;
//                attributes.flags |= 128;
//                ExampleActivity.this.getWindow().setAttributes(attributes);
//                ExampleActivity.this.getWindow().getDecorView().setSystemUiVisibility(1);
//                return;
//            }
//            ExampleActivity.this.setRequestedOrientation(1);
//            WindowManager.LayoutParams attributes = ExampleActivity.this.getWindow().getAttributes();
//            attributes.flags &= -1025;
//            attributes.flags &= -129;
//            ExampleActivity.this.getWindow().setAttributes(attributes);
//            ExampleActivity.this.getWindow().getDecorView().setSystemUiVisibility(0);
//        }
//    }
//
//    @Override
//    public void onBackPressed() {
//        // Notify the VideoEnabledWebChromeClient, and handle it ourselves if it doesn't handle it
//        if (!webChromeClient.onBackPressed()) {
//            if (webView.canGoBack()) {
//                webView.goBack();
//            } else {
//                // Standard back button implementation (for example this could close the app)
//                super.onBackPressed();
//            }
//        }
//    }

}