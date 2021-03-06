package com.mycity4kids.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.mycity4kids.R;
import com.mycity4kids.models.response.ArticleListingResult;
import com.mycity4kids.utils.DateTimeUtils;
import com.mycity4kids.utils.StringUtils;
import com.squareup.picasso.Picasso;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by hemant on 19/7/17.
 */
public class UserPublishedArticleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    IEditVlog editVlog;
    private LayoutInflater layoutInflater;
    private ArrayList<ArticleListingResult> articleDataModelsNew;
    private RecyclerViewClickListener recyclerViewClickListener;
    private boolean isPrivateProfile;

    public UserPublishedArticleAdapter(Context context, RecyclerViewClickListener listener, IEditVlog editVlog,
            boolean isPrivateProfile) {
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.recyclerViewClickListener = listener;
        this.isPrivateProfile = isPrivateProfile;
        this.editVlog = editVlog;
    }

    public void setListData(ArrayList<ArticleListingResult> articleListingResults) {
        articleDataModelsNew = articleListingResults;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        UserPublishedArticleViewHolder viewHolder = null;
        View v0 = layoutInflater.inflate(R.layout.user_published_article_list_item, parent, false);
        viewHolder = new UserPublishedArticleViewHolder(v0, recyclerViewClickListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (null != articleDataModelsNew.get(position).getImageUrl()) {
            Picasso.get().load(articleDataModelsNew.get(position).getImageUrl().getThumbMin())
                    .placeholder(R.drawable.default_article).error(R.drawable.default_article)
                    .into(((UserPublishedArticleViewHolder) holder).articleImageView);
        } else {
            ((UserPublishedArticleViewHolder) holder).articleImageView
                    .setBackgroundResource(R.drawable.article_default);
        }

        ((UserPublishedArticleViewHolder) holder).txvArticleTitle
                .setText(articleDataModelsNew.get(position).getTitle());
        ((UserPublishedArticleViewHolder) holder).viewCountTextView
                .setText(articleDataModelsNew.get(position).getArticleCount());

        ((UserPublishedArticleViewHolder) holder).commentCountTextView
                .setText(articleDataModelsNew.get(position).getCommentsCount());
        if (StringUtils.isNullOrEmpty(articleDataModelsNew.get(position).getCommentsCount()) || "0"
                .equals(articleDataModelsNew.get(position).getCommentsCount())) {
            ((UserPublishedArticleViewHolder) holder).commentCountTextView.setVisibility(View.GONE);
            ((UserPublishedArticleViewHolder) holder).separatorView1.setVisibility(View.GONE);
        } else {
            ((UserPublishedArticleViewHolder) holder).commentCountTextView.setVisibility(View.VISIBLE);
            ((UserPublishedArticleViewHolder) holder).separatorView1.setVisibility(View.VISIBLE);
        }
        ((UserPublishedArticleViewHolder) holder).recommendCountTextView
                .setText(articleDataModelsNew.get(position).getLikesCount());
        if (StringUtils.isNullOrEmpty(articleDataModelsNew.get(position).getLikesCount()) || "0"
                .equals(articleDataModelsNew.get(position).getLikesCount())) {
            ((UserPublishedArticleViewHolder) holder).recommendCountTextView.setVisibility(View.GONE);
            ((UserPublishedArticleViewHolder) holder).separatorView2.setVisibility(View.GONE);
        } else {
            ((UserPublishedArticleViewHolder) holder).recommendCountTextView.setVisibility(View.VISIBLE);
            ((UserPublishedArticleViewHolder) holder).separatorView2.setVisibility(View.VISIBLE);
        }
        if (isPrivateProfile) {
            ((UserPublishedArticleViewHolder) holder).editPublishedTextView.setVisibility(View.GONE);
            ((UserPublishedArticleViewHolder) holder).menuEditAndDisableComments.setVisibility(View.VISIBLE);
            ((UserPublishedArticleViewHolder) holder).menuEditAndDisableComments
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            editVlog.onBlogEdit(position,
                                    ((UserPublishedArticleViewHolder) holder).menuEditAndDisableComments);
                        }
                    });
        } else {
            ((UserPublishedArticleViewHolder) holder).editPublishedTextView.setVisibility(View.GONE);
        }

        try {
            Calendar calendar1 = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
            SimpleDateFormat sdf1 = new SimpleDateFormat("HH:mm", Locale.US);
            calendar1.setTimeInMillis(articleDataModelsNew.get(position).getCreatedTime() * 1000);

            Long diff = System.currentTimeMillis() - articleDataModelsNew.get(position).getCreatedTime() * 1000;
            if (diff / (1000 * 60 * 60) > 24 && !sdf.format(System.currentTimeMillis())
                    .equals(sdf.format((articleDataModelsNew.get(position).getCreatedTime() * 1000)))) {
                ((UserPublishedArticleViewHolder) holder).txvPublishDate.setText(
                        DateTimeUtils.getDateFromTimestamp(articleDataModelsNew.get(position).getCreatedTime()));
            } else {
                ((UserPublishedArticleViewHolder) holder).txvPublishDate.setText(sdf1.format(calendar1.getTime()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return articleDataModelsNew == null ? 0 : articleDataModelsNew.size();
    }

    public class UserPublishedArticleViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView articleImageView;
        ImageView menuEditAndDisableComments;
        TextView txvArticleTitle;
        TextView txvPublishDate;
        TextView viewCountTextView;
        TextView commentCountTextView;
        TextView recommendCountTextView;
        ImageView shareArticleImageView;
        TextView editPublishedTextView;
        View separatorView1;
        View separatorView2;

        public UserPublishedArticleViewHolder(View itemView, RecyclerViewClickListener listener) {
            super(itemView);
            txvArticleTitle = (TextView) itemView.findViewById(R.id.txvArticleTitle);
            txvPublishDate = (TextView) itemView.findViewById(R.id.txvPublishDate);
            viewCountTextView = (TextView) itemView.findViewById(R.id.viewCountTextView);
            commentCountTextView = (TextView) itemView.findViewById(R.id.commentCountTextView);
            recommendCountTextView = (TextView) itemView.findViewById(R.id.recommendCountTextView);
            articleImageView = (ImageView) itemView.findViewById(R.id.articleImageView);
            shareArticleImageView = (ImageView) itemView.findViewById(R.id.shareArticleImageView);
            editPublishedTextView = (TextView) itemView.findViewById(R.id.editPublishedTextView);
            menuEditAndDisableComments = (ImageView) itemView.findViewById(R.id.menuEditAndDisableComments);
            menuEditAndDisableComments.setOnClickListener(this);
            separatorView1 = itemView.findViewById(R.id.separatorView1);
            separatorView2 = itemView.findViewById(R.id.separatorView2);
            shareArticleImageView.setOnClickListener(this);
            editPublishedTextView.setOnClickListener(this);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            recyclerViewClickListener.onClick(v, getAdapterPosition());
        }
    }

    public interface RecyclerViewClickListener {

        void onClick(View view, int position);
    }

    public interface IEditVlog {

        void onBlogEdit(int position, ImageView imageView);
    }

}
