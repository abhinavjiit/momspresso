package com.mycity4kids.ui.fragment;

import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.widget.FacebookDialog;
import com.google.android.gms.plus.PlusShare;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.dbtable.TableKids;
import com.mycity4kids.models.user.KidsInfo;
import com.mycity4kids.ui.activity.BloggerDashboardActivity;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 08-06-2015.
 */
public class PublishedArticleShareDialogFragment extends DialogFragment implements OnClickListener {

    String shareUrl;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.published_article_share_dialog, container,
                false);

        getDialog().setTitle("Mycity4kids");
        Bundle extras = getArguments();
        if (extras != null) {
            shareUrl = extras.getString("shareUrl");
        }

        ImageView fImageView = (ImageView) rootView.findViewById(R.id.facebookImageView);
        ImageView gImageView = (ImageView) rootView.findViewById(R.id.googlePlusImageView);
        ImageView whatsappImageView = (ImageView) rootView.findViewById(R.id.whatsappImageView);
        ImageView twitterImageView = (ImageView) rootView.findViewById(R.id.twitterImageView);
        ImageView closeImageView = (ImageView) rootView.findViewById(R.id.closeImageView);

        fImageView.setOnClickListener(this);
        gImageView.setOnClickListener(this);
        whatsappImageView.setOnClickListener(this);
        twitterImageView.setOnClickListener(this);
        closeImageView.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.facebookImageView:
                if (FacebookDialog.canPresentShareDialog(getActivity(), FacebookDialog.ShareDialogFeature.SHARE_DIALOG) && !StringUtils.isNullOrEmpty(shareUrl)) {
                    FacebookDialog shareDialog = new FacebookDialog.ShareDialogBuilder(
                            getActivity()).setName("mycity4kids")
                            .setDescription("Check out this interesting blog post")
                            .setLink(shareUrl).build();
                    shareDialog.present();
                } else {
                    Toast.makeText(getActivity(), "Unable to share with facebook.", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.googlePlusImageView:
                if (StringUtils.isNullOrEmpty(shareUrl)) {
                    Toast.makeText(getActivity(), "Unable to share with google plus.", Toast.LENGTH_SHORT).show();
                } else {
                    Intent shareIntent = new PlusShare.Builder(getActivity())
                            .setType("text/plain")
                            .setText("mycity4kids\n" +
                                    "\n" +
                                    "Check out this interesting blog post ")
                            .setContentUrl(Uri.parse(shareUrl))
                            .getIntent();
                    startActivityForResult(shareIntent, 0);
                }
                break;
            case R.id.whatsappImageView:
                if (StringUtils.isNullOrEmpty(shareUrl)) {
                    Toast.makeText(getActivity(), "Unable to share with whatsapp.", Toast.LENGTH_SHORT).show();
                } else {
                    Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
                    whatsappIntent.setType("text/plain");
                    whatsappIntent.setPackage("com.whatsapp");
                    whatsappIntent.putExtra(Intent.EXTRA_TEXT, "mycity4kids\n\nCheck out this interesting blog post\n " + shareUrl);
                    try {
                        startActivity(whatsappIntent);
                    } catch (android.content.ActivityNotFoundException ex) {
                        Toast.makeText(getActivity(), "Whatsapp have not been installed.", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.twitterImageView:
                if (StringUtils.isNullOrEmpty(shareUrl)) {
                    Toast.makeText(getActivity(), "Unable to share with twitter.", Toast.LENGTH_SHORT).show();
                } else {
                    // Create intent using ACTION_VIEW and a normal Twitter url:
                    String tweetUrl = String.format("https://twitter.com/intent/tweet?text=%s&url=%s",
                            urlEncode("mycity4kids\n\nCheck out this interesting blog post\n "),
                            urlEncode(shareUrl));
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(tweetUrl));
                    // Narrow down to official Twitter app, if available:
                    List<ResolveInfo> matches = getActivity().getPackageManager().queryIntentActivities(intent, 0);
                    for (ResolveInfo info : matches) {
                        if (info.activityInfo.packageName.toLowerCase().startsWith("com.twitter")) {
                            intent.setPackage(info.activityInfo.packageName);
                        }
                    }
                    startActivity(intent);
                }
                break;
            case R.id.closeImageView:

                TableKids tableKids = new TableKids(BaseApplication.getInstance());
                ArrayList<KidsInfo> kidsInformations = (ArrayList<KidsInfo>) tableKids.getAllKids();
                if (kidsInformations != null && !kidsInformations.isEmpty()) {
                    Intent intent = new Intent(getActivity(), BloggerDashboardActivity.class);
                    intent.putExtra(AppConstants.STACK_CLEAR_REQUIRED, true);
                    startActivity(intent);
                    getActivity().finish();
                } else {
                    CompleteProfileDialogFragment completeProfileDialogFragment = new CompleteProfileDialogFragment();
                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    completeProfileDialogFragment.setCancelable(false);
                    completeProfileDialogFragment.show(fm, "Complete blogger profile");
                    dismiss();
                }
                break;
        }

    }

    public static String urlEncode(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            Log.d("UnsupEncodinException", "UTF-8 should always be supported");
            throw new RuntimeException("URLEncoder.encode() failed for " + s);
        }
    }

}