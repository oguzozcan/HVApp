package com.armut.armuthv.components;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.android.datetimepicker.date.DatePickerDialog;
import com.android.datetimepicker.time.RadialPickerLayout;
import com.android.datetimepicker.time.TimePickerDialog;
import com.armut.armuthv.R;
import com.armut.armuthv.utils.TimeUtils;

import org.joda.time.DateTime;

import java.util.Calendar;

/**
 * Created by oguzemreozcan on 13/07/16.
 */
public class DatePickerButton extends Button implements View.OnClickListener, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener{


    private String time = "";
    private String date = "";
    private Activity activity;
    private final DateTime dateTime;

    public DatePickerButton(Context context, AttributeSet attrs) {
        super(context, attrs);
//        if (attrs != null) {
//            final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.NumberSelector);
//            selectedColor = a.getColor(R.styleable.NumberSelector_selected_color, ContextCompat.getColor(context,R.color.colorPrimary));
//            unSelectedColor = a.getColor(R.styleable.NumberSelector_unselected_color, ContextCompat.getColor(context,R.color.gray2));
//            selectorWithPredefinedValues = a.getBoolean(R.styleable.NumberSelector_predefined_values, false) ;
//            unit = a.getString(R.styleable.NumberSelector_unit);
//            question =  a.getString(R.styleable.NumberSelector_question);
//            a.recycle();
//        }
        dateTime = TimeUtils.getDateTime(TimeUtils.getDaysBeforeOrAfterToday(2, true), true);
        setOnClickListener(this);
        //setBackgroundResource(R.drawable.spinner_selector_shape);
        setBackgroundResource(R.drawable.dropdown);

    }

    @Override
    public void onClick(View view) {
        Log.d("DatePicker", "ON CLICK");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 2);
        DatePickerDialog datePicker = DatePickerDialog.newInstance(DatePickerButton.this, dateTime.getYear(), dateTime.getMonthOfYear() - 1, dateTime.getDayOfMonth() + 2);
        datePicker.setMinDate(calendar);
        datePicker.setYearRange(calendar.get(Calendar.YEAR), calendar.get(Calendar.YEAR) + 1);
        datePicker.show(activity.getFragmentManager(), "datePicker");
    }

    public void setActivity(Activity activity){
        this.activity = activity;
    }

    @Override
    public void onDateSet(DatePickerDialog dialog, int year, int monthOfYear, int dayOfMonth) {
        if(activity == null){
            return;
        }
        if(time.equals("")){
            TimePickerDialog timePicker = TimePickerDialog.newInstance(this, dateTime.getHourOfDay(), dateTime.getMinuteOfHour(), true);
            timePicker.show(activity.getFragmentManager(), "timePicker");
        }
        String dateText = "" + dayOfMonth + "." + (monthOfYear + 1) + "." + year;
        date = TimeUtils.convertSimpleDateToReadableForm(dateText, false);
        this.setText(date);
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
        if(activity == null){
            return;
        }
        String dateTime  = "" + hourOfDay + "." + minute;
        Log.d("DatePickerButton", "HOUR OF DAY !!!: " + hourOfDay);
        time = TimeUtils.convertSimpleTimeToReadableForm(dateTime);
        String text = getText().toString();
        text = text + " " + time;
        setText(text);
    }
}
