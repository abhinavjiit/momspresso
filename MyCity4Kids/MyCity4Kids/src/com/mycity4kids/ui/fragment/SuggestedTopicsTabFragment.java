package com.mycity4kids.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseFragment;
import com.mycity4kids.R;
import com.mycity4kids.editor.EditorPostActivity;
import com.mycity4kids.ui.adapter.SuggestedTopicsAdapter;

import java.util.ArrayList;

/**
 * Created by hemant on 1/11/17.
 */

public class SuggestedTopicsTabFragment extends BaseFragment implements SuggestedTopicsAdapter.RecyclerViewClickListener, View.OnClickListener {

    private RecyclerView recyclerView;
    private SuggestedTopicsAdapter adapter;
    private TextView textView;
    private TextView startWritingTextView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.suggested_topics_tab_fragment, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        startWritingTextView = (TextView) view.findViewById(R.id.startWritingTextView);

        startWritingTextView.setOnClickListener(this);

        ArrayList<String> list = getArguments().getStringArrayList("languageData");

        adapter = new SuggestedTopicsAdapter(getActivity(), this);
        adapter.setListData(list);
        final LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    protected void updateUi(Response response) {

    }

    @Override
    public void onClick(View view, int position) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.startWritingTextView:
                Intent intent = new Intent(getActivity(), EditorPostActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString(EditorPostActivity.TITLE_PARAM, "");
                bundle.putString(EditorPostActivity.CONTENT_PARAM, "");
                bundle.putString(EditorPostActivity.TITLE_PLACEHOLDER_PARAM,
                        getString(R.string.example_post_title_placeholder));
                bundle.putString(EditorPostActivity.CONTENT_PLACEHOLDER_PARAM,
                        getString(R.string.example_post_content_placeholder));
                bundle.putInt(EditorPostActivity.EDITOR_PARAM, EditorPostActivity.USE_NEW_EDITOR);
                bundle.putString("from", "dashboard");
                intent.putExtras(bundle);
                startActivity(intent);
                break;
        }
    }
}
