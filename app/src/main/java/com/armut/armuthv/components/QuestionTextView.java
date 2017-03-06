package com.armut.armuthv.components;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.armut.armuthv.R;

/**
 * Created by oguzemreozcan on 13/07/16.
 */
public class QuestionTextView extends TextView {

    private int fontTextSize;
    private int color;
    private boolean isFontBold = true;

    public QuestionTextView(Context context, int fontSize) {
        super(context);
        this.fontTextSize = fontSize;
        init(context);
    }

    public QuestionTextView(Context context, int fontSize, int color, boolean isFontBold) {
        super(context);
        this.fontTextSize = fontSize;
        this.color = color;
        this.isFontBold = isFontBold;
        init(context);
    }

    public QuestionTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context){
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER_HORIZONTAL;
        int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 13, getResources().getDisplayMetrics());
        params.bottomMargin = margin;
        params.topMargin = margin;
        setPadding(margin,0, margin,0);
//        setTypeface(ArmutUtils.loadFont(context.getAssets(), context.getString(R.string.font_pera_bold)));
//        if(!isFontBold){
//            setTypeface(ArmutUtils.loadFont(context.getAssets(), context.getString(R.string.font_pera_regular)));
//        }
        if(fontTextSize == 0){
            fontTextSize = R.dimen.font_size_questions;
        }
        if(color == 0){
            setTextColor(Color.WHITE);
        }
        int fontSize = (int) (getResources().getDimension(fontTextSize) / getResources().getDisplayMetrics().density);
        //int fontSize = (int) getResources().getDimension(R.dimen.font_size_questions);
        setTextSize(fontSize);
        setGravity(Gravity.CENTER);
        setLayoutParams(params);
    }
}
