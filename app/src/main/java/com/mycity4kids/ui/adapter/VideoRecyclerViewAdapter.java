package com.mycity4kids.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.crashlytics.android.Crashlytics;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.response.VlogsListingAndDetailResult;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.ui.BaseViewHolder;
import com.mycity4kids.ui.activity.ParallelFeedActivity;
import com.mycity4kids.ui.fragment.AddCollectionAndCollectionItemDialogFragment;
import com.mycity4kids.utils.StringUtils;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;

public class VideoRecyclerViewAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private static final int VIEW_TYPE_EMPTY = 0;
    private static final int VIEW_TYPE_NORMAL = 1;
    private Context context;
    private ViewHolder viewHolder;
    private String likeStatus;
    private String userDynamoId;
    private VideoFeedRecyclerViewClick videoFeedRecyclerViewClick;
    FragmentManager fm;

    private ArrayList<VlogsListingAndDetailResult> vlogsListingAndDetailResults;

    public VideoRecyclerViewAdapter(VideoFeedRecyclerViewClick videoFeedRecyclerViewClick, Context context,
            FragmentManager fm) {
        this.fm = fm;
        this.context = context;
        this.videoFeedRecyclerViewClick = videoFeedRecyclerViewClick;
        userDynamoId = SharedPrefUtils.getUserDetailModel(context).getDynamoId();
    }

    public void setListUpdate(int updatePos, ArrayList<VlogsListingAndDetailResult> infoList) {
        vlogsListingAndDetailResults = infoList;
        notifyItemChanged(updatePos, viewHolder.followText);
    }

    public void updateList(ArrayList<VlogsListingAndDetailResult> infoList) {
        vlogsListingAndDetailResults = infoList;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_NORMAL:
                return new ViewHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false));
            case VIEW_TYPE_EMPTY:
                return new EmptyViewHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.item_empty_view, parent, false));
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        viewHolder = (ViewHolder) holder;
        holder.onBind(position);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position, List<Object> payload) {
        viewHolder = (ViewHolder) holder;
        holder.onBind(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        if (vlogsListingAndDetailResults != null && vlogsListingAndDetailResults.size() > 0) {
            return VIEW_TYPE_NORMAL;
        } else {
            return VIEW_TYPE_EMPTY;
        }
    }

    @Override
    public int getItemCount() {
        if (vlogsListingAndDetailResults != null && vlogsListingAndDetailResults.size() > 0) {
            return vlogsListingAndDetailResults.size();
        } else {
            return 1;
        }
    }

    public class ViewHolder extends BaseViewHolder {

        TextView textViewTitle;
        TextView userHandle;
        TextView followText;
        TextView commentCount;
        TextView viewsCount;
        TextView likeCount;
        RelativeLayout videoCell;
        RelativeLayout videoLayout;
        ImageView coverImageView;
        ImageView heart;
        ImageView share;
        ImageView whatsapp;
        ImageView threeDot;
        ImageView comment;
        ImageView imgBookmark;
        ProgressBar progressBar;
        View parent;
        ImageView userImage;
        ImageView collectionAdd;


        public ViewHolder(View itemView) {
            super(itemView);
            videoCell = itemView.findViewById(R.id.video_cell);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            userImage = itemView.findViewById(R.id.user_image);
            userHandle = itemView.findViewById(R.id.userHandle);
            videoLayout = itemView.findViewById(R.id.video_layout);
            coverImageView = itemView.findViewById(R.id.cover);
            progressBar = itemView.findViewById(R.id.progressBar);
            followText = itemView.findViewById(R.id.follow_textview);
            commentCount = itemView.findViewById(R.id.commentCount);
            comment = itemView.findViewById(R.id.comment);
            viewsCount = itemView.findViewById(R.id.viewsCount);
            likeCount = itemView.findViewById(R.id.viewsLike);
            heart = itemView.findViewById(R.id.heart);
            share = itemView.findViewById(R.id.share);
            whatsapp = itemView.findViewById(R.id.whatsapp);
            collectionAdd = itemView.findViewById(R.id.collectionAdd);
            threeDot = itemView.findViewById(R.id.three_dot);
            imgBookmark = itemView.findViewById(R.id.bookmark);
            parent = itemView;
        }

        protected void clear() {

        }

        public void onBind(int position) {
            super.onBind(position);
            parent.setTag(this);

            VlogsListingAndDetailResult responseData = vlogsListingAndDetailResults.get(position);

            if (responseData.getIs_liked() != null && responseData.getIs_liked().equals("1")) {
                heart.setImageResource(R.drawable.ic_likevideofilled);
            } else {
                heart.setImageResource(R.drawable.ic_likevideo);
            }

            if (responseData.isFollowed()) {
                followText.setText("Following");
            } else {
                followText.setText("Follow");
            }

            textViewTitle.setText(responseData.getTitle());
            makeTextViewResizable(textViewTitle, 2, " ..See More", true, responseData.getTitle());
            userHandle.setText(responseData.getAuthor().getFirstName() + " " + responseData.getAuthor().getLastName());
            try {
                Picasso.get().load(responseData.getAuthor().getProfilePic().getClientApp()).into(userImage);
            } catch (Exception e) {
                userImage.setImageResource(R.drawable.default_blogger_profile_img);
            }
            commentCount.setText(responseData.getComment_count());
            likeCount.setText(responseData.getLike_count() + " " + "Likes");
            viewsCount.setText(responseData.getView_count() + " " + "Views");
            followText.setOnClickListener(view -> {
                Utils.momVlogEvent(context, "Video Detail", "Follow", "", "android",
                        SharedPrefUtils.getAppLocale(context),
                        SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId(),
                        String.valueOf(System.currentTimeMillis()), "Vlogs_Engagement_CTA", "", "");

                ((ParallelFeedActivity) context).followApiCall(responseData.getAuthor().getId(), position);
            });

            userImage.setOnClickListener(view -> ((ParallelFeedActivity) context)
                    .openPublicProfile(responseData.getAuthor().getUserType(), responseData.getAuthor().getId(),
                            responseData.getAuthor().getFirstName() + responseData.getAuthor().getLastName()));
            userHandle.setOnClickListener(view -> ((ParallelFeedActivity) context)
                    .openPublicProfile(responseData.getAuthor().getUserType(), responseData.getAuthor().getId(),
                            responseData.getAuthor().getFirstName() + responseData.getAuthor().getLastName()));
            heart.setOnClickListener(view -> {
                if (responseData.getIs_liked() != null && responseData.getIs_liked().equals("1")) {
                    likeStatus = "0";
                    vlogsListingAndDetailResults.get(position).setIs_liked(likeStatus);
                    notifyDataSetChanged();
                    ((ParallelFeedActivity) context)
                            .recommendUnrecommentArticleApi(responseData.getId(), likeStatus, position);
                    Utils.momVlogEvent(context, "Video Detail", "DisLike", "", "android",
                            SharedPrefUtils.getAppLocale(context),
                            SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId(),
                            String.valueOf(System.currentTimeMillis()), "Vlogs_Engagement_CTA", "", "");
                } else {
                    likeStatus = "1";
                    vlogsListingAndDetailResults.get(position).setIs_liked(likeStatus);
                    notifyDataSetChanged();
                    ((ParallelFeedActivity) context)
                            .recommendUnrecommentArticleApi(responseData.getId(), likeStatus, position);
                    Utils.momVlogEvent(context, "Video Detail", "Like", "", "android",
                            SharedPrefUtils.getAppLocale(context),
                            SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId(),
                            String.valueOf(System.currentTimeMillis()), "Vlogs_Engagement_CTA", "", "");
                }
            });

            imgBookmark.setOnClickListener(view -> videoFeedRecyclerViewClick.onClick(getAdapterPosition(), view));

            share.setOnClickListener(view -> {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");

                if (!StringUtils.isNullOrEmpty(getShareUrl(responseData))) {
                    shareIntent.putExtra(Intent.EXTRA_TEXT, getShareUrl(responseData));
                    context.startActivity(Intent.createChooser(shareIntent, "Momspresso"));
                    Utils.pushShareArticleEvent(context, "DetailVideoScreen", userDynamoId + "",
                            responseData.getId(),
                            responseData.getAuthor().getId() + "~" + responseData.getAuthor().getFirstName() + " "
                                    + responseData.getAuthor().getLastName(), "CommonShare");
                }

                Utils.momVlogEvent(context, "Video Detail", "Share", "", "android",
                        SharedPrefUtils.getAppLocale(context),
                        SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId(),
                        String.valueOf(System.currentTimeMillis()), "Vlogs_Engagement_CTA", "", "");

            });

            collectionAdd.setOnClickListener(view -> {
                try {
                    AddCollectionAndCollectionItemDialogFragment addCollectionAndCollectionitemDialogFragment =
                            new AddCollectionAndCollectionItemDialogFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("articleId", vlogsListingAndDetailResults.get(getAdapterPosition()).getId());
                    bundle.putString("type", AppConstants.VIDEO_COLLECTION_TYPE);
                    addCollectionAndCollectionitemDialogFragment.setArguments(bundle);
                    addCollectionAndCollectionitemDialogFragment.show(fm, "collectionAdd");
                    Utils.pushProfileEvents(context, "CTA_Vlog_Add_To_Collection",
                            "VideoRecyclerViewAdapter", "Add to Collection", "-");
                } catch (Exception e) {
                    Crashlytics.logException(e);
                    Log.d("MC4kException", Log.getStackTraceString(e));
                }
            });

            whatsapp.setOnClickListener(v -> {
                if (StringUtils.isNullOrEmpty(getShareUrl(responseData))) {
                    Toast.makeText(context, context.getString(R.string.moderation_or_share_whatsapp_fail),
                            Toast.LENGTH_SHORT).show();
                } else {
                    Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
                    whatsappIntent.setType("text/plain");
                    whatsappIntent.setPackage("com.whatsapp");
                    whatsappIntent.putExtra(Intent.EXTRA_TEXT,
                            context.getString(R.string.check_out_momvlog) + getShareUrl(responseData));
                    try {
                        context.startActivity(whatsappIntent);
                        Utils.pushShareArticleEvent(context, "DetailVideoScreen", userDynamoId + "",
                                responseData.getId(),
                                responseData.getAuthor().getId() + "~" + responseData.getAuthor().getFirstName()
                                        + " " + responseData.getAuthor().getLastName(), "Whatsapp");
                    } catch (android.content.ActivityNotFoundException ex) {
                        Toast.makeText(context,
                                context.getString(R.string.moderation_or_share_whatsapp_not_installed),
                                Toast.LENGTH_SHORT).show();
                    }
                    Utils.momVlogEvent(context, "Video Detail", "Whatsapp", "", "android",
                            SharedPrefUtils.getAppLocale(context),
                            SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId(),
                            String.valueOf(System.currentTimeMillis()), "Vlogs_Engagement_CTA", "", "");

                }
            });
            commentCount.setOnClickListener(view -> ((ParallelFeedActivity) context)
                    .openViewCommentDialog(responseData.getCommentUri(), getShareUrl(responseData),
                            responseData.getAuthor().getId(),
                            responseData.getAuthor().getFirstName() + " " + responseData.getAuthor()
                                    .getLastName(), responseData.getId()));

            comment.setOnClickListener(view -> {
                Utils.momVlogEvent(context, "Video Detail", "Add comment", "", "android",
                        SharedPrefUtils.getAppLocale(context),
                        SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId(),
                        String.valueOf(System.currentTimeMillis()), "Vlogs_Engagement_CTA", "", "");

                ((ParallelFeedActivity) context)
                        .openViewCommentDialog(responseData.getCommentUri(), getShareUrl(responseData),
                                responseData.getAuthor().getId(),
                                responseData.getAuthor().getFirstName() + " " + responseData.getAuthor()
                                        .getLastName(), responseData.getId());
            });

            threeDot.setOnClickListener(view -> {
                if (responseData.getIs_bookmark() != null) {
                    PopupWindow popupWindow = popupDisplay(responseData.getIs_bookmark());
                    popupWindow.showAsDropDown(threeDot, -40, 18);
                }
            });

            Glide.with(context)
                    .asBitmap()
                    .load(responseData.getThumbnail())
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap bitmap,
                                Transition<? super Bitmap> transition) {
                            int w = bitmap.getWidth();
                            int h = bitmap.getHeight();
                            Log.e("width and height", w + " * " + h);

                            float ratio = ((float) h / (float) w);
                            videoLayout.getLayoutParams().height = Math
                                    .round(ratio * context.getResources().getDisplayMetrics().widthPixels);
                            videoLayout.getLayoutParams().width = Math
                                    .round(context.getResources().getDisplayMetrics().widthPixels);

                            coverImageView.setImageBitmap(bitmap);
                            Log.e("from ratio",
                                    w + "   " + h + "   " + videoLayout.getLayoutParams().height + " * " + videoLayout
                                            .getLayoutParams().width);
                        }
                    });
        }
    }

    private PopupWindow popupDisplay(String isBookmarked) {

        final PopupWindow popupWindow = new PopupWindow(context);

        LayoutInflater inflater = (LayoutInflater) ((ParallelFeedActivity) context)
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.parallel_feed_popup, null);

        ImageView imageView = view.findViewById(R.id.popup_bookmark);
        TextView textView = view.findViewById(R.id.bookmark_text);
        if (isBookmarked.equals("1")) {
            textView.setText("Bookmarked");
            imageView.setImageResource(R.drawable.ic_bookmarked);
        } else {
            textView.setText("Bookmark");
            imageView.setImageResource(R.drawable.ic_bookmark);
        }

        popupWindow.setFocusable(true);
        popupWindow.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setContentView(view);

        return popupWindow;
    }

    private void makeTextViewResizable(final TextView tv, final int maxLine, final String expandText,
            final boolean viewMore, final String userBio) {

        if (tv.getTag() == null) {
            tv.setTag(tv.getText());
        }
        ViewTreeObserver vto = tv.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @SuppressWarnings("deprecation")
            @Override
            public void onGlobalLayout() {

                ViewTreeObserver obs = tv.getViewTreeObserver();
                obs.removeGlobalOnLayoutListener(this);
                if (maxLine == 0) {
                    int lineEndIndex = tv.getLayout().getLineEnd(0);
                    String text =
                            tv.getText().subSequence(0, lineEndIndex - expandText.length() + 1) + " " + expandText;
                    tv.setText(text);
                    tv.setMovementMethod(LinkMovementMethod.getInstance());
                    tv.setText(
                            addClickablePartTextViewResizable(Html.fromHtml(tv.getText().toString()), tv, maxLine,
                                    expandText,
                                    viewMore, userBio), TextView.BufferType.SPANNABLE);
                } else if (maxLine > 0 && tv.getLineCount() > maxLine) {
                    int lineEndIndex = tv.getLayout().getLineEnd(maxLine - 1);
                    if ((lineEndIndex - expandText.length() + 1) > 10) {
                        String text =
                                tv.getText().subSequence(0, lineEndIndex - expandText.length() + 1) + " " + expandText;
                        tv.setText(text);
                        tv.setMovementMethod(LinkMovementMethod.getInstance());
                        tv.setText(
                                addClickablePartTextViewResizable(Html.fromHtml(tv.getText().toString()), tv, maxLine,
                                        expandText,
                                        viewMore, userBio), TextView.BufferType.SPANNABLE);
                    } else {
                        String text = tv.getText().subSequence(0, lineEndIndex) + " " + expandText;
                        tv.setText(text);
                        tv.setMovementMethod(LinkMovementMethod.getInstance());
                        tv.setText(
                                addClickablePartTextViewResizable(Html.fromHtml(tv.getText().toString()), tv, maxLine,
                                        expandText,
                                        viewMore, userBio), TextView.BufferType.SPANNABLE);
                    }
                }
            }
        });

    }

    private SpannableStringBuilder addClickablePartTextViewResizable(final Spanned strSpanned, final TextView tv,
            final int maxLine, final String spanableText, final boolean viewMore, final String userBio) {
        String str = strSpanned.toString();
        SpannableStringBuilder ssb = new SpannableStringBuilder(strSpanned);

        if (str.contains(spanableText)) {

            ssb.setSpan(new MySpannable(false) {
                @Override
                public void onClick(View widget) {
                    tv.setLayoutParams(tv.getLayoutParams());
                    tv.setText(userBio, TextView.BufferType.SPANNABLE);
                    tv.invalidate();
                }
            }, str.indexOf(spanableText), str.indexOf(spanableText) + spanableText.length(), 0);

        }
        return ssb;

    }

    public class MySpannable extends ClickableSpan {

        private boolean isUnderline;

        public MySpannable(boolean isUnderline) {
            this.isUnderline = isUnderline;
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            ds.setUnderlineText(isUnderline);
            ds.setColor(Color.parseColor("#FFFFFF"));
        }

        @Override
        public void onClick(View widget) {

        }
    }

    private String getShareUrl(VlogsListingAndDetailResult responseData) {
        String shareUrl = null;
        try {
            String titleSlug = responseData.getTitleSlug();
            String authorType = responseData.getAuthor().getUserType();
            if (!StringUtils.isNullOrEmpty(authorType)) {
                switch (authorType) {
                    case AppConstants.USER_TYPE_BLOGGER: {
                        String blogTitleSlug = responseData.getAuthor().getBlogTitleSlug();
                        if (StringUtils.isNullOrEmpty(blogTitleSlug)) {
                            shareUrl =
                                    AppConstants.VIDEO_ARTICLE_SHARE_URL + "video/" + responseData.getTitleSlug();
                        } else {
                            shareUrl = AppConstants.VIDEO_ARTICLE_SHARE_URL + blogTitleSlug + "/video/" + responseData
                                    .getTitleSlug();
                        }
                    }
                    break;
                    case AppConstants.USER_TYPE_EXPERT:
                    case AppConstants.USER_TYPE_EDITOR:
                    case AppConstants.USER_TYPE_EDITORIAL:
                    case AppConstants.USER_TYPE_FEATURED:
                        shareUrl = AppConstants.VIDEO_ARTICLE_SHARE_URL + "video/" + responseData.getTitleSlug();
                        break;
                    case AppConstants.USER_TYPE_USER:
                        String blogTitleSlug = responseData.getAuthor().getBlogTitleSlug();
                        if (StringUtils.isNullOrEmpty(blogTitleSlug)) {
                            shareUrl =
                                    AppConstants.VIDEO_ARTICLE_SHARE_URL + "video/" + responseData.getTitleSlug();
                        } else {
                            shareUrl = AppConstants.VIDEO_ARTICLE_SHARE_URL + blogTitleSlug + "/video/" + responseData
                                    .getTitleSlug();
                        }
                        break;
                    default:
                        break;
                }
            } else {
                // Default Author type set to Blogger
                shareUrl = AppConstants.VIDEO_ARTICLE_SHARE_URL + responseData.getAuthor().getBlogTitleSlug()
                        + "/video/" + titleSlug;
            }
        } catch (Exception e) {
            Crashlytics.logException(e);
            Log.d("MC4kException", Log.getStackTraceString(e));
        }

        return shareUrl;
    }

    public class EmptyViewHolder extends BaseViewHolder implements View.OnClickListener {

        Button retryButton;
        TextView messageTextView;

        EmptyViewHolder(View itemView) {
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

    public interface VideoFeedRecyclerViewClick {

        void onClick(int position, View view);
    }
}