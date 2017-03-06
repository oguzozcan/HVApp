package com.armut.armuthv.busevents;

/**
 * Created by oguzemreozcan on 25/05/16.
 */
public class DealsRequest extends EventRequestParent {

    final String userId;

    public DealsRequest(String token, String clientInfo, String userId) {
        super(token, clientInfo);
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }
}
