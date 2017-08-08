package com.mycity4kids.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseFragment;
import com.mycity4kids.R;

/**
 * Created by hemant on 2/8/17.
 */
public class BecomeBloggerTabFragment extends BaseFragment {

    private TextView titleTextView, descTextView;
    private ImageView headerImageView, cornerImageView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.become_blogger_tab_fragment, container, false);

        titleTextView = (TextView) view.findViewById(R.id.titleTextView);
        descTextView = (TextView) view.findViewById(R.id.descTextView);
        headerImageView = (ImageView) view.findViewById(R.id.headerImageView);
        cornerImageView = (ImageView) view.findViewById(R.id.cornerImageView);

        String title = getArguments().getString("title");
        String desc = getArguments().getString("desc");
        int position = getArguments().getInt("position");

        switch (position) {
            case 0:
                headerImageView.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_blogger1));
                cornerImageView.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_red_edit));
                break;
            case 1:
                headerImageView.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_blogger2));
                cornerImageView.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_red_edit));
                break;
            case 2:
                headerImageView.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_cash));
                cornerImageView.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_rupee));
                break;
            case 3:
                headerImageView.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_blogger1));
                cornerImageView.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_question_mark));
                break;
        }
        titleTextView.setText("" + title);
        descTextView.setText("" + desc);
        return view;
    }

    @Override
    protected void updateUi(Response response) {

    }
}
