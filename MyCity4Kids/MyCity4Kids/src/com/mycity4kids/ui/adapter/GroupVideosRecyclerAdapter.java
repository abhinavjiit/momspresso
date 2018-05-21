package com.mycity4kids.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mycity4kids.R;

import java.util.ArrayList;

/**
 * Created by hemant on 4/12/17.
 */

public class GroupVideosRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int HEADER = 0;
    public static final int ARTICLE = 1;
    private final Context mContext;
    private final LayoutInflater mInflator;
    private ArrayList<String> postDataList;
    private RecyclerViewClickListener mListener;
    private int selectedPosition;

    public GroupVideosRecyclerAdapter(Context pContext, RecyclerViewClickListener listener) {
        mContext = pContext;
        mInflator = (LayoutInflater) pContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mListener = listener;
    }

    public void setData(ArrayList<String> postDataList) {
        this.postDataList = postDataList;
    }

    @Override
    public int getItemCount() {
        return postDataList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return HEADER;
        } else {
            return ARTICLE;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == HEADER) {
            View v0 = mInflator.inflate(R.layout.groups_summary_header, parent, false);
            return new HeaderViewHolder(v0);
        } else {
            View v0 = mInflator.inflate(R.layout.groups_text_post_item, parent, false);
            return new GroupPostHolder(v0);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof HeaderViewHolder) {
//            addArticleItem((RootCommentViewHolder) holder, position);
        } else {
//            addArticleItem((GroupPostHolder) holder, position);
            ((GroupPostHolder) holder).usernameTextView.setText(postDataList.get(position));
            ((GroupPostHolder) holder).postDateTextView.setText("12 Mar 2018");
            ((GroupPostHolder) holder).postDataTextView.setText("gfwad awde wae dawed a awed agvrd vgdrf aw awde afgvs ewawad gfrcewarfaw dfaefr awd ewadcawde awda de");
            ((GroupPostHolder) holder).upvoteTextView.setText("32");
            ((GroupPostHolder) holder).downvoteTextView.setText("33");
            ((GroupPostHolder) holder).postCommentsTextView.setText("545");
        }
    }

    public class GroupPostHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView userImageView;
        TextView usernameTextView;
        TextView postDateTextView;
        ImageView postSettingImageView;
        TextView postDataTextView;
        TextView upvoteTextView;
        TextView downvoteTextView;
        TextView postCommentsTextView;
        ImageView shareTextView;

        GroupPostHolder(View view) {
            super(view);
            userImageView = (ImageView) view.findViewById(R.id.userImageView);
            usernameTextView = (TextView) view.findViewById(R.id.usernameTextView);
            postDateTextView = (TextView) view.findViewById(R.id.postDateTextView);
            postSettingImageView = (ImageView) view.findViewById(R.id.postSettingImageView);
            postDataTextView = (TextView) view.findViewById(R.id.postDataTextView);
            upvoteTextView = (TextView) view.findViewById(R.id.upvoteTextView);
            downvoteTextView = (TextView) view.findViewById(R.id.downvoteTextView);
            postCommentsTextView = (TextView) view.findViewById(R.id.postCommentsTextView);
            shareTextView = (ImageView) view.findViewById(R.id.shareTextView);
            userImageView.setOnClickListener(this);
            usernameTextView.setOnClickListener(this);
            postDataTextView.setOnClickListener(this);
            postDateTextView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            mListener.onRecyclerItemClick(v, getAdapterPosition());
        }
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView groupDescTextView;
        TextView groupAdminTextView;

        HeaderViewHolder(View view) {
            super(view);
            groupDescTextView = (TextView) view.findViewById(R.id.groupDescTextView);
            groupAdminTextView = (TextView) view.findViewById(R.id.groupAdminTextView);
            groupDescTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("GpSumPstRecyclerAdapter", "groupDescTextView");
//                    getAdapterPosition()
                }
            });
            groupAdminTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("GpSumPstRecyclerAdapter", "groupAdminTextView");
                }
            });
        }

        @Override
        public void onClick(View v) {
            mListener.onRecyclerItemClick(v, getAdapterPosition());
        }
    }

    public interface RecyclerViewClickListener {
        void onRecyclerItemClick(View view, int position);
    }

}
