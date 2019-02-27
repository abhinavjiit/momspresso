package com.mycity4kids.ui.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.mycity4kids.R;
import com.mycity4kids.models.VideoInfo;
import com.mycity4kids.models.response.VlogsDetailResponse;
import com.mycity4kids.models.response.VlogsListingAndDetailResult;
import com.mycity4kids.ui.BaseViewHolder;

import java.util.ArrayList;
import java.util.List;

public class VideoRecyclerViewAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    public static final int VIEW_TYPE_EMPTY = 0;
    public static final int VIEW_TYPE_NORMAL = 1;

//    private List<VideoInfo> mInfoList;
    private ArrayList<VlogsListingAndDetailResult> mInfoList;

    public VideoRecyclerViewAdapter(ArrayList<VlogsListingAndDetailResult> infoList) {
        mInfoList = infoList;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_NORMAL:
                return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false));
            case VIEW_TYPE_EMPTY:
                return new EmptyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_empty_view, parent, false));
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        holder.onBind(position);
    }


    @Override
    public int getItemViewType(int position) {
        if (mInfoList != null && mInfoList.size() > 0) {
            return VIEW_TYPE_NORMAL;
        } else {
            return VIEW_TYPE_EMPTY;
        }
    }

    @Override
    public int getItemCount() {
        if (mInfoList != null && mInfoList.size() > 0) {
            return mInfoList.size();
        } else {
            return 1;
        }
    }


    public void onRelease() {
        if (mInfoList != null) {
            mInfoList.clear();
            mInfoList = null;
        }
    }

    public class ViewHolder extends BaseViewHolder {

        TextView textViewTitle;
        TextView userHandle;
        public RelativeLayout videoCell;
        public FrameLayout videoLayout;
        public ImageView mCover;
        public ProgressBar mProgressBar;
        public final View parent;


        public ViewHolder(View itemView) {
            super(itemView);
            videoCell = itemView.findViewById(R.id.video_cell);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            userHandle = itemView.findViewById(R.id.userHandle);
            videoLayout = itemView.findViewById(R.id.video_layout);
            mCover = itemView.findViewById(R.id.cover);
            mProgressBar = itemView.findViewById(R.id.progressBar);
            parent = itemView;
        }

        protected void clear() {

        }

        public void onBind(int position) {
            super.onBind(position);
            parent.setTag(this);

            VlogsListingAndDetailResult responseData = mInfoList.get(position);
//            VlogsListingAndDetailResult videoInfo = responseData.getData().getResult();
            textViewTitle.setText(responseData.getTitle());
            userHandle.setText(responseData.getPublished_time());
            Glide.with(itemView.getContext())
                    .load(responseData.getThumbnail()).apply(new RequestOptions().optionalCenterCrop())
                    .into(mCover);
        }
    }

    public class EmptyViewHolder extends BaseViewHolder implements View.OnClickListener {

        Button retryButton;
        TextView messageTextView;

        public EmptyViewHolder(View itemView) {
            super(itemView);
            retryButton = itemView.findViewById(R.id.btn_retry);
            messageTextView = itemView.findViewById(R.id.tv_message);
            itemView.setVisibility(View.GONE);
            retryButton.setOnClickListener(this);
        }

        @Override
        protected void clear() {

        }

        @Override
        public void onClick(View v) {

        }
    }
}
