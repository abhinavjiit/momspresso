package com.mycity4kids.ui.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.dbtable.TableKids;
import com.mycity4kids.enums.MapTypeFilter;
import com.mycity4kids.models.category.AgeGroup;
import com.mycity4kids.models.user.KidsInfo;
import com.mycity4kids.ui.activity.BusinessListActivityKidsResources;
import com.mycity4kids.ui.fragment.FragmentBusinesslistEvents;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

public class AgeGroupAdapter extends BaseAdapter {
    private ArrayList<AgeGroup> mAgeGroupList;
    private LayoutInflater mInflator;
    private CheckBox mSelectedRB;
    private int mSelectedPosition = -1;
    private boolean mIsComeFromSearch;
    private HashMap<MapTypeFilter, String> mFilterMap;
    ViewHolder holder;
    boolean isviewinflatefirst = false;
    private ArrayList<KidsInfo> kidsInformations;
    ArrayList<Integer> agelist;
    private    HashSet<String> selectedageGroups;

    public AgeGroupAdapter(Context pContext, Fragment fragment, ArrayList<AgeGroup> pAgeGroupList, boolean isComeFromSearch) {
        mInflator = LayoutInflater.from(pContext);
        mAgeGroupList = pAgeGroupList;
        mIsComeFromSearch = isComeFromSearch;
        if (fragment == null) {
            mFilterMap = ((BusinessListActivityKidsResources) pContext).mFilterMap;
        } else {
            mFilterMap = ((FragmentBusinesslistEvents) fragment).mFilterMap;
        }

        setAge();
    }

    public void setAge() {
        TableKids tableKids = new TableKids(BaseApplication.getInstance());
        kidsInformations = new ArrayList<>();
        kidsInformations = tableKids.getAllKids();
        agelist = new ArrayList<>();

        selectedageGroups = new HashSet<>();

        if (!Constants.IS_RESET) {
            for (int i = 0; i < kidsInformations.size(); i++) {
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                Date startDate;
                try {
                    startDate = df.parse(kidsInformations.get(i).getDate_of_birth());
                    int age = getAge(startDate);
                    agelist.add(age);

                    selectedageGroups.add(""+age);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public int getCount() {
        return mAgeGroupList == null ? 0 : ((mAgeGroupList.size()));
    }

    @Override
    public Object getItem(int position) {
        return mAgeGroupList.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {

        if (view == null) {
            view = mInflator.inflate(R.layout.list_item_age_group, null);
            holder = new ViewHolder();
            holder.mAgeGroupName = (TextView) view.findViewById(R.id.txvAgeGroupName);
            holder.radioBtn = (CheckBox) view.findViewById(R.id.radioBtnAgeGroup);
            holder.dots = (ImageView) view.findViewById(R.id.colordots);
            view.setTag(holder);

            if (position == 0) {
                holder.dots.setBackgroundResource(R.drawable.eventcolor1);
            } else if (position == 1) {
                holder.dots.setBackgroundResource(R.drawable.eventcolor2);
                for (int i = 0; i < agelist.size(); i++) {
                    if ((agelist.get(i) >= 0) && (agelist.get(i) < 2)) {
                        mAgeGroupList.get(position).setSelected(true);
                    }
                }
            } else if (position == 2) {
                holder.dots.setBackgroundResource(R.drawable.eventcolor3);
                for (int i = 0; i < agelist.size(); i++) {
                    if ((agelist.get(i) >= 2) && (agelist.get(i) < 4)) {
                        mAgeGroupList.get(position).setSelected(true);
                    }
                }
            } else if (position == 3) {
                holder.dots.setBackgroundResource(R.drawable.eventcolor4);

                for (int i = 0; i < agelist.size(); i++) {
                    if ((agelist.get(i) >= 4) && (agelist.get(i) < 6)) {
                        mAgeGroupList.get(position).setSelected(true);
                    }
                }
            } else if (position == 4) {
                holder.dots.setBackgroundResource(R.drawable.eventcolor5);
                for (int i = 0; i < agelist.size(); i++) {
                    if ((agelist.get(i) >= 6) && (agelist.get(i) < 10)) {
                        mAgeGroupList.get(position).setSelected(true);
                    }
                }
            } else if (position == 5) {
                holder.dots.setBackgroundResource(R.drawable.eventcolor6);

                for (int i = 0; i < agelist.size(); i++) {
                    if ((agelist.get(i) >= 10) && (agelist.get(i) < 14)) {
                        mAgeGroupList.get(position).setSelected(true);
                    }
                }
            }
        } else {
            holder = (ViewHolder) view.getTag();
        }
        /*if(mIsComeFromSearch){
            holder.mAgeGroupName.setText(mAgeGroupList.get(position).getKey()+" ("+mAgeGroupList.get(position).getValue()+")");
		}else{*/

        /*if(position==0)
        {
            holder.dots.setBackgroundResource(R.drawable.eventcolor1);
            holder.mAgeGroupName.setText("All Age Group");
        }*/

      /*  Matcher m = Pattern.compile("\\(([^)]+)\\)").matcher(mAgeGroupList.get(position).getValue());
        while(m.find()) {
            Log.d("check", "check" + m.group(1));
        }*/
        //  String s[]=mAgeGroupList.get(position).getValue().split("'('");
        holder.mAgeGroupName.setText(mAgeGroupList.get(position).getValue());
        holder.radioBtn.setChecked(mAgeGroupList.get(position).isSelected());
        Log.d("check", "check key " + mAgeGroupList.get(1).getKey() + " val " + mAgeGroupList.get(1).getValue());
        //}


        //notifyDataSetChanged();
        addOrRemoveFilters(mAgeGroupList);

        holder.radioBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                CheckBox chkBok = (CheckBox) v;
                /*if(chkBok.isChecked())
                {
                    holder.radioBtn.setChecked(false);
                }
                else
                {
                    holder.radioBtn.setChecked(true);
                }*/
                mAgeGroupList.get(position).setSelected(chkBok.isChecked());
                if (position == 0) {
                    for (int i = 0; i < mAgeGroupList.size(); i++) {
                        if (!chkBok.isChecked())
                            mAgeGroupList.get(i).setSelected(false);
                        else
                            mAgeGroupList.get(i).setSelected(true);
                    }
                    notifyDataSetChanged();
                } else {
                    for (AgeGroup activities : mAgeGroupList) {

                        if (activities.equals(mAgeGroupList.get(position))) {
                            if (!chkBok.isChecked())
                                activities.setSelected(false);
                            else
                                activities.setSelected(true);
                            activities.setSelected(mAgeGroupList.get(position).isSelected());
                            break;
                        }

                    }
                }
                notifyDataSetChanged();
               /* AgeGroup activity=(AgeGroup)chkBok.getTag();
                activity.setSelected(chkBok.isChecked());
                notifyDataSetChanged();*/
                Log.d("check", "check mAgeGroupList size " + mAgeGroupList);
                addOrRemoveFilters(mAgeGroupList);
            }
        });
       /* holder.radioBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (position != mSelectedPosition && mSelectedRB != null) {
                    mSelectedRB.setChecked(false);
                    mAgeGroupList.get(mSelectedPosition).setSelected(false);
                }
                mSelectedPosition = position;
                mSelectedRB = (CheckBox) v;
                if (mSelectedRB.isChecked()) {
                    mAgeGroupList.get(mSelectedPosition).setSelected(true);
                    Log.d("check","check set");
                }

                addOrRemoveFilters(mAgeGroupList);

            }
        });*/
        /*if (mSelectedPosition != position) {
            holder.radioBtn.setChecked(false);
        }
        else {
            holder.radioBtn.setChecked(true);
            if (mSelectedRB != null && holder.radioBtn != mSelectedRB) {
                mSelectedRB = holder.radioBtn;
            }
        }*/
        /*if(mAgeGroupList.get(mSelectedPosition).isSelected())
        {
            holder.radioBtn.setChecked(true);
        }
        else
        {
            holder.radioBtn.setChecked(false);
        }*/
        return view;
    }

    public int getAge(Date dateOfBirth) {
        int age = 0;

        Calendar born = Calendar.getInstance();
        Calendar now = Calendar.getInstance();

        if (dateOfBirth != null) {
            now.setTime(new Date());
            born.setTime(dateOfBirth);
            if (now.get(Calendar.YEAR) == born.get(Calendar.YEAR)) {
                age = 0;
            } else if (now.get(Calendar.YEAR) > born.get(Calendar.YEAR)) {
                if (now.get(Calendar.MONTH) >= born.get(Calendar.MONTH) && now.get(Calendar.DAY_OF_MONTH) >= born.get(Calendar.DAY_OF_MONTH)) {
                    age = now.get(Calendar.YEAR) - born.get(Calendar.YEAR);
                } else {
                    age = now.get(Calendar.YEAR) - born.get(Calendar.YEAR);
                    age = age - 1;
                }
            }
        }

        return age;
    }

    class ViewHolder {
        TextView mAgeGroupName;
        CheckBox radioBtn;
        ImageView dots;


    }

    private void addOrRemoveFilters(ArrayList<AgeGroup> mAgeGroupList) {
        mFilterMap.remove(MapTypeFilter.AgeGroup);
        HashSet<String> selected_filters = new HashSet<String>();
        for (int i = 0; i < mAgeGroupList.size(); i++) {
            AgeGroup category = mAgeGroupList.get(i);
            if (category.isSelected()) {
                selected_filters.add(category.getKey());
            }
        }

        if ((selected_filters != null) && !(selected_filters.isEmpty())) {

            if (selected_filters != null) {
                String ageGr = "";
                Object[] myArr = selected_filters.toArray();
                for (int i = 0; i < myArr.length; i++) {
                    ageGr += myArr[i].toString() + ",";
                }
                String finalString[] = ageGr.split(",");
                String agegoupstr = "&age_group[]=";
                String finalstrdata = "";
                for (int i = 0; i < finalString.length; i++) {
                    finalstrdata = finalstrdata + agegoupstr + finalString[i];
                }

                // now add age also
                Object[] ageObj = selectedageGroups.toArray();

                String ageString = "&age[]=";

                boolean flag = false;
                for (int i = 0; i < ageObj.length; i++) {

                    if (flag) {
                        ageString = ageString + "&age[]=";
                    }

                    ageString = ageString + ageObj[i].toString();
                    flag = true;

                }


                finalstrdata = finalstrdata + ageString;

                mFilterMap.put(MapTypeFilter.AgeGroup, finalstrdata);

                try {
                    (FragmentBusinesslistEvents.mFilterMap).remove(MapTypeFilter.AgeGroup);
                    (FragmentBusinesslistEvents.mFilterMap).put(MapTypeFilter.AgeGroup, finalstrdata);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    }

}
