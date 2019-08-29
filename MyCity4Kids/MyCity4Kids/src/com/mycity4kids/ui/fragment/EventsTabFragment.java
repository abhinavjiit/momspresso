package com.mycity4kids.ui.fragment;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseFragment;
import com.mycity4kids.R;
import com.mycity4kids.ui.adapter.EventSubcategoriesRecyclerAdapter;
import com.mycity4kids.ui.adapter.EventsRecyclerAdapter;

/**
 * Created by hemant on 8/12/17.
 */

public class EventsTabFragment extends BaseFragment {

    private RecyclerView allEventsRecyclerView;
    private RecyclerView eventsSubcategoryRecyclerView;
    private RecyclerView trendingEventsRecyclerView;
    private RecyclerView featuredEventsRecyclerView;
    private RecyclerView upcomingEventsRecyclerView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.events_tab_fragment, container, false);
        allEventsRecyclerView = (RecyclerView) view.findViewById(R.id.allEventsRecyclerView);
        eventsSubcategoryRecyclerView = (RecyclerView) view.findViewById(R.id.eventsSubcategoryRecyclerView);
        trendingEventsRecyclerView = (RecyclerView) view.findViewById(R.id.trendingEventsRecyclerView);
        featuredEventsRecyclerView = (RecyclerView) view.findViewById(R.id.featuredEventsRecyclerView);
        upcomingEventsRecyclerView = (RecyclerView) view.findViewById(R.id.upcomingEventsRecyclerView);

        EventsRecyclerAdapter eventRecyclerAdapter = new EventsRecyclerAdapter(getActivity());
        EventSubcategoriesRecyclerAdapter eventSubcategoriesRecyclerAdapter = new EventSubcategoriesRecyclerAdapter(getActivity());

        final LinearLayoutManager llm1 = new LinearLayoutManager(getActivity());
        llm1.setOrientation(LinearLayoutManager.HORIZONTAL);
        allEventsRecyclerView.setLayoutManager(llm1);
        allEventsRecyclerView.setAdapter(eventRecyclerAdapter);

        final LinearLayoutManager llm2 = new LinearLayoutManager(getActivity());
        llm2.setOrientation(LinearLayoutManager.HORIZONTAL);
        eventsSubcategoryRecyclerView.setLayoutManager(llm2);
        eventsSubcategoryRecyclerView.setAdapter(eventSubcategoriesRecyclerAdapter);

        final LinearLayoutManager llm3 = new LinearLayoutManager(getActivity());
        llm3.setOrientation(LinearLayoutManager.HORIZONTAL);
        trendingEventsRecyclerView.setLayoutManager(llm3);
        trendingEventsRecyclerView.setAdapter(eventRecyclerAdapter);

        final LinearLayoutManager llm4 = new LinearLayoutManager(getActivity());
        llm4.setOrientation(LinearLayoutManager.HORIZONTAL);
        featuredEventsRecyclerView.setLayoutManager(llm4);
        featuredEventsRecyclerView.setAdapter(eventRecyclerAdapter);

        final LinearLayoutManager llm5 = new LinearLayoutManager(getActivity());
        llm5.setOrientation(LinearLayoutManager.HORIZONTAL);
        upcomingEventsRecyclerView.setLayoutManager(llm5);
        upcomingEventsRecyclerView.setAdapter(eventRecyclerAdapter);
        return view;
    }

    @Override
    protected void updateUi(Response response) {

    }
}
