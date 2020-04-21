package com.mycity4kids.ui.adapter;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.crashlytics.android.Crashlytics;
import com.mycity4kids.R;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.models.response.VlogsListingAndDetailResult;
import com.mycity4kids.utils.DateTimeUtils;
import com.mycity4kids.utils.StringUtils;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;

public class MyFunnyVideosListingAdapter extends BaseAdapter {

    private final IEditVlog editVlog;
    private boolean isPrivateProfile;
    ArrayList<VlogsListingAndDetailResult> articleDataModelsNew;

    public MyFunnyVideosListingAdapter(IEditVlog editVlog, boolean isPrivateProfile) {
        this.editVlog = editVlog;
        this.isPrivateProfile = isPrivateProfile;
    }

    public void setNewListData(ArrayList<VlogsListingAndDetailResult> articleDataModelsNew) {
        this.articleDataModelsNew = articleDataModelsNew;
    }

    @Override
    public int getCount() {
        return articleDataModelsNew == null ? 0 : articleDataModelsNew.size();
    }

    @Override
    public Object getItem(int position) {
        return articleDataModelsNew.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {

        try {
            final ViewHolder holder;
            if (view == null) {
                view = LayoutInflater.from(view.getContext()).inflate(R.layout.users_funny_video_item, null);
                holder = new ViewHolder();
                holder.rootView = (RelativeLayout) view.findViewById(R.id.rootView);
                holder.txvArticleTitle = (TextView) view.findViewById(R.id.articleTitleTextView);
                holder.articleImageView = (ImageView) view.findViewById(R.id.articleImageView);
                holder.shareImageView = (ImageView) view.findViewById(R.id.shareImageView);
                holder.dateTextView = (TextView) view.findViewById(R.id.dateTextView);
                holder.vlogOptionImageView = (ImageView) view.findViewById(R.id.vlogOptionImageView);
                holder.viewCountTextView = (TextView) view.findViewById(R.id.viewCountTextView);
                holder.commentCountTextView = (TextView) view.findViewById(R.id.commentCountTextView);
                holder.recommendCountTextView = (TextView) view.findViewById(R.id.recommendCountTextView);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }

            try {
                holder.viewCountTextView.setText("" + articleDataModelsNew.get(position).getView_count());
                holder.commentCountTextView.setText("" + articleDataModelsNew.get(position).getComment_count());
                holder.recommendCountTextView.setText("" + articleDataModelsNew.get(position).getLike_count());
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }

            if (!StringUtils.isNullOrEmpty(articleDataModelsNew.get(position).getTitle())) {
                holder.txvArticleTitle.setText(articleDataModelsNew.get(position).getTitle());
            } else {
                holder.txvArticleTitle.setText(articleDataModelsNew.get(position).getTitleSlug());
            }

            try {
                if (StringUtils.isNullOrEmpty(articleDataModelsNew.get(position).getUrl())) {
                    Picasso.get().load(R.drawable.default_article)
                            .placeholder(R.drawable.default_article).error(R.drawable.default_article)
                            .into(holder.articleImageView);
                } else {
                    Picasso.get().load(articleDataModelsNew.get(position).getThumbnail())
                            .placeholder(R.drawable.default_article).error(R.drawable.default_article)
                            .into(holder.articleImageView);
                }
            } catch (Exception e) {
                Crashlytics.logException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }

            if (AppConstants.VIDEO_STATUS_PUBLISHED.equals(articleDataModelsNew.get(position).getPublication_status())
                    && isPrivateProfile) {
                holder.shareImageView.setVisibility(View.VISIBLE);
                holder.vlogOptionImageView.setVisibility(View.VISIBLE);
                holder.dateTextView.setText(
                        holder.dateTextView.getContext().getString(R.string.user_funny_video_published_on, DateTimeUtils
                                .getDateFromTimestamp(
                                        Long.parseLong(articleDataModelsNew.get(position).getPublished_time()))));
            } else if (AppConstants.VIDEO_STATUS_APPROVAL_PENDING
                    .equals(articleDataModelsNew.get(position).getPublication_status())) {
                holder.shareImageView.setVisibility(View.GONE);
                holder.vlogOptionImageView.setVisibility(View.GONE);
                holder.dateTextView.setText(holder.dateTextView.getContext()
                        .getString(R.string.user_funny_video_pending_since, DateTimeUtils
                                .getDateFromTimestamp(
                                        Long.parseLong(articleDataModelsNew.get(position).getPublished_time()))));
            } else if (AppConstants.VIDEO_STATUS_APPROVAL_CANCELLED
                    .equals(articleDataModelsNew.get(position).getPublication_status())) {
                holder.shareImageView.setVisibility(View.GONE);
                holder.vlogOptionImageView.setVisibility(View.GONE);
                holder.dateTextView.setText(
                        holder.dateTextView.getContext().getString(R.string.user_funny_video_rejected_on, DateTimeUtils
                                .getDateFromTimestamp(
                                        Long.parseLong(articleDataModelsNew.get(position).getPublished_time()))));
            } else {
                holder.shareImageView.setVisibility(View.GONE);
                holder.vlogOptionImageView.setVisibility(View.GONE);
                holder.dateTextView
                        .setText(holder.dateTextView.getContext().getString(R.string.user_funny_video_unspecified, ""));
            }

            holder.vlogOptionImageView.setOnClickListener(
                    view1 -> editVlog.onVlogEdit(position, holder.vlogOptionImageView));

            holder.shareImageView.setOnClickListener(v -> {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                String shareUrl = articleDataModelsNew.get(position).getUrl();
                String shareMessage;
                if (StringUtils.isNullOrEmpty(shareUrl)) {
                    shareMessage = holder.shareImageView.getContext().getString(R.string.check_out_blog) + "\""
                            + articleDataModelsNew.get(position).getTitle() + "\" by " + articleDataModelsNew
                            .get(position).getAuthor().getFirstName() + ".";
                } else {
                    shareMessage = holder.shareImageView.getContext().getString(R.string.check_out_blog) + "\""
                            + articleDataModelsNew.get(position).getTitle() + "\" by " + articleDataModelsNew
                            .get(position).getAuthor().getFirstName() + ".\nRead Here: " + shareUrl;
                }
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                holder.shareImageView.getContext().startActivity(Intent.createChooser(shareIntent, "Momspresso"));
            });
        } catch (Exception ex) {
            Crashlytics.logException(ex);
            Log.d("MC4kException", Log.getStackTraceString(ex));
        }

        return view;
    }

    class ViewHolder {

        TextView txvArticleTitle;
        ImageView articleImageView;
        TextView dateTextView;
        ImageView shareImageView;
        ImageView vlogOptionImageView;
        RelativeLayout rootView;
        TextView viewCountTextView;
        TextView commentCountTextView;
        TextView recommendCountTextView;
    }

    public interface IEditVlog {

        void onVlogEdit(int position, ImageView imageView);
    }

}
