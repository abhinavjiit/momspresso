package com.mycity4kids.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kelltontech.utils.StringUtils;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.models.Topics;
import com.mycity4kids.models.response.VlogsListingAndDetailResult;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.ui.activity.ChooseVideoCategoryActivity;
import com.mycity4kids.ui.activity.HeaderChallengeVideoPlayActivity;
import com.mycity4kids.utils.MixPanelUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class VideoChallengeDetailListingAdapter extends BaseAdapter {
    private Context mContext;
    private Topics topic;
    private LayoutInflater mInflator;
    ArrayList<VlogsListingAndDetailResult> articleDataModelsNew;
    private String selectedId;
    private String selected_Name;
    private String selectedActiveUrl;
    private String selectedStreamUrl;
    private final float density;
    private ArrayList<VlogsListingAndDetailResult> mArticleListData;

    public VideoChallengeDetailListingAdapter(Context pContext, String selected_Name, String selectedActiveUrl, String selectedId, Topics topic, String selectedStreamUrl) {
        density = pContext.getResources().getDisplayMetrics().density;
        mInflator = (LayoutInflater) pContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = pContext;
        this.topic = topic;
        this.selected_Name = selected_Name;
        this.selectedActiveUrl = selectedActiveUrl;
        this.selectedId = selectedId;
        this.selectedStreamUrl = selectedStreamUrl;
    }

    public void setListData(ArrayList<VlogsListingAndDetailResult> mParentingLists) {
        mArticleListData = mParentingLists;
    }

    public void setNewListData(ArrayList<VlogsListingAndDetailResult> mParentingLists_new) {
        articleDataModelsNew = mParentingLists_new;
    }

    @Override
    public int getItemViewType(int position) {
        if (position != 0 && position % 5 == 0) {
            return 0;
        } else if (position == 0) {
            return 2;
        } else {
            return 1;
        }
    }

    @Override
    public int getCount() {
        return articleDataModelsNew == null ? 0 : articleDataModelsNew.size() + 1;
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
    public int getViewTypeCount() {
        return 3;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        if (getItemViewType(position) == 2) {
            VideoChallengeHeaderView videoChallengeHeaderView;
            if (view == null) {
                videoChallengeHeaderView = new VideoChallengeHeaderView();
                view = mInflator.inflate(R.layout.video_challenge_detail_listing_header, null);
                videoChallengeHeaderView.rootChallengeHeaderContainer = (RelativeLayout) view.findViewById(R.id.rootChallengeHeaderContainer);
                videoChallengeHeaderView.challengeHeaderImageView = (ImageView) view.findViewById(R.id.ChallengeNameImage);
                videoChallengeHeaderView.submitButtonVideoChallenge = (TextView) view.findViewById(R.id.submit_story_text);
                view.setTag(videoChallengeHeaderView);
            } else {
                videoChallengeHeaderView = (VideoChallengeHeaderView) view.getTag();
            }
            try {
                Picasso.with(mContext).load(selectedActiveUrl)
                        .placeholder(R.drawable.default_article).error(R.drawable.default_article).into(videoChallengeHeaderView.challengeHeaderImageView);
            } catch (Exception e) {
                videoChallengeHeaderView.challengeHeaderImageView.setImageResource(R.drawable.default_article);
            }
            videoChallengeHeaderView.challengeHeaderImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(mContext, HeaderChallengeVideoPlayActivity.class);
                    if (selected_Name != null && !selected_Name.isEmpty() && selectedId != null && !selectedId.isEmpty()) {
                        intent.putExtra("selectedId", selectedId);
                        intent.putExtra("selectedName", selected_Name);
                        intent.putExtra("StreamUrl", selectedStreamUrl);
                        intent.putExtra("comingFrom", "Challenge");
                    }
                    mContext.startActivity(intent);


                }
            });
            videoChallengeHeaderView.submitButtonVideoChallenge.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, ChooseVideoCategoryActivity.class);
                    if (selected_Name != null && !selected_Name.isEmpty() && selectedId != null && !selectedId.isEmpty()) {
                        intent.putExtra("selectedId", selectedId);
                        intent.putExtra("selectedName", selected_Name);
                        intent.putExtra("comingFrom", "Challenge");
                    }
                    mContext.startActivity(intent);
                }
            });

            return view;
        } else if (getItemViewType(position) == 0) {
            AddVlogViewHolderChallenge addVlogViewHolder;
            if (view == null) {
                addVlogViewHolder = new AddVlogViewHolderChallenge();
                view = mInflator.inflate(R.layout.add_momvlog_list_item, null);
                addVlogViewHolder.txvArticleTitle = (TextView) view.findViewById(R.id.txvArticleTitle);
                addVlogViewHolder.txvAuthorName = (TextView) view.findViewById(R.id.txvAuthorName);
                addVlogViewHolder.articleImageView = (ImageView) view.findViewById(R.id.articleImageView);
                addVlogViewHolder.authorImageView = (ImageView) view.findViewById(R.id.authorImageView);
                addVlogViewHolder.viewCountTextView = (TextView) view.findViewById(R.id.viewCountTextView);
                addVlogViewHolder.commentCountTextView = (TextView) view.findViewById(R.id.commentCountTextView);
                addVlogViewHolder.recommendCountTextView = (TextView) view.findViewById(R.id.recommendCountTextView);
                addVlogViewHolder.addMomVlogImageView = (ImageView) view.findViewById(R.id.addMomVlogImageView);
                if (AppConstants.LOCALE_HINDI.equals(SharedPrefUtils.getAppLocale(mContext))) {
                    addVlogViewHolder.addMomVlogImageView.setImageResource(R.drawable.add_mom_vlog_hi);
                } else {
                    addVlogViewHolder.addMomVlogImageView.setImageResource(R.drawable.add_mom_vlog_en);
                }
                view.setTag(addVlogViewHolder);
            } else {
                addVlogViewHolder = (AddVlogViewHolderChallenge) view.getTag();
            }

            addVlogViewHolder.txvArticleTitle.setText(articleDataModelsNew.get(position - 1).getTitle());
            addVlogViewHolder.viewCountTextView.setVisibility(View.GONE);
            addVlogViewHolder.commentCountTextView.setVisibility(View.GONE);
            addVlogViewHolder.recommendCountTextView.setVisibility(View.GONE);

            try {
                String userName = articleDataModelsNew.get(position - 1).getAuthor().getFirstName() + " " + articleDataModelsNew.get(position - 1).getAuthor().getLastName();
                if (StringUtils.isNullOrEmpty(userName) || userName.trim().equalsIgnoreCase("")) {
                    addVlogViewHolder.txvAuthorName.setText("NA");
                } else {
                    addVlogViewHolder.txvAuthorName.setText(userName);
                }
            } catch (Exception e) {
                addVlogViewHolder.txvAuthorName.setText("NA");
            }
            try {
                Picasso.with(mContext).load(articleDataModelsNew.get(position - 1).getThumbnail())
                        .placeholder(R.drawable.default_article).error(R.drawable.default_article).into(addVlogViewHolder.articleImageView);
            } catch (Exception e) {
                addVlogViewHolder.articleImageView.setImageResource(R.drawable.default_article);
            }

            addVlogViewHolder.addMomVlogImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MixpanelAPI mixpanel = MixpanelAPI.getInstance(BaseApplication.getAppContext(), AppConstants.MIX_PANEL_TOKEN);
                    MixPanelUtils.pushAddMomVlogClickEvent(mixpanel, topic.getDisplay_name());
                    Intent intent = new Intent(mContext, ChooseVideoCategoryActivity.class);
                    mContext.startActivity(intent);
                }
            });
            return view;
        } else {
            ViewHolderChallenge holder;
            if (view == null) {
                holder = new ViewHolderChallenge();
                view = mInflator.inflate(R.layout.video_listing_item, null);
                holder.txvArticleTitle = (TextView) view.findViewById(R.id.txvArticleTitle);
                holder.txvAuthorName = (TextView) view.findViewById(R.id.txvAuthorName);
                holder.articleImageView = (ImageView) view.findViewById(R.id.articleImageView);
                holder.authorImageView = (ImageView) view.findViewById(R.id.authorImageView);
                holder.viewCountTextView = (TextView) view.findViewById(R.id.viewCountTextView);
                holder.commentCountTextView = (TextView) view.findViewById(R.id.commentCountTextView);
                holder.recommendCountTextView = (TextView) view.findViewById(R.id.recommendCountTextView);
                Log.d("SetTag", "VLOGSetTag = " + position);
                view.setTag(holder);
            } else {
                holder = (ViewHolderChallenge) view.getTag();
            }
            holder.txvArticleTitle.setText(articleDataModelsNew.get(position - 1).getTitle());
            holder.viewCountTextView.setVisibility(View.GONE);
            holder.commentCountTextView.setVisibility(View.GONE);
            holder.recommendCountTextView.setVisibility(View.GONE);

            try {
                String userName = articleDataModelsNew.get(position - 1).getAuthor().getFirstName() + " " + articleDataModelsNew.get(position - 1).getAuthor().getLastName();
                if (StringUtils.isNullOrEmpty(userName) || userName.trim().equalsIgnoreCase("")) {
                    holder.txvAuthorName.setText("NA");
                } else {
                    holder.txvAuthorName.setText(userName);
                }
            } catch (Exception e) {
                holder.txvAuthorName.setText("NA");
            }
            try {
                Picasso.with(mContext).load(articleDataModelsNew.get(position - 1).getThumbnail())
                        .placeholder(R.drawable.default_article).error(R.drawable.default_article).into(holder.articleImageView);
            } catch (Exception e) {
                holder.articleImageView.setImageResource(R.drawable.default_article);
            }
            return view;
        }
    }

    class ViewHolderChallenge {
        TextView txvArticleTitle;
        TextView txvAuthorName;
        ImageView articleImageView;
        ImageView authorImageView;
        TextView viewCountTextView;
        TextView commentCountTextView;
        TextView recommendCountTextView;
    }

    class AddVlogViewHolderChallenge {
        ImageView addMomVlogImageView;
        TextView txvArticleTitle;
        TextView txvAuthorName;
        ImageView articleImageView;
        ImageView authorImageView;
        TextView viewCountTextView;
        TextView commentCountTextView;
        TextView recommendCountTextView;
    }

    class VideoChallengeHeaderView {
        ImageView challengeHeaderImageView;
        RelativeLayout rootChallengeHeaderContainer;
        TextView submitButtonVideoChallenge;


    }


}
