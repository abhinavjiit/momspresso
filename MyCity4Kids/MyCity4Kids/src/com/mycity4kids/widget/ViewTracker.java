package com.mycity4kids.widget;

import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;

import java.util.ArrayList;

public class ViewTracker {

//    // Time from which a particular view has been started viewing.
//    private long startTime = 0;
//
//    // Time at which a particular view has been stopped viewing.
//    private long endTime = 0;
//
//    // Flag is required because 'addOnGlobalLayoutListener'
//    // is called multiple times.
//    // The flag limits the action inside 'onGlobalLayout' to only once.
//    private boolean firstTrackFlag = false;
//
//    // ArrayList of view ids that are being considered for tracking.
//    private ArrayList<Integer> viewsViewed = new ArrayList<>();
//
//    // ArrayList of TrackingData class instances.
//    private ArrayList<TrackingData> trackingData = new ArrayList<>();
//
//    // The minimum amount of area of the list item that should be on
//    // the screen for the tracking to start.
//    private double minimumVisibleHeightThreshold;
//
//    // Start the tracking process.
//    public void startTracking() {
//
//        // Track the views when the data is loaded into
//        // recycler view for the first time.
//        recyclerView.getViewTreeObserver()
//                .addOnGlobalLayoutListener(new ViewTreeObserver
//                        .OnGlobalLayoutListener() {
//                    @Override
//                    public void onGlobalLayout() {
//
//                        if (!firstTrackFlag) {
//
//                            startTime = System.currentTimeMillis();
//
//                            int firstVisibleItemPosition = ((LinearLayoutManager)
//                                    recyclerView.getLayoutManager())
//                                    .findFirstVisibleItemPosition();
//
//                            int lastVisibleItemPosition = ((LinearLayoutManager)
//                                    recyclerView.getLayoutManager())
//                                    .findLastVisibleItemPosition();
//
//                            analyzeAndAddViewData(firstVisibleItemPosition,
//                                    lastVisibleItemPosition);
//
//                            firstTrackFlag = true;
//                        }
//                    }
//                });
//
//        // Track the views when user scrolls through the recyclerview.
//        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(RecyclerView recyclerView,
//                                             int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//
//                // User is scrolling, calculate and store the tracking
//                // data of the views that were being viewed
//                // before the scroll.
//                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
//                    endTime = System.currentTimeMillis();
//
//                    for (int trackedViewsCount = 0;
//                         trackedViewsCount < viewsViewed.size();
//                         trackedViewsCount++) {
//
//                        trackingData.add(prepareTrackingData(String
//                                        .valueOf(viewsViewed
//                                                .get(trackedViewsCount)),
//                                (endTime - startTime) / 1000));
//                    }
//
//                    // We clear the list of current item positions.
//                    // If we don't do this, the items will be tracked
//                    // every time the new items are added.
//                    viewsViewed.clear();
//                }
//
//                // Scrolling has ended, start the tracking
//                // process by assigning a start time
//                // and maintaining a list of views being viewed.
//                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
//
//                    startTime = System.currentTimeMillis();
//
//                    int firstVisibleItemPosition = ((LinearLayoutManager)
//                            recyclerView.getLayoutManager())
//                            .findFirstVisibleItemPosition();
//
//                    int lastVisibleItemPosition = ((LinearLayoutManager)
//                            recyclerView.getLayoutManager())
//                            .findLastVisibleItemPosition();
//
//                    analyzeAndAddViewData(firstVisibleItemPosition,
//                            lastVisibleItemPosition);
//                }
//            }
//        });
//    }
//
//    // Track the items currently visible and then stop the tracking process.
//    public void stopTracking() {
//
//        endTime = System.currentTimeMillis();
//
//        int firstVisibleItemPosition = ((LinearLayoutManager)
//                recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
//
//        int lastVisibleItemPosition = ((LinearLayoutManager)
//                recyclerView.getLayoutManager()).findLastVisibleItemPosition();
//
//        analyzeAndAddViewData(firstVisibleItemPosition,
//                lastVisibleItemPosition);
//
//        for (int trackedViewsCount = 0; trackedViewsCount < viewsViewed.size();
//             trackedViewsCount++) {
//
//            trackingData.add(prepareTrackingData(String.valueOf(viewsViewed
//                            .get(trackedViewsCount)),
//                    (endTime - startTime) / 1000));
//
//            viewsViewed.clear();
//        }
//    }
//
//    private void analyzeAndAddViewData(int firstVisibleItemPosition,
//                                       int lastVisibleItemPosition) {
//
//        // Analyze all the views
//        for (int viewPosition = firstVisibleItemPosition;
//             viewPosition <= lastVisibleItemPosition; viewPosition++) {
//
//            Log.i("View being considered", String.valueOf(viewPosition));
//
//            // Get the view from its position.
//            View itemView = recyclerView.getLayoutManager()
//                    .findViewByPosition(viewPosition);
//
//            // Check if the visibility of the view is more than or equal
//            // to the threshold provided. If it falls under the desired limit,
//            // add it to the tracking data.
//            if (getVisibleHeightPercentage(itemView) >= minimumVisibleHeightThreshold) {
//                viewsViewed.add(viewPosition);
//            }
//        }
//    }
//
//    // Method to calculate how much of the view is visible
//    // (i.e. within the screen) wrt the view height.
//    // @param view
//    // @return Percentage of the height visible.
//    private double getVisibleHeightPercentage(View view) {
//
//        Rect itemRect = new Rect();
//        view.getLocalVisibleRect(itemRect);
//
//        // Find the height of the item.
//        double visibleHeight = itemRect.height();
//        double height = view.getMeasuredHeight();
//
//        Log.i("Visible Height", String.valueOf(visibleHeight));
//        Log.i("Measured Height", String.valueOf(height));
//
//        double viewVisibleHeightPercentage = ((visibleHeight / height) * 100);
//
//        Log.i("Percentage visible", String.valueOf(viewVisibleHeightPercentage));
//
//        Log.i("___", "___");
//
//        return viewVisibleHeightPercentage;
//    }
//
//    // Method to store the tracking data in an instance of "TrackingData" and
//    // then returning that instance.
//    // @param viewId
//    // @param viewDuration in seconds.
//    private TrackingData prepareTrackingData(String viewId, long viewDuration) {
//
//        TrackingData trackingData = new TrackingData();
//
//        trackingData.setViewId(viewId);
//        trackingData.setViewDuration(viewDuration);
//
//        return trackingData;
//    }
}    