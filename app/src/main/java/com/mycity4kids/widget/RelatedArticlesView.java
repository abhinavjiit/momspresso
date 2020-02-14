package com.mycity4kids.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mycity4kids.R;

/**
 * Created by hemant on 3/10/16.
 */
public class RelatedArticlesView extends RelativeLayout {

    TextView articleTitleTextView;
    ImageView articleImageView;
    String titleText;
    Drawable articleImage;

    public RelatedArticlesView(Context context) {
        super(context);
        initializeViews(null, context);
    }

    public RelatedArticlesView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeViews(attrs, context);
    }

    public RelatedArticlesView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initializeViews(attrs, context);
    }

    private void initializeViews(AttributeSet attrs, Context context) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.related_articles_item_layout, this);

        if (null != attrs) {
            TypedArray a = context.obtainStyledAttributes(attrs,
                    R.styleable.RelatedArticlesComponent, 0, 0);
            titleText = a.getString(R.styleable.RelatedArticlesComponent_articleTitle);
            articleImage = a.getDrawable(R.styleable.RelatedArticlesComponent_articleImage);
            a.recycle();
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        articleTitleTextView = (TextView) this.findViewById(R.id.articleTitleTextView);
        articleImageView = (ImageView) this.findViewById(R.id.articleImageView);

        articleTitleTextView.setText(titleText);
        articleImageView.setImageDrawable(articleImage);
    }

    public ImageView getArticleImageView() {
        return articleImageView;
    }

    public void setArticleTitle(String title) {
        articleTitleTextView.setText(title);
    }
}
