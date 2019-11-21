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

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.crashlytics.android.Crashlytics;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.response.VlogsListingAndDetailResult;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.ui.BaseViewHolder;
import com.mycity4kids.ui.activity.ParallelFeedActivity;
import com.mycity4kids.ui.fragment.AddCollectionAndCollectionItemDialogFragment;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

public class VideoRecyclerViewAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    public static final int VIEW_TYPE_EMPTY = 0;
    public static final int VIEW_TYPE_NORMAL = 1;
    private Context mContext;
    private String followUnfollowText;
    private boolean enableDisableFollow;
    private ViewHolder mHolder;
    private String likeStatus;
    private boolean isRecommendRequestRunning;
    private String userDynamoId;
    private String bookmarkStatus;
    FragmentManager fm;

    //    private List<VideoInfo> mInfoList;
    private ArrayList<VlogsListingAndDetailResult> mInfoList;

    public VideoRecyclerViewAdapter(Context mContext, FragmentManager fm) {
        this.fm = fm;
        this.mContext = mContext;
        userDynamoId = SharedPrefUtils.getUserDetailModel(mContext).getDynamoId();
    }

    public void setText(int pos, String followUnfollowText) {
        this.followUnfollowText = followUnfollowText;
        notifyItemChanged(pos, mHolder.followText);
    }


    public void setListUpdate(int updatePos, ArrayList<VlogsListingAndDetailResult> infoList) {
        mInfoList = infoList;
        notifyItemChanged(updatePos, mHolder.followText);
    }

    public void updateList(ArrayList<VlogsListingAndDetailResult> infoList) {
        mInfoList = infoList;
    }

    public void setList(int updatePos, ArrayList<VlogsListingAndDetailResult> infoList) {
        mInfoList = infoList;
        notifyItemChanged(updatePos, mHolder.heart);
    }

    public void setBookmark(int updatePos, ArrayList<VlogsListingAndDetailResult> infoList) {
        mInfoList = infoList;
        notifyItemChanged(updatePos, mHolder.mImgBookmark);
    }


    public void setTextFromResponse(boolean enableDisableFollow, String followUnfollowText) {
        this.enableDisableFollow = enableDisableFollow;
        this.followUnfollowText = followUnfollowText;
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
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        mHolder = (ViewHolder) holder;
        holder.onBind(position);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position, List<Object> payload) {
        mHolder = (ViewHolder) holder;
        holder.onBind(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
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
        TextView userHandle, followText, commentCount, viewsCount, likeCount;
        public RelativeLayout videoCell;
        public RelativeLayout videoLayout;
        public ImageView mCover, heart, share, whatsapp, three_dot, comment, mImgBookmark;
        public ProgressBar mProgressBar;
        public final View parent;
        ImageView userImage, collectionAdd;


        public ViewHolder(View itemView) {
            super(itemView);
            videoCell = itemView.findViewById(R.id.video_cell);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            userImage = itemView.findViewById(R.id.user_image);
            userHandle = itemView.findViewById(R.id.userHandle);
            videoLayout = itemView.findViewById(R.id.video_layout);
            mCover = itemView.findViewById(R.id.cover);
            mProgressBar = itemView.findViewById(R.id.progressBar);
            followText = itemView.findViewById(R.id.follow_textview);
            commentCount = itemView.findViewById(R.id.commentCount);
            comment = itemView.findViewById(R.id.comment);
            viewsCount = itemView.findViewById(R.id.viewsCount);
            likeCount = itemView.findViewById(R.id.viewsLike);
            heart = itemView.findViewById(R.id.heart);
            share = itemView.findViewById(R.id.share);
            whatsapp = itemView.findViewById(R.id.whatsapp);
            collectionAdd = itemView.findViewById(R.id.collectionAdd);
            three_dot = itemView.findViewById(R.id.three_dot);
            mImgBookmark = itemView.findViewById(R.id.bookmark);
            parent = itemView;
        }

        protected void clear() {

        }

        public void onBind(int position) {
            super.onBind(position);
            parent.setTag(this);

            VlogsListingAndDetailResult responseData = mInfoList.get(position);

            if (responseData.getIs_liked() != null && responseData.getIs_liked().equals("1")) {
                heart.setImageResource(R.drawable.ic_likevideofilled);
            } else {
                heart.setImageResource(R.drawable.ic_likevideo);
            }

            if (responseData.getIs_bookmark() != null && responseData.getIs_bookmark().equals("1")) {
                mImgBookmark.setImageResource(R.drawable.ic_bookmarked);
            } else {
                mImgBookmark.setImageResource(R.drawable.ic_bookmark);
            }

            if (responseData.isFollowed()) {
                followText.setText("Following");
            } else {
                followText.setText("Follow");
            }

            if (responseData.isBookmarked()) {

            } else {

            }
//            VlogsListingAndDetailResult videoInfo = responseData.getData().getResult();
            textViewTitle.setText(responseData.getTitle());
            makeTextViewResizable(textViewTitle, 2, " ..See More", true, responseData.getTitle());
            userHandle.setText(responseData.getAuthor().getFirstName() + " " + responseData.getAuthor().getLastName());
            try {
                Picasso.with(mContext).load(responseData.getAuthor().getProfilePic().getClientApp()).into(userImage);
            } catch (Exception e) {
                userImage.setImageResource(R.drawable.default_blogger_profile_img);
            }
            commentCount.setText(responseData.getComment_count());
            likeCount.setText(responseData.getLike_count() + " " + "Likes");
            viewsCount.setText(responseData.getView_count() + " " + "Views");
            followText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Utils.momVlogEvent(mContext, "Video Detail", "Follow", "", "android", SharedPrefUtils.getAppLocale(mContext), SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId(), String.valueOf(System.currentTimeMillis()), "Vlogs_Engagement_CTA", "", "");

                    ((ParallelFeedActivity) mContext).followAPICall(responseData.getAuthor().getId(), position);
                }
            });

            userImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((ParallelFeedActivity) mContext).openPublicProfile(responseData.getAuthor().getUserType(), responseData.getAuthor().getId(), responseData.getAuthor().getFirstName() + responseData.getAuthor().getLastName());
                }
            });
            userHandle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((ParallelFeedActivity) mContext).openPublicProfile(responseData.getAuthor().getUserType(), responseData.getAuthor().getId(), responseData.getAuthor().getFirstName() + responseData.getAuthor().getLastName());
                }
            });
            heart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (responseData.getIs_liked() != null && responseData.getIs_liked().equals("1")) {
                        likeStatus = "0";
                        ((ParallelFeedActivity) mContext).recommendUnrecommentArticleAPI(responseData.getId(), likeStatus, position);
                        Utils.momVlogEvent(mContext, "Video Detail", "DisLike", "", "android", SharedPrefUtils.getAppLocale(mContext), SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId(), String.valueOf(System.currentTimeMillis()), "Vlogs_Engagement_CTA", "", "");
                    } else {
                        likeStatus = "1";
                        ((ParallelFeedActivity) mContext).recommendUnrecommentArticleAPI(responseData.getId(), likeStatus, position);
                        Utils.momVlogEvent(mContext, "Video Detail", "Like", "", "android", SharedPrefUtils.getAppLocale(mContext), SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId(), String.valueOf(System.currentTimeMillis()), "Vlogs_Engagement_CTA", "", "");
                    }
                }
            });

            mImgBookmark.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (responseData.getIs_bookmark() != null && responseData.getIs_bookmark().equals("0")) {
                        bookmarkStatus = "1";
                    } else {
                        bookmarkStatus = "0";
                        Utils.momVlogEvent(mContext, "Video Detail", "Bookmark", "", "android", SharedPrefUtils.getAppLocale(mContext), SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId(), String.valueOf(System.currentTimeMillis()), "Vlogs_Engagement_CTA", "", "");
                    }
                    ((ParallelFeedActivity) mContext).addRemoveBookmark(bookmarkStatus, position, responseData.getAuthor().getId(), responseData.getId());

                }
            });

            share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");

                    if (StringUtils.isNullOrEmpty(getShareUrl(responseData))) {

                    } else {
                        shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, getShareUrl(responseData));
                        mContext.startActivity(Intent.createChooser(shareIntent, "Momspresso"));
                        Utils.pushShareArticleEvent(mContext, "DetailVideoScreen", userDynamoId + "", responseData.getId(), responseData.getAuthor().getId() + "~" + responseData.getAuthor().getFirstName() + " " + responseData.getAuthor().getLastName(), "CommonShare");
                    }

                    Utils.momVlogEvent(mContext, "Video Detail", "Share", "", "android", SharedPrefUtils.getAppLocale(mContext), SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId(), String.valueOf(System.currentTimeMillis()), "Vlogs_Engagement_CTA", "", "");

                }
            });

            collectionAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        AddCollectionAndCollectionItemDialogFragment addCollectionAndCollectionitemDialogFragment = new AddCollectionAndCollectionItemDialogFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("articleId", mInfoList.get(getAdapterPosition()).getId());
                        bundle.putString("type", AppConstants.VIDEO_COLLECTION_TYPE);
                        addCollectionAndCollectionitemDialogFragment.setArguments(bundle);
                        //   addCollectionAndCollectionitemDialogFragment.setTargetFragment(F, 0);
                        addCollectionAndCollectionitemDialogFragment.show(fm, "collectionAdd");
                    } catch (Exception e) {
                        Crashlytics.logException(e);
                        Log.d("MC4kException", Log.getStackTraceString(e));
                    }
                }
            });

            whatsapp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (StringUtils.isNullOrEmpty(getShareUrl(responseData))) {
                        Toast.makeText(mContext, mContext.getString(R.string.moderation_or_share_whatsapp_fail), Toast.LENGTH_SHORT).show();
                    } else {
                        Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
                        whatsappIntent.setType("text/plain");
                        whatsappIntent.setPackage("com.whatsapp");
                        whatsappIntent.putExtra(Intent.EXTRA_TEXT, mContext.getString(R.string.check_out_momvlog) + getShareUrl(responseData));
                        try {
                            mContext.startActivity(whatsappIntent);
                            Utils.pushShareArticleEvent(mContext, "DetailVideoScreen", userDynamoId + "", responseData.getId(), responseData.getAuthor().getId() + "~" + responseData.getAuthor().getFirstName() + " " + responseData.getAuthor().getLastName(), "Whatsapp");
                        } catch (android.content.ActivityNotFoundException ex) {
                            Toast.makeText(mContext, mContext.getString(R.string.moderation_or_share_whatsapp_not_installed), Toast.LENGTH_SHORT).show();
                        }
                        Utils.momVlogEvent(mContext, "Video Detail", "Whatsapp", "", "android", SharedPrefUtils.getAppLocale(mContext), SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId(), String.valueOf(System.currentTimeMillis()), "Vlogs_Engagement_CTA", "", "");

                    }
                }
            });
            commentCount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((ParallelFeedActivity) mContext).openViewCommentDialog(responseData.getCommentUri(), getShareUrl(responseData), responseData.getAuthor().getId(), responseData.getAuthor().getFirstName() + " " + responseData.getAuthor().getLastName(), responseData.getId());
                }
            });

            comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Utils.momVlogEvent(mContext, "Video Detail", "Add comment", "", "android", SharedPrefUtils.getAppLocale(mContext), SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId(), String.valueOf(System.currentTimeMillis()), "Vlogs_Engagement_CTA", "", "");

                    ((ParallelFeedActivity) mContext).openViewCommentDialog(responseData.getCommentUri(), getShareUrl(responseData), responseData.getAuthor().getId(), responseData.getAuthor().getFirstName() + " " + responseData.getAuthor().getLastName(), responseData.getId());
                }
            });

            three_dot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (responseData.getIs_bookmark() != null) {
                        PopupWindow popupwindow_obj = popupDisplay(responseData.getId(), responseData.getIs_bookmark());
                        popupwindow_obj.showAsDropDown(three_dot, -40, 18);
                    }
                }
            });

//            Glide.with(itemView.getContext())
//                    .load(responseData.getThumbnail()).apply(new RequestOptions().optionalCenterCrop())
//                    .into(mCover);

            Glide.with(mContext)
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
//                            mCover.getLayoutParams().height = heightInDp;
//                            mCover.getLayoutParams().width = widthInDp;
                            videoLayout.getLayoutParams().height = Math.round(ratio * mContext.getResources().getDisplayMetrics().widthPixels);
                            videoLayout.getLayoutParams().width = Math.round(mContext.getResources().getDisplayMetrics().widthPixels);

                            mCover.setImageBitmap(bitmap);
                            Log.e("from ratio", w + "   " + h + "   " + videoLayout.getLayoutParams().height + " * " + videoLayout.getLayoutParams().width);
                        }
                    });


//            Glide.with(itemView.getContext().getApplicationContext())
//                    .asBitmap()
//                    .load(responseData.getThumbnail())
//                    .into(new SimpleTarget<Bitmap>() {
//                        @Override
//                        public void onResourceReady(Bitmap bitmap,
//                                                    Transition<? super Bitmap> transition) {
//                            int w = bitmap.getWidth();
//                            int h = bitmap.getHeight();
//                            Log.e("width and height", w + " * " + h);
//
//                            int  heightInDp = Math.round(bitmap.getHeight() / itemView.getContext().getResources().getDisplayMetrics().density);
//                            int widthInDp = Math.round(bitmap.getWidth() / itemView.getContext().getResources().getDisplayMetrics().density);
////                            mCover.getLayoutParams().height = heightInDp;
////                            mCover.getLayoutParams().width = widthInDp;
//                            mCover.getLayoutParams().height = 500;
//                            mCover.getLayoutParams().width = 500;
//
//                            Log.e("width and height", widthInDp + " * " + heightInDp);
//                        }
//                    });
        }
    }

    public PopupWindow popupDisplay(String vidId, String isBookmarked) {

        final PopupWindow popupWindow = new PopupWindow(mContext);

        LayoutInflater inflater = (LayoutInflater) ((ParallelFeedActivity) mContext).getSystemService(Context.LAYOUT_INFLATER_SERVICE);

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

    public void makeTextViewResizable(final TextView tv, final int maxLine, final String expandText, final boolean viewMore, final String userBio) {

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
                /*if (maxLine == 0) {
                    int lineEndIndex = tv.getLayout().getLineEnd(0);
                    String text = tv.getText().subSequence(0, lineEndIndex - expandText.length() + 1) + " " + expandText;
                    tv.setText(text);
                    tv.setMovementMethod(LinkMovementMethod.getInstance());
                    tv.setText(
                            addClickablePartTextViewResizable(Html.fromHtml(tv.getText().toString()), tv, maxLine, expandText,
                                    viewMore, userBio), TextView.BufferType.SPANNABLE);
                } else if (maxLine > 0 && tv.getLineCount() >= maxLine) {
                    int lineEndIndex = tv.getLayout().getLineEnd(maxLine - 1);
                    String text = tv.getText().subSequence(0, lineEndIndex - expandText.length() + 1) + " " + expandText;
                    tv.setText(text);
                    tv.setMovementMethod(LinkMovementMethod.getInstance());
                    tv.setText(
                            addClickablePartTextViewResizable(Html.fromHtml(tv.getText().toString()), tv, maxLine, expandText,
                                    viewMore, userBio), TextView.BufferType.SPANNABLE);
                } else {
                }*/

                if (maxLine == 0) {
                    int lineEndIndex = tv.getLayout().getLineEnd(0);
                    String text = tv.getText().subSequence(0, lineEndIndex - expandText.length() + 1) + " " + expandText;
                    tv.setText(text);
                    tv.setMovementMethod(LinkMovementMethod.getInstance());
                    tv.setText(
                            addClickablePartTextViewResizable(Html.fromHtml(tv.getText().toString()), tv, maxLine, expandText,
                                    viewMore, userBio), TextView.BufferType.SPANNABLE);
                } else if (maxLine > 0 && tv.getLineCount() > maxLine) {
                    int lineEndIndex = tv.getLayout().getLineEnd(maxLine - 1);
                    if ((lineEndIndex - expandText.length() + 1) > 10) {
                        String text = tv.getText().subSequence(0, lineEndIndex - expandText.length() + 1) + " " + expandText;
                        tv.setText(text);
                        tv.setMovementMethod(LinkMovementMethod.getInstance());
                        tv.setText(
                                addClickablePartTextViewResizable(Html.fromHtml(tv.getText().toString()), tv, maxLine, expandText,
                                        viewMore, userBio), TextView.BufferType.SPANNABLE);
                    } else {
                        // int lineEndIndex1 = tv.getLayout().getLineEnd(maxLine-1);
                        String text = tv.getText().subSequence(0, lineEndIndex) + " " + expandText;
                        tv.setText(text);
                        tv.setMovementMethod(LinkMovementMethod.getInstance());
                        tv.setText(
                                addClickablePartTextViewResizable(Html.fromHtml(tv.getText().toString()), tv, maxLine, expandText,
                                        viewMore, userBio), TextView.BufferType.SPANNABLE);
                    }
                } else {

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
                    /*if (viewMore) {
                        tv.setLayoutParams(tv.getLayoutParams());
                        tv.setText(tv.getTag().toString(), TextView.BufferType.SPANNABLE);
                        tv.invalidate();
                        makeTextViewResizable(tv, 1, "See Less", false, userBio);
                    } else {
                        tv.setLayoutParams(tv.getLayoutParams());
                        tv.setText(tv.getTag().toString(), TextView.BufferType.SPANNABLE);
                        tv.invalidate();
                        makeTextViewResizable(tv, 2, ".. See More", true, userBio);
                    }*/
                    tv.setLayoutParams(tv.getLayoutParams());
                    tv.setText(userBio, TextView.BufferType.SPANNABLE);
                    tv.invalidate();
                }
            }, str.indexOf(spanableText), str.indexOf(spanableText) + spanableText.length(), 0);

        }
        return ssb;

    }

    public class MySpannable extends ClickableSpan {

        private boolean isUnderline = true;

        /**
         * Constructor
         */
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
            String deepLinkURL = null;
            String titleSlug = responseData.getTitleSlug();
            String authorType = responseData.getAuthor().getUserType();
            if (!StringUtils.isNullOrEmpty(authorType)) {

                if (AppConstants.USER_TYPE_BLOGGER.equals(authorType)) {
                    if (StringUtils.isNullOrEmpty(deepLinkURL)) {
                        String bSlug = responseData.getAuthor().getBlogTitleSlug();
                        if (StringUtils.isNullOrEmpty(bSlug)) {
                            shareUrl = AppConstants.VIDEO_ARTICLE_SHARE_URL + "video/" + responseData.getTitleSlug();
                        } else {
                            shareUrl = AppConstants.VIDEO_ARTICLE_SHARE_URL + bSlug + "/video/" + responseData.getTitleSlug();
                        }
                    } else {
                        shareUrl = deepLinkURL;
                    }
                } else if (AppConstants.USER_TYPE_EXPERT.equals(authorType)) {
                    if (StringUtils.isNullOrEmpty(deepLinkURL)) {
                        shareUrl = AppConstants.VIDEO_ARTICLE_SHARE_URL + "video/" + responseData.getTitleSlug();
                    } else {
                        shareUrl = deepLinkURL;
                    }
                } else if (AppConstants.USER_TYPE_EDITOR.equals(authorType)) {
                    if (StringUtils.isNullOrEmpty(deepLinkURL)) {
                        shareUrl = AppConstants.VIDEO_ARTICLE_SHARE_URL + "video/" + responseData.getTitleSlug();
                    } else {
                        shareUrl = deepLinkURL;
                    }
                } else if (AppConstants.USER_TYPE_EDITORIAL.equals(authorType)) {
                    if (StringUtils.isNullOrEmpty(deepLinkURL)) {
                        shareUrl = AppConstants.VIDEO_ARTICLE_SHARE_URL + "video/" + responseData.getTitleSlug();
                    } else {
                        shareUrl = deepLinkURL;
                    }
                } else if (AppConstants.USER_TYPE_FEATURED.equals(authorType)) {
                    if (StringUtils.isNullOrEmpty(deepLinkURL)) {
                        shareUrl = AppConstants.VIDEO_ARTICLE_SHARE_URL + "video/" + responseData.getTitleSlug();
                    } else {
                        shareUrl = deepLinkURL;
                    }
                } else if (AppConstants.USER_TYPE_USER.equals(authorType)) {
                    if (StringUtils.isNullOrEmpty(deepLinkURL)) {
                        String bSlug = responseData.getAuthor().getBlogTitleSlug();
                        if (StringUtils.isNullOrEmpty(bSlug)) {
                            shareUrl = AppConstants.VIDEO_ARTICLE_SHARE_URL + "video/" + responseData.getTitleSlug();
                        } else {
                            shareUrl = AppConstants.VIDEO_ARTICLE_SHARE_URL + bSlug + "/video/" + responseData.getTitleSlug();
                        }
                    } else {
                        shareUrl = deepLinkURL;
                    }
                }
            } else {
                // Default Author type set to Blogger
                if (StringUtils.isNullOrEmpty(deepLinkURL)) {
                    shareUrl = AppConstants.VIDEO_ARTICLE_SHARE_URL + responseData.getAuthor().getBlogTitleSlug() + "/video/" + titleSlug;
                } else {
                    shareUrl = deepLinkURL;
                }
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
