package com.armut.armuthv.busevents;

/**
 * Created by oguzemreozcan on 17/05/16.
 */
public class EventRequestParent {

    private final String token;
    private final String clientInfo;

    public EventRequestParent(String token, String clientInfo) {
        this.token = token;
        this.clientInfo = clientInfo;
    }

    public String getToken() {
        return token;
    }

    public String getClientInfo() {
        return clientInfo;
    }
}
