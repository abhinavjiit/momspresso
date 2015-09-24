package com.mycity4kids.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mycity4kids.R;
import com.mycity4kids.newmodels.AppointmentMappingModel;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AdapterExpandableList extends BaseExpandableListAdapter {

    Boolean flag = false;
    Calendar calendar;
    private Context _context;
    private List<String> _listDataHeader; // header titles
    // child data in format of header title, child title
    private LinkedHashMap<String, ArrayList<AppointmentMappingModel>> _listDataChild;

    public AdapterExpandableList(Context context) {
        this._context = context;
    }

    public void setData(List<String> listDataHeader,
                        LinkedHashMap<String, ArrayList<AppointmentMappingModel>> listChildData) {
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;
    }

    @Override
    public int getGroupCount() {
        return this._listDataHeader == null ? 0 : this._listDataHeader.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if (_listDataChild == null) {
            return 0;
        }
        return this._listDataChild.get(this._listDataHeader.get(groupPosition)) == null ? 0 : this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);

        final GroupViewHolder groupHolder;

        LayoutInflater infalInflater = (LayoutInflater) this._context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {

            convertView = infalInflater.inflate(R.layout.aa_cal_list_header, null);
            groupHolder = new GroupViewHolder();

            groupHolder.header = (TextView) convertView.findViewById(R.id.header);
            groupHolder.day = (TextView) convertView.findViewById(R.id.day);
            groupHolder.today = (TextView) convertView.findViewById(R.id.today);

            convertView.setTag(groupHolder);
        } else {
            groupHolder = (GroupViewHolder) convertView.getTag();
        }

        ExpandableListView eLV = (ExpandableListView) parent;
        eLV.expandGroup(groupPosition);

        String tempdate = "2015-07-12";

        DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        Date date = null;
        try {
            date = format.parse(headerTitle);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        System.out.println(date);


        DateFormat df = new SimpleDateFormat("dd MMM", Locale.US);
        DateFormat format2 = new SimpleDateFormat("EEEE");


        if (getTodayDate().equals(headerTitle)) {
            convertView.setBackgroundColor(_context.getResources().getColor(R.color.a_light_pink));
            groupHolder.today.setVisibility(View.VISIBLE);
        } else {
            convertView.setBackgroundColor(_context.getResources().getColor(R.color.a_light_blue));
            groupHolder.today.setVisibility(View.GONE);
        }
        groupHolder.day.setText(format2.format(date).toUpperCase());

        String reportDate = df.format(date);
        groupHolder.header.setText(reportDate.toUpperCase());

        return convertView;
    }

    class GroupViewHolder {
        TextView header;
        TextView day;
        TextView today;
    }


    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        AppointmentMappingModel appointmantItems = (AppointmentMappingModel) getChild(groupPosition, childPosition);

        final ViewHolder holder;

        LayoutInflater infalInflater = (LayoutInflater) this._context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {

            convertView = infalInflater.inflate(R.layout.aa_cal_list_item, null);
            holder = new ViewHolder();

            holder.hasAppontment = (LinearLayout) convertView.findViewById(R.id.appointment_layout);
            holder.noAppontment = (TextView) convertView.findViewById(R.id.no_appointment);
            holder.time = (TextView) convertView.findViewById(R.id.appoint_time);
            holder.title = (TextView) convertView.findViewById(R.id.appoint_title);
            holder.address = (TextView) convertView.findViewById(R.id.appoint_address);
            holder.colorCode = (LinearLayout) convertView.findViewById(R.id.colorCode);
            holder.ifNote = (ImageView) convertView.findViewById(R.id.appointment_notes);
            holder.eventIcon = (ImageView) convertView.findViewById(R.id.event_icon);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        convertView.setVisibility(View.VISIBLE);

        if (appointmantItems.getAppointment_name() == null) {
            holder.hasAppontment.setVisibility(View.GONE);
            holder.noAppontment.setVisibility(View.VISIBLE);
        } else {
            holder.hasAppontment.setVisibility(View.VISIBLE);
            holder.noAppontment.setVisibility(View.GONE);

            LinearLayout.LayoutParams dummyParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0);

            holder.time.setText(getTime(appointmantItems.getStarttime()));
            holder.title.setText(appointmantItems.getAppointment_name());
            holder.address.setText(appointmantItems.getLocality());
            holder.colorCode.removeAllViews();

//            int notesCount = tableNotes.getCountById(appointmantItems.getEventId());

//            if (notesCount > 0) {
            if (appointmantItems.isHasNotes()) {
                holder.ifNote.setVisibility(View.VISIBLE);
            } else {
                holder.ifNote.setVisibility(View.GONE);
            }

            if (appointmantItems.getIs_bday() == 1 || appointmantItems.getIs_holiday() == 1) {
                holder.time.setVisibility(View.GONE);
                holder.eventIcon.setVisibility(View.VISIBLE);

                if (appointmantItems.getIs_holiday() == 1) {
                    holder.eventIcon.setBackgroundResource(R.drawable.holiday);
                } else if (appointmantItems.getIs_bday() == 1) {
                    holder.eventIcon.setBackgroundResource(R.drawable.cake_xxhdpi);
                }
            } else {
                holder.time.setVisibility(View.VISIBLE);
                holder.eventIcon.setVisibility(View.GONE);
            }

            for (int i = 0; i < ((AppointmentMappingModel) getChild(groupPosition, childPosition)).getAttendee().size(); i++) {

                View dummyView = new View(_context);
                dummyView.setLayoutParams(dummyParams);

                try {
                    dummyView.setBackgroundColor(Color.parseColor(appointmantItems.getAttendee().get(i).getColorCode()));
                    dummyParams.weight = 1f;

                    holder.colorCode.addView(dummyView);
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        }

        return convertView;
    }

    class ViewHolder {
        TextView time;
        TextView title;
        TextView address;
        LinearLayout colorCode;
        LinearLayout hasAppontment;
        TextView noAppontment;
        ImageView ifNote;
        ImageView eventIcon;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public String getTodayDate() {

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        System.out.println(cal.getTime());
        String currentDate = format.format(cal.getTime());

        return currentDate;
    }

    public void getcolorCode() {

    }

    public void updateDates(List<String> dayslist, LinkedHashMap<String, ArrayList<AppointmentMappingModel>> itemList) {

        for (int i = 0; i < dayslist.size(); i++) {
            _listDataHeader.add(dayslist.get(i));
        }
        _listDataChild.putAll(itemList);
        notifyDataSetChanged();

    }

    public void updateDatesPrevious(List<String> dayslist, LinkedHashMap<String, ArrayList<AppointmentMappingModel>> itemList) {

        List<String> newList = new ArrayList<>();
        int prvLimit = dayslist.size();
        int totalLimit = dayslist.size() + _listDataHeader.size();

        for (int i = 0; i < prvLimit; i++) {
            newList.add(i, dayslist.get(i));
        }

        int temp = newList.size();
        newList.addAll(temp, _listDataHeader);

        _listDataHeader = newList;
        _listDataChild.putAll(itemList);

        notifyDataSetChanged();

    }

    public List<String> getHeaderList() {
        return _listDataHeader;
    }

    public LinkedHashMap<String, ArrayList<AppointmentMappingModel>> getChildList() {
        return _listDataChild;
    }

    public void searchList(String searchDate) {

        LinkedHashMap<String, ArrayList<AppointmentMappingModel>> newList = new LinkedHashMap<String, ArrayList<AppointmentMappingModel>>();

        newList = this._listDataChild;

        for (Map.Entry<String, ArrayList<AppointmentMappingModel>> entry : newList.entrySet()) {

            String key = entry.getKey();
            ArrayList<AppointmentMappingModel> values = entry.getValue();

            for (int i = 0; i < values.size(); i++) {

                for (int j = 0; j < values.get(i).getAttendee().size(); j++) {

                    if (values.get(i).getAttendee().get(j).getName().equals(searchDate)) {

                    }
                }
            }
        }
    }

    private String getTime(long timeStampStr) {

        try {
            DateFormat sdf = new SimpleDateFormat("h:mma");
            Date netDate = (new Date(timeStampStr));
            return sdf.format(netDate).toUpperCase();
        } catch (Exception ex) {
            return "xx";
        }
    }

    public void notifyDataChange(ArrayList<String> headerData, LinkedHashMap<String, ArrayList<AppointmentMappingModel>> childData) {

        this._listDataHeader = headerData;
        this._listDataChild = childData;

        notifyDataSetChanged();

    }

}
