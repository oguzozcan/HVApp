package com.armut.armuthv.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.armut.armuthv.R;
import com.armut.armuthv.objects.OpportunityCancelReason;

import java.util.ArrayList;

/**
 * Created by oguzemreozcan on 30/06/16.
 */
public class CancelJobReasonsAdapter extends ArrayAdapter<OpportunityCancelReason> {

    private final String TAG = "CancelJobReasonsAdapter";
    private final ArrayList<OpportunityCancelReason> reasons;
    private final LayoutInflater inflater;

    public CancelJobReasonsAdapter(Context context, int resource, ArrayList<OpportunityCancelReason> objects) {
        super(context, resource, objects);
        this.reasons = objects;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void removeItem(int position){
        if(reasons != null){
            reasons.remove(position);
        }
        this.notifyDataSetChanged();
    }

    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {
        final ViewHolder holder;
        View rowView = convertView;
        final OpportunityCancelReason reason = reasons.get(position);
        if (rowView == null) {
            rowView = inflater.inflate(R.layout.address_list_view_row, parent, false);
            holder = new ViewHolder();
            holder.textView = (TextView) rowView.findViewById(R.id.textViewItem);
            rowView.setTag(holder);
        }
        else {
            holder = (ViewHolder) rowView.getTag();
        }
        holder.textView.setText(reason.getReason());
        return rowView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        final OpportunityCancelReason reason = reasons.get(position);
        if (convertView == null) {
            // inflate the layout
            convertView = inflater.inflate(R.layout.address_list_view_row, parent, false);
            holder = new ViewHolder();
            holder.textView = (TextView) convertView.findViewById(R.id.textViewItem);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }
        // object item based on the position
        holder.textView.setText(reason.getReason());
        return convertView;
    }

    public static class ViewHolder {
        TextView textView;
    }

}