package com.mycity4kids.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.models.request.FollowUnfollowUserRequest;
import com.mycity4kids.models.response.ContributorListResult;
import com.mycity4kids.models.response.FollowUnfollowUserResponse;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.ui.activity.DashboardActivity;
import com.mycity4kids.utils.RoundedTransformation;
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

/**
 * Created by manish.soni on 27-07-2015.
 */
public class ParentingBlogAdapter extends BaseAdapter {
    Context context;
    ArrayList<ContributorListResult> datalist;

    public ParentingBlogAdapter(Context context, ArrayList<ContributorListResult> datalist) {
        this.context = context;
        this.datalist = datalist;
    }

    @Override
    public int getCount() {
        return datalist == null ? 0 : datalist.size();
    }

    @Override
    public Object getItem(int i) {
        return datalist.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int position, final View convertView, ViewGroup viewGroup) {

        View view = convertView;
        final ViewHolder holder;
        if (view == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            view = inflater.inflate(R.layout.contributor_list_item, viewGroup, false);

            holder = new ViewHolder();
            holder.bloggerName = (TextView) view.findViewById(R.id.bloggerName);
            holder.authorType = (TextView) view.findViewById(R.id.userType);
            holder.authorRank = (TextView) view.findViewById(R.id.rank);
            holder.bloggerCover = (ImageView) view.findViewById(R.id.bloggerImageView);
            holder.bloggerFollow = (TextView) view.findViewById(R.id.blog_follow_text);
            holder.bloggerBio = (TextView) view.findViewById(R.id.bloggerBio);
            holder.followersCount = (TextView) view.findViewById(R.id.followersCount);
            holder.relativeLoadingView = (RelativeLayout) view.findViewById(R.id.relativeLoadingView);
            holder.rankText = (TextView) view.findViewById(R.id.rankText);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.bloggerName.setText(datalist.get(position).getFirstName() + " " + datalist.get(position).getLastName());
        if (!StringUtils.isNullOrEmpty(datalist.get(position).getUserType())) {
            switch (datalist.get(position).getUserType()) {
                case AppConstants.USER_TYPE_USER:
                    holder.authorType.setText("User");
                    break;
                case AppConstants.USER_TYPE_ADMIN:
                    holder.authorType.setText("Admin");
                    break;
                case AppConstants.USER_TYPE_CITY_ADMIN:
                    holder.authorType.setText("City Admin");
                    break;
                case AppConstants.USER_TYPE_BLOGGER:
                    holder.authorType.setText("Blogger");
                    break;
                case AppConstants.USER_TYPE_BUSINESS:
                    holder.authorType.setText("Business");
                    break;
                case AppConstants.USER_TYPE_EDITOR:
                    holder.authorType.setText("Editor");
                    break;
                case AppConstants.USER_TYPE_EDITORIAL:
                    holder.authorType.setText("Editorial Team");
                    break;
                case AppConstants.USER_TYPE_EXPERT:
                    holder.authorType.setText("Expert");
                    break;
                case AppConstants.USER_TYPE_REPORT_MANAGER:
                    holder.authorType.setText("Report Manager");
                    break;
                case AppConstants.USER_TYPE_FEATURED:
                    holder.authorType.setText(AppConstants.AUTHOR_TYPE_FEATURED);
                    break;
                default:
                    holder.authorType.setText("Blogger");
                    break;
            }
            if (!datalist.get(position).getUserType().equals(AppConstants.USER_TYPE_BLOGGER)) {
                holder.rankText.setVisibility(View.GONE);
                holder.authorRank.setVisibility(View.GONE);
            } else {
                holder.rankText.setVisibility(View.VISIBLE);
                holder.authorRank.setVisibility(View.VISIBLE);
            }

        }
        try {
            holder.authorType.setTextColor(Color.parseColor(datalist.get(position).getColorCode()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        if ((datalist.get(position).getProfilePic() == null)) {
            Picasso.with(context).load(R.drawable.default_commentor_img).fit().placeholder(R.drawable.default_commentor_img).transform(new RoundedTransformation()).into(holder.bloggerCover);
        } else {
            try {
                Picasso.with(context).load(datalist.get(position).getProfilePic().getClientApp()).fit().placeholder(R.drawable.default_commentor_img).transform(new RoundedTransformation()).into(holder.bloggerCover);
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
                Picasso.with(context).load(R.drawable.blog_bgnew).fit().placeholder(R.drawable.blog_bgnew).transform(new RoundedTransformation()).into(holder.bloggerCover);
            }

        }
        if (StringUtils.isNullOrEmpty(datalist.get(position).getAbout())) {
            holder.bloggerBio.setVisibility(View.INVISIBLE);
        } else {
            holder.bloggerBio.setVisibility(View.VISIBLE);
            //    holder.aboutLayout.setVisibility(View.VISIBLE);
            holder.bloggerBio.setText(datalist.get(position).getAbout());
        }

        if (!StringUtils.isNullOrEmpty(String.valueOf(datalist.get(position).getRank()))) {
            holder.authorRank.setText(String.valueOf(datalist.get(position).getRank()));
        } else {
            holder.authorRank.setText("--");
        }
        holder.followersCount.setText(datalist.get(position).getFollowersCount() + "");

        if (datalist.get(position).getIsFollowed() == 0) {
            holder.bloggerFollow.setText("FOLLOW");
        } else {
            holder.bloggerFollow.setText("FOLLOWING");
        }
        holder.bloggerFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Follow", "Follow");
                followUserAPI(position, holder);
            }
        });


        return view;
    }

    private void followUserAPI(int position, ViewHolder holder) {
        FollowUnfollowUserRequest followUnfollowUserRequest = new FollowUnfollowUserRequest();
        followUnfollowUserRequest.setFollowerId(datalist.get(position).getId());
        if (datalist.get(position).getIsFollowed() == 0) {
            holder.relativeLoadingView.setVisibility(View.VISIBLE);
            holder.bloggerFollow.setVisibility(View.GONE);
            String jsonString = new Gson().toJson(followUnfollowUserRequest);
            new FollowUnfollowAsyncTask(holder, "follow", position).execute(jsonString, "follow");
        } else {
            holder.relativeLoadingView.setVisibility(View.VISIBLE);
            holder.bloggerFollow.setVisibility(View.GONE);
            String jsonString = new Gson().toJson(followUnfollowUserRequest);
            new FollowUnfollowAsyncTask(holder, "unfollow", position).execute(jsonString, "unfollow");
        }
    }

    public static class ViewHolder {

        TextView bloggerName;
        TextView authorType;
        TextView authorRank;
        ImageView shareBlogImageView;
        ImageView bloggerCover;
        TextView bloggerFollow;
        TextView bloggerBio;
        TextView followersCount;
        RelativeLayout relativeLoadingView;
        TextView rankText;
        TextView moreDesc;
        LinearLayout recentArticleLayout;
        LinearLayout articleBlock;
        RelativeLayout aboutLayout;

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
                urlConnection.addRequestProperty("id", SharedPrefUtils.getUserDetailModel(context).getDynamoId());
                urlConnection.addRequestProperty("mc4kToken", SharedPrefUtils.getUserDetailModel(context).getMc4kToken());

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
                resetFollowUnfollowStatus(pos);
                return;
            }
            try {
                FollowUnfollowUserResponse responseData = new Gson().fromJson(result, FollowUnfollowUserResponse.class);
                if (responseData.getCode() == 200 & Constants.SUCCESS.equals(responseData.getStatus())) {
                    for (int i = 0; i < datalist.size(); i++) {
                        if (datalist.get(i).getId().equals(responseData.getData().getResult().getId())) {
                            if ("follow".equals(type)) {
                                datalist.get(i).setIsFollowed(1);
                                viewHolder.relativeLoadingView.setVisibility(View.GONE);
                                viewHolder.bloggerFollow.setVisibility(View.VISIBLE);
                                viewHolder.bloggerFollow.setText("FOLLOWING");
                                long followersCount=datalist.get(i).getFollowersCount()+1;
                                datalist.get(i).setFollowersCount(followersCount);
                                viewHolder.followersCount.setText(followersCount+"");
                                //  viewHolder.followTextView.setVisibility(View.INVISIBLE);
                            } else {
                                datalist.get(i).setIsFollowed(0);
                                viewHolder.relativeLoadingView.setVisibility(View.GONE);
                                viewHolder.bloggerFollow.setVisibility(View.VISIBLE);
                                long followersCount=datalist.get(i).getFollowersCount()-1;
                                datalist.get(i).setFollowersCount(followersCount);
                                viewHolder.followersCount.setText(followersCount+"");
                                viewHolder.bloggerFollow.setText("FOLLOW");
                            }
//                            notifyDataSetChanged();
                        }
                    }
                } else {
                    resetFollowUnfollowStatus(pos);
                }
            } catch (Exception e) {
                resetFollowUnfollowStatus(pos);
            }
        }

        void resetFollowUnfollowStatus(int position) {
            viewHolder.relativeLoadingView.setVisibility(View.GONE);
            if (type.equals("follow")) {
                //   viewHolder.followingTextView.setVisibility(View.INVISIBLE);
                viewHolder.bloggerFollow.setVisibility(View.VISIBLE);
                viewHolder.bloggerFollow.setText("FOLLOW");
                viewHolder.followersCount.setText(datalist.get(position).getFollowersCount()-1+"");
                datalist.get(position).setFollowersCount(datalist.get(position).getFollowersCount()-1);

            } else {
                viewHolder.bloggerFollow.setVisibility(View.VISIBLE);
                viewHolder.bloggerFollow.setText("FOLLOWING");
                viewHolder.followersCount.setText(datalist.get(position).getFollowersCount()+1+"");
                datalist.get(position).setFollowersCount(datalist.get(position).getFollowersCount()+1);
            }
        }

    }

}