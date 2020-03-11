package com.mycity4kids.ui.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.ui.activity.DashboardActivity;
import com.yariksoffice.lingver.Lingver;

/**
 * Created by user on 08-06-2015.
 */
public class ChangePreferredLanguageDialogFragment extends DialogFragment implements OnClickListener {

    private TextView englishTextView;
    private TextView hindiTextView;
    private TextView marathiTextView;
    private TextView bengaliTextView;
    private TextView tamilTextView;
    private TextView teleguTextView;
    private TextView malayalamTextView;
    private TextView kannadaTextView;
    private TextView gujratiTextView;
    private TextView punjabiTextView;
    private TextView cancelTextView;
    private String userId = "";
    private String selectedLang;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

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
        teleguTextView = (TextView) rootView.findViewById(R.id.teluguTextView);
        tamilTextView = (TextView) rootView.findViewById(R.id.tamilTextView);
        kannadaTextView = (TextView) rootView.findViewById(R.id.kannadaTextView);
        malayalamTextView = (TextView) rootView.findViewById(R.id.malayalamTextView);
        cancelTextView = (TextView) rootView.findViewById(R.id.cancelTextView);
        gujratiTextView = (TextView) rootView.findViewById(R.id.gujratiTextView);
        punjabiTextView = (TextView) rootView.findViewById(R.id.punjabiTextView);

        englishTextView.setOnClickListener(this);
        hindiTextView.setOnClickListener(this);
        marathiTextView.setOnClickListener(this);
        bengaliTextView.setOnClickListener(this);
        teleguTextView.setOnClickListener(this);
        tamilTextView.setOnClickListener(this);
        kannadaTextView.setOnClickListener(this);
        malayalamTextView.setOnClickListener(this);
        cancelTextView.setOnClickListener(this);
        gujratiTextView.setOnClickListener(this);
        punjabiTextView.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.englishTextView:
                if (isAdded()) {
                    Utils.pushLanguageChangeEvent(getActivity(), "ChangeLanguageDialog", userId,
                            getString(R.string.language_label_english));
                }
                selectedLang = AppConstants.LOCALE_ENGLISH;
                englishTextView.setSelected(true);
                hindiTextView.setSelected(false);
                marathiTextView.setSelected(false);
                bengaliTextView.setSelected(false);
                tamilTextView.setSelected(false);
                teleguTextView.setSelected(false);
                kannadaTextView.setSelected(false);
                malayalamTextView.setSelected(false);
                gujratiTextView.setSelected(false);
                setNewLocale(selectedLang);
                break;
            case R.id.hindiTextView:
                if (isAdded()) {
                    Utils.pushLanguageChangeEvent(getActivity(), "ChangeLanguageDialog", userId,
                            getString(R.string.language_label_hindi));
                }
                selectedLang = AppConstants.LOCALE_HINDI;
                englishTextView.setSelected(false);
                hindiTextView.setSelected(true);
                marathiTextView.setSelected(false);
                bengaliTextView.setSelected(false);
                tamilTextView.setSelected(false);
                teleguTextView.setSelected(false);
                kannadaTextView.setSelected(false);
                malayalamTextView.setSelected(false);
                gujratiTextView.setSelected(false);
                setNewLocale(selectedLang);
                break;
            case R.id.marathiTextView:
                if (isAdded()) {
                    Utils.pushLanguageChangeEvent(getActivity(), "ChangeLanguageDialog", userId,
                            getString(R.string.language_label_marathi));
                }
                selectedLang = AppConstants.LOCALE_MARATHI;
                englishTextView.setSelected(false);
                hindiTextView.setSelected(false);
                marathiTextView.setSelected(true);
                bengaliTextView.setSelected(false);
                tamilTextView.setSelected(false);
                teleguTextView.setSelected(false);
                kannadaTextView.setSelected(false);
                malayalamTextView.setSelected(false);
                gujratiTextView.setSelected(false);
                setNewLocale(selectedLang);
                break;
            case R.id.bengaliTextView:
                if (isAdded()) {
                    Utils.pushLanguageChangeEvent(getActivity(), "ChangeLanguageDialog", userId,
                            getString(R.string.language_label_bengali));
                }
                selectedLang = AppConstants.LOCALE_BENGALI;
                englishTextView.setSelected(false);
                hindiTextView.setSelected(false);
                marathiTextView.setSelected(false);
                bengaliTextView.setSelected(true);
                tamilTextView.setSelected(false);
                teleguTextView.setSelected(false);
                kannadaTextView.setSelected(false);
                malayalamTextView.setSelected(false);
                gujratiTextView.setSelected(false);
                setNewLocale(selectedLang);
                break;
            case R.id.tamilTextView:
                if (isAdded()) {
                    Utils.pushLanguageChangeEvent(getActivity(), "ChangeLanguageDialog", userId,
                            getString(R.string.language_label_tamil));
                }
                selectedLang = AppConstants.LOCALE_TAMIL;
                englishTextView.setSelected(false);
                hindiTextView.setSelected(false);
                marathiTextView.setSelected(false);
                bengaliTextView.setSelected(false);
                tamilTextView.setSelected(true);
                teleguTextView.setSelected(false);
                kannadaTextView.setSelected(false);
                malayalamTextView.setSelected(false);
                gujratiTextView.setSelected(false);
                setNewLocale(selectedLang);
                break;
            case R.id.teluguTextView:
                if (isAdded()) {
                    Utils.pushLanguageChangeEvent(getActivity(), "ChangeLanguageDialog", userId,
                            getString(R.string.language_label_telegu));
                }
                selectedLang = AppConstants.LOCALE_TELUGU;
                englishTextView.setSelected(false);
                hindiTextView.setSelected(false);
                marathiTextView.setSelected(false);
                bengaliTextView.setSelected(false);
                tamilTextView.setSelected(false);
                teleguTextView.setSelected(true);
                kannadaTextView.setSelected(false);
                malayalamTextView.setSelected(false);
                gujratiTextView.setSelected(false);
                setNewLocale(selectedLang);
                break;
            case R.id.kannadaTextView:
                if (isAdded()) {
                    Utils.pushLanguageChangeEvent(getActivity(), "ChangeLanguageDialog", userId,
                            getString(R.string.language_label_kannada));
                }
                selectedLang = AppConstants.LOCALE_KANNADA;
                englishTextView.setSelected(false);
                hindiTextView.setSelected(false);
                marathiTextView.setSelected(false);
                bengaliTextView.setSelected(false);
                tamilTextView.setSelected(false);
                teleguTextView.setSelected(false);
                kannadaTextView.setSelected(true);
                malayalamTextView.setSelected(false);
                gujratiTextView.setSelected(false);

                setNewLocale(selectedLang);
                break;
            case R.id.malayalamTextView:
                if (isAdded()) {
                    Utils.pushLanguageChangeEvent(getActivity(), "ChangeLanguageDialog", userId,
                            getString(R.string.language_label_malayalam));
                }
                selectedLang = AppConstants.LOCALE_MALAYALAM;
                englishTextView.setSelected(false);
                hindiTextView.setSelected(false);
                marathiTextView.setSelected(false);
                bengaliTextView.setSelected(false);
                tamilTextView.setSelected(false);
                teleguTextView.setSelected(false);
                kannadaTextView.setSelected(false);
                malayalamTextView.setSelected(true);
                gujratiTextView.setSelected(false);
                setNewLocale(selectedLang);
                break;
            case R.id.gujratiTextView:
                if (isAdded()) {
                    Utils.pushLanguageChangeEvent(getActivity(), "ChangeLanguageDialog", userId,
                            getString(R.string.language_label_gujarati));
                }
                selectedLang = AppConstants.LOCALE_GUJARATI;
                englishTextView.setSelected(false);
                hindiTextView.setSelected(false);
                marathiTextView.setSelected(false);
                bengaliTextView.setSelected(false);
                tamilTextView.setSelected(false);
                teleguTextView.setSelected(false);
                kannadaTextView.setSelected(false);
                malayalamTextView.setSelected(false);
                gujratiTextView.setSelected(true);
                setNewLocale(selectedLang);
                break;
            case R.id.punjabiTextView:
                if (isAdded()) {
                    Utils.pushLanguageChangeEvent(getActivity(), "ChangeLanguageDialog", userId,
                            getString(R.string.language_label_punjabi));
                }
                selectedLang = AppConstants.LOCALE_PUNJABI;
                englishTextView.setSelected(false);
                hindiTextView.setSelected(false);
                marathiTextView.setSelected(false);
                bengaliTextView.setSelected(false);
                tamilTextView.setSelected(false);
                teleguTextView.setSelected(false);
                kannadaTextView.setSelected(false);
                malayalamTextView.setSelected(false);
                gujratiTextView.setSelected(false);
                punjabiTextView.setSelected(true);
                setNewLocale(selectedLang);
                break;
            case R.id.cancelTextView:
                dismiss();
                break;
            default:
                break;
        }
    }

    private void setNewLocale(String language) {
        dismiss();
        BaseApplication.getAppContext().deleteFile(AppConstants.FOLLOW_UNFOLLOW_TOPICS_JSON_FILE);
        BaseApplication.getAppContext().deleteFile(AppConstants.CATEGORIES_JSON_FILE);
        BaseApplication.setTopicList(null);
        BaseApplication.setTopicsMap(null);
        BaseApplication.setShortStoryTopicList(null);
        if (getActivity() != null) {
            Lingver.getInstance().setLocale(getActivity(), language, "IN");
        }
        SharedPrefUtils.setConfigCategoryVersion(BaseApplication.getAppContext(), 0);
        if (getActivity() != null) {
            Intent i = new Intent(getActivity(), DashboardActivity.class);
            getActivity().finish();
            startActivity(i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
        }
    }
}