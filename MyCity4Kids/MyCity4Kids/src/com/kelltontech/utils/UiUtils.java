package com.kelltontech.utils;

import android.content.Context;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;

/**
 * Utility class for UI related methods.
 */
public class UiUtils {

	/**
	 * @param event
	 * @return
	 */
	public static String getEventDetails( MotionEvent event ) {
		String action = "";
		switch( event.getAction() ) {
		case MotionEvent.ACTION_DOWN: action = "ACTION_DOWN"; break;
		case MotionEvent.ACTION_UP: action = "ACTION_UP"; break;
		case MotionEvent.ACTION_MOVE: action = "ACTION_MOVE"; break;
		case MotionEvent.ACTION_CANCEL: action = "ACTION_CANCEL"; break;
		default: action = "ACTION_" + event.getAction(); break;
		}
		return action + ": " + event.getX() + "-" + event.getY();
	}

	/**
	 * @param event
	 * @return
	 */
	public static float getPixels( Context context, float dp ) {
		DisplayMetrics dm = context.getResources().getDisplayMetrics();
		return dp * dm.density;
	}

	public static void sendScrollToBottom(final ScrollView scrollview){
		final Handler handler = new Handler();
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {Thread.sleep(400);} catch (InterruptedException e) {}
				handler.post(new Runnable() {
					@Override
					public void run() {
						scrollview.fullScroll(View.FOCUS_DOWN);
					}
				});
			}
		}).start();
	}

	/**
	 * @param listView
	 */
	public static void setListViewHeightBasedOnChildren(ListView listView) {
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			// pre-condition
			return;
		}

		int totalHeight = 0;
		for (int i = 0; i < listAdapter.getCount(); i++) {
			View listItem = listAdapter.getView(i, null, listView);
			listItem.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			listItem.measure(0, 0);
			totalHeight += listItem.getMeasuredHeight();
		}

		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		listView.setLayoutParams(params);
		listView.requestLayout();
	}
	public static void setListViewHeightBasedOnChildren(ListView listView,float height,int margin) {
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			// pre-condition
			return ;
		}

		int totalHeight = 0;
		for (int i = 0; i < listAdapter.getCount(); i++) {
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(0, 0);
			totalHeight += listItem.getMeasuredHeight();
		}

		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = (int) Math.min(totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1)),height );
		params.height=params.height+margin;
		listView.setLayoutParams(params);
		listView.requestLayout();
	}
}
