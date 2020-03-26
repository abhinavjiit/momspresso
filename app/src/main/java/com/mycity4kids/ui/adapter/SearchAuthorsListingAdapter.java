package com.mycity4kids.ui.adapter;

import android.content.Context;
import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.google.gson.Gson;
import com.mycity4kids.R;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.models.request.FollowUnfollowUserRequest;
import com.mycity4kids.models.response.FollowUnfollowUserResponse;
import com.mycity4kids.models.response.SearchAuthorResult;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.utils.StringUtils;
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
import java.util.List;

/**
 * Created by hemant on 19/4/16.
 */
public class SearchAuthorsListingAdapter extends BaseAdapter {

    private LayoutInflater layoutInflater;
    private Context context;
    private List<SearchAuthorResult> datalist;

    public SearchAuthorsListingAdapter(Context context) {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }

    public void setNewListData(ArrayList<SearchAuthorResult> datalist) {
        this.datalist = datalist;
    }

    static class Viewholder {

        TextView authorNameTextView;
        ImageView authorImageView;
        TextView followTextView;
        TextView followingTextView;
        RelativeLayout relativeLoadingView;
        int position;
    }

    @Override
    public int getCount() {
        return datalist == null ? 0 : datalist.size();
    }

    @Override
    public Object getItem(int position) {
        return datalist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final Viewholder viewholder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.search_author_list_item, null);
            viewholder = new Viewholder();
            viewholder.authorImageView = (ImageView) convertView.findViewById(R.id.authorImageView);
            viewholder.authorNameTextView = (TextView) convertView.findViewById(R.id.authorNameTextView);
            viewholder.followTextView = (TextView) convertView.findViewById(R.id.followTextView);
            viewholder.followingTextView = (TextView) convertView.findViewById(R.id.followingTextView);
            convertView.setTag(viewholder);
        } else {
            viewholder = (Viewholder) convertView.getTag();
        }

        if (null != datalist.get(position).getProfile_image() && !StringUtils
                .isNullOrEmpty(datalist.get(position).getProfile_image().getClientApp())) {
            try {
                Picasso.get().load(datalist.get(position).getProfile_image().getClientApp())
                        .placeholder(R.drawable.default_commentor_img).error(R.drawable.default_commentor_img)
                        .into(viewholder.authorImageView);
            } catch (Exception e) {
                e.printStackTrace();
                Picasso.get().load(R.drawable.default_commentor_img).into(viewholder.authorImageView);
            }
        } else {
            Picasso.get().load(R.drawable.default_commentor_img).into(viewholder.authorImageView);
        }

        viewholder.followTextView.setOnClickListener(v -> followUserApi(position, viewholder));

        viewholder.followingTextView.setOnClickListener(view -> followUserApi(position, viewholder));
        viewholder.authorNameTextView.setText(
                Html.fromHtml(datalist.get(position).getFirst_name() + " " + datalist.get(position).getLast_name()));
        return convertView;
    }

    private void followUserApi(int position, Viewholder holder) {
        FollowUnfollowUserRequest followUnfollowUserRequest = new FollowUnfollowUserRequest();
        followUnfollowUserRequest.setFollowee_id(datalist.get(position).getUserId());
        if (datalist.get(position).getIsFollowed() == 0) {
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
        Viewholder viewHolder;
        String type;
        int pos;

        public FollowUnfollowAsyncTask(Viewholder viewHolder, String type, int position) {
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
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String inputLine;
                while ((inputLine = reader.readLine()) != null) {
                    buffer.append(inputLine + "\n");
                }
                if (buffer.length() == 0) {
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
                    for (int i = 0; i < datalist.size(); i++) {
                        if (datalist.get(i).getUserId().equals(responseData.getData().getResult())) {
                            if ("follow".equals(type)) {
                                datalist.get(i).setIsFollowed(1);
                                viewHolder.relativeLoadingView.setVisibility(View.GONE);
                                viewHolder.followingTextView.setVisibility(View.VISIBLE);
                                viewHolder.followTextView.setVisibility(View.INVISIBLE);
                            } else {
                                datalist.get(i).setIsFollowed(0);
                                viewHolder.relativeLoadingView.setVisibility(View.GONE);
                                viewHolder.followTextView.setVisibility(View.VISIBLE);
                                viewHolder.followingTextView.setVisibility(View.INVISIBLE);
                            }
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
}
