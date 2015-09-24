package com.kelltontech.utils.facebook.model;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;

import com.facebook.FacebookAuthorizationException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.FacebookRequestError;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Request.GraphUserCallback;
import com.facebook.Request.GraphUserListCallback;
import com.facebook.RequestAsyncTask;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;
import com.facebook.widget.FacebookDialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kelltontech.utils.facebook.listener.FacebookFriendListListener;
import com.kelltontech.utils.facebook.listener.FacebookLoginListener;
import com.kelltontech.utils.facebook.listener.FacebookPostListener;
import com.mycity4kids.R;
import com.mycity4kids.enums.ParentingFilterType;
import com.mycity4kids.interfaces.IFacebookUser;

/**
 * 
 * @author monish.agarwal created: 15-oct-2013
 * 
 */
public class FacebookUtils {
	private static final List<String> PERMISSIONS = Arrays
			.asList("publish_actions");
	private static Activity mActivity;

	static UserInfo mUserInfo;
	static List<FriendList> mFriendList;

	private static PendingAction pendingAction = PendingAction.NONE;

	private enum PendingAction {
		NONE, POST_PHOTO, POST_STATUS_UPDATE
	}

	private static boolean isSubsetOf(Collection<String> subset,
			Collection<String> superset) {
		for (String string : subset) {
			if (!superset.contains(string)) {
				return false;
			}
		}
		return true;
	}

	public static void postOnWall(Activity pActivity,
			final FacebookPostListener facebookPostListener,final String appUrl,final String title) {
		mActivity = pActivity;

		Session session = Session.getActiveSession();
		if (session.getState().isOpened()) {
			if (session != null) {

				// Check for publish permissions
				List<String> permissions = session.getPermissions();
				if (!isSubsetOf(PERMISSIONS, permissions)) {

					Session.NewPermissionsRequest newPermissionsRequest = new Session.NewPermissionsRequest(
							mActivity, PERMISSIONS);
					session.requestNewPublishPermissions(newPermissionsRequest);
					return;
				}

				Bundle postParams = new Bundle();
				postParams.putString("name", pActivity.getString(R.string.post_title));
				postParams.putString("caption","");
				
				postParams.putString("description",title+ " on mycity4kids. Check it out here " +appUrl);
				postParams.putString("link","https://www.facebook.com/mycity4kids");
				postParams.putString("picture","http://cdn1.mycity4kids.com/img/mycity4kids-logo.png");

				Request.Callback callback = new Request.Callback() {
					public void onCompleted(Response response) {
						JSONObject graphResponse = response.getGraphObject().getInnerJSONObject();
								
						String postId = null;
						try {
							postId = graphResponse.getString("id");
						} catch (JSONException e) {
							e.printStackTrace();
						}
						FacebookRequestError error = response.getError();

						if (error != null) {
							facebookPostListener.doAfterPostOnWall(true);

						} else {
							facebookPostListener.doAfterPostOnWall(false);
						}

					}
				};

				Request request = new Request(session, "me/feed", postParams,
						HttpMethod.POST, callback);

				RequestAsyncTask task = new RequestAsyncTask(request);
				task.execute();
			}
		}
		else
		{
			loginFacebook(mActivity, new FacebookLoginListener() {

				@Override
				public void doAfterLogin(UserInfo userInfo) {

					postOnWall(mActivity, facebookPostListener,appUrl,title);

				}
			});
		}

	}

	public static void getFriendList(Activity pActivity,
			final FacebookFriendListListener facebookFriendListListener) {
		mActivity = pActivity;

		Session activeSession = Session.getActiveSession();
		if (activeSession.getState().isOpened()) {
			Request friendRequest = Request.newMyFriendsRequest(activeSession,
					new GraphUserListCallback() {

						@Override
						public void onCompleted(List<GraphUser> users,
								Response response) {

							System.out.println(response.getGraphObject()
									.getProperty("data"));

							Gson gson = new Gson();

							List<FriendList> friendLists = gson.fromJson(
									response.getGraphObject()
											.getProperty("data").toString(),
									new TypeToken<List<FriendList>>() {
									}.getType());
							facebookFriendListListener
									.doAfterFriendList(friendLists);
							System.out.println(friendLists.get(0).getName());

						}
					});
			Bundle params = new Bundle();
			params.putString("fields", "id,name,picture");
			friendRequest.setParameters(params);
			friendRequest.executeAsync();

		} else {
			loginFacebook(mActivity, new FacebookLoginListener() {

				@Override
				public void doAfterLogin(UserInfo userInfo) {

					getFriendList(mActivity, facebookFriendListListener);

				}
			});
		}

	}
	
	

	public static void loginFacebook(Activity pActivity,
			final FacebookLoginListener facebookListener) {

		mActivity = pActivity;

		Session.openActiveSession(pActivity, true,
				new Session.StatusCallback() {

					@Override
					public void call(Session session, SessionState state,
							Exception exception) {
						// TODO Auto-generated method stub
						if (session.isOpened()) {
							Request.newMeRequest(session,
									new GraphUserCallback() {

										@Override
										public void onCompleted(GraphUser user,
												Response response) {
											// TODO Auto-generated method stub
											if (user != null) {

												System.out.println(user
														.getInnerJSONObject());

												Gson gson = new Gson();

												UserInfo userInfo = gson
														.fromJson(
																user.getInnerJSONObject()
																		.toString(),
																UserInfo.class);

												facebookListener
														.doAfterLogin(userInfo);

											}
										}
									}).executeAsync();

						}
					}
				});

	}

	public static void logout() {

		Session session = Session.getActiveSession();
		if (session != null)
			session.closeAndClearTokenInformation();

	}

	public static Session.StatusCallback callback = new Session.StatusCallback() {
		@Override
		public void call(Session session, SessionState state,
				Exception exception) {
			onSessionStateChange(session, state, exception);
		}
	};

	private static void onSessionStateChange(Session session,
			SessionState state, Exception exception) {
		if (pendingAction != PendingAction.NONE
				&& (exception instanceof FacebookOperationCanceledException || exception instanceof FacebookAuthorizationException)) {
			new AlertDialog.Builder(mActivity).setTitle(R.string.cancelled)
					.setMessage(R.string.permission_not_granted)
					.setPositiveButton(R.string.ok, null).show();
			pendingAction = PendingAction.NONE;
		} else if (state == SessionState.OPENED_TOKEN_UPDATED) {

		}
		updateUI();
	}

	public static void updateUI() {// to update UI at time of login and
		// logout....................
		Session session = Session.getActiveSession();

	}

	public static FacebookDialog.Callback dialogCallback = new FacebookDialog.Callback() {
		@Override
		public void onError(FacebookDialog.PendingCall pendingCall,
				Exception error, Bundle data) {
			Log.d("HelloFacebook", String.format("Error: %s", error.toString()));
		}

		@Override
		public void onComplete(FacebookDialog.PendingCall pendingCall,
				Bundle data) {
			Log.d("HelloFacebook", "Success!");
		}
	};
}
