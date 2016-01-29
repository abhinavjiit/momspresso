package com.mycity4kids.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.mycity4kids.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hemant on 22/1/16.
 */
public class ListFamilyInvitesActivity extends BaseActivity {

    ListView listView;
    TextView createFamilyTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.invite_layout);
        listView = (ListView) findViewById(R.id.listView);
        View header = getLayoutInflater().inflate(R.layout.invite_list_header, null);
        createFamilyTextView = (TextView)header.findViewById(R.id.createFamilyTextView);
        listView.addHeaderView(header, null, false);
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            list.add("item_" + i);
        }
        TestAdapter adapter = new TestAdapter(this, R.layout.invite_item, list);
        listView.setAdapter(adapter);

        createFamilyTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    public static class TestAdapter extends ArrayAdapter<String> {

        List<String> list;
        Context mContext;
        LayoutInflater mInflater;

        public TestAdapter(Context context, int resource, List<String> objects) {
            super(context, resource, objects);
            mContext = context;
            list = objects;
            mInflater = LayoutInflater.from(context);
        }


        static class ViewHolder {
            TextView tv;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.invite_item, parent, false);
                holder = new ViewHolder();
                holder.tv = (TextView) convertView.findViewById(R.id.inviteeName);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.tv.setText(list.get(position));
            return convertView;
        }
    }

    @Override
    protected void updateUi(Response response) {

    }
}
