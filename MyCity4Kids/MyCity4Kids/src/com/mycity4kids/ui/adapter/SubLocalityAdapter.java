package com.mycity4kids.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mycity4kids.R;

import java.util.ArrayList;

public class SubLocalityAdapter extends BaseAdapter {

	private Context mContext ;
	private ArrayList<String> mSubLOcalities ;
	
	public SubLocalityAdapter(Context context , ArrayList<String> arrayList ) {
		mContext = context ; 
		mSubLOcalities = arrayList ; 
	}
	
	@Override
	public int getCount() {
		
		return mSubLOcalities.size() ;
	}

	@Override
	public Object getItem(int pos) {
		
		return mSubLOcalities==null?"":mSubLOcalities.get(pos);
	}

	@Override
	public long getItemId(int pos) {
		
		return 0;
	}

	@Override
	public View getView(int position , View convertView, ViewGroup arg2) {
		ViewHolder holder = new ViewHolder() ;
		if(convertView == null ) {
			LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ; 
			holder.title  = (TextView) inflater.inflate(R.layout.text_for_locality , null ) ;
		} else {
			holder.title = (TextView) convertView ; 
		}
		holder.title.setText(mSubLOcalities.get(position)) ;
		if(position == 0 ) {
			holder.title.setTextColor(Color.parseColor("#929292")) ; 
		} else {
			holder.title.setTextColor(Color.BLACK) ;
		}
		return holder.title;
	}
	
	private class ViewHolder {
		TextView title ; 
	}
	public ArrayList<String> getSublocalities() {
		return mSubLOcalities ; 
	}
}
