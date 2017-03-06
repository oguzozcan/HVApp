package com.armut.armuthv.components;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.armut.armuthv.R;

/**
 * Created by oguzemreozcan on 12/07/16.
 */
public class NumberSelector extends LinearLayout implements QuestionView {

    private TextView numberTv;
    private TextView infoTv;
   // private QuestionTextView questionTv;
    private StrokeCircleButton incrementButton;
    private StrokeCircleButton decrementButton;
    private int number = 2;
    private int min;
    private int max;
    private String question;
    private String[] values;
    private boolean selectorWithPredefinedValues;
    private int defaultSelectionIndex;
    private int index;
    private int order;
    private boolean required;
    private int selectedColor;
    private int unSelectedColor;
    private Drawable decrementDrawable;
    private Drawable incrementDrawable;
    private String unit;
    //private final QuestionsFragment parentFragment;
//    private Float[] valueMultiplierArray;
    private int controlId;
    private int selectedValue;
   // private final int quantityQuestionId;

    public NumberSelector(Context context, AttributeSet attrs){
        super(context, attrs);
        if (attrs != null) {
            final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.NumberSelector);
            selectedColor = a.getColor(R.styleable.NumberSelector_selected_color, ContextCompat.getColor(context,R.color.colorPrimary));
            unSelectedColor = a.getColor(R.styleable.NumberSelector_unselected_color, ContextCompat.getColor(context,R.color.gray2));
            selectorWithPredefinedValues = a.getBoolean(R.styleable.NumberSelector_predefined_values, false) ;
            unit = a.getString(R.styleable.NumberSelector_unit);
            question =  a.getString(R.styleable.NumberSelector_question);
            a.recycle();
        }
        init(context);
    }

    private void init(Context context){
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //quantityQuestionId = QuestionProvider.getInstance().getService().getQuantityQuestionId();
        if (inflater != null) {
            View view = inflater.inflate(R.layout.number_selector, this);
            numberTv = (TextView) view.findViewById(R.id.numberTv);
            infoTv = (TextView) view.findViewById(R.id.infoTv);
            incrementButton = (StrokeCircleButton) view.findViewById(R.id.incrementButton);
            decrementButton = (StrokeCircleButton) view.findViewById(R.id.decrementButton);
            decrementDrawable = decrementButton.getDrawable();
            incrementDrawable = incrementButton.getDrawable();
            incrementButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    incrementNumber();
                }
            });
            decrementButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    decrementNumber();
                }
            });
            int marginBottom = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15, getResources().getDisplayMetrics());
            //int marginTop = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics());
            int paddingVertical = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics());
            //params.setMargins(0, 0, 0, marginTop);
            setPadding(marginBottom, paddingVertical, marginBottom, paddingVertical);
        }
    }

    public NumberSelector(Context context, boolean selectorWithPredefinedValues, int backColor) { // , QuestionsFragment fragment
        super(context);
        this.selectorWithPredefinedValues = selectorWithPredefinedValues;
       // parentFragment = fragment;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //quantityQuestionId = QuestionProvider.getInstance().getService().getQuantityQuestionId();
        if (inflater != null) {
            View view = inflater.inflate(R.layout.number_selector, this);
            numberTv = (TextView) view.findViewById(R.id.numberTv);
            infoTv = (TextView) view.findViewById(R.id.infoTv);
            //questionTv = (QuestionTextView) view.findViewById(R.id.questionTv);
          //  numberTv.setTypeface(ArmutUtils.loadFont(context.getAssets(), context.getString(R.string.font_pera_regular)));
           // infoTv.setTypeface(ArmutUtils.loadFont(context.getAssets(), context.getString(R.string.font_pera_regular)));
            //questionTv.setTypeface(ArmutUtils.loadFont(context.getAssets(), context.getString(R.string.font_pera_bold)));
            incrementButton = (StrokeCircleButton) view.findViewById(R.id.incrementButton);
            decrementButton = (StrokeCircleButton) view.findViewById(R.id.decrementButton);
            decrementDrawable = decrementButton.getDrawable();
            incrementDrawable = incrementButton.getDrawable();
            //int color = getContext().getResources().getColor(R.color.colorPrimary);//Color.parseColor("#2196F3"); //The color u want
            if(backColor == -1){
                selectedColor = ContextCompat.getColor(context,R.color.colorPrimary);
            }else{
                selectedColor = backColor;
            }
            unSelectedColor = ContextCompat.getColor(context,R.color.gray2);
            //setButtonResourceAccordingToLimits();
//            decrementDrawable.setColorFilter(selectedColor, PorterDuff.Mode.MULTIPLY);
//            incrementDrawable.setColorFilter(selectedColor, PorterDuff.Mode.MULTIPLY);
            incrementButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    incrementNumber();
                }
            });
            decrementButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    decrementNumber();
                }
            });
            int marginBottom = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15, getResources().getDisplayMetrics());
            //int marginTop = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics());
            int paddingVertical = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics());
            //params.setMargins(0, 0, 0, marginTop);
            setPadding(marginBottom, paddingVertical, marginBottom, paddingVertical);
        }
    }

//    public void setValueMultiplierArray(Float[] multiplierArray){
//        valueMultiplierArray = multiplierArray;
//    }

    private void incrementNumber(){
        if(!selectorWithPredefinedValues){
            if(number < max){
                number ++;
                numberTv.setText(Integer.toString(number));
            }
        }else{
            if(values != null){
                if(index < values.length - 1){
                    ++index;
                    numberTv.setText(values[index]);
                    int convertedValue = convertTextToInt(values[index]);
                    Log.d("CONVERTED VALUE", "VALUE : " + convertedValue);
                    setSelectedValue(convertedValue);
//                    parentFragment.initializeCalculation(valueMultiplierArray[index], controlId, convertedValue);
                    setButtonResourceAccordingToLimits(index);
                }
            }
        }
    }

    private int convertTextToInt(String text){
        int answer;
        try{
            answer = Integer.parseInt(text);
            return answer;
        }catch(Exception e){
            char[] chars = text.toCharArray();
            StringBuilder builder = new StringBuilder();
            for(char c : chars){
                if(Character.isDigit(c)){
                    builder.append(c);
                }
            }
            return builder.length() > 0 ? Integer.parseInt(builder.toString()) : 0;
        }
    }

    private void setButtonResourceAccordingToLimits(int index){

        if(index == values.length - 1){
            incrementDrawable.setColorFilter(unSelectedColor, PorterDuff.Mode.MULTIPLY);
            incrementButton.setColor(unSelectedColor);
            decrementDrawable.setColorFilter(selectedColor, PorterDuff.Mode.MULTIPLY);
            decrementButton.setColor(selectedColor);
            //incrementButton.setImageResource(R.drawable.stepper_add_inactive);
        }else if(index == 0){
            decrementDrawable.setColorFilter(unSelectedColor, PorterDuff.Mode.MULTIPLY);
            decrementButton.setColor(unSelectedColor);
            incrementDrawable.setColorFilter(selectedColor, PorterDuff.Mode.MULTIPLY);
            incrementButton.setColor(selectedColor);
            //decrementButton.setImageResource(R.drawable.stepper_add_inactive);
        }else{
            decrementDrawable.setColorFilter(selectedColor, PorterDuff.Mode.MULTIPLY);
            incrementDrawable.setColorFilter(selectedColor, PorterDuff.Mode.MULTIPLY);
            incrementButton.setColor(selectedColor);
            decrementButton.setColor(selectedColor);
            //decrementButton.setImageResource(R.drawable.stepper_add);
            //incrementButton.setImageResource(R.drawable.stepper_add);
        }
    }

    private void decrementNumber(){
        if(!selectorWithPredefinedValues){
            if(number > min){
                number --;
                numberTv.setText(Integer.toString(number));
            }
        }else{
            if(values != null) {
                if(index > 0){
                    --index;
                    numberTv.setText(values[index]);
                    int convertedValue = convertTextToInt(values[index]);
                    Log.d("CONVERTED VALUE", "VALUE : " + convertedValue);
                    setSelectedValue(convertedValue);
                   // parentFragment.initializeCalculation(valueMultiplierArray[index], controlId, convertedValue);
                    setButtonResourceAccordingToLimits(index);
                }
            }
        }
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
        numberTv.setText(number);
    }

//    public String getQuestion() {
//        return question;
//    }
//
//    public void setQuestion(String question) {
//        this.question = question;
//        questionTv.setText(question);
//    }

    public String[] getValues() {
        return values;
    }

    public void setValues(String[] values) {
        this.values = values;
        //Log.d("SET VALUES", "SET VALUES - SET DEFAULT VALUE: " + QuestionProvider.getInstance().getService().getQuantityQuestionId() + "  - control id: " + controlId);
//        if(quantityQuestionId != controlId){
//            index = values.length/2 - 1;
//        }
//        else{
            int defaultValue = 0;//(int)QuestionProvider.getInstance().quantity;
            Log.d("SET VALUES", "SET QUANTITY " + defaultValue);
            int i = 0;
            for(String value : values){
                int intValue = convertTextToInt(value);
                if(intValue == defaultValue){
                    index = i;
                    break;
                }
                i ++;
            }
//        }
        int convertedValue = convertTextToInt(values[index]);
        numberTv.setText(values[index]);
        setSelectedValue(convertedValue);
        //parentFragment.initializeCalculation(valueMultiplierArray[index], controlId, convertedValue);
        setButtonResourceAccordingToLimits(index);
    }

    //Find answer if present and set
    public void setValue(int answer){
        for (String value : values) {
            try {
                int convertedValue = Integer.parseInt(value);
                if (answer == convertedValue) {
                    numberTv.setText(values[index]);
                    setSelectedValue(convertedValue);
                   // parentFragment.initializeCalculation(valueMultiplierArray[index], controlId, convertedValue);
                    setButtonResourceAccordingToLimits(index);
                    break;
                }
            } catch (NumberFormatException ex) {
                ex.printStackTrace();
            }
        }
    }

    public int getDefaultSelectionIndex() {
        return defaultSelectionIndex;
    }

    public void setDefaultSelectionIndex(int defaultSelectionIndex) {
        this.defaultSelectionIndex = defaultSelectionIndex;
        index = defaultSelectionIndex;
    }

    private void setSelectedValue(int value){
        selectedValue = value;
//        if(QuestionProvider.getInstance().getQuantityVariableValue(controlId) != 0){
//            QuestionProvider.getInstance().setQuantityVariables(controlId, selectedValue);
//        }
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
        return values[index];
    }

    @Override
    public String getAnswerToSend() {
        return values[index];
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
        return true;
    }

    @Override
    public int getControlId() {
        return controlId;
    }

    @Override
    public void setControlId(int controlId) {
        this.controlId = controlId;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
        if(unit == null){
            infoTv.setVisibility(View.GONE);
        }else{
            infoTv.setText(unit);
            infoTv.setVisibility(View.VISIBLE);
        }
    }
}

