package com.mycity4kids.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.dbtable.ActivititiesTable;
import com.mycity4kids.enums.MapTypeFilter;
import com.mycity4kids.interfaces.IFilter;
import com.mycity4kids.models.category.Activities;
import com.mycity4kids.ui.activity.BusinessListActivityKidsResources;
import com.mycity4kids.ui.activity.DashboardActivity;
import com.mycity4kids.ui.adapter.ActivitiesFragmentAdapter;

import java.util.ArrayList;
import java.util.HashMap;

public class ActivitiesFragment extends Fragment implements OnClickListener {

    private ListView mActivitiesListView;
    int categoryId, businessOrEvent;
    private IFilter ifilter;
    private ArrayList<Activities> mActivitiesList = null;
    private Fragment parentzFragment;
    EditText query_search;
    ActivitiesFragmentAdapter _adapter;
    ArrayList<Integer> checkboxlist = new ArrayList<>();
    Context mcontext;
    boolean isFromKidsresourcesFlow = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_activities_group, container, false);
        query_search = (EditText) view.findViewById(R.id.query_search);
        ActivititiesTable _table = new ActivititiesTable((BaseApplication) getActivity().getApplication());
        categoryId = getArguments().getInt(Constants.CATEGORY_KEY);
        businessOrEvent = getArguments().getInt(Constants.PAGE_TYPE);
        isFromKidsresourcesFlow = getArguments().getBoolean("flag");
        mActivitiesList = _table.getAllActivity(categoryId);
        if (mActivitiesList != null && mActivitiesList.size() != 0) {
            mActivitiesListView = (ListView) view.findViewById(R.id.subAgeGroupListView);
//            if (isFromKidsresourcesFlow)
//                _adapter = new ActivitiesFragmentAdapter(getActivity(), mActivitiesList);
//            else
            _adapter = new ActivitiesFragmentAdapter(getActivity(), parentzFragment, mActivitiesList);
            mActivitiesListView.setAdapter(_adapter);
            ((TextView) view.findViewById(R.id.list_apply)).setOnClickListener(this);
            ((TextView) view.findViewById(R.id.list_reset)).setOnClickListener(this);
            ((TextView) view.findViewById(R.id.cancel)).setOnClickListener(this);
        } else {
            ((RelativeLayout) view.findViewById(R.id.layout_age_non_blank)).setVisibility(View.GONE);
            ((TextView) view.findViewById(R.id.txt_no_data)).setVisibility(View.VISIBLE);
            ((TextView) view.findViewById(R.id.txt_no_data)).setText(getString(R.string.no_age_group));
        }
        query_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                _adapter.getFilter().filter(s.toString());
                //searchList(s, start, before, count, query_search.getText() + "");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        return view;
    }

    public void searchList(CharSequence s, int start, int before, int count,
                           String edittext) {
        // TODO Auto-generated method stub
        Log.i("Getid", "s" + s);
        Log.i("Getid", "edittext " + edittext);
        ArrayList<Activities> activitieslist = new ArrayList<Activities>();
        int editlength = edittext.length();
        for (int i = 0; i < mActivitiesList.size(); i++) {
            if (editlength <= mActivitiesList.get(i).getName().length()) {
                if (edittext
                        .equalsIgnoreCase((String) mActivitiesList.get(i).getName()
                                .subSequence(0, editlength))) {
                    Log.d("Getid", "success ");
                    Activities activityModel = new Activities();
                    activityModel.setId(mActivitiesList.get(i).getId());
                    activityModel.setName(mActivitiesList.get(i).getName());
                    activitieslist.add(activityModel);
                    //friendslistsearch.add(new Activities(mActivitiesList.get(i).getId(),mActivitiesList.get(i).getName()));
                    Log.d("Getid", "success " + activitieslist.size());
                    /*_adapter = new ActivitiesFragmentAdapter(getActivity(),parentzFragment,
                            activitieslist);*/
                    //_adapter.setList(activitieslist);
                    //mActivitiesListView.setAdapter(_adapter);
                    _adapter.notifyDataSetChanged();


                }


            }

        }
    }

    public void setAction(Fragment parentFragment) {
        ifilter = (IFilter) parentFragment;
        this.parentzFragment = parentFragment;
    }

    public void setAction(Context mcontext) {
        ifilter = (IFilter) mcontext;
        this.mcontext = mcontext;
    }

    private void manageFilter() {

		/*HashMap<String, String>   filterMap  =	((BusinessListActivity) getActivity()).mFilterMap;
		ifilter.doFilter(FilterType.Activities, filterMap,businessOrEvent);*/
        try {
            HashMap<MapTypeFilter, String> filterMap;
            if (parentzFragment == null) {
                // kids resources flow
                filterMap = ((BusinessListActivityKidsResources) getActivity()).mFilterMap;
            } else {
               // filterMap = ((FragmentBusinesslistEvents) parentzFragment).mFilterMap;
                filterMap = ((FragmentBusinesslistEvents.mFilterMap));

            }
            if (filterMap != null && !filterMap.isEmpty() && ifilter != null) {
                ifilter.doNewFilter(businessOrEvent);
            } else {
                if (getActivity() instanceof DashboardActivity) {
                    ((DashboardActivity) getActivity()).showToast(getActivity().getResources().getString(R.string.please_select));
                } else {
                    ((BusinessListActivityKidsResources) getActivity()).showToast(getActivity().getResources().getString(R.string.please_select));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //ifilter.doNewFilter(businessOrEvent);
		/*ArrayList<Activities> selected_activities = new ArrayList<Activities>() ; 
		for(int i = 0 ; i < mActivitiesList.size() ; i++) {
			Activities activities = mActivitiesList.get(i) ; 
			if(activities.isSelected()) {
				selected_activities.add(activities) ; 
			}
		}
		if(selected_activities!=null && !(selected_activities.isEmpty()))
		{
			if(selected_activities != null ) {
				String activities = "" ; 
				for(int i = 0 ; i < selected_activities.size() ; i++) {
					activities += selected_activities.get(i).getId() + "," ; 
				}
			String activitiesFilter=activities.substring(0 , activities.length() - 1 ) ;
			filterMap.put("Activities", "&activities="+activitiesFilter);
			}
			ifilter.doFilter(FilterType.Activities, filterMap,businessOrEvent);
		//	ifilter.doFilter(FilterType.Activities, selected_activities,businessOrEvent);
		}else{
			Toast.makeText(getActivity(), getResources().getString(R.string.please_select), Toast.LENGTH_SHORT).show();
		}*/

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.list_apply:
                manageFilter();
                break;
            case R.id.list_reset:
                ifilter.reject(businessOrEvent);
                break;
            case R.id.cancel:
                ifilter.cancel(businessOrEvent);
                break;
            default:
                break;
        }
    }

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        categoryId = args.getInt(Constants.CATEGORY_KEY);
        businessOrEvent = args.getInt(Constants.PAGE_TYPE);
    }
}
