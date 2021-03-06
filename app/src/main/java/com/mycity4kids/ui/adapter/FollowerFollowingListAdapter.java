package com.mycity4kids.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.BlockUserModel;
import com.mycity4kids.models.request.FollowUnfollowUserRequest;
import com.mycity4kids.models.response.FollowUnfollowUserResponse;
import com.mycity4kids.models.response.FollowersFollowingResult;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.ArticleDetailsAPI;
import com.mycity4kids.sync.SyncUserFollowingList;
import com.mycity4kids.utils.StringUtils;
import com.mycity4kids.utils.ToastUtils;
import com.squareup.picasso.Picasso;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by hemant on 1/8/16.
 */
public class FollowerFollowingListAdapter extends BaseAdapter {

    private LayoutInflater layoutInflater;
    private Context context;
    private ArrayList<FollowersFollowingResult> dataList;
    String currentUserId;
    private String eventName;

    public FollowerFollowingListAdapter(Context context, String eventName) {
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
        currentUserId = SharedPrefUtils.getUserDetailModel(context).getDynamoId();
        this.eventName = eventName;
    }

    public void setData(ArrayList<FollowersFollowingResult> dataList) {
        this.dataList = dataList;
    }

    @Override
    public int getCount() {
        return dataList == null ? 0 : dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    class ViewHolder {

        ImageView authorImageView;
        TextView authorNameTextView;
        TextView followTextView;
        TextView followingTextView;
        ImageView imgLoader;
        RelativeLayout relativeLoadingView;
        int position;
        ImageView menuImageView;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        if (view == null) {
            view = layoutInflater.inflate(R.layout.follower_following_list_item, null);
            holder = new ViewHolder();
            holder.authorImageView = (ImageView) view.findViewById(R.id.authorImageView);
            holder.authorNameTextView = (TextView) view.findViewById(R.id.authorNameTextView);
            holder.followTextView = (TextView) view.findViewById(R.id.followTextView);
            holder.followingTextView = (TextView) view.findViewById(R.id.followingTextView);
            holder.imgLoader = (ImageView) view.findViewById(R.id.imgLoader);
            holder.relativeLoadingView = (RelativeLayout) view.findViewById(R.id.relativeLoadingView);
            holder.imgLoader = (ImageView) holder.relativeLoadingView.findViewById(R.id.imgLoader);
            holder.menuImageView = (ImageView) view.findViewById(R.id.menuImageView);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        holder.imgLoader.startAnimation(AnimationUtils.loadAnimation(context, R.anim.rotate_indefinitely));

        holder.authorNameTextView
                .setText(dataList.get(position).getFirstName() + " " + dataList.get(position).getLastName());
        holder.position = position;
        if (!StringUtils.isNullOrEmpty(dataList.get(position).getProfilePicUrl().getClientApp())) {
            Picasso.get().load(dataList.get(position).getProfilePicUrl().getClientApp())
                    .placeholder(R.drawable.default_commentor_img).error(R.drawable.default_commentor_img)
                    .into(holder.authorImageView);
        } else {
            Picasso.get().load(R.drawable.default_commentor_img).into(holder.authorImageView);
        }

        try {
            if (dataList.get(position).getUserId().equals(currentUserId)) {
                holder.followingTextView.setVisibility(View.INVISIBLE);
                holder.followTextView.setVisibility(View.INVISIBLE);
            } else {
                if (!dataList.get(position).getIsFollowed()) {
                    holder.followingTextView.setVisibility(View.INVISIBLE);
                    holder.followTextView.setVisibility(View.VISIBLE);
                } else {
                    holder.followingTextView.setVisibility(View.VISIBLE);
                    holder.followTextView.setVisibility(View.INVISIBLE);
                }
            }
        } catch (Exception e) {
            holder.followingTextView.setVisibility(View.INVISIBLE);
            holder.followTextView.setVisibility(View.INVISIBLE);
        }

        holder.followingTextView.setOnClickListener(v -> {
            Log.d("Unfollow", "Unfollow");
            followUserApi(position, holder);

        });

        holder.followTextView.setOnClickListener(v -> {
            Log.d("Follow", "Follow");
            followUserApi(position, holder);
        });
        if (eventName.equals("SelfProfile_Followers_Follow")) {
            holder.menuImageView.setVisibility(View.VISIBLE);
        } else {
            holder.menuImageView.setVisibility(View.INVISIBLE);

        }

        holder.menuImageView.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(context, v);
            popupMenu.getMenuInflater().inflate(R.menu.block_user_menu, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(menuItem -> {
                if (menuItem.getItemId() == R.id.blockUserTextView) {
                    Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
                    ArticleDetailsAPI articleDetailsAPI = retrofit.create(ArticleDetailsAPI.class);
                    BlockUserModel blockUserModel = new BlockUserModel();
                    blockUserModel.setBlocked_user_id(dataList.get(position).getUserId());
                    Call<ResponseBody> call = articleDetailsAPI.blockUserApi(blockUserModel);
                    call.enqueue(blockUserCallBack);
                    dataList.remove(position);
                    notifyDataSetChanged();
                    return true;
                }
                return false;
            });

            popupMenu.show();
        });
        return view;
    }

    private Callback<ResponseBody> blockUserCallBack = new Callback<ResponseBody>() {

        @Override
        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
            if (null == response.body()) {
                return;
            }
            try {
                Log.d("Tag", "success");
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }

        }

        @Override
        public void onFailure(Call<ResponseBody> call, Throwable e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            Log.d("MC4kException", Log.getStackTraceString(e));
        }
    };


    private void followUserApi(int position, ViewHolder holder) {
        FollowUnfollowUserRequest followUnfollowUserRequest = new FollowUnfollowUserRequest();
        followUnfollowUserRequest.setFollowee_id(dataList.get(position).getUserId());
        if (!dataList.get(position).getIsFollowed()) {
            holder.relativeLoadingView.setVisibility(View.VISIBLE);
            holder.followingTextView.setVisibility(View.INVISIBLE);
            holder.followTextView.setVisibility(View.INVISIBLE);
            String jsonString = new Gson().toJson(followUnfollowUserRequest);
            Utils.shareEventTracking(holder.followTextView.getContext(), "Self Profile", "Follow_Android", eventName);
            new FollowUnfollowAsyncTask(holder, "follow", position).execute(jsonString, "follow");
        } else {
            holder.relativeLoadingView.setVisibility(View.VISIBLE);
            holder.followingTextView.setVisibility(View.INVISIBLE);
            holder.followTextView.setVisibility(View.INVISIBLE);
            String jsonString = new Gson().toJson(followUnfollowUserRequest);
            Utils.pushGenericEvent(context, "CTA_Unfollow_Profile_Followers",
                    SharedPrefUtils.getUserDetailModel(context).getDynamoId(), "FollowerFollowingListAdapter");
            new FollowUnfollowAsyncTask(holder, "unfollow", position).execute(jsonString, "unfollow");
        }
    }

    private class FollowUnfollowAsyncTask extends AsyncTask<String, String, String> {

        // The variable is moved here, we only need it here while displaying the
        // progress dialog.
        ViewHolder viewHolder;
        String type;
        int pos;

        public FollowUnfollowAsyncTask(ViewHolder viewHolder, String type, int position) {
            this.viewHolder = viewHolder;
            this.type = type;
            pos = position;
        }

        @Override
        protected String doInBackground(String... strings) {

            String jsonResponse = null;
            String jsonData = strings[0];

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            try {
                URL url;
                if ("follow".equals(strings[1])) {
                    url = new URL(AppConstants.BASE_URL + "follow/v2/users/follow");
                } else {
                    url = new URL(AppConstants.BASE_URL + "follow/v2/users/unfollow");
                }

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);
                // is output buffer writter
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Accept", "application/json");
                urlConnection.addRequestProperty("id", SharedPrefUtils.getUserDetailModel(context).getDynamoId());
                urlConnection
                        .addRequestProperty("mc4kToken", SharedPrefUtils.getUserDetailModel(context).getMc4kToken());

                Writer writer = new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream(), "UTF-8"));
                writer.write(jsonData);
                writer.close();
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String inputLine;
                while ((inputLine = reader.readLine()) != null) {
                    buffer.append(inputLine + "\n");
                }
                if (buffer.length() == 0) {
                    // Stream was empty. No point in parsing.
                    return null;
                }
                jsonResponse = buffer.toString();

                Log.i("RESPONSE " + type, jsonResponse);
                return jsonResponse;
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("TAAGG", "Error closing stream", e);
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {

            if (result == null) {
                resetFollowUnfollowStatus();
                return;
            }
            try {
                FollowUnfollowUserResponse responseData = new Gson().fromJson(result, FollowUnfollowUserResponse.class);
                if (responseData.getCode() == 200 & Constants.SUCCESS.equals(responseData.getStatus())) {
                    Intent followIntent = new Intent(viewHolder.relativeLoadingView.getContext(),
                            SyncUserFollowingList.class);
                    viewHolder.relativeLoadingView.getContext().startService(followIntent);
                    for (int i = 0; i < dataList.size(); i++) {
                        if (dataList.get(i).getUserId().equals(responseData.getData().getResult())) {
                            if ("follow".equals(type)) {
                                dataList.get(i).setIsFollowed(true);
                                viewHolder.relativeLoadingView.setVisibility(View.GONE);
                                viewHolder.followingTextView.setVisibility(View.VISIBLE);
                                viewHolder.followTextView.setVisibility(View.INVISIBLE);
                            } else {
                                dataList.get(i).setIsFollowed(false);
                                viewHolder.relativeLoadingView.setVisibility(View.GONE);
                                viewHolder.followTextView.setVisibility(View.VISIBLE);
                                viewHolder.followingTextView.setVisibility(View.INVISIBLE);
                            }
                        }
                    }
                    ToastUtils.showToast(context, responseData.getData().getMsg());
                } else {
                    ToastUtils.showToast(context, responseData.getData().getMsg());
                    resetFollowUnfollowStatus();
                }
            } catch (Exception e) {
                resetFollowUnfollowStatus();
                ToastUtils.showToast(context, e.getMessage());

            }
        }

        void resetFollowUnfollowStatus() {
            viewHolder.relativeLoadingView.setVisibility(View.GONE);
            if (type.equals("follow")) {
                viewHolder.followingTextView.setVisibility(View.INVISIBLE);
                viewHolder.followTextView.setVisibility(View.VISIBLE);

            } else {
                viewHolder.followingTextView.setVisibility(View.VISIBLE);
                viewHolder.followTextView.setVisibility(View.INVISIBLE);
            }
        }

    }
}
