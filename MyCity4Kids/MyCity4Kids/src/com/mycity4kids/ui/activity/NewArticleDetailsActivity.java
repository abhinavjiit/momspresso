package com.mycity4kids.ui.activity;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.controller.ArticleBlogDetailsController;
import com.mycity4kids.models.parentingdetails.ParentingDetailResponse;
import com.mycity4kids.ui.CircleTransformation;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

/**
 * Created by hemant on 5/4/16.
 */
public class NewArticleDetailsActivity extends BaseActivity {

    private CollapsingToolbarLayout collapsingToolbarLayout;
    FloatingActionButton floatingActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_article_details_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.anim_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Dear Mom-In-Law, You Can't Love Me Like Your Daughter, At Least Treat Me The Same As Your Son-In-Law?");
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setExpandedTitleGravity(Gravity.CENTER);
        collapsingToolbarLayout.setExpandedTitleColor(Color.TRANSPARENT);

        NestedScrollView nestedScrollView = (NestedScrollView) findViewById(R.id.scroll);
//        nestedScrollView.getViewTreeObserver().addOnScrollChangedListener();
        floatingActionButton = (FloatingActionButton) findViewById(R.id.user_image);

        ArticleBlogDetailsController _controller = new ArticleBlogDetailsController(this, this);
        showProgressDialog(getString(R.string.fetching_data));
        _controller.getData(AppConstants.ARTICLES_DETAILS_REQUEST, "11114");
    }

    @Override
    protected void updateUi(Response response) {
        ParentingDetailResponse responseData = (ParentingDetailResponse) response.getResponseObject();
        if (responseData.getResponseCode() == 200) {

            Picasso.with(this).load(responseData.getResult().getData().getAuthor_image()).
                    transform(new CircleTransformation()).into(new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    int sdk = android.os.Build.VERSION.SDK_INT;
//                    if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    floatingActionButton.setPadding(-1, -1, -1, -1);
                    floatingActionButton.setImageDrawable(new BitmapDrawable(getResources(), bitmap));
//                    } else {
//                        floatingActionButton.setImageDrawable(new BitmapDrawable(getResources(), bitmap));
//                    }

                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {
                    floatingActionButton.setBackgroundResource(R.drawable.activities_icon);
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            });


            removeProgressDialog();
        } else if (responseData.getResponseCode() == 400) {
            removeProgressDialog();
            finish();
            String message = responseData.getResult().getMessage();
            if (!StringUtils.isNullOrEmpty(message)) {
                showToast(message);
            } else {
                showToast(getString(R.string.went_wrong));
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_res, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_settings:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
