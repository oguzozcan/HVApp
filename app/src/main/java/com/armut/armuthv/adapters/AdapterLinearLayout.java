package com.armut.armuthv.adapters;

/**
 * Created by oguzemreozcan on 12/08/16.
 */

import android.annotation.TargetApi;
import android.content.Context;
import android.database.DataSetObserver;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.LinearLayout;

import java.util.ArrayList;

/**
 * A linear layout that will contain views taken from an adapter. It differs from the list view in the fact that it will
 * not optimize anything and draw all the views from the adapter. It also does not provide scrolling. However, when you
 * need a layout that will render views horizontally and you know there are not many child views, this is a good
 * option.
 *
 * @author Vincent Mimoun-Prat @ MarvinLabs
 */
public class AdapterLinearLayout extends LinearLayout {

    private Adapter adapter;
    private ArrayList<View> views;
    private boolean selectable;
    private OnItemClick callback;
    private final DataSetObserver dataSetObserver = new DataSetObserver() {
        @Override
        public void onChanged() {
            super.onChanged();
            reloadChildViews();
        }
    };

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public AdapterLinearLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setOrientation(LinearLayout.VERTICAL);
    }

    public AdapterLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(LinearLayout.VERTICAL);
    }

    public AdapterLinearLayout(Context context) {
        super(context);
        setOrientation(LinearLayout.VERTICAL);
    }

    public void setAdapter(Adapter adapter) {
        if (this.adapter == adapter) return;
        this.adapter = adapter;
        if (adapter != null) adapter.registerDataSetObserver(dataSetObserver);
        reloadChildViews();
    }

    public void setOnItemClickListener(OnItemClick callback) {
        this.callback = callback;
    }

    public void setItemsSelectable(boolean selectable) {
        this.selectable = selectable;
    }

    public void setItemSelected(boolean selected, int index) {
        if (views == null) {
            return;
        }
        if (views.size() == 0) {
            return;
        }
        try {
            views.get(index).setSelected(selected);
            if (callback != null) {
                callback.onItemClick(index, adapter.getItem(index));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (adapter != null) adapter.unregisterDataSetObserver(dataSetObserver);
    }

    private void reloadChildViews() {
        removeAllViews();

        if (adapter == null) return;
        views = new ArrayList<>();
        int count = adapter.getCount();
        for (int position = 0; position < count; ++position) {
            View v = adapter.getView(position, null, this);
            if (v != null) {
                addView(v);
                views.add(v);
                if (selectable) {
                    v.setOnClickListener(new ItemClickListener(position));
                }
            }
        }
        requestLayout();
    }

    public interface OnItemClick {
        void onItemClick(int position, Object item);
    }

    public class ItemClickListener implements OnClickListener {

        final int position;

        boolean selected = false;

        public ItemClickListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            selected = !selected;
            setOtherItems(v, position);
            Log.d("AdapterLinearLayout", "ON CLICK");
            //v.setSelected(selected);
            if (callback != null) {
                callback.onItemClick(position, adapter.getItem(position));
            }
        }

        public void setOtherItems(View view, int position) {
            int count = adapter.getCount();
            for (int i = 0; i < count; ++i) {
                //View v = adapter.getView(i, null, AdapterLinearLayout.this);
                //  if (v != null){
                if (i != position) {
                    //views.get(i).setSelected(false);
                    //v.setSelected(false);
                    views.get(i).setSelected(false);
                    //Log.d("AdapterLinearLayout", "SET UNSELECTED " + i);
                } else {
                    // Log.d("AdapterLinearLayout", "SET SELECTED " + i);
                    //v.setSelected(true);
                    views.get(i).setSelected(true);
                }
                //}
            }
            requestLayout();
        }
    }
}