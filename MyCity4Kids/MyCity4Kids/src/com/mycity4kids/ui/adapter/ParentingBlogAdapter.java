package com.mycity4kids.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.enums.ParentingFilterType;
import com.mycity4kids.newmodels.bloggermodel.BlogItemModel;
import com.mycity4kids.ui.activity.ArticlesAndBlogsDetailsActivity;
import com.mycity4kids.ui.activity.DashboardActivity;
import com.mycity4kids.ui.activity.LoadWebViewActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by manish.soni on 27-07-2015.
 */
public class ParentingBlogAdapter extends BaseAdapter {
    private final float density;
    private final int screenWidth;
    Context context;
    ArrayList<BlogItemModel> datalist;

    public ParentingBlogAdapter(Context context, ArrayList<BlogItemModel> datalist) {
        this.context = context;
        this.datalist = datalist;
        density = context.getResources().getDisplayMetrics().density;
        screenWidth = context.getResources().getDisplayMetrics().widthPixels;
    }

    public void setListData(ArrayList<BlogItemModel> datalist) {
        this.datalist = datalist;
    }


    public ArrayList<BlogItemModel> getListData() {
        return datalist;
    }

    @Override
    public int getCount() {
        return datalist == null ? 0 : datalist.size();
    }

    @Override
    public Object getItem(int i) {
        return datalist.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int position, final View convertView, ViewGroup viewGroup) {

        View view = convertView;
        ViewHolder holder = null;
        if (view == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            view = inflater.inflate(R.layout.blog_list_item, viewGroup, false);

            holder = new ViewHolder();
            holder.bloggerName = (TextView) view.findViewById(R.id.blogger_name);
            holder.authorType = (TextView) view.findViewById(R.id.author_type);
            holder.authorRank = (TextView) view.findViewById(R.id.blog_rank);
//            holder.bloggerImage = (ImageView) view.findViewById(R.id.blogger_profile);
            holder.bloggerCover = (ImageView) view.findViewById(R.id.blogger_bg);
            holder.bloggerFollow = (TextView) view.findViewById(R.id.blog_follow_text);
//            holder.facebook = (ImageView) view.findViewById(R.id.facebook_);
//            holder.twitter = (ImageView) view.findViewById(R.id.twitter_);
//            holder.rss = (ImageView) view.findViewById(R.id.rss_);
            holder.description = (TextView) view.findViewById(R.id.blogger_desc);
            holder.moreDesc = (TextView) view.findViewById(R.id.more_text);
            holder.recentArticleLayout = (LinearLayout) view.findViewById(R.id.recent_article_frame);
            holder.articleBlock = (LinearLayout) view.findViewById(R.id.article_block);
            holder.aboutLayout = (RelativeLayout) view.findViewById(R.id.about_desc_layout);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.bloggerName.setText(datalist.get(position).getFirst_name() + " " + datalist.get(position).getLast_name());
        holder.bloggerName.setTextColor(Color.WHITE);
        holder.authorType.setText(datalist.get(position).getAuthor_type().toUpperCase());
        holder.authorType.setTextColor(Color.parseColor(datalist.get(position).getAuthor_color_code()));

        holder.description.invalidate();
        if (datalist.get(position).getMaxLineCount() == 0) {
            datalist.get(position).setMaxLineCount(holder.description.getLineCount());
        }

//        if (!StringUtils.isNullOrEmpty(datalist.get(position).getProfile_image())) {
//            Picasso.with(context).load(datalist.get(position).getProfile_image()).resize((int) (90 * density), (int) (100 * density)).centerCrop().into(holder.bloggerImage);
//        } else {
//            Picasso.with(context).load(R.drawable.default_img).resize((int) (90 * density), (int) (100 * density)).centerCrop().into(holder.bloggerImage);
//        }
        if (StringUtils.isNullOrEmpty(datalist.get(position).getProfile_image())) {
            Picasso.with(context).load(R.drawable.blog_bgnew).resize(screenWidth, (int) (200 * density)).centerCrop().placeholder(R.drawable.blog_bgnew).into(holder.bloggerCover);
        } else {
            Picasso.with(context).load(datalist.get(position).getProfile_image()).resize(screenWidth, (int) (200 * density)).centerCrop().placeholder(R.drawable.blog_bgnew).into(holder.bloggerCover);
        }
        if (StringUtils.isNullOrEmpty(datalist.get(position).getAbout_user())) {
            holder.aboutLayout.setVisibility(View.GONE);
        } else {
            holder.aboutLayout.setVisibility(View.VISIBLE);
            holder.description.setText(datalist.get(position).getAbout_user());
        }

        if (!StringUtils.isNullOrEmpty(String.valueOf(datalist.get(position).getAuthor_rank()))) {
            holder.authorRank.setText(String.valueOf(datalist.get(position).getAuthor_rank()));
        } else {
            holder.authorRank.setText("");
        }

        holder.recentArticleLayout.removeAllViews();
        if (datalist.get(position).getRecent_articles().size() == 0) {
            holder.articleBlock.setVisibility(View.GONE);

        } else {
            holder.articleBlock.setVisibility(View.VISIBLE);
            for (int i = 0; i < datalist.get(position).getRecent_articles().size(); i++) {

                final TextView article = new TextView(context);
                article.setTypeface(Typeface.create("sans-serif-light", Typeface.NORMAL));
                article.setText(datalist.get(position).getRecent_articles().get(i).getTitle());
                article.setTextColor(Color.parseColor("#4056DA"));
                article.setPadding(10, 5, 5, 5);
                article.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                holder.recentArticleLayout.addView(article);

                final int finalI = i;
                article.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, ArticlesAndBlogsDetailsActivity.class);
                        intent.putExtra(Constants.ARTICLE_ID, String.valueOf(datalist.get(position).getRecent_articles().get(finalI).getId()));
                        intent.putExtra(Constants.PARENTING_TYPE, ParentingFilterType.ARTICLES);
                        context.startActivity(intent);
                    }
                });
            }
        }

        holder.bloggerFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((DashboardActivity) context).followAPICall_List(String.valueOf(datalist.get(position).getId()), position);
            }
        });

//        if (!StringUtils.isNullOrEmpty(datalist.get(position).getFacebook_id())) {
//            holder.facebook.setVisibility(View.VISIBLE);
//        } else {
//            holder.facebook.setVisibility(View.GONE);
//        }
//
//        if (!StringUtils.isNullOrEmpty(datalist.get(position).getTwitter_id())) {
//            holder.twitter.setVisibility(View.VISIBLE);
//        } else {
//            holder.twitter.setVisibility(View.GONE);
//        }

//        holder.facebook.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                if (!StringUtils.isNullOrEmpty(datalist.get(position).getFacebook_id())) {
//                    Intent intent = new Intent(context, LoadWebViewActivity.class);
//                    intent.putExtra(Constants.WEB_VIEW_URL, datalist.get(position).getFacebook_id());
//                    Log.e("Link", datalist.get(position).getFacebook_id());
//                    context.startActivity(intent);
//                }
//            }
//        });
//        holder.twitter.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (!StringUtils.isNullOrEmpty(datalist.get(position).getTwitter_id())) {
//                    Intent intent = new Intent(context, LoadWebViewActivity.class);
//                    intent.putExtra(Constants.WEB_VIEW_URL, datalist.get(position).getTwitter_id());
//                    Log.e("Link", datalist.get(position).getTwitter_id());
//                    context.startActivity(intent);
//                }
//            }
//        });


        if (datalist.get(position).getMaxLineCount() >= 4) {
            holder.moreDesc.setVisibility(View.VISIBLE);
            holder.description.setMaxLines(4);
            holder.description.setEllipsize(null);
            holder.moreDesc.setText("More");
        } else {
            holder.description.setMaxLines(4);
            holder.description.setEllipsize(null);
            holder.moreDesc.setVisibility(View.GONE);
        }

        final ViewHolder finalHolder = holder;
        holder.moreDesc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (finalHolder.moreDesc.getText().toString().equalsIgnoreCase("More")) {
                    finalHolder.description.setMaxLines(100);
                    finalHolder.description.setEllipsize(null);
                    finalHolder.moreDesc.setText("Less");
                } else {
                    finalHolder.description.setMaxLines(4);
                    finalHolder.description.setEllipsize(null);
                    finalHolder.moreDesc.setText("More");
                }
            }
        });

        if (!StringUtils.isNullOrEmpty(datalist.get(position).getUser_following_status())) {
            if (datalist.get(position).getUser_following_status().equalsIgnoreCase("0")) {
                holder.bloggerFollow.setText("FOLLOW");
            } else {
                holder.bloggerFollow.setText("UNFOLLOW");
            }
        }

        return view;
    }

    public static class ViewHolder {

        TextView bloggerName;
        TextView authorType;
        TextView authorRank;
        ImageView bloggerImage;
        ImageView bloggerCover;
        TextView bloggerFollow;
        ImageView facebook;
        ImageView twitter;
        ImageView rss;
        TextView description;
        TextView moreDesc;
        LinearLayout recentArticleLayout;
        LinearLayout articleBlock;
        RelativeLayout aboutLayout;

    }
}