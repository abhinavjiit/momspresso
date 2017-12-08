package com.mycity4kids.ui.activity;

import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.AdSettings;
import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAd;
import com.mycity4kids.R;

import java.util.ArrayList;
import java.util.List;

public class NativeAdListActivity extends ListActivity implements AdListener {

    private ListView listView;
    private ListViewAdapter adapter;
    private NativeAd listNativeAd;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        AdSettings.addTestDevice("515fe7a9766e5910cfead95b96ab424b");
//        AdSettings.clearTestDevices();
        listNativeAd = new NativeAd(this, "206155642763202_1699568463421905");
        listNativeAd.setAdListener(this);
        listNativeAd.loadAd();

        listView = getListView();
        adapter = new ListViewAdapter(getApplicationContext());
        listView.setAdapter(adapter);
    }

    @Override
    public void onAdClicked(Ad ad) {
        Toast.makeText(this, "Ad Clicked", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLoggingImpression(Ad ad) {

    }

    @Override
    public void onAdLoaded(Ad ad) {
        adapter.addNativeAd((NativeAd) ad);
    }

    @Override
    public void onError(Ad ad, AdError error) {
        Toast.makeText(this, "Ad failed to load: " + error.getErrorMessage(), Toast.LENGTH_SHORT).show();
    }

    class ListViewAdapter extends BaseAdapter {

        private LayoutInflater inflater;
        private List<Object> list;

        private NativeAd ad;
        private static final int AD_INDEX = 8;

        public ListViewAdapter(Context context) {
            list = new ArrayList<Object>();
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            for (int i = 1; i <= 350; i++) {
                list.add("ListView Item #" + i);
            }
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (position == AD_INDEX && ad != null) {
                // Return the native ad view
                return (View) list.get(position);
            } else {
                TextView view; // Default item type (non-ad)
                if (convertView != null && convertView instanceof TextView) {
                    view = (TextView) convertView;
                } else {
                    view = (TextView) inflater.inflate(R.layout.list_item, parent, false);
                }
                view.setText((String) list.get(position));
                return view;
            }
        }

        public synchronized void addNativeAd(NativeAd ad) {
            if (ad == null) {
                return;
            }
            if (this.ad != null) {
                // Clean up the old ad before inserting the new one
                this.ad.unregisterView();
                this.list.remove(AD_INDEX);
                this.ad = null;
                this.notifyDataSetChanged();
            }
            this.ad = ad;
            View adView = inflater.inflate(R.layout.facebook_ad_unit, null);
            inflateAd(ad, adView);
            list.add(AD_INDEX, adView);
            this.notifyDataSetChanged();
        }

        //Method to inflate ads
        private void inflateAd(NativeAd nativeAd, View adView) {
            // Create native UI using the ad metadata.
            ImageView nativeAdIcon = (ImageView) adView.findViewById(R.id.native_ad_icon);
            TextView nativeAdTitle = (TextView) adView.findViewById(R.id.native_ad_title);
            TextView nativeAdBody = (TextView) adView.findViewById(R.id.native_ad_body);
            MediaView nativeAdMedia = (MediaView) adView.findViewById(R.id.native_ad_media);
            nativeAdMedia.setAutoplay(AdSettings.isVideoAutoplay());
            TextView nativeAdSocialContext =
                    (TextView) adView.findViewById(R.id.native_ad_social_context);
            Button nativeAdCallToAction = (Button) adView.findViewById(R.id.native_ad_call_to_action);

            // Setting the Text
            nativeAdSocialContext.setText(nativeAd.getAdSocialContext());
            nativeAdCallToAction.setText(nativeAd.getAdCallToAction());
            nativeAdCallToAction.setVisibility(View.VISIBLE);
            nativeAdTitle.setText(nativeAd.getAdTitle());
            nativeAdBody.setText(nativeAd.getAdBody());

            // Downloading and setting the ad icon.
            NativeAd.Image adIcon = nativeAd.getAdIcon();
            NativeAd.downloadAndDisplayImage(adIcon, nativeAdIcon);

            // Downloading and setting the cover image.
            nativeAdMedia.setNativeAd(nativeAd);

            // Wire up the View with the native ad, the whole nativeAdContainer will be clickable.
            nativeAd.registerViewForInteraction(adView);
        }
    }
}