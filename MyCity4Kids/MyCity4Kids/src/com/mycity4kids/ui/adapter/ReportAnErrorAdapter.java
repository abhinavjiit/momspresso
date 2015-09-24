package com.mycity4kids.ui.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.mycity4kids.R;
import com.mycity4kids.models.reportanerror.ErrorListModel;
import com.mycity4kids.ui.activity.ReportAnErrorActivity;

import java.util.ArrayList;

public class ReportAnErrorAdapter extends BaseAdapter {

    private ArrayList<ErrorListModel> mErrorList;
    private LayoutInflater mInflator;
    private Context mContext;
    private float density;

    public ReportAnErrorAdapter(Context pContext) {
        mContext = pContext;
        mInflator = (LayoutInflater) pContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        density = mContext.getResources().getDisplayMetrics().density;
    }

    public void setData(ArrayList<ErrorListModel> pErrorList) {
        mErrorList = pErrorList;
    }

    @Override
    public int getCount() {
        return mErrorList.size();
    }

    @Override
    public Object getItem(int position) {
        return mErrorList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        if (view == null) {
            view = mInflator.inflate(R.layout.list_item_report_an_error, null);
            holder = new ViewHolder();
            holder.txvErrorType = (TextView) view.findViewById(R.id.txvError);
            holder.chkBoxErrorSelection = (CheckBox) view.findViewById(R.id.chkBoxError);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        if (position == 0) {
            //	holder.txvErrorType.setTextSize(12 * density);
//			holder.txvErrorType.setTextColor(mContext.getResources().getColor(R.color.btn_bg_blue));
            holder.txvErrorType.setBackgroundColor(mContext.getResources().getColor(R.color.white_color));
            holder.txvErrorType.setPadding((int) (5 * density), 2, 0, 2);
            holder.txvErrorType.setTypeface(null, Typeface.BOLD);
            holder.chkBoxErrorSelection.setVisibility(View.GONE);
        } else {
            //	holder.txvErrorType.setTextSize(12 * density);
            holder.txvErrorType.setBackgroundColor(mContext.getResources().getColor(R.color.white_color));
            holder.txvErrorType.setPadding((int) (10 * density), 10, 0, 10);
            holder.txvErrorType.setTypeface(null, Typeface.NORMAL);
            holder.chkBoxErrorSelection.setVisibility(View.VISIBLE);
        }

        holder.txvErrorType.setText(mErrorList.get(position).getErrorType());
        holder.chkBoxErrorSelection
                .setOnCheckedChangeListener(new OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                        mErrorList.get(position).setSelected(isChecked);
                        if (("Others".equals(mErrorList.get(position).getErrorType())) && isChecked) {
                            ((ReportAnErrorActivity) mContext).specifyErrorEditText.setVisibility(View.VISIBLE);
                            ((ReportAnErrorActivity) mContext).separator.setVisibility(View.VISIBLE);
                        } else if (("Others".equals(mErrorList.get(position).getErrorType())) && !isChecked) {
                            ((ReportAnErrorActivity) mContext).specifyErrorEditText.setVisibility(View.GONE);
                            ((ReportAnErrorActivity) mContext).separator.setVisibility(View.GONE);
                        }
                    }
                });

        return view;
    }

    class ViewHolder {
        TextView txvErrorType;
        CheckBox chkBoxErrorSelection;
    }

}
