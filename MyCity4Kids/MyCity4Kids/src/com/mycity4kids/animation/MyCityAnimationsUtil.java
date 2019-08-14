package com.mycity4kids.animation;

import android.content.Context;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mycity4kids.R;
import com.mycity4kids.models.response.LanguageRanksModel;
import com.mycity4kids.ui.activity.RankingActivity;

import java.util.ArrayList;

/**
 * Created by hemant on 10/8/17.
 */
public class MyCityAnimationsUtil {

    public static void animate(final Context mContext, final View view, final ArrayList<LanguageRanksModel> arr, final int index, final boolean forever) {

        //imageView <-- The View which displays the images
        //images[] <-- Holds R references to the images to display
        //index <-- index of the first image to show in images[]
        //forever <-- If equals true then after the last image it starts all over again with the first image resulting in an infinite loop. You have been warned.

        int fadeInDuration = 2000; // Configure time values here
        int timeBetween = 1500;
        int fadeOutDuration = 2000;

        view.setVisibility(View.INVISIBLE);    //Visible or invisible by default - this will apply when the animation ends
        if (mContext instanceof RankingActivity) {
            ((TextView) ((RelativeLayout) view).getChildAt(1)).setText("" + arr.get(index).getRank());
            ((TextView) ((RelativeLayout) view).getChildAt(3)).setText(mContext.getString(R.string.ranking_in) + " " + arr.get(index).getLangValue().toUpperCase());
        } else {
            ((TextView) ((LinearLayout) view).getChildAt(0)).setText("" + arr.get(index).getRank());
            ((TextView) ((LinearLayout) view).getChildAt(1)).setText(mContext.getString(R.string.blogger_profile_rank_in) + " " + arr.get(index).getLangValue().toUpperCase());
        }


        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new DecelerateInterpolator()); // add this
        fadeIn.setDuration(fadeInDuration);

        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new AccelerateInterpolator()); // and this
        fadeOut.setStartOffset(fadeInDuration + timeBetween);
        fadeOut.setDuration(fadeOutDuration);

        AnimationSet animation = new AnimationSet(false); // change to false
        animation.addAnimation(fadeIn);
        animation.addAnimation(fadeOut);
        animation.setRepeatCount(1);
        view.setAnimation(animation);

        animation.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationEnd(Animation animation) {
                if (arr.size() - 1 > index) {
                    animate(mContext, view, arr, index + 1, forever); //Calls itself until it gets to the end of the array
                } else {
                    if (forever) {
                        animate(mContext, view, arr, 0, forever);  //Calls itself to start the animation all over again in a loop if forever = true
                    }
                }
            }

            public void onAnimationRepeat(Animation animation) {
                // TODO Auto-generated method stub
            }

            public void onAnimationStart(Animation animation) {
                // TODO Auto-generated method stub
            }
        });
    }
}
