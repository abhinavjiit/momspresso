package com.mycity4kids.ui.videochallengenewui.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatDelegate;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kelltontech.utils.StringUtils;
import com.kelltontech.utils.ToastUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.Topics;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.ui.videochallengenewui.activity.NewVideoChallengeActivity;

public class VideoChallengeDiscription extends Fragment implements View.OnClickListener {

    private TextView challengeRuleWithInfoIcon, submitStoryText;
    private ImageView infoImage;
    private Topics topics;
    private LinearLayout challengeRuleLinearLayout;
    private String selectedName, selectedId, challengeRules;
    private WebView webView;
    private String url;


    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.challenge_discription_layout, container, false);
        challengeRuleWithInfoIcon = (TextView) view.findViewById(R.id.challengeRuleWithInfoIcon);
        infoImage = (ImageView) view.findViewById(R.id.infoImage);
        challengeRuleLinearLayout = (LinearLayout) view.findViewById(R.id.challengeRuleLinearLayout);
        submitStoryText = (TextView) view.findViewById(R.id.submitStoryText);
        webView = (WebView) view.findViewById(R.id.videoChallengeRulesWebView);
        if (getArguments() != null) {
            selectedId = getArguments().getString("selectedId");
            selectedName = getArguments().getString("selected_Name");
            challengeRules = getArguments().getString("challengeRules");
            topics = getArguments().getParcelable("topics");

        } else {
            ToastUtils.showToast(getContext(), "something went wrong at the server");
        }
        if (!StringUtils.isNullOrEmpty(challengeRules)) {
            webView.loadData(challengeRules, "text/html", "UTF-8");
        }
        Utils.momVlogEvent(getActivity(), "Challenge detail", "About", "", "android", SharedPrefUtils.getAppLocale(getActivity()), SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId(), String.valueOf(System.currentTimeMillis()), "Show_challenge__detail", "", "");

        return view;
    }

    @Override
    public void onClick(View view) {


    }


}
