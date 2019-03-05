package com.mycity4kids.ui.videochallengenewui.Fragment;

import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ScaleDrawable;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatDelegate;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kelltontech.utils.ToastUtils;
import com.mycity4kids.R;
import com.mycity4kids.loading.Utils;
import com.mycity4kids.models.Topics;
import com.mycity4kids.ui.activity.ChooseVideoCategoryActivity;

public class VideoChallengeDiscription extends Fragment implements View.OnClickListener {

    private TextView challengeRuleWithInfoIcon, submitStoryText;
    private ImageView infoImage;
    private Topics topics;
    private LinearLayout challengeRuleLinearLayout;
    private String selectedName, selectedId;


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
        if (getArguments() != null) {
            selectedId = getArguments().getString("selectedId");
            selectedName = getArguments().getString("selected_Name");

            topics = getArguments().getParcelable("topics");

        } else {
            ToastUtils.showToast(getContext(), "something went wrong at the server");
        }
      /*  ViewGroup.LayoutParams lparams = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        TextView tv = new TextView(getContext());
        tv.setLayoutParams(lparams);
        tv.setText("1. Ahujd asjd jeduwueb bdcd uru weiw ioee");
        tv.setTypeface(Typeface.createFromAsset(getResources().getAssets(), "fonts/oswald.ttf"));

        this.challengeRuleLinearLayout.addView(tv);
 */
        submitStoryText.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {

        Intent intent = new Intent(getActivity(), ChooseVideoCategoryActivity.class);
        if (selectedName != null && !selectedName.isEmpty() && selectedId != null && !selectedId.isEmpty()) {
            intent.putExtra("selectedId", selectedId);
            intent.putExtra("selectedName", selectedName);
            intent.putExtra("comingFrom", "Challenge");
            startActivity(intent);

        } else {
            ToastUtils.showToast(getContext(), "something went wrong at the server");

        }

    }
}
