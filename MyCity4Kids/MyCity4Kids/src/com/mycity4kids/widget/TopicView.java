package com.mycity4kids.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mycity4kids.R;

import org.apmem.tools.layouts.FlowLayout;

/**
 * Created by hemant on 10/6/16.
 */
public class TopicView extends RelativeLayout {
    TextView categoryTextView;
    TextView subcategoryTextView;
    ImageView removeTopicImageView;
    View containerView;

    public TopicView(Context context) {
        super(context);
        init();
    }

    public TopicView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TopicView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init(Context context) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        containerView = inflater.inflate(R.layout.topic_view, this);

        categoryTextView = (TextView) this.findViewById(R.id.categoryTextView);
        subcategoryTextView = (TextView) this.findViewById(R.id.subcategoryTextView);
        removeTopicImageView = (ImageView) this.findViewById(R.id.removeTopicImageView);
//        inflate(getContext(), R.layout.topic_view, this);
//        this.categoryTextView = (TextView) findViewById(R.id.categoryTextView);
//        this.subcategoryTextView = (TextView) findViewById(R.id.subcategoryTextView);
    }

    /**
     * Initializer method to load from xml
     */
    private void init() {
        containerView = inflate(getContext(), R.layout.topic_view, this);
        categoryTextView = (TextView) findViewById(R.id.categoryTextView);
        subcategoryTextView = (TextView) findViewById(R.id.subcategoryTextView);
        removeTopicImageView = (ImageView) findViewById(R.id.removeTopicImageView);

//        removeTopicImageView.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ((FlowLayout) containerView.getParent()).removeView(containerView);
//            }
//        });
    }

//    @Override
//    protected void onFinishInflate() {
//        super.onFinishInflate();
//
//        // Sets the images for the previous and next buttons. Uses
//        // built-in images so you don't need to add images, but in
//        // a real application your images should be in the
//        // application package so they are always available.
//
//
//    }

    /**
     * Sets the category.
     *
     * @param categoryName name of the category to be set.
     */
    public void setCategory(String categoryName) {
        this.categoryTextView.setText(categoryName);
    }

    /**
     * Sets the subcategory.
     *
     * @param subcategoryName name of the subcategory to be set..
     */
    public void setSubcategory(String subcategoryName) {
        this.subcategoryTextView.setText(subcategoryName);
    }

    public void removeTopic(){
        ((FlowLayout) containerView.getParent()).removeView(containerView);
    }


}
