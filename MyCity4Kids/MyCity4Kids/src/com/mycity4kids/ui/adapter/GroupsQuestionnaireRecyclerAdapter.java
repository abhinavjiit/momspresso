package com.mycity4kids.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.mycity4kids.R;

import java.util.ArrayList;

/**
 * Created by hemant on 10/4/18.
 */

public class GroupsQuestionnaireRecyclerAdapter extends RecyclerView.Adapter<GroupsQuestionnaireRecyclerAdapter.GroupsViewHolder> {

    private final Context mContext;
    private final LayoutInflater mInflator;
    private ArrayList<String> groupsDataList;
    private GroupsQuestionnaireRecyclerAdapter.RecyclerViewClickListener mListener;
    private int selectedPosition;
    private String[] mDataset;

    public GroupsQuestionnaireRecyclerAdapter(Context pContext, GroupsQuestionnaireRecyclerAdapter.RecyclerViewClickListener listener) {
        mContext = pContext;
        mInflator = (LayoutInflater) pContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mListener = listener;

    }

    public void setData(ArrayList<String> groupsDataList) {
        this.groupsDataList = groupsDataList;
        mDataset = new String[groupsDataList.size()];
    }

    @Override
    public int getItemCount() {
        return groupsDataList.size();
    }

    @Override
    public GroupsQuestionnaireRecyclerAdapter.GroupsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v0 = mInflator.inflate(R.layout.groups_questionnaire_item_layout, parent, false);
        return new GroupsQuestionnaireRecyclerAdapter.GroupsViewHolder(v0, new MyCustomEditTextListener());
    }

    @Override
    public void onBindViewHolder(GroupsViewHolder holder, int position) {
//        holder.groupImageView.setText(groupsDataList.get(position));
        holder.questionTextView.setText(groupsDataList.get(position));
        holder.myCustomEditTextListener.updatePosition(holder.getAdapterPosition());
        holder.answerEditText.setText(mDataset[holder.getAdapterPosition()]);
//        holder.groupsNameTextView.setSelected(position == selectedPosition);
    }

    public class GroupsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public MyCustomEditTextListener myCustomEditTextListener;
        TextView questionTextView;
        EditText answerEditText;

        GroupsViewHolder(View view, MyCustomEditTextListener myCustomEditTextListener) {
            super(view);
            questionTextView = (TextView) view.findViewById(R.id.questionTextView);
            answerEditText = (EditText) view.findViewById(R.id.answerEditText);
            this.myCustomEditTextListener = myCustomEditTextListener;
            this.answerEditText.addTextChangedListener(myCustomEditTextListener);
//            view.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    mListener.onRecyclerItemClick(v, getAdapterPosition());
//                    notifyDataSetChanged();
//                }
//            });
        }

        @Override
        public void onClick(View v) {
            mListener.onRecyclerItemClick(v, getAdapterPosition());
        }
    }

    private class MyCustomEditTextListener implements TextWatcher {
        private int position;

        public void updatePosition(int position) {
            this.position = position;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            // no op
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            mDataset[position] = charSequence.toString();
        }

        @Override
        public void afterTextChanged(Editable editable) {
            // no op
        }
    }

    public interface RecyclerViewClickListener {
        void onRecyclerItemClick(View view, int position);
    }

    public String[] getAnswersList() {
        return mDataset;
    }
}
