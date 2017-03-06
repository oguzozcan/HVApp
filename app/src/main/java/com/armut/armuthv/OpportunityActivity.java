package com.armut.armuthv;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ViewSwitcher;
import com.armut.armuthv.busevents.JobEvents;
import com.armut.armuthv.components.AppBarStateChangeListener;
import com.armut.armuthv.fragments.BasicFragment;
import com.armut.armuthv.fragments.DialogCancelJob;
import com.armut.armuthv.fragments.JobDetailFragment;
import com.armut.armuthv.objects.BasicNameValuePair;
import com.armut.armuthv.objects.JobCancelReason;
import com.armut.armuthv.objects.JobDetails;
import com.armut.armuthv.objects.OpportunityCancelReason;
import com.armut.armuthv.objects.QuoteLead;
import com.armut.armuthv.utils.ArmutUtils;
import com.armut.armuthv.utils.Constants;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.squareup.otto.Subscribe;

import org.json.JSONObject;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class OpportunityActivity extends BaseActivity implements BasicFragment.LoadingProcess, JobDetailFragment.JobDetailsReceivedCallback, DialogCancelJob.CancelJobCallback {

    private String[] jobTypeDateArray;

    @Override
    protected void setTag() {
        TAG = "OpportunityActivity";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opportunity);
        View progressBarLayout = findViewById(R.id.progressBar1);
        progressBarLayout.setVisibility(View.VISIBLE);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView toolbarTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbarTitle.setText(getString(R.string.tab1));
        Bundle bundle = getIntent().getExtras();
        JobDetailFragment fragment = JobDetailFragment.newInstance(bundle.getLong(JobEvents.PARAM_JOB_ID), 1, bundle.getString(JobEvents.PARAM_JOB_ADDRESS, ""), false);

        if (savedInstanceState == null) {
            FragmentTransaction fragmentTx = getSupportFragmentManager().beginTransaction();
            fragmentTx.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_from_left, R.anim.enter_from_left, R.anim.exit_from_right);
            fragmentTx.replace(R.id.container, fragment, "jobdetails");
            fragmentTx.commit();
        }
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        final CardView giveQuoteButton = (CardView) findViewById(R.id.giveQuoteButton);
        final Animation fadeOut = AnimationUtils.loadAnimation(OpportunityActivity.this, R.anim.fade_out);
        final Animation fadeIn = AnimationUtils.loadAnimation(OpportunityActivity.this, R.anim.fade_in);
        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                giveQuoteButton.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        fadeIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                giveQuoteButton.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        appBarLayout.addOnOffsetChangedListener(new AppBarStateChangeListener(this) {
            @Override
            public void onStateChanged(AppBarLayout appBarLayout, State state) {
                //Log.d("STATE", state.name());
                if (state.equals(State.COLLAPSED)) {
                    if (giveQuoteButton.getVisibility() == View.VISIBLE)
                        giveQuoteButton.startAnimation(fadeOut);
                    //giveQuoteButton.setVisibility(View.INVISIBLE);
                } else {
                    if (giveQuoteButton.getVisibility() == View.INVISIBLE)
                        giveQuoteButton.startAnimation(fadeIn);
                    //giveQuoteButton.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_empty, menu);
        return true;
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onPause() {
        try{
            app.getBus().unregister(this);
        }catch (Exception e){
            e.printStackTrace();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        app.getBus().register(this);
        long jobId = getIntent().getExtras().getLong(JobEvents.PARAM_JOB_ID);
        long serviceId = getIntent().getExtras().getLong(JobEvents.PARAM_SERVICE_ID);
        Log.d(TAG, "JOB_ID : " + jobId + " - serviceId: " + serviceId);
        app.getBus().post(new JobEvents.QuoteLeadRequest(jobId, serviceId));
        loadingIsInProgress(true);
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
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            finish();
            MainActivity.setBackwardsTranslateAnimation(this);
        }
    }

    @Override
    public void loadingIsInProgress(boolean isLoading) {
        ScrollView sv = (ScrollView) findViewById(R.id.scroll);
        View progressBarLayout = findViewById(R.id.progressBar1);
        if (sv != null) {
            if (isLoading) {
                sv.setVisibility(View.VISIBLE);
                progressBarLayout.setVisibility(View.VISIBLE);
            } else {
                sv.setVisibility(View.GONE);
                progressBarLayout.setVisibility(View.GONE);
            }
            sv.setEnabled(false);
        }
    }

    @Override
    public void startJobCancelTask(OpportunityCancelReason reason) {
        if (reason != null) {
            loadingIsInProgress(true);
            app.getBus().post(new JobEvents.PostRejectOpportunityRequest(new JobCancelReason(reason.getReasonId(), reason.getReason(), "", 0), getIntent().getExtras().getLong(JobEvents.PARAM_JOB_ID)));
            try{
                MixpanelAPI mixpanel = MixpanelAPI.getInstance(OpportunityActivity.this, OpportunityActivity.this.getString(R.string.mpId));
                JSONObject object = ArmutUtils.getBasicJson(new BasicNameValuePair("Reason", reason.getReason()));
                mixpanel.track("Tapped Not Interested", object);
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }
    String callPrefText;

    @Override
    public void onJobDetailsReceived(JobDetails details) {
        if (details != null) {
            //0 stands for
            int model = details.getBusinessModel();
            Bundle bundle = getIntent().getExtras();
            ViewSwitcher modelSwitcher = (ViewSwitcher) findViewById(R.id.modelSwitcher);
            String serviceName = bundle.getString(JobEvents.PARAM_SERVICE_NAME);
            String jobDate = bundle.getString(JobEvents.PARAM_JOB_DATE);
            TextView jobDateTv;
            TextView serviceNameTv;
            TextView urgentTv;
            TextView privateTv;

            if (model == Constants.BUSINESS_MODEL_RESERVATION) {
                modelSwitcher.setDisplayedChild(0);
                String name = bundle.getString(JobEvents.PARAM_CUSTOMER_NAME);
                String address = bundle.getString(JobEvents.PARAM_JOB_ADDRESS);
                TextView customerNameTv = (TextView) findViewById(R.id.customerName);
                TextView jobAddressTv = (TextView) findViewById(R.id.jobAddress);
                jobDateTv = (TextView) findViewById(R.id.jobDate);
                serviceNameTv = (TextView) findViewById(R.id.serviceName);
                privateTv = (TextView) findViewById(R.id.privateJobTv);
                urgentTv = (TextView) findViewById(R.id.urgentJobTv);
                customerNameTv.setText(name);
                jobAddressTv.setText(address);
                jobDateTv.setText(jobDate); //TimeUtils.convertDateTimeFormat(TimeUtils.dfISO, TimeUtils.dtfOut, jobDate)
            } else {
                modelSwitcher.setDisplayedChild(1);
                TextView callPreferenceTv = (TextView) findViewById(R.id.callPreferenceLead);
                String city = bundle.getString(JobEvents.PARAM_JOB_CITY);
                jobDateTv = (TextView) findViewById(R.id.jobDateLead);
                serviceNameTv = (TextView) findViewById(R.id.serviceNameLead);
                privateTv = (TextView) findViewById(R.id.privateJobTvLead);
                urgentTv = (TextView) findViewById(R.id.urgentJobTvLead);
                callPrefText = ArmutUtils.getCallPreferenceText(this, details.getCallPreference());
                callPreferenceTv.setText(callPrefText);
                //receivedQuoteNumberTv.setText();
                jobDateTv.setText(city + ", " + jobDate); //TimeUtils.convertDateTimeFormat(TimeUtils.dfISO, TimeUtils.dtfOut, jobDate)
            }

            boolean isUrgent = bundle.getBoolean(JobEvents.PARAM_URGENT_JOB);
            boolean isPrivate = bundle.getBoolean(JobEvents.PARAM_PRIVATE_JOB);
            Log.d(TAG, "Service Name : " + serviceName + " - date: " + jobDate + " - isPrivate : " + isPrivate + " - isUrgent: " + isUrgent);
            if(isUrgent){
                urgentTv.setVisibility(View.VISIBLE);
            }else{
                urgentTv.setVisibility(View.GONE);
            }
            if(isPrivate){
                privateTv.setVisibility(View.VISIBLE);
            }else{
                privateTv.setVisibility(View.GONE);
            }
            serviceNameTv.setText(serviceName);
        }
    }

    @Override
    public void onJobStartDateTypeCalculated(String[] typeDateArray) {
        jobTypeDateArray = null;
        if (typeDateArray != null) {
            Log.d(TAG, "JOB DATE TYPE CALCULATED");
            jobTypeDateArray = typeDateArray;
        }
    }

    @Subscribe
    public void onJobCanceled(JobEvents.PostRejectOpportunityResponse response) {
        loadingIsInProgress(false);
        if (response != null) {
            if (response.getRejectOpportunityResponse() != null)
                if (response.getRejectOpportunityResponse().isSuccessful()) {
                    Log.d(TAG, "RESPONSE: " + response.getRejectOpportunityResponse().body());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setResult(Activity.RESULT_OK);
                            finish();
                        }
                    });
                }
        }
    }

    @Subscribe
    public void onLoadLeadPrice(JobEvents.QuoteLeadResponse response) {
        Log.d(TAG, "ON LOAD PRICE response : " + response.toString());
        if (response.getQuoteLead() != null) {
            final QuoteLead lead = response.getQuoteLead().body();
            final CardView giveQuoteButton = (CardView) findViewById(R.id.giveQuoteButton);
            final TextView selectQuoteText = (TextView) findViewById(R.id.selectQuoteText);
            final Double leadPrice = lead.getLeadPrice();
            String giveQuoteTxt = getString(R.string.give_quote);
            if (leadPrice != null) {
                giveQuoteTxt += " " + leadPrice + " TL ";
                SpannableString spanPrice = new SpannableString(giveQuoteTxt);
                int titleLength = giveQuoteTxt.length() - 1;
                spanPrice.setSpan(new RelativeSizeSpan(0.65f), titleLength - 3, titleLength, 0);
                Log.d(TAG, "LEAD PRICE: " + leadPrice);
                selectQuoteText.setText(spanPrice);
            }
            if (giveQuoteButton != null) {
                giveQuoteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Bundle bundle = getIntent().getExtras();
                        Intent jobIntent = new Intent(OpportunityActivity.this, GiveQuoteActivity.class);
                        jobIntent.putExtra(JobEvents.PARAM_JOB_ID, bundle.getLong(JobEvents.PARAM_JOB_ID));
                        jobIntent.putExtra(JobEvents.PARAM_LEAD_PRICE, lead.isLeadRequired() ? leadPrice : null);
                        jobIntent.putExtra(JobEvents.PARAM_PROVIDER_PROFILE_ID, bundle.getLong(JobEvents.PARAM_PROVIDER_PROFILE_ID));
                        BasicNameValuePair timePair = null;
                        if (jobTypeDateArray != null) {
                            jobIntent.putExtra(JobEvents.PARAM_JOB_START_DATE_TYPE, jobTypeDateArray[0]);
                            jobIntent.putExtra(JobEvents.PARAM_JOB_START_DATE, jobTypeDateArray[1]);
                            Log.d(TAG, "JOB START DATE TYPE: " + jobTypeDateArray[0] + " - jobdate: " + jobTypeDateArray[1]);
                            timePair = ArmutUtils.getJobStartDatePair(OpportunityActivity.this, jobTypeDateArray[1], Integer.parseInt(jobTypeDateArray[0]));
                        }
                        try{
                            MixpanelAPI mixpanel = MixpanelAPI.getInstance(OpportunityActivity.this, OpportunityActivity.this.getString(R.string.mpId));
                            String leadTxt = "0";
                            if(leadPrice != null){
                                leadTxt = String.valueOf(leadPrice);
                            }
                            JSONObject object = ArmutUtils.getBasicJson(new BasicNameValuePair("Lead Price", leadTxt),
                                    new BasicNameValuePair("Call Preference", callPrefText),timePair);
                            mixpanel.track("Tapped Give A Quote", object);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        Log.d(TAG, "PROFILE ID OPPORTUNITIES :  " + bundle.getLong(JobEvents.PARAM_PROVIDER_PROFILE_ID));
                        //jobIntent.putExtra(JobEvents.PARAM_JOB_ADDRESS, addressTxt);
                        startActivityForResult(jobIntent, Constants.UPDATE_JOBS_PAGE);
                        BaseActivity.setTranslateAnimation(OpportunityActivity.this);
                    }
                });
            }
        }
        loadingIsInProgress(false);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "REQUEST CODE: " + requestCode);
        Log.d(TAG, "RESULT CODE: " + resultCode);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == Constants.UPDATE_JOBS_PAGE) {
            Bundle bundle = getIntent().getExtras();
            Intent jobIntent = new Intent(OpportunityActivity.this, JobDetailActivity.class);
            long jobId = bundle.getLong(JobEvents.PARAM_JOB_ID);
            long serviceId = bundle.getLong(JobEvents.PARAM_SERVICE_ID);
            int quoteId = data.getExtras().getInt(JobEvents.PARAM_JOB_QUOTE_ID);
            jobIntent.putExtra(JobEvents.PARAM_JOB_QUOTE_ID, quoteId);
            jobIntent.putExtra(JobEvents.PARAM_CUSTOMER_PHONE, bundle.getString(JobEvents.PARAM_CUSTOMER_PHONE));
            String jobDate = bundle.getString(JobEvents.PARAM_JOB_DATE);
            //String date = TimeUtils.convertDateTimeFormat(TimeUtils.dfISOMS, TimeUtils.dtfOutWOYear, jobDate);
            jobIntent.putExtra(JobEvents.PARAM_SECTION_NUMBER, 2);
            jobIntent.putExtra(JobEvents.PARAM_JOB_ID, jobId);
            jobIntent.putExtra(JobEvents.PARAM_CUSTOMER_ID, bundle.getString(JobEvents.PARAM_CUSTOMER_ID));
            jobIntent.putExtra(JobEvents.PARAM_SERVICE_ID, serviceId);
            jobIntent.putExtra(JobEvents.PARAM_SERVICE_NAME, bundle.getString(JobEvents.PARAM_SERVICE_NAME));
            jobIntent.putExtra(JobEvents.PARAM_CUSTOMER_NAME, bundle.getString(JobEvents.PARAM_CUSTOMER_NAME));

            jobIntent.putExtra(JobEvents.PARAM_JOB_QUOTE_PRICE, data.getExtras().getDouble(JobEvents.PARAM_JOB_QUOTE_PRICE));
            //jobIntent.putExtra(JobEvents.PARAM_QUOTE_REMAINING_TIME, event.getJobQuoteExpireDate());
            long providerProfileId = bundle.getLong(JobEvents.PARAM_PROVIDER_PROFILE_ID);
            jobIntent.putExtra(JobEvents.PARAM_PROVIDER_PROFILE_ID, providerProfileId);
            Log.d(TAG, "PROVIDER PROFILE ID: " + providerProfileId + " - sectionNumber : " + 2 + "EVENT DATE: " + jobDate + "- quoteId " + quoteId);
            jobIntent.putExtra(JobEvents.PARAM_JOB_DATE, jobDate);
            String city = bundle.getString(JobEvents.PARAM_JOB_CITY);
            //String state = bundle.getString(JobEvents.Param_J)
            // String addressTxt = event.getJobDistrict() + " " + event.getAddress() + "\n" + city + ", " + event.getJobState();
            jobIntent.putExtra(JobEvents.PARAM_JOB_ADDRESS, bundle.getString(JobEvents.PARAM_JOB_ADDRESS, ""));
            jobIntent.putExtra(JobEvents.PARAM_JOB_CITY, city);
            jobIntent.putExtra(JobEvents.PARAM_CAN_CALL, bundle.getInt(JobEvents.PARAM_CAN_CALL));
            jobIntent.putExtra("requestCode", Constants.CHANGE_OPPORTUNITY_TO_QUOTE_PAGE);
            startActivityForResult(jobIntent, Constants.CHANGE_OPPORTUNITY_TO_QUOTE_PAGE);
            BaseActivity.setTranslateAnimation(this);
            //TODO finish this as well
            setResult(Activity.RESULT_OK);
            finish();
        }
    }
}

