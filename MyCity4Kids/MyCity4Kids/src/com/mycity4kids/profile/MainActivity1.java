package com.mycity4kids.profile;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mycity4kids.R;

import java.util.ArrayList;
import java.util.List;

public class MainActivity1 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.r_private_profile_activity);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        RecyclerAdapter adapter = new RecyclerAdapter();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        setData(adapter);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new StickHeaderItemDecoration(adapter));
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void setData(RecyclerAdapter adapter) {
        HeaderDataImpl headerData1 = new HeaderDataImpl(HeaderDataImpl.HEADER_TYPE_1, R.layout.empty_view);
        HeaderDataImpl headerData2 = new HeaderDataImpl(HeaderDataImpl.HEADER_TYPE_2, R.layout.header2_item_recycler);

        List<CustomerData> items = new ArrayList<>();
        items.add(new CustomerData());
        items.add(new CustomerData());
        items.add(new CustomerData());
        items.add(new CustomerData());
        adapter.setHeaderAndData(items, headerData1);


        items = new ArrayList<>();
        items.add(new CustomerData());
        items.add(new CustomerData());
        items.add(new CustomerData());
        items.add(new CustomerData());
        adapter.setHeaderAndData(items, headerData2);

        items = new ArrayList<>();
        items.add(new CustomerData());
        items.add(new CustomerData());
        items.add(new CustomerData());
        items.add(new CustomerData());
        adapter.setHeaderAndData(items, headerData1);

        items = new ArrayList<>();
        items.add(new CustomerData());
        items.add(new CustomerData());
        items.add(new CustomerData());
        items.add(new CustomerData());
        adapter.setHeaderAndData(items, headerData2);

        items = new ArrayList<>();
        items.add(new CustomerData());
        items.add(new CustomerData());
        items.add(new CustomerData());
        items.add(new CustomerData());
        adapter.setHeaderAndData(items, headerData1);

        items = new ArrayList<>();
        items.add(new CustomerData());
        items.add(new CustomerData());
        items.add(new CustomerData());
        items.add(new CustomerData());
        adapter.setHeaderAndData(items, headerData2);

        items = new ArrayList<>();
        items.add(new CustomerData());
        items.add(new CustomerData());
        items.add(new CustomerData());
        items.add(new CustomerData());
        adapter.setHeaderAndData(items, headerData1);
    }
}