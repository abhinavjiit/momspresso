package com.mycity4kids.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.base.BaseFragment;
import com.mycity4kids.editor.NewEditor;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.ui.adapter.SuggestedTopicsAdapter;
import java.util.ArrayList;

/**
 * Created by hemant on 1/11/17.
 */

public class SuggestedTopicsTabFragment extends BaseFragment implements
        SuggestedTopicsAdapter.RecyclerViewClickListener, View.OnClickListener {

    private String languageName;
    private RecyclerView recyclerView;
    private SuggestedTopicsAdapter adapter;
    private TextView startWritingTextView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.suggested_topics_tab_fragment, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        startWritingTextView = view.findViewById(R.id.startWritingTextView);

        startWritingTextView.setOnClickListener(this);

        ArrayList<String> list = getArguments().getStringArrayList("languageData");
        languageName = getArguments().getString("languageName");

        adapter = new SuggestedTopicsAdapter(getActivity(), this);
        adapter.setListData(list);
        final LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onClick(View view, int position) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.startWritingTextView:
                Utils.pushSuggestedTopicClickEvent(getActivity(), "SuggestedTopicScreen",
                        SharedPrefUtils.getUserDetailModel(BaseApplication.getAppContext()).getDynamoId(),
                        languageName);
                Intent intent = new Intent(getActivity(), NewEditor.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }
}
