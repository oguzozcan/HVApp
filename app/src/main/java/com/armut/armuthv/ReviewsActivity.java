package com.armut.armuthv;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.armut.armuthv.fragments.ReviewsFragment;
import com.armut.armuthv.objects.Review;

import java.util.ArrayList;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ReviewsActivity extends AppCompatActivity{

    private ViewPager mViewPager;
    private ArrayList<Review> reviewList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviews);
        // Set up the action bar.
        // app = (ArmutHAApp)getApplication();
        Toolbar mainToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mainToolbar);
        final ActionBar actionBar = getSupportActionBar();
        mainToolbar.setTitle("");
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle("");
        //TextView toolbarTitle = (TextView) findViewById(R.id.toolbar_title);
        //toolbarTitle.setTypeface(ArmutUtils.loadFont(getAssets(), getString(R.string.font_pera_bold)));
        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        String profileId = getIntent().getStringExtra("PROFILE_ID");
        reviewList = getIntent().getParcelableArrayListExtra("REVIEW_LIST");
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), profileId);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        final TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.dark_gray_actionbar_secondary));
        tabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(this, R.color.gray2));
        tabLayout.setTabTextColors(ContextCompat.getColor(this, R.color.gray2), ContextCompat.getColor(this, R.color.white));
        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                tabLayout.setupWithViewPager(mViewPager);
            }
        });
        mViewPager.setOffscreenPageLimit(2);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_empty, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home){
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        if(getSupportFragmentManager().getBackStackEntryCount() > 0){
            getSupportFragmentManager().popBackStack();
        }
        else {
            finish();
            MainActivity.setBackwardsTranslateAnimation(this);
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        final String profileId;

        public SectionsPagerAdapter(FragmentManager fm, String profileId) {
            super(fm);
            this.profileId = profileId;

        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return ReviewsFragment.newInstance(position + 1, profileId, reviewList);
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            //Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.tab_all_comments);//.toUpperCase(l);
                case 1:
                    return getString(R.string.tab_best_comments);//.toUpperCase(l);
                case 2:
                    return getString(R.string.tab_worst_comments);//.toUpperCase(l);
            }
            return null;
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}


