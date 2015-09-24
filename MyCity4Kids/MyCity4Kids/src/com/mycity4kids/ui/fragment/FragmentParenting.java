package com.mycity4kids.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseFragment;
import com.mycity4kids.R;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.enums.ParentingFilterType;
import com.mycity4kids.ui.activity.ParentingArticlesActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by khushboo.goyal on 08-06-2015.
 */
public class FragmentParenting extends BaseFragment implements View.OnClickListener {


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.aa_parenting, container, false);

        view.findViewById(R.id.articles).setOnClickListener(this);
        view.findViewById(R.id.blogs).setOnClickListener(this);


        return view;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.articles:
                Intent intent = new Intent(getActivity(), ParentingArticlesActivity.class);
                intent.putExtra(Constants.PARENTING_TYPE, ParentingFilterType.ARTICLES);
                startActivity(intent);
                break;
            case R.id.blogs:
                intent = new Intent(getActivity(), ParentingArticlesActivity.class);
                intent.putExtra(Constants.PARENTING_TYPE, ParentingFilterType.BLOGS);
                startActivity(intent);
                break;
        }
    }

    @Override
    protected void updateUi(Response response) {

    }
}
