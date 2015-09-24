package com.mycity4kids.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TabHost;
/**
 * 
 * @author deepanker chaudhary
 *
 *we are using tabhost with this class because api version 2.2 2.3 it's give window focus error
 */
public class FixedTabHost extends TabHost{

	
	
	 public FixedTabHost(Context context, AttributeSet attrs) {
	        super(context, attrs);
	    }
	public FixedTabHost(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	@Override
	public void dispatchWindowFocusChanged(boolean hasFocus) {
		if(getCurrentView()!=null)
		{
		super.dispatchWindowFocusChanged(hasFocus);
		}
	}

}
