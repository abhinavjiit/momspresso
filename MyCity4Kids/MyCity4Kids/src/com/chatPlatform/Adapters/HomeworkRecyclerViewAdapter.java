package com.chatPlatform.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chatPlatform.models.HomeworkModel;
import com.mycity4kids.R;

import java.util.ArrayList;

/**
 * Created by hemant on 13/1/16.
 */
public class HomeworkRecyclerViewAdapter extends RecyclerView.Adapter<HomeworkRecyclerViewAdapter.DataObjectHolder> {
    private static String LOG_TAG = "HomeworkRecyclerViewAdapter";
    private ArrayList<HomeworkModel> mDataset;
    private static MyClickListener myClickListener;

    public static class DataObjectHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView label;
        TextView name;

        public DataObjectHolder(View itemView) {
            super(itemView);
            label = (TextView) itemView.findViewById(R.id.calDateTextView);
            name = (TextView) itemView.findViewById(R.id.calMonthTextView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            myClickListener.onItemClick(getLayoutPosition(), v);
        }
    }

    public HomeworkRecyclerViewAdapter(ArrayList<HomeworkModel> myDataset) {
        mDataset = myDataset;
    }

    public void setOnItemClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.homework_grid_item, parent, false);

        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(DataObjectHolder holder, int position) {
        holder.label.setText(mDataset.get(position).getId());
        holder.name.setText(mDataset.get(position).getName());
    }

    public void addItem(HomeworkModel dataObj, int index) {
        mDataset.add(dataObj);
        notifyItemInserted(index);
    }

    public void deleteItem(int index) {
        mDataset.remove(index);
        notifyItemRemoved(index);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public interface MyClickListener {
        public void onItemClick(int position, View v);
    }
}
