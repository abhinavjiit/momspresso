package com.mycity4kids.widget;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;

/**
 * Created by hemant on 3/10/16.
 */
public class KidsInfoNewCustomView extends RelativeLayout {

    private TextView kidsDOBTextView;
    private RadioGroup genderRadioGroup;
    private RadioButton maleRadioButton, femaleRadioButton;
    private TextView nameTextView;
    private TextView genderTextView;
    //    private ImageView genderImageView;
    private TextView editInfoTextView;

    public KidsInfoNewCustomView(Context context) {
        super(context);
        initializeViews(null, context);
    }

    public KidsInfoNewCustomView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeViews(attrs, context);
    }

    public KidsInfoNewCustomView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initializeViews(attrs, context);
    }

    private void initializeViews(AttributeSet attrs, Context context) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.kids_info_new_custom_view, this);

        kidsDOBTextView = (TextView) this.findViewById(R.id.kidsDOBTextView);
        nameTextView = (TextView) this.findViewById(R.id.nameTextView);
        genderTextView = (TextView) this.findViewById(R.id.genderTextView);
//        genderImageView = (ImageView) this.findViewById(R.id.genderImageView);
        editInfoTextView = (TextView) this.findViewById(R.id.editKidsInfoTextView);
//        Typeface font = Typeface.createFromAsset(context.getAssets(), "fonts/" + "oswald.ttf");
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    public void setKidName(String kidName) {
        if (StringUtils.isNullOrEmpty(kidName)) {
            nameTextView.setText("NA");
        } else {
            nameTextView.setText(kidName);
        }
    }

    public void setKids_bdy(String kids_bdy) {
        kidsDOBTextView.setText(kids_bdy);
    }

    public void setGenderRadioGroup(RadioGroup genderRadioGroup) {
        this.genderRadioGroup = genderRadioGroup;
    }

    public void setGenderAsMale(boolean isChecked) {
        genderTextView.setText("Male");
    }

    public void setGenderAsFemale(boolean isChecked) {
        genderTextView.setText("Female");
    }

    public TextView getKidsDOBTextView() {
        return kidsDOBTextView;
    }

    public TextView getEditKidInfoIV() {
        return editInfoTextView;
    }
}
