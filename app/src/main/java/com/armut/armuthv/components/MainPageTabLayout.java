package com.armut.armuthv.components;

import android.content.Context;
import android.support.design.widget.TabLayout;
import android.util.AttributeSet;

/**
 * Created by oguzemreozcan on 07/06/16.
 */
public class MainPageTabLayout extends TabLayout {
    //Tab layout that has an desired height
    public MainPageTabLayout(Context context) {
        super(context);
    }

    public MainPageTabLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MainPageTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        super.setMeasuredDimension(widthMeasureSpec,
                MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(heightMeasureSpec), MeasureSpec.EXACTLY));
    }

}
