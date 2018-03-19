package com.mycity4kids.ui.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.sync.CategorySyncService;
import com.mycity4kids.ui.activity.DashboardActivity;
import com.mycity4kids.ui.activity.TutorialActivity;
import com.mycity4kids.utils.LocaleManager;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by user on 08-06-2015.
 */
public class ChangePreferredLanguageDialogFragment extends DialogFragment implements OnClickListener {

    private TextView englishTextView, hindiTextView, marathiTextView, bengaliTextView, tamilTextView, teleguTextView;
    private String selectedLang = "";
    private TextView cancelTextView;
    private String userId = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.choose_preferred_lang_dialog, container,
                false);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        if (isAdded()) {
            userId = SharedPrefUtils.getUserDetailModel(getActivity()).getDynamoId();
        }

        englishTextView = (TextView) rootView.findViewById(R.id.englishTextView);
        hindiTextView = (TextView) rootView.findViewById(R.id.hindiTextView);
        marathiTextView = (TextView) rootView.findViewById(R.id.marathiTextView);
        bengaliTextView = (TextView) rootView.findViewById(R.id.bengaliTextView);
        teleguTextView = (TextView) rootView.findViewById(R.id.teleguTextView);
        tamilTextView = (TextView) rootView.findViewById(R.id.tamilTextView);
        cancelTextView = (TextView) rootView.findViewById(R.id.cancelTextView);

        englishTextView.setOnClickListener(this);
        hindiTextView.setOnClickListener(this);
        marathiTextView.setOnClickListener(this);
        bengaliTextView.setOnClickListener(this);
        teleguTextView.setOnClickListener(this);
        tamilTextView.setOnClickListener(this);
        cancelTextView.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.englishTextView:
                if (isAdded()) {
                    Utils.pushLanguageChangeEvent(getActivity(), "ChangeLanguageDialog", userId, getString(R.string.language_label_english));
                }
                selectedLang = AppConstants.LOCALE_ENGLISH;
                englishTextView.setSelected(true);
                hindiTextView.setSelected(false);
                marathiTextView.setSelected(false);
                bengaliTextView.setSelected(false);
                tamilTextView.setSelected(false);
                teleguTextView.setSelected(false);
                setNewLocale(selectedLang, false);
                break;
            case R.id.hindiTextView:
                if (isAdded()) {
                    Utils.pushLanguageChangeEvent(getActivity(), "ChangeLanguageDialog", userId, getString(R.string.language_label_hindi));
                }
                selectedLang = AppConstants.LOCALE_HINDI;
                englishTextView.setSelected(false);
                hindiTextView.setSelected(true);
                marathiTextView.setSelected(false);
                bengaliTextView.setSelected(false);
                tamilTextView.setSelected(false);
                teleguTextView.setSelected(false);
                setNewLocale(selectedLang, false);
                break;
            case R.id.marathiTextView:
                if (isAdded()) {
                    Utils.pushLanguageChangeEvent(getActivity(), "ChangeLanguageDialog", userId, getString(R.string.language_label_marathi));
                }
                selectedLang = AppConstants.LOCALE_MARATHI;
                englishTextView.setSelected(false);
                hindiTextView.setSelected(false);
                marathiTextView.setSelected(true);
                bengaliTextView.setSelected(false);
                tamilTextView.setSelected(false);
                teleguTextView.setSelected(false);
                setNewLocale(selectedLang, false);
                break;
            case R.id.bengaliTextView:
                if (isAdded()) {
                    Utils.pushLanguageChangeEvent(getActivity(), "ChangeLanguageDialog", userId, getString(R.string.language_label_bengali));
                }
                selectedLang = AppConstants.LOCALE_BENGALI;
                englishTextView.setSelected(false);
                hindiTextView.setSelected(false);
                marathiTextView.setSelected(false);
                bengaliTextView.setSelected(true);
                tamilTextView.setSelected(false);
                teleguTextView.setSelected(false);
                setNewLocale(selectedLang, false);
                break;
            case R.id.tamilTextView:
                if (isAdded()) {
                    Utils.pushLanguageChangeEvent(getActivity(), "ChangeLanguageDialog", userId, getString(R.string.language_label_tamil));
                }
                selectedLang = AppConstants.LOCALE_TAMIL;
                englishTextView.setSelected(false);
                hindiTextView.setSelected(false);
                marathiTextView.setSelected(false);
                bengaliTextView.setSelected(false);
                tamilTextView.setSelected(true);
                teleguTextView.setSelected(false);
                setNewLocale(selectedLang, false);
                break;
            case R.id.teleguTextView:
                if (isAdded()) {
                    Utils.pushLanguageChangeEvent(getActivity(), "ChangeLanguageDialog", userId, getString(R.string.language_label_telegu));
                }
                selectedLang = AppConstants.LOCALE_TELEGU;
                englishTextView.setSelected(false);
                hindiTextView.setSelected(false);
                marathiTextView.setSelected(false);
                bengaliTextView.setSelected(false);
                tamilTextView.setSelected(false);
                teleguTextView.setSelected(true);
                setNewLocale(selectedLang, false);
                break;
            case R.id.cancelTextView:
                dismiss();
                break;
        }
    }

    private void setNewLocale(String language, boolean restartProcess) {
        getApplicationContext().deleteFile(AppConstants.FOLLOW_UNFOLLOW_TOPICS_JSON_FILE);
        getApplicationContext().deleteFile(AppConstants.CATEGORIES_JSON_FILE);
        BaseApplication.setTopicList(null);
        BaseApplication.setTopicsMap(null);
        LocaleManager.setNewLocale(getActivity(), language);
        SharedPrefUtils.setConfigCategoryVersion(getActivity(), 0);
        Intent i = new Intent(getActivity(), DashboardActivity.class);
        startActivity(i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
    }


}