package com.mycity4kids.ui.adapter;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.models.request.FollowUnfollowUserRequest;
import com.mycity4kids.models.response.FollowUnfollowUserResponse;
import com.mycity4kids.models.response.FollowersFollowingResult;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.ui.CircleTransformation;
import com.mycity4kids.ui.activity.FollowersAndFollowingListActivity;
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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by hemant on 1/8/16.
 */
public class FollowerFollowingListAdapter extends BaseAdapter {

    private LayoutInflater mInflator;
    private Context mContext;
    private ArrayList<FollowersFollowingResult> mDataList;
    private final float density;
    Retrofit retrofit;

    public FollowerFollowingListAdapter(Context mContext) {
        density = mContext.getResources().getDisplayMetrics().density;
        mInflator = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mContext = mContext;
        retrofit = BaseApplication.getInstance().getRetrofit();
    }

    public void setData(ArrayList<FollowersFollowingResult> mDataList) {
        this.mDataList = mDataList;
    }

    @Override
    public int getCount() {
        return mDataList == null ? 0 : mDataList.size();
    }

    @Override
    public Object getItem(int position) {
        return mDataList.get(position);
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
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        if (view == null) {
            view = mInflator.inflate(R.layout.follower_following_list_item, null);
            holder = new ViewHolder();
            holder.authorImageView = (ImageView) view.findViewById(R.id.authorImageView);
            holder.authorNameTextView = (TextView) view.findViewById(R.id.authorNameTextView);
            holder.followTextView = (TextView) view.findViewById(R.id.followTextView);
            holder.followingTextView = (TextView) view.findViewById(R.id.followingTextView);
            holder.imgLoader = (ImageView) view.findViewById(R.id.imgLoader);
            holder.relativeLoadingView = (RelativeLayout) view.findViewById(R.id.relativeLoadingView);
            holder.imgLoader = (ImageView) holder.relativeLoadingView.findViewById(R.id.imgLoader);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        holder.imgLoader.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.rotate_indefinitely));
//        RotateAnimation rotateAnimation = new RotateAnimation(30, 90,
//                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
//        rotateAnimation.setRepeatCount(Animation.INFINITE);
//        holder.imgLoader.startAnimation(rotateAnimation);
        holder.authorNameTextView.setText(mDataList.get(position).getFirstName() + mDataList.get(position).getLastName());
        holder.position = position;
        if (!StringUtils.isNullOrEmpty(mDataList.get(position).getProfilePicUrl().getClientApp())) {
            try {
                Picasso.with(mContext).load(mDataList.get(position).getProfilePicUrl().getClientApp()).transform(new CircleTransformation()).into(holder.authorImageView);
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
                Picasso.with(mContext).load(R.drawable.default_commentor_img).transform(new CircleTransformation()).into(holder.authorImageView);
            }
        } else {
            Picasso.with(mContext).load(R.drawable.default_commentor_img).transform(new CircleTransformation()).into(holder.authorImageView);
        }

        if (mDataList.get(position).getIsFollowed() == 0) {
            holder.followingTextView.setVisibility(View.INVISIBLE);
            holder.followTextView.setVisibility(View.VISIBLE);
            if (mDataList.get(position).getUserId().equals(SharedPrefUtils.getUserDetailModel(mContext).getDynamoId()))
            {
                holder.followingTextView.setVisibility(View.INVISIBLE);
                holder.followTextView.setVisibility(View.INVISIBLE);
            }
        } else {
            holder.followingTextView.setVisibility(View.VISIBLE);
            holder.followTextView.setVisibility(View.INVISIBLE);
        }

        holder.followingTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Unfollow", "Unfollow");
                followUserAPI(position, holder);

            }
        });

        holder.followTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Follow", "Follow");
                followUserAPI(position, holder);
//                holder.followingTextView.setVisibility(View.VISIBLE);
//                holder.followTextView.setVisibility(View.INVISIBLE);
            }
        });
        return view;
    }

    private void followUserAPI(int position, ViewHolder holder) {
        FollowUnfollowUserRequest followUnfollowUserRequest = new FollowUnfollowUserRequest();
        followUnfollowUserRequest.setFollowerId(mDataList.get(position).getUserId());
        if (mDataList.get(position).getIsFollowed() == 0) {
            holder.relativeLoadingView.setVisibility(View.VISIBLE);
            holder.followingTextView.setVisibility(View.INVISIBLE);
            holder.followTextView.setVisibility(View.INVISIBLE);
            String jsonString = new Gson().toJson(followUnfollowUserRequest);
            new FollowUnfollowAsyncTask(holder, "follow", position).execute(jsonString, "follow");
        } else {
            holder.relativeLoadingView.setVisibility(View.VISIBLE);
            holder.followingTextView.setVisibility(View.INVISIBLE);
            holder.followTextView.setVisibility(View.INVISIBLE);
            String jsonString = new Gson().toJson(followUnfollowUserRequest);
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

            String JsonResponse = null;
            String JsonDATA = strings[0];

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            try {
                URL url;
                if ("follow".equals(strings[1])) {
                    url = new URL(AppConstants.BASE_URL + "/v1/users/followers/");
                } else {
                    url = new URL(AppConstants.BASE_URL + "/v1/users/unfollow/");
                }

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);
                // is output buffer writter
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Accept", "application/json");
                urlConnection.addRequestProperty("id", SharedPrefUtils.getUserDetailModel(mContext).getDynamoId());
                urlConnection.addRequestProperty("mc4kToken", SharedPrefUtils.getUserDetailModel(mContext).getMc4kToken());

//set headers and method
                Writer writer = new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream(), "UTF-8"));
                writer.write(JsonDATA);
// json data
                writer.close();
                InputStream inputStream = urlConnection.getInputStream();
//input stream
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String inputLine;
                while ((inputLine = reader.readLine()) != null)
                    buffer.append(inputLine + "\n");
                if (buffer.length() == 0) {
                    // Stream was empty. No point in parsing.
                    return null;
                }
                JsonResponse = buffer.toString();

                Log.i("RESPONSE " + type, JsonResponse);
//send to post execute
                return JsonResponse;
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
                    for (int i = 0; i < mDataList.size(); i++) {
                        if (mDataList.get(i).getUserId().equals(responseData.getData().getResult().getId())) {
                            if ("follow".equals(type)) {
                                mDataList.get(i).setIsFollowed(1);
                                viewHolder.relativeLoadingView.setVisibility(View.GONE);
                                viewHolder.followingTextView.setVisibility(View.VISIBLE);
                                viewHolder.followTextView.setVisibility(View.INVISIBLE);
                            } else {
                                mDataList.get(i).setIsFollowed(0);
                                viewHolder.relativeLoadingView.setVisibility(View.GONE);
                                viewHolder.followTextView.setVisibility(View.VISIBLE);
                                viewHolder.followingTextView.setVisibility(View.INVISIBLE);
                            }
//                            notifyDataSetChanged();
                        }
                    }
                } else {
                    resetFollowUnfollowStatus();
                }
            } catch (Exception e) {
                resetFollowUnfollowStatus();
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

    private Callback<FollowUnfollowUserResponse> followResponseCallback = new Callback<FollowUnfollowUserResponse>() {
        @Override
        public void onResponse(Call<FollowUnfollowUserResponse> call, Response<FollowUnfollowUserResponse> response) {
            if (response == null || response.body() == null) {
                ((FollowersAndFollowingListActivity) mContext).showToast("Something went wrong");
                return;
            }
            try {
                FollowUnfollowUserResponse responseData = (FollowUnfollowUserResponse) response.body();
                if (responseData.getCode() == 200 & Constants.SUCCESS.equals(responseData.getStatus())) {
//                    holder.followingTextView.setVisibility(View.VISIBLE);
//                    holder.followTextView.setVisibility(View.INVISIBLE);
                    for (int i = 0; i < mDataList.size(); i++) {
                        if (mDataList.get(i).getUserId().equals(responseData.getData().getResult().getId())) {
                            mDataList.get(i).setIsFollowed(1);
                            notifyDataSetChanged();
                        }
                    }
                }
//                processFollowingListResponse(responseData);
            } catch (Exception e) {
//                holder.followingTextView.setVisibility(View.INVISIBLE);
//                holder.followTextView.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onFailure(Call<FollowUnfollowUserResponse> call, Throwable t) {

        }
    };

    private Callback<FollowUnfollowUserResponse> unfollowResponseCallback = new Callback<FollowUnfollowUserResponse>() {
        @Override
        public void onResponse(Call<FollowUnfollowUserResponse> call, Response<FollowUnfollowUserResponse> response) {
            if (response == null || response.body() == null) {
                ((FollowersAndFollowingListActivity) mContext).showToast("Something went wrong");
                return;
            }
            try {
                FollowUnfollowUserResponse responseData = (FollowUnfollowUserResponse) response.body();
                if (responseData.getCode() == 200 & Constants.SUCCESS.equals(responseData.getStatus())) {
//                    holder.followingTextView.setVisibility(View.INVISIBLE);
//                    holder.followTextView.setVisibility(View.VISIBLE);
                    for (int i = 0; i < mDataList.size(); i++) {
                        if (mDataList.get(i).getUserId().equals(responseData.getData().getResult().getId())) {
                            mDataList.get(i).setIsFollowed(0);
                            notifyDataSetChanged();
                        }
                    }
                }
//                processFollowingListResponse(responseData);
            } catch (Exception e) {
//                holder.followingTextView.setVisibility(View.VISIBLE);
//                holder.followTextView.setVisibility(View.INVISIBLE);
            }
        }

        @Override
        public void onFailure(Call<FollowUnfollowUserResponse> call, Throwable t) {

        }
    };
}
