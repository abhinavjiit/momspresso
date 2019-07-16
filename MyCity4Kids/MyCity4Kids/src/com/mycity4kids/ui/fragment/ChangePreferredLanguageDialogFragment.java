package com.mycity4kids.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.mycity4kids.ui.activity.DashboardActivity;
import com.mycity4kids.ui.rewards.fragment.RewardsPersonalInfoFragment;
import com.mycity4kids.utils.LocaleManager;

import static com.facebook.FacebookSdk.getApplicationContext;
import static com.facebook.FacebookSdk.isFacebookRequestCode;

/**
 * Created by user on 08-06-2015.
 */
public class ChangePreferredLanguageDialogFragment extends DialogFragment implements OnClickListener {

    private TextView englishTextView, hindiTextView, marathiTextView, bengaliTextView, tamilTextView,
            teleguTextView, malayalamTextView, kannadaTextView, gujratiTextView;
    private String selectedLang = "";
    private TextView cancelTextView;
    private String userId = "";
    private Boolean isFromPopup = false;
    private static OnClickDoneListener onClickDoneListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Bundle bundle = getArguments();
            isFromPopup = bundle.getBoolean("isFromPopup");
        }
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
                kannadaTextView.setSelected(false);
                malayalamTextView.setSelected(false);
                gujratiTextView.setSelected(false);

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
                kannadaTextView.setSelected(false);
                malayalamTextView.setSelected(false);
                gujratiTextView.setSelected(false);

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
                kannadaTextView.setSelected(false);
                malayalamTextView.setSelected(false);
                gujratiTextView.setSelected(false);

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
                kannadaTextView.setSelected(false);
                malayalamTextView.setSelected(false);
                gujratiTextView.setSelected(false);

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
                kannadaTextView.setSelected(false);
                malayalamTextView.setSelected(false);
                gujratiTextView.setSelected(false);

                setNewLocale(selectedLang, false);
                break;
            case R.id.teluguTextView:
                if (isAdded()) {
                    Utils.pushLanguageChangeEvent(getActivity(), "ChangeLanguageDialog", userId, getString(R.string.language_label_telegu));
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

                setNewLocale(selectedLang, false);
                break;
            case R.id.kannadaTextView:
                if (isAdded()) {
                    Utils.pushLanguageChangeEvent(getActivity(), "ChangeLanguageDialog", userId, getString(R.string.language_label_kannada));
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

                setNewLocale(selectedLang, false);
                break;
            case R.id.malayalamTextView:
                if (isAdded()) {
                    Utils.pushLanguageChangeEvent(getActivity(), "ChangeLanguageDialog", userId, getString(R.string.language_label_malayalam));
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

                setNewLocale(selectedLang, false);
                break;

            case R.id.gujratiTextView:
                if (isAdded()) {
                    Utils.pushLanguageChangeEvent(getActivity(), "ChangeLanguageDialog", userId, getString(R.string.language_label_gujrati));
                }
                selectedLang = AppConstants.LOCAL_GUJRATI;
                englishTextView.setSelected(false);
                hindiTextView.setSelected(false);
                marathiTextView.setSelected(false);
                bengaliTextView.setSelected(false);
                tamilTextView.setSelected(false);
                teleguTextView.setSelected(false);
                kannadaTextView.setSelected(false);
                malayalamTextView.setSelected(false);
                gujratiTextView.setSelected(true);
                setNewLocale(selectedLang, false);
                break;
            case R.id.cancelTextView:
                dismiss();
                break;
        }
    }

    public static ChangePreferredLanguageDialogFragment newInstance(RewardsPersonalInfoFragment context, Boolean isUsingAsPopup) {
        onClickDoneListener = context;
        Bundle args = new Bundle();
        args.putBoolean("isFromPopup", isUsingAsPopup);
        ChangePreferredLanguageDialogFragment fragment = new ChangePreferredLanguageDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private void setNewLocale(String language, boolean restartProcess) {
        if (isFromPopup) {
            onClickDoneListener.onItemClick(language);
            dismiss();
        } else {
            getApplicationContext().deleteFile(AppConstants.FOLLOW_UNFOLLOW_TOPICS_JSON_FILE);
            getApplicationContext().deleteFile(AppConstants.CATEGORIES_JSON_FILE);
            BaseApplication.setTopicList(null);
            BaseApplication.setTopicsMap(null);
            BaseApplication.setShortStoryTopicList(null);
            LocaleManager.setNewLocale(getActivity(), language);
            SharedPrefUtils.setConfigCategoryVersion(getActivity(), 0);
            Intent i = new Intent(getActivity(), DashboardActivity.class);
            startActivity(i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
        }
    }

    public interface OnClickDoneListener {
        void onItemClick(String language);
    }

}