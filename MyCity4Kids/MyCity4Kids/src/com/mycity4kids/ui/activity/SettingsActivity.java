package com.mycity4kids.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.mycity4kids.R;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.ui.fragment.ArticlesFragment;
import com.mycity4kids.ui.fragment.ChangeCityFragment;
import com.mycity4kids.ui.fragment.FragmentAdultProfile;
import com.mycity4kids.ui.fragment.FragmentBusinesslistEvents;
import com.mycity4kids.ui.fragment.FragmentFamilyDetail;
import com.mycity4kids.ui.fragment.FragmentFamilyProfile;
import com.mycity4kids.ui.fragment.FragmentHomeCategory;
import com.mycity4kids.ui.fragment.FragmentKidProfile;
import com.mycity4kids.ui.fragment.FragmentSetting;
import com.mycity4kids.ui.fragment.NotificationFragment;

/**
 * Created by anshul on 8/4/16.
 */
public class SettingsActivity extends BaseActivity {

    int cityId;
    TextView cityChange;
    String bio, firstName, lastName;
    Toolbar mToolBar;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Utils.pushOpenScreenEvent(SettingsActivity.this, "Settings", SharedPrefUtils.getUserDetailModel(this).getDynamoId() + "");

        //  ((DashboardActivity) getActivity()).refreshMenu();

        mToolBar = (Toolbar) findViewById(R.id.toolbar);
        mToolBar.setVisibility(View.VISIBLE);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.setStatusBarBackgroundColor(ContextCompat.getColor(this, R.color.black_color));

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolBar, R.string.blank, R.string.blank) {
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                //  getActionBar().setTitle(mTitle);
                invalidateOptionsMenu();
                syncState();
            }

            public void onDrawerOpened(View drawerView) {
                // getActionBar().setTitle(mTitle);
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
                syncState();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
        bio = getIntent().getStringExtra("bio");
        firstName = getIntent().getStringExtra("firstName");
        lastName = getIntent().getStringExtra("lastName");
        Bundle extras = getIntent().getExtras();
        String fragmentToLoad = "";
        if (null != extras)
            fragmentToLoad = extras.getString("load_fragment", "");
        if (Constants.SETTINGS_FRAGMENT.equals(fragmentToLoad)) {
            //changeVisibiltyOfArrow(false);
            setTitle("Settings");
            Bundle bundle = new Bundle();
            bundle.putString("bio", getIntent().getStringExtra("bio"));
            bundle.putString("firstName", getIntent().getStringExtra("firstName"));
            bundle.putString("lastName", getIntent().getStringExtra("lastName"));
            bundle.putString("phoneNumber", getIntent().getStringExtra("phoneNumber"));
            replaceFragment(new FragmentSetting(), bundle, false);
        }
        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                Fragment currentFrag = getSupportFragmentManager().findFragmentById(R.id.content_frame);

                if (currentFrag instanceof FragmentSetting) {
                    //   changeVisibiltyOfArrow(false);
                    setTitle("Settings");
                    mDrawerToggle.setDrawerIndicatorEnabled(false);
                    mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                    mDrawerToggle.onDrawerStateChanged(DrawerLayout.LOCK_MODE_UNLOCKED);
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);

                    getSupportActionBar().setHomeButtonEnabled(true);


                } else if (currentFrag instanceof FragmentFamilyDetail) {

                    setTitle("Family Details");

                    mDrawerToggle.setDrawerIndicatorEnabled(false);
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);

                    getSupportActionBar().setHomeButtonEnabled(true);
                    mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                    mDrawerToggle.onDrawerStateChanged(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
//                    mDrawerToggle.setHomeAsUpIndicator(R.drawable.back_arroow);
                    //    changeVisibiltyOfArrow(false);
                } else if (currentFrag instanceof FragmentFamilyProfile) {


                    mDrawerToggle.setDrawerIndicatorEnabled(false);
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                    mDrawerToggle.onDrawerStateChanged(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
//                    mDrawerToggle.setHomeAsUpIndicator(R.drawable.back_arroow);
                    getSupportActionBar().setHomeButtonEnabled(true);

                    //   changeVisibiltyOfArrow(false);
                } else if (currentFrag instanceof FragmentAdultProfile) {


                    mDrawerToggle.setDrawerIndicatorEnabled(false);
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    getSupportActionBar().setHomeButtonEnabled(true);
                    mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                    mDrawerToggle.onDrawerStateChanged(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
//                    mDrawerToggle.setHomeAsUpIndicator(R.drawable.back_arroow);
                    //     changeVisibiltyOfArrow(false);
                } else if (currentFrag instanceof FragmentKidProfile) {


                    mDrawerToggle.setDrawerIndicatorEnabled(false);
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    getSupportActionBar().setHomeButtonEnabled(true);
                    mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                    mDrawerToggle.onDrawerStateChanged(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
//                    mDrawerToggle.setHomeAsUpIndicator(R.drawable.back_arroow);
                    //    changeVisibiltyOfArrow(false);

                } else if (currentFrag instanceof ChangeCityFragment) {
                    setTitle("Change City");

                    mDrawerToggle.setDrawerIndicatorEnabled(false);

                    mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                    mDrawerToggle.onDrawerStateChanged(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
//                    mDrawerToggle.setHomeAsUpIndicator(R.drawable.back_arroow);
                    //     changeVisibiltyOfArrow(false);
                }
                invalidateOptionsMenu();
                mDrawerToggle.syncState();

            }
        });
        mDrawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment topFragment = getSupportFragmentManager().findFragmentById(R.id.content_frame);
                if (topFragment instanceof FragmentSetting) {
                    finish();
                }
                switch (v.getId()) {

                    case -1:
                        getSupportFragmentManager().popBackStack();
                        break;

                    default:
                        break;
                }
            }
        });

    }

    @Override
    protected void updateUi(Response response) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        Fragment topFragment = getSupportFragmentManager().findFragmentById(R.id.content_frame);
        if (topFragment instanceof FragmentFamilyDetail) {

            getMenuInflater().inflate(R.menu.forgot_password, menu);
        } else if (topFragment instanceof FragmentFamilyProfile) {

            getMenuInflater().inflate(R.menu.forgot_password, menu);
        } else if (topFragment instanceof FragmentAdultProfile) {

            getMenuInflater().inflate(R.menu.forgot_password, menu);
        } else if (topFragment instanceof FragmentKidProfile) {

            getMenuInflater().inflate(R.menu.forgot_password, menu);
        } else if (topFragment instanceof ChangeCityFragment) {
            getMenuInflater().inflate(R.menu.forgot_password, menu);

        } else if (topFragment instanceof FragmentBusinesslistEvents) {
            getMenuInflater().inflate(R.menu.menu_event, menu);
        } else if (topFragment instanceof ArticlesFragment) {
            getMenuInflater().inflate(R.menu.menu_articles, menu);
        } else if (topFragment instanceof NotificationFragment) {
            getMenuInflater().inflate(R.menu.forgot_password, menu);
        } else if (topFragment instanceof FragmentHomeCategory) {
            getMenuInflater().inflate(R.menu.kidsresource_listing, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Fragment topFragment = getSupportFragmentManager().findFragmentById(R.id.content_frame);
        switch (item.getItemId()) {
            case R.id.save:

                if (topFragment instanceof FragmentAdultProfile) {
                    ((FragmentAdultProfile) topFragment).callService();
                } else if (topFragment instanceof FragmentKidProfile) {
                    ((FragmentKidProfile) topFragment).callService();
                } else if (topFragment instanceof FragmentFamilyProfile) {
                    ((FragmentFamilyProfile) topFragment).callService();
                } else if (topFragment instanceof FragmentFamilyDetail) {
                    ((FragmentFamilyDetail) topFragment).onHeaderButtonTapped();
                } else if (topFragment instanceof NotificationFragment) {
                    ((NotificationFragment) topFragment).saveNotificationSetting();
                } else if (topFragment instanceof ChangeCityFragment)
                    ((ChangeCityFragment) topFragment).changeCity();
                break;
            case android.R.id.home:
                finish();
                break;

        }
        return true;
    }
   /* public void updateImageProfile() {
        if (!StringUtils.isNullOrEmpty(SharedPrefUtils.getProfileImgUrl(this))) {
            Picasso.with(this).load(SharedPrefUtils.getProfileImgUrl(this)).placeholder(R.drawable.family_xxhdpi).error(R.drawable.family_xxhdpi).transform(new RoundedTransformation()).into(profileImage);
        }
    }*/

    @Override
    public void onBackPressed() {
        Fragment topFragment = getSupportFragmentManager().findFragmentById(R.id.content_frame);
        if (topFragment instanceof FragmentSetting) {
            finish();
        }
        super.onBackPressed();
    }
}

