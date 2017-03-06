package com.armut.armuthv;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.RelativeSizeSpan;
import android.util.Base64;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.armut.armuthv.busevents.JobEvents;
import com.armut.armuthv.busevents.MiscEvents;
import com.armut.armuthv.fragments.BasicFragment;
import com.armut.armuthv.fragments.DialogUploadPhotoOrLocation;
import com.armut.armuthv.fragments.JobDetailFragment;
import com.armut.armuthv.fragments.JobMessagesFragment;
import com.armut.armuthv.objects.BasicNameValuePair;
import com.armut.armuthv.objects.JobDetails;
import com.armut.armuthv.objects.Message;
import com.armut.armuthv.services.LocationService;
import com.armut.armuthv.utils.ArmutUtils;
import com.armut.armuthv.utils.Constants;
import com.armut.armuthv.utils.PhotoSelectorHelper;
import com.armut.armuthv.utils.TimeUtils;
import com.google.gson.Gson;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.squareup.otto.Subscribe;

import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Timer;
import java.util.TimerTask;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class JobDetailActivity extends BaseActivity implements BasicFragment.LoadingProcess, PhotoSelectorHelper.PhotoSelector, JobDetailFragment.JobDetailsReceivedCallback, JobMessagesFragment.FullScrollCallback {

    private int requestCode = 0;
    private SectionsPagerAdapter mSectionsPagerAdapter;

    @Override
    protected void setTag() {
        TAG = "JobDetailActivity";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_detail_collapsable_layout);
        Bundle bundle = getIntent().getExtras();
        requestCode = bundle.getInt("requestCode");
        final long jobId = getIntent().getLongExtra(JobEvents.PARAM_JOB_ID, 0);
        final int sectionNumber = bundle.getInt(JobEvents.PARAM_SECTION_NUMBER, 1);
        //long serviceId = getIntent().getLongExtra(JobEvents.PARAM_SERVICE_ID, 0);
        final String customerUserId = bundle.getString(JobEvents.PARAM_CUSTOMER_ID);
        final String name = bundle.getString(JobEvents.PARAM_CUSTOMER_NAME);
        final String phone = bundle.getString(JobEvents.PARAM_CUSTOMER_PHONE);
        Log.d(TAG, "CUSTOMER NAME: " + name + " phone: " + phone);
        final String serviceName = bundle.getString(JobEvents.PARAM_SERVICE_NAME);
        final String city = bundle.getString(JobEvents.PARAM_JOB_CITY) + ", ";
        final String jobFullAddress = bundle.getString(JobEvents.PARAM_JOB_ADDRESS);
        final String date = bundle.getString(JobEvents.PARAM_JOB_DATE);
        final int canCall = bundle.getInt(JobEvents.PARAM_CAN_CALL);

        int jobQuoteId = getIntent().getIntExtra(JobEvents.PARAM_JOB_QUOTE_ID, 0);
        // jobIntent.putExtra(JobEvents.PARAM_PROVIDER_PROFILE_ID, event.getProfileId());
        //TextView jobIdTv = (TextView) findViewById(R.id.jobId);
        Button customerNameButton = (Button) findViewById(R.id.customerName);
        TextView customerNameTxt = (TextView) findViewById(R.id.customerNameTxt);
        final TextView callPrefTv = (TextView) findViewById(R.id.callPreference);
        final ViewSwitcher nameSwitcher = (ViewSwitcher) findViewById(R.id.customerNameSwitcher);
        final ProgressBar loadingBar = (ProgressBar) findViewById(R.id.loadingBar);
        customerNameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MixpanelAPI mixpanel = MixpanelAPI.getInstance(JobDetailActivity.this, getString(R.string.mpId));
                JSONObject object = null;
                if (canCall == Constants.CALL_PREF_CAN_CALL) {
                    object = ArmutUtils.getBasicJson(new BasicNameValuePair("Call Preference", "Can Call"));
                    ArmutUtils.callNumber(JobDetailActivity.this, 0 + phone);
                } else if (canCall == Constants.CALL_PREF_SECRET_NUMBER) {
                    //TODO test this
                    object = ArmutUtils.getBasicJson(new BasicNameValuePair("Call Preference", "Secret Number"));
                    app.getBus().post(new MiscEvents.GetAssignedPhoneNumberRequest(customerUserId, jobId));
                    loadingBar.setVisibility(View.VISIBLE);
                }
                mixpanel.track("Tapped Call Customer", object);
            }
        });
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (canCall != Constants.CALL_PREF_CAN_CALL) {
                    nameSwitcher.setDisplayedChild(1);
                    callPrefTv.setText(ArmutUtils.getCallPreferenceText(JobDetailActivity.this, canCall));
                } else {
                    nameSwitcher.setDisplayedChild(0);
                }
            }
        });
        TextView jobAddressTv = (TextView) findViewById(R.id.jobAddress);
        TextView jobDateTv = (TextView) findViewById(R.id.jobDate);
        TextView serviceNameTv = (TextView) findViewById(R.id.serviceName);
        TextView priceTv = (TextView) findViewById(R.id.price);
        final double quotePrice = bundle.getDouble(JobEvents.PARAM_JOB_QUOTE_PRICE);
        if (quotePrice > 0) {
            String priceText = ArmutUtils.getMoneyPatternWithoutCurrency(quotePrice) + " TL ";
            SpannableString spanPrice = new SpannableString(priceText);
            int titleLength = priceText.length() - 1;
            spanPrice.setSpan(new RelativeSizeSpan(0.65f), titleLength - 3, titleLength, 0);
            Log.d(TAG, "QUOTE PRICE: " + quotePrice);
            priceTv.setText(spanPrice);
        } else {
            priceTv.setVisibility(View.INVISIBLE);
        }
        //TODO
        //ImageView jobTypeIv = (ImageView) findViewById(R.id.jobTypeIv);
        // jobIdTv.setText(Long.toString(jobId));
        customerNameButton.setText(!TextUtils.isEmpty(name) ? name : getString(R.string.call));
        customerNameTxt.setText(name);
        jobAddressTv.setText(city);
        serviceNameTv.setText(serviceName);
        jobDateTv.setText(date);

        Toolbar mainToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mainToolbar);
        final ActionBar actionBar = getSupportActionBar();
        mainToolbar.setTitle("");
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle("");
        TextView toolbarTitle = (TextView) findViewById(R.id.toolbar_title);
        toolbarTitle.setText(getString(R.string.job_details));
        // Set up the ViewPager with the sections adapter.
        final ViewPager mViewPager = (ViewPager) findViewById(R.id.pager);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), jobId, jobQuoteId, sectionNumber, jobFullAddress);
        Log.d(TAG, "JOB_ID: " + jobId);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        Log.d(TAG, "GET REGISTERED FRAGMENT: " + mSectionsPagerAdapter.getRegisteredFragment(1));
        final TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        //tabLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.darkenBackground)); //blackoverlay
        tabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(this, R.color.kermit_green));
        tabLayout.setTabTextColors(ContextCompat.getColor(this, R.color.gray2), ContextCompat.getColor(this, R.color.white));
        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                tabLayout.setupWithViewPager(mViewPager);
            }
        });
        //final AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        final EditText editText = (EditText) findViewById(R.id.editText);
        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Log.d(TAG, "Edit Text click");
//                appBarLayout.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        appBarLayout.setExpanded(false);
//                        Log.d(TAG, "EDIT TEXT CLICKED: set expanded false ");
//                        JobMessagesFragment messagesFragment = null;
//                        if (mSectionsPagerAdapter.getRegisteredFragment(1) instanceof JobMessagesFragment) {
//                            messagesFragment = (JobMessagesFragment) mSectionsPagerAdapter.getRegisteredFragment(1);
//                        }
//                        if (messagesFragment != null) {
//                            messagesFragment.editTextClicked();
//                        }
//                        //app.bus.post(new EventBusEditTextClickedObject());
//                    }
//                }, 350);
                scrollToBottom(true);
            }
        });
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (editText.hasFocus()) {
                    editText.performClick();
                }
            }
        });

        FloatingActionButton button = (FloatingActionButton) findViewById(R.id.sendButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage(editText, Message.MESSAGE_TYPE_TEXT, editText.getText().toString().trim());
            }
        });

        ImageView attachButton = (ImageView) findViewById(R.id.attachButton);
        attachButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAttachmentMenu();
            }
        });

        final LinearLayout editTextRelativeLayout = (LinearLayout) findViewById(R.id.editTextRelativeLayout);
        editTextRelativeLayout.setVisibility(View.GONE);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Log.d(TAG, "ON PAGE SELECTED");
                if (position == 1) {
                    editTextRelativeLayout.setVisibility(View.VISIBLE);
                } else {
                    editTextRelativeLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mViewPager.setOffscreenPageLimit(2);
        int businessModel = app.getDataSaver().getBusinessModel(Constants.BUSINESS_MODEL_ID);
        if (businessModel == Constants.BUSINESS_MODEL_REQUEST){
            mViewPager.setCurrentItem(1);
        }else{
            mViewPager.setCurrentItem(0);
        }
    }

    @Subscribe
    public void onAssignedNumberReceived(MiscEvents.GetAssignedPhoneNumberResponse response) {
        final ProgressBar loadingBar = (ProgressBar) findViewById(R.id.loadingBar);
        loadingBar.setVisibility(View.GONE);
        if (response != null) {
            try {
                String jobTxt = response.getResponse().body().string();
                Log.d(TAG, "RECEIVED NUMBER: " + jobTxt);
                JSONObject json = new JSONObject(jobTxt);
                String phone = json.getString("phone_number");
                ArmutUtils.callNumber(this, 0 + phone);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void sendMessage(final EditText editText, int type, String txt) {
        int jobQuoteId = getIntent().getIntExtra(JobEvents.PARAM_JOB_QUOTE_ID, 0);
        if (txt.equals("")) {
            return;
        }
        final AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        appBarLayout.setExpanded(false);
        Message message = new Message();
        String myUserId = app.getUserId();
        message.setUserId(myUserId); // app.me.getUserId()
        message.setMessage(txt);
        try {
            message.setTime(TimeUtils.convertDateTimeFormat(TimeUtils.updateDateFormat, TimeUtils.dfISOMS, TimeUtils.getTodayJoda(false)));
        } catch (Exception e) {
            message.setTime(TimeUtils.getTodayJoda(false));
        }
        message.setIsUser(true);
        message.setJobQuotesId(jobQuoteId);
        message.setContentType(type);
        Gson gson = new Gson();
        Log.d(TAG, "JSON: " + gson.toJson(message));
        if (jobQuoteId != 0) {
            app.getBus().post(new MiscEvents.PostMessageRequest(message, editText));
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onPause() {
        app.getBus().unregister(this);
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        app.getBus().register(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home || id == R.id.home) {
            onBackPressed();
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            //If opportunity is turned into a quote, we should refresh job lists whenever this screen gets closed
            int requestCode = getIntent().getExtras().getInt("requestCode");
            if (requestCode == Constants.CHANGE_OPPORTUNITY_TO_QUOTE_PAGE) {
                setResult(Activity.RESULT_OK);
            }
            finish();
            MainActivity.setBackwardsTranslateAnimation(this);
        }
    }

    @Override
    public void loadingIsInProgress(boolean isLoading) {

    }

    private PhotoSelectorHelper helper;

    @Override
    public void uploadMethodSelected(int status) {
        Log.d(TAG, "UPLOAD METHOD SELECTED : " + status);
        //if (attached && getActivity() != null) {
        //Log.d(TAG, "HELPER CREATED for photo upload");
        switch (status) {
            case PhotoSelectorHelper.REQ_CAMERA:
                helper = new PhotoSelectorHelper(this, null);
                helper.takePicture();
                break;
            case PhotoSelectorHelper.REQ_PICK_IMAGE:
                helper = new PhotoSelectorHelper(this, null);
                helper.openGallery();
                break;
            case PhotoSelectorHelper.ADD_LOCATION:
                LocationService locationService = new LocationService(app);
                double[] latlon = null;
                try {
                    latlon = locationService.getLatLong(JobDetailActivity.this, this);
                } catch (SecurityException ex) {
                    ex.printStackTrace();
                }
                if (latlon == null) {
                    locationService.openLocationDialog(this);
                    return;
                } else if (latlon[0] == -1 || latlon[1] == -1) {
                    locationService.openLocationDialog(this);
                    return;
                }
                String latlonTxt = latlon[0] + "," + latlon[1];
                sendMessage(null, Message.MESSAGE_TYPE_LOCATION, latlonTxt);
                //sendMessageClicked(new EventBusSendMessageObject(latlonTxt, Message.MESSAGE_TYPE_LOCATION, null));
                break;
        }
    }

    public void openAttachmentMenu() {
        Log.d(TAG, "OPEN ATTACHMENT MENU");
        if (app.requestCardWritePermission(this, getString(R.string.warning_photo_upload_while_messaging))) {
            final DialogUploadPhotoOrLocation uploadDialog = DialogUploadPhotoOrLocation.newInstance();
            //uploadDialog.setTargetFragment(UserProfileFragment.this, Constants.UPLOAD_PHOTO_DIALOG);
            uploadDialog.show(JobDetailActivity.this.getSupportFragmentManager(), "uploadDialog");
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (openPhotoDialog) {
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    // EXECUTE ACTIONS (LIKE FRAGMENT TRANSACTION ETC.)
                    final DialogUploadPhotoOrLocation uploadDialog = DialogUploadPhotoOrLocation.newInstance();
                    //uploadDialog.setTargetFragment(UserProfileFragment.this, Constants.UPLOAD_PHOTO_DIALOG);
                    uploadDialog.show(JobDetailActivity.this.getSupportFragmentManager(), "uploadDialog");
                }
            }, 100);
            openPhotoDialog = false;
        }
    }

    boolean openPhotoDialog = false;

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case Constants.PERMISSIONS_WRITE_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the contacts-related task you need to do.
                    app.isWriteToSdCardPermissionGranted = true;
                    openPhotoDialog = true;
                } else {
                    // permission denied, boo! Disable the functionality that depends on this permission.
                    app.isWriteToSdCardPermissionGranted = false;
                }
                app.getDataSaver().putBoolean(Constants.PERMISSION_KEY_WRITE_EXTERNAL_STORAGE, app.isWriteToSdCardPermissionGranted);
                app.getDataSaver().save();
                break;
            }
            case Constants.PERMISSIONS_CALL: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the contacts-related task you need to do.
                    app.isCallPermissionGranted = true;
                    ArmutUtils.callArmut(JobDetailActivity.this);
                } else {
                    // permission denied, boo! Disable the functionality that depends on this permission.
                    app.isCallPermissionGranted = false;
                }
                app.getDataSaver().putBoolean(Constants.PERMISSION_KEY_CALL, app.isCallPermissionGranted);
                app.getDataSaver().save();
                break;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_empty, menu);
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "REQUEST CODE: " + requestCode);
        Log.d(TAG, "RESULT CODE: " + resultCode);
        if (resultCode != Activity.RESULT_OK) {
            //TODO handle error
            return;
        }
        switch (requestCode) {
            case PhotoSelectorHelper.REQ_PICK_IMAGE:
                try {
                    InputStream inputStream = getContentResolver().openInputStream(data.getData());
                    FileOutputStream fileOutputStream = new FileOutputStream(helper.createImageFile());
                    PhotoSelectorHelper.copyStream(inputStream, fileOutputStream);
                    fileOutputStream.close();
                    inputStream.close();
                } catch (Exception e) {
                    Log.e(TAG, "Error while creating temp file", e);
                }
                break;
            case PhotoSelectorHelper.REQ_CAMERA:
                break;
            case Constants.LOCATION_AVAILABLE:
                Log.d(TAG, "TRY TO SEND LOCATION AGAIN");
                break;
        }

        if (requestCode == PhotoSelectorHelper.REQ_CAMERA || requestCode == PhotoSelectorHelper.REQ_PICK_IMAGE) {
            Log.d(TAG, "SEND PHOTO - REQUEST CODE : " + requestCode);
            if (helper != null) {
                byte[] imageEncoded = helper.initPhotoForUpload();
                if (imageEncoded != null) {
                    String encodedImage = Base64.encodeToString(imageEncoded, Base64.DEFAULT);
                    sendMessage(null, Message.MESSAGE_TYPE_IMAGE, encodedImage);
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(JobDetailActivity.this, "Fotoğraf yüklenemedi, lütfen daha sonra tekrar deneyin.", Toast.LENGTH_LONG).show();
                        }
                    });
                }
//                Log.d(TAG, "ON ACTIVITY RESULT IN FRAGMENT : START PHOTO UPLOAD TASK");
//                //loadingLayout.setVisibility(View.VISIBLE);
            }
            return;
        }
        Log.d(TAG, "ON ACTIVITY RESULT IN FRAGMENT : " + requestCode);
        super.onActivityResult(requestCode, resultCode, data);
//        if (userUpdatePart != Constants.UPDATE_USER_PROFILE_PAGE) {
//            useLoader();
//        } else {
//            fillUserInfo(rootView, getActivity().getAssets());
//            //addCreditCardData();
//            //updateProfileInfo(me.getUserInfo());
//            switcher.setDisplayedChild(1);
//        }
    }

    @Override
    public void onJobDetailsReceived(JobDetails details) {

    }

    @Override
    public void onJobStartDateTypeCalculated(String[] typeDateArray) {

    }

    @Override
    public void scrollToBottom(final boolean fullScroll) {
        final AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        //Log.d(TAG, "Edit Text click");
        appBarLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (fullScroll) {
                    appBarLayout.setExpanded(false);
                }
                Log.d(TAG, "EDIT TEXT CLICKED: set expanded false ");
                JobMessagesFragment messagesFragment = null;
                if (mSectionsPagerAdapter.getRegisteredFragment(1) instanceof JobMessagesFragment) {
                    messagesFragment = (JobMessagesFragment) mSectionsPagerAdapter.getRegisteredFragment(1);
                }
                if (messagesFragment != null) {
                    messagesFragment.editTextClicked();
                }
                //app.bus.post(new EventBusEditTextClickedObject());
            }
        }, 350);
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private final long jobId;
        private final int jobQuoteId;
        private final String jobFullAddress;
        private final static int TAB_COUNT = 2;
        private final int sectionNumber;
        private final SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();
        boolean jobHasAddressDetails;

        SectionsPagerAdapter(FragmentManager fm, long jobId, int jobQuoteId, int sectionNumber, String jobFullAddress) {
            super(fm);
            this.jobId = jobId;
            this.jobQuoteId = jobQuoteId;
            this.sectionNumber = sectionNumber;
            this.jobFullAddress = jobFullAddress;
            Bundle bundle = getIntent().getExtras();
            jobHasAddressDetails = bundle.getBoolean(JobEvents.PARAM_JOB_HAS_ADDRESS_DETAILS, false);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Log.d(TAG, "GET ITEM POSITOION" + position + " - jobid: " + jobId + " color: " + this.color);
            if (position == 0) {

                return JobDetailFragment.newInstance(jobId, sectionNumber, jobFullAddress, jobHasAddressDetails);
            } else {
                return JobMessagesFragment.newInstance(jobQuoteId);//, imagePath
            }
        }

        @Override
        public int getCount() {
            return TAB_COUNT;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            //Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.tab_details);
                case 1:
                    return getString(R.string.tab_messages);
            }
            return null;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment fragment = (Fragment) super.instantiateItem(container, position);
            registeredFragments.put(position, fragment);
            return fragment;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            registeredFragments.remove(position);
            super.destroyItem(container, position, object);
        }

        Fragment getRegisteredFragment(int position) {
            return registeredFragments.get(position);
        }
    }
}
