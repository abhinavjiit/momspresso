package com.mycity4kids.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ListView;

public class CustomListView extends ListView{
	boolean expanded = true;
	public CustomListView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	public CustomListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
 
    public CustomListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    public boolean isExpanded() {
    	return expanded;
    	}
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    	  
    	// HACK! TAKE THAT ANDROID!
    	  if (isExpanded()) {
    	  // Calculate entire height by providing a very large height hint.
    	  // View.MEASURED_SIZE_MASK represents the largest height possible.
    	  int expandSpec = MeasureSpec.makeMeasureSpec(MEASURED_SIZE_MASK, MeasureSpec.AT_MOST);
    	 
    	  super.onMeasure(widthMeasureSpec, expandSpec);
    	  ViewGroup.LayoutParams params = getLayoutParams();
    	  params.height = getMeasuredHeight();
    	  } else {
    	  super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    	  }
    }
    public void setIsExpanded(boolean expanded) {
    	this.expanded = expanded;
    	
    	}
}
