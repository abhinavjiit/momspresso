package com.mycity4kids.widget;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.gtmutils.GTMEventType;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.response.ArticleListingResult;
import com.mycity4kids.models.response.LanguageConfigModel;
import com.mycity4kids.models.response.VlogsListingAndDetailResult;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.ui.activity.AllVideoSectionActivity;
import com.mycity4kids.ui.activity.ArticleListingActivity;
import com.mycity4kids.ui.activity.ArticlesAndBlogsDetailsActivity;
import com.mycity4kids.ui.activity.CityBestArticleListingActivity;
import com.mycity4kids.ui.activity.DashboardActivity;
import com.mycity4kids.ui.activity.FilteredTopicsArticleListingActivity;
import com.mycity4kids.ui.activity.TopicsSplashActivity;
import com.mycity4kids.ui.activity.VlogsDetailActivity;
import com.mycity4kids.ui.activity.VlogsListingActivity;
import com.mycity4kids.ui.fragment.ChooseVideoUploadOptionDialogFragment;
import com.mycity4kids.utils.AppUtils;
import com.squareup.picasso.Picasso;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by hemant on 3/10/16.
 */
public class HorizontalScrollCustomView extends LinearLayout {

    private TextView sectionNameTextView;
    private TextView sectionNameTextView2;
    private TextView sectionNameTextView3;
    private TextView emptyListTextView;
    private TextView addTopicsTextView;
    private ProgressBar progressBar;
    private HorizontalScrollView horizontalScrollView;
    private LinearLayout hsvLinearLayout;
    private LayoutInflater mInflator;
    private LinearLayout languageContainer;

    private ArrayList<ArticleListingResult> mDatalist;
    private ArrayList<VlogsListingAndDetailResult> vlogslist;

    private LanguageConfigModel hindiLangModel;

    public HorizontalScrollCustomView(Context context) {
        super(context);
        initializeViews(null, context);
    }

    public HorizontalScrollCustomView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeViews(attrs, context);
    }

    public HorizontalScrollCustomView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initializeViews(attrs, context);
    }

    private void initializeViews(AttributeSet attrs, Context context) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.include_home_horizontal_item, this);

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        sectionNameTextView = (TextView) this.findViewById(R.id.labelTextView);
        sectionNameTextView2 = (TextView) this.findViewById(R.id.labelTextView2);
        sectionNameTextView3 = (TextView) this.findViewById(R.id.labelTextView3);

        languageContainer = (LinearLayout) this.findViewById(R.id.headerLayout);

        emptyListTextView = (TextView) this.findViewById(R.id.emptyListTextView);
        addTopicsTextView = (TextView) this.findViewById(R.id.addTopicsTextView);
        progressBar = (ProgressBar) this.findViewById(R.id.progressBar);
        horizontalScrollView = (HorizontalScrollView) this.findViewById(R.id.horizontalScrollView);
        hsvLinearLayout = (LinearLayout) this.findViewById(R.id.hsvLinearLayout);

    }

    public ArrayList<ArticleListingResult> getmDatalist() {
        return mDatalist;
    }

    public void setmDatalist(final ArrayList<ArticleListingResult> mDatalist, final String listingType, final String currentScreen) {
        if (Constants.KEY_FOR_YOU.equals(listingType)) {
            addTopicsTextView.setVisibility(VISIBLE);
            addTopicsTextView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), TopicsSplashActivity.class);
                    intent.putExtra(AppConstants.IS_ADD_MORE_TOPIC, true);
                    getContext().startActivity(intent);
                }
            });
        }
        if (Constants.KEY_HINDI.equals(listingType)) {
            hindiLangModel = AppUtils.getLangModelForLanguage(getContext(), AppConstants.LANG_KEY_HINDI);
        }

        this.mDatalist = mDatalist;
        mInflator = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        progressBar.setVisibility(GONE);
        hsvLinearLayout.removeAllViews();
        if (!mDatalist.isEmpty()) {
            Collections.shuffle(mDatalist);
        }
        for (int i = 0; i < mDatalist.size(); i++) {
            final View view = mInflator.inflate(R.layout.card_item_article_dashboard, null);
            view.setTag(i);
            ImageView articleImage = (ImageView) view.findViewById(R.id.imvAuthorThumb);
            ImageView videoIndicatorImageView = (ImageView) view.findViewById(R.id.videoIndicatorImageView);
            TextView title = (TextView) view.findViewById(R.id.txvArticleTitle);
            if (!StringUtils.isNullOrEmpty(mDatalist.get(i).getVideoUrl())
                    && (mDatalist.get(i).getImageUrl().getMobileWebThumbnail() == null || mDatalist.get(i).getImageUrl().getMobileWebThumbnail().endsWith("default.jpg"))) {
                Picasso.with(getContext()).load(AppUtils.getYoutubeThumbnailURLMomspresso(mDatalist.get(i).getVideoUrl())).placeholder(R.drawable.default_article).into(articleImage);
            } else {
                Picasso.with(getContext()).load(mDatalist.get(i).getImageUrl().getMobileWebThumbnail()).error(R.drawable.default_article).placeholder(R.drawable.default_article).into(articleImage);
            }

            if (!StringUtils.isNullOrEmpty(mDatalist.get(i).getVideoUrl())) {
                videoIndicatorImageView.setVisibility(VISIBLE);
            } else {
                videoIndicatorImageView.setVisibility(INVISIBLE);
            }

            title.setText(mDatalist.get(i).getTitle());
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Constants.KEY_FOR_YOU.equals(listingType)) {
                        Utils.pushEvent(getContext(), GTMEventType.FOR_YOU_ARTICLE_CLICK_EVENT, SharedPrefUtils.getUserDetailModel(getContext()).getDynamoId(), "Home Screen");
                    }
//                    else if (Constants.KEY_HINDI.equals(listingType)) {
//                        Utils.pushEvent(getContext(), GTMEventType.HINDI_ARTICLE_CLICK_EVENT, SharedPrefUtils.getUserDetailModel(getContext()).getDynamoId(), "Home Screen");
//                    }
                    Intent intent = new Intent(getContext(), ArticlesAndBlogsDetailsActivity.class);
                    ArticleListingResult parentingListData = (ArticleListingResult) (mDatalist.get((int) view.getTag()));
                    intent.putExtra(Constants.ARTICLE_ID, parentingListData.getId());
                    intent.putExtra(Constants.AUTHOR_ID, parentingListData.getUserId());
                    intent.putExtra(Constants.BLOG_SLUG, parentingListData.getBlogPageSlug());
                    intent.putExtra(Constants.TITLE_SLUG, parentingListData.getTitleSlug());
                    if (Constants.KEY_HINDI.equals(listingType) && null != hindiLangModel && !StringUtils.isNullOrEmpty(hindiLangModel.getId())) {
                        intent.putExtra(Constants.ARTICLE_OPENED_FROM, hindiLangModel.getName() + "~" + hindiLangModel.getId());
                    } else {
                        intent.putExtra(Constants.ARTICLE_OPENED_FROM, listingType);
                    }
                    intent.putExtra(Constants.FROM_SCREEN, currentScreen);
                    intent.putExtra(Constants.ARTICLE_INDEX, "" + (int) view.getTag());
                    getContext().startActivity(intent);
                    Log.e("Tag", "" + view.getTag());
                }
            });
            hsvLinearLayout.addView(view);
        }

        View customViewMore = mInflator.inflate(R.layout.custom_view_more_dashboard, null);
        DisplayMetrics metrics = new DisplayMetrics();
        if (getContext() != null) {
            ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(metrics);
            int widthPixels = metrics.widthPixels;
            float width = (float) (widthPixels * 0.45);
            customViewMore.setMinimumWidth((int) width);
        }
        hsvLinearLayout.addView(customViewMore);
        customViewMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Constants.KEY_FOR_YOU.equals(listingType)) {
                    Intent intent1 = new Intent(getContext(), ArticleListingActivity.class);
                    intent1.putExtra(Constants.SORT_TYPE, Constants.KEY_FOR_YOU);
                    intent1.putExtra(Constants.FROM_SCREEN, currentScreen);
                    getContext().startActivity(intent1);
                } else if (Constants.KEY_TRENDING.equals(listingType)) {
                    Intent intent1 = new Intent(getContext(), ArticleListingActivity.class);
                    intent1.putExtra(Constants.SORT_TYPE, Constants.KEY_TRENDING);
                    intent1.putExtra(Constants.FROM_SCREEN, currentScreen);
                    getContext().startActivity(intent1);
                } else if (listingType != null && listingType.contains(Constants.KEY_IN_YOUR_CITY)) {
                    Intent intent1 = new Intent(getContext(), CityBestArticleListingActivity.class);
                    intent1.putExtra(Constants.FROM_SCREEN, currentScreen);
                    getContext().startActivity(intent1);
                } else if (Constants.KEY_EDITOR_PICKS.equals(listingType)) {
                    Intent intent1 = new Intent(getContext(), ArticleListingActivity.class);
                    intent1.putExtra(Constants.SORT_TYPE, Constants.KEY_EDITOR_PICKS);
                    intent1.putExtra(Constants.FROM_SCREEN, currentScreen);
                    getContext().startActivity(intent1);
                } else if (Constants.KEY_MOMSPRESSO.equals(listingType)) {
                    Intent intent1 = new Intent(getContext(), FilteredTopicsArticleListingActivity.class);
                    intent1.putExtra("selectedTopics", AppConstants.MOMSPRESSO_CATEGORYID);
                    intent1.putExtra("displayName", getContext().getString(R.string.home_sections_title_momspresso));
                    intent1.putExtra(Constants.FROM_SCREEN, currentScreen);
                    getContext().startActivity(intent1);
                } else if (Constants.KEY_HINDI.equals(listingType)) {
                    Intent hindiIntent = new Intent(getContext(), FilteredTopicsArticleListingActivity.class);
                    if (null != hindiLangModel && !StringUtils.isNullOrEmpty(hindiLangModel.getId())) {
                        hindiIntent.putExtra("selectedTopics", hindiLangModel.getId());
                        hindiIntent.putExtra("displayName", hindiLangModel.getDisplay_name());
                        hindiIntent.putExtra("categoryName", hindiLangModel.getName());
                        hindiIntent.putExtra("isLanguage", true);
                        hindiIntent.putExtra(Constants.FROM_SCREEN, currentScreen);
                        getContext().startActivity(hindiIntent);
                    }

                }
            }
        });

        sectionNameTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Constants.KEY_FOR_YOU.equals(listingType)) {
                    Intent intent1 = new Intent(getContext(), ArticleListingActivity.class);
                    intent1.putExtra(Constants.SORT_TYPE, Constants.KEY_FOR_YOU);
                    intent1.putExtra(Constants.FROM_SCREEN, currentScreen);
                    getContext().startActivity(intent1);
                } else if (Constants.KEY_TRENDING.equals(listingType)) {
                    Intent intent1 = new Intent(getContext(), ArticleListingActivity.class);
                    intent1.putExtra(Constants.SORT_TYPE, Constants.KEY_TRENDING);
                    intent1.putExtra(Constants.FROM_SCREEN, currentScreen);
                    getContext().startActivity(intent1);
                } else if (listingType != null && listingType.contains(Constants.KEY_IN_YOUR_CITY)) {
                    Intent intent1 = new Intent(getContext(), CityBestArticleListingActivity.class);
                    intent1.putExtra(Constants.FROM_SCREEN, currentScreen);
                    getContext().startActivity(intent1);
                } else if (Constants.KEY_EDITOR_PICKS.equals(listingType)) {
                    Intent intent1 = new Intent(getContext(), ArticleListingActivity.class);
                    intent1.putExtra(Constants.SORT_TYPE, Constants.KEY_EDITOR_PICKS);
                    intent1.putExtra(Constants.FROM_SCREEN, currentScreen);
                    getContext().startActivity(intent1);
                } else if (Constants.KEY_MOMSPRESSO.equals(listingType)) {
                    Intent intent1 = new Intent(getContext(), FilteredTopicsArticleListingActivity.class);
                    intent1.putExtra("selectedTopics", AppConstants.MOMSPRESSO_CATEGORYID);
                    intent1.putExtra("displayName", getContext().getString(R.string.home_sections_title_momspresso));
                    intent1.putExtra(Constants.FROM_SCREEN, currentScreen);
                    getContext().startActivity(intent1);
                }
//                else if (Constants.KEY_HINDI.equals(listingType)) {
//                    Intent hindiIntent = new Intent(getContext(), FilteredTopicsArticleListingActivity.class);
//                    hindiIntent.putExtra("selectedTopics", AppConstants.HINDI_CATEGORYID);
//                    hindiIntent.putExtra("displayName", getContext().getString(R.string.home_sections_title_hindi));
//                    getContext().startActivity(hindiIntent);
//                }
            }
        });

//        if (Constants.KEY_HINDI.equals(listingType)) {
//            sectionNameTextView2.setOnClickListener(new OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent banglaIntent = new Intent(getContext(), FilteredTopicsArticleListingActivity.class);
//                    banglaIntent.putExtra("selectedTopics", AppConstants.BANGLA_CATEGORYID);
//                    banglaIntent.putExtra("displayName", getContext().getString(R.string.home_sections_title_bangla));
//                    getContext().startActivity(banglaIntent);
//                }
//            });
//
//            sectionNameTextView3.setOnClickListener(new OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent marathiIntent = new Intent(getContext(), FilteredTopicsArticleListingActivity.class);
//                    marathiIntent.putExtra("selectedTopics", AppConstants.MARATHI_CATEGORYID);
//                    marathiIntent.putExtra("displayName", getContext().getString(R.string.home_sections_title_marathi));
//                    getContext().startActivity(marathiIntent);
//                }
//            });
//        }

        if (mDatalist.isEmpty()) {
            emptyListTextView.setVisibility(View.VISIBLE);
        }
    }

    public ArrayList<VlogsListingAndDetailResult> getVlogslist() {
        return vlogslist;
    }

    public void setVlogslist(final ArrayList<VlogsListingAndDetailResult> vlogslist, final String fromActivity, final String currentScreen) {
        addTopicsTextView.setVisibility(VISIBLE);
        addTopicsTextView.setText("ADD VIDEO");
        addTopicsTextView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ChooseVideoUploadOptionDialogFragment chooseVideoUploadOptionDialogFragment = new ChooseVideoUploadOptionDialogFragment();
                FragmentManager fm = null;
                Bundle _args = new Bundle();
                if ("dashboard".equals(fromActivity)) {
                    fm = ((DashboardActivity) getContext()).getSupportFragmentManager();
                    _args.putString("activity", "dashboard");
                } else if ("allvideosection".equals(fromActivity)) {
                    fm = ((AllVideoSectionActivity) getContext()).getSupportFragmentManager();
                    _args.putString("activity", "allvideosection");
                }
                chooseVideoUploadOptionDialogFragment.setArguments(_args);
                chooseVideoUploadOptionDialogFragment.setCancelable(true);
                chooseVideoUploadOptionDialogFragment.show(fm, "Choose video option");
            }
        });
        this.vlogslist = vlogslist;
        mInflator = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        progressBar.setVisibility(GONE);
        hsvLinearLayout.removeAllViews();
        if (!vlogslist.isEmpty()) {
            Collections.shuffle(vlogslist);
        }
        for (int i = 0; i < vlogslist.size(); i++) {
            final View view = mInflator.inflate(R.layout.card_item_article_dashboard, null);
            view.setTag(i);
            ImageView articleImage = (ImageView) view.findViewById(R.id.imvAuthorThumb);
            TextView title = (TextView) view.findViewById(R.id.txvArticleTitle);
            ImageView videoIndicatorImageView = (ImageView) view.findViewById(R.id.videoIndicatorImageView);
            Picasso.with(getContext()).load(AppUtils.getYoutubeThumbnailURL(vlogslist.get(i).getUrl())).placeholder(R.drawable.default_article).into(articleImage);
            title.setText(vlogslist.get(i).getTitle());
            videoIndicatorImageView.setVisibility(VISIBLE);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.pushEvent(getContext(), GTMEventType.FUNNY_VIDEO_CLICK_EVENT, SharedPrefUtils.getUserDetailModel(getContext()).getDynamoId(), "Home Screen");
                    Intent intent = new Intent(getContext(), VlogsDetailActivity.class);
                    VlogsListingAndDetailResult parentingListData = (VlogsListingAndDetailResult) (vlogslist.get((int) view.getTag()));
                    intent.putExtra(Constants.VIDEO_ID, parentingListData.getId());
                    intent.putExtra(Constants.AUTHOR_ID, parentingListData.getAuthor().getId());
                    intent.putExtra(Constants.ARTICLE_OPENED_FROM, "funnyVideos");
                    intent.putExtra(Constants.FROM_SCREEN, currentScreen);
                    intent.putExtra(Constants.ARTICLE_INDEX, "" + (int) view.getTag());

                    getContext().startActivity(intent);
                    Log.e("Tag", "" + view.getTag());
                }
            });
            hsvLinearLayout.addView(view);
        }

        View customViewMore = mInflator.inflate(R.layout.custom_view_more_dashboard, null);
        DisplayMetrics metrics = new DisplayMetrics();
        if (getContext() != null) {
            ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(metrics);
            int widthPixels = metrics.widthPixels;
            float width = (float) (widthPixels * 0.45);
            customViewMore.setMinimumWidth((int) width);
        }
        hsvLinearLayout.addView(customViewMore);
        customViewMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(getContext(), VlogsListingActivity.class);
                intent1.putExtra(Constants.FROM_SCREEN, currentScreen);
                getContext().startActivity(intent1);
            }
        });

        sectionNameTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(getContext(), VlogsListingActivity.class);
                intent1.putExtra(Constants.FROM_SCREEN, currentScreen);
                getContext().startActivity(intent1);
            }
        });
        if (vlogslist.isEmpty()) {
            emptyListTextView.setVisibility(View.VISIBLE);
        }
    }

    public void setSectionTitle(String title) {
        sectionNameTextView.setText(title);
    }

    public void setMultipleSectionsTitle() {
//        SpannableString content = new SpannableString(title1);
//        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
//        sectionNameTextView.setText(content);
//        sectionNameTextView2.setVisibility(VISIBLE);
//        sectionNameTextView2.setText(title2);
//        sectionNameTextView3.setVisibility(VISIBLE);
//        sectionNameTextView3.setText(title3);
        sectionNameTextView.setVisibility(GONE);
        populateLanguagesInSection();
    }

    private void populateLanguagesInSection() {
        try {
            FileInputStream fileInputStream = getContext().openFileInput(AppConstants.LANGUAGES_JSON_FILE);
            String fileContent = AppUtils.convertStreamToString(fileInputStream);
//            ConfigResult res = new Gson().fromJson(fileContent, ConfigResult.class);
            LinkedHashMap<String, LanguageConfigModel> retMap = new Gson().fromJson(
                    fileContent, new TypeToken<LinkedHashMap<String, LanguageConfigModel>>() {
                    }.getType()
            );
            LayoutInflater inflator = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            Log.d("Map", "" + retMap.toString());
            int i = 0;
            for (final Map.Entry<String, LanguageConfigModel> entry : retMap.entrySet()) {
                final TextView view = (TextView) inflator.inflate(R.layout.horizontal_section_language_item, null);
                if (i == 0) {
                    SpannableString content = new SpannableString(entry.getValue().getDisplay_name());
                    content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
                    view.setText(content);
                } else {
                    view.setText(entry.getValue().getDisplay_name());
                }

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent languageIntent = new Intent(getContext(), FilteredTopicsArticleListingActivity.class);
                        languageIntent.putExtra("selectedTopics", entry.getValue().getId());
                        languageIntent.putExtra("displayName", entry.getValue().getDisplay_name());
                        languageIntent.putExtra("categoryName", entry.getValue().getName());
                        languageIntent.putExtra("isLanguage", true);
                        languageIntent.putExtra(Constants.FROM_SCREEN, "Home Screen");
                        getContext().startActivity(languageIntent);
                    }
                });
                languageContainer.addView(view);
                i++;
            }
        } catch (FileNotFoundException ffe) {
            Crashlytics.logException(ffe);
            Log.d("MC4kException", Log.getStackTraceString(ffe));
        }
    }

    public void setCityNameFromCityId(int cityId) {

        switch (cityId) {
            case 1:
                sectionNameTextView.setText("BEST OF " + "DELHI-NCR");
                break;
            case 2:
                sectionNameTextView.setText("BEST OF " + "BANGLORE");
                break;
            case 3:
                sectionNameTextView.setText("BEST OF " + "MUMBAI");
                break;
            case 4:
                sectionNameTextView.setText("BEST OF " + "PUNE");
                break;
            case 5:
                sectionNameTextView.setText("BEST OF " + "HYDERABAD");
                break;
            case 6:
                sectionNameTextView.setText("BEST OF " + "CHENNAI");
                break;
            case 7:
                sectionNameTextView.setText("BEST OF " + "KOLKATA");
                break;
            case 8:
                sectionNameTextView.setText("BEST OF " + "JAIPUR");
                break;
            case 9:
                sectionNameTextView.setText("BEST OF " + "AHMEDABAD");
                break;
            default:
                sectionNameTextView.setText("BEST OF " + "DELHI-NCR");
                break;
        }
    }

    public void setCityName(String cityName) {
        sectionNameTextView.setText("BEST OF " + cityName);
    }

    public void setProgressBarVisibility(int visibility) {
        progressBar.setVisibility(visibility);
    }

    public void setEmptyListLabelVisibility(int visibility) {
        emptyListTextView.setVisibility(visibility);
    }
}