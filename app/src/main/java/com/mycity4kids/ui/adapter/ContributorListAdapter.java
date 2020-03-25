package com.mycity4kids.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.mycity4kids.R;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.request.FollowUnfollowUserRequest;
import com.mycity4kids.models.response.ContributorListResult;
import com.mycity4kids.models.response.FollowUnfollowUserResponse;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.utils.RoundedTransformation;
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

/**
 * Created by manish.soni on 27-07-2015.
 */
public class ContributorListAdapter extends BaseAdapter {

    Context context;
    ArrayList<ContributorListResult> datalist;

    public ContributorListAdapter(Context context, ArrayList<ContributorListResult> datalist) {
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
                    holder.authorType.setTextColor(ContextCompat.getColor(context, R.color.black_color));
                    break;
                case AppConstants.USER_TYPE_ADMIN:
                    holder.authorType.setText("Admin");
                    holder.authorType.setTextColor(ContextCompat.getColor(context, R.color.black_color));
                    break;
                case AppConstants.USER_TYPE_CITY_ADMIN:
                    holder.authorType.setText("City Admin");
                    holder.authorType.setTextColor(ContextCompat.getColor(context, R.color.black_color));
                    break;
                case AppConstants.USER_TYPE_BLOGGER:
                    holder.authorType.setText("Blogger");
                    holder.authorType
                            .setTextColor(ContextCompat.getColor(context, R.color.authortype_colorcode_blogger));
                    break;
                case AppConstants.USER_TYPE_BUSINESS:
                    holder.authorType.setText("Business");
                    holder.authorType.setTextColor(ContextCompat.getColor(context, R.color.black_color));
                    break;
                case AppConstants.USER_TYPE_EDITOR:
                    holder.authorType.setText("Editor");
                    holder.authorType
                            .setTextColor(ContextCompat.getColor(context, R.color.authortype_colorcode_editor));
                    break;
                case AppConstants.USER_TYPE_EDITORIAL:
                    holder.authorType.setText("Editorial Team");
                    holder.authorType
                            .setTextColor(ContextCompat.getColor(context, R.color.authortype_colorcode_editorial));
                    break;
                case AppConstants.USER_TYPE_EXPERT:
                    holder.authorType.setText("Expert");
                    holder.authorType
                            .setTextColor(ContextCompat.getColor(context, R.color.authortype_colorcode_expert));
                    break;
                case AppConstants.USER_TYPE_REPORT_MANAGER:
                    holder.authorType.setText("Report Manager");
                    holder.authorType.setTextColor(ContextCompat.getColor(context, R.color.black_color));
                    break;
                case AppConstants.USER_TYPE_FEATURED:
                    holder.authorType.setText(AppConstants.AUTHOR_TYPE_FEATURED);
                    holder.authorType
                            .setTextColor(ContextCompat.getColor(context, R.color.authortype_colorcode_featured));
                    break;
                default:
                    holder.authorType.setText("Blogger");
                    holder.authorType
                            .setTextColor(ContextCompat.getColor(context, R.color.authortype_colorcode_blogger));
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

        if ((datalist.get(position).getProfilePic() == null)) {
            Picasso.get().load(R.drawable.default_commentor_img).fit().placeholder(R.drawable.default_commentor_img)
                    .transform(new RoundedTransformation()).into(holder.bloggerCover);
        } else {
            try {
                Picasso.get().load(datalist.get(position).getProfilePic().getClientApp()).fit()
                        .placeholder(R.drawable.default_commentor_img).transform(new RoundedTransformation())
                        .into(holder.bloggerCover);
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
                Picasso.get().load(R.drawable.blog_bgnew).fit().placeholder(R.drawable.blog_bgnew)
                        .transform(new RoundedTransformation()).into(holder.bloggerCover);
            }
        }
        if (StringUtils.isNullOrEmpty(datalist.get(position).getAbout())) {
            holder.bloggerBio.setVisibility(View.INVISIBLE);
        } else {
            holder.bloggerBio.setVisibility(View.VISIBLE);
            holder.bloggerBio.setText(datalist.get(position).getAbout());
        }

        if (!StringUtils.isNullOrEmpty(String.valueOf(datalist.get(position).getRank()))) {
            holder.authorRank.setText(String.valueOf(datalist.get(position).getRank()));
        } else {
            holder.authorRank.setText("--");
        }
        holder.followersCount.setText(datalist.get(position).getFollowersCount() + "");

        if (datalist.get(position).getIsFollowed() == 0) {
            holder.bloggerFollow.setText(context.getString(R.string.ad_follow_author));
        } else {
            holder.bloggerFollow.setText(context.getString(R.string.ad_following_author));
        }
        holder.bloggerFollow.setOnClickListener(v -> {
            Log.d("Follow", "Follow");
            followUserApi(position, holder);
        });

        return view;
    }

    private void followUserApi(int position, ViewHolder holder) {
        FollowUnfollowUserRequest followUnfollowUserRequest = new FollowUnfollowUserRequest();
        followUnfollowUserRequest.setFollowee_id(datalist.get(position).getId());
        if (datalist.get(position).getIsFollowed() == 0) {
            holder.relativeLoadingView.setVisibility(View.VISIBLE);
            holder.bloggerFollow.setVisibility(View.GONE);
            String jsonString = new Gson().toJson(followUnfollowUserRequest);
            Utils.pushFollowAuthorEvent(context, "ContributorsScreen",
                    SharedPrefUtils.getUserDetailModel(context).getDynamoId(),
                    datalist.get(position).getId() + "-" + datalist.get(position).getFirstName() + " " + datalist
                            .get(position).getLastName());
            new FollowUnfollowAsyncTask(holder, "follow", position).execute(jsonString, "follow");
        } else {
            holder.relativeLoadingView.setVisibility(View.VISIBLE);
            holder.bloggerFollow.setVisibility(View.GONE);
            String jsonString = new Gson().toJson(followUnfollowUserRequest);
            Utils.pushUnfollowAuthorEvent(context, "ContributorsScreen",
                    SharedPrefUtils.getUserDetailModel(context).getDynamoId(),
                    datalist.get(position).getId() + "-" + datalist.get(position).getFirstName() + " " + datalist
                            .get(position).getLastName());
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
            String jsondata = strings[0];

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
                writer.write(jsondata);
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
                resetFollowUnfollowStatus(pos);
                return;
            }
            try {
                FollowUnfollowUserResponse responseData = new Gson().fromJson(result, FollowUnfollowUserResponse.class);
                if (responseData.getCode() == 200 & Constants.SUCCESS.equals(responseData.getStatus())) {
                    for (int i = 0; i < datalist.size(); i++) {
                        if (datalist.get(i).getId().equals(responseData.getData().getResult())) {
                            if ("follow".equals(type)) {
                                datalist.get(i).setIsFollowed(1);
                                viewHolder.relativeLoadingView.setVisibility(View.GONE);
                                viewHolder.bloggerFollow.setVisibility(View.VISIBLE);
                                viewHolder.bloggerFollow.setText(context.getString(R.string.ad_following_author));
                                long followersCount = datalist.get(i).getFollowersCount() + 1;
                                datalist.get(i).setFollowersCount(followersCount);
                                viewHolder.followersCount.setText(followersCount + "");
                            } else {
                                datalist.get(i).setIsFollowed(0);
                                viewHolder.relativeLoadingView.setVisibility(View.GONE);
                                viewHolder.bloggerFollow.setVisibility(View.VISIBLE);
                                long followersCount = datalist.get(i).getFollowersCount() - 1;
                                datalist.get(i).setFollowersCount(followersCount);
                                viewHolder.followersCount.setText(followersCount + "");
                                viewHolder.bloggerFollow.setText(context.getString(R.string.ad_follow_author));
                            }
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
                viewHolder.bloggerFollow.setVisibility(View.VISIBLE);
                viewHolder.bloggerFollow.setText(context.getString(R.string.ad_follow_author));
                viewHolder.followersCount.setText(datalist.get(position).getFollowersCount() - 1 + "");
                datalist.get(position).setFollowersCount(datalist.get(position).getFollowersCount() - 1);

            } else {
                viewHolder.bloggerFollow.setVisibility(View.VISIBLE);
                viewHolder.bloggerFollow.setText(context.getString(R.string.ad_following_author));
                viewHolder.followersCount.setText(datalist.get(position).getFollowersCount() + 1 + "");
                datalist.get(position).setFollowersCount(datalist.get(position).getFollowersCount() + 1);
            }
        }

    }

}
