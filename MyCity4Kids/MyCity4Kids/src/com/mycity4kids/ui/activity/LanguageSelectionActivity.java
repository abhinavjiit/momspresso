package com.mycity4kids.ui.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.ui.adapter.LanguageSelectionRecyclerAdapter;
import com.mycity4kids.utils.LocaleManager;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by hemant on 22/2/18.
 */

public class LanguageSelectionActivity extends BaseActivity implements View.OnClickListener, LanguageSelectionRecyclerAdapter.RecyclerViewClickListener {

    private RelativeLayout langListOverlay;
    private TextView selectMoreTextView, okayTextView;
    private TextView englishTextView, hindiTextView, marathiTextView, bengaliTextView, tamilTextView, teluguTextView;
    private TextView continueTextView;
    private String selectedLang = "";
    private TextView currentLangTextView;
    private RecyclerView languageRecyclerView;

    private LanguageSelectionRecyclerAdapter languageSelectionRecyclerAdapter;
    private RelativeLayout root;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.language_selection_activity);
        root = findViewById(R.id.root);
        ((BaseApplication) getApplication()).setView(root);
        ((BaseApplication) getApplication()).setActivity(this);

        Utils.pushOpenScreenEvent(this, "LanguageSelectionScreen", SharedPrefUtils.getUserDetailModel(this).getDynamoId() + "");
        languageRecyclerView = (RecyclerView) findViewById(R.id.languageRecyclerView);
        String[] langArray = {getString(R.string.language_label_english), getString(R.string.language_label_hindi), getString(R.string.language_label_marathi),
                getString(R.string.language_label_bengali), getString(R.string.language_label_telegu), getString(R.string.language_label_tamil)
                , getString(R.string.language_label_kannada), getString(R.string.language_label_malayalam), getString(R.string.language_label_gujarati), getString(R.string.language_label_punjabi)};
        ArrayList<String> langList = new ArrayList<String>(Arrays.asList(langArray));

        continueTextView = (TextView) findViewById(R.id.continueTextView);


        continueTextView.setOnClickListener(this);

        final LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        languageRecyclerView.setLayoutManager(llm);

        languageSelectionRecyclerAdapter = new LanguageSelectionRecyclerAdapter(this, this);
        languageSelectionRecyclerAdapter.setNewListData(langList);

        languageRecyclerView.setAdapter(languageSelectionRecyclerAdapter);


    }

    @Override
    protected void updateUi(Response response) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
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
        BaseApplication.setShortStoryTopicList(null);
        LocaleManager.setNewLocale(this, language);
        Utils.initialLanguageSelection(this, "LanguageSelectionActivity", "App_Launch", "Continue", "android"
                , language, "NA", String.valueOf(System.currentTimeMillis()), "Initial_language_selection");
        Intent i = new Intent(this, TutorialActivity.class);
        startActivity(i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
        if (restartProcess) {
            System.exit(0);
        }
        return true;
    }

    @Override
    public void onRecyclerItemClick(View view, int position) {
        continueTextView.setVisibility(View.VISIBLE);
        switch (position) {
            case 0:
                selectedLang = AppConstants.LOCALE_ENGLISH;
                break;
            case 1:
                selectedLang = AppConstants.LOCALE_HINDI;
                break;
            case 2:
                selectedLang = AppConstants.LOCALE_MARATHI;
                break;
            case 3:
                selectedLang = AppConstants.LOCALE_BENGALI;
                break;
            case 4:
                selectedLang = AppConstants.LOCALE_TELUGU;
                break;
            case 5:
                selectedLang = AppConstants.LOCALE_TAMIL;
                break;
            case 6:
                selectedLang = AppConstants.LOCALE_KANNADA;
                break;
            case 7:
                selectedLang = AppConstants.LOCALE_MALAYALAM;
                break;
            case 8:
                selectedLang = AppConstants.LOCAL_GUJARATI;
                break;
            case 9:
                selectedLang = AppConstants.LOCAL_PUNJABI;
                break;
            default:
                continueTextView.setVisibility(View.GONE);
        }

    }
}
