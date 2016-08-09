package com.mycity4kids.ui.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.mycity4kids.R;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.models.Topics;
import com.mycity4kids.widget.TopicsExpandableListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class AddTopicsParentExpandableListAdapter extends BaseExpandableListAdapter implements AddTopicsChildExpandableListAdapter.TotalSelectedItems {

    private Context context;
    ArrayList<Topics> topicList;
    HashMap<Topics, List<Topics>> map;
    private LayoutInflater inflater;
    private ExpandableListView topExpList;
    private TopicsExpandableListView listViewCache[];
    private static final String LOG_TAG = "ParentExpandableAdapter";
    AddTopicsChildExpandableListAdapter adapter;
    private int totalSelectedItems;

    public AddTopicsParentExpandableListAdapter(Context context,
                                                ExpandableListView topExpList,
                                                ArrayList<Topics> topicList,
                                                HashMap<Topics, List<Topics>> map) {
        this.context = context;
        this.topExpList = topExpList;
        this.topicList = topicList;
        this.map = map;
        inflater = LayoutInflater.from(context);
        listViewCache = new TopicsExpandableListView[topicList.size()];
    }

    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        View v = null;
        if (convertView != null)
            v = convertView;
        else
            v = inflater.inflate(R.layout.topics_parent_item, parent, false);
        Topics gt = (Topics) getGroup(groupPosition);
        TextView parentTopicName = (TextView) v.findViewById(R.id.parentTopicName);
        ImageView groupCheckedTxv = (ImageView) v.findViewById(R.id.plus_minus_topic);
        if (gt != null)
            parentTopicName.setText(gt.getTitle());

        if (isExpanded) {
            groupCheckedTxv.setImageResource(R.drawable.uparrow);
        } else {
            groupCheckedTxv.setImageResource(R.drawable.downarrow);
        }
        return v;
    }

    public Object getGroup(int groupPosition) {
        return topicList.get(groupPosition);
    }

    public int getGroupCount() {
        return topicList.size();
    }

    public long getGroupId(int groupPosition) {
        return (long) (groupPosition);  // To be consistent with getChildId
    }

    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        View v = null;
        if (listViewCache[groupPosition] != null)
            v = listViewCache[groupPosition];
        else {
            TopicsExpandableListView dev = new TopicsExpandableListView(context);
            dev.setRows(calculateRowCount(groupPosition, null));
            adapter = new AddTopicsChildExpandableListAdapter(context);
            adapter.setTopicsData(createGroupList(groupPosition), createChildList(groupPosition), this);
            dev.setAdapter(adapter);
            dev.setOnGroupClickListener(new Level2GroupExpandListener(groupPosition));
            listViewCache[groupPosition] = dev;
            v = dev;
        }
        return v;
    }

    public Object getChild(int groupPosition, int childPosition) {
        return map.get(topicList.get(groupPosition)).get(childPosition);
    }

    public long getChildId(int groupPosition, int childPosition) {
        return (long) (groupPosition + childPosition);  // Max 1024 children per group
    }

    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    public boolean hasStableIds() {
        return true;
    }

    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    /**
     * Creates a level2 group list out of the listdesc array according to
     * the structure required by SimpleExpandableListAdapter. The resulting
     * List contains Maps. Each Map contains one entry with key "colorName" and
     * value of an entry in the listdesc array.
     *
     * @param level1 Index of the level1 group whose level2 subgroups are listed.
     */
    private List createGroupList(int level1) {
        ArrayList<Topics> result = new ArrayList();

        for (int i = 0; i < map.get(topicList.get(level1)).size(); ++i) {
            result.add(map.get(topicList.get(level1)).get(i));
        }
        return (List) result;
    }

    /**
     * Creates the child list out of the listdesc array according to the
     * structure required by SimpleExpandableListAdapter. The resulting List
     * contains one list for each group. Each such second-level group contains
     * Maps. Each such Map contains two keys: "shadeName" is the name of the
     * shade and "rgb" is the RGB value for the shade.
     *
     * @param level1 Index of the level1 group whose level2 subgroups are included in the child list.
     */
    private HashMap createChildList(int level1) {
        HashMap child = new HashMap();
        ArrayList result = new ArrayList();
        for (int i = 0; i < map.get(topicList.get(level1)).size(); ++i) {
            // Second-level lists
            ArrayList secList = new ArrayList();
            if (map.get(topicList.get(level1)).get(i).getChild().size() == 0) {
                child.put(map.get(topicList.get(level1)).get(i), map.get(topicList.get(level1)).get(i).getChild());
                secList.add(child);
            }
            for (int n = 0; n < map.get(topicList.get(level1)).get(i).getChild().size(); ++n) {
                child.put(map.get(topicList.get(level1)).get(i), map.get(topicList.get(level1)).get(i).getChild());
                secList.add(child);
            }
            result.add(secList);
        }

        return child;
    }

    // Calculates the row count for a level1 expandable list adapter. Each level2 group counts 1 row (group row) plus any child row that
    // belongs to the group
    private int calculateRowCount(int level1, ExpandableListView level2view) {
        int level2GroupCount = map.get(topicList.get(level1)).size();
        int rowCtr = 0;
        for (int i = 0; i < level2GroupCount; ++i) {
            ++rowCtr;       // for the group row
            if ((level2view != null) && (level2view.isGroupExpanded(i)))
                rowCtr += map.get(topicList.get(level1)).get(i).getChild().size();    // then add the children too (minus the group descriptor)
        }
        return rowCtr;
    }

    class Level2GroupExpandListener implements ExpandableListView.OnGroupClickListener {
        private int level1GroupPosition;

        public Level2GroupExpandListener(int level1GroupPosition) {
            this.level1GroupPosition = level1GroupPosition;
        }

        public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
            if (parent.isGroupExpanded(groupPosition))
                parent.collapseGroup(groupPosition);
            else
                parent.expandGroup(groupPosition);
            if (parent instanceof TopicsExpandableListView) {
                TopicsExpandableListView dev = (TopicsExpandableListView) parent;
                dev.setRows(calculateRowCount(level1GroupPosition, parent));
            }
            Log.d(LOG_TAG, "onGroupClick");
            topExpList.requestLayout();
            return true;
        }

    }


    /*
    * WHILE PUBLISHING A NEW BLOG.
    * When returning to expandable listview screen from selected topics screen
    * update the list with remaining selected topics
    * */
    public ArrayList<Topics> counttotalSelected() {
        totalSelectedItems = 0;
        ArrayList<Topics> list = new ArrayList<>();

        Iterator it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            ArrayList<Topics> tList = ((ArrayList) pair.getValue());

            for (int j = 0; j < tList.size(); j++) {

                //subcategories with no child
                if (tList.get(j).getChild().size() == 0 && tList.get(j).isSelected()) {
                    list.add(tList.get(j));
                    totalSelectedItems++;
                }

                //subcategories children
                for (int k = 0; k < tList.get(j).getChild().size(); k++) {
                    if (tList.get(j).getChild().get(k).isSelected()) {
                        list.add(tList.get(j).getChild().get(k));
                        totalSelectedItems++;

                    }
                }
            }
        }

        return list;
    }


    /*
    * WHILE PUBLISHING A NEW BLOG.
    * When returning to expandable listview screen from selected topics screen
    * notify all child adapters to update the data.
    * */
    public void updateChildAdapter() {
        if (null != adapter)
            adapter.notifyDataSetChanged();

        for (int i = 0; i < listViewCache.length; i++) {
            if (null != listViewCache[i])
                ((AddTopicsChildExpandableListAdapter) listViewCache[i].getExpandableListAdapter()).notifyDataSetChanged();
        }
    }


    /*
    * returns a list of Selected Topics.
    * Used only when Publishing a new Article.
    * */
    public ArrayList<Topics> getAllSelectedElements() {
        ArrayList<Topics> list = new ArrayList<>();

        for (int i = 0; i < listViewCache.length; i++) {
            if (null != listViewCache[i]) {
                HashMap<Topics, List<Topics>> selectedTopics =
                        ((AddTopicsChildExpandableListAdapter) listViewCache[i].getExpandableListAdapter()).getUpdatedMap();
                Iterator it = selectedTopics.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry pair = (Map.Entry) it.next();
                    ArrayList<Topics> tList = ((ArrayList) pair.getValue());

                    //Include subcategories with no child.
                    if (tList.size() == 0 && !(((Topics) pair.getKey()).getId().equals("-1")) && ((Topics) pair.getKey()).isSelected()) {
                        list.add((Topics) pair.getKey());
                    }
                    //subcategories children
                    for (int j = 0; j < tList.size(); j++) {
                        if (tList.get(j).isSelected()) {
                            list.add(tList.get(j));
                        }
                    }
                }
            }
        }
        return list;
    }

    /*
     * called when a checkbox state changes inside the child adapter
     * to update the total items selected
     * */
    @Override
    public boolean updateTotalSelectedItems(boolean status) {
        if (status) {
            totalSelectedItems++;
        } else {
            totalSelectedItems--;
        }
        if (totalSelectedItems > Constants.MAX_ARTICLE_CATEGORIES) {
            totalSelectedItems--;
            return true;
        }
        return false;
    }

    public void setTotalSelectedItem(int totalSelectedItem) {
        totalSelectedItems = totalSelectedItem;
    }

    public int getTotalSelectedItem() {
        return totalSelectedItems;
    }
}