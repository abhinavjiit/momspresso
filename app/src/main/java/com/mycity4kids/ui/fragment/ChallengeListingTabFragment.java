package com.mycity4kids.ui.fragment;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mycity4kids.base.BaseFragment;
import com.mycity4kids.R;
import com.mycity4kids.ui.adapter.ChallengeListingRecycleAdapter;

public class ChallengeListingTabFragment extends BaseFragment implements View.OnClickListener, ChallengeListingRecycleAdapter.RecyclerViewClickListener {
    private RecyclerView recyclerView;
    private LinearLayoutManager llm;
    private ChallengeListingRecycleAdapter challengeListingRecycleAdapter;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.challenge_detail_listing_fragment, container, false);
        recyclerView = view.findViewById(R.id.recyclerview_challenge);
        llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        //challengeListingRecycleAdapter = new ChallengeListingRecycleAdapter(this, getActivity());
        // recyclerView.setAdapter(challengeListingRecycleAdapter);
        return view;

    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onClick(View view, int position, String activeUrl) {

    }
}
