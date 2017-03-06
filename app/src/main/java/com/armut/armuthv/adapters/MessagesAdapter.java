package com.armut.armuthv.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.armut.armuthv.FullScreenImageActivity;
import com.armut.armuthv.MainActivity;
import com.armut.armuthv.R;
import com.armut.armuthv.objects.Message;
import com.armut.armuthv.utils.Constants;
import com.armut.armuthv.utils.TimeUtils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by oguzemreozcan on 26/07/16.
 */
public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.DataObjectHolder> {

    private final Activity activity;
    private ArrayList<Message> data;
    private LayoutInflater inflater = null;
    //private final String TAG = "MESSAGES_ADAPTER";
//    private String imagePath;
    private String customerImagePath;
    private String myImagePath;
    private String messageImagePath;
    private int layoutId;
    private int businessModel;
    private String path = "";

    static class DataObjectHolder extends RecyclerView.ViewHolder implements OnMapReadyCallback {
        final TextView messagesTv;
        final TextView dateTv;
        final CircleImageView thumbnailImage;
        final ImageView imageView;
        final MapView mapView;
        final Activity activity;
        GoogleMap gMap;

        DataObjectHolder(View itemView, Activity activity) {
            super(itemView);
            this.activity = activity;
            thumbnailImage = (CircleImageView) itemView.findViewById(R.id.thumbnailImage);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
            messagesTv = (TextView) itemView.findViewById(R.id.commentTv);
            dateTv = (TextView) itemView.findViewById(R.id.dateTv);
            mapView = (MapView) itemView.findViewById(R.id.mapImageView);
            initializeMapView();
        }

        /**
         * Initialises the MapView by calling its lifecycle methods.
         */
        void initializeMapView() {
            try{
                if (mapView != null) {
                    // Initialise the MapView
                    mapView.onCreate(null);
                    // Set the map ready callback to receive the GoogleMap object
                    mapView.getMapAsync(this);
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }

        @Override
        public void onMapReady(GoogleMap googleMap) {
            MapsInitializer.initialize(activity);
            gMap = googleMap;
            LatLng data = (LatLng) mapView.getTag();
            if (data != null) {
                setMapLocation(gMap, data);
            }
            //you can move map here to item specific 'location'
            //int pos = getPosition();
            //get 'location' by 'pos' from data list
            //then move to 'location'
            //gMap.moveCamera();
        }


    }

    public ArrayList<Message> getList(){
        return data;
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(layoutId, parent, false);
        return new DataObjectHolder(view, activity);
    }

    private static void setMapLocation(GoogleMap map, LatLng data) {
        // Add a marker for this item and set the camera
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(data, 12f));
        map.addMarker(new MarkerOptions().position(data));
        // Set the map type back to normal.
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }

    public MessagesAdapter(Activity act, ArrayList<Message> messages, String myImagePath, String customerImagePath, int businessModel) {
        this.activity = act;
        this.businessModel = businessModel;
        if (messages != null) {
            int size = messages.size();
            if(size > 0) {
                if(size > 1){
                    messages = mergeMessages(messages);
                }
                Collections.sort(messages, new Comparator<Message>() {
                    @Override
                    public int compare(Message lhs, Message rhs) {
                        return lhs.getTime().compareTo(rhs.getTime());
                    }
                });
                data = new ArrayList<>(messages);
            } else {
                data = new ArrayList<>();
                data.add(prepareDummyMessage());
            }
        }
        inflater = (LayoutInflater) act
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //TODO profileimage
        this.myImagePath = myImagePath;
        this.customerImagePath = customerImagePath;
        try {
            messageImagePath = Constants.IMAGE_BASE_URL_REAL + Constants.SERVICE_IMAGES_POSTFIX;
            //messageImagePath = new StringBuilder(Constants.IMAGE_BASE_URL_REAL).append("/").append(Constants.THUMBNAIL_SIZE).append("/").append(Constants.THUMBNAIL_SIZE).append(Constants.SERVICE_IMAGES_POSTFIX).toString();
        } catch (Exception e) {
            //myImagePath = "";
            e.printStackTrace();
        }
        //Log.d("MessagesAdapter", "BUSINESS MODEL: " + businessModel);
    }

    private Message prepareDummyMessage() {
        Message message = new Message();
        message.setIsUser(false);
        message.setId(Message.DUMMY_MESSAGE);
        message.setContentType(Message.MESSAGE_TYPE_TEXT);
        message.setMessage(activity.getString(businessModel == Constants.BUSINESS_MODEL_RESERVATION ? R.string.empyt_message_to_provider_bm2 : R.string.empty_message_to_provider));
        message.setTime(TimeUtils.getTodayJoda(TimeUtils.dfISOMS));
        return message;
    }

    private ArrayList<Message> mergeMessages(ArrayList<Message> messages) {
        ArrayList<Message> mergedMessages = new ArrayList<>();
        for (int i = 1; i < messages.size(); i++) { //int i = messages.size() - 1; i > 0 ; i --
            Message message = messages.get(i - 1);
            Message message1 = messages.get(i);
            if (i - 1 == 0) {
                mergedMessages.add(message);
            }
            if (message1 != null) {
                if (message1.isMergedMessage()) {
                    message.setTime(message1.getTime());
                    message.addToMessage(message1.getMessage());
                    messages.set(i, message);
                } else {
                    if (message.isMergedMessage() && i - 1 > 0) {
                        mergedMessages.add(message);
                    }
                    mergedMessages.add(message1);
                }
            } else {
                mergedMessages.add(message);
            }
        }
        return mergedMessages;
    }

    public void add(Message data) {
        if (this.data == null) {
            this.data = new ArrayList<>();
        }
        //Log.d("ADD MESSAGE", "MERGED MESSAGE SO ADD TO CURRENT:" + data.isMergedMessage());
        if (data.isMergedMessage() && this.data.size() > 0) {
            Message lastMessage = this.data.get(this.data.size() - 1);
            lastMessage.addToMessage(data.getMessage());
            lastMessage.setTime(data.getTime());
        } else {
            this.data.add(data);
        }
        notifyDataSetChanged();
    }

    public void remove(int position){
        this.data.remove(position);
        notifyDataSetChanged();
    }

    public int getSize(){
        return data.size();
    }

    public void addData(ArrayList<Message> data) {
        if (this.data == null) {
            this.data = data;
        } else {
            // this.data.addAll(data);
            notifyDataSetChanged();
        }
    }

    @Override
    public void onBindViewHolder(DataObjectHolder holder, int position) {
        Message message = getItem(position);
        int viewType = getItemViewType(position);
        String imagePostFix = "";
        if (viewType == 0 || viewType == 1) {
            holder.imageView.setVisibility(View.GONE);
            holder.mapView.setVisibility(View.GONE);
        } else if (viewType == 2 || viewType == 3) {
            holder.messagesTv.setVisibility(View.INVISIBLE);
            holder.imageView.setVisibility(View.VISIBLE);
            holder.mapView.setVisibility(View.GONE);
            imagePostFix = message.getMessage();
        } else if (viewType == 4 || viewType == 5) {
            holder.messagesTv.setVisibility(View.INVISIBLE);
            holder.mapView.setVisibility(View.VISIBLE);
            holder.imageView.setVisibility(View.GONE);
            holder.initializeMapView();

            String[] latlngArray = message.getMessage().split("\\,");
            LatLng latlng = new LatLng(Double.parseDouble(latlngArray[0]), Double.parseDouble(latlngArray[1]));
            holder.mapView.setTag(latlng);
            // Ensure the map has been initialised by the on map ready callback in ViewHolder.
            // If it is not ready yet, it will be initialised with the NamedLocation set as its tag
            // when the callback is received.
            if (holder.gMap != null) {
                // The map is already ready to be used
                setMapLocation(holder.gMap, latlng);
            }
        }
        holder.messagesTv.setText(message.getMessage());//quote.getUserFirstName().concat(" ").concat(quote.getUserLastName())
        try {
            //TimeUtils.compareDateWithToday(message.getTime(), TimeUtils.dfISOMS);
            holder.dateTv.setText(dateRepresentationDecider(message.getTime()));
        } catch (Exception e) {
            e.printStackTrace();
            holder.dateTv.setText(message.getTime());
        }
        try {
            //TODO profile pic
//            if(!path.equals("")){
//                Picasso.with(activity).load(path).fit().placeholder(R.drawable.default_users_7).into(holder.thumbnailImage);
//            }
            if (message.isUser()) {
                Picasso.with(activity).load(myImagePath).fit().placeholder(R.drawable.default_users_7).into(holder.thumbnailImage);
            } else {
                if(!message.getId().equals(Message.DUMMY_MESSAGE)){
                    Picasso.with(activity).load(customerImagePath).fit().placeholder(R.drawable.default_users_7).into(holder.thumbnailImage);
                }else{
                    Picasso.with(activity).load(R.drawable.appicon).fit().placeholder(R.drawable.default_users_7).into(holder.thumbnailImage);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (!imagePostFix.equals("")) {
                final String imageUrl = messageImagePath + imagePostFix + ";width=" + Constants.THUMBNAIL_SIZE + ";height=" + Constants.THUMBNAIL_SIZE + ";mode=crop;";
                Log.d("IMAGE", "IMAGE URL: " + imageUrl);
                final String imageName = imagePostFix;
                Picasso.with(activity).load(imageUrl).fit().placeholder(R.drawable.default_users_7).into(holder.imageView);
                holder.imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String[] photoNames = new String[1];
                        photoNames[0] = imageName;
                        Intent intent = new Intent(activity, FullScreenImageActivity.class);
                        intent.putExtra("POSITION", 0);
                        intent.putExtra("ARRAY", photoNames);
                        intent.putExtra("PROFILE_PICS", false);
                        activity.startActivity(intent);
                        MainActivity.setTranslateAnimation(activity);
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onViewRecycled(DataObjectHolder holder) {
        super.onViewRecycled(holder);
        if (holder.gMap != null) {
            holder.gMap.clear();
            holder.gMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        }
    }

    private String dateRepresentationDecider(String date){
        int days = TimeUtils.compareDateWithToday(date, TimeUtils.dfISOMS);
        date = TimeUtils.convertDateTimeFormat(TimeUtils.dfISOMS, TimeUtils.dtfOutWOYear, date);
        if(days < 2 ){
            String textToReplace = date.substring(0, date.length() - 6);
            //Log.d("Adapter", "TextToReplace: " + textToReplace);
            if(days == 0){
                date = date.replace(textToReplace, "Bugün");
            }else if(days == 1){
                date = date.replace(textToReplace, "Dün");
            }
        }
        return date;
    }

    @Override
    public int getItemCount() {
        if (data == null) {
            return 0;
        }
        return data.size();
    }

    public Message getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        Message text = getItem(position);
        //User and Provider layouts are converted
        if (text.isUser()) {
            path = myImagePath;
            switch (text.getContentType()) {
                case Message.MESSAGE_TYPE_TEXT:
                    layoutId = R.layout.message_provider_row_layout;
                    return 0;
                case Message.MESSAGE_TYPE_IMAGE:
                    layoutId = R.layout.message_photo_provider_row_layout;
                    return 2;
                default:
                    layoutId = R.layout.message_map_provider_row_layout;
                    return 4;
            }
        } else {
            path = customerImagePath;
            switch (text.getContentType()) {
                case Message.MESSAGE_TYPE_TEXT:
                    layoutId = R.layout.message_user_row_layout;
                    return 1;
                case Message.MESSAGE_TYPE_IMAGE:
                    layoutId = R.layout.message_photo_user_row_layout;
                    return 3;
                default:
                    layoutId = R.layout.message_map_user_row_layout;
                    return 5;
            }
        }
    }
}
