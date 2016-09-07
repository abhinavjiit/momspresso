package com.mycity4kids.ui.fragment;

import android.app.Activity;
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
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.dbtable.LocalityTable;
import com.mycity4kids.enums.MapTypeFilter;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.interfaces.IFilter;
import com.mycity4kids.models.locality.LocalityModel;
import com.mycity4kids.models.locality.ZoneModel;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.ui.activity.BusinessListActivityKidsResources;
import com.mycity4kids.ui.activity.DashboardActivity;
import com.mycity4kids.ui.adapter.LocalitiesAdapter;

import java.util.ArrayList;
import java.util.HashMap;

public class LocalitiesFragment extends Fragment implements OnClickListener {


    private HashMap<ZoneModel, ArrayList<LocalityModel>> localityData;
    private IFilter ifilter;
    private ArrayList<ZoneModel> mZoneCityList;
    private int businessOrEvent;
    private LocalityTable _table;
    private LocalitiesAdapter _localityExpandAdapter;
    private ArrayList<LocalityModel> tempLocalityList;
    private Fragment parentFragment;
    Context mcontext;
    private Activity parentActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_locality, container, false);
        Utils.pushOpenScreenEvent(getActivity(), "Listing Localities Filter", SharedPrefUtils.getUserDetailModel(getActivity()).getDynamoId() + "");

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        businessOrEvent = getArguments().getInt(Constants.PAGE_TYPE);
        localityData = new HashMap<ZoneModel, ArrayList<LocalityModel>>();

        ExpandableListView mLocalitiesExpandList = (ExpandableListView) view.findViewById(R.id.localityListView);
        mLocalitiesExpandList.setGroupIndicator(null);
        mLocalitiesExpandList.setChildIndicator(null);
        mLocalitiesExpandList.setDividerHeight(0);
        /*// By Deepak Sharma to perform reset task when the ListView scrolling stopped
        mLocalitiesExpandList.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub
				if(scrollState == 0)
				Log.i("a", "scrolling stopped...");
			//	Constants.BACK_PRESS_CONST = 0;
				ifilter.reject(businessOrEvent); 
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub
				Log.i("a", "scrolling...");
			//	Constants.BACK_PRESS_CONST = 1;
			}
		});*/

        final EditText _searchTxt = (EditText) view.findViewById(R.id.txt_search_locality);
        _table = new LocalityTable((BaseApplication) (Context) getActivity().getApplication());
        mZoneCityList = _table.getZoneModel();
        for (ZoneModel _zoneModel : mZoneCityList) {
            ArrayList<LocalityModel> localityList = new ArrayList<LocalityModel>();
            LocalityModel all = new LocalityModel();
            all.setLocalityId(0);
            all.setLocalityName("All");
            localityList.add(all);
            localityList.addAll(_table.getLocalityMoedel(_zoneModel.getZoneId()));
            localityData.put(_zoneModel, localityList);
        }
        _localityExpandAdapter = new LocalitiesAdapter(getActivity(), parentFragment);
        _localityExpandAdapter.setLocalityData(mZoneCityList, localityData);
        mLocalitiesExpandList.setAdapter(_localityExpandAdapter);
        ((TextView) view.findViewById(R.id.list_reset)).setOnClickListener(this);
        ((TextView) view.findViewById(R.id.list_apply)).setOnClickListener(this);
        ((TextView) view.findViewById(R.id.cancel)).setOnClickListener(this);
        _searchTxt.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = _searchTxt.getText().toString().trim();
                if (text != null && text != "") {
                    //		search(text);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
		/*	 final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		    imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);*/


    }

    public void setAction(Fragment parentFragment) {
        ifilter = (IFilter) parentFragment;
        this.parentFragment = parentFragment;
    }

    public void setAction(Context mcontext) {
        ifilter = (IFilter) mcontext;
        this.mcontext = mcontext;
    }

    @Override
    public void onClick(View arg0) {
        switch (arg0.getId()) {
            case R.id.list_apply:
                manageSort();
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

    private void search(String text) {

        if (text.length() != 0) {

            ArrayList<ZoneModel> temp_zone_model = new ArrayList<ZoneModel>();
            if (mZoneCityList.size() > 0) {
                for (int i = 0; i < mZoneCityList.size(); i++) {
                    String temp = mZoneCityList.get(i).getZoneCity();
                    String textWithStartChar = "" + text.charAt(0);
                    if ((temp.toLowerCase()).startsWith(textWithStartChar.toLowerCase())) {
                        temp_zone_model.add(mZoneCityList.get(i));
                    }
                }
            }
            bindList(text, temp_zone_model);
			/*adapter = new GroupDetailsListAdapter(GroupDetails.this,
					temp_myGroupsInfo, gemailId);
			list.setAdapter(adapter);*/
        } else if (text.length() == 0) {
            _localityExpandAdapter.setLocalityData(mZoneCityList, localityData);
            _localityExpandAdapter.notifyDataSetChanged();
        }

    }

    private void bindList(String text, ArrayList<ZoneModel> temp_zone_model) {
        HashMap<ZoneModel, ArrayList<LocalityModel>> localityData = new HashMap<ZoneModel, ArrayList<LocalityModel>>();
        tempLocalityList = new ArrayList<LocalityModel>();
        for (ZoneModel _zoneModel : temp_zone_model) {
            ArrayList<LocalityModel> localityList = new ArrayList<LocalityModel>();

            LocalityModel all = new LocalityModel();
            all.setLocalityId(0);
            all.setLocalityName("All");
            localityList.add(all);
            localityList.addAll(_table.getLocalityMoedel(_zoneModel.getZoneId()));
            if (localityList.size() > 0) {
                for (LocalityModel model : localityList) {
                    String temp = model.getLocalityName();
                    if ((temp.toLowerCase()).startsWith(text.toLowerCase())) {
                        tempLocalityList.add(model);
                    }
                }
            }

            localityData.put(_zoneModel, tempLocalityList);
        }
        _localityExpandAdapter.setLocalityData(temp_zone_model, localityData);
        _localityExpandAdapter.notifyDataSetChanged();
    }

	/*
	private void manageSort() {
		ArrayList<LocalityModel> models = new ArrayList<LocalityModel>() ; 
		for(ZoneModel model : mZoneCityList) {
			ArrayList<LocalityModel> models1 = localityData.get(model) ;
			for(LocalityModel myModel : models1) {
				if(model.isSelected()){
					myModel.setZoneId(model.getZoneId());
					myModel.setZoneName(model.getZoneCity());
				}

				if(myModel.isSelected() && ! myModel.getLocalityName().toLowerCase().equals("all")) {
					models.add(myModel) ;
				}
			}
		}

		if(tempLocalityList!=null){
			ifilter.doFilter(FilterType.Locality, tempLocalityList,businessOrEvent) ; 
		}else if((models!=null) && !(models.isEmpty())){
			ifilter.doFilter(FilterType.Locality, models,businessOrEvent) ; 
		}else{
			Toast.makeText(getActivity(), getResources().getString(R.string.please_select), Toast.LENGTH_SHORT).show();
		}
	}
	 */

    private void manageSort() {

        try {
            HashMap<MapTypeFilter, String> filterMap;
            if (parentFragment == null) {
                // kids resources flow
                filterMap = ((BusinessListActivityKidsResources) getActivity()).mFilterMap;
            } else {
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
            Log.i("locality", e.getMessage());
            //e.printStackTrace();
        }

		/*HashMap<String, String>   filterMap  =	((BusinessListActivity) getActivity()).mFilterMap;
		ifilter.doFilter(FilterType.Locality, filterMap,businessOrEvent);*/
		/*	int groupPosition = _localityExpandAdapter.getGroupPosition();

		if( groupPosition == -1 ) {
			Toast.makeText(getActivity(), getResources().getString(R.string.please_select), Toast.LENGTH_SHORT).show();
		} else {
			ZoneModel zoneModel = mZoneCityList.get(groupPosition); 
			ArrayList<LocalityModel> allLocalitiesArrayList = localityData.get(zoneModel);
			ArrayList<LocalityModel> selectedLocalitiesArrayList = new ArrayList<LocalityModel>();

			for( LocalityModel localityModel : allLocalitiesArrayList ) {
				if(zoneModel.isSelected()){
					localityModel.setZoneId(zoneModel.getZoneId());
					localityModel.setZoneName(zoneModel.getZoneCity());
				}
				if( localityModel.isSelected()) {
					selectedLocalitiesArrayList.add(localityModel);
				}
			}

			if(selectedLocalitiesArrayList != null ) {
				String locality = "" ; 
				int zoneId=0;
				  zoneId=selectedLocalitiesArrayList.get(0).getZoneId();
				for(int i = 0 ; i < selectedLocalitiesArrayList.size() ; i++) {

					locality += selectedLocalitiesArrayList.get(i).getLocalityId() + "," ; 
				}

			String localityFilter=locality.substring(0 , locality.length() - 1 ) ;
			filterMap.put("Locality", "&locality_id="+localityFilter);
			}*/
        //	ifilter.doFilter(FilterType.Locality, filterMap,businessOrEvent);
        //ifilter.doFilter(FilterType.Locality, selectedLocalitiesArrayList, businessOrEvent) ;
        //	}

    }


    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        businessOrEvent = args.getInt(Constants.PAGE_TYPE);
    }
}

