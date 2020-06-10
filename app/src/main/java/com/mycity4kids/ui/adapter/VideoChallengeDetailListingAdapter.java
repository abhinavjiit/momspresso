package com.mycity4kids.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.mycity4kids.R;
import com.mycity4kids.models.response.VlogsListingAndDetailResult;
import com.mycity4kids.utils.AppUtils;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;

public class VideoChallengeDetailListingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    ArrayList<VlogsListingAndDetailResult> articleDataModelsNew;
    public SimpleExoPlayer player;
    RecyclerViewClickListener recyclerViewClickListner;


    public VideoChallengeDetailListingAdapter(RecyclerViewClickListener recyclerViewClickListner) {
        this.recyclerViewClickListner = recyclerViewClickListner;
    }

    public void setNewListData(ArrayList<VlogsListingAndDetailResult> articleDataModelsNew) {
        this.articleDataModelsNew = articleDataModelsNew;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewHolderChallenge viewHolderChallenge = null;
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.mom_vlog_listing_adapter, parent, false);
        viewHolderChallenge = new ViewHolderChallenge(view);
        return viewHolderChallenge;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolderChallenge) {
            try {
                Picasso.get().load(articleDataModelsNew.get(position).getThumbnail())
                        .into(((ViewHolderChallenge) holder).articleImageView);
            } catch (Exception e) {
                ((ViewHolderChallenge) holder).articleImageView.setImageResource(R.drawable.default_article);
            }
            ((ViewHolderChallenge) holder).articleTitleTextView.setText(articleDataModelsNew.get(position).getTitle());
            ((ViewHolderChallenge) holder).authorName.setText(
                    articleDataModelsNew.get(position).getAuthor().getFirstName().trim() + " " + articleDataModelsNew
                            .get(position).getAuthor().getLastName().trim());
            try {
                ((ViewHolderChallenge) holder).viewCountTextView
                        .setText(AppUtils.withSuffix(
                                Long.parseLong(articleDataModelsNew.get(position).getView_count())));
            } catch (Exception e) {
                ((ViewHolderChallenge) holder).viewCountTextView
                        .setText(articleDataModelsNew.get(position).getView_count());
            }
            try {
                ((ViewHolderChallenge) holder).recommendCountTextView1
                        .setText(AppUtils.withSuffix(
                                Long.parseLong(articleDataModelsNew.get(position).getLike_count())));
            } catch (Exception e) {
                ((ViewHolderChallenge) holder).recommendCountTextView1
                        .setText(articleDataModelsNew.get(position).getLike_count());
            }
            if (articleDataModelsNew.get(position).isIs_gold()) {
                ((ViewHolderChallenge) holder).imageWinner.setVisibility(View.VISIBLE);
                ((ViewHolderChallenge) holder).imageWinner.setImageResource(R.drawable.ic_star_yellow);
            } else if (articleDataModelsNew.get(position).getWinner() == 1) {
                ((ViewHolderChallenge) holder).imageWinner.setVisibility(View.VISIBLE);
                ((ViewHolderChallenge) holder).imageWinner.setImageResource(R.drawable.ic_trophy);
            } else {
                ((ViewHolderChallenge) holder).imageWinner.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position != 0 && position % 5 == 0) {
            return 0;
        } else {
            return 1;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return articleDataModelsNew == null ? 0 : articleDataModelsNew.size();
    }

    private class ViewHolderChallenge extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView articleTitleTextView;
        TextView authorName;
        ImageView imageWinner;
        ImageView articleImageView;
        TextView viewCountTextView;
        TextView recommendCountTextView1;
        RelativeLayout container;

        ViewHolderChallenge(@NonNull View itemView) {
            super(itemView);

            articleTitleTextView = itemView.findViewById(R.id.articleTitleTextView);
            articleImageView = itemView.findViewById(R.id.articleImageView);
            authorName = itemView.findViewById(R.id.author_name);
            recommendCountTextView1 = itemView.findViewById(R.id.recommendCountTextView1);
            viewCountTextView = itemView.findViewById(R.id.viewCountTextView1);
            imageWinner = itemView.findViewById(R.id.imageWinner);
            container = itemView.findViewById(R.id.container);
            container.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            recyclerViewClickListner.onRecyclerClick(view, getAdapterPosition());
        }
    }

    public interface RecyclerViewClickListener {

        void onRecyclerClick(View v, int pos);
    }
}

