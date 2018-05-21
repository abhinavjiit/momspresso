package com.mycity4kids.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.mycity4kids.R;

import java.util.ArrayList;

/**
 * Created by hemant on 10/4/18.
 */

public class AddTextPollRecyclerGridAdapter extends RecyclerView.Adapter<AddTextPollRecyclerGridAdapter.TextPollViewHolder> {

    private final Context mContext;
    private final LayoutInflater mInflator;
    private ArrayList<String> textChoiceList;
    private TextPollRecyclerViewClickListener mListener;
    private int selectedPosition;

    public AddTextPollRecyclerGridAdapter(Context pContext, TextPollRecyclerViewClickListener listener) {
        mContext = pContext;
        mInflator = (LayoutInflater) pContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mListener = listener;

    }

    public void setNewListData(ArrayList<String> textChoiceList) {
        this.textChoiceList = textChoiceList;
    }

    @Override
    public int getItemCount() {
        return textChoiceList.size();
    }

    @Override
    public TextPollViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v0 = mInflator.inflate(R.layout.poll_choice_item, parent, false);
        return new TextPollViewHolder(v0);
    }

    @Override
    public void onBindViewHolder(TextPollViewHolder holder, int position) {
//        holder.groupImageView.setText(textChoiceList.get(position));
//        holder.groupsNameTextView.setText(textChoiceList.get(position).getTitle());
//        holder.groupsNameTextView.setSelected(position == selectedPosition);
        if (textChoiceList.size() > 2) {
            holder.removeOptionImageView.setVisibility(View.VISIBLE);
        } else {
            holder.removeOptionImageView.setVisibility(View.INVISIBLE);
        }

        int x = holder.getLayoutPosition();

        if (textChoiceList.get(x).length() > 0) {
            holder.textPollEditText.setText(textChoiceList.get(x));
        } else {
            holder.textPollEditText.setText(null);
            holder.textPollEditText.setHint("Choice " + (position + 1));
            holder.textPollEditText.requestFocus();
        }
    }

    public class TextPollViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        EditText textPollEditText;
        ImageView removeOptionImageView;

        TextPollViewHolder(View view) {
            super(view);
            textPollEditText = (EditText) view.findViewById(R.id.textPollEditText);
            removeOptionImageView = (ImageView) view.findViewById(R.id.removeItemImageView);
            removeOptionImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        textChoiceList.remove(getAdapterPosition());
                        notifyDataSetChanged();
                    } catch (ArrayIndexOutOfBoundsException ex) {

                    }
                    mListener.onTextPollItemClick(v, getAdapterPosition());
                }
            });
            textPollEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    textChoiceList.set(getAdapterPosition(), s.toString());
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

        }

        @Override
        public void onClick(View v) {
            mListener.onTextPollItemClick(v, getAdapterPosition());
        }
    }


    public interface TextPollRecyclerViewClickListener {
        void onTextPollItemClick(View view, int position);
    }

}
