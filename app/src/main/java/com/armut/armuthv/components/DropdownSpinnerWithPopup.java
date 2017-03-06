package com.armut.armuthv.components;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.armut.armuthv.R;

/**
 * Created by oguzemreozcan on 30/06/16.
 */
public class DropdownSpinnerWithPopup extends Spinner implements QuestionView, AdapterView.OnItemSelectedListener {

    private int order;
    private boolean required;
    private int controlId;
//    private boolean answered = false;
//    private int selectedPosition;
//    private int selectedId;

//    private boolean isDatePicker;
//    private boolean isTimePicker;

    public DropdownSpinnerWithPopup(Context context) {
        super(context, Spinner.MODE_DIALOG);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
//        setBackgroundColor(getResources().getColor(R.color.transparent));
        params.bottomMargin = (int) getResources().getDimension(R.dimen.bottom_margin_between_components);
        setLayoutParams(params);
    }

//    public DropdownSpinnerWithPopup(Context context, AttributeSet attrs){
//        super(context, attrs, Spinner.MODE_DIALOG);
//        if (attrs != null) {
//            final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.DropdownSpinnerWithPopup);
//            isDatePicker = a.getBoolean(R.styleable.DropdownSpinnerWithPopup_isDatePicker, false) ;
//            isTimePicker = a.getBoolean(R.styleable.DropdownSpinnerWithPopup_isTimePicker, false) ;
//            a.recycle();
//        }
//    }

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
        String value = "";
        Object o = getAdapter().getItem(getSelectedItemPosition());
        if (o == null) {
            return "";
        }
        if (o instanceof String) {
            value = (String) o;
        }
//        else if (o instanceof AddressSelector.AddressItem) {
//            value = ((AddressSelector.AddressItem) o).itemName;
//        }
        Log.d("DropdownSpinner", "GET SPINNER SELECTED VALUE: " + value);
        return value;
    }

//    public int getSelectedId(){
//        return selectedId;
//    }

    private String getCallPreferenceId() {
        String answer = "1";
        int selection = getSelectedItemPosition();
        if (selection == 1) {
            answer = "1";
        } else if (selection == 2) {
            answer = "2";
        } else if (selection == 3) {
            answer = "0";
        }
        return answer;
    }

    @Override
    public String getAnswerToSend() {
        String value = "";
        Object o = getAdapter().getItem(getSelectedItemPosition());
        if (o instanceof String) {
            value = (String) o;
            if (controlId == 9000005) {
                value = getCallPreferenceId();
            }
        } else if (o instanceof AddressSelector.AddressItem) {
            value = ((AddressSelector.AddressItem) o).itemId + "";
        }
        return value;
    }

    public void setItemSelected(String name) {
        for (int i = 0; i < getAdapter().getCount(); i++) {
            String value = "";
            Object o = getAdapter().getItem(i);
            if (o instanceof String) {
                value = (String) o;
            }
//            else if (o instanceof AddressSelector.AddressItem) {
//                value = ((AddressSelector.AddressItem) o).itemName;
//            }
            if (value.equals(name)) {
                setSelection(i);
                break;
            }
        }
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
        //Log.d("Spinner", "GET SELECTED ITEM POSITION: " + getSelectedItemPosition());
        return getAdapter() != null && getSelectedItemPosition() != 0;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        //selectedPosition = position;
        ((TextView) parent.getChildAt(position)).setTextColor(Color.WHITE);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

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

