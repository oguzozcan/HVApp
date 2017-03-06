package com.armut.armuthv.busevents;

/**
 * Created by oguzemreozcan on 05/10/16.
 */

public class LiveAuthRequest {
    private final String userName;
    private final String password;
    private final String grantType;
    private final String clientId;

    public LiveAuthRequest(final String userName, final String password, final String grantType, final String clientId) {
        this.userName = userName;
        this.password = password;
        this.grantType = grantType;
        this.clientId = clientId;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public String getGrantType() {
        return grantType;
    }

    public String getClientId() {
        return clientId;
    }

}
