package com.mycity4kids.ui.adapter;

import android.content.Context;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by hemant on 10/4/18.
 */

public class AddImagePollRecyclerGridAdapter extends RecyclerView.Adapter<AddImagePollRecyclerGridAdapter.ImagePollViewHolder> {

    private final Context mContext;
    private final LayoutInflater mInflator;
    private ArrayList<String> urlList;
    private ImagePollRecyclerViewClickListener mListener;
    private int selectedPosition;

    public AddImagePollRecyclerGridAdapter(Context pContext, ImagePollRecyclerViewClickListener listener) {
        mContext = pContext;
        mInflator = (LayoutInflater) pContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mListener = listener;
    }

    public void setNewListData(ArrayList<String> urlList) {
        this.urlList = urlList;
    }

    @Override
    public int getItemCount() {
        return urlList.size();
    }

    @Override
    public AddImagePollRecyclerGridAdapter.ImagePollViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v0 = mInflator.inflate(R.layout.image_poll_choice_item, parent, false);
        return new AddImagePollRecyclerGridAdapter.ImagePollViewHolder(v0);
    }

    @Override
    public void onBindViewHolder(ImagePollViewHolder holder, int position) {
//        holder.groupImageView.setText(urlList.get(position));
//        holder.groupsNameTextView.setText(urlList.get(position).getTitle());
//        holder.groupsNameTextView.setSelected(position == selectedPosition);
        if (urlList.size() > 2) {
            holder.removeOptionImageView.setVisibility(View.VISIBLE);
        } else {
            holder.removeOptionImageView.setVisibility(View.INVISIBLE);
        }

        if (StringUtils.isNullOrEmpty(urlList.get(position))) {
            holder.addImageOptionImageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.default_article));
            holder.addImageOptionImageView.setVisibility(View.INVISIBLE);
            holder.addImageTextView.setVisibility(View.VISIBLE);
        } else {
            Picasso.with(mContext).load(urlList.get(position))
                    .placeholder(R.drawable.default_article).error(R.drawable.default_article).into(holder.addImageOptionImageView);
            holder.addImageOptionImageView.setVisibility(View.VISIBLE);
            holder.addImageTextView.setVisibility(View.INVISIBLE);
        }
    }

    public class ImagePollViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView addImageOptionImageView;
        ImageView removeOptionImageView;
        TextView addImageTextView;
        RelativeLayout addImageOptionContainer;

        ImagePollViewHolder(View view) {
            super(view);
            addImageOptionImageView = (ImageView) view.findViewById(R.id.addImageOptionImageView);
            addImageTextView = (TextView) view.findViewById(R.id.addImageTextView);
            removeOptionImageView = (ImageView) view.findViewById(R.id.removeItemImageView);
            addImageOptionContainer = (RelativeLayout) view.findViewById(R.id.addImageOptionContainer);

            addImageOptionContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onImagePollItemClick(v, getAdapterPosition());
                    notifyDataSetChanged();
                }
            });
            removeOptionImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onImagePollItemClick(v, getAdapterPosition());
                }
            });
        }

        @Override
        public void onClick(View v) {
            mListener.onImagePollItemClick(v, getAdapterPosition());
        }
    }

    public interface ImagePollRecyclerViewClickListener {
        void onImagePollItemClick(View view, int position);
    }

}
