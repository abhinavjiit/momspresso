package com.mycity4kids.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.models.response.SearchArticleResult;
import com.squareup.picasso.Picasso;

/**
 * Created by hemant on 3/10/16.
 */
public class SearchArticlesView extends RelativeLayout {

    TextView articleTitleTextView;
    ImageView articleImageView;
    private TextView articleDescTextView;

    private SearchArticleResult searchArticleResult;
    String titleText;
    String imageUrl;
    Drawable articleImage;
    private String descText;

    public SearchArticlesView(Context context) {
        super(context);
        initializeViews(null, context);
    }

    public SearchArticlesView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeViews(attrs, context);
    }

    public SearchArticlesView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initializeViews(attrs, context);
    }

    private void initializeViews(AttributeSet attrs, Context context) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.search_article_view, this);

        articleTitleTextView = (TextView) this.findViewById(R.id.articleTitleTextView);
        articleDescTextView = (TextView) this.findViewById(R.id.articleDescTextView);
        articleImageView = (ImageView) this.findViewById(R.id.articleImageView);

        if (null != attrs) {
            TypedArray a = context.obtainStyledAttributes(attrs,
                    R.styleable.SearchArticlesComponent, 0, 0);
            titleText = a.getString(R.styleable.SearchArticlesComponent_searchArticleTitle);
            descText = a.getString(R.styleable.SearchArticlesComponent_searchArticleDesc);
            articleImage = a.getDrawable(R.styleable.SearchArticlesComponent_searchArticleImage);
            a.recycle();
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        articleTitleTextView.setText(titleText);
        articleDescTextView.setText(descText);
        if (StringUtils.isNullOrEmpty(imageUrl)) {
            articleImageView.setImageDrawable(articleImage);
        } else {
            try {
                Picasso.with(getContext()).load(imageUrl).placeholder(R.drawable.default_article).centerCrop().into(articleImageView);
            } catch (Exception e) {
                articleImageView.setImageResource(R.drawable.default_article);
            }

        }
    }

    public void setArticleTitleTextView(String articleTitle) {
        articleTitleTextView.setText(articleTitle);
    }

    public void setArticleImageView(String imageUrl) {
        try {
            Picasso.with(getContext()).load(imageUrl).placeholder(R.drawable.default_article).centerCrop().into(articleImageView);
        } catch (Exception e) {
            articleImageView.setImageResource(R.drawable.default_article);
        }
    }

    public void setArticleDescTextView(String articleDesc) {
        articleDescTextView.setText(articleDesc);
    }
}
