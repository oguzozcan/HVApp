package com.armut.armuthv.fragments;

import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.adjust.sdk.Adjust;
import com.adjust.sdk.AdjustEvent;
import com.armut.armuthv.R;
import com.armut.armuthv.adapters.MessagesAdapter;
import com.armut.armuthv.busevents.MiscEvents;
import com.armut.armuthv.objects.BasicNameValuePair;
import com.armut.armuthv.objects.Message;
import com.armut.armuthv.utils.ArmutUtils;
import com.armut.armuthv.utils.Constants;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.squareup.otto.Subscribe;

import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Response;

/**
 * Created by oguzemreozcan on 25/07/16.
 */
public class JobMessagesFragment extends BasicFragment  {

    private RecyclerView chatListView;
    private LinearLayout progressBar;
    private FullScrollCallback callback;
    //private int jobQuoteId;
    private String userId;

    public JobMessagesFragment() {
        // Required empty public constructor
    }

    @Override
    public void setTag() {
        TAG = "JobMessagesFragment";
    }

    public static JobMessagesFragment newInstance(int jobQuoteId) {
        JobMessagesFragment fragment = new JobMessagesFragment();
        Bundle args = new Bundle();
        //args.putInt("INDEX", index);
        args.putInt("JOB_QUOTE_ID", jobQuoteId);
//        args.putString("IMAGE_PATH", profileImagePath);
        Log.d("JOB MESSAGES FRAGMENT", "JOB QUOTE ID: " + jobQuoteId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onPause() {
        super.onPause();
        //this.getActivity().getApplicationContext().unregisterReceiver(mMessageReceiver);
    }

    @Override
    public void onResume() {
        super.onResume();
        //this.getActivity().getApplicationContext().registerReceiver(mMessageReceiver, new IntentFilter("GcmIntentService"));
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            callback = (FullScrollCallback) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement FullScrollCallback");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.messages_main_layout, container, false);
        chatListView = (RecyclerView) rootView.findViewById(R.id.chatListView);
        chatListView.setHasFixedSize(false);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this.getActivity());
        chatListView.setLayoutManager(mLayoutManager);
        chatListView.setNestedScrollingEnabled(true);
        chatListView.setItemAnimator(new DefaultItemAnimator());
        progressBar = (LinearLayout) rootView.findViewById(R.id.progressBar1);
        int jobQuoteId = getArguments().getInt("JOB_QUOTE_ID");
        Log.d(TAG, "JOB QUOTE ID: " + jobQuoteId);
        app.getBus().post(new MiscEvents.MessagesRequest(jobQuoteId));
        progressBar.setVisibility(View.VISIBLE);
        //index = getArguments().getInt("INDEX", -1);
//        profileImagePath = getArguments().getString("IMAGE_PATH");
//        useLoader(jobQuoteId);
//        userId = app.getUserId();
        return rootView;
    }

    @Subscribe
    public void onNotificationReceived(MiscEvents.NotificationReceivedEvent event){
        try{
            if(event != null && getActivity() != null){
                if(event.jobQuoteId == getArguments().getInt("JOB_QUOTE_ID")){
                    app.getBus().post(new MiscEvents.MessagesRequest(event.jobQuoteId));
                    Log.d(TAG, "REFRESH MESSAGE FEED AND CANCEL NOTIFICATION: " + event.jobId);
                    progressBar.setVisibility(View.VISIBLE);
                    NotificationManager notificationManager = (NotificationManager)getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.cancel(event.jobId);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Subscribe
    public void onMessagesLoaded(MiscEvents.MessagesResponse response) {
        Response<ArrayList<Message>> messagesResponse = response.getResponse();
        if (messagesResponse == null) {
            chatListView.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            return;
        }
        ArrayList<Message> data = messagesResponse.body();
        if(data == null || app.getUser() == null){
            chatListView.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            return;
        }
        ArrayList<Message> dataList = new ArrayList<>();
        String myUserId = app.getUser().getUserInfo().getUserId();//"testboyaci1@armut.com";//"salih";//app.getUserId();

        for (int i = data.size() - 1; i >= 0; i--) {
            Message message1 = data.get(i);
            message1.setIsUser(message1.getUserId().equals(myUserId));
            Log.d(TAG, "MY USER ID: " + myUserId + " - messageUserId: " + message1.getUserId());
            if (i - 1 >= 0) {
                Message message0 = data.get(i - 1);
                message0.setIsUser(message0.getUserId().equals(myUserId));
                message0.compareMessage(message1);
            }
            //Log.d(TAG, "MESSAGES: " + message1.getMessage());
            dataList.add(message1);
            Log.d(TAG, "MESSAGE : " + message1.getMessage());
        }
//        Constants.IMAGE_BASE_URL_REAL + Constants.PROFILE_PICS_POSTFIX + photo + ";width=" + Constants.THUMBNAIL_SIZE + ";height=" + Constants.THUMBNAIL_SIZE+";mode=crop;"
        int businessModel = app.getDataSaver().getBusinessModel(Constants.BUSINESS_MODEL_ID);
        MessagesAdapter adapter = new MessagesAdapter(getActivity(), dataList, getMyImagePath(), getCustomerImagePath(dataList), businessModel); // , profileImagePath
        chatListView.setAdapter(adapter);
        chatListView.postDelayed(new Runnable() {
            @Override
            public void run() {
                editTextClicked();//new EventBusEditTextClickedObject());
                //chatListView.scrollToPosition(adapter.getItemCount() - 1);
            }
        }, 150);
        if(dataList.size() > 3 && callback != null){
            callback.scrollToBottom(true);
        }
        chatListView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
    }

    public String getMyImagePath(){
        String myImagePath = "";
        String picUrl = app.getUser().getUserInfo().getPicUrl();
        if(picUrl != null){
            myImagePath = Constants.IMAGE_BASE_URL_REAL + Constants.PROFILE_PICS_POSTFIX + picUrl + ";width=" + Constants.THUMBNAIL_SIZE + ";height=" + Constants.THUMBNAIL_SIZE+";mode=crop;";
        }
        return myImagePath;
    }

    //TODO
    public String getCustomerImagePath(ArrayList<Message> messages){
        //String imagePath = null;
//        for(Message message : messages){
//            if(!message.isUser()){
//               imagePath = Constants.IMAGE_BASE_URL_REAL + Constants.PROFILE_PICS_POSTFIX + message.get + ";width=" + Constants.THUMBNAIL_SIZE + ";height=" + Constants.THUMBNAIL_SIZE+";mode=crop;";
//            }
//        }
        return null;
    }

    public void editTextClicked() {
        try {
            if (chatListView != null && attached) {
                final int itemCount = chatListView.getAdapter().getItemCount();
                if (itemCount > 0) {
                    scrollDown(itemCount);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void scrollDown( final int itemCount){
        chatListView.post(new Runnable() {
            @Override
            public void run() {
                chatListView.smoothScrollToPosition(itemCount);
            }
        });
    }

    @Subscribe
    public void onMessageSent(MiscEvents.PostMessageResponse response){
        final MessagesAdapter adapter = (MessagesAdapter) chatListView.getAdapter();
        if(adapter == null){
            return;
        }
        if(adapter.getItemCount() > 0){
            if(adapter.getItem(0).getId().equals(Message.DUMMY_MESSAGE)){
                adapter.remove(0);
            }
        }
        MixpanelAPI mixpanel = MixpanelAPI.getInstance(getActivity(), getString(R.string.mpId));
        BasicNameValuePair messagePair = null;

        EditText editText = response.getEditTextWeakReference().get();
        Message message = response.getMessage();
        message.setIsUser(true);
        if (message.getContentType() == (Message.MESSAGE_TYPE_TEXT)) {
            if(adapter.getItemCount() > 0){
                message.compareMessage(adapter.getItem(adapter.getItemCount() - 1));
            }
            messagePair = new BasicNameValuePair("Type Of Message", "text");

        } else if (message.getContentType() == (Message.MESSAGE_TYPE_IMAGE)) {
            Message jpeg = response.getResponse().body();
            message.setMessage(jpeg.getMessage());
            messagePair = new BasicNameValuePair("Type Of Message", "photo");
        } else{
            messagePair = new BasicNameValuePair("Type Of Message", "location");
        }
        //adapter.getItem(adapter.getCount()-1).compareMessage(message);
        adapter.add(message);
        if (editText != null) {
            editText.setText("");
        }
        scrollDown(adapter.getItemCount());
        int messageSent = 0;
        int messageReceived = 0;
        try{
            for(Message m :  adapter.getList()){
                if(m.isUser()){
                    messageSent ++;
                    if(messageSent == 1){
                        mixpanel.getPeople().set("Avg Length Of First Message", adapter.getItem(0).getMessage().length());
                    }
                }else{
                    messageReceived ++;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        try{
            JSONObject object = ArmutUtils.getBasicJson(messagePair, new BasicNameValuePair("Number Of Messages Sent", Integer.toString(messageSent)),
                    new BasicNameValuePair("Number Of Messages Received", Integer.toString(messageReceived)) );
            mixpanel.track("Sent A Message", object);
            AdjustEvent event = new AdjustEvent("rten8o"); //Sent Message
            Adjust.trackEvent(event);
        }catch(Exception e){
            e.printStackTrace();
        }

    }

    public interface FullScrollCallback{
        void scrollToBottom(boolean fullScroll);
    }
}
