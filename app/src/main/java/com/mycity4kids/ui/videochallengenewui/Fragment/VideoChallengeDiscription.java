package com.mycity4kids.ui.videochallengenewui.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.utils.StringUtils;
import com.mycity4kids.utils.ToastUtils;

public class VideoChallengeDiscription extends Fragment {

    private String challengeRules;

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.challenge_discription_layout, container, false);
        WebView webView = view.findViewById(R.id.videoChallengeRulesWebView);
        if (getArguments() != null) {
            challengeRules = getArguments().getString("challengeRules");
        } else {
            ToastUtils.showToast(getContext(), "something went wrong at the server");
        }
        if (!StringUtils.isNullOrEmpty(challengeRules)) {
            webView.loadDataWithBaseURL("", challengeRules, "text/html", "UTF-8", "");
        }
        Utils.momVlogEvent(getActivity(), "Challenge detail", "About", "", "android",
                SharedPrefUtils.getAppLocale(BaseApplication.getAppContext()),
                SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId(),
                String.valueOf(System.currentTimeMillis()), "Show_challenge_detail", "", "");

        return view;
    }
}
