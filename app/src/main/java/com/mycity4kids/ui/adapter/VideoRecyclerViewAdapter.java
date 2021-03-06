package com.mycity4kids.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
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
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.Topics;
import com.mycity4kids.models.request.FollowUnfollowUserRequest;
import com.mycity4kids.models.response.FollowUnfollowUserResponse;
import com.mycity4kids.models.response.MomVlogersDetailResponse;
import com.mycity4kids.models.response.UserDetailResult;
import com.mycity4kids.models.response.VlogsListingAndDetailResult;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.profile.UserProfileActivity;
import com.mycity4kids.retrofitAPIsInterfaces.FollowAPI;
import com.mycity4kids.retrofitAPIsInterfaces.VlogsListingAndDetailsAPI;
import com.mycity4kids.sync.SyncUserFollowingList;
import com.mycity4kids.ui.BaseViewHolder;
import com.mycity4kids.ui.activity.CategoryVideosListingActivity;
import com.mycity4kids.ui.activity.ParallelFeedActivity;
import com.mycity4kids.ui.fragment.AddCollectionAndCollectionItemDialogFragment;
import com.mycity4kids.ui.videochallengenewui.activity.NewVideoChallengeActivity;
import com.mycity4kids.utils.AppUtils;
import com.mycity4kids.utils.StringUtils;
import com.mycity4kids.utils.ToastUtils;
import com.mycity4kids.widget.MomspressoButtonWidget;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class VideoRecyclerViewAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private static final int VIEW_TYPE_EMPTY = 0;
    private static final int VIEW_TYPE_NORMAL = 1;
    private static final int VIEW_TYPE_CAROUSAL = 2;
    private static final int VIEW_TYPE_CHALLENGE = 3;
    private Context context;
    private ViewHolder viewHolder;
    private String likeStatus;
    private VideoFeedRecyclerViewClick videoFeedRecyclerViewClick;
    private String collectionId;
    FragmentManager fm;
    private int start = 0;
    private String challengeId;
    private int end = 0;
    private ArrayList<VlogsListingAndDetailResult> vlogsListingAndDetailResults;

    public VideoRecyclerViewAdapter(VideoFeedRecyclerViewClick videoFeedRecyclerViewClick, Context context,
            FragmentManager fm, String collectionId) {
        this.fm = fm;
        this.context = context;
        this.videoFeedRecyclerViewClick = videoFeedRecyclerViewClick;
        this.collectionId = collectionId;
    }

    public void setListUpdate(int updatePos, ArrayList<VlogsListingAndDetailResult> infoList) {
        vlogsListingAndDetailResults = infoList;
        notifyItemChanged(updatePos, viewHolder.followText);
    }

    public void updateLikeStatus(int updatePos, ArrayList<VlogsListingAndDetailResult> infoList) {
        vlogsListingAndDetailResults = infoList;
        notifyItemChanged(updatePos, viewHolder.heart);
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
            case VIEW_TYPE_CAROUSAL:
                return new FollowFollowingCarousal(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.mom_vlog_follow_following_carousal, parent, false));
            case VIEW_TYPE_CHALLENGE:
                return new ChallengeCardHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.vlog_challenge_card, parent, false));
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
        if (holder instanceof ViewHolder) {
            viewHolder = (ViewHolder) holder;
            holder.onBind(position);
        } else if (holder instanceof ChallengeCardHolder) {
            holder.onBind(position);
        } else if (holder instanceof FollowFollowingCarousal) {
            if (!vlogsListingAndDetailResults.get(position).isCarouselRequestRunning()
                    && !vlogsListingAndDetailResults.get(position)
                    .isResponseReceived()) {
                vlogsListingAndDetailResults.get(position)
                        .setCarouselRequestRunning(true);
                ((FollowFollowingCarousal) holder).shimmerLayout.startShimmerAnimation();
                ((FollowFollowingCarousal) holder).shimmerLayout.setVisibility(View.VISIBLE);
                Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
                VlogsListingAndDetailsAPI vlogsListingAndDetailsApi = retrofit.create(VlogsListingAndDetailsAPI.class);
                end = start + 6;
                Call<MomVlogersDetailResponse> call = vlogsListingAndDetailsApi.getVlogersData(
                        SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId(), start, end,
                        1);
                start = end + 1;
                call.enqueue(
                        new Callback<MomVlogersDetailResponse>() {
                            @Override
                            public void onResponse(@NonNull Call<MomVlogersDetailResponse> call,
                                    @NonNull Response<MomVlogersDetailResponse> response) {
                                try {
                                    ((FollowFollowingCarousal) holder).shimmerLayout.stopShimmerAnimation();
                                    ((FollowFollowingCarousal) holder).shimmerLayout.setVisibility(View.GONE);

                                    if (response.isSuccessful() && null != response.body()) {
                                        if (response.body().getData() != null) {
                                            ArrayList<UserDetailResult> responseData = response.body().getData()
                                                    .getResult();

                                            processVlogersData(
                                                    (FollowFollowingCarousal) holder,
                                                    responseData,
                                                    position
                                            );
                                        }

                                        vlogsListingAndDetailResults.get(position)
                                                .setCarouselRequestRunning(true);
                                        vlogsListingAndDetailResults.get(position)
                                                .setResponseReceived(true);

                                    } else {
                                        vlogsListingAndDetailResults.get(position)
                                                .setCarouselRequestRunning(false);
                                        vlogsListingAndDetailResults.get(position)
                                                .setResponseReceived(true);
                                    }

                                } catch (Exception e) {
                                    vlogsListingAndDetailResults.get(position)
                                            .setCarouselRequestRunning(false);
                                    vlogsListingAndDetailResults.get(position)
                                            .setResponseReceived(false);
                                    FirebaseCrashlytics.getInstance().recordException(e);
                                    Log.d("MC4kException", Log.getStackTraceString(e));
                                }
                            }

                            @Override
                            public void onFailure(Call<MomVlogersDetailResponse> call, Throwable t) {

                            }
                        }
                );


            } else if (vlogsListingAndDetailResults.get(position)
                    .isCarouselRequestRunning()
                    && !vlogsListingAndDetailResults.get(position)
                    .isResponseReceived()) {
                Log.d("TAG", vlogsListingAndDetailResults.get(position)
                        .isCarouselRequestRunning() + " .........."
                        + vlogsListingAndDetailResults.get(position)
                        .isResponseReceived()
                );
            } else {
                if (null != vlogsListingAndDetailResults.get(position)
                        .getCarouselVideoList()
                        && !vlogsListingAndDetailResults.get(position)
                        .getCarouselVideoList().isEmpty()) {
                    populateCarouselFollowFollowing(
                            (FollowFollowingCarousal) holder,
                            vlogsListingAndDetailResults.get(position)
                                    .getCarouselVideoList()
                    );
                }
            }
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        if (vlogsListingAndDetailResults.get(position).getItemType() == 1) {
            return VIEW_TYPE_CAROUSAL;
        } else if (vlogsListingAndDetailResults.get(position).getItemType() == 2) {
            return VIEW_TYPE_CHALLENGE;
        } else {
            if (vlogsListingAndDetailResults != null && vlogsListingAndDetailResults.size() > 0) {
                return VIEW_TYPE_NORMAL;
            } else {
                return VIEW_TYPE_EMPTY;
            }
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
        ImageView commentIndicator;
        TextView viewsCount;
        TextView likeCount;
        public RelativeLayout videoCell;
        public ImageView coverImageView;
        public ProgressBar progressBar;
        RelativeLayout videoLayout;
        ImageView heart;
        ImageView share;
        ImageView whatsapp;
        ImageView comment;
        ImageView moreOptionImageView;
        View parent;
        ImageView userImage;
        ImageView collectionAdd;


        public ViewHolder(View itemView) {
            super(itemView);
            videoCell = itemView.findViewById(R.id.video_cell);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            commentIndicator = itemView.findViewById(R.id.commentIndicator);
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
            moreOptionImageView = itemView.findViewById(R.id.moreOptionImageView);
            parent = itemView;

            if (!StringUtils.isNullOrEmpty(collectionId)) {
                commentIndicator.setVisibility(View.GONE);
                commentCount.setVisibility(View.GONE);
                comment.setVisibility(View.GONE);
                viewsCount.setVisibility(View.GONE);
                heart.setVisibility(View.GONE);
                share.setVisibility(View.GONE);
                whatsapp.setVisibility(View.GONE);
                moreOptionImageView.setVisibility(View.GONE);
                collectionAdd.setVisibility(View.GONE);
            }
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
                followText.setText(followText.getContext().getResources().getString(R.string.all_following));
            } else {
                followText.setText(followText.getContext().getResources().getString(R.string.all_follow));
            }

            textViewTitle.setText(responseData.getTitle());
            makeTextViewResizable(textViewTitle, 2, " ..See More", true, responseData.getTitle());
            userHandle.setText(responseData.getAuthor().getFirstName() + " " + responseData.getAuthor().getLastName());
            try {
                Picasso.get().load(responseData.getAuthor().getProfilePic().getClientApp()).into(userImage);
            } catch (Exception e) {
                userImage.setImageResource(R.drawable.default_blogger_profile_img);
            }
            try {
                if (Integer.parseInt(responseData.getComment_count()) > 0 && StringUtils.isNullOrEmpty(collectionId)) {
                    commentIndicator.setVisibility(View.VISIBLE);
                } else {
                    commentIndicator.setVisibility(View.INVISIBLE);
                }
            } catch (Exception e) {
                commentIndicator.setVisibility(View.INVISIBLE);
            }

            try {
                commentCount.setText(AppUtils.withSuffix(Long.parseLong(responseData.getComment_count())));
            } catch (Exception e) {
                commentCount.setText(responseData.getComment_count());
            }
            try {
                likeCount.setText(AppUtils.withSuffix(Long.parseLong(responseData.getLike_count())) + " " + "Likes");
            } catch (Exception e) {
                likeCount.setText(responseData.getLike_count() + " " + "Likes");
            }
            try {
                viewsCount.setText(AppUtils.withSuffix(Long.parseLong(responseData.getView_count())) + " " + "Views");
            } catch (Exception e) {
                viewsCount.setText(responseData.getView_count() + " " + "Views");
            }
            followText.setOnClickListener(view -> {
                Utils.momVlogEvent(context, "Video Detail", "Follow", "", "android",
                        SharedPrefUtils.getAppLocale(context),
                        SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId(),
                        String.valueOf(System.currentTimeMillis()), "Following", "", "");

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
                    ((ParallelFeedActivity) context)
                            .recommendUnrecommentArticleApi(responseData.getId(), "0", position);
                } else {
                    ((ParallelFeedActivity) context)
                            .recommendUnrecommentArticleApi(responseData.getId(), "1", position);
                    Utils.shareEventTracking(context, "Video Detail", "Like_Android", "VlogDetail_Like");
                }
            });

            moreOptionImageView
                    .setOnClickListener(view -> videoFeedRecyclerViewClick.onClick(getAdapterPosition(), view));

            share.setOnClickListener(view -> {
                try {
                    Intent shareIntent = AppUtils.getVlogsShareIntent(responseData.getAuthor().getUserType(),
                            responseData.getAuthor().getBlogTitleSlug(),
                            responseData.getTitleSlug(), view.getContext().getString(R.string.check_out_momvlog),
                            responseData.getTitle(), responseData.getAuthor().getFirstName() + " "
                                    + responseData.getAuthor().getLastName(), "VD_Generic_Share", "Share_Android");
                    view.getContext().startActivity(Intent.createChooser(shareIntent, "Momspresso"));
                    Utils.shareEventTracking(context, "Video Detail", "Share_Android", "VD_Generic_Share");
                } catch (Exception e) {
                    FirebaseCrashlytics.getInstance().recordException(e);
                    Log.d("MC4kException", Log.getStackTraceString(e));
                }
            });

            collectionAdd.setOnClickListener(view -> {
                try {
                    AddCollectionAndCollectionItemDialogFragment addCollectionAndCollectionitemDialogFragment =
                            new AddCollectionAndCollectionItemDialogFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("articleId",
                            vlogsListingAndDetailResults.get(getAdapterPosition())
                                    .getId());
                    bundle.putString("type", AppConstants.VIDEO_COLLECTION_TYPE);
                    addCollectionAndCollectionitemDialogFragment.setArguments(bundle);
                    addCollectionAndCollectionitemDialogFragment.show(fm, "collectionAdd");
                    Utils.pushProfileEvents(context, "CTA_Vlog_Add_To_Collection",
                            "VideoRecyclerViewAdapter", "Add to Collection", "-");
                } catch (Exception e) {
                    FirebaseCrashlytics.getInstance().recordException(e);
                    Log.d("MC4kException", Log.getStackTraceString(e));
                }
            });

            whatsapp.setOnClickListener(v -> {
                String shareUrl = AppUtils.getVlogsShareUrl(responseData.getAuthor().getUserType(),
                        responseData.getAuthor().getBlogTitleSlug(),
                        responseData.getTitleSlug());
                if (StringUtils.isNullOrEmpty(shareUrl)) {
                    Toast.makeText(context, context.getString(R.string.moderation_or_share_whatsapp_fail),
                            Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        String shareData;
                        if (StringUtils.isNullOrEmpty(shareUrl)) {
                            shareData = context.getString(R.string.check_out_momvlog) + "\"" + responseData.getTitle()
                                    + "\" by " + responseData.getAuthor().getFirstName() + " " + responseData
                                    .getAuthor()
                                    .getLastName();
                        } else {
                            shareData = context.getString(R.string.check_out_momvlog) + "\"" + responseData.getTitle()
                                    + "\" by " + responseData.getAuthor().getFirstName() + " " + responseData
                                    .getAuthor()
                                    .getLastName() + ".\nWatch Here: " + AppUtils
                                    .getUtmParamsAppendedShareUrl(shareUrl, "VD_Whatsapp_Share", "Share_Android");
                        }
                        Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
                        whatsappIntent.setType("text/plain");
                        whatsappIntent.setPackage("com.whatsapp");
                        whatsappIntent.putExtra(Intent.EXTRA_TEXT, shareData);
                        context.startActivity(whatsappIntent);
                        Utils.shareEventTracking(context, "Video Detail", "Share_Android", "VD_Whatsapp_Share");
                    } catch (android.content.ActivityNotFoundException ex) {
                        Toast.makeText(context,
                                context.getString(R.string.moderation_or_share_whatsapp_not_installed),
                                Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        FirebaseCrashlytics.getInstance().recordException(e);
                        Log.d("MC4kException", Log.getStackTraceString(e));
                    }
                }
            });
            commentCount.setOnClickListener(view -> {
                try {
                    ((ParallelFeedActivity) context)
                            .openViewCommentDialog(responseData.getCommentUri(),
                                    AppUtils.getVlogsShareUrl(responseData.getAuthor().getUserType(),
                                            responseData.getAuthor().getBlogTitleSlug(),
                                            responseData.getTitleSlug()),
                                    responseData.getAuthor().getId(),
                                    responseData.getAuthor().getFirstName() + " " + responseData.getAuthor()
                                            .getLastName(), responseData.getId(), responseData.getTitleSlug());
                } catch (Exception e) {
                    FirebaseCrashlytics.getInstance().recordException(e);
                    Log.d("MC4kException", Log.getStackTraceString(e));
                }
            });
            comment.setOnClickListener(view -> {
                try {
                    Utils.shareEventTracking(context, "Video Detail", "Comment_Android", "VlogDetail_Comment");
                    ((ParallelFeedActivity) context)
                            .openViewCommentDialog(responseData.getCommentUri(),
                                    AppUtils.getVlogsShareUrl(responseData.getAuthor().getUserType(),
                                            responseData.getAuthor().getBlogTitleSlug(),
                                            responseData.getTitleSlug()),
                                    responseData.getAuthor().getId(),
                                    responseData.getAuthor().getFirstName() + " " + responseData.getAuthor()
                                            .getLastName(), responseData.getId(), responseData.getTitleSlug());
                } catch (Exception e) {
                    FirebaseCrashlytics.getInstance().recordException(e);
                    Log.d("MC4kException", Log.getStackTraceString(e));
                }
            });

            if (StringUtils.isNullOrEmpty(responseData.getThumbnail())) {
                Glide.with(context).asBitmap().load(R.drawable.default_article)
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap bitmap,
                                    @Nullable Transition<? super Bitmap> transition) {
                                int w = bitmap.getWidth();
                                int h = bitmap.getHeight();
                                Log.e("width and height", w + " * " + h);

                                float ratio = ((float) h / (float) w);
                                videoLayout.getLayoutParams().height = Math
                                        .round(ratio * context.getResources().getDisplayMetrics().widthPixels);
                                videoLayout.getLayoutParams().width = Math
                                        .round(context.getResources().getDisplayMetrics().widthPixels);
                                coverImageView.setImageBitmap(bitmap);
                            }
                        });
            } else {
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
                            }
                        });
            }
        }
    }

    public class ChallengeCardHolder extends BaseViewHolder implements View.OnClickListener {

        TextView textViewTitle;
        TextView challengeHandle;
        TextView followText;
        TextView likeCount;
        public RelativeLayout videoCell;
        public ImageView coverImageView;
        public ProgressBar progressBar;
        RelativeLayout videoLayout;
        View parent;
        MomspressoButtonWidget participate;
        TextView seeMoreChallenge;
        ImageView shareChallengeImageView;

        public ChallengeCardHolder(View itemView) {
            super(itemView);
            videoCell = itemView.findViewById(R.id.video_cell);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            challengeHandle = itemView.findViewById(R.id.challengeHandle);
            videoLayout = itemView.findViewById(R.id.video_layout);
            coverImageView = itemView.findViewById(R.id.cover);
            progressBar = itemView.findViewById(R.id.progressBar);
            followText = itemView.findViewById(R.id.follow_textview);
            likeCount = itemView.findViewById(R.id.viewsLike);
            participate = itemView.findViewById(R.id.participate_textview);
            seeMoreChallenge = itemView.findViewById(R.id.seeMoreChallenge);
            shareChallengeImageView = itemView.findViewById(R.id.shareChallengeImageView);
            parent = itemView;

            participate.setOnClickListener(this);
            seeMoreChallenge.setOnClickListener(this);
            shareChallengeImageView.setOnClickListener(this);
        }

        @Override
        public void onBind(int position) {
            super.onBind(position);
            parent.setTag(this);

            Topics responseData = vlogsListingAndDetailResults.get(position).getChallengeInfo();
            challengeId = responseData.getId();
            try {
                textViewTitle.setText(AppUtils.stripHtml(
                        responseData.getExtraData().get(0).getChallenge().getRules()));
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
            }
            challengeHandle.setText(responseData.getDisplay_name());
            if (StringUtils
                    .isNullOrEmpty(responseData.getExtraData().get(0).getChallenge().getImageUrl())) {
                Glide.with(context).asBitmap().load(R.drawable.default_article)
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap bitmap,
                                    @Nullable Transition<? super Bitmap> transition) {
                                int w = bitmap.getWidth();
                                int h = bitmap.getHeight();
                                Log.e("width and height", w + " * " + h);

                                float ratio = ((float) h / (float) w);
                                videoLayout.getLayoutParams().height = Math
                                        .round(ratio * context.getResources().getDisplayMetrics().widthPixels);
                                videoLayout.getLayoutParams().width = Math
                                        .round(context.getResources().getDisplayMetrics().widthPixels);
                                coverImageView.setImageBitmap(bitmap);
                            }
                        });
            } else {
                Glide.with(context)
                        .asBitmap()
                        .load(responseData.getExtraData().get(0).getChallenge().getImageUrl())
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
                            }
                        });
            }
        }

        protected void clear() {

        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.participate_textview:
                    if (!StringUtils.isNullOrEmpty(challengeId)) {
                        Intent intent = new Intent(context, NewVideoChallengeActivity.class);
                        intent.putExtra(Constants.CHALLENGE_ID, challengeId);
                        context.startActivity(intent);
                    }
                    break;
                case R.id.seeMoreChallenge:
                    Intent vlogsIntent = new Intent(context, CategoryVideosListingActivity.class);
                    vlogsIntent.putExtra("categoryId", "" + AppConstants.VIDEO_CHALLENGE_ID);
                    context.startActivity(vlogsIntent);
                    break;
                case R.id.shareChallengeImageView:
                    if (!StringUtils.isNullOrEmpty(challengeId)) {
                        AppUtils.shareGenericLinkWithSuccessStatus(view.getContext(),
                                AppConstants.VLOG_CHALLENGES_BASE_SHARE_URL + challengeId);
                    }
                    break;
                default:
            }
        }
    }

    public class FollowFollowingCarousal extends BaseViewHolder implements View.OnClickListener {

        ShimmerFrameLayout shimmerLayout;
        View spacingView;
        TextView headerTextView;
        View divider1;
        View divider2;
        View divider3;
        View divider4;
        View divider5;
        LinearLayout carosalContainer1;
        LinearLayout carosalContainer2;
        LinearLayout carosalContainer3;
        LinearLayout carosalContainer4;
        LinearLayout carosalContainer5;
        LinearLayout carosalContainer6;
        HorizontalScrollView scroll;
        ImageView authorImageView1;
        ImageView authorImageView2;
        ImageView authorImageView3;
        ImageView authorImageView4;
        ImageView authorImageView5;
        ImageView authorImageView6;
        TextView authorNameTextView1;
        TextView authorRankTextView1;
        TextView authorFollowTextView1;
        TextView authorNameTextView2;
        TextView authorRankTextView2;
        TextView authorFollowTextView2;
        TextView authorNameTextView3;
        TextView authorRankTextView3;
        TextView authorFollowTextView3;
        TextView authorNameTextView4;
        TextView authorRankTextView4;
        TextView authorFollowTextView4;
        TextView authorNameTextView5;
        TextView authorRankTextView5;
        TextView authorFollowTextView5;
        TextView authorNameTextView6;
        TextView authorRankTextView6;
        TextView authorFollowTextView6;
        ProgressBar progress1;
        ProgressBar progress2;
        ProgressBar progress3;
        ProgressBar progress4;
        ProgressBar progress5;
        ProgressBar progress6;


        FollowFollowingCarousal(View itemView) {
            super(itemView);
            itemView.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.video_feed_bg));
            spacingView = itemView.findViewById(R.id.spacingView);
            headerTextView = itemView.findViewById(R.id.headerTextView);
            headerTextView.setTextColor(Color.parseColor("#D1D1D1"));
            progress1 = itemView.findViewById(R.id.progress1);
            progress2 = itemView.findViewById(R.id.progress2);
            progress3 = itemView.findViewById(R.id.progress3);
            progress4 = itemView.findViewById(R.id.progress4);
            progress5 = itemView.findViewById(R.id.progress5);
            progress6 = itemView.findViewById(R.id.progress6);
            shimmerLayout = itemView.findViewById(R.id.shimmerLayout);
            shimmerLayout.setBackgroundColor(ContextCompat.getColor(shimmerLayout.getContext(), R.color.video_feed_bg));
            divider1 = itemView.findViewById(R.id.divider1);
            divider1.setBackgroundColor(Color.parseColor("#303030"));
            divider2 = itemView.findViewById(R.id.divider2);
            divider2.setBackgroundColor(Color.parseColor("#303030"));
            divider3 = itemView.findViewById(R.id.divider3);
            divider3.setBackgroundColor(Color.parseColor("#303030"));
            divider4 = itemView.findViewById(R.id.divider4);
            divider4.setBackgroundColor(Color.parseColor("#303030"));
            divider5 = itemView.findViewById(R.id.divider5);
            divider5.setBackgroundColor(Color.parseColor("#303030"));
            carosalContainer1 = itemView.findViewById(R.id.carosalContainer1);
            carosalContainer2 = itemView.findViewById(R.id.carosalContainer2);
            carosalContainer3 = itemView.findViewById(R.id.carosalContainer3);
            carosalContainer4 = itemView.findViewById(R.id.carosalContainer4);
            carosalContainer5 = itemView.findViewById(R.id.carosalContainer5);
            carosalContainer6 = itemView.findViewById(R.id.carosalContainer6);
            authorImageView1 = itemView.findViewById(R.id.authorImageView1);
            authorImageView2 = itemView.findViewById(R.id.authorImageView2);
            authorImageView3 = itemView.findViewById(R.id.authorImageView3);
            authorImageView4 = itemView.findViewById(R.id.authorImageView4);
            authorImageView5 = itemView.findViewById(R.id.authorImageView5);
            authorImageView6 = itemView.findViewById(R.id.authorImageView6);
            authorNameTextView1 = itemView.findViewById(R.id.authorNameTextView1);
            authorNameTextView1.setTextColor(Color.parseColor("#D1D1D1"));
            authorNameTextView2 = itemView.findViewById(R.id.authorNameTextView2);
            authorNameTextView2.setTextColor(Color.parseColor("#D1D1D1"));
            authorNameTextView3 = itemView.findViewById(R.id.authorNameTextView3);
            authorNameTextView3.setTextColor(Color.parseColor("#D1D1D1"));
            authorNameTextView4 = itemView.findViewById(R.id.authorNameTextView4);
            authorNameTextView4.setTextColor(Color.parseColor("#D1D1D1"));
            authorNameTextView5 = itemView.findViewById(R.id.authorNameTextView5);
            authorNameTextView5.setTextColor(Color.parseColor("#D1D1D1"));
            authorNameTextView6 = itemView.findViewById(R.id.authorNameTextView6);
            authorNameTextView6.setTextColor(Color.parseColor("#D1D1D1"));
            authorRankTextView1 = itemView.findViewById(R.id.authorRankTextView1);
            authorRankTextView2 = itemView.findViewById(R.id.authorRankTextView2);
            authorRankTextView3 = itemView.findViewById(R.id.authorRankTextView3);
            authorRankTextView4 = itemView.findViewById(R.id.authorRankTextView4);
            authorRankTextView5 = itemView.findViewById(R.id.authorRankTextView5);
            authorRankTextView6 = itemView.findViewById(R.id.authorRankTextView6);
            authorFollowTextView1 = itemView.findViewById(R.id.authorFollowTextView1);
            authorFollowTextView2 = itemView.findViewById(R.id.authorFollowTextView2);
            authorFollowTextView3 = itemView.findViewById(R.id.authorFollowTextView3);
            authorFollowTextView4 = itemView.findViewById(R.id.authorFollowTextView4);
            authorFollowTextView5 = itemView.findViewById(R.id.authorFollowTextView5);
            authorFollowTextView6 = itemView.findViewById(R.id.authorFollowTextView6);
            scroll = itemView.findViewById(R.id.scroll);
            authorFollowTextView1.setOnClickListener(this);
            authorFollowTextView2.setOnClickListener(this);
            authorFollowTextView3.setOnClickListener(this);
            authorFollowTextView4.setOnClickListener(this);
            authorFollowTextView5.setOnClickListener(this);
            authorFollowTextView6.setOnClickListener(this);
            carosalContainer1.setOnClickListener(this);
            carosalContainer2.setOnClickListener(this);
            carosalContainer3.setOnClickListener(this);
            carosalContainer4.setOnClickListener(this);
            carosalContainer5.setOnClickListener(this);
            carosalContainer6.setOnClickListener(this);
            spacingView.setVisibility(View.VISIBLE);
        }

        @Override
        protected void clear() {

        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.authorFollowTextView1:
                    if (vlogsListingAndDetailResults.get(getAdapterPosition())
                            .getCarouselVideoList().get(0)
                            .getFollowing()) {
                        unFollowApiCall(
                                vlogsListingAndDetailResults.get(getAdapterPosition())
                                        .getCarouselVideoList().get(0)
                                        .getDynamoId(),
                                getAdapterPosition(),
                                0,
                                authorFollowTextView1);
                    } else {
                        followApiCall(
                                vlogsListingAndDetailResults.get(getAdapterPosition())
                                        .getCarouselVideoList().get(0)
                                        .getDynamoId(),
                                getAdapterPosition(),
                                0,
                                authorFollowTextView1);
                    }
                    break;
                case R.id.authorFollowTextView2:
                    if (vlogsListingAndDetailResults.get(getAdapterPosition())
                            .getCarouselVideoList().get(1)
                            .getFollowing()) {
                        unFollowApiCall(
                                vlogsListingAndDetailResults.get(getAdapterPosition())
                                        .getCarouselVideoList().get(1)
                                        .getDynamoId(),
                                getAdapterPosition(),
                                1,
                                authorFollowTextView2);
                    } else {
                        followApiCall(
                                vlogsListingAndDetailResults.get(getAdapterPosition())
                                        .getCarouselVideoList().get(1)
                                        .getDynamoId(),
                                getAdapterPosition(),
                                1,
                                authorFollowTextView2);
                    }
                    break;
                case R.id.authorFollowTextView3:
                    if (vlogsListingAndDetailResults.get(getAdapterPosition())
                            .getCarouselVideoList().get(2)
                            .getFollowing()) {
                        unFollowApiCall(
                                vlogsListingAndDetailResults.get(getAdapterPosition())
                                        .getCarouselVideoList().get(2)
                                        .getDynamoId(),
                                getAdapterPosition(),
                                2,
                                authorFollowTextView3);
                    } else {
                        followApiCall(
                                vlogsListingAndDetailResults.get(getAdapterPosition())
                                        .getCarouselVideoList().get(2)
                                        .getDynamoId(),
                                getAdapterPosition(),
                                2,
                                authorFollowTextView3);
                    }
                    break;
                case R.id.authorFollowTextView4:
                    if (vlogsListingAndDetailResults.get(getAdapterPosition())
                            .getCarouselVideoList().get(3)
                            .getFollowing()) {
                        unFollowApiCall(
                                vlogsListingAndDetailResults.get(getAdapterPosition())
                                        .getCarouselVideoList().get(3)
                                        .getDynamoId(),
                                getAdapterPosition(),
                                3,
                                authorFollowTextView4);
                    } else {
                        followApiCall(
                                vlogsListingAndDetailResults.get(getAdapterPosition())
                                        .getCarouselVideoList().get(3)
                                        .getDynamoId(),
                                getAdapterPosition(),
                                3,
                                authorFollowTextView4);
                    }
                    break;
                case R.id.authorFollowTextView5:
                    if (vlogsListingAndDetailResults.get(getAdapterPosition())
                            .getCarouselVideoList().get(4)
                            .getFollowing()) {
                        unFollowApiCall(
                                vlogsListingAndDetailResults.get(getAdapterPosition())
                                        .getCarouselVideoList().get(4)
                                        .getDynamoId(),
                                getAdapterPosition(),
                                4,
                                authorFollowTextView5);
                    } else {
                        followApiCall(
                                vlogsListingAndDetailResults.get(getAdapterPosition())
                                        .getCarouselVideoList().get(4)
                                        .getDynamoId(),
                                getAdapterPosition(),
                                4,
                                authorFollowTextView5);
                    }
                    break;
                case R.id.authorFollowTextView6:
                    if (vlogsListingAndDetailResults.get(getAdapterPosition())
                            .getCarouselVideoList().get(5)
                            .getFollowing()) {
                        unFollowApiCall(
                                vlogsListingAndDetailResults.get(getAdapterPosition())
                                        .getCarouselVideoList().get(5)
                                        .getDynamoId(),
                                getAdapterPosition(),
                                5,
                                authorFollowTextView6);
                    } else {
                        followApiCall(
                                vlogsListingAndDetailResults.get(getAdapterPosition())
                                        .getCarouselVideoList().get(5)
                                        .getDynamoId(),
                                getAdapterPosition(),
                                5,
                                authorFollowTextView6);
                    }
                    break;
                case R.id.carosalContainer1:
                    Intent intent1 = new Intent(context, UserProfileActivity.class);
                    intent1.putExtra(Constants.USER_ID,
                            vlogsListingAndDetailResults.get(getAdapterPosition())
                                    .getCarouselVideoList().get(0)
                                    .getDynamoId());
                    context.startActivity(intent1);
                    break;
                case R.id.carosalContainer2:

                    Intent intent2 = new Intent(context, UserProfileActivity.class);
                    intent2.putExtra(Constants.USER_ID,
                            vlogsListingAndDetailResults.get(getAdapterPosition())
                                    .getCarouselVideoList().get(1)
                                    .getDynamoId());
                    context.startActivity(intent2);
                    break;
                case R.id.carosalContainer3:
                    Intent intent3 = new Intent(context, UserProfileActivity.class);
                    intent3.putExtra(Constants.USER_ID,
                            vlogsListingAndDetailResults.get(getAdapterPosition())
                                    .getCarouselVideoList().get(2)
                                    .getDynamoId());
                    context.startActivity(intent3);
                    break;
                case R.id.carosalContainer4:
                    Intent intent4 = new Intent(context, UserProfileActivity.class);
                    intent4.putExtra(Constants.USER_ID,
                            vlogsListingAndDetailResults.get(getAdapterPosition())
                                    .getCarouselVideoList().get(3)
                                    .getDynamoId());
                    context.startActivity(intent4);
                    break;
                case R.id.carosalContainer5:
                    Intent intent5 = new Intent(context, UserProfileActivity.class);
                    intent5.putExtra(Constants.USER_ID,
                            vlogsListingAndDetailResults.get(getAdapterPosition())
                                    .getCarouselVideoList().get(4)
                                    .getDynamoId());
                    context.startActivity(intent5);
                    break;
                case R.id.carosalContainer6:
                    Intent intent6 = new Intent(context, UserProfileActivity.class);
                    intent6.putExtra(Constants.USER_ID,
                            vlogsListingAndDetailResults.get(getAdapterPosition())
                                    .getCarouselVideoList().get(5)
                                    .getDynamoId());
                    context.startActivity(intent6);
                    break;
                default:
            }
        }
    }

    private void unFollowApiCall(String authorId,
            int position,
            int index,
            TextView followFollowingTextView) {
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        FollowAPI vlogsListingAndDetailsApi = retrofit.create(FollowAPI.class);
        FollowUnfollowUserRequest followUnfollowUserRequest = new FollowUnfollowUserRequest();
        followUnfollowUserRequest.setFollowee_id(authorId);
        Call<FollowUnfollowUserResponse> call = vlogsListingAndDetailsApi.unfollowUserV2(followUnfollowUserRequest);
        call.enqueue(new Callback<FollowUnfollowUserResponse>() {
            @Override
            public void onResponse(@NonNull Call<FollowUnfollowUserResponse> call,
                    @NonNull Response<FollowUnfollowUserResponse> response) {
                if (response.body() == null) {
                    return;
                }
                try {
                    FollowUnfollowUserResponse responseData = response.body();
                    if (responseData.getCode() == 200 && "success".equals(responseData.getStatus())) {
                        Intent followIntent = new Intent(followFollowingTextView.getContext(),
                                SyncUserFollowingList.class);
                        followFollowingTextView.getContext().startService(followIntent);
                        vlogsListingAndDetailResults.get(position).getCarouselVideoList().get(index)
                                .setFollowing(false);
                        GradientDrawable myGrad = (GradientDrawable) followFollowingTextView.getBackground();
                        myGrad.setStroke(2, ContextCompat.getColor(context, R.color.app_red));
                        followFollowingTextView.setTextColor(ContextCompat.getColor(context, R.color.white));
                        myGrad.setColor(ContextCompat.getColor(context, R.color.app_red));
                        followFollowingTextView.setText(StringUtils
                                .firstLetterToUpperCase(
                                        context.getResources().getString(R.string.ad_follow_author).toLowerCase()));
                        ToastUtils.showToast(context, responseData.getData().getMsg());
                    } else {
                        ToastUtils.showToast(context, responseData.getData().getMsg());
                    }
                } catch (Exception e) {
                    FirebaseCrashlytics.getInstance().recordException(e);
                    Log.d("MC4kException", Log.getStackTraceString(e));
                    ToastUtils.showToast(context, e.getMessage());
                }
            }

            @Override
            public void onFailure(@NonNull Call<FollowUnfollowUserResponse> call, @NonNull Throwable e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
                ToastUtils.showToast(context, "something went wrong");
            }
        });
    }


    private void followApiCall(String authorId,
            int position,
            int index,
            TextView followFollowingTextView) {
        Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
        FollowAPI vlogsListingAndDetailsApi = retrofit.create(FollowAPI.class);
        FollowUnfollowUserRequest followUnfollowUserRequest = new FollowUnfollowUserRequest();
        followUnfollowUserRequest.setFollowee_id(authorId);
        Call<FollowUnfollowUserResponse> call = vlogsListingAndDetailsApi.followUserV2(followUnfollowUserRequest);
        call.enqueue(new Callback<FollowUnfollowUserResponse>() {
            @Override
            public void onResponse(@NonNull Call<FollowUnfollowUserResponse> call,
                    @NonNull Response<FollowUnfollowUserResponse> response) {
                if (response.body() == null) {
                    return;
                }
                try {
                    FollowUnfollowUserResponse responseData = response.body();
                    if (responseData.getCode() == 200 && "success".equals(responseData.getStatus())) {
                        Intent followIntent = new Intent(followFollowingTextView.getContext(),
                                SyncUserFollowingList.class);
                        followFollowingTextView.getContext().startService(followIntent);
                        vlogsListingAndDetailResults.get(position).getCarouselVideoList().get(index)
                                .setFollowing(true);
                        GradientDrawable myGrad = (GradientDrawable) followFollowingTextView.getBackground();
                        myGrad.setStroke(2, ContextCompat.getColor(context, R.color.color_BABABA));
                        followFollowingTextView.setTextColor(ContextCompat.getColor(context, R.color.color_BABABA));
                        myGrad.setColor(ContextCompat.getColor(context, R.color.video_feed_bg));
                        followFollowingTextView.setText(StringUtils
                                .firstLetterToUpperCase(
                                        context.getResources().getString(R.string.ad_following_author).toLowerCase()));
                        ToastUtils.showToast(context, responseData.getData().getMsg());
                    } else {
                        ToastUtils.showToast(context, responseData.getData().getMsg());
                    }
                } catch (Exception e) {
                    FirebaseCrashlytics.getInstance().recordException(e);
                    Log.d("MC4kException", Log.getStackTraceString(e));
                    ToastUtils.showToast(context, e.getMessage());
                }
            }

            @Override
            public void onFailure(@NonNull Call<FollowUnfollowUserResponse> call, @NonNull Throwable e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                Log.d("MC4kException", Log.getStackTraceString(e));
                ToastUtils.showToast(context, "something went wrong");
            }
        });
    }

    private void processVlogersData(FollowFollowingCarousal holder, ArrayList<UserDetailResult> dataList,
            int position) {
        if (null != dataList && !dataList.isEmpty()) {
            ((VlogsListingAndDetailResult) vlogsListingAndDetailResults.get(position)).setCarouselVideoList(dataList);
            populateCarouselFollowFollowing(holder, dataList);
        }
    }

    private void populateCarouselFollowFollowing(FollowFollowingCarousal holder,
            ArrayList<UserDetailResult> carosalList) {
        if (carosalList.isEmpty()) {
            holder.scroll.setVisibility(View.GONE);
        } else {
            holder.scroll.setVisibility(View.VISIBLE);
        }
        if (carosalList.size() >= 1) {
            holder.carosalContainer1.setVisibility(View.VISIBLE);
            updateCarosal(
                    holder.authorFollowTextView1,
                    holder.authorImageView1,
                    holder.authorNameTextView1,
                    holder.authorRankTextView1,
                    holder.progress1,
                    carosalList.get(0)
            );
        }
        if (carosalList.size() >= 2) {
            holder.carosalContainer2.setVisibility(View.VISIBLE);
            updateCarosal(
                    holder.authorFollowTextView2,
                    holder.authorImageView2,
                    holder.authorNameTextView2,
                    holder.authorRankTextView2,
                    holder.progress2,
                    carosalList.get(1)
            );
        }
        if (carosalList.size() >= 3) {
            holder.carosalContainer3.setVisibility(View.VISIBLE);
            updateCarosal(
                    holder.authorFollowTextView3,
                    holder.authorImageView3,
                    holder.authorNameTextView3,
                    holder.authorRankTextView3,
                    holder.progress3,
                    carosalList.get(2)
            );
        }
        if (carosalList.size() >= 4) {
            holder.carosalContainer4.setVisibility(View.VISIBLE);
            updateCarosal(
                    holder.authorFollowTextView4,
                    holder.authorImageView4,
                    holder.authorNameTextView4,
                    holder.authorRankTextView4,
                    holder.progress4,
                    carosalList.get(3)
            );
        }
        if (carosalList.size() >= 5) {
            holder.carosalContainer5.setVisibility(View.VISIBLE);
            updateCarosal(
                    holder.authorFollowTextView5,
                    holder.authorImageView5,
                    holder.authorNameTextView5,
                    holder.authorRankTextView5,
                    holder.progress5,
                    carosalList.get(4)
            );
        }
        if (carosalList.size() >= 6) {
            holder.carosalContainer6.setVisibility(View.VISIBLE);
            updateCarosal(
                    holder.authorFollowTextView6,
                    holder.authorImageView6,
                    holder.authorNameTextView6,
                    holder.authorRankTextView6,
                    holder.progress6,
                    carosalList.get(5)
            );
        }
    }

    private void updateCarosal(TextView followTextView, ImageView authorImageView, TextView authorNameTextView,
            TextView authorRankTextView, ProgressBar progressBar, UserDetailResult carosalList) {
        Picasso.get().load(carosalList.getProfilePicUrl().getClientApp()).error(R.drawable.default_article)
                .into(authorImageView, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {
                    }

                    @Override
                    public void onError(Exception e) {
                    }
                });
        if (carosalList.getFollowing()) {
            GradientDrawable myGrad = (GradientDrawable) followTextView.getBackground();
            myGrad.setStroke(2, ContextCompat.getColor(context, R.color.color_BABABA));
            followTextView.setTextColor(ContextCompat.getColor(context, R.color.color_BABABA));
            myGrad.setColor(ContextCompat.getColor(context, R.color.video_feed_bg));
            followTextView.setText(
                    StringUtils.firstLetterToUpperCase(context.getString(R.string.ad_following_author).toLowerCase()));
        } else {
            GradientDrawable myGrad = (GradientDrawable) followTextView.getBackground();
            myGrad.setStroke(2, ContextCompat.getColor(context, R.color.app_red));
            myGrad.setColor(ContextCompat.getColor(context, R.color.app_red));
            followTextView.setTextColor(ContextCompat.getColor(context, R.color.white));
            followTextView.setText(
                    StringUtils.firstLetterToUpperCase(context.getString(R.string.ad_follow_author).toLowerCase()));
        }
        authorNameTextView.setText(
                StringUtils.firstLetterToUpperCase(carosalList.getFirstName().trim().toLowerCase()) + " " + StringUtils
                        .firstLetterToUpperCase(carosalList.getLastName().trim().toLowerCase()));
        authorRankTextView.setText(
                StringUtils.firstLetterToUpperCase(context.getString(R.string.myprofile_rank_label)) + ": "
                        + carosalList.getRank());
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