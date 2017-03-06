package com.armut.armuthv.components;

import android.animation.LayoutTransition;
import android.app.Activity;
import android.content.res.Resources;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

import com.android.datetimepicker.date.DatePickerDialog;
import com.android.datetimepicker.time.RadialPickerLayout;
import com.android.datetimepicker.time.TimePickerDialog;
import com.armut.armuthv.R;
import com.armut.armuthv.utils.Constants;
import com.armut.armuthv.utils.TimeUtils;

import org.joda.time.DateTime;

import java.util.Calendar;

/**
 * Created by oguzemreozcan on 13/07/16.
 */
public class DatePickerLayout extends LinearLayout implements QuestionView, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private int controlId;
    private final AdditionalInfoTextView tvDate;
    private final AdditionalInfoTextView tvTime;
    private String defaultDateText;
    private String defaultTimeText;
    private int order;
    private final Activity activity;
    private boolean required;
    //private boolean answered = false;
    private final DateTime dateTime;
    private final String TAG = "DatePickerLayout";
    private String question;
    private QuestionTextView questionTx;
   // private int workHour;

    public DatePickerLayout(final Activity activity){
        super(activity);
        this.activity = activity;
        setLayoutTransition(new LayoutTransition());
        defaultDateText = "Lütfen tarih seçin!";
        defaultTimeText = "Lütfen zaman seçin!";
        //TimeUtil.getDaysBeforeOrAfterToday(7, true);
       // workHour = (int)QuestionProvider.getInstance().quantity;
        tvDate = new AdditionalInfoTextView(activity, defaultDateText, false, false, false);
        tvTime = new AdditionalInfoTextView(activity, defaultTimeText, false, false, false);
//        dateTextAdded = false;
        dateTime = TimeUtils.getDateTime(TimeUtils.getDaysBeforeOrAfterToday(2, true), true);//getDateTime(defaultDateText, true);
        init();
        addView(tvDate);
        addView(tvTime);
        tvDate.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.DAY_OF_MONTH, 2);
                DatePickerDialog datePicker = DatePickerDialog.newInstance(DatePickerLayout.this, dateTime.getYear(), dateTime.getMonthOfYear() - 1, dateTime.getDayOfMonth() + 2);
                datePicker.setMinDate(calendar);
                datePicker.setYearRange(calendar.get(Calendar.YEAR), calendar.get(Calendar.YEAR) + 1);
                datePicker.show(activity.getFragmentManager(), "datePicker");

            }
        });
        tvTime.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePicker = TimePickerDialog.newInstance(DatePickerLayout.this, dateTime.getHourOfDay(), dateTime.getMinuteOfHour(), true);
                timePicker.show(activity.getFragmentManager(), "timePicker");
                Calendar hour = Calendar.getInstance();
                hour.set(Calendar.HOUR_OF_DAY, 8);
                hour.set(Calendar.MINUTE, 0);
                timePicker.setStartTime(hour.get(Calendar.HOUR_OF_DAY), hour.get(Calendar.MINUTE));
            }
        });
        String dateTime ="";
//        if(workHour == 3){        if(workHour == 3){
//            dateTime  = "15:00";
//        }else if(workHour > 3 && workHour < 10){
//            dateTime  = "09:00";
//        }else if(workHour == 10){
//            dateTime = "08:00";
//        }
//            dateTime  = "15:00";
//        }else if(workHour > 3 && workHour < 10){
//            dateTime  = "09:00";
//        }else if(workHour == 10){
//            dateTime = "08:00";
//        }
        if(!dateTime.equals("")){
            tvTime.setText(dateTime);
            defaultTimeText = dateTime;
        }
        tvDate.setSelected(true);
        tvTime.setSelected(true);
    }

    private void init(){
        Resources res = getResources();
        LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL;

        int marginBottom = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15, res.getDisplayMetrics());
        int marginTop = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, res.getDisplayMetrics());
        int paddingVertical = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, res.getDisplayMetrics());
        layoutParams.setMargins(0, 0, 0, marginTop);
        setPadding(marginBottom, paddingVertical, marginBottom, paddingVertical);
        setOrientation(LinearLayout.VERTICAL);
        setLayoutParams(layoutParams);
        setQuestionParams();
    }

    private void setQuestionParams(){
        questionTx = new QuestionTextView(activity, R.dimen.font_size_questions);
        if(question == null){
            Log.e(TAG, "Question should be set!");
            questionTx.setText(question);
        }
        //questionTx.setTypeface(ArmutUtils.loadFont(context.getAssets(), context.getString(R.string.font_pera_bold)));
        //questionTx.setTextSize(context.getResources().getDimension(R.dimen.font_size_questions));
        this.addView(questionTx);
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
        if(questionTx != null){
            questionTx.setText(question);
        }
    }

    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int year, int monthOfYear, int dayOfMonth) {
        if(tvTime.getText().equals("")){
            TimePickerDialog timePicker = TimePickerDialog.newInstance(this, dateTime.getHourOfDay(), dateTime.getMinuteOfHour(), true);
            timePicker.show(activity.getFragmentManager(), "timePicker");
        }
        String dateText = "" + dayOfMonth + "." + (monthOfYear + 1) + "." + year;
        defaultDateText = TimeUtils.convertSimpleDateToReadableForm(dateText, false);
        tvDate.setText(defaultDateText);
    }

    @Override
    public void onTimeSet(RadialPickerLayout radialPickerLayout, int hourOfDay, int minute) {
        String dateTime  = "" + hourOfDay + "." + minute;
        Log.d(TAG, "HOUR OF DAY !!!: " + hourOfDay);
//        workHour = (int)QuestionProvider.getInstance().quantity;
//        if(hourOfDay < 8 || hourOfDay > 18){
//            activity.runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    Toast.makeText(activity, activity.getString(R.string.reservation_available_time_warning), Toast.LENGTH_LONG).show();
//                    tvTime.performClick();
//                }
//            });
//            return;
//        }else if(hourOfDay + workHour > 18 ){
//            activity.runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    String text = activity.getString(R.string.reservation_hour_not_available_time_warning);
//                    text = text.replaceFirst("X", workHour+" ");
//                    text = text.replaceFirst("Y", (18-workHour) +" ");
//                    Toast.makeText(activity,text , Toast.LENGTH_LONG).show();
//                    tvTime.performClick();
//                }
//            });
//            return;
//        }
        Log.d(TAG, "DATE TIME STRING: " + dateTime);
        defaultTimeText = TimeUtils.convertSimpleTimeToReadableForm(dateTime);
        tvTime.setText(defaultTimeText);
    }

    public void setTime(String text){
        tvTime.setText(text);
    }

    public void setDate(String date){
        tvDate.setText(date);
    }

    @Override
    public void setOrder(int order) {
        this.order = order;
    }

    @Override
    public int getOrder() {
        return order;
    }

    @Override
    public String getSelectedValue() {
        return defaultDateText + " " + defaultTimeText;
    }

    @Override
    public String getAnswerToSend() {
        String text = "4";
        //tvDate.getAnswerToSend() + tvTime.getAnswerToSend();
        Log.d(TAG, "DEFAULT DATE TEXT : " + defaultDateText);
        if(tvDate != null && tvTime != null){
            //String dateText = //tvDate.getAnswerToSend() + tvTime.getAnswerToSend();
            //if(dateText.equals("")){
            String dateText = defaultDateText + " " + defaultTimeText;
            // }
            if(defaultDateText.contains("Lütfen") || defaultTimeText.contains("Lütfen")){

                return "";
            }
            String convertedDate;
            try{
                convertedDate = TimeUtils.convertDateTimeFormat(TimeUtils.dtfOut, TimeUtils.updateDateFormat, dateText);
            }catch(Exception e){
                e.printStackTrace();
                return "";
            }
            text = text.concat(Constants.SEPERATOR).concat(convertedDate);
        }
        return text;
    }

    @Override
    public void setRequired(boolean answerRequired) {
        required = answerRequired;
    }

    @Override
    public boolean getRequired() {
        return required;
    }

    @Override
    public boolean isAnswered() {
        return !getAnswerToSend().equals("");
    }

    @Override
    public int getControlId() {
        return controlId;
    }

    @Override
    public void setControlId(int controlId) {
        this.controlId = controlId;
    }
}

