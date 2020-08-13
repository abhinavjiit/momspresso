package com.mycity4kids.widget;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.FontRes;
import androidx.core.content.res.ResourcesCompat;
import com.mycity4kids.R;
import com.mycity4kids.utils.AppUtils;
import java.util.ArrayList;

public class MomspressoButtonWidget extends LinearLayout {

    public static final String TAG = MomspressoButtonWidget.class.getSimpleName();

    private Context context;

    // # Background Attributes
    private int defaultBackgroundColor = Color.BLACK;
    private int focusBackgroundColor = 0;
    private int disabledBackgroundColor = Color.parseColor("#f6f7f9");
    private int disabledTextColor = Color.parseColor("#bec2c9");
    private int disabledBorderColor = Color.parseColor("#dddfe2");

    // # Text Attributes
    private int defaultTextColor = Color.WHITE;
    private int defaultIconColor = Color.WHITE;
    private int textPosition = 1;
    private int defaultTextSize = AppUtils.spToPx(getContext(), 15);
    private int defaultTextGravity = 0x11; // Gravity.CENTER
    private String text = null;

    // # Icon Attributes
    private Drawable iconResource = null;
    private int fontIconSize = AppUtils.spToPx(getContext(), 15);
    private String fontIcon = null;
    private int iconPosition = 1;

    private int iconPaddingLeft = 10;
    private int iconPaddingRight = 10;
    private int iconPaddingTop = 0;
    private int iconPaddingBottom = 0;


    private int borderColor = Color.TRANSPARENT;
    private int borderWidth = 0;

    private int radius = 0;
    private int radiusTopLeft = 0;
    private int radiusTopRight = 0;
    private int radiusBottomLeft = 0;
    private int radiusBottomRight = 0;

    private boolean enabledFlag = true;

    private boolean textAllCaps = false;

    private Typeface textTypeFace = null;
    private Typeface iconTypeFace = null;
    private int textStyle;

    /**
     * Tags to identify icon position.
     */
    public static final int POSITION_LEFT = 1;
    public static final int POSITION_RIGHT = 2;
    public static final int POSITION_TOP = 3;
    public static final int POSITION_BOTTOM = 4;

    private String defaultIconFont = "fontawesome.ttf";
    private String defaultTextFont = "robotoregular.ttf";

    private ImageView iconView;
    private TextView fontIconView;
    private TextView textView;

    private boolean ghost = false; // Default is a solid button !
    private boolean useSystemFont = false; // Default is using robotoregular.ttf
    private boolean useRippleEffect = true;

    /**
     * Default constructor.
     *
     * @param context : Context
     */
    public MomspressoButtonWidget(Context context) {
        super(context);
        this.context = context;

        textTypeFace = AppUtils.findFont(this.context, defaultTextFont, null);
        iconTypeFace = AppUtils.findFont(this.context, defaultIconFont, null);
        initializeFancyButton();
    }


    /**
     * Default constructor called from Layouts.
     *
     * @param context : Context
     * @param attrs : Attributes Array
     */
    public MomspressoButtonWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;

        TypedArray attrsArray = context.obtainStyledAttributes(attrs, R.styleable.MomspressoButtonWidget, 0, 0);
        initAttributesArray(attrsArray);
        attrsArray.recycle();

        initializeFancyButton();

    }

    /**
     * Initialize Button dependencies - Initialize Button Container : The LinearLayout - Initialize Button TextView.
     * Initialize Button Icon - Initialize Button Font Icon.
     */
    private void initializeFancyButton() {

        initializeButtonContainer();

        textView = setupTextView();
        iconView = setupIconView();
        fontIconView = setupFontIconView();

        int iconIndex;
        int textIndex;
        View view1;
        View view2;

        this.removeAllViews();
        setupBackground();

        ArrayList<View> views = new ArrayList<>();

        if (iconPosition == POSITION_LEFT || iconPosition == POSITION_TOP) {

            if (iconView != null) {
                views.add(iconView);
            }

            if (fontIconView != null) {
                views.add(fontIconView);
            }
            if (textView != null) {
                views.add(textView);
            }

        } else {
            if (textView != null) {
                views.add(textView);
            }

            if (iconView != null) {
                views.add(iconView);
            }

            if (fontIconView != null) {
                views.add(fontIconView);
            }
        }

        for (View view : views) {
            this.addView(view);
        }
    }

    /**
     * Setup Text View.
     *
     * @return : TextView
     */
    private TextView setupTextView() {
        if (text == null) {
            text = "Fancy Button";
        }

        TextView textView = new TextView(context);
        textView.setText(text);

        textView.setGravity(defaultTextGravity);
        textView.setTextColor(enabledFlag ? defaultTextColor : disabledTextColor);
        textView.setTextSize(AppUtils.pxToSp(getContext(), defaultTextSize));
        textView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        if (!isInEditMode() && !useSystemFont) {
            textView.setTypeface(textTypeFace, textStyle); //we can pass null in first arg
        }
        return textView;
    }

    /**
     * Setup Font Icon View.
     *
     * @return : TextView
     */
    private TextView setupFontIconView() {

        if (fontIcon != null) {
            TextView fontIconView = new TextView(context);
            fontIconView.setTextColor(enabledFlag ? defaultIconColor : disabledTextColor);

            LayoutParams iconTextViewParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            iconTextViewParams.rightMargin = iconPaddingRight;
            iconTextViewParams.leftMargin = iconPaddingLeft;
            iconTextViewParams.topMargin = iconPaddingTop;
            iconTextViewParams.bottomMargin = iconPaddingBottom;

            if (textView != null) {

                if (iconPosition == POSITION_TOP || iconPosition == POSITION_BOTTOM) {
                    iconTextViewParams.gravity = Gravity.CENTER;
                    fontIconView.setGravity(Gravity.CENTER);
                } else {
                    fontIconView.setGravity(Gravity.CENTER_VERTICAL);
                    iconTextViewParams.gravity = Gravity.CENTER_VERTICAL;
                }
            } else {
                iconTextViewParams.gravity = Gravity.CENTER;
                fontIconView.setGravity(Gravity.CENTER_VERTICAL);
            }

            fontIconView.setLayoutParams(iconTextViewParams);
            if (!isInEditMode()) {
                fontIconView.setTextSize(AppUtils.pxToSp(getContext(), fontIconSize));
                fontIconView.setText(fontIcon);
                fontIconView.setTypeface(iconTypeFace);
            } else {
                fontIconView.setTextSize(AppUtils.pxToSp(getContext(), fontIconSize));
                fontIconView.setText("O");
            }
            return fontIconView;
        }
        return null;
    }

    /**
     * Text Icon resource view.
     *
     * @return : ImageView
     */
    private ImageView setupIconView() {
        if (iconResource != null) {
            ImageView iconView = new ImageView(context);
            iconView.setImageDrawable(iconResource);
            iconView.setPaddingRelative(iconPaddingLeft, iconPaddingTop, iconPaddingRight, iconPaddingBottom);

            LayoutParams iconViewParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            if (textView != null) {
                if (iconPosition == POSITION_TOP || iconPosition == POSITION_BOTTOM) {
                    iconViewParams.gravity = Gravity.CENTER;
                } else {
                    iconViewParams.gravity = Gravity.START;
                }

                iconViewParams.rightMargin = 10;
                iconViewParams.leftMargin = 10;
            } else {
                iconViewParams.gravity = Gravity.CENTER_VERTICAL;
            }
            iconView.setLayoutParams(iconViewParams);

            return iconView;
        }
        return null;
    }

    /**
     * Initialize Attributes arrays.
     *
     * @param attrsArray : Attributes array
     */
    private void initAttributesArray(TypedArray attrsArray) {

        defaultBackgroundColor = attrsArray
                .getColor(R.styleable.MomspressoButtonWidget_fb_defaultColor, defaultBackgroundColor);
        focusBackgroundColor = attrsArray
                .getColor(R.styleable.MomspressoButtonWidget_fb_focusColor, focusBackgroundColor);
        disabledBackgroundColor = attrsArray
                .getColor(R.styleable.MomspressoButtonWidget_fb_disabledColor, disabledBackgroundColor);

        // fix bug when set android:enabled="false" in xml file is not work, isEnabled() always return true when version
        // is 1.9.0 or before this happens because this FancyButton extends of LinearLayout, which enabled attribute
        // is not declared
        enabledFlag = attrsArray.getBoolean(R.styleable.MomspressoButtonWidget_android_enabled, isEnabled());
        // super.setEnabled(mEnabled);
        super.setSelected(enabledFlag);

        disabledTextColor = attrsArray
                .getColor(R.styleable.MomspressoButtonWidget_fb_disabledTextColor, disabledTextColor);
        disabledBorderColor = attrsArray
                .getColor(R.styleable.MomspressoButtonWidget_fb_disabledBorderColor, disabledBorderColor);
        defaultTextColor = attrsArray.getColor(R.styleable.MomspressoButtonWidget_fb_textColor, defaultTextColor);
        // if default color is set then the icon's color is the same (the default for icon's color)
        defaultIconColor = attrsArray.getColor(R.styleable.MomspressoButtonWidget_fb_iconColor, defaultTextColor);

        defaultTextSize = (int) attrsArray
                .getDimension(R.styleable.MomspressoButtonWidget_fb_textSize, defaultTextSize);
        defaultTextSize = (int) attrsArray
                .getDimension(R.styleable.MomspressoButtonWidget_android_textSize, defaultTextSize);

        defaultTextGravity = attrsArray.getInt(R.styleable.MomspressoButtonWidget_fb_textGravity, defaultTextGravity);

        borderColor = attrsArray.getColor(R.styleable.MomspressoButtonWidget_fb_borderColor, borderColor);
        borderWidth = (int) attrsArray.getDimension(R.styleable.MomspressoButtonWidget_fb_borderWidth, borderWidth);

        radius = (int) attrsArray.getDimension(R.styleable.MomspressoButtonWidget_fb_radius, radius);

        radiusTopLeft = (int) attrsArray.getDimension(R.styleable.MomspressoButtonWidget_fb_radiusTopLeft, radius);
        radiusTopRight = (int) attrsArray.getDimension(R.styleable.MomspressoButtonWidget_fb_radiusTopRight, radius);
        radiusBottomLeft = (int) attrsArray
                .getDimension(R.styleable.MomspressoButtonWidget_fb_radiusBottomLeft, radius);
        radiusBottomRight = (int) attrsArray
                .getDimension(R.styleable.MomspressoButtonWidget_fb_radiusBottomRight, radius);

        fontIconSize = (int) attrsArray
                .getDimension(R.styleable.MomspressoButtonWidget_fb_fontIconSize, fontIconSize);

        iconPaddingLeft = (int) attrsArray
                .getDimension(R.styleable.MomspressoButtonWidget_fb_iconPaddingLeft, iconPaddingLeft);
        iconPaddingRight = (int) attrsArray
                .getDimension(R.styleable.MomspressoButtonWidget_fb_iconPaddingRight, iconPaddingRight);
        iconPaddingTop = (int) attrsArray
                .getDimension(R.styleable.MomspressoButtonWidget_fb_iconPaddingTop, iconPaddingTop);
        iconPaddingBottom = (int) attrsArray
                .getDimension(R.styleable.MomspressoButtonWidget_fb_iconPaddingBottom, iconPaddingBottom);

        textAllCaps = attrsArray.getBoolean(R.styleable.MomspressoButtonWidget_fb_textAllCaps, false);
        textAllCaps = attrsArray.getBoolean(R.styleable.MomspressoButtonWidget_android_textAllCaps, false);

        ghost = attrsArray.getBoolean(R.styleable.MomspressoButtonWidget_fb_ghost, ghost);
        useSystemFont = attrsArray.getBoolean(R.styleable.MomspressoButtonWidget_fb_useSystemFont, useSystemFont);

        String text = attrsArray.getString(R.styleable.MomspressoButtonWidget_fb_text);

        if (text == null) { //no fb_text attribute
            text = attrsArray.getString(R.styleable.MomspressoButtonWidget_android_text);
        }

        iconPosition = attrsArray.getInt(R.styleable.MomspressoButtonWidget_fb_iconPosition, iconPosition);

        textStyle = attrsArray.getInt(R.styleable.MomspressoButtonWidget_android_textStyle, Typeface.NORMAL);

        String fontIcon = attrsArray.getString(R.styleable.MomspressoButtonWidget_fb_fontIconResource);

        String iconFontFamily = attrsArray.getString(R.styleable.MomspressoButtonWidget_fb_iconFont);
        String textFontFamily = attrsArray.getString(R.styleable.MomspressoButtonWidget_fb_textFont);

        try {
            iconResource = attrsArray.getDrawable(R.styleable.MomspressoButtonWidget_fb_iconResource);
        } catch (Exception e) {
            iconResource = null;
        }

        if (fontIcon != null) {
            this.fontIcon = fontIcon;
        }

        if (text != null) {
            this.text = textAllCaps ? text.toUpperCase() : text;
        }

        if (!isInEditMode()) {
            iconTypeFace = iconFontFamily != null
                    ? AppUtils.findFont(context, iconFontFamily, defaultIconFont)
                    : AppUtils.findFont(context, defaultIconFont, null);

            Typeface fontResource = getTypeface(attrsArray);
            if (fontResource != null) {
                textTypeFace = fontResource;
            } else {
                textTypeFace = textFontFamily != null
                        ? AppUtils.findFont(context, textFontFamily, defaultTextFont)
                        : AppUtils.findFont(context, defaultTextFont, null);
            }
        }
    }

    private Typeface getTypeface(TypedArray ta) {
        if (ta.hasValue(R.styleable.MomspressoButtonWidget_android_fontFamily)) {
            int fontId = ta.getResourceId(R.styleable.MomspressoButtonWidget_android_fontFamily, 0);
            if (fontId != 0) {
                try {
                    return ResourcesCompat.getFont(getContext(), fontId);
                } catch (Exception exception) {
                    Log.e("getTypeface", exception.getMessage());
                }
            }
        }
        if (ta.hasValue(R.styleable.MomspressoButtonWidget_fb_textFontRes)) {
            int fontId = ta.getResourceId(R.styleable.MomspressoButtonWidget_fb_textFontRes, 0);
            if (fontId != 0) {
                try {
                    return ResourcesCompat.getFont(getContext(), fontId);
                } catch (Exception exception) {
                    Log.e("getTypeface", exception.getMessage());
                }
            }
        }
        return null;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private Drawable getRippleDrawable(Drawable defaultDrawable, Drawable focusDrawable, Drawable disabledDrawable) {
        if (!enabledFlag) {
            return disabledDrawable;
        } else {
            return new RippleDrawable(ColorStateList.valueOf(focusBackgroundColor), defaultDrawable, focusDrawable);
        }

    }

    /**
     * This method applies radius to the drawable corners Specify radius for each corner if radius attribute is not
     * defined.
     *
     * @param drawable Drawable
     */
    private void applyRadius(GradientDrawable drawable) {
        if (radius > 0) {
            drawable.setCornerRadius(radius);
        } else {
            drawable.setCornerRadii(new float[] {radiusTopLeft, radiusTopLeft, radiusTopRight, radiusTopRight,
                    radiusBottomRight, radiusBottomRight, radiusBottomLeft, radiusBottomLeft});
        }
    }

    @SuppressLint("NewApi")
    private void setupBackground() {
        // Default Drawable
        GradientDrawable defaultDrawable = new GradientDrawable();
        applyRadius(defaultDrawable);

        if (ghost) {
            defaultDrawable.setColor(getResources().getColor(android.R.color.transparent)); // Hollow Background
        } else {
            defaultDrawable.setColor(defaultBackgroundColor);
        }

        //Focus Drawable
        GradientDrawable focusDrawable = new GradientDrawable();
        applyRadius(focusDrawable);

        focusDrawable.setColor(focusBackgroundColor);

        // Disabled Drawable
        GradientDrawable disabledDrawable = new GradientDrawable();
        applyRadius(disabledDrawable);

        disabledDrawable.setColor(disabledBackgroundColor);
        disabledDrawable.setStroke(borderWidth, disabledBorderColor);

        // Handle Border
        if (borderColor != 0) {
            defaultDrawable.setStroke(borderWidth, borderColor);
        }

        // Handle disabled border color
        if (!enabledFlag) {
            defaultDrawable.setStroke(borderWidth, disabledBorderColor);
            if (ghost) {
                disabledDrawable.setColor(getResources().getColor(android.R.color.transparent));
            }
        }

        if (useRippleEffect && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            this.setBackground(getRippleDrawable(defaultDrawable, focusDrawable, disabledDrawable));

        } else {

            StateListDrawable states = new StateListDrawable();

            // Focus/Pressed Drawable
            GradientDrawable drawable2 = new GradientDrawable();
            applyRadius(drawable2);

            if (ghost) {
                drawable2.setColor(getResources().getColor(android.R.color.transparent)); // No focus color
            } else {
                drawable2.setColor(focusBackgroundColor);
            }

            // Handle Button Border
            if (borderColor != 0) {
                if (ghost) {
                    drawable2.setStroke(borderWidth, focusBackgroundColor); // Border is the main part of button now
                } else {
                    drawable2.setStroke(borderWidth, borderColor);
                }
            }

            if (!enabledFlag) {
                if (ghost) {
                    drawable2.setStroke(borderWidth, disabledBorderColor);
                } else {
                    drawable2.setStroke(borderWidth, disabledBorderColor);
                }
            }

            if (focusBackgroundColor != 0) {
                states.addState(new int[] {android.R.attr.state_pressed}, drawable2);
                states.addState(new int[] {android.R.attr.state_focused}, drawable2);
                states.addState(new int[] {-android.R.attr.state_enabled}, disabledDrawable);
            }

            states.addState(new int[] {}, defaultDrawable);

            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                this.setBackgroundDrawable(states);
            } else {
                this.setBackground(states);
            }
        }
    }


    /**
     * Initialize button container.
     */
    private void initializeButtonContainer() {

        if (iconPosition == POSITION_TOP || iconPosition == POSITION_BOTTOM) {
            this.setOrientation(LinearLayout.VERTICAL);
        } else {
            this.setOrientation(LinearLayout.HORIZONTAL);
        }

        if (this.getLayoutParams() == null) {
            LayoutParams containerParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            this.setLayoutParams(containerParams);
        }

        this.setGravity(Gravity.CENTER);
        // disable click listeners for fix bug in this issue as:
        // https://github.com/medyo/Fancybuttons/issues/100 
        //this.setClickable(true);
        //this.setFocusable(true);

        if (iconResource == null && fontIcon == null && getPaddingStart() == 0 && getPaddingEnd() == 0
                && getPaddingTop() == 0 && getPaddingBottom() == 0) {
            //fix for all version of androids and screens 
            this.setPadding(20, 0, 20, 0);
        }
    }

    /**
     * Set Text of the button.
     *
     * @param text : Text
     */
    public void setText(String text) {
        text = textAllCaps ? text.toUpperCase() : text;
        this.text = text;
        if (textView == null) {
            initializeFancyButton();
        } else {
            textView.setText(text);
        }
    }

    /**
     * Set the capitalization of text.
     *
     * @param textAllCaps : is text to be capitalized
     */
    public void setTextAllCaps(boolean textAllCaps) {
        this.textAllCaps = textAllCaps;
        setText(text);
    }

    /**
     * Set the color of text.
     *
     * @param color : Color use Color.parse('#code')
     */
    public void setTextColor(int color) {
        this.defaultTextColor = color;
        if (textView == null) {
            initializeFancyButton();
        } else {
            textView.setTextColor(color);
        }

    }

    /**
     * Setting the icon's color independent of the text color.
     *
     * @param color : Color
     */
    public void setIconColor(int color) {
        if (fontIconView != null) {
            fontIconView.setTextColor(color);
        }
    }

    /**
     * Set Background color of the button.
     *
     * @param color : use Color.parse('#code')
     */
    public void setBackgroundColor(int color) {
        this.defaultBackgroundColor = color;
        if (iconView != null || fontIconView != null || textView != null) {
            this.setupBackground();
        }
    }

    /**
     * Set Focus color of the button.
     *
     * @param color : use Color.parse('#code')
     */
    public void setFocusBackgroundColor(int color) {
        this.focusBackgroundColor = color;
        if (iconView != null || fontIconView != null || textView != null) {
            this.setupBackground();
        }

    }

    /**
     * Set Disabled state color of the button.
     *
     * @param color : use Color.parse('#code')
     */
    public void setDisableBackgroundColor(int color) {
        this.disabledBackgroundColor = color;
        if (iconView != null || fontIconView != null || textView != null) {
            this.setupBackground();
        }

    }

    /**
     * Set Disabled state color of the button text.
     *
     * @param color : use Color.parse('#code')
     */
    public void setDisableTextColor(int color) {
        this.disabledTextColor = color;
        if (textView == null) {
            initializeFancyButton();
        } else if (!enabledFlag) {
            textView.setTextColor(color);
        }

    }

    /**
     * Set Disabled state color of the button border.
     *
     * @param color : use Color.parse('#code')
     */
    public void setDisableBorderColor(int color) {
        this.disabledBorderColor = color;
        if (iconView != null || fontIconView != null || textView != null) {
            this.setupBackground();
        }

    }

    /**
     * Set the size of Text in sp.
     *
     * @param textSize : Text Size
     */
    public void setTextSize(int textSize) {
        this.defaultTextSize = AppUtils.spToPx(getContext(), textSize);
        if (textView != null) {
            textView.setTextSize(textSize);
        }
    }

    /**
     * Set the gravity of Text.
     *
     * @param gravity : Text Gravity
     */

    public void setTextGravity(int gravity) {
        this.defaultTextGravity = gravity;
        if (textView != null) {
            this.setGravity(gravity);
        }
    }

    /**
     * Set Padding for mIconView and mFontIconSize.
     *
     * @param paddingLeft : Padding Left
     * @param paddingTop : Padding Top
     * @param paddingRight : Padding Right
     * @param paddingBottom : Padding Bottom
     */
    public void setIconPadding(int paddingLeft, int paddingTop, int paddingRight, int paddingBottom) {
        this.iconPaddingLeft = paddingLeft;
        this.iconPaddingTop = paddingTop;
        this.iconPaddingRight = paddingRight;
        this.iconPaddingBottom = paddingBottom;
        if (iconView != null) {
            iconView.setPaddingRelative(this.iconPaddingLeft, this.iconPaddingTop, this.iconPaddingRight,
                    this.iconPaddingBottom);
        }
        if (fontIconView != null) {
            fontIconView.setPaddingRelative(this.iconPaddingLeft, this.iconPaddingTop, this.iconPaddingRight,
                    this.iconPaddingBottom);
        }
    }

    /**
     * Set an icon from resources to the button.
     *
     * @param drawable : Int resource
     */
    public void setIconResource(int drawable) {
        this.iconResource = context.getResources().getDrawable(drawable);
        if (iconView == null || fontIconView != null) {
            fontIconView = null;
            initializeFancyButton();
        } else {
            iconView.setImageDrawable(iconResource);
        }
    }

    /**
     * Set a drawable to the button.
     *
     * @param drawable : Drawable resource
     */
    public void setIconResource(Drawable drawable) {
        this.iconResource = drawable;
        if (iconView == null || fontIconView != null) {
            fontIconView = null;
            initializeFancyButton();
        } else {
            iconView.setImageDrawable(iconResource);
        }
    }

    /**
     * Set a font icon to the button (eg FFontAwesome or Entypo...).
     *
     * @param icon : Icon value eg : \uf082
     */
    public void setIconResource(String icon) {
        this.fontIcon = icon;
        if (fontIconView == null) {
            iconView = null;
            initializeFancyButton();
        } else {
            fontIconView.setText(icon);
        }
    }

    /**
     * Set Icon size of the button (for only font icons) in sp.
     *
     * @param iconSize : Icon Size
     */
    public void setFontIconSize(int iconSize) {
        this.fontIconSize = AppUtils.spToPx(getContext(), iconSize);
        if (fontIconView != null) {
            fontIconView.setTextSize(iconSize);
        }
    }

    /**
     * Set Icon Position Use the global variables (FancyButton.POSITION_LEFT, FancyButton.POSITION_RIGHT,
     * FancyButton.POSITION_TOP, FancyButton.POSITION_BOTTOM).
     *
     * @param position : Position
     */
    public void setIconPosition(int position) {
        if (position > 0 && position < 5) {
            iconPosition = position;
        } else {
            iconPosition = POSITION_LEFT;
        }

        initializeFancyButton();
    }

    /**
     * Set color of the button border.
     *
     * @param color : Color use Color.parse('#code')
     */
    public void setBorderColor(int color) {
        this.borderColor = color;
        if (iconView != null || fontIconView != null || textView != null) {
            this.setupBackground();
        }
    }

    /**
     * Set Width of the button.
     *
     * @param width : Width
     */
    public void setBorderWidth(int width) {
        this.borderWidth = width;
        if (iconView != null || fontIconView != null || textView != null) {
            this.setupBackground();
        }
    }

    /**
     * Set Border Radius of the button.
     *
     * @param radius : Radius
     */
    public void setRadius(int radius) {
        this.radius = radius;
        if (iconView != null || fontIconView != null || textView != null) {
            this.setupBackground();
        }
    }

    /**
     * Set Border Radius for each button corner Top Left, Top Right, Bottom Left, Bottom Right.
     *
     * @param radius : Array of int
     */
    public void setRadius(int[] radius) {
        this.radiusTopLeft = radius[0];
        this.radiusTopRight = radius[1];
        this.radiusBottomLeft = radius[2];
        this.radiusBottomRight = radius[3];

        if (iconView != null || fontIconView != null || textView != null) {
            this.setupBackground();
        }
    }

    /**
     * Set custom font for button Text.
     *
     * @param fontName : Font Name Place your text fonts in assets
     */
    public void setCustomTextFont(String fontName) {
        textTypeFace = AppUtils.findFont(context, fontName, defaultTextFont);

        if (textView == null) {
            initializeFancyButton();
        } else {
            textView.setTypeface(textTypeFace, textStyle);
        }
    }

    /**
     * Set custom font for button Text.
     *
     * @param fontId : Font id Place your text fonts in font resources. Eg. res/font/roboto.ttf or res/font/roboto.xml
     */
    public void setCustomTextFont(@FontRes int fontId) {
        textTypeFace = ResourcesCompat.getFont(getContext(), fontId);

        if (textView == null) {
            initializeFancyButton();
        } else {
            textView.setTypeface(textTypeFace, textStyle);
        }
    }

    /**
     * Set Custom font for button icon.
     *
     * @param fontName : Font Name Place your icon fonts in assets
     */
    public void setCustomIconFont(String fontName) {

        iconTypeFace = AppUtils.findFont(context, fontName, defaultIconFont);

        if (fontIconView == null) {
            initializeFancyButton();
        } else {
            fontIconView.setTypeface(iconTypeFace);
        }

    }

    /**
     * Override setEnabled and rebuild the fancybutton view To redraw the button according to the state : enabled or
     * disabled.
     */
    //    @Override
    //    public void setEnabled(boolean value) {
    //        super.setEnabled(value);
    //        this.mEnabled = value;
    //        initializeFancyButton();
    //
    //    }
    @Override
    public void setSelected(boolean value) {
        super.setSelected(value);
        this.enabledFlag = value;
        initializeFancyButton();
    }

    /**
     * Setting the button to have hollow or solid shape.
     */
    public void setGhost(boolean ghost) {
        this.ghost = ghost;

        if (iconView != null || fontIconView != null || textView != null) {
            this.setupBackground();
        }

    }

    /**
     * If enabled, the button title will ignore its custom font and use the default system font.
     *
     * @param status : true || false
     */
    public void setUsingSystemFont(boolean status) {
        this.useSystemFont = status;
    }

    /**
     * Return Text of the button.
     *
     * @return Text
     */
    public CharSequence getText() {
        if (textView != null) {
            return textView.getText();
        } else {
            return "";
        }
    }

    /**
     * Return TextView Object of the FancyButton.
     *
     * @return TextView Object
     */
    public TextView getTextViewObject() {
        return textView;
    }

    /**
     * Return Icon Font of the FancyButton.
     *
     * @return TextView Object
     */
    public TextView getIconFontObject() {
        return fontIconView;
    }

    /**
     * Return Icon of the FancyButton.
     *
     * @return ImageView Object
     */
    public ImageView getIconImageObject() {
        return iconView;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private class CustomOutline extends ViewOutlineProvider {

        int width;
        int height;

        CustomOutline(int width, int height) {
            this.width = width;
            this.height = height;
        }

        @Override
        public void getOutline(View view, Outline outline) {

            if (radius == 0) {
                outline.setRect(0, 10, width, height);
            } else {
                outline.setRoundRect(0, 10, width, height, radius);
            }

        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setOutlineProvider(new CustomOutline(w, h));
        }
    }

}