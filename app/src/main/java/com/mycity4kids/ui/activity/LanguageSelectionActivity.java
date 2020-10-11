package com.mycity4kids.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.base.BaseActivity;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.LanguageSelectionData;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.ui.adapter.LanguageSelectionRecyclerAdapter;
import com.mycity4kids.ui.login.LoginActivity;
import com.mycity4kids.utils.StringUtils;
import com.mycity4kids.widget.MomspressoButtonWidget;
import com.mycity4kids.widget.SpacesItemDecoration;
import com.yariksoffice.lingver.Lingver;
import java.util.ArrayList;

/**
 * Created by hemant on 22/2/18.
 */

public class LanguageSelectionActivity extends BaseActivity implements View.OnClickListener,
        LanguageSelectionRecyclerAdapter.RecyclerViewClickListener {

    private MomspressoButtonWidget continueButtonWidget;
    private String selectedLang = "";
    private RecyclerView languageRecyclerView;
    private LanguageSelectionRecyclerAdapter languageSelectionRecyclerAdapter;
    private RelativeLayout root;
    private ArrayList<LanguageSelectionData> languageSelectionList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.language_selection_activity);
        root = findViewById(R.id.root);
        Utils.pushOpenScreenEvent(this, "LanguageSelectionScreen",
                SharedPrefUtils.getUserDetailModel(this).getDynamoId() + "");
        languageRecyclerView = findViewById(R.id.languageRecyclerView);
        continueButtonWidget = findViewById(R.id.continueButtonWidget);
        continueButtonWidget.setOnClickListener(this);
        continueButtonWidget.setSelected(false);
        final GridLayoutManager glm = new GridLayoutManager(this, 2);
        languageRecyclerView.setLayoutManager(glm);
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.space_8);
        languageRecyclerView.addItemDecoration(new SpacesItemDecoration(spacingInPixels));

        languageSelectionRecyclerAdapter = new LanguageSelectionRecyclerAdapter(this);

        languageSelectionList
                .add(new LanguageSelectionData("English", getString(R.string.language_label_english),
                        R.drawable.lang_enabled_english, R.drawable.lang_disabled_english));
        languageSelectionList
                .add(new LanguageSelectionData("Hindi", getString(R.string.language_label_hindi),
                        R.drawable.lang_enabled_hindi, R.drawable.lang_disabled_hindi));
        languageSelectionList
                .add(new LanguageSelectionData("Marathi", getString(R.string.language_label_marathi),
                        R.drawable.lang_enabled_marathi, R.drawable.lang_disabled_marathi));
        languageSelectionList
                .add(new LanguageSelectionData("Bangla", getString(R.string.language_label_bengali),
                        R.drawable.lang_enabled_bangla, R.drawable.lang_disabled_bangla));
        languageSelectionList
                .add(new LanguageSelectionData("Tamil", getString(R.string.language_label_tamil),
                        R.drawable.lang_enabled_tamil, R.drawable.lang_disabled_tamil));
        languageSelectionList
                .add(new LanguageSelectionData("Telugu", getString(R.string.language_label_telegu),
                        R.drawable.lang_enabled_telugu, R.drawable.lang_disabled_telugu));
        languageSelectionList
                .add(new LanguageSelectionData("Kannada", getString(R.string.language_label_kannada),
                        R.drawable.lang_enabled_kannada, R.drawable.lang_disabled_kannada));
        languageSelectionList
                .add(new LanguageSelectionData("Malayalam", getString(R.string.language_label_malayalam),
                        R.drawable.lang_enabled_malayalam, R.drawable.lang_disabled_malayalam));
        languageSelectionList
                .add(new LanguageSelectionData("Gujarati", getString(R.string.language_label_gujarati),
                        R.drawable.lang_enabled_gujrati, R.drawable.lang_disabled_gujrati));
        languageSelectionList
                .add(new LanguageSelectionData("Punjabi", getString(R.string.language_label_punjabi),
                        R.drawable.lang_enabled_punjabi, R.drawable.lang_disabled_punjabi));

        languageSelectionRecyclerAdapter.setNewListData(languageSelectionList);
        languageRecyclerView.setAdapter(languageSelectionRecyclerAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.continueButtonWidget:
                if (StringUtils.isNullOrEmpty(selectedLang)) {
                    showToast(getString(R.string.lang_sel_choose_lang_toast));
                    return;
                }
                Utils.shareEventTracking(this, "Splash screen", "Onboarding_Android", "Lang_Get_Started");
                setNewLocale(selectedLang);
                break;
            default:
                break;
        }
    }

    private void setNewLocale(String language) {
        getApplicationContext().deleteFile(AppConstants.FOLLOW_UNFOLLOW_TOPICS_JSON_FILE);
        getApplicationContext().deleteFile(AppConstants.CATEGORIES_JSON_FILE);
        BaseApplication.setTopicList(null);
        BaseApplication.setTopicsMap(null);
        BaseApplication.setShortStoryTopicList(null);
        Lingver.getInstance().setLocale(this, language, "IN");
        Utils.initialLanguageSelection(this, "LanguageSelectionActivity", "App_Launch", "Continue",
                "android", language, "NA", String.valueOf(System.currentTimeMillis()), "Initial_language_selection");
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
    }

    @Override
    public void onRecyclerItemClick(View view, int position) {
        continueButtonWidget.setVisibility(View.VISIBLE);
        switch (position) {
            case 0:
                selectedLang = AppConstants.LOCALE_ENGLISH;
                continueButtonWidget.setSelected(true);
                break;
            case 1:
                selectedLang = AppConstants.LOCALE_HINDI;
                continueButtonWidget.setSelected(true);
                break;
            case 2:
                selectedLang = AppConstants.LOCALE_MARATHI;
                continueButtonWidget.setSelected(true);
                break;
            case 3:
                selectedLang = AppConstants.LOCALE_BENGALI;
                continueButtonWidget.setSelected(true);
                break;
            case 4:
                selectedLang = AppConstants.LOCALE_TAMIL;
                continueButtonWidget.setSelected(true);
                break;
            case 5:
                selectedLang = AppConstants.LOCALE_TELUGU;
                continueButtonWidget.setSelected(true);
                break;
            case 6:
                selectedLang = AppConstants.LOCALE_KANNADA;
                continueButtonWidget.setSelected(true);
                break;
            case 7:
                selectedLang = AppConstants.LOCALE_MALAYALAM;
                continueButtonWidget.setSelected(true);
                break;
            case 8:
                selectedLang = AppConstants.LOCALE_GUJARATI;
                continueButtonWidget.setSelected(true);
                break;
            case 9:
                selectedLang = AppConstants.LOCALE_PUNJABI;
                continueButtonWidget.setSelected(true);
                break;
            default:
                continueButtonWidget.setVisibility(View.GONE);
        }
    }
}
