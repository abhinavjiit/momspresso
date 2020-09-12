package com.mycity4kids.ui.livestreaming;

import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.mycity4kids.R;
import com.mycity4kids.utils.AppUtils;
import com.mycity4kids.utils.DateTimeUtils;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;

public class LiveChatRecyclerAdapter extends
        RecyclerView.Adapter<LiveChatRecyclerAdapter.CommentsViewHolder> {

    private ArrayList<ChatListData> commentList;
    private RecyclerViewClickListener recyclerViewClickListener;
    private String authorId;

    public LiveChatRecyclerAdapter(RecyclerViewClickListener listener, String authorId) {
        recyclerViewClickListener = listener;
        this.authorId = authorId;
    }

    public void setData(ArrayList<ChatListData> commentList) {
        this.commentList = commentList;
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    @Override
    public CommentsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v0 = LayoutInflater.from(parent.getContext()).inflate(R.layout.live_stream_chat_item, parent, false);
        return new CommentsViewHolder(v0);
    }

    @Override
    public void onBindViewHolder(final CommentsViewHolder commentsViewHolder, final int position) {
        commentsViewHolder.commentDataTextView.setText(
                AppUtils.createSpannableForMentionHandling(commentList.get(position).getUserId(),
                        commentList.get(position).getUserName(), commentList.get(position).getMessage(),
                        commentList.get(position).getMentions(), ContextCompat
                                .getColor(commentsViewHolder.commentDataTextView.getContext(),
                                        R.color.app_dark_black), ContextCompat
                                .getColor(commentsViewHolder.commentDataTextView.getContext(),
                                        R.color.user_tag)));
        commentsViewHolder.commentDataTextView.setMovementMethod(LinkMovementMethod.getInstance());
        try {
            commentsViewHolder.dateTextView.setText(DateTimeUtils
                    .getDateFromNanoMilliTimestamp(commentList.get(position).getCreatedTime()));
        } catch (Exception e) {
            commentsViewHolder.dateTextView.setText("--");
        }

        try {
            Picasso.get().load(commentList.get(position).getUserPic())
                    .placeholder(R.drawable.default_commentor_img).into((commentsViewHolder.commentorImageView));
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            Log.d("MC4kException", Log.getStackTraceString(e));
            Picasso.get().load(R.drawable.default_commentor_img).into(commentsViewHolder.commentorImageView);
        }
    }

    public class CommentsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            View.OnLongClickListener {

        ImageView commentorImageView;
        TextView commentDataTextView;
        TextView dateTextView;

        CommentsViewHolder(View view) {
            super(view);
            commentorImageView = view.findViewById(R.id.commentorImageView);
            dateTextView = view.findViewById(R.id.dateTextView);
            commentDataTextView = view.findViewById(R.id.commentDataTextView);
        }

        @Override
        public void onClick(View v) {
            recyclerViewClickListener.onRecyclerItemClick(v, getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View v) {
            recyclerViewClickListener.onRecyclerItemClick(v, getAdapterPosition());
            return true;
        }
    }

    public interface RecyclerViewClickListener {

        void onRecyclerItemClick(View view, int position);
    }
}
