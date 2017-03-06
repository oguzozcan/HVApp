package com.armut.armuthv.fragments;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.aigestudio.wheelpicker.WheelPicker;
import com.armut.armuthv.R;

import java.util.ArrayList;

/**
 * Created by oguzemreozcan on 14/10/16.
 */

public class DialogTimePickerWheel extends DialogFragment {

    private TimesSelectedCallback callback;
    private WheelPicker startHours;
    private WheelPicker endHours;
    int index;

    public DialogTimePickerWheel() {
    }

    public static DialogTimePickerWheel newInstance(int index, int startHourIndex, int endHourIndex) {
        DialogTimePickerWheel dialog = new DialogTimePickerWheel();
        Bundle args = new Bundle();
        args.putInt("index", index);
        args.putInt("startHourIndex", startHourIndex);
        args.putInt("endHourIndex", endHourIndex);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            callback = (TimesSelectedCallback) getParentFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException("Calling fragment must implement TimesSelectedCallback interface");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.hour_wheel_pickers, container);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().setCancelable(true);
        getActivity().setFinishOnTouchOutside(true);
        startHours = (WheelPicker) rootView.findViewById(R.id.startTimePicker);
        endHours = (WheelPicker) rootView.findViewById(R.id.endTimePicker);
        startHours.getSelectedItemPosition();
        Typeface type = Typeface.createFromAsset(getActivity().getAssets(), "fonts/raleway-bold.ttf");
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
        if (getArguments() != null) {
            int startHourIndex = getArguments().getInt("startHourIndex");
            int endHourIndex = getArguments().getInt("endHourIndex");
            index = getArguments().getInt("index");
            startHours.setSelectedItemPosition(startHourIndex);
            endHours.setSelectedItemPosition(endHourIndex);
        }
        setOnWheelSelectedListener(startHours);
        setOnWheelSelectedListener(endHours);
        return rootView;
    }

    private void setOnWheelSelectedListener(final WheelPicker picker){
        picker.setOnWheelChangeListener(new WheelPicker.OnWheelChangeListener() {
            @Override
            public void onWheelScrolled(int offset) {
            }

            @Override
            public void onWheelSelected(int position) {
                picker.setSelectedItemPosition(position);
            }

            @Override
            public void onWheelScrollStateChanged(int state) {
            }
        });
    }

    @Override
    public void onDetach() {
        if (callback != null) {
            int startHourPos = startHours.getSelectedItemPosition();
            int endHourPos = endHours.getSelectedItemPosition();
            callback.timesSelected(index, startHourPos, endHourPos, true);
        }
        super.onDetach();
    }

    public interface TimesSelectedCallback {
        void timesSelected(int index, int startHour, int finishHour, boolean working);
    }
}