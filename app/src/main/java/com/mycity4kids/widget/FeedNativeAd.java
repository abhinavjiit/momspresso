package com.mycity4kids.widget;

@SuppressWarnings({"FieldCanBeLocal", "WeakerAccess"})
public class FeedNativeAd /*implements NativeAdsManager.Listener*/ {
//    private static final String TAG = FeedNativeAd.class.getSimpleName();
//
//    private Context context;
//    private NativeAdsManager manager;
//    private AdLoadingListener adLoadingListener;
//    private String feedAdId;
//
//    public FeedNativeAd(final Context context, final AdLoadingListener adLoadingListener, String feedAdId) {
////        AdSettings.addTestDevice("515fe7a9766e5910cfead95b96ab424b");
////        AdSettings.clearTestDevices();
//        this.context = context;
//        this.feedAdId = feedAdId;
//        manager = new NativeAdsManager(this.context, feedAdId, 10);
//        manager.setListener(this);
//        this.adLoadingListener = adLoadingListener;
//    }
//
//    @Override
//    public void onAdsLoaded() {
//        Log.d(TAG, "onAdsLoaded");
//        adLoadingListener.onFinishToLoadAds();
//    }
//
//    @Override
//    public void onAdError(AdError adError) {
//        Log.w(TAG, "onAdError: " + adError.getErrorMessage());
//        adLoadingListener.onErrorToLoadAd();
//    }
//
//    public void loadAds() {
//        manager.loadAds();
//    }
//
//    @Nullable
//    public NativeAd getAd() {
//        if (manager != null && manager.isLoaded()) {
//            return manager.nextNativeAd();
//        } else {
//            return null;
//        }
//    }
//
//    public interface AdLoadingListener {
//        void onFinishToLoadAds();
//
//        void onErrorToLoadAd();
//    }
}