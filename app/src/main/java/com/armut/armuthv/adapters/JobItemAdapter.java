package com.armut.armuthv.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.armut.armuthv.ArmutHVApp;
import com.armut.armuthv.BaseActivity;
import com.armut.armuthv.JobDetailActivity;
import com.armut.armuthv.OpportunityActivity;
import com.armut.armuthv.R;
import com.armut.armuthv.busevents.JobEvents;
import com.armut.armuthv.busevents.MiscEvents;
import com.armut.armuthv.objects.BasicNameValuePair;
import com.armut.armuthv.objects.Job;
import com.armut.armuthv.utils.ArmutUtils;
import com.armut.armuthv.utils.Constants;
import com.armut.armuthv.utils.TimeUtils;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.squareup.otto.Subscribe;

import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import uk.co.chrisjenx.calligraphy.CalligraphyUtils;
import uk.co.chrisjenx.calligraphy.TypefaceUtils;

/**
 * Created by oguzemreozcan on 24/05/16.
 */
public class JobItemAdapter extends RecyclerView.Adapter<JobItemAdapter.DataObjectHolder> {

    private final WeakReference<Activity> activity;
    private ArrayList<Job> data;
    private LayoutInflater inflater = null;
    private final int sectionNumber;
    private final RecyclerView jobsRecyclerView;
    private final String TAG = "JobItemAdapter";
    private Typeface mediumFont;
    private Typeface boldFont;

    static class DataObjectHolder extends RecyclerView.ViewHolder {
        final TextView titleTv;
        final TextView descriptionTv;
        final TextView remainingDateTv;
        final ImageView canCallIv;
        final TextView privateJobTv;
        final TextView urgentJobTv;
        final ImageView hasPicturesIv;
        final ImageView notificationIv;
        final FrameLayout parent;

        DataObjectHolder(View itemView) {
            super(itemView);
            parent = (FrameLayout) itemView;
            titleTv = (TextView) itemView.findViewById(R.id.titleTv);
            descriptionTv = (TextView) itemView.findViewById(R.id.descriptionTv);
            remainingDateTv = (TextView) itemView.findViewById(R.id.remainingTimeTv);
            privateJobTv = (TextView) itemView.findViewById(R.id.privateJobTv);
            urgentJobTv = (TextView) itemView.findViewById(R.id.urgentJobTv);
            canCallIv = (ImageView) itemView.findViewById(R.id.phoneAvailableIv);
            hasPicturesIv = (ImageView) itemView.findViewById(R.id.hasPictures);
            notificationIv = (ImageView) itemView.findViewById(R.id.notificationIcon);
        }
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.row_job_item_layout, parent, false);
        view.setOnClickListener(new JobItemClickListener());
        return new DataObjectHolder(view);
    }

    public JobItemAdapter(Activity act, ArrayList<Job> jobs, RecyclerView jobsRecyclerView, int sectionNumber) {
        this.activity = new WeakReference(act);
        this.sectionNumber = sectionNumber;
        this.jobsRecyclerView = jobsRecyclerView;
        inflater = (LayoutInflater) act
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mediumFont = TypefaceUtils.load(act.getAssets(), "fonts/raleway-medium.ttf");
        boldFont = TypefaceUtils.load(act.getAssets(), "fonts/raleway-bold.ttf");
        if (jobs != null) {
            data = new ArrayList<>(jobs);
            Log.d(TAG, "DATA not null : " + data.size());
        }
//        TypedValue value = new TypedValue();
//        act.getTheme().resolveAttribute(android.R.attr.listPreferredItemHeight, value, true);
//        android.util.TypedValue value = new android.util.TypedValue();
//        boolean b = act.getTheme().resolveAttribute(android.R.attr.listPreferredItemHeight, value, true);
//        String s = TypedValue.coerceToString(value.type, value.data);
//        android.util.DisplayMetrics metrics = new android.util.DisplayMetrics();
//        act.getWindowManager().getDefaultDisplay().getMetrics(metrics);
//        float ret = value.getDimension(metrics);
//        Log.d(TAG, "List item size: " + ret + "- " + getItemHeight());

    }

    public void register(ArmutHVApp app){
        Log.d(TAG, "REGISTER ADAPTER");
        app.getBus().register(this);
    }

    public void unregister(ArmutHVApp app){
        Log.d(TAG, "UN REGISTER ADAPTER");
        app.getBus().unregister(this);
    }

//    float getItemHeight() {
//        TypedValue value = new TypedValue();
//        DisplayMetrics metrics = new DisplayMetrics();
//
//        activity.get().getTheme().resolveAttribute(
//                android.R.attr.listPreferredItemHeight, value, true);
//        ((WindowManager) (activity.get().getSystemService(Context.WINDOW_SERVICE)))
//                .getDefaultDisplay().getMetrics(metrics);
//
//        return TypedValue.complexToDimension(value.data, metrics);
//    }

    public void add(Job data) {
        if (this.data == null) {
            this.data = new ArrayList<>();
        }
        this.data.add(data);
        int position = this.data.size() - 1;
        notifyItemInserted(position);
    }

    public void addData(ArrayList<Job> data) {
        if (this.data == null) {
            this.data = data;
        } else {
            this.data.addAll(data);
        }
        notifyDataSetChanged();
    }

    public void remove(Job item) {
        int position = data.indexOf(item);
        data.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public void onBindViewHolder(DataObjectHolder holder, int position) {
        final Job event = getItem(position);
        //Log.d(TAG, "APPOINMENT DATE: " + event.getAppointmentDate() + "- quoteexpiredate: " + event.getJobQuoteExpireDate() );
        String dateTitle = "";
        String title = event.getJobCity();
        Double quotePrice = event.getQuotePrice();
        String remainingDate;
        String price = "";
        SpannableString spanPrice = null;
        if (sectionNumber == 1) {
            //TODO
            remainingDate = event.getCreateDate();//event.getJobQuoteExpireDate();
            Log.d(TAG, "JOB ID: " + event.getJobId() + "  - quote id: " + event.getQuoteId());
            Log.d(TAG, "DATES: createdate: " + event.getCreateDate() + " - quoteExpireDate: " + event.getJobQuoteExpireDate() + "- getJobStartDateTime: " + event.getJobStartDateTime());
//            if (event.isLeadRequired()) {
//                holder.priceTv.setText(event.getLeadPrice().doubleValue() + " TL");
//            }
            dateTitle = convertJobStartTypeToDate(event.getJobStartTypeId(), TimeUtils.convertDateTimeFormat(TimeUtils.dfISOMS, TimeUtils.dtfOutWOYearShort, event.getJobStartDateTime())); //remainingDate
            holder.remainingDateTv.setVisibility(View.VISIBLE);
            String remainingTimeText = TimeUtils.calculateTimeInBetween(remainingDate);
            int distance = (int) event.getDistance();
            if (!remainingTimeText.equals("")) {
                remainingTimeText = distance + " KM - " + remainingTimeText;
            } else {
                remainingTimeText = distance + " KM - YENI";
            }
            holder.remainingDateTv.setText(remainingTimeText);
            //Log.d(TAG, "calculateTimeInBetween(remainingDate) " + TimeUtils.calculateTimeInBetween(remainingDate));
            holder.descriptionTv.setText(event.getServiceName() + " - " + event.getDetail());//event.getServiceName());
        } else if (sectionNumber == 2) {
            if (quotePrice != null) {
                //holder.priceTv.setText(quotePrice.doubleValue() + " TL " + "teklif verildi");
                price = quotePrice.intValue() + " TL ";
            }
            dateTitle = TimeUtils.convertDateTimeFormat(TimeUtils.dfISOMS, TimeUtils.dtfOutWOYearShort, event.getJobStartDateTime());
            //holder.remainingDateTv.setText(event.getDistance() + " KM ");
            holder.descriptionTv.setText(event.getServiceName() + " - " + event.getDetail());//event.getServiceName());
        } else if (sectionNumber == 3) {
            remainingDate = event.getAppointmentDate();
            if (quotePrice != null) {
                price = quotePrice.intValue() + " TL ";
                //holder.priceTv.setText(quotePrice.doubleValue() + " TL " + event.getCustomerFullName());
            }
            String lastMessage = event.getLastMessage();
            if (lastMessage.equals("")) {
                lastMessage = event.getDetail();
            }
            dateTitle = TimeUtils.convertDateTimeFormat(TimeUtils.dfISOMS, TimeUtils.dtfOutWOYearShort, remainingDate);
            holder.descriptionTv.setText(event.getServiceName() + " - " + lastMessage);//event.getServiceName());
            //holder.remainingDateTv.setText(event.getDistance() + " KM ");//TimeUtils.calculateTimeInBetween(remainingDate));
        }

        title += ", " + dateTitle;
        if (!price.equals("")) {
            title += ", " + price;
            spanPrice = new SpannableString(title);
            int titleLength = title.length() - 1;
            spanPrice.setSpan(new RelativeSizeSpan(0.65f), titleLength - 3, titleLength, 0);
        }
        holder.titleTv.setText(spanPrice != null ? spanPrice : title);

        if (event.getCallPreference() != null) {
            if (canCall(event.getCallPreference())) {
                holder.canCallIv.setVisibility(View.VISIBLE);
            } else {
                holder.canCallIv.setVisibility(View.INVISIBLE);
            }
        }
        if (event.isHasPicture()) {
            holder.hasPicturesIv.setVisibility(View.VISIBLE);
        } else {
            holder.hasPicturesIv.setVisibility(View.INVISIBLE);
        }

        if (event.isRead()) {
            holder.parent.setForeground(ContextCompat.getDrawable(activity.get(), R.drawable.job_is_read_foreground_drawable));
            holder.notificationIv.setVisibility(View.INVISIBLE);
            CalligraphyUtils.applyFontToTextView(holder.titleTv, mediumFont);
        } else {
            holder.notificationIv.setVisibility(View.VISIBLE);
            holder.parent.setForeground(ContextCompat.getDrawable(activity.get(), R.drawable.job_is_not_read_foreground_drawable));
            CalligraphyUtils.applyFontToTextView(holder.titleTv, boldFont);
        }

        if (event.isUrgentJob()) {
            holder.urgentJobTv.setVisibility(View.VISIBLE);
        } else {
            holder.urgentJobTv.setVisibility(View.INVISIBLE);
        }

        if (event.isPrivateJobRequest()) {
            holder.privateJobTv.setVisibility(View.VISIBLE);
        } else {
            holder.privateJobTv.setVisibility(View.INVISIBLE);
        }
    }

    private boolean canCall(String callValue) {
        return !callValue.equals("0");
    }

    private String convertJobStartTypeToDate(int type, String appointmentDate) {
        switch (type) {
            case Constants.JOB_START_DATE_TYPE_SPECIFIC_TIME:
                return appointmentDate;
            case Constants.JOB_START_DATE_TYPE_SIX_MONTHS:
                return "Altı ay içinde";
            case Constants.JOB_START_DATE_TYPE_TWO_MONTHS:
                return "İki ay içinde";
            case Constants.JOB_START_DATE_TYPE_LOOKING_PRICE:
                return "Fiyat bakıyorum";
            default:
                return appointmentDate;
        }
    }

    @Override
    public int getItemCount() {
        if (data == null) {
            return 0;
        }
        return data.size();
    }

    @Subscribe
    public void itemClickedEvent(MiscEvents.ItemClickedRequest request){
        if(request != null){
            Log.d(TAG, "ITEM CLICKED EVENT : " + request.job.getJobId());
            if(sectionNumber == request.sectionNumber){
                itemClicked(request.activity, request.job, getPosition(request.job), request.sectionNumber);
            }
        }
    }

    private void itemClicked(Activity act, Job event, int itemPosition, int sectionNumber) {
        if (act != null && event != null) {
            MixpanelAPI mixpanel = MixpanelAPI.getInstance(act, act.getString(R.string.mpId));
            JSONObject object = ArmutUtils.getBasicJson(new BasicNameValuePair("Which Service", event.getServiceName()));
            Intent jobIntent = new Intent(act, OpportunityActivity.class);
            String date = TimeUtils.convertDateTimeFormat(TimeUtils.dfISOMS, TimeUtils.dtfOutWOYear, event.getAppointmentDate());
            if (sectionNumber == Constants.OPPORTUNITY_SECTION_INDEX) {
                date = TimeUtils.convertDateTimeFormat(TimeUtils.dfISOMS, TimeUtils.dtfOutWOYear, event.getJobQuoteExpireDate());
                jobIntent.putExtra(JobEvents.PARAM_URGENT_JOB, event.isUrgentJob());
                jobIntent.putExtra(JobEvents.PARAM_PRIVATE_JOB, event.isPrivateJobRequest());
                mixpanel.track("Viewed An Opportunity", object);
            } else if (sectionNumber == Constants.QUOTE_SECTION_INDEX || sectionNumber == Constants.DEALS_SECTION_INDEX) {
                jobIntent = new Intent(act, JobDetailActivity.class);
                jobIntent.putExtra(JobEvents.PARAM_JOB_QUOTE_ID, event.getQuoteId());
                jobIntent.putExtra(JobEvents.PARAM_JOB_QUOTE_PRICE, event.getQuotePrice());
                date = TimeUtils.convertDateTimeFormat(TimeUtils.dfISOMS, TimeUtils.dtfOutWOYear, event.getJobStartDateTime());
                if (sectionNumber == Constants.QUOTE_SECTION_INDEX) {
                    mixpanel.track("Viewed A Quote", object);
                } else {
                    mixpanel.track("Viewed A Deal", object);
                }
                if (sectionNumber == Constants.DEALS_SECTION_INDEX) {
                    String detail = event.getAddress();
                    boolean jobHasAddressDetails = false;
                    String addressTxt = event.getJobDistrict() + " " + detail + "\n" + event.getJobCity() + ", " + event.getJobState();
                    if (detail != null) {
                        if (!detail.isEmpty()) {
                            jobHasAddressDetails = true;
                        }
                    }
                    jobIntent.putExtra(JobEvents.PARAM_JOB_HAS_ADDRESS_DETAILS, jobHasAddressDetails);
                    jobIntent.putExtra(JobEvents.PARAM_JOB_ADDRESS, addressTxt);
                }
            }

            String addressTxt = event.getJobDistrict() + " " + event.getAddress() + "\n" + event.getJobCity() + ", " + event.getJobState();
            Log.d(TAG, "ADDRESS TEXT: " + addressTxt);
            jobIntent.putExtra(JobEvents.PARAM_CUSTOMER_PHONE, event.getCustomerPhoneNumber());
            jobIntent.putExtra(JobEvents.PARAM_SECTION_NUMBER, sectionNumber);
            jobIntent.putExtra(JobEvents.PARAM_JOB_ID, event.getJobId());
            jobIntent.putExtra(JobEvents.PARAM_CUSTOMER_ID, event.getUserId());
            jobIntent.putExtra(JobEvents.PARAM_SERVICE_ID, event.getServiceId());
            jobIntent.putExtra(JobEvents.PARAM_SERVICE_NAME, event.getServiceName());
            jobIntent.putExtra(JobEvents.PARAM_CUSTOMER_NAME, event.getCustomerFullName());
            jobIntent.putExtra(JobEvents.PARAM_QUOTE_REMAINING_TIME, event.getJobQuoteExpireDate());
            jobIntent.putExtra(JobEvents.PARAM_PROVIDER_PROFILE_ID, event.getProfileId());
            event.setRead(true);
            notifyItemChanged(itemPosition);
            Log.d(TAG, "PROVIDER PROFILE ID: " + event.getProfileId() + " - sectionNumber : " + sectionNumber + "EVENT DATE: " + date + "event.getCustomerFullName() : " + event.getCustomerFullName() + " - callPref: " + event.getCallPreference());
//                Double leadPrice = event.getLeadPrice();
//                if(leadPrice != null){
//                    jobIntent.putExtra(JobEvents.PARAM_LEAD_PRICE, leadPrice.toString() + " TL" );
//                }
//                jobIntent.putExtra(JobEvents.PARAM_JOB_MODEL, event.)
            jobIntent.putExtra(JobEvents.PARAM_JOB_DATE, date);
            jobIntent.putExtra(JobEvents.PARAM_JOB_CITY, event.getJobCity());
            jobIntent.putExtra(JobEvents.PARAM_CAN_CALL, Integer.parseInt(event.getCallPreference()));
            act.startActivityForResult(jobIntent, Constants.UPDATE_JOBS_PAGE);
            BaseActivity.setTranslateAnimation(act);
        }
    }

    public Job getItem(int position) {
        return data.get(position);
    }

    private int getPosition(Job job){
        for(int i = 0; i < data.size(); i ++){
            if(data.get(i) == job){
                return i;
            }
        }
        return 0;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class JobItemClickListener implements View.OnClickListener {

        JobItemClickListener() {
        }

        @Override
        public void onClick(View view) {
            int itemPosition = jobsRecyclerView.getChildLayoutPosition(view);
            final Job event = getItem(itemPosition);
            Activity act = activity.get();
            itemClicked(act, event, itemPosition, sectionNumber);
        }
    }
}



