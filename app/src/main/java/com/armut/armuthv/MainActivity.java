package com.armut.armuthv;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.LongSparseArray;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.armut.armuthv.busevents.ApiErrorEvent;
import com.armut.armuthv.busevents.JobEvents;
import com.armut.armuthv.busevents.MiscEvents;
import com.armut.armuthv.components.MainPageTabLayout;
import com.armut.armuthv.fragments.BasicFragment;
import com.armut.armuthv.fragments.CalendarFragment;
import com.armut.armuthv.fragments.DealsFragment;
import com.armut.armuthv.fragments.JobBasicFragment;
import com.armut.armuthv.fragments.OpportunitiesFragment;
import com.armut.armuthv.fragments.QuotesFragment;
import com.armut.armuthv.fragments.UserProfileFragment;
import com.armut.armuthv.objects.DeviceToken;
import com.armut.armuthv.objects.Job;
import com.armut.armuthv.objects.User;
import com.armut.armuthv.services.FusedLocationer;
import com.armut.armuthv.utils.Constants;
import com.google.firebase.iid.FirebaseInstanceId;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.squareup.otto.Subscribe;

import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends BaseActivity implements JobBasicFragment.TabLayoutRefreshCallback {

    private String activeFragmentTag = "";
    protected int selectedTab = 1;
    protected int previousTab = 1;
    public final static int TAB_COUNT_FOR_QUOTE = 3;
    public final static int TAB_COUNT_FOR_BOOKING = 2;
    public static int defaultBusinessModelId = 0;//Constants.BUSINESS_MODEL_REQUEST;

    public LongSparseArray<JobWithSectionId> jobWithSectionIdHashMap;

    @Override
    protected void setTag() {
        TAG = "MAIN_ACTIVITY";
    }

    @Override
    public void onStart() {
        super.onStart();
        app.getBus().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        app.getBus().unregister(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "ON CREATE MAIN ACTIVITY");
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView toolbarTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbarTitle.setText(getString(R.string.tab1_projects));
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );
        ViewSwitcher bottomTabSwitcher = (ViewSwitcher) findViewById(R.id.bottomTabSwitcher);
        bottomTabSwitcher.setDisplayedChild(1);
        initPagerViews(defaultBusinessModelId);

        FusedLocationer locationer = app.getFusedLocationer();
        locationer.startService(MainActivity.this);

        if(app.isTokenPresent(TAG) != null){
            if (app.getDeviceToken().isEmpty()) {
                String token = FirebaseInstanceId.getInstance().getToken();
                Log.d(TAG, "DEVICE TOKEN not found on datasaver and requested: " + token);
                app.getBus().post(new MiscEvents.PostDeviceTokenRequest(new DeviceToken(token)));
            }else{
                String token = FirebaseInstanceId.getInstance().getToken();
                Log.d(TAG, "DEVICE TOKEN not found on datasaver and requested: " + token);
            }
            // Handle possible data accompanying notification message.
            // [START handle_data_extras]
            handleNotificationAndDeepLinks(false);
        }
    }

    @Override
    protected void onDestroy() {
        try{
            MixpanelAPI mixpanel = MixpanelAPI.getInstance(this, getString(R.string.mpId));
            mixpanel.flush();
        }catch(Exception e){
            e.printStackTrace();
        }
        super.onDestroy();
        FusedLocationer locationer = app.getFusedLocationer();
        locationer.onActivityDestroyed();
    }

    private void initPagerViews(final int businessModel) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final ViewPager mViewPager = (ViewPager) findViewById(R.id.viewPager);
                int tabCount = businessModel != Constants.BUSINESS_MODEL_RESERVATION ? TAB_COUNT_FOR_QUOTE : TAB_COUNT_FOR_BOOKING;
                Log.d(TAG, "INIT PAGER VIEWS : " + businessModel + " - tabCount: " + tabCount);
                FragmentManager fm = getSupportFragmentManager();
//                if (fm.getFragments() != null) {
//                    fm.getFragments().clear();
//                }
//                if(mViewPager.getAdapter() != null){
//                    Log.d(TAG, "ADAPTER ITEM COUNT: " + mViewPager.getAdapter().getCount());
//                }
                mViewPager.setAdapter(new SectionsPagerAdapter(fm, tabCount));
                mViewPager.getAdapter().notifyDataSetChanged();
                MainPageTabLayout tabLayout = (MainPageTabLayout) findViewById(R.id.tabs);
                tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
                tabLayout.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.slate));
                tabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(MainActivity.this, R.color.avocado));
                tabLayout.setTabTextColors(ContextCompat.getColor(MainActivity.this, R.color.warm_grey2), ContextCompat.getColor(MainActivity.this, R.color.white));
                tabLayout.setupWithViewPager(mViewPager);
                for (int i = 0; i < tabCount; i++) {
                    setTab(tabLayout, i, tabCount);
                }
                tabLayout.clearOnTabSelectedListeners();
                tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager) {
                    @Override
                    public void onTabSelected(TabLayout.Tab tab) {
                        View customView = tab.getCustomView();
                        if (customView == null) {
                            return;
                        }
                        TextView jobNumber = (TextView) customView.findViewById(R.id.jobNumber);
                        TextView tabName = (TextView) customView.findViewById(R.id.tabName);
                        jobNumber.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.white));
                        tabName.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.white));
                        mViewPager.setCurrentItem(tab.getPosition());
                        Log.d(TAG, "ITEM SELECTED : " + tab.getPosition());
                    }

                    @Override
                    public void onTabUnselected(TabLayout.Tab tab) {
                        View customView = tab.getCustomView();
                        if (customView == null) {
                            return;
                        }
                        TextView jobNumber = (TextView) customView.findViewById(R.id.jobNumber);
                        TextView tabName = (TextView) customView.findViewById(R.id.tabName);
                        jobNumber.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.warm_grey2));
                        tabName.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.warm_grey2));
                    }

                    @Override
                    public void onTabReselected(TabLayout.Tab tab) {

                    }
                });
                //mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
                //TODO This should change
                mViewPager.setOffscreenPageLimit(2);
                mViewPager.setCurrentItem(0);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (app.isTokenPresent(TAG) != null) {
            if (app.getUser() == null) { // !userName.equals("") &&
                Log.d(TAG, "USER ID: Call GET USER");
                View loadingLayout = findViewById(R.id.progressBar1);
                loadingLayout.setVisibility(View.VISIBLE);
                app.getBus().post(new MiscEvents.UserRequest(""));
            }
            ViewSwitcher bottomTabSwitcher = (ViewSwitcher) findViewById(R.id.bottomTabSwitcher);
            int businessModelId = app.getDataSaver().getBusinessModel(Constants.BUSINESS_MODEL_ID);
            Log.d(TAG, "ON RESUME BUSINESS MODEL : " + businessModelId + " default business id : " + defaultBusinessModelId);

            if (defaultBusinessModelId != businessModelId) {
                Log.d(TAG, "* ON RESUME CHANGE TABS - initpagerviews ");
                initPagerViews(businessModelId);
                defaultBusinessModelId = businessModelId;
            }

            if (Constants.BUSINESS_MODEL_RESERVATION == businessModelId) {
                bottomTabSwitcher.setDisplayedChild(0);
            } else {
                bottomTabSwitcher.setDisplayedChild(1);
            }
        } else {
            //TOKEN NULL
            //TODO
            Log.d(TAG, "TOKEN NULL - USER NULL call login screen");
//            Intent intent = new Intent(this, LoginActivity.class);
//            startActivityForResult(intent, Constants.UPDATE_JOBS_PAGE);
//            RadioButton profileRadioButton = (RadioButton) findViewById(R.id.profileTab);
//            profileRadioButton.setChecked(true);
//            onRadioButtonClicked(profileRadioButton);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        //Handle notification click
        Log.d(TAG, "ON NEW INTENT calls handle notification");
        handleNotificationAndDeepLinks(true);
    }

    int jobIdForDelayedRedirection = 0;

    private void handleNotificationAndDeepLinks(boolean appWasAlive) {
        Bundle bundle = getIntent().getExtras();
        //This is notification
        if (bundle != null) {
            try {
//                for (String key : getIntent().getExtras().keySet()) {
//                    Object value = getIntent().getExtras().get(key);
//                    Log.d(TAG, "Key: " + key + " Value: " + value);
//                }
                Log.d(TAG, "BUNDLE IS NOT NULL: " + getIntent().getData());
                String action = bundle.getString("ACTION");
                int jobId = bundle.getInt(JobEvents.PARAM_JOB_ID);
                int jobQuoteId = bundle.getInt(JobEvents.PARAM_JOB_QUOTE_ID);
                Log.d(TAG, "ACTION: " + action + " - jobid: " + jobId + " - quoteid: " + jobQuoteId);
                if(appWasAlive){
                    redirectOutsideRequests(jobId);
                }else{
                    jobIdForDelayedRedirection = jobId;
                }

                app.clearNotificationGroup(jobId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            // THIS IS DEEP LINK
            Uri data = this.getIntent().getData();
            if (data != null && data.isHierarchical()) {
                Log.d(TAG, "BUNDLE IS NULL : " + getIntent().getData());
                handleDeepLinks(appWasAlive);
            }
        }
    }

    private void redirectOutsideRequests(int jobId) {
        if (jobWithSectionIdHashMap != null) {
            JobWithSectionId jobWithSectionId = jobWithSectionIdHashMap.get(jobId);
            if (jobWithSectionId != null) {
                jobIdForDelayedRedirection = 0;
                Log.d(TAG, "ITEM CLICKED EVENT sent to adapter from MainAct");
                app.getBus().post(new MiscEvents.ItemClickedRequest(this, jobWithSectionId.job, jobWithSectionId.sectionId));
            }
        }
//        else {
//            //Couldn't redirect do nothing for now
//        }
    }

    private void handleDeepLinks(boolean appWasAlive) {
        try {
            String uri = this.getIntent().getDataString().toLowerCase();
            Log.i("MyApp", "Deep link clicked " + uri);
            //http://178.211.57.11/hesap/teklifver_564461
            if (uri.contains("teklifver")) {
                String deepLinkText = uri.substring(uri.indexOf("_") + 1, uri.length()).trim();
                int jobId = Integer.parseInt(deepLinkText);
                Log.d(TAG, "Deep link clicked text: " + deepLinkText);
                if(appWasAlive){
                    redirectOutsideRequests(jobId);
                }else{
                    jobIdForDelayedRedirection = jobId;
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setTab(TabLayout tablayout, int index, int tabCount) {
        View v = LayoutInflater.from(this).inflate(R.layout.main_tab_layout, null);
        TextView jobNumber = (TextView) v.findViewById(R.id.jobNumber);
        TextView tabName = (TextView) v.findViewById(R.id.tabName);
        if (index == Constants.OPPORTUNITY_SECTION) {
            tabName.setText(getString(R.string.tab1));
        } else if (index == Constants.QUOTE_SECTION) {
            if (tabCount == TAB_COUNT_FOR_QUOTE) {
                tabName.setText(getString(R.string.tab2));
            } else {
                tabName.setText(getString(R.string.tab3));
            }
        } else if (index == Constants.DEALS_SECTION) {
            tabName.setText(getString(R.string.tab3));
        }
        //First Item as selected
        if (index == 0) {
            jobNumber.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.white));
            tabName.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.white));
        }
        tablayout.getTabAt(index).setCustomView(v);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_empty, menu);
        return false;
    }

    @Subscribe
    public void onNetworkError(ApiErrorEvent errorEvent) {
        Log.d(TAG, "MAIN ACT ON ERROR EVENT: " + errorEvent.getErrorMessage());
        View loadingLayout = findViewById(R.id.progressBar1);
        loadingLayout.setVisibility(View.GONE);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "ITEM ID: " + item.getItemId() + " - saveid: " + R.id.action_save);
        if (item.getItemId() == R.id.saveButton) {
            return false;
        }
        return false;
    }

    public void onRadioButtonClicked(View view) {
        //super.onRadioButtonClicked(view);
        boolean checked = ((RadioButton) view).isChecked();
        previousTab = selectedTab;
        TextView toolbarTitle = (TextView) findViewById(R.id.toolbar_title);
        ViewPager mViewPager = (ViewPager) findViewById(R.id.viewPager);
        final MainPageTabLayout tabLayout = (MainPageTabLayout) findViewById(R.id.tabs);
        switch (view.getId()) {
            case R.id.projectsTab:

                if (checked) {
                    toolbarTitle.setText(getString(R.string.tab1_projects));
                    if (app.isTokenPresent(TAG) == null) {
                        //mViewPager.setAdapter(new SectionsPagerAdapter(getSupportFragmentManager()));
                        int businessModel = app.getDataSaver().getBusinessModel(Constants.BUSINESS_MODEL_ID);
                        Log.d(TAG, "BUSINESS MODEL: " + businessModel);
                        initPagerViews(businessModel);
                    }
                    tabLayout.setVisibility(View.VISIBLE);
                    mViewPager.setVisibility(View.VISIBLE);
                    if (!activeFragmentTag.equals("")) {
                        BasicFragment fragment = (BasicFragment) getSupportFragmentManager().findFragmentByTag(activeFragmentTag);
                        Log.d(TAG, "DETACH FRAGMENT: " + activeFragmentTag);
                        FragmentTransaction fragmentTx = getSupportFragmentManager().beginTransaction();
                        activeFragmentTag = "";
                        //TODO remove animation
                        fragmentTx = setAnimation(1, fragmentTx); //previousTab > selectedTab ? 1 : 0
                        fragmentTx.remove(fragment).commit();
                    }
                    selectedTab = 1;
                    break;
                }
            case R.id.calendarTab:
                if (checked) {
                    toolbarTitle.setText(getString(R.string.tab2_calendar));
                    activeFragmentTag = "Takvim";
                    replaceFragment(false, null, CalendarFragment.newInstance());
                    tabLayout.setVisibility(View.GONE);
                    mViewPager.setVisibility(View.GONE);
                    selectedTab = 2;
                    break;
                }
            case R.id.profileTab:
                if (checked) {
                    toolbarTitle.setText(getString(R.string.tab3_profile));
                    activeFragmentTag = "User_Profile_Fragment";
                    //BasicFragment fragment = (BasicFragment) getSupportFragmentManager().findFragmentByTag(activeFragmentTag);
                    replaceFragment(false, null, UserProfileFragment.newInstance());
                    tabLayout.setVisibility(View.GONE);
                    mViewPager.setVisibility(View.GONE);
                    selectedTab = 3;
                    break;
                }
        }
    }

    @Override
    public void refreshJobCountTitle(int number, int sectionNo) {
        final MainPageTabLayout tabLayout = (MainPageTabLayout) findViewById(R.id.tabs);
//        if(sectionNo > 2){
//            return;
//        }
        try {
            TabLayout.Tab tab = tabLayout.getTabAt(sectionNo - 1);
            View v = tab.getCustomView();
            TextView jobNumber = (TextView) v.findViewById(R.id.jobNumber);
            jobNumber.setText(Integer.toString(number));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Subscribe
    public void onLoadUser(final MiscEvents.UserResponse event) {
        Log.d(TAG, "ON LOAD USER");
        if (event != null) {
            Response<User> userResponse = event.getResponse();
            if (userResponse != null) {
                User user = userResponse.body();
                if (user != null) {
                    Log.d(TAG, "USER SUCCESFULLY SAVED ");
                    app.setUser(user);
                    try {
                        String userId = user.getUserInfo().getUserId();
                        MixpanelAPI mixpanelAPI = MixpanelAPI.getInstance(this, getString(R.string.mpId));
                        mixpanelAPI.identify(userId);
                        mixpanelAPI.getPeople().identify(userId);
                        mixpanelAPI.getPeople().set("Email", userId);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    //TODO might be necassary
//                    app.getDataSaver().putString(Constants.USERNAME, user.getUserInfo().getUserId());
//                    app.getDataSaver().save();
                }
            }
        }
        View loadingLayout = findViewById(R.id.progressBar1);
        loadingLayout.setVisibility(View.GONE);
    }

    private FragmentTransaction setAnimation(int animDirection, FragmentTransaction fragmentTx) {
        if (animDirection == 0) { // || activeFragmentTag.equals("SUB_CATEGORY_LIST_FRAGMENT")
            fragmentTx.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_from_left, R.anim.enter_from_left, R.anim.exit_from_right);
        } else if (animDirection == 1) {
            fragmentTx.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_from_right, R.anim.enter_from_right, R.anim.exit_from_left);
        }
        return fragmentTx;
    }

    private void replaceFragment(final boolean addToBackStack, final BasicFragment fromFragment, final BasicFragment toFragment) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String tag = toFragment.TAG;
                FragmentTransaction fragmentTx = getSupportFragmentManager().beginTransaction();
                if (tag == null) {
                    tag = "";
                }
                activeFragmentTag = tag;
                //TODO animation
                fragmentTx = setAnimation(previousTab > selectedTab ? 1 : 0, fragmentTx);
                fragmentTx.replace(R.id.container, toFragment, tag);
//                if (addToBackStack) {
//                    fragmentTx.addToBackStack(tag);
//                    setBackNavigationEnabled(true);
//                }
                try {
                    fragmentTx.commit();
                } catch (IllegalStateException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    // This is also for userprofilefragment image upload to work
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "ON ACTIVItY RESULT: result code: " + resultCode + " -requestCode: " + requestCode);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.CHANGE_OPPORTUNITY_TO_QUOTE_PAGE || requestCode == Constants.UPDATE_JOBS_PAGE) {
                int businessModel = app.getDataSaver().getBusinessModel(Constants.BUSINESS_MODEL_ID);
                if (businessModel != Constants.BUSINESS_MODEL_RESERVATION) {
                    initPagerViews(Constants.BUSINESS_MODEL_REQUEST);
                }
            }
            String token = FirebaseInstanceId.getInstance().getToken();
            Log.d(TAG, "DEVICE TOKEN on activity result: " + token);
            app.getBus().post(new MiscEvents.PostDeviceTokenRequest(new DeviceToken(token)));
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Subscribe
    public void onDeviceTokenSent(MiscEvents.PostDeviceTokenResponse response) {
        Log.d(TAG, "On DEVICE TOKEN sent: ");
        if (response.getResponse() != null) {
            Log.d(TAG, "ON DEVICE TOKEN sent success");
            String deviceToken = response.getDeviceToken().getDeviceToken();
            Log.d(TAG, "On DEVICE TOKEN sent: " + deviceToken);
            if (deviceToken != null) {
                app.getDataSaver().putString("DEVICE_TOKEN", deviceToken);
                app.getDataSaver().save();
            }
        }
    }

    @Subscribe
    public void onDeviceTokenDeleted(MiscEvents.DeleteDeviceTokenResponse response) {
        Log.d(TAG, "On DEVICE TOKEN deleted: ");
        if (response.getResponse() != null) {
            Log.d(TAG, "ON DEVICE TOKEN deleted success");
        }
    }

    @Subscribe
    public void saveJobItems(MiscEvents.FillJobHashMapRequest fillJobHashMapRequest) {
        if (jobWithSectionIdHashMap == null) {
            jobWithSectionIdHashMap = new LongSparseArray<>();
        }
        if (fillJobHashMapRequest.jobArrayList != null) {
            for (Job job : fillJobHashMapRequest.jobArrayList) {
                jobWithSectionIdHashMap.put(job.getJobId(), new JobWithSectionId(fillJobHashMapRequest.sectionNumber, job));
            }
            if(jobIdForDelayedRedirection != 0){
                redirectOutsideRequests(jobIdForDelayedRedirection);
            }
        }
        //jobWithSectionIdHashMap.
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        Log.d(TAG, "ON REQUEST PERMISSON RESULT: " + requestCode);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Constants.PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the contacts-related task you need to do.
                    Log.d(TAG, "ON REQUEST PERMISSON GRANTED");
                    app.isLocationPremissionGranted = true;
                    app.getFusedLocationer().requestLocationInBackground();
                } else {
                    // permission denied, boo! Disable the functionality that depends on this permission.
                    Log.d(TAG, "ON REQUEST PERMISSON DENIED: display blocking");
                    app.isLocationPremissionGranted = false;
                    app.getBus().post(new MiscEvents.DisplayBlockingLocationPopupRequest());
                }

                app.getDataSaver().putBoolean(Constants.PERMISSION_KEY_REQUEST_LOCATION, app.isLocationPremissionGranted);
                app.getDataSaver().save();
                break;
            }
            case Constants.PERMISSIONS_WRITE_EXTERNAL_STORAGE: {
                break;
            }
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

        private final int TAB_COUNT;

        SectionsPagerAdapter(FragmentManager fm, int tabCount) {
            super(fm);
            TAB_COUNT = tabCount;
            notifyDataSetChanged();
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public Fragment getItem(int position) {
            Log.d(TAG, "INIT SECTIONS PAGER ADAPTER tab count: " + TAB_COUNT);
            if (TAB_COUNT == 3) {
                if (position == Constants.OPPORTUNITY_SECTION) {
                    return OpportunitiesFragment.newInstance(1);
                } else if (position == Constants.QUOTE_SECTION) {
                    return QuotesFragment.newInstance(2);
                } else if (position == Constants.DEALS_SECTION) {
                    return DealsFragment.newInstance(3);
                } else {
                    return null;
                }
            } else {
                if (position == Constants.OPPORTUNITY_SECTION) {
                    return OpportunitiesFragment.newInstance(1);
                } else if (position == Constants.DEALS_SECTION - 1) {
                    return DealsFragment.newInstance(3);
                } else {
                    return null;
                }
            }

        }

        @Override
        public int getCount() {
            return TAB_COUNT;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case Constants.OPPORTUNITY_SECTION:
                    return getString(R.string.tab1);
                case Constants.QUOTE_SECTION:
                    return getCount() == 3 ? getString(R.string.tab2) : getString(R.string.tab3);
                case Constants.DEALS_SECTION:
                    return getString(R.string.tab3);
            }
            return null;
        }
    }

    public class JobWithSectionId {
        int sectionId;
        public Job job;

        JobWithSectionId(int sectionId, Job job) {
            this.sectionId = sectionId;
            this.job = job;
        }
    }
}
