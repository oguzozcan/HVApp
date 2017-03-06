package com.armut.armuthv.components;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.armut.armuthv.R;

/**
 * Created by oguzemreozcan on 13/07/16.
 */
public class AdditionalInfoTextView extends LinearLayout implements QuestionView {

    private final Context context;
    //final String hintText;
    private final TextView tv;
    private final QuestionTextView textView;
    //final boolean typeNumber;
    //final boolean longText;
    private boolean required;
    private boolean hasQuestion = true;
    private int order ;

    public AdditionalInfoTextView(Context context, String text, boolean typeNumber, boolean longText, boolean hasQuestion) {
        super(context);
        this.context = context;
        //this.typeNumber = typeNumber;
        //this.longText = longText;
        this.hasQuestion = hasQuestion;
        //hintText = text;
        textView = new QuestionTextView(context,R.dimen.font_size_questions);
        tv = new TextView(context);
        if(longText){
            tv.setLines(3);
            tv.setMaxLines(5);
            tv.setMinLines(3);
        }else{
            tv.setSingleLine();
        }

        if(typeNumber ){
            tv.setInputType(InputType.TYPE_CLASS_NUMBER);
        }

        //tv.setTypeface(ArmutUtils.loadFont(context.getAssets(), context.getString(R.string.font_pera_regular)));
        int fontSize = (int) (getResources().getDimension(R.dimen.font_size_long_text) / getResources().getDisplayMetrics().density);
        tv.setTextSize(fontSize);
        if(text != null){
            tv.setHint(text);
            setQuestionText(text);
        }else{
            this.hasQuestion = false;
        }
        init();
    }

    public void setSelected(boolean isSelected){
        if(isSelected){
            tv.setBackgroundResource(R.drawable.big_text_area_selected_shape);
            tv.setTextColor(ContextCompat.getColor(context, R.color.blackish));
        }else{
            tv.setBackgroundResource(R.drawable.big_text_area_unselected_shape);
            tv.setTextColor(Color.WHITE);
        }
    }

    private void init(){
        setOrientation(LinearLayout.VERTICAL);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER_VERTICAL;
        //setBackgroundColor(getResources().getColor(R.color.accent_material_dark));
        //setGravity(Gravity.CENTER);
        //TODO Check this values if they are in DP type
        params.setMargins(10, 5, 10, 5);
        setPadding(10, 10, 10, 10);
        //LayoutParams editTextParams = (LayoutParams)getLayoutParams();
//        editTextParams.setMargins(10,10,10,10);
        tv.setPadding(10, 10, 10, 10);
        //editText.setLayoutParams(editTextParams);
        if(hasQuestion){
            addView(textView);
        }
        addView(tv);
        setLayoutParams(params);
    }

    private void setQuestionText(String text){
        textView.setText(text);
        textView.setGravity(Gravity.CENTER_HORIZONTAL);
    }

    @Override
    public boolean isFocused() {
        boolean focused = super.isFocused();
        //if(focused){
        Log.d("EDITTEXT", "FOCUS: " + focused);
        //}
        return focused;
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
        return tv.getText().toString();
    }

    @Override
    public String getAnswerToSend() {
        return getSelectedValue();
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
        return !tv.getText().equals("");
    }

    public void setText(String text){
        tv.setText(text);
    }

    public String getText(){
        return tv.getText().toString();
    }

    private int controlId;

    @Override
    public int getControlId() {
        return controlId;
    }

    @Override
    public void setControlId(int controlId) {
        this.controlId = controlId;
    }
}
