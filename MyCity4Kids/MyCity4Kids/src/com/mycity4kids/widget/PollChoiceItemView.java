package com.mycity4kids.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.mycity4kids.R;

import java.util.TimerTask;

/**
 * Created by hemant on 24/4/18.
 */

public class PollChoiceItemView  {

//    private EditText choiceEditText;
//    private ImageView choiceImageView;
//    private View view;
//
//    private String choiceText;
//
//    public PollChoiceItemView(Context context) {
//        super(context);
//        initializeViews(null, context);
//    }
//
//    public PollChoiceItemView(Context context, AttributeSet attrs) {
//        super(context, attrs);
//        initializeViews(attrs, context);
//    }
//
//    public PollChoiceItemView(Context context, AttributeSet attrs, int defStyleAttr) {
//        super(context, attrs, defStyleAttr);
//        initializeViews(attrs, context);
//    }
//
//    private void initializeViews(AttributeSet attrs, Context context) {
//        LayoutInflater inflater = (LayoutInflater) context
//                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        view = inflater.inflate(R.layout.poll_choice_item, this);
//        choiceEditText = (EditText) this.findViewById(R.id.textPollEditText);
//        choiceImageView = (ImageView) this.findViewById(R.id.imagePollImageView);
//
//        choiceImageView.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ViewGroup parentContainer = (ViewGroup) view.getParent();
//                parentContainer.removeView(view);
//                if (parentContainer.getChildCount() < 3) {
//                    for (int i = 0; i < parentContainer.getChildCount(); i++) {
//                        ((PollChoiceItemView) parentContainer.getChildAt(i)).setDeleteImageVisibility(false);
//                        ((PollChoiceItemView) parentContainer.getChildAt(i)).setChoiceTextHint("Choice " + (i + 1));
//                    }
//                } else {
//                    for (int i = 0; i < parentContainer.getChildCount(); i++) {
//                        ((PollChoiceItemView) parentContainer.getChildAt(i)).setChoiceTextHint("Choice " + (i + 1));
//                    }
//                }
//
//            }
//        });
//        if (null != attrs) {
//            TypedArray a = context.obtainStyledAttributes(attrs,
//                    R.styleable.PollChoiceComponents, 0, 0);
//            choiceText = a.getString(R.styleable.PollChoiceComponents_pollChoiceText);
//            choiceEditText.setHint(choiceText);
//            a.recycle();
//        }
//    }
//
//    public void setChoiceTextHint(String title) {
//        choiceEditText.setHint(title);
//    }
//
//    public void setDeleteImageVisibility(boolean flag) {
//        if (flag) {
//            choiceImageView.setVisibility(VISIBLE);
//        } else {
//            choiceImageView.setVisibility(INVISIBLE);
//        }
//    }


}
