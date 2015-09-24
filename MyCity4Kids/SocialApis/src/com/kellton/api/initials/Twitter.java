package com.kellton.api.initials;

import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.MailTo;
import android.net.Uri;
import android.net.http.SslError;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.webkit.GeolocationPermissions;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.kellton.api.interfaces.ITLogin;
import com.kellton.api.model.TwitterUser;
import com.kellton.socialapis.BuildConfig;
import com.kellton.socialapis.R;

public class Twitter extends DefaultClass {

	private static Twitter _up;
	private final static String LOG_TAG = "Twitter";
	private String key;
	private String secret;
	private String callbackURL;
	private WebView web_view;
	private ITLogin login;

	private RequestToken requestToken;
	private AccessToken accessToken;
	private twitter4j.Twitter twitter;

	/**
	 * @return the callbackURL
	 */
	public String getCallbackURL() {
		return callbackURL;
	}

	/**
	 * @param callbackURL
	 *            the callbackURL to set
	 */
	public void setCallbackURL(String callbackURL) {
		this.callbackURL = callbackURL;
	}

	/**
	 * @return the key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * @param key
	 *            the key to set
	 */
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * @return the secret
	 */
	public String getSecret() {
		return secret;
	}

	/**
	 * @param secret
	 *            the secret to set
	 */
	public void setSecret(String secret) {
		this.secret = secret;
	}

	/**
	 * @return the context
	 */
	public Context getContext() {
		return context;
	}

	/**
	 * @param context
	 *            the context to set
	 */
	public void setContext(Context context) {
		this.context = context;
	}

	private Twitter() {

	}

	private Twitter(String consumer_key, String consumer_secret, String callback_url, Context context) {
		_up = new Twitter();
		key = consumer_key;
		secret = consumer_secret;
		callbackURL = callback_url;
		this.context = context;
		ConfigurationBuilder builder = new ConfigurationBuilder();
		builder.setOAuthConsumerKey(key);
		builder.setOAuthConsumerSecret(secret);
		Configuration configuration = builder.build();
		TwitterFactory factory = new TwitterFactory(configuration);
		twitter = factory.getInstance();
	}

	public static Twitter getInstance(String consumer_key, String consumer_secret, String callback_url, Context context) {
		// if (_up == null) {
		// _up = new Twitter(consumer_key, consumer_secret, callback_url ,
		// context);
		// }
		_up = new Twitter(consumer_key, consumer_secret, callback_url, context);
		return _up;
	}

	public void doLogin(final ITLogin ilogin, final String tweetPostMessage) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					showProgress("Loading...");
					login = ilogin;
					requestToken = twitter.getOAuthRequestToken(callbackURL);
					final String authURL = requestToken.getAuthenticationURL();

					((Activity) context).runOnUiThread(new Runnable() {
						@Override
						public void run() {

							web_view = new WebView(context);
							// web_view.getSettings().setDefaultFontSize(16);
							// web_view.getSettings().setBuiltInZoomControls(true);
							// web_view.getSettings().setGeolocationEnabled(true);
							web_view.getSettings().setJavaScriptEnabled(true);
							// web_view.getSettings().setJavaScriptCanOpenWindowsAutomatically(false);

							/**
							 * DOM Storage, Cache mode and RenderPriority.
							 */
							web_view.getSettings().setDomStorageEnabled(true);
							// web_view.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);

							/**
							 * Enable AppCache. Set cache size to 8 mb by
							 * default. should be more than enough.
							 */
							String appCachePath = context.getCacheDir().getAbsolutePath();
							// web_view.getSettings().setAppCachePath(appCachePath);
							// web_view.getSettings().setAllowFileAccess(true);

							/**
							 * Enable Database and set Database path.
							 */

							web_view.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);

							// _internationalWebView.addJavascriptInterface(new
							// MmtJsDelegate(),AppConstants.MMT_JS_DELEGATE_NAME);

							/**
							 * WebChromeClient with overridden methods for
							 * updating quota.
							 */
							/*web_view.setWebChromeClient(new WebChromeClient() {
								@Override
								public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
									callback.invoke(origin, true, false);
								}

								@Override
								public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
									super.onJsAlert(view, url, message, result);
									return false;
								}
							});*/
							
							web_view.setWebChromeClient(new WebChromeClient());
							web_view.setWebViewClient(new MeetUpWebViewClient(tweetPostMessage));

							web_view.setFocusable(true);
							web_view.setFocusableInTouchMode(true);
							web_view.requestFocus();
							ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
							((Activity) context).addContentView(web_view, params);
							web_view.loadUrl(authURL);
						}
					});
				} catch (Exception e) {
					removeProgress();
					e.printStackTrace();
				}
			}
		}).start();
	}

	/**
	 *
	 */
	private class MeetUpWebViewClient extends WebViewClient {
		String mTweetmessage;

		public MeetUpWebViewClient(String tweetMessage) {
			mTweetmessage = tweetMessage;
		}

		@Override
		public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
			try {

				if (BuildConfig.DEBUG) {
					Log.i(LOG_TAG, "onReceivedError: " + failingUrl + ", " + description);
				}
				super.onReceivedError(view, errorCode, description, failingUrl);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {

			handler.proceed();
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			super.onPageStarted(view, url, favicon);
			try {

				if (BuildConfig.DEBUG) {
					Log.i(LOG_TAG, "onPageStarted: " + url);
				}
				showProgress("Loading...");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			try {

				Log.i(LOG_TAG, "shouldOverrideUrlLoading: " + url);

				/**
				 * Opening dialer screen. It does not require n/w check.
				 */
				if (url.startsWith("tel:")) {
					Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(url));
					context.startActivity(intent);
					return true;
				}

				/**
				 * Opening mail screen. It does not require n/w check.
				 */
				if (url.startsWith("mailto:")) {
					MailTo mt = MailTo.parse(url);
					Intent intent = new Intent(Intent.ACTION_SEND);
					intent.putExtra(Intent.EXTRA_EMAIL, new String[] { mt.getTo() });
					intent.putExtra(Intent.EXTRA_TEXT, mt.getBody());
					intent.putExtra(Intent.EXTRA_SUBJECT, mt.getSubject());
					intent.putExtra(Intent.EXTRA_CC, mt.getCc());
					intent.setType("message/rfc822");
					context.startActivity(intent);
					view.reload();
					return true;
				}
				if (url.equals("https://www.twitter.com/") || url.equals("https://www.twitter.com") || url.equals("http://www.twitter.com/")
						|| url.equals("http://www.twitter.com")) {
					web_view.setVisibility(View.GONE);
					// web_view.destroy();
					removeProgress();
					return true;
				}
				if (url.startsWith(callbackURL)) {
					web_view.setVisibility(View.GONE);
					// web_view.destroy();
					removeProgress();
					fetchUser(url, mTweetmessage);
					return true;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return false;
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
			try {

				// Set cookie for maintain login state
				if (BuildConfig.DEBUG) {
					Log.i(LOG_TAG, "onPageFinished: " + url);
				}
				removeProgress();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void fetchUser(final String url, final String tweetMessage) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					final TwitterUser twitterUser = new TwitterUser();
					Uri uri = Uri.parse(url);
					accessToken = twitter.getOAuthAccessToken(requestToken, uri.getQueryParameter("oauth_verifier"));
					Log.e(LOG_TAG, accessToken.getToken());
					twitterUser.setUserID(accessToken.getUserId());
					twitterUser.setUser(twitter.showUser(twitterUser.getUserID()));
					twitterUser.setToken(accessToken.getToken());
					twitterUser.setSecret(accessToken.getTokenSecret());
					try {
						/*
						 * twitter.updateStatus(getContext() .getString(
						 * R.string.twitter_description));
						 */
						twitter.updateStatus(tweetMessage);

					} catch (TwitterException e) {
						e.printStackTrace();
					}
					((Activity) context).runOnUiThread(new Runnable() {
						@Override
						public void run() {
							login.success(twitterUser);
							removeProgress();
						}
					});

				} catch (Exception e) {
					e.printStackTrace();
					login.success(null);
				}
			}
		}).start();

	}
}
