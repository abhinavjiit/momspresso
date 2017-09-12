package com.mycity4kids.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.models.response.SearchArticleResult;
import com.squareup.picasso.Picasso;

/**
 * Created by hemant on 3/10/16.
 */
public class KidsInfoCustomView extends RelativeLayout {

    private TextView kidsDOBTextView;
    private RadioGroup genderRadioGroup;
    private RadioButton maleRadioButton, femaleRadioButton;

    public KidsInfoCustomView(Context context) {
        super(context);
        initializeViews(null, context);
    }

    public KidsInfoCustomView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeViews(attrs, context);
    }

    public KidsInfoCustomView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initializeViews(attrs, context);
    }

    private void initializeViews(AttributeSet attrs, Context context) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.kids_info_custom_view, this);

        kidsDOBTextView = (TextView) this.findViewById(R.id.kidsDOBTextView);
        genderRadioGroup = (RadioGroup) this.findViewById(R.id.genderRadioGroup);
        maleRadioButton = (RadioButton) this.findViewById(R.id.maleRadioButton);
        femaleRadioButton = (RadioButton) this.findViewById(R.id.femaleRadioButton);
        Typeface font = Typeface.createFromAsset(context.getAssets(), "fonts/" + "oswald.ttf");
        maleRadioButton.setTypeface(font);
        femaleRadioButton.setTypeface(font);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    public void setKids_bdy(String kids_bdy) {
        kidsDOBTextView.setText(kids_bdy);
    }

    public void setGenderRadioGroup(RadioGroup genderRadioGroup) {
        this.genderRadioGroup = genderRadioGroup;
    }

    public void setMaleRadioButton(boolean isChecked) {
        maleRadioButton.setChecked(isChecked);
    }

    public void setFemaleRadioButton(boolean isChecked) {
        femaleRadioButton.setChecked(isChecked);
    }

    public TextView getKidsDOBTextView() {
        return kidsDOBTextView;
    }

}
