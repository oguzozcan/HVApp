package com.armut.armuthv.adapters;

import android.animation.LayoutTransition;
import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aigestudio.wheelpicker.WheelPicker;
import com.armut.armuthv.R;
import com.armut.armuthv.fragments.DialogTimePickerWheel;
import com.armut.armuthv.objects.UserCalendarItem;
import com.armut.armuthv.utils.TimeUtils;

import java.util.ArrayList;

/**
 * Created by oguzemreozcan on 14/10/16.
 */

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.ViewHolder> {

    private ArrayList<UserCalendarItem> mDataset;
    private final DialogTimePickerWheel.TimesSelectedCallback listener;
    private final Context context;
    private final LayoutInflater inflater;
    private int extendedChildIndex = -1;
    private ArrayList<ViewHolder> viewHolders;
    private final String TAG = "Calendar";

    class ViewHolder extends RecyclerView.ViewHolder {

        final TextView date;
        final TextView time;
        final TextView arrow;
        final TextView finish;
        final View dayIndicator;
        final SwitchCompat isWorkingToggle;
        final LinearLayout wheelPickerParent;
        final View wheelPickerLayout;

        ViewHolder(View v) {
            super(v);
            date = (TextView) v.findViewById(R.id.date);
            time = (TextView) v.findViewById(R.id.timeSelector);
            arrow = (TextView) v.findViewById(R.id.arrow);
            dayIndicator = v.findViewById(R.id.dayIndicator);
            isWorkingToggle = (SwitchCompat) v.findViewById(R.id.workingToggleButton);
            wheelPickerParent = (LinearLayout) v.findViewById(R.id.wheelPickerLayout);
            wheelPickerLayout = inflater.inflate(R.layout.hour_wheel_pickers, null);
            finish = (TextView) wheelPickerLayout.findViewById(R.id.finishButton);
            LayoutTransition transition = new LayoutTransition();
            transition.addTransitionListener(new LayoutTransition.TransitionListener() {
                @Override
                public void startTransition(LayoutTransition layoutTransition, ViewGroup viewGroup, View view, int transitionType) {
                    if(transitionType == LayoutTransition.APPEARING){
                        wheelPickerParent.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void endTransition(LayoutTransition layoutTransition, ViewGroup viewGroup, View view, int transitionType) {
                    if(transitionType == LayoutTransition.DISAPPEARING){
                        wheelPickerParent.setVisibility(View.GONE);
                    }
                }
            });
            wheelPickerParent.setLayoutTransition(transition);
        }
    }

    public void add(int position, UserCalendarItem item) {
        mDataset.add(position, item);
        notifyItemInserted(position);
    }

    public void updateItem(int position, UserCalendarItem item){
        mDataset.set(position, item);
        notifyItemChanged(position);
        extendedChildIndex = -1;
    }

    public void updateData(int position, UserCalendarItem item){
        mDataset.set(position, item);
    }

    public void updateAll(){
        notifyDataSetChanged();
        extendedChildIndex = -1;
    }

    public ArrayList<UserCalendarItem> getItems(){
        return mDataset;
    }

    public UserCalendarItem getItem(int position){
        return mDataset.get(position);
    }

    public void remove(UserCalendarItem item) {
        int position = mDataset.indexOf(item);
        mDataset.remove(position);
        notifyItemRemoved(position);
    }

    public CalendarAdapter(Context context, DialogTimePickerWheel.TimesSelectedCallback listener, ArrayList<UserCalendarItem> myDataset) { //, OnCalendarItemTimePickerSelectedListener listener
        mDataset = myDataset;
        this.context = context;
        this.listener = listener;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        viewHolders = new ArrayList<>();
    }

    @Override
    public CalendarAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.calendar_row, parent, false);
        int height = parent.getMeasuredHeight() / 7;
        Log.d("CalendarAdapter", "Calendar View Height: " + height);
        view.setMinimumHeight(height);
        ViewHolder vh = new ViewHolder(view);
        viewHolders.add(vh);
        //vh.dayIndicator.setMinimumHeight(height);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final UserCalendarItem calendarItem = mDataset.get(position);
        setWheelPickerView(holder, calendarItem.getDayStartHour(), calendarItem.getDayEndHour(), position);
        String date = calendarItem.getDate();
        boolean isWorking = calendarItem.isWorking();
        int dateNo = TimeUtils.getDayOfWeekAsNumber(TimeUtils.dfISOMS, date);
        holder.date.setText(getWeekdayName(date));
        holder.time.setText(setWorkingHours(calendarItem.getDayStartHour(), calendarItem.getDayEndHour(), isWorking));
        Log.d(TAG, "START : " + calendarItem.getDayStartHour() + " - END: " + calendarItem.getDayEndHour());
        holder.isWorkingToggle.setChecked(isWorking);
        holder.isWorkingToggle.setOnCheckedChangeListener(new ToggleChangeListener(position, holder));
        if(dateNo == 6 || dateNo == 7){
            holder.dayIndicator.setBackgroundColor(ContextCompat.getColor(context, R.color.calendar_weekend));
        }else{
            holder.dayIndicator.setBackgroundColor(ContextCompat.getColor(context, R.color.calendar_weekday));
        }
        holder.time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onTimeClick(holder);
            }
        });

        holder.date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onTimeClick(holder);
            }
        });
    }

    private void onTimeClick(ViewHolder holder){
        String text = holder.time.getText().toString();
        if(text.equals(context.getString(R.string.not_working))){
            return;
        }
        Log.d("CalendarAdapter", "EXTENDED CHILD INDEX: " + extendedChildIndex);
        if(extendedChildIndex == -1) {
            if(holder.wheelPickerParent.getChildCount() == 0){
                try{
                    holder.wheelPickerParent.addView(holder.wheelPickerLayout);
                    extendedChildIndex = holder.getAdapterPosition();
                }catch(Exception e){
                    e.printStackTrace();
                }
            }else{
                holder.wheelPickerParent.removeView(holder.wheelPickerLayout);
                extendedChildIndex = -1;
            }

        }else {
            ViewHolder unselectedHolder = viewHolders.get(extendedChildIndex);
            if (unselectedHolder.wheelPickerParent.getChildCount() != 0) {
                unselectedHolder.wheelPickerParent.removeView(unselectedHolder.wheelPickerLayout);
                if(unselectedHolder != holder){
                    try{
                        holder.wheelPickerParent.addView(holder.wheelPickerLayout);
                        extendedChildIndex = holder.getAdapterPosition();
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }else{
                    //holder.wheelPickerParent.removeView(holder.wheelPickerLayout);
                    extendedChildIndex = -1;
                }
            }else{
                extendedChildIndex = -1;
            }
        }
    }

    private String getWeekdayName(String date){
        Log.d("CalendarAdapter", "DATE: " + date);
        return TimeUtils.getDayOfWeek(TimeUtils.dfISOMS, date);
    }

    private void setWheelPickerView(ViewHolder holder, int startHourIndex, int endHourIndex, int position){
        WheelPicker startHours = (WheelPicker) holder.wheelPickerLayout.findViewById(R.id.startTimePicker);
        WheelPicker endHours = (WheelPicker) holder.wheelPickerLayout.findViewById(R.id.endTimePicker);
        //startHours.getSelectedItemPosition();
        Typeface type = Typeface.createFromAsset(context.getAssets(), "fonts/raleway-bold.ttf");
        startHours.setTypeface(type);
        endHours.setTypeface(type);
        ArrayList<String> hourList = new ArrayList<>();
        for (int i = 0; i < 24; i++) {
            String time = "";
            if (i < 10) {
                time = "0" + i + " : 00";
            } else {
                time = i + " : 00";
            }
            hourList.add(time);
        }
        startHours.setData(hourList);
        endHours.setData(hourList);
        //calendarItem.getDayStartHour(), calendarItem.getDayEndHour()
        startHours.setSelectedItemPosition(startHourIndex);
        endHours.setSelectedItemPosition(endHourIndex);
        setOnWheelSelectedListener(startHours, holder.time, true, position);
        setOnWheelSelectedListener(endHours, holder.time, false, position);
    }

    private String setWorkingHours(int startHour, int endHour, boolean working){
        Log.d("Calendar", "START HOUR: " + startHour + " - Endhour: " + endHour);
        if(working){
            return convertIntToHour(startHour) + " - " + convertIntToHour(endHour);
        }else{
            return context.getString(R.string.not_working) ;
        }
    }

    private String convertIntToHour(int hour){
        String text = "";
        if(hour < 10){
            text = "0" + hour + ":00";
        }else{
            text = hour + ":00";
        }
        return text;
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

//    public interface OnCalendarItemTimePickerSelectedListener{
//        void onTimePickerSelected(UserCalendarItem calendarItem, int index);
//    }

    private void setOnWheelSelectedListener(final WheelPicker picker, TextView textView, boolean isStartHour, int wheelPosition){
        picker.setOnWheelChangeListener(new WheelChangeSelectionListener(picker, textView, isStartHour, wheelPosition));
    }

    private class WheelChangeSelectionListener implements WheelPicker.OnWheelChangeListener{
        final WheelPicker picker;
        final TextView textView;
        final boolean isStart;
        final int wheelPosition;

            WheelChangeSelectionListener(WheelPicker picker, TextView textView, boolean isStart, int wheelPosition){
                this.picker = picker;
                this.textView = textView;
                this.isStart = isStart;
                this.wheelPosition = wheelPosition;
            }

            @Override
            public void onWheelScrolled(int offset) {

            }

            @Override
            public void onWheelSelected(int position) {
                String text = textView.getText().toString();
                if(isStart){
                    text = convertIntToHour(position) + " - " + text.substring(8);
                    Log.d("WHEELSELECTED", "start hour: " + position + " - endhour: " + text.substring(8,10));
                    int endhour = Integer.parseInt(text.substring(8,10));
                    if(listener != null)
                        listener.timesSelected(wheelPosition, position, endhour, true);
                }else{
                    text = text.substring(0,5) + " - " + convertIntToHour(position);
                    Log.d("WHEELSELECTED", "start hour: " + text.substring(0,2) + " - endhour: " + position);
                    int starthour = Integer.parseInt(text.substring(0,2));
                    if(listener != null)
                        listener.timesSelected(wheelPosition, starthour, position, true);
                }
                textView.setText(text);
                picker.setSelectedItemPosition(position);
            }

            @Override
            public void onWheelScrollStateChanged(int state) {

            }
    }

    private class ToggleChangeListener implements CompoundButton.OnCheckedChangeListener{

        final int position;
        final TextView timeTv;
        final TextView arrowTv;
        final TextView finishTv;
        final ViewHolder holder;

        ToggleChangeListener(final int position,final ViewHolder holder){
            this.position = position;
            this.timeTv = holder.time;
            this.arrowTv = holder.arrow;
            this.finishTv = holder.finish;
            this.holder = holder;
            this.finishTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onTimeClick(holder);
                }
            });
        }

        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            if(mDataset != null){
                UserCalendarItem item = mDataset.get(position);
                item.setWorking(b);
                if(!b){
                    timeTv.setText(context.getString(R.string.not_working));//setVisibility(View.INVISIBLE);
                    arrowTv.setVisibility(View.INVISIBLE);
                    holder.wheelPickerParent.removeView(holder.wheelPickerLayout);
                }else{
                    timeTv.setText(setWorkingHours(item.getDayStartHour(), item.getDayEndHour(), true));
                    arrowTv.setVisibility(View.VISIBLE);
                    //timeTv.setVisibility(View.VISIBLE);
                }
                if(listener != null)
                    listener.timesSelected(position, item.getDayStartHour(), item.getDayEndHour(), b);

            }
        }
    }

}
