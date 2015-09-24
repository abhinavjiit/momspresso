package com.mycity4kids.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TabWidget;

public class FixedTabWidget extends TabWidget{

	public FixedTabWidget(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	 public FixedTabWidget(Context context, AttributeSet attrs) {
	        super(context, attrs);
	    }
	 @Override
	public void dispatchWindowFocusChanged(boolean hasFocus) {
		 if(getRootView()!=null)
			{
			super.dispatchWindowFocusChanged(hasFocus);
			}
	}

}
