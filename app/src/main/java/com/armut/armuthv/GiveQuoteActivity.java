package com.armut.armuthv;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.adjust.sdk.Adjust;
import com.adjust.sdk.AdjustEvent;
import com.armut.armuthv.busevents.JobEvents;
import com.armut.armuthv.components.DatePickerButton;
import com.armut.armuthv.objects.BasicNameValuePair;
import com.armut.armuthv.objects.CommissionRate;
import com.armut.armuthv.objects.JobQuote;
import com.armut.armuthv.utils.ArmutUtils;
import com.armut.armuthv.utils.Constants;
import com.armut.armuthv.utils.TimeUtils;
import com.google.gson.Gson;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.squareup.otto.Subscribe;

import org.json.JSONObject;

import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class GiveQuoteActivity extends BaseActivity {

    private Timer timer;
    private final long DELAY = 700; // in ms
    private long jobId;
    private boolean isLeadRequired;
    private int selectedJobStartDateTypeId;

    @Override
    protected void setTag() {
        TAG = "Teklif Ver";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_give_quote);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView toolbarTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbarTitle.setText(getString(R.string.give_quote_lowercase));
    }

    @Override
    public void onStart() {
        super.onStart();
        Bundle bundle = getIntent().getExtras();
        jobId = bundle.getLong(JobEvents.PARAM_JOB_ID);
        final double leadPrice = bundle.getDouble(JobEvents.PARAM_LEAD_PRICE);
        final long providerProfileId = bundle.getLong(JobEvents.PARAM_PROVIDER_PROFILE_ID);
        final String jobStartDate = bundle.getString(JobEvents.PARAM_JOB_START_DATE);
        final String jobStartDateType = bundle.getString(JobEvents.PARAM_JOB_START_DATE_TYPE);
        Log.d(TAG, "JOB_ID : " + jobId + " leadPrice: " + leadPrice);
        //final NumberSelector teamSizeSelector = (NumberSelector) findViewById(R.id.teamSizeSelector);
        //teamSizeSelector.setValues(new String[]{"0" , "1", "2"});
        final DatePickerButton datePicker = (DatePickerButton) findViewById(R.id.datePickerButton);
        //This button get visible only user select the specific date radiobutton
        LinearLayout dateTimeLayoutSelectLayout = (LinearLayout) findViewById(R.id.dateTimeSelectLayout);
        dateTimeLayoutSelectLayout.setVisibility(View.GONE);

        if (!TextUtils.isEmpty(jobStartDateType)) {
            Log.d(TAG, "JOB START DATE TYPE: " + jobStartDateType + " - jobdate: " + jobStartDate);
            RadioButton specificDateButton = (RadioButton) findViewById(R.id.specificDateButton);
            RadioButton twoMonthsButton = (RadioButton) findViewById(R.id.twoMonthsButton);
            RadioButton sixMonthsButton = (RadioButton) findViewById(R.id.sixMonthsButton);
            int type = Integer.parseInt(jobStartDateType);
            selectedJobStartDateTypeId = type;
            if (!TextUtils.isEmpty(jobStartDate)) {
                //Log.d(TAG, "JOB START DATE: " + jobStartDate + " - jobStartDateFormatted: " + TimeUtils.convertDateTimeFormat(TimeUtils.dtfOut, TimeUtils.updateDateFormat, jobStartDate));
                datePicker.setText(TimeUtils.convertDateTimeFormat(TimeUtils.updateDateFormat, TimeUtils.dtfOut, jobStartDate));
            }
            if (type == Constants.JOB_START_DATE_TYPE_SPECIFIC_TIME) {
                specificDateButton.setChecked(true);
                dateTimeLayoutSelectLayout.setVisibility(View.VISIBLE);
            } else if (type == Constants.JOB_START_DATE_TYPE_SIX_MONTHS || type == Constants.JOB_START_DATE_TYPE_LOOKING_PRICE) {
                sixMonthsButton.setChecked(true);
            } else if (type == Constants.JOB_START_DATE_TYPE_TWO_MONTHS) {
                twoMonthsButton.setChecked(true);
            }
        }
        final EditText quotePriceEt = (EditText) findViewById(R.id.quotePrice);
        isLeadRequired = leadPrice != 0;
        datePicker.setActivity(this);
        initSearch(quotePriceEt);
        Button giveQuoteButton = (Button) findViewById(R.id.giveQuoteButton);
        giveQuoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String priceTxt = quotePriceEt.getText().toString();
                String date = datePicker.getText().toString();
                String formattedStartDate = getStartDateIfValid(date);
                if(isPriceValid(priceTxt, quotePriceEt) && formattedStartDate != null){
                    JobQuote quote = new JobQuote();
                    quote.setJobId(jobId);
                    quote.setPrice(Double.parseDouble(priceTxt));
                    quote.setStartDate(formattedStartDate);
                    quote.setProviderProfileId(providerProfileId);
                    app.getBus().post(new JobEvents.PostQuoteRequest(quote));
                    Gson gson = new Gson();
                    Log.d(TAG, "JSON : " + gson.toJson(quote));
                    Log.d(TAG, "Price: " + priceTxt + "- profileId: " + providerProfileId + " - jobId : " + jobId);
                    Log.d(TAG, "ConvertedTime : " + formattedStartDate);
                }
            }
        });

        String giveQuoteText = getString(R.string.give_quote);
        if (leadPrice != 0) {
            giveQuoteText += " ( " + leadPrice + " TL )";
        }
        giveQuoteButton.setText(giveQuoteText);
    }

    private boolean isPriceValid(String text, EditText quotePriceEt) {
        double price = 0;
        String message = null;
        quotePriceEt.setError(null);
        try {
            price = Double.parseDouble(text);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            message = "Fiyat teklifin geçerli değil.";
        }
        if (TextUtils.isEmpty(text)) {
            message = "Teklif boş olamaz.";
        } else if (price <= 30){
            message = "Teklif 30 TL\'nin altında olamaz.";

        }
        if(message != null){
            app.showMessage(this,message, Toast.LENGTH_SHORT);
            quotePriceEt.requestFocus();
            quotePriceEt.setError(message);
            return false;
        }

        return true;
    }

    private String getStartDateIfValid(String text){
        if (TextUtils.isEmpty(text)) {
            return null;
        }
        String dateApiFormat;
        try{
            dateApiFormat = TimeUtils.convertDateTimeFormat(TimeUtils.dtfOut, TimeUtils.dfISOMS, text);//TimeUtils.convertSimpleDateToApiForm(text, true);
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
        return dateApiFormat;
    }

    private void initSearch(EditText quotePriceEt) {
        quotePriceEt.addTextChangedListener(new TextWatcher() {
            String text = "";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (timer != null) {
                    timer.cancel();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                String t = s.toString().trim();
                if (t.equals(text)) {
                    return;
                }
                text = t.toLowerCase(Locale.getDefault());
                Log.d(TAG, "TEXT: " + text);
                if (text.length() > 2) {
                    double price = 0;
                    try {
                        price = Double.parseDouble(text);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (price != 0) {
                        timer = new Timer();
                        quotePrice = price;
                        timer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                app.getBus().post(new JobEvents.CommissionRateRequest(quotePrice, jobId, isLeadRequired));
                            }
                        }, DELAY);
                    }
                } else {
                    TextView quotePriceInfo = (TextView) findViewById(R.id.quotePriceInfo);
                    quotePriceInfo.setVisibility(View.GONE);
                }
            }
        });
    }

    double quotePrice;

    public void onDateGroupRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        LinearLayout dateTimeLayoutSelectLayout = (LinearLayout) findViewById(R.id.dateTimeSelectLayout);
        switch (view.getId()) {
            case R.id.specificDateButton:
                if (checked) {
                    dateTimeLayoutSelectLayout.setVisibility(View.VISIBLE);
                    selectedJobStartDateTypeId = Constants.JOB_START_DATE_TYPE_SPECIFIC_TIME;
                    break;
                }
            case R.id.twoMonthsButton:
                if (checked) {
                    dateTimeLayoutSelectLayout.setVisibility(View.GONE);
                    selectedJobStartDateTypeId = Constants.JOB_START_DATE_TYPE_TWO_MONTHS;
                    break;
                }
            case R.id.sixMonthsButton:
                if (checked) {
                    dateTimeLayoutSelectLayout.setVisibility(View.GONE);
                    selectedJobStartDateTypeId = Constants.JOB_START_DATE_TYPE_SIX_MONTHS;
                    break;
                }
        }
        app.hideKeyboard(this);
    }

    @Subscribe
    public void isQuoteGiven(JobEvents.PostQuoteResponse response) {
        if (response != null){
            try {
                String json = response.getJobQuoteIdJson().body().string();
                JSONObject object = new JSONObject(json);
                int quoteId = object.getInt("job_quote_id");
                Log.d(TAG, "RESPONSE quoteId: " + quoteId);
                quoteGivenEvent(response.getQuote());
                Intent resultIntent = new Intent();
                resultIntent.putExtra(JobEvents.PARAM_JOB_QUOTE_ID, quoteId);
                resultIntent.putExtra(JobEvents.PARAM_JOB_QUOTE_PRICE, quotePrice);
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }else{
            Log.d(TAG, "RESULT IS null - try again");
        }
    }

    private void quoteGivenEvent(JobQuote quote){
        try{
            if(quote != null){
                MixpanelAPI mixpanel = MixpanelAPI.getInstance(GiveQuoteActivity.this, GiveQuoteActivity.this.getString(R.string.mpId));
                JSONObject mixPanelObject = ArmutUtils.getBasicJson(new BasicNameValuePair("Price", Double.toString(quote.getPrice())), ArmutUtils.getJobStartDatePair(GiveQuoteActivity.this, quote.getStartDate(), selectedJobStartDateTypeId));
                mixpanel.track("Completed Quote", mixPanelObject);
                mixpanel.getPeople().set("Platform", "Android");
                mixpanel.getPeople().increment("Number Of Quotes", 1);
                mixpanel.getPeople().set("Date Of Last Quote", TimeUtils.getTodayJoda(true));
                AdjustEvent event = new AdjustEvent("qr7p8b"); //Quoted Job
                Adjust.trackEvent(event);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Subscribe
    public void getCommissionRate(JobEvents.CommissionRateResponse response) {
        if (response != null) {
            CommissionRate rate = response.getResponse().body();
            Log.d(TAG, "RATE : " + rate.getCommissionRate());
            TextView quotePriceInfo = (TextView) findViewById(R.id.quotePriceInfo);
            EditText quotePriceEt = (EditText) findViewById(R.id.quotePrice);
            try {
                double price = Double.parseDouble(quotePriceEt.getText().toString());
                double commissionRate = rate.getCommissionRate();
                NumberFormat nf = NumberFormat.getInstance();
                nf.setMaximumFractionDigits(2);
                nf.setMinimumFractionDigits(2);
                nf.setRoundingMode(RoundingMode.HALF_UP);

                double commission = price * commissionRate / 100;

                String text = ArmutUtils.getMoneyPattern(price) + "fiyat verdiğin bu işi kazanırsan, " + (nf.format(commission)) + " TL komisyon (%" + nf.format(commissionRate) + ") ödeyeceksin, kalan net tutar " +
                        nf.format((price - commission)) + " TL olacak.";
                quotePriceInfo.setText(text);
                quotePriceInfo.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                e.printStackTrace();
                quotePriceInfo.setVisibility(View.GONE);
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
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
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
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            finish();
            MainActivity.setBackwardsTranslateAnimation(this);
        }
    }
}
