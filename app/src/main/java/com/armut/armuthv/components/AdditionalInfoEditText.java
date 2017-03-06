package com.armut.armuthv.components;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.text.InputType;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.armut.armuthv.ArmutHVApp;
import com.armut.armuthv.R;

/**
 * Created by oguzemreozcan on 21/09/16.
 */

public class AdditionalInfoEditText extends LinearLayout implements QuestionView {

    private Context context;
    private ArmutHVApp app;
    private String hintText;
    private EditText editText;
    private QuestionTextView textView;
    private boolean typeNumber;
    private boolean longText;
    private boolean required;
    private boolean hasQuestion = true;
    private boolean isWhiteColored = false;
    private int order;
    private String answer = "";

    public AdditionalInfoEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        String questionText;
        if (attrs != null) {
            TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
                    R.styleable.AdditionalInfoEditText, 0, 0);
            try {
                typeNumber = a.getBoolean(R.styleable.AdditionalInfoEditText_typeNumber, false);
                longText = a.getBoolean(R.styleable.AdditionalInfoEditText_longText, true);
                required = a.getBoolean(R.styleable.AdditionalInfoEditText_longText, false);
                questionText = a.getString(R.styleable.AdditionalInfoEditText_questionText);
                hintText = a.getString(R.styleable.AdditionalInfoEditText_hintText);
                hasQuestion = questionText != null;
            } finally {
                a.recycle();
            }

            textView = new QuestionTextView(context, R.dimen.font_size_questions);
            editText = new EditText(context);
            //setBackgroundColor(getResources().getColor(R.color.accent_material_dark));
            editText.setBackgroundResource(R.drawable.selector_edit_text);
            if (longText) {
                editText.setLines(3);
                editText.setMaxLines(5);
                editText.setMinLines(3);
            } else {
                editText.setSingleLine();
            }
            if (typeNumber) {
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
            }
            //TODO
            //editText.setTextColor(ContextCompat.getColor(context, R.drawable.text_selector));
            //editText.setHintTextColor(ContextCompat.getColor(context, R.drawable.text_selector));
            //editText.setFocusable(true);
            if (hintText != null) {
                editText.setHint(hintText);
            }

            if (hasQuestion) {
                setQuestionText(questionText);
            }
        }
    }

    public AdditionalInfoEditText(Context context, ArmutHVApp app, String text, String placeholder, boolean typeNumber, boolean longText, boolean hasQuestion, final boolean isWhiteColored) {
        super(context);
        this.app = app;
        this.context = context;
        this.typeNumber = typeNumber;
        this.longText = longText;
        this.hasQuestion = hasQuestion;
        this.isWhiteColored = isWhiteColored;
        hintText = text;
        textView = new QuestionTextView(context, R.dimen.font_size_questions);
        editText = new EditText(context);
        //setBackgroundColor(getResources().getColor(R.color.accent_material_dark));
        editText.setImeActionLabel(context.getString(R.string.continueText), EditorInfo.IME_ACTION_DONE);
        //editText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        if (isWhiteColored) {
            editText.setBackgroundResource(R.drawable.selector_white_colored_edit_text);
        } else {
            editText.setBackgroundResource(R.drawable.selector_edit_text);
        }
        if (longText) {
            editText.setLines(3);
            editText.setMaxLines(7);
            editText.setMinLines(5);
            editText.setImeActionLabel(context.getString(R.string.continueText), KeyEvent.KEYCODE_ENTER);
            if(placeholder != null){
                if (!placeholder.equals("")) {
                    editText.setHint(placeholder);
                }
            }
            //editText.setInputType(InputType.TYPE_CLASS_TEXT);
        } else {
            editText.setSingleLine();
        }

        if (typeNumber) {
            editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        }

        //editText.setTextColor(R.drawable.text_selector);
        // editText.setHintTextColor(R.drawable.text_selector);
        if (text != null) {
            if (!hasQuestion) {
                if(editText.getHint() != null){
                    if(editText.getHint().equals("")){
                        editText.setHint(text);
                    }
                }
            }
            setQuestionText(text);
        } else {
            this.hasQuestion = false;
        }
        init();
    }

    public EditText getEditText() {
        return editText;
    }

    public void setText(String text) {
        if (text != null) {
            editText.setText(text);
        }
    }

    private void init() {
        setOrientation(LinearLayout.VERTICAL);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
        //setBackgroundColor(getResources().getColor(R.color.accent_material_dark));
        //setGravity(Gravity.CENTER)
        Resources res = getResources();
        //int marginBottom = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15, res.getDisplayMetrics());
        int marginTop = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, res.getDisplayMetrics());
        //int paddingVertical = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, res.getDisplayMetrics());
        //params.setMargins(marginTop, 0, marginTop, marginTop);
        params.setMargins(0, 0, 0, 3 * marginTop);
//        if(longText){
//            params.setMargins(0, 0, 0, 3*marginTop);
//        }else{
//            params.setMargins(0, 0, 0, 3*marginTop);
//        }
        //setPadding(marginBottom, paddingVertical, marginBottom, paddingVertical);
        editText.setPadding(marginTop, marginTop, marginTop, marginTop);
        //editText.setLayoutParams(editTextParams);
        if (hasQuestion) {
            addView(textView);
        }
        //editText.setTypeface(ArmutUtils.loadFont(context.getAssets(), context.getString(R.string.font_pera_regular)));
        int fontSize = (int) (getResources().getDimension(R.dimen.font_size_long_text) / getResources().getDisplayMetrics().density);
        editText.setTextSize(fontSize);
        editText.setEllipsize(TextUtils.TruncateAt.START);
        editText.setGravity(Gravity.TOP | Gravity.START);
        addView(editText);
        setLayoutParams(params);
        editText.setFocusableInTouchMode(true);
        editText.setFocusable(true);
        if (isWhiteColored) {
            editText.setTextColor(ContextCompat.getColor(context, R.color.blackish));

            editText.setHintTextColor(ContextCompat.getColor(context, R.color.warm_grey));
            // editText.setHintTextColor(Color.BLACK);
        } else {
            editText.setTextColor(Color.WHITE);
            editText.setHintTextColor(Color.WHITE);
        }

        editText.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    v.performClick();
                    // tv.setBackgroundResource(R.drawable.big_text_area_selected_shape);
                    if (isWhiteColored) {
                        editText.setTextColor(Color.WHITE);
                        editText.setHintTextColor(Color.WHITE);
                    } else {
                        editText.setTextColor(ContextCompat.getColor(context, R.color.blackish));
                        editText.setHintTextColor(ContextCompat.getColor(context, R.color.warm_grey));
                        //editText.setHintTextColor(Color.BLACK);
                    }
                } else {
                    //tv.setBackgroundResource(R.drawable.big_text_area_unselected_shape);
                    if (isWhiteColored) {
                        editText.setTextColor(ContextCompat.getColor(context, R.color.blackish));
                        editText.setHintTextColor(ContextCompat.getColor(context, R.color.warm_grey));
                        //editText.setHintTextColor(Color.BLACK);
                    } else {
                        editText.setTextColor(Color.WHITE);
                        editText.setHintTextColor(Color.WHITE);
                    }
                    try {
                        //TODO call hidekeyboard from here if it works
                        app.hideKeyboard(context);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        editText.requestFocus();
    }

    private void setQuestionText(String text) {
        textView.setText(text);
        textView.setGravity(Gravity.CENTER_HORIZONTAL);
    }

    @Override
    public boolean isFocused() {
        //if(focused){
        //Log.d("EDITTEXT","FOCUS: " + focused);
        //}
        return super.isFocused();
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
        answer = editText.getText().toString();
        return answer;
    }

    @Override
    public String getAnswerToSend() {
        if (answer == null) {
            return getSelectedValue();
        }
        return answer;
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
        return !editText.getText().toString().equals("");
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
