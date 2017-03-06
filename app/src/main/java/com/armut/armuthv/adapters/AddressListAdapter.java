package com.armut.armuthv.adapters;

/**
 * Created by oguzemreozcan on 12/08/16.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.armut.armuthv.R;
import com.armut.armuthv.objects.Address;

import java.util.ArrayList;

public class AddressListAdapter extends ArrayAdapter<Address> {

    //final String TAG = "AddressListAdapter";
    private final Context context;
    private final ArrayList<Address> addresses;
    private final LayoutInflater inflater;
    private final AddressClicked callback;

    public AddressListAdapter(Context context, AddressClicked callback, int resource, ArrayList<Address> objects) {
        super(context, resource, objects);
        this.context = context;
        this.callback = callback;
        this.addresses = objects;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        View rowView = convertView;
        final Address address = addresses.get(position);
        if (rowView == null) {
            rowView = inflater.inflate(R.layout.profile_address_card, parent, false);
            holder = new ViewHolder();
            holder.address = (TextView) rowView.findViewById(R.id.name);
            //holder.address.setTypeface(ArmutUtils.loadFont(context.getAssets(), context.getString(R.string.font_pera_regular)));
            // holder.deleteButton = (ImageView) rowView.findViewById(R.id.deleteButton);
            rowView.setTag(holder);
        } else {
            holder = (ViewHolder) rowView.getTag();
        }
        String addressTxt = address.getDistrictName() + " " + address.getAddress() + "\n" + address.getCityName() + ", " + address.getStateName();
        holder.address.setText(addressTxt);
//        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//            }
//        });

        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onAddressClicked(position);
            }
        });
        return rowView;//super.getView(position, convertView, parent);
    }

    public static class ViewHolder {
        TextView address;
        // ImageView deleteButton;
    }


    public interface AddressClicked {
        void onAddressClicked(int position);
    }

}

