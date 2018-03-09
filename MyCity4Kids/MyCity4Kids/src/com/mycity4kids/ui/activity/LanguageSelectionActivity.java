package com.mycity4kids.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.ui.adapter.LanguageRecyclerViewAdapter;
import com.mycity4kids.utils.LocaleManager;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by hemant on 22/2/18.
 */

public class LanguageSelectionActivity extends BaseActivity implements View.OnClickListener, LanguageRecyclerViewAdapter.RecyclerViewClickListener {

    private RelativeLayout langListOverlay;
    private TextView selectMoreTextView, okayTextView;
    private TextView englishTextView, hindiTextView, marathiTextView, bengaliTextView;
    private TextView continueTextView;
    private String selectedLang = "";
    private TextView currentLangTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.language_selection_activity);

        currentLangTextView = (TextView) findViewById(R.id.currentLangTextView);
        englishTextView = (TextView) findViewById(R.id.englishTextView);
        hindiTextView = (TextView) findViewById(R.id.hindiTextView);
        marathiTextView = (TextView) findViewById(R.id.marathiTextView);
        bengaliTextView = (TextView) findViewById(R.id.bengaliTextView);
        langListOverlay = (RelativeLayout) findViewById(R.id.langListOverlay);
        selectMoreTextView = (TextView) findViewById(R.id.selectMoreTextView);
        okayTextView = (TextView) findViewById(R.id.okayTextView);
        continueTextView = (TextView) findViewById(R.id.continueTextView);

        englishTextView.setOnClickListener(this);
        hindiTextView.setOnClickListener(this);
        marathiTextView.setOnClickListener(this);
        bengaliTextView.setOnClickListener(this);
        selectMoreTextView.setOnClickListener(this);
        okayTextView.setOnClickListener(this);
        continueTextView.setOnClickListener(this);

        if (AppConstants.LOCALE_ENGLISH.equals(SharedPrefUtils.getAppLocale(this))) {
            currentLangTextView.setText(getString(R.string.lang_sel_app_lang_desc, getString(R.string.language_label_english)));
        } else if (AppConstants.LOCALE_HINDI.equals(SharedPrefUtils.getAppLocale(this))) {
            currentLangTextView.setText(getString(R.string.lang_sel_app_lang_desc, getString(R.string.language_label_hindi)));
        } else if (AppConstants.LOCALE_MARATHI.equals(SharedPrefUtils.getAppLocale(this))) {
            currentLangTextView.setText(getString(R.string.lang_sel_app_lang_desc, getString(R.string.language_label_marathi)));
        } else if (AppConstants.LOCALE_BENGALI.equals(SharedPrefUtils.getAppLocale(this))) {
            currentLangTextView.setText(getString(R.string.lang_sel_app_lang_desc, getString(R.string.language_label_bengali)));
        } else {
            langListOverlay.setVisibility(View.GONE);
        }

    }

    @Override
    protected void updateUi(Response response) {

    }

    @Override
    public void onRecyclerItemClick(View view, int position) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.okayTextView:
                Intent intent = new Intent(LanguageSelectionActivity.this, TutorialActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
                break;
            case R.id.selectMoreTextView:
                langListOverlay.setVisibility(View.GONE);
                break;
            case R.id.englishTextView:
                selectedLang = AppConstants.LOCALE_ENGLISH;
                englishTextView.setSelected(true);
                hindiTextView.setSelected(false);
                marathiTextView.setSelected(false);
                bengaliTextView.setSelected(false);
                break;
            case R.id.hindiTextView:
                selectedLang = AppConstants.LOCALE_HINDI;
                englishTextView.setSelected(false);
                hindiTextView.setSelected(true);
                marathiTextView.setSelected(false);
                bengaliTextView.setSelected(false);
                break;
            case R.id.marathiTextView:
                selectedLang = AppConstants.LOCALE_MARATHI;
                englishTextView.setSelected(false);
                hindiTextView.setSelected(false);
                marathiTextView.setSelected(true);
                bengaliTextView.setSelected(false);
                break;
            case R.id.bengaliTextView:
                selectedLang = AppConstants.LOCALE_BENGALI;
                englishTextView.setSelected(false);
                hindiTextView.setSelected(false);
                marathiTextView.setSelected(false);
                bengaliTextView.setSelected(true);
                break;
            case R.id.continueTextView:
                if (StringUtils.isNullOrEmpty(selectedLang)) {
                    showToast(getString(R.string.lang_sel_choose_lang_toast));
                    return;
                }
                setNewLocale(selectedLang, false);
                break;
        }
    }

    private boolean setNewLocale(String language, boolean restartProcess) {
        getApplicationContext().deleteFile(AppConstants.FOLLOW_UNFOLLOW_TOPICS_JSON_FILE);
        getApplicationContext().deleteFile(AppConstants.CATEGORIES_JSON_FILE);
        BaseApplication.setTopicList(null);
        BaseApplication.setTopicsMap(null);

        LocaleManager.setNewLocale(this, language);

        Intent i = new Intent(this, TutorialActivity.class);
        startActivity(i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
        if (restartProcess) {
            System.exit(0);
        }
        return true;
    }
}
