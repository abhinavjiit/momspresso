package com.mycity4kids.ui.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mycity4kids.R;
import com.mycity4kids.enums.MapTypeFilter;
import com.mycity4kids.models.locality.LocalityModel;
import com.mycity4kids.models.locality.ZoneModel;
import com.mycity4kids.ui.activity.BusinessListActivityKidsResources;
import com.mycity4kids.ui.fragment.FragmentBusinesslistEvents;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @edited sachin.gupta
 */
public class LocalitiesAdapter extends BaseExpandableListAdapter {

    private LayoutInflater mInflator;
    private ArrayList<ZoneModel> mZoneCityList;
    private HashMap<ZoneModel, ArrayList<LocalityModel>> mLocalityData;
    private HashMap<MapTypeFilter, String> mFilterMap;
    private int mGroupPosition;
    private Context mContext;

    /**
     * @return the mGroupPosition
     */
    public int getGroupPosition() {
        return mGroupPosition;
    }

    /**
     * @param pContext
     */
    public LocalitiesAdapter(Context pContext, Fragment fragment) {
        mInflator = LayoutInflater.from(pContext);
       // mFilterMap = ((FragmentBusinesslistEvents) fragment).mFilterMap;
        if (fragment == null){
            mFilterMap = ((BusinessListActivityKidsResources)pContext).mFilterMap;
        } else {
            mFilterMap = ((FragmentBusinesslistEvents) fragment).mFilterMap;
        }
        mGroupPosition = -1;
        mContext = pContext;
    }

    /**
     * @param pZoneCityList
     * @param pLocalityData
     */
    public void setLocalityData(ArrayList<ZoneModel> pZoneCityList, HashMap<ZoneModel, ArrayList<LocalityModel>> pLocalityData) {
        mZoneCityList = pZoneCityList;
        mLocalityData = pLocalityData;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        LocalityModel localityChild = mLocalityData.get(this.mZoneCityList.get(groupPosition)).get(childPosition);
        return localityChild;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflator.inflate(R.layout.child_item_localities, null);
            holder.childChkBox = (CheckBox) convertView.findViewById(R.id.childChkBox);
            holder.txtLocalityName = (TextView) convertView.findViewById(R.id.localityName);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        LocalityModel localityModel = (LocalityModel) getChild(groupPosition, childPosition);

        if (childPosition != 0)
            holder.txtLocalityName.setText(localityModel.getLocalityName());
        else {
            int count = mLocalityData.get((ZoneModel) getGroup(groupPosition)).size() - 1;
            holder.txtLocalityName.setText(localityModel.getLocalityName() + " (" + count + ")");
        }

        holder.childChkBox.setChecked(localityModel.isSelected());
        holder.childChkBox.setTag(new Positions(groupPosition, childPosition));

        holder.childChkBox.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                handleChildCheckboxClick((CheckBox) v);
            }
        });
        return convertView;
    }


    @Override
    public int getChildrenCount(int groupPosition) {
        return mLocalityData.get(this.mZoneCityList.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mZoneCityList.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return mZoneCityList.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflator.inflate(R.layout.group_item_localities, null);
            //holder.groupChkBox=(CheckBox)convertView.findViewById(R.id.checkBox);
            holder.txvZoneName = (TextView) convertView.findViewById(R.id.localityZoneName);
            holder.groupCheckedTxv = (ImageView) convertView.findViewById(R.id.plus_minus_locality);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final ZoneModel _zoneData = (ZoneModel) getGroup(groupPosition);

        int count = mLocalityData.get(_zoneData).size() - 1;
        holder.txvZoneName.setText(_zoneData.getZoneCity() + " (" + count + ")");
        if (isExpanded){
            holder.groupCheckedTxv.setImageResource(R.drawable.uparrow);
        } else {
            holder.groupCheckedTxv.setImageResource(R.drawable.downarrow);
        }
//        holder.groupCheckedTxv.setChecked(isExpanded);
        //holder.groupChkBox.setChecked(_zoneData.isSelected());
        //holder.groupChkBox.setTag(groupPosition);
        //	holder.txvZoneName.setTag(groupPosition);
        /**
         * CR
         */
        //	handleGroupCheckboxClick(groupPosition);
        /*holder.txvZoneName.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				handleGroupCheckboxClick((TextView)v);

			}
		});*/

		/*holder.groupChkBox.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				handleGroupCheckboxClick((CheckBox)v);
			}
		});*/
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    class ViewHolder {
        TextView txvZoneName;
        TextView txtLocalityName;
        CheckBox childChkBox;
        CheckBox groupChkBox;
        ImageView groupCheckedTxv;
    }

    class Positions {
        int groupPosition;
        int childPosition;

        Positions(int groupPosition, int childPosition) {
            this.groupPosition = groupPosition;
            this.childPosition = childPosition;
        }
    }

    /**
     * @param state
     * @param groupPosition-comment due to CR
     */
	/*private void handleGroupCheckboxClick( CheckBox pGroupCheckBox ) {
		clearPreviousGroupSelection();
		boolean newState = pGroupCheckBox.isChecked();
		if( newState ) {
			mGroupPosition = (Integer)pGroupCheckBox.getTag();
			ZoneModel zoneModel = (ZoneModel)getGroup(mGroupPosition);
			zoneModel.setSelected(newState);
			setChildrenState(zoneModel, newState);
		}
		notifyDataSetChanged();
	}*/

    /**
     * @param pPositon
     */
//    private void handleGroupCheckboxClick(int pPositon) {
//        //	clearPreviousGroupSelection();
//        boolean newState = true;
//        if (newState) {
//            mGroupPosition = pPositon;
//            ZoneModel zoneModel = (ZoneModel) getGroup(mGroupPosition);
//            zoneModel.setSelected(newState);
//            //	setChildrenState(zoneModel, newState);//now checkbox will not be selected:
//        }
//        notifyDataSetChanged();
//        addOrRemoveFilters();
//    }

    /**
     * @param pChildCheckBox
     */
    private void handleChildCheckboxClick(CheckBox pChildCheckBox) {
        boolean isChecked = pChildCheckBox.isChecked();
        int groupPos = ((Positions) pChildCheckBox.getTag()).groupPosition;
        int childPos = ((Positions) pChildCheckBox.getTag()).childPosition;

        ZoneModel zoneModel = (ZoneModel) getGroup(groupPos);
        /**
         * select & unselect for all checkbox;
         */
        if (childPos == 0) {
            zoneModel.setSelected(isChecked);
            setChildrenState(zoneModel, isChecked);
        } else {
            LocalityModel localityModel = (LocalityModel) getChild(groupPos, childPos);
            localityModel.setSelected(isChecked);
        }
        if (isChecked) {
            zoneModel.setSelected(isChecked);

            if (mGroupPosition != groupPos) {
                clearPreviousGroupSelection();
                mGroupPosition = groupPos;
            }
            if (childPos != 0 && areAllChecked(zoneModel)) {
                LocalityModel AllModel = (LocalityModel) getChild(groupPos, 0);
                AllModel.setSelected(isChecked);
            }
        } else {
            LocalityModel AllModel = (LocalityModel) getChild(groupPos, 0);
            AllModel.setSelected(isChecked);
            if (childPos != 0 && isNoneChecked(zoneModel)) {
                zoneModel.setSelected(isChecked);
            }
        }
        notifyDataSetChanged();

        addOrRemoveFilters();
    }

    /**
     * clear previous selection
     */
    private void clearPreviousGroupSelection() {
        if (mGroupPosition == -1) {
            return;
        }
        ZoneModel prevZoneModel = (ZoneModel) getGroup(mGroupPosition);
        prevZoneModel.setSelected(false);
        setChildrenState(prevZoneModel, false);
        mGroupPosition = -1;
    }

    /**
     * @param zoneModel
     * @param newState
     */
    private void setChildrenState(ZoneModel zoneModel, boolean newState) {
        ArrayList<LocalityModel> models = mLocalityData.get(zoneModel);
        for (int i = 0; i < models.size(); i++) {
            models.get(i).setSelected(newState);
        }
    }

    /**
     * @param zoneModel
     * @return
     */
    private boolean areAllChecked(ZoneModel zoneModel) {
        ArrayList<LocalityModel> models = mLocalityData.get(zoneModel);
        for (int i = 1; i < models.size(); i++) {
            if (!models.get(i).isSelected()) {
                return false;
            }
        }
        return true;
    }

    /**
     * @param zoneModel
     * @return
     */
    private boolean isNoneChecked(ZoneModel zoneModel) {
        ArrayList<LocalityModel> models = mLocalityData.get(zoneModel);
        for (int i = 1; i < models.size(); i++) {
            if (models.get(i).isSelected()) {
                return false;
            }
        }
        return true;
    }


    private void addOrRemoveFilters() {
        mFilterMap.remove(MapTypeFilter.Locality);
        if (mGroupPosition == -1) {
            Toast.makeText(mContext, mContext.getResources().getString(R.string.please_select), Toast.LENGTH_SHORT).show();
        } else {
            ZoneModel zoneModel = mZoneCityList.get(mGroupPosition);
            ArrayList<LocalityModel> allLocalitiesArrayList = mLocalityData.get(zoneModel);
            ArrayList<LocalityModel> selectedLocalitiesArrayList = new ArrayList<LocalityModel>();

            for (LocalityModel localityModel : allLocalitiesArrayList) {
                if (zoneModel.isSelected()) {
                    localityModel.setZoneId(zoneModel.getZoneId());
                    localityModel.setZoneName(zoneModel.getZoneCity());
                }
                if (localityModel.isSelected()) {
                    selectedLocalitiesArrayList.add(localityModel);
                }
            }
   if (selectedLocalitiesArrayList != null && selectedLocalitiesArrayList.size() > 0) {

                String locality = "";
                int zoneId = 0;
                zoneId = selectedLocalitiesArrayList.get(0).getZoneId();
                String localityName = selectedLocalitiesArrayList.get(0).getLocalityName();
                localityName.toLowerCase();
                if (localityName.equalsIgnoreCase("All")) {
                    mFilterMap.put(MapTypeFilter.Locality, "&zone_id=" + zoneId + "&locality_id=" + localityName.toLowerCase());

                    try {
                        // remove from list
                        (FragmentBusinesslistEvents.mFilterMap).remove(MapTypeFilter.Locality);
                        (FragmentBusinesslistEvents.mFilterMap).put(MapTypeFilter.Locality, "&zone_id=" + zoneId + "&locality_id=" + localityName.toLowerCase());
                    }
                    catch(Exception e)
                    {
                        e.printStackTrace();
                    }


                } else {
                    for (int i = 0; i < selectedLocalitiesArrayList.size(); i++) {
                        if (selectedLocalitiesArrayList.get(i).getLocalityId() != 0)
                            locality += selectedLocalitiesArrayList.get(i).getLocalityId() + ",";
                    }

                    String localityFilter = locality.substring(0, locality.length() - 1);
                    mFilterMap.put(MapTypeFilter.Locality, "&zone_id=" + zoneId + "&locality_id=" + localityFilter);

                    try {
                        // remove from list
                        (FragmentBusinesslistEvents.mFilterMap).remove(MapTypeFilter.Locality);
                        (FragmentBusinesslistEvents.mFilterMap).put(MapTypeFilter.Locality, "&zone_id=" + zoneId + "&locality_id=" + localityFilter);
                    }
                    catch(Exception e)
                    {
                        e.printStackTrace();
                    }

                }
            }
        }
    }
}
