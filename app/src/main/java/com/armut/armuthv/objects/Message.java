package com.armut.armuthv.objects;

import android.util.Log;

import com.armut.armuthv.utils.TimeUtils;
import com.google.gson.annotations.SerializedName;

import org.joda.time.DateTime;

/**
 * Created by oguzemreozcan on 25/07/16.
 */
public class Message {

    @SerializedName("message_text")
    private String message;
    @SerializedName("message_id")
    private String id;
    @SerializedName("user_id")
    private String userId;
    @SerializedName("job_quotes_id")
    private int jobQuotesId;
    private User user;
    @SerializedName("create_date")
    private String time;
    @SerializedName("is_read")
    private boolean isRead;
    @SerializedName("message_content_type_id")
    private int contentType;

    private boolean isUser;

    private boolean isMergedMessage;

    private final static long MERGE_MESSAGE_TIME = 900000; //Meaning 15 min
    public final static int MESSAGE_TYPE_TEXT = 0;
    public final static int MESSAGE_TYPE_LOCATION = 2;
    public final static int MESSAGE_TYPE_IMAGE = 1;
    public final static String DUMMY_MESSAGE = "DUMMY_MESSAGE";

    //public int layout;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void addToMessage(String newMessage){
        if(message != null){
            message = message +" \n" + newMessage;
            //Log.d("MESSAGE", "MERGED MESSAGE: " + message);
        }else{
            this.message = newMessage;
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getJobQuotesId() {
        return jobQuotesId;
    }

    public void setJobQuotesId(int jobQuotesId) {
        this.jobQuotesId = jobQuotesId;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setIsRead(boolean isRead) {
        this.isRead = isRead;
    }

    public boolean isUser() {
        return isUser;
    }

    public void setIsUser(boolean isUser) {
        this.isUser = isUser;
        //layout = isUser? R.layout.message_user_row_layout : R.layout.message_provider_row_layout;
    }

    public boolean isMergedMessage() {
        return isMergedMessage;
    }

    private void setIsMergedMessage(boolean isMergedMessage) {
        this.isMergedMessage = isMergedMessage;
    }

    public int getContentType() {
        return contentType;
    }

    public void setContentType(int contentType) {
        this.contentType = contentType;
    }

    public void compareMessage(Message message){
        if(message == null){
            return;
        }
        Log.d("MessagesAdapter", "MESSAGE COMBINE " + getMessage() + " -written by:  " +isUser + " - Message 2: " + message.getMessage()+ " -written by:  " +isUser);
        if(message.isUser != isUser || (message.getContentType() != (MESSAGE_TYPE_TEXT) || contentType != MESSAGE_TYPE_TEXT)){
            return;
        }
        try{
            String time1 = message.getTime();
            DateTime dt0 = TimeUtils.dfISOMS.parseDateTime(time);
            DateTime dt1 = TimeUtils.dfISOMS.parseDateTime(time1);
            long diffInMillis = dt0.getMillis() - dt1.getMillis();
            //Log.d("COMPARE MESSAGE: ", "DIFFF IN  MILLIS " + diffInMillis + "-calculate dt1: " + dt1.getMillis() +" - dt0: " + dt0.getMillis());
            if(diffInMillis <= MERGE_MESSAGE_TIME){
                setIsMergedMessage(true);
                //return;
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
